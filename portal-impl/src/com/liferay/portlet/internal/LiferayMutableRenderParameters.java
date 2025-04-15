/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import jakarta.portlet.MutableRenderParameters;

/**
 * @author Neil Griffin
 */
public interface LiferayMutableRenderParameters
	extends LiferayMutablePortletParameters, MutableRenderParameters {

	/**
	 * Returns <code>true</code> if the value of the parameter associated with
	 * the specified name has changed.
	 *
	 * @param  name the parameter's name
	 * @return <code>true</code> if the value of the parameter associated with
	 *         the specified name has changed; <code>false</code> otherwise
	 */
	public boolean isMutated(String name);

}