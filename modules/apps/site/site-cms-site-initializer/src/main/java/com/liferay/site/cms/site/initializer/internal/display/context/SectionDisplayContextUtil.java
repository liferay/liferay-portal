/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.depot.service.DepotEntryServiceUtil;
import com.liferay.document.library.configuration.DLConfiguration;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.model.FDSActionDropdownItemBuilder;
import com.liferay.frontend.data.set.model.FDSActionDropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.object.service.ObjectEntryFolderLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
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
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionRegistryUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.site.cms.site.initializer.constants.CMSWorkflowConstants;
import com.liferay.site.cms.site.initializer.internal.util.ActionUtil;
import com.liferay.translation.constants.TranslationPortletKeys;

import jakarta.portlet.ActionRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author Daniel Sanz
 */
public class SectionDisplayContextUtil {

	public static String appendGroupIds(
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
				DepotEntryLocalServiceUtil.getDepotEntryGroupIds(
					themeDisplay.getCompanyId(), themeDisplay.getUserId(),
					DepotConstants.TYPE_SPACE),
				StringPool.COMMA),
			"))");
	}

	public static String appendStatus(String filterString) {
		return StringBundler.concat(
			filterString, " and status in (", _CMS_WORKFLOW_STATUSES_STRING,
			")");
	}

	public static String getAdditionalAPIURLParameters(
		String filterString, HttpServletRequest httpServletRequest,
		String rootObjectEntryFolderExternalReferenceCode) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		ObjectEntryFolder objectEntryFolder = _getObjectEntryFolder(
			themeDisplay.getCompanyId(),
			httpServletRequest.getAttribute(InfoDisplayWebKeys.INFO_ITEM),
			rootObjectEntryFolderExternalReferenceCode);

		StringBundler sb = new StringBundler(14);

		sb.append("emptySearch=true&filter=");

		if (objectEntryFolder != null) {
			sb.append("folderId eq ");
			sb.append(objectEntryFolder.getObjectEntryFolderId());
			sb.append(" and rootDescendantNode eq false");

			if (objectEntryFolder.getStatus() ==
					WorkflowConstants.STATUS_IN_TRASH) {

				sb.append(" and status eq ");
				sb.append(WorkflowConstants.STATUS_IN_TRASH);
			}
			else {
				sb.append(" and status in (");
				sb.append(_CMS_WORKFLOW_STATUSES_STRING);
				sb.append(")");
			}
		}
		else {
			sb.append(filterString);
		}

		sb.append("&nestedFields=embedded,embeddedTaxonomyCategory,");
		sb.append("file.metadata,file.previewURL,file.thumbnailURL,");
		sb.append("modifiedBy,numberOfObjectEntries,");
		sb.append("numberOfObjectEntryFolders,");
		sb.append("systemProperties.collaboratorBrief,");
		sb.append("systemProperties.objectDefinitionBrief");
		sb.append("&sort=dateModified:desc");

		return sb.toString();
	}

	public static List<DropdownItem> getAllSectionBulkActionDropdownItems(
		HttpServletRequest httpServletRequest) {

		List<DropdownItem> bulkActionDropdownItems =
			_getBulkActionDropdownItems(httpServletRequest);

		bulkActionDropdownItems.add(
			FDSActionDropdownItemBuilder.setHighlighted(
				true
			).setHref(
				"#"
			).setIcon(
				"upload"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "export-for-translation")
			).build(
				"export-for-translation"
			));
		bulkActionDropdownItems.add(
			FDSActionDropdownItemBuilder.setHref(
				"#"
			).setIcon(
				"download"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "download")
			).build(
				"download"
			));

		_addEditCategoriesAndTagsBulkActions(
			bulkActionDropdownItems, httpServletRequest);

		bulkActionDropdownItems.add(
			FDSActionDropdownItemBuilder.setHighlighted(
				true
			).setHref(
				"#"
			).setIcon(
				"semantic-search"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "find-and-replace")
			).build(
				"find-and-replace"
			));

		_addPermissionsBulkActions(bulkActionDropdownItems, httpServletRequest);

		return bulkActionDropdownItems;
	}

	public static List<FDSActionDropdownItem>
		getAllSectionFDSActionDropdownItems(
			HttpServletRequest httpServletRequest) {

		List<FDSActionDropdownItem> fdsActionDropdownItems =
			getFDSActionDropdownItems(httpServletRequest);

		fdsActionDropdownItems.add(
			6,
			FDSActionDropdownItemBuilder.setHref(
				"{embedded.file.link.href}"
			).setIcon(
				"download"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "download")
			).setMethod(
				"get"
			).setTarget(
				"link"
			).build(
				"download"
			));

		return fdsActionDropdownItems;
	}

	public static String getAutocompleteURL() {
		return StringBundler.concat(
			"/o/search/v1.0/search?emptySearch=true&entryClassNames=",
			"com.liferay.portal.kernel.model.User,",
			"com.liferay.portal.kernel.model.UserGroup&nestedFields=",
			"embedded");
	}

	public static Map<String, String> getCollaboratorURLs(
		long companyId, ObjectDefinitionService objectDefinitionService,
		String[] objectFolderExternalReferenceCodes) {

		Map<String, String> collaboratorURLs = new HashMap<>();

		for (ObjectDefinition objectDefinition :
				objectDefinitionService.getCMSObjectDefinitions(
					companyId, objectFolderExternalReferenceCodes)) {

			collaboratorURLs.put(
				objectDefinition.getClassName(),
				StringBundler.concat(
					"/o", objectDefinition.getRESTContextPath(),
					"/{objectEntryId}/collaborators"));
		}

		collaboratorURLs.put(
			ObjectEntryFolder.class.getName(),
			"/o/headless-object/v1.0/object-entry-folders" +
				"/{objectEntryFolderId}/collaborators");

		return collaboratorURLs;
	}

	public static List<DropdownItem> getContentsBulkActionDropdownItems(
		HttpServletRequest httpServletRequest) {

		List<DropdownItem> bulkActionDropdownItems =
			_getBulkActionDropdownItems(httpServletRequest);

		bulkActionDropdownItems.add(
			FDSActionDropdownItemBuilder.setHighlighted(
				true
			).setHref(
				"#"
			).setIcon(
				"upload"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "export-for-translation")
			).build(
				"export-for-translation"
			));

		_addEditCategoriesAndTagsBulkActions(
			bulkActionDropdownItems, httpServletRequest);
		_addPermissionsBulkActions(bulkActionDropdownItems, httpServletRequest);

		return bulkActionDropdownItems;
	}

	public static List<FDSActionDropdownItem> getContentsFDSActionDropdownItems(
		HttpServletRequest httpServletRequest) {

		return getFDSActionDropdownItems(httpServletRequest);
	}

	public static String getContentViewURL(ThemeDisplay themeDisplay) {
		return StringBundler.concat(
			themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
			GroupConstants.CMS_FRIENDLY_URL,
			"/edit_content_item?p_l_mode=read&p_p_state=",
			LiferayWindowState.POP_UP, "&redirect=",
			themeDisplay.getURLCurrent(), "&objectEntryId={embedded.id}");
	}

	public static CreationMenu getCreationMenu(
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
			if (_contains(
					ActionKeys.ADD_ENTRY,
					objectEntryFolder.getObjectEntryFolderId(), themeDisplay)) {

				objectEntryDepotEntryGroupIds.add(
					objectEntryFolder.getGroupId());
			}

			if (_contains(
					ObjectActionKeys.ADD_OBJECT_ENTRY_FOLDER,
					objectEntryFolder.getObjectEntryFolderId(), themeDisplay)) {

				objectEntryFolderDepotEntryGroupIds.add(
					objectEntryFolder.getGroupId());
			}
		}
		else {
			Map<Long, List<Long>> objectEntryFolderIdsMap =
				getObjectEntryFolderIdsMap(
					themeDisplay.getCompanyId(),
					rootObjectEntryFolderExternalReferenceCode,
					themeDisplay.getUserId());

			objectEntryDepotEntryGroupIds = getDepotEntryGroupIds(
				ActionKeys.ADD_ENTRY, objectEntryFolderIdsMap, themeDisplay);

			objectEntryFolderDepotEntryGroupIds = getDepotEntryGroupIds(
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

	public static JSONArray getDepotEntriesJSONArray(
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

	public static JSONArray getDepotEntriesJSONArray(
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

	public static List<Long> getDepotEntryGroupIds(
		String actionId, Map<Long, List<Long>> objectEntryFolderIdsMap,
		ThemeDisplay themeDisplay) {

		List<Long> depotEntryGroupIds = new ArrayList<>();

		for (Map.Entry<Long, List<Long>> entry :
				objectEntryFolderIdsMap.entrySet()) {

			for (long objectEntryFolderId : entry.getValue()) {
				if (_contains(actionId, objectEntryFolderId, themeDisplay)) {
					depotEntryGroupIds.add(entry.getKey());

					break;
				}
			}
		}

		return depotEntryGroupIds;
	}

	public static List<FDSActionDropdownItem> getFDSActionDropdownItems(
		HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return ListUtil.fromArray(
			FDSActionDropdownItemBuilder.setHref(
				ActionUtil.getBaseViewFolderURL(themeDisplay) + "{embedded.id}"
			).setIcon(
				"view"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "view-folder")
			).setMethod(
				"get"
			).setPermissionKey(
				"get"
			).setVisibilityFilters(
				HashMapBuilder.<String, Object>put(
					"entryClassName", ObjectEntryFolder.class.getName()
				).build()
			).build(
				"actionLinkFolder"
			),
			FDSActionDropdownItemBuilder.setHref(
				StringBundler.concat(
					themeDisplay.getPathFriendlyURLPublic(),
					GroupConstants.CMS_FRIENDLY_URL, "/e/edit-folder/",
					PortalUtil.getClassNameId(ObjectEntryFolder.class),
					"/{embedded.id}?redirect=", themeDisplay.getURLCurrent())
			).setIcon(
				"pencil"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "edit")
			).setMethod(
				"get"
			).setPermissionKey(
				"update"
			).setVisibilityFilters(
				HashMapBuilder.<String, Object>put(
					"entryClassName", ObjectEntryFolder.class.getName()
				).build()
			).build(
				"editFolder"
			),
			FDSActionDropdownItemBuilder.setHref(
				StringBundler.concat(
					themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
					GroupConstants.CMS_FRIENDLY_URL,
					"/edit_content_item?objectEntryId={embedded.id}&",
					"redirect=", themeDisplay.getURLCurrent())
			).setIcon(
				"pencil"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "edit")
			).setMethod(
				"get"
			).setPermissionKey(
				"update"
			).build(
				"actionLink"
			),
			FDSActionDropdownItemBuilder.setHref(
				StringBundler.concat(
					themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
					GroupConstants.CMS_FRIENDLY_URL,
					"/edit_content_item?p_l_mode=read&p_p_state=",
					LiferayWindowState.POP_UP, "&redirect=",
					themeDisplay.getURLCurrent(),
					"&objectEntryId={embedded.id}")
			).setIcon(
				"view"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "view")
			).setPermissionKey(
				"get"
			).build(
				"view-content"
			),
			FDSActionDropdownItemBuilder.setHref(
				StringPool.BLANK
			).setIcon(
				"view"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "view")
			).setPermissionKey(
				"get"
			).build(
				"view-file"
			),
			FDSActionDropdownItemBuilder.setHref(
				StringBundler.concat(
					themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
					GroupConstants.CMS_FRIENDLY_URL,
					"/translate_content_item?objectEntryId={embedded.id}&",
					"redirect=", themeDisplay.getURLCurrent())
			).setIcon(
				"automatic-translate"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "translate")
			).setMethod(
				"get"
			).setPermissionKey(
				"update"
			).build(
				"translate"
			),
			FDSActionDropdownItemBuilder.setIcon(
				"share"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "share")
			).setMethod(
				"get"
			).setPermissionKey(
				"share"
			).setTarget(
				"link"
			).build(
				"share"
			),
			FDSActionDropdownItemBuilder.setHref(
				StringPool.BLANK
			).setIcon(
				"info-circle-open"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "show-details")
			).setTarget(
				"infoPanel"
			).build(
				"show-details"
			),
			FDSActionDropdownItemBuilder.setHref(
				"{actions.expire.href}"
			).setIcon(
				"time"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "expire")
			).setMethod(
				"post"
			).setPermissionKey(
				"expire"
			).setTarget(
				"headless"
			).build(
				"expire"
			),
			FDSActionDropdownItemBuilder.setHref(
				StringBundler.concat(
					themeDisplay.getPathFriendlyURLPublic(),
					GroupConstants.CMS_FRIENDLY_URL,
					"/version-history?objectEntryId={embedded.id}&backURL=",
					themeDisplay.getURLCurrent())
			).setIcon(
				"date-time"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "view-history")
			).setMethod(
				"get"
			).setPermissionKey(
				"versions"
			).build(
				"version-history"
			),
			FDSActionDropdownItemBuilder.setIcon(
				"move-folder"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "move")
			).setPermissionKey(
				"update"
			).build(
				"move"
			),
			_getCopyFDSActionDropdownItem(httpServletRequest),
			FDSActionDropdownItemBuilder.setHref(
				PortletURLBuilder.create(
					PortalUtil.getControlPanelPortletURL(
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
				).buildString()
			).setIcon(
				"upload"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "export-for-translation")
			).setPermissionKey(
				"get"
			).build(
				"export-for-translation"
			),
			FDSActionDropdownItemBuilder.setHref(
				StringBundler.concat(
					themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
					GroupConstants.CMS_FRIENDLY_URL,
					"/edit_content_item?objectEntryId={embedded.id}&",
					"redirect=", themeDisplay.getURLCurrent())
			).setIcon(
				"download"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "import-translation")
			).setPermissionKey(
				"update"
			).build(
				"import-translation"
			),
			_getPermissionsFDSActionDropdownItem(
				httpServletRequest, themeDisplay),
			FDSActionDropdownItemBuilder.setIcon(
				"trash"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "delete")
			).setPermissionKey(
				"delete"
			).build(
				"delete"
			));
	}

	public static Map<String, String> getFileMimeTypeCssClasses(
		DLConfiguration dlConfiguration) {

		return HashMapBuilder.put(
			"default", "file-icon-color-0"
		).putAll(
			_getFileMimeTypeValues(
				dlConfiguration.codeFileMimeTypes(), "file-icon-color-7")
		).putAll(
			_getFileMimeTypeValues(
				dlConfiguration.compressedFileMimeTypes(), "file-icon-color-1")
		).putAll(
			_getFileMimeTypeValues(
				ArrayUtil.append(
					dlConfiguration.multimediaFileMimeTypes(),
					ContentTypes.
						APPLICATION_VND_LIFERAY_VIDEO_EXTERNAL_SHORTCUT_HTML),
				"file-icon-color-3")
		).putAll(
			_getFileMimeTypeValues(
				dlConfiguration.presentationFileMimeTypes(),
				"file-icon-color-4")
		).putAll(
			_getFileMimeTypeValues(
				dlConfiguration.spreadSheetFileMimeTypes(), "file-icon-color-2")
		).putAll(
			_getFileMimeTypeValues(
				dlConfiguration.textFileMimeTypes(), "file-icon-color-6")
		).putAll(
			_getFileMimeTypeValues(
				dlConfiguration.vectorialFileMimeTypes(), "file-icon-color-5")
		).build();
	}

	public static Map<String, String> getFileMimeTypeIcons(
		DLConfiguration dlConfiguration) {

		return HashMapBuilder.put(
			"default", "document-default"
		).putAll(
			_getFileMimeTypeValues(
				dlConfiguration.codeFileMimeTypes(), "document-code")
		).putAll(
			_getFileMimeTypeValues(
				dlConfiguration.compressedFileMimeTypes(),
				"document-compressed")
		).putAll(
			_getFileMimeTypeValues(
				dlConfiguration.presentationFileMimeTypes(),
				"document-presentation")
		).putAll(
			_getFileMimeTypeValues(
				dlConfiguration.spreadSheetFileMimeTypes(), "document-table")
		).putAll(
			_getFileMimeTypeValues(
				dlConfiguration.textFileMimeTypes(), "document-text")
		).putAll(
			_getFileMimeTypeValues(
				dlConfiguration.vectorialFileMimeTypes(), "document-vector")
		).putAll(
			_getFileMimeTypeMultimediaCssClasses(
				ArrayUtil.append(
					dlConfiguration.multimediaFileMimeTypes(),
					ContentTypes.
						APPLICATION_VND_LIFERAY_VIDEO_EXTERNAL_SHORTCUT_HTML))
		).build();
	}

	public static List<DropdownItem> getFilesBulkActionDropdownItems(
		HttpServletRequest httpServletRequest) {

		List<DropdownItem> bulkActionDropdownItems =
			_getBulkActionDropdownItems(httpServletRequest);

		bulkActionDropdownItems.add(
			FDSActionDropdownItemBuilder.setHref(
				"#"
			).setIcon(
				"download"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "download")
			).build(
				"download"
			));

		_addEditCategoriesAndTagsBulkActions(
			bulkActionDropdownItems, httpServletRequest);
		_addPermissionsBulkActions(bulkActionDropdownItems, httpServletRequest);

		return bulkActionDropdownItems;
	}

	public static List<FDSActionDropdownItem> getFilesFDSActionDropdownItems(
		HttpServletRequest httpServletRequest) {

		List<FDSActionDropdownItem> fdsActionDropdownItems =
			getFDSActionDropdownItems(httpServletRequest);

		fdsActionDropdownItems.add(
			6,
			FDSActionDropdownItemBuilder.setHref(
				"{embedded.file.link.href}"
			).setIcon(
				"download"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "download")
			).setMethod(
				"get"
			).setTarget(
				"link"
			).build(
				"download"
			));
		fdsActionDropdownItems.add(
			7,
			FDSActionDropdownItemBuilder.setHref(
				StringBundler.concat(
					"/o", GroupConstants.CMS_FRIENDLY_URL, "/download-folder/",
					PortalUtil.getClassNameId(ObjectEntryFolder.class),
					"/{embedded.id}")
			).setIcon(
				"download"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "download")
			).setMethod(
				"get"
			).setTarget(
				"link"
			).setVisibilityFilters(
				HashMapBuilder.<String, Object>put(
					"entryClassName", ObjectEntryFolder.class.getName()
				).build()
			).build(
				"download-folder"
			));

		return fdsActionDropdownItems;
	}

	public static Map<String, String> getObjectDefinitionCssClasses() {
		return HashMapBuilder.put(
			"default", "content-icon-custom-structure"
		).put(
			"L_CMS_BASIC_WEB_CONTENT", "content-icon-basic-content"
		).put(
			"L_CMS_BLOG", "content-icon-blog"
		).put(
			"L_CMS_EXTERNAL_VIDEO", "file-icon-color-3"
		).build();
	}

	public static Map<String, String> getObjectDefinitionIcons() {
		return HashMapBuilder.put(
			"default", "web-content"
		).put(
			"L_CMS_BASIC_WEB_CONTENT", "forms"
		).put(
			"L_CMS_BLOG", "blogs"
		).put(
			"L_CMS_EXTERNAL_VIDEO", "document-multimedia"
		).build();
	}

	public static Map<Long, List<Long>> getObjectEntryFolderIdsMap(
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

	private static void _addEditCategoriesAndTagsBulkActions(
		List<DropdownItem> bulkActionDropdownItems,
		HttpServletRequest httpServletRequest) {

		bulkActionDropdownItems.add(
			FDSActionDropdownItemBuilder.setIcon(
				"pencil"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "edit-categories")
			).setMethod(
				"post"
			).setPermissionKey(
				"update"
			).build(
				"edit-categories"
			));
		bulkActionDropdownItems.add(
			FDSActionDropdownItemBuilder.setIcon(
				"pencil"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "edit-tags")
			).setMethod(
				"post"
			).setPermissionKey(
				"update"
			).build(
				"edit-tags"
			));
	}

	private static void _addPermissionsBulkActions(
		List<DropdownItem> bulkActionDropdownItems,
		HttpServletRequest httpServletRequest) {

		bulkActionDropdownItems.add(
			FDSActionDropdownItemBuilder.setHref(
				"#"
			).setIcon(
				"password-policies"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "permissions")
			).build(
				"permissions"
			));
		bulkActionDropdownItems.add(
			FDSActionDropdownItemBuilder.setHref(
				"#"
			).setIcon(
				"password-policies"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "default-permissions")
			).build(
				"default-permissions"
			));
		bulkActionDropdownItems.add(
			FDSActionDropdownItemBuilder.setHref(
				StringPool.BLANK
			).setIcon(
				"password-policies"
			).setLabel(
				LanguageUtil.get(
					httpServletRequest, "edit-default-permissions-by-role")
			).build(
				"edit-default-permissions-by-role"
			));
		bulkActionDropdownItems.add(
			FDSActionDropdownItemBuilder.setHref(
				StringPool.BLANK
			).setIcon(
				"password-policies"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "edit-permissions-by-role")
			).build(
				"edit-permissions-by-role"
			));
		bulkActionDropdownItems.add(
			FDSActionDropdownItemBuilder.setHref(
				StringPool.BLANK
			).setIcon(
				"password-policies"
			).setLabel(
				LanguageUtil.get(
					httpServletRequest, "reset-to-default-permissions")
			).build(
				"reset-to-default-permissions"
			));
	}

	private static boolean _contains(
		String actionId, long objectEntryFolderId, ThemeDisplay themeDisplay) {

		try {
			ModelResourcePermission<ObjectEntryFolder> modelResourcePermission =
				ModelResourcePermissionRegistryUtil.getModelResourcePermission(
					ObjectEntryFolder.class.getName());

			if (modelResourcePermission == null) {
				return false;
			}

			return modelResourcePermission.contains(
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

	private static List<DropdownItem> _getBulkActionDropdownItems(
		HttpServletRequest httpServletRequest) {

		return ListUtil.fromArray(
			FDSActionDropdownItemBuilder.setHref(
				"#"
			).setIcon(
				"trash"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "delete")
			).build(
				"delete"
			),
			FDSActionDropdownItemBuilder.setHighlighted(
				true
			).setHref(
				"#"
			).setIcon(
				"move-folder"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "move-to")
			).build(
				"move-to"
			),
			FDSActionDropdownItemBuilder.setHighlighted(
				true
			).setHref(
				"#"
			).setIcon(
				"copy"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "copy-to")
			).build(
				"copy-to"
			),
			FDSActionDropdownItemBuilder.setHref(
				"#"
			).setIcon(
				"copy"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "duplicate")
			).build(
				"duplicate"
			),
			FDSActionDropdownItemBuilder.setHighlighted(
				true
			).setHref(
				"#"
			).setIcon(
				"time"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "expire")
			).build(
				"expire"
			));
	}

	private static FDSActionDropdownItem _getCopyFDSActionDropdownItem(
		HttpServletRequest httpServletRequest) {

		return FDSActionDropdownItemBuilder.setFDSActionDropdownItems(
			FDSActionDropdownItemList.of(
				FDSActionDropdownItemBuilder.setHref(
					StringPool.BLANK
				).setIcon(
					"copy"
				).setLabel(
					LanguageUtil.get(httpServletRequest, "copy-to")
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
					LanguageUtil.get(httpServletRequest, "duplicate")
				).setPermissionKey(
					"update"
				).build(
					"duplicate"
				))
		).setIcon(
			"copy"
		).setLabel(
			LanguageUtil.get(httpServletRequest, "copy")
		).setPermissionKey(
			"update"
		).setType(
			"contextual"
		).build(
			"copy-menu"
		);
	}

	private static JSONArray _getDepotEntriesJSONArray(
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

	private static JSONArray _getDepotEntriesJSONArray(
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

	private static Map<String, String> _getFileMimeTypeMultimediaCssClasses(
		String[] mimeTypes) {

		Map<String, String> fileMimeTypeMultimediaCssClasses = new HashMap<>();

		for (String mimeType : mimeTypes) {
			if (mimeType.startsWith("image")) {
				fileMimeTypeMultimediaCssClasses.put(
					mimeType, "document-image");
			}
			else {
				fileMimeTypeMultimediaCssClasses.put(
					mimeType, "document-multimedia");
			}
		}

		return fileMimeTypeMultimediaCssClasses;
	}

	private static Map<String, String> _getFileMimeTypeValues(
		String[] mimeTypes, String value) {

		Map<String, String> fileMimeTypeValues = new HashMap<>();

		for (String mimeType : mimeTypes) {
			fileMimeTypeValues.put(mimeType, value);
		}

		return fileMimeTypeValues;
	}

	private static JSONObject _getJSONObject(long groupId, Locale locale) {
		Group group = GroupLocalServiceUtil.fetchGroup(groupId);

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

	private static ObjectEntryFolder _getObjectEntryFolder(
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

	private static FDSActionDropdownItem _getPermissionsFDSActionDropdownItem(
		HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay) {

		return FDSActionDropdownItemBuilder.setFDSActionDropdownItems(
			FDSActionDropdownItemList.of(
				FDSActionDropdownItemBuilder.setHref(
					PortletURLBuilder.create(
						PortalUtil.getControlPanelPortletURL(
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
					LanguageUtil.get(httpServletRequest, "permissions")
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
			LanguageUtil.get(httpServletRequest, "permissions")
		).setPermissionKey(
			"permissions"
		).setType(
			"contextual"
		).build(
			"permissions-menu"
		);
	}

	private static String[] _getRootObjectEntryFolderExternalReferenceCodes(
		String rootObjectEntryFolderExternalReferenceCode) {

		if (rootObjectEntryFolderExternalReferenceCode == null) {
			return new String[] {
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES
			};
		}

		return new String[] {rootObjectEntryFolderExternalReferenceCode};
	}

	private static final String _CMS_WORKFLOW_STATUSES_STRING =
		StringUtil.merge(CMSWorkflowConstants.STATUSES, ", ");

	private static final Log _log = LogFactoryUtil.getLog(
		SectionDisplayContextUtil.class);

}