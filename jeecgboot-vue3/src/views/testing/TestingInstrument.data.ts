import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import {JVxeTypes,JVxeColumn} from '/@/components/jeecg/JVxeTable/types'
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '设备名称',
    align:"center",
    dataIndex: 'assetName'
   },
   {
    title: '设备编码',
    align:"center",
    dataIndex: 'assetCode'
   },
   {
    title: '设备品牌',
    align:"center",
    dataIndex: 'assetBrand'
   },
];

//子表表格配置
export const protocolColumns: JVxeColumn[] = [
    {
      title: '串口名称',
      key: 'serialName',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
          { pattern: "only", message: "${title}不能重复" }
        ],
    },
    {
      title: '串口协议',
      key: 'serialProtocol',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
  ]
export const testingStepColumns: JVxeColumn[] = [
    {
      title: '步骤编码',
      key: 'stepCode',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
        validateRules: [
          { required: true, message: '${title}不能为空' },
          { pattern: "only", message: "${title}不能重复" }
        ],
    },
    {
      title: '步骤名称',
      key: 'stepName',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '步骤详细描述',
      key: 'stepDesc',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '接收指令',
      key: 'acceptInstruct',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
  ]

// 高级查询数据
export const superQuerySchema = {
  assetName: {title: '设备名称',order: 0,view: 'text', type: 'string',},
  assetCode: {title: '设备编码',order: 1,view: 'text', type: 'string',},
  assetBrand: {title: '设备品牌',order: 2,view: 'text', type: 'string',},
  //子表高级查询
  protocol: {
    title: '通信协议表',
    view: 'table',
    fields: {
        serialName: {title: '串口名称',order: 0,view: 'text', type: 'string',},
        serialProtocol: {title: '串口协议',order: 1,view: 'text', type: 'string',},
    }
  },
  testingStep: {
    title: '检测步骤',
    view: 'table',
    fields: {
        stepCode: {title: '步骤编码',order: 0,view: 'text', type: 'string',},
        stepName: {title: '步骤名称',order: 1,view: 'text', type: 'string',},
        stepDesc: {title: '步骤详细描述',order: 2,view: 'text', type: 'string',},
        acceptInstruct: {title: '接收指令',order: 3,view: 'text', type: 'string',},
    }
  },
};
