/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import jakarta.portlet.MutablePortletParameters;

/**
 * @author Neil Griffin
 */
public interface LiferayMutablePortletParameters
	extends MutablePortletParameters {

	public static final String NULL_PARAM_VALUE = "_!null!_";

	/**
	 * Returns <code>true</code> if the state of the portlet parameters has
	 * changed.
	 *
	 * @return <code>true</code> if the state has changed; <code>false</code>
	 *         otherwise
	 */
	public boolean isMutated();

	/**
	 * Sets the parameter value. An <code>IllegalArgumentException</code> is
	 * thrown if the name is <code>null</code>.
	 *
	 * @param  name the parameter's name
	 * @param  value the parameter's value. If <code>null</code>, the parameter
	 *         is removed.
	 * @param  append whether the new value is appended to any existing values
	 *         for the parameter. If this is <code>false</code>, any existing
	 *         values are overwritten with the new value.
	 * @return the parameter value prior to setting
	 */
	public String setValue(String name, String value, boolean append);

	/**
	 * Sets the parameter values. An <code>IllegalArgumentException</code> is
	 * thrown if the name is <code>null</code>.
	 *
	 * @param  name the parameter's name
	 * @param  values the parameter's values. If <code>null</code>, the
	 *         parameter is removed.
	 * @param  append whether the new values are appended to any existing values
	 *         for the parameter. If this is <code>false</code>, any existing
	 *         values are overwritten with the new values.
	 * @return the parameter values prior to setting
	 */
	public String[] setValues(String name, String[] values, boolean append);

}