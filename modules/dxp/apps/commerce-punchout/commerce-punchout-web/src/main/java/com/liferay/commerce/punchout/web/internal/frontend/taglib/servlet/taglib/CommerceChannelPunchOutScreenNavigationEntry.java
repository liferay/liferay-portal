/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.punchout.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.punchout.configuration.PunchOutConfiguration;
import com.liferay.commerce.punchout.constants.PunchOutConstants;
import com.liferay.commerce.punchout.web.internal.display.context.CommercePunchOutDisplayContext;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jaclyn Ong
 */
@Component(
	property = "screen.navigation.entry.order:Integer=" + Integer.MAX_VALUE,
	service = ScreenNavigationEntry.class
)
public class CommerceChannelPunchOutScreenNavigationEntry
	extends CommerceChannelPunchOutScreenNavigationCategory
	implements ScreenNavigationEntry<CommerceChannel> {

	@Override
	public String getEntryKey() {
		return getCategoryKey();
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		CommerceChannel commerceChannel = _getCommerceChannel(
			httpServletRequest);

		PunchOutConfiguration punchOutConfiguration = _getPunchOutConfiguration(
			commerceChannel.getGroupId());

		CommercePunchOutDisplayContext commerceShippingMethodsDisplayContext =
			new CommercePunchOutDisplayContext(
				commerceChannel.getCommerceChannelId(), punchOutConfiguration);

		httpServletRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			commerceShippingMethodsDisplayContext);

		_jspRenderer.renderJSP(
			_servletContext, httpServletRequest, httpServletResponse,
			"/configuration.jsp");
	}

	private CommerceChannel _getCommerceChannel(
		HttpServletRequest httpServletRequest) {

		try {
			long commerceChannelId = ParamUtil.getLong(
				httpServletRequest, "commerceChannelId");

			return _commerceChannelLocalService.getCommerceChannel(
				commerceChannelId);
		}
		catch (Exception exception) {
			_log.error("Unable to get commerce channel", exception);
		}

		return null;
	}

	private PunchOutConfiguration _getPunchOutConfiguration(
		long commerceChannelGroupId) {

		try {
			return _configurationProvider.getConfiguration(
				PunchOutConfiguration.class,
				new GroupServiceSettingsLocator(
					commerceChannelGroupId, PunchOutConstants.SERVICE_NAME));
		}
		catch (Exception exception) {
			_log.error("Unable to get punch out configuration", exception);
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceChannelPunchOutScreenNavigationEntry.class);

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.punchout.web)"
	)
	private ServletContext _servletContext;

}