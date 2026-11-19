/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.resource.v1_0;

import com.liferay.exportimport.kernel.background.task.BackgroundTaskExecutorNames;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationFactory;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationParameterMapFactory;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactory;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.exportimport.kernel.staging.StagingURLHelper;
import com.liferay.exportimport.portlet.data.handler.provider.PortletDataHandlerProvider;
import com.liferay.exportimport.rest.dto.v1_0.ProcessProgress;
import com.liferay.exportimport.rest.dto.v1_0.PublishProcess;
import com.liferay.exportimport.rest.dto.v1_0.PublishProcessRequest;
import com.liferay.exportimport.rest.dto.v1_0.Status;
import com.liferay.exportimport.rest.internal.util.BackgroundTaskUtil;
import com.liferay.exportimport.rest.internal.util.GroupUtil;
import com.liferay.exportimport.rest.internal.util.LayoutUtil;
import com.liferay.exportimport.rest.internal.util.ParameterMapUtil;
import com.liferay.exportimport.rest.internal.util.PermissionUtil;
import com.liferay.exportimport.rest.internal.util.PortletDataHandlerUtil;
import com.liferay.exportimport.rest.internal.util.RemoteStagingUtil;
import com.liferay.exportimport.rest.resource.v1_0.PublishProcessResource;
import com.liferay.headless.delivery.dto.v1_0.util.CreatorUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.backgroundtask.display.BackgroundTaskDisplay;
import com.liferay.portal.kernel.backgroundtask.display.BackgroundTaskDisplayFactory;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.service.http.LayoutServiceHttp;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.staging.StagingGroupHelper;

import jakarta.ws.rs.BadRequestException;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Daniel Raposo
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/publish-process.properties",
	scope = ServiceScope.PROTOTYPE, service = PublishProcessResource.class
)
public class PublishProcessResourceImpl extends BasePublishProcessResourceImpl {

	@Override
	public void deletePublishProcess(Long publishProcessId) throws Exception {
		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(publishProcessId);

		BackgroundTaskUtil.checkTaskExecutorClassName(
			backgroundTask, _CLASS_NAMES_TASK_EXECUTOR);
		PermissionUtil.checkPublishPermission(
			backgroundTask.getGroupId(),
			PermissionUtil.getPublishGroupActionId(backgroundTask));

		_backgroundTaskLocalService.deleteBackgroundTask(backgroundTask);
	}

	@Override
	public Page<PublishProcess> getAssetLibraryPublishProcessesPage(
			String assetLibraryExternalReferenceCode, Long creatorId,
			String portletId, String search, Integer status,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		return _getPublishProcessesPage(
			creatorId,
			GroupUtil.getAssetLibraryGroup(
				contextCompany.getCompanyId(),
				assetLibraryExternalReferenceCode),
			pagination, portletId, search, null, sorts, status);
	}

	@Override
	public PublishProcess getPublishProcess(Long publishProcessId)
		throws Exception {

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(publishProcessId);

		BackgroundTaskUtil.checkTaskExecutorClassName(
			backgroundTask, _CLASS_NAMES_TASK_EXECUTOR);
		PermissionUtil.checkPublishPermission(
			backgroundTask.getGroupId(),
			PermissionUtil.getViewPublishGroupActionId(backgroundTask));

		return _toPublishProcess(backgroundTask);
	}

	@Override
	public ProcessProgress getPublishProcessProgress(Long publishProcessId)
		throws Exception {

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(publishProcessId);

		BackgroundTaskUtil.checkTaskExecutorClassName(
			backgroundTask, _CLASS_NAMES_TASK_EXECUTOR);
		PermissionUtil.checkPublishPermission(
			backgroundTask.getGroupId(),
			PermissionUtil.getViewPublishGroupActionId(backgroundTask));

		return new ProcessProgress() {
			{
				setPercentage(
					() -> BackgroundTaskUtil.getPercentage(
						backgroundTask.getBackgroundTaskId()));
			}
		};
	}

	@Override
	public Page<PublishProcess> getSitePublishProcessesPage(
			String siteExternalReferenceCode, Long creatorId, String portletId,
			String search, String sitePageExternalReferenceCode, Integer status,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		return _getPublishProcessesPage(
			creatorId,
			GroupUtil.getSiteGroup(
				contextCompany.getCompanyId(), siteExternalReferenceCode),
			pagination, portletId, search, sitePageExternalReferenceCode, sorts,
			status);
	}

