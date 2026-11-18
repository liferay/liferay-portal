/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.io.ByteArrayOutputStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Moral
 */
@RunWith(Arquillian.class)
public class GetFragmentEntryLinksMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		_draftLayout = layout.fetchDraftLayout();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), TestPropsValues.getUserId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testGetFragmentEntryLinks() throws Exception {
		LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
			_addMasterLayoutPageTemplateEntry();

		_draftLayout =
			_layoutLocalService.updateMasterLayoutPageTemplateEntryERC(
				_draftLayout.getGroupId(), _draftLayout.isPrivateLayout(),
				_draftLayout.getLayoutId(),
				masterLayoutPageTemplateEntry.getExternalReferenceCode());

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink(
			_draftLayout);

		FragmentEntryLink masterFragmentEntryLink = _addFragmentEntryLink(
			_layoutLocalService.getLayout(
				masterLayoutPageTemplateEntry.getPlid()));

		JSONArray jsonArray = _serveResource(
			fragmentEntryLink.getFragmentEntryLinkId(),
			masterFragmentEntryLink.getFragmentEntryLinkId());

		Assert.assertEquals(2, jsonArray.length());

		JSONObject jsonObject = jsonArray.getJSONObject(0);

		Assert.assertTrue(jsonObject.has("content"));
		Assert.assertEquals(
			fragmentEntryLink.getFragmentEntryLinkId(),
			jsonObject.getLong("fragmentEntryLinkId"));

		jsonObject = jsonArray.getJSONObject(1);

		Assert.assertTrue(jsonObject.has("content"));
		Assert.assertEquals(
			masterFragmentEntryLink.getFragmentEntryLinkId(),
			jsonObject.getLong("fragmentEntryLinkId"));
	}

	@Test
	public void testGetFragmentEntryLinksWithOtherLayoutFragmentEntryLinks()
		throws Exception {

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink(
			_draftLayout);
		FragmentEntryLink noLayoutFragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), null,
				null, null, 0, 0, StringPool.BLANK, StringPool.BLANK,
				StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
				StringPool.BLANK, 0, StringPool.BLANK,
				FragmentConstants.TYPE_COMPONENT, _serviceContext);

		_otherGroup = GroupTestUtil.addGroup();

		Layout otherGroupLayout = LayoutTestUtil.addTypeContentLayout(
			_otherGroup);

		FragmentEntryLink otherGroupFragmentEntryLink = _addFragmentEntryLink(
			otherGroupLayout.fetchDraftLayout());

		Layout otherLayout = LayoutTestUtil.addTypeContentLayout(_group);

		FragmentEntryLink otherLayoutFragmentEntryLink = _addFragmentEntryLink(
			otherLayout.fetchDraftLayout());

		LayoutPageTemplateEntry otherMasterLayoutPageTemplateEntry =
			_addMasterLayoutPageTemplateEntry();

		FragmentEntryLink otherMasterFragmentEntryLink = _addFragmentEntryLink(
			_layoutLocalService.getLayout(
				otherMasterLayoutPageTemplateEntry.getPlid()));

		JSONArray jsonArray = _serveResource(
			fragmentEntryLink.getFragmentEntryLinkId(),
			noLayoutFragmentEntryLink.getFragmentEntryLinkId(),
			otherGroupFragmentEntryLink.getFragmentEntryLinkId(),
			otherLayoutFragmentEntryLink.getFragmentEntryLinkId(),
			otherMasterFragmentEntryLink.getFragmentEntryLinkId(),
			RandomTestUtil.nextLong());

		Assert.assertEquals(6, jsonArray.length());

		JSONObject jsonObject = jsonArray.getJSONObject(0);

		Assert.assertTrue(jsonObject.has("content"));
		Assert.assertEquals(
			fragmentEntryLink.getFragmentEntryLinkId(),
			jsonObject.getLong("fragmentEntryLinkId"));

		for (int i = 1; i < jsonArray.length(); i++) {
			jsonObject = jsonArray.getJSONObject(i);

			Assert.assertEquals(0, jsonObject.length());
		}
	}

	@Test
	public void testGetFragmentEntryLinksWithOtherMasterLayoutFragmentEntryLink()
		throws Exception {

		LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
			_addMasterLayoutPageTemplateEntry();

		_draftLayout =
			_layoutLocalService.updateMasterLayoutPageTemplateEntryERC(
				_draftLayout.getGroupId(), _draftLayout.isPrivateLayout(),
				_draftLayout.getLayoutId(),
				masterLayoutPageTemplateEntry.getExternalReferenceCode());

		LayoutPageTemplateEntry otherMasterLayoutPageTemplateEntry =
			_addMasterLayoutPageTemplateEntry();

		FragmentEntryLink otherMasterFragmentEntryLink = _addFragmentEntryLink(
			_layoutLocalService.getLayout(
				otherMasterLayoutPageTemplateEntry.getPlid()));

		JSONArray jsonArray = _serveResource(
			otherMasterFragmentEntryLink.getFragmentEntryLinkId());

		Assert.assertEquals(1, jsonArray.length());

		JSONObject jsonObject = jsonArray.getJSONObject(0);

		Assert.assertEquals(0, jsonObject.length());
	}

	private FragmentEntryLink _addFragmentEntryLink(Layout layout)
		throws Exception {

		return ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			StringPool.BLANK, layout,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid()));
	}

	private LayoutPageTemplateEntry _addMasterLayoutPageTemplateEntry()
		throws Exception {

		return _layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(), 0, null,
			RandomTestUtil.randomString(),
			LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0,
			WorkflowConstants.STATUS_APPROVED, _serviceContext);
	}

	private MockLiferayResourceRequest _getMockLiferayResourceRequest(
			long... fragmentEntryLinkIds)
		throws Exception {

		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY,
			ContentLayoutTestUtil.getThemeDisplay(
				_companyLocalService.fetchCompany(_group.getCompanyId()),
				_group, _draftLayout));

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		for (long fragmentEntryLinkId : fragmentEntryLinkIds) {
			jsonArray.put(
				JSONUtil.put("fragmentEntryLinkId", fragmentEntryLinkId));
		}

		mockLiferayResourceRequest.setParameter("data", jsonArray.toString());
		mockLiferayResourceRequest.setParameter(
			"segmentsExperienceId",
			String.valueOf(
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_draftLayout.getPlid())));

		return mockLiferayResourceRequest;
	}

	private JSONArray _serveResource(long... fragmentEntryLinkIds)
		throws Exception {

		MockLiferayResourceResponse mockLiferayResourceResponse =
			new MockLiferayResourceResponse();

		_mvcResourceCommand.serveResource(
			_getMockLiferayResourceRequest(fragmentEntryLinkIds),
			mockLiferayResourceResponse);

		ByteArrayOutputStream byteArrayOutputStream =
			(ByteArrayOutputStream)
				mockLiferayResourceResponse.getPortletOutputStream();

		return JSONFactoryUtil.createJSONArray(
			byteArrayOutputStream.toString());
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	private Layout _draftLayout;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject(
		filter = "mvc.command.name=/layout_content_page_editor/get_fragment_entry_links"
	)
	private MVCResourceCommand _mvcResourceCommand;

	@DeleteAfterTestRun
	private Group _otherGroup;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext;

}