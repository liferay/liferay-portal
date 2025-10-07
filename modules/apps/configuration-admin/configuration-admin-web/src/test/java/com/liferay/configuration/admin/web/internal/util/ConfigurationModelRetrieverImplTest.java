/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.util;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Map;

import junit.framework.AssertionFailedError;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockSettings;
import org.mockito.Mockito;

import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Raymond Augé
 * @author Thiago Buarque
 */
public class ConfigurationModelRetrieverImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetConfiguration() throws Exception {
		MockSettings mockSettings = Mockito.withSettings();

		mockSettings = mockSettings.useConstructor();

		ConfigurationModelRetrieverImpl configurationModelRetrieverImpl =
			Mockito.mock(
				ConfigurationModelRetrieverImpl.class,
				mockSettings.defaultAnswer(Mockito.CALLS_REAL_METHODS));

		ConfigurationAdmin configurationAdmin = Mockito.mock(
			ConfigurationAdmin.class);

		Mockito.when(
			configurationAdmin.listConfigurations(Mockito.anyString())
		).thenReturn(
			new Configuration[0]
		);

		ReflectionTestUtil.setFieldValue(
			configurationModelRetrieverImpl, "_configurationAdmin",
			configurationAdmin);

		GroupLocalService groupLocalService = Mockito.mock(
			GroupLocalService.class);

		Group group = Mockito.mock(Group.class);

		long groupId = RandomTestUtil.randomLong();

		Mockito.when(
			group.getGroupId()
		).thenReturn(
			groupId
		);

		long companyId = RandomTestUtil.randomLong();

		Mockito.when(
			group.getCompanyId()
		).thenReturn(
			companyId
		);

		Mockito.when(
			groupLocalService.fetchGroup(Mockito.eq(groupId))
		).thenReturn(
			group
		);

		ReflectionTestUtil.setFieldValue(
			configurationModelRetrieverImpl, "_groupLocalService",
			groupLocalService);

		String pid = RandomTestUtil.randomString();

		configurationModelRetrieverImpl.getConfiguration(
			pid, ExtendedObjectClassDefinition.Scope.GROUP, groupId);

		Mockito.verify(
			configurationModelRetrieverImpl, Mockito.times(1)
		).getConfiguration(
			pid, ExtendedObjectClassDefinition.Scope.GROUP, groupId, true
		);

		Mockito.verify(
			configurationModelRetrieverImpl, Mockito.never()
		).getConfiguration(
			pid, ExtendedObjectClassDefinition.Scope.COMPANY, companyId, true
		);

		Mockito.verify(
			configurationModelRetrieverImpl, Mockito.never()
		).getConfiguration(
			pid, ExtendedObjectClassDefinition.Scope.SYSTEM, null, true
		);

		Mockito.clearInvocations(configurationModelRetrieverImpl);

		configurationModelRetrieverImpl.getConfiguration(
			pid, ExtendedObjectClassDefinition.Scope.GROUP, groupId, false);

		Mockito.verify(
			configurationModelRetrieverImpl, Mockito.times(1)
		).getConfiguration(
			pid, ExtendedObjectClassDefinition.Scope.GROUP, groupId, false
		);

		Mockito.verify(
			configurationModelRetrieverImpl, Mockito.times(1)
		).getConfiguration(
			pid, ExtendedObjectClassDefinition.Scope.COMPANY, companyId, false
		);

