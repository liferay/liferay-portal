/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.display.context;

import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionServiceUtil;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.pim.site.initializer.connector.PIMConnector;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorChannelField;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorRegistry;
import com.liferay.site.pim.site.initializer.constants.PIMObjectFolderConstants;
import com.liferay.site.pim.site.initializer.internal.util.PIMConnectorFieldMappingsUtil;
import com.liferay.site.pim.site.initializer.internal.util.PIMURLUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Andrea Sbarra
 * @author Stefano Motta
 */
public class EditPIMConnectorFieldMappingsDisplayContext {

	public EditPIMConnectorFieldMappingsDisplayContext(
		HttpServletRequest httpServletRequest,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectFieldLocalService objectFieldLocalService,
		PIMConnectorRegistry pimConnectorRegistry) {

		_httpServletRequest = httpServletRequest;
		_objectEntryLocalService = objectEntryLocalService;
		_objectFieldLocalService = objectFieldLocalService;
		_pimConnectorRegistry = pimConnectorRegistry;

		_channelField = ParamUtil.getString(httpServletRequest, "channelField");
		_objectEntryId = ParamUtil.getLong(httpServletRequest, "objectEntryId");
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getReactData() throws Exception {
		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			_objectEntryId);

		String channelFieldLabel = _getChannelFieldLabel(objectEntry);

		return HashMapBuilder.<String, Object>put(
			"apiURL",
			PIMConnectorFieldMappingsUtil.getAPIURL(
				_themeDisplay.getCompanyId())
		).put(
			"backURL",
			PIMURLUtil.getBackURL(
				PIMURLUtil.getFieldMappingsURL(
					String.valueOf(_objectEntryId), _themeDisplay),
				_httpServletRequest)
		).put(
			"channelField", _channelField
		).put(
			"channelFieldLabel", channelFieldLabel
		).put(
			"fieldMappings", _getFieldMappingsJSONArray(objectEntry)
		).put(
			"objectDefinitions", _getObjectDefinitionsJSONArray()
		).put(
			"objectEntryId", _objectEntryId
		).put(
			"objectRelationshipObjectFieldName",
			_getObjectRelationshipObjectFieldName(objectEntry)
		).put(
			"spritemap", _themeDisplay.getPathThemeSpritemap()
		).put(
			"title",
			LanguageUtil.format(
				_httpServletRequest, "edit-x", channelFieldLabel, false)
		).build();
	}

	private String _getChannelFieldLabel(ObjectEntry objectEntry) {
		if (objectEntry == null) {
			return _channelField;
		}

		PIMConnector pimConnector = _pimConnectorRegistry.getPIMConnector(
			MapUtil.getString(objectEntry.getValues(), "key"));

		if (pimConnector == null) {
			return _channelField;
		}

		for (PIMConnectorChannelField pimConnectorChannelField :
				pimConnector.getPIMConnectorChannelFields(
					_themeDisplay.getLocale())) {

			if (Objects.equals(
					pimConnectorChannelField.getName(), _channelField)) {

				return pimConnectorChannelField.getLabel();
			}
		}

		return _channelField;
	}

	private JSONArray _getFieldMappingsJSONArray(ObjectEntry objectEntry)
		throws Exception {

		if (objectEntry == null) {
			return JSONUtil.putAll();
		}

		List<ObjectEntry> objectEntries = ListUtil.filter(
			PIMConnectorFieldMappingsUtil.getObjectEntries(objectEntry),
			curObjectEntry -> Objects.equals(
				MapUtil.getString(
					curObjectEntry.getValues(), "channelFieldName"),
				_channelField));

		return JSONUtil.toJSONArray(
			objectEntries,
			curObjectEntry -> {
				Map<String, Serializable> values = curObjectEntry.getValues();

				return JSONUtil.put(
					"id", curObjectEntry.getObjectEntryId()
				).put(
					"sourceClassName",
					MapUtil.getString(values, "sourceClassName")
				).put(
					"sourceFieldName",
					MapUtil.getString(values, "sourceFieldName")
				).put(
					"type", MapUtil.getString(values, "type")
				).put(
					"value", MapUtil.getString(values, "value")
				);
			});
	}

	private JSONArray _getObjectDefinitionsJSONArray() throws Exception {
		return JSONUtil.toJSONArray(
			ObjectDefinitionServiceUtil.getCMSObjectDefinitions(
				_themeDisplay.getCompanyId(),
				new String[] {
					PIMObjectFolderConstants.
						EXTERNAL_REFERENCE_CODE_PRODUCT_TYPES
				}),
			objectDefinition -> JSONUtil.put(
				"className", objectDefinition.getClassName()
			).put(
				"label", objectDefinition.getLabel(_themeDisplay.getLocale())
			).put(
				"objectFields",
				JSONUtil.toJSONArray(
					_objectFieldLocalService.getObjectFields(
						objectDefinition.getObjectDefinitionId()),
					objectField -> JSONUtil.put(
						"label", objectField.getLabel(_themeDisplay.getLocale())
					).put(
						"name", objectField.getName()
					))
			));
	}

	private String _getObjectRelationshipObjectFieldName(
		ObjectEntry objectEntry) {

		if (objectEntry == null) {
			return StringPool.BLANK;
		}

		ObjectRelationship objectRelationship =
			PIMConnectorFieldMappingsUtil.fetchObjectRelationship(
				objectEntry.getObjectDefinitionId());

		if (objectRelationship == null) {
			return StringPool.BLANK;
		}

		ObjectField objectField = _objectFieldLocalService.fetchObjectField(
			objectRelationship.getObjectFieldId2());

		if (objectField == null) {
			return StringPool.BLANK;
		}

		return objectField.getName();
	}

	private final String _channelField;
	private final HttpServletRequest _httpServletRequest;
	private final long _objectEntryId;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final ObjectFieldLocalService _objectFieldLocalService;
	private final PIMConnectorRegistry _pimConnectorRegistry;
	private final ThemeDisplay _themeDisplay;

}