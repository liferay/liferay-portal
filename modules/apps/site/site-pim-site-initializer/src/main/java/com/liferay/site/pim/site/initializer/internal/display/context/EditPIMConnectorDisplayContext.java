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
			"connector", _getConnectorJSONObject(values)
		).put(
			"connectors",
			JSONUtil.toJSONArray(
				_pimConnectors,
				pimConnector -> JSONUtil.put(
					"key", pimConnector.getKey()
				).put(
					"name", _getPIMConnectorName(pimConnector)
				))
		).put(
			"objectEntryId", _objectEntryId
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

	private JSONObject _getConnectorJSONObject(
		Map<String, Serializable> values) {

		if (values == null) {
			return null;
		}

		return JSONUtil.put(
			"active", GetterUtil.getBoolean(values.get("active"))
		).put(
			"apiSchema", GetterUtil.getString(values.get("apiSchema"))
		).put(
			"connectorKey", GetterUtil.getString(values.get("connectorKey"))
		).put(
			"name", GetterUtil.getString(values.get("name"))
		);
	}

	private String _getPIMConnectorName(PIMConnector pimConnector) {
		String name = pimConnector.getName(_themeDisplay.getLocale());

		if (Validator.isNotNull(name)) {
			return name;
		}

		return pimConnector.getKey();
	}

	private String _getTitle(Map<String, Serializable> values) {
		if (values == null) {
			return LanguageUtil.get(_httpServletRequest, "new-connector");
		}

		String name = GetterUtil.getString(values.get("name"));

		if (Validator.isNotNull(name)) {
			return LanguageUtil.format(
				_httpServletRequest, "edit-x", name, false);
		}

		return LanguageUtil.get(_httpServletRequest, "edit-connector");
	}

	private Map<String, Serializable> _getValues() throws Exception {
		if (_objectEntryId == 0) {
			return null;
		}

		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			_objectEntryId);

		if (objectEntry == null) {
			return null;
		}

		return _objectEntryLocalService.getValues(objectEntry);
	}

	private final HttpServletRequest _httpServletRequest;
	private final ObjectDefinition _objectDefinition;
	private final long _objectEntryId;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final List<PIMConnector> _pimConnectors;
	private final ThemeDisplay _themeDisplay;

}