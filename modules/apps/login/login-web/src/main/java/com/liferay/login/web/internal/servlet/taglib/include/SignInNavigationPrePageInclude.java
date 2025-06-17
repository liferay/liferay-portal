/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.login.web.internal.servlet.taglib.include;

import com.liferay.layout.utility.page.kernel.constants.LayoutUtilityPageEntryConstants;
import com.liferay.layout.utility.page.kernel.provider.LayoutUtilityPageEntryLayoutProvider;
import com.liferay.login.web.constants.LoginPortletKeys;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.include.PageInclude;
import com.liferay.taglib.ui.IconTag;

import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
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
		"login.web.navigation.position=pre", "service.ranking:Integer=100"
	},
	service = PageInclude.class
)
public class SignInNavigationPrePageInclude implements PageInclude {

	@Override
	public void include(PageContext pageContext) throws JspException {
		HttpServletRequest httpServletRequest =
			(HttpServletRequest)pageContext.getRequest();

		String mvcRenderCommandName = httpServletRequest.getParameter(
			"mvcRenderCommandName");

		PortletConfig portletConfig =
			(PortletConfig)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_CONFIG);

		String portletName = portletConfig.getPortletName();

		if (FeatureFlagManagerUtil.isEnabled("LPD-6378")) {
			if (portletName.equals(LoginPortletKeys.LOGIN) &&
				Validator.isNull(mvcRenderCommandName)) {

				return;
			}
		}
		else {
			if (Validator.isNull(mvcRenderCommandName)) {
				return;
			}
		}

		if (Objects.equals(mvcRenderCommandName, "/login/login")) {
			return;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String signInURL = null;

		try {
			if (FeatureFlagManagerUtil.isEnabled("LPD-6378")) {
				Layout layout =
					_layoutUtilityPageEntryLayoutProvider.
						getDefaultLayoutUtilityPageEntryLayout(
							themeDisplay.getScopeGroupId(),
							LayoutUtilityPageEntryConstants.TYPE_LOGIN);

				if (layout != null) {
					signInURL = _portal.getLayoutURL(layout, themeDisplay);
				}
				else {
					signInURL = _getSignInURL(httpServletRequest, themeDisplay);
				}
			}
			else {
				signInURL = themeDisplay.getURLSignIn();
			}
		}
		catch (Exception exception) {
			throw new JspException(exception);
		}

		if (portletName.equals(PortletKeys.FAST_LOGIN)) {
			PortletURL fastLoginURL = PortletURLBuilder.create(
				PortletURLFactoryUtil.create(
					httpServletRequest, PortletKeys.FAST_LOGIN,
					PortletRequest.RENDER_PHASE)
			).setMVCRenderCommandName(
				"/login/login"
			).setParameter(
				"saveLastPath", false
			).buildPortletURL();

			try {
				fastLoginURL.setPortletMode(PortletMode.VIEW);
				fastLoginURL.setWindowState(LiferayWindowState.POP_UP);
			}
			catch (PortletException portletException) {
				throw new JspException(portletException);
			}

			signInURL = fastLoginURL.toString();
		}

		IconTag iconTag = new IconTag();

		iconTag.setCssClass("text-4");
		iconTag.setMessage("sign-in");
		iconTag.setUrl(signInURL);

		iconTag.doTag(pageContext);
	}

	private String _getSignInURL(
			HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay)
		throws Exception {

		long plid = themeDisplay.getPlid();

		Layout layout = themeDisplay.getLayout();

		if (layout.isPrivateLayout()) {
			plid = _layoutLocalService.getDefaultPlid(
				layout.getGroupId(), false);
		}

		PortletConfig portletConfig =
			(PortletConfig)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_CONFIG);

		return PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				httpServletRequest, portletConfig.getPortletName(), plid,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/login/login"
		).setParameter(
			"saveLastPath", false
		).setPortletMode(
			PortletMode.VIEW
		).setWindowState(
			WindowState.MAXIMIZED
		).buildString();
	}

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutUtilityPageEntryLayoutProvider
		_layoutUtilityPageEntryLayoutProvider;

	@Reference
	private Portal _portal;

}