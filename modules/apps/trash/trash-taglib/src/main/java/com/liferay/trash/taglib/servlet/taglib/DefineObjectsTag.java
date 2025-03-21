/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.taglib.servlet.taglib;

import com.liferay.trash.TrashHelper;
import com.liferay.trash.util.TrashWebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;

/**
 * @author Eudaldo Alonso
 */
public class DefineObjectsTag extends TagSupport {

	@Override
	public int doStartTag() throws JspException {
		HttpServletRequest httpServletRequest =
			(HttpServletRequest)pageContext.getRequest();

		TrashHelper trashHelper = (TrashHelper)httpServletRequest.getAttribute(
			TrashWebKeys.TRASH_HELPER);

		if (trashHelper != null) {
			pageContext.setAttribute("trashHelper", trashHelper);
		}

		return SKIP_BODY;
	}

}