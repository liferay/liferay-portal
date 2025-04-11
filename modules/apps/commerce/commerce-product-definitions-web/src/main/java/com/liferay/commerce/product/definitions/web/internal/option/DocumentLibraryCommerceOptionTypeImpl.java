/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.option;

import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.option.CommerceOptionType;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderer;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderingContext;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.ProductOption;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.template.react.renderer.ComponentDescriptor;
import com.liferay.portal.template.react.renderer.ReactRenderer;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import java.io.PrintWriter;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = {
		"commerce.option.type.display.order:Integer=200",
		"commerce.option.type.key=" + CPConstants.PRODUCT_OPTION_DOCUMENT_LIBRARY_KEY
	},
	service = CommerceOptionType.class
)
public class DocumentLibraryCommerceOptionTypeImpl
	implements CommerceOptionType {

	@Override
	public String getKey() {
		return CPConstants.PRODUCT_OPTION_DOCUMENT_LIBRARY_KEY;
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "upload");
	}

	@Override
	public boolean hasValues() {
		return false;
	}

	@Override
	public void render(
			CPDefinitionOptionRel cpDefinitionOptionRel,
			long defaultCPInstanceId, boolean forceRequired, String json,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		if (cpDefinitionOptionRel == null) {
			return;
		}

		PrintWriter printWriter = httpServletResponse.getWriter();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		DDMForm ddmForm = new DDMForm();
		DDMFormField ddmFormField = new DDMFormField(
			cpDefinitionOptionRel.getKey(),
			cpDefinitionOptionRel.getCommerceOptionTypeKey());

		Locale locale = _portal.getLocale(httpServletRequest);

		LocalizedValue ddmFormFieldLabelLocalizedValue = new LocalizedValue(
			locale);

		ddmFormFieldLabelLocalizedValue.addString(
			locale, cpDefinitionOptionRel.getName(locale));

		ddmFormField.setLabel(ddmFormFieldLabelLocalizedValue);

		ddmFormField.setName(cpDefinitionOptionRel.getKey());
		ddmFormField.setRequired(cpDefinitionOptionRel.isRequired());

		ddmForm.addDDMFormField(ddmFormField);
		ddmForm.addAvailableLocale(locale);
		ddmForm.setDefaultLocale(locale);

		DDMFormRenderingContext ddmFormRenderingContext =
			new DDMFormRenderingContext();

		ddmFormRenderingContext.setContainerId(
			"ProductOptions" + cpDefinitionOptionRel.getCPDefinitionId());
		ddmFormRenderingContext.setHttpServletRequest(httpServletRequest);
		ddmFormRenderingContext.setHttpServletResponse(httpServletResponse);
		ddmFormRenderingContext.setLocale(locale);
		ddmFormRenderingContext.setPortletNamespace(
			portletDisplay.getNamespace());
		ddmFormRenderingContext.setShowRequiredFieldsWarning(false);

		printWriter.write(
			_ddmFormRenderer.render(ddmForm, ddmFormRenderingContext));

		printWriter.write("<div>");

		_reactRenderer.renderReact(
			new ComponentDescriptor(
				"{ProductOptionUpload} from commerce-frontend-js"),
			HashMapBuilder.<String, Object>put(
				"componentId", StringUtil.randomId()
			).put(
				"cpDefinitionId", cpDefinitionOptionRel.getCPDefinitionId()
			).put(
				"namespace", portletDisplay.getNamespace()
			).put(
				"productOption",
				_productOptionDTOConverter.toDTO(
					new DefaultDTOConverterContext(
						_dtoConverterRegistry,
						cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
						_portal.getLocale(httpServletRequest), null,
						_portal.getUser(httpServletRequest)))
			).build(),
			httpServletRequest, printWriter);

		printWriter.write("</div>");
	}

	@Reference
	private DDMFormRenderer _ddmFormRenderer;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter.ProductOptionDTOConverter)"
	)
	private DTOConverter<CPDefinitionOptionRel, ProductOption>
		_productOptionDTOConverter;

	@Reference
	private ReactRenderer _reactRenderer;

}