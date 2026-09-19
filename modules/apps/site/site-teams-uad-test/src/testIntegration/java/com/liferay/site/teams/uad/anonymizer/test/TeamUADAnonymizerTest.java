/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.teams.uad.anonymizer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.TeamLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.site.teams.uad.test.TeamUADTestHelper;
import com.liferay.user.associated.data.anonymizer.UADAnonymizer;
import com.liferay.user.associated.data.test.util.BaseUADAnonymizerTestCase;

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
public class TeamUADAnonymizerTest extends BaseUADAnonymizerTestCase<Team> {

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
		return addBaseModel(userId, true);
	}

	@Override
	protected Team addBaseModel(long userId, boolean deleteAfterTestRun)
		throws Exception {

		Team team = _teamUADTestHelper.addTeam(userId);

		if (deleteAfterTestRun) {
			_teams.add(team);
		}

		return team;
	}

	@Override
	protected void deleteBaseModels(List<Team> baseModels) throws Exception {
		_teamUADTestHelper.cleanUpDependencies(baseModels);
	}

	@Override
	protected UADAnonymizer<Team> getUADAnonymizer() {
		return _uadAnonymizer;
	}

	@Override
	protected boolean isBaseModelAutoAnonymized(long baseModelPK, User user)
		throws Exception {

		Team team = _teamLocalService.getTeam(baseModelPK);

		String userName = team.getUserName();

		if ((team.getUserId() != user.getUserId()) &&
			!userName.equals(user.getFullName())) {

			return true;
		}

		return false;
	}

	@Override
	protected boolean isBaseModelDeleted(long baseModelPK) {
		if (_teamLocalService.fetchTeam(baseModelPK) == null) {
			return true;
		}

		return false;
	}

	@Inject
	private TeamLocalService _teamLocalService;

	@Inject
	private TeamUADTestHelper _teamUADTestHelper;

	@DeleteAfterTestRun
	private final List<Team> _teams = new ArrayList<>();

	@Inject(
		filter = "component.name=com.liferay.site.teams.uad.anonymizer.TeamUADAnonymizer"
	)
	private UADAnonymizer<Team> _uadAnonymizer;

}