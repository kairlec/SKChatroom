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
  document.oncontextmenu = () => {
    parent.closeMenu()
    return false
  }

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

  $('#toolbar').on('click', '.layui-icon-search', function (event) {
    parent.layer.prompt({
      formType: 0,
      title: '请输入查找内容',
      area: ['800px', '350px'] // 自定义文本域宽高
    }, function (value, index, elem) {
      userMethod.searchUser(value, function (data) {
        console.log(data)
      })
      parent.layer.close(index)
    })
  })
  parent.layui.form.verify({
    nickname: function (value, item) { // value：表单的值、item：表单的DOM对象
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
      content: createValueInputDOM({
        id: 'updateForm',
        lay_filter: 'updateForm',
        inputBlock: [
          { label: '昵称', name: 'nickname', type: 'input', placeholder: '请输入昵称', value: parent.selfData.nickname, lay_verify: 'required|nickname' },
          {
            label: '性别',
            name: 'sex',
            type: 'select',
            selectList: [{ value: '男', text: '男' }, { value: '女', text: '女' }, { value: '未知', text: '未知' }],
            value: parent.selfData.sex
          },
          { label: 'email', name: 'email', type: 'input', placeholder: '请输入邮箱', value: parent.selfData.email, lay_verify: 'required|email' },
          { label: '电话', name: 'phone', type: 'input', placeholder: '请输入号码', value: parent.selfData.phone, lay_verify: 'phone|number' },
          { label: '签名', id: 'tv', name: 'signature', type: 'richInput', value: parent.selfData.signature, lay_verify: 'signature' }
        ],
        buttonBlock: [
          { type: 'button', id: 'updateAvatar', field: '更新头像' },
          { type: 'submit', id: 'submit', field: '提交', lay_submit: '', lay_filter: 'updateInfoBox' },
          { type: 'button', class: 'layui-btn-normal', id: 'cancel', field: '取消' }
        ]
      }),
      area: '500px',
      shadeClose: false,
      resize: true,
      // btn: ['提交', '取消'],
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
        console.log(index)
        parent.layui.form.render()
        parent.layui.form.on('select', function (data) {
          console.log(data)
        })
        console.log(parent.layui.form.val('updateForm'))
        // parent.layui.form.val('updateForm', {
        //   nickname: parent.selfData.nickname,
        //   sex: parent.selfData.sex,
        //   email: parent.selfData.email,
        //   phone: parent.selfData.phone,
        //   signature: parent.selfData.signature
        // })
        // console.log(parent.layui.form.val('updateForm'))
        // parent.$('.layui-form').on('click', '#submit', function () {
        //   var layero = parent.$('#updateForm')
        //   const data = {}
        //   layero.find('input').each(function () {
        //     if (typeof ($(this).attr('name')) !== 'undefined') {
        //       data[$(this).attr('name')] = $(this).val()
        //     }
        //   })
        //   layero.find('select').each(function () {
        //     if (typeof ($(this).attr('name')) !== 'undefined') {
        //       data[$(this).attr('name')] = $(this).val()
        //     }
        //   })
        //   layero.find('textarea').each(function () {
        //     if (typeof ($(this).attr('name')) !== 'undefined') {
        //       data[$(this).attr('name')] = $(this).val()
        //     }
        //   })
        //   console.log(data)
        //   waitMethod.start('更新中')
        //   $.ajax({
        //     type: 'POST',
        //     url: api.update,
        //     data: data,
        //     dataType: 'json',
        //     xhrFields: {
        //       withCredentials: true
        //     },
        //     success: function (data) {
        //       waitMethod.end()
        //       warpResponseData(data, (data) => {
        //         updateSelfData(data)
        //       })
        //     },
        //     error: ajaxError
        //   })
        // })
        parent.$('.layui-form').on('click', '#cancel', function () {
          if (confirm('确定要关闭吗,所做的修改都会丢弃')) {
            parent.layer.close(updateFormIndex)
          }
        })
        parent.layui.form.on('submit(updateInfoBox)', function (data) {
          console.log(data.field)
          data.field.nickname = data.field.nickname.trim()
          data.field.signature = data.field.signature.trim()
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
      // yes: function (index, layero) {
      //   const data = {}
      //   layero.find('input').each(function () {
      //     if (typeof ($(this).attr('name')) !== 'undefined') {
      //       data[$(this).attr('name')] = $(this).val()
      //     }
      //   })
      //   layero.find('select').each(function () {
      //     if (typeof ($(this).attr('name')) !== 'undefined') {
      //       data[$(this).attr('name')] = $(this).val()
      //     }
      //   })
      //   layero.find('textarea').each(function () {
      //     if (typeof ($(this).attr('name')) !== 'undefined') {
      //       data[$(this).attr('name')] = $(this).val()
      //     }
      //   })
      //   console.log(data)
      //   waitMethod.start('更新中')
      //   $.ajax({
      //     type: 'POST',
      //     url: api.update,
      //     data: data,
      //     dataType: 'json',
      //     xhrFields: {
      //       withCredentials: true
      //     },
      //     success: function (data) {
      //       waitMethod.end()
      //       warpResponseData(data, (data) => {
      //         updateSelfData(data)
      //       })
      //     },
      //     error: ajaxError
      //   })
      // },
      // btn2: function (index, layero) {
      //   return this.cancel(index, layero)
      // },
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
 * @description 创建输入组
 * @param {Object} itemSet 包含组元件信息的集
 */
function createValueInputDOM (itemSet) {
  var tmp = $('<div/>')
  var DOM = $('<form/>').attr({ class: 'layui-form', id: itemSet.id, 'lay-filter': itemSet.lay_filter, action: '##', onsubmit: 'return false', method: 'post' })
  $.each(itemSet.inputBlock, function (index, item) {
    DOM.append(
      $('<div/>').attr({ class: item.type === 'richInput' ? 'layui-form-item layui-form-text' : 'layui-form-item' }).html(
        $('<label/>').attr({ class: 'layui-form-label cs-form-label' }).text(item.label)).append(((type) => {
        if (type === 'input') {
          return $('<div/>').attr({ class: 'layui-input-block' }).html(
            $('<input/>').attr({
              id: item.id || '',
              name: item.name || '',
              class: 'layui-input',
              type: item.inputType || 'text',
              placeholder: item.placeholder || '',
              autocomplete: item.autocomplete || 'off',
              value: item.value || '',
              'lay-verify': item.lay_verify
            }))
        } else if (type === 'select') {
          const inputBlock = $('<div/>').attr({ class: 'layui-input-block' })
          const selectDOM = $('<select/>').attr({ name: item.name })
          $.each(item.selectList, function (index, itemOption) {
            let attr
            console.log(itemOption.value)
            console.log(item.value)
            if (itemOption.value === item.value) {
              attr = { value: itemOption.value, selected: '' }
            } else {
              attr = { value: itemOption.value }
            }
            selectDOM.append($('<option/>').attr(attr).text(itemOption.text))
          })
          inputBlock.html(selectDOM)
          return inputBlock
        } else if (type === 'richInput') {
          console.log(item.value)
          return $('<div/>').attr({ class: 'layui-input-block' }).html(
            $('<textarea/>').attr({
              id: item.id || '',
              name: item.name || '',
              class: 'layui-textarea',
              placeholder: item.placeholder || '请输入内容',
              'lay-verify': item.lay_verify
            }).text(item.value))
        }
      })(item.type)))
  })

  /**
   * @description 创建按钮组
   */
  var buttonGroup = $('<div/>').attr({ class: 'layui-input-block' })
  $.each(itemSet.buttonBlock, function (index, item) {
    var classes = item.class || ''
    if (item.type === 'submit') {
      buttonGroup.append(
        $('<button/>').attr({
          class: 'layui-btn ' + classes,
          id: item.id,
          type: item.type,
          'lay-submit': item.lay_submit,
          'lay-filter': item.lay_filter
        }).text(item.field)
      )
    } else {
      buttonGroup.append(
        $('<button/>').attr({ class: 'layui-btn ' + classes, id: item.id, type: item.type }).text(item.field)
      )
    }
  })
  if (itemSet.buttonBlock.length > 0) {
    DOM.append(
      $('<div/>').attr({ class: 'layui-form-item' }).html(buttonGroup)
    )
  }
  tmp.html(DOM)
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
