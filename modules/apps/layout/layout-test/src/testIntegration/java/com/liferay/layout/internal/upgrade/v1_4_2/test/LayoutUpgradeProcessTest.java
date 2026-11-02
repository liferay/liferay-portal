/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.upgrade.v1_4_2.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutFriendlyURL;
import com.liferay.portal.kernel.service.LayoutFriendlyURLLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rubén Pulido
 */
@RunWith(Arquillian.class)
public class LayoutUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testUpgrade() throws Exception {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, null,
				RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0,
				WorkflowConstants.STATUS_DRAFT, _serviceContext);

		Layout draftLayout = _layoutLocalService.fetchDraftLayout(
			layoutPageTemplateEntry.getPlid());

		Layout layout = _layoutLocalService.getLayout(draftLayout.getClassPK());

		draftLayout.setPrivateLayout(false);

		draftLayout = _layoutLocalService.updateLayout(draftLayout);

		layout.setPrivateLayout(false);

		layout = _layoutLocalService.updateLayout(layout);

		LayoutFriendlyURL draftLayoutFriendlyURL =
			_layoutFriendlyURLLocalService.fetchLayoutFriendlyURL(
				draftLayout.getPlid(), draftLayout.getDefaultLanguageId(),
				true);

		draftLayoutFriendlyURL.setPrivateLayout(false);

		_layoutFriendlyURLLocalService.updateLayoutFriendlyURL(
			draftLayoutFriendlyURL);

		LayoutFriendlyURL layoutFriendlyURL =
			_layoutFriendlyURLLocalService.fetchLayoutFriendlyURL(
				layout.getPlid(), layout.getDefaultLanguageId(), true);

		layoutFriendlyURL.setPrivateLayout(false);

		_layoutFriendlyURLLocalService.updateLayoutFriendlyURL(
			layoutFriendlyURL);

		_runUpgrade();

		Layout updatedDraftLayout = _layoutLocalService.fetchLayout(
			draftLayout.getPlid());

		Assert.assertTrue(updatedDraftLayout.isPrivateLayout());

		Layout updatedLayout = _layoutLocalService.fetchLayout(
			layout.getPlid());

		Assert.assertTrue(updatedLayout.isPrivateLayout());

		LayoutFriendlyURL updatedDraftLayoutFriendlyURL =
			_layoutFriendlyURLLocalService.fetchLayoutFriendlyURL(
				updatedDraftLayout.getPlid(),
				updatedDraftLayout.getDefaultLanguageId(), true);

		Assert.assertTrue(updatedDraftLayoutFriendlyURL.isPrivateLayout());

		LayoutFriendlyURL updatedLayoutFriendlyURL =
			_layoutFriendlyURLLocalService.fetchLayoutFriendlyURL(
				updatedLayout.getPlid(), updatedLayout.getDefaultLanguageId(),
				true);

		Assert.assertTrue(updatedLayoutFriendlyURL.isPrivateLayout());
	}

	@Test
	public void testUpgradeWhenPrivateLayoutAlreadyExists() throws Exception {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, null,
				RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0,
				WorkflowConstants.STATUS_DRAFT, _serviceContext);

		Layout draftLayout = _layoutLocalService.fetchDraftLayout(
			layoutPageTemplateEntry.getPlid());

		Layout layout = _layoutLocalService.getLayout(draftLayout.getClassPK());

		Assert.assertTrue(layout.isPrivateLayout());

		LayoutPageTemplateEntry duplicateLayoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, null,
				RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0,
				WorkflowConstants.STATUS_DRAFT, _serviceContext);

		Layout duplicateDraftLayout = _layoutLocalService.fetchDraftLayout(
			duplicateLayoutPageTemplateEntry.getPlid());

		Layout duplicateLayout = _layoutLocalService.getLayout(
			duplicateDraftLayout.getClassPK());

		duplicateLayout.setUuid(layout.getUuid());
		duplicateLayout.setPrivateLayout(false);
		duplicateLayout.setFriendlyURL(layout.getFriendlyURL());

		duplicateLayout = _layoutLocalService.updateLayout(duplicateLayout);

		LayoutFriendlyURL layoutFriendlyURL =
			_layoutFriendlyURLLocalService.fetchLayoutFriendlyURL(
				layout.getPlid(), layout.getDefaultLanguageId(), true);

		LayoutFriendlyURL duplicateLayoutFriendlyURL =
			_layoutFriendlyURLLocalService.fetchLayoutFriendlyURL(
				duplicateLayout.getPlid(),
				duplicateDraftLayout.getDefaultLanguageId(), true);

		duplicateLayoutFriendlyURL.setPrivateLayout(false);
		duplicateLayoutFriendlyURL.setFriendlyURL(
			layoutFriendlyURL.getFriendlyURL());

		_layoutFriendlyURLLocalService.updateLayoutFriendlyURL(
			duplicateLayoutFriendlyURL);

		_runUpgrade();

		Layout updatedLayout = _layoutLocalService.getLayout(
			duplicateLayout.getPlid());

		Assert.assertTrue(updatedLayout.isPrivateLayout());

		LayoutFriendlyURL updatedLayoutFriendlyURL =
			_layoutFriendlyURLLocalService.fetchLayoutFriendlyURL(
				updatedLayout.getPlid(), updatedLayout.getDefaultLanguageId(),
				true);

		Assert.assertTrue(updatedLayoutFriendlyURL.isPrivateLayout());
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();

		_multiVMPool.clear();
	}

	private static final String _CLASS_NAME =
		"com.liferay.layout.internal.upgrade.v1_4_2.LayoutUpgradeProcess";

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutFriendlyURLLocalService _layoutFriendlyURLLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private MultiVMPool _multiVMPool;

	private ServiceContext _serviceContext;

	@Inject(
		filter = "(&(component.name=com.liferay.layout.internal.upgrade.registry.LayoutServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}