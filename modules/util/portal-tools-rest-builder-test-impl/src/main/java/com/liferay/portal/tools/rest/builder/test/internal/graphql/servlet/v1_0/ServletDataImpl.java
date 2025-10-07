/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.internal.graphql.servlet.v1_0;

import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.tools.rest.builder.test.internal.graphql.mutation.v1_0.Mutation;
import com.liferay.portal.tools.rest.builder.test.internal.graphql.query.v1_0.Query;
import com.liferay.portal.tools.rest.builder.test.internal.resource.v1_0.AssetLibraryTestEntityResourceImpl;
import com.liferay.portal.tools.rest.builder.test.internal.resource.v1_0.BatchTestEntityResourceImpl;
import com.liferay.portal.tools.rest.builder.test.internal.resource.v1_0.CompanyTestEntityResourceImpl;
import com.liferay.portal.tools.rest.builder.test.internal.resource.v1_0.ERCAssetLibraryTestEntityResourceImpl;
import com.liferay.portal.tools.rest.builder.test.internal.resource.v1_0.ERCScopedTestEntityResourceImpl;
import com.liferay.portal.tools.rest.builder.test.internal.resource.v1_0.ERCSiteTestEntityResourceImpl;
import com.liferay.portal.tools.rest.builder.test.internal.resource.v1_0.EntityModelResourceTestEntity1ResourceImpl;
import com.liferay.portal.tools.rest.builder.test.internal.resource.v1_0.EntityModelResourceTestEntity2ResourceImpl;
import com.liferay.portal.tools.rest.builder.test.internal.resource.v1_0.FilterResourceImpl;
import com.liferay.portal.tools.rest.builder.test.internal.resource.v1_0.MultipartTestEntityResourceImpl;
import com.liferay.portal.tools.rest.builder.test.internal.resource.v1_0.SchemaResourceImpl;
import com.liferay.portal.tools.rest.builder.test.internal.resource.v1_0.ScopedTestEntityResourceImpl;
import com.liferay.portal.tools.rest.builder.test.internal.resource.v1_0.SiteTestEntityResourceImpl;
import com.liferay.portal.tools.rest.builder.test.internal.resource.v1_0.SortResourceImpl;
import com.liferay.portal.tools.rest.builder.test.internal.resource.v1_0.TestEntityAddressResourceImpl;
import com.liferay.portal.tools.rest.builder.test.internal.resource.v1_0.TestEntityResourceImpl;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.AssetLibraryTestEntityResource;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.BatchTestEntityResource;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.CompanyTestEntityResource;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.ERCAssetLibraryTestEntityResource;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.ERCScopedTestEntityResource;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.ERCSiteTestEntityResource;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.EntityModelResourceTestEntity1Resource;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.EntityModelResourceTestEntity2Resource;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.FilterResource;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.MultipartTestEntityResource;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.SchemaResource;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.ScopedTestEntityResource;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.SiteTestEntityResource;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.SortResource;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.TestEntityAddressResource;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.TestEntityResource;
import com.liferay.portal.vulcan.graphql.servlet.ServletData;

import jakarta.annotation.Generated;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Component(service = ServletData.class)
@Generated("")
public class ServletDataImpl implements ServletData {

