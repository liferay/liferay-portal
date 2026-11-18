/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.attachments.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.InputStream;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
 * @author Roberto Díaz
 */
@RunWith(Arquillian.class)
public class BlogsEntryImageSelectorHelperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			BlogsEntryAttachmentFileEntryHelperTest.class);

		bundle = BundleUtil.getBundle(
			bundle.getBundleContext(), "com.liferay.blogs.web");

		Class<?> clazz = bundle.loadClass(
			"com.liferay.blogs.web.internal.helper." +
				"BlogsEntryImageSelectorHelper");

		_constructor = clazz.getConstructor(
			Long.TYPE, Long.TYPE, Long.TYPE, String.class, String.class,
			String.class);

		_getImageSelectorMethod = clazz.getMethod("getImageSelector");

		_isFileEntryTempFileMethod = clazz.getMethod("isFileEntryTempFile");
	}

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		UserTestUtil.setUser(TestPropsValues.getUser());
	}

	@Test
	public void testGetEmptyImageSelectorWithDifferentFileEntryIds()
		throws Exception {

		Object blogsEntryImageSelectorHelper = _constructor.newInstance(
			0, 0, 1, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK);

		ImageSelector imageSelector =
			(ImageSelector)_getImageSelectorMethod.invoke(
				blogsEntryImageSelectorHelper);

		Assert.assertNull(imageSelector.getImageBytes());
		Assert.assertEquals(StringPool.BLANK, imageSelector.getImageTitle());
		Assert.assertEquals(StringPool.BLANK, imageSelector.getImageMimeType());
		Assert.assertEquals(
			StringPool.BLANK, imageSelector.getImageCropRegion());
		Assert.assertEquals(StringPool.BLANK, imageSelector.getImageURL());
	}

	@Test
	public void testGetImageSelectorWithDLImageFileEntry() throws Exception {
		try (InputStream inputStream = getInputStream()) {
			byte[] bytes = FileUtil.getBytes(inputStream);

			FileEntry fileEntry = DLAppLocalServiceUtil.addFileEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, _IMAGE_TITLE,
				MimeTypesUtil.getContentType(_IMAGE_TITLE), "image",
				StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, bytes,
				null, null, null,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

			Object blogsEntryImageSelectorHelper = _constructor.newInstance(
				0, fileEntry.getFileEntryId(), fileEntry.getFileEntryId() + 1,
				_IMAGE_CROP_REGION, StringPool.BLANK, StringPool.BLANK);

			ImageSelector imageSelector =
				(ImageSelector)_getImageSelectorMethod.invoke(
					blogsEntryImageSelectorHelper);

			Assert.assertArrayEquals(bytes, imageSelector.getImageBytes());
			Assert.assertEquals(_IMAGE_TITLE, imageSelector.getImageTitle());
			Assert.assertEquals(
				MimeTypesUtil.getContentType(_IMAGE_TITLE),
				imageSelector.getImageMimeType());
			Assert.assertEquals(
				_IMAGE_CROP_REGION, imageSelector.getImageCropRegion());
			Assert.assertEquals(StringPool.BLANK, imageSelector.getImageURL());

			Assert.assertFalse(
				(Boolean)_isFileEntryTempFileMethod.invoke(
					blogsEntryImageSelectorHelper));
		}
	}

	@Test
	@TestInfo("LPD-116075")
	public void testGetImageSelectorWithDLImageFileEntryWithoutViewPermission()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);

		FileEntry fileEntry = DLAppLocalServiceUtil.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, _IMAGE_TITLE,
			MimeTypesUtil.getContentType(_IMAGE_TITLE),
			FileUtil.getBytes(getInputStream()), null, null, null,
			serviceContext);

		Object blogsEntryImageSelectorHelper = _constructor.newInstance(
			0, fileEntry.getFileEntryId(), 0, _IMAGE_CROP_REGION,
			StringPool.BLANK, StringPool.BLANK);

		User user = UserTestUtil.addUser();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			InvocationTargetException invocationTargetException =
				Assert.assertThrows(
					InvocationTargetException.class,
					() -> _getImageSelectorMethod.invoke(
						blogsEntryImageSelectorHelper));

			PrincipalException.MustHavePermission mustHavePermission =
				(PrincipalException.MustHavePermission)
					invocationTargetException.getCause();

			Assert.assertArrayEquals(
				new String[] {ActionKeys.VIEW}, mustHavePermission.actionId);
			Assert.assertEquals(
				fileEntry.getFileEntryId(), mustHavePermission.resourceId);
			Assert.assertEquals(
				DLFileEntry.class.getName(), mustHavePermission.resourceName);
			Assert.assertEquals(user.getUserId(), mustHavePermission.userId);
		}
	}

	@Test
	public void testGetImageSelectorWithImageURL() throws Exception {
		Object blogsEntryImageSelectorHelper = _constructor.newInstance(
			0, 0, 0, StringPool.BLANK, _IMAGE_URL, StringPool.BLANK);

		ImageSelector imageSelector =
			(ImageSelector)_getImageSelectorMethod.invoke(
				blogsEntryImageSelectorHelper);

		Assert.assertNull(imageSelector.getImageBytes());
		Assert.assertEquals(StringPool.BLANK, imageSelector.getImageTitle());
		Assert.assertEquals(StringPool.BLANK, imageSelector.getImageMimeType());
		Assert.assertEquals(
			StringPool.BLANK, imageSelector.getImageCropRegion());
		Assert.assertEquals(_IMAGE_URL, imageSelector.getImageURL());

		Assert.assertFalse(
			(Boolean)_isFileEntryTempFileMethod.invoke(
				blogsEntryImageSelectorHelper));
	}

	@Test
	public void testGetImageSelectorWithSameDLImageFileEntry()
		throws Exception {

		try (InputStream inputStream = getInputStream()) {
			FileEntry fileEntry = DLAppLocalServiceUtil.addFileEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, _IMAGE_TITLE,
				MimeTypesUtil.getContentType(_IMAGE_TITLE), "image",
				StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
				FileUtil.getBytes(inputStream), null, null, null,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

			Object blogsEntryImageSelectorHelper = _constructor.newInstance(
				0, fileEntry.getFileEntryId(), fileEntry.getFileEntryId(),
				_IMAGE_CROP_REGION, StringPool.BLANK, StringPool.BLANK);

			Assert.assertNull(
				(ImageSelector)_getImageSelectorMethod.invoke(
					blogsEntryImageSelectorHelper));
			Assert.assertFalse(
				(Boolean)_isFileEntryTempFileMethod.invoke(
					blogsEntryImageSelectorHelper));
		}
	}

	@Test
	public void testGetImageSelectorWithSameImageURL() throws Exception {
		Object blogsEntryImageSelectorHelper = _constructor.newInstance(
			0, 0, 0, StringPool.BLANK, _IMAGE_URL, _IMAGE_URL);

		Assert.assertNull(
			(ImageSelector)_getImageSelectorMethod.invoke(
				blogsEntryImageSelectorHelper));
		Assert.assertFalse(
			(Boolean)_isFileEntryTempFileMethod.invoke(
				blogsEntryImageSelectorHelper));
	}

	@Test
	public void testGetImageSelectorWithTempImageFileEntry() throws Exception {
		try (InputStream inputStream = getInputStream()) {
			byte[] bytes = FileUtil.getBytes(inputStream);

			FileEntry tempFileEntry = TempFileEntryUtil.addTempFileEntry(
				_group.getGroupId(), TestPropsValues.getUserId(),
				_TEMP_FOLDER_NAME, _IMAGE_TITLE, getInputStream(),
				ContentTypes.IMAGE_JPEG);

			Object blogsEntryImageSelectorHelper = _constructor.newInstance(
				0, tempFileEntry.getFileEntryId(),
				tempFileEntry.getFileEntryId() + 1, _IMAGE_CROP_REGION,
				StringPool.BLANK, StringPool.BLANK);

			ImageSelector imageSelector =
				(ImageSelector)_getImageSelectorMethod.invoke(
					blogsEntryImageSelectorHelper);

			Assert.assertArrayEquals(bytes, imageSelector.getImageBytes());
			Assert.assertEquals(_IMAGE_TITLE, imageSelector.getImageTitle());
			Assert.assertEquals(
				MimeTypesUtil.getContentType(_IMAGE_TITLE),
				imageSelector.getImageMimeType());
			Assert.assertEquals(
				_IMAGE_CROP_REGION, imageSelector.getImageCropRegion());
			Assert.assertEquals(StringPool.BLANK, imageSelector.getImageURL());

			Assert.assertTrue(
				(Boolean)_isFileEntryTempFileMethod.invoke(
					blogsEntryImageSelectorHelper));
		}
	}

	protected InputStream getInputStream() {
		Class<?> clazz = BlogsEntryAttachmentFileEntryHelperTest.class;

		ClassLoader classLoader = clazz.getClassLoader();

		return classLoader.getResourceAsStream(
			"com/liferay/blogs/dependencies/test.jpg");
	}

	private static final String _IMAGE_CROP_REGION =
		"{\"height\": 10, \"width\": 10, \"x\": 0, \"y\": 0}";

	private static final String _IMAGE_TITLE = "image.jpg";

	private static final String _IMAGE_URL = "http://www.liferay.com";

	private static final String _TEMP_FOLDER_NAME =
		BlogsEntryImageSelectorHelperTest.class.getName();

	private static Constructor<?> _constructor;
	private static Method _getImageSelectorMethod;
	private static Method _isFileEntryTempFileMethod;

	@DeleteAfterTestRun
	private Group _group;

}