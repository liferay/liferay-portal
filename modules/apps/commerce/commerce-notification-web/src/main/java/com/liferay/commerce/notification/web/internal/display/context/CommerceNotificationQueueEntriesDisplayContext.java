/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.notification.web.internal.display.context;

import com.liferay.commerce.notification.web.internal.display.context.helper.CommerceNotificationsRequestHelper;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceNotificationQueueEntriesDisplayContext {

	public CommerceNotificationQueueEntriesDisplayContext(
		CommerceChannelLocalService commerceChannelLocalService,
		HttpServletRequest httpServletRequest) {

		_commerceChannelLocalService = commerceChannelLocalService;

		_commerceNotificationsRequestHelper =
			new CommerceNotificationsRequestHelper(httpServletRequest);
	}

	public String getAddNotificationTemplateURL() throws Exception {
		return PortletURLBuilder.create(
			PortletProviderUtil.getPortletURL(
				_commerceNotificationsRequestHelper.getRequest(),
				CommerceChannel.class.getName(), PortletProvider.Action.MANAGE)
		).setMVCRenderCommandName(
			"/commerce_channels/edit_commerce_notification_template"
		).setParameter(
			"commerceChannelId", getCommerceChannelId()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public CommerceChannel getCommerceChannel() throws PortalException {
		long commerceChannelId = ParamUtil.getLong(
			_commerceNotificationsRequestHelper.getRequest(),
			"commerceChannelId");

		if (commerceChannelId <= 0) {
			return null;
		}

		return _commerceChannelLocalService.getCommerceChannel(
			commerceChannelId);
	}

	public long getCommerceChannelId() throws PortalException {
		CommerceChannel commerceChannel = getCommerceChannel();

		if (commerceChannel == null) {
			return 0;
		}

		return commerceChannel.getCommerceChannelId();
	}

	public CreationMenu getNotificationTemplateCreationMenu() throws Exception {
		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(getAddNotificationTemplateURL());
				dropdownItem.setLabel(
					LanguageUtil.get(
						_commerceNotificationsRequestHelper.getRequest(),
						"add-notification-template"));
				dropdownItem.setTarget("sidePanel");
			}
		).build();
	}

	public PortletURL getPortletURL() {
		LiferayPortletResponse liferayPortletResponse =
			_commerceNotificationsRequestHelper.getLiferayPortletResponse();

		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		String delta = ParamUtil.getString(
			_commerceNotificationsRequestHelper.getRequest(), "delta");

		if (Validator.isNotNull(delta)) {
			portletURL.setParameter("delta", delta);
		}

		return portletURL;
	}

	private final CommerceChannelLocalService _commerceChannelLocalService;
	private final CommerceNotificationsRequestHelper
		_commerceNotificationsRequestHelper;

}