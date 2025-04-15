/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.portlet.extender.internal.portlet.action;

import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderer;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderingContext;
import com.liferay.dynamic.data.mapping.form.values.factory.DDMFormValuesFactory;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldType;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.DDM;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.InputStream;
import java.io.PrintWriter;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Gustavo Mantuan
 */
public class PortletExtenderConfigurationAction
	extends DefaultConfigurationAction {

	public PortletExtenderConfigurationAction(
			DDM ddm, DDMFormRenderer ddmFormRenderer,
			DDMFormValuesFactory ddmFormValuesFactory,
			JSONObject preferencesJSONObject)
		throws PortalException {

		_ddmFormRenderer = ddmFormRenderer;
		_ddmFormValuesFactory = ddmFormValuesFactory;
		_preferencesJSONObject = preferencesJSONObject;

		_ddmForm = ddm.getDDMForm(preferencesJSONObject.toString());

		_ddmFormFieldsMap = _ddmForm.getDDMFormFieldsMap(true);

		_populateFieldNames();
	}

	@Override
	public void include(
			PortletConfig portletConfig, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		PrintWriter printWriter = httpServletResponse.getWriter();

		JSONArray fieldsJSONArray = _preferencesJSONObject.getJSONArray(
			"fields");

		printWriter.println(
			StringUtil.replace(
				_TPL_CONFIGURATION_FORM,
				new String[] {
					"[$ACTION_URL$]", "[$CONSTANTS_CMD$]",
					"[$CONSTANTS_UPDATE$]", "[$CURRENT_TIME_MILLIS$]",
					"[$DDM_FORM_HTML$]", "[$FIELDS_JSON_ARRAY$]",
					"[$PORTLET_NAMESPACE$]", "[$SAVE_LABEL$]"
				},
				new String[] {
					_getActionURL(httpServletRequest, portletDisplay),
					Constants.CMD, Constants.UPDATE,
					String.valueOf(System.currentTimeMillis()),
					_ddmFormRenderer.render(
						_ddmForm,
						_createDDMFormRenderingContext(
							httpServletRequest, httpServletResponse)),
					fieldsJSONArray.toString(), portletDisplay.getNamespace(),
					LanguageUtil.get(themeDisplay.getLocale(), "save")
				}));
	}

	@Override
	public void processAction(
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		DDMFormValues ddmFormValues = _ddmFormValuesFactory.create(
			actionRequest, _ddmForm);

		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
			ddmFormValues.getDDMFormFieldValuesMap();

		for (Map.Entry<String, List<DDMFormFieldValue>> entry :
				ddmFormFieldValuesMap.entrySet()) {

			DDMFormField ddmFormField = _ddmFormFieldsMap.get(entry.getKey());

			String ddmFormFieldType = ddmFormField.getType();

			setPreference(
				actionRequest, entry.getKey(),
				TransformUtil.transformToArray(
					entry.getValue(),
					ddmFormFieldValue -> {
						Value value = ddmFormFieldValue.getValue();

						String stringValue = value.getString(
							value.getDefaultLocale());

						if (ddmFormFieldType.equals(DDMFormFieldType.SELECT)) {
							stringValue = StringUtil.removeSubstring(
								stringValue, "[\"");
							stringValue = StringUtil.removeSubstring(
								stringValue, "\"]");
						}

						return stringValue;
					},
					String.class));
		}

		super.processAction(portletConfig, actionRequest, actionResponse);
	}

	private static String _loadTemplate(String name) {
		try (InputStream inputStream =
				PortletExtenderConfigurationAction.class.getResourceAsStream(
					"dependencies/" + name)) {

			return StringUtil.read(inputStream);
		}
		catch (Exception exception) {
			_log.error("Unable to read template " + name, exception);
		}

		return StringPool.BLANK;
	}

	private DDMFormRenderingContext _createDDMFormRenderingContext(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		DDMFormRenderingContext ddmFormRenderingContext =
			new DDMFormRenderingContext();

		ddmFormRenderingContext.setHttpServletRequest(httpServletRequest);
		ddmFormRenderingContext.setHttpServletResponse(httpServletResponse);

		Set<Locale> availableLocales = _ddmForm.getAvailableLocales();

		Locale locale = _ddmForm.getDefaultLocale();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (availableLocales.contains(themeDisplay.getLocale())) {
			locale = themeDisplay.getLocale();
		}

		ddmFormRenderingContext.setLocale(locale);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		ddmFormRenderingContext.setPortletNamespace(
			portletDisplay.getNamespace());

		ddmFormRenderingContext.setReadOnly(false);

		_setDDMFormValues(ddmFormRenderingContext, themeDisplay);

		return ddmFormRenderingContext;
	}

	private LocalizedValue _createLocalizedValue(
		String fieldName, String value, Locale locale) {

		LocalizedValue localizedValue = new LocalizedValue(locale);

		DDMFormField ddmFormField = _ddmFormFieldsMap.get(fieldName);

		String ddmFormFieldType = ddmFormField.getType();

		if (ddmFormFieldType.equals(DDMFormFieldType.SELECT)) {
			localizedValue.addString(locale, "[\"" + value + "\"]");
		}
		else {
			localizedValue.addString(locale, value);
		}

		return localizedValue;
	}

	private String _getActionURL(
			HttpServletRequest httpServletRequest,
			PortletDisplay portletDisplay)
		throws Exception {

		return PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				httpServletRequest, portletDisplay.getPortletName(),
				PortletRequest.ACTION_PHASE)
		).setActionName(
			"editConfiguration"
		).setMVCPath(
			"/edit_configuration.jsp"
		).setPortletResource(
			portletDisplay.getPortletResource()
		).setParameter(
			"p_auth", AuthTokenUtil.getToken(httpServletRequest)
		).setParameter(
			"portletConfiguration", true
		).setParameter(
			"previewWidth", StringPool.BLANK
		).setParameter(
			"returnToFullPageURL", "/"
		).setParameter(
			"settingsScope", "portletInstance"
		).setPortletMode(
			PortletMode.VIEW
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	private void _populateFieldNames() {
		JSONArray fieldsJSONArray = _preferencesJSONObject.getJSONArray(
			"fields");

		for (int i = 0; i < fieldsJSONArray.length(); i++) {
			JSONObject fieldJSONObject = fieldsJSONArray.getJSONObject(i);

			_fieldNames.add(fieldJSONObject.getString("name"));
		}
	}

	private void _setDDMFormValues(
			DDMFormRenderingContext ddmFormRenderingContext,
			ThemeDisplay themeDisplay)
		throws Exception {

		DDMFormValues ddmFormValues = new DDMFormValues(_ddmForm);

		Locale locale = _ddmForm.getDefaultLocale();

		ddmFormValues.addAvailableLocale(locale);
		ddmFormValues.setDefaultLocale(locale);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.getExistingPortletSetup(
				themeDisplay.getLayout(), portletDisplay.getPortletResource());

		Map<String, String[]> portletPreferencesMap =
			portletPreferences.getMap();

		for (Map.Entry<String, String[]> entry :
				portletPreferencesMap.entrySet()) {

			String fieldName = entry.getKey();

			if (!_ddmFormFieldsMap.containsKey(fieldName)) {
				continue;
			}

			for (String value : entry.getValue()) {
				DDMFormFieldValue ddmFormFieldValue = new DDMFormFieldValue();

				ddmFormFieldValue.setName(fieldName);
				ddmFormFieldValue.setValue(
					_createLocalizedValue(fieldName, value, locale));

				ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);
			}
		}

		ddmFormRenderingContext.setDDMFormValues(ddmFormValues);
	}

	private static final String _TPL_CONFIGURATION_FORM;

	private static final Log _log = LogFactoryUtil.getLog(
		PortletExtenderConfigurationAction.class);

	static {
		_TPL_CONFIGURATION_FORM = _loadTemplate("configuration_form.html.tpl");
	}

	private final DDMForm _ddmForm;
	private final Map<String, DDMFormField> _ddmFormFieldsMap;
	private final DDMFormRenderer _ddmFormRenderer;
	private final DDMFormValuesFactory _ddmFormValuesFactory;
	private final Set<String> _fieldNames = new HashSet<>();
	private final JSONObject _preferencesJSONObject;

}