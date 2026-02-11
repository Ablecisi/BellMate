/* BellMate shared runtime
 * - storage keys and defaults
 * - auth/session/account helpers
 * - settings (theme/language/notification)
 * - cross-iframe messaging
 */
(function () {
  const DAY_MS = 24 * 60 * 60 * 1000;

  const KEYS = {
    token: "bellmate_token",
    guestMode: "bellmate_guest_mode",
    alarms: "bellmate_alarms",
    plans: "bellmate_plans",
    pauseDate: "bellmate_today_pause_date",
    pauseUndoUntil: "bellmate_pause_undo_until",
    cloudPrefix: "bellmate_cloud_",
    syncLogs: "bellmate_sync_logs",
    deviceId: "bellmate_device_id",
    accounts: "bellmate_accounts",
    sessions: "bellmate_account_sessions",
    prefs: "bellmate_preferences",
    profile: "bellmate_profile",
  };

  function readJSON(key, fallback) {
    try {
      const raw = localStorage.getItem(key);
      return raw ? JSON.parse(raw) : fallback;
    } catch (err) {
      return fallback;
    }
  }

  function writeJSON(key, value) {
    localStorage.setItem(key, JSON.stringify(value));
  }

  function todayStr() {
    return new Date().toISOString().slice(0, 10);
  }

  function ensureDevice() {
    if (!localStorage.getItem(KEYS.deviceId)) {
      localStorage.setItem(KEYS.deviceId, `device-${Math.random().toString(36).slice(2, 8)}`);
    }
  }

  function tokenToAccount(token) {
    if (!token) return null;
    const match = token.match(/^token::(.+?)::/);
    return match ? match[1] : null;
  }

  function getAccount() {
    return tokenToAccount(localStorage.getItem(KEYS.token));
  }

  function getCloudKey(account) {
    return `${KEYS.cloudPrefix}${account || "guest"}`;
  }

  function getPrefs() {
    return {
      notifications: true,
      language: "system",
      forceDark: false,
      ...(readJSON(KEYS.prefs, {}) || {}),
    };
  }

  function setPrefs(partial) {
    writeJSON(KEYS.prefs, { ...getPrefs(), ...partial });
  }

  function getEffectiveTheme() {
    const prefs = getPrefs();
    if (prefs.forceDark) return "dark";
    const prefersDark = window.matchMedia && window.matchMedia("(prefers-color-scheme: dark)").matches;
    return prefersDark ? "dark" : "light";
  }

  function getEffectiveLanguage() {
    const prefs = getPrefs();
    if (prefs.language === "zh-CN" || prefs.language === "en-US") return prefs.language;
    const navLang = (navigator.language || "zh-CN").toLowerCase();
    return navLang.startsWith("en") ? "en-US" : "zh-CN";
  }

  function applyPreferences(rootDoc) {
    const doc = rootDoc || document;
    const theme = getEffectiveTheme();
    doc.documentElement.setAttribute("data-theme", theme);
    doc.documentElement.setAttribute("lang", getEffectiveLanguage());
  }

  function getAccounts() {
    return readJSON(KEYS.accounts, []) || [];
  }

  function setAccounts(list) {
    writeJSON(KEYS.accounts, list);
  }

  function getSessions() {
    return readJSON(KEYS.sessions, {}) || {};
  }

  function setSessions(map) {
    writeJSON(KEYS.sessions, map);
  }

  function pruneSessionsAndAccounts() {
    const now = Date.now();
    const sessions = getSessions();
    Object.keys(sessions).forEach((account) => {
      sessions[account] = (sessions[account] || []).filter((x) => x.expiresAt > now);
      if (!sessions[account].length) delete sessions[account];
    });
    setSessions(sessions);

    const accounts = getAccounts().filter((a) => a.expiresAt > now);
    setAccounts(accounts);
  }

  function getAvailableAccounts() {
    pruneSessionsAndAccounts();
    return getAccounts().sort((a, b) => b.lastLogin - a.lastLogin);
  }

  function login(account) {
    ensureDevice();
    pruneSessionsAndAccounts();
    localStorage.removeItem(KEYS.guestMode);

    const now = Date.now();
    const deviceId = localStorage.getItem(KEYS.deviceId);
    const sessions = getSessions();
    const list = sessions[account] || [];
    const currentExists = list.some((x) => x.deviceId === deviceId);

    if (!currentExists && list.length >= 2) {
      return { ok: false, message: "该账号已在2台设备登录，请先在其他设备退出。" };
    }

    const token = `token::${account}::${now}`;
    const expiresAt = now + 7 * DAY_MS;
    const updated = currentExists
      ? list.map((x) => (x.deviceId === deviceId ? { ...x, lastSeen: now, expiresAt } : x))
      : [...list, { deviceId, lastSeen: now, expiresAt }];
    sessions[account] = updated;
    setSessions(sessions);

    const accounts = getAccounts();
    const idx = accounts.findIndex((x) => x.account === account);
    const nextEntry = { account, token, expiresAt, lastLogin: now };
    if (idx >= 0) accounts[idx] = nextEntry;
    else accounts.push(nextEntry);
    setAccounts(accounts);

    localStorage.setItem(KEYS.token, token);
    return { ok: true, token };
  }

  function switchAccount(account) {
    const candidate = getAvailableAccounts().find((x) => x.account === account);
    if (!candidate) return { ok: false, message: "该账号不可用或已过期。" };
    return login(account);
  }

  function removeAccount(account) {
    if (!account) return { ok: false, message: "invalid account" };
    setAccounts(getAccounts().filter((x) => x.account !== account));
    const sessions = getSessions();
    if (sessions[account]) {
      delete sessions[account];
      setSessions(sessions);
    }
    if (getAccount() === account) localStorage.removeItem(KEYS.token);
    return { ok: true };
  }

  function logoutCurrent() {
    const account = getAccount();
    const deviceId = localStorage.getItem(KEYS.deviceId);
    localStorage.removeItem(KEYS.token);
    localStorage.removeItem(KEYS.guestMode);
    if (!account || !deviceId) return;

    const sessions = getSessions();
    sessions[account] = (sessions[account] || []).filter((x) => x.deviceId !== deviceId);
    if (!sessions[account].length) delete sessions[account];
    setSessions(sessions);
  }

  function enableGuestMode() {
    localStorage.removeItem(KEYS.token);
    localStorage.setItem(KEYS.guestMode, "1");
  }

  function isGuestMode() {
    return localStorage.getItem(KEYS.guestMode) === "1";
  }

  function isAuthed() {
    if (isGuestMode()) return true;
    const token = localStorage.getItem(KEYS.token);
    const account = tokenToAccount(token);
    if (!token || !account) return false;

    const entry = getAvailableAccounts().find((x) => x.account === account);
    if (!entry) {
      localStorage.removeItem(KEYS.token);
      return false;
    }
    return true;
  }

  function ensureSeedData() {
    const alarms = readJSON(KEYS.alarms, null);
    if (!alarms || !alarms.length) {
      writeJSON(KEYS.alarms, [
        {
          id: `a_${Date.now()}_1`,
          time: "08:50",
          label: "项目会议提醒",
          repeatMode: "workday",
          weekdays: [1, 2, 3, 4, 5],
          ringtone: "default",
          enabled: true,
          aiEnabled: true,
          aiStyle: "gentle",
          aiPlanId: "auto",
        },
        {
          id: `a_${Date.now()}_2`,
          time: "07:30",
          label: "晨跑",
          repeatMode: "daily",
          weekdays: [0, 1, 2, 3, 4, 5, 6],
          ringtone: "bird",
          enabled: false,
          aiEnabled: false,
          aiStyle: "energetic",
          aiPlanId: "auto",
        },
      ]);
    }

    const plans = readJSON(KEYS.plans, null);
    if (!plans || !plans.length) {
      const now = new Date();
      const meeting = new Date(now.getTime() + 60 * 60 * 1000);
      writeJSON(KEYS.plans, [
        {
          id: `p_${Date.now()}_1`,
          title: "9点开项目会议",
          datetime: meeting.toISOString().slice(0, 16),
          remark: "准备里程碑汇报",
          priority: "high",
          done: false,
        },
      ]);
    }

    if (!readJSON(KEYS.syncLogs, null)) writeJSON(KEYS.syncLogs, []);
    if (!readJSON(KEYS.profile, null)) {
      writeJSON(KEYS.profile, { nickname: "BellMate 用户", phone: "", bio: "" });
    }

    pruneSessionsAndAccounts();
  }

  function clearAppCache() {
    localStorage.removeItem(KEYS.alarms);
    localStorage.removeItem(KEYS.plans);
    localStorage.removeItem(KEYS.syncLogs);
    localStorage.removeItem(KEYS.pauseDate);
    localStorage.removeItem(KEYS.pauseUndoUntil);
    ensureSeedData();
  }

  function autoRecoverPaused() {
    const pausedDate = localStorage.getItem(KEYS.pauseDate);
    if (!pausedDate || pausedDate === todayStr()) return;
    const alarms = readJSON(KEYS.alarms, []);
    alarms.forEach((a) => {
      if (a.todayPausedDate) delete a.todayPausedDate;
    });
    writeJSON(KEYS.alarms, alarms);
    localStorage.removeItem(KEYS.pauseDate);
    localStorage.removeItem(KEYS.pauseUndoUntil);
  }

  function postToParent(type, payload) {
    if (window.parent && window.parent !== window) {
      window.parent.postMessage({ source: "bellmate-child", type, payload }, "*");
    }
  }

  function postToChild(iframe, type, payload) {
    if (!iframe || !iframe.contentWindow) return;
    iframe.contentWindow.postMessage({ source: "bellmate-parent", type, payload }, "*");
  }

  window.BellMate = {
    KEYS,
    readJSON,
    writeJSON,
    todayStr,
    ensureDevice,
    ensureSeedData,
    autoRecoverPaused,
    clearAppCache,
    getAccount,
    getCloudKey,
    getPrefs,
    setPrefs,
    applyPreferences,
    getEffectiveTheme,
    getEffectiveLanguage,
    getAvailableAccounts,
    login,
    switchAccount,
    removeAccount,
    logoutCurrent,
    enableGuestMode,
    isGuestMode,
    postToParent,
    postToChild,
    isAuthed,
  };
})();
