/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.internal.asset.validator.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.validator.AssetEntryValidatorExclusionRule;
import com.liferay.portal.configuration.test.util.GroupConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.wiki.constants.WikiConstants;
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
public class FrontPageAssetEntryValidatorExclusionRuleTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testIsValidationExcluded() throws Exception {
		WikiNode wikiNode = WikiTestUtil.addNode(_group.getGroupId());

		WikiPage wikiPage = WikiTestUtil.addPage(
			_group.getGroupId(), wikiNode.getNodeId(), true);

		Assert.assertFalse(_isValidationExcluded(wikiPage));

		try (GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						_group.getGroupId(), WikiConstants.SERVICE_NAME,
						MapUtil.<String, Object>singletonDictionary(
							"frontPageName", wikiPage.getTitle()))) {

			Assert.assertTrue(_isValidationExcluded(wikiPage));
		}
	}

	private boolean _isValidationExcluded(WikiPage wikiPage) {
		return _assetEntryValidatorExclusionRule.isValidationExcluded(
			wikiPage.getGroupId(), WikiPage.class.getName(),
			wikiPage.getPageId(), 0, new long[0], new String[0]);
	}

	@Inject(
		filter = "component.name=com.liferay.wiki.internal.asset.validator.FrontPageAssetEntryValidatorExclusionRule"
	)
	private AssetEntryValidatorExclusionRule _assetEntryValidatorExclusionRule;

	@DeleteAfterTestRun
	private Group _group;

}