const $ = layui.$
const waitMethod = {
  id: 0,
  start: function (msg) {
    this.id = parent.layer.msg(msg, {
      icon: 16,
      shade: 0.3,
      time: 0
    })
  },
  end: function () {
    parent.layer.close(this.id)
  }
}

const groupMethod = {
  name: function (groupID, name) {
    if (!name) {
      return $('#g' + groupID + '-name').text()
    } else {
      $('#g' + groupID + '-name').text(name)
      $('#g' + groupID).data('group-name', name)
    }
  },
  groupListData: new Map(),
  addOrRefresh: function (data) {
    this.groupListData.set(data.groupID, data)
  },
  // <i class="layui-bg-red layui-badge-dot msgdot" id="MessageGroupDot"></i>
  newGroup: function (groupObject) {
    console.log(groupObject)
    return (
      $('<div/>').attr({ class: 'layui-colla-item' }).html(
        $('<h2/>').attr({ class: 'layui-colla-title' }).html(
          $('<i/>').attr({ class: 'layui-icon layui-icon-right' })).append(
          $('<span/>').attr({ id: 'g' + groupObject.groupID + '-name' }).text(groupObject.groupName)).append(
          $('<i/>').attr({ class: 'layui-bg-red layui-badge-dot GroupMsgdot', style: 'display: none;' })
        )).append(
        $('<div/>').attr({ class: 'layui-colla-content group', id: 'g' + groupObject.groupID }).data('group-name', groupObject.groupName).data('group-order', groupObject.groupOrder))
    )
  },
  addFriendInGroup: function (friendDOM, groupID) {
    console.log($('#g' + groupID))
    $('#g' + groupID).append(friendDOM)
  }

}
var uiMethod = {
  newMsgContent: {
    hasMessage: function (userID) {
      return $('m' + userID).length !== 0
    },
    addOrUpdateMessage: function () {
    },
    removeMessage: function (userID) {
      $('m' + userID).remove()
    },
    newMessageDOM: function (user, message) {
      const DOM = (
        $('<div/>').attr({ class: 'layui-row box-my-list user', id: 'm' + user.userID }).html(
          $('<div/>').attr({ class: 'layui-col-xs2' }).html(
            $('<div/>').attr({ class: 'layui-row' }).html(
              $('<div/>').attr({ class: 'layui-col-xs12' }).html(
                $('<img/>').attr({ class: 'box-my-pic', src: user.avatar }))))).append(
          $('<div/>').attr({ class: 'layui-col-xs10' }).html(
            $('<div/>').attr({ class: 'layui-row' }).html(
              $('<div/>').attr({ class: 'layui-col-xs12 box-my-name' }).html(
                $('<span>').attr({ class: 'userNickname' }).text(user.nickname)).append(
                $('<i>').attr({ class: 'layui-bg-red layui-badge-dot UserMsgdot' }))).append(
              $('<div/>').attr({ class: 'layui-col-xs12 box-my-msg box-line-1' }).text(message))))
      )
      return DOM
    },
    addMessageDOM: function (DOM) {
      $('#newMsg').append(DOM)
    }

  },
  GroupPand: {
    pExpand: function (jqDOM) {
      if (!jqDOM.hasClass('layui-show')) {
        jqDOM.addClass('layui-show')
        var icon = jqDOM.prev().children('.layui-icon-right')
        icon.removeClass('layui-icon-right')
        icon.addClass('layui-icon-down')
      }
    },
    pUexpand: function (jqDOM) {
      if (jqDOM.hasClass('layui-show')) {
        jqDOM.removeClass('layui-show')
        var icon = jqDOM.prev().children('.layui-icon-down')
        icon.removeClass('layui-icon-down')
        icon.addClass('layui-icon-right')
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
        groupMethod.pUexpand($(this))
      })
    },
    expandAll: function () {
      $('.group').each(function () {
        groupMethod.pExpand($(this))
      })
    },
    toggleExpandedAll: function () {
      $('.group').each(function () {
        groupMethod.pToggleExpanded($(this))
      })
    }
  },
  addGroup: function (groupDOM) {
    const sleep = (ms) => {
      return new Promise(resolve => setTimeout(resolve, ms))
    }
    while ($('#group').length === 0) {
      sleep(10)
    }
    $('#group').append(groupDOM)
  },
  userStatus: {
    offline: function () {
      var status = parent.$('#self-status')
      if (status.hasClass('layui-bg-green')) {
        status.removeClass('layui-bg-green')
        status.addClass('layui-bg-red')
      }
    },
    online: function () {
      var status = parent.$('#self-status')
      if (status.hasClass('layui-bg-red')) {
        status.removeClass('layui-bg-red')
        status.addClass('layui-bg-green')
      }
    }
  },
  MessageDot: {
    showMain: function () {
      $('#MessageGroupDot').show()
    },
    hideMain: function () {
      $('#MessageGroupDot').hide()
    },
    showGroup: function (groupID) {
      $('#g' + groupID).prev().children('.GroupMsgdot').show()
    },
    hideGroup: function (groupID) {
      $('#g' + groupID).prev().children('.GroupMsgdot').hide()
    },
    showUser: function (userID) {
      console.log(userID)
      var userDom = $('#f' + userID)
      console.log(userDom)
      userDom.find('.UserMsgdot').css('display', 'inline-block')
      $('#msguser-' + userID).find('.UserMsgdot').css('display', 'inline-block')
      console.log(userDom.find('.UserMsgdot'))
      this.showGroup(userDom.parent('.group').attr('id').substr(1))
    },
    hideUser: function (userID) {
      var hasMsg = false
      $('#f' + userID).find('.UserMsgdot').hide()
      $('#msguser-' + userID).find('.UserMsgdot').hide()
      var groupDOM = $('#f' + userID).parent('.group')
      groupDOM.find('.user').each(function (index, item) {
        console.log(index)
        console.log(item)
        if ($(item).find('.UserMsgdot').css('display') !== 'none') {
          hasMsg = true
          return false
        }
      })
      if (!hasMsg) {
        this.hideGroup(groupDOM.attr('id').substr(1))
      }
    }

  }
}

