/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.engine.creole.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.configuration.test.util.GroupConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.wiki.constants.WikiConstants;
import com.liferay.wiki.engine.BaseWikiEngine;
import com.liferay.wiki.engine.WikiEngine;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.test.util.WikiTestUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class CreoleWikiEngineTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testConvert() throws Exception {
		WikiNode wikiNode = WikiTestUtil.addNode(_group.getGroupId());

		WikiPage wikiPage = WikiTestUtil.addPage(
			TestPropsValues.getUserId(), wikiNode.getNodeId(),
			RandomTestUtil.randomString(),
			"[[" + RandomTestUtil.randomString() + "]]", true,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertFalse(_hasNewWikiPageClass(wikiPage));

		try (GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						_group.getGroupId(), WikiConstants.SERVICE_NAME,
						MapUtil.<String, Object>singletonDictionary(
							"enableHighlightCreoleFormat", true))) {

			Assert.assertTrue(_hasNewWikiPageClass(wikiPage));
		}
	}

	@Test
	public void testGetEditorName() throws Exception {
		String editorName = RandomTestUtil.randomString();

		try (GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						_group.getGroupId(), WikiConstants.SERVICE_NAME,
						MapUtil.<String, Object>singletonDictionary(
							"getCreoleEditor", editorName))) {

			BaseWikiEngine baseWikiEngine = (BaseWikiEngine)_wikiEngine;

			Assert.assertEquals(
				editorName, baseWikiEngine.getEditorName(_group.getGroupId()));
		}
	}

	private boolean _hasNewWikiPageClass(WikiPage wikiPage) throws Exception {
		String content = _wikiEngine.convert(wikiPage, null, null, null);

		return content.contains("class=\"new-wiki-page\"");
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "component.name=com.liferay.wiki.engine.creole.internal.CreoleWikiEngine"
	)
	private WikiEngine _wikiEngine;

}