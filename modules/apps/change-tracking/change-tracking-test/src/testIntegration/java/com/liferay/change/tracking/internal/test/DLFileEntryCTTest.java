/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.configuration.CTSettingsConfiguration;
import com.liferay.change.tracking.conflict.ConflictInfo;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.service.CTProcessLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFolderService;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.webdav.methods.Method;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.webserver.WebServerServlet;

import java.io.ByteArrayInputStream;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author David Truong
 */
@RunWith(Arquillian.class)
public class DLFileEntryCTTest {

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
			0, JournalArticleCTTest.class.getName(), null);
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testAddFileEntry() throws Exception {
		String fileName = RandomTestUtil.randomString();
		String title = RandomTestUtil.randomString();

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			_addFileEntry(fileName, title);
		}

		_addFileEntry(fileName, title);

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						CTSettingsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"automaticFriendlyURLConflictResolutionEnabled",
							true
						).build())) {

			Map<Long, List<ConflictInfo>> conflictInfosMap =
				_ctCollectionLocalService.checkConflicts(_ctCollection);

			List<ConflictInfo> conflictInfos = conflictInfosMap.get(
				_classNameLocalService.getClassNameId(
					FriendlyURLEntryLocalization.class));

			for (ConflictInfo conflictInfo : conflictInfos) {
				Assert.assertTrue(conflictInfo.isResolved());
			}
		}
	}

	@Test
	public void testAddTempFileEntry() throws Exception {
		DLFolder dlFolder = null;

		TempFileEntryUtil.getTempFileNames(
			_group.getGroupId(), TestPropsValues.getUserId(),
			DLFileEntryCTTest.class.getName());

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			FileEntry tempFileEntry = TempFileEntryUtil.addTempFileEntry(
				_group.getGroupId(), TestPropsValues.getUserId(),
				DLFileEntryCTTest.class.getName(),
				TempFileEntryUtil.getTempFileName("image.jpg"),
				new ByteArrayInputStream("test".getBytes()),
				ContentTypes.IMAGE_JPEG);

			dlFolder = _dlFolderService.getFolder(tempFileEntry.getFolderId());
		}

		_dlFolderService.deleteFolder(dlFolder.getFolderId());

		Map<Long, List<ConflictInfo>> conflictsMap =
			_ctCollectionLocalService.checkConflicts(_ctCollection);

		Assert.assertNull(
			conflictsMap.get(
				_classNameLocalService.getClassNameId(DLFolder.class)));
	}

	@Test
	public void testCheckOutFileEntry() throws Exception {
		FileEntry fileEntry = _addFileEntry();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(fileEntry.getGroupId());

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			_dlAppService.checkOutFileEntry(
				fileEntry.getFileEntryId(), serviceContext);
		}

		List<CTEntry> ctEntries = _ctEntryLocalService.getCTCollectionCTEntries(
			_ctCollection.getCtCollectionId());

		Assert.assertEquals(ctEntries.toString(), 0, ctEntries.size());

		Assert.assertTrue(fileEntry.isCheckedOut());

		_dlAppService.cancelCheckOut(fileEntry.getFileEntryId());

		_dlAppService.checkOutFileEntry(
			fileEntry.getFileEntryId(), serviceContext);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			_dlAppService.cancelCheckOut(fileEntry.getFileEntryId());
		}

		ctEntries = _ctEntryLocalService.getCTCollectionCTEntries(
			_ctCollection.getCtCollectionId());

		Assert.assertEquals(ctEntries.toString(), 0, ctEntries.size());

		Assert.assertFalse(fileEntry.isCheckedOut());
	}

	@Test
	public void testHasFiles() throws Exception {
		String title = RandomTestUtil.randomString();

		FileEntry fileEntry;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			fileEntry = _addFileEntry(RandomTestUtil.randomString(), title);
		}

		String friendlyURL = String.format(
			"%s%s/%s", FriendlyURLResolverConstants.URL_SEPARATOR_X_FILE_ENTRY,
			_group.getFriendlyURL(), title);

		_assertHasFiles(friendlyURL);

		StringBundler sb = new StringBundler(8);

		sb.append(StringPool.SLASH);
		sb.append(fileEntry.getRepositoryId());
		sb.append(StringPool.SLASH);
		sb.append(fileEntry.getFolderId());
		sb.append(StringPool.SLASH);
		sb.append(URLCodec.encodeURL(fileEntry.getFileName()));
		sb.append(StringPool.SLASH);
		sb.append(URLCodec.encodeURL(fileEntry.getUuid()));

		friendlyURL = sb.toString();

		_assertHasFiles(friendlyURL);
	}

	@Test
	public void testTwoPublicationsFriendlyURLCollision() throws Exception {
		long classNameId = _classNameLocalService.getClassNameId(
			DLFileEntry.class);

		String urlTitle = StringUtil.toLowerCase(
			"abc" + RandomTestUtil.randomString());

		String languageId = LocaleUtil.toLanguageId(LocaleUtil.getDefault());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		FriendlyURLEntry friendlyURLEntry1;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			friendlyURLEntry1 =
				_friendlyURLEntryLocalService.addFriendlyURLEntry(
					_group.getGroupId(), classNameId,
					RandomTestUtil.randomLong(), urlTitle, serviceContext);
		}

		CTCollection conflictCTCollection =
			_ctCollectionLocalService.addCTCollection(
				null, TestPropsValues.getCompanyId(),
				TestPropsValues.getUserId(), 0, "LPD-87156-pub2", null);

		FriendlyURLEntry friendlyURLEntry2;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					conflictCTCollection.getCtCollectionId())) {

			friendlyURLEntry2 =
				_friendlyURLEntryLocalService.addFriendlyURLEntry(
					_group.getGroupId(), classNameId,
					RandomTestUtil.randomLong(), urlTitle, serviceContext);
		}

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						CTSettingsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"automaticFriendlyURLConflictResolutionEnabled",
							true
						).build())) {

			_ctProcessLocalService.addCTProcess(
				_ctCollection.getUserId(), _ctCollection.getCtCollectionId());

			_ctProcessLocalService.addCTProcess(
				conflictCTCollection.getUserId(),
				conflictCTCollection.getCtCollectionId());

			friendlyURLEntry1 =
				_friendlyURLEntryLocalService.fetchFriendlyURLEntry(
					friendlyURLEntry1.getFriendlyURLEntryId());
			friendlyURLEntry2 =
				_friendlyURLEntryLocalService.fetchFriendlyURLEntry(
					friendlyURLEntry2.getFriendlyURLEntryId());

			Assert.assertEquals(
				urlTitle, friendlyURLEntry1.getUrlTitle(languageId));
			Assert.assertEquals(
				urlTitle + "-1", friendlyURLEntry2.getUrlTitle(languageId));
		}

		_ctCollectionLocalService.deleteCTCollection(conflictCTCollection);
	}

	@Test
	public void testUpdateFileEntry() throws Exception {
		FileEntry fileEntry = _addFileEntry();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(fileEntry.getGroupId());

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			_dlAppService.updateFileEntry(
				fileEntry.getFileEntryId(), fileEntry.getFileName(),
				fileEntry.getMimeType(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), fileEntry.getDescription(),
				RandomTestUtil.randomString(), DLVersionNumberIncrease.MINOR,
				null, fileEntry.getSize(), fileEntry.getDisplayDate(),
				fileEntry.getExpirationDate(), fileEntry.getReviewDate(),
				serviceContext);
		}

		fileEntry = _dlAppService.getFileEntry(fileEntry.getFileEntryId());

		Assert.assertFalse(fileEntry.isCheckedOut());
	}

	private FileEntry _addFileEntry() throws Exception {
		return _addFileEntry(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());
	}

	private FileEntry _addFileEntry(String fileName, String title)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		Folder folder = _dlAppLocalService.addFolder(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);

		return _dlAppLocalService.addFileEntry(
			null, serviceContext.getUserId(), folder.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, fileName,
			ContentTypes.TEXT_PLAIN, title, StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, "liferay".getBytes(), null, null, null,
			serviceContext);
	}

	private void _assertHasFiles(String friendlyURL) throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest(Method.GET, "/documents" + friendlyURL);

		mockHttpServletRequest.setAttribute(
			WebKeys.USER, TestPropsValues.getUser());
		mockHttpServletRequest.setContextPath("/documents");
		mockHttpServletRequest.setParameter(
			"previewCTCollectionId",
			String.valueOf(_ctCollection.getCtCollectionId()));
		mockHttpServletRequest.setPathInfo(friendlyURL);
		mockHttpServletRequest.setServletPath(StringPool.BLANK);

		Assert.assertTrue(WebServerServlet.hasFiles(mockHttpServletRequest));
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@DeleteAfterTestRun
	private CTCollection _ctCollection;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private CTEntryLocalService _ctEntryLocalService;

	@Inject
	private CTProcessLocalService _ctProcessLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLAppService _dlAppService;

	@Inject
	private DLFolderService _dlFolderService;

	@Inject
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

}