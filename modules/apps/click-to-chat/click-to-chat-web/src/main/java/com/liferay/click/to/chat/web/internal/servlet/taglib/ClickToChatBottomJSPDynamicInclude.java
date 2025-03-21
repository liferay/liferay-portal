/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.click.to.chat.web.internal.servlet.taglib;

import com.liferay.click.to.chat.web.internal.configuration.ClickToChatConfiguration;
import com.liferay.click.to.chat.web.internal.configuration.ClickToChatConfigurationUtil;
import com.liferay.click.to.chat.web.internal.constants.ClickToChatConstants;
import com.liferay.click.to.chat.web.internal.constants.ClickToChatWebKeys;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.servlet.taglib.BaseJSPDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author José Abelenda
 */
@Component(service = DynamicInclude.class)
public class ClickToChatBottomJSPDynamicInclude extends BaseJSPDynamicInclude {

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		if (LiferayWindowState.isPopUp(httpServletRequest)) {
			return;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Group group = themeDisplay.getScopeGroup();

		if (group.isStagingGroup()) {
			return;
		}

		ClickToChatConfiguration clickToChatConfiguration =
			ClickToChatConfigurationUtil.getClickToChatConfiguration(
				themeDisplay.getCompanyId(), themeDisplay.getSiteGroupId());

		if ((clickToChatConfiguration == null) ||
			!clickToChatConfiguration.enabled()) {

			return;
		}

		if (!clickToChatConfiguration.guestUsersAllowed() &&
			!themeDisplay.isSignedIn()) {

			return;
		}

		if (Validator.isNull(
				clickToChatConfiguration.chatProviderAccountId()) ||
			Validator.isNull(clickToChatConfiguration.chatProviderId())) {

			return;
		}

		Layout layout = themeDisplay.getLayout();

		if (clickToChatConfiguration.hideInControlPanel() &&
			layout.isTypeControlPanel()) {

			super.include(httpServletRequest, httpServletResponse, key);

			return;
		}

		if (themeDisplay.isSignedIn() &&
			(clickToChatConfiguration.chatProviderId() ==
				ClickToChatConstants.CHAT_PROVIDER_ID_ZENDESK_WEB_WIDGET) &&
			(Validator.isNull(clickToChatConfiguration.chatProviderKeyId()) ||
			 Validator.isNull(
				 clickToChatConfiguration.chatProviderSecretKey()))) {

			return;
		}

		httpServletRequest.setAttribute(
			ClickToChatWebKeys.CLICK_TO_CHAT_CHAT_PROVIDER_ACCOUNT_ID,
			clickToChatConfiguration.chatProviderAccountId());
		httpServletRequest.setAttribute(
			ClickToChatWebKeys.CLICK_TO_CHAT_CHAT_PROVIDER_ID,
			clickToChatConfiguration.chatProviderId());
		httpServletRequest.setAttribute(
			ClickToChatWebKeys.CLICK_TO_CHAT_CHAT_PROVIDER_KEY_ID,
			clickToChatConfiguration.chatProviderKeyId());
		httpServletRequest.setAttribute(
			ClickToChatWebKeys.CLICK_TO_CHAT_CHAT_PROVIDER_SECRET_KEY,
			clickToChatConfiguration.chatProviderSecretKey());

		super.include(httpServletRequest, httpServletResponse, key);
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register("/html/common/themes/bottom.jsp#post");
	}

	@Override
	protected String getJspPath() {
		return "/dynamic_include/view.jsp";
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ClickToChatBottomJSPDynamicInclude.class);

	@Reference(target = "(osgi.web.symbolicname=com.liferay.click.to.chat.web)")
	private ServletContext _servletContext;

}