const messageMethod = {
  messageListData: new Map(),
  messageFromListData: new Set(),
  newFriendMsgDom: function (user, message) {
    var DOM = null
    switch (message.action) {
      case 'ADD_FRIEND_REQUEST':
        var messageContent = JSON.parse(message.content)
        if (messageContent.type === 'RESPONSE') {
          return ''
        }
        if (this.messageFromListData.has(user.userID)) {
          return ''
        } else {
          this.messageFromListData.add(user.userID)
        }
        console.log(messageContent)
        DOM = (
          $('<div/>').attr({ class: 'layui-row box-my-list user msgdom' }).data('msg-id', message.messageID).data('target-id', user.userID).html(
            $('<div/>').attr({ class: 'layui-col-xs2' }).html(
              $('<div/>').attr({ class: 'layui-row' }).html(
                $('<div/>').attr({ class: 'layui-col-xs12' }).html(
                  $('<img/>').attr({ class: 'box-my-pic', src: user.avatar }))))).append(
            $('<div/>').attr({ class: 'layui-col-xs8' }).html(
              $('<div/>').attr({ class: 'layui-row' }).html(
                $('<div/>').attr({ class: 'layui-col-xs12 box-my-name' }).html(
                  $('<span>').attr({ class: 'userNickname' }).text(user.nickname)).append(
                  $('<i>').attr({ class: 'layui-bg-red layui-badge-dot UserMsgdot' }))).append(
                $('<div/>').attr({ class: 'layui-col-xs12 box-my-msg box-line-1', title: messageContent.message }).text('[添加好友] ' + messageContent.message)))).append(
            $('<div/>').attr({ class: 'layui-col-xs2' }).html(
              (() => {
                if (messageContent.type === 'REQUEST') {
                  switch (messageContent.result) {
                    case 'IGNORE':
                      return $('<button/>').attr({ class: 'layui-btn requestBtn-class3 layui-btn-disabled layui-bg-gray' }).text('已忽略')
                    case 'ACCRPT':
                      return $('<button/>').attr({ class: 'layui-btn requestBtn-class3 layui-btn-disabled' }).text('已接受')
                    case 'REFUSE':
                      return $('<button/>').attr({ class: 'layui-btn requestBtn-class3 layui-btn-disabled layui-bg-red' }).text('已拒绝')
                    case 'WAIT':
                    default:
                      return $('<button/>').attr({ class: 'layui-btn requestBtn-class requestBtn' }).text('接受').data('msg-id', message.messageID).data('target-id', user.userID)
                  }
                }
              })()
            )
          )
        )
        break
      case 'GROUP_CHAT_MESSAGE':
        break
      case 'PRIVATE_CHAT_MESSAGE':
        if (message.fromID === user.userID && !message.isRead) {
          uiMethod.MessageDot.showUser(user.userID)
        }
        if (this.messageFromListData.has(user.userID)) {
          var targetDom = $('#msgdom-' + user.userID)
          var time = targetDom.data('time')
          if (message.time > time) {
            targetDom.text(message.content)
          }
          return ''
        } else {
          this.messageFromListData.add(user.userID)
        }
        DOM = (
          $('<div/>').attr({ class: 'layui-row box-my-list user msgdom', id: 'msguser-' + user.userID }).data('msg-id', message.messageID).data('target-id', user.userID).data('type', 'OPEN').html(
            $('<div/>').attr({ class: 'layui-col-xs2' }).html(
              $('<div/>').attr({ class: 'layui-row' }).html(
                $('<div/>').attr({ class: 'layui-col-xs12' }).html(
                  $('<img/>').attr({ class: 'box-my-pic', src: user.avatar }))))).append(
            $('<div/>').attr({ class: 'layui-col-xs10' }).html(
              $('<div/>').attr({ class: 'layui-row' }).html(
                $('<div/>').attr({ class: 'layui-col-xs12 box-my-name' }).html(
                  $('<span>').attr({ class: 'userNickname' }).text(user.nickname)).append(
                  $('<i>').attr({ class: 'layui-bg-red layui-badge-dot UserMsgdot' }))).append(
                $('<div/>').attr({ class: 'layui-col-xs12 box-my-msg box-line-1', id: 'msgdom-' + user.userID, title: message.content }).data('time', message.time).text(message.content))))
        )
        break
      case 'DELETE_FRIEND_REQUEST':
        break
      case 'SYSTEM_NOTIFICATION':
        break
      default:
        break
    }
    return DOM
  },
  addOrRefreshMessage: function (data) {
    this.messageListData.set(data.messageID, data)
  }
}

const userMethod = {
  userListData: new Map(),
  addOrRefreshUser: function (data) {
    if (data.avatar === null || data.avatar === '@DEFAULT?') {
      data.avatar = 'assets/images/1.png'
    }
    this.userListData.set(data.userID, data)
  },
  checkUserData: async function (userID, callback) {
    if (this.userListData.has(userID)) {
      callback(this.userListData.get(userID))
    } else {
      var data
      try {
        data = this.getUser(userID)
      } catch (ajaxErrorStatus) {
        ajaxError(ajaxErrorStatus)
        return null
      }
      warpResponseData(data, function (data) {
        userMethod.addOrRefreshUser(data)
        callback(data)
      })
    }
  },
  isFriend: function (userID) {
    // return true
    return this.userListData.has(userID)
  },
  newFriendDom: function (user) {
    const DOM = (
      $('<div/>').attr({ class: 'layui-row box-my-list user', id: 'f' + user.userID }).html(
        $('<div/>').attr({ class: 'layui-col-xs2' }).html(
          $('<div/>').attr({ class: 'layui-row' }).html(
            $('<div/>').attr({ class: 'layui-col-xs12' }).html(
              $('<img/>').attr({ class: 'box-my-pic', src: user.avatar }))))).append(
        $('<div/>').attr({ class: 'layui-col-xs10' }).html(
          $('<div/>').attr({ class: 'layui-row' }).html(
            $('<div/>').attr({ class: 'layui-col-xs12 box-my-name' }).html(
              $('<span>').attr({ class: 'userNickname' }).text(user.nickname)).append(
              $('<i>').attr({ class: 'layui-bg-red layui-badge-dot UserMsgdot' }))).append(
            $('<div/>').attr({ class: 'layui-col-xs12 box-my-signal box-line-1' }).text(user.signature))))
    )
    return DOM
  },
  newAddFriendDom: function (user) {
    const DOM = (
      $('<div/>').attr({ class: 'layui-row box-my-list user', id: 'a' + user.userID }).html(
        $('<div/>').attr({ class: 'layui-col-xs1' }).html(
          $('<div/>').attr({ class: 'layui-row' }).html(
            $('<div/>').attr({ class: 'layui-col-xs12' }).html(
              $('<img/>').attr({ class: 'box-my-pic', src: user.avatar }))))).append(
        $('<div/>').attr({ class: 'layui-col-xs10' }).html(
          $('<div/>').attr({ class: 'layui-row' }).html(
            $('<div/>').attr({ class: 'layui-col-xs12 box-my-name' }).html(
              $('<span>').attr({ class: 'userNickname' }).text(user.nickname))).append(
            $('<div/>').attr({ class: 'layui-col-xs12 box-my-signal box-line-1' }).text(user.signature)))).append(
        $('<div/>').attr({ class: 'layui-col-xs1' }).html(
          $('<button/>').attr({ class: `layui-btn searchBtn ${this.isFriend(user.userID) ? 'chatBtn' : 'addFriendBtn'}`, type: 'button' }).text(this.isFriend(user.userID) ? '会话' : '添加'))
      )
    )
    return DOM
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
      data: { targetID: friendID, content: JSON.stringify({ groupID: groupID, msg: message }) },
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
      url: api.getUnreadMessage,
      dataType: 'json',
      xhrFields: {
        withCredentials: true
      },
      success: function (data) { warpResponseData(data, fnSuccess) },
      error: ajaxError
    })
  },
  getHistoryMessage: function (fnSuccess) {
    $.ajax({
      type: 'POST',
      url: api.getHistoryMessage,
      dataType: 'json',
      xhrFields: {
        withCredentials: true
      },
      success: function (data) { warpResponseData(data, fnSuccess) },
      error: ajaxError
    })
  },
  getUser: function (userID) {
    if (this.userListData.has(userID)) {
      return { code: 0, msg: 'OK', data: this.userListData.get(userID) }
    }
    var userData = null
    $.ajax({
      type: 'POST',
      url: api.getUserInfo + userID,
      dataType: 'json',
      async: false,
      success: function (data) {
        userData = data
      },
      error: ajaxError
    })
    return userData
  }
}

