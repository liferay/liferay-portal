/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.model.impl.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalServiceUtil;
import com.liferay.blogs.test.util.BlogsTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.InputStream;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jonathan McCann
 */
@RunWith(Arquillian.class)
public class BlogsEntryImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetCoverImageAlt() throws Exception {
		BlogsEntry blogsEntry = _addBlogsEntryWithCoverImage();

		Assert.assertEquals(_IMAGE_TITLE, blogsEntry.getCoverImageAlt());
	}

	@Test
	public void testGetCoverImageURL() throws Exception {
		BlogsEntry blogsEntry = _addBlogsEntryWithCoverImage();

		Assert.assertNotNull(blogsEntry.getCoverImageURL(null));
	}

	@Test
	public void testGetSmallImageAlt() throws Exception {
		BlogsEntry blogsEntry = _addBlogsEntryWithSmallImage();

		Assert.assertEquals(_IMAGE_TITLE, blogsEntry.getSmallImageAlt());
	}

	@Test
	public void testGetSmallImageURL() throws Exception {
		BlogsEntry blogsEntry = _addBlogsEntryWithSmallImage();

		Assert.assertNotNull(blogsEntry.getSmallImageURL(null));
	}

	@Test
	public void testIsVisible() throws Exception {
		BlogsEntry blogsEntry = BlogsTestUtil.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertTrue(blogsEntry.isVisible());
	}

	@Test
	public void testIsVisibleWithFutureDisplayDate() throws Exception {
		BlogsEntry blogsEntry = BlogsTestUtil.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext());

		Calendar displayDate = new GregorianCalendar();

		displayDate.add(Calendar.DATE, 1);

		blogsEntry.setDisplayDate(displayDate.getTime());

		Assert.assertFalse(blogsEntry.isVisible());
	}

	@Test
	public void testIsVisibleWithPendingBlogsEntry() throws Exception {
		BlogsEntry blogsEntry = BlogsTestUtil.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext());

		blogsEntry.setStatus(WorkflowConstants.STATUS_PENDING);

		Assert.assertFalse(blogsEntry.isVisible());
	}

	private BlogsEntry _addBlogsEntryWithCoverImage() throws Exception {
		return BlogsEntryLocalServiceUtil.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), new Date(), true, true,
			new String[0], StringPool.BLANK, _getImageSelector(), null,
			ServiceContextTestUtil.getServiceContext());
	}

	private BlogsEntry _addBlogsEntryWithSmallImage() throws Exception {
		return BlogsEntryLocalServiceUtil.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), new Date(), true, true,
			new String[0], StringPool.BLANK, null, _getImageSelector(),
			ServiceContextTestUtil.getServiceContext());
	}

	private ImageSelector _getImageSelector() throws Exception {
		InputStream inputStream = _getInputStream();

		String mimeType = MimeTypesUtil.getContentType(_IMAGE_TITLE);

		return new ImageSelector(
			FileUtil.getBytes(inputStream), _IMAGE_TITLE, mimeType,
			StringPool.BLANK);
	}

	private InputStream _getInputStream() {
		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		return classLoader.getResourceAsStream(
			"com/liferay/blogs/dependencies/test.jpg");
	}

	private static final String _IMAGE_TITLE = "test.jpg";

}