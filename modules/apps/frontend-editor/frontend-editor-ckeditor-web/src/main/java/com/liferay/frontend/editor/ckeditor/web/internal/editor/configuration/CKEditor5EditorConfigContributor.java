/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.editor.ckeditor.web.internal.editor.configuration;

import com.liferay.frontend.editor.ckeditor.web.internal.configuration.CKEditor5Configuration;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.editor.configuration.BaseEditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;

import java.nio.charset.StandardCharsets;

import java.util.Base64;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marko Cikos
 */
@Component(
	configurationPid = "com.liferay.frontend.editor.ckeditor.web.internal.configuration.CKEditor5Configuration",
	property = {
		"editor.name=ckeditor5_balloon", "editor.name=ckeditor5_classic"
	},
	service = EditorConfigContributor.class
)
public class CKEditor5EditorConfigContributor
	extends BaseEditorConfigContributor {

	@Override
	public void populateConfigJSONObject(
		JSONObject jsonObject, Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		String namespace = GetterUtil.getString(
			inputEditorTaglibAttributes.get(
				"liferay-ui:input-editor:namespace"));
		String name = GetterUtil.getString(
			inputEditorTaglibAttributes.get("liferay-ui:input-editor:name"));
		String placeholder = LanguageUtil.format(
			themeDisplay.getLocale(), "start-writing-content", false);
		String licenseKey = _ckEditor5Configuration.licenseKey();

		jsonObject.put(
			"editorType", "ckeditor5"
		).put(
			"itemSelectorEventName", namespace + name + "selectItem"
		).put(
			"licenseKey", licenseKey
		).put(
			"placeholder", placeholder
		).put(
			"preset", "advanced"
		).put(
			"showPasteFromOfficeEnhanced",
			ReleaseInfo.isDXP() && _isLicenseKeyValid(licenseKey)
		);
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_ckEditor5Configuration = ConfigurableUtil.createConfigurable(
			CKEditor5Configuration.class, properties);
	}

	private boolean _isLicenseKeyValid(String licenseKey) {
		if (Validator.isNull(licenseKey)) {
			return false;
		}

		String[] parts = licenseKey.split("\\.");

		if (parts.length != 3) {
			return false;
		}

		try {
			String payload = parts[1];

			int paddingLength = (4 - (payload.length() % 4)) % 4;

			JSONObject payloadJSONObject = _jsonFactory.createJSONObject(
				new String(
					Base64.getUrlDecoder(
					).decode(
						payload.concat(StringPool.EQUAL.repeat(paddingLength))
					),
					StandardCharsets.UTF_8));

			long expirationTime = payloadJSONObject.getLong("exp", 0);

			if (expirationTime > (System.currentTimeMillis() / Time.SECOND)) {
				return true;
			}

			return false;
		}
		catch (IllegalArgumentException | JSONException exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to parse the CKEditor 5 license key", exception);
			}

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CKEditor5EditorConfigContributor.class);

	private volatile CKEditor5Configuration _ckEditor5Configuration;

	@Reference
	private JSONFactory _jsonFactory;

}