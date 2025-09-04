<template>
  <div>
    <el-card shadow="hover" class="main-container">
      <div class="menu-management-page">
        <!-- 页面标题与操作栏 -->
        <div class="page-header">
          <div class="header-actions">
            <el-input
              v-model="data.searchKeyword"
              placeholder="输入菜单名称/路由/Icon搜索"
              prefix-icon="Search"
              class="search-input"
              @input="handleSearch"
            />
            <el-button type="primary" icon="Plus" @click="handleAddPermission">新增菜单</el-button>
          </div>
        </div>

        <!-- 树形表格 -->
        <el-table
          :data="data.permissionList"
          row-key="id"
          border
          lazy
          :load="loadChildren"
          :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
          :indent="12"
          style="width: 100%"
          :cell-style="{ fontSize: data.tdFontSize }"
        >
          <el-table-column label="名称" min-width="120">
            <template #default="scope">
              <el-icon v-if="getIconComponent(scope.row.meta.icon)" class="menu-icon">
                <component :is="getIconComponent(scope.row.meta.icon)" />
              </el-icon>
              {{ scope.row.meta.title }}
            </template>
          </el-table-column>

          <el-table-column label="路由地址" prop="path" min-width="120" />
          <el-table-column label="视图文件路径" prop="component" min-width="120" />

          <el-table-column label="可见">
            <template #default="scope">
              <el-tag :type="scope.row.meta.isHide === 0 ? 'success' : 'warning'" size="small">
                {{ scope.row.meta.isHide === 0 ? "显示" : "隐藏" }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="类型">
            <template #default="scope">
              <el-tag :type="scope.row.component ? 'success' : 'info'" size="small">
                {{ scope.row.component ? "菜单" : "目录" }}
              </el-tag>
            </template>
          </el-table-column>
          <!-- 操作（子菜单仅显示自身可用操作） -->
          <el-table-column label="操作" width="240">
            <template #default="scope">
              <!-- 只有“目录”（无component）才能新增子菜单 -->
              <el-tooltip content="新增子菜单" placement="top">
                <template v-if="!scope.row.component">
                  <el-button
                    type="success"
                    icon="Plus"
                    size="small"
                    class="is-plain"
                    circle
                    @click="handleAddSubPermission(scope.row)"
                  ></el-button>
                </template>
                <template v-else>
                  <span></span>
                </template>
              </el-tooltip>

              <el-tooltip content="编辑" placement="top">
                <el-button
                  type="primary"
                  icon="Edit"
                  size="small"
                  class="is-plain"
                  circle
                  @click="showEditPermissionDialog(scope.row)"
                ></el-button>
              </el-tooltip>

              <el-tooltip content="删除" placement="top">
                <el-button
                  type="danger"
                  icon="Delete"
                  size="small"
                  class="is-plain"
                  circle
                  @click="handleDeletePermission(scope.row.id)"
                  :disabled="scope.row.hasChildren"
                ></el-button>
              </el-tooltip>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>

    <!-- 编辑dialog -->
    <el-dialog v-model="data.dialogEditPermissionFormVisible" title="编辑菜单" width="480">
      <el-form :model="data.editPermissionForm" ref="editPermissionFormRef" :rules="data.editPermissionFormRules">
        <el-form-item label="菜单名称" prop="title" :label-width="data.formLabelWidth">
          <el-input v-model="data.editPermissionForm.title" clearable />
        </el-form-item>
        <el-form-item label="路由name" prop="name" :label-width="data.formLabelWidth">
          <el-input v-model="data.editPermissionForm.name" clearable />
        </el-form-item>
        <el-form-item label="路由地址" prop="path" :label-width="data.formLabelWidth">
          <el-input v-model="data.editPermissionForm.path" clearable />
        </el-form-item>
        <el-form-item label="视图文件路径" prop="component" :label-width="data.formLabelWidth">
          <el-input v-model="data.editPermissionForm.component" clearable />
        </el-form-item>
        <el-form-item label="重定向地址" prop="redirect" :label-width="data.formLabelWidth">
          <el-input v-model="data.editPermissionForm.redirect" readonly clearable />
        </el-form-item>
        <el-form-item label="外链地址" prop="isLink" :label-width="data.formLabelWidth">
          <el-input v-model="data.editPermissionForm.isLink" clearable />
        </el-form-item>
        <el-form-item label="排序序号" prop="sortOrder" :label-width="data.formLabelWidth">
          <el-input v-model.number="data.editPermissionForm.sortOrder" type="number" />
        </el-form-item>
        <el-form-item label="图标" prop="icon" :label-width="data.formLabelWidth">
          <el-col :span="11">
            <el-input
              v-model="data.editPermissionForm.icon"
              placeholder="请选择图标"
              readonly
              @click="data.dialogIconFormVisible = true"
            />
          </el-col>
          <el-col :span="2" class="ml8">
            <div v-if="data.editPermissionForm.icon" style="margin-top: 8px">
              <el-icon size="large">
                <component :is="data.editPermissionForm.icon" />
              </el-icon>
            </div>
          </el-col>
        </el-form-item>
        <el-form-item label="是否隐藏" prop="isHide" :label-width="data.formLabelWidth">
          <el-radio-group v-model="data.editPermissionForm.isHide">
            <el-radio :value="false" size="small">否</el-radio>
            <el-radio :value="true" size="small">是</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="是否全屏" prop="isFull" :label-width="data.formLabelWidth">
          <el-radio-group v-model="data.editPermissionForm.isFull">
            <el-radio :value="false" size="small">否</el-radio>
            <el-radio :value="true" size="small">是</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="是否标签固定" prop="isAffix" :label-width="data.formLabelWidth">
          <el-radio-group v-model="data.editPermissionForm.isAffix">
            <el-radio :value="false" size="small">否</el-radio>
            <el-radio :value="true" size="small">是</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="是否路由缓存" prop="isKeepAlive" :label-width="data.formLabelWidth">
          <el-radio-group v-model="data.editPermissionForm.isKeepAlive">
            <el-radio :value="false" size="small">否</el-radio>
            <el-radio :value="true" size="small">是</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="data.dialogEditPermissionFormVisible = false">取消</el-button>
          <el-button type="primary" @click="editPermissionSubmit">提交</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- icon图标dialog -->
    <el-dialog title="选择图标" v-model="data.dialogIconFormVisible" width="40%">
      <el-input v-model="data.iconSearch" placeholder="搜索图标..." clearable prefix-icon="Search" class="mb12" />

      <div class="icon-scroll-container">
        <div
          v-for="(icon, index) in filteredIcons"
          :key="index"
          :class="{ selected: data.editPermissionForm.icon === icon.name }"
          @click="selectIcon(icon.name)"
          class="icon-grid-item"
        >
          <el-icon :size="24">
            <component :is="icon.component" />
          </el-icon>
          <span class="icon-name">{{ icon.name }}</span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed } from "vue";
