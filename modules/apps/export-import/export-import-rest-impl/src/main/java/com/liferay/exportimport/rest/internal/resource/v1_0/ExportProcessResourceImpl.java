/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.resource.v1_0;

import com.liferay.exportimport.kernel.background.task.BackgroundTaskExecutorNames;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationFactory;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactory;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.exportimport.portlet.data.handler.provider.PortletDataHandlerProvider;
import com.liferay.exportimport.rest.dto.v1_0.DateFilter;
import com.liferay.exportimport.rest.dto.v1_0.ExportProcess;
import com.liferay.exportimport.rest.dto.v1_0.ExportProcessRequest;
import com.liferay.exportimport.rest.dto.v1_0.FromLastPublishDateFilter;
import com.liferay.exportimport.rest.dto.v1_0.InstanceExportProcessRequest;
import com.liferay.exportimport.rest.dto.v1_0.ProcessProgress;
import com.liferay.exportimport.rest.dto.v1_0.Site;
import com.liferay.exportimport.rest.dto.v1_0.Status;
import com.liferay.exportimport.rest.internal.util.BackgroundTaskUtil;
import com.liferay.exportimport.rest.internal.util.GroupUtil;
import com.liferay.exportimport.rest.internal.util.LayoutUtil;
import com.liferay.exportimport.rest.internal.util.ParameterMapUtil;
import com.liferay.exportimport.rest.internal.util.PermissionUtil;
import com.liferay.exportimport.rest.internal.util.PortletDataHandlerUtil;
import com.liferay.exportimport.rest.resource.v1_0.ExportProcessResource;
import com.liferay.headless.delivery.dto.v1_0.util.CreatorUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.ContentDispositionUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.site.staticexport.background.task.StaticSiteExportBackgroundTaskExecutorNames;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

import java.io.Serializable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Daniel Raposo
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/export-process.properties",
	scope = ServiceScope.PROTOTYPE, service = ExportProcessResource.class
)
public class ExportProcessResourceImpl extends BaseExportProcessResourceImpl {

	@Override
	public void deleteExportProcess(Long exportProcessId) throws Exception {
		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(exportProcessId);

		PermissionUtil.checkExportPermission(
			contextCompany.getCompanyId(), backgroundTask.getGroupId(),
			PermissionUtil.getGroupActionId(backgroundTask));

		BackgroundTaskUtil.checkTaskExecutorClassName(
			backgroundTask, _CLASS_NAMES_TASK_EXECUTOR);

		_backgroundTaskLocalService.deleteBackgroundTask(backgroundTask);
	}

	@Override
	public Page<ExportProcess> getAssetLibraryExportProcessesPage(
			String assetLibraryExternalReferenceCode, Long creatorId,
			String portletId, String search, Integer status,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		return _getExportProcessesPage(
			creatorId,
			GroupUtil.getAssetLibraryGroup(
				contextCompany.getCompanyId(),
				assetLibraryExternalReferenceCode),
			pagination, portletId, search, sorts, status);
	}

	@Override
	public ExportProcess getExportProcess(Long exportProcessId)
		throws Exception {

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(exportProcessId);

		PermissionUtil.checkExportPermission(
			contextCompany.getCompanyId(), backgroundTask.getGroupId(),
			PermissionUtil.getGroupActionId(backgroundTask));

		BackgroundTaskUtil.checkTaskExecutorClassName(
			backgroundTask, _CLASS_NAMES_TASK_EXECUTOR);

		return _toExportProcess(backgroundTask);
	}

	@Override
	public Response getExportProcessContent(Long exportProcessId)
		throws Exception {

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(exportProcessId);

		PermissionUtil.checkExportPermission(
			contextCompany.getCompanyId(), backgroundTask.getGroupId(),
			PermissionUtil.getGroupActionId(backgroundTask));

		BackgroundTaskUtil.checkTaskExecutorClassName(
			backgroundTask, _CLASS_NAMES_TASK_EXECUTOR);

		List<FileEntry> fileEntries =
			backgroundTask.getAttachmentsFileEntries();

		if (fileEntries.isEmpty()) {
			throw new NotFoundException();
		}

		FileEntry fileEntry = fileEntries.get(0);

		return Response.ok(
			fileEntry.getContentStream()
		).header(
			HttpHeaders.CONTENT_DISPOSITION,
			ContentDispositionUtil.getContentDispositionHeaderValue(
				fileEntry.getTitle())
		).build();
	}

