/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.editor.ckeditor5.sample.web.internal.editor.configuration;

import com.liferay.frontend.editor.ckeditor5.sample.web.internal.constants.CKEditor5SamplePortletKeys;
import com.liferay.portal.kernel.editor.configuration.BaseEditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

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

		jsonObject.put(
			"licenseKey", licenseKey
		).put(
			"showPasteFromOfficeEnhanced", true
		).put(
			"showSourceEditingEnhanced", true
		);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ReactCETPremiumEditorConfigContributor.class);

}