/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.search;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.search.query.QueryVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Selena Aungst
 */
public class TermsQuery extends Query {

	public TermsQuery(String field, Collection<String> values) {
		_field = field;
		_values = new ArrayList<>(values);
	}

	@Override
	public <T> T accept(QueryVisitor<T> queryVisitor) {
		return queryVisitor.visitQuery(this);
	}

	public String getField() {
		return _field;
	}

	public List<String> getValues() {
		return _values;
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(7);

		sb.append("{className=");

		Class<?> clazz = getClass();

		sb.append(clazz.getSimpleName());

		sb.append(", field=");
		sb.append(_field);
		sb.append(", values=");
		sb.append(_values);
		sb.append("}");

		return sb.toString();
	}

	private final String _field;
	private final List<String> _values;

}