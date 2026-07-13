/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariation;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariationAudienceEntryRel;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelElementVariationLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Arrays;
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
	SaveLayoutPageTemplateStructureRelElementVariationMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testProcessAction() throws Exception {
		String audienceEntryERC1 = RandomTestUtil.randomString();
		String audienceEntryERC2 = RandomTestUtil.randomString();
		String externalReferenceCode = RandomTestUtil.randomString();
		String name = RandomTestUtil.randomString();
		String targetElement = RandomTestUtil.randomString();

		Group group = _groupLocalService.getGroup(TestPropsValues.getGroupId());

		Layout layout = LayoutTestUtil.addTypeContentLayout(group);

		_mvcActionCommand.processAction(
			_getMockLiferayPortletActionRequest(
				JSONUtil.put(
					"audienceEntryERCs",
					JSONUtil.putAll(audienceEntryERC1, audienceEntryERC2)
				).put(
					"externalReferenceCode", externalReferenceCode
				).put(
					"hide", "true"
				).put(
					"htmlMap",
					JSONUtil.put("en_US", RandomTestUtil.randomString())
				).put(
					"jsMap",
					JSONUtil.put("en_US", RandomTestUtil.randomString())
				).put(
					"name", name
				).put(
					"segmentsExperienceERC", RandomTestUtil.randomString()
				).put(
					"targetElement", targetElement
				),
				group, layout),
			new MockLiferayPortletActionResponse());

		List<LayoutPageTemplateStructureRelElementVariation>
			layoutPageTemplateStructureRelElementVariations =
				_layoutPageTemplateStructureRelElementVariationLocalService.
					getLayoutPageTemplateStructureRelElementVariations(
						layout.getPlid());

		Assert.assertEquals(
			layoutPageTemplateStructureRelElementVariations.toString(), 1,
			layoutPageTemplateStructureRelElementVariations.size());

		LayoutPageTemplateStructureRelElementVariation
			layoutPageTemplateStructureRelElementVariation =
				layoutPageTemplateStructureRelElementVariations.get(0);

		_assertAudienceEntryERCs(
			externalReferenceCode,
			Arrays.asList(audienceEntryERC1, audienceEntryERC2));

		Assert.assertTrue(
			layoutPageTemplateStructureRelElementVariation.isActive());
		Assert.assertEquals(
			externalReferenceCode,
			layoutPageTemplateStructureRelElementVariation.
				getExternalReferenceCode());
		Assert.assertEquals(
			"true", layoutPageTemplateStructureRelElementVariation.getHide());
		Assert.assertEquals(
			name, layoutPageTemplateStructureRelElementVariation.getName());
		Assert.assertEquals(
			targetElement,
			layoutPageTemplateStructureRelElementVariation.getTargetElement());

		String updatedAudienceEntryERC = RandomTestUtil.randomString();
		String updatedName = RandomTestUtil.randomString();
		String updatedTargetElement = RandomTestUtil.randomString();

		_mvcActionCommand.processAction(
			_getMockLiferayPortletActionRequest(
				JSONUtil.put(
					"active", false
				).put(
					"audienceEntryERCs",
					JSONUtil.putAll(updatedAudienceEntryERC)
				).put(
					"externalReferenceCode", externalReferenceCode
				).put(
					"hide", "false"
				).put(
					"htmlMap",
					JSONUtil.put("en_US", RandomTestUtil.randomString())
				).put(
					"jsMap",
					JSONUtil.put("en_US", RandomTestUtil.randomString())
				).put(
					"name", updatedName
				).put(
					"segmentsExperienceERC", RandomTestUtil.randomString()
				).put(
					"targetElement", updatedTargetElement
				),
				group, layout),
			new MockLiferayPortletActionResponse());

		layoutPageTemplateStructureRelElementVariations =
			_layoutPageTemplateStructureRelElementVariationLocalService.
				getLayoutPageTemplateStructureRelElementVariations(
					layout.getPlid());

		Assert.assertEquals(
			layoutPageTemplateStructureRelElementVariations.toString(), 1,
			layoutPageTemplateStructureRelElementVariations.size());

		layoutPageTemplateStructureRelElementVariation =
			layoutPageTemplateStructureRelElementVariations.get(0);

		_assertAudienceEntryERCs(
			externalReferenceCode,
			Collections.singletonList(updatedAudienceEntryERC));

		Assert.assertFalse(
			layoutPageTemplateStructureRelElementVariation.isActive());
		Assert.assertEquals(
			updatedName,
			layoutPageTemplateStructureRelElementVariation.getName());
		Assert.assertEquals(
			updatedTargetElement,
			layoutPageTemplateStructureRelElementVariation.getTargetElement());
	}

	private void _assertAudienceEntryERCs(
		String externalReferenceCode, List<String> audienceEntryERCs) {

		Collections.sort(audienceEntryERCs);

		List<String> actualAudienceEntryERCs = TransformUtil.transform(
			_layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService.
				getLayoutPageTemplateStructureRelElementVariationAudienceEntryRels(
					externalReferenceCode),
			LayoutPageTemplateStructureRelElementVariationAudienceEntryRel::
				getAudienceEntryERC);

		Collections.sort(actualAudienceEntryERCs);

		Assert.assertEquals(audienceEntryERCs, actualAudienceEntryERCs);
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			JSONObject elementVariationJSONObject, Group group, Layout layout)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"elementVariation", elementVariationJSONObject.toString());
		mockLiferayPortletActionRequest.addParameter(
			"plid", String.valueOf(layout.getPlid()));

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(group.getCompanyId()));
		themeDisplay.setLayout(layout);
		themeDisplay.setLayoutSet(layout.getLayoutSet());
		themeDisplay.setLocale(LocaleUtil.US);
		themeDisplay.setScopeGroupId(group.getGroupId());
		themeDisplay.setServerName("localhost");
		themeDisplay.setSiteGroupId(group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockLiferayPortletActionRequest;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService
			_layoutPageTemplateStructureRelElementVariationAudienceEntryRelLocalService;

	@Inject
	private LayoutPageTemplateStructureRelElementVariationLocalService
		_layoutPageTemplateStructureRelElementVariationLocalService;

	@Inject(
		filter = "mvc.command.name=/layout_content_page_editor/save_layout_page_template_structure_rel_element_variation"
	)
	private MVCActionCommand _mvcActionCommand;

}