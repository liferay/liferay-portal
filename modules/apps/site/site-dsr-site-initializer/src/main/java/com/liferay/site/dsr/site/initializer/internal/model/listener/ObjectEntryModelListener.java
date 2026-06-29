/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.internal.model.listener;

import com.liferay.analytics.settings.rest.dto.v1_0.Channel;
import com.liferay.analytics.settings.rest.dto.v1_0.DataSource;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.analytics.settings.rest.resource.v1_0.ChannelResource;
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
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.GroupModel;
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
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.transaction.TransactionCommitCallbackUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.liveusers.LiveUsers;
import com.liferay.portal.security.permission.PermissionCacheUtil;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.site.dsr.site.initializer.constants.DSRFolderConstants;
import com.liferay.site.dsr.site.initializer.thread.local.DSRRoomThreadLocal;
import com.liferay.site.dsr.site.initializer.util.DSRRoomUtil;
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
			return;
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

	private String _getFriendlyURL(String friendlyURL) {
		if (Validator.isNotNull(friendlyURL) && !friendlyURL.startsWith("/")) {
			return "/" + friendlyURL;
		}

		return friendlyURL;
	}

	private Channel _getOrAddAnalyticsChannel(ChannelResource channelResource)
		throws Exception {

		Page<Channel> channelsPage = channelResource.getChannelsPage(
			_DSR_CHANNEL_NAME, Pagination.of(1, 1), null);

		List<Channel> channels = ListUtil.fromCollection(
			channelsPage.getItems());

		if (!channels.isEmpty()) {
			return channels.get(0);
		}

		Channel channel = new Channel();

		channel.setName(() -> _DSR_CHANNEL_NAME);

		return channelResource.postChannel(channel);
	}

	private ServiceContext _getServiceContext(long companyId, long userId) {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);
		serviceContext.setUserId(userId);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		return serviceContext;
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
		Group group;
		LayoutSetPrototype layoutSetPrototype = null;
		User user = _userLocalService.getUser(objectEntry.getUserId());

		try (AutoCloseable autoCloseable =
				_layoutServiceContextHelper.getServiceContextAutoCloseable(
					company, user)) {

			Map<String, Serializable> values = objectEntry.getValues();

			group = _groupLocalService.addGroup(
				null, user.getUserId(), GroupConstants.DEFAULT_PARENT_GROUP_ID,
				objectDefinition.getClassName(), objectEntry.getObjectEntryId(),
				GroupConstants.DEFAULT_LIVE_GROUP_ID,
				HashMapBuilder.put(
					LocaleUtil.getDefault(),
					GetterUtil.getString(values.get("name"))
				).build(),
				null, GroupConstants.TYPE_SITE_RESTRICTED, null, true,
				GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION,
				_getFriendlyURL(
					GetterUtil.getString(
						values.get("friendlyURL"),
						GetterUtil.getString(values.get("name")))),
				true, false, true,
				_getServiceContext(company.getCompanyId(), user.getUserId()));

			Role role = _roleLocalService.getRole(
				group.getCompanyId(), RoleConstants.SITE_OWNER);

			_userGroupRoleLocalService.addUserGroupRoles(
				user.getUserId(), group.getGroupId(),
				new long[] {role.getRoleId()});

			_userLocalService.addGroupUsers(
				group.getGroupId(), new long[] {user.getUserId()});

			LiveUsers.joinGroup(
				group.getCompanyId(), group.getGroupId(), user.getUserId());

			layoutSetPrototype =
				_layoutSetPrototypeLocalService.
					getLayoutSetPrototypeByUuidAndCompanyId(
						GetterUtil.getString(
							values.get("siteTemplateKey"),
							"L_DSR_LAYOUT_SET_PROTOTYPE"),
						company.getCompanyId());
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

			long[] fileEntryIds = DSRRoomThreadLocal.getFileEntryIds();

			TransactionCommitCallbackUtil.registerCallback(
				() -> {
					if (sourceObjectEntryId != 0) {
						_duplicateGroup(
							company, fileEntryIds, group, objectDefinition,
							sourceObjectEntryId, administratorUser);
					}

					_objectEntryLocalService.partialUpdateObjectEntry(
						objectEntry.getUserId(), objectEntry.getObjectEntryId(),
						objectEntry.getObjectEntryFolderId(),
						HashMapBuilder.<String, Serializable>put(
							"friendlyURL",
							StringUtil.removeFirst(group.getFriendlyURL(), "/")
						).put(
							"siteExternalReferenceCode",
							group.getExternalReferenceCode()
						).put(
							"siteId", group.getGroupId()
						).build(),
						new ServiceContext());

					try {
						_patchAnalyticsChannel(
							company.getCompanyId(), objectDefinition,
							objectEntry.getUserId());
					}
					catch (Exception exception) {
						_log.error(
							"Unable to connect site " + group.getGroupId() +
								" to analytics channel",
							exception);
					}

					return null;
				});
		}
		catch (Exception exception) {

			// LPS-169057

			PermissionCacheUtil.clearCache(objectEntry.getUserId());

			throw exception;
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
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

		String name = MapUtil.getString(objectEntry.getValues(), "name");

		String friendlyURL = _getFriendlyURL(
			MapUtil.getString(objectEntry.getValues(), "friendlyURL", name));

		Map<Locale, String> nameMap = group.getNameMap();

		if (Objects.equals(friendlyURL, group.getFriendlyURL()) &&
			Objects.equals(name, nameMap.get(LocaleUtil.getDefault()))) {

			return;
		}

		nameMap.put(LocaleUtil.getDefault(), name);

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(objectEntry.getCompanyId());
		serviceContext.setUserId(objectEntry.getUserId());

		_groupLocalService.updateGroup(
			group.getGroupId(), group.getParentGroupId(), nameMap,
			group.getDescriptionMap(), group.getType(), group.getTypeSettings(),
			group.isManualMembership(), group.getMembershipRestriction(),
			friendlyURL, group.isInheritContent(), group.isActive(),
			serviceContext);
	}

	private void _onBeforeUpdate(
			ObjectEntry originalObjectEntry, ObjectEntry objectEntry)
		throws Exception {

		ObjectDefinition objectDefinition = objectEntry.getObjectDefinition();

		if (!Objects.equals(
				objectDefinition.getExternalReferenceCode(), "L_DSR_ROOM")) {

			return;
		}

		Map<String, Serializable> originalValues =
			originalObjectEntry.getValues();
		Map<String, Serializable> values = objectEntry.getValues();

		for (Map.Entry<String, Serializable> entry : values.entrySet()) {
			String name = entry.getKey();

			if (Objects.equals(name, "archiveDate") ||
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

	private void _patchAnalyticsChannel(
			long companyId, ObjectDefinition objectDefinition, long userId)
		throws Exception {

		if (!_analyticsSettingsManager.isAnalyticsEnabled(companyId)) {
			return;
		}

		Channel channel = new Channel();

		ChannelResource channelResource = _channelResourceFactory.create(
		).checkPermissions(
			false
		).user(
			_userLocalService.getUser(userId)
		).build();

		Channel analyticsChannel = _getOrAddAnalyticsChannel(channelResource);

		channel.setChannelId(analyticsChannel::getChannelId);

		DataSource dataSource = new DataSource();

		dataSource.setSiteIds(
			() -> TransformUtil.transformToArray(
				_groupLocalService.getGroups(
					companyId, objectDefinition.getClassName(),
					GroupConstants.DEFAULT_PARENT_GROUP_ID),
				GroupModel::getGroupId, Long.class));

		channel.setDataSources(() -> new DataSource[] {dataSource});

		channelResource.patchChannel(channel);
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

			JSONObject jsonObject =
				fragmentEntryLink.getEditableValuesJSONObject();

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

	private static final String _DSR_CHANNEL_NAME = "DSR";

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

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile ChannelResource.Factory _channelResourceFactory;

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