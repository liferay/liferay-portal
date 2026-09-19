/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.teams.uad.exporter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.site.teams.uad.test.TeamUADTestHelper;
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
public class TeamUADExporterTest extends BaseUADExporterTestCase<Team> {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@After
	public void tearDown() throws Exception {
		_teamUADTestHelper.cleanUpDependencies(_teams);
	}

	@Override
	protected Team addBaseModel(long userId) throws Exception {
		Team team = _teamUADTestHelper.addTeam(userId);

		_teams.add(team);

		return team;
	}

	@Override
	protected UADExporter<Team> getUADExporter() {
		return _uadExporter;
	}

	@Inject
	private TeamUADTestHelper _teamUADTestHelper;

	@DeleteAfterTestRun
	private final List<Team> _teams = new ArrayList<>();

	@Inject(
		filter = "component.name=com.liferay.site.teams.uad.exporter.TeamUADExporter"
	)
	private UADExporter<Team> _uadExporter;

}