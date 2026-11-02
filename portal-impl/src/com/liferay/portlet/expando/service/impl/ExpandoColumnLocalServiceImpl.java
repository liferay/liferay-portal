/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.expando.service.impl;

import com.liferay.expando.kernel.exception.ColumnNameException;
import com.liferay.expando.kernel.exception.ColumnTypeException;
import com.liferay.expando.kernel.exception.DuplicateColumnNameException;
import com.liferay.expando.kernel.exception.ValueDataException;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.kernel.model.ExpandoValue;
import com.liferay.expando.kernel.model.adapter.StagedExpandoColumn;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.expando.kernel.service.persistence.ExpandoTablePersistence;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.SystemEventLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.adapter.util.ModelAdapterUtil;
import com.liferay.portlet.expando.model.impl.ExpandoValueImpl;
import com.liferay.portlet.expando.service.base.ExpandoColumnLocalServiceBaseImpl;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Raymond Augé
 * @author Brian Wing Shun Chan
 */
public class ExpandoColumnLocalServiceImpl
	extends ExpandoColumnLocalServiceBaseImpl {

	@Override
	public ExpandoColumn addColumn(long tableId, String name, int type)
		throws PortalException {

		return addColumn(tableId, name, type, null);
	}

	@Override
	public ExpandoColumn addColumn(
			long tableId, String name, int type, Object defaultData)
		throws PortalException {

		// Column

		ExpandoTable table = _expandoTablePersistence.findByPrimaryKey(tableId);

		ExpandoValue value = validate(0, tableId, name, type, defaultData);

		long columnId = counterLocalService.increment();

		ExpandoColumn column = expandoColumnPersistence.create(columnId);

		column.setCompanyId(table.getCompanyId());
		column.setTableId(tableId);
		column.setName(name);
		column.setType(type);
		column.setDefaultData(value.getData());

		column = expandoColumnPersistence.update(column);

		// Resources

		_resourceLocalService.addResources(
			table.getCompanyId(), 0, 0, ExpandoColumn.class.getName(),
			column.getColumnId(), false, false, false);

		return column;
	}

	@Override
	public void deleteColumn(ExpandoColumn column) throws PortalException {
		addDeletionSystemEvent(column);

		expandoColumnPersistence.remove(column);

		_resourceLocalService.deleteResource(
			column.getCompanyId(), ExpandoColumn.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, column.getColumnId());

		_expandoValueLocalService.deleteColumnValues(column.getColumnId());
	}

	@Override
	public void deleteColumn(long columnId) throws PortalException {
		ExpandoColumn column = expandoColumnPersistence.findByPrimaryKey(
			columnId);

		deleteColumn(column);
	}

	@Override
	public void deleteColumn(
			long companyId, long classNameId, String tableName, String name)
		throws PortalException {

		ExpandoTable table = _expandoTablePersistence.findByC_C_N(
			companyId, classNameId, tableName);

		deleteColumn(table.getTableId(), name);
	}

	@Override
	public void deleteColumn(long tableId, String name) throws PortalException {
		ExpandoColumn column = expandoColumnPersistence.fetchByT_N(
			tableId, name);

		if (column != null) {
			deleteColumn(column);
		}
	}

	@Override
	public void deleteColumn(
			long companyId, String className, String tableName, String name)
		throws PortalException {

		deleteColumn(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName, name);
	}

	@Override
	public void deleteColumns(long tableId) throws PortalException {
		List<ExpandoColumn> columns = expandoColumnPersistence.findByTableId(
			tableId);

		for (ExpandoColumn column : columns) {
			deleteColumn(column);
		}
	}

	@Override
	public ExpandoColumn fetchColumn(
		long companyId, long classNameId, String tableName, String name) {

		ExpandoTable table = _expandoTablePersistence.fetchByC_C_N(
			companyId, classNameId, tableName);

		if (table == null) {
			return null;
		}

		return expandoColumnPersistence.fetchByT_N(table.getTableId(), name);
	}

	@Override
	public ExpandoColumn fetchColumn(long tableId, String name) {
		return expandoColumnPersistence.fetchByT_N(tableId, name);
	}

	@Override
	public ExpandoColumn getColumn(long columnId) throws PortalException {
		return expandoColumnPersistence.findByPrimaryKey(columnId);
	}

	@Override
	public ExpandoColumn getColumn(
			long companyId, long classNameId, String tableName, String name)
		throws PortalException {

		ExpandoTable table = _expandoTablePersistence.findByC_C_N(
			companyId, classNameId, tableName);

		return expandoColumnPersistence.findByT_N(table.getTableId(), name);
	}

	@Override
	public ExpandoColumn getColumn(long tableId, String name)
		throws PortalException {

		return expandoColumnPersistence.findByT_N(tableId, name);
	}

	@Override
	public ExpandoColumn getColumn(
		long companyId, String className, String tableName, String name) {

		return fetchColumn(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName, name);
	}

	@Override
	public List<ExpandoColumn> getColumns(long tableId) {
		return expandoColumnPersistence.findByTableId(tableId);
	}

	@Override
	public List<ExpandoColumn> getColumns(
		long companyId, long classNameId, String tableName) {

		ExpandoTable table = _expandoTablePersistence.fetchByC_C_N(
			companyId, classNameId, tableName);

		if (table == null) {
			return Collections.emptyList();
		}

		return expandoColumnPersistence.findByTableId(table.getTableId());
	}

	@Override
	public List<ExpandoColumn> getColumns(
		long companyId, String className, String tableName) {

		return getColumns(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);
	}

	@Override
	public int getColumnsCount(long tableId) {
		return expandoColumnPersistence.countByTableId(tableId);
	}

	@Override
	public ExpandoColumn getDefaultTableColumn(
		long companyId, long classNameId, String name) {

		return fetchColumn(
			companyId, classNameId, ExpandoTableConstants.DEFAULT_TABLE_NAME,
			name);
	}

	@Override
	public ExpandoColumn getDefaultTableColumn(
		long companyId, String className, String name) {

		return fetchColumn(
			companyId, _classNameLocalService.getClassNameId(className),
			ExpandoTableConstants.DEFAULT_TABLE_NAME, name);
	}

	@Override
	public List<ExpandoColumn> getDefaultTableColumns(
		long companyId, long classNameId) {

		ExpandoTable table = _expandoTablePersistence.fetchByC_C_N(
			companyId, classNameId, ExpandoTableConstants.DEFAULT_TABLE_NAME);

		if (table == null) {
			return Collections.emptyList();
		}

		return expandoColumnPersistence.findByTableId(table.getTableId());
	}

	@Override
	public List<ExpandoColumn> getDefaultTableColumns(
		long companyId, String className) {

		return getColumns(
			companyId, _classNameLocalService.getClassNameId(className),
			ExpandoTableConstants.DEFAULT_TABLE_NAME);
	}

	@Override
	public ExpandoColumn updateColumn(long columnId, String name, int type)
		throws PortalException {

		return expandoColumnLocalService.updateColumn(
			columnId, name, type, null);
	}

	@Override
	public ExpandoColumn updateColumn(
			long columnId, String name, int type, Object defaultData)
		throws PortalException {

		ExpandoColumn column = expandoColumnPersistence.findByPrimaryKey(
			columnId);

		ExpandoValue value = validate(
			columnId, column.getTableId(), name, type, defaultData);

		column.setName(name);
		column.setType(type);
		column.setDefaultData(value.getData());

		return expandoColumnPersistence.update(column);
	}

	@Override
	public ExpandoColumn updateTypeSettings(long columnId, String typeSettings)
		throws PortalException {

		ExpandoColumn column = expandoColumnPersistence.findByPrimaryKey(
			columnId);

		column.setTypeSettings(typeSettings);

		return expandoColumnPersistence.update(column);
	}

	protected void addDeletionSystemEvent(ExpandoColumn expandoColumn) {
		StagedExpandoColumn stagedExpandoColumn = ModelAdapterUtil.adapt(
			expandoColumn, ExpandoColumn.class, StagedExpandoColumn.class);

		StagedModelType stagedModelType =
			stagedExpandoColumn.getStagedModelType();

		JSONObject extraDataJSONObject = JSONUtil.put(
			"companyId", stagedExpandoColumn.getCompanyId()
		).put(
			"uuid", stagedExpandoColumn.getUuid()
		);

		try {
			_systemEventLocalService.addSystemEvent(
				stagedExpandoColumn.getCompanyId(), StringPool.BLANK,
				stagedModelType.getClassName(),
				stagedExpandoColumn.getPrimaryKey(), StringPool.BLANK, null,
				SystemEventConstants.TYPE_DELETE,
				extraDataJSONObject.toString());
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	protected ExpandoValue validate(
			long columnId, long tableId, String name, int type,
			Object defaultData)
		throws PortalException {

		if (Validator.isNull(name)) {
			throw new ColumnNameException("Name is null");
		}

		ExpandoColumn column = expandoColumnPersistence.fetchByT_N(
			tableId, name);

		if ((column != null) && (column.getColumnId() != columnId)) {
			throw new DuplicateColumnNameException(
				StringBundler.concat(
					"{tableId=", tableId, ", columnId=", columnId, ", name=",
					name, "}"));
		}

		if ((type != ExpandoColumnConstants.BOOLEAN) &&
			(type != ExpandoColumnConstants.BOOLEAN_ARRAY) &&
			(type != ExpandoColumnConstants.DATE) &&
			(type != ExpandoColumnConstants.DATE_ARRAY) &&
			(type != ExpandoColumnConstants.DOUBLE) &&
			(type != ExpandoColumnConstants.DOUBLE_ARRAY) &&
			(type != ExpandoColumnConstants.FLOAT) &&
			(type != ExpandoColumnConstants.FLOAT_ARRAY) &&
			(type != ExpandoColumnConstants.GEOLOCATION) &&
			(type != ExpandoColumnConstants.INTEGER) &&
			(type != ExpandoColumnConstants.INTEGER_ARRAY) &&
			(type != ExpandoColumnConstants.LONG) &&
			(type != ExpandoColumnConstants.LONG_ARRAY) &&
			(type != ExpandoColumnConstants.NUMBER) &&
			(type != ExpandoColumnConstants.NUMBER_ARRAY) &&
			(type != ExpandoColumnConstants.SHORT) &&
			(type != ExpandoColumnConstants.SHORT_ARRAY) &&
			(type != ExpandoColumnConstants.STRING) &&
			(type != ExpandoColumnConstants.STRING_ARRAY) &&
			(type != ExpandoColumnConstants.STRING_ARRAY_LOCALIZED) &&
			(type != ExpandoColumnConstants.STRING_LOCALIZED)) {

			throw new ColumnTypeException("Invalid type " + type);
		}

		ExpandoValue value = new ExpandoValueImpl();

		if (defaultData == null) {
			return value;
		}

		value.setColumnId(columnId);

		if (type == ExpandoColumnConstants.BOOLEAN) {
			value.setBoolean((Boolean)defaultData);
		}
		else if (type == ExpandoColumnConstants.BOOLEAN_ARRAY) {
			value.setBooleanArray((boolean[])defaultData);
		}
		else if (type == ExpandoColumnConstants.DATE) {
			value.setDate((Date)defaultData);
		}
		else if (type == ExpandoColumnConstants.DATE_ARRAY) {
			value.setDateArray((Date[])defaultData);
		}
		else if (type == ExpandoColumnConstants.DOUBLE) {
			value.setDouble((Double)defaultData);
		}
		else if (type == ExpandoColumnConstants.DOUBLE_ARRAY) {
			value.setDoubleArray((double[])defaultData);
		}
		else if (type == ExpandoColumnConstants.FLOAT) {
			value.setFloat((Float)defaultData);
		}
		else if (type == ExpandoColumnConstants.FLOAT_ARRAY) {
			value.setFloatArray((float[])defaultData);
		}
		else if (type == ExpandoColumnConstants.GEOLOCATION) {
			value.setGeolocationJSONObject((JSONObject)defaultData);
		}
		else if (type == ExpandoColumnConstants.INTEGER) {
			value.setInteger((Integer)defaultData);
		}
		else if (type == ExpandoColumnConstants.INTEGER_ARRAY) {
			value.setIntegerArray((int[])defaultData);
		}
		else if (type == ExpandoColumnConstants.LONG) {
			value.setLong((Long)defaultData);
		}
		else if (type == ExpandoColumnConstants.LONG_ARRAY) {
			value.setLongArray((long[])defaultData);
		}
		else if (type == ExpandoColumnConstants.NUMBER) {
			value.setNumber((Number)defaultData);
		}
		else if (type == ExpandoColumnConstants.NUMBER_ARRAY) {
			value.setNumberArray((Number[])defaultData);
		}
		else if (type == ExpandoColumnConstants.SHORT) {
			value.setShort((Short)defaultData);
		}
		else if (type == ExpandoColumnConstants.SHORT_ARRAY) {
			value.setShortArray((short[])defaultData);
		}
		else if (type == ExpandoColumnConstants.STRING) {
			value.setString((String)defaultData);
		}
		else if (type == ExpandoColumnConstants.STRING_ARRAY) {
			value.setStringArray((String[])defaultData);
		}
		else if (type == ExpandoColumnConstants.STRING_ARRAY_LOCALIZED) {
			value.setStringArrayMap(
				(Map<Locale, String[]>)defaultData, LocaleUtil.getDefault());
		}
		else if (type == ExpandoColumnConstants.STRING_LOCALIZED) {
			Map<Locale, String> defaultValuesMap =
				(Map<Locale, String>)defaultData;

			Locale defaultLocale = LocaleUtil.getDefault();

			if (Validator.isNull(defaultValuesMap.get(defaultLocale))) {
				for (String defaultValue : defaultValuesMap.values()) {
					if (Validator.isNotNull(defaultValue)) {
						throw new ValueDataException.MustInformDefaultLocale(
							defaultLocale);
					}
				}
			}

			value.setStringMap(defaultValuesMap, defaultLocale);
		}

		return value;
	}

	@BeanReference(type = ClassNameLocalService.class)
	private ClassNameLocalService _classNameLocalService;

	@BeanReference(type = ExpandoTablePersistence.class)
	private ExpandoTablePersistence _expandoTablePersistence;

	@BeanReference(type = ExpandoValueLocalService.class)
	private ExpandoValueLocalService _expandoValueLocalService;

	@BeanReference(type = ResourceLocalService.class)
	private ResourceLocalService _resourceLocalService;

	@BeanReference(type = SystemEventLocalService.class)
	private SystemEventLocalService _systemEventLocalService;

}