	@Override
	public PublishProcess postAssetLibraryPublishProcess(
			String assetLibraryExternalReferenceCode, String portletId,
			PublishProcessRequest publishProcessRequest)
		throws Exception {

		return _postPublishProcess(
			GroupUtil.getAssetLibraryGroup(
				contextCompany.getCompanyId(),
				assetLibraryExternalReferenceCode),
			portletId, publishProcessRequest, null);
	}

	@Override
	public PublishProcess postPublishProcessRelaunch(Long publishProcessId)
		throws Exception {

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(publishProcessId);

		BackgroundTaskUtil.checkTaskExecutorClassName(
			backgroundTask, _CLASS_NAMES_TASK_EXECUTOR);
		PermissionUtil.checkPublishPermission(
			backgroundTask.getGroupId(),
			PermissionUtil.getPublishGroupActionId(backgroundTask));

		ExportImportConfiguration exportImportConfiguration =
			ExportImportConfigurationFactory.cloneExportImportConfiguration(
				_exportImportConfigurationLocalService.
					getExportImportConfiguration(
						MapUtil.getLong(
							backgroundTask.getTaskContextMap(),
							"exportImportConfigurationId")));

		if (ArrayUtil.contains(
				_CLASS_NAMES_PORTLET_TASK_EXECUTOR,
				backgroundTask.getTaskExecutorClassName())) {

			return _toPublishProcess(
				_backgroundTaskLocalService.getBackgroundTask(
					_staging.publishPortlet(
						contextUser.getUserId(), exportImportConfiguration)));
		}

		if (StringUtil.equals(
				backgroundTask.getTaskExecutorClassName(),
				BackgroundTaskExecutorNames.
					LAYOUT_REMOTE_STAGING_BACKGROUND_TASK_EXECUTOR)) {

			Map<String, Serializable> settingsMap =
				exportImportConfiguration.getSettingsMap();

			_groupLocalService.validateRemote(
				exportImportConfiguration.getGroupId(),
				MapUtil.getString(settingsMap, "remoteAddress"),
				MapUtil.getInteger(settingsMap, "remotePort"),
				MapUtil.getString(settingsMap, "remotePathContext"),
				MapUtil.getBoolean(settingsMap, "secureConnection"),
				MapUtil.getLong(settingsMap, "targetGroupId"));

			return _toPublishProcess(
				_backgroundTaskLocalService.getBackgroundTask(
					_staging.copyRemoteLayouts(exportImportConfiguration)));
		}

		return _toPublishProcess(
			_backgroundTaskLocalService.getBackgroundTask(
				_staging.publishLayouts(
					contextUser.getUserId(), exportImportConfiguration)));
	}

	@Override
	public PublishProcess postSitePublishProcess(
			String siteExternalReferenceCode, String portletId,
			String sitePageExternalReferenceCode,
			PublishProcessRequest publishProcessRequest)
		throws Exception {

		return _postPublishProcess(
			GroupUtil.getSiteGroup(
				contextCompany.getCompanyId(), siteExternalReferenceCode),
			portletId, publishProcessRequest, sitePageExternalReferenceCode);
	}

	private List<BackgroundTask> _getBackgroundTasks(
		Long creatorId, List<Long> groupIds, Pagination pagination,
		String search, Sort[] sorts, Integer status) {

		DynamicQuery dynamicQuery = _getDynamicQuery(
			creatorId, groupIds, null, search, status);

		BackgroundTaskUtil.addOrders(dynamicQuery, sorts);

		return _backgroundTaskLocalService.dynamicQuery(
			dynamicQuery, pagination.getStartPosition(),
			pagination.getEndPosition());
	}

	private BadRequestException _getBadRequestException(String key) {
		return new BadRequestException(
			_language.get(contextAcceptLanguage.getPreferredLocale(), key));
	}

