<template>
  <div class="p-2 erpNativeList">
    <!--查询区域-->
    <div class="jeecg-basic-table-form-container">
      <a-form ref="formRef" @keyup.enter.native="reload" :model="queryParam" :label-col="labelCol" :wrapper-col="wrapperCol">
        <a-row :gutter="24">
        </a-row>
      </a-form>
    </div>
  <div class="content">
    <!--引用表格-->
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button type="primary" v-auth="'test:student:add'"  @click="handleAdd" preIcon="ant-design:plus-outlined"> 新增</a-button>
        <a-button type="primary" v-auth="'test:student:exportXls'"  preIcon="ant-design:export-outlined" @click="onExportXls"> 导出</a-button>
        <j-upload-button type="primary"  v-auth="'test:student:importExcel'" preIcon="ant-design:import-outlined" @click="onImportXls">导入</j-upload-button>
        <a-dropdown v-if="selectedRowKeys.length > 0">
          <template #overlay>
            <a-menu>
              <a-menu-item key="1" @click="batchHandleDelete">
                <Icon icon="ant-design:delete-outlined"></Icon>
                删除
              </a-menu-item>
            </a-menu>
          </template>
          <a-button  v-auth="'test:student:deleteBatch'"
            >批量操作
            <Icon icon="mdi:chevron-down"></Icon>
          </a-button>
        </a-dropdown>
        <!-- 高级查询 -->
        <super-query :config="superQueryConfig" @search="handleSuperQuery" />
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" :dropDownActions="getDropDownAction(record)" />
      </template>
      <!--字段回显插槽-->
      <template v-slot:bodyCell="{ column, record, index, text }">
      </template>
    </BasicTable>
    <!--子表表格tab-->
    <a-tabs defaultActiveKey="1" style="margin: 10px">
      <a-tab-pane tab="学生课程成绩表" key="1" >
        <StudentCourseScoreList />
      </a-tab-pane>
     </a-tabs>
  </div>
    <!-- 表单区域 -->
    <StudentModal ref="registerModal" @success="handleSuccess" />
  </div>
</template>

<script lang="ts" name="test-student" setup>
  import { ref, reactive, computed, unref, provide } from 'vue';
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage'
  import StudentModal from './components/StudentModal.vue'
  import { columns, superQuerySchema } from './Student.data';
  import { list, deleteOne, batchDelete, getImportUrl,getExportUrl } from './Student.api';
  import { downloadFile } from '/@/utils/common/renderUtils';
  import StudentCourseScoreList from './StudentCourseScoreList.vue'
  import { useUserStore } from '/@/store/modules/user';
  const formRef = ref();
  const queryParam = reactive<any>({});
  const checkedKeys = ref<Array<string | number>>([]);
  const registerModal = ref();
  const userStore = useUserStore();
   //注册table数据
  const { prefixCls,tableContext,onExportXls,onImportXls } = useListPage({
    tableProps:{
      title: '学生表',
      api: list,
      columns,
      canResize:false,
      useSearchForm: false,
      clickToRowSelect: true,
      rowSelection: {type: 'radio'},
      actionColumn: {
        width: 120,
        fixed:'right'
      },
      beforeFetch: async (params) => {
        return Object.assign(params, queryParam);
      },
      pagination: {
        current: 1,
        pageSize: 5,
        pageSizeOptions: ['5', '10', '20'],
      },
    },
    exportConfig: {
      name:"学生表",
      url: getExportUrl,
      params: queryParam,
    },
    importConfig: {
      url: getImportUrl,
      success: handleSuccess,
    },
  })

  const [registerTable, { reload },{ rowSelection, selectedRowKeys }] = tableContext
  const mainId = computed(() => (unref(selectedRowKeys).length > 0 ? unref(selectedRowKeys)[0] : ''));
  //下发 mainId,子组件接收
  provide('mainId', mainId);

  // 高级查询配置
  const superQueryConfig = reactive(superQuerySchema);

  /**
   * 高级查询事件
   */
  function handleSuperQuery(params) {
    Object.keys(params).map((k) => {
      queryParam[k] = params[k];
    });
    reload();
  }

  /**
   * 新增事件
   */
  function handleAdd() {
    registerModal.value.disableSubmit = false;
    registerModal.value.add();
  }
  
  /**
   * 编辑事件
   */
  function handleEdit(record: Recordable) {
     registerModal.value.disableSubmit = false;
     registerModal.value.edit(record);
  }
  
  /**
   * 详情事件
   */
  function handleDetail(record: Recordable) {
    registerModal.value.disableSubmit = true;
    registerModal.value.edit(record);
  }
  
  /**
   * 删除事件
   */
  async function handleDelete(record) {
    await deleteOne({id: record.id}, handleSuccess);
  }
  
  /**
   * 批量删除事件
   */
  async function batchHandleDelete() {
    await batchDelete({ids: selectedRowKeys.value},handleSuccess);
  }
  
  /**
   * 成功回调
   */
  function handleSuccess() {
    (selectedRowKeys.value = []) && reload();
  }
  
  /**
   * 操作栏
   */
  function getTableAction(record) {
    return [
      {
        label: '编辑',
        onClick: handleEdit.bind(null, record),
        auth: 'test:student:edit'
      },
    ];
  }
  
  /**
   * 下拉操作栏
   */
  function getDropDownAction(record){
    return [
      {
        label: '详情',
        onClick: handleDetail.bind(null, record),
      },
      {
        label: '删除',
        popConfirm: {
          title: '是否确认删除',
          confirm: handleDelete.bind(null, record),
          placement: 'topLeft'
        },
        auth: 'test:student:delete'
      },
    ];
  }

  

  /* ----------------------以下为原生查询需要添加的-------------------------- */
  const toggleSearchStatus = ref<boolean>(false);
  const labelCol = reactive({
    xs:24,
    sm:4,
    xl:6,
    xxl:4
  });
  const wrapperCol = reactive({
    xs: 24,
    sm: 20,
  });
  
  /**
   * 重置
   */
  function searchReset() {
    formRef.value.resetFields();
    selectedRowKeys.value = [];
    //刷新数据
    reload();
  }
  

</script>
<style lang="less" scoped>
  .jeecg-basic-table-form-container {
    padding: 0;
    .table-page-search-submitButtons {
      display: block;
      margin-bottom: 24px;
      white-space: nowrap;
    }
    .query-group-cust{
      min-width: 100px !important;
    }
    .query-group-split-cust{
      width: 30px;
      display: inline-block;
      text-align: center
    }
    .ant-form-item:not(.ant-form-item-with-help){
      margin-bottom: 16px;
      height: 32px;
    }
    :deep(.ant-picker),:deep(.ant-input-number){
      width: 100%;
    }
  }
  .erpNativeList {
      height: 100%;
      .content {
        background-color: #fff;
        height: 100%;
      }
  }
</style>