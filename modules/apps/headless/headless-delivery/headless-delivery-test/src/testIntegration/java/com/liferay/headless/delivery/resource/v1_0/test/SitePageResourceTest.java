/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0.test;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.entry.rel.service.AssetEntryAssetCategoryRelLocalService;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.model.ClientExtensionEntry;
import com.liferay.client.extension.model.ClientExtensionEntryRel;
import com.liferay.client.extension.service.ClientExtensionEntryLocalService;
import com.liferay.client.extension.service.ClientExtensionEntryRelLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalServiceUtil;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.headless.delivery.client.dto.v1_0.ClientExtension;
import com.liferay.headless.delivery.client.dto.v1_0.ContentDocument;
import com.liferay.headless.delivery.client.dto.v1_0.CustomField;
import com.liferay.headless.delivery.client.dto.v1_0.CustomValue;
import com.liferay.headless.delivery.client.dto.v1_0.OpenGraphSettings;
import com.liferay.headless.delivery.client.dto.v1_0.PageDefinition;
import com.liferay.headless.delivery.client.dto.v1_0.PageElement;
import com.liferay.headless.delivery.client.dto.v1_0.PagePermission;
import com.liferay.headless.delivery.client.dto.v1_0.PageSettings;
import com.liferay.headless.delivery.client.dto.v1_0.ParentSitePage;
import com.liferay.headless.delivery.client.dto.v1_0.SEOSettings;
import com.liferay.headless.delivery.client.dto.v1_0.Settings;
import com.liferay.headless.delivery.client.dto.v1_0.SiteMapSettings;
import com.liferay.headless.delivery.client.dto.v1_0.SitePage;
import com.liferay.headless.delivery.client.dto.v1_0.SitePageNavigationMenuSettings;
import com.liferay.headless.delivery.client.dto.v1_0.TaxonomyCategoryBrief;
import com.liferay.headless.delivery.client.dto.v1_0.TaxonomyCategoryReference;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.headless.delivery.client.problem.Problem;
import com.liferay.headless.delivery.client.resource.v1_0.SitePageResource;
import com.liferay.layout.admin.kernel.model.LayoutTypePortletConstants;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.util.PropsValues;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.constants.SegmentsExperienceConstants;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.test.util.SegmentsTestUtil;

import java.io.Serializable;

