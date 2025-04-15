/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.portlet;

import com.liferay.portal.kernel.util.ArrayUtil;

import jakarta.portlet.MutablePortletParameters;
import jakarta.portlet.PortletParameters;

import java.util.Set;

/**
 * @author Dante Wang
 */
public class MockMutablePortletParameters
	extends MockPortletParameters implements MutablePortletParameters {

	@Override
	public MutablePortletParameters add(PortletParameters portletParameters) {
		MutablePortletParameters mutablePortletParameters = clone();

		if (portletParameters != null) {
			Set<String> names = portletParameters.getNames();

			for (String name : names) {
				parameters.put(name, portletParameters.getValues(name));
			}
		}

		return mutablePortletParameters;
	}

	@Override
	public void clear() {
		parameters.clear();
	}

	@Override
	public boolean removeParameter(String name) {
		String[] values = parameters.remove(name);

		if (values != null) {
			return true;
		}

		return false;
	}

	@Override
	public MutablePortletParameters set(PortletParameters portletParameters) {
		MutablePortletParameters mutablePortletParameters = clone();

		parameters.clear();

		if (portletParameters != null) {
			Set<String> names = portletParameters.getNames();

			for (String name : names) {
				parameters.put(name, portletParameters.getValues(name));
			}
		}

		return mutablePortletParameters;
	}

	@Override
	public String setValue(String name, String value) {
		String[] oldValues = parameters.remove(name);

		if (value == null) {
			parameters.put(name, null);
		}
		else {
			parameters.put(name, new String[] {value});
		}

		if (ArrayUtil.isEmpty(oldValues)) {
			return null;
		}

		return oldValues[0];
	}

	@Override
	public String[] setValues(String name, String... values) {
		String[] oldValues = parameters.remove(name);

		parameters.put(name, values);

		return oldValues;
	}

}