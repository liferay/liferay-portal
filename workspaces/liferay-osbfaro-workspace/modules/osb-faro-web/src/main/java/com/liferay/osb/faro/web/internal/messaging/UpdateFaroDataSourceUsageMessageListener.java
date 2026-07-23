/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.messaging;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.DataSourceUsageMetric;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.service.FaroDataSourceUsageLocalService;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.osb.faro.web.internal.constants.FaroMessageDestinationNames;
import com.liferay.osb.faro.web.internal.messaging.destination.creator.DestinationCreator;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.DestinationFactory;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.Trigger;
import com.liferay.portal.kernel.scheduler.TriggerFactory;
import com.liferay.portal.kernel.util.Time;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Caio Pinheiro
 */
@Component(
	property = "destination.name=" + FaroMessageDestinationNames.FARO_UPDATE_FARO_DATA_SOURCE_USAGE_MESSAGE_PROCESSOR,
	service = MessageListener.class
)
public class UpdateFaroDataSourceUsageMessageListener
	extends BaseMessageListener {

	@Activate
	protected void activate(BundleContext bundleContext) {
		try {
			_destinationCreator.createDestination(
				bundleContext, _destinationFactory,
				FaroMessageDestinationNames.
					FARO_UPDATE_FARO_DATA_SOURCE_USAGE_MESSAGE_PROCESSOR);

			Class<?> clazz = getClass();

			_trigger = _triggerFactory.createTrigger(
				clazz.getName(), clazz.getName(), new Date(), null,
				"0 0 1 * * ?");

			_schedulerEngineHelper.schedule(
				_trigger, StorageType.PERSISTED, null,
				FaroMessageDestinationNames.
					FARO_UPDATE_FARO_DATA_SOURCE_USAGE_MESSAGE_PROCESSOR,
				null);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	@Deactivate
	protected void deactivate() {
		try {
			if (_destinationCreator != null) {
				_destinationCreator.removeDestination();

				_destinationCreator = null;
			}

			if (_trigger == null) {
				return;
			}

			_schedulerEngineHelper.delete(
				_trigger.getJobName(), _trigger.getGroupName(),
				StorageType.PERSISTED);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	@Override
	protected void doReceive(Message message) throws Exception {
		Calendar calendar = Calendar.getInstance();

		calendar.setTimeInMillis(
			System.currentTimeMillis() / Time.DAY * Time.DAY);

		calendar.add(Calendar.DATE, -1);

		Date date = calendar.getTime();

		Map<String, Map<String, List<DataSourceUsageMetric>>>
			dataSourceUsageMetricsMap = new HashMap<>();

		for (FaroProject faroProject :
				_faroProjectLocalService.getFaroProjects(
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			try {
				_addOrUpdateFaroDataSourceUsages(
					dataSourceUsageMetricsMap, date, faroProject);
			}
			catch (Exception exception) {
				_log.error(
					"Unable to add Faro data source usage for " + faroProject,
					exception);
			}
		}
	}

	private void _addOrUpdateFaroDataSourceUsages(
			Map<String, Map<String, List<DataSourceUsageMetric>>>
				dataSourceUsageMetricsMap,
			Date date, FaroProject faroProject)
		throws Exception {

		Map<String, List<DataSourceUsageMetric>> map =
			dataSourceUsageMetricsMap.get(faroProject.getServerLocation());

		if (map == null) {
			map = new HashMap<>();

			Results<DataSourceUsageMetric> results =
				_contactsEngineClient.getDataSourceUsageMetrics(
					faroProject, date);

			for (DataSourceUsageMetric dataSourceUsageMetric :
					results.getItems()) {

				List<DataSourceUsageMetric> dataSourceUsageMetrics =
					map.computeIfAbsent(
						dataSourceUsageMetric.getProjectId(),
						projectId -> new ArrayList<>());

				dataSourceUsageMetrics.add(dataSourceUsageMetric);
			}

			dataSourceUsageMetricsMap.put(faroProject.getServerLocation(), map);
		}

		List<DataSourceUsageMetric> dataSourceUsageMetrics = map.get(
			faroProject.getProjectId());

		if (dataSourceUsageMetrics == null) {
			return;
		}

		for (DataSourceUsageMetric dataSourceUsageMetric :
				dataSourceUsageMetrics) {

			_faroDataSourceUsageLocalService.addOrUpdateFaroDataSourceUsage(
				faroProject.getUserId(),
				dataSourceUsageMetric.getBillableEventsCount(),
				dataSourceUsageMetric.getDataSourceId(),
				dataSourceUsageMetric.getDataSourceName(),
				dataSourceUsageMetric.getDataSourceStatus(),
				faroProject.getFaroProjectId(),
				dataSourceUsageMetric.getKnownIndividualsCount(), date);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpdateFaroDataSourceUsageMessageListener.class);

	@Reference
	private ContactsEngineClient _contactsEngineClient;

	private DestinationCreator _destinationCreator = new DestinationCreator();

	@Reference
	private DestinationFactory _destinationFactory;

	@Reference
	private FaroDataSourceUsageLocalService _faroDataSourceUsageLocalService;

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

	@Reference
	private SchedulerEngineHelper _schedulerEngineHelper;

	private Trigger _trigger;

	@Reference
	private TriggerFactory _triggerFactory;

}