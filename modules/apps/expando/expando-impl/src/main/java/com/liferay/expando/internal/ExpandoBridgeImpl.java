/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.internal;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.kernel.service.ExpandoColumnLocalServiceUtil;
import com.liferay.expando.kernel.service.ExpandoColumnServiceUtil;
import com.liferay.expando.kernel.service.ExpandoTableLocalServiceUtil;
import com.liferay.expando.kernel.service.ExpandoValueLocalServiceUtil;
import com.liferay.expando.kernel.service.ExpandoValueServiceUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.staging.MergeLayoutPrototypesThreadLocal;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.CopyLayoutThreadLocal;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.UnicodeProperties;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Raymond Augé
 */
public class ExpandoBridgeImpl implements ExpandoBridge {

	public ExpandoBridgeImpl(long companyId, String className) {
		this(companyId, className, 0);
	}

	public ExpandoBridgeImpl(long companyId, String className, long classPK) {
		_companyId = companyId;

		if (companyId == 0) {
			_companyId = CompanyThreadLocal.getCompanyId();
		}

		_className = className;
		_classPK = classPK;
	}

	@Override
	public void addAttribute(String name) throws PortalException {
		boolean secure =
			PropsValues.PERMISSIONS_CUSTOM_ATTRIBUTE_WRITE_CHECK_BY_DEFAULT;

		if (CopyLayoutThreadLocal.isCopyLayout() ||
			ExportImportThreadLocal.isImportInProcess() ||
			MergeLayoutPrototypesThreadLocal.isInProgress()) {

			secure = false;
		}

		addAttribute(name, ExpandoColumnConstants.STRING, null, secure);
	}

	@Override
	public void addAttribute(String name, boolean secure)
		throws PortalException {

		addAttribute(name, ExpandoColumnConstants.STRING, null, secure);
	}

	@Override
	public void addAttribute(String name, int type) throws PortalException {
		boolean secure =
			PropsValues.PERMISSIONS_CUSTOM_ATTRIBUTE_WRITE_CHECK_BY_DEFAULT;

		if (CopyLayoutThreadLocal.isCopyLayout() ||
			ExportImportThreadLocal.isImportInProcess() ||
			MergeLayoutPrototypesThreadLocal.isInProgress()) {

			secure = false;
		}

		addAttribute(name, type, null, secure);
	}

	@Override
	public void addAttribute(String name, int type, boolean secure)
		throws PortalException {

		addAttribute(name, type, null, secure);
	}

	@Override
	public void addAttribute(String name, int type, Serializable defaultValue)
		throws PortalException {

		boolean secure =
			PropsValues.PERMISSIONS_CUSTOM_ATTRIBUTE_WRITE_CHECK_BY_DEFAULT;

		if (CopyLayoutThreadLocal.isCopyLayout() ||
			ExportImportThreadLocal.isImportInProcess() ||
			MergeLayoutPrototypesThreadLocal.isInProgress()) {

			secure = false;
		}

		addAttribute(name, type, defaultValue, secure);
	}

