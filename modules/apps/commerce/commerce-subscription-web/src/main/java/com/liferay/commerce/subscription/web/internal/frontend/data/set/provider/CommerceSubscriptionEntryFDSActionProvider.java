/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.subscription.web.internal.frontend.data.set.provider;

import com.liferay.commerce.constants.CommerceActionKeys;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.model.CommerceSubscriptionEntry;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.subscription.web.internal.constants.CommerceSubscriptionFDSNames;
import com.liferay.commerce.subscription.web.internal.model.SubscriptionEntry;
import com.liferay.frontend.data.set.provider.FDSActionProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fds.data.provider.key=" + CommerceSubscriptionFDSNames.SUBSCRIPTION_ENTRIES,
	service = FDSActionProvider.class
)
public class CommerceSubscriptionEntryFDSActionProvider
	implements FDSActionProvider {

	@Override
	public List<DropdownItem> getDropdownItems(
			long groupId, HttpServletRequest httpServletRequest, Object model)
		throws PortalException {

		SubscriptionEntry subscriptionEntry = (SubscriptionEntry)model;

		return DropdownItemListBuilder.add(
			() -> _portletResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(), null,
				CommerceActionKeys.MANAGE_COMMERCE_SUBSCRIPTIONS),
			dropdownItem -> {
				dropdownItem.setHref(
					_getSubscriptionEntryEditURL(
						subscriptionEntry.getSubscriptionId(),
						httpServletRequest));
				dropdownItem.setLabel(
					_language.get(httpServletRequest, "edit"));
			}
		).add(
			() -> _portletResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(), null,
				CommerceActionKeys.MANAGE_COMMERCE_SUBSCRIPTIONS),
			dropdownItem -> {
				dropdownItem.setHref(
					_getSubscriptionEntryDeleteURL(
						subscriptionEntry.getSubscriptionId(),
						httpServletRequest));
				dropdownItem.setLabel(
					_language.get(httpServletRequest, "delete"));
			}
		).build();
	}

	private PortletURL _getSubscriptionEntryDeleteURL(
		long commerceSubscriptionEntryId,
		HttpServletRequest httpServletRequest) {

		PortletURL portletURL = _portal.getControlPanelPortletURL(
			httpServletRequest, CPPortletKeys.COMMERCE_SUBSCRIPTION_ENTRY,
			PortletRequest.ACTION_PHASE);

		portletURL.setParameter("redirect", portletURL.toString());
		portletURL.setParameter(
			ActionRequest.ACTION_NAME,
			"/commerce_subscription_entry/edit_commerce_subscription_entry");
		portletURL.setParameter(Constants.CMD, Constants.DELETE);
		portletURL.setParameter(
			"commerceSubscriptionEntryId",
			String.valueOf(commerceSubscriptionEntryId));

		return portletURL;
	}

	private PortletURL _getSubscriptionEntryEditURL(
			long commerceSubscriptionEntryId,
			HttpServletRequest httpServletRequest)
		throws PortalException {

		return PortletURLBuilder.create(
			PortletProviderUtil.getPortletURL(
				httpServletRequest, CommerceSubscriptionEntry.class.getName(),
				PortletProvider.Action.MANAGE)
		).setMVCRenderCommandName(
			"/commerce_subscription_entry/edit_commerce_subscription_entry"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setParameter(
			"commerceSubscriptionEntryId", commerceSubscriptionEntryId
		).buildPortletURL();
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(resource.name=" + CommerceConstants.RESOURCE_NAME_COMMERCE_SUBSCRIPTION + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}