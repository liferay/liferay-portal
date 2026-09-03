/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.editor.ckeditor5.sample.web.internal.display.context;

import com.liferay.frontend.editor.ckeditor5.sample.web.internal.constants.CKEditor5SamplePortletKeys;
import com.liferay.portal.kernel.editor.configuration.EditorConfiguration;
import com.liferay.portal.kernel.editor.configuration.EditorConfigurationFactoryUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Miguel Arroyo
 */
public class CKEditor5SampleDisplayContext {

	public CKEditor5SampleDisplayContext(RenderRequest renderRequest) {
		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Object getCKEditor5ClassicEditorConfig(String editorConfigKey)
		throws Exception {

		EditorConfiguration editorConfiguration =
			EditorConfigurationFactoryUtil.getEditorConfiguration(
				CKEditor5SamplePortletKeys.CKEDITOR_5_SAMPLE, editorConfigKey,
				"ckeditor5_classic", new HashMap<String, Object>(),
				_themeDisplay,
				RequestBackedPortletURLFactoryUtil.create(
					_themeDisplay.getRequest()));

		Map<String, Object> data = editorConfiguration.getData();

		return data.get("editorConfig");
	}

	private final ThemeDisplay _themeDisplay;

}