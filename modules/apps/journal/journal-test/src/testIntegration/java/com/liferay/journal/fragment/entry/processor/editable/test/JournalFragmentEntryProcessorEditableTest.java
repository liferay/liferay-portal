/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.fragment.entry.processor.editable.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.model.DDMTemplateLink;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLinkLocalService;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
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
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class JournalFragmentEntryProcessorEditableTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = _groupLocalService.fetchGroup(TestPropsValues.getGroupId());

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		_ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), ddmStructure.getStructureId(),
			_portal.getClassNameId(JournalArticle.class));
	}

	@Test
	public void testFragmentEntryProcessorEditableMappedDDMTemplate()
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), null,
				null, null,
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_layout.getPlid()),
				_layout.getPlid(), StringPool.BLANK, StringPool.BLANK,
				StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
				StringPool.BLANK, 0, StringPool.BLANK,
				FragmentConstants.TYPE_COMPONENT,
				ServiceContextTestUtil.getServiceContext());

		Class<?> clazz = getClass();

		String fileContent = StringUtil.read(
			clazz.getClassLoader(),
			"com/liferay/journal/dependencies" +
				"/fragment_entry_link_mapped_ddm.json");

		JSONObject editableValuesJSONObject = JSONFactoryUtil.createJSONObject(
			fileContent);

		_fragmentEntryLinkLocalService.updateFragmentEntryLink(
			TestPropsValues.getUserId(),
			fragmentEntryLink.getFragmentEntryLinkId(),
			StringUtil.replace(
				editableValuesJSONObject.toString(), "TEMPLATE_KEY",
				_ddmTemplate.getTemplateKey()),
			true);

		String compositeClassName = ResourceActionsUtil.getCompositeModelName(
			FragmentEntryLink.class.getName(), "editable_text");

		DDMTemplateLink ddmTemplateLink =
			_ddmTemplateLinkLocalService.getTemplateLink(
				PortalUtil.getClassNameId(compositeClassName),
				fragmentEntryLink.getFragmentEntryLinkId());

		Assert.assertNotNull(ddmTemplateLink);

		_fragmentEntryLinkLocalService.deleteFragmentEntryLink(
			fragmentEntryLink);

		Assert.assertNull(
			_ddmTemplateLinkLocalService.fetchDDMTemplateLink(
				ddmTemplateLink.getTemplateLinkId()));
	}

	private DDMTemplate _ddmTemplate;

	@Inject
	private DDMTemplateLinkLocalService _ddmTemplateLinkLocalService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	private Layout _layout;

	@Inject
	private Portal _portal;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}