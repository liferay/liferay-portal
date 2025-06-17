/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.scheduler.internal;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.cluster.ClusterMasterExecutor;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.scheduler.SchedulerEngine;
import com.liferay.portal.kernel.scheduler.SchedulerEngineAuditor;
import com.liferay.portal.kernel.scheduler.SchedulerException;
import com.liferay.portal.kernel.scheduler.TriggerState;
import com.liferay.portal.kernel.util.InetAddressUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.scheduler.internal.configuration.SchedulerEngineHelperConfiguration;

import java.util.Date;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Dante Wang
 */
@Component(
	configurationPid = "com.liferay.portal.scheduler.internal.configuration.SchedulerEngineHelperConfiguration",
	enabled = false, service = SchedulerEngineAuditor.class
)
public class SchedulerEngineAuditorImpl implements SchedulerEngineAuditor {

	@Override
	public void auditSchedulerJobs(Message message, TriggerState triggerState)
		throws SchedulerException {

		if (!_clusterMasterExecutor.isMaster()) {
			return;
		}

		AuditRouter auditRouter = _auditRouterSnapshot.get();

		if (!_schedulerEngineHelperConfiguration.auditSchedulerJobEnabled() ||
			(auditRouter == null)) {

			return;
		}

		try {
			AuditMessage auditMessage = new AuditMessage(
				SchedulerEngine.SCHEDULER, CompanyConstants.SYSTEM, 0,
				StringPool.BLANK, SchedulerEngine.class.getName(), "0",
				triggerState.toString(), new Date(),
				_jsonFactory.createJSONObject(_jsonFactory.serialize(message)));

			auditMessage.setServerName(InetAddressUtil.getLocalHostName());
			auditMessage.setServerPort(_portal.getPortalLocalPort(false));

			auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			throw new SchedulerException(exception);
		}
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_schedulerEngineHelperConfiguration =
			ConfigurableUtil.createConfigurable(
				SchedulerEngineHelperConfiguration.class, properties);
	}

	private static final Snapshot<AuditRouter> _auditRouterSnapshot =
		new Snapshot<>(
			SchedulerEngineAuditorImpl.class, AuditRouter.class, null, true);

	@Reference
	private ClusterMasterExecutor _clusterMasterExecutor;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

	private volatile SchedulerEngineHelperConfiguration
		_schedulerEngineHelperConfiguration;

}