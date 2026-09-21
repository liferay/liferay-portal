/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.fragment.test.util.FragmentEntryTestUtil;
import com.liferay.fragment.test.util.FragmentTestUtil;
import com.liferay.layout.manager.LayoutLockManager;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ScopeUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Collections;

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
@Sync
public class PropagateGroupFragmentEntryChangesMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = _groupLocalService.fetchGroup(TestPropsValues.getGroupId());

		Group companyGroup = _groupLocalService.getCompanyGroup(
			TestPropsValues.getCompanyId());

		_fragmentEntry = _addFragmentEntry(_group.getGroupId());

		_globalFragmentEntry = _addFragmentEntry(companyGroup.getGroupId());

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			Collections.emptyMap(), DepotConstants.TYPE_DESIGN_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		_connectedGroup = GroupTestUtil.addGroup();

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			_depotEntry.getDepotEntryId(), _connectedGroup.getGroupId());

		_unconnectedGroup = GroupTestUtil.addGroup();
	}

	@Test
	public void testAddFragmentEntryLink() throws Exception {
		_testAddFragmentEntryLink(_fragmentEntry);
		_testAddFragmentEntryLink(_globalFragmentEntry);
	}

	@Test
	@TestInfo("LPD-102204")
	public void testPropagateChangesFromDesignLibrary() throws Exception {
		_testPropagateChangesFromDesignLibraryWithConnectedAndUnconnectedGroups();
		_testPropagateChangesFromDesignLibraryWithDuplicateGroupIds();
		_testPropagateChangesFromDesignLibraryWithSourceGroup();
		_testPropagateChangesFromDesignLibraryWithUnconnectedGroup();
	}

	@Test
	public void testPropagateChangesOfFragmentEntryToLockedContentLayout()
		throws Exception {

		_testPropagateChangesOfFragmentEntryToLockedContentLayout(
			_fragmentEntry);
		_testPropagateChangesOfFragmentEntryToLockedContentLayout(
			_globalFragmentEntry);
	}

	private FragmentEntry _addFragmentEntry(long groupId) throws Exception {
		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(groupId);

		return FragmentEntryTestUtil.addFragmentEntry(
			fragmentCollection.getFragmentCollectionId());
	}

	private FragmentEntryLink _addFragmentEntryLink(
			FragmentEntry fragmentEntry, Group group, long plid)
		throws Exception {

		return _fragmentEntryLinkLocalService.addFragmentEntryLink(
			null, TestPropsValues.getUserId(), group.getGroupId(), null,
			fragmentEntry.getExternalReferenceCode(),
			ScopeUtil.getItemScopeExternalReferenceCode(
				fragmentEntry.getGroupId(), group.getGroupId()),
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				plid),
			plid, fragmentEntry.getCss(), fragmentEntry.getHtml(),
			fragmentEntry.getJs(), "{fieldSets: []}", StringPool.BLANK,
			StringPool.BLANK, 0, null, fragmentEntry.getType(),
			ServiceContextTestUtil.getServiceContext(
				group, TestPropsValues.getUserId()));
	}

	private void _assertFragmentEntryLinkContent(
		FragmentEntry fragmentEntry, long fragmentEntryLinkId) {

		FragmentEntryLink persistedFragmentEntryLink =
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentEntryLinkId);

		Assert.assertEquals(
			fragmentEntry.getCss(), persistedFragmentEntryLink.getCss());
		Assert.assertEquals(
			fragmentEntry.getHtml(), persistedFragmentEntryLink.getHtml());
		Assert.assertEquals(
			fragmentEntry.getJs(), persistedFragmentEntryLink.getJs());
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			FragmentEntry fragmentEntry, long plid)
		throws Exception {

		return _getMockLiferayPortletActionRequest(
			fragmentEntry, plid, new long[] {_group.getGroupId()});
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			FragmentEntry fragmentEntry, long plid, long[] rowIds)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			new MockLiferayPortletActionResponse());

		Portlet portlet = _portletLocalService.getPortletById(
			FragmentPortletKeys.FRAGMENT);

		LiferayPortletConfig liferayPortletConfig =
			(LiferayPortletConfig)PortletConfigFactoryUtil.create(
				portlet, null);

		mockLiferayPortletActionRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG, liferayPortletConfig);

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());
		mockLiferayPortletActionRequest.setParameter(
			"segmentsExperienceId",
			String.valueOf(
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(plid)));
		mockLiferayPortletActionRequest.setParameter(
			"fragmentEntryERC", fragmentEntry.getExternalReferenceCode());
		mockLiferayPortletActionRequest.setParameter(
			"fragmentEntryGroupId", String.valueOf(fragmentEntry.getGroupId()));
		mockLiferayPortletActionRequest.setParameter(
			"rowIds", ArrayUtil.toStringArray(rowIds));

		return mockLiferayPortletActionRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		Company company = _companyLocalService.getCompany(
			_group.getCompanyId());

		themeDisplay.setCompany(company);

		Layout controlPanelLayout = _layoutLocalService.getLayout(
			_portal.getControlPanelPlid(company.getCompanyId()));

		themeDisplay.setLayout(controlPanelLayout);

		LayoutSet layoutSet = _group.getPublicLayoutSet();

		themeDisplay.setLookAndFeel(layoutSet.getTheme(), null);

		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private void _testAddFragmentEntryLink(FragmentEntry fragmentEntry)
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink(
			fragmentEntry, _group, layout.getPlid());

		fragmentEntry = _updateFragmentEntry(fragmentEntry);

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "processAction",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			_getMockLiferayPortletActionRequest(
				fragmentEntry, layout.getPlid()),
			new MockLiferayPortletActionResponse());

		_assertFragmentEntryLinkContent(
			fragmentEntry, fragmentEntryLink.getFragmentEntryLinkId());

		_layoutLocalService.deleteLayout(layout);
	}

	private void _testPropagateChangesFromDesignLibraryWithConnectedAndUnconnectedGroups()
		throws Exception {

		FragmentEntry fragmentEntry = _addFragmentEntry(
			_depotEntry.getGroupId());

		Layout connectedLayout = LayoutTestUtil.addTypeContentLayout(
			_connectedGroup);

		FragmentEntryLink connectedFragmentEntryLink = _addFragmentEntryLink(
			fragmentEntry, _connectedGroup, connectedLayout.getPlid());

		Layout unconnectedLayout = LayoutTestUtil.addTypeContentLayout(
			_unconnectedGroup);

		FragmentEntryLink unconnectedFragmentEntryLink = _addFragmentEntryLink(
			fragmentEntry, _unconnectedGroup, unconnectedLayout.getPlid());

		FragmentEntry staleFragmentEntry = (FragmentEntry)fragmentEntry.clone();

		fragmentEntry = _updateFragmentEntry(fragmentEntry);

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(
				fragmentEntry, connectedLayout.getPlid(),
				new long[] {
					_connectedGroup.getGroupId(), _unconnectedGroup.getGroupId()
				});

		boolean success = ReflectionTestUtil.invoke(
			_mvcActionCommand, "processAction",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		Assert.assertTrue(success);

		Assert.assertNotNull(
			SessionMessages.get(
				mockLiferayPortletActionRequest,
				"sitesSkippedFromPropagation"));

		_assertFragmentEntryLinkContent(
			fragmentEntry, connectedFragmentEntryLink.getFragmentEntryLinkId());

		_assertFragmentEntryLinkContent(
			staleFragmentEntry,
			unconnectedFragmentEntryLink.getFragmentEntryLinkId());
	}

	private void _testPropagateChangesFromDesignLibraryWithDuplicateGroupIds()
		throws Exception {

		FragmentEntry fragmentEntry = _addFragmentEntry(
			_depotEntry.getGroupId());

		Layout connectedLayout = LayoutTestUtil.addTypeContentLayout(
			_connectedGroup);

		_addFragmentEntryLink(
			fragmentEntry, _connectedGroup, connectedLayout.getPlid());

		fragmentEntry = _updateFragmentEntry(fragmentEntry);

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(
				fragmentEntry, connectedLayout.getPlid(),
				new long[] {
					_connectedGroup.getGroupId(), _connectedGroup.getGroupId()
				});

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "processAction",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		Assert.assertNull(
			SessionMessages.get(
				mockLiferayPortletActionRequest,
				"sitesSkippedFromPropagation"));
	}

	private void _testPropagateChangesFromDesignLibraryWithSourceGroup()
		throws Exception {

		FragmentEntry fragmentEntry = _addFragmentEntry(
			_depotEntry.getGroupId());

		Group depotGroup = _groupLocalService.getGroup(
			_depotEntry.getGroupId());

		Layout depotLayout = LayoutTestUtil.addTypeContentLayout(depotGroup);

		FragmentEntryLink depotFragmentEntryLink = _addFragmentEntryLink(
			fragmentEntry, depotGroup, depotLayout.getPlid());

		fragmentEntry = _updateFragmentEntry(fragmentEntry);

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "processAction",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			_getMockLiferayPortletActionRequest(
				fragmentEntry, depotLayout.getPlid(),
				new long[] {depotGroup.getGroupId()}),
			new MockLiferayPortletActionResponse());

		_assertFragmentEntryLinkContent(
			fragmentEntry, depotFragmentEntryLink.getFragmentEntryLinkId());
	}

	private void _testPropagateChangesFromDesignLibraryWithUnconnectedGroup()
		throws Exception {

		FragmentEntry fragmentEntry = _addFragmentEntry(
			_depotEntry.getGroupId());

		Layout unconnectedLayout = LayoutTestUtil.addTypeContentLayout(
			_unconnectedGroup);

		_addFragmentEntryLink(
			fragmentEntry, _unconnectedGroup, unconnectedLayout.getPlid());

		boolean success = ReflectionTestUtil.invoke(
			_mvcActionCommand, "processAction",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			_getMockLiferayPortletActionRequest(
				fragmentEntry, unconnectedLayout.getPlid(),
				new long[] {_unconnectedGroup.getGroupId()}),
			new MockLiferayPortletActionResponse());

		Assert.assertFalse(success);
	}

	private void _testPropagateChangesOfFragmentEntryToLockedContentLayout(
			FragmentEntry fragmentEntry)
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink(
			fragmentEntry, _group, layout.getPlid());

		fragmentEntry = _updateFragmentEntry(fragmentEntry);

		Layout draftLayout = layout.fetchDraftLayout();

		Assert.assertNotNull(draftLayout);

		User user = UserTestUtil.getAdminUser(_group.getCompanyId());

		_layoutLockManager.getLock(draftLayout, user.getUserId());

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "processAction",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			_getMockLiferayPortletActionRequest(
				fragmentEntry, layout.getPlid()),
			new MockLiferayPortletActionResponse());

		_assertFragmentEntryLinkContent(
			fragmentEntry, fragmentEntryLink.getFragmentEntryLinkId());

		_layoutLocalService.deleteLayout(layout);
	}

	private FragmentEntry _updateFragmentEntry(FragmentEntry fragmentEntry)
		throws Exception {

		fragmentEntry.setCss(RandomTestUtil.randomString());
		fragmentEntry.setHtml(RandomTestUtil.randomString());
		fragmentEntry.setJs(RandomTestUtil.randomString());

		return _fragmentEntryLocalService.updateFragmentEntry(fragmentEntry);
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _connectedGroup;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	private FragmentEntry _fragmentEntry;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	private FragmentEntry _globalFragmentEntry;
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutLockManager _layoutLockManager;

	@Inject(
		filter = "mvc.command.name=/fragment/propagate_group_fragment_entry_changes"
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private Portal _portal;

	@Inject
	private PortletLocalService _portletLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@DeleteAfterTestRun
	private Group _unconnectedGroup;

}