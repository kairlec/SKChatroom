var $ = layui.$

var ui = {
  addGroup: function (group) {
    $('#group').append(group)
  }
}

var group = {
  pExpand: function (jqDOM) {
    if (!jqDOM.hasClass('layui-show')) {
      jqDOM.addClass('layui-show')
      var icon = jqDOM.prev().children('i')
      console.log(icon)
      if (icon.hasClass('layui-icon-right')) {
        icon.removeClass('layui-icon-right')
        icon.addClass('layui-icon-down')
      }
    }
  },
  pUexpand: function (jqDOM) {
    if (jqDOM.hasClass('layui-show')) {
      jqDOM.removeClass('layui-show')
      var icon = jqDOM.prev().children('i')
      console.log(icon)
      if (icon.hasClass('layui-icon-down')) {
        icon.removeClass('layui-icon-down')
        icon.addClass('layui-icon-right')
      }
    }
  },
  pIsExpanded: function (jqDOM) {
    return jqDOM.hasClass('layui-show')
  },
  pToggleExpanded: function (jqDOM) {
    if (this.pIsExpanded(jqDOM)) {
      this.pUexpand(jqDOM)
    } else {
      this.pExpand(jqDOM)
    }
  },
  groupListData: new Map(),
  addOrRefresh: function (data) {
    this.groupListData.set(data.groupID, data)
  },
  newGroup: function (group) {
    return (
      $('<div/>').attr({ class: 'layui-colla-item' }).html(
        $('<h2/>').attr({ class: 'layui-colla-title' }).html(
          $('<i/>').attr({ class: 'layui-icon layui-icon-right' })).append(group.groupName)).append(
        $('<div/>').attr({ class: 'layui-colla-content group', id: 'g' + group.groupID }))
    )
  },
  addFriendInGroup: function (friend, groupID) {
    $('#g' + groupID).append(friend)
  },
  isExpanded: function (groupID) {
    return this.pIsExpanded($('#g' + groupID))
  },
  expand: function (groupID) {
    this.pExpand($('#g' + groupID))
  },
  unexpand: function (groupID) {
    this.pUexpand($('#g' + groupID))
  },
  toggleExpanded: function (groupID) {
    this.pToggleExpanded($('#g' + groupID))
  },
  unexpandAll: function () {
    $('.group').each(function () {
      group.pUexpand($(this))
    })
  },
  expandAll: function () {
    $('.group').each(function () {
      group.pExpand($(this))
    })
  },
  toggleExpandedAll: function () {
    $('.group').each(function () {
      group.pToggleExpanded($(this))
    })
  }
}

