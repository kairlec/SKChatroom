const $ = layui.$
// 中性图标 <i class="iconfont layui-extend-zhongxing my_iconfont"></i>
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
    }
  },
  groupListData: new Map(),
  addOrRefresh: function (data) {
    this.groupListData.set(data.groupID, data)
  },
  // <i class="layui-bg-red layui-badge-dot msgdot" id="MessageGroupDot"></i>
  newGroup: function (groupObject) {
    return (
      $('<div/>').attr({ class: 'layui-colla-item' }).html(
        $('<h2/>').attr({ class: 'layui-colla-title' }).html(
          $('<i/>').attr({ class: 'layui-icon layui-icon-right' })).append(
          $('<span/>').text(groupObject.groupName)).append(
          $('<i/>').attr({ class: 'layui-bg-red layui-badge-dot GroupMsgdot', style: 'display: none;' })
        )).append(
        $('<div/>').attr({ class: 'layui-colla-content group', id: 'g' + groupObject.groupID }))
    )
  },
  addFriendInGroup: function (friendDOM, groupID) {
    console.log($('#g' + groupID))
    $('#g' + groupID).append(friendDOM)
  }

}
const uiMethod = {
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
      $('#f' + userID).find('.UserMsgdot').show().css('display', 'inline-block')
      console.log($('#f' + userID).find('.UserMsgdot'))
      this.showGroup($('#f' + userID).parent('.group').attr('id').substr(1))
    },
    hideUser: function (userID) {
      var hasMsg = false
      $('#f' + userID).find('.UserMsgdot').hide()
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

const userMethod = {
  userListData: new Map(),
  addOrRefreshUser: function (data) {
    if (data.avatar == null || data.avatar === '@DEFAULT?') {
      data.avatar = 'assets/images/1.png'
    }
    this.userListData.set(data.userID, data)
  },
  isFriend: function (userID) {
    return false
    return this.userListData.has(userID)
  },
  addOrRefreshMessage: function (data) {
    this.userListData.add(data.fromID)
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
    // TODO:添加好友的块
    const DOM = (
      $('<div/>').attr({ class: 'layui-row box-my-list user', id: 'a' + user.userID }).html(
        $('<div/>').attr({ class: 'layui-col-xs1' }).html(
          $('<div/>').attr({ class: 'layui-row' }).html(
            $('<div/>').attr({ class: 'layui-col-xs12' }).html(
              $('<img/>').attr({ class: 'box-my-pic', src: user.avatar }))))).append(
        $('<div/>').attr({ class: 'layui-col-xs8' }).html(
          $('<div/>').attr({ class: 'layui-row' }).html(
            $('<div/>').attr({ class: 'layui-col-xs12 box-my-name' }).html(
              $('<span>').attr({ class: 'userNickname' }).text(user.nickname))).append(
            $('<div/>').attr({ class: 'layui-col-xs12 box-my-signal box-line-1' }).text(user.signature)))).append(
        $('<div/>').attr({ class: 'layui-col-xs3' }).html(
          $('<div/>').attr({ class: 'layui-row' }).html(
            $('<div/>').attr({ class: 'layui-col-xs6' }).html(
              $('<button/>').attr({ class: 'layui-btn searchBtn informationBtn', type: 'button' }).text('资料'))).append(
            $('<div/>').attr({ class: 'layui-col-xs6' }).html(
              $('<button/>').attr({ class: `layui-btn searchBtn ${this.isFriend(user.userID) ? 'chatBtn' : 'addFriendBtn'}`, type: 'button' }).text(this.isFriend(user.userID) ? '聊天' : '添加'))
          )
        )
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
      url: api.getMessage + 'unreadTo',
      dataType: 'json',
      xhrFields: {
        withCredentials: true
      },
      success: function (data) { warpResponseData(data, fnSuccess) },
      error: ajaxError
    })
  },
  getUser: function (userID, fnSuccess) {
    $.ajax({
      type: 'POST',
      url: api.getUserInfo + userID,
      dataType: 'json',
      xhrFields: {
        withCredentials: true
      },
      success: function (data) {
        console.log(data)
        warpResponseData(data, fnSuccess)
      },
      error: ajaxError
    })
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
    parent.menuIndex = parent.mouseRightMenu.open(getGroupData($(this).children('.group').attr('id').substr(1)), { offset: parent.getBoxMouseXY(event.pageX, event.pageY) }, function (data) {
      // TODO:分组右击
      switch (data.type) {
        case 1:// 展开/收缩
          uiMethod.GroupPand.toggleExpanded(data.data)
          break
        case 2:// 重命名
          parent.layer.prompt({
            formType: 0,
            title: '请输入分组名',
            value: groupMethod.name(data.data),
            area: ['800px', '350px'] // 自定义文本域宽高
          }, function (value, index, elem) {
            // $.ajax({
            //   type: 'POST',
            //   url: api.ignoreAddFriend,
            //   data:
            //   dataType: 'json',
            //   xhrFields: {
            //     withCredentials: true
            //   },
            //   success: function (data) { warpResponseData(data, fnSuccess) },
            //   error: ajaxError
            // })
            parent.layer.close(index)
          })
          break
        case 3:// 删除分组
          break
      }
    })
    return false
  })

  $('#group').on('contextmenu', '.user', function (event) {
    parent.menuIndex = parent.mouseRightMenu.open(getUserData($(this).attr('id').substr(1)), { offset: parent.getBoxMouseXY(event.pageX, event.pageY) }, function (data) {
      // TODO:用户右击
      switch (data.type) {
        case 1:// 打开会话
          openWindow(data.data)
          break
        case 2:// 删除好友
          break
        // case 3:
        //   break
      }
    })
    return false
  })

  $('.boxBlank').on('contextmenu', function (event) {
    parent.menuIndex = parent.mouseRightMenu.open(getBlankData(), { offset: parent.getBoxMouseXY(event.pageX, event.pageY) }, function (data) {
      // TODO: 空白块右击
      switch (data.type) {
        case 1:// 刷新列表
          break
        case 2:// 新建分组
          break
        // case 3:
        //   break
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
        // TODO: 好友搜索
        parent.layer.open({
          type: 1,
          title: '查找好友',
          content: '',
          id: 'searchFriendPanel',
          area: ['560px', '300px'],
          success: function (layero, index) {
            console.log($(layero))
            var panel
            $.each(data, function (index, item) {
              if (item.avatar == null || item.avatar === '@DEFAULT?') {
                item.avatar = 'assets/images/1.png'
              }
              panel = $(layero).children('#searchFriendPanel')
              console.log(panel)
              panel.append(userMethod.newAddFriendDom(item))
            })
            panel.on('click', '.informationBtn', function (event) {
              console.log('好友资料')
            })
            panel.on('click', '.chatBtn', function (event) {
              console.log('好友聊天')
            })
            panel.on('click', '.addFriendBtn', function (event) {
              console.log('好友添加')
            })
          }
        })
        console.log(data)
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

  userMethod.getUnreadMessage((data) => {
    console.log(data)
    $.each(data, function (index, item) {
      parent.actionMessagePool.addMessage(item.fromID, item)
    })
  })

  updateSelfData(parent.selfData)

  flushFriendList()
})

function openWindow (userID) {
  uiMethod.MessageDot.hideUser(userID)
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
 * @returns true when rorward function success and flush success
 */
async function flushGroupList () {
  console.log(getGroupList())
  try {
    var data = await getGroupList()
  } catch (ajaxErrorStatus) {
    ajaxError(ajaxErrorStatus)
    return false
  }
  if (data.code !== 0) {
    responseError(data.msg)
    return false
  }
  $.each(data.data, function (index, item) {
    groupMethod.addOrRefresh(item)
    uiMethod.addGroup(groupMethod.newGroup(item))
  })
  return true
}

/**
 * @description 获取好友列表数据,此函数会等待刷新分组UI
 * @throws {XMLHttpRequest} ajaxErrorStatus
 * @returns Promise when rorward function success and ajax success
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
  $.each(data.data, function (index, item) {
    console.log(item)
    userMethod.getUser(item.userID, (data) => {
      userMethod.addOrRefreshUser(data)
      groupMethod.addFriendInGroup(userMethod.newFriendDom(data), item.userGroupID)
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
  parent.layui.msg('错误:' + msg)
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
  }
  if (callback) { return callback(data.data) }
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

function updateSelfData (data) {
  parent.updateSelfData(data)
  $('#self-signature').text(data.signature || '')
}

function receive (message) {
  if (!parent.ChatBoxMap.hasOpen(message.fromID)) {
    uiMethod.MessageDot.showUser(message.fromID)
  }
}
