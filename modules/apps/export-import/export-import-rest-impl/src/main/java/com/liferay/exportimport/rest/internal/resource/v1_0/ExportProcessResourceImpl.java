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
import com.liferay.exportimport.rest.dto.v1_0.ExportProcess;
import com.liferay.exportimport.rest.dto.v1_0.ExportProcessRequest;
import com.liferay.exportimport.rest.dto.v1_0.ProcessProgress;
import com.liferay.exportimport.rest.dto.v1_0.Status;
import com.liferay.exportimport.rest.internal.util.BackgroundTaskUtil;
import com.liferay.exportimport.rest.internal.util.DateRangeUtil;
import com.liferay.exportimport.rest.internal.util.ParameterMapUtil;
import com.liferay.exportimport.rest.internal.util.PermissionUtil;
import com.liferay.exportimport.rest.internal.util.PreviewPortletDataHandlerUtil;
import com.liferay.exportimport.rest.resource.v1_0.ExportProcessResource;
import com.liferay.headless.delivery.dto.v1_0.util.CreatorUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.OrderFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.NoSuchBackgroundTaskException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.DateRange;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.staging.StagingGroupHelper;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;

import java.io.Serializable;

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
			contextCompany.getCompanyId(), backgroundTask.getGroupId());

		_validateExportBackgroundTask(backgroundTask);

		_backgroundTaskLocalService.deleteBackgroundTask(backgroundTask);
	}

	@Override
	public Page<ExportProcess> getAssetLibraryExportProcessesPage(
			String assetLibraryExternalReferenceCode, Long creatorId,
			String search, Integer status, Pagination pagination, Sort[] sorts)
		throws Exception {

		Group group = _getAssetLibraryGroup(assetLibraryExternalReferenceCode);

		return _getExportProcessesPage(
			creatorId, group.getGroupId(), pagination, null, search, sorts,
			status);
	}

	@Override
	public Page<ExportProcess> getAssetLibraryPortletExportProcessesPage(
			String assetLibraryExternalReferenceCode, String portletId,
			Long creatorId, String search, Integer status,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		Group group = _getAssetLibraryGroup(assetLibraryExternalReferenceCode);

		return _getExportProcessesPage(
			creatorId, group.getGroupId(), pagination, portletId, search, sorts,
			status);
	}

	@Override
	public ExportProcess getExportProcess(Long exportProcessId)
		throws Exception {

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(exportProcessId);

		PermissionUtil.checkExportPermission(
			contextCompany.getCompanyId(), backgroundTask.getGroupId());

		_validateExportBackgroundTask(backgroundTask);

		return _toExportProcess(backgroundTask);
	}

	@Override
	public Response getExportProcessContent(Long exportProcessId)
		throws Exception {

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(exportProcessId);

		PermissionUtil.checkExportPermission(
			contextCompany.getCompanyId(), backgroundTask.getGroupId());

		_validateExportBackgroundTask(backgroundTask);

		List<FileEntry> fileEntries =
			backgroundTask.getAttachmentsFileEntries();

		if (fileEntries.isEmpty()) {
			throw new NotFoundException();
		}

		FileEntry fileEntry = fileEntries.get(0);

		return Response.ok(
			fileEntry.getContentStream()
		).header(
			"Content-Disposition",
			"attachment; filename=\"" + fileEntry.getTitle() + "\""
		).build();
	}

	@Override
	public Page<ExportProcess> getExportProcessesPage(
			Long creatorId, String search, Integer status,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		return _getExportProcessesPage(
			creatorId, _getCompanyGroupId(), pagination, null, search, sorts,
			status);
	}

	@Override
	public ProcessProgress getExportProcessProgress(Long exportProcessId)
		throws Exception {

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(exportProcessId);

		PermissionUtil.checkExportPermission(
			contextCompany.getCompanyId(), backgroundTask.getGroupId());

		_validateExportBackgroundTask(backgroundTask);

		return new ProcessProgress() {
			{
				setPercentage(
					() -> BackgroundTaskUtil.getPercentage(
						backgroundTask.getBackgroundTaskId()));
			}
		};
	}

	@Override
	public Page<ExportProcess> getSiteExportProcessesPage(
			String siteExternalReferenceCode, Long creatorId, String search,
			Integer status, Pagination pagination, Sort[] sorts)
		throws Exception {

		Group group = _getSiteGroup(siteExternalReferenceCode);

		return _getExportProcessesPage(
			creatorId, group.getGroupId(), pagination, null, search, sorts,
			status);
	}

	@Override
	public Page<ExportProcess> getSitePortletExportProcessesPage(
			String siteExternalReferenceCode, String portletId, Long creatorId,
			String search, Integer status, Pagination pagination, Sort[] sorts)
		throws Exception {

		Group group = _getSiteGroup(siteExternalReferenceCode);

		return _getExportProcessesPage(
			creatorId, group.getGroupId(), pagination, portletId, search, sorts,
			status);
	}

	@Override
	public ExportProcess postAssetLibraryExportProcess(
			String assetLibraryExternalReferenceCode,
			ExportProcessRequest exportProcessRequest)
		throws Exception {

		return _postLayoutExportProcess(
			exportProcessRequest,
			_getAssetLibraryGroup(assetLibraryExternalReferenceCode));
	}

	@Override
	public ExportProcess postAssetLibraryPortletExportProcess(
			String assetLibraryExternalReferenceCode, String portletId,
			Long plid, ExportProcessRequest exportProcessRequest)
		throws Exception {

		return _postPortletExportProcess(
			exportProcessRequest,
			_getAssetLibraryGroup(assetLibraryExternalReferenceCode),
			GetterUtil.getLong(plid), portletId);
	}

	@Override
	public ExportProcess postExportProcess(
			ExportProcessRequest exportProcessRequest)
		throws Exception {

		Group group = _stagingGroupHelper.fetchCompanyGroup(
			contextCompany.getCompanyId());

		if (group == null) {
			throw new NotFoundException();
		}

		return _postLayoutExportProcess(exportProcessRequest, group);
	}

	@Override
	public ExportProcess postExportProcessRelaunch(Long exportProcessId)
		throws Exception {

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(exportProcessId);

		PermissionUtil.checkExportPermission(
			contextCompany.getCompanyId(), backgroundTask.getGroupId());

		_validateExportBackgroundTask(backgroundTask);

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
			String siteExternalReferenceCode,
			ExportProcessRequest exportProcessRequest)
		throws Exception {

		return _postLayoutExportProcess(
			exportProcessRequest, _getSiteGroup(siteExternalReferenceCode));
	}

	@Override
	public ExportProcess postSitePortletExportProcess(
			String siteExternalReferenceCode, String portletId, Long plid,
			ExportProcessRequest exportProcessRequest)
		throws Exception {

		return _postPortletExportProcess(
			exportProcessRequest, _getSiteGroup(siteExternalReferenceCode),
			GetterUtil.getLong(plid), portletId);
	}

	private Group _getAssetLibraryGroup(String externalReferenceCode) {
		Group group = groupLocalService.fetchGroupByExternalReferenceCode(
			externalReferenceCode, contextCompany.getCompanyId());

		if ((group == null) || !group.isDepot()) {
			throw new NotFoundException();
		}

		return group;
	}

	private List<BackgroundTask> _getBackgroundTasks(
			Long creatorId, long groupId, Pagination pagination,
			String portletId, String search, Sort[] sorts, Integer status)
		throws Exception {

		PermissionUtil.checkExportPermission(
			contextCompany.getCompanyId(), groupId);

		DynamicQuery dynamicQuery = _getDynamicQuery(
			creatorId, groupId, portletId, search, status);

		_setSorts(dynamicQuery, sorts);

		return _backgroundTaskLocalService.dynamicQuery(
			dynamicQuery, pagination.getStartPosition(),
			pagination.getEndPosition());
	}

	private long _getCompanyGroupId() {
		Group group = _stagingGroupHelper.fetchCompanyGroup(
			contextCompany.getCompanyId());

		if (group == null) {
			return 0L;
		}

		return group.getGroupId();
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
				RestrictionsFactoryUtil.eq(
					"taskExecutorClassName",
					BackgroundTaskExecutorNames.
						LAYOUT_EXPORT_BACKGROUND_TASK_EXECUTOR));
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
			Long creatorId, long groupId, Pagination pagination,
			String portletId, String search, Sort[] sorts, Integer status)
		throws Exception {

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

	private Group _getSiteGroup(String externalReferenceCode) {
		Group group = groupLocalService.fetchGroupByExternalReferenceCode(
			externalReferenceCode, contextCompany.getCompanyId());

		if ((group == null) || (!group.isCMS() && !group.isSite())) {
			throw new NotFoundException();
		}

		return group;
	}

	private ExportProcess _postLayoutExportProcess(
			ExportProcessRequest exportProcessRequest, Group group)
		throws Exception {

		long groupId = group.getGroupId();

		PermissionUtil.checkExportPermission(
			contextCompany.getCompanyId(), groupId);

		Map<String, String[]> parameterMap = ParameterMapUtil.toParameterMap(
			exportProcessRequest, false);

		boolean privateLayout = parameterMap.containsKey(
			PreviewPortletDataHandlerUtil.PRIVATE_PAGES_CONTROL_NAME);
		boolean publicLayout = parameterMap.containsKey(
			PreviewPortletDataHandlerUtil.PUBLIC_PAGES_CONTROL_NAME);

		if (privateLayout && publicLayout) {
			throw new BadRequestException(
				"Cannot request both private and public pages");
		}

		if (privateLayout && !group.isPrivateLayoutsEnabled()) {
			throw new BadRequestException("Private pages are not enabled");
		}

		String[] values = parameterMap.get(
			privateLayout ?
				PreviewPortletDataHandlerUtil.PRIVATE_PAGES_CONTROL_NAME :
					PreviewPortletDataHandlerUtil.PUBLIC_PAGES_CONTROL_NAME);

		long[] layoutIds = new long[0];

		if (values != null) {
			if (Boolean.parseBoolean(values[0])) {
				layoutIds = _exportImportHelper.getAllLayoutIds(
					groupId, privateLayout);
			}
			else {
				layoutIds = GetterUtil.getLongValues(values);
			}
		}

		Map<String, Serializable> settingsMap =
			_exportImportConfigurationSettingsMapFactory.
				buildExportLayoutSettingsMap(
					contextUser.getUserId(), groupId, privateLayout, layoutIds,
					parameterMap, contextAcceptLanguage.getPreferredLocale(),
					contextUser.getTimeZone());

		_putDateRange(exportProcessRequest, settingsMap);

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
			ExportProcessRequest exportProcessRequest, Group group, long plid,
			String portletId)
		throws Exception {

		long groupId = group.getGroupId();

		PermissionUtil.checkExportPermission(
			contextCompany.getCompanyId(), groupId);

		String fileName = exportProcessRequest.getName();

		if (Validator.isBlank(fileName)) {
			Portlet portlet = _portletLocalService.getPortletById(
				contextCompany.getCompanyId(), portletId);

			fileName = _exportImportHelper.getPortletExportFileName(portlet);
		}
		else if (!fileName.endsWith(".lar")) {
			fileName += ".lar";
		}

		Map<String, String[]> parameterMap = ParameterMapUtil.toParameterMap(
			exportProcessRequest, true);

		Map<String, Serializable> settingsMap =
			_exportImportConfigurationSettingsMapFactory.
				buildExportPortletSettingsMap(
					contextUser.getUserId(), plid, groupId, portletId,
					parameterMap, contextAcceptLanguage.getPreferredLocale(),
					contextUser.getTimeZone(), fileName);

		_putDateRange(exportProcessRequest, settingsMap);

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

	private void _putDateRange(
		ExportProcessRequest exportProcessRequest,
		Map<String, Serializable> settingsMap) {

		DateRange dateRange = DateRangeUtil.toDateRange(
			exportProcessRequest.getStartDate(),
			exportProcessRequest.getEndDate());

		if (dateRange == null) {
			return;
		}

		settingsMap.put("endDate", dateRange.getEndDate());
		settingsMap.put("startDate", dateRange.getStartDate());
	}

	private void _setSorts(DynamicQuery dynamicQuery, Sort[] sorts) {
		if (sorts == null) {
			dynamicQuery.addOrder(OrderFactoryUtil.desc("createDate"));

			return;
		}

		for (Sort sort : sorts) {
			String fieldName = sort.getFieldName();

			fieldName = StringUtil.removeSubstring(fieldName, "_sortable");

			if (fieldName.equals("creator")) {
				fieldName = "userName";
			}
			else if (fieldName.equals("dateCompleted")) {
				fieldName = "completionDate";
			}
			else if (fieldName.equals("dateCreated")) {
				fieldName = "createDate";
			}
			else if (fieldName.equals("dateModified")) {
				fieldName = "modifiedDate";
			}
			else if (fieldName.equals("id")) {
				fieldName = "backgroundTaskId";
			}

			if (sort.isReverse()) {
				dynamicQuery.addOrder(OrderFactoryUtil.desc(fieldName));
			}
			else {
				dynamicQuery.addOrder(OrderFactoryUtil.asc(fieldName));
			}
		}
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
					() -> {
						JSONObject jsonObject =
							_jsonFactory.safeCreateJSONObject(
								backgroundTask.getStatusMessage(), true);

						if (jsonObject == null) {
							return backgroundTask.getStatusMessage();
						}

						return jsonObject.getString(
							"message", backgroundTask.getStatusMessage());
					});
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

	private void _validateExportBackgroundTask(BackgroundTask backgroundTask)
		throws Exception {

		String taskExecutorClassName =
			backgroundTask.getTaskExecutorClassName();

		if (!StringUtil.equals(
				taskExecutorClassName,
				BackgroundTaskExecutorNames.
					LAYOUT_EXPORT_BACKGROUND_TASK_EXECUTOR) &&
			!StringUtil.equals(
				taskExecutorClassName,
				BackgroundTaskExecutorNames.
					PORTLET_EXPORT_BACKGROUND_TASK_EXECUTOR)) {

			throw new NoSuchBackgroundTaskException();
		}
	}

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
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference
	private StagingGroupHelper _stagingGroupHelper;

	@Reference
	private UserLocalService _userLocalService;

}