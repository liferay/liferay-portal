/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.attachments.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.constants.BlogsConstants;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalServiceUtil;
import com.liferay.blogs.test.util.BlogsTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.editor.constants.EditorConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.upload.UniqueFileNameProvider;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Roberto Díaz
 * @author Sergio González
 */
@RunWith(Arquillian.class)
public class BlogsEntryAttachmentFileEntryHelperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.addGroupAdminUser(_group);

		UserTestUtil.setUser(TestPropsValues.getUser());
	}

	@Test
	public void testAddBlogsEntryAttachmentFileEntries() throws Exception {
		FileEntry tempFileEntry = TempFileEntryUtil.addTempFileEntry(
			_group.getGroupId(), _user.getUserId(), _TEMP_FOLDER_NAME,
			"image.jpg", getInputStream(), ContentTypes.IMAGE_JPEG);

		List<BlogsEntryAttachmentFileEntryReference>
			blogsEntryAttachmentFileEntryReferences =
				getBlogsEntryAttachmentFileEntryReferences(tempFileEntry);

		Assert.assertEquals(
			blogsEntryAttachmentFileEntryReferences.toString(), 1,
			blogsEntryAttachmentFileEntryReferences.size());

		BlogsEntryAttachmentFileEntryReference
			blogsEntryAttachmentFileEntryReference =
				blogsEntryAttachmentFileEntryReferences.get(0);

		Assert.assertEquals(
			tempFileEntry.getFileEntryId(),
			blogsEntryAttachmentFileEntryReference.
				getTempBlogsEntryAttachmentFileEntryId());

		FileEntry fileEntry =
			blogsEntryAttachmentFileEntryReference.
				getBlogsEntryAttachmentFileEntry();

		Assert.assertEquals(tempFileEntry.getTitle(), fileEntry.getTitle());
		Assert.assertEquals(
			tempFileEntry.getMimeType(), fileEntry.getMimeType());
		Assert.assertEquals(
			DigesterUtil.digestBase64(tempFileEntry.getContentStream()),
			DigesterUtil.digestBase64(fileEntry.getContentStream()));
	}

	@Test
	public void testGetTempBlogsEntryAttachmentFileEntries() throws Exception {
		FileEntry tempFileEntry = TempFileEntryUtil.addTempFileEntry(
			_group.getGroupId(), _user.getUserId(), _TEMP_FOLDER_NAME,
			"image.jpg", getInputStream(), ContentTypes.IMAGE_JPEG);

		String tempFileEntryImgTag =
			BlogsTestUtil.getTempBlogsEntryAttachmentFileEntryImgTag(
				tempFileEntry.getFileEntryId(),
				PortletFileRepositoryUtil.getPortletFileEntryURL(
					null, tempFileEntry, StringPool.BLANK));

		List<FileEntry> tempBlogsEntryAttachmentFileEntries =
			_getTempBlogsEntryAttachmentFileEntries(
				getContent(tempFileEntryImgTag));

		Assert.assertEquals(
			tempBlogsEntryAttachmentFileEntries.toString(), 1,
			tempBlogsEntryAttachmentFileEntries.size());

		for (FileEntry tempBlogsEntryAttachmentFileEntry :
				tempBlogsEntryAttachmentFileEntries) {

			Assert.assertEquals(
				tempFileEntry.getFileEntryId(),
				tempBlogsEntryAttachmentFileEntry.getFileEntryId());
		}
	}

	@Test
	public void testGetTempBlogsEntryAttachmentFileEntriesWithModifiedImgTag()
		throws Exception {

		FileEntry tempFileEntry = TempFileEntryUtil.addTempFileEntry(
			_group.getGroupId(), _user.getUserId(), _TEMP_FOLDER_NAME,
			"image.jpg", getInputStream(), ContentTypes.IMAGE_JPEG);

		String tempFileEntryImgTag = getModifiedTempFileEntryImgTag(
			tempFileEntry);

		List<FileEntry> tempBlogsEntryAttachmentFileEntries =
			_getTempBlogsEntryAttachmentFileEntries(
				getContent(tempFileEntryImgTag));

		Assert.assertEquals(
			tempBlogsEntryAttachmentFileEntries.toString(), 1,
			tempBlogsEntryAttachmentFileEntries.size());

		for (FileEntry tempBlogsEntryAttachmentFileEntry :
				tempBlogsEntryAttachmentFileEntries) {

			Assert.assertEquals(
				tempFileEntry.getFileEntryId(),
				tempBlogsEntryAttachmentFileEntry.getFileEntryId());
		}
	}

	protected List<BlogsEntryAttachmentFileEntryReference>
			getBlogsEntryAttachmentFileEntryReferences(FileEntry tempFileEntry)
		throws Exception {

		BlogsEntry entry = BlogsTestUtil.addEntry(
			_user.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId()));

		List<FileEntry> tempFileEntries = new ArrayList<>();

		tempFileEntries.add(tempFileEntry);

		Folder folder = BlogsEntryLocalServiceUtil.addAttachmentsFolder(
			_user.getUserId(), _group.getGroupId());

		return _addBlogsEntryAttachmentFileEntries(
			_group.getGroupId(), _user.getUserId(), entry.getEntryId(),
			folder.getFolderId(), tempFileEntries);
	}

	protected String getContent(String tempFileEntryImgTag) {
		StringBundler sb = new StringBundler(10);

		sb.append("<p>");
		sb.append(RandomTestUtil.randomStrings(50));
		sb.append("</p>");
		sb.append("<a href=\"www.liferay.com\"><span>");
		sb.append(RandomTestUtil.randomStrings(50));
		sb.append("<img src=\"www.liferay.com/logo.png\" /><span>");
		sb.append(RandomTestUtil.randomStrings(50));
		sb.append("</span>");
		sb.append(tempFileEntryImgTag);
		sb.append("<span></a>");

		return sb.toString();
	}

	protected InputStream getInputStream() {
		Class<?> clazz = BlogsEntryAttachmentFileEntryHelperTest.class;

		ClassLoader classLoader = clazz.getClassLoader();

		return classLoader.getResourceAsStream(
			"com/liferay/blogs/dependencies/test.jpg");
	}

	protected String getModifiedTempFileEntryImgTag(FileEntry tempFileEntry) {
		return StringBundler.concat(
			"<img ", EditorConstants.ATTRIBUTE_DATA_IMAGE_ID, "=\"",
			tempFileEntry.getFileEntryId(),
			"\" class=\"test-class\" id=\"test-id\" src=\"",
			PortletFileRepositoryUtil.getPortletFileEntryURL(
				null, tempFileEntry, StringPool.BLANK),
			"\" title=\"test-title\" />");
	}

	private List<BlogsEntryAttachmentFileEntryReference>
			_addBlogsEntryAttachmentFileEntries(
				long groupId, long userId, long blogsEntryId, long folderId,
				List<FileEntry> tempFileEntries)
		throws Exception {

		return TransformUtil.transform(
			tempFileEntries,
			tempFileEntry -> {
				FileEntry blogsEntryAttachmentFileEntry =
					_addBlogsEntryAttachmentFileEntry(
						groupId, userId, blogsEntryId, folderId,
						tempFileEntry.getTitle(), tempFileEntry.getMimeType(),
						tempFileEntry.getContentStream());

				return new BlogsEntryAttachmentFileEntryReference(
					tempFileEntry.getFileEntryId(),
					blogsEntryAttachmentFileEntry);
			});
	}

	private FileEntry _addBlogsEntryAttachmentFileEntry(
			long groupId, long userId, long blogsEntryId, long folderId,
			String fileName, String mimeType, InputStream inputStream)
		throws Exception {

		String uniqueFileName = _getUniqueFileName(groupId, fileName, folderId);

		return PortletFileRepositoryUtil.addPortletFileEntry(
			null, groupId, userId, BlogsEntry.class.getName(), blogsEntryId,
			BlogsConstants.SERVICE_NAME, folderId, inputStream, uniqueFileName,
			mimeType, true);
	}

	private List<FileEntry> _getTempBlogsEntryAttachmentFileEntries(
			String content)
		throws Exception {

		List<FileEntry> tempBlogsEntryAttachmentFileEntries = new ArrayList<>();

		Pattern pattern = Pattern.compile(
			EditorConstants.ATTRIBUTE_DATA_IMAGE_ID + "=.(\\d+)");

		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			long fileEntryId = GetterUtil.getLong(matcher.group(1));

			FileEntry tempFileEntry =
				PortletFileRepositoryUtil.getPortletFileEntry(fileEntryId);

			tempBlogsEntryAttachmentFileEntries.add(tempFileEntry);
		}

		return tempBlogsEntryAttachmentFileEntries;
	}

	private String _getUniqueFileName(
			long groupId, String fileName, long folderId)
		throws Exception {

		return _uniqueFileNameProvider.provide(
			fileName,
			curFileName -> _hasFileEntry(groupId, folderId, curFileName));
	}

	private boolean _hasFileEntry(
		long groupId, long folderId, String fileName) {

		FileEntry fileEntry = _portletFileRepository.fetchPortletFileEntry(
			groupId, folderId, fileName);

		if (fileEntry == null) {
			return false;
		}

		return true;
	}

	private static final String _TEMP_FOLDER_NAME = BlogsEntry.class.getName();

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private PortletFileRepository _portletFileRepository;

	@Inject
	private UniqueFileNameProvider _uniqueFileNameProvider;

	@DeleteAfterTestRun
	private User _user;

	private static class BlogsEntryAttachmentFileEntryReference {

		public FileEntry getBlogsEntryAttachmentFileEntry() {
			return _blogsEntryAttachmentFileEntry;
		}

		public long getTempBlogsEntryAttachmentFileEntryId() {
			return _tempBlogsEntryAttachmentFileEntryId;
		}

		private BlogsEntryAttachmentFileEntryReference(
			long tempBlogsEntryAttachmentFileEntryId,
			FileEntry blogsEntryAttachmentFileEntry) {

			_tempBlogsEntryAttachmentFileEntryId =
				tempBlogsEntryAttachmentFileEntryId;
			_blogsEntryAttachmentFileEntry = blogsEntryAttachmentFileEntry;
		}

		private final FileEntry _blogsEntryAttachmentFileEntry;
		private final long _tempBlogsEntryAttachmentFileEntryId;

	}

}