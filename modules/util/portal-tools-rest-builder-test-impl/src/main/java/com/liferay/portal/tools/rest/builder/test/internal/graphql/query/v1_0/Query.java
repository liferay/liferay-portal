/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.internal.graphql.query.v1_0;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.AssetLibraryTestEntity;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.BatchTestEntity;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.CompanyTestEntity;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.ERCAssetLibraryTestEntity;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.ERCScopedTestEntity;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.ERCSiteTestEntity;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.EntityModelResourceTestEntity1;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.EntityModelResourceTestEntity2;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.Filter;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.MultipartTestEntity;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.Schema;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.ScopedTestEntity;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.SiteTestEntity;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.Sort;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.TestEntity;
import com.liferay.portal.tools.rest.builder.test.dto.v1_0.TestEntityAddress;
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
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLTypeExtension;
import com.liferay.portal.vulcan.pagination.Page;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.validation.constraints.NotEmpty;

import jakarta.ws.rs.core.UriInfo;

import java.util.Map;
import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class Query {

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

	public static void
		setEntityModelResourceTestEntity1ResourceComponentServiceObjects(
			ComponentServiceObjects<EntityModelResourceTestEntity1Resource>
				entityModelResourceTestEntity1ResourceComponentServiceObjects) {

		_entityModelResourceTestEntity1ResourceComponentServiceObjects =
			entityModelResourceTestEntity1ResourceComponentServiceObjects;
	}

	public static void
		setEntityModelResourceTestEntity2ResourceComponentServiceObjects(
			ComponentServiceObjects<EntityModelResourceTestEntity2Resource>
				entityModelResourceTestEntity2ResourceComponentServiceObjects) {

		_entityModelResourceTestEntity2ResourceComponentServiceObjects =
			entityModelResourceTestEntity2ResourceComponentServiceObjects;
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

	public static void setTestEntityAddressResourceComponentServiceObjects(
		ComponentServiceObjects<TestEntityAddressResource>
			testEntityAddressResourceComponentServiceObjects) {

		_testEntityAddressResourceComponentServiceObjects =
			testEntityAddressResourceComponentServiceObjects;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryAssetLibraryTestEntities(assetLibraryId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AssetLibraryTestEntityPage assetLibraryAssetLibraryTestEntities(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId)
		throws Exception {

		return _applyComponentServiceObjects(
			_assetLibraryTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			assetLibraryTestEntityResource -> new AssetLibraryTestEntityPage(
				assetLibraryTestEntityResource.
					getAssetLibraryAssetLibraryTestEntitiesPage(
						Long.valueOf(assetLibraryId))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {batchTestEntities{items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public BatchTestEntityPage batchTestEntities() throws Exception {
		return _applyComponentServiceObjects(
			_batchTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			batchTestEntityResource -> new BatchTestEntityPage(
				batchTestEntityResource.getBatchTestEntitiesPage()));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {batchTestEntity(batchTestEntityId: ___){customFields, externalReferenceCode, id, name, nestedField, relatedCompanyTestEntity}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public BatchTestEntity batchTestEntity(
			@GraphQLName("batchTestEntityId") Long batchTestEntityId)
		throws Exception {

		return _applyComponentServiceObjects(
			_batchTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			batchTestEntityResource ->
				batchTestEntityResource.getBatchTestEntity(batchTestEntityId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {batchTestEntityByExternalReferenceCode(externalReferenceCode: ___){customFields, externalReferenceCode, id, name, nestedField, relatedCompanyTestEntity}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public BatchTestEntity batchTestEntityByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_batchTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			batchTestEntityResource ->
				batchTestEntityResource.
					getBatchTestEntityByExternalReferenceCode(
						externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {companyTestEntities{items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public CompanyTestEntityPage companyTestEntities() throws Exception {
		return _applyComponentServiceObjects(
			_companyTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			companyTestEntityResource -> new CompanyTestEntityPage(
				companyTestEntityResource.getCompanyTestEntitiesPage()));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {companyTestEntity(companyTestEntityId: ___){dateCreated, dateModified, description, externalReferenceCode, id, permissions}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public CompanyTestEntity companyTestEntity(
			@GraphQLName("companyTestEntityId") Long companyTestEntityId)
		throws Exception {

		return _applyComponentServiceObjects(
			_companyTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			companyTestEntityResource ->
				companyTestEntityResource.getCompanyTestEntity(
					companyTestEntityId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {companyTestEntityByExternalReferenceCode(externalReferenceCode: ___){dateCreated, dateModified, description, externalReferenceCode, id, permissions}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public CompanyTestEntity companyTestEntityByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_companyTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			companyTestEntityResource ->
				companyTestEntityResource.
					getCompanyTestEntityByExternalReferenceCode(
						externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {companyTestEntityPermissions(companyTestEntityId: ___, roleNames: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public CompanyTestEntityPage companyTestEntityPermissions(
			@GraphQLName("companyTestEntityId") Long companyTestEntityId,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_companyTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			companyTestEntityResource -> new CompanyTestEntityPage(
				companyTestEntityResource.getCompanyTestEntityPermissionsPage(
					companyTestEntityId, roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryERCAssetLibraryTestEntities(assetLibraryExternalReferenceCode: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ERCAssetLibraryTestEntityPage
			assetLibraryERCAssetLibraryTestEntities(
				@GraphQLName("assetLibraryExternalReferenceCode") @NotEmpty
					String assetLibraryExternalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_ercAssetLibraryTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			ercAssetLibraryTestEntityResource ->
				new ERCAssetLibraryTestEntityPage(
					ercAssetLibraryTestEntityResource.
						getAssetLibraryERCAssetLibraryTestEntitiesPage(
							assetLibraryExternalReferenceCode)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryERCAssetLibraryTestEntity(assetLibraryExternalReferenceCode: ___, ercAssetLibraryTestEntityExternalReferenceCode: ___){assetLibraryExternalReferenceCode, dateCreated, dateModified, description, externalReferenceCode, permissions}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ERCAssetLibraryTestEntity assetLibraryERCAssetLibraryTestEntity(
			@GraphQLName("assetLibraryExternalReferenceCode") @NotEmpty String
				assetLibraryExternalReferenceCode,
			@GraphQLName("ercAssetLibraryTestEntityExternalReferenceCode")
				String ercAssetLibraryTestEntityExternalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_ercAssetLibraryTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			ercAssetLibraryTestEntityResource ->
				ercAssetLibraryTestEntityResource.
					getAssetLibraryERCAssetLibraryTestEntity(
						assetLibraryExternalReferenceCode,
						ercAssetLibraryTestEntityExternalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryERCAssetLibraryTestEntityPermissions(assetLibraryExternalReferenceCode: ___, ercAssetLibraryTestEntityExternalReferenceCode: ___, roleNames: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ERCAssetLibraryTestEntityPage
			assetLibraryERCAssetLibraryTestEntityPermissions(
				@GraphQLName("assetLibraryExternalReferenceCode") @NotEmpty
					String assetLibraryExternalReferenceCode,
				@GraphQLName("ercAssetLibraryTestEntityExternalReferenceCode")
					String ercAssetLibraryTestEntityExternalReferenceCode,
				@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_ercAssetLibraryTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			ercAssetLibraryTestEntityResource ->
				new ERCAssetLibraryTestEntityPage(
					ercAssetLibraryTestEntityResource.
						getAssetLibraryERCAssetLibraryTestEntityPermissionsPage(
							assetLibraryExternalReferenceCode,
							ercAssetLibraryTestEntityExternalReferenceCode,
							roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryERCScopedTestEntities(assetLibraryExternalReferenceCode: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ERCScopedTestEntityPage assetLibraryERCScopedTestEntities(
			@GraphQLName("assetLibraryExternalReferenceCode") @NotEmpty String
				assetLibraryExternalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_ercScopedTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			ercScopedTestEntityResource -> new ERCScopedTestEntityPage(
				ercScopedTestEntityResource.
					getAssetLibraryERCScopedTestEntitiesPage(
						assetLibraryExternalReferenceCode)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryERCScopedTestEntity(assetLibraryExternalReferenceCode: ___, ercScopedTestEntityExternalReferenceCode: ___){assetLibraryExternalReferenceCode, dateCreated, dateModified, description, externalReferenceCode, permissions, siteExternalReferenceCode}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ERCScopedTestEntity assetLibraryERCScopedTestEntity(
			@GraphQLName("assetLibraryExternalReferenceCode") @NotEmpty String
				assetLibraryExternalReferenceCode,
			@GraphQLName("ercScopedTestEntityExternalReferenceCode") String
				ercScopedTestEntityExternalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_ercScopedTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			ercScopedTestEntityResource ->
				ercScopedTestEntityResource.getAssetLibraryERCScopedTestEntity(
					assetLibraryExternalReferenceCode,
					ercScopedTestEntityExternalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {eRCScopedTestEntities(siteExternalReferenceCode: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ERCScopedTestEntityPage eRCScopedTestEntities(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_ercScopedTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			ercScopedTestEntityResource -> new ERCScopedTestEntityPage(
				ercScopedTestEntityResource.getSiteERCScopedTestEntitiesPage(
					siteExternalReferenceCode)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {eRCScopedTestEntity(ercScopedTestEntityExternalReferenceCode: ___, siteExternalReferenceCode: ___){assetLibraryExternalReferenceCode, dateCreated, dateModified, description, externalReferenceCode, permissions, siteExternalReferenceCode}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ERCScopedTestEntity eRCScopedTestEntity(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("ercScopedTestEntityExternalReferenceCode") String
				ercScopedTestEntityExternalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_ercScopedTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			ercScopedTestEntityResource ->
				ercScopedTestEntityResource.getSiteERCScopedTestEntity(
					siteExternalReferenceCode,
					ercScopedTestEntityExternalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {eRCSiteTestEntities(siteExternalReferenceCode: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ERCSiteTestEntityPage eRCSiteTestEntities(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_ercSiteTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			ercSiteTestEntityResource -> new ERCSiteTestEntityPage(
				ercSiteTestEntityResource.getSiteERCSiteTestEntitiesPage(
					siteExternalReferenceCode)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {eRCSiteTestEntity(ercSiteTestEntityExternalReferenceCode: ___, siteExternalReferenceCode: ___){dateCreated, dateModified, description, externalReferenceCode, permissions, siteExternalReferenceCode}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ERCSiteTestEntity eRCSiteTestEntity(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("ercSiteTestEntityExternalReferenceCode") String
				ercSiteTestEntityExternalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_ercSiteTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			ercSiteTestEntityResource ->
				ercSiteTestEntityResource.getSiteERCSiteTestEntity(
					siteExternalReferenceCode,
					ercSiteTestEntityExternalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {eRCSiteTestEntityPermissions(ercSiteTestEntityExternalReferenceCode: ___, roleNames: ___, siteExternalReferenceCode: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ERCSiteTestEntityPage eRCSiteTestEntityPermissions(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("ercSiteTestEntityExternalReferenceCode") String
				ercSiteTestEntityExternalReferenceCode,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_ercSiteTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			ercSiteTestEntityResource -> new ERCSiteTestEntityPage(
				ercSiteTestEntityResource.
					getSiteERCSiteTestEntityPermissionsPage(
						siteExternalReferenceCode,
						ercSiteTestEntityExternalReferenceCode, roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {entityModelResourceTestEntities1{items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieve all EntityModelResourceTestEntity1 items."
	)
	public EntityModelResourceTestEntity1Page entityModelResourceTestEntities1()
		throws Exception {

		return _applyComponentServiceObjects(
			_entityModelResourceTestEntity1ResourceComponentServiceObjects,
			this::_populateResourceContext,
			entityModelResourceTestEntity1Resource ->
				new EntityModelResourceTestEntity1Page(
					entityModelResourceTestEntity1Resource.
						getEntityModelResourceTestEntities1Page()));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {entityModelResourceTestEntities2EntityModelResourceTestEntity2(entityModelResourceTestEntity2Id: ___){id, name}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieve a EntityModelResourceTestEntity2 item. (EntityModelResource and VulcanBatchEngineTaskItemDelegate interfaces will not be implemented automatically)"
	)
	public EntityModelResourceTestEntity2
			entityModelResourceTestEntities2EntityModelResourceTestEntity2(
				@GraphQLName("entityModelResourceTestEntity2Id") Long
					entityModelResourceTestEntity2Id)
		throws Exception {

		return _applyComponentServiceObjects(
			_entityModelResourceTestEntity2ResourceComponentServiceObjects,
			this::_populateResourceContext,
			entityModelResourceTestEntity2Resource ->
				entityModelResourceTestEntity2Resource.
					getEntityModelResourceTestEntities2EntityModelResourceTestEntity2(
						entityModelResourceTestEntity2Id));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {filters{items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public FilterPage filters() throws Exception {
		return _applyComponentServiceObjects(
			_filterResourceComponentServiceObjects,
			this::_populateResourceContext,
			filterResource -> new FilterPage(filterResource.getFiltersPage()));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {multipartTestEntity(multipartTestEntityId: ___){id, name}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public MultipartTestEntity multipartTestEntity(
			@GraphQLName("multipartTestEntityId") Long multipartTestEntityId)
		throws Exception {

		return _applyComponentServiceObjects(
			_multipartTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			multipartTestEntityResource ->
				multipartTestEntityResource.getMultipartTestEntity(
					multipartTestEntityId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {schemas{items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public SchemaPage schemas() throws Exception {
		return _applyComponentServiceObjects(
			_schemaResourceComponentServiceObjects,
			this::_populateResourceContext,
			schemaResource -> new SchemaPage(schemaResource.getSchemasPage()));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryScopedTestEntities(assetLibraryId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ScopedTestEntityPage assetLibraryScopedTestEntities(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId)
		throws Exception {

		return _applyComponentServiceObjects(
			_scopedTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			scopedTestEntityResource -> new ScopedTestEntityPage(
				scopedTestEntityResource.getAssetLibraryScopedTestEntitiesPage(
					Long.valueOf(assetLibraryId))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryScopedTestEntityByExternalReferenceCode(assetLibraryId: ___, externalReferenceCode: ___){assetLibraryKey, dateCreated, dateModified, description, externalReferenceCode, id, permissions, siteId}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ScopedTestEntity assetLibraryScopedTestEntityByExternalReferenceCode(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_scopedTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			scopedTestEntityResource ->
				scopedTestEntityResource.
					getAssetLibraryScopedTestEntityByExternalReferenceCode(
						Long.valueOf(assetLibraryId), externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {scopedTestEntities(siteKey: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ScopedTestEntityPage scopedTestEntities(
			@GraphQLName("siteKey") @NotEmpty String siteKey)
		throws Exception {

		return _applyComponentServiceObjects(
			_scopedTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			scopedTestEntityResource -> new ScopedTestEntityPage(
				scopedTestEntityResource.getSiteScopedTestEntitiesPage(
					Long.valueOf(siteKey))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {scopedTestEntityByExternalReferenceCode(externalReferenceCode: ___, siteKey: ___){assetLibraryKey, dateCreated, dateModified, description, externalReferenceCode, id, permissions, siteId}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ScopedTestEntity scopedTestEntityByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_scopedTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			scopedTestEntityResource ->
				scopedTestEntityResource.
					getSiteScopedTestEntityByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {siteTestEntities(siteKey: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public SiteTestEntityPage siteTestEntities(
			@GraphQLName("siteKey") @NotEmpty String siteKey)
		throws Exception {

		return _applyComponentServiceObjects(
			_siteTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			siteTestEntityResource -> new SiteTestEntityPage(
				siteTestEntityResource.getSiteSiteTestEntitiesPage(
					Long.valueOf(siteKey))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {siteTestEntityByExternalReferenceCode(externalReferenceCode: ___, siteKey: ___){dateCreated, dateModified, description, externalReferenceCode, id, permissions, siteId}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public SiteTestEntity siteTestEntityByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_siteTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			siteTestEntityResource ->
				siteTestEntityResource.
					getSiteSiteTestEntityByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {siteTestEntity(siteTestEntityId: ___){dateCreated, dateModified, description, externalReferenceCode, id, permissions, siteId}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public SiteTestEntity siteTestEntity(
			@GraphQLName("siteTestEntityId") Long siteTestEntityId)
		throws Exception {

		return _applyComponentServiceObjects(
			_siteTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			siteTestEntityResource -> siteTestEntityResource.getSiteTestEntity(
				siteTestEntityId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {testEntityPermissions(roleNames: ___, siteTestEntityId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public SiteTestEntityPage testEntityPermissions(
			@GraphQLName("siteTestEntityId") Long siteTestEntityId,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_siteTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			siteTestEntityResource -> new SiteTestEntityPage(
				siteTestEntityResource.getSiteTestEntityPermissionsPage(
					siteTestEntityId, roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {sorts{items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public SortPage sorts() throws Exception {
		return _applyComponentServiceObjects(
			_sortResourceComponentServiceObjects,
			this::_populateResourceContext,
			sortResource -> new SortPage(sortResource.getSortsPage()));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {testEntities(filter: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public TestEntityPage testEntities(
			@GraphQLName("filter") String filterString)
		throws Exception {

		return _applyComponentServiceObjects(
			_testEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			testEntityResource -> new TestEntityPage(
				testEntityResource.getTestEntitiesPage(
					_filterBiFunction.apply(
						testEntityResource, filterString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {testEntity(testEntityId: ___){dateCreated, dateModified, description, documentId, id, jsonProperty, name, nestedTestEntity, self, stringTestEntities, stringTestEntity, testEntities, type}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public TestEntity testEntity(@GraphQLName("testEntityId") Long testEntityId)
		throws Exception {

		return _applyComponentServiceObjects(
			_testEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			testEntityResource -> testEntityResource.getTestEntity(
				testEntityId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {testEntityCount{}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the count.")
	public Integer testEntityCount() throws Exception {
		return _applyComponentServiceObjects(
			_testEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			testEntityResource -> testEntityResource.getTestEntityCount());
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {testEntityTestEntityAddress(testEntityId: ___){dateCreated, dateModified, description, documentId, id, jsonProperty, name}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public TestEntityAddress testEntityTestEntityAddress(
			@GraphQLName("testEntityId") Long testEntityId)
		throws Exception {

		return _applyComponentServiceObjects(
			_testEntityAddressResourceComponentServiceObjects,
			this::_populateResourceContext,
			testEntityAddressResource ->
				testEntityAddressResource.getTestEntityTestEntityAddress(
					testEntityId));
	}

	@GraphQLTypeExtension(SiteTestEntity.class)
	public class GetSiteTestEntityPermissionsPageTypeExtension {

		public GetSiteTestEntityPermissionsPageTypeExtension(
			SiteTestEntity siteTestEntity) {

			_siteTestEntity = siteTestEntity;
		}

		@GraphQLField
		public SiteTestEntityPage testEntityPermissions(
				@GraphQLName("roleNames") String roleNames)
			throws Exception {

			return _applyComponentServiceObjects(
				_siteTestEntityResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				siteTestEntityResource -> new SiteTestEntityPage(
					siteTestEntityResource.getSiteTestEntityPermissionsPage(
						_siteTestEntity.getId(), roleNames)));
		}

		private SiteTestEntity _siteTestEntity;

	}

	@GraphQLTypeExtension(TestEntity.class)
	public class GetTestEntityTestEntityAddressTypeExtension {

		public GetTestEntityTestEntityAddressTypeExtension(
			TestEntity testEntity) {

			_testEntity = testEntity;
		}

		@GraphQLField
		public TestEntityAddress testEntityAddress() throws Exception {
			return _applyComponentServiceObjects(
				_testEntityAddressResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				testEntityAddressResource ->
					testEntityAddressResource.getTestEntityTestEntityAddress(
						_testEntity.getId()));
		}

		private TestEntity _testEntity;

	}

	@GraphQLTypeExtension(CompanyTestEntity.class)
	public class GetBatchTestEntityByExternalReferenceCodeTypeExtension {

		public GetBatchTestEntityByExternalReferenceCodeTypeExtension(
			CompanyTestEntity companyTestEntity) {

			_companyTestEntity = companyTestEntity;
		}

		@GraphQLField
		public BatchTestEntity batchTestEntityByExternalReferenceCode()
			throws Exception {

			return _applyComponentServiceObjects(
				_batchTestEntityResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				batchTestEntityResource ->
					batchTestEntityResource.
						getBatchTestEntityByExternalReferenceCode(
							_companyTestEntity.getExternalReferenceCode()));
		}

		private CompanyTestEntity _companyTestEntity;

	}

	@GraphQLTypeExtension(BatchTestEntity.class)
	public class GetCompanyTestEntityByExternalReferenceCodeTypeExtension {

		public GetCompanyTestEntityByExternalReferenceCodeTypeExtension(
			BatchTestEntity batchTestEntity) {

			_batchTestEntity = batchTestEntity;
		}

		@GraphQLField
		public CompanyTestEntity companyTestEntityByExternalReferenceCode()
			throws Exception {

			return _applyComponentServiceObjects(
				_companyTestEntityResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				companyTestEntityResource ->
					companyTestEntityResource.
						getCompanyTestEntityByExternalReferenceCode(
							_batchTestEntity.getExternalReferenceCode()));
		}

		private BatchTestEntity _batchTestEntity;

	}

	@GraphQLName("AssetLibraryTestEntityPage")
	public class AssetLibraryTestEntityPage {

		public AssetLibraryTestEntityPage(Page assetLibraryTestEntityPage) {
			actions = assetLibraryTestEntityPage.getActions();

			items = assetLibraryTestEntityPage.getItems();
			lastPage = assetLibraryTestEntityPage.getLastPage();
			page = assetLibraryTestEntityPage.getPage();
			pageSize = assetLibraryTestEntityPage.getPageSize();
			totalCount = assetLibraryTestEntityPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<AssetLibraryTestEntity> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("BatchTestEntityPage")
	public class BatchTestEntityPage {

		public BatchTestEntityPage(Page batchTestEntityPage) {
			actions = batchTestEntityPage.getActions();

			items = batchTestEntityPage.getItems();
			lastPage = batchTestEntityPage.getLastPage();
			page = batchTestEntityPage.getPage();
			pageSize = batchTestEntityPage.getPageSize();
			totalCount = batchTestEntityPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<BatchTestEntity> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("CompanyTestEntityPage")
	public class CompanyTestEntityPage {

		public CompanyTestEntityPage(Page companyTestEntityPage) {
			actions = companyTestEntityPage.getActions();

			items = companyTestEntityPage.getItems();
			lastPage = companyTestEntityPage.getLastPage();
			page = companyTestEntityPage.getPage();
			pageSize = companyTestEntityPage.getPageSize();
			totalCount = companyTestEntityPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<CompanyTestEntity> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ERCAssetLibraryTestEntityPage")
	public class ERCAssetLibraryTestEntityPage {

		public ERCAssetLibraryTestEntityPage(
			Page ercAssetLibraryTestEntityPage) {

			actions = ercAssetLibraryTestEntityPage.getActions();

			items = ercAssetLibraryTestEntityPage.getItems();
			lastPage = ercAssetLibraryTestEntityPage.getLastPage();
			page = ercAssetLibraryTestEntityPage.getPage();
			pageSize = ercAssetLibraryTestEntityPage.getPageSize();
			totalCount = ercAssetLibraryTestEntityPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ERCAssetLibraryTestEntity> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ERCScopedTestEntityPage")
	public class ERCScopedTestEntityPage {

		public ERCScopedTestEntityPage(Page ercScopedTestEntityPage) {
			actions = ercScopedTestEntityPage.getActions();

			items = ercScopedTestEntityPage.getItems();
			lastPage = ercScopedTestEntityPage.getLastPage();
			page = ercScopedTestEntityPage.getPage();
			pageSize = ercScopedTestEntityPage.getPageSize();
			totalCount = ercScopedTestEntityPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ERCScopedTestEntity> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ERCSiteTestEntityPage")
	public class ERCSiteTestEntityPage {

		public ERCSiteTestEntityPage(Page ercSiteTestEntityPage) {
			actions = ercSiteTestEntityPage.getActions();

			items = ercSiteTestEntityPage.getItems();
			lastPage = ercSiteTestEntityPage.getLastPage();
			page = ercSiteTestEntityPage.getPage();
			pageSize = ercSiteTestEntityPage.getPageSize();
			totalCount = ercSiteTestEntityPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ERCSiteTestEntity> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("EntityModelResourceTestEntity1Page")
	public class EntityModelResourceTestEntity1Page {

		public EntityModelResourceTestEntity1Page(
			Page entityModelResourceTestEntity1Page) {

			actions = entityModelResourceTestEntity1Page.getActions();

			items = entityModelResourceTestEntity1Page.getItems();
			lastPage = entityModelResourceTestEntity1Page.getLastPage();
			page = entityModelResourceTestEntity1Page.getPage();
			pageSize = entityModelResourceTestEntity1Page.getPageSize();
			totalCount = entityModelResourceTestEntity1Page.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<EntityModelResourceTestEntity1> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("EntityModelResourceTestEntity2Page")
	public class EntityModelResourceTestEntity2Page {

		public EntityModelResourceTestEntity2Page(
			Page entityModelResourceTestEntity2Page) {

			actions = entityModelResourceTestEntity2Page.getActions();

			items = entityModelResourceTestEntity2Page.getItems();
			lastPage = entityModelResourceTestEntity2Page.getLastPage();
			page = entityModelResourceTestEntity2Page.getPage();
			pageSize = entityModelResourceTestEntity2Page.getPageSize();
			totalCount = entityModelResourceTestEntity2Page.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<EntityModelResourceTestEntity2> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("FilterPage")
	public class FilterPage {

		public FilterPage(Page filterPage) {
			actions = filterPage.getActions();

			items = filterPage.getItems();
			lastPage = filterPage.getLastPage();
			page = filterPage.getPage();
			pageSize = filterPage.getPageSize();
			totalCount = filterPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Filter> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("MultipartTestEntityPage")
	public class MultipartTestEntityPage {

		public MultipartTestEntityPage(Page multipartTestEntityPage) {
			actions = multipartTestEntityPage.getActions();

			items = multipartTestEntityPage.getItems();
			lastPage = multipartTestEntityPage.getLastPage();
			page = multipartTestEntityPage.getPage();
			pageSize = multipartTestEntityPage.getPageSize();
			totalCount = multipartTestEntityPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<MultipartTestEntity> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("SchemaPage")
	public class SchemaPage {

		public SchemaPage(Page schemaPage) {
			actions = schemaPage.getActions();

			items = schemaPage.getItems();
			lastPage = schemaPage.getLastPage();
			page = schemaPage.getPage();
			pageSize = schemaPage.getPageSize();
			totalCount = schemaPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Schema> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ScopedTestEntityPage")
	public class ScopedTestEntityPage {

		public ScopedTestEntityPage(Page scopedTestEntityPage) {
			actions = scopedTestEntityPage.getActions();

			items = scopedTestEntityPage.getItems();
			lastPage = scopedTestEntityPage.getLastPage();
			page = scopedTestEntityPage.getPage();
			pageSize = scopedTestEntityPage.getPageSize();
			totalCount = scopedTestEntityPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ScopedTestEntity> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("SiteTestEntityPage")
	public class SiteTestEntityPage {

		public SiteTestEntityPage(Page siteTestEntityPage) {
			actions = siteTestEntityPage.getActions();

			items = siteTestEntityPage.getItems();
			lastPage = siteTestEntityPage.getLastPage();
			page = siteTestEntityPage.getPage();
			pageSize = siteTestEntityPage.getPageSize();
			totalCount = siteTestEntityPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<SiteTestEntity> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("SortPage")
	public class SortPage {

		public SortPage(Page sortPage) {
			actions = sortPage.getActions();

			items = sortPage.getItems();
			lastPage = sortPage.getLastPage();
			page = sortPage.getPage();
			pageSize = sortPage.getPageSize();
			totalCount = sortPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Sort> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("TestEntityPage")
	public class TestEntityPage {

		public TestEntityPage(Page testEntityPage) {
			actions = testEntityPage.getActions();

			items = testEntityPage.getItems();
			lastPage = testEntityPage.getLastPage();
			page = testEntityPage.getPage();
			pageSize = testEntityPage.getPageSize();
			totalCount = testEntityPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<TestEntity> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("TestEntityAddressPage")
	public class TestEntityAddressPage {

		public TestEntityAddressPage(Page testEntityAddressPage) {
			actions = testEntityAddressPage.getActions();

			items = testEntityAddressPage.getItems();
			lastPage = testEntityAddressPage.getLastPage();
			page = testEntityAddressPage.getPage();
			pageSize = testEntityAddressPage.getPageSize();
			totalCount = testEntityAddressPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<TestEntityAddress> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

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
		assetLibraryTestEntityResource.setResourceActionLocalService(
			_resourceActionLocalService);
		assetLibraryTestEntityResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		assetLibraryTestEntityResource.setRoleLocalService(_roleLocalService);
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
		batchTestEntityResource.setResourceActionLocalService(
			_resourceActionLocalService);
		batchTestEntityResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		batchTestEntityResource.setRoleLocalService(_roleLocalService);
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
		companyTestEntityResource.setResourceActionLocalService(
			_resourceActionLocalService);
		companyTestEntityResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		companyTestEntityResource.setRoleLocalService(_roleLocalService);
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
		ercAssetLibraryTestEntityResource.setResourceActionLocalService(
			_resourceActionLocalService);
		ercAssetLibraryTestEntityResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		ercAssetLibraryTestEntityResource.setRoleLocalService(
			_roleLocalService);
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
		ercScopedTestEntityResource.setResourceActionLocalService(
			_resourceActionLocalService);
		ercScopedTestEntityResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		ercScopedTestEntityResource.setRoleLocalService(_roleLocalService);
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
		ercSiteTestEntityResource.setResourceActionLocalService(
			_resourceActionLocalService);
		ercSiteTestEntityResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		ercSiteTestEntityResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			EntityModelResourceTestEntity1Resource
				entityModelResourceTestEntity1Resource)
		throws Exception {

		entityModelResourceTestEntity1Resource.setContextAcceptLanguage(
			_acceptLanguage);
		entityModelResourceTestEntity1Resource.setContextCompany(_company);
		entityModelResourceTestEntity1Resource.setContextHttpServletRequest(
			_httpServletRequest);
		entityModelResourceTestEntity1Resource.setContextHttpServletResponse(
			_httpServletResponse);
		entityModelResourceTestEntity1Resource.setContextUriInfo(_uriInfo);
		entityModelResourceTestEntity1Resource.setContextUser(_user);
		entityModelResourceTestEntity1Resource.setGroupLocalService(
			_groupLocalService);
		entityModelResourceTestEntity1Resource.setResourceActionLocalService(
			_resourceActionLocalService);
		entityModelResourceTestEntity1Resource.
			setResourcePermissionLocalService(_resourcePermissionLocalService);
		entityModelResourceTestEntity1Resource.setRoleLocalService(
			_roleLocalService);
	}

	private void _populateResourceContext(
			EntityModelResourceTestEntity2Resource
				entityModelResourceTestEntity2Resource)
		throws Exception {

		entityModelResourceTestEntity2Resource.setContextAcceptLanguage(
			_acceptLanguage);
		entityModelResourceTestEntity2Resource.setContextCompany(_company);
		entityModelResourceTestEntity2Resource.setContextHttpServletRequest(
			_httpServletRequest);
		entityModelResourceTestEntity2Resource.setContextHttpServletResponse(
			_httpServletResponse);
		entityModelResourceTestEntity2Resource.setContextUriInfo(_uriInfo);
		entityModelResourceTestEntity2Resource.setContextUser(_user);
		entityModelResourceTestEntity2Resource.setGroupLocalService(
			_groupLocalService);
		entityModelResourceTestEntity2Resource.setResourceActionLocalService(
			_resourceActionLocalService);
		entityModelResourceTestEntity2Resource.
			setResourcePermissionLocalService(_resourcePermissionLocalService);
		entityModelResourceTestEntity2Resource.setRoleLocalService(
			_roleLocalService);
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
		filterResource.setResourceActionLocalService(
			_resourceActionLocalService);
		filterResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		filterResource.setRoleLocalService(_roleLocalService);
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
		multipartTestEntityResource.setResourceActionLocalService(
			_resourceActionLocalService);
		multipartTestEntityResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		multipartTestEntityResource.setRoleLocalService(_roleLocalService);
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
		schemaResource.setResourceActionLocalService(
			_resourceActionLocalService);
		schemaResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		schemaResource.setRoleLocalService(_roleLocalService);
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
		scopedTestEntityResource.setResourceActionLocalService(
			_resourceActionLocalService);
		scopedTestEntityResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		scopedTestEntityResource.setRoleLocalService(_roleLocalService);
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
		siteTestEntityResource.setResourceActionLocalService(
			_resourceActionLocalService);
		siteTestEntityResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		siteTestEntityResource.setRoleLocalService(_roleLocalService);
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
		sortResource.setResourceActionLocalService(_resourceActionLocalService);
		sortResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		sortResource.setRoleLocalService(_roleLocalService);
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
		testEntityResource.setResourceActionLocalService(
			_resourceActionLocalService);
		testEntityResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		testEntityResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			TestEntityAddressResource testEntityAddressResource)
		throws Exception {

		testEntityAddressResource.setContextAcceptLanguage(_acceptLanguage);
		testEntityAddressResource.setContextCompany(_company);
		testEntityAddressResource.setContextHttpServletRequest(
			_httpServletRequest);
		testEntityAddressResource.setContextHttpServletResponse(
			_httpServletResponse);
		testEntityAddressResource.setContextUriInfo(_uriInfo);
		testEntityAddressResource.setContextUser(_user);
		testEntityAddressResource.setGroupLocalService(_groupLocalService);
		testEntityAddressResource.setResourceActionLocalService(
			_resourceActionLocalService);
		testEntityAddressResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		testEntityAddressResource.setRoleLocalService(_roleLocalService);
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
	private static ComponentServiceObjects
		<EntityModelResourceTestEntity1Resource>
			_entityModelResourceTestEntity1ResourceComponentServiceObjects;
	private static ComponentServiceObjects
		<EntityModelResourceTestEntity2Resource>
			_entityModelResourceTestEntity2ResourceComponentServiceObjects;
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
	private static ComponentServiceObjects<TestEntityAddressResource>
		_testEntityAddressResourceComponentServiceObjects;

	private AcceptLanguage _acceptLanguage;
	private com.liferay.portal.kernel.model.Company _company;
	private BiFunction
		<Object, String, com.liferay.portal.kernel.search.filter.Filter>
			_filterBiFunction;
	private GroupLocalService _groupLocalService;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private ResourceActionLocalService _resourceActionLocalService;
	private ResourcePermissionLocalService _resourcePermissionLocalService;
	private RoleLocalService _roleLocalService;
	private BiFunction<Object, String, com.liferay.portal.kernel.search.Sort[]>
		_sortsBiFunction;
	private UriInfo _uriInfo;
	private com.liferay.portal.kernel.model.User _user;

}