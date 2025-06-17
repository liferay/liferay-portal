/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.model.impl.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.fragment.constants.FragmentExportImportConstants;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.test.util.FragmentTestUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipReaderFactory;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactory;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;

import java.util.List;
import java.util.Map;

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
public class FragmentCollectionImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_fragmentCollection = FragmentTestUtil.addFragmentCollection(
			_group.getGroupId());

		Class<?> clazz = getClass();

		PortletFileRepositoryUtil.addPortletFileEntry(
			null, _fragmentCollection.getGroupId(), TestPropsValues.getUserId(),
			FragmentCollection.class.getName(),
			_fragmentCollection.getFragmentCollectionId(),
			FragmentPortletKeys.FRAGMENT,
			_fragmentCollection.getResourcesFolderId(),
			clazz.getResourceAsStream("dependencies/liferay.png"),
			"liferay.png", ContentTypes.IMAGE_PNG, false);
	}

	@Test
	@TestInfo({"LPD-33704", "LPD-55643"})
	public void testGetResourcesMap() throws Exception {
		Map<String, FileEntry> resourcesMap =
			_fragmentCollection.getResourcesMap();

		Assert.assertEquals(resourcesMap.toString(), 1, resourcesMap.size());

		FileEntry fileEntry = resourcesMap.get("liferay.png");

		_dlAppService.updateFileEntry(
			fileEntry.getFileEntryId(), null, null, "liferayUpdate", null, null,
			null, DLVersionNumberIncrease.NONE, (byte[])null, null, null, null,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		resourcesMap = _fragmentCollection.getResourcesMap();

		Assert.assertNotNull(resourcesMap.get("liferayUpdate.png"));
		Assert.assertEquals(resourcesMap.toString(), 1, resourcesMap.size());
	}

	@Test
	@TestInfo("LPD-33704")
	public void testPopulateZipWriter() throws Exception {
		ZipWriter zipWriter = _zipWriterFactory.getZipWriter();

		_fragmentCollection.populateZipWriter(zipWriter, "test");

		ZipReader zipReader = _zipReaderFactory.getZipReader(
			zipWriter.getFile());

		List<String> entries = zipReader.getEntries();

		for (String entry : entries) {
			if (StringUtil.endsWith(
					entry,
					FragmentExportImportConstants.FILE_NAME_COLLECTION)) {

				JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
					zipReader.getEntryAsString(entry));

				Assert.assertNotNull(jsonObject);
				Assert.assertEquals(
					jsonObject.getString("name"),
					_fragmentCollection.getName());
				Assert.assertEquals(
					jsonObject.getString("description"),
					_fragmentCollection.getDescription());
			}
			else if (StringUtil.contains(entry, "/resources")) {
				Assert.assertTrue(entry.contains("/resources/liferay.png"));
			}
		}

		FileUtil.delete(zipWriter.getFile());
	}

	@Inject
	private static DLAppService _dlAppService;

	private FragmentCollection _fragmentCollection;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private ZipReaderFactory _zipReaderFactory;

	@Inject
	private ZipWriterFactory _zipWriterFactory;

}