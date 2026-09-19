/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.Validator;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author Brian Wing Shun Chan
 * @author Charles May
 * @author Shuyang Zhou
 */
public class EntityColumn implements Cloneable, Comparable<EntityColumn> {

	public EntityColumn(ServiceBuilder serviceBuilder, String name) {
		this(
			serviceBuilder, name, null, null, null, null, false, false, false,
			null, null, true, true, false, null, null, false, null, null, true,
			true, false, false, CTColumnResolutionType.STRICT, false, false,
			null, false);
	}

	public EntityColumn(
		ServiceBuilder serviceBuilder, String name, String dbName) {

		this(
			serviceBuilder, name, null, dbName, null, "String", false, false,
			false, null, null, null, null, true, false, false, false,
			CTColumnResolutionType.STRICT, false, false, null, false);
	}

	public EntityColumn(
		ServiceBuilder serviceBuilder, String name, String pluralName,
		String dbName, String methodName, String type, boolean primary,
		boolean accessor, boolean filterPrimary, String entityName,
		String mappingTableName, boolean caseSensitive,
		boolean orderByAscending, boolean orderColumn, String comparator,
		String arrayableOperator, boolean arrayablePagination, String idType,
		String idParam, boolean convertNull, boolean lazy, boolean localized,
		boolean jsonEnabled, CTColumnResolutionType ctColumnResolutionType,
		boolean containerModel, boolean parentContainerModel,
		String uadAnonymizeFieldName, boolean uadNonanonymizable) {

		_serviceBuilder = serviceBuilder;
		_name = name;
		_pluralName = GetterUtil.getString(
			pluralName, serviceBuilder.formatPlural(name));
		_dbName = dbName;
		_methodName = GetterUtil.getString(
			methodName, TextFormatter.format(name, TextFormatter.G));
		_type = type;
		_primary = primary;
		_accessor = accessor;
		_filterPrimary = filterPrimary;
		_entityName = entityName;
		_mappingTableName = mappingTableName;
		_caseSensitive = caseSensitive;
		_orderByAscending = orderByAscending;
		_orderColumn = orderColumn;
		_comparator = comparator;
		_arrayableOperator = arrayableOperator;
		_arrayablePagination = arrayablePagination;
		_idType = idType;
		_idParam = idParam;
		_convertNull = convertNull;
		_lazy = lazy;
		_localized = localized;
		_jsonEnabled = jsonEnabled;
		_ctColumnResolutionType = ctColumnResolutionType;
		_containerModel = containerModel;
		_parentContainerModel = parentContainerModel;
		_uadAnonymizeFieldName = uadAnonymizeFieldName;
		_uadNonanonymizable = uadNonanonymizable;

		_finderColumnTypeName = _finderColumnTypeNames.get(type);

		_humanName = ServiceBuilder.toHumanName(name);

		if (Objects.equals(
				_methodName, TextFormatter.format(name, TextFormatter.G))) {

			_modelHintsName = name;
		}
		else {
			_modelHintsName = methodName;
		}
	}

	public EntityColumn(
		ServiceBuilder serviceBuilder, String name, String pluralName,
		String dbName, String methodName, String type, boolean primary,
		boolean accessor, boolean filterPrimary, String ejbName,
		String mappingTable, String idType, String idParam, boolean convertNull,
		boolean lazy, boolean localized, boolean jsonEnabled,
		CTColumnResolutionType ctColumnResolutionType, boolean containerModel,
		boolean parentContainerModel, String uadAnonymizeFieldName,
		boolean uadNonanonymizable) {

		this(
			serviceBuilder, name, pluralName, dbName, methodName, type, primary,
			accessor, filterPrimary, ejbName, mappingTable, true, true, false,
			null, null, false, idType, idParam, convertNull, lazy, localized,
			jsonEnabled, ctColumnResolutionType, containerModel,
			parentContainerModel, uadAnonymizeFieldName, uadNonanonymizable);
	}

