/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.exportimport.kernel.service.StagingLocalService;
import com.liferay.exportimport.test.util.LazyReferencingTestUtil;
import com.liferay.headless.admin.site.client.dto.v1_0.DisplayPageTemplateFolder;
import com.liferay.headless.admin.site.client.pagination.Page;
import com.liferay.headless.admin.site.client.permission.Permission;
import com.liferay.headless.admin.site.resource.v1_0.test.util.ProblemExceptionTestUtil;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionService;
import com.liferay.layout.page.template.test.util.LayoutPageTemplateTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
 * @author Bárbara Cabrera
 * @author Javier Moral
 */
@FeatureFlag("LPD-57283")
@RunWith(Arquillian.class)
public class DisplayPageTemplateFolderResourceTest
	extends BaseDisplayPageTemplateFolderResourceTestCase {

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
	@TestInfo("LPD-107953")
	public void testDeleteDesignLibraryDisplayPageTemplateFolder()
		throws Exception {

		super.testDeleteDesignLibraryDisplayPageTemplateFolder();

		_testDeleteDesignLibraryDisplayPageTemplateFolderWithAssetLibraryExternalReferenceCodeProblemException();
		_testDeleteDesignLibraryDisplayPageTemplateFolderWithSiteExternalReferenceCodeProblemException();
	}

	@Override
	@Test
	public void testDeleteSiteDisplayPageTemplateFolder() throws Exception {
		DisplayPageTemplateFolder postDisplayPageTemplateFolder =
			testGetSiteDisplayPageTemplateFoldersPage_addDisplayPageTemplateFolder(
				testGroup.getExternalReferenceCode(),
				randomDisplayPageTemplateFolder());

		displayPageTemplateFolderResource.deleteSiteDisplayPageTemplateFolder(
			testGroup.getExternalReferenceCode(),
			postDisplayPageTemplateFolder.getExternalReferenceCode());

		Assert.assertNull(
			_layoutPageTemplateCollectionService.
				fetchLayoutPageTemplateCollection(
					postDisplayPageTemplateFolder.getExternalReferenceCode(),
					testGroup.getGroupId()));

		DisplayPageTemplateFolder liveGroupDisplayPageTemplateFolder =
			testGetSiteDisplayPageTemplateFoldersPage_addDisplayPageTemplateFolder(
				irrelevantGroup.getExternalReferenceCode(),
				randomDisplayPageTemplateFolder());

		_enableLocalStaging(irrelevantGroup);

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST", "The site does not support this operation",
			() ->
				displayPageTemplateFolderResource.
					deleteSiteDisplayPageTemplateFolder(
						irrelevantGroup.getExternalReferenceCode(),
						liveGroupDisplayPageTemplateFolder.
							getExternalReferenceCode()));
	}

	@Override
	@Test
	@TestInfo("LPD-107953")
	public void testGetDesignLibraryDisplayPageTemplateFolder()
		throws Exception {

		super.testGetDesignLibraryDisplayPageTemplateFolder();

		Group group = _depotEntry.getGroup();

		DisplayPageTemplateFolder displayPageTemplateFolder =
			_addDesignLibraryDisplayPageTemplateFolder(group);

		DisplayPageTemplateFolder getDisplayPageTemplateFolder =
			displayPageTemplateFolderResource.
				getDesignLibraryDisplayPageTemplateFolder(
					group.getExternalReferenceCode(),
					displayPageTemplateFolder.getExternalReferenceCode());

		assertEquals(displayPageTemplateFolder, getDisplayPageTemplateFolder);
		assertValid(getDisplayPageTemplateFolder);

		_assertActionHref(
			getDisplayPageTemplateFolder.getActions(),
			StringBundler.concat(
				"/design-libraries/", group.getExternalReferenceCode(),
				"/display-page-template-folders/",
				displayPageTemplateFolder.getExternalReferenceCode()),
			"delete", "get", "permissions");
	}

	@Override
	@Test
	@TestInfo("LPD-107953")
	public void testGetDesignLibraryDisplayPageTemplateFolderPermissionsPage()
		throws Exception {

		DisplayPageTemplateFolder displayPageTemplateFolder =
			testGetDesignLibraryDisplayPageTemplateFolderPermissionsPage_addDisplayPageTemplateFolder();
		Group group = _depotEntry.getGroup();

		Page<Permission> page =
			displayPageTemplateFolderResource.
				getDesignLibraryDisplayPageTemplateFolderPermissionsPage(
					group.getExternalReferenceCode(),
					displayPageTemplateFolder.getExternalReferenceCode(),
					RoleConstants.GUEST);

		List<Permission> permissions = (List<Permission>)page.getItems();

		Assert.assertEquals(permissions.toString(), 1, permissions.size());

		Permission permission = permissions.get(0);

		Assert.assertEquals(RoleConstants.GUEST, permission.getRoleName());

		_assertActionHref(
			page.getActions(),
			StringBundler.concat(
				"/design-libraries/", group.getExternalReferenceCode(),
				"/display-page-template-folders/",
				displayPageTemplateFolder.getExternalReferenceCode(),
				"/permissions"),
			"get", "replace");
	}

	@Override
	@Test
	@TestInfo("LPD-107953")
	public void testGetDesignLibraryDisplayPageTemplateFoldersPage()
		throws Exception {

		super.testGetDesignLibraryDisplayPageTemplateFoldersPage();
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteDisplayPageTemplateFolder() throws Exception {
		DisplayPageTemplateFolder postDisplayPageTemplateFolder =
			testGetSiteDisplayPageTemplateFoldersPage_addDisplayPageTemplateFolder(
				testGroup.getExternalReferenceCode(),
				randomDisplayPageTemplateFolder());

		DisplayPageTemplateFolder getDisplayPageTemplateFolder =
			displayPageTemplateFolderResource.getSiteDisplayPageTemplateFolder(
				testGroup.getExternalReferenceCode(),
				postDisplayPageTemplateFolder.getExternalReferenceCode());

		assertEquals(
			postDisplayPageTemplateFolder, getDisplayPageTemplateFolder);
		assertValid(getDisplayPageTemplateFolder);

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionService.
				getLayoutPageTemplateCollection(
					getDisplayPageTemplateFolder.getExternalReferenceCode(),
					testGroup.getGroupId());

		List<LayoutPageTemplateCollection> parentLayoutPageTemplateCollections =
			new ArrayList<>();

		_assertParentDisplayPageTemplateFolder(
			_layoutPageTemplateCollectionService.
				moveLayoutPageTemplateCollection(
					layoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId(),
					_getParentLayoutPageTemplateCollectionId(
						5, parentLayoutPageTemplateCollections)),
			parentLayoutPageTemplateCollections);

		_enableLocalStaging();

		assertEquals(
			postDisplayPageTemplateFolder,
			displayPageTemplateFolderResource.getSiteDisplayPageTemplateFolder(
				testGroup.getExternalReferenceCode(),
				postDisplayPageTemplateFolder.getExternalReferenceCode()));
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteDisplayPageTemplateFolderPermissionsPage()
		throws Exception {

		super.testGetSiteDisplayPageTemplateFolderPermissionsPage();
	}

	@Override
	@Test
	public void testGetSiteDisplayPageTemplateFoldersPage() throws Exception {
		super.testGetSiteDisplayPageTemplateFoldersPage();

		DisplayPageTemplateFolder displayPageTemplateFolder =
			randomDisplayPageTemplateFolder();

		displayPageTemplateFolder.setDateModified(
			new Date(System.currentTimeMillis() - Time.DAY));

		displayPageTemplateFolderResource.postSiteDisplayPageTemplateFolder(
			testGroup.getExternalReferenceCode(), displayPageTemplateFolder);

		Page<DisplayPageTemplateFolder> page =
			displayPageTemplateFolderResource.
				getSiteDisplayPageTemplateFoldersPage(
					testGroup.getExternalReferenceCode(), null, null,
					"dateModified le " +
						new Date(
							System.currentTimeMillis() - Time.DAY
						).toInstant(),
					null, null);

		Assert.assertEquals(1, page.getTotalCount());
	}

	@Override
	@Test
	public void testPatchSiteDisplayPageTemplateFolder() throws Exception {
		DisplayPageTemplateFolder parentDisplayPageTemplateFolder =
			testPostSiteDisplayPageTemplateFolder_addDisplayPageTemplateFolder(
				randomDisplayPageTemplateFolder());

		DisplayPageTemplateFolder displayPageTemplateFolder =
			testPostSiteDisplayPageTemplateFolder_addDisplayPageTemplateFolder(
				randomDisplayPageTemplateFolder());

		Assert.assertNull(
			displayPageTemplateFolder.
				getParentDisplayPageTemplateFolderExternalReferenceCode());

		_testPatchSiteDisplayPageTemplateFolder(
			displayPageTemplateFolder.getExternalReferenceCode(),
			parentDisplayPageTemplateFolder.getExternalReferenceCode());

		_testPatchSiteDisplayPageTemplateFolder(
			displayPageTemplateFolder.getExternalReferenceCode(), null);
		_testPatchSiteDisplayPageTemplateFolder(
			displayPageTemplateFolder.getExternalReferenceCode(),
			StringPool.BLANK);

		ProblemExceptionTestUtil.assertProblemException(
			"NOT_FOUND", null,
			() ->
				displayPageTemplateFolderResource.
					patchSiteDisplayPageTemplateFolder(
						testGroup.getExternalReferenceCode(),
						RandomTestUtil.randomString(),
						randomDisplayPageTemplateFolder()));

		_enableLocalStaging();

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST", "The site does not support this operation",
			() ->
				displayPageTemplateFolderResource.
					patchSiteDisplayPageTemplateFolder(
						testGroup.getExternalReferenceCode(),
						displayPageTemplateFolder.getExternalReferenceCode(),
						displayPageTemplateFolder));
	}

	@Override
	@Test
	public void testPostSiteDisplayPageTemplateFolder() throws Exception {
		super.testPostSiteDisplayPageTemplateFolder();

		_testPostSiteDisplayPageTemplateFolderWithExistingParentExternalReferenceCode();

		DisplayPageTemplateFolder postDisplayPageTemplateFolder =
			testPostSiteDisplayPageTemplateFolder_addDisplayPageTemplateFolder(
				randomDisplayPageTemplateFolder());

		_testPostSiteDisplayPageTemplateFolderWithInvalidKey(
			postDisplayPageTemplateFolder.getKey(),
			StringBundler.concat(
				"Duplicate display page template folder for group ",
				testGroup.getGroupId(), " with key ",
				postDisplayPageTemplateFolder.getKey()));

		String key =
			RandomTestUtil.randomString() + StringPool.AMPERSAND +
				RandomTestUtil.randomString();

		_testPostSiteDisplayPageTemplateFolderWithInvalidKey(
			key,
			StringBundler.concat(
				"Key ", key,
				" must contain only alphanumeric characters, dashes, and ",
				"underscores"));

		key = RandomTestUtil.randomString(80);

		_testPostSiteDisplayPageTemplateFolderWithInvalidKey(
			key,
			StringBundler.concat(
				"Key ", key, " must have fewer than 75 characters"));

		DisplayPageTemplateFolder displayPageTemplateFolder =
			randomDisplayPageTemplateFolder();
		String parentExternalReferenceCode = RandomTestUtil.randomString();

		displayPageTemplateFolder.
			setParentDisplayPageTemplateFolderExternalReferenceCode(
				parentExternalReferenceCode);

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST",
			"The parent display page template folder type does not match the " +
				"display page type",
			() ->
				displayPageTemplateFolderResource.
					postSiteDisplayPageTemplateFolder(
						testGroup.getExternalReferenceCode(),
						displayPageTemplateFolder));

		_enableLocalStaging();

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST", "The site does not support this operation",
			() ->
				displayPageTemplateFolderResource.
					postSiteDisplayPageTemplateFolder(
						testGroup.getExternalReferenceCode(),
						randomDisplayPageTemplateFolder()));
	}

	@Override
	@Test
	@TestInfo("LPD-107953")
	public void testPutDesignLibraryDisplayPageTemplateFolderPermissionsPage()
		throws Exception {

		DisplayPageTemplateFolder displayPageTemplateFolder =
			testPutDesignLibraryDisplayPageTemplateFolderPermissionsPage_addDisplayPageTemplateFolder();
		Group group = _depotEntry.getGroup();
		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		displayPageTemplateFolderResource.
			putDesignLibraryDisplayPageTemplateFolderPermissionsPage(
				group.getExternalReferenceCode(),
				displayPageTemplateFolder.getExternalReferenceCode(),
				new Permission[] {
					new Permission() {
						{
							setActionIds(new String[] {"VIEW"});
							setRoleName(role.getName());
						}
					}
				});

		Page<Permission> page =
			displayPageTemplateFolderResource.
				getDesignLibraryDisplayPageTemplateFolderPermissionsPage(
					group.getExternalReferenceCode(),
					displayPageTemplateFolder.getExternalReferenceCode(),
					role.getName());

		List<Permission> permissions = (List<Permission>)page.getItems();

		Assert.assertEquals(permissions.toString(), 1, permissions.size());

		Permission permission = permissions.get(0);

		Assert.assertArrayEquals(
			new String[] {"VIEW"}, permission.getActionIds());
		Assert.assertEquals(role.getName(), permission.getRoleName());

		ProblemExceptionTestUtil.assertProblemException(
			"NOT_FOUND", null,
			() ->
				displayPageTemplateFolderResource.
					putDesignLibraryDisplayPageTemplateFolderPermissionsPage(
						group.getExternalReferenceCode(),
						displayPageTemplateFolder.getExternalReferenceCode(),
						new Permission[] {
							new Permission() {
								{
									setActionIds(new String[] {"-"});
									setRoleName("-");
								}
							}
						}));

		_testPutDesignLibraryDisplayPageTemplateFolderPermissionsPageWithSiteExternalReferenceCodeProblemException(
			role);
	}

	@Override
	@Test
	public void testPutSiteDisplayPageTemplateFolder() throws Exception {
		DisplayPageTemplateFolder displayPageTemplateFolder =
			_testPutSiteDisplayPageTemplateFolder(
				randomDisplayPageTemplateFolder(), null);

		_assertNoParentDisplayPageTemplateFolder(displayPageTemplateFolder);

		_testPutSiteDisplayPageTemplateFolderWithMismatchedParentDisplayPageTemplateFolder();
		_testPutSiteDisplayPageTemplateFolderWithParentDisplayPageTemplateFolder();

		DisplayPageTemplateFolder liveGroupDisplayPageTemplateFolder =
			_testPutSiteDisplayPageTemplateFolder(
				randomDisplayPageTemplateFolder(),
				displayPageTemplateFolder.getExternalReferenceCode());

		_assertParentDisplayPageTemplateFolder(
			liveGroupDisplayPageTemplateFolder, displayPageTemplateFolder);

		_enableLocalStaging();

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST", "The site does not support this operation",
			() ->
				displayPageTemplateFolderResource.
					putSiteDisplayPageTemplateFolder(
						testGroup.getExternalReferenceCode(),
						liveGroupDisplayPageTemplateFolder.
							getExternalReferenceCode(),
						liveGroupDisplayPageTemplateFolder));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"description", "externalReferenceCode", "name"};
	}

	@Override
	protected DisplayPageTemplateFolder randomDisplayPageTemplateFolder()
		throws Exception {

		DisplayPageTemplateFolder displayPageTemplateFolder =
			super.randomDisplayPageTemplateFolder();

		displayPageTemplateFolder.setDateCreated(
			new Date(System.currentTimeMillis()));
		displayPageTemplateFolder.setDateModified(
			new Date(System.currentTimeMillis()));
		displayPageTemplateFolder.
			setParentDisplayPageTemplateFolderExternalReferenceCode(
				(String)null);

		return displayPageTemplateFolder;
	}

	@Override
	protected String
			testBatchEngineDeleteImportTask_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	@Override
	protected DisplayPageTemplateFolder
			testDeleteDesignLibraryDisplayPageTemplateFolder_addDisplayPageTemplateFolder()
		throws Exception {

		return _addDesignLibraryDisplayPageTemplateFolder(
			_depotEntry.getGroup());
	}

	@Override
	protected String
			testDeleteDesignLibraryDisplayPageTemplateFolder_getDesignLibraryExternalReferenceCode()
		throws Exception {

		Group group = _depotEntry.getGroup();

		return group.getExternalReferenceCode();
	}

	@Override
	protected DisplayPageTemplateFolder
			testGetDesignLibraryDisplayPageTemplateFolder_addDisplayPageTemplateFolder()
		throws Exception {

		return _addDesignLibraryDisplayPageTemplateFolder(
			_depotEntry.getGroup());
	}

	@Override
	protected String
			testGetDesignLibraryDisplayPageTemplateFolder_getDesignLibraryExternalReferenceCode()
		throws Exception {

		Group group = _depotEntry.getGroup();

		return group.getExternalReferenceCode();
	}

	@Override
	protected DisplayPageTemplateFolder
			testGetDesignLibraryDisplayPageTemplateFolderPermissionsPage_addDisplayPageTemplateFolder()
		throws Exception {

		return _addDesignLibraryDisplayPageTemplateFolder(
			_depotEntry.getGroup());
	}

	@Override
	protected DisplayPageTemplateFolder
			testGetDesignLibraryDisplayPageTemplateFoldersPage_addDisplayPageTemplateFolder(
				String designLibraryExternalReferenceCode,
				DisplayPageTemplateFolder displayPageTemplateFolder)
		throws Exception {

		return _addDesignLibraryDisplayPageTemplateFolder(
			_groupLocalService.getGroupByExternalReferenceCode(
				designLibraryExternalReferenceCode,
				TestPropsValues.getCompanyId()),
			displayPageTemplateFolder);
	}

	@Override
	protected String
			testGetDesignLibraryDisplayPageTemplateFoldersPage_getDesignLibraryExternalReferenceCode()
		throws Exception {

		Group group = _depotEntry.getGroup();

		return group.getExternalReferenceCode();
	}

	@Override
	protected DisplayPageTemplateFolder
			testGetSiteDisplayPageTemplateFoldersPage_addDisplayPageTemplateFolder(
				String siteExternalReferenceCode,
				DisplayPageTemplateFolder displayPageTemplateFolder)
		throws Exception {

		return displayPageTemplateFolderResource.
			postSiteDisplayPageTemplateFolder(
				siteExternalReferenceCode, displayPageTemplateFolder);
	}

	@Override
	protected Map<String, Map<String, String>>
			testGetSiteDisplayPageTemplateFoldersPage_getExpectedActions(
				String siteExternalReferenceCode)
		throws Exception {

		return Collections.emptyMap();
	}

	@Override
	protected DisplayPageTemplateFolder
			testPostSiteDisplayPageTemplateFolder_addDisplayPageTemplateFolder(
				DisplayPageTemplateFolder displayPageTemplateFolder)
		throws Exception {

		return testGetSiteDisplayPageTemplateFoldersPage_addDisplayPageTemplateFolder(
			testGroup.getExternalReferenceCode(), displayPageTemplateFolder);
	}

	@Override
	protected DisplayPageTemplateFolder
			testPutDesignLibraryDisplayPageTemplateFolderPermissionsPage_addDisplayPageTemplateFolder()
		throws Exception {

		return _addDesignLibraryDisplayPageTemplateFolder(
			_depotEntry.getGroup());
	}

	private DepotEntry _addDepotEntry(int type) throws Exception {
		return _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, type,
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId()));
	}

	private DisplayPageTemplateFolder
			_addDesignLibraryDisplayPageTemplateFolder(Group group)
		throws Exception {

		return _addDesignLibraryDisplayPageTemplateFolder(
			group, randomDisplayPageTemplateFolder());
	}

	private DisplayPageTemplateFolder
			_addDesignLibraryDisplayPageTemplateFolder(
				Group group,
				DisplayPageTemplateFolder displayPageTemplateFolder)
		throws Exception {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateCollection(
				displayPageTemplateFolder.getDescription(),
				displayPageTemplateFolder.getExternalReferenceCode(),
				group.getGroupId(), displayPageTemplateFolder.getKey(),
				displayPageTemplateFolder.getName(),
				LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE);

		return displayPageTemplateFolderResource.
			getDesignLibraryDisplayPageTemplateFolder(
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

	private void _assertNoParentDisplayPageTemplateFolder(
			DisplayPageTemplateFolder displayPageTemplateFolder)
		throws Exception {

		_assertParentDisplayPageTemplateFolder(
			_layoutPageTemplateCollectionService.
				getLayoutPageTemplateCollection(
					displayPageTemplateFolder.getExternalReferenceCode(),
					testGroup.getGroupId()),
			Collections.emptyList());
	}

	private void _assertParentDisplayPageTemplateFolder(
			DisplayPageTemplateFolder displayPageTemplateFolder,
			DisplayPageTemplateFolder parentDisplayPageTemplateFolder)
		throws Exception {

		_assertParentDisplayPageTemplateFolder(
			_layoutPageTemplateCollectionService.
				getLayoutPageTemplateCollection(
					displayPageTemplateFolder.getExternalReferenceCode(),
					testGroup.getGroupId()),
			ListUtil.fromArray(
				_layoutPageTemplateCollectionService.
					getLayoutPageTemplateCollection(
						parentDisplayPageTemplateFolder.
							getExternalReferenceCode(),
						testGroup.getGroupId())));
	}

	private void _assertParentDisplayPageTemplateFolder(
			LayoutPageTemplateCollection layoutPageTemplateCollection,
			List<LayoutPageTemplateCollection>
				parentLayoutPageTemplateCollections)
		throws Exception {

		DisplayPageTemplateFolder displayPageTemplateFolder =
			displayPageTemplateFolderResource.getSiteDisplayPageTemplateFolder(
				testGroup.getExternalReferenceCode(),
				layoutPageTemplateCollection.getExternalReferenceCode());

		if (parentLayoutPageTemplateCollections.isEmpty()) {
			Assert.assertNull(
				displayPageTemplateFolder.getParentDisplayPageTemplateFolder());
			Assert.assertNull(
				displayPageTemplateFolder.
					getParentDisplayPageTemplateFolderExternalReferenceCode());

			return;
		}

		DisplayPageTemplateFolder parentDisplayPageTemplateFolder =
			displayPageTemplateFolder.getParentDisplayPageTemplateFolder();

		Assert.assertEquals(
			parentDisplayPageTemplateFolder.getExternalReferenceCode(),
			displayPageTemplateFolder.
				getParentDisplayPageTemplateFolderExternalReferenceCode());

		for (LayoutPageTemplateCollection parentLayoutPageTemplateCollection :
				parentLayoutPageTemplateCollections) {

			assertEquals(
				displayPageTemplateFolderResource.
					getSiteDisplayPageTemplateFolder(
						testGroup.getExternalReferenceCode(),
						parentLayoutPageTemplateCollection.
							getExternalReferenceCode()),
				parentDisplayPageTemplateFolder);

			parentDisplayPageTemplateFolder =
				parentDisplayPageTemplateFolder.
					getParentDisplayPageTemplateFolder();
		}

		Assert.assertNull(parentDisplayPageTemplateFolder);
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

	private DisplayPageTemplateFolder _getParentDisplayPageTemplateFolder(
			int count)
		throws Exception {

		DisplayPageTemplateFolder parentDisplayPageTemplateFolder =
			randomDisplayPageTemplateFolder();

		for (int i = 0; i < count; i++) {
			DisplayPageTemplateFolder displayPageTemplateFolder =
				randomDisplayPageTemplateFolder();

			displayPageTemplateFolder.setParentDisplayPageTemplateFolder(
				parentDisplayPageTemplateFolder);
			displayPageTemplateFolder.
				setParentDisplayPageTemplateFolderExternalReferenceCode(
					parentDisplayPageTemplateFolder.getExternalReferenceCode());

			parentDisplayPageTemplateFolder = displayPageTemplateFolder;
		}

		return parentDisplayPageTemplateFolder;
	}

	private long _getParentLayoutPageTemplateCollectionId(
			int count,
			List<LayoutPageTemplateCollection>
				parentLayoutPageTemplateCollections)
		throws Exception {

		long parentLayoutPageTemplateCollectionId =
			LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT;

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup, TestPropsValues.getUserId());

		for (int i = 0; i < count; i++) {
			LayoutPageTemplateCollection parentLayoutPageTemplateCollection =
				_layoutPageTemplateCollectionService.
					addLayoutPageTemplateCollection(
						null, testGroup.getGroupId(),
						parentLayoutPageTemplateCollectionId, null,
						RandomTestUtil.randomString(),
						RandomTestUtil.randomString(),
						LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE,
						serviceContext);

			parentLayoutPageTemplateCollectionId =
				parentLayoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId();

			parentLayoutPageTemplateCollections.add(
				parentLayoutPageTemplateCollection);
		}

		Collections.reverse(parentLayoutPageTemplateCollections);

		return parentLayoutPageTemplateCollectionId;
	}

	private void _testDeleteDesignLibraryDisplayPageTemplateFolderWithAssetLibraryExternalReferenceCodeProblemException()
		throws Exception {

		DepotEntry assetLibraryDepotEntry = _addDepotEntry(
			DepotConstants.TYPE_ASSET_LIBRARY);

		Group group = assetLibraryDepotEntry.getGroup();

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST", null,
			() ->
				displayPageTemplateFolderResource.
					deleteDesignLibraryDisplayPageTemplateFolder(
						group.getExternalReferenceCode(),
						RandomTestUtil.randomString()));
	}

	private void _testDeleteDesignLibraryDisplayPageTemplateFolderWithSiteExternalReferenceCodeProblemException()
		throws Exception {

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST", null,
			() ->
				displayPageTemplateFolderResource.
					deleteDesignLibraryDisplayPageTemplateFolder(
						testGroup.getExternalReferenceCode(),
						RandomTestUtil.randomString()));
	}

	private void _testPatchSiteDisplayPageTemplateFolder(
			String displayPageTemplateFolderExternalReferenceCode,
			String parentDisplayPageTemplateFolderExternalReferenceCode)
		throws Exception {

		DisplayPageTemplateFolder getDisplayPageTemplateFolder =
			displayPageTemplateFolderResource.getSiteDisplayPageTemplateFolder(
				testGroup.getExternalReferenceCode(),
				displayPageTemplateFolderExternalReferenceCode);

		DisplayPageTemplateFolder randomDisplayPageTemplateFolder =
			randomDisplayPageTemplateFolder();

		randomDisplayPageTemplateFolder.setExternalReferenceCode(
			displayPageTemplateFolderExternalReferenceCode);
		randomDisplayPageTemplateFolder.
			setParentDisplayPageTemplateFolderExternalReferenceCode(
				parentDisplayPageTemplateFolderExternalReferenceCode);

		DisplayPageTemplateFolder patchDisplayPageTemplateFolder =
			displayPageTemplateFolderResource.
				patchSiteDisplayPageTemplateFolder(
					testGroup.getExternalReferenceCode(),
					displayPageTemplateFolderExternalReferenceCode,
					randomDisplayPageTemplateFolder);

		assertEquals(
			randomDisplayPageTemplateFolder, patchDisplayPageTemplateFolder);
		assertValid(patchDisplayPageTemplateFolder);

		if (parentDisplayPageTemplateFolderExternalReferenceCode == null) {
			parentDisplayPageTemplateFolderExternalReferenceCode =
				getDisplayPageTemplateFolder.
					getParentDisplayPageTemplateFolderExternalReferenceCode();
		}

		if (Validator.isBlank(
				parentDisplayPageTemplateFolderExternalReferenceCode)) {

			_assertNoParentDisplayPageTemplateFolder(
				patchDisplayPageTemplateFolder);
		}
		else {
			_assertParentDisplayPageTemplateFolder(
				_layoutPageTemplateCollectionService.
					getLayoutPageTemplateCollection(
						patchDisplayPageTemplateFolder.
							getExternalReferenceCode(),
						testGroup.getGroupId()),
				ListUtil.fromArray(
					_layoutPageTemplateCollectionService.
						getLayoutPageTemplateCollection(
							parentDisplayPageTemplateFolderExternalReferenceCode,
							testGroup.getGroupId())));
		}
	}

	private void _testPostSiteDisplayPageTemplateFolderWithExistingParentExternalReferenceCode()
		throws Exception {

		DisplayPageTemplateFolder displayPageTemplateFolder =
			randomDisplayPageTemplateFolder();

		displayPageTemplateFolder.setKey(StringPool.BLANK);

		DisplayPageTemplateFolder parentDisplayPageTemplateFolder =
			testPostSiteDisplayPageTemplateFolder_addDisplayPageTemplateFolder(
				displayPageTemplateFolder);

		Assert.assertNotNull(
			Validator.isNotNull(parentDisplayPageTemplateFolder.getKey()));

		DisplayPageTemplateFolder randomDisplayPageTemplateFolder =
			randomDisplayPageTemplateFolder();

		randomDisplayPageTemplateFolder.
			setParentDisplayPageTemplateFolderExternalReferenceCode(
				parentDisplayPageTemplateFolder.getExternalReferenceCode());

		DisplayPageTemplateFolder postDisplayPageTemplateFolder =
			testPostSiteDisplayPageTemplateFolder_addDisplayPageTemplateFolder(
				randomDisplayPageTemplateFolder);

		assertEquals(
			randomDisplayPageTemplateFolder, postDisplayPageTemplateFolder);
		Assert.assertEquals(
			randomDisplayPageTemplateFolder.getKey(),
			postDisplayPageTemplateFolder.getKey());
		Assert.assertEquals(
			randomDisplayPageTemplateFolder.
				getParentDisplayPageTemplateFolderExternalReferenceCode(),
			postDisplayPageTemplateFolder.
				getParentDisplayPageTemplateFolderExternalReferenceCode());
		assertValid(postDisplayPageTemplateFolder);
		_assertParentDisplayPageTemplateFolder(
			postDisplayPageTemplateFolder, parentDisplayPageTemplateFolder);
	}

	private void _testPostSiteDisplayPageTemplateFolderWithInvalidKey(
			String key, String title)
		throws Exception {

		DisplayPageTemplateFolder displayPageTemplateFolder =
			randomDisplayPageTemplateFolder();

		displayPageTemplateFolder.setKey(key);

		ProblemExceptionTestUtil.assertProblemException(
			"CONFLICT", title,
			() ->
				displayPageTemplateFolderResource.
					postSiteDisplayPageTemplateFolder(
						testGroup.getExternalReferenceCode(),
						displayPageTemplateFolder));
	}

	private void
			_testPutDesignLibraryDisplayPageTemplateFolderPermissionsPageWithSiteExternalReferenceCodeProblemException(
				Role role)
		throws Exception {

		DisplayPageTemplateFolder displayPageTemplateFolder =
			testGetSiteDisplayPageTemplateFoldersPage_addDisplayPageTemplateFolder(
				testGroup.getExternalReferenceCode(),
				randomDisplayPageTemplateFolder());

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST", null,
			() ->
				displayPageTemplateFolderResource.
					putDesignLibraryDisplayPageTemplateFolderPermissionsPage(
						testGroup.getExternalReferenceCode(),
						displayPageTemplateFolder.getExternalReferenceCode(),
						new Permission[] {
							new Permission() {
								{
									setActionIds(new String[] {"VIEW"});
									setRoleName(role.getName());
								}
							}
						}));

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				getLayoutPageTemplateCollectionByExternalReferenceCode(
					displayPageTemplateFolder.getExternalReferenceCode(),
					testGroup.getGroupId());

		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				testCompany.getCompanyId(),
				LayoutPageTemplateCollection.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(
					layoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId()),
				role.getRoleId(), ActionKeys.VIEW));
	}

	private DisplayPageTemplateFolder _testPutSiteDisplayPageTemplateFolder(
			DisplayPageTemplateFolder displayPageTemplateFolder,
			String parentDisplayPageTemplateFolderExternalReferenceCode)
		throws Exception {

		displayPageTemplateFolder.
			setParentDisplayPageTemplateFolderExternalReferenceCode(
				parentDisplayPageTemplateFolderExternalReferenceCode);

		if (Validator.isNull(
				parentDisplayPageTemplateFolderExternalReferenceCode)) {

			displayPageTemplateFolder.setParentDisplayPageTemplateFolder(
				() -> null);
		}

		DisplayPageTemplateFolder putDisplayPageTemplateFolder =
			displayPageTemplateFolderResource.putSiteDisplayPageTemplateFolder(
				testGroup.getExternalReferenceCode(),
				displayPageTemplateFolder.getExternalReferenceCode(),
				displayPageTemplateFolder);

		assertEquals(displayPageTemplateFolder, putDisplayPageTemplateFolder);
		assertValid(putDisplayPageTemplateFolder);

		return putDisplayPageTemplateFolder;
	}

	private void _testPutSiteDisplayPageTemplateFolderWithMismatchedParentDisplayPageTemplateFolder()
		throws Exception {

		try (SafeCloseable safeCloseable =
				LazyReferencingTestUtil.setLazyReferencingWithSafeCloseable(
					true)) {

			DisplayPageTemplateFolder displayPageTemplateFolder =
				randomDisplayPageTemplateFolder();

			displayPageTemplateFolder.setParentDisplayPageTemplateFolder(
				randomDisplayPageTemplateFolder());
			displayPageTemplateFolder.
				setParentDisplayPageTemplateFolderExternalReferenceCode(
					RandomTestUtil.randomString());

			ProblemExceptionTestUtil.assertProblemException(
				"BAD_REQUEST",
				"The provided external reference code does not point to the " +
					"parent display page template folder",
				() ->
					displayPageTemplateFolderResource.
						putSiteDisplayPageTemplateFolder(
							testGroup.getExternalReferenceCode(),
							displayPageTemplateFolder.
								getExternalReferenceCode(),
							displayPageTemplateFolder));
		}
	}

	private void _testPutSiteDisplayPageTemplateFolderWithParentDisplayPageTemplateFolder()
		throws Exception {

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST",
			"The parent display page template folder type does not match the " +
				"display page type",
			() -> _testPutSiteDisplayPageTemplateFolder(
				randomDisplayPageTemplateFolder(),
				RandomTestUtil.randomString()));

		DisplayPageTemplateFolder parentDisplayPageTemplateFolder =
			testPostSiteDisplayPageTemplateFolder_addDisplayPageTemplateFolder(
				randomDisplayPageTemplateFolder());

		DisplayPageTemplateFolder displayPageTemplateFolder =
			_testPutSiteDisplayPageTemplateFolder(
				randomDisplayPageTemplateFolder(),
				parentDisplayPageTemplateFolder.getExternalReferenceCode());

		_assertParentDisplayPageTemplateFolder(
			displayPageTemplateFolder, parentDisplayPageTemplateFolder);

		DisplayPageTemplateFolder putDisplayPageTemplateFolder =
			_testPutSiteDisplayPageTemplateFolder(
				displayPageTemplateFolder, StringPool.BLANK);

		_assertNoParentDisplayPageTemplateFolder(putDisplayPageTemplateFolder);

		parentDisplayPageTemplateFolder = _getParentDisplayPageTemplateFolder(
			5);

		putDisplayPageTemplateFolder.setParentDisplayPageTemplateFolder(
			parentDisplayPageTemplateFolder);
		putDisplayPageTemplateFolder.
			setParentDisplayPageTemplateFolderExternalReferenceCode(
				parentDisplayPageTemplateFolder.getExternalReferenceCode());

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST",
			"The parent display page template folder type does not match the " +
				"display page type",
			() ->
				displayPageTemplateFolderResource.
					putSiteDisplayPageTemplateFolder(
						testGroup.getExternalReferenceCode(),
						putDisplayPageTemplateFolder.getExternalReferenceCode(),
						putDisplayPageTemplateFolder));

		try (SafeCloseable safeCloseable =
				LazyReferencingTestUtil.setLazyReferencingWithSafeCloseable(
					true)) {

			displayPageTemplateFolderResource.putSiteDisplayPageTemplateFolder(
				testGroup.getExternalReferenceCode(),
				putDisplayPageTemplateFolder.getExternalReferenceCode(),
				putDisplayPageTemplateFolder);

			List<LayoutPageTemplateCollection>
				parentLayoutPageTemplateCollections = new ArrayList<>();

			while (parentDisplayPageTemplateFolder != null) {
				parentLayoutPageTemplateCollections.add(
					_layoutPageTemplateCollectionService.
						getLayoutPageTemplateCollection(
							parentDisplayPageTemplateFolder.
								getExternalReferenceCode(),
							testGroup.getGroupId()));

				parentDisplayPageTemplateFolder =
					parentDisplayPageTemplateFolder.
						getParentDisplayPageTemplateFolder();
			}

			_assertParentDisplayPageTemplateFolder(
				_layoutPageTemplateCollectionService.
					getLayoutPageTemplateCollection(
						putDisplayPageTemplateFolder.getExternalReferenceCode(),
						testGroup.getGroupId()),
				parentLayoutPageTemplateCollections);
		}
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
	private LayoutPageTemplateCollectionService
		_layoutPageTemplateCollectionService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private StagingLocalService _stagingLocalService;

}