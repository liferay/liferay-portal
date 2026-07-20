/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.expando.service.impl;

import com.liferay.expando.kernel.model.ExpandoRow;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.expando.kernel.service.persistence.ExpandoTablePersistence;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portlet.expando.service.base.ExpandoRowLocalServiceBaseImpl;

import java.util.Collections;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 * @author Wesley Gong
 */
public class ExpandoRowLocalServiceImpl extends ExpandoRowLocalServiceBaseImpl {

	@Override
	public ExpandoRow addRow(long tableId, long classPK)
		throws PortalException {

		ExpandoTable table = _expandoTablePersistence.findByPrimaryKey(tableId);

		long rowId = counterLocalService.increment();

		ExpandoRow row = expandoRowPersistence.create(rowId);

		row.setCompanyId(table.getCompanyId());
		row.setTableId(tableId);
		row.setClassPK(classPK);

		return expandoRowPersistence.update(row);
	}

	@Override
	public void deleteRow(ExpandoRow row) {

		// Row

		expandoRowPersistence.remove(row);

		expandoRowPersistence.flush();

		// Values

		_expandoValueLocalService.deleteRowValues(row.getRowId());
	}

	@Override
	public void deleteRow(long rowId) throws PortalException {
		ExpandoRow row = expandoRowPersistence.findByPrimaryKey(rowId);

		deleteRow(row);
	}

	@Override
	public void deleteRow(long tableId, long classPK) throws PortalException {
		ExpandoRow row = expandoRowPersistence.findByT_C(tableId, classPK);

		deleteRow(row);
	}

	@Override
	public void deleteRow(
			long companyId, long classNameId, String tableName, long classPK)
		throws PortalException {

		ExpandoTable table = _expandoTableLocalService.getTable(
			companyId, classNameId, tableName);

		expandoRowLocalService.deleteRow(table.getTableId(), classPK);
	}

	@Override
	public void deleteRow(
			long companyId, String className, String tableName, long classPK)
		throws PortalException {

		expandoRowLocalService.deleteRow(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName, classPK);
	}

	@Override
	public void deleteRows(long classPK) {
		List<ExpandoRow> rows = expandoRowPersistence.findByClassPK(classPK);

		for (ExpandoRow row : rows) {
			deleteRow(row);
		}
	}

	@Override
	public void deleteRows(long companyId, long classNameId, long classPK) {
		List<ExpandoTable> tables = _expandoTableLocalService.getTables(
			companyId, classNameId);

		for (ExpandoTable table : tables) {
			ExpandoRow row = expandoRowPersistence.fetchByT_C(
				table.getTableId(), classPK);

			if (row == null) {
				continue;
			}

			deleteRow(row);
		}
	}

	@Override
	public ExpandoRow fetchRow(long tableId, long classPK) {
		return expandoRowPersistence.fetchByT_C(tableId, classPK);
	}

	@Override
	public List<ExpandoRow> getDefaultTableRows(
		long companyId, long classNameId, int start, int end) {

		return expandoRowLocalService.getRows(
			companyId, classNameId, ExpandoTableConstants.DEFAULT_TABLE_NAME,
			start, end);
	}

	@Override
	public List<ExpandoRow> getDefaultTableRows(
		long companyId, String className, int start, int end) {

		return expandoRowLocalService.getDefaultTableRows(
			companyId, _classNameLocalService.getClassNameId(className), start,
			end);
	}

	@Override
	public int getDefaultTableRowsCount(long companyId, long classNameId) {
		return expandoRowLocalService.getRowsCount(
			companyId, classNameId, ExpandoTableConstants.DEFAULT_TABLE_NAME);
	}

	@Override
	public int getDefaultTableRowsCount(long companyId, String className) {
		return expandoRowLocalService.getDefaultTableRowsCount(
			companyId, _classNameLocalService.getClassNameId(className));
	}

	@Override
	public ExpandoRow getRow(long rowId) throws PortalException {
		return expandoRowPersistence.findByPrimaryKey(rowId);
	}

	@Override
	public ExpandoRow getRow(long tableId, long classPK)
		throws PortalException {

		return expandoRowPersistence.findByT_C(tableId, classPK);
	}

	@Override
	public ExpandoRow getRow(
			long companyId, long classNameId, String tableName, long classPK)
		throws PortalException {

		ExpandoTable table = _expandoTablePersistence.findByC_C_N(
			companyId, classNameId, tableName);

		return expandoRowPersistence.findByT_C(table.getTableId(), classPK);
	}

	@Override
	public ExpandoRow getRow(
			long companyId, String className, String tableName, long classPK)
		throws PortalException {

		return expandoRowLocalService.getRow(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName, classPK);
	}

	@Override
	public List<ExpandoRow> getRows(long tableId, int start, int end) {
		return expandoRowPersistence.findByTableId(tableId, start, end);
	}

	@Override
	public List<ExpandoRow> getRows(
		long companyId, long classNameId, String tableName, int start,
		int end) {

		ExpandoTable table = _expandoTablePersistence.fetchByC_C_N(
			companyId, classNameId, tableName);

		if (table == null) {
			return Collections.emptyList();
		}

		return expandoRowPersistence.findByTableId(
			table.getTableId(), start, end);
	}

	@Override
	public List<ExpandoRow> getRows(
		long companyId, String className, String tableName, int start,
		int end) {

		return expandoRowLocalService.getRows(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName, start, end);
	}

	@Override
	public int getRowsCount(long tableId) {
		return expandoRowPersistence.countByTableId(tableId);
	}

	@Override
	public int getRowsCount(
		long companyId, long classNameId, String tableName) {

		ExpandoTable table = _expandoTablePersistence.fetchByC_C_N(
			companyId, classNameId, tableName);

		if (table == null) {
			return 0;
		}

		return expandoRowPersistence.countByTableId(table.getTableId());
	}

	@Override
	public int getRowsCount(
		long companyId, String className, String tableName) {

		return expandoRowLocalService.getRowsCount(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);
	}

	@BeanReference(type = ClassNameLocalService.class)
	private ClassNameLocalService _classNameLocalService;

	@BeanReference(type = ExpandoTableLocalService.class)
	private ExpandoTableLocalService _expandoTableLocalService;

	@BeanReference(type = ExpandoTablePersistence.class)
	private ExpandoTablePersistence _expandoTablePersistence;

	@BeanReference(type = ExpandoValueLocalService.class)
	private ExpandoValueLocalService _expandoValueLocalService;

}