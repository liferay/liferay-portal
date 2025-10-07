/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.expando.service.impl;

import com.liferay.expando.kernel.exception.ValueDataException;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoRow;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.kernel.model.ExpandoValue;
import com.liferay.expando.kernel.service.persistence.ExpandoColumnPersistence;
import com.liferay.expando.kernel.service.persistence.ExpandoRowPersistence;
import com.liferay.expando.kernel.service.persistence.ExpandoTablePersistence;
import com.liferay.expando.kernel.util.ExpandoValueDeleteHandler;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.typeconverter.TypeConverterUtil;
import com.liferay.portlet.expando.model.impl.ExpandoValueImpl;
import com.liferay.portlet.expando.service.base.ExpandoValueLocalServiceBaseImpl;

import java.io.Serializable;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Raymond Augé
 * @author Brian Wing Shun Chan
 * @author Marcellus Tavares
 */
public class ExpandoValueLocalServiceImpl
	extends ExpandoValueLocalServiceBaseImpl {

	@Override
	public ExpandoValue addValue(
			long classNameId, long tableId, long columnId, long classPK,
			String data)
		throws PortalException {

		ExpandoTable table = _expandoTablePersistence.findByPrimaryKey(tableId);

		return doAddValue(
			table.getCompanyId(), classNameId, tableId, columnId, classPK,
			data);
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, boolean data)
		throws PortalException {

		ExpandoTable table = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn column = _expandoColumnPersistence.findByT_N(
			table.getTableId(), columnName);

		ExpandoValue value = new ExpandoValueImpl();

		value.setCompanyId(table.getCompanyId());
		value.setColumnId(column.getColumnId());
		value.setBoolean(data);

		return expandoValueLocalService.addValue(
			table.getClassNameId(), table.getTableId(), column.getColumnId(),
			classPK, value.getData());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, boolean[] data)
		throws PortalException {

		ExpandoTable table = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn column = _expandoColumnPersistence.findByT_N(
			table.getTableId(), columnName);

		ExpandoValue value = new ExpandoValueImpl();

		value.setCompanyId(table.getCompanyId());
		value.setColumnId(column.getColumnId());
		value.setBooleanArray(data);

		return expandoValueLocalService.addValue(
			table.getClassNameId(), table.getTableId(), column.getColumnId(),
			classPK, value.getData());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, Date data)
		throws PortalException {

		ExpandoTable table = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn column = _expandoColumnPersistence.findByT_N(
			table.getTableId(), columnName);

		ExpandoValue value = new ExpandoValueImpl();

		value.setCompanyId(table.getCompanyId());
		value.setColumnId(column.getColumnId());
		value.setDate(data);

		return expandoValueLocalService.addValue(
			table.getClassNameId(), table.getTableId(), column.getColumnId(),
			classPK, value.getData());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, Date[] data)
		throws PortalException {

		ExpandoTable table = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn column = _expandoColumnPersistence.findByT_N(
			table.getTableId(), columnName);

		ExpandoValue value = new ExpandoValueImpl();

		value.setCompanyId(table.getCompanyId());
		value.setColumnId(column.getColumnId());
		value.setDateArray(data);

		return expandoValueLocalService.addValue(
			table.getClassNameId(), table.getTableId(), column.getColumnId(),
			classPK, value.getData());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, double data)
		throws PortalException {

		ExpandoTable table = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn column = _expandoColumnPersistence.findByT_N(
			table.getTableId(), columnName);

		ExpandoValue value = new ExpandoValueImpl();

		value.setCompanyId(table.getCompanyId());
		value.setColumnId(column.getColumnId());
		value.setDouble(data);

		return expandoValueLocalService.addValue(
			table.getClassNameId(), table.getTableId(), column.getColumnId(),
			classPK, value.getData());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, double[] data)
		throws PortalException {

		ExpandoTable table = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn column = _expandoColumnPersistence.findByT_N(
			table.getTableId(), columnName);

		ExpandoValue value = new ExpandoValueImpl();

		value.setCompanyId(table.getCompanyId());
		value.setColumnId(column.getColumnId());
		value.setDoubleArray(data);

		return expandoValueLocalService.addValue(
			table.getClassNameId(), table.getTableId(), column.getColumnId(),
			classPK, value.getData());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, float data)
		throws PortalException {

		ExpandoTable table = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn column = _expandoColumnPersistence.findByT_N(
			table.getTableId(), columnName);

		ExpandoValue value = new ExpandoValueImpl();

		value.setCompanyId(table.getCompanyId());
		value.setColumnId(column.getColumnId());
		value.setFloat(data);

		return expandoValueLocalService.addValue(
			table.getClassNameId(), table.getTableId(), column.getColumnId(),
			classPK, value.getData());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, float[] data)
		throws PortalException {

		ExpandoTable table = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn column = _expandoColumnPersistence.findByT_N(
			table.getTableId(), columnName);

		ExpandoValue value = new ExpandoValueImpl();

		value.setCompanyId(table.getCompanyId());
		value.setColumnId(column.getColumnId());
		value.setFloatArray(data);

		return expandoValueLocalService.addValue(
			table.getClassNameId(), table.getTableId(), column.getColumnId(),
			classPK, value.getData());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, int data)
		throws PortalException {

		ExpandoTable table = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn column = _expandoColumnPersistence.findByT_N(
			table.getTableId(), columnName);

		ExpandoValue value = new ExpandoValueImpl();

		value.setCompanyId(table.getCompanyId());
		value.setColumnId(column.getColumnId());
		value.setInteger(data);

		return expandoValueLocalService.addValue(
			table.getClassNameId(), table.getTableId(), column.getColumnId(),
			classPK, value.getData());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, int[] data)
		throws PortalException {

		ExpandoTable table = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn column = _expandoColumnPersistence.findByT_N(
			table.getTableId(), columnName);

		ExpandoValue value = new ExpandoValueImpl();

		value.setCompanyId(table.getCompanyId());
		value.setColumnId(column.getColumnId());
		value.setIntegerArray(data);

		return expandoValueLocalService.addValue(
			table.getClassNameId(), table.getTableId(), column.getColumnId(),
			classPK, value.getData());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, JSONObject dataJSONObject)
		throws PortalException {

		ExpandoTable table = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn column = _expandoColumnPersistence.findByT_N(
			table.getTableId(), columnName);

		ExpandoValue value = new ExpandoValueImpl();

		value.setCompanyId(table.getCompanyId());
		value.setColumnId(column.getColumnId());
		value.setGeolocationJSONObject(dataJSONObject);

		return expandoValueLocalService.addValue(
			table.getClassNameId(), table.getTableId(), column.getColumnId(),
			classPK, value.getData());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, long data)
		throws PortalException {

		ExpandoTable table = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn column = _expandoColumnPersistence.findByT_N(
			table.getTableId(), columnName);

		ExpandoValue value = new ExpandoValueImpl();

		value.setCompanyId(table.getCompanyId());
		value.setColumnId(column.getColumnId());
		value.setLong(data);

		return expandoValueLocalService.addValue(
			table.getClassNameId(), table.getTableId(), column.getColumnId(),
			classPK, value.getData());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, long[] data)
		throws PortalException {

		ExpandoTable table = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn column = _expandoColumnPersistence.findByT_N(
			table.getTableId(), columnName);

		ExpandoValue value = new ExpandoValueImpl();

		value.setCompanyId(table.getCompanyId());
		value.setColumnId(column.getColumnId());
		value.setLongArray(data);

		return expandoValueLocalService.addValue(
			table.getClassNameId(), table.getTableId(), column.getColumnId(),
			classPK, value.getData());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, Map<Locale, ?> dataMap,
			Locale defaultLocale)
		throws PortalException {

		ExpandoTable table = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn column = _expandoColumnPersistence.findByT_N(
			table.getTableId(), columnName);

		ExpandoValue value = new ExpandoValueImpl();

		value.setCompanyId(table.getCompanyId());
		value.setColumnId(column.getColumnId());

		int type = column.getType();

		if (type == ExpandoColumnConstants.STRING_ARRAY_LOCALIZED) {
			value.setStringArrayMap(
				(Map<Locale, String[]>)dataMap, defaultLocale);
		}
		else {
			value.setStringMap((Map<Locale, String>)dataMap, defaultLocale);
		}

		return expandoValueLocalService.addValue(
			table.getClassNameId(), table.getTableId(), column.getColumnId(),
			classPK, value.getData());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, Number data)
		throws PortalException {

		ExpandoTable table = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn column = _expandoColumnPersistence.findByT_N(
			table.getTableId(), columnName);

		ExpandoValue value = new ExpandoValueImpl();

		value.setCompanyId(table.getCompanyId());
		value.setColumnId(column.getColumnId());
		value.setNumber(data);

		return expandoValueLocalService.addValue(
			table.getClassNameId(), table.getTableId(), column.getColumnId(),
			classPK, value.getData());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, Number[] data)
		throws PortalException {

		ExpandoTable table = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn column = _expandoColumnPersistence.findByT_N(
			table.getTableId(), columnName);

		ExpandoValue value = new ExpandoValueImpl();

		value.setCompanyId(table.getCompanyId());
		value.setColumnId(column.getColumnId());
		value.setNumberArray(data);

		return expandoValueLocalService.addValue(
			table.getClassNameId(), table.getTableId(), column.getColumnId(),
			classPK, value.getData());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, Object data)
		throws PortalException {

		ExpandoTable table = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn column = _expandoColumnPersistence.findByT_N(
			table.getTableId(), columnName);

		int type = column.getType();

		data = convertType(type, data);

		if (type == ExpandoColumnConstants.BOOLEAN) {
			Boolean booleanData = (Boolean)data;

			return expandoValueLocalService.addValue(
				companyId, className, tableName, columnName, classPK,
				booleanData.booleanValue());
		}
		else if (type == ExpandoColumnConstants.BOOLEAN_ARRAY) {
			return expandoValueLocalService.addValue(
				companyId, className, tableName, columnName, classPK,
				(boolean[])data);
		}
		else if (type == ExpandoColumnConstants.DATE) {
			return expandoValueLocalService.addValue(
				companyId, className, tableName, columnName, classPK,
				(Date)data);
		}
		else if (type == ExpandoColumnConstants.DATE_ARRAY) {
			return expandoValueLocalService.addValue(
				companyId, className, tableName, columnName, classPK,
				(Date[])data);
		}
		else if (type == ExpandoColumnConstants.DOUBLE) {
			Double doubleData = (Double)data;

			return expandoValueLocalService.addValue(
				companyId, className, tableName, columnName, classPK,
				doubleData.doubleValue());
		}
		else if (type == ExpandoColumnConstants.DOUBLE_ARRAY) {
			return expandoValueLocalService.addValue(
				companyId, className, tableName, columnName, classPK,
				(double[])data);
		}
		else if (type == ExpandoColumnConstants.FLOAT) {
			Float floatData = (Float)data;

			return expandoValueLocalService.addValue(
				companyId, className, tableName, columnName, classPK,
				floatData.floatValue());
		}
		else if (type == ExpandoColumnConstants.FLOAT_ARRAY) {
			return expandoValueLocalService.addValue(
				companyId, className, tableName, columnName, classPK,
				(float[])data);
		}
		else if (type == ExpandoColumnConstants.GEOLOCATION) {
			return expandoValueLocalService.addValue(
				companyId, className, tableName, columnName, classPK,
				JSONFactoryUtil.createJSONObject(
					HtmlUtil.unescape(data.toString())));
		}
		else if (type == ExpandoColumnConstants.INTEGER) {
			Integer integerData = (Integer)data;

			return expandoValueLocalService.addValue(
				companyId, className, tableName, columnName, classPK,
				integerData.intValue());
		}
		else if (type == ExpandoColumnConstants.INTEGER_ARRAY) {
			return expandoValueLocalService.addValue(
				companyId, className, tableName, columnName, classPK,
				(int[])data);
		}
		else if (type == ExpandoColumnConstants.LONG) {
			Long longData = (Long)data;

			return expandoValueLocalService.addValue(
				companyId, className, tableName, columnName, classPK,
				longData.longValue());
		}
		else if (type == ExpandoColumnConstants.LONG_ARRAY) {
			return expandoValueLocalService.addValue(
				companyId, className, tableName, columnName, classPK,
				(long[])data);
		}
		else if (type == ExpandoColumnConstants.NUMBER) {
			return expandoValueLocalService.addValue(
				companyId, className, tableName, columnName, classPK,
				(Number)data);
		}
		else if (type == ExpandoColumnConstants.NUMBER_ARRAY) {
			return expandoValueLocalService.addValue(
				companyId, className, tableName, columnName, classPK,
				(Number[])data);
		}
		else if (type == ExpandoColumnConstants.SHORT) {
			Short shortData = (Short)data;

			return expandoValueLocalService.addValue(
				companyId, className, tableName, columnName, classPK,
				shortData.shortValue());
		}
		else if (type == ExpandoColumnConstants.SHORT_ARRAY) {
			return expandoValueLocalService.addValue(
				companyId, className, tableName, columnName, classPK,
				(short[])data);
		}
		else if (type == ExpandoColumnConstants.STRING_ARRAY) {
			return expandoValueLocalService.addValue(
				companyId, className, tableName, columnName, classPK,
				(String[])data);
		}
		else if (type == ExpandoColumnConstants.STRING) {
			return expandoValueLocalService.addValue(
				companyId, className, tableName, columnName, classPK,
				(String)data);
		}

		return expandoValueLocalService.addValue(
			companyId, className, tableName, columnName, classPK,
			(Map<Locale, ?>)data, LocaleUtil.getSiteDefault());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, short data)
		throws PortalException {

		ExpandoTable table = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn column = _expandoColumnPersistence.findByT_N(
			table.getTableId(), columnName);

		ExpandoValue value = new ExpandoValueImpl();

		value.setCompanyId(table.getCompanyId());
		value.setColumnId(column.getColumnId());
		value.setShort(data);

		return expandoValueLocalService.addValue(
			table.getClassNameId(), table.getTableId(), column.getColumnId(),
			classPK, value.getData());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, short[] data)
		throws PortalException {

		ExpandoTable table = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn column = _expandoColumnPersistence.findByT_N(
			table.getTableId(), columnName);

		ExpandoValue value = new ExpandoValueImpl();

		value.setCompanyId(table.getCompanyId());
		value.setColumnId(column.getColumnId());
		value.setShortArray(data);

		return expandoValueLocalService.addValue(
			table.getClassNameId(), table.getTableId(), column.getColumnId(),
			classPK, value.getData());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, String data)
		throws PortalException {

		ExpandoTable table = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn column = _expandoColumnPersistence.findByT_N(
			table.getTableId(), columnName);

		ExpandoValue value = new ExpandoValueImpl();

		value.setCompanyId(table.getCompanyId());
		value.setColumnId(column.getColumnId());
		value.setString(data);

		return expandoValueLocalService.addValue(
			table.getClassNameId(), table.getTableId(), column.getColumnId(),
			classPK, value.getData());
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, String[] data)
		throws PortalException {

		ExpandoTable table = _expandoTablePersistence.findByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		ExpandoColumn column = _expandoColumnPersistence.findByT_N(
			table.getTableId(), columnName);

		ExpandoValue value = new ExpandoValueImpl();

		value.setCompanyId(table.getCompanyId());
		value.setColumnId(column.getColumnId());
		value.setStringArray(data);

		return expandoValueLocalService.addValue(
			table.getClassNameId(), table.getTableId(), column.getColumnId(),
			classPK, value.getData());
	}

	@Override
	public void addValues(
			long classNameId, long tableId, List<ExpandoColumn> columns,
			long classPK, Map<String, String> data)
		throws PortalException {

		ExpandoTable table = _expandoTablePersistence.findByPrimaryKey(tableId);

		ExpandoRow row = _expandoRowPersistence.fetchByT_C(tableId, classPK);

		if (row == null) {
			long rowId = counterLocalService.increment();

			row = _expandoRowPersistence.create(rowId);

			row.setCompanyId(table.getCompanyId());
			row.setTableId(tableId);
			row.setClassPK(classPK);

			row = _expandoRowPersistence.update(row);
		}

		boolean rowModified = false;

		for (ExpandoColumn column : columns) {
			String dataString = data.get(column.getName());

			if (dataString == null) {
				continue;
			}

			ExpandoValue value = expandoValuePersistence.fetchByC_R(
				column.getColumnId(), row.getRowId());

			if (value == null) {
				long valueId = counterLocalService.increment();

				value = expandoValuePersistence.create(valueId);

				value.setCompanyId(table.getCompanyId());
				value.setTableId(tableId);
				value.setColumnId(column.getColumnId());
				value.setRowId(row.getRowId());
				value.setClassNameId(classNameId);
				value.setClassPK(classPK);
			}

			if (value.isNew() || !Objects.equals(value.getData(), dataString)) {
				value.setData(dataString);

				expandoValuePersistence.update(value);

				rowModified = true;
			}
		}

		if (rowModified) {
			row.setModifiedDate(new Date());

			_expandoRowPersistence.update(row);
		}
	}

	@Override
	public void addValues(
			long companyId, long classNameId, String tableName, long classPK,
			Map<String, Serializable> attributes)
		throws PortalException {

		Map<String, String> data = new HashMap<>();

		ExpandoTable table = _expandoTablePersistence.findByC_C_N(
			companyId, classNameId, tableName);

		Set<String> keySet = attributes.keySet();

		List<ExpandoColumn> columns = _expandoColumnPersistence.findByT_N(
			table.getTableId(), keySet.toArray(new String[0]));

		for (ExpandoColumn column : columns) {
			ExpandoValue value = new ExpandoValueImpl();

			value.setCompanyId(companyId);

			Serializable attributeValue = attributes.get(column.getName());

			value.setColumn(column);

			int type = column.getType();

			if (type == ExpandoColumnConstants.BOOLEAN) {
				value.setBoolean((Boolean)attributeValue);
			}
			else if (type == ExpandoColumnConstants.BOOLEAN_ARRAY) {
				value.setBooleanArray((boolean[])attributeValue);
			}
			else if (type == ExpandoColumnConstants.DATE) {
				value.setDate((Date)attributeValue);
			}
			else if (type == ExpandoColumnConstants.DATE_ARRAY) {
				value.setDateArray((Date[])attributeValue);
			}
			else if (type == ExpandoColumnConstants.DOUBLE) {
				value.setDouble((Double)attributeValue);
			}
			else if (type == ExpandoColumnConstants.DOUBLE_ARRAY) {
				value.setDoubleArray((double[])attributeValue);
			}
			else if (type == ExpandoColumnConstants.FLOAT) {
				value.setFloat((Float)attributeValue);
			}
			else if (type == ExpandoColumnConstants.FLOAT_ARRAY) {
				value.setFloatArray((float[])attributeValue);
			}
			else if (type == ExpandoColumnConstants.GEOLOCATION) {
				value.setGeolocationJSONObject(
					JSONFactoryUtil.createJSONObject(
						attributeValue.toString()));
			}
			else if (type == ExpandoColumnConstants.INTEGER) {
				value.setInteger((Integer)attributeValue);
			}
			else if (type == ExpandoColumnConstants.INTEGER_ARRAY) {
				value.setIntegerArray((int[])attributeValue);
			}
			else if (type == ExpandoColumnConstants.LONG) {
				value.setLong((Long)attributeValue);
			}
			else if (type == ExpandoColumnConstants.LONG_ARRAY) {
				value.setLongArray((long[])attributeValue);
			}
			else if (type == ExpandoColumnConstants.NUMBER) {
				value.setNumber((Number)attributeValue);
			}
			else if (type == ExpandoColumnConstants.NUMBER_ARRAY) {
				value.setNumberArray((Number[])attributeValue);
			}
			else if (type == ExpandoColumnConstants.SHORT) {
				value.setShort((Short)attributeValue);
			}
			else if (type == ExpandoColumnConstants.SHORT_ARRAY) {
				value.setShortArray((short[])attributeValue);
			}
			else if (type == ExpandoColumnConstants.STRING_ARRAY) {
				value.setStringArray((String[])attributeValue);
			}
			else if (type == ExpandoColumnConstants.STRING_LOCALIZED) {
				Map<Locale, String> defaultValuesMap =
					(Map<Locale, String>)attributeValue;

				Locale defaultLocale = LocaleUtil.getSiteDefault();

				if ((defaultValuesMap != null) &&
					Validator.isNull(defaultValuesMap.get(defaultLocale))) {

					for (String defaultValue : defaultValuesMap.values()) {
						if (Validator.isNotNull(defaultValue)) {
							throw new ValueDataException.
								MustInformDefaultLocale(defaultLocale);
						}
					}
				}

				value.setStringMap(
					(Map<Locale, String>)attributeValue, defaultLocale);
			}
			else {
				value.setString((String)attributeValue);
			}

			data.put(column.getName(), value.getData());
		}

		addValues(
			table.getClassNameId(), table.getTableId(), columns, classPK, data);
	}

	@Override
	public void addValues(
			long companyId, String className, String tableName, long classPK,
			Map<String, Serializable> attributes)
		throws PortalException {

		addValues(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName, classPK, attributes);
	}

	@Override
	public void deleteColumnValues(long columnId) {
		List<ExpandoValue> values = expandoValuePersistence.findByColumnId(
			columnId);

		for (ExpandoValue value : values) {
			deleteValue(value);
		}
	}

	@Override
	public void deleteRowValues(long rowId) {
		List<ExpandoValue> values = expandoValuePersistence.findByRowId(rowId);

		for (ExpandoValue value : values) {
			deleteValue(value);
		}
	}

	@Override
	public void deleteTableValues(long tableId) {
		List<ExpandoValue> values = expandoValuePersistence.findByTableId(
			tableId);

		for (ExpandoValue value : values) {
			deleteValue(value);
		}
	}

	@Override
	public void deleteValue(ExpandoValue value) {
		expandoValuePersistence.remove(value);

		expandoValuePersistence.flush();

		// Notify delete handlers

		List<ExpandoValueDeleteHandler> expandoValueDeleteHandlers =
			ExpandoValueDeleteHandlerHolder.getService(value.getClassName());

		if (expandoValueDeleteHandlers != null) {
			for (ExpandoValueDeleteHandler expandoValueDeleteHandler :
					expandoValueDeleteHandlers) {

				expandoValueDeleteHandler.deletedExpandoValue(
					value.getClassPK());
			}
		}

		List<ExpandoValue> values = expandoValuePersistence.findByRowId(
			value.getRowId());

		if (values.isEmpty()) {
			ExpandoRow row = _expandoRowPersistence.fetchByPrimaryKey(
				value.getRowId());

			if (row != null) {
				_expandoRowPersistence.remove(row);
			}
		}
	}

	@Override
	public void deleteValue(long valueId) throws PortalException {
		ExpandoValue value = expandoValuePersistence.findByPrimaryKey(valueId);

		deleteValue(value);
	}

	@Override
	public void deleteValue(long columnId, long rowId) throws PortalException {
		ExpandoValue value = expandoValuePersistence.findByC_R(columnId, rowId);

		deleteValue(value);
	}

	@Override
	public void deleteValue(
			long companyId, long classNameId, String tableName,
			String columnName, long classPK)
		throws PortalException {

		ExpandoTable table = _expandoTablePersistence.fetchByC_C_N(
			companyId, classNameId, tableName);

		if (table == null) {
			return;
		}

		ExpandoColumn column = _expandoColumnPersistence.fetchByT_N(
			table.getTableId(), columnName);

		if (column == null) {
			return;
		}

		ExpandoValue value = expandoValuePersistence.fetchByT_C_C(
			table.getTableId(), column.getColumnId(), classPK);

		if (value != null) {
			deleteValue(value.getValueId());
		}
	}

	@Override
	public void deleteValue(
			long companyId, String className, String tableName,
			String columnName, long classPK)
		throws PortalException {

		expandoValueLocalService.deleteValue(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName, columnName, classPK);
	}

	@Override
	public void deleteValues(long classNameId, long classPK) {
		List<ExpandoValue> values = expandoValuePersistence.findByC_C(
			classNameId, classPK);

		for (ExpandoValue value : values) {
			deleteValue(value);
		}
	}

	@Override
	public void deleteValues(String className, long classPK) {
		expandoValueLocalService.deleteValues(
			_classNameLocalService.getClassNameId(className), classPK);
	}

	@Override
	public List<ExpandoValue> getColumnValues(
		long columnId, int start, int end) {

		return expandoValuePersistence.findByColumnId(columnId, start, end);
	}

	@Override
	public List<ExpandoValue> getColumnValues(
		long companyId, long classNameId, String tableName, String columnName,
		int start, int end) {

		return expandoValueLocalService.getColumnValues(
			companyId, classNameId, tableName, columnName, null, start, end);
	}

	@Override
	public List<ExpandoValue> getColumnValues(
		long companyId, long classNameId, String tableName, String columnName,
		String data, int start, int end) {

		ExpandoTable table = _expandoTablePersistence.fetchByC_C_N(
			companyId, classNameId, tableName);

		if (table == null) {
			return Collections.emptyList();
		}

		ExpandoColumn column = _expandoColumnPersistence.fetchByT_N(
			table.getTableId(), columnName);

		if (column == null) {
			return Collections.emptyList();
		}

		if (data == null) {
			return expandoValuePersistence.findByT_C(
				table.getTableId(), column.getColumnId(), start, end);
		}

		return expandoValuePersistence.findByT_C_D(
			table.getTableId(), column.getColumnId(), data, start, end);
	}

	@Override
	public List<ExpandoValue> getColumnValues(
		long companyId, String className, String tableName, String columnName,
		int start, int end) {

		return expandoValueLocalService.getColumnValues(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName, columnName, start, end);
	}

	@Override
	public List<ExpandoValue> getColumnValues(
		long companyId, String className, String tableName, String columnName,
		String data, int start, int end) {

		return expandoValueLocalService.getColumnValues(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName, columnName, data, start, end);
	}

	@Override
	public int getColumnValuesCount(long columnId) {
		return expandoValuePersistence.countByColumnId(columnId);
	}

	@Override
	public int getColumnValuesCount(
		long companyId, long classNameId, String tableName, String columnName) {

		return expandoValueLocalService.getColumnValuesCount(
			companyId, classNameId, tableName, columnName, null);
	}

	@Override
	public int getColumnValuesCount(
		long companyId, long classNameId, String tableName, String columnName,
		String data) {

		ExpandoTable table = _expandoTablePersistence.fetchByC_C_N(
			companyId, classNameId, tableName);

		if (table == null) {
			return 0;
		}

		ExpandoColumn column = _expandoColumnPersistence.fetchByT_N(
			table.getTableId(), columnName);

		if (column == null) {
			return 0;
		}

		if (data == null) {
			return expandoValuePersistence.countByT_C(
				table.getTableId(), column.getColumnId());
		}

		return expandoValuePersistence.countByT_C_D(
			table.getTableId(), column.getColumnId(), data);
	}

	@Override
	public int getColumnValuesCount(
		long companyId, String className, String tableName, String columnName) {

		return expandoValueLocalService.getColumnValuesCount(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName, columnName);
	}

	@Override
	public int getColumnValuesCount(
		long companyId, String className, String tableName, String columnName,
		String data) {

		return expandoValueLocalService.getColumnValuesCount(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName, columnName, data);
	}

	@Override
	public Map<String, Serializable> getData(
			long companyId, String className, String tableName,
			Collection<String> columnNames, long classPK)
		throws PortalException {

		List<ExpandoColumn> columns = Collections.emptyList();

		ExpandoTable table = _expandoTablePersistence.fetchByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		if (table != null) {
			columns = _expandoColumnPersistence.findByT_N(
				table.getTableId(), columnNames.toArray(new String[0]));
		}

		Map<String, Serializable> attributeValues = new HashMap<>(
			(int)(columnNames.size() * 1.4));

		ExpandoValue value = new ExpandoValueImpl();

		for (ExpandoColumn column : columns) {
			value.setColumn(column);
			value.setData(column.getDefaultData());

			Serializable attributeValue = doGetData(
				companyId, className, tableName, column.getName(), classPK,
				value, column.getType());

			attributeValues.put(column.getName(), attributeValue);
		}

		return attributeValues;
	}

	@Override
	public Serializable getData(
			long companyId, String className, String tableName,
			String columnName, long classPK)
		throws PortalException {

		ExpandoTable table = _expandoTablePersistence.fetchByC_C_N(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName);

		if (table == null) {
			return null;
		}

		ExpandoColumn column = _expandoColumnPersistence.fetchByT_N(
			table.getTableId(), columnName);

		if (column == null) {
			return null;
		}

		ExpandoValue value = new ExpandoValueImpl();

		value.setColumn(column);
		value.setData(column.getDefaultData());

		return doGetData(
			companyId, className, tableName, columnName, classPK, value,
			column.getType());
	}

	@Override
	public boolean getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, boolean defaultData)
		throws PortalException {

		ExpandoValue value = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (value == null) {
			return defaultData;
		}

		return value.getBoolean();
	}

	@Override
	public boolean[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, boolean[] defaultData)
		throws PortalException {

		ExpandoValue value = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (value == null) {
			return defaultData;
		}

		return value.getBooleanArray();
	}

	@Override
	public Date getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, Date defaultData)
		throws PortalException {

		ExpandoValue value = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (value == null) {
			return defaultData;
		}

		return value.getDate();
	}

	@Override
	public Date[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, Date[] defaultData)
		throws PortalException {

		ExpandoValue value = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (value == null) {
			return defaultData;
		}

		return value.getDateArray();
	}

	@Override
	public double getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, double defaultData)
		throws PortalException {

		ExpandoValue value = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (value == null) {
			return defaultData;
		}

		return value.getDouble();
	}

	@Override
	public double[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, double[] defaultData)
		throws PortalException {

		ExpandoValue value = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (value == null) {
			return defaultData;
		}

		return value.getDoubleArray();
	}

	@Override
	public float getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, float defaultData)
		throws PortalException {

		ExpandoValue value = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (value == null) {
			return defaultData;
		}

		return value.getFloat();
	}

	@Override
	public float[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, float[] defaultData)
		throws PortalException {

		ExpandoValue value = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (value == null) {
			return defaultData;
		}

		return value.getFloatArray();
	}

	@Override
	public int getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, int defaultData)
		throws PortalException {

		ExpandoValue value = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (value == null) {
			return defaultData;
		}

		return value.getInteger();
	}

	@Override
	public int[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, int[] defaultData)
		throws PortalException {

		ExpandoValue value = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (value == null) {
			return defaultData;
		}

		return value.getIntegerArray();
	}

	@Override
	public JSONObject getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, JSONObject defaultDataJSONObject)
		throws PortalException {

		ExpandoValue value = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (value == null) {
			return defaultDataJSONObject;
		}

		return value.getGeolocationJSONObject();
	}

	@Override
	public long getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, long defaultData)
		throws PortalException {

		ExpandoValue value = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (value == null) {
			return defaultData;
		}

		return value.getLong();
	}

	@Override
	public long[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, long[] defaultData)
		throws PortalException {

		ExpandoValue value = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (value == null) {
			return defaultData;
		}

		return value.getLongArray();
	}

	@Override
	public Map<?, ?> getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, Map<?, ?> defaultData)
		throws PortalException {

		ExpandoValue value = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (value == null) {
			return defaultData;
		}

		ExpandoColumn column = value.getColumn();

		int type = column.getType();

		if (type == ExpandoColumnConstants.STRING_ARRAY_LOCALIZED) {
			return value.getStringArrayMap();
		}

		return value.getStringMap();
	}

	@Override
	public Number getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, Number defaultData)
		throws PortalException {

		ExpandoValue value = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (value == null) {
			return defaultData;
		}

		return value.getNumber();
	}

	@Override
	public Number[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, Number[] defaultData)
		throws PortalException {

		ExpandoValue value = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (value == null) {
			return defaultData;
		}

		return value.getNumberArray();
	}

	@Override
	public short getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, short defaultData)
		throws PortalException {

		ExpandoValue value = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (value == null) {
			return defaultData;
		}

		return value.getShort();
	}

	@Override
	public short[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, short[] defaultData)
		throws PortalException {

		ExpandoValue value = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (value == null) {
			return defaultData;
		}

		return value.getShortArray();
	}

	@Override
	public String getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, String defaultData)
		throws PortalException {

		ExpandoValue value = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (value == null) {
			return defaultData;
		}

		return value.getString();
	}

	@Override
	public String[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, String[] defaultData)
		throws PortalException {

		ExpandoValue value = expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);

		if (value == null) {
			return defaultData;
		}

		return value.getStringArray();
	}

	@Override
	public List<ExpandoValue> getDefaultTableColumnValues(
		long companyId, long classNameId, String columnName, int start,
		int end) {

		return expandoValueLocalService.getColumnValues(
			companyId, classNameId, ExpandoTableConstants.DEFAULT_TABLE_NAME,
			columnName, start, end);
	}

	@Override
	public List<ExpandoValue> getDefaultTableColumnValues(
		long companyId, String className, String columnName, int start,
		int end) {

		return expandoValueLocalService.getDefaultTableColumnValues(
			companyId, _classNameLocalService.getClassNameId(className),
			columnName, start, end);
	}

	@Override
	public int getDefaultTableColumnValuesCount(
		long companyId, long classNameId, String columnName) {

		return expandoValueLocalService.getColumnValuesCount(
			companyId, classNameId, ExpandoTableConstants.DEFAULT_TABLE_NAME,
			columnName);
	}

	@Override
	public int getDefaultTableColumnValuesCount(
		long companyId, String className, String columnName) {

		return expandoValueLocalService.getDefaultTableColumnValuesCount(
			companyId, _classNameLocalService.getClassNameId(className),
			columnName);
	}

	@Override
	public List<ExpandoValue> getRowValues(long rowId) {
		return expandoValuePersistence.findByRowId(rowId);
	}

	@Override
	public List<ExpandoValue> getRowValues(long rowId, int start, int end) {
		return expandoValuePersistence.findByRowId(rowId, start, end);
	}

	@Override
	public List<ExpandoValue> getRowValues(
		long companyId, long classNameId, String tableName, long classPK,
		int start, int end) {

		ExpandoTable table = _expandoTablePersistence.fetchByC_C_N(
			companyId, classNameId, tableName);

		if (table == null) {
			return Collections.emptyList();
		}

		return expandoValuePersistence.findByT_CPK(
			table.getTableId(), classPK, start, end);
	}

	@Override
	public List<ExpandoValue> getRowValues(
		long companyId, String className, String tableName, long classPK,
		int start, int end) {

		return expandoValueLocalService.getRowValues(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName, classPK, start, end);
	}

	@Override
	public int getRowValuesCount(long rowId) {
		return expandoValuePersistence.countByRowId(rowId);
	}

	@Override
	public int getRowValuesCount(
		long companyId, long classNameId, String tableName, long classPK) {

		ExpandoTable table = _expandoTablePersistence.fetchByC_C_N(
			companyId, classNameId, tableName);

		if (table == null) {
			return 0;
		}

		return expandoValuePersistence.countByT_CPK(
			table.getTableId(), classPK);
	}

	@Override
	public int getRowValuesCount(
		long companyId, String className, String tableName, long classPK) {

		return expandoValueLocalService.getRowValuesCount(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName, classPK);
	}

	@Override
	public ExpandoValue getValue(long valueId) throws PortalException {
		return expandoValuePersistence.findByPrimaryKey(valueId);
	}

	@Override
	public ExpandoValue getValue(long columnId, long rowId)
		throws PortalException {

		return expandoValuePersistence.findByC_R(columnId, rowId);
	}

	@Override
	public ExpandoValue getValue(long tableId, long columnId, long classPK) {
		return expandoValuePersistence.fetchByT_C_C(tableId, columnId, classPK);
	}

	@Override
	public ExpandoValue getValue(
		long companyId, long classNameId, String tableName, String columnName,
		long classPK) {

		ExpandoTable table = _expandoTablePersistence.fetchByC_C_N(
			companyId, classNameId, tableName);

		if (table == null) {
			return null;
		}

		ExpandoColumn column = _expandoColumnPersistence.fetchByT_N(
			table.getTableId(), columnName);

		if (column == null) {
			return null;
		}

		return expandoValuePersistence.fetchByT_C_C(
			table.getTableId(), column.getColumnId(), classPK);
	}

	@Override
	public ExpandoValue getValue(
		long companyId, String className, String tableName, String columnName,
		long classPK) {

		return expandoValueLocalService.getValue(
			companyId, _classNameLocalService.getClassNameId(className),
			tableName, columnName, classPK);
	}

	protected <T> T convertType(int type, Object data) {
		if (data == null) {
			return (T)data;
		}

		data = handleCollections(type, data);
		data = handleStrings(type, data);

		if (type == ExpandoColumnConstants.BOOLEAN) {
			data = TypeConverterUtil.convertType(data, Boolean.TYPE);
		}
		else if (type == ExpandoColumnConstants.BOOLEAN_ARRAY) {
			data = TypeConverterUtil.convertType(data, boolean[].class);
		}
		else if (type == ExpandoColumnConstants.DATE) {
			data = TypeConverterUtil.convertType(data, Date.class);
		}
		else if (type == ExpandoColumnConstants.DATE_ARRAY) {
			data = TypeConverterUtil.convertType(data, Date[].class);
		}
		else if (type == ExpandoColumnConstants.DOUBLE) {
			data = TypeConverterUtil.convertType(data, Double.TYPE);
		}
		else if (type == ExpandoColumnConstants.DOUBLE_ARRAY) {
			data = TypeConverterUtil.convertType(data, double[].class);
		}
		else if (type == ExpandoColumnConstants.FLOAT) {
			data = TypeConverterUtil.convertType(data, Float.TYPE);
		}
		else if (type == ExpandoColumnConstants.FLOAT_ARRAY) {
			data = TypeConverterUtil.convertType(data, float[].class);
		}
		else if (type == ExpandoColumnConstants.INTEGER) {
			data = TypeConverterUtil.convertType(data, Integer.TYPE);
		}
		else if (type == ExpandoColumnConstants.INTEGER_ARRAY) {
			data = TypeConverterUtil.convertType(data, int[].class);
		}
		else if (type == ExpandoColumnConstants.LONG) {
			data = TypeConverterUtil.convertType(data, Long.TYPE);
		}
		else if (type == ExpandoColumnConstants.LONG_ARRAY) {
			data = TypeConverterUtil.convertType(data, long[].class);
		}
		else if (type == ExpandoColumnConstants.NUMBER) {
			data = TypeConverterUtil.convertType(data, Number.class);
		}
		else if (type == ExpandoColumnConstants.NUMBER_ARRAY) {
			data = TypeConverterUtil.convertType(data, Number[].class);
		}
		else if (type == ExpandoColumnConstants.SHORT) {
			data = TypeConverterUtil.convertType(data, Short.TYPE);
		}
		else if (type == ExpandoColumnConstants.SHORT_ARRAY) {
			data = TypeConverterUtil.convertType(data, short[].class);
		}
		else if (type == ExpandoColumnConstants.STRING_ARRAY) {
			data = TypeConverterUtil.convertType(data, String[].class);
		}

		return (T)data;
	}

	protected ExpandoValue doAddValue(
		long companyId, long classNameId, long tableId, long columnId,
		long classPK, String data) {

		ExpandoValue value = expandoValuePersistence.fetchByT_C_C(
			tableId, columnId, classPK);

		if (value == null) {
			ExpandoRow row = _expandoRowPersistence.fetchByT_C(
				tableId, classPK);

			if (row == null) {
				row = _expandoRowPersistence.create(
					counterLocalService.increment());

				row.setCompanyId(companyId);
				row.setModifiedDate(new Date());
				row.setTableId(tableId);
				row.setClassPK(classPK);

				row = _expandoRowPersistence.update(row);
			}

			value = expandoValuePersistence.create(
				counterLocalService.increment());

			value.setCompanyId(companyId);
			value.setTableId(tableId);
			value.setColumnId(columnId);
			value.setRowId(row.getRowId());
			value.setClassNameId(classNameId);
			value.setClassPK(classPK);
			value.setData(data);

			return expandoValuePersistence.update(value);
		}

		if (!Objects.equals(value.getData(), data)) {
			value.setData(data);

			value = expandoValuePersistence.update(value);

			ExpandoRow row = _expandoRowPersistence.fetchByT_C(
				tableId, classPK);

			row.setModifiedDate(new Date());

			_expandoRowPersistence.update(row);
		}

		return value;
	}

	protected Serializable doGetData(
			long companyId, String className, String tableName,
			String columnName, long classPK, ExpandoValue value, int type)
		throws PortalException {

		if (type == ExpandoColumnConstants.BOOLEAN) {
			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK,
				value.getBoolean());
		}
		else if (type == ExpandoColumnConstants.BOOLEAN_ARRAY) {
			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK,
				new boolean[0]);
		}
		else if (type == ExpandoColumnConstants.DATE) {
			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK,
				value.getDate());
		}
		else if (type == ExpandoColumnConstants.DATE_ARRAY) {
			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK,
				new Date[0]);
		}
		else if (type == ExpandoColumnConstants.DOUBLE) {
			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK,
				value.getDouble());
		}
		else if (type == ExpandoColumnConstants.DOUBLE_ARRAY) {
			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK,
				new double[0]);
		}
		else if (type == ExpandoColumnConstants.FLOAT) {
			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK,
				value.getFloat());
		}
		else if (type == ExpandoColumnConstants.FLOAT_ARRAY) {
			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK,
				new float[0]);
		}
		else if (type == ExpandoColumnConstants.GEOLOCATION) {
			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK,
				value.getGeolocationJSONObject());
		}
		else if (type == ExpandoColumnConstants.INTEGER) {
			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK,
				value.getInteger());
		}
		else if (type == ExpandoColumnConstants.INTEGER_ARRAY) {
			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK,
				new int[0]);
		}
		else if (type == ExpandoColumnConstants.LONG) {
			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK,
				value.getLong());
		}
		else if (type == ExpandoColumnConstants.LONG_ARRAY) {
			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK,
				new long[0]);
		}
		else if (type == ExpandoColumnConstants.NUMBER) {
			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK,
				value.getNumber());
		}
		else if (type == ExpandoColumnConstants.NUMBER_ARRAY) {
			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK,
				new Number[0]);
		}
		else if (type == ExpandoColumnConstants.SHORT) {
			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK,
				value.getShort());
		}
		else if (type == ExpandoColumnConstants.SHORT_ARRAY) {
			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK,
				new short[0]);
		}
		else if (type == ExpandoColumnConstants.STRING_ARRAY) {
			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK,
				new String[0]);
		}
		else if (type == ExpandoColumnConstants.STRING) {
			return expandoValueLocalService.getData(
				companyId, className, tableName, columnName, classPK,
				value.getString());
		}

		return (Serializable)expandoValueLocalService.getData(
			companyId, className, tableName, columnName, classPK,
			new HashMap<Object, Object>());
	}

	protected Object handleCollections(int type, Object object) {
		if (!(object instanceof Collection) || !isTypeArray(type)) {
			return object;
		}

		Collection<?> collection = (Collection<?>)object;

		return collection.toArray();
	}

	protected Object handleStrings(int type, Object object) {
		if (!(object instanceof String)) {
			return object;
		}

		String string = (String)object;

		if (isTypeArray(type) && string.startsWith(StringPool.OPEN_BRACKET) &&
			string.endsWith(StringPool.CLOSE_BRACKET)) {

			string = string.substring(1, string.length() - 1);
		}

		return string;
	}

	protected boolean isTypeArray(int type) {
		if ((type == ExpandoColumnConstants.BOOLEAN_ARRAY) ||
			(type == ExpandoColumnConstants.DATE_ARRAY) ||
			(type == ExpandoColumnConstants.DOUBLE_ARRAY) ||
			(type == ExpandoColumnConstants.FLOAT_ARRAY) ||
			(type == ExpandoColumnConstants.INTEGER_ARRAY) ||
			(type == ExpandoColumnConstants.LONG_ARRAY) ||
			(type == ExpandoColumnConstants.NUMBER_ARRAY) ||
			(type == ExpandoColumnConstants.SHORT_ARRAY) ||
			(type == ExpandoColumnConstants.STRING_ARRAY)) {

			return true;
		}

		return false;
	}

	@BeanReference(type = ClassNameLocalService.class)
	private ClassNameLocalService _classNameLocalService;

	@BeanReference(type = ExpandoColumnPersistence.class)
	private ExpandoColumnPersistence _expandoColumnPersistence;

	@BeanReference(type = ExpandoRowPersistence.class)
	private ExpandoRowPersistence _expandoRowPersistence;

	@BeanReference(type = ExpandoTablePersistence.class)
	private ExpandoTablePersistence _expandoTablePersistence;

	private static class ExpandoValueDeleteHandlerHolder {

		public static List<ExpandoValueDeleteHandler> getService(String key) {
			return _serviceTrackerMap.getService(key);
		}

		private static final ServiceTrackerMap
			<String, List<ExpandoValueDeleteHandler>> _serviceTrackerMap =
				ServiceTrackerMapFactory.openMultiValueMap(
					SystemBundleUtil.getBundleContext(),
					ExpandoValueDeleteHandler.class, "model.class.name");

	}

}