
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
  var content = $('#chat-text').val()
  parent.websocket.send({
    toID: window.totalUserID,
    typeCode: 2,
    content: content
  })
  parent.actionMessagePool.addMessage(window.totalUserID, {
    action: 'PRIVATE_CHAT_MESSAGE',
    content: content,
    toID: window.totalUserID
  })
  uiMethod.addRightMsg(content)
  $('#content').scrollTop($('#content').prop('scrollHeight'))
  $('#chat-text').val('')
})

function receive (message) {
  console.log(message)
  uiMethod.addLeftMsg(message.content)
  $('#content').scrollTop($('#content').prop('scrollHeight'))
  $.ajax({
    type: 'POST',
    url: api.readMessage,
    dataType: 'json',
    data: { ids: message.messageID },
    xhrFields: {
      withCredentials: true
    }
  })
}

function ready () {
  console.log(window.totalUserID)
  var messages = parent.actionMessagePool.getMessage(window.totalUserID)
  console.log(messages)
  var messageids = []
  var messagearr = []
  messages.forEach(function (message, index) {
    if (message.action === 'PRIVATE_CHAT_MESSAGE') {
      if (message.toID === window.totalUserID) {
        messagearr.push({ type: 'right', content: message.content, time: message.time })
      } else {
        messagearr.push({ type: 'left', content: message.content, time: message.time })
        if (!message.isRead) {
          messageids.push(message.messageID)
        }
      }
    }
  })
  messagearr.sort(function (a, b) {
    if (a.time > b.time) {
      return 1
    }
    if (a.time < b.time) {
      return -1
    }
    return 0
  })
  messagearr.forEach(function (item) {
    if (item.type === 'left') {
      uiMethod.addLeftMsg(item.content)
    } else {
      uiMethod.addRightMsg(item.content)
    }
  })
  $('#content').scrollTop($('#content').prop('scrollHeight'))
  $.ajax({
    type: 'POST',
    url: api.readMessage,
    dataType: 'json',
    data: { ids: messageids.join(';') },
    xhrFields: {
      withCredentials: true
    }
  })
  parent.getMainBoxWindow().uiMethod.MessageDot.hideUser(window.totalUserID)
}
