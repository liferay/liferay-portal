/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.login.web.internal.servlet.taglib.include;

import com.liferay.layout.utility.page.kernel.constants.LayoutUtilityPageEntryConstants;
import com.liferay.layout.utility.page.kernel.provider.LayoutUtilityPageEntryLayoutProvider;
import com.liferay.login.web.constants.LoginPortletKeys;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.include.PageInclude;
import com.liferay.taglib.portlet.RenderURLTag;
import com.liferay.taglib.ui.IconTag;

import jakarta.portlet.PortletConfig;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 */
@Component(
	property = {
		"login.web.navigation.position=post", "service.ranking:Integer=100"
	},
	service = PageInclude.class
)
public class ForgetPasswordNavigationPostPageInclude implements PageInclude {

	@Override
	public void include(PageContext pageContext) throws JspException {
		HttpServletRequest httpServletRequest =
			(HttpServletRequest)pageContext.getRequest();

		String mvcRenderCommandName = httpServletRequest.getParameter(
			"mvcRenderCommandName");

		if (FeatureFlagManagerUtil.isEnabled("LPD-6378")) {
			PortletConfig portletConfig =
				(PortletConfig)httpServletRequest.getAttribute(
					JavaConstants.JAVAX_PORTLET_CONFIG);

			String portletName = portletConfig.getPortletName();

			if (portletName.equals(LoginPortletKeys.FORGOT_PASSWORD) &&
				Validator.isNull(mvcRenderCommandName)) {

				return;
			}
		}

		if (Objects.equals(mvcRenderCommandName, "/login/forgot_password")) {
			return;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Company company = themeDisplay.getCompany();

		if (!company.isSendPasswordResetLink()) {
			return;
		}

		try {
			Layout layout =
				_layoutUtilityPageEntryLayoutProvider.
					getDefaultLayoutUtilityPageEntryLayout(
						themeDisplay.getScopeGroupId(),
						LayoutUtilityPageEntryConstants.TYPE_FORGOT_PASSWORD);

			String forgetPasswordURL = null;

			if (layout != null) {
				forgetPasswordURL = _portal.getLayoutURL(layout, themeDisplay);
			}
			else {
				RenderURLTag renderURLTag = new RenderURLTag();

				renderURLTag.setPageContext(pageContext);

				renderURLTag.addParam("saveLastPath", Boolean.FALSE.toString());
				renderURLTag.addParam(
					"mvcRenderCommandName", "/login/forgot_password");
				renderURLTag.setVar("forgotPasswordURL");
				renderURLTag.setWindowState(WindowState.MAXIMIZED.toString());

				renderURLTag.doTag(pageContext);

				forgetPasswordURL = (String)pageContext.getAttribute(
					"forgotPasswordURL");
			}

			IconTag iconTag = new IconTag();

			iconTag.setCssClass("text-4");
			iconTag.setMessage("forgot-password");
			iconTag.setUrl(forgetPasswordURL);

			iconTag.doTag(pageContext);
		}
		catch (Exception exception) {
			throw new JspException(exception);
		}
	}

	@Reference
	private LayoutUtilityPageEntryLayoutProvider
		_layoutUtilityPageEntryLayoutProvider;

	@Reference
	private Portal _portal;

}