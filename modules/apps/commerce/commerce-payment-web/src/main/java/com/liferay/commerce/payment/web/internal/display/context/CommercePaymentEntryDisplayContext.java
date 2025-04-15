/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.web.internal.display.context;

import com.liferay.commerce.constants.CommercePaymentEntryConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyService;
import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.frontend.model.HeaderActionModel;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.payment.entry.CommercePaymentEntryRefundType;
import com.liferay.commerce.payment.entry.CommercePaymentEntryRefundTypeRegistry;
import com.liferay.commerce.payment.model.CommercePaymentEntry;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
import com.liferay.commerce.payment.service.CommercePaymentEntryService;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelService;
import com.liferay.commerce.payment.web.internal.display.context.helper.CommercePaymentRequestHelper;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.commerce.service.CommerceOrderLocalServiceUtil;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

import java.text.DateFormat;
import java.text.Format;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alessio Antonio Rendina
 * @author Crescenzo Rega
 */
public class CommercePaymentEntryDisplayContext {

	public CommercePaymentEntryDisplayContext(
			ClassNameLocalService classNameLocalService,
			CommerceChannelService commerceChannelService,
			CommerceCurrencyService commerceCurrencyService,
			ModelResourcePermission<CommercePaymentEntry>
				commercePaymentEntryModelResourcePermission,
			CommercePaymentEntryRefundTypeRegistry
				commercePaymentEntryRefundTypeRegistry,
			CommercePaymentEntryService commercePaymentEntryService,
			CommercePaymentMethodGroupRelService
				commercePaymentMethodGroupRelService,
			CommercePriceFormatter commercePriceFormatter,
			HttpServletRequest httpServletRequest, Language language,
			Portal portal)
		throws PortalException {

		_classNameLocalService = classNameLocalService;
		_commerceChannelService = commerceChannelService;
		_commerceCurrencyService = commerceCurrencyService;
		_commercePaymentEntryModelResourcePermission =
			commercePaymentEntryModelResourcePermission;
		_commercePaymentEntryRefundTypeRegistry =
			commercePaymentEntryRefundTypeRegistry;
		_commercePaymentEntryService = commercePaymentEntryService;
		_commercePaymentMethodGroupRelService =
			commercePaymentMethodGroupRelService;
		_commercePriceFormatter = commercePriceFormatter;
		_language = language;
		_portal = portal;

		long commercePaymentEntryId = ParamUtil.getLong(
			httpServletRequest, "commercePaymentEntryId");

		_commercePaymentEntry =
			_commercePaymentEntryService.fetchCommercePaymentEntry(
				commercePaymentEntryId);

		_commercePaymentRequestHelper = new CommercePaymentRequestHelper(
			httpServletRequest);

		long classPK = ParamUtil.getLong(httpServletRequest, "classPK");

		if (_commercePaymentEntry != null) {
			classPK = _commercePaymentEntry.getClassPK();
		}

		_relatedCommercePaymentEntry =
			_commercePaymentEntryService.fetchCommercePaymentEntry(classPK);
	}

	public BigDecimal getAmount() {
		BigDecimal amount = BigDecimal.ZERO;

		if (_commercePaymentEntry != null) {
			amount = _commercePaymentEntry.getAmount();

			return amount.stripTrailingZeros();
		}

		if (_relatedCommercePaymentEntry != null) {
			amount = _relatedCommercePaymentEntry.getAmount();
		}

		return amount.stripTrailingZeros();
	}

	public String getAPIURL() {
		String encodedFilter = URLCodec.encodeURL(
			StringBundler.concat(
				"id ne ", getCommercePaymentEntryId(), " and relatedItemId eq ",
				getClassPK(), " and type/any(x:x eq ",
				CommercePaymentEntryConstants.TYPE_REFUND,
				StringPool.CLOSE_PARENTHESIS),
			true);

		return "/o/headless-commerce-admin-payment/v1.0/payments?filter=" +
			encodedFilter;
	}

	public String getClassName() {
		if (_relatedCommercePaymentEntry == null) {
			return StringPool.BLANK;
		}

		return _relatedCommercePaymentEntry.getModelClassName();
	}

	public long getClassPK() {
		if (_relatedCommercePaymentEntry == null) {
			return 0;
		}

		return _relatedCommercePaymentEntry.getCommercePaymentEntryId();
	}

	public long getCommerceChannelId() {
		if (_relatedCommercePaymentEntry == null) {
			return 0;
		}

		return _relatedCommercePaymentEntry.getCommerceChannelId();
	}

	public CommercePaymentEntry getCommercePaymentEntry() {
		return _commercePaymentEntry;
	}

	public long getCommercePaymentEntryId() {
		if (_commercePaymentEntry == null) {
			return 0;
		}

		return _commercePaymentEntry.getCommercePaymentEntryId();
	}

	public List<CommercePaymentEntryRefundType>
		getCommercePaymentEntryRefundTypes() {

		return _commercePaymentEntryRefundTypeRegistry.
			getCommercePaymentEntryRefundTypes(
				_commercePaymentRequestHelper.getCompanyId());
	}

