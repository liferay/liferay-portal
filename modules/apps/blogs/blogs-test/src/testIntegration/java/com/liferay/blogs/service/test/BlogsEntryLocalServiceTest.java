/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.exception.AssetCategoryException;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.blogs.attachments.test.BlogsEntryAttachmentFileEntryHelperTest;
import com.liferay.blogs.constants.BlogsConstants;
import com.liferay.blogs.exception.EntryContentException;
import com.liferay.blogs.exception.EntrySmallImageNameException;
import com.liferay.blogs.exception.EntryTitleException;
import com.liferay.blogs.exception.EntryUrlTitleException;
import com.liferay.blogs.exception.NoSuchEntryException;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.blogs.test.util.BlogsTestUtil;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.friendly.url.exception.DuplicateFriendlyURLEntryException;
import com.liferay.message.boards.constants.MBMessageConstants;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBMessageDisplay;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.service.MBMessageLocalServiceUtil;
import com.liferay.message.boards.test.util.MBTestUtil;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.comment.CommentManagerUtil;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.IdentityServiceContextFunction;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.mail.MailServiceTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.SynchronousMailTestRule;
import com.liferay.portlet.asset.util.AssetVocabularySettingsHelper;
import com.liferay.subscription.service.SubscriptionLocalServiceUtil;

import java.io.InputStream;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Cristina González
 * @author Manuel de la Peña
 */
