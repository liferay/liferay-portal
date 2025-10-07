/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.conflict.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.conflict.ConflictInfo;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.service.CTProcessLocalService;
import com.liferay.change.tracking.store.model.CTSContent;
import com.liferay.change.tracking.store.service.CTSContentLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pei-Jung Lan
 */
@RunWith(Arquillian.class)
public class CTEntryConflictHelperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_ctCollection1 = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, CTColumnResolutionMaxTest.class.getSimpleName(), null);
		_ctCollection2 = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, CTColumnResolutionMaxTest.class.getSimpleName(), null);
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testGetMissingRequirementTypeName() throws Exception {
		DLFolder dlFolder = DLTestUtil.addDLFolder(_group.getGroupId());

		DLFileEntry dlFileEntry = null;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection1.getCtCollectionId())) {

			dlFileEntry = DLTestUtil.addDLFileEntry(dlFolder.getFolderId());

			List<CTEntry> ctEntries = _ctEntryLocalService.getCTEntries(
				_ctCollection1.getCtCollectionId(),
				_classNameLocalService.getClassNameId(CTSContent.class));

			for (CTEntry ctEntry : ctEntries) {
				_ctsContentLocalService.deleteCTSContent(
					ctEntry.getModelClassPK());
			}
		}

		Map<Long, List<ConflictInfo>> conflictInfosMap =
			_ctCollectionLocalService.checkConflicts(_ctCollection1);

		List<ConflictInfo> dlFileEntryConflictInfos = conflictInfosMap.get(
			_classNameLocalService.getClassNameId(DLFileEntry.class));

		Assert.assertEquals(
			conflictInfosMap.toString(), 1, dlFileEntryConflictInfos.size());

		ConflictInfo conflictInfo = dlFileEntryConflictInfos.get(0);

		Assert.assertEquals(
			_language.format(
				LocaleUtil.ENGLISH,
				"cannot-be-added-because-a-required-x-has-been-deleted",
				"file"),
			conflictInfo.getResolutionDescription(
				conflictInfo.getResourceBundle(LocaleUtil.ENGLISH)));
		Assert.assertEquals(
			conflictInfo.getSourcePrimaryKey(), dlFileEntry.getFileEntryId());
	}

	@Test
	public void testHasDeletionModificationConflict() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection1.getCtCollectionId())) {

			_journalArticleLocalService.moveArticleToTrash(
				TestPropsValues.getUserId(), journalArticle);
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection2.getCtCollectionId())) {

			JournalTestUtil.updateArticle(journalArticle);
		}

		_ctProcessLocalService.addCTProcess(
			_ctCollection1.getUserId(), _ctCollection1.getCtCollectionId());

		Map<Long, List<ConflictInfo>> conflictInfosMap =
			_ctCollectionLocalService.checkConflicts(_ctCollection2);

		Assert.assertEquals(
			conflictInfosMap.toString(), 2, conflictInfosMap.size());
	}

	@Inject
	private static ClassNameLocalService _classNameLocalService;

	@Inject
	private static CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private static CTEntryLocalService _ctEntryLocalService;

	@Inject
	private static CTProcessLocalService _ctProcessLocalService;

	@Inject
	private static CTSContentLocalService _ctsContentLocalService;

	@Inject
	private static JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private static Language _language;

	@DeleteAfterTestRun
	private CTCollection _ctCollection1;

	@DeleteAfterTestRun
	private CTCollection _ctCollection2;

	@DeleteAfterTestRun
	private Group _group;

}