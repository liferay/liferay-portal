/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.app.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.exception.AssetCategoryException;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.document.library.app.service.test.util.DLAppServiceTestUtil;
import com.liferay.document.library.kernel.exception.DuplicateFileEntryException;
import com.liferay.document.library.kernel.exception.FileExtensionException;
import com.liferay.document.library.kernel.exception.FileNameException;
import com.liferay.document.library.kernel.exception.FileSizeException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.document.library.test.util.BaseDLAppTestCase;
import com.liferay.document.library.workflow.WorkflowHandlerInvocationCounter;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.increment.BufferedIncrementThreadLocal;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.constants.ServiceTestConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.upload.configuration.UploadServletRequestConfigurationProviderUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.security.permission.DoAsUserThread;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portlet.asset.util.AssetVocabularySettingsHelper;

import java.io.File;
import java.io.InputStream;

import java.util.Date;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alexander Chow
 */
@RunWith(Arquillian.class)
public class DLAppServiceWhenAddingAFileEntryTest extends BaseDLAppTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAssetEntryShouldHavePublishDate() throws Exception {
		String fileName = RandomTestUtil.randomString();

		FileEntry fileEntry = DLAppServiceTestUtil.addFileEntry(
			group.getGroupId(), parentFolder.getFolderId(), fileName);

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			DLFileEntryConstants.getClassName(), fileEntry.getFileEntryId());

		Assert.assertEquals(
			assetEntry.getCreateDate(), assetEntry.getPublishDate());
	}

	@Test
	public void testAssetEntryShouldStoreExternalReferenceCode()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();
		String fileName = RandomTestUtil.randomString();

		FileEntry fileEntry = DLAppServiceTestUtil.addFileEntry(
			externalReferenceCode, group.getGroupId(),
			parentFolder.getFolderId(), fileName, fileName, null, null, null,
			null);

		Assert.assertEquals(
			externalReferenceCode, fileEntry.getExternalReferenceCode());

		fileEntry = dlAppService.getFileEntryByExternalReferenceCode(
			externalReferenceCode, group.getGroupId());

		Assert.assertEquals(
			externalReferenceCode, fileEntry.getExternalReferenceCode());
	}

	@Test
	public void testAssetEntryShouldStoreFileEntryIdAsExternalReferenceCodeIfExternalReferenceCodeIsNotPresent()
		throws Exception {

		String fileName = RandomTestUtil.randomString();

		FileEntry fileEntry = DLAppServiceTestUtil.addFileEntry(
			null, group.getGroupId(), parentFolder.getFolderId(), fileName,
			fileName, null, null, null, null);

		String externalReferenceCode = fileEntry.getExternalReferenceCode();

		Assert.assertEquals(
			externalReferenceCode, fileEntry.getExternalReferenceCode());

		fileEntry = dlAppService.getFileEntryByExternalReferenceCode(
			externalReferenceCode, group.getGroupId());

		Assert.assertEquals(
			externalReferenceCode, fileEntry.getExternalReferenceCode());
	}

	@Test
	public void testAssetTagsShouldBeOrdered() throws Exception {
		String fileName = RandomTestUtil.randomString();

		String[] assetTagNames = {"hello", "world"};

		FileEntry fileEntry = DLAppServiceTestUtil.addFileEntry(
			RandomTestUtil.randomString(), group.getGroupId(),
			parentFolder.getFolderId(), fileName, fileName, null, null, null,
			assetTagNames);

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			DLFileEntryConstants.getClassName(), fileEntry.getFileEntryId());

		AssertUtils.assertEqualsSorted(assetTagNames, assetEntry.getTagNames());
	}

	@Test
	public void testFileEntryShouldSaveCorrectMimeTypeForGPXExtension()
		throws Exception {

		String fileName = "test.gpx";

		FileEntry fileEntry = dlAppService.addFileEntry(
			null, group.getGroupId(), parentFolder.getFolderId(), fileName,
			ContentTypes.APPLICATION_OCTET_STREAM, fileName, StringPool.BLANK,
			StringPool.BLANK, StringPool.BLANK,
			FileUtil.getBytes(getClass(), _TEST_GPX), null, null, null,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		Assert.assertEquals("application/gpx+xml", fileEntry.getMimeType());
	}

	@Test
	public void testFileEntryShouldSaveDisplayDate() throws Exception {
		Date displayDate = new Date();

		FileEntry fileEntry = DLAppServiceTestUtil.addFileEntry(
			null, group.getGroupId(), parentFolder.getFolderId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			displayDate, null, null, null);

		Assert.assertEquals(displayDate, fileEntry.getDisplayDate());
		Assert.assertNull(fileEntry.getExpirationDate());
		Assert.assertNull(fileEntry.getReviewDate());
	}

	@Test
	public void testFileEntryShouldSaveExpirationDate() throws Exception {
		Date expirationDate = new Date(
			System.currentTimeMillis() + Time.MINUTE);

		FileEntry fileEntry = DLAppServiceTestUtil.addFileEntry(
			null, group.getGroupId(), parentFolder.getFolderId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			expirationDate, null, null);

		Assert.assertNull(fileEntry.getDisplayDate());
		Assert.assertEquals(expirationDate, fileEntry.getExpirationDate());
		Assert.assertNull(fileEntry.getReviewDate());
	}

	@Test
	public void testFileEntryShouldSaveReviewDate() throws Exception {
		Date reviewDate = new Date();

		FileEntry fileEntry = DLAppServiceTestUtil.addFileEntry(
			null, group.getGroupId(), parentFolder.getFolderId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			null, reviewDate, null);

		Assert.assertNull(fileEntry.getDisplayDate());
		Assert.assertNull(fileEntry.getExpirationDate());
		Assert.assertEquals(reviewDate, fileEntry.getReviewDate());
	}

	@Test
	public void testGetMaxSizeWithPadding() throws Exception {
		String fileName = "test.pdf";
		int maxSize =
			(int)UploadServletRequestConfigurationProviderUtil.getMaxSize();

		FileEntry fileEntry = DLAppServiceUtil.addFileEntry(
			null, group.getGroupId(), parentFolder.getFolderId(), fileName,
			ContentTypes.APPLICATION_OCTET_STREAM, fileName, StringPool.BLANK,
			StringPool.BLANK, StringPool.BLANK, new byte[maxSize], null, null,
			null, ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		Assert.assertNotNull(fileEntry);
	}

	@Test
	public void testShouldCallWorkflowHandler() throws Exception {
		try (WorkflowHandlerInvocationCounter<DLFileEntry>
				workflowHandlerInvocationCounter =
					new WorkflowHandlerInvocationCounter<>(
						DLFileEntryConstants.getClassName())) {

			DLAppServiceTestUtil.addFileEntry(
				group.getGroupId(), parentFolder.getFolderId());

			Assert.assertEquals(
				1,
				workflowHandlerInvocationCounter.getCount(
					"updateStatus", Object.class, int.class, Map.class));
		}
	}

	@Test(expected = AssetCategoryException.class)
	public void testShouldFailIfAddingAssetCategoriesFromNonmultiValuedAssetVocabulary()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group, TestPropsValues.getUserId());

		AssetVocabularySettingsHelper assetVocabularySettingsHelper =
			new AssetVocabularySettingsHelper();

		assetVocabularySettingsHelper.setClassNameIdsAndClassTypePKs(
			new long[] {AssetCategoryConstants.ALL_CLASS_NAME_ID},
			new long[] {AssetCategoryConstants.ALL_CLASS_TYPE_PK},
			new boolean[] {false}, new boolean[] {false});

		assetVocabularySettingsHelper.setMultiValued(false);

		Assert.assertFalse(assetVocabularySettingsHelper.isMultiValued());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), group.getGroupId(),
				RandomTestUtil.randomString(),
				HashMapBuilder.put(
					LocaleUtil.US, RandomTestUtil.randomString()
				).build(),
				null, assetVocabularySettingsHelper.toString(), serviceContext);

		AssetCategory assetCategory1 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), group.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);
		AssetCategory assetCategory2 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), group.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);

		serviceContext.setAssetCategoryIds(
			new long[] {
				assetCategory1.getCategoryId(), assetCategory2.getCategoryId()
			});

		dlAppService.addFileEntry(
			null, group.getGroupId(), parentFolder.getFolderId(),
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, CONTENT.getBytes(), null, null, null,
			serviceContext);
	}

	@Test(expected = DuplicateFileEntryException.class)
	public void testShouldFailIfDuplicateExternalReferenceCode()
		throws Exception {

		String externalReferenceCode = StringUtil.randomString();

		DLAppServiceTestUtil.addFileEntry(
			externalReferenceCode, group.getGroupId(),
			parentFolder.getFolderId(), DLAppServiceTestUtil.FILE_NAME,
			DLAppServiceTestUtil.STRIPPED_FILE_NAME, null, null, null, null);
		DLAppServiceTestUtil.addFileEntry(
			externalReferenceCode, group.getGroupId(),
			parentFolder.getFolderId(), DLAppServiceTestUtil.FILE_NAME,
			DLAppServiceTestUtil.FILE_NAME, null, null, null, null);
	}

	@Test(expected = DuplicateFileEntryException.class)
	public void testShouldFailIfDuplicateNameAndExtensionInFolder1()
		throws Exception {

		DLAppServiceTestUtil.addFileEntry(
			RandomTestUtil.randomString(), group.getGroupId(),
			parentFolder.getFolderId(), DLAppServiceTestUtil.FILE_NAME,
			DLAppServiceTestUtil.STRIPPED_FILE_NAME, null, null, null, null);
		DLAppServiceTestUtil.addFileEntry(
			RandomTestUtil.randomString(), group.getGroupId(),
			parentFolder.getFolderId(), DLAppServiceTestUtil.FILE_NAME,
			DLAppServiceTestUtil.FILE_NAME, null, null, null, null);
	}

	@Test(expected = DuplicateFileEntryException.class)
	public void testShouldFailIfDuplicateNameAndExtensionInFolder2()
		throws Exception {

		DLAppServiceTestUtil.addFileEntry(
			RandomTestUtil.randomString(), group.getGroupId(),
			parentFolder.getFolderId(), DLAppServiceTestUtil.FILE_NAME,
			DLAppServiceTestUtil.FILE_NAME, null, null, null, null);
		DLAppServiceTestUtil.addFileEntry(
			RandomTestUtil.randomString(), group.getGroupId(),
			parentFolder.getFolderId(), DLAppServiceTestUtil.FILE_NAME,
			DLAppServiceTestUtil.STRIPPED_FILE_NAME, null, null, null, null);
	}

	@Test(expected = DuplicateFileEntryException.class)
	public void testShouldFailIfDuplicateNameInFolder() throws Exception {
		DLAppServiceTestUtil.addFileEntry(
			group.getGroupId(), parentFolder.getFolderId());
		DLAppServiceTestUtil.addFileEntry(
			group.getGroupId(), parentFolder.getFolderId());
	}

	@Test(expected = FileSizeException.class)
	public void testShouldFailIfSizeLimitExceeded() throws Exception {
		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.document.library.internal.configuration." +
						"DLSizeLimitConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"fileMaxSize", 1L
					).build())) {

			String fileName = RandomTestUtil.randomString();

			DLAppServiceTestUtil.addFileEntry(
				group.getGroupId(), parentFolder.getFolderId(), fileName);
		}
	}

	@Test(expected = FileNameException.class)
	public void testShouldFailIfSourceFileNameContainsBlacklistedChar()
		throws Exception {

		int i =
			RandomTestUtil.randomInt() % PropsValues.DL_CHAR_BLACKLIST.length;

		String blackListedChar = PropsValues.DL_CHAR_BLACKLIST[i];

		String sourceFileName =
			RandomTestUtil.randomString() + blackListedChar +
				RandomTestUtil.randomString();

		DLAppServiceTestUtil.addFileEntry(
			group.getGroupId(), parentFolder.getFolderId(), sourceFileName);
	}

	@Test(expected = FileNameException.class)
	public void testShouldFailIfSourceFileNameEndsWithBlacklistedChar()
		throws Exception {

		int i =
			RandomTestUtil.randomInt() %
				PropsValues.DL_CHAR_LAST_BLACKLIST.length;

		String blackListedChar = PropsValues.DL_CHAR_LAST_BLACKLIST[i];

		String sourceFileName = RandomTestUtil.randomString() + blackListedChar;

		DLAppServiceTestUtil.addFileEntry(
			group.getGroupId(), parentFolder.getFolderId(), sourceFileName);
	}

	@Test(expected = FileExtensionException.class)
	public void testShouldFailIfSourceFileNameExtensionNotSupported()
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				DLAppServiceTestUtil.getConfigurationTemporarySwapper(
					"fileExtensions", new String[0])) {

			String sourceFileName = "file.jpg";

			DLAppServiceTestUtil.addFileEntry(
				group.getGroupId(), parentFolder.getFolderId(), sourceFileName);
		}
	}

	@Test(expected = FileNameException.class)
	public void testShouldFailIfSourceFileNameIsBlacklisted() throws Exception {
		int i =
			RandomTestUtil.randomInt() % PropsValues.DL_NAME_BLACKLIST.length;

		String blackListedName = PropsValues.DL_NAME_BLACKLIST[i];

		DLAppServiceTestUtil.addFileEntry(
			group.getGroupId(), parentFolder.getFolderId(), blackListedName);
	}

	@Test
	public void testShouldHaveDefaultVersion() throws Exception {
		String fileName = RandomTestUtil.randomString();

		FileEntry fileEntry = DLAppServiceTestUtil.addFileEntry(
			group.getGroupId(), parentFolder.getFolderId(), fileName);

		Assert.assertEquals(
			"Version label incorrect after add", "1.0", fileEntry.getVersion());
	}

	@Test
	public void testShouldInferValidMimeType() throws Exception {
		String fileName = RandomTestUtil.randomString();

		FileEntry fileEntry = dlAppService.addFileEntry(
			null, group.getGroupId(), parentFolder.getFolderId(), fileName,
			ContentTypes.APPLICATION_OCTET_STREAM, fileName, StringPool.BLANK,
			StringPool.BLANK, StringPool.BLANK, CONTENT.getBytes(), null, null,
			null, ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		Assert.assertEquals(ContentTypes.TEXT_PLAIN, fileEntry.getMimeType());
	}

	@Test
	public void testShouldSucceedIfDuplicateNameInOtherFolder()
		throws Exception {

		DLAppServiceTestUtil.addFileEntry(
			group.getGroupId(), parentFolder.getFolderId());
		DLAppServiceTestUtil.addFileEntry(
			group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);
	}

	@Ignore
	@Test
	public void testShouldSucceedWithConcurrentAccess() throws Exception {
		_users = new User[ServiceTestConstants.THREAD_COUNT];

		for (int i = 0; i < ServiceTestConstants.THREAD_COUNT; i++) {
			User user = UserTestUtil.addUser(
				"DLAppServiceTest" + (i + 1), group.getGroupId());

			_users[i] = user;
		}

		DoAsUserThread[] doAsUserThreads = new DoAsUserThread[_users.length];

		_fileEntryIds = new long[_users.length];

		for (int i = 0; i < doAsUserThreads.length; i++) {
			doAsUserThreads[i] = new AddFileEntryThread(
				_users[i].getUserId(), i);
		}

		int successCount = DLAppServiceTestUtil.runUserThreads(doAsUserThreads);

		Assert.assertEquals(
			StringBundler.concat(
				"Only ", successCount, " out of ", _users.length,
				" threads added successfully"),
			_users.length, successCount);

		for (int i = 0; i < doAsUserThreads.length; i++) {
			doAsUserThreads[i] = new GetFileEntryThread(
				_users[i].getUserId(), i);
		}

		successCount = DLAppServiceTestUtil.runUserThreads(doAsUserThreads);

		Assert.assertEquals(
			StringBundler.concat(
				"Only ", successCount, " out of ", _users.length,
				" threads retrieved successfully"),
			_users.length, successCount);
	}

	@Test
	public void testShouldSucceedWithNullBytes() throws Exception {
		String fileName = RandomTestUtil.randomString();

		dlAppService.addFileEntry(
			null, group.getGroupId(), parentFolder.getFolderId(), fileName,
			ContentTypes.TEXT_PLAIN, fileName, StringPool.BLANK,
			StringPool.BLANK, StringPool.BLANK, (byte[])null, null, null, null,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));
	}

	@Test
	public void testShouldSucceedWithNullFile() throws Exception {
		String fileName = RandomTestUtil.randomString();

		dlAppService.addFileEntry(
			null, group.getGroupId(), parentFolder.getFolderId(), fileName,
			ContentTypes.TEXT_PLAIN, fileName, StringPool.BLANK,
			StringPool.BLANK, StringPool.BLANK, (File)null, null, null, null,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));
	}

	@Test
	public void testShouldSucceedWithNullInputStream() throws Exception {
		String fileName = RandomTestUtil.randomString();

		dlAppService.addFileEntry(
			null, group.getGroupId(), parentFolder.getFolderId(), fileName,
			ContentTypes.TEXT_PLAIN, fileName, StringPool.BLANK,
			StringPool.BLANK, StringPool.BLANK, null, 0, null, null, null,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));
	}

	private static final String _TEST_GPX =
		"/com/liferay/document/library/dependencies/test.gpx";

	private static final Log _log = LogFactoryUtil.getLog(
		DLAppServiceWhenAddingAFileEntryTest.class);

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	private long[] _fileEntryIds;

	@DeleteAfterTestRun
	private User[] _users;

	private class AddFileEntryThread extends DoAsUserThread {

		public AddFileEntryThread(long userId, int index) {
			super(userId);

			_index = index;
		}

		@Override
		public boolean isSuccess() {
			return _success;
		}

		@Override
		protected void doRun() throws Exception {
			try (SafeCloseable safeCloseable =
					BufferedIncrementThreadLocal.setForceSyncWithSafeCloseable(
						true)) {

				FileEntry fileEntry = DLAppServiceTestUtil.addFileEntry(
					group.getGroupId(), parentFolder.getFolderId(),
					"Test-" + _index + ".txt");

				_fileEntryIds[_index] = fileEntry.getFileEntryId();

				if (_log.isDebugEnabled()) {
					_log.debug("Added file " + _index);
				}

				_success = true;
			}
			catch (Exception exception) {
				_log.error("Unable to add file " + _index, exception);
			}
		}

		private int _index;
		private boolean _success;

	}

	private class GetFileEntryThread extends DoAsUserThread {

		public GetFileEntryThread(long userId, int index) {
			super(userId);

			_index = index;
		}

		@Override
		public boolean isSuccess() {
			return _success;
		}

		@Override
		protected void doRun() throws Exception {
			try {
				FileEntry fileEntry = dlAppService.getFileEntry(
					_fileEntryIds[_index]);

				InputStream inputStream = fileEntry.getContentStream();

				String content = StringUtil.read(inputStream);

				if (CONTENT.equals(content)) {
					if (_log.isDebugEnabled()) {
						_log.debug("Retrieved file " + _index);
					}

					_success = true;
				}
			}
			catch (Exception exception) {
				_log.error("Unable to get file " + _index, exception);
			}
		}

		private int _index;
		private boolean _success;

	}

}