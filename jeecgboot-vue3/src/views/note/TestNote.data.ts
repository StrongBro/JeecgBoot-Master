import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
  {
    title: '用户名',
    align: "center",
    sorter: true,
    dataIndex: 'name'
  },
  {
    title: '年龄',
    align: "center",
    sorter: true,
    dataIndex: 'age'
  },
  {
    title: '性别',
    align: "center",
    dataIndex: 'sex_dictText'
  },
  {
    title: '生日',
    align: "center",
    dataIndex: 'birthday',
    customRender:({text}) =>{
      text = !text ? "" : (text.length > 10 ? text.substr(0,10) : text);
      return text;
    },
  },
  {
    title: '请假原因',
    align: "center",
    dataIndex: 'contents',
  },
  {
    title: '地区',
    align: "center",
    dataIndex: 'sheng',
  },
  {
    title: '年',
    align: "center",
    dataIndex: 'year',
    customRender:({text}) =>{
      text = !text ? "" : (text.length > 10 ? text.substr(0,10) : text);
      if(text) {
        return getWeekMonthQuarterYear(text)['year'];
      } else {
        return text;
      }
    },
  },
  {
    title: '月',
    align: "center",
    dataIndex: 'month',
    customRender:({text}) =>{
      text = !text ? "" : (text.length > 10 ? text.substr(0,10) : text);
      if(text) {
        return getWeekMonthQuarterYear(text)['month'];
      } else {
        return text;
      }
    },
  },
];

// 高级查询数据
export const superQuerySchema = {
  name: {title: '用户名',order: 0,view: 'popup', type: 'string',code: 'tj_user_report', orgFields: 'username', destFields: 'name', popupMulti: false,},
  age: {title: '年龄',order: 1,view: 'number', type: 'number',},
  sex: {title: '性别',order: 2,view: 'list', type: 'string',dictCode: 'sex',},
  birthday: {title: '生日',order: 3,view: 'date', type: 'string',},
  contents: {title: '请假原因',order: 4,view: 'umeditor', type: 'string',},
  sheng: {title: '地区',order: 5,view: 'pca', type: 'string',},
  year: {title: '年',order: 6,view: 'date', type: 'string',},
  month: {title: '月',order: 7,view: 'date', type: 'string',},
};
