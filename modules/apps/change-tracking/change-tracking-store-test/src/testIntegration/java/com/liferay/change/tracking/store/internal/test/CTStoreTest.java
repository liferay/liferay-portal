/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.store.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTProcessLocalService;
import com.liferay.change.tracking.store.exception.NoSuchContentException;
import com.liferay.change.tracking.store.model.CTSContent;
import com.liferay.change.tracking.store.service.CTSContentLocalService;
import com.liferay.document.library.kernel.exception.NoSuchFileException;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.change.tracking.store.CTStoreFactory;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.InputStream;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class CTStoreTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		Company company = CompanyTestUtil.addCompany();

		_companyId = company.getCompanyId();

		_ctStore = _ctStoreFactory.createCTStore(
			(Store)ProxyUtil.newProxyInstance(
				Store.class.getClassLoader(), new Class<?>[] {Store.class},
				new RecorderInvocationHandler(_fileSystemStore)),
			_STORE_TYPE);
	}

	@Before
	public void setUp() throws PortalException {
		for (int i = 0; i < 4; i++) {
			_ctCollections[i] = _createCTCollection();
		}

		_fileSystemStore.deleteDirectory(
			_companyId, _REPOSITORY_ID, StringPool.BLANK);
	}

	@After
	public void tearDown() {
		_methods.clear();

		_fileSystemStore.deleteDirectory(
			_companyId, _REPOSITORY_ID, StringPool.BLANK);
	}

	@Test
	public void testAddFile() throws Exception {

		// Production mode, add

		String fileName = "testFile";

		_addCTFile(fileName, _DATA_1);

		_assertMethods(_ADD_FILE_METHOD);
		_assertNoSuchCTSContent(fileName);
		_assertFile(fileName, _DATA_1);

		// Production mode, modify

		_addCTFile(fileName, _DATA_2);

		_assertMethods(_ADD_FILE_METHOD);
		_assertNoSuchCTSContent(fileName);
		_assertFile(fileName, _DATA_2);

		_deleteFile(fileName, _VERSION_1);

		// CT mode, add

		_runInCTMode(
			_ctCollections[0],
			() -> {
				_addCTFile(fileName, _DATA_1);

				_assertMethods(_HAS_FILE_METHOD);
				_assertCTSContent(fileName, _DATA_1);
				_assertNoSuchFile(fileName);
			});

		_assertNoSuchCTSContent(fileName);
		_assertNoSuchFile(fileName);

		// CT mode, scratched add

		_runInCTMode(
			_ctCollections[0],
			() -> {
				_addCTFile(fileName, _DATA_2);

				_assertMethods();
				_assertCTSContent(fileName, _DATA_2);
				_assertNoSuchFile(fileName);
			});

		_assertNoSuchCTSContent(fileName);
		_assertNoSuchFile(fileName);

		_publish(_ctCollections[0]);

		_assertCTSContent(fileName, _DATA_2);
		_assertFile(fileName, _DATA_2);

		// CT mode, modify

		_runInCTMode(
			_ctCollections[1],
			() -> {
				_addCTFile(fileName, _DATA_1);

				_assertMethods();
				_assertCTSContent(fileName, _DATA_1);
				_assertFile(fileName, _DATA_2);
			});

		_assertCTSContent(fileName, _DATA_2);
		_assertFile(fileName, _DATA_2);

		// CT mode, scratched modify

		_runInCTMode(
			_ctCollections[1],
			() -> {
				_addCTFile(fileName, _DATA_3);

				_assertMethods();
				_assertCTSContent(fileName, _DATA_3);
				_assertFile(fileName, _DATA_2);
			});

		_assertCTSContent(fileName, _DATA_2);
		_assertFile(fileName, _DATA_2);

		_publish(_ctCollections[1]);

		_assertCTSContent(fileName, _DATA_3);
		_assertFile(fileName, _DATA_3);

		// Purge mode, delete CTCollection1

		_ctCollectionLocalService.deleteCTCollection(_ctCollections[0]);

		_assertCTSContent(fileName, _DATA_3);
		_assertFile(fileName, _DATA_3);

		// Purge mode, delete CTCollection2

		_ctCollectionLocalService.deleteCTCollection(_ctCollections[1]);

		_assertNoSuchCTSContent(fileName);
		_assertFile(fileName, _DATA_3);
	}

	@Test
	public void testDeleteCompany() throws Exception {

		// Production mode, with files

		_addFiles("testDir1/testFile1:v1", "testDir2/testDir3/testFile2:v1,v2");

		_deleteDirectory();

		_assertMethods(_DELETE_DIRECTORY_COMPANY_METHOD);

		_assertFileNames(_ROOT);

		// CT mode, delete company files

		String fileName = "testFile";

		_runInCTMode(
			_ctCollections[0],
			() -> {
				_addCTFile(fileName, _DATA_1);
				_deleteDirectory();

				_assertMethods(
					_HAS_FILE_METHOD, _DELETE_DIRECTORY_COMPANY_METHOD);
				_assertNoSuchCTSContent(fileName);
				_assertNoSuchFile(fileName);
			});

		_assertNoSuchCTSContent(fileName);
		_assertNoSuchFile(fileName);
	}

	@Test
	public void testDeleteDirectory() throws Exception {

		// Production mode, empty dir

		_deleteCTDirectory("/someDir");

		_assertMethods(_DELETE_DIRECTORY_METHOD);

		_assertFileNames("/someDir");

		_deleteCTDirectory(_ROOT);

		_assertMethods(_DELETE_DIRECTORY_METHOD);

		_assertFileNames(_ROOT);

		// Production mode, with files

		_addFiles(
			"testDir1/testDir2/testFile1:v1",
			"testDir1/testDir2/testFile2:v1,v2", "testDir1/testFile3:v1",
			"testDir3/testFile4:v1,v2", "testFile5:v1");

		_deleteCTDirectory("testDir1");

		_assertMethods(_DELETE_DIRECTORY_METHOD);

		_assertFileNames(_ROOT, "testDir3/testFile4", "testFile5");

		_deleteCTDirectory(_ROOT);

		_assertMethods(_DELETE_DIRECTORY_METHOD);

		_assertFileNames(_ROOT);

		// CT mode, empty dir

		_runInCTMode(
			_ctCollections[0],
			() -> {
				_deleteCTDirectory("/someDir");

				_assertMethods(_GET_FILE_NAMES);

				_assertFileNames("/someDir");

				_deleteCTDirectory(_ROOT);

				_assertMethods(_GET_FILE_NAMES);

				_assertFileNames(_ROOT);
			});

		// CT mode, scratched delete

		String[] files = {
			"testDir1/testDir2/testFile1:v1", "testDir1/testFile2:v1",
			"testDir3/testFile3:v1", "testFile4:v1"
		};

		_runInCTMode(
			_ctCollections[0],
			() -> {
				_addCTSContents(files);

				_deleteCTDirectory("testDir1");

				_assertMethods(_GET_FILE_NAMES);
				_assertCTContentNames(_ROOT, "testDir3/testFile3", "testFile4");
				_assertFileNames(_ROOT);

				_deleteCTDirectory(_ROOT);

				_assertMethods(_GET_FILE_NAMES);
				_assertCTContentNames(_ROOT);
				_assertFileNames(_ROOT);
			});

		_publish(_ctCollections[0]);

		_assertCTContentNames(_ROOT);
		_assertFileNames(_ROOT);

		// CT mode, delete unmodified tracked production files

		_addFiles(files);
		_addCTSContents(files);

		_runInCTMode(
			_ctCollections[1],
			() -> {
				_deleteCTDirectory("testDir1");

				_assertMethods(
					_GET_FILE_NAMES, _GET_FILE_VERSIONS, _GET_FILE_VERSIONS);
				_assertCTContentNames(_ROOT, "testDir3/testFile3", "testFile4");
				_assertFileNames(
					_ROOT, "testDir1/testDir2/testFile1", "testDir1/testFile2",
					"testDir3/testFile3", "testFile4");

				_deleteCTDirectory(_ROOT);

				_assertMethods(
					_GET_FILE_NAMES, _GET_FILE_VERSIONS, _GET_FILE_VERSIONS,
					_GET_FILE_VERSIONS, _GET_FILE_VERSIONS);
				_assertCTContentNames(_ROOT);
				_assertFileNames(
					_ROOT, "testDir1/testDir2/testFile1", "testDir1/testFile2",
					"testDir3/testFile3", "testFile4");
			});

		_publish(_ctCollections[1]);

		_assertCTContentNames(_ROOT);
		_assertFileNames(_ROOT);

		// CT mode, delete modified tracked production files

		_addFiles(files);
		_addCTSContents(files);

		_runInCTMode(
			_ctCollections[2],
			() -> {
				_addCTSContents(files);

				_deleteCTDirectory("testDir1");

				_assertMethods(
					_GET_FILE_NAMES, _GET_FILE_VERSIONS, _GET_FILE_VERSIONS);
				_assertCTContentNames(_ROOT, "testDir3/testFile3", "testFile4");
				_assertFileNames(
					_ROOT, "testDir1/testDir2/testFile1", "testDir1/testFile2",
					"testDir3/testFile3", "testFile4");

				_deleteCTDirectory(_ROOT);

				_assertMethods(
					_GET_FILE_NAMES, _GET_FILE_VERSIONS, _GET_FILE_VERSIONS,
					_GET_FILE_VERSIONS, _GET_FILE_VERSIONS);
				_assertCTContentNames(_ROOT);
				_assertFileNames(
					_ROOT, "testDir1/testDir2/testFile1", "testDir1/testFile2",
					"testDir3/testFile3", "testFile4");
			});

		_publish(_ctCollections[2]);

		_assertCTContentNames(_ROOT);
		_assertFileNames(_ROOT);

		// CT mode, delete untracked production files

		_addFiles(files);

		_runInCTMode(
			_ctCollections[3],
			() -> {
				_deleteCTDirectory("testDir1");

				_assertMethods(
					_GET_FILE_NAMES, _GET_FILE_VERSIONS,
					_GET_FILE_AS_STREAM_METHOD, _GET_FILE_VERSIONS,
					_GET_FILE_AS_STREAM_METHOD);
				_assertCTContentNames(_ROOT);
				_assertFileNames(
					_ROOT, "testDir1/testDir2/testFile1", "testDir1/testFile2",
					"testDir3/testFile3", "testFile4");

				_deleteCTDirectory(_ROOT);

				_assertMethods(
					_GET_FILE_NAMES, _GET_FILE_VERSIONS, _GET_FILE_VERSIONS,
					_GET_FILE_VERSIONS, _GET_FILE_AS_STREAM_METHOD,
					_GET_FILE_VERSIONS, _GET_FILE_AS_STREAM_METHOD);
				_assertCTContentNames(_ROOT);
				_assertFileNames(
					_ROOT, "testDir1/testDir2/testFile1", "testDir1/testFile2",
					"testDir3/testFile3", "testFile4");
			});

		_publish(_ctCollections[3]);

		_assertCTContentNames(_ROOT);
		_assertFileNames(_ROOT);
	}

	@Test
	public void testDeleteFile() throws Exception {

		// Production mode, no such file

		String fileName = "testFile";

		_deleteCTFile(fileName);

		_assertMethods(_DELETE_FILE_METHOD);

		// Production mode, delete file

		_addFile(fileName, _DATA_1);

		_deleteCTFile(fileName);

		_assertMethods(_DELETE_FILE_METHOD);

		// CT mode, no such file

		_runInCTMode(
			_ctCollections[0],
			() -> {
				_deleteCTFile(fileName);

				_assertMethods(_HAS_FILE_METHOD);
			});

		// CT mode, scratched delete

		_runInCTMode(
			_ctCollections[0],
			() -> {
				_addCTSContent(fileName, _DATA_1);

				_deleteCTFile(fileName);

				_assertMethods();
			});

		_publish(_ctCollections[0]);

		// CT mode, delete untracked production file

		_addFile(fileName, _DATA_1);

		_runInCTMode(
			_ctCollections[1],
			() -> {
				_deleteCTFile(fileName);

				_assertMethods(_HAS_FILE_METHOD, _GET_FILE_AS_STREAM_METHOD);

				_assertNoSuchCTSContent(fileName);
				_assertFile(fileName, _DATA_1);
			});

		_publish(_ctCollections[1]);

		_assertNoSuchCTSContent(fileName);
		_assertNoSuchFile(fileName);

		// CT mode, delete tracked production file

		_addFile(fileName, _DATA_1);

		_runInCTMode(
			_ctCollections[2],
			() -> {
				_addCTSContent(fileName, _DATA_1);

				_addCTSContent(fileName, _DATA_2, _VERSION_1);
			});

		_publish(_ctCollections[2]);

		_runInCTMode(
			_ctCollections[3],
			() -> {
				_deleteCTFile(fileName);

				_assertMethods();

				_assertNoSuchCTSContent(fileName);
				_assertFile(fileName, _DATA_2);
			});

		_publish(_ctCollections[3]);

		_assertNoSuchCTSContent(fileName);
		_assertNoSuchFile(fileName);
	}

	@Test
	public void testGetFileAsStream() throws Exception {
		_testSingleFileRead(
			this::_assertCTFile, this::_assertFile, _GET_FILE_AS_STREAM_METHOD);
	}

	@Test
	public void testGetFileNames() throws Exception {

		// Production mode, empty dir

		_assertCTFileNames("/someDir");

		_assertMethods(_GET_FILE_NAMES);

		_assertCTFileNames(_ROOT);

		_assertMethods(_GET_FILE_NAMES);

		// Production mode, with files

		_addFiles(
			"testDir1/testDir2/testFile1:v1",
			"testDir1/testDir2/testFile2:v1,v2", "testDir1/testFile3:v1",
			"testDir3/testFile4:v1,v2", "testFile5:v1");

		_assertCTFileNames(
			"testDir1", "testDir1/testDir2/testFile1",
			"testDir1/testDir2/testFile2", "testDir1/testFile3");

		_assertMethods(_GET_FILE_NAMES);

		_assertCTFileNames(
			_ROOT, "testDir1/testDir2/testFile1", "testDir1/testDir2/testFile2",
			"testDir1/testFile3", "testDir3/testFile4", "testFile5");

		_assertMethods(_GET_FILE_NAMES);

		_deleteDirectory(_ROOT);

		// CT mode, get from underneath Store

		_runInCTMode(
			_ctCollections[0],
			() -> {

				// empty dir

				_assertCTFileNames("/someDir");

				_assertMethods(_GET_FILE_NAMES);

				_assertCTFileNames(_ROOT);

				_assertMethods(_GET_FILE_NAMES);

				// with files

				_addFiles(
					"testDir1/testDir2/testFile1:v1",
					"testDir1/testDir2/testFile2:v1,v2",
					"testDir1/testFile3:v1", "testDir3/testFile4:v1,v2",
					"testFile5:v1");

				_assertCTFileNames(
					"testDir1", "testDir1/testDir2/testFile1",
					"testDir1/testDir2/testFile2", "testDir1/testFile3");

				_assertMethods(_GET_FILE_NAMES);

				_assertCTFileNames(
					_ROOT, "testDir1/testDir2/testFile1",
					"testDir1/testDir2/testFile2", "testDir1/testFile3",
					"testDir3/testFile4", "testFile5");

				_assertMethods(_GET_FILE_NAMES);
			});

		// CT mode, get from CT added file

		_runInCTMode(
			_ctCollections[0],
			() -> {
				_addCTSContents("testDir1/testDir2/newFile:v1", "newFile:v1");

				_assertCTFileNames(
					"testDir1/testDir2", "testDir1/testDir2/newFile",
					"testDir1/testDir2/testFile1",
					"testDir1/testDir2/testFile2");

				_assertMethods(_GET_FILE_NAMES);

				_assertCTFileNames("testDir3", "testDir3/testFile4");

				_assertMethods(_GET_FILE_NAMES);

				_assertCTFileNames(
					_ROOT, "newFile", "testDir1/testDir2/newFile",
					"testDir1/testDir2/testFile1",
					"testDir1/testDir2/testFile2", "testDir1/testFile3",
					"testDir3/testFile4", "testFile5");

				_assertMethods(_GET_FILE_NAMES);
			});

		// CT mode, get from CT published file

		_publish(_ctCollections[0]);

		_runInCTMode(
			_ctCollections[1],
			() -> {
				_assertCTFileNames(
					"testDir1/testDir2", "testDir1/testDir2/newFile",
					"testDir1/testDir2/testFile1",
					"testDir1/testDir2/testFile2");

				_assertMethods(_GET_FILE_NAMES);

				_assertCTFileNames("testDir3", "testDir3/testFile4");

				_assertMethods(_GET_FILE_NAMES);

				_assertCTFileNames(
					_ROOT, "newFile", "testDir1/testDir2/newFile",
					"testDir1/testDir2/testFile1",
					"testDir1/testDir2/testFile2", "testDir1/testFile3",
					"testDir3/testFile4", "testFile5");

				_assertMethods(_GET_FILE_NAMES);
			});

		// CT mode, get from CT added, modified and deleted file

		_runInCTMode(
			_ctCollections[1],
			() -> {
				_addCTSContents(
					"testDir1/testDir2/newFile2:v1",
					"testDir1/testDir2/testFile2:v1");

				_deleteCTFile("testDir1/testFile3");

				_assertMethods(_HAS_FILE_METHOD, _GET_FILE_AS_STREAM_METHOD);

				_deleteCTFile("testDir3/testFile4");

				_assertMethods(_HAS_FILE_METHOD, _GET_FILE_AS_STREAM_METHOD);

				_assertCTFileNames(
					"testDir1/testDir2", "testDir1/testDir2/newFile",
					"testDir1/testDir2/newFile2", "testDir1/testDir2/testFile1",
					"testDir1/testDir2/testFile2");

				_assertMethods(
					_GET_FILE_NAMES, _GET_FILE_VERSIONS, _GET_FILE_VERSIONS,
					_GET_FILE_VERSIONS);

				_assertCTFileNames("testDir3", "testDir3/testFile4");

				_assertMethods(_GET_FILE_NAMES, _GET_FILE_VERSIONS);

				_assertCTFileNames(
					_ROOT, "newFile", "testDir1/testDir2/newFile",
					"testDir1/testDir2/newFile2", "testDir1/testDir2/testFile1",
					"testDir1/testDir2/testFile2", "testDir3/testFile4",
					"testFile5");

				_assertMethods(
					_GET_FILE_NAMES, _GET_FILE_VERSIONS, _GET_FILE_VERSIONS,
					_GET_FILE_VERSIONS, _GET_FILE_VERSIONS, _GET_FILE_VERSIONS,
					_GET_FILE_VERSIONS, _GET_FILE_VERSIONS);
			});

		_publish(_ctCollections[1]);

		_assertCTFileNames(
			_ROOT, "newFile", "testDir1/testDir2/newFile",
			"testDir1/testDir2/newFile2", "testDir1/testDir2/testFile1",
			"testDir1/testDir2/testFile2", "testDir3/testFile4", "testFile5");

		_assertMethods(_GET_FILE_NAMES);
	}

	@Test
	public void testGetFileSize() throws Exception {
		_testSingleFileRead(
			this::_assertCTFileSize, this::_assertFileSize, _GET_FILE_SIZE);
	}

	@Test
	public void testGetFileVersions() throws Exception {

		// Production mode

		String fileName = "testFile";

		_assertCTFileVersions(fileName);

		_addFile(fileName, _DATA_1);

		_assertCTFileVersions(fileName, _VERSION_1);

		_addFile(fileName, _DATA_2);

		_assertCTFileVersions(fileName, _VERSION_1, _VERSION_2);

		_addFile(fileName, _DATA_3);

		_assertCTFileVersions(fileName, _VERSION_1, _VERSION_2, _VERSION_3);

		_deleteFile(fileName, _VERSION_2);

		_assertCTFileVersions(fileName, _VERSION_1, _VERSION_3);

		_deleteFile(fileName, _VERSION_1);

		_assertCTFileVersions(fileName, _VERSION_3);

		_deleteFile(fileName, _VERSION_3);

		_assertCTFileVersions(fileName);

		// CT mode, get from underneath Store

		_runInCTMode(
			_ctCollections[0],
			() -> {
				_assertCTFileVersions(fileName);

				_addFile(fileName, _DATA_1);

				_assertCTFileVersions(fileName, _VERSION_1);

				_addFile(fileName, _DATA_2);

				_assertCTFileVersions(fileName, _VERSION_1, _VERSION_2);

				_addFile(fileName, _DATA_3);

				_assertCTFileVersions(
					fileName, _VERSION_1, _VERSION_2, _VERSION_3);

				_deleteFile(fileName, _VERSION_2);

				_assertCTFileVersions(fileName, _VERSION_1, _VERSION_3);

				_deleteFile(fileName, _VERSION_1);

				_assertCTFileVersions(fileName, _VERSION_3);

				_deleteFile(fileName, _VERSION_3);

				_assertCTFileVersions(fileName);
			});

		// CT mode, get from CT added file

		_runInCTMode(
			_ctCollections[0],
			() -> {
				_addCTSContent(fileName, _DATA_1);

				_assertCTFileVersions(fileName, _VERSION_1);

				_addCTSContent(fileName, _DATA_2);

				_assertCTFileVersions(fileName, _VERSION_1, _VERSION_2);
			});

		// CT mode, get from CT published file

		_publish(_ctCollections[0]);

		_runInCTMode(
			_ctCollections[1],
			() -> _assertCTFileVersions(fileName, _VERSION_1, _VERSION_2));

		// CT mode, get from CT added, modified and deleted file

		_runInCTMode(
			_ctCollections[1],
			() -> {
				_addCTSContent(fileName, _DATA_3);

				_assertCTFileVersions(
					fileName, _VERSION_1, _VERSION_2, _VERSION_3);

				_addCTSContent(fileName, _DATA_2, _VERSION_1);

				_deleteCTSContent(fileName, _VERSION_2);

				_assertCTFileVersions(fileName, _VERSION_1, _VERSION_3);
			});

		_publish(_ctCollections[1]);

		_assertCTFileVersions(fileName, _VERSION_1, _VERSION_3);
	}

	@Test
	public void testHasFile() throws Exception {
		_testSingleFileRead(
			this::_assertHasCTFile, this::_assertHasFile, _HAS_FILE_METHOD);
	}

	@Test
	public void testHasFileWithBlankVersion() throws Exception {
		_testSingleFileRead(
			(fileName, data) -> Assert.assertArrayEquals(
				data,
				StreamUtil.toByteArray(
					_ctStore.getFileAsStream(
						_companyId, _REPOSITORY_ID, fileName,
						StringPool.BLANK))),
			this::_assertFile, _GET_FILE_AS_STREAM_METHOD);
	}

	@Test
	public void testVerifyCompanyStores() throws Exception {
		Company company = _companyLocalService.getCompany(_companyId);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_STORE_TYPE, LoggerTestUtil.WARN)) {

			_addFiles("testDir1/testFile1:v1");

			_verifyCompanyStores();

			_assertMethods(_VERIFY_COMPANY_STORES_METHOD);

			List<String> messages = logCapture.getMessages();

			Assert.assertTrue(messages.toString(), messages.isEmpty());

			PortalInstancePool.remove(_companyId);

			_verifyCompanyStores();

			_assertMethods(_VERIFY_COMPANY_STORES_METHOD);

			messages = logCapture.getMessages();

			Assert.assertTrue(
				messages.toString(),
				messages.contains(
					StringBundler.concat(
						"Manually remove unused store ", _companyId,
						" that belongs to company ", _companyId,
						" if it is no longer used anywhere else")));
		}
		finally {
			PortalInstancePool.add(company);
		}
	}

	private void _addCTFile(String fileName, byte[] data)
		throws PortalException {

		_ctStore.addFile(
			_companyId, _REPOSITORY_ID, fileName, _VERSION_1,
			new UnsyncByteArrayInputStream(data));
	}

	private void _addCTSContent(String fileName, byte[] data) {
		_addCTSContent(fileName, data, _toVersion(data));
	}

	private void _addCTSContent(String fileName, byte[] data, String version) {
		_ctsContentLocalService.addCTSContent(
			_companyId, _REPOSITORY_ID, fileName, version, _STORE_TYPE,
			new UnsyncByteArrayInputStream(data));
	}

	private void _addCTSContents(String... files) throws PortalException {
		for (String file : files) {
			List<String> parts = StringUtil.split(file, CharPool.COLON);

			String path = parts.get(0);

			for (String version : StringUtil.split(parts.get(1))) {
				byte[] data = _toData(version);

				_ctsContentLocalService.addCTSContent(
					_companyId, _REPOSITORY_ID, path, _toVersion(data),
					_STORE_TYPE, new UnsyncByteArrayInputStream(data));
			}
		}
	}

	private void _addFile(String fileName, byte[] data) throws PortalException {
		_fileSystemStore.addFile(
			_companyId, _REPOSITORY_ID, fileName, _toVersion(data),
			new UnsyncByteArrayInputStream(data));
	}

	private void _addFiles(String... files) throws PortalException {
		for (String file : files) {
			List<String> parts = StringUtil.split(file, CharPool.COLON);

			String path = parts.get(0);

			for (String version : StringUtil.split(parts.get(1))) {
				byte[] data = _toData(version);

				_fileSystemStore.addFile(
					_companyId, _REPOSITORY_ID, path, _toVersion(data),
					new UnsyncByteArrayInputStream(data));
			}
		}
	}

	private void _assertCTContentNames(String dirName, String... fileNames)
		throws PortalException {

		Set<String> ctsContentNameSet = new HashSet<>();

		for (CTSContent ctsContent :
				_ctsContentLocalService.getCTSContentsByDirectory(
					_companyId, _REPOSITORY_ID, dirName, _STORE_TYPE)) {

			ctsContentNameSet.add(ctsContent.getPath());
		}

		String[] ctsContentNames = ctsContentNameSet.toArray(new String[0]);

		Arrays.sort(ctsContentNames);

		Assert.assertArrayEquals(fileNames, ctsContentNames);
	}

	private void _assertCTFile(String fileName, byte[] data) throws Exception {
		Assert.assertArrayEquals(
			data,
			StreamUtil.toByteArray(
				_ctStore.getFileAsStream(
					_companyId, _REPOSITORY_ID, fileName, _VERSION_1)));
	}

	private void _assertCTFileNames(String dirName, String... fileNames)
		throws PortalException {

		Assert.assertArrayEquals(
			fileNames,
			_ctStore.getFileNames(_companyId, _REPOSITORY_ID, dirName));
	}

	private void _assertCTFileSize(String fileName, byte[] data)
		throws Exception {

		Assert.assertEquals(
			data.length,
			_ctStore.getFileSize(
				_companyId, _REPOSITORY_ID, fileName, _VERSION_1));
	}

	private void _assertCTFileVersions(String fileName, String... versions)
		throws PortalException {

		Assert.assertArrayEquals(
			versions,
			_ctStore.getFileVersions(_companyId, _REPOSITORY_ID, fileName));

		_assertMethods(_GET_FILE_VERSIONS);
	}

	private void _assertCTSContent(String fileName, byte[] data)
		throws Exception {

		CTSContent ctsContent = _ctsContentLocalService.getCTSContent(
			_companyId, _REPOSITORY_ID, fileName, _VERSION_1, _STORE_TYPE);

		Assert.assertArrayEquals(
			data,
			StreamUtil.toByteArray(
				_ctsContentLocalService.openDataInputStream(
					ctsContent.getCtsContentId())));
	}

	private void _assertFile(String fileName, byte[] data) throws Exception {
		Assert.assertArrayEquals(
			data,
			StreamUtil.toByteArray(
				_fileSystemStore.getFileAsStream(
					_companyId, _REPOSITORY_ID, fileName, _VERSION_1)));
	}

	private void _assertFileNames(String dirName, String... fileNames)
		throws PortalException {

		Assert.assertArrayEquals(
			fileNames,
			_fileSystemStore.getFileNames(_companyId, _REPOSITORY_ID, dirName));
	}

	private void _assertFileSize(String fileName, byte[] data)
		throws Exception {

		Assert.assertEquals(
			data.length,
			_fileSystemStore.getFileSize(
				_companyId, _REPOSITORY_ID, fileName, _VERSION_1));
	}

	private void _assertHasCTFile(String fileName, byte[] data)
		throws Exception {

		if (data.length == 0) {
			Assert.assertFalse(
				_ctStore.hasFile(
					_companyId, _REPOSITORY_ID, fileName, _VERSION_1));

			throw new NoSuchFileException(
				StringBundler.concat(
					"Do not have ", fileName, ", ", _VERSION_1));
		}

		Assert.assertTrue(
			_ctStore.hasFile(_companyId, _REPOSITORY_ID, fileName, _VERSION_1));
	}

	private void _assertHasFile(String fileName, byte[] data) throws Exception {
		if (data.length == 0) {
			Assert.assertFalse(
				_fileSystemStore.hasFile(
					_companyId, _REPOSITORY_ID, fileName, _VERSION_1));

			throw new NoSuchFileException(
				StringBundler.concat(
					"Do not have ", fileName, ", ", _VERSION_1));
		}

		Assert.assertTrue(
			_fileSystemStore.hasFile(
				_companyId, _REPOSITORY_ID, fileName, _VERSION_1));
	}

	private void _assertMethods(Method... methods) {
		if (methods.length == 0) {
			Assert.assertTrue(_methods.toString(), _methods.isEmpty());
		}
		else {
			Assert.assertEquals(Arrays.asList(methods), _methods);

			_methods.clear();
		}
	}

	private void _assertNoSuchCTSContent(String fileName) {
		Assert.assertFalse(
			_ctsContentLocalService.hasCTSContent(
				_companyId, _REPOSITORY_ID, fileName, _VERSION_1, _STORE_TYPE));
	}

	private void _assertNoSuchFile(String fileName) {
		Assert.assertFalse(
			_fileSystemStore.hasFile(
				_companyId, _REPOSITORY_ID, fileName, _VERSION_1));
	}

	private CTCollection _createCTCollection() throws PortalException {
		return _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, CTStoreTest.class.getName(), null);
	}

	private void _deleteCTDirectory(String dirName) {
		_ctStore.deleteDirectory(_companyId, _REPOSITORY_ID, dirName);
	}

	private void _deleteCTFile(String fileName) {
		_ctStore.deleteFile(_companyId, _REPOSITORY_ID, fileName, _VERSION_1);
	}

	private void _deleteCTSContent(String fileName, String version) {
		_ctsContentLocalService.deleteCTSContent(
			_companyId, _REPOSITORY_ID, fileName, version, _STORE_TYPE);
	}

	private void _deleteDirectory() throws Exception {
		_ctStore.deleteDirectory(_companyId);
	}

	private void _deleteDirectory(String dirName) {
		_fileSystemStore.deleteDirectory(_companyId, _REPOSITORY_ID, dirName);
	}

	private void _deleteFile(String fileName, String version) {
		_fileSystemStore.deleteFile(
			_companyId, _REPOSITORY_ID, fileName, version);
	}

	private void _publish(CTCollection ctCollection) throws Exception {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			DDMStructureTestUtil.addStructure(
				TestPropsValues.getGroupId(), JournalArticle.class.getName());
		}

		_ctProcessLocalService.addCTProcess(
			ctCollection.getUserId(), ctCollection.getCtCollectionId());
	}

	private void _runInCTMode(
			CTCollection ctCollection, UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			unsafeRunnable.run();
		}
	}

	private void _testSingleFileRead(
			FileAssertor ctFileAssertor, FileAssertor fsFileAssertor,
			Method readMethod)
		throws Exception {

		// Production mode, no such file

		String fileName = "testFile";

		try {
			ctFileAssertor.assertFile(fileName, new byte[0]);

			Assert.fail();
		}
		catch (NoSuchFileException noSuchFileException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchFileException);
			}
		}

		_assertMethods(readMethod);

		// Production mode, get from underneath store

		_addFile(fileName, _DATA_1);

		ctFileAssertor.assertFile(fileName, _DATA_1);

		_assertMethods(readMethod);

		_assertNoSuchCTSContent(fileName);

		fsFileAssertor.assertFile(fileName, _DATA_1);

		_deleteFile(fileName, _VERSION_1);

		// CT mode, no such file

		_runInCTMode(
			_ctCollections[0],
			() -> {
				try {
					ctFileAssertor.assertFile(fileName, new byte[0]);

					Assert.fail();
				}
				catch (NoSuchFileException noSuchFileException) {
					if (_log.isDebugEnabled()) {
						_log.debug(noSuchFileException);
					}
				}

				_assertMethods(readMethod);
			});

		// CT mode, get from underneath Store

		_addFile(fileName, _DATA_1);

		_runInCTMode(
			_ctCollections[0],
			() -> {
				ctFileAssertor.assertFile(fileName, _DATA_1);

				_assertMethods(readMethod);

				_assertNoSuchCTSContent(fileName);

				fsFileAssertor.assertFile(fileName, _DATA_1);
			});

		_deleteFile(fileName, _VERSION_1);

		// CT mode, get from CT added file

		_runInCTMode(
			_ctCollections[0],
			() -> {
				_addCTSContent(fileName, _DATA_1);

				ctFileAssertor.assertFile(fileName, _DATA_1);

				_assertMethods();
				_assertNoSuchFile(fileName);
			});

		// CT mode, get from CT published file

		_publish(_ctCollections[0]);

		_runInCTMode(
			_ctCollections[1],
			() -> {
				ctFileAssertor.assertFile(fileName, _DATA_1);

				_assertMethods();

				fsFileAssertor.assertFile(fileName, _DATA_1);
			});

		// CT mode, get from CT modified file

		_runInCTMode(
			_ctCollections[1],
			() -> {
				_addCTSContent(fileName, _DATA_2, _VERSION_1);

				ctFileAssertor.assertFile(fileName, _DATA_2);

				_assertMethods();

				fsFileAssertor.assertFile(fileName, _DATA_1);
			});

		// CT mode, get from CT deleted file

		_publish(_ctCollections[1]);

		_runInCTMode(
			_ctCollections[2],
			() -> {
				_deleteCTSContent(fileName, _VERSION_1);

				try {
					ctFileAssertor.assertFile(fileName, new byte[0]);

					Assert.fail();
				}
				catch (NoSuchContentException | NoSuchFileException exception) {
				}

				_assertMethods();

				fsFileAssertor.assertFile(fileName, _DATA_2);
			});

		// Production mode, no such file

		_publish(_ctCollections[2]);

		try {
			ctFileAssertor.assertFile(fileName, new byte[0]);

			Assert.fail();
		}
		catch (NoSuchFileException noSuchFileException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchFileException);
			}
		}

		_assertMethods(readMethod);

		try {
			fsFileAssertor.assertFile(fileName, new byte[0]);

			Assert.fail();
		}
		catch (NoSuchFileException noSuchFileException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchFileException);
			}
		}
	}

	private byte[] _toData(String s) {
		if (s.equals("v1")) {
			return _DATA_1;
		}

		if (s.equals("v2")) {
			return _DATA_2;
		}

		if (s.equals("v3")) {
			return _DATA_3;
		}

		throw new IllegalArgumentException("Unknown version : " + s);
	}

	private String _toVersion(byte[] data) {
		String version = _VERSION_1;

		if (data == _DATA_3) {
			version = _VERSION_3;
		}
		else if (data == _DATA_2) {
			version = _VERSION_2;
		}

		return version;
	}

	private void _verifyCompanyStores() throws Exception {
		_ctStore.verifyCompanyStores();
	}

	private static final Method _ADD_FILE_METHOD;

	private static final byte[] _DATA_1 = "Data1 a".getBytes();

	private static final byte[] _DATA_2 = "Data2 ab".getBytes();

	private static final byte[] _DATA_3 = "Data3 abc".getBytes();

	private static final Method _DELETE_DIRECTORY_COMPANY_METHOD;

	private static final Method _DELETE_DIRECTORY_METHOD;

	private static final Method _DELETE_FILE_METHOD;

	private static final Method _GET_FILE_AS_STREAM_METHOD;

	private static final Method _GET_FILE_NAMES;

	private static final Method _GET_FILE_SIZE;

	private static final Method _GET_FILE_VERSIONS;

	private static final Method _HAS_FILE_METHOD;

	private static final long _REPOSITORY_ID = 0;

	private static final String _ROOT = StringPool.BLANK;

	private static final String _STORE_TYPE =
		"com.liferay.portal.store.file.system.FileSystemStore";

	private static final Method _VERIFY_COMPANY_STORES_METHOD;

	private static final String _VERSION_1 = Store.VERSION_DEFAULT;

	private static final String _VERSION_2 = "2.0";

	private static final String _VERSION_3 = "3.0";

	private static final Log _log = LogFactoryUtil.getLog(CTStoreTest.class);

	private static long _companyId;
	private static Store _ctStore;

	@Inject
	private static CTStoreFactory _ctStoreFactory;

	@Inject(filter = "store.type=" + _STORE_TYPE)
	private static Store _fileSystemStore;

	private static final List<Method> _methods = new ArrayList<>();

	static {
		try {
			_ADD_FILE_METHOD = Store.class.getMethod(
				"addFile", long.class, long.class, String.class, String.class,
				InputStream.class);

			_DELETE_DIRECTORY_COMPANY_METHOD = Store.class.getMethod(
				"deleteDirectory", long.class);

			_DELETE_DIRECTORY_METHOD = Store.class.getMethod(
				"deleteDirectory", long.class, long.class, String.class);

			_DELETE_FILE_METHOD = Store.class.getMethod(
				"deleteFile", long.class, long.class, String.class,
				String.class);

			_GET_FILE_AS_STREAM_METHOD = Store.class.getMethod(
				"getFileAsStream", long.class, long.class, String.class,
				String.class);

			_GET_FILE_NAMES = Store.class.getMethod(
				"getFileNames", long.class, long.class, String.class);

			_GET_FILE_SIZE = Store.class.getMethod(
				"getFileSize", long.class, long.class, String.class,
				String.class);

			_GET_FILE_VERSIONS = Store.class.getMethod(
				"getFileVersions", long.class, long.class, String.class);

			_HAS_FILE_METHOD = Store.class.getMethod(
				"hasFile", long.class, long.class, String.class, String.class);

			_VERIFY_COMPANY_STORES_METHOD = Store.class.getMethod(
				"verifyCompanyStores");
		}
		catch (NoSuchMethodException noSuchMethodException) {
			throw new ExceptionInInitializerError(noSuchMethodException);
		}
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@DeleteAfterTestRun
	private final CTCollection[] _ctCollections = new CTCollection[4];

	@Inject
	private CTProcessLocalService _ctProcessLocalService;

	@Inject
	private CTSContentLocalService _ctsContentLocalService;

	private static class RecorderInvocationHandler
		implements InvocationHandler {

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
			throws ReflectiveOperationException {

			_methods.add(method);

			try {
				return method.invoke(_target, args);
			}
			catch (InvocationTargetException invocationTargetException) {
				return ReflectionUtil.throwException(
					invocationTargetException.getCause());
			}
		}

		private RecorderInvocationHandler(Object target) {
			_target = target;
		}

		private final Object _target;

	}

	private interface FileAssertor {

		public void assertFile(String fileName, byte[] fileContent)
			throws Exception;

	}

}