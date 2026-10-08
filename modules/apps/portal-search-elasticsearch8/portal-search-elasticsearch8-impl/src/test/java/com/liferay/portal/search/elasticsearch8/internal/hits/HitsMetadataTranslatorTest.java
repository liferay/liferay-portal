/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.hits;

import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.json.JsonData;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.hits.TotalHitsRelation;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Rodrigo Guedes de Souza
 */
public class HitsMetadataTranslatorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testTranslateTotalHitsRelation() {
		_testTranslateTotalHitsRelation(
			TotalHitsRelation.EQ,
			co.elastic.clients.elasticsearch.core.search.TotalHitsRelation.Eq);
		_testTranslateTotalHitsRelation(
			TotalHitsRelation.GTE,
			co.elastic.clients.elasticsearch.core.search.TotalHitsRelation.Gte);
	}

	private void _testTranslateTotalHitsRelation(
		TotalHitsRelation expectedTotalHitsRelation,
		co.elastic.clients.elasticsearch.core.search.TotalHitsRelation
			totalHitsRelation) {

		HitsMetadata.Builder<JsonData> builder = new HitsMetadata.Builder<>();

		builder.hits(Collections.emptyList());

		long totalHits = RandomTestUtil.randomLong();

		builder.total(
			TotalHits.of(
				totalHitsBuilder -> totalHitsBuilder.relation(
					totalHitsRelation
				).value(
					totalHits
				)));

		SearchHits searchHits = _hitsMetadataTranslator.translate(
			builder.build());

		Assert.assertEquals(totalHits, searchHits.getTotalHits());
		Assert.assertEquals(
			expectedTotalHitsRelation, searchHits.getTotalHitsRelation());
	}

	private final HitsMetadataTranslator _hitsMetadataTranslator =
		new HitsMetadataTranslator();

}