/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.internal.graphql.mutation.v1_0;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.BatchTestEntity;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.CompanyTestEntity;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.ERCAssetLibraryTestEntity;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.ERCScopedTestEntity;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.ERCSiteTestEntity;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.Filter;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.MultipartTestEntity;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.ScopedTestEntity;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.SiteTestEntity;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.Sort;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.TestEntity;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.AssetLibraryTestEntityResource;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.BatchTestEntityResource;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.CompanyTestEntityResource;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.ERCAssetLibraryTestEntityResource;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.ERCScopedTestEntityResource;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.ERCSiteTestEntityResource;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.FilterResource;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.MultipartTestEntityResource;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.SchemaResource;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.ScopedTestEntityResource;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.SiteTestEntityResource;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.SortResource;
import com.liferay.portal.tools.rest.builder.test.resource.v1_0.TestEntityResource;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.multipart.MultipartBody;
import com.liferay.portal.vulcan.pagination.Page;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.validation.constraints.NotEmpty;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class Mutation {

	public static void setAssetLibraryTestEntityResourceComponentServiceObjects(
		ComponentServiceObjects<AssetLibraryTestEntityResource>
			assetLibraryTestEntityResourceComponentServiceObjects) {

		_assetLibraryTestEntityResourceComponentServiceObjects =
			assetLibraryTestEntityResourceComponentServiceObjects;
	}

	public static void setBatchTestEntityResourceComponentServiceObjects(
		ComponentServiceObjects<BatchTestEntityResource>
			batchTestEntityResourceComponentServiceObjects) {

		_batchTestEntityResourceComponentServiceObjects =
			batchTestEntityResourceComponentServiceObjects;
	}

	public static void setCompanyTestEntityResourceComponentServiceObjects(
		ComponentServiceObjects<CompanyTestEntityResource>
			companyTestEntityResourceComponentServiceObjects) {

		_companyTestEntityResourceComponentServiceObjects =
			companyTestEntityResourceComponentServiceObjects;
	}

	public static void
		setERCAssetLibraryTestEntityResourceComponentServiceObjects(
			ComponentServiceObjects<ERCAssetLibraryTestEntityResource>
				ercAssetLibraryTestEntityResourceComponentServiceObjects) {

		_ercAssetLibraryTestEntityResourceComponentServiceObjects =
			ercAssetLibraryTestEntityResourceComponentServiceObjects;
	}

	public static void setERCScopedTestEntityResourceComponentServiceObjects(
		ComponentServiceObjects<ERCScopedTestEntityResource>
			ercScopedTestEntityResourceComponentServiceObjects) {

		_ercScopedTestEntityResourceComponentServiceObjects =
			ercScopedTestEntityResourceComponentServiceObjects;
	}

	public static void setERCSiteTestEntityResourceComponentServiceObjects(
		ComponentServiceObjects<ERCSiteTestEntityResource>
			ercSiteTestEntityResourceComponentServiceObjects) {

		_ercSiteTestEntityResourceComponentServiceObjects =
			ercSiteTestEntityResourceComponentServiceObjects;
	}

	public static void setFilterResourceComponentServiceObjects(
		ComponentServiceObjects<FilterResource>
			filterResourceComponentServiceObjects) {

		_filterResourceComponentServiceObjects =
			filterResourceComponentServiceObjects;
	}

	public static void setMultipartTestEntityResourceComponentServiceObjects(
		ComponentServiceObjects<MultipartTestEntityResource>
			multipartTestEntityResourceComponentServiceObjects) {

		_multipartTestEntityResourceComponentServiceObjects =
			multipartTestEntityResourceComponentServiceObjects;
	}

	public static void setSchemaResourceComponentServiceObjects(
		ComponentServiceObjects<SchemaResource>
			schemaResourceComponentServiceObjects) {

		_schemaResourceComponentServiceObjects =
			schemaResourceComponentServiceObjects;
	}

	public static void setScopedTestEntityResourceComponentServiceObjects(
		ComponentServiceObjects<ScopedTestEntityResource>
			scopedTestEntityResourceComponentServiceObjects) {

		_scopedTestEntityResourceComponentServiceObjects =
			scopedTestEntityResourceComponentServiceObjects;
	}

	public static void setSiteTestEntityResourceComponentServiceObjects(
		ComponentServiceObjects<SiteTestEntityResource>
			siteTestEntityResourceComponentServiceObjects) {

		_siteTestEntityResourceComponentServiceObjects =
			siteTestEntityResourceComponentServiceObjects;
	}

	public static void setSortResourceComponentServiceObjects(
		ComponentServiceObjects<SortResource>
			sortResourceComponentServiceObjects) {

		_sortResourceComponentServiceObjects =
			sortResourceComponentServiceObjects;
	}

	public static void setTestEntityResourceComponentServiceObjects(
		ComponentServiceObjects<TestEntityResource>
			testEntityResourceComponentServiceObjects) {

		_testEntityResourceComponentServiceObjects =
			testEntityResourceComponentServiceObjects;
	}

	@GraphQLField
	public boolean
			deleteAssetLibraryAssetLibraryTestEntityByExternalReferenceCode(
				@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_assetLibraryTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			assetLibraryTestEntityResource ->
				assetLibraryTestEntityResource.
					deleteAssetLibraryAssetLibraryTestEntityByExternalReferenceCode(
						Long.valueOf(assetLibraryId), externalReferenceCode));

		return true;
	}

	@GraphQLField
	public Response createAssetLibraryAssetLibraryTestEntitiesPageExportBatch(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_assetLibraryTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			assetLibraryTestEntityResource ->
				assetLibraryTestEntityResource.
					postAssetLibraryAssetLibraryTestEntitiesPageExportBatch(
						Long.valueOf(assetLibraryId), callbackURL, contentType,
						fieldNames));
	}

	@GraphQLField
	public boolean deleteBatchTestEntityByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_batchTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			batchTestEntityResource ->
				batchTestEntityResource.
					deleteBatchTestEntityByExternalReferenceCode(
						externalReferenceCode));

		return true;
	}

	@GraphQLField
	public Response createBatchTestEntitiesPageExportBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_batchTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			batchTestEntityResource ->
				batchTestEntityResource.postBatchTestEntitiesPageExportBatch(
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public BatchTestEntity createBatchTestEntity(
			@GraphQLName("batchTestEntity") BatchTestEntity batchTestEntity)
		throws Exception {

		return _applyComponentServiceObjects(
			_batchTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			batchTestEntityResource ->
				batchTestEntityResource.postBatchTestEntity(batchTestEntity));
	}

	@GraphQLField
	public Response createBatchTestEntityBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_batchTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			batchTestEntityResource ->
				batchTestEntityResource.postBatchTestEntityBatch(
					callbackURL, object));
	}

	@GraphQLField
	public BatchTestEntity updateBatchTestEntityByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("batchTestEntity") BatchTestEntity batchTestEntity)
		throws Exception {

		return _applyComponentServiceObjects(
			_batchTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			batchTestEntityResource ->
				batchTestEntityResource.
					putBatchTestEntityByExternalReferenceCode(
						externalReferenceCode, batchTestEntity));
	}

	@GraphQLField
	public boolean deleteCompanyTestEntityByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_companyTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			companyTestEntityResource ->
				companyTestEntityResource.
					deleteCompanyTestEntityByExternalReferenceCode(
						externalReferenceCode));

		return true;
	}

	@GraphQLField
	public CompanyTestEntity patchCompanyTestEntity(
			@GraphQLName("companyTestEntityId") Long companyTestEntityId,
			@GraphQLName("companyTestEntity") CompanyTestEntity
				companyTestEntity)
		throws Exception {

		return _applyComponentServiceObjects(
			_companyTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			companyTestEntityResource ->
				companyTestEntityResource.patchCompanyTestEntity(
					companyTestEntityId, companyTestEntity));
	}

	@GraphQLField
	public Response createCompanyTestEntitiesPageExportBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_companyTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			companyTestEntityResource ->
				companyTestEntityResource.
					postCompanyTestEntitiesPageExportBatch(
						callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public CompanyTestEntity createCompanyTestEntity(
			@GraphQLName("companyTestEntity") CompanyTestEntity
				companyTestEntity)
		throws Exception {

		return _applyComponentServiceObjects(
			_companyTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			companyTestEntityResource ->
				companyTestEntityResource.postCompanyTestEntity(
					companyTestEntity));
	}

	@GraphQLField
	public Response createCompanyTestEntityBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_companyTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			companyTestEntityResource ->
				companyTestEntityResource.postCompanyTestEntityBatch(
					callbackURL, object));
	}

	@GraphQLField
	public CompanyTestEntity updateCompanyTestEntity(
			@GraphQLName("companyTestEntityId") Long companyTestEntityId,
			@GraphQLName("companyTestEntity") CompanyTestEntity
				companyTestEntity)
		throws Exception {

		return _applyComponentServiceObjects(
			_companyTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			companyTestEntityResource ->
				companyTestEntityResource.putCompanyTestEntity(
					companyTestEntityId, companyTestEntity));
	}

	@GraphQLField
	public Response updateCompanyTestEntityBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_companyTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			companyTestEntityResource ->
				companyTestEntityResource.putCompanyTestEntityBatch(
					callbackURL, object));
	}

	@GraphQLField
	public CompanyTestEntity updateCompanyTestEntityByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("companyTestEntity") CompanyTestEntity
				companyTestEntity)
		throws Exception {

		return _applyComponentServiceObjects(
			_companyTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			companyTestEntityResource ->
				companyTestEntityResource.
					putCompanyTestEntityByExternalReferenceCode(
						externalReferenceCode, companyTestEntity));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateCompanyTestEntityPermissionsPage(
				@GraphQLName("companyTestEntityId") Long companyTestEntityId,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_companyTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			companyTestEntityResource -> {
				Page paginationPage =
					companyTestEntityResource.
						putCompanyTestEntityPermissionsPage(
							companyTestEntityId, permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public boolean deleteAssetLibraryERCAssetLibraryTestEntity(
			@GraphQLName("assetLibraryExternalReferenceCode") @NotEmpty String
				assetLibraryExternalReferenceCode,
			@GraphQLName("ercAssetLibraryTestEntityExternalReferenceCode")
				String ercAssetLibraryTestEntityExternalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_ercAssetLibraryTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			ercAssetLibraryTestEntityResource ->
				ercAssetLibraryTestEntityResource.
					deleteAssetLibraryERCAssetLibraryTestEntity(
						assetLibraryExternalReferenceCode,
						ercAssetLibraryTestEntityExternalReferenceCode));

		return true;
	}

	@GraphQLField
	public Response
			createAssetLibraryERCAssetLibraryTestEntitiesPageExportBatch(
				@GraphQLName("assetLibraryExternalReferenceCode") @NotEmpty
					String assetLibraryExternalReferenceCode,
				@GraphQLName("callbackURL") String callbackURL,
				@GraphQLName("contentType") String contentType,
				@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_ercAssetLibraryTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			ercAssetLibraryTestEntityResource ->
				ercAssetLibraryTestEntityResource.
					postAssetLibraryERCAssetLibraryTestEntitiesPageExportBatch(
						assetLibraryExternalReferenceCode, callbackURL,
						contentType, fieldNames));
	}

	@GraphQLField
	public ERCAssetLibraryTestEntity
			createAssetLibraryERCAssetLibraryTestEntity(
				@GraphQLName("assetLibraryExternalReferenceCode") @NotEmpty
					String assetLibraryExternalReferenceCode,
				@GraphQLName("ercAssetLibraryTestEntity")
					ERCAssetLibraryTestEntity ercAssetLibraryTestEntity)
		throws Exception {

		return _applyComponentServiceObjects(
			_ercAssetLibraryTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			ercAssetLibraryTestEntityResource ->
				ercAssetLibraryTestEntityResource.
					postAssetLibraryERCAssetLibraryTestEntity(
						assetLibraryExternalReferenceCode,
						ercAssetLibraryTestEntity));
	}

	@GraphQLField
	public Response createAssetLibraryERCAssetLibraryTestEntityBatch(
			@GraphQLName("assetLibraryExternalReferenceCode") @NotEmpty String
				assetLibraryExternalReferenceCode,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_ercAssetLibraryTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			ercAssetLibraryTestEntityResource ->
				ercAssetLibraryTestEntityResource.
					postAssetLibraryERCAssetLibraryTestEntityBatch(
						assetLibraryExternalReferenceCode, callbackURL,
						object));
	}

	@GraphQLField
	public ERCAssetLibraryTestEntity
			updateAssetLibraryERCAssetLibraryTestEntity(
				@GraphQLName("assetLibraryExternalReferenceCode") @NotEmpty
					String assetLibraryExternalReferenceCode,
				@GraphQLName("ercAssetLibraryTestEntityExternalReferenceCode")
					String ercAssetLibraryTestEntityExternalReferenceCode,
				@GraphQLName("ercAssetLibraryTestEntity")
					ERCAssetLibraryTestEntity ercAssetLibraryTestEntity)
		throws Exception {

		return _applyComponentServiceObjects(
			_ercAssetLibraryTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			ercAssetLibraryTestEntityResource ->
				ercAssetLibraryTestEntityResource.
					putAssetLibraryERCAssetLibraryTestEntity(
						assetLibraryExternalReferenceCode,
						ercAssetLibraryTestEntityExternalReferenceCode,
						ercAssetLibraryTestEntity));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateAssetLibraryERCAssetLibraryTestEntityPermissionsPage(
				@GraphQLName("assetLibraryExternalReferenceCode") @NotEmpty
					String assetLibraryExternalReferenceCode,
				@GraphQLName("ercAssetLibraryTestEntityExternalReferenceCode")
					String ercAssetLibraryTestEntityExternalReferenceCode,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_ercAssetLibraryTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			ercAssetLibraryTestEntityResource -> {
				Page paginationPage =
					ercAssetLibraryTestEntityResource.
						putAssetLibraryERCAssetLibraryTestEntityPermissionsPage(
							assetLibraryExternalReferenceCode,
							ercAssetLibraryTestEntityExternalReferenceCode,
							permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public boolean deleteAssetLibraryERCScopedTestEntity(
			@GraphQLName("assetLibraryExternalReferenceCode") @NotEmpty String
				assetLibraryExternalReferenceCode,
			@GraphQLName("ercScopedTestEntityExternalReferenceCode") String
				ercScopedTestEntityExternalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_ercScopedTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			ercScopedTestEntityResource ->
				ercScopedTestEntityResource.
					deleteAssetLibraryERCScopedTestEntity(
						assetLibraryExternalReferenceCode,
						ercScopedTestEntityExternalReferenceCode));

		return true;
	}

	@GraphQLField
	public boolean deleteSiteERCScopedTestEntity(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("ercScopedTestEntityExternalReferenceCode") String
				ercScopedTestEntityExternalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_ercScopedTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			ercScopedTestEntityResource ->
				ercScopedTestEntityResource.deleteSiteERCScopedTestEntity(
					siteExternalReferenceCode,
					ercScopedTestEntityExternalReferenceCode));

		return true;
	}

	@GraphQLField
	public Response createAssetLibraryERCScopedTestEntitiesPageExportBatch(
			@GraphQLName("assetLibraryExternalReferenceCode") @NotEmpty String
				assetLibraryExternalReferenceCode,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_ercScopedTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			ercScopedTestEntityResource ->
				ercScopedTestEntityResource.
					postAssetLibraryERCScopedTestEntitiesPageExportBatch(
						assetLibraryExternalReferenceCode, callbackURL,
						contentType, fieldNames));
	}

	@GraphQLField
	public ERCScopedTestEntity createAssetLibraryERCScopedTestEntity(
			@GraphQLName("assetLibraryExternalReferenceCode") @NotEmpty String
				assetLibraryExternalReferenceCode,
			@GraphQLName("ercScopedTestEntity") ERCScopedTestEntity
				ercScopedTestEntity)
		throws Exception {

		return _applyComponentServiceObjects(
			_ercScopedTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			ercScopedTestEntityResource ->
				ercScopedTestEntityResource.postAssetLibraryERCScopedTestEntity(
					assetLibraryExternalReferenceCode, ercScopedTestEntity));
	}

	@GraphQLField
	public Response createAssetLibraryERCScopedTestEntityBatch(
			@GraphQLName("assetLibraryExternalReferenceCode") @NotEmpty String
				assetLibraryExternalReferenceCode,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_ercScopedTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			ercScopedTestEntityResource ->
				ercScopedTestEntityResource.
					postAssetLibraryERCScopedTestEntityBatch(
						assetLibraryExternalReferenceCode, callbackURL,
						object));
	}

	@GraphQLField
	public Response createSiteERCScopedTestEntitiesPageExportBatch(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_ercScopedTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			ercScopedTestEntityResource ->
				ercScopedTestEntityResource.
					postSiteERCScopedTestEntitiesPageExportBatch(
						siteExternalReferenceCode, callbackURL, contentType,
						fieldNames));
	}

	@GraphQLField
	public ERCScopedTestEntity createSiteERCScopedTestEntity(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("ercScopedTestEntity") ERCScopedTestEntity
				ercScopedTestEntity)
		throws Exception {

		return _applyComponentServiceObjects(
			_ercScopedTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			ercScopedTestEntityResource ->
				ercScopedTestEntityResource.postSiteERCScopedTestEntity(
					siteExternalReferenceCode, ercScopedTestEntity));
	}

	@GraphQLField
	public Response createSiteERCScopedTestEntityBatch(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_ercScopedTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			ercScopedTestEntityResource ->
				ercScopedTestEntityResource.postSiteERCScopedTestEntityBatch(
					siteExternalReferenceCode, callbackURL, object));
	}

	@GraphQLField
	public ERCScopedTestEntity updateAssetLibraryERCScopedTestEntity(
			@GraphQLName("assetLibraryExternalReferenceCode") @NotEmpty String
				assetLibraryExternalReferenceCode,
			@GraphQLName("ercScopedTestEntityExternalReferenceCode") String
				ercScopedTestEntityExternalReferenceCode,
			@GraphQLName("ercScopedTestEntity") ERCScopedTestEntity
				ercScopedTestEntity)
		throws Exception {

		return _applyComponentServiceObjects(
			_ercScopedTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			ercScopedTestEntityResource ->
				ercScopedTestEntityResource.putAssetLibraryERCScopedTestEntity(
					assetLibraryExternalReferenceCode,
					ercScopedTestEntityExternalReferenceCode,
					ercScopedTestEntity));
	}

	@GraphQLField
	public ERCScopedTestEntity updateSiteERCScopedTestEntity(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("ercScopedTestEntityExternalReferenceCode") String
				ercScopedTestEntityExternalReferenceCode,
			@GraphQLName("ercScopedTestEntity") ERCScopedTestEntity
				ercScopedTestEntity)
		throws Exception {

		return _applyComponentServiceObjects(
			_ercScopedTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			ercScopedTestEntityResource ->
				ercScopedTestEntityResource.putSiteERCScopedTestEntity(
					siteExternalReferenceCode,
					ercScopedTestEntityExternalReferenceCode,
					ercScopedTestEntity));
	}

	@GraphQLField
	public boolean deleteSiteERCSiteTestEntity(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("ercSiteTestEntityExternalReferenceCode") String
				ercSiteTestEntityExternalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_ercSiteTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			ercSiteTestEntityResource ->
				ercSiteTestEntityResource.deleteSiteERCSiteTestEntity(
					siteExternalReferenceCode,
					ercSiteTestEntityExternalReferenceCode));

		return true;
	}

	@GraphQLField
	public Response createSiteERCSiteTestEntitiesPageExportBatch(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_ercSiteTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			ercSiteTestEntityResource ->
				ercSiteTestEntityResource.
					postSiteERCSiteTestEntitiesPageExportBatch(
						siteExternalReferenceCode, callbackURL, contentType,
						fieldNames));
	}

	@GraphQLField
	public ERCSiteTestEntity createSiteERCSiteTestEntity(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("ercSiteTestEntity") ERCSiteTestEntity
				ercSiteTestEntity)
		throws Exception {

		return _applyComponentServiceObjects(
			_ercSiteTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			ercSiteTestEntityResource ->
				ercSiteTestEntityResource.postSiteERCSiteTestEntity(
					siteExternalReferenceCode, ercSiteTestEntity));
	}

	@GraphQLField
	public Response createSiteERCSiteTestEntityBatch(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_ercSiteTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			ercSiteTestEntityResource ->
				ercSiteTestEntityResource.postSiteERCSiteTestEntityBatch(
					siteExternalReferenceCode, callbackURL, object));
	}

	@GraphQLField
	public ERCSiteTestEntity updateSiteERCSiteTestEntity(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("ercSiteTestEntityExternalReferenceCode") String
				ercSiteTestEntityExternalReferenceCode,
			@GraphQLName("ercSiteTestEntity") ERCSiteTestEntity
				ercSiteTestEntity)
		throws Exception {

		return _applyComponentServiceObjects(
			_ercSiteTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			ercSiteTestEntityResource ->
				ercSiteTestEntityResource.putSiteERCSiteTestEntity(
					siteExternalReferenceCode,
					ercSiteTestEntityExternalReferenceCode, ercSiteTestEntity));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateSiteERCSiteTestEntityPermissionsPage(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("ercSiteTestEntityExternalReferenceCode") String
					ercSiteTestEntityExternalReferenceCode,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_ercSiteTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			ercSiteTestEntityResource -> {
				Page paginationPage =
					ercSiteTestEntityResource.
						putSiteERCSiteTestEntityPermissionsPage(
							siteExternalReferenceCode,
							ercSiteTestEntityExternalReferenceCode,
							permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public Response createFiltersPageExportBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_filterResourceComponentServiceObjects,
			this::_populateResourceContext,
			filterResource -> filterResource.postFiltersPageExportBatch(
				callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public MultipartTestEntity patchMultipartTestEntity(
			@GraphQLName("multipartTestEntityId") Long multipartTestEntityId,
			@GraphQLName("multipartTestEntity") MultipartTestEntity
				multipartTestEntity)
		throws Exception {

		return _applyComponentServiceObjects(
			_multipartTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			multipartTestEntityResource ->
				multipartTestEntityResource.patchMultipartTestEntity(
					multipartTestEntityId, multipartTestEntity));
	}

	@GraphQLField
	public MultipartTestEntity createMultipartTestEntity(
			@GraphQLName("multipartTestEntity") MultipartTestEntity
				multipartTestEntity)
		throws Exception {

		return _applyComponentServiceObjects(
			_multipartTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			multipartTestEntityResource ->
				multipartTestEntityResource.postMultipartTestEntity(
					multipartTestEntity));
	}

	@GraphQLField
	@GraphQLName(
		description = "null", value = "postMultipartTestEntityMultipartBody"
	)
	public MultipartTestEntity createMultipartTestEntity(
			@GraphQLName("multipartBody") MultipartBody multipartBody)
		throws Exception {

		return _applyComponentServiceObjects(
			_multipartTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			multipartTestEntityResource ->
				multipartTestEntityResource.postMultipartTestEntity(
					multipartBody));
	}

	@GraphQLField
	public Response createMultipartTestEntityBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_multipartTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			multipartTestEntityResource ->
				multipartTestEntityResource.postMultipartTestEntityBatch(
					callbackURL, object));
	}

	@GraphQLField
	@GraphQLName(
		description = "null",
		value = "putMultipartTestEntityMultipartTestEntityIdMultipartBody"
	)
	public MultipartTestEntity updateMultipartTestEntity(
			@GraphQLName("multipartTestEntityId") Long multipartTestEntityId,
			@GraphQLName("multipartBody") MultipartBody multipartBody)
		throws Exception {

		return _applyComponentServiceObjects(
			_multipartTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			multipartTestEntityResource ->
				multipartTestEntityResource.putMultipartTestEntity(
					multipartTestEntityId, multipartBody));
	}

	@GraphQLField
	public Response updateMultipartTestEntityBatch(
			@GraphQLName("multipartBody") MultipartBody multipartBody,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_multipartTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			multipartTestEntityResource ->
				multipartTestEntityResource.putMultipartTestEntityBatch(
					multipartBody, callbackURL, object));
	}

	@GraphQLField
	public Response createSchemasPageExportBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_schemaResourceComponentServiceObjects,
			this::_populateResourceContext,
			schemaResource -> schemaResource.postSchemasPageExportBatch(
				callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public boolean deleteAssetLibraryScopedTestEntityByExternalReferenceCode(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_scopedTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			scopedTestEntityResource ->
				scopedTestEntityResource.
					deleteAssetLibraryScopedTestEntityByExternalReferenceCode(
						Long.valueOf(assetLibraryId), externalReferenceCode));

		return true;
	}

	@GraphQLField
	public boolean deleteSiteScopedTestEntityByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_scopedTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			scopedTestEntityResource ->
				scopedTestEntityResource.
					deleteSiteScopedTestEntityByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode));

		return true;
	}

	@GraphQLField
	public ScopedTestEntity
			patchAssetLibraryScopedTestEntityByExternalReferenceCode(
				@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("scopedTestEntity") ScopedTestEntity
					scopedTestEntity)
		throws Exception {

		return _applyComponentServiceObjects(
			_scopedTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			scopedTestEntityResource ->
				scopedTestEntityResource.
					patchAssetLibraryScopedTestEntityByExternalReferenceCode(
						Long.valueOf(assetLibraryId), externalReferenceCode,
						scopedTestEntity));
	}

	@GraphQLField
	public ScopedTestEntity patchSiteScopedTestEntityByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("scopedTestEntity") ScopedTestEntity scopedTestEntity)
		throws Exception {

		return _applyComponentServiceObjects(
			_scopedTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			scopedTestEntityResource ->
				scopedTestEntityResource.
					patchSiteScopedTestEntityByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode,
						scopedTestEntity));
	}

	@GraphQLField
	public Response createAssetLibraryScopedTestEntitiesPageExportBatch(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_scopedTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			scopedTestEntityResource ->
				scopedTestEntityResource.
					postAssetLibraryScopedTestEntitiesPageExportBatch(
						Long.valueOf(assetLibraryId), callbackURL, contentType,
						fieldNames));
	}

	@GraphQLField
	public ScopedTestEntity createAssetLibraryScopedTestEntity(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("scopedTestEntity") ScopedTestEntity scopedTestEntity)
		throws Exception {

		return _applyComponentServiceObjects(
			_scopedTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			scopedTestEntityResource ->
				scopedTestEntityResource.postAssetLibraryScopedTestEntity(
					Long.valueOf(assetLibraryId), scopedTestEntity));
	}

	@GraphQLField
	public Response createAssetLibraryScopedTestEntityBatch(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_scopedTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			scopedTestEntityResource ->
				scopedTestEntityResource.postAssetLibraryScopedTestEntityBatch(
					Long.valueOf(assetLibraryId), callbackURL, object));
	}

	@GraphQLField
	public Response createSiteScopedTestEntitiesPageExportBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_scopedTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			scopedTestEntityResource ->
				scopedTestEntityResource.
					postSiteScopedTestEntitiesPageExportBatch(
						Long.valueOf(siteKey), callbackURL, contentType,
						fieldNames));
	}

	@GraphQLField
	public ScopedTestEntity createSiteScopedTestEntity(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("scopedTestEntity") ScopedTestEntity scopedTestEntity)
		throws Exception {

		return _applyComponentServiceObjects(
			_scopedTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			scopedTestEntityResource ->
				scopedTestEntityResource.postSiteScopedTestEntity(
					Long.valueOf(siteKey), scopedTestEntity));
	}

	@GraphQLField
	public Response createSiteScopedTestEntityBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_scopedTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			scopedTestEntityResource ->
				scopedTestEntityResource.postSiteScopedTestEntityBatch(
					Long.valueOf(siteKey), callbackURL, object));
	}

	@GraphQLField
	public ScopedTestEntity
			updateAssetLibraryScopedTestEntityByExternalReferenceCode(
				@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("scopedTestEntity") ScopedTestEntity
					scopedTestEntity)
		throws Exception {

		return _applyComponentServiceObjects(
			_scopedTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			scopedTestEntityResource ->
				scopedTestEntityResource.
					putAssetLibraryScopedTestEntityByExternalReferenceCode(
						Long.valueOf(assetLibraryId), externalReferenceCode,
						scopedTestEntity));
	}

	@GraphQLField
	public ScopedTestEntity updateSiteScopedTestEntityByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("scopedTestEntity") ScopedTestEntity scopedTestEntity)
		throws Exception {

		return _applyComponentServiceObjects(
			_scopedTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			scopedTestEntityResource ->
				scopedTestEntityResource.
					putSiteScopedTestEntityByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode,
						scopedTestEntity));
	}

	@GraphQLField
	public boolean deleteSiteSiteTestEntityByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_siteTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			siteTestEntityResource ->
				siteTestEntityResource.
					deleteSiteSiteTestEntityByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Updates only the fields received in the request body, leaving any other fields untouched."
	)
	public SiteTestEntity patchSiteTestEntity(
			@GraphQLName("siteTestEntityId") Long siteTestEntityId,
			@GraphQLName("siteTestEntity") SiteTestEntity siteTestEntity)
		throws Exception {

		return _applyComponentServiceObjects(
			_siteTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			siteTestEntityResource ->
				siteTestEntityResource.patchSiteTestEntity(
					siteTestEntityId, siteTestEntity));
	}

	@GraphQLField
	public Response createSiteSiteTestEntitiesPageExportBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_siteTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			siteTestEntityResource ->
				siteTestEntityResource.postSiteSiteTestEntitiesPageExportBatch(
					Long.valueOf(siteKey), callbackURL, contentType,
					fieldNames));
	}

	@GraphQLField
	public SiteTestEntity createSiteSiteTestEntity(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("siteTestEntity") SiteTestEntity siteTestEntity)
		throws Exception {

		return _applyComponentServiceObjects(
			_siteTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			siteTestEntityResource ->
				siteTestEntityResource.postSiteSiteTestEntity(
					Long.valueOf(siteKey), siteTestEntity));
	}

	@GraphQLField
	public Response createSiteSiteTestEntityBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_siteTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			siteTestEntityResource ->
				siteTestEntityResource.postSiteSiteTestEntityBatch(
					Long.valueOf(siteKey), callbackURL, object));
	}

	@GraphQLField
	public SiteTestEntity updateSiteSiteTestEntityByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("siteTestEntity") SiteTestEntity siteTestEntity)
		throws Exception {

		return _applyComponentServiceObjects(
			_siteTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			siteTestEntityResource ->
				siteTestEntityResource.
					putSiteSiteTestEntityByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode,
						siteTestEntity));
	}

	@GraphQLField
	public SiteTestEntity updateSiteTestEntity(
			@GraphQLName("siteTestEntityId") Long siteTestEntityId,
			@GraphQLName("siteTestEntity") SiteTestEntity siteTestEntity)
		throws Exception {

		return _applyComponentServiceObjects(
			_siteTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			siteTestEntityResource -> siteTestEntityResource.putSiteTestEntity(
				siteTestEntityId, siteTestEntity));
	}

	@GraphQLField
	public Response updateSiteTestEntityBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_siteTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			siteTestEntityResource ->
				siteTestEntityResource.putSiteTestEntityBatch(
					callbackURL, object));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateSiteTestEntityPermissionsPage(
				@GraphQLName("siteTestEntityId") Long siteTestEntityId,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_siteTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			siteTestEntityResource -> {
				Page paginationPage =
					siteTestEntityResource.putSiteTestEntityPermissionsPage(
						siteTestEntityId, permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public Response createSortsPageExportBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_sortResourceComponentServiceObjects,
			this::_populateResourceContext,
			sortResource -> sortResource.postSortsPageExportBatch(
				callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public Response deleteTestEntity(
			@GraphQLName("testEntityId") Long testEntityId,
			@GraphQLName("permanent") Boolean permanent)
		throws Exception {

		return _applyComponentServiceObjects(
			_testEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			testEntityResource -> testEntityResource.deleteTestEntity(
				testEntityId, permanent));
	}

	@GraphQLField
	public Response deleteTestEntityBatch(
			@GraphQLName("permanent") Boolean permanent,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_testEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			testEntityResource -> testEntityResource.deleteTestEntityBatch(
				permanent, callbackURL, object));
	}

	@GraphQLField
	public TestEntity patchTestEntity(
			@GraphQLName("testEntityId") Long testEntityId,
			@GraphQLName("optionalParameter") Long optionalParameter,
			@GraphQLName("testEntity") TestEntity testEntity)
		throws Exception {

		return _applyComponentServiceObjects(
			_testEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			testEntityResource -> testEntityResource.patchTestEntity(
				testEntityId, optionalParameter, testEntity));
	}

	@GraphQLField
	public Response createReservedWord(
			@GraphQLName("booleanValue") Boolean booleanValue)
		throws Exception {

		return _applyComponentServiceObjects(
			_testEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			testEntityResource -> testEntityResource.postReservedWord(
				booleanValue));
	}

	@GraphQLField
	public Response createTestEntitiesPageExportBatch(
			@GraphQLName("filter") String filterString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_testEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			testEntityResource ->
				testEntityResource.postTestEntitiesPageExportBatch(
					_filterBiFunction.apply(testEntityResource, filterString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public TestEntity createTestEntity(
			@GraphQLName("testEntity") TestEntity testEntity)
		throws Exception {

		return _applyComponentServiceObjects(
			_testEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			testEntityResource -> testEntityResource.postTestEntity(
				testEntity));
	}

	@GraphQLField
	public Response createTestEntityBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_testEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			testEntityResource -> testEntityResource.postTestEntityBatch(
				callbackURL, object));
	}

	@GraphQLField
	@GraphQLName(
		description = "null", value = "postTestEntityMultipartBulkMultipartBody"
	)
	public Response createTestEntityMultipartBulk(
			@GraphQLName("multipartBody") MultipartBody multipartBody)
		throws Exception {

		return _applyComponentServiceObjects(
			_testEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			testEntityResource ->
				testEntityResource.postTestEntityMultipartBulk(multipartBody));
	}

	@GraphQLField
	public TestEntity updateTestEntity(
			@GraphQLName("testEntityId") Long testEntityId,
			@GraphQLName("optionalParameter") Long optionalParameter,
			@GraphQLName("testEntity") TestEntity testEntity)
		throws Exception {

		return _applyComponentServiceObjects(
			_testEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			testEntityResource -> testEntityResource.putTestEntity(
				testEntityId, optionalParameter, testEntity));
	}

	@GraphQLField
	public Response updateTestEntityBatch(
			@GraphQLName("optionalParameter") Long optionalParameter,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_testEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			testEntityResource -> testEntityResource.putTestEntityBatch(
				optionalParameter, callbackURL, object));
	}

	@GraphQLField
	public TestEntity updateTestEntityStatus(
			@GraphQLName("testEntityId") Long testEntityId,
			@GraphQLName("testEntity") TestEntity testEntity)
		throws Exception {

		return _applyComponentServiceObjects(
			_testEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			testEntityResource -> testEntityResource.putTestEntityStatus(
				testEntityId, testEntity));
	}

	private <T, R, E1 extends Throwable, E2 extends Throwable> R
			_applyComponentServiceObjects(
				ComponentServiceObjects<T> componentServiceObjects,
				UnsafeConsumer<T, E1> unsafeConsumer,
				UnsafeFunction<T, R, E2> unsafeFunction)
		throws E1, E2 {

		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			return unsafeFunction.apply(resource);
		}
		finally {
			componentServiceObjects.ungetService(resource);
		}
	}

	private <T, E1 extends Throwable, E2 extends Throwable> void
			_applyVoidComponentServiceObjects(
				ComponentServiceObjects<T> componentServiceObjects,
				UnsafeConsumer<T, E1> unsafeConsumer,
				UnsafeConsumer<T, E2> unsafeFunction)
		throws E1, E2 {

		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			unsafeFunction.accept(resource);
		}
		finally {
			componentServiceObjects.ungetService(resource);
		}
	}

	private void _populateResourceContext(
			AssetLibraryTestEntityResource assetLibraryTestEntityResource)
		throws Exception {

		assetLibraryTestEntityResource.setContextAcceptLanguage(
			_acceptLanguage);
		assetLibraryTestEntityResource.setContextCompany(_company);
		assetLibraryTestEntityResource.setContextHttpServletRequest(
			_httpServletRequest);
		assetLibraryTestEntityResource.setContextHttpServletResponse(
			_httpServletResponse);
		assetLibraryTestEntityResource.setContextUriInfo(_uriInfo);
		assetLibraryTestEntityResource.setContextUser(_user);
		assetLibraryTestEntityResource.setGroupLocalService(_groupLocalService);
		assetLibraryTestEntityResource.setRoleLocalService(_roleLocalService);

		assetLibraryTestEntityResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		assetLibraryTestEntityResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			BatchTestEntityResource batchTestEntityResource)
		throws Exception {

		batchTestEntityResource.setContextAcceptLanguage(_acceptLanguage);
		batchTestEntityResource.setContextCompany(_company);
		batchTestEntityResource.setContextHttpServletRequest(
			_httpServletRequest);
		batchTestEntityResource.setContextHttpServletResponse(
			_httpServletResponse);
		batchTestEntityResource.setContextUriInfo(_uriInfo);
		batchTestEntityResource.setContextUser(_user);
		batchTestEntityResource.setGroupLocalService(_groupLocalService);
		batchTestEntityResource.setRoleLocalService(_roleLocalService);

		batchTestEntityResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		batchTestEntityResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			CompanyTestEntityResource companyTestEntityResource)
		throws Exception {

		companyTestEntityResource.setContextAcceptLanguage(_acceptLanguage);
		companyTestEntityResource.setContextCompany(_company);
		companyTestEntityResource.setContextHttpServletRequest(
			_httpServletRequest);
		companyTestEntityResource.setContextHttpServletResponse(
			_httpServletResponse);
		companyTestEntityResource.setContextUriInfo(_uriInfo);
		companyTestEntityResource.setContextUser(_user);
		companyTestEntityResource.setGroupLocalService(_groupLocalService);
		companyTestEntityResource.setRoleLocalService(_roleLocalService);

		companyTestEntityResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		companyTestEntityResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			ERCAssetLibraryTestEntityResource ercAssetLibraryTestEntityResource)
		throws Exception {

		ercAssetLibraryTestEntityResource.setContextAcceptLanguage(
			_acceptLanguage);
		ercAssetLibraryTestEntityResource.setContextCompany(_company);
		ercAssetLibraryTestEntityResource.setContextHttpServletRequest(
			_httpServletRequest);
		ercAssetLibraryTestEntityResource.setContextHttpServletResponse(
			_httpServletResponse);
		ercAssetLibraryTestEntityResource.setContextUriInfo(_uriInfo);
		ercAssetLibraryTestEntityResource.setContextUser(_user);
		ercAssetLibraryTestEntityResource.setGroupLocalService(
			_groupLocalService);
		ercAssetLibraryTestEntityResource.setRoleLocalService(
			_roleLocalService);

		ercAssetLibraryTestEntityResource.
			setVulcanBatchEngineExportTaskResource(
				_vulcanBatchEngineExportTaskResource);

		ercAssetLibraryTestEntityResource.
			setVulcanBatchEngineImportTaskResource(
				_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			ERCScopedTestEntityResource ercScopedTestEntityResource)
		throws Exception {

		ercScopedTestEntityResource.setContextAcceptLanguage(_acceptLanguage);
		ercScopedTestEntityResource.setContextCompany(_company);
		ercScopedTestEntityResource.setContextHttpServletRequest(
			_httpServletRequest);
		ercScopedTestEntityResource.setContextHttpServletResponse(
			_httpServletResponse);
		ercScopedTestEntityResource.setContextUriInfo(_uriInfo);
		ercScopedTestEntityResource.setContextUser(_user);
		ercScopedTestEntityResource.setGroupLocalService(_groupLocalService);
		ercScopedTestEntityResource.setRoleLocalService(_roleLocalService);

		ercScopedTestEntityResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		ercScopedTestEntityResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			ERCSiteTestEntityResource ercSiteTestEntityResource)
		throws Exception {

		ercSiteTestEntityResource.setContextAcceptLanguage(_acceptLanguage);
		ercSiteTestEntityResource.setContextCompany(_company);
		ercSiteTestEntityResource.setContextHttpServletRequest(
			_httpServletRequest);
		ercSiteTestEntityResource.setContextHttpServletResponse(
			_httpServletResponse);
		ercSiteTestEntityResource.setContextUriInfo(_uriInfo);
		ercSiteTestEntityResource.setContextUser(_user);
		ercSiteTestEntityResource.setGroupLocalService(_groupLocalService);
		ercSiteTestEntityResource.setRoleLocalService(_roleLocalService);

		ercSiteTestEntityResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		ercSiteTestEntityResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(FilterResource filterResource)
		throws Exception {

		filterResource.setContextAcceptLanguage(_acceptLanguage);
		filterResource.setContextCompany(_company);
		filterResource.setContextHttpServletRequest(_httpServletRequest);
		filterResource.setContextHttpServletResponse(_httpServletResponse);
		filterResource.setContextUriInfo(_uriInfo);
		filterResource.setContextUser(_user);
		filterResource.setGroupLocalService(_groupLocalService);
		filterResource.setRoleLocalService(_roleLocalService);

		filterResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		filterResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			MultipartTestEntityResource multipartTestEntityResource)
		throws Exception {

		multipartTestEntityResource.setContextAcceptLanguage(_acceptLanguage);
		multipartTestEntityResource.setContextCompany(_company);
		multipartTestEntityResource.setContextHttpServletRequest(
			_httpServletRequest);
		multipartTestEntityResource.setContextHttpServletResponse(
			_httpServletResponse);
		multipartTestEntityResource.setContextUriInfo(_uriInfo);
		multipartTestEntityResource.setContextUser(_user);
		multipartTestEntityResource.setGroupLocalService(_groupLocalService);
		multipartTestEntityResource.setRoleLocalService(_roleLocalService);

		multipartTestEntityResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		multipartTestEntityResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(SchemaResource schemaResource)
		throws Exception {

		schemaResource.setContextAcceptLanguage(_acceptLanguage);
		schemaResource.setContextCompany(_company);
		schemaResource.setContextHttpServletRequest(_httpServletRequest);
		schemaResource.setContextHttpServletResponse(_httpServletResponse);
		schemaResource.setContextUriInfo(_uriInfo);
		schemaResource.setContextUser(_user);
		schemaResource.setGroupLocalService(_groupLocalService);
		schemaResource.setRoleLocalService(_roleLocalService);

		schemaResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		schemaResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			ScopedTestEntityResource scopedTestEntityResource)
		throws Exception {

		scopedTestEntityResource.setContextAcceptLanguage(_acceptLanguage);
		scopedTestEntityResource.setContextCompany(_company);
		scopedTestEntityResource.setContextHttpServletRequest(
			_httpServletRequest);
		scopedTestEntityResource.setContextHttpServletResponse(
			_httpServletResponse);
		scopedTestEntityResource.setContextUriInfo(_uriInfo);
		scopedTestEntityResource.setContextUser(_user);
		scopedTestEntityResource.setGroupLocalService(_groupLocalService);
		scopedTestEntityResource.setRoleLocalService(_roleLocalService);

		scopedTestEntityResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		scopedTestEntityResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			SiteTestEntityResource siteTestEntityResource)
		throws Exception {

		siteTestEntityResource.setContextAcceptLanguage(_acceptLanguage);
		siteTestEntityResource.setContextCompany(_company);
		siteTestEntityResource.setContextHttpServletRequest(
			_httpServletRequest);
		siteTestEntityResource.setContextHttpServletResponse(
			_httpServletResponse);
		siteTestEntityResource.setContextUriInfo(_uriInfo);
		siteTestEntityResource.setContextUser(_user);
		siteTestEntityResource.setGroupLocalService(_groupLocalService);
		siteTestEntityResource.setRoleLocalService(_roleLocalService);

		siteTestEntityResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		siteTestEntityResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(SortResource sortResource)
		throws Exception {

		sortResource.setContextAcceptLanguage(_acceptLanguage);
		sortResource.setContextCompany(_company);
		sortResource.setContextHttpServletRequest(_httpServletRequest);
		sortResource.setContextHttpServletResponse(_httpServletResponse);
		sortResource.setContextUriInfo(_uriInfo);
		sortResource.setContextUser(_user);
		sortResource.setGroupLocalService(_groupLocalService);
		sortResource.setRoleLocalService(_roleLocalService);

		sortResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		sortResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(TestEntityResource testEntityResource)
		throws Exception {

		testEntityResource.setContextAcceptLanguage(_acceptLanguage);
		testEntityResource.setContextCompany(_company);
		testEntityResource.setContextHttpServletRequest(_httpServletRequest);
		testEntityResource.setContextHttpServletResponse(_httpServletResponse);
		testEntityResource.setContextUriInfo(_uriInfo);
		testEntityResource.setContextUser(_user);
		testEntityResource.setGroupLocalService(_groupLocalService);
		testEntityResource.setRoleLocalService(_roleLocalService);

		testEntityResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		testEntityResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private static ComponentServiceObjects<AssetLibraryTestEntityResource>
		_assetLibraryTestEntityResourceComponentServiceObjects;
	private static ComponentServiceObjects<BatchTestEntityResource>
		_batchTestEntityResourceComponentServiceObjects;
	private static ComponentServiceObjects<CompanyTestEntityResource>
		_companyTestEntityResourceComponentServiceObjects;
	private static ComponentServiceObjects<ERCAssetLibraryTestEntityResource>
		_ercAssetLibraryTestEntityResourceComponentServiceObjects;
	private static ComponentServiceObjects<ERCScopedTestEntityResource>
		_ercScopedTestEntityResourceComponentServiceObjects;
	private static ComponentServiceObjects<ERCSiteTestEntityResource>
		_ercSiteTestEntityResourceComponentServiceObjects;
	private static ComponentServiceObjects<FilterResource>
		_filterResourceComponentServiceObjects;
	private static ComponentServiceObjects<MultipartTestEntityResource>
		_multipartTestEntityResourceComponentServiceObjects;
	private static ComponentServiceObjects<SchemaResource>
		_schemaResourceComponentServiceObjects;
	private static ComponentServiceObjects<ScopedTestEntityResource>
		_scopedTestEntityResourceComponentServiceObjects;
	private static ComponentServiceObjects<SiteTestEntityResource>
		_siteTestEntityResourceComponentServiceObjects;
	private static ComponentServiceObjects<SortResource>
		_sortResourceComponentServiceObjects;
	private static ComponentServiceObjects<TestEntityResource>
		_testEntityResourceComponentServiceObjects;

	private AcceptLanguage _acceptLanguage;
	private com.liferay.portal.kernel.model.Company _company;
	private BiFunction
		<Object, String, com.liferay.portal.kernel.search.filter.Filter>
			_filterBiFunction;
	private GroupLocalService _groupLocalService;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private RoleLocalService _roleLocalService;
	private BiFunction<Object, String, com.liferay.portal.kernel.search.Sort[]>
		_sortsBiFunction;
	private UriInfo _uriInfo;
	private com.liferay.portal.kernel.model.User _user;
	private VulcanBatchEngineExportTaskResource
		_vulcanBatchEngineExportTaskResource;
	private VulcanBatchEngineImportTaskResource
		_vulcanBatchEngineImportTaskResource;

}