	private DynamicQuery _getDynamicQuery(
		Long creatorId, List<Long> groupIds, String portletId, String search,
		Integer status) {

		DynamicQuery dynamicQuery = _backgroundTaskLocalService.dynamicQuery();

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"companyId", contextCompany.getCompanyId()));
		dynamicQuery.add(RestrictionsFactoryUtil.in("groupId", groupIds));

		if (Validator.isBlank(portletId)) {
			dynamicQuery.add(
				RestrictionsFactoryUtil.in(
					"taskExecutorClassName",
					_CLASS_NAMES_LAYOUT_TASK_EXECUTOR));
		}
		else {
			dynamicQuery.add(RestrictionsFactoryUtil.eq("name", portletId));
			dynamicQuery.add(
				RestrictionsFactoryUtil.in(
					"taskExecutorClassName",
					_CLASS_NAMES_PORTLET_TASK_EXECUTOR));
		}

		if (!Validator.isBlank(search)) {
			dynamicQuery.add(
				RestrictionsFactoryUtil.ilike(
					"name", StringUtil.quote(search, StringPool.PERCENT)));
		}

		if (status != null) {
			dynamicQuery.add(RestrictionsFactoryUtil.eq("status", status));
		}

		if (creatorId != null) {
			dynamicQuery.add(RestrictionsFactoryUtil.eq("userId", creatorId));
		}

		return dynamicQuery;
	}

	private String _getName(BackgroundTask backgroundTask) {
		if (ArrayUtil.contains(
				_CLASS_NAMES_PORTLET_TASK_EXECUTOR,
				backgroundTask.getTaskExecutorClassName())) {

			return BackgroundTaskUtil.getName(backgroundTask);
		}

		BackgroundTaskDisplay backgroundTaskDisplay =
			_backgroundTaskDisplayFactory.getBackgroundTaskDisplay(
				backgroundTask.getBackgroundTaskId());

		return backgroundTaskDisplay.getDisplayName(contextHttpServletRequest);
	}

	private List<BackgroundTask> _getPortletBackgroundTasks(
		Long creatorId, List<Long> groupIds, String portletId, String search,
		Sort[] sorts, Integer status) {

		DynamicQuery dynamicQuery = _getDynamicQuery(
			creatorId, groupIds, portletId, null, status);

		BackgroundTaskUtil.addOrders(dynamicQuery, sorts);

		List<BackgroundTask> backgroundTasks =
			_backgroundTaskLocalService.dynamicQuery(dynamicQuery);

		if (Validator.isNotNull(search)) {
			backgroundTasks = ListUtil.filter(
				backgroundTasks,
				backgroundTask -> {
					String name = StringUtil.toLowerCase(
						BackgroundTaskUtil.getName(backgroundTask));

					return name.contains(StringUtil.toLowerCase(search));
				});
		}

		if (ArrayUtil.isEmpty(sorts) ||
			!Objects.equals(sorts[0].getFieldName(), "name")) {

			return backgroundTasks;
		}

		Comparator<BackgroundTask> comparator = Comparator.comparing(
			BackgroundTaskUtil::getName, String.CASE_INSENSITIVE_ORDER);

		if (sorts[0].isReverse()) {
			comparator = comparator.reversed();
		}

		return ListUtil.sort(backgroundTasks, comparator);
	}

	private List<Long> _getPublishGroupIds(Group stagingGroup) {
		List<Long> groupIds = new ArrayList<>();

		groupIds.add(stagingGroup.getGroupId());

		Group liveGroup = _stagingGroupHelper.fetchLocalLiveGroup(stagingGroup);

		if (liveGroup != null) {
			groupIds.add(liveGroup.getGroupId());
		}

		return groupIds;
	}

	private Page<PublishProcess> _getPublishProcessesPage(
			Long creatorId, Group group, Pagination pagination,
			String portletId, String search,
			String sitePageExternalReferenceCode, Sort[] sorts, Integer status)
		throws Exception {

		Group stagingGroup = GroupUtil.getStagingGroup(group);

		Layout layout = LayoutUtil.fetchPortletLayout(
			stagingGroup, portletId, sitePageExternalReferenceCode);

		if ((layout == null) &&
			Validator.isNotNull(sitePageExternalReferenceCode)) {

			return Page.of(Collections.emptyList());
		}

		Group scopeGroup = GroupUtil.getScopeGroup(
			stagingGroup, layout, portletId);

		PermissionUtil.checkPublishPermission(
			scopeGroup.getGroupId(),
			PermissionUtil.getViewPublishGroupActionId(portletId));

		List<Long> groupIds = _getPublishGroupIds(scopeGroup);

		if (Validator.isNotNull(portletId)) {
			List<BackgroundTask> backgroundTasks = _getPortletBackgroundTasks(
				creatorId, groupIds, portletId, search, sorts, status);

			return Page.of(
				transform(
					ListUtil.subList(
						backgroundTasks, pagination.getStartPosition(),
						pagination.getEndPosition()),
					this::_toPublishProcess),
				pagination, backgroundTasks.size());
		}

		return Page.of(
			transform(
				_getBackgroundTasks(
					creatorId, groupIds, pagination, search, sorts, status),
				this::_toPublishProcess),
			pagination,
			_backgroundTaskLocalService.dynamicQueryCount(
				_getDynamicQuery(creatorId, groupIds, null, search, status)));
	}

	private long _getTargetGroupId(Group scopeGroup, Group stagingGroup) {
		if (stagingGroup.isStagedRemotely()) {
			if (scopeGroup.isLayout()) {
				throw _getBadRequestException(
					"a-widget-is-placed-in-this-page-of-scope-that-does-not-" +
						"exist-in-the-live-site-publish-the-page-first");
			}

			return stagingGroup.getRemoteLiveGroupId();
		}

		if (scopeGroup.getLiveGroupId() == 0) {
			throw _getBadRequestException(
				"a-widget-is-placed-in-this-page-of-scope-that-does-not-" +
					"exist-in-the-live-site-publish-the-page-first");
		}

		return scopeGroup.getLiveGroupId();
	}

	private long _getTargetPlid(
			Layout layout, String portletId, Group stagingGroup)
		throws Exception {

		if (layout.isTypeControlPanel()) {
			if (!stagingGroup.isStagedRemotely()) {
				return layout.getPlid();
			}

			return LayoutServiceHttp.getControlPanelLayoutPlid(
				new HttpPrincipal(
					_stagingURLHelper.buildRemoteURL(
						stagingGroup.getTypeSettingsProperties()),
					contextUser.getLogin(), contextUser.getPassword(),
					contextUser.isPasswordEncrypted()));
		}

		if (stagingGroup.isStagedRemotely()) {
			long targetPlid = _staging.getRemoteLayoutPlid(
				contextUser.getUserId(), stagingGroup.getGroupId(),
				layout.getPlid());

			if (targetPlid == 0) {
				throw _getBadRequestException(
					"this-widget-is-placed-in-a-page-that-does-not-exist-in-" +
						"the-live-site-publish-the-page-first");
			}

			if (!_staging.isRemoteLayoutHasPortletId(
					contextUser.getUserId(), stagingGroup.getGroupId(),
					targetPlid, portletId)) {

				throw _getBadRequestException(
					"this-widget-has-not-been-added-to-the-live-page-publish-" +
						"the-page-first");
			}

			return targetPlid;
		}

		Layout targetLayout = _layoutLocalService.fetchLayoutByUuidAndGroupId(
			layout.getUuid(), stagingGroup.getLiveGroupId(),
			layout.isPrivateLayout());

		if (targetLayout == null) {
			throw _getBadRequestException(
				"this-widget-is-placed-in-a-page-that-does-not-exist-in-the-" +
					"live-site-publish-the-page-first");
		}

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)targetLayout.getLayoutType();

		if (!layoutTypePortlet.hasPortletId(portletId)) {
			throw _getBadRequestException(
				"this-widget-has-not-been-added-to-the-live-page-publish-the-" +
					"page-first");
		}

		return targetLayout.getPlid();
	}

	private boolean _isScheduled(
		ExportImportConfiguration exportImportConfiguration) {

		if (exportImportConfiguration == null) {
			return false;
		}

		if (exportImportConfiguration.getType() ==
				ExportImportConfigurationConstants.
					TYPE_SCHEDULED_PUBLISH_LAYOUT_LOCAL) {

			return true;
		}

		Map<String, Serializable> settingsMap =
			exportImportConfiguration.getSettingsMap();

		Map<String, String[]> parameterMap =
			(Map<String, String[]>)settingsMap.get("parameterMap");

		if ((parameterMap != null) &&
			parameterMap.containsKey(ParameterMapUtil.CRON_EXPRESSION)) {

			return true;
		}

		return false;
	}

	private PublishProcess _postLayoutPublishProcess(
			Group group, PublishProcessRequest publishProcessRequest,
			Group stagingGroup)
		throws Exception {

		long stagingGroupId = stagingGroup.getGroupId();

		PermissionUtil.checkPublishPermission(
			stagingGroupId, ActionKeys.PUBLISH_STAGING);

		Map<String, String[]> parameterMap =
			ParameterMapUtil.putDateRangeParameters(
				publishProcessRequest.getDateFilter(),
				ParameterMapUtil.toParameterMap(
					publishProcessRequest,
					PortletDataHandlerUtil.getExportPortletDataHandlers(
						stagingGroup,
						contextAcceptLanguage.getPreferredLocale(), 0,
						_portletDataHandlerProvider,
						_exportImportHelper.getExportablePortlets(
							contextCompany.getCompanyId(), false,
							stagingGroupId),
						false)),
				contextUser);

		parameterMap =
			_exportImportConfigurationParameterMapFactory.buildParameterMap(
				parameterMap);

		if (stagingGroup.isStagedRemotely()) {
			ExportImportConfiguration exportImportConfiguration =
				_exportImportConfigurationLocalService.
					addDraftExportImportConfiguration(
						contextUser.getUserId(),
						publishProcessRequest.getName(),
						ExportImportConfigurationConstants.
							TYPE_PUBLISH_LAYOUT_REMOTE,
						RemoteStagingUtil.buildPublishLayoutRemoteSettingsMap(
							parameterMap,
							publishProcessRequest.getRemoteConnection(),
							stagingGroup, contextUser));

			return _toPublishProcess(
				_backgroundTaskLocalService.getBackgroundTask(
					_staging.copyRemoteLayouts(exportImportConfiguration)));
		}

		if (publishProcessRequest.getRemoteConnection() != null) {
			throw new BadRequestException(
				"Remote staging is not enabled for \"" +
					group.getExternalReferenceCode() + "\"");
		}

		parameterMap.put(
			PortletDataHandlerKeys.PERFORM_DIRECT_BINARY_IMPORT,
			new String[] {Boolean.TRUE.toString()});

		boolean privateLayout = LayoutUtil.isPrivateLayout(parameterMap);

		Group liveGroup = GroupUtil.getLiveGroup(stagingGroup);

		Map<String, Serializable> settingsMap =
			_exportImportConfigurationSettingsMapFactory.
				buildPublishLayoutLocalSettingsMap(
					contextUser, stagingGroupId, liveGroup.getGroupId(),
					privateLayout,
					LayoutUtil.getLayoutIds(
						_layoutLocalService.getLayouts(
							stagingGroupId, privateLayout),
						parameterMap),
					parameterMap);

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					contextUser.getUserId(), publishProcessRequest.getName(),
					ExportImportConfigurationConstants.
						TYPE_PUBLISH_LAYOUT_LOCAL,
					settingsMap);

		return _toPublishProcess(
			_backgroundTaskLocalService.getBackgroundTask(
				_staging.publishLayouts(
					contextUser.getUserId(), exportImportConfiguration)));
	}

	private PublishProcess _postPortletPublishProcess(
			Layout layout, String portletId,
			PublishProcessRequest publishProcessRequest, Group scopeGroup,
			Group stagingGroup)
		throws Exception {

		PermissionUtil.checkPublishPermission(
			scopeGroup.getGroupId(), ActionKeys.PUBLISH_PORTLET_INFO);

		if (publishProcessRequest.getRemoteConnection() != null) {
			throw new BadRequestException(
				"A remote connection does not apply to the publication of a " +
					"portlet");
		}

		Map<String, String[]> parameterMap =
			ParameterMapUtil.putDateRangeParameters(
				publishProcessRequest.getDateFilter(),
				ParameterMapUtil.toParameterMap(
					publishProcessRequest,
					PortletDataHandlerUtil.getExportPortletDataHandlers(
						scopeGroup, contextAcceptLanguage.getPreferredLocale(),
						layout.getPlid(), _portletDataHandlerProvider,
						ListUtil.fromArray(
							_portletLocalService.getPortletById(
								contextCompany.getCompanyId(), portletId)),
						true)),
				contextUser);

		parameterMap =
			_exportImportConfigurationParameterMapFactory.buildParameterMap(
				parameterMap);

		Map<String, Serializable> settingsMap =
			_exportImportConfigurationSettingsMapFactory.
				buildPublishPortletSettingsMap(
					contextUser, scopeGroup.getGroupId(), layout.getPlid(),
					_getTargetGroupId(scopeGroup, stagingGroup),
					_getTargetPlid(layout, portletId, stagingGroup), portletId,
					parameterMap);

		settingsMap.put("name", publishProcessRequest.getName());

		int type =
			ExportImportConfigurationConstants.TYPE_PUBLISH_PORTLET_LOCAL;

		if (stagingGroup.isStagedRemotely()) {
			type =
				ExportImportConfigurationConstants.TYPE_PUBLISH_PORTLET_REMOTE;
		}

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					contextUser.getUserId(), type, settingsMap);

		return _toPublishProcess(
			_backgroundTaskLocalService.getBackgroundTask(
				_staging.publishPortlet(
					contextUser.getUserId(), exportImportConfiguration)));
	}

	private PublishProcess _postPublishProcess(
			Group group, String portletId,
			PublishProcessRequest publishProcessRequest,
			String sitePageExternalReferenceCode)
		throws Exception {

		Group stagingGroup = GroupUtil.getStagingGroup(group);

		Layout layout = LayoutUtil.getPortletLayout(
			stagingGroup, portletId, sitePageExternalReferenceCode);

		if (layout == null) {
			return _postLayoutPublishProcess(
				group, publishProcessRequest, stagingGroup);
		}

		return _postPortletPublishProcess(
			layout, portletId, publishProcessRequest,
			GroupUtil.getScopeGroup(stagingGroup, layout, portletId),
			stagingGroup);
	}

	private PublishProcess _toPublishProcess(BackgroundTask backgroundTask) {
		return new PublishProcess() {
			{
				setCreator(
					() -> CreatorUtil.toCreator(
						null, _portal,
						_userLocalService.fetchUser(
							backgroundTask.getUserId())));
				setDateCompleted(backgroundTask::getCompletionDate);
				setDateCreated(backgroundTask::getCreateDate);
				setDateModified(backgroundTask::getModifiedDate);
				setErrorMessage(
					() -> BackgroundTaskUtil.getErrorMessage(
						backgroundTask,
						contextAcceptLanguage.getPreferredLocale()));
				setId(backgroundTask::getBackgroundTaskId);
				setName(() -> _getName(backgroundTask));
				setStatus(
					() -> new Status() {
						{
							setCode(backgroundTask::getStatus);
							setLabel(
								() -> _language.get(
									contextUser.getLocale(),
									BackgroundTaskConstants.getStatusLabel(
										backgroundTask.getStatus())));
						}
					});
				setType(() -> _toType(backgroundTask));
			}
		};
	}

	private PublishProcess.Type _toType(BackgroundTask backgroundTask) {
		if (_isScheduled(
				_exportImportConfigurationLocalService.
					fetchExportImportConfiguration(
						MapUtil.getLong(
							backgroundTask.getTaskContextMap(),
							"exportImportConfigurationId")))) {

			return PublishProcess.Type.SCHEDULED;
		}

		return PublishProcess.Type.MANUAL;
	}

	private static final String[] _CLASS_NAMES_LAYOUT_TASK_EXECUTOR = {
		BackgroundTaskExecutorNames.
			LAYOUT_REMOTE_STAGING_BACKGROUND_TASK_EXECUTOR,
		BackgroundTaskExecutorNames.LAYOUT_STAGING_BACKGROUND_TASK_EXECUTOR
	};

	private static final String[] _CLASS_NAMES_PORTLET_TASK_EXECUTOR = {
		BackgroundTaskExecutorNames.
			PORTLET_REMOTE_STAGING_BACKGROUND_TASK_EXECUTOR,
		BackgroundTaskExecutorNames.PORTLET_STAGING_BACKGROUND_TASK_EXECUTOR
	};

	private static final String[] _CLASS_NAMES_TASK_EXECUTOR = ArrayUtil.append(
		_CLASS_NAMES_LAYOUT_TASK_EXECUTOR, _CLASS_NAMES_PORTLET_TASK_EXECUTOR);

	@Reference
	private BackgroundTaskDisplayFactory _backgroundTaskDisplayFactory;

	@Reference
	private BackgroundTaskLocalService _backgroundTaskLocalService;

	@Reference
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Reference
	private ExportImportConfigurationParameterMapFactory
		_exportImportConfigurationParameterMapFactory;

	@Reference
	private ExportImportConfigurationSettingsMapFactory
		_exportImportConfigurationSettingsMapFactory;

	@Reference
	private ExportImportHelper _exportImportHelper;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletDataHandlerProvider _portletDataHandlerProvider;

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference
	private Staging _staging;

	@Reference
	private StagingGroupHelper _stagingGroupHelper;

	@Reference
	private StagingURLHelper _stagingURLHelper;

	@Reference
	private UserLocalService _userLocalService;

}