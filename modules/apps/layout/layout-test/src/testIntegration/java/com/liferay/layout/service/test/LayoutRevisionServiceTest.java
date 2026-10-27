/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.NoSuchLayoutRevisionException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutBranch;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutRevision;
import com.liferay.portal.kernel.model.LayoutRevisionConstants;
import com.liferay.portal.kernel.model.LayoutSetBranch;
import com.liferay.portal.kernel.model.LayoutSetBranchConstants;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.LayoutBranchLocalService;
import com.liferay.portal.kernel.service.LayoutRevisionLocalService;
import com.liferay.portal.kernel.service.LayoutRevisionService;
import com.liferay.portal.kernel.service.LayoutSetBranchLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@FeatureFlag("LPD-105778")
@RunWith(Arquillian.class)
public class LayoutRevisionServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypePortletLayout(_group);
		_layoutSetBranch = _addLayoutSetBranch(_group);
	}

	@Test
	public void testAddLayoutRevision() throws Exception {
		_testAddLayoutRevision();
		_testAddLayoutRevisionWhenFeatureFlagDisabled();
		_testAddLayoutRevisionWithAnotherLayoutPortletPreferencesPlid();
		_testAddLayoutRevisionWithInvalidParentLayoutRevisionId();
		_testAddLayoutRevisionWithLayoutFromAnotherGroup();
	}

	private LayoutRevision _addLayoutRevision(
			Layout layout, long parentLayoutRevisionId,
			long portletPreferencesPlid)
		throws Exception {

		LayoutBranch layoutBranch =
			_layoutBranchLocalService.getMasterLayoutBranch(
				_layoutSetBranch.getLayoutSetBranchId(), _layout.getPlid());

		return _layoutRevisionService.addLayoutRevision(
			RandomTestUtil.randomLong(),
			_layoutSetBranch.getLayoutSetBranchId(),
			layoutBranch.getLayoutBranchId(), parentLayoutRevisionId, false,
			layout.getPlid(), portletPreferencesPlid, layout.isPrivateLayout(),
			layout.getName(), layout.getTitle(), layout.getDescription(),
			layout.getKeywords(), layout.getRobots(), layout.getTypeSettings(),
			layout.getIconImage(), layout.getIconImageId(), layout.getThemeId(),
			layout.getColorSchemeId(), layout.getCss(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private LayoutSetBranch _addLayoutSetBranch(Group group) throws Exception {
		return _layoutSetBranchLocalService.addLayoutSetBranch(
			TestPropsValues.getUserId(), group.getGroupId(), false,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), true,
			LayoutSetBranchConstants.ALL_BRANCHES,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));
	}

	private void _assertPortletPreferences(
		LayoutRevision layoutRevision, String portletId) {

		List<PortletPreferences> portletPreferencesList =
			_portletPreferencesLocalService.getPortletPreferencesByPlid(
				layoutRevision.getLayoutRevisionId());

		Assert.assertEquals(
			portletPreferencesList.toString(), 1,
			portletPreferencesList.size());

		PortletPreferences portletPreferences = portletPreferencesList.get(0);

		Assert.assertEquals(portletId, portletPreferences.getPortletId());
	}

	private void _testAddLayoutRevision() throws Exception {
		String portletId = RandomTestUtil.randomString();

		_portletPreferencesLocalService.updatePreferences(
			PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, _layout.getPlid(), portletId,
			StringBundler.concat(
				"<portlet-preferences><preference><name>",
				RandomTestUtil.randomString(), "</name><value>",
				RandomTestUtil.randomString(),
				"</value></preference></portlet-preferences>"));

		LayoutRevision layoutRevision = _addLayoutRevision(
			_layout, LayoutRevisionConstants.DEFAULT_PARENT_LAYOUT_REVISION_ID,
			LayoutConstants.DEFAULT_PLID);

		Assert.assertEquals(
			TestPropsValues.getUserId(), layoutRevision.getUserId());

		_assertPortletPreferences(layoutRevision, portletId);

		_assertPortletPreferences(
			_addLayoutRevision(
				_layout,
				LayoutRevisionConstants.DEFAULT_PARENT_LAYOUT_REVISION_ID,
				layoutRevision.getLayoutRevisionId()),
			portletId);

		Assert.assertNotNull(
			_addLayoutRevision(
				_layout, layoutRevision.getLayoutRevisionId(),
				LayoutConstants.DEFAULT_PLID));
	}

	private void _testAddLayoutRevisionWhenFeatureFlagDisabled()
		throws Exception {

		try (SafeCloseable safeCloseable =
				FeatureFlagTestUtil.setFeatureFlagsWithSafeCloseable(
					false, "LPD-105778")) {

			AssertUtils.assertFailure(
				UnsupportedOperationException.class,
				"Feature flag LPD-105778 is disabled for company " +
					TestPropsValues.getCompanyId(),
				() -> _addLayoutRevision(
					_layout,
					LayoutRevisionConstants.DEFAULT_PARENT_LAYOUT_REVISION_ID,
					LayoutConstants.DEFAULT_PLID));
		}
	}

	private void _testAddLayoutRevisionWithAnotherLayoutPortletPreferencesPlid()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		_groups.add(group);

		Layout layout = LayoutTestUtil.addTypePortletLayout(group);

		LayoutSetBranch layoutSetBranch = _addLayoutSetBranch(group);

		LayoutRevision layoutRevision =
			_layoutRevisionLocalService.fetchLatestLayoutRevision(
				layoutSetBranch.getLayoutSetBranchId(), layout.getPlid());

		AssertUtils.assertFailure(
			NoSuchLayoutRevisionException.class,
			StringBundler.concat(
				"No layout revision exists with the primary key ",
				layout.getPlid(), " for layout ", _layout.getPlid()),
			() -> _addLayoutRevision(
				_layout,
				LayoutRevisionConstants.DEFAULT_PARENT_LAYOUT_REVISION_ID,
				layout.getPlid()));

		AssertUtils.assertFailure(
			NoSuchLayoutRevisionException.class,
			StringBundler.concat(
				"No layout revision exists with the primary key ",
				layoutRevision.getLayoutRevisionId(), " for layout ",
				_layout.getPlid()),
			() -> _addLayoutRevision(
				_layout,
				LayoutRevisionConstants.DEFAULT_PARENT_LAYOUT_REVISION_ID,
				layoutRevision.getLayoutRevisionId()));
	}

	private void _testAddLayoutRevisionWithInvalidParentLayoutRevisionId()
		throws Exception {

		// Nonexistent parent layout revision

		long parentLayoutRevisionId = RandomTestUtil.randomLong();

		AssertUtils.assertFailure(
			NoSuchLayoutRevisionException.class,
			StringBundler.concat(
				"No layout revision exists with the primary key ",
				parentLayoutRevisionId, " for layout ", _layout.getPlid()),
			() -> _addLayoutRevision(
				_layout, parentLayoutRevisionId, LayoutConstants.DEFAULT_PLID));

		// Parent layout revision from another group

		Group group = GroupTestUtil.addGroup();

		_groups.add(group);

		Layout layout = LayoutTestUtil.addTypePortletLayout(group);

		LayoutSetBranch layoutSetBranch = _addLayoutSetBranch(group);

		LayoutRevision layoutRevision =
			_layoutRevisionLocalService.fetchLatestLayoutRevision(
				layoutSetBranch.getLayoutSetBranchId(), layout.getPlid());

		AssertUtils.assertFailure(
			NoSuchLayoutRevisionException.class,
			StringBundler.concat(
				"No layout revision exists with the primary key ",
				layoutRevision.getLayoutRevisionId(), " for layout ",
				_layout.getPlid()),
			() -> _addLayoutRevision(
				_layout, layoutRevision.getLayoutRevisionId(),
				LayoutConstants.DEFAULT_PLID));
	}

	private void _testAddLayoutRevisionWithLayoutFromAnotherGroup()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		_groups.add(group);

		Layout layout = LayoutTestUtil.addTypePortletLayout(group);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", TestPropsValues.getUserId(),
				" must have UPDATE permission for ", Layout.class.getName(),
				" ", layout.getPlid()),
			() -> _addLayoutRevision(
				layout,
				LayoutRevisionConstants.DEFAULT_PARENT_LAYOUT_REVISION_ID,
				LayoutConstants.DEFAULT_PLID));
	}

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private final List<Group> _groups = new ArrayList<>();

	private Layout _layout;

	@Inject
	private LayoutBranchLocalService _layoutBranchLocalService;

	@Inject
	private LayoutRevisionLocalService _layoutRevisionLocalService;

	@Inject
	private LayoutRevisionService _layoutRevisionService;

	private LayoutSetBranch _layoutSetBranch;

	@Inject
	private LayoutSetBranchLocalService _layoutSetBranchLocalService;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

}