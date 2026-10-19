/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.marketing.platform.web.internal.upgrade.v2_1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.content.marketing.platform.test.util.CMPTestUtil;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Larissa Ribeiro
 */
@RunWith(Arquillian.class)
public class CMPDescriptionDisplayPageTemplateUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		ObjectDefinition cmpTaskObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMP_TASK", TestPropsValues.getCompanyId());
		Group group = CMPTestUtil.getOrAddGroup(
			CMPDescriptionDisplayPageTemplateUpgradeProcessTest.class);

		_layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				group.getGroupId(),
				PortalUtil.getClassNameId(
					cmpTaskObjectDefinition.getClassName()),
				null, false, WorkflowConstants.STATUS_APPROVED);
	}

	@Test
	public void testUpgrade() throws Exception {
		Layout layout = _layoutLocalService.getLayout(
			_layoutPageTemplateEntry.getPlid());

		_testUpgrade(layout);
		_testUpgrade(layout.fetchDraftLayout());
	}

	private FragmentEntryLink _addFragmentEntryLink(
			String inputFieldId, Layout layout, int position)
		throws Exception {

		return ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			_getEditableValues(inputFieldId), StringPool.BLANK,
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, StringPool.BLANK, layout, "INPUTS-textarea",
			FragmentConstants.TYPE_INPUT, null, position,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid()));
	}

	private String _getEditableValues(String inputFieldId) {
		JSONObject jsonObject = JSONUtil.put(
			FragmentEntryProcessorConstants.
				KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
			JSONUtil.put(
				"inputFieldId", inputFieldId
			).put(
				"inputReadOnly", false
			).put(
				"numberOfLines", 5
			).put(
				"placeholder", StringPool.BLANK
			).put(
				"showCharactersCount", false
			));

		return jsonObject.toString();
	}

	private void _testUpgrade(Layout layout) throws Exception {
		FragmentEntryLink fragmentEntryLink1 = _addFragmentEntryLink(
			"ObjectField_description", layout, 0);
		FragmentEntryLink fragmentEntryLink2 = _addFragmentEntryLink(
			"ObjectField_title", layout, 1);

		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.content.marketing.platform.web.internal.upgrade." +
				"v2_1_0.CMPDescriptionDisplayPageTemplateUpgradeProcess");

		upgradeProcess.upgrade();

		fragmentEntryLink1 =
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				fragmentEntryLink1.getFragmentEntryLinkId());

		Assert.assertEquals(
			"INPUTS-rich-text-input", fragmentEntryLink1.getRendererKey());
		Assert.assertEquals(
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					"inputFieldId", "ObjectField_description"
				).put(
					"inputReadOnly", false
				)
			).toString(),
			fragmentEntryLink1.getEditableValues());

		fragmentEntryLink2 =
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				fragmentEntryLink2.getFragmentEntryLinkId());

		Assert.assertEquals(
			"INPUTS-textarea", fragmentEntryLink2.getRendererKey());
		Assert.assertEquals(
			_getEditableValues("ObjectField_title"),
			fragmentEntryLink2.getEditableValues());
	}

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@DeleteAfterTestRun
	private LayoutPageTemplateEntry _layoutPageTemplateEntry;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject(
		filter = "component.name=com.liferay.content.marketing.platform.web.internal.upgrade.registry.CMPSiteInitializerUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}