@RunWith(Arquillian.class)
public class BlogsEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), SynchronousMailTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			BlogsEntryAttachmentFileEntryHelperTest.class);

		bundle = BundleUtil.getBundle(
			bundle.getBundleContext(), "com.liferay.blogs.web");

		Class<?> clazz = bundle.loadClass(
			"com.liferay.blogs.web.internal.util.BlogsUtil");

		_getUrlTitleMethod = clazz.getMethod(
			"getUrlTitle", Long.TYPE, String.class);
	}

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
		_user = TestPropsValues.getUser();

		UserTestUtil.setUser(TestPropsValues.getUser());
	}

	@Test
	public void testAddCoverImageWithCoverImageURL() throws Exception {
		BlogsEntry entry = addEntry(false);

		String coverImageURL = StringUtil.randomString();

		_blogsEntryLocalService.addCoverImage(
			entry.getEntryId(), new ImageSelector(coverImageURL));

		BlogsEntry updatedEntry = _blogsEntryLocalService.getEntry(
			entry.getEntryId());

		Assert.assertEquals(0, updatedEntry.getCoverImageFileEntryId());
		Assert.assertEquals(coverImageURL, updatedEntry.getCoverImageURL());
		Assert.assertFalse(updatedEntry.isSmallImage());
	}

	@Test
	public void testAddCoverImageWithImageBytes() throws Exception {
		BlogsEntry entry = addEntry(false);

		byte[] bytes = FileUtil.getBytes(
			new UnsyncByteArrayInputStream(TestDataConstants.TEST_BYTE_ARRAY));

		_blogsEntryLocalService.addCoverImage(
			entry.getEntryId(),
			new ImageSelector(
				bytes, StringUtil.randomString() + ".bin",
				ContentTypes.APPLICATION_OCTET_STREAM, StringPool.BLANK));

		BlogsEntry updatedEntry = _blogsEntryLocalService.getEntry(
			entry.getEntryId());

		Assert.assertNotEquals(0, updatedEntry.getCoverImageFileEntryId());
		Assert.assertEquals(StringPool.BLANK, updatedEntry.getCoverImageURL());
		Assert.assertFalse(updatedEntry.isSmallImage());
	}

	@Test
	public void testAddCoverImageWithURL() throws Exception {
		BlogsEntry entry = addEntry(false);

		String imageURL = StringUtil.randomString();

		_blogsEntryLocalService.addCoverImage(
			entry.getEntryId(), new ImageSelector(imageURL));

		BlogsEntry updatedEntry = _blogsEntryLocalService.getEntry(
			entry.getEntryId());

		Assert.assertEquals(0, updatedEntry.getCoverImageFileEntryId());
		Assert.assertEquals(imageURL, updatedEntry.getCoverImageURL());
	}

	@Test
	public void testAddDiscussion() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		BlogsEntry entry = _blogsEntryLocalService.addEntry(
			TestPropsValues.getUserId(), StringUtil.randomString(),
			StringUtil.randomString(), new Date(), serviceContext);

		_blogsEntries.add(entry);

		long initialCommentsCount = CommentManagerUtil.getCommentsCount(
			BlogsEntry.class.getName(), entry.getEntryId());

		CommentManagerUtil.addComment(
			TestPropsValues.getUserId(), _group.getGroupId(),
			BlogsEntry.class.getName(), entry.getEntryId(),
			StringUtil.randomString(),
			new IdentityServiceContextFunction(serviceContext));

		Assert.assertEquals(
			initialCommentsCount + 1,
			CommentManagerUtil.getCommentsCount(
				BlogsEntry.class.getName(), entry.getEntryId()));
	}

	@Test
	public void testAddDraftEntryWithBlankTitle() throws Exception {
		int initialCount = _blogsEntryLocalService.getGroupEntriesCount(
			_group.getGroupId(), _statusAnyQueryDefinition);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group, _user.getUserId());

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		_blogsEntryLocalService.addEntry(
			_user.getUserId(), StringPool.BLANK, RandomTestUtil.randomString(),
			serviceContext);

		int actualCount = _blogsEntryLocalService.getGroupEntriesCount(
			_group.getGroupId(), _statusAnyQueryDefinition);

		Assert.assertEquals(initialCount + 1, actualCount);
	}

	@Test
	public void testAddDraftEntryWithDuplicateURLTitle() throws Exception {
		String urlTitle = RandomTestUtil.randomString();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group, _user.getUserId());

		_blogsEntryLocalService.addEntry(
			RandomTestUtil.randomString(), _user.getUserId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			urlTitle, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), new Date(), false, false, null, null,
			null, null, serviceContext);

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		BlogsEntry entry = _blogsEntryLocalService.addEntry(
			RandomTestUtil.randomString(), _user.getUserId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			urlTitle, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), new Date(), false, false, null, null,
			null, null, serviceContext);

		Assert.assertNotEquals(urlTitle, entry.getUrlTitle());
	}

	@Test
	public void testAddDraftEntryWithNullTitle() throws Exception {
		int initialCount = _blogsEntryLocalService.getGroupEntriesCount(
			_group.getGroupId(), _statusAnyQueryDefinition);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group, _user.getUserId());

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		_blogsEntryLocalService.addEntry(
			_user.getUserId(), null, RandomTestUtil.randomString(),
			serviceContext);

		int actualCount = _blogsEntryLocalService.getGroupEntriesCount(
			_group.getGroupId(), _statusAnyQueryDefinition);

		Assert.assertEquals(initialCount + 1, actualCount);
	}

	@Test
	public void testAddDuplicateAttachmentFileEntry() throws Exception {
		String fileName = StringUtil.randomString();

		FileEntry fileEntry1 = _addAttachmentFileEntry(null, fileName);

		FileEntry fileEntry2 = _addAttachmentFileEntry(null, fileName);

		Assert.assertNotEquals(
			fileEntry1.getFileName(), fileEntry2.getFileName());

		Assert.assertEquals(
			2,
			PortletFileRepositoryUtil.getPortletFileEntriesCount(
				fileEntry2.getGroupId(), fileEntry2.getFolderId()));
	}

	@Test
	public void testAddEmptyCoverImage() throws Exception {
		BlogsEntry entry = addEntry(false);

		_blogsEntryLocalService.addCoverImage(
			entry.getEntryId(), new ImageSelector());

		BlogsEntry updatedEntry = _blogsEntryLocalService.getEntry(
			entry.getEntryId());

		Assert.assertEquals(0, updatedEntry.getCoverImageFileEntryId());
		Assert.assertEquals(StringPool.BLANK, updatedEntry.getCoverImageURL());
	}

	@Test
	public void testAddEmptyOriginalImageFileEntry() throws Exception {
		BlogsEntry entry = addEntry(false);

		Assert.assertEquals(
			0,
			_blogsEntryLocalService.addOriginalImageFileEntry(
				entry.getUserId(), entry.getGroupId(), entry.getEntryId(),
				new ImageSelector()));
	}

	@Test
	public void testAddEmptySmallImage() throws Exception {
		BlogsEntry entry = addEntry(false);

		_blogsEntryLocalService.addSmallImage(
			entry.getEntryId(), new ImageSelector());

		BlogsEntry updatedEntry = _blogsEntryLocalService.getEntry(
			entry.getEntryId());

		Assert.assertEquals(0, updatedEntry.getSmallImageFileEntryId());
		Assert.assertEquals(StringPool.BLANK, updatedEntry.getCoverImageURL());
		Assert.assertFalse(updatedEntry.isSmallImage());
	}

	@Test
	public void testAddEntry() throws Exception {
		int initialCount = _blogsEntryLocalService.getGroupEntriesCount(
			_group.getGroupId(), _statusApprovedQueryDefinition);

		addEntry(false);

		int actualCount = _blogsEntryLocalService.getGroupEntriesCount(
			_group.getGroupId(), _statusApprovedQueryDefinition);

		Assert.assertEquals(initialCount + 1, actualCount);
	}

	@Test
	public void testAddEntrySubscribesCreatorWhenSubscribeBlogsEntryCreatorToCommentsEnabled()
		throws Exception {

		_creatorUser = UserTestUtil.addUser();

		_withSubscribeBlogsEntryCreatorToCommentsEnabled(
			() -> {
				ServiceContext serviceContext =
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId(), _creatorUser.getUserId());

				BlogsEntry entry = _blogsEntryLocalService.addEntry(
					_creatorUser.getUserId(), RandomTestUtil.randomString(),
					RandomTestUtil.randomString(), serviceContext);

				_addMBMessage(
					TestPropsValues.getUserId(), serviceContext, entry);

				Assert.assertEquals(1, MailServiceTestUtil.getInboxSize());
			});
	}

	@Test(expected = AssetCategoryException.class)
	public void testAddEntryWithAssetCategoriesFromNonmultiValuedAssetVocabulary()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group, _user.getUserId());

		AssetVocabularySettingsHelper assetVocabularySettingsHelper =
			_getAssetVocabularySettingsHelper(
				false, new long[] {AssetCategoryConstants.ALL_CLASS_NAME_ID},
				new long[] {AssetCategoryConstants.ALL_CLASS_TYPE_PK},
				new boolean[] {false}, new boolean[] {false});

		Assert.assertFalse(assetVocabularySettingsHelper.isMultiValued());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				_user.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(),
				HashMapBuilder.put(
					LocaleUtil.US, RandomTestUtil.randomString()
				).build(),
				null, assetVocabularySettingsHelper.toString(), serviceContext);

		AssetCategory assetCategory1 = _assetCategoryLocalService.addCategory(
			_user.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);
		AssetCategory assetCategory2 = _assetCategoryLocalService.addCategory(
			_user.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);

		serviceContext.setAssetCategoryIds(
			new long[] {
				assetCategory1.getCategoryId(), assetCategory2.getCategoryId()
			});

		_blogsEntryLocalService.addEntry(
			RandomTestUtil.randomString(), _user.getUserId(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), new Date(), true, true, new String[0],
			null, null, null, serviceContext);
	}

	@Test
	public void testAddEntryWithDuplicatedAutogeneratedFriendlyURL()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group, _user.getUserId());

		String title = "title";

		_blogsEntryLocalService.addEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), title,
			StringUtil.randomString(), StringPool.BLANK,
			StringUtil.randomString(), StringUtil.randomString(), new Date(),
			true, true, new String[0], null, null, null, serviceContext);

		BlogsEntry entry = _blogsEntryLocalService.addEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), title,
			StringUtil.randomString(), StringPool.BLANK,
			StringUtil.randomString(), StringUtil.randomString(), new Date(),
			true, true, new String[0], null, null, null, serviceContext);

		Assert.assertEquals("title-1", entry.getUrlTitle());
	}

	@Test(expected = DuplicateFriendlyURLEntryException.class)
	public void testAddEntryWithDuplicatedCategorizedFriendlyURL()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group, _user.getUserId());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), serviceContext);

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);

		serviceContext.setAttribute(
			"friendlyURLAssetCategoryIds",
			new long[] {assetCategory.getCategoryId()});

		String urlTitle = "urlTitle";

		_blogsEntryLocalService.addEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			StringUtil.randomString(), StringUtil.randomString(), urlTitle,
			StringUtil.randomString(), StringUtil.randomString(), new Date(),
			true, true, new String[0], null, null, null, serviceContext);

		_blogsEntryLocalService.addEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			StringUtil.randomString(), StringUtil.randomString(), urlTitle,
			StringUtil.randomString(), StringUtil.randomString(), new Date(),
			true, true, new String[0], null, null, null, serviceContext);
	}

	@Test(expected = EntryUrlTitleException.class)
	public void testAddEntryWithInvalidURLTitle() throws Exception {
		_blogsEntryLocalService.addEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(256), StringUtil.randomString(),
			StringUtil.randomString(), new Date(), true, true, new String[0],
			null, null, null, new ServiceContext());
	}

	@Test
	public void testAddEntryWithNoImages() throws Exception {
		BlogsEntry entry = _blogsEntryLocalService.addEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), new Date(), true, true, new String[0],
			null, new ImageSelector(), new ImageSelector(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(0, entry.getCoverImageFileEntryId());
		Assert.assertEquals(StringPool.BLANK, entry.getCoverImageURL());
		Assert.assertEquals(0, entry.getSmallImageFileEntryId());
		Assert.assertEquals(StringPool.BLANK, entry.getSmallImageURL());
		Assert.assertFalse(entry.isSmallImage());
	}

	@Test
	public void testAddEntryWithURLTitle() throws Exception {
		String urlTitle = StringUtil.toLowerCase(StringUtil.randomString());

		BlogsEntry entry = _blogsEntryLocalService.addEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			StringUtil.randomString(), StringUtil.randomString(), urlTitle,
			StringUtil.randomString(), StringUtil.randomString(), new Date(),
			true, true, new String[0], null, null, null,
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(urlTitle, entry.getUrlTitle());
	}

	@Test
	public void testAddEntryWithURLTitleWithSlashPrefix() throws Exception {
		String expectedUrlTitle = StringUtil.toLowerCase(
			StringUtil.randomString());

		String bakedUrlTitle = "///////" + expectedUrlTitle;

		BlogsEntry entry = _blogsEntryLocalService.addEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			StringUtil.randomString(), StringUtil.randomString(), bakedUrlTitle,
			StringUtil.randomString(), StringUtil.randomString(), new Date(),
			true, true, new String[0], null, null, null,
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(expectedUrlTitle, entry.getUrlTitle());
	}

	@Test(expected = EntryContentException.class)
	public void testAddEntryWithVeryLongContent() throws Exception {
		int maxLength = ModelHintsUtil.getMaxLength(
			BlogsEntry.class.getName(), "content");

		String content = _repeat("0", maxLength + 1);

		_blogsEntryLocalService.addEntry(
			_user.getUserId(), RandomTestUtil.randomString(), content,
			ServiceContextTestUtil.getServiceContext(
				_group, _user.getUserId()));
	}

	@Test(expected = EntryTitleException.class)
	public void testAddEntryWithVeryLongTitle() throws Exception {
		int maxLength = ModelHintsUtil.getMaxLength(
			BlogsEntry.class.getName(), "title");

		String title = _repeat("0", maxLength + 1);

		_blogsEntryLocalService.addEntry(
			_user.getUserId(), title, RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				_group, _user.getUserId()));
	}

	@Test
	public void testAddNullCoverImage() throws Exception {
		BlogsEntry entry = addEntry(false);

		_blogsEntryLocalService.addCoverImage(entry.getEntryId(), null);

		BlogsEntry updatedEntry = _blogsEntryLocalService.getEntry(
			entry.getEntryId());

		Assert.assertEquals(0, updatedEntry.getCoverImageFileEntryId());
		Assert.assertEquals(StringPool.BLANK, updatedEntry.getCoverImageURL());
		Assert.assertFalse(updatedEntry.isSmallImage());
	}

	@Test
	public void testAddNullSmallImage() throws Exception {
		BlogsEntry entry = addEntry(false);

		_blogsEntryLocalService.addSmallImage(entry.getEntryId(), null);

		BlogsEntry updatedEntry = _blogsEntryLocalService.getEntry(
			entry.getEntryId());

		Assert.assertEquals(0, updatedEntry.getSmallImageFileEntryId());
		Assert.assertEquals(StringPool.BLANK, updatedEntry.getCoverImageURL());
		Assert.assertFalse(updatedEntry.isSmallImage());
	}

	@Test
	public void testAddOriginalImageInVisibleImageFolder() throws Exception {
		BlogsEntry entry = _blogsEntryLocalService.addEntry(
			_user.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId()));

		FileEntry tempFileEntry = getTempFileEntry(
			_user.getUserId(), _group.getGroupId(), "image.jpg");

		ImageSelector imageSelector = new ImageSelector(
			FileUtil.getBytes(tempFileEntry.getContentStream()),
			tempFileEntry.getTitle(), tempFileEntry.getMimeType(),
			StringPool.BLANK);

		long originalImageFileEntryId =
			_blogsEntryLocalService.addOriginalImageFileEntry(
				_user.getUserId(), _group.getGroupId(), entry.getEntryId(),
				imageSelector);

		FileEntry portletFileEntry =
			PortletFileRepositoryUtil.getPortletFileEntry(
				originalImageFileEntryId);

		DLFileEntry dlFileEntry = (DLFileEntry)portletFileEntry.getModel();

		Assert.assertEquals(StringPool.BLANK, dlFileEntry.getClassName());
		Assert.assertEquals(0, dlFileEntry.getClassPK());

		Folder folder = portletFileEntry.getFolder();

		Assert.assertEquals(BlogsConstants.SERVICE_NAME, folder.getName());
	}

	@Test(expected = EntrySmallImageNameException.class)
	public void testAddSmallImageWithNotSupportedExtension() throws Exception {
		BlogsEntry entry = addEntry(false);

		FileEntry fileEntry = getTempFileEntry(
			_user.getUserId(), _group.getGroupId(), "image1.svg");

		ImageSelector imageSelector = new ImageSelector(
			FileUtil.getBytes(fileEntry.getContentStream()),
			fileEntry.getTitle(), fileEntry.getMimeType(), StringPool.BLANK);

		_blogsEntryLocalService.addSmallImage(
			entry.getEntryId(), imageSelector);
	}

	@Test
	public void testAddSmallImageWithSmallImageURL() throws Exception {
		BlogsEntry entry = addEntry(false);

		String imageURL = StringUtil.randomString();

		_blogsEntryLocalService.addSmallImage(
			entry.getEntryId(), new ImageSelector(imageURL));

		BlogsEntry updatedEntry = _blogsEntryLocalService.getEntry(
			entry.getEntryId());

		Assert.assertEquals(0, updatedEntry.getSmallImageFileEntryId());
		Assert.assertEquals(imageURL, updatedEntry.getSmallImageURL());
		Assert.assertTrue(updatedEntry.isSmallImage());
	}

	@Test
	public void testDeleteAttachmentFileEntry() throws Exception {
		FileEntry fileEntry = _addAttachmentFileEntry(
			null, StringUtil.randomString());

		_blogsEntryLocalService.deleteAttachmentFileEntry(
			fileEntry.getFileEntryId());

		try {
			_blogsEntryLocalService.getAttachmentFileEntry(
				fileEntry.getFileEntryId());

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertTrue(exception instanceof NoSuchFileEntryException);
		}
	}

	@Test
	public void testDeleteDiscussion() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		BlogsEntry entry = _blogsEntryLocalService.addEntry(
			TestPropsValues.getUserId(), StringUtil.randomString(),
			StringUtil.randomString(), new Date(), serviceContext);

		_blogsEntries.add(entry);

		CommentManagerUtil.addComment(
			TestPropsValues.getUserId(), _group.getGroupId(),
			BlogsEntry.class.getName(), entry.getEntryId(),
			StringUtil.randomString(),
			new IdentityServiceContextFunction(serviceContext));

		Assert.assertTrue(
			CommentManagerUtil.hasDiscussion(
				BlogsEntry.class.getName(), entry.getEntryId()));

		CommentManagerUtil.deleteDiscussion(
			BlogsEntry.class.getName(), entry.getEntryId());

		Assert.assertFalse(
			CommentManagerUtil.hasDiscussion(
				BlogsEntry.class.getName(), entry.getEntryId()));
	}

	@Test
	public void testDeleteEntry() throws Exception {
		BlogsEntry entry = addEntry(false);

		_blogsEntryLocalService.deleteEntry(entry);

		try {
			_blogsEntryLocalService.getEntry(entry.getEntryId());

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertTrue(exception instanceof NoSuchEntryException);
		}
	}

	@Test
	public void testFetchNotNullAttachmentsFolder() throws Exception {
		BlogsEntry entry = addEntry(false);

		byte[] bytes = null;

		try (InputStream inputStream = new UnsyncByteArrayInputStream(
				TestDataConstants.TEST_BYTE_ARRAY)) {

			bytes = FileUtil.getBytes(inputStream);
		}

		_blogsEntryLocalService.addOriginalImageFileEntry(
			entry.getUserId(), entry.getGroupId(), entry.getEntryId(),
			new ImageSelector(
				bytes, StringUtil.randomString() + ".bin",
				ContentTypes.APPLICATION_OCTET_STREAM, StringPool.BLANK));

		Assert.assertNotNull(
			_blogsEntryLocalService.fetchAttachmentsFolder(
				entry.getUserId(), entry.getGroupId()));
	}

	@Test
	public void testFetchNullAttachmentsFolder() throws Exception {
		Assert.assertNull(
			_blogsEntryLocalService.fetchAttachmentsFolder(
				TestPropsValues.getUserId(), _group.getGroupId()));
	}

	@Test
	public void testGetAttachmentFileEntry() throws Exception {
		FileEntry fileEntry1 = _addAttachmentFileEntry(
			null, StringUtil.randomString());

		FileEntry fileEntry2 = _blogsEntryLocalService.getAttachmentFileEntry(
			fileEntry1.getFileEntryId());

		Assert.assertEquals(fileEntry1, fileEntry2);
	}

	@Test
	public void testGetAttachmentFileEntryByExternalReferenceCode()
		throws Exception {

		String externalReferenceCode = StringUtil.randomString();

		FileEntry fileEntry1 = _addAttachmentFileEntry(
			externalReferenceCode, StringUtil.randomString());

		Assert.assertEquals(
			externalReferenceCode, fileEntry1.getExternalReferenceCode());

		FileEntry fileEntry2 =
			_blogsEntryLocalService.
				getAttachmentFileEntryByExternalReferenceCode(
					externalReferenceCode, fileEntry1.getGroupId());

		Assert.assertEquals(fileEntry1, fileEntry2);
	}

	@Test
	public void testGetCompanyEntriesCountInTrash() throws Exception {
		testGetCompanyEntriesCount(true);
	}

	@Test
	public void testGetCompanyEntriesCountNotInTrash() throws Exception {
		testGetCompanyEntriesCount(false);
	}

	@Test
	public void testGetCompanyEntriesInTrash() throws Exception {
		testGetCompanyEntries(true);
	}

	@Test
	public void testGetCompanyEntriesNotInTrash() throws Exception {
		testGetCompanyEntries(false);
	}

	@Test
	public void testGetDiscussionMessageDisplay() throws Exception {
		BlogsEntry entry = addEntry(false);

		MBMessageLocalServiceUtil.getDiscussionMessageDisplay(
			_user.getUserId(), _group.getGroupId(), BlogsEntry.class.getName(),
			entry.getEntryId(), WorkflowConstants.STATUS_ANY);
	}

	@Test
	public void testGetEntriesPrevAndNextByDisplayDate() throws Exception {
		BlogsEntry firstEntry = addEntry(false, 1);

		BlogsEntry thirdEntry = addEntry(false, 3);

		BlogsEntry secondEntry = addEntry(false, 2);

		BlogsEntry[] entries = _blogsEntryLocalService.getEntriesPrevAndNext(
			secondEntry.getEntryId());

		Assert.assertNotNull(
			StringBundler.concat(
				"The previous entry relative to entry ",
				secondEntry.getEntryId(), " should be ",
				firstEntry.getEntryId(), " but is null"),
			entries[0]);
		Assert.assertNotNull(
			StringBundler.concat(
				"The current entry relative to entry ",
				secondEntry.getEntryId(), " should be ",
				secondEntry.getEntryId(), " but is null"),
			entries[1]);
		Assert.assertNotNull(
			StringBundler.concat(
				"The next entry relative to entry ", secondEntry.getEntryId(),
				" should be ", thirdEntry.getEntryId(), " but is null"),
			entries[2]);
		Assert.assertEquals(
			StringBundler.concat(
				"The previous entry relative to entry ",
				secondEntry.getEntryId(), " should be ",
				firstEntry.getEntryId()),
			entries[0].getEntryId(), firstEntry.getEntryId());
		Assert.assertEquals(
			StringBundler.concat(
				"The current entry relative to entry ",
				secondEntry.getEntryId(), " should be ",
				secondEntry.getEntryId()),
			entries[1].getEntryId(), secondEntry.getEntryId());
		Assert.assertEquals(
			StringBundler.concat(
				"The next entry relative to entry ", secondEntry.getEntryId(),
				" should be ", thirdEntry.getEntryId()),
			entries[2].getEntryId(), thirdEntry.getEntryId());
	}

	@Test
	public void testGetEntriesPrevAndNextRelativeToCurrentEntry()
		throws Exception {

		BlogsEntry previousEntry = addEntry(false);

		BlogsEntry currentEntry = addEntry(false);

		BlogsEntry nextEntry = addEntry(false);

		BlogsEntry[] entries = _blogsEntryLocalService.getEntriesPrevAndNext(
			currentEntry.getEntryId());

		Assert.assertNotNull(
			StringBundler.concat(
				"The previous entry relative to entry ",
				currentEntry.getEntryId(), " should be ",
				previousEntry.getEntryId(), " but is null"),
			entries[0]);
		Assert.assertNotNull(
			StringBundler.concat(
				"The current entry relative to entry ",
				currentEntry.getEntryId(), " should be ",
				currentEntry.getEntryId(), " but is null"),
			entries[1]);
		Assert.assertNotNull(
			StringBundler.concat(
				"The next entry relative to entry ", currentEntry.getEntryId(),
				" should be ", nextEntry.getEntryId(), " but is null"),
			entries[2]);
		Assert.assertEquals(
			StringBundler.concat(
				"The previous entry relative to entry",
				currentEntry.getEntryId(), " should be ",
				previousEntry.getEntryId()),
			entries[0].getEntryId(), previousEntry.getEntryId());
		Assert.assertEquals(
			StringBundler.concat(
				"The current entry relative to entry ",
				currentEntry.getEntryId(), " should be ",
				currentEntry.getEntryId()),
			entries[1].getEntryId(), currentEntry.getEntryId());
		Assert.assertEquals(
			StringBundler.concat(
				"The next entry relative to entry ", currentEntry.getEntryId(),
				" should be ", nextEntry.getEntryId()),
			entries[2].getEntryId(), nextEntry.getEntryId());
	}

	@Test
	public void testGetEntriesPrevAndNextRelativeToNextEntry()
		throws Exception {

		addEntry(false);

		BlogsEntry currentEntry = addEntry(false);

		BlogsEntry nextEntry = addEntry(false);

		BlogsEntry[] entries = _blogsEntryLocalService.getEntriesPrevAndNext(
			nextEntry.getEntryId());

		Assert.assertNull(
			"The next entry relative to entry " + nextEntry.getEntryId() +
				" should be null",
			entries[2]);
		Assert.assertNotNull(
			StringBundler.concat(
				"The current entry relative to entry ", nextEntry.getEntryId(),
				" should be ", nextEntry.getEntryId(), " but is null"),
			entries[1]);
		Assert.assertNotNull(
			StringBundler.concat(
				"The previous entry relative to entry ", nextEntry.getEntryId(),
				" should be ", currentEntry.getEntryId(), " but is null"),
			entries[0]);
		Assert.assertEquals(
			StringBundler.concat(
				"The previous entry relative to entry ", nextEntry.getEntryId(),
				" should be ", currentEntry.getEntryId()),
			entries[0].getEntryId(), currentEntry.getEntryId());
		Assert.assertEquals(
			StringBundler.concat(
				"The current entry relative to entry", nextEntry.getEntryId(),
				" should be ", nextEntry.getEntryId()),
			entries[1].getEntryId(), nextEntry.getEntryId());
	}

	@Test
	public void testGetEntriesPrevAndNextRelativeToPreviousEntry()
		throws Exception {

		BlogsEntry previousEntry = addEntry(false);

		BlogsEntry currentEntry = addEntry(false);

		addEntry(false);

		BlogsEntry[] entries = _blogsEntryLocalService.getEntriesPrevAndNext(
			previousEntry.getEntryId());

		Assert.assertNull(
			"The previous entry relative to entry " +
				previousEntry.getEntryId() + " should be null",
			entries[0]);
		Assert.assertNotNull(
			StringBundler.concat(
				"The current entry relative to entry ",
				previousEntry.getEntryId(), " should be ",
				previousEntry.getEntryId(), " but is null"),
			entries[1]);
		Assert.assertNotNull(
			StringBundler.concat(
				"The next entry relative to entry ", previousEntry.getEntryId(),
				" should be ", currentEntry.getEntryId(), " but is null"),
			entries[2]);
		Assert.assertEquals(
			StringBundler.concat(
				"The current entry relative to entry ",
				previousEntry.getEntryId(), " should be ",
				previousEntry.getEntryId()),
			entries[1].getEntryId(), previousEntry.getEntryId());
		Assert.assertEquals(
			StringBundler.concat(
				"The next entry relative to entry ", previousEntry.getEntryId(),
				" should be ", currentEntry.getEntryId()),
			entries[2].getEntryId(), currentEntry.getEntryId());
	}

	@Test
	public void testGetEntryByGroupAndOldUrlTitle() throws Exception {
		BlogsEntry expectedEntry = addEntry(false);

		String oldUrlTitle = expectedEntry.getUrlTitle();

		String urlTitle = "new-friendly-url";

		_blogsEntryLocalService.updateEntry(
			expectedEntry.getUserId(), expectedEntry.getEntryId(),
			expectedEntry.getTitle(), expectedEntry.getSubtitle(), urlTitle,
			expectedEntry.getDescription(), expectedEntry.getContent(),
			expectedEntry.getDisplayDate(), expectedEntry.isAllowPingbacks(),
			expectedEntry.isAllowTrackbacks(), new String[0],
			expectedEntry.getCoverImageCaption(), null, null,
			ServiceContextTestUtil.getServiceContext(
				_group, _user.getUserId()));

		BlogsEntry actualEntry = _blogsEntryLocalService.getEntry(
			expectedEntry.getGroupId(), oldUrlTitle);

		BlogsTestUtil.assertEquals(expectedEntry, actualEntry);

		actualEntry = _blogsEntryLocalService.getEntry(
			expectedEntry.getGroupId(), urlTitle);

		BlogsTestUtil.assertEquals(expectedEntry, actualEntry);
	}

	@Test
	public void testGetEntryByGroupAndUrlTitle() throws Exception {
		BlogsEntry expectedEntry = addEntry(false);

		BlogsEntry actualEntry = _blogsEntryLocalService.getEntry(
			expectedEntry.getGroupId(), expectedEntry.getUrlTitle());

		BlogsTestUtil.assertEquals(expectedEntry, actualEntry);
	}

	@Test
	public void testGetGroupEntriesCountInTrashWithDisplayDate()
		throws Exception {

		testGetGroupEntriesCount(true, true);
	}

	@Test
	public void testGetGroupEntriesCountInTrashWithoutDisplayDate()
		throws Exception {

		testGetGroupEntriesCount(true, false);
	}

	@Test
	public void testGetGroupEntriesCountNotInTrashWithDisplayDate()
		throws Exception {

		testGetGroupEntriesCount(false, true);
	}

	@Test
	public void testGetGroupEntriesCountNotInTrashWithoutDisplayDate()
		throws Exception {

		testGetGroupEntriesCount(false, false);
	}

	@Test
	public void testGetGroupEntriesInTrashWithDisplayDate() throws Exception {
		testGetGroupEntries(true, true);
	}

	@Test
	public void testGetGroupEntriesInTrashWithoutDisplayDate()
		throws Exception {

		testGetGroupEntries(true, false);
	}

	@Test
	public void testGetGroupEntriesNotInTrashWithDisplayDate()
		throws Exception {

		testGetGroupEntries(false, true);
	}

	@Test
	public void testGetGroupEntriesNotInTrashWithoutDisplayDate()
		throws Exception {

		testGetGroupEntries(false, false);
	}

	@Test
	public void testGetGroupUserEntriesCountInTrash() throws Exception {
		testGetGroupUserEntriesCount(true);
	}

	@Test
	public void testGetGroupUserEntriesCountNotInTrash() throws Exception {
		testGetGroupUserEntriesCount(false);
	}

	@Test
	public void testGetGroupUserEntriesInTrash() throws Exception {
		testGetGroupUserEntries(true);
	}

	@Test
	public void testGetGroupUserEntriesNotInTrash() throws Exception {
		testGetGroupUserEntries(false);
	}

	@Test
	public void testGetGroupsEntries() throws Exception {
		List<BlogsEntry> initialBlogsEntries =
			_blogsEntryLocalService.getGroupsEntries(
				_user.getCompanyId(), _group.getGroupId(), new Date(),
				_statusInTrashQueryDefinition);

		int initialCount = initialBlogsEntries.size();

		addEntry(false);
		addEntry(true);

		List<BlogsEntry> actualBlogsEntries =
			_blogsEntryLocalService.getGroupsEntries(
				_user.getCompanyId(), _group.getGroupId(), new Date(),
				_statusInTrashQueryDefinition);

		Assert.assertEquals(
			actualBlogsEntries.toString(), initialCount + 1,
			actualBlogsEntries.size());

		for (BlogsEntry actualBlogsEntry : actualBlogsEntries) {
			Assert.assertEquals(
				"Entry " + actualBlogsEntry.getEntryId() + " is not in trash",
				WorkflowConstants.STATUS_IN_TRASH,
				actualBlogsEntry.getStatus());
			Assert.assertEquals(
				StringBundler.concat(
					"Entry belongs to company ",
					actualBlogsEntry.getCompanyId(),
					" but should belong to company ", _user.getCompanyId()),
				_user.getCompanyId(), actualBlogsEntry.getCompanyId());
		}
	}

	@Test
	public void testGetOrganizationEntriesCountInTrash() throws Exception {
		testGetOrganizationEntriesCount(true);
	}

	@Test
	public void testGetOrganizationEntriesCountNotInTrash() throws Exception {
		testGetOrganizationEntriesCount(false);
	}

	@Test
	public void testGetOrganizationEntriesInTrash() throws Exception {
		testGetOrganizationEntries(true);
	}

	@Test
	public void testGetOrganizationEntriesNotInTrash() throws Exception {
		testGetOrganizationEntries(false);
	}

	@Test(expected = EntryTitleException.class)
	public void testPublishWithBlankTitle() throws Exception {
		_blogsEntryLocalService.addEntry(
			_user.getUserId(), StringPool.BLANK, RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				_group, _user.getUserId()));
	}

	@Test(expected = EntryTitleException.class)
	public void testPublishWithNullTitle() throws Exception {
		_blogsEntryLocalService.addEntry(
			_user.getUserId(), null, RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				_group, _user.getUserId()));
	}

	@Test(expected = EntryTitleException.class)
	public void testPublishWithoutTitle() throws Exception {
		_blogsEntryLocalService.addEntry(
			_user.getUserId(), StringPool.BLANK, RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				_group, _user.getUserId()));
	}

	@Test
	public void testSubscribe() throws Exception {
		int initialCount =
			SubscriptionLocalServiceUtil.getUserSubscriptionsCount(
				_user.getUserId());

		_blogsEntryLocalService.subscribe(
			_user.getUserId(), _group.getGroupId());

		int actualCount =
			SubscriptionLocalServiceUtil.getUserSubscriptionsCount(
				_user.getUserId());

		Assert.assertEquals(initialCount + 1, actualCount);
	}

	@Test
	public void testURLTitleIsNotUpdatedWhenUpdatingEntryTitle()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group, _user.getUserId());

		BlogsEntry entry = _blogsEntryLocalService.addEntry(
			_user.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), serviceContext);

		String urlTitle = entry.getUrlTitle();

		entry = _blogsEntryLocalService.updateEntry(
			_user.getUserId(), entry.getEntryId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);

		Assert.assertEquals(urlTitle, entry.getUrlTitle());
	}

	@Test
	public void testURLTitleIsNotUpdatedWhenUpdatingEntryTitleToDraftEntry()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group, _user.getUserId());

		BlogsEntry entry = _blogsEntryLocalService.addEntry(
			_user.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), serviceContext);

		String urlTitle = entry.getUrlTitle();

		serviceContext.setWorkflowAction(WorkflowConstants.STATUS_DRAFT);

		entry = _blogsEntryLocalService.updateEntry(
			_user.getUserId(), entry.getEntryId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);

		Assert.assertEquals(urlTitle, entry.getUrlTitle());
	}

	@Test
	public void testURLTitleIsSavedWhenAddingApprovedEntry() throws Exception {
		String title = RandomTestUtil.randomString();

		BlogsEntry entry = _blogsEntryLocalService.addEntry(
			_user.getUserId(), title, RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				_group, _user.getUserId()));

		Assert.assertEquals(
			_getUrlTitleMethod.invoke(null, entry.getEntryId(), title),
			entry.getUrlTitle());
	}

	@Test
	public void testURLTitleIsSavedWhenAddingApprovedEntryWithWorkflow()
		throws Exception {

		String title = RandomTestUtil.randomString();

		BlogsEntry entry = BlogsTestUtil.addEntryWithWorkflow(
			_user.getUserId(), title, true,
			ServiceContextTestUtil.getServiceContext(
				_group, _user.getUserId()));

		Assert.assertEquals(
			_getUrlTitleMethod.invoke(null, entry.getEntryId(), title),
			entry.getUrlTitle());
	}

	@Test
	public void testURLTitleIsSavedWhenAddingDraftEntry() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group, _user.getUserId());

		serviceContext.setWorkflowAction(WorkflowConstants.STATUS_DRAFT);

		BlogsEntry entry = _blogsEntryLocalService.addEntry(
			_user.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), serviceContext);

		Assert.assertTrue(Validator.isNotNull(entry.getUrlTitle()));
	}

	@Test
	public void testURLTitleIsSavedWhenAddingDraftEntryWithWorkflow()
		throws Exception {

		BlogsEntry entry = BlogsTestUtil.addEntryWithWorkflow(
			_user.getUserId(), RandomTestUtil.randomString(), false,
			ServiceContextTestUtil.getServiceContext(
				_group, _user.getUserId()));

		Assert.assertTrue(Validator.isNotNull(entry.getUrlTitle()));
	}

	@Test
	public void testUnsubscribe() throws Exception {
		int initialCount =
			SubscriptionLocalServiceUtil.getUserSubscriptionsCount(
				_user.getUserId());

		_blogsEntryLocalService.subscribe(
			_user.getUserId(), _group.getGroupId());

		_blogsEntryLocalService.unsubscribe(
			_user.getUserId(), _group.getGroupId());

		int actualCount =
			SubscriptionLocalServiceUtil.getUserSubscriptionsCount(
				_user.getUserId());

		Assert.assertEquals(initialCount, actualCount);
	}

	@Test
	public void testUpdateDraftEntryWithDuplicateURLTitle() throws Exception {
		String urlTitle = RandomTestUtil.randomString();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group, _user.getUserId());

		_blogsEntryLocalService.addEntry(
			RandomTestUtil.randomString(), _user.getUserId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			urlTitle, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), new Date(), false, false, null, null,
			null, null, serviceContext);

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		BlogsEntry entry = _blogsEntryLocalService.addEntry(
			RandomTestUtil.randomString(), _user.getUserId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			urlTitle, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), new Date(), false, false, null, null,
			null, null, serviceContext);

		entry = _blogsEntryLocalService.updateEntry(
			_user.getUserId(), entry.getEntryId(), entry.getTitle(),
			entry.getSubtitle(), urlTitle, entry.getDescription(),
			entry.getContent(), entry.getDisplayDate(), false, false, null,
			null, null, null, serviceContext);

		Assert.assertNotEquals(urlTitle, entry.getUrlTitle());
	}

	@Test
	public void testUpdateEntryResources() throws Exception {
		BlogsEntry entry = addEntry(false);

		_blogsEntryLocalService.updateEntryResources(
			entry, new String[] {ActionKeys.ADD_DISCUSSION}, null);
	}

	@Test(expected = AssetCategoryException.class)
	public void testUpdateEntryWithAssetCategoriesFromNonmultiValuedAssetVocabulary()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group, _user.getUserId());

		AssetVocabularySettingsHelper assetVocabularySettingsHelper =
			_getAssetVocabularySettingsHelper(
				false, new long[] {AssetCategoryConstants.ALL_CLASS_NAME_ID},
				new long[] {AssetCategoryConstants.ALL_CLASS_TYPE_PK},
				new boolean[] {false}, new boolean[] {false});

		Assert.assertFalse(assetVocabularySettingsHelper.isMultiValued());

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				_user.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(),
				HashMapBuilder.put(
					LocaleUtil.US, RandomTestUtil.randomString()
				).build(),
				null, assetVocabularySettingsHelper.toString(), serviceContext);

		AssetCategory assetCategory1 = _assetCategoryLocalService.addCategory(
			_user.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);
		AssetCategory assetCategory2 = _assetCategoryLocalService.addCategory(
			_user.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);

		serviceContext.setAssetCategoryIds(
			new long[] {assetCategory1.getCategoryId()});

		BlogsEntry blogsEntry = _blogsEntryLocalService.addEntry(
			RandomTestUtil.randomString(), _user.getUserId(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), new Date(), true, true, new String[0],
			null, null, null, serviceContext);

		serviceContext.setAssetCategoryIds(
			new long[] {
				assetCategory1.getCategoryId(), assetCategory2.getCategoryId()
			});

		_blogsEntryLocalService.updateEntry(
			blogsEntry.getUserId(), blogsEntry.getEntryId(),
			blogsEntry.getTitle(), blogsEntry.getSubtitle(),
			blogsEntry.getUrlTitle(), blogsEntry.getDescription(),
			blogsEntry.getContent(), blogsEntry.getDisplayDate(),
			blogsEntry.isAllowPingbacks(), blogsEntry.isAllowTrackbacks(),
			new String[0], blogsEntry.getCoverImageCaption(), null, null,
			serviceContext);
	}

	@Test
	public void testUpdateEntryWithURLTitleWithSlashPrefix() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group, _user.getUserId());

		BlogsEntry entry = _blogsEntryLocalService.addEntry(
			_user.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), serviceContext);

		String expectedUrlTitle = entry.getUrlTitle();

		String bakedUrlTitle = "///////" + expectedUrlTitle;

		entry = _blogsEntryLocalService.updateEntry(
			_user.getUserId(), entry.getEntryId(), entry.getTitle(),
			entry.getSubtitle(), bakedUrlTitle, entry.getDescription(),
			entry.getContent(), entry.getDisplayDate(), false, false, null,
			null, null, null, serviceContext);

		Assert.assertEquals(expectedUrlTitle, entry.getUrlTitle());
	}

	protected BlogsEntry addEntry(boolean statusInTrash) throws Exception {
		return addEntry(_user.getUserId(), statusInTrash);
	}

	protected BlogsEntry addEntry(boolean statusInTrash, int date)
		throws Exception {

		return addEntry(_user.getUserId(), statusInTrash, date);
	}

	protected BlogsEntry addEntry(long userId, boolean statusInTrash)
		throws Exception {

		return addEntry(userId, statusInTrash, 1);
	}

	protected BlogsEntry addEntry(long userId, boolean statusInTrash, int date)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), userId);

		Calendar displayDateCalendar = CalendarFactoryUtil.getCalendar(
			2012, 1, date);

		BlogsEntry entry = _blogsEntryLocalService.addEntry(
			userId, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), displayDateCalendar.getTime(),
			serviceContext);

		if (statusInTrash) {
			entry = _blogsEntryLocalService.moveEntryToTrash(userId, entry);
		}

		return entry;
	}

	protected void assertBlogsEntriesStatus(
		List<BlogsEntry> blogsEntries, boolean statusInTrash) {

		for (BlogsEntry blogsEntry : blogsEntries) {
			if (statusInTrash) {
				Assert.assertEquals(
					"The entry " + blogsEntry.getEntryId() +
						" should be in trash",
					WorkflowConstants.STATUS_IN_TRASH, blogsEntry.getStatus());
			}
			else {
				Assert.assertNotEquals(
					"The entry " + blogsEntry.getEntryId() +
						" should not be in trash",
					WorkflowConstants.STATUS_IN_TRASH, blogsEntry.getStatus());
			}
		}
	}

	protected FileEntry getTempFileEntry(
			long userId, long groupId, String title)
		throws PortalException {

		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		InputStream inputStream = classLoader.getResourceAsStream(
			"com/liferay/blogs/dependencies/test.jpg");

		return TempFileEntryUtil.addTempFileEntry(
			groupId, userId, BlogsEntry.class.getName(), title, inputStream,
			MimeTypesUtil.getContentType(title));
	}

	protected void testGetCompanyEntries(boolean statusInTrash)
		throws Exception {

		QueryDefinition<BlogsEntry> queryDefinition =
			_statusInTrashQueryDefinition;

		if (!statusInTrash) {
			queryDefinition = _statusAnyQueryDefinition;
		}

		List<BlogsEntry> initialBlogsEntries =
			_blogsEntryLocalService.getCompanyEntries(
				_user.getCompanyId(), new Date(), queryDefinition);

		int initialCount = initialBlogsEntries.size();

		addEntry(false);
		addEntry(true);

		List<BlogsEntry> actualBlogsEntries =
			_blogsEntryLocalService.getCompanyEntries(
				_user.getCompanyId(), new Date(), queryDefinition);

		Assert.assertEquals(
			actualBlogsEntries.toString(), initialCount + 1,
			actualBlogsEntries.size());

		assertBlogsEntriesStatus(actualBlogsEntries, statusInTrash);
	}

	protected void testGetCompanyEntriesCount(boolean statusInTrash)
		throws Exception {

		QueryDefinition<BlogsEntry> queryDefinition =
			_statusInTrashQueryDefinition;

		if (!statusInTrash) {
			queryDefinition = _statusAnyQueryDefinition;
		}

		int initialCount = _blogsEntryLocalService.getCompanyEntriesCount(
			_user.getCompanyId(), new Date(), queryDefinition);

		addEntry(false);
		addEntry(true);

		int actualCount = _blogsEntryLocalService.getCompanyEntriesCount(
			_user.getCompanyId(), new Date(), queryDefinition);

		Assert.assertEquals(initialCount + 1, actualCount);
	}

	protected void testGetGroupEntries(
			boolean statusInTrash, boolean displayDate)
		throws Exception {

		QueryDefinition<BlogsEntry> queryDefinition =
			_statusInTrashQueryDefinition;

		if (!statusInTrash) {
			queryDefinition = _statusAnyQueryDefinition;
		}

		List<BlogsEntry> initialBlogsEntries = null;

		if (displayDate) {
			initialBlogsEntries = _blogsEntryLocalService.getGroupEntries(
				_group.getGroupId(), new Date(), queryDefinition);
		}
		else {
			initialBlogsEntries = _blogsEntryLocalService.getGroupEntries(
				_group.getGroupId(), queryDefinition);
		}

		int initialCount = initialBlogsEntries.size();

		addEntry(false);
		addEntry(true);

		List<BlogsEntry> actualBlogsEntries = null;

		if (displayDate) {
			actualBlogsEntries = _blogsEntryLocalService.getGroupEntries(
				_group.getGroupId(), new Date(), queryDefinition);
		}
		else {
			actualBlogsEntries = _blogsEntryLocalService.getGroupEntries(
				_group.getGroupId(), queryDefinition);
		}

		Assert.assertEquals(
			actualBlogsEntries.toString(), initialCount + 1,
			actualBlogsEntries.size());

		assertBlogsEntriesStatus(actualBlogsEntries, statusInTrash);
	}

	protected void testGetGroupEntriesCount(
			boolean statusInTrash, boolean displayDate)
		throws Exception {

		QueryDefinition<BlogsEntry> queryDefinition =
			_statusInTrashQueryDefinition;

		if (!statusInTrash) {
			queryDefinition = _statusAnyQueryDefinition;
		}

		int initialCount = 0;

		if (displayDate) {
			initialCount = _blogsEntryLocalService.getGroupEntriesCount(
				_group.getGroupId(), new Date(), queryDefinition);
		}
		else {
			initialCount = _blogsEntryLocalService.getGroupEntriesCount(
				_group.getGroupId(), queryDefinition);
		}

		addEntry(false);
		addEntry(true);

		int actualCount = 0;

		if (displayDate) {
			actualCount = _blogsEntryLocalService.getGroupEntriesCount(
				_group.getGroupId(), new Date(), queryDefinition);
		}
		else {
			actualCount = _blogsEntryLocalService.getGroupEntriesCount(
				_group.getGroupId(), queryDefinition);
		}

		Assert.assertEquals(initialCount + 1, actualCount);
	}

	protected void testGetGroupUserEntries(boolean statusInTrash)
		throws Exception {

		QueryDefinition<BlogsEntry> queryDefinition =
			_statusInTrashQueryDefinition;

		if (!statusInTrash) {
			queryDefinition = _statusAnyQueryDefinition;
		}

		List<BlogsEntry> initialBlogsEntries =
			_blogsEntryLocalService.getGroupUserEntries(
				_group.getGroupId(), _user.getUserId(), new Date(),
				queryDefinition);

		int initialCount = initialBlogsEntries.size();

		addEntry(false);
		addEntry(true);

		List<BlogsEntry> actualBlogsEntries =
			_blogsEntryLocalService.getGroupUserEntries(
				_group.getGroupId(), _user.getUserId(), new Date(),
				queryDefinition);

		Assert.assertEquals(
			actualBlogsEntries.toString(), initialCount + 1,
			actualBlogsEntries.size());

		assertBlogsEntriesStatus(actualBlogsEntries, statusInTrash);
	}

	protected void testGetGroupUserEntriesCount(boolean statusInTrash)
		throws Exception {

		QueryDefinition<BlogsEntry> queryDefinition =
			_statusInTrashQueryDefinition;

		if (!statusInTrash) {
			queryDefinition = _statusAnyQueryDefinition;
		}

		int initialCount = _blogsEntryLocalService.getGroupUserEntriesCount(
			_group.getGroupId(), _user.getUserId(), new Date(),
			queryDefinition);

		addEntry(false);
		addEntry(true);

		int actualCount = _blogsEntryLocalService.getGroupUserEntriesCount(
			_group.getGroupId(), _user.getUserId(), new Date(),
			queryDefinition);

		Assert.assertEquals(initialCount + 1, actualCount);
	}

	protected void testGetOrganizationEntries(boolean statusInTrash)
		throws Exception {

		QueryDefinition<BlogsEntry> queryDefinition =
			_statusInTrashQueryDefinition;

		if (!statusInTrash) {
			queryDefinition = _statusAnyQueryDefinition;
		}

		_organization = OrganizationTestUtil.addOrganization();

		_organizationUser = UserTestUtil.addOrganizationOwnerUser(
			_organization);

		List<BlogsEntry> initialBlogsEntries =
			_blogsEntryLocalService.getOrganizationEntries(
				_organization.getOrganizationId(), new Date(), queryDefinition);

		int initialCount = initialBlogsEntries.size();

		addEntry(_organizationUser.getUserId(), false);
		addEntry(_organizationUser.getUserId(), true);

		List<BlogsEntry> actualBlogsEntries =
			_blogsEntryLocalService.getOrganizationEntries(
				_organization.getOrganizationId(), new Date(), queryDefinition);

		Assert.assertEquals(
			actualBlogsEntries.toString(), initialCount + 1,
			actualBlogsEntries.size());

		assertBlogsEntriesStatus(actualBlogsEntries, statusInTrash);
	}

	protected void testGetOrganizationEntriesCount(boolean statusInTrash)
		throws Exception {

		QueryDefinition<BlogsEntry> queryDefinition =
			_statusInTrashQueryDefinition;

		if (!statusInTrash) {
			queryDefinition = _statusAnyQueryDefinition;
		}

		_organization = OrganizationTestUtil.addOrganization();

		_organizationUser = UserTestUtil.addOrganizationOwnerUser(
			_organization);

		int initialCount = _blogsEntryLocalService.getOrganizationEntriesCount(
			_organization.getOrganizationId(), new Date(), queryDefinition);

		addEntry(_organizationUser.getUserId(), false);
		addEntry(_organizationUser.getUserId(), true);

		int actualCount = _blogsEntryLocalService.getOrganizationEntriesCount(
			_organization.getOrganizationId(), new Date(), queryDefinition);

		Assert.assertEquals(initialCount + 1, actualCount);
	}

	private FileEntry _addAttachmentFileEntry(
			String externalReferenceCode, String fileName)
		throws Exception {

		return _blogsEntryLocalService.addAttachmentFileEntry(
			externalReferenceCode, _user.getUserId(), _group.getGroupId(),
			fileName, ContentTypes.APPLICATION_OCTET_STREAM,
			new UnsyncByteArrayInputStream(new byte[0]));
	}

	private MBMessage _addMBMessage(
			long userId, ServiceContext serviceContext, BlogsEntry entry)
		throws Exception {

		MBMessageDisplay mbMessageDisplay =
			MBMessageLocalServiceUtil.getDiscussionMessageDisplay(
				TestPropsValues.getUserId(), _group.getGroupId(),
				BlogsEntry.class.getName(), entry.getEntryId(),
				WorkflowConstants.STATUS_APPROVED);

		MBThread mbThread = mbMessageDisplay.getThread();

		MBTestUtil.populateNotificationsServiceContext(
			serviceContext, Constants.ADD);

		return MBMessageLocalServiceUtil.addDiscussionMessage(
			null, userId, RandomTestUtil.randomString(), _group.getGroupId(),
			BlogsEntry.class.getName(), entry.getEntryId(),
			mbThread.getThreadId(),
			MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);
	}

	private AssetVocabularySettingsHelper _getAssetVocabularySettingsHelper(
		boolean multiValued, long[] classNameIds, long[] classTypePKs,
		boolean[] depotRequireds, boolean[] requireds) {

		AssetVocabularySettingsHelper assetVocabularySettingsHelper =
			new AssetVocabularySettingsHelper();

		assetVocabularySettingsHelper.setClassNameIdsAndClassTypePKs(
			classNameIds, classTypePKs, depotRequireds, requireds);
		assetVocabularySettingsHelper.setMultiValued(multiValued);

		return assetVocabularySettingsHelper;
	}

	private String _repeat(String string, int times) {
		StringBundler sb = new StringBundler(times);

		for (int i = 0; i < times; i++) {
			sb.append(string);
		}

		return sb.toString();
	}

	private void _withSubscribeBlogsEntryCreatorToCommentsEnabled(
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		Dictionary<String, Object> dictionary =
			HashMapDictionaryBuilder.<String, Object>put(
				"subscribeBlogsEntryCreatorToComments", true
			).build();

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.blogs.configuration." +
						"BlogsGroupServiceConfiguration",
					dictionary)) {

			unsafeRunnable.run();
		}
	}

	private static Method _getUrlTitleMethod;

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@DeleteAfterTestRun
	private final List<BlogsEntry> _blogsEntries = new ArrayList<>();

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	@DeleteAfterTestRun
	private User _creatorUser;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private Organization _organization;

	@DeleteAfterTestRun
	private User _organizationUser;

	private final QueryDefinition<BlogsEntry> _statusAnyQueryDefinition =
		new QueryDefinition<>(
			WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	private final QueryDefinition<BlogsEntry> _statusApprovedQueryDefinition =
		new QueryDefinition<>(
			WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	private final QueryDefinition<BlogsEntry> _statusInTrashQueryDefinition =
		new QueryDefinition<>(
			WorkflowConstants.STATUS_IN_TRASH, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	private User _user;

}