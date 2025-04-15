/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.notification.web.internal.frontend.data.set.provider;

import com.liferay.commerce.notification.web.internal.constants.CommerceNotificationFDSNames;
import com.liferay.commerce.notification.web.internal.model.NotificationEntry;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.frontend.data.set.provider.FDSActionProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
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
	property = "fds.data.provider.key=" + CommerceNotificationFDSNames.NOTIFICATION_ENTRIES,
	service = FDSActionProvider.class
)
public class CommerceNotificationEntryFDSActionProvider
	implements FDSActionProvider {

	@Override
	public List<DropdownItem> getDropdownItems(
			long groupId, HttpServletRequest httpServletRequest, Object model)
		throws PortalException {

		PortletURL portletURL = PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, CPPortletKeys.COMMERCE_CHANNELS,
				PortletRequest.ACTION_PHASE)
		).setActionName(
			"/commerce_channels/edit_commerce_notification_queue_entry"
		).setCMD(
			"resend"
		).setRedirect(
			ParamUtil.getString(
				httpServletRequest, "currentUrl",
				_portal.getCurrentURL(httpServletRequest))
		).setParameter(
			"commerceNotificationQueueEntryId",
			() -> {
				NotificationEntry notificationEntry = (NotificationEntry)model;

				return notificationEntry.getNotificationEntryId();
			}
		).buildPortletURL();

		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setHref(portletURL.toString());
				dropdownItem.setLabel(
					_language.get(httpServletRequest, "resend"));
			}
		).add(
			dropdownItem -> {
				portletURL.setParameter(Constants.CMD, Constants.DELETE);

				dropdownItem.setHref(portletURL.toString());
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