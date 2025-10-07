/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.fragment.renderer;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionSpecificationOptionValue;
import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.commerce.product.service.CPDefinitionSpecificationOptionValueLocalService;
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

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(service = FragmentRenderer.class)
public class ProductSpecificationFragmentRenderer implements FragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "commerce-product";
	}

	@Override
	public JSONObject getConfigurationJSONObject(
		FragmentRendererContext fragmentRendererContext) {

		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject(
				StringUtil.read(
					getClass(),
					"/com/liferay/commerce/product/content/web/internal" +
						"/fragment/renderer/product_specification" +
							"/dependencies/configuration.json"));

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
		return _language.get(locale, "product-specification");
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

		try {
			Object infoItem = httpServletRequest.getAttribute(
				InfoDisplayWebKeys.INFO_ITEM);

			if (!(infoItem instanceof CPDefinition)) {
				if (_isEditMode(httpServletRequest)) {
					_printPortletMessageInfo(
						httpServletRequest, httpServletResponse,
						"the-product-specification-component-will-be-shown-" +
							"here");
				}

				return;
			}

			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(
					"/fragment/renderer/product_specification/page.jsp");

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			String label = StringPool.BLANK;

			CPDefinition cpDefinition = (CPDefinition)infoItem;

			CPDefinitionSpecificationOptionValue
				cpDefinitionSpecificationOptionValue =
					_cpDefinitionSpecificationOptionValueLocalService.
						fetchCPDefinitionSpecificationOptionValue(
							cpDefinition.getCPDefinitionId(),
							GetterUtil.getString(
								_getConfigurationValue(
									fragmentRendererContext.
										getFragmentEntryLink(),
									"key")));

			if (cpDefinitionSpecificationOptionValue != null) {
				CPSpecificationOption cpSpecificationOption =
					cpDefinitionSpecificationOptionValue.
						getCPSpecificationOption();

				label = cpSpecificationOption.getTitle(
					themeDisplay.getLanguageId());
			}

			httpServletRequest.setAttribute(
				"liferay-commerce:product-specification:label", label);

			httpServletRequest.setAttribute(
				"liferay-commerce:product-specification:labelElementType",
				GetterUtil.getString(
					_getConfigurationValue(
						fragmentRendererContext.getFragmentEntryLink(),
						"labelElementType")));

			String namespace = (String)httpServletRequest.getAttribute(
				"liferay-commerce:product-specification:namespace");

			if (Validator.isNull(namespace)) {
				PortletDisplay portletDisplay =
					themeDisplay.getPortletDisplay();

				namespace = portletDisplay.getNamespace();

				httpServletRequest.setAttribute(
					"liferay-commerce:product-specification:namespace",
					namespace);
			}

			httpServletRequest.setAttribute(
				"liferay-commerce:product-specification:showLabel",
				GetterUtil.getBoolean(
					_getConfigurationValue(
						fragmentRendererContext.getFragmentEntryLink(),
						"showLabel")));

			String value = StringPool.BLANK;

			if (cpDefinitionSpecificationOptionValue != null) {
				value = cpDefinitionSpecificationOptionValue.getValue(
					themeDisplay.getLanguageId());
			}

			httpServletRequest.setAttribute(
				"liferay-commerce:product-specification:value", value);

			httpServletRequest.setAttribute(
				"liferay-commerce:product-specification:valueElementType",
				GetterUtil.getString(
					_getConfigurationValue(
						fragmentRendererContext.getFragmentEntryLink(),
						"valueElementType")));

			if (cpDefinitionSpecificationOptionValue != null) {
				CPSpecificationOption cpSpecificationOption =
					cpDefinitionSpecificationOptionValue.
						getCPSpecificationOption();

				httpServletRequest.setAttribute(
					"liferay-commerce:product-specification:visible",
					cpDefinitionSpecificationOptionValue.isVisible() &&
					cpSpecificationOption.isVisible());
			}
			else {
				httpServletRequest.setAttribute(
					"liferay-commerce:product-specification:visible",
					Boolean.FALSE);
			}

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private Object _getConfigurationValue(
		FragmentEntryLink fragmentEntryLink, String name) {

		return _fragmentEntryConfigurationParser.getFieldValue(
			fragmentEntryLink.getConfigurationJSONObject(),
			fragmentEntryLink.getEditableValuesJSONObject(),
			LocaleUtil.getMostRelevantLocale(), name);
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

	private static final Log _log = LogFactoryUtil.getLog(
		ProductSpecificationFragmentRenderer.class);

	@Reference
	private CPDefinitionSpecificationOptionValueLocalService
		_cpDefinitionSpecificationOptionValueLocalService;

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