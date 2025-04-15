/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action.helper;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.dynamic.data.mapping.exception.FormInstanceSettingsRedirectURLException;
import com.liferay.dynamic.data.mapping.exception.FormInstanceSettingsStorageTypeException;
import com.liferay.dynamic.data.mapping.exception.StructureDefinitionException;
import com.liferay.dynamic.data.mapping.exception.StructureLayoutException;
import com.liferay.dynamic.data.mapping.form.builder.context.DDMFormContextDeserializer;
import com.liferay.dynamic.data.mapping.form.builder.context.DDMFormContextDeserializerRequest;
import com.liferay.dynamic.data.mapping.form.web.internal.portlet.action.util.DDMFormInstanceFieldSettingsValidator;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceSettings;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.DDMFormFactory;
import com.liferay.dynamic.data.mapping.validator.DDMFormValuesValidationException;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.redirect.RedirectURLSettings;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.InetAddressUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(service = SaveFormInstanceMVCCommandHelper.class)
public class SaveFormInstanceMVCCommandHelper {

	public Map<Locale, String> getNameMap(
			DDMForm ddmForm, String name, String defaultName)
		throws PortalException {

		Locale defaultLocale = ddmForm.getDefaultLocale();

		Map<Locale, String> nameMap = _getLocalizedMap(
			name, ddmForm.getAvailableLocales(), defaultLocale);

		if (nameMap.isEmpty() || Validator.isNull(nameMap.get(defaultLocale))) {
			nameMap.put(
				defaultLocale,
				_language.get(_getResourceBundle(defaultLocale), defaultName));
		}

		return nameMap;
	}

	public DDMFormInstance saveFormInstance(
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws Exception {

		return saveFormInstance(portletRequest, portletResponse, false);
	}

	public DDMFormInstance saveFormInstance(
			PortletRequest portletRequest, PortletResponse portletResponse,
			boolean validateDDMFormFieldSettings)
		throws Exception {

		long formInstanceId = ParamUtil.getLong(
			portletRequest, "formInstanceId");

		if (formInstanceId == 0) {
			return _addFormInstance(
				portletRequest, validateDDMFormFieldSettings);
		}

		return _updateFormInstance(
			portletRequest, formInstanceId, validateDDMFormFieldSettings);
	}

	protected DDMFormLayout getDDMFormLayout(PortletRequest portletRequest)
		throws PortalException {

		try {
			String serializedFormBuilderContext = ParamUtil.getString(
				portletRequest, "serializedFormBuilderContext");

			return ddmFormBuilderContextToDDMFormLayout.deserialize(
				DDMFormContextDeserializerRequest.with(
					serializedFormBuilderContext));
		}
		catch (PortalException portalException) {
			throw new StructureLayoutException(portalException);
		}
	}

	@Reference(
		target = "(dynamic.data.mapping.form.builder.context.deserializer.type=form)"
	)
	protected DDMFormContextDeserializer<DDMForm>
		ddmFormBuilderContextToDDMForm;

	@Reference(
		target = "(dynamic.data.mapping.form.builder.context.deserializer.type=formLayout)"
	)
	protected DDMFormContextDeserializer<DDMFormLayout>
		ddmFormBuilderContextToDDMFormLayout;

	@Reference(
		target = "(dynamic.data.mapping.form.builder.context.deserializer.type=formValues)"
	)
	protected DDMFormContextDeserializer<DDMFormValues>
		ddmFormTemplateContextToDDMFormValues;

	@Reference
	protected volatile DDMFormInstanceFieldSettingsValidator
		formInstanceFieldSettingsValidator;

	@Reference
	protected DDMFormInstanceService formInstanceService;

	@Reference
	protected JSONFactory jsonFactory;

	private DDMFormInstance _addFormInstance(
			PortletRequest portletRequest, boolean validateFormFieldsSettings)
		throws Exception {

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			DDMFormInstance.class.getName(), portletRequest);

		long groupId = ParamUtil.getLong(portletRequest, "groupId");
		String name = ParamUtil.getString(portletRequest, "name");
		String description = ParamUtil.getString(portletRequest, "description");
		DDMForm ddmForm = _getDDMForm(portletRequest);
		DDMFormLayout ddmFormLayout = getDDMFormLayout(portletRequest);

		Map<Locale, String> nameMap = getNameMap(
			ddmForm, name, "untitled-form");
		Map<Locale, String> descriptionMap = _getLocalizedMap(
			description, ddmForm.getAvailableLocales(),
			ddmForm.getDefaultLocale());

		if (ParamUtil.getBoolean(portletRequest, "saveAsDraft")) {
			serviceContext.setAttribute(
				"status", WorkflowConstants.STATUS_DRAFT);
		}

		if (validateFormFieldsSettings) {
			formInstanceFieldSettingsValidator.validate(
				portletRequest, ddmForm);
		}

		DDMFormValues settingsDDMFormValues = _getSettingsDDMFormValues(
			portletRequest);

		_validateSettingsDDMFormValues(
			settingsDDMFormValues,
			_portal.getHttpServletRequest(portletRequest),
			ddmForm.getDefaultLocale());

		return formInstanceService.addFormInstance(
			groupId, nameMap, descriptionMap, ddmForm, ddmFormLayout,
			settingsDDMFormValues, serviceContext);
	}

