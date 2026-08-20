/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.frontend.js.audiences.ElementVariations;
import com.liferay.frontend.js.audiences.ElementVariationsProvider;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelElementVariationLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.test.util.SegmentsTestUtil;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Georgel Pop
 */
@FeatureFlag("LPD-85746")
@RunWith(Arquillian.class)
public class LayoutModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	@TestInfo("LPD-103017")
	public void testOnAfterUpdate() throws Exception {
		Group group = GroupTestUtil.addGroup();

		Layout layout = LayoutTestUtil.addTypeContentLayout(group);

		SegmentsExperience segmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(
				group.getGroupId(), layout.getPlid());

		String targetElement1 = RandomTestUtil.randomString();

		_addLayoutPageTemplateStructureRelElementVariation(
			group, layout, segmentsExperience, targetElement1);

		String content = _getElementVariationsContent(
			layout, segmentsExperience);

		Assert.assertTrue(content, content.contains(targetElement1));

		String targetElement2 = RandomTestUtil.randomString();

		_addLayoutPageTemplateStructureRelElementVariation(
			group, layout, segmentsExperience, targetElement2);

		content = _getElementVariationsContent(layout, segmentsExperience);

		Assert.assertTrue(content, content.contains(targetElement1));
		Assert.assertFalse(content, content.contains(targetElement2));

		_layoutLocalService.updateName(
			layout.getPlid(), RandomTestUtil.randomString(),
			LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()));

		content = _getElementVariationsContent(layout, segmentsExperience);

		Assert.assertTrue(content, content.contains(targetElement1));
		Assert.assertTrue(content, content.contains(targetElement2));
	}

	private void _addLayoutPageTemplateStructureRelElementVariation(
			Group group, Layout layout, SegmentsExperience segmentsExperience,
			String targetElement)
		throws Exception {

		_layoutPageTemplateStructureRelElementVariationLocalService.
			addOrUpdateLayoutPageTemplateStructureRelElementVariation(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				group.getGroupId(), true, RandomTestUtil.randomString(),
				Collections.emptyMap(), Collections.emptyMap(),
				RandomTestUtil.randomString(), layout.getPlid(),
				segmentsExperience.getExternalReferenceCode(), targetElement,
				new String[] {RandomTestUtil.randomString()},
				ServiceContextTestUtil.getServiceContext(group.getGroupId()));
	}

	private String _getElementVariationsContent(
		Layout layout, SegmentsExperience segmentsExperience) {

		ElementVariations elementVariations =
			_elementVariationsProvider.getElementVariations(
				layout.getPlid(), segmentsExperience.getSegmentsExperienceId());

		return elementVariations.getContent();
	}

	@Inject
	private ElementVariationsProvider _elementVariationsProvider;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateStructureRelElementVariationLocalService
		_layoutPageTemplateStructureRelElementVariationLocalService;

}