/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet.taglib;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author Carlos Sierra Andrés
 */
public interface TagDynamicInclude {

	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String tagClassName,
			String tagDynamicId, String tagPoint)
		throws IOException;

	public void register(TagDynamicIncludeRegistry tagDynamicIncludeRegistry);

	public interface TagDynamicIncludeRegistry {

		public void register(
			String tagClassName, String tagDynamicId, String tagPoint);

	}

}