var user = {
  userListData: new Map(),
  addOrRefresh: function (data) {
    this.userListData.set(data.userID, data)
  },
  // 获取头像
  getAvatar: function (id, jqImg) {
    var user = this.userListData.get(data.userID)
    if (user.avatarCache) {
      jqImg.attr('src', user.avatarData)
    }
    $.ajax({
      type: 'POST',
      url: api + '/get/resource/Avatar/' + id,
      xhrFields: {
        withCredentials: true
      },
      xhr: function () {
        var xhr = new XMLHttpRequest()
        xhr.responseType = 'blob'
        return xhr
      },
      success: function (data) {
        user.avatarCache = true
        user.avatarData = window.URL.createObjectURL(data)
        jqImg.attr('src', user.avatarData)
      },
      error: ajaxError
    })
  },
  newFriendDom: function (user) {
    return (
      $('<div/>').attr({ class: 'layui-row box-my-list user', id: 'f' + user.userID }).html(
        $('<div/>').attr({ class: 'layui-col-xs2' }).html(
          $('<div/>').attr({ class: 'layui-row' }).html(
            $('<div/>').attr({ class: 'layui-col-xs12' }).html(
              $('<img/>').attr({ class: 'box-my-pic', /* 头像链接 */ src: 'assets/images/1.png' }))))).append(
        $('<div/>').attr({ class: 'layui-col-xs10' }).html(
          $('<div/>').attr({ class: 'layui-row' }).html(
            $('<div/>').attr({ class: 'layui-col-xs12 box-my-name' }).html(user.nickName)).append(
            $('<div/>').attr({ class: 'layui-col-xs12 box-my-signal box-line-1' }).html(user.signture))))
    )
  },
  searchUser: function (searchData, fnSuccess) {
    $.ajax({
      type: 'POST',
      url: api.searchUser,
      data: { data: searchData },
      dataType: 'json',
      xhrFields: {
        withCredentials: true
      },
      success: function (data) {
        warpResponseData(data, fnSuccess)
      },
      error: ajaxError
    })
  },
  addFriend: function (friendID, groupID, message, fnSuccess) {
    $.ajax({
      type: 'POST',
      url: api.addFriend,
      data: { targetID: friendID, content: { groupID: groupID, msg: message } },
      dataType: 'json',
      xhrFields: {
        withCredentials: true
      },
      success: function (data) { warpResponseData(data, fnSuccess) },
      error: ajaxError
    })
  },
  deleteFriend: function (friendID, fnSuccess) {
    $.ajax({
      type: 'POST',
      url: api.deleteFriend,
      data: { targetID: friendID },
      dataType: 'json',
      xhrFields: {
        withCredentials: true
      },
      success: function (data) { warpResponseData(data, fnSuccess) },
      error: ajaxError
    })
  },
  acceptAddFriend: function (messageID, groupID, friendID, fnSuccess) {
    $.ajax({
      type: 'POST',
      url: api.acceptAddFriend,
      data: { targetID: friendID, groupID: groupID, messageID: messageID },
      dataType: 'json',
      xhrFields: {
        withCredentials: true
      },
      success: function (data) { warpResponseData(data, fnSuccess) },
      error: ajaxError
    })
  },
  refuseAddFriend: function (messageID, friendID, fnSuccess) {
    $.ajax({
      type: 'POST',
      url: api.refuseAddFriend,
      data: { targetID: friendID, messageID: messageID },
      dataType: 'json',
      xhrFields: {
        withCredentials: true
      },
      success: function (data) { warpResponseData(data, fnSuccess) },
      error: ajaxError
    })
  },
  ignoreAddFriend: function (messageID, friendID, fnSuccess) {
    $.ajax({
      type: 'POST',
      url: api.ignoreAddFriend,
      data: { targetID: friendID, messageID: messageID },
      dataType: 'json',
      xhrFields: {
        withCredentials: true
      },
      success: function (data) { warpResponseData(data, fnSuccess) },
      error: ajaxError
    })
  },
  getUnreadMessage: function (fnSuccess) {
    $.ajax({
      type: 'POST',
      url: api.getMessage + 'unreadTo',
      dataType: 'json',
      xhrFields: {
        withCredentials: true
      },
      success: function (data) { warpResponseData(data, fnSuccess) },
      error: ajaxError
    })
  }
}

function outArr (data) {
  for (var i = 0; i < data.length; i++) {
    console.log(data[i])
  }
}

$(() => {
  document.oncontextmenu = () => {
    parent.closeMenu()
    return false
  }

  $('#group').on('click', '.layui-colla-title', function () {
    var theContent = $(this).next('.group')
    group.toggleExpanded(theContent.attr('id').substr(1))
  })

  $('.box-btn-list-li').on('click', function () {
    var index = $(this).index()
    $('.box-btn-content').removeClass('layui-show').eq(index).addClass('layui-show')
  })

  // 在父页面打开聊天窗口
  $('#group').on('click', '.box-my-list', function () {
    parent.createWindow(user.userListData.get($(this).attr('id').substr(1)))
  })

  $('#group').on('contextmenu', '.layui-colla-item', function (event) {
    parent.menuIndex = parent.mouseRightMenu.open(getGroupData($(this).children('.group').attr('id').substr(1)), { offset: parent.getBoxMouseXY(event.pageX, event.pageY) }, function (data) {
      switch (data.type) {
        case 1:// 展开/收缩
          break
        case 2:// 重命名
          break
        case 3:// 删除分组
          break
      }
    })
    return false
  })

  $('#group').on('contextmenu', '.user', function (event) {
    parent.menuIndex = parent.mouseRightMenu.open(getUserData($(this).attr('id').substr(1)), { offset: parent.getBoxMouseXY(event.pageX, event.pageY) }, function (data) {
      switch (data.type) {
        case 1:// 打开会话
          parent.createWindow(user.userListData.get(data.data))
          break
        case 2:// 删除好友
          break
        case 3:
          break
      }
    })
    return false
  })

  $('#self-signature').text(parent.selfData.signture || '')

  $.ajax({
    type: 'POST',
    url: api.getGroupList,
    dataType: 'json',
    xhrFields: {
      withCredentials: true
    },
    success: function (data) {
      $.each(data.data, function (index, item) {
        group.addOrRefresh(item)
        ui.addGroup(group.newGroup(item))
      })
    },
    error: ajaxError
  })
})

function ajaxError (jqXHR, textStatus, errorThrown) {
  if (errorThrown === null || errorThrown.length === 0) {
    layer.msg('错误:' + jqXHR.statusText)
  } else {
    layer.msg('错误:' + errorThrown)
  }
}

function responseError (msg) {

}

var warpResponseData = (data, callback) => {
  if (data.code !== 0) {
    responseError(data)
  }
  !callback || callback(data.data)
}
