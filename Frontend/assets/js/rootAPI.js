var rootAPI = (() => {
  var host = window.location.protocol + '//' + window.location.hostname + ':18320'
  return {
    host: host,
    webSocket: 'ws://' + window.location.hostname + ':18320/api/msg'
  }
})()
