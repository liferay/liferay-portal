/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.spi.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTCollectionServiceUtil;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.util.DLURLHelperUtil;
import com.liferay.dynamic.data.mapping.model.DDMFieldAttribute;
import com.liferay.dynamic.data.mapping.service.DDMFieldLocalService;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.repository.liferayrepository.model.LiferayFileEntry;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brooke Dalton
 */
@RunWith(Arquillian.class)
public class DDMFieldAttributeCTEventListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, StringUtil.randomString(), null);
	}

	@Test
	public void testOnAfterPublish() throws Exception {
		JournalArticle journalArticle = null;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			FileEntry fileEntry = _addFileEntry();

			journalArticle = JournalTestUtil.addArticleWithXMLContent(
				DDMStructureTestUtil.getSampleStructuredContent(
					"content",
					Collections.singletonList(
						HashMapBuilder.put(
							LocaleUtil.US,
							StringBundler.concat(
								"<img src=\"",
								DLURLHelperUtil.getPreviewURL(
									fileEntry, fileEntry.getFileVersion(), null,
									StringPool.BLANK),
								"\"/>")
						).build()),
					LanguageUtil.getLanguageId(LocaleUtil.US)),
				"BASIC-WEB-CONTENT", "BASIC-WEB-CONTENT");

			String attributeValue = _getDDMFieldAttributeValue(
				journalArticle.getId());

			Assert.assertTrue(
				attributeValue.contains(
					"previewCTCollectionId=" +
						_ctCollection.getCtCollectionId()));
		}

		CTCollectionServiceUtil.publishCTCollection(
			TestPropsValues.getUserId(), _ctCollection.getCtCollectionId());

		String attributeValue = _getDDMFieldAttributeValue(
			journalArticle.getId());

		Assert.assertFalse(attributeValue.contains("previewCTCollectionId="));
	}

	private FileEntry _addFileEntry() throws Exception {
		DLFileEntry dlFileEntry = _dlFileEntryLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
			TestPropsValues.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), ContentTypes.IMAGE_JPEG,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			StringPool.BLANK, StringPool.BLANK,
			DLFileEntryTypeConstants.COMPANY_ID_BASIC_DOCUMENT,
			Collections.emptyMap(), null,
			new UnsyncByteArrayInputStream(new byte[0]), 0, null, null, null,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId()));

		return new LiferayFileEntry(dlFileEntry);
	}

	private String _getDDMFieldAttributeValue(long journalArticleId) {
		List<DDMFieldAttribute> ddmFieldAttributes =
			_ddmFieldLocalService.getDDMFieldAttributes(journalArticleId, null);

		Assert.assertEquals(
			ddmFieldAttributes.toString(), 1, ddmFieldAttributes.size());

		DDMFieldAttribute ddmFieldAttribute = ddmFieldAttributes.get(0);

		return ddmFieldAttribute.getAttributeValue();
	}

	private CTCollection _ctCollection;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private DDMFieldLocalService _ddmFieldLocalService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

}