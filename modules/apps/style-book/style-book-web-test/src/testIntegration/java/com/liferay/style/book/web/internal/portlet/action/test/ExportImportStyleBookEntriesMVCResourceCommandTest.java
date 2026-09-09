/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.style.book.exception.DuplicateStyleBookEntryKeyException;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;

import java.io.File;

import java.util.Enumeration;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class ExportImportStyleBookEntriesMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_sourceGroup = GroupTestUtil.addGroup();
		_targetGroup = GroupTestUtil.addGroup();
	}

	@Test
	public void testExportImportMultipleStyleBookEntries() throws Exception {
		String styleBookEntryKey1 = RandomTestUtil.randomString();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_sourceGroup, TestPropsValues.getUserId());

		StyleBookEntry styleBookEntry1 =
			_styleBookEntryLocalService.addStyleBookEntry(
				null, TestPropsValues.getUserId(), _sourceGroup.getGroupId(),
				false, StringPool.BLANK, _read("frontend-tokens-values.json"),
				RandomTestUtil.randomString(), styleBookEntryKey1,
				RandomTestUtil.randomString(), serviceContext);

		String styleBookEntryKey2 = RandomTestUtil.randomString();

		StyleBookEntry styleBookEntry2 =
			_styleBookEntryLocalService.addStyleBookEntry(
				null, TestPropsValues.getUserId(), _sourceGroup.getGroupId(),
				false, StringPool.BLANK, _read("frontend-tokens-values.json"),
				RandomTestUtil.randomString(), styleBookEntryKey2,
				RandomTestUtil.randomString(), serviceContext);

		File file = ReflectionTestUtil.invoke(
			_exportStyleBookEntriesMVCResourceCommand,
			"_exportStyleBookEntries", new Class<?>[] {long[].class},
			new long[] {
				styleBookEntry1.getStyleBookEntryId(),
				styleBookEntry2.getStyleBookEntryId()
			});

		ReflectionTestUtil.invoke(
			_importStyleBookEntriesMVCActionCommand, "_importStyleBookEntries",
			new Class<?>[] {long.class, long.class, File.class, boolean.class},
			TestPropsValues.getUserId(), _targetGroup.getGroupId(), file,
			false);

		Assert.assertEquals(
			2,
			_styleBookEntryLocalService.getStyleBookEntriesCount(
				_targetGroup.getGroupId()));
		Assert.assertNotNull(
			_styleBookEntryLocalService.fetchStyleBookEntry(
				_targetGroup.getGroupId(), styleBookEntryKey1));
		Assert.assertNotNull(
			_styleBookEntryLocalService.fetchStyleBookEntry(
				_targetGroup.getGroupId(), styleBookEntryKey2));
	}

	@Test
	public void testExportImportSingleStyleBookEntry() throws Exception {
		String name = RandomTestUtil.randomString();
		String styleBookEntryKey = RandomTestUtil.randomString();
		String themeId = RandomTestUtil.randomString();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_sourceGroup, TestPropsValues.getUserId());

		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				null, TestPropsValues.getUserId(), _sourceGroup.getGroupId(),
				false, StringPool.BLANK, _read("frontend-tokens-values.json"),
				name, styleBookEntryKey, themeId, serviceContext);

		File file = ReflectionTestUtil.invoke(
			_exportStyleBookEntriesMVCResourceCommand,
			"_exportStyleBookEntries", new Class<?>[] {long[].class},
			new long[] {styleBookEntry.getStyleBookEntryId()});

		ReflectionTestUtil.invoke(
			_importStyleBookEntriesMVCActionCommand, "_importStyleBookEntries",
			new Class<?>[] {long.class, long.class, File.class, boolean.class},
			TestPropsValues.getUserId(), _targetGroup.getGroupId(), file,
			false);

		Assert.assertEquals(
			1,
			_styleBookEntryLocalService.getStyleBookEntriesCount(
				_targetGroup.getGroupId()));

		StyleBookEntry targetGroupStyleBookEntry =
			_styleBookEntryLocalService.fetchStyleBookEntry(
				_targetGroup.getGroupId(), styleBookEntryKey);

		Assert.assertNotNull(targetGroupStyleBookEntry);

		Assert.assertEquals(name, targetGroupStyleBookEntry.getName());
		Assert.assertEquals(themeId, targetGroupStyleBookEntry.getThemeId());

		JSONObject expectedFrontendTokensValuesJSONObject =
			JSONFactoryUtil.createJSONObject(
				styleBookEntry.getFrontendTokensValues());
		JSONObject actualFrontendTokensValuesJSONObject =
			JSONFactoryUtil.createJSONObject(
				targetGroupStyleBookEntry.getFrontendTokensValues());

		Assert.assertEquals(
			expectedFrontendTokensValuesJSONObject.toString(),
			actualFrontendTokensValuesJSONObject.toString());
	}

	@Test(expected = DuplicateStyleBookEntryKeyException.class)
	public void testExportImportSingleStyleBookEntryAndNotOverwrite()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_sourceGroup, TestPropsValues.getUserId());

		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				null, TestPropsValues.getUserId(), _sourceGroup.getGroupId(),
				false, StringPool.BLANK, _read("frontend-tokens-values.json"),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), serviceContext);

		File file = ReflectionTestUtil.invoke(
			_exportStyleBookEntriesMVCResourceCommand,
			"_exportStyleBookEntries", new Class<?>[] {long[].class},
			new long[] {styleBookEntry.getStyleBookEntryId()});

		ReflectionTestUtil.invoke(
			_importStyleBookEntriesMVCActionCommand, "_importStyleBookEntries",
			new Class<?>[] {long.class, long.class, File.class, boolean.class},
			TestPropsValues.getUserId(), _targetGroup.getGroupId(), file,
			false);

		StyleBookEntry updatedStyleBookEntry =
			_styleBookEntryLocalService.updateStyleBookEntry(
				styleBookEntry.getStyleBookEntryId(),
				_read("updated-frontend-tokens-values.json"),
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(
					_sourceGroup, TestPropsValues.getUserId()));

		ReflectionTestUtil.invoke(
			_exportStyleBookEntriesMVCResourceCommand,
			"_exportStyleBookEntries", new Class<?>[] {long[].class},
			new long[] {updatedStyleBookEntry.getStyleBookEntryId()});

		ReflectionTestUtil.invoke(
			_importStyleBookEntriesMVCActionCommand, "_importStyleBookEntries",
			new Class<?>[] {long.class, long.class, File.class, boolean.class},
			TestPropsValues.getUserId(), _targetGroup.getGroupId(), file,
			false);
	}

	@Test
	public void testExportImportSingleStyleBookEntryAndOverwrite()
		throws Exception {

		String styleBookEntryKey = RandomTestUtil.randomString();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_sourceGroup, TestPropsValues.getUserId());

		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				null, TestPropsValues.getUserId(), _sourceGroup.getGroupId(),
				false, StringPool.BLANK, _read("frontend-tokens-values.json"),
				RandomTestUtil.randomString(), styleBookEntryKey,
				RandomTestUtil.randomString(), serviceContext);

		File file = ReflectionTestUtil.invoke(
			_exportStyleBookEntriesMVCResourceCommand,
			"_exportStyleBookEntries", new Class<?>[] {long[].class},
			new long[] {styleBookEntry.getStyleBookEntryId()});

		ReflectionTestUtil.invoke(
			_importStyleBookEntriesMVCActionCommand, "_importStyleBookEntries",
			new Class<?>[] {long.class, long.class, File.class, boolean.class},
			TestPropsValues.getUserId(), _targetGroup.getGroupId(), file,
			false);

		String name = RandomTestUtil.randomString();

		StyleBookEntry updatedStyleBookEntry =
			_styleBookEntryLocalService.updateStyleBookEntry(
				styleBookEntry.getStyleBookEntryId(),
				_read("updated-frontend-tokens-values.json"), name,
				ServiceContextTestUtil.getServiceContext(
					_sourceGroup, TestPropsValues.getUserId()));

		file = ReflectionTestUtil.invoke(
			_exportStyleBookEntriesMVCResourceCommand,
			"_exportStyleBookEntries", new Class<?>[] {long[].class},
			new long[] {updatedStyleBookEntry.getStyleBookEntryId()});

		ReflectionTestUtil.invoke(
			_importStyleBookEntriesMVCActionCommand, "_importStyleBookEntries",
			new Class<?>[] {long.class, long.class, File.class, boolean.class},
			TestPropsValues.getUserId(), _targetGroup.getGroupId(), file, true);

		Assert.assertEquals(
			1,
			_styleBookEntryLocalService.getStyleBookEntriesCount(
				_targetGroup.getGroupId()));

		StyleBookEntry updatedTargetGroupStyleBookEntry =
			_styleBookEntryLocalService.fetchStyleBookEntry(
				_targetGroup.getGroupId(), styleBookEntryKey);

		Assert.assertEquals(name, updatedTargetGroupStyleBookEntry.getName());

		JSONObject expectedFrontendTokensValuesJSONObject =
			JSONFactoryUtil.createJSONObject(
				updatedStyleBookEntry.getFrontendTokensValues());
		JSONObject actualFrontendTokensValuesJSONObject =
			JSONFactoryUtil.createJSONObject(
				updatedTargetGroupStyleBookEntry.getFrontendTokensValues());

		Assert.assertEquals(
			expectedFrontendTokensValuesJSONObject.toString(),
			actualFrontendTokensValuesJSONObject.toString());
	}

	@Test
	public void testExportStyleBookEntries() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_sourceGroup, TestPropsValues.getUserId());

		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				null, TestPropsValues.getUserId(), _sourceGroup.getGroupId(),
				false, StringPool.BLANK, _read("frontend-tokens-values.json"),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), serviceContext);

		FileEntry fileEntry = _addFileEntry(styleBookEntry);

		_styleBookEntryLocalService.updatePreviewFileEntryId(
			styleBookEntry.getStyleBookEntryId(), fileEntry.getFileEntryId(),
			ServiceContextTestUtil.getServiceContext(
				_sourceGroup, TestPropsValues.getUserId()));

		File file = ReflectionTestUtil.invoke(
			_exportStyleBookEntriesMVCResourceCommand,
			"_exportStyleBookEntries", new Class<?>[] {long[].class},
			new long[] {styleBookEntry.getStyleBookEntryId()});

		try (ZipFile zipFile = new ZipFile(file)) {
			int count = 0;

			Enumeration<? extends ZipEntry> enumeration = zipFile.entries();

			while (enumeration.hasMoreElements()) {
				ZipEntry zipEntry = enumeration.nextElement();

				if (!zipEntry.isDirectory()) {
					_validateZipEntry(styleBookEntry, zipEntry, zipFile);

					count++;
				}
			}

			Assert.assertEquals(3, count);
		}
	}

	private FileEntry _addFileEntry(StyleBookEntry styleBookEntry)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_sourceGroup, TestPropsValues.getUserId());

		Repository repository = PortletFileRepositoryUtil.addPortletRepository(
			_sourceGroup.getGroupId(), RandomTestUtil.randomString(),
			serviceContext);

		Class<?> clazz = getClass();

		return PortletFileRepositoryUtil.addPortletFileEntry(
			null, _sourceGroup.getGroupId(), TestPropsValues.getUserId(),
			StyleBookEntry.class.getName(),
			styleBookEntry.getStyleBookEntryId(), RandomTestUtil.randomString(),
			repository.getDlFolderId(),
			clazz.getResourceAsStream("dependencies/thumbnail.png"),
			RandomTestUtil.randomString(), ContentTypes.IMAGE_PNG, false);
	}

	private boolean _isStyleBookDefinitionFile(String path) {
		String[] pathParts = StringUtil.split(path, CharPool.SLASH);

		if ((pathParts.length == 2) &&
			Objects.equals(pathParts[1], "style-book.json")) {

			return true;
		}

		return false;
	}

	private boolean _isStyleBookThumbnailFile(String fileName) {
		String[] pathParts = StringUtil.split(fileName, CharPool.SLASH);

		if ((pathParts.length == 2) &&
			Objects.equals(pathParts[1], "thumbnail.png")) {

			return true;
		}

		return false;
	}

	private boolean _isStyleBookTokensValuesFile(String path) {
		String[] pathParts = StringUtil.split(path, CharPool.SLASH);

		if ((pathParts.length == 2) &&
			Objects.equals(pathParts[1], "frontend-tokens-values.json")) {

			return true;
		}

		return false;
	}

	private String _read(String fileName) throws Exception {
		return new String(
			FileUtil.getBytes(getClass(), "dependencies/" + fileName));
	}

	private void _validateZipEntry(
			StyleBookEntry styleBookEntry, ZipEntry zipEntry, ZipFile zipFile)
		throws Exception {

		if (_isStyleBookDefinitionFile(zipEntry.getName())) {
			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				StringUtil.read(zipFile.getInputStream(zipEntry)));

			Assert.assertEquals(
				styleBookEntry.getName(), jsonObject.getString("name"));
			Assert.assertEquals(
				"frontend-tokens-values.json",
				jsonObject.getString("frontendTokensValuesPath"));
		}

		if (_isStyleBookTokensValuesFile(zipEntry.getName())) {
			JSONObject expectedFrontendTokensValuesJSONObject =
				JSONFactoryUtil.createJSONObject(
					styleBookEntry.getFrontendTokensValues());

			JSONObject actualFrontendTokensValuesJSONObject =
				JSONFactoryUtil.createJSONObject(
					StringUtil.read(zipFile.getInputStream(zipEntry)));

			Assert.assertEquals(
				expectedFrontendTokensValuesJSONObject.toString(),
				actualFrontendTokensValuesJSONObject.toString());
		}

		if (_isStyleBookThumbnailFile(zipEntry.getName())) {
			Assert.assertArrayEquals(
				FileUtil.getBytes(getClass(), "dependencies/thumbnail.png"),
				FileUtil.getBytes(zipFile.getInputStream(zipEntry)));
		}
	}

	@Inject(filter = "mvc.command.name=/style_book/export_style_book_entries")
	private MVCResourceCommand _exportStyleBookEntriesMVCResourceCommand;

	@Inject(filter = "mvc.command.name=/style_book/import_style_book_entries")
	private MVCActionCommand _importStyleBookEntriesMVCActionCommand;

	@DeleteAfterTestRun
	private Group _sourceGroup;

	@Inject
	private StyleBookEntryLocalService _styleBookEntryLocalService;

	@DeleteAfterTestRun
	private Group _targetGroup;

}