import java.net.URLEncoder;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@FeatureFlags("LPS-178052")
@RunWith(Arquillian.class)
public class SitePageResourceTest extends BaseSitePageResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_originalName = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(TestPropsValues.getUserId());

		SitePageResource.Builder builder = SitePageResource.builder();

		sitePageResource = builder.authentication(
			"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
		).header(
			"X-Liferay-Accept-All-Languages", "true"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		PrincipalThreadLocal.setName(_originalName);
	}

	@Override
	@Test
	public void testGetSiteSitePage() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(testGroup);

		String friendlyURL = layout.getFriendlyURL();

		SitePage sitePage = sitePageResource.getSiteSitePage(
			testGroup.getGroupId(), friendlyURL.substring(1));

		Assert.assertNotNull(sitePage);
		Assert.assertEquals(
			layout.getName(LocaleUtil.getDefault()), sitePage.getTitle());

		try {
			sitePageResource.getSiteSitePage(
				testGroup.getGroupId(), URLEncoder.encode("aaa/bbb"));

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Assert.assertNotNull(problemException);
		}
	}

	@Override
	@Test
	public void testGetSiteSitePageExperienceExperienceKey() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(testGroup);

		String friendlyURL = layout.getFriendlyURL();

		SitePage sitePage =
			sitePageResource.getSiteSitePageExperienceExperienceKey(
				testGroup.getGroupId(), friendlyURL.substring(1),
				SegmentsExperienceConstants.KEY_DEFAULT);

		Assert.assertNotNull(sitePage);
		Assert.assertNotNull(sitePage.getExperience());
	}

	@Override
	@Test
	public void testGetSiteSitePageExperienceExperienceKeyRenderedPage()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(testGroup);

		Layout draftLayout = layout.fetchDraftLayout();

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		String friendlyURL = layout.getFriendlyURL();

		SegmentsExperience segmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(
				testGroup.getGroupId(), draftLayout.getPlid());

		Assert.assertNotNull(
			sitePageResource.getSiteSitePageExperienceExperienceKeyRenderedPage(
				testGroup.getGroupId(), friendlyURL.substring(1),
				segmentsExperience.getSegmentsExperienceKey()));
	}

	@Override
	@Test
	public void testGetSiteSitePageRenderedPage() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(testGroup);

		String friendlyURL = layout.getFriendlyURL();

		Assert.assertNotNull(
			sitePageResource.getSiteSitePageRenderedPage(
				testGroup.getGroupId(), friendlyURL.substring(1)));
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteSitePagesExperiencesPage() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(testGroup);

		String friendlyURL = layout.getFriendlyURL();

		Page<SitePage> page = sitePageResource.getSiteSitePagesExperiencesPage(
			testGroup.getGroupId(), friendlyURL.substring(1));

		long originalPageCount = page.getTotalCount();

		Layout draftLayout = layout.fetchDraftLayout();

		SegmentsTestUtil.addSegmentsExperience(
			testGroup.getGroupId(), draftLayout.getPlid());

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		page = sitePageResource.getSiteSitePagesExperiencesPage(
			testGroup.getGroupId(), friendlyURL.substring(1));

		Assert.assertEquals(originalPageCount + 1, page.getTotalCount());
	}

	@Override
	@Test
	public void testGetSiteSitePagesPage() throws Exception {
		Page<SitePage> sitePagePage = sitePageResource.getSiteSitePagesPage(
			testGroup.getGroupId(), null, null, null, null, null);

		Assert.assertEquals(
			_layoutLocalService.getLayoutsCount(testGroup.getGroupId(), false),
			sitePagePage.getTotalCount());
	}

	@Test
	@TestInfo("LPD-35928")
	public void testGetSiteSitePagesPageSet() throws Exception {
		LayoutTestUtil.addTypeContentLayout(testGroup);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		Layout layout = _layoutLocalService.addLayout(
			null, serviceContext.getUserId(), testGroup.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			RandomTestUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			LayoutConstants.TYPE_NODE, false, StringPool.BLANK, serviceContext);

		_layoutLocalService.addLayout(
			null, serviceContext.getUserId(), testGroup.getGroupId(), false,
			layout.getLayoutId(), RandomTestUtil.randomString(),
			StringPool.BLANK, StringPool.BLANK, LayoutConstants.TYPE_PORTLET,
			false, StringPool.BLANK, serviceContext);

		Page<SitePage> sitePagePage = sitePageResource.getSiteSitePagesPage(
			testGroup.getGroupId(), null, null, null, null, null);

		List<String> pageTypes = TransformUtil.transform(
			sitePagePage.getItems(), SitePage::getPageType);

		Assert.assertTrue(pageTypes.contains("Page Set"));
	}

	@Override
	@Test
	public void testGraphQLGetSiteSitePagesPage() throws Exception {
		Long siteId = testGetSiteSitePagesPage_getSiteId();

		Layout layout = LayoutTestUtil.addTypeContentLayout(
			_groupLocalService.fetchGroup(siteId));

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		BaseSitePageResourceTestCase.GraphQLField graphQLField =
			new BaseSitePageResourceTestCase.GraphQLField(
				"sitePages",
				HashMapBuilder.<String, Object>put(
					"siteKey", "\"" + siteId + "\""
				).build(),
				new BaseSitePageResourceTestCase.GraphQLField(
					"items", getGraphQLFields()),
				new BaseSitePageResourceTestCase.GraphQLField("page"),
				new BaseSitePageResourceTestCase.GraphQLField("totalCount"));

		JSONObject sitePagesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/sitePages");

		Assert.assertEquals(
			_layoutLocalService.getLayoutsCount(testGroup.getGroupId(), false),
			sitePagesJSONObject.get("totalCount"));
	}

	@FeatureFlags("LPS-178052")
	@Override
	@Test
	public void testPostSiteSitePage() throws Exception {
		super.testPostSiteSitePage();

		_testPostSiteSitePageFailureDuplicateFriendlyURL();
		_testPostSiteSitePageFailureInvalidTitle();
		_testPostSiteSitePageFailureFriendlyURLContainsDoubleSlash();
		_testPostSiteSitePageFailureFriendlyURLContainsInvalidCharacters();
		_testPostSiteSitePageFailureFriendlyURLEndsWithSlash();
		_testPostSiteSitePageFailureFriendlyURLTooLong();
		_testPostSiteSitePageFailureFriendlyURLTooShort();
		_testPostSiteSitePageFailurePageDefinitionSettingsClientExtensions();
		_testPostSiteSitePageFailurePagePermissionsActionKeyNonexisting();
		_testPostSiteSitePageSuccessCustomFields();
		_testPostSiteSitePageSuccessInvalidParentSitePage();
		_testPostSiteSitePageSuccessKeywords();
		_testPostSiteSitePageSuccessPageDefinition();
		_testPostSiteSitePageSuccessPageDefinitionSettingsClientExtensionEntries();
		_testPostSiteSitePageSuccessPageDefinitionSettingsFaviconFromClientExtensionEntry();
		_testPostSiteSitePageSuccessPageDefinitionSettingsFaviconFromDocument();
		_testPostSiteSitePageSuccessPagePermissions();
		_testPostSiteSitePageSuccessPagePermissionsActionKeysEmpty();
		_testPostSiteSitePageSuccessPagePermissionsEmpty();
		_testPostSiteSitePageSuccessPagePermissionsNull();
		_testPostSiteSitePageSuccessPagePermissionsRoleNonexisting();
		_testPostSiteSitePageSuccessPagePermissionsRoleOwnerMissing();
		_testPostSiteSitePageSuccessPageSettingsOpenGraphSettings();
		_testPostSiteSitePageSuccessPageSettingsSeoSettings();
		_testPostSiteSitePageSuccessPageSettingsSiteNavigationMenuSettings();
		_testPostSiteSitePageSuccessParentSitePage();
		_testPostSiteSitePageSuccessParentSitePageNonexisting();
		_testPostSiteSitePageSuccessTaxonomyCategoryBriefNonexisting();
		_testPostSiteSitePageSuccessTaxonomyCategoryBriefSitePageSiteSiteKeyNull();
		_testPostSiteSitePageSuccessTaxonomyCategoryBriefSitePageSiteSiteKeyNonnull();
		_testPostSiteSitePageSuccessTaxonomyCategoryBriefNonsitePage();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"friendlyUrlPath", "title"};
	}

	@Override
	protected Collection<EntityField> getEntityFields() throws Exception {
		return super.getEntityFields();
	}

	@Override
	protected SitePage randomSitePage() {
		return new SitePage() {
			{
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				datePublished = RandomTestUtil.nextDate();
				friendlyUrlPath =
					StringPool.FORWARD_SLASH +
						StringUtil.toLowerCase(RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				pageType = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				siteId = testGroup.getGroupId();
				title = StringUtil.toLowerCase(RandomTestUtil.randomString());
				uuid = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	@Override
	protected String
			testGraphQLGetSiteSitePageExperienceExperienceKey_getExperienceKey()
		throws Exception {

		return SegmentsEntryConstants.KEY_DEFAULT;
	}

	private ClientExtensionEntry _addClientExtension(String type)
		throws Exception {

		return _clientExtensionEntryLocalService.addClientExtensionEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			StringPool.BLANK,
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			StringPool.BLANK, StringPool.BLANK, type,
			UnicodePropertiesBuilder.create(
				true
			).put(
				"url", "http://localhost"
			).buildString());
	}

	private void _assertEqualsIgnoringOrder(
		TaxonomyCategoryBrief[] taxonomyCategoryBriefs1,
		TaxonomyCategoryBrief[] taxonomyCategoryBriefs2) {

		Assert.assertEquals(
			Arrays.toString(taxonomyCategoryBriefs2),
			taxonomyCategoryBriefs1.length, taxonomyCategoryBriefs2.length);

		for (TaxonomyCategoryBrief taxonomyCategoryBrief1 :
				taxonomyCategoryBriefs1) {

			boolean contains = false;

			for (TaxonomyCategoryBrief taxonomyCategoryBrief2 :
					taxonomyCategoryBriefs2) {

				if (taxonomyCategoryBrief1.equals(taxonomyCategoryBrief2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				Arrays.toString(taxonomyCategoryBriefs2) +
					" does not contain " + taxonomyCategoryBrief1,
				contains);
		}
	}

	private void _testPostSiteSitePageFailureDuplicateFriendlyURL()
		throws Exception {

		SitePage randomSitePage = randomSitePage();

		testPostSiteSitePage_addSitePage(randomSitePage);

		try {
			testPostSiteSitePage_addSitePage(randomSitePage);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(
				"LayoutFriendlyURLsException", problem.getType());
		}
	}

	private void _testPostSiteSitePageFailureFriendlyURLContainsDoubleSlash()
		throws Exception {

		SitePage randomSitePage = randomSitePage();

		randomSitePage.setFriendlyUrlPath(
			RandomTestUtil.randomString() + StringPool.DOUBLE_SLASH +
				RandomTestUtil.randomString());

		try {
			testPostSiteSitePage_addSitePage(randomSitePage);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(
				"LayoutFriendlyURLsException", problem.getType());
		}
	}

	private void _testPostSiteSitePageFailureFriendlyURLContainsInvalidCharacters()
		throws Exception {

		SitePage randomSitePage = randomSitePage();

		randomSitePage.setFriendlyUrlPath("-%.+\\*_");

		try {
			testPostSiteSitePage_addSitePage(randomSitePage);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(
				"LayoutFriendlyURLsException", problem.getType());
		}
	}

	private void _testPostSiteSitePageFailureFriendlyURLEndsWithSlash()
		throws Exception {

		SitePage randomSitePage = randomSitePage();

		randomSitePage.setFriendlyUrlPath(
			RandomTestUtil.randomString() + StringPool.FORWARD_SLASH);

		try {
			testPostSiteSitePage_addSitePage(randomSitePage);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(
				"LayoutFriendlyURLsException", problem.getType());
		}
	}

	private void _testPostSiteSitePageFailureFriendlyURLTooLong()
		throws Exception {

		SitePage randomSitePage = randomSitePage();

		randomSitePage.setFriendlyUrlPath(
			RandomTestUtil.randomString(
				LayoutConstants.FRIENDLY_URL_MAX_LENGTH + 1));

		try {
			testPostSiteSitePage_addSitePage(randomSitePage);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(
				"LayoutFriendlyURLsException", problem.getType());
		}
	}

	private void _testPostSiteSitePageFailureFriendlyURLTooShort()
		throws Exception {

		SitePage randomSitePage = randomSitePage();

		randomSitePage.setFriendlyUrlPath("a");

		try {
			testPostSiteSitePage_addSitePage(randomSitePage);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(
				"LayoutFriendlyURLsException", problem.getType());
		}
	}

	private void _testPostSiteSitePageFailureInvalidTitle() throws Exception {
		SitePage randomSitePage = randomSitePage();

		int maxLength = ModelHintsUtil.getMaxLength(
			Layout.class.getName(), "friendlyURL");

		randomSitePage.setTitle(RandomTestUtil.randomString(maxLength + 1));

		try {
			testPostSiteSitePage_addSitePage(randomSitePage);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals("LayoutNameException", problem.getType());
		}
	}

	private void _testPostSiteSitePageFailurePageDefinitionSettingsClientExtensions()
		throws Exception {

		SitePage randomSitePage = randomSitePage();

		ClientExtensionEntry globalCSSClientExtensionEntry =
			_addClientExtension(ClientExtensionEntryConstants.TYPE_GLOBAL_CSS);

		randomSitePage.setPageDefinition(
			new PageDefinition() {
				{
					settings = new Settings() {
						{
							globalJSClientExtensions = new ClientExtension[] {
								new ClientExtension() {
									{
										externalReferenceCode =
											globalCSSClientExtensionEntry.
												getExternalReferenceCode();
									}
								}
							};
						}
					};
				}
			});

		SitePage postSitePage = testPostSiteSitePage_addSitePage(
			randomSitePage);

		Layout layout = _layoutLocalService.fetchLayout(postSitePage.getId());

		Assert.assertNotNull(layout);

		Assert.assertNull(
			_clientExtensionEntryRelLocalService.fetchClientExtensionEntryRel(
				_portal.getClassNameId(Layout.class.getName()),
				layout.getPlid(),
				ClientExtensionEntryConstants.TYPE_GLOBAL_CSS));
		Assert.assertNull(
			_clientExtensionEntryRelLocalService.fetchClientExtensionEntryRel(
				_portal.getClassNameId(Layout.class.getName()),
				layout.getPlid(),
				ClientExtensionEntryConstants.TYPE_GLOBAL_JS));
	}

	private void _testPostSiteSitePageFailurePagePermissionsActionKeyNonexisting()
		throws Exception {

		SitePage randomSitePage = randomSitePage();

		PagePermission[] inputPagePermissions = {
			new PagePermission() {
				{
					actionKeys = new String[] {RandomTestUtil.randomString()};
					roleKey = RoleConstants.OWNER;
				}
			}
		};

		randomSitePage.setPagePermissions(inputPagePermissions);

		try {
			testPostSiteSitePage_addSitePage(randomSitePage);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}
	}

	private void _testPostSiteSitePageSuccessCustomFields() throws Exception {
		SitePage randomSitePage = randomSitePage();

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));

			ExpandoTable expandoTable =
				_expandoTableLocalService.addDefaultTable(
					PortalUtil.getDefaultCompanyId(), Layout.class.getName());

			String randomExpandoAttributeName = RandomTestUtil.randomString();

			_expandoColumnLocalService.addColumn(
				expandoTable.getTableId(), randomExpandoAttributeName,
				ExpandoColumnConstants.STRING, StringPool.BLANK);

			try {
				String randomCustomValue = RandomTestUtil.randomString();

				randomSitePage.setCustomFields(
					new CustomField[] {
						new CustomField() {
							{
								customValue = new CustomValue() {
									{
										data = randomCustomValue;
									}
								};
								name = randomExpandoAttributeName;
							}
						}
					});

				SitePage postSitePage = testPostSiteSitePage_addSitePage(
					randomSitePage);

				Layout layout = _layoutLocalService.fetchLayout(
					postSitePage.getId());

				Assert.assertNotNull(layout);

				ExpandoBridge expandoBridge = layout.getExpandoBridge();

				Assert.assertNotNull(expandoBridge);

				Map<String, Serializable> attributes =
					expandoBridge.getAttributes();

				Assert.assertEquals(
					attributes.get(randomExpandoAttributeName),
					randomCustomValue);
			}
			finally {
				ExpandoTableLocalServiceUtil.deleteTable(expandoTable);
			}
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}
	}

	private void _testPostSiteSitePageSuccessInvalidParentSitePage()
		throws Exception {

		SitePage randomSitePage = randomSitePage();

		randomSitePage.setParentSitePage(
			new ParentSitePage() {
				{
					friendlyUrlPath = RandomTestUtil.randomString();
				}
			});

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME_EXCEPTION_MAPPER, LoggerTestUtil.WARN)) {

			SitePage postSitePage = testPostSiteSitePage_addSitePage(
				randomSitePage);

			Layout layout = _layoutLocalService.fetchLayout(
				postSitePage.getId());

			Assert.assertNotNull(layout);

			Assert.assertEquals(0, layout.getParentPlid());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Unable to get parent layout", logEntry.getMessage());
		}
	}

	private void _testPostSiteSitePageSuccessKeywords() throws Exception {
		SitePage randomSitePage = randomSitePage();

		String[] keywords = {
			RandomTestUtil.randomString(), RandomTestUtil.randomString()
		};

		randomSitePage.setKeywords(keywords);

		SitePage postSitePage = testPostSiteSitePage_addSitePage(
			randomSitePage);

		Layout layout = _layoutLocalService.fetchLayout(postSitePage.getId());

		Assert.assertNotNull(layout);

		String[] tags = ListUtil.toArray(
			_assetTagLocalService.getTags(
				Layout.class.getName(), layout.getPlid()),
			AssetTag.NAME_ACCESSOR);

		Assert.assertEquals(Arrays.toString(tags), 2, tags.length);

		for (String keyword : keywords) {
			Assert.assertTrue(ArrayUtil.contains(tags, keyword));
		}
	}

	private void _testPostSiteSitePageSuccessPageDefinition() throws Exception {
		SitePage randomSitePage = randomSitePage();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(testGroup.getGroupId());

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, testGroup.getCreatorUserId(), testGroup.getGroupId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				serviceContext);

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), testGroup.getGroupId(),
				fragmentCollection.getFragmentCollectionId(), null,
				RandomTestUtil.randomString(), StringPool.BLANK,
				"<lfr-editable id=\"fragmentEditableId\" type=\"text\">" +
					"Default Fragment Text</lfr-editable>",
				StringPool.BLANK, false, null, null, 0, false,
				FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED, serviceContext);

		PageElement originalFragmentPageElement = new PageElement() {
			{
				definition = JSONUtil.put(
					"fragment",
					JSONUtil.put(
						"key", fragmentEntry.getFragmentEntryKey()
					).put(
						"siteKey", testGroup.getGroupKey()
					)
				).put(
					"fragmentConfig", JSONFactoryUtil.createJSONObject()
				).put(
					"fragmentFields",
					JSONUtil.put(
						JSONUtil.put(
							"id", "fragmentEditableId"
						).put(
							"value",
							JSONUtil.put(
								"fragmentLink",
								JSONFactoryUtil.createJSONObject()
							).put(
								"text",
								JSONUtil.put(
									"value_i18n",
									JSONUtil.put(
										"en_US", "English Text"
									).put(
										"es_ES", "Spanish Text"
									))
							)
						))
				).put(
					"indexed", true
				);
				type = Type.FRAGMENT;
			}
		};

		randomSitePage.setPageDefinition(
			new PageDefinition() {
				{
					pageElement = new PageElement() {
						{
							type = Type.ROOT;

							setPageElements(
								() -> new PageElement[] {
									originalFragmentPageElement
								});
						}
					};
				}
			});

		SitePage postSitePage = testPostSiteSitePage_addSitePage(
			randomSitePage);

		PageDefinition pageDefinition = postSitePage.getPageDefinition();

		Assert.assertNotNull(pageDefinition);

		PageElement rootPageElement = pageDefinition.getPageElement();

		Assert.assertNotNull(rootPageElement);

		Assert.assertEquals(PageElement.Type.ROOT, rootPageElement.getType());

		PageElement[] pageElements = rootPageElement.getPageElements();

		Assert.assertNotNull(pageElements);

		Assert.assertEquals(pageElements.toString(), 1, pageElements.length);

		PageElement actualFragmentPageElement = pageElements[0];

		JSONObject originalDefinitionJSONObject =
			(JSONObject)originalFragmentPageElement.getDefinition();

		Assert.assertEquals(
			_objectMapper.readTree(originalDefinitionJSONObject.toString()),
			_objectMapper.readTree(
				(String)actualFragmentPageElement.getDefinition()));
	}

	private void _testPostSiteSitePageSuccessPageDefinitionSettingsClientExtensionEntries()
		throws Exception {

		SitePage randomSitePage = randomSitePage();

		ClientExtensionEntry globalCSSClientExtensionEntry =
			_addClientExtension(ClientExtensionEntryConstants.TYPE_GLOBAL_CSS);

		ClientExtensionEntry globalJSClientExtensionEntry = _addClientExtension(
			ClientExtensionEntryConstants.TYPE_GLOBAL_JS);

		randomSitePage.setPageDefinition(
			new PageDefinition() {
				{
					settings = new Settings() {
						{
							globalCSSClientExtensions = new ClientExtension[] {
								new ClientExtension() {
									{
										externalReferenceCode =
											globalCSSClientExtensionEntry.
												getExternalReferenceCode();
									}
								}
							};
							globalJSClientExtensions = new ClientExtension[] {
								new ClientExtension() {
									{
										externalReferenceCode =
											globalJSClientExtensionEntry.
												getExternalReferenceCode();
									}
								}
							};
						}
					};
				}
			});

		SitePage postSitePage = testPostSiteSitePage_addSitePage(
			randomSitePage);

		Layout layout = _layoutLocalService.fetchLayout(postSitePage.getId());

		Assert.assertNotNull(layout);

		ClientExtensionEntryRel globalCSSClientExtensionEntryRel =
			_clientExtensionEntryRelLocalService.fetchClientExtensionEntryRel(
				_portal.getClassNameId(Layout.class.getName()),
				layout.getPlid(),
				ClientExtensionEntryConstants.TYPE_GLOBAL_CSS);

		Assert.assertNotNull(globalCSSClientExtensionEntryRel);

		Assert.assertEquals(
			globalCSSClientExtensionEntry.getExternalReferenceCode(),
			globalCSSClientExtensionEntryRel.getCETExternalReferenceCode());

		ClientExtensionEntryRel globalJSClientExtensionEntryRel =
			_clientExtensionEntryRelLocalService.fetchClientExtensionEntryRel(
				_portal.getClassNameId(Layout.class.getName()),
				layout.getPlid(), ClientExtensionEntryConstants.TYPE_GLOBAL_JS);

		Assert.assertNotNull(globalJSClientExtensionEntryRel);

		Assert.assertEquals(
			globalJSClientExtensionEntry.getExternalReferenceCode(),
			globalJSClientExtensionEntryRel.getCETExternalReferenceCode());
	}

	private void _testPostSiteSitePageSuccessPageDefinitionSettingsFaviconFromClientExtensionEntry()
		throws Exception {

		SitePage randomSitePage = randomSitePage();

		ClientExtensionEntry clientExtensionEntry = _addClientExtension(
			ClientExtensionEntryConstants.TYPE_THEME_FAVICON);

		randomSitePage.setPageDefinition(
			new PageDefinition() {
				{
					settings = new Settings() {
						{
							favIcon = JSONUtil.put(
								"externalReferenceCode",
								clientExtensionEntry.
									getExternalReferenceCode());
						}
					};
				}
			});

		SitePage postSitePage = testPostSiteSitePage_addSitePage(
			randomSitePage);

		Layout layout = _layoutLocalService.fetchLayout(postSitePage.getId());

		Assert.assertNotNull(layout);

		ClientExtensionEntryRel clientExtensionEntryRel =
			_clientExtensionEntryRelLocalService.fetchClientExtensionEntryRel(
				_portal.getClassNameId(Layout.class.getName()),
				layout.getPlid(),
				ClientExtensionEntryConstants.TYPE_THEME_FAVICON);

		Assert.assertNotNull(clientExtensionEntryRel);

		Assert.assertEquals(
			clientExtensionEntry.getExternalReferenceCode(),
			clientExtensionEntryRel.getCETExternalReferenceCode());
	}

	private void _testPostSiteSitePageSuccessPageDefinitionSettingsFaviconFromDocument()
		throws Exception {

		SitePage randomSitePage = randomSitePage();

		DLFolder dlFolder = DLTestUtil.addDLFolder(testGroup.getGroupId());

		DLFileEntry dlFileEntry = DLTestUtil.addDLFileEntry(
			dlFolder.getFolderId());

		randomSitePage.setPageDefinition(
			new PageDefinition() {
				{
					settings = new Settings() {
						{
							favIcon = JSONUtil.put(
								"contentType", "Document"
							).put(
								"id", dlFileEntry.getFileEntryId()
							);
						}
					};
				}
			});

		SitePage postSitePage = testPostSiteSitePage_addSitePage(
			randomSitePage);

		Layout layout = _layoutLocalService.fetchLayout(postSitePage.getId());

		Assert.assertNotNull(layout);

		Assert.assertEquals(
			dlFileEntry.getFileEntryId(), layout.getFaviconFileEntryId());
	}

	private void _testPostSiteSitePageSuccessPagePermissions()
		throws Exception {

		PagePermission[] expectedPagePermissions = {
			new PagePermission() {
				{
					actionKeys = new String[] {
						ActionKeys.DELETE_DISCUSSION,
						ActionKeys.UPDATE_LAYOUT_LIMITED
					};
					roleKey = RoleConstants.OWNER;
				}
			},
			new PagePermission() {
				{
					actionKeys = new String[] {ActionKeys.VIEW};
					roleKey = RoleConstants.SITE_MEMBER;
				}
			}
		};

		_testPostSiteSitePageSuccessPagePermissions(
			expectedPagePermissions, expectedPagePermissions);
	}

	private void _testPostSiteSitePageSuccessPagePermissions(
			PagePermission[] expectedPagePermissions,
			PagePermission[] inputPagePermissions)
		throws Exception {

		SitePage randomSitePage = randomSitePage();

		randomSitePage.setPagePermissions(inputPagePermissions);

		SitePage postSitePage = testPostSiteSitePage_addSitePage(
			randomSitePage);

		Layout layout = _layoutLocalService.fetchLayout(postSitePage.getId());

		Assert.assertNotNull(layout);

		List<ResourcePermission> resourcePermissions =
			_resourcePermissionLocalService.getResourcePermissions(
				layout.getCompanyId(), Layout.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(layout.getPlid()));

		Assert.assertEquals(
			resourcePermissions.toString(), expectedPagePermissions.length,
			resourcePermissions.size());

		List<ResourceAction> resourceActions =
			_resourceActionLocalService.getResourceActions(
				Layout.class.getName());

		for (PagePermission pagePermission : expectedPagePermissions) {
			Role role = _roleLocalService.getRole(
				testGroup.getCompanyId(), pagePermission.getRoleKey());

			ResourcePermission resourcePermission =
				_resourcePermissionLocalService.fetchResourcePermission(
					testGroup.getCompanyId(), Layout.class.getName(),
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(layout.getPlid()), role.getRoleId());

			Set<String> actionIdsSet = new HashSet<>();

			long actionIds = resourcePermission.getActionIds();

			for (ResourceAction resourceAction : resourceActions) {
				long bitwiseValue = resourceAction.getBitwiseValue();

				if ((actionIds & bitwiseValue) == bitwiseValue) {
					actionIdsSet.add(resourceAction.getActionId());
				}
			}

			String[] actionKeys = pagePermission.getActionKeys();

			Assert.assertEquals(
				actionIdsSet.toString(), actionKeys.length,
				actionIdsSet.size());

			for (String actionKey : actionKeys) {
				Assert.assertTrue(actionIdsSet.contains(actionKey));
			}
		}
	}

	private void _testPostSiteSitePageSuccessPagePermissionsActionKeysEmpty()
		throws Exception {

		PagePermission[] expectedPagePermissions = {
			new PagePermission() {
				{
					actionKeys = new String[] {
						ActionKeys.DELETE_DISCUSSION,
						ActionKeys.UPDATE_LAYOUT_LIMITED
					};
					roleKey = RoleConstants.OWNER;
				}
			}
		};
		PagePermission[] inputPagePermissions = {
			new PagePermission() {
				{
					actionKeys = new String[] {
						ActionKeys.DELETE_DISCUSSION,
						ActionKeys.UPDATE_LAYOUT_LIMITED
					};
					roleKey = RoleConstants.OWNER;
				}
			},
			new PagePermission() {
				{
					actionKeys = new String[0];
					roleKey = RoleConstants.SITE_MEMBER;
				}
			}
		};

		_testPostSiteSitePageSuccessPagePermissions(
			expectedPagePermissions, inputPagePermissions);
	}

	private void _testPostSiteSitePageSuccessPagePermissionsEmpty()
		throws Exception {

		PagePermission[] expectedPagePermissions = {_PAGE_PERMISSIONS};
		PagePermission[] inputPagePermissions = {};

		_testPostSiteSitePageSuccessPagePermissions(
			expectedPagePermissions, inputPagePermissions);
	}

	private void _testPostSiteSitePageSuccessPagePermissionsNull()
		throws Exception {

		PagePermission[] expectedPagePermissions = {
			_PAGE_PERMISSIONS,
			new PagePermission() {
				{
					actionKeys = new String[] {
						ActionKeys.CUSTOMIZE, ActionKeys.VIEW,
						ActionKeys.ADD_DISCUSSION
					};
					roleKey = RoleConstants.SITE_MEMBER;
				}
			},
			new PagePermission() {
				{
					actionKeys = new String[] {ActionKeys.VIEW};
					roleKey = RoleConstants.GUEST;
				}
			}
		};

		_testPostSiteSitePageSuccessPagePermissions(
			expectedPagePermissions, null);
	}

	private void _testPostSiteSitePageSuccessPagePermissionsRoleNonexisting()
		throws Exception {

		PagePermission[] expectedPagePermissions = {
			new PagePermission() {
				{
					actionKeys = new String[] {ActionKeys.UPDATE};
					roleKey = RoleConstants.OWNER;
				}
			}
		};
		PagePermission[] inputPagePermissions = {
			new PagePermission() {
				{
					actionKeys = new String[] {ActionKeys.UPDATE};
					roleKey = RoleConstants.OWNER;
				}
			},
			new PagePermission() {
				{
					actionKeys = new String[] {ActionKeys.VIEW};
					roleKey = RandomTestUtil.randomString();
				}
			}
		};

		_testPostSiteSitePageSuccessPagePermissions(
			expectedPagePermissions, inputPagePermissions);
	}

	private void _testPostSiteSitePageSuccessPagePermissionsRoleOwnerMissing()
		throws Exception {

		PagePermission[] expectedPagePermissions = {
			_PAGE_PERMISSIONS,
			new PagePermission() {
				{
					actionKeys = new String[] {ActionKeys.VIEW};
					roleKey = RoleConstants.GUEST;
				}
			}
		};
		PagePermission[] inputPagePermissions = {
			new PagePermission() {
				{
					actionKeys = new String[] {ActionKeys.VIEW};
					roleKey = RoleConstants.GUEST;
				}
			}
		};

		_testPostSiteSitePageSuccessPagePermissions(
			expectedPagePermissions, inputPagePermissions);
	}

	private void _testPostSiteSitePageSuccessPageSettingsOpenGraphSettings()
		throws Exception {

		SitePage randomSitePage = randomSitePage();

		String randomDescription = RandomTestUtil.randomString();
		String randomImageAlt = RandomTestUtil.randomString();
		String randomTitle = RandomTestUtil.randomString();

		OpenGraphSettings randomOpenGraphSettings = new OpenGraphSettings() {
			{
				description = randomDescription;
				description_i18n = HashMapBuilder.put(
					"en-US", randomDescription
				).put(
					"es-ES", RandomTestUtil.randomString()
				).build();
				imageAlt = randomImageAlt;
				imageAlt_i18n = HashMapBuilder.put(
					"en-US", randomImageAlt
				).put(
					"es-ES", RandomTestUtil.randomString()
				).build();
				title = randomTitle;
				title_i18n = HashMapBuilder.put(
					"en-US", randomTitle
				).put(
					"es-ES", RandomTestUtil.randomString()
				).build();

				setImage(
					() -> {
						DLFolder dlFolder = DLTestUtil.addDLFolder(
							testGroup.getGroupId());

						DLFileEntry dlFileEntry = DLTestUtil.addDLFileEntry(
							dlFolder.getFolderId());

						return new ContentDocument() {
							{
								id = dlFileEntry.getPrimaryKey();
							}
						};
					});
			}
		};

		randomSitePage.setPageSettings(
			new PageSettings() {
				{
					openGraphSettings = randomOpenGraphSettings;
				}
			});

		SitePage postSitePage = testPostSiteSitePage_addSitePage(
			randomSitePage);

		PageSettings postPageSettings = postSitePage.getPageSettings();

		OpenGraphSettings postOpenGraphSettings =
			postPageSettings.getOpenGraphSettings();

		Assert.assertEquals(
			randomOpenGraphSettings.getDescription(),
			postOpenGraphSettings.getDescription());
		Assert.assertEquals(
			randomOpenGraphSettings.getDescription_i18n(),
			postOpenGraphSettings.getDescription_i18n());

		ContentDocument randomContentDocument =
			randomOpenGraphSettings.getImage();
		ContentDocument contentDocument = postOpenGraphSettings.getImage();

		Assert.assertEquals(
			randomContentDocument.getId(), contentDocument.getId());

		Assert.assertEquals(
			randomOpenGraphSettings.getImageAlt(),
			postOpenGraphSettings.getImageAlt());
		Assert.assertEquals(
			randomOpenGraphSettings.getImageAlt_i18n(),
			postOpenGraphSettings.getImageAlt_i18n());
		Assert.assertEquals(
			randomOpenGraphSettings.getTitle(),
			postOpenGraphSettings.getTitle());
		Assert.assertEquals(
			randomOpenGraphSettings.getTitle_i18n(),
			postOpenGraphSettings.getTitle_i18n());
	}

	private void _testPostSiteSitePageSuccessPageSettingsSeoSettings()
		throws Exception {

		SitePage randomSitePage = randomSitePage();

		String randomCustomCanonicalURL = RandomTestUtil.randomString();
		String randomDescription = RandomTestUtil.randomString();
		String randomHtmlTitle = RandomTestUtil.randomString();
		String randomRobots = RandomTestUtil.randomString();
		String randomSeoKeywords = RandomTestUtil.randomString();

		PageSettings pageSettings = new PageSettings() {
			{
				seoSettings = new SEOSettings() {
					{
						customCanonicalURL = randomCustomCanonicalURL;
						customCanonicalURL_i18n = HashMapBuilder.put(
							"en-US", randomCustomCanonicalURL
						).put(
							"es-ES", RandomTestUtil.randomString()
						).build();
						description = randomDescription;
						description_i18n = HashMapBuilder.put(
							"en-US", randomDescription
						).put(
							"es-ES", RandomTestUtil.randomString()
						).build();
						htmlTitle = randomHtmlTitle;
						htmlTitle_i18n = HashMapBuilder.put(
							"en-US", randomHtmlTitle
						).put(
							"es-ES", RandomTestUtil.randomString()
						).build();
						robots = randomRobots;
						robots_i18n = HashMapBuilder.put(
							"en-US", randomRobots
						).put(
							"es-ES", RandomTestUtil.randomString()
						).build();
						seoKeywords = randomSeoKeywords;
						seoKeywords_i18n = HashMapBuilder.put(
							"en-US", randomSeoKeywords
						).put(
							"es-ES", RandomTestUtil.randomString()
						).build();
						siteMapSettings = new SiteMapSettings() {
							{
								changeFrequency = ChangeFrequency.ALWAYS;
								include = RandomTestUtil.randomBoolean();
								includeChildSitePages =
									RandomTestUtil.randomBoolean();
								pagePriority = RandomTestUtil.randomDouble();
							}
						};
					}
				};
			}
		};

		randomSitePage.setPageSettings(pageSettings);

		SitePage postSitePage = testPostSiteSitePage_addSitePage(
			randomSitePage);

		PageSettings postPageSettings = postSitePage.getPageSettings();

		Assert.assertEquals(
			pageSettings.getSeoSettings(), postPageSettings.getSeoSettings());
	}

	private void _testPostSiteSitePageSuccessPageSettingsSiteNavigationMenuSettings()
		throws Exception {

		SitePage randomSitePage = randomSitePage();

		String randomQueryString = RandomTestUtil.randomString();
		String randomTarget = RandomTestUtil.randomString();

		PageSettings pageSettings = new PageSettings() {
			{
				sitePageNavigationMenuSettings =
					new SitePageNavigationMenuSettings() {
						{
							queryString = randomQueryString;
							target = randomTarget;
						}
					};
			}
		};

		randomSitePage.setPageSettings(pageSettings);

		SitePage postSitePage = testPostSiteSitePage_addSitePage(
			randomSitePage);

		Layout layout = _layoutLocalService.fetchLayout(postSitePage.getId());

		Assert.assertNotNull(layout);

		UnicodeProperties typeSettingsUnicodeProperties =
			layout.getTypeSettingsProperties();

		Assert.assertEquals(
			typeSettingsUnicodeProperties.get(
				LayoutTypePortletConstants.QUERY_STRING),
			randomQueryString);
		Assert.assertEquals(
			typeSettingsUnicodeProperties.get(
				LayoutTypePortletConstants.TARGET),
			randomTarget);

		Assert.assertNull(typeSettingsUnicodeProperties.get("targetType"));
	}

	private void _testPostSiteSitePageSuccessParentSitePage() throws Exception {
		SitePage parentPostSitePage = testPostSiteSitePage_addSitePage(
			randomSitePage());

		SitePage randomSitePage = randomSitePage();

		randomSitePage.setParentSitePage(
			new ParentSitePage() {
				{
					friendlyUrlPath = parentPostSitePage.getFriendlyUrlPath();
				}
			});

		SitePage postSitePage = testPostSiteSitePage_addSitePage(
			randomSitePage);

		ParentSitePage parentSitePage = postSitePage.getParentSitePage();

		Assert.assertEquals(
			parentPostSitePage.getFriendlyUrlPath(),
			parentSitePage.getFriendlyUrlPath());

		Layout layout = _layoutLocalService.fetchLayout(postSitePage.getId());

		Assert.assertNotNull(layout);

		Layout parentLayout = _layoutLocalService.fetchLayout(
			layout.getParentPlid());

		Assert.assertEquals(
			parentPostSitePage.getFriendlyUrlPath(),
			parentLayout.getFriendlyURL());
	}

	private void _testPostSiteSitePageSuccessParentSitePageNonexisting()
		throws Exception {

		SitePage randomSitePage = randomSitePage();

		randomSitePage.setParentSitePage(
			new ParentSitePage() {
				{
					friendlyUrlPath =
						StringPool.FORWARD_SLASH +
							RandomTestUtil.randomString();
				}
			});

		SitePage postSitePage = testPostSiteSitePage_addSitePage(
			randomSitePage);

		Assert.assertNull(postSitePage.getParentSitePage());

		Layout layout = _layoutLocalService.fetchLayout(postSitePage.getId());

		Assert.assertNotNull(layout);

		Assert.assertEquals(
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			layout.getParentLayoutId());
	}

	private void _testPostSiteSitePageSuccessTaxonomyCategoryBriefNonexisting()
		throws Exception {

		TaxonomyCategoryBrief[] inputTaxonomyCategoryBriefs = {
			new TaxonomyCategoryBrief() {
				{
					taxonomyCategoryReference =
						new TaxonomyCategoryReference() {
							{
								externalReferenceCode =
									RandomTestUtil.randomString();
							}
						};
				}
			}
		};

		_testPostSiteSitePageSuccessTaxonomyCategoryBriefs(
			new TaxonomyCategoryBrief[0], inputTaxonomyCategoryBriefs);
	}

	private void _testPostSiteSitePageSuccessTaxonomyCategoryBriefNonsitePage()
		throws Exception {

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				irrelevantGroup.getCreatorUserId(),
				irrelevantGroup.getGroupId(), RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(
					irrelevantGroup.getGroupId()));

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			RandomTestUtil.randomString(), irrelevantGroup.getCreatorUserId(),
			irrelevantGroup.getGroupId(), 0,
			RandomTestUtil.randomLocaleStringMap(), null,
			assetVocabulary.getVocabularyId(), null,
			ServiceContextTestUtil.getServiceContext(
				irrelevantGroup.getGroupId()));

		TaxonomyCategoryBrief[] expectedTaxonomyCategoryBriefs = {
			new TaxonomyCategoryBrief() {
				{
					taxonomyCategoryId = assetCategory.getCategoryId();
					taxonomyCategoryName = assetCategory.getName();
					taxonomyCategoryName_i18n = HashMapBuilder.put(
						"en-US", assetCategory.getName()
					).build();
					taxonomyCategoryReference =
						new TaxonomyCategoryReference() {
							{
								externalReferenceCode =
									assetCategory.getExternalReferenceCode();
								siteKey = irrelevantGroup.getGroupKey();
							}
						};
				}
			}
		};

		TaxonomyCategoryBrief[] inputTaxonomyCategoryBriefs = {
			new TaxonomyCategoryBrief() {
				{
					taxonomyCategoryReference =
						new TaxonomyCategoryReference() {
							{
								externalReferenceCode =
									assetCategory.getExternalReferenceCode();
								siteKey = irrelevantGroup.getGroupKey();
							}
						};
				}
			}
		};

		_testPostSiteSitePageSuccessTaxonomyCategoryBriefs(
			expectedTaxonomyCategoryBriefs, inputTaxonomyCategoryBriefs);
	}

	private void _testPostSiteSitePageSuccessTaxonomyCategoryBriefs(
			TaxonomyCategoryBrief[] expectedTaxonomyCategoryBriefs,
			TaxonomyCategoryBrief[] inputTaxonomyCategoryBriefs)
		throws Exception {

		SitePage randomSitePage = randomSitePage();

		randomSitePage.setTaxonomyCategoryBriefs(inputTaxonomyCategoryBriefs);

		SitePage postSitePage = testPostSiteSitePage_addSitePage(
			randomSitePage);

		Layout layout = _layoutLocalService.fetchLayout(postSitePage.getId());

		Assert.assertNotNull(layout);

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			Layout.class.getName(), layout.getPlid());

		long[] assetCategoryIds =
			_assetEntryAssetCategoryRelLocalService.getAssetCategoryPrimaryKeys(
				assetEntry.getEntryId());

		Assert.assertEquals(
			Arrays.toString(assetCategoryIds),
			expectedTaxonomyCategoryBriefs.length, assetCategoryIds.length);

		for (long assetCategoryId : assetCategoryIds) {
			AssetCategory assetCategory =
				_assetCategoryLocalService.fetchAssetCategory(assetCategoryId);

			TaxonomyCategoryBrief[] filteredTaxonomyCategoryBriefs =
				ArrayUtil.filter(
					expectedTaxonomyCategoryBriefs,
					taxonomyCategoryBrief -> {
						TaxonomyCategoryReference taxonomyCategoryReference =
							taxonomyCategoryBrief.
								getTaxonomyCategoryReference();

						Group group = _groupLocalService.fetchGroup(
							assetCategory.getGroupId());

						if (Objects.equals(
								taxonomyCategoryReference.
									getExternalReferenceCode(),
								assetCategory.getExternalReferenceCode()) &&
							(((taxonomyCategoryReference.getSiteKey() ==
								null) &&
							  (layout.getGroupId() ==
								  assetCategory.getGroupId())) ||
							 ((taxonomyCategoryReference.getSiteKey() !=
								 null) &&
							  Objects.equals(
								  taxonomyCategoryReference.getSiteKey(),
								  group.getGroupKey())))) {

							return true;
						}

						return false;
					});

			Assert.assertEquals(
				Arrays.toString(filteredTaxonomyCategoryBriefs), 1,
				filteredTaxonomyCategoryBriefs.length);
		}

		_assertEqualsIgnoringOrder(
			expectedTaxonomyCategoryBriefs,
			postSitePage.getTaxonomyCategoryBriefs());
	}

	private void _testPostSiteSitePageSuccessTaxonomyCategoryBriefSitePageSiteSiteKeyNonnull()
		throws Exception {

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				testGroup.getCreatorUserId(), testGroup.getGroupId(),
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(
					testGroup.getGroupId()));

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			RandomTestUtil.randomString(), testGroup.getCreatorUserId(),
			testGroup.getGroupId(), 0, RandomTestUtil.randomLocaleStringMap(),
			null, assetVocabulary.getVocabularyId(), null,
			ServiceContextTestUtil.getServiceContext(testGroup.getGroupId()));

		TaxonomyCategoryBrief[] expectedTaxonomyCategoryBriefs = {
			new TaxonomyCategoryBrief() {
				{
					taxonomyCategoryId = assetCategory.getCategoryId();
					taxonomyCategoryName = assetCategory.getName();
					taxonomyCategoryName_i18n = HashMapBuilder.put(
						"en-US", assetCategory.getName()
					).build();
					taxonomyCategoryReference =
						new TaxonomyCategoryReference() {
							{
								externalReferenceCode =
									assetCategory.getExternalReferenceCode();
							}
						};
				}
			}
		};

		TaxonomyCategoryBrief[] inputTaxonomyCategoryBriefs = {
			new TaxonomyCategoryBrief() {
				{
					taxonomyCategoryReference =
						new TaxonomyCategoryReference() {
							{
								externalReferenceCode =
									assetCategory.getExternalReferenceCode();
								siteKey = testGroup.getGroupKey();
							}
						};
				}
			}
		};

		_testPostSiteSitePageSuccessTaxonomyCategoryBriefs(
			expectedTaxonomyCategoryBriefs, inputTaxonomyCategoryBriefs);
	}

	private void _testPostSiteSitePageSuccessTaxonomyCategoryBriefSitePageSiteSiteKeyNull()
		throws Exception {

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				testGroup.getCreatorUserId(), testGroup.getGroupId(),
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(
					testGroup.getGroupId()));

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			RandomTestUtil.randomString(), testGroup.getCreatorUserId(),
			testGroup.getGroupId(), 0, RandomTestUtil.randomLocaleStringMap(),
			null, assetVocabulary.getVocabularyId(), null,
			ServiceContextTestUtil.getServiceContext(testGroup.getGroupId()));

		TaxonomyCategoryBrief[] expectedTaxonomyCategoryBriefs = {
			new TaxonomyCategoryBrief() {
				{
					taxonomyCategoryId = assetCategory.getCategoryId();
					taxonomyCategoryName = assetCategory.getName();
					taxonomyCategoryName_i18n = HashMapBuilder.put(
						"en-US", assetCategory.getName()
					).build();
					taxonomyCategoryReference =
						new TaxonomyCategoryReference() {
							{
								externalReferenceCode =
									assetCategory.getExternalReferenceCode();
							}
						};
				}
			}
		};

		TaxonomyCategoryBrief[] inputTaxonomyCategoryBriefs = {
			new TaxonomyCategoryBrief() {
				{
					taxonomyCategoryReference =
						new TaxonomyCategoryReference() {
							{
								externalReferenceCode =
									assetCategory.getExternalReferenceCode();
							}
						};
				}
			}
		};

		_testPostSiteSitePageSuccessTaxonomyCategoryBriefs(
			expectedTaxonomyCategoryBriefs, inputTaxonomyCategoryBriefs);
	}

	private static final String _CLASS_NAME_EXCEPTION_MAPPER =
		"com.liferay.headless.delivery.internal.resource.v1_0." +
			"SitePageResourceImpl";

	private static final PagePermission _PAGE_PERMISSIONS =
		new PagePermission() {
			{
				actionKeys = new String[] {
					ActionKeys.ADD_DISCUSSION, ActionKeys.ADD_LAYOUT,
					ActionKeys.CONFIGURE_PORTLETS, ActionKeys.CUSTOMIZE,
					ActionKeys.DELETE, ActionKeys.DELETE_DISCUSSION,
					ActionKeys.LAYOUT_RULE_BUILDER, ActionKeys.PREVIEW_DRAFT,
					ActionKeys.UPDATE, ActionKeys.UPDATE_DISCUSSION,
					ActionKeys.UPDATE_LAYOUT_ADVANCED_OPTIONS,
					ActionKeys.UPDATE_LAYOUT_BASIC,
					ActionKeys.UPDATE_LAYOUT_CONTENT,
					ActionKeys.UPDATE_LAYOUT_LIMITED, ActionKeys.PERMISSIONS,
					ActionKeys.VIEW
				};
				roleKey = RoleConstants.OWNER;
			}
		};

	@Inject
	private static ExpandoColumnLocalService _expandoColumnLocalService;

	@Inject
	private static ExpandoTableLocalService _expandoTableLocalService;

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetEntryAssetCategoryRelLocalService
		_assetEntryAssetCategoryRelLocalService;

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private AssetTagLocalService _assetTagLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private ClientExtensionEntryLocalService _clientExtensionEntryLocalService;

	@Inject
	private ClientExtensionEntryRelLocalService
		_clientExtensionEntryRelLocalService;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private LayoutPageTemplateStructureRelLocalService
		_layoutPageTemplateStructureRelLocalService;

	private final ObjectMapper _objectMapper = new ObjectMapper() {
		{
			configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
		}
	};
	private String _originalName;

	@Inject
	private Portal _portal;

	@Inject
	private ResourceActionLocalService _resourceActionLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

}