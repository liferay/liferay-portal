/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.headless.admin.list.type.dto.v1_0.ListTypeDefinition;
import com.liferay.headless.admin.list.type.resource.v1_0.ListTypeDefinitionResource;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Verónica González
 */
public class PicklistBuilderDisplayContext {

	public PicklistBuilderDisplayContext(
		HttpServletRequest httpServletRequest, JSONFactory jsonFactory,
		ListTypeDefinitionResource.Factory listTypeDefinitionResourceFactory) {

		_httpServletRequest = httpServletRequest;
		_jsonFactory = jsonFactory;
		_listTypeDefinitionResourceFactory = listTypeDefinitionResourceFactory;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getProps() throws Exception {
		return HashMapBuilder.<String, Object>put(
			"state",
			JSONUtil.put(
				"listTypeDefinition", _getListTypeDefinitionJSONObject())
		).build();
	}

	private ListTypeDefinition _getListTypeDefinition() {
		if (_listTypeDefinition != null) {
			return _listTypeDefinition;
		}

		long listTypeDefinitionId = ParamUtil.getLong(
			_httpServletRequest, "listTypeDefinitionId");

		if (listTypeDefinitionId <= 0) {
			return null;
		}

		ListTypeDefinitionResource.Builder builder =
			_listTypeDefinitionResourceFactory.create();

		ListTypeDefinitionResource listTypeDefinitionResource = builder.user(
			_themeDisplay.getUser()
		).build();

		try {
			_listTypeDefinition =
				listTypeDefinitionResource.getListTypeDefinition(
					listTypeDefinitionId);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return _listTypeDefinition;
	}

	private JSONObject _getListTypeDefinitionJSONObject() {
		ListTypeDefinition listTypeDefinition = _getListTypeDefinition();

		if (listTypeDefinition == null) {
			return null;
		}

		try {
			return _jsonFactory.createJSONObject(listTypeDefinition.toString());
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PicklistBuilderDisplayContext.class);

	private final HttpServletRequest _httpServletRequest;
	private final JSONFactory _jsonFactory;
	private ListTypeDefinition _listTypeDefinition;
	private final ListTypeDefinitionResource.Factory
		_listTypeDefinitionResourceFactory;
	private final ThemeDisplay _themeDisplay;

}