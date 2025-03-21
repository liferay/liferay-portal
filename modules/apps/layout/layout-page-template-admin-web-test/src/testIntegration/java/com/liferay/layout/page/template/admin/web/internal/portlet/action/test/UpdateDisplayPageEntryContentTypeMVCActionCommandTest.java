/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.util.DDMUtil;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.manager.LayoutLockManager;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.RenderURLBuilder;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class UpdateDisplayPageEntryContentTypeMVCActionCommandTest {

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

		_controlPanelLayout = _layoutLocalService.getLayout(
			_portal.getControlPanelPlid(_company.getCompanyId()));
	}

	@Test
	public void testUpdateDisplayPageEntryContentType() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		DDMStructure ddmStructure = _addDDMStructure(serviceContext);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.addLayoutPageTemplateEntry(
				null, serviceContext.getScopeGroupId(), 0, null,
				_portal.getClassNameId(JournalArticle.class.getName()),
				ddmStructure.getStructureId(), RandomTestUtil.randomString(), 0,
				WorkflowConstants.STATUS_DRAFT, serviceContext);

		String redirect = RandomTestUtil.randomString();

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(
				layoutPageTemplateEntry, redirect);

		MockLiferayPortletActionResponse mockLiferayPortletActionResponse =
			new MockLiferayPortletActionResponse();

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "doProcessAction",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest, mockLiferayPortletActionResponse);

		_assertLayoutPageTemplateEntry(
			_portal.getClassNameId(AssetCategory.class.getName()), 0,
			layoutPageTemplateEntry);

		_assertMockLiferayPortletActionResponse(
			JSONUtil.put("redirectURL", redirect),
			mockLiferayPortletActionResponse);

		Assert.assertTrue(
			SessionErrors.isEmpty(mockLiferayPortletActionRequest));

		Assert.assertTrue(
			SessionMessages.contains(
				mockLiferayPortletActionRequest,
				"displayPageContentTypeChanged"));

		String message = GetterUtil.getString(
			SessionMessages.get(
				mockLiferayPortletActionRequest,
				"displayPageContentTypeChanged"));

		Assert.assertEquals(
			message,
			_language.format(
				_portal.getSiteDefaultLocale(_group),
				"the-content-type-of-x-was-successfully-changed",
				new String[] {
					HtmlUtil.escape(layoutPageTemplateEntry.getName())
				}),
			message);
	}

	@Test
	public void testUpdateDisplayPageEntryContentTypeOnLockedLayout()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		DDMStructure ddmStructure = _addDDMStructure(serviceContext);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.addLayoutPageTemplateEntry(
				null, serviceContext.getScopeGroupId(), 0, null,
				_portal.getClassNameId(JournalArticle.class.getName()),
				ddmStructure.getStructureId(), RandomTestUtil.randomString(), 0,
				WorkflowConstants.STATUS_DRAFT, serviceContext);

		User user = UserTestUtil.addCompanyAdminUser(
			_companyLocalService.getCompany(_group.getCompanyId()));

		Layout draftLayout = _lockLayoutPageTemplateEntryDraftLayout(
			layoutPageTemplateEntry, user);

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(
				layoutPageTemplateEntry, RandomTestUtil.randomString());

		MockLiferayPortletActionResponse mockLiferayPortletActionResponse =
			new MockLiferayPortletActionResponse();

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "doProcessAction",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest, mockLiferayPortletActionResponse);

		_assertLayoutUnlocked(user, draftLayout);

		_assertLayoutPageTemplateEntry(
			layoutPageTemplateEntry.getClassNameId(),
			layoutPageTemplateEntry.getClassTypeId(), layoutPageTemplateEntry);

		_assertMockLiferayPortletActionResponse(
			JSONUtil.put("error", JSONUtil.put("isLocked", true)),
			mockLiferayPortletActionResponse);

		Assert.assertTrue(
			SessionErrors.isEmpty(mockLiferayPortletActionRequest));
	}

	@Test
	public void testUpdateDisplayPageEntryContentTypeWithUsages()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		long classNameId = _portal.getClassNameId(
			JournalArticle.class.getName());

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.addLayoutPageTemplateEntry(
				null, serviceContext.getScopeGroupId(), 0, null, classNameId,
				journalArticle.getDDMStructureId(),
				RandomTestUtil.randomString(), 0,
				WorkflowConstants.STATUS_DRAFT, serviceContext);

		_assetDisplayPageEntryLocalService.addAssetDisplayPageEntry(
			TestPropsValues.getUserId(), _group.getGroupId(), classNameId,
			journalArticle.getResourcePrimKey(),
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			AssetDisplayPageConstants.TYPE_SPECIFIC, serviceContext);

		Assert.assertEquals(
			1,
			_assetDisplayPageEntryLocalService.getAssetDisplayPageEntriesCount(
				layoutPageTemplateEntry.getClassNameId(),
				layoutPageTemplateEntry.getClassTypeId(),
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
				layoutPageTemplateEntry.isDefaultTemplate()));

		String redirect = RandomTestUtil.randomString();

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(
				layoutPageTemplateEntry, redirect);

		MockLiferayPortletActionResponse mockLiferayPortletActionResponse =
			new MockLiferayPortletActionResponse();

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "doProcessAction",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest, mockLiferayPortletActionResponse);

		_assertLayoutPageTemplateEntry(
			layoutPageTemplateEntry.getClassNameId(),
			layoutPageTemplateEntry.getClassTypeId(), layoutPageTemplateEntry);

		_assertMockLiferayPortletActionResponse(
			JSONUtil.put(
				"error",
				JSONUtil.put(
					"assetType",
					_resourceActions.getModelResource(
						_portal.getSiteDefaultLocale(_group),
						JournalArticle.class.getName())
				).put(
					"hasUsages", true
				).put(
					"viewUsagesURL",
					_getExpectedViewUsagesURL(
						mockLiferayPortletActionResponse, redirect,
						layoutPageTemplateEntry)
				)),
			mockLiferayPortletActionResponse);

		Assert.assertTrue(
			SessionErrors.isEmpty(mockLiferayPortletActionRequest));
	}

	private DDMStructure _addDDMStructure(ServiceContext serviceContext)
		throws Exception {

		DDMForm ddmForm = DDMStructureTestUtil.getSampleDDMForm();

		DDMFormLayout ddmFormLayout = DDMUtil.getDefaultDDMFormLayout(ddmForm);

		return _ddmStructureLocalService.addStructure(
			null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
			_portal.getClassNameId(JournalArticle.class.getName()), null,
			RandomTestUtil.randomLocaleStringMap(), null, ddmForm,
			ddmFormLayout, StorageType.DEFAULT.toString(),
			DDMStructureConstants.TYPE_DEFAULT, serviceContext);
	}

	private void _assertLayoutPageTemplateEntry(
			long classNameId, long classTypeId,
			LayoutPageTemplateEntry layoutPageTemplateEntry)
		throws Exception {

		layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.getLayoutPageTemplateEntry(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId());

		Assert.assertEquals(
			layoutPageTemplateEntry.getClassNameId(), classNameId,
			layoutPageTemplateEntry.getClassNameId());
		Assert.assertEquals(
			layoutPageTemplateEntry.getClassTypeId(), classTypeId,
			layoutPageTemplateEntry.getClassTypeId());
	}

	private void _assertLayoutUnlocked(User user, Layout layout)
		throws Exception {

		Assert.assertTrue(layout.isUnlocked(Constants.EDIT, user.getUserId()));
		Assert.assertFalse(
			layout.isUnlocked(Constants.EDIT, TestPropsValues.getUserId()));
	}

	private void _assertMockLiferayPortletActionResponse(
			JSONObject expectedJSONObject,
			MockLiferayPortletActionResponse mockLiferayPortletActionResponse)
		throws Exception {

		MockHttpServletResponse mockHttpServletResponse =
			(MockHttpServletResponse)
				mockLiferayPortletActionResponse.getHttpServletResponse();

		String content = mockHttpServletResponse.getContentAsString();

		Assert.assertTrue(content, JSONUtil.isJSONObject(content));
		Assert.assertEquals(content, expectedJSONObject.toString(), content);
	}

	private String _getExpectedViewUsagesURL(
		MockLiferayPortletActionResponse mockLiferayPortletActionResponse,
		String redirect, LayoutPageTemplateEntry layoutPageTemplateEntry) {

		return RenderURLBuilder.createRenderURL(
			mockLiferayPortletActionResponse
		).setMVCRenderCommandName(
			"/layout_page_template_admin/view_asset_display_page_usages"
		).setRedirect(
			redirect
		).setParameter(
			"classNameId", layoutPageTemplateEntry.getClassNameId()
		).setParameter(
			"classTypeId", layoutPageTemplateEntry.getClassTypeId()
		).setParameter(
			"layoutPageTemplateEntryId",
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId()
		).setParameter(
			"defaultTemplate", layoutPageTemplateEntry.isDefaultTemplate()
		).buildString();
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			LayoutPageTemplateEntry layoutPageTemplateEntry, String redirect)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			ContentLayoutTestUtil.getMockLiferayPortletActionRequest(
				_company, _group, _controlPanelLayout);

		mockLiferayPortletActionRequest.addParameter(
			"classNameId",
			String.valueOf(
				_portal.getClassNameId(AssetCategory.class.getName())));
		mockLiferayPortletActionRequest.addParameter("classTypeId", "0");
		mockLiferayPortletActionRequest.addParameter(
			"layoutPageTemplateEntryId",
			String.valueOf(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId()));

		mockLiferayPortletActionRequest.addParameter("redirect", redirect);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)mockLiferayPortletActionRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			mockLiferayPortletActionRequest);

		themeDisplay.setRequest(httpServletRequest);

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		return mockLiferayPortletActionRequest;
	}

	private Layout _lockLayoutPageTemplateEntryDraftLayout(
			LayoutPageTemplateEntry layoutPageTemplateEntry, User user)
		throws Exception {

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		Layout draftLayout = layout.fetchDraftLayout();

		Assert.assertNotNull(draftLayout);

		_layoutLockManager.getLock(draftLayout, user.getUserId());

		_assertLayoutUnlocked(user, draftLayout);

		return draftLayout;
	}

	@Inject
	private AssetDisplayPageEntryLocalService
		_assetDisplayPageEntryLocalService;

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	private Layout _controlPanelLayout;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Language _language;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutLockManager _layoutLockManager;

	@Inject
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

	@Inject(
		filter = "mvc.command.name=/layout_page_template_admin/update_display_page_entry_content_type"
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private Portal _portal;

	@Inject
	private ResourceActions _resourceActions;

}