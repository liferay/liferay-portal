/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.resource.v1_0;

import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.exportimport.rest.dto.v1_0.ScheduledPublishProcess;
import com.liferay.exportimport.rest.internal.util.GroupUtil;
import com.liferay.exportimport.rest.internal.util.ParameterMapUtil;
import com.liferay.exportimport.rest.internal.util.PermissionUtil;
import com.liferay.exportimport.rest.resource.v1_0.ScheduledPublishProcessResource;
import com.liferay.headless.delivery.dto.v1_0.util.CreatorUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.NotFoundException;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Daniel Raposo
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/scheduled-publish-process.properties",
	scope = ServiceScope.PROTOTYPE,
	service = ScheduledPublishProcessResource.class
)
public class ScheduledPublishProcessResourceImpl
	extends BaseScheduledPublishProcessResourceImpl {

	@Override
	public void deleteSiteScheduledPublishProcess(
			String siteExternalReferenceCode, Long scheduledPublishProcessId)
		throws Exception {

		Group stagingGroup = GroupUtil.getStagingGroup(
			GroupUtil.getSiteGroup(
				contextCompany.getCompanyId(), siteExternalReferenceCode));

		PermissionUtil.checkPublishPermission(stagingGroup.getGroupId());

		Group liveGroup = GroupUtil.getLiveGroup(stagingGroup);

		String groupName = _staging.getSchedulerGroupName(
			DestinationNames.LAYOUTS_LOCAL_PUBLISHER, liveGroup.getGroupId());

		SchedulerResponse schedulerResponse = _getSchedulerResponse(
			groupName, scheduledPublishProcessId);

		_layoutService.unschedulePublishToLive(
			liveGroup.getGroupId(), schedulerResponse.getJobName(), groupName);

		_exportImportConfigurationLocalService.deleteExportImportConfiguration(
			scheduledPublishProcessId);
	}

	@Override
	public ScheduledPublishProcess getSiteScheduledPublishProcess(
			String siteExternalReferenceCode, Long scheduledPublishProcessId)
		throws Exception {

		Group stagingGroup = GroupUtil.getStagingGroup(
			GroupUtil.getSiteGroup(
				contextCompany.getCompanyId(), siteExternalReferenceCode));

		PermissionUtil.checkPublishPermission(stagingGroup.getGroupId());

		Group liveGroup = GroupUtil.getLiveGroup(stagingGroup);

		return _toScheduledPublishProcess(
			_getSchedulerResponse(
				_staging.getSchedulerGroupName(
					DestinationNames.LAYOUTS_LOCAL_PUBLISHER,
					liveGroup.getGroupId()),
				scheduledPublishProcessId));
	}

	@Override
	public Page<ScheduledPublishProcess> getSiteScheduledPublishProcessesPage(
			String siteExternalReferenceCode, String search,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		Group stagingGroup = GroupUtil.getStagingGroup(
			GroupUtil.getSiteGroup(
				contextCompany.getCompanyId(), siteExternalReferenceCode));

		PermissionUtil.checkPublishPermission(stagingGroup.getGroupId());

		Group liveGroup = GroupUtil.getLiveGroup(stagingGroup);

		List<ScheduledPublishProcess> scheduledPublishProcesses =
			new ArrayList<>(
				transform(
					_schedulerEngineHelper.getScheduledJobs(
						_staging.getSchedulerGroupName(
							DestinationNames.LAYOUTS_LOCAL_PUBLISHER,
							liveGroup.getGroupId()),
						StorageType.PERSISTED),
					this::_toScheduledPublishProcess));

		if (!Validator.isBlank(search)) {
			scheduledPublishProcesses.removeIf(
				scheduledPublishProcess -> !StringUtil.containsIgnoreCase(
					GetterUtil.getString(scheduledPublishProcess.getName()),
					search, StringPool.BLANK));
		}

		Comparator<ScheduledPublishProcess> comparator = _getComparator(sorts);

		if (comparator == null) {
			comparator = _getFieldComparator(
				"dateCreated"
			).reversed();
		}

		scheduledPublishProcesses.sort(comparator);

		return Page.of(
			ListUtil.subList(
				scheduledPublishProcesses, pagination.getStartPosition(),
				pagination.getEndPosition()),
			pagination, scheduledPublishProcesses.size());
	}

	private Comparator<ScheduledPublishProcess> _getComparator(Sort[] sorts) {
		if (ArrayUtil.isEmpty(sorts)) {
			return null;
		}

		Comparator<ScheduledPublishProcess> comparator = null;

		for (Sort sort : sorts) {
			Comparator<ScheduledPublishProcess> fieldComparator =
				_getFieldComparator(
					StringUtil.removeSubstring(
						sort.getFieldName(), "_sortable"));

			if (fieldComparator == null) {
				continue;
			}

			if (sort.isReverse()) {
				fieldComparator = fieldComparator.reversed();
			}

			if (comparator == null) {
				comparator = fieldComparator;
			}
			else {
				comparator = comparator.thenComparing(fieldComparator);
			}
		}

		return comparator;
	}

	private Comparator<ScheduledPublishProcess> _getFieldComparator(
		String fieldName) {

		if (fieldName.equals("dateCreated")) {
			return Comparator.comparing(
				ScheduledPublishProcess::getDateCreated,
				Comparator.nullsLast(Comparator.naturalOrder()));
		}

		if (fieldName.equals("name")) {
			return Comparator.comparing(
				ScheduledPublishProcess::getName,
				Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
		}

		if (fieldName.equals("nextFireDate")) {
			return Comparator.comparing(
				ScheduledPublishProcess::getNextFireDate,
				Comparator.nullsLast(Comparator.naturalOrder()));
		}

		if (fieldName.equals("scheduleEndDate")) {
			return Comparator.comparing(
				ScheduledPublishProcess::getScheduleEndDate,
				Comparator.nullsLast(Comparator.naturalOrder()));
		}

		return null;
	}

	private Map<String, String[]> _getParameterMap(
		ExportImportConfiguration exportImportConfiguration) {

		if (exportImportConfiguration == null) {
			return null;
		}

		Map<String, Serializable> settingsMap =
			exportImportConfiguration.getSettingsMap();

		return (Map<String, String[]>)settingsMap.get("parameterMap");
	}

	private SchedulerResponse _getSchedulerResponse(
			String groupName, Long scheduledPublishProcessId)
		throws Exception {

		for (SchedulerResponse schedulerResponse :
				_schedulerEngineHelper.getScheduledJobs(
					groupName, StorageType.PERSISTED)) {

			Message message = schedulerResponse.getMessage();

			if (scheduledPublishProcessId == GetterUtil.getLong(
					message.getPayload())) {

				return schedulerResponse;
			}
		}

		throw new NotFoundException(
			"No scheduled publish process was found with ID " +
				scheduledPublishProcessId);
	}

	private ScheduledPublishProcess _toScheduledPublishProcess(
		SchedulerResponse schedulerResponse) {

		Message message = schedulerResponse.getMessage();

		long exportImportConfigurationId = GetterUtil.getLong(
			message.getPayload());

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.
				fetchExportImportConfiguration(exportImportConfigurationId);

		Map<String, String[]> parameterMap = _getParameterMap(
			exportImportConfiguration);

		return new ScheduledPublishProcess() {
			{
				setCreator(
					() -> {
						if (exportImportConfiguration == null) {
							return null;
						}

						return CreatorUtil.toCreator(
							null, _portal,
							_userLocalService.fetchUser(
								exportImportConfiguration.getUserId()));
					});
				setCronExpression(
					() -> {
						if (parameterMap == null) {
							return null;
						}

						return GetterUtil.getString(
							ArrayUtil.getValue(
								parameterMap.get(
									ParameterMapUtil.CRON_EXPRESSION),
								0),
							null);
					});
				setDateCreated(
					() -> {
						if (exportImportConfiguration == null) {
							return null;
						}

						return exportImportConfiguration.getCreateDate();
					});
				setId(() -> exportImportConfigurationId);
				setName(schedulerResponse::getDescription);
				setNextFireDate(
					() -> _schedulerEngineHelper.getNextFireDate(
						schedulerResponse));
				setPublishParameters(() -> parameterMap);
				setScheduleEndDate(
					() -> _schedulerEngineHelper.getEndDate(schedulerResponse));
				setScheduleStartDate(
					() -> _schedulerEngineHelper.getStartDate(
						schedulerResponse));
			}
		};
	}

	@Reference
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Reference
	private LayoutService _layoutService;

	@Reference
	private Portal _portal;

	@Reference
	private SchedulerEngineHelper _schedulerEngineHelper;

	@Reference
	private Staging _staging;

	@Reference
	private UserLocalService _userLocalService;

}