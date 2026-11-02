/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.expando.service.impl;

import com.liferay.expando.kernel.model.ExpandoRow;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portlet.expando.service.base.ExpandoRowLocalServiceBaseImpl;

import java.util.List;

/**
 * @author Brian Wing Shun Chan
 * @author Wesley Gong
 */
public class ExpandoRowLocalServiceImpl extends ExpandoRowLocalServiceBaseImpl {

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

	@BeanReference(type = ExpandoTableLocalService.class)
	private ExpandoTableLocalService _expandoTableLocalService;

	@BeanReference(type = ExpandoValueLocalService.class)
	private ExpandoValueLocalService _expandoValueLocalService;

}