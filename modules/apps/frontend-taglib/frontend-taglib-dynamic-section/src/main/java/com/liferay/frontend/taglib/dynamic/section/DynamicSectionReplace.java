/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.dynamic.section;

import jakarta.servlet.ServletException;
import jakarta.servlet.jsp.PageContext;

import java.io.IOException;

/**
 * @author Matthew Tambara
 */
public interface DynamicSectionReplace {

	public String replace(PageContext pageContext)
		throws IOException, ServletException;

}