$(() => {
  $.ajaxSetup({
    xhrFields: {
      withCredentials: true
    }
  })

  // 屏蔽默认的右键菜单,并设为捕获时事件
  document.addEventListener('contextmenu', function (event) {
    parent.closeMenu()
    event.returnValue = false
  }, true)

  $('#group').on('click', '.layui-colla-title', function () {
    var theContent = $(this).next('.group')
    uiMethod.GroupPand.toggleExpanded(theContent.attr('id').substr(1))
  })

  $('.box-btn-list-li').on('click', function () {
    var index = $(this).index()
    $('.box-btn-content').removeClass('layui-show').eq(index).addClass('layui-show')
  })

  // 在父页面打开聊天窗口
  $('#group').on('click', '.box-my-list', function () {
    openWindow($(this).attr('id').substr(1))
  })

  $('#group').on('contextmenu', '.layui-colla-item', function (event) {
    parent.menuIndex = parent.mouseRightMenu.open(getGroupData((() => { var group = $(this).children('.group'); return { id: group.attr('id').substr(1), order: group.data('group-order'), name: group.data('group-name') } })()), { offset: parent.getBoxMouseXY(event.pageX, event.pageY) }, function (data) {
      switch (data.type) {
        case 1:// 展开/收缩
          uiMethod.GroupPand.toggleExpanded(data.data.id)
          break
        case 2:// 重命名
          parent.layer.prompt({
            formType: 0,
            title: '请输入分组名',
            value: data.data.name,
            area: ['800px', '350px'] // 自定义文本域宽高
          }, function (value, index, elem) {
            console.log(value)
            console.log(data.data)
            parent.layer.close(index)
            waitMethod.start('处理中')
            $.ajax({
              type: 'POST',
              url: api.updateGroup,
              dataType: 'json',
              data: { id: data.data.id, name: value, order: data.data.order },
              success: function (responseData) {
                warpResponseData(responseData, function () {
                  waitMethod.end()
                  console.log(data.data.id)
                  console.log(value)
                  groupMethod.name(data.data.id, value)
                })
              },
              error: ajaxError
            })
          })
          break
        case 3:// 删除分组
        // TODO: 删除分组
          parent.layer.msg('未完善')
          // parent.layer.confirm('你确定要删除该分组吗?', { icon: 3, title: '删除提示' }, function (index) {
          //   console.log(data.data)
          //   parent.layer.close(index)
          // })
          break
      }
    })
    return false
  })

  $('#group').on('contextmenu', '.user', function (event) {
    parent.menuIndex = parent.mouseRightMenu.open(getUserData($(this).attr('id').substr(1)), { offset: parent.getBoxMouseXY(event.pageX, event.pageY) }, function (data) {
      switch (data.type) {
        case 1:// 打开会话
          openWindow(data.data)
          break
        case 2:// 删除好友
          parent.layer.confirm('你确定要删除该好友吗?', { icon: 3, title: '删除提示' }, function (index) {
            parent.layer.close(index)
            waitMethod.start('提交中')
            $.ajax({
              url: api.deleteFriend,
              dataType: 'json',
              data: { targetID: data.data },
              success: function (data) {
                warpResponseData(data, function () {
                  parent.ChatBoxMap.closeID(data.data)
                  $('#msguser-' + data.data).remove()
                  $('#f' + data.data).remove()
                  waitMethod.end()
                })
              },
              error: ajaxError
            })
          })
          break
        case 3:// 查看资料
          parent.layer.open({
            type: 1,
            title: '详细资料',
            content: createUserInfomationPanelDOM(data.data),
            area: ['600px', '500px'],
            shadeClose: false,
            resize: true
          })
          break
      }
    })
    return false
  })

  $('.boxBlank').on('contextmenu', function (event) {
    if (!$('#mainMenu').hasClass('layui-show')) {
      return
    }
    parent.menuIndex = parent.mouseRightMenu.open(getBlankData(), { offset: parent.getBoxMouseXY(event.pageX, event.pageY) }, function (data) {
      switch (data.type) {
        case 1:// 刷新列表
        // TODO: 刷新列表
          parent.layer.msg('未完善')
          break
        case 2:// 新建分组
          parent.layer.prompt({
            formType: 0,
            title: '请输入分组名',
            value: '',
            area: ['800px', '350px'] // 自定义文本域宽高
          }, function (value, index, elem) {
            console.log(value)
            console.log(data.data)
            parent.layer.close(index)
            waitMethod.start('处理中')
            $.ajax({
              type: 'POST',
              url: api.createGroup,
              dataType: 'json',
              data: { name: value, order: 0 },
              success: function (responseData) {
                warpResponseData(responseData, function (data) {
                  waitMethod.end()
                  groupMethod.addOrRefresh(data)
                  uiMethod.addGroup(groupMethod.newGroup(data))
                })
              },
              error: ajaxError
            })
          })
          break
        // case 3:
        //   break
      }
    })
  })

  $('.boxBlank').on('click', '.msgdom', function (event) {
    console.log($(this))
    var type = $(this).data('type')
    if (type === 'OPEN') {
      openWindow($(this).data('target-id'))
    } else {
      var messageID = $(this).data('msg-id')
      var targetID = $(this).data('target-id')
      parent.layer.open({
        type: 1,
        id: 'msgdetail',
        title: '详细信息',
        content: createMsgDetailPanelContent(messageID, targetID),
        area: ['600px', '500px'],
        shadeClose: false,
        resize: true,
        success: function (layero, index) {
          var msgdetail = $(layero).children('#msgdetail')
          console.log(msgdetail)
          msgdetail.on('click', '.ignore-btn', function (event) {
            waitMethod.start('提交中')
            $.ajax({
              type: 'POST',
              url: api.ignoreAddFriend,
              data: { messageID: messageID },
              dataType: 'json',
              success: function (data) {
                waitMethod.end()
                warpResponseData(data, (data) => {
                  updateSelfData(data)
                })
              },
              error: ajaxError
            })
          })
          msgdetail.on('click', '.refuse-btn', function (event) {
            waitMethod.start('提交中')
            $.ajax({
              type: 'POST',
              url: api.refuseAddFriend,
              data: { messageID: messageID },
              dataType: 'json',
              success: function (data) {
                waitMethod.end()
                warpResponseData(data, (data) => {
                  updateSelfData(data)
                })
              },
              error: ajaxError
            })
          })
          msgdetail.on('click', '.accept-btn', function (event) {
            console.log(messageID)
            console.log(targetID)
            var chooseGroupPanelIndex = parent.layer.open({
              type: 1,
              title: '选择分组',
              content: createGroupChoosePanel(),
              id: 'groupChoose',
              area: ['480px', '300px'],
              success: function (layero, index) {
                parent.layui.form.render()
                parent.layui.form.on('submit(chooseGroupBtn)', function (data) {
                  console.log(data.field)
                  waitMethod.start('提交中')
                  $.ajax({
                    type: 'POST',
                    url: api.acceptAddFriend,
                    data: { groupID: data.field.group, messageID: messageID },
                    dataType: 'json',
                    success: function (data) {
                      waitMethod.end()
                      warpResponseData(data, (data) => {
                        updateSelfData(data)
                      })
                    },
                    error: ajaxError
                  })
                  parent.layer.close(chooseGroupPanelIndex)
                  return false
                })
              }
            })
          })
        }
      })
    }
  })
  $('.boxBlank').on('click', '.requestBtn', function (event) {
    event.stopPropagation()
    var messageID = $(this).data('msg-id')
    var targetID = $(this).data('target-id')
    console.log(messageID)
    console.log(targetID)
    var chooseGroupPanelIndex = parent.layer.open({
      type: 1,
      title: '选择分组',
      content: createGroupChoosePanel(),
      id: 'groupChoose',
      area: ['480px', '300px'],
      success: function (layero, index) {
        parent.layui.form.render()
        parent.layui.form.on('submit(chooseGroupBtn)', function (data) {
          console.log(data.field)
          waitMethod.start('提交中')
          $.ajax({
            type: 'POST',
            url: api.acceptAddFriend,
            data: { groupID: data.field.group, messageID: messageID },
            dataType: 'json',
            success: function (data) {
              waitMethod.end()
              warpResponseData(data, (data) => {
                updateSelfData(data)
              })
            },
            error: ajaxError
          })
          parent.layer.close(chooseGroupPanelIndex)
          return false
        })
      }
    })
  })

  $('#toolbar').on('click', '.layui-icon-search', function (event) {
    parent.layer.prompt({
      formType: 0,
      title: '查找好友',
      area: ['800px', '600px'] // 自定义文本域宽高
    }, function (value, index, elem) {
      userMethod.searchUser(value, function (data) {
        if (data.length === 0) {
          parent.layer.msg('未找到相关用户')
          return
        }
        openSearchFriendPanel(data)
      })
      parent.layer.close(index)
    })
  })

  parent.layui.form.verify({
    nickname: function (value, item) {
      value = value.trim()
      if (value.length > 10) {
        return '昵称不能超过10个字符'
      }
      if (value.length === 0) {
        return '昵称不能为空'
      }
    },
    signature: function (value, item) {
      if (value.length > 32) {
        return '签名不能超过32个字符'
      }
    }
  })

  $('#toolbar').on('click', '.layui-icon-set', function (event) {
    console.log(parent.selfData)
    console.log(parent.selfData.signature)
    var updateFormIndex = parent.layer.open({
      type: 1,
      id: 'updateBox',
      title: '更新信息',
      content: createUploadFormDOM(parent.selfData),
      area: '500px',
      shadeClose: false,
      resize: true,
      success: function (layero, index) {
        parent.upload.render({
          elem: '#updateAvatar',
          url: api.updateAvatar,
          size: 2048, // 限制文件大小，单位 KB
          accept: 'images',
          acceptMime: 'image/*',
          exts: 'bmp|gif|jpg|png',
          done: function (data) {
            console.log(data)
            if (data.code !== 0) {
              parent.layer.msg('错误:' + data.msg)
            } else {
              parent.$('#self-avatar').attr('src', data.data)
            }
          }
        })
        parent.layui.form.render()
        parent.$('.layui-form').on('click', '#cancel', function () {
          if (confirm('确定要关闭吗,所做的修改都会丢弃')) {
            parent.layer.close(updateFormIndex)
          }
        })
        parent.layui.form.on('submit(updateInfoBox)', function (data) {
          data.field.nickname = data.field.nickname.trim()
          data.field.signature = data.field.signature.trim()
          // layui最傻逼的设计就是开关的值不是返回true/false而是'on'/null
          if (!('private-email' in data.field)) {
            data.field['private-email'] = 'true'
          }
          if (!('private-phone' in data.field)) {
            data.field['private-phone'] = 'true'
          }
          if (!('private-sex' in data.field)) {
            data.field['private-sex'] = 'true'
          }
          if (data.field['private-email'] === 'on') {
            data.field['private-email'] = 'false'
          }
          if (data.field['private-phone'] === 'on') {
            data.field['private-phone'] = 'false'
          }
          if (data.field['private-sex'] === 'on') {
            data.field['private-sex'] = 'false'
          }
          delete data.field.file
          waitMethod.start('更新中')
          $.ajax({
            type: 'POST',
            url: api.update,
            data: data.field,
            dataType: 'json',
            xhrFields: {
              withCredentials: true
            },
            success: function (data) {
              waitMethod.end()
              warpResponseData(data, (data) => {
                updateSelfData(data)
              })
            },
            error: ajaxError
          })
          parent.layer.close(updateFormIndex)
          return false
        })
      },
      cancel: function (indexC) {
        if (confirm('确定要关闭吗,所做的修改都会丢弃')) {
          parent.layer.close(indexC)
        }
        return false
      }
    })
  })

  waitMethod.start('加载中...')
  updateSelfData(parent.selfData)
  flushFriendList().then(() => {
    userMethod.getUnreadMessage((data) => {
      console.log(data)
      data.forEach((item) => {
        if (!messageMethod.messageListData.has(item.messageID)) {
          var targetid = item.fromID === parent.selfData.userID ? item.toID : item.fromID
          if (item.action === 'PRIVATE_CHAT_MESSAGE') {
            parent.actionMessagePool.addMessage(targetid, item)
          }
          messageMethod.addOrRefreshMessage(item)
          userMethod.checkUserData(targetid, function (user) {
            $('#newMsgContent').append(messageMethod.newFriendMsgDom(user, item))
          })
        }
      })
    })
    userMethod.getHistoryMessage((data) => {
      console.log(data)
      data.forEach((item) => {
        if (!messageMethod.messageListData.has(item.messageID)) {
          var targetid = item.fromID === parent.selfData.userID ? item.toID : item.fromID
          if (item.action === 'PRIVATE_CHAT_MESSAGE') {
            parent.actionMessagePool.addMessage(targetid, item)
          }
          messageMethod.addOrRefreshMessage(item)
          userMethod.checkUserData(targetid, function (user) {
            $('#newMsgContent').append(messageMethod.newFriendMsgDom(user, item))
          })
        }
      })
    })
    waitMethod.end()
  })
})

