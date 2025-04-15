/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.convert;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Iván Zaera
 */
public interface ConvertProcess {

	public void convert() throws ConvertException;

	public String getConfigurationErrorMessage();

	public String getDescription();

	public String getParameterDescription();

	public String[] getParameterNames();

	/**
	 * @deprecated As of Mueller (7.2.x), with no direct replacement
	 */
	@Deprecated
	public default String getPath() {
		return null;
	}

	public default boolean hasCustomView() {
		return false;
	}

	public default boolean includeCustomView(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		return false;
	}

	public boolean isEnabled();

	public void setParameterValues(String[] values);

	public void validate() throws ConvertException;

}