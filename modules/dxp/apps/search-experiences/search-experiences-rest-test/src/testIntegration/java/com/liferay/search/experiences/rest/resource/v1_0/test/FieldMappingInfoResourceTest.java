/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.index.GetIndexIndexRequest;
import com.liferay.portal.search.engine.adapter.index.GetIndexIndexResponse;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.search.experiences.rest.client.dto.v1_0.FieldMappingInfo;
import com.liferay.search.experiences.rest.client.pagination.Page;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian Wing Shun Chan
 */
@RunWith(Arquillian.class)
public class FieldMappingInfoResourceTest
	extends BaseFieldMappingInfoResourceTestCase {

	@Override
	@Test
	public void testGetFieldMappingInfosPage() throws Exception {
		String prefix =
			_indexNameBuilder.getIndexName(testCompany.getCompanyId()) +
				StringPool.DASH;

		GetIndexIndexResponse getIndexIndexResponse =
			_searchEngineAdapter.execute(
				new GetIndexIndexRequest(prefix + StringPool.STAR));

		for (String indexName : getIndexIndexResponse.getIndexNames()) {
			Page<FieldMappingInfo> page =
				fieldMappingInfoResource.getFieldMappingInfosPage(
					false, indexName, null);

			Assert.assertNotNull(page.getItems());

			assertValid(page);
		}
	}

	@Inject
	private IndexNameBuilder _indexNameBuilder;

	@Inject
	private SearchEngineAdapter _searchEngineAdapter;

}