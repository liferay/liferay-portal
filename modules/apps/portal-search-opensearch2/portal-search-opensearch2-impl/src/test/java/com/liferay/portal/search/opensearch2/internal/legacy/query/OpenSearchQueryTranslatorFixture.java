/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.legacy.query;

import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.search.filter.FilterTranslator;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.opensearch2.internal.filter.OpenSearchFilterTranslatorFixture;
import com.liferay.portal.search.opensearch2.internal.index.CompanyIdIndexNameBuilder;

import org.opensearch.client.opensearch._types.query_dsl.QueryVariant;

/**
 * @author Michael C. Han
 */
public class OpenSearchQueryTranslatorFixture {

	public OpenSearchQueryTranslatorFixture() {
		OpenSearchQueryTranslator openSearchQueryTranslator =
			new OpenSearchQueryTranslator(null);

		OpenSearchFilterTranslatorFixture openSearchFilterTranslatorFixture =
			new OpenSearchFilterTranslatorFixture(openSearchQueryTranslator);

		ReflectionTestUtil.setFieldValue(
			openSearchQueryTranslator, "_filterTranslatorSnapshot",
			new Snapshot<FilterTranslator<QueryVariant>>(null, null) {

				public FilterTranslator<QueryVariant> get() {
					return openSearchFilterTranslatorFixture.
						getOpenSearchFilterTranslator();
				}

			});

		_openSearchQueryTranslator = openSearchQueryTranslator;

		IndexNameBuilder indexNameBuilder = new CompanyIdIndexNameBuilder() {
			{
				setIndexNamePrefix(null);
			}
		};

		ReflectionTestUtil.setFieldValue(
			_openSearchQueryTranslator, "_indexNameBuilder", indexNameBuilder);
	}

	public OpenSearchQueryTranslator getOpenSearchQueryTranslator() {
		return _openSearchQueryTranslator;
	}

	private final OpenSearchQueryTranslator _openSearchQueryTranslator;

}