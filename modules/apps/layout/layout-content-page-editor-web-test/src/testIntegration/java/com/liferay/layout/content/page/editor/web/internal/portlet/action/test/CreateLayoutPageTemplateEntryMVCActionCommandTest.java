/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalServiceUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class CreateLayoutPageTemplateEntryMVCActionCommandTest {

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

		_layoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					null, TestPropsValues.getUserId(), _group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, RandomTestUtil.randomString(), StringPool.BLANK,
					LayoutPageTemplateCollectionTypeConstants.BASIC,
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId()));
	}

	@Test
	@TestInfo("LPS-140483")
	public void testAutoRenameLayoutPageTemplateEntryCreatedInTheSameLayoutPageTemplateCollection()
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest();

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			_mvcActionCommand, "doTransactionalCommand",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				jsonObject.getLong("layoutPageTemplateEntryId"));

		Assert.assertEquals(
			_layout.getName(LocaleUtil.getSiteDefault()) + " - page-template",
			layoutPageTemplateEntry.getName());

		jsonObject = ReflectionTestUtil.invoke(
			_mvcActionCommand, "doTransactionalCommand",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				jsonObject.getLong("layoutPageTemplateEntryId"));

		Assert.assertEquals(
			_layout.getName(LocaleUtil.getSiteDefault()) + " - page-template 2",
			layoutPageTemplateEntry.getName());
	}

	@Test
	@TestInfo("LPS-140483")
	public void testCreateLayoutPageTemplateEntry() throws Exception {
		Layout draftLayout = _layout.fetchDraftLayout();

		LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, null,
				RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		draftLayout = _layoutLocalService.updateMasterLayoutPlid(
			draftLayout.getGroupId(), draftLayout.isPrivateLayout(),
			draftLayout.getLayoutId(), masterLayoutPageTemplateEntry.getPlid());

		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), false,
				StringPool.BLANK, RandomTestUtil.randomString(),
				StringPool.BLANK, RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		draftLayout = _layoutLocalService.updateStyleBookEntryId(
			draftLayout.getGroupId(), draftLayout.isPrivateLayout(),
			draftLayout.getLayoutId(), styleBookEntry.getStyleBookEntryId());

		ContentLayoutTestUtil.addPortletToLayout(
			draftLayout, JournalContentPortletKeys.JOURNAL_CONTENT);

		ContentLayoutTestUtil.publishLayout(draftLayout, _layout);

		_layout = _layoutLocalService.fetchLayout(_layout.getPlid());

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			_mvcActionCommand, "doTransactionalCommand",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			_getMockLiferayPortletActionRequest(),
			new MockLiferayPortletActionResponse());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				jsonObject.getLong("layoutPageTemplateEntryId"));

		Layout layout = _layoutLocalService.fetchLayout(
			layoutPageTemplateEntry.getPlid());

		draftLayout = layout.fetchDraftLayout();

		Assert.assertEquals(
			masterLayoutPageTemplateEntry.getPlid(),
			draftLayout.getMasterLayoutPlid());
		Assert.assertEquals(
			styleBookEntry.getStyleBookEntryId(),
			draftLayout.getStyleBookEntryId());

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			LayoutPageTemplateStructureLocalServiceUtil.
				fetchLayoutPageTemplateStructure(
					draftLayout.getGroupId(), draftLayout.getPlid());

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());

		LayoutStructureItem layoutStructureItem =
			layoutStructure.getMainLayoutStructureItem();

		FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem =
			(FragmentStyledLayoutStructureItem)
				layoutStructure.getLayoutStructureItem(
					layoutStructureItem.getChildrenItemId(0));

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentStyledLayoutStructureItem.getFragmentEntryLinkId());

		Assert.assertTrue(fragmentEntryLink.isTypePortlet());

		JSONObject editableValuesJSONObject = _jsonFactory.createJSONObject(
			fragmentEntryLink.getEditableValues());

		Assert.assertEquals(
			JournalContentPortletKeys.JOURNAL_CONTENT,
			editableValuesJSONObject.getString("portletId"));
	}

	@Test
	@TestInfo("LPD-49417")
	public void testCreateLayoutPageTemplateEntryWithLayoutNameWithInvalidCharacters()
		throws Exception {

		StringBundler expectedSB = new StringBundler();
		StringBundler sb = new StringBundler();
		String string = RandomTestUtil.randomString(1);

		for (char c : _BLACKLIST_CHARS) {
			expectedSB.append(CharPool.DASH);
			sb.append(c);

			expectedSB.append(string);
			sb.append(string);
		}

		_layoutLocalService.updateName(
			_layout, sb.toString(),
			LanguageUtil.getLanguageId(LocaleUtil.getSiteDefault()));

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			_mvcActionCommand, "doTransactionalCommand",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			_getMockLiferayPortletActionRequest(),
			new MockLiferayPortletActionResponse());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				jsonObject.getLong("layoutPageTemplateEntryId"));

		Assert.assertEquals(
			expectedSB + " - page-template", layoutPageTemplateEntry.getName());
	}

	private MockLiferayPortletActionRequest
			_getMockLiferayPortletActionRequest()
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(WebKeys.LAYOUT, _layout);
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		mockLiferayPortletActionRequest.addParameter(
			"groupId", String.valueOf(_group.getGroupId()));
		mockLiferayPortletActionRequest.setParameter(
			"layoutPageTemplateCollectionId",
			String.valueOf(
				_layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId()));
		mockLiferayPortletActionRequest.addParameter(
			"segmentsExperienceId",
			String.valueOf(
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_layout.getPlid())));

		return mockLiferayPortletActionRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLayout(_layout);
		themeDisplay.setLayoutSet(_layout.getLayoutSet());
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setPlid(_layout.getPlid());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private static final char[] _BLACKLIST_CHARS = {
		';', '/', '?', ':', '@', '=', '&', '\"', '<', '>', '#', '%', '{', '}',
		'|', '\\', '^', '~', '[', ']', '`'
	};

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JSONFactory _jsonFactory;

	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	private LayoutPageTemplateCollection _layoutPageTemplateCollection;

	@Inject
	private LayoutPageTemplateCollectionLocalService
		_layoutPageTemplateCollectionLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject(
		filter = "mvc.command.name=/layout_content_page_editor/create_layout_page_template_entry"
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject
	private StyleBookEntryLocalService _styleBookEntryLocalService;

}