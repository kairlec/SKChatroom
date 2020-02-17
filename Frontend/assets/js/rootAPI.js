var rootAPI = (() => {
  var host = window.location.protocol + '//' + window.location.hostname + ':8320'
  return {
    host: host,
    webSocket: 'ws://' + window.location.hostname + ':8320/api/msg'
  }
})()
