/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.model.FDSActionDropdownItemBuilder;
import com.liferay.frontend.data.set.model.FDSActionDropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.model.ObjectDefinitionSetting;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
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
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
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
import java.util.Map;

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

		StringBundler sb = new StringBundler(10);

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

		return sb.toString();
	}

	public CreationMenu getCreationMenu(
		List<DropdownItem> dropdownItems, HttpServletRequest httpServletRequest,
		String rootObjectEntryFolderExternalReferenceCode) {

		return new CreationMenu() {
			{
				if (_hasAddEntryPermission(
						httpServletRequest,
						rootObjectEntryFolderExternalReferenceCode)) {

					for (DropdownItem dropdownItem : dropdownItems) {
						JSONArray depotEntriesJSONArray =
							_getDepotEntriesJSONArray(
								dropdownItem, httpServletRequest,
								rootObjectEntryFolderExternalReferenceCode);

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
				List.of(objectEntryFolder.getGroupId()), httpServletRequest);
		}

		return _getDepotEntriesJSONArray(
			TransformUtil.transform(
				_depotEntryLocalService.getDepotEntries(
					themeDisplay.getCompanyId(), DepotConstants.TYPE_SPACE),
				DepotEntry::getGroupId),
			httpServletRequest);
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
				"update", null,
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
				LanguageUtil.get(httpServletRequest, "view"), null, "get",
				null),
			new FDSActionDropdownItem(
				StringPool.BLANK, "view", "view-file",
				LanguageUtil.get(httpServletRequest, "view"), null, "get",
				null),
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
			new FDSActionDropdownItem(
				null, "copy", "copy",
				_language.get(httpServletRequest, "copy-to"), null, null, null),
			new FDSActionDropdownItem(
				null, "move-folder", "move",
				_language.get(httpServletRequest, "move"), null, null, null),
			_getPermissionsFDSActionDropdownItem(
				httpServletRequest, themeDisplay),
			new FDSActionDropdownItem(
				null, "trash", "delete",
				_language.get(httpServletRequest, "delete"), null, "delete",
				null));
	}

	private List<Long> _getAcceptedGroupIds(long objectDefinitionId) {
		List<Long> acceptedGroupIds = new ArrayList<>();

		ObjectDefinitionSetting objectDefinitionSetting =
			_objectDefinitionSettingLocalService.fetchObjectDefinitionSetting(
				objectDefinitionId,
				ObjectDefinitionSettingConstants.NAME_ACCEPTED_GROUP_IDS);

		for (String groupId :
				StringUtil.split(objectDefinitionSetting.getValue())) {

			DepotEntry depotEntry =
				_depotEntryLocalService.fetchGroupDepotEntry(
					GetterUtil.getLong(groupId));

			if (depotEntry != null) {
				acceptedGroupIds.add(depotEntry.getGroupId());
			}
		}

		return acceptedGroupIds;
	}

	private JSONArray _getDepotEntriesJSONArray(
		DropdownItem dropdownItem, HttpServletRequest httpServletRequest,
		String rootObjectEntryFolderExternalReferenceCode) {

		Map<String, Object> dropdownItemData =
			(HashMap<String, Object>)dropdownItem.get("data");

		long objectDefinitionId = GetterUtil.getLong(
			dropdownItemData.get("objectDefinitionId"));

		if (objectDefinitionId != 0) {
			return _getDepotEntriesJSONArray(
				httpServletRequest, objectDefinitionId,
				rootObjectEntryFolderExternalReferenceCode);
		}

		return getDepotEntriesJSONArray(
			httpServletRequest, rootObjectEntryFolderExternalReferenceCode);
	}

	private JSONArray _getDepotEntriesJSONArray(
		HttpServletRequest httpServletRequest, long objectDefinitionId,
		String rootObjectEntryFolderExternalReferenceCode) {

		if (_isAcceptAllGroups(objectDefinitionId)) {
			return getDepotEntriesJSONArray(
				httpServletRequest, rootObjectEntryFolderExternalReferenceCode);
		}

		List<Long> acceptedGroupIds = _getAcceptedGroupIds(objectDefinitionId);

		if (acceptedGroupIds.isEmpty()) {
			return null;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		ObjectEntryFolder objectEntryFolder = _getObjectEntryFolder(
			themeDisplay.getCompanyId(),
			httpServletRequest.getAttribute(InfoDisplayWebKeys.INFO_ITEM),
			rootObjectEntryFolderExternalReferenceCode);

		if (objectEntryFolder != null) {
			if (!acceptedGroupIds.contains(objectEntryFolder.getGroupId())) {
				return null;
			}

			return _getDepotEntriesJSONArray(
				List.of(objectEntryFolder.getGroupId()), httpServletRequest);
		}

		return _getDepotEntriesJSONArray(acceptedGroupIds, httpServletRequest);
	}

	private JSONArray _getDepotEntriesJSONArray(
		List<Long> groupIds, HttpServletRequest httpServletRequest) {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		for (Long groupId : groupIds) {
			JSONObject jsonObject = _getJSONObject(groupId, httpServletRequest);

			if (jsonObject != null) {
				jsonArray.put(jsonObject);
			}
		}

		return jsonArray;
	}

	private JSONObject _getJSONObject(
		long groupId, HttpServletRequest httpServletRequest) {

		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return null;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return JSONUtil.put(
			"externalReferenceCode", group.getExternalReferenceCode()
		).put(
			"groupId", group.getGroupId()
		).put(
			"name", group.getName(themeDisplay.getLocale())
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

	private boolean _hasAddEntryPermission(
		HttpServletRequest httpServletRequest,
		String rootObjectEntryFolderExternalReferenceCode) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		ObjectEntryFolder objectEntryFolder = _getObjectEntryFolder(
			themeDisplay.getCompanyId(),
			httpServletRequest.getAttribute(InfoDisplayWebKeys.INFO_ITEM),
			rootObjectEntryFolderExternalReferenceCode);

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