var $ = layui.$
$(() => {
  $('.layui-colla-title').click(function () {
    var theContent = $(this).next('.layui-colla-content')
    if (theContent.hasClass('layui-show')) {
      theContent.removeClass('layui-show')
    } else {
      theContent.addClass('layui-show')
    }
  })
  $('.box-btn-list-li').click(function () {
    var index = $(this).index()
    $('.box-btn-content').removeClass('layui-show').eq(index).addClass('layui-show')
  })
  // 在父页面打开聊天窗口
  // console.log(parent.layer)

  $('.box-my-list').click(function () {
    console.log(window)
    console.log(parent)
    parent.createWindow()
  })
})
