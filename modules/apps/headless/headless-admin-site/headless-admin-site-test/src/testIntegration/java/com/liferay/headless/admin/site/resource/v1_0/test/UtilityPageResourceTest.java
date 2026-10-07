/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.site.client.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.FriendlyUrlHistory;
import com.liferay.headless.admin.site.client.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.ThumbnailURLReference;
import com.liferay.headless.admin.site.client.dto.v1_0.UtilityPage;
import com.liferay.headless.admin.site.client.dto.v1_0.UtilityPageSEOSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.UtilityPageSettings;
import com.liferay.headless.admin.site.client.pagination.Page;
import com.liferay.headless.admin.site.client.problem.Problem;
import com.liferay.headless.admin.site.client.resource.v1_0.UtilityPageResource;
import com.liferay.headless.admin.site.resource.v1_0.test.util.FileEntryTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.LayoutUtilityPageEntryTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.PageSpecificationsTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.ThumbnailHttpServer;
import com.liferay.headless.admin.site.resource.v1_0.test.util.ThumbnailURLReferenceTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.ThumbnailURLReferenceUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.URLFetchSecurityCompanyConfigurationUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryLocalService;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.search.test.util.IdempotentRetryAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import java.text.DateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rubén Pulido
 */
@RunWith(Arquillian.class)
public class UtilityPageResourceTest extends BaseUtilityPageResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseUtilityPageResourceTestCase.setUpClass();

		_thumbnailHttpServer = ThumbnailHttpServer.start(
			UtilityPageResourceTest.class);

		_thumbnail1Base64 = _thumbnailHttpServer.getThumbnail1Base64();
		_thumbnail1Bytes = _thumbnailHttpServer.getThumbnail1Bytes();
		_thumbnail1URL = _thumbnailHttpServer.getThumbnail1URL();
		_thumbnail2Base64 = _thumbnailHttpServer.getThumbnail2Base64();
		_thumbnail2Bytes = _thumbnailHttpServer.getThumbnail2Bytes();
		_thumbnail2URL = _thumbnailHttpServer.getThumbnail2URL();
	}

	@AfterClass
	public static void tearDownClass() {
		if (_thumbnailHttpServer != null) {
			_thumbnailHttpServer.stop();
		}
	}

	@Ignore
	@Override
	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		super.testBatchEngineDeleteImportTask();
	}

	@Override
	@Test
	public void testDeleteSiteUtilityPage() throws Exception {
		UtilityPage postUtilityPage = testPostSiteUtilityPage_addUtilityPage(
			randomUtilityPage());

		Assert.assertNotNull(
			_layoutUtilityPageEntryLocalService.
				fetchLayoutUtilityPageEntryByExternalReferenceCode(
					postUtilityPage.getExternalReferenceCode(),
					testGroup.getGroupId()));

		utilityPageResource.deleteSiteUtilityPage(
			testGroup.getExternalReferenceCode(),
			postUtilityPage.getExternalReferenceCode());

		Assert.assertNull(
			_layoutUtilityPageEntryLocalService.
				fetchLayoutUtilityPageEntryByExternalReferenceCode(
					postUtilityPage.getExternalReferenceCode(),
					testGroup.getGroupId()));

		_assertProblemException(
			"NOT_FOUND", null,
			() -> utilityPageResource.deleteSiteUtilityPage(
				testGroup.getExternalReferenceCode(),
				postUtilityPage.getExternalReferenceCode()));
	}

	@Override
	@Test
	public void testGetSiteUtilityPage() throws Exception {
		UtilityPage utilityPage = randomUtilityPage();

		utilityPage.setMarkedAsDefault(false);

		UtilityPage postUtilityPage = testPostSiteUtilityPage_addUtilityPage(
			utilityPage);

		UtilityPage getUtilityPage = utilityPageResource.getSiteUtilityPage(
			testGroup.getExternalReferenceCode(),
			postUtilityPage.getExternalReferenceCode());

		assertEquals(postUtilityPage, getUtilityPage);
		assertValid(getUtilityPage);

		_assertProblemException(
			"NOT_FOUND", null,
			() -> utilityPageResource.getSiteUtilityPage(
				testGroup.getExternalReferenceCode(),
				RandomTestUtil.randomString()));

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryLocalService.
				getLayoutUtilityPageEntryByExternalReferenceCode(
					postUtilityPage.getExternalReferenceCode(),
					testGroup.getGroupId());

		Layout layout = _layoutLocalService.getLayout(
			layoutUtilityPageEntry.getPlid());

		Assert.assertFalse(layout.isPublished());

		UtilityPageResource utilityPageResource = _getUtilityPageResource();

		_assertNestedFields(
			utilityPageResource.getSiteUtilityPage(
				testGroup.getExternalReferenceCode(),
				postUtilityPage.getExternalReferenceCode()));

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		Assert.assertTrue(layout.isPublished());

		_assertNestedFields(
			utilityPageResource.getSiteUtilityPage(
				testGroup.getExternalReferenceCode(),
				postUtilityPage.getExternalReferenceCode()));

		try (PageSpecificationsTestUtil.ExpandoTableAutocloseable
				expandoTableAutoCloseable =
					PageSpecificationsTestUtil.getExpandoTableAutoCloseable()) {

			_assertNestedFields(
				utilityPageResource.getSiteUtilityPage(
					testGroup.getExternalReferenceCode(),
					postUtilityPage.getExternalReferenceCode()));
		}
	}

	@Override
	@Test
	public void testGetSiteUtilityPagesPage() throws Exception {
		super.testGetSiteUtilityPagesPage();

		_testGetSiteUtilityPagesPageWithPageSpecificationsAsNestedFields();
		_testGetSiteUtilityPagesPageWithThumbnailAsNestedField();
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteUtilityPagesPageWithSortDateTime() throws Exception {
		super.testGetSiteUtilityPagesPageWithSortDateTime();
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteUtilityPagesPageWithSortDouble() throws Exception {
		super.testGetSiteUtilityPagesPageWithSortDouble();
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteUtilityPagesPageWithSortInteger() throws Exception {
		super.testGetSiteUtilityPagesPageWithSortInteger();
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteUtilityPagesPageWithSortString() throws Exception {
		super.testGetSiteUtilityPagesPageWithSortString();
	}

	@Override
	@Test
	@TestInfo("LPD-92443")
	public void testPatchSiteUtilityPage() throws Exception {
		LayoutUtilityPageEntry layoutUtilityPageEntry =
			LayoutUtilityPageEntryTestUtil.getLayoutUtilityPageEntry(
				ServiceContextTestUtil.getServiceContext(
					testGroup.getGroupId(), TestPropsValues.getUserId()));

		UtilityPage utilityPage = utilityPageResource.getSiteUtilityPage(
			testGroup.getExternalReferenceCode(),
			layoutUtilityPageEntry.getExternalReferenceCode());

		_testPatchSiteUtilityPage(
			Boolean.FALSE, utilityPage,
			new UtilityPage() {
				{
					setMarkedAsDefault(Boolean.FALSE);
				}
			});

		Layout layout = _layoutLocalService.getLayout(
			layoutUtilityPageEntry.getPlid());

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		_testPatchSiteUtilityPage(
			Boolean.TRUE, utilityPage,
			new UtilityPage() {
				{
					setMarkedAsDefault(Boolean.TRUE);
				}
			});

		utilityPage.setName(RandomTestUtil::randomString);
		utilityPage.setUtilityPageSettings(
			() -> new UtilityPageSettings() {
				{
					setSeoSettings(
						() -> new UtilityPageSEOSettings() {
							{
								setDescription_i18n(
									() -> LocalizedMapUtil.getI18nMap(
										true,
										RandomTestUtil.
											randomLocaleStringMap()));
								setHtmlTitle_i18n(
									() -> LocalizedMapUtil.getI18nMap(
										true,
										RandomTestUtil.
											randomLocaleStringMap()));
							}
						});
				}
			});

		_testPatchSiteUtilityPage(
			Boolean.TRUE, utilityPage,
			new UtilityPage() {
				{
					setName(utilityPage::getName);
					setUtilityPageSettings(utilityPage::getUtilityPageSettings);
				}
			});

		_testPatchSiteUtilityPageWithPageSpecifications();
		_testPatchSiteUtilityPageWithThumbnail();

		_assertProblemException(
			"NOT_FOUND", null,
			() -> utilityPageResource.patchSiteUtilityPage(
				testGroup.getExternalReferenceCode(),
				RandomTestUtil.randomString(), randomUtilityPage()));
	}

	@Override
	@Test
	@TestInfo({"LPD-48984", "LPD-92443", "LPD-107149"})
	public void testPostSiteUtilityPage() throws Exception {
		super.testPostSiteUtilityPage();

		_testPostSiteUtilityPageWithPageSpecifications();
		_testPostSiteUtilityPageWithThumbnailURLReferenceExternalReferenceCodeAndFileBase64();
		_testPostSiteUtilityPageWithThumbnailURLReferenceExternalReferenceCodeEmptyAndFileBase64();
		_testPostSiteUtilityPageWithThumbnailURLReferenceExternalReferenceCodeNullAndFileBase64();
		_testPostSiteUtilityPageWithThumbnailURLReferenceExternalReferenceCodeNullAndURL();
		_testPostSiteUtilityPageWithThumbnailURLReferenceFileBase64();
		_testPostSiteUtilityPageWithThumbnailURLReferenceFileBase64AndURL();
		_testPostSiteUtilityPageWithThumbnailURLReferenceNonexistingProblemException();
		_testPostSiteUtilityPageWithThumbnailURLReferenceURL();
		_testPostSiteUtilityPageWithThumbnailURLReferenceURLFetchSecurity();
		_testPostSiteUtilityPageWithThumbnailURLReferenceURLUnreachableProblemException();
		_testPostSiteUtilityPageWithThumbnailURLReferenceURLUnsupportedProtocolProblemException();
	}

	@Override
	@Test
	@TestInfo("LPD-48987")
	public void testPostSiteUtilityPagePageSpecification() throws Exception {
		UtilityPageResource utilityPageResource = _getUtilityPageResource();

		UtilityPage utilityPage = utilityPageResource.postSiteUtilityPage(
			testGroup.getExternalReferenceCode(),
			_getUtilityPage(
				null, Boolean.FALSE, RandomTestUtil.randomString()));

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryLocalService.
				getLayoutUtilityPageEntryByExternalReferenceCode(
					utilityPage.getExternalReferenceCode(),
					testGroup.getGroupId());

		PageSpecificationsTestUtil.testPostSitePageSpecification(
			_layoutLocalService.getLayout(layoutUtilityPageEntry.getPlid()),
			utilityPage.getPageSpecifications(),
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId()),
			contentPageSpecification ->
				utilityPageResource.postSiteUtilityPagePageSpecification(
					testGroup.getExternalReferenceCode(),
					utilityPage.getExternalReferenceCode(),
					contentPageSpecification));
	}

	@Override
	@Test
	@TestInfo({"LPD-42587", "LPD-48987", "LPD-86647", "LPD-92443"})
	public void testPutSiteUtilityPage() throws Exception {
		_testPutSiteUtilityPage(Boolean.FALSE, randomUtilityPage());

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			LayoutUtilityPageEntryTestUtil.getLayoutUtilityPageEntry(
				ServiceContextTestUtil.getServiceContext(
					testGroup.getGroupId(), TestPropsValues.getUserId()));

		Layout layout = _layoutLocalService.getLayout(
			layoutUtilityPageEntry.getPlid());

		Assert.assertFalse(layout.isPublished());

		_assertProblemException(
			"BAD_REQUEST", null,
			() -> _testPutSiteUtilityPage(
				Boolean.TRUE,
				_getUtilityPage(
					null, Boolean.TRUE,
					layoutUtilityPageEntry.getExternalReferenceCode())));

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		_testPutSiteUtilityPage(
			Boolean.FALSE,
			_getUtilityPage(
				FileEntryTestUtil.addPreviewFileEntry(
					testGroup, _portletFileRepository, getClass()),
				Boolean.FALSE,
				layoutUtilityPageEntry.getExternalReferenceCode()));
		_testPutSiteUtilityPage(
			Boolean.TRUE,
			_getUtilityPage(
				FileEntryTestUtil.addPreviewFileEntry(
					testGroup, _portletFileRepository, getClass()),
				Boolean.TRUE,
				layoutUtilityPageEntry.getExternalReferenceCode()));

		_testPutSiteUtilityPage(
			Boolean.FALSE,
			_getUtilityPage(
				null, null, layoutUtilityPageEntry.getExternalReferenceCode()));

		_testPutSiteUtilityPageWithDates();
		_testPutSiteUtilityPageWithPageSpecifications();
		_testPutSiteUtilityPageWithThumbnail();
		_testPutSiteUtilityPageWithThumbnailURLReferenceExternalReferenceCodeAndFileBase64();
		_testPutSiteUtilityPageWithThumbnailURLReferenceFileBase64();
		_testPutSiteUtilityPageWithThumbnailURLReferenceURL();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"externalReferenceCode", "friendlyUrlPath_i18n", "name",
			"utilityPageSettings"
		};
	}

	@Override
	protected UtilityPage randomUtilityPage() throws Exception {
		UtilityPage utilityPage = super.randomUtilityPage();

		utilityPage.setFriendlyUrlPath_i18n(
			() -> HashMapBuilder.put(
				LocaleUtil.toBCP47LanguageId(LocaleUtil.SPAIN),
				StringPool.FORWARD_SLASH +
					StringUtil.toLowerCase(RandomTestUtil.randomString())
			).put(
				LocaleUtil.toBCP47LanguageId(LocaleUtil.US),
				StringPool.FORWARD_SLASH +
					StringUtil.toLowerCase(RandomTestUtil.randomString())
			).build());
		utilityPage.setMarkedAsDefault(Boolean.FALSE);
		utilityPage.setType(UtilityPage.Type.ERROR);
		utilityPage.setUtilityPageSettings(
			() -> new UtilityPageSettings() {
				{
					setSeoSettings(
						() -> new UtilityPageSEOSettings() {
							{
								setDescription_i18n(
									() -> LocalizedMapUtil.getI18nMap(
										true,
										RandomTestUtil.
											randomLocaleStringMap()));
								setHtmlTitle_i18n(
									() -> LocalizedMapUtil.getI18nMap(
										true,
										RandomTestUtil.
											randomLocaleStringMap()));
							}
						});
				}
			});

		return utilityPage;
	}

	@Override
	protected UtilityPage testGetSiteUtilityPagesPage_addUtilityPage(
			String siteExternalReferenceCode, UtilityPage utilityPage)
		throws Exception {

		return utilityPageResource.postSiteUtilityPage(
			siteExternalReferenceCode, utilityPage);
	}

	@Override
	protected Map<String, Map<String, String>>
		testGetSiteUtilityPagesPage_getExpectedActions(
			String siteExternalReferenceCode) {

		return new HashMap<>();
	}

	@Override
	protected UtilityPage testPostSiteUtilityPage_addUtilityPage(
			UtilityPage utilityPage)
		throws Exception {

		return testGetSiteUtilityPagesPage_addUtilityPage(
			testGroup.getExternalReferenceCode(), utilityPage);
	}

	private void _assertNestedFields(UtilityPage utilityPage) throws Exception {
		FriendlyUrlHistory friendlyUrlHistory =
			utilityPage.getFriendlyUrlHistory();

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			GetterUtil.getString(friendlyUrlHistory.getFriendlyUrlPath_i18n()));

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryLocalService.
				getLayoutUtilityPageEntryByExternalReferenceCode(
					utilityPage.getExternalReferenceCode(),
					testGroup.getGroupId());

		Layout layout = _layoutLocalService.getLayout(
			layoutUtilityPageEntry.getPlid());

		Map<Locale, String> friendlyURLMap = new HashMap<>();

		if (layout.isPublished()) {
			friendlyURLMap = layout.getFriendlyURLMap();
		}

		Assert.assertEquals(
			jsonObject.toString(), friendlyURLMap.size(), jsonObject.length());

		for (Map.Entry<Locale, String> entry : friendlyURLMap.entrySet()) {
			String key = LocaleUtil.toBCP47LanguageId(entry.getKey());

			JSONArray jsonArray = jsonObject.getJSONArray(key);

			Assert.assertEquals(jsonArray.toString(), 1, jsonArray.length());
			Assert.assertEquals(
				jsonArray.toString(), entry.getValue(), jsonArray.getString(0));
		}

		PageSpecificationsTestUtil.assertPageSpecifications(
			layout, utilityPage.getPageSpecifications());
	}

	private void _assertPageSpecifications(
			UtilityPage utilityPage,
			ContentPageSpecification draftContentPageSpecification,
			ContentPageSpecification publishedContentPageSpecification)
		throws Exception {

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryLocalService.
				getLayoutUtilityPageEntryByExternalReferenceCode(
					utilityPage.getExternalReferenceCode(),
					testGroup.getGroupId());

		Layout layout = _layoutLocalService.getLayout(
			layoutUtilityPageEntry.getPlid());

		PageSpecification.Status status = PageSpecification.Status.APPROVED;

		if (!layout.isPublished()) {
			status = PageSpecification.Status.DRAFT;
		}

		PageSpecificationsTestUtil.assertPageSpecifications(
			draftContentPageSpecification, publishedContentPageSpecification,
			utilityPage.getPageSpecifications(), layout, status);
	}

	private void _assertProblemException(
			String expectedStatus, String expectedTitle,
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try {
			unsafeRunnable.run();

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals(expectedStatus, problem.getStatus());
			Assert.assertEquals(expectedTitle, problem.getTitle());
		}
	}

	private void _assertThumbnailFileEntryId(
			Boolean defaultValue, String thumbnailExternalReferenceCode,
			String utilityPageExternalReferenceCode)
		throws Exception {

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryLocalService.
				getLayoutUtilityPageEntryByExternalReferenceCode(
					utilityPageExternalReferenceCode, testGroup.getGroupId());

		long fileEntryId = 0;

		if (!defaultValue) {
			FileEntry fileEntry =
				_portletFileRepository.
					getPortletFileEntryByExternalReferenceCode(
						thumbnailExternalReferenceCode, testGroup.getGroupId());

			fileEntryId = fileEntry.getFileEntryId();
		}

		Assert.assertEquals(
			layoutUtilityPageEntry.getPreviewFileEntryId(), fileEntryId);
	}

	private void _assertThumbnailURLReference(
			byte[] expectedBytes, String expectedExternalReferenceCode,
			UtilityPage utilityPage)
		throws Exception {

		Assert.assertNotNull(expectedExternalReferenceCode);

		ThumbnailURLReference thumbnailURLReference =
			utilityPage.getThumbnailURLReference();

		Assert.assertEquals(
			expectedExternalReferenceCode,
			thumbnailURLReference.getExternalReferenceCode());

		_assertThumbnailFileEntryId(
			false, expectedExternalReferenceCode,
			utilityPage.getExternalReferenceCode());

		FileEntry fileEntry =
			PortletFileRepositoryUtil.
				fetchPortletFileEntryByExternalReferenceCode(
					expectedExternalReferenceCode, testGroup.getGroupId());

		try (InputStream inputStream = fileEntry.getContentStream()) {
			Assert.assertArrayEquals(
				expectedBytes, StreamUtil.toByteArray(inputStream));
		}

		UtilityPageResource utilityPageResource = _getUtilityPageResource();

		URL url = new URL(
			IdempotentRetryAssert.retryAssert(
				30, TimeUnit.SECONDS, 500, TimeUnit.MILLISECONDS,
				() -> {
					UtilityPage getUtilityPage =
						utilityPageResource.getSiteUtilityPage(
							testGroup.getExternalReferenceCode(),
							utilityPage.getExternalReferenceCode());

					ThumbnailURLReference getThumbnailURLReference =
						getUtilityPage.getThumbnailURLReference();

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

	private UtilityPage _getUtilityPage(
			FileEntry fileEntry, Boolean markedAsDefault,
			String utilityPageExternalReferenceCode)
		throws Exception {

		UtilityPage utilityPage = randomUtilityPage();

		utilityPage.setExternalReferenceCode(utilityPageExternalReferenceCode);
		utilityPage.setMarkedAsDefault(markedAsDefault);

		if (fileEntry != null) {
			utilityPage.setThumbnailURLReference(
				() -> new ThumbnailURLReference() {
					{
						setExternalReferenceCode(
							fileEntry::getExternalReferenceCode);
						setUrl(RandomTestUtil.randomString());
					}
				});
		}

		return utilityPage;
	}

	private UtilityPage _getUtilityPage(
		String externalReferenceCode, List<UtilityPage> utilityPages) {

		for (UtilityPage utilityPage : utilityPages) {
			if (Objects.equals(
					utilityPage.getExternalReferenceCode(),
					externalReferenceCode)) {

				return utilityPage;
			}
		}

		return null;
	}

	private UtilityPageResource _getUtilityPageResource() throws Exception {
		User user = UserTestUtil.getAdminUser(testCompany.getCompanyId());

		return UtilityPageResource.builder(
		).authentication(
			user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).parameters(
			"nestedFields",
			"friendlyUrlHistory,pageSpecifications,thumbnailURLReference"
		).build();
	}

	private UtilityPage _postSiteUtilityPageAndAssertThumbnailURLReference(
			byte[] expectedBytes, String expectedExternalReferenceCode,
			ThumbnailURLReference thumbnailURLReference)
		throws Exception {

		UtilityPage utilityPage = randomUtilityPage();

		utilityPage.setThumbnailURLReference(thumbnailURLReference);

		UtilityPageResource utilityPageResource = _getUtilityPageResource();

		UtilityPage postUtilityPage =
			URLFetchSecurityCompanyConfigurationUtil.
				swapURLLocalNetworkAccessEnabled(
					true,
					() -> utilityPageResource.postSiteUtilityPage(
						testGroup.getExternalReferenceCode(), utilityPage));

		if (expectedExternalReferenceCode == null) {
			ThumbnailURLReference postThumbnailURLReference =
				postUtilityPage.getThumbnailURLReference();

			expectedExternalReferenceCode =
				postThumbnailURLReference.getExternalReferenceCode();
		}

		_assertThumbnailURLReference(
			expectedBytes, expectedExternalReferenceCode, postUtilityPage);

		return postUtilityPage;
	}

	private UtilityPage _putSiteUtilityPageAndAssertThumbnailURLReference(
			byte[] expectedBytes, String expectedExternalReferenceCode,
			UtilityPage utilityPage,
			ThumbnailURLReference thumbnailURLReference)
		throws Exception {

		utilityPage.setThumbnailURLReference(thumbnailURLReference);

		UtilityPageResource utilityPageResource = _getUtilityPageResource();

		UtilityPage putUtilityPage =
			URLFetchSecurityCompanyConfigurationUtil.
				swapURLLocalNetworkAccessEnabled(
					true,
					() -> utilityPageResource.putSiteUtilityPage(
						testGroup.getExternalReferenceCode(),
						utilityPage.getExternalReferenceCode(), utilityPage));

		if (expectedExternalReferenceCode == null) {
			ThumbnailURLReference putThumbnailURLReference =
				putUtilityPage.getThumbnailURLReference();

			expectedExternalReferenceCode =
				putThumbnailURLReference.getExternalReferenceCode();
		}

		_assertThumbnailURLReference(
			expectedBytes, expectedExternalReferenceCode, putUtilityPage);

		return putUtilityPage;
	}

	private UtilityPage _randomUtilityPage(
			ThumbnailURLReference thumbnailURLReference)
		throws Exception {

		UtilityPage utilityPage = randomUtilityPage();

		utilityPage.setThumbnailURLReference(thumbnailURLReference);

		return utilityPage;
	}

	private void _testGetSiteUtilityPagesPageWithPageSpecificationsAsNestedFields()
		throws Exception {

		Page<UtilityPage> page = utilityPageResource.getSiteUtilityPagesPage(
			testGroup.getExternalReferenceCode(), null, null, null, null, null);

		long totalCount = page.getTotalCount();

		UtilityPage utilityPage = randomUtilityPage();

		utilityPage.setMarkedAsDefault(false);

		testGetSiteUtilityPagesPage_addUtilityPage(
			testGroup.getExternalReferenceCode(), utilityPage);

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryLocalService.
				getLayoutUtilityPageEntryByExternalReferenceCode(
					utilityPage.getExternalReferenceCode(),
					testGroup.getGroupId());

		Layout layout = _layoutLocalService.getLayout(
			layoutUtilityPageEntry.getPlid());

		Assert.assertFalse(layout.isPublished());

		UtilityPageResource utilityPageResource = _getUtilityPageResource();

		page = utilityPageResource.getSiteUtilityPagesPage(
			testGroup.getExternalReferenceCode(), null, null, null, null, null);

		Assert.assertEquals(totalCount + 1, page.getTotalCount());

		_assertNestedFields(
			_getUtilityPage(
				utilityPage.getExternalReferenceCode(),
				(List<UtilityPage>)page.getItems()));

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		Assert.assertTrue(layout.isPublished());

		page = utilityPageResource.getSiteUtilityPagesPage(
			testGroup.getExternalReferenceCode(), null, null, null, null, null);

		Assert.assertEquals(totalCount + 1, page.getTotalCount());

		_assertNestedFields(
			_getUtilityPage(
				utilityPage.getExternalReferenceCode(),
				(List<UtilityPage>)page.getItems()));
	}

	private void _testGetSiteUtilityPagesPageWithThumbnailAsNestedField()
		throws Exception {

		UtilityPage randomUtilityPage = randomUtilityPage();

		FileEntry fileEntry = FileEntryTestUtil.addPreviewFileEntry(
			testGroup, _portletFileRepository, getClass());

		randomUtilityPage.setThumbnailURLReference(
			() -> ThumbnailURLReferenceUtil.getThumbnailURLReference(
				fileEntry, null));

		UtilityPage postUtilityPage = testPostSiteUtilityPage_addUtilityPage(
			randomUtilityPage);

		UtilityPageResource utilityPageResource = _getUtilityPageResource();

		IdempotentRetryAssert.retryAssert(
			30, TimeUnit.SECONDS, 500, TimeUnit.MILLISECONDS,
			() -> {
				UtilityPage getUtilityPage =
					utilityPageResource.getSiteUtilityPage(
						testGroup.getExternalReferenceCode(),
						postUtilityPage.getExternalReferenceCode());

				ThumbnailURLReference thumbnailURLReference =
					getUtilityPage.getThumbnailURLReference();

				Assert.assertNotNull(thumbnailURLReference);

				return thumbnailURLReference.getUrl();
			});

		Page<UtilityPage> page = utilityPageResource.getSiteUtilityPagesPage(
			testGroup.getExternalReferenceCode(), null, null, null, null, null);

		for (UtilityPage utilityPage : page.getItems()) {
			if (StringUtil.equals(
					postUtilityPage.getExternalReferenceCode(),
					utilityPage.getExternalReferenceCode())) {

				ThumbnailURLReference thumbnail =
					utilityPage.getThumbnailURLReference();

				_assertThumbnailURLReference(
					_thumbnail1Bytes, thumbnail.getExternalReferenceCode(),
					utilityPage);
			}
			else {
				Assert.assertNull(utilityPage.getThumbnailURLReference());
			}
		}
	}

	private void _testPatchSiteUtilityPage(
			Boolean expectedMarkedAsDefault, UtilityPage expectedUtilityPage,
			UtilityPage utilityPage)
		throws Exception {

		UtilityPage patchUtilityPage = utilityPageResource.patchSiteUtilityPage(
			testGroup.getExternalReferenceCode(),
			expectedUtilityPage.getExternalReferenceCode(), utilityPage);

		assertEquals(expectedUtilityPage, patchUtilityPage);
		assertValid(patchUtilityPage);

		Assert.assertEquals(
			expectedMarkedAsDefault, patchUtilityPage.getMarkedAsDefault());
		Assert.assertEquals(
			expectedUtilityPage.getThumbnailURLReference(),
			patchUtilityPage.getThumbnailURLReference());
	}

	private void _testPatchSiteUtilityPageWithPageSpecifications()
		throws Exception {

		_testPatchSiteUtilityPageWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT,
			PageSpecification.Status.APPROVED);
		_testPatchSiteUtilityPageWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT,
			PageSpecification.Status.DRAFT);
		_testPatchSiteUtilityPageWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED);
		_testPatchSiteUtilityPageWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.DRAFT,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT);

		try (PageSpecificationsTestUtil.ExpandoTableAutocloseable
				expandoTableAutoCloseable =
					PageSpecificationsTestUtil.getExpandoTableAutoCloseable()) {

			UtilityPageResource utilityPageResource = _getUtilityPageResource();

			UtilityPage utilityPage = utilityPageResource.postSiteUtilityPage(
				testGroup.getExternalReferenceCode(), randomUtilityPage());

			_assertNestedFields(utilityPage);

			PageSpecification[] pageSpecifications =
				utilityPage.getPageSpecifications();

			PageSpecification pageSpecification = pageSpecifications[0];

			_assertProblemException(
				"BAD_REQUEST", "Utility pages do not support custom fields",
				() -> utilityPageResource.patchSiteUtilityPage(
					testGroup.getExternalReferenceCode(),
					utilityPage.getExternalReferenceCode(),
					new UtilityPage() {
						{
							setPageSpecifications(
								() ->
									PageSpecificationsTestUtil.
										getContentPageSpecifications(
											pageSpecification.
												getExternalReferenceCode(),
											testGroup.getGroupId()));
						}
					}));
		}
	}

	private void _testPatchSiteUtilityPageWithPageSpecifications(
			PageSpecification.Status newDraftLayoutStatus,
			PageSpecification.Status newPublishedLayoutStatus,
			PageSpecification.Status oldDraftLayoutStatus,
			PageSpecification.Status oldPublishedLayoutStatus)
		throws Exception {

		UtilityPage utilityPage = _getUtilityPage(
			null, Boolean.FALSE, RandomTestUtil.randomString());

		ContentPageSpecification draftContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				null, testGroup.getGroupId(), oldDraftLayoutStatus);

		ContentPageSpecification publishedContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				draftContentPageSpecification.getExternalReferenceCode(),
				testGroup.getGroupId(), oldPublishedLayoutStatus);

		utilityPage.setPageSpecifications(
			() -> new PageSpecification[] {
				publishedContentPageSpecification, draftContentPageSpecification
			});

		UtilityPageResource utilityPageResource = _getUtilityPageResource();

		UtilityPage postUtilityPage = utilityPageResource.postSiteUtilityPage(
			testGroup.getExternalReferenceCode(), utilityPage);

		_assertPageSpecifications(
			postUtilityPage, draftContentPageSpecification,
			publishedContentPageSpecification);

		draftContentPageSpecification.setStatus(newDraftLayoutStatus);
		publishedContentPageSpecification.setStatus(newPublishedLayoutStatus);

		_assertPageSpecifications(
			utilityPageResource.patchSiteUtilityPage(
				testGroup.getExternalReferenceCode(),
				utilityPage.getExternalReferenceCode(),
				new UtilityPage() {
					{
						setPageSpecifications(
							() -> new PageSpecification[] {
								publishedContentPageSpecification,
								draftContentPageSpecification
							});
					}
				}),
			draftContentPageSpecification, publishedContentPageSpecification);
	}

	private void _testPatchSiteUtilityPageWithThumbnail() throws Exception {
		UtilityPage utilityPage1 = randomUtilityPage();

		FileEntry fileEntry = FileEntryTestUtil.addPreviewFileEntry(
			testGroup, _portletFileRepository, getClass());

		utilityPage1.setThumbnailURLReference(
			() -> ThumbnailURLReferenceUtil.getThumbnailURLReference(
				fileEntry, null));

		UtilityPageResource utilityPageResource = _getUtilityPageResource();

		UtilityPage postUtilityPage = utilityPageResource.postSiteUtilityPage(
			testGroup.getExternalReferenceCode(), utilityPage1);

		Assert.assertEquals(
			utilityPage1.getExternalReferenceCode(),
			postUtilityPage.getExternalReferenceCode());

		_assertThumbnailURLReference(
			_thumbnail1Bytes, fileEntry.getExternalReferenceCode(),
			postUtilityPage);

		FileEntry newFileEntry = FileEntryTestUtil.addPreviewFileEntry(
			testGroup, _portletFileRepository, getClass());

		utilityPage1.setThumbnailURLReference(
			() -> ThumbnailURLReferenceUtil.getThumbnailURLReference(
				newFileEntry, null));

		UtilityPage patchUtilityPage = utilityPageResource.patchSiteUtilityPage(
			testGroup.getExternalReferenceCode(),
			utilityPage1.getExternalReferenceCode(), utilityPage1);

		_assertThumbnailURLReference(
			_thumbnail1Bytes, newFileEntry.getExternalReferenceCode(),
			patchUtilityPage);

		UtilityPage utilityPage2 = randomUtilityPage();

		ThumbnailURLReference thumbnailURLReference =
			ThumbnailURLReferenceUtil.getRandomThumbnailURLReference();

		utilityPage2.setThumbnailURLReference(thumbnailURLReference);

		try {
			utilityPageResource.patchSiteUtilityPage(
				testGroup.getExternalReferenceCode(),
				utilityPage1.getExternalReferenceCode(), utilityPage2);
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(
				"Unable to download file from " +
					thumbnailURLReference.getUrl() +
						" because of restricted host invalid.example.test",
				problem.getTitle());
		}
	}

	private void _testPostSiteUtilityPageWithPageSpecifications()
		throws Exception {

		_testPostSiteUtilityPageWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED);
		_testPostSiteUtilityPageWithPageSpecifications(
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT);
		_testPostSiteUtilityPageWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.APPROVED);
		_testPostSiteUtilityPageWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.DRAFT);

		try (PageSpecificationsTestUtil.ExpandoTableAutocloseable
				expandoTableAutoCloseable =
					PageSpecificationsTestUtil.getExpandoTableAutoCloseable()) {

			UtilityPageResource utilityPageResource = _getUtilityPageResource();

			UtilityPage utilityPage = randomUtilityPage();

			utilityPage.setPageSpecifications(
				() -> PageSpecificationsTestUtil.getContentPageSpecifications(
					RandomTestUtil.randomString(), testGroup.getGroupId()));

			_assertProblemException(
				"BAD_REQUEST", "Utility pages do not support custom fields",
				() -> utilityPageResource.postSiteUtilityPage(
					testGroup.getExternalReferenceCode(), utilityPage));
		}
	}

	private void _testPostSiteUtilityPageWithPageSpecifications(
			PageSpecification.Status draftLayoutStatus,
			PageSpecification.Status publishedLayoutStatus)
		throws Exception {

		UtilityPage utilityPage = _getUtilityPage(
			null, Boolean.FALSE, RandomTestUtil.randomString());

		ContentPageSpecification draftContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				null, testGroup.getGroupId(), draftLayoutStatus);

		ContentPageSpecification publishedContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				draftContentPageSpecification.getExternalReferenceCode(),
				testGroup.getGroupId(), publishedLayoutStatus);

		utilityPage.setPageSpecifications(
			() -> new PageSpecification[] {
				publishedContentPageSpecification, draftContentPageSpecification
			});

		UtilityPageResource utilityPageResource = _getUtilityPageResource();

		_assertPageSpecifications(
			utilityPageResource.postSiteUtilityPage(
				testGroup.getExternalReferenceCode(), utilityPage),
			draftContentPageSpecification, publishedContentPageSpecification);
	}

	private void _testPostSiteUtilityPageWithThumbnailURLReferenceExternalReferenceCodeAndFileBase64()
		throws Exception {

		FileEntry fileEntry = FileEntryTestUtil.addPreviewFileEntry(
			testGroup, _portletFileRepository, getClass());

		String externalReferenceCode = fileEntry.getExternalReferenceCode();

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setExternalReferenceCode(externalReferenceCode);
		thumbnailURLReference.setFileBase64(_thumbnail2Base64);

		_postSiteUtilityPageAndAssertThumbnailURLReference(
			_thumbnail1Bytes, externalReferenceCode, thumbnailURLReference);
	}

	private void _testPostSiteUtilityPageWithThumbnailURLReferenceExternalReferenceCodeEmptyAndFileBase64()
		throws Exception {

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setExternalReferenceCode(StringPool.BLANK);
		thumbnailURLReference.setFileBase64(_thumbnail1Base64);

		_postSiteUtilityPageAndAssertThumbnailURLReference(
			_thumbnail1Bytes, null, thumbnailURLReference);
	}

	private void _testPostSiteUtilityPageWithThumbnailURLReferenceExternalReferenceCodeNullAndFileBase64()
		throws Exception {

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setFileBase64(_thumbnail1Base64);

		_postSiteUtilityPageAndAssertThumbnailURLReference(
			_thumbnail1Bytes, null, thumbnailURLReference);
	}

	private void _testPostSiteUtilityPageWithThumbnailURLReferenceExternalReferenceCodeNullAndURL()
		throws Exception {

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setUrl(_thumbnail1URL);

		_postSiteUtilityPageAndAssertThumbnailURLReference(
			_thumbnail1Bytes, null, thumbnailURLReference);
	}

	private void _testPostSiteUtilityPageWithThumbnailURLReferenceFileBase64()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setExternalReferenceCode(externalReferenceCode);
		thumbnailURLReference.setFileBase64(_thumbnail1Base64);

		_postSiteUtilityPageAndAssertThumbnailURLReference(
			_thumbnail1Bytes, externalReferenceCode, thumbnailURLReference);
	}

	private void _testPostSiteUtilityPageWithThumbnailURLReferenceFileBase64AndURL()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setExternalReferenceCode(externalReferenceCode);
		thumbnailURLReference.setFileBase64(_thumbnail1Base64);
		thumbnailURLReference.setUrl(_thumbnail2URL);

		_postSiteUtilityPageAndAssertThumbnailURLReference(
			_thumbnail1Bytes, externalReferenceCode, thumbnailURLReference);
	}

	private void _testPostSiteUtilityPageWithThumbnailURLReferenceNonexistingProblemException()
		throws Exception {

		_testPostUtilityPageThumbnailURLReferenceProblemException(
			"Unable to resolve file", RandomTestUtil.randomString(), null);
	}

	private void _testPostSiteUtilityPageWithThumbnailURLReferenceURL()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setExternalReferenceCode(externalReferenceCode);
		thumbnailURLReference.setUrl(_thumbnail1URL);

		_postSiteUtilityPageAndAssertThumbnailURLReference(
			_thumbnail1Bytes, externalReferenceCode, thumbnailURLReference);
	}

	private void _testPostSiteUtilityPageWithThumbnailURLReferenceURLFetchSecurity()
		throws Exception {

		UtilityPageResource utilityPageResource = _getUtilityPageResource();

		ThumbnailURLReferenceTestUtil.testThumbnailURLReferenceURLFetchSecurity(
			(thumbnailURLReference, utilityPage) ->
				_assertThumbnailURLReference(
					_thumbnail1URL.equals(thumbnailURLReference.getUrl()) ?
						_thumbnail1Bytes : _thumbnail2Bytes,
					thumbnailURLReference.getExternalReferenceCode(),
					utilityPage),
			_randomUtilityPage(
				ThumbnailURLReferenceTestUtil.getThumbnailURLReference(
					_thumbnail1URL)),
			_randomUtilityPage(
				ThumbnailURLReferenceTestUtil.getThumbnailURLReference(
					_thumbnail2URL)),
			UtilityPage::getThumbnailURLReference,
			utilityPage -> utilityPageResource.postSiteUtilityPage(
				testGroup.getExternalReferenceCode(), utilityPage));
	}

	private void _testPostSiteUtilityPageWithThumbnailURLReferenceURLUnreachableProblemException()
		throws Exception {

		String url =
			"http://invalid.example.test/" + RandomTestUtil.randomString();

		_testPostUtilityPageThumbnailURLReferenceProblemException(
			"Unable to download file from " + url +
				" because of restricted host invalid.example.test",
			RandomTestUtil.randomString(), url);
	}

	private void _testPostSiteUtilityPageWithThumbnailURLReferenceURLUnsupportedProtocolProblemException()
		throws Exception {

		String url =
			"ftp://invalid.example.test/" + RandomTestUtil.randomString();

		_testPostUtilityPageThumbnailURLReferenceProblemException(
			"Unable to download file from " + url +
				" because of unsupported protocol ftp",
			RandomTestUtil.randomString(), url);
	}

	private void _testPostUtilityPageThumbnailURLReferenceProblemException(
			String expectedTitle, String externalReferenceCode, String url)
		throws Exception {

		UtilityPage utilityPage = randomUtilityPage();

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setExternalReferenceCode(externalReferenceCode);
		thumbnailURLReference.setUrl(url);

		utilityPage.setThumbnailURLReference(thumbnailURLReference);

		UtilityPageResource utilityPageResource = _getUtilityPageResource();

		try {
			utilityPageResource.postSiteUtilityPage(
				testGroup.getExternalReferenceCode(), utilityPage);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(expectedTitle, problem.getTitle());
		}
	}

	private void _testPutSiteUtilityPage(
			Boolean markedAsDefault, UtilityPage utilityPage)
		throws Exception {

		UtilityPage putUtilityPage = utilityPageResource.putSiteUtilityPage(
			testGroup.getExternalReferenceCode(),
			utilityPage.getExternalReferenceCode(), utilityPage);

		assertEquals(utilityPage, putUtilityPage);
		assertValid(putUtilityPage);

		Assert.assertEquals(
			markedAsDefault, putUtilityPage.getMarkedAsDefault());
	}

	private void _testPutSiteUtilityPageWithDates() throws Exception {
		UtilityPage utilityPage = _getUtilityPage(
			null, Boolean.FALSE, RandomTestUtil.randomString());

		DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd");

		Date date = dateFormat.parse("2023-01-01");

		utilityPage.setDateCreated(date);
		utilityPage.setDateModified(date);

		UtilityPage putUtilityPage = utilityPageResource.putSiteUtilityPage(
			testGroup.getExternalReferenceCode(),
			utilityPage.getExternalReferenceCode(), utilityPage);

		Assert.assertEquals(
			utilityPage.getDateCreated(), putUtilityPage.getDateCreated());
		Assert.assertEquals(
			utilityPage.getDateModified(), putUtilityPage.getDateModified());

		utilityPage.setDateCreated(RandomTestUtil.nextDate());
		utilityPage.setDateModified(new Date(date.getTime() + Time.SECOND));

		putUtilityPage = utilityPageResource.putSiteUtilityPage(
			testGroup.getExternalReferenceCode(),
			utilityPage.getExternalReferenceCode(), utilityPage);

		Assert.assertEquals(date, putUtilityPage.getDateCreated());
		Assert.assertEquals(
			utilityPage.getDateModified(), putUtilityPage.getDateModified());
	}

	private void _testPutSiteUtilityPageWithPageSpecifications()
		throws Exception {

		_testPutSiteUtilityPageWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT,
			PageSpecification.Status.APPROVED);
		_testPutSiteUtilityPageWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT,
			PageSpecification.Status.DRAFT);
		_testPutSiteUtilityPageWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED);
		_testPutSiteUtilityPageWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.DRAFT,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT);

		try (PageSpecificationsTestUtil.ExpandoTableAutocloseable
				expandoTableAutoCloseable =
					PageSpecificationsTestUtil.getExpandoTableAutoCloseable()) {

			UtilityPageResource utilityPageResource = _getUtilityPageResource();

			UtilityPage utilityPage = utilityPageResource.postSiteUtilityPage(
				testGroup.getExternalReferenceCode(), randomUtilityPage());

			_assertNestedFields(utilityPage);

			PageSpecification[] pageSpecifications =
				utilityPage.getPageSpecifications();

			PageSpecification pageSpecification = pageSpecifications[0];

			utilityPage.setPageSpecifications(
				() -> PageSpecificationsTestUtil.getContentPageSpecifications(
					pageSpecification.getExternalReferenceCode(),
					testGroup.getGroupId()));

			_assertProblemException(
				"BAD_REQUEST", "Utility pages do not support custom fields",
				() -> utilityPageResource.patchSiteUtilityPage(
					testGroup.getExternalReferenceCode(),
					utilityPage.getExternalReferenceCode(), utilityPage));
		}
	}

	private void _testPutSiteUtilityPageWithPageSpecifications(
			PageSpecification.Status newDraftLayoutStatus,
			PageSpecification.Status newPublishedLayoutStatus,
			PageSpecification.Status oldDraftLayoutStatus,
			PageSpecification.Status oldPublishedLayoutStatus)
		throws Exception {

		UtilityPage utilityPage = _getUtilityPage(
			null, Boolean.FALSE, RandomTestUtil.randomString());

		ContentPageSpecification draftContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				null, testGroup.getGroupId(), oldDraftLayoutStatus);

		ContentPageSpecification publishedContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				draftContentPageSpecification.getExternalReferenceCode(),
				testGroup.getGroupId(), oldPublishedLayoutStatus);

		utilityPage.setPageSpecifications(
			() -> new PageSpecification[] {
				publishedContentPageSpecification, draftContentPageSpecification
			});

		UtilityPageResource utilityPageResource = _getUtilityPageResource();

		_assertPageSpecifications(
			utilityPageResource.putSiteUtilityPage(
				testGroup.getExternalReferenceCode(),
				utilityPage.getExternalReferenceCode(), utilityPage),
			draftContentPageSpecification, publishedContentPageSpecification);

		draftContentPageSpecification.setStatus(newDraftLayoutStatus);
		publishedContentPageSpecification.setStatus(newPublishedLayoutStatus);

		_assertPageSpecifications(
			utilityPageResource.putSiteUtilityPage(
				testGroup.getExternalReferenceCode(),
				utilityPage.getExternalReferenceCode(), utilityPage),
			draftContentPageSpecification, publishedContentPageSpecification);
	}

	private void _testPutSiteUtilityPageWithThumbnail() throws Exception {
		UtilityPage utilityPage = randomUtilityPage();

		utilityPage.setExternalReferenceCode(RandomTestUtil.randomString());

		FileEntry fileEntry1 = FileEntryTestUtil.addPreviewFileEntry(
			testGroup, _portletFileRepository, getClass());

		utilityPage.setThumbnailURLReference(
			() -> ThumbnailURLReferenceUtil.getThumbnailURLReference(
				fileEntry1, null));

		UtilityPageResource utilityPageResource = _getUtilityPageResource();

		UtilityPage putUtilityPage = utilityPageResource.putSiteUtilityPage(
			testGroup.getExternalReferenceCode(),
			utilityPage.getExternalReferenceCode(), utilityPage);

		_assertThumbnailURLReference(
			_thumbnail1Bytes, fileEntry1.getExternalReferenceCode(),
			putUtilityPage);

		FileEntry fileEntry2 = FileEntryTestUtil.addPreviewFileEntry(
			testGroup, _portletFileRepository, getClass());

		putUtilityPage.setFriendlyUrlHistory((FriendlyUrlHistory)null);
		putUtilityPage.setThumbnailURLReference(
			() -> ThumbnailURLReferenceUtil.getThumbnailURLReference(
				fileEntry2, null));

		putUtilityPage = utilityPageResource.putSiteUtilityPage(
			testGroup.getExternalReferenceCode(),
			putUtilityPage.getExternalReferenceCode(), putUtilityPage);

		_assertThumbnailURLReference(
			_thumbnail1Bytes, fileEntry2.getExternalReferenceCode(),
			putUtilityPage);

		putUtilityPage.setFriendlyUrlHistory((FriendlyUrlHistory)null);
		putUtilityPage.setThumbnailURLReference(() -> null);

		putUtilityPage = utilityPageResource.putSiteUtilityPage(
			testGroup.getExternalReferenceCode(),
			putUtilityPage.getExternalReferenceCode(), putUtilityPage);

		_assertThumbnailFileEntryId(
			true, null, putUtilityPage.getExternalReferenceCode());

		utilityPage = randomUtilityPage();

		ThumbnailURLReference thumbnailURLReference =
			ThumbnailURLReferenceUtil.getRandomThumbnailURLReference();

		utilityPage.setThumbnailURLReference(thumbnailURLReference);

		try {
			utilityPageResource.putSiteUtilityPage(
				testGroup.getExternalReferenceCode(),
				putUtilityPage.getExternalReferenceCode(), utilityPage);
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(
				"Unable to download file from " +
					thumbnailURLReference.getUrl() +
						" because of restricted host invalid.example.test",
				problem.getTitle());
		}
	}

	private void _testPutSiteUtilityPageWithThumbnailURLReferenceExternalReferenceCodeAndFileBase64()
		throws Exception {

		UtilityPage postUtilityPage = testPostSiteUtilityPage_addUtilityPage(
			randomUtilityPage());

		Assert.assertNull(postUtilityPage.getThumbnailURLReference());

		FileEntry fileEntry = FileEntryTestUtil.addPreviewFileEntry(
			testGroup, _portletFileRepository, getClass());

		String externalReferenceCode = fileEntry.getExternalReferenceCode();

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setExternalReferenceCode(externalReferenceCode);
		thumbnailURLReference.setFileBase64(_thumbnail2Base64);

		_putSiteUtilityPageAndAssertThumbnailURLReference(
			_thumbnail1Bytes, externalReferenceCode, postUtilityPage,
			thumbnailURLReference);
	}

	private void _testPutSiteUtilityPageWithThumbnailURLReferenceFileBase64()
		throws Exception {

		UtilityPage postUtilityPage = testPostSiteUtilityPage_addUtilityPage(
			randomUtilityPage());

		Assert.assertNull(postUtilityPage.getThumbnailURLReference());

		ThumbnailURLReference thumbnailURLReference1 =
			new ThumbnailURLReference();

		thumbnailURLReference1.setFileBase64(_thumbnail1Base64);

		UtilityPage putUtilityPage =
			_putSiteUtilityPageAndAssertThumbnailURLReference(
				_thumbnail1Bytes, null, postUtilityPage,
				thumbnailURLReference1);

		ThumbnailURLReference thumbnailURLReference2 =
			new ThumbnailURLReference();

		thumbnailURLReference2.setFileBase64(_thumbnail2Base64);

		_putSiteUtilityPageAndAssertThumbnailURLReference(
			_thumbnail2Bytes, null, putUtilityPage, thumbnailURLReference2);
	}

	private void _testPutSiteUtilityPageWithThumbnailURLReferenceURL()
		throws Exception {

		UtilityPage postUtilityPage = testPostSiteUtilityPage_addUtilityPage(
			randomUtilityPage());

		Assert.assertNull(postUtilityPage.getThumbnailURLReference());

		ThumbnailURLReference thumbnailURLReference1 =
			new ThumbnailURLReference();

		thumbnailURLReference1.setUrl(_thumbnail1URL);

		UtilityPage putUtilityPage =
			_putSiteUtilityPageAndAssertThumbnailURLReference(
				_thumbnail1Bytes, null, postUtilityPage,
				thumbnailURLReference1);

		ThumbnailURLReference thumbnailURLReference2 =
			new ThumbnailURLReference();

		thumbnailURLReference2.setUrl(_thumbnail2URL);

		_putSiteUtilityPageAndAssertThumbnailURLReference(
			_thumbnail2Bytes, null, putUtilityPage, thumbnailURLReference2);
	}

	private static String _thumbnail1Base64;
	private static byte[] _thumbnail1Bytes;
	private static String _thumbnail1URL;
	private static String _thumbnail2Base64;
	private static byte[] _thumbnail2Bytes;
	private static String _thumbnail2URL;
	private static ThumbnailHttpServer _thumbnailHttpServer;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutUtilityPageEntryLocalService
		_layoutUtilityPageEntryLocalService;

	@Inject
	private PortletFileRepository _portletFileRepository;

	@Inject
	private UserLocalService _userLocalService;

}