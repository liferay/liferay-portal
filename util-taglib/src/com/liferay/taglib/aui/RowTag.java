/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui;

import com.liferay.taglib.aui.base.BaseRowTag;

import jakarta.servlet.jsp.JspWriter;

/**
 * @author     Eduardo Lundgren
 * @author     Bruno Basto
 * @author     Nathan Cavanaugh
 * @author     Julio Camarero
 * @deprecated As of Athanasius (7.3.x), replaced by clay:row
 */
@Deprecated
public class RowTag extends BaseRowTag {

	@Override
	protected int processEndTag() throws Exception {
		JspWriter jspWriter = pageContext.getOut();

		jspWriter.write("</div>");

		return EVAL_PAGE;
	}

}