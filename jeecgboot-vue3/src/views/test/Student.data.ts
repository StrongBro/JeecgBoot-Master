import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
  {
    title: '学生姓名',
    align:"center",
    dataIndex: 'studentName'
  },
  {
    title: '学生年龄',
    align:"center",
    dataIndex: 'studentAge'
  },
];

//子表列表数据
export const studentCourseScoreColumns: BasicColumn[] = [
  {
    title: '学生ID',
    align:"center",
    dataIndex: 'studentId'
  },
  {
    title: '课程ID',
    align:"center",
    dataIndex: 'courseId'
  },
  {
    title: '教师ID',
    align:"center",
    dataIndex: 'teacherId'
  },
  {
    title: '课程成绩',
    align:"center",
    dataIndex: 'courseScore'
  },
];

// 高级查询数据
export const superQuerySchema = {
  studentName: {title: '学生姓名',order: 0,view: 'text', type: 'string',},
  studentAge: {title: '学生年龄',order: 1,view: 'number', type: 'number',},
};
