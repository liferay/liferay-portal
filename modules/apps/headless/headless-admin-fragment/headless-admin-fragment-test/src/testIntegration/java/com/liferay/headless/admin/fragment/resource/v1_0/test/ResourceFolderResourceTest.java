/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.test.util.LazyReferencingTestUtil;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.headless.admin.fragment.client.dto.v1_0.FragmentSet;
import com.liferay.headless.admin.fragment.client.dto.v1_0.ResourceFolder;
import com.liferay.headless.admin.fragment.client.pagination.Page;
import com.liferay.headless.admin.fragment.client.pagination.Pagination;
import com.liferay.headless.admin.fragment.client.problem.Problem;
import com.liferay.headless.admin.fragment.client.resource.v1_0.ResourceFolderResource;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
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

import java.io.InputStream;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rubén Pulido
 */
@FeatureFlag("LPD-39244")
@RunWith(Arquillian.class)
public class ResourceFolderResourceTest
	extends BaseResourceFolderResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		String password = RandomTestUtil.randomString();

		User user = UserTestUtil.addUser(testCompany, password);

		_resourceFolderResource = ResourceFolderResource.builder(
		).authentication(
			user.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	@Override
	@Test
	@TestInfo("LPD-88489")
	public void testDeleteSiteResourceFolder() throws Exception {
		super.testDeleteSiteResourceFolder();

		_testDeleteSiteResourceFolderChildResourceFolder();
		_testDeleteSiteResourceFolderPortletFolderProblemException();
		_testDeleteSiteResourceFolderWithoutPermissionsProblemException();
	}

	@Override
	@Test
	@TestInfo("LPD-88489")
	public void testGetSiteFragmentSetResourceFoldersPage() throws Exception {
		super.testGetSiteFragmentSetResourceFoldersPage();

		_testGetSiteFragmentSetResourceFoldersPage();
		_testGetSiteFragmentSetResourceFoldersPageEmpty();
		_testGetSiteFragmentSetResourceFoldersPageFragmentSetNonexistentProblemException();
		_testGetSiteFragmentSetResourceFoldersPageWithoutPermissions();
	}

	@Override
	@Test
	@TestInfo("LPD-88489")
	public void testGetSiteResourceFolder() throws Exception {
		super.testGetSiteResourceFolder();

		_testGetSiteResourceFolderPortletFolderProblemException();
		_testGetSiteResourceFolderResourceFolderNonexistentProblemException();
		_testGetSiteResourceFolderWithoutPermissionsProblemException();
	}

	@Override
	@Test
	@TestInfo("LPD-88489")
	public void testGetSiteResourceFolderResourceFoldersPage()
		throws Exception {

		super.testGetSiteResourceFolderResourceFoldersPage();

		_testGetSiteResourceFolderResourceFoldersPage();
		_testGetSiteResourceFolderResourceFoldersPageEmpty();
		_testGetSiteResourceFolderResourceFoldersPageResourceFolderNonexistentProblemException();
		_testGetSiteResourceFolderResourceFoldersPageWithoutPermissionsProblemException();
	}

	@Override
	@Test
	@TestInfo("LPD-88489")
	public void testGetSiteResourceFoldersPage() throws Exception {
		super.testGetSiteResourceFoldersPage();

		_testGetSiteResourceFoldersPage();
		_testGetSiteResourceFoldersPagePortletFolder();
		_testGetSiteResourceFoldersPageWithoutPermissions();
	}

	@Override
	@Test
	@TestInfo("LPD-88489")
	public void testPostSiteFragmentSetResourceFolder() throws Exception {
		super.testPostSiteFragmentSetResourceFolder();

		_testPostSiteFragmentSetResourceFolderWithoutPermissionsProblemException();
	}

	@Override
	@Test
	@TestInfo("LPD-88489")
	public void testPostSiteResourceFolder() throws Exception {
		super.testPostSiteResourceFolder();

		_testPostSiteResourceFolder();
		_testPostSiteResourceFolderBatch();
		_testPostSiteResourceFolderBatchLazyReferencingParentResourceFolder();
		_testPostSiteResourceFolderDuplicateExternalReferenceCodeProblemException();
		_testPostSiteResourceFolderFragmentSetAndFragmentSetExternalReferenceCode();
		_testPostSiteResourceFolderFragmentSetAndFragmentSetExternalReferenceCodeProblemException();
		_testPostSiteResourceFolderFragmentSetExternalReferenceCode();
		_testPostSiteResourceFolderFragmentSetExternalReferenceCodeNullProblemException();
		_testPostSiteResourceFolderFragmentSetNonexistentProblemException();
		_testPostSiteResourceFolderParentResourceFolderAndParentResourceFolderExternalReferenceCode();
		_testPostSiteResourceFolderParentResourceFolderAndParentResourceFolderExternalReferenceCodeProblemException();
		_testPostSiteResourceFolderParentResourceFolderExternalReferenceCode();
		_testPostSiteResourceFolderParentResourceFolderExternalReferenceCodeNull();
		_testPostSiteResourceFolderParentResourceFolderNonexistentProblemException();
		_testPostSiteResourceFolderParentResourceFolderPortletFolderLazyReferencingProblemException();
		_testPostSiteResourceFolderParentResourceFolderPortletFolderProblemException();
		_testPostSiteResourceFolderWithoutPermissionsProblemException();
	}

	@Override
	@Test
	@TestInfo("LPD-88489")
	public void testPutSiteResourceFolder() throws Exception {
		_testPutSiteResourceFolder();
		_testPutSiteResourceFolderParentResourceFolderExternalReferenceCode();
		_testPutSiteResourceFolderPortletFolderProblemException();
		_testPutSiteResourceFolderWithoutPermissionsProblemException();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"externalReferenceCode", "fragmentSetExternalReferenceCode", "name"
		};
	}

	@Override
	protected ResourceFolder randomResourceFolder() throws Exception {
		return _randomResourceFolder(_getFragmentSetExternalReferenceCode());
	}

	@Override
	protected ResourceFolder
			testGetSiteFragmentSetResourceFoldersPage_addResourceFolder(
				String siteExternalReferenceCode,
				String fragmentSetExternalReferenceCode,
				ResourceFolder resourceFolder)
		throws Exception {

		return resourceFolderResource.postSiteFragmentSetResourceFolder(
			siteExternalReferenceCode, fragmentSetExternalReferenceCode,
			resourceFolder);
	}

	@Override
	protected Map<String, Map<String, String>>
			testGetSiteFragmentSetResourceFoldersPage_getExpectedActions(
				String siteExternalReferenceCode,
				String fragmentSetExternalReferenceCode)
		throws Exception {

		return new HashMap<>();
	}

	@Override
	protected String
			testGetSiteFragmentSetResourceFoldersPage_getFragmentSetExternalReferenceCode()
		throws Exception {

		return _getFragmentSetExternalReferenceCode();
	}

	@Override
	protected ResourceFolder
			testGetSiteResourceFolderResourceFoldersPage_addResourceFolder(
				String siteExternalReferenceCode,
				String resourceFolderExternalReferenceCode,
				ResourceFolder resourceFolder)
		throws Exception {

		resourceFolder.setParentResourceFolderExternalReferenceCode(
			resourceFolderExternalReferenceCode);

		return resourceFolderResource.postSiteResourceFolder(
			siteExternalReferenceCode, resourceFolder);
	}

	@Override
	protected String
			testGetSiteResourceFolderResourceFoldersPage_getResourceFolderExternalReferenceCode()
		throws Exception {

		ResourceFolder resourceFolder =
			resourceFolderResource.postSiteResourceFolder(
				testGroup.getExternalReferenceCode(), randomResourceFolder());

		return resourceFolder.getExternalReferenceCode();
	}

	@Override
	protected ResourceFolder testGetSiteResourceFoldersPage_addResourceFolder(
			String siteExternalReferenceCode, ResourceFolder resourceFolder)
		throws Exception {

		Group group = _groupLocalService.getGroupByExternalReferenceCode(
			siteExternalReferenceCode, testCompany.getCompanyId());

		FragmentCollection fragmentCollection = _addFragmentCollection(
			group.getGroupId());

		resourceFolder.setFragmentSetExternalReferenceCode(
			fragmentCollection.getExternalReferenceCode());

		return resourceFolderResource.postSiteResourceFolder(
			siteExternalReferenceCode, resourceFolder);
	}

	@Override
	protected Map<String, Map<String, String>>
			testGetSiteResourceFoldersPage_getExpectedActions(
				String siteExternalReferenceCode)
		throws Exception {

		return new HashMap<>();
	}

	@Override
	protected ResourceFolder
			testPostSiteFragmentSetResourceFolder_addResourceFolder(
				ResourceFolder resourceFolder)
		throws Exception {

		return resourceFolderResource.postSiteFragmentSetResourceFolder(
			testGroup.getExternalReferenceCode(),
			resourceFolder.getFragmentSetExternalReferenceCode(),
			resourceFolder);
	}

	@Override
	protected ResourceFolder testPostSiteResourceFolder_addResourceFolder(
			ResourceFolder resourceFolder)
		throws Exception {

		return resourceFolderResource.postSiteResourceFolder(
			testGroup.getExternalReferenceCode(), resourceFolder);
	}

	private FragmentCollection _addFragmentCollection(long groupId)
		throws Exception {

		return _fragmentCollectionLocalService.addFragmentCollection(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), groupId,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			false, ServiceContextTestUtil.getServiceContext(groupId));
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

	private void _assertNotContains(
		Folder folder, List<ResourceFolder> resourceFolders) {

		String externalReferenceCode = folder.getExternalReferenceCode();

		for (ResourceFolder resourceFolder : resourceFolders) {
			Assert.assertNotEquals(
				resourceFolders + " contains " + folder, externalReferenceCode,
				resourceFolder.getExternalReferenceCode());
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

	private String _exportResourceFoldersToJSON(
			String siteExternalReferenceCode)
		throws Exception {

		JSONObject exportTaskJSONObject = _waitForFinish(
			"COMPLETED", false,
			HTTPTestUtil.invokeToJSONObject(
				null,
				"headless-admin-fragment/v1.0/sites/" +
					siteExternalReferenceCode +
						"/resource-folders/export-batch?contentType=JSON",
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

	private ResourceFolderResource _getResourceFolderResource(
			String nestedFields)
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
		).parameters(
			"nestedFields", nestedFields
		).build();
	}

	private ResourceFolder _getSiteResourceFolder(String externalReferenceCode)
		throws Exception {

		return _getSiteResourceFolder(
			externalReferenceCode, testGroup.getExternalReferenceCode());
	}

	private ResourceFolder _getSiteResourceFolder(
			String externalReferenceCode, String siteExternalReferenceCode)
		throws Exception {

		ResourceFolderResource resourceFolderResource =
			_getResourceFolderResource("fragmentSet,parentResourceFolder");

		return resourceFolderResource.getSiteResourceFolder(
			siteExternalReferenceCode, externalReferenceCode);
	}

	private ResourceFolder _postSiteResourceFolder(
			ResourceFolder parentResourceFolder)
		throws Exception {

		return resourceFolderResource.postSiteResourceFolder(
			testGroup.getExternalReferenceCode(),
			_randomResourceFolder(parentResourceFolder));
	}

	private ResourceFolder _postSiteResourceFolder(
			String fragmentSetExternalReferenceCode)
		throws Exception {

		return resourceFolderResource.postSiteResourceFolder(
			testGroup.getExternalReferenceCode(),
			_randomResourceFolder(fragmentSetExternalReferenceCode));
	}

	private ResourceFolder _putSiteResourceFolder(
			ResourceFolder resourceFolder,
			String resourceFolderExternalReferenceCode)
		throws Exception {

		ResourceFolderResource resourceFolderResource =
			_getResourceFolderResource("fragmentSet,parentResourceFolder");

		return resourceFolderResource.putSiteResourceFolder(
			testGroup.getExternalReferenceCode(),
			resourceFolderExternalReferenceCode, resourceFolder);
	}

	private ResourceFolder _randomResourceFolder(
			ResourceFolder parentResourceFolder)
		throws Exception {

		ResourceFolder resourceFolder = super.randomResourceFolder();

		resourceFolder.setFragmentSetExternalReferenceCode(
			parentResourceFolder.getFragmentSetExternalReferenceCode());
		resourceFolder.setParentResourceFolderExternalReferenceCode(
			parentResourceFolder.getExternalReferenceCode());

		return resourceFolder;
	}

	private ResourceFolder _randomResourceFolder(
			String fragmentSetExternalReferenceCode)
		throws Exception {

		ResourceFolder resourceFolder = super.randomResourceFolder();

		resourceFolder.setFragmentSetExternalReferenceCode(
			fragmentSetExternalReferenceCode);
		resourceFolder.setParentResourceFolderExternalReferenceCode(
			(String)null);

		return resourceFolder;
	}

	private void _testDeleteSiteResourceFolderChildResourceFolder()
		throws Exception {

		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFolder parentResourceFolder = _postSiteResourceFolder(
			fragmentCollection.getExternalReferenceCode());

		ResourceFolder childResourceFolder = _postSiteResourceFolder(
			parentResourceFolder);

		resourceFolderResource.deleteSiteResourceFolder(
			testGroup.getExternalReferenceCode(),
			parentResourceFolder.getExternalReferenceCode());

		try {
			resourceFolderResource.getSiteResourceFolder(
				testGroup.getExternalReferenceCode(),
				childResourceFolder.getExternalReferenceCode());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}
	}

	private void _testDeleteSiteResourceFolderPortletFolderProblemException()
		throws Exception {

		Folder folder = _addPortletFolder();

		try {
			resourceFolderResource.deleteSiteResourceFolder(
				testGroup.getExternalReferenceCode(),
				folder.getExternalReferenceCode());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}
	}

	private void _testDeleteSiteResourceFolderWithoutPermissionsProblemException()
		throws Exception {

		ResourceFolder resourceFolder =
			resourceFolderResource.postSiteResourceFolder(
				testGroup.getExternalReferenceCode(), randomResourceFolder());

		try {
			_resourceFolderResource.deleteSiteResourceFolder(
				testGroup.getExternalReferenceCode(),
				resourceFolder.getExternalReferenceCode());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("FORBIDDEN", problem.getStatus());
		}
	}

	private void _testGetSiteFragmentSetResourceFoldersPage() throws Exception {
		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFolder resourceFolder = _postSiteResourceFolder(
			fragmentCollection.getExternalReferenceCode());

		_postSiteResourceFolder(resourceFolder);

		Page<ResourceFolder> page =
			resourceFolderResource.getSiteFragmentSetResourceFoldersPage(
				testGroup.getExternalReferenceCode(),
				fragmentCollection.getExternalReferenceCode(),
				Pagination.of(1, 10));

		assertContains(resourceFolder, (List<ResourceFolder>)page.getItems());
		Assert.assertEquals(1, page.getTotalCount());
	}

	private void _testGetSiteFragmentSetResourceFoldersPageEmpty()
		throws Exception {

		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		Page<ResourceFolder> page =
			resourceFolderResource.getSiteFragmentSetResourceFoldersPage(
				testGroup.getExternalReferenceCode(),
				fragmentCollection.getExternalReferenceCode(),
				Pagination.of(1, 10));

		Assert.assertEquals(0, page.getTotalCount());
	}

	private void _testGetSiteFragmentSetResourceFoldersPageFragmentSetNonexistentProblemException()
		throws Exception {

		try {
			resourceFolderResource.getSiteFragmentSetResourceFoldersPage(
				testGroup.getExternalReferenceCode(),
				RandomTestUtil.randomString(), Pagination.of(1, 10));

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}
	}

	private void _testGetSiteFragmentSetResourceFoldersPageWithoutPermissions()
		throws Exception {

		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		resourceFolderResource.postSiteFragmentSetResourceFolder(
			testGroup.getExternalReferenceCode(),
			fragmentCollection.getExternalReferenceCode(),
			_randomResourceFolder(
				fragmentCollection.getExternalReferenceCode()));

		Page<ResourceFolder> page =
			_resourceFolderResource.getSiteFragmentSetResourceFoldersPage(
				testGroup.getExternalReferenceCode(),
				fragmentCollection.getExternalReferenceCode(),
				Pagination.of(1, 10));

		Assert.assertEquals(0, page.getTotalCount());
	}

	private void _testGetSiteResourceFolderPortletFolderProblemException()
		throws Exception {

		Folder folder = _addPortletFolder();

		try {
			resourceFolderResource.getSiteResourceFolder(
				testGroup.getExternalReferenceCode(),
				folder.getExternalReferenceCode());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}
	}

	private void _testGetSiteResourceFolderResourceFolderNonexistentProblemException()
		throws Exception {

		try {
			resourceFolderResource.getSiteResourceFolder(
				testGroup.getExternalReferenceCode(),
				RandomTestUtil.randomString());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}
	}

	private void _testGetSiteResourceFolderResourceFoldersPage()
		throws Exception {

		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFolder parentResourceFolder = _postSiteResourceFolder(
			fragmentCollection.getExternalReferenceCode());

		ResourceFolder childResourceFolder = _postSiteResourceFolder(
			parentResourceFolder);

		_postSiteResourceFolder(childResourceFolder);

		Page<ResourceFolder> page =
			resourceFolderResource.getSiteResourceFolderResourceFoldersPage(
				testGroup.getExternalReferenceCode(),
				parentResourceFolder.getExternalReferenceCode(),
				Pagination.of(1, 10));

		assertContains(
			childResourceFolder, (List<ResourceFolder>)page.getItems());
		Assert.assertEquals(1, page.getTotalCount());
	}

	private void _testGetSiteResourceFolderResourceFoldersPageEmpty()
		throws Exception {

		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFolder resourceFolder = _postSiteResourceFolder(
			fragmentCollection.getExternalReferenceCode());

		Page<ResourceFolder> page =
			resourceFolderResource.getSiteResourceFolderResourceFoldersPage(
				testGroup.getExternalReferenceCode(),
				resourceFolder.getExternalReferenceCode(),
				Pagination.of(1, 10));

		Assert.assertEquals(0, page.getTotalCount());
	}

	private void _testGetSiteResourceFolderResourceFoldersPageResourceFolderNonexistentProblemException()
		throws Exception {

		try {
			resourceFolderResource.getSiteResourceFolderResourceFoldersPage(
				testGroup.getExternalReferenceCode(),
				RandomTestUtil.randomString(), Pagination.of(1, 10));

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}
	}

	private void _testGetSiteResourceFolderResourceFoldersPageWithoutPermissionsProblemException()
		throws Exception {

		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFolder resourceFolder = _postSiteResourceFolder(
			fragmentCollection.getExternalReferenceCode());

		try {
			_resourceFolderResource.getSiteResourceFolderResourceFoldersPage(
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

	private void _testGetSiteResourceFoldersPage() throws Exception {
		FragmentCollection fragmentCollection1 = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFolder resourceFolder1 = _postSiteResourceFolder(
			fragmentCollection1.getExternalReferenceCode());

		ResourceFolder childResourceFolder1 = _postSiteResourceFolder(
			resourceFolder1);

		FragmentCollection fragmentCollection2 = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFolder resourceFolder2 = _postSiteResourceFolder(
			fragmentCollection2.getExternalReferenceCode());

		ResourceFolder childResourceFolder2 = _postSiteResourceFolder(
			resourceFolder2);

		Page<ResourceFolder> page =
			resourceFolderResource.getSiteResourceFoldersPage(
				testGroup.getExternalReferenceCode(), null,
				Pagination.of(1, 1));

		long totalCount = page.getTotalCount();

		page = resourceFolderResource.getSiteResourceFoldersPage(
			testGroup.getExternalReferenceCode(), null,
			Pagination.of(1, (int)totalCount));

		List<ResourceFolder> resourceFolders =
			(List<ResourceFolder>)page.getItems();

		assertContains(childResourceFolder1, resourceFolders);
		assertContains(childResourceFolder2, resourceFolders);
		assertContains(resourceFolder1, resourceFolders);
		assertContains(resourceFolder2, resourceFolders);
	}

	private void _testGetSiteResourceFoldersPagePortletFolder()
		throws Exception {

		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFolder resourceFolder = _postSiteResourceFolder(
			fragmentCollection.getExternalReferenceCode());

		Page<ResourceFolder> page =
			resourceFolderResource.getSiteResourceFoldersPage(
				testGroup.getExternalReferenceCode(), null,
				Pagination.of(1, 1));

		long totalCount = page.getTotalCount();

		page = resourceFolderResource.getSiteResourceFoldersPage(
			testGroup.getExternalReferenceCode(), null,
			Pagination.of(1, (int)totalCount));

		assertContains(resourceFolder, (List<ResourceFolder>)page.getItems());

		Folder folder = _addPortletFolder();

		page = resourceFolderResource.getSiteResourceFoldersPage(
			testGroup.getExternalReferenceCode(), null,
			Pagination.of(1, (int)totalCount + 1));

		Assert.assertEquals(totalCount, page.getTotalCount());

		List<ResourceFolder> resourceFolders =
			(List<ResourceFolder>)page.getItems();

		assertContains(resourceFolder, resourceFolders);
		_assertNotContains(folder, resourceFolders);
	}

	private void _testGetSiteResourceFoldersPageWithoutPermissions()
		throws Exception {

		resourceFolderResource.postSiteResourceFolder(
			testGroup.getExternalReferenceCode(), randomResourceFolder());

		Page<ResourceFolder> page =
			_resourceFolderResource.getSiteResourceFoldersPage(
				testGroup.getExternalReferenceCode(), null,
				Pagination.of(1, 10));

		Assert.assertEquals(0, page.getTotalCount());
	}

	private void _testGetSiteResourceFolderWithoutPermissionsProblemException()
		throws Exception {

		ResourceFolder resourceFolder =
			resourceFolderResource.postSiteResourceFolder(
				testGroup.getExternalReferenceCode(), randomResourceFolder());

		try {
			_resourceFolderResource.getSiteResourceFolder(
				testGroup.getExternalReferenceCode(),
				resourceFolder.getExternalReferenceCode());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}
	}

	private void _testPostSiteFragmentSetResourceFolderWithoutPermissionsProblemException()
		throws Exception {

		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFolder resourceFolder = new ResourceFolder();

		resourceFolder.setExternalReferenceCode(RandomTestUtil.randomString());
		resourceFolder.setName(RandomTestUtil.randomString());

		try {
			_resourceFolderResource.postSiteFragmentSetResourceFolder(
				testGroup.getExternalReferenceCode(),
				fragmentCollection.getExternalReferenceCode(), resourceFolder);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("FORBIDDEN", problem.getStatus());
		}
	}

	private void _testPostSiteResourceFolder() throws Exception {
		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFolder parentResourceFolder = _randomResourceFolder(
			fragmentCollection.getExternalReferenceCode());

		ResourceFolder postParentResourceFolder =
			resourceFolderResource.postSiteResourceFolder(
				testGroup.getExternalReferenceCode(), parentResourceFolder);

		ResourceFolder postResourceFolder =
			resourceFolderResource.postSiteResourceFolder(
				testGroup.getExternalReferenceCode(),
				_randomResourceFolder(parentResourceFolder));

		ResourceFolder getResourceFolder = _getSiteResourceFolder(
			postResourceFolder.getExternalReferenceCode());

		FragmentSet getFragmentSet = getResourceFolder.getFragmentSet();

		Assert.assertEquals(
			fragmentCollection.getExternalReferenceCode(),
			getFragmentSet.getExternalReferenceCode());

		ResourceFolder getParentResourceFolder =
			getResourceFolder.getParentResourceFolder();

		Assert.assertEquals(
			postParentResourceFolder.getExternalReferenceCode(),
			getParentResourceFolder.getExternalReferenceCode());
	}

	private void _testPostSiteResourceFolderBatch() throws Exception {
		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFolder parentResourceFolder = _randomResourceFolder(
			fragmentCollection.getExternalReferenceCode());

		ResourceFolder postParentResourceFolder =
			resourceFolderResource.postSiteResourceFolder(
				testGroup.getExternalReferenceCode(), parentResourceFolder);

		ResourceFolder postResourceFolder =
			resourceFolderResource.postSiteResourceFolder(
				testGroup.getExternalReferenceCode(),
				_randomResourceFolder(parentResourceFolder));

		try (SafeCloseable safeCloseable =
				LazyReferencingTestUtil.setLazyReferencingWithSafeCloseable(
					true)) {

			waitForFinish(
				"COMPLETED",
				HTTPTestUtil.invokeToJSONObject(
					_exportResourceFoldersToJSON(
						testGroup.getExternalReferenceCode()),
					"headless-admin-fragment/v1.0/sites/" +
						irrelevantGroup.getExternalReferenceCode() +
							"/resource-folders/batch?createStrategy=INSERT",
					Http.Method.POST));
		}

		ResourceFolder importedResourceFolder = _getSiteResourceFolder(
			postResourceFolder.getExternalReferenceCode(),
			irrelevantGroup.getExternalReferenceCode());

		FragmentSet importedFragmentSet =
			importedResourceFolder.getFragmentSet();

		Assert.assertEquals(
			fragmentCollection.getExternalReferenceCode(),
			importedFragmentSet.getExternalReferenceCode());

		ResourceFolder importedParentResourceFolder =
			importedResourceFolder.getParentResourceFolder();

		Assert.assertEquals(
			postParentResourceFolder.getExternalReferenceCode(),
			importedParentResourceFolder.getExternalReferenceCode());

		Assert.assertNotNull(
			_fragmentCollectionLocalService.
				fetchFragmentCollectionByExternalReferenceCode(
					fragmentCollection.getExternalReferenceCode(),
					irrelevantGroup.getGroupId()));
	}

	private void _testPostSiteResourceFolderBatchLazyReferencingParentResourceFolder()
		throws Exception {

		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFolder parentResourceFolder = _randomResourceFolder(
			fragmentCollection.getExternalReferenceCode());

		ResourceFolder postParentResourceFolder =
			resourceFolderResource.postSiteResourceFolder(
				testGroup.getExternalReferenceCode(), parentResourceFolder);

		ResourceFolder postResourceFolder =
			resourceFolderResource.postSiteResourceFolder(
				testGroup.getExternalReferenceCode(),
				_randomResourceFolder(parentResourceFolder));

		JSONArray resourceFoldersJSONArray = JSONFactoryUtil.createJSONArray(
			_exportResourceFoldersToJSON(testGroup.getExternalReferenceCode()));

		JSONArray importJSONArray = JSONFactoryUtil.createJSONArray();

		for (int i = 0; i < resourceFoldersJSONArray.length(); i++) {
			JSONObject resourceFolderJSONObject =
				resourceFoldersJSONArray.getJSONObject(i);

			String externalReferenceCode = resourceFolderJSONObject.getString(
				"externalReferenceCode");

			if (externalReferenceCode.equals(
					postResourceFolder.getExternalReferenceCode())) {

				importJSONArray.put(resourceFolderJSONObject);
			}
		}

		try (SafeCloseable safeCloseable =
				LazyReferencingTestUtil.setLazyReferencingWithSafeCloseable(
					true)) {

			waitForFinish(
				"COMPLETED",
				HTTPTestUtil.invokeToJSONObject(
					importJSONArray.toString(),
					"headless-admin-fragment/v1.0/sites/" +
						irrelevantGroup.getExternalReferenceCode() +
							"/resource-folders/batch?createStrategy=INSERT",
					Http.Method.POST));
		}

		ResourceFolder importedParentResourceFolder =
			resourceFolderResource.getSiteResourceFolder(
				irrelevantGroup.getExternalReferenceCode(),
				postParentResourceFolder.getExternalReferenceCode());

		Assert.assertEquals(
			postParentResourceFolder.getExternalReferenceCode(),
			importedParentResourceFolder.getExternalReferenceCode());

		ResourceFolder importedResourceFolder = _getSiteResourceFolder(
			postResourceFolder.getExternalReferenceCode(),
			irrelevantGroup.getExternalReferenceCode());

		ResourceFolder importedResourceFolderParentResourceFolder =
			importedResourceFolder.getParentResourceFolder();

		Assert.assertEquals(
			postParentResourceFolder.getExternalReferenceCode(),
			importedResourceFolderParentResourceFolder.
				getExternalReferenceCode());
	}

	private void _testPostSiteResourceFolderDuplicateExternalReferenceCodeProblemException()
		throws Exception {

		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFolder postResourceFolder =
			resourceFolderResource.postSiteResourceFolder(
				testGroup.getExternalReferenceCode(),
				_randomResourceFolder(
					fragmentCollection.getExternalReferenceCode()));

		ResourceFolder resourceFolder = _randomResourceFolder(
			fragmentCollection.getExternalReferenceCode());

		resourceFolder.setExternalReferenceCode(
			postResourceFolder.getExternalReferenceCode());

		_assertProblemException(
			"CONFLICT", "this-external-reference-code-is-already-in-use",
			() -> resourceFolderResource.postSiteResourceFolder(
				testGroup.getExternalReferenceCode(), resourceFolder));
	}

	private void _testPostSiteResourceFolderFragmentSetAndFragmentSetExternalReferenceCode()
		throws Exception {

		FragmentCollection fragmentCollection1 = _addFragmentCollection(
			testGroup.getGroupId());

		FragmentCollection fragmentCollection2 = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFolder resourceFolder = _randomResourceFolder(
			fragmentCollection2.getExternalReferenceCode());

		resourceFolder.setFragmentSet(
			_toFragmentSet(fragmentCollection1.getExternalReferenceCode()));

		ResourceFolder postResourceFolder =
			resourceFolderResource.postSiteResourceFolder(
				testGroup.getExternalReferenceCode(), resourceFolder);

		ResourceFolder getResourceFolder = _getSiteResourceFolder(
			postResourceFolder.getExternalReferenceCode());

		FragmentSet getFragmentSet = getResourceFolder.getFragmentSet();

		Assert.assertEquals(
			fragmentCollection2.getExternalReferenceCode(),
			getFragmentSet.getExternalReferenceCode());

		Assert.assertEquals(
			fragmentCollection2.getExternalReferenceCode(),
			getResourceFolder.getFragmentSetExternalReferenceCode());
	}

	private void _testPostSiteResourceFolderFragmentSetAndFragmentSetExternalReferenceCodeProblemException()
		throws Exception {

		ResourceFolder resourceFolder = _randomResourceFolder(
			RandomTestUtil.randomString());

		resourceFolder.setFragmentSet(
			_toFragmentSet(RandomTestUtil.randomString()));

		_assertProblemException(
			"the-fragment-set-external-reference-codes-do-not-match",
			() -> {
				try (SafeCloseable safeCloseable =
						LazyReferencingTestUtil.
							setLazyReferencingWithSafeCloseable(true)) {

					resourceFolderResource.postSiteResourceFolder(
						testGroup.getExternalReferenceCode(), resourceFolder);
				}
			});
	}

	private void _testPostSiteResourceFolderFragmentSetExternalReferenceCode()
		throws Exception {

		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFolder resourceFolder = _randomResourceFolder(
			fragmentCollection.getExternalReferenceCode());

		ResourceFolder postResourceFolder =
			resourceFolderResource.postSiteResourceFolder(
				testGroup.getExternalReferenceCode(), resourceFolder);

		ResourceFolder getResourceFolder = _getSiteResourceFolder(
			postResourceFolder.getExternalReferenceCode());

		FragmentSet getFragmentSet = getResourceFolder.getFragmentSet();

		Assert.assertEquals(
			fragmentCollection.getExternalReferenceCode(),
			getFragmentSet.getExternalReferenceCode());

		Assert.assertEquals(
			fragmentCollection.getExternalReferenceCode(),
			getResourceFolder.getFragmentSetExternalReferenceCode());
	}

	private void _testPostSiteResourceFolderFragmentSetExternalReferenceCodeNullProblemException()
		throws Exception {

		ResourceFolder resourceFolder = _randomResourceFolder((String)null);

		_assertProblemException(
			"a-fragment-set-external-reference-code-is-required-to-create-a-" +
				"new-resource-folder",
			() -> resourceFolderResource.postSiteResourceFolder(
				testGroup.getExternalReferenceCode(), resourceFolder));
	}

	private void _testPostSiteResourceFolderFragmentSetNonexistentProblemException()
		throws Exception {

		String fragmentSetExternalReferenceCode = RandomTestUtil.randomString();

		ResourceFolder resourceFolder = _randomResourceFolder(
			fragmentSetExternalReferenceCode);

		_assertProblemException(
			"no-fragment-set-was-found-with-external-reference-code-x",
			() -> resourceFolderResource.postSiteResourceFolder(
				testGroup.getExternalReferenceCode(), resourceFolder),
			fragmentSetExternalReferenceCode);
	}

	private void _testPostSiteResourceFolderParentResourceFolderAndParentResourceFolderExternalReferenceCode()
		throws Exception {

		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFolder resourceFolder = _randomResourceFolder(
			fragmentCollection.getExternalReferenceCode());

		ResourceFolder postParentResourceFolder1 = _postSiteResourceFolder(
			fragmentCollection.getExternalReferenceCode());
		ResourceFolder postParentResourceFolder2 = _postSiteResourceFolder(
			fragmentCollection.getExternalReferenceCode());

		resourceFolder.setParentResourceFolder(postParentResourceFolder1);
		resourceFolder.setParentResourceFolderExternalReferenceCode(
			postParentResourceFolder2.getExternalReferenceCode());

		ResourceFolder postResourceFolder =
			resourceFolderResource.postSiteResourceFolder(
				testGroup.getExternalReferenceCode(), resourceFolder);

		ResourceFolder getResourceFolder = _getSiteResourceFolder(
			postResourceFolder.getExternalReferenceCode());

		ResourceFolder getParentResourceFolder =
			getResourceFolder.getParentResourceFolder();

		Assert.assertEquals(
			postParentResourceFolder2.getExternalReferenceCode(),
			getParentResourceFolder.getExternalReferenceCode());
	}

	private void _testPostSiteResourceFolderParentResourceFolderAndParentResourceFolderExternalReferenceCodeProblemException()
		throws Exception {

		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFolder resourceFolder = _randomResourceFolder(
			fragmentCollection.getExternalReferenceCode());

		resourceFolder.setParentResourceFolder(
			_randomResourceFolder(
				fragmentCollection.getExternalReferenceCode()));
		resourceFolder.setParentResourceFolderExternalReferenceCode(
			RandomTestUtil.randomString());

		_assertProblemException(
			"the-parent-resource-folder-external-reference-codes-do-not-match",
			() -> {
				try (SafeCloseable safeCloseable =
						LazyReferencingTestUtil.
							setLazyReferencingWithSafeCloseable(true)) {

					resourceFolderResource.postSiteResourceFolder(
						testGroup.getExternalReferenceCode(), resourceFolder);
				}
			});
	}

	private void _testPostSiteResourceFolderParentResourceFolderExternalReferenceCode()
		throws Exception {

		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFolder resourceFolder = _randomResourceFolder(
			fragmentCollection.getExternalReferenceCode());

		ResourceFolder postParentResourceFolder = _postSiteResourceFolder(
			fragmentCollection.getExternalReferenceCode());

		resourceFolder.setParentResourceFolderExternalReferenceCode(
			postParentResourceFolder.getExternalReferenceCode());

		ResourceFolder postResourceFolder =
			resourceFolderResource.postSiteResourceFolder(
				testGroup.getExternalReferenceCode(), resourceFolder);

		ResourceFolder getResourceFolder = _getSiteResourceFolder(
			postResourceFolder.getExternalReferenceCode());

		Assert.assertEquals(
			postParentResourceFolder.getExternalReferenceCode(),
			getResourceFolder.getParentResourceFolderExternalReferenceCode());

		ResourceFolder getParentResourceFolder =
			getResourceFolder.getParentResourceFolder();

		Assert.assertEquals(
			postParentResourceFolder.getExternalReferenceCode(),
			getParentResourceFolder.getExternalReferenceCode());
	}

	private void _testPostSiteResourceFolderParentResourceFolderExternalReferenceCodeNull()
		throws Exception {

		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFolder resourceFolder = _randomResourceFolder(
			fragmentCollection.getExternalReferenceCode());

		ResourceFolder postParentResourceFolder = _postSiteResourceFolder(
			fragmentCollection.getExternalReferenceCode());

		resourceFolder.setParentResourceFolder(postParentResourceFolder);

		ResourceFolder postResourceFolder =
			resourceFolderResource.postSiteResourceFolder(
				testGroup.getExternalReferenceCode(), resourceFolder);

		ResourceFolder getResourceFolder = _getSiteResourceFolder(
			postResourceFolder.getExternalReferenceCode());

		Assert.assertNull(getResourceFolder.getParentResourceFolder());
		Assert.assertNull(
			getResourceFolder.getParentResourceFolderExternalReferenceCode());
	}

	private void _testPostSiteResourceFolderParentResourceFolderNonexistentProblemException()
		throws Exception {

		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		String parentResourceFolderExternalReferenceCode =
			RandomTestUtil.randomString();

		ResourceFolder resourceFolder = _randomResourceFolder(
			fragmentCollection.getExternalReferenceCode());

		resourceFolder.setParentResourceFolderExternalReferenceCode(
			parentResourceFolderExternalReferenceCode);

		_assertProblemException(
			"no-resource-folder-was-found-with-external-reference-code-x",
			() -> resourceFolderResource.postSiteResourceFolder(
				testGroup.getExternalReferenceCode(), resourceFolder),
			parentResourceFolderExternalReferenceCode);
	}

	private void _testPostSiteResourceFolderParentResourceFolderPortletFolderLazyReferencingProblemException()
		throws Exception {

		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFolder parentResourceFolder = _randomResourceFolder(
			fragmentCollection.getExternalReferenceCode());

		Folder folder = _addPortletFolder();

		parentResourceFolder.setExternalReferenceCode(
			folder.getExternalReferenceCode());

		ResourceFolder resourceFolder = _randomResourceFolder(
			fragmentCollection.getExternalReferenceCode());

		resourceFolder.setParentResourceFolder(parentResourceFolder);
		resourceFolder.setParentResourceFolderExternalReferenceCode(
			folder.getExternalReferenceCode());

		_assertProblemException(
			"no-resource-folder-was-found-with-external-reference-code-x",
			() -> {
				try (SafeCloseable safeCloseable =
						LazyReferencingTestUtil.
							setLazyReferencingWithSafeCloseable(true)) {

					resourceFolderResource.postSiteResourceFolder(
						testGroup.getExternalReferenceCode(), resourceFolder);
				}
			},
			folder.getExternalReferenceCode());
	}

	private void _testPostSiteResourceFolderParentResourceFolderPortletFolderProblemException()
		throws Exception {

		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		Folder folder = _addPortletFolder();

		String parentResourceFolderExternalReferenceCode =
			folder.getExternalReferenceCode();

		ResourceFolder resourceFolder = _randomResourceFolder(
			fragmentCollection.getExternalReferenceCode());

		resourceFolder.setParentResourceFolderExternalReferenceCode(
			parentResourceFolderExternalReferenceCode);

		_assertProblemException(
			"no-resource-folder-was-found-with-external-reference-code-x",
			() -> resourceFolderResource.postSiteResourceFolder(
				testGroup.getExternalReferenceCode(), resourceFolder),
			parentResourceFolderExternalReferenceCode);
	}

	private void _testPostSiteResourceFolderWithoutPermissionsProblemException()
		throws Exception {

		try {
			_resourceFolderResource.postSiteResourceFolder(
				testGroup.getExternalReferenceCode(), randomResourceFolder());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("FORBIDDEN", problem.getStatus());
		}
	}

	private void _testPutSiteResourceFolder() throws Exception {
		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFolder originalResourceFolder = _randomResourceFolder(
			fragmentCollection.getExternalReferenceCode());

		ResourceFolder putResourceFolder =
			resourceFolderResource.putSiteResourceFolder(
				testGroup.getExternalReferenceCode(),
				originalResourceFolder.getExternalReferenceCode(),
				originalResourceFolder);

		assertEquals(originalResourceFolder, putResourceFolder);
		assertValid(putResourceFolder);

		FragmentCollection irrelevantFragmentCollection =
			_addFragmentCollection(testGroup.getGroupId());

		ResourceFolder updatedResourceFolder = _randomResourceFolder(
			_postSiteResourceFolder(
				irrelevantFragmentCollection.getExternalReferenceCode()));

		Assert.assertNotNull(
			updatedResourceFolder.
				getParentResourceFolderExternalReferenceCode());

		putResourceFolder = _putSiteResourceFolder(
			updatedResourceFolder,
			originalResourceFolder.getExternalReferenceCode());

		Assert.assertEquals(
			originalResourceFolder.getExternalReferenceCode(),
			putResourceFolder.getExternalReferenceCode());
		Assert.assertEquals(
			updatedResourceFolder.getName(), putResourceFolder.getName());
		Assert.assertNull(putResourceFolder.getParentResourceFolder());
		Assert.assertNull(
			putResourceFolder.getParentResourceFolderExternalReferenceCode());

		FragmentSet fragmentSet = putResourceFolder.getFragmentSet();

		Assert.assertEquals(
			fragmentCollection.getExternalReferenceCode(),
			fragmentSet.getExternalReferenceCode());

		ResourceFolder getResourceFolder = _getSiteResourceFolder(
			originalResourceFolder.getExternalReferenceCode());

		Assert.assertEquals(
			originalResourceFolder.getExternalReferenceCode(),
			getResourceFolder.getExternalReferenceCode());
		Assert.assertEquals(
			updatedResourceFolder.getName(), getResourceFolder.getName());
		Assert.assertNull(getResourceFolder.getParentResourceFolder());
		Assert.assertNull(
			getResourceFolder.getParentResourceFolderExternalReferenceCode());

		ResourceFolder parentResourceFolder = _postSiteResourceFolder(
			fragmentCollection.getExternalReferenceCode());

		ResourceFolder childResourceFolder = _postSiteResourceFolder(
			parentResourceFolder);

		putResourceFolder = _putSiteResourceFolder(
			_randomResourceFolder(
				_postSiteResourceFolder(
					irrelevantFragmentCollection.getExternalReferenceCode())),
			childResourceFolder.getExternalReferenceCode());

		ResourceFolder putParentResourceFolder =
			putResourceFolder.getParentResourceFolder();

		Assert.assertEquals(
			parentResourceFolder.getExternalReferenceCode(),
			putParentResourceFolder.getExternalReferenceCode());

		Assert.assertEquals(
			parentResourceFolder.getExternalReferenceCode(),
			putResourceFolder.getParentResourceFolderExternalReferenceCode());
	}

	private void _testPutSiteResourceFolderParentResourceFolderExternalReferenceCode()
		throws Exception {

		FragmentCollection fragmentCollection = _addFragmentCollection(
			testGroup.getGroupId());

		ResourceFolder resourceFolder = _randomResourceFolder(
			fragmentCollection.getExternalReferenceCode());

		ResourceFolder postParentResourceFolder = _postSiteResourceFolder(
			fragmentCollection.getExternalReferenceCode());

		resourceFolder.setParentResourceFolderExternalReferenceCode(
			postParentResourceFolder.getExternalReferenceCode());

		ResourceFolder putResourceFolder =
			resourceFolderResource.putSiteResourceFolder(
				testGroup.getExternalReferenceCode(),
				resourceFolder.getExternalReferenceCode(), resourceFolder);

		ResourceFolder getResourceFolder = _getSiteResourceFolder(
			putResourceFolder.getExternalReferenceCode());

		Assert.assertEquals(
			postParentResourceFolder.getExternalReferenceCode(),
			getResourceFolder.getParentResourceFolderExternalReferenceCode());

		ResourceFolder getParentResourceFolder =
			getResourceFolder.getParentResourceFolder();

		Assert.assertEquals(
			postParentResourceFolder.getExternalReferenceCode(),
			getParentResourceFolder.getExternalReferenceCode());
	}

	private void _testPutSiteResourceFolderPortletFolderProblemException()
		throws Exception {

		ResourceFolder resourceFolder = randomResourceFolder();

		Folder folder = _addPortletFolder();

		resourceFolder.setExternalReferenceCode(
			folder.getExternalReferenceCode());

		try {
			resourceFolderResource.putSiteResourceFolder(
				testGroup.getExternalReferenceCode(),
				folder.getExternalReferenceCode(), resourceFolder);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}
	}

	private void _testPutSiteResourceFolderWithoutPermissionsProblemException()
		throws Exception {

		ResourceFolder resourceFolder =
			resourceFolderResource.postSiteResourceFolder(
				testGroup.getExternalReferenceCode(), randomResourceFolder());

		resourceFolder.setName(RandomTestUtil.randomString());

		try {
			_resourceFolderResource.putSiteResourceFolder(
				testGroup.getExternalReferenceCode(),
				resourceFolder.getExternalReferenceCode(), resourceFolder);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("FORBIDDEN", problem.getStatus());
		}
	}

	private FragmentSet _toFragmentSet(String externalReferenceCode) {
		FragmentSet fragmentSet = new FragmentSet();

		fragmentSet.setExternalReferenceCode(externalReferenceCode);

		return fragmentSet;
	}

	private JSONObject _waitForFinish(
			String expectedExecuteStatus, boolean importTask,
			JSONObject jsonObject)
		throws Exception {

		String endpoint = StringBundler.concat(
			"headless-batch-engine/v1.0/",
			importTask ? "import-task" : "export-task",
			"/by-external-reference-code/");

		while (true) {
			jsonObject = HTTPTestUtil.invokeToJSONObject(
				null, endpoint + jsonObject.getString("externalReferenceCode"),
				Http.Method.GET);

			String executeStatus = jsonObject.getString("executeStatus");

			if (StringUtil.equals(executeStatus, "COMPLETED") ||
				StringUtil.equals(executeStatus, "FAILED")) {

				Assert.assertEquals(expectedExecuteStatus, executeStatus);

				return jsonObject;
			}
		}
	}

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	private String _fragmentSetExternalReferenceCode;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private Language _language;

	private ResourceFolderResource _resourceFolderResource;

}