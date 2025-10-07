/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.site.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.exportimport.test.rule.LazyReferencing;
import com.liferay.exportimport.test.rule.LazyReferencingTestRule;
import com.liferay.google.places.constants.GooglePlacesWebKeys;
import com.liferay.headless.site.client.dto.v1_0.Site;
import com.liferay.headless.site.client.pagination.Page;
import com.liferay.headless.site.client.pagination.Pagination;
import com.liferay.headless.site.client.problem.Problem;
import com.liferay.headless.site.client.resource.v1_0.SiteResource;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
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

		// Nonexistent site ID

		long siteId = RandomTestUtil.randomLong();

		try {
			siteResource.deleteSite(siteId);

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
	public void testDeleteSiteByExternalReferenceCode() throws Exception {
		super.testDeleteSiteByExternalReferenceCode();

		// Nonexistent external reference code

		String externalReferenceCode = RandomTestUtil.randomString(10);

		try {
			siteResource.deleteSiteByExternalReferenceCode(
				externalReferenceCode);

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
	public void testGetSiteByExternalReferenceCode() throws Exception {
		super.testGetSiteByExternalReferenceCode();

		_testGetSiteByExternalReferenceCodeWithDollar();
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteByExternalReferenceCodeSiteInitializer()
		throws Exception {
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
		_testPostSiteSuccessMembershipTypePrivate();
		_testPostSiteSuccessSiteInitializer();
		_testPostSiteSuccessSiteTemplate();
		_testPostSiteWithLocalizedDescription();
		_testPostSiteWithLocalizedName();
		_testPostSiteWithNondefaultLocales();
		_testPostSiteWithParentSiteExternalReferenceCode();
		_testPostSiteWithTypeSettings();
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
				description = LinkedHashMapBuilder.put(
					String.valueOf(LocaleUtil.getDefault()),
					RandomTestUtil.randomString()
				).build();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				friendlyUrlPath =
					CharPool.FORWARD_SLASH +
						StringUtil.toLowerCase(RandomTestUtil.randomString());
				manualMembership = RandomTestUtil.randomBoolean();
				membershipRestriction =
					GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION;
				membershipType = MembershipType.create(
					GroupConstants.getTypeLabel(GroupConstants.TYPE_SITE_OPEN));
				name = RandomTestUtil.randomString();
				parentSiteExternalReferenceCode = StringPool.BLANK;
				typeSettings = LinkedHashMapBuilder.put(
					RandomTestUtil.randomString(), RandomTestUtil.randomString()
				).build();
			}
		};
	}

	@Override
	protected Site testDeleteSite_addSite() throws Exception {
		return testPutSiteByExternalReferenceCode_addSite();
	}

	@Override
	protected Site testDeleteSiteByExternalReferenceCode_addSite()
		throws Exception {

		return testPutSiteByExternalReferenceCode_addSite();
	}

	@Override
	protected Site testGetSite_addSite() throws Exception {
		return testPostSite_addSite(randomSite());
	}

	@Override
	protected Site testGetSiteByExternalReferenceCode_addSite()
		throws Exception {

		return testPutSiteByExternalReferenceCode_addSite();
	}

	@Override
	protected Site testGetSitesPage_addSite(Site site) throws Exception {
		Site postSite = siteResource.postSite(site);

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
	protected Site testPutSiteByExternalReferenceCode_addSite()
		throws Exception {

		return siteResource.putSiteByExternalReferenceCode(
			RandomTestUtil.randomString(), randomSite(), getMultipartFiles());
	}

	private void _assertEquals(Group group, Site site) throws Exception {
		Assert.assertEquals(site.getActive(), group.isActive());
		Assert.assertEquals(
			site.getDescription(
			).get(
				String.valueOf(LocaleUtil.getDefault())
			),
			group.getDescription(LocaleUtil.getDefault()));
		Assert.assertEquals(site.getFriendlyUrlPath(), group.getFriendlyURL());
		Assert.assertEquals(
			site.getManualMembership(), group.isManualMembership());
		Assert.assertEquals(
			site.getMembershipRestriction(),
			Integer.valueOf(group.getMembershipRestriction()));
		Assert.assertEquals(
			site.getMembershipType(),
			Site.MembershipType.create(
				GroupConstants.getTypeLabel(group.getType())));
		Assert.assertEquals(
			site.getName(), group.getName(LocaleUtil.getDefault()));
	}

	private void _testGetSiteByExternalReferenceCodeWithDollar()
		throws Exception {

		Site postSite = siteResource.putSiteByExternalReferenceCode(
			RandomTestUtil.randomString() + StringPool.DOLLAR, randomSite(),
			getMultipartFiles());

		Site getSite = siteResource.getSiteByExternalReferenceCode(
			postSite.getExternalReferenceCode());

		assertEquals(postSite, getSite);
		assertValid(getSite);
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

	private Site _testPostSite_addSite(Site site) throws Exception {
		Site postSite = siteResource.postSite(site);

		_sites.add(postSite);

		return postSite;
	}

	private void _testPostSiteBatch() throws Exception {
		Site site = randomSite();

		waitForFinish(
			"COMPLETED",
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					_jsonFactory.createJSONObject(site.toString())
				).toString(),
				"headless-site/v1.0/sites/batch", Http.Method.POST));

		Group group = _groupLocalService.getGroupByExternalReferenceCode(
			site.getExternalReferenceCode(), TestPropsValues.getCompanyId());

		_assertEquals(group, site);

		_sites.add(
			siteResource.getSiteByExternalReferenceCode(
				site.getExternalReferenceCode()));
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

		randomSite.setParentSiteKey(parentSite.getKey());

		Site postSite = _testPostSiteSuccess(randomSite);

		Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
			postSite.getExternalReferenceCode(),
			TestPropsValues.getCompanyId());

		Group parentGroup = group.getParentGroup();

		Assert.assertEquals(parentSite.getKey(), parentGroup.getGroupKey());

		parentSite = _testPostSite_addSite(randomSite());

		randomSite = randomSite();

		randomSite.setParentSiteExternalReferenceCode(
			parentSite.getExternalReferenceCode());

		postSite = _testPostSiteSuccess(randomSite);

		group = _groupLocalService.fetchGroupByExternalReferenceCode(
			postSite.getExternalReferenceCode(),
			TestPropsValues.getCompanyId());

		parentGroup = group.getParentGroup();

		Assert.assertEquals(
			parentSite.getExternalReferenceCode(),
			parentGroup.getExternalReferenceCode());
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

	private void _testPostSiteWithLocalizedDescription() throws Exception {
		Site randomSite = randomSite();

		String description1 = RandomTestUtil.randomString();
		String description2 = RandomTestUtil.randomString();

		randomSite.setDescription(
			LinkedHashMapBuilder.put(
				String.valueOf(LocaleUtil.getDefault()), description1
			).put(
				String.valueOf(LocaleUtil.BRAZIL), description2
			).build());

		Site postSite = _testPostSite_addSite(randomSite);

		Map<String, String> descriptionMap = postSite.getDescription();

		Assert.assertEquals(
			description1,
			descriptionMap.get(String.valueOf(LocaleUtil.getDefault())));
		Assert.assertEquals(
			description2,
			descriptionMap.get(String.valueOf(LocaleUtil.BRAZIL)));
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

		String locales = StringBundler.concat(
			LocaleUtil.BRAZIL, StringPool.COMMA, LocaleUtil.SPAIN);

		site.setTypeSettings(
			LinkedHashMapBuilder.put(
				PropsKeys.LOCALES, locales
			).put(
				"languageId", String.valueOf(LocaleUtil.BRAZIL)
			).build());

		Site postSite = _testPostSite_addSite(site);

		Assert.assertEquals(
			postSite.getTypeSettings(
			).get(
				PropsKeys.LOCALES
			),
			locales);
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

	private void _testPostSiteWithTypeSettings() throws Exception {
		Site randomSite = randomSite();

		randomSite.setTypeSettings(
			LinkedHashMapBuilder.put(
				GroupConstants.TYPE_SETTINGS_KEY_INHERIT_LOCALES, "true"
			).put(
				GooglePlacesWebKeys.GOOGLE_PLACES_API_KEY,
				RandomTestUtil.randomString()
			).put(
				"defaultSiteRoleIds", RandomTestUtil.randomString()
			).put(
				"defaultTeamIds", RandomTestUtil.randomString()
			).put(
				"googleMapsAPIKey", RandomTestUtil.randomString()
			).build());

		Site postSite = _testPostSite_addSite(randomSite);

		Map<String, String> typeSettingsMap = postSite.getTypeSettings();

		Assert.assertEquals(
			"true",
			typeSettingsMap.get(
				GroupConstants.TYPE_SETTINGS_KEY_INHERIT_LOCALES));
		Assert.assertNotNull(typeSettingsMap.get(PropsKeys.LOCALES));
		Assert.assertNull(
			typeSettingsMap.get(GooglePlacesWebKeys.GOOGLE_PLACES_API_KEY));
		Assert.assertNull(typeSettingsMap.get("defaultSiteRoleIds"));
		Assert.assertNull(typeSettingsMap.get("defaultTeamIds"));
		Assert.assertNull(typeSettingsMap.get("googleMapsAPIKey"));
	}

	private void _testPutSiteBatch() throws Exception {
		Site site = randomSite();

		waitForFinish(
			"COMPLETED",
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					_jsonFactory.createJSONObject(site.toString())
				).toString(),
				"headless-site/v1.0/sites/batch", Http.Method.PUT));

		Group group = _groupLocalService.getGroupByExternalReferenceCode(
			site.getExternalReferenceCode(), TestPropsValues.getCompanyId());

		_assertEquals(group, site);

		_sites.add(
			siteResource.getSiteByExternalReferenceCode(
				site.getExternalReferenceCode()));

		Site updatedSite = randomSite();

		updatedSite.setExternalReferenceCode(site.getExternalReferenceCode());

		waitForFinish(
			"COMPLETED",
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					_jsonFactory.createJSONObject(updatedSite.toString())
				).toString(),
				"headless-site/v1.0/sites/batch", Http.Method.PUT));

		group = _groupLocalService.getGroupByExternalReferenceCode(
			updatedSite.getExternalReferenceCode(),
			TestPropsValues.getCompanyId());

		_assertEquals(group, updatedSite);

		_sites.add(
			siteResource.getSiteByExternalReferenceCode(
				updatedSite.getExternalReferenceCode()));
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
				"headless-site/v1.0/sites/batch", Http.Method.PUT));

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
				"headless-site/v1.0/sites/batch", Http.Method.PUT));

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
				"headless-site/v1.0/sites/batch", Http.Method.PUT));

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
				"headless-site/v1.0/sites/batch", Http.Method.PUT));

		group = _groupLocalService.getGroupByExternalReferenceCode(
			site.getExternalReferenceCode(), TestPropsValues.getCompanyId());

		Group parentGroup = _groupLocalService.getGroupByExternalReferenceCode(
			site.getParentSiteExternalReferenceCode(),
			TestPropsValues.getCompanyId());

		Assert.assertEquals(parentGroup.getGroupId(), group.getParentGroupId());
	}

	private void _testPutSiteWithExcludedTypeSettings() throws Exception {
		Site postSite = testPutSite_addSite();

		Group group = _groupLocalService.getGroupByExternalReferenceCode(
			postSite.getExternalReferenceCode(),
			TestPropsValues.getCompanyId());

		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.putAll(
			postSite.getTypeSettings()
		).put(
			GooglePlacesWebKeys.GOOGLE_PLACES_API_KEY,
			RandomTestUtil.randomString()
		).put(
			"defaultSiteRoleIds", RandomTestUtil.randomString()
		).put(
			"defaultTeamIds", RandomTestUtil.randomString()
		).put(
			"googleMapsAPIKey", RandomTestUtil.randomString()
		).build();

		_groupLocalService.updateGroup(
			group.getGroupId(), unicodeProperties.toString());

		Site randomSite = randomSite();

		randomSite.setExternalReferenceCode(
			postSite.getExternalReferenceCode());

		siteResource.putSite(randomSite);

		group = _groupLocalService.fetchGroupByExternalReferenceCode(
			postSite.getExternalReferenceCode(),
			TestPropsValues.getCompanyId());

		UnicodeProperties putSiteUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				group.getTypeSettings()
			).build();

		Assert.assertEquals(
			unicodeProperties.get(GooglePlacesWebKeys.GOOGLE_PLACES_API_KEY),
			putSiteUnicodeProperties.get(
				GooglePlacesWebKeys.GOOGLE_PLACES_API_KEY));
		Assert.assertEquals(
			unicodeProperties.get("defaultSiteRoleIds"),
			putSiteUnicodeProperties.get("defaultSiteRoleIds"));
		Assert.assertEquals(
			unicodeProperties.get("defaultTeamIds"),
			putSiteUnicodeProperties.get("defaultTeamIds"));
		Assert.assertEquals(
			unicodeProperties.get("googleMapsAPIKey"),
			putSiteUnicodeProperties.get("googleMapsAPIKey"));

		Map<String, String> randomSiteTypeSettings =
			randomSite.getTypeSettings();

		for (Map.Entry<String, String> randomSiteTypeSetting :
				randomSiteTypeSettings.entrySet()) {

			Assert.assertEquals(
				putSiteUnicodeProperties.get(randomSiteTypeSetting.getKey()),
				randomSiteTypeSetting.getValue());
		}
	}

	private void _testPutSiteWithParentSiteExternalReferenceCode()
		throws Exception {

		Site postParentSite = testPutSite_addSite();

		Site randomSite = randomSite();

		randomSite.setParentSiteExternalReferenceCode(
			postParentSite.getExternalReferenceCode());

		Site putSite = siteResource.putSite(randomSite);

		Assert.assertEquals(
			postParentSite.getExternalReferenceCode(),
			putSite.getParentSiteExternalReferenceCode());

		randomSite.setParentSiteExternalReferenceCode(StringPool.BLANK);

		putSite = siteResource.putSite(randomSite);

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