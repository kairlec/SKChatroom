function longSock (url, fn, fnResponse, intro = '') {
  let lockReconnect = false // 避免重复连接
  let timeoutFlag = true
  let timeoutSet = null
  let reconectNum = 0
  const timeout = 30000 // 超时重连间隔
  let ws
  const sleep = (ms) => {
    return new Promise(resolve => setTimeout(resolve, ms))
  }
  function reconnect () {
    if (lockReconnect) return
    lockReconnect = true
    // 没连接上会一直重连，设置延迟避免请求过多
    if (reconectNum < 3) {
      setTimeout(function () {
        timeoutFlag = true
        createWebSocket()
        console.info(`${intro}正在重连第${reconectNum + 1}次`)
        reconectNum++
        lockReconnect = false
      }, 5000) // 这里设置重连间隔(ms)
    }
  }
  // 心跳检测
  const heartCheck = {
    timeout: 5000, // 毫秒
    timeoutObj: null,
    serverTimeoutObj: null,
    reset: function () {
      clearInterval(this.timeoutObj)
      clearTimeout(this.serverTimeoutObj)
      return this
    },
    start: function () {
      const self = this
      let count = 0
      this.timeoutObj = setInterval(() => {
        if (count < 3) {
          if (ws.readyState === 1) {
            sendMessage(null, 'HeartBeat')
            // console.info(`${intro}HeartBeat第${count + 1}次`)
          }
          count++
        } else {
          clearInterval(this.timeoutObj)
          count = 0
          if (ws.readyState === 0 && ws.readyState === 1) {
            ws.close()
          }
        }
      }, self.timeout)
    }
  }
  const createWebSocket = () => {
    console.info(`${intro}创建`)
    timeoutSet = setTimeout(() => {
      if (timeoutFlag && reconectNum < 3) {
        console.info(`${intro}重连`)
        reconectNum++
        createWebSocket()
      }
    }, timeout)
    ws = new WebSocket(url)
    ws.onopen = () => {
      reconectNum = 0
      timeoutFlag = false
      clearTimeout(timeoutSet)
      heartCheck.reset().start()
    }
    ws.onmessage = evt => {
      heartCheck.reset().start()
      var json = JSON.parse(evt.data)
      if (json.type === 'HeartBeat') return
      if (json.type === 'Response') {
        fnResponse(json, ws)
        return
      }
      fn(json.data, ws)
    }
    ws.onclose = e => {
      console.info(`${intro}关闭`, e.code)
      if (e.code !== 1000) {
        timeoutFlag = false
        clearTimeout(timeoutSet)
        reconnect()// 被异常关闭,重连
      } else {
        clearInterval(heartCheck.timeoutObj)
        clearTimeout(heartCheck.serverTimeoutObj)
      }
    }
    ws.onerror = function () {
      console.info(`${intro}错误`)
      reconnect() // 重连
    }
  }
  const sendMessage = (data, type) => {
    ws.send(JSON.stringify({ data: data, type: type }))
  }
  createWebSocket()
  return {
    ws: ws,
    send: async function (messageObject, fnSuccess, fnError) {
      console.log('ready to send a message')
      console.log(messageObject)
      var submit = new Date()
      // 未连接上,等待连接完毕
      while (ws.readyState !== ws.OPEN) {
        if (new Date().getTime() - submit.getTime() > 5000) {
          !fnError || fnError(messageObject)
          return
        }
        await sleep(1)
      }
      messageObject.timestamp = new Date().getTime().toString()
      sendMessage(messageObject, 'Message')
      !fnSuccess || fnSuccess(messageObject)
    }
  }
}

const handler = (data, ws) => {
  console.log(data)
  var dataObject = JSON.parse(data)
  actionMessagePool.addMessage(dataObject.fromID, dataObject)
  ChatBoxMap.sendMessage(dataObject.fromID, dataObject)
  getMainBoxWindow().receive(dataObject)
}

var websocket = longSock(rootAPI.webSocket, handler, 'Main')
