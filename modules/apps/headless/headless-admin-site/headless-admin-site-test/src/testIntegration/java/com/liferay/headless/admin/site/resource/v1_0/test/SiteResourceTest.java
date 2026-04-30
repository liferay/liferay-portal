/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.exportimport.test.rule.LazyReferencing;
import com.liferay.exportimport.test.rule.LazyReferencingTestRule;
import com.liferay.headless.admin.site.client.dto.v1_0.AnalyticsConfiguration;
import com.liferay.headless.admin.site.client.dto.v1_0.GoogleAnalyticsConfiguration;
import com.liferay.headless.admin.site.client.dto.v1_0.RatingsTypes;
import com.liferay.headless.admin.site.client.dto.v1_0.Site;
import com.liferay.headless.admin.site.client.pagination.Page;
import com.liferay.headless.admin.site.client.pagination.Pagination;
import com.liferay.headless.admin.site.client.problem.Problem;
import com.liferay.headless.admin.site.client.resource.v1_0.SiteResource;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LanguageIds;
import com.liferay.site.initializer.SiteInitializer;

import java.io.File;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Rubén Pulido
 */
@LanguageIds(
	availableLanguageIds = {"en_US", "es_ES", "pt_BR"},
	defaultLanguageId = "en_US"
)
@RunWith(Arquillian.class)
public class SiteResourceTest extends BaseSiteResourceTestCase {

	@ClassRule
	@Rule
	public static final LazyReferencingTestRule lazyReferencingTestRule =
		LazyReferencingTestRule.INSTANCE;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_originalName = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(TestPropsValues.getUserId());
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		Collections.reverse(_sites);

		for (Site site : _sites) {
			Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
				site.getExternalReferenceCode(),
				TestPropsValues.getCompanyId());

