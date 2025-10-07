/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.fragment.renderer;

import com.liferay.account.constants.AccountListTypeConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.model.CommerceShippingEngine;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.payment.integration.CommercePaymentIntegration;
import com.liferay.commerce.payment.integration.CommercePaymentIntegrationRegistry;
import com.liferay.commerce.payment.method.CommercePaymentMethod;
import com.liferay.commerce.payment.method.CommercePaymentMethodRegistry;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.commerce.service.CommerceOrderTypeService;
import com.liferay.commerce.util.CommerceOrderInfoItemUtil;
import com.liferay.commerce.util.CommerceShippingEngineRegistry;
import com.liferay.document.library.util.DLURLHelperUtil;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.account.configuration.manager.AccountEntryAddressSubtypeConfigurationManagerUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.text.DateFormat;

import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Alessio Antonio Rendina
 * @author Gianmarco Brunialti Masera
 */
@Component(service = FragmentRenderer.class)
public class InfoBoxFragmentRenderer implements FragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "commerce-order";
	}

	@Override
	public JSONObject getConfigurationJSONObject(
		FragmentRendererContext fragmentRendererContext) {

		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject(
				StringUtil.read(
					getClass(), "info_box/dependencies/configuration.json"));

			return _fragmentEntryConfigurationParser.translateConfiguration(
				jsonObject,
				ResourceBundleUtil.getBundle("content.Language", getClass()));
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}

			return null;
		}
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "info-box");
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

		FragmentEntryLink fragmentEntryLink =
			fragmentRendererContext.getFragmentEntryLink();

		String field = _getConfigurationValue(
			fragmentRendererContext, fragmentEntryLink, "field");

		boolean readOnly = GetterUtil.getBoolean(
			_fragmentEntryConfigurationParser.getFieldValue(
				getConfigurationJSONObject(fragmentRendererContext),
				fragmentEntryLink.getEditableValuesJSONObject(),
				fragmentRendererContext.getLocale(), "readOnly"));

		if (!readOnly && ArrayUtil.contains(_READ_ONLY_FIELDS, field)) {
			_printPortletMessageInfo(
				httpServletRequest, httpServletResponse,
				"the-info-box-component-is-not-correctly-configured");

			return;
		}

		httpServletRequest.setAttribute(
			"liferay-commerce:info-box:buttonStyle",
			_getConfigurationValue(
				fragmentRendererContext, fragmentEntryLink, "buttonStyle"));
		httpServletRequest.setAttribute(
			"liferay-commerce:info-box:field", field);

		CommerceOrder commerceOrder =
			CommerceOrderInfoItemUtil.getCommerceOrder(
				_commerceOrderService, httpServletRequest);

		if (commerceOrder == null) {
			if (_isEditMode(httpServletRequest)) {
				_printPortletMessageInfo(
					httpServletRequest, httpServletResponse,
					"the-info-box-component-will-be-shown-here");
			}

			return;
		}

		try {
			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(
					"/fragment/renderer/info_box/page.jsp");

			PermissionChecker permissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			httpServletRequest.setAttribute(
				"liferay-commerce:info-box:additionalProps",
				_getAdditionalProps(
					commerceOrder, field, httpServletRequest,
					permissionChecker));

			httpServletRequest.setAttribute(
				"liferay-commerce:info-box:commerceOrderId",
				commerceOrder.getCommerceOrderId());
			httpServletRequest.setAttribute(
				"liferay-commerce:info-box:fieldValue",
				_getFieldValue(
					commerceOrder, field, httpServletRequest,
					fragmentRendererContext.getLocale()));
			httpServletRequest.setAttribute(
				"liferay-commerce:info-box:fieldValueType",
				_getEditableFieldValueType(field));
			httpServletRequest.setAttribute(
				"liferay-commerce:info-box:hasPermission",
				_commerceOrderModelResourcePermission.contains(
					permissionChecker, commerceOrder, ActionKeys.UPDATE));

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			httpServletRequest.setAttribute(
				"liferay-commerce:info-box:label",
				_language.get(
					fragmentRendererContext.getLocale(),
					_getConfigurationValue(
						fragmentRendererContext, fragmentEntryLink, "label")));

			String namespace = (String)httpServletRequest.getAttribute(
				"liferay-commerce:info-box:namespace");

			if (Validator.isNull(namespace)) {
				PortletDisplay portletDisplay =
					themeDisplay.getPortletDisplay();

				namespace = portletDisplay.getNamespace();
			}

			if (Validator.isNull(namespace)) {
				namespace = StringUtil.randomId() + StringPool.UNDERLINE;
			}

			httpServletRequest.setAttribute(
				"liferay-commerce:info-box:namespace", namespace);

			httpServletRequest.setAttribute(
				"liferay-commerce:info-box:open", commerceOrder.isOpen());
			httpServletRequest.setAttribute(
				"liferay-commerce:info-box:readOnly", readOnly);

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private Map<String, Object> _getAdditionalProps(
			CommerceOrder commerceOrder, String field,
			HttpServletRequest httpServletRequest,
			PermissionChecker permissionChecker)
		throws PortalException {

		if (field.equals("billingAddress")) {
			return HashMapBuilder.<String, Object>put(
				"addressSubtypeConfiguration",
				_getAddressSubtypeConfiguration(commerceOrder.getCompanyId())
			).put(
				"hasManageAddressesPermission",
				() -> {
					CommerceContext commerceContext =
						(CommerceContext)httpServletRequest.getAttribute(
							CommerceWebKeys.COMMERCE_CONTEXT);

					return _accountEntryModelResourcePermission.contains(
						permissionChecker, commerceContext.getAccountEntry(),
						"MANAGE_ADDRESSES");
				}
			).put(
				"value", commerceOrder.getBillingAddressId()
			).build();
		}
		else if (field.equals("deliveryTermId")) {
			long deliveryCommerceTermEntryId =
				commerceOrder.getDeliveryCommerceTermEntryId();

			if (deliveryCommerceTermEntryId == 0) {
				return Collections.emptyMap();
			}

			return HashMapBuilder.<String, Object>put(
				"termDescription",
				commerceOrder.getDeliveryCommerceTermEntryDescription()
			).put(
				"value", deliveryCommerceTermEntryId
			).build();
		}
		else if (field.equals("paymentMethod")) {
			CommercePaymentMethod commercePaymentMethod =
				_commercePaymentMethodRegistry.getCommercePaymentMethod(
					commerceOrder.getCommercePaymentMethodKey());

			if (commercePaymentMethod != null) {
				return HashMapBuilder.<String, Object>put(
					"value", commercePaymentMethod.getKey()
				).build();
			}

			CommercePaymentIntegration commercePaymentIntegration =
				_commercePaymentIntegrationRegistry.
					getCommercePaymentIntegration(
						commerceOrder.getCommercePaymentMethodKey());

			if (commercePaymentIntegration != null) {
				return HashMapBuilder.<String, Object>put(
					"value", commercePaymentIntegration.getKey()
				).build();
			}

			return Collections.emptyMap();
		}
		else if (field.equals("paymentTermId")) {
			long paymentCommerceTermEntryId =
				commerceOrder.getPaymentCommerceTermEntryId();

			if (paymentCommerceTermEntryId == 0) {
				return Collections.emptyMap();
			}

			return HashMapBuilder.<String, Object>put(
				"termDescription",
				commerceOrder.getPaymentCommerceTermEntryDescription()
			).put(
				"value", paymentCommerceTermEntryId
			).build();
		}
		else if (field.equals("purchaseOrderDocument")) {
			List<FileEntry> attachmentFileEntries =
				commerceOrder.getAttachmentFileEntries(
					QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			if (attachmentFileEntries.isEmpty()) {
				return Collections.emptyMap();
			}

			FileEntry fileEntry = attachmentFileEntries.get(0);

			return HashMapBuilder.<String, Object>put(
				"downloadURL",
				DLURLHelperUtil.getDownloadURL(
					fileEntry, fileEntry.getLatestFileVersion(), null,
					StringPool.BLANK, true, true)
			).put(
				"value", fileEntry.getFileEntryId()
			).build();
		}
		else if (field.equals("shippingAddress")) {
			return HashMapBuilder.<String, Object>put(
				"addressSubtypeConfiguration",
				_getAddressSubtypeConfiguration(commerceOrder.getCompanyId())
			).put(
				"hasManageAddressesPermission",
				() -> {
					CommerceContext commerceContext =
						(CommerceContext)httpServletRequest.getAttribute(
							CommerceWebKeys.COMMERCE_CONTEXT);

					return _accountEntryModelResourcePermission.contains(
						permissionChecker, commerceContext.getAccountEntry(),
						"MANAGE_ADDRESSES");
				}
			).put(
				"value", commerceOrder.getShippingAddressId()
			).build();
		}
		else if (field.equals("shippingMethod")) {
			CommerceShippingMethod commerceShippingMethod =
				commerceOrder.getCommerceShippingMethod();

			if (commerceShippingMethod == null) {
				return Collections.emptyMap();
			}

			return HashMapBuilder.<String, Object>put(
				"value",
				() -> {
					if (Validator.isNull(
							commerceOrder.getShippingOptionName())) {

						return commerceShippingMethod.getEngineKey();
					}

					return commerceShippingMethod.getEngineKey() + "#" +
						commerceOrder.getShippingOptionName();
				}
			).build();
		}

		return Collections.emptyMap();
	}

	private String _getAddress(CommerceAddress commerceAddress, Locale locale)
		throws PortalException {

		if (commerceAddress == null) {
			return StringPool.BLANK;
		}

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		Country country = commerceAddress.getCountry();
		Region region = commerceAddress.getRegion();

		jsonObject.put(
			"city", commerceAddress.getCity()
		).put(
			"country", country.getName(locale)
		).put(
			"name", commerceAddress.getName()
		).put(
			"region",
			(region == null) ? StringPool.BLANK :
				region.getTitle(_language.getLanguageId(locale))
		).put(
			"street1", commerceAddress.getStreet1()
		).put(
			"subtype", commerceAddress.getSubtype(locale)
		).put(
			"zip", commerceAddress.getZip()
		);

		return jsonObject.toString();
	}

	private Map<String, String> _getAddressSubtypeConfiguration(
		long companyId) {

		return HashMapBuilder.put(
			"billing",
			AccountEntryAddressSubtypeConfigurationManagerUtil.
				getAddressSubtypeListTypeDefinitionExternalReferenceCode(
					companyId,
					AccountListTypeConstants.ACCOUNT_ENTRY_ADDRESS_TYPE_BILLING)
		).put(
			"billingAndShipping",
			AccountEntryAddressSubtypeConfigurationManagerUtil.
				getAddressSubtypeListTypeDefinitionExternalReferenceCode(
					companyId,
					AccountListTypeConstants.
						ACCOUNT_ENTRY_ADDRESS_TYPE_BILLING_AND_SHIPPING)
		).put(
			"shipping",
			AccountEntryAddressSubtypeConfigurationManagerUtil.
				getAddressSubtypeListTypeDefinitionExternalReferenceCode(
					companyId,
					AccountListTypeConstants.
						ACCOUNT_ENTRY_ADDRESS_TYPE_SHIPPING)
		).build();
	}

	private String _getConfigurationValue(
		FragmentRendererContext fragmentRendererContext,
		FragmentEntryLink fragmentEntryLink, String name) {

		return GetterUtil.getString(
			_fragmentEntryConfigurationParser.getFieldValue(
				getConfigurationJSONObject(fragmentRendererContext),
				fragmentEntryLink.getEditableValuesJSONObject(),
				fragmentRendererContext.getLocale(), name));
	}

	private String _getEditableFieldValueType(String field) {
		if (field.equals("billingAddress") || field.equals("shippingAddress")) {
			return "address";
		}
		else if (field.equals("requestedDeliveryDate")) {
			return "date";
		}

		return "text";
	}

	private String _getFieldValue(
			CommerceOrder commerceOrder, String field,
			HttpServletRequest httpServletRequest, Locale locale)
		throws PortalException {

		if (field.equals("accountInfo")) {
			AccountEntry accountEntry = commerceOrder.getAccountEntry();

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			if ((accountEntry == null) ||
				(accountEntry.isGuestAccount() && themeDisplay.isSignedIn())) {

				return StringPool.BLANK;
			}

			return StringBundler.concat(
				accountEntry.getName(), StringPool.NEW_LINE, StringPool.POUND,
				accountEntry.getAccountEntryId());
		}
		else if (field.equals("billingAddress")) {
			return _getAddress(commerceOrder.getBillingAddress(), locale);
		}
		else if (field.equals("channelName")) {
			CommerceChannel commerceChannel =
				_commerceChannelLocalService.getCommerceChannelByOrderGroupId(
					commerceOrder.getGroupId());

			return commerceChannel.getName();
		}
		else if (field.equals("deliveryTermId")) {
			return commerceOrder.getDeliveryCommerceTermEntryName();
		}
		else if (field.equals("orderDate")) {
			DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
				DateTimeFormatterBuilder.getLocalizedDateTimePattern(
					FormatStyle.SHORT, FormatStyle.SHORT,
					IsoChronology.INSTANCE, locale),
				locale);

			return dateFormat.format(commerceOrder.getOrderDate());
		}
		else if (field.equals("notes")) {
			return StringPool.BLANK;
		}
		else if (field.equals("orderType")) {
			CommerceOrderType commerceOrderType =
				_commerceOrderTypeService.fetchCommerceOrderType(
					commerceOrder.getCommerceOrderTypeId());

			if (commerceOrderType != null) {
				return commerceOrderType.getName(locale);
			}
		}
		else if (field.equals("paymentMethod")) {
			CommercePaymentMethod commercePaymentMethod =
				_commercePaymentMethodRegistry.getCommercePaymentMethod(
					commerceOrder.getCommercePaymentMethodKey());

			if (commercePaymentMethod != null) {
				return commercePaymentMethod.getName(locale);
			}

			CommercePaymentIntegration commercePaymentIntegration =
				_commercePaymentIntegrationRegistry.
					getCommercePaymentIntegration(
						commerceOrder.getCommercePaymentMethodKey());

			if (commercePaymentIntegration != null) {
				return commercePaymentIntegration.getName(locale);
			}
		}
		else if (field.equals("paymentTermId")) {
			return commerceOrder.getPaymentCommerceTermEntryName();
		}
		else if (field.equals("purchaseOrderDocument")) {
			List<FileEntry> attachmentFileEntries =
				commerceOrder.getAttachmentFileEntries(
					QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			if (attachmentFileEntries.isEmpty()) {
				return StringPool.BLANK;
			}

			FileEntry fileEntry = attachmentFileEntries.get(0);

			return fileEntry.getFileName();
		}
		else if (field.equals("purchaseOrderNumber")) {
			return commerceOrder.getPurchaseOrderNumber();
		}
		else if (field.equals("requestedDeliveryDate")) {
			if (commerceOrder.getRequestedDeliveryDate() == null) {
				return StringPool.BLANK;
			}

			DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
				"yyyy-MM-dd", locale);

			return dateFormat.format(commerceOrder.getOrderDate());
		}
		else if (field.equals("shippingAddress")) {
			return _getAddress(commerceOrder.getShippingAddress(), locale);
		}
		else if (field.equals("shippingMethod")) {
			CommerceShippingMethod commerceShippingMethod =
				commerceOrder.getCommerceShippingMethod();

			if (commerceShippingMethod != null) {
				if (Validator.isNull(commerceOrder.getShippingOptionName())) {
					return commerceShippingMethod.getName(locale);
				}

				CommerceShippingEngine commerceShippingEngine =
					_commerceShippingEngineRegistry.getCommerceShippingEngine(
						commerceShippingMethod.getEngineKey());

				return StringBundler.concat(
					commerceShippingMethod.getName(locale), " - ",
					commerceShippingEngine.getCommerceShippingOptionLabel(
						commerceOrder.getShippingOptionName(), locale));
			}
		}

		return StringPool.BLANK;
	}

	private boolean _isEditMode(HttpServletRequest httpServletRequest) {
		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		String layoutMode = ParamUtil.getString(
			originalHttpServletRequest, "p_l_mode", Constants.VIEW);

		return layoutMode.equals(Constants.EDIT);
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

	private static final String[] _READ_ONLY_FIELDS = {
		"accountInfo", "channelName", "orderDate", "orderType"
	};

	private static final Log _log = LogFactoryUtil.getLog(
		InfoBoxFragmentRenderer.class);

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(model.class.name=com.liferay.account.model.AccountEntry)"
	)
	private volatile ModelResourcePermission<AccountEntry>
		_accountEntryModelResourcePermission;

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
	private CommerceOrderTypeService _commerceOrderTypeService;

	@Reference
	private CommercePaymentIntegrationRegistry
		_commercePaymentIntegrationRegistry;

	@Reference
	private CommercePaymentMethodRegistry _commercePaymentMethodRegistry;

	@Reference
	private CommerceShippingEngineRegistry _commerceShippingEngineRegistry;

	@Reference
	private FragmentEntryConfigurationParser _fragmentEntryConfigurationParser;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.order.content.web)"
	)
	private ServletContext _servletContext;

}