	@Override
	public Object clone() {
		return new EntityColumn(
			_serviceBuilder, getName(), getPluralName(), getDBName(),
			getMethodName(), getType(), isPrimary(), isAccessor(),
			isFilterPrimary(), getEntityName(), getMappingTableName(),
			isCaseSensitive(), isOrderByAscending(), isOrderColumn(),
			getComparator(), getArrayableOperator(), hasArrayablePagination(),
			getIdType(), getIdParam(), isConvertNull(), isLazy(), isLocalized(),
			isJsonEnabled(), getCTColumnResolutionType(), isContainerModel(),
			isParentContainerModel(), getUADAnonymizeFieldName(),
			isUADNonanonymizable());
	}

	@Override
	public int compareTo(EntityColumn entityColumn) {
		return _name.compareTo(entityColumn._name);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof EntityColumn)) {
			return false;
		}

		EntityColumn entityColumn = (EntityColumn)object;

		return _name.equals(entityColumn.getName());
	}

	public String getAccessorName(String className) {
		String accessorName = _accessorNames.get(className + "." + _name);

		if (Validator.isNull(accessorName)) {
			accessorName = TextFormatter.format(
				TextFormatter.format(_name, TextFormatter.H), TextFormatter.A);

			accessorName += "_ACCESSOR";
		}

		return accessorName;
	}

	public String getArrayableOperator() {
		return _arrayableOperator;
	}

	public CTColumnResolutionType getCTColumnResolutionType() {
		return _ctColumnResolutionType;
	}

	public String getCTColumnResolutionTypeName() {
		String name = StringUtil.toLowerCase(
			_ctColumnResolutionType.toString());

		return StringUtil.upperCaseFirstLetter(name);
	}

	public String getComparator() {
		return _comparator;
	}

	public String getDBName() {
		return _dbName;
	}

	public String getEntityName() {
		return _entityName;
	}

	public String getFinderColumnTypeName() {
		return _finderColumnTypeName;
	}

	public String getGenericizedType() {
		if (_type.equals("Map")) {
			return "Map<String, Serializable>";
		}

		return _type;
	}

	public String getHumanCondition(boolean arrayable) {
		StringBundler sb = new StringBundler(6);

		sb.append(_name);
		sb.append(" ");
		sb.append(convertComparatorToHtml(_comparator));
		sb.append(" ");

		if (arrayable && hasArrayableOperator()) {
			if (isArrayableAndOperator()) {
				sb.append("all ");
			}
			else {
				sb.append("any ");
			}
		}

		sb.append("&#63;");

		return sb.toString();
	}

	public String getHumanName() {
		return _humanName;
	}

	public String getIdParam() {
		return _idParam;
	}

	public String getIdType() {
		return _idType;
	}

	public String getMappingTableName() {
		return _mappingTableName;
	}

	public String getMethodName() {
		return _methodName;
	}

	public String getMethodNames() {
		return _serviceBuilder.formatPlural(_methodName);
	}

	public String getMethodUserUuidName() {
		return _methodName.substring(0, _methodName.length() - 2) + "Uuid";
	}

	public String getModelHintsName() {
		return _modelHintsName;
	}

	public String getName() {
		return _name;
	}

	public String getPluralHumanName() {
		return _serviceBuilder.formatPlural(getHumanName());
	}

	public String getPluralName() {
		return _pluralName;
	}

	public String getType() {
		return _type;
	}

	public String getUADAnonymizeFieldName() {
		if (Validator.isNull(_uadAnonymizeFieldName) && isUADUserId()) {
			return "userId";
		}

		if (Validator.isNull(_uadAnonymizeFieldName) && isUADUserName()) {
			return "fullName";
		}

		return _uadAnonymizeFieldName;
	}

	public String getUADUserIdColumnName() {
		if (isUADUserName()) {
			return StringUtil.replace(_name, "Name", "Id");
		}

		return StringPool.BLANK;
	}

	public String getUserUuidHumanName() {
		return ServiceBuilder.toHumanName(getUserUuidName());
	}

	public String getUserUuidName() {
		return _name.substring(0, _name.length() - 2) + "Uuid";
	}

	public boolean hasArrayableOperator() {
		return Validator.isNotNull(_arrayableOperator);
	}

	public boolean hasArrayablePagination() {
		return _arrayablePagination;
	}

	@Override
	public int hashCode() {
		return _name.hashCode();
	}

	public boolean isAccessor() {
		return _accessor;
	}

	public boolean isArrayableAndOperator() {
		return _arrayableOperator.equals("AND");
	}

	public boolean isCaseSensitive() {
		return _caseSensitive;
	}

	public boolean isCollection() {
		return _type.equals("Collection");
	}

	public boolean isContainerModel() {
		return _containerModel;
	}

	public boolean isConvertNull() {
		return _convertNull;
	}

	public boolean isFilterPrimary() {
		return _filterPrimary;
	}

	public boolean isFinderPath() {
		return _finderPath;
	}

	public boolean isIndexable() {
		return _indexable;
	}

	public boolean isInterfaceColumn() {
		return _interfaceColumn;
	}

	public boolean isJsonEnabled() {
		return _jsonEnabled;
	}

	public boolean isLazy() {
		return _lazy;
	}

	public boolean isLocalized() {
		return _localized;
	}

	public boolean isMappingManyToMany() {
		return Validator.isNotNull(_mappingTableName);
	}

	public boolean isOrderByAscending() {
		return _orderByAscending;
	}

	public boolean isOrderColumn() {
		return _orderColumn;
	}

	public boolean isParentContainerModel() {
		return _parentContainerModel;
	}

	public boolean isPrimary() {
		return _primary;
	}

	public boolean isPrimitiveType() {
		return isPrimitiveType(true);
	}

	public boolean isPrimitiveType(boolean includeWrappers) {
		if (Character.isLowerCase(_type.charAt(0)) ||
			(includeWrappers && isPrimitiveTypeWrapper())) {

			return true;
		}

		return false;
	}

	public boolean isPrimitiveTypeWrapper() {
		if (_type.equals("Boolean")) {
			return true;
		}
		else if (_type.equals("Byte")) {
			return true;
		}
		else if (_type.equals("Char")) {
			return true;
		}
		else if (_type.equals("Double")) {
			return true;
		}
		else if (_type.equals("Float")) {
			return true;
		}
		else if (_type.equals("Integer")) {
			return true;
		}
		else if (_type.equals("Long")) {
			return true;
		}
		else if (_type.equals("Short")) {
			return true;
		}

		return false;
	}

	public boolean isUADEnabled() {
		if (Validator.isNotNull(_uadAnonymizeFieldName) ||
			_uadNonanonymizable) {

			return true;
		}

		return false;
	}

	public boolean isUADNonanonymizable() {
		return _uadNonanonymizable;
	}

	public boolean isUADUserId() {
		return _isUADUserId(_name);
	}

	public boolean isUADUserName() {
		if (_name.equals("userName") || _name.endsWith("UserName")) {
			return true;
		}

		return false;
	}

	public boolean isUserUuid() {
		if (_type.equals("long") && _methodName.endsWith("UserId")) {
			return true;
		}

		return false;
	}

	public void setArrayableOperator(String arrayableOperator) {
		_arrayableOperator = StringUtil.toUpperCase(arrayableOperator);
	}

	public void setArrayablePagination(boolean arrayablePagination) {
		_arrayablePagination = arrayablePagination;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		_caseSensitive = caseSensitive;
	}

	public void setComparator(String comparator) {
		_comparator = comparator;
	}

	public void setContainerModel(boolean containerModel) {
		_containerModel = containerModel;
	}

	public void setConvertNull(boolean convertNull) {
		_convertNull = convertNull;
	}

	public void setDBName(String dbName) {
		_dbName = dbName;
	}

	public void setFinderPath(boolean finderPath) {
		_finderPath = finderPath;
	}

	public void setIdParam(String idParam) {
		_idParam = idParam;
	}

	public void setIdType(String idType) {
		_idType = idType;
	}

	public void setIndexable(boolean indexable) {
		_indexable = indexable;
	}

	public void setInterfaceColumn(boolean interfaceColumn) {
		_interfaceColumn = interfaceColumn;
	}

	public void setLazy(boolean lazy) {
		_lazy = lazy;
	}

	public void setLocalized(boolean localized) {
		_localized = localized;
	}

	public void setOrderByAscending(boolean orderByAscending) {
		_orderByAscending = orderByAscending;
	}

	public void setOrderColumn(boolean orderColumn) {
		_orderColumn = orderColumn;
	}

	public void setParentContainerModel(boolean parentContainerModel) {
		_parentContainerModel = parentContainerModel;
	}

	public void validate() {
		if (Validator.isNotNull(_arrayableOperator) && !_type.equals("char") &&
			!_type.equals("int") && !_type.equals("long") &&
			!_type.equals("short") && !_type.equals("String")) {

			throw new IllegalArgumentException(
				"Type " + _type + " cannot be arrayable");
		}

		String comparator = _comparator;

		if (comparator == null) {
			comparator = StringPool.EQUAL;
		}

		if (_arrayableOperator.equals("AND") &&
			!comparator.equals(StringPool.NOT_EQUAL)) {

			throw new IllegalArgumentException(
				"Illegal combination of arrayable \"AND\" and comparator \"" +
					comparator + "\"");
		}

		if (_arrayableOperator.equals("OR") &&
			!comparator.equals(StringPool.EQUAL) &&
			!comparator.equals(StringPool.LIKE)) {

			throw new IllegalArgumentException(
				"Illegal combination of arrayable \"OR\" and comparator \"" +
					comparator + "\"");
		}

		if (!_arrayableOperator.equals("OR") && _arrayablePagination) {
			throw new IllegalArgumentException(
				"Illegal combination of arrayable \"OR\" and arrayable " +
					"pagination");
		}
	}

	protected String convertComparatorToHtml(String comparator) {
		if (comparator.equals(">")) {
			return "&gt;";
		}

		if (comparator.equals("<")) {
			return "&lt;";
		}

		if (comparator.equals(">=")) {
			return "&ge;";
		}

		if (comparator.equals("<=")) {
			return "&le;";
		}

		if (comparator.equals("!=")) {
			return "&ne;";
		}

		return comparator;
	}

	private boolean _isUADUserId(String name) {
		if (name.equals("userId") || name.endsWith("UserId")) {
			return true;
		}

		return false;
	}

	private static final Map<String, String> _accessorNames =
		Collections.singletonMap(
			"com.liferay.portal.kernel.model.LayoutFriendlyURL." +
				"layoutFriendlyURLId",
			"LAYOUT_FRIENDLY_U_R_L_ID_ACCESSOR");
	private static final Map<String, String> _finderColumnTypeNames =
		HashMapBuilder.put(
			"BigDecimal", "FinderColumn.Type.BIG_DECIMAL"
		).put(
			"boolean", "FinderColumn.Type.BOOLEAN"
		).put(
			"Date", "FinderColumn.Type.DATE"
		).put(
			"double", "FinderColumn.Type.DOUBLE"
		).put(
			"float", "FinderColumn.Type.FLOAT"
		).put(
			"int", "FinderColumn.Type.INTEGER"
		).put(
			"long", "FinderColumn.Type.LONG"
		).put(
			"short", "FinderColumn.Type.SHORT"
		).put(
			"String", "FinderColumn.Type.STRING"
		).build();

	private final boolean _accessor;
	private String _arrayableOperator;
	private boolean _arrayablePagination;
	private boolean _caseSensitive;
	private String _comparator;
	private boolean _containerModel;
	private boolean _convertNull;
	private final CTColumnResolutionType _ctColumnResolutionType;
	private String _dbName;
	private final String _entityName;
	private final boolean _filterPrimary;
	private final String _finderColumnTypeName;
	private boolean _finderPath;
	private final String _humanName;
	private String _idParam;
	private String _idType;
	private boolean _indexable = true;
	private boolean _interfaceColumn = true;
	private final boolean _jsonEnabled;
	private boolean _lazy;
	private boolean _localized;
	private final String _mappingTableName;
	private final String _methodName;
	private String _modelHintsName;
	private final String _name;
	private boolean _orderByAscending;
	private boolean _orderColumn;
	private boolean _parentContainerModel;
	private final String _pluralName;
	private final boolean _primary;
	private ServiceBuilder _serviceBuilder;
	private final String _type;
	private final String _uadAnonymizeFieldName;
	private final boolean _uadNonanonymizable;

}