/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.search.spi.model.index.contributor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.store.DLStore;
import com.liferay.document.library.kernel.store.DLStoreRequest;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.IOException;

import java.nio.charset.StandardCharsets;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class DLFileEntryModelDocumentContributorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testCachedTextExtractionIsNotReused() throws Exception {
		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				_getConfigurationTemporarySwapper(false)) {

			DLFileEntry dlFileEntry = _addDLFileEntry();

			DLFileVersion dlFileVersion = dlFileEntry.getFileVersion();

			_dlStore.addFile(
				DLStoreRequest.builder(
					dlFileEntry.getCompanyId(),
					dlFileEntry.getDataRepositoryId(), dlFileEntry.getName()
				).versionLabel(
					dlFileVersion.getStoreFileName() + ".index"
				).build(),
				"overriden".getBytes(StandardCharsets.UTF_8));

			Document document = new DocumentImpl();

			_dlFileEntryModelDocumentContributor.contribute(
				document, dlFileEntry);

			Assert.assertNotEquals(
				"overriden",
				document.get(
					_portal.getSiteDefaultLocale(dlFileEntry.getGroupId()),
					Field.CONTENT));
		}
	}

	@Test
	public void testCachedTextExtractionIsReused() throws Exception {
		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				_getConfigurationTemporarySwapper(true)) {

			DLFileEntry dlFileEntry = _addDLFileEntry();

			DLFileVersion dlFileVersion = dlFileEntry.getFileVersion();

			_dlStore.addFile(
				DLStoreRequest.builder(
					dlFileEntry.getCompanyId(),
					dlFileEntry.getDataRepositoryId(), dlFileEntry.getName()
				).versionLabel(
					dlFileVersion.getStoreFileName() + ".index"
				).build(),
				"overriden".getBytes(StandardCharsets.UTF_8));

			Document document = new DocumentImpl();

			_dlFileEntryModelDocumentContributor.contribute(
				document, dlFileEntry);

			Assert.assertEquals(
				"overriden",
				document.get(
					_portal.getSiteDefaultLocale(dlFileEntry.getGroupId()),
					Field.CONTENT));
		}
	}

	@Test
	public void testCachedTextExtractionWithDLFileIndexingMaxSizeAndCacheExtraction()
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				_getConfigurationTemporarySwapper(true)) {

			String fileContent = StringUtil.randomString(
				PropsValues.DL_FILE_INDEXING_MAX_SIZE + 1);

			DLFileEntry dlFileEntry = _addDLFileEntry(
				ContentTypes.APPLICATION_OCTET_STREAM,
				fileContent.getBytes(StandardCharsets.UTF_8));

			Document document = new DocumentImpl();

			_dlFileEntryModelDocumentContributor.contribute(
				document, dlFileEntry);

			String content = _getFileAsString(dlFileEntry);

			Assert.assertEquals(
				PropsValues.DL_FILE_INDEXING_MAX_SIZE, content.length());

			_withDLFileIndexingMaxSize(
				100,
				() -> _dlFileEntryModelDocumentContributor.contribute(
					document, dlFileEntry));

			content = _getFileAsString(dlFileEntry);

			Assert.assertEquals(100, content.length());
		}
	}

	@Test
	public void testCachedTextExtractionWithDLFileIndexingMaxSizeAndNoCacheExtraction()
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				_getConfigurationTemporarySwapper(false)) {

			String fileContent = StringUtil.randomString(
				PropsValues.DL_FILE_INDEXING_MAX_SIZE + 1);

			DLFileEntry dlFileEntry = _addDLFileEntry(
				ContentTypes.APPLICATION_OCTET_STREAM,
				fileContent.getBytes(StandardCharsets.UTF_8));

			Document document = new DocumentImpl();

			_dlFileEntryModelDocumentContributor.contribute(
				document, dlFileEntry);

			String content = document.get(
				_portal.getSiteDefaultLocale(dlFileEntry.getGroupId()),
				Field.CONTENT);

			Assert.assertEquals(
				PropsValues.DL_FILE_INDEXING_MAX_SIZE, content.length());

			_withDLFileIndexingMaxSize(
				100,
				() -> _dlFileEntryModelDocumentContributor.contribute(
					document, dlFileEntry));

			content = document.get(
				_portal.getSiteDefaultLocale(dlFileEntry.getGroupId()),
				Field.CONTENT);

			Assert.assertEquals(100, content.length());
		}
	}

	@Test
	public void testIndexDefaultLanguageId() throws Exception {
		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		_testIndexDefaultLanguageId(
			company.getLocale(), "true", LocaleUtil.HUNGARY);

		_testIndexDefaultLanguageId(
			LocaleUtil.HUNGARY, "false", LocaleUtil.HUNGARY);
	}

	@Test
	public void testTextExtractionIsCachedInDLStore() throws Exception {
		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				_getConfigurationTemporarySwapper(true)) {

			DLFileEntry dlFileEntry = _addDLFileEntry();

			Assert.assertFalse(_hasFile(dlFileEntry));

			_dlFileEntryModelDocumentContributor.contribute(
				new DocumentImpl(), dlFileEntry);

			Assert.assertEquals(
				StreamUtil.toString(dlFileEntry.getContentStream()),
				_getFileAsString(dlFileEntry));
			Assert.assertTrue(_hasFile(dlFileEntry));
		}
	}

	@Test
	public void testTextExtractionIsCachedInDLStoreForCTCollection()
		throws Exception {

		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), null);

		DLFileEntry dlFileEntry = _addDLFileEntry();

		Assert.assertFalse(_hasFile(dlFileEntry));

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			try (ConfigurationTemporarySwapper configurationTemporarySwapper =
					_getConfigurationTemporarySwapper(true)) {

				_dlFileEntryModelDocumentContributor.contribute(
					new DocumentImpl(), dlFileEntry);

				Assert.assertEquals(
					StreamUtil.toString(dlFileEntry.getContentStream()),
					_getFileAsString(dlFileEntry));
				Assert.assertTrue(_hasFile(dlFileEntry));
			}
		}
	}

	@Test
	public void testTextExtractionIsNotCachedInDLStore() throws Exception {
		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				_getConfigurationTemporarySwapper(false)) {

			DLFileEntry dlFileEntry = _addDLFileEntry();

			Assert.assertFalse(_hasFile(dlFileEntry));

			_dlFileEntryModelDocumentContributor.contribute(
				new DocumentImpl(), dlFileEntry);

			Assert.assertFalse(_hasFile(dlFileEntry));
		}
	}

	@Test
	public void testTextExtractionIsNotCachedInDLStoreForReadOnlyCTCollection()
		throws Exception {

		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), null);

		DLFileEntry dlFileEntry = _addDLFileEntry();

		Assert.assertFalse(_hasFile(dlFileEntry));

		ctCollection.setStatus(WorkflowConstants.STATUS_EXPIRED);

		ctCollection = _ctCollectionLocalService.updateCTCollection(
			ctCollection);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			try (ConfigurationTemporarySwapper configurationTemporarySwapper =
					_getConfigurationTemporarySwapper(true)) {

				_dlFileEntryModelDocumentContributor.contribute(
					new DocumentImpl(), dlFileEntry);

				Assert.assertFalse(_hasFile(dlFileEntry));
			}
		}
	}

	private DLFileEntry _addDLFileEntry() throws Exception {
		String content = StringUtil.randomString();

		return _addDLFileEntry(
			ContentTypes.APPLICATION_OCTET_STREAM,
			content.getBytes(StandardCharsets.UTF_8));
	}

	private DLFileEntry _addDLFileEntry(String mimeType, byte[] bytes)
		throws Exception {

		return _dlFileEntryLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			_group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), mimeType, StringUtil.randomString(),
			null, StringPool.BLANK, StringPool.BLANK,
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT, null,
			null, new UnsyncByteArrayInputStream(bytes), bytes.length, null,
			null, null, ServiceContextTestUtil.getServiceContext());
	}

	private ConfigurationTemporarySwapper _getConfigurationTemporarySwapper(
			boolean cacheTextExtraction)
		throws Exception {

		return new ConfigurationTemporarySwapper(
			"com.liferay.document.library.internal.configuration." +
				"DLIndexerConfiguration",
			HashMapDictionaryBuilder.<String, Object>put(
				"cacheTextExtraction", cacheTextExtraction
			).build());
	}

	private String _getFileAsString(DLFileEntry dlFileEntry)
		throws IOException, PortalException {

		DLFileVersion dlFileVersion = dlFileEntry.getFileVersion();

		return StringUtil.trim(
			StreamUtil.toString(
				_dlStore.getFileAsStream(
					dlFileEntry.getCompanyId(),
					dlFileEntry.getDataRepositoryId(), dlFileEntry.getName(),
					dlFileVersion.getStoreFileName() + ".index")));
	}

	private boolean _hasFile(DLFileEntry dlFileEntry) throws PortalException {
		DLFileVersion dlFileVersion = dlFileEntry.getFileVersion();

		return _dlStore.hasFile(
			dlFileEntry.getCompanyId(), dlFileEntry.getDataRepositoryId(),
			dlFileEntry.getName(), dlFileVersion.getStoreFileName() + ".index");
	}

	private void _testIndexDefaultLanguageId(
			Locale expectedLocale, String inheritLocales, Locale locale)
		throws Exception {

		Document document = new DocumentImpl();

		UnicodeProperties typeSettingsUnicodeProperties =
			_group.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.setProperty(
			GroupConstants.TYPE_SETTINGS_KEY_INHERIT_LOCALES, inheritLocales);

		String languageId = LanguageUtil.getLanguageId(locale);

		typeSettingsUnicodeProperties.setProperty(
			PropsKeys.LOCALES, languageId);
		typeSettingsUnicodeProperties.setProperty("languageId", languageId);

		_group.setTypeSettingsProperties(typeSettingsUnicodeProperties);

		_group = _groupLocalService.updateGroup(_group);

		DLFileEntry dlFileEntry = _addDLFileEntry();

		_dlFileEntryModelDocumentContributor.contribute(document, dlFileEntry);

		Assert.assertEquals(
			LocaleUtil.toLanguageId(expectedLocale),
			document.get(Field.DEFAULT_LANGUAGE_ID));
	}

	private <E extends Exception> void _withDLFileIndexingMaxSize(
			int dlFileIndexingMaxSize, UnsafeRunnable<E> unsafeRunnable)
		throws Exception {

		PropsUtil.set(
			PropsKeys.DL_FILE_INDEXING_MAX_SIZE,
			String.valueOf(dlFileIndexingMaxSize));

		try {
			unsafeRunnable.run();
		}
		finally {
			PropsUtil.set(
				PropsKeys.DL_FILE_INDEXING_MAX_SIZE,
				String.valueOf(PropsValues.DL_FILE_INDEXING_MAX_SIZE));
		}
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.document.library.internal.search.spi.model.index.contributor.DLFileEntryModelDocumentContributor"
	)
	private ModelDocumentContributor<DLFileEntry>
		_dlFileEntryModelDocumentContributor;

	@Inject
	private DLStore _dlStore;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private Portal _portal;

}