/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.management.toolbar;

import com.liferay.portal.kernel.util.ArrayUtil;

import java.util.Locale;
import java.util.Map;

/**
 * @author Drew Brokke
 */
public interface FilterContributor {

	public default String getCurrentValue(String currentValue) {
		return currentValue;
	}

	public String getDefaultValue();

	public default String[] getFilterLabelValues() {
		return ArrayUtil.filter(
			getValues(), value -> !value.equals(getDefaultValue()));
	}

	public String getLabel(Locale locale);

	public String getParameter();

	public Map<String, Object> getSearchParameters(String currentValue);

	public String getShortLabel(Locale locale);

	public String getValueLabel(Locale locale, String value);

	public String[] getValues();

}