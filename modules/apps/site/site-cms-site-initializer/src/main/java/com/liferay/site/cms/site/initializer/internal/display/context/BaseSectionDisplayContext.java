/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.configuration.DLConfiguration;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectDefinitionSetting;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.editor.configuration.EditorConfiguration;
import com.liferay.portal.kernel.editor.configuration.EditorConfigurationFactoryUtil;
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
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.site.cms.site.initializer.internal.util.ActionUtil;
import com.liferay.translation.constants.TranslationPortletKeys;

import jakarta.portlet.ActionRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Marco Galluzzi
 */
public abstract class BaseSectionDisplayContext {

	public BaseSectionDisplayContext(
		DepotEntryLocalService depotEntryLocalService,
		DLConfiguration dlConfiguration, GroupLocalService groupLocalService,
		HttpServletRequest httpServletRequest, Language language,
		ObjectDefinitionService objectDefinitionService,
		ObjectDefinitionSettingLocalService objectDefinitionSettingLocalService,
		ModelResourcePermission<ObjectEntryFolder>
			objectEntryFolderModelResourcePermission,
		Portal portal) {

		this.depotEntryLocalService = depotEntryLocalService;

		_dlConfiguration = dlConfiguration;

		this.groupLocalService = groupLocalService;
		this.httpServletRequest = httpServletRequest;
		this.language = language;

		_objectDefinitionService = objectDefinitionService;
		_objectDefinitionSettingLocalService =
			objectDefinitionSettingLocalService;
		_objectEntryFolderModelResourcePermission =
			objectEntryFolderModelResourcePermission;

		this.portal = portal;

		themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		objectEntryFolder = _getObjectEntryFolder(
			themeDisplay.getCompanyId(),
			httpServletRequest.getAttribute(InfoDisplayWebKeys.INFO_ITEM));
	}

