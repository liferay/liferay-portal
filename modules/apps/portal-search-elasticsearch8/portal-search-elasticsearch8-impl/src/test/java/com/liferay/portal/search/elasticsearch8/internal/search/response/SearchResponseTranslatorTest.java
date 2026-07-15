/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.search.response;

import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.InnerHitsResult;
import co.elastic.clients.json.JsonData;

import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.LocalizationImpl;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Prathima Shreenath
 */
public class SearchResponseTranslatorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		LocalizationUtil localizationUtil = new LocalizationUtil();

		localizationUtil.setLocalization(new LocalizationImpl());
	}

	@Test
	public void testAddSnippetsConcatenatesDistinctInnerHitFragments() {
		String fieldName = RandomTestUtil.randomString();

		Document document = _addSnippets(
			_createHit(
				fieldName, Collections.singletonList("alpha"),
				_createInnerHitsResult(
					_createHit(
						fieldName, Collections.singletonList("gamma"), null))));

		Assert.assertEquals(
			"alpha...gamma", document.get("snippet_" + fieldName));
	}

	@Test
	public void testAddSnippetsDoesNotDuplicateBaseAndLocalizedField() {
		Hit.Builder<JsonData> builder = new Hit.Builder<>();

		List<String> fragments = Collections.singletonList(
			"<liferay-hl>alpha</liferay-hl> beta");

		String fieldName = RandomTestUtil.randomString();

		builder.highlight(fieldName, fragments);

		String localizedFieldName = Field.getLocalizedName(
			LocaleUtil.US, fieldName);

		builder.highlight(localizedFieldName, fragments);

		builder.index("0");

		Document document = _addSnippets(builder.build());

		Assert.assertEquals(
			"<liferay-hl>alpha</liferay-hl> beta",
			document.get("snippet_" + localizedFieldName));
	}

	private Document _addSnippets(Hit<JsonData> hit) {
		Document document = new DocumentImpl();

		ReflectionTestUtil.invoke(
			new SearchResponseTranslator(), "_addSnippets",
			new Class<?>[] {Document.class, Hit.class, Locale.class}, document,
			hit, LocaleUtil.US);

		return document;
	}

	private Hit<JsonData> _createHit(
		String fieldName, List<String> fragments,
		InnerHitsResult innerHitsResult) {

		Hit.Builder<JsonData> builder = new Hit.Builder<>();

		builder.highlight(fieldName, fragments);
		builder.index("0");

		if (innerHitsResult != null) {
			builder.innerHits(RandomTestUtil.randomString(), innerHitsResult);
		}

		return builder.build();
	}

	private InnerHitsResult _createInnerHitsResult(Hit<JsonData> hit) {
		HitsMetadata.Builder<JsonData> hitsMetadataBuilder =
			new HitsMetadata.Builder<>();

		hitsMetadataBuilder.hits(Collections.singletonList(hit));

		InnerHitsResult.Builder builder = new InnerHitsResult.Builder();

		builder.hits(hitsMetadataBuilder.build());

		return builder.build();
	}

}