/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.fragment.renderer;

import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.util.CPDefinitionHelper;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
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

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(service = FragmentRenderer.class)
public class AddToCartFragmentRenderer implements FragmentRenderer {

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
						"/fragment/renderer/add_to_cart/dependencies" +
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
		return _language.get(locale, "add-to-cart");
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

		Object infoItem = httpServletRequest.getAttribute(
			InfoDisplayWebKeys.INFO_ITEM);

		if (!(infoItem instanceof CPDefinition)) {
			if (_isEditMode(httpServletRequest)) {
				_printPortletMessageInfo(
					httpServletRequest, httpServletResponse,
					"the-add-to-cart-component-will-be-shown-here");
			}

			return;
		}

		try {
			CommerceContext commerceContext =
				(CommerceContext)httpServletRequest.getAttribute(
					CommerceWebKeys.COMMERCE_CONTEXT);

			CPDefinition cpDefinition = (CPDefinition)infoItem;

			httpServletRequest.setAttribute(
				"liferay-commerce:add-to-cart:cpCatalogEntry",
				_cpDefinitionHelper.getCPCatalogEntry(
					CommerceUtil.getCommerceAccountId(commerceContext),
					commerceContext.getCommerceChannelGroupId(),
					cpDefinition.getCPDefinitionId(),
					_portal.getLocale(httpServletRequest)));

			FragmentEntryLink fragmentEntryLink =
				fragmentRendererContext.getFragmentEntryLink();

			httpServletRequest.setAttribute(
				"liferay-commerce:add-to-cart:alignment",
				_getConfigurationValue(fragmentEntryLink, "alignment"));
			httpServletRequest.setAttribute(
				"liferay-commerce:add-to-cart:inline",
				GetterUtil.getBoolean(
					_fragmentEntryConfigurationParser.getFieldValue(
						fragmentEntryLink.getConfiguration(),
						fragmentEntryLink.getEditableValues(),
						LocaleUtil.getMostRelevantLocale(), "inline")));

			String namespace = (String)httpServletRequest.getAttribute(
				"liferay-commerce:add-to-cart:namespace");

			if (Validator.isNull(namespace)) {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				PortletDisplay portletDisplay =
					themeDisplay.getPortletDisplay();

				namespace = portletDisplay.getNamespace();
			}

			httpServletRequest.setAttribute(
				"liferay-commerce:add-to-cart:namespace", namespace);

			httpServletRequest.setAttribute(
				"liferay-commerce:add-to-cart:size",
				_getConfigurationValue(fragmentEntryLink, "size"));

			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(
					"/fragment/renderer/add_to_cart/page.jsp");

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private String _getConfigurationValue(
		FragmentEntryLink fragmentEntryLink, String name) {

		return GetterUtil.getString(
			_fragmentEntryConfigurationParser.getFieldValue(
				fragmentEntryLink.getConfiguration(),
				fragmentEntryLink.getEditableValues(),
				LocaleUtil.getMostRelevantLocale(), name));
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
		AddToCartFragmentRenderer.class);

	@Reference
	private CPDefinitionHelper _cpDefinitionHelper;

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