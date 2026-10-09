/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.permission.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.PermissionService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Arrays;
import java.util.Objects;

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
 * @author Philip Jones
 */
@RunWith(Arquillian.class)
public class PermissionServiceImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testCheckPermission() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			PermissionServiceImplTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		boolean[] calledCheckBaseModel = {false};

		ServiceRegistration<ModelResourcePermission<?>> serviceRegistration =
			bundleContext.registerService(
				(Class<ModelResourcePermission<?>>)
					(Class<?>)ModelResourcePermission.class,
				(ModelResourcePermission<?>)ProxyUtil.newProxyInstance(
					ModelResourcePermission.class.getClassLoader(),
					new Class<?>[] {ModelResourcePermission.class},
					(proxy, method, args) -> {
						calledCheckBaseModel[0] = true;

						return null;
					}),
				HashMapDictionaryBuilder.<String, Object>put(
					"model.class.name", _CLASS_NAME
				).put(
					"service.ranking", Integer.MAX_VALUE
				).build());

		String portletName = RandomTestUtil.randomString();
		String rootModelName = RandomTestUtil.randomString();

		ResourceActionsUtil resourceActionsUtil = new ResourceActionsUtil();

		resourceActionsUtil.setResourceActions(
			(ResourceActions)ProxyUtil.newProxyInstance(
				ResourceActions.class.getClassLoader(),
				new Class<?>[] {ResourceActions.class},
				(proxy, method, args) -> {
					if (Objects.equals(method.getName(), "getPortletNames")) {
						return Arrays.asList(portletName);
					}

					if (Objects.equals(
							method.getName(), "isRootModelResource")) {

						return Objects.equals(args[0], rootModelName);
					}

					return method.invoke(_resourceActions, args);
				}));

		long[] groupId = {0};

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			(PermissionChecker)ProxyUtil.newProxyInstance(
				PermissionChecker.class.getClassLoader(),
				new Class<?>[] {PermissionChecker.class},
				(proxy, method, args) -> {
					if (Objects.equals(method.getName(), "hasPermission")) {
						groupId[0] = (Long)args[0];

						return true;
					}

					return method.invoke(permissionChecker, args);
				}));

		try {
			_permissionService.checkPermission(0, _CLASS_NAME, 0);

			Assert.assertTrue(calledCheckBaseModel[0]);

			calledCheckBaseModel[0] = false;

			_permissionService.checkPermission(0, _CLASS_NAME, null);

			Assert.assertTrue(calledCheckBaseModel[0]);

			long siteGroupId = RandomTestUtil.randomLong();

			_permissionService.checkPermission(
				RandomTestUtil.randomLong(), rootModelName,
				String.valueOf(siteGroupId));

			Assert.assertEquals(siteGroupId, groupId[0]);

			String modelName = RandomTestUtil.randomString();

			_permissionService.checkPermission(
				RandomTestUtil.randomLong(), modelName, modelName);

			Assert.assertEquals(0, groupId[0]);

			_permissionService.checkPermission(
				RandomTestUtil.randomLong(), portletName, portletName);

			Assert.assertEquals(0, groupId[0]);
		}
		finally {
			resourceActionsUtil.setResourceActions(_resourceActions);
			serviceRegistration.unregister();
		}
	}

	private static final String _CLASS_NAME = "PermissionServiceImplTest";

	@Inject
	private PermissionService _permissionService;

	@Inject
	private ResourceActions _resourceActions;

}