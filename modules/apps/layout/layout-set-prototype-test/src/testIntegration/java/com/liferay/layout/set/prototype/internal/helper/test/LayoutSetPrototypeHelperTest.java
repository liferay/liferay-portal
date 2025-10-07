/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.set.prototype.internal.helper.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.staging.MergeLayoutPrototypesThreadLocal;
import com.liferay.layout.set.prototype.helper.LayoutSetPrototypeHelper;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.sites.kernel.util.Sites;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Vendel Toreki
 */
@RunWith(Arquillian.class)
public class LayoutSetPrototypeHelperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext());

		UserTestUtil.setUser(TestPropsValues.getUser());

		_group = GroupTestUtil.addGroup();

		_layoutSetPrototype = LayoutTestUtil.addLayoutSetPrototype(
			RandomTestUtil.randomString());

		_layoutSetPrototypeGroup = _layoutSetPrototype.getGroup();

		_prototypeLayout = LayoutTestUtil.addTypePortletLayout(
			_layoutSetPrototypeGroup, true);

		setLinkEnabled();

		_siteLayout = LayoutLocalServiceUtil.getFriendlyURLLayout(
			_group.getGroupId(), false, _prototypeLayout.getFriendlyURL());
	}

	@Test
	public void testDuplicatedFriendlyURLsInLayoutSet() throws Exception {
		List<Layout> layouts = new ArrayList<>();

		for (String name : RandomTestUtil.randomStrings(3)) {
			layouts.add(
				LayoutTestUtil.addTypePortletLayout(
					_group.getGroupId(), name, false));

			LayoutTestUtil.addTypePortletLayout(
				_group.getGroupId(), RandomTestUtil.randomString(5), false);
			LayoutTestUtil.addTypePortletLayout(
				_layoutSetPrototypeGroup.getGroupId(), name, true);
			LayoutTestUtil.addTypePortletLayout(
				_layoutSetPrototypeGroup.getGroupId(),
				RandomTestUtil.randomString(5), true);
		}

		List<Long> duplicatedFriendlyURLPlids =
			_layoutSetPrototypeHelper.getDuplicatedFriendlyURLPlids(
				_group.getPublicLayoutSet());

		Assert.assertEquals(
			duplicatedFriendlyURLPlids.toString(), 3,
			duplicatedFriendlyURLPlids.size());

		for (Layout layout : layouts) {
			Assert.assertTrue(
				duplicatedFriendlyURLPlids.contains(layout.getPlid()));
		}
	}

	@Test
	public void testDuplicatedFriendlyURLsInLayoutSetPrototype()
		throws Exception {

		List<Layout> layouts = new ArrayList<>();

		for (String name : RandomTestUtil.randomStrings(3)) {
			LayoutTestUtil.addTypePortletLayout(
				_group.getGroupId(), name, false);
			LayoutTestUtil.addTypePortletLayout(
				_group.getGroupId(), RandomTestUtil.randomString(5), false);

			layouts.add(
				LayoutTestUtil.addTypePortletLayout(
					_layoutSetPrototypeGroup.getGroupId(), name, true));

			LayoutTestUtil.addTypePortletLayout(
				_layoutSetPrototypeGroup.getGroupId(),
				RandomTestUtil.randomString(5), true);
		}

		List<Long> duplicatedFriendlyURLPlids =
			_layoutSetPrototypeHelper.getDuplicatedFriendlyURLPlids(
				_layoutSetPrototype);

		Assert.assertEquals(
			duplicatedFriendlyURLPlids.toString(), 3,
			duplicatedFriendlyURLPlids.size());

		for (Layout layout : layouts) {
			Assert.assertTrue(
				duplicatedFriendlyURLPlids.contains(layout.getPlid()));
		}
	}

	@Test
	public void testLayoutSetPrototypeLayoutFriendlyURLConflictDetectionBeforeChange()
		throws Exception {

		LayoutTestUtil.addTypePortletLayout(_group.getGroupId(), "test", false);

		Layout layoutSetPrototypeLayout = LayoutTestUtil.addTypePortletLayout(
			_layoutSetPrototypeGroup.getGroupId(), "testNoConflict", true);

		boolean hasConflicts =
			_layoutSetPrototypeHelper.hasDuplicatedFriendlyURLs(
				layoutSetPrototypeLayout.getExternalReferenceCode(),
				layoutSetPrototypeLayout.getGroupId(),
				layoutSetPrototypeLayout.isPrivateLayout(), "/test");

		Assert.assertTrue(hasConflicts);
	}

	@Test
	public void testLayoutSetPrototypeLayoutFriendlyURLConflictDetectionBeforeCreate()
		throws Exception {

		LayoutTestUtil.addTypePortletLayout(_group.getGroupId(), "test", false);

		boolean hasConflicts =
			_layoutSetPrototypeHelper.hasDuplicatedFriendlyURLs(
				null, _layoutSetPrototypeGroup.getGroupId(), true, "/test");

		Assert.assertTrue(hasConflicts);
	}

	@Test
	public void testLayoutSetPrototypeLayoutFriendlyURLConflictDetectionBeforePropagate()
		throws Exception {

		Layout siteLayout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), "test", false);

		Layout layoutSetPrototypeLayout = LayoutTestUtil.addTypePortletLayout(
			_layoutSetPrototypeGroup.getGroupId(), "test", true);

		List<Layout> conflictLayouts =
			_layoutSetPrototypeHelper.getDuplicatedFriendlyURLLayouts(
				layoutSetPrototypeLayout);

		Assert.assertEquals(
			conflictLayouts.toString(), 1, conflictLayouts.size());

		Layout conflictLayout = conflictLayouts.get(0);

		Assert.assertEquals(conflictLayout.getPlid(), siteLayout.getPlid());
	}

	@Test
	public void testLayoutSetPrototypeLayoutFriendlyURLConflictDetectionIgnorePropagated()
		throws Exception {

		boolean hasConflicts =
			_layoutSetPrototypeHelper.hasDuplicatedFriendlyURLs(
				_prototypeLayout.getExternalReferenceCode(),
				_prototypeLayout.getGroupId(),
				_prototypeLayout.isPrivateLayout(),
				_prototypeLayout.getFriendlyURL());

		Assert.assertFalse(hasConflicts);
	}

	@Test
	public void testSiteLayoutFriendlyURLConflictDetectionBeforeChange()
		throws Exception {

		Layout siteLayout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), "testNoConflict", false);

		LayoutTestUtil.addTypePortletLayout(
			_layoutSetPrototypeGroup.getGroupId(), "test", true);

		boolean hasConflicts =
			_layoutSetPrototypeHelper.hasDuplicatedFriendlyURLs(
				siteLayout.getExternalReferenceCode(), siteLayout.getGroupId(),
				siteLayout.isPrivateLayout(), "/test");

		Assert.assertTrue(hasConflicts);
	}

	@Test
	public void testSiteLayoutFriendlyURLConflictDetectionBeforeCreate()
		throws Exception {

		LayoutTestUtil.addTypePortletLayout(
			_layoutSetPrototypeGroup.getGroupId(), "test", true);

		boolean hasConflicts =
			_layoutSetPrototypeHelper.hasDuplicatedFriendlyURLs(
				null, _group.getGroupId(), false, "/test");

		Assert.assertTrue(hasConflicts);
	}

	@Test
	public void testSiteLayoutFriendlyURLConflictDetectionBeforePropagate()
		throws Exception {

		Layout siteLayout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), "test", false);

		Layout layoutSetPrototypeLayout = LayoutTestUtil.addTypePortletLayout(
			_layoutSetPrototypeGroup.getGroupId(), "test", true);

		List<Layout> conflicts =
			_layoutSetPrototypeHelper.getDuplicatedFriendlyURLLayouts(
				siteLayout);

		Assert.assertEquals(conflicts.toString(), 1, conflicts.size());

		Layout conflictLayout = conflicts.get(0);

		Assert.assertEquals(
			conflictLayout.getPlid(), layoutSetPrototypeLayout.getPlid());
	}

	@Test
	public void testSiteLayoutFriendlyURLConflictDetectionIgnorePropagated()
		throws Exception {

		boolean hasConflicts =
			_layoutSetPrototypeHelper.hasDuplicatedFriendlyURLs(
				_siteLayout.getExternalReferenceCode(),
				_siteLayout.getGroupId(), _siteLayout.isPrivateLayout(),
				_siteLayout.getFriendlyURL());

		Assert.assertFalse(hasConflicts);
	}

	protected void setLinkEnabled() throws Exception {
		MergeLayoutPrototypesThreadLocal.clearMergeComplete();

		_sites.updateLayoutSetPrototypesLinks(
			_group, _layoutSetPrototype.getLayoutSetPrototypeId(), 0, true,
			false);
	}

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private LayoutSetPrototype _layoutSetPrototype;

	@DeleteAfterTestRun
	private Group _layoutSetPrototypeGroup;

	@Inject
	private LayoutSetPrototypeHelper _layoutSetPrototypeHelper;

	private Layout _prototypeLayout;
	private Layout _siteLayout;

	@Inject
	private Sites _sites;

}