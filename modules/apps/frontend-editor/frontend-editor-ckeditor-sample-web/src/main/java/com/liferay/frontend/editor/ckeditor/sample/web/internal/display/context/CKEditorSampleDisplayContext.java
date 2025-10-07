/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.editor.ckeditor.sample.web.internal.display.context;

import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.type.EditorConfigContributorCET;
import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.editor.configuration.EditorConfiguration;
import com.liferay.portal.kernel.editor.configuration.EditorConfigurationFactoryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.portlet.RenderRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Marko Cikos
 */
public class CKEditorSampleDisplayContext {

	public CKEditorSampleDisplayContext(
		CETManager cetManager, RenderRequest renderRequest) {

		_cetManager = cetManager;

		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Object getCKEditor5ClassicEditorConfig() throws Exception {
		EditorConfiguration editorConfiguration =
			EditorConfigurationFactoryUtil.getEditorConfiguration(
				StringPool.BLANK, StringPool.BLANK, "ckeditor5_classic",
				new HashMap<String, Object>(), _themeDisplay,
				RequestBackedPortletURLFactoryUtil.create(
					_themeDisplay.getRequest()));

		Map<String, Object> data = editorConfiguration.getData();

		return data.get("editorConfig");
	}

	public JSONArray getEditorTransformerURLsJSONArray() throws Exception {
		return JSONUtil.toJSONArray(
			_cetManager.getCETs(
				_themeDisplay.getCompanyId(), null,
				ClientExtensionEntryConstants.TYPE_EDITOR_CONFIG_CONTRIBUTOR,
				Pagination.of(QueryUtil.ALL_POS, QueryUtil.ALL_POS), null),
			cet -> {
				EditorConfigContributorCET editorConfigContributorCET =
					(EditorConfigContributorCET)cet;

				if (StringUtil.matches(
						editorConfigContributorCET.getEditorConfigKeys(),
						"sampleReactClassicEditor") ||
					StringUtil.matches(
						editorConfigContributorCET.getEditorConfigKeys(),
						"sampleReactCKEditor5ClassicEditor")) {

					return editorConfigContributorCET.getURL();
				}

				return null;
			});
	}

	private final CETManager _cetManager;
	private final ThemeDisplay _themeDisplay;

}