var api = (() => {
  var privateUserAPI = rootAPI.host + '/api/user/'
  var privateMessageAPI = rootAPI.host + '/api/message/'
  var privateGroupAPI = rootAPI.host + '/api/group/'
  return {
    login: privateUserAPI + 'login',
    loginStatus: privateUserAPI + 'status',
    logout: privateUserAPI + 'logout',
    onlineCount: privateUserAPI + 'onlineCount',
    isAdmin: privateUserAPI + 'isAdmin',
    getUserInfo: privateUserAPI + 'get/id/',
    getUnreadMessage: privateMessageAPI + 'get/unreadTo',
    getHistoryMessage: privateMessageAPI + 'get/history',
    readMessage: privateMessageAPI + 'read',
    getResource: privateUserAPI + 'get/resource/',
    getAvatarResource: privateUserAPI + 'get/resource/Avatar/',
    getFriendList: privateUserAPI + 'get/list/friend',
    getGroupList: privateUserAPI + 'get/list/group',
    searchUser: privateUserAPI + 'search',
    addFriend: privateMessageAPI + 'friend/add',
    deleteFriend: privateMessageAPI + 'friend/delete',
    acceptAddFriend: privateMessageAPI + 'friend/accept',
    refuseAddFriend: privateMessageAPI + 'friend/refuse',
    ignoreAddFriend: privateMessageAPI + 'friend/ignore',
    update: privateUserAPI + 'update',
    updateAvatar: privateUserAPI + 'update/avatar',
    updateGroup: privateGroupAPI + 'update',
    createGroup: privateGroupAPI + 'create'
  }
})()
