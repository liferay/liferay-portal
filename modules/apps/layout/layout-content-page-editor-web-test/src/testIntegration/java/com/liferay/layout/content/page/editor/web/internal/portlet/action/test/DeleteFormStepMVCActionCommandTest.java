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
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.structure.FormStepContainerStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FormStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class DeleteFormStepMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		_draftLayout = _layout.fetchDraftLayout();
	}

	@Test
	public void testDeleteFirstFormStep() throws Exception {
		_addFormStyledLayoutStructureItem(2);

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			ContentLayoutTestUtil.getMockLiferayPortletActionRequest(
				_companyLocalService.getCompany(_group.getCompanyId()), _group,
				_draftLayout);

		mockLiferayPortletActionRequest.addParameter(
			"itemId", _getFormStepLayoutStructureItemId(0));

		MockLiferayPortletActionResponse mockLiferayPortletActionResponse =
			new MockLiferayPortletActionResponse();

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest, mockLiferayPortletActionResponse);

		MockHttpServletResponse mockHttpServletResponse =
			(MockHttpServletResponse)
				mockLiferayPortletActionResponse.getHttpServletResponse();

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			mockHttpServletResponse.getContentAsString());

		Assert.assertEquals(
			_language.get(
				LocaleUtil.getSiteDefault(), "an-unexpected-error-occurred"),
			jsonObject.getString("error"));
	}

	@Test
	public void testDeleteFormStepWithMoreThanTwoFormSteps() throws Exception {
		_addFormStyledLayoutStructureItem(3);

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			ContentLayoutTestUtil.getMockLiferayPortletActionRequest(
				_companyLocalService.getCompany(_group.getCompanyId()), _group,
				_draftLayout);

		mockLiferayPortletActionRequest.addParameter(
			"itemId", _getFormStepLayoutStructureItemId(1));

		FragmentEntryLink stepperFragmentEntryLink =
			_getStepperFragmentEntryLink();

		mockLiferayPortletActionRequest.addParameter(
			"stepperFragmentEntryLinkId",
			String.valueOf(stepperFragmentEntryLink.getFragmentEntryLinkId()));

		MockLiferayPortletActionResponse mockLiferayPortletActionResponse =
			new MockLiferayPortletActionResponse();

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest, mockLiferayPortletActionResponse);

		MockHttpServletResponse mockHttpServletResponse =
			(MockHttpServletResponse)
				mockLiferayPortletActionResponse.getHttpServletResponse();

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			mockHttpServletResponse.getContentAsString());

		JSONObject layoutDataJSONObject = jsonObject.getJSONObject(
			"layoutData");

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutDataJSONObject.toString());

		List<FormStyledLayoutStructureItem> formStyledLayoutStructureItems =
			layoutStructure.getFormStyledLayoutStructureItems();

		FormStyledLayoutStructureItem formStyledLayoutStructureItem =
			formStyledLayoutStructureItems.get(0);

		Assert.assertEquals(
			"multiple", formStyledLayoutStructureItem.getFormType());
		Assert.assertEquals(
			2, formStyledLayoutStructureItem.getNumberOfSteps());

		JSONArray removedItemIdsJSONArray = jsonObject.getJSONArray(
			"removedItemIds");

		Assert.assertEquals(1, removedItemIdsJSONArray.length());

		stepperFragmentEntryLink = _getStepperFragmentEntryLink();

		Assert.assertFalse(stepperFragmentEntryLink.isDeleted());

		JSONObject editableValuesJSONObject =
			stepperFragmentEntryLink.getEditableValuesJSONObject();

		JSONObject configurationValuesJSONObject =
			editableValuesJSONObject.getJSONObject(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR);

		Assert.assertEquals(
			2, configurationValuesJSONObject.getInt("numberOfSteps"));
	}

	@Test
	public void testDeleteFormStepWithNoExistingFormStyledLayoutStructureItem()
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			ContentLayoutTestUtil.getMockLiferayPortletActionRequest(
				_companyLocalService.getCompany(_group.getCompanyId()), _group,
				_layout.fetchDraftLayout());

		mockLiferayPortletActionRequest.addParameter(
			"itemId", RandomTestUtil.randomString());

		MockLiferayPortletActionResponse mockLiferayPortletActionResponse =
			new MockLiferayPortletActionResponse();

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest, mockLiferayPortletActionResponse);

		MockHttpServletResponse mockHttpServletResponse =
			(MockHttpServletResponse)
				mockLiferayPortletActionResponse.getHttpServletResponse();

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			mockHttpServletResponse.getContentAsString());

		Assert.assertEquals(
			_language.get(
				LocaleUtil.getSiteDefault(), "an-unexpected-error-occurred"),
			jsonObject.getString("error"));
	}

	@Test
	public void testDeleteFormStepWithOneFormStep() throws Exception {
		_addFormStyledLayoutStructureItem(1);

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			ContentLayoutTestUtil.getMockLiferayPortletActionRequest(
				_companyLocalService.getCompany(_group.getCompanyId()), _group,
				_draftLayout);

		mockLiferayPortletActionRequest.addParameter(
			"itemId", _getFormStepLayoutStructureItemId(0));

		MockLiferayPortletActionResponse mockLiferayPortletActionResponse =
			new MockLiferayPortletActionResponse();

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest, mockLiferayPortletActionResponse);

		MockHttpServletResponse mockHttpServletResponse =
			(MockHttpServletResponse)
				mockLiferayPortletActionResponse.getHttpServletResponse();

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			mockHttpServletResponse.getContentAsString());

		Assert.assertEquals(
			_language.get(
				LocaleUtil.getSiteDefault(), "an-unexpected-error-occurred"),
			jsonObject.getString("error"));
	}

	@Test
	public void testDeleteFormStepWithTwoFormSteps() throws Exception {
		_addFormStyledLayoutStructureItem(2);

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			ContentLayoutTestUtil.getMockLiferayPortletActionRequest(
				_companyLocalService.getCompany(_group.getCompanyId()), _group,
				_draftLayout);

		mockLiferayPortletActionRequest.addParameter(
			"itemId", _getFormStepLayoutStructureItemId(1));

		MockLiferayPortletActionResponse mockLiferayPortletActionResponse =
			new MockLiferayPortletActionResponse();

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest, mockLiferayPortletActionResponse);

		MockHttpServletResponse mockHttpServletResponse =
			(MockHttpServletResponse)
				mockLiferayPortletActionResponse.getHttpServletResponse();

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			mockHttpServletResponse.getContentAsString());

		JSONObject layoutDataJSONObject = jsonObject.getJSONObject(
			"layoutData");

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutDataJSONObject.toString());

		List<FormStyledLayoutStructureItem> formStyledLayoutStructureItems =
			layoutStructure.getFormStyledLayoutStructureItems();

		FormStyledLayoutStructureItem formStyledLayoutStructureItem =
			formStyledLayoutStructureItems.get(0);

		Assert.assertEquals(
			"simple", formStyledLayoutStructureItem.getFormType());
		Assert.assertEquals(
			0, formStyledLayoutStructureItem.getNumberOfSteps());

		JSONArray removedItemIdsJSONArray = jsonObject.getJSONArray(
			"removedItemIds");

		Assert.assertEquals(2, removedItemIdsJSONArray.length());

		FragmentEntryLink stepperFragmentEntryLink =
			_getStepperFragmentEntryLink();

		Assert.assertTrue(stepperFragmentEntryLink.isDeleted());
	}

	private void _addFormStyledLayoutStructureItem(int numberOfSteps)
		throws Exception {

		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		FormStyledLayoutStructureItem formStyledLayoutStructureItem =
			(FormStyledLayoutStructureItem)
				layoutStructure.addFormStyledLayoutStructureItem(
					rootLayoutStructureItem.getItemId(), 0);

		formStyledLayoutStructureItem.setFormType("multiple");
		formStyledLayoutStructureItem.setNumberOfSteps(numberOfSteps);

		Map<String, FragmentEntry> fragmentEntries =
			_fragmentCollectionContributorRegistry.getFragmentEntries(
				LocaleUtil.getDefault());

		FragmentEntry fragmentEntry = fragmentEntries.get("INPUTS-stepper");

		JSONObject editableValuesJSONObject = JSONUtil.put(
			FragmentEntryProcessorConstants.
				KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
			JSONUtil.put("numberOfSteps", numberOfSteps));

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(),
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_draftLayout.getPlid()),
				_draftLayout.getPlid(), fragmentEntry.getCss(),
				fragmentEntry.getHtml(), fragmentEntry.getJs(),
				fragmentEntry.getConfiguration(),
				editableValuesJSONObject.toString(), StringPool.BLANK, 0,
				"INPUTS-stepper", fragmentEntry.getType(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		layoutStructure.addFragmentStyledLayoutStructureItem(
			fragmentEntryLink.getFragmentEntryLinkId(),
			formStyledLayoutStructureItem.getItemId(), 0);

		LayoutStructureItem formStepContainerStyledLayoutStructureItem =
			layoutStructure.addFormStepContainerStyledLayoutStructureItem(
				formStyledLayoutStructureItem.getItemId(), 0);

		for (int i = 0; i < numberOfSteps; i++) {
			layoutStructure.addFormStepLayoutStructureItem(
				formStepContainerStyledLayoutStructureItem.getItemId(), 0);
		}

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				TestPropsValues.getUserId(), _group.getGroupId(),
				_draftLayout.getPlid(),
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_draftLayout.getPlid()),
				layoutStructure.toString());
	}

	private String _getFormStepLayoutStructureItemId(int index) {
		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_group.getGroupId(), _draftLayout.getPlid());

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());

		List<FormStyledLayoutStructureItem> formStyledLayoutStructureItems =
			layoutStructure.getFormStyledLayoutStructureItems();

		FormStyledLayoutStructureItem formStyledLayoutStructureItem =
			formStyledLayoutStructureItems.get(0);

		for (String childrenItemId :
				formStyledLayoutStructureItem.getChildrenItemIds()) {

			LayoutStructureItem layoutStructureItem =
				layoutStructure.getLayoutStructureItem(childrenItemId);

			if (layoutStructureItem instanceof
					FormStepContainerStyledLayoutStructureItem) {

				return layoutStructureItem.getChildrenItemId(index);
			}
		}

		return null;
	}

	private FragmentEntryLink _getStepperFragmentEntryLink() {
		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_group.getGroupId(), _draftLayout.getPlid());

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());

		Map<Long, LayoutStructureItem> fragmentLayoutStructureItems =
			layoutStructure.getFragmentLayoutStructureItems();

		Set<Long> keySet = fragmentLayoutStructureItems.keySet();

		Iterator<Long> iterator = keySet.iterator();

		return _fragmentEntryLinkLocalService.fetchFragmentEntryLink(
			iterator.next());
	}

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
	private Language _language;

	private Layout _layout;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject(
		filter = "mvc.command.name=/layout_content_page_editor/delete_form_step"
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}