import * as ElIcons from "@element-plus/icons-vue";
import http from "@/api/index.js";
import { ElMessage } from "element-plus";
import * as ElementPlusIconsVue from "@element-plus/icons-vue";

const editPermissionFormRef = ref(null);

// 变量声明
const data = reactive({
  // 常量
  formLabelWidth: "100px",
  tdFontSize: "12px",

  // 查询条件
  searchKeyword: "",
  iconSearch: "",

  // 菜单数据
  permissionList: [],

  // dialog
  dialogIconFormVisible: false,
  dialogEditPermissionFormVisible: false,

  // 表单相关
  editPermissionForm: {},

  // 表单规则
  editPermissionFormRules: {
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
  }
});

// 所有icon图标
const icons = Object.keys(ElementPlusIconsVue).map(key => ({
  name: key,
  component: ElementPlusIconsVue[key]
}));

// 过滤加载所有icon图标
const filteredIcons = computed(() => {
  if (!data.iconSearch) return icons;
  const keyword = data.iconSearch.toLowerCase();
  return icons.filter(icon => icon.name.toLowerCase().includes(keyword));
});

// 选择icon，并关闭icon dialog
const selectIcon = iconName => {
  data.editPermissionForm.icon = iconName;
  data.dialogIconFormVisible = false;
  data.iconSearch = "";
};

