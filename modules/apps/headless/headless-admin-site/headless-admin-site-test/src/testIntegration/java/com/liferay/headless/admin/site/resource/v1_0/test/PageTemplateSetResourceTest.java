/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.exportimport.kernel.service.StagingLocalService;
import com.liferay.headless.admin.site.client.dto.v1_0.PageTemplateSet;
import com.liferay.headless.admin.site.client.pagination.Page;
import com.liferay.headless.admin.site.client.pagination.Pagination;
import com.liferay.headless.admin.site.client.problem.Problem;
import com.liferay.headless.admin.site.client.resource.v1_0.PageTemplateSetResource;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.layout.page.template.test.util.LayoutPageTemplateTestUtil;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.constants.FeatureFlagConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.props.test.util.PropsTemporarySwapper;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rubén Pulido
 * @author Georgel Pop
 */
@FeatureFlags(
	featureFlags = {@FeatureFlag("LPD-35443"), @FeatureFlag("LPD-57283")}
)
@RunWith(Arquillian.class)
public class PageTemplateSetResourceTest
	extends BasePageTemplateSetResourceTestCase {

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

		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			TestPropsValues.getCompanyId(), true, "LPD-57283");

		_depotEntry = _addDepotEntry(DepotConstants.TYPE_DESIGN_LIBRARY);
	}

	@Ignore
	@Override
	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		super.testBatchEngineDeleteImportTask();
	}

	@Override
	@Test
	@TestInfo("LPD-104838")
	public void testDeleteDesignLibraryPageTemplateSet() throws Exception {
		super.testDeleteDesignLibraryPageTemplateSet();

		_testDeleteDesignLibraryPageTemplateSetWithAssetLibraryExternalReferenceCodeProblemException();
		_testDeleteDesignLibraryPageTemplateSetWithFeatureFlagDisabledProblemException();
		_testDeleteDesignLibraryPageTemplateSetWithSiteExternalReferenceCodeProblemException();
	}

	@Override
	@Test
	public void testDeleteSitePageTemplateSet() throws Exception {
		PageTemplateSet pageTemplateSet =
			testGetSitePageTemplateSetsPage_addPageTemplateSet(
				testGroup.getExternalReferenceCode(), randomPageTemplateSet());

		pageTemplateSetResource.deleteSitePageTemplateSet(
			testGroup.getExternalReferenceCode(),
			pageTemplateSet.getExternalReferenceCode());

		Assert.assertNull(
			_layoutPageTemplateCollectionLocalService.
				fetchLayoutPageTemplateCollectionByExternalReferenceCode(
					pageTemplateSet.getExternalReferenceCode(),
					testGroup.getGroupId()));

		PageTemplateSet liveGroupPageTemplateSet =
			testGetSitePageTemplateSetsPage_addPageTemplateSet(
				irrelevantGroup.getExternalReferenceCode(),
				randomPageTemplateSet());

		_enableLocalStaging(irrelevantGroup);

		_assertProblemException(
			"BAD_REQUEST", null,
			() -> pageTemplateSetResource.deleteSitePageTemplateSet(
				irrelevantGroup.getExternalReferenceCode(),
				liveGroupPageTemplateSet.getExternalReferenceCode()));
	}

	@Override
	@Test
	@TestInfo("LPD-104838")
	public void testGetDesignLibraryPageTemplateSet() throws Exception {
		super.testGetDesignLibraryPageTemplateSet();

		_testGetDesignLibraryPageTemplateSetActions();
	}

	@Override
	@Test
	@TestInfo("LPD-104838")
	public void testGetDesignLibraryPageTemplateSetsPage() throws Exception {
		super.testGetDesignLibraryPageTemplateSetsPage();

		_testGetDesignLibraryPageTemplateSetsPageAsDesignLibraryOwner();
		_testGetDesignLibraryPageTemplateSetsPageWithoutPermissions();
	}

	@Override
	@Test
	public void testGetSitePageTemplateSet() throws Exception {
		PageTemplateSet pageTemplateSet =
			testGetSitePageTemplateSetsPage_addPageTemplateSet(
				testGroup.getExternalReferenceCode(), randomPageTemplateSet());

		_testGetSitePageTemplateSet(pageTemplateSet);

		_enableLocalStaging();

		_testGetSitePageTemplateSet(pageTemplateSet);
	}

	@Override
	@Test
	public void testGetSitePageTemplateSetsPage() throws Exception {
		super.testGetSitePageTemplateSetsPage();

		String search = RandomTestUtil.randomString();

		Page<PageTemplateSet> page =
			pageTemplateSetResource.getSitePageTemplateSetsPage(
				testGroup.getExternalReferenceCode(), search, null, null, null,
				null);

		long searchTotalCount = page.getTotalCount();

		page = pageTemplateSetResource.getSitePageTemplateSetsPage(
			testGroup.getExternalReferenceCode(), null, null, null, null, null);

		long totalCount = page.getTotalCount();

		pageTemplateSetResource.postSitePageTemplateSet(
			testGroup.getExternalReferenceCode(), randomPageTemplateSet());

		page = pageTemplateSetResource.getSitePageTemplateSetsPage(
			testGroup.getExternalReferenceCode(), search, null, null, null,
			null);

		Assert.assertEquals(searchTotalCount, page.getTotalCount());

		PageTemplateSet pageTemplateSet = randomPageTemplateSet();

		pageTemplateSet.setName(
			StringBundler.concat(
				RandomTestUtil.randomString(), StringPool.SPACE, search,
				StringPool.SPACE, RandomTestUtil.randomString()));

		pageTemplateSetResource.postSitePageTemplateSet(
			testGroup.getExternalReferenceCode(), pageTemplateSet);

		page = pageTemplateSetResource.getSitePageTemplateSetsPage(
			testGroup.getExternalReferenceCode(), search, null, null, null,
			null);

		Assert.assertEquals(searchTotalCount + 1, page.getTotalCount());

		pageTemplateSetResource.postSitePageTemplateSet(
			testGroup.getExternalReferenceCode(), randomPageTemplateSet());

		page = pageTemplateSetResource.getSitePageTemplateSetsPage(
			testGroup.getExternalReferenceCode(), search, null, null, null,
			null);

		Assert.assertEquals(searchTotalCount + 1, page.getTotalCount());

		page = pageTemplateSetResource.getSitePageTemplateSetsPage(
			testGroup.getExternalReferenceCode(), null, null, null, null, null);

		Assert.assertEquals(totalCount + 3, page.getTotalCount());

		pageTemplateSet = randomPageTemplateSet();

		pageTemplateSet.setDateModified(
			new Date(System.currentTimeMillis() - Time.DAY));

		pageTemplateSetResource.postSitePageTemplateSet(
			testGroup.getExternalReferenceCode(), pageTemplateSet);

		page = pageTemplateSetResource.getSitePageTemplateSetsPage(
			testGroup.getExternalReferenceCode(), null, null,
			"dateModified le " +
				new Date(
					System.currentTimeMillis() - Time.DAY
				).toInstant(),
			null, null);

		Assert.assertEquals(1, page.getTotalCount());
	}

	@Ignore
	@Override
	@Test
	public void testGetSitePageTemplateSetsPageWithPagination()
		throws Exception {

		super.testGetSitePageTemplateSetsPageWithPagination();
	}

	@Override
	@Test
	public void testPatchSitePageTemplateSet() throws Exception {
		PageTemplateSet pageTemplateSet = randomPageTemplateSet();

		pageTemplateSetResource.putSitePageTemplateSet(
			testGroup.getExternalReferenceCode(),
			pageTemplateSet.getExternalReferenceCode(), pageTemplateSet);

		pageTemplateSet.setDescription(RandomTestUtil.randomString());

		PageTemplateSet patchPageTemplateSet =
			pageTemplateSetResource.patchSitePageTemplateSet(
				testGroup.getExternalReferenceCode(),
				pageTemplateSet.getExternalReferenceCode(), pageTemplateSet);

		assertEquals(pageTemplateSet, patchPageTemplateSet);
		assertValid(patchPageTemplateSet);

		pageTemplateSet.setName(RandomTestUtil.randomString());

		patchPageTemplateSet = pageTemplateSetResource.patchSitePageTemplateSet(
			testGroup.getExternalReferenceCode(),
			pageTemplateSet.getExternalReferenceCode(), pageTemplateSet);

		assertEquals(pageTemplateSet, patchPageTemplateSet);
		assertValid(patchPageTemplateSet);

		_enableLocalStaging();

		_assertProblemException(
			"BAD_REQUEST", null,
			() -> pageTemplateSetResource.patchSitePageTemplateSet(
				testGroup.getExternalReferenceCode(),
				pageTemplateSet.getExternalReferenceCode(), pageTemplateSet));
	}

	@Override
	@Test
	public void testPostSitePageTemplateSet() throws Exception {
		PageTemplateSet randomPageTemplateSet = randomPageTemplateSet();

		randomPageTemplateSet.setKey(StringPool.BLANK);

		PageTemplateSet postPageTemplateSet = _testPostSitePageTemplateSet(
			randomPageTemplateSet);

		Assert.assertTrue(Validator.isNotNull(postPageTemplateSet.getKey()));

		randomPageTemplateSet = randomPageTemplateSet();

		postPageTemplateSet = _testPostSitePageTemplateSet(
			randomPageTemplateSet);

		Assert.assertEquals(
			randomPageTemplateSet.getKey(), postPageTemplateSet.getKey());

		_postSitePageTemplateSetWithInvalidKey(
			postPageTemplateSet.getKey(),
			StringBundler.concat(
				"Duplicate page template set for group ",
				testGroup.getGroupId(), " with key ",
				postPageTemplateSet.getKey()));

		String key =
			RandomTestUtil.randomString() + StringPool.AMPERSAND +
				RandomTestUtil.randomString();

		_postSitePageTemplateSetWithInvalidKey(
			key,
			StringBundler.concat(
				"Key ", key,
				" must contain only alphanumeric characters, dashes, and ",
				"underscores"));

		key = RandomTestUtil.randomString(80);

		_postSitePageTemplateSetWithInvalidKey(
			key,
			StringBundler.concat(
				"Key ", key, " must have fewer than 75 characters"));
	}

	@Override
	@Test
	public void testPutSitePageTemplateSet() throws Exception {
		PageTemplateSet pageTemplateSet = randomPageTemplateSet();

		PageTemplateSet putPageTemplateSet =
			pageTemplateSetResource.putSitePageTemplateSet(
				testGroup.getExternalReferenceCode(),
				pageTemplateSet.getExternalReferenceCode(), pageTemplateSet);

		assertEquals(pageTemplateSet, putPageTemplateSet);
		assertValid(putPageTemplateSet);

		_enableLocalStaging();

		_assertProblemException(
			"BAD_REQUEST", null,
			() -> pageTemplateSetResource.putSitePageTemplateSet(
				testGroup.getExternalReferenceCode(),
				pageTemplateSet.getExternalReferenceCode(), pageTemplateSet));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"description", "externalReferenceCode", "name"};
	}

	@Override
	protected PageTemplateSet randomPageTemplateSet() throws Exception {
		PageTemplateSet pageTemplateSet = super.randomPageTemplateSet();

		pageTemplateSet.setDateCreated(new Date(System.currentTimeMillis()));
		pageTemplateSet.setDateModified(new Date(System.currentTimeMillis()));

		return pageTemplateSet;
	}

	@Override
	protected PageTemplateSet
			testDeleteDesignLibraryPageTemplateSet_addPageTemplateSet()
		throws Exception {

		Group group = _depotEntry.getGroup();

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateCollection(
				group.getGroupId());

		return new PageTemplateSet() {
			{
				setExternalReferenceCode(
					layoutPageTemplateCollection::getExternalReferenceCode);
			}
		};
	}

	@Override
	protected String
			testDeleteDesignLibraryPageTemplateSet_getDesignLibraryExternalReferenceCode()
		throws Exception {

		Group group = _depotEntry.getGroup();

		return group.getExternalReferenceCode();
	}

	@Override
	protected PageTemplateSet
			testGetDesignLibraryPageTemplateSet_addPageTemplateSet()
		throws Exception {

		return _addDesignLibraryPageTemplateSet(
			_depotEntry.getGroup(), randomPageTemplateSet());
	}

	@Override
	protected String
			testGetDesignLibraryPageTemplateSet_getDesignLibraryExternalReferenceCode()
		throws Exception {

		Group group = _depotEntry.getGroup();

		return group.getExternalReferenceCode();
	}

	@Override
	protected PageTemplateSet
			testGetDesignLibraryPageTemplateSetsPage_addPageTemplateSet(
				String designLibraryExternalReferenceCode,
				PageTemplateSet pageTemplateSet)
		throws Exception {

		return _addDesignLibraryPageTemplateSet(
			_groupLocalService.getGroupByExternalReferenceCode(
				designLibraryExternalReferenceCode,
				TestPropsValues.getCompanyId()),
			pageTemplateSet);
	}

	@Override
	protected String
			testGetDesignLibraryPageTemplateSetsPage_getDesignLibraryExternalReferenceCode()
		throws Exception {

		Group group = _depotEntry.getGroup();

		return group.getExternalReferenceCode();
	}

	@Override
	protected Map<String, Map<String, String>>
		testGetSitePageTemplateSetsPage_getExpectedActions(
			String siteExternalReferenceCode) {

		return new HashMap<>();
	}

	@Override
	protected PageTemplateSet testPostSitePageTemplateSet_addPageTemplateSet(
			PageTemplateSet pageTemplateSet)
		throws Exception {

		return pageTemplateSetResource.postSitePageTemplateSet(
			testGroup.getExternalReferenceCode(), pageTemplateSet);
	}

	private DepotEntry _addDepotEntry(int type) throws Exception {
		return _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, type,
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId()));
	}

	private PageTemplateSet _addDesignLibraryPageTemplateSet(
			Group group, PageTemplateSet pageTemplateSet)
		throws Exception {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					pageTemplateSet.getExternalReferenceCode(),
					TestPropsValues.getUserId(), group.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					pageTemplateSet.getKey(), pageTemplateSet.getName(),
					pageTemplateSet.getDescription(),
					LayoutPageTemplateCollectionTypeConstants.BASIC,
					ServiceContextTestUtil.getServiceContext(
						group.getGroupId(), TestPropsValues.getUserId()));

		return pageTemplateSetResource.getDesignLibraryPageTemplateSet(
			group.getExternalReferenceCode(),
			layoutPageTemplateCollection.getExternalReferenceCode());
	}

	private void _assertActionHref(
		Map<String, Map<String, String>> actions, String content,
		String... keys) {

		for (String key : keys) {
			Map<String, String> action = actions.get(key);

			String href = action.get("href");

			Assert.assertTrue(key, href.contains(content));
		}
	}

	private void _assertProblemException(
			String status, String title,
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try {
			unsafeRunnable.run();

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals(status, problem.getStatus());
			Assert.assertEquals(title, problem.getTitle());
		}
	}

	private void _enableLocalStaging() throws Exception {
		_enableLocalStaging(testGroup);
	}

	private void _enableLocalStaging(Group group) throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.batch.engine.internal." +
					"BatchEngineImportTaskExecutorImpl",
				LoggerTestUtil.OFF)) {

			_stagingLocalService.enableLocalStaging(
				TestPropsValues.getUserId(), group, true, false,
				ServiceContextTestUtil.getServiceContext(
					group, TestPropsValues.getUserId()));
		}

		Assert.assertTrue(group.hasStagingGroup());
	}

	private PageTemplateSetResource
			_getDesignLibraryOwnerPageTemplateSetResource()
		throws Exception {

		String password = RandomTestUtil.randomString();

		User user = UserTestUtil.addUser(testCompany, password);

		Group group = _depotEntry.getGroup();

		_userLocalService.addGroupUser(group.getGroupId(), user.getUserId());

		Role role = _roleLocalService.getRole(
			testCompany.getCompanyId(),
			DepotRolesConstants.DESIGN_LIBRARY_OWNER);

		_userGroupRoleLocalService.addUserGroupRoles(
			user.getUserId(), group.getGroupId(),
			new long[] {role.getRoleId()});

		return _getPageTemplateSetResource(password, user);
	}

	private PageTemplateSetResource _getPageTemplateSetResource(
		String password, User user) {

		return PageTemplateSetResource.builder(
		).authentication(
			user.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	private PageTemplateSetResource
			_getUserWithoutPermissionsPageTemplateSetResource()
		throws Exception {

		String password = RandomTestUtil.randomString();

		User user = UserTestUtil.addUser(testCompany, password);

		_userLocalService.addGroupUser(
			testGroup.getGroupId(), user.getUserId());

		return _getPageTemplateSetResource(password, user);
	}

	private void _postSitePageTemplateSetWithInvalidKey(
			String key, String title)
		throws Exception {

		PageTemplateSet pageTemplateSet = randomPageTemplateSet();

		pageTemplateSet.setKey(key);

		_assertProblemException(
			"CONFLICT", title,
			() -> pageTemplateSetResource.postSitePageTemplateSet(
				testGroup.getExternalReferenceCode(), pageTemplateSet));
	}

	private void _testDeleteDesignLibraryPageTemplateSetWithAssetLibraryExternalReferenceCodeProblemException()
		throws Exception {

		DepotEntry assetLibraryDepotEntry = _addDepotEntry(
			DepotConstants.TYPE_ASSET_LIBRARY);

		Group group = assetLibraryDepotEntry.getGroup();

		_assertProblemException(
			"BAD_REQUEST", null,
			() -> pageTemplateSetResource.deleteDesignLibraryPageTemplateSet(
				group.getExternalReferenceCode(),
				RandomTestUtil.randomString()));
	}

	private void _testDeleteDesignLibraryPageTemplateSetWithFeatureFlagDisabledProblemException()
		throws Exception {

		Group group = _depotEntry.getGroup();

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateCollection(
				group.getGroupId());

		try (PropsTemporarySwapper propsTemporarySwapper =
				new PropsTemporarySwapper(
					FeatureFlagConstants.getKey("LPD-57283"),
					Boolean.FALSE.toString())) {

			_assertProblemException(
				"BAD_REQUEST",
				"Feature flag LPD-57283 is disabled for company " +
					testCompany.getCompanyId(),
				() ->
					pageTemplateSetResource.deleteDesignLibraryPageTemplateSet(
						group.getExternalReferenceCode(),
						layoutPageTemplateCollection.
							getExternalReferenceCode()));
		}
	}

	private void _testDeleteDesignLibraryPageTemplateSetWithSiteExternalReferenceCodeProblemException()
		throws Exception {

		_assertProblemException(
			"BAD_REQUEST", null,
			() -> pageTemplateSetResource.deleteDesignLibraryPageTemplateSet(
				testGroup.getExternalReferenceCode(),
				RandomTestUtil.randomString()));
	}

	private void _testGetDesignLibraryPageTemplateSetActions()
		throws Exception {

		Group group = _depotEntry.getGroup();

		PageTemplateSet pageTemplateSet = _addDesignLibraryPageTemplateSet(
			group, randomPageTemplateSet());

		PageTemplateSet getPageTemplateSet =
			pageTemplateSetResource.getDesignLibraryPageTemplateSet(
				group.getExternalReferenceCode(),
				pageTemplateSet.getExternalReferenceCode());

		_assertActionHref(
			getPageTemplateSet.getActions(),
			StringBundler.concat(
				"/design-libraries/", group.getExternalReferenceCode(),
				"/page-template-sets/",
				pageTemplateSet.getExternalReferenceCode()),
			"delete", "get");
	}

	private void _testGetDesignLibraryPageTemplateSetsPageAsDesignLibraryOwner()
		throws Exception {

		Group group = _depotEntry.getGroup();

		PageTemplateSet pageTemplateSet = _addDesignLibraryPageTemplateSet(
			group, randomPageTemplateSet());

		PageTemplateSetResource designLibraryOwnerPageTemplateSetResource =
			_getDesignLibraryOwnerPageTemplateSetResource();

		Page<PageTemplateSet> page =
			designLibraryOwnerPageTemplateSetResource.
				getDesignLibraryPageTemplateSetsPage(
					group.getExternalReferenceCode(), null, null, null,
					Pagination.of(1, 10), null);

		assertContains(pageTemplateSet, (List<PageTemplateSet>)page.getItems());
	}

	private void _testGetDesignLibraryPageTemplateSetsPageWithoutPermissions()
		throws Exception {

		Group group = _depotEntry.getGroup();

		_addDesignLibraryPageTemplateSet(group, randomPageTemplateSet());

		PageTemplateSetResource userWithoutPermissionsPageTemplateSetResource =
			_getUserWithoutPermissionsPageTemplateSetResource();

		Page<PageTemplateSet> page =
			userWithoutPermissionsPageTemplateSetResource.
				getDesignLibraryPageTemplateSetsPage(
					group.getExternalReferenceCode(), null, null, null,
					Pagination.of(1, 10), null);

		Assert.assertEquals(0, page.getTotalCount());
	}

	private void _testGetSitePageTemplateSet(PageTemplateSet pageTemplateSet)
		throws Exception {

		PageTemplateSet getPageTemplateSet =
			pageTemplateSetResource.getSitePageTemplateSet(
				testGroup.getExternalReferenceCode(),
				pageTemplateSet.getExternalReferenceCode());

		assertEquals(pageTemplateSet, getPageTemplateSet);
		assertValid(getPageTemplateSet);
	}

	private PageTemplateSet _testPostSitePageTemplateSet(
			PageTemplateSet pageTemplateSet)
		throws Exception {

		PageTemplateSet postPageTemplateSet =
			testPostSitePageTemplateSet_addPageTemplateSet(pageTemplateSet);

		assertEquals(pageTemplateSet, postPageTemplateSet);
		assertValid(postPageTemplateSet);

		return postPageTemplateSet;
	}

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutPageTemplateCollectionLocalService
		_layoutPageTemplateCollectionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private StagingLocalService _stagingLocalService;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}