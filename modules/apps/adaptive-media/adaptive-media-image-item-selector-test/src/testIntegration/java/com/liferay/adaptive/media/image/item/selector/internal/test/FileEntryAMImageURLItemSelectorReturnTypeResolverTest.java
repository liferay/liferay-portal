/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.item.selector.internal.test;

import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationHelper;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.helper.DLURLHelper;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.item.selector.ItemSelectorReturnTypeResolver;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
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
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Sergio González
 */
@RunWith(Arquillian.class)
public class FileEntryAMImageURLItemSelectorReturnTypeResolverTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		UserTestUtil.setUser(TestPropsValues.getUser());

		Collection<AMImageConfigurationEntry> amImageConfigurationEntries =
			_amImageConfigurationHelper.getAMImageConfigurationEntries(
				TestPropsValues.getCompanyId(),
				amImageConfigurationEntry -> true);

		for (AMImageConfigurationEntry amImageConfigurationEntry :
				amImageConfigurationEntries) {

			_amImageConfigurationHelper.forceDeleteAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(),
				amImageConfigurationEntry.getUUID());
		}
	}

	@After
	public void tearDown() throws Exception {
		Collection<AMImageConfigurationEntry> amImageConfigurationEntries =
			_amImageConfigurationHelper.getAMImageConfigurationEntries(
				TestPropsValues.getCompanyId(),
				amImageConfigurationEntry -> true);

		for (AMImageConfigurationEntry amImageConfigurationEntry :
				amImageConfigurationEntries) {

			_amImageConfigurationHelper.forceDeleteAMImageConfigurationEntry(
				TestPropsValues.getCompanyId(),
				amImageConfigurationEntry.getUUID());
		}
	}

	@Test
	public void testAddingFileEntryWithHDMediaQueries() throws Exception {
		_addTestVariant("small", "uuid0", 50, 50);
		_addTestVariant("small.hd", "uuid1", 100, 100);
		_addTestVariant("medium", "uuid2", 300, 300);

		FileEntry fileEntry = _addImageFileEntry(
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			_itemSelectorReturnTypeResolver.getValue(fileEntry, null));

		String defaultSource = jsonObject.getString("defaultSource");

		Assert.assertEquals(
			_dlURLHelper.getImagePreviewURL(
				fileEntry, fileEntry.getFileVersion(), null, StringPool.BLANK,
				false, false),
			defaultSource);

		JSONArray sourcesJSONArray = jsonObject.getJSONArray("sources");

		Assert.assertEquals(3, sourcesJSONArray.length());

		_assertHDSrcSource(
			sourcesJSONArray.getJSONObject(0), fileEntry.getFileEntryId(),
			"uuid0", "uuid1", fileEntry.getTitle());
		_assertSrcSource(
			sourcesJSONArray.getJSONObject(1), fileEntry.getFileEntryId(),
			"uuid1", fileEntry.getTitle());
		_assertSrcSource(
			sourcesJSONArray.getJSONObject(2), fileEntry.getFileEntryId(),
			"uuid2", fileEntry.getTitle());

		_assertAttibutes(sourcesJSONArray.getJSONObject(0), 50, 0);
		_assertAttibutes(sourcesJSONArray.getJSONObject(1), 100, 50);
		_assertAttibutes(sourcesJSONArray.getJSONObject(2), 300, 100);
	}

	@Test
	public void testAddingFileEntryWithImageCreatesMedia() throws Exception {
		_addTestVariant("small", "uuid0", 50, 50);
		_addTestVariant("big", "uuid1", 400, 280);
		_addTestVariant("medium", "uuid2", 300, 200);
		_addTestVariant("extra", "uuid3", 500, 330);

		FileEntry fileEntry = _addImageFileEntry(
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			_itemSelectorReturnTypeResolver.getValue(fileEntry, null));

		String defaultSource = jsonObject.getString("defaultSource");

		Assert.assertEquals(
			_dlURLHelper.getImagePreviewURL(
				fileEntry, fileEntry.getFileVersion(), null, StringPool.BLANK,
				false, false),
			defaultSource);

		JSONArray sourcesJSONArray = jsonObject.getJSONArray("sources");

		Assert.assertEquals(4, sourcesJSONArray.length());

		_assertSrcSource(
			sourcesJSONArray.getJSONObject(0), fileEntry.getFileEntryId(),
			"uuid0", fileEntry.getTitle());
		_assertSrcSource(
			sourcesJSONArray.getJSONObject(1), fileEntry.getFileEntryId(),
			"uuid2", fileEntry.getTitle());
		_assertSrcSource(
			sourcesJSONArray.getJSONObject(2), fileEntry.getFileEntryId(),
			"uuid1", fileEntry.getTitle());
		_assertSrcSource(
			sourcesJSONArray.getJSONObject(3), fileEntry.getFileEntryId(),
			"uuid3", fileEntry.getTitle());

		_assertAttibutes(sourcesJSONArray.getJSONObject(0), 50, 0);
		_assertAttibutes(sourcesJSONArray.getJSONObject(1), 200, 50);
		_assertAttibutes(sourcesJSONArray.getJSONObject(2), 280, 200);
		_assertAttibutes(sourcesJSONArray.getJSONObject(3), 330, 280);
	}

	@Test
	public void testHDMediaQueryAppliesWhenHeightHas1PXLessThanExpected()
		throws Exception {

		FileEntry fileEntry = _addImageFileEntry(
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));

		_addTestVariant("small", "uuid0", 50, 50);
		_addTestVariant("small.hd", "uuid1", 99, 100);

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			_itemSelectorReturnTypeResolver.getValue(fileEntry, null));

		String defaultSource = jsonObject.getString("defaultSource");

		Assert.assertEquals(
			_dlURLHelper.getImagePreviewURL(
				fileEntry, fileEntry.getFileVersion(), null, StringPool.BLANK,
				false, false),
			defaultSource);

		JSONArray sourcesJSONArray = jsonObject.getJSONArray("sources");

		Assert.assertEquals(2, sourcesJSONArray.length());

		_assertHDSrcSource(
			sourcesJSONArray.getJSONObject(0), fileEntry.getFileEntryId(),
			"uuid0", "uuid1", fileEntry.getTitle());
		_assertSrcSource(
			sourcesJSONArray.getJSONObject(1), fileEntry.getFileEntryId(),
			"uuid1", fileEntry.getTitle());

		_assertAttibutes(sourcesJSONArray.getJSONObject(0), 50, 0);
		_assertAttibutes(sourcesJSONArray.getJSONObject(1), 100, 50);
	}

	@Test
	public void testHDMediaQueryAppliesWhenHeightHas1PXMoreThanExpected()
		throws Exception {

		_addTestVariant("small", "uuid0", 50, 50);
		_addTestVariant("small.hd", "uuid1", 101, 100);

		FileEntry fileEntry = _addImageFileEntry(
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			_itemSelectorReturnTypeResolver.getValue(fileEntry, null));

		String defaultSource = jsonObject.getString("defaultSource");

		Assert.assertEquals(
			_dlURLHelper.getImagePreviewURL(
				fileEntry, fileEntry.getFileVersion(), null, StringPool.BLANK,
				false, false),
			defaultSource);

		JSONArray sourcesJSONArray = jsonObject.getJSONArray("sources");

		Assert.assertEquals(2, sourcesJSONArray.length());

		_assertHDSrcSource(
			sourcesJSONArray.getJSONObject(0), fileEntry.getFileEntryId(),
			"uuid0", "uuid1", fileEntry.getTitle());
		_assertSrcSource(
			sourcesJSONArray.getJSONObject(1), fileEntry.getFileEntryId(),
			"uuid1", fileEntry.getTitle());

		_assertAttibutes(sourcesJSONArray.getJSONObject(0), 50, 0);
		_assertAttibutes(sourcesJSONArray.getJSONObject(1), 100, 50);
	}

	@Test
	public void testHDMediaQueryAppliesWhenWidthHas1PXLessThanExpected()
		throws Exception {

		_addTestVariant("small", "uuid0", 50, 50);
		_addTestVariant("small.hd", "uuid1", 100, 99);

		FileEntry fileEntry = _addImageFileEntry(
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			_itemSelectorReturnTypeResolver.getValue(fileEntry, null));

		String defaultSource = jsonObject.getString("defaultSource");

		Assert.assertEquals(
			_dlURLHelper.getImagePreviewURL(
				fileEntry, fileEntry.getFileVersion(), null, StringPool.BLANK,
				false, false),
			defaultSource);

		JSONArray sourcesJSONArray = jsonObject.getJSONArray("sources");

		Assert.assertEquals(2, sourcesJSONArray.length());

		_assertHDSrcSource(
			sourcesJSONArray.getJSONObject(0), fileEntry.getFileEntryId(),
			"uuid0", "uuid1", fileEntry.getTitle());
		_assertSrcSource(
			sourcesJSONArray.getJSONObject(1), fileEntry.getFileEntryId(),
			"uuid1", fileEntry.getTitle());

		_assertAttibutes(sourcesJSONArray.getJSONObject(0), 50, 0);
		_assertAttibutes(sourcesJSONArray.getJSONObject(1), 99, 50);
	}

	@Test
	public void testHDMediaQueryAppliesWhenWidthHas1PXMoreThanExpected()
		throws Exception {

		FileEntry fileEntry = _addImageFileEntry(
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));

		_addTestVariant("small", "uuid0", 50, 50);
		_addTestVariant("small.hd", "uuid1", 100, 101);

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			_itemSelectorReturnTypeResolver.getValue(fileEntry, null));

		String defaultSource = jsonObject.getString("defaultSource");

		Assert.assertEquals(
			_dlURLHelper.getImagePreviewURL(
				fileEntry, fileEntry.getFileVersion(), null, StringPool.BLANK,
				false, false),
			defaultSource);

		JSONArray sourcesJSONArray = jsonObject.getJSONArray("sources");

		Assert.assertEquals(2, sourcesJSONArray.length());

		_assertHDSrcSource(
			sourcesJSONArray.getJSONObject(0), fileEntry.getFileEntryId(),
			"uuid0", "uuid1", fileEntry.getTitle());
		_assertSrcSource(
			sourcesJSONArray.getJSONObject(1), fileEntry.getFileEntryId(),
			"uuid1", fileEntry.getTitle());

		_assertAttibutes(sourcesJSONArray.getJSONObject(0), 50, 0);
		_assertAttibutes(sourcesJSONArray.getJSONObject(1), 101, 50);
	}

	@Test
	public void testHDMediaQueryNotAppliesWhenHeightHas2PXLessThanExpected()
		throws Exception {

		FileEntry fileEntry = _addImageFileEntry(
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));

		_addTestVariant("small", "uuid0", 50, 50);
		_addTestVariant("small.hd", "uuid1", 98, 200);

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			_itemSelectorReturnTypeResolver.getValue(fileEntry, null));

		String defaultSource = jsonObject.getString("defaultSource");

		Assert.assertEquals(
			_dlURLHelper.getImagePreviewURL(
				fileEntry, fileEntry.getFileVersion(), null, StringPool.BLANK,
				false, false),
			defaultSource);

		JSONArray sourcesJSONArray = jsonObject.getJSONArray("sources");

		Assert.assertEquals(2, sourcesJSONArray.length());

		_assertSrcSource(
			sourcesJSONArray.getJSONObject(0), fileEntry.getFileEntryId(),
			"uuid0", fileEntry.getTitle());
		_assertSrcSource(
			sourcesJSONArray.getJSONObject(1), fileEntry.getFileEntryId(),
			"uuid1", fileEntry.getTitle());

		_assertAttibutes(sourcesJSONArray.getJSONObject(0), 50, 0);
		_assertAttibutes(sourcesJSONArray.getJSONObject(1), 200, 50);
	}

	@Test
	public void testHDMediaQueryNotAppliesWhenHeightHas2PXMoreThanExpected()
		throws Exception {

		FileEntry fileEntry = _addImageFileEntry(
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));

		_addTestVariant("small", "uuid0", 50, 50);
		_addTestVariant("small.hd", "uuid1", 102, 200);

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			_itemSelectorReturnTypeResolver.getValue(fileEntry, null));

		String defaultSource = jsonObject.getString("defaultSource");

		Assert.assertEquals(
			_dlURLHelper.getImagePreviewURL(
				fileEntry, fileEntry.getFileVersion(), null, StringPool.BLANK,
				false, false),
			defaultSource);

		JSONArray sourcesJSONArray = jsonObject.getJSONArray("sources");

		Assert.assertEquals(2, sourcesJSONArray.length());

		_assertSrcSource(
			sourcesJSONArray.getJSONObject(0), fileEntry.getFileEntryId(),
			"uuid0", fileEntry.getTitle());
		_assertSrcSource(
			sourcesJSONArray.getJSONObject(1), fileEntry.getFileEntryId(),
			"uuid1", fileEntry.getTitle());

		_assertAttibutes(sourcesJSONArray.getJSONObject(0), 50, 0);
		_assertAttibutes(sourcesJSONArray.getJSONObject(1), 200, 50);
	}

	@Test
	public void testHDMediaQueryNotAppliesWhenWidthHas2PXLessThanExpected()
		throws Exception {

		_addTestVariant("small", "uuid0", 50, 50);
		_addTestVariant("small.hd", "uuid1", 200, 98);

		FileEntry fileEntry = _addImageFileEntry(
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			_itemSelectorReturnTypeResolver.getValue(fileEntry, null));

		String defaultSource = jsonObject.getString("defaultSource");

		Assert.assertEquals(
			_dlURLHelper.getImagePreviewURL(
				fileEntry, fileEntry.getFileVersion(), null, StringPool.BLANK,
				false, false),
			defaultSource);

		JSONArray sourcesJSONArray = jsonObject.getJSONArray("sources");

		Assert.assertEquals(2, sourcesJSONArray.length());

		_assertSrcSource(
			sourcesJSONArray.getJSONObject(0), fileEntry.getFileEntryId(),
			"uuid0", fileEntry.getTitle());
		_assertSrcSource(
			sourcesJSONArray.getJSONObject(1), fileEntry.getFileEntryId(),
			"uuid1", fileEntry.getTitle());

		_assertAttibutes(sourcesJSONArray.getJSONObject(0), 50, 0);
		_assertAttibutes(sourcesJSONArray.getJSONObject(1), 98, 50);
	}

	@Test
	public void testHDMediaQueryNotAppliesWhenWidthHas2PXMoreThanExpected()
		throws Exception {

		_addTestVariant("small", "uuid0", 50, 50);
		_addTestVariant("small.hd", "uuid1", 200, 102);

		FileEntry fileEntry = _addImageFileEntry(
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			_itemSelectorReturnTypeResolver.getValue(fileEntry, null));

		String defaultSource = jsonObject.getString("defaultSource");

		Assert.assertEquals(
			_dlURLHelper.getImagePreviewURL(
				fileEntry, fileEntry.getFileVersion(), null, StringPool.BLANK,
				false, false),
			defaultSource);

		JSONArray sourcesJSONArray = jsonObject.getJSONArray("sources");

		Assert.assertEquals(2, sourcesJSONArray.length());

		_assertSrcSource(
			sourcesJSONArray.getJSONObject(0), fileEntry.getFileEntryId(),
			"uuid0", fileEntry.getTitle());
		_assertSrcSource(
			sourcesJSONArray.getJSONObject(1), fileEntry.getFileEntryId(),
			"uuid1", fileEntry.getTitle());

		_assertAttibutes(sourcesJSONArray.getJSONObject(0), 50, 0);
		_assertAttibutes(sourcesJSONArray.getJSONObject(1), 102, 50);
	}

	private FileEntry _addImageFileEntry(ServiceContext serviceContext)
		throws Exception {

		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), ContentTypes.IMAGE_JPEG,
			_getImageBytes(), null, null, null, serviceContext);
	}

	private void _addTestVariant(
			String name, String uuid, int height, int width)
		throws Exception {

		_amImageConfigurationHelper.addAMImageConfigurationEntry(
			TestPropsValues.getCompanyId(), name, StringPool.BLANK, uuid,
			HashMapBuilder.put(
				"max-height", String.valueOf(height)
			).put(
				"max-width", String.valueOf(width)
			).build());
	}

	private void _assertAttibutes(
		JSONObject sourceJSONObject, int expectedMaxWidth,
		int expectedMinWidth) {

		JSONObject attributesJSONObject = sourceJSONObject.getJSONObject(
			"attributes");

		JSONArray attributeNamesJSONArray = attributesJSONObject.names();

		boolean assertedMaxWidth = false;
		boolean assertedMinWidth = false;

		for (int i = 0; i < attributeNamesJSONArray.length(); i++) {
			String key = attributeNamesJSONArray.getString(i);

			Assert.assertTrue(
				"Unexpected attribute found '" + key + "'",
				key.equals("max-width") || key.equals("min-width"));

			String value = attributesJSONObject.getString(key);

			if (key.equals("max-width")) {
				Assert.assertEquals(expectedMaxWidth + "px", value);

				assertedMaxWidth = true;
			}
			else if (key.equals("min-width")) {
				Assert.assertEquals(expectedMinWidth + "px", value);

				assertedMinWidth = true;
			}
		}

		Assert.assertTrue(
			StringBundler.concat(
				"Could not find expected max-width of '", expectedMaxWidth,
				"' in '", sourceJSONObject, "'"),
			(expectedMaxWidth == 0) || assertedMaxWidth);
		Assert.assertTrue(
			StringBundler.concat(
				"Could not find expected min-width of '", expectedMinWidth,
				"' in '", sourceJSONObject, "'"),
			(expectedMinWidth == 0) || assertedMinWidth);
	}

	private void _assertHDSrcSource(
		JSONObject sourceJSONObject, long fileEntryId,
		String originalConfigurationEntryUuid, String hdConfigurationEntryUuid,
		String title) {

		String srcSource = sourceJSONObject.getString("src");

		Matcher matcher = _pattern.matcher(srcSource);

		Assert.assertEquals(
			StringBundler.concat(
				"/o/adaptive-media/image/", fileEntryId, "/",
				originalConfigurationEntryUuid, "/", title,
				", /o/adaptive-media/image/", fileEntryId, "/",
				hdConfigurationEntryUuid, "/", title, " 2x"),
			matcher.replaceAll(StringPool.BLANK));
	}

	private void _assertSrcSource(
		JSONObject sourceJSONObject, long fileEntryId,
		String configurationEntryUuid, String title) {

		String srcSource = sourceJSONObject.getString("src");

		Matcher matcher = _pattern.matcher(srcSource);

		Assert.assertEquals(
			StringBundler.concat(
				"/o/adaptive-media/image/", fileEntryId, "/",
				configurationEntryUuid, "/", title),
			matcher.replaceFirst(StringPool.BLANK));
	}

	private byte[] _getImageBytes() throws Exception {
		return FileUtil.getBytes(
			FileEntryAMImageURLItemSelectorReturnTypeResolverTest.class,
			"dependencies/image.jpg");
	}

	private static final Pattern _pattern = Pattern.compile("\\?t=\\d+");

	@Inject
	private AMImageConfigurationHelper _amImageConfigurationHelper;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLURLHelper _dlURLHelper;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "component.name=com.liferay.adaptive.media.image.item.selector.internal.FileEntryAMImageURLItemSelectorReturnTypeResolver"
	)
	private ItemSelectorReturnTypeResolver<?, FileEntry>
		_itemSelectorReturnTypeResolver;

}