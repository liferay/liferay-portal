/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.depot.service.DepotEntryServiceUtil;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.model.FDSActionDropdownItemBuilder;
import com.liferay.frontend.data.set.model.FDSActionDropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.site.cms.site.initializer.internal.util.ActionUtil;
import com.liferay.translation.constants.TranslationPortletKeys;

import jakarta.portlet.ActionRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author Daniel Sanz
 */
public class SectionDisplayContextHelper {

	public SectionDisplayContextHelper(
		DepotEntryLocalService depotEntryLocalService,
		GroupLocalService groupLocalService, Language language,
		ObjectDefinitionSettingLocalService objectDefinitionSettingLocalService,
		ModelResourcePermission<ObjectEntryFolder>
			objectEntryFolderModelResourcePermission,
		Portal portal) {

		_depotEntryLocalService = depotEntryLocalService;
		_groupLocalService = groupLocalService;
		_language = language;
		_objectDefinitionSettingLocalService =
			objectDefinitionSettingLocalService;
		_objectEntryFolderModelResourcePermission =
			objectEntryFolderModelResourcePermission;
		_portal = portal;
	}

	public String appendGroupIds(
		String filterString, HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			if (RoleLocalServiceUtil.hasUserRole(
					themeDisplay.getUserId(), themeDisplay.getCompanyId(),
					RoleConstants.CMS_ADMINISTRATOR, true)) {

				return filterString;
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return StringBundler.concat(
			filterString, " and groupIds/any(g:g in (",
			StringUtil.merge(
				_depotEntryLocalService.getDepotEntryGroupIds(
					themeDisplay.getCompanyId(), themeDisplay.getUserId(),
					DepotConstants.TYPE_SPACE),
				StringPool.COMMA),
			"))");
	}

	public String appendStatus(String filterString) {
		return StringBundler.concat(
			filterString, " and status in (", StringUtil.merge(_statuses, ", "),
			")");
	}

	public String getAdditionalAPIURLParameters(
		String filterString, HttpServletRequest httpServletRequest,
		String rootObjectEntryFolderExternalReferenceCode) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		ObjectEntryFolder objectEntryFolder = _getObjectEntryFolder(
			themeDisplay.getCompanyId(),
			httpServletRequest.getAttribute(InfoDisplayWebKeys.INFO_ITEM),
			rootObjectEntryFolderExternalReferenceCode);

		StringBundler sb = new StringBundler(11);

		sb.append("emptySearch=true&filter=");

		if (objectEntryFolder != null) {
			sb.append("folderId eq ");
			sb.append(objectEntryFolder.getObjectEntryFolderId());

			if (objectEntryFolder.getStatus() ==
					WorkflowConstants.STATUS_IN_TRASH) {

				sb.append(" and status eq ");
				sb.append(WorkflowConstants.STATUS_IN_TRASH);
			}
			else {
				sb.append(" and status in (");
				sb.append(StringUtil.merge(_statuses, ", "));
				sb.append(")");
			}
		}
		else {
			sb.append(filterString);
		}

		sb.append("&nestedFields=embedded,embeddedTaxonomyCategory,");
		sb.append("file.metadata,file.previewURL,file.thumbnailURL,");
		sb.append("numberOfObjectEntries,numberOfObjectEntryFolders,");
		sb.append("systemProperties.objectDefinitionBrief");
		sb.append("&sort=dateModified:desc");

		return sb.toString();
	}

