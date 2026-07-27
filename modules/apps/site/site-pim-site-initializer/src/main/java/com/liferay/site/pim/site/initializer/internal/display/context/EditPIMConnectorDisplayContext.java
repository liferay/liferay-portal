/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.display.context;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.pim.site.initializer.connector.PIMConnector;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * @author Andrea Sbarra
 */
public class EditPIMConnectorDisplayContext {

	public EditPIMConnectorDisplayContext(
		HttpServletRequest httpServletRequest,
		ObjectDefinition objectDefinition,
		ObjectEntryLocalService objectEntryLocalService,
		List<PIMConnector> pimConnectors) {

		_httpServletRequest = httpServletRequest;
		_objectDefinition = objectDefinition;
		_objectEntryLocalService = objectEntryLocalService;
		_pimConnectors = pimConnectors;

		_objectEntryId = ParamUtil.getLong(httpServletRequest, "objectEntryId");
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getReactData() throws Exception {
		Map<String, Serializable> values = _getValues();

		return HashMapBuilder.<String, Object>put(
			"apiURL", _getAPIURL()
		).put(
			"backURL", _getBackURL()
		).put(
			"objectEntryId", _objectEntryId
		).put(
			"pimConnector", _getPIMConnectorJSONObject(values)
		).put(
			"pimConnectors",
			JSONUtil.toJSONArray(
				_pimConnectors,
				pimConnector -> JSONUtil.put(
					"key", pimConnector.getKey()
				).put(
					"name",
					GetterUtil.getString(
						pimConnector.getName(_themeDisplay.getLocale()),
						pimConnector.getKey())
				))
		).put(
			"title", _getTitle(values)
		).build();
	}

	private String _getAPIURL() {
		if (_objectDefinition == null) {
			return StringPool.BLANK;
		}

		return "/o" + _objectDefinition.getRESTContextPath();
	}

	private String _getBackURL() {
		String backURL = ParamUtil.getString(_httpServletRequest, "backURL");

		if (Validator.isNotNull(backURL)) {
			return backURL;
		}

		return _themeDisplay.getURLCurrent();
	}

	private JSONObject _getPIMConnectorJSONObject(
		Map<String, Serializable> values) {

		if (values == null) {
			return null;
		}

		return JSONUtil.put(
			"active", MapUtil.getBoolean(values, "active")
		).put(
			"apiSchema", MapUtil.getString(values, "apiSchema")
		).put(
			"key", MapUtil.getString(values, "key")
		).put(
			"name", MapUtil.getString(values, "name")
		);
	}

	private String _getTitle(Map<String, Serializable> values) {
		if (values == null) {
			return LanguageUtil.get(_httpServletRequest, "new-connector");
		}

		String name = MapUtil.getString(values, "name");

		if (Validator.isNotNull(name)) {
			return LanguageUtil.format(
				_httpServletRequest, "edit-x", name, false);
		}

		return LanguageUtil.get(_httpServletRequest, "edit-connector");
	}

	private Map<String, Serializable> _getValues() {
		if (_objectEntryId == 0) {
			return null;
		}

		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			_objectEntryId);

		if (objectEntry == null) {
			return null;
		}

		return objectEntry.getValues();
	}

	private final HttpServletRequest _httpServletRequest;
	private final ObjectDefinition _objectDefinition;
	private final long _objectEntryId;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final List<PIMConnector> _pimConnectors;
	private final ThemeDisplay _themeDisplay;

}