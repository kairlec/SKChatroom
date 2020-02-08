var $ = layui.$
var layer = layui.layer

var chat_box_index = layer.open({
  type: 2,
  closeBtn: 1,
  offset: 'auto',
  title: [
    '<div class="box-title-container"><div class="box-title-name">Kairlec<span class="layui-bg-green layui-badge-dot"></span></div></div>',
    'height:40px;line-height:80px;border:none;'
  ],
  maxmin: true,
  shade: 0,
  shadeClose: false, // 点击遮罩关闭层
  area: ['260px', '520px'],
  content: 'box.html',
  resize: true,
  scrollbar: false,
  cancel: function (index, layero) { // 点击关闭时执行最小化
    layer.min(chat_box_index)
    return false
  },
  resizing: function (layero, index) {
    var body = layer.getChildFrame('body', index)
    console.log(body)
    // var iframeWin = window[layero.find('iframe')[0].name] // 得到iframe页的窗口对象，执行iframe页的方法：iframeWin.method();
    // console.log(body.html()) // 得到iframe页的body内容
    console.log(body.find('#toolbar').html())
    console.log(layero)
  }
})

function createWindow () {
  layer.open({
    type: 2,
    title: [
      '<img src="assets/images/lufei.jpeg"><div class="title-container"><div class="title-name">超超</div><span class="title-msg">对方正在输入...</span></div>',
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