	public CreationMenu getCreationMenu(
		List<DropdownItem> dropdownItems, HttpServletRequest httpServletRequest,
		String rootObjectEntryFolderExternalReferenceCode) {

		CreationMenu creationMenu = new CreationMenu();

		if (ListUtil.isEmpty(dropdownItems)) {
			return creationMenu;
		}

		List<Long> objectEntryDepotEntryGroupIds = new ArrayList<>();
		List<Long> objectEntryFolderDepotEntryGroupIds = new ArrayList<>();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		ObjectEntryFolder objectEntryFolder = _getObjectEntryFolder(
			themeDisplay.getCompanyId(),
			httpServletRequest.getAttribute(InfoDisplayWebKeys.INFO_ITEM),
			rootObjectEntryFolderExternalReferenceCode);

		if (objectEntryFolder != null) {
			if (_modelResourcePermissionContains(
					ActionKeys.ADD_ENTRY,
					objectEntryFolder.getObjectEntryFolderId(), themeDisplay)) {

				objectEntryDepotEntryGroupIds.add(
					objectEntryFolder.getGroupId());
			}

			if (_modelResourcePermissionContains(
					ObjectActionKeys.ADD_OBJECT_ENTRY_FOLDER,
					objectEntryFolder.getObjectEntryFolderId(), themeDisplay)) {

				objectEntryFolderDepotEntryGroupIds.add(
					objectEntryFolder.getGroupId());
			}
		}
		else {
			Map<Long, List<Long>> objectEntryFolderIdsMap =
				_getObjectEntryFolderIdsMap(
					themeDisplay.getCompanyId(),
					rootObjectEntryFolderExternalReferenceCode,
					themeDisplay.getUserId());

			objectEntryDepotEntryGroupIds = _getDepotEntryGroupIds(
				ActionKeys.ADD_ENTRY, objectEntryFolderIdsMap, themeDisplay);

			objectEntryFolderDepotEntryGroupIds = _getDepotEntryGroupIds(
				ObjectActionKeys.ADD_OBJECT_ENTRY_FOLDER,
				objectEntryFolderIdsMap, themeDisplay);
		}

		if (objectEntryDepotEntryGroupIds.isEmpty() &&
			objectEntryFolderDepotEntryGroupIds.isEmpty()) {

			return creationMenu;
		}

		for (DropdownItem dropdownItem : dropdownItems) {
			JSONArray depotEntriesJSONArray = null;

			Map<String, Object> dropdownItemData =
				(Map<String, Object>)dropdownItem.get("data");

			if (Objects.equals(
					dropdownItemData.get("action"), "createFolder")) {

				depotEntriesJSONArray = _getDepotEntriesJSONArray(
					objectEntryFolderDepotEntryGroupIds, dropdownItem,
					themeDisplay.getLocale());
			}
			else {
				depotEntriesJSONArray = _getDepotEntriesJSONArray(
					objectEntryDepotEntryGroupIds, dropdownItem,
					themeDisplay.getLocale());
			}

			if (depotEntriesJSONArray.length() == 0) {
				continue;
			}

			dropdownItem.putData("assetLibraries", depotEntriesJSONArray);

			creationMenu.addPrimaryDropdownItem(dropdownItem);
		}

		return creationMenu;
	}

	public JSONArray getDepotEntriesJSONArray(
		HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return _getDepotEntriesJSONArray(
			DepotEntryServiceUtil.getDepotEntryGroupIds(
				themeDisplay.getCompanyId(), themeDisplay.getUserId(),
				DepotConstants.TYPE_SPACE),
			themeDisplay.getLocale());
	}

	public JSONArray getDepotEntriesJSONArray(
		HttpServletRequest httpServletRequest,
		String rootObjectEntryFolderExternalReferenceCode) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		ObjectEntryFolder objectEntryFolder = _getObjectEntryFolder(
			themeDisplay.getCompanyId(),
			httpServletRequest.getAttribute(InfoDisplayWebKeys.INFO_ITEM),
			rootObjectEntryFolderExternalReferenceCode);

		if (objectEntryFolder != null) {
			return _getDepotEntriesJSONArray(
				List.of(objectEntryFolder.getGroupId()),
				themeDisplay.getLocale());
		}

