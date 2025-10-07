/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.taxonomy.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.service.AssetVocabularyGroupRelLocalService;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.headless.admin.taxonomy.client.dto.v1_0.AssetLibrary;
import com.liferay.headless.admin.taxonomy.client.dto.v1_0.AssetType;
import com.liferay.headless.admin.taxonomy.client.dto.v1_0.TaxonomyVocabulary;
import com.liferay.headless.admin.taxonomy.client.pagination.Page;
import com.liferay.headless.admin.taxonomy.client.pagination.Pagination;
import com.liferay.headless.admin.taxonomy.client.problem.Problem;
import com.liferay.headless.admin.taxonomy.client.resource.v1_0.TaxonomyVocabularyResource;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;

import java.util.List;
import java.util.Map;

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

		assertValid(
			page,
			HashMapBuilder.<String, Map<String, String>>put(
				"create",
				HashMapBuilder.put(
					"href",
					StringBundler.concat(
						"http://localhost:8080/o/headless-admin-taxonomy/v1.0",
						"/asset-libraries/",
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
						"http://localhost:8080/o/headless-admin-taxonomy/v1.0",
						"/asset-libraries/",
						testGetAssetLibraryTaxonomyVocabulariesPage_getAssetLibraryId(),
						"/taxonomy-vocabularies/batch")
				).put(
					"method", "POST"
				).build()
			).put(
				"deleteBatch",
				HashMapBuilder.put(
					"href",
					"http://localhost:8080/o/headless-admin-taxonomy/v1.0" +
						"/taxonomy-vocabularies/batch"
				).put(
					"method", "DELETE"
				).build()
			).put(
				"updateBatch",
				HashMapBuilder.put(
					"href",
					"http://localhost:8080/o/headless-admin-taxonomy/v1.0" +
						"/taxonomy-vocabularies/batch"
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

	@FeatureFlag("LPD-17564")
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
		_testGetTaxonomyVocabularyWithoutPermissionsAction();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"assetTypes", "description", "multiValued", "name", "visibilityType"
		};
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {"dateCreated", "dateModified"};
	}

	@Override
	protected TaxonomyVocabulary randomTaxonomyVocabulary() throws Exception {
		return new TaxonomyVocabulary() {
			{
				assetLibraries =
					testGroup.isCMS() ?
						new AssetLibrary[] {_randomAssetLibrary()} : null;
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

		// These tests require the instance to be created with the feature
		// flag LPD-17564 enabled. On CI, feature flags are enabled on
		// demand for each test, but not during instance initialization.
		// Until the feature flag LPD-17564 is removed, we need an explicit CMS
		// group creation.

		Role role = _roleLocalService.fetchRole(
			testDepotEntryGroup.getCompanyId(), RoleConstants.SITE_MEMBER);

		if (role == null) {
			_roleLocalService.addRole(
				null, TestPropsValues.getUserId(), null, 0,
				RoleConstants.SITE_MEMBER, null, null,
				RoleConstants.TYPE_REGULAR, null, null);
		}

		irrelevantGroup = GroupTestUtil.addGroup(
			testDepotEntryGroup.getCompanyId(), TestPropsValues.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID, GroupConstants.CMS);
		testGroup = GroupTestUtil.addGroup(
			testDepotEntryGroup.getCompanyId(), TestPropsValues.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID, GroupConstants.CMS);
	}

	private AssetLibrary _randomAssetLibrary() throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(), null,
			DepotConstants.TYPE_ASSET_LIBRARY,
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

		assertValid(
			page,
			HashMapBuilder.<String, Map<String, String>>put(
				"create",
				HashMapBuilder.put(
					"href",
					StringBundler.concat(
						"http://localhost:8080/o/headless-admin-taxonomy/v1.0",
						"/sites/",
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
						"http://localhost:8080/o/headless-admin-taxonomy/v1.0",
						"/sites/",
						testGetSiteTaxonomyVocabulariesPage_getSiteId(),
						"/taxonomy-vocabularies/batch")
				).put(
					"method", "POST"
				).build()
			).put(
				"deleteBatch",
				HashMapBuilder.put(
					"href",
					"http://localhost:8080/o/headless-admin-taxonomy/v1.0" +
						"/taxonomy-vocabularies/batch"
				).put(
					"method", "DELETE"
				).build()
			).put(
				"updateBatch",
				HashMapBuilder.put(
					"href",
					"http://localhost:8080/o/headless-admin-taxonomy/v1.0" +
						"/taxonomy-vocabularies/batch"
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

		assertValid(
			getTaxonomyVocabulary.getActions(),
			HashMapBuilder.<String, Map<String, String>>put(
				"delete",
				HashMapBuilder.put(
					"href",
					"http://localhost:8080/o/headless-admin-taxonomy/v1.0" +
						"/taxonomy-vocabularies/" +
							getTaxonomyVocabulary.getId()
				).put(
					"method", "DELETE"
				).build()
			).put(
				"get",
				HashMapBuilder.put(
					"href",
					"http://localhost:8080/o/headless-admin-taxonomy/v1.0" +
						"/taxonomy-vocabularies/" +
							getTaxonomyVocabulary.getId()
				).put(
					"method", "GET"
				).build()
			).put(
				"replace",
				HashMapBuilder.put(
					"href",
					"http://localhost:8080/o/headless-admin-taxonomy/v1.0" +
						"/taxonomy-vocabularies/" +
							getTaxonomyVocabulary.getId()
				).put(
					"method", "PUT"
				).build()
			).put(
				"update",
				HashMapBuilder.put(
					"href",
					"http://localhost:8080/o/headless-admin-taxonomy/v1.0" +
						"/taxonomy-vocabularies/" +
							getTaxonomyVocabulary.getId()
				).put(
					"method", "PATCH"
				).build()
			).build());
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
				testCompany.getVirtualHostname(), 8080, "http"
			).locale(
				LocaleUtil.getDefault()
			).build();

		TaxonomyVocabulary getTaxonomyVocabulary =
			siteMemberTaxonomyVocabularyResource.getTaxonomyVocabulary(
				postTaxonomyVocabulary.getId());

		Assert.assertNull(getTaxonomyVocabulary.getPermissions());
	}

	@Inject
	private AssetVocabularyGroupRelLocalService
		_assetVocabularyGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}