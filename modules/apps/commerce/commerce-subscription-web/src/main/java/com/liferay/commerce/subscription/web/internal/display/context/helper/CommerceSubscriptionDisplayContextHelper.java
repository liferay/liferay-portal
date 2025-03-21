/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.subscription.web.internal.display.context.helper;

import com.liferay.commerce.configuration.CommerceSubscriptionConfiguration;
import com.liferay.commerce.constants.CommerceSubscriptionEntryConstants;
import com.liferay.commerce.model.CommerceSubscriptionEntry;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceSubscriptionDisplayContextHelper {

	public CommerceSubscriptionDisplayContextHelper(
		CommerceSubscriptionEntry commerceSubscriptionEntry,
		ConfigurationProvider configurationProvider,
		PortletRequest portletRequest, PortletResponse portletResponse) {

		_commerceSubscriptionEntry = commerceSubscriptionEntry;
		_configurationProvider = configurationProvider;
		_portletRequest = portletRequest;
		_portletResponse = portletResponse;
	}

	public DropdownItemList getCommerceSubscriptionEntryActionItemList()
		throws PortalException {

		if (_commerceSubscriptionEntry == null) {
			return null;
		}

		int subscriptionStatus =
			_commerceSubscriptionEntry.getSubscriptionStatus();

		if (_isSubscriptionOver(subscriptionStatus)) {
			return null;
		}

		DropdownItemList dropdownItems = new DropdownItemList();

		if (CommerceSubscriptionEntryConstants.SUBSCRIPTION_STATUS_SUSPENDED ==
				subscriptionStatus) {

			dropdownItems.add(_getDropdownItem("activate"));
		}
		else {
			CommerceSubscriptionConfiguration
				commerceSubscriptionConfiguration =
					_configurationProvider.getSystemConfiguration(
						CommerceSubscriptionConfiguration.class);

			if (commerceSubscriptionConfiguration.
					subscriptionSuspensionAllowed() &&
				(subscriptionStatus !=
					CommerceSubscriptionEntryConstants.
						SUBSCRIPTION_STATUS_SUSPENDED)) {

				dropdownItems.add(_getDropdownItem("suspend"));
			}

			if (commerceSubscriptionConfiguration.
					subscriptionCancellationAllowed() &&
				(subscriptionStatus !=
					CommerceSubscriptionEntryConstants.
						SUBSCRIPTION_STATUS_CANCELLED)) {

				dropdownItems.add(_getDropdownItem("cancel"));
			}
		}

		return dropdownItems;
	}

	private PortletURL _getActionURL(String cmd) {
		return PortletURLBuilder.createActionURL(
			PortalUtil.getLiferayPortletResponse(_portletResponse)
		).setActionName(
			"/commerce_subscription_content_web" +
				"/edit_commerce_subscription_content"
		).setCMD(
			cmd
		).setRedirect(
			PortalUtil.getCurrentURL(_portletRequest)
		).setParameter(
			"commerceSubscriptionEntryId",
			_commerceSubscriptionEntry.getCommerceSubscriptionEntryId()
		).buildActionURL();
	}

	private DropdownItem _getDropdownItem(String cmd) {
		return DropdownItemBuilder.setHref(
			_getActionURL(cmd)
		).setLabel(
			LanguageUtil.get(
				PortalUtil.getHttpServletRequest(_portletRequest), cmd)
		).build();
	}

	private boolean _isSubscriptionOver(int subscriptionStatus) {
		if ((subscriptionStatus ==
				CommerceSubscriptionEntryConstants.
					SUBSCRIPTION_STATUS_COMPLETED) ||
			(subscriptionStatus ==
				CommerceSubscriptionEntryConstants.
					SUBSCRIPTION_STATUS_CANCELLED)) {

			return true;
		}

		return false;
	}

	private final CommerceSubscriptionEntry _commerceSubscriptionEntry;
	private final ConfigurationProvider _configurationProvider;
	private final PortletRequest _portletRequest;
	private final PortletResponse _portletResponse;

}