function createGroupChoosePanel () {
  var DOM = $('<div/>')
  DOM.html(
    $('<form/>').attr({ class: 'layui-form', action: '##', onsubmit: 'return false', method: 'post' }).append(
      $('<div/>').attr({ class: 'layui-form-item' }).append(
        $('<label/>').attr({ class: 'layui-form-label cs-form-label' }).text('选择分组')
      ).append(
        $('<div/>').attr({ class: 'layui-input-block fullselect width50p' }).html(
          (() => {
            var select = $('<select/>').attr({ name: 'group' })
            console.log(groupMethod.groupListData)
            groupMethod.groupListData.forEach(function (group, groupID) {
              console.log(group)
              select.append($('<option/>').attr(groupID === -parent.selfData.userID ? { value: groupID, select: '' } : { value: groupID }).text(group.groupName))
            })
            return select
          })()
        )
      ).append(
        $('<button/>').attr({ class: 'layui-btn', id: 'choose', type: 'submit', 'lay-submit': '', 'lay-filter': 'chooseGroupBtn' }).text('确定')
      )
    )
  )
  return DOM.html()
}

function createMsgDetailPanelContent (messageID, targetID) {
  const message = messageMethod.messageListData.get(messageID)
  var messageContent = JSON.parse(message.content)
  console.log(message)
  var userData = userMethod.getUser(message.fromID)
  if (userData.code !== 0) {
    responseError(userData.msg)
    return ''
  }
  const user = userData.data
  console.log(user)
  var tmp = $('<div/>')
  tmp.append(
    $('<div/>').attr({ class: 'layui-row' }).append(
      $('<div/>').attr({ class: 'layui-col-xs3' }).html(
        $('<img/>').attr({ class: 'box-my-pic', src: user.avatar })
      )
    ).append(
      $('<div/>').attr({ class: 'layui-col-xs9 padding-10' }).html(
        $('<div/>').attr({ class: 'layui-row' }).append(
          $('<div/>').attr({ class: 'layui-col-xs12' }).append(
            $('<span>').attr({ class: 'title-label' }).text('昵称:')
          ).append(
            $('<span>').attr({ class: 'nickname' }).text(user.nickname)
          )
        ).append(
          $('<div/>').attr({ class: 'layui-col-xs12' }).append(
            $('<span>').attr({ class: 'title-label' }).text('性别:')
          ).append(
            (() => {
              switch (user.sex) {
                case '@PRIVATE?':
                  return $('<i/>').attr({ class: 'iconfont layui-extend-zhongxing my_iconfont sexicon', title: '未公开' })
                case '男':
                  return $('<i/>').attr({ class: 'layui-icon layui-icon-male my_iconfont sexicon', title: '男' })
                case '女':
                  return $('<i/>').attr({ class: 'layui-icon layui-icon-female my_iconfont sexicon', title: '女' })
                default:
                  return $('<i/>').attr({ class: 'iconfont layui-extend-zhongxing my_iconfont sexicon', title: '未知' })
              }
            })()
          )
        )
      )
    ).append(
      $('<div/>').attr({ class: 'layui-col-xs12' }).html(
        $('<div/>').attr({ class: 'layui-row layui-col-space5' }).html(
          $('<div/>').attr({ class: 'layui-col-xs6' }).html(
            $('<div/>').attr({ class: 'border' }).html(
              $('<div/>').attr({ class: 'layui-row' }).append(
                $('<div/>').attr({ class: 'layui-col-xs3' }).html(
                  $('<i/>').attr({ class: 'iconfont layui-extend-youxiang my_iconfont emailicon', title: '邮箱' })
                )
              ).append(
                $('<div/>').attr({ class: 'layui-col-xs9', style: 'float:right' }).html(
                  $('<div/>').attr({ class: 'infobox' }).html(
                    $('<span/>').text(user.email === '@PRIVATE?' ? '私密' : user.email)
                  )
                )
              )
            )
          )
        ).append(
          $('<div/>').attr({ class: 'layui-col-xs6' }).html(
            $('<div/>').attr({ class: 'border' }).html(
              $('<div/>').attr({ class: 'layui-row' }).append(
                $('<div/>').attr({ class: 'layui-col-xs3' }).html(
                  $('<i/>').attr({ class: 'layui-icon layui-icon-cellphone my_iconfont phoneicon', title: '电话' })
                )
              ).append(
                $('<div/>').attr({ class: 'layui-col-xs9', style: 'float:right' }).html(
                  $('<div/>').attr({ class: 'infobox' }).html(
                    $('<span/>').text(user.phoneNumber === '@PRIVATE?' ? '私密' : (user.phoneNumber === null || user.phoneNumber === '' ? '未知' : user.phone))
                  )
                )
              )
            )
          )
        )
      )
    ).append(
      $('<div/>').attr({ class: 'layui-col-xs12' }).html(
        $('<div/>').attr({ class: 'border' }).html(
          $('<div/>').attr({ class: 'layui-row' }).append(
            $('<div/>').attr({ class: 'layui-col-xs1 sigicon' }).html(
              $('<i/>').attr({ class: 'layui-icon layui-icon-note my_iconfont', title: '个性签名' })
            )
          ).append(
            $('<div/>').attr({ class: 'layui-col-xs10' }).html(
              $('<div/>').attr({ class: 'sig' }).html(
                $('<span/>').text(user.signature)
              )
            )
          )
        )
      )
    ).append(
      $('<div/>').attr({ class: 'layui-col-xs12' }).html(
        $('<div/>').attr({ class: 'border' }).html(
          $('<div/>').attr({ class: 'layui-row' }).append(
            $('<div/>').attr({ class: 'layui-col-xs1 sigicon' }).html(
              $('<i/>').attr({ class: 'iconfont layui-extend-icon-test8 my_iconfont', title: '验证消息' })
            )
          ).append(
            $('<div/>').attr({ class: 'layui-col-xs10' }).html(
              $('<div/>').attr({ class: 'sig' }).html(
                $('<span/>').text(messageContent.message)
              )
            )
          )
        )
      )
    ).append(
      $('<div/>').attr({ class: 'layui-col-xs12', style: 'margin-top: 15px;' }).html(
        (() => {
          switch (messageContent.result) {
            case 'IGNORE':
              return $('<div/>').attr({ class: 'layui-row' }).append(
                $('<div/>').attr({ class: 'layui-col-xs12', style: 'text-align: center;' }).html(
                  $('<button/>').attr({ class: 'layui-btn layui-bg-gray layui-btn-disabled', type: 'button' }).text('已忽略')
                )
              )
            case 'ACCEPT':
              return $('<div/>').attr({ class: 'layui-row' }).append(
                $('<div/>').attr({ class: 'layui-col-xs12', style: 'text-align: center;' }).html(
                  $('<button/>').attr({ class: 'layui-btn layui-btn-disabled', type: 'button' }).text('已接受')
                )
              )
            case 'REFUSE':
              return $('<div/>').attr({ class: 'layui-row' }).append(
                $('<div/>').attr({ class: 'layui-col-xs12', style: 'text-align: center;' }).html(
                  $('<button/>').attr({ class: 'layui-btn layui-bg-red layui-btn-disabled', type: 'button' }).text('已拒绝')
                )
              )
            case 'WAIT':
            default:
              return $('<div/>').attr({ class: 'layui-row' }).append(
                $('<div/>').attr({ class: 'layui-col-xs4' }).html(
                  $('<button/>').attr({ class: 'layui-btn accept-btn', type: 'button', style: 'float:right;' }).text('接受').data('msg-id', messageID).data('target-id', targetID)
                )
              ).append(
                $('<div/>').attr({ class: 'layui-col-xs4', style: 'text-align: center;' }).html(
                  $('<button/>').attr({ class: 'layui-btn refuse-btn layui-bg-red', type: 'button' }).text('拒绝').data('msg-id', messageID).data('target-id', targetID)
                )
              ).append(
                $('<div/>').attr({ class: 'layui-col-xs4' }).html(
                  $('<button/>').attr({ class: 'layui-btn ignore-btn layui-bg-gray', type: 'button' }).text('忽略').data('msg-id', messageID).data('target-id', targetID)
                )
              )
          }
        })()
      )
    )
  )
  return tmp.html()
}

