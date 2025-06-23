/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTProcessLocalService;
import com.liferay.exportimport.kernel.staging.MergeLayoutPrototypesThreadLocal;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.sites.kernel.util.Sites;

import java.util.Date;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pei-Jung Lan
 */
@RunWith(Arquillian.class)
public class LayoutSetPrototypePropagationCTTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testPropagation() throws Exception {
		_testPropagation(false);
		_testPropagation(true);
	}

	private void _assertLayoutPropagation(
			long ctCollectionId, long groupId, boolean propagationComplete,
			Layout layoutSetPrototypeLayout,
			LayoutSet layoutSetPrototypeLayoutSet)
		throws Exception {

		Layout layout = _layoutLocalService.getLayoutByFriendlyURL(
			groupId, false, layoutSetPrototypeLayout.getFriendlyURL());

		Assert.assertEquals(ctCollectionId, layout.getCtCollectionId());

		LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
			groupId, false);

		Assert.assertEquals(ctCollectionId, layoutSet.getCtCollectionId());

		if (propagationComplete) {
			Assert.assertEquals(
				layoutSetPrototypeLayout.getPriority(), layout.getPriority());
			Assert.assertEquals(
				layoutSetPrototypeLayoutSet.getFaviconFileEntryId(),
				layoutSet.getFaviconFileEntryId());
		}
		else {
			Assert.assertNotEquals(
				layoutSetPrototypeLayout.getPriority(), layout.getPriority());
			Assert.assertNotEquals(
				layoutSetPrototypeLayoutSet.getFaviconFileEntryId(),
				layoutSet.getFaviconFileEntryId());
		}
	}

	private void _propagateChanges(Group group) throws Exception {
		MergeLayoutPrototypesThreadLocal.clearMergeComplete();

		LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
			group.getGroupId(), false);

		MergeLayoutPrototypesThreadLocal.setSkipMerge(false);

		_sites.mergeLayoutSetPrototypeLayouts(group, layoutSet);

		Thread.sleep(2000);

		LayoutSetPrototype layoutSetPrototype =
			_layoutSetPrototypeLocalService.
				getLayoutSetPrototypeByUuidAndCompanyId(
					layoutSet.getLayoutSetPrototypeUuid(),
					layoutSet.getCompanyId());

		LayoutSet layoutSetPrototypeLayoutSet =
			layoutSetPrototype.getLayoutSet();

		UnicodeProperties layoutSetPrototypeSettingsUnicodeProperties =
			layoutSetPrototypeLayoutSet.getSettingsProperties();

		int mergeFailCount = GetterUtil.getInteger(
			layoutSetPrototypeSettingsUnicodeProperties.getProperty(
				Sites.MERGE_FAIL_COUNT));

		Assert.assertEquals(0, mergeFailCount);
	}

	private void _testPropagation(boolean propagateInPublication)
		throws Exception {

		LayoutSetPrototype layoutSetPrototype =
			LayoutTestUtil.addLayoutSetPrototype(RandomTestUtil.randomString());

		Layout layoutSetPrototypeLayout =
			_layoutLocalService.fetchDefaultLayout(
				layoutSetPrototype.getGroupId(), true);

		LayoutSet layoutSetPrototypeLayoutSet =
			_layoutSetLocalService.getLayoutSet(
				layoutSetPrototype.getGroupId(), false);

		Group group = GroupTestUtil.addGroup();

		_sites.updateLayoutSetPrototypesLinks(
			group, layoutSetPrototype.getLayoutSetPrototypeId(), 0, true, true);

		Thread.sleep(2000);

		LayoutSet groupLayoutSet = _layoutSetLocalService.getLayoutSet(
			group.getGroupId(), false);

		groupLayoutSet.setFaviconFileEntryId(RandomTestUtil.randomLong());

		_layoutSetLocalService.updateLayoutSet(groupLayoutSet);

		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), null);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			layoutSetPrototypeLayout.setPriority(RandomTestUtil.randomInt());

			layoutSetPrototypeLayout = _layoutLocalService.updateLayout(
				layoutSetPrototypeLayout);

			layoutSetPrototype =
				_layoutSetPrototypeLocalService.getLayoutSetPrototype(
					layoutSetPrototype.getLayoutSetPrototypeId());

			layoutSetPrototype.setModifiedDate(new Date());

			_layoutSetPrototypeLocalService.updateLayoutSetPrototype(
				layoutSetPrototype);

			if (propagateInPublication) {
				_propagateChanges(group);

				_assertLayoutPropagation(
					ctCollection.getCtCollectionId(), group.getGroupId(), true,
					layoutSetPrototypeLayout, layoutSetPrototypeLayoutSet);
			}
			else {
				_assertLayoutPropagation(
					CTCollectionThreadLocal.CT_COLLECTION_ID_PRODUCTION,
					group.getGroupId(), false, layoutSetPrototypeLayout,
					layoutSetPrototypeLayoutSet);
			}
		}

		_propagateChanges(group);

		_assertLayoutPropagation(
			CTCollectionThreadLocal.CT_COLLECTION_ID_PRODUCTION,
			group.getGroupId(), false, layoutSetPrototypeLayout,
			layoutSetPrototypeLayoutSet);

		_ctProcessLocalService.addCTProcess(
			TestPropsValues.getUserId(), ctCollection.getCtCollectionId());

		_propagateChanges(group);

		_assertLayoutPropagation(
			CTCollectionThreadLocal.CT_COLLECTION_ID_PRODUCTION,
			group.getGroupId(), true, layoutSetPrototypeLayout,
			layoutSetPrototypeLayoutSet);
	}

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private CTProcessLocalService _ctProcessLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	@Inject
	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

	@Inject
	private Sites _sites;

}