import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';
import { rules } from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
  {
    title: '端口号',
    align: 'center',
    dataIndex: 'serialPort',
  },
  {
    title: '端口名字',
    align: 'center',
    dataIndex: 'serialName',
  },
  {
    title: '端口描述',
    align: 'center',
    dataIndex: 'serialDesc',
  },
  {
    title: '波特率',
    align: 'center',
    dataIndex: 'baudRate_dictText',
  },
  {
    title: '数据位',
    align: 'center',
    dataIndex: 'dataBits',
  },
  {
    title: '停止位',
    align: 'center',
    dataIndex: 'stopBit',
  },
  {
    title: '数据校验位',
    align: 'center',
    dataIndex: 'dataParty',
  },
];
//查询数据
export const searchFormSchema: FormSchema[] = [];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '端口号',
    field: 'serialPort',
    component: 'Input',
    dynamicRules: ({ model, schema }) => {
      return [{ required: true, message: '请输入端口号!' }, { ...rules.duplicateCheckRule('serial_port', 'serial_port', model, schema)[0] }];
    },
  },
  {
    label: '端口名字',
    field: 'serialName',
    component: 'Input',
  },
  {
    label: '端口描述',
    field: 'serialDesc',
    component: 'Input',
  },
  {
    label: '波特率',
    field: 'baudRate',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: '',
    },
  },
  {
    label: '数据位',
    field: 'dataBits',
    component: 'InputNumber',
  },
  {
    label: '停止位',
    field: 'stopBit',
    component: 'InputNumber',
  },
  {
    label: '数据校验位',
    field: 'dataParty',
    component: 'InputNumber',
  },
  // TODO 主键隐藏字段，目前写死为ID
  {
    label: '',
    field: 'id',
    component: 'Input',
    show: false,
  },
];

// 高级查询数据
export const superQuerySchema = {
  serialPort: { title: '端口号', order: 0, view: 'text', type: 'string' },
  serialName: { title: '端口名字', order: 1, view: 'text', type: 'string' },
  serialDesc: { title: '端口描述', order: 2, view: 'text', type: 'string' },
  baudRate: { title: '波特率', order: 3, view: 'number', type: 'number', dictCode: '' },
  dataBits: { title: '数据位', order: 4, view: 'number', type: 'number' },
  stopBit: { title: '停止位', order: 5, view: 'number', type: 'number' },
  dataParty: { title: '数据校验位', order: 6, view: 'number', type: 'number' },
};

/**
 * 流程表单调用这个方法获取formSchema
 * @param param
 */
export function getBpmFormSchema(_formData): FormSchema[] {
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}