			if (group != null) {
				List<Group> childGroups = group.getChildren(true);

				for (Group childGroup : childGroups) {
					_groupLocalService.deleteGroup(childGroup);
				}

				_groupLocalService.deleteGroup(group);
			}
		}

		PrincipalThreadLocal.setName(_originalName);
	}

	@Override
	@Test
	public void testDeleteSite() throws Exception {
		super.testDeleteSite();

		// Nonexistent external reference code

		String externalReferenceCode = RandomTestUtil.randomString(10);

		try {
			siteResource.deleteSite(externalReferenceCode);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}
	}

	@Override
	@Test
	public void testGetSite() throws Exception {
		super.testGetSite();

		_testGetSiteWithDollar();
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteSiteInitializer() throws Exception {
	}

	@Override
	@Test
	public void testGetSitesPage() throws Exception {
		super.testGetSitesPage();

		_testGetSitesPageWithActiveAndInactiveSites();
		_testGetSitesPageWithActiveOrSiteGroups(false, true);
		_testGetSitesPageWithActiveOrSiteGroups(true, false);
		_testGetSitesPageWithDepotEntry();
		_testGetSitesPageWithInactiveSites();
		_testGetSitesPageWithSearch();
		_testGetSitesPageWithoutAuthentication();
	}

	@LazyReferencing
	@Override
	@Test
	public void testPostSite() throws Exception {
		super.testPostSite();

		_testPostSiteBatch();
		_testPostSiteDuplicateFriendlyURL();
		_testPostSiteFailureDuplicateName();
		_testPostSiteFailureInvalidKey();
		_testPostSiteFailureNoName();
		_testPostSiteFailureSiteInitializerInactive();
		_testPostSiteFailureSiteInitializerNotFound();
		_testPostSiteFailureSiteTemplateInactive();
		_testPostSiteFailureSiteTemplateNotFound();
		_testPostSiteFailureTemplateKeyNoTemplateType();
		_testPostSiteFailureTemplateTypeNoTemplateKey();
		_testPostSiteSuccessChild();
		_testPostSiteSuccessMembershipTypeOpen();
		_testPostSiteSuccessMembershipTypePrivate();
		_testPostSiteSuccessMembershipTypeRestricted();
		_testPostSiteSuccessSiteInitializer();
		_testPostSiteSuccessSiteTemplate();
		_testPostSiteWithFriendlyURLMissingSlash();
		_testPostSiteWithInheritLocales();
		_testPostSiteWithLocalizedDescription();
		_testPostSiteWithLocalizedName();
		_testPostSiteWithNondefaultLocales();
		_testPostSiteWithParentSiteExternalReferenceCode();
		_testPostSiteWithTypeSettingsFields();
		_testPostSiteWithoutAuthentication();
	}

	@LazyReferencing
	@Override
	@Test
	public void testPutSite() throws Exception {
		super.testPutSite();

		_testPutSiteBatch();
		_testPutSiteBatchWithParentSiteExternalReferenceCode();
		_testPutSiteWithExcludedTypeSettings();
		_testPutSiteWithParentSiteExternalReferenceCode();
		_testPutSiteWithoutUpdatePermission();
	}

	@LazyReferencing
	@Override
	@Test
	public void testPutSiteActivate() throws Exception {
		super.testPutSiteActivate();

		_testPutSiteActivateParentSite();
		_testPutSiteActivateWithoutAuthentication();
	}

	@LazyReferencing
	@Override
	@Test
	public void testPutSiteDeactivate() throws Exception {
		super.testPutSiteDeactivate();

		_testPutSiteDeactivateParentSite();
		_testPutSiteDeactivateWithoutAuthentication();
		_testPutSiteDeactivateSystemSite();
	}

	@Override
	@Test
	public void testPutSiteSiteInitializer() throws Exception {
		super.testPutSiteSiteInitializer();

		_testPutSiteSiteInitializerPreservesFriendlyUrlPath();
	}

	@Override
	protected void assertValid(Site site, Map<String, File> multipartFiles)
		throws Exception {
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"active", "description", "friendlyUrlPath", "manualMembership",
			"name"
		};
	}

	@Override
	protected Map<String, File> getMultipartFiles() throws Exception {
		return HashMapBuilder.<String, File>put(
			"file",
			() -> FileUtil.createTempFile(TestDataConstants.TEST_BYTE_ARRAY)
		).build();
	}

	@Override
	protected Site randomSite() throws Exception {
		return new Site() {
			{
				active = RandomTestUtil.randomBoolean();
				description = RandomTestUtil.randomString();
				friendlyUrlPath =
					CharPool.FORWARD_SLASH +
						StringUtil.toLowerCase(RandomTestUtil.randomString());
				manualMembership = RandomTestUtil.randomBoolean();
				mapProviderKey = MapProviderKey.GOOGLE_MAPS;
				membershipRestriction =
					GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION;
				name = RandomTestUtil.randomString();
				parentSiteExternalReferenceCode = StringPool.BLANK;
			}
		};
	}

	@Override
	protected Site testDeleteSite_addSite() throws Exception {
		return testPutSiteSiteInitializer_addSite();
	}

	@Override
	protected Site testGetSite_addSite() throws Exception {
		return testPostSite_addSite(randomSite());
	}

	@Override
	protected Site testGetSitePermissionsPage_addSite() throws Exception {
		return testPostSite_addSite(randomSite());
	}

	@Override
	protected Site testGetSitesPage_addSite(Site site) throws Exception {
		Site postSite = siteResource.postSite(site);

		_sites.add(postSite);

		return postSite;
	}

	@Override
	protected Site testPostSite_addPermissionsSite(Site site) throws Exception {
		Site postSite = permissionsSiteResource.postSite(site);

		_sites.add(postSite);

		return postSite;
	}

	@Override
	protected Site testPostSite_addSite(Site site) throws Exception {
		Site postSite = siteResource.postSite(site);

		_sites.add(postSite);

		return postSite;
	}

	@Override
	protected Site testPostSiteSiteInitializer_addSite(
			Site site, Map<String, File> multipartFiles)
		throws Exception {

		Site postSite = siteResource.postSiteSiteInitializer(
			site, multipartFiles);

		_sites.add(postSite);

		return postSite;
	}

	@Override
	protected Site testPutSite_addSite() throws Exception {
		return testPostSite_addSite(randomSite());
	}

	@Override
	protected Site testPutSiteActivate_addSite() throws Exception {
		Site postSite = randomSite();

		postSite.setActive(false);

		return testPostSite_addSite(postSite);
	}

	@Override
	protected Site testPutSiteDeactivate_addSite() throws Exception {
		Site postSite = randomSite();

		postSite.setActive(true);

		return testPostSite_addSite(postSite);
	}

	@Override
	protected Site testPutSitePermissionsPage_addSite() throws Exception {
		return testPostSite_addSite(randomSite());
	}

	@Override
	protected Site testPutSiteSiteInitializer_addSite() throws Exception {
		return siteResource.putSiteSiteInitializer(
			RandomTestUtil.randomString(), randomSite(), getMultipartFiles());
	}

	@Override
	protected Site testPutSiteSiteInitializer_getSite(
		String siteExternalReferenceCode) {

		try {
			return siteResource.getSite(siteExternalReferenceCode);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private void _assertEquals(Group group, Site site) throws Exception {
		Assert.assertEquals(site.getActive(), group.isActive());
		Assert.assertEquals(
			site.getDescription(),
			group.getDescription(LocaleUtil.getDefault()));
		Assert.assertEquals(site.getFriendlyUrlPath(), group.getFriendlyURL());
		Assert.assertEquals(
			site.getManualMembership(), group.isManualMembership());
		Assert.assertEquals(
			site.getMembershipRestriction(),
			Integer.valueOf(group.getMembershipRestriction()));

		if (site.getMembershipType() == null) {
			Assert.assertEquals(
				Site.MembershipType.RESTRICTED,
				Site.MembershipType.create(
					GroupConstants.getTypeLabel(group.getType())));
		}
		else {
			Assert.assertEquals(
				site.getMembershipType(),
				Site.MembershipType.create(
					GroupConstants.getTypeLabel(group.getType())));
		}

		Assert.assertEquals(
			site.getName(), group.getName(LocaleUtil.getDefault()));
	}

	private void _testGetSitesPageWithActiveAndInactiveSites()
		throws Exception {

		Page<Site> page = siteResource.getSitesPage(
			null, null, Pagination.of(1, 100));

		long totalCount = page.getTotalCount();

		Site site = randomSite();

		site.setActive(false);

		testGetSitesPage_addSite(site);

		page = siteResource.getSitesPage(null, null, Pagination.of(1, 100));

		Assert.assertEquals(totalCount + 1, page.getTotalCount());
	}

	private void _testGetSitesPageWithActiveOrSiteGroups(
			boolean active, boolean site)
		throws Exception {

		Page<Site> sitesPage = siteResource.getSitesPage(
			true, null, Pagination.of(1, 100));

		List<Site> originalItems = (List<Site>)sitesPage.getItems();

		Site postSite = testGetSitesPage_addSite(randomSite());

		Group group = _groupLocalService.getGroup(postSite.getId());

		group.setSite(site);
		group.setActive(active);

		_groupLocalService.updateGroup(group);

		sitesPage = siteResource.getSitesPage(
			true, null, Pagination.of(1, 100));

		List<Site> existingItems = (List<Site>)sitesPage.getItems();

		Assert.assertEquals(originalItems, existingItems);
	}

	private void _testGetSitesPageWithDepotEntry() throws Exception {
		Page<Site> sitesPage = siteResource.getSitesPage(
			true, null, Pagination.of(1, 100));

		List<Site> originalItems = (List<Site>)sitesPage.getItems();

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		sitesPage = siteResource.getSitesPage(
			true, null, Pagination.of(1, 100));

		List<Site> existingItems = (List<Site>)sitesPage.getItems();

		Assert.assertEquals(originalItems, existingItems);
	}

	private void _testGetSitesPageWithInactiveSites() throws Exception {
		Page<Site> page = siteResource.getSitesPage(
			false, null, Pagination.of(1, 100));

		long totalCount = page.getTotalCount();

		Site site1 = randomSite();

		site1.setActive(false);

		testGetSitesPage_addSite(site1);

		page = siteResource.getSitesPage(false, null, Pagination.of(1, 100));

		Assert.assertEquals(totalCount + 1, page.getTotalCount());

		Collection<Site> sites = page.getItems();

		for (Site site2 : sites) {
			Assert.assertEquals(site2.getActive(), false);
		}
	}

	private void _testGetSitesPageWithoutAuthentication() throws Exception {
		SiteResource.Builder builder = SiteResource.builder();

		SiteResource siteResource = builder.build();

		try {
			siteResource.getSitesPage(true, null, Pagination.of(1, 1));

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("403", problem.getStatus());
		}
	}

	private void _testGetSitesPageWithSearch() throws Exception {
		String name = RandomTestUtil.randomString();

		Site randomSite = new Site();

		randomSite.setName(name);

		Site postSite = _testPostSite_addSite(randomSite);

		Page<Site> sitesPage = siteResource.getSitesPage(
			true, name, Pagination.of(1, 10));

		List<Site> items = (List<Site>)sitesPage.getItems();

		Assert.assertEquals(items.toString(), 1, items.size());

		assertEquals(postSite, items.get(0));
	}

	private void _testGetSiteWithDollar() throws Exception {
		Site postSite = siteResource.putSiteSiteInitializer(
			RandomTestUtil.randomString() + StringPool.DOLLAR, randomSite(),
			getMultipartFiles());

		Site getSite = siteResource.getSite(
			postSite.getExternalReferenceCode());

		assertEquals(postSite, getSite);
		assertValid(getSite);
	}

	private Site _testPostSite_addSite(Site site) throws Exception {
		Site postSite = siteResource.postSite(site);

		_sites.add(postSite);

		return postSite;
	}

	private void _testPostSiteBatch() throws Exception {
		Site site = randomSite();

		site.setExternalReferenceCode(RandomTestUtil.randomString());

		waitForFinish(
			"COMPLETED",
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					_jsonFactory.createJSONObject(site.toString())
				).toString(),
				"headless-admin-site/v1.0/sites/batch", Http.Method.POST));

		Group group = _groupLocalService.getGroupByExternalReferenceCode(
			site.getExternalReferenceCode(), TestPropsValues.getCompanyId());

		_assertEquals(group, site);

		_sites.add(siteResource.getSite(site.getExternalReferenceCode()));
	}

	private void _testPostSiteDuplicateFriendlyURL() throws Exception {
		Site site1 = _testPostSite_addSite(randomSite());

		Site site2 = randomSite();

		site2.setFriendlyUrlPath(site1.getFriendlyUrlPath());

		site2 = _testPostSite_addSite(site2);

		Assert.assertEquals(
			StringBundler.concat(site1.getFriendlyUrlPath(), CharPool.DASH, 1),
			site2.getFriendlyUrlPath());
	}

	private void _testPostSiteFailureDuplicateName() throws Exception {
		Site randomSite = new Site() {
			{
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};

		_testPostSite_addSite(randomSite);

		try {
			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					_CLASS_NAME_EXCEPTION_MAPPER, LoggerTestUtil.ERROR)) {

				_testPostSite_addSite(randomSite);
			}

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("CONFLICT", problem.getStatus());
			Assert.assertEquals(
				"A site with the same key already exists", problem.getTitle());
		}
	}

	private void _testPostSiteFailureInvalidKey() throws Exception {
		Site randomSite = randomSite();

		randomSite.setName("*");

		try {
			_testPostSite_addSite(randomSite);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals("Site key is invalid", problem.getTitle());
		}
	}

	private void _testPostSiteFailureNoName() throws Exception {
		Site randomSite = randomSite();

		randomSite.setName((String)null);

		try {
			_testPostSite_addSite(randomSite);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
		}
	}

	private void _testPostSiteFailureSiteInitializerInactive()
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(SiteResourceTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		String siteInitializerKey = RandomTestUtil.randomString();

		ServiceRegistration<SiteInitializer> serviceRegistration =
			bundleContext.registerService(
				SiteInitializer.class,
				new TestSiteInitializer(siteInitializerKey),
				HashMapDictionaryBuilder.put(
					"site.initializer.key", siteInitializerKey
				).build());

		Site randomSite = randomSite();

		randomSite.setTemplateKey(siteInitializerKey);
		randomSite.setTemplateType(Site.TemplateType.SITE_INITIALIZER);

		try {
			_testPostSite_addSite(randomSite);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(
				"Site initializer with site template key " +
					randomSite.getTemplateKey() + " is inactive",
				problem.getTitle());
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	private void _testPostSiteFailureSiteInitializerNotFound()
		throws Exception {

		Site randomSite = randomSite();

		randomSite.setTemplateKey(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));
		randomSite.setTemplateType(Site.TemplateType.SITE_INITIALIZER);

		try {
			_testPostSite_addSite(randomSite);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(
				"No site initializer was found for site template key " +
					randomSite.getTemplateKey(),
				problem.getTitle());
		}
	}

	private void _testPostSiteFailureSiteTemplateInactive() throws Exception {
		Site randomSite = randomSite();

		LayoutSetPrototype layoutSetPrototype =
			_layoutSetPrototypeLocalService.addLayoutSetPrototype(
				TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
				HashMapBuilder.put(
					LocaleUtil.getDefault(),
					StringUtil.toLowerCase(RandomTestUtil.randomString())
				).build(),
				null, false, true, new ServiceContext());

		randomSite.setTemplateKey(
			String.valueOf(layoutSetPrototype.getLayoutSetPrototypeId()));

		randomSite.setTemplateType(Site.TemplateType.SITE_TEMPLATE);

		try {
			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					_CLASS_NAME_EXCEPTION_MAPPER, LoggerTestUtil.ERROR)) {

				_testPostSite_addSite(randomSite);
			}

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(
				"Site template with site template key " +
					randomSite.getTemplateKey() + " is inactive",
				problem.getTitle());
		}
	}

	private void _testPostSiteFailureSiteTemplateNotFound() throws Exception {
		Site randomSite = randomSite();

		randomSite.setTemplateKey(String.valueOf(RandomTestUtil.randomLong()));
		randomSite.setTemplateType(Site.TemplateType.SITE_TEMPLATE);

		try {
			_testPostSite_addSite(randomSite);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(
				"No site template was found for site template key " +
					randomSite.getTemplateKey(),
				problem.getTitle());
		}
	}

	private void _testPostSiteFailureTemplateKeyNoTemplateType()
		throws Exception {

		Site randomSite = randomSite();

		randomSite.setTemplateKey(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));

		try {
			_testPostSite_addSite(randomSite);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(
				"Template type cannot be empty if template key is specified",
				problem.getTitle());
		}
	}

	private void _testPostSiteFailureTemplateTypeNoTemplateKey()
		throws Exception {

		Site randomSite = randomSite();

		randomSite.setTemplateType(Site.TemplateType.SITE_INITIALIZER);

		try {
			_testPostSite_addSite(randomSite);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(
				"Template key cannot be empty if template type is specified",
				problem.getTitle());
		}
	}

	private Site _testPostSiteSuccess(Site site) throws Exception {
		Site postSite = _testPostSite_addSite(site);

		assertEquals(site, postSite);
		assertValid(postSite);

		return postSite;
	}

	private void _testPostSiteSuccessChild() throws Exception {
		Site parentSite = _testPostSite_addSite(randomSite());

		Site randomSite = randomSite();

		randomSite.setParentSiteExternalReferenceCode(
			parentSite.getExternalReferenceCode());

		Site postSite = _testPostSiteSuccess(randomSite);

		Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
			postSite.getExternalReferenceCode(),
			TestPropsValues.getCompanyId());

		Group parentGroup = group.getParentGroup();

		Assert.assertEquals(
			parentSite.getExternalReferenceCode(),
			parentGroup.getExternalReferenceCode());
	}

	private void _testPostSiteSuccessMembershipTypeOpen() throws Exception {
		Site randomSite = randomSite();

		randomSite.setMembershipType(Site.MembershipType.OPEN);

		Site postSite = _testPostSiteSuccess(randomSite);

		Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
			postSite.getExternalReferenceCode(),
			TestPropsValues.getCompanyId());

		Assert.assertEquals(GroupConstants.TYPE_SITE_OPEN, group.getType());
	}

	private void _testPostSiteSuccessMembershipTypePrivate() throws Exception {
		Site randomSite = randomSite();

		randomSite.setMembershipType(Site.MembershipType.PRIVATE);

		Site postSite = _testPostSiteSuccess(randomSite);

		Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
			postSite.getExternalReferenceCode(),
			TestPropsValues.getCompanyId());

		Assert.assertEquals(GroupConstants.TYPE_SITE_PRIVATE, group.getType());
	}

	private void _testPostSiteSuccessMembershipTypeRestricted()
		throws Exception {

		Site randomSite = randomSite();

		randomSite.setMembershipType(Site.MembershipType.RESTRICTED);

		Site postSite = _testPostSiteSuccess(randomSite);

		Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
			postSite.getExternalReferenceCode(),
			TestPropsValues.getCompanyId());

		Assert.assertEquals(
			GroupConstants.TYPE_SITE_RESTRICTED, group.getType());
	}

	private void _testPostSiteSuccessSiteInitializer() throws Exception {
		Site randomSite = randomSite();

		randomSite.setTemplateKey("blank-site-initializer");
		randomSite.setTemplateType(Site.TemplateType.SITE_INITIALIZER);

		_testPostSiteSuccess(randomSite);
	}

	private void _testPostSiteSuccessSiteTemplate() throws Exception {
		Site randomSite = randomSite();

		LayoutSetPrototype layoutSetPrototype =
			_layoutSetPrototypeLocalService.addLayoutSetPrototype(
				TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
				HashMapBuilder.put(
					LocaleUtil.getDefault(),
					StringUtil.toLowerCase(RandomTestUtil.randomString())
				).build(),
				null, true, true, new ServiceContext());

		randomSite.setTemplateKey(
			String.valueOf(layoutSetPrototype.getLayoutSetPrototypeId()));

		randomSite.setTemplateType(Site.TemplateType.SITE_TEMPLATE);

		Site postSite = _testPostSiteSuccess(randomSite);

		Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
			postSite.getExternalReferenceCode(),
			TestPropsValues.getCompanyId());

		LayoutSet publicLayoutSet = group.getPublicLayoutSet();

		Assert.assertEquals(
			layoutSetPrototype.getLayoutSetPrototypeId(),
			publicLayoutSet.getLayoutSetPrototypeId());
	}

	private void _testPostSiteWithFriendlyURLMissingSlash() throws Exception {
		Site site = randomSite();

		String friendlyUrlPath = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		site.setFriendlyUrlPath(friendlyUrlPath);

		site = _testPostSite_addSite(site);

		Assert.assertEquals(
			StringPool.SLASH + friendlyUrlPath, site.getFriendlyUrlPath());
	}

	private void _testPostSiteWithInheritLocales() throws Exception {
		Site randomSite = randomSite();

		Site postSite = _testPostSite_addSite(randomSite);

		Assert.assertTrue(postSite.getInheritLocales());

		randomSite = randomSite();

		randomSite.setLocales(
			new String[] {LocaleUtil.toW3cLanguageId(LocaleUtil.US)});

		postSite = _testPostSite_addSite(randomSite);

		Assert.assertFalse(postSite.getInheritLocales());

		randomSite = randomSite();

		randomSite.setInheritLocales(true);

		postSite = _testPostSite_addSite(randomSite);

		Assert.assertTrue(postSite.getInheritLocales());
	}

	private void _testPostSiteWithLocalizedDescription() throws Exception {
		Site randomSite = randomSite();

		String description1 = RandomTestUtil.randomString();
		String description2 = RandomTestUtil.randomString();

		randomSite.setDescription_i18n(
			LinkedHashMapBuilder.put(
				String.valueOf(LocaleUtil.getDefault()), description1
			).put(
				String.valueOf(LocaleUtil.BRAZIL), description2
			).build());

		Site postSite = _testPostSite_addSite(randomSite);

		Map<String, String> descriptionMap = postSite.getDescription_i18n();

		Assert.assertEquals(
			description1,
			descriptionMap.get(
				LocaleUtil.toBCP47LanguageId(LocaleUtil.getDefault())));
		Assert.assertEquals(
			description2,
			descriptionMap.get(
				LocaleUtil.toBCP47LanguageId(LocaleUtil.BRAZIL)));
	}

	private void _testPostSiteWithLocalizedName() throws Exception {
		Site randomSite = randomSite();

		String name1 = RandomTestUtil.randomString();
		String name2 = RandomTestUtil.randomString();

		randomSite.setName_i18n(
			LinkedHashMapBuilder.put(
				"en-US", name1
			).put(
				"pt-BR", name2
			).build());

		Site postSite = _testPostSite_addSite(randomSite);

		Map<String, String> nameMap = postSite.getName_i18n();

		Assert.assertEquals(name1, nameMap.get("en-US"));
		Assert.assertEquals(name2, nameMap.get("pt-BR"));
	}

	private void _testPostSiteWithNondefaultLocales() throws Exception {
		Site site = randomSite();

		site.setDefaultLanguageId(String.valueOf(LocaleUtil.SPAIN));
		site.setInheritLocales(false);

		String[] locales = {
			LocaleUtil.toW3cLanguageId(LocaleUtil.BRAZIL),
			LocaleUtil.toW3cLanguageId(LocaleUtil.SPAIN)
		};

		site.setLocales(locales);

		Site postSite = _testPostSite_addSite(site);

		Assert.assertEquals(
			postSite.getDefaultLanguageId(), String.valueOf(LocaleUtil.SPAIN));
		Assert.assertArrayEquals(postSite.getLocales(), locales);
	}

	private void _testPostSiteWithoutAuthentication() throws Exception {
		SiteResource.Builder builder = SiteResource.builder();

		SiteResource siteResource = builder.build();

		try {
			siteResource.postSite(randomSite());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("403", problem.getStatus());
		}
	}

	private void _testPostSiteWithParentSiteExternalReferenceCode()
		throws Exception {

		Site postParentSite = testPostSite_addSite(randomSite());

		Site randomSite = randomSite();

		randomSite.setParentSiteExternalReferenceCode(
			postParentSite.getExternalReferenceCode());

		Site postSite = siteResource.postSite(randomSite);

		Assert.assertEquals(
			randomSite.getParentSiteExternalReferenceCode(),
			postSite.getParentSiteExternalReferenceCode());
		assertEquals(randomSite, postSite);
		assertValid(postSite);

		randomSite = randomSite();

		randomSite.setParentSiteExternalReferenceCode(StringPool.BLANK);

		postSite = siteResource.postSite(randomSite);

		Assert.assertNotNull(postSite.getParentSiteExternalReferenceCode());
		assertEquals(randomSite, postSite);
		assertValid(postSite);

		randomSite = randomSite();

		randomSite.setParentSiteExternalReferenceCode(
			RandomTestUtil.randomString());

		postSite = siteResource.postSite(randomSite);

		Assert.assertEquals(
			StringPool.BLANK, postSite.getParentSiteExternalReferenceCode());
		assertEquals(randomSite, postSite);
		assertValid(postSite);

		randomSite = randomSite();

		randomSite.setParentSiteExternalReferenceCode(StringPool.BLANK);

		postSite = siteResource.postSite(randomSite);

		Assert.assertEquals(
			StringPool.BLANK, postSite.getParentSiteExternalReferenceCode());
		assertEquals(randomSite, postSite);
		assertValid(postSite);
	}

	private void _testPostSiteWithTypeSettingsFields() throws Exception {
		Site randomSite = randomSite();

		GoogleAnalyticsConfiguration googleAnalyticsConfiguration1 =
			new GoogleAnalyticsConfiguration() {
				{
					setGoogleAnalytics4CustomConfig(
						RandomTestUtil::randomString);
					setGoogleAnalytics4Id(RandomTestUtil::randomString);
				}
			};

		AnalyticsConfiguration analyticsConfiguration =
			new AnalyticsConfiguration() {
				{
					setGoogleAnalyticsConfiguration(
						googleAnalyticsConfiguration1);
					setMatomoAnalyticsScript(RandomTestUtil::randomString);
				}
			};

		randomSite.setAnalyticsConfiguration(analyticsConfiguration);

		RatingsTypes ratingsTypes = new RatingsTypes() {
			{
				setComment(Comment.STARS);
				setWikiPage(WikiPage.LIKE);
			}
		};

		randomSite.setInheritLocales(true);
		randomSite.setRatingsTypes(ratingsTypes);

		Site postSite = _testPostSite_addSite(randomSite);

		Assert.assertTrue(postSite.getInheritLocales());
		Assert.assertNotNull(postSite.getLocales());

		assertEquals(randomSite, postSite);
	}

	private void _testPutSiteActivateParentSite() throws Exception {
		Site parentSite = randomSite();

		parentSite.setActive(false);

		Site postParentSite = _testPostSite_addSite(parentSite);

		Site childSite = randomSite();

		childSite.setActive(false);

		childSite.setParentSiteExternalReferenceCode(
			postParentSite.getExternalReferenceCode());

		Site postChildSite = _testPostSite_addSite(childSite);

		siteResource.putSiteActivate(postParentSite.getExternalReferenceCode());

		Site getParentSite = siteResource.getSite(
			postParentSite.getExternalReferenceCode());

		Assert.assertTrue(getParentSite.getActive());

		Site getChildSite = siteResource.getSite(
			postChildSite.getExternalReferenceCode());

		Assert.assertFalse(getChildSite.getActive());
	}

	private void _testPutSiteActivateWithoutAuthentication() throws Exception {
		SiteResource.Builder builder = SiteResource.builder();

		SiteResource siteResource = builder.build();

		Site postSite = _testPostSite_addSite(randomSite());

		try {
			siteResource.putSiteActivate(postSite.getExternalReferenceCode());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("403", problem.getStatus());
		}
	}

	private void _testPutSiteBatch() throws Exception {
		Site site = randomSite();

		site.setExternalReferenceCode(RandomTestUtil.randomString());

		waitForFinish(
			"COMPLETED",
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					_jsonFactory.createJSONObject(site.toString())
				).toString(),
				"headless-admin-site/v1.0/sites/batch", Http.Method.PUT));

		Group group = _groupLocalService.getGroupByExternalReferenceCode(
			site.getExternalReferenceCode(), TestPropsValues.getCompanyId());

		_assertEquals(group, site);

		_sites.add(siteResource.getSite(site.getExternalReferenceCode()));

		Site updatedSite = randomSite();

		updatedSite.setExternalReferenceCode(site.getExternalReferenceCode());

		waitForFinish(
			"COMPLETED",
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					_jsonFactory.createJSONObject(updatedSite.toString())
				).toString(),
				"headless-admin-site/v1.0/sites/batch", Http.Method.PUT));

		group = _groupLocalService.getGroupByExternalReferenceCode(
			updatedSite.getExternalReferenceCode(),
			TestPropsValues.getCompanyId());

		_assertEquals(group, updatedSite);

		_sites.add(
			siteResource.getSite(updatedSite.getExternalReferenceCode()));
	}

	private void _testPutSiteBatchWithParentSiteExternalReferenceCode()
		throws Exception {

		Site postParentSite = testPutSite_addSite();

		Site postSite = testPutSite_addSite();

		Site site = randomSite();

		site.setExternalReferenceCode(postSite.getExternalReferenceCode());

		site.setParentSiteExternalReferenceCode(
			postParentSite.getExternalReferenceCode());

		waitForFinish(
			"COMPLETED",
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					_jsonFactory.createJSONObject(site.toString())
				).toString(),
				"headless-admin-site/v1.0/sites/batch", Http.Method.PUT));

		Group group = _groupLocalService.getGroupByExternalReferenceCode(
			site.getExternalReferenceCode(), TestPropsValues.getCompanyId());

		Assert.assertEquals(
			GroupConstants.DEFAULT_PARENT_GROUP_ID, group.getParentGroupId());

		site.setParentSiteExternalReferenceCode(RandomTestUtil.randomString());

		waitForFinish(
			"COMPLETED",
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					_jsonFactory.createJSONObject(site.toString())
				).toString(),
				"headless-admin-site/v1.0/sites/batch", Http.Method.PUT));

		group = _groupLocalService.getGroupByExternalReferenceCode(
			site.getExternalReferenceCode(), TestPropsValues.getCompanyId());

		Assert.assertEquals(
			GroupConstants.DEFAULT_PARENT_GROUP_ID, group.getParentGroupId());

		site.setParentSiteExternalReferenceCode(StringPool.BLANK);

		waitForFinish(
			"COMPLETED",
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					_jsonFactory.createJSONObject(site.toString())
				).toString(),
				"headless-admin-site/v1.0/sites/batch", Http.Method.PUT));

		group = _groupLocalService.getGroupByExternalReferenceCode(
			site.getExternalReferenceCode(), TestPropsValues.getCompanyId());

		Assert.assertEquals(
			GroupConstants.DEFAULT_PARENT_GROUP_ID, group.getParentGroupId());

		site = randomSite();

		site.setParentSiteExternalReferenceCode(
			postParentSite.getExternalReferenceCode());

		postSite = siteResource.postSite(site);

		site = randomSite();

		site.setExternalReferenceCode(postSite.getExternalReferenceCode());
		site.setParentSiteExternalReferenceCode(
			postSite.getParentSiteExternalReferenceCode());

		waitForFinish(
			"COMPLETED",
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					_jsonFactory.createJSONObject(site.toString())
				).toString(),
				"headless-admin-site/v1.0/sites/batch", Http.Method.PUT));

		group = _groupLocalService.getGroupByExternalReferenceCode(
			site.getExternalReferenceCode(), TestPropsValues.getCompanyId());

		Group parentGroup = _groupLocalService.getGroupByExternalReferenceCode(
			site.getParentSiteExternalReferenceCode(),
			TestPropsValues.getCompanyId());

		Assert.assertEquals(parentGroup.getGroupId(), group.getParentGroupId());
	}

	private void _testPutSiteDeactivateParentSite() throws Exception {
		Site parentSite = randomSite();

		parentSite.setActive(true);

		Site postParentSite = _testPostSite_addSite(parentSite);

		Site childSite = randomSite();

		childSite.setActive(true);

		childSite.setParentSiteExternalReferenceCode(
			postParentSite.getExternalReferenceCode());

		Site postChildSite = _testPostSite_addSite(childSite);

		siteResource.putSiteDeactivate(
			postParentSite.getExternalReferenceCode());

		Site getParentSite = siteResource.getSite(
			postParentSite.getExternalReferenceCode());

		Assert.assertFalse(getParentSite.getActive());

		Site getChildSite = siteResource.getSite(
			postChildSite.getExternalReferenceCode());

		Assert.assertTrue(getChildSite.getActive());
	}

	private void _testPutSiteDeactivateSystemSite() throws Exception {
		Group companyGroup = _groupLocalService.getCompanyGroup(
			TestPropsValues.getCompanyId());

		try {
			siteResource.putSiteDeactivate(
				companyGroup.getExternalReferenceCode());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("METHOD_NOT_ALLOWED", problem.getStatus());
			Assert.assertEquals(
				String.format(
					"Site %s cannot be deactivated because it is a system " +
						"required site",
					companyGroup.getExternalReferenceCode()),
				problem.getTitle());
		}
	}

	private void _testPutSiteDeactivateWithoutAuthentication()
		throws Exception {

		SiteResource.Builder builder = SiteResource.builder();

		SiteResource siteResource = builder.build();

		Site postSite = _testPostSite_addSite(randomSite());

		try {
			siteResource.putSiteDeactivate(postSite.getExternalReferenceCode());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("403", problem.getStatus());
		}
	}

	private void _testPutSiteSiteInitializerPreservesFriendlyUrlPath()
		throws Exception {

		Site postSite = testPutSiteSiteInitializer_addSite();

		String originalFriendlyUrlPath = postSite.getFriendlyUrlPath();

		Site randomSite = randomSite();

		randomSite.setFriendlyUrlPath((String)null);

		Site putSite = siteResource.putSiteSiteInitializer(
			postSite.getExternalReferenceCode(), randomSite,
			getMultipartFiles());

		Assert.assertEquals(
			originalFriendlyUrlPath, putSite.getFriendlyUrlPath());

		Site getSite = siteResource.getSite(
			postSite.getExternalReferenceCode());

		Assert.assertEquals(
			originalFriendlyUrlPath, getSite.getFriendlyUrlPath());
	}

	private void _testPutSiteWithExcludedTypeSettings() throws Exception {
		Site postSite = testPutSite_addSite();

		Group group = _groupLocalService.getGroupByExternalReferenceCode(
			postSite.getExternalReferenceCode(),
			TestPropsValues.getCompanyId());

		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.put(
			"defaultSiteRoleIds", RandomTestUtil.randomString()
		).put(
			"defaultTeamIds", RandomTestUtil.randomString()
		).put(
			"googleMapsAPIKey", RandomTestUtil.randomString()
		).put(
			"MAP_PROVIDER_KEY", postSite.getMapProviderKeyAsString()
		).build();

		_groupLocalService.updateGroup(
			group.getGroupId(), unicodeProperties.toString());

		Site randomSite = randomSite();

		randomSite.setExternalReferenceCode(
			postSite.getExternalReferenceCode());

		siteResource.putSite(randomSite.getExternalReferenceCode(), randomSite);

		group = _groupLocalService.fetchGroupByExternalReferenceCode(
			postSite.getExternalReferenceCode(),
			TestPropsValues.getCompanyId());

		UnicodeProperties putSiteUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				group.getTypeSettings()
			).build();

		Assert.assertEquals(
			unicodeProperties.get("defaultSiteRoleIds"),
			putSiteUnicodeProperties.get("defaultSiteRoleIds"));
		Assert.assertEquals(
			unicodeProperties.get("defaultTeamIds"),
			putSiteUnicodeProperties.get("defaultTeamIds"));
		Assert.assertEquals(
			unicodeProperties.get("googleMapsAPIKey"),
			putSiteUnicodeProperties.get("googleMapsAPIKey"));
		Assert.assertEquals(
			unicodeProperties.get("MAP_PROVIDER_KEY"),
			putSiteUnicodeProperties.get("MAP_PROVIDER_KEY"));
	}

	private void _testPutSiteWithoutUpdatePermission() throws Exception {
		User user = UserTestUtil.addUser(false);

		user = _userLocalService.updatePassword(
			user.getUserId(), "test", "test", false, true);

		SiteResource siteResource = SiteResource.builder(
		).authentication(
			user.getEmailAddress(), "test"
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).locale(
			LocaleUtil.getDefault()
		).build();

		Site postSite = _testPostSite_addSite(randomSite());

		try {
			siteResource.putSite(
				postSite.getExternalReferenceCode(), randomSite());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("FORBIDDEN", problem.getStatus());
		}
	}

	private void _testPutSiteWithParentSiteExternalReferenceCode()
		throws Exception {

		Site postParentSite = testPutSite_addSite();

		Site randomSite = randomSite();

		randomSite.setExternalReferenceCode(RandomTestUtil.randomString());
		randomSite.setParentSiteExternalReferenceCode(
			postParentSite.getExternalReferenceCode());

		Site putSite = siteResource.putSite(
			randomSite.getExternalReferenceCode(), randomSite);

		Assert.assertEquals(
			postParentSite.getExternalReferenceCode(),
			putSite.getParentSiteExternalReferenceCode());

		randomSite.setParentSiteExternalReferenceCode(StringPool.BLANK);

		putSite = siteResource.putSite(
			randomSite.getExternalReferenceCode(), randomSite);

		Assert.assertEquals(
			StringPool.BLANK, putSite.getParentSiteExternalReferenceCode());
	}

	private static final String _CLASS_NAME_EXCEPTION_MAPPER =
		"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
			"ExceptionMapper";

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

	private String _originalName;
	private final List<Site> _sites = new ArrayList<>();

	@Inject
	private UserLocalService _userLocalService;

	private class TestSiteInitializer implements SiteInitializer {

		public TestSiteInitializer(String key) {
			_key = key;
		}

		@Override
		public String getDescription(Locale locale) {
			return RandomTestUtil.randomString();
		}

		@Override
		public String getKey() {
			return _key;
		}

		@Override
		public String getName(Locale locale) {
			return RandomTestUtil.randomString();
		}

		@Override
		public String getThumbnailSrc() {
			return RandomTestUtil.randomString();
		}

		@Override
		public void initialize(long groupId) {
		}

		@Override
		public boolean isActive(long companyId) {
			return false;
		}

		private final String _key;

	}

}