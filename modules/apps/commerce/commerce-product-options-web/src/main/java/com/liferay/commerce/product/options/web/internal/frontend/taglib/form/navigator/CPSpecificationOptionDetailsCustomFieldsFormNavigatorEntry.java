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
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.taglib.util.CustomAttributesUtil;

import jakarta.servlet.ServletContext;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 */
@Component(
	property = "form.navigator.entry.order:Integer=10",
	service = FormNavigatorEntry.class
)
public class CPSpecificationOptionDetailsCustomFieldsFormNavigatorEntry
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
		return "custom-fields";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "custom-fields");
	}

	@Override
	public ServletContext getServletContext() {
		return null;
	}

	@Override
	public boolean isVisible(
		User user, CPSpecificationOption cpSpecificationOption) {

		boolean hasCustomAttributesAvailable = false;

		try {
			long classPK = 0;

			if (cpSpecificationOption != null) {
				classPK = cpSpecificationOption.getCPSpecificationOptionId();
			}

			hasCustomAttributesAvailable =
				CustomAttributesUtil.hasCustomAttributes(
					user.getCompanyId(), CPSpecificationOption.class.getName(),
					classPK, null);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return hasCustomAttributesAvailable;
	}

	@Override
	protected String getJspPath() {
		return "/specification_option/custom_fields.jsp";
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPSpecificationOptionDetailsCustomFieldsFormNavigatorEntry.class);

	@Reference
	private Language _language;

}