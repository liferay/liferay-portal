/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.configuration.test.util.GroupConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.wiki.constants.WikiConstants;
import com.liferay.wiki.constants.WikiWebKeys;
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
public class WikiDisplayViewMVCRenderCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testRender() throws Exception {
		WikiNode wikiNode = WikiTestUtil.addNode(_group.getGroupId());

		WikiPage wikiPage = WikiTestUtil.addPage(
			_group.getGroupId(), wikiNode.getNodeId(), true);

		try (GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						_group.getGroupId(), WikiConstants.SERVICE_NAME,
						MapUtil.<String, Object>singletonDictionary(
							"frontPageName", wikiPage.getTitle()))) {

			MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
				new MockLiferayPortletRenderRequest();

			mockLiferayPortletRenderRequest.addParameter(
				"nodeName", wikiNode.getName());

			ThemeDisplay themeDisplay = new ThemeDisplay();

			themeDisplay.setScopeGroupId(_group.getGroupId());
			themeDisplay.setSiteGroupId(_group.getGroupId());

			mockLiferayPortletRenderRequest.setAttribute(
				WebKeys.THEME_DISPLAY, themeDisplay);

			Assert.assertEquals(
				"/wiki_display/view.jsp",
				_mvcRenderCommand.render(
					mockLiferayPortletRenderRequest,
					new MockLiferayPortletRenderResponse()));

			WikiPage renderedWikiPage =
				(WikiPage)mockLiferayPortletRenderRequest.getAttribute(
					WikiWebKeys.WIKI_PAGE);

			Assert.assertEquals(
				wikiPage.getTitle(), renderedWikiPage.getTitle());
		}
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject(filter = "mvc.command.name=/wiki_display/view")
	private MVCRenderCommand _mvcRenderCommand;

}