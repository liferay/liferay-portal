/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.fragment.renderer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Map;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;

/**
 * @author Fábio Alves
 */
@FeatureFlags(featureFlags = @FeatureFlag("LPD-58677"))
@RunWith(Arquillian.class)
public class ProjectSelectorComponentSectionFragmentRendererTest
	extends BaseComponentSectionFragmentRendererTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		mockHttpServletRequest = getMockHttpServletRequest(
			cmpTaskObjectDefinition, cmpTaskObjectEntry);
	}

	@Test
	public void testGetProps() throws Exception {
		Map<String, Object> props = getProps();

		JSONArray jsonArray = (JSONArray)props.get("items");

		JSONAssert.assertEquals(
			JSONUtil.put(
				"label",
				MapUtil.getString(cmpProjectObjectEntry.getValues(), "title")
			).put(
				"value",
				String.valueOf(cmpProjectObjectEntry.getObjectEntryId())
			).toString(),
			String.valueOf(jsonArray.getJSONObject(0)), true);
	}

	@Override
	protected FragmentRenderer getFragmentRenderer() {
		return _fragmentRenderer;
	}

	@Inject(
		filter = "component.name=com.liferay.site.cmp.site.initializer.internal.fragment.renderer.ProjectSelectorComponentSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

}