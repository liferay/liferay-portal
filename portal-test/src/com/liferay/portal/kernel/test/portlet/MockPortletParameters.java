/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.portlet;

import com.liferay.portal.kernel.util.ArrayUtil;

import jakarta.portlet.MutablePortletParameters;
import jakarta.portlet.PortletParameters;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Dante Wang
 */
public class MockPortletParameters implements PortletParameters {

	@Override
	public MutablePortletParameters clone() {
		return null;
	}

	@Override
	public Set<String> getNames() {
		return parameters.keySet();
	}

	@Override
	public String getValue(String name) {
		String[] values = parameters.get(name);

		if (ArrayUtil.isEmpty(values)) {
			return null;
		}

		return values[0];
	}

	@Override
	public String[] getValues(String name) {
		return parameters.get(name);
	}

	@Override
	public boolean isEmpty() {
		return parameters.isEmpty();
	}

	@Override
	public int size() {
		return parameters.size();
	}

	protected Map<String, String[]> parameters = new HashMap<>();

}