	@Override
	public ProcessProgress getExportProcessProgress(Long exportProcessId)
		throws Exception {

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(exportProcessId);

		PermissionUtil.checkExportPermission(
			contextCompany.getCompanyId(), backgroundTask.getGroupId(),
			PermissionUtil.getGroupActionId(backgroundTask));

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
	public Page<ExportProcess> getExportProcessesPage(
			Long creatorId, String portletId, String search, Integer status,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		return _getExportProcessesPage(
			creatorId, GroupUtil.getCompanyGroup(contextCompany.getCompanyId()),
			pagination, portletId, search, sorts, status);
	}

	@Override
	public Page<ExportProcess> getSiteExportProcessesPage(
			String siteExternalReferenceCode, Long creatorId, String portletId,
			String search, String sitePageExternalReferenceCode, Integer status,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		Group group = GroupUtil.getSiteGroup(
			contextCompany.getCompanyId(), siteExternalReferenceCode,
			portletId);

		Layout layout = LayoutUtil.fetchPortletLayout(
			group, portletId, sitePageExternalReferenceCode);

		if ((layout == null) &&
			Validator.isNotNull(sitePageExternalReferenceCode)) {

			return Page.of(Collections.emptyList());
		}

		return _getExportProcessesPage(
			creatorId, GroupUtil.getScopeGroup(group, layout, portletId),
			pagination, portletId, search, sorts, status);
	}

	@Override
	public ExportProcess postAssetLibraryExportProcess(
			String assetLibraryExternalReferenceCode, String portletId,
			ExportProcessRequest exportProcessRequest)
		throws Exception {

		return _postExportProcess(
			exportProcessRequest,
			GroupUtil.getAssetLibraryGroup(
				contextCompany.getCompanyId(),
				assetLibraryExternalReferenceCode),
			portletId, null, null);
	}

	@Override
	public ExportProcess postExportProcess(
			String portletId,
			InstanceExportProcessRequest instanceExportProcessRequest)
		throws Exception {

		return _postExportProcess(
			_toExportProcessRequest(instanceExportProcessRequest),
			GroupUtil.getCompanyGroup(contextCompany.getCompanyId()), portletId,
			transform(
				instanceExportProcessRequest.getSites(),
				Site::getExternalReferenceCode, String.class),
			null);
	}

	@Override
	public ExportProcess postExportProcessRelaunch(Long exportProcessId)
		throws Exception {

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(exportProcessId);

		PermissionUtil.checkExportPermission(
			contextCompany.getCompanyId(), backgroundTask.getGroupId(),
			PermissionUtil.getGroupActionId(backgroundTask));

		BackgroundTaskUtil.checkTaskExecutorClassName(
			backgroundTask, _CLASS_NAMES_TASK_EXECUTOR);

		ExportImportConfiguration exportImportConfiguration =
			ExportImportConfigurationFactory.cloneExportImportConfiguration(
				_exportImportConfigurationLocalService.
					getExportImportConfiguration(
						MapUtil.getLong(
							backgroundTask.getTaskContextMap(),
							"exportImportConfigurationId")));

		long backgroundTaskId = 0;

		if (StringUtil.equals(
				backgroundTask.getTaskExecutorClassName(),
				BackgroundTaskExecutorNames.
					PORTLET_EXPORT_BACKGROUND_TASK_EXECUTOR)) {

			backgroundTaskId =
				_exportImportLocalService.exportPortletInfoAsFileInBackground(
					contextUser.getUserId(), exportImportConfiguration);
		}
		else {
			backgroundTaskId =
				_exportImportLocalService.exportLayoutsAsFileInBackground(
					contextUser.getUserId(), exportImportConfiguration);
		}

		return _toExportProcess(
			_backgroundTaskLocalService.getBackgroundTask(backgroundTaskId));
	}

	@Override
	public ExportProcess postSiteExportProcess(
			String siteExternalReferenceCode, String portletId,
			String sitePageExternalReferenceCode,
			ExportProcessRequest exportProcessRequest)
		throws Exception {

		return _postExportProcess(
			exportProcessRequest,
			GroupUtil.getSiteGroup(
				contextCompany.getCompanyId(), siteExternalReferenceCode,
				portletId),
			portletId, null, sitePageExternalReferenceCode);
	}

	private List<BackgroundTask> _getBackgroundTasks(
			Long creatorId, long groupId, Pagination pagination,
			String portletId, String search, Sort[] sorts, Integer status)
		throws Exception {

		PermissionUtil.checkExportPermission(
			contextCompany.getCompanyId(), groupId,
			PermissionUtil.getGroupActionId(portletId));

		DynamicQuery dynamicQuery = _getDynamicQuery(
			creatorId, groupId, portletId, search, status);

		BackgroundTaskUtil.addOrders(dynamicQuery, sorts);

		return _backgroundTaskLocalService.dynamicQuery(
			dynamicQuery, pagination.getStartPosition(),
			pagination.getEndPosition());
	}

	private DateFilter _getDateFilter(
		ExportProcessRequest exportProcessRequest) {

		DateFilter dateFilter = exportProcessRequest.getDateFilter();

		if (dateFilter instanceof FromLastPublishDateFilter) {
			throw new BadRequestException(
				"A \"FromLastPublishDate\" date filter applies only to " +
					"publications");
		}

		return dateFilter;
	}

	private DynamicQuery _getDynamicQuery(
		Long creatorId, long groupId, String portletId, String search,
		Integer status) {

		DynamicQuery dynamicQuery = _backgroundTaskLocalService.dynamicQuery();

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"companyId", contextCompany.getCompanyId()));
		dynamicQuery.add(RestrictionsFactoryUtil.eq("groupId", groupId));

