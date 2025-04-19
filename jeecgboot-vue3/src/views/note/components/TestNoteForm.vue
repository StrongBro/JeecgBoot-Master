<template>
  <a-spin :spinning="confirmLoading">
    <JFormContainer :disabled="disabled">
      <template #detail>
        <a-form ref="formRef" class="antd-modal-form" :labelCol="labelCol" :wrapperCol="wrapperCol" name="TestNoteForm">
          <a-row>
						<a-col :span="24">
							<a-form-item label="用户名" v-bind="validateInfos.name" id="TestNoteForm-name" name="name">
								<j-popup
									placeholder="请选择用户名"
									v-model:value="formData.name"
									code="tj_user_report"
									:fieldConfig="[
										{ source: 'username', target: 'name' },
									]"
									:multi="true"
									:setFieldsValue="setFieldsValue"
									 allow-clear />							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="年龄" v-bind="validateInfos.age" id="TestNoteForm-age" name="age">
								<a-input-number v-model:value="formData.age" placeholder="请输入年龄" style="width: 100%" />
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="性别" v-bind="validateInfos.sex" id="TestNoteForm-sex" name="sex">
								<j-dict-select-tag v-model:value="formData.sex" dictCode="sex" placeholder="请选择性别"  allow-clear />
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="生日" v-bind="validateInfos.birthday" id="TestNoteForm-birthday" name="birthday">
								<a-date-picker placeholder="请选择生日"  v-model:value="formData.birthday" value-format="YYYY-MM-DD"  style="width: 100%"  allow-clear />
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="请假原因" v-bind="validateInfos.contents" id="TestNoteForm-contents" name="contents">
								<j-editor v-model:value="formData.contents"  :autoFocus="false"/>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="地区" v-bind="validateInfos.sheng" id="TestNoteForm-sheng" name="sheng">
								<j-area-linkage v-model:value="formData.sheng" placeholder="请输入地区" saveCode="region"  allow-clear />
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="年" v-bind="validateInfos.year" id="TestNoteForm-year" name="year">
								<a-date-picker placeholder="请选择年" picker="year" v-model:value="formData.year" value-format="YYYY-MM-DD"  style="width: 100%"  allow-clear />
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="月" v-bind="validateInfos.month" id="TestNoteForm-month" name="month">
								<a-date-picker placeholder="请选择月" picker="month" v-model:value="formData.month" value-format="YYYY-MM-DD"  style="width: 100%"  allow-clear />
							</a-form-item>
						</a-col>
          </a-row>
        </a-form>
      </template>
    </JFormContainer>
  </a-spin>
</template>

<script lang="ts" setup>
  import { ref, reactive, defineExpose, nextTick, defineProps, computed, onMounted } from 'vue';
  import { defHttp } from '/@/utils/http/axios';
  import { useMessage } from '/@/hooks/web/useMessage';
  import JDictSelectTag from '/@/components/Form/src/jeecg/components/JDictSelectTag.vue';
  import JPopup from '/@/components/Form/src/jeecg/components/JPopup.vue';
  import JAreaLinkage from '/@/components/Form/src/jeecg/components/JAreaLinkage.vue';
  import JEditor from '/@/components/Form/src/jeecg/components/JEditor.vue';
  import { getValueType } from '/@/utils';
  import { saveOrUpdate } from '../TestNote.api';
  import { Form } from 'ant-design-vue';
  import JFormContainer from '/@/components/Form/src/container/JFormContainer.vue';
  const props = defineProps({
    formDisabled: { type: Boolean, default: false },
    formData: { type: Object, default: () => ({})},
    formBpm: { type: Boolean, default: true }
  });
  const formRef = ref();
  const useForm = Form.useForm;
  const emit = defineEmits(['register', 'ok']);
  const formData = reactive<Record<string, any>>({
    id: '',
    name: '',   
    age: undefined,
    sex: '',   
    birthday: '',   
    contents: '',   
    sheng: '',   
    year: '',   
    month: '',   
  });
  const { createMessage } = useMessage();
  const labelCol = ref<any>({ xs: { span: 24 }, sm: { span: 5 } });
  const wrapperCol = ref<any>({ xs: { span: 24 }, sm: { span: 16 } });
  const confirmLoading = ref<boolean>(false);
  //表单验证
  const validatorRules = reactive({
  });
  const { resetFields, validate, validateInfos } = useForm(formData, validatorRules, { immediate: false });

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

  
  /**
   * 新增
   */
  function add() {
    edit({});
  }

  /**
   * 编辑
   */
  function edit(record) {
    nextTick(() => {
      resetFields();
      const tmpData = {};
      Object.keys(formData).forEach((key) => {
        if(record.hasOwnProperty(key)){
          tmpData[key] = record[key]
        }
      })
      //赋值
      Object.assign(formData, tmpData);
    });
  }

  /**
   * 提交数据
   */
  async function submitForm() {
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
    confirmLoading.value = true;
    const isUpdate = ref<boolean>(false);
    //时间格式化
    let model = formData;
    if (model.id) {
      isUpdate.value = true;
    }
    //循环数据
    for (let data in model) {
      //如果该数据是数组并且是字符串类型
      if (model[data] instanceof Array) {
        let valueType = getValueType(formRef.value.getProps, data);
        //如果是字符串类型的需要变成以逗号分割的字符串
        if (valueType === 'string') {
          model[data] = model[data].join(',');
        }
      }
    }
    await saveOrUpdate(model, isUpdate.value)
      .then((res) => {
        if (res.success) {
          createMessage.success(res.message);
          emit('ok');
        } else {
          createMessage.warning(res.message);
        }
      })
      .finally(() => {
        confirmLoading.value = false;
      });
  }

  /**
   *  popup组件值改变事件
   */
  function setFieldsValue(map) {
    Object.keys(map).map((key) => {
      formData[key] = map[key];
    });
  }

  defineExpose({
    add,
    edit,
    submitForm,
  });
</script>

<style lang="less" scoped>
  .antd-modal-form {
    padding: 14px;
  }
</style>
