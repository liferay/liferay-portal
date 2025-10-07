/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.service.BlogsEntryLocalServiceUtil;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.headless.delivery.client.dto.v1_0.BlogPostingImage;
import com.liferay.headless.delivery.client.http.HttpInvoker;
import com.liferay.headless.delivery.client.problem.Problem;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;

import java.io.File;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@RunWith(Arquillian.class)
public class BlogPostingImageResourceTest
	extends BaseBlogPostingImageResourceTestCase {

	@Override
	@Test
	public void testGetBlogPostingImage() throws Exception {
		super.testGetBlogPostingImage();

		_testGetBlogPostingImageContentURL();
	}

	@Override
	@Test
	public void testPostSiteBlogPostingImage() throws Exception {
		super.testPostSiteBlogPostingImage();

		_testPostSiteBlogPostingImageRollback();
		_testPostSiteBlogPostingImageWithDuplicateExternalReferenceCode();
		_testPostSiteBlogPostingImageWithDuplicateTitle();
	}

	@Override
	protected void assertValid(
			BlogPostingImage blogPostingImage, Map<String, File> multipartFiles)
		throws Exception {

		Assert.assertEquals(
			new String(FileUtil.getBytes(multipartFiles.get("file"))),
			_read("http://localhost:8080" + blogPostingImage.getContentUrl()));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"title"};
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {"fileExtension", "sizeInBytes"};
	}

	@Override
	protected Map<String, File> getMultipartFiles() throws Exception {
		return HashMapBuilder.<String, File>put(
			"file",
			() -> FileUtil.createTempFile(TestDataConstants.TEST_BYTE_ARRAY)
		).build();
	}

	@Override
	protected Long
			testDeleteSiteBlogPostingImageByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	@Override
	protected Long
			testGetSiteBlogPostingImageByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	@Override
	protected BlogPostingImage testGraphQLBlogPostingImage_addBlogPostingImage()
		throws Exception {

		return testDeleteBlogPostingImage_addBlogPostingImage();
	}

	@Override
	protected Long
			testGraphQLGetSiteBlogPostingImageByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	@Override
	protected BlogPostingImage
			testGraphQLSiteBlogPostingImage_addBlogPostingImage(
				Long siteId, BlogPostingImage blogPostingImage)
		throws Exception {

		return blogPostingImageResource.postSiteBlogPostingImage(
			siteId, blogPostingImage, getMultipartFiles());
	}

	private String _read(String url) throws Exception {
		HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

		httpInvoker.httpMethod(HttpInvoker.HttpMethod.GET);
		httpInvoker.path(url);
		httpInvoker.userNameAndPassword(
			"test@liferay.com:" + PropsValues.DEFAULT_ADMIN_PASSWORD);

		HttpInvoker.HttpResponse httpResponse = httpInvoker.invoke();

		return httpResponse.getContent();
	}

	private void _testGetBlogPostingImageContentURL() throws Exception {
		DLFolder dlFolder = DLTestUtil.addDLFolder(testGroup.getGroupId());

		DLFileEntry dlFileEntry = DLTestUtil.addDLFileEntry(
			dlFolder.getFolderId());

		FileEntry fileEntry = BlogsEntryLocalServiceUtil.addAttachmentFileEntry(
			null, dlFileEntry.getUserId(), dlFileEntry.getGroupId(),
			dlFileEntry.getFileName(), dlFileEntry.getMimeType(),
			dlFileEntry.getContentStream());

		String previewURL = _dlURLHelper.getPreviewURL(
			fileEntry, fileEntry.getFileVersion(), null, "", true, false);

		BlogPostingImage getBlogPostingImage =
			blogPostingImageResource.getBlogPostingImage(
				fileEntry.getFileEntryId());

		Assert.assertEquals(getBlogPostingImage.getContentUrl(), previewURL);
	}

	private void _testPostSiteBlogPostingImageRollback() throws Exception {
		tearDown();
		setUp();

		Folder folder = BlogsEntryLocalServiceUtil.fetchAttachmentsFolder(
			UserLocalServiceUtil.getGuestUserId(testGroup.getCompanyId()),
			testGroup.getGroupId());

		Assert.assertNull(folder);

		BlogPostingImage blogPostingImage = randomBlogPostingImage();

		blogPostingImage.setTitle("*,?" + blogPostingImage.getTitle());

		try {
			testPostSiteBlogPostingImage_addBlogPostingImage(
				blogPostingImage, getMultipartFiles());

			Assert.fail();
		}
		catch (Throwable throwable) {
			Assert.assertTrue(throwable instanceof Problem.ProblemException);
		}

		folder = BlogsEntryLocalServiceUtil.fetchAttachmentsFolder(
			UserLocalServiceUtil.getGuestUserId(testGroup.getCompanyId()),
			testGroup.getGroupId());

		Assert.assertNull(folder);
	}

	private void _testPostSiteBlogPostingImageWithDuplicateExternalReferenceCode()
		throws Exception {

		Map<String, File> multipartFiles = getMultipartFiles();

		BlogPostingImage randomBlogPostingImage1 = randomBlogPostingImage();

		testPostSiteBlogPostingImage_addBlogPostingImage(
			randomBlogPostingImage1, multipartFiles);

		BlogPostingImage randomBlogPostingImage2 = randomBlogPostingImage();

		randomBlogPostingImage2.setExternalReferenceCode(
			randomBlogPostingImage1.getExternalReferenceCode());

		try {
			testPostSiteBlogPostingImage_addBlogPostingImage(
				randomBlogPostingImage2, multipartFiles);

			Assert.fail();
		}
		catch (Throwable throwable) {
			Assert.assertTrue(throwable instanceof Problem.ProblemException);
		}
	}

	private void _testPostSiteBlogPostingImageWithDuplicateTitle()
		throws Exception {

		Map<String, File> multipartFiles = getMultipartFiles();

		BlogPostingImage randomBlogPostingImage1 = randomBlogPostingImage();

		BlogPostingImage blogPostingImage =
			testPostSiteBlogPostingImage_addBlogPostingImage(
				randomBlogPostingImage1, multipartFiles);

		Assert.assertEquals(
			randomBlogPostingImage1.getTitle(), blogPostingImage.getTitle());

		BlogPostingImage randomBlogPostingImage2 = randomBlogPostingImage();

		randomBlogPostingImage2.setTitle(randomBlogPostingImage1.getTitle());

		blogPostingImage = testPostSiteBlogPostingImage_addBlogPostingImage(
			randomBlogPostingImage2, multipartFiles);

		Assert.assertEquals(
			StringUtil.appendParentheticalSuffix(
				randomBlogPostingImage2.getTitle(), 1),
			blogPostingImage.getTitle());
	}

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLURLHelper _dlURLHelper;

}