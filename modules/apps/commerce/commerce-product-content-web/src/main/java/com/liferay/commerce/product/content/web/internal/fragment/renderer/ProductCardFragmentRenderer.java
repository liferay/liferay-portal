/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.fragment.renderer;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.product.constants.CPWebKeys;
import com.liferay.commerce.product.content.constants.CPContentWebKeys;
import com.liferay.commerce.product.content.helper.CPContentHelper;
import com.liferay.commerce.product.content.info.item.renderer.CPContentInfoItemRendererRegistry;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.permission.CommerceProductViewPermission;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.util.CPDefinitionHelper;
import com.liferay.commerce.util.CommerceAccountHelper;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(service = FragmentRenderer.class)
public class ProductCardFragmentRenderer implements FragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "commerce-product";
	}

	@Override
	public String getConfiguration(
		FragmentRendererContext fragmentRendererContext) {

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", getClass());

		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject(
				StringUtil.read(
					getClass(),
					"/com/liferay/commerce/product/content/web/internal" +
						"/fragment/renderer/product_card/dependencies" +
							"/configuration.json"));

			return _fragmentEntryConfigurationParser.translateConfiguration(
				jsonObject, resourceBundle);
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}

			return StringPool.BLANK;
		}
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "product-card");
	}

	@Override
	public boolean isSelectable(HttpServletRequest httpServletRequest) {
		return true;
	}

	@Override
	public void render(
			FragmentRendererContext fragmentRendererContext,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		CPDefinition cpDefinition = null;

		InfoItemReference infoItemReference =
			(InfoItemReference)httpServletRequest.getAttribute(
				InfoDisplayWebKeys.INFO_ITEM_REFERENCE);

		if (infoItemReference != null) {
			CommerceContext commerceContext =
				(CommerceContext)httpServletRequest.getAttribute(
					CommerceWebKeys.COMMERCE_CONTEXT);
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			try {
				ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
					(ClassPKInfoItemIdentifier)
						infoItemReference.getInfoItemIdentifier();

				cpDefinition = _cpDefinitionLocalService.getCPDefinition(
					classPKInfoItemIdentifier.getClassPK());

				if (!_commerceProductViewPermission.contains(
						themeDisplay.getPermissionChecker(),
						CommerceUtil.getCommerceAccountId(commerceContext),
						commerceContext.getCommerceChannelGroupId(),
						cpDefinition.getCPDefinitionId())) {

					return;
				}
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}

				return;
			}
		}

		if (cpDefinition == null) {
			Object infoItem = httpServletRequest.getAttribute(
				InfoDisplayWebKeys.INFO_ITEM);

			if (!(infoItem instanceof CPDefinition)) {
				if (_isEditMode(httpServletRequest)) {
					_printPortletMessageInfo(
						httpServletRequest, httpServletResponse,
						"the-product-card-component-will-be-shown-here");
				}

				return;
			}

			cpDefinition = (CPDefinition)infoItem;
		}

		try {
			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(
					"/fragment/renderer/product_card/page.jsp");

			long groupId = _portal.getScopeGroupId(httpServletRequest);

			httpServletRequest.setAttribute(
				CPWebKeys.CP_CATALOG_ENTRY,
				_cpDefinitionHelper.getCPCatalogEntry(
					_getAccountEntryId(groupId, httpServletRequest), groupId,
					cpDefinition.getCPDefinitionId(),
					_portal.getLocale(httpServletRequest)));

			httpServletRequest.setAttribute(
				CPContentWebKeys.CP_CONTENT_HELPER, _cpContentHelper);
			httpServletRequest.setAttribute(
				CPContentWebKeys.CP_CONTENT_INFO_ITEM_RENDERER,
				_cpContentInfoItemRendererRegistry.getCPContentInfoItemRenderer(
					cpDefinition.getProductTypeName()));

			FragmentEntryLink fragmentEntryLink =
				fragmentRendererContext.getFragmentEntryLink();

			String configuration = fragmentEntryLink.getConfiguration();
			String editableValues = fragmentEntryLink.getEditableValues();

			Locale locale = fragmentRendererContext.getLocale();

			httpServletRequest.setAttribute(
				"liferay-commerce:product-card:showAddToCartButton",
				GetterUtil.getBoolean(
					_fragmentEntryConfigurationParser.getFieldValue(
						configuration, editableValues, locale,
						"showAddToCartButton")));
			httpServletRequest.setAttribute(
				"liferay-commerce:product-card:showAddToWishListButton",
				GetterUtil.getBoolean(
					_fragmentEntryConfigurationParser.getFieldValue(
						configuration, editableValues, locale,
						"showAddToWishListButton")));
			httpServletRequest.setAttribute(
				"liferay-commerce:product-card:showAvailabilityLabel",
				GetterUtil.getBoolean(
					_fragmentEntryConfigurationParser.getFieldValue(
						configuration, editableValues, locale,
						"showAvailabilityLabel")));
			httpServletRequest.setAttribute(
				"liferay-commerce:product-card:showCompareCheckbox",
				GetterUtil.getBoolean(
					_fragmentEntryConfigurationParser.getFieldValue(
						configuration, editableValues, locale,
						"showCompareCheckbox")));
			httpServletRequest.setAttribute(
				"liferay-commerce:product-card:showDiscontinuedLabel",
				GetterUtil.getBoolean(
					_fragmentEntryConfigurationParser.getFieldValue(
						configuration, editableValues, locale,
						"showDiscontinuedLabel")));
			httpServletRequest.setAttribute(
				"liferay-commerce:product-card:showImage",
				GetterUtil.getBoolean(
					_fragmentEntryConfigurationParser.getFieldValue(
						configuration, editableValues, locale, "showImage")));
			httpServletRequest.setAttribute(
				"liferay-commerce:product-card:showName",
				GetterUtil.getBoolean(
					_fragmentEntryConfigurationParser.getFieldValue(
						configuration, editableValues, locale, "showName")));
			httpServletRequest.setAttribute(
				"liferay-commerce:product-card:showPrice",
				GetterUtil.getBoolean(
					_fragmentEntryConfigurationParser.getFieldValue(
						configuration, editableValues, locale, "showPrice")));
			httpServletRequest.setAttribute(
				"liferay-commerce:product-card:showSku",
				GetterUtil.getBoolean(
					_fragmentEntryConfigurationParser.getFieldValue(
						configuration, editableValues, locale, "showSku")));

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private long _getAccountEntryId(
			long groupId, HttpServletRequest httpServletRequest)
		throws PortalException {

		long accountEntryId = 0;

		AccountEntry accountEntry =
			_commerceAccountHelper.getCurrentAccountEntry(
				_commerceChannelLocalService.
					getCommerceChannelGroupIdBySiteGroupId(groupId),
				httpServletRequest);

		if (accountEntry != null) {
			accountEntryId = accountEntry.getAccountEntryId();
		}

		return accountEntryId;
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
		HttpServletResponse httpServletResponse, String message) {

		try {
			PrintWriter printWriter = httpServletResponse.getWriter();

			StringBundler sb = new StringBundler(3);

			sb.append("<div class=\"portlet-msg-info\">");

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			sb.append(themeDisplay.translate(message));

			sb.append("</div>");

			printWriter.write(sb.toString());
		}
		catch (IOException ioException) {
			if (_log.isDebugEnabled()) {
				_log.debug(ioException);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ProductCardFragmentRenderer.class);

	@Reference
	private CommerceAccountHelper _commerceAccountHelper;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceProductViewPermission _commerceProductViewPermission;

	@Reference
	private CPContentHelper _cpContentHelper;

	@Reference
	private CPContentInfoItemRendererRegistry
		_cpContentInfoItemRendererRegistry;

	@Reference
	private CPDefinitionHelper _cpDefinitionHelper;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private FragmentEntryConfigurationParser _fragmentEntryConfigurationParser;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.product.content.web)"
	)
	private ServletContext _servletContext;

}