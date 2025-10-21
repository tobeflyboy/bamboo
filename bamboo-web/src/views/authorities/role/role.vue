<template>
  <div>
    <el-card shadow="never">
      <!-- 搜索区域 -->
      <el-row :gutter="15" style="margin-bottom: 15px">
        <el-col :span="4">
          <el-input v-model.trim="data.roleName" placeholder="角色名称，模糊查询" clearable>
            <template #prefix>
              <el-icon>
                <Search />
              </el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-input v-model.trim="data.roleCode" placeholder="角色编码，精准查询" clearable>
            <template #prefix>
              <el-icon>
                <Search />
              </el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-space>
            <el-button type="primary" icon="Search" @click="search">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-space>
        </el-col>
      </el-row>

      <!-- 工具栏 -->
      <el-space style="margin-bottom: 15px">
        <el-button type="success" icon="Plus" @click="showAddRoleDialog">新增角色</el-button>
      </el-space>

      <!-- 表格区域 -->
      <el-table
        :data="data.roleList"
        class="mb20"
        :cell-style="{ fontSize: data.tdFontSize }"
        :header-cell-style="{ color: '#303133', backgroundColor: '#EBEEF5' }"
      >
        <el-table-column prop="roleName" label="角色名称" />b
        <el-table-column prop="roleCode" label="角色编码" />
        <el-table-column prop="createTime" label="创建时间" :formatter="formatTime" />
        <el-table-column prop="createUserRealName" label="创建人" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-tooltip content="授权" placement="top">
              <el-button
                type="primary"
                icon="Connection"
                size="small"
                class="is-plain"
                circle
                @click="showRolePermissionDialog(scope.row.id)"
              ></el-button>
            </el-tooltip>
            <el-tooltip content="编辑" placement="top">
              <el-button
                type="warning"
                icon="Edit"
                size="small"
                class="is-plain"
                circle
                @click="showEditRoleDialog(scope.row)"
              ></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button
                type="danger"
                icon="Delete"
                size="small"
                class="is-plain"
                circle
                @click="deleteRole(scope.row)"
              ></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="data.pageNum"
        v-model:page-size="data.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="data.total"
        layout="total, sizes, prev, pager, next, jumper"
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="margin-top: 15px"
      />
    </el-card>

    <!-- 新增/编辑角色弹窗 -->
    <el-dialog :title="data.roleForm.title" v-model="data.roleFormVisible" width="480">
      <el-form ref="roleFormRef" :model="data.roleForm" :rules="data.rules">
        <el-form-item label="角色名称" prop="roleName" :label-width="data.formLabelWidth">
          <el-input v-model.trim="data.roleForm.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode" :label-width="data.formLabelWidth">
          <el-input v-model.trim="data.roleForm.roleCode" placeholder="请输入角色编码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="data.roleFormVisible = false">取消</el-button>
        <el-button type="primary" @click="roleSave">保存</el-button>
      </template>
    </el-dialog>

    <!-- 角色权限弹窗 -->
    <el-dialog title="角色授权" v-model="permission.visible" width="40%">
      <template #header>
        <div class="dialog-header">
          <el-button @click="permission.visible = false">取消</el-button>
          <el-button type="info" @click="selectAll">全选</el-button>
          <el-button type="primary" @click="rolePermissionSave">保存</el-button>
        </div>
      </template>
      <el-form label-width="auto" style="padding: 0 5% 0 4%">
        <input type="hidden" v-model="permission.roleId" />
        <div class="scrollable-container" v-loading="permission.loading">
          <el-tree-v2
            ref="treeRef"
            :data="permission.tree"
            node-key="id"
            show-checkbox
            :props="{ children: 'children', label: 'label' }"
            :highlight-current="false"
            :height="500"
          >
            <template #default="{ node, data }">
              <div class="custom-tree-node">
                <span>
                  <el-icon v-if="data.icon">
                    <component :is="data.icon" />
                  </el-icon>
                  {{ node.label }}
                </span>
              </div>
            </template>
          </el-tree-v2>
        </div>
      </el-form>
    </el-dialog>
  </div>
