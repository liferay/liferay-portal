/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.display.context;

import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Andrea Sbarra
 */
public class PIMConnectorFieldMappingDisplayContext {

	public PIMConnectorFieldMappingDisplayContext(
		HttpServletRequest httpServletRequest,
		ObjectEntryLocalService objectEntryLocalService) {

		_httpServletRequest = httpServletRequest;
		_objectEntryLocalService = objectEntryLocalService;

		_objectEntryId = ParamUtil.getLong(httpServletRequest, "objectEntryId");
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getBreadcrumbProps() {
		return HashMapBuilder.<String, Object>put(
			"actionItems",
			JSONUtil.putAll(
				JSONUtil.put(
					"href", _getEditConnectorURL()
				).put(
					"label", LanguageUtil.get(_httpServletRequest, "edit")
				))
		).put(
			"breadcrumbItems",
			JSONUtil.putAll(
				JSONUtil.put(
					"active", false
				).put(
					"href", _getSiteURL("/connectors")
				).put(
					"label", LanguageUtil.get(_httpServletRequest, "connectors")
				),
				JSONUtil.put(
					"active", true
				).put(
					"href", StringPool.BLANK
				).put(
					"label", _getName()
				))
		).put(
			"hideSpace", true
		).put(
			"size", "lg"
		).build();
	}

	public Map<String, String> getContextParams() {
		return HashMapBuilder.put(
			"mapChannelFieldURL", URLCodec.encodeURL(_getMapChannelFieldURL())
		).put(
			"objectEntryId", String.valueOf(_objectEntryId)
		).build();
	}

	public Map<String, Object> getEmptyState() {
		return HashMapBuilder.<String, Object>put(
			"description",
			LanguageUtil.get(
				_httpServletRequest,
				"this-connector-does-not-declare-any-field")
		).put(
			"title",
			LanguageUtil.get(_httpServletRequest, "no-fields-were-found")
		).build();
	}

	private String _getEditConnectorURL() {
		return StringBundler.concat(
			_getSiteURL("/edit-connector"), "?backURL=",
			URLCodec.encodeURL(_themeDisplay.getURLCurrent()),
			"&objectEntryId=", _objectEntryId);
	}

	private String _getMapChannelFieldURL() {
		return StringBundler.concat(
			_getSiteURL("/map-channel-field"), "?objectEntryId=",
			_objectEntryId);
	}

	private String _getName() {
		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			_objectEntryId);

		if (objectEntry == null) {
			return LanguageUtil.get(_httpServletRequest, "field-mapping");
		}

		String name = MapUtil.getString(objectEntry.getValues(), "name");

		if (Validator.isNotNull(name)) {
			return name;
		}

		return LanguageUtil.get(_httpServletRequest, "field-mapping");
	}

	private String _getSiteURL(String friendlyURL) {
		Group group = _themeDisplay.getScopeGroup();

		return StringBundler.concat(
			_themeDisplay.getPathFriendlyURLPublic(), group.getFriendlyURL(),
			friendlyURL);
	}

	private final HttpServletRequest _httpServletRequest;
	private final long _objectEntryId;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final ThemeDisplay _themeDisplay;

}