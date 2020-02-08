var api = (() => {
  var publicUserAPI = rootAPI.host + '/api/public/user/'
  var privateUserAPI = rootAPI.host + '/api/user/'
  var registerAPI = rootAPI.host + '/api/public/register/'
  return {
    pk: publicUserAPI + 'pk',
    login: privateUserAPI + 'login',
    loginStatus: privateUserAPI + 'relogin',
    logout: privateUserAPI + 'logout',
    getCaptcha: publicUserAPI + 'captcha',
    getTestCaptcha: publicUserAPI + 'test/captcha',
    refreshCaptcha: publicUserAPI + 'newcaptcha',
    reg: registerAPI,
    activate: registerAPI + 'activate'
  }
})()
