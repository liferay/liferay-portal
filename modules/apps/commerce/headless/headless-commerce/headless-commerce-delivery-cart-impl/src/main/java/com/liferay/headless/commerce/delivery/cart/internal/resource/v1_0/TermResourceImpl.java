/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.cart.internal.resource.v1_0;

import com.liferay.commerce.context.CommerceContextFactory;
import com.liferay.commerce.exception.NoSuchOrderException;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceShippingEngine;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.model.CommerceShippingOption;
import com.liferay.commerce.order.CommerceOrderHttpHelper;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelLocalService;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.commerce.service.CommerceShippingMethodLocalService;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOption;
import com.liferay.commerce.shipping.engine.fixed.service.CommerceShippingFixedOptionLocalService;
import com.liferay.commerce.term.model.CommerceTermEntry;
import com.liferay.commerce.term.service.CommerceTermEntryLocalService;
import com.liferay.commerce.util.CommerceShippingEngineRegistry;
import com.liferay.headless.commerce.delivery.cart.dto.v1_0.Term;
import com.liferay.headless.commerce.delivery.cart.internal.util.v1_0.GuestCommerceOrderUtil;
import com.liferay.headless.commerce.delivery.cart.resource.v1_0.TermResource;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.pagination.Page;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Crescenzo Rega
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/term.properties",
	scope = ServiceScope.PROTOTYPE, service = TermResource.class
)
public class TermResourceImpl extends BaseTermResourceImpl {

	@Override
	public Page<Term> getCartByExternalReferenceCodeDeliveryTermsPage(
			String externalReferenceCode)
		throws Exception {

		CommerceOrder commerceOrder =
			_commerceOrderService.fetchCommerceOrderByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrder == null) {
			throw new NoSuchOrderException(
				"Unable to find order with external reference code " +
					externalReferenceCode);
		}

		GuestCommerceOrderUtil.checkGuestCommerceOrder(
			commerceOrder, _commerceOrderHttpHelper, contextHttpServletRequest);

		return getCartDeliveryTermsPage(commerceOrder.getCommerceOrderId());
	}

	@Override
	public Page<Term> getCartByExternalReferenceCodePaymentTermsPage(
			String externalReferenceCode)
		throws Exception {

		CommerceOrder commerceOrder =
			_commerceOrderService.fetchCommerceOrderByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrder == null) {
			throw new NoSuchOrderException(
				"Unable to find order with external reference code " +
					externalReferenceCode);
		}

		GuestCommerceOrderUtil.checkGuestCommerceOrder(
			commerceOrder, _commerceOrderHttpHelper, contextHttpServletRequest);

		return getCartPaymentTermsPage(commerceOrder.getCommerceOrderId());
	}

	@Override
	public Page<Term> getCartDeliveryTermsPage(Long cartId) throws Exception {
		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			cartId);

		GuestCommerceOrderUtil.checkGuestCommerceOrder(
			commerceOrder, _commerceOrderHttpHelper, contextHttpServletRequest);

		return Page.of(
			transform(
				_commerceTermEntryLocalService.getDeliveryCommerceTermEntries(
					commerceOrder.getCompanyId(),
					commerceOrder.getCommerceOrderTypeId(),
					_getCommerceShippingFixedOptionId(commerceOrder)),
				this::_toTerm));
	}

	@Override
	public Page<Term> getCartPaymentTermsPage(Long cartId) throws Exception {
		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			cartId);

		GuestCommerceOrderUtil.checkGuestCommerceOrder(
			commerceOrder, _commerceOrderHttpHelper, contextHttpServletRequest);

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			_commercePaymentMethodGroupRelLocalService.
				fetchCommercePaymentMethodGroupRel(
					commerceOrder.getGroupId(),
					commerceOrder.getCommercePaymentMethodKey());

		if (commercePaymentMethodGroupRel == null) {
			return super.getCartPaymentTermsPage(cartId);
		}

		return Page.of(
			transform(
				_commerceTermEntryLocalService.getPaymentCommerceTermEntries(
					commerceOrder.getCompanyId(),
					commerceOrder.getCommerceOrderTypeId(),
					commercePaymentMethodGroupRel.
						getCommercePaymentMethodGroupRelId()),
				this::_toTerm));
	}

	private long _getCommerceShippingFixedOptionId(CommerceOrder commerceOrder)
		throws Exception {

		CommerceShippingMethod commerceShippingMethod =
			_commerceShippingMethodLocalService.fetchCommerceShippingMethod(
				commerceOrder.getCommerceShippingMethodId());

		if (commerceShippingMethod == null) {
			return 0;
		}

		CommerceShippingEngine commerceShippingEngine =
			_commerceShippingEngineRegistry.getCommerceShippingEngine(
				commerceShippingMethod.getEngineKey());

		if (commerceShippingEngine == null) {
			return 0;
		}

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.fetchCommerceChannelByGroupClassPK(
				commerceOrder.getGroupId());

		if (commerceChannel == null) {
			return 0;
		}

		for (CommerceShippingOption commerceShippingOption :
				commerceShippingEngine.getCommerceShippingOptions(
					_commerceContextFactory.create(
						commerceOrder.getCommerceAccountId(),
						commerceChannel.getGroupId(), null,
						commerceOrder.getCommerceOrderId(),
						contextCompany.getCompanyId()),
					commerceOrder,
					contextAcceptLanguage.getPreferredLocale())) {

			if (!StringUtil.equals(
					commerceOrder.getShippingOptionName(),
					commerceShippingOption.getKey())) {

				continue;
			}

			CommerceShippingFixedOption commerceShippingFixedOption =
				_commerceShippingFixedOptionLocalService.
					fetchCommerceShippingFixedOption(
						commerceOrder.getCompanyId(),
						commerceShippingOption.getKey());

			if (commerceShippingFixedOption != null) {
				return commerceShippingFixedOption.
					getCommerceShippingFixedOptionId();
			}
		}

		return 0;
	}

	private Term _toTerm(CommerceTermEntry commerceTermEntry) {
		return new Term() {
			{
				setDescription(
					() -> commerceTermEntry.getDescription(
						_language.getLanguageId(
							contextAcceptLanguage.getPreferredLocale())));
				setExternalReferenceCode(
					commerceTermEntry::getExternalReferenceCode);
				setId(commerceTermEntry::getCommerceTermEntryId);
				setLabel(
					() -> commerceTermEntry.getLabel(
						_language.getLanguageId(
							contextAcceptLanguage.getPreferredLocale())));
				setName(commerceTermEntry::getName);
			}
		};
	}

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceContextFactory _commerceContextFactory;

	@Reference
	private CommerceOrderHttpHelper _commerceOrderHttpHelper;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private CommercePaymentMethodGroupRelLocalService
		_commercePaymentMethodGroupRelLocalService;

	@Reference
	private CommerceShippingEngineRegistry _commerceShippingEngineRegistry;

	@Reference
	private CommerceShippingFixedOptionLocalService
		_commerceShippingFixedOptionLocalService;

	@Reference
	private CommerceShippingMethodLocalService
		_commerceShippingMethodLocalService;

	@Reference
	private CommerceTermEntryLocalService _commerceTermEntryLocalService;

	@Reference
	private Language _language;

}