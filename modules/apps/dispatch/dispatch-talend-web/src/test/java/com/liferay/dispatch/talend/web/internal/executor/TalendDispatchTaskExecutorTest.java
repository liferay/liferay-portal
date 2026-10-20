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
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.KeyReferenceUtil;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.portal.security.key.secret.SecretResolverUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PortalClassPathUtil;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
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

		_secretResolverSnapshot = ReflectionTestUtil.getAndSetFieldValue(
			SecretResolverUtil.class, "_secretResolverSnapshot",
			new Snapshot<SecretResolver>(
				SecretResolverUtil.class, SecretResolver.class) {

				@Override
				public SecretResolver get() {
					return _secretResolver;
				}

			});
	}

	@After
	public void tearDown() {
		ReflectionTestUtil.setFieldValue(
			SecretResolverUtil.class, "_secretResolverSnapshot",
			_secretResolverSnapshot);
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

		long dispatchTriggerId = RandomTestUtil.randomLong();

		Mockito.when(
			dispatchTrigger.getDispatchTriggerId()
		).thenReturn(
			dispatchTriggerId
		);

		String name = RandomTestUtil.randomString();

		String keyReferenceString = _getKeyReferenceString(
			companyId, dispatchTriggerId, name);

		String otherName = RandomTestUtil.randomString();

		String javaOptsKeyReferenceString = _getKeyReferenceString(
			companyId, dispatchTriggerId, "JAVA_OPTS");

		Mockito.when(
			dispatchTrigger.getDispatchTaskSettingsUnicodeProperties()
		).thenReturn(
			UnicodePropertiesBuilder.put(
				name, keyReferenceString
			).put(
				otherName, keyReferenceString
			).put(
				"JAVA_OPTS", javaOptsKeyReferenceString
			).build()
		);

		String value = RandomTestUtil.randomString();

		Mockito.when(
			_secretResolver.resolve(companyId, keyReferenceString)
		).thenReturn(
			value
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

		List<String> mainMethodArguments = Arrays.asList(
			talendProcess.getMainMethodArguments());

		Assert.assertTrue(
			mainMethodArguments.contains(
				StringBundler.concat(
					"--context_param ", name, StringPool.EQUAL, value)));
		Assert.assertTrue(
			mainMethodArguments.contains(
				StringBundler.concat(
					"--context_param ", otherName, StringPool.EQUAL,
					keyReferenceString)));

		ProcessConfig processConfig = talendProcess.getProcessConfig();

		List<String> processConfigArguments = processConfig.getArguments();

		Assert.assertTrue(
			processConfigArguments.contains(javaOptsKeyReferenceString));
	}

	private String _getKeyReferenceString(
		long companyId, long dispatchTriggerId, String name) {

		return KeyReferenceUtil.toKeyReferenceString(
			new KeyReference(
				StringBundler.concat(
					DispatchTrigger.class.getSimpleName(), StringPool.SLASH,
					companyId, StringPool.SLASH, dispatchTriggerId,
					StringPool.SLASH, name),
				RandomTestUtil.randomString(), KeyReference.Type.SECRET));
	}

	private static final MockedStatic<ServiceTrackerFactory>
		_serviceTrackerFactoryMockedStatic = Mockito.mockStatic(
			ServiceTrackerFactory.class);

	private final SecretResolver _secretResolver = Mockito.mock(
		SecretResolver.class);
	private Snapshot<SecretResolver> _secretResolverSnapshot;
	private final TalendDispatchTaskExecutor _talendDispatchTaskExecutor =
		new TalendDispatchTaskExecutor();

}