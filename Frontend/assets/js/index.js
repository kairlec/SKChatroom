var $ = layui.$
$.ajaxSetup({
  xhrFields: {
    withCredentials: true
  }
})
var layer = layui.layer
var mouseRightMenu = layui.mouseRightMenu
var upload = layui.upload
// 主窗口
var chatBoxIndex
// 右键菜单
var menuIndex
// 用户自己的信息
var selfData

function getMainBoxWindow () {
  return window['layui-layer-iframe' + chatBoxIndex]
}

var ChatBoxMap = {
  mp: new Map(),
  hasOpen: function (userID) {
    console.log(ChatBoxMap)
    console.log(userID)
    return this.mp.has(userID)
  },
  addWindow: function (userID, index) {
    this.mp.set(userID, index)
  },
  closeID: function (userID) {
    if (this.hasOpen(userID)) {
      layer.close(this.mp.get(userID))
      this.mp.delete(userID)
    }
  },
  getWindow: function (userID) {
    if (!this.hasOpen(userID)) {
      return null
    }
    return window['layui-layer-iframe' + this.mp.get(userID)]
  },
  getWindowDOM: function (userID) {
    if (!this.hasOpen(userID)) {
      return null
    }
    return $('#u' + userID).parent()
  },
  /**
   * @description 向指定窗口发送消息
   * @param {String} userID 用户的ID,以此来确定窗口对象
   * @param {Obejct} message 消息体,必须是ActionMessage消息体
   */
  sendMessage: function (userID, message) {
    console.log(message)
    if (!this.hasOpen(userID)) {
      console.log(userID + 'is not open')
      return
    }
    this.getWindow(userID).receive(message)
  },
  remove: function (userID) {
    this.mp.delete(userID)
  }

}

start()
function start () {
  const waitIndex = layer.msg('连接中', {
    icon: 16,
    shade: 0.3,
    time: 0
  })
  $.ajax({
    type: 'POST',
    dataType: 'json',
    url: api.loginStatus,
    xhrFields: {
      withCredentials: true
    },
    success: function (data) {
      console.log(data)
      if (data.code === 30007) {
        window.location.href = 'login'
      } else if (data.code !== 0) {
        layer.close(waitIndex)
        var msg = data.msg || '连接失败'
        layer.confirm(msg + ',是否重新连接', { icon: 3, title: '错误' }, function (index) {
          start()
          layer.close(index)
        })
        return
      }
      layer.close(waitIndex)
      selfData = data.data
      if (selfData.avatar == null || selfData.avatar === '@DEFAULT') {
        selfData.avatar = 'assets/images/1.png'
      }
      chatBoxIndex = layer.open({
        type: 2,
        closeBtn: 1,
        offset: 'auto',
        title: [
          '<img id="self-avatar" src="' + selfData.avatar + '" /><div class="box-title-container"><div class="box-title-name"><span id="self-nickname">' + selfData.nickname + '</span><span id="self-status" class="layui-bg-green layui-badge-dot"></span></div></div>',
          'height:40px;line-height:80px;border:none;'
        ],
        id: 'box',
        maxmin: true,
        shade: 0,
        shadeClose: false, // 点击遮罩关闭层
        area: ['260px', '520px'],
        content: 'box.html',
        resize: true,
        scrollbar: false,
        success: function (layero, index) {
          console.log(`this box html is ${index}`)
        },
        cancel: function (index, layero) { // 点击关闭时执行最小化
          layer.min(chatBoxIndex)
          return false
        }
        // resizing: function (layero, index) {
        //   var body = layer.getChildFrame('body', index)
        //   console.log(body)
        //   // var iframeWin = window[layero.find('iframe')[0].name] // 得到iframe页的窗口对象，执行iframe页的方法：iframeWin.method();
        //   // console.log(body.html()) // 得到iframe页的body内容
        //   console.log(body.find('#toolbar').html())
        //   console.log(layero)
        // }
      })
    },
    error: function () {
      layer.close(waitIndex)
      layer.confirm('连接到服务器失败,是否重新连接?', { icon: 3, title: '提示' }, function (index) {
        start()
        layer.close(index)
      })
    }
  })
}

function closeMenu () { layer.close(menuIndex) }

/* 屏蔽浏览器右键 */
document.oncontextmenu = () => {
  closeMenu()
  return false
}

/* 获取鼠标在Box中的绝对位置 */
function getBoxMouseXY (x, y) {
  var offset = $('#box').offset()
  return [(y + offset.top) + 'px', (x + offset.left) + 'px']
}

// 详细的聊天界面
function createWindow (user) {
  if (user.avatar == null || user.avatar === '@DEFAULT') {
    user.avatar = 'assets/images/1.png'
  }
  layer.open({
    type: 2,
    title: [
      '<img src="' + user.avatar + '"><div class="title-container"><div class="title-name">' + user.nickname + '</div><span class="title-msg">' + user.signature + '</span></div>',
      'height:80px;line-height:80px;'
    ],
    id: 'u' + user.userID,
    maxmin: true,
    shade: 0,
    shadeClose: false,
    area: ['600px;min-width:400px', '520px;min-height:400px'],
    content: 'chat.html',
    scrollbar: false,
    success: function (layero, index) {
      ChatBoxMap.addWindow(user.userID, index)
      var totalWindow = window['layui-layer-iframe' + index]
      totalWindow.totalUserID = user.userID
      totalWindow.ready()
      console.log(ChatBoxMap)
    },
    resizing: function (layero) {
      var index = ChatBoxMap.get($(this).attr('id'))
      layero.height(function (n, c) {
        var minHeight = parseInt(layero.css('min-height'))
        if (c < minHeight) {
          return minHeight
        }
        return c
      })
      layero.find('iframe').height(layero.height() - layero.find('.layui-layer-title').height())
      var body = layer.getChildFrame('body', index)
      var bodyHeight = parseInt(body.children('.chat-body').height())
      var contentBox = body.find('#content')
      contentBox.height(bodyHeight - 180)
      contentBox.find('.msgContent').css('max-width', (parseInt(contentBox.width()) - 132) + 'px')
    },
    cancel: function (index, layero) {
      ChatBoxMap.remove(user.userID)
    }
  })
}

/* 提示框 */
function message (msg) {
  layer.msg(msg)
}
function ajaxError (jqXHR, textStatus, errorThrown) {
  if (errorThrown === null || errorThrown.length === 0) {
    layer.msg('错误:' + jqXHR.statusText)
  } else {
    layer.msg('错误:' + errorThrown)
  }
}

function updateSelfData (data) {
  selfData = data
  if (selfData.signature === null) {
    selfData.signature = ''
  }
  $('#self-nickname').text(data.nickname)
}

var actionMessagePool = {
  mp: new Map(),
  addMessage: function (targetID, message) {
    var array = this.mp.get(targetID)
    if (typeof (array) === 'undefined') {
      array = new Array(0)
      this.mp.set(targetID, array)
    }
    array.push(message)
  },
  getMessage: function (targetID) {
    console.log(targetID)
    var array = this.mp.get(targetID)
    if (typeof (array) === 'undefined') {
      array = new Array(0)
      this.mp.set(targetID, array)
    }
    return array
  }
}

window.onbeforeunload = function () {
  console.trace(new Error().stack)
}
