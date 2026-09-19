/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.uad.exporter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.user.associated.data.exporter.UADExporter;
import com.liferay.user.associated.data.test.util.BaseUADExporterTestCase;
import com.liferay.user.associated.data.test.util.WhenHasStatusByUserIdField;
import com.liferay.wiki.model.WikiNode;
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
public class WikiNodeUADExporterTest
	extends BaseUADExporterTestCase<WikiNode>
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
		WikiNode wikiNode = _wikiNodeUADTestHelper.addWikiNode(userId);

		_wikiNodes.add(wikiNode);

		return wikiNode;
	}

	@Override
	protected UADExporter<WikiNode> getUADExporter() {
		return _uadExporter;
	}

	@Inject(
		filter = "component.name=com.liferay.wiki.uad.exporter.WikiNodeUADExporter"
	)
	private UADExporter<WikiNode> _uadExporter;

	@Inject
	private WikiNodeUADTestHelper _wikiNodeUADTestHelper;

	@DeleteAfterTestRun
	private final List<WikiNode> _wikiNodes = new ArrayList<>();

}