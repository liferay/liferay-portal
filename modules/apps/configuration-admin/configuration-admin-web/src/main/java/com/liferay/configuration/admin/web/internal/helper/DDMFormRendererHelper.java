/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.helper;

import com.liferay.configuration.admin.web.internal.model.ConfigurationModel;
import com.liferay.configuration.admin.web.internal.util.ConfigurationDDMFormDeclarationUtil;
import com.liferay.configuration.admin.web.internal.util.ConfigurationModelToDDMFormConverter;
import com.liferay.configuration.admin.web.internal.util.ConfigurationModelToDDMFormValuesConverter;
import com.liferay.configuration.admin.web.internal.util.ResourceBundleLoaderProviderUtil;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderer;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderingContext;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderingException;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.DDMFormLayoutFactory;
import com.liferay.dynamic.data.mapping.util.DDMUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.settings.LocationVariableResolver;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Marcellus Tavares
 */
public class DDMFormRendererHelper {

	public DDMFormRendererHelper(
		PortletRequest portletRequest, PortletResponse portletResponse,
		ConfigurationModel configurationModel, DDMFormRenderer ddmFormRenderer,
		LocationVariableResolver locationVariableResolver) {

		_portletRequest = portletRequest;
		_portletResponse = portletResponse;
		_configurationModel = configurationModel;
		_ddmFormRenderer = ddmFormRenderer;
		_locationVariableResolver = locationVariableResolver;
	}

	public String getDDMFormHTML() throws PortletException {
		try {
			DDMForm ddmForm = _getDDMForm();

			return _ddmFormRenderer.render(
				ddmForm, _getDDMFormLayout(ddmForm),
				_createDDMFormRenderingContext(ddmForm));
		}
		catch (DDMFormRenderingException ddmFormRenderingException) {
			_log.error("Unable to render DDM Form ", ddmFormRenderingException);

			throw new PortletException(ddmFormRenderingException);
		}
	}

	private DDMFormRenderingContext _createDDMFormRenderingContext(
		DDMForm ddmForm) {

		DDMFormRenderingContext ddmFormRenderingContext =
			new DDMFormRenderingContext();

		ddmFormRenderingContext.setDDMFormValues(_getDDMFormValues(ddmForm));
		ddmFormRenderingContext.setHttpServletRequest(
			PortalUtil.getHttpServletRequest(_portletRequest));
		ddmFormRenderingContext.setHttpServletResponse(
			PortalUtil.getHttpServletResponse(_portletResponse));
		ddmFormRenderingContext.setLocale(_getLocale());
		ddmFormRenderingContext.setPortletNamespace(
			_portletResponse.getNamespace());
		ddmFormRenderingContext.setShowSubmitButton(false);

		return ddmFormRenderingContext;
	}

	private DDMForm _getDDMForm() {
		ResourceBundleLoader resourceBundleLoader =
			ResourceBundleLoaderProviderUtil.getResourceBundleLoader(
				_configurationModel.getBundleSymbolicName());

		Locale locale = _getLocale();

		ResourceBundle resourceBundle = resourceBundleLoader.loadResourceBundle(
			locale);

		ConfigurationModelToDDMFormConverter
			configurationModelToDDMFormConverter =
				new ConfigurationModelToDDMFormConverter(
					_configurationModel, locale, resourceBundle);

		return configurationModelToDDMFormConverter.getDDMForm();
	}

	private DDMFormLayout _getDDMFormLayout(DDMForm ddmForm) {
		Class<?> formClass =
			ConfigurationDDMFormDeclarationUtil.getConfigurationDDMFormClass(
				_configurationModel);

		if (formClass != null) {
			try {
				return DDMFormLayoutFactory.create(formClass);
			}
			catch (IllegalArgumentException illegalArgumentException) {
				if (_log.isDebugEnabled()) {
					_log.debug(illegalArgumentException);
				}
			}
		}

		return DDMUtil.getDefaultDDMFormLayout(ddmForm);
	}

	private DDMFormValues _getDDMFormValues(DDMForm ddmForm) {
		ConfigurationModelToDDMFormValuesConverter
			configurationModelToDDMFormValuesConverter =
				new ConfigurationModelToDDMFormValuesConverter(
					_configurationModel, ddmForm, _getLocale(),
					_locationVariableResolver);

		return configurationModelToDDMFormValuesConverter.getDDMFormValues();
	}

	private Locale _getLocale() {
		ThemeDisplay themeDisplay = (ThemeDisplay)_portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return themeDisplay.getLocale();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormRendererHelper.class);

	private final ConfigurationModel _configurationModel;
	private final DDMFormRenderer _ddmFormRenderer;
	private final LocationVariableResolver _locationVariableResolver;
	private final PortletRequest _portletRequest;
	private final PortletResponse _portletResponse;

}