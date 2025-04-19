-- 注意：该页面对应的前台目录为views/serial文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external) 
VALUES ('2025041410256300140', NULL, '串口表', '/serial/serialPortList', 'serial/SerialPortList', NULL, NULL, 0, NULL, '1', 0.00, 0, NULL, 1, 0, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2025-04-14 22:25:14', NULL, NULL, 0);

-- 权限控制sql
-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025041410256300141', '2025041410256300140', '添加串口表', NULL, NULL, 0, NULL, NULL, 2, 'serial:serial_port:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-04-14 22:25:14', NULL, NULL, 0, 0, '1', 0);
-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025041410256300142', '2025041410256300140', '编辑串口表', NULL, NULL, 0, NULL, NULL, 2, 'serial:serial_port:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-04-14 22:25:14', NULL, NULL, 0, 0, '1', 0);
-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025041410256300143', '2025041410256300140', '删除串口表', NULL, NULL, 0, NULL, NULL, 2, 'serial:serial_port:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-04-14 22:25:14', NULL, NULL, 0, 0, '1', 0);
-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025041410256300144', '2025041410256300140', '批量删除串口表', NULL, NULL, 0, NULL, NULL, 2, 'serial:serial_port:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-04-14 22:25:14', NULL, NULL, 0, 0, '1', 0);
-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025041410256300145', '2025041410256300140', '导出excel_串口表', NULL, NULL, 0, NULL, NULL, 2, 'serial:serial_port:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-04-14 22:25:14', NULL, NULL, 0, 0, '1', 0);
-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025041410256300146', '2025041410256300140', '导入excel_串口表', NULL, NULL, 0, NULL, NULL, 2, 'serial:serial_port:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-04-14 22:25:14', NULL, NULL, 0, 0, '1', 0);