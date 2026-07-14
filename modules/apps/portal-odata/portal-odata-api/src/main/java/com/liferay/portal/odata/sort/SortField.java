/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.odata.sort;

import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.odata.entity.EntityField;

import java.io.Serializable;

import java.util.List;
import java.util.Locale;

/**
 * Models a sort field.
 *
 * @author Cristina González
 * @review
 */
public class SortField implements Serializable {

	public SortField(
		boolean asc, EntityField entityField,
		List<EntityField> parentEntityFields) {

		if (entityField == null) {
			throw new IllegalArgumentException("Entity field is null");
		}

		_asc = asc;
		_entityField = entityField;
		_parentEntityFields = parentEntityFields;

		_fieldName = entityField.getName();
	}

	/**
	 * Creates a new sort field.
	 *
	 * @param  entityField the entity field
	 * @param  asc whether the sort should be ascending
	 * @review
	 */
	public SortField(EntityField entityField, boolean asc) {
		this(asc, entityField, null);
	}

	/**
	 * Creates a new sort field not linked to a entityField
	 *
	 * @param  fieldName the entity field name
	 * @param  asc whether the sort should be ascending
	 * @review
	 */
	public SortField(String fieldName, boolean asc) {
		_fieldName = fieldName;
		_asc = asc;

		_entityField = null;
		_parentEntityFields = null;
	}

	public EntityField.Type getEntityFieldType() {
		if (_entityField == null) {
			return null;
		}

		return _entityField.getType();
	}

	/**
	 * Returns the field's name.
	 *
	 * @param  locale the locale
	 * @return the field's name
	 * @review
	 */
	public String getSortableFieldName(Locale locale) {
		if (_entityField == null) {
			return _fieldName;
		}

		return _entityField.getSortableName(locale);
	}

	public String getSortableFieldPath(Locale locale) {
		String sortableFieldName = getSortableFieldName(locale);

		if (ListUtil.isEmpty(_parentEntityFields)) {
			return sortableFieldName;
		}

		String prefix = StringUtil.merge(
			_parentEntityFields,
			parentEntityField -> parentEntityField.getSortableName(locale),
			StringPool.FORWARD_SLASH);

		return prefix + StringPool.FORWARD_SLASH + sortableFieldName;
	}

	/**
	 * Returns {@code true} if the sort field is ascending.
	 *
	 * @return {@code true} if the sort field is ascending; {@code false}
	 *         otherwise
	 * @review
	 */
	public boolean isAscending() {
		return _asc;
	}

	private final boolean _asc;
	private final EntityField _entityField;
	private final String _fieldName;
	private final List<EntityField> _parentEntityFields;

}