/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.cluster.internal;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.configuration.persistence.ReloadablePersistenceManager;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.Dictionary;
import java.util.EnumSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.SynchronousConfigurationListener;

/**
 * @author Raymond Augé
 */
public class ConfigurationSynchronousConfigurationListenerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);

		_configurationSynchronousConfigurationListener =
			new ConfigurationSynchronousConfigurationListener();

		ReflectionTestUtil.setFieldValue(
			_configurationSynchronousConfigurationListener,
			"_configurationAdmin", _configurationAdmin);
		ReflectionTestUtil.setFieldValue(
			_configurationSynchronousConfigurationListener,
			"_reloadablePersistenceManager", _reloadablePersistenceManager);

		Snapshot<SynchronousConfigurationListener> snapshot = Mockito.mock(
			Snapshot.class);

		Mockito.when(
			snapshot.get()
		).thenReturn(
			_configurationSynchronousConfigurationListener
		);

		ReflectionTestUtil.setFieldValue(
			_configurationSynchronousConfigurationListener, "_snapshot",
			snapshot);
	}

	@Test
	public void testOnNotify() throws Exception {
		Mockito.when(
			_configurationAdmin.listConfigurations(Mockito.anyString())
		).thenAnswer(
			new Answer<Configuration[]>() {

				@Override
				public Configuration[] answer(InvocationOnMock invocationOnMock)
					throws Throwable {

					Assert.assertTrue(
						_COMPANY_ID == CompanyThreadLocal.getCompanyId());

					return null;
				}

			}
		);

		ReflectionTestUtil.invoke(
			_configurationSynchronousConfigurationListener, "_onNotify",
			new Class<?>[] {long.class, String.class, int.class}, _COMPANY_ID,
			RandomTestUtil.randomString(), ConfigurationEvent.CM_UPDATED);
	}

	@Test
	public void testReadOnlyConfigurationDelete() throws Exception {
		_invokeConfigurationListener(
			EnumSet.of(Configuration.ConfigurationAttribute.READ_ONLY),
			ConfigurationEvent.CM_DELETED, new HashMapDictionary<>(),
			configuration -> {
				Mockito.verify(
					configuration, Mockito.times(1)
				).delete();

				Mockito.verify(
					configuration, Mockito.times(0)
				).addAttributes(
					Configuration.ConfigurationAttribute.READ_ONLY
				);
			});
	}

	@Test
	public void testReadOnlyConfigurationUpdate() throws Exception {
		_invokeConfigurationListener(
			EnumSet.of(Configuration.ConfigurationAttribute.READ_ONLY),
			ConfigurationEvent.CM_UPDATED,
			HashMapDictionaryBuilder.<String, Object>put(
				"foo", "bar"
			).build(),
			configuration -> {
				Mockito.verify(
					configuration, Mockito.times(1)
				).update(
					Mockito.any()
				);

				Mockito.verify(
					configuration, Mockito.times(1)
				).addAttributes(
					Configuration.ConfigurationAttribute.READ_ONLY
				);
			});
	}

	@Test
	public void testReadOnlyConfigurationUpdateEmpty() throws Exception {
		_invokeConfigurationListener(
			EnumSet.of(Configuration.ConfigurationAttribute.READ_ONLY),
			ConfigurationEvent.CM_UPDATED, null,
			configuration -> {
				Mockito.verify(
					configuration, Mockito.times(1)
				).update();

				Mockito.verify(
					configuration, Mockito.times(1)
				).addAttributes(
					Configuration.ConfigurationAttribute.READ_ONLY
				);
			});
	}

	@Test
	public void testReadWriteConfigurationDelete() throws Exception {
		_invokeConfigurationListener(
			Collections.emptySet(), ConfigurationEvent.CM_DELETED, null,
			configuration -> {
				Mockito.verify(
					configuration, Mockito.times(1)
				).delete();

				Mockito.verify(
					configuration, Mockito.times(0)
				).addAttributes(
					Mockito.any()
				);
			});
	}

	@Test
	public void testReadWriteConfigurationUpdate() throws Exception {
		_invokeConfigurationListener(
			Collections.emptySet(), ConfigurationEvent.CM_UPDATED,
			HashMapDictionaryBuilder.<String, Object>put(
				"foo", "bar"
			).build(),
			configuration -> {
				Mockito.verify(
					configuration, Mockito.times(1)
				).update(
					Mockito.any()
				);

				Mockito.verify(
					configuration, Mockito.times(0)
				).addAttributes(
					Mockito.any()
				);
			});
	}

	@Test
	public void testReadWriteConfigurationUpdateEmpty() throws Exception {
		_invokeConfigurationListener(
			Collections.emptySet(), ConfigurationEvent.CM_UPDATED, null,
			configuration -> {
				Mockito.verify(
					configuration, Mockito.times(1)
				).update();

				Mockito.verify(
					configuration, Mockito.times(0)
				).addAttributes(
					Mockito.any()
				);
			});
	}

	private void _invokeConfigurationListener(
			Set<Configuration.ConfigurationAttribute> attributes,
			int configuratonEventType, Dictionary<String, Object> properties,
			UnsafeConsumer<Configuration, Exception> unsafeConsumer)
		throws Exception {

		Mockito.doReturn(
			properties
		).when(
			_reloadablePersistenceManager
		).reload(
			Mockito.anyString()
		);

		Configuration configuration = Mockito.mock(Configuration.class);

		Mockito.when(
			configuration.getAttributes()
		).thenReturn(
			attributes
		);

		Mockito.when(
			_configurationAdmin.listConfigurations(Mockito.anyString())
		).thenReturn(
			new Configuration[] {configuration}
		);

		ReflectionTestUtil.invoke(
			_configurationSynchronousConfigurationListener,
			"_reloadConfiguration", new Class<?>[] {String.class, int.class},
			RandomTestUtil.randomString(), configuratonEventType);

		unsafeConsumer.accept(configuration);
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	@Mock
	private ConfigurationAdmin _configurationAdmin;

	private ConfigurationSynchronousConfigurationListener
		_configurationSynchronousConfigurationListener;

	@Mock
	private ReloadablePersistenceManager _reloadablePersistenceManager;

}