// 根据 icon 名获取组件
const getIconComponent = iconName => {
  return iconName ? ElIcons[iconName] : null;
};

// 加载子节点（懒加载模式）
const loadChildren = (row, treeNode, resolve) => {
  if (!row) {
    // 根节点
    const roots = data.permissionList.filter(item => !item.parentId);
    roots.forEach(r => {
      r.hasChildren = data.permissionList.some(c => c.parentId === r.id);
      r._level = 0;
    });
    resolve(roots);
  } else {
    const children = data.permissionList
      .filter(item => item.parentId === row.id)
      .map(c => {
        c.hasChildren = data.permissionList.some(ch => ch.parentId === c.id);
        c._level = (row._level || 0) + 1;
        return c;
      });
    resolve(children);
  }
};

const load = async () => {
  const resp = await http.post("/api/permission/tree");
  if (resp.code === "00000") {
    data.permissionList = resp.data.map(item => ({
      ...item,
      hasChildren: false, // 先默认 false，懒加载时计算
      _level: 0 // 根节点默认层级 0
    }));
  }
};

// 初始化菜单数据
onMounted(async () => {
  await load();
});

// 搜索功能
const handleSearch = () => {
  if (!data.searchKeyword.value) return;
  const keyword = data.searchKeyword.value.toLowerCase();
  data.permissionList = data.permissionList.filter(
    item =>
      (item.meta.title && item.meta.title.toLowerCase().includes(keyword)) ||
      (item.path && item.path.toLowerCase().includes(keyword)) ||
      (item.meta.icon && item.meta.icon.toLowerCase().includes(keyword))
  );
};

// 新增顶级菜单示例
const handleAddPermission = () => {
  alert("新增菜单功能示例，可以接入表单逻辑");
};

// 新增子菜单
const handleAddSubPermission = () => {
  console.log("新增子菜单");
};

// 编辑菜单
const showEditPermissionDialog = async permission => {
  console.log("showEditPermissionDialog permission:", permission);
  // 表单重置
  const resp = await http.post(`/api/permission/${permission.id}`);
  console.log("showEditPermissionDialog resp:", resp);
  if (resp.code === "00000") {
    data.editPermissionForm = resp.data;
    // 显示模态窗口
    data.dialogEditPermissionFormVisible = true;
  } else {
    ElMessage.error(resp.msg);
  }
};

// 编辑菜单，提交保存
const editPermissionSubmit = async () => {
  console.log("editPermissionSubmit form:", data.editPermissionForm);
  const resp = await http.post("/api/permission/save", data.editPermissionForm);
  console.log("编辑菜单，提交保存响应结果 resp:", resp);
  if (resp.code === "00000") {
    ElMessage.success("编辑用户成功");
    data.dialogEditPermissionFormVisible = false;
    console.log("编辑菜单，提交保存成功");
    await load();
  } else {
    ElMessage.error(resp.msg);
  }
};

// 删除菜单
const handleDeletePermission = () => {
  console.log("删除菜单");
};
</script>

<style scoped>
.menu-management-page {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 64px);
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.header-actions {
  display: flex;
  gap: 16px;
}
.search-input {
  width: 360px;
  min-width: 240px;
}
.menu-icon {
  display: inline-flex;
  align-items: center;
  vertical-align: middle;
  margin-right: 5px;
}

.icon-scroll-container {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 3px;
  padding: 1px 0;
  overflow-y: auto;
  overflow-x: hidden;
  max-height: 420px;
}

.icon-grid-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  margin-bottom: 12px;
  text-align: center;
  transition: all 0.2s;
  min-width: 80px;
  min-height: 40px;

  .icon-name {
    font-size: 10px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
  }
}
</style>
