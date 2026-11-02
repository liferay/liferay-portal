/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.expando.service.impl;

import com.liferay.expando.kernel.exception.DuplicateTableNameException;
import com.liferay.expando.kernel.exception.TableNameException;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.persistence.ExpandoRowPersistence;
import com.liferay.expando.kernel.service.persistence.ExpandoValuePersistence;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.expando.service.base.ExpandoTableLocalServiceBaseImpl;

import java.util.List;

/**
 * @author Raymond Augé
 * @author Brian Wing Shun Chan
 */
@CTAware(onProduction = true)
public class ExpandoTableLocalServiceImpl
	extends ExpandoTableLocalServiceBaseImpl {

	@Override
	public ExpandoTable addDefaultTable(long companyId, long classNameId)
		throws PortalException {

		return addTable(
			companyId, classNameId, ExpandoTableConstants.DEFAULT_TABLE_NAME);
	}

	@Override
	public ExpandoTable addDefaultTable(long companyId, String className)
		throws PortalException {

		return addTable(
			companyId, className, ExpandoTableConstants.DEFAULT_TABLE_NAME);
	}

	@Override
	public ExpandoTable addTable(long companyId, long classNameId, String name)
		throws PortalException {

		ExpandoTable expandoTable = expandoTablePersistence.fetchByC_C_N(
			companyId, classNameId, name);

		if (expandoTable != null) {
			return expandoTable;
		}

		validate(companyId, 0, classNameId, name);

		long tableId = counterLocalService.increment();

		expandoTable = expandoTablePersistence.create(tableId);

		expandoTable.setCompanyId(companyId);
		expandoTable.setClassNameId(classNameId);
		expandoTable.setName(name);

		return expandoTablePersistence.update(expandoTable);
	}

	@Override
	public ExpandoTable addTable(long companyId, String className, String name)
		throws PortalException {

		return addTable(
			companyId, _classNameLocalService.getClassNameId(className), name);
	}

	@Override
	public ExpandoTable deleteExpandoTable(ExpandoTable expandoTable)
		throws PortalException {

		deleteTable(expandoTable);

		return expandoTable;
	}

	@Override
	public void deleteTable(ExpandoTable table) throws PortalException {

		// Values

		_expandoValuePersistence.removeByTableId(table.getTableId());

		// Rows

		_expandoRowPersistence.removeByTableId(table.getTableId());

		// Columns

		_expandoColumnLocalService.deleteColumns(table.getTableId());

		// Table

		expandoTablePersistence.remove(table);
	}

	@Override
	public void deleteTable(long tableId) throws PortalException {
		ExpandoTable table = expandoTablePersistence.findByPrimaryKey(tableId);

		deleteTable(table);
	}

	@Override
	public void deleteTable(long companyId, long classNameId, String name)
		throws PortalException {

		ExpandoTable table = expandoTablePersistence.findByC_C_N(
			companyId, classNameId, name);

		deleteTable(table);
	}

	@Override
	public void deleteTable(long companyId, String className, String name)
		throws PortalException {

		deleteTable(
			companyId, _classNameLocalService.getClassNameId(className), name);
	}

	@Override
	public void deleteTables(long companyId, long classNameId)
		throws PortalException {

		List<ExpandoTable> tables = expandoTablePersistence.findByC_C(
			companyId, classNameId);

		for (ExpandoTable table : tables) {
			deleteTable(table);
		}
	}

	@Override
	public void deleteTables(long companyId, String className)
		throws PortalException {

		deleteTables(
			companyId, _classNameLocalService.getClassNameId(className));
	}

	@Override
	public ExpandoTable fetchDefaultTable(long companyId, long classNameId) {
		return fetchTable(
			companyId, classNameId, ExpandoTableConstants.DEFAULT_TABLE_NAME);
	}

	@Override
	public ExpandoTable fetchDefaultTable(long companyId, String className) {
		return fetchTable(
			companyId, _classNameLocalService.getClassNameId(className),
			ExpandoTableConstants.DEFAULT_TABLE_NAME);
	}

	@Override
	public ExpandoTable fetchTable(
		long companyId, long classNameId, String name) {

		return expandoTablePersistence.fetchByC_C_N(
			companyId, classNameId, name);
	}

	@Override
	public ExpandoTable getDefaultTable(long companyId, long classNameId)
		throws PortalException {

		return getTable(
			companyId, classNameId, ExpandoTableConstants.DEFAULT_TABLE_NAME);
	}

	@Override
	public ExpandoTable getDefaultTable(long companyId, String className)
		throws PortalException {

		return getTable(
			companyId, _classNameLocalService.getClassNameId(className),
			ExpandoTableConstants.DEFAULT_TABLE_NAME);
	}

	@Override
	public ExpandoTable getTable(long tableId) throws PortalException {
		return expandoTablePersistence.findByPrimaryKey(tableId);
	}

	@Override
	public ExpandoTable getTable(long companyId, long classNameId, String name)
		throws PortalException {

		return expandoTablePersistence.findByC_C_N(
			companyId, classNameId, name);
	}

	@Override
	public ExpandoTable getTable(long companyId, String className, String name)
		throws PortalException {

		return getTable(
			companyId, _classNameLocalService.getClassNameId(className), name);
	}

	@Override
	public List<ExpandoTable> getTables(long companyId, long classNameId) {
		return expandoTablePersistence.findByC_C(companyId, classNameId);
	}

	@Override
	public List<ExpandoTable> getTables(long companyId, String className) {
		return getTables(
			companyId, _classNameLocalService.getClassNameId(className));
	}

	protected void validate(
			long companyId, long tableId, long classNameId, String name)
		throws PortalException {

		if (Validator.isNull(name)) {
			throw new TableNameException("Name is null");
		}

		ExpandoTable table = expandoTablePersistence.fetchByC_C_N(
			companyId, classNameId, name);

		if ((table != null) && (table.getTableId() != tableId)) {
			throw new DuplicateTableNameException("{tableId=" + tableId + "}");
		}
	}

	@BeanReference(type = ClassNameLocalService.class)
	private ClassNameLocalService _classNameLocalService;

	@BeanReference(type = ExpandoColumnLocalService.class)
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@BeanReference(type = ExpandoRowPersistence.class)
	private ExpandoRowPersistence _expandoRowPersistence;

	@BeanReference(type = ExpandoValuePersistence.class)
	private ExpandoValuePersistence _expandoValuePersistence;

}