function openSearchFriendPanel (data) {
  return parent.layer.open({
    type: 1,
    title: '查找好友',
    content: '',
    id: 'searchFriendPanel',
    area: ['560px', '300px'],
    success: function (layero, index) {
      console.log($(layero))
      var panel = $(layero).children('#searchFriendPanel')
      data.forEach((item) => {
        if (item.avatar == null || item.avatar === '@DEFAULT?') {
          item.avatar = 'assets/images/1.png'
        }
        panel.append(userMethod.newAddFriendDom(item))
      })
      panel.on('click', '.chatBtn', function (event) {
        var targetID = $(this).parents('.user:first').attr('id').substr(1)
        parent.layer.close(index)
        openWindow(targetID)
        console.log('好友聊天')
      })
      panel.on('click', '.addFriendBtn', function (event) {
        var targetID = $(this).parents('.user:first').attr('id').substr(1)
        openAddFriendRequestPanel(targetID)
      })
    }
  })
}

function openAddFriendRequestPanel (targetID) {
  var addFriendRequestPanelIndex = parent.layer.open({
    type: 1,
    id: 'addFriendRequest',
    title: '添加好友',
    content: createAddFriendRequestPanelContent(targetID),
    area: '500px',
    shadeClose: false,
    resize: true,
    success: function (layero, index) {
      parent.layui.form.render()
      parent.$('#addFriendRequest').on('click', '#cancel', function () {
        parent.layer.close(addFriendRequestPanelIndex)
      })
      parent.layui.form.on('submit(addFriendRequestBox)', function (data) {
        console.log(data)
        data.field.content = data.field.content.trim()
        waitMethod.start('提交中')
        $.ajax({
          type: 'POST',
          url: api.addFriend,
          data: { content: JSON.stringify({ groupID: data.field.group, message: data.field.content }), targetID: targetID },
          dataType: 'json',
          xhrFields: {
            withCredentials: true
          },
          success: function (data) {
            waitMethod.end()
            warpResponseData(data, (data) => {
              parent.layer.msg('发送成功')
            })
          },
          error: ajaxError
        })
        parent.layer.close(addFriendRequestPanelIndex)
        return false
      })
    }
  })
}

