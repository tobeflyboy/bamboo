<template>
  <div>
    <el-card shadow="hover" class="main-container">
      <div class="menu-management-page">
        <!-- 第一块：查询条件区域 -->
        <!-- gutter 属性用于设置行内栅格（el-col）之间的间距。-->
        <el-row :gutter="20" class="mb20">
          <el-col :span="6">
            <el-input
              v-model.trim="data.searchKeyword"
              placeholder="输入菜单名称/路由/Icon搜索"
              clearable
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon>
                  <Search />
                </el-icon>
              </template>
            </el-input>
          </el-col>
          <el-col :span="4">
            <el-button type="primary" icon="Search" @click="handleSearch">搜索</el-button>
            <el-button icon="CircleClose" @click="resetSearch">重置</el-button>
          </el-col>
        </el-row>

        <!-- 第二块：操作工具栏 -->
        <el-space class="mb20">
          <el-button type="success" icon="Plus" @click="handleAddPermission">新增菜单</el-button>
        </el-space>

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
          <el-table-column label="菜单名称" min-width="120">
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
              <el-tag :type="scope.row.meta.isHide === 0 ? 'primary' : 'info'" size="small">
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
                  type="warning"
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

    <!-- 新增、编辑dialog -->
    <el-dialog v-model="data.dialogPermissionFormVisible" :title="data.dialogPermissionFormTitle" width="480">
      <el-form :model="data.permissionForm" ref="permissionFormRef" :rules="data.permissionFormRules">
        <el-form-item label="上级菜单" prop="parentTitle" v-if="data.showParentMenuFormItem" :label-width="data.formLabelWidth">
          <el-input v-model.trim="data.permissionForm.parentTitle" disabled />
        </el-form-item>
        <el-form-item label="菜单名称" prop="title" :label-width="data.formLabelWidth">
          <el-input v-model.trim="data.permissionForm.title" clearable placeholder="请输入菜单名称，必填项" />
        </el-form-item>
        <el-form-item label="路由name" prop="name" :label-width="data.formLabelWidth">
          <el-input v-model.trim="data.permissionForm.name" clearable placeholder="请输入路由name，必填项" />
        </el-form-item>
        <el-form-item label="路由地址" prop="path" :label-width="data.formLabelWidth">
          <el-input v-model.trim="data.permissionForm.path" clearable placeholder="请输入路由地址，必填项" />
        </el-form-item>
        <el-form-item label="视图文件路径" prop="component" :label-width="data.formLabelWidth">
          <el-input v-model.trim="data.permissionForm.component" clearable placeholder="请输入视图文件路径" />
        </el-form-item>
        <el-form-item label="重定向地址" prop="redirect" :label-width="data.formLabelWidth">
          <el-input v-model.trim="data.permissionForm.redirect" readonly clearable placeholder="可以不填写" />
        </el-form-item>
        <el-form-item label="外链地址" prop="isLink" :label-width="data.formLabelWidth">
          <el-input v-model.trim="data.permissionForm.isLink" clearable placeholder="可以不填写" />
        </el-form-item>
        <el-form-item label="排序序号" prop="sortOrder" :label-width="data.formLabelWidth">
          <el-input v-model.number.trim="data.permissionForm.sortOrder" type="number" />
        </el-form-item>
        <el-form-item label="图标" prop="icon" :label-width="data.formLabelWidth">
          <el-col :span="21">
            <el-input
              v-model="data.permissionForm.icon"
              placeholder="请选择图标，必选择项"
              readonly
              @click="data.dialogIconFormVisible = true"
            />
          </el-col>
          <el-col :span="2" class="ml8">
            <div v-if="data.permissionForm.icon" style="margin-top: 8px">
              <el-icon size="large">
                <component :is="data.permissionForm.icon" />
              </el-icon>
            </div>
          </el-col>
        </el-form-item>
        <el-form-item label="是否隐藏" prop="isHide" :label-width="data.formLabelWidth">
          <el-radio-group v-model="data.permissionForm.isHide">
            <el-radio :value="false" size="small">否</el-radio>
            <el-radio :value="true" size="small">是</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="是否全屏" prop="isFull" :label-width="data.formLabelWidth">
          <el-radio-group v-model="data.permissionForm.isFull">
            <el-radio :value="false" size="small">否</el-radio>
            <el-radio :value="true" size="small">是</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="是否标签固定" prop="isAffix" :label-width="data.formLabelWidth">
          <el-radio-group v-model="data.permissionForm.isAffix">
            <el-radio :value="false" size="small">否</el-radio>
            <el-radio :value="true" size="small">是</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="是否路由缓存" prop="isKeepAlive" :label-width="data.formLabelWidth">
          <el-radio-group v-model="data.permissionForm.isKeepAlive">
            <el-radio :value="false" size="small">否</el-radio>
            <el-radio :value="true" size="small">是</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="data.dialogPermissionFormVisible = false">取消</el-button>
          <el-button type="primary" @click="permissionSave">提交</el-button>
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
          :class="{ selected: data.permissionForm.icon === icon.name }"
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

    <!-- 删除用户dialog -->
    <el-dialog v-model="data.dialogDeletePermissionFormVisible" title="删除菜单" width="480" align-center>
      <el-form :model="data.deletePermissionForm">
        <el-form-item label="菜单名称" prop="title" :label-width="data.formLabelWidth">
          <el-input v-model.trim="data.deletePermissionForm.title" disabled />
        </el-form-item>
        <el-form-item label="路由name" prop="name" :label-width="data.formLabelWidth">
          <el-input v-model.trim="data.deletePermissionForm.name" disabled />
        </el-form-item>
        <el-form-item label="路由地址" prop="path" :label-width="data.formLabelWidth">
          <el-input v-model.trim="data.deletePermissionForm.path" disabled />
        </el-form-item>
        <el-form-item label="视图文件路径" prop="component" :label-width="data.formLabelWidth">
          <el-input v-model.trim="data.deletePermissionForm.component" disabled />
        </el-form-item>
        <el-form-item label="重定向地址" prop="redirect" :label-width="data.formLabelWidth">
          <el-input v-model.trim="data.deletePermissionForm.redirect" readonly disabled />
        </el-form-item>
        <el-form-item label="外链地址" prop="isLink" :label-width="data.formLabelWidth">
          <el-input v-model.trim="data.deletePermissionForm.isLink" disabled />
        </el-form-item>
        <el-form-item label="排序序号" prop="sortOrder" :label-width="data.formLabelWidth">
          <el-input v-model.number.trim="data.deletePermissionForm.sortOrder" type="number" disabled />
        </el-form-item>
        <el-form-item label="图标" prop="icon" :label-width="data.formLabelWidth">
          <el-col :span="21">
            <el-input v-model="data.deletePermissionForm.icon" placeholder="请选择图标，必选择项" disabled />
          </el-col>
          <el-col :span="2" class="ml8">
            <div v-if="data.deletePermissionForm.icon" style="margin-top: 8px">
              <el-icon size="large">
                <component :is="data.deletePermissionForm.icon" />
              </el-icon>
            </div>
          </el-col>
        </el-form-item>
        <el-form-item label="是否隐藏" prop="isHide" :label-width="data.formLabelWidth">
          <el-radio-group v-model="data.deletePermissionForm.isHide" disabled>
            <el-radio :value="false" size="small">否</el-radio>
            <el-radio :value="true" size="small">是</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="是否全屏" prop="isFull" :label-width="data.formLabelWidth">
          <el-radio-group v-model="data.deletePermissionForm.isFull" disabled>
            <el-radio :value="false" size="small">否</el-radio>
            <el-radio :value="true" size="small">是</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="是否标签固定" prop="isAffix" :label-width="data.formLabelWidth">
          <el-radio-group v-model="data.deletePermissionForm.isAffix" disabled>
            <el-radio :value="false" size="small">否</el-radio>
            <el-radio :value="true" size="small">是</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="是否路由缓存" prop="isKeepAlive" :label-width="data.formLabelWidth">
          <el-radio-group v-model="data.deletePermissionForm.isKeepAlive" disabled>
            <el-radio :value="false" size="small">否</el-radio>
            <el-radio :value="true" size="small">是</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="data.dialogDeletePermissionFormVisible = false">取消</el-button>
          <el-button type="danger" @click="deletePermissionSubmit">确认删除</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import * as ElIcons from "@element-plus/icons-vue";