		if (!Validator.isBlank(search)) {
			dynamicQuery.add(
				RestrictionsFactoryUtil.ilike(
					"name", StringUtil.quote(search, StringPool.PERCENT)));
		}

		if (status != null) {
			dynamicQuery.add(RestrictionsFactoryUtil.eq("status", status));
		}

		if (Validator.isBlank(portletId)) {
			dynamicQuery.add(
				RestrictionsFactoryUtil.in(
					"taskExecutorClassName",
					new String[] {
						BackgroundTaskExecutorNames.
							LAYOUT_EXPORT_BACKGROUND_TASK_EXECUTOR,
						StaticSiteExportBackgroundTaskExecutorNames.
							STATIC_SITE_EXPORT_BACKGROUND_TASK_EXECUTOR
					}));
		}
		else {
			dynamicQuery.add(RestrictionsFactoryUtil.eq("name", portletId));
			dynamicQuery.add(
				RestrictionsFactoryUtil.eq(
					"taskExecutorClassName",
					BackgroundTaskExecutorNames.
						PORTLET_EXPORT_BACKGROUND_TASK_EXECUTOR));
		}

		if (creatorId != null) {
			dynamicQuery.add(RestrictionsFactoryUtil.eq("userId", creatorId));
		}

		return dynamicQuery;
	}

	private Page<ExportProcess> _getExportProcessesPage(
			Long creatorId, Group group, Pagination pagination,
			String portletId, String search, Sort[] sorts, Integer status)
		throws Exception {

		long groupId = group.getGroupId();

		return Page.of(
			transform(
				_getBackgroundTasks(
					creatorId, groupId, pagination, portletId, search, sorts,
					status),
				this::_toExportProcess),
			pagination,
			_backgroundTaskLocalService.dynamicQueryCount(
				_getDynamicQuery(
					creatorId, groupId, portletId, search, status)));
	}

	private ExportProcess _postExportProcess(
			ExportProcessRequest exportProcessRequest, Group group,
			String portletId, String[] siteExternalReferenceCodes,
			String sitePageExternalReferenceCode)
		throws Exception {

		Layout layout = LayoutUtil.getPortletLayout(
			group, portletId, sitePageExternalReferenceCode);

		if (layout == null) {
			return _postLayoutExportProcess(
				exportProcessRequest, group, siteExternalReferenceCodes);
		}

		return _postPortletExportProcess(
			exportProcessRequest,
			GroupUtil.getScopeGroup(group, layout, portletId), layout,
			portletId);
	}

	private ExportProcess _postLayoutExportProcess(
			ExportProcessRequest exportProcessRequest, Group group,
			String[] siteExternalReferenceCodes)
		throws Exception {

		long groupId = group.getGroupId();

		PermissionUtil.checkExportPermission(
			contextCompany.getCompanyId(), groupId,
			ActionKeys.EXPORT_IMPORT_LAYOUTS);

		Map<String, String[]> parameterMap =
			ParameterMapUtil.putDateRangeParameters(
				_getDateFilter(exportProcessRequest),
				ParameterMapUtil.toParameterMap(
					exportProcessRequest,
					PortletDataHandlerUtil.getExportPortletDataHandlers(
						group, contextAcceptLanguage.getPreferredLocale(), 0,
						_portletDataHandlerProvider,
						_exportImportHelper.getExportablePortlets(
							contextCompany.getCompanyId(), false, groupId),
						false),
					false),
				contextUser);

		ParameterMapUtil.putSiteExternalReferenceCodes(
			parameterMap, siteExternalReferenceCodes);

		boolean privateLayout = LayoutUtil.isPrivateLayout(parameterMap);

		Map<String, Serializable> settingsMap =
			_exportImportConfigurationSettingsMapFactory.
				buildExportLayoutSettingsMap(
					contextUser.getUserId(), groupId, privateLayout,
					LayoutUtil.getLayoutIds(
						_layoutLocalService.getLayouts(groupId, privateLayout),
						parameterMap),
					parameterMap, contextAcceptLanguage.getPreferredLocale(),
					contextUser.getTimeZone());

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					contextUser.getUserId(), exportProcessRequest.getName(),
					ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT,
					settingsMap);

		long backgroundTaskId =
			_exportImportLocalService.exportLayoutsAsFileInBackground(
				contextUser.getUserId(), exportImportConfiguration);

		return _toExportProcess(
			_backgroundTaskLocalService.getBackgroundTask(backgroundTaskId));
	}

	private ExportProcess _postPortletExportProcess(
			ExportProcessRequest exportProcessRequest, Group group,
			Layout layout, String portletId)
		throws Exception {

		long groupId = group.getGroupId();

		PermissionUtil.checkExportPermission(
			contextCompany.getCompanyId(), groupId,
			ActionKeys.EXPORT_IMPORT_PORTLET_INFO);

		Portlet portlet = _portletLocalService.getPortletById(
			contextCompany.getCompanyId(), portletId);

		String fileName = exportProcessRequest.getName();

		if (Validator.isBlank(fileName)) {
			fileName = _exportImportHelper.getPortletExportFileName(portlet);
		}
		else if (!fileName.endsWith(".lar")) {
			fileName += ".lar";
		}

		Map<String, String[]> parameterMap =
			ParameterMapUtil.putDateRangeParameters(
				_getDateFilter(exportProcessRequest),
				ParameterMapUtil.toParameterMap(
					exportProcessRequest,
					PortletDataHandlerUtil.getExportPortletDataHandlers(
						group, contextAcceptLanguage.getPreferredLocale(),
						layout.getPlid(), _portletDataHandlerProvider,
						ListUtil.fromArray(portlet), true),
					true),
				contextUser);

		Map<String, Serializable> settingsMap =
			_exportImportConfigurationSettingsMapFactory.
				buildExportPortletSettingsMap(
					contextUser.getUserId(), layout.getPlid(), groupId,
					portletId, parameterMap,
					contextAcceptLanguage.getPreferredLocale(),
					contextUser.getTimeZone(), fileName);

		settingsMap.put("name", exportProcessRequest.getName());

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					contextUser.getUserId(),
					ExportImportConfigurationConstants.TYPE_EXPORT_PORTLET,
					settingsMap);

		long backgroundTaskId =
			_exportImportLocalService.exportPortletInfoAsFileInBackground(
				contextUser.getUserId(), exportImportConfiguration);

		return _toExportProcess(
			_backgroundTaskLocalService.getBackgroundTask(backgroundTaskId));
	}

	private ExportProcess _toExportProcess(BackgroundTask backgroundTask) {
		return new ExportProcess() {
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
				setName(() -> BackgroundTaskUtil.getName(backgroundTask));
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
			}
		};
	}

	private ExportProcessRequest _toExportProcessRequest(
		InstanceExportProcessRequest instanceExportProcessRequest) {

		return new ExportProcessRequest() {
			{
				setComments(instanceExportProcessRequest::getComments);
				setDateFilter(instanceExportProcessRequest::getDateFilter);
				setDeletions(instanceExportProcessRequest::getDeletions);
				setName(instanceExportProcessRequest::getName);
				setPermissions(instanceExportProcessRequest::getPermissions);
				setPortletDataHandlers(
					instanceExportProcessRequest::getPortletDataHandlers);
				setRatings(instanceExportProcessRequest::getRatings);
			}
		};
	}

	private static final String[] _CLASS_NAMES_TASK_EXECUTOR = {
		BackgroundTaskExecutorNames.LAYOUT_EXPORT_BACKGROUND_TASK_EXECUTOR,
		BackgroundTaskExecutorNames.PORTLET_EXPORT_BACKGROUND_TASK_EXECUTOR,
		StaticSiteExportBackgroundTaskExecutorNames.
			STATIC_SITE_EXPORT_BACKGROUND_TASK_EXECUTOR
	};

	@Reference
	private BackgroundTaskLocalService _backgroundTaskLocalService;

	@Reference
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Reference
	private ExportImportConfigurationSettingsMapFactory
		_exportImportConfigurationSettingsMapFactory;

	@Reference
	private ExportImportHelper _exportImportHelper;

	@Reference
	private ExportImportLocalService _exportImportLocalService;

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
	private UserLocalService _userLocalService;

}