	@Override
	public void addAttribute(
			String name, int type, Serializable defaultValue, boolean secure)
		throws PortalException {

		try {
			ExpandoTable table = getTable();

			if (secure) {
				ExpandoColumnServiceUtil.addColumn(
					table.getTableId(), name, type, defaultValue);
			}
			else {
				ExpandoColumnLocalServiceUtil.addColumn(
					table.getTableId(), name, type, defaultValue);
			}
		}
		catch (Exception exception) {
			if (exception instanceof PortalException) {
				throw (PortalException)exception;
			}

			ReflectionUtil.throwException(exception);
		}
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof ExpandoBridgeImpl)) {
			return false;
		}

		ExpandoBridgeImpl expandoBridgeImpl = (ExpandoBridgeImpl)object;

		try {
			ExpandoTable table1 = getTable();

			long tableId1 = table1.getTableId();

			ExpandoTable table2 = expandoBridgeImpl.getTable();

			long tableId2 = table2.getTableId();

			if (tableId1 != tableId2) {
				return false;
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return false;
		}

		for (ExpandoColumn column : getAttributeColumns()) {
			Serializable attribute1 = getAttribute(column.getName());
			Serializable attribute2 = expandoBridgeImpl.getAttribute(
				column.getName());

			if (!equals(column.getType(), attribute1, attribute2)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public Serializable getAttribute(String name) {
		boolean secure =
			PropsValues.PERMISSIONS_CUSTOM_ATTRIBUTE_READ_CHECK_BY_DEFAULT;

		if (CopyLayoutThreadLocal.isCopyLayout() ||
			ExportImportThreadLocal.isExportInProcess() ||
			MergeLayoutPrototypesThreadLocal.isInProgress()) {

			secure = false;
		}

		return getAttribute(name, secure);
	}

	@Override
	public Serializable getAttribute(String name, boolean secure) {
		Serializable data = null;

		try {
			if (secure) {
				data = ExpandoValueServiceUtil.getData(
					_companyId, _className,
					ExpandoTableConstants.DEFAULT_TABLE_NAME, name, _classPK);
			}
			else {
				data = ExpandoValueLocalServiceUtil.getData(
					_companyId, _className,
					ExpandoTableConstants.DEFAULT_TABLE_NAME, name, _classPK);
			}
		}
		catch (Exception exception) {
			ReflectionUtil.throwException(exception);
		}

		return data;
	}

	@Override
	public Serializable getAttributeDefault(String name) {
		ExpandoColumn column =
			ExpandoColumnLocalServiceUtil.getDefaultTableColumn(
				_companyId, _className, name);

		return column.getDefaultValue();
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		List<String> columnNames = new ArrayList<>();

		for (ExpandoColumn column : getAttributeColumns()) {
			columnNames.add(column.getName());
		}

		return Collections.enumeration(columnNames);
	}

	@Override
	public UnicodeProperties getAttributeProperties(String name) {
		ExpandoColumn column =
			ExpandoColumnLocalServiceUtil.getDefaultTableColumn(
				_companyId, _className, name);

		return column.getTypeSettingsProperties();
	}

	@Override
	public Map<String, Serializable> getAttributes() {
		boolean secure =
			PropsValues.PERMISSIONS_CUSTOM_ATTRIBUTE_READ_CHECK_BY_DEFAULT;

		if (CopyLayoutThreadLocal.isCopyLayout() ||
			ExportImportThreadLocal.isExportInProcess() ||
			MergeLayoutPrototypesThreadLocal.isInProgress()) {

			secure = false;
		}

		return getAttributes(secure);
	}

	@Override
	public Map<String, Serializable> getAttributes(boolean secure) {
		Map<String, Serializable> attributes = new HashMap<>();

		for (ExpandoColumn column : getAttributeColumns()) {
			attributes.put(
				column.getName(), getAttribute(column.getName(), secure));
		}

		return attributes;
	}

	@Override
	public Map<String, Serializable> getAttributes(Collection<String> names) {
		boolean secure =
			PropsValues.PERMISSIONS_CUSTOM_ATTRIBUTE_READ_CHECK_BY_DEFAULT;

		if (CopyLayoutThreadLocal.isCopyLayout() ||
			ExportImportThreadLocal.isExportInProcess() ||
			MergeLayoutPrototypesThreadLocal.isInProgress()) {

			secure = false;
		}

		return getAttributes(names, secure);
	}

	@Override
	public Map<String, Serializable> getAttributes(
		Collection<String> names, boolean secure) {

		Map<String, Serializable> attributeValues = null;

		try {
			if (secure) {
				attributeValues = ExpandoValueServiceUtil.getData(
					_companyId, _className,
					ExpandoTableConstants.DEFAULT_TABLE_NAME, names, _classPK);
			}
			else {
				attributeValues = ExpandoValueLocalServiceUtil.getData(
					_companyId, _className,
					ExpandoTableConstants.DEFAULT_TABLE_NAME, names, _classPK);
			}
		}
		catch (Exception exception) {
			ReflectionUtil.throwException(exception);
		}

		return attributeValues;
	}

	@Override
	public int getAttributeType(String name) {
		ExpandoColumn column =
			ExpandoColumnLocalServiceUtil.getDefaultTableColumn(
				_companyId, _className, name);

		return column.getType();
	}

	@Override
	public String getClassName() {
		return _className;
	}

	@Override
	public long getClassPK() {
		return _classPK;
	}

	@Override
	public long getCompanyId() {
		return _companyId;
	}

	@Override
	public boolean hasAttribute(String name) {
		ExpandoColumn column =
			ExpandoColumnLocalServiceUtil.getDefaultTableColumn(
				_companyId, _className, name);

		if (column != null) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hash = 0;

		try {
			hash = HashUtil.hash(0, getTable());
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return HashUtil.hash(hash, getAttributeColumns());
	}

	public void reindex() {
		if (_classPK <= 0) {
			return;
		}

		Indexer<?> indexer = IndexerRegistryUtil.nullSafeGetIndexer(_className);

		try {
			indexer.reindex(_className, _classPK);
		}
		catch (Exception exception) {
			ReflectionUtil.throwException(exception);
		}
	}

	@Override
	public void setAttribute(String name, Serializable value) {
		boolean secure =
			PropsValues.PERMISSIONS_CUSTOM_ATTRIBUTE_WRITE_CHECK_BY_DEFAULT;

		if (CopyLayoutThreadLocal.isCopyLayout() ||
			ExportImportThreadLocal.isImportInProcess() ||
			MergeLayoutPrototypesThreadLocal.isInProgress()) {

			secure = false;
		}

		setAttribute(name, value, secure);
	}

	@Override
	public void setAttribute(String name, Serializable value, boolean secure) {
		if (_classPK <= 0) {
			throw new UnsupportedOperationException(
				"Class primary key is less than 0");
		}

		try {
			if (secure) {
				ExpandoValueServiceUtil.addValue(
					_companyId, _className,
					ExpandoTableConstants.DEFAULT_TABLE_NAME, name, _classPK,
					value);
			}
			else {
				ExpandoValueLocalServiceUtil.addValue(
					_companyId, _className,
					ExpandoTableConstants.DEFAULT_TABLE_NAME, name, _classPK,
					value);
			}
		}
		catch (Exception exception) {
			ReflectionUtil.throwException(exception);
		}
	}

	@Override
	public void setAttributeDefault(String name, Serializable defaultValue) {
		try {
			ExpandoColumn column =
				ExpandoColumnLocalServiceUtil.getDefaultTableColumn(
					_companyId, _className, name);

			ExpandoColumnServiceUtil.updateColumn(
				column.getColumnId(), column.getName(), column.getType(),
				defaultValue);
		}
		catch (Exception exception) {
			ReflectionUtil.throwException(exception);
		}
	}

	@Override
	public void setAttributeProperties(
		String name, UnicodeProperties unicodeProperties) {

		boolean secure =
			PropsValues.PERMISSIONS_CUSTOM_ATTRIBUTE_WRITE_CHECK_BY_DEFAULT;

		if (CopyLayoutThreadLocal.isCopyLayout() ||
			ExportImportThreadLocal.isImportInProcess() ||
			MergeLayoutPrototypesThreadLocal.isInProgress()) {

			secure = false;
		}

		setAttributeProperties(name, unicodeProperties, secure);
	}

	@Override
	public void setAttributeProperties(
		String name, UnicodeProperties unicodeProperties, boolean secure) {

		try {
			ExpandoColumn column =
				ExpandoColumnLocalServiceUtil.getDefaultTableColumn(
					_companyId, _className, name);

			if (secure) {
				ExpandoColumnServiceUtil.updateTypeSettings(
					column.getColumnId(), unicodeProperties.toString());
			}
			else {
				ExpandoColumnLocalServiceUtil.updateTypeSettings(
					column.getColumnId(), unicodeProperties.toString());
			}
		}
		catch (Exception exception) {
			ReflectionUtil.throwException(exception);
		}
	}

	@Override
	public void setAttributes(Map<String, Serializable> attributes) {
		boolean secure =
			PropsValues.PERMISSIONS_CUSTOM_ATTRIBUTE_WRITE_CHECK_BY_DEFAULT;

		if (CopyLayoutThreadLocal.isCopyLayout() ||
			ExportImportThreadLocal.isImportInProcess() ||
			MergeLayoutPrototypesThreadLocal.isInProgress()) {

			secure = false;
		}

		setAttributes(attributes, secure);
	}

	@Override
	public void setAttributes(
		Map<String, Serializable> attributes, boolean secure) {

		if (_classPK <= 0) {
			throw new UnsupportedOperationException(
				"Class primary key is less than 0");
		}

		if ((attributes == null) || attributes.isEmpty()) {
			return;
		}

		try {
			if (secure) {
				ExpandoValueServiceUtil.addValues(
					_companyId, _className,
					ExpandoTableConstants.DEFAULT_TABLE_NAME, _classPK,
					attributes);
			}
			else {
				ExpandoValueLocalServiceUtil.addValues(
					_companyId, _className,
					ExpandoTableConstants.DEFAULT_TABLE_NAME, _classPK,
					attributes);
			}
		}
		catch (Exception exception) {
			ReflectionUtil.throwException(exception);
		}
	}

	@Override
	public void setAttributes(ServiceContext serviceContext) {
		boolean secure =
			PropsValues.PERMISSIONS_CUSTOM_ATTRIBUTE_WRITE_CHECK_BY_DEFAULT;

		if (CopyLayoutThreadLocal.isCopyLayout() ||
			ExportImportThreadLocal.isImportInProcess() ||
			MergeLayoutPrototypesThreadLocal.isInProgress()) {

			secure = false;
		}

		setAttributes(serviceContext, secure);
	}

	@Override
	public void setAttributes(ServiceContext serviceContext, boolean secure) {
		if (serviceContext == null) {
			return;
		}

		setAttributes(serviceContext.getExpandoBridgeAttributes(), secure);
	}

	@Override
	public void setClassName(String className) {
		_className = className;
	}

	@Override
	public void setClassPK(long classPK) {
		_classPK = classPK;
	}

	@Override
	public void setCompanyId(long companyId) {
		_companyId = companyId;
	}

	protected boolean equals(
		int type, Serializable serializable1, Serializable serializable2) {

		if (type == ExpandoColumnConstants.BOOLEAN_ARRAY) {
			Boolean[] array1 = (Boolean[])serializable1;
			Boolean[] array2 = (Boolean[])serializable2;

			return Arrays.equals(array1, array2);
		}
		else if (type == ExpandoColumnConstants.DATE_ARRAY) {
			Date[] array1 = (Date[])serializable1;
			Date[] array2 = (Date[])serializable2;

			return Arrays.equals(array1, array2);
		}
		else if (type == ExpandoColumnConstants.DOUBLE_ARRAY) {
			double[] array1 = (double[])serializable1;
			double[] array2 = (double[])serializable2;

			return Arrays.equals(array1, array2);
		}
		else if (type == ExpandoColumnConstants.FLOAT_ARRAY) {
			float[] array1 = (float[])serializable1;
			float[] array2 = (float[])serializable2;

			return Arrays.equals(array1, array2);
		}
		else if (type == ExpandoColumnConstants.INTEGER_ARRAY) {
			int[] array1 = (int[])serializable1;
			int[] array2 = (int[])serializable2;

			return Arrays.equals(array1, array2);
		}
		else if (type == ExpandoColumnConstants.LONG_ARRAY) {
			long[] array1 = (long[])serializable1;
			long[] array2 = (long[])serializable2;

			return Arrays.equals(array1, array2);
		}
		else if (type == ExpandoColumnConstants.NUMBER_ARRAY) {
			Number[] array1 = (Number[])serializable1;
			Number[] array2 = (Number[])serializable2;

			return Arrays.equals(array1, array2);
		}
		else if (type == ExpandoColumnConstants.SHORT_ARRAY) {
			short[] array1 = (short[])serializable1;
			short[] array2 = (short[])serializable2;

			return Arrays.equals(array1, array2);
		}
		else if (type == ExpandoColumnConstants.STRING_ARRAY) {
			String[] array1 = (String[])serializable1;
			String[] array2 = (String[])serializable2;

			return Arrays.equals(array1, array2);
		}

		return serializable1.equals(serializable2);
	}

	protected List<ExpandoColumn> getAttributeColumns() {
		return ExpandoColumnLocalServiceUtil.getDefaultTableColumns(
			_companyId, _className);
	}

	protected ExpandoTable getTable() throws PortalException {
		ExpandoTable table = ExpandoTableLocalServiceUtil.fetchDefaultTable(
			_companyId, _className);

		if (table == null) {
			table = ExpandoTableLocalServiceUtil.addDefaultTable(
				_companyId, _className);
		}

		return table;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExpandoBridgeImpl.class);

	private String _className;
	private long _classPK;
	private long _companyId;

}