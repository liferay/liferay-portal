/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.css.variables;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collection;

/**
 * @author Iván Zaera Avellón
 */
public interface ScopedCSSVariablesProvider {

	public Collection<ScopedCSSVariables> getScopedCSSVariablesCollection(
		HttpServletRequest httpServletRequest);

}