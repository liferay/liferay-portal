/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.channel.web.internal.frontend.data.set.provider;

import com.liferay.commerce.channel.web.internal.constants.CommerceChannelFDSNames;
import com.liferay.commerce.channel.web.internal.model.PaymentMethod;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelService;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.frontend.data.set.provider.FDSActionProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.PortletQName;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = "fds.data.provider.key=" + CommerceChannelFDSNames.PAYMENT_METHOD,
	service = FDSActionProvider.class
)
public class CommercePaymentMethodFDSActionProvider
	implements FDSActionProvider {

	@Override
	public List<DropdownItem> getDropdownItems(
			long groupId, HttpServletRequest httpServletRequest, Object model)
		throws PortalException {

		long commerceChannelId = ParamUtil.getLong(
			httpServletRequest, "commerceChannelId");
		PaymentMethod paymentMethod = (PaymentMethod)model;

		DropdownItemList dropdownItemList = DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.create(
						PortletProviderUtil.getPortletURL(
							httpServletRequest,
							CommercePaymentMethodGroupRel.class.getName(),
							PortletProvider.Action.EDIT)
					).setParameter(
						"commerceChannelId", commerceChannelId
					).setParameter(
						"commercePaymentIntegrationKey",
						paymentMethod.getPaymentIntegrationKey()
					).setParameter(
						"commercePaymentMethodEngineKey", paymentMethod.getKey()
					).setWindowState(
						LiferayWindowState.POP_UP
					).buildPortletURL());
				dropdownItem.setLabel(
					_language.get(httpServletRequest, "edit"));
				dropdownItem.setTarget("sidePanel");
			}
		).build();

		CommerceChannel commerceChannel =
			_commerceChannelService.getCommerceChannel(commerceChannelId);

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			_commercePaymentMethodGroupRelService.
				fetchCommercePaymentMethodGroupRel(
					commerceChannel.getGroupId(), paymentMethod.getKey());

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if ((commercePaymentMethodGroupRel != null) &&
			permissionChecker.hasPermission(
				commercePaymentMethodGroupRel.getGroupId(),
				CommercePaymentMethodGroupRel.class.getName(),
				commercePaymentMethodGroupRel.
					getCommercePaymentMethodGroupRelId(),
				ActionKeys.PERMISSIONS)) {

			dropdownItemList.add(
				dropdownItem -> {
					dropdownItem.setHref(
						_getPaymentMethodPermissionURL(
							commercePaymentMethodGroupRel,
							paymentMethod.getKey(), httpServletRequest));
					dropdownItem.setLabel(
						_language.get(httpServletRequest, "permissions"));
					dropdownItem.setTarget("modal-permissions");
				});
		}

		return dropdownItemList;
	}

	private PortletURL _getPaymentMethodPermissionURL(
			CommercePaymentMethodGroupRel commercePaymentMethodGroupRel,
			String paymentMethodKey, HttpServletRequest httpServletRequest)
		throws PortalException {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest,
				"com_liferay_portlet_configuration_web_portlet_" +
					"PortletConfigurationPortlet",
				ActionRequest.RENDER_PHASE)
		).setMVCPath(
			"/edit_permissions.jsp"
		).setParameter(
			PortletQName.PUBLIC_RENDER_PARAMETER_NAMESPACE + "backURL",
			ParamUtil.getString(
				httpServletRequest, "currentUrl",
				_portal.getCurrentURL(httpServletRequest))
		).setParameter(
			"modelResource", CommercePaymentMethodGroupRel.class.getName()
		).setParameter(
			"modelResourceDescription", paymentMethodKey
		).setParameter(
			"resourcePrimKey",
			commercePaymentMethodGroupRel.getCommercePaymentMethodGroupRelId()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildPortletURL();
	}

	@Reference
	private CommerceChannelService _commerceChannelService;

	@Reference
	private CommercePaymentMethodGroupRelService
		_commercePaymentMethodGroupRelService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}