		Mockito.verify(
			configurationModelRetrieverImpl, Mockito.times(1)
		).getConfiguration(
			pid, ExtendedObjectClassDefinition.Scope.SYSTEM, null, false
		);
	}

	@Test
	public void testGetPidFilterStringScopeCompany() throws Exception {
		String key = ConfigurationAdmin.SERVICE_FACTORYPID;

		String pid = "foo";

		String pidFilterString =
			_configurationModelRetrieverImpl.getPidFilterString(
				pid, ExtendedObjectClassDefinition.Scope.COMPANY);

		_test(
			false, pidFilterString,
			HashMapBuilder.put(
				key, pid
			).build());
		_test(
			true, pidFilterString,
			HashMapBuilder.put(
				key, pid
			).put(
				"companyId", "any"
			).build());
		_test(
			false, pidFilterString,
			HashMapBuilder.put(
				key, pid
			).put(
				"groupId", "any"
			).build());
		_test(
			true, pidFilterString,
			HashMapBuilder.put(
				key, pid + ".scoped"
			).put(
				"companyId", "any"
			).build());
	}

	@Test
	public void testGetPidFilterStringScopeGroup() throws Exception {
		String key = ConfigurationAdmin.SERVICE_FACTORYPID;

		String pid = "foo";

		String pidFilterString =
			_configurationModelRetrieverImpl.getPidFilterString(
				pid, ExtendedObjectClassDefinition.Scope.GROUP);

		_test(
			false, pidFilterString,
			HashMapBuilder.put(
				key, pid
			).build());
		_test(
			false, pidFilterString,
			HashMapBuilder.put(
				key, pid
			).put(
				"companyId", "any"
			).build());
		_test(
			true, pidFilterString,
			HashMapBuilder.put(
				key, pid
			).put(
				"groupId", "any"
			).build());
		_test(
			false, pidFilterString,
			HashMapBuilder.put(
				key, pid
			).put(
				"portletInstanceId", "any"
			).build());
		_test(
			true, pidFilterString,
			HashMapBuilder.put(
				key, pid + ".scoped"
			).put(
				"groupId", "any"
			).build());
	}

	@Test
	public void testGetPidFilterStringScopePortletInstance() throws Exception {
		String key = ConfigurationAdmin.SERVICE_FACTORYPID;

		String pid = "foo";

		String pidFilterString =
			_configurationModelRetrieverImpl.getPidFilterString(
				pid, ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE);

		_test(
			false, pidFilterString,
			HashMapBuilder.put(
				key, pid
			).build());
		_test(
			false, pidFilterString,
			HashMapBuilder.put(
				key, pid
			).put(
				"companyId", "any"
			).build());
		_test(
			false, pidFilterString,
			HashMapBuilder.put(
				key, pid
			).put(
				"groupId", "any"
			).build());

		_test(
			false, pidFilterString,
			HashMapBuilder.put(
				key, pid
			).put(
				"portletInstanceId", "any"
			).build());
		_test(
			true, pidFilterString,
			HashMapBuilder.put(
				key, pid
			).put(
				"groupId", "any"
			).put(
				"portletInstanceId", "any"
			).build());
		_test(
			true, pidFilterString,
			HashMapBuilder.put(
				key, pid + ".scoped"
			).put(
				"groupId", "any"
			).put(
				"portletInstanceId", "any"
			).build());
	}

	@Test
	public void testGetPidFilterStringScopeSystem() throws Exception {
		String key = Constants.SERVICE_PID;

		String pid = "foo";

		String pidFilterString =
			_configurationModelRetrieverImpl.getPidFilterString(
				pid, ExtendedObjectClassDefinition.Scope.SYSTEM);

		_test(
			true, pidFilterString,
			HashMapBuilder.put(
				key, pid
			).build());
		_test(
			false, pidFilterString,
			HashMapBuilder.put(
				key, pid
			).put(
				"companyId", "any"
			).build());

		_test(
			false, pidFilterString,
			HashMapBuilder.put(
				key, pid
			).put(
				"groupId", "any"
			).build());

		String factoryInstancePID = "foo~1234";

		pidFilterString = _configurationModelRetrieverImpl.getPidFilterString(
			factoryInstancePID, ExtendedObjectClassDefinition.Scope.SYSTEM);

		_test(
			true, pidFilterString,
			HashMapBuilder.put(
				ConfigurationAdmin.SERVICE_FACTORYPID, pid
			).put(
				key, factoryInstancePID
			).build());
		_test(
			true, pidFilterString,
			HashMapBuilder.put(
				ConfigurationAdmin.SERVICE_FACTORYPID, pid
			).put(
				key, factoryInstancePID
			).put(
				"companyId", "0"
			).build());
		_test(
			false, pidFilterString,
			HashMapBuilder.put(
				ConfigurationAdmin.SERVICE_FACTORYPID, pid
			).put(
				key, factoryInstancePID
			).put(
				"companyId", "any"
			).build());
		_test(
			false, pidFilterString,
			HashMapBuilder.put(
				ConfigurationAdmin.SERVICE_FACTORYPID, pid
			).put(
				key, factoryInstancePID
			).put(
				"groupId", "any"
			).build());
		_test(
			false, pidFilterString,
			HashMapBuilder.put(
				key, pid + ".scoped"
			).build());
	}

	private void _test(
			boolean matches, String pidFilterString,
			Map<String, String> payload)
		throws Exception {

		Filter filter = FrameworkUtil.createFilter(pidFilterString);

		if (matches && !filter.matches(payload)) {
			throw new AssertionFailedError(
				filter + " does not match " + payload);
		}
		else if (!matches && filter.matches(payload)) {
			throw new AssertionFailedError(filter + " matches " + payload);
		}
	}

	private final ConfigurationModelRetrieverImpl
		_configurationModelRetrieverImpl =
			new ConfigurationModelRetrieverImpl();

}