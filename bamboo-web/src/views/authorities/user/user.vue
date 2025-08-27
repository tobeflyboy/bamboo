<template>
  <div>
    <!-- shadow 属性设置卡片阴影出现的时机。 该属性的值可以是：always、hover、never。默认值：always，el-card边框呼吸灯效果-->
    <el-card shadow="never">
      <!-- 第一块：查询条件区域 -->
      <!-- gutter 属性用于设置行内栅格（el-col）之间的间距。-->
      <el-row :gutter="20" class="mb20">
        <el-col :span="4">
          <el-input v-model.trim="data.username" placeholder="账号，精准查询" clearable @keyup.enter="search">
            <template #prefix>
              <el-icon>
                <Search />
              </el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-input v-model.trim="data.realName" placeholder="姓名，模糊查询" clearable @keyup.enter="search">
            <template #prefix>
              <el-icon>
                <Search />
              </el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-select v-model="data.status" placeholder="用户状态" clearable style="width: 100%">
            <el-option v-for="(value, key) in data.userStatusEnum" :key="key" :label="value" :value="key" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" icon="Search" @click="search">搜索</el-button>
          <el-button icon="CircleClose" @click="resetSearch">重置</el-button>
        </el-col>
      </el-row>

      <!-- 第二块：操作工具栏 -->
      <el-space class="mb20">
        <el-button type="success" icon="Plus" @click="showAddUserDialog">新增用户</el-button>
        <el-button type="primary" class="is-plain" icon="Download" @click="showBatchExportUser">批量导出</el-button>

        <!-- 使用自定义上传 -->
        <el-upload :show-file-list="false" :http-request="batchImportUser" accept=".xlsx,.xls">
          <el-button type="primary" class="is-plain" icon="Upload">批量导入</el-button>
        </el-upload>
      </el-space>

      <!-- 第三块：数据表格 -->
      <el-table
        :data="data.userList"
        class="mb20"
        :cell-style="{ fontSize: data.tdFontSize }"
        :header-cell-style="{ color: '#303133', backgroundColor: '#EBEEF5' }"
      >
        <el-table-column prop="username" label="账号" fixed="left" />
        <el-table-column prop="realName" label="姓名" />
        <el-table-column prop="email" label="邮箱">
          <template #default="{ row }">
            <el-link
              type="primary"
              :href="`mailto:${row.email}`"
              :underline="false"
              :style="{
                fontSize: data.tdFontSize,
                display: 'inline-block',
                lineHeight: '1.2'
              }"
              v-if="row.email"
            >
              {{ row.email }}
            </el-link>
            <span v-else :style="`font-size: ${data.tdFontSize}`">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="statusDesc" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" :style="{ fontSize: data.tdFontSize }" effect="dark" round>
              {{ row.statusDesc }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="roleName" label="所属角色" />
        <el-table-column prop="createTime" label="创建时间" :formatter="formatTime" />
        <el-table-column prop="createUserRealName" label="创建人" />
        <el-table-column prop="updateTime" label="更新时间" :formatter="formatTime" />
        <el-table-column prop="updateUserRealName" label="更新人" />
        <el-table-column label="操作" fixed="right">
          <template #default="scope">
            <el-space>
              <el-tooltip content="编辑用户" placement="top">
                <el-button size="small" type="primary" icon="Edit" circle @click="showEditUserDialog(scope.row)" />
              </el-tooltip>
              <el-tooltip content="重置密码" placement="top">
                <el-button size="small" type="warning" icon="Key" circle @click="showResetPasswordDialog(scope.row)" />
              </el-tooltip>
              <el-tooltip content="删除用户" placement="top">
                <el-button size="small" type="danger" icon="Delete" circle @click="showDeleteUserDialog(scope.row)" />
              </el-tooltip>
            </el-space>
          </template>
        </el-table-column>
      </el-table>

      <!-- 第四块：分页控件 -->
      <!-- 分页控件
       v-model:current-page 双向绑定当前页码
       v-model:page-size 双向绑定每页显示条数
       :page-sizes 可选的每页条数选项
       :total 总数据条数
       :layout 分页组件布局
       background 使用背景色
       @size-change 当每页显示条数改变时触发
       @current-change 当当前页码改变时触发
       -->
      <el-pagination
        v-model:current-page="data.pageNum"
        v-model:page-size="data.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="data.total"
        layout="total, sizes, prev, pager, next, jumper"
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- 新增用户dialog -->
    <el-dialog v-model="data.dialogAddUserFormVisible" title="新增用户" width="480">
      <el-form :model="data.addUserForm" autocomplete="off" ref="addUserFormRef" :rules="data.addUserFormRules">
        <el-form-item label="账号" prop="username" :label-width="data.formLabelWidth">
          <el-input v-model.trim="data.addUserForm.username" placeholder="请输入6-12位的英文字母或字符" clearable />
        </el-form-item>
        <el-form-item label="姓名" prop="realName" :label-width="data.formLabelWidth">
          <!-- autocomplete="new-realName" 为了阻止浏览器自动填充-->
          <el-input v-model.trim="data.addUserForm.realName" placeholder="请输入姓名" autocomplete="new-realName" clearable />
        </el-form-item>
        <el-form-item label="密码" prop="password" :label-width="data.formLabelWidth">
          <el-input
            v-model.trim="data.addUserForm.password"
            placeholder="请输入6-12位的英文字母或字符"
            type="password"
            autocomplete="new-password"
            clearable
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword" :label-width="data.formLabelWidth">
          <el-input
            v-model.trim="data.addUserForm.confirmPassword"
            placeholder="请再次输入密码"
            type="password"
            clearable
            show-password
          />
        </el-form-item>
        <el-form-item label="邮箱" prop="email" :label-width="data.formLabelWidth">
          <el-input v-model.trim="data.addUserForm.email" placeholder="请输入邮箱地址" clearable />
        </el-form-item>
        <el-form-item label="所属角色" prop="roleId" :label-width="data.formLabelWidth">
          <el-select v-model="data.addUserForm.roleId" placeholder="请选择角色">
            <el-option v-for="item in data.roleList" :key="item.id" :label="item.roleName" :value="item.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="data.dialogAddUserFormVisible = false">取消</el-button>
          <el-button type="primary" @click="addUserSubmit">提交</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 编辑用户dialog -->
    <el-dialog v-model="data.dialogEditUserFormVisible" title="编辑用户" width="480">
      <el-form :model="data.editUserForm" ref="editUserFormRef" :rules="data.editUserFormRules">
        <el-form-item label="账号" :label-width="data.formLabelWidth">
          <el-input v-model.trim="data.editUserForm.username" disabled />
        </el-form-item>
        <el-form-item label="姓名" prop="realName" :label-width="data.formLabelWidth">
          <el-input v-model.trim="data.editUserForm.realName" placeholder="请输入姓名" clearable />
        </el-form-item>
        <el-form-item label="邮箱" prop="email" :label-width="data.formLabelWidth">
          <el-input v-model.trim="data.editUserForm.email" placeholder="请输入邮箱地址" clearable />
        </el-form-item>
        <el-form-item label="状态" prop="status" :label-width="data.formLabelWidth">
          <el-select v-model="data.editUserForm.status" placeholder="请选择状态">
            <el-option v-for="(value, key) in data.userStatusEnum" :key="key" :label="value" :value="key" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属角色" prop="roleId" :label-width="data.formLabelWidth">
          <el-select v-model="data.editUserForm.roleId" placeholder="请选择角色">
            <el-option v-for="item in data.roleList" :key="item.id" :label="item.roleName" :value="item.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="data.dialogEditUserFormVisible = false">取消</el-button>
          <el-button type="primary" @click="editUserSubmit">提交</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 删除用户dialog -->
    <el-dialog v-model="data.dialogDeleteUserFormVisible" title="删除用户" width="480" align-center>
      <el-form :model="data.deleteUserForm">
        <el-form-item label="账号" :label-width="data.formLabelWidth">
          <el-input v-model.trim="data.deleteUserForm.username" disabled />
        </el-form-item>
        <el-form-item label="姓名" :label-width="data.formLabelWidth">
          <el-input v-model.trim="data.deleteUserForm.realName" disabled />
        </el-form-item>
        <el-form-item label="邮箱" :label-width="data.formLabelWidth">
          <el-input v-model.trim="data.deleteUserForm.email" disabled />
        </el-form-item>
        <el-form-item label="所属角色" :label-width="data.formLabelWidth">
          <el-select v-model="data.deleteUserForm.roleId" disabled>
            <el-option v-for="item in data.roleList" :key="item.id" :label="item.roleName" :value="item.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="data.dialogDeleteUserFormVisible = false">取消</el-button>
          <el-button type="danger" @click="deleteUserSubmit">确认删除</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 重置用户密码dialog -->
    <el-dialog v-model="data.dialogResetPasswordFormVisible" title="重置密码" width="480" align-center>
      <el-form :model="data.resetPasswordForm" autocomplete="off" ref="resetPasswordFormRef" :rules="data.resetPasswordFormRules">
        <el-form-item label="账号" :label-width="data.formLabelWidth">
          <el-input v-model.trim="data.resetPasswordForm.username" disabled />
        </el-form-item>
        <el-form-item label="姓名" :label-width="data.formLabelWidth">
          <el-input v-model.trim="data.resetPasswordForm.realName" disabled />
        </el-form-item>
        <el-form-item label="密码" prop="password" :label-width="data.formLabelWidth">
          <el-input
            v-model.trim="data.resetPasswordForm.password"
            placeholder="请输入6-12位的英文字母或字符"
            type="password"
            clearable
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword" :label-width="data.formLabelWidth">
          <el-input
            v-model.trim="data.resetPasswordForm.confirmPassword"
            placeholder="请再次输入密码"
            type="password"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="data.dialogResetPasswordFormVisible = false">取消</el-button>
          <el-button type="danger" @click="resetPasswordSubmit">重置</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="jsx" name="userList">
import { ref, reactive, onMounted, nextTick } from "vue";
import { ElLoading, ElMessage, ElMessageBox } from "element-plus";
import http from "@/api";
import dayjs from "dayjs";

// 日期格式化方法
const formatTime = (row, column, cellValue, index) => {
  // 处理 null、undefined、空字符串等
  if (!cellValue) {
    return "";
  }
  return cellValue ? dayjs(cellValue).format("YYYY-MM-DD HH:mm:ss") : "";
};

const addUserFormRef = ref(null);
const editUserFormRef = ref(null);
const resetPasswordFormRef = ref(null);

// 变量声明
const data = reactive({
  // 常量
  formLabelWidth: "90px",
  tdFontSize: "12px",

  // 搜索条件
  username: "",
  realName: "",
  status: "",

  // 状态枚举
  userStatusEnum: [],

  // 所有有效角色
  roleList: [],

  // 数据
  userList: [], // 查询的用户数据
  pageNum: 1, // 当前页码
  pageSize: 10, // 每页显示条目个数
  total: 0, // 总条目数

  // dialog
  dialogAddUserFormVisible: false,
  dialogEditUserFormVisible: false,
  dialogDeleteUserFormVisible: false,
  dialogResetPasswordFormVisible: false,

  // 表单相关
  addUserForm: {},
  editUserForm: {},
  deleteUserForm: {},
  resetPasswordForm: {},

  // 表单规则
  addUserFormRules: {
    username: [
      { required: true, message: "请输入账号名称", trigger: "blur" },
      { min: 6, max: 12, message: "长度在 6 到 12 个字符", trigger: "blur" }
    ],
    realName: [{ required: true, message: "请输入真实姓名", trigger: "blur" }],
    password: [
      { required: true, message: "请输入密码", trigger: "blur" },
      { min: 6, max: 12, message: "长度在 6 到 12 个字符", trigger: "blur" }
    ],
    confirmPassword: [
      { required: true, message: "请再次输入密码", trigger: "blur" },
      { min: 6, max: 12, message: "长度在 6 到 12 个字符", trigger: "blur" },
      {
        validator: (rule, value, callback) => {
          if (value !== data.addUserForm.password) {
            console.error("两次输入密码不一致!", value, data.addUserForm.password);
            callback(new Error("两次输入密码不一致!"));
          } else {
            callback();
          }
        },
        trigger: "blur"
      }
    ],
    email: [
      { required: true, message: "请输入邮箱地址", trigger: "blur" },
      { type: "email", message: "请输入正确的邮箱地址", trigger: "blur" }
    ],
    roleId: [{ required: true, message: "请选择角色", trigger: "change" }],
    status: [{ required: true, message: "请选择状态", trigger: "change" }]
  },
  editUserFormRules: {
    username: [
      { required: true, message: "请输入账号名称", trigger: "blur" },
      { min: 6, max: 12, message: "长度在 6 到 12 个字符", trigger: "blur" }
    ],
    realName: [{ required: true, message: "请输入真实姓名", trigger: "blur" }],
    email: [
      { required: true, message: "请输入邮箱地址", trigger: "blur" },
      { type: "email", message: "请输入正确的邮箱地址", trigger: "blur" }
    ],
    roleId: [{ required: true, message: "请选择角色", trigger: "change" }],
    status: [{ required: true, message: "请选择状态", trigger: "change" }]
  },
  resetPasswordFormRules: {
    password: [
      { required: true, message: "请输入密码", trigger: "blur" },
      { min: 6, max: 12, message: "长度在 6 到 12 个字符", trigger: "blur" }
    ],
    confirmPassword: [
      { required: true, message: "请再次输入密码", trigger: "blur" },
      { min: 6, max: 12, message: "长度在 6 到 12 个字符", trigger: "blur" },
      {
        validator: (rule, value, callback) => {
          if (value !== data.resetPasswordForm.password) {
            console.error("两次输入密码不一致!", value, data.resetPasswordForm.password);
            callback(new Error("两次输入密码不一致!"));
          } else {
            callback();
          }
        },
        trigger: "blur"
      }
    ]
  }
});

// 搜索用户
const search = () => {
  console.log("data:", data);
  data.pageNum = 1;
  load();
};

// 重置搜索条件，及发起搜索
const resetSearch = () => {
  data.username = "";
  data.realName = "";
  data.status = "";
  console.log("resetSearch:", data);
  search();
};

// 执行搜索用户
const load = async () => {
  let params = {
    pageNum: data.pageNum,
    pageSize: data.pageSize,
    username: data.username,
    realName: data.realName,
    status: data.status
  };
  console.log("/api/user/list params:", params);
  const resp = await http.post("/api/user/list", params);
  console.log("/api/user/list resp:", resp);
  data.userList = resp.data.list;
  data.total = resp.data.total;
};

// 组件挂载完成后，首次加载数据
onMounted(async () => {
  try {
    // 并行请求
    const [userStatusEnumResp, rolesResp] = await Promise.all([
      http.post("/api/user/status-enum"),
      http.post("/api/role/all-list")
    ]);
    data.userStatusEnum = userStatusEnumResp.data;
    data.roleList = rolesResp.data;
    console.log("userStatusEnum:", data.userStatusEnum);
    console.log("roleList:", data.roleList);
    // 查询用户数据
    await load();
  } catch (error) {
    console.error("初始化数据失败：", error);
    ElMessage.error("加载数据失败");
  }
});

// 显示批量导出用户模态窗口
const showBatchExportUser = async () => {
  console.log("showBatchExportUser");
  const params = {
    username: data.username,
    realName: data.realName,
    status: data.status
  };
  console.log("/api/user/export params:", params);
  const result = await http.downloadFile("/api/user/export", params, { loading: true });
  console.log("/api/user/export result:", result);
};

// 批量导入用户
const batchImportUser = async options => {
  console.log("批量导入用户 options:", options);
  const { file } = options;

  // 验证文件是否存在
  if (!file) {
    ElMessage.error("请选择要上传的文件");
    return;
  }

  try {
    const formData = new FormData();
    formData.append("file", file); // 正确附加文件对象

    const res = await http.post("/api/user/import", formData, {
      headers: {
        "Content-Type": "multipart/form-data" // 明确指定为表单数据类型
      },
      responseType: "json",
      loading: true // 显式控制加载状态
    });

    if (res.code === "00000") {
      ElMessage.success("导入成功");
      await load();
    } else {
      ElMessage.error(res.msg || "导入失败");
    }
  } catch (error) {
    ElMessage.error("导入失败: " + (error.message || "未知错误"));
  }
};

// 分页控件：当每页大小改变时
const handleSizeChange = newSize => {
  console.log("每页大小改变为:", newSize);
  // 关键：改变每页大小时，通常重置到第一页
  data.pageNum = 1;
  // v-model 会同步，但这里直接赋值更清晰
  load();
};

// 分页控件：当当前页码改变时
const handleCurrentChange = newPage => {
  console.log("当前页码改变为:", newPage);
  // 页码改变时，直接加载数据，不需要重置页码
  load();
};

// 显示新增用户模态窗口
const showAddUserDialog = () => {
  console.log("showAddUserDialog");
  // 表单重置
  data.addUserForm = {
    username: "",
    realName: "",
    password: "",
    email: "",
    roleId: ""
  };
  // 显示模态窗口
  data.dialogAddUserFormVisible = true;
  nextTick(() => {
    addUserFormRef.value.resetFields();
    console.log("addUserForm 表单已重置");
  });
};

// 新增用户，提交保存
const addUserSubmit = () => {
  console.log("addUserSubmit:");
  // 使用回调函数形式进行验证
  addUserFormRef.value.validate(async (valid, fields) => {
    if (valid) {
      console.log("表单验证通过:", data.addUserForm);
      const params = {
        username: data.addUserForm.username,
        realName: data.addUserForm.realName,
        password: data.addUserForm.password,
        email: data.addUserForm.email,
        roleId: data.addUserForm.roleId
      };
      console.log("创建用户:", params);
      const res = await http.post("/api/user/add", params);
      if (res.code === "00000") {
        ElMessage.success("新增用户成功");
        data.dialogAddUserFormVisible = false;
        await load();
      } else {
        ElMessage.error(res.msg);
      }
    }
  });
};

// 显示编辑用户模态窗口
const showEditUserDialog = async user => {
  console.log("showEditUserDialog user:", user);
  // 表单重置
  const resp = await http.post(`/api/user/detail/${user.userId}`);
  console.log("showEditUserDialog resp:", resp);
  if (resp.code === "00000") {
    data.editUserForm = resp.data;
    data.editUserForm.status = resp.data.status.toString();
    // 显示模态窗口
    data.dialogEditUserFormVisible = true;
  } else {
    ElMessage.error(resp.msg);
  }
};

// 编辑用户，提交保存
const editUserSubmit = () => {
  console.log("editUserSubmit");
  // 使用独立的表单引用
  editUserFormRef.value.validate(async (valid, fields) => {
    if (valid) {
      console.log("表单验证通过:", data.editUserForm);
      const params = {
        userId: data.editUserForm.userId,
        realName: data.editUserForm.realName,
        status: data.editUserForm.status,
        email: data.editUserForm.email,
        roleId: data.editUserForm.roleId
      };
      console.log("/api/user/edit params:", params);
      const resp = await http.post("/api/user/edit", params);
      console.log("编辑用户，提交保存响应结果 resp:", resp);
      if (resp.code === "00000") {
        ElMessage.success("编辑用户成功");
        data.dialogEditUserFormVisible = false;
        console.log("编辑用户，提交保存成功");
        await load();
      } else {
        ElMessage.error(resp.msg);
      }
    }
  });
};

// 重置密码，显示模态窗口
const showResetPasswordDialog = user => {
  // 浅拷贝
  const userDetail = { ...user };
  console.log("showResetPasswordDialog user:", userDetail);
  data.resetPasswordForm = userDetail;
  data.resetPasswordForm.password = "";
  data.dialogResetPasswordFormVisible = true;
};

// 重置密码，确认保存
const resetPasswordSubmit = async () => {
  console.log("resetPasswordSubmit");
  resetPasswordFormRef.value.validate(async (valid, field) => {
    if (valid) {
      console.log("resetPasswordForm 表单验证通过");
      const params = {
        userId: data.resetPasswordForm.userId,
        newPassword: data.resetPasswordForm.password
      };
      console.log("/api/user/reset-pwd params:", params);
      const resp = await http.post("/api/user/reset-pwd", params);
      if (resp.code === "00000") {
        ElMessage.success("密码重置成功");
        data.dialogResetPasswordFormVisible = false;
      }
    }
  });
};

// 删除用户，显示模态窗口
const showDeleteUserDialog = user => {
  console.log("showDeleteUserDialog user:", user);
  data.deleteUserForm = user;
  data.dialogDeleteUserFormVisible = true;
};

// 删除用户，确认删除
const deleteUserSubmit = async () => {
  let userId = data.deleteUserForm.userId;
  console.log("deleteUser:", userId);
  const res = await http.post(`/api/user/delete/${userId}`);
  data.dialogDeleteUserFormVisible = false;
  if (res.code === "00000") {
    ElMessage.success("删除用户成功");
    await load();
  } else {
    ElMessage.error(res.msg);
  }
};
</script>
