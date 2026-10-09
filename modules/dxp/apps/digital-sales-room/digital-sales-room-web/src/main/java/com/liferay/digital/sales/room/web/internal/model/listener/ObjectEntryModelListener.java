/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.sales.room.web.internal.model.listener;

import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.digital.sales.room.constants.DSRFolderConstants;
import com.liferay.digital.sales.room.thread.local.DSRRoomThreadLocal;
import com.liferay.digital.sales.room.util.DSRRoomUtil;
import com.liferay.digital.sales.room.web.internal.background.task.DSRAnalyticsChannelBackgroundTaskExecutor;
import com.liferay.digital.sales.room.web.internal.util.DSRUtil;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.UserIdStrategy;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.util.LayoutServiceContextHelper;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskContextMapConstants;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.liveusers.LiveUsers;
import com.liferay.portal.security.permission.PermissionCacheUtil;
import com.liferay.sites.kernel.util.Sites;

import java.io.File;
import java.io.Serializable;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Stefano Motta
 */
@Component(service = ModelListener.class)
public class ObjectEntryModelListener extends BaseModelListener<ObjectEntry> {

	@Override
	public void onAfterCreate(ObjectEntry objectEntry)
		throws ModelListenerException {

		try {
			_onAfterCreate(objectEntry);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Override
	public void onAfterRemove(ObjectEntry objectEntry)
		throws ModelListenerException {

		try {
			_onAfterRemove(objectEntry);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Override
	public void onAfterUpdate(
			ObjectEntry originalObjectEntry, ObjectEntry objectEntry)
		throws ModelListenerException {

		try {
			_onAfterUpdate(originalObjectEntry, objectEntry);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Override
	public void onBeforeCreate(ObjectEntry objectEntry)
		throws ModelListenerException {

		try {
			_onBeforeCreate(objectEntry);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Override
	public void onBeforeUpdate(
			ObjectEntry originalObjectEntry, ObjectEntry objectEntry)
		throws ModelListenerException {

		try {
			_onBeforeUpdate(originalObjectEntry, objectEntry);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	private void _copyFileEntries(long[] fileEntryIds, Group group)
		throws Exception {

		long folderId = 0;

		DLFolder dlFolder =
			_dlFolderLocalService.fetchDLFolderByExternalReferenceCode(
				DSRFolderConstants.EXTERNAL_REFERENCE_CODE_DSR_DOCUMENTS,
				group.getGroupId());

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(group.getCompanyId());
		serviceContext.setScopeGroupId(group.getGroupId());

		if (dlFolder == null) {
			Folder folder = _dlAppService.addFolder(
				DSRFolderConstants.EXTERNAL_REFERENCE_CODE_DSR_DOCUMENTS,
				group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				"Documents", null, serviceContext);

			folderId = folder.getFolderId();
		}
		else {
			folderId = dlFolder.getFolderId();
		}

		Map<String, Long> sourceFileEntries = new HashMap<>();

		for (long fileEntryId : fileEntryIds) {
			FileEntry fileEntry = _dlAppService.getFileEntry(fileEntryId);

			sourceFileEntries.put(fileEntry.getTitle(), fileEntryId);
		}

		for (FileEntry fileEntry :
				_dlAppService.getFileEntries(group.getGroupId(), folderId)) {

			if (sourceFileEntries.remove(fileEntry.getTitle()) == null) {
				_dlAppService.deleteFileEntry(fileEntry.getFileEntryId());
			}
		}

		for (long fileEntryId : sourceFileEntries.values()) {
			_dlAppService.copyFileEntry(
				fileEntryId, folderId, group.getGroupId(),
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT,
				new long[] {group.getGroupId()}, serviceContext);
		}
	}

	private void _duplicateGroup(
			Company company, long[] fileEntryIds, Group group,
			ObjectDefinition objectDefinition, long objectEntryId, User user)
		throws Exception {

		Group sourceGroup = _groupLocalService.fetchGroup(
			group.getCompanyId(),
			_classNameLocalService.getClassNameId(
				objectDefinition.getClassName()),
			objectEntryId);

		if (sourceGroup == null) {
			throw new NoSuchGroupException(
				StringBundler.concat(
					"Unable to duplicate room ", objectEntryId,
					" because its site does not exist"));
		}

		Map<String, String[]> parameterMap = HashMapBuilder.put(
			PortletDataHandlerKeys.COMMENTS,
			new String[] {Boolean.FALSE.toString()}
		).put(
			PortletDataHandlerKeys.DATA_STRATEGY,
			new String[] {PortletDataHandlerKeys.DATA_STRATEGY_MIRROR}
		).put(
			PortletDataHandlerKeys.DELETE_MISSING_LAYOUTS,
			new String[] {Boolean.FALSE.toString()}
		).put(
			PortletDataHandlerKeys.DELETE_PORTLET_DATA,
			new String[] {Boolean.FALSE.toString()}
		).put(
			PortletDataHandlerKeys.FAVICON,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.IGNORE_LAST_PUBLISH_DATE,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.LAYOUT_SET_SETTINGS,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.LAYOUTS_IMPORT_MODE,
			new String[] {
				PortletDataHandlerKeys.
					LAYOUTS_IMPORT_MODE_CREATED_FROM_PROTOTYPE
			}
		).put(
			PortletDataHandlerKeys.LOGO, new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PERMISSIONS,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION_ALL,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA_ALL,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_SETUP_ALL,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.RATINGS,
			new String[] {Boolean.FALSE.toString()}
		).put(
			PortletDataHandlerKeys.THEME_REFERENCE,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.UPDATE_LAST_PUBLISH_DATE,
			new String[] {Boolean.FALSE.toString()}
		).put(
			PortletDataHandlerKeys.USER_ID_STRATEGY,
			new String[] {UserIdStrategy.CURRENT_USER_ID}
		).build();

		try (AutoCloseable autoCloseable =
				_layoutServiceContextHelper.getServiceContextAutoCloseable(
					company, user)) {

			_importLayouts(parameterMap, false, sourceGroup, group, user);
			_importLayouts(parameterMap, true, sourceGroup, group, user);

			// Copy file entries after importing layouts

			_copyFileEntries(fileEntryIds, group);

			_updateFragmentEntryLink(group);
		}
	}

	private User _getAdministratorUser(long companyId) throws Exception {
		List<User> users = _userLocalService.getUsersByRoleName(
			companyId, RoleConstants.ADMINISTRATOR, 0, 1);

		return users.get(0);
	}

	private void _importLayouts(
			Map<String, String[]> parameterMap, boolean privateLayout,
			Group sourceGroup, Group targetGroup, User user)
		throws Exception {

		long[] layoutIds = _exportImportHelper.getAllLayoutIds(
			sourceGroup.getGroupId(), privateLayout);

		if (ArrayUtil.isEmpty(layoutIds)) {
			return;
		}

		File file = null;

		try {
			file = _exportImportLocalService.exportLayoutsAsFile(
				_exportImportConfigurationLocalService.
					addDraftExportImportConfiguration(
						user.getUserId(),
						ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT,
						ExportImportConfigurationSettingsMapFactoryUtil.
							buildExportLayoutSettingsMap(
								user, sourceGroup.getGroupId(), privateLayout,
								layoutIds, parameterMap)));

			_exportImportLocalService.importLayouts(
				_exportImportConfigurationLocalService.
					addDraftExportImportConfiguration(
						user.getUserId(),
						ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT,
						ExportImportConfigurationSettingsMapFactoryUtil.
							buildImportLayoutSettingsMap(
								user, targetGroup.getGroupId(), privateLayout,
								null, parameterMap)),
				file);
		}
		finally {
			if (file != null) {
				file.delete();
			}
		}
	}

	private void _onAfterCreate(ObjectEntry objectEntry) throws Exception {
		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();

		if (!Objects.equals(
				objectDefinition.getExternalReferenceCode(), "L_DSR_ROOM")) {

			return;
		}

		Company company = _companyLocalService.getCompany(
			objectEntry.getCompanyId());
		Map<String, Serializable> values = objectEntry.getValues();

		// The group is added by DSRRoomObjectEntryValuesContributor before the
		// object entry exists, so link it to the object entry now

		Group group = _groupLocalService.getGroup(
			MapUtil.getLong(values, "siteId"));

		group.setClassPK(objectEntry.getObjectEntryId());
		group.setGroupKey(String.valueOf(objectEntry.getObjectEntryId()));

		group = _groupLocalService.updateGroup(group);

		LayoutSetPrototype layoutSetPrototype = null;
		User user = _userLocalService.getUser(objectEntry.getUserId());

		try (AutoCloseable autoCloseable =
				_layoutServiceContextHelper.getServiceContextAutoCloseable(
					company, user)) {

			Role role = _roleLocalService.getRole(
				group.getCompanyId(), RoleConstants.SITE_OWNER);

			_userGroupRoleLocalService.addUserGroupRoles(
				user.getUserId(), group.getGroupId(),
				new long[] {role.getRoleId()});

			_userLocalService.addGroupUsers(
				group.getGroupId(), new long[] {user.getUserId()});

			LiveUsers.joinGroup(
				group.getCompanyId(), group.getGroupId(), user.getUserId());

			String siteTemplateKey = MapUtil.getString(
				values, "siteTemplateKey");

			if (Validator.isNull(siteTemplateKey)) {
				siteTemplateKey = "L_DSR_LAYOUT_SET_PROTOTYPE";
			}

			layoutSetPrototype =
				_layoutSetPrototypeLocalService.
					getLayoutSetPrototypeByUuidAndCompanyId(
						siteTemplateKey, company.getCompanyId());
		}

		User administratorUser = _getAdministratorUser(company.getCompanyId());

		try (AutoCloseable autoCloseable =
				_layoutServiceContextHelper.getServiceContextAutoCloseable(
					company, administratorUser)) {

			long sourceObjectEntryId = DSRRoomThreadLocal.getObjectEntryId();

			if (sourceObjectEntryId == 0) {
				_sites.updateLayoutSetPrototypesLinks(
					group, layoutSetPrototype.getLayoutSetPrototypeId(), 0,
					false, false);

				_updateFragmentEntryLink(group);
			}
			else {
				_duplicateGroup(
					company, DSRRoomThreadLocal.getFileEntryIds(), group,
					objectDefinition, sourceObjectEntryId, administratorUser);
			}

			if (_analyticsSettingsManager.isAnalyticsEnabled(
					company.getCompanyId())) {

				_backgroundTaskManager.addBackgroundTask(
					objectEntry.getUserId(),
					BackgroundTaskConstants.GROUP_ID_DEFAULT,
					DSRAnalyticsChannelBackgroundTaskExecutor.class.getName(),
					DSRAnalyticsChannelBackgroundTaskExecutor.class.getName(),
					HashMapBuilder.<String, Serializable>put(
						BackgroundTaskContextMapConstants.DELETE_ON_SUCCESS,
						true
					).put(
						"objectDefinitionId",
						objectDefinition.getObjectDefinitionId()
					).build(),
					new ServiceContext());
			}
		}
		catch (Exception exception) {

			// LPS-169057

			PermissionCacheUtil.clearCache(objectEntry.getUserId());

			throw exception;
		}
	}

	private void _onAfterRemove(ObjectEntry objectEntry)
		throws PortalException {

		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();

		if (!Objects.equals(
				objectDefinition.getExternalReferenceCode(), "L_DSR_ROOM")) {

			return;
		}

		Group group = _groupLocalService.fetchGroup(
			objectEntry.getCompanyId(),
			_classNameLocalService.getClassNameId(
				objectDefinition.getClassName()),
			objectEntry.getObjectEntryId());

		if (group != null) {
			_groupLocalService.deleteGroup(group);
		}
	}

	private void _onAfterUpdate(
			ObjectEntry originalObjectEntry, ObjectEntry objectEntry)
		throws Exception {

		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();

		if (!Objects.equals(
				objectDefinition.getExternalReferenceCode(), "L_DSR_ROOM") ||
			(Objects.equals(
				MapUtil.getString(originalObjectEntry.getValues(), "name"),
				MapUtil.getString(objectEntry.getValues(), "name")) &&
			 Objects.equals(
				 MapUtil.getString(
					 originalObjectEntry.getValues(), "friendlyURL"),
				 MapUtil.getString(objectEntry.getValues(), "friendlyURL")))) {

			return;
		}

		Group group = _groupLocalService.fetchGroup(
			objectEntry.getCompanyId(),
			_classNameLocalService.getClassNameId(
				objectDefinition.getClassName()),
			objectEntry.getObjectEntryId());

		if (group == null) {
			return;
		}

		String friendlyURL = DSRUtil.getFriendlyURL(objectEntry.getValues());
		String name = MapUtil.getString(objectEntry.getValues(), "name");
		Map<Locale, String> nameMap = group.getNameMap();

		if (Objects.equals(friendlyURL, group.getFriendlyURL()) &&
			Objects.equals(name, nameMap.get(LocaleUtil.getDefault()))) {

			return;
		}

		nameMap.put(LocaleUtil.getDefault(), name);

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(objectEntry.getCompanyId());
		serviceContext.setUserId(objectEntry.getUserId());

		group = _groupLocalService.updateGroup(
			group.getGroupId(), group.getParentGroupId(), nameMap,
			group.getDescriptionMap(), group.getType(), group.getTypeSettings(),
			group.isManualMembership(), group.getMembershipRestriction(),
			friendlyURL, group.isInheritContent(), group.isActive(),
			serviceContext);

		friendlyURL = StringUtil.removeFirst(group.getFriendlyURL(), "/");

		if (Objects.equals(
				friendlyURL,
				MapUtil.getString(objectEntry.getValues(), "friendlyURL"))) {

			return;
		}

		_objectEntryLocalService.partialUpdateObjectEntry(
			objectEntry.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"friendlyURL", friendlyURL
			).build(),
			new ServiceContext());
	}

	private void _onBeforeCreate(ObjectEntry objectEntry) {
		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();

		if (!Objects.equals(
				objectDefinition.getExternalReferenceCode(), "L_DSR_ROOM")) {

			return;
		}

		if (DSRUtil.isExpired()) {
			throw new UnsupportedOperationException(
				"Unable to create a digital sales room because the license " +
					"has expired");
		}

		if (objectEntry.getExpirationDate() != null) {
			throw new UnsupportedOperationException();
		}
	}

	private void _onBeforeUpdate(
			ObjectEntry originalObjectEntry, ObjectEntry objectEntry)
		throws Exception {

		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();

		if (!Objects.equals(
				objectDefinition.getExternalReferenceCode(), "L_DSR_ROOM")) {

			return;
		}

		if ((objectEntry.getStatus() == WorkflowConstants.STATUS_EXPIRED) ||
			(objectEntry.getExpirationDate() != null)) {

			throw new UnsupportedOperationException();
		}

		Map<String, Serializable> originalValues =
			originalObjectEntry.getValues();
		Map<String, Serializable> values = objectEntry.getValues();

		for (Map.Entry<String, Serializable> entry : values.entrySet()) {
			String name = entry.getKey();

			if (Objects.equals(name, "archiveDate") ||
				Objects.equals(name, "initialized") ||
				Objects.equals(name, "roomStatus")) {

				continue;
			}

			if (!Objects.equals(entry.getValue(), originalValues.get(name))) {
				DSRRoomUtil.checkPermission(
					originalObjectEntry,
					PermissionThreadLocal.getPermissionChecker(),
					ActionKeys.UPDATE);

				return;
			}
		}
	}

	private void _updateFragmentEntryLink(Group group) {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				group.getGroupId(), "digital-sales-room-master");

		if (layoutPageTemplateEntry == null) {
			return;
		}

		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				group.getGroupId(), layoutPageTemplateEntry.getPlid());

		for (FragmentEntryLink fragmentEntryLink : fragmentEntryLinks) {
			if (!Objects.equals(
					fragmentEntryLink.getRendererKey(), _RENDERER_KEY)) {

				continue;
			}

			JSONObject jsonObject = _jsonFactory.safeCreateJSONObject(
				fragmentEntryLink.getEditableValues());

			jsonObject = jsonObject.getJSONObject(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR);

			if (jsonObject == null) {
				continue;
			}

			jsonObject.put("source", "");

			try {
				_fragmentEntryLinkLocalService.updateFragmentEntryLink(
					fragmentEntryLink.getUserId(),
					fragmentEntryLink.getFragmentEntryLinkId(),
					jsonObject.toString(), false);
			}
			catch (PortalException portalException) {
				_log.error(portalException);
			}
		}
	}

	private static final String _RENDERER_KEY =
		"com.liferay.fragment.renderer.menu.display.internal." +
			"MenuDisplayFragmentRenderer";

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryModelListener.class);

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile AnalyticsSettingsManager _analyticsSettingsManager;

	@Reference
	private BackgroundTaskManager _backgroundTaskManager;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private DLFolderLocalService _dlFolderLocalService;

	@Reference
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Reference
	private ExportImportHelper _exportImportHelper;

	@Reference
	private ExportImportLocalService _exportImportLocalService;

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private LayoutServiceContextHelper _layoutServiceContextHelper;

	@Reference
	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private Sites _sites;

	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}