function createAddFriendRequestPanelContent (targetID) {
  var tmp = $('<div/>')
  tmp.append($('<form/>').attr({ class: 'layui-form', 'lay-filter': 'addFriendForm', action: '##', onsubmit: 'return false', method: 'post' }).append(
    $('<div/>').attr({ class: 'layui-form-item' }).html(
      $('<label/>').attr({ class: 'layui-form-label cs-form-label' }).text('性别')
    ).append(
      $('<div/>').attr({ class: 'layui-input-block fullselect' }).html(
        (() => {
          var select = $('<select/>').attr({ name: 'group' })
          console.log(groupMethod.groupListData)
          groupMethod.groupListData.forEach(function (group, groupID) {
            console.log(group)
            select.append($('<option/>').attr(groupID === -parent.selfData.userID ? { value: groupID, select: '' } : { value: groupID }).text(group.groupName))
          })
          return select
        })()
      )
    )).append(
    $('<div/>').attr({ class: 'layui-form-item layui-form-text' }).html(
      $('<label/>').attr({ class: 'layui-form-label cs-form-label' }).text('验证信息')).append(
      $('<div/>').attr({ class: 'layui-input-block' }).html(
        $('<textarea/>').attr({ name: 'content', class: 'layui-textarea' })))).append(
    $('<div/>').attr({ class: 'layui-form-item' }).html(
      $('<div/>').attr({ class: 'layui-input-block noborder' }).html(
        $('<button/>').attr({ class: 'layui-btn', id: 'submit', type: 'submit', 'lay-submit': '', 'lay-filter': 'addFriendRequestBox' }).text('提交')
      ).append(
        $('<button/>').attr({ class: 'layui-btn', id: 'cancel', type: 'button' }).text('取消')
      ))
  )
  )
  return tmp.html()
}

