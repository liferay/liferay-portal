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
import com.liferay.exportimport.rest.resource.v1_0.PublishProcessResource;
import com.liferay.headless.delivery.dto.v1_0.util.CreatorUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.backgroundtask.display.BackgroundTaskDisplayFactory;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.staging.StagingGroupHelper;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

		PermissionUtil.checkPublishPermission(backgroundTask.getGroupId());

		BackgroundTaskUtil.checkTaskExecutorClassName(
			backgroundTask, _CLASS_NAMES_TASK_EXECUTOR);

		_backgroundTaskLocalService.deleteBackgroundTask(backgroundTask);
	}

	@Override
	public PublishProcess getPublishProcess(Long publishProcessId)
		throws Exception {

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(publishProcessId);

		PermissionUtil.checkPublishPermission(backgroundTask.getGroupId());

		BackgroundTaskUtil.checkTaskExecutorClassName(
			backgroundTask, _CLASS_NAMES_TASK_EXECUTOR);

		return _toPublishProcess(backgroundTask);
	}

	@Override
	public ProcessProgress getPublishProcessProgress(Long publishProcessId)
		throws Exception {

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(publishProcessId);

		PermissionUtil.checkPublishPermission(backgroundTask.getGroupId());

		BackgroundTaskUtil.checkTaskExecutorClassName(
			backgroundTask, _CLASS_NAMES_TASK_EXECUTOR);

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
			String siteExternalReferenceCode, Long creatorId, String search,
			Integer status, Pagination pagination, Sort[] sorts)
		throws Exception {

		Group stagingGroup = GroupUtil.getStagingGroup(
			GroupUtil.getSiteGroup(
				contextCompany.getCompanyId(), siteExternalReferenceCode));

		long stagingGroupId = stagingGroup.getGroupId();

		PermissionUtil.checkPublishPermission(stagingGroupId);

		List<Long> groupIds = _getPublishGroupIds(stagingGroup);

		return Page.of(
			transform(
				_getBackgroundTasks(
					creatorId, groupIds, pagination, search, sorts, status),
				this::_toPublishProcess),
			pagination,
			_backgroundTaskLocalService.dynamicQueryCount(
				_getDynamicQuery(creatorId, groupIds, search, status)));
	}

	@Override
	public PublishProcess postPublishProcessRelaunch(Long publishProcessId)
		throws Exception {

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(publishProcessId);

		PermissionUtil.checkPublishPermission(backgroundTask.getGroupId());

		BackgroundTaskUtil.checkTaskExecutorClassName(
			backgroundTask, _CLASS_NAMES_TASK_EXECUTOR);

		ExportImportConfiguration exportImportConfiguration =
			ExportImportConfigurationFactory.cloneExportImportConfiguration(
				_exportImportConfigurationLocalService.
					getExportImportConfiguration(
						MapUtil.getLong(
							backgroundTask.getTaskContextMap(),
							"exportImportConfigurationId")));

		return _toPublishProcess(
			_backgroundTaskLocalService.getBackgroundTask(
				_staging.publishLayouts(
					contextUser.getUserId(), exportImportConfiguration)));
	}

	@Override
	public PublishProcess postSitePublishProcess(
			String siteExternalReferenceCode,
			PublishProcessRequest publishProcessRequest)
		throws Exception {

		Group stagingGroup = GroupUtil.getStagingGroup(
			GroupUtil.getSiteGroup(
				contextCompany.getCompanyId(), siteExternalReferenceCode));

		long stagingGroupId = stagingGroup.getGroupId();

		PermissionUtil.checkPublishPermission(stagingGroupId);

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

	private List<BackgroundTask> _getBackgroundTasks(
			Long creatorId, List<Long> groupIds, Pagination pagination,
			String search, Sort[] sorts, Integer status)
		throws Exception {

		DynamicQuery dynamicQuery = _getDynamicQuery(
			creatorId, groupIds, search, status);

		BackgroundTaskUtil.addOrders(dynamicQuery, sorts);

		return _backgroundTaskLocalService.dynamicQuery(
			dynamicQuery, pagination.getStartPosition(),
			pagination.getEndPosition());
	}

	private DynamicQuery _getDynamicQuery(
		Long creatorId, List<Long> groupIds, String search, Integer status) {

		DynamicQuery dynamicQuery = _backgroundTaskLocalService.dynamicQuery();

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"companyId", contextCompany.getCompanyId()));
		dynamicQuery.add(RestrictionsFactoryUtil.in("groupId", groupIds));
		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"taskExecutorClassName",
				BackgroundTaskExecutorNames.
					LAYOUT_STAGING_BACKGROUND_TASK_EXECUTOR));

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

	private List<Long> _getPublishGroupIds(Group stagingGroup) {
		List<Long> groupIds = new ArrayList<>();

		groupIds.add(stagingGroup.getGroupId());

		Group liveGroup = _stagingGroupHelper.fetchLocalLiveGroup(stagingGroup);

		if (liveGroup != null) {
			groupIds.add(liveGroup.getGroupId());
		}

		return groupIds;
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
				setName(
					() ->
						_backgroundTaskDisplayFactory.getBackgroundTaskDisplay(
							backgroundTask.getBackgroundTaskId()
						).getDisplayName(
							contextHttpServletRequest
						));
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

	private static final String[] _CLASS_NAMES_TASK_EXECUTOR = {
		BackgroundTaskExecutorNames.LAYOUT_STAGING_BACKGROUND_TASK_EXECUTOR
	};

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
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletDataHandlerProvider _portletDataHandlerProvider;

	@Reference
	private Staging _staging;

	@Reference
	private StagingGroupHelper _stagingGroupHelper;

	@Reference
	private UserLocalService _userLocalService;

}