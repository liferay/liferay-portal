/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.partition.internal.messaging;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.db.partition.internal.configuration.DBPartitionConfiguration;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.messaging.MessageBusInterceptor;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.scheduler.SchedulerEngine;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.util.PortalInstances;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alberto Chaparro
 */
@Component(
	configurationPid = "com.liferay.portal.db.partition.internal.configuration.DBPartitionConfiguration",
	service = MessageBusInterceptor.class
)
public class DBPartitionMessageBusInterceptor implements MessageBusInterceptor {

	@Override
	public boolean intercept(
		MessageBus messageBus, String destinationName, Message message) {

		if (PropsValues.DATABASE_PARTITION_ENABLED &&
			(message.getLong("companyId") == CompanyConstants.SYSTEM) &&
			!_excludedMessageBusDestinationNames.contains(destinationName) &&
			!_excludedSchedulerJobNames.contains(
				message.getString(SchedulerEngine.JOB_NAME))) {

			List<Long> companyIds = new ArrayList<>();

			_companyLocalService.forEachCompany(
				company -> {
					if (!company.isActive() ||
						PortalInstances.isCompanyInDeletionProcess(
							company.getCompanyId())) {

						return;
					}

					companyIds.add(company.getCompanyId());
				});

			message.remove("companyId");

			message.put("companyIds", companyIds.toArray(new Long[0]));
		}

		return false;
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		modified(properties);
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		DBPartitionConfiguration dbPartitionConfiguration =
			ConfigurableUtil.createConfigurable(
				DBPartitionConfiguration.class, properties);

		_excludedMessageBusDestinationNames = SetUtil.fromArray(
			dbPartitionConfiguration.excludedMessageBusDestinationNames());
		_excludedSchedulerJobNames = SetUtil.fromArray(
			dbPartitionConfiguration.excludedSchedulerJobNames());
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	private volatile Set<String> _excludedMessageBusDestinationNames;
	private volatile Set<String> _excludedSchedulerJobNames;

}