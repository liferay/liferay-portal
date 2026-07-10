/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.test.util.LazyReferencingTestUtil;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryVersion;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.headless.admin.fragment.client.constant.v1_0.FieldType;
import com.liferay.headless.admin.fragment.client.dto.v1_0.BasicFragment;
import com.liferay.headless.admin.fragment.client.dto.v1_0.Creator;
import com.liferay.headless.admin.fragment.client.dto.v1_0.FormFragment;
import com.liferay.headless.admin.fragment.client.dto.v1_0.Fragment;
import com.liferay.headless.admin.fragment.client.dto.v1_0.FragmentSet;
import com.liferay.headless.admin.fragment.client.dto.v1_0.FragmentVersion;
import com.liferay.headless.admin.fragment.client.dto.v1_0.ThumbnailURLReference;
import com.liferay.headless.admin.fragment.client.pagination.Page;
import com.liferay.headless.admin.fragment.client.pagination.Pagination;
import com.liferay.headless.admin.fragment.client.problem.Problem;
import com.liferay.headless.admin.fragment.client.resource.v1_0.FragmentResource;
import com.liferay.headless.batch.engine.client.http.HttpInvoker;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.search.test.util.IdempotentRetryAssert;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
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
public class FragmentResourceTest extends BaseFragmentResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseFragmentResourceTestCase.setUpClass();

		_httpServer = HttpServer.create(
			new InetSocketAddress("127.0.0.1", 0), 0);

		_thumbnail1Bytes = _getBytes("thumbnail_1.png");
		_thumbnail2Bytes = _getBytes("thumbnail_2.png");

		_httpServer.createContext(
			"/thumbnail_1.png",
			httpExchange -> _writeBytes(httpExchange, _thumbnail1Bytes));
		_httpServer.createContext(
			"/thumbnail_2.png",
			httpExchange -> _writeBytes(httpExchange, _thumbnail2Bytes));

		_httpServer.start();

		_thumbnail1Base64 = Base64.encode(_thumbnail1Bytes);
		_thumbnail2Base64 = Base64.encode(_thumbnail2Bytes);

		InetSocketAddress inetSocketAddress = _httpServer.getAddress();

		String baseURL = "http://127.0.0.1:" + inetSocketAddress.getPort();

		_thumbnail1URL = baseURL + "/thumbnail_1.png";
		_thumbnail2URL = baseURL + "/thumbnail_2.png";
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

		_fragmentCollection = _addFragmentCollection();
	}

	@Override
	@Test
	@TestInfo("LPD-95281")
	public void testBatchEngineDeleteImportTask() throws Exception {
		super.testBatchEngineDeleteImportTask();

		_testBatchEngineDeleteImportTask();
	}

	@Override
	@Test
	@TestInfo("LPD-95281")
	public void testDeleteSiteFragment() throws Exception {
		super.testDeleteSiteFragment();

		_testDeleteSiteFragment(false, true);
		_testDeleteSiteFragment(true, false);
		_testDeleteSiteFragment(true, true);
		_testDeleteSiteFragmentNonexistent();
		_testDeleteSiteFragmentWithFormFragment();
	}

	@Override
	@Test
	@TestInfo({"LPD-88489", "LPD-95281"})
	public void testGetSiteFragment() throws Exception {
		super.testGetSiteFragment();

		_testGetSiteFragmentApproved();
		_testGetSiteFragmentApprovedAndDraft();
		_testGetSiteFragmentDraft();
		_testGetSiteFragmentThumbnailURLReference();
		_testGetSiteFragmentWithFormFragment();
	}

	@Override
	@Test
	public void testGetSiteFragmentSetFragmentsPage() throws Exception {
		super.testGetSiteFragmentSetFragmentsPage();

		_testGetSiteFragmentSetFragmentsPageWithNonexistentFragmentSet();
		_testGetSiteFragmentSetFragmentsPageWithStatus();
	}

	@Override
	@Test
	public void testGetSiteFragmentsPage() throws Exception {
		super.testGetSiteFragmentsPage();

		_testGetSiteFragmentsPageWithFilter();
		_testGetSiteFragmentsPageWithFragmentSets();
	}

	@Override
	@Test
	@TestInfo({"LPD-88489", "LPD-95281"})
	public void testPostSiteFragment() throws Exception {
		super.testPostSiteFragment();

		_testPostSiteFragmentApproved();
		_testPostSiteFragmentApprovedAndDraft();
		_testPostSiteFragmentBatch();
		_testPostSiteFragmentDraft();
		_testPostSiteFragmentDuplicateExternalReferenceCodeProblemException();
		_testPostSiteFragmentDuplicateKeyProblemException();
		_testPostSiteFragmentEmpty();
		_testPostSiteFragmentFragmentSetAndFragmentSetExternalReferenceCode();
		_testPostSiteFragmentFragmentSetAndFragmentSetExternalReferenceCodeProblemException();
		_testPostSiteFragmentFragmentSetExisting();
		_testPostSiteFragmentFragmentSetExternalReferenceCodeNullProblemException();
		_testPostSiteFragmentFragmentSetNonexisting();
		_testPostSiteFragmentFragmentSetNonexistingProblemException();
		_testPostSiteFragmentFragmentSetNullProblemException();
		_testPostSiteFragmentMarketplace();
		_testPostSiteFragmentThumbnailURLReferenceExternalReferenceCode();
		_testPostSiteFragmentThumbnailURLReferenceExternalReferenceCodeAndFileBase64();
		_testPostSiteFragmentThumbnailURLReferenceExternalReferenceCodeEmptyAndFileBase64();
		_testPostSiteFragmentThumbnailURLReferenceExternalReferenceCodeNullAndFileBase64();
		_testPostSiteFragmentThumbnailURLReferenceExternalReferenceCodeNullAndURL();
		_testPostSiteFragmentThumbnailURLReferenceFileBase64();
		_testPostSiteFragmentThumbnailURLReferenceFileBase64AndURL();
		_testPostSiteFragmentThumbnailURLReferenceNonexistingProblemException();
		_testPostSiteFragmentThumbnailURLReferenceURL();
		_testPostSiteFragmentThumbnailURLReferenceURLUnreachableProblemException();
		_testPostSiteFragmentThumbnailURLReferenceURLUnsupportedProtocolProblemException();
		_testPostSiteFragmentWithFormFragment();
	}

	@Override
	@Test
	@TestInfo("LPD-95281")
	public void testPostSiteFragmentSetFragment() throws Exception {
		super.testPostSiteFragmentSetFragment();

		_testPostSiteFragmentSetFragmentApproved();
		_testPostSiteFragmentSetFragmentApprovedAndDraft();
		_testPostSiteFragmentSetFragmentDraft();
		_testPostSiteFragmentSetFragmentDuplicateExternalReferenceCodeProblemException();
		_testPostSiteFragmentSetFragmentDuplicateKeyProblemException();
		_testPostSiteFragmentSetFragmentEmpty();
		_testPostSiteFragmentSetFragmentFragmentSetExisting();
		_testPostSiteFragmentSetFragmentFragmentSetExternalReferenceCodeNull();
		_testPostSiteFragmentSetFragmentFragmentSetInPathNonexistingProblemException();
		_testPostSiteFragmentSetFragmentFragmentSetNonexisting();
		_testPostSiteFragmentSetFragmentFragmentSetNull();
		_testPostSiteFragmentSetFragmentWithFormFragment();
	}

	@Override
	@Test
	@TestInfo({"LPD-88489", "LPD-95281"})
	public void testPutSiteFragment() throws Exception {
		_testPutSiteFragmentBatch();
		_testPutSiteFragmentCreateApproved();
		_testPutSiteFragmentCreateApprovedAndDraft();
		_testPutSiteFragmentCreateDraft();
		_testPutSiteFragmentCreateEmpty();
		_testPutSiteFragmentCreateFragmentSetExisting();
		_testPutSiteFragmentCreateFragmentSetExternalReferenceCodeNullProblemException();
		_testPutSiteFragmentCreateFragmentSetNonexisting();
		_testPutSiteFragmentCreateFragmentSetNonexistingProblemException();
		_testPutSiteFragmentCreateFragmentSetNullProblemException();
		_testPutSiteFragmentUpdateApprovedAddDraft();
		_testPutSiteFragmentUpdateApprovedAddDraftModifyApproved();
		_testPutSiteFragmentUpdateApprovedAndDraftToDraftProblemException();
		_testPutSiteFragmentUpdateApprovedAndDraftToEmptyProblemException();
		_testPutSiteFragmentUpdateApprovedModifyApproved();
		_testPutSiteFragmentUpdateApprovedModifyApprovedAndDraft();
		_testPutSiteFragmentUpdateApprovedToDraftProblemException();
		_testPutSiteFragmentUpdateApprovedToEmpty();
		_testPutSiteFragmentUpdateDraft();
		_testPutSiteFragmentUpdateDraftToApproved();
		_testPutSiteFragmentUpdateDraftToEmptyProblemException();
		_testPutSiteFragmentUpdateFragmentSetExisting();
		_testPutSiteFragmentUpdateFragmentSetExternalReferenceCodeNull();
		_testPutSiteFragmentUpdateFragmentSetNonexisting();
		_testPutSiteFragmentUpdateFragmentSetNonexistingProblemException();
		_testPutSiteFragmentUpdateFragmentSetNull();
		_testPutSiteFragmentUpdateThumbnailURLReferenceExternalReferenceCode();
		_testPutSiteFragmentUpdateThumbnailURLReferenceExternalReferenceCodeAndFileBase64();
		_testPutSiteFragmentUpdateThumbnailURLReferenceFileBase64();
		_testPutSiteFragmentUpdateThumbnailURLReferenceNull();
		_testPutSiteFragmentUpdateThumbnailURLReferenceURL();
		_testPutSiteFragmentUpdateTypeProblemException();
		_testPutSiteFragmentWithFormFragment();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"cacheable", "externalReferenceCode",
			"fragmentSetExternalReferenceCode", "fragmentVersions", "key",
			"marketplace", "name", "readOnly"
		};
	}

	@Override
	protected Fragment randomFragment() throws Exception {
		Fragment fragment = _randomBasicFragment();

		fragment.setFragmentSet(_toFragmentSet(_fragmentCollection));
		fragment.setFragmentSetExternalReferenceCode(
			_fragmentCollection.getExternalReferenceCode());
		fragment.setFragmentVersions(_randomFragmentVersions());

		return fragment;
	}

	@Override
	protected void testBatchEngineDeleteImportTask_deleteFragment(
			int expectedStatusCode, String externalReferenceCode,
			String... parameters)
		throws Exception {

		_testBatchEngineDeleteImportTask_deleteFragments(
			expectedStatusCode,
			new JSONObject[] {
				JSONUtil.put(
					"externalReferenceCode", () -> externalReferenceCode
				).put(
					"type", "BasicFragment"
				)
			},
			parameters);
	}

	@Override
	protected Fragment testDeleteSiteFragment_addFragment() throws Exception {
		return _postSiteFragmentSetFragment(randomFragment());
	}

	@Override
	protected Fragment testGetSiteFragment_addFragment() throws Exception {
		return _postSiteFragmentSetFragment(randomFragment());
	}

	@Override
	protected Fragment testGetSiteFragmentSetFragmentsPage_addFragment(
			String siteExternalReferenceCode,
			String fragmentSetExternalReferenceCode, Fragment fragment)
		throws Exception {

		return _postSiteFragmentSetFragment(fragment);
	}

	@Override
	protected String
			testGetSiteFragmentSetFragmentsPage_getFragmentSetExternalReferenceCode()
		throws Exception {

		return _fragmentCollection.getExternalReferenceCode();
	}

	@Override
	protected Fragment testGetSiteFragmentsPage_addFragment(
			String siteExternalReferenceCode, Fragment fragment)
		throws Exception {

		try (SafeCloseable safeCloseable =
				LazyReferencingTestUtil.setLazyReferencingWithSafeCloseable(
					true)) {

			return fragmentResource.postSiteFragment(
				siteExternalReferenceCode, fragment);
		}
	}

	@Override
	protected Map<String, Map<String, String>>
		testGetSiteFragmentsPage_getExpectedActions(
			String siteExternalReferenceCode) {

		return new HashMap<>();
	}

	@Override
	protected Fragment testPostSiteFragment_addFragment(Fragment fragment)
		throws Exception {

		_populateFragment(fragment);

		return fragmentResource.postSiteFragment(
			testGroup.getExternalReferenceCode(), fragment);
	}

	@Override
	protected Fragment testPostSiteFragmentSetFragment_addFragment(
			Fragment fragment)
		throws Exception {

		_populateFragment(fragment);

		return _postSiteFragmentSetFragment(fragment);
	}

	@Override
	protected Fragment testPutSiteFragment_addFragment() throws Exception {
		return _postSiteFragmentSetFragment(randomFragment());
	}

	private static byte[] _getBytes(String fileName) throws Exception {
		try (InputStream inputStream =
				FragmentResourceTest.class.getResourceAsStream(
					"dependencies/" + fileName)) {

			return StreamUtil.toByteArray(inputStream);
		}
	}

	private static void _writeBytes(HttpExchange httpExchange, byte[] bytes)
		throws IOException {

		Headers responseHeaders = httpExchange.getResponseHeaders();

		responseHeaders.set("Content-Type", ContentTypes.IMAGE_PNG);

		httpExchange.sendResponseHeaders(200, bytes.length);

		try (OutputStream outputStream = httpExchange.getResponseBody()) {
			outputStream.write(bytes);
		}
	}

	private FragmentCollection _addFragmentCollection() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(testGroup.getGroupId());

		return _fragmentCollectionLocalService.addFragmentCollection(
			null, serviceContext.getUserId(), testGroup.getGroupId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);
	}

	private FileEntry _addPortletFileEntry(String fileName) throws Exception {
		Repository repository = _portletFileRepository.addPortletRepository(
			testGroup.getGroupId(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				testGroup, TestPropsValues.getUserId()));

		Class<?> clazz = getClass();

		return _portletFileRepository.addPortletFileEntry(
			null, testGroup.getGroupId(), TestPropsValues.getUserId(),
			FragmentEntry.class.getName(), RandomTestUtil.randomLong(),
			RandomTestUtil.randomString(), repository.getDlFolderId(),
			clazz.getResourceAsStream("dependencies/" + fileName),
			RandomTestUtil.randomString() + ".png", ContentTypes.IMAGE_PNG,
			false);
	}

	private void _assertExportImportFragments(
			List<Fragment> expectedFragments, String filterString,
			List<Fragment> notExpectedFragments)
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		try (SafeCloseable safeCloseable =
				LazyReferencingTestUtil.setLazyReferencingWithSafeCloseable(
					true)) {

			waitForFinish(
				"COMPLETED",
				HTTPTestUtil.invokeToJSONObject(
					_exportFragmentsToJSON(
						filterString, testGroup.getExternalReferenceCode()),
					"headless-admin-fragment/v1.0/sites/" +
						group.getExternalReferenceCode() +
							"/fragments/batch?createStrategy=INSERT",
					Http.Method.POST));
		}

		for (Fragment fragment : expectedFragments) {
			_assertFragmentEntry(fragment, group);
		}

		for (Fragment fragment : notExpectedFragments) {
			_assertNullFragmentEntry(fragment, group);
		}
	}

	private void _assertExportImportFragmentsFails(String filterString)
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.batch.engine.internal." +
					"BatchEngineImportTaskExecutorImpl",
				LoggerTestUtil.ERROR);
			SafeCloseable safeCloseable =
				LazyReferencingTestUtil.setLazyReferencingWithSafeCloseable(
					true)) {

			waitForFinish(
				"FAILED",
				HTTPTestUtil.invokeToJSONObject(
					_exportFragmentsToJSON(
						filterString, testGroup.getExternalReferenceCode()),
					"headless-admin-fragment/v1.0/sites/" +
						group.getExternalReferenceCode() +
							"/fragments/batch?createStrategy=INSERT",
					Http.Method.POST));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertFalse(logEntries.toString(), logEntries.isEmpty());

			LogEntry logEntry = logEntries.get(0);

			Throwable throwable = logEntry.getThrowable();

			Assert.assertEquals(
				_language.get(
					LocaleUtil.getDefault(), "html-content-must-not-be-empty"),
				throwable.getMessage());
		}
	}

	private void _assertFormFragment(
		FieldType[] expectedFieldTypes, Fragment fragment) {

		FormFragment formFragment = (FormFragment)fragment;

		Assert.assertArrayEquals(
			expectedFieldTypes, formFragment.getFieldTypes());

		Assert.assertEquals(Fragment.Type.FORM_FRAGMENT, fragment.getType());
	}

	private void _assertFormFragmentEntry(
			FieldType[] expectedFieldTypes, Fragment fragment, Group group)
		throws Exception {

		_assertFormFragment(
			expectedFieldTypes,
			fragmentResource.getSiteFragment(
				group.getExternalReferenceCode(),
				fragment.getExternalReferenceCode()));
		_assertFragmentEntry(fragment, group);
	}

	private void _assertFragmentEntry(Fragment fragment, Group group)
		throws Exception {

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.getFragmentEntryByExternalReferenceCode(
				fragment.getExternalReferenceCode(), group.getGroupId());

		Assert.assertEquals(fragment.getName(), fragmentEntry.getName());
	}

	private void _assertGetSiteFragmentsPageWithFilter(
			Fragment expectedFragment, String filterString,
			Fragment notExpectedFragment)
		throws Exception {

		Page<Fragment> page = fragmentResource.getSiteFragmentsPage(
			testGroup.getExternalReferenceCode(), filterString, null);

		List<Fragment> fragments = (List<Fragment>)page.getItems();

		assertContains(expectedFragment, fragments);
		_assertNotContains(notExpectedFragment, fragments);
	}

	private void _assertNotContains(
		Fragment fragment, List<Fragment> fragments) {

		boolean contains = false;

		for (Fragment curFragment : fragments) {
			if (equals(fragment, curFragment)) {
				contains = true;

				break;
			}
		}

		Assert.assertFalse(fragments + " contains " + fragment, contains);
	}

	private void _assertNullFragmentEntry(Fragment fragment, Group group) {
		Assert.assertNull(
			_fragmentEntryLocalService.
				fetchFragmentEntryByExternalReferenceCode(
					fragment.getExternalReferenceCode(), group.getGroupId()));
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

	private void _assertThumbnailURLReference(
			byte[] expectedBytes, String expectedExternalReferenceCode,
			Fragment fragment)
		throws Exception {

		Assert.assertNotNull(expectedExternalReferenceCode);

		ThumbnailURLReference thumbnailURLReference =
			fragment.getThumbnailURLReference();

		Assert.assertEquals(
			expectedExternalReferenceCode,
			thumbnailURLReference.getExternalReferenceCode());

		FileEntry fileEntry =
			PortletFileRepositoryUtil.
				fetchPortletFileEntryByExternalReferenceCode(
					expectedExternalReferenceCode, testGroup.getGroupId());

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.getFragmentEntryByExternalReferenceCode(
				fragment.getExternalReferenceCode(), testGroup.getGroupId());

		Assert.assertEquals(
			fileEntry.getFileEntryId(), fragmentEntry.getPreviewFileEntryId());

		try (InputStream inputStream = fileEntry.getContentStream()) {
			Assert.assertArrayEquals(
				expectedBytes, StreamUtil.toByteArray(inputStream));
		}

		FragmentResource fragmentResource = _getFragmentResource(
			"thumbnailURLReference");

		URL url = new URL(
			IdempotentRetryAssert.retryAssert(
				30, TimeUnit.SECONDS, 500, TimeUnit.MILLISECONDS,
				() -> {
					Fragment getFragment = fragmentResource.getSiteFragment(
						testGroup.getExternalReferenceCode(),
						fragment.getExternalReferenceCode());

					ThumbnailURLReference getThumbnailURLReference =
						getFragment.getThumbnailURLReference();

					Assert.assertNotNull(getThumbnailURLReference);

					return getThumbnailURLReference.getUrl();
				}));

		HttpURLConnection httpURLConnection =
			(HttpURLConnection)url.openConnection();

		Assert.assertEquals(
			HttpURLConnection.HTTP_OK, httpURLConnection.getResponseCode());

		String contentType = httpURLConnection.getContentType();

		Assert.assertTrue(contentType.startsWith("image/"));
	}

	private String _exportFragmentsToJSON(String siteExternalReferenceCode)
		throws Exception {

		return _exportFragmentsToJSON(null, siteExternalReferenceCode);
	}

	private String _exportFragmentsToJSON(
			String filterString, String siteExternalReferenceCode)
		throws Exception {

		String endpoint = StringBundler.concat(
			"headless-admin-fragment/v1.0/sites/", siteExternalReferenceCode,
			"/fragments/export-batch?contentType=JSON");

		if (filterString != null) {
			endpoint = StringBundler.concat(
				endpoint, "&filter=", URLCodec.encodeURL(filterString));
		}

		JSONObject exportTaskJSONObject = _waitForFinish(
			"COMPLETED", false,
			HTTPTestUtil.invokeToJSONObject(null, endpoint, Http.Method.POST));

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

	private FragmentResource _getFragmentResource(String nestedFields)
		throws Exception {

		User user = UserTestUtil.getAdminUser(testCompany.getCompanyId());

		return FragmentResource.builder(
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

	private FragmentVersion _getFragmentVersion(
		Fragment fragment, FragmentVersion.Status status) {

		FragmentVersion[] fragmentVersions = fragment.getFragmentVersions();

		if (fragmentVersions == null) {
			return null;
		}

		for (FragmentVersion fragmentVersion : fragmentVersions) {
			if (status == fragmentVersion.getStatus()) {
				return fragmentVersion;
			}
		}

		return null;
	}

	private void _populateFragment(Fragment fragment) {
		if ((fragment instanceof FormFragment formFragment) &&
			(formFragment.getFieldTypes() == null)) {

			formFragment.setFieldTypes(new FieldType[] {FieldType.TEXT});
		}

		if (fragment.getFragmentSet() == null) {
			fragment.setFragmentSet(_toFragmentSet(_fragmentCollection));
		}

		fragment.setFragmentSetExternalReferenceCode(
			_fragmentCollection.getExternalReferenceCode());

		if (ArrayUtil.isEmpty(fragment.getFragmentVersions())) {
			fragment.setFragmentVersions(_randomFragmentVersions());
		}

		fragment.setMarketplace(false);
	}

	private Fragment _postSiteFragment(Fragment fragment) throws Exception {
		return fragmentResource.postSiteFragment(
			testGroup.getExternalReferenceCode(), fragment);
	}

	private Fragment _postSiteFragment(Fragment fragment, String nestedFields)
		throws Exception {

		FragmentResource fragmentResource = _getFragmentResource(nestedFields);

		return fragmentResource.postSiteFragment(
			testGroup.getExternalReferenceCode(), fragment);
	}

	private Fragment _postSiteFragmentAndAssertThumbnailURLReference(
			byte[] expectedBytes, String expectedExternalReferenceCode,
			ThumbnailURLReference thumbnailURLReference)
		throws Exception {

		Fragment fragment = randomFragment();

		fragment.setThumbnailURLReference(thumbnailURLReference);

		Fragment postFragment = _postSiteFragment(
			fragment, "thumbnailURLReference");

		if (expectedExternalReferenceCode == null) {
			ThumbnailURLReference postThumbnailURLReference =
				postFragment.getThumbnailURLReference();

			expectedExternalReferenceCode =
				postThumbnailURLReference.getExternalReferenceCode();
		}

		_assertThumbnailURLReference(
			expectedBytes, expectedExternalReferenceCode, postFragment);

		return postFragment;
	}

	private Fragment _postSiteFragmentSetFragment(Fragment fragment)
		throws Exception {

		return _postSiteFragmentSetFragment(
			fragment, _fragmentCollection.getExternalReferenceCode());
	}

	private Fragment _postSiteFragmentSetFragment(
			Fragment fragment, String fragmentSetExternalReferenceCode)
		throws Exception {

		return fragmentResource.postSiteFragmentSetFragment(
			testGroup.getExternalReferenceCode(),
			fragmentSetExternalReferenceCode, fragment);
	}

	private Fragment _putSiteFragment(
			String externalReferenceCode, Fragment fragment,
			String nestedFields)
		throws Exception {

		FragmentResource fragmentResource = _getFragmentResource(nestedFields);

		return fragmentResource.putSiteFragment(
			testGroup.getExternalReferenceCode(), externalReferenceCode,
			fragment);
	}

	private Fragment _putSiteFragmentAndAssertThumbnailURLReference(
			byte[] expectedBytes, String expectedExternalReferenceCode,
			Fragment fragment, ThumbnailURLReference thumbnailURLReference)
		throws Exception {

		fragment.setThumbnailURLReference(thumbnailURLReference);

		Fragment putFragment = _putSiteFragment(
			fragment.getExternalReferenceCode(), fragment,
			"thumbnailURLReference");

		if (expectedExternalReferenceCode == null) {
			ThumbnailURLReference putThumbnailURLReference =
				putFragment.getThumbnailURLReference();

			expectedExternalReferenceCode =
				putThumbnailURLReference.getExternalReferenceCode();
		}

		_assertThumbnailURLReference(
			expectedBytes, expectedExternalReferenceCode, putFragment);

		return putFragment;
	}

	private BasicFragment _randomBasicFragment() {
		return new BasicFragment() {
			{
				setCacheable(RandomTestUtil.randomBoolean());
				setDateCreated(RandomTestUtil.nextDate());
				setDateModified(RandomTestUtil.nextDate());
				setExternalReferenceCode(
					StringUtil.toLowerCase(RandomTestUtil.randomString()));
				setIcon(StringUtil.toLowerCase(RandomTestUtil.randomString()));
				setKey(StringUtil.toLowerCase(RandomTestUtil.randomString()));
				setMarketplace(false);
				setName(StringUtil.toLowerCase(RandomTestUtil.randomString()));
				setReadOnly(RandomTestUtil.randomBoolean());
				setType(Fragment.Type.BASIC_FRAGMENT);
			}
		};
	}

	private FormFragment _randomFormFragment() {
		return _randomFormFragment(new FieldType[] {FieldType.TEXT});
	}

	private FormFragment _randomFormFragment(FieldType[] fieldTypes) {
		return _randomFormFragment(null, fieldTypes);
	}

	private FormFragment _randomFormFragment(
		String externalReferenceCode, FieldType[] fieldTypes) {

		FormFragment formFragment = new FormFragment() {
			{
				setFragmentSet(_toFragmentSet(_fragmentCollection));
				setFragmentSetExternalReferenceCode(
					_fragmentCollection.getExternalReferenceCode());
				setFragmentVersions(
					new FragmentVersion[] {
						_randomFragmentVersion(FragmentVersion.Status.APPROVED)
					});
				setKey(StringUtil.toLowerCase(RandomTestUtil.randomString()));
				setMarketplace(false);
				setName(StringUtil.toLowerCase(RandomTestUtil.randomString()));
				setType(Fragment.Type.FORM_FRAGMENT);
			}
		};

		formFragment.setExternalReferenceCode(externalReferenceCode);
		formFragment.setFieldTypes(fieldTypes);

		return formFragment;
	}

	private Fragment _randomFragment(boolean approved, boolean draft)
		throws Exception {

		return _randomFragment(approved, draft, null, null);
	}

	private Fragment _randomFragment(
			boolean approved, boolean draft,
			FragmentCollection fragmentCollection)
		throws Exception {

		return _randomFragment(approved, draft, null, fragmentCollection, null);
	}

	private Fragment _randomFragment(
			boolean approved, boolean draft, String externalReferenceCode,
			FragmentCollection fragmentCollection, String key)
		throws Exception {

		Fragment fragment = _randomBasicFragment();

		List<FragmentVersion> fragmentVersions = new ArrayList<>();

		if (approved) {
			fragmentVersions.add(
				_randomFragmentVersion(FragmentVersion.Status.APPROVED));
		}

		if (draft) {
			fragmentVersions.add(
				_randomFragmentVersion(FragmentVersion.Status.DRAFT));
		}

		fragment.setExternalReferenceCode(externalReferenceCode);
		fragment.setFragmentSet(_toFragmentSet(fragmentCollection));

		if (fragmentCollection != null) {
			fragment.setFragmentSetExternalReferenceCode(
				fragmentCollection.getExternalReferenceCode());
		}

		fragment.setFragmentVersions(
			fragmentVersions.toArray(new FragmentVersion[0]));

		if (key != null) {
			fragment.setKey(key);
		}

		return fragment;
	}

	private Fragment _randomFragment(
			boolean approved, boolean draft, String externalReferenceCode,
			String key)
		throws Exception {

		return _randomFragment(
			approved, draft, externalReferenceCode, _fragmentCollection, key);
	}

	private FragmentSet _randomFragmentSet(String externalReferenceCode) {
		FragmentSet fragmentSet = new FragmentSet();

		fragmentSet.setExternalReferenceCode(externalReferenceCode);
		fragmentSet.setDescription(RandomTestUtil.randomString());
		fragmentSet.setName(RandomTestUtil.randomString());

		return fragmentSet;
	}

	private FragmentVersion _randomFragmentVersion(
		FragmentVersion.Status fragmentVersionStatus) {

		return new FragmentVersion() {
			{
				configuration = RandomTestUtil.randomString();
				css = RandomTestUtil.randomString();
				html = RandomTestUtil.randomString();
				js = RandomTestUtil.randomString();
				status = fragmentVersionStatus;
			}
		};
	}

	private FragmentVersion[] _randomFragmentVersions() {
		return new FragmentVersion[] {
			_randomFragmentVersion(FragmentVersion.Status.APPROVED),
			_randomFragmentVersion(FragmentVersion.Status.DRAFT)
		};
	}

	private Fragment _randomMarketplaceFragment() throws Exception {
		Fragment fragment = randomFragment();

		fragment.setMarketplace(true);

		return fragment;
	}

	private void _testBatchEngineDeleteImportTask() throws Exception {
		BasicFragment basicFragment1 =
			(BasicFragment)_postSiteFragmentSetFragment(randomFragment());
		BasicFragment basicFragment2 =
			(BasicFragment)_postSiteFragmentSetFragment(randomFragment());
		FormFragment formFragment1 = (FormFragment)_postSiteFragmentSetFragment(
			_randomFormFragment());
		FormFragment formFragment2 = (FormFragment)_postSiteFragmentSetFragment(
			_randomFormFragment());

		try (SafeCloseable safeCloseable =
				LazyReferencingTestUtil.setLazyReferencingWithSafeCloseable(
					true)) {

			waitForFinish(
				"COMPLETED",
				HTTPTestUtil.invokeToJSONObject(
					_exportFragmentsToJSON(
						testGroup.getExternalReferenceCode()),
					"headless-admin-fragment/v1.0/sites/" +
						irrelevantGroup.getExternalReferenceCode() +
							"/fragments/batch?createStrategy=INSERT",
					Http.Method.POST));
		}

		_assertFragmentEntry(basicFragment1, irrelevantGroup);
		_assertFragmentEntry(basicFragment2, irrelevantGroup);
		_assertFormFragmentEntry(
			new FieldType[] {FieldType.TEXT}, formFragment1, irrelevantGroup);
		_assertFormFragmentEntry(
			new FieldType[] {FieldType.TEXT}, formFragment2, irrelevantGroup);

		_testBatchEngineDeleteImportTask_deleteFragments(
			200,
			new JSONObject[] {
				JSONUtil.put(
					"externalReferenceCode",
					basicFragment1.getExternalReferenceCode()
				).put(
					"type", "BasicFragment"
				),
				JSONUtil.put(
					"externalReferenceCode",
					formFragment1.getExternalReferenceCode()
				).put(
					"type", "FormFragment"
				)
			},
			"siteExternalReferenceCode",
			irrelevantGroup.getExternalReferenceCode());

		_assertNullFragmentEntry(basicFragment1, irrelevantGroup);
		_assertFragmentEntry(basicFragment2, irrelevantGroup);
		_assertNullFragmentEntry(formFragment1, irrelevantGroup);
		_assertFormFragmentEntry(
			new FieldType[] {FieldType.TEXT}, formFragment2, irrelevantGroup);
	}

	private void _testBatchEngineDeleteImportTask_deleteFragments(
			int expectedStatusCode, JSONObject[] jsonObjects,
			String... parameters)
		throws Exception {

		User user = UserTestUtil.getAdminUser(testCompany.getCompanyId());

		ImportTaskResource importTaskResource = ImportTaskResource.builder(
		).authentication(
			user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).parameters(
			parameters
		).build();

		HttpInvoker.HttpResponse httpResponse =
			importTaskResource.deleteImportTaskHttpResponse(
				"com.liferay.headless.admin.fragment.dto.v1_0.Fragment", null,
				null, null, null, JSONUtil.putAll((Object[])jsonObjects));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		if (expectedStatusCode == 200) {
			waitForFinish(
				"COMPLETED",
				JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
		}
	}

	private void _testDeleteSiteFragment(boolean approved, boolean draft)
		throws Exception {

		Fragment postFragment = _postSiteFragmentSetFragment(
			_randomFragment(approved, draft));

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.
				fetchFragmentEntryByExternalReferenceCode(
					postFragment.getExternalReferenceCode(),
					testGroup.getGroupId(), approved);

		fragmentResource.deleteSiteFragment(
			testGroup.getExternalReferenceCode(),
			postFragment.getExternalReferenceCode());

		Assert.assertNull(
			_fragmentEntryLocalService.fetchFragmentEntry(
				fragmentEntry.getFragmentEntryId()));

		List<FragmentEntryVersion> fragmentEntryVersions =
			_fragmentEntryLocalService.getVersions(fragmentEntry);

		Assert.assertTrue(fragmentEntryVersions.isEmpty());
	}

	private void _testDeleteSiteFragmentNonexistent() throws Exception {
		assertHttpResponseStatusCode(
			404,
			fragmentResource.deleteSiteFragmentHttpResponse(
				testGroup.getExternalReferenceCode(),
				RandomTestUtil.randomString()));
	}

	private void _testDeleteSiteFragmentWithFormFragment() throws Exception {
		Fragment fragment = _postSiteFragmentSetFragment(_randomFormFragment());

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.
				fetchFragmentEntryByExternalReferenceCode(
					fragment.getExternalReferenceCode(), testGroup.getGroupId(),
					true);

		fragmentResource.deleteSiteFragment(
			testGroup.getExternalReferenceCode(),
			fragment.getExternalReferenceCode());

		Assert.assertNull(
			_fragmentEntryLocalService.fetchFragmentEntry(
				fragmentEntry.getFragmentEntryId()));

		List<FragmentEntryVersion> fragmentEntryVersions =
			_fragmentEntryLocalService.getVersions(fragmentEntry);

		Assert.assertTrue(fragmentEntryVersions.isEmpty());
	}

	private void _testGetSiteFragment(boolean approved, boolean draft)
		throws Exception {

		Fragment postFragment = _postSiteFragmentSetFragment(
			_randomFragment(approved, draft));

		Fragment getFragment = fragmentResource.getSiteFragment(
			testGroup.getExternalReferenceCode(),
			postFragment.getExternalReferenceCode());

		assertEquals(postFragment, getFragment);
		assertValid(getFragment);
	}

	private void _testGetSiteFragmentApproved() throws Exception {
		_testGetSiteFragment(true, false);
	}

	private void _testGetSiteFragmentApprovedAndDraft() throws Exception {
		_testGetSiteFragment(true, true);
	}

	private void _testGetSiteFragmentDraft() throws Exception {
		_testGetSiteFragment(false, true);
	}

	private void _testGetSiteFragmentSetFragmentsPageWithNonexistentFragmentSet()
		throws Exception {

		try {
			fragmentResource.getSiteFragmentSetFragmentsPage(
				testGroup.getExternalReferenceCode(),
				RandomTestUtil.randomString(), Pagination.of(1, 10));

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}
	}

	private void _testGetSiteFragmentSetFragmentsPageWithStatus()
		throws Exception {

		FragmentCollection fragmentCollection = _addFragmentCollection();

		Fragment approvedAndDraftFragment = _postSiteFragmentSetFragment(
			_randomFragment(true, true, fragmentCollection),
			fragmentCollection.getExternalReferenceCode());
		Fragment approvedFragment = _postSiteFragmentSetFragment(
			_randomFragment(true, false, fragmentCollection),
			fragmentCollection.getExternalReferenceCode());
		Fragment draftFragment = _postSiteFragmentSetFragment(
			_randomFragment(false, true, fragmentCollection),
			fragmentCollection.getExternalReferenceCode());

		FragmentCollection irrelevantFragmentCollection =
			_addFragmentCollection();

		_postSiteFragmentSetFragment(
			_randomFragment(true, true, irrelevantFragmentCollection),
			irrelevantFragmentCollection.getExternalReferenceCode());
		_postSiteFragmentSetFragment(
			_randomFragment(true, false, irrelevantFragmentCollection),
			irrelevantFragmentCollection.getExternalReferenceCode());
		_postSiteFragmentSetFragment(
			_randomFragment(false, true, irrelevantFragmentCollection),
			irrelevantFragmentCollection.getExternalReferenceCode());

		Page<Fragment> page = fragmentResource.getSiteFragmentSetFragmentsPage(
			testGroup.getExternalReferenceCode(),
			fragmentCollection.getExternalReferenceCode(),
			Pagination.of(1, 10));

		List<Fragment> items = (List<Fragment>)page.getItems();

		assertContains(approvedAndDraftFragment, items);
		assertContains(approvedFragment, items);
		assertContains(draftFragment, items);
		Assert.assertEquals(items.toString(), 3, items.size());
	}

	private void _testGetSiteFragmentsPageWithFilter() throws Exception {
		Fragment marketplaceFragment = _randomMarketplaceFragment();

		marketplaceFragment = testGetSiteFragmentsPage_addFragment(
			testGroup.getExternalReferenceCode(), marketplaceFragment);

		Fragment nonmarketplaceFragment = randomFragment();

		nonmarketplaceFragment = testGetSiteFragmentsPage_addFragment(
			testGroup.getExternalReferenceCode(), nonmarketplaceFragment);

		_assertGetSiteFragmentsPageWithFilter(
			marketplaceFragment, "marketplace eq true", nonmarketplaceFragment);
		_assertGetSiteFragmentsPageWithFilter(
			nonmarketplaceFragment, "marketplace eq false",
			marketplaceFragment);
	}

	private void _testGetSiteFragmentsPageWithFragmentSets() throws Exception {
		FragmentCollection fragmentCollection1 = _addFragmentCollection();

		Fragment approvedAndDraftFragment1 = _postSiteFragmentSetFragment(
			_randomFragment(true, true, fragmentCollection1),
			fragmentCollection1.getExternalReferenceCode());
		Fragment approvedFragment1 = _postSiteFragmentSetFragment(
			_randomFragment(true, false, fragmentCollection1),
			fragmentCollection1.getExternalReferenceCode());
		Fragment draftFragment1 = _postSiteFragmentSetFragment(
			_randomFragment(false, true, fragmentCollection1),
			fragmentCollection1.getExternalReferenceCode());

		FragmentCollection fragmentCollection2 = _addFragmentCollection();

		Fragment approvedAndDraftFragment2 = _postSiteFragmentSetFragment(
			_randomFragment(true, true, fragmentCollection2),
			fragmentCollection2.getExternalReferenceCode());
		Fragment approvedFragment2 = _postSiteFragmentSetFragment(
			_randomFragment(true, false, fragmentCollection2),
			fragmentCollection2.getExternalReferenceCode());
		Fragment draftFragment2 = _postSiteFragmentSetFragment(
			_randomFragment(false, true, fragmentCollection2),
			fragmentCollection2.getExternalReferenceCode());

		Page<Fragment> page = fragmentResource.getSiteFragmentsPage(
			testGroup.getExternalReferenceCode(), null, null);

		List<Fragment> items = (List<Fragment>)page.getItems();

		assertContains(approvedAndDraftFragment1, items);
		assertContains(approvedAndDraftFragment2, items);
		assertContains(approvedFragment1, items);
		assertContains(approvedFragment2, items);
		assertContains(draftFragment1, items);
		assertContains(draftFragment2, items);
	}

	private void _testGetSiteFragmentThumbnailURLReference() throws Exception {
		Fragment postFragment = _postSiteFragmentSetFragment(randomFragment());

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.getFragmentEntryByExternalReferenceCode(
				postFragment.getExternalReferenceCode(),
				testGroup.getGroupId());

		FileEntry fileEntry = _addPortletFileEntry("thumbnail_1.png");

		_fragmentEntryLocalService.updateFragmentEntry(
			fragmentEntry.getFragmentEntryId(), fileEntry.getFileEntryId());

		Fragment getFragment = fragmentResource.getSiteFragment(
			testGroup.getExternalReferenceCode(),
			postFragment.getExternalReferenceCode());

		Assert.assertNull(getFragment.getThumbnailURLReference());

		FragmentResource fragmentResource = _getFragmentResource(
			"thumbnailURLReference");

		_assertThumbnailURLReference(
			_thumbnail1Bytes, fileEntry.getExternalReferenceCode(),
			fragmentResource.getSiteFragment(
				testGroup.getExternalReferenceCode(),
				postFragment.getExternalReferenceCode()));
	}

	private void _testGetSiteFragmentWithFormFragment() throws Exception {
		FieldType[] fieldTypes = {RandomTestUtil.randomEnum(FieldType.class)};

		Fragment fragment = _postSiteFragmentSetFragment(
			_randomFormFragment(fieldTypes));

		_assertFormFragment(
			fieldTypes,
			fragmentResource.getSiteFragment(
				testGroup.getExternalReferenceCode(),
				fragment.getExternalReferenceCode()));
	}

	private void _testPostFragmentApproved(
			boolean approved, boolean draft,
			UnsafeFunction<Fragment, Fragment, Exception>
				postFragmentUnsafeFunction)
		throws Exception {

		Fragment postFragment = postFragmentUnsafeFunction.apply(
			_randomFragment(approved, draft));

		Fragment getFragment = fragmentResource.getSiteFragment(
			testGroup.getExternalReferenceCode(),
			postFragment.getExternalReferenceCode());

		assertEquals(postFragment, getFragment);
		assertValid(getFragment);
	}

	private void _testPostFragmentApproved(
			UnsafeFunction<Fragment, Fragment, Exception>
				postFragmentUnsafeFunction)
		throws Exception {

		_testPostFragmentApproved(true, false, postFragmentUnsafeFunction);
	}

	private void _testPostFragmentApprovedAndDraft(
			UnsafeFunction<Fragment, Fragment, Exception>
				postFragmentUnsafeFunction)
		throws Exception {

		_testPostFragmentApproved(true, true, postFragmentUnsafeFunction);
	}

	private void _testPostFragmentDraft(
			UnsafeFunction<Fragment, Fragment, Exception>
				postFragmentUnsafeFunction)
		throws Exception {

		_testPostFragmentApproved(false, true, postFragmentUnsafeFunction);
	}

	private void
			_testPostFragmentDuplicateExternalReferenceCodeProblemException(
				UnsafeFunction<Fragment, Fragment, Exception>
					postFragmentUnsafeFunction)
		throws Exception {

		Fragment postFragment = postFragmentUnsafeFunction.apply(
			randomFragment());

		Fragment duplicateFragment = randomFragment();

		duplicateFragment.setExternalReferenceCode(
			postFragment.getExternalReferenceCode());

		_assertProblemException(
			"CONFLICT", "this-external-reference-code-is-already-in-use",
			() -> postFragmentUnsafeFunction.apply(duplicateFragment));
	}

	private void _testPostFragmentDuplicateKeyProblemException(
			UnsafeFunction<Fragment, Fragment, Exception>
				postFragmentUnsafeFunction)
		throws Exception {

		Fragment postFragment = postFragmentUnsafeFunction.apply(
			randomFragment());

		Fragment duplicateFragment = randomFragment();

		duplicateFragment.setKey(postFragment.getKey());

		_assertProblemException(
			"CONFLICT", "a-fragment-entry-with-the-key-x-already-exists",
			() -> postFragmentUnsafeFunction.apply(duplicateFragment),
			postFragment.getKey());
	}

	private void _testPostFragmentEmpty(
			UnsafeFunction<Fragment, Fragment, Exception>
				postFragmentUnsafeFunction)
		throws Exception {

		_testPostFragmentApproved(false, false, postFragmentUnsafeFunction);
	}

	private void _testPostFragmentThumbnailURLReferenceProblemException(
			String expectedTitle, String externalReferenceCode, String url)
		throws Exception {

		Fragment fragment = randomFragment();

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setExternalReferenceCode(externalReferenceCode);

		thumbnailURLReference.setUrl(url);

		fragment.setThumbnailURLReference(thumbnailURLReference);

		try {
			_postSiteFragment(fragment);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(expectedTitle, problem.getTitle());
		}
	}

	private void _testPostSiteFragmentApproved() throws Exception {
		_testPostFragmentApproved(this::_postSiteFragment);
	}

	private void _testPostSiteFragmentApprovedAndDraft() throws Exception {
		_testPostFragmentApprovedAndDraft(this::_postSiteFragment);
	}

	private void _testPostSiteFragmentBatch() throws Exception {
		_testPostSiteFragmentBatchWithLazyReferencingDisabled();
		_testPostSiteFragmentBatchWithLazyReferencingEnabled();
	}

	private void _testPostSiteFragmentBatchWithLazyReferencingDisabled()
		throws Exception {

		Fragment fragment1 = _postSiteFragmentSetFragment(randomFragment());
		Fragment fragment2 = _postSiteFragmentSetFragment(randomFragment());

		Assert.assertNull(
			_fragmentCollectionLocalService.
				fetchFragmentCollectionByExternalReferenceCode(
					_fragmentCollection.getExternalReferenceCode(),
					irrelevantGroup.getGroupId()));
		_assertNullFragmentEntry(fragment1, irrelevantGroup);
		_assertNullFragmentEntry(fragment2, irrelevantGroup);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.batch.engine.internal." +
					"BatchEngineImportTaskExecutorImpl",
				LoggerTestUtil.ERROR)) {

			waitForFinish(
				"FAILED",
				HTTPTestUtil.invokeToJSONObject(
					_exportFragmentsToJSON(
						testGroup.getExternalReferenceCode()),
					"headless-admin-fragment/v1.0/sites/" +
						irrelevantGroup.getExternalReferenceCode() +
							"/fragments/batch?createStrategy=INSERT",
					Http.Method.POST));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertFalse(logEntries.toString(), logEntries.isEmpty());

			LogEntry logEntry = logEntries.get(0);

			Throwable throwable = logEntry.getThrowable();

			Assert.assertTrue(
				String.valueOf(throwable),
				throwable instanceof IllegalArgumentException);
			Assert.assertEquals(
				_language.format(
					LocaleUtil.getDefault(),
					"no-fragment-set-was-found-with-external-reference-code-x",
					_fragmentCollection.getExternalReferenceCode()),
				throwable.getMessage());
		}

		Assert.assertNull(
			_fragmentCollectionLocalService.
				fetchFragmentCollectionByExternalReferenceCode(
					_fragmentCollection.getExternalReferenceCode(),
					irrelevantGroup.getGroupId()));
		_assertNullFragmentEntry(fragment1, irrelevantGroup);
		_assertNullFragmentEntry(fragment2, irrelevantGroup);
	}

	private void _testPostSiteFragmentBatchWithLazyReferencingEnabled()
		throws Exception {

		Fragment marketplaceFragment = _randomMarketplaceFragment();

		marketplaceFragment = _postSiteFragmentSetFragment(marketplaceFragment);

		Fragment nonmarketplaceFragment = randomFragment();

		nonmarketplaceFragment = _postSiteFragmentSetFragment(
			nonmarketplaceFragment);

		_assertExportImportFragments(
			Collections.singletonList(nonmarketplaceFragment),
			"marketplace eq false",
			Collections.singletonList(marketplaceFragment));

		_assertExportImportFragmentsFails(null);
		_assertExportImportFragmentsFails("marketplace eq true");
	}

	private void _testPostSiteFragmentDraft() throws Exception {
		_testPostFragmentDraft(this::_postSiteFragment);
	}

	private void _testPostSiteFragmentDuplicateExternalReferenceCodeProblemException()
		throws Exception {

		_testPostFragmentDuplicateExternalReferenceCodeProblemException(
			this::_postSiteFragment);
	}

	private void _testPostSiteFragmentDuplicateKeyProblemException()
		throws Exception {

		_testPostFragmentDuplicateKeyProblemException(this::_postSiteFragment);
	}

	private void _testPostSiteFragmentEmpty() throws Exception {
		_testPostFragmentEmpty(this::_postSiteFragment);
	}

	private void _testPostSiteFragmentFragmentSetAndFragmentSetExternalReferenceCode()
		throws Exception {

		Fragment fragment = randomFragment();

		FragmentCollection fragmentCollection1 = _addFragmentCollection();
		FragmentCollection fragmentCollection2 = _addFragmentCollection();

		fragment.setFragmentSet(
			_randomFragmentSet(fragmentCollection1.getExternalReferenceCode()));
		fragment.setFragmentSetExternalReferenceCode(
			fragmentCollection2.getExternalReferenceCode());

		Fragment postFragment = _postSiteFragment(fragment);

		Assert.assertEquals(
			fragmentCollection2.getExternalReferenceCode(),
			postFragment.getFragmentSetExternalReferenceCode());
	}

	private void _testPostSiteFragmentFragmentSetAndFragmentSetExternalReferenceCodeProblemException()
		throws Exception {

		Fragment fragment = randomFragment();

		fragment.setFragmentSet(
			_randomFragmentSet(RandomTestUtil.randomString()));
		fragment.setFragmentSetExternalReferenceCode(
			RandomTestUtil.randomString());

		_assertProblemException(
			"the-fragment-set-external-reference-codes-do-not-match",
			() -> {
				try (SafeCloseable safeCloseable =
						LazyReferencingTestUtil.
							setLazyReferencingWithSafeCloseable(true)) {

					_postSiteFragment(fragment);
				}
			});
	}

	private void _testPostSiteFragmentFragmentSetExisting() throws Exception {
		Fragment fragment = randomFragment();

		FragmentCollection fragmentCollection = _addFragmentCollection();

		fragment.setFragmentSetExternalReferenceCode(
			fragmentCollection.getExternalReferenceCode());

		Fragment postFragment = _postSiteFragment(fragment);

		Assert.assertEquals(
			fragmentCollection.getExternalReferenceCode(),
			postFragment.getFragmentSetExternalReferenceCode());
	}

	private void _testPostSiteFragmentFragmentSetExternalReferenceCodeNullProblemException()
		throws Exception {

		Fragment fragment = randomFragment();

		fragment.setFragmentSetExternalReferenceCode((String)null);

		_assertProblemException(
			"a-fragment-set-external-reference-code-is-required-to-create-a-" +
				"new-fragment",
			() -> _postSiteFragment(fragment));
	}

	private void _testPostSiteFragmentFragmentSetNonexisting()
		throws Exception {

		Fragment fragment = randomFragment();

		String fragmentSetExternalReferenceCode = RandomTestUtil.randomString();

		FragmentSet fragmentSet = _randomFragmentSet(
			fragmentSetExternalReferenceCode);

		fragment.setFragmentSet(fragmentSet);

		fragment.setFragmentSetExternalReferenceCode(
			fragmentSetExternalReferenceCode);

		try (SafeCloseable safeCloseable =
				LazyReferencingTestUtil.setLazyReferencingWithSafeCloseable(
					true)) {

			Fragment postFragment = _postSiteFragment(fragment);

			Assert.assertEquals(
				fragmentSet.getExternalReferenceCode(),
				postFragment.getFragmentSetExternalReferenceCode());

			Assert.assertNotNull(
				_fragmentCollectionLocalService.
					fetchFragmentCollectionByExternalReferenceCode(
						fragmentSetExternalReferenceCode,
						testGroup.getGroupId()));
		}
	}

	private void _testPostSiteFragmentFragmentSetNonexistingProblemException()
		throws Exception {

		Fragment fragment = randomFragment();

		String fragmentSetExternalReferenceCode = RandomTestUtil.randomString();

		fragment.setFragmentSetExternalReferenceCode(
			fragmentSetExternalReferenceCode);

		_assertProblemException(
			"no-fragment-set-was-found-with-external-reference-code-x",
			() -> _postSiteFragment(fragment),
			fragmentSetExternalReferenceCode);

		Assert.assertNull(
			_fragmentCollectionLocalService.
				fetchFragmentCollectionByExternalReferenceCode(
					fragmentSetExternalReferenceCode, testGroup.getGroupId()));
	}

	private void _testPostSiteFragmentFragmentSetNullProblemException()
		throws Exception {

		Fragment fragment = randomFragment();

		fragment.setFragmentSet((FragmentSet)null);
		fragment.setFragmentSetExternalReferenceCode((String)null);

		_assertProblemException(
			"a-fragment-set-external-reference-code-is-required-to-create-a-" +
				"new-fragment",
			() -> _postSiteFragment(fragment));
	}

	private void _testPostSiteFragmentMarketplace() throws Exception {
		Fragment fragment = _randomMarketplaceFragment();

		Fragment postFragment = _postSiteFragmentSetFragment(fragment);

		FragmentVersion[] postFragmentVersions =
			postFragment.getFragmentVersions();

		Assert.assertTrue(
			Arrays.toString(postFragmentVersions),
			postFragmentVersions.length > 0);

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.getFragmentEntryByExternalReferenceCode(
				postFragment.getExternalReferenceCode(),
				testGroup.getGroupId());

		FragmentEntry draftFragmentEntry =
			_fragmentEntryLocalService.fetchDraft(
				fragmentEntry.getFragmentEntryId());

		FragmentVersion[] fragmentVersions = fragment.getFragmentVersions();

		for (int i = 0; i < fragmentVersions.length; i++) {
			FragmentVersion fragmentVersion = fragmentVersions[i];

			FragmentVersion postFragmentVersion = postFragmentVersions[i];

			Assert.assertNull(postFragmentVersion.getConfiguration());
			Assert.assertNull(postFragmentVersion.getCss());
			Assert.assertNull(postFragmentVersion.getHtml());
			Assert.assertNull(postFragmentVersion.getJs());

			FragmentEntry curFragmentEntry = fragmentEntry;

			if (fragmentVersion.getStatus() == FragmentVersion.Status.DRAFT) {
				curFragmentEntry = draftFragmentEntry;
			}

			Assert.assertEquals(
				fragmentVersion.getConfiguration(),
				curFragmentEntry.getConfiguration());
			Assert.assertEquals(
				fragmentVersion.getCss(), curFragmentEntry.getCss());
			Assert.assertEquals(
				fragmentVersion.getHtml(), curFragmentEntry.getHtml());
			Assert.assertEquals(
				fragmentVersion.getJs(), curFragmentEntry.getJs());
		}
	}

	private void _testPostSiteFragmentSetFragmentApproved() throws Exception {
		_testPostFragmentApproved(this::_postSiteFragmentSetFragment);
	}

	private void _testPostSiteFragmentSetFragmentApprovedAndDraft()
		throws Exception {

		_testPostFragmentApprovedAndDraft(this::_postSiteFragmentSetFragment);
	}

	private void _testPostSiteFragmentSetFragmentDraft() throws Exception {
		_testPostFragmentDraft(this::_postSiteFragmentSetFragment);
	}

	private void _testPostSiteFragmentSetFragmentDuplicateExternalReferenceCodeProblemException()
		throws Exception {

		_testPostFragmentDuplicateExternalReferenceCodeProblemException(
			this::_postSiteFragmentSetFragment);
	}

	private void _testPostSiteFragmentSetFragmentDuplicateKeyProblemException()
		throws Exception {

		_testPostFragmentDuplicateKeyProblemException(
			this::_postSiteFragmentSetFragment);
	}

	private void _testPostSiteFragmentSetFragmentEmpty() throws Exception {
		_testPostFragmentEmpty(this::_postSiteFragmentSetFragment);
	}

	private void _testPostSiteFragmentSetFragmentFragmentSetExisting()
		throws Exception {

		Fragment fragment = randomFragment();

		FragmentCollection fragmentCollection = _addFragmentCollection();

		fragment.setFragmentSet(
			_randomFragmentSet(fragmentCollection.getExternalReferenceCode()));

		Fragment postFragment = _postSiteFragmentSetFragment(fragment);

		Assert.assertEquals(
			_fragmentCollection.getExternalReferenceCode(),
			postFragment.getFragmentSetExternalReferenceCode());
	}

	private void _testPostSiteFragmentSetFragmentFragmentSetExternalReferenceCodeNull()
		throws Exception {

		Fragment fragment = randomFragment();

		fragment.setFragmentSet(_randomFragmentSet(null));

		Fragment postFragment = _postSiteFragmentSetFragment(fragment);

		Assert.assertEquals(
			_fragmentCollection.getExternalReferenceCode(),
			postFragment.getFragmentSetExternalReferenceCode());
	}

	private void _testPostSiteFragmentSetFragmentFragmentSetInPathNonexistingProblemException()
		throws Exception {

		Fragment fragment = randomFragment();

		fragment.setFragmentSet((FragmentSet)null);

		try {
			fragmentResource.postSiteFragmentSetFragment(
				testGroup.getExternalReferenceCode(),
				RandomTestUtil.randomString(), fragment);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}
	}

	private void _testPostSiteFragmentSetFragmentFragmentSetNonexisting()
		throws Exception {

		Fragment fragment = randomFragment();

		String fragmentSetExternalReferenceCode = RandomTestUtil.randomString();

		fragment.setFragmentSet(
			_randomFragmentSet(fragmentSetExternalReferenceCode));

		Fragment postFragment = _postSiteFragmentSetFragment(fragment);

		Assert.assertEquals(
			_fragmentCollection.getExternalReferenceCode(),
			postFragment.getFragmentSetExternalReferenceCode());

		Assert.assertNull(
			_fragmentCollectionLocalService.
				fetchFragmentCollectionByExternalReferenceCode(
					fragmentSetExternalReferenceCode, testGroup.getGroupId()));
	}

	private void _testPostSiteFragmentSetFragmentFragmentSetNull()
		throws Exception {

		Fragment fragment = randomFragment();

		fragment.setFragmentSet((FragmentSet)null);

		Fragment postFragment = _postSiteFragmentSetFragment(fragment);

		Assert.assertEquals(
			_fragmentCollection.getExternalReferenceCode(),
			postFragment.getFragmentSetExternalReferenceCode());
	}

	private void _testPostSiteFragmentSetFragmentWithFormFragment()
		throws Exception {

		FieldType[] fieldTypes = {FieldType.NUMBER, FieldType.TEXT};

		_assertFormFragment(
			fieldTypes,
			_postSiteFragmentSetFragment(_randomFormFragment(fieldTypes)));
	}

	private void _testPostSiteFragmentThumbnailURLReferenceExternalReferenceCode()
		throws Exception {

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		FileEntry fileEntry = _addPortletFileEntry("thumbnail_1.png");

		String externalReferenceCode = fileEntry.getExternalReferenceCode();

		thumbnailURLReference.setExternalReferenceCode(externalReferenceCode);

		_postSiteFragmentAndAssertThumbnailURLReference(
			_thumbnail1Bytes, externalReferenceCode, thumbnailURLReference);
	}

	private void _testPostSiteFragmentThumbnailURLReferenceExternalReferenceCodeAndFileBase64()
		throws Exception {

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		FileEntry fileEntry = _addPortletFileEntry("thumbnail_1.png");

		String externalReferenceCode = fileEntry.getExternalReferenceCode();

		thumbnailURLReference.setExternalReferenceCode(externalReferenceCode);

		thumbnailURLReference.setFileBase64(_thumbnail2Base64);

		_postSiteFragmentAndAssertThumbnailURLReference(
			_thumbnail1Bytes, externalReferenceCode, thumbnailURLReference);
	}

	private void _testPostSiteFragmentThumbnailURLReferenceExternalReferenceCodeEmptyAndFileBase64()
		throws Exception {

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setExternalReferenceCode(StringPool.BLANK);

		thumbnailURLReference.setFileBase64(_thumbnail1Base64);

		_postSiteFragmentAndAssertThumbnailURLReference(
			_thumbnail1Bytes, null, thumbnailURLReference);
	}

	private void _testPostSiteFragmentThumbnailURLReferenceExternalReferenceCodeNullAndFileBase64()
		throws Exception {

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setFileBase64(_thumbnail1Base64);

		_postSiteFragmentAndAssertThumbnailURLReference(
			_thumbnail1Bytes, null, thumbnailURLReference);
	}

	private void _testPostSiteFragmentThumbnailURLReferenceExternalReferenceCodeNullAndURL()
		throws Exception {

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setUrl(_thumbnail1URL);

		_postSiteFragmentAndAssertThumbnailURLReference(
			_thumbnail1Bytes, null, thumbnailURLReference);
	}

	private void _testPostSiteFragmentThumbnailURLReferenceFileBase64()
		throws Exception {

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		String externalReferenceCode = RandomTestUtil.randomString();

		thumbnailURLReference.setExternalReferenceCode(externalReferenceCode);

		thumbnailURLReference.setFileBase64(_thumbnail1Base64);

		_postSiteFragmentAndAssertThumbnailURLReference(
			_thumbnail1Bytes, externalReferenceCode, thumbnailURLReference);
	}

	private void _testPostSiteFragmentThumbnailURLReferenceFileBase64AndURL()
		throws Exception {

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		String externalReferenceCode = RandomTestUtil.randomString();

		thumbnailURLReference.setExternalReferenceCode(externalReferenceCode);

		thumbnailURLReference.setFileBase64(_thumbnail1Base64);

		thumbnailURLReference.setUrl(_thumbnail2URL);

		_postSiteFragmentAndAssertThumbnailURLReference(
			_thumbnail1Bytes, externalReferenceCode, thumbnailURLReference);
	}

	private void _testPostSiteFragmentThumbnailURLReferenceNonexistingProblemException()
		throws Exception {

		_testPostFragmentThumbnailURLReferenceProblemException(
			"Unable to resolve file", RandomTestUtil.randomString(), null);
	}

	private void _testPostSiteFragmentThumbnailURLReferenceURL()
		throws Exception {

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		String externalReferenceCode = RandomTestUtil.randomString();

		thumbnailURLReference.setExternalReferenceCode(externalReferenceCode);

		thumbnailURLReference.setUrl(_thumbnail1URL);

		_postSiteFragmentAndAssertThumbnailURLReference(
			_thumbnail1Bytes, externalReferenceCode, thumbnailURLReference);
	}

	private void _testPostSiteFragmentThumbnailURLReferenceURLUnreachableProblemException()
		throws Exception {

		String url =
			"http://invalid.example.test/" + RandomTestUtil.randomString();

		_testPostFragmentThumbnailURLReferenceProblemException(
			"Unable to download file from " + url,
			RandomTestUtil.randomString(), url);
	}

	private void _testPostSiteFragmentThumbnailURLReferenceURLUnsupportedProtocolProblemException()
		throws Exception {

		String url =
			"ftp://invalid.example.test/" + RandomTestUtil.randomString();

		_testPostFragmentThumbnailURLReferenceProblemException(
			"Unable to download file from " + url +
				" because of unsupported protocol ftp",
			RandomTestUtil.randomString(), url);
	}

	private void _testPostSiteFragmentWithFormFragment() throws Exception {
		_testPostSiteFragmentWithFormFragmentFieldTypes();
		_testPostSiteFragmentWithFormFragmentFieldTypesInvalidJsonMappingException();
		_testPostSiteFragmentWithFormFragmentFieldTypesInvalidProblemException();
	}

	private void _testPostSiteFragmentWithFormFragmentFieldTypes()
		throws Exception {

		for (FieldType fieldType : FieldType.values()) {
			FieldType[] fieldTypes = {fieldType};

			_assertFormFragment(
				fieldTypes, _postSiteFragment(_randomFormFragment(fieldTypes)));
		}
	}

	private void _testPostSiteFragmentWithFormFragmentFieldTypesInvalidJsonMappingException()
		throws Exception {

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"fieldTypes", JSONUtil.putAll(RandomTestUtil.randomString())
			).put(
				"type", "FormFragment"
			).toString(),
			"headless-admin-fragment/v1.0/sites/" +
				testGroup.getExternalReferenceCode() + "/fragments",
			Http.Method.POST);

		Assert.assertEquals("BAD_REQUEST", jsonObject.getString("status"));
		Assert.assertEquals(
			"Unable to map JSON path: fieldTypes.null",
			jsonObject.getString("title"));
	}

	private void _testPostSiteFragmentWithFormFragmentFieldTypesInvalidProblemException()
		throws Exception {

		_testPostSiteFragmentWithFormFragmentFieldTypesInvalidProblemException(
			null);
		_testPostSiteFragmentWithFormFragmentFieldTypesInvalidProblemException(
			new FieldType[0]);
		_testPostSiteFragmentWithFormFragmentFieldTypesInvalidProblemException(
			new FieldType[] {FieldType.CAPTCHA, FieldType.TEXT});
		_testPostSiteFragmentWithFormFragmentFieldTypesInvalidProblemException(
			new FieldType[] {FieldType.STEPPER, FieldType.TEXT});
	}

	private void
			_testPostSiteFragmentWithFormFragmentFieldTypesInvalidProblemException(
				FieldType[] fieldTypes)
		throws Exception {

		_assertProblemException(
			"the-form-fragment-field-types-are-invalid",
			() -> _postSiteFragment(_randomFormFragment(fieldTypes)));
	}

	private void _testPutFragment(
			String externalReferenceCode, Fragment fragment)
		throws Exception {

		Fragment putFragment = fragmentResource.putSiteFragment(
			testGroup.getExternalReferenceCode(), externalReferenceCode,
			fragment);

		assertEquals(fragment, putFragment);
		assertValid(putFragment);

		Fragment getFragment = fragmentResource.getSiteFragment(
			testGroup.getExternalReferenceCode(),
			putFragment.getExternalReferenceCode());

		assertEquals(fragment, getFragment);
		assertValid(getFragment);
	}

	private void _testPutSiteFragmentBatch() throws Exception {
		BasicFragment basicFragment1 =
			(BasicFragment)_postSiteFragmentSetFragment(randomFragment());
		BasicFragment basicFragment2 =
			(BasicFragment)_postSiteFragmentSetFragment(randomFragment());
		FormFragment formFragment1 = (FormFragment)_postSiteFragmentSetFragment(
			_randomFormFragment());
		FormFragment formFragment2 = (FormFragment)_postSiteFragmentSetFragment(
			_randomFormFragment());

		try (SafeCloseable safeCloseable =
				LazyReferencingTestUtil.setLazyReferencingWithSafeCloseable(
					true)) {

			waitForFinish(
				"COMPLETED",
				HTTPTestUtil.invokeToJSONObject(
					_exportFragmentsToJSON(
						testGroup.getExternalReferenceCode()),
					"headless-admin-fragment/v1.0/sites/" +
						irrelevantGroup.getExternalReferenceCode() +
							"/fragments/batch?createStrategy=INSERT",
					Http.Method.POST));
		}

		BasicFragment putBasicFragment1 =
			(BasicFragment)fragmentResource.putSiteFragment(
				testGroup.getExternalReferenceCode(),
				basicFragment1.getExternalReferenceCode(),
				_randomFragment(
					true, true, basicFragment1.getExternalReferenceCode(),
					null));

		FormFragment putFormFragment1 =
			(FormFragment)fragmentResource.putSiteFragment(
				testGroup.getExternalReferenceCode(),
				formFragment1.getExternalReferenceCode(),
				_randomFormFragment(
					formFragment1.getExternalReferenceCode(),
					new FieldType[] {FieldType.NUMBER, FieldType.TEXT}));

		try (SafeCloseable safeCloseable =
				LazyReferencingTestUtil.setLazyReferencingWithSafeCloseable(
					true)) {

			waitForFinish(
				"COMPLETED",
				HTTPTestUtil.invokeToJSONObject(
					_exportFragmentsToJSON(
						testGroup.getExternalReferenceCode()),
					"headless-admin-fragment/v1.0/sites/" +
						irrelevantGroup.getExternalReferenceCode() +
							"/fragments/batch?createStrategy=UPSERT",
					Http.Method.POST));
		}

		_assertFragmentEntry(putBasicFragment1, irrelevantGroup);
		_assertFragmentEntry(basicFragment2, irrelevantGroup);
		_assertFormFragmentEntry(
			putFormFragment1.getFieldTypes(), putFormFragment1,
			irrelevantGroup);
		_assertFormFragmentEntry(
			formFragment2.getFieldTypes(), formFragment2, irrelevantGroup);
	}

	private void _testPutSiteFragmentCreateApproved() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();

		_testPutFragment(
			externalReferenceCode,
			_randomFragment(true, false, externalReferenceCode, null));
	}

	private void _testPutSiteFragmentCreateApprovedAndDraft() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();

		_testPutFragment(
			externalReferenceCode,
			_randomFragment(true, true, externalReferenceCode, null));
	}

	private void _testPutSiteFragmentCreateDraft() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();

		_testPutFragment(
			externalReferenceCode,
			_randomFragment(false, true, externalReferenceCode, null));
	}

	private void _testPutSiteFragmentCreateEmpty() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();

		Fragment fragment = _randomFragment(
			false, false, externalReferenceCode, null);

		Fragment putFragment = fragmentResource.putSiteFragment(
			testGroup.getExternalReferenceCode(), externalReferenceCode,
			fragment);

		Assert.assertNull(
			_getFragmentVersion(putFragment, FragmentVersion.Status.APPROVED));
		Assert.assertNotNull(
			_getFragmentVersion(putFragment, FragmentVersion.Status.DRAFT));
	}

	private void _testPutSiteFragmentCreateFragmentSetExisting()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		Fragment fragment = _randomFragment(
			true, false, externalReferenceCode, null);

		FragmentCollection fragmentCollection = _addFragmentCollection();

		fragment.setFragmentSetExternalReferenceCode(
			fragmentCollection.getExternalReferenceCode());

		Fragment putFragment = fragmentResource.putSiteFragment(
			testGroup.getExternalReferenceCode(), externalReferenceCode,
			fragment);

		Assert.assertEquals(
			fragmentCollection.getExternalReferenceCode(),
			putFragment.getFragmentSetExternalReferenceCode());
	}

	private void _testPutSiteFragmentCreateFragmentSetExternalReferenceCodeNullProblemException()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		Fragment fragment = _randomFragment(
			true, false, externalReferenceCode, null);

		fragment.setFragmentSetExternalReferenceCode((String)null);

		_testPutSiteFragmentProblemException(
			externalReferenceCode, fragment,
			"a-fragment-set-external-reference-code-is-required-to-create-a-" +
				"new-fragment");
	}

	private void _testPutSiteFragmentCreateFragmentSetNonexisting()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		Fragment fragment = _randomFragment(
			true, false, externalReferenceCode, null);

		String fragmentSetExternalReferenceCode = RandomTestUtil.randomString();

		FragmentSet fragmentSet = _randomFragmentSet(
			fragmentSetExternalReferenceCode);

		fragment.setFragmentSet(fragmentSet);

		fragment.setFragmentSetExternalReferenceCode(
			fragmentSetExternalReferenceCode);

		try (SafeCloseable safeCloseable =
				LazyReferencingTestUtil.setLazyReferencingWithSafeCloseable(
					true)) {

			Fragment putFragment = fragmentResource.putSiteFragment(
				testGroup.getExternalReferenceCode(), externalReferenceCode,
				fragment);

			Assert.assertEquals(
				fragmentSet.getExternalReferenceCode(),
				putFragment.getFragmentSetExternalReferenceCode());

			Assert.assertNotNull(
				_fragmentCollectionLocalService.
					fetchFragmentCollectionByExternalReferenceCode(
						fragmentSetExternalReferenceCode,
						testGroup.getGroupId()));
		}
	}

	private void _testPutSiteFragmentCreateFragmentSetNonexistingProblemException()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		Fragment fragment = _randomFragment(
			true, false, externalReferenceCode, null);

		String fragmentSetExternalReferenceCode = RandomTestUtil.randomString();

		fragment.setFragmentSetExternalReferenceCode(
			fragmentSetExternalReferenceCode);

		_assertProblemException(
			"no-fragment-set-was-found-with-external-reference-code-x",
			() -> fragmentResource.putSiteFragment(
				testGroup.getExternalReferenceCode(), externalReferenceCode,
				fragment),
			fragmentSetExternalReferenceCode);

		Assert.assertNull(
			_fragmentCollectionLocalService.
				fetchFragmentCollectionByExternalReferenceCode(
					fragmentSetExternalReferenceCode, testGroup.getGroupId()));
	}

	private void _testPutSiteFragmentCreateFragmentSetNullProblemException()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		Fragment fragment = _randomFragment(
			true, false, externalReferenceCode, null);

		fragment.setFragmentSet((FragmentSet)null);
		fragment.setFragmentSetExternalReferenceCode((String)null);

		_testPutSiteFragmentProblemException(
			externalReferenceCode, fragment,
			"a-fragment-set-external-reference-code-is-required-to-create-a-" +
				"new-fragment");
	}

	private void _testPutSiteFragmentProblemException(
			String externalReferenceCode, Fragment fragment, String titleKey)
		throws Exception {

		_assertProblemException(
			titleKey,
			() -> fragmentResource.putSiteFragment(
				testGroup.getExternalReferenceCode(), externalReferenceCode,
				fragment));
	}

	private void _testPutSiteFragmentUpdateApprovedAddDraft() throws Exception {
		Fragment postFragment = _postSiteFragmentSetFragment(
			_randomFragment(true, false));

		FragmentVersion approvedVersion = _getFragmentVersion(
			postFragment, FragmentVersion.Status.APPROVED);

		Fragment fragment = _randomFragment(
			false, true, postFragment.getExternalReferenceCode(),
			postFragment.getKey());

		FragmentVersion draftVersion = _getFragmentVersion(
			fragment, FragmentVersion.Status.DRAFT);

		fragment.setFragmentVersions(
			new FragmentVersion[] {approvedVersion, draftVersion});

		_testPutFragment(postFragment.getExternalReferenceCode(), fragment);
	}

	private void _testPutSiteFragmentUpdateApprovedAddDraftModifyApproved()
		throws Exception {

		Fragment postFragment = _postSiteFragmentSetFragment(
			_randomFragment(true, false));

		_testPutFragment(
			postFragment.getExternalReferenceCode(),
			_randomFragment(
				true, true, postFragment.getExternalReferenceCode(),
				postFragment.getKey()));
	}

	private void _testPutSiteFragmentUpdateApprovedAndDraftToDraftProblemException()
		throws Exception {

		Fragment postFragment = _postSiteFragmentSetFragment(
			_randomFragment(true, true));

		_testPutSiteFragmentProblemException(
			postFragment.getExternalReferenceCode(),
			_randomFragment(
				false, true, postFragment.getExternalReferenceCode(),
				postFragment.getKey()),
			"unpublishing-a-fragment-entry-is-not-supported");
	}

	private void _testPutSiteFragmentUpdateApprovedAndDraftToEmptyProblemException()
		throws Exception {

		Fragment postFragment = _postSiteFragmentSetFragment(
			_randomFragment(true, true));

		_testPutSiteFragmentProblemException(
			postFragment.getExternalReferenceCode(),
			_randomFragment(
				false, false, postFragment.getExternalReferenceCode(),
				postFragment.getKey()),
			"at-least-one-fragment-entry-version-is-required");
	}

	private void _testPutSiteFragmentUpdateApprovedModifyApproved()
		throws Exception {

		Fragment postFragment = _postSiteFragmentSetFragment(
			_randomFragment(true, false));

		_testPutFragment(
			postFragment.getExternalReferenceCode(),
			_randomFragment(
				true, false, postFragment.getExternalReferenceCode(),
				postFragment.getKey()));
	}

	private void _testPutSiteFragmentUpdateApprovedModifyApprovedAndDraft()
		throws Exception {

		Fragment postFragment = _postSiteFragmentSetFragment(
			_randomFragment(true, true));

		_testPutFragment(
			postFragment.getExternalReferenceCode(),
			_randomFragment(
				true, true, postFragment.getExternalReferenceCode(),
				postFragment.getKey()));
	}

	private void _testPutSiteFragmentUpdateApprovedToDraftProblemException()
		throws Exception {

		Fragment postFragment = _postSiteFragmentSetFragment(
			_randomFragment(true, false));

		_testPutSiteFragmentProblemException(
			postFragment.getExternalReferenceCode(),
			_randomFragment(
				false, true, postFragment.getExternalReferenceCode(),
				postFragment.getKey()),
			"unpublishing-a-fragment-entry-is-not-supported");
	}

	private void _testPutSiteFragmentUpdateApprovedToEmpty() throws Exception {
		Fragment postFragment = _postSiteFragmentSetFragment(
			_randomFragment(true, false));

		_testPutSiteFragmentProblemException(
			postFragment.getExternalReferenceCode(),
			_randomFragment(
				false, false, postFragment.getExternalReferenceCode(),
				postFragment.getKey()),
			"at-least-one-fragment-entry-version-is-required");
	}

	private void _testPutSiteFragmentUpdateDraft() throws Exception {
		Fragment postFragment = _postSiteFragmentSetFragment(
			_randomFragment(false, true));

		_testPutFragment(
			postFragment.getExternalReferenceCode(),
			_randomFragment(
				false, true, postFragment.getExternalReferenceCode(),
				postFragment.getKey()));
	}

	private void _testPutSiteFragmentUpdateDraftToApproved() throws Exception {
		Fragment postFragment = _postSiteFragmentSetFragment(
			_randomFragment(false, true));

		_testPutFragment(
			postFragment.getExternalReferenceCode(),
			_randomFragment(
				true, false, postFragment.getExternalReferenceCode(),
				postFragment.getKey()));
	}

	private void _testPutSiteFragmentUpdateDraftToEmptyProblemException()
		throws Exception {

		Fragment postFragment = _postSiteFragmentSetFragment(
			_randomFragment(false, true));

		_testPutSiteFragmentProblemException(
			postFragment.getExternalReferenceCode(),
			_randomFragment(
				false, false, postFragment.getExternalReferenceCode(),
				postFragment.getKey()),
			"at-least-one-fragment-entry-version-is-required");
	}

	private void _testPutSiteFragmentUpdateFragmentSetExisting()
		throws Exception {

		Fragment postFragment = _postSiteFragmentSetFragment(
			_randomFragment(true, false));

		Fragment fragment = _randomFragment(
			true, false, postFragment.getExternalReferenceCode(),
			postFragment.getKey());

		FragmentCollection fragmentCollection = _addFragmentCollection();

		fragment.setFragmentSetExternalReferenceCode(
			fragmentCollection.getExternalReferenceCode());

		Fragment putFragment = fragmentResource.putSiteFragment(
			testGroup.getExternalReferenceCode(),
			postFragment.getExternalReferenceCode(), fragment);

		Assert.assertEquals(
			fragmentCollection.getExternalReferenceCode(),
			putFragment.getFragmentSetExternalReferenceCode());
	}

	private void _testPutSiteFragmentUpdateFragmentSetExternalReferenceCodeNull()
		throws Exception {

		Fragment postFragment = _postSiteFragmentSetFragment(
			_randomFragment(true, false));

		Fragment fragment = _randomFragment(
			true, false, postFragment.getExternalReferenceCode(),
			postFragment.getKey());

		fragment.setFragmentSet(_randomFragmentSet(null));

		Fragment putFragment = fragmentResource.putSiteFragment(
			testGroup.getExternalReferenceCode(),
			postFragment.getExternalReferenceCode(), fragment);

		Assert.assertEquals(
			_fragmentCollection.getExternalReferenceCode(),
			putFragment.getFragmentSetExternalReferenceCode());
	}

	private void _testPutSiteFragmentUpdateFragmentSetNonexisting()
		throws Exception {

		Fragment postFragment = _postSiteFragmentSetFragment(
			_randomFragment(true, false));

		Fragment fragment = _randomFragment(
			true, false, postFragment.getExternalReferenceCode(),
			postFragment.getKey());

		String fragmentSetExternalReferenceCode = RandomTestUtil.randomString();

		FragmentSet fragmentSet = _randomFragmentSet(
			fragmentSetExternalReferenceCode);

		fragment.setFragmentSet(fragmentSet);

		fragment.setFragmentSetExternalReferenceCode(
			fragmentSetExternalReferenceCode);

		try (SafeCloseable safeCloseable =
				LazyReferencingTestUtil.setLazyReferencingWithSafeCloseable(
					true)) {

			Fragment putFragment = fragmentResource.putSiteFragment(
				testGroup.getExternalReferenceCode(),
				postFragment.getExternalReferenceCode(), fragment);

			Assert.assertEquals(
				fragmentSet.getExternalReferenceCode(),
				putFragment.getFragmentSetExternalReferenceCode());

			Assert.assertNotNull(
				_fragmentCollectionLocalService.
					fetchFragmentCollectionByExternalReferenceCode(
						fragmentSetExternalReferenceCode,
						testGroup.getGroupId()));
		}
	}

	private void _testPutSiteFragmentUpdateFragmentSetNonexistingProblemException()
		throws Exception {

		Fragment postFragment = _postSiteFragmentSetFragment(
			_randomFragment(true, false));

		Fragment fragment = _randomFragment(
			true, false, postFragment.getExternalReferenceCode(),
			postFragment.getKey());

		String fragmentSetExternalReferenceCode = RandomTestUtil.randomString();

		fragment.setFragmentSetExternalReferenceCode(
			fragmentSetExternalReferenceCode);

		_assertProblemException(
			"no-fragment-set-was-found-with-external-reference-code-x",
			() -> fragmentResource.putSiteFragment(
				testGroup.getExternalReferenceCode(),
				postFragment.getExternalReferenceCode(), fragment),
			fragmentSetExternalReferenceCode);

		Assert.assertNull(
			_fragmentCollectionLocalService.
				fetchFragmentCollectionByExternalReferenceCode(
					fragmentSetExternalReferenceCode, testGroup.getGroupId()));
	}

	private void _testPutSiteFragmentUpdateFragmentSetNull() throws Exception {
		Fragment postFragment = _postSiteFragmentSetFragment(
			_randomFragment(true, false));

		Fragment fragment = _randomFragment(
			true, false, postFragment.getExternalReferenceCode(),
			postFragment.getKey());

		fragment.setFragmentSet((FragmentSet)null);

		Fragment putFragment = fragmentResource.putSiteFragment(
			testGroup.getExternalReferenceCode(),
			postFragment.getExternalReferenceCode(), fragment);

		Assert.assertEquals(
			_fragmentCollection.getExternalReferenceCode(),
			putFragment.getFragmentSetExternalReferenceCode());
	}

	private void _testPutSiteFragmentUpdateThumbnailURLReferenceExternalReferenceCode()
		throws Exception {

		Fragment postFragment = _postSiteFragmentSetFragment(randomFragment());

		Assert.assertNull(postFragment.getThumbnailURLReference());

		ThumbnailURLReference thumbnailURLReference1 =
			new ThumbnailURLReference();

		FileEntry fileEntry1 = _addPortletFileEntry("thumbnail_1.png");

		String externalReferenceCode1 = fileEntry1.getExternalReferenceCode();

		thumbnailURLReference1.setExternalReferenceCode(externalReferenceCode1);

		Fragment putFragment = _putSiteFragmentAndAssertThumbnailURLReference(
			_thumbnail1Bytes, externalReferenceCode1, postFragment,
			thumbnailURLReference1);

		ThumbnailURLReference thumbnailURLReference2 =
			new ThumbnailURLReference();

		FileEntry fileEntry2 = _addPortletFileEntry("thumbnail_2.png");

		String externalReferenceCode2 = fileEntry2.getExternalReferenceCode();

		thumbnailURLReference2.setExternalReferenceCode(externalReferenceCode2);

		_putSiteFragmentAndAssertThumbnailURLReference(
			_thumbnail2Bytes, externalReferenceCode2, putFragment,
			thumbnailURLReference2);
	}

	private void _testPutSiteFragmentUpdateThumbnailURLReferenceExternalReferenceCodeAndFileBase64()
		throws Exception {

		Fragment postFragment = _postSiteFragmentSetFragment(randomFragment());

		Assert.assertNull(postFragment.getThumbnailURLReference());

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		FileEntry fileEntry = _addPortletFileEntry("thumbnail_1.png");

		String externalReferenceCode = fileEntry.getExternalReferenceCode();

		thumbnailURLReference.setExternalReferenceCode(externalReferenceCode);

		thumbnailURLReference.setFileBase64(_thumbnail2Base64);

		_putSiteFragmentAndAssertThumbnailURLReference(
			_thumbnail1Bytes, externalReferenceCode, postFragment,
			thumbnailURLReference);
	}

	private void _testPutSiteFragmentUpdateThumbnailURLReferenceFileBase64()
		throws Exception {

		Fragment postFragment = _postSiteFragmentSetFragment(randomFragment());

		Assert.assertNull(postFragment.getThumbnailURLReference());

		ThumbnailURLReference thumbnailURLReference1 =
			new ThumbnailURLReference();

		thumbnailURLReference1.setFileBase64(_thumbnail1Base64);

		Fragment putFragment = _putSiteFragmentAndAssertThumbnailURLReference(
			_thumbnail1Bytes, null, postFragment, thumbnailURLReference1);

		ThumbnailURLReference thumbnailURLReference2 =
			new ThumbnailURLReference();

		thumbnailURLReference2.setFileBase64(_thumbnail2Base64);

		_putSiteFragmentAndAssertThumbnailURLReference(
			_thumbnail2Bytes, null, putFragment, thumbnailURLReference2);
	}

	private void _testPutSiteFragmentUpdateThumbnailURLReferenceNull()
		throws Exception {

		Fragment postFragment = _postSiteFragmentSetFragment(randomFragment());

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.getFragmentEntryByExternalReferenceCode(
				postFragment.getExternalReferenceCode(),
				testGroup.getGroupId());

		FileEntry fileEntry = _addPortletFileEntry("thumbnail_1.png");

		_fragmentEntryLocalService.updateFragmentEntry(
			fragmentEntry.getFragmentEntryId(), fileEntry.getFileEntryId());

		fragmentResource.putSiteFragment(
			testGroup.getExternalReferenceCode(),
			postFragment.getExternalReferenceCode(), postFragment);

		fragmentEntry =
			_fragmentEntryLocalService.getFragmentEntryByExternalReferenceCode(
				postFragment.getExternalReferenceCode(),
				testGroup.getGroupId());

		Assert.assertEquals(0L, fragmentEntry.getPreviewFileEntryId());
	}

	private void _testPutSiteFragmentUpdateThumbnailURLReferenceURL()
		throws Exception {

		Fragment postFragment = _postSiteFragmentSetFragment(randomFragment());

		Assert.assertNull(postFragment.getThumbnailURLReference());

		ThumbnailURLReference thumbnailURLReference1 =
			new ThumbnailURLReference();

		thumbnailURLReference1.setUrl(_thumbnail1URL);

		Fragment putFragment = _putSiteFragmentAndAssertThumbnailURLReference(
			_thumbnail1Bytes, null, postFragment, thumbnailURLReference1);

		ThumbnailURLReference thumbnailURLReference2 =
			new ThumbnailURLReference();

		thumbnailURLReference2.setUrl(_thumbnail2URL);

		_putSiteFragmentAndAssertThumbnailURLReference(
			_thumbnail2Bytes, null, putFragment, thumbnailURLReference2);
	}

	private void _testPutSiteFragmentUpdateTypeProblemException()
		throws Exception {

		_testPutSiteFragmentUpdateTypeProblemException(
			randomFragment(), _randomFormFragment());
		_testPutSiteFragmentUpdateTypeProblemException(
			_randomFormFragment(), randomFragment());
	}

	private void _testPutSiteFragmentUpdateTypeProblemException(
			Fragment originalFragment, Fragment updatedFragment)
		throws Exception {

		Fragment postFragment = _postSiteFragmentSetFragment(originalFragment);

		updatedFragment.setExternalReferenceCode(
			postFragment.getExternalReferenceCode());
		updatedFragment.setKey(postFragment.getKey());

		_testPutSiteFragmentProblemException(
			postFragment.getExternalReferenceCode(), updatedFragment,
			"the-fragment-type-cannot-be-changed");
	}

	private void _testPutSiteFragmentWithFormFragment() throws Exception {
		_testPutSiteFragmentWithFormFragmentFieldTypes();
		_testPutSiteFragmentWithFormFragmentFieldTypesInvalidProblemException();
	}

	private void _testPutSiteFragmentWithFormFragmentFieldTypes()
		throws Exception {

		FormFragment formFragment = (FormFragment)_postSiteFragmentSetFragment(
			_randomFormFragment());

		FieldType[] fieldTypes = {FieldType.NUMBER, FieldType.TEXT};

		formFragment.setFieldTypes(fieldTypes);

		_assertFormFragment(
			fieldTypes,
			fragmentResource.putSiteFragment(
				testGroup.getExternalReferenceCode(),
				formFragment.getExternalReferenceCode(), formFragment));
	}

	private void _testPutSiteFragmentWithFormFragmentFieldTypesInvalidProblemException()
		throws Exception {

		_testPutSiteFragmentWithFormFragmentFieldTypesInvalidProblemException(
			null);
		_testPutSiteFragmentWithFormFragmentFieldTypesInvalidProblemException(
			new FieldType[0]);
		_testPutSiteFragmentWithFormFragmentFieldTypesInvalidProblemException(
			new FieldType[] {FieldType.CAPTCHA, FieldType.TEXT});
		_testPutSiteFragmentWithFormFragmentFieldTypesInvalidProblemException(
			new FieldType[] {FieldType.STEPPER, FieldType.TEXT});
	}

	private void
			_testPutSiteFragmentWithFormFragmentFieldTypesInvalidProblemException(
				FieldType[] fieldTypes)
		throws Exception {

		FormFragment formFragment = (FormFragment)_postSiteFragmentSetFragment(
			_randomFormFragment());

		formFragment.setFieldTypes(fieldTypes);

		_assertProblemException(
			"the-form-fragment-field-types-are-invalid",
			() -> fragmentResource.putSiteFragment(
				testGroup.getExternalReferenceCode(),
				formFragment.getExternalReferenceCode(), formFragment));
	}

	private FragmentSet _toFragmentSet(FragmentCollection fragmentCollection) {
		return new FragmentSet() {
			{
				setCreator(
					() -> {
						User user = _userLocalService.fetchUser(
							fragmentCollection.getUserId());

						if (user == null) {
							return null;
						}

						return new Creator() {
							{
								setExternalReferenceCode(
									user::getExternalReferenceCode);
							}
						};
					});
				setDateCreated(fragmentCollection::getCreateDate);
				setDateModified(fragmentCollection::getModifiedDate);
				setDescription(fragmentCollection::getDescription);
				setExternalReferenceCode(
					fragmentCollection::getExternalReferenceCode);
				setKey(fragmentCollection::getFragmentCollectionKey);
				setMarketplace(fragmentCollection::isMarketplace);
				setName(fragmentCollection::getName);
			}
		};
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

	private static HttpServer _httpServer;
	private static String _thumbnail1Base64;
	private static byte[] _thumbnail1Bytes;
	private static String _thumbnail1URL;
	private static String _thumbnail2Base64;
	private static byte[] _thumbnail2Bytes;
	private static String _thumbnail2URL;

	private FragmentCollection _fragmentCollection;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Inject
	private Language _language;

	@Inject
	private PortletFileRepository _portletFileRepository;

	@Inject
	private UserLocalService _userLocalService;

}