		return _getDepotEntriesJSONArray(
			DepotEntryServiceUtil.getDepotEntryGroupIds(
				themeDisplay.getCompanyId(), themeDisplay.getUserId(),
				DepotConstants.TYPE_SPACE),
			themeDisplay.getLocale());
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems(
		HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				ActionUtil.getBaseViewFolderURL(themeDisplay) + "{embedded.id}",
				"view", "actionLinkFolder",
				LanguageUtil.get(httpServletRequest, "view-folder"), "get",
				"get", null,
				HashMapBuilder.<String, Object>put(
					"entryClassName", ObjectEntryFolder.class.getName()
				).build()),
			new FDSActionDropdownItem(
				StringBundler.concat(
					themeDisplay.getPathFriendlyURLPublic(),
					GroupConstants.CMS_FRIENDLY_URL, "/e/edit-folder/",
					_portal.getClassNameId(ObjectEntryFolder.class),
					"/{embedded.id}?redirect=", themeDisplay.getURLCurrent()),
				"pencil", "editFolder",
				LanguageUtil.get(httpServletRequest, "edit"), "get", "update",
				null,
				HashMapBuilder.<String, Object>put(
					"entryClassName", ObjectEntryFolder.class.getName()
				).build()),
			new FDSActionDropdownItem(
				StringBundler.concat(
					themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
					GroupConstants.CMS_FRIENDLY_URL,
					"/edit_content_item?objectEntryId={embedded.id}&",
					"redirect=", themeDisplay.getURLCurrent()),
				"pencil", "actionLink",
				LanguageUtil.get(httpServletRequest, "edit"), "get", "update",
				null),
			new FDSActionDropdownItem(
				StringBundler.concat(
					themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
					GroupConstants.CMS_FRIENDLY_URL,
					"/edit_content_item?p_l_mode=read&p_p_state=",
					LiferayWindowState.POP_UP, "&redirect=",
					themeDisplay.getURLCurrent(),
					"&objectEntryId={embedded.id}"),
				"view", "view-content",
				LanguageUtil.get(httpServletRequest, "view"), null, "get",
				null),
			new FDSActionDropdownItem(
				StringPool.BLANK, "view", "view-file",
				LanguageUtil.get(httpServletRequest, "view"), null, "get",
				null),
			new FDSActionDropdownItem(
				StringBundler.concat(
					themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
					GroupConstants.CMS_FRIENDLY_URL,
					"/translate_content_item?objectEntryId={embedded.id}&",
					"redirect=", themeDisplay.getURLCurrent()),
				"automatic-translate", "translate",
				LanguageUtil.get(httpServletRequest, "translate"), "get",
				"update", null),
			new FDSActionDropdownItem(
				null, "share", "share",
				LanguageUtil.get(httpServletRequest, "share"), "get", "share",
				"link"),
			new FDSActionDropdownItem(
				"{actions.expire.href}", "time", "expire",
				LanguageUtil.get(httpServletRequest, "expire"), "post",
				"expire", "headless"),
			new FDSActionDropdownItem(
				StringBundler.concat(
					themeDisplay.getPathFriendlyURLPublic(),
					GroupConstants.CMS_FRIENDLY_URL,
					"/version-history?objectEntryId={embedded.id}&backURL=",
					themeDisplay.getURLCurrent()),
				"date-time", "version-history",
				LanguageUtil.get(httpServletRequest, "view-history"), "get",
				"versions", null),
			new FDSActionDropdownItem(
				null, "move-folder", "move",
				_language.get(httpServletRequest, "move"), null, "update",
				null),
			_getCopyFDSActionDropdownItem(httpServletRequest),
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					_portal.getControlPanelPortletURL(
						httpServletRequest, TranslationPortletKeys.TRANSLATION,
						ActionRequest.RENDER_PHASE)
				).setMVCRenderCommandName(
					"/translation/export_translation"
				).setParameter(
					"className", "{entryClassName}"
				).setParameter(
					"classPK", "{embedded.id}"
				).setParameter(
					"groupId", "{embedded.scopeId}"
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString(),
				"upload", "export-for-translation",
				LanguageUtil.get(httpServletRequest, "export-for-translation"),
				null, "get", null),
			new FDSActionDropdownItem(
				StringBundler.concat(
					themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
					GroupConstants.CMS_FRIENDLY_URL,
					"/edit_content_item?objectEntryId={embedded.id}&",
					"redirect=", themeDisplay.getURLCurrent()),
				"download", "import-translation",
				LanguageUtil.get(httpServletRequest, "import-translation"),
				null, "update", null),
			_getPermissionsFDSActionDropdownItem(
				httpServletRequest, themeDisplay),
			new FDSActionDropdownItem(
				null, "trash", "delete",
				_language.get(httpServletRequest, "delete"), null, "delete",
				null));
	}

	private FDSActionDropdownItem _getCopyFDSActionDropdownItem(
		HttpServletRequest httpServletRequest) {

		return FDSActionDropdownItemBuilder.setFDSActionDropdownItems(
			FDSActionDropdownItemList.of(
				FDSActionDropdownItemBuilder.setHref(
					StringPool.BLANK
				).setIcon(
					"copy"
				).setLabel(
					_language.get(httpServletRequest, "copy-to")
				).setPermissionKey(
					"update"
				).build(
					"copy"
				),
				FDSActionDropdownItemBuilder.setHref(
					StringPool.BLANK
				).setIcon(
					"copy"
				).setLabel(
					_language.get(httpServletRequest, "duplicate")
				).setPermissionKey(
					"duplicate"
				).build(
					"duplicate"
				))
		).setIcon(
			"copy"
		).setLabel(
			_language.get(httpServletRequest, "copy")
		).setPermissionKey(
			"update"
		).setType(
			"contextual"
		).build(
			"copy-menu"
		);
	}

	private JSONArray _getDepotEntriesJSONArray(
		List<Long> depotEntryGroupIds, DropdownItem dropdownItem,
		Locale locale) {

		Map<String, Object> dropdownItemData =
			(Map<String, Object>)dropdownItem.get("data");

		long objectDefinitionId = GetterUtil.getLong(
			dropdownItemData.get("objectDefinitionId"));

		if (objectDefinitionId != 0) {
			return _getDepotEntriesJSONArray(
				ActionUtil.getAcceptedDepotEntryGroupIds(
					depotEntryGroupIds, objectDefinitionId),
				locale);
		}

		return _getDepotEntriesJSONArray(depotEntryGroupIds, locale);
	}

	private JSONArray _getDepotEntriesJSONArray(
		List<Long> groupIds, Locale locale) {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		for (Long groupId : groupIds) {
			JSONObject jsonObject = _getJSONObject(groupId, locale);

			if (jsonObject != null) {
				jsonArray.put(jsonObject);
			}
		}

		return jsonArray;
	}

	private List<Long> _getDepotEntryGroupIds(
		String actionId, Map<Long, List<Long>> objectEntryFolderIdsMap,
		ThemeDisplay themeDisplay) {

		List<Long> depotEntryGroupIds = new ArrayList<>();

		for (Map.Entry<Long, List<Long>> entry :
				objectEntryFolderIdsMap.entrySet()) {

			for (long objectEntryFolderId : entry.getValue()) {
				if (_modelResourcePermissionContains(
						actionId, objectEntryFolderId, themeDisplay)) {

					depotEntryGroupIds.add(entry.getKey());

					break;
				}
			}
		}

		return depotEntryGroupIds;
	}

	private JSONObject _getJSONObject(long groupId, Locale locale) {
		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return null;
		}

		return JSONUtil.put(
			"externalReferenceCode", group.getExternalReferenceCode()
		).put(
			"groupId", group.getGroupId()
		).put(
			"name", group.getName(locale)
		);
	}

	private ObjectEntryFolder _getObjectEntryFolder(
		long companyId, Object object,
		String rootObjectEntryFolderExternalReferenceCode) {

		if (object instanceof DepotEntry) {
			DepotEntry depotEntry = (DepotEntry)object;

			return ObjectEntryFolderLocalServiceUtil.
				fetchObjectEntryFolderByExternalReferenceCode(
					rootObjectEntryFolderExternalReferenceCode,
					depotEntry.getGroupId(), companyId);
		}
		else if (object instanceof ObjectEntryFolder) {
			return (ObjectEntryFolder)object;
		}

		return null;
	}

	private Map<Long, List<Long>> _getObjectEntryFolderIdsMap(
		long companyId, String rootObjectEntryFolderExternalReferenceCode,
		long userId) {

		Map<Long, List<Long>> objectEntryFolderIdsMap = new HashMap<>();

		for (long depotEntryGroupId :
				DepotEntryServiceUtil.getDepotEntryGroupIds(
					companyId, userId, DepotConstants.TYPE_SPACE)) {

			for (String objectEntryFolderExternalReferenceCode :
					_getRootObjectEntryFolderExternalReferenceCodes(
						rootObjectEntryFolderExternalReferenceCode)) {

				ObjectEntryFolder objectEntryFolder =
					ObjectEntryFolderLocalServiceUtil.
						fetchObjectEntryFolderByExternalReferenceCode(
							objectEntryFolderExternalReferenceCode,
							depotEntryGroupId, companyId);

				if (objectEntryFolder != null) {
					List<Long> objectEntryFolderIds =
						objectEntryFolderIdsMap.computeIfAbsent(
							depotEntryGroupId, key -> new ArrayList<>());

					objectEntryFolderIds.add(
						objectEntryFolder.getObjectEntryFolderId());
				}
			}
		}

		return objectEntryFolderIdsMap;
	}

	private FDSActionDropdownItem _getPermissionsFDSActionDropdownItem(
		HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay) {

		return FDSActionDropdownItemBuilder.setFDSActionDropdownItems(
			FDSActionDropdownItemList.of(
				FDSActionDropdownItemBuilder.setHref(
					PortletURLBuilder.create(
						_portal.getControlPanelPortletURL(
							httpServletRequest,
							"com_liferay_portlet_configuration_web_portlet_" +
								"PortletConfigurationPortlet",
							ActionRequest.RENDER_PHASE)
					).setMVCPath(
						"/edit_permissions.jsp"
					).setRedirect(
						themeDisplay.getURLCurrent()
					).setParameter(
						"modelResource", "{entryClassName}"
					).setParameter(
						"modelResourceDescription", "{embedded.name}"
					).setParameter(
						"resourceGroupId", "{embedded.scopeId}"
					).setParameter(
						"resourcePrimKey", "{embedded.id}"
					).setWindowState(
						LiferayWindowState.POP_UP
					).buildString()
				).setIcon(
					"password-policies"
				).setLabel(
					_language.get(httpServletRequest, "permissions")
				).setMethod(
					"get"
				).setPermissionKey(
					"permissions"
				).setTarget(
					"modal-permissions"
				).build(
					"permissions"
				),
				FDSActionDropdownItemBuilder.setHref(
					StringPool.BLANK
				).setIcon(
					"password-policies"
				).setLabel(
					LanguageUtil.get(httpServletRequest, "default-permissions")
				).setPermissionKey(
					"permissions"
				).setVisibilityFilters(
					HashMapBuilder.<String, Object>put(
						"entryClassName", ObjectEntryFolder.class.getName()
					).build()
				).build(
					"default-permissions"
				),
				FDSActionDropdownItemBuilder.setHref(
					StringPool.BLANK
				).setIcon(
					"password-policies"
				).setLabel(
					LanguageUtil.get(
						httpServletRequest,
						"edit-and-propagate-default-permissions")
				).setPermissionKey(
					"permissions"
				).setVisibilityFilters(
					HashMapBuilder.<String, Object>put(
						"entryClassName", ObjectEntryFolder.class.getName()
					).build()
				).build(
					"edit-and-propagate-default-permissions"
				),
				FDSActionDropdownItemBuilder.setHref(
					StringPool.BLANK
				).setIcon(
					"password-policies"
				).setLabel(
					LanguageUtil.get(
						httpServletRequest, "reset-to-default-permissions")
				).setPermissionKey(
					"permissions"
				).build(
					"reset-to-default-permissions"
				))
		).setIcon(
			"password-policies"
		).setLabel(
			_language.get(httpServletRequest, "permissions")
		).setPermissionKey(
			"permissions"
		).setType(
			"contextual"
		).build(
			"permissions-menu"
		);
	}

	private String[] _getRootObjectEntryFolderExternalReferenceCodes(
		String rootObjectEntryFolderExternalReferenceCode) {

		if (rootObjectEntryFolderExternalReferenceCode == null) {
			return new String[] {
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES
			};
		}

		return new String[] {rootObjectEntryFolderExternalReferenceCode};
	}

	private boolean _modelResourcePermissionContains(
		String actionId, long objectEntryFolderId, ThemeDisplay themeDisplay) {

		try {
			return _objectEntryFolderModelResourcePermission.contains(
				themeDisplay.getPermissionChecker(), objectEntryFolderId,
				actionId);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SectionDisplayContextHelper.class);

	private static final List<Integer> _statuses = Arrays.asList(
		WorkflowConstants.STATUS_APPROVED, WorkflowConstants.STATUS_DRAFT,
		WorkflowConstants.STATUS_EXPIRED, WorkflowConstants.STATUS_PENDING,
		WorkflowConstants.STATUS_SCHEDULED);

	private final DepotEntryLocalService _depotEntryLocalService;
	private final GroupLocalService _groupLocalService;
	private final Language _language;
	private final ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;
	private final ModelResourcePermission<ObjectEntryFolder>
		_objectEntryFolderModelResourcePermission;
	private final Portal _portal;

}