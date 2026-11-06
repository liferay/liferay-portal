/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.internal.change.tracking.spi.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTCollectionService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.DateTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian Wulbern
 */
@RunWith(Arquillian.class)
public class BlogsEntryCTEventListenerTest {

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
			0, RandomTestUtil.randomString(), null);

		_group = GroupTestUtil.addGroup();
	}

	@Test
	@TestInfo("LPD-113292")
	public void testOnAfterPublish() throws Exception {
		BlogsEntry blogsEntry = null;
		long ctCollectionId = _ctCollection.getCtCollectionId();

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			blogsEntry = _blogsEntryLocalService.addEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				StringBundler.concat(
					"<p><img src=\"/documents/d/guest/image1",
					"?previewCTCollectionId=", ctCollectionId, "\" /></p>",
					"<p><img src=\"/documents/d/guest/image2",
					"?previewCTCollectionId=", ctCollectionId,
					"&amp;imagePreview=1\" /></p><p><a href=\"/documents/d",
					"/guest/file1?download=true&amp;previewCTCollectionId=",
					ctCollectionId, "\">file1</a></p><p><a href=\"/documents/d",
					"/guest/file2?previewCTCollectionId=", ctCollectionId,
					"1\">file2</a></p><p><a href=\"/documents/d/guest/file3",
					"?download=true&amp;previewCTCollectionId=", ctCollectionId,
					"&amp;version=1.0\">file3</a></p><p><a href=\"/documents/d",
					"/guest/file4?_foo_previewCTCollectionId=", ctCollectionId,
					"&amp;version=1.0\">file4</a></p>"),
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
		}

		Date modifiedDate = blogsEntry.getModifiedDate();

		_ctCollectionService.publishCTCollection(
			TestPropsValues.getUserId(), ctCollectionId);

		blogsEntry = _blogsEntryLocalService.getEntry(blogsEntry.getEntryId());

		DateTestUtil.assertEquals(modifiedDate, blogsEntry.getModifiedDate());

		String content = blogsEntry.getContent();

		Assert.assertTrue(
			content.contains(
				"href=\"/documents/d/guest/file1?download=true\""));
		Assert.assertTrue(
			content.contains(
				StringBundler.concat(
					"href=\"/documents/d/guest/file2?previewCTCollectionId=",
					ctCollectionId, "1\"")));
		Assert.assertTrue(
			content.contains(
				"href=\"/documents/d/guest/file3?download=true&amp;" +
					"version=1.0\""));
		Assert.assertTrue(
			content.contains(
				StringBundler.concat(
					"href=\"/documents/d/guest/file4",
					"?_foo_previewCTCollectionId=", ctCollectionId,
					"&amp;version=1.0\"")));
		Assert.assertTrue(
			content.contains("src=\"/documents/d/guest/image1\""));
		Assert.assertTrue(
			content.contains(
				"src=\"/documents/d/guest/image2?imagePreview=1\""));
	}

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	private CTCollection _ctCollection;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private CTCollectionService _ctCollectionService;

	@DeleteAfterTestRun
	private Group _group;

}