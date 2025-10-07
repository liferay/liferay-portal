/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.fragment.renderer;

import com.liferay.commerce.configuration.CommerceOrderCheckoutConfiguration;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.account.configuration.manager.AccountEntryAddressSubtypeConfigurationManagerUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Crescenzo Rega
 */
@Component(service = FragmentRenderer.class)
public class MultishippingFragmentRenderer implements FragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "commerce-order";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "multishipping");
	}

	@Override
	public boolean isSelectable(HttpServletRequest httpServletRequest) {
		return FeatureFlagManagerUtil.isEnabled("LPD-20379");
	}

	@Override
	public void render(
			FragmentRendererContext fragmentRendererContext,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		CommerceOrder commerceOrder = null;

		InfoItemReference infoItemReference =
			(InfoItemReference)httpServletRequest.getAttribute(
				InfoDisplayWebKeys.INFO_ITEM_REFERENCE);

		if (infoItemReference != null) {
			try {
				ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
					(ClassPKInfoItemIdentifier)
						infoItemReference.getInfoItemIdentifier();

				commerceOrder = _commerceOrderService.getCommerceOrder(
					classPKInfoItemIdentifier.getClassPK());
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}

				return;
			}
		}

		if (commerceOrder == null) {
			Object infoItem = httpServletRequest.getAttribute(
				InfoDisplayWebKeys.INFO_ITEM);

			if ((infoItem == null) || !(infoItem instanceof CommerceOrder)) {
				if (_isEditMode(httpServletRequest)) {
					_printPortletMessageInfo(
						httpServletRequest, httpServletResponse,
						"the-multishipping-component-will-be-shown-here");
				}

				return;
			}

			commerceOrder = (CommerceOrder)infoItem;
		}

		if (!_isMultishippingEnabled(commerceOrder, httpServletRequest)) {
			return;
		}

		try {
			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(
					"/fragment/renderer/multishipping/page.jsp");

			httpServletRequest.setAttribute(
				StringBundler.concat(
					"liferay-commerce:multishipping:",
					"billingAddressSubtypeListTypeDefinition",
					"ExternalReferenceCode"),
				AccountEntryAddressSubtypeConfigurationManagerUtil.
					getAddressSubtypeListTypeDefinitionExternalReferenceCode(
						commerceOrder.getCompanyId(), "billing"));
			httpServletRequest.setAttribute(
				StringBundler.concat(
					"liferay-commerce:multishipping:",
					"billingAndShippingAddressSubtypeListTypeDefinition",
					"ExternalReferenceCode"),
				AccountEntryAddressSubtypeConfigurationManagerUtil.
					getAddressSubtypeListTypeDefinitionExternalReferenceCode(
						commerceOrder.getCompanyId(), "billing-and-shipping"));
			httpServletRequest.setAttribute(
				"liferay-commerce:multishipping:commerceAccountId",
				commerceOrder.getCommerceAccountId());
			httpServletRequest.setAttribute(
				"liferay-commerce:multishipping:commerceOrderId",
				commerceOrder.getCommerceOrderId());
			httpServletRequest.setAttribute(
				"liferay-commerce:multishipping:readOnly",
				!commerceOrder.isOpen());
			httpServletRequest.setAttribute(
				StringBundler.concat(
					"liferay-commerce:multishipping:",
					"shippingAddressSubtypeListTypeDefinition",
					"ExternalReferenceCode"),
				AccountEntryAddressSubtypeConfigurationManagerUtil.
					getAddressSubtypeListTypeDefinitionExternalReferenceCode(
						commerceOrder.getCompanyId(), "shipping"));

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private boolean _isEditMode(HttpServletRequest httpServletRequest) {
		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		String layoutMode = ParamUtil.getString(
			originalHttpServletRequest, "p_l_mode", Constants.VIEW);

		return layoutMode.equals(Constants.EDIT);
	}

	private boolean _isMultishippingEnabled(
		CommerceOrder commerceOrder, HttpServletRequest httpServletRequest) {

		try {
			if ((commerceOrder == null) ||
				!_commerceOrderModelResourcePermission.contains(
					PermissionThreadLocal.getPermissionChecker(), commerceOrder,
					CommerceOrderActionKeys.
						MANAGE_COMMERCE_ORDER_MULTISHIPPING)) {

				return false;
			}

			CommerceOrderCheckoutConfiguration
				commerceOrderCheckoutConfiguration =
					_configurationProvider.getConfiguration(
						CommerceOrderCheckoutConfiguration.class,
						new GroupServiceSettingsLocator(
							_commerceChannelLocalService.
								getCommerceChannelGroupIdBySiteGroupId(
									_portal.getScopeGroupId(
										httpServletRequest)),
							CommerceConstants.SERVICE_NAME_COMMERCE_ORDER));

			return commerceOrderCheckoutConfiguration.multishippingEnabled();
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return false;
	}

	private void _printPortletMessageInfo(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String message)
		throws IOException {

		PrintWriter printWriter = httpServletResponse.getWriter();

		printWriter.write(
			StringBundler.concat(
				"<div class=\"portlet-msg-info\">",
				_language.get(httpServletRequest, message), "</div>"));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MultishippingFragmentRenderer.class);

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceOrder)"
	)
	private ModelResourcePermission<CommerceOrder>
		_commerceOrderModelResourcePermission;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.order.content.web)"
	)
	private ServletContext _servletContext;

}