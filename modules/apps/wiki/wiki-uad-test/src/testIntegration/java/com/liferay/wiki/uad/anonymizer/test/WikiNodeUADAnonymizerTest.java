/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.uad.anonymizer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.user.associated.data.anonymizer.UADAnonymizer;
import com.liferay.user.associated.data.test.util.BaseUADAnonymizerTestCase;
import com.liferay.user.associated.data.test.util.WhenHasStatusByUserIdField;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.service.WikiNodeLocalService;
import com.liferay.wiki.uad.test.WikiNodeUADTestHelper;

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
public class WikiNodeUADAnonymizerTest
	extends BaseUADAnonymizerTestCase<WikiNode>
	implements WhenHasStatusByUserIdField<WikiNode> {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Override
	public WikiNode addBaseModelWithStatusByUserId(
			long userId, long statusByUserId)
		throws Exception {

		WikiNode wikiNode =
			_wikiNodeUADTestHelper.addWikiNodeWithStatusByUserId(
				userId, statusByUserId);

		_wikiNodes.add(wikiNode);

		return wikiNode;
	}

	@After
	public void tearDown() throws Exception {
		_wikiNodeUADTestHelper.cleanUpDependencies(_wikiNodes);
	}

	@Override
	protected WikiNode addBaseModel(long userId) throws Exception {
		return addBaseModel(userId, true);
	}

	@Override
	protected WikiNode addBaseModel(long userId, boolean deleteAfterTestRun)
		throws Exception {

		WikiNode wikiNode = _wikiNodeUADTestHelper.addWikiNode(userId);

		if (deleteAfterTestRun) {
			_wikiNodes.add(wikiNode);
		}

		return wikiNode;
	}

	@Override
	protected void deleteBaseModels(List<WikiNode> baseModels)
		throws Exception {

		_wikiNodeUADTestHelper.cleanUpDependencies(baseModels);
	}

	@Override
	protected UADAnonymizer<WikiNode> getUADAnonymizer() {
		return _uadAnonymizer;
	}

	@Override
	protected boolean isBaseModelAutoAnonymized(long baseModelPK, User user)
		throws Exception {

		WikiNode wikiNode = _wikiNodeLocalService.getWikiNode(baseModelPK);

		String userName = wikiNode.getUserName();
		String statusByUserName = wikiNode.getStatusByUserName();

		if ((wikiNode.getUserId() != user.getUserId()) &&
			!userName.equals(user.getFullName()) &&
			(wikiNode.getStatusByUserId() != user.getUserId()) &&
			!statusByUserName.equals(user.getFullName())) {

			return true;
		}

		return false;
	}

	@Override
	protected boolean isBaseModelDeleted(long baseModelPK) {
		if (_wikiNodeLocalService.fetchWikiNode(baseModelPK) == null) {
			return true;
		}

		return false;
	}

	@Inject(
		filter = "component.name=com.liferay.wiki.uad.anonymizer.WikiNodeUADAnonymizer"
	)
	private UADAnonymizer<WikiNode> _uadAnonymizer;

	@Inject
	private WikiNodeLocalService _wikiNodeLocalService;

	@Inject
	private WikiNodeUADTestHelper _wikiNodeUADTestHelper;

	@DeleteAfterTestRun
	private final List<WikiNode> _wikiNodes = new ArrayList<>();

}