/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.display.page.internal.upgrade.v3_0_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.asset.display.page.model.AssetDisplayPageEntry;
import com.liferay.asset.display.page.service.AssetDisplayPageEntryLocalService;
import com.liferay.change.tracking.configuration.CTSettingsConfiguration;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTCollectionService;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
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
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class UpgradeAssetDisplayPageEntryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_classNameId = _portal.getClassNameId(FileEntry.class.getName());

		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());

		_layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(), _classNameId, 0, null, false,
				WorkflowConstants.STATUS_APPROVED);

		DisplayPageTemplateTestUtil.addDisplayPageTemplate(
			_group.getGroupId(), _classNameId, 0, null, true,
			WorkflowConstants.STATUS_APPROVED);
	}

	@Test
	public void testUpgradeProcess() throws Exception {
		DLFolder dlFolder = DLTestUtil.addDLFolder(_group.getGroupId());

		DLFileEntry dlFileEntry1 = DLTestUtil.addDLFileEntry(
			dlFolder.getFolderId());

		_addAssetDisplayPageEntry(
			dlFileEntry1.getFileEntryId(), 0,
			AssetDisplayPageConstants.TYPE_DEFAULT, 0);

		DLFileEntry dlFileEntry2 = DLTestUtil.addDLFileEntry(
			dlFolder.getFolderId());

		_addAssetDisplayPageEntry(
			dlFileEntry2.getFileEntryId(),
			_layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			AssetDisplayPageConstants.TYPE_SPECIFIC,
			_layoutPageTemplateEntry.getPlid());

		DLFileEntry dlFileEntry3 = DLTestUtil.addDLFileEntry(
			dlFolder.getFolderId());

		_assertNoAssetDisplayPageEntry(dlFileEntry3.getFileEntryId());

		_runUpgrade();

		_assertNoAssetDisplayPageEntry(dlFileEntry1.getFileEntryId());
		_assertAssetDisplayPageEntry(
			dlFileEntry2.getFileEntryId(), _layoutPageTemplateEntry.getPlid(),
			AssetDisplayPageConstants.TYPE_SPECIFIC);
		_assertAssetDisplayPageEntry(
			dlFileEntry3.getFileEntryId(), 0,
			AssetDisplayPageConstants.TYPE_NONE);
	}

	@Test
	public void testUpgradeProcessWithCTCollection() throws Exception {
		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						CTSettingsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", true
						).build())) {

			DLFolder dlFolder = DLTestUtil.addDLFolder(_group.getGroupId());

			DLFileEntry dlFileEntry1 = DLTestUtil.addDLFileEntry(
				dlFolder.getFolderId());

			_addAssetDisplayPageEntry(
				dlFileEntry1.getFileEntryId(), 0,
				AssetDisplayPageConstants.TYPE_DEFAULT, 0);

			DLFileEntry dlFileEntry2 = DLTestUtil.addDLFileEntry(
				dlFolder.getFolderId());

			_addAssetDisplayPageEntry(
				dlFileEntry2.getFileEntryId(),
				_layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
				AssetDisplayPageConstants.TYPE_SPECIFIC,
				_layoutPageTemplateEntry.getPlid());

			DLFileEntry dlFileEntry3 = DLTestUtil.addDLFileEntry(
				dlFolder.getFolderId());

			CTCollection ctCollection =
				_ctCollectionLocalService.addCTCollection(
					null, TestPropsValues.getCompanyId(),
					TestPropsValues.getUserId(), 0,
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString());

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						ctCollection.getCtCollectionId())) {

				_updateDLFileEntryTitle(dlFileEntry1.getFileEntryId());
				_updateDLFileEntryTitle(dlFileEntry2.getFileEntryId());
				_updateDLFileEntryTitle(dlFileEntry3.getFileEntryId());
			}

			_assertAssetDisplayPageEntry(
				dlFileEntry1.getFileEntryId(), 0,
				AssetDisplayPageConstants.TYPE_DEFAULT);
			_assertAssetDisplayPageEntry(
				dlFileEntry2.getFileEntryId(),
				_layoutPageTemplateEntry.getPlid(),
				AssetDisplayPageConstants.TYPE_SPECIFIC);
			_assertNoAssetDisplayPageEntry(dlFileEntry3.getFileEntryId());

			_ctCollectionService.publishCTCollection(
				TestPropsValues.getUserId(), ctCollection.getCtCollectionId());

			_runUpgrade();

			_assertNoAssetDisplayPageEntry(dlFileEntry1.getFileEntryId());
			_assertAssetDisplayPageEntry(
				dlFileEntry2.getFileEntryId(),
				_layoutPageTemplateEntry.getPlid(),
				AssetDisplayPageConstants.TYPE_SPECIFIC);
			_assertAssetDisplayPageEntry(
				dlFileEntry3.getFileEntryId(), 0,
				AssetDisplayPageConstants.TYPE_NONE);
		}
	}

	private void _addAssetDisplayPageEntry(
		long classPK, long layoutPageTemplateEntryId, int type, long plid) {

		AssetDisplayPageEntry assetDisplayPageEntry =
			_assetDisplayPageEntryLocalService.createAssetDisplayPageEntry(
				_counterLocalService.increment());

		assetDisplayPageEntry.setUuid(String.valueOf(UUID.randomUUID()));
		assetDisplayPageEntry.setGroupId(_group.getGroupId());
		assetDisplayPageEntry.setCompanyId(_group.getCompanyId());
		assetDisplayPageEntry.setClassNameId(_classNameId);
		assetDisplayPageEntry.setClassPK(classPK);
		assetDisplayPageEntry.setLayoutPageTemplateEntryId(
			layoutPageTemplateEntryId);
		assetDisplayPageEntry.setType(type);
		assetDisplayPageEntry.setPlid(plid);

		_assetDisplayPageEntryLocalService.updateAssetDisplayPageEntry(
			assetDisplayPageEntry);

		_assertAssetDisplayPageEntry(classPK, plid, type);
	}

	private void _assertAssetDisplayPageEntry(
		long classPK, long plid, int type) {

		AssetDisplayPageEntry assetDisplayPageEntry =
			_assetDisplayPageEntryLocalService.fetchAssetDisplayPageEntry(
				_group.getGroupId(), _classNameId, classPK);

		Assert.assertNotNull(assetDisplayPageEntry);
		Assert.assertEquals(plid, assetDisplayPageEntry.getPlid());
		Assert.assertEquals(type, assetDisplayPageEntry.getType());
	}

	private void _assertNoAssetDisplayPageEntry(long classPK) {
		AssetDisplayPageEntry assetDisplayPageEntry =
			_assetDisplayPageEntryLocalService.fetchAssetDisplayPageEntry(
				_group.getGroupId(), _classNameId, classPK);

		Assert.assertNull(assetDisplayPageEntry);
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

	private void _updateDLFileEntryTitle(long fileEntryId)
		throws PortalException {

		DLFileEntry dlFileEntry = _dlFileEntryLocalService.getDLFileEntry(
			fileEntryId);

		dlFileEntry.setTitle(RandomTestUtil.randomString());

		_dlFileEntryLocalService.updateDLFileEntry(dlFileEntry);
	}

	private static final String _CLASS_NAME =
		"com.liferay.asset.display.page.internal.upgrade.v3_0_0." +
			"UpgradeAssetDisplayPageEntry";

	@Inject(
		filter = "(&(component.name=com.liferay.asset.display.page.internal.upgrade.registry.AssetDisplayPageServiceUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private AssetDisplayPageEntryLocalService
		_assetDisplayPageEntryLocalService;

	private long _classNameId;

	@Inject
	private CounterLocalService _counterLocalService;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private CTCollectionService _ctCollectionService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private LayoutPageTemplateEntry _layoutPageTemplateEntry;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private Portal _portal;

	private ServiceContext _serviceContext;

}