	public String getCurrencyCode() {
		if (_relatedCommercePaymentEntry == null) {
			return StringPool.BLANK;
		}

		return _relatedCommercePaymentEntry.getCurrencyCode();
	}

	public String getDeliveryFormatted() throws PortalException {
		CommerceOrder commerceOrder =
			CommerceOrderLocalServiceUtil.getCommerceOrder(
				_relatedCommercePaymentEntry.getClassPK());

		return _commercePriceFormatter.format(
			commerceOrder.getCommerceCurrency(),
			commerceOrder.getShippingAmount(),
			_commercePaymentRequestHelper.getLocale());
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems()
		throws PortalException {

		HttpServletRequest httpServletRequest =
			_commercePaymentRequestHelper.getRequest();

		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					PortletProviderUtil.getPortletURL(
						httpServletRequest,
						CommercePaymentEntry.class.getName(),
						PortletProvider.Action.MANAGE)
				).setMVCRenderCommandName(
					"/commerce_payment/edit_commerce_payment_entry"
				).setParameter(
					"commercePaymentEntryId", "{id}"
				).buildString(),
				null, "view", LanguageUtil.get(httpServletRequest, "view"),
				"get", "get", null),
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					PortletProviderUtil.getPortletURL(
						httpServletRequest,
						CommercePaymentEntry.class.getName(),
						PortletProvider.Action.MANAGE)
				).setMVCRenderCommandName(
					"/commerce_payment/edit_commerce_payment_entry"
				).setParameter(
					"className", CommercePaymentEntry.class.getName()
				).setParameter(
					"classPK", "{id}"
				).buildString(),
				null, "makeRefund",
				LanguageUtil.get(httpServletRequest, "make-a-refund"), "get",
				"create", null));
	}

	public String getFormattedValue(BigDecimal value) throws PortalException {
		return _commercePriceFormatter.format(
			value, _commercePaymentRequestHelper.getLocale());
	}

	public List<HeaderActionModel> getHeaderActionModels() throws Exception {
		List<HeaderActionModel> headerActionModels = new ArrayList<>();

		if ((_commercePaymentEntry != null) &&
			(_commercePaymentEntry.getType() ==
				CommercePaymentEntryConstants.TYPE_REFUND) &&
			(_commercePaymentEntry.getPaymentStatus() !=
				CommercePaymentEntryConstants.STATUS_PENDING)) {

			return headerActionModels;
		}

		LiferayPortletResponse liferayPortletResponse =
			_commercePaymentRequestHelper.getLiferayPortletResponse();

		String additionalClasses = StringPool.BLANK;

		if (_commercePaymentEntry == null) {
			additionalClasses = "btn-primary";
		}

		HeaderActionModel saveHeaderActionModel = new HeaderActionModel(
			additionalClasses, liferayPortletResponse.getNamespace() + "fm",
			PortletURLBuilder.createActionURL(
				liferayPortletResponse
			).setActionName(
				"/commerce_payment/edit_commerce_payment_entry"
			).setCMD(
				Constants.UPDATE
			).buildString(),
			null, "save");

		headerActionModels.add(saveHeaderActionModel);

		if (_commercePaymentEntry == null) {
			return headerActionModels;
		}

		HeaderActionModel submitHeaderActionModel = new HeaderActionModel(
			"btn-primary", liferayPortletResponse.getNamespace() + "fm",
			PortletURLBuilder.createActionURL(
				liferayPortletResponse
			).setActionName(
				"/commerce_payment/edit_commerce_payment_entry"
			).setCMD(
				Constants.PUBLISH
			).buildString(),
			liferayPortletResponse.getNamespace() + "submitButton", "submit");

		headerActionModels.add(submitHeaderActionModel);

		return headerActionModels;
	}

	public String getLanguageId() {
		if (_relatedCommercePaymentEntry == null) {
			return StringPool.BLANK;
		}

		return _relatedCommercePaymentEntry.getLanguageId();
	}

	public String getPayload() {
		if (_relatedCommercePaymentEntry == null) {
			return StringPool.BLANK;
		}

		return _relatedCommercePaymentEntry.getPayload();
	}

	public String getPaymentDate() {
		if (_relatedCommercePaymentEntry == null) {
			return StringPool.BLANK;
		}

		ThemeDisplay themeDisplay =
			_commercePaymentRequestHelper.getThemeDisplay();

		Format dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(
			DateFormat.MEDIUM, DateFormat.MEDIUM, themeDisplay.getLocale(),
			themeDisplay.getTimeZone());

		return dateTimeFormat.format(
			_relatedCommercePaymentEntry.getCreateDate());
	}

	public String getPaymentIntegrationKey() {
		if (_relatedCommercePaymentEntry == null) {
			return StringPool.BLANK;
		}

		return _relatedCommercePaymentEntry.getPaymentIntegrationKey();
	}

	public int getPaymentIntegrationType() {
		if (_relatedCommercePaymentEntry == null) {
			return -1;
		}

		return _relatedCommercePaymentEntry.getPaymentIntegrationType();
	}

	public String getPaymentMethod() throws PortalException {
		if (_relatedCommercePaymentEntry == null) {
			return StringPool.BLANK;
		}

		CommerceChannel commerceChannel =
			_commerceChannelService.fetchCommerceChannel(
				_relatedCommercePaymentEntry.getCommerceChannelId());

		if (commerceChannel == null) {
			return StringPool.BLANK;
		}

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			_commercePaymentMethodGroupRelService.
				fetchCommercePaymentMethodGroupRel(
					commerceChannel.getGroupId(),
					_relatedCommercePaymentEntry.getPaymentIntegrationKey());

		if (commercePaymentMethodGroupRel == null) {
			return StringPool.BLANK;
		}

		return commercePaymentMethodGroupRel.getName(
			_commercePaymentRequestHelper.getLocale());
	}

	public String getRefundAlreadyCompleted() throws PortalException {
		return _commercePriceFormatter.format(
			_getCommerceCurrency(
				_relatedCommercePaymentEntry.getCurrencyCode()),
			_commercePaymentEntryService.getRefundedAmount(
				_relatedCommercePaymentEntry.getCompanyId(),
				_classNameLocalService.getClassNameId(CommerceOrder.class),
				_relatedCommercePaymentEntry.getClassPK()),
			_commercePaymentRequestHelper.getLocale());
	}

	public String getRelatedToClassName() {
		if (_relatedCommercePaymentEntry == null) {
			return StringPool.BLANK;
		}

		return _language.get(
			_commercePaymentRequestHelper.getLocale(),
			"model.resource." + _relatedCommercePaymentEntry.getClassName());
	}

	public String getRelatedToClassPK() {
		if (_relatedCommercePaymentEntry == null) {
			return StringPool.BLANK;
		}

		return String.valueOf(_relatedCommercePaymentEntry.getClassPK());
	}

	public String getRelatedToURL() throws PortalException {
		if (_relatedCommercePaymentEntry == null) {
			return StringPool.BLANK;
		}

		return PortletURLBuilder.create(
			PortletProviderUtil.getPortletURL(
				_commercePaymentRequestHelper.getRequest(),
				CommerceOrder.class.getName(), PortletProvider.Action.MANAGE)
		).setMVCRenderCommandName(
			"/commerce_order/edit_commerce_order"
		).setBackURL(
			_portal.getCurrentURL(_commercePaymentRequestHelper.getRequest())
		).setParameter(
			"commerceOrderId", _relatedCommercePaymentEntry.getClassPK()
		).buildString();
	}

	public String getTotalAmountFormatted() throws PortalException {
		if (_relatedCommercePaymentEntry == null) {
			return BigDecimal.ZERO.toString();
		}

		return _commercePriceFormatter.format(
			_getCommerceCurrency(
				_relatedCommercePaymentEntry.getCurrencyCode()),
			_relatedCommercePaymentEntry.getAmount(),
			_commercePaymentRequestHelper.getLocale());
	}

	public String getTransactionCode() {
		if (_relatedCommercePaymentEntry == null) {
			return StringPool.BLANK;
		}

		return _relatedCommercePaymentEntry.getTransactionCode();
	}

	public boolean hasCommercePaymentEntryModelPermission(String actionId)
		throws PortalException {

		ThemeDisplay themeDisplay =
			_commercePaymentRequestHelper.getThemeDisplay();

		return _commercePaymentEntryModelResourcePermission.contains(
			themeDisplay.getPermissionChecker(), _commercePaymentEntry,
			actionId);
	}

	public boolean isDisabled() {
		if (_commercePaymentEntry == null) {
			return false;
		}

		if (_commercePaymentEntry.getPaymentStatus() ==
				CommercePaymentEntryConstants.STATUS_REFUNDED) {

			return true;
		}

		return false;
	}

	public boolean isRelatedToOrder() {
		if (_relatedCommercePaymentEntry == null) {
			return false;
		}

		return StringUtil.equals(
			_relatedCommercePaymentEntry.getClassName(),
			CommerceOrder.class.getName());
	}

	private CommerceCurrency _getCommerceCurrency(String currencyCode)
		throws PortalException {

		return _commerceCurrencyService.getCommerceCurrency(
			_commercePaymentRequestHelper.getCompanyId(), currencyCode);
	}

	private final ClassNameLocalService _classNameLocalService;
	private final CommerceChannelService _commerceChannelService;
	private final CommerceCurrencyService _commerceCurrencyService;
	private final CommercePaymentEntry _commercePaymentEntry;
	private final ModelResourcePermission<CommercePaymentEntry>
		_commercePaymentEntryModelResourcePermission;
	private final CommercePaymentEntryRefundTypeRegistry
		_commercePaymentEntryRefundTypeRegistry;
	private final CommercePaymentEntryService _commercePaymentEntryService;
	private final CommercePaymentMethodGroupRelService
		_commercePaymentMethodGroupRelService;
	private final CommercePaymentRequestHelper _commercePaymentRequestHelper;
	private final CommercePriceFormatter _commercePriceFormatter;
	private final Language _language;
	private final Portal _portal;
	private final CommercePaymentEntry _relatedCommercePaymentEntry;

}