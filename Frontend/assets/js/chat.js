
var $ = layui.$
var uiMethod = {
  addLeftMsg: function (message) {
    $('#content').append(
      $('<div/>').attr({ class: 'sender' }).html(
        $('<div/>').html(
          $('<img/>').attr({ src: 'assets/images/1.png' }))).append(
        $('<div/>').attr({ class: 'msgContent' }).html(
          $('<div/>').attr({ class: 'left_triangle' })).append(
          $('<span/>').text(message))))
  },
  addRightMsg: function (message) {
    $('#content').append(
      $('<div/>').attr({ class: 'receiver' }).html(
        $('<div/>').html(
          $('<img/>').attr({ src: 'assets/images/1.png' }))).append(
        $('<div/>').attr({ class: 'msgContent' }).html(
          $('<div/>').attr({ class: 'right_triangle' })).append(
          $('<span/>').text(message))))
  }
}

$('#closeBtn').on('click', function (enevt) {
  console.log(window.totalUserID)
  parent.ChatBoxMap.closeID(window.totalUserID)
})

$('#sendBtn').on('click', function (event) {
  var context = $('#chat-text').val()
  parent.websocket.send({
    toID: window.totalUserID,
    typeCode: 2,
    content: context
  })
  parent.actionMessagePool.addMessage(window.totalUserID, {
    action: 'PRIVATE_CHAT_MESSAGE',
    context: context,
    toID: window.totalUserID
  })
  uiMethod.addRightMsg(context)
  $('#chat-text').val('')
})

function receive (message) {
  console.log(message)
  uiMethod.addLeftMsg(message.context)
}

function ready () {
  console.log(window.totalUserID)
  var messages = parent.actionMessagePool.getMessage(window.totalUserID)
  console.log(messages)
  $.each(messages, function (index, item) {
    if (item.action === 'PRIVATE_CHAT_MESSAGE') {
      if (item.toID === window.totalUserID) {
        uiMethod.addRightMsg(item.context)
      } else {
        uiMethod.addLeftMsg(item.context)
        $.ajax({
          type: 'POST',
          url: api.readMessage + item.messageID,
          dataType: 'json',
          xhrFields: {
            withCredentials: true
          }
        })
      }
    }
  })
}
