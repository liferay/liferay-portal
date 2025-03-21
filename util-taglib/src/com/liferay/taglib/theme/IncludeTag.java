/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.theme;

import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.theme.ThemeUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.servlet.PipingServletResponseFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

/**
 * @author Brian Wing Shun Chan
 */
public class IncludeTag extends com.liferay.taglib.util.IncludeTag {

	@Override
	public int doEndTag() throws JspException {
		try {
			HttpServletRequest httpServletRequest = getRequest();

			Theme theme = (Theme)httpServletRequest.getAttribute(WebKeys.THEME);

			ThemeUtil.include(
				getServletContext(), httpServletRequest,
				PipingServletResponseFactory.createPipingServletResponse(
					pageContext),
				getPage(), theme);

			return EVAL_PAGE;
		}
		catch (Exception exception) {
			throw new JspException(exception);
		}
	}

}