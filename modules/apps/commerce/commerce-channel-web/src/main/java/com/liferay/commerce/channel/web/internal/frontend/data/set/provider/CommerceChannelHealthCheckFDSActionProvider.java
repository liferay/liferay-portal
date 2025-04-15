/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.channel.web.internal.frontend.data.set.provider;

import com.liferay.commerce.channel.web.internal.constants.CommerceChannelFDSNames;
import com.liferay.commerce.channel.web.internal.model.HealthCheck;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.frontend.data.set.provider.FDSActionProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Victor Silvestre
 */
@Component(
	property = "fds.data.provider.key=" + CommerceChannelFDSNames.CHANNEL_HEALTH_CHECK,
	service = FDSActionProvider.class
)
public class CommerceChannelHealthCheckFDSActionProvider
	implements FDSActionProvider {

	@Override
	public List<DropdownItem> getDropdownItems(
			long groupId, HttpServletRequest httpServletRequest, Object model)
		throws PortalException {

		return DropdownItemListBuilder.add(
			dropdownItem -> {
				PortletURL portletURL = PortletURLBuilder.create(
					_portal.getControlPanelPortletURL(
						httpServletRequest, CPPortletKeys.COMMERCE_CHANNELS,
						PortletRequest.ACTION_PHASE)
				).setActionName(
					"/commerce_channels/edit_commerce_channel_health_status"
				).setRedirect(
					ParamUtil.getString(
						httpServletRequest, "currentUrl",
						_portal.getCurrentURL(httpServletRequest))
				).setParameter(
					"commerceChannelHealthStatusKey",
					() -> {
						HealthCheck healthCheck = (HealthCheck)model;

						return healthCheck.getKey();
					}
				).buildPortletURL();

				long commerceChannelId = ParamUtil.getLong(
					httpServletRequest, "commerceChannelId");

				portletURL.setParameter(
					"commerceChannelId", String.valueOf(commerceChannelId));

				ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
					"content.Language", _portal.getLocale(httpServletRequest),
					getClass());

				dropdownItem.setHref(portletURL.toString());
				dropdownItem.setLabel(
					_language.get(
						httpServletRequest, resourceBundle, "fix-issue"));
			}
		).build();
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}