	@Activate
	public void activate(BundleContext bundleContext) {
		Mutation.setAssetLibraryTestEntityResourceComponentServiceObjects(
			_assetLibraryTestEntityResourceComponentServiceObjects);
		Mutation.setBatchTestEntityResourceComponentServiceObjects(
			_batchTestEntityResourceComponentServiceObjects);
		Mutation.setCompanyTestEntityResourceComponentServiceObjects(
			_companyTestEntityResourceComponentServiceObjects);
		Mutation.setERCAssetLibraryTestEntityResourceComponentServiceObjects(
			_ercAssetLibraryTestEntityResourceComponentServiceObjects);
		Mutation.setERCScopedTestEntityResourceComponentServiceObjects(
			_ercScopedTestEntityResourceComponentServiceObjects);
		Mutation.setERCSiteTestEntityResourceComponentServiceObjects(
			_ercSiteTestEntityResourceComponentServiceObjects);
		Mutation.setFilterResourceComponentServiceObjects(
			_filterResourceComponentServiceObjects);
		Mutation.setMultipartTestEntityResourceComponentServiceObjects(
			_multipartTestEntityResourceComponentServiceObjects);
		Mutation.setSchemaResourceComponentServiceObjects(
			_schemaResourceComponentServiceObjects);
		Mutation.setScopedTestEntityResourceComponentServiceObjects(
			_scopedTestEntityResourceComponentServiceObjects);
		Mutation.setSiteTestEntityResourceComponentServiceObjects(
			_siteTestEntityResourceComponentServiceObjects);
		Mutation.setSortResourceComponentServiceObjects(
			_sortResourceComponentServiceObjects);
		Mutation.setTestEntityResourceComponentServiceObjects(
			_testEntityResourceComponentServiceObjects);

		Query.setAssetLibraryTestEntityResourceComponentServiceObjects(
			_assetLibraryTestEntityResourceComponentServiceObjects);
		Query.setBatchTestEntityResourceComponentServiceObjects(
			_batchTestEntityResourceComponentServiceObjects);
		Query.setCompanyTestEntityResourceComponentServiceObjects(
			_companyTestEntityResourceComponentServiceObjects);
		Query.setERCAssetLibraryTestEntityResourceComponentServiceObjects(
			_ercAssetLibraryTestEntityResourceComponentServiceObjects);
		Query.setERCScopedTestEntityResourceComponentServiceObjects(
			_ercScopedTestEntityResourceComponentServiceObjects);
		Query.setERCSiteTestEntityResourceComponentServiceObjects(
			_ercSiteTestEntityResourceComponentServiceObjects);
		Query.setEntityModelResourceTestEntity1ResourceComponentServiceObjects(
			_entityModelResourceTestEntity1ResourceComponentServiceObjects);
		Query.setEntityModelResourceTestEntity2ResourceComponentServiceObjects(
			_entityModelResourceTestEntity2ResourceComponentServiceObjects);
		Query.setFilterResourceComponentServiceObjects(
			_filterResourceComponentServiceObjects);
		Query.setMultipartTestEntityResourceComponentServiceObjects(
			_multipartTestEntityResourceComponentServiceObjects);
		Query.setSchemaResourceComponentServiceObjects(
			_schemaResourceComponentServiceObjects);
		Query.setScopedTestEntityResourceComponentServiceObjects(
			_scopedTestEntityResourceComponentServiceObjects);
		Query.setSiteTestEntityResourceComponentServiceObjects(
			_siteTestEntityResourceComponentServiceObjects);
		Query.setSortResourceComponentServiceObjects(
			_sortResourceComponentServiceObjects);
		Query.setTestEntityResourceComponentServiceObjects(
			_testEntityResourceComponentServiceObjects);
		Query.setTestEntityAddressResourceComponentServiceObjects(
			_testEntityAddressResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "Liferay.Portal.Tools.REST.Builder.Test";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/test-graphql/v1_0";
	}

	@Override
	public Query getQuery() {
		return new Query();
	}

	public ObjectValuePair<Class<?>, String> getResourceMethodObjectValuePair(
		String methodName, boolean mutation) {

		if (mutation) {
			return _resourceMethodObjectValuePairs.get(
				"mutation#" + methodName);
		}

		return _resourceMethodObjectValuePairs.get("query#" + methodName);
	}

	private static final Map<String, ObjectValuePair<Class<?>, String>>
		_resourceMethodObjectValuePairs =
			new HashMap<String, ObjectValuePair<Class<?>, String>>() {
				{
					put(
						"mutation#deleteAssetLibraryAssetLibraryTestEntityByExternalReferenceCode",
						new ObjectValuePair<>(
							AssetLibraryTestEntityResourceImpl.class,
							"deleteAssetLibraryAssetLibraryTestEntityByExternalReferenceCode"));
					put(
						"mutation#createAssetLibraryAssetLibraryTestEntitiesPageExportBatch",
						new ObjectValuePair<>(
							AssetLibraryTestEntityResourceImpl.class,
							"postAssetLibraryAssetLibraryTestEntitiesPageExportBatch"));
					put(
						"mutation#deleteBatchTestEntityByExternalReferenceCode",
						new ObjectValuePair<>(
							BatchTestEntityResourceImpl.class,
							"deleteBatchTestEntityByExternalReferenceCode"));
					put(
						"mutation#createBatchTestEntitiesPageExportBatch",
						new ObjectValuePair<>(
							BatchTestEntityResourceImpl.class,
							"postBatchTestEntitiesPageExportBatch"));
					put(
						"mutation#createBatchTestEntity",
						new ObjectValuePair<>(
							BatchTestEntityResourceImpl.class,
							"postBatchTestEntity"));
					put(
						"mutation#createBatchTestEntityBatch",
						new ObjectValuePair<>(
							BatchTestEntityResourceImpl.class,
							"postBatchTestEntityBatch"));
					put(
						"mutation#updateBatchTestEntityByExternalReferenceCode",
						new ObjectValuePair<>(
							BatchTestEntityResourceImpl.class,
							"putBatchTestEntityByExternalReferenceCode"));
					put(
						"mutation#deleteCompanyTestEntityByExternalReferenceCode",
						new ObjectValuePair<>(
							CompanyTestEntityResourceImpl.class,
							"deleteCompanyTestEntityByExternalReferenceCode"));
					put(
						"mutation#patchCompanyTestEntity",
						new ObjectValuePair<>(
							CompanyTestEntityResourceImpl.class,
							"patchCompanyTestEntity"));
					put(
						"mutation#createCompanyTestEntitiesPageExportBatch",
						new ObjectValuePair<>(
							CompanyTestEntityResourceImpl.class,
							"postCompanyTestEntitiesPageExportBatch"));
					put(
						"mutation#createCompanyTestEntity",
						new ObjectValuePair<>(
							CompanyTestEntityResourceImpl.class,
							"postCompanyTestEntity"));
					put(
						"mutation#createCompanyTestEntityBatch",
						new ObjectValuePair<>(
							CompanyTestEntityResourceImpl.class,
							"postCompanyTestEntityBatch"));
					put(
						"mutation#updateCompanyTestEntity",
						new ObjectValuePair<>(
							CompanyTestEntityResourceImpl.class,
							"putCompanyTestEntity"));
					put(
						"mutation#updateCompanyTestEntityBatch",
						new ObjectValuePair<>(
							CompanyTestEntityResourceImpl.class,
							"putCompanyTestEntityBatch"));
					put(
						"mutation#updateCompanyTestEntityByExternalReferenceCode",
						new ObjectValuePair<>(
							CompanyTestEntityResourceImpl.class,
							"putCompanyTestEntityByExternalReferenceCode"));
					put(
						"mutation#updateCompanyTestEntityPermissionsPage",
						new ObjectValuePair<>(
							CompanyTestEntityResourceImpl.class,
							"putCompanyTestEntityPermissionsPage"));
					put(
						"mutation#deleteAssetLibraryERCAssetLibraryTestEntity",
						new ObjectValuePair<>(
							ERCAssetLibraryTestEntityResourceImpl.class,
							"deleteAssetLibraryERCAssetLibraryTestEntity"));
					put(
						"mutation#createAssetLibraryERCAssetLibraryTestEntitiesPageExportBatch",
						new ObjectValuePair<>(
							ERCAssetLibraryTestEntityResourceImpl.class,
							"postAssetLibraryERCAssetLibraryTestEntitiesPageExportBatch"));
					put(
						"mutation#createAssetLibraryERCAssetLibraryTestEntity",
						new ObjectValuePair<>(
							ERCAssetLibraryTestEntityResourceImpl.class,
							"postAssetLibraryERCAssetLibraryTestEntity"));
					put(
						"mutation#createAssetLibraryERCAssetLibraryTestEntityBatch",
						new ObjectValuePair<>(
							ERCAssetLibraryTestEntityResourceImpl.class,
							"postAssetLibraryERCAssetLibraryTestEntityBatch"));
					put(
						"mutation#updateAssetLibraryERCAssetLibraryTestEntity",
						new ObjectValuePair<>(
							ERCAssetLibraryTestEntityResourceImpl.class,
							"putAssetLibraryERCAssetLibraryTestEntity"));
					put(
						"mutation#updateAssetLibraryERCAssetLibraryTestEntityPermissionsPage",
						new ObjectValuePair<>(
							ERCAssetLibraryTestEntityResourceImpl.class,
							"putAssetLibraryERCAssetLibraryTestEntityPermissionsPage"));
					put(
						"mutation#deleteAssetLibraryERCScopedTestEntity",
						new ObjectValuePair<>(
							ERCScopedTestEntityResourceImpl.class,
							"deleteAssetLibraryERCScopedTestEntity"));
					put(
						"mutation#deleteSiteERCScopedTestEntity",
						new ObjectValuePair<>(
							ERCScopedTestEntityResourceImpl.class,
							"deleteSiteERCScopedTestEntity"));
					put(
						"mutation#createAssetLibraryERCScopedTestEntitiesPageExportBatch",
						new ObjectValuePair<>(
							ERCScopedTestEntityResourceImpl.class,
							"postAssetLibraryERCScopedTestEntitiesPageExportBatch"));
					put(
						"mutation#createAssetLibraryERCScopedTestEntity",
						new ObjectValuePair<>(
							ERCScopedTestEntityResourceImpl.class,
							"postAssetLibraryERCScopedTestEntity"));
					put(
						"mutation#createAssetLibraryERCScopedTestEntityBatch",
						new ObjectValuePair<>(
							ERCScopedTestEntityResourceImpl.class,
							"postAssetLibraryERCScopedTestEntityBatch"));
					put(
						"mutation#createSiteERCScopedTestEntitiesPageExportBatch",
						new ObjectValuePair<>(
							ERCScopedTestEntityResourceImpl.class,
							"postSiteERCScopedTestEntitiesPageExportBatch"));
					put(
						"mutation#createSiteERCScopedTestEntity",
						new ObjectValuePair<>(
							ERCScopedTestEntityResourceImpl.class,
							"postSiteERCScopedTestEntity"));
					put(
						"mutation#createSiteERCScopedTestEntityBatch",
						new ObjectValuePair<>(
							ERCScopedTestEntityResourceImpl.class,
							"postSiteERCScopedTestEntityBatch"));
					put(
						"mutation#updateAssetLibraryERCScopedTestEntity",
						new ObjectValuePair<>(
							ERCScopedTestEntityResourceImpl.class,
							"putAssetLibraryERCScopedTestEntity"));
					put(
						"mutation#updateSiteERCScopedTestEntity",
						new ObjectValuePair<>(
							ERCScopedTestEntityResourceImpl.class,
							"putSiteERCScopedTestEntity"));
					put(
						"mutation#deleteSiteERCSiteTestEntity",
						new ObjectValuePair<>(
							ERCSiteTestEntityResourceImpl.class,
							"deleteSiteERCSiteTestEntity"));
					put(
						"mutation#createSiteERCSiteTestEntitiesPageExportBatch",
						new ObjectValuePair<>(
							ERCSiteTestEntityResourceImpl.class,
							"postSiteERCSiteTestEntitiesPageExportBatch"));
					put(
						"mutation#createSiteERCSiteTestEntity",
						new ObjectValuePair<>(
							ERCSiteTestEntityResourceImpl.class,
							"postSiteERCSiteTestEntity"));
					put(
						"mutation#createSiteERCSiteTestEntityBatch",
						new ObjectValuePair<>(
							ERCSiteTestEntityResourceImpl.class,
							"postSiteERCSiteTestEntityBatch"));
					put(
						"mutation#updateSiteERCSiteTestEntity",
						new ObjectValuePair<>(
							ERCSiteTestEntityResourceImpl.class,
							"putSiteERCSiteTestEntity"));
					put(
						"mutation#updateSiteERCSiteTestEntityPermissionsPage",
						new ObjectValuePair<>(
							ERCSiteTestEntityResourceImpl.class,
							"putSiteERCSiteTestEntityPermissionsPage"));
					put(
						"mutation#createFiltersPageExportBatch",
						new ObjectValuePair<>(
							FilterResourceImpl.class,
							"postFiltersPageExportBatch"));
					put(
						"mutation#patchMultipartTestEntity",
						new ObjectValuePair<>(
							MultipartTestEntityResourceImpl.class,
							"patchMultipartTestEntity"));
					put(
						"mutation#createMultipartTestEntity",
						new ObjectValuePair<>(
							MultipartTestEntityResourceImpl.class,
							"postMultipartTestEntity"));
					put(
						"mutation#createMultipartTestEntity",
						new ObjectValuePair<>(
							MultipartTestEntityResourceImpl.class,
							"postMultipartTestEntity"));
					put(
						"mutation#createMultipartTestEntityBatch",
						new ObjectValuePair<>(
							MultipartTestEntityResourceImpl.class,
							"postMultipartTestEntityBatch"));
					put(
						"mutation#updateMultipartTestEntity",
						new ObjectValuePair<>(
							MultipartTestEntityResourceImpl.class,
							"putMultipartTestEntity"));
					put(
						"mutation#updateMultipartTestEntityBatch",
						new ObjectValuePair<>(
							MultipartTestEntityResourceImpl.class,
							"putMultipartTestEntityBatch"));
					put(
						"mutation#createSchemasPageExportBatch",
						new ObjectValuePair<>(
							SchemaResourceImpl.class,
							"postSchemasPageExportBatch"));
					put(
						"mutation#deleteAssetLibraryScopedTestEntityByExternalReferenceCode",
						new ObjectValuePair<>(
							ScopedTestEntityResourceImpl.class,
							"deleteAssetLibraryScopedTestEntityByExternalReferenceCode"));
					put(
						"mutation#deleteSiteScopedTestEntityByExternalReferenceCode",
						new ObjectValuePair<>(
							ScopedTestEntityResourceImpl.class,
							"deleteSiteScopedTestEntityByExternalReferenceCode"));
					put(
						"mutation#patchAssetLibraryScopedTestEntityByExternalReferenceCode",
						new ObjectValuePair<>(
							ScopedTestEntityResourceImpl.class,
							"patchAssetLibraryScopedTestEntityByExternalReferenceCode"));
					put(
						"mutation#patchSiteScopedTestEntityByExternalReferenceCode",
						new ObjectValuePair<>(
							ScopedTestEntityResourceImpl.class,
							"patchSiteScopedTestEntityByExternalReferenceCode"));
					put(
						"mutation#createAssetLibraryScopedTestEntitiesPageExportBatch",
						new ObjectValuePair<>(
							ScopedTestEntityResourceImpl.class,
							"postAssetLibraryScopedTestEntitiesPageExportBatch"));
					put(
						"mutation#createAssetLibraryScopedTestEntity",
						new ObjectValuePair<>(
							ScopedTestEntityResourceImpl.class,
							"postAssetLibraryScopedTestEntity"));
					put(
						"mutation#createAssetLibraryScopedTestEntityBatch",
						new ObjectValuePair<>(
							ScopedTestEntityResourceImpl.class,
							"postAssetLibraryScopedTestEntityBatch"));
					put(
						"mutation#createSiteScopedTestEntitiesPageExportBatch",
						new ObjectValuePair<>(
							ScopedTestEntityResourceImpl.class,
							"postSiteScopedTestEntitiesPageExportBatch"));
					put(
						"mutation#createSiteScopedTestEntity",
						new ObjectValuePair<>(
							ScopedTestEntityResourceImpl.class,
							"postSiteScopedTestEntity"));
					put(
						"mutation#createSiteScopedTestEntityBatch",
						new ObjectValuePair<>(
							ScopedTestEntityResourceImpl.class,
							"postSiteScopedTestEntityBatch"));
					put(
						"mutation#updateAssetLibraryScopedTestEntityByExternalReferenceCode",
						new ObjectValuePair<>(
							ScopedTestEntityResourceImpl.class,
							"putAssetLibraryScopedTestEntityByExternalReferenceCode"));
					put(
						"mutation#updateSiteScopedTestEntityByExternalReferenceCode",
						new ObjectValuePair<>(
							ScopedTestEntityResourceImpl.class,
							"putSiteScopedTestEntityByExternalReferenceCode"));
					put(
						"mutation#deleteSiteSiteTestEntityByExternalReferenceCode",
						new ObjectValuePair<>(
							SiteTestEntityResourceImpl.class,
							"deleteSiteSiteTestEntityByExternalReferenceCode"));
					put(
						"mutation#patchSiteTestEntity",
						new ObjectValuePair<>(
							SiteTestEntityResourceImpl.class,
							"patchSiteTestEntity"));
					put(
						"mutation#createSiteSiteTestEntitiesPageExportBatch",
						new ObjectValuePair<>(
							SiteTestEntityResourceImpl.class,
							"postSiteSiteTestEntitiesPageExportBatch"));
					put(
						"mutation#createSiteSiteTestEntity",
						new ObjectValuePair<>(
							SiteTestEntityResourceImpl.class,
							"postSiteSiteTestEntity"));
					put(
						"mutation#createSiteSiteTestEntityBatch",
						new ObjectValuePair<>(
							SiteTestEntityResourceImpl.class,
							"postSiteSiteTestEntityBatch"));
					put(
						"mutation#updateSiteSiteTestEntityByExternalReferenceCode",
						new ObjectValuePair<>(
							SiteTestEntityResourceImpl.class,
							"putSiteSiteTestEntityByExternalReferenceCode"));
					put(
						"mutation#updateSiteTestEntity",
						new ObjectValuePair<>(
							SiteTestEntityResourceImpl.class,
							"putSiteTestEntity"));
					put(
						"mutation#updateSiteTestEntityBatch",
						new ObjectValuePair<>(
							SiteTestEntityResourceImpl.class,
							"putSiteTestEntityBatch"));
					put(
						"mutation#updateSiteTestEntityPermissionsPage",
						new ObjectValuePair<>(
							SiteTestEntityResourceImpl.class,
							"putSiteTestEntityPermissionsPage"));
					put(
						"mutation#createSortsPageExportBatch",
						new ObjectValuePair<>(
							SortResourceImpl.class,
							"postSortsPageExportBatch"));
					put(
						"mutation#deleteTestEntity",
						new ObjectValuePair<>(
							TestEntityResourceImpl.class, "deleteTestEntity"));
					put(
						"mutation#deleteTestEntityBatch",
						new ObjectValuePair<>(
							TestEntityResourceImpl.class,
							"deleteTestEntityBatch"));
					put(
						"mutation#patchTestEntity",
						new ObjectValuePair<>(
							TestEntityResourceImpl.class, "patchTestEntity"));
					put(
						"mutation#createReservedWord",
						new ObjectValuePair<>(
							TestEntityResourceImpl.class, "postReservedWord"));
					put(
						"mutation#createTestEntitiesPageExportBatch",
						new ObjectValuePair<>(
							TestEntityResourceImpl.class,
							"postTestEntitiesPageExportBatch"));
					put(
						"mutation#createTestEntity",
						new ObjectValuePair<>(
							TestEntityResourceImpl.class, "postTestEntity"));
					put(
						"mutation#createTestEntityBatch",
						new ObjectValuePair<>(
							TestEntityResourceImpl.class,
							"postTestEntityBatch"));
					put(
						"mutation#createTestEntityMultipartBulk",
						new ObjectValuePair<>(
							TestEntityResourceImpl.class,
							"postTestEntityMultipartBulk"));
					put(
						"mutation#updateTestEntity",
						new ObjectValuePair<>(
							TestEntityResourceImpl.class, "putTestEntity"));
					put(
						"mutation#updateTestEntityBatch",
						new ObjectValuePair<>(
							TestEntityResourceImpl.class,
							"putTestEntityBatch"));
					put(
						"mutation#updateTestEntityStatus",
						new ObjectValuePair<>(
							TestEntityResourceImpl.class,
							"putTestEntityStatus"));

					put(
						"query#assetLibraryAssetLibraryTestEntities",
						new ObjectValuePair<>(
							AssetLibraryTestEntityResourceImpl.class,
							"getAssetLibraryAssetLibraryTestEntitiesPage"));
					put(
						"query#batchTestEntities",
						new ObjectValuePair<>(
							BatchTestEntityResourceImpl.class,
							"getBatchTestEntitiesPage"));
					put(
						"query#batchTestEntity",
						new ObjectValuePair<>(
							BatchTestEntityResourceImpl.class,
							"getBatchTestEntity"));
					put(
						"query#batchTestEntityByExternalReferenceCode",
						new ObjectValuePair<>(
							BatchTestEntityResourceImpl.class,
							"getBatchTestEntityByExternalReferenceCode"));
					put(
						"query#companyTestEntities",
						new ObjectValuePair<>(
							CompanyTestEntityResourceImpl.class,
							"getCompanyTestEntitiesPage"));
					put(
						"query#companyTestEntity",
						new ObjectValuePair<>(
							CompanyTestEntityResourceImpl.class,
							"getCompanyTestEntity"));
					put(
						"query#companyTestEntityByExternalReferenceCode",
						new ObjectValuePair<>(
							CompanyTestEntityResourceImpl.class,
							"getCompanyTestEntityByExternalReferenceCode"));
					put(
						"query#companyTestEntityPermissions",
						new ObjectValuePair<>(
							CompanyTestEntityResourceImpl.class,
							"getCompanyTestEntityPermissionsPage"));
					put(
						"query#assetLibraryERCAssetLibraryTestEntities",
						new ObjectValuePair<>(
							ERCAssetLibraryTestEntityResourceImpl.class,
							"getAssetLibraryERCAssetLibraryTestEntitiesPage"));
					put(
						"query#assetLibraryERCAssetLibraryTestEntity",
						new ObjectValuePair<>(
							ERCAssetLibraryTestEntityResourceImpl.class,
							"getAssetLibraryERCAssetLibraryTestEntity"));
					put(
						"query#assetLibraryERCAssetLibraryTestEntityPermissions",
						new ObjectValuePair<>(
							ERCAssetLibraryTestEntityResourceImpl.class,
							"getAssetLibraryERCAssetLibraryTestEntityPermissionsPage"));
					put(
						"query#assetLibraryERCScopedTestEntities",
						new ObjectValuePair<>(
							ERCScopedTestEntityResourceImpl.class,
							"getAssetLibraryERCScopedTestEntitiesPage"));
					put(
						"query#assetLibraryERCScopedTestEntity",
						new ObjectValuePair<>(
							ERCScopedTestEntityResourceImpl.class,
							"getAssetLibraryERCScopedTestEntity"));
					put(
						"query#eRCScopedTestEntities",
						new ObjectValuePair<>(
							ERCScopedTestEntityResourceImpl.class,
							"getSiteERCScopedTestEntitiesPage"));
					put(
						"query#eRCScopedTestEntity",
						new ObjectValuePair<>(
							ERCScopedTestEntityResourceImpl.class,
							"getSiteERCScopedTestEntity"));
					put(
						"query#eRCSiteTestEntities",
						new ObjectValuePair<>(
							ERCSiteTestEntityResourceImpl.class,
							"getSiteERCSiteTestEntitiesPage"));
					put(
						"query#eRCSiteTestEntity",
						new ObjectValuePair<>(
							ERCSiteTestEntityResourceImpl.class,
							"getSiteERCSiteTestEntity"));
					put(
						"query#eRCSiteTestEntityPermissions",
						new ObjectValuePair<>(
							ERCSiteTestEntityResourceImpl.class,
							"getSiteERCSiteTestEntityPermissionsPage"));
					put(
						"query#entityModelResourceTestEntities1",
						new ObjectValuePair<>(
							EntityModelResourceTestEntity1ResourceImpl.class,
							"getEntityModelResourceTestEntities1Page"));
					put(
						"query#entityModelResourceTestEntities2EntityModelResourceTestEntity2",
						new ObjectValuePair<>(
							EntityModelResourceTestEntity2ResourceImpl.class,
							"getEntityModelResourceTestEntities2EntityModelResourceTestEntity2"));
					put(
						"query#filters",
						new ObjectValuePair<>(
							FilterResourceImpl.class, "getFiltersPage"));
					put(
						"query#multipartTestEntity",
						new ObjectValuePair<>(
							MultipartTestEntityResourceImpl.class,
							"getMultipartTestEntity"));
					put(
						"query#schemas",
						new ObjectValuePair<>(
							SchemaResourceImpl.class, "getSchemasPage"));
					put(
						"query#assetLibraryScopedTestEntities",
						new ObjectValuePair<>(
							ScopedTestEntityResourceImpl.class,
							"getAssetLibraryScopedTestEntitiesPage"));
					put(
						"query#assetLibraryScopedTestEntityByExternalReferenceCode",
						new ObjectValuePair<>(
							ScopedTestEntityResourceImpl.class,
							"getAssetLibraryScopedTestEntityByExternalReferenceCode"));
					put(
						"query#scopedTestEntities",
						new ObjectValuePair<>(
							ScopedTestEntityResourceImpl.class,
							"getSiteScopedTestEntitiesPage"));
					put(
						"query#scopedTestEntityByExternalReferenceCode",
						new ObjectValuePair<>(
							ScopedTestEntityResourceImpl.class,
							"getSiteScopedTestEntityByExternalReferenceCode"));
					put(
						"query#siteTestEntities",
						new ObjectValuePair<>(
							SiteTestEntityResourceImpl.class,
							"getSiteSiteTestEntitiesPage"));
					put(
						"query#siteTestEntityByExternalReferenceCode",
						new ObjectValuePair<>(
							SiteTestEntityResourceImpl.class,
							"getSiteSiteTestEntityByExternalReferenceCode"));
					put(
						"query#siteTestEntity",
						new ObjectValuePair<>(
							SiteTestEntityResourceImpl.class,
							"getSiteTestEntity"));
					put(
						"query#testEntityPermissions",
						new ObjectValuePair<>(
							SiteTestEntityResourceImpl.class,
							"getSiteTestEntityPermissionsPage"));
					put(
						"query#sorts",
						new ObjectValuePair<>(
							SortResourceImpl.class, "getSortsPage"));
					put(
						"query#testEntities",
						new ObjectValuePair<>(
							TestEntityResourceImpl.class,
							"getTestEntitiesPage"));
					put(
						"query#testEntity",
						new ObjectValuePair<>(
							TestEntityResourceImpl.class, "getTestEntity"));
					put(
						"query#testEntityCount",
						new ObjectValuePair<>(
							TestEntityResourceImpl.class,
							"getTestEntityCount"));
					put(
						"query#testEntityTestEntityAddress",
						new ObjectValuePair<>(
							TestEntityAddressResourceImpl.class,
							"getTestEntityTestEntityAddress"));

					put(
						"query#SiteTestEntity.testEntityPermissions",
						new ObjectValuePair<>(
							SiteTestEntityResourceImpl.class,
							"getSiteTestEntityPermissionsPage"));
					put(
						"query#TestEntity.testEntityAddress",
						new ObjectValuePair<>(
							TestEntityAddressResourceImpl.class,
							"getTestEntityTestEntityAddress"));
					put(
						"query#CompanyTestEntity.batchTestEntityByExternalReferenceCode",
						new ObjectValuePair<>(
							BatchTestEntityResourceImpl.class,
							"getBatchTestEntityByExternalReferenceCode"));
					put(
						"query#BatchTestEntity.companyTestEntityByExternalReferenceCode",
						new ObjectValuePair<>(
							CompanyTestEntityResourceImpl.class,
							"getCompanyTestEntityByExternalReferenceCode"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<AssetLibraryTestEntityResource>
		_assetLibraryTestEntityResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<BatchTestEntityResource>
		_batchTestEntityResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<CompanyTestEntityResource>
		_companyTestEntityResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ERCAssetLibraryTestEntityResource>
		_ercAssetLibraryTestEntityResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ERCScopedTestEntityResource>
		_ercScopedTestEntityResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ERCSiteTestEntityResource>
		_ercSiteTestEntityResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<FilterResource>
		_filterResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<MultipartTestEntityResource>
		_multipartTestEntityResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<SchemaResource>
		_schemaResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ScopedTestEntityResource>
		_scopedTestEntityResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<SiteTestEntityResource>
		_siteTestEntityResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<SortResource>
		_sortResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<TestEntityResource>
		_testEntityResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<EntityModelResourceTestEntity1Resource>
		_entityModelResourceTestEntity1ResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<EntityModelResourceTestEntity2Resource>
		_entityModelResourceTestEntity2ResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<TestEntityAddressResource>
		_testEntityAddressResourceComponentServiceObjects;

}