/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.fragment.exception.NoSuchEntryException;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.test.util.FragmentEntryTestUtil;
import com.liferay.fragment.test.util.FragmentTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alvaro Fernandez Gonzalez
 */
@RunWith(Arquillian.class)
public class ViewFragmentEntryUsagesMVCRenderCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(_group.getGroupId());

		_fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			fragmentCollection.getFragmentCollectionId());
	}

	@Test
	@TestInfo("LPD-114647")
	public void testRender() throws Exception {
		User user = TestPropsValues.getUser();

		UserTestUtil.setUser(user);

		_assertFound(
			_getMockLiferayPortletRenderRequest(
				_fragmentEntry.getFragmentEntryId(), 0, user));
	}

	@FeatureFlag("LPD-57283")
	@Test
	@TestInfo({"LPD-115801", "LPD-117598"})
	public void testRenderWithGroupId() throws Exception {

		// Company group fragment entry

		User user = TestPropsValues.getUser();

		UserTestUtil.setUser(user);

		_irrelevantGroup = GroupTestUtil.addGroup();

		FragmentEntry fragmentEntry = _addCompanyGroupFragmentEntry();

		_assertFound(
			_getMockLiferayPortletRenderRequest(
				fragmentEntry.getFragmentEntryId(), _group.getGroupId(), user));
		_assertNotFound(
			_getMockLiferayPortletRenderRequest(
				fragmentEntry.getFragmentEntryId(), RandomTestUtil.randomLong(),
				user));

		_company = CompanyTestUtil.addCompany();

		User companyAdminUser = UserTestUtil.getAdminUser(
			_company.getCompanyId());

		Group group = GroupTestUtil.addGroup(
			_company.getCompanyId(), companyAdminUser.getUserId(), 0);

		_assertNotFound(
			_getMockLiferayPortletRenderRequest(
				fragmentEntry.getFragmentEntryId(), group.getGroupId(), user));

		// Design library fragment entry

		fragmentEntry = _addDesignLibraryFragmentEntry();

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			_depotEntry.getDepotEntryId(), _group.getGroupId());

		_assertFound(
			_getMockLiferayPortletRenderRequest(
				fragmentEntry.getFragmentEntryId(), _group.getGroupId(), user));
		_assertNotFound(
			_getMockLiferayPortletRenderRequest(
				fragmentEntry.getFragmentEntryId(),
				_irrelevantGroup.getGroupId(), user));

		// Site fragment entry

		_assertFound(
			_getMockLiferayPortletRenderRequest(
				_fragmentEntry.getFragmentEntryId(), _group.getGroupId(),
				user));
		_assertNotFound(
			_getMockLiferayPortletRenderRequest(
				_fragmentEntry.getFragmentEntryId(),
				_irrelevantGroup.getGroupId(), user));
	}

	@Test
	@TestInfo("LPD-114647")
	public void testRenderWithNonexistentFragmentEntry() throws Exception {
		User user = TestPropsValues.getUser();

		UserTestUtil.setUser(user);

		_assertNotFound(
			_getMockLiferayPortletRenderRequest(
				RandomTestUtil.randomLong(), 0, user));
	}

	@FeatureFlag("LPD-57283")
	@Test
	@TestInfo({"LPD-115801", "LPD-117598"})
	public void testRenderWithoutGroupId() throws Exception {

		// Company group fragment entry

		User user = TestPropsValues.getUser();

		UserTestUtil.setUser(user);

		FragmentEntry fragmentEntry = _addCompanyGroupFragmentEntry();

		_assertNotFound(
			_getMockLiferayPortletRenderRequest(
				fragmentEntry.getFragmentEntryId(), 0, user));

		// Design library fragment entry

		fragmentEntry = _addDesignLibraryFragmentEntry();

		_assertNotFound(
			_getMockLiferayPortletRenderRequest(
				fragmentEntry.getFragmentEntryId(), 0, user));

		// Site fragment entry

		_assertFound(
			_getMockLiferayPortletRenderRequest(
				_fragmentEntry.getFragmentEntryId(), 0, user));
	}

	@Test
	@TestInfo("LPD-114647")
	public void testRenderWithoutManageFragmentEntriesPermission()
		throws Exception {

		_user = UserTestUtil.addGroupUser(_group, RoleConstants.POWER_USER);

		UserTestUtil.setUser(_user);

		_assertNotFound(
			_getMockLiferayPortletRenderRequest(
				_fragmentEntry.getFragmentEntryId(), 0, _user));
	}

	private FragmentEntry _addCompanyGroupFragmentEntry() throws Exception {
		Group companyGroup = _groupLocalService.getCompanyGroup(
			TestPropsValues.getCompanyId());

		_fragmentCollection = FragmentTestUtil.addFragmentCollection(
			companyGroup.getGroupId());

		return FragmentEntryTestUtil.addFragmentEntry(
			_fragmentCollection.getFragmentCollectionId());
	}

	private FragmentEntry _addDesignLibraryFragmentEntry() throws Exception {
		_depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			Collections.emptyMap(), DepotConstants.TYPE_DESIGN_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(_depotEntry.getGroupId());

		return FragmentEntryTestUtil.addFragmentEntry(
			fragmentCollection.getFragmentCollectionId());
	}

	private void _assertFound(
			MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest)
		throws Exception {

		Assert.assertEquals(
			"/view_fragment_entry_usages.jsp",
			_mvcRenderCommand.render(
				mockLiferayPortletRenderRequest,
				new MockLiferayPortletRenderResponse()));
	}

	private void _assertNotFound(
			MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest)
		throws Exception {

		Assert.assertEquals(
			"/error.jsp",
			_mvcRenderCommand.render(
				mockLiferayPortletRenderRequest,
				new MockLiferayPortletRenderResponse()));

		Assert.assertTrue(
			SessionErrors.contains(
				mockLiferayPortletRenderRequest, NoSuchEntryException.class));
	}

	private MockLiferayPortletRenderRequest _getMockLiferayPortletRenderRequest(
			long fragmentEntryId, long groupId, User user)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		ThemeDisplay themeDisplay = ContentLayoutTestUtil.getThemeDisplay(
			_companyLocalService.getCompany(_group.getCompanyId()), _group,
			layout);

		themeDisplay.setUser(user);

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockLiferayPortletRenderRequest.setParameter(
			"fragmentEntryId", String.valueOf(fragmentEntryId));

		if (groupId > 0) {
			mockLiferayPortletRenderRequest.setParameter(
				"groupId", String.valueOf(groupId));
		}

		return mockLiferayPortletRenderRequest;
	}

	@DeleteAfterTestRun
	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@DeleteAfterTestRun
	private FragmentCollection _fragmentCollection;

	private FragmentEntry _fragmentEntry;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@DeleteAfterTestRun
	private Group _irrelevantGroup;

	@Inject(filter = "mvc.command.name=/fragment/view_fragment_entry_usages")
	private MVCRenderCommand _mvcRenderCommand;

	@DeleteAfterTestRun
	private User _user;

}