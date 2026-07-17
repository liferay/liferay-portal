/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.test.util.LazyReferencingTestUtil;
import com.liferay.fragment.constants.FragmentActionKeys;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.headless.admin.fragment.client.dto.v1_0.FileURLReference;
import com.liferay.headless.admin.fragment.client.dto.v1_0.FragmentSet;
import com.liferay.headless.admin.fragment.client.dto.v1_0.ResourceFile;
import com.liferay.headless.admin.fragment.client.dto.v1_0.ResourceFolder;
import com.liferay.headless.admin.fragment.client.pagination.Page;
import com.liferay.headless.admin.fragment.client.pagination.Pagination;
import com.liferay.headless.admin.fragment.client.problem.Problem;
import com.liferay.headless.admin.fragment.client.resource.v1_0.ResourceFileResource;
import com.liferay.headless.admin.fragment.client.resource.v1_0.ResourceFolderResource;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rubén Pulido
 */
@FeatureFlag("LPD-39244")
@RunWith(Arquillian.class)
public class ResourceFileResourceTest extends BaseResourceFileResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseResourceFileResourceTestCase.setUpClass();

		_httpServer = HttpServer.create(
			new InetSocketAddress("127.0.0.1", 0), 0);

		_content1Bytes = RandomTestUtil.randomBytes();
		_content2Bytes = RandomTestUtil.randomBytes();

		_httpServer.createContext(
			"/content_1.txt",
			httpExchange -> _writeBytes(_content1Bytes, httpExchange));
		_httpServer.createContext(
			"/content_2.txt",
			httpExchange -> _writeBytes(_content2Bytes, httpExchange));

		_httpServer.start();

		_content1Base64 = Base64.encode(_content1Bytes);

		InetSocketAddress inetSocketAddress = _httpServer.getAddress();

		String baseURL = "http://127.0.0.1:" + inetSocketAddress.getPort();

		_content1URL = baseURL + "/content_1.txt";
		_content2URL = baseURL + "/content_2.txt";
	}

	@AfterClass
	public static void tearDownClass() {
		if (_httpServer != null) {
			_httpServer.stop(0);
		}
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_guestResourceFileResource = _getGuestResourceFileResource();
		_nestedFieldsResourceFileResource =
			_getNestedFieldsResourceFileResource();
		_resourceFolderResource = _getResourceFolderResource();
		_userWithoutPermissionsResourceFileResource =
			_getUserWithoutPermissionsResourceFileResource();
		_userWithPermissionsResourceFileResource =
			_getUserWithPermissionsResourceFileResource();
	}

	@Override
	@Test
	@TestInfo("LPD-88395")
	public void testDeleteSiteResourceFile() throws Exception {
		super.testDeleteSiteResourceFile();

		_testDeleteSiteResourceFilePortletFileProblemException();
		_testDeleteSiteResourceFileWithoutPermissionsProblemException();
		_testDeleteSiteResourceFileWithPermissions();
	}

	@Override
	@Test
	@TestInfo("LPD-88395")
	public void testGetSiteFragmentSetResourceFilesPage() throws Exception {
		super.testGetSiteFragmentSetResourceFilesPage();

		_testGetSiteFragmentSetResourceFilesPage();
		_testGetSiteFragmentSetResourceFilesPageEmpty();
		_testGetSiteFragmentSetResourceFilesPageFragmentSetNonexistentProblemException();
		_testGetSiteFragmentSetResourceFilesPageWithoutPermissions();
		_testGetSiteFragmentSetResourceFilesPageWithPermissions();
	}

	@Override
	@Test
	@TestInfo("LPD-88395")
	public void testGetSiteResourceFile() throws Exception {
		super.testGetSiteResourceFile();

		_testGetSiteResourceFileFileURLReferenceFileBase64();
		_testGetSiteResourceFileFileURLReferenceURL();
		_testGetSiteResourceFileFragmentSet();
		_testGetSiteResourceFileGuest();
		_testGetSiteResourceFileNonexistentProblemException();
		_testGetSiteResourceFilePortletFileProblemException();
		_testGetSiteResourceFileResourceFolder();
		_testGetSiteResourceFileWithoutPermissionsProblemException();
		_testGetSiteResourceFileWithPermissions();
	}

	@Override
	@Test
	@TestInfo("LPD-88395")
	public void testGetSiteResourceFilesPage() throws Exception {
		super.testGetSiteResourceFilesPage();

		_testGetSiteResourceFilesPage();
		_testGetSiteResourceFilesPagePortletFile();
		_testGetSiteResourceFilesPageWithoutPermissions();
		_testGetSiteResourceFilesPageWithPermissions();
	}

	@Override
	@Test
	@TestInfo("LPD-88395")
	public void testGetSiteResourceFolderResourceFilesPage() throws Exception {
		super.testGetSiteResourceFolderResourceFilesPage();

		_testGetSiteResourceFolderResourceFilesPage();
		_testGetSiteResourceFolderResourceFilesPageEmpty();
		_testGetSiteResourceFolderResourceFilesPagePortletFolderProblemException();
		_testGetSiteResourceFolderResourceFilesPageResourceFolderNonexistentProblemException();
		_testGetSiteResourceFolderResourceFilesPageWithoutPermissionsProblemException();
		_testGetSiteResourceFolderResourceFilesPageWithPermissions();
	}

	@Override
	@Test
	@TestInfo("LPD-88395")
	public void testPostSiteFragmentSetResourceFile() throws Exception {
		super.testPostSiteFragmentSetResourceFile();

		_testPostSiteFragmentSetResourceFileWithoutPermissionsProblemException();
		_testPostSiteFragmentSetResourceFileWithPermissions();
	}

	@Override
	@Test
	@TestInfo("LPD-88395")
	public void testPostSiteResourceFile() throws Exception {
		super.testPostSiteResourceFile();

		_testPostSiteResourceFile();
		_testPostSiteResourceFileBatch();
		_testPostSiteResourceFileDuplicateExternalReferenceCodeProblemException();
		_testPostSiteResourceFileFileURLReferenceFileBase64();
		_testPostSiteResourceFileFileURLReferenceFileBase64AndURL();
		_testPostSiteResourceFileFileURLReferenceFileBase64AndURLNullProblemException();
		_testPostSiteResourceFileFileURLReferenceNullProblemException();
		_testPostSiteResourceFileFileURLReferenceURL();
		_testPostSiteResourceFileFileURLReferenceURLLARProblemException();
		_testPostSiteResourceFileFileURLReferenceURLUnreachableProblemException();
		_testPostSiteResourceFileFileURLReferenceURLUnsupportedProtocolProblemException();
		_testPostSiteResourceFileFragmentSetAndFragmentSetExternalReferenceCode();
		_testPostSiteResourceFileFragmentSetAndFragmentSetExternalReferenceCodeProblemException();
		_testPostSiteResourceFileFragmentSetExternalReferenceCode();
		_testPostSiteResourceFileFragmentSetExternalReferenceCodeNullProblemException();
		_testPostSiteResourceFileFragmentSetNonexistentProblemException();
		_testPostSiteResourceFileNameNullProblemException();
		_testPostSiteResourceFileResourceFolderAndResourceFolderExternalReferenceCode();
		_testPostSiteResourceFileResourceFolderExternalReferenceCode();
		_testPostSiteResourceFileResourceFolderNonexistentProblemException();
		_testPostSiteResourceFileResourceFolderPortletFolderProblemException();
		_testPostSiteResourceFileWithoutPermissionsProblemException();
		_testPostSiteResourceFileWithPermissions();
	}

	@Override
	@Test
	@TestInfo("LPD-88395")
	public void testPutSiteResourceFile() throws Exception {
		_testPutSiteResourceFile();
		_testPutSiteResourceFileBatch();
		_testPutSiteResourceFileFileURLReferenceFileBase64();
		_testPutSiteResourceFileFileURLReferenceFileBase64AndURLNullProblemException();
		_testPutSiteResourceFileFileURLReferenceNullProblemException();
		_testPutSiteResourceFileFileURLReferenceURL();
		_testPutSiteResourceFileName();
		_testPutSiteResourceFileNameNullProblemException();
		_testPutSiteResourceFilePortletFileProblemException();
		_testPutSiteResourceFileResourceFolderExternalReferenceCode();
		_testPutSiteResourceFileResourceFolderPortletFolderProblemException();
		_testPutSiteResourceFileWithoutPermissionsProblemException();
		_testPutSiteResourceFileWithPermissions();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"externalReferenceCode", "fragmentSetExternalReferenceCode", "name"
		};
	}

	@Override
	protected ResourceFile randomResourceFile() throws Exception {
		return _randomResourceFile(_getFragmentSetExternalReferenceCode());
	}

	@Override
	protected ResourceFile
			testGetSiteFragmentSetResourceFilesPage_addResourceFile(
				String siteExternalReferenceCode,
				String fragmentSetExternalReferenceCode,
				ResourceFile resourceFile)
		throws Exception {

		return resourceFileResource.postSiteFragmentSetResourceFile(
			siteExternalReferenceCode, fragmentSetExternalReferenceCode,
			resourceFile);
	}

	@Override
	protected Map<String, Map<String, String>>
			testGetSiteFragmentSetResourceFilesPage_getExpectedActions(
				String siteExternalReferenceCode,
				String fragmentSetExternalReferenceCode)
		throws Exception {

		return new HashMap<>();
	}

	@Override
	protected String
			testGetSiteFragmentSetResourceFilesPage_getFragmentSetExternalReferenceCode()
		throws Exception {

		return _getFragmentSetExternalReferenceCode();
	}

	@Override
	protected ResourceFile testGetSiteResourceFilesPage_addResourceFile(
			String siteExternalReferenceCode, ResourceFile resourceFile)
		throws Exception {

		Group group = _groupLocalService.getGroupByExternalReferenceCode(
			siteExternalReferenceCode, testCompany.getCompanyId());

		FragmentCollection fragmentCollection = _addFragmentCollection(
			group.getGroupId());

		resourceFile.setFragmentSetExternalReferenceCode(
			fragmentCollection.getExternalReferenceCode());

		return resourceFileResource.postSiteResourceFile(
			siteExternalReferenceCode, resourceFile);
	}

	@Override
	protected Map<String, Map<String, String>>
			testGetSiteResourceFilesPage_getExpectedActions(
				String siteExternalReferenceCode)
		throws Exception {

		return new HashMap<>();
	}

	@Override
	protected ResourceFile
			testGetSiteResourceFolderResourceFilesPage_addResourceFile(
				String siteExternalReferenceCode,
				String resourceFolderExternalReferenceCode,
				ResourceFile resourceFile)
		throws Exception {

		resourceFile.setResourceFolderExternalReferenceCode(
			resourceFolderExternalReferenceCode);

		return resourceFileResource.postSiteResourceFile(
			siteExternalReferenceCode, resourceFile);
	}

	@Override
	protected String
			testGetSiteResourceFolderResourceFilesPage_getResourceFolderExternalReferenceCode()
		throws Exception {

		ResourceFolder resourceFolder = _postSiteResourceFolder(
			_getFragmentSetExternalReferenceCode());

		return resourceFolder.getExternalReferenceCode();
	}

	@Override
	protected ResourceFile testPostSiteFragmentSetResourceFile_addResourceFile(
			ResourceFile resourceFile)
		throws Exception {

		return resourceFileResource.postSiteFragmentSetResourceFile(
			testGroup.getExternalReferenceCode(),
			resourceFile.getFragmentSetExternalReferenceCode(), resourceFile);
	}

	@Override
	protected ResourceFile testPostSiteResourceFile_addResourceFile(
			ResourceFile resourceFile)
		throws Exception {

		return resourceFileResource.postSiteResourceFile(
			testGroup.getExternalReferenceCode(), resourceFile);
	}

	private static void _writeBytes(byte[] bytes, HttpExchange httpExchange)
		throws IOException {

		Headers responseHeaders = httpExchange.getResponseHeaders();

		responseHeaders.set("Content-Type", ContentTypes.TEXT_PLAIN);

		httpExchange.sendResponseHeaders(200, bytes.length);

		try (OutputStream outputStream = httpExchange.getResponseBody()) {
			outputStream.write(bytes);
		}
	}

	private FragmentCollection _addFragmentCollection(long groupId)
		throws Exception {

		return _fragmentCollectionLocalService.addFragmentCollection(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), groupId,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			false, ServiceContextTestUtil.getServiceContext(groupId));
	}

	private FileEntry _addPortletFileEntry() throws Exception {
		Repository repository = PortletFileRepositoryUtil.addPortletRepository(
			testGroup.getGroupId(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(testGroup.getGroupId()));

		return PortletFileRepositoryUtil.addPortletFileEntry(
			testGroup.getGroupId(), TestPropsValues.getUserId(), null, 0,
			repository.getPortletId(), repository.getDlFolderId(),
			RandomTestUtil.randomBytes(), RandomTestUtil.randomString(),
			ContentTypes.APPLICATION_OCTET_STREAM, false);
	}

	private Folder _addPortletFolder() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(testGroup.getGroupId());

		Repository repository = PortletFileRepositoryUtil.addPortletRepository(
			testGroup.getGroupId(), RandomTestUtil.randomString(),
			serviceContext);

		return PortletFileRepositoryUtil.addPortletFolder(
			TestPropsValues.getUserId(), repository.getRepositoryId(),
			repository.getDlFolderId(), RandomTestUtil.randomString(),
			serviceContext);
	}

	private void _assertContent(
			byte[] expectedBytes, String externalReferenceCode, long groupId)
		throws Exception {

		FileEntry fileEntry =
			PortletFileRepositoryUtil.
				fetchPortletFileEntryByExternalReferenceCode(
					externalReferenceCode, groupId);

		Assert.assertArrayEquals(
			expectedBytes, FileUtil.getBytes(fileEntry.getContentStream()));
	}

	private void _assertNotContains(
		FileEntry fileEntry, List<ResourceFile> resourceFiles) {

		String externalReferenceCode = fileEntry.getExternalReferenceCode();

		for (ResourceFile resourceFile : resourceFiles) {
			Assert.assertNotEquals(
				resourceFiles + " contains " + fileEntry, externalReferenceCode,
				resourceFile.getExternalReferenceCode());
		}
	}

	private void _assertProblemException(
			String status, String titleKey,
			UnsafeRunnable<Exception> unsafeRunnable, Object... titleArguments)
		throws Exception {

		try {
			unsafeRunnable.run();

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals(status, problem.getStatus());
			Assert.assertEquals(
				_language.format(
					LocaleUtil.getDefault(), titleKey, titleArguments),
				problem.getTitle());
		}
	}

	private void _assertProblemException(
			String titleKey, UnsafeRunnable<Exception> unsafeRunnable,
			Object... titleArguments)
		throws Exception {

		_assertProblemException(
			"BAD_REQUEST", titleKey, unsafeRunnable, titleArguments);
	}

	private void _assertURLContent(byte[] expectedBytes, String urlString)
		throws Exception {

		Assert.assertTrue(urlString, urlString.startsWith("http"));

		URL url = new URL(urlString);

		HttpURLConnection httpURLConnection =
			(HttpURLConnection)url.openConnection();

		Assert.assertEquals(
			HttpURLConnection.HTTP_OK, httpURLConnection.getResponseCode());

		try (InputStream inputStream = httpURLConnection.getInputStream()) {
			Assert.assertArrayEquals(
				expectedBytes, FileUtil.getBytes(inputStream));
		}
	}

	private String _exportResourceFilesToJSON(String siteExternalReferenceCode)
		throws Exception {

		JSONObject exportTaskJSONObject = _waitForFinish(
			HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					"headless-admin-fragment/v1.0/sites/",
					siteExternalReferenceCode,
					"/resource-files/export-batch?contentType=JSON",
					"&batchNestedFields=fileURLReference.fileBase64,",
					"fragmentSet,resourceFolder"),
				Http.Method.POST));

		try (InputStream inputStream = HTTPTestUtil.invokeToInputStream(
				null,
				StringBundler.concat(
					"headless-batch-engine/v1.0/export-task",
					"/by-external-reference-code/",
					exportTaskJSONObject.getString("externalReferenceCode"),
					"/content"),
				HashMapBuilder.put(
					HttpHeaders.ACCEPT, ContentTypes.APPLICATION_OCTET_STREAM
				).build(),
				Http.Method.GET)) {

			ZipInputStream zipInputStream = new ZipInputStream(inputStream);

			zipInputStream.getNextEntry();

			return StringUtil.read(zipInputStream);
		}
	}

	private String _getFragmentSetExternalReferenceCode() throws Exception {
		if (_fragmentSetExternalReferenceCode == null) {
			FragmentCollection fragmentCollection = _addFragmentCollection(
				testGroup.getGroupId());

			_fragmentSetExternalReferenceCode =
				fragmentCollection.getExternalReferenceCode();
		}

		return _fragmentSetExternalReferenceCode;
	}

	private ResourceFileResource _getGuestResourceFileResource() {
		return ResourceFileResource.builder(
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	private ResourceFileResource _getNestedFieldsResourceFileResource()
		throws Exception {

		User user = UserTestUtil.getAdminUser(testCompany.getCompanyId());

		return ResourceFileResource.builder(
		).authentication(
			user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).parameters(
			"nestedFields",
			"fileURLReference.fileBase64,fragmentSet,resourceFolder"
		).build();
	}

	private ResourceFolderResource _getResourceFolderResource()
		throws Exception {

		User user = UserTestUtil.getAdminUser(testCompany.getCompanyId());

		return ResourceFolderResource.builder(
		).authentication(
			user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	private ResourceFile _getSiteResourceFile(String externalReferenceCode)
		throws Exception {

		return _getSiteResourceFile(
			externalReferenceCode, testGroup.getExternalReferenceCode());
	}

	private ResourceFile _getSiteResourceFile(
			String externalReferenceCode, String siteExternalReferenceCode)
		throws Exception {

		return _nestedFieldsResourceFileResource.getSiteResourceFile(
			siteExternalReferenceCode, externalReferenceCode);
	}

	private ResourceFileResource
			_getUserWithoutPermissionsResourceFileResource()
		throws Exception {

		String password = RandomTestUtil.randomString();

		User user = UserTestUtil.addUser(testCompany, password);

		_userLocalService.addGroupUser(
			testGroup.getGroupId(), user.getUserId());

		return ResourceFileResource.builder(
		).authentication(
			user.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	private ResourceFileResource _getUserWithPermissionsResourceFileResource()
		throws Exception {

		Role role = RoleTestUtil.addRole(
			RandomTestUtil.randomString(), RoleConstants.TYPE_REGULAR,
			FragmentConstants.RESOURCE_NAME, ResourceConstants.SCOPE_GROUP,
			String.valueOf(testGroup.getGroupId()),
			FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		String password = RandomTestUtil.randomString();

		User user = UserTestUtil.addUser(testCompany, password);

		_userLocalService.addRoleUser(role.getRoleId(), user.getUserId());

		return ResourceFileResource.builder(
		).authentication(
			user.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	private ResourceFile _postSiteResourceFile(ResourceFolder resourceFolder)
		throws Exception {

		return resourceFileResource.postSiteResourceFile(
			testGroup.getExternalReferenceCode(),
			_randomResourceFile(resourceFolder));
	}

	private void _postSiteResourceFileAndAssertContent(
			byte[] expectedBytes, FileURLReference fileURLReference)
		throws Exception {

		ResourceFile resourceFile = _randomResourceFile(
			_getFragmentSetExternalReferenceCode());

		resourceFile.setFileURLReference(fileURLReference);

		ResourceFile postResourceFile =
			resourceFileResource.postSiteResourceFile(
				testGroup.getExternalReferenceCode(), resourceFile);

		_assertContent(
			expectedBytes, postResourceFile.getExternalReferenceCode(),
			testGroup.getGroupId());
	}

	private ResourceFolder _postSiteResourceFolder(
			ResourceFolder parentResourceFolder)
		throws Exception {

		return _resourceFolderResource.postSiteResourceFolder(
			testGroup.getExternalReferenceCode(),
			_randomResourceFolder(parentResourceFolder));
	}

	private ResourceFolder _postSiteResourceFolder(
			String fragmentSetExternalReferenceCode)
		throws Exception {

		return _resourceFolderResource.postSiteResourceFolder(
			testGroup.getExternalReferenceCode(),
			_randomResourceFolder(fragmentSetExternalReferenceCode));
	}

	private ResourceFile _putSiteResourceFile(
			ResourceFile resourceFile, String resourceFileExternalReferenceCode)
		throws Exception {

		return _nestedFieldsResourceFileResource.putSiteResourceFile(
			testGroup.getExternalReferenceCode(),
			resourceFileExternalReferenceCode, resourceFile);
	}

	private ResourceFile _randomResourceFile(ResourceFolder resourceFolder)
		throws Exception {

		ResourceFile resourceFile = _randomResourceFile(
			resourceFolder.getFragmentSetExternalReferenceCode());

		resourceFile.setResourceFolderExternalReferenceCode(
			resourceFolder.getExternalReferenceCode());

		return resourceFile;
	}

	private ResourceFile _randomResourceFile(
			String fragmentSetExternalReferenceCode)
		throws Exception {

		ResourceFile resourceFile = super.randomResourceFile();

		resourceFile.setDateCreated(new Date());
		resourceFile.setDateModified(new Date());
		resourceFile.setFileURLReference(
			_toFileURLReference(RandomTestUtil.randomBytes()));
		resourceFile.setFragmentSetExternalReferenceCode(
			fragmentSetExternalReferenceCode);
		resourceFile.setResourceFolderExternalReferenceCode((String)null);

		return resourceFile;
	}

	private ResourceFolder _randomResourceFolder(
		ResourceFolder parentResourceFolder) {

		ResourceFolder resourceFolder = _randomResourceFolder(
			parentResourceFolder.getFragmentSetExternalReferenceCode());

		resourceFolder.setParentResourceFolderExternalReferenceCode(
			parentResourceFolder.getExternalReferenceCode());

		return resourceFolder;
	}

	private ResourceFolder _randomResourceFolder(
		String fragmentSetExternalReferenceCode) {

		ResourceFolder resourceFolder = new ResourceFolder();

		resourceFolder.setExternalReferenceCode(RandomTestUtil.randomString());
		resourceFolder.setFragmentSetExternalReferenceCode(
			fragmentSetExternalReferenceCode);
		resourceFolder.setName(RandomTestUtil.randomString());

		return resourceFolder;
	}

	private void _testDeleteSiteResourceFilePortletFileProblemException()
		throws Exception {

		FileEntry fileEntry = _addPortletFileEntry();

		try {
			resourceFileResource.deleteSiteResourceFile(
				testGroup.getExternalReferenceCode(),
				fileEntry.getExternalReferenceCode());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}
	}

	private void _testDeleteSiteResourceFileWithoutPermissionsProblemException()
		throws Exception {

		ResourceFile resourceFile = resourceFileResource.postSiteResourceFile(
			testGroup.getExternalReferenceCode(), randomResourceFile());

		try {
			_userWithoutPermissionsResourceFileResource.deleteSiteResourceFile(
				testGroup.getExternalReferenceCode(),
				resourceFile.getExternalReferenceCode());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("FORBIDDEN", problem.getStatus());
		}
	}

	private void _testDeleteSiteResourceFileWithPermissions() throws Exception {
		ResourceFile resourceFile = resourceFileResource.postSiteResourceFile(
			testGroup.getExternalReferenceCode(), randomResourceFile());

		_userWithPermissionsResourceFileResource.deleteSiteResourceFile(
			testGroup.getExternalReferenceCode(),
			resourceFile.getExternalReferenceCode());

		try {
			resourceFileResource.getSiteResourceFile(
				testGroup.getExternalReferenceCode(),
				resourceFile.getExternalReferenceCode());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}
	}

	private void _testGetSiteFragmentSetResourceFilesPage() throws Exception {
		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFile resourceFile =
			resourceFileResource.postSiteFragmentSetResourceFile(
				testGroup.getExternalReferenceCode(),
				fragmentCollection.getExternalReferenceCode(),
				_randomResourceFile(
					fragmentCollection.getExternalReferenceCode()));

		_postSiteResourceFile(
			_postSiteResourceFolder(
				fragmentCollection.getExternalReferenceCode()));

		Page<ResourceFile> page =
			resourceFileResource.getSiteFragmentSetResourceFilesPage(
				testGroup.getExternalReferenceCode(),
				fragmentCollection.getExternalReferenceCode(),
				Pagination.of(1, 10));

		assertContains(resourceFile, (List<ResourceFile>)page.getItems());
		Assert.assertEquals(1, page.getTotalCount());
	}

	private void _testGetSiteFragmentSetResourceFilesPageEmpty()
		throws Exception {

		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		Page<ResourceFile> page =
			resourceFileResource.getSiteFragmentSetResourceFilesPage(
				testGroup.getExternalReferenceCode(),
				fragmentCollection.getExternalReferenceCode(),
				Pagination.of(1, 10));

		Assert.assertEquals(0, page.getTotalCount());
	}

	private void _testGetSiteFragmentSetResourceFilesPageFragmentSetNonexistentProblemException()
		throws Exception {

		try {
			resourceFileResource.getSiteFragmentSetResourceFilesPage(
				testGroup.getExternalReferenceCode(),
				RandomTestUtil.randomString(), Pagination.of(1, 10));

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}
	}

	private void _testGetSiteFragmentSetResourceFilesPageWithoutPermissions()
		throws Exception {

		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		resourceFileResource.postSiteFragmentSetResourceFile(
			testGroup.getExternalReferenceCode(),
			fragmentCollection.getExternalReferenceCode(),
			_randomResourceFile(fragmentCollection.getExternalReferenceCode()));

		Page<ResourceFile> page =
			_userWithoutPermissionsResourceFileResource.
				getSiteFragmentSetResourceFilesPage(
					testGroup.getExternalReferenceCode(),
					fragmentCollection.getExternalReferenceCode(),
					Pagination.of(1, 10));

		Assert.assertEquals(0, page.getTotalCount());
	}

	private void _testGetSiteFragmentSetResourceFilesPageWithPermissions()
		throws Exception {

		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFile resourceFile =
			resourceFileResource.postSiteFragmentSetResourceFile(
				testGroup.getExternalReferenceCode(),
				fragmentCollection.getExternalReferenceCode(),
				_randomResourceFile(
					fragmentCollection.getExternalReferenceCode()));

		Page<ResourceFile> page =
			_userWithPermissionsResourceFileResource.
				getSiteFragmentSetResourceFilesPage(
					testGroup.getExternalReferenceCode(),
					fragmentCollection.getExternalReferenceCode(),
					Pagination.of(1, 10));

		assertContains(resourceFile, (List<ResourceFile>)page.getItems());
		Assert.assertEquals(1, page.getTotalCount());
	}

	private void _testGetSiteResourceFileFileURLReferenceFileBase64()
		throws Exception {

		ResourceFile resourceFile = _randomResourceFile(
			_getFragmentSetExternalReferenceCode());

		byte[] bytes = RandomTestUtil.randomBytes();

		resourceFile.setFileURLReference(_toFileURLReference(bytes));

		ResourceFile postResourceFile =
			resourceFileResource.postSiteResourceFile(
				testGroup.getExternalReferenceCode(), resourceFile);

		ResourceFile getResourceFile = resourceFileResource.getSiteResourceFile(
			testGroup.getExternalReferenceCode(),
			postResourceFile.getExternalReferenceCode());

		FileURLReference fileURLReference =
			getResourceFile.getFileURLReference();

		Assert.assertNull(fileURLReference.getFileBase64());

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				"headless-admin-fragment/v1.0/sites/",
				testGroup.getExternalReferenceCode(), "/resource-files/",
				postResourceFile.getExternalReferenceCode(),
				"?nestedFields=fileURLReference.fileBase64"),
			Http.Method.GET);

		JSONObject fileURLReferenceJSONObject = jsonObject.getJSONObject(
			"fileURLReference");

		Assert.assertArrayEquals(
			bytes,
			Base64.decode(fileURLReferenceJSONObject.getString("fileBase64")));
	}

	private void _testGetSiteResourceFileFileURLReferenceURL()
		throws Exception {

		ResourceFile resourceFile = _randomResourceFile(
			_getFragmentSetExternalReferenceCode());

		byte[] bytes = RandomTestUtil.randomBytes();

		resourceFile.setFileURLReference(_toFileURLReference(bytes));

		ResourceFile postResourceFile =
			resourceFileResource.postSiteResourceFile(
				testGroup.getExternalReferenceCode(), resourceFile);

		ResourceFile getResourceFile = resourceFileResource.getSiteResourceFile(
			testGroup.getExternalReferenceCode(),
			postResourceFile.getExternalReferenceCode());

		FileURLReference fileURLReference =
			getResourceFile.getFileURLReference();

		_assertURLContent(bytes, fileURLReference.getUrl());
	}

	private void _testGetSiteResourceFileFragmentSet() throws Exception {
		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFile postResourceFile =
			resourceFileResource.postSiteResourceFile(
				testGroup.getExternalReferenceCode(),
				_randomResourceFile(
					fragmentCollection.getExternalReferenceCode()));

		ResourceFile getResourceFile = resourceFileResource.getSiteResourceFile(
			testGroup.getExternalReferenceCode(),
			postResourceFile.getExternalReferenceCode());

		Assert.assertNull(getResourceFile.getFragmentSet());

		getResourceFile = _getSiteResourceFile(
			postResourceFile.getExternalReferenceCode());

		FragmentSet fragmentSet = getResourceFile.getFragmentSet();

		Assert.assertEquals(
			fragmentCollection.getExternalReferenceCode(),
			fragmentSet.getExternalReferenceCode());
	}

	private void _testGetSiteResourceFileGuest() throws Exception {
		ResourceFile resourceFile = _randomResourceFile(
			_getFragmentSetExternalReferenceCode());

		byte[] bytes = RandomTestUtil.randomBytes();

		resourceFile.setFileURLReference(_toFileURLReference(bytes));

		ResourceFile postResourceFile =
			resourceFileResource.postSiteResourceFile(
				testGroup.getExternalReferenceCode(), resourceFile);

		try {
			_guestResourceFileResource.getSiteResourceFile(
				testGroup.getExternalReferenceCode(),
				postResourceFile.getExternalReferenceCode());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("403", problem.getStatus());
		}

		FileURLReference fileURLReference =
			postResourceFile.getFileURLReference();

		_assertURLContent(bytes, fileURLReference.getUrl());
	}

	private void _testGetSiteResourceFileNonexistentProblemException()
		throws Exception {

		try {
			resourceFileResource.getSiteResourceFile(
				testGroup.getExternalReferenceCode(),
				RandomTestUtil.randomString());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}
	}

	private void _testGetSiteResourceFilePortletFileProblemException()
		throws Exception {

		FileEntry fileEntry = _addPortletFileEntry();

		try {
			resourceFileResource.getSiteResourceFile(
				testGroup.getExternalReferenceCode(),
				fileEntry.getExternalReferenceCode());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}
	}

	private void _testGetSiteResourceFileResourceFolder() throws Exception {
		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFile resourceFile = _randomResourceFile(
			fragmentCollection.getExternalReferenceCode());

		ResourceFolder postResourceFolder = _postSiteResourceFolder(
			fragmentCollection.getExternalReferenceCode());

		resourceFile.setResourceFolderExternalReferenceCode(
			postResourceFolder.getExternalReferenceCode());

		ResourceFile postResourceFile =
			resourceFileResource.postSiteResourceFile(
				testGroup.getExternalReferenceCode(), resourceFile);

		ResourceFile getResourceFile = resourceFileResource.getSiteResourceFile(
			testGroup.getExternalReferenceCode(),
			postResourceFile.getExternalReferenceCode());

		Assert.assertNull(getResourceFile.getResourceFolder());

		getResourceFile = _getSiteResourceFile(
			postResourceFile.getExternalReferenceCode());

		ResourceFolder getResourceFolder = getResourceFile.getResourceFolder();

		Assert.assertEquals(
			postResourceFolder.getExternalReferenceCode(),
			getResourceFolder.getExternalReferenceCode());
	}

	private void _testGetSiteResourceFilesPage() throws Exception {
		FragmentCollection fragmentCollection1 = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFile childResourceFile1 = _postSiteResourceFile(
			_postSiteResourceFolder(
				fragmentCollection1.getExternalReferenceCode()));

		ResourceFile resourceFile1 = resourceFileResource.postSiteResourceFile(
			testGroup.getExternalReferenceCode(),
			_randomResourceFile(
				fragmentCollection1.getExternalReferenceCode()));

		FragmentCollection fragmentCollection2 = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFile childResourceFile2 = _postSiteResourceFile(
			_postSiteResourceFolder(
				fragmentCollection2.getExternalReferenceCode()));

		ResourceFile resourceFile2 = resourceFileResource.postSiteResourceFile(
			testGroup.getExternalReferenceCode(),
			_randomResourceFile(
				fragmentCollection2.getExternalReferenceCode()));

		Page<ResourceFile> page = resourceFileResource.getSiteResourceFilesPage(
			testGroup.getExternalReferenceCode(), null, Pagination.of(1, 1));

		long totalCount = page.getTotalCount();

		page = resourceFileResource.getSiteResourceFilesPage(
			testGroup.getExternalReferenceCode(), null,
			Pagination.of(1, (int)totalCount));

		List<ResourceFile> resourceFiles = (List<ResourceFile>)page.getItems();

		assertContains(childResourceFile1, resourceFiles);
		assertContains(childResourceFile2, resourceFiles);
		assertContains(resourceFile1, resourceFiles);
		assertContains(resourceFile2, resourceFiles);
	}

	private void _testGetSiteResourceFilesPagePortletFile() throws Exception {
		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFile resourceFile = resourceFileResource.postSiteResourceFile(
			testGroup.getExternalReferenceCode(),
			_randomResourceFile(fragmentCollection.getExternalReferenceCode()));

		Page<ResourceFile> page = resourceFileResource.getSiteResourceFilesPage(
			testGroup.getExternalReferenceCode(), null, Pagination.of(1, 1));

		long totalCount = page.getTotalCount();

		FileEntry fileEntry = _addPortletFileEntry();

		page = resourceFileResource.getSiteResourceFilesPage(
			testGroup.getExternalReferenceCode(), null,
			Pagination.of(1, (int)totalCount + 1));

		Assert.assertEquals(totalCount, page.getTotalCount());

		List<ResourceFile> resourceFiles = (List<ResourceFile>)page.getItems();

		assertContains(resourceFile, resourceFiles);
		_assertNotContains(fileEntry, resourceFiles);
	}

	private void _testGetSiteResourceFilesPageWithoutPermissions()
		throws Exception {

		resourceFileResource.postSiteResourceFile(
			testGroup.getExternalReferenceCode(), randomResourceFile());

		Page<ResourceFile> page =
			_userWithoutPermissionsResourceFileResource.
				getSiteResourceFilesPage(
					testGroup.getExternalReferenceCode(), null,
					Pagination.of(1, 10));

		Assert.assertEquals(0, page.getTotalCount());
	}

	private void _testGetSiteResourceFilesPageWithPermissions()
		throws Exception {

		ResourceFile resourceFile = resourceFileResource.postSiteResourceFile(
			testGroup.getExternalReferenceCode(), randomResourceFile());

		Page<ResourceFile> page =
			_userWithPermissionsResourceFileResource.getSiteResourceFilesPage(
				testGroup.getExternalReferenceCode(), null,
				Pagination.of(1, 1));

		page =
			_userWithPermissionsResourceFileResource.getSiteResourceFilesPage(
				testGroup.getExternalReferenceCode(), null,
				Pagination.of(1, (int)page.getTotalCount()));

		assertContains(resourceFile, (List<ResourceFile>)page.getItems());
	}

	private void _testGetSiteResourceFileWithoutPermissionsProblemException()
		throws Exception {

		ResourceFile resourceFile = resourceFileResource.postSiteResourceFile(
			testGroup.getExternalReferenceCode(), randomResourceFile());

		try {
			_userWithoutPermissionsResourceFileResource.getSiteResourceFile(
				testGroup.getExternalReferenceCode(),
				resourceFile.getExternalReferenceCode());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}
	}

	private void _testGetSiteResourceFileWithPermissions() throws Exception {
		ResourceFile resourceFile = resourceFileResource.postSiteResourceFile(
			testGroup.getExternalReferenceCode(), randomResourceFile());

		ResourceFile getResourceFile =
			_userWithPermissionsResourceFileResource.getSiteResourceFile(
				testGroup.getExternalReferenceCode(),
				resourceFile.getExternalReferenceCode());

		assertEquals(resourceFile, getResourceFile);
		assertValid(getResourceFile);
	}

	private void _testGetSiteResourceFolderResourceFilesPage()
		throws Exception {

		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFolder parentResourceFolder = _postSiteResourceFolder(
			fragmentCollection.getExternalReferenceCode());

		ResourceFile resourceFile = _postSiteResourceFile(parentResourceFolder);

		_postSiteResourceFile(_postSiteResourceFolder(parentResourceFolder));

		Page<ResourceFile> page =
			resourceFileResource.getSiteResourceFolderResourceFilesPage(
				testGroup.getExternalReferenceCode(),
				parentResourceFolder.getExternalReferenceCode(),
				Pagination.of(1, 10));

		assertContains(resourceFile, (List<ResourceFile>)page.getItems());
		Assert.assertEquals(1, page.getTotalCount());
	}

	private void _testGetSiteResourceFolderResourceFilesPageEmpty()
		throws Exception {

		ResourceFolder resourceFolder = _postSiteResourceFolder(
			_getFragmentSetExternalReferenceCode());

		Page<ResourceFile> page =
			resourceFileResource.getSiteResourceFolderResourceFilesPage(
				testGroup.getExternalReferenceCode(),
				resourceFolder.getExternalReferenceCode(),
				Pagination.of(1, 10));

		Assert.assertEquals(0, page.getTotalCount());
	}

	private void _testGetSiteResourceFolderResourceFilesPagePortletFolderProblemException()
		throws Exception {

		Folder folder = _addPortletFolder();

		try {
			resourceFileResource.getSiteResourceFolderResourceFilesPage(
				testGroup.getExternalReferenceCode(),
				folder.getExternalReferenceCode(), Pagination.of(1, 10));

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}
	}

	private void _testGetSiteResourceFolderResourceFilesPageResourceFolderNonexistentProblemException()
		throws Exception {

		try {
			resourceFileResource.getSiteResourceFolderResourceFilesPage(
				testGroup.getExternalReferenceCode(),
				RandomTestUtil.randomString(), Pagination.of(1, 10));

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}
	}

	private void _testGetSiteResourceFolderResourceFilesPageWithoutPermissionsProblemException()
		throws Exception {

		ResourceFolder resourceFolder = _postSiteResourceFolder(
			_getFragmentSetExternalReferenceCode());

		try {
			_userWithoutPermissionsResourceFileResource.
				getSiteResourceFolderResourceFilesPage(
					testGroup.getExternalReferenceCode(),
					resourceFolder.getExternalReferenceCode(),
					Pagination.of(1, 10));

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}
	}

	private void _testGetSiteResourceFolderResourceFilesPageWithPermissions()
		throws Exception {

		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFolder resourceFolder = _postSiteResourceFolder(
			fragmentCollection.getExternalReferenceCode());

		ResourceFile resourceFile = _postSiteResourceFile(resourceFolder);

		Page<ResourceFile> page =
			_userWithPermissionsResourceFileResource.
				getSiteResourceFolderResourceFilesPage(
					testGroup.getExternalReferenceCode(),
					resourceFolder.getExternalReferenceCode(),
					Pagination.of(1, 10));

		assertContains(resourceFile, (List<ResourceFile>)page.getItems());
		Assert.assertEquals(1, page.getTotalCount());
	}

	private void _testPostSiteFragmentSetResourceFileWithoutPermissionsProblemException()
		throws Exception {

		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		try {
			_userWithoutPermissionsResourceFileResource.
				postSiteFragmentSetResourceFile(
					testGroup.getExternalReferenceCode(),
					fragmentCollection.getExternalReferenceCode(),
					_randomResourceFile(
						fragmentCollection.getExternalReferenceCode()));

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("FORBIDDEN", problem.getStatus());
		}
	}

	private void _testPostSiteFragmentSetResourceFileWithPermissions()
		throws Exception {

		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFile resourceFile = _randomResourceFile(
			fragmentCollection.getExternalReferenceCode());

		ResourceFile postResourceFile =
			_userWithPermissionsResourceFileResource.
				postSiteFragmentSetResourceFile(
					testGroup.getExternalReferenceCode(),
					fragmentCollection.getExternalReferenceCode(),
					resourceFile);

		assertEquals(resourceFile, postResourceFile);
		assertValid(postResourceFile);
	}

	private void _testPostSiteResourceFile() throws Exception {
		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFolder postResourceFolder = _postSiteResourceFolder(
			fragmentCollection.getExternalReferenceCode());

		ResourceFile postResourceFile =
			resourceFileResource.postSiteResourceFile(
				testGroup.getExternalReferenceCode(),
				_randomResourceFile(postResourceFolder));

		ResourceFile getResourceFile = _getSiteResourceFile(
			postResourceFile.getExternalReferenceCode());

		FragmentSet fragmentSet = getResourceFile.getFragmentSet();

		Assert.assertEquals(
			fragmentCollection.getExternalReferenceCode(),
			fragmentSet.getExternalReferenceCode());

		ResourceFolder getResourceFolder = getResourceFile.getResourceFolder();

		Assert.assertEquals(
			postResourceFolder.getExternalReferenceCode(),
			getResourceFolder.getExternalReferenceCode());
	}

	private void _testPostSiteResourceFileBatch() throws Exception {
		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFolder postResourceFolder = _postSiteResourceFolder(
			fragmentCollection.getExternalReferenceCode());

		ResourceFile resourceFile = _randomResourceFile(postResourceFolder);

		byte[] bytes = RandomTestUtil.randomBytes();

		resourceFile.setFileURLReference(_toFileURLReference(bytes));

		ResourceFile postResourceFile =
			resourceFileResource.postSiteResourceFile(
				testGroup.getExternalReferenceCode(), resourceFile);

		try (SafeCloseable safeCloseable =
				LazyReferencingTestUtil.setLazyReferencingWithSafeCloseable(
					true)) {

			waitForFinish(
				"COMPLETED",
				HTTPTestUtil.invokeToJSONObject(
					_exportResourceFilesToJSON(
						testGroup.getExternalReferenceCode()),
					"headless-admin-fragment/v1.0/sites/" +
						irrelevantGroup.getExternalReferenceCode() +
							"/resource-files/batch?createStrategy=INSERT",
					Http.Method.POST));
		}

		ResourceFile importedResourceFile = _getSiteResourceFile(
			postResourceFile.getExternalReferenceCode(),
			irrelevantGroup.getExternalReferenceCode());

		FragmentSet importedFragmentSet = importedResourceFile.getFragmentSet();

		Assert.assertEquals(
			fragmentCollection.getExternalReferenceCode(),
			importedFragmentSet.getExternalReferenceCode());

		Assert.assertNotNull(
			_fragmentCollectionLocalService.
				fetchFragmentCollectionByExternalReferenceCode(
					fragmentCollection.getExternalReferenceCode(),
					irrelevantGroup.getGroupId()));

		Assert.assertEquals(
			postResourceFile.getName(), importedResourceFile.getName());

		ResourceFolder importedResourceFolder =
			importedResourceFile.getResourceFolder();

		Assert.assertEquals(
			postResourceFolder.getExternalReferenceCode(),
			importedResourceFolder.getExternalReferenceCode());

		_assertContent(
			bytes, postResourceFile.getExternalReferenceCode(),
			irrelevantGroup.getGroupId());
	}

	private void _testPostSiteResourceFileDuplicateExternalReferenceCodeProblemException()
		throws Exception {

		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFile postResourceFile =
			resourceFileResource.postSiteResourceFile(
				testGroup.getExternalReferenceCode(),
				_randomResourceFile(
					fragmentCollection.getExternalReferenceCode()));

		ResourceFile resourceFile = _randomResourceFile(
			fragmentCollection.getExternalReferenceCode());

		resourceFile.setExternalReferenceCode(
			postResourceFile.getExternalReferenceCode());

		_assertProblemException(
			"CONFLICT", "this-external-reference-code-is-already-in-use",
			() -> resourceFileResource.postSiteResourceFile(
				testGroup.getExternalReferenceCode(), resourceFile));
	}

	private void _testPostSiteResourceFileFileURLReferenceFileBase64()
		throws Exception {

		FileURLReference fileURLReference = new FileURLReference();

		fileURLReference.setFileBase64(_content1Base64);

		_postSiteResourceFileAndAssertContent(_content1Bytes, fileURLReference);
	}

	private void _testPostSiteResourceFileFileURLReferenceFileBase64AndURL()
		throws Exception {

		FileURLReference fileURLReference = new FileURLReference();

		fileURLReference.setFileBase64(_content1Base64);
		fileURLReference.setUrl(_content2URL);

		_postSiteResourceFileAndAssertContent(_content1Bytes, fileURLReference);
	}

	private void _testPostSiteResourceFileFileURLReferenceFileBase64AndURLNullProblemException()
		throws Exception {

		ResourceFile resourceFile = _randomResourceFile(
			_getFragmentSetExternalReferenceCode());

		resourceFile.setFileURLReference(new FileURLReference());

		_assertProblemException(
			"a-file-url-reference-with-content-is-required",
			() -> resourceFileResource.postSiteResourceFile(
				testGroup.getExternalReferenceCode(), resourceFile));
	}

	private void _testPostSiteResourceFileFileURLReferenceNullProblemException()
		throws Exception {

		ResourceFile resourceFile = _randomResourceFile(
			_getFragmentSetExternalReferenceCode());

		resourceFile.setFileURLReference((FileURLReference)null);

		_assertProblemException(
			"a-file-url-reference-with-content-is-required",
			() -> resourceFileResource.postSiteResourceFile(
				testGroup.getExternalReferenceCode(), resourceFile));
	}

	private void _testPostSiteResourceFileFileURLReferenceProblemException(
			String expectedTitle, String url)
		throws Exception {

		ResourceFile resourceFile = _randomResourceFile(
			_getFragmentSetExternalReferenceCode());

		FileURLReference fileURLReference = new FileURLReference();

		fileURLReference.setUrl(url);

		resourceFile.setFileURLReference(fileURLReference);

		try {
			resourceFileResource.postSiteResourceFile(
				testGroup.getExternalReferenceCode(), resourceFile);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(expectedTitle, problem.getTitle());
		}
	}

	private void _testPostSiteResourceFileFileURLReferenceURL()
		throws Exception {

		FileURLReference fileURLReference = new FileURLReference();

		fileURLReference.setUrl(_content1URL);

		_postSiteResourceFileAndAssertContent(_content1Bytes, fileURLReference);
	}

	private void _testPostSiteResourceFileFileURLReferenceURLLARProblemException()
		throws Exception {

		String url = "lar://12345/" + RandomTestUtil.randomString();

		_testPostSiteResourceFileFileURLReferenceProblemException(
			"Unable to download file from " + url, url);
	}

	private void _testPostSiteResourceFileFileURLReferenceURLUnreachableProblemException()
		throws Exception {

		String url = "http://127.0.0.1:1/" + RandomTestUtil.randomString();

		_testPostSiteResourceFileFileURLReferenceProblemException(
			"Unable to download file from " + url, url);
	}

	private void _testPostSiteResourceFileFileURLReferenceURLUnsupportedProtocolProblemException()
		throws Exception {

		String url =
			"ftp://invalid.example.test/" + RandomTestUtil.randomString();

		_testPostSiteResourceFileFileURLReferenceProblemException(
			"Unable to download file from " + url +
				" because of unsupported protocol ftp",
			url);
	}

	private void _testPostSiteResourceFileFragmentSetAndFragmentSetExternalReferenceCode()
		throws Exception {

		FragmentCollection fragmentCollection1 = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFile resourceFile = _randomResourceFile(
			fragmentCollection1.getExternalReferenceCode());

		FragmentCollection fragmentCollection2 = _addFragmentCollection(
			testGroup.getGroupId());

		resourceFile.setFragmentSet(
			_toFragmentSet(fragmentCollection2.getExternalReferenceCode()));

		ResourceFile postResourceFile =
			resourceFileResource.postSiteResourceFile(
				testGroup.getExternalReferenceCode(), resourceFile);

		ResourceFile getResourceFile = _getSiteResourceFile(
			postResourceFile.getExternalReferenceCode());

		FragmentSet fragmentSet = getResourceFile.getFragmentSet();

		Assert.assertEquals(
			fragmentCollection1.getExternalReferenceCode(),
			fragmentSet.getExternalReferenceCode());

		Assert.assertEquals(
			fragmentCollection1.getExternalReferenceCode(),
			getResourceFile.getFragmentSetExternalReferenceCode());
	}

	private void _testPostSiteResourceFileFragmentSetAndFragmentSetExternalReferenceCodeProblemException()
		throws Exception {

		ResourceFile resourceFile = _randomResourceFile(
			RandomTestUtil.randomString());

		resourceFile.setFragmentSet(
			_toFragmentSet(RandomTestUtil.randomString()));

		_assertProblemException(
			"the-fragment-set-external-reference-codes-do-not-match",
			() -> {
				try (SafeCloseable safeCloseable =
						LazyReferencingTestUtil.
							setLazyReferencingWithSafeCloseable(true)) {

					resourceFileResource.postSiteResourceFile(
						testGroup.getExternalReferenceCode(), resourceFile);
				}
			});
	}

	private void _testPostSiteResourceFileFragmentSetExternalReferenceCode()
		throws Exception {

		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFile postResourceFile =
			resourceFileResource.postSiteResourceFile(
				testGroup.getExternalReferenceCode(),
				_randomResourceFile(
					fragmentCollection.getExternalReferenceCode()));

		ResourceFile getResourceFile = _getSiteResourceFile(
			postResourceFile.getExternalReferenceCode());

		FragmentSet fragmentSet = getResourceFile.getFragmentSet();

		Assert.assertEquals(
			fragmentCollection.getExternalReferenceCode(),
			fragmentSet.getExternalReferenceCode());

		Assert.assertEquals(
			fragmentCollection.getExternalReferenceCode(),
			getResourceFile.getFragmentSetExternalReferenceCode());
	}

	private void _testPostSiteResourceFileFragmentSetExternalReferenceCodeNullProblemException()
		throws Exception {

		_assertProblemException(
			"a-fragment-set-external-reference-code-is-required-to-create-a-" +
				"new-resource-file",
			() -> resourceFileResource.postSiteResourceFile(
				testGroup.getExternalReferenceCode(),
				_randomResourceFile((String)null)));
	}

	private void _testPostSiteResourceFileFragmentSetNonexistentProblemException()
		throws Exception {

		String fragmentSetExternalReferenceCode = RandomTestUtil.randomString();

		ResourceFile resourceFile = _randomResourceFile(
			fragmentSetExternalReferenceCode);

		_assertProblemException(
			"no-fragment-set-was-found-with-external-reference-code-x",
			() -> resourceFileResource.postSiteResourceFile(
				testGroup.getExternalReferenceCode(), resourceFile),
			fragmentSetExternalReferenceCode);
	}

	private void _testPostSiteResourceFileNameNullProblemException()
		throws Exception {

		ResourceFile resourceFile = _randomResourceFile(
			_getFragmentSetExternalReferenceCode());

		resourceFile.setName((String)null);

		_assertProblemException(
			"name-is-required",
			() -> resourceFileResource.postSiteResourceFile(
				testGroup.getExternalReferenceCode(), resourceFile));
	}

	private void _testPostSiteResourceFileResourceFolderAndResourceFolderExternalReferenceCode()
		throws Exception {

		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFile resourceFile = _randomResourceFile(
			fragmentCollection.getExternalReferenceCode());

		ResourceFolder postResourceFolder1 = _postSiteResourceFolder(
			fragmentCollection.getExternalReferenceCode());

		resourceFile.setResourceFolder(postResourceFolder1);

		ResourceFolder postResourceFolder2 = _postSiteResourceFolder(
			fragmentCollection.getExternalReferenceCode());

		resourceFile.setResourceFolderExternalReferenceCode(
			postResourceFolder2.getExternalReferenceCode());

		ResourceFile postResourceFile =
			resourceFileResource.postSiteResourceFile(
				testGroup.getExternalReferenceCode(), resourceFile);

		ResourceFile getResourceFile = _getSiteResourceFile(
			postResourceFile.getExternalReferenceCode());

		ResourceFolder getResourceFolder = getResourceFile.getResourceFolder();

		Assert.assertEquals(
			postResourceFolder2.getExternalReferenceCode(),
			getResourceFolder.getExternalReferenceCode());
	}

	private void _testPostSiteResourceFileResourceFolderExternalReferenceCode()
		throws Exception {

		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFile resourceFile = _randomResourceFile(
			fragmentCollection.getExternalReferenceCode());

		byte[] bytes = RandomTestUtil.randomBytes();

		resourceFile.setFileURLReference(_toFileURLReference(bytes));

		ResourceFolder postResourceFolder = _postSiteResourceFolder(
			fragmentCollection.getExternalReferenceCode());

		resourceFile.setResourceFolderExternalReferenceCode(
			postResourceFolder.getExternalReferenceCode());

		ResourceFile postResourceFile =
			resourceFileResource.postSiteResourceFile(
				testGroup.getExternalReferenceCode(), resourceFile);

		ResourceFile getResourceFile = _getSiteResourceFile(
			postResourceFile.getExternalReferenceCode());

		FileURLReference fileURLReference =
			getResourceFile.getFileURLReference();

		_assertURLContent(bytes, fileURLReference.getUrl());

		ResourceFolder getResourceFolder = getResourceFile.getResourceFolder();

		Assert.assertEquals(
			postResourceFolder.getExternalReferenceCode(),
			getResourceFolder.getExternalReferenceCode());

		Assert.assertEquals(
			postResourceFolder.getExternalReferenceCode(),
			getResourceFile.getResourceFolderExternalReferenceCode());
	}

	private void _testPostSiteResourceFileResourceFolderNonexistentProblemException()
		throws Exception {

		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFile resourceFile = _randomResourceFile(
			fragmentCollection.getExternalReferenceCode());

		String resourceFolderExternalReferenceCode =
			RandomTestUtil.randomString();

		resourceFile.setResourceFolderExternalReferenceCode(
			resourceFolderExternalReferenceCode);

		_assertProblemException(
			"no-resource-folder-was-found-with-external-reference-code-x",
			() -> resourceFileResource.postSiteResourceFile(
				testGroup.getExternalReferenceCode(), resourceFile),
			resourceFolderExternalReferenceCode);
	}

	private void _testPostSiteResourceFileResourceFolderPortletFolderProblemException()
		throws Exception {

		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFile resourceFile = _randomResourceFile(
			fragmentCollection.getExternalReferenceCode());

		Folder folder = _addPortletFolder();

		String resourceFolderExternalReferenceCode =
			folder.getExternalReferenceCode();

		resourceFile.setResourceFolderExternalReferenceCode(
			resourceFolderExternalReferenceCode);

		_assertProblemException(
			"no-resource-folder-was-found-with-external-reference-code-x",
			() -> resourceFileResource.postSiteResourceFile(
				testGroup.getExternalReferenceCode(), resourceFile),
			resourceFolderExternalReferenceCode);
	}

	private void _testPostSiteResourceFileWithoutPermissionsProblemException()
		throws Exception {

		try {
			_userWithoutPermissionsResourceFileResource.postSiteResourceFile(
				testGroup.getExternalReferenceCode(), randomResourceFile());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("FORBIDDEN", problem.getStatus());
		}
	}

	private void _testPostSiteResourceFileWithPermissions() throws Exception {
		ResourceFile resourceFile = randomResourceFile();

		ResourceFile postResourceFile =
			_userWithPermissionsResourceFileResource.postSiteResourceFile(
				testGroup.getExternalReferenceCode(), resourceFile);

		assertEquals(resourceFile, postResourceFile);
		assertValid(postResourceFile);
	}

	private void _testPutSiteResourceFile() throws Exception {
		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFile originalResourceFile = _randomResourceFile(
			fragmentCollection.getExternalReferenceCode());

		ResourceFile putResourceFile = resourceFileResource.putSiteResourceFile(
			testGroup.getExternalReferenceCode(),
			originalResourceFile.getExternalReferenceCode(),
			originalResourceFile);

		assertEquals(originalResourceFile, putResourceFile);
		assertValid(putResourceFile);

		Date dateCreated = putResourceFile.getDateCreated();

		FileURLReference fileURLReference =
			putResourceFile.getFileURLReference();

		FragmentCollection irrelevantFragmentCollection =
			_addFragmentCollection(testGroup.getGroupId());

		ResourceFile updatedResourceFile = _randomResourceFile(
			_postSiteResourceFolder(
				irrelevantFragmentCollection.getExternalReferenceCode()));

		updatedResourceFile.setDateCreated(RandomTestUtil.nextDate());

		byte[] bytes = RandomTestUtil.randomBytes();

		updatedResourceFile.setFileURLReference(_toFileURLReference(bytes));

		putResourceFile = _putSiteResourceFile(
			updatedResourceFile,
			originalResourceFile.getExternalReferenceCode());

		Assert.assertEquals(
			originalResourceFile.getExternalReferenceCode(),
			putResourceFile.getExternalReferenceCode());

		FragmentSet fragmentSet = putResourceFile.getFragmentSet();

		Assert.assertEquals(
			fragmentCollection.getExternalReferenceCode(),
			fragmentSet.getExternalReferenceCode());

		Assert.assertEquals(
			updatedResourceFile.getName(), putResourceFile.getName());
		Assert.assertNull(putResourceFile.getResourceFolder());
		Assert.assertNull(
			putResourceFile.getResourceFolderExternalReferenceCode());

		_assertContent(
			bytes, originalResourceFile.getExternalReferenceCode(),
			testGroup.getGroupId());

		ResourceFile getResourceFile = resourceFileResource.getSiteResourceFile(
			testGroup.getExternalReferenceCode(),
			originalResourceFile.getExternalReferenceCode());

		Assert.assertEquals(dateCreated, getResourceFile.getDateCreated());
		Assert.assertEquals(
			originalResourceFile.getExternalReferenceCode(),
			getResourceFile.getExternalReferenceCode());
		Assert.assertEquals(
			updatedResourceFile.getName(), getResourceFile.getName());

		_assertURLContent(bytes, fileURLReference.getUrl());

		FileEntry fileEntry =
			PortletFileRepositoryUtil.
				fetchPortletFileEntryByExternalReferenceCode(
					originalResourceFile.getExternalReferenceCode(),
					testGroup.getGroupId());

		Assert.assertEquals("1.0", fileEntry.getVersion());
	}

	private void _testPutSiteResourceFileBatch() throws Exception {
		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFile postResourceFile =
			resourceFileResource.postSiteResourceFile(
				testGroup.getExternalReferenceCode(),
				_randomResourceFile(
					fragmentCollection.getExternalReferenceCode()));

		try (SafeCloseable safeCloseable =
				LazyReferencingTestUtil.setLazyReferencingWithSafeCloseable(
					true)) {

			waitForFinish(
				"COMPLETED",
				HTTPTestUtil.invokeToJSONObject(
					_exportResourceFilesToJSON(
						testGroup.getExternalReferenceCode()),
					"headless-admin-fragment/v1.0/sites/" +
						irrelevantGroup.getExternalReferenceCode() +
							"/resource-files/batch?createStrategy=INSERT",
					Http.Method.POST));
		}

		ResourceFile updatedResourceFile = _randomResourceFile(
			fragmentCollection.getExternalReferenceCode());

		byte[] bytes = RandomTestUtil.randomBytes();

		updatedResourceFile.setFileURLReference(_toFileURLReference(bytes));

		ResourceFile putResourceFile = resourceFileResource.putSiteResourceFile(
			testGroup.getExternalReferenceCode(),
			postResourceFile.getExternalReferenceCode(), updatedResourceFile);

		try (SafeCloseable safeCloseable =
				LazyReferencingTestUtil.setLazyReferencingWithSafeCloseable(
					true)) {

			waitForFinish(
				"COMPLETED",
				HTTPTestUtil.invokeToJSONObject(
					_exportResourceFilesToJSON(
						testGroup.getExternalReferenceCode()),
					"headless-admin-fragment/v1.0/sites/" +
						irrelevantGroup.getExternalReferenceCode() +
							"/resource-files/batch?createStrategy=UPSERT",
					Http.Method.POST));
		}

		ResourceFile importedResourceFile = _getSiteResourceFile(
			postResourceFile.getExternalReferenceCode(),
			irrelevantGroup.getExternalReferenceCode());

		Assert.assertEquals(
			putResourceFile.getName(), importedResourceFile.getName());

		_assertContent(
			bytes, postResourceFile.getExternalReferenceCode(),
			irrelevantGroup.getGroupId());
	}

	private void _testPutSiteResourceFileFileURLReferenceFileBase64()
		throws Exception {

		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFile resourceFile = _randomResourceFile(
			fragmentCollection.getExternalReferenceCode());

		resourceFile.setFileURLReference(
			_toFileURLReference(RandomTestUtil.randomBytes()));

		ResourceFile postResourceFile =
			resourceFileResource.postSiteResourceFile(
				testGroup.getExternalReferenceCode(), resourceFile);

		FileURLReference postFileURLReference =
			postResourceFile.getFileURLReference();

		byte[] bytes = RandomTestUtil.randomBytes();

		postResourceFile.setFileURLReference(_toFileURLReference(bytes));

		ResourceFile putResourceFile = resourceFileResource.putSiteResourceFile(
			testGroup.getExternalReferenceCode(),
			postResourceFile.getExternalReferenceCode(), postResourceFile);

		FileURLReference putFileURLReference =
			putResourceFile.getFileURLReference();

		Assert.assertEquals(
			postFileURLReference.getUrl(), putFileURLReference.getUrl());

		_assertURLContent(bytes, postFileURLReference.getUrl());
	}

	private void _testPutSiteResourceFileFileURLReferenceFileBase64AndURLNullProblemException()
		throws Exception {

		ResourceFile postResourceFile =
			resourceFileResource.postSiteResourceFile(
				testGroup.getExternalReferenceCode(),
				_randomResourceFile(_getFragmentSetExternalReferenceCode()));

		ResourceFile updatedResourceFile = _randomResourceFile(
			_getFragmentSetExternalReferenceCode());

		updatedResourceFile.setFileURLReference(new FileURLReference());

		_assertProblemException(
			"a-file-url-reference-with-content-is-required",
			() -> resourceFileResource.putSiteResourceFile(
				testGroup.getExternalReferenceCode(),
				postResourceFile.getExternalReferenceCode(),
				updatedResourceFile));
	}

	private void _testPutSiteResourceFileFileURLReferenceNullProblemException()
		throws Exception {

		ResourceFile postResourceFile =
			resourceFileResource.postSiteResourceFile(
				testGroup.getExternalReferenceCode(),
				_randomResourceFile(_getFragmentSetExternalReferenceCode()));

		ResourceFile updatedResourceFile = _randomResourceFile(
			_getFragmentSetExternalReferenceCode());

		updatedResourceFile.setFileURLReference((FileURLReference)null);

		_assertProblemException(
			"a-file-url-reference-with-content-is-required",
			() -> resourceFileResource.putSiteResourceFile(
				testGroup.getExternalReferenceCode(),
				postResourceFile.getExternalReferenceCode(),
				updatedResourceFile));
	}

	private void _testPutSiteResourceFileFileURLReferenceURL()
		throws Exception {

		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFile postResourceFile =
			resourceFileResource.postSiteResourceFile(
				testGroup.getExternalReferenceCode(),
				_randomResourceFile(
					fragmentCollection.getExternalReferenceCode()));

		ResourceFile updatedResourceFile = _randomResourceFile(
			fragmentCollection.getExternalReferenceCode());

		FileURLReference fileURLReference = new FileURLReference();

		fileURLReference.setUrl(_content2URL);

		updatedResourceFile.setFileURLReference(fileURLReference);

		resourceFileResource.putSiteResourceFile(
			testGroup.getExternalReferenceCode(),
			postResourceFile.getExternalReferenceCode(), updatedResourceFile);

		_assertContent(
			_content2Bytes, postResourceFile.getExternalReferenceCode(),
			testGroup.getGroupId());
	}

	private void _testPutSiteResourceFileName() throws Exception {
		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFile originalResourceFile = _randomResourceFile(
			fragmentCollection.getExternalReferenceCode());

		byte[] bytes = RandomTestUtil.randomBytes();

		originalResourceFile.setFileURLReference(_toFileURLReference(bytes));

		ResourceFile postResourceFile =
			_nestedFieldsResourceFileResource.postSiteResourceFile(
				testGroup.getExternalReferenceCode(), originalResourceFile);

		ResourceFile getResourceFile =
			_nestedFieldsResourceFileResource.getSiteResourceFile(
				testGroup.getExternalReferenceCode(),
				postResourceFile.getExternalReferenceCode());

		FileURLReference getFileURLReference =
			getResourceFile.getFileURLReference();

		ResourceFile updatedResourceFile = _randomResourceFile(
			fragmentCollection.getExternalReferenceCode());

		FileURLReference updatedFileURLReference = new FileURLReference();

		updatedFileURLReference.setFileBase64(
			getFileURLReference.getFileBase64());

		updatedResourceFile.setFileURLReference(updatedFileURLReference);

		ResourceFile putResourceFile =
			_nestedFieldsResourceFileResource.putSiteResourceFile(
				testGroup.getExternalReferenceCode(),
				postResourceFile.getExternalReferenceCode(),
				updatedResourceFile);

		FileURLReference putFileURLReference =
			putResourceFile.getFileURLReference();

		Assert.assertNotEquals(
			getFileURLReference.getUrl(), putFileURLReference.getUrl());

		Assert.assertEquals(
			updatedResourceFile.getName(), putResourceFile.getName());
		Assert.assertNotEquals(
			postResourceFile.getName(), putResourceFile.getName());

		_assertContent(
			bytes, postResourceFile.getExternalReferenceCode(),
			testGroup.getGroupId());
	}

	private void _testPutSiteResourceFileNameNullProblemException()
		throws Exception {

		ResourceFile postResourceFile =
			resourceFileResource.postSiteResourceFile(
				testGroup.getExternalReferenceCode(),
				_randomResourceFile(_getFragmentSetExternalReferenceCode()));

		ResourceFile updatedResourceFile = _randomResourceFile(
			_getFragmentSetExternalReferenceCode());

		updatedResourceFile.setName((String)null);

		_assertProblemException(
			"name-is-required",
			() -> resourceFileResource.putSiteResourceFile(
				testGroup.getExternalReferenceCode(),
				postResourceFile.getExternalReferenceCode(),
				updatedResourceFile));
	}

	private void _testPutSiteResourceFilePortletFileProblemException()
		throws Exception {

		ResourceFile resourceFile = randomResourceFile();

		FileEntry fileEntry = _addPortletFileEntry();

		resourceFile.setExternalReferenceCode(
			fileEntry.getExternalReferenceCode());

		try {
			resourceFileResource.putSiteResourceFile(
				testGroup.getExternalReferenceCode(),
				fileEntry.getExternalReferenceCode(), resourceFile);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}
	}

	private void _testPutSiteResourceFileResourceFolderExternalReferenceCode()
		throws Exception {

		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFile resourceFile = _randomResourceFile(
			fragmentCollection.getExternalReferenceCode());

		ResourceFolder postResourceFolder = _postSiteResourceFolder(
			fragmentCollection.getExternalReferenceCode());

		resourceFile.setResourceFolderExternalReferenceCode(
			postResourceFolder.getExternalReferenceCode());

		ResourceFile putResourceFile = resourceFileResource.putSiteResourceFile(
			testGroup.getExternalReferenceCode(),
			resourceFile.getExternalReferenceCode(), resourceFile);

		ResourceFile getResourceFile = _getSiteResourceFile(
			putResourceFile.getExternalReferenceCode());

		ResourceFolder getResourceFolder = getResourceFile.getResourceFolder();

		Assert.assertEquals(
			postResourceFolder.getExternalReferenceCode(),
			getResourceFolder.getExternalReferenceCode());

		Assert.assertEquals(
			postResourceFolder.getExternalReferenceCode(),
			getResourceFile.getResourceFolderExternalReferenceCode());
	}

	private void _testPutSiteResourceFileResourceFolderPortletFolderProblemException()
		throws Exception {

		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFile resourceFile = _randomResourceFile(
			fragmentCollection.getExternalReferenceCode());

		Folder folder = _addPortletFolder();

		String resourceFolderExternalReferenceCode =
			folder.getExternalReferenceCode();

		resourceFile.setResourceFolderExternalReferenceCode(
			resourceFolderExternalReferenceCode);

		_assertProblemException(
			"no-resource-folder-was-found-with-external-reference-code-x",
			() -> resourceFileResource.putSiteResourceFile(
				testGroup.getExternalReferenceCode(),
				resourceFile.getExternalReferenceCode(), resourceFile),
			resourceFolderExternalReferenceCode);
	}

	private void _testPutSiteResourceFileWithoutPermissionsProblemException()
		throws Exception {

		ResourceFile resourceFile = resourceFileResource.postSiteResourceFile(
			testGroup.getExternalReferenceCode(), randomResourceFile());

		resourceFile.setName(RandomTestUtil.randomString());

		try {
			_userWithoutPermissionsResourceFileResource.putSiteResourceFile(
				testGroup.getExternalReferenceCode(),
				resourceFile.getExternalReferenceCode(), resourceFile);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("FORBIDDEN", problem.getStatus());
		}
	}

	private void _testPutSiteResourceFileWithPermissions() throws Exception {
		ResourceFile resourceFile = resourceFileResource.postSiteResourceFile(
			testGroup.getExternalReferenceCode(), randomResourceFile());

		byte[] bytes = RandomTestUtil.randomBytes();

		resourceFile.setFileURLReference(_toFileURLReference(bytes));

		resourceFile.setName(RandomTestUtil.randomString());

		ResourceFile putResourceFile =
			_userWithPermissionsResourceFileResource.putSiteResourceFile(
				testGroup.getExternalReferenceCode(),
				resourceFile.getExternalReferenceCode(), resourceFile);

		Assert.assertEquals(resourceFile.getName(), putResourceFile.getName());
		assertValid(putResourceFile);

		_assertContent(
			bytes, resourceFile.getExternalReferenceCode(),
			testGroup.getGroupId());
	}

	private FileURLReference _toFileURLReference(byte[] bytes) {
		FileURLReference fileURLReference = new FileURLReference();

		fileURLReference.setFileBase64(Base64.encode(bytes));

		return fileURLReference;
	}

	private FragmentSet _toFragmentSet(String externalReferenceCode) {
		FragmentSet fragmentSet = new FragmentSet();

		fragmentSet.setExternalReferenceCode(externalReferenceCode);

		return fragmentSet;
	}

	private JSONObject _waitForFinish(JSONObject jsonObject) throws Exception {
		while (true) {
			jsonObject = HTTPTestUtil.invokeToJSONObject(
				null,
				StringBundler.concat(
					"headless-batch-engine/v1.0/export-task",
					"/by-external-reference-code/",
					jsonObject.getString("externalReferenceCode")),
				Http.Method.GET);

			String executeStatus = jsonObject.getString("executeStatus");

			if (StringUtil.equals(executeStatus, "COMPLETED") ||
				StringUtil.equals(executeStatus, "FAILED")) {

				Assert.assertEquals("COMPLETED", executeStatus);

				return jsonObject;
			}
		}
	}

	private static String _content1Base64;
	private static byte[] _content1Bytes;
	private static String _content1URL;
	private static byte[] _content2Bytes;
	private static String _content2URL;
	private static HttpServer _httpServer;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	private String _fragmentSetExternalReferenceCode;

	@Inject
	private GroupLocalService _groupLocalService;

	private ResourceFileResource _guestResourceFileResource;

	@Inject
	private Language _language;

	private ResourceFileResource _nestedFieldsResourceFileResource;
	private ResourceFolderResource _resourceFolderResource;

	@Inject
	private UserLocalService _userLocalService;

	private ResourceFileResource _userWithoutPermissionsResourceFileResource;
	private ResourceFileResource _userWithPermissionsResourceFileResource;

}