function openWindow (userID) {
  parent.createWindow(userMethod.userListData.get(userID))
}

/**
 * @description 获取分组数据
 * @throws ajaxErrorStatus
 * @returns Promise
 */
function getGroupList () {
  return $.ajax({
    type: 'POST',
    url: api.getGroupList,
    dataType: 'json',
    xhrFields: {
      withCredentials: true
    }
  })
}

/**
 * @description 刷新分组UI,此函数会等待获取分组数据
 * @returns true when forward function success and flush success
 */
async function flushGroupList () {
  var data
  try {
    data = await getGroupList()
    console.log(data)
  } catch (ajaxErrorStatus) {
    ajaxError(ajaxErrorStatus)
    return false
  }
  if (data.code !== 0) {
    responseError(data.msg)
    return false
  }
  data.data.forEach((item) => {
    groupMethod.addOrRefresh(item)
    uiMethod.addGroup(groupMethod.newGroup(item))
  })
  return true
}

/**
 * @description 获取好友列表数据,此函数会等待刷新分组UI
 * @throws {XMLHttpRequest} ajaxErrorStatus
 * @returns Promise when forward function success and ajax success
 */
async function getFriendList () {
  var result = await flushGroupList()
  if (result === false) {
    return false
  }
  return $.ajax({
    type: 'POST',
    url: api.getFriendList,
    dataType: 'json',
    xhrFields: {
      withCredentials: true
    }
  })
}

/**
 * @description 刷新好友列表UI,此函数会等待获取好友数据
 * @returns 函数执行结果(包括前置函数必须完成)
 */
async function flushFriendList () {
  try {
    var data = await getFriendList()
  } catch (ajaxErrorStatus) {
    ajaxError(ajaxErrorStatus)
    return false
  }
  if (data === false || data.code !== 0) {
    responseError(data.msg)
    return false
  }
  data.data.forEach(async (item) => {
    console.log(item)
    var userData = userMethod.getUser(item.userID)
    warpResponseData(userData, (user) => {
      userMethod.addOrRefreshUser(user)
      console.log(item)
      groupMethod.addFriendInGroup(userMethod.newFriendDom(user), item.userGroupID)
    })
  })
  return true
}

function ajaxError (jqXHR, textStatus, errorThrown) {
  waitMethod.end()
  if (errorThrown === null || errorThrown.length === 0) {
    parent.layer.msg('错误:' + jqXHR.statusText)
  } else {
    parent.layer.msg('错误:' + errorThrown)
  }
}

function responseError (msg) {
  parent.layer.msg('错误:' + msg)
}

/**
 * @description 对Response数据进行包装
 * @param {any} data 得到的Response数据
 * @param {function} callback 回调函数,传入Response的data为参数
 * @returns 返回Callback的执行结果
 */
function warpResponseData (data, callback) {
  if (data.code !== 0) {
    responseError(data.msg)
  } else {
    if (callback) { console.log(data); return callback(data.data) }
  }
}

/**
 * @description 创建上传版UI,作为layer.open内容
 * @param {Object} selfData 自己的信息
 */
function createUploadFormDOM (selfData) {
  var tmp = $('<div/>')
  tmp.append($('<form/>').attr({ class: 'layui-form', id: selfData.userID, 'lay-filter': 'updateForm', action: '##', onsubmit: 'return false', method: 'post' }).append(
    $('<div/>').attr({ class: 'layui-form-item' }).html(
      $('<label/>').attr({ class: 'layui-form-label cs-form-label' }).text('昵称')).append(
      $('<div/>').attr({ class: 'layui-input-block' }).html(
        $('<input/>').attr({ name: 'nickname', class: 'layui-input', type: 'text', placeholder: '请输入昵称', value: selfData.nickname, 'lay-verify': 'required|nickname' })))).append(
    $('<div/>').attr({ class: 'layui-form-item' }).html(
      $('<label/>').attr({ class: 'layui-form-label cs-form-label' }).text('性别')).append(
      $('<div/>').attr({ class: 'layui-input-block' }).html(
        $('<select/>').attr({ name: 'sex' }).append(
          $('<option/>').attr(selfData.sex === '男' ? { value: '男', select: '' } : { value: '男' }).text('男')).append(
          $('<option/>').attr(selfData.sex === '女' ? { value: '女', select: '' } : { value: '男' }).text('女')).append(
          $('<option/>').attr(selfData.sex === '未知' ? { value: '未知', select: '' } : { value: '男' }).text('未知'))).append(
        $('<input/>').attr({ name: 'private-sex', type: 'checkbox', 'lay-skin': 'switch', 'lay-text': '公开|私密', checked: selfData.privateSex ? null : '' })))).append(
    $('<div/>').attr({ class: 'layui-form-item' }).html(
      $('<label/>').attr({ class: 'layui-form-label cs-form-label' }).text('email')).append(
      $('<div/>').attr({ class: 'layui-input-block' }).html(
        $('<input/>').attr({ name: 'email', class: 'layui-input', type: 'text', placeholder: '请输入邮箱', value: selfData.email, 'lay-verify': 'required|email' })).append(
        $('<input/>').attr({ name: 'private-email', type: 'checkbox', 'lay-skin': 'switch', 'lay-text': '公开|私密', checked: selfData.privateEmail ? null : '' })))).append(
    $('<div/>').attr({ class: 'layui-form-item' }).html(
      $('<label/>').attr({ class: 'layui-form-label cs-form-label' }).text('电话')).append(
      $('<div/>').attr({ class: 'layui-input-block' }).html(
        $('<input/>').attr({ name: 'phone', class: 'layui-input', type: 'text', placeholder: '请输入电话', value: selfData.phone, 'lay-verify': 'phone|number' })).append(
        $('<input/>').attr({ name: 'private-phone', type: 'checkbox', 'lay-skin': 'switch', 'lay-text': '公开|私密', checked: selfData.privatePhone ? null : '' })))).append(
    $('<div/>').attr({ class: 'layui-form-item layui-form-text' }).html(
      $('<label/>').attr({ class: 'layui-form-label cs-form-label' }).text('签名')).append(
      $('<div/>').attr({ class: 'layui-input-block' }).html(
        $('<textarea/>').attr({ name: 'signature', class: 'layui-textarea', placeholder: '请输入内容', 'lay-verify': 'signature' }).text(selfData.signature)))).append(
    $('<div/>').attr({ class: 'layui-form-item' }).html(
      $('<div/>').attr({ class: 'layui-input-block noborder' }).html(
        $('<button/>').attr({ class: 'layui-btn', id: 'updateAvatar', type: 'button' }).text('更新头像')
      ).append(
        $('<button/>').attr({ class: 'layui-btn', id: 'submit', type: 'submit', 'lay-submit': '', 'lay-filter': 'updateInfoBox' }).text('提交')
      ).append(
        $('<button/>').attr({ class: 'layui-btn', id: 'cancel', type: 'button' }).text('取消')
      ))
  )
  )
  return tmp.html()
}

