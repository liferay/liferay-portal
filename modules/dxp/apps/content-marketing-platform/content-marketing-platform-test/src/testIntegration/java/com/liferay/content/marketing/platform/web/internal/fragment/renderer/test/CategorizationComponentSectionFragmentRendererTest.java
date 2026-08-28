/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.marketing.platform.web.internal.fragment.renderer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Yuri Monteiro
 */
@RunWith(Arquillian.class)
public class CategorizationComponentSectionFragmentRendererTest
	extends BaseComponentSectionFragmentRendererTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testGetProps() throws Exception {
		AssertUtils.assertEquals(
			HashMapBuilder.<String, Object>put(
				"cmsGroupId", themeDisplay.getScopeGroupId()
			).put(
				"funnelStagesVocabularyERC", "L_CMP_FUNNEL_STAGE"
			).put(
				"hasUpdatePermission", true
			).put(
				"objectEntryKeywords", new String[0]
			).put(
				"personasVocabularyERC", "L_CMP_PERSONAS"
			).put(
				"projectGroupId", GroupConstants.DEFAULT_PARENT_GROUP_ID
			).put(
				"selectedFunnelStageCategories",
				JSONFactoryUtil.createJSONArray()
			).put(
				"selectedPersonaCategories", JSONFactoryUtil.createJSONArray()
			).build(),
			getProps());
	}

	@FeatureFlag("LPD-99403")
	@Test
	public void testGetPropsWithProjectObjectEntry() throws Exception {
		Map<String, Object> props = getProps();

		Assert.assertEquals(
			cmpProjectObjectEntry.getGroupId(), props.get("projectGroupId"));

		testGetPropsWithCMSObjectEntry();
		testGetPropsWithTaskObjectEntry();
	}

	@Override
	protected FragmentRenderer getFragmentRenderer() {
		return _fragmentRenderer;
	}

	@Inject(
		filter = "component.name=com.liferay.content.marketing.platform.web.internal.fragment.renderer.CategorizationComponentSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

}