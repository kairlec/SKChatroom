function getBlankData () {
  return [
    { type: 1, title: '刷新列表' },
    { type: 2, title: '新建分组' }
  ]
}

function getGroupData (groupData) {
  return [
    { data: groupData, type: 1, title: uiMethod.GroupPand.isExpanded(groupData.id) ? '缩起' : '展开' },
    { data: groupData, type: 2, title: '重命名' },
    { data: groupData, type: 3, title: '删除分组' }
  ]
}

function getUserData (userID) {
  return [
    { data: userID, type: 1, title: '打开会话' },
    { data: userID, type: 2, title: '删除好友' },
    { data: userID, type: 3, title: '查看资料' }
  ]
}