	private DDMForm _getDDMForm(PortletRequest portletRequest)
		throws Exception {

		try {
			String serializedFormBuilderContext = ParamUtil.getString(
				portletRequest, "serializedFormBuilderContext");

			return ddmFormBuilderContextToDDMForm.deserialize(
				DDMFormContextDeserializerRequest.with(
					serializedFormBuilderContext));
		}
		catch (PortalException portalException) {
			throw new StructureDefinitionException(portalException);
		}
	}

	private Map<Locale, String> _getLocalizedMap(
			String value, Set<Locale> availableLocales, Locale defaultLocale)
		throws PortalException {

		Map<Locale, String> localizedMap = new HashMap<>();

		JSONObject jsonObject = jsonFactory.createJSONObject(value);

		String defaultValueString = jsonObject.getString(
			LocaleUtil.toLanguageId(defaultLocale));

		for (Locale availableLocale : availableLocales) {
			String valueString = jsonObject.getString(
				LocaleUtil.toLanguageId(availableLocale), defaultValueString);

			localizedMap.put(availableLocale, valueString);
		}

		return localizedMap;
	}

	private String _getPropertyValue(
		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap,
		Locale locale, String propertyName) {

		if (!ddmFormFieldValuesMap.containsKey(propertyName)) {
			return StringPool.BLANK;
		}

		List<DDMFormFieldValue> ddmFormFieldValues = ddmFormFieldValuesMap.get(
			propertyName);

		DDMFormFieldValue ddmFormFieldValue = ddmFormFieldValues.get(0);

		Value value = ddmFormFieldValue.getValue();

		String valueString = value.getString(locale);

		try {
			JSONArray jsonArray = jsonFactory.createJSONArray(valueString);

			return jsonArray.getString(0);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return valueString;
		}
	}

	private String _getRedirectURLExceptionMessage(
		HttpServletRequest httpServletRequest, String fieldName, String value) {

		return _language.format(
			httpServletRequest,
			"the-external-redirect-url-x-is-not-allowed.-set-it-in-the-x-" +
				"field-of-the-x-configuration-in-x-to-allow-it",
			new String[] {
				value, fieldName, "redirect-url-configuration-name",
				"jakarta.portlet.title." +
					ConfigurationAdminPortletKeys.INSTANCE_SETTINGS
			});
	}

	private ResourceBundle _getResourceBundle(Locale locale) {
		Class<?> clazz = getClass();

		return ResourceBundleUtil.getBundle(
			"content.Language", locale, clazz.getClassLoader());
	}

	private DDMFormValues _getSettingsDDMFormValues(
			PortletRequest portletRequest)
		throws Exception {

		String settingsContext = ParamUtil.getString(
			portletRequest, "serializedSettingsContext");

		return ddmFormTemplateContextToDDMFormValues.deserialize(
			DDMFormContextDeserializerRequest.with(
				DDMFormFactory.create(DDMFormInstanceSettings.class),
				settingsContext));
	}

	private URI _getURI(String uriString) {
		try {
			return new URI(uriString.trim());
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return null;
		}
	}

	private DDMFormInstance _updateFormInstance(
			PortletRequest portletRequest, long formInstanceId,
			boolean validateFormFieldsSettings)
		throws Exception {

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			DDMFormInstance.class.getName(), portletRequest);

		String name = ParamUtil.getString(portletRequest, "name");
		String description = ParamUtil.getString(portletRequest, "description");
		DDMForm ddmForm = _getDDMForm(portletRequest);
		DDMFormLayout ddmFormLayout = getDDMFormLayout(portletRequest);

		Map<Locale, String> nameMap = getNameMap(
			ddmForm, name, "untitled-form");
		Map<Locale, String> descriptionMap = _getLocalizedMap(
			description, ddmForm.getAvailableLocales(),
			ddmForm.getDefaultLocale());

		if (ParamUtil.getBoolean(portletRequest, "saveAsDraft")) {
			serviceContext.setAttribute(
				"status", WorkflowConstants.ACTION_SAVE_DRAFT);
		}

		if (validateFormFieldsSettings) {
			formInstanceFieldSettingsValidator.validate(
				portletRequest, ddmForm);
		}

		DDMFormValues settingsDDMFormValues = _getSettingsDDMFormValues(
			portletRequest);

		_validateSettingsDDMFormValues(
			settingsDDMFormValues,
			_portal.getHttpServletRequest(portletRequest),
			ddmForm.getDefaultLocale());

