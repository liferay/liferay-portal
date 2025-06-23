/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.site.client.dto.v1_0.ContentPageSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.FriendlyUrlHistory;
import com.liferay.headless.admin.site.client.dto.v1_0.PageSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.SitePage;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetPageSettings;
import com.liferay.headless.admin.site.client.problem.Problem;
import com.liferay.headless.admin.site.client.resource.v1_0.SitePageResource;
import com.liferay.headless.admin.site.resource.v1_0.test.util.LayoutPageTemplateEntryTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.LayoutUtilityPageEntryTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.PageSpecificationsTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypePortletConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Arrays;
import java.util.Collections;
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
public class SitePageResourceTest extends BaseSitePageResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Override
	@Test
	public void testDeleteSiteSiteByExternalReferenceCodeSitePage()
		throws Exception {

		SitePage postSitePage =
			testGetSiteSiteByExternalReferenceCodeSitePagesPage_addSitePage(
				testGroup.getExternalReferenceCode(), randomSitePage());

		sitePageResource.deleteSiteSiteByExternalReferenceCodeSitePage(
			testGroup.getExternalReferenceCode(),
			postSitePage.getExternalReferenceCode());

		Assert.assertNull(
			_layoutLocalService.fetchLayoutByExternalReferenceCode(
				postSitePage.getExternalReferenceCode(),
				testGroup.getGroupId()));

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		_testDeleteSiteSiteByExternalReferenceCodeSitePage(
			_addLayout(LayoutConstants.TYPE_CONTENT, null, serviceContext),
			_addLayout(
				LayoutConstants.TYPE_PORTLET,
				UnicodePropertiesBuilder.put(
					LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID, "1_column"
				).buildString(),
				serviceContext));

		Layout layout = _addLayout(
			LayoutConstants.TYPE_CONTENT, null, serviceContext);

		_assertDeleteSiteSiteByExternalReferenceCodeSitePageProblemException(
			layout.fetchDraftLayout(),
			LayoutPageTemplateEntryTestUtil.
				getBasicLayoutPageTemplateEntryLayout(serviceContext),
			LayoutPageTemplateEntryTestUtil.
				getDisplayPageLayoutPageTemplateEntryLayout(serviceContext),
			LayoutPageTemplateEntryTestUtil.
				getMasterLayoutPageTemplateEntryLayout(serviceContext),
			LayoutUtilityPageEntryTestUtil.getLayoutUtilityPageEntryLayout(
				serviceContext));
	}

	@Override
	@Test
	public void testGetSiteSiteByExternalReferenceCodeSitePage()
		throws Exception {

		SitePage postSitePage =
			testGetSiteSiteByExternalReferenceCodeSitePagesPage_addSitePage(
				testGroup.getExternalReferenceCode(), randomSitePage());

		SitePage getSitePage =
			sitePageResource.getSiteSiteByExternalReferenceCodeSitePage(
				testGroup.getExternalReferenceCode(),
				postSitePage.getExternalReferenceCode());

		assertEquals(postSitePage, getSitePage);
		assertValid(getSitePage);

		_testGetSiteSiteByExternalReferenceCodeSitePageWithNestedFields(
			testGetSiteSiteByExternalReferenceCodeSitePagesPage_addSitePage(
				testGroup.getExternalReferenceCode(), randomSitePage()));

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		Layout layout = _addLayout(
			LayoutConstants.TYPE_CONTENT, null, serviceContext);

		Assert.assertFalse(layout.isPublished());

		_testGetSiteSiteByExternalReferenceCodeSitePage(layout);

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		Assert.assertTrue(layout.isPublished());

		_testGetSiteSiteByExternalReferenceCodeSitePage(layout);

		_testGetSiteSiteByExternalReferenceCodeSitePage(
			_addLayout(
				LayoutConstants.TYPE_PORTLET,
				UnicodePropertiesBuilder.put(
					LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID, "1_column"
				).buildString(),
				serviceContext));
	}

	@Override
	@Test
	public void testGetSiteSiteByExternalReferenceCodeSitePagesPage()
		throws Exception {

		super.testGetSiteSiteByExternalReferenceCodeSitePagesPage();
	}

	@Override
	@Test
	public void testGetSiteSiteByExternalReferenceCodeSitePagesPageWithPagination()
		throws Exception {

		super.
			testGetSiteSiteByExternalReferenceCodeSitePagesPageWithPagination();
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteSitePagePermissionsPage() throws Exception {
		super.testGetSiteSitePagePermissionsPage();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetSiteSiteByExternalReferenceCodeSitePage()
		throws Exception {

		super.testGraphQLGetSiteSiteByExternalReferenceCodeSitePage();
	}

	@Override
	@Test
	public void testPatchSiteSiteByExternalReferenceCodeSitePage()
		throws Exception {

		_testPatchSiteSiteByExternalReferenceCodeSitePage(
			SitePage.Type.CONTENT_PAGE);
		_testPatchSiteSiteByExternalReferenceCodeSitePage(
			SitePage.Type.WIDGET_PAGE);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		Layout layout = _addLayout(
			LayoutConstants.TYPE_CONTENT, null, serviceContext);

		_assertPatchSiteSiteByExternalReferenceCodeSitePageProblemException(
			layout.fetchDraftLayout(),
			LayoutPageTemplateEntryTestUtil.
				getBasicLayoutPageTemplateEntryLayout(serviceContext),
			LayoutPageTemplateEntryTestUtil.
				getDisplayPageLayoutPageTemplateEntryLayout(serviceContext),
			LayoutPageTemplateEntryTestUtil.
				getMasterLayoutPageTemplateEntryLayout(serviceContext),
			LayoutUtilityPageEntryTestUtil.getLayoutUtilityPageEntryLayout(
				serviceContext));
	}

	@Override
	@Test
	public void testPostByExternalReferenceCodeSitePage() throws Exception {
		super.testPostByExternalReferenceCodeSitePage();

		_testPostByExternalReferenceCodeSitePage(
			_getRandomSitePage(SitePage.Type.CONTENT_PAGE));
		_testPostByExternalReferenceCodeSitePage(
			_getRandomSitePage(SitePage.Type.WIDGET_PAGE));

		Layout layout = LayoutTestUtil.addTypePortletLayout(testGroup);

		_testPostByExternalReferenceCodeSitePage(
			_getRandomSitePage(
				StringUtil.toLowerCase(RandomTestUtil.randomString()),
				layout.getExternalReferenceCode(), SitePage.Type.CONTENT_PAGE,
				StringUtil.toLowerCase(RandomTestUtil.randomString())));
	}

	@Override
	@Test
	public void testPostSiteSiteByExternalReferenceCodeSitePagePageSpecification()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(testGroup);

		SitePageResource sitePageResource = _getSitePageResource();

		SitePage sitePage =
			sitePageResource.getSiteSiteByExternalReferenceCodeSitePage(
				testGroup.getExternalReferenceCode(),
				layout.getExternalReferenceCode());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		PageSpecificationsTestUtil.
			testPostSiteSiteByExternalReferenceCodePageSpecification(
				layout, sitePage.getPageSpecifications(), serviceContext,
				contentPageSpecification ->
					sitePageResource.
						postSiteSiteByExternalReferenceCodeSitePagePageSpecification(
							testGroup.getExternalReferenceCode(),
							layout.getExternalReferenceCode(),
							contentPageSpecification));

		_assertPostSiteSiteByExternalReferenceCodeSitePagePageSpecificationProblemException(
			LayoutTestUtil.addTypePortletLayout(testGroup));
		_assertPostSiteSiteByExternalReferenceCodeSitePagePageSpecificationProblemException(
			LayoutPageTemplateEntryTestUtil.
				getBasicLayoutPageTemplateEntryLayout(serviceContext));
		_assertPostSiteSiteByExternalReferenceCodeSitePagePageSpecificationProblemException(
			LayoutPageTemplateEntryTestUtil.
				getDisplayPageLayoutPageTemplateEntryLayout(serviceContext));
		_assertPostSiteSiteByExternalReferenceCodeSitePagePageSpecificationProblemException(
			LayoutPageTemplateEntryTestUtil.
				getMasterLayoutPageTemplateEntryLayout(serviceContext));
		_assertPostSiteSiteByExternalReferenceCodeSitePagePageSpecificationProblemException(
			LayoutUtilityPageEntryTestUtil.getLayoutUtilityPageEntryLayout(
				serviceContext));
	}

	@Override
	@Test
	public void testPutSiteSiteByExternalReferenceCodeSitePage()
		throws Exception {

		_testPutSiteSiteByExternalReferenceCodeSitePage(
			SitePage.Type.CONTENT_PAGE);
		_testPutSiteSiteByExternalReferenceCodeSitePage(
			SitePage.Type.WIDGET_PAGE);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		Layout layout = _addLayout(
			LayoutConstants.TYPE_CONTENT, null, serviceContext);

		_assertPutSiteSiteByExternalReferenceCodeSitePageProblemException(
			layout.fetchDraftLayout(),
			LayoutPageTemplateEntryTestUtil.
				getBasicLayoutPageTemplateEntryLayout(serviceContext),
			LayoutPageTemplateEntryTestUtil.
				getDisplayPageLayoutPageTemplateEntryLayout(serviceContext),
			LayoutPageTemplateEntryTestUtil.
				getMasterLayoutPageTemplateEntryLayout(serviceContext),
			LayoutUtilityPageEntryTestUtil.getLayoutUtilityPageEntryLayout(
				serviceContext));
	}

	@Ignore
	@Override
	@Test
	public void testPutSiteSitePagePermissionsPage() throws Exception {
		super.testPutSiteSitePagePermissionsPage();
	}

	@Override
	protected boolean equals(SitePage sitePage1, SitePage sitePage2) {
		super.equals(sitePage1, sitePage2);

		return Objects.deepEquals(
			sitePage1.getPageSettings(), sitePage2.getPageSettings());
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"externalReferenceCode", "friendlyUrlPath_i18n", "name_i18n",
			"type", "uuid"
		};
	}

	@Override
	protected SitePage randomSitePage() throws Exception {
		return _getRandomSitePage(_getRandomType(_types));
	}

	@Override
	protected SitePage
			testGetSiteSiteByExternalReferenceCodeSitePagesPage_addSitePage(
				String siteExternalReferenceCode, SitePage sitePage)
		throws Exception {

		return sitePageResource.postByExternalReferenceCodeSitePage(
			siteExternalReferenceCode, sitePage);
	}

	@Override
	protected String
		testGetSiteSiteByExternalReferenceCodeSitePagesPage_getIrrelevantSiteExternalReferenceCode() {

		return irrelevantGroup.getExternalReferenceCode();
	}

	@Override
	protected String
		testGetSiteSiteByExternalReferenceCodeSitePagesPage_getSiteExternalReferenceCode() {

		return testGroup.getExternalReferenceCode();
	}

	@Ignore
	@Override
	@Test
	protected SitePage testGetSiteSitePagePermissionsPage_addSitePage()
		throws Exception {

		return super.testGetSiteSitePagePermissionsPage_addSitePage();
	}

	@Override
	protected SitePage testPostByExternalReferenceCodeSitePage_addSitePage(
			SitePage sitePage)
		throws Exception {

		return sitePageResource.postByExternalReferenceCodeSitePage(
			testGroup.getExternalReferenceCode(), sitePage);
	}

	@Ignore
	@Override
	@Test
	protected SitePage testPutSiteSitePagePermissionsPage_addSitePage()
		throws Exception {

		return super.testPutSiteSitePagePermissionsPage_addSitePage();
	}

	private Layout _addLayout(
			String type, String typeSettings, ServiceContext serviceContext)
		throws Exception {

		return _layoutLocalService.addLayout(
			null, TestPropsValues.getUserId(), testGroup.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, 0, 0,
			RandomTestUtil.randomLocaleStringMap(), Collections.emptyMap(),
			Collections.emptyMap(), Collections.emptyMap(),
			Collections.emptyMap(), type, typeSettings, false, false,
			Collections.emptyMap(), 0L, serviceContext);
	}

	private void _assertContentSitePage(SitePage sitePage) {
		Assert.assertEquals(SitePage.Type.CONTENT_PAGE, sitePage.getType());

		Assert.assertTrue(
			sitePage.getPageSettings() instanceof ContentPageSettings);
	}

	private void
			_assertDeleteSiteSiteByExternalReferenceCodeSitePageProblemException(
				Layout... layouts)
		throws Exception {

		for (Layout layout : layouts) {
			_assertProblemException(
				() ->
					sitePageResource.
						deleteSiteSiteByExternalReferenceCodeSitePage(
							testGroup.getExternalReferenceCode(),
							layout.getExternalReferenceCode()));
		}
	}

	private void _assertMapEquals(
		Map<String, String> expectedMap, Map<String, String> map) {

		Assert.assertEquals(
			MapUtil.toString(map), expectedMap.size(), map.size());

		for (Map.Entry<String, String> entry : expectedMap.entrySet()) {
			Assert.assertEquals(entry.getValue(), map.get(entry.getKey()));
		}
	}

	private void _assertNestedFields(SitePage sitePage) throws Exception {
		FriendlyUrlHistory friendlyUrlHistory =
			sitePage.getFriendlyUrlHistory();

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			GetterUtil.getString(friendlyUrlHistory.getFriendlyUrlPath_i18n()));

		Layout layout = _layoutLocalService.getLayoutByExternalReferenceCode(
			sitePage.getExternalReferenceCode(), testGroup.getGroupId());

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
			layout, sitePage.getPageSpecifications());
	}

	private void _assertPatchSiteSiteByExternalReferenceCodeSitePage(
			SitePage expectedSitePage, SitePage sitePage)
		throws Exception {

		SitePage patchSitePage =
			sitePageResource.patchSiteSiteByExternalReferenceCodeSitePage(
				testGroup.getExternalReferenceCode(),
				sitePage.getExternalReferenceCode(), sitePage);

		equals(expectedSitePage, patchSitePage);
		assertValid(patchSitePage);

		_assertSitePage(
			_layoutLocalService.getLayoutByExternalReferenceCode(
				sitePage.getExternalReferenceCode(), testGroup.getGroupId()),
			patchSitePage);
	}

	private void
			_assertPatchSiteSiteByExternalReferenceCodeSitePageProblemException(
				Layout... layouts)
		throws Exception {

		for (Layout layout : layouts) {
			_assertPatchSiteSiteByExternalReferenceCodeSitePageProblemException(
				_getRandomSitePage(
					layout.getExternalReferenceCode(), null,
					SitePage.Type.CONTENT_PAGE, layout.getUuid()));
		}
	}

	private void
			_assertPatchSiteSiteByExternalReferenceCodeSitePageProblemException(
				SitePage sitePage)
		throws Exception {

		_assertProblemException(
			() -> sitePageResource.patchSiteSiteByExternalReferenceCodeSitePage(
				testGroup.getExternalReferenceCode(),
				sitePage.getExternalReferenceCode(), sitePage));
	}

	private void
			_assertPostSiteSiteByExternalReferenceCodeSitePagePageSpecificationProblemException(
				Layout layout)
		throws Exception {

		_assertProblemException(
			() ->
				sitePageResource.
					postSiteSiteByExternalReferenceCodeSitePagePageSpecification(
						testGroup.getExternalReferenceCode(),
						layout.getExternalReferenceCode(),
						new ContentPageSpecification() {
							{
								setExternalReferenceCode(
									layout::getExternalReferenceCode);
								setStatus(() -> Status.DRAFT);
								setType(() -> Type.CONTENT_PAGE_SPECIFICATION);
							}
						}));
	}

	private void _assertProblemException(
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try {
			unsafeRunnable.run();
			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}
	}

	private void
			_assertPutSiteSiteByExternalReferenceCodeSitePageProblemException(
				Layout... layouts)
		throws Exception {

		for (Layout layout : layouts) {
			_assertPutSiteSiteByExternalReferenceCodeSitePageProblemException(
				_getRandomSitePage(
					layout.getExternalReferenceCode(), null,
					SitePage.Type.CONTENT_PAGE, layout.getUuid()));
		}
	}

	private void
			_assertPutSiteSiteByExternalReferenceCodeSitePageProblemException(
				SitePage sitePage)
		throws Exception {

		_assertProblemException(
			() -> sitePageResource.putSiteSiteByExternalReferenceCodeSitePage(
				testGroup.getExternalReferenceCode(),
				sitePage.getExternalReferenceCode(), sitePage));
	}

	private void _assertSitePage(Layout layout, SitePage sitePage)
		throws Exception {

		Assert.assertArrayEquals(
			LocaleUtil.toW3cLanguageIds(layout.getAvailableLanguageIds()),
			sitePage.getAvailableLanguages());
		Assert.assertEquals(
			layout.getExternalReferenceCode(),
			sitePage.getExternalReferenceCode());

		_assertMapEquals(
			LocalizedMapUtil.getI18nMap(true, layout.getFriendlyURLMap()),
			sitePage.getFriendlyUrlPath_i18n());
		_assertMapEquals(
			LocalizedMapUtil.getI18nMap(true, layout.getNameMap()),
			sitePage.getName_i18n());

		if (layout.getParentLayoutId() == 0) {
			Assert.assertTrue(
				Validator.isNull(
					sitePage.getParentSitePageExternalReferenceCode()));
		}
		else {
			Layout parentLayout = _layoutLocalService.getLayout(
				layout.getGroupId(), layout.isPrivateLayout(),
				layout.getParentLayoutId());

			Assert.assertEquals(
				parentLayout.getExternalReferenceCode(),
				sitePage.getParentSitePageExternalReferenceCode());
		}

		Assert.assertEquals(layout.getUuid(), sitePage.getUuid());

		if (Objects.equals(layout.getType(), LayoutConstants.TYPE_CONTENT)) {
			_assertContentSitePage(sitePage);
		}
		else {
			_assertWidgetSitePage(layout, sitePage);
		}
	}

	private void _assertWidgetSitePage(Layout layout, SitePage sitePage) {
		Assert.assertEquals(SitePage.Type.WIDGET_PAGE, sitePage.getType());

		WidgetPageSettings widgetPageSettings =
			(WidgetPageSettings)sitePage.getPageSettings();

		Assert.assertEquals(
			layout.getTypeSettingsProperty(
				LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID),
			widgetPageSettings.getLayoutTemplateId());
	}

	private PageSettings _getPageSettings(SitePage.Type type) throws Exception {
		if (type == SitePage.Type.CONTENT_PAGE) {
			return new ContentPageSettings() {
				{
					setType(Type.CONTENT_PAGE_SETTINGS);
				}
			};
		}

		return new WidgetPageSettings() {
			{
				setLayoutTemplateId("1_column");
				setType(Type.WIDGET_PAGE_SETTINGS);
			}
		};
	}

	private SitePage _getRandomSitePage(SitePage.Type type) throws Exception {
		return _getRandomSitePage(
			StringUtil.toLowerCase(RandomTestUtil.randomString()), null, type,
			StringUtil.toLowerCase(RandomTestUtil.randomString()));
	}

	private SitePage _getRandomSitePage(
			String curExternalReferenceCode,
			String curParentSitePageExternalReferenceCode,
			SitePage.Type curType, String curUuid)
		throws Exception {

		SitePage sitePage = new SitePage() {
			{
				externalReferenceCode = curExternalReferenceCode;
				uuid = curUuid;
			}
		};

		sitePage.setAvailableLanguages(
			() -> LocaleUtil.toW3cLanguageIds(
				new Locale[] {LocaleUtil.US, LocaleUtil.SPAIN}));
		sitePage.setFriendlyUrlPath_i18n(
			() -> HashMapBuilder.put(
				LocaleUtil.toBCP47LanguageId(LocaleUtil.SPAIN),
				StringPool.FORWARD_SLASH +
					StringUtil.toLowerCase(RandomTestUtil.randomString())
			).put(
				LocaleUtil.toBCP47LanguageId(LocaleUtil.US),
				StringPool.FORWARD_SLASH +
					StringUtil.toLowerCase(RandomTestUtil.randomString())
			).build());
		sitePage.setName_i18n(
			() -> HashMapBuilder.put(
				LocaleUtil.toBCP47LanguageId(LocaleUtil.US),
				RandomTestUtil.randomString()
			).put(
				LocaleUtil.toBCP47LanguageId(LocaleUtil.SPAIN),
				RandomTestUtil.randomString()
			).build());
		sitePage.setPageSettings(
			() -> {
				PageSettings pageSettings = _getPageSettings(curType);

				pageSettings.setHiddenFromNavigation(
					RandomTestUtil::randomBoolean);

				return pageSettings;
			});
		sitePage.setParentSitePageExternalReferenceCode(
			() -> curParentSitePageExternalReferenceCode);
		sitePage.setType(() -> curType);

		return sitePage;
	}

	private SitePage.Type _getRandomType(List<SitePage.Type> types) {
		return types.get(RandomTestUtil.randomInt(0, types.size() - 1));
	}

	private SitePageResource _getSitePageResource() throws Exception {
		User user = UserTestUtil.getAdminUser(testCompany.getCompanyId());

		return SitePageResource.builder(
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

	private void _testDeleteSiteSiteByExternalReferenceCodeSitePage(
			Layout... layouts)
		throws Exception {

		for (Layout layout : layouts) {
			sitePageResource.deleteSiteSiteByExternalReferenceCodeSitePage(
				testGroup.getExternalReferenceCode(),
				layout.getExternalReferenceCode());

			Assert.assertNull(
				_layoutLocalService.fetchLayoutByExternalReferenceCode(
					layout.getExternalReferenceCode(), testGroup.getGroupId()));
		}
	}

	private void _testGetSiteSiteByExternalReferenceCodeSitePage(Layout layout)
		throws Exception {

		SitePage sitePage =
			sitePageResource.getSiteSiteByExternalReferenceCodeSitePage(
				testGroup.getExternalReferenceCode(),
				layout.getExternalReferenceCode());

		_assertSitePage(layout, sitePage);
		_testGetSiteSiteByExternalReferenceCodeSitePageWithNestedFields(
			sitePage);
	}

	private void
			_testGetSiteSiteByExternalReferenceCodeSitePageWithNestedFields(
				SitePage sitePage)
		throws Exception {

		SitePageResource sitePageResource = _getSitePageResource();

		_assertNestedFields(
			sitePageResource.getSiteSiteByExternalReferenceCodeSitePage(
				testGroup.getExternalReferenceCode(),
				sitePage.getExternalReferenceCode()));
	}

	private void _testPatchSiteSiteByExternalReferenceCodeSitePage(
			SitePage.Type type)
		throws Exception {

		SitePage sitePage = testPostByExternalReferenceCodeSitePage_addSitePage(
			_getRandomSitePage(type));

		_assertSitePage(
			_layoutLocalService.getLayoutByExternalReferenceCode(
				sitePage.getExternalReferenceCode(), testGroup.getGroupId()),
			sitePage);

		sitePage.setFriendlyUrlPath_i18n(
			() -> HashMapBuilder.put(
				LocaleUtil.toBCP47LanguageId(LocaleUtil.SPAIN),
				StringPool.FORWARD_SLASH +
					StringUtil.toLowerCase(RandomTestUtil.randomString())
			).put(
				LocaleUtil.toBCP47LanguageId(LocaleUtil.US),
				StringPool.FORWARD_SLASH +
					StringUtil.toLowerCase(RandomTestUtil.randomString())
			).build());

		_assertPatchSiteSiteByExternalReferenceCodeSitePage(
			sitePage,
			new SitePage() {
				{
					setExternalReferenceCode(
						sitePage::getExternalReferenceCode);
					setFriendlyUrlPath_i18n(sitePage::getFriendlyUrlPath_i18n);
					setType(sitePage::getType);
					setUuid(sitePage::getUuid);
				}
			});

		sitePage.setName_i18n(
			() -> HashMapBuilder.put(
				LocaleUtil.toBCP47LanguageId(LocaleUtil.US),
				RandomTestUtil.randomString()
			).put(
				LocaleUtil.toBCP47LanguageId(LocaleUtil.SPAIN),
				RandomTestUtil.randomString()
			).build());

		_assertPatchSiteSiteByExternalReferenceCodeSitePage(
			sitePage,
			new SitePage() {
				{
					setExternalReferenceCode(
						sitePage::getExternalReferenceCode);
					setName_i18n(sitePage::getName_i18n);
					setType(sitePage::getType);
					setUuid(sitePage::getUuid);
				}
			});

		PageSettings pageSettings = sitePage.getPageSettings();

		pageSettings.setHiddenFromNavigation(
			() -> !pageSettings.getHiddenFromNavigation());

		_assertPatchSiteSiteByExternalReferenceCodeSitePage(
			sitePage,
			new SitePage() {
				{
					setExternalReferenceCode(
						sitePage::getExternalReferenceCode);
					setPageSettings(sitePage::getPageSettings);
					setType(sitePage::getType);
					setUuid(sitePage::getUuid);
				}
			});

		Layout layout = LayoutTestUtil.addTypePortletLayout(testGroup);

		sitePage.setParentSitePageExternalReferenceCode(
			layout.getExternalReferenceCode());

		_assertPatchSiteSiteByExternalReferenceCodeSitePage(
			sitePage,
			new SitePage() {
				{
					setExternalReferenceCode(
						sitePage::getExternalReferenceCode);
					setParentSitePageExternalReferenceCode(
						layout::getExternalReferenceCode);
					setType(sitePage::getType);
					setUuid(sitePage::getUuid);
				}
			});

		_assertPatchSiteSiteByExternalReferenceCodeSitePageProblemException(
			_getRandomSitePage(
				sitePage.getExternalReferenceCode(), null,
				_getRandomType(
					ListUtil.filter(
						_types, curType -> !Objects.equals(curType, type))),
				sitePage.getUuid()));
	}

	private void _testPostByExternalReferenceCodeSitePage(SitePage sitePage)
		throws Exception {

		SitePage postSitePage =
			testPostByExternalReferenceCodeSitePage_addSitePage(sitePage);

		assertEquals(sitePage, postSitePage);
		assertValid(postSitePage);

		_assertSitePage(
			_layoutLocalService.getLayoutByExternalReferenceCode(
				sitePage.getExternalReferenceCode(), testGroup.getGroupId()),
			postSitePage);
	}

	private void _testPutSiteSiteByExternalReferenceCodeSitePage(
			SitePage.Type type)
		throws Exception {

		SitePage sitePage = testPostByExternalReferenceCodeSitePage_addSitePage(
			_getRandomSitePage(type));

		_assertSitePage(
			_layoutLocalService.getLayoutByExternalReferenceCode(
				sitePage.getExternalReferenceCode(), testGroup.getGroupId()),
			sitePage);

		Layout layout = LayoutTestUtil.addTypePortletLayout(testGroup);

		sitePage = _getRandomSitePage(
			sitePage.getExternalReferenceCode(),
			layout.getExternalReferenceCode(), type, sitePage.getUuid());

		SitePage putSitePage =
			sitePageResource.putSiteSiteByExternalReferenceCodeSitePage(
				testGroup.getExternalReferenceCode(),
				sitePage.getExternalReferenceCode(), sitePage);

		equals(sitePage, putSitePage);
		assertValid(putSitePage);

		_assertSitePage(
			_layoutLocalService.getLayoutByExternalReferenceCode(
				sitePage.getExternalReferenceCode(), testGroup.getGroupId()),
			sitePage);

		_assertPutSiteSiteByExternalReferenceCodeSitePageProblemException(
			_getRandomSitePage(
				sitePage.getExternalReferenceCode(), null,
				_getRandomType(
					ListUtil.filter(
						_types, curType -> !Objects.equals(curType, type))),
				sitePage.getUuid()));
	}

	private static final List<SitePage.Type> _types = Arrays.asList(
		SitePage.Type.CONTENT_PAGE, SitePage.Type.WIDGET_PAGE);

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private LayoutLocalService _layoutLocalService;

}