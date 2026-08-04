/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.license.util.App;
import com.liferay.portal.kernel.license.util.LicenseManager;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionRegistryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cmp.site.initializer.test.util.CMPTestUtil;
import com.liferay.site.cms.site.initializer.util.CMPLicenseUtil;

import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Fábio Alves
 */
@RunWith(Arquillian.class)
public class CMPLicenseUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = CMPTestUtil.getOrAddGroup(CMPLicenseUtilTest.class);
	}

	@After
	public void tearDown() throws Exception {
		_checkResources();
	}

	@Test
	public void testCheckResources() throws Exception {
		_testCheckResources(false);
		_testCheckResources(true);
	}

	private void _assertLayout(boolean appEnabled, String friendlyURL)
		throws Exception {

		Layout layout = _layoutLocalService.getFriendlyURLLayout(
			_group.getGroupId(), false, friendlyURL);

		Assert.assertEquals(!appEnabled, layout.isHidden());
	}

	private void _assertObjectDefinition(
			boolean appEnabled, String externalReferenceCode)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					externalReferenceCode, _group.getCompanyId());

		Assert.assertEquals(appEnabled, objectDefinition.isActive());

		ModelResourcePermission<ObjectEntry> modelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				objectDefinition.getClassName());

		if (appEnabled) {
			Assert.assertNotNull(modelResourcePermission);
		}
		else {
			Assert.assertNull(modelResourcePermission);
		}
	}

	private void _checkResources() {
		CMPLicenseUtil.checkResources(
			_group.getCompanyId(), _groupLocalService, _layoutLocalService,
			_objectDefinitionLocalService);
	}

	private void _testCheckResources(boolean appEnabled) throws Exception {

		// Read before the stub replaces it

		LicenseManager licenseManager = LicenseManagerUtil.getLicenseManager();

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					LicenseManagerUtil.class, "_licenseManager",
					ProxyUtil.newProxyInstance(
						LicenseManager.class.getClassLoader(),
						new Class<?>[] {LicenseManager.class},
						(proxy, method, arguments) -> {
							if (Objects.equals(
									method.getName(), "isAppEnabled") &&
								Objects.equals(arguments[0], App.CMP)) {

								return appEnabled;
							}

							return method.invoke(licenseManager, arguments);
						}))) {

			_checkResources();

			_assertLayout(appEnabled, "/planning");
			_assertLayout(appEnabled, "/projects");
			_assertLayout(appEnabled, "/tasks");
			_assertObjectDefinition(appEnabled, "L_CMP_PROJECT");
			_assertObjectDefinition(appEnabled, "L_CMP_PROJECT_LINK");
			_assertObjectDefinition(appEnabled, "L_CMP_TASK");
			_assertObjectDefinition(appEnabled, "L_CMP_TASK_LINK");
		}
	}

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}