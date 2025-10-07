/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.resource.v1_0;

import com.liferay.exportimport.kernel.background.task.BackgroundTaskExecutorNames;
import com.liferay.exportimport.rest.dto.v1_0.ImportProcess;
import com.liferay.exportimport.rest.dto.v1_0.Status;
import com.liferay.exportimport.rest.internal.util.PermissionUtil;
import com.liferay.exportimport.rest.resource.v1_0.ImportProcessResource;
import com.liferay.headless.delivery.dto.v1_0.util.CreatorUtil;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.OrderFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.NoSuchBackgroundTaskException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.staging.StagingGroupHelper;

import jakarta.ws.rs.NotFoundException;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Petteri Karttunen
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/import-process.properties",
	scope = ServiceScope.PROTOTYPE, service = ImportProcessResource.class
)
public class ImportProcessResourceImpl extends BaseImportProcessResourceImpl {

	@Override
	public Page<ImportProcess> getAssetLibraryImportProcessesPage(
			Long assetLibraryId, Long creatorId, String search, Integer status,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-35914")) {

			throw new NotFoundException();
		}

		return Page.of(
			transform(
				_getBackgroundTasks(
					creatorId, assetLibraryId, pagination, search, sorts,
					status),
				this::_toImportProcess),
			pagination,
			_backgroundTaskLocalService.dynamicQueryCount(
				_getDynamicQuery(creatorId, assetLibraryId, search, status)));
	}

	@Override
	public ImportProcess getImportProcess(Long importProcessId)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-35914")) {

			throw new NotFoundException();
		}

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(importProcessId);

		PermissionUtil.checkPermission(
			contextCompany.getCompanyId(), backgroundTask.getGroupId());

		if (!StringUtil.equals(
				backgroundTask.getTaskExecutorClassName(),
				BackgroundTaskExecutorNames.
					LAYOUT_IMPORT_BACKGROUND_TASK_EXECUTOR)) {

			throw new NoSuchBackgroundTaskException();
		}

		return _toImportProcess(backgroundTask);
	}

	@Override
	public Page<ImportProcess> getImportProcessesPage(
			Long creatorId, String search, Integer status,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-35914")) {

			throw new NotFoundException();
		}

		long groupId = _getCompanyGroupId();

		return Page.of(
			transform(
				_getBackgroundTasks(
					creatorId, groupId, pagination, search, sorts, status),
				this::_toImportProcess),
			pagination,
			_backgroundTaskLocalService.dynamicQueryCount(
				_getDynamicQuery(creatorId, groupId, search, status)));
	}

	@Override
	public Page<ImportProcess> getSiteImportProcessesPage(
			Long siteId, Long creatorId, String search, Integer status,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-35914")) {

			throw new NotFoundException();
		}

		return Page.of(
			transform(
				_getBackgroundTasks(
					creatorId, siteId, pagination, search, sorts, status),
				this::_toImportProcess),
			pagination,
			_backgroundTaskLocalService.dynamicQueryCount(
				_getDynamicQuery(creatorId, siteId, search, status)));
	}

	private List<BackgroundTask> _getBackgroundTasks(
			Long creatorId, long groupId, Pagination pagination, String search,
			Sort[] sorts, Integer status)
		throws Exception {

		PermissionUtil.checkPermission(contextCompany.getCompanyId(), groupId);

		DynamicQuery dynamicQuery = _getDynamicQuery(
			creatorId, groupId, search, status);

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
		Long creatorId, long groupId, String search, Integer status) {

		DynamicQuery dynamicQuery = _backgroundTaskLocalService.dynamicQuery();

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"companyId", contextCompany.getCompanyId()));
		dynamicQuery.add(RestrictionsFactoryUtil.eq("groupId", groupId));

		if (!Validator.isBlank(search)) {
			dynamicQuery.add(RestrictionsFactoryUtil.ilike("name", search));
		}

		if (status != null) {
			dynamicQuery.add(RestrictionsFactoryUtil.eq("status", status));
		}

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"taskExecutorClassName",
				BackgroundTaskExecutorNames.
					LAYOUT_IMPORT_BACKGROUND_TASK_EXECUTOR));

		if (creatorId != null) {
			dynamicQuery.add(RestrictionsFactoryUtil.eq("userId", creatorId));
		}

		return dynamicQuery;
	}

	private void _setSorts(DynamicQuery dynamicQuery, Sort[] sorts) {
		if (sorts == null) {
			dynamicQuery.addOrder(OrderFactoryUtil.desc("modifiedDate"));

			return;
		}

		for (Sort sort : sorts) {
			String fieldName = sort.getFieldName();

			fieldName = StringUtil.removeSubstring(fieldName, "_sortable");

			if (fieldName.equals("creator")) {
				fieldName = "userName";
			}
			else if (fieldName.equals("dateCreated")) {
				fieldName = "createDate";
			}
			else if (fieldName.equals("id")) {
				fieldName = "backgroundTaskId";
			}
			else if (fieldName.equals("dateModified")) {
				fieldName = "modifiedDate";
			}
			else if (fieldName.equals("title")) {
				fieldName = "name";
			}

			if (sort.isReverse()) {
				dynamicQuery.addOrder(OrderFactoryUtil.desc(fieldName));
			}
			else {
				dynamicQuery.addOrder(OrderFactoryUtil.asc(fieldName));
			}
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
				setDateCreated(backgroundTask::getCreateDate);
				setDateModified(backgroundTask::getModifiedDate);
				setId(backgroundTask::getBackgroundTaskId);
				setStatus(() -> _toStatus(backgroundTask.getStatus()));
				setTitle(backgroundTask::getName);
			}
		};
	}

	private Status _toStatus(int status) {
		return new Status() {
			{
				setCode(() -> status);
				setLabel(
					() -> _language.get(
						contextAcceptLanguage.getPreferredLocale(),
						BackgroundTaskConstants.getStatusLabel(status)));
			}
		};
	}

	@Reference
	private BackgroundTaskLocalService _backgroundTaskLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private StagingGroupHelper _stagingGroupHelper;

	@Reference
	private UserLocalService _userLocalService;

}