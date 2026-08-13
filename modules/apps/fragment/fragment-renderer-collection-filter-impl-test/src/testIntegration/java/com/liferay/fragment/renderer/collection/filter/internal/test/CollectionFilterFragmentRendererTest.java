/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.renderer.collection.filter.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.LayoutServiceContextHelper;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Georgel Pop
 */
@RunWith(Arquillian.class)
public class CollectionFilterFragmentRendererTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = _groupLocalService.getGroup(TestPropsValues.getGroupId());
	}

	@Test
	@TestInfo("LPD-101829")
	public void testRenderWithXSS() throws Exception {
		String label = "<img src=x onerror=alert(123)>";

		for (String filterKey : new String[] {"category", "keywords", "tags"}) {
			String content = _render(
				JSONUtil.put(
					"filterKey", filterKey
				).put(
					"label", label
				).put(
					"showLabel", true
				));

			Assert.assertFalse(content, content.contains(label));
			Assert.assertTrue(
				content,
				content.contains("&lt;img src=x onerror=alert(123)&gt;"));
		}

		String helpText = "<img src=y onerror=alert(456)>";

		String content = _render(
			JSONUtil.put(
				"filterKey", "tags"
			).put(
				"helpText", helpText
			).put(
				"showHelpText", true
			));

		Assert.assertFalse(content, content.contains(helpText));
		Assert.assertTrue(
			content, content.contains("&lt;img src=y onerror=alert(456)&gt;"));
	}

	private String _render(JSONObject fragmentConfigJSONObject)
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid());

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				fragmentConfigJSONObject
			).toString(),
			_collectionFilterFragmentRenderer, draftLayout, null, 0,
			segmentsExperienceId);

		return ContentLayoutTestUtil.getRenderLayoutHTML(
			draftLayout, _layoutServiceContextHelper, _layoutStructureProvider,
			segmentsExperienceId);
	}

	@Inject(
		filter = "component.name=com.liferay.fragment.renderer.collection.filter.internal.CollectionFilterFragmentRenderer",
		type = FragmentRenderer.class
	)
	private FragmentRenderer _collectionFilterFragmentRenderer;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutServiceContextHelper _layoutServiceContextHelper;

	@Inject
	private LayoutStructureProvider _layoutStructureProvider;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}