/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.scheduler;

import com.liferay.journal.configuration.JournalServiceConfiguration;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.TriggerConfiguration;
import com.liferay.portal.kernel.service.CompanyLocalService;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 * @author Tina Tian
 */
@Component(
	configurationPid = "com.liferay.journal.configuration.JournalServiceConfiguration",
	service = SchedulerJobConfiguration.class
)
public class CheckArticleSchedulerJobConfiguration
	implements SchedulerJobConfiguration {

	@Override
	public UnsafeConsumer<Long, Exception>
		getCompanyJobExecutorUnsafeConsumer() {

		return companyId -> _checkArticles(companyId);
	}

	@Override
	public UnsafeRunnable<Exception> getJobExecutorUnsafeRunnable() {
		return () -> _companyLocalService.forEachCompanyId(
			companyId -> _checkArticles(companyId));
	}

	@Override
	public TriggerConfiguration getTriggerConfiguration() {
		return _triggerConfiguration;
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		JournalServiceConfiguration journalServiceConfiguration =
			ConfigurableUtil.createConfigurable(
				JournalServiceConfiguration.class, properties);

		_triggerConfiguration = TriggerConfiguration.createTriggerConfiguration(
			journalServiceConfiguration.checkInterval(), TimeUnit.MINUTE);
	}

	private void _checkArticles(long companyId) {
		try {
			_journalArticleLocalService.checkArticles(companyId);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to check articles for company " + companyId,
					exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CheckArticleSchedulerJobConfiguration.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	private TriggerConfiguration _triggerConfiguration;

}