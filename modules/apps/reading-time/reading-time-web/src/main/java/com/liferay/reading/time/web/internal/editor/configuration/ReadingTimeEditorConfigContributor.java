/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.reading.time.web.internal.editor.configuration;

import com.liferay.portal.kernel.editor.configuration.BaseEditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.reading.time.web.internal.constants.ReadingTimePortletKeys;

import jakarta.portlet.PortletURL;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = "editor.config.key=reading-time-editor-config-key",
	service = EditorConfigContributor.class
)
public class ReadingTimeEditorConfigContributor
	extends BaseEditorConfigContributor {

	@Override
	public void populateConfigJSONObject(
		JSONObject jsonObject, Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		PortletURL calculateReadingTimeURL =
			requestBackedPortletURLFactory.createResourceURL(
				ReadingTimePortletKeys.READING_TIME);

		LiferayPortletURL liferayPortletURL =
			(LiferayPortletURL)calculateReadingTimeURL;

		liferayPortletURL.setResourceID("/reading_time/calculate_reading_time");

		JSONObject readingTimeJSONObject = JSONUtil.put(
			"url", calculateReadingTimeURL.toString());

		jsonObject.put("readingTime", readingTimeJSONObject);
	}

}