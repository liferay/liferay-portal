/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.login.web.internal.servlet.taglib.include;

import com.liferay.layout.utility.page.kernel.constants.LayoutUtilityPageEntryConstants;
import com.liferay.layout.utility.page.kernel.provider.LayoutUtilityPageEntryLayoutProvider;
import com.liferay.login.web.constants.LoginPortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;
import com.liferay.taglib.include.PageInclude;
import com.liferay.taglib.ui.IconTag;

import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletRequest;
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
		"login.web.navigation.position=post", "service.ranking:Integer=200"
	},
	service = PageInclude.class
)
public class CreateAccountNavigationPostPageInclude implements PageInclude {

	@Override
	public void include(PageContext pageContext) throws JspException {
		HttpServletRequest httpServletRequest =
			(HttpServletRequest)pageContext.getRequest();

		String mvcRenderCommandName = httpServletRequest.getParameter(
			"mvcRenderCommandName");

		if (FeatureFlagManagerUtil.isEnabled("LPD-6378")) {
			PortletConfig portletConfig =
				(PortletConfig)httpServletRequest.getAttribute(
					JavaConstants.JAKARTA_PORTLET_CONFIG);

			String portletName = portletConfig.getPortletName();

			if (portletName.equals(LoginPortletKeys.CREATE_ACCOUNT) &&
				Validator.isNull(mvcRenderCommandName)) {

				return;
			}
		}

		if (Objects.equals(mvcRenderCommandName, "/login/create_account")) {
			return;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Company company = themeDisplay.getCompany();

		if (!company.isStrangers()) {
			return;
		}

		IconTag iconTag = new IconTag();

		iconTag.setCssClass("text-4");
		iconTag.setMessage("create-account");

		try {
			String url = StringPool.BLANK;

			if (FeatureFlagManagerUtil.isEnabled("LPD-6378")) {
				Layout layout =
					_layoutUtilityPageEntryLayoutProvider.
						getDefaultLayoutUtilityPageEntryLayout(
							themeDisplay.getScopeGroupId(),
							LayoutUtilityPageEntryConstants.
								TYPE_CREATE_ACCOUNT);

				if (layout != null) {
					url = _portal.getLayoutURL(layout, themeDisplay);
				}
				else {
					url = _getCreateAccountURL(
						httpServletRequest, themeDisplay);
				}
			}
			else {
				url = _portal.getCreateAccountURL(
					httpServletRequest, themeDisplay);
			}

			iconTag.setUrl(url);
		}
		catch (Exception exception) {
			throw new JspException(exception);
		}

		iconTag.doTag(pageContext);
	}

	private String _getCreateAccountURL(
			HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay)
		throws Exception {

		if (Validator.isNull(PropsValues.COMPANY_SECURITY_STRANGERS_URL)) {
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
				"/login/create_account"
			).setParameter(
				"saveLastPath", false
			).setPortletMode(
				PortletMode.VIEW
			).setWindowState(
				WindowState.MAXIMIZED
			).buildString();
		}

		try {
			Layout layout = _layoutLocalService.getFriendlyURLLayout(
				themeDisplay.getScopeGroupId(), false,
				PropsValues.COMPANY_SECURITY_STRANGERS_URL);

			return _portal.getLayoutURL(layout, themeDisplay);
		}
		catch (NoSuchLayoutException noSuchLayoutException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(noSuchLayoutException);
			}
		}

		return StringPool.BLANK;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CreateAccountNavigationPostPageInclude.class);

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutUtilityPageEntryLayoutProvider
		_layoutUtilityPageEntryLayoutProvider;

	@Reference
	private Portal _portal;

}