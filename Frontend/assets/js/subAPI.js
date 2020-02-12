var api = (() => {
  var privateUserAPI = rootAPI.host + '/api/user/'
  return {
    login: privateUserAPI + 'login',
    loginStatus: privateUserAPI + 'relogin',
    logout: privateUserAPI + 'logout',
    onlineCount: privateUserAPI + 'onlineCount',
    isAdmin: privateUserAPI + 'isAdmin',
    getUserInfo: privateUserAPI + 'get/id/',
    getMessage: privateUserAPI + 'get/message/',
    getResource: privateUserAPI + 'get/resource/',
    getAvatarResource: privateUserAPI + 'get/resource/Avatar/',
    getFriendList: privateUserAPI + 'get/list/friend',
    getGroupList: privateUserAPI + 'get/list/group',
    searchUser: privateUserAPI + 'search',
    addFriend: privateUserAPI + 'friend/add',
    deleteFriend: privateUserAPI + 'friend/delete',
    acceptAddFriend: privateUserAPI + 'friend/accept',
    refuseAddFriend: privateUserAPI + 'friend/refuse',
    ignoreAddFriend: privateUserAPI + 'friend/ignore',
    update: privateUserAPI + 'update'
  }
})()
