/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import jakarta.portlet.RenderParameters;

import java.util.Set;

/**
 * @author Neil Griffin
 */
public interface LiferayRenderParameters extends RenderParameters {

	/**
	 * Returns the public render parameter names.
	 *
	 * @return the public render parameter names
	 */
	public Set<String> getPublicRenderParameterNames();

}