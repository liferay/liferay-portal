/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.item.selector.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.helper.DLURLHelper;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.item.selector.ItemSelectorReturnTypeResolver;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class FileEntryAMImageFileEntryItemSelectorReturnTypeResolverTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		UserTestUtil.setUser(TestPropsValues.getUser());
	}

	@Test
	public void testAddingFileEntryWithImageCreatesMedia() throws Exception {
		FileEntry fileEntry = _addImageFileEntry(
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			_itemSelectorReturnTypeResolver.getValue(fileEntry, null));

		String url = jsonObject.getString("url");
		long fileEntryId = jsonObject.getLong("fileEntryId");

		Assert.assertEquals(
			_dlURLHelper.getPreviewURL(
				fileEntry, fileEntry.getFileVersion(), null, StringPool.BLANK,
				false, false),
			url);

		Assert.assertEquals(fileEntry.getFileEntryId(), fileEntryId);
	}

	private FileEntry _addImageFileEntry(ServiceContext serviceContext)
		throws Exception {

		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), ContentTypes.IMAGE_JPEG,
			_getImageBytes(), null, null, null, serviceContext);
	}

	private byte[] _getImageBytes() throws Exception {
		return FileUtil.getBytes(
			FileEntryAMImageFileEntryItemSelectorReturnTypeResolverTest.class,
			"dependencies/image.jpg");
	}

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLURLHelper _dlURLHelper;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "component.name=com.liferay.adaptive.media.image.item.selector.internal.FileEntryAMImageFileEntryItemSelectorReturnTypeResolver"
	)
	private ItemSelectorReturnTypeResolver<?, FileEntry>
		_itemSelectorReturnTypeResolver;

}