/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.internal.graphql.servlet.v1_0;

import com.liferay.bulk.rest.internal.graphql.mutation.v1_0.Mutation;
import com.liferay.bulk.rest.internal.graphql.query.v1_0.Query;
import com.liferay.bulk.rest.internal.resource.v1_0.BulkActionResourceImpl;
import com.liferay.bulk.rest.internal.resource.v1_0.KeywordResourceImpl;
import com.liferay.bulk.rest.internal.resource.v1_0.SelectionResourceImpl;
import com.liferay.bulk.rest.internal.resource.v1_0.StatusResourceImpl;
import com.liferay.bulk.rest.internal.resource.v1_0.TaxonomyCategoryResourceImpl;
import com.liferay.bulk.rest.internal.resource.v1_0.TaxonomyVocabularyResourceImpl;
import com.liferay.bulk.rest.resource.v1_0.BulkActionResource;
import com.liferay.bulk.rest.resource.v1_0.KeywordResource;
import com.liferay.bulk.rest.resource.v1_0.SelectionResource;
import com.liferay.bulk.rest.resource.v1_0.StatusResource;
import com.liferay.bulk.rest.resource.v1_0.TaxonomyCategoryResource;
import com.liferay.bulk.rest.resource.v1_0.TaxonomyVocabularyResource;
import com.liferay.portal.kernel.util.ObjectValuePair;
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
		Mutation.setBulkActionResourceComponentServiceObjects(
			_bulkActionResourceComponentServiceObjects);
		Mutation.setKeywordResourceComponentServiceObjects(
			_keywordResourceComponentServiceObjects);
		Mutation.setSelectionResourceComponentServiceObjects(
			_selectionResourceComponentServiceObjects);
		Mutation.setTaxonomyCategoryResourceComponentServiceObjects(
			_taxonomyCategoryResourceComponentServiceObjects);
		Mutation.setTaxonomyVocabularyResourceComponentServiceObjects(
			_taxonomyVocabularyResourceComponentServiceObjects);

		Query.setStatusResourceComponentServiceObjects(
			_statusResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "Liferay.Bulk.REST";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/bulk-graphql/v1_0";
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
						"mutation#createBulkAction",
						new ObjectValuePair<>(
							BulkActionResourceImpl.class, "postBulkAction"));
					put(
						"mutation#createBulkActionItemPreviewPage",
						new ObjectValuePair<>(
							BulkActionResourceImpl.class,
							"postBulkActionItemPreviewPage"));
					put(
						"mutation#patchKeywordBatch",
						new ObjectValuePair<>(
							KeywordResourceImpl.class, "patchKeywordBatch"));
					put(
						"mutation#createKeywordsCommonPageObject",
						new ObjectValuePair<>(
							KeywordResourceImpl.class,
							"postKeywordsCommonPageObject"));
					put(
						"mutation#updateKeywordBatch",
						new ObjectValuePair<>(
							KeywordResourceImpl.class, "putKeywordBatch"));
					put(
						"mutation#createBulkSelection",
						new ObjectValuePair<>(
							SelectionResourceImpl.class, "postBulkSelection"));
					put(
						"mutation#patchTaxonomyCategoryBatch",
						new ObjectValuePair<>(
							TaxonomyCategoryResourceImpl.class,
							"patchTaxonomyCategoryBatch"));
					put(
						"mutation#updateTaxonomyCategoryBatch",
						new ObjectValuePair<>(
							TaxonomyCategoryResourceImpl.class,
							"putTaxonomyCategoryBatch"));
					put(
						"mutation#createSiteTaxonomyVocabulariesCommonPageObject",
						new ObjectValuePair<>(
							TaxonomyVocabularyResourceImpl.class,
							"postSiteTaxonomyVocabulariesCommonPageObject"));

					put(
						"query#status",
						new ObjectValuePair<>(
							StatusResourceImpl.class, "getStatus"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<BulkActionResource>
		_bulkActionResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<KeywordResource>
		_keywordResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<SelectionResource>
		_selectionResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<TaxonomyCategoryResource>
		_taxonomyCategoryResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<TaxonomyVocabularyResource>
		_taxonomyVocabularyResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<StatusResource>
		_statusResourceComponentServiceObjects;

}