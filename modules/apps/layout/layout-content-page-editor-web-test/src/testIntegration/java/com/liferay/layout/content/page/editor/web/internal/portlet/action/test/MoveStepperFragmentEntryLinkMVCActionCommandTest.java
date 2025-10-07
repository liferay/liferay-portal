/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.structure.FormStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class MoveStepperFragmentEntryLinkMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_company = _companyLocalService.getCompany(_group.getCompanyId());

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		_draftLayout = layout.fetchDraftLayout();

		_segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_draftLayout.getPlid());
	}

	@Test
	@TestInfo("LPD-31772")
	public void testMoveStepperFragmentEntryLink() throws Exception {
		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		FormStyledLayoutStructureItem firstFormStyledLayoutStructureItem =
			(FormStyledLayoutStructureItem)
				layoutStructure.addFormStyledLayoutStructureItem(
					rootLayoutStructureItem.getItemId(), 0);

		firstFormStyledLayoutStructureItem.setFormType("multiple");
		firstFormStyledLayoutStructureItem.setNumberOfSteps(2);

		FragmentEntryLink fragmentEntryLink = _addStepperFragmentEntryLink();

		LayoutStructureItem fragmentStyledLayoutStructureItem =
			layoutStructure.addFragmentStyledLayoutStructureItem(
				fragmentEntryLink.getFragmentEntryLinkId(),
				firstFormStyledLayoutStructureItem.getItemId(), 0);

		LayoutStructureItem formStepContainerStyledLayoutStructureItem =
			layoutStructure.addFormStepContainerStyledLayoutStructureItem(
				firstFormStyledLayoutStructureItem.getItemId(), 0);

		for (int i = 0; i < 2; i++) {
			layoutStructure.addFormStepLayoutStructureItem(
				formStepContainerStyledLayoutStructureItem.getItemId(), 0);
		}

		FormStyledLayoutStructureItem secondFormStyledLayoutStructureItem =
			(FormStyledLayoutStructureItem)
				layoutStructure.addFormStyledLayoutStructureItem(
					rootLayoutStructureItem.getItemId(), 1);

		secondFormStyledLayoutStructureItem.setFormType("simple");

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				TestPropsValues.getUserId(), _group.getGroupId(),
				_draftLayout.getPlid(), _segmentsExperienceId,
				layoutStructure.toString());

		MockLiferayPortletActionResponse mockLiferayPortletActionResponse =
			new MockLiferayPortletActionResponse();

		_mvcActionCommand.processAction(
			_getMockLiferayPortletActionRequest(
				fragmentEntryLink.getFragmentEntryLinkId(),
				fragmentStyledLayoutStructureItem.getItemId(),
				secondFormStyledLayoutStructureItem.getItemId()),
			mockLiferayPortletActionResponse);

		MockHttpServletResponse mockHttpServletResponse =
			(MockHttpServletResponse)
				mockLiferayPortletActionResponse.getHttpServletResponse();

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			mockHttpServletResponse.getContentAsString());

		layoutStructure = LayoutStructure.of(
			jsonObject.getString("layoutData"));

		secondFormStyledLayoutStructureItem =
			(FormStyledLayoutStructureItem)
				layoutStructure.getLayoutStructureItem(
					secondFormStyledLayoutStructureItem.getItemId());

		Assert.assertEquals(
			"multistep", secondFormStyledLayoutStructureItem.getFormType());
		Assert.assertEquals(
			2, secondFormStyledLayoutStructureItem.getNumberOfSteps());

		JSONArray addedItemIdsJSONArray = jsonObject.getJSONArray(
			"addedItemIds");

		Assert.assertEquals(5, addedItemIdsJSONArray.length());

		for (int i = 0; i < addedItemIdsJSONArray.length(); i++) {
			Assert.assertNotEquals(
				fragmentStyledLayoutStructureItem.getItemId(),
				addedItemIdsJSONArray.get(i));
		}

		JSONArray movedItemIdsJSONArray = jsonObject.getJSONArray(
			"movedItemIds");

		Assert.assertEquals(1, movedItemIdsJSONArray.length());

		JSONArray removedItemIdsJSONArray = jsonObject.getJSONArray(
			"removedItemIds");

		Assert.assertEquals(0, removedItemIdsJSONArray.length());

		JSONObject fragmentEntryLinksJSONObject = _jsonFactory.createJSONObject(
			jsonObject.getString("fragmentEntryLinks"));

		Assert.assertEquals(3, fragmentEntryLinksJSONObject.length());
	}

	private FragmentEntryLink _addStepperFragmentEntryLink() throws Exception {
		Map<String, FragmentEntry> fragmentEntries =
			_fragmentCollectionContributorRegistry.getFragmentEntries(
				LocaleUtil.getDefault());

		FragmentEntry fragmentEntry = fragmentEntries.get("INPUTS-stepper");

		JSONObject editableValuesJSONObject = JSONUtil.put(
			FragmentEntryProcessorConstants.
				KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
			JSONUtil.put("numberOfSteps", 2));

		return _fragmentEntryLinkLocalService.addFragmentEntryLink(
			null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
			fragmentEntry.getFragmentEntryId(), _segmentsExperienceId,
			_draftLayout.getPlid(), fragmentEntry.getCss(),
			fragmentEntry.getHtml(), fragmentEntry.getJs(),
			fragmentEntry.getConfiguration(),
			editableValuesJSONObject.toString(), StringPool.BLANK, 0,
			"INPUTS-stepper", fragmentEntry.getType(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			long fragmentEntryLinkId, String itemId, String parentItemId)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			ContentLayoutTestUtil.getMockLiferayPortletActionRequest(
				_company, _group, _draftLayout);

		mockLiferayPortletActionRequest.setParameter(
			"fragmentEntryLinkId", String.valueOf(fragmentEntryLinkId));
		mockLiferayPortletActionRequest.setParameter("itemId", itemId);
		mockLiferayPortletActionRequest.setParameter("numberOfSteps", "2");
		mockLiferayPortletActionRequest.setParameter(
			"parentItemId", parentItemId);
		mockLiferayPortletActionRequest.setParameter("position", "0");
		mockLiferayPortletActionRequest.setParameter(
			"segmentsExperienceId", String.valueOf(_segmentsExperienceId));

		return mockLiferayPortletActionRequest;
	}

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	private Layout _draftLayout;

	@Inject
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject(
		filter = "mvc.command.name=/layout_content_page_editor/move_stepper_fragment_entry_link"
	)
	private MVCActionCommand _mvcActionCommand;

	private long _segmentsExperienceId;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}