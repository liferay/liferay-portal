/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.frontend.data.set.filter;

import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.frontend.data.set.constants.FDSEntityFieldTypes;
import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.search.aggregation.Aggregations;
import com.liferay.portal.search.aggregation.bucket.Bucket;
import com.liferay.portal.search.aggregation.bucket.TermsAggregation;
import com.liferay.portal.search.aggregation.bucket.TermsAggregationResult;
import com.liferay.portal.search.filter.ComplexQueryPartBuilder;
import com.liferay.portal.search.filter.ComplexQueryPartBuilderFactory;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Sam Ziemer
 */
public class ExtensionSelectionFDSFilterTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);

		ReflectionTestUtil.setFieldValue(
			_extensionSelectionFDSFilter, "_aggregations", _aggregations);
		ReflectionTestUtil.setFieldValue(
			_extensionSelectionFDSFilter, "_complexQueryPartBuilderFactory",
			_complexQueryPartBuilderFactory);
		ReflectionTestUtil.setFieldValue(
			_extensionSelectionFDSFilter, "_depotEntryLocalService",
			_depotEntryLocalService);
		ReflectionTestUtil.setFieldValue(
			_extensionSelectionFDSFilter, "_objectDefinitionService",
			_objectDefinitionService);
		ReflectionTestUtil.setFieldValue(
			_extensionSelectionFDSFilter, "_searcher", _searcher);
		ReflectionTestUtil.setFieldValue(
			_extensionSelectionFDSFilter, "_searchRequestBuilderFactory",
			_searchRequestBuilderFactory);

		Mockito.when(
			_aggregations.terms(Mockito.anyString(), Mockito.anyString())
		).thenReturn(
			Mockito.mock(TermsAggregation.class)
		);

		Mockito.when(
			_complexQueryPartBuilderFactory.builder()
		).thenReturn(
			Mockito.mock(ComplexQueryPartBuilder.class, Mockito.RETURNS_SELF)
		);

		Mockito.when(
			_depotEntryLocalService.getDepotEntries(
				Mockito.anyLong(), Mockito.anyInt())
		).thenReturn(
			Collections.emptyList()
		);

		Mockito.when(
			_objectDefinition.getClassName()
		).thenReturn(
			_OBJECT_DEFINITION_CLASS_NAME
		);

		Mockito.when(
			_objectDefinitionService.getCMSObjectDefinitions(
				Mockito.anyLong(), Mockito.any())
		).thenReturn(
			List.of(_objectDefinition)
		);

		Mockito.when(
			_searcher.search(Mockito.any())
		).thenReturn(
			_searchResponse
		);

		Mockito.when(
			_searchRequestBuilderFactory.builder()
		).thenReturn(
			_searchRequestBuilder
		);
	}

	@Test
	public void testGetProperties() {
		Assert.assertEquals(
			FDSEntityFieldTypes.STRING,
			_extensionSelectionFDSFilter.getEntityFieldType());
		Assert.assertEquals("extension", _extensionSelectionFDSFilter.getId());
		Assert.assertEquals(
			"extension", _extensionSelectionFDSFilter.getLabel());
	}

	@Test
	public void testGetSelectionFDSFilterItemsWhenNoCMSObjectDefinitions() {
		Mockito.when(
			_objectDefinitionService.getCMSObjectDefinitions(
				Mockito.anyLong(), Mockito.any())
		).thenReturn(
			Collections.emptyList()
		);

		List<SelectionFDSFilterItem> selectionFDSFilterItems =
			_extensionSelectionFDSFilter.getSelectionFDSFilterItems(_locale);

		Assert.assertTrue(
			selectionFDSFilterItems.toString(),
			selectionFDSFilterItems.isEmpty());

		Mockito.verify(
			_searcher, Mockito.never()
		).search(
			Mockito.any()
		);
	}

	@Test
	public void testGetSelectionFDSFilterItemsWhenNoExtensions() {
		Mockito.when(
			_searchResponse.getAggregationResult("extensions")
		).thenReturn(
			null
		);

		List<SelectionFDSFilterItem> selectionFDSFilterItems =
			_extensionSelectionFDSFilter.getSelectionFDSFilterItems(_locale);

		Assert.assertTrue(
			selectionFDSFilterItems.toString(),
			selectionFDSFilterItems.isEmpty());
	}

	@Test
	public void testGetSelectionFDSFilterItemsWithCMSObjectDefinitions() {
		TermsAggregationResult termsAggregationResult = Mockito.mock(
			TermsAggregationResult.class);

		Mockito.when(
			termsAggregationResult.getBuckets()
		).thenReturn(
			Collections.emptyList()
		);

		Mockito.when(
			_searchResponse.getAggregationResult("extensions")
		).thenReturn(
			termsAggregationResult
		);

		_extensionSelectionFDSFilter.getSelectionFDSFilterItems(_locale);

		Mockito.verify(
			_searchRequestBuilder
		).entryClassNames(
			_OBJECT_DEFINITION_CLASS_NAME
		);
	}

	@Test
	public void testGetSelectionFDSFilterItemsWithExtensions() {
		Bucket pdfBucket = Mockito.mock(Bucket.class);

		Mockito.when(
			pdfBucket.getKey()
		).thenReturn(
			"pdf"
		);

		Bucket pngBucket = Mockito.mock(Bucket.class);

		Mockito.when(
			pngBucket.getKey()
		).thenReturn(
			"png"
		);

		TermsAggregationResult termsAggregationResult = Mockito.mock(
			TermsAggregationResult.class);

		Mockito.when(
			termsAggregationResult.getBuckets()
		).thenReturn(
			List.of(pdfBucket, pngBucket)
		);

		Mockito.when(
			_searchResponse.getAggregationResult("extensions")
		).thenReturn(
			termsAggregationResult
		);

		List<SelectionFDSFilterItem> selectionFDSFilterItems =
			_extensionSelectionFDSFilter.getSelectionFDSFilterItems(_locale);

		Assert.assertEquals(
			selectionFDSFilterItems.toString(), 2,
			selectionFDSFilterItems.size());

		SelectionFDSFilterItem pdfSelectionFDSFilterItem =
			selectionFDSFilterItems.get(0);

		Assert.assertEquals("pdf", pdfSelectionFDSFilterItem.getLabel());
		Assert.assertEquals("pdf", pdfSelectionFDSFilterItem.getValue());

		SelectionFDSFilterItem pngSelectionFDSFilterItem =
			selectionFDSFilterItems.get(1);

		Assert.assertEquals("png", pngSelectionFDSFilterItem.getLabel());
		Assert.assertEquals("png", pngSelectionFDSFilterItem.getValue());
	}

	private static final String _OBJECT_DEFINITION_CLASS_NAME =
		"com.liferay.object.model.ObjectDefinition#" +
			RandomTestUtil.randomString();

	@Mock
	private Aggregations _aggregations;

	@Mock
	private ComplexQueryPartBuilderFactory _complexQueryPartBuilderFactory;

	@Mock
	private DepotEntryLocalService _depotEntryLocalService;

	private final ExtensionSelectionFDSFilter _extensionSelectionFDSFilter =
		new ExtensionSelectionFDSFilter();
	private final Locale _locale = LocaleUtil.US;

	@Mock
	private ObjectDefinition _objectDefinition;

	@Mock
	private ObjectDefinitionService _objectDefinitionService;

	@Mock
	private Searcher _searcher;

	@Mock(answer = Answers.RETURNS_SELF)
	private SearchRequestBuilder _searchRequestBuilder;

	@Mock
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Mock
	private SearchResponse _searchResponse;

}