import * as ElementPlusIconsVue from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { ref, onMounted, reactive, computed } from "vue";
import http from "@/api/index.js";

const permissionFormRef = ref(null);

// 变量声明
const data = reactive({
  // 常量
  formLabelWidth: "100px",
  tdFontSize: "12px",
  showParentMenuFormItem: false,

  // 查询条件
  searchKeyword: "",
  iconSearch: "",

  // 菜单数据
  permissionCloneList: [],
  permissionList: [],

  // dialog
  dialogPermissionFormTitle: "",
  dialogIconFormVisible: false,
  dialogPermissionFormVisible: false,
  dialogDeletePermissionFormVisible: false,

  // 表单相关
  permissionForm: {},
  deletePermissionForm: {},

  // 表单规则
  permissionFormRules: {
    title: [{ required: true, message: "请输入菜单名称", trigger: "blur" }],
    name: [{ required: true, message: "请输入路由name", trigger: "blur" }],
    path: [{ required: true, message: "视图文件路径", trigger: "blur" }],
    icon: [{ required: true, message: "请选择图标", trigger: "change" }]
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
  data.permissionForm.icon = iconName;
  data.dialogIconFormVisible = false;
};

// 根据 icon 名获取组件
const getIconComponent = iconName => {
  return iconName ? ElIcons[iconName] : null;
};

// 加载子节点（懒加载模式）
const loadChildren = (row, treeNode, resolve) => {
  if (!row) {
    // 根节点
    const roots = data.permissionCloneList.filter(item => !item.parentId);
    roots.forEach(r => {
      r.hasChildren = data.permissionCloneList.some(c => c.parentId === r.id);
      r._level = 0;
    });
    resolve(roots);
  } else {
    const children = data.permissionCloneList
      .filter(item => item.parentId === row.id)
      .map(c => {
        c.hasChildren = data.permissionCloneList.some(ch => ch.parentId === c.id);
        c._level = (row._level || 0) + 1;
        return c;
      });
    resolve(children);
  }
};

const load = async () => {
  const resp = await http.post("/api/permission/tree");
  if (resp.code === "00000") {
    data.permissionCloneList = resp.data.map(item => ({
      ...item,
      hasChildren: false, // 先默认 false，懒加载时计算
      _level: 0 // 根节点默认层级 0
    }));
    data.permissionList = data.permissionCloneList;
  }
};

// 初始化菜单数据
onMounted(async () => {
  await load();
});

// 搜索功能
const handleSearch = () => {
  console.log("handleSearch searchKeyword:", data.searchKeyword);
  if (!data.searchKeyword) {
    const keyword = data.searchKeyword.toLowerCase();
    data.permissionList = data.permissionCloneList;
  } else {
    const keyword = data.searchKeyword.toLowerCase();
    data.permissionList = data.permissionCloneList.filter(
      item =>
        (item.meta.title && item.meta.title.toLowerCase().includes(keyword)) ||
        (item.path && item.path.toLowerCase().includes(keyword)) ||
        (item.meta.icon && item.meta.icon.toLowerCase().includes(keyword))
    );
  }
};

// 重置搜索条件
const resetSearch = () => {
  console.log("resetSearch");
  data.searchKeyword = "";
  handleSearch();
};

// 新增顶级菜单示例
const handleAddPermission = () => {
  data.dialogPermissionFormTitle = "新增菜单";
  data.showParentMenuFormItem = false;
  data.permissionForm = {
    id: "",
    parentId: "",
    parentTitle: "",
    title: "",
    name: "",
    path: "",
    component: "",
    redirect: "",
    isLink: "",
    sortOrder: "",
    icon: "",
    isHide: false,
    isFull: false,
    isAffix: false,
    isKeepAlive: false
  };
  // 显示模态窗口
  data.dialogPermissionFormVisible = true;
};

// 新增子菜单
const handleAddSubPermission = permission => {
  data.dialogPermissionFormTitle = "新增子菜单";
  data.showParentMenuFormItem = true;
  data.permissionForm = {
    id: "",
    parentId: permission.id,
    parentTitle: permission.meta.title,
    title: "",
    name: "",
    path: "",
    component: "",
    redirect: "",
    isLink: "",
    sortOrder: "",
    icon: "",
    isHide: false,
    isFull: false,
    isAffix: false,
    isKeepAlive: false
  };
  console.log("新增子菜单:", data.permissionForm);
  // 显示模态窗口
  data.dialogPermissionFormVisible = true;
};

// 编辑菜单
const showEditPermissionDialog = async permission => {
  data.dialogPermissionFormTitle = "编辑菜单";
  data.showParentMenuFormItem = false;
  console.log("showEditPermissionDialog permission:", permission);
  // 表单重置
  const resp = await http.post(`/api/permission/${permission.id}`);
  console.log("showEditPermissionDialog resp:", resp);
  if (resp.code === "00000") {
    data.permissionForm = resp.data;
    // 显示模态窗口
    data.dialogPermissionFormVisible = true;
  } else {
    ElMessage.error(resp.msg);
  }
};

// 编辑菜单，提交保存
const permissionSave = () => {
  permissionFormRef.value.validate(async (valid, fields) => {
    if (valid) {
      console.log("表单验证通过:", data.permissionForm);
      const permission = {
        id: data.permissionForm.id,
        parentId: data.permissionForm.parentId,
        title: data.permissionForm.title,
        name: data.permissionForm.name,
        path: data.permissionForm.path,
        component: data.permissionForm.component,
        redirect: data.permissionForm.redirect,
        isLink: data.permissionForm.isLink,
        sortOrder: data.permissionForm.sortOrder,
        icon: data.permissionForm.icon,
        isHide: data.permissionForm.isHide,
        isFull: data.permissionForm.isFull,
        isAffix: data.permissionForm.isAffix,
        isKeepAlive: data.permissionForm.isKeepAlive
      };
      console.log("editPermissionSubmit permission:", permission);
      const resp = await http.post("/api/permission/save", permission);
      console.log("编辑菜单，提交保存响应结果 resp:", resp);
      if (resp.code === "00000") {
        ElMessage.success("编辑用户成功");
        data.dialogPermissionFormVisible = false;
        console.log("编辑菜单，提交保存成功");
        await load();
      } else {
        ElMessage.error(resp.msg);
      }
    }
  });
};

// 删除菜单
const handleDeletePermission = async permissionId => {
  console.log("删除菜单");
  console.log("handleDeletePermission permission:", permissionId);
  // 表单重置
  const resp = await http.post(`/api/permission/${permissionId}`);
  console.log("handleDeletePermission resp:", resp);
  if (resp.code === "00000") {
    data.deletePermissionForm = resp.data;
    // 显示模态窗口
    data.dialogDeletePermissionFormVisible = true;
  } else {
    ElMessage.error(resp.msg);
  }
};

const deletePermissionSubmit = async () => {
  console.log("deletePermissionSubmit permissionId=", data.deletePermissionForm.id);
  // 表单重置
  const resp = await http.post(`/api/permission/delete/${data.deletePermissionForm.id}`);
  console.log("handleDeletePermission resp:", resp);
  if (resp.code === "00000") {
    ElMessage.success("删除菜单成功");
    await load();
    data.dialogDeletePermissionFormVisible = false;
  } else {
    ElMessage.error(resp.msg);
  }
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
