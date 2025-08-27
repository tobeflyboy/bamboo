import http from "@/api";

// 获取用户列表
export const getUserList = params => {
  return http.post("/api/user/list", params);
};

// 获取树形用户列表
export const getUserTreeList = params => {
  return http.post("api/user/tree/list", params);
};

// 新增用户
export const addUser = params => {
  return http.post("api/user/add", params);
};

// 批量添加用户
export const BatchAddUser = params => {
  return http.post("api/user/import", params);
};

// 编辑用户
export const editUser = params => {
  return http.post("api/user/edit", params);
};

// 删除用户
export const deleteUser = params => {
  return http.post("api/user/delete", params);
};

// 切换用户状态
export const changeUserStatus = async params => {
  return await http.post("api/user/change", params);
};

// 重置用户密码
export const resetUserPassWord = params => {
  return http.post("api/user/rest_password", params);
};

// 导出用户数据
export const exportUserInfo = params => {
  return http.download("api/user/export", params);
};

// 获取用户状态字典
export const getUserStatus = async () => {
  const rest = await http.post("/api/user/status-enum", {});
  return Object.entries(rest.data).map(([value, label]) => ({
    key: label.toString(), // label 是 '无效'、'有效'
    value: value.toString() // value 是 '0'、'1'
  }));
};

// 获取用户性别字典
export const getUserGender = async () => {
  return await http.get("api/user/gender");
};

// 获取用户部门列表
export const getUserDepartment = () => {
  return http.get("api/user/department", {}, { cancel: false });
};

// 获取用户角色字典
export const getUserRole = () => {
  return http.get("api/user/role");
};
