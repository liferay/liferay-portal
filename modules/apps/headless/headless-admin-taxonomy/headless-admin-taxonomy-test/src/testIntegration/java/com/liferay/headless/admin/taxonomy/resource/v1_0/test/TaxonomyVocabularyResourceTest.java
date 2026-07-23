/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.taxonomy.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.kernel.service.AssetVocabularyGroupRelLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.service.DLFileEntryTypeService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.headless.admin.taxonomy.client.dto.v1_0.AssetLibrary;
import com.liferay.headless.admin.taxonomy.client.dto.v1_0.AssetType;
import com.liferay.headless.admin.taxonomy.client.dto.v1_0.Project;
import com.liferay.headless.admin.taxonomy.client.dto.v1_0.TaxonomyVocabulary;
import com.liferay.headless.admin.taxonomy.client.pagination.Page;
import com.liferay.headless.admin.taxonomy.client.pagination.Pagination;
import com.liferay.headless.admin.taxonomy.client.problem.Problem;
import com.liferay.headless.admin.taxonomy.client.resource.v1_0.TaxonomyVocabularyResource;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class TaxonomyVocabularyResourceTest
	extends BaseTaxonomyVocabularyResourceTestCase {

	@Override
	@Test
	public void testDeleteAssetLibraryTaxonomyVocabularyByExternalReferenceCode()
		throws Exception {

		super.testDeleteAssetLibraryTaxonomyVocabularyByExternalReferenceCode();

		testDeleteAssetLibraryTaxonomyVocabularyByExternalReferenceCode_addTaxonomyVocabulary();

		String externalReferenceCode = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		try {
			taxonomyVocabularyResource.
				deleteAssetLibraryTaxonomyVocabularyByExternalReferenceCode(
					testDeleteAssetLibraryTaxonomyVocabularyByExternalReferenceCode_getAssetLibraryId(),
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
	public void testDeleteTaxonomyVocabulary() throws Exception {
		super.testDeleteTaxonomyVocabulary();

		TaxonomyVocabulary randomTaxonomyVocabulary =
			randomTaxonomyVocabulary();

		randomTaxonomyVocabulary.setSystem(true);

		TaxonomyVocabulary postTaxonomyVocabulary =
			taxonomyVocabularyResource.postSiteTaxonomyVocabulary(
				testGroup.getGroupId(), randomTaxonomyVocabulary);

		Assert.assertTrue(postTaxonomyVocabulary.getSystem());

		try {
			taxonomyVocabularyResource.deleteTaxonomyVocabulary(
				postTaxonomyVocabulary.getId());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("FORBIDDEN", problem.getStatus());
		}
	}

	@Override
	@Test
	public void testGetAssetLibraryTaxonomyVocabulariesPage() throws Exception {
		super.testGetAssetLibraryTaxonomyVocabulariesPage();

		Page<TaxonomyVocabulary> page =
			taxonomyVocabularyResource.getAssetLibraryTaxonomyVocabulariesPage(
				testGetAssetLibraryTaxonomyVocabulariesPage_getAssetLibraryId(),
				null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		TaxonomyVocabulary taxonomyVocabulary =
			testGetAssetLibraryTaxonomyVocabulariesPage_addTaxonomyVocabulary(
				testGetAssetLibraryTaxonomyVocabulariesPage_getAssetLibraryId(),
				randomTaxonomyVocabulary());

		page =
			taxonomyVocabularyResource.getAssetLibraryTaxonomyVocabulariesPage(
				testGetAssetLibraryTaxonomyVocabulariesPage_getAssetLibraryId(),
				null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 1, page.getTotalCount());

		String href = StringBundler.concat(
			"http://localhost:", PortalUtil.getPortalServerPort(false),
			"/o/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/batch");

		assertValid(
			page,
			HashMapBuilder.<String, Map<String, String>>put(
				"create",
				HashMapBuilder.put(
					"href",
					StringBundler.concat(
						"http://localhost:",
						PortalUtil.getPortalServerPort(false),
						"/o/headless-admin-taxonomy/v1.0/asset-libraries/",
						testGetAssetLibraryTaxonomyVocabulariesPage_getAssetLibraryId(),
						"/taxonomy-vocabularies")
				).put(
					"method", "POST"
				).build()
			).put(
				"createBatch",
				HashMapBuilder.put(
					"href",
					StringBundler.concat(
						"http://localhost:",
						PortalUtil.getPortalServerPort(false),
						"/o/headless-admin-taxonomy/v1.0/asset-libraries/",
						testGetAssetLibraryTaxonomyVocabulariesPage_getAssetLibraryId(),
						"/taxonomy-vocabularies/batch")
				).put(
					"method", "POST"
				).build()
			).put(
				"deleteBatch",
				HashMapBuilder.put(
					"href", href
				).put(
					"method", "DELETE"
				).build()
			).put(
				"updateBatch",
				HashMapBuilder.put(
					"href", href
				).put(
					"method", "PUT"
				).build()
			).build());

		taxonomyVocabularyResource = TaxonomyVocabularyResource.builder(
		).authentication(
			"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
		).locale(
			LocaleUtil.getDefault()
		).parameters(
			"restrictFields",
			StringBundler.concat(
				"actions,assetLibraries,assetLibraryKey,",
				"assetTypes,availableLanguages,creator,dateCreated,",
				"dateModified,description,externalReferenceCode,id,",
				"multiValued,numberOfTaxonomyCategories,visibilityType")
		).build();

		page =
			taxonomyVocabularyResource.getAssetLibraryTaxonomyVocabulariesPage(
				testDepotEntry.getDepotEntryId(), null, null, null,
				Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 1, page.getTotalCount());

		assertContains(
			new TaxonomyVocabulary() {
				{
					name = taxonomyVocabulary.getName();
				}
			},
			(List<TaxonomyVocabulary>)page.getItems());

		assertValid(page);
	}

	@Override
	@Test
	public void testGetAssetLibraryTaxonomyVocabularyByExternalReferenceCode()
		throws Exception {

		super.testGetAssetLibraryTaxonomyVocabularyByExternalReferenceCode();

		String externalReferenceCode = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		try {
			taxonomyVocabularyResource.
				getAssetLibraryTaxonomyVocabularyByExternalReferenceCode(
					testGetAssetLibraryTaxonomyVocabularyByExternalReferenceCode_getAssetLibraryId(),
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
	public void testGetSiteTaxonomyVocabulariesPage() throws Exception {
		super.testGetSiteTaxonomyVocabulariesPage();

		_testGetSiteTaxonomyVocabulariesPage();

		Group originalIrrelevantGroup = irrelevantGroup;
		Group originalTestGroup = testGroup;

		_addCMSGroup();

		super.testGetSiteTaxonomyVocabulariesPage();

		_testGetSiteTaxonomyVocabulariesPage();

		irrelevantGroup = originalIrrelevantGroup;
		testGroup = originalTestGroup;
	}

	@Override
	@Test
	public void testGetTaxonomyVocabulary() throws Exception {
		super.testGetTaxonomyVocabulary();

		_testGetTaxonomyVocabularyActions();
		_testGetTaxonomyVocabularyWithoutClassTypePK();
		_testGetTaxonomyVocabularyWithoutPermissionsAction();
	}

	@Override
	@Test
	public void testPatchTaxonomyVocabulary() throws Exception {
		super.testPatchTaxonomyVocabulary();

		TaxonomyVocabulary randomTaxonomyVocabulary =
			randomTaxonomyVocabulary();

		randomTaxonomyVocabulary.setSystem(true);

		TaxonomyVocabulary postTaxonomyVocabulary =
			taxonomyVocabularyResource.postSiteTaxonomyVocabulary(
				testGroup.getGroupId(), randomTaxonomyVocabulary);

		postTaxonomyVocabulary.setName(RandomTestUtil.randomString());

		try {
			taxonomyVocabularyResource.patchTaxonomyVocabulary(
				postTaxonomyVocabulary.getId(), postTaxonomyVocabulary);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("FORBIDDEN", problem.getStatus());
		}
	}

	@Test
	public void testPostSiteTaxonomyVocabulary() throws Exception {
		super.testPostSiteTaxonomyVocabulary();

		_testPostSiteTaxonomyVocabulary();
		_testPostSiteTaxonomyVocabularyInvalidAssetTypeType();
		_testPostSiteTaxonomyVocabularyInvalidAssetTypeSubtype();
	}

	@Test
	public void testPostSiteTaxonomyVocabularySystemWithoutAssetTypes()
		throws Exception {

		TaxonomyVocabulary randomTaxonomyVocabulary =
			randomTaxonomyVocabulary();

		randomTaxonomyVocabulary.setAssetTypes(new AssetType[0]);
		randomTaxonomyVocabulary.setSystem(true);

		TaxonomyVocabulary postTaxonomyVocabulary =
			taxonomyVocabularyResource.postSiteTaxonomyVocabulary(
				testGroup.getGroupId(), randomTaxonomyVocabulary);

		Assert.assertTrue(postTaxonomyVocabulary.getSystem());
	}

	@Override
	@Test
	@TestInfo("LPD-83785")
	public void testPutSiteTaxonomyVocabularyByExternalReferenceCode()
		throws Exception {

		super.testPutSiteTaxonomyVocabularyByExternalReferenceCode();

		_testPutSiteTaxonomyVocabularyByExternalReferenceCodeExternalReferenceCode();
		_testPutSiteTaxonomyVocabularyByExternalReferenceCodeWithNonexistentAssetLibrary();
	}

	@Override
	@Test
	public void testPutTaxonomyVocabulary() throws Exception {
		super.testPutTaxonomyVocabulary();

		Group originalIrrelevantGroup = irrelevantGroup;
		Group originalTestGroup = testGroup;

		_addCMSGroup();

		try {
			_testPutTaxonomyVocabularyResetsProjectScope();
			_testPutTaxonomyVocabularyResetsSpaceScope();
		}
		finally {
			irrelevantGroup = originalIrrelevantGroup;
			testGroup = originalTestGroup;
		}

		_testPutTaxonomyVocabularyUpdatesEmptyVocabulary();
		_testPutTaxonomyVocabularyWithoutDescription();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"assetTypes", "description", "multiValued", "name",
			"numberOfTaxonomyCategories", "visibilityType"
		};
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {
			"assetLibraries", "dateCreated", "dateModified", "projects",
			"siteId", "visibilityType"
		};
	}

	@Override
	protected TaxonomyVocabulary randomTaxonomyVocabulary() throws Exception {
		return new TaxonomyVocabulary() {
			{
				assetLibraries = testGroup.isCMS() ?
					new AssetLibrary[] {_randomSpaceAssetLibrary()} : null;
				assetTypes = new AssetType[] {
					new AssetType() {
						{
							required = RandomTestUtil.randomBoolean();
							subtype = "AllAssetSubtypes";
							type = "AllAssetTypes";
							typeId = 0L;
						}
					}
				};
				description = RandomTestUtil.randomString();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				multiValued = RandomTestUtil.randomBoolean();
				name = RandomTestUtil.randomString();
				numberOfTaxonomyCategories = 0;
				projects = testGroup.isCMS() ?
					new Project[] {_randomProjectAssetLibrary()} : null;
				siteId = testGroup.getGroupId();
				visibilityType = VisibilityType.PUBLIC;
			}
		};
	}

	@Override
	protected Long testGetSiteTaxonomyVocabulariesPage_getIrrelevantSiteId()
		throws Exception {

		if (irrelevantGroup.isCMS()) {
			return null;
		}

		return irrelevantGroup.getGroupId();
	}

	@Override
	protected TaxonomyVocabulary
			testGraphQLGetAssetLibraryTaxonomyVocabularyByExternalReferenceCode_addTaxonomyVocabulary()
		throws Exception {

		return testGetAssetLibraryTaxonomyVocabularyByExternalReferenceCode_addTaxonomyVocabulary();
	}

	private void _addCMSGroup() throws Exception {

		// These tests require an explicit CMS group creation.

		Role role = _roleLocalService.fetchRole(
			testDepotEntryGroup.getCompanyId(), RoleConstants.SITE_MEMBER);

		if (role == null) {
			_roleLocalService.addRole(
				null, TestPropsValues.getUserId(), null, 0,
				RoleConstants.SITE_MEMBER, null, null,
				RoleConstants.TYPE_REGULAR, null, null);
		}

		irrelevantGroup = GroupTestUtil.getOrAddCMSGroup(
			testDepotEntryGroup.getCompanyId());

		testGroup = irrelevantGroup;
	}

	private <T> void _assertSingletonArrayEquals(
		T[] array, long expected, Function<T, Long> function) {

		Assert.assertEquals(Arrays.toString(array), 1, array.length);
		Assert.assertEquals(Long.valueOf(expected), function.apply(array[0]));
	}

	private Project _randomProjectAssetLibrary() throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(), null,
			DepotConstants.TYPE_PROJECT,
			ServiceContextTestUtil.getServiceContext());

		Group depotEntryGroup = depotEntry.getGroup();

		return new Project() {
			{
				id = depotEntryGroup.getGroupId();
				name = depotEntryGroup.getName(LocaleUtil.getDefault());
			}
		};
	}

	private AssetLibrary _randomSpaceAssetLibrary() throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(), null,
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());

		Group depotEntryGroup = depotEntry.getGroup();

		return new AssetLibrary() {
			{
				id = depotEntryGroup.getGroupId();
				name = depotEntryGroup.getName(LocaleUtil.getDefault());
			}
		};
	}

	private void _testGetSiteTaxonomyVocabulariesPage() throws Exception {
		Page<TaxonomyVocabulary> page =
			taxonomyVocabularyResource.getSiteTaxonomyVocabulariesPage(
				testGetSiteTaxonomyVocabulariesPage_getSiteId(), null, null,
				null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		testGetSiteTaxonomyVocabulariesPage_addTaxonomyVocabulary(
			testGetSiteTaxonomyVocabulariesPage_getSiteId(),
			randomTaxonomyVocabulary());

		page = taxonomyVocabularyResource.getSiteTaxonomyVocabulariesPage(
			testGetSiteTaxonomyVocabulariesPage_getSiteId(), null, null, null,
			Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 1, page.getTotalCount());

		String href = StringBundler.concat(
			"http://localhost:", PortalUtil.getPortalServerPort(false),
			"/o/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/batch");

		assertValid(
			page,
			HashMapBuilder.<String, Map<String, String>>put(
				"create",
				HashMapBuilder.put(
					"href",
					StringBundler.concat(
						"http://localhost:",
						PortalUtil.getPortalServerPort(false),
						"/o/headless-admin-taxonomy/v1.0/sites/",
						testGetSiteTaxonomyVocabulariesPage_getSiteId(),
						"/taxonomy-vocabularies")
				).put(
					"method", "POST"
				).build()
			).put(
				"createBatch",
				HashMapBuilder.put(
					"href",
					StringBundler.concat(
						"http://localhost:",
						PortalUtil.getPortalServerPort(false),
						"/o/headless-admin-taxonomy/v1.0/sites/",
						testGetSiteTaxonomyVocabulariesPage_getSiteId(),
						"/taxonomy-vocabularies/batch")
				).put(
					"method", "POST"
				).build()
			).put(
				"deleteBatch",
				HashMapBuilder.put(
					"href", href
				).put(
					"method", "DELETE"
				).build()
			).put(
				"updateBatch",
				HashMapBuilder.put(
					"href", href
				).put(
					"method", "PUT"
				).build()
			).build());
	}

	private void _testGetTaxonomyVocabularyActions() throws Exception {
		TaxonomyVocabulary postTaxonomyVocabulary =
			testGetTaxonomyVocabulary_addTaxonomyVocabulary();

		TaxonomyVocabulary getTaxonomyVocabulary =
			taxonomyVocabularyResource.getTaxonomyVocabulary(
				postTaxonomyVocabulary.getId());

		String href = StringBundler.concat(
			"http://localhost:", PortalUtil.getPortalServerPort(false),
			"/o/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/",
			getTaxonomyVocabulary.getId());

		assertValid(
			getTaxonomyVocabulary.getActions(),
			HashMapBuilder.<String, Map<String, String>>put(
				"delete",
				HashMapBuilder.put(
					"href", href
				).put(
					"method", "DELETE"
				).build()
			).put(
				"get",
				HashMapBuilder.put(
					"href", href
				).put(
					"method", "GET"
				).build()
			).put(
				"replace",
				HashMapBuilder.put(
					"href", href
				).put(
					"method", "PUT"
				).build()
			).put(
				"update",
				HashMapBuilder.put(
					"href", href
				).put(
					"method", "PATCH"
				).build()
			).build());
	}

	private void _testGetTaxonomyVocabularyWithoutClassTypePK()
		throws Exception {

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			testGroup.getGroupId(), DLFileEntryMetadata.class.getName());

		DLFileEntryType dlFileEntryType =
			_dlFileEntryTypeService.addFileEntryType(
				null, testGroup.getGroupId(), ddmStructure.getStructureId(),
				null,
				Collections.singletonMap(LocaleUtil.US, "New File Entry Type"),
				Collections.singletonMap(LocaleUtil.US, "New File Entry Type"),
				ServiceContextTestUtil.getServiceContext(
					testGroup, TestPropsValues.getUserId()));

		long classNameId = _classNameLocalService.getClassNameId(
			DLFileEntryConstants.getClassName());

		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.create(
			true
		).put(
			"selectedClassNameIds",
			classNameId + StringPool.COLON +
				dlFileEntryType.getFileEntryTypeId()
		).build();

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), testGroup.getGroupId(),
				RandomTestUtil.randomString(),
				Collections.singletonMap(
					LocaleUtil.getSiteDefault(), RandomTestUtil.randomString()),
				null, unicodeProperties.toString(),
				ServiceContextTestUtil.getServiceContext(
					testGroup.getGroupId()));

		_dlFileEntryTypeService.deleteFileEntryType(
			dlFileEntryType.getFileEntryTypeId());

		TaxonomyVocabulary taxonomyVocabulary =
			taxonomyVocabularyResource.getTaxonomyVocabulary(
				assetVocabulary.getVocabularyId());

		Assert.assertNotNull(taxonomyVocabulary);

		AssetType[] assetTypes = taxonomyVocabulary.getAssetTypes();

		Assert.assertEquals(classNameId, (long)assetTypes[0].getTypeId());
		Assert.assertNull(assetTypes[0].getSubtype());
	}

	private void _testGetTaxonomyVocabularyWithoutPermissionsAction()
		throws Exception {

		TaxonomyVocabulary randomTaxonomyVocabulary =
			randomTaxonomyVocabulary();

		randomTaxonomyVocabulary.setViewableBy(
			TaxonomyVocabulary.ViewableBy.MEMBERS);

		TaxonomyVocabulary postTaxonomyVocabulary =
			taxonomyVocabularyResource.postSiteTaxonomyVocabulary(
				testGroup.getGroupId(), randomTaxonomyVocabulary);

		User siteMemberUser = UserTestUtil.addGroupUser(
			testGroup, RoleConstants.SITE_MEMBER);

		String password = RandomTestUtil.randomString();

		_userLocalService.updatePassword(
			siteMemberUser.getUserId(), password, password, false, true);

		TaxonomyVocabularyResource siteMemberTaxonomyVocabularyResource =
			TaxonomyVocabularyResource.builder(
			).authentication(
				siteMemberUser.getEmailAddress(), password
			).endpoint(
				testCompany.getVirtualHostname(),
				PortalUtil.getPortalServerPort(false), "http"
			).locale(
				LocaleUtil.getDefault()
			).build();

		TaxonomyVocabulary getTaxonomyVocabulary =
			siteMemberTaxonomyVocabularyResource.getTaxonomyVocabulary(
				postTaxonomyVocabulary.getId());

		Assert.assertNull(getTaxonomyVocabulary.getPermissions());
	}

	private void _testPostSiteTaxonomyVocabulary() throws Exception {
		TaxonomyVocabulary randomTaxonomyVocabulary =
			randomTaxonomyVocabulary();

		randomTaxonomyVocabulary.setVisibilityType(
			TaxonomyVocabulary.VisibilityType.EMPTY);

		TaxonomyVocabulary postTaxonomyVocabulary =
			testPostSiteTaxonomyVocabulary_addTaxonomyVocabulary(
				randomTaxonomyVocabulary);

		assertEquals(randomTaxonomyVocabulary, postTaxonomyVocabulary);
		assertValid(postTaxonomyVocabulary);
	}

	private void _testPostSiteTaxonomyVocabularyInvalidAssetTypeSubtype()
		throws Exception {

		String randomSubtype = RandomTestUtil.randomString();

		TaxonomyVocabulary randomTaxonomyVocabulary =
			randomTaxonomyVocabulary();

		randomTaxonomyVocabulary.setAssetTypes(
			new AssetType[] {
				new AssetType() {
					{
						required = true;
						subtype = randomSubtype;
						type = "StructuredContent";
					}
				}
			});

		TaxonomyVocabulary postTaxonomyVocabulary = null;

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_LOG_NAME, LoggerTestUtil.DEBUG)) {

			postTaxonomyVocabulary =
				testPostSiteTaxonomyVocabulary_addTaxonomyVocabulary(
					randomTaxonomyVocabulary);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Invalid asset type subtype " + randomSubtype,
				logEntry.getMessage());
		}

		AssetType[] assetTypes = postTaxonomyVocabulary.getAssetTypes();

		Assert.assertEquals(assetTypes.toString(), 1, assetTypes.length);

		AssetType assetType = assetTypes[0];

		Assert.assertEquals("AllAssetSubtypes", assetType.getSubtype());
		Assert.assertEquals("StructuredContent", assetType.getType());
		Assert.assertFalse(assetType.getRequired());
	}

	private void _testPostSiteTaxonomyVocabularyInvalidAssetTypeType()
		throws Exception {

		String randomType = RandomTestUtil.randomString();

		TaxonomyVocabulary randomTaxonomyVocabulary =
			randomTaxonomyVocabulary();

		randomTaxonomyVocabulary.setAssetTypes(
			new AssetType[] {
				new AssetType() {
					{
						required = true;
						subtype = RandomTestUtil.randomString();
						type = randomType;
					}
				}
			});

		TaxonomyVocabulary postTaxonomyVocabulary = null;

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_LOG_NAME, LoggerTestUtil.DEBUG)) {

			postTaxonomyVocabulary =
				testPostSiteTaxonomyVocabulary_addTaxonomyVocabulary(
					randomTaxonomyVocabulary);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Invalid asset type type " + randomType, logEntry.getMessage());
		}

		AssetType[] assetTypes = postTaxonomyVocabulary.getAssetTypes();

		Assert.assertEquals(assetTypes.toString(), 1, assetTypes.length);

		AssetType assetType = assetTypes[0];

		Assert.assertEquals("AllAssetSubtypes", assetType.getSubtype());
		Assert.assertEquals("AllAssetTypes", assetType.getType());
		Assert.assertFalse(assetType.getRequired());
	}

	private void _testPutSiteTaxonomyVocabularyByExternalReferenceCodeExternalReferenceCode()
		throws Exception {

		TaxonomyVocabulary postTaxonomyVocabulary =
			testPutSiteTaxonomyVocabularyByExternalReferenceCode_addTaxonomyVocabulary();

		TaxonomyVocabulary randomTaxonomyVocabulary =
			randomTaxonomyVocabulary();

		randomTaxonomyVocabulary.setExternalReferenceCode(() -> null);

		TaxonomyVocabulary putTaxonomyVocabulary =
			taxonomyVocabularyResource.
				putSiteTaxonomyVocabularyByExternalReferenceCode(
					postTaxonomyVocabulary.getSiteId(),
					postTaxonomyVocabulary.getExternalReferenceCode(),
					randomTaxonomyVocabulary);

		Assert.assertEquals(
			postTaxonomyVocabulary.getExternalReferenceCode(),
			putTaxonomyVocabulary.getExternalReferenceCode());

		String externalReferenceCode = RandomTestUtil.randomString();

		randomTaxonomyVocabulary.setExternalReferenceCode(
			externalReferenceCode);

		putTaxonomyVocabulary =
			taxonomyVocabularyResource.
				putSiteTaxonomyVocabularyByExternalReferenceCode(
					postTaxonomyVocabulary.getSiteId(),
					postTaxonomyVocabulary.getExternalReferenceCode(),
					randomTaxonomyVocabulary);

		Assert.assertEquals(
			externalReferenceCode,
			putTaxonomyVocabulary.getExternalReferenceCode());
	}

	private void _testPutSiteTaxonomyVocabularyByExternalReferenceCodeWithNonexistentAssetLibrary()
		throws Exception {

		// See LPD-83785

		long nonexistentAssetLibraryId = RandomTestUtil.randomLong();

		TaxonomyVocabulary taxonomyVocabulary = new TaxonomyVocabulary() {
			{
				assetLibraries = new AssetLibrary[] {
					new AssetLibrary() {
						{
							id = nonexistentAssetLibraryId;
						}
					}
				};
				assetTypes = new AssetType[] {
					new AssetType() {
						{
							required = false;
							subtype = "AllAssetSubtypes";
							type = "AllAssetTypes";
							typeId = 0L;
						}
					}
				};
				description = RandomTestUtil.randomString();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				name = RandomTestUtil.randomString();
				siteId = testGroup.getGroupId();
				visibilityType = VisibilityType.PUBLIC;
			}
		};

		TaxonomyVocabulary putTaxonomyVocabulary =
			taxonomyVocabularyResource.
				putSiteTaxonomyVocabularyByExternalReferenceCode(
					testGroup.getGroupId(),
					taxonomyVocabulary.getExternalReferenceCode(),
					taxonomyVocabulary);

		Assert.assertTrue(
			ArrayUtil.isEmpty(putTaxonomyVocabulary.getAssetLibraries()));
	}

	private void _testPutTaxonomyVocabularyResetsProjectScope()
		throws Exception {

		Project project = _randomProjectAssetLibrary();

		TaxonomyVocabulary randomTaxonomyVocabulary =
			randomTaxonomyVocabulary();

		randomTaxonomyVocabulary.setProjects(new Project[] {project});

		TaxonomyVocabulary postTaxonomyVocabulary =
			taxonomyVocabularyResource.postSiteTaxonomyVocabulary(
				testGroup.getGroupId(), randomTaxonomyVocabulary);

		_assertSingletonArrayEquals(
			postTaxonomyVocabulary.getProjects(), project.getId(),
			Project::getId);

		postTaxonomyVocabulary.setProjects(new Project[0]);

		TaxonomyVocabulary putTaxonomyVocabulary =
			taxonomyVocabularyResource.putTaxonomyVocabulary(
				postTaxonomyVocabulary.getId(), postTaxonomyVocabulary);

		_assertSingletonArrayEquals(
			putTaxonomyVocabulary.getProjects(), GroupConstants.GROUP_ID_ALL,
			Project::getId);
	}

	private void _testPutTaxonomyVocabularyResetsSpaceScope() throws Exception {
		AssetLibrary assetLibrary = _randomSpaceAssetLibrary();

		TaxonomyVocabulary randomTaxonomyVocabulary =
			randomTaxonomyVocabulary();

		randomTaxonomyVocabulary.setAssetLibraries(
			new AssetLibrary[] {assetLibrary});

		TaxonomyVocabulary postTaxonomyVocabulary =
			taxonomyVocabularyResource.postSiteTaxonomyVocabulary(
				testGroup.getGroupId(), randomTaxonomyVocabulary);

		_assertSingletonArrayEquals(
			postTaxonomyVocabulary.getAssetLibraries(), assetLibrary.getId(),
			AssetLibrary::getId);

		postTaxonomyVocabulary.setAssetLibraries(new AssetLibrary[0]);

		TaxonomyVocabulary putTaxonomyVocabulary =
			taxonomyVocabularyResource.putTaxonomyVocabulary(
				postTaxonomyVocabulary.getId(), postTaxonomyVocabulary);

		_assertSingletonArrayEquals(
			putTaxonomyVocabulary.getAssetLibraries(),
			GroupConstants.GROUP_ID_ALL, AssetLibrary::getId);
	}

	private void _testPutTaxonomyVocabularyUpdatesEmptyVocabulary()
		throws Exception {

		AssetVocabulary assetVocabulary = null;

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			assetVocabulary =
				_assetVocabularyLocalService.getOrAddEmptyVocabulary(
					RandomTestUtil.randomString(), TestPropsValues.getUserId(),
					testGroup.getGroupId());
		}

		TaxonomyVocabulary updateTaxonomyVocabulary = new TaxonomyVocabulary() {
			{
				multiValued = false;
				name = RandomTestUtil.randomString();
				visibilityType = VisibilityType.PUBLIC;
			}
		};

		TaxonomyVocabulary updatedTaxonomyVocabulary =
			taxonomyVocabularyResource.
				putSiteTaxonomyVocabularyByExternalReferenceCode(
					assetVocabulary.getGroupId(),
					assetVocabulary.getExternalReferenceCode(),
					updateTaxonomyVocabulary);

		Assert.assertEquals(
			TaxonomyVocabulary.VisibilityType.PUBLIC,
			updatedTaxonomyVocabulary.getVisibilityType());

		assetVocabulary =
			_assetVocabularyLocalService.
				getAssetVocabularyByExternalReferenceCode(
					assetVocabulary.getExternalReferenceCode(),
					assetVocabulary.getGroupId());

		Assert.assertEquals(
			AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC,
			assetVocabulary.getVisibilityType());
	}

	private void _testPutTaxonomyVocabularyWithoutDescription()
		throws Exception {

		TaxonomyVocabulary randomTaxonomyVocabulary =
			randomTaxonomyVocabulary();

		randomTaxonomyVocabulary.setDescription((String)null);

		TaxonomyVocabulary postTaxonomyVocabulary =
			taxonomyVocabularyResource.postSiteTaxonomyVocabulary(
				testGroup.getGroupId(), randomTaxonomyVocabulary);

		Assert.assertTrue(
			Validator.isNull(postTaxonomyVocabulary.getDescription()));
		Assert.assertNull(postTaxonomyVocabulary.getDescription_i18n());

		postTaxonomyVocabulary.setDescription(StringPool.BLANK);

		TaxonomyVocabulary putTaxonomyVocabulary =
			taxonomyVocabularyResource.putTaxonomyVocabulary(
				postTaxonomyVocabulary.getId(), postTaxonomyVocabulary);

		Assert.assertTrue(
			Validator.isNull(putTaxonomyVocabulary.getDescription()));
		Assert.assertNull(putTaxonomyVocabulary.getDescription_i18n());
	}

	private static final String _LOG_NAME =
		"com.liferay.headless.admin.taxonomy.internal.resource.v1_0." +
			"TaxonomyVocabularyResourceImpl";

	@Inject
	private AssetVocabularyGroupRelLocalService
		_assetVocabularyGroupRelLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private DLFileEntryTypeService _dlFileEntryTypeService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}