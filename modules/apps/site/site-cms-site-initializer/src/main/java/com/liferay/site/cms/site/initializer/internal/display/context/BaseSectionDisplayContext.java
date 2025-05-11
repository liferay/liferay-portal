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
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Marco Galluzzi
 */
public abstract class BaseSectionDisplayContext {

	public BaseSectionDisplayContext(
		DepotEntryLocalService depotEntryLocalService,
		GroupLocalService groupLocalService,
		HttpServletRequest httpServletRequest, Language language,
		ObjectDefinitionService objectDefinitionService,
		ObjectDefinitionSettingLocalService
			objectDefinitionSettingLocalService) {

		_depotEntryLocalService = depotEntryLocalService;
		_groupLocalService = groupLocalService;

		this.httpServletRequest = httpServletRequest;
		this.language = language;

		_objectDefinitionService = objectDefinitionService;
		_objectDefinitionSettingLocalService =
			objectDefinitionSettingLocalService;

		Object object = httpServletRequest.getAttribute(
			InfoDisplayWebKeys.INFO_ITEM);

		_objectEntryFolder =
			object instanceof ObjectEntryFolder ? (ObjectEntryFolder)object :
				null;

		themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getAPIURL() {
		String[] objectFolderExternalReferenceCodes =
			getObjectFolderExternalReferenceCodes();

		StringBundler sb = new StringBundler(10);

		sb.append("/o/search/v1.0/search?emptySearch=true&filter=(");

		if (_objectEntryFolder != null) {
			sb.append("folderId eq");
			sb.append(_objectEntryFolder.getObjectEntryFolderId());
			sb.append("and");
		}

		sb.append("(objectFolderExternalReferenceCode in ('");
		sb.append(StringUtil.merge(objectFolderExternalReferenceCodes, "','"));
		sb.append("')");

		String cmsSectionFilterString = getCMSSectionFilterString();

		if (Validator.isNotNull(cmsSectionFilterString)) {
			sb.append(" or ");
			sb.append(cmsSectionFilterString);
		}

		sb.append("))&nestedFields=embedded,file.thumbnailURL");

		return sb.toString();
	}

	public List<DropdownItem> getBulkActionDropdownItems() {
		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				"#", "document", "sampleBulkAction",
				LanguageUtil.get(httpServletRequest, "label"), null, null,
				null));
	}

	public abstract CreationMenu getCreationMenu();

	public abstract Map<String, Object> getEmptyState();

	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				StringBundler.concat(
					themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
					GroupConstants.CMS_FRIENDLY_URL,
					"/edit_content_item?className={entryClassName}&",
					"objectEntryId={embedded.id}&redirect=",
					themeDisplay.getURLCurrent()),
				"pencil", "edit", LanguageUtil.get(httpServletRequest, "edit"),
				"get", "update", null),
			new FDSActionDropdownItem(
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
							"/add_structured_content_item?objectDefinitionId=",
							objectDefinition.getObjectDefinitionId(), "&plid=",
							themeDisplay.getPlid()));
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
			Group group = _groupLocalService.fetchGroup(
				depotEntry.getGroupId());

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

	protected final HttpServletRequest httpServletRequest;
	protected final Language language;
	protected final ThemeDisplay themeDisplay;

	private JSONArray _getDepotEntriesJSONArray(
		ObjectDefinition objectDefinition) {

		ObjectDefinitionSetting objectDefinitionSetting =
			_objectDefinitionSettingLocalService.fetchObjectDefinitionSetting(
				objectDefinition.getObjectDefinitionId(),
				ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS);

		if (objectDefinitionSetting != null) {
			return getDepotEntriesJSONArray(
				_depotEntryLocalService.getDepotEntries(
					QueryUtil.ALL_POS, QueryUtil.ALL_POS));
		}

		objectDefinitionSetting =
			_objectDefinitionSettingLocalService.fetchObjectDefinitionSetting(
				objectDefinition.getObjectDefinitionId(),
				ObjectDefinitionSettingConstants.NAME_ACCEPTED_GROUP_IDS);

		if ((objectDefinitionSetting == null) ||
			Validator.isNull(objectDefinitionSetting.getValue())) {

			return getDepotEntriesJSONArray(
				_depotEntryLocalService.getDepotEntries(
					QueryUtil.ALL_POS, QueryUtil.ALL_POS));
		}

		return getDepotEntriesJSONArray(
			TransformUtil.transform(
				StringUtil.split(objectDefinitionSetting.getValue()),
				groupId -> _depotEntryLocalService.fetchGroupDepotEntry(
					GetterUtil.getLong(groupId))));
	}

	private final DepotEntryLocalService _depotEntryLocalService;
	private final GroupLocalService _groupLocalService;
	private final ObjectDefinitionService _objectDefinitionService;
	private final ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;
	private final ObjectEntryFolder _objectEntryFolder;

}