/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.frontend.taglib.form.navigator;

import com.liferay.commerce.product.content.web.internal.constants.CPPublisherConstants;
import com.liferay.commerce.product.content.web.internal.helper.CPPublisherWebHelper;
import com.liferay.frontend.taglib.form.navigator.BaseJSPFormNavigatorEntry;
import com.liferay.frontend.taglib.form.navigator.FormNavigatorEntry;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import jakarta.portlet.PortletPreferences;

import jakarta.servlet.ServletContext;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = "form.navigator.entry.order:Integer=300",
	service = FormNavigatorEntry.class
)
public class ProductEntriesFormNavigatorEntry
	extends BaseJSPFormNavigatorEntry<Void> {

	@Override
	public String getCategoryKey() {
		return CPPublisherConstants.CATEGORY_KEY_PRODUCT_SELECTION;
	}

	@Override
	public String getFormNavigatorId() {
		return CPPublisherConstants.FORM_NAVIGATOR_ID_CONFIGURATION;
	}

	@Override
	public String getKey() {
		return "product-entries";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, getKey());
	}

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public boolean isVisible(User user, Void object) {
		return _isManualSelection();
	}

	@Override
	protected String getJspPath() {
		return "/product_publisher/configuration/product_entries.jsp";
	}

	private boolean _isManualSelection() {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		PortletPreferences portletPreferences =
			themeDisplay.getStrictLayoutPortletSetup(
				themeDisplay.getLayout(), portletDisplay.getPortletResource());

		return _cpPublisherWebHelper.isManualSelection(portletPreferences);
	}

	@Reference
	private CPPublisherWebHelper _cpPublisherWebHelper;

	@Reference
	private Language _language;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.product.content.web)"
	)
	private ServletContext _servletContext;

}