/**
 * @description 创建用户信息窗
 * @param {Object} targetID 用户ID
 */
function createUserInfomationPanelDOM (targetID) {
  var userData = userMethod.getUser(targetID)
  if (userData.code !== 0) {
    responseError(userData.msg)
    return ''
  }
  const user = userData.data
  console.log(user)
  var tmp = $('<div/>')
  tmp.append(
    $('<div/>').attr({ class: 'layui-row infodetail' }).append(
      $('<div/>').attr({ class: 'layui-col-xs3' }).html(
        $('<img/>').attr({ class: 'box-my-pic', src: user.avatar })
      )
    ).append(
      $('<div/>').attr({ class: 'layui-col-xs9 padding-10' }).html(
        $('<div/>').attr({ class: 'layui-row' }).append(
          $('<div/>').attr({ class: 'layui-col-xs12' }).append(
            $('<span>').attr({ class: 'title-label' }).text('昵称:')
          ).append(
            $('<span>').attr({ class: 'nickname' }).text(user.nickname)
          )
        ).append(
          $('<div/>').attr({ class: 'layui-col-xs12' }).append(
            $('<span>').attr({ class: 'title-label' }).text('性别:')
          ).append(
            (() => {
              switch (user.sex) {
                case '@PRIVATE?':
                  return $('<i/>').attr({ class: 'iconfont layui-extend-zhongxing my_iconfont sexicon', title: '未公开' })
                case '男':
                  return $('<i/>').attr({ class: 'layui-icon layui-icon-male my_iconfont sexicon', title: '男' })
                case '女':
                  return $('<i/>').attr({ class: 'layui-icon layui-icon-female my_iconfont sexicon', title: '女' })
                default:
                  return $('<i/>').attr({ class: 'iconfont layui-extend-zhongxing my_iconfont sexicon', title: '未知' })
              }
            })()
          )
        )
      )
    ).append(
      $('<div/>').attr({ class: 'layui-col-xs12' }).html(
        $('<div/>').attr({ class: 'layui-row layui-col-space5' }).html(
          $('<div/>').attr({ class: 'layui-col-xs6' }).html(
            $('<div/>').attr({ class: 'border' }).html(
              $('<div/>').attr({ class: 'layui-row' }).append(
                $('<div/>').attr({ class: 'layui-col-xs3' }).html(
                  $('<i/>').attr({ class: 'iconfont layui-extend-youxiang my_iconfont emailicon mt-5', title: '邮箱' })
                )
              ).append(
                $('<div/>').attr({ class: 'layui-col-xs9', style: 'float:right' }).html(
                  $('<div/>').attr({ class: 'infobox' }).html(
                    $('<span/>').text(user.email === '@PRIVATE?' ? '私密' : user.email)
                  )
                )
              )
            )
          )
        ).append(
          $('<div/>').attr({ class: 'layui-col-xs6' }).html(
            $('<div/>').attr({ class: 'border' }).html(
              $('<div/>').attr({ class: 'layui-row' }).append(
                $('<div/>').attr({ class: 'layui-col-xs3' }).html(
                  $('<i/>').attr({ class: 'layui-icon layui-icon-cellphone my_iconfont phoneicon mt-5', title: '电话' })
                )
              ).append(
                $('<div/>').attr({ class: 'layui-col-xs9', style: 'float:right' }).html(
                  $('<div/>').attr({ class: 'infobox' }).html(
                    $('<span/>').text(user.phoneNumber === '@PRIVATE?' ? '私密' : (user.phoneNumber === null || user.phoneNumber === '' ? '未知' : user.phone))
                  )
                )
              )
            )
          )
        )
      )
    ).append(
      $('<div/>').attr({ class: 'layui-col-xs12' }).html(
        $('<div/>').attr({ class: 'border' }).html(
          $('<div/>').attr({ class: 'layui-row' }).append(
            $('<div/>').attr({ class: 'layui-col-xs1 sigicon' }).html(
              $('<i/>').attr({ class: 'layui-icon layui-icon-note my_iconfont', title: '个性签名' })
            )
          ).append(
            $('<div/>').attr({ class: 'layui-col-xs10' }).html(
              $('<div/>').attr({ class: 'sig' }).html(
                $('<span/>').text(user.signature)
              )
            )
          )
        )
      )
    )
  )
  return tmp.html()
}

function updateSelfData (data) {
  parent.updateSelfData(data)
  $('#self-signature').text(data.signature || '')
}

function receive (message) {
  console.log(message)
  console.log(message.fromID)
  var targetid = message.fromID === parent.selfData.userID ? message.toID : message.fromID
  console.log(`receive the message:[${message.content}] by message id:[${message.messageID}]`)
  if (!parent.ChatBoxMap.hasOpen(message.fromID)) {
    console.log('notOpen')
    uiMethod.MessageDot.showUser(message.fromID)
  } else {
    var chatBox = parent.ChatBoxMap.getWindow(targetid)
    console.log(chatBox)
    chatBox.receive(message)
  }
  console.log(message)
  if (message.action === 'PRIVATE_CHAT_MESSAGE') {
    console.log(message)
    parent.actionMessagePool.addMessage(targetid, message)
  }
  messageMethod.addOrRefreshMessage(message)
  userMethod.checkUserData(targetid, function (user) {
    $('#newMsgContent').append(messageMethod.newFriendMsgDom(user, message))
  })
}
