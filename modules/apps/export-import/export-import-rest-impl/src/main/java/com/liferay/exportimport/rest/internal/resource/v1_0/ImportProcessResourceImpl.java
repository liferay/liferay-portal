/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.resource.v1_0;

import com.liferay.exportimport.kernel.background.task.BackgroundTaskExecutorNames;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactory;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.exportimport.rest.dto.v1_0.ImportProcess;
import com.liferay.exportimport.rest.dto.v1_0.ImportProcessRequest;
import com.liferay.exportimport.rest.dto.v1_0.ProcessProgress;
import com.liferay.exportimport.rest.dto.v1_0.Status;
import com.liferay.exportimport.rest.internal.util.BackgroundTaskUtil;
import com.liferay.exportimport.rest.internal.util.GroupUtil;
import com.liferay.exportimport.rest.internal.util.ParameterMapUtil;
import com.liferay.exportimport.rest.internal.util.PermissionUtil;
import com.liferay.exportimport.rest.resource.v1_0.ImportPreviewResource;
import com.liferay.exportimport.rest.resource.v1_0.ImportProcessResource;
import com.liferay.headless.delivery.dto.v1_0.util.CreatorUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipReaderFactory;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.io.InputStream;
import java.io.Serializable;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Petteri Karttunen
 * @author Daniel Raposo
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/import-process.properties",
	scope = ServiceScope.PROTOTYPE, service = ImportProcessResource.class
)
public class ImportProcessResourceImpl extends BaseImportProcessResourceImpl {

	@Override
	public void deleteImportProcess(Long importProcessId) throws Exception {
		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(importProcessId);

		PermissionUtil.checkImportPermission(
			contextCompany.getCompanyId(), backgroundTask.getGroupId());

		BackgroundTaskUtil.checkTaskExecutorClassName(
			backgroundTask, _CLASS_NAMES_TASK_EXECUTOR);

		_backgroundTaskLocalService.deleteBackgroundTask(backgroundTask);
	}

	@Override
	public Page<ImportProcess> getAssetLibraryImportProcessesPage(
			String assetLibraryExternalReferenceCode, Long creatorId,
			String portletId, String search, Integer status,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		return _getImportProcessesPage(
			creatorId,
			GroupUtil.getAssetLibraryGroup(
				contextCompany.getCompanyId(),
				assetLibraryExternalReferenceCode),
			pagination, portletId, search, sorts, status);
	}

	@Override
	public ImportProcess getImportProcess(Long importProcessId)
		throws Exception {

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(importProcessId);

		PermissionUtil.checkImportPermission(
			contextCompany.getCompanyId(), backgroundTask.getGroupId());

		BackgroundTaskUtil.checkTaskExecutorClassName(
			backgroundTask, _CLASS_NAMES_TASK_EXECUTOR);

		return _toImportProcess(backgroundTask);
	}

	@Override
	public Page<ImportProcess> getImportProcessesPage(
			Long creatorId, String portletId, String search, Integer status,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		return _getImportProcessesPage(
			creatorId, GroupUtil.getCompanyGroup(contextCompany.getCompanyId()),
			pagination, portletId, search, sorts, status);
	}

