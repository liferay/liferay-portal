/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.filter;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.search.filter.FilterVisitor;
import com.liferay.portal.search.filter.TermsSetFilter;

import java.util.Collections;
import java.util.List;

/**
 * @author Marco Leo
 * @author André de Oliveira
 */
public class TermsSetFilterImpl implements TermsSetFilter {

	public TermsSetFilterImpl(String fieldName, List<String> values) {
		_fieldName = fieldName;
		_values = values;
	}

	@Override
	public <T> T accept(FilterVisitor<T> filterVisitor) {
		return filterVisitor.visit(this);
	}

	@Override
	public String getExecutionOption() {
		return null;
	}

	@Override
	public String getFieldName() {
		return _fieldName;
	}

	@Override
	public String getMinimumShouldMatchField() {
		return _minimumShouldMatchField;
	}

	@Override
	public int getSortOrder() {
		return 4;
	}

	@Override
	public List<String> getValues() {
		return Collections.unmodifiableList(_values);
	}

	@Override
	public Boolean isCached() {
		return true;
	}

	@Override
	public void setCached(Boolean cached) {
	}

	@Override
	public void setExecutionOption(String executionOption) {
	}

	public void setMinimumShouldMatchField(String minimumShouldMatchField) {
		_minimumShouldMatchField = minimumShouldMatchField;
	}

	@Override
	public String toString() {
		return StringBundler.concat(
			"{(", _fieldName, "=", _values, "), (minimum_should_match_field=",
			_minimumShouldMatchField, ")}");
	}

	private final String _fieldName;
	private String _minimumShouldMatchField;
	private final List<String> _values;

}