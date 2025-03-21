/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.portlet;

import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.taglib.TagSupport;

import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

/**
 * @author Brian Wing Shun Chan
 */
public class NamespaceTag extends TagSupport {

	@Override
	public int doStartTag() throws JspException {
		try {
			HttpServletRequest httpServletRequest =
				(HttpServletRequest)pageContext.getRequest();

			PortletResponse portletResponse =
				(PortletResponse)httpServletRequest.getAttribute(
					JavaConstants.JAVAX_PORTLET_RESPONSE);

			if (portletResponse != null) {
				JspWriter jspWriter = pageContext.getOut();

				jspWriter.write(portletResponse.getNamespace());
			}
		}
		catch (Exception exception) {
			throw new JspException(exception);
		}

		return SKIP_BODY;
	}

}