	@Override
	public ProcessProgress getImportProcessProgress(Long importProcessId)
		throws Exception {

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(importProcessId);

		PermissionUtil.checkImportPermission(
			contextCompany.getCompanyId(), backgroundTask.getGroupId());

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
	public Page<ImportProcess> getSiteImportProcessesPage(
			String siteExternalReferenceCode, Long creatorId, String portletId,
			String search, Integer status, Pagination pagination, Sort[] sorts)
		throws Exception {

		return _getImportProcessesPage(
			creatorId,
			GroupUtil.getSiteGroup(
				contextCompany.getCompanyId(), siteExternalReferenceCode),
			pagination, portletId, search, sorts, status);
	}

	@Override
	public ImportProcess postAssetLibraryImportProcess(
			String assetLibraryExternalReferenceCode, Long plid,
			String portletId, ImportProcessRequest importProcessRequest)
		throws Exception {

		return _postImportProcess(
			GroupUtil.getAssetLibraryGroup(
				contextCompany.getCompanyId(),
				assetLibraryExternalReferenceCode),
			importProcessRequest, GetterUtil.getLong(plid), portletId);
	}

	@Override
	public ImportProcess postImportProcess(
			Long plid, String portletId,
			ImportProcessRequest importProcessRequest)
		throws Exception {

		return _postImportProcess(
			GroupUtil.getCompanyGroup(contextCompany.getCompanyId()),
			importProcessRequest, GetterUtil.getLong(plid), portletId);
	}

	@Override
	public ImportProcess postSiteImportProcess(
			String siteExternalReferenceCode, Long plid, String portletId,
			ImportProcessRequest importProcessRequest)
		throws Exception {

		return _postImportProcess(
			GroupUtil.getSiteGroup(
				contextCompany.getCompanyId(), siteExternalReferenceCode),
			importProcessRequest, GetterUtil.getLong(plid), portletId);
	}

	private List<BackgroundTask> _getBackgroundTasks(
			Long creatorId, long groupId, Pagination pagination,
			String portletId, String search, Sort[] sorts, Integer status)
		throws Exception {

		PermissionUtil.checkImportPermission(
			contextCompany.getCompanyId(), groupId);

		DynamicQuery dynamicQuery = _getDynamicQuery(
			creatorId, groupId, portletId, search, status);

		BackgroundTaskUtil.addOrders(dynamicQuery, sorts);

		return _backgroundTaskLocalService.dynamicQuery(
			dynamicQuery, pagination.getStartPosition(),
			pagination.getEndPosition());
	}

	private String _getDefaultErrorMessage() {
		return _language.get(
			contextAcceptLanguage.getPreferredLocale(),
			"an-unexpected-error-occurred");
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
						LAYOUT_IMPORT_BACKGROUND_TASK_EXECUTOR));
		}
		else {
			dynamicQuery.add(RestrictionsFactoryUtil.eq("name", portletId));
			dynamicQuery.add(
				RestrictionsFactoryUtil.eq(
					"taskExecutorClassName",
					BackgroundTaskExecutorNames.
						PORTLET_IMPORT_BACKGROUND_TASK_EXECUTOR));
		}

		if (creatorId != null) {
			dynamicQuery.add(RestrictionsFactoryUtil.eq("userId", creatorId));
		}

		return dynamicQuery;
	}

	private Page<ImportProcess> _getImportProcessesPage(
			Long creatorId, Group group, Pagination pagination,
			String portletId, String search, Sort[] sorts, Integer status)
		throws Exception {

		long groupId = group.getGroupId();

		return Page.of(
			transform(
				_getBackgroundTasks(
					creatorId, groupId, pagination, portletId, search, sorts,
					status),
				this::_toImportProcess),
			pagination,
			_backgroundTaskLocalService.dynamicQueryCount(
				_getDynamicQuery(
					creatorId, groupId, portletId, search, status)));
	}

	private FileEntry _getImportTempFileEntry(long groupId) throws Exception {
		FileEntry fileEntry = _exportImportHelper.getTempFileEntry(
			groupId, contextUser.getUserId(),
			ImportPreviewResource.class.getName());

		if (fileEntry == null) {
			throw new NotFoundException();
		}

		return fileEntry;
	}

	private boolean _isPrivateLayout(FileEntry fileEntry) throws Exception {
		try (InputStream inputStream = fileEntry.getContentStream();

			ZipReader zipReader = _zipReaderFactory.getZipReader(inputStream)) {

			Document document = SAXReaderUtil.read(
				zipReader.getEntryAsString("/manifest.xml"));

			Element rootElement = document.getRootElement();

			Element headerElement = rootElement.element("header");

			if (headerElement == null) {
				return false;
			}

			return GetterUtil.getBoolean(
				headerElement.attributeValue("private-layout"));
		}
	}

	private ImportProcess _postImportProcess(
			Group group, ImportProcessRequest importProcessRequest, long plid,
			String portletId)
		throws Exception {

		if (Validator.isBlank(portletId)) {
			return _postLayoutImportProcess(group, importProcessRequest);
		}

		if (plid <= 0) {
			throw new BadRequestException(
				"Importing the portlet " + portletId + " requires a PLID");
		}

		return _postPortletImportProcess(
			group, importProcessRequest, plid, portletId);
	}

	private ImportProcess _postLayoutImportProcess(
			Group group, ImportProcessRequest importProcessRequest)
		throws Exception {

		long groupId = group.getGroupId();

		PermissionUtil.checkImportPermission(
			contextCompany.getCompanyId(), groupId);

		FileEntry fileEntry = _getImportTempFileEntry(groupId);

		String name = importProcessRequest.getName();

		if (Validator.isBlank(name)) {
			name = fileEntry.getFileName();
		}

		Map<String, String[]> parameterMap = ParameterMapUtil.toParameterMap(
			importProcessRequest, false);

		Map<String, Serializable> settingsMap =
			_exportImportConfigurationSettingsMapFactory.
				buildImportLayoutSettingsMap(
					contextUser.getUserId(), groupId,
					_isPrivateLayout(fileEntry), null, parameterMap,
					contextAcceptLanguage.getPreferredLocale(),
					contextUser.getTimeZone());

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					contextUser.getUserId(), name,
					ExportImportConfigurationConstants.TYPE_IMPORT_LAYOUT,
					settingsMap);

		try (InputStream inputStream = fileEntry.getContentStream()) {
			long backgroundTaskId =
				_exportImportLocalService.importLayoutsInBackground(
					contextUser.getUserId(), exportImportConfiguration,
					inputStream);

			TempFileEntryUtil.deleteTempFileEntry(fileEntry.getFileEntryId());

			return _toImportProcess(
				_backgroundTaskLocalService.getBackgroundTask(
					backgroundTaskId));
		}
	}

	private ImportProcess _postPortletImportProcess(
			Group group, ImportProcessRequest importProcessRequest, long plid,
			String portletId)
		throws Exception {

		long groupId = group.getGroupId();

		PermissionUtil.checkImportPermission(
			contextCompany.getCompanyId(), groupId);

		FileEntry fileEntry = _getImportTempFileEntry(groupId);

		String name = importProcessRequest.getName();

		if (Validator.isBlank(name)) {
			name = fileEntry.getFileName();
		}

		Map<String, String[]> parameterMap = ParameterMapUtil.toParameterMap(
			importProcessRequest, true);

		Map<String, Serializable> settingsMap =
			_exportImportConfigurationSettingsMapFactory.
				buildImportPortletSettingsMap(
					contextUser.getUserId(), plid, groupId, portletId,
					parameterMap, contextAcceptLanguage.getPreferredLocale(),
					contextUser.getTimeZone());

		settingsMap.put("name", name);

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					contextUser.getUserId(),
					ExportImportConfigurationConstants.TYPE_IMPORT_PORTLET,
					settingsMap);

		try (InputStream inputStream = fileEntry.getContentStream()) {
			long backgroundTaskId =
				_exportImportLocalService.importPortletInfoInBackground(
					contextUser.getUserId(), exportImportConfiguration,
					inputStream);

			TempFileEntryUtil.deleteTempFileEntry(fileEntry.getFileEntryId());

			return _toImportProcess(
				_backgroundTaskLocalService.getBackgroundTask(
					backgroundTaskId));
		}
	}

	private ImportProcess _toImportProcess(BackgroundTask backgroundTask)
		throws PortalException {

		return new ImportProcess() {
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
							return _getDefaultErrorMessage();
						}

						return jsonObject.getString(
							"message", _getDefaultErrorMessage());
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

	private static final String[] _CLASS_NAMES_TASK_EXECUTOR = {
		BackgroundTaskExecutorNames.LAYOUT_IMPORT_BACKGROUND_TASK_EXECUTOR,
		BackgroundTaskExecutorNames.PORTLET_IMPORT_BACKGROUND_TASK_EXECUTOR
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
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private ZipReaderFactory _zipReaderFactory;

}