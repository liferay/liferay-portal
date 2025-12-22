/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.internal.graphql.mutation.v1_0;

import com.liferay.bulk.rest.dto.v1_0.BulkAction;
import com.liferay.bulk.rest.dto.v1_0.BulkActionItem;
import com.liferay.bulk.rest.dto.v1_0.BulkActionTask;
import com.liferay.bulk.rest.dto.v1_0.DocumentBulkSelection;
import com.liferay.bulk.rest.dto.v1_0.Keyword;
import com.liferay.bulk.rest.dto.v1_0.KeywordBulkSelection;
import com.liferay.bulk.rest.dto.v1_0.Selection;
import com.liferay.bulk.rest.dto.v1_0.TaxonomyCategoryBulkSelection;
import com.liferay.bulk.rest.dto.v1_0.TaxonomyVocabulary;
import com.liferay.bulk.rest.resource.v1_0.BulkActionResource;
import com.liferay.bulk.rest.resource.v1_0.KeywordResource;
import com.liferay.bulk.rest.resource.v1_0.SelectionResource;
import com.liferay.bulk.rest.resource.v1_0.TaxonomyCategoryResource;
import com.liferay.bulk.rest.resource.v1_0.TaxonomyVocabularyResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.validation.constraints.NotEmpty;

import jakarta.ws.rs.core.UriInfo;

