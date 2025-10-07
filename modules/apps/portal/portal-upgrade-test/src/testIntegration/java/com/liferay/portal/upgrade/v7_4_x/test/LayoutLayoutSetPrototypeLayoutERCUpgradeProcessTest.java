/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.test.util.BaseCTUpgradeProcessTestCase;
import com.liferay.exportimport.kernel.staging.MergeLayoutPrototypesThreadLocal;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.v7_4_x.LayoutLayoutSetPrototypeLayoutERCUpgradeProcess;
import com.liferay.sites.kernel.util.Sites;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Moral
 */
@RunWith(Arquillian.class)
public class LayoutLayoutSetPrototypeLayoutERCUpgradeProcessTest
	extends BaseCTUpgradeProcessTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layoutSetPrototype = LayoutTestUtil.addLayoutSetPrototype(
			RandomTestUtil.randomString());

		_sites.updateLayoutSetPrototypesLinks(
			_group, _layoutSetPrototype.getLayoutSetPrototypeId(), 0, true,
			true);
	}

	@Test
	@TestInfo("LPD-60063")
	public void testUpgrade() throws Exception {
		Layout layoutSetPrototypeLayout = _addLayout();

		_propagateChanges();

		Layout layout = _layoutLocalService.getLayoutByFriendlyURL(
			_group.getGroupId(), false,
			layoutSetPrototypeLayout.getFriendlyURL());

		layoutSetPrototypeLayout.setExternalReferenceCode(
			RandomTestUtil.randomString());

		layoutSetPrototypeLayout = _layoutLocalService.updateLayout(
			layoutSetPrototypeLayout);

		runUpgrade();

		layout = _layoutLocalService.fetchLayout(layout.getPlid());

		Assert.assertEquals(
			layoutSetPrototypeLayout.getExternalReferenceCode(),
			layout.getLayoutSetPrototypeLayoutERC());
	}

	@Override
	protected CTModel<?> addCTModel() throws Exception {
		Layout layout = _addLayout();

		_propagateChanges();

		return _layoutLocalService.getLayoutByFriendlyURL(
			_group.getGroupId(), false, layout.getFriendlyURL());
	}

	@Override
	protected void deleteCTModel(long primaryKey) throws Exception {
		Layout layout = _layoutLocalService.getLayout(primaryKey);

		Layout layoutSetPrototypeLayout = layout.getLayoutSetPrototypeLayout();

		_layoutLocalService.deleteLayout(layout);

		if (layoutSetPrototypeLayout != null) {
			_layoutLocalService.deleteLayout(layoutSetPrototypeLayout);
		}
	}

	@Override
	protected CTService<?> getCTService() {
		return _layoutLocalService;
	}

	@Override
	protected void runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess =
			new LayoutLayoutSetPrototypeLayoutERCUpgradeProcess();

		upgradeProcess.upgrade();

		_entityCache.clearCache();
		_multiVMPool.clear();
	}

	@Override
	protected CTModel<?> updateCTModel(CTModel<?> ctModel) throws Exception {
		Layout layout = (Layout)ctModel;

		Layout layoutSetPrototypeLayout = layout.getLayoutSetPrototypeLayout();

		layoutSetPrototypeLayout.setPriority(RandomTestUtil.randomInt());

		layoutSetPrototypeLayout = _layoutLocalService.updateLayout(
			layoutSetPrototypeLayout);

		_propagateChanges();

		return _layoutLocalService.getLayoutByFriendlyURL(
			_group.getGroupId(), false,
			layoutSetPrototypeLayout.getFriendlyURL());
	}

	private Layout _addLayout() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(
			_layoutSetPrototype.getGroup(), true, false);

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		return _layoutLocalService.getLayout(layout.getPlid());
	}

	private void _propagateChanges() throws Exception {
		_layoutSetPrototype =
			_layoutSetPrototypeLocalService.getLayoutSetPrototype(
				_layoutSetPrototype.getLayoutSetPrototypeId());

		_layoutSetPrototype.setModifiedDate(new Date());

		_layoutSetPrototype =
			_layoutSetPrototypeLocalService.updateLayoutSetPrototype(
				_layoutSetPrototype);

		MergeLayoutPrototypesThreadLocal.clearMergeComplete();
		MergeLayoutPrototypesThreadLocal.setSkipMerge(false);

		LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
			_group.getGroupId(), false);

		_sites.mergeLayoutSetPrototypeLayouts(_group, layoutSet);
	}

	@Inject
	private EntityCache _entityCache;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	@DeleteAfterTestRun
	private LayoutSetPrototype _layoutSetPrototype;

	@Inject
	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private Sites _sites;

}