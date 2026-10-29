/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.resource.v1_0;

import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationParameterMapFactory;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactory;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.exportimport.portlet.data.handler.provider.PortletDataHandlerProvider;
import com.liferay.exportimport.rest.dto.v1_0.PortletDataHandler;
import com.liferay.exportimport.rest.dto.v1_0.PublishProcessRequest;
import com.liferay.exportimport.rest.dto.v1_0.ScheduledPublishProcess;
import com.liferay.exportimport.rest.dto.v1_0.ScheduledPublishProcessRequest;
import com.liferay.exportimport.rest.internal.util.GroupUtil;
import com.liferay.exportimport.rest.internal.util.LayoutUtil;
import com.liferay.exportimport.rest.internal.util.ParameterMapUtil;
import com.liferay.exportimport.rest.internal.util.PermissionUtil;
import com.liferay.exportimport.rest.internal.util.PortletDataHandlerUtil;
import com.liferay.exportimport.rest.resource.v1_0.ScheduledPublishProcessResource;
import com.liferay.headless.delivery.dto.v1_0.util.CreatorUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.Trigger;
import com.liferay.portal.kernel.scheduler.TriggerFactory;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

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
			_getExportPortletDataHandlers(stagingGroup),
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

		PortletDataHandler[] exportPortletDataHandlers =
			_getExportPortletDataHandlers(stagingGroup);
		Group liveGroup = GroupUtil.getLiveGroup(stagingGroup);

		List<ScheduledPublishProcess> scheduledPublishProcesses =
			new ArrayList<>(
				transform(
					_schedulerEngineHelper.getScheduledJobs(
						_staging.getSchedulerGroupName(
							DestinationNames.LAYOUTS_LOCAL_PUBLISHER,
							liveGroup.getGroupId()),
						StorageType.PERSISTED),
					schedulerResponse -> _toScheduledPublishProcess(
						exportPortletDataHandlers, schedulerResponse)));

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

	@Override
	public ScheduledPublishProcess postSiteScheduledPublishProcess(
			String siteExternalReferenceCode,
			ScheduledPublishProcessRequest scheduledPublishProcessRequest)
		throws Exception {

		Group stagingGroup = GroupUtil.getStagingGroup(
			GroupUtil.getSiteGroup(
				contextCompany.getCompanyId(), siteExternalReferenceCode));

		long stagingGroupId = stagingGroup.getGroupId();

		PermissionUtil.checkPublishPermission(stagingGroupId);

		PublishProcessRequest publishProcessRequest =
			scheduledPublishProcessRequest.getPublishProcessRequest();

		if (publishProcessRequest == null) {
			throw new BadRequestException(
				"A publish process request is required");
		}

		String cronExpression =
			scheduledPublishProcessRequest.getCronExpression();

		if (Validator.isBlank(cronExpression)) {
			throw new BadRequestException("A cron expression is required");
		}

		Date date = new Date();
		Date scheduleStartDate =
			scheduledPublishProcessRequest.getScheduleStartDate();

		if (scheduleStartDate == null) {
			scheduleStartDate = date;
		}
		else if (scheduleStartDate.before(date)) {
			throw new BadRequestException(
				"The schedule start date must be in the future");
		}

		String timeZoneId = scheduledPublishProcessRequest.getTimeZoneId();

		if (Validator.isBlank(timeZoneId)) {
			TimeZone timeZone = TimeZoneUtil.getDefault();

			timeZoneId = timeZone.getID();
		}
		else if (!ArrayUtil.contains(TimeZone.getAvailableIDs(), timeZoneId)) {
			throw new BadRequestException(
				"No time zone exists with ID \"" + timeZoneId + "\"");
		}

		Group liveGroup = GroupUtil.getLiveGroup(stagingGroup);

		String groupName = _staging.getSchedulerGroupName(
			DestinationNames.LAYOUTS_LOCAL_PUBLISHER, liveGroup.getGroupId());

		Trigger trigger = _getTrigger(
			cronExpression, groupName,
			scheduledPublishProcessRequest.getScheduleEndDate(),
			scheduleStartDate, timeZoneId);

		PortletDataHandler[] exportPortletDataHandlers =
			_getExportPortletDataHandlers(stagingGroup);

		Map<String, String[]> parameterMap =
			ParameterMapUtil.putDateRangeParameters(
				publishProcessRequest.getDateFilter(), date,
				ParameterMapUtil.toParameterMap(
					publishProcessRequest, exportPortletDataHandlers),
				contextUser);

		parameterMap =
			_exportImportConfigurationParameterMapFactory.buildParameterMap(
				parameterMap);

		parameterMap.put(
			ParameterMapUtil.CRON_EXPRESSION, new String[] {cronExpression});
		parameterMap.put(
			ParameterMapUtil.TIME_ZONE_ID, new String[] {timeZoneId});
		parameterMap.put(
			PortletDataHandlerKeys.PERFORM_DIRECT_BINARY_IMPORT,
			new String[] {Boolean.TRUE.toString()});

		boolean privateLayout = LayoutUtil.isPrivateLayout(parameterMap);

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					contextUser.getUserId(), publishProcessRequest.getName(),
					ExportImportConfigurationConstants.
						TYPE_SCHEDULED_PUBLISH_LAYOUT_LOCAL,
					_exportImportConfigurationSettingsMapFactory.
						buildPublishLayoutLocalSettingsMap(
							contextUser, stagingGroupId, liveGroup.getGroupId(),
							privateLayout,
							LayoutUtil.getLayoutIds(
								_layoutLocalService.getLayouts(
									stagingGroupId, privateLayout),
								parameterMap),
							parameterMap));

		Message message = new Message();

		message.put("companyId", contextCompany.getCompanyId());
		message.setPayload(
			exportImportConfiguration.getExportImportConfigurationId());

		_schedulerEngineHelper.schedule(
			trigger, StorageType.PERSISTED, publishProcessRequest.getName(),
			DestinationNames.LAYOUTS_LOCAL_PUBLISHER, message);

		return _toScheduledPublishProcess(
			exportPortletDataHandlers,
			_getSchedulerResponse(
				groupName,
				exportImportConfiguration.getExportImportConfigurationId()));
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

	private PortletDataHandler[] _getExportPortletDataHandlers(
			Group stagingGroup)
		throws Exception {

		return PortletDataHandlerUtil.getExportPortletDataHandlers(
			stagingGroup, contextAcceptLanguage.getPreferredLocale(), 0,
			_portletDataHandlerProvider,
			_exportImportHelper.getExportablePortlets(
				contextCompany.getCompanyId(), false,
				stagingGroup.getGroupId()),
			false);
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

	private Trigger _getTrigger(
		String cronExpression, String groupName, Date scheduleEndDate,
		Date scheduleStartDate, String timeZoneId) {

		Trigger trigger = null;

		try {
			trigger = _triggerFactory.createTrigger(
				StringBundler.concat(
					PortalUUIDUtil.generate(), StringPool.AT,
					contextCompany.getCompanyId()),
				groupName, scheduleStartDate, scheduleEndDate, cronExpression,
				TimeZone.getTimeZone(timeZoneId));
		}
		catch (Exception exception) {
			throw new BadRequestException(
				"The publication schedule is invalid", exception);
		}

		if (trigger.getFireDateAfter(new Date(0)) == null) {
			throw new BadRequestException(
				"The publication schedule never runs");
		}

		return trigger;
	}

	private ScheduledPublishProcess _toScheduledPublishProcess(
		PortletDataHandler[] exportPortletDataHandlers,
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
				setScheduledPublishProcessRequest(
					() -> {
						if (parameterMap == null) {
							return null;
						}

						return _toScheduledPublishProcessRequest(
							exportImportConfiguration,
							exportPortletDataHandlers, parameterMap,
							schedulerResponse);
					});
				setScheduleEndDate(
					() -> _schedulerEngineHelper.getEndDate(schedulerResponse));
				setScheduleStartDate(
					() -> _schedulerEngineHelper.getStartDate(
						schedulerResponse));
			}
		};
	}

	private ScheduledPublishProcessRequest _toScheduledPublishProcessRequest(
			ExportImportConfiguration exportImportConfiguration,
			PortletDataHandler[] exportPortletDataHandlers,
			Map<String, String[]> parameterMap,
			SchedulerResponse schedulerResponse)
		throws Exception {

		Map<String, Serializable> settingsMap =
			exportImportConfiguration.getSettingsMap();

		PublishProcessRequest storedPublishProcessRequest =
			ParameterMapUtil.toPublishProcessRequest(
				exportPortletDataHandlers, (Locale)settingsMap.get("locale"),
				parameterMap, (TimeZone)settingsMap.get("timezone"));

		storedPublishProcessRequest.setName(schedulerResponse::getDescription);

		return new ScheduledPublishProcessRequest() {
			{
				setCronExpression(
					() -> MapUtil.getString(
						parameterMap, ParameterMapUtil.CRON_EXPRESSION, null));
				setPublishProcessRequest(() -> storedPublishProcessRequest);
				setScheduleEndDate(
					() -> _schedulerEngineHelper.getEndDate(schedulerResponse));
				setScheduleStartDate(
					() -> _schedulerEngineHelper.getStartDate(
						schedulerResponse));
				setTimeZoneId(
					() -> MapUtil.getString(
						parameterMap, ParameterMapUtil.TIME_ZONE_ID, null));
			}
		};
	}

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
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutService _layoutService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletDataHandlerProvider _portletDataHandlerProvider;

	@Reference
	private SchedulerEngineHelper _schedulerEngineHelper;

	@Reference
	private Staging _staging;

	@Reference
	private TriggerFactory _triggerFactory;

	@Reference
	private UserLocalService _userLocalService;

}