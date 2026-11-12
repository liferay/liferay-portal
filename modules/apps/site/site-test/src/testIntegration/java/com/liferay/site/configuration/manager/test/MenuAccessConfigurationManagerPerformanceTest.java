/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.configuration.manager.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.test.performance.PerformanceTimer;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.configuration.manager.MenuAccessConfigurationManager;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Evan Thibodeau
 */
@RunWith(Arquillian.class)
public class MenuAccessConfigurationManagerPerformanceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		for (int i = 0; i < _NUMBER_GROUPS; i++) {
			_groups.add(GroupTestUtil.addGroup());
		}
	}

	@Test
	public void testDeleteRoleAccessToControlMenu() throws Exception {
		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		try (PerformanceTimer performanceTimer = new PerformanceTimer(200)) {
			_menuAccessConfigurationManager.deleteRoleAccessToControlMenu(role);
		}
	}

	@Test
	public void testDeleteRoleAccessToControlMenuWithMenuAccessEnabled()
		throws Exception {

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		for (int i = 0; i < (_NUMBER_GROUPS / 5); i++) {
			Group group = _groups.get(i);

			_menuAccessConfigurationManager.updateMenuAccessConfiguration(
				group.getGroupId(),
				new String[] {role.getExternalReferenceCode()}, true);
		}

		try (PerformanceTimer performanceTimer = new PerformanceTimer(400)) {
			_menuAccessConfigurationManager.deleteRoleAccessToControlMenu(role);
		}
	}

	private static final int _NUMBER_GROUPS = 100;

	@DeleteAfterTestRun
	private final List<Group> _groups = new ArrayList<>();

	@Inject
	private MenuAccessConfigurationManager _menuAccessConfigurationManager;

}