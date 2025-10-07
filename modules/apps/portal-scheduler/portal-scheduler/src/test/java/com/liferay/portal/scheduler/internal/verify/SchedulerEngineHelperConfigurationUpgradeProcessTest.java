/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.scheduler.internal.verify;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.scheduler.internal.configuration.SchedulerEngineHelperConfiguration;
import com.liferay.portal.scheduler.internal.upgrade.v1_0_0.SchedulerEngineHelperConfigurationUpgradeProcess;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Dictionary;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Michael C. Han
 * @author Alberto Chaparro
 */
public class SchedulerEngineHelperConfigurationUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testNoVerify() throws Exception {
		ConfigurationAdmin configurationAdmin = Mockito.mock(
			ConfigurationAdmin.class);

		SchedulerEngineHelperConfigurationUpgradeProcess
			schedulerEngineHelperConfigurationUpgradeProcess =
				new SchedulerEngineHelperConfigurationUpgradeProcess(
					configurationAdmin);

		Mockito.when(
			configurationAdmin.getConfiguration(
				SchedulerEngineHelperConfiguration.class.getName())
		).then(
			new Answer<Object>() {

				@Override
				public Object answer(InvocationOnMock invocationOnMock)
					throws Throwable {

					Assert.fail("No properties should have been verified");

					return null;
				}

			}
		);

		schedulerEngineHelperConfigurationUpgradeProcess.doUpgrade();
	}

	@Test
	public void testVerify() throws Exception {
		ConfigurationAdmin configurationAdmin = Mockito.mock(
			ConfigurationAdmin.class);

		PropsUtil.set(_LEGACY_AUDIT_MESSAGE_SCHEDULER_JOB, "true");

		SchedulerEngineHelperConfigurationUpgradeProcess
			schedulerEngineHelperConfigurationUpgradeProcess =
				new SchedulerEngineHelperConfigurationUpgradeProcess(
					configurationAdmin);

		Configuration configuration = Mockito.mock(Configuration.class);

		Mockito.when(
			configurationAdmin.getConfiguration(
				SchedulerEngineHelperConfiguration.class.getName(),
				StringPool.QUESTION)
		).thenReturn(
			configuration
		);

		schedulerEngineHelperConfigurationUpgradeProcess.doUpgrade();

		Mockito.verify(
			configuration
		).update(
			_argumentCaptor.capture()
		);

		Dictionary<String, Object> dictionary = _argumentCaptor.getValue();

		Assert.assertEquals(1, dictionary.size());

		Assert.assertEquals(
			Boolean.TRUE, dictionary.get(_AUDIT_SCHEDULER_JOB_ENABLED));
	}

	private static final String _AUDIT_SCHEDULER_JOB_ENABLED =
		"auditSchedulerJobEnabled";

	private static final String _LEGACY_AUDIT_MESSAGE_SCHEDULER_JOB =
		"audit.message.scheduler.job";

	private final ArgumentCaptor<Dictionary<String, Object>> _argumentCaptor =
		ArgumentCaptor.forClass(Dictionary.class);

}