/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.action.executor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.action.executor.ObjectActionExecutor;
import com.liferay.object.action.executor.ObjectActionExecutorRegistry;
import com.liferay.object.scope.CompanyScoped;
import com.liferay.object.scope.ObjectDefinitionScoped;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Guilherme Camacho
 */
@RunWith(Arquillian.class)
public class ObjectActionExecutorRegistryImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testActivateWithDifferentAllowedCompanyId() {
		ServiceRegistration<ObjectActionExecutor>
			objectActionExecutorServiceRegistration1 = _register(
				new TestObjectActionExecutor(
					1, Collections.emptyList(), false));

		ObjectActionExecutor objectActionExecutor =
			new TestObjectActionExecutor(2, Collections.emptyList(), false);

		ServiceRegistration<ObjectActionExecutor>
			objectActionExecutorServiceRegistration2 = _register(
				objectActionExecutor);

		List<ObjectActionExecutor> objectActionExecutors =
			_objectActionExecutorRegistry.getObjectActionExecutors(
				1, StringUtil.randomId());

		Assert.assertFalse(
			objectActionExecutors.contains(objectActionExecutor));

		_unregister(objectActionExecutorServiceRegistration1);
		_unregister(objectActionExecutorServiceRegistration2);
	}

	@Test
	public void testActivateWithException() {
		TestObjectActionExecutor testObjectActionExecutor1 =
			new TestObjectActionExecutor(1, Collections.emptyList(), true);

		ServiceRegistration<ObjectActionExecutor>
			objectActionExecutorServiceRegistration1 = _register(
				testObjectActionExecutor1);

		TestObjectActionExecutor testObjectActionExecutor2 =
			new TestObjectActionExecutor(1, Collections.emptyList(), false);

		ServiceRegistration<ObjectActionExecutor>
			objectActionExecutorServiceRegistration2 = _register(
				testObjectActionExecutor2);

		List<ObjectActionExecutor> objectActionExecutors =
			_objectActionExecutorRegistry.getObjectActionExecutors(
				1, StringUtil.randomId());

		Assert.assertFalse(
			objectActionExecutors.contains(testObjectActionExecutor1));
		Assert.assertTrue(
			objectActionExecutors.contains(testObjectActionExecutor2));

		_unregister(objectActionExecutorServiceRegistration1);
		_unregister(objectActionExecutorServiceRegistration2);
	}

	private ServiceRegistration<ObjectActionExecutor> _register(
		ObjectActionExecutor objectActionExecutor) {

		Bundle bundle = FrameworkUtil.getBundle(
			ObjectActionExecutorRegistryImplTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		return bundleContext.registerService(
			ObjectActionExecutor.class, objectActionExecutor, null);
	}

	private void _unregister(
		ServiceRegistration<ObjectActionExecutor>
			objectActionExecutorServiceRegistration) {

		if (objectActionExecutorServiceRegistration == null) {
			return;
		}

		objectActionExecutorServiceRegistration.unregister();
	}

	@Inject
	private ObjectActionExecutorRegistry _objectActionExecutorRegistry;

	private static class TestObjectActionExecutor
		implements CompanyScoped, ObjectActionExecutor, ObjectDefinitionScoped {

		public TestObjectActionExecutor(
			long allowedCompanyId, List<String> allowedObjectDefinitionNames,
			boolean fail) {

			_allowedCompanyId = allowedCompanyId;
			_allowedObjectDefinitionNames = allowedObjectDefinitionNames;
			_fail = fail;
		}

		@Override
		public void execute(
				long companyId, long objectActionId,
				UnicodeProperties parametersUnicodeProperties,
				JSONObject payloadJSONObject, long userId)
			throws Exception {
		}

		@Override
		public long getAllowedCompanyId() {
			return _allowedCompanyId;
		}

		@Override
		public List<String> getAllowedObjectDefinitionNames() {
			return _allowedObjectDefinitionNames;
		}

		@Override
		public String getKey() {
			if (_fail) {
				throw new RuntimeException();
			}

			return "test";
		}

		private final long _allowedCompanyId;
		private final List<String> _allowedObjectDefinitionNames;
		private final boolean _fail;

	}

}