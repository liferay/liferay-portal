/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.upgrade.v6_2_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;

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
public class LayoutPageTemplateEntryClassTypeKeyUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	@TestInfo("LPD-103532")
	public void testUpgradeFileEntryDisplayPageTemplate() throws Exception {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_addDisplayPageTemplate(
				_portal.getClassNameId(FileEntry.class.getName()),
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT,
				null);

		_runUpgrade();

		layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.getLayoutPageTemplateEntry(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId());

		DLFileEntryType dlFileEntryType =
			_dlFileEntryTypeLocalService.getDLFileEntryType(
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT);

		Assert.assertEquals(
			dlFileEntryType.getFileEntryTypeKey(),
			layoutPageTemplateEntry.getClassTypeKey());
	}

	@Test
	@TestInfo("LPD-103532")
	public void testUpgradeJournalArticleDisplayPageTemplateWithBlankClassTypeKey()
		throws Exception {

		_testUpgradeJournalArticleDisplayPageTemplate(StringPool.BLANK);
	}

	@Test
	@TestInfo("LPD-103532")
	public void testUpgradeJournalArticleDisplayPageTemplateWithClassTypeKey()
		throws Exception {

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		String classTypeKey = RandomTestUtil.randomString();

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_addDisplayPageTemplate(
				_portal.getClassNameId(JournalArticle.class.getName()),
				ddmStructure.getStructureId(), classTypeKey);

		_runUpgrade();

		layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.getLayoutPageTemplateEntry(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId());

		Assert.assertEquals(
			ddmStructure.getStructureId(),
			layoutPageTemplateEntry.getClassTypeId());
		Assert.assertEquals(
			classTypeKey, layoutPageTemplateEntry.getClassTypeKey());
	}

	@Test
	@TestInfo("LPD-103532")
	public void testUpgradeJournalArticleDisplayPageTemplateWithCTCollection()
		throws Exception {

		CTCollection ctCollection = _addCTCollection();

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
				_group.getGroupId(), JournalArticle.class.getName());

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_addDisplayPageTemplate(
					_portal.getClassNameId(JournalArticle.class.getName()),
					ddmStructure.getStructureId(), null);

			_runUpgrade();

			layoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.getLayoutPageTemplateEntry(
					layoutPageTemplateEntry.getLayoutPageTemplateEntryId());

			Assert.assertEquals(
				ddmStructure.getStructureKey(),
				layoutPageTemplateEntry.getClassTypeKey());
		}
		finally {
			_ctCollectionLocalService.deleteCTCollection(ctCollection);
		}
	}

	@Test
	@TestInfo("LPD-103532")
	public void testUpgradeJournalArticleDisplayPageTemplateWithNonexistentClassTypeId()
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_addDisplayPageTemplate(
				_portal.getClassNameId(JournalArticle.class.getName()),
				RandomTestUtil.nextLong(), null);

		_runUpgrade();

		layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.getLayoutPageTemplateEntry(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId());

		Assert.assertTrue(
			Validator.isNull(layoutPageTemplateEntry.getClassTypeKey()));
	}

	@Test
	@TestInfo("LPD-103532")
	public void testUpgradeJournalArticleDisplayPageTemplateWithNullClassTypeKey()
		throws Exception {

		_testUpgradeJournalArticleDisplayPageTemplate(null);
	}

	private CTCollection _addCTCollection() throws Exception {
		return _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), RandomTestUtil.randomString());
	}

	private LayoutPageTemplateEntry _addDisplayPageTemplate(
			long classNameId, long classTypeId, String classTypeKey)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId(), classNameId, null, false,
				WorkflowConstants.STATUS_APPROVED);

		layoutPageTemplateEntry.setClassTypeId(classTypeId);

		layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.updateLayoutPageTemplateEntry(
				layoutPageTemplateEntry);

		if (classTypeKey != null) {
			_updateClassTypeKey(
				classTypeKey, layoutPageTemplateEntry.getCtCollectionId(),
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId());
		}

		return layoutPageTemplateEntry;
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();

		_entityCache.clearCache();
		_multiVMPool.clear();
	}

	private void _testUpgradeJournalArticleDisplayPageTemplate(
			String classTypeKey)
		throws Exception {

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_addDisplayPageTemplate(
				_portal.getClassNameId(JournalArticle.class.getName()),
				ddmStructure.getStructureId(), classTypeKey);

		_runUpgrade();

		layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.getLayoutPageTemplateEntry(
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId());

		Assert.assertEquals(
			ddmStructure.getStructureKey(),
			layoutPageTemplateEntry.getClassTypeKey());
	}

	private void _updateClassTypeKey(
			String classTypeKey, long ctCollectionId,
			long layoutPageTemplateEntryId)
		throws Exception {

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"update LayoutPageTemplateEntry set classTypeKey = ? where " +
					"ctCollectionId = ? and layoutPageTemplateEntryId = ?")) {

			preparedStatement.setString(1, classTypeKey);
			preparedStatement.setLong(2, ctCollectionId);
			preparedStatement.setLong(3, layoutPageTemplateEntryId);

			preparedStatement.executeUpdate();
		}
	}

	private static final String _CLASS_NAME =
		"com.liferay.layout.page.template.internal.upgrade.v6_2_1." +
			"LayoutPageTemplateEntryClassTypeKeyUpgradeProcess";

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Inject
	private EntityCache _entityCache;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private Portal _portal;

	@Inject(
		filter = "(&(component.name=com.liferay.layout.page.template.internal.upgrade.registry.LayoutPageTemplateServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}