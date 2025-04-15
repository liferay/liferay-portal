/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.notification.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.commerce.notification.web.internal.display.context.CommerceNotificationQueueEntriesDisplayContext;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Victor Silvestre
 * @deprecated As of Cavanaugh (7.4.x)
 */
@Component(
	property = "screen.navigation.entry.order:Integer=10",
	service = ScreenNavigationEntry.class
)
@Deprecated
public class CommerceChannelNotificationsQueueScreenNavigationEntry
	extends CommerceChannelNotificationsQueueScreenNavigationCategory
	implements ScreenNavigationEntry<CommerceChannel> {

	@Override
	public String getEntryKey() {
		return getCategoryKey();
	}

	@Override
	public boolean isVisible(User user, CommerceChannel commerceChannel) {
		return CommerceChannelConstants.CHANNEL_TYPE_SITE.equals(
			commerceChannel.getType());
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		CommerceNotificationQueueEntriesDisplayContext
			commerceNotificationQueueEntriesDisplayContext =
				new CommerceNotificationQueueEntriesDisplayContext(
					_commerceChannelLocalService, httpServletRequest);

		httpServletRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			commerceNotificationQueueEntriesDisplayContext);

		_jspRenderer.renderJSP(
			_servletContext, httpServletRequest, httpServletResponse,
			"/view.jsp");
	}

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.notification.web)"
	)
	private ServletContext _servletContext;

}