	public Map<String, Object> getAdditionalProps() {
		return HashMapBuilder.<String, Object>put(
			"assetLibraries", _getDepotEntriesJSONArray()
		).put(
			"autocompleteURL",
			() -> StringBundler.concat(
				"/o/search/v1.0/search?emptySearch=",
				"true&entryClassNames=com.liferay.portal.kernel.model.User,",
				"com.liferay.portal.kernel.model.UserGroup&nestedFields=",
				"embedded")
		).put(
			"baseAssetLibraryViewURL", ActionUtil.getBaseSpaceURL(themeDisplay)
		).put(
			"baseFolderViewURL", ActionUtil.getBaseViewFolderURL(themeDisplay)
		).put(
			"brokenLinksCheckerEnabled",
			GetterUtil.getBoolean(
				PropsUtil.get(PropsKeys.CMS_BROKEN_LINKS_CHECKER_ENABLED))
		).put(
			"cmsGroupId",
			() -> {
				try {
					Group group = groupLocalService.getGroup(
						themeDisplay.getCompanyId(), GroupConstants.CMS);

					return GetterUtil.getLong(group.getGroupId());
				}
				catch (PortalException portalException) {
					if (_log.isDebugEnabled()) {
						_log.debug(portalException);
					}
				}

				return null;
			}
		).put(
			"collaboratorURLs",
			() -> {
				Map<String, String> collaboratorURLs = new HashMap<>();

				for (ObjectDefinition objectDefinition :
						_objectDefinitionService.getCMSObjectDefinitions(
							themeDisplay.getCompanyId(),
							getObjectFolderExternalReferenceCodes())) {

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
		).put(
			"commentsProps",
			HashMapBuilder.<String, Object>put(
				"addCommentURL",
				StringBundler.concat(
					themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
					GroupConstants.CMS_FRIENDLY_URL,
					"/add_content_item_comment")
			).put(
				"deleteCommentURL",
				StringBundler.concat(
					themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
					GroupConstants.CMS_FRIENDLY_URL,
					"/delete_content_item_comment")
			).put(
				"editCommentURL",
				StringBundler.concat(
					themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
					GroupConstants.CMS_FRIENDLY_URL,
					"/edit_content_item_comment")
			).put(
				"editorConfig",
				() -> {
					EditorConfiguration contentItemCommentEditorConfiguration =
						EditorConfigurationFactoryUtil.getEditorConfiguration(
							StringPool.BLANK, "contentItemCommentEditor",
							StringPool.BLANK, Collections.emptyMap(),
							themeDisplay,
							RequestBackedPortletURLFactoryUtil.create(
								httpServletRequest));

					Map<String, Object> data =
						contentItemCommentEditorConfiguration.getData();

					return data.get("editorConfig");
				}
			).put(
				"getCommentsURL",
				StringBundler.concat(
					themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
					GroupConstants.CMS_FRIENDLY_URL, "/get_asset_comments")
			).build()
		).put(
			"contentViewURL",
			StringBundler.concat(
				themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
				GroupConstants.CMS_FRIENDLY_URL,
				"/edit_content_item?&p_l_mode=read&p_p_state=",
				LiferayWindowState.POP_UP, "&redirect=",
				themeDisplay.getURLCurrent(), "&objectEntryId={embedded.id}")
		).put(
			"defaultPermissionAdditionalProps",
			_getDefaultPermissionAdditionalProps()
		).put(
			"fileMimeTypeCssClasses",
			() -> {
				if (_dlConfiguration == null) {
					return null;
				}

				return _getFileMimeTypeCssClasses();
			}
		).put(
			"fileMimeTypeIcons",
			() -> {
				if (_dlConfiguration == null) {
					return null;
				}

				return _getFileMimeTypeIcons();
			}
		).put(
			"objectDefinitionCssClasses",
			HashMapBuilder.put(
				"default", "content-icon-custom-structure"
			).put(
				"L_BASIC_WEB_CONTENT", "content-icon-basic-content"
			).put(
				"L_BLOG", "content-icon-blog"
			).build()
		).put(
			"objectDefinitionIcons",
			HashMapBuilder.put(
				"default", "web-content"
			).put(
				"L_BASIC_WEB_CONTENT", "forms"
			).put(
				"L_BLOG", "blogs"
			).build()
		).put(
			"parentObjectEntryFolderExternalReferenceCode",
			_getParentObjectEntryFolderExternalReferenceCode()
		).put(
			"redirect", themeDisplay.getURLCurrent()
		).build();
	}

	public String getAPIURL() {
		StringBundler sb = new StringBundler(9);

		sb.append("/o/search/v1.0/search?emptySearch=true&filter=");

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
			sb.append(getCMSSectionFilterString());
		}

		sb.append("&nestedFields=embedded,file.metadata,");
		sb.append("file.previewURL,file.thumbnailURL,");
		sb.append("systemProperties.objectDefinitionBrief");

		return sb.toString();
	}

	public Map<String, Object> getBreadcrumbProps() throws PortalException {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		addBreadcrumbItem(jsonArray, false, null, _getLayoutName());

		return HashMapBuilder.<String, Object>put(
			"breadcrumbItems", jsonArray
		).put(
			"hideSpace", true
		).build();
	}

	public List<DropdownItem> getBulkActionDropdownItems() {
		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				"#", "trash", "delete",
				LanguageUtil.get(httpServletRequest, "delete"), null, null,
				null));
	}

	public CreationMenu getCreationMenu() {
		return new CreationMenu() {
			{
				if (_hasAddEntryPermission()) {
					for (DropdownItem dropdownItem :
							getCreationMenuDropdownItems()) {

						JSONArray depotEntriesJSONArray =
							_getDepotEntriesJSONArray(dropdownItem);

						if (depotEntriesJSONArray == null) {
							continue;
						}

						dropdownItem.putData(
							"assetLibraries", depotEntriesJSONArray);

						addPrimaryDropdownItem(dropdownItem);
					}
				}
			}
		};
	}

	public List<DropdownItem> getCreationMenuDropdownItems() {
		return Collections.emptyList();
	}

	public abstract Map<String, Object> getEmptyState();

	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				ActionUtil.getBaseViewFolderURL(themeDisplay) + "{embedded.id}",
				"view", "actionLinkFolder",
				LanguageUtil.get(httpServletRequest, "view-folder"), "get",
				"update", null,
				HashMapBuilder.<String, Object>put(
					"entryClassName", ObjectEntryFolder.class.getName()
				).build()),
			new FDSActionDropdownItem(
				StringBundler.concat(
					themeDisplay.getPathFriendlyURLPublic(),
					GroupConstants.CMS_FRIENDLY_URL, "/e/edit-folder/",
					portal.getClassNameId(ObjectEntryFolder.class),
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
				null, "share", "share",
				LanguageUtil.get(httpServletRequest, "share"), "get", "share",
				"link"),
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
				"{actions.expire.href}", "time", "expire",
				LanguageUtil.get(httpServletRequest, "expire"), "post",
				"expire", "headless"),
			new FDSActionDropdownItem(
				StringBundler.concat(
					themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
					GroupConstants.CMS_FRIENDLY_URL,
					"/edit_content_item?&p_l_mode=read&p_p_state=",
					LiferayWindowState.POP_UP, "&redirect=",
					themeDisplay.getURLCurrent(),
					"&objectEntryId={embedded.id}"),
				"view", "view-content",
				LanguageUtil.get(httpServletRequest, "view"), null, null, null),
			new FDSActionDropdownItem(
				StringPool.BLANK, "view", "view-file",
				LanguageUtil.get(httpServletRequest, "view"), null, null, null),
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
				PortletURLBuilder.create(
					portal.getControlPanelPortletURL(
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
				null, null, null),
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					portal.getControlPanelPortletURL(
						httpServletRequest, TranslationPortletKeys.TRANSLATION,
						ActionRequest.RENDER_PHASE)
				).setMVCRenderCommandName(
					"/translation/import_translation"
				).setParameter(
					"className", "{entryClassName}"
				).setParameter(
					"classPK", "{embedded.id}"
				).setParameter(
					"groupId", "{embedded.scopeId}"
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString(),
				"download", "import-translation",
				LanguageUtil.get(httpServletRequest, "import-translation"),
				null, null, null),
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					portal.getControlPanelPortletURL(
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
				).buildString(),
				"password-policies", "permissions",
				language.get(httpServletRequest, "permissions"), "get",
				"permissions", "modal-permissions"),
			new FDSActionDropdownItem(
				StringPool.BLANK, "password-policies", "default-permissions",
				LanguageUtil.get(httpServletRequest, "default-permissions"),
				null, "permissions", null),
			new FDSActionDropdownItem(
				null, "trash", "delete",
				language.get(httpServletRequest, "delete"), null, "delete",
				null));
	}

	protected void addBreadcrumbItem(
		JSONArray jsonArray, boolean active, String friendlyURL, String label) {

		jsonArray.put(
			JSONUtil.put(
				"active", active
			).put(
				"href", friendlyURL
			).put(
				"label", label
			));
	}

	protected String appendStatus(String filterString) {
		return StringBundler.concat(
			filterString, " and status in (", StringUtil.merge(_statuses, ", "),
			")");
	}

	protected abstract String getCMSSectionFilterString();

	protected String[] getObjectFolderExternalReferenceCodes() {
		return new String[] {
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES,
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES
		};
	}

	protected String getRootObjectEntryFolderExternalReferenceCode() {
		return null;
	}

	protected final DepotEntryLocalService depotEntryLocalService;
	protected final GroupLocalService groupLocalService;
	protected final HttpServletRequest httpServletRequest;
	protected final Language language;
	protected final ObjectEntryFolder objectEntryFolder;
	protected final Portal portal;
	protected final ThemeDisplay themeDisplay;

	private List<Long> _getAcceptedGroupIds(long objectDefinitionId) {
		List<Long> acceptedGroupIds = new ArrayList<>();

		ObjectDefinitionSetting objectDefinitionSetting =
			_objectDefinitionSettingLocalService.fetchObjectDefinitionSetting(
				objectDefinitionId,
				ObjectDefinitionSettingConstants.NAME_ACCEPTED_GROUP_IDS);

		for (String groupId :
				StringUtil.split(objectDefinitionSetting.getValue())) {

			DepotEntry depotEntry = depotEntryLocalService.fetchGroupDepotEntry(
				GetterUtil.getLong(groupId));

			if (depotEntry != null) {
				acceptedGroupIds.add(depotEntry.getGroupId());
			}
		}

		return acceptedGroupIds;
	}

	private Map<String, Object> _getDefaultPermissionAdditionalProps() {
		return HashMapBuilder.<String, Object>put(
			"actions",
			() -> HashMapBuilder.put(
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
				() -> {
					ObjectDefinition objectDefinition =
						ObjectDefinitionLocalServiceUtil.
							getObjectDefinitionByExternalReferenceCode(
								"L_BASIC_WEB_CONTENT",
								themeDisplay.getCompanyId());

					List<String> guestUnsupportedActions =
						ResourceActionsUtil.getResourceGuestUnsupportedActions(
							null, objectDefinition.getClassName());

					return TransformUtil.transformToArray(
						ResourceActionsUtil.getResourceActions(
							objectDefinition.getClassName()),
						resourceAction -> HashMapBuilder.<String, Object>put(
							"guestUnsupported",
							guestUnsupportedActions.contains(resourceAction)
						).put(
							"key", resourceAction
						).put(
							"label",
							ResourceActionsUtil.getAction(
								httpServletRequest, resourceAction)
						).build(),
						Map.class);
				}
			).put(
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES,
				() -> {
					ObjectDefinition objectDefinition =
						ObjectDefinitionLocalServiceUtil.
							getObjectDefinitionByExternalReferenceCode(
								"L_BASIC_DOCUMENT",
								themeDisplay.getCompanyId());

					List<String> guestUnsupportedActions =
						ResourceActionsUtil.getResourceGuestUnsupportedActions(
							null, objectDefinition.getClassName());

					return TransformUtil.transformToArray(
						ResourceActionsUtil.getResourceActions(
							objectDefinition.getClassName()),
						resourceAction -> HashMapBuilder.<String, Object>put(
							"guestUnsupported",
							guestUnsupportedActions.contains(resourceAction)
						).put(
							"key", resourceAction
						).put(
							"label",
							ResourceActionsUtil.getAction(
								httpServletRequest, resourceAction)
						).build(),
						Map.class);
				}
			).put(
				"OBJECT_ENTRY_FOLDERS",
				() -> {
					List<String> guestUnsupportedActions =
						ResourceActionsUtil.getResourceGuestUnsupportedActions(
							null, ObjectEntryFolder.class.getName());

					return TransformUtil.transformToArray(
						ResourceActionsUtil.getResourceActions(
							ObjectEntryFolder.class.getName()),
						resourceAction -> HashMapBuilder.<String, Object>put(
							"guestUnsupported",
							guestUnsupportedActions.contains(resourceAction)
						).put(
							"key", resourceAction
						).put(
							"label",
							ResourceActionsUtil.getAction(
								httpServletRequest, resourceAction)
						).build(),
						Map.class);
				}
			).build()
		).put(
			"roles",
			() -> TransformUtil.transformToArray(
				RoleLocalServiceUtil.getGroupRolesAndTeamRoles(
					themeDisplay.getCompanyId(), null,
					Arrays.asList(
						RoleConstants.ADMINISTRATOR,
						DepotRolesConstants.ASSET_LIBRARY_OWNER),
					null, null,
					new int[] {
						RoleConstants.TYPE_REGULAR, RoleConstants.TYPE_DEPOT
					},
					0, 0, QueryUtil.ALL_POS, QueryUtil.ALL_POS),
				role -> HashMapBuilder.put(
					"key", role.getName()
				).put(
					"name", role.getTitle(themeDisplay.getLocale())
				).put(
					"type", String.valueOf(role.getType())
				).build(),
				Map.class)
		).build();
	}

	private JSONArray _getDepotEntriesJSONArray() {
		if (objectEntryFolder != null) {
			return _getDepotEntriesJSONArray(
				List.of(objectEntryFolder.getGroupId()));
		}

		return _getDepotEntriesJSONArray(
			TransformUtil.transform(
				depotEntryLocalService.getDepotEntries(
					themeDisplay.getCompanyId(), DepotConstants.TYPE_SPACE),
				DepotEntry::getGroupId));
	}

	private JSONArray _getDepotEntriesJSONArray(DropdownItem dropdownItem) {
		Map<String, Object> dropdownItemData =
			(HashMap<String, Object>)dropdownItem.get("data");

		long objectDefinitionId = GetterUtil.getLong(
			dropdownItemData.get("objectDefinitionId"));

		if (objectDefinitionId != 0) {
			return _getDepotEntriesJSONArray(objectDefinitionId);
		}

		return _getDepotEntriesJSONArray();
	}

	private JSONArray _getDepotEntriesJSONArray(List<Long> groupIds) {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		for (Long groupId : groupIds) {
			JSONObject jsonObject = _getJSONObject(groupId);

			if (jsonObject != null) {
				jsonArray.put(jsonObject);
			}
		}

		return jsonArray;
	}

	private JSONArray _getDepotEntriesJSONArray(long objectDefinitionId) {
		if (_isAcceptAllGroups(objectDefinitionId)) {
			return _getDepotEntriesJSONArray();
		}

		List<Long> acceptedGroupIds = _getAcceptedGroupIds(objectDefinitionId);

		if (acceptedGroupIds.isEmpty()) {
			return null;
		}

		if (objectEntryFolder != null) {
			if (!acceptedGroupIds.contains(objectEntryFolder.getGroupId())) {
				return null;
			}

			return _getDepotEntriesJSONArray(
				List.of(objectEntryFolder.getGroupId()));
		}

		return _getDepotEntriesJSONArray(acceptedGroupIds);
	}

	private Map<String, String> _getFileMimeTypeCssClasses() {
		return HashMapBuilder.put(
			"default", "file-icon-color-0"
		).putAll(
			_getFileMimeTypeValues(
				_dlConfiguration.codeFileMimeTypes(), "file-icon-color-7")
		).putAll(
			_getFileMimeTypeValues(
				_dlConfiguration.compressedFileMimeTypes(), "file-icon-color-1")
		).putAll(
			_getFileMimeTypeValues(
				ArrayUtil.append(
					_dlConfiguration.multimediaFileMimeTypes(),
					ContentTypes.
						APPLICATION_VND_LIFERAY_VIDEO_EXTERNAL_SHORTCUT_HTML),
				"file-icon-color-3")
		).putAll(
			_getFileMimeTypeValues(
				_dlConfiguration.presentationFileMimeTypes(),
				"file-icon-color-4")
		).putAll(
			_getFileMimeTypeValues(
				_dlConfiguration.spreadSheetFileMimeTypes(),
				"file-icon-color-2")
		).putAll(
			_getFileMimeTypeValues(
				_dlConfiguration.textFileMimeTypes(), "file-icon-color-6")
		).putAll(
			_getFileMimeTypeValues(
				_dlConfiguration.vectorialFileMimeTypes(), "file-icon-color-5")
		).build();
	}

	private Map<String, String> _getFileMimeTypeIcons() {
		return HashMapBuilder.put(
			"default", "document-default"
		).putAll(
			_getFileMimeTypeValues(
				_dlConfiguration.codeFileMimeTypes(), "document-code")
		).putAll(
			_getFileMimeTypeValues(
				_dlConfiguration.compressedFileMimeTypes(),
				"document-compressed")
		).putAll(
			_getFileMimeTypeValues(
				_dlConfiguration.presentationFileMimeTypes(),
				"document-presentation")
		).putAll(
			_getFileMimeTypeValues(
				_dlConfiguration.spreadSheetFileMimeTypes(), "document-table")
		).putAll(
			_getFileMimeTypeValues(
				_dlConfiguration.textFileMimeTypes(), "document-text")
		).putAll(
			_getFileMimeTypeValues(
				_dlConfiguration.vectorialFileMimeTypes(), "document-vector")
		).putAll(
			_getFileMimeTypeMultimediaCssClasses(
				ArrayUtil.append(
					_dlConfiguration.multimediaFileMimeTypes(),
					ContentTypes.
						APPLICATION_VND_LIFERAY_VIDEO_EXTERNAL_SHORTCUT_HTML))
		).build();
	}

	private Map<String, String> _getFileMimeTypeMultimediaCssClasses(
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

	private Map<String, String> _getFileMimeTypeValues(
		String[] mimeTypes, String value) {

		Map<String, String> fileMimeTypeValues = new HashMap<>();

		for (String mimeType : mimeTypes) {
			fileMimeTypeValues.put(mimeType, value);
		}

		return fileMimeTypeValues;
	}

	private JSONObject _getJSONObject(long groupId) {
		Group group = groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return null;
		}

		return JSONUtil.put(
			"externalReferenceCode", group.getExternalReferenceCode()
		).put(
			"groupId", group.getGroupId()
		).put(
			"name", group.getName(themeDisplay.getLocale())
		);
	}

	private String _getLayoutName() {
		Layout layout = themeDisplay.getLayout();

		if (layout == null) {
			return null;
		}

		return layout.getName(themeDisplay.getLocale(), true);
	}

	private ObjectEntryFolder _getObjectEntryFolder(
		long companyId, Object object) {

		if (object instanceof DepotEntry) {
			DepotEntry depotEntry = (DepotEntry)object;

			return ObjectEntryFolderLocalServiceUtil.
				fetchObjectEntryFolderByExternalReferenceCode(
					getRootObjectEntryFolderExternalReferenceCode(),
					depotEntry.getGroupId(), companyId);
		}
		else if (object instanceof ObjectEntryFolder) {
			return (ObjectEntryFolder)object;
		}

		return null;
	}

	private String _getParentObjectEntryFolderExternalReferenceCode() {
		if (objectEntryFolder == null) {
			return getRootObjectEntryFolderExternalReferenceCode();
		}

		return objectEntryFolder.getExternalReferenceCode();
	}

	private boolean _hasAddEntryPermission() {
		if (objectEntryFolder == null) {
			return true;
		}

		try {
			return _objectEntryFolderModelResourcePermission.contains(
				themeDisplay.getPermissionChecker(),
				objectEntryFolder.getObjectEntryFolderId(),
				ActionKeys.ADD_ENTRY);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return false;
	}

	private boolean _isAcceptAllGroups(long objectDefinitionId) {
		ObjectDefinitionSetting objectDefinitionSetting =
			_objectDefinitionSettingLocalService.fetchObjectDefinitionSetting(
				objectDefinitionId,
				ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS);

		if ((objectDefinitionSetting != null) &&
			GetterUtil.getBoolean(objectDefinitionSetting.getValue())) {

			return true;
		}

		objectDefinitionSetting =
			_objectDefinitionSettingLocalService.fetchObjectDefinitionSetting(
				objectDefinitionId,
				ObjectDefinitionSettingConstants.NAME_ACCEPTED_GROUP_IDS);

		if ((objectDefinitionSetting == null) ||
			Validator.isNull(objectDefinitionSetting.getValue())) {

			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseSectionDisplayContext.class);

	private static final List<Integer> _statuses = Arrays.asList(
		WorkflowConstants.STATUS_APPROVED, WorkflowConstants.STATUS_DRAFT,
		WorkflowConstants.STATUS_EXPIRED);

	private final DLConfiguration _dlConfiguration;
	private final ObjectDefinitionService _objectDefinitionService;
	private final ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;
	private final ModelResourcePermission<ObjectEntryFolder>
		_objectEntryFolderModelResourcePermission;

}