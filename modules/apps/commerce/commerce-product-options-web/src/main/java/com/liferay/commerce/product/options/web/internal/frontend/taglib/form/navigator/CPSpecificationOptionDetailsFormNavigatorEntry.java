/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.options.web.internal.frontend.taglib.form.navigator;

import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.commerce.product.options.web.internal.servlet.taglib.ui.constants.CPSpecificationOptionFormNavigatorConstants;
import com.liferay.frontend.taglib.form.navigator.BaseJSPFormNavigatorEntry;
import com.liferay.frontend.taglib.form.navigator.FormNavigatorEntry;
import com.liferay.portal.kernel.language.Language;

import jakarta.servlet.ServletContext;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 */
@Component(
	property = "form.navigator.entry.order:Integer=100",
	service = FormNavigatorEntry.class
)
public class CPSpecificationOptionDetailsFormNavigatorEntry
	extends BaseJSPFormNavigatorEntry<CPSpecificationOption> {

	@Override
	public String getCategoryKey() {
		return CPSpecificationOptionFormNavigatorConstants.
			CATEGORY_KEY_COMMERCE_PRODUCT_SPECIFICATION_OPTION_DETAILS;
	}

	@Override
	public String getFormNavigatorId() {
		return CPSpecificationOptionFormNavigatorConstants.
			FORM_NAVIGATOR_ID_COMMERCE_PRODUCT_SPECIFICATION_OPTION;
	}

	@Override
	public String getKey() {
		return "details";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "details");
	}

	@Override
	public ServletContext getServletContext() {
		return null;
	}

	@Override
	protected String getJspPath() {
		return "/specification_option/details.jsp";
	}

	@Reference
	private Language _language;

}