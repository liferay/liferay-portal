/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariation;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelElementVariationLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Víctor Galán
 */
@RunWith(Arquillian.class)
public class
	UpdateLayoutPageTemplateStructureRelElementVariationMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testProcessAction() throws Exception {
		Group group = _groupLocalService.getGroup(TestPropsValues.getGroupId());

		Layout layout = LayoutTestUtil.addTypeContentLayout(group);

		String externalReferenceCode = RandomTestUtil.randomString();

		_layoutPageTemplateStructureRelElementVariationLocalService.
			addOrUpdateLayoutPageTemplateStructureRelElementVariation(
				externalReferenceCode, TestPropsValues.getUserId(),
				group.getGroupId(), false, StringPool.FALSE,
				Collections.emptyMap(), Collections.emptyMap(),
				RandomTestUtil.randomString(), layout.getPlid(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				new String[] {RandomTestUtil.randomString()},
				ServiceContextTestUtil.getServiceContext(
					group, TestPropsValues.getUserId()));

		_mvcActionCommand.processAction(
			_getMockLiferayPortletActionRequest(
				true, externalReferenceCode, group, layout),
			new MockLiferayPortletActionResponse());

		LayoutPageTemplateStructureRelElementVariation
			layoutPageTemplateStructureRelElementVariation =
				_getLayoutPageTemplateStructureRelElementVariation(layout);

		Assert.assertTrue(
			layoutPageTemplateStructureRelElementVariation.isActive());

		_mvcActionCommand.processAction(
			_getMockLiferayPortletActionRequest(
				false, externalReferenceCode, group, layout),
			new MockLiferayPortletActionResponse());

		layoutPageTemplateStructureRelElementVariation =
			_getLayoutPageTemplateStructureRelElementVariation(layout);

		Assert.assertFalse(
			layoutPageTemplateStructureRelElementVariation.isActive());
	}

	private LayoutPageTemplateStructureRelElementVariation
		_getLayoutPageTemplateStructureRelElementVariation(Layout layout) {

		List<LayoutPageTemplateStructureRelElementVariation>
			layoutPageTemplateStructureRelElementVariations =
				_layoutPageTemplateStructureRelElementVariationLocalService.
					getLayoutPageTemplateStructureRelElementVariations(
						layout.getPlid());

		return layoutPageTemplateStructureRelElementVariations.get(0);
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
		boolean active, String externalReferenceCode, Group group,
		Layout layout) {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"active", String.valueOf(active));
		mockLiferayPortletActionRequest.addParameter(
			"externalReferenceCode", externalReferenceCode);
		mockLiferayPortletActionRequest.addParameter(
			"plid", String.valueOf(layout.getPlid()));

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setScopeGroupId(group.getGroupId());

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockLiferayPortletActionRequest;
	}

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutPageTemplateStructureRelElementVariationLocalService
		_layoutPageTemplateStructureRelElementVariationLocalService;

	@Inject(
		filter = "mvc.command.name=/layout_content_page_editor/update_layout_page_template_structure_rel_element_variation"
	)
	private MVCActionCommand _mvcActionCommand;

}