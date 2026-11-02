/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.info.item.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.blogs.test.util.BlogsTestUtil;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.field.type.HTMLInfoFieldType;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.info.type.WebImage;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.InputStream;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class BlogsEntryInfoItemFieldValuesProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Ignore
	@Test
	public void testGetInfoItemFieldValues() throws PortalException {
		BlogsEntry blogsEntry = BlogsTestUtil.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		InfoItemFieldValues infoItemFieldValues =
			_infoItemFieldValuesProvider.getInfoItemFieldValues(blogsEntry);

		InfoFieldValue<Object> createDateInfoFieldValue =
			infoItemFieldValues.getInfoFieldValue("createDate");

		Assert.assertEquals(
			blogsEntry.getCreateDate(), createDateInfoFieldValue.getValue());

		InfoFieldValue<Object> modifiedDateInfoFieldValue =
			infoItemFieldValues.getInfoFieldValue("modifiedDate");

		Assert.assertEquals(
			blogsEntry.getModifiedDate(),
			modifiedDateInfoFieldValue.getValue());

		InfoFieldValue<Object> publishDateInfoFieldValue =
			infoItemFieldValues.getInfoFieldValue("publishDate");

		Assert.assertEquals(
			blogsEntry.getDisplayDate(), publishDateInfoFieldValue.getValue());

		InfoFieldValue<Object> contentInfoFieldValue =
			infoItemFieldValues.getInfoFieldValue("content");

		Assert.assertEquals(
			blogsEntry.getContent(), contentInfoFieldValue.getValue());

		InfoField infoField = contentInfoFieldValue.getInfoField();

		Assert.assertTrue(
			infoField.getInfoFieldType() instanceof HTMLInfoFieldType);
	}

	@Test
	public void testGetInfoItemFieldValuesWithCoverImage() throws Exception {
		ThemeDisplay themeDisplay = _getThemeDisplay();

		ServiceContextThreadLocal.pushServiceContext(
			_getServiceContext(themeDisplay));

		try {
			FileEntry fileEntry = _getTempFileEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

			ImageSelector imageSelector = new ImageSelector(
				FileUtil.getBytes(fileEntry.getContentStream()),
				fileEntry.getTitle(), fileEntry.getMimeType(),
				StringPool.BLANK);

			BlogsEntry blogsEntry = _blogsEntryLocalService.addEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), new Date(), true, true,
				new String[0], StringPool.BLANK, imageSelector, null,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

			InfoItemFieldValues infoItemFieldValues =
				_infoItemFieldValuesProvider.getInfoItemFieldValues(blogsEntry);

			InfoFieldValue<Object> previewImageInfoFieldValue =
				infoItemFieldValues.getInfoFieldValue("previewImage");

			WebImage webImage = (WebImage)previewImageInfoFieldValue.getValue();

			Assert.assertEquals(
				blogsEntry.getCoverImageURL(themeDisplay), webImage.getURL());
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testGetInfoItemFieldValuesWithCoverImageAndSmallImage()
		throws Exception {

		ThemeDisplay themeDisplay = _getThemeDisplay();

		ServiceContextThreadLocal.pushServiceContext(
			_getServiceContext(themeDisplay));

		try {
			FileEntry fileEntry = _getTempFileEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

			ImageSelector imageSelector = new ImageSelector(
				FileUtil.getBytes(fileEntry.getContentStream()),
				fileEntry.getTitle(), fileEntry.getMimeType(),
				StringPool.BLANK);

			BlogsEntry blogsEntry = _blogsEntryLocalService.addEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), new Date(), true, true,
				new String[0], StringPool.BLANK, imageSelector, imageSelector,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

			InfoItemFieldValues infoItemFieldValues =
				_infoItemFieldValuesProvider.getInfoItemFieldValues(blogsEntry);

			InfoFieldValue<Object> previewImageInfoFieldValue =
				infoItemFieldValues.getInfoFieldValue("previewImage");

			WebImage webImage = (WebImage)previewImageInfoFieldValue.getValue();

			Assert.assertEquals(
				blogsEntry.getCoverImageURL(themeDisplay), webImage.getURL());
			Assert.assertNotEquals(
				blogsEntry.getSmallImageURL(themeDisplay), webImage.getURL());
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testGetInfoItemFieldValuesWithSmallImage() throws Exception {
		ThemeDisplay themeDisplay = _getThemeDisplay();

		ServiceContextThreadLocal.pushServiceContext(
			_getServiceContext(themeDisplay));

		try {
			FileEntry fileEntry = _getTempFileEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

			ImageSelector imageSelector = new ImageSelector(
				FileUtil.getBytes(fileEntry.getContentStream()),
				fileEntry.getTitle(), fileEntry.getMimeType(),
				StringPool.BLANK);

			BlogsEntry blogsEntry = _blogsEntryLocalService.addEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), new Date(), true, true,
				new String[0], StringPool.BLANK, null, imageSelector,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

			InfoItemFieldValues infoItemFieldValues =
				_infoItemFieldValuesProvider.getInfoItemFieldValues(blogsEntry);

			InfoFieldValue<Object> previewImageInfoFieldValue =
				infoItemFieldValues.getInfoFieldValue("previewImage");

			WebImage webImage = (WebImage)previewImageInfoFieldValue.getValue();

			Assert.assertEquals(
				blogsEntry.getSmallImageURL(themeDisplay), webImage.getURL());
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private ServiceContext _getServiceContext(ThemeDisplay themeDisplay)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setRequest(themeDisplay.getRequest());

		return serviceContext;
	}

	private FileEntry _getTempFileEntry(
			long userId, String title, ServiceContext serviceContext)
		throws PortalException {

		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		InputStream inputStream = classLoader.getResourceAsStream(
			"com/liferay/blogs/web/internal/info/item/provider/test" +
				"/dependencies/test.jpg");

		return TempFileEntryUtil.addTempFileEntry(
			serviceContext.getScopeGroupId(), userId,
			BlogsEntry.class.getName(), title, inputStream,
			MimeTypesUtil.getContentType(title));
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		themeDisplay.setRequest(mockHttpServletRequest);

		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setURLCurrent(
			"http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/currentURL");

		return themeDisplay;
	}

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "component.name=com.liferay.blogs.web.internal.info.item.provider.BlogsEntryInfoItemFieldValuesProvider"
	)
	private InfoItemFieldValuesProvider _infoItemFieldValuesProvider;

}