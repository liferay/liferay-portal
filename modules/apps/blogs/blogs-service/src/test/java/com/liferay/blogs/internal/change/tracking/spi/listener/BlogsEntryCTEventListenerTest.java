/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.internal.change.tracking.spi.listener;

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.model.impl.BlogsEntryImpl;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Mariano Álvaro Sáiz
 */
public class BlogsEntryCTEventListenerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testOnAfterPublish() {
		_testOnAfterPublish("/a?b&amp;previewCTCollectionId=1", "/a?b");
		_testOnAfterPublish(
			"/a?b&amp;previewCTCollectionId=1&amp;c", "/a?b&amp;c");
		_testOnAfterPublish("/a?b&previewCTCollectionId=1", "/a?b");
		_testOnAfterPublish("/a?b&previewCTCollectionId=1&c", "/a?b&c");
		_testOnAfterPublish(
			"/a?bpreviewCTCollectionId=1", "/a?bpreviewCTCollectionId=1");
		_testOnAfterPublish("/a?previewCTCollectionId=1", "/a");
		_testOnAfterPublish("/a?previewCTCollectionId=1&amp;b", "/a?b");
		_testOnAfterPublish("/a?previewCTCollectionId=1&b", "/a?b");
		_testOnAfterPublish(
			"/a?previewCTCollectionId=11", "/a?previewCTCollectionId=11");
	}

	private void _testOnAfterPublish(String content, String expectedContent) {
		BlogsEntryCTEventListener blogsEntryCTEventListener =
			new BlogsEntryCTEventListener();

		BlogsEntry blogsEntry = new BlogsEntryImpl();

		blogsEntry.setContent(content);

		BlogsEntryLocalService blogsEntryLocalService = Mockito.mock(
			BlogsEntryLocalService.class);

		Mockito.when(
			blogsEntryLocalService.fetchBlogsEntry(Mockito.anyLong())
		).thenReturn(
			blogsEntry
		);

		CTEntryLocalService ctEntryLocalService = Mockito.mock(
			CTEntryLocalService.class);

		Mockito.when(
			ctEntryLocalService.getCTEntries(Mockito.eq(1L), Mockito.anyLong())
		).thenReturn(
			Collections.singletonList(Mockito.mock(CTEntry.class))
		);

		ReflectionTestUtil.setFieldValue(
			blogsEntryCTEventListener, "_blogsEntryLocalService",
			blogsEntryLocalService);
		ReflectionTestUtil.setFieldValue(
			blogsEntryCTEventListener, "_ctEntryLocalService",
			ctEntryLocalService);
		ReflectionTestUtil.setFieldValue(
			blogsEntryCTEventListener, "_portal", Mockito.mock(Portal.class));

		blogsEntryCTEventListener.onAfterPublish(1);

		Assert.assertEquals(expectedContent, blogsEntry.getContent());

		int wantedNumberOfInvocations = 1;

		if (content.equals(expectedContent)) {
			wantedNumberOfInvocations = 0;
		}

		Mockito.verify(
			blogsEntryLocalService, Mockito.times(wantedNumberOfInvocations)
		).updateBlogsEntry(
			blogsEntry
		);
	}

}