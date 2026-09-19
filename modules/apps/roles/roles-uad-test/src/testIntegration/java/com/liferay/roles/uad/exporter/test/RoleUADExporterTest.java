/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.uad.exporter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.roles.uad.test.RoleUADTestHelper;
import com.liferay.user.associated.data.exporter.UADExporter;
import com.liferay.user.associated.data.test.util.BaseUADExporterTestCase;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Brian Wing Shun Chan
 */
@RunWith(Arquillian.class)
public class RoleUADExporterTest extends BaseUADExporterTestCase<Role> {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@After
	public void tearDown() throws Exception {
		_roleUADTestHelper.cleanUpDependencies(_roles);
	}

	@Override
	protected Role addBaseModel(long userId) throws Exception {
		Role role = _roleUADTestHelper.addRole(userId);

		_roles.add(role);

		return role;
	}

	@Override
	protected UADExporter<Role> getUADExporter() {
		return _uadExporter;
	}

	@Inject
	private RoleUADTestHelper _roleUADTestHelper;

	@DeleteAfterTestRun
	private final List<Role> _roles = new ArrayList<>();

	@Inject(
		filter = "component.name=com.liferay.roles.uad.exporter.RoleUADExporter"
	)
	private UADExporter<Role> _uadExporter;

}