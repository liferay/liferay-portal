/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
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
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

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
@Sync
public class PropagateFragmentEntryChangesMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = _groupLocalService.fetchGroup(TestPropsValues.getGroupId());

		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(_group.getGroupId());

		Group globalGroup = _groupLocalService.getCompanyGroup(
			TestPropsValues.getCompanyId());

		FragmentCollection globalFragmentCollection =
			FragmentTestUtil.addFragmentCollection(globalGroup.getGroupId());

		_fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			fragmentCollection.getFragmentCollectionId());
		_globalFragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			globalFragmentCollection.getFragmentCollectionId());
	}

	@Test
	public void testPropagateChangesOfFragmentEntryToLockedContentLayout()
		throws Exception {

		_testPropagateChangesOfFragmentEntryToLockedContentLayout(
			_fragmentEntry);
		_testPropagateChangesOfFragmentEntryToLockedContentLayout(
			_globalFragmentEntry);
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			String[] rowIds)
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
		mockLiferayPortletActionRequest.setParameter("rowIds", rowIds);

		return mockLiferayPortletActionRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));

		Layout controlPanelLayout = _layoutLocalService.getLayout(
			_portal.getControlPanelPlid(TestPropsValues.getCompanyId()));

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

	private void _testPropagateChangesOfFragmentEntryToLockedContentLayout(
			FragmentEntry fragmentEntry)
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentPublishedLayout(
			_group, RandomTestUtil.randomString(),
			WorkflowConstants.STATUS_APPROVED);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), null,
				fragmentEntry.getExternalReferenceCode(),
				fragmentEntry.getScopeERC(),
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(layout.getPlid()),
				layout.getPlid(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				"{fieldSets: []}", StringPool.BLANK, StringPool.BLANK, 0, null,
				fragmentEntry.getType(),
				ServiceContextTestUtil.getServiceContext(
					_group, TestPropsValues.getUserId()));

		fragmentEntry.setCss(RandomTestUtil.randomString());
		fragmentEntry.setHtml(RandomTestUtil.randomString());
		fragmentEntry.setJs(RandomTestUtil.randomString());

		fragmentEntry = _fragmentEntryLocalService.updateFragmentEntry(
			fragmentEntry);

		Layout draftLayout = layout.fetchDraftLayout();

		Assert.assertNotNull(draftLayout);

		User user = UserTestUtil.addCompanyAdminUser(
			_companyLocalService.getCompany(_group.getCompanyId()));

		_layoutLockManager.getLock(draftLayout, user.getUserId());

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "processAction",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			_getMockLiferayPortletActionRequest(
				new String[] {
					String.valueOf(fragmentEntryLink.getFragmentEntryLinkId())
				}),
			new MockLiferayPortletActionResponse());

		FragmentEntryLink persistedFragmentEntryLink =
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentEntryLink.getFragmentEntryLinkId());

		Assert.assertEquals(
			fragmentEntry.getCss(), persistedFragmentEntryLink.getCss());
		Assert.assertEquals(
			fragmentEntry.getHtml(), persistedFragmentEntryLink.getHtml());
		Assert.assertEquals(
			fragmentEntry.getJs(), persistedFragmentEntryLink.getJs());

		_layoutLocalService.deleteLayout(layout);
	}

	@Inject
	private CompanyLocalService _companyLocalService;

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
		filter = "mvc.command.name=/fragment/propagate_fragment_entry_changes"
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private Portal _portal;

	@Inject
	private PortletLocalService _portletLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}