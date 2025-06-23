/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.site.client.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.FriendlyUrlHistory;
import com.liferay.headless.admin.site.client.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.client.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.UtilityPage;
import com.liferay.headless.admin.site.client.dto.v1_0.UtilityPageSEOSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.UtilityPageSettings;
import com.liferay.headless.admin.site.client.pagination.Page;
import com.liferay.headless.admin.site.client.problem.Problem;
import com.liferay.headless.admin.site.client.resource.v1_0.UtilityPageResource;
import com.liferay.headless.admin.site.resource.v1_0.test.util.LayoutUtilityPageEntryTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.PageSpecificationsTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryLocalService;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rubén Pulido
 */
@FeatureFlag("LPD-35443")
@RunWith(Arquillian.class)
public class UtilityPageResourceTest extends BaseUtilityPageResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Override
	@Test
	public void testDeleteSiteSiteByExternalReferenceCodeUtilityPage()
		throws Exception {

		UtilityPage postUtilityPage =
			testPostSiteSiteByExternalReferenceCodeUtilityPage_addUtilityPage(
				randomUtilityPage());

		Assert.assertNotNull(
			_layoutUtilityPageEntryLocalService.
				fetchLayoutUtilityPageEntryByExternalReferenceCode(
					postUtilityPage.getExternalReferenceCode(),
					testGroup.getGroupId()));

		utilityPageResource.deleteSiteSiteByExternalReferenceCodeUtilityPage(
			testGroup.getExternalReferenceCode(),
			postUtilityPage.getExternalReferenceCode());

		Assert.assertNull(
			_layoutUtilityPageEntryLocalService.
				fetchLayoutUtilityPageEntryByExternalReferenceCode(
					postUtilityPage.getExternalReferenceCode(),
					testGroup.getGroupId()));

		_assertProblemException(
			"NOT_FOUND",
			() ->
				utilityPageResource.
					deleteSiteSiteByExternalReferenceCodeUtilityPage(
						testGroup.getExternalReferenceCode(),
						postUtilityPage.getExternalReferenceCode()));
	}

	@Override
	@Test
	public void testGetSiteSiteByExternalReferenceCodeUtilityPage()
		throws Exception {

		UtilityPage utilityPage = randomUtilityPage();

		utilityPage.setMarkedAsDefault(false);

		UtilityPage postUtilityPage =
			testPostSiteSiteByExternalReferenceCodeUtilityPage_addUtilityPage(
				utilityPage);

		UtilityPage getUtilityPage =
			utilityPageResource.getSiteSiteByExternalReferenceCodeUtilityPage(
				testGroup.getExternalReferenceCode(),
				postUtilityPage.getExternalReferenceCode());

		assertEquals(postUtilityPage, getUtilityPage);
		assertValid(getUtilityPage);

		_assertProblemException(
			"NOT_FOUND",
			() ->
				utilityPageResource.
					getSiteSiteByExternalReferenceCodeUtilityPage(
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
			utilityPageResource.getSiteSiteByExternalReferenceCodeUtilityPage(
				testGroup.getExternalReferenceCode(),
				postUtilityPage.getExternalReferenceCode()));

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		Assert.assertTrue(layout.isPublished());

		_assertNestedFields(
			utilityPageResource.getSiteSiteByExternalReferenceCodeUtilityPage(
				testGroup.getExternalReferenceCode(),
				postUtilityPage.getExternalReferenceCode()));
	}

	@Override
	@Test
	public void testGetSiteSiteByExternalReferenceCodeUtilityPagesPage()
		throws Exception {

		super.testGetSiteSiteByExternalReferenceCodeUtilityPagesPage();

		_testGetSiteSiteByExternalReferenceCodeUtilityPagesPageWithNestedFields();
		_testGetSiteSiteByExternalReferenceCodeUtilityPagesPageWithSearch();
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteSiteByExternalReferenceCodeUtilityPagesPageWithSortDateTime()
		throws Exception {

		super.
			testGetSiteSiteByExternalReferenceCodeUtilityPagesPageWithSortDateTime();
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteSiteByExternalReferenceCodeUtilityPagesPageWithSortDouble()
		throws Exception {

		super.
			testGetSiteSiteByExternalReferenceCodeUtilityPagesPageWithSortDouble();
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteSiteByExternalReferenceCodeUtilityPagesPageWithSortInteger()
		throws Exception {

		super.
			testGetSiteSiteByExternalReferenceCodeUtilityPagesPageWithSortInteger();
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteSiteByExternalReferenceCodeUtilityPagesPageWithSortString()
		throws Exception {

		super.
			testGetSiteSiteByExternalReferenceCodeUtilityPagesPageWithSortString();
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteUtilityPagePermissionsPage() throws Exception {
		super.testGetSiteUtilityPagePermissionsPage();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetSiteSiteByExternalReferenceCodeUtilityPage()
		throws Exception {

		super.testGraphQLGetSiteSiteByExternalReferenceCodeUtilityPage();
	}

	@Override
	@Test
	public void testPatchSiteSiteByExternalReferenceCodeUtilityPage()
		throws Exception {

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			LayoutUtilityPageEntryTestUtil.getLayoutUtilityPageEntry(
				ServiceContextTestUtil.getServiceContext(
					testGroup.getGroupId(), TestPropsValues.getUserId()));

		UtilityPage utilityPage =
			utilityPageResource.getSiteSiteByExternalReferenceCodeUtilityPage(
				testGroup.getExternalReferenceCode(),
				layoutUtilityPageEntry.getExternalReferenceCode());

		_testPatchSiteSiteByExternalReferenceCodeUtilityPage(
			Boolean.FALSE, utilityPage,
			new UtilityPage() {
				{
					setMarkedAsDefault(Boolean.FALSE);
				}
			});

		Layout layout = _layoutLocalService.getLayout(
			layoutUtilityPageEntry.getPlid());

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		Repository repository = _portletFileRepository.addPortletRepository(
			testGroup.getGroupId(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				testGroup, TestPropsValues.getUserId()));

		utilityPage.setThumbnail(
			() -> new ItemExternalReference() {
				{
					setClassName(() -> FileEntry.class.getName());
					setExternalReferenceCode(
						() -> {
							FileEntry fileEntry = _addPortletFileEntry(
								repository.getDlFolderId());

							return fileEntry.getExternalReferenceCode();
						});
				}
			});

		_testPatchSiteSiteByExternalReferenceCodeUtilityPage(
			Boolean.TRUE, utilityPage,
			new UtilityPage() {
				{
					setMarkedAsDefault(Boolean.TRUE);
					setThumbnail(utilityPage::getThumbnail);
				}
			});

		utilityPage.setName(RandomTestUtil::randomString);
		utilityPage.setThumbnail(
			() -> new ItemExternalReference() {
				{
					setClassName(() -> FileEntry.class.getName());
					setExternalReferenceCode(
						() -> {
							FileEntry fileEntry = _addPortletFileEntry(
								repository.getDlFolderId());

							return fileEntry.getExternalReferenceCode();
						});
				}
			});
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

		_testPatchSiteSiteByExternalReferenceCodeUtilityPage(
			Boolean.TRUE, utilityPage,
			new UtilityPage() {
				{
					setName(utilityPage::getName);
					setThumbnail(utilityPage::getThumbnail);
					setUtilityPageSettings(utilityPage::getUtilityPageSettings);
				}
			});

		_testPatchSiteSiteByExternalReferenceCodeUtilityPageWithPageSpecifications();

		_assertProblemException(
			"NOT_FOUND",
			() ->
				utilityPageResource.
					patchSiteSiteByExternalReferenceCodeUtilityPage(
						testGroup.getExternalReferenceCode(),
						RandomTestUtil.randomString(), randomUtilityPage()));
	}

	@Override
	@Test
	@TestInfo("LPD-48984")
	public void testPostSiteSiteByExternalReferenceCodeUtilityPage()
		throws Exception {

		super.testPostSiteSiteByExternalReferenceCodeUtilityPage();

		UtilityPage utilityPage = randomUtilityPage();

		Repository repository = _portletFileRepository.addPortletRepository(
			testGroup.getGroupId(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				testGroup, TestPropsValues.getUserId()));

		ItemExternalReference itemExternalReference =
			new ItemExternalReference() {
				{
					setClassName(() -> FileEntry.class.getName());
					setExternalReferenceCode(
						() -> {
							FileEntry fileEntry = _addPortletFileEntry(
								repository.getDlFolderId());

							return fileEntry.getExternalReferenceCode();
						});
				}
			};

		utilityPage.setThumbnail(itemExternalReference);

		UtilityPage postUtilityPage =
			testPostSiteSiteByExternalReferenceCodeUtilityPage_addUtilityPage(
				utilityPage);

		assertEquals(utilityPage, postUtilityPage);
		assertValid(postUtilityPage);
		Assert.assertEquals(
			itemExternalReference, postUtilityPage.getThumbnail());

		_testPostSiteSiteByExternalReferenceCodeUtilityPageWithPageSpecifications();
	}

	@Override
	@Test
	@TestInfo("LPD-48987")
	public void testPostSiteSiteByExternalReferenceCodeUtilityPagePageSpecification()
		throws Exception {

		UtilityPageResource utilityPageResource = _getUtilityPageResource();

		UtilityPage utilityPage =
			utilityPageResource.postSiteSiteByExternalReferenceCodeUtilityPage(
				testGroup.getExternalReferenceCode(),
				_getUtilityPage(
					null, Boolean.FALSE, RandomTestUtil.randomString()));

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryLocalService.
				getLayoutUtilityPageEntryByExternalReferenceCode(
					utilityPage.getExternalReferenceCode(),
					testGroup.getGroupId());

		PageSpecificationsTestUtil.
			testPostSiteSiteByExternalReferenceCodePageSpecification(
				_layoutLocalService.getLayout(layoutUtilityPageEntry.getPlid()),
				utilityPage.getPageSpecifications(),
				ServiceContextTestUtil.getServiceContext(
					testGroup.getGroupId(), TestPropsValues.getUserId()),
				contentPageSpecification ->
					utilityPageResource.
						postSiteSiteByExternalReferenceCodeUtilityPagePageSpecification(
							testGroup.getExternalReferenceCode(),
							utilityPage.getExternalReferenceCode(),
							contentPageSpecification));
	}

	@Override
	@Test
	@TestInfo({"LPD-42587", "LPD-48987"})
	public void testPutSiteSiteByExternalReferenceCodeUtilityPage()
		throws Exception {

		_testPutSiteSiteByExternalReferenceCodeUtilityPage(
			Boolean.FALSE, randomUtilityPage());

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			LayoutUtilityPageEntryTestUtil.getLayoutUtilityPageEntry(
				ServiceContextTestUtil.getServiceContext(
					testGroup.getGroupId(), TestPropsValues.getUserId()));

		Layout layout = _layoutLocalService.getLayout(
			layoutUtilityPageEntry.getPlid());

		Assert.assertFalse(layout.isPublished());

		_assertProblemException(
			"BAD_REQUEST",
			() -> _testPutSiteSiteByExternalReferenceCodeUtilityPage(
				Boolean.TRUE,
				_getUtilityPage(
					null, Boolean.TRUE,
					layoutUtilityPageEntry.getExternalReferenceCode())));

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		Repository repository = _portletFileRepository.addPortletRepository(
			testGroup.getGroupId(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				testGroup, TestPropsValues.getUserId()));

		_testPutSiteSiteByExternalReferenceCodeUtilityPage(
			Boolean.FALSE,
			_getUtilityPage(
				_addPortletFileEntry(repository.getDlFolderId()), Boolean.FALSE,
				layoutUtilityPageEntry.getExternalReferenceCode()));
		_testPutSiteSiteByExternalReferenceCodeUtilityPage(
			Boolean.TRUE,
			_getUtilityPage(
				_addPortletFileEntry(repository.getDlFolderId()), Boolean.TRUE,
				layoutUtilityPageEntry.getExternalReferenceCode()));

		_testPutSiteSiteByExternalReferenceCodeUtilityPage(
			Boolean.FALSE,
			_getUtilityPage(
				null, null, layoutUtilityPageEntry.getExternalReferenceCode()));

		_testPutSiteSiteByExternalReferenceCodeUtilityPageWithPageSpecifications();
	}

	@Ignore
	@Override
	@Test
	public void testPutSiteUtilityPagePermissionsPage() throws Exception {
		super.testPutSiteUtilityPagePermissionsPage();
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
	protected UtilityPage
			testGetSiteSiteByExternalReferenceCodeUtilityPagesPage_addUtilityPage(
				String siteExternalReferenceCode, UtilityPage utilityPage)
		throws Exception {

		return utilityPageResource.
			postSiteSiteByExternalReferenceCodeUtilityPage(
				siteExternalReferenceCode, utilityPage);
	}

	@Ignore
	@Override
	@Test
	protected UtilityPage testGetSiteUtilityPagePermissionsPage_addUtilityPage()
		throws Exception {

		return super.testGetSiteUtilityPagePermissionsPage_addUtilityPage();
	}

	@Override
	protected UtilityPage
			testPostSiteSiteByExternalReferenceCodeUtilityPage_addUtilityPage(
				UtilityPage utilityPage)
		throws Exception {

		return testGetSiteSiteByExternalReferenceCodeUtilityPagesPage_addUtilityPage(
			testGroup.getExternalReferenceCode(), utilityPage);
	}

	@Ignore
	@Override
	@Test
	protected UtilityPage testPutSiteUtilityPagePermissionsPage_addUtilityPage()
		throws Exception {

		return super.testPutSiteUtilityPagePermissionsPage_addUtilityPage();
	}

	private FileEntry _addPortletFileEntry(long folderId) throws Exception {
		Class<?> clazz = getClass();

		return _portletFileRepository.addPortletFileEntry(
			null, testGroup.getGroupId(), TestPropsValues.getUserId(),
			LayoutUtilityPageEntry.class.getName(), RandomTestUtil.randomLong(),
			RandomTestUtil.randomString(), folderId,
			clazz.getResourceAsStream("dependencies/thumbnail.png"),
			RandomTestUtil.randomString(), ContentTypes.IMAGE_PNG, false);
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
			String status, UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try {
			unsafeRunnable.run();

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals(status, problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}
	}

	private UtilityPage _getUtilityPage(
			FileEntry fileEntry, Boolean markedAsDefault,
			String utilityPageExternalReferenceCode)
		throws Exception {

		UtilityPage utilityPage = randomUtilityPage();

		utilityPage.setExternalReferenceCode(utilityPageExternalReferenceCode);
		utilityPage.setMarkedAsDefault(markedAsDefault);

		if (fileEntry != null) {
			utilityPage.setThumbnail(
				() -> new ItemExternalReference() {
					{
						setClassName(() -> FileEntry.class.getName());
						setExternalReferenceCode(
							fileEntry::getExternalReferenceCode);
					}
				});
		}

		return utilityPage;
	}

	private UtilityPage _getUtilityPage(String name) throws Exception {
		UtilityPage utilityPage = randomUtilityPage();

		utilityPage.setName(name);

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
			testCompany.getVirtualHostname(), 8080, "http"
		).locale(
			LocaleUtil.getDefault()
		).parameters(
			"nestedFields", "friendlyUrlHistory,pageSpecifications"
		).build();
	}

	private List<UtilityPage>
			_testGetSiteSiteByExternalReferenceCodeUtilityPagesPage(
				int count, String search)
		throws Exception {

		Page<UtilityPage> utilityPagePage =
			utilityPageResource.
				getSiteSiteByExternalReferenceCodeUtilityPagesPage(
					testGroup.getExternalReferenceCode(), search, null, null,
					null, null);

		List<UtilityPage> utilityPages =
			(List<UtilityPage>)utilityPagePage.getItems();

		Assert.assertEquals(
			utilityPages.toString(), count, utilityPages.size());

		return utilityPages;
	}

	private void _testGetSiteSiteByExternalReferenceCodeUtilityPagesPageWithNestedFields()
		throws Exception {

		Page<UtilityPage> page =
			utilityPageResource.
				getSiteSiteByExternalReferenceCodeUtilityPagesPage(
					testGroup.getExternalReferenceCode(), null, null, null,
					null, null);

		long totalCount = page.getTotalCount();

		UtilityPage utilityPage = randomUtilityPage();

		utilityPage.setMarkedAsDefault(false);

		testGetSiteSiteByExternalReferenceCodeUtilityPagesPage_addUtilityPage(
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

		page =
			utilityPageResource.
				getSiteSiteByExternalReferenceCodeUtilityPagesPage(
					testGroup.getExternalReferenceCode(), null, null, null,
					null, null);

		Assert.assertEquals(totalCount + 1, page.getTotalCount());

		_assertNestedFields(
			_getUtilityPage(
				utilityPage.getExternalReferenceCode(),
				(List<UtilityPage>)page.getItems()));

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		Assert.assertTrue(layout.isPublished());

		page =
			utilityPageResource.
				getSiteSiteByExternalReferenceCodeUtilityPagesPage(
					testGroup.getExternalReferenceCode(), null, null, null,
					null, null);

		Assert.assertEquals(totalCount + 1, page.getTotalCount());

		_assertNestedFields(
			_getUtilityPage(
				utilityPage.getExternalReferenceCode(),
				(List<UtilityPage>)page.getItems()));
	}

	private void _testGetSiteSiteByExternalReferenceCodeUtilityPagesPageWithSearch()
		throws Exception {

		String search = RandomTestUtil.randomString();

		Page<UtilityPage> utilityPagePage =
			utilityPageResource.
				getSiteSiteByExternalReferenceCodeUtilityPagesPage(
					testGroup.getExternalReferenceCode(), search, null, null,
					null, null);

		int totalCount = GetterUtil.getInteger(utilityPagePage.getTotalCount());

		testGetSiteSiteByExternalReferenceCodeUtilityPagesPage_addUtilityPage(
			testGroup.getExternalReferenceCode(), randomUtilityPage());

		testGetSiteSiteByExternalReferenceCodeUtilityPagesPage_addUtilityPage(
			testGroup.getExternalReferenceCode(), randomUtilityPage());

		UtilityPage utilityPage = _getUtilityPage(search);

		testGetSiteSiteByExternalReferenceCodeUtilityPagesPage_addUtilityPage(
			testGroup.getExternalReferenceCode(), utilityPage);

		testGetSiteSiteByExternalReferenceCodeUtilityPagesPage_addUtilityPage(
			testGroup.getExternalReferenceCode(), randomUtilityPage());

		List<UtilityPage> utilityPages =
			_testGetSiteSiteByExternalReferenceCodeUtilityPagesPage(
				totalCount + 1, search);

		String name = null;

		for (UtilityPage curUtilityPage : utilityPages) {
			if (!Objects.equals(
					curUtilityPage.getExternalReferenceCode(),
					utilityPage.getExternalReferenceCode())) {

				continue;
			}

			name = curUtilityPage.getName();

			break;
		}

		Assert.assertEquals(search, name);

		testGetSiteSiteByExternalReferenceCodeUtilityPagesPage_addUtilityPage(
			testGroup.getExternalReferenceCode(),
			_getUtilityPage(
				RandomTestUtil.randomString() + search +
					RandomTestUtil.randomString()));

		_testGetSiteSiteByExternalReferenceCodeUtilityPagesPage(
			totalCount + 2, search);
	}

	private void _testPatchSiteSiteByExternalReferenceCodeUtilityPage(
			Boolean expectedMarkedAsDefault, UtilityPage expectedUtilityPage,
			UtilityPage utilityPage)
		throws Exception {

		UtilityPage patchUtilityPage =
			utilityPageResource.patchSiteSiteByExternalReferenceCodeUtilityPage(
				testGroup.getExternalReferenceCode(),
				expectedUtilityPage.getExternalReferenceCode(), utilityPage);

		assertEquals(expectedUtilityPage, patchUtilityPage);
		assertValid(patchUtilityPage);

		Assert.assertEquals(
			expectedMarkedAsDefault, patchUtilityPage.getMarkedAsDefault());
		Assert.assertEquals(
			expectedUtilityPage.getThumbnail(),
			patchUtilityPage.getThumbnail());
	}

	private void _testPatchSiteSiteByExternalReferenceCodeUtilityPageWithPageSpecifications()
		throws Exception {

		_testPatchSiteSiteByExternalReferenceCodeUtilityPageWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT,
			PageSpecification.Status.APPROVED);
		_testPatchSiteSiteByExternalReferenceCodeUtilityPageWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT,
			PageSpecification.Status.DRAFT);
		_testPatchSiteSiteByExternalReferenceCodeUtilityPageWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED);
		_testPatchSiteSiteByExternalReferenceCodeUtilityPageWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.DRAFT,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT);
	}

	private void
			_testPatchSiteSiteByExternalReferenceCodeUtilityPageWithPageSpecifications(
				PageSpecification.Status newDraftLayoutStatus,
				PageSpecification.Status newPublishedLayoutStatus,
				PageSpecification.Status oldDraftLayoutStatus,
				PageSpecification.Status oldPublishedLayoutStatus)
		throws Exception {

		UtilityPage utilityPage = _getUtilityPage(
			null, Boolean.FALSE, RandomTestUtil.randomString());

		ContentPageSpecification draftContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				null, oldDraftLayoutStatus);

		ContentPageSpecification publishedContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				draftContentPageSpecification.getExternalReferenceCode(),
				oldPublishedLayoutStatus);

		utilityPage.setPageSpecifications(
			() -> new PageSpecification[] {
				publishedContentPageSpecification, draftContentPageSpecification
			});

		UtilityPageResource utilityPageResource = _getUtilityPageResource();

		UtilityPage postUtilityPage =
			utilityPageResource.postSiteSiteByExternalReferenceCodeUtilityPage(
				testGroup.getExternalReferenceCode(), utilityPage);

		_assertPageSpecifications(
			postUtilityPage, draftContentPageSpecification,
			publishedContentPageSpecification);

		draftContentPageSpecification.setStatus(newDraftLayoutStatus);
		publishedContentPageSpecification.setStatus(newPublishedLayoutStatus);

		_assertPageSpecifications(
			utilityPageResource.patchSiteSiteByExternalReferenceCodeUtilityPage(
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

	private void _testPostSiteSiteByExternalReferenceCodeUtilityPageWithPageSpecifications()
		throws Exception {

		_testPostSiteSiteByExternalReferenceCodeUtilityPageWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED);
		_testPostSiteSiteByExternalReferenceCodeUtilityPageWithPageSpecifications(
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT);
		_testPostSiteSiteByExternalReferenceCodeUtilityPageWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.APPROVED);
		_testPostSiteSiteByExternalReferenceCodeUtilityPageWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.DRAFT);
	}

	private void
			_testPostSiteSiteByExternalReferenceCodeUtilityPageWithPageSpecifications(
				PageSpecification.Status draftLayoutStatus,
				PageSpecification.Status publishedLayoutStatus)
		throws Exception {

		UtilityPage utilityPage = _getUtilityPage(
			null, Boolean.FALSE, RandomTestUtil.randomString());

		ContentPageSpecification draftContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				null, draftLayoutStatus);

		ContentPageSpecification publishedContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				draftContentPageSpecification.getExternalReferenceCode(),
				publishedLayoutStatus);

		utilityPage.setPageSpecifications(
			() -> new PageSpecification[] {
				publishedContentPageSpecification, draftContentPageSpecification
			});

		UtilityPageResource utilityPageResource = _getUtilityPageResource();

		_assertPageSpecifications(
			utilityPageResource.postSiteSiteByExternalReferenceCodeUtilityPage(
				testGroup.getExternalReferenceCode(), utilityPage),
			draftContentPageSpecification, publishedContentPageSpecification);
	}

	private void _testPutSiteSiteByExternalReferenceCodeUtilityPage(
			Boolean markedAsDefault, UtilityPage utilityPage)
		throws Exception {

		UtilityPage putUtilityPage =
			utilityPageResource.putSiteSiteByExternalReferenceCodeUtilityPage(
				testGroup.getExternalReferenceCode(),
				utilityPage.getExternalReferenceCode(), utilityPage);

		assertEquals(utilityPage, putUtilityPage);
		assertValid(putUtilityPage);

		Assert.assertEquals(
			markedAsDefault, putUtilityPage.getMarkedAsDefault());
		Assert.assertEquals(
			utilityPage.getThumbnail(), putUtilityPage.getThumbnail());
	}

	private void _testPutSiteSiteByExternalReferenceCodeUtilityPageWithPageSpecifications()
		throws Exception {

		_testPutSiteSiteByExternalReferenceCodeUtilityPageWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT,
			PageSpecification.Status.APPROVED);
		_testPutSiteSiteByExternalReferenceCodeUtilityPageWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT,
			PageSpecification.Status.DRAFT);
		_testPutSiteSiteByExternalReferenceCodeUtilityPageWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED);
		_testPutSiteSiteByExternalReferenceCodeUtilityPageWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.DRAFT,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT);
	}

	private void
			_testPutSiteSiteByExternalReferenceCodeUtilityPageWithPageSpecifications(
				PageSpecification.Status newDraftLayoutStatus,
				PageSpecification.Status newPublishedLayoutStatus,
				PageSpecification.Status oldDraftLayoutStatus,
				PageSpecification.Status oldPublishedLayoutStatus)
		throws Exception {

		UtilityPage utilityPage = _getUtilityPage(
			null, Boolean.FALSE, RandomTestUtil.randomString());

		ContentPageSpecification draftContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				null, oldDraftLayoutStatus);

		ContentPageSpecification publishedContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				draftContentPageSpecification.getExternalReferenceCode(),
				oldPublishedLayoutStatus);

		utilityPage.setPageSpecifications(
			() -> new PageSpecification[] {
				publishedContentPageSpecification, draftContentPageSpecification
			});

		UtilityPageResource utilityPageResource = _getUtilityPageResource();

		_assertPageSpecifications(
			utilityPageResource.putSiteSiteByExternalReferenceCodeUtilityPage(
				testGroup.getExternalReferenceCode(),
				utilityPage.getExternalReferenceCode(), utilityPage),
			draftContentPageSpecification, publishedContentPageSpecification);

		draftContentPageSpecification.setStatus(newDraftLayoutStatus);
		publishedContentPageSpecification.setStatus(newPublishedLayoutStatus);

		_assertPageSpecifications(
			utilityPageResource.putSiteSiteByExternalReferenceCodeUtilityPage(
				testGroup.getExternalReferenceCode(),
				utilityPage.getExternalReferenceCode(), utilityPage),
			draftContentPageSpecification, publishedContentPageSpecification);
	}

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