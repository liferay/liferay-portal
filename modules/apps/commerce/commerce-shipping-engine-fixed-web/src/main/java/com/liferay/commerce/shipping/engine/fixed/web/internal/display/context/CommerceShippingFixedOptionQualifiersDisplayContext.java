/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipping.engine.fixed.web.internal.display.context;

import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceShippingMethodService;
import com.liferay.commerce.shipping.engine.fixed.constants.CommerceShippingEngineFixedWebKeys;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOption;
import com.liferay.commerce.shipping.engine.fixed.service.CommerceShippingFixedOptionQualifierService;
import com.liferay.commerce.shipping.engine.fixed.service.CommerceShippingFixedOptionService;
import com.liferay.commerce.shipping.engine.fixed.web.internal.display.context.helper.CommerceShippingFixedOptionRequestHelper;
import com.liferay.commerce.term.model.CommerceTermEntry;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceShippingFixedOptionQualifiersDisplayContext
	extends BaseCommerceShippingFixedOptionDisplayContext {

	public CommerceShippingFixedOptionQualifiersDisplayContext(
		CommerceChannelLocalService commerceChannelLocalService,
		ModelResourcePermission<CommerceChannel>
			commerceChannelModelResourcePermission,
		CommerceCurrencyLocalService commerceCurrencyLocalService,
		CommerceShippingFixedOptionQualifierService
			commerceShippingFixedOptionQualifierService,
		CommerceShippingFixedOptionService commerceShippingFixedOptionService,
		CommerceShippingMethodService commerceShippingMethodService,
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		super(
			commerceChannelLocalService, commerceCurrencyLocalService,
			commerceShippingMethodService, renderRequest, renderResponse);

		_commerceChannelModelResourcePermission =
			commerceChannelModelResourcePermission;
		_commerceShippingFixedOptionQualifierService =
			commerceShippingFixedOptionQualifierService;
		_commerceShippingFixedOptionService =
			commerceShippingFixedOptionService;

		_commerceShippingFixedOptionRequestHelper =
			new CommerceShippingFixedOptionRequestHelper(httpServletRequest);
	}

	public String getActiveOrderTypeEligibility() throws PortalException {
		long commerceOrderTypeCommerceShippingFixedOptionsCount =
			_commerceShippingFixedOptionQualifierService.
				getCommerceOrderTypeCommerceShippingFixedOptionQualifiersCount(
					getCommerceShippingFixedOptionId(), null);

		if (commerceOrderTypeCommerceShippingFixedOptionsCount > 0) {
			return "orderTypes";
		}

		return "all";
	}

	public String getActiveTermEntryEligibility() throws PortalException {
		long commerceTermEntryCommerceShippingFixedOptionsCount =
			_commerceShippingFixedOptionQualifierService.
				getCommerceTermEntryCommerceShippingFixedOptionQualifiersCount(
					getCommerceShippingFixedOptionId(), null);

		if (commerceTermEntryCommerceShippingFixedOptionsCount > 0) {
			return "termEntries";
		}

		return "none";
	}

	public String getCommerceOrderTypeCommerceShippingFixedOptionsAPIURL()
		throws PortalException {

		return StringBundler.concat(
			"/o/headless-commerce-admin-channel/v1.0/shipping-fixed-options",
			"/", getCommerceShippingFixedOptionId(),
			"/shipping-fixed-option-order-types?nestedFields=orderType");
	}

	public List<FDSActionDropdownItem>
			getCommerceOrderTypeFDSActionDropdownItems()
		throws PortalException {

		return _getFDSActionTemplates(
			PortletURLBuilder.create(
				PortletProviderUtil.getPortletURL(
					_commerceShippingFixedOptionRequestHelper.getRequest(),
					CommerceOrderType.class.getName(),
					PortletProvider.Action.MANAGE)
			).setMVCRenderCommandName(
				"/commerce_order_type/edit_commerce_order_type"
			).setRedirect(
				_commerceShippingFixedOptionRequestHelper.getCurrentURL()
			).setParameter(
				"commerceOrderTypeId", "{orderType.id}"
			).buildString(),
			false);
	}

	public CommerceShippingFixedOption getCommerceShippingFixedOption()
		throws PortalException {

		CommerceShippingFixedOption commerceShippingFixedOption =
			(CommerceShippingFixedOption)renderRequest.getAttribute(
				CommerceShippingEngineFixedWebKeys.
					COMMERCE_SHIPPING_FIXED_OPTION);

		if (commerceShippingFixedOption != null) {
			return commerceShippingFixedOption;
		}

		long commerceShippingFixedOptionId = ParamUtil.getLong(
			renderRequest, "commerceShippingFixedOptionId");

		commerceShippingFixedOption =
			_commerceShippingFixedOptionService.
				fetchCommerceShippingFixedOption(commerceShippingFixedOptionId);

		renderRequest.setAttribute(
			CommerceShippingEngineFixedWebKeys.COMMERCE_SHIPPING_FIXED_OPTION,
			commerceShippingFixedOption);

		return commerceShippingFixedOption;
	}

	public long getCommerceShippingFixedOptionId() throws PortalException {
		CommerceShippingFixedOption commerceShippingFixedOption =
			getCommerceShippingFixedOption();

		if (commerceShippingFixedOption == null) {
			return 0;
		}

		return commerceShippingFixedOption.getCommerceShippingFixedOptionId();
	}

	public String getCommerceTermEntriesCommerceShippingFixedOptionsAPIURL()
		throws PortalException {

		return StringBundler.concat(
			"/o/headless-commerce-admin-channel/v1.0/shipping-fixed-options",
			"/", getCommerceShippingFixedOptionId(),
			"/shipping-fixed-option-terms?nestedFields=term");
	}

	public List<FDSActionDropdownItem>
			getCommerceTermEntryFDSActionDropdownItems()
		throws PortalException {

		return _getFDSActionTemplates(
			PortletURLBuilder.create(
				PortletProviderUtil.getPortletURL(
					_commerceShippingFixedOptionRequestHelper.getRequest(),
					CommerceTermEntry.class.getName(),
					PortletProvider.Action.MANAGE)
			).setMVCRenderCommandName(
				"/commerce_term_entry/edit_commerce_term_entry"
			).setRedirect(
				_commerceShippingFixedOptionRequestHelper.getCurrentURL()
			).setParameter(
				"commerceTermEntryId", "{term.id}"
			).buildString(),
			false);
	}

	public boolean hasPermission(String actionId) throws PortalException {
		CommerceShippingFixedOption commerceShippingFixedOption =
			getCommerceShippingFixedOption();

		if (commerceShippingFixedOption == null) {
			return false;
		}

		CommerceChannel commerceChannel =
			commerceChannelLocalService.getCommerceChannelByGroupId(
				commerceShippingFixedOption.getGroupId());

		return _commerceChannelModelResourcePermission.contains(
			_commerceShippingFixedOptionRequestHelper.getPermissionChecker(),
			commerceChannel, actionId);
	}

	private List<FDSActionDropdownItem> _getFDSActionTemplates(
		String portletURL, boolean sidePanel) {

		List<FDSActionDropdownItem> fdsActionDropdownItems = new ArrayList<>();

		FDSActionDropdownItem fdsActionDropdownItem = new FDSActionDropdownItem(
			portletURL, "pencil", "edit",
			LanguageUtil.get(
				_commerceShippingFixedOptionRequestHelper.getRequest(), "edit"),
			"get", null, null);

		if (sidePanel) {
			fdsActionDropdownItem.setTarget("sidePanel");
		}

		fdsActionDropdownItems.add(fdsActionDropdownItem);

		fdsActionDropdownItems.add(
			new FDSActionDropdownItem(
				null, "trash", "remove",
				LanguageUtil.get(
					_commerceShippingFixedOptionRequestHelper.getRequest(),
					"remove"),
				"delete", "delete", "headless"));

		return fdsActionDropdownItems;
	}

	private final ModelResourcePermission<CommerceChannel>
		_commerceChannelModelResourcePermission;
	private final CommerceShippingFixedOptionQualifierService
		_commerceShippingFixedOptionQualifierService;
	private final CommerceShippingFixedOptionRequestHelper
		_commerceShippingFixedOptionRequestHelper;
	private final CommerceShippingFixedOptionService
		_commerceShippingFixedOptionService;

}