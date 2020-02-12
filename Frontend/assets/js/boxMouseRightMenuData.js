function getBlankData () {
  return [
    { type: 1, title: '刷新列表' },
    { type: 2, title: '新建分组' }
  ]
}

function getGroupData (groupID) {
  return [
    { data: groupID, type: 1, title: group.isExpanded(groupID) ? '缩起' : '展开' },
    { data: groupID, type: 2, title: '重命名' },
    { data: groupID, type: 3, title: '删除分组' }
  ]
}

function getUserData (userID) {
  return [
    { data: userID, type: 1, title: '打开会话' },
    { data: userID, type: 2, title: '删除好友' }
  ]
}
