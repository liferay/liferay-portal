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
import com.liferay.headless.admin.fragment.client.problem.Problem;
import com.liferay.headless.admin.fragment.client.resource.v1_0.ResourceFolderResource;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
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
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.InputStream;

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
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"externalReferenceCode", "name"};
	}

	@Override
	protected ResourceFolder randomResourceFolder() throws Exception {
		return _randomResourceFolder(
			_toFragmentSet(_getFragmentSetExternalReferenceCode()));
	}

	@Override
	protected ResourceFolder
			testPostSiteFragmentSetResourceFolder_addResourceFolder(
				ResourceFolder resourceFolder)
		throws Exception {

		FragmentSet fragmentSet = resourceFolder.getFragmentSet();

		return resourceFolderResource.postSiteFragmentSetResourceFolder(
			testGroup.getExternalReferenceCode(),
			fragmentSet.getExternalReferenceCode(), resourceFolder);
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
			_randomResourceFolder(
				_toFragmentSet(fragmentSetExternalReferenceCode)));
	}

	private ResourceFolder _randomResourceFolder(FragmentSet fragmentSet)
		throws Exception {

		ResourceFolder resourceFolder = super.randomResourceFolder();

		resourceFolder.setFragmentSet(fragmentSet);
		resourceFolder.setParentResourceFolderExternalReferenceCode(
			(String)null);

		return resourceFolder;
	}

	private ResourceFolder _randomResourceFolder(
			ResourceFolder parentResourceFolder)
		throws Exception {

		ResourceFolder resourceFolder = super.randomResourceFolder();

		resourceFolder.setFragmentSet(parentResourceFolder.getFragmentSet());
		resourceFolder.setParentResourceFolderExternalReferenceCode(
			parentResourceFolder.getExternalReferenceCode());

		return resourceFolder;
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
			_toFragmentSet(fragmentCollection.getExternalReferenceCode()));

		ResourceFolder postParentResourceFolder =
			resourceFolderResource.postSiteResourceFolder(
				testGroup.getExternalReferenceCode(), parentResourceFolder);

		ResourceFolder postResourceFolder =
			resourceFolderResource.postSiteResourceFolder(
				testGroup.getExternalReferenceCode(),
				_randomResourceFolder(parentResourceFolder));

		ResourceFolder getResourceFolder =
			resourceFolderResource.getSiteResourceFolder(
				testGroup.getExternalReferenceCode(),
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
			_toFragmentSet(fragmentCollection.getExternalReferenceCode()));

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

		ResourceFolder importedResourceFolder =
			resourceFolderResource.getSiteResourceFolder(
				irrelevantGroup.getExternalReferenceCode(),
				postResourceFolder.getExternalReferenceCode());

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
			_toFragmentSet(fragmentCollection.getExternalReferenceCode()));

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

		ResourceFolder importedResourceFolder =
			resourceFolderResource.getSiteResourceFolder(
				irrelevantGroup.getExternalReferenceCode(),
				postResourceFolder.getExternalReferenceCode());

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
					_toFragmentSet(
						fragmentCollection.getExternalReferenceCode())));

		ResourceFolder resourceFolder = _randomResourceFolder(
			_toFragmentSet(fragmentCollection.getExternalReferenceCode()));

		resourceFolder.setExternalReferenceCode(
			postResourceFolder.getExternalReferenceCode());

		_assertProblemException(
			"CONFLICT", "this-external-reference-code-is-already-in-use",
			() -> resourceFolderResource.postSiteResourceFolder(
				testGroup.getExternalReferenceCode(), resourceFolder));
	}

	private void _testPostSiteResourceFolderFragmentSetExternalReferenceCodeNullProblemException()
		throws Exception {

		ResourceFolder resourceFolder = _randomResourceFolder(
			new FragmentSet());

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
			_toFragmentSet(fragmentSetExternalReferenceCode));

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
			_toFragmentSet(fragmentCollection.getExternalReferenceCode()));

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

		ResourceFolder getResourceFolder =
			resourceFolderResource.getSiteResourceFolder(
				testGroup.getExternalReferenceCode(),
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
			_toFragmentSet(fragmentCollection.getExternalReferenceCode()));

		resourceFolder.setParentResourceFolder(
			_randomResourceFolder(
				_toFragmentSet(fragmentCollection.getExternalReferenceCode())));
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
			_toFragmentSet(fragmentCollection.getExternalReferenceCode()));

		ResourceFolder postParentResourceFolder = _postSiteResourceFolder(
			fragmentCollection.getExternalReferenceCode());

		resourceFolder.setParentResourceFolderExternalReferenceCode(
			postParentResourceFolder.getExternalReferenceCode());

		ResourceFolder postResourceFolder =
			resourceFolderResource.postSiteResourceFolder(
				testGroup.getExternalReferenceCode(), resourceFolder);

		ResourceFolder getResourceFolder =
			resourceFolderResource.getSiteResourceFolder(
				testGroup.getExternalReferenceCode(),
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
			_toFragmentSet(fragmentCollection.getExternalReferenceCode()));

		ResourceFolder postParentResourceFolder = _postSiteResourceFolder(
			fragmentCollection.getExternalReferenceCode());

		resourceFolder.setParentResourceFolder(postParentResourceFolder);

		ResourceFolder postResourceFolder =
			resourceFolderResource.postSiteResourceFolder(
				testGroup.getExternalReferenceCode(), resourceFolder);

		ResourceFolder getResourceFolder =
			resourceFolderResource.getSiteResourceFolder(
				testGroup.getExternalReferenceCode(),
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
			_toFragmentSet(fragmentCollection.getExternalReferenceCode()));

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
			_toFragmentSet(fragmentCollection.getExternalReferenceCode()));

		Folder folder = _addPortletFolder();

		parentResourceFolder.setExternalReferenceCode(
			folder.getExternalReferenceCode());

		ResourceFolder resourceFolder = _randomResourceFolder(
			_toFragmentSet(fragmentCollection.getExternalReferenceCode()));

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
			_toFragmentSet(fragmentCollection.getExternalReferenceCode()));

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