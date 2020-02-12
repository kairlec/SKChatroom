var $ = layui.$

var group = {
  groupData: [],
  newGroup: function (groupID, groupName) {
    return (
      $('<div/>').attr({ class: 'layui-colla-item' }).html(
        $('<h2/>').attr({ class: 'layui-colla-title' }).html(
          $('<i/>').attr({ class: 'layui-icon layui-icon-right' })).append(groupName)).append(
        $('<div/>').attr({ class: 'layui-colla-content group', id: 'g' + groupID }))
    )
  },
  addFriendInGroup: function (friend, groupID) {
    $('#g' + groupID).append(friend)
  },
  isExpanded: function (groupID) {
    return $('#g' + groupID).hasClass('layui-show')
  },
  expand: function (groupID) {
    var theContent = $('#g' + groupID)
    if (!theContent.hasClass('layui-show')) {
      theContent.addClass('layui-show')
    }
  },
  unexpand: function (groupID) {
    var theContent = $('#g' + groupID)
    if (!theContent.hasClass('layui-show')) {
      theContent.removeClass('layui-show')
    }
  },
  toggleExpanded: function (groupID) {
    if (this.isExpanded(groupID)) {
      this.Unexpand(groupID)
    } else {
      this.Expand(groupID)
    }
  },
  unexpandAll: function () {
    $('.group').each(function () {
      var item = $(this)
      if (item.hasClass('layui-show')) {
        item.removeClass('layui-show')
      }
    })
  },
  expandAll: function () {
    $('.group').each(function () {
      var item = $(this)
      if (!item.hasClass('layui-show')) {
        item.addClass('layui-show')
      }
    })
  },
  toggleExpandedAll: function () {
    $('.group').each(function () {
      var item = $(this)
      if (item.hasClass('layui-show')) {
        item.removeClass('layui-show')
      } else {
        item.addClass('layui-show')
      }
    })
  }
}

$(() => {
  /* 屏蔽浏览器右键 */
  document.oncontextmenu = () => {
    parent.closeMenu()
    return false
  }
  $('#group').on('click', '.layui-colla-title', function () {
    var theContent = $(this).next('.layui-colla-content')
    if (theContent.hasClass('layui-show')) {
      theContent.removeClass('layui-show')
    } else {
      theContent.addClass('layui-show')
    }
  })
  $('.box-btn-list-li').on('click', function () {
    var index = $(this).index()
    $('.box-btn-content').removeClass('layui-show').eq(index).addClass('layui-show')
  })
  // 在父页面打开聊天窗口
  $('#group').on('click', '.box-my-list', function () {
    parent.createWindow($(this).attr('id'))
  })

  $('#group').on('contextmenu', '.layui-colla-item', function (event) {
    parent.menuIndex = parent.mouseRightMenu.open(getGroupData($(this).children('.group').attr('id').substr(1)), { offset: parent.getBoxMouseXY(event.pageX, event.pageY) }, function (data) {
      layer.alert(JSON.stringify(data))
    })
    return false
  })

  addGroup(group.newGroup(123, '哈哈'))
  addGroup(group.newGroup(456, '哈哈哈哈'))
  group.addFriendInGroup(newFriend(), 123)
  group.addFriendInGroup(newFriend(), 456)
})

function swap () { }

function addGroup (group) {
  $('#group').append(group)
}

function newFriend (data) {
  return (
    $('<div/>').attr({ class: 'layui-row box-my-list user', id: 'f' + '' }).html(
      $('<div/>').attr({ class: 'layui-col-xs2' }).html(
        $('<div/>').attr({ class: 'layui-row' }).html(
          $('<div/>').attr({ class: 'layui-col-xs12' }).html(
            $('<img/>').attr({ class: 'box-my-pic', /* 头像链接 */ src: 'assets/images/1.png' }))))).append(
      $('<div/>').attr({ class: 'layui-col-xs10' }).html(
        $('<div/>').attr({ class: 'layui-row' }).html(
          $('<div/>').attr({ class: 'layui-col-xs12 box-my-name' }).html('沙雕好友1')).append(
          $('<div/>').attr({ class: 'layui-col-xs12 box-my-signal box-line-1' }).html('个性签名'))))
  )
}

// 获取验证码
function getAvatar (id, jqImg) {
  $.ajax({
    type: 'POST',
    url: api + '/get/resource/Avatar/' + id,
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
