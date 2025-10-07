/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.info.item.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.GroupKeyInfoItemIdentifier;
import com.liferay.info.item.GroupUrlTitleInfoItemIdentifier;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class FileEntryInfoItemDetailsProviderTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(),
			ContentTypes.APPLICATION_OCTET_STREAM, null, null, null, null,
			ServiceContextTestUtil.getServiceContext());
	}

	@Test
	public void testGetInfoItemDetails() throws Exception {
		InfoItemDetailsProvider<FileEntry> infoItemDetailsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemDetailsProvider.class, FileEntry.class.getName());

		InfoItemDetails classPKInfoItemDetails =
			infoItemDetailsProvider.getInfoItemDetails(
				_group.getGroupId(), ClassPKInfoItemIdentifier.class,
				_fileEntry);

		Assert.assertEquals(
			FileEntry.class.getName(), classPKInfoItemDetails.getClassName());
		Assert.assertEquals(
			new InfoItemReference(
				FileEntry.class.getName(), _fileEntry.getFileEntryId()),
			classPKInfoItemDetails.getInfoItemReference());

		InfoItemDetails ercInfoItemDetails =
			infoItemDetailsProvider.getInfoItemDetails(
				_group.getGroupId(), ERCInfoItemIdentifier.class, _fileEntry);

		Assert.assertEquals(
			new InfoItemReference(
				FileEntry.class.getName(),
				new ERCInfoItemIdentifier(
					_fileEntry.getExternalReferenceCode())),
			ercInfoItemDetails.getInfoItemReference());

		InfoItemDetails randomGroupERCInfoItemDetails =
			infoItemDetailsProvider.getInfoItemDetails(
				RandomTestUtil.randomLong(), ERCInfoItemIdentifier.class,
				_fileEntry);

		Assert.assertEquals(
			new InfoItemReference(
				FileEntry.class.getName(),
				new ERCInfoItemIdentifier(
					_fileEntry.getExternalReferenceCode(),
					_group.getExternalReferenceCode())),
			randomGroupERCInfoItemDetails.getInfoItemReference());

		Assert.assertNull(
			infoItemDetailsProvider.getInfoItemDetails(
				_group.getGroupId(), GroupKeyInfoItemIdentifier.class,
				_fileEntry));

		InfoItemDetails groupUrlTitleInfoItemDetails =
			infoItemDetailsProvider.getInfoItemDetails(
				_group.getGroupId(), GroupUrlTitleInfoItemIdentifier.class,
				_fileEntry);

		Assert.assertEquals(
			new InfoItemReference(
				FileEntry.class.getName(),
				new GroupUrlTitleInfoItemIdentifier(
					_fileEntry.getGroupId(),
					String.valueOf(_fileEntry.getFileEntryId()))),
			groupUrlTitleInfoItemDetails.getInfoItemReference());
	}

	@Inject
	private DLAppLocalService _dlAppLocalService;

	private FileEntry _fileEntry;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

}