</template>

<script setup>
import * as ElIcons from "@element-plus/icons-vue";
import * as ElementPlusIconsVue from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { ref, onMounted, reactive, computed, nextTick } from "vue";
import http from "@/api/index.js";
import dayjs from "dayjs";

// 日期格式化方法
const formatTime = (row, column, cellValue, index) => {
  // 处理 null、undefined、空字符串等
  if (!cellValue) {
    return "";
  }
  return cellValue ? dayjs(cellValue).format("YYYY-MM-DD HH:mm:ss") : "";
};

// 树组件 ref
const treeRef = ref();
const roleFormRef = ref();

// 权限相关状态
const permission = reactive({
  visible: false, // 弹窗是否可见
  loading: false, // 数据加载状态
  roleId: "", // 当前角色ID
  tree: [] // 树数据
});

const data = reactive({
  // 常量
  formLabelWidth: "90px",
  tdFontSize: "12px",

  // 查询参数
  roleName: null,
  roleCode: null,
  pageNum: 1,
  pageSize: 10,

  roleList: [],
  total: 0,

  roleForm: [],
  roleFormVisible: false,

  rules: {
    roleName: [{ required: true, message: "请输入角色名称", trigger: "blur" }],
    roleCode: [{ required: true, message: "请输入角色编码", trigger: "blur" }]
  }
});

// 加载角色列表
const loadRoles = async () => {
  const role = {
    pageNum: data.pageNum,
    roleCode: data.roleCode,
    roleName: data.roleName
  };
  console.log("loadRoles role:", role);
  const resp = await http.post("/api/role/list", role);
  console.log("loadRoles resp:", resp);
  data.roleList = resp.data.list;
  data.total = resp.data.total;
  data.pageSize = resp.data.pageSize;
};

// 搜索
const search = () => {
  data.pageNum = 1;
  loadRoles();
};

// 重置搜索
const resetSearch = () => {
  data.pageNum = 1;
  data.roleCode = "";
  data.roleName = "";
  loadRoles();
};

// 分页控件：当每页大小改变时
const handleSizeChange = newSize => {
  console.log("每页大小改变为:", newSize);
  // 关键：改变每页大小时，通常重置到第一页
  data.pageNum = 1;
  // v-model 会同步，但这里直接赋值更清晰
  loadRoles();
};

// 分页控件：当当前页码改变时
const handleCurrentChange = newPage => {
  console.log("当前页码改变为:", newPage);
  // 页码改变时，直接加载数据，不需要重置页码
  loadRoles();
};

// 显示新增角色对话框
const showAddRoleDialog = () => {
  data.roleForm = { title: "新增角色" };
  data.roleFormVisible = true;
};

// 显示编辑角色对话框
const showEditRoleDialog = role => {
  data.roleForm = {
    title: "编辑角色",
    id: role.id,
    roleName: role.roleName,
    roleCode: role.roleCode
  };
  data.roleFormVisible = true;
};

// 保存角色
const roleSave = async () => {
  roleFormRef.value.validate(async valid => {
    if (valid) {
      const role = {
        id: data.roleForm.id,
        roleName: data.roleForm.roleName,
        roleCode: data.roleForm.roleCode
      };
      const resp = await http.post("/api/role/save", role);
      console.log("roleSave resp:", resp);
      if (resp.code === "00000") {
        search();
        data.roleFormVisible = false;
        ElMessage.success("保存成功");
      } else {
        ElMessage.error(resp.msg);
      }
    }
  });
};

