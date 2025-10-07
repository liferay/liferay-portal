/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.taxonomy.internal.resource.v1_0;

import com.liferay.headless.admin.taxonomy.dto.v1_0.TaxonomyCategory;
import com.liferay.headless.admin.taxonomy.resource.v1_0.TaxonomyCategoryResource;
import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Resource;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.PermissionServiceUtil;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourceLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.GroupThreadLocal;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.filter.ExpressionConvert;
import com.liferay.portal.odata.filter.FilterParser;
import com.liferay.portal.odata.filter.FilterParserProvider;
import com.liferay.portal.odata.sort.SortField;
import com.liferay.portal.odata.sort.SortParser;
import com.liferay.portal.odata.sort.SortParserProvider;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.VulcanBatchEngineTaskItemDelegate;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.permission.ModelPermissionsUtil;
import com.liferay.portal.vulcan.permission.Permission;
import com.liferay.portal.vulcan.resource.EntityModelResource;
import com.liferay.portal.vulcan.util.ActionUtil;
import com.liferay.portal.vulcan.util.UriInfoUtil;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.NotSupportedException;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.io.Serializable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@jakarta.ws.rs.Path("/v1.0")
public abstract class BaseTaxonomyCategoryResourceImpl
	implements EntityModelResource, TaxonomyCategoryResource,
			   VulcanBatchEngineTaskItemDelegate<TaxonomyCategory> {

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-admin-taxonomy/v1.0/asset-libraries/{assetLibraryId}/taxonomy-categories/by-external-reference-code/{externalReferenceCode}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Deletes the asset library's taxonomy category by external reference code."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "assetLibraryId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {
			@io.swagger.v3.oas.annotations.tags.Tag(name = "TaxonomyCategory")
		}
	)
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path(
		"/asset-libraries/{assetLibraryId}/taxonomy-categories/by-external-reference-code/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public void deleteAssetLibraryTaxonomyCategoryByExternalReferenceCode(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("assetLibraryId")
			Long assetLibraryId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode)
		throws Exception {
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-admin-taxonomy/v1.0/sites/{siteId}/taxonomy-categories/by-external-reference-code/{externalReferenceCode}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Deletes the site's taxonomy category by external reference code."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "siteId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {
			@io.swagger.v3.oas.annotations.tags.Tag(name = "TaxonomyCategory")
		}
	)
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path(
		"/sites/{siteId}/taxonomy-categories/by-external-reference-code/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public void deleteSiteTaxonomyCategoryByExternalReferenceCode(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("siteId")
			Long siteId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode)
		throws Exception {
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-admin-taxonomy/v1.0/taxonomy-categories/{taxonomyCategoryId}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Deletes the taxonomy category and returns a 204 if the operation succeeds."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "taxonomyCategoryId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {
			@io.swagger.v3.oas.annotations.tags.Tag(name = "TaxonomyCategory")
		}
	)
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path("/taxonomy-categories/{taxonomyCategoryId}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public void deleteTaxonomyCategory(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("taxonomyCategoryId")
			String taxonomyCategoryId)
		throws Exception {
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-admin-taxonomy/v1.0/taxonomy-categories/batch'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "callbackURL"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {
			@io.swagger.v3.oas.annotations.tags.Tag(name = "TaxonomyCategory")
		}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path("/taxonomy-categories/batch")
	@jakarta.ws.rs.Produces("application/json")
	@Override
	public Response deleteTaxonomyCategoryBatch(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("callbackURL")
			String callbackURL,
			Object object)
		throws Exception {

		vulcanBatchEngineImportTaskResource.setContextAcceptLanguage(
			contextAcceptLanguage);
		vulcanBatchEngineImportTaskResource.setContextCompany(contextCompany);
		vulcanBatchEngineImportTaskResource.setContextHttpServletRequest(
			contextHttpServletRequest);
		vulcanBatchEngineImportTaskResource.setContextUriInfo(contextUriInfo);
		vulcanBatchEngineImportTaskResource.setContextUser(contextUser);

		Response.ResponseBuilder responseBuilder = Response.accepted();

		return responseBuilder.entity(
			vulcanBatchEngineImportTaskResource.deleteImportTask(
				TaxonomyCategory.class.getName(), callbackURL, object)
		).build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'DELETE' 'http://localhost:8080/o/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/{taxonomyVocabularyId}/taxonomy-categories/by-external-reference-code/{externalReferenceCode}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Deletes the site's taxonomy category by external reference code."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "taxonomyVocabularyId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {
			@io.swagger.v3.oas.annotations.tags.Tag(name = "TaxonomyCategory")
		}
	)
	@jakarta.ws.rs.DELETE
	@jakarta.ws.rs.Path(
		"/taxonomy-vocabularies/{taxonomyVocabularyId}/taxonomy-categories/by-external-reference-code/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public void deleteTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("taxonomyVocabularyId")
			Long taxonomyVocabularyId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("externalReferenceCode")
			String externalReferenceCode)
		throws Exception {
	}

	protected abstract Page<TaxonomyCategory>
			doGetAssetLibraryTaxonomyCategoriesPage(
				Long assetLibraryId, String search,
				com.liferay.portal.vulcan.aggregation.Aggregation aggregation,
				com.liferay.portal.kernel.search.filter.Filter filter,
				Pagination pagination,
				com.liferay.portal.kernel.search.Sort[] sorts)
		throws Exception;

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-admin-taxonomy/v1.0/asset-libraries/{assetLibraryId}/taxonomy-categories'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Retrieves the asset library's taxonomy categories."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "assetLibraryId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "aggregationTerms"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "fields"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "filter"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "page"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "pageSize"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "restrictFields"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "search"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "sort"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {
			@io.swagger.v3.oas.annotations.tags.Tag(name = "TaxonomyCategory")
		}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/asset-libraries/{assetLibraryId}/taxonomy-categories")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public final Page<TaxonomyCategory> getAssetLibraryTaxonomyCategoriesPage(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("assetLibraryId")
			Long assetLibraryId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("search")
			String search,
			@jakarta.ws.rs.core.Context
				com.liferay.portal.vulcan.aggregation.Aggregation aggregation,
			@jakarta.ws.rs.core.Context
				com.liferay.portal.kernel.search.filter.Filter filter,
			@jakarta.ws.rs.core.Context Pagination pagination,
			@jakarta.ws.rs.core.Context com.liferay.portal.kernel.search.Sort[]
				sorts)
		throws Exception {

		Page<TaxonomyCategory> taxonomyCategoriesPage =
			doGetAssetLibraryTaxonomyCategoriesPage(
				assetLibraryId, search, aggregation, filter, pagination, sorts);

		for (TaxonomyCategory taxonomyCategory :
				taxonomyCategoriesPage.getItems()) {

			taxonomyCategory.setPermissions(
				() -> NestedFieldsSupplier.supply(
					"permissions",
					nestedField -> {
						Page<Permission> permissionsPage =
							getTaxonomyCategoryPermissionsPage(
								taxonomyCategory.getId(), null);

						Collection<Permission> permissions =
							permissionsPage.getItems();

						return permissions.toArray(
							new Permission[permissions.size()]);
					}));
		}

		return taxonomyCategoriesPage;
	}

	protected abstract TaxonomyCategory
			doGetAssetLibraryTaxonomyCategoryByExternalReferenceCode(
				Long assetLibraryId, String externalReferenceCode)
		throws Exception;

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-admin-taxonomy/v1.0/asset-libraries/{assetLibraryId}/taxonomy-categories/by-external-reference-code/{externalReferenceCode}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Retrieves the asset library's taxonomy category by external reference code."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "assetLibraryId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "fields"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "restrictFields"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {
			@io.swagger.v3.oas.annotations.tags.Tag(name = "TaxonomyCategory")
		}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path(
		"/asset-libraries/{assetLibraryId}/taxonomy-categories/by-external-reference-code/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public final TaxonomyCategory
			getAssetLibraryTaxonomyCategoryByExternalReferenceCode(
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("assetLibraryId")
				Long assetLibraryId,
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("externalReferenceCode")
				String externalReferenceCode)
		throws Exception {

		TaxonomyCategory getTaxonomyCategory =
			doGetAssetLibraryTaxonomyCategoryByExternalReferenceCode(
				assetLibraryId, externalReferenceCode);

		getTaxonomyCategory.setPermissions(
			() -> NestedFieldsSupplier.supply(
				"permissions",
				nestedField -> {
					Page<Permission> permissionsPage =
						getTaxonomyCategoryPermissionsPage(
							getTaxonomyCategory.getId(), null);

					Collection<Permission> permissions =
						permissionsPage.getItems();

					return permissions.toArray(
						new Permission[permissions.size()]);
				}));

		return getTaxonomyCategory;
	}

	protected abstract Page<TaxonomyCategory> doGetSiteTaxonomyCategoriesPage(
			Long siteId, String search,
			com.liferay.portal.vulcan.aggregation.Aggregation aggregation,
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[] sorts)
		throws Exception;

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-admin-taxonomy/v1.0/sites/{siteId}/taxonomy-categories'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Retrieves the site's taxonomy categories."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "siteId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "aggregationTerms"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "fields"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "filter"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "page"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "pageSize"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "restrictFields"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "search"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "sort"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {
			@io.swagger.v3.oas.annotations.tags.Tag(name = "TaxonomyCategory")
		}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/sites/{siteId}/taxonomy-categories")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public final Page<TaxonomyCategory> getSiteTaxonomyCategoriesPage(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("siteId")
			Long siteId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("search")
			String search,
			@jakarta.ws.rs.core.Context
				com.liferay.portal.vulcan.aggregation.Aggregation aggregation,
			@jakarta.ws.rs.core.Context
				com.liferay.portal.kernel.search.filter.Filter filter,
			@jakarta.ws.rs.core.Context Pagination pagination,
			@jakarta.ws.rs.core.Context com.liferay.portal.kernel.search.Sort[]
				sorts)
		throws Exception {

		Page<TaxonomyCategory> taxonomyCategoriesPage =
			doGetSiteTaxonomyCategoriesPage(
				siteId, search, aggregation, filter, pagination, sorts);

		for (TaxonomyCategory taxonomyCategory :
				taxonomyCategoriesPage.getItems()) {

			taxonomyCategory.setPermissions(
				() -> NestedFieldsSupplier.supply(
					"permissions",
					nestedField -> {
						Page<Permission> permissionsPage =
							getTaxonomyCategoryPermissionsPage(
								taxonomyCategory.getId(), null);

						Collection<Permission> permissions =
							permissionsPage.getItems();

						return permissions.toArray(
							new Permission[permissions.size()]);
					}));
		}

		return taxonomyCategoriesPage;
	}

	protected abstract TaxonomyCategory
			doGetSiteTaxonomyCategoryByExternalReferenceCode(
				Long siteId, String externalReferenceCode)
		throws Exception;

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-admin-taxonomy/v1.0/sites/{siteId}/taxonomy-categories/by-external-reference-code/{externalReferenceCode}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Retrieves the site's taxonomy category by external reference code."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "siteId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "fields"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "restrictFields"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {
			@io.swagger.v3.oas.annotations.tags.Tag(name = "TaxonomyCategory")
		}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path(
		"/sites/{siteId}/taxonomy-categories/by-external-reference-code/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public final TaxonomyCategory
			getSiteTaxonomyCategoryByExternalReferenceCode(
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("siteId")
				Long siteId,
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("externalReferenceCode")
				String externalReferenceCode)
		throws Exception {

		TaxonomyCategory getTaxonomyCategory =
			doGetSiteTaxonomyCategoryByExternalReferenceCode(
				siteId, externalReferenceCode);

		getTaxonomyCategory.setPermissions(
			() -> NestedFieldsSupplier.supply(
				"permissions",
				nestedField -> {
					Page<Permission> permissionsPage =
						getTaxonomyCategoryPermissionsPage(
							getTaxonomyCategory.getId(), null);

					Collection<Permission> permissions =
						permissionsPage.getItems();

					return permissions.toArray(
						new Permission[permissions.size()]);
				}));

		return getTaxonomyCategory;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-admin-taxonomy/v1.0/taxonomy-categories/ranked'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "fields"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "page"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "pageSize"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "restrictFields"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "siteId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {
			@io.swagger.v3.oas.annotations.tags.Tag(name = "TaxonomyCategory")
		}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/taxonomy-categories/ranked")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Page<TaxonomyCategory> getTaxonomyCategoriesRankedPage(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("siteId")
			Long siteId,
			@jakarta.ws.rs.core.Context Pagination pagination)
		throws Exception {

		return Page.of(Collections.emptyList());
	}

	protected abstract TaxonomyCategory doGetTaxonomyCategory(
			String taxonomyCategoryId)
		throws Exception;

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-admin-taxonomy/v1.0/taxonomy-categories/{taxonomyCategoryId}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Retrieves a taxonomy category."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "taxonomyCategoryId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "fields"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "restrictFields"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {
			@io.swagger.v3.oas.annotations.tags.Tag(name = "TaxonomyCategory")
		}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/taxonomy-categories/{taxonomyCategoryId}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public final TaxonomyCategory getTaxonomyCategory(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("taxonomyCategoryId")
			String taxonomyCategoryId)
		throws Exception {

		TaxonomyCategory getTaxonomyCategory = doGetTaxonomyCategory(
			taxonomyCategoryId);

		getTaxonomyCategory.setPermissions(
			() -> NestedFieldsSupplier.supply(
				"permissions",
				nestedField -> {
					Page<Permission> permissionsPage =
						getTaxonomyCategoryPermissionsPage(
							getTaxonomyCategory.getId(), null);

					Collection<Permission> permissions =
						permissionsPage.getItems();

					return permissions.toArray(
						new Permission[permissions.size()]);
				}));

		return getTaxonomyCategory;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-admin-taxonomy/v1.0/taxonomy-categories/{taxonomyCategoryId}/permissions'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "taxonomyCategoryId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "fields"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "restrictFields"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "roleNames"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {
			@io.swagger.v3.oas.annotations.tags.Tag(name = "TaxonomyCategory")
		}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path("/taxonomy-categories/{taxonomyCategoryId}/permissions")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Page<Permission> getTaxonomyCategoryPermissionsPage(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("taxonomyCategoryId")
			String taxonomyCategoryId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("roleNames")
			String roleNames)
		throws Exception {

		Long groupId = getPermissionCheckerGroupId(taxonomyCategoryId);
		String resourceName = getPermissionCheckerResourceName(
			taxonomyCategoryId);
		Long resourceId = getPermissionCheckerResourceId(taxonomyCategoryId);

		PermissionServiceUtil.checkPermission(
			groupId, resourceName, resourceId);

		return toPermissionPage(
			HashMapBuilder.put(
				"get",
				addAction(
					ActionKeys.PERMISSIONS, resourceId,
					"getTaxonomyCategoryPermissionsPage", null, resourceName,
					groupId)
			).put(
				"replace",
				addAction(
					ActionKeys.PERMISSIONS, resourceId,
					"putTaxonomyCategoryPermissionsPage", null, resourceName,
					groupId)
			).build(),
			resourceId, resourceName, roleNames);
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-admin-taxonomy/v1.0/taxonomy-categories/{parentTaxonomyCategoryId}/taxonomy-categories'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Retrieves a taxonomy category's child taxonomy categories. Results can be paginated, filtered, searched, and sorted."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "parentTaxonomyCategoryId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "aggregationTerms"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "fields"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "filter"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "page"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "pageSize"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "restrictFields"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "search"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "sort"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {
			@io.swagger.v3.oas.annotations.tags.Tag(name = "TaxonomyCategory")
		}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path(
		"/taxonomy-categories/{parentTaxonomyCategoryId}/taxonomy-categories"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public Page<TaxonomyCategory> getTaxonomyCategoryTaxonomyCategoriesPage(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("parentTaxonomyCategoryId")
			String parentTaxonomyCategoryId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("search")
			String search,
			@jakarta.ws.rs.core.Context
				com.liferay.portal.vulcan.aggregation.Aggregation aggregation,
			@jakarta.ws.rs.core.Context
				com.liferay.portal.kernel.search.filter.Filter filter,
			@jakarta.ws.rs.core.Context Pagination pagination,
			@jakarta.ws.rs.core.Context com.liferay.portal.kernel.search.Sort[]
				sorts)
		throws Exception {

		return Page.of(Collections.emptyList());
	}

	protected abstract Page<TaxonomyCategory>
			doGetTaxonomyVocabularyTaxonomyCategoriesPage(
				Long taxonomyVocabularyId, Boolean flatten, String search,
				com.liferay.portal.vulcan.aggregation.Aggregation aggregation,
				com.liferay.portal.kernel.search.filter.Filter filter,
				Pagination pagination,
				com.liferay.portal.kernel.search.Sort[] sorts)
		throws Exception;

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/{taxonomyVocabularyId}/taxonomy-categories'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Retrieves a vocabulary's taxonomy categories. Results can be paginated, filtered, searched, and sorted."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "taxonomyVocabularyId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "aggregationTerms"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "fields"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "filter"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "flatten"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "page"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "pageSize"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "restrictFields"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "search"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "sort"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {
			@io.swagger.v3.oas.annotations.tags.Tag(name = "TaxonomyCategory")
		}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path(
		"/taxonomy-vocabularies/{taxonomyVocabularyId}/taxonomy-categories"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public final Page<TaxonomyCategory>
			getTaxonomyVocabularyTaxonomyCategoriesPage(
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("taxonomyVocabularyId")
				Long taxonomyVocabularyId,
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.ws.rs.QueryParam("flatten")
				Boolean flatten,
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.ws.rs.QueryParam("search")
				String search,
				@jakarta.ws.rs.core.Context
					com.liferay.portal.vulcan.aggregation.Aggregation
						aggregation,
				@jakarta.ws.rs.core.Context
					com.liferay.portal.kernel.search.filter.Filter filter,
				@jakarta.ws.rs.core.Context Pagination pagination,
				@jakarta.ws.rs.core.Context
					com.liferay.portal.kernel.search.Sort[] sorts)
		throws Exception {

		Page<TaxonomyCategory> taxonomyCategoriesPage =
			doGetTaxonomyVocabularyTaxonomyCategoriesPage(
				taxonomyVocabularyId, flatten, search, aggregation, filter,
				pagination, sorts);

		for (TaxonomyCategory taxonomyCategory :
				taxonomyCategoriesPage.getItems()) {

			taxonomyCategory.setPermissions(
				() -> NestedFieldsSupplier.supply(
					"permissions",
					nestedField -> {
						Page<Permission> permissionsPage =
							getTaxonomyCategoryPermissionsPage(
								taxonomyCategory.getId(), null);

						Collection<Permission> permissions =
							permissionsPage.getItems();

						return permissions.toArray(
							new Permission[permissions.size()]);
					}));
		}

		return taxonomyCategoriesPage;
	}

	protected abstract TaxonomyCategory
			doGetTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode(
				Long taxonomyVocabularyId, String externalReferenceCode)
		throws Exception;

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'GET' 'http://localhost:8080/o/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/{taxonomyVocabularyId}/taxonomy-categories/by-external-reference-code/{externalReferenceCode}'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Retrieves the site's taxonomy category by external reference code."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "taxonomyVocabularyId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "fields"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "restrictFields"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {
			@io.swagger.v3.oas.annotations.tags.Tag(name = "TaxonomyCategory")
		}
	)
	@jakarta.ws.rs.GET
	@jakarta.ws.rs.Path(
		"/taxonomy-vocabularies/{taxonomyVocabularyId}/taxonomy-categories/by-external-reference-code/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public final TaxonomyCategory
			getTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode(
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("taxonomyVocabularyId")
				Long taxonomyVocabularyId,
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("externalReferenceCode")
				String externalReferenceCode)
		throws Exception {

		TaxonomyCategory getTaxonomyCategory =
			doGetTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode(
				taxonomyVocabularyId, externalReferenceCode);

		getTaxonomyCategory.setPermissions(
			() -> NestedFieldsSupplier.supply(
				"permissions",
				nestedField -> {
					Page<Permission> permissionsPage =
						getTaxonomyCategoryPermissionsPage(
							getTaxonomyCategory.getId(), null);

					Collection<Permission> permissions =
						permissionsPage.getItems();

					return permissions.toArray(
						new Permission[permissions.size()]);
				}));

		return getTaxonomyCategory;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PATCH' 'http://localhost:8080/o/headless-admin-taxonomy/v1.0/taxonomy-categories/{taxonomyCategoryId}' -d $'{"description": ___, "description_i18n": ___, "externalReferenceCode": ___, "name": ___, "name_i18n": ___, "parentTaxonomyCategory": ___, "parentTaxonomyVocabulary": ___, "permissions": ___, "taxonomyCategoryProperties": ___, "taxonomyVocabularyId": ___, "viewableBy": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Updates only the fields received in the request body. Other fields are left untouched."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "taxonomyCategoryId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {
			@io.swagger.v3.oas.annotations.tags.Tag(name = "TaxonomyCategory")
		}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.PATCH
	@jakarta.ws.rs.Path("/taxonomy-categories/{taxonomyCategoryId}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public TaxonomyCategory patchTaxonomyCategory(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("taxonomyCategoryId")
			String taxonomyCategoryId,
			TaxonomyCategory taxonomyCategory)
		throws Exception {

		TaxonomyCategory existingTaxonomyCategory = getTaxonomyCategory(
			taxonomyCategoryId);

		if (taxonomyCategory.getDescription() != null) {
			existingTaxonomyCategory.setDescription(
				taxonomyCategory.getDescription());
		}

		if (taxonomyCategory.getDescription_i18n() != null) {
			existingTaxonomyCategory.setDescription_i18n(
				taxonomyCategory.getDescription_i18n());
		}

		if (taxonomyCategory.getExternalReferenceCode() != null) {
			existingTaxonomyCategory.setExternalReferenceCode(
				taxonomyCategory.getExternalReferenceCode());
		}

		if (taxonomyCategory.getName() != null) {
			existingTaxonomyCategory.setName(taxonomyCategory.getName());
		}

		if (taxonomyCategory.getName_i18n() != null) {
			existingTaxonomyCategory.setName_i18n(
				taxonomyCategory.getName_i18n());
		}

		if (taxonomyCategory.getPermissions() != null) {
			existingTaxonomyCategory.setPermissions(
				taxonomyCategory.getPermissions());
		}

		if (taxonomyCategory.getTaxonomyVocabularyId() != null) {
			existingTaxonomyCategory.setTaxonomyVocabularyId(
				taxonomyCategory.getTaxonomyVocabularyId());
		}

		if (taxonomyCategory.getViewableBy() != null) {
			existingTaxonomyCategory.setViewableBy(
				taxonomyCategory.getViewableBy());
		}

		preparePatch(taxonomyCategory, existingTaxonomyCategory);

		return putTaxonomyCategory(
			taxonomyCategoryId, existingTaxonomyCategory);
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-admin-taxonomy/v1.0/asset-libraries/{assetLibraryId}/taxonomy-categories/export-batch'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "assetLibraryId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "filter"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "search"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "sort"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "callbackURL"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "contentType"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "fieldNames"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {
			@io.swagger.v3.oas.annotations.tags.Tag(name = "TaxonomyCategory")
		}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.Path(
		"/asset-libraries/{assetLibraryId}/taxonomy-categories/export-batch"
	)
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces("application/json")
	@Override
	public Response postAssetLibraryTaxonomyCategoriesPageExportBatch(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("assetLibraryId")
			Long assetLibraryId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("search")
			String search,
			@jakarta.ws.rs.core.Context
				com.liferay.portal.kernel.search.filter.Filter filter,
			@jakarta.ws.rs.core.Context com.liferay.portal.kernel.search.Sort[]
				sorts,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("callbackURL")
			String callbackURL,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.DefaultValue("JSON")
			@jakarta.ws.rs.QueryParam("contentType")
			String contentType,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("fieldNames")
			String fieldNames)
		throws Exception {

		vulcanBatchEngineExportTaskResource.setContextAcceptLanguage(
			contextAcceptLanguage);
		vulcanBatchEngineExportTaskResource.setContextCompany(contextCompany);
		vulcanBatchEngineExportTaskResource.setContextHttpServletRequest(
			contextHttpServletRequest);
		vulcanBatchEngineExportTaskResource.setContextUriInfo(contextUriInfo);
		vulcanBatchEngineExportTaskResource.setContextUser(contextUser);
		vulcanBatchEngineExportTaskResource.setGroupLocalService(
			groupLocalService);

		Response.ResponseBuilder responseBuilder = Response.accepted();

		return responseBuilder.entity(
			vulcanBatchEngineExportTaskResource.postExportTask(
				TaxonomyCategory.class.getName(), callbackURL, contentType,
				fieldNames)
		).build();
	}

	protected abstract TaxonomyCategory doPostAssetLibraryTaxonomyCategory(
			Long assetLibraryId, TaxonomyCategory taxonomyCategory)
		throws Exception;

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-admin-taxonomy/v1.0/asset-libraries/{assetLibraryId}/taxonomy-categories' -d $'{"description": ___, "description_i18n": ___, "externalReferenceCode": ___, "name": ___, "name_i18n": ___, "parentTaxonomyCategory": ___, "parentTaxonomyVocabulary": ___, "permissions": ___, "taxonomyCategoryProperties": ___, "taxonomyVocabularyId": ___, "viewableBy": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Inserts a new Category in a Scope."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "assetLibraryId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {
			@io.swagger.v3.oas.annotations.tags.Tag(name = "TaxonomyCategory")
		}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path("/asset-libraries/{assetLibraryId}/taxonomy-categories")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public final TaxonomyCategory postAssetLibraryTaxonomyCategory(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("assetLibraryId")
			Long assetLibraryId,
			TaxonomyCategory taxonomyCategory)
		throws Exception {

		Permission[] permissions = taxonomyCategory.getPermissions();

		TaxonomyCategory postTaxonomyCategory =
			doPostAssetLibraryTaxonomyCategory(
				assetLibraryId, taxonomyCategory);

		if (permissions != null) {
			Page<Permission> permissionsPage =
				putTaxonomyCategoryPermissionsPage(
					postTaxonomyCategory.getId(), permissions);

			postTaxonomyCategory.setPermissions(
				() -> NestedFieldsSupplier.supply(
					"permissions",
					nestedField -> {
						Collection<Permission> collection =
							permissionsPage.getItems();

						return collection.toArray(
							new Permission[collection.size()]);
					}));
		}

		return postTaxonomyCategory;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-admin-taxonomy/v1.0/asset-libraries/{assetLibraryId}/taxonomy-categories/batch'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "assetLibraryId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "callbackURL"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {
			@io.swagger.v3.oas.annotations.tags.Tag(name = "TaxonomyCategory")
		}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.Path(
		"/asset-libraries/{assetLibraryId}/taxonomy-categories/batch"
	)
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces("application/json")
	@Override
	public Response postAssetLibraryTaxonomyCategoryBatch(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("assetLibraryId")
			Long assetLibraryId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("callbackURL")
			String callbackURL,
			Object object)
		throws Exception {

		vulcanBatchEngineImportTaskResource.setContextAcceptLanguage(
			contextAcceptLanguage);
		vulcanBatchEngineImportTaskResource.setContextCompany(contextCompany);
		vulcanBatchEngineImportTaskResource.setContextHttpServletRequest(
			contextHttpServletRequest);
		vulcanBatchEngineImportTaskResource.setContextUriInfo(contextUriInfo);
		vulcanBatchEngineImportTaskResource.setContextUser(contextUser);

		Response.ResponseBuilder responseBuilder = Response.accepted();

		return responseBuilder.entity(
			vulcanBatchEngineImportTaskResource.postImportTask(
				TaxonomyCategory.class.getName(), callbackURL, null, object)
		).build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-admin-taxonomy/v1.0/sites/{siteId}/taxonomy-categories/export-batch'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "siteId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "filter"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "search"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "sort"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "callbackURL"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "contentType"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "fieldNames"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {
			@io.swagger.v3.oas.annotations.tags.Tag(name = "TaxonomyCategory")
		}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.Path("/sites/{siteId}/taxonomy-categories/export-batch")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces("application/json")
	@Override
	public Response postSiteTaxonomyCategoriesPageExportBatch(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("siteId")
			Long siteId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("search")
			String search,
			@jakarta.ws.rs.core.Context
				com.liferay.portal.kernel.search.filter.Filter filter,
			@jakarta.ws.rs.core.Context com.liferay.portal.kernel.search.Sort[]
				sorts,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("callbackURL")
			String callbackURL,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.DefaultValue("JSON")
			@jakarta.ws.rs.QueryParam("contentType")
			String contentType,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("fieldNames")
			String fieldNames)
		throws Exception {

		vulcanBatchEngineExportTaskResource.setContextAcceptLanguage(
			contextAcceptLanguage);
		vulcanBatchEngineExportTaskResource.setContextCompany(contextCompany);
		vulcanBatchEngineExportTaskResource.setContextHttpServletRequest(
			contextHttpServletRequest);
		vulcanBatchEngineExportTaskResource.setContextUriInfo(contextUriInfo);
		vulcanBatchEngineExportTaskResource.setContextUser(contextUser);
		vulcanBatchEngineExportTaskResource.setGroupLocalService(
			groupLocalService);

		Response.ResponseBuilder responseBuilder = Response.accepted();

		return responseBuilder.entity(
			vulcanBatchEngineExportTaskResource.postExportTask(
				TaxonomyCategory.class.getName(), callbackURL, contentType,
				fieldNames)
		).build();
	}

	protected abstract TaxonomyCategory doPostSiteTaxonomyCategory(
			Long siteId, TaxonomyCategory taxonomyCategory)
		throws Exception;

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-admin-taxonomy/v1.0/sites/{siteId}/taxonomy-categories' -d $'{"description": ___, "description_i18n": ___, "externalReferenceCode": ___, "name": ___, "name_i18n": ___, "parentTaxonomyCategory": ___, "parentTaxonomyVocabulary": ___, "permissions": ___, "taxonomyCategoryProperties": ___, "taxonomyVocabularyId": ___, "viewableBy": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Inserts a new Category in a Scope."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "siteId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {
			@io.swagger.v3.oas.annotations.tags.Tag(name = "TaxonomyCategory")
		}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path("/sites/{siteId}/taxonomy-categories")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public final TaxonomyCategory postSiteTaxonomyCategory(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("siteId")
			Long siteId,
			TaxonomyCategory taxonomyCategory)
		throws Exception {

		Permission[] permissions = taxonomyCategory.getPermissions();

		TaxonomyCategory postTaxonomyCategory = doPostSiteTaxonomyCategory(
			siteId, taxonomyCategory);

		if (permissions != null) {
			Page<Permission> permissionsPage =
				putTaxonomyCategoryPermissionsPage(
					postTaxonomyCategory.getId(), permissions);

			postTaxonomyCategory.setPermissions(
				() -> NestedFieldsSupplier.supply(
					"permissions",
					nestedField -> {
						Collection<Permission> collection =
							permissionsPage.getItems();

						return collection.toArray(
							new Permission[collection.size()]);
					}));
		}

		return postTaxonomyCategory;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-admin-taxonomy/v1.0/sites/{siteId}/taxonomy-categories/batch'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "siteId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "callbackURL"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {
			@io.swagger.v3.oas.annotations.tags.Tag(name = "TaxonomyCategory")
		}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.Path("/sites/{siteId}/taxonomy-categories/batch")
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces("application/json")
	@Override
	public Response postSiteTaxonomyCategoryBatch(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("siteId")
			Long siteId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("callbackURL")
			String callbackURL,
			Object object)
		throws Exception {

		vulcanBatchEngineImportTaskResource.setContextAcceptLanguage(
			contextAcceptLanguage);
		vulcanBatchEngineImportTaskResource.setContextCompany(contextCompany);
		vulcanBatchEngineImportTaskResource.setContextHttpServletRequest(
			contextHttpServletRequest);
		vulcanBatchEngineImportTaskResource.setContextUriInfo(contextUriInfo);
		vulcanBatchEngineImportTaskResource.setContextUser(contextUser);

		Response.ResponseBuilder responseBuilder = Response.accepted();

		return responseBuilder.entity(
			vulcanBatchEngineImportTaskResource.postImportTask(
				TaxonomyCategory.class.getName(), callbackURL, null, object)
		).build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-admin-taxonomy/v1.0/taxonomy-categories/{parentTaxonomyCategoryId}/taxonomy-categories' -d $'{"description": ___, "description_i18n": ___, "externalReferenceCode": ___, "name": ___, "name_i18n": ___, "parentTaxonomyCategory": ___, "parentTaxonomyVocabulary": ___, "permissions": ___, "taxonomyCategoryProperties": ___, "taxonomyVocabularyId": ___, "viewableBy": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Inserts a new child taxonomy category."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "parentTaxonomyCategoryId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {
			@io.swagger.v3.oas.annotations.tags.Tag(name = "TaxonomyCategory")
		}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path(
		"/taxonomy-categories/{parentTaxonomyCategoryId}/taxonomy-categories"
	)
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public TaxonomyCategory postTaxonomyCategoryTaxonomyCategory(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("parentTaxonomyCategoryId")
			String parentTaxonomyCategoryId,
			TaxonomyCategory taxonomyCategory)
		throws Exception {

		return new TaxonomyCategory();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/{taxonomyVocabularyId}/taxonomy-categories/export-batch'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "taxonomyVocabularyId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "filter"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "search"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "sort"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "callbackURL"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "contentType"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "fieldNames"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {
			@io.swagger.v3.oas.annotations.tags.Tag(name = "TaxonomyCategory")
		}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.Path(
		"/taxonomy-vocabularies/{taxonomyVocabularyId}/taxonomy-categories/export-batch"
	)
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces("application/json")
	@Override
	public Response postTaxonomyVocabularyTaxonomyCategoriesPageExportBatch(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("taxonomyVocabularyId")
			Long taxonomyVocabularyId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("search")
			String search,
			@jakarta.ws.rs.core.Context
				com.liferay.portal.kernel.search.filter.Filter filter,
			@jakarta.ws.rs.core.Context com.liferay.portal.kernel.search.Sort[]
				sorts,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("callbackURL")
			String callbackURL,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.DefaultValue("JSON")
			@jakarta.ws.rs.QueryParam("contentType")
			String contentType,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("fieldNames")
			String fieldNames)
		throws Exception {

		vulcanBatchEngineExportTaskResource.setContextAcceptLanguage(
			contextAcceptLanguage);
		vulcanBatchEngineExportTaskResource.setContextCompany(contextCompany);
		vulcanBatchEngineExportTaskResource.setContextHttpServletRequest(
			contextHttpServletRequest);
		vulcanBatchEngineExportTaskResource.setContextUriInfo(contextUriInfo);
		vulcanBatchEngineExportTaskResource.setContextUser(contextUser);
		vulcanBatchEngineExportTaskResource.setGroupLocalService(
			groupLocalService);

		Response.ResponseBuilder responseBuilder = Response.accepted();

		return responseBuilder.entity(
			vulcanBatchEngineExportTaskResource.postExportTask(
				TaxonomyCategory.class.getName(), callbackURL, contentType,
				fieldNames)
		).build();
	}

	protected abstract TaxonomyCategory
			doPostTaxonomyVocabularyTaxonomyCategory(
				Long taxonomyVocabularyId, TaxonomyCategory taxonomyCategory)
		throws Exception;

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/{taxonomyVocabularyId}/taxonomy-categories' -d $'{"description": ___, "description_i18n": ___, "externalReferenceCode": ___, "name": ___, "name_i18n": ___, "parentTaxonomyCategory": ___, "parentTaxonomyVocabulary": ___, "permissions": ___, "taxonomyCategoryProperties": ___, "taxonomyVocabularyId": ___, "viewableBy": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Inserts a new taxonomy category in a taxonomy vocabulary."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "taxonomyVocabularyId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {
			@io.swagger.v3.oas.annotations.tags.Tag(name = "TaxonomyCategory")
		}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path(
		"/taxonomy-vocabularies/{taxonomyVocabularyId}/taxonomy-categories"
	)
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@Override
	public final TaxonomyCategory postTaxonomyVocabularyTaxonomyCategory(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("taxonomyVocabularyId")
			Long taxonomyVocabularyId,
			TaxonomyCategory taxonomyCategory)
		throws Exception {

		Permission[] permissions = taxonomyCategory.getPermissions();

		TaxonomyCategory postTaxonomyCategory =
			doPostTaxonomyVocabularyTaxonomyCategory(
				taxonomyVocabularyId, taxonomyCategory);

		if (permissions != null) {
			Page<Permission> permissionsPage =
				putTaxonomyCategoryPermissionsPage(
					postTaxonomyCategory.getId(), permissions);

			postTaxonomyCategory.setPermissions(
				() -> NestedFieldsSupplier.supply(
					"permissions",
					nestedField -> {
						Collection<Permission> collection =
							permissionsPage.getItems();

						return collection.toArray(
							new Permission[collection.size()]);
					}));
		}

		return postTaxonomyCategory;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'POST' 'http://localhost:8080/o/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/{taxonomyVocabularyId}/taxonomy-categories/batch'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "taxonomyVocabularyId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "callbackURL"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {
			@io.swagger.v3.oas.annotations.tags.Tag(name = "TaxonomyCategory")
		}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.Path(
		"/taxonomy-vocabularies/{taxonomyVocabularyId}/taxonomy-categories/batch"
	)
	@jakarta.ws.rs.POST
	@jakarta.ws.rs.Produces("application/json")
	@Override
	public Response postTaxonomyVocabularyTaxonomyCategoryBatch(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("taxonomyVocabularyId")
			Long taxonomyVocabularyId,
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("callbackURL")
			String callbackURL,
			Object object)
		throws Exception {

		vulcanBatchEngineImportTaskResource.setContextAcceptLanguage(
			contextAcceptLanguage);
		vulcanBatchEngineImportTaskResource.setContextCompany(contextCompany);
		vulcanBatchEngineImportTaskResource.setContextHttpServletRequest(
			contextHttpServletRequest);
		vulcanBatchEngineImportTaskResource.setContextUriInfo(contextUriInfo);
		vulcanBatchEngineImportTaskResource.setContextUser(contextUser);

		Response.ResponseBuilder responseBuilder = Response.accepted();

		return responseBuilder.entity(
			vulcanBatchEngineImportTaskResource.postImportTask(
				TaxonomyCategory.class.getName(), callbackURL, null, object)
		).build();
	}

	protected abstract TaxonomyCategory
			doPutAssetLibraryTaxonomyCategoryByExternalReferenceCode(
				Long assetLibraryId, String externalReferenceCode,
				TaxonomyCategory taxonomyCategory)
		throws Exception;

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PUT' 'http://localhost:8080/o/headless-admin-taxonomy/v1.0/asset-libraries/{assetLibraryId}/taxonomy-categories/by-external-reference-code/{externalReferenceCode}' -d $'{"description": ___, "description_i18n": ___, "externalReferenceCode": ___, "name": ___, "name_i18n": ___, "parentTaxonomyCategory": ___, "parentTaxonomyVocabulary": ___, "permissions": ___, "taxonomyCategoryProperties": ___, "taxonomyVocabularyId": ___, "viewableBy": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Updates the asset library's taxonomy category with the given external reference code, or creates it if it not exists."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "assetLibraryId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {
			@io.swagger.v3.oas.annotations.tags.Tag(name = "TaxonomyCategory")
		}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path(
		"/asset-libraries/{assetLibraryId}/taxonomy-categories/by-external-reference-code/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@jakarta.ws.rs.PUT
	@Override
	public final TaxonomyCategory
			putAssetLibraryTaxonomyCategoryByExternalReferenceCode(
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("assetLibraryId")
				Long assetLibraryId,
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("externalReferenceCode")
				String externalReferenceCode,
				TaxonomyCategory taxonomyCategory)
		throws Exception {

		Permission[] permissions = taxonomyCategory.getPermissions();

		TaxonomyCategory putTaxonomyCategory =
			doPutAssetLibraryTaxonomyCategoryByExternalReferenceCode(
				assetLibraryId, externalReferenceCode, taxonomyCategory);

		if (permissions != null) {
			Page<Permission> permissionsPage =
				putTaxonomyCategoryPermissionsPage(
					putTaxonomyCategory.getId(), permissions);

			putTaxonomyCategory.setPermissions(
				() -> NestedFieldsSupplier.supply(
					"permissions",
					nestedField -> {
						Collection<Permission> collection =
							permissionsPage.getItems();

						return collection.toArray(
							new Permission[collection.size()]);
					}));
		}

		return putTaxonomyCategory;
	}

	protected abstract TaxonomyCategory
			doPutSiteTaxonomyCategoryByExternalReferenceCode(
				Long siteId, String externalReferenceCode,
				TaxonomyCategory taxonomyCategory)
		throws Exception;

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PUT' 'http://localhost:8080/o/headless-admin-taxonomy/v1.0/sites/{siteId}/taxonomy-categories/by-external-reference-code/{externalReferenceCode}' -d $'{"description": ___, "description_i18n": ___, "externalReferenceCode": ___, "name": ___, "name_i18n": ___, "parentTaxonomyCategory": ___, "parentTaxonomyVocabulary": ___, "permissions": ___, "taxonomyCategoryProperties": ___, "taxonomyVocabularyId": ___, "viewableBy": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Updates the site's taxonomy category with the given external reference code, or creates it if it not exists."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "siteId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {
			@io.swagger.v3.oas.annotations.tags.Tag(name = "TaxonomyCategory")
		}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path(
		"/sites/{siteId}/taxonomy-categories/by-external-reference-code/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@jakarta.ws.rs.PUT
	@Override
	public final TaxonomyCategory
			putSiteTaxonomyCategoryByExternalReferenceCode(
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("siteId")
				Long siteId,
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("externalReferenceCode")
				String externalReferenceCode,
				TaxonomyCategory taxonomyCategory)
		throws Exception {

		Permission[] permissions = taxonomyCategory.getPermissions();

		TaxonomyCategory putTaxonomyCategory =
			doPutSiteTaxonomyCategoryByExternalReferenceCode(
				siteId, externalReferenceCode, taxonomyCategory);

		if (permissions != null) {
			Page<Permission> permissionsPage =
				putTaxonomyCategoryPermissionsPage(
					putTaxonomyCategory.getId(), permissions);

			putTaxonomyCategory.setPermissions(
				() -> NestedFieldsSupplier.supply(
					"permissions",
					nestedField -> {
						Collection<Permission> collection =
							permissionsPage.getItems();

						return collection.toArray(
							new Permission[collection.size()]);
					}));
		}

		return putTaxonomyCategory;
	}

	protected abstract TaxonomyCategory doPutTaxonomyCategory(
			String taxonomyCategoryId, TaxonomyCategory taxonomyCategory)
		throws Exception;

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PUT' 'http://localhost:8080/o/headless-admin-taxonomy/v1.0/taxonomy-categories/{taxonomyCategoryId}' -d $'{"description": ___, "description_i18n": ___, "externalReferenceCode": ___, "name": ___, "name_i18n": ___, "parentTaxonomyCategory": ___, "parentTaxonomyVocabulary": ___, "permissions": ___, "taxonomyCategoryProperties": ___, "taxonomyVocabularyId": ___, "viewableBy": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Replaces the taxonomy category with the information sent in the request body. Any missing fields are deleted unless they are required."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "taxonomyCategoryId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {
			@io.swagger.v3.oas.annotations.tags.Tag(name = "TaxonomyCategory")
		}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path("/taxonomy-categories/{taxonomyCategoryId}")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@jakarta.ws.rs.PUT
	@Override
	public final TaxonomyCategory putTaxonomyCategory(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("taxonomyCategoryId")
			String taxonomyCategoryId,
			TaxonomyCategory taxonomyCategory)
		throws Exception {

		Permission[] permissions = taxonomyCategory.getPermissions();

		TaxonomyCategory putTaxonomyCategory = doPutTaxonomyCategory(
			taxonomyCategoryId, taxonomyCategory);

		if (permissions != null) {
			Page<Permission> permissionsPage =
				putTaxonomyCategoryPermissionsPage(
					putTaxonomyCategory.getId(), permissions);

			putTaxonomyCategory.setPermissions(
				() -> NestedFieldsSupplier.supply(
					"permissions",
					nestedField -> {
						Collection<Permission> collection =
							permissionsPage.getItems();

						return collection.toArray(
							new Permission[collection.size()]);
					}));
		}

		return putTaxonomyCategory;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PUT' 'http://localhost:8080/o/headless-admin-taxonomy/v1.0/taxonomy-categories/batch'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY,
				name = "callbackURL"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {
			@io.swagger.v3.oas.annotations.tags.Tag(name = "TaxonomyCategory")
		}
	)
	@jakarta.ws.rs.Consumes("application/json")
	@jakarta.ws.rs.Path("/taxonomy-categories/batch")
	@jakarta.ws.rs.Produces("application/json")
	@jakarta.ws.rs.PUT
	@Override
	public Response putTaxonomyCategoryBatch(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.ws.rs.QueryParam("callbackURL")
			String callbackURL,
			Object object)
		throws Exception {

		vulcanBatchEngineImportTaskResource.setContextAcceptLanguage(
			contextAcceptLanguage);
		vulcanBatchEngineImportTaskResource.setContextCompany(contextCompany);
		vulcanBatchEngineImportTaskResource.setContextHttpServletRequest(
			contextHttpServletRequest);
		vulcanBatchEngineImportTaskResource.setContextUriInfo(contextUriInfo);
		vulcanBatchEngineImportTaskResource.setContextUser(contextUser);

		Response.ResponseBuilder responseBuilder = Response.accepted();

		return responseBuilder.entity(
			vulcanBatchEngineImportTaskResource.putImportTask(
				TaxonomyCategory.class.getName(), callbackURL, object)
		).build();
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PUT' 'http://localhost:8080/o/headless-admin-taxonomy/v1.0/taxonomy-categories/{taxonomyCategoryId}/permissions'  -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "taxonomyCategoryId"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {
			@io.swagger.v3.oas.annotations.tags.Tag(name = "TaxonomyCategory")
		}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path("/taxonomy-categories/{taxonomyCategoryId}/permissions")
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@jakarta.ws.rs.PUT
	@Override
	public Page<Permission> putTaxonomyCategoryPermissionsPage(
			@io.swagger.v3.oas.annotations.Parameter(hidden = true)
			@jakarta.validation.constraints.NotNull
			@jakarta.ws.rs.PathParam("taxonomyCategoryId")
			String taxonomyCategoryId,
			Permission[] permissions)
		throws Exception {

		Long groupId = getPermissionCheckerGroupId(taxonomyCategoryId);
		String resourceName = getPermissionCheckerResourceName(
			taxonomyCategoryId);
		Long resourceId = getPermissionCheckerResourceId(taxonomyCategoryId);

		PermissionServiceUtil.checkPermission(
			groupId, resourceName, resourceId);

		ModelPermissions modelPermissions =
			ModelPermissionsUtil.toModelPermissions(
				contextCompany.getCompanyId(), permissions, resourceId,
				resourceName, resourceActionLocalService,
				resourcePermissionLocalService, roleLocalService);

		Collection<String> roleNames = modelPermissions.getRoleNames();

		for (ResourcePermission resourcePermission :
				resourcePermissionLocalService.getResourcePermissions(
					contextCompany.getCompanyId(), resourceName,
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(resourceId))) {

			com.liferay.portal.kernel.model.Role role =
				roleLocalService.fetchRole(resourcePermission.getRoleId());

			if ((role == null) || roleNames.contains(role.getName())) {
				continue;
			}

			for (ResourceAction resourceAction :
					resourceActionLocalService.getResourceActions(
						resourceName)) {

				resourcePermissionLocalService.removeResourcePermission(
					contextCompany.getCompanyId(), resourceName,
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(resourceId), role.getRoleId(),
					resourceAction.getActionId());
			}
		}

		resourcePermissionLocalService.updateResourcePermissions(
			contextCompany.getCompanyId(), groupId, resourceName,
			String.valueOf(resourceId), modelPermissions);

		return toPermissionPage(
			HashMapBuilder.put(
				"get",
				addAction(
					ActionKeys.PERMISSIONS, resourceId,
					"getTaxonomyCategoryPermissionsPage", null, resourceName,
					groupId)
			).put(
				"replace",
				addAction(
					ActionKeys.PERMISSIONS, resourceId,
					"putTaxonomyCategoryPermissionsPage", null, resourceName,
					groupId)
			).build(),
			resourceId, resourceName, null);
	}

	protected abstract TaxonomyCategory
			doPutTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode(
				Long taxonomyVocabularyId, String externalReferenceCode,
				TaxonomyCategory taxonomyCategory)
		throws Exception;

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -X 'PUT' 'http://localhost:8080/o/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/{taxonomyVocabularyId}/taxonomy-categories/by-external-reference-code/{externalReferenceCode}' -d $'{"description": ___, "description_i18n": ___, "externalReferenceCode": ___, "name": ___, "name_i18n": ___, "parentTaxonomyCategory": ___, "parentTaxonomyVocabulary": ___, "permissions": ___, "taxonomyCategoryProperties": ___, "taxonomyVocabularyId": ___, "viewableBy": ___}' --header 'Content-Type: application/json' -u 'test@liferay.com:test'
	 */
	@io.swagger.v3.oas.annotations.Operation(
		description = "Updates the site's taxonomy category with the given external reference code, or creates it if it not exists."
	)
	@io.swagger.v3.oas.annotations.Parameters(
		value = {
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "taxonomyVocabularyId"
			),
			@io.swagger.v3.oas.annotations.Parameter(
				in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH,
				name = "externalReferenceCode"
			)
		}
	)
	@io.swagger.v3.oas.annotations.tags.Tags(
		value = {
			@io.swagger.v3.oas.annotations.tags.Tag(name = "TaxonomyCategory")
		}
	)
	@jakarta.ws.rs.Consumes({"application/json", "application/xml"})
	@jakarta.ws.rs.Path(
		"/taxonomy-vocabularies/{taxonomyVocabularyId}/taxonomy-categories/by-external-reference-code/{externalReferenceCode}"
	)
	@jakarta.ws.rs.Produces({"application/json", "application/xml"})
	@jakarta.ws.rs.PUT
	@Override
	public final TaxonomyCategory
			putTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode(
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("taxonomyVocabularyId")
				Long taxonomyVocabularyId,
				@io.swagger.v3.oas.annotations.Parameter(hidden = true)
				@jakarta.validation.constraints.NotNull
				@jakarta.ws.rs.PathParam("externalReferenceCode")
				String externalReferenceCode,
				TaxonomyCategory taxonomyCategory)
		throws Exception {

		Permission[] permissions = taxonomyCategory.getPermissions();

		TaxonomyCategory putTaxonomyCategory =
			doPutTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode(
				taxonomyVocabularyId, externalReferenceCode, taxonomyCategory);

		if (permissions != null) {
			Page<Permission> permissionsPage =
				putTaxonomyCategoryPermissionsPage(
					putTaxonomyCategory.getId(), permissions);

			putTaxonomyCategory.setPermissions(
				() -> NestedFieldsSupplier.supply(
					"permissions",
					nestedField -> {
						Collection<Permission> collection =
							permissionsPage.getItems();

						return collection.toArray(
							new Permission[collection.size()]);
					}));
		}

		return putTaxonomyCategory;
	}

	@Override
	@SuppressWarnings("PMD.UnusedLocalVariable")
	public void create(
			Collection<TaxonomyCategory> taxonomyCategories,
			Map<String, Serializable> parameters)
		throws Exception {

		UnsafeFunction<TaxonomyCategory, TaxonomyCategory, Exception>
			taxonomyCategoryUnsafeFunction = null;

		String createStrategy = (String)parameters.getOrDefault(
			"createStrategy", "INSERT");

		if (StringUtil.equalsIgnoreCase(createStrategy, "INSERT")) {
			if (parameters.containsKey("assetLibraryId")) {
				taxonomyCategoryUnsafeFunction =
					taxonomyCategory -> postAssetLibraryTaxonomyCategory(
						(Long)parameters.get("assetLibraryId"),
						taxonomyCategory);
			}
			else if (parameters.containsKey("siteId")) {
				taxonomyCategoryUnsafeFunction =
					taxonomyCategory -> postSiteTaxonomyCategory(
						(Long)parameters.get("siteId"), taxonomyCategory);
			}
			else if (parameters.containsKey("taxonomyVocabularyId")) {
				taxonomyCategoryUnsafeFunction =
					taxonomyCategory -> postTaxonomyVocabularyTaxonomyCategory(
						_parseLong(
							(String)parameters.get("taxonomyVocabularyId")),
						taxonomyCategory);
			}
			else {
				throw new NotSupportedException(
					"One of the following parameters must be specified: [assetLibraryId, siteId, taxonomyVocabularyId]");
			}
		}

		if (StringUtil.equalsIgnoreCase(createStrategy, "UPSERT")) {
			String updateStrategy = (String)parameters.getOrDefault(
				"updateStrategy", "UPDATE");

			if (StringUtil.equalsIgnoreCase(updateStrategy, "PARTIAL_UPDATE")) {
				taxonomyCategoryUnsafeFunction = taxonomyCategory -> {
					TaxonomyCategory getTaxonomyCategory = null;
					TaxonomyCategory persistedTaxonomyCategory = null;

					try {
						if (parameters.containsKey("assetLibraryId")) {
							getTaxonomyCategory =
								getAssetLibraryTaxonomyCategoryByExternalReferenceCode(
									(Long)parameters.get("assetLibraryId"),
									taxonomyCategory.
										getExternalReferenceCode());
						}
						else if (parameters.containsKey("siteId")) {
							getTaxonomyCategory =
								getSiteTaxonomyCategoryByExternalReferenceCode(
									(Long)parameters.get("siteId"),
									taxonomyCategory.
										getExternalReferenceCode());
						}
						else if (parameters.containsKey(
									"taxonomyVocabularyId")) {

							getTaxonomyCategory =
								getTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode(
									_parseLong(
										(String)parameters.get(
											"taxonomyVocabularyId")),
									taxonomyCategory.
										getExternalReferenceCode());
						}
						else {
							throw new NotSupportedException(
								"One of the following parameters must be specified: [assetLibraryId, siteId, taxonomyVocabularyId]");
						}

						persistedTaxonomyCategory = patchTaxonomyCategory(
							getTaxonomyCategory.getId(), taxonomyCategory);
					}
					catch (NoSuchModelException noSuchModelException) {
						if (parameters.containsKey("assetLibraryId")) {
							persistedTaxonomyCategory =
								postAssetLibraryTaxonomyCategory(
									(Long)parameters.get("assetLibraryId"),
									taxonomyCategory);
						}
						else if (parameters.containsKey("siteId")) {
							persistedTaxonomyCategory =
								postSiteTaxonomyCategory(
									(Long)parameters.get("siteId"),
									taxonomyCategory);
						}
						else if (parameters.containsKey(
									"taxonomyVocabularyId")) {

							persistedTaxonomyCategory =
								postTaxonomyVocabularyTaxonomyCategory(
									_parseLong(
										(String)parameters.get(
											"taxonomyVocabularyId")),
									taxonomyCategory);
						}
						else {
							throw new NotSupportedException(
								"One of the following parameters must be specified: [assetLibraryId, siteId, taxonomyVocabularyId]");
						}
					}

					return persistedTaxonomyCategory;
				};
			}

			if (StringUtil.equalsIgnoreCase(updateStrategy, "UPDATE")) {
				taxonomyCategoryUnsafeFunction = taxonomyCategory -> {
					TaxonomyCategory persistedTaxonomyCategory = null;

					if (parameters.containsKey("assetLibraryId")) {
						persistedTaxonomyCategory =
							putAssetLibraryTaxonomyCategoryByExternalReferenceCode(
								(Long)parameters.get("assetLibraryId"),
								taxonomyCategory.getExternalReferenceCode(),
								taxonomyCategory);
					}
					else if (parameters.containsKey("siteId")) {
						persistedTaxonomyCategory =
							putSiteTaxonomyCategoryByExternalReferenceCode(
								(Long)parameters.get("siteId"),
								taxonomyCategory.getExternalReferenceCode(),
								taxonomyCategory);
					}
					else if (parameters.containsKey("taxonomyVocabularyId")) {
						persistedTaxonomyCategory =
							putTaxonomyVocabularyTaxonomyCategoryByExternalReferenceCode(
								_parseLong(
									(String)parameters.get(
										"taxonomyVocabularyId")),
								taxonomyCategory.getExternalReferenceCode(),
								taxonomyCategory);
					}
					else {
						throw new NotSupportedException(
							"One of the following parameters must be specified: [assetLibraryId, siteId, taxonomyVocabularyId]");
					}

					return persistedTaxonomyCategory;
				};
			}
		}

		if (taxonomyCategoryUnsafeFunction == null) {
			throw new NotSupportedException(
				"Create strategy \"" + createStrategy +
					"\" is not supported for TaxonomyCategory");
		}

		if (contextBatchUnsafeBiConsumer != null) {
			contextBatchUnsafeBiConsumer.accept(
				taxonomyCategories, taxonomyCategoryUnsafeFunction);
		}
		else if (contextBatchUnsafeConsumer != null) {
			contextBatchUnsafeConsumer.accept(
				taxonomyCategories, taxonomyCategoryUnsafeFunction::apply);
		}
		else {
			for (TaxonomyCategory taxonomyCategory : taxonomyCategories) {
				taxonomyCategoryUnsafeFunction.apply(taxonomyCategory);
			}
		}
	}

	@Override
	public void delete(
			Collection<TaxonomyCategory> taxonomyCategories,
			Map<String, Serializable> parameters)
		throws Exception {

		UnsafeFunction<TaxonomyCategory, TaxonomyCategory, Exception>
			taxonomyCategoryUnsafeFunction = taxonomyCategory -> {
				deleteTaxonomyCategory(taxonomyCategory.getId());

				return taxonomyCategory;
			};

		if (contextBatchUnsafeBiConsumer != null) {
			contextBatchUnsafeBiConsumer.accept(
				taxonomyCategories, taxonomyCategoryUnsafeFunction);
		}
		else if (contextBatchUnsafeConsumer != null) {
			contextBatchUnsafeConsumer.accept(
				taxonomyCategories, taxonomyCategoryUnsafeFunction::apply);
		}
		else {
			for (TaxonomyCategory taxonomyCategory : taxonomyCategories) {
				taxonomyCategoryUnsafeFunction.apply(taxonomyCategory);
			}
		}
	}

	public Set<String> getAvailableCreateStrategies() {
		return SetUtil.fromArray("INSERT", "UPSERT");
	}

	public Set<String> getAvailableUpdateStrategies() {
		return SetUtil.fromArray("PARTIAL_UPDATE", "UPDATE");
	}

	@Override
	public EntityModel getEntityModel(Map<String, List<String>> multivaluedMap)
		throws Exception {

		return getEntityModel(
			new MultivaluedHashMap<String, Object>(multivaluedMap));
	}

	public String getResourceName() {
		return "TaxonomyCategory";
	}

	public String getVersion() {
		return "v1.0";
	}

	@Override
	public Page<TaxonomyCategory> read(
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[] sorts,
			Map<String, Serializable> parameters, String search)
		throws Exception {

		if (parameters.containsKey("assetLibraryId")) {
			return getAssetLibraryTaxonomyCategoriesPage(
				(Long)parameters.get("assetLibraryId"), search, null, filter,
				pagination, sorts);
		}
		else if (parameters.containsKey("siteId")) {
			return getSiteTaxonomyCategoriesPage(
				(Long)parameters.get("siteId"), search, null, filter,
				pagination, sorts);
		}
		else if (parameters.containsKey("taxonomyVocabularyId")) {
			return getTaxonomyVocabularyTaxonomyCategoriesPage(
				_parseLong((String)parameters.get("taxonomyVocabularyId")),
				_parseBoolean((String)parameters.get("flatten")), search, null,
				filter, pagination, sorts);
		}
		else {
			throw new NotSupportedException(
				"One of the following parameters must be specified: [assetLibraryId, siteId, taxonomyVocabularyId]");
		}
	}

	@Override
	public void setLanguageId(String languageId) {
		this.contextAcceptLanguage = new AcceptLanguage() {

			@Override
			public List<Locale> getLocales() {
				return null;
			}

			@Override
			public String getPreferredLanguageId() {
				return languageId;
			}

			@Override
			public Locale getPreferredLocale() {
				return LocaleUtil.fromLanguageId(languageId);
			}

		};
	}

	@Override
	public void update(
			Collection<TaxonomyCategory> taxonomyCategories,
			Map<String, Serializable> parameters)
		throws Exception {

		UnsafeFunction<TaxonomyCategory, TaxonomyCategory, Exception>
			taxonomyCategoryUnsafeFunction = null;

		String updateStrategy = (String)parameters.getOrDefault(
			"updateStrategy", "UPDATE");

		if (StringUtil.equalsIgnoreCase(updateStrategy, "PARTIAL_UPDATE")) {
			taxonomyCategoryUnsafeFunction =
				taxonomyCategory -> patchTaxonomyCategory(
					taxonomyCategory.getId(), taxonomyCategory);
		}

		if (StringUtil.equalsIgnoreCase(updateStrategy, "UPDATE")) {
			taxonomyCategoryUnsafeFunction =
				taxonomyCategory -> putTaxonomyCategory(
					taxonomyCategory.getId(), taxonomyCategory);
		}

		if (taxonomyCategoryUnsafeFunction == null) {
			throw new NotSupportedException(
				"Update strategy \"" + updateStrategy +
					"\" is not supported for TaxonomyCategory");
		}

		if (contextBatchUnsafeBiConsumer != null) {
			contextBatchUnsafeBiConsumer.accept(
				taxonomyCategories, taxonomyCategoryUnsafeFunction);
		}
		else if (contextBatchUnsafeConsumer != null) {
			contextBatchUnsafeConsumer.accept(
				taxonomyCategories, taxonomyCategoryUnsafeFunction::apply);
		}
		else {
			for (TaxonomyCategory taxonomyCategory : taxonomyCategories) {
				taxonomyCategoryUnsafeFunction.apply(taxonomyCategory);
			}
		}
	}

	private Boolean _parseBoolean(String value) {
		if (value != null) {
			return Boolean.parseBoolean(value);
		}

		return null;
	}

	private Long _parseLong(String value) {
		if (value != null) {
			return Long.parseLong(value);
		}

		return null;
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		return null;
	}

	protected String getPermissionCheckerActionsResourceName(Object id)
		throws Exception {

		return getPermissionCheckerResourceName(id);
	}

	protected Long getPermissionCheckerGroupId(Object id) throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String getPermissionCheckerPortletName(Object id)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long getPermissionCheckerResourceId(Object id) throws Exception {
		return GetterUtil.getLong(id);
	}

	protected String getPermissionCheckerResourceName(Object id)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Page<Permission> toPermissionPage(
			Map<String, Map<String, String>> actions, long id,
			String resourceName, String roleNames)
		throws Exception {

		List<ResourceAction> resourceActions =
			resourceActionLocalService.getResourceActions(resourceName);

		if (Validator.isNotNull(roleNames)) {
			return Page.of(
				actions,
				_getPermissions(
					contextCompany.getCompanyId(), resourceActions, id,
					resourceName, StringUtil.split(roleNames)));
		}

		return Page.of(
			actions,
			_getPermissions(
				contextCompany.getCompanyId(), resourceActions, id,
				resourceName, null));
	}

	/**
	 * @see com.liferay.portal.vulcan.permission.PermissionUtil#getPermissions(long, List, long, String, String[])
	 */
	private Collection<Permission> _getPermissions(
			long companyId, List<ResourceAction> resourceActions,
			long resourceId, String resourceName, String[] roleNames)
		throws Exception {

		Map<String, Permission> permissions = new HashMap<>();

		int count = resourcePermissionLocalService.getResourcePermissionsCount(
			companyId, resourceName, ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(resourceId));

		if (count == 0) {
			ResourceLocalServiceUtil.addResources(
				companyId, resourceId, 0, resourceName,
				String.valueOf(resourceId), false, true, true);
		}

		List<String> actionIds = transform(
			resourceActions, resourceAction -> resourceAction.getActionId());

		Set<ResourcePermission> resourcePermissions = new HashSet<>();

		resourcePermissions.addAll(
			resourcePermissionLocalService.getResourcePermissions(
				companyId, resourceName, ResourceConstants.SCOPE_COMPANY,
				String.valueOf(companyId)));
		resourcePermissions.addAll(
			resourcePermissionLocalService.getResourcePermissions(
				companyId, resourceName, ResourceConstants.SCOPE_GROUP,
				String.valueOf(GroupThreadLocal.getGroupId())));
		resourcePermissions.addAll(
			resourcePermissionLocalService.getResourcePermissions(
				companyId, resourceName, ResourceConstants.SCOPE_GROUP_TEMPLATE,
				"0"));
		resourcePermissions.addAll(
			resourcePermissionLocalService.getResourcePermissions(
				companyId, resourceName, ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(resourceId)));

		List<Resource> resources = transform(
			resourcePermissions,
			resourcePermission -> ResourceLocalServiceUtil.getResource(
				resourcePermission.getCompanyId(), resourcePermission.getName(),
				resourcePermission.getScope(),
				resourcePermission.getPrimKey()));

		Set<com.liferay.portal.kernel.model.Role> roles = new HashSet<>();

		if (roleNames != null) {
			for (String roleName : roleNames) {
				roles.add(roleLocalService.getRole(companyId, roleName));
			}
		}
		else {
			for (ResourcePermission resourcePermission : resourcePermissions) {
				com.liferay.portal.kernel.model.Role role =
					roleLocalService.getRole(resourcePermission.getRoleId());

				roles.add(role);
			}
		}

		for (com.liferay.portal.kernel.model.Role role : roles) {
			Set<String> actionsIdsSet = new HashSet<>();

			for (Resource resource : resources) {
				actionsIdsSet.addAll(
					resourcePermissionLocalService.
						getAvailableResourcePermissionActionIds(
							resource.getCompanyId(), resource.getName(),
							ResourceConstants.SCOPE_COMPANY,
							String.valueOf(resource.getCompanyId()),
							role.getRoleId(), actionIds));
				actionsIdsSet.addAll(
					resourcePermissionLocalService.
						getAvailableResourcePermissionActionIds(
							resource.getCompanyId(), resource.getName(),
							ResourceConstants.SCOPE_GROUP,
							String.valueOf(GroupThreadLocal.getGroupId()),
							role.getRoleId(), actionIds));
				actionsIdsSet.addAll(
					resourcePermissionLocalService.
						getAvailableResourcePermissionActionIds(
							resource.getCompanyId(), resource.getName(),
							ResourceConstants.SCOPE_GROUP_TEMPLATE, "0",
							role.getRoleId(), actionIds));
				actionsIdsSet.addAll(
					resourcePermissionLocalService.
						getAvailableResourcePermissionActionIds(
							resource.getCompanyId(), resource.getName(),
							resource.getScope(), resource.getPrimKey(),
							role.getRoleId(), actionIds));
			}

			if (actionsIdsSet.isEmpty()) {
				continue;
			}

			Permission permission = new Permission() {
				{
					actionIds = actionsIdsSet.toArray(new String[0]);
					roleName = role.getName();
				}
			};

			permissions.put(role.getName(), permission);
		}

		return permissions.values();
	}

	public void setContextAcceptLanguage(AcceptLanguage contextAcceptLanguage) {
		this.contextAcceptLanguage = contextAcceptLanguage;
	}

	public void setContextBatchUnsafeBiConsumer(
		UnsafeBiConsumer
			<Collection<TaxonomyCategory>,
			 UnsafeFunction<TaxonomyCategory, TaxonomyCategory, Exception>,
			 Exception> contextBatchUnsafeBiConsumer) {

		this.contextBatchUnsafeBiConsumer = contextBatchUnsafeBiConsumer;
	}

	public void setContextBatchUnsafeConsumer(
		UnsafeBiConsumer
			<Collection<TaxonomyCategory>,
			 UnsafeConsumer<TaxonomyCategory, Exception>, Exception>
				contextBatchUnsafeConsumer) {

		this.contextBatchUnsafeConsumer = contextBatchUnsafeConsumer;
	}

	public void setContextCompany(
		com.liferay.portal.kernel.model.Company contextCompany) {

		this.contextCompany = contextCompany;
	}

	public void setContextHttpServletRequest(
		HttpServletRequest contextHttpServletRequest) {

		this.contextHttpServletRequest = contextHttpServletRequest;
	}

	public void setContextHttpServletResponse(
		HttpServletResponse contextHttpServletResponse) {

		this.contextHttpServletResponse = contextHttpServletResponse;
	}

	public void setContextUriInfo(UriInfo contextUriInfo) {
		this.contextUriInfo = UriInfoUtil.getVulcanUriInfo(
			getApplicationPath(), contextUriInfo);
	}

	public void setContextUser(
		com.liferay.portal.kernel.model.User contextUser) {

		this.contextUser = contextUser;
	}

	public void setExpressionConvert(
		ExpressionConvert<com.liferay.portal.kernel.search.filter.Filter>
			expressionConvert) {

		this.expressionConvert = expressionConvert;
	}

	public void setFilterParserProvider(
		FilterParserProvider filterParserProvider) {

		this.filterParserProvider = filterParserProvider;
	}

	public void setGroupLocalService(GroupLocalService groupLocalService) {
		this.groupLocalService = groupLocalService;
	}

	public void setResourceActionLocalService(
		ResourceActionLocalService resourceActionLocalService) {

		this.resourceActionLocalService = resourceActionLocalService;
	}

	public void setResourcePermissionLocalService(
		ResourcePermissionLocalService resourcePermissionLocalService) {

		this.resourcePermissionLocalService = resourcePermissionLocalService;
	}

	public void setRoleLocalService(RoleLocalService roleLocalService) {
		this.roleLocalService = roleLocalService;
	}

	public void setSortParserProvider(SortParserProvider sortParserProvider) {
		this.sortParserProvider = sortParserProvider;
	}

	protected String getApplicationPath() {
		return "headless-admin-taxonomy";
	}

	public void setVulcanBatchEngineExportTaskResource(
		VulcanBatchEngineExportTaskResource
			vulcanBatchEngineExportTaskResource) {

		this.vulcanBatchEngineExportTaskResource =
			vulcanBatchEngineExportTaskResource;
	}

	public void setVulcanBatchEngineImportTaskResource(
		VulcanBatchEngineImportTaskResource
			vulcanBatchEngineImportTaskResource) {

		this.vulcanBatchEngineImportTaskResource =
			vulcanBatchEngineImportTaskResource;
	}

	@Override
	public com.liferay.portal.kernel.search.filter.Filter toFilter(
		String filterString, Map<String, List<String>> multivaluedMap) {

		try {
			EntityModel entityModel = getEntityModel(multivaluedMap);

			FilterParser filterParser = filterParserProvider.provide(
				entityModel);

			com.liferay.portal.odata.filter.Filter oDataFilter =
				new com.liferay.portal.odata.filter.Filter(
					filterParser.parse(filterString));

			return expressionConvert.convert(
				oDataFilter.getExpression(),
				contextAcceptLanguage.getPreferredLocale(), entityModel);
		}
		catch (Exception exception) {
			_log.error("Invalid filter " + filterString, exception);

			return null;
		}
	}

	@Override
	public com.liferay.portal.kernel.search.Sort[] toSorts(String sortString) {
		if (Validator.isNull(sortString)) {
			return null;
		}

		try {
			SortParser sortParser = sortParserProvider.provide(
				getEntityModel(Collections.emptyMap()));

			if (sortParser == null) {
				return null;
			}

			com.liferay.portal.odata.sort.Sort oDataSort =
				new com.liferay.portal.odata.sort.Sort(
					sortParser.parse(sortString));

			List<SortField> sortFields = oDataSort.getSortFields();
			com.liferay.portal.kernel.search.Sort[] sorts =
				new com.liferay.portal.kernel.search.Sort[sortFields.size()];

			for (int i = 0; i < sortFields.size(); i++) {
				SortField sortField = sortFields.get(i);

				sorts[i] = new com.liferay.portal.kernel.search.Sort(
					sortField.getSortableFieldName(
						contextAcceptLanguage.getPreferredLocale()),
					!sortField.isAscending());
			}

			return sorts;
		}
		catch (Exception exception) {
			_log.error("Invalid sort " + sortString, exception);

			return new com.liferay.portal.kernel.search.Sort[0];
		}
	}

	protected Map<String, String> addAction(
		String actionName,
		com.liferay.portal.kernel.model.GroupedModel groupedModel,
		String methodName) {

		return ActionUtil.addAction(
			actionName, getClass(), groupedModel, methodName,
			contextScopeChecker, contextUriInfo);
	}

	protected Map<String, String> addAction(
		String actionName, Long id, String methodName, Long ownerId,
		String permissionName, Long siteId) {

		return ActionUtil.addAction(
			actionName, getClass(), id, methodName, contextScopeChecker,
			ownerId, permissionName, siteId, contextUriInfo);
	}

	protected Map<String, String> addAction(
		String actionName, Long id, String methodName,
		ModelResourcePermission modelResourcePermission) {

		return ActionUtil.addAction(
			actionName, getClass(), id, methodName, contextScopeChecker,
			modelResourcePermission, contextUriInfo);
	}

	protected Map<String, String> addAction(
		String actionName, String methodName, String permissionName,
		Long siteId) {

		return addAction(
			actionName, siteId, methodName, null, permissionName, siteId);
	}

	protected void preparePatch(
		TaxonomyCategory taxonomyCategory,
		TaxonomyCategory existingTaxonomyCategory) {
	}

	protected <T, R, E extends Throwable> List<R> transform(
		Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction) {

		return TransformUtil.transform(collection, unsafeFunction);
	}

	public static <R, E extends Throwable> R[] transform(
		int[] array, UnsafeFunction<Integer, R, E> unsafeFunction,
		Class<? extends R> clazz) {

		return TransformUtil.transform(array, unsafeFunction, clazz);
	}

	public static <R, E extends Throwable> R[] transform(
		long[] array, UnsafeFunction<Long, R, E> unsafeFunction,
		Class<? extends R> clazz) {

		return TransformUtil.transform(array, unsafeFunction, clazz);
	}

	protected <T, R, E extends Throwable> R[] transform(
		T[] array, UnsafeFunction<T, R, E> unsafeFunction,
		Class<? extends R> clazz) {

		return TransformUtil.transform(array, unsafeFunction, clazz);
	}

	protected <T, R, E extends Throwable> R[] transformToArray(
		Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction,
		Class<? extends R> clazz) {

		return TransformUtil.transformToArray(
			collection, unsafeFunction, clazz);
	}

	public static <T, E extends Throwable> boolean[] transformToBooleanArray(
		Collection<T> collection,
		UnsafeFunction<T, Boolean, E> unsafeFunction) {

		return TransformUtil.transformToBooleanArray(
			collection, unsafeFunction);
	}

	public static <T, E extends Throwable> boolean[] transformToBooleanArray(
		T[] array, UnsafeFunction<T, Boolean, E> unsafeFunction) {

		return TransformUtil.transformToBooleanArray(array, unsafeFunction);
	}

	public static <T, E extends Throwable> byte[] transformToByteArray(
		Collection<T> collection, UnsafeFunction<T, Byte, E> unsafeFunction) {

		return TransformUtil.transformToByteArray(collection, unsafeFunction);
	}

	public static <T, E extends Throwable> byte[] transformToByteArray(
		T[] array, UnsafeFunction<T, Byte, E> unsafeFunction) {

		return TransformUtil.transformToByteArray(array, unsafeFunction);
	}

	public static <T, E extends Throwable> double[] transformToDoubleArray(
		Collection<T> collection, UnsafeFunction<T, Double, E> unsafeFunction) {

		return TransformUtil.transformToDoubleArray(collection, unsafeFunction);
	}

	public static <T, E extends Throwable> double[] transformToDoubleArray(
		T[] array, UnsafeFunction<T, Double, E> unsafeFunction) {

		return TransformUtil.transformToDoubleArray(array, unsafeFunction);
	}

	public static <T, E extends Throwable> float[] transformToFloatArray(
		Collection<T> collection, UnsafeFunction<T, Float, E> unsafeFunction) {

		return TransformUtil.transformToFloatArray(collection, unsafeFunction);
	}

	public static <T, E extends Throwable> float[] transformToFloatArray(
		T[] array, UnsafeFunction<T, Float, E> unsafeFunction) {

		return TransformUtil.transformToFloatArray(array, unsafeFunction);
	}

	public static <T, R, E extends Throwable> int[] transformToIntArray(
		Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction) {

		return TransformUtil.transformToIntArray(collection, unsafeFunction);
	}

	public static <T, E extends Throwable> int[] transformToIntArray(
		T[] array, UnsafeFunction<T, Integer, E> unsafeFunction) {

		return TransformUtil.transformToIntArray(array, unsafeFunction);
	}

	public static <R, E extends Throwable> List<R> transformToList(
		int[] array, UnsafeFunction<Integer, R, E> unsafeFunction) {

		return TransformUtil.transformToList(array, unsafeFunction);
	}

	public static <R, E extends Throwable> List<R> transformToList(
		long[] array, UnsafeFunction<Long, R, E> unsafeFunction) {

		return TransformUtil.transformToList(array, unsafeFunction);
	}

	protected <T, R, E extends Throwable> List<R> transformToList(
		T[] array, UnsafeFunction<T, R, E> unsafeFunction) {

		return TransformUtil.transformToList(array, unsafeFunction);
	}

	protected <T, R, E extends Throwable> long[] transformToLongArray(
		Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction) {

		return TransformUtil.transformToLongArray(collection, unsafeFunction);
	}

	public static <T, E extends Throwable> long[] transformToLongArray(
		T[] array, UnsafeFunction<T, Long, E> unsafeFunction) {

		return TransformUtil.transformToLongArray(array, unsafeFunction);
	}

	public static <T, E extends Throwable> short[] transformToShortArray(
		Collection<T> collection, UnsafeFunction<T, Short, E> unsafeFunction) {

		return TransformUtil.transformToShortArray(collection, unsafeFunction);
	}

	public static <T, E extends Throwable> short[] transformToShortArray(
		T[] array, UnsafeFunction<T, Short, E> unsafeFunction) {

		return TransformUtil.transformToShortArray(array, unsafeFunction);
	}

	protected <T, R, E extends Throwable> List<R> unsafeTransform(
			Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransform(collection, unsafeFunction);
	}

	public static <R, E extends Throwable> R[] unsafeTransform(
			int[] array, UnsafeFunction<Integer, R, E> unsafeFunction,
			Class<? extends R> clazz)
		throws E {

		return TransformUtil.unsafeTransform(array, unsafeFunction, clazz);
	}

	public static <R, E extends Throwable> R[] unsafeTransform(
			long[] array, UnsafeFunction<Long, R, E> unsafeFunction,
			Class<? extends R> clazz)
		throws E {

		return TransformUtil.unsafeTransform(array, unsafeFunction, clazz);
	}

	protected <T, R, E extends Throwable> R[] unsafeTransform(
			T[] array, UnsafeFunction<T, R, E> unsafeFunction,
			Class<? extends R> clazz)
		throws E {

		return TransformUtil.unsafeTransform(array, unsafeFunction, clazz);
	}

	protected <T, R, E extends Throwable> R[] unsafeTransformToArray(
			Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction,
			Class<? extends R> clazz)
		throws E {

		return TransformUtil.unsafeTransformToArray(
			collection, unsafeFunction, clazz);
	}

	public static <T, E extends Throwable> boolean[]
			unsafeTransformToBooleanArray(
				Collection<T> collection,
				UnsafeFunction<T, Boolean, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToBooleanArray(
			collection, unsafeFunction);
	}

	public static <T, E extends Throwable> boolean[]
			unsafeTransformToBooleanArray(
				T[] array, UnsafeFunction<T, Boolean, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToBooleanArray(
			array, unsafeFunction);
	}

	public static <T, E extends Throwable> byte[] unsafeTransformToByteArray(
			Collection<T> collection, UnsafeFunction<T, Byte, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToByteArray(
			collection, unsafeFunction);
	}

	public static <T, E extends Throwable> byte[] unsafeTransformToByteArray(
			T[] array, UnsafeFunction<T, Byte, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToByteArray(array, unsafeFunction);
	}

	public static <T, E extends Throwable> double[]
			unsafeTransformToDoubleArray(
				Collection<T> collection,
				UnsafeFunction<T, Double, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToDoubleArray(
			collection, unsafeFunction);
	}

	public static <T, E extends Throwable> double[]
			unsafeTransformToDoubleArray(
				T[] array, UnsafeFunction<T, Double, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToDoubleArray(
			array, unsafeFunction);
	}

	public static <T, E extends Throwable> float[] unsafeTransformToFloatArray(
			Collection<T> collection,
			UnsafeFunction<T, Float, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToFloatArray(
			collection, unsafeFunction);
	}

	public static <T, E extends Throwable> float[] unsafeTransformToFloatArray(
			T[] array, UnsafeFunction<T, Float, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToFloatArray(array, unsafeFunction);
	}

	public static <T, R, E extends Throwable> int[] unsafeTransformToIntArray(
			Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToIntArray(
			collection, unsafeFunction);
	}

	public static <T, E extends Throwable> int[] unsafeTransformToIntArray(
			T[] array, UnsafeFunction<T, Integer, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToIntArray(array, unsafeFunction);
	}

	public static <R, E extends Throwable> List<R> unsafeTransformToList(
			int[] array, UnsafeFunction<Integer, R, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToList(array, unsafeFunction);
	}

	public static <R, E extends Throwable> List<R> unsafeTransformToList(
			long[] array, UnsafeFunction<Long, R, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToList(array, unsafeFunction);
	}

	protected <T, R, E extends Throwable> List<R> unsafeTransformToList(
			T[] array, UnsafeFunction<T, R, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToList(array, unsafeFunction);
	}

	protected <T, R, E extends Throwable> long[] unsafeTransformToLongArray(
			Collection<T> collection, UnsafeFunction<T, R, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToLongArray(
			collection, unsafeFunction);
	}

	public static <T, E extends Throwable> long[] unsafeTransformToLongArray(
			T[] array, UnsafeFunction<T, Long, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToLongArray(array, unsafeFunction);
	}

	public static <T, E extends Throwable> short[] unsafeTransformToShortArray(
			Collection<T> collection,
			UnsafeFunction<T, Short, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToShortArray(
			collection, unsafeFunction);
	}

	public static <T, E extends Throwable> short[] unsafeTransformToShortArray(
			T[] array, UnsafeFunction<T, Short, E> unsafeFunction)
		throws E {

		return TransformUtil.unsafeTransformToShortArray(array, unsafeFunction);
	}

	protected AcceptLanguage contextAcceptLanguage;
	protected UnsafeBiConsumer
		<Collection<TaxonomyCategory>,
		 UnsafeFunction<TaxonomyCategory, TaxonomyCategory, Exception>,
		 Exception> contextBatchUnsafeBiConsumer;
	protected UnsafeBiConsumer
		<Collection<TaxonomyCategory>,
		 UnsafeConsumer<TaxonomyCategory, Exception>, Exception>
			contextBatchUnsafeConsumer;
	protected com.liferay.portal.kernel.model.Company contextCompany;
	protected HttpServletRequest contextHttpServletRequest;
	protected HttpServletResponse contextHttpServletResponse;
	protected Object contextScopeChecker;
	protected UriInfo contextUriInfo;
	protected com.liferay.portal.kernel.model.User contextUser;
	protected ExpressionConvert<com.liferay.portal.kernel.search.filter.Filter>
		expressionConvert;
	protected FilterParserProvider filterParserProvider;
	protected GroupLocalService groupLocalService;
	protected ResourceActionLocalService resourceActionLocalService;
	protected ResourcePermissionLocalService resourcePermissionLocalService;
	protected RoleLocalService roleLocalService;
	protected SortParserProvider sortParserProvider;
	protected VulcanBatchEngineExportTaskResource
		vulcanBatchEngineExportTaskResource;
	protected VulcanBatchEngineImportTaskResource
		vulcanBatchEngineImportTaskResource;

	private static final com.liferay.portal.kernel.log.Log _log =
		LogFactoryUtil.getLog(BaseTaxonomyCategoryResourceImpl.class);

}