/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.login.web.internal.servlet.taglib.include;

import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.taglib.include.PageInclude;
import com.liferay.taglib.portlet.RenderURLTag;
import com.liferay.taglib.ui.IconTag;

import jakarta.portlet.PortletConfig;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

import org.osgi.service.component.annotations.Component;

/**
 * @author Shuyang Zhou
 */
@Component(
	property = {
		"login.web.navigation.position=pre", "service.ranking:Integer=200"
	},
	service = PageInclude.class
)
public class AnonymousNavigationPrePageInclude implements PageInclude {

	@Override
	public void include(PageContext pageContext) throws JspException {
		HttpServletRequest httpServletRequest =
			(HttpServletRequest)pageContext.getRequest();

		String mvcRenderCommandName = httpServletRequest.getParameter(
			"mvcRenderCommandName");

		if ((mvcRenderCommandName != null) &&
			mvcRenderCommandName.startsWith(
				"/login/create_anonymous_account")) {

			return;
		}

		PortletConfig portletConfig =
			(PortletConfig)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_CONFIG);

		String portletName = portletConfig.getPortletName();

		if (!portletName.equals(PortletKeys.FAST_LOGIN)) {
			return;
		}

		RenderURLTag renderURLTag = new RenderURLTag();

		renderURLTag.setPageContext(pageContext);

		renderURLTag.addParam(
			"mvcRenderCommandName", "/login/create_anonymous_account");
		renderURLTag.setVar("anonymousURL");
		renderURLTag.setWindowState(WindowState.MAXIMIZED.toString());

		renderURLTag.doTag(pageContext);

		String anonymousURL = (String)pageContext.getAttribute("anonymousURL");

		IconTag iconTag = new IconTag();

		iconTag.setCssClass("text-4");
		iconTag.setMessage("guest");
		iconTag.setUrl(anonymousURL);

		iconTag.doTag(pageContext);
	}

}