/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.content.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.content.model.DLContent;
import com.liferay.document.library.content.service.DLContentLocalService;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tina Tian
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class DLContentLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() throws Exception {
		_companyId = RandomTestUtil.nextLong();
		_repositoryId = RandomTestUtil.nextLong();
	}

	@After
	public void tearDown() throws Exception {
		_dlContentLocalService.deleteContentsByDirectory(
			_companyId, _repositoryId, StringPool.SLASH);
	}

	@Test
	public void testAddContentByByteArrayInputStream() throws Exception {
		String path = RandomTestUtil.randomString();

		DLContent addDLContent = _dlContentLocalService.addContent(
			_companyId, _repositoryId, path, Store.VERSION_DEFAULT,
			new ByteArrayInputStream(_DATA_VERSION_1));

		DLContent getDLContent = _dlContentLocalService.getContent(
			_companyId, _repositoryId, path, StringPool.BLANK);

		Assert.assertEquals(addDLContent, getDLContent);
	}

	@Test
	public void testAddContentByFileInputStream() throws Exception {
		String path = RandomTestUtil.randomString();

		File tempFile = FileUtil.createTempFile(_DATA_VERSION_1);

		try {
			DLContent addDLContent = _dlContentLocalService.addContent(
				_companyId, _repositoryId, path, Store.VERSION_DEFAULT,
				new FileInputStream(tempFile));

			DLContent getDLContent = _dlContentLocalService.getContent(
				_companyId, _repositoryId, path, StringPool.BLANK);

			Assert.assertEquals(addDLContent, getDLContent);
		}
		finally {
			tempFile.delete();
		}
	}

	@Test
	public void testAddContentByPetraUnsyncByteArrayInputStream()
		throws Exception {

		String path = RandomTestUtil.randomString();

		DLContent addDLContent = _dlContentLocalService.addContent(
			_companyId, _repositoryId, path, Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(_DATA_VERSION_1));

		DLContent getDLContent = _dlContentLocalService.getContent(
			_companyId, _repositoryId, path, StringPool.BLANK);

		Assert.assertEquals(addDLContent, getDLContent);
	}

	@Test
	public void testDeleteContent() throws Exception {
		String path = RandomTestUtil.randomString();

		_dlContentLocalService.addContent(
			_companyId, _repositoryId, path, Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(_DATA_VERSION_1));

		Assert.assertTrue(
			_dlContentLocalService.hasContent(
				_companyId, _repositoryId, path, Store.VERSION_DEFAULT));

		_dlContentLocalService.deleteContent(
			_companyId, _repositoryId, path, Store.VERSION_DEFAULT);

		Assert.assertFalse(
			_dlContentLocalService.hasContent(
				_companyId, _repositoryId, path, Store.VERSION_DEFAULT));
	}

	@Test
	public void testDeleteContentsByDirectory() throws Exception {
		String directory = RandomTestUtil.randomString();

		String path1 = directory + "/" + RandomTestUtil.randomString();
		String path2 = StringBundler.concat(
			directory, "/", RandomTestUtil.randomString(), "/",
			RandomTestUtil.randomString());

		_dlContentLocalService.addContent(
			_companyId, _repositoryId, path1, Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(_DATA_VERSION_1));

		_dlContentLocalService.addContent(
			_companyId, _repositoryId, path2, Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(_DATA_VERSION_1));

		Assert.assertTrue(
			_dlContentLocalService.hasContent(
				_companyId, _repositoryId, path1, Store.VERSION_DEFAULT));
		Assert.assertTrue(
			_dlContentLocalService.hasContent(
				_companyId, _repositoryId, path2, Store.VERSION_DEFAULT));

		_dlContentLocalService.deleteContentsByDirectory(
			_companyId, _repositoryId, directory);

		Assert.assertFalse(
			_dlContentLocalService.hasContent(
				_companyId, _repositoryId, path1, Store.VERSION_DEFAULT));
		Assert.assertFalse(
			_dlContentLocalService.hasContent(
				_companyId, _repositoryId, path2, Store.VERSION_DEFAULT));
	}

	@Test
	public void testGetContentLatest() throws Exception {
		String path = RandomTestUtil.randomString();

		DLContent addDLContent1 = _dlContentLocalService.addContent(
			_companyId, _repositoryId, path, Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(_DATA_VERSION_1));

		DLContent getDLContent1 = _dlContentLocalService.getContent(
			_companyId, _repositoryId, path, StringPool.BLANK);

		Assert.assertEquals(addDLContent1, getDLContent1);

		DLContent addDLContent2 = _dlContentLocalService.addContent(
			_companyId, _repositoryId, path, "1.1",
			new UnsyncByteArrayInputStream(_DATA_VERSION_2));

		DLContent getDLContent2 = _dlContentLocalService.getContent(
			_companyId, _repositoryId, path, StringPool.BLANK);

		Assert.assertEquals(addDLContent2, getDLContent2);
	}

	@Test
	public void testGetContentVersion() throws Exception {
		String path = RandomTestUtil.randomString();

		DLContent addDLContent1 = _dlContentLocalService.addContent(
			_companyId, _repositoryId, path, Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(_DATA_VERSION_1));

		DLContent getDLContent1 = _dlContentLocalService.getContent(
			_companyId, _repositoryId, path, Store.VERSION_DEFAULT);

		assertEquals(addDLContent1, getDLContent1);

		DLContent addDLContent2 = _dlContentLocalService.addContent(
			_companyId, _repositoryId, path, "1.1",
			new UnsyncByteArrayInputStream(_DATA_VERSION_2));

		DLContent getDLContent2 = _dlContentLocalService.getContent(
			_companyId, _repositoryId, path, "1.1");

		assertEquals(addDLContent2, getDLContent2);
	}

	@Test
	public void testGetContentsAll() throws Exception {
		DLContent dlContent1 = _dlContentLocalService.addContent(
			_companyId, _repositoryId, RandomTestUtil.randomString(),
			Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(_DATA_VERSION_1));
		DLContent dlContent2 = _dlContentLocalService.addContent(
			_companyId, _repositoryId, RandomTestUtil.randomString(),
			Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(_DATA_VERSION_1));
		DLContent dlContent3 = _dlContentLocalService.addContent(
			_companyId, _repositoryId, RandomTestUtil.randomString(),
			Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(_DATA_VERSION_1));

		List<DLContent> dlContents = _dlContentLocalService.getContents(
			_companyId, _repositoryId);

		Assert.assertEquals(dlContents.toString(), 3, dlContents.size());
		Assert.assertTrue(
			dlContents.toString(), dlContents.contains(dlContent1));
		Assert.assertTrue(
			dlContents.toString(), dlContents.contains(dlContent2));
		Assert.assertTrue(
			dlContents.toString(), dlContents.contains(dlContent3));
	}

	@Test
	public void testGetContentsByDirectory() throws Exception {
		String path1 = RandomTestUtil.randomString();
		String path2 = RandomTestUtil.randomString();

		DLContent dlContent1 = _dlContentLocalService.addContent(
			_companyId, _repositoryId,
			path1 + "/" + RandomTestUtil.randomString(), Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(_DATA_VERSION_1));
		DLContent dlContent2 = _dlContentLocalService.addContent(
			_companyId, _repositoryId,
			path2 + "/" + RandomTestUtil.randomString(), Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(_DATA_VERSION_1));

		List<DLContent> dlContents =
			_dlContentLocalService.getContentsByDirectory(
				_companyId, _repositoryId, path1);

		Assert.assertEquals(dlContents.toString(), 1, dlContents.size());
		Assert.assertTrue(
			dlContents.toString(), dlContents.contains(dlContent1));

		dlContents = _dlContentLocalService.getContentsByDirectory(
			_companyId, _repositoryId, path2);

		Assert.assertEquals(dlContents.toString(), 1, dlContents.size());
		Assert.assertEquals(dlContent2, dlContents.get(0));
	}

	@Test
	public void testGetContentsVersions() throws Exception {
		String path1 = RandomTestUtil.randomString();
		String path2 = RandomTestUtil.randomString();

		DLContent dlContent1 = _dlContentLocalService.addContent(
			_companyId, _repositoryId, path1, Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(_DATA_VERSION_1));
		DLContent dlContent2 = _dlContentLocalService.addContent(
			_companyId, _repositoryId, path1, "1.1",
			new UnsyncByteArrayInputStream(_DATA_VERSION_2));
		DLContent dlContent3 = _dlContentLocalService.addContent(
			_companyId, _repositoryId, path2, Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(_DATA_VERSION_1));
		DLContent dlContent4 = _dlContentLocalService.addContent(
			_companyId, _repositoryId, path2, "1.1",
			new UnsyncByteArrayInputStream(_DATA_VERSION_2));

		List<DLContent> dlContents1 = _dlContentLocalService.getContents(
			_companyId, _repositoryId, path1);

		Assert.assertEquals(dlContents1.toString(), 2, dlContents1.size());
		Assert.assertTrue(
			dlContents1.toString(), dlContents1.contains(dlContent1));
		Assert.assertTrue(
			dlContents1.toString(), dlContents1.contains(dlContent2));

		List<DLContent> dlContents2 = _dlContentLocalService.getContents(
			_companyId, _repositoryId, path2);

		Assert.assertEquals(dlContents2.toString(), 2, dlContents2.size());
		Assert.assertTrue(
			dlContents2.toString(), dlContents2.contains(dlContent3));
		Assert.assertTrue(
			dlContents2.toString(), dlContents2.contains(dlContent4));
	}

	@Test
	public void testHasContent() throws Exception {
		String path = RandomTestUtil.randomString();

		// 1.0

		Assert.assertFalse(
			_dlContentLocalService.hasContent(
				_companyId, _repositoryId, path, Store.VERSION_DEFAULT));

		_dlContentLocalService.addContent(
			_companyId, _repositoryId, path, Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(_DATA_VERSION_1));

		Assert.assertTrue(
			_dlContentLocalService.hasContent(
				_companyId, _repositoryId, path, Store.VERSION_DEFAULT));

		// 1.1

		Assert.assertFalse(
			_dlContentLocalService.hasContent(
				_companyId, _repositoryId, path, "1.1"));

		_dlContentLocalService.addContent(
			_companyId, _repositoryId, path, "1.1",
			new UnsyncByteArrayInputStream(_DATA_VERSION_1));

		Assert.assertTrue(
			_dlContentLocalService.hasContent(
				_companyId, _repositoryId, path, "1.1"));
	}

	@Test
	public void testUpdateContent() throws Exception {
		long newRepositoryId = RandomTestUtil.nextLong();
		String oldPath = RandomTestUtil.randomString();
		String newPath = RandomTestUtil.randomString();

		DLContent addDLContent = _dlContentLocalService.addContent(
			_companyId, _repositoryId, oldPath, Store.VERSION_DEFAULT,
			new UnsyncByteArrayInputStream(_DATA_VERSION_1));

		_dlContentLocalService.updateDLContent(
			_companyId, _repositoryId, newRepositoryId, oldPath, newPath);

		DLContent getDLContent = _dlContentLocalService.getContent(
			_companyId, newRepositoryId, newPath, StringPool.BLANK);

		addDLContent.setRepositoryId(newRepositoryId);
		addDLContent.setPath(newPath);

		assertEquals(addDLContent, getDLContent);

		_dlContentLocalService.deleteDLContent(getDLContent);
	}

	protected void assertEquals(
			DLContent expectedDLContent, DLContent actualDLContent)
		throws Exception {

		Assert.assertEquals(
			expectedDLContent.getContentId(), actualDLContent.getContentId());
		Assert.assertEquals(
			expectedDLContent.getGroupId(), actualDLContent.getGroupId());
		Assert.assertEquals(
			expectedDLContent.getCompanyId(), actualDLContent.getCompanyId());
		Assert.assertEquals(
			expectedDLContent.getRepositoryId(),
			actualDLContent.getRepositoryId());
		Assert.assertEquals(
			expectedDLContent.getPath(), actualDLContent.getPath());
		Assert.assertEquals(
			expectedDLContent.getVersion(), actualDLContent.getVersion());
		AssertUtils.assertEquals(
			expectedDLContent.getData(), actualDLContent.getData());
		Assert.assertEquals(
			expectedDLContent.getSize(), actualDLContent.getSize());
	}

	private static final int _DATA_SIZE = 1024;

	private static final byte[] _DATA_VERSION_1 = new byte[_DATA_SIZE];

	private static final byte[] _DATA_VERSION_2 = new byte[_DATA_SIZE];

	static {
		for (int i = 0; i < _DATA_SIZE; i++) {
			_DATA_VERSION_1[i] = (byte)i;
			_DATA_VERSION_2[i] = (byte)(i + 1);
		}
	}

	private long _companyId;

	@Inject
	private DLContentLocalService _dlContentLocalService;

	private long _repositoryId;

}