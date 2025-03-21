/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.notification.web.internal.frontend.data.set.provider;

import com.liferay.commerce.notification.web.internal.constants.CommerceNotificationFDSNames;
import com.liferay.commerce.notification.web.internal.model.NotificationTemplate;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.frontend.data.set.provider.FDSActionProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Victor Silvestre
 */
@Component(
	property = "fds.data.provider.key=" + CommerceNotificationFDSNames.NOTIFICATION_TEMPLATES,
	service = FDSActionProvider.class
)
public class CommerceNotificationTemplateFDSActionProvider
	implements FDSActionProvider {

	@Override
	public List<DropdownItem> getDropdownItems(
			long groupId, HttpServletRequest httpServletRequest, Object model)
		throws PortalException {

		NotificationTemplate notificationTemplate = (NotificationTemplate)model;

		return DropdownItemListBuilder.add(
			dropdownItem -> {
				PortletURL portletURL = PortletURLBuilder.create(
					PortletProviderUtil.getPortletURL(
						httpServletRequest, CommerceChannel.class.getName(),
						PortletProvider.Action.MANAGE)
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildPortletURL();

				long commerceChannelId = ParamUtil.getLong(
					httpServletRequest, "commerceChannelId");

				dropdownItem.setHref(
					portletURL, "mvcRenderCommandName",
					"/commerce_channels/edit_commerce_notification_template",
					"commerceChannelId", String.valueOf(commerceChannelId),
					"commerceNotificationTemplateId",
					String.valueOf(
						notificationTemplate.getNotificationTemplateId()));

				dropdownItem.setLabel(
					_language.get(httpServletRequest, "edit"));
				dropdownItem.setTarget("sidePanel");
			}
		).add(
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.create(
						_portal.getControlPanelPortletURL(
							httpServletRequest, CPPortletKeys.COMMERCE_CHANNELS,
							PortletRequest.ACTION_PHASE)
					).setActionName(
						"/commerce_channels/edit_commerce_notification_template"
					).setCMD(
						Constants.DELETE
					).setRedirect(
						ParamUtil.getString(
							httpServletRequest, "currentUrl",
							_portal.getCurrentURL(httpServletRequest))
					).setParameter(
						"commerceNotificationTemplateId",
						notificationTemplate.getNotificationTemplateId()
					).buildPortletURL());
				dropdownItem.setLabel(
					_language.get(httpServletRequest, "delete"));
			}
		).build();
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}