/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.display.context;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorRegistry;
import com.liferay.site.pim.site.initializer.internal.util.PIMConnectorFieldMappingsUtil;
import com.liferay.site.pim.site.initializer.internal.util.PIMURLUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Andrea Sbarra
 * @author Stefano Motta
 */
public class PIMConnectorFieldMappingsDisplayContext {

	public PIMConnectorFieldMappingsDisplayContext(
		HttpServletRequest httpServletRequest,
		ObjectDefinition objectDefinition,
		ObjectEntryLocalService objectEntryLocalService,
		PIMConnectorRegistry pimConnectorRegistry) {

		_httpServletRequest = httpServletRequest;
		_objectDefinition = objectDefinition;
		_objectEntryLocalService = objectEntryLocalService;
		_pimConnectorRegistry = pimConnectorRegistry;

		_objectEntryId = ParamUtil.getLong(httpServletRequest, "objectEntryId");
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getBreadcrumbProps() throws PortalException {
		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			_objectEntryId);

		boolean mappingComplete = _isMappingComplete(objectEntry);
		String name = _getName(objectEntry);

		return HashMapBuilder.<String, Object>put(
			"actionItems",
			JSONUtil.putAll(
				JSONUtil.put(
					"href",
					PIMURLUtil.getEditConnectorURL(
						String.valueOf(_objectEntryId), _themeDisplay)
				).put(
					"label", LanguageUtil.get(_httpServletRequest, "edit")
				).put(
					"symbolLeft", "pencil"
				)
			).put(
				() -> {
					if (!mappingComplete ||
						!MapUtil.getBoolean(
							objectEntry.getValues(), "active")) {

						return null;
					}

					return JSONUtil.put(
						"href",
						PIMURLUtil.getConnectorExecuteURL(
							String.valueOf(_objectEntryId))
					).put(
						"id", "execute"
					).put(
						"label",
						LanguageUtil.get(_httpServletRequest, "execute")
					).put(
						"successMessage",
						LanguageUtil.get(
							_httpServletRequest,
							"execution-has-started-successfully-and-will-" +
								"continue-in-the-background")
					).put(
						"symbolLeft", "play"
					).put(
						"target", "asyncPost"
					);
				}
			).put(
				JSONUtil.put(
					"href",
					PIMURLUtil.getConnectorScheduleURL(
						String.valueOf(_objectEntryId), _themeDisplay)
				).put(
					"label", LanguageUtil.get(_httpServletRequest, "schedule")
				).put(
					"symbolLeft", "date-time"
				)
			).put(
				JSONUtil.put(
					"className", "text-danger"
				).put(
					"confirmationMessage",
					LanguageUtil.get(
						_httpServletRequest,
						"the-connector-and-all-its-field-mappings-will-be-" +
							"deleted.-this-action-cannot-be-undone")
				).put(
					"confirmationTitle",
					LanguageUtil.format(_httpServletRequest, "delete-x", name)
				).put(
					"href",
					() -> {
						if (_objectDefinition == null) {
							return StringPool.BLANK;
						}

						return StringBundler.concat(
							"/o", _objectDefinition.getRESTContextPath(),
							StringPool.SLASH, _objectEntryId);
					}
				).put(
					"label", LanguageUtil.get(_httpServletRequest, "delete")
				).put(
					"redirect", PIMURLUtil.getConnectorsURL(_themeDisplay)
				).put(
					"symbolLeft", "trash"
				).put(
					"target", "asyncDelete"
				)
			)
		).put(
			"breadcrumbItems",
			JSONUtil.putAll(
				JSONUtil.put(
					"active", false
				).put(
					"href", PIMURLUtil.getConnectorsURL(_themeDisplay)
				).put(
					"label", LanguageUtil.get(_httpServletRequest, "connectors")
				),
				JSONUtil.put(
					"active", true
				).put(
					"href", StringPool.BLANK
				).put(
					"label", name
				))
		).put(
			"hideSpace", true
		).put(
			"mappingComplete", mappingComplete
		).put(
			"size", "lg"
		).build();
	}

	public Map<String, String> getContextParams() {
		return HashMapBuilder.put(
			"editFieldMappingsURL",
			URLCodec.encodeURL(
				PIMURLUtil.getEditFieldMappingsURL(
					String.valueOf(_objectEntryId), _themeDisplay))
		).put(
			"objectEntryId", String.valueOf(_objectEntryId)
		).build();
	}

	public Map<String, Object> getEmptyState() {
		return HashMapBuilder.<String, Object>put(
			"description",
			LanguageUtil.get(
				_httpServletRequest,
				"this-connector-does-not-declare-any-fields")
		).put(
			"title",
			LanguageUtil.get(_httpServletRequest, "no-fields-were-found")
		).build();
	}

	private String _getName(ObjectEntry objectEntry) {
		if (objectEntry == null) {
			return LanguageUtil.get(_httpServletRequest, "field-mappings");
		}

		String name = MapUtil.getString(objectEntry.getValues(), "name");

		if (Validator.isNotNull(name)) {
			return name;
		}

		return LanguageUtil.get(_httpServletRequest, "field-mappings");
	}

	private boolean _isMappingComplete(ObjectEntry objectEntry)
		throws PortalException {

		if (objectEntry == null) {
			return false;
		}

		return PIMConnectorFieldMappingsUtil.isMappingComplete(
			objectEntry,
			_pimConnectorRegistry.getPIMConnector(
				MapUtil.getString(objectEntry.getValues(), "key")));
	}

	private final HttpServletRequest _httpServletRequest;
	private final ObjectDefinition _objectDefinition;
	private final long _objectEntryId;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final PIMConnectorRegistry _pimConnectorRegistry;
	private final ThemeDisplay _themeDisplay;

}