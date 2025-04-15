/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.web.internal.portlet.editor.config.contributor;

import com.liferay.client.extension.web.internal.type.deployer.Registrable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Validator;

import java.util.Dictionary;
import java.util.Map;

/**
 * @author Daniel Sanz
 */
public class CETEditorConfigContributor
	implements EditorConfigContributor, Registrable {

	public CETEditorConfigContributor(
		String editorConfigKeys, String editorNames, String portletNames,
		String url) {

		_editorConfigKeys = editorConfigKeys;
		_editorNames = editorNames;
		_portletNames = portletNames;
		_url = url;
	}

	@Override
	public Dictionary<String, Object> getDictionary() {
		return HashMapDictionaryBuilder.<String, Object>put(
			"editor.config.key",
			() -> {
				if (Validator.isNotNull(_editorConfigKeys)) {
					return _editorConfigKeys.split(StringPool.NEW_LINE);
				}

				return null;
			}
		).put(
			"editor.name",
			() -> {
				if (Validator.isNotNull(_editorNames)) {
					return _editorNames.split(StringPool.NEW_LINE);
				}

				return null;
			}
		).put(
			"jakarta.portlet.name",
			() -> {
				if (Validator.isNotNull(_portletNames)) {
					return _portletNames.split(StringPool.NEW_LINE);
				}

				return null;
			}
		).build();
	}

	@Override
	public void populateConfigJSONObject(
		JSONObject jsonObject, Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		JSONArray jsonArray = jsonObject.getJSONArray("editorTransformerURLs");

		if (jsonArray == null) {
			jsonArray = JSONFactoryUtil.createJSONArray();

			jsonObject.put("editorTransformerURLs", jsonArray);
		}

		jsonArray.put(_url);
	}

	private final String _editorConfigKeys;
	private final String _editorNames;
	private final String _portletNames;
	private final String _url;

}