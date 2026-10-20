/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.talend.web.internal.executor;

import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.dispatch.talend.archive.TalendArchive;
import com.liferay.dispatch.talend.web.internal.process.TalendProcess;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.process.ProcessConfig;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PortalClassPathUtil;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Caio Farias
 */
public class TalendDispatchTaskExecutorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		PortalClassPathUtil.initializeClassPaths(null);
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceTrackerFactoryMockedStatic.close();
	}

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_talendDispatchTaskExecutor, "_dispatchTriggerLocalService",
			Mockito.mock(DispatchTriggerLocalService.class));
		ReflectionTestUtil.setFieldValue(
			_talendDispatchTaskExecutor, "_secretResolver", _secretResolver);
	}

	@Test
	public void testGetTalendProcess() {
		DispatchTrigger dispatchTrigger = Mockito.mock(DispatchTrigger.class);

		long companyId = RandomTestUtil.randomLong();

		Mockito.when(
			dispatchTrigger.getCompanyId()
		).thenReturn(
			companyId
		);

		String javaOpts = RandomTestUtil.randomString();
		String name = RandomTestUtil.randomString();
		String value = RandomTestUtil.randomString();

		Mockito.when(
			dispatchTrigger.getDispatchTaskSettingsUnicodeProperties()
		).thenReturn(
			UnicodePropertiesBuilder.put(
				name, value
			).put(
				"JAVA_OPTS", javaOpts
			).build()
		);

		long dispatchTriggerId = RandomTestUtil.randomLong();

		Mockito.when(
			dispatchTrigger.getDispatchTriggerId()
		).thenReturn(
			dispatchTriggerId
		);

		String resolvedValue = RandomTestUtil.randomString();

		Mockito.when(
			_secretResolver.resolve(
				companyId,
				StringBundler.concat(
					DispatchTrigger.class.getSimpleName(), StringPool.SLASH,
					companyId, StringPool.SLASH, dispatchTriggerId,
					StringPool.SLASH, name),
				value)
		).thenReturn(
			resolvedValue
		);

		TalendProcess talendProcess = ReflectionTestUtil.invoke(
			_talendDispatchTaskExecutor, "_getTalendProcess",
			new Class<?>[] {DispatchTrigger.class, TalendArchive.class},
			dispatchTrigger,
			new TalendArchive.Builder(
			).contextName(
				RandomTestUtil.randomString()
			).jobDirectory(
				RandomTestUtil.randomString()
			).build());

		Assert.assertTrue(
			ArrayUtil.contains(
				talendProcess.getMainMethodArguments(),
				StringBundler.concat(
					"--context_param ", name, StringPool.EQUAL,
					resolvedValue)));

		ProcessConfig processConfig = talendProcess.getProcessConfig();

		List<String> processConfigArguments = processConfig.getArguments();

		Assert.assertTrue(processConfigArguments.contains(javaOpts));
	}

	private static final MockedStatic<ServiceTrackerFactory>
		_serviceTrackerFactoryMockedStatic = Mockito.mockStatic(
			ServiceTrackerFactory.class);

	private final SecretResolver _secretResolver = Mockito.mock(
		SecretResolver.class);
	private final TalendDispatchTaskExecutor _talendDispatchTaskExecutor =
		new TalendDispatchTaskExecutor();

}