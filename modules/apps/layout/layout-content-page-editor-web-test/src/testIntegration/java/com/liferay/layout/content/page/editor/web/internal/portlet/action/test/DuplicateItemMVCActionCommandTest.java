/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.listener.FragmentEntryLinkListener;
import com.liferay.fragment.listener.FragmentEntryLinkListenerRegistry;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.structure.FragmentDropZoneLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class DuplicateItemMVCActionCommandTest {

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

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), TestPropsValues.getUserId());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			new MockLiferayPortletActionResponse());
		mockHttpServletRequest.setAttribute(WebKeys.LAYOUT, _layout);

		ThemeDisplay themeDisplay = ContentLayoutTestUtil.getThemeDisplay(
			_company, _group, _layout);

		themeDisplay.setRequest(mockHttpServletRequest);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		_serviceContext.setRequest(mockHttpServletRequest);

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testDuplicateDropZoneFragmentEntryLink() throws Exception {
		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_layout.getPlid());

		FragmentEntryLink dropzoneFragmentEntryLink = _addFragmentEntryLink(
			"{}",
			"<lfr-drop-zone " +
				"data-lfr-drop-zone-id=${fragmentEntryLinkNamespace}>" +
					"</lfr-drop-zone>",
			null, segmentsExperienceId);

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_layout.getGroupId(), _layout.getPlid());

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getData(segmentsExperienceId));

		FragmentStyledLayoutStructureItem
			dropZoneFragmentStyledLayoutStructureItem =
				_assertFragmentStyledLayoutStructureItem(
					layoutStructure.getLayoutStructureItemByFragmentEntryLinkId(
						dropzoneFragmentEntryLink.getFragmentEntryLinkId()));

		FragmentDropZoneLayoutStructureItem
			fragmentDropZoneLayoutStructureItem =
				_assertFragmentDropZoneLayoutStructureItem(
					layoutStructure, dropZoneFragmentStyledLayoutStructureItem);

		FragmentEntryLink headingFragmentEntryLink = _addFragmentEntryLink(
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					"element-text",
					JSONUtil.put(
						LocaleUtil.toLanguageId(
							_portal.getSiteDefaultLocale(_group)),
						RandomTestUtil.randomString()))
			).toString(),
			"<h1 data-lfr-editable-id=\"element-text\" " +
				"data-lfr-editable-type=\"text\">Heading Example</h1>",
			fragmentDropZoneLayoutStructureItem.getItemId(),
			segmentsExperienceId);

		layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getData(segmentsExperienceId));

		FragmentStyledLayoutStructureItem
			headingFragmentStyledLayoutStructureItem =
				_assertFragmentStyledLayoutStructureItem(
					layoutStructure.getLayoutStructureItemByFragmentEntryLinkId(
						headingFragmentEntryLink.getFragmentEntryLinkId()));

		Assert.assertEquals(
			fragmentDropZoneLayoutStructureItem.getItemId(),
			headingFragmentStyledLayoutStructureItem.getParentItemId());

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			_mvcActionCommand, "doTransactionalCommand",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			_getMockLiferayPortletActionRequest(
				new String[] {
					dropZoneFragmentStyledLayoutStructureItem.getItemId()
				},
				segmentsExperienceId),
			new MockLiferayPortletActionResponse());

		List<String> duplicatedItemIds = (List<String>)jsonObject.get(
			"duplicatedItemIds");

		String duplicatedItemId = duplicatedItemIds.get(0);

		Assert.assertNotNull(duplicatedItemId);

		layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getData(segmentsExperienceId));

		FragmentStyledLayoutStructureItem
			duplicatedDropZoneFragmentStyledLayoutStructureItem =
				_assertFragmentStyledLayoutStructureItem(
					layoutStructure.getLayoutStructureItem(duplicatedItemId));

		FragmentEntryLink duplicatedDropzoneFragmentEntryLink =
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				duplicatedDropZoneFragmentStyledLayoutStructureItem.
					getFragmentEntryLinkId());

		_assertDuplicatedFragmentEntryLink(
			duplicatedDropzoneFragmentEntryLink, dropzoneFragmentEntryLink);

		FragmentDropZoneLayoutStructureItem
			duplicatedFragmentDropZoneLayoutStructureItem =
				_assertFragmentDropZoneLayoutStructureItem(
					layoutStructure,
					duplicatedDropZoneFragmentStyledLayoutStructureItem);

		Assert.assertEquals(
			duplicatedDropzoneFragmentEntryLink.getNamespace(),
			duplicatedFragmentDropZoneLayoutStructureItem.
				getFragmentDropZoneId());

		FragmentStyledLayoutStructureItem
			duplicatedHeadingFragmentStyledLayoutStructureItem =
				_assertFragmentStyledLayoutStructureItem(
					_assertChildrenItems(
						layoutStructure,
						duplicatedFragmentDropZoneLayoutStructureItem));

		_assertDuplicatedFragmentEntryLink(
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				duplicatedHeadingFragmentStyledLayoutStructureItem.
					getFragmentEntryLinkId()),
			headingFragmentEntryLink);
	}

	@Test
	public void testDuplicateMultipleItems() throws Exception {
		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_layout.getGroupId(), _layout.getPlid());

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_layout.getPlid());

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getData(segmentsExperienceId));

		LayoutStructureItem rowStyledLayoutStructureItem1 =
			layoutStructure.addRowStyledLayoutStructureItem(
				layoutStructure.getMainItemId(), 0, 1);

		LayoutStructureItem rowStyledLayoutStructureItem2 =
			layoutStructure.addRowStyledLayoutStructureItem(
				layoutStructure.getMainItemId(), 0, 1);

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				_layout.getGroupId(), _layout.getPlid(),
				layoutStructure.toString());

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			_mvcActionCommand, "doTransactionalCommand",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			_getMockLiferayPortletActionRequest(
				new String[] {
					rowStyledLayoutStructureItem1.getItemId(),
					rowStyledLayoutStructureItem2.getItemId()
				},
				segmentsExperienceId),
			new MockLiferayPortletActionResponse());

		List<String> duplicatedItemIds = (List<String>)jsonObject.get(
			"duplicatedItemIds");

		Assert.assertEquals(
			duplicatedItemIds.toString(), 2, duplicatedItemIds.size());

		JSONObject layoutDataJSONObject = jsonObject.getJSONObject(
			"layoutData");

		layoutStructure = LayoutStructure.of(layoutDataJSONObject.toString());

		LayoutStructureItem mainLayoutStructureItem =
			layoutStructure.getLayoutStructureItem(
				layoutStructure.getMainItemId());

		List<String> childrenItemIds =
			mainLayoutStructureItem.getChildrenItemIds();

		Assert.assertEquals(
			childrenItemIds.toString(), 4, childrenItemIds.size());
	}

	private FragmentEntryLink _addFragmentEntryLink(
			String editableValues, String html, String parentItemId,
			long segmentsExperienceId)
		throws Exception {

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				StringUtil.randomString(), StringPool.BLANK, _serviceContext);

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				fragmentCollection.getFragmentCollectionId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				StringPool.BLANK, html, StringPool.BLANK, false,
				StringPool.BLANK, null, 0, false, false,
				FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED, _serviceContext);

		FragmentEntryLink fragmentEntryLink =
			ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
				editableValues, fragmentEntry.getCss(),
				fragmentEntry.getConfiguration(),
				fragmentEntry.getFragmentEntryId(), fragmentEntry.getHtml(),
				fragmentEntry.getJs(), _layout,
				fragmentEntry.getFragmentEntryKey(), fragmentEntry.getType(),
				parentItemId, 0, segmentsExperienceId);

		for (FragmentEntryLinkListener fragmentEntryLinkListener :
				_fragmentEntryLinkListenerRegistry.
					getFragmentEntryLinkListeners()) {

			fragmentEntryLinkListener.onAddFragmentEntryLink(fragmentEntryLink);
		}

		return fragmentEntryLink;
	}

	private LayoutStructureItem _assertChildrenItems(
		LayoutStructure layoutStructure,
		LayoutStructureItem layoutStructureItem) {

		List<String> childrenItemIds = layoutStructureItem.getChildrenItemIds();

		Assert.assertEquals(
			childrenItemIds.toString(), 1, childrenItemIds.size());

		String childrenItemId = childrenItemIds.get(0);

		LayoutStructureItem childLayoutStructureItem =
			layoutStructure.getLayoutStructureItem(childrenItemId);

		Assert.assertNotNull(childLayoutStructureItem);

		return childLayoutStructureItem;
	}

	private void _assertDuplicatedFragmentEntryLink(
		FragmentEntryLink duplicatedFragmentEntryLink,
		FragmentEntryLink fragmentEntryLink) {

		Assert.assertEquals(
			fragmentEntryLink.getFragmentEntryId(),
			duplicatedFragmentEntryLink.getFragmentEntryId());
		Assert.assertEquals(
			fragmentEntryLink.getHtml(), duplicatedFragmentEntryLink.getHtml());
		Assert.assertNotEquals(
			fragmentEntryLink.getNamespace(),
			duplicatedFragmentEntryLink.getNamespace());
		Assert.assertEquals(
			0, duplicatedFragmentEntryLink.getOriginalFragmentEntryLinkId());
	}

	private FragmentDropZoneLayoutStructureItem
			_assertFragmentDropZoneLayoutStructureItem(
				LayoutStructure layoutStructure,
				FragmentStyledLayoutStructureItem
					fragmentStyledLayoutStructureItem)
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				fragmentStyledLayoutStructureItem.getFragmentEntryLinkId());

		Assert.assertNotNull(fragmentEntryLink);

		LayoutStructureItem childLayoutStructureItem = _assertChildrenItems(
			layoutStructure, fragmentStyledLayoutStructureItem);

		Assert.assertTrue(
			childLayoutStructureItem instanceof
				FragmentDropZoneLayoutStructureItem);

		FragmentDropZoneLayoutStructureItem
			fragmentDropZoneLayoutStructureItem =
				(FragmentDropZoneLayoutStructureItem)childLayoutStructureItem;

		Assert.assertEquals(
			fragmentEntryLink.getNamespace(),
			fragmentDropZoneLayoutStructureItem.getFragmentDropZoneId());

		return fragmentDropZoneLayoutStructureItem;
	}

	private FragmentStyledLayoutStructureItem
		_assertFragmentStyledLayoutStructureItem(
			LayoutStructureItem layoutStructureItem) {

		Assert.assertNotNull(layoutStructureItem);
		Assert.assertTrue(
			layoutStructureItem instanceof FragmentStyledLayoutStructureItem);

		return (FragmentStyledLayoutStructureItem)layoutStructureItem;
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			String[] itemIds, long segmentExperienceId)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		mockLiferayPortletActionRequest.addParameter("itemIds", itemIds);
		mockLiferayPortletActionRequest.addParameter(
			"segmentsExperienceId", String.valueOf(segmentExperienceId));

		return mockLiferayPortletActionRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setLayout(_layout);
		themeDisplay.setLayoutSet(_layout.getLayoutSet());
		themeDisplay.setLocale(LocaleUtil.US);
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setPlid(_layout.getPlid());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Inject
	private FragmentEntryLinkListenerRegistry
		_fragmentEntryLinkListenerRegistry;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject(
		filter = "mvc.command.name=/layout_content_page_editor/duplicate_item"
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private Portal _portal;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext;

}