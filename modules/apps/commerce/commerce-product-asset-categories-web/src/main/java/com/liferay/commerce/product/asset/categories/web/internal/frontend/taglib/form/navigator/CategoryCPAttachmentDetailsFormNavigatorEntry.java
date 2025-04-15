/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.asset.categories.web.internal.frontend.taglib.form.navigator;

import com.liferay.commerce.product.asset.categories.web.internal.servlet.taglib.ui.constants.CategoryCPAttachmentFormNavigatorConstants;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.frontend.taglib.form.navigator.BaseJSPFormNavigatorEntry;
import com.liferay.frontend.taglib.form.navigator.FormNavigatorEntry;
import com.liferay.portal.kernel.language.Language;

import jakarta.servlet.ServletContext;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "form.navigator.entry.order:Integer=300",
	service = FormNavigatorEntry.class
)
public class CategoryCPAttachmentDetailsFormNavigatorEntry
	extends BaseJSPFormNavigatorEntry<CPAttachmentFileEntry> {

	@Override
	public String getCategoryKey() {
		return CategoryCPAttachmentFormNavigatorConstants.
			CATEGORY_KEY_CP_ATTACHMENT_FILE_ENTRY_DETAILS;
	}

	@Override
	public String getFormNavigatorId() {
		return CategoryCPAttachmentFormNavigatorConstants.
			FORM_NAVIGATOR_ID_COMMERCE_CP_ATTACHMENT_FILE_ENTRY;
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
		return _servletContext;
	}

	@Override
	protected String getJspPath() {
		return "/image/details.jsp";
	}

	@Reference
	private Language _language;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.product.asset.categories.web)"
	)
	private ServletContext _servletContext;

}