import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class Mutation {

	public static void setBulkActionResourceComponentServiceObjects(
		ComponentServiceObjects<BulkActionResource>
			bulkActionResourceComponentServiceObjects) {

		_bulkActionResourceComponentServiceObjects =
			bulkActionResourceComponentServiceObjects;
	}

	public static void setKeywordResourceComponentServiceObjects(
		ComponentServiceObjects<KeywordResource>
			keywordResourceComponentServiceObjects) {

		_keywordResourceComponentServiceObjects =
			keywordResourceComponentServiceObjects;
	}

	public static void setSelectionResourceComponentServiceObjects(
		ComponentServiceObjects<SelectionResource>
			selectionResourceComponentServiceObjects) {

		_selectionResourceComponentServiceObjects =
			selectionResourceComponentServiceObjects;
	}

	public static void setTaxonomyCategoryResourceComponentServiceObjects(
		ComponentServiceObjects<TaxonomyCategoryResource>
			taxonomyCategoryResourceComponentServiceObjects) {

		_taxonomyCategoryResourceComponentServiceObjects =
			taxonomyCategoryResourceComponentServiceObjects;
	}

	public static void setTaxonomyVocabularyResourceComponentServiceObjects(
		ComponentServiceObjects<TaxonomyVocabularyResource>
			taxonomyVocabularyResourceComponentServiceObjects) {

		_taxonomyVocabularyResourceComponentServiceObjects =
			taxonomyVocabularyResourceComponentServiceObjects;
	}

	@GraphQLField(description = "Execute a bulk action")
	public BulkActionTask createBulkAction(
			@GraphQLName("blueprintExternalReferenceCode") String
				blueprintExternalReferenceCode,
			@GraphQLName("emptySearch") Boolean emptySearch,
			@GraphQLName("entryClassNames") String entryClassNames,
			@GraphQLName("scope") String scope,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("bulkAction") BulkAction bulkAction)
		throws Exception {

		return _applyComponentServiceObjects(
			_bulkActionResourceComponentServiceObjects,
			this::_populateResourceContext,
			bulkActionResource -> bulkActionResource.postBulkAction(
				blueprintExternalReferenceCode, emptySearch, entryClassNames,
				scope, search,
				_filterBiFunction.apply(bulkActionResource, filterString),
				Pagination.of(page, pageSize),
				_sortsBiFunction.apply(bulkActionResource, sortsString),
				bulkAction));
	}

	@GraphQLField(
		description = "Creates a preview for each item based on the bulk action type"
	)
	public java.util.Collection<BulkActionItem> createBulkActionItemPreviewPage(
			@GraphQLName("fetchChildren") Boolean fetchChildren,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("bulkAction") BulkAction bulkAction)
		throws Exception {

		return _applyComponentServiceObjects(
			_bulkActionResourceComponentServiceObjects,
			this::_populateResourceContext,
			bulkActionResource -> {
				Page paginationPage =
					bulkActionResource.postBulkActionItemPreviewPage(
						fetchChildren, search,
						_filterBiFunction.apply(
							bulkActionResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(bulkActionResource, sortsString),
						bulkAction);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public boolean patchKeywordBatch(
			@GraphQLName("keywordBulkSelection") KeywordBulkSelection
				keywordBulkSelection)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_keywordResourceComponentServiceObjects,
			this::_populateResourceContext,
			keywordResource -> keywordResource.patchKeywordBatch(
				keywordBulkSelection));

		return true;
	}

	@GraphQLField
	public java.util.Collection<Keyword> createKeywordsCommonPageObject(
			@GraphQLName("blueprintExternalReferenceCode") String
				blueprintExternalReferenceCode,
			@GraphQLName("emptySearch") Boolean emptySearch,
			@GraphQLName("entryClassNames") String entryClassNames,
			@GraphQLName("scope") String scope,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_keywordResourceComponentServiceObjects,
			this::_populateResourceContext,
			keywordResource -> {
				Page paginationPage =
					keywordResource.postKeywordsCommonPageObject(
						blueprintExternalReferenceCode, emptySearch,
						entryClassNames, scope, search,
						_filterBiFunction.apply(keywordResource, filterString),
						_sortsBiFunction.apply(keywordResource, sortsString),
						object);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public boolean updateKeywordBatch(
			@GraphQLName("keywordBulkSelection") KeywordBulkSelection
				keywordBulkSelection)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_keywordResourceComponentServiceObjects,
			this::_populateResourceContext,
			keywordResource -> keywordResource.putKeywordBatch(
				keywordBulkSelection));

		return true;
	}

	@GraphQLField
	public Selection createBulkSelection(
			@GraphQLName("documentBulkSelection") DocumentBulkSelection
				documentBulkSelection)
		throws Exception {

		return _applyComponentServiceObjects(
			_selectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			selectionResource -> selectionResource.postBulkSelection(
				documentBulkSelection));
	}

	@GraphQLField
	public boolean patchTaxonomyCategoryBatch(
			@GraphQLName("taxonomyCategoryBulkSelection")
				TaxonomyCategoryBulkSelection taxonomyCategoryBulkSelection)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_taxonomyCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyCategoryResource ->
				taxonomyCategoryResource.patchTaxonomyCategoryBatch(
					taxonomyCategoryBulkSelection));

		return true;
	}

	@GraphQLField
	public boolean updateTaxonomyCategoryBatch(
			@GraphQLName("taxonomyCategoryBulkSelection")
				TaxonomyCategoryBulkSelection taxonomyCategoryBulkSelection)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_taxonomyCategoryResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyCategoryResource ->
				taxonomyCategoryResource.putTaxonomyCategoryBatch(
					taxonomyCategoryBulkSelection));

		return true;
	}

	@GraphQLField
	public java.util.Collection<TaxonomyVocabulary>
			createSiteTaxonomyVocabulariesCommonPageObject(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("blueprintExternalReferenceCode") String
					blueprintExternalReferenceCode,
				@GraphQLName("emptySearch") Boolean emptySearch,
				@GraphQLName("entryClassNames") String entryClassNames,
				@GraphQLName("scope") String scope,
				@GraphQLName("search") String search,
				@GraphQLName("filter") String filterString,
				@GraphQLName("sort") String sortsString,
				@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_taxonomyVocabularyResourceComponentServiceObjects,
			this::_populateResourceContext,
			taxonomyVocabularyResource -> {
				Page paginationPage =
					taxonomyVocabularyResource.
						postSiteTaxonomyVocabulariesCommonPageObject(
							Long.valueOf(siteKey),
							blueprintExternalReferenceCode, emptySearch,
							entryClassNames, scope, search,
							_filterBiFunction.apply(
								taxonomyVocabularyResource, filterString),
							_sortsBiFunction.apply(
								taxonomyVocabularyResource, sortsString),
							object);

				return paginationPage.getItems();
			});
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

	private void _populateResourceContext(BulkActionResource bulkActionResource)
		throws Exception {

		bulkActionResource.setContextAcceptLanguage(_acceptLanguage);
		bulkActionResource.setContextCompany(_company);
		bulkActionResource.setContextHttpServletRequest(_httpServletRequest);
		bulkActionResource.setContextHttpServletResponse(_httpServletResponse);
		bulkActionResource.setContextUriInfo(_uriInfo);
		bulkActionResource.setContextUser(_user);
		bulkActionResource.setGroupLocalService(_groupLocalService);
		bulkActionResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(KeywordResource keywordResource)
		throws Exception {

		keywordResource.setContextAcceptLanguage(_acceptLanguage);
		keywordResource.setContextCompany(_company);
		keywordResource.setContextHttpServletRequest(_httpServletRequest);
		keywordResource.setContextHttpServletResponse(_httpServletResponse);
		keywordResource.setContextUriInfo(_uriInfo);
		keywordResource.setContextUser(_user);
		keywordResource.setGroupLocalService(_groupLocalService);
		keywordResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(SelectionResource selectionResource)
		throws Exception {

		selectionResource.setContextAcceptLanguage(_acceptLanguage);
		selectionResource.setContextCompany(_company);
		selectionResource.setContextHttpServletRequest(_httpServletRequest);
		selectionResource.setContextHttpServletResponse(_httpServletResponse);
		selectionResource.setContextUriInfo(_uriInfo);
		selectionResource.setContextUser(_user);
		selectionResource.setGroupLocalService(_groupLocalService);
		selectionResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			TaxonomyCategoryResource taxonomyCategoryResource)
		throws Exception {

		taxonomyCategoryResource.setContextAcceptLanguage(_acceptLanguage);
		taxonomyCategoryResource.setContextCompany(_company);
		taxonomyCategoryResource.setContextHttpServletRequest(
			_httpServletRequest);
		taxonomyCategoryResource.setContextHttpServletResponse(
			_httpServletResponse);
		taxonomyCategoryResource.setContextUriInfo(_uriInfo);
		taxonomyCategoryResource.setContextUser(_user);
		taxonomyCategoryResource.setGroupLocalService(_groupLocalService);
		taxonomyCategoryResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			TaxonomyVocabularyResource taxonomyVocabularyResource)
		throws Exception {

		taxonomyVocabularyResource.setContextAcceptLanguage(_acceptLanguage);
		taxonomyVocabularyResource.setContextCompany(_company);
		taxonomyVocabularyResource.setContextHttpServletRequest(
			_httpServletRequest);
		taxonomyVocabularyResource.setContextHttpServletResponse(
			_httpServletResponse);
		taxonomyVocabularyResource.setContextUriInfo(_uriInfo);
		taxonomyVocabularyResource.setContextUser(_user);
		taxonomyVocabularyResource.setGroupLocalService(_groupLocalService);
		taxonomyVocabularyResource.setRoleLocalService(_roleLocalService);
	}

	private static ComponentServiceObjects<BulkActionResource>
		_bulkActionResourceComponentServiceObjects;
	private static ComponentServiceObjects<KeywordResource>
		_keywordResourceComponentServiceObjects;
	private static ComponentServiceObjects<SelectionResource>
		_selectionResourceComponentServiceObjects;
	private static ComponentServiceObjects<TaxonomyCategoryResource>
		_taxonomyCategoryResourceComponentServiceObjects;
	private static ComponentServiceObjects<TaxonomyVocabularyResource>
		_taxonomyVocabularyResourceComponentServiceObjects;

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

}