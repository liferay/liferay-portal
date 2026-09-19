/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.aggregation.bucket;

import com.liferay.portal.search.aggregation.bucket.IncludeExcludeClause;

/**
 * @author Michael C. Han
 */
public class IncludeExcludeClauseImpl implements IncludeExcludeClause {

	public IncludeExcludeClauseImpl(String includeRegex, String excludeRegex) {
		_includeRegex = includeRegex;
		_excludeRegex = excludeRegex;
	}

	public IncludeExcludeClauseImpl(
		String[] includedValues, String[] excludedValues) {

		_includedValues = includedValues;
		_excludedValues = excludedValues;
	}

	@Override
	public String getExcludeRegex() {
		return _excludeRegex;
	}

	@Override
	public String[] getExcludedValues() {
		return _excludedValues;
	}

	@Override
	public String getIncludeRegex() {
		return _includeRegex;
	}

	@Override
	public String[] getIncludedValues() {
		return _includedValues;
	}

	private String _excludeRegex;
	private String[] _excludedValues;
	private String _includeRegex;
	private String[] _includedValues;

}