var $ = layui.$
var layer = layui.layer
var mouseRightMenu = layui.mouseRightMenu
// 主窗口
var chatBoxIndex
// 右键菜单
var menuIndex
// 用户自己的信息
var selfData
function getAvatar (id, jqImg) {
  $.ajax({
    type: 'POST',
    url: api.getAvatarResource + id,
    xhrFields: {
      withCredentials: true
    },
    xhr: function () {
      var xhr = new XMLHttpRequest()
      xhr.responseType = 'blob'
      return xhr
    },
    success: function (data) {
      jqImg.attr('src', window.URL.createObjectURL(data))
    },
    error: ajaxError
  })
}
start()
function start () {
  var waitIndex = layer.msg('连接中', {
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
      if (data.code === 30007) {
        window.location.href = 'login'
      } else if (data.code !== 0) {
        layer.close(waitIndex)
        var msg = data.message || '连接失败'
        layer.confirm(msg + ',是否重新连接', { icon: 3, title: '错误' }, function (index) {
          start()
          layer.close(index)
        })
        return
      }
      layer.close(waitIndex)
      selfData = data.data
      chatBoxIndex = layer.open({
        type: 2,
        closeBtn: 1,
        offset: 'auto',
        title: [
          '<img id="myAvatar" src="assets/images/1.png"><div class="box-title-container"><div class="box-title-name">' + data.data.nickname + '<span class="layui-bg-green layui-badge-dot"></span></div></div>',
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
      if (data.data.avatar !== null && data.data.avatar !== '@Default?') {
        getAvatar(data.data.userID, $('#myAvatar'))
      }
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
  layer.open({
    type: 2,
    title: [
      '<img src="assets/images/1.png"><div class="title-container"><div class="title-name">' + user.id + '</div><span class="title-msg">对方正在输入...</span></div>',
      'height:80px;line-height:80px;'
    ],
    maxmin: true,
    shade: 0,
    shadeClose: false,
    area: ['600px;min-width:400px', '520px;min-height:400px'],
    content: 'chat.html',
    scrollbar: false,
    success: function (layero, index) {
      console.log(layero, index)
    },
    resizing: function (layero, index) {
      layero.height(function (n, c) {
        var minHeight = parseInt(layero.css('min-height'))
        if (c < minHeight) {
          return minHeight
        }
        return c
      })
      layero.find('iframe').height(layero.height() - layero.find('.layui-layer-title').height())
    }
  })
}

/* 提示框 */
function message (msg) {
  layer.msg(msg)
}
