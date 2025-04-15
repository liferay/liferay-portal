/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.product.compare.frontend.taglib.form.navigator;

import com.liferay.commerce.product.content.web.internal.configuration.CPCompareContentPortletInstanceConfiguration;
import com.liferay.commerce.product.content.web.internal.constants.CPCompareContentConstants;
import com.liferay.frontend.taglib.form.navigator.BaseJSPFormNavigatorEntry;
import com.liferay.frontend.taglib.form.navigator.FormNavigatorEntry;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import jakarta.servlet.ServletContext;

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "form.navigator.entry.order:Integer=500",
	service = FormNavigatorEntry.class
)
public class ProductListRendererFormNavigatorEntry
	extends BaseJSPFormNavigatorEntry<Void> {

	@Override
	public String getCategoryKey() {
		return CPCompareContentConstants.CATEGORY_KEY_RENDER_SELECTION;
	}

	@Override
	public String getFormNavigatorId() {
		return CPCompareContentConstants.FORM_NAVIGATOR_ID_CONFIGURATION;
	}

	@Override
	public String getKey() {
		return "product-list-renderer";
	}

	@Override
	public String getLabel(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return _language.get(resourceBundle, getKey());
	}

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public boolean isVisible(User user, Void object) {
		return _isSelectionStyleCustomRenderer();
	}

	@Override
	protected String getJspPath() {
		return "/compare_products/configuration/product_list_renderer.jsp";
	}

	private boolean _isSelectionStyleCustomRenderer() {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		try {
			CPCompareContentPortletInstanceConfiguration
				cpCompareContentPortletInstanceConfiguration =
					_configurationProvider.getPortletInstanceConfiguration(
						CPCompareContentPortletInstanceConfiguration.class,
						serviceContext.getThemeDisplay());

			String selectionStyle =
				cpCompareContentPortletInstanceConfiguration.selectionStyle();

			return selectionStyle.equals("custom");
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ProductListRendererFormNavigatorEntry.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Language _language;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.product.content.web)"
	)
	private ServletContext _servletContext;

}