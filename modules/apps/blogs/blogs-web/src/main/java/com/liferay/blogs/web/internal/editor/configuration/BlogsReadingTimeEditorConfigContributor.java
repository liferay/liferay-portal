/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.editor.configuration;

import com.liferay.blogs.constants.BlogsPortletKeys;
import com.liferay.blogs.web.internal.configuration.BlogsPortletInstanceConfiguration;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.editor.configuration.BaseEditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = {
		"editor.config.key=contentEditor", "editor.name=alloyeditor",
		"editor.name=ckeditor",
		"jakarta.portlet.name=" + BlogsPortletKeys.BLOGS,
		"jakarta.portlet.name=" + BlogsPortletKeys.BLOGS_ADMIN,
		"service.ranking:Integer=101"
	},
	service = EditorConfigContributor.class
)
public class BlogsReadingTimeEditorConfigContributor
	extends BaseEditorConfigContributor {

	@Override
	public void populateConfigJSONObject(
		JSONObject jsonObject, Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		try {
			BlogsPortletInstanceConfiguration
				blogsPortletInstanceConfiguration =
					_configurationProvider.getPortletInstanceConfiguration(
						BlogsPortletInstanceConfiguration.class, themeDisplay);

			if (!blogsPortletInstanceConfiguration.enableReadingTime()) {
				return;
			}
		}
		catch (ConfigurationException configurationException) {
			_log.error(
				"Unable to get blogs portlet instance configuration",
				configurationException);
		}

		String extraPlugins = jsonObject.getString("extraPlugins");

		if (Validator.isNotNull(extraPlugins)) {
			extraPlugins = extraPlugins + ",readingtime";
		}
		else {
			extraPlugins = "readingtime";
		}

		jsonObject.put("extraPlugins", extraPlugins);

		_readingTimeConfigContributor.populateConfigJSONObject(
			jsonObject, inputEditorTaglibAttributes, themeDisplay,
			requestBackedPortletURLFactory);

		JSONObject readingTimeJSONObject = jsonObject.getJSONObject(
			"readingTime");

		if (readingTimeJSONObject != null) {
			String namespace = GetterUtil.getString(
				inputEditorTaglibAttributes.get(
					"liferay-ui:input-editor:namespace"));

			readingTimeJSONObject.put("elementId", namespace + "readingTime");
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BlogsReadingTimeEditorConfigContributor.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference(target = "(editor.config.key=reading-time-editor-config-key)")
	private EditorConfigContributor _readingTimeConfigContributor;

}