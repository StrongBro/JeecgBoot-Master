import {defHttp} from '/@/utils/http/axios';
import { useMessage } from "/@/hooks/web/useMessage";

const { createConfirm } = useMessage();

enum Api {
  list = '/test/student/list',
  save= '/test/student/add',
  edit= '/test/student/edit',
  deleteOne = '/test/student/delete',
  deleteBatch = '/test/student/deleteBatch',
  importExcel = '/test/student/importExcel',
  exportXls = '/test/student/exportXls',
  studentCourseScoreList = '/test/student/listStudentCourseScoreByMainId',
  studentCourseScoreSave = '/test/student/addStudentCourseScore',
  studentCourseScoreEdit = '/test/student/editStudentCourseScore',
  studentCourseScoreDelete = '/test/student/deleteStudentCourseScore',
  studentCourseScoreDeleteBatch = '/test/student/deleteBatchStudentCourseScore',
}
/**
 * 导出api
 * @param params
 */
export const getExportUrl = Api.exportXls;

/**
 * 导入api
 */
export const getImportUrl = Api.importExcel;

/**
 * 列表接口
 * @param params
 */
export const list = (params) =>
  defHttp.get({ url: Api.list, params });

/**
 * 删除单个
 */
export const deleteOne = (params, handleSuccess) => {
  return defHttp.delete({ url: Api.deleteOne, params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
}

/**
 * 批量删除
 * @param params
 */
export const batchDelete = (params, handleSuccess) => {
  createConfirm({
    iconType: 'warning',
    title: '确认删除',
    content: '是否删除选中数据',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      return defHttp.delete({ url: Api.deleteBatch, data: params }, { joinParamsToUrl: true }).then(() => {
        handleSuccess();
      });
    }
  });
}

/**
 * 保存或者更新
 * @param params
 */
export const saveOrUpdate = (params, isUpdate) => {
  let url = isUpdate ? Api.edit : Api.save;
  return defHttp.post({ url: url, params },{ isTransformResponse: false });
}
  
/**
 * 列表接口
 * @param params
 */
export const studentCourseScoreList = (params) => {
  if(params['studentId']){
    return defHttp.get({ url: Api.studentCourseScoreList, params });
  }
  return Promise.resolve({});
}

/**
 * 删除单个
 */
export const studentCourseScoreDelete = (params,handleSuccess) => {
  return defHttp.delete({ url: Api.studentCourseScoreDelete, params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
}

/**
 * 批量删除
 * @param params
 */
export const studentCourseScoreDeleteBatch = (params, handleSuccess) => {
  createConfirm({
    iconType: 'warning',
    title: '确认删除',
    content: '是否删除选中数据',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      return defHttp.delete({ url: Api.studentCourseScoreDeleteBatch, data: params }, { joinParamsToUrl: true }).then(() => {
        handleSuccess();
      });
    },
  });
};

/**
 * 保存或者更新
 * @param params
 */
export const studentCourseScoreSaveOrUpdate = (params, isUpdate) => {
  let url = isUpdate ? Api.studentCourseScoreEdit : Api.studentCourseScoreSave;
  return defHttp.post({ url: url, params },{ isTransformResponse: false });
};

/**
 * 导入
 */
export const studentCourseScoreImportUrl = '/test/student/importStudentCourseScore'

/**
 * 导出
 */
export const studentCourseScoreExportXlsUrl = '/test/student/exportStudentCourseScore'
