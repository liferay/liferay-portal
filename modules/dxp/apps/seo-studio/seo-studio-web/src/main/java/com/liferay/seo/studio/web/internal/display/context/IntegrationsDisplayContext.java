/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.web.internal.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.model.FDSActionDropdownItemList;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.object.rest.dto.v1_0.ListEntry;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.seo.studio.web.internal.constants.SEOStudioFDSNames;

import jakarta.servlet.http.HttpServletRequest;

import java.time.Instant;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Kiana Suetani
 */
public class IntegrationsDisplayContext {

	public IntegrationsDisplayContext(
		Map<String, String> configurationURLsMap,
		HttpServletRequest httpServletRequest, Language language,
		List<ObjectEntry> seoStudioIntegrationObjectEntries,
		List<ListTypeEntry> seoStudioIntegrationTypeListTypeEntries,
		JSONArray viewsJSONArray) {

		_configurationURLsMap = configurationURLsMap;
		_httpServletRequest = httpServletRequest;
		_language = language;
		_seoStudioIntegrationObjectEntries = seoStudioIntegrationObjectEntries;
		_seoStudioIntegrationTypeListTypeEntries =
			seoStudioIntegrationTypeListTypeEntries;
		_viewsJSONArray = viewsJSONArray;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getReactData() throws Exception {
		return HashMapBuilder.<String, Object>put(
			"fdsId", SEOStudioFDSNames.INTEGRATIONS
		).put(
			"instancesURL", "/o/seo-studio/instances"
		).put(
			"integrationsURL", "/o/seo-studio/integrations"
		).put(
			"integrationTypes", _getIntegrationTypesJSONArray()
		).put(
			"items", _getItemsJSONArray()
		).put(
			"itemsActions", _getFDSActionDropdownItems()
		).put(
			"views", _viewsJSONArray
		).build();
	}

	private ListTypeEntry _fetchListTypeEntry(String key) {
		for (ListTypeEntry listTypeEntry :
				_seoStudioIntegrationTypeListTypeEntries) {

			if (Objects.equals(key, listTypeEntry.getKey())) {
				return listTypeEntry;
			}
		}

		return null;
	}

	private List<FDSActionDropdownItem> _getFDSActionDropdownItems() {
		return FDSActionDropdownItemList.of(
			new FDSActionDropdownItem(
				null, null, "edit", _language.get(_httpServletRequest, "edit"),
				null, null, null),
			new FDSActionDropdownItem(
				null, null, "remove",
				_language.get(_httpServletRequest, "remove"), null, null, null),
			new FDSActionDropdownItem(
				null, null, "validate-connection",
				_language.get(_httpServletRequest, "validate-connection"), null,
				null, null,
				HashMapBuilder.<String, Object>put(
					"stateKey", "unavailable"
				).build()));
	}

	private JSONArray _getIntegrationTypesJSONArray() throws Exception {
		Set<String> configuredKeys = new HashSet<>();

		for (ObjectEntry objectEntry : _seoStudioIntegrationObjectEntries) {
			configuredKeys.add(_getPropertyValue(objectEntry, "type"));
		}

		return JSONUtil.toJSONArray(
			_seoStudioIntegrationTypeListTypeEntries,
			listTypeEntry -> {
				String key = listTypeEntry.getKey();

				String configurationURL = _configurationURLsMap.get(key);

				if (configurationURL == null) {
					return null;
				}

				return JSONUtil.put(
					"configurationURL", configurationURL
				).put(
					"disabled", configuredKeys.contains(key)
				).put(
					"id", key
				).put(
					"name", listTypeEntry.getName(_themeDisplay.getLocale())
				);
			});
	}

	private JSONArray _getItemsJSONArray() throws Exception {
		return JSONUtil.toJSONArray(
			_seoStudioIntegrationObjectEntries,
			objectEntry -> {
				String type = _getPropertyValue(objectEntry, "type");

				ListTypeEntry listTypeEntry = _fetchListTypeEntry(type);

				if (listTypeEntry == null) {
					return null;
				}

				Date dateModified = objectEntry.getDateModified();

				Instant dateModifiedInstant = dateModified.toInstant();

				String state = _getPropertyValue(objectEntry, "state");

				return JSONUtil.put(
					"configurationURL",
					_configurationURLsMap.getOrDefault(type, StringPool.BLANK)
				).put(
					"dateModified", dateModifiedInstant.toString()
				).put(
					"id", objectEntry.getId()
				).put(
					"name", listTypeEntry.getName(_themeDisplay.getLocale())
				).put(
					"seoStudioInstanceId",
					_getPropertyValue(
						objectEntry,
						"r_seoStudioInstanceToSEOStudioIntegrations_" +
							"seoStudioInstanceId")
				).put(
					"state",
					JSONUtil.put(
						"key", state
					).put(
						"name", _language.get(_httpServletRequest, state)
					)
				).put(
					"stateKey", state
				);
			});
	}

	private String _getPropertyValue(ObjectEntry objectEntry, String key) {
		Map<String, Object> properties = objectEntry.getProperties();

		if (properties == null) {
			return null;
		}

		Object value = properties.get(key);

		if (value == null) {
			return null;
		}

		if (value instanceof ListEntry) {
			ListEntry listEntry = (ListEntry)value;

			return listEntry.getKey();
		}

		return value.toString();
	}

	private final Map<String, String> _configurationURLsMap;
	private final HttpServletRequest _httpServletRequest;
	private final Language _language;
	private final List<ObjectEntry> _seoStudioIntegrationObjectEntries;
	private final List<ListTypeEntry> _seoStudioIntegrationTypeListTypeEntries;
	private final ThemeDisplay _themeDisplay;
	private final JSONArray _viewsJSONArray;

}