/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.scheduler.internal.upgrade.v1_0_0;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.scheduler.internal.configuration.SchedulerEngineHelperConfiguration;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Michael C. Han
 * @author Alberto Chaparro
 */
public class SchedulerEngineHelperConfigurationUpgradeProcess
	extends UpgradeProcess {

	public SchedulerEngineHelperConfigurationUpgradeProcess(
		ConfigurationAdmin configurationAdmin) {

		_configurationAdmin = configurationAdmin;
	}

	@Override
	public void doUpgrade() throws Exception {
		String audiMessageScheduleJobString = PropsUtil.get(
			_LEGACY_AUDIT_MESSAGE_SCHEDULER_JOB);

		if (Validator.isNull(audiMessageScheduleJobString)) {
			return;
		}

		Configuration configuration = _configurationAdmin.getConfiguration(
			SchedulerEngineHelperConfiguration.class.getName(),
			StringPool.QUESTION);

		configuration.update(
			HashMapDictionaryBuilder.<String, Object>put(
				_AUDIT_SCHEDULER_JOB_ENABLED,
				GetterUtil.getBoolean(audiMessageScheduleJobString)
			).build());
	}

	private static final String _AUDIT_SCHEDULER_JOB_ENABLED =
		"auditSchedulerJobEnabled";

	private static final String _LEGACY_AUDIT_MESSAGE_SCHEDULER_JOB =
		"audit.message.scheduler.job";

	private final ConfigurationAdmin _configurationAdmin;

}