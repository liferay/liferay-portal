/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template.react.renderer;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.Writer;

import java.util.Map;

/**
 * @author Chema Balsas
 */
public interface ReactRenderer {

	public void renderReact(
			ComponentDescriptor componentDescriptor, Map<String, Object> data,
			HttpServletRequest httpServletRequest, Writer writer)
		throws IOException;

}