		return formInstanceService.updateFormInstance(
			formInstanceId, nameMap, descriptionMap, ddmForm, ddmFormLayout,
			settingsDDMFormValues, serviceContext);
	}

	private void _validateExpirationDate(DDMFormValues ddmFormValues)
		throws Exception {

		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
			ddmFormValues.getDDMFormFieldValuesMap(false);

		if (!GetterUtil.getBoolean(
				_getPropertyValue(
					ddmFormFieldValuesMap, LocaleUtil.ROOT, "neverExpire"),
				true)) {

			try {
				LocalDate.parse(
					_getPropertyValue(
						ddmFormFieldValuesMap, LocaleUtil.ROOT,
						"expirationDate"));
			}
			catch (DateTimeParseException dateTimeParseException) {
				if (_log.isDebugEnabled()) {
					_log.debug(dateTimeParseException);
				}

				DDMForm ddmForm = ddmFormValues.getDDMForm();

				Map<String, DDMFormField> ddmFormFieldsMap =
					ddmForm.getDDMFormFieldsMap(false);

				DDMFormField ddmFormField = ddmFormFieldsMap.get(
					"expirationDate");

				throw new DDMFormValuesValidationException.MustSetValidValue(
					ddmFormField.getLabel(), ddmFormField.getName());
			}
		}
	}

	private void _validateRedirectURL(
			DDMFormValues settingsDDMFormValues,
			HttpServletRequest httpServletRequest)
		throws Exception {

		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
			settingsDDMFormValues.getDDMFormFieldValuesMap();

		if (!ddmFormFieldValuesMap.containsKey("redirectURL")) {
			return;
		}

		List<DDMFormFieldValue> ddmFormFieldValues = ddmFormFieldValuesMap.get(
			"redirectURL");

		DDMFormFieldValue ddmFormFieldValue = ddmFormFieldValues.get(0);

		Value value = ddmFormFieldValue.getValue();

		for (Locale availableLocale : value.getAvailableLocales()) {
			String valueString = value.getString(availableLocale);

			if (Validator.isNull(valueString)) {
				continue;
			}

			String escapedRedirect = _portal.escapeRedirect(valueString);

			if (Validator.isNotNull(escapedRedirect)) {
				continue;
			}

			URI uri = _getURI(valueString);

			if (uri != null) {
				String securityMode = _redirectURLSettings.getSecurityMode(
					_portal.getCompanyId(httpServletRequest));

				String host = uri.getHost();

				if (securityMode.equals("domain")) {
					List<String> allowedDomains = Arrays.asList(
						_redirectURLSettings.getAllowedDomains(
							_portal.getCompanyId(httpServletRequest)));

					if (!allowedDomains.contains(host)) {
						throw new FormInstanceSettingsRedirectURLException(
							_getRedirectURLExceptionMessage(
								httpServletRequest, "allowed-domains", host));
					}
				}
				else if (securityMode.equals("ip")) {
					try {
						List<String> allowedIps = Arrays.asList(
							_redirectURLSettings.getAllowedIPs(
								_portal.getCompanyId(httpServletRequest)));

						InetAddress inetAddress =
							InetAddressUtil.getInetAddressByName(host);

						String hostAddress = inetAddress.getHostAddress();

						if (!allowedIps.contains(hostAddress)) {
							throw new FormInstanceSettingsRedirectURLException(
								_getRedirectURLExceptionMessage(
									httpServletRequest, "allowed-ips",
									hostAddress));
						}
					}
					catch (UnknownHostException unknownHostException) {
						if (_log.isDebugEnabled()) {
							_log.debug(unknownHostException);
						}
					}
				}
			}

			throw new FormInstanceSettingsRedirectURLException(
				_language.get(
					httpServletRequest,
					"the-specified-redirect-url-is-not-allowed"));
		}
	}

	private void _validateSettingsDDMFormValues(
			DDMFormValues settingsDDMFormValues,
			HttpServletRequest httpServletRequest, Locale locale)
		throws Exception {

		_validateExpirationDate(settingsDDMFormValues);
		_validateRedirectURL(settingsDDMFormValues, httpServletRequest);
		_validateStorageType(settingsDDMFormValues, httpServletRequest, locale);
	}

	private void _validateStorageType(
			DDMFormValues settingsDDMFormValues,
			HttpServletRequest httpServletRequest, Locale locale)
		throws Exception {

		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
			settingsDDMFormValues.getDDMFormFieldValuesMap(false);

		String storageType = _getPropertyValue(
			ddmFormFieldValuesMap, locale, "storageType");

		if (!StringUtil.equals(storageType, "object")) {
			return;
		}

		String objectDefinitionId = _getPropertyValue(
			ddmFormFieldValuesMap, locale, "objectDefinitionId");

		if (Validator.isNull(objectDefinitionId)) {
			throw new FormInstanceSettingsStorageTypeException(
				_language.get(
					httpServletRequest,
					"you-must-define-an-object-for-the-selected-storage-type"));
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SaveFormInstanceMVCCommandHelper.class);

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private RedirectURLSettings _redirectURLSettings;

}