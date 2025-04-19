<template>
  <a-spin :spinning="loading">
    <JFormContainer :disabled="disabled">
      <template #detail>
        <a-form v-bind="formItemLayout" name="TestingInstrumentForm" ref="formRef">
          <a-row>
						<a-col :span="24">
							<a-form-item label="设备名称" v-bind="validateInfos.assetName" id="TestingInstrumentForm-assetName" name="assetName">
								<a-input v-model:value="formData.assetName" placeholder="请输入设备名称"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="设备编码" v-bind="validateInfos.assetCode" id="TestingInstrumentForm-assetCode" name="assetCode">
								<a-input v-model:value="formData.assetCode" placeholder="请输入设备编码"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="设备品牌" v-bind="validateInfos.assetBrand" id="TestingInstrumentForm-assetBrand" name="assetBrand">
								<a-input v-model:value="formData.assetBrand" placeholder="请输入设备品牌"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
          </a-row>
        </a-form>
      </template>
    </JFormContainer>

		<!-- 子表单区域 -->
    <a-tabs v-model:activeKey="activeKey" animated style="overflow:hidden;">
      <a-tab-pane tab="通信协议表" key="protocol" :forceRender="true">
        <j-vxe-table
          :keep-source="true"
          resizable
          ref="protocolTableRef"
          :loading="protocolTable.loading"
          :columns="protocolTable.columns"
          :dataSource="protocolTable.dataSource"
          :height="340"
          :disabled="disabled"
          :rowNumber="true"
          :rowSelection="true"
          :toolbar="true"/>
      </a-tab-pane>
      <a-tab-pane tab="检测步骤" key="testingStep" :forceRender="true">
        <j-vxe-table
          :keep-source="true"
          resizable
          ref="testingStepTableRef"
          :loading="testingStepTable.loading"
          :columns="testingStepTable.columns"
          :dataSource="testingStepTable.dataSource"
          :height="340"
          :disabled="disabled"
          :rowNumber="true"
          :rowSelection="true"
          :toolbar="true"/>
      </a-tab-pane>
    </a-tabs>
  </a-spin>
</template>

<script lang="ts">
  import { defineComponent, ref, reactive, computed, toRaw, onMounted } from 'vue';
  import { defHttp } from '/@/utils/http/axios';
  import { useValidateAntFormAndTable } from '/@/hooks/system/useJvxeMethods';
  import { queryProtocolListByMainId, queryTestingStepListByMainId, queryDataById, saveOrUpdate } from '../TestingInstrument.api';
  import { JVxeTable } from '/@/components/jeecg/JVxeTable';
  import {protocolColumns, testingStepColumns} from '../TestingInstrument.data';
  import JFormContainer from '/@/components/Form/src/container/JFormContainer.vue';
  import { Form } from 'ant-design-vue';
  const useForm = Form.useForm;

  export default defineComponent({
    name: "TestingInstrumentForm",
    components:{
      JVxeTable,
			JFormContainer,
    },
    props:{
      formDisabled:{
        type: Boolean,
        default: false
      },
      formData: { type: Object, default: ()=>{} },
      formBpm: { type: Boolean, default: true }
    },
    emits:['success'],
    setup(props, {emit}) {
      const loading = ref(false);
      const formRef = ref();
      const protocolTableRef = ref();
      const protocolTable = reactive<Record<string, any>>({
        loading: false,
        columns: protocolColumns,
        dataSource: []
      });
      const testingStepTableRef = ref();
      const testingStepTable = reactive<Record<string, any>>({
        loading: false,
        columns: testingStepColumns,
        dataSource: []
      });
      const activeKey = ref('protocol');
      const formData = reactive<Record<string, any>>({
        id: '',
        assetName: '',   
        assetCode: '',   
        assetBrand: '',   
      });

      //表单验证
      const validatorRules = reactive({
      });
      const {resetFields, validate, validateInfos} = useForm(formData, validatorRules, {immediate: false});
      const dbData = {};
      const formItemLayout = {
        labelCol: {xs: {span: 24}, sm: {span: 5}},
        wrapperCol: {xs: {span: 24}, sm: {span: 16}},
      };

      // 表单禁用
      const disabled = computed(()=>{
        if(props.formBpm === true){
          if(props.formData.disabled === false){
            return false;
          }else{
            return true;
          }
        }
        return props.formDisabled;
      });

      

      function add() {
        resetFields();
        protocolTable.dataSource = [];
        testingStepTable.dataSource = [];
      }

      async function edit(row) {
        //主表数据
        await queryMainData(row.id);
        //子表数据
        const protocolDataList = await queryProtocolListByMainId(row['id']);
        protocolTable.dataSource = [...protocolDataList];
        const testingStepDataList = await queryTestingStepListByMainId(row['id']);
        testingStepTable.dataSource = [...testingStepDataList];
      }

      async function queryMainData(id) {
        const row = await queryDataById(id);
        resetFields();
        const tmpData = {};
        Object.keys(formData).forEach((key) => {
          if(row.hasOwnProperty(key)){
            tmpData[key] = row[key]
          }
        })
        //赋值
        Object.assign(formData,tmpData);
      }

      const {getSubFormAndTableData, transformData} = useValidateAntFormAndTable(activeKey, {
        'protocol': protocolTableRef,
        'testingStep': testingStepTableRef,
      });

      async function getFormData() {
        try {
          // 触发表单验证
          await validate();
        } catch ({ errorFields }) {
          if (errorFields) {
            const firstField = errorFields[0];
            if (firstField) {
              formRef.value.scrollToField(firstField.name, { behavior: 'smooth', block: 'center' });
            }
          }
          return Promise.reject(errorFields);
        }
        return transformData(toRaw(formData))
      }

      async function submitForm() {
        const mainData = await getFormData();
        const subData = await getSubFormAndTableData();
        const values = Object.assign({}, dbData, mainData, subData);
        console.log('表单提交数据', values)
        const isUpdate = values.id ? true : false
        await saveOrUpdate(values, isUpdate);
        //关闭弹窗
        emit('success');
      }
      
      function setFieldsValue(values) {
        if(values){
          Object.keys(values).map(k=>{
            formData[k] = values[k];
          });
        }
      }

      /**
       * 值改变事件触发-树控件回调
       * @param key
       * @param value
       */
      function handleFormChange(key, value) {
        formData[key] = value;
      }


      return {
        protocolTableRef,
        protocolTable,
        testingStepTableRef,
        testingStepTable,
        validatorRules,
        validateInfos,
        activeKey,
        loading,
        formData,
        setFieldsValue,
        handleFormChange,
        formItemLayout,
        disabled,
        getFormData,
        submitForm,
        add,
        edit,
        formRef,
      }
    }
  });
</script>
<style lang="less" scoped>
  .ant-tabs-tabpane.sub-one-form {
    max-height: 340px;
    overflow: auto;
  }
</style>
