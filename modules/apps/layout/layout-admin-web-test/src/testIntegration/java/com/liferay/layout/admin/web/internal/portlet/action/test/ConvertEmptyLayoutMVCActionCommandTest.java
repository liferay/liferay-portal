/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalServiceUtil;
import com.liferay.layout.admin.web.internal.portlet.constants.LayoutAdminWebPortletKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.LayoutPageTemplateTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.LayoutTypeException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutPrototypeLocalService;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalServiceUtil;

import jakarta.portlet.PortletException;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Brooke Dalton
 */
@RunWith(Arquillian.class)
public class ConvertEmptyLayoutMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = _groupLocalService.getGroup(TestPropsValues.getGroupId());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getCompanyId(), _group.getGroupId(),
			TestPropsValues.getUserId());

		_serviceContext.setAttribute(
			"layout.instanceable.allowed", Boolean.TRUE);

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testCannotConvertEmptyLayout() throws Exception {
		_testCannotConvertEmptyLayout(LayoutConstants.TYPE_EMBEDDED);
		_testCannotConvertEmptyLayout(LayoutConstants.TYPE_URL);
	}

	@Test
	@TestInfo("LPD-72013")
	public void testConvertEmptyLayoutToContentLayoutWithMasterLayoutPlid()
		throws Exception {

		Layout emptyLayout = LayoutTestUtil.addTypeEmptyLayout(_group);
		LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_group.getGroupId(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
				WorkflowConstants.STATUS_APPROVED);

		_mvcActionCommand.processAction(
			_getMockLiferayPortletActionRequest(
				emptyLayout, 0, masterLayoutPageTemplateEntry.getPlid(),
				LayoutConstants.TYPE_CONTENT, TestPropsValues.getUser()),
			new MockLiferayPortletActionResponse());

		Layout layout = _layoutLocalService.getLayout(emptyLayout.getPlid());

		Assert.assertTrue(layout.isTypeContent());
		Assert.assertEquals(
			masterLayoutPageTemplateEntry.getExternalReferenceCode(),
			layout.getMasterLayoutPageTemplateEntryERC());
	}

	@Test
	@TestInfo("LPD-72013")
	public void testConvertEmptyLayoutToContentLayoutWithPageTemplateEntryId()
		throws Exception {

		Layout emptyLayout = LayoutTestUtil.addTypeEmptyLayout(_group);

		LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_group.getGroupId(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
				WorkflowConstants.STATUS_APPROVED);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_group.getGroupId(), LayoutPageTemplateEntryTypeConstants.BASIC,
				masterLayoutPageTemplateEntry.getPlid(),
				WorkflowConstants.STATUS_APPROVED);

		Layout layoutPageTemplateEntryLayout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		Layout layoutPageTemplateEntryDraftLayout =
			layoutPageTemplateEntryLayout.fetchDraftLayout();

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			StringPool.BLANK, layoutPageTemplateEntryDraftLayout,
			SegmentsExperienceLocalServiceUtil.fetchDefaultSegmentsExperienceId(
				layoutPageTemplateEntryDraftLayout.getPlid()));

		ContentLayoutTestUtil.publishLayout(
			layoutPageTemplateEntryDraftLayout, layoutPageTemplateEntryLayout);

		_mvcActionCommand.processAction(
			_getMockLiferayPortletActionRequest(
				emptyLayout,
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(), 0,
				LayoutConstants.TYPE_CONTENT, TestPropsValues.getUser()),
			new MockLiferayPortletActionResponse());

		Layout layout = _layoutLocalService.getLayout(emptyLayout.getPlid());

		Assert.assertEquals(
			_portal.getClassNameId(LayoutPageTemplateEntry.class),
			layout.getClassNameId());
		Assert.assertEquals(
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			layout.getClassPK());
		Assert.assertEquals(
			masterLayoutPageTemplateEntry.getExternalReferenceCode(),
			layout.getMasterLayoutPageTemplateEntryERC());
		Assert.assertEquals(WorkflowConstants.STATUS_DRAFT, layout.getStatus());

		List<FragmentEntryLink> fragmentEntryLinks =
			FragmentEntryLinkLocalServiceUtil.getFragmentEntryLinksByPlid(
				layout.getGroupId(), layout.getPlid());

		Assert.assertEquals(
			fragmentEntryLinks.toString(), 1, fragmentEntryLinks.size());
	}

	@Test
	public void testConvertEmptyLayoutToPortletLayoutWithoutPermissions()
		throws Exception {

		Layout emptyLayout = LayoutTestUtil.addTypeEmptyLayout(_group);

		User user = _userLocalService.getDefaultUser(_group.getCompanyId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			Exception exception = Assert.assertThrows(
				PortletException.class,
				() -> _mvcActionCommand.processAction(
					_getMockLiferayPortletActionRequest(
						emptyLayout, 0, 0, LayoutConstants.TYPE_PORTLET, user),
					new MockLiferayPortletActionResponse()));

			Assert.assertTrue(
				exception.getCause() instanceof PrincipalException);
		}
	}

	@Test
	@TestInfo("LPD-72013")
	public void testConvertEmptyLayoutToPortletLayoutWithPageTemplateEntryId()
		throws Exception {

		Layout emptyLayout = LayoutTestUtil.addTypeEmptyLayout(_group);

		LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_group.getGroupId(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
				WorkflowConstants.STATUS_APPROVED);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_group.getGroupId(),
				LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE,
				masterLayoutPageTemplateEntry.getPlid(),
				WorkflowConstants.STATUS_APPROVED);

		_mvcActionCommand.processAction(
			_getMockLiferayPortletActionRequest(
				emptyLayout,
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(), 0,
				LayoutConstants.TYPE_PORTLET, TestPropsValues.getUser()),
			new MockLiferayPortletActionResponse());

		Layout layout = _layoutLocalService.getLayout(emptyLayout.getPlid());

		Assert.assertTrue(layout.isTypePortlet());
		Assert.assertEquals(0, layout.getClassNameId());
		Assert.assertEquals(0, layout.getClassPK());
		Assert.assertEquals(
			masterLayoutPageTemplateEntry.getExternalReferenceCode(),
			layout.getMasterLayoutPageTemplateEntryERC());

		Assert.assertEquals(
			layoutPageTemplateEntry.getExternalReferenceCode(),
			layout.getPortletLayoutPageTemplateEntryERC());
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			Layout layout, long layoutPageTemplateEntryId,
			long masterLayoutPlid, String type, User user)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"name", layout.getExternalReferenceCode());

		if (layoutPageTemplateEntryId > 0) {
			mockLiferayPortletActionRequest.addParameter(
				"layoutPageTemplateEntryId",
				String.valueOf(layoutPageTemplateEntryId));
		}

		if (masterLayoutPlid > 0) {
			mockLiferayPortletActionRequest.addParameter(
				"masterLayoutPlid", String.valueOf(masterLayoutPlid));
		}

		mockLiferayPortletActionRequest.addParameter(
			"selPlid", String.valueOf(layout.getPlid()));
		mockLiferayPortletActionRequest.addParameter("type", type);

		mockLiferayPortletActionRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG,
			PortletConfigFactoryUtil.create(
				PortletLocalServiceUtil.getPortletById(
					LayoutAdminWebPortletKeys.LAYOUT_ADMIN_WEB_TEST_PORTLET),
				null));

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.fetchCompany(_group.getCompanyId()));
		themeDisplay.setLanguageId(_group.getDefaultLanguageId());
		themeDisplay.setLayout(layout);
		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		themeDisplay.setRequest(
			mockLiferayPortletActionRequest.getHttpServletRequest());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(user);

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockLiferayPortletActionRequest;
	}

	private void _testCannotConvertEmptyLayout(String type) throws Exception {
		Layout emptyLayout = LayoutTestUtil.addTypeEmptyLayout(_group);
		MockLiferayPortletActionResponse mockLiferayPortletActionResponse =
			new MockLiferayPortletActionResponse();

		try {
			_mvcActionCommand.processAction(
				_getMockLiferayPortletActionRequest(
					emptyLayout, 0, 0, type, TestPropsValues.getUser()),
				mockLiferayPortletActionResponse);

			Assert.fail();
		}
		catch (PortletException portletException) {
			LayoutTypeException layoutTypeException =
				(LayoutTypeException)portletException.getCause();

			Assert.assertEquals(
				LayoutTypeException.TYPE_NOT_ALLOWED,
				layoutTypeException.getType());

			MockHttpServletResponse mockHttpServletResponse =
				(MockHttpServletResponse)
					mockLiferayPortletActionResponse.getHttpServletResponse();

			JSONObject jsonObject = _jsonFactory.createJSONObject(
				mockHttpServletResponse.getContentAsString());

			Assert.assertEquals(
				_language.format(
					LocaleUtil.getDefault(),
					"an-empty-page-cannot-be-converted-to-x",
					_language.get(
						LocaleUtil.getDefault(), "layout.types." + type)),
				jsonObject.getString("errorMessage"));
		}

		Layout layout = _layoutLocalService.getLayout(emptyLayout.getPlid());

		Assert.assertTrue(layout.isTypeEmpty());
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private Language _language;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPrototypeLocalService _layoutPrototypeLocalService;

	@Inject(filter = "mvc.command.name=/layout_admin/convert_empty_layout")
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private Portal _portal;

	private ServiceContext _serviceContext;

	@Inject
	private UserLocalService _userLocalService;

}