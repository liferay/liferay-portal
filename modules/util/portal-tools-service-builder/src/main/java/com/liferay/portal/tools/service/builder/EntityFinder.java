/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 * @author Connor McKay
 */
public class EntityFinder {

	public EntityFinder(
		ServiceBuilder serviceBuilder, String name, String pluralName,
		boolean pretouch, String returnType, boolean unique, String where,
		String dbWhere, boolean dbIndex, List<EntityColumn> entityColumns) {

		_serviceBuilder = serviceBuilder;
		_name = name;
		_pluralName = GetterUtil.getString(
			pluralName, serviceBuilder.formatPlural(name));
		_pretouch = pretouch;
		_returnType = returnType;
		_unique = unique;
		_where = where;
		_dbWhere = dbWhere;
		_dbIndex = dbIndex;

		_entityColumns = entityColumns;

		for (EntityColumn column : _entityColumns) {
			if (column.hasArrayableOperator()) {
				_arrayableColumns.add(column);
			}
		}

		if (isCollection() && isUnique() && !hasArrayableOperator()) {
			throw new IllegalArgumentException(
				"A finder cannot return a Collection and be unique unless it " +
					"has an arrayable column. See the ExpandoColumn " +
						"service.xml declaration for an example.");
		}

		if ((!isCollection() || isUnique()) && hasCustomComparator()) {
			throw new IllegalArgumentException(
				"A unique finder cannot have a custom comparator");
		}
	}

	public List<EntityColumn> getArrayableOrEntityColumns() {
		List<EntityColumn> arrayableOrEntityColumns = new ArrayList<>();

		for (EntityColumn arrayableEntityColumn : _arrayableColumns) {
			if (!arrayableEntityColumn.isArrayableAndOperator()) {
				arrayableOrEntityColumns.add(arrayableEntityColumn);
			}
		}

		return arrayableOrEntityColumns;
	}

	public String getDBWhere() {
		return _dbWhere;
	}

	public EntityColumn getEntityColumn(String name) {
		for (EntityColumn entityColumn : _entityColumns) {
			if (name.equals(entityColumn.getName())) {
				return entityColumn;
			}
		}

		return null;
	}

	public List<EntityColumn> getEntityColumns() {
		return _entityColumns;
	}

	public String getHumanConditions(boolean arrayable) {
		if (_entityColumns.size() == 1) {
			EntityColumn entityColumn = _entityColumns.get(0);

			return entityColumn.getHumanCondition(arrayable);
		}

		StringBundler sb = new StringBundler(_entityColumns.size() * 2);

		for (EntityColumn column : _entityColumns) {
			sb.append(column.getHumanCondition(arrayable));
			sb.append(" and ");
		}

		if (!_entityColumns.isEmpty()) {
			sb.setIndex(sb.index() - 1);
		}

		return sb.toString();
	}

	public String getName() {
		return _name;
	}

	public String getPluralName() {
		return _pluralName;
	}

	public String getReturnType() {
		return _returnType;
	}

	public String getWhere() {
		return _where;
	}

	public boolean hasArrayableOperator() {
		for (EntityColumn column : _entityColumns) {
			if (column.hasArrayableOperator()) {
				return true;
			}
		}

		return false;
	}

	public boolean hasArrayablePagination() {
		for (EntityColumn column : _entityColumns) {
			if (column.hasArrayablePagination()) {
				return true;
			}
		}

		return false;
	}

	public boolean hasCustomComparator() {
		for (EntityColumn column : _entityColumns) {
			String comparator = column.getComparator();

			if (!comparator.equals(StringPool.EQUAL)) {
				return true;
			}
		}

		return false;
	}

	public boolean hasEntityColumn(String name) {
		return Entity.hasEntityColumn(_serviceBuilder, name, _entityColumns);
	}

	public boolean isCollection() {
		if ((_returnType != null) && _returnType.equals("Collection")) {
			return true;
		}

		return false;
	}

	public boolean isCollectionPersistenceFinderEnabled() {
		if (_serviceBuilder.isVersionGTE_7_4_0() && isCollection()) {
			return true;
		}

		return false;
	}

	public boolean isDBIndex() {
		return _dbIndex;
	}

	public boolean isPretouch() {
		return _pretouch;
	}

	public boolean isUnique() {
		return _unique;
	}

	public boolean isUniquePersistenceFinderEnabled() {
		if (_serviceBuilder.isVersionGTE_7_4_0() &&
			(!isCollection() || isUnique())) {

			return true;
		}

		return false;
	}

	private final List<EntityColumn> _arrayableColumns = new ArrayList<>();
	private final boolean _dbIndex;
	private final String _dbWhere;
	private final List<EntityColumn> _entityColumns;
	private final String _name;
	private final String _pluralName;
	private final boolean _pretouch;
	private final String _returnType;
	private final ServiceBuilder _serviceBuilder;
	private final boolean _unique;
	private final String _where;

}