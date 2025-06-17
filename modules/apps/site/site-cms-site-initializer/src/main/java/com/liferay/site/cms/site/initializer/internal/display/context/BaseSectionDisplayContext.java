/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
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
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.cms.site.initializer.internal.util.ActionUtil;

import jakarta.portlet.ActionRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Marco Galluzzi
 */
public abstract class BaseSectionDisplayContext {

	public BaseSectionDisplayContext(
		DepotEntryLocalService depotEntryLocalService,
		GroupLocalService groupLocalService,
		HttpServletRequest httpServletRequest, Language language,
		ObjectDefinitionService objectDefinitionService,
		ObjectDefinitionSettingLocalService objectDefinitionSettingLocalService,
		Portal portal) {

		this.depotEntryLocalService = depotEntryLocalService;
		this.groupLocalService = groupLocalService;
		this.httpServletRequest = httpServletRequest;
		this.language = language;

		_objectDefinitionService = objectDefinitionService;
		_objectDefinitionSettingLocalService =
			objectDefinitionSettingLocalService;

		Object object = httpServletRequest.getAttribute(
			InfoDisplayWebKeys.INFO_ITEM);

		objectEntryFolder =
			object instanceof ObjectEntryFolder ? (ObjectEntryFolder)object :
				null;

		this.portal = portal;

		themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getAPIURL() {
		StringBundler sb = new StringBundler(4);

		sb.append("/o/search/v1.0/search?emptySearch=true&filter=");

		if (objectEntryFolder != null) {
			sb.append("folderId eq ");
			sb.append(objectEntryFolder.getObjectEntryFolderId());
		}
		else {
			sb.append(getCMSSectionFilterString());
		}

		sb.append("&nestedFields=embedded,file.thumbnailURL");

		return sb.toString();
	}

	public List<DropdownItem> getBulkActionDropdownItems() {
		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				"#", "document", "sampleBulkAction",
				LanguageUtil.get(httpServletRequest, "label"), null, null,
				null));
	}

	public CreationMenu getCreationMenu() {
		return new CreationMenu() {
			{
				if (getRootObjectEntryFolderExternalReferenceCode() != null) {
					addPrimaryDropdownItem(
						dropdownItem -> {
							dropdownItem.putData("action", "createFolder");
							dropdownItem.putData(
								"assetLibraries", _getDepotEntriesJSONArray());
							dropdownItem.putData(
								"baseAssetLibraryViewURL",
								ActionUtil.getBaseSpaceURL(themeDisplay));
							dropdownItem.putData(
								"baseFolderViewURL",
								ActionUtil.getBaseViewFolderURL(themeDisplay));
							dropdownItem.putData(
								"parentObjectEntryFolderExternalReferenceCode",
								_getParentObjectEntryFolderExternalReferenceCode());
							dropdownItem.setIcon("folder");
							dropdownItem.setLabel(
								language.get(httpServletRequest, "folder"));
						});
				}

				if (!Objects.equals(
						getRootObjectEntryFolderExternalReferenceCode(),
						ObjectEntryFolderConstants.
							EXTERNAL_REFERENCE_CODE_CONTENTS)) {

					addPrimaryDropdownItem(
						dropdownItem -> {
							dropdownItem.putData(
								"action", "uploadMultipleFiles");
							dropdownItem.putData(
								"assetLibraries", _getDepotEntriesJSONArray());
							dropdownItem.putData(
								"parentObjectEntryFolderExternalReferenceCode",
								_getParentObjectEntryFolderExternalReferenceCode());
							dropdownItem.setIcon("upload-multiple");
							dropdownItem.setLabel(
								language.get(
									httpServletRequest, "multiple-files"));
						});
				}

				addStructureContentDropdownItems(this);
			}
		};
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
				language.get(httpServletRequest, "permissions"), "get", null,
				"modal-permissions"),
			new FDSActionDropdownItem(
				language.get(
					httpServletRequest,
					"are-you-sure-you-want-to-delete-this-entry"),
				null, "trash", "delete",
				language.get(httpServletRequest, "delete"), "delete", "delete",
				"headless"));
	}

	protected void addStructureContentDropdownItems(CreationMenu creationMenu) {
		for (ObjectDefinition objectDefinition :
				_objectDefinitionService.getCMSObjectDefinitions(
					themeDisplay.getCompanyId(),
					getObjectFolderExternalReferenceCodes())) {

			creationMenu.addPrimaryDropdownItem(
				dropdownItem -> {
					dropdownItem.putData("action", "createAsset");
					dropdownItem.putData(
						"assetLibraries",
						_getDepotEntriesJSONArray(objectDefinition));
					dropdownItem.putData(
						"redirect",
						StringBundler.concat(
							themeDisplay.getPortalURL(),
							themeDisplay.getPathMain(),
							GroupConstants.CMS_FRIENDLY_URL,
							"/add_structured_content_item?",
							"objectDefinitionId=",
							objectDefinition.getObjectDefinitionId(),
							"&objectEntryFolderExternalReferenceCode=",
							_getObjectEntryFolderExternalReferenceCode(
								objectDefinition),
							"&plid=", themeDisplay.getPlid(), "&redirect=",
							themeDisplay.getURLCurrent()));
					dropdownItem.putData(
						"title",
						objectDefinition.getLabel(themeDisplay.getLocale()));
					dropdownItem.setIcon("forms");
					dropdownItem.setLabel(
						objectDefinition.getLabel(themeDisplay.getLocale()));
				});
		}
	}

	protected abstract String getCMSSectionFilterString();

	protected JSONArray getDepotEntriesJSONArray(
		List<DepotEntry> depotEntries) {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		for (DepotEntry depotEntry : depotEntries) {
			Group group = groupLocalService.fetchGroup(depotEntry.getGroupId());

			if (group != null) {
				jsonArray.put(
					JSONUtil.put(
						"groupId", group.getGroupId()
					).put(
						"name", group.getName(themeDisplay.getLocale())
					));
			}
		}

		return jsonArray;
	}

	protected abstract String[] getObjectFolderExternalReferenceCodes();

	protected abstract String getRootObjectEntryFolderExternalReferenceCode();

	protected final DepotEntryLocalService depotEntryLocalService;
	protected final GroupLocalService groupLocalService;
	protected final HttpServletRequest httpServletRequest;
	protected final Language language;
	protected final ObjectEntryFolder objectEntryFolder;
	protected final Portal portal;
	protected final ThemeDisplay themeDisplay;

	private JSONArray _getDepotEntriesJSONArray() {
		if (objectEntryFolder == null) {
			return getDepotEntriesJSONArray(
				depotEntryLocalService.getDepotEntries(
					QueryUtil.ALL_POS, QueryUtil.ALL_POS));
		}

		Group group = groupLocalService.fetchGroup(
			objectEntryFolder.getGroupId());

		return JSONUtil.putAll(
			JSONUtil.put(
				"groupId", group.getGroupId()
			).put(
				"name", group.getName(themeDisplay.getLocale())
			));
	}

	private JSONArray _getDepotEntriesJSONArray(
		ObjectDefinition objectDefinition) {

		ObjectDefinitionSetting objectDefinitionSetting =
			_objectDefinitionSettingLocalService.fetchObjectDefinitionSetting(
				objectDefinition.getObjectDefinitionId(),
				ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS);

		if (objectDefinitionSetting != null) {
			return getDepotEntriesJSONArray(
				depotEntryLocalService.getDepotEntries(
					QueryUtil.ALL_POS, QueryUtil.ALL_POS));
		}

		objectDefinitionSetting =
			_objectDefinitionSettingLocalService.fetchObjectDefinitionSetting(
				objectDefinition.getObjectDefinitionId(),
				ObjectDefinitionSettingConstants.NAME_ACCEPTED_GROUP_IDS);

		if ((objectDefinitionSetting == null) ||
			Validator.isNull(objectDefinitionSetting.getValue())) {

			return getDepotEntriesJSONArray(
				depotEntryLocalService.getDepotEntries(
					QueryUtil.ALL_POS, QueryUtil.ALL_POS));
		}

		return getDepotEntriesJSONArray(
			TransformUtil.transform(
				StringUtil.split(objectDefinitionSetting.getValue()),
				groupId -> depotEntryLocalService.fetchGroupDepotEntry(
					GetterUtil.getLong(groupId))));
	}

	private String _getObjectEntryFolderExternalReferenceCode(
		ObjectDefinition objectDefinition) {

		if (objectEntryFolder != null) {
			return objectEntryFolder.getExternalReferenceCode();
		}

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

	private String _getParentObjectEntryFolderExternalReferenceCode() {
		if (objectEntryFolder == null) {
			return getRootObjectEntryFolderExternalReferenceCode();
		}

		return objectEntryFolder.getExternalReferenceCode();
	}

	private final ObjectDefinitionService _objectDefinitionService;
	private final ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

}