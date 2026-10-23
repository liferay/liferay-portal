/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.editor.ckeditor5.sample.web.internal.editor.configuration;

import com.liferay.frontend.editor.ckeditor5.sample.web.internal.constants.CKEditor5SamplePortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.editor.configuration.BaseEditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.Validator;

import java.nio.charset.StandardCharsets;

import java.util.Base64;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Miguel Arroyo
 */
@Component(
	property = {
		"editor.config.key=sampleReactCETPremiumEditor",
		"jakarta.portlet.name=" + CKEditor5SamplePortletKeys.CKEDITOR_5_SAMPLE
	},
	service = EditorConfigContributor.class
)
public class ReactCETPremiumEditorConfigContributor
	extends BaseEditorConfigContributor {

	@Override
	public void populateConfigJSONObject(
		JSONObject jsonObject, Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		String licenseKey = SystemProperties.get(
			"env.CKEDITOR_DXP_LICENSE_KEY");

		if (Validator.isNull(licenseKey)) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"The premium CKEditor 5 plugins are not entitled for " +
						"this editor because no license key is set");
			}

			return;
		}

		JSONArray featuresJSONArray = _getFeaturesJSONArray(licenseKey);

		jsonObject.put(
			"licenseKey", licenseKey
		).put(
			"showPasteFromOfficeEnhanced",
			JSONUtil.hasValue(featuresJSONArray, "POE")
		).put(
			"showSourceEditingEnhanced",
			JSONUtil.hasValue(featuresJSONArray, "SEE")
		);
	}

	private JSONArray _getFeaturesJSONArray(String licenseKey) {
		String[] parts = licenseKey.split("\\.");

		if (parts.length != 3) {
			return JSONUtil.putAll();
		}

		try {
			String payload = parts[1];

			int paddingLength = (4 - (payload.length() % 4)) % 4;

			Base64.Decoder decoder = Base64.getUrlDecoder();

			JSONObject payloadJSONObject = _jsonFactory.createJSONObject(
				new String(
					decoder.decode(
						payload.concat(StringPool.EQUAL.repeat(paddingLength))),
					StandardCharsets.UTF_8));

			JSONArray featuresJSONArray = payloadJSONObject.getJSONArray(
				"features");

			if (featuresJSONArray == null) {
				return JSONUtil.putAll();
			}

			return featuresJSONArray;
		}
		catch (IllegalArgumentException | JSONException exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to parse the CKEditor 5 license key", exception);
			}

			return JSONUtil.putAll();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ReactCETPremiumEditorConfigContributor.class);

	@Reference
	private JSONFactory _jsonFactory;

}