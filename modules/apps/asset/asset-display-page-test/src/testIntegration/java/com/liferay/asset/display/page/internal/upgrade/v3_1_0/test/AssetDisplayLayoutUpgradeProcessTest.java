/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.display.page.internal.upgrade.v3_1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.asset.display.page.model.AssetDisplayPageEntry;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.change.tracking.test.util.BaseCTUpgradeProcessTestCase;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.util.UUID;

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
public class AssetDisplayLayoutUpgradeProcessTest
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

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());
	}

	@Test
	public void testUpgradeTypeDefaultAssetDisplayPage() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_addLayoutPageTemplateEntry(
			journalArticle.getDDMStructureId(),
			journalArticle.getDDMStructureKey());

		_addAssetDisplayPageEntry(
			journalArticle.getResourcePrimKey(), 0,
			AssetDisplayPageConstants.TYPE_DEFAULT, 0);

		_runUpgrade();

		_assertAssetDisplayPageEntry(journalArticle.getResourcePrimKey(), 0);
	}

	@Test
	public void testUpgradeTypeNoneAssetDisplayPage() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_addLayoutPageTemplateEntry(
			journalArticle.getDDMStructureId(),
			journalArticle.getDDMStructureKey());

		_addAssetDisplayPageEntry(
			journalArticle.getResourcePrimKey(), 0,
			AssetDisplayPageConstants.TYPE_NONE, 0);

		_runUpgrade();

		_assertAssetDisplayPageEntry(journalArticle.getResourcePrimKey(), 0);
	}

	@Test
	public void testUpgradeTypeSpecificAssetDisplayPage() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_addLayoutPageTemplateEntry(
				journalArticle.getDDMStructureId(),
				journalArticle.getDDMStructureKey());

		_addAssetDisplayPageEntry(
			journalArticle.getResourcePrimKey(),
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			AssetDisplayPageConstants.TYPE_SPECIFIC,
			layoutPageTemplateEntry.getPlid());

		_runUpgrade();

		_assertAssetDisplayPageEntry(
			journalArticle.getResourcePrimKey(),
			layoutPageTemplateEntry.getPlid());
	}

	@Test
	public void testUpgradeTypeSpecificAssetDisplayPageWithWrongPlid()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_addLayoutPageTemplateEntry(
				journalArticle.getDDMStructureId(),
				journalArticle.getDDMStructureKey());

		_addAssetDisplayPageEntry(
			journalArticle.getResourcePrimKey(),
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			AssetDisplayPageConstants.TYPE_SPECIFIC, 0);

		_runUpgrade();

		_assertAssetDisplayPageEntry(
			journalArticle.getResourcePrimKey(),
			layoutPageTemplateEntry.getPlid());
	}

	@Override
	protected CTModel<?> addCTModel() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_addLayoutPageTemplateEntry(
				journalArticle.getDDMStructureId(),
				journalArticle.getDDMStructureKey());

		return _addAssetDisplayPageEntry(
			journalArticle.getResourcePrimKey(),
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			AssetDisplayPageConstants.TYPE_SPECIFIC,
			layoutPageTemplateEntry.getPlid());
	}

	@Override
	protected CTService<?> getCTService() {
		return _assetDisplayPageEntryLocalService;
	}

	@Override
	protected void runUpgrade() throws Exception {
		_runUpgrade();
	}

	@Override
	protected CTModel<?> updateCTModel(CTModel<?> ctModel) throws Exception {
		AssetDisplayPageEntry assetDisplayPageEntry =
			(AssetDisplayPageEntry)ctModel;

		assetDisplayPageEntry.setLayoutPageTemplateEntryId(0);
		assetDisplayPageEntry.setType(AssetDisplayPageConstants.TYPE_NONE);

		return _assetDisplayPageEntryLocalService.updateAssetDisplayPageEntry(
			assetDisplayPageEntry);
	}

	private AssetDisplayPageEntry _addAssetDisplayPageEntry(
		long classPK, long layoutPageTemplateEntryId, int type, long plid) {

		AssetDisplayPageEntry assetDisplayPageEntry =
			_assetDisplayPageEntryLocalService.createAssetDisplayPageEntry(
				_counterLocalService.increment());

		assetDisplayPageEntry.setUuid(String.valueOf(UUID.randomUUID()));
		assetDisplayPageEntry.setGroupId(_group.getGroupId());
		assetDisplayPageEntry.setCompanyId(_group.getCompanyId());
		assetDisplayPageEntry.setClassNameId(
			_portal.getClassNameId(JournalArticle.class));
		assetDisplayPageEntry.setClassPK(classPK);
		assetDisplayPageEntry.setLayoutPageTemplateEntryId(
			layoutPageTemplateEntryId);
		assetDisplayPageEntry.setType(type);
		assetDisplayPageEntry.setPlid(plid);

		return _assetDisplayPageEntryLocalService.updateAssetDisplayPageEntry(
			assetDisplayPageEntry);
	}

	private LayoutPageTemplateEntry _addLayoutPageTemplateEntry(
			long ddmStructureId, String ddmStructureKey)
		throws Exception {

		return DisplayPageTemplateTestUtil.addDisplayPageTemplate(
			_group.getGroupId(),
			_portal.getClassNameId(JournalArticle.class.getName()),
			ddmStructureId, ddmStructureKey, false,
			WorkflowConstants.STATUS_APPROVED);
	}

	private void _assertAssetDisplayPageEntry(long classPK, long plid) {
		AssetDisplayPageEntry assetDisplayPageEntry =
			_assetDisplayPageEntryLocalService.fetchAssetDisplayPageEntry(
				_group.getGroupId(),
				_portal.getClassNameId(JournalArticle.class), classPK);

		Assert.assertNotNull(assetDisplayPageEntry);
		Assert.assertEquals(plid, assetDisplayPageEntry.getPlid());
	}

	private void _runUpgrade() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.OFF)) {

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator, _CLASS_NAME);

			upgradeProcess.upgrade();

			_multiVMPool.clear();
		}
	}

	private static final String _CLASS_NAME =
		"com.liferay.asset.display.page.internal.upgrade.v3_1_0." +
			"AssetDisplayLayoutUpgradeProcess";

	@Inject(
		filter = "(&(component.name=com.liferay.asset.display.page.internal.upgrade.registry.AssetDisplayPageServiceUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private AssetDisplayPageEntryLocalService
		_assetDisplayPageEntryLocalService;

	@Inject
	private CounterLocalService _counterLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private Portal _portal;

	private ServiceContext _serviceContext;

}