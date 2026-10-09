/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portlet.asset.service.permission.AssetCategoriesPermission;
import com.liferay.site.cms.site.initializer.internal.util.ActionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Christian Dorado
 */
public class ViewHomeQuickActionsDisplayContext {

	public ViewHomeQuickActionsDisplayContext(
		GroupLocalService groupLocalService,
		ObjectDefinitionService objectDefinitionService,
		ThemeDisplay themeDisplay) {

		_groupLocalService = groupLocalService;
		_objectDefinitionService = objectDefinitionService;
		_themeDisplay = themeDisplay;
	}

	public Map<String, Object> getProps() throws Exception {
		return HashMapBuilder.<String, Object>put(
			"quickActions", _getQuickActions()
		).build();
	}

	public boolean hasQuickActions() throws Exception {
		return ListUtil.isNotEmpty(_getQuickActions());
	}

	private Map<String, Object> _createQuickAction(
		JSONArray depotEntriesJSONArray, String icon,
		ObjectDefinition objectDefinition) {

		return HashMapBuilder.<String, Object>put(
			"action", "createAsset"
		).put(
			"assetLibraries", depotEntriesJSONArray
		).put(
			"icon", icon
		).put(
			"redirect",
			StringBundler.concat(
				_themeDisplay.getPortalURL(), _themeDisplay.getPathMain(),
				GroupConstants.CMS_FRIENDLY_URL,
				"/add_structured_content_item?objectDefinitionId=",
				objectDefinition.getObjectDefinitionId(),
				"&objectEntryFolderExternalReferenceCode=",
				_getObjectEntryFolderExternalReferenceCode(objectDefinition),
				"&plid=", _themeDisplay.getPlid(), "&redirect=",
				_themeDisplay.getURLCurrent())
		).put(
			"title", objectDefinition.getLabel(_themeDisplay.getLocale())
		).build();
	}

	private JSONArray _getDepotEntriesJSONArray(List<Long> groupIds) {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		for (Long groupId : groupIds) {
			JSONObject jsonObject = _getJSONObject(groupId);

			if (jsonObject == null) {
				continue;
			}

			jsonArray.put(jsonObject);
		}

		return jsonArray;
	}

	private JSONObject _getJSONObject(long groupId) {
		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return null;
		}

		return JSONUtil.put(
			"groupId", group.getGroupId()
		).put(
			"name", group.getName(_themeDisplay.getLocale())
		);
	}

	private String _getObjectEntryFolderExternalReferenceCode(
		ObjectDefinition objectDefinition) {

		if (Objects.equals(
				objectDefinition.getObjectFolderExternalReferenceCode(),
				ObjectFolderConstants.
					EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES)) {

			return ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS;
		}

		if (Objects.equals(
				objectDefinition.getObjectFolderExternalReferenceCode(),
				ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES)) {

			return ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES;
		}

		return null;
	}

	private List<Map<String, Object>> _getQuickActions() throws Exception {
		if (_quickActions != null) {
			return _quickActions;
		}

		_quickActions = new ArrayList<>();

		List<Long> depotEntryGroupIds =
			SectionDisplayContextUtil.getDepotEntryGroupIds(
				ActionKeys.ADD_ENTRY,
				SectionDisplayContextUtil.getObjectEntryFolderIdsMap(
					_themeDisplay.getCompanyId(), null,
					_themeDisplay.getUserId()),
				_themeDisplay);

		if (ListUtil.isNotEmpty(depotEntryGroupIds)) {
			List<ObjectDefinition> objectDefinitions =
				_objectDefinitionService.getCMSObjectDefinitions(
					_themeDisplay.getCompanyId(),
					new String[] {
						ObjectFolderConstants.
							EXTERNAL_REFERENCE_CODE_FILE_TYPES,
						ObjectFolderConstants.
							EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES
					});

			for (ObjectDefinition objectDefinition : objectDefinitions) {
				JSONArray depotEntriesJSONArray = _getDepotEntriesJSONArray(
					ActionUtil.getAcceptedDepotEntryGroupIds(
						depotEntryGroupIds,
						objectDefinition.getObjectDefinitionId()));

				if (depotEntriesJSONArray.length() == 0) {
					continue;
				}

				String actionIcon = _icons.get(
					objectDefinition.getExternalReferenceCode());

				if (actionIcon == null) {
					String entryFolderERC =
						_getObjectEntryFolderExternalReferenceCode(
							objectDefinition);

					actionIcon = _icons.getOrDefault(entryFolderERC, "forms");
				}

				_quickActions.add(
					_createQuickAction(
						depotEntriesJSONArray, actionIcon, objectDefinition));
			}
		}

		if (AssetCategoriesPermission.contains(
				_themeDisplay.getPermissionChecker(),
				_themeDisplay.getScopeGroupId(), ActionKeys.ADD_VOCABULARY)) {

			_quickActions.add(
				HashMapBuilder.<String, Object>put(
					"action", "createVocabulary"
				).put(
					"icon", _icons.get("L_CMS_VOCABULARY")
				).put(
					"redirect",
					StringBundler.concat(
						PortalUtil.getLayoutFullURL(
							LayoutLocalServiceUtil.getLayoutByFriendlyURL(
								_themeDisplay.getScopeGroupId(), false,
								"/categorization/new-vocabulary"),
							_themeDisplay),
						"?backURL=", _themeDisplay.getURLCurrent())
				).put(
					"title",
					LanguageUtil.get(_themeDisplay.getLocale(), "vocabulary")
				).build());
		}

		return _quickActions;
	}

	private static final Map<String, String> _icons = HashMapBuilder.put(
		"L_CMS_BASIC_DOCUMENT", "documents-and-media"
	).put(
		"L_CMS_BASIC_WEB_CONTENT", "forms"
	).put(
		"L_CMS_BLOG", "blogs"
	).put(
		"L_CMS_EXTERNAL_VIDEO", "video"
	).put(
		"L_CMS_VOCABULARY", "vocabulary"
	).put(
		"L_CONTENTS", "web-content"
	).put(
		"L_FILES", "document-default"
	).build();

	private final GroupLocalService _groupLocalService;
	private final ObjectDefinitionService _objectDefinitionService;
	private List<Map<String, Object>> _quickActions;
	private final ThemeDisplay _themeDisplay;

}