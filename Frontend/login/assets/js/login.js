var needCaptcha = false

function getFatherPath () {
  var url
  if (document.location.href.endsWith('/index.html')) {
    url = document.location.href.substr(0, document.location.href.length - 11)
  } else {
    url = document.location.href
  }
  var index = url.substr(1).lastIndexOf('/')
  var result = url.substr(0, index + 1)
  return result
}

function wait (msg) {
  layer.msg(msg, {
    icon: 16,
    shade: 0.3,
    time: 0
  })
}

function endWait () {
  layer.close(layer.msg())
}

function responseWrapper (data, callback) {
  if (data.code !== 0) {
    if (data.code !== 30009) {
      layer.msg('错误:' + data.message)
    }
  } else {
    callback(data.data)
  }
}

function showCaptcha () {
  needCaptcha = true
  $('#captcha').show(200)
  $('#captcha_input').attr('lay-verify', 'required|empty')
}

function hideCaptcha () {
  needCaptcha = false
  $('#captcha').hide(200)
  $('#captcha_input').removeAttr('lay-verify')
}

function ajaxError (jqXHR, textStatus, errorThrown) {
  endWait()
  if (errorThrown === null || errorThrown.length === 0) {
    layer.msg('错误:' + jqXHR.statusText)
  } else {
    layer.msg('错误:' + errorThrown)
  }
}

function afterEncode (formData, callback, msg) {
  wait(msg)
  $.ajax({
    type: 'POST',
    dataType: 'json',
    url: api.pk,
    success: (data) => { responseWrapper(data, (data) => { callback(formData, data) }) },
    error: ajaxError
  })
}

function login (formData, publicKey) {
  var encrypt = new JSEncrypt()
  encrypt.setPublicKey(publicKey)
  formData.field.password = encrypt.encrypt(formData.field.password)
  if (!needCaptcha) {
    delete formData.field.captcha
  }
  $.ajax({
    type: 'POST',
    dataType: 'json',
    url: api.login,
    xhrFields: {
      withCredentials: true
    },
    data: formData.field,
    success: function (data) {
      endWait()
      if (data.code !== 0) {
        if (data.code === 30009 || data.code === 30010 || data.code === 30011) {
          if (data.code === 30010) {
            layer.msg('验证码错误')
          }
          getCaptcha()
          showCaptcha()
        } else {
          layer.msg('登录失败:' + data.message)
        }
      } else {
        window.location.href = '..'
      }
    },
    error: ajaxError
  })
}

function reg (formData, publicKey) {
  var encrypt = new JSEncrypt()
  encrypt.setPublicKey(publicKey)
  formData.field.password = encrypt.encrypt(formData.field.password)
  formData.field.domain = getFatherPath() + '/activate'
  $.ajax({
    type: 'POST',
    dataType: 'json',
    url: api.reg,
    data: formData.field,
    success: function (data) {
      endWait()
      if (data.code !== 0) {
        layer.msg('错误:' + data.message)
      } else {
        if (data.data === 'VERIFICATION_REQUIRED') {
          layer.msg('激活链接已发送到邮箱,请查看')
        } else if (data.data === 'NO_VERIFICATION_REQUIRED') {
          layer.msg('注册成功!')
        } else {
          layer.msg('已提交注册信息')
        }
      }
    },
    error: ajaxError
  })
}

function getCaptcha () {
  $.ajax({
    type: 'POST',
    url: api.getCaptcha,
    xhrFields: {
      withCredentials: true
    },
    xhr: function () {
      var xhr = new XMLHttpRequest()
      xhr.responseType = 'blob'
      return xhr
    },
    success: function (data) {
      $('#captcha_img').attr('src', window.URL.createObjectURL(data))
    },
    error: ajaxError
  })
}

function getTestCaptcha () {
  $.ajax({
    type: 'POST',
    url: api.getTestCaptcha,
    xhrFields: {
      withCredentials: true
    },
    xhr: function () {
      var xhr = new XMLHttpRequest()
      xhr.responseType = 'blob'
      return xhr
    },
    success: function (data) {
      $('#captcha_img').attr('src', window.URL.createObjectURL(data))
    },
    error: ajaxError
  })
}

function logout () {
  $.ajax({
    type: 'POST',
    url: api.logout,
    xhrFields: {
      withCredentials: true
    },
    success: function (data) {
      console.debug(data)
    },
    error: ajaxError
  })
}

function newCaptcha () {
  $.ajax({
    type: 'POST',
    url: api.refreshCaptcha,
    xhrFields: {
      withCredentials: true
    },
    success: function (data) {
      getCaptcha()
    },
    error: ajaxError
  })
}

function toLoginTab () {
  $('button[lay-type="sub"]').click()
}

function toRegTab () {
  $('button[lay-type="add"]').click()
}

layui.use(['layer', 'form', 'carousel'], function () {
  var layer = layui.layer
  var form = layui.form
  var carousel = layui.carousel
  carousel.render({
    elem: '#tab',
    arrow: 'none',
    indicator: 'none',
    autoplay: false,
    width: '480px',
    height: '400px'
  })
  form.verify({
    username: [/^[a-zA-Z0-9]{0,20}$/, '用户名不能含有非法字符且最多20位'],
    pass: [/^[\S]{6,16}$/, '密码必须6到16位，且不能出现空格'],
    empty: function (value, item) {
      if (value.trim().length === 0) {
        return '请填写此字段'
      }
    }
  })
  form.on('submit(login)', (formData) => {
    afterEncode(formData, login, '登录中,请稍后...')
  })
  form.on('submit(reg)', function (formData) {
    afterEncode(formData, reg, '注册中,请稍后...')
  })
})
$(function () {
  $('#captcha_img').on('click', newCaptcha)
  $('#reg').on('click', toRegTab)
  $('#login').on('click', toLoginTab)
  $.ajax({
    type: 'POST',
    url: api.loginStatus,
    xhrFields: {
      withCredentials: true
    },
    success: function (data) {
      if (data.code === 0) {
        window.location.href = '..'
      }
    },
    error: ajaxError
  })
})