// 删除角色
const deleteRole = async role => {
  console.log(role);
  ElMessageBox.confirm(`确认删除角色【${role.roleName}】？`, "删除确认", {
    confirmButtonText: "确认",
    cancelButtonText: "取消",
    type: "warning"
  }).then(async () => {
    const resp = await http.post(`/api/role/delete/${role.id}`);
    if (resp.code === "00000") {
      ElMessage.success("删除成功");
      search();
    } else {
      ElMessage.error(resp.msg);
    }
  });
};

// 构建树节点，同时收集选中和部分选中节点
function buildPermissionTree(data, checkedKeys = [], halfCheckedKeys = []) {
  return data.map(item => {
    const node = {
      id: item.id,
      label: item.meta.title,
      children: item.children ? buildPermissionTree(item.children, checkedKeys, halfCheckedKeys) : undefined
    };

    // 选中节点
    if (item.checked === 1) checkedKeys.push(item.id);
    // 部分选中节点
    if (item.checked === 3) halfCheckedKeys.push(item.id);

    return node;
  });
}

// 获取所有需要展开的节点ID，保证选中和部分选中的父节点都会展开
function getExpandedKeys(treeData, checkedKeys = [], halfCheckedKeys = []) {
  const expandedSet = new Set(); // 保存展开节点ID
  const parentMap = new Map(); // 保存每个节点的父节点ID

  // 遍历树构建 parentMap
  function buildMap(nodes, parentId = null) {
    nodes.forEach(node => {
      parentMap.set(node.id, parentId);
      if (node.children?.length) buildMap(node.children, node.id);
    });
  }

  // 将选中节点及半选节点自身和父节点都加入展开集合
  function addParents(id) {
    expandedSet.add(id);
    const pid = parentMap.get(id);
    if (pid) addParents(pid);
  }

  buildMap(treeData);
  [...checkedKeys, ...halfCheckedKeys].forEach(addParents);

  return Array.from(expandedSet);
}

// 打开权限弹窗
async function showRolePermissionDialog(roleId) {
  permission.visible = true;
  permission.loading = true;
  permission.roleId = roleId;
  permission.tree = [];

  try {
    const resp = await http.post(`/api/role_permission/${roleId}`);
    if (resp.code !== "00000") {
      ElMessage.error(resp.msg);
      permission.loading = false;
      return;
    }

    const checkedKeys = [];
    const halfCheckedKeys = [];
    // 构建树节点
    permission.tree = buildPermissionTree(resp.data, checkedKeys, halfCheckedKeys);

    await nextTick();

    // 获取所有需要展开的节点ID
    const expandedKeys = getExpandedKeys(permission.tree, checkedKeys, halfCheckedKeys);
    // 设置树选中状态
    treeRef.value.setCheckedKeys([...checkedKeys, ...halfCheckedKeys]);
    // 设置树展开状态
    treeRef.value.setExpandedKeys(expandedKeys);
  } catch (err) {
    console.error(err);
    ElMessage.error("加载权限失败");
  } finally {
    permission.loading = false;
  }
}

// 全选
function selectAll() {
  const allIds = [];
  function traverse(nodes) {
    nodes.forEach(n => {
      allIds.push(n.id);
      if (n.children?.length) traverse(n.children);
    });
  }
  traverse(permission.tree);
  treeRef.value.setCheckedKeys(allIds);
}

// 保存权限
const rolePermissionSave = async () => {
  const checkedIds = treeRef.value.getCheckedKeys();
  const halfCheckedIds = treeRef.value.getHalfCheckedKeys();
  const allSelectedIds = [...checkedIds, ...halfCheckedIds].map(id => id.toString());
  const rolePermission = {
    roleId: permission.roleId,
    permissionIdList: allSelectedIds
  };

  const resp = await http.post("/api/role_permission/save", rolePermission);
  if (resp.code === "00000") {
    ElMessage.success("权限保存成功");
    permission.visible = false;
  } else {
    ElMessage.error(resp.msg);
  }
};

// 初始化加载
onMounted(() => {
  loadRoles();
});
</script>
