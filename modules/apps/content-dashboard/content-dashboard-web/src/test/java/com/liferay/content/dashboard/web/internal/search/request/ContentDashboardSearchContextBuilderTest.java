/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.web.internal.search.request;

import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.content.dashboard.item.action.exception.ContentDashboardItemActionException;
import com.liferay.content.dashboard.item.filter.ContentDashboardItemFilter;
import com.liferay.content.dashboard.item.filter.provider.ContentDashboardItemFilterProvider;
import com.liferay.content.dashboard.web.internal.item.filter.ContentDashboardItemFilterProviderRegistry;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalServiceUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.RangeTermFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.context.SearchContextFactory;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.text.DateFormat;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Jürgen Kappler
 */
public class ContentDashboardSearchContextBuilderTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setUpCompanyLocalServiceUtil();
		_setUpDLFileEntryTypeLocalServiceUtil();
		_setUpPortal();
		_setUpSearchContextFactory();
	}

	@Test
	public void testBuildWithAvailableContentDashboardItemFilterProvider()
		throws ContentDashboardItemActionException {

		String parameterName = RandomTestUtil.randomString();
		String[] parameterValues = {RandomTestUtil.randomString()};

		HttpServletRequest httpServletRequest =
			_getHttpServletRequestWithParameterValues(
				parameterName, parameterValues);

		AssetCategoryLocalService assetCategoryLocalService = Mockito.mock(
			AssetCategoryLocalService.class);
		AssetVocabularyLocalService assetVocabularyLocalService = Mockito.mock(
			AssetVocabularyLocalService.class);

		ContentDashboardItemFilterProviderRegistry
			contentDashboardItemFilterProviderRegistry =
				_getContentDashboardItemFilterProviderRegistry(
					Arrays.asList(
						_getContentDashboardItemFilterProvider(
							_getContentDashboardItemFilter(
								parameterName, parameterValues),
							httpServletRequest, true)));

		ContentDashboardSearchContextBuilder
			contentDashboardSearchContextBuilder =
				new ContentDashboardSearchContextBuilder(
					httpServletRequest, assetCategoryLocalService,
					assetVocabularyLocalService,
					contentDashboardItemFilterProviderRegistry);

		Assert.assertNotNull(contentDashboardSearchContextBuilder);

		SearchContext searchContext =
			contentDashboardSearchContextBuilder.build();

		List<BooleanClause<Filter>> mustBooleanClauses = _getBooleanClauses(
			searchContext);

		Assert.assertEquals(
			mustBooleanClauses.toString(), 1, mustBooleanClauses.size());

		BooleanClause<Filter> filterBooleanClause = mustBooleanClauses.get(0);

		TermsFilter termsFilter = (TermsFilter)filterBooleanClause.getClause();

		Assert.assertEquals(parameterName, termsFilter.getField());
		Assert.assertEquals(parameterValues, termsFilter.getValues());
	}

	@Test
	public void testBuildWithCustomDateFilter() {
		DateFormat simpleDateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd");

		Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		Calendar startCalendar = (Calendar)calendar.clone();

		calendar.add(Calendar.DATE, 1);

		Calendar endCalendar = (Calendar)calendar.clone();

		HttpServletRequest httpServletRequest =
			_getHttpServletRequestWithParameters(
				HashMapBuilder.put(
					"dateType", "create-date"
				).put(
					"endDate",
					simpleDateFormat.format(endCalendar.getTimeInMillis())
				).put(
					"startDate",
					simpleDateFormat.format(startCalendar.getTimeInMillis())
				).build());

		AssetCategoryLocalService assetCategoryLocalService = Mockito.mock(
			AssetCategoryLocalService.class);
		AssetVocabularyLocalService assetVocabularyLocalService = Mockito.mock(
			AssetVocabularyLocalService.class);

		ContentDashboardItemFilterProviderRegistry
			contentDashboardItemFilterProviderRegistry =
				_getContentDashboardItemFilterProviderRegistry(
					Collections.emptyList());

		ContentDashboardSearchContextBuilder
			contentDashboardSearchContextBuilder =
				new ContentDashboardSearchContextBuilder(
					httpServletRequest, assetCategoryLocalService,
					assetVocabularyLocalService,
					contentDashboardItemFilterProviderRegistry);

		Assert.assertNotNull(contentDashboardSearchContextBuilder);

		SearchContext searchContext =
			contentDashboardSearchContextBuilder.build();

		List<BooleanClause<Filter>> mustBooleanClauses = _getBooleanClauses(
			searchContext);

		Assert.assertEquals(
			mustBooleanClauses.toString(), 1, mustBooleanClauses.size());

		BooleanClause<Filter> filterBooleanClause = mustBooleanClauses.get(0);

		RangeTermFilter rangeTermFilter =
			(RangeTermFilter)filterBooleanClause.getClause();

		Assert.assertEquals(
			Field.getSortableFieldName(Field.CREATE_DATE),
			rangeTermFilter.getField());
		Assert.assertEquals(
			String.valueOf(startCalendar.getTimeInMillis()),
			rangeTermFilter.getLowerBound());

		endCalendar.add(Calendar.DATE, 1);

		Assert.assertEquals(
			String.valueOf(endCalendar.getTimeInMillis()),
			rangeTermFilter.getUpperBound());

		Assert.assertTrue(rangeTermFilter.isIncludesLower());
		Assert.assertFalse(rangeTermFilter.isIncludesUpper());
	}

	@Test
	public void testBuildWithCustomDateFilterWithInvalidDates() {
		HttpServletRequest httpServletRequest =
			_getHttpServletRequestWithParameters(
				HashMapBuilder.put(
					"dateType", RandomTestUtil.randomString()
				).put(
					"endDate", RandomTestUtil.randomString()
				).put(
					"startDate", RandomTestUtil.randomString()
				).build());

		AssetCategoryLocalService assetCategoryLocalService = Mockito.mock(
			AssetCategoryLocalService.class);
		AssetVocabularyLocalService assetVocabularyLocalService = Mockito.mock(
			AssetVocabularyLocalService.class);

		ContentDashboardItemFilterProviderRegistry
			contentDashboardItemFilterProviderRegistry =
				_getContentDashboardItemFilterProviderRegistry(
					Collections.emptyList());

		ContentDashboardSearchContextBuilder
			contentDashboardSearchContextBuilder =
				new ContentDashboardSearchContextBuilder(
					httpServletRequest, assetCategoryLocalService,
					assetVocabularyLocalService,
					contentDashboardItemFilterProviderRegistry);

		Assert.assertNotNull(contentDashboardSearchContextBuilder);

		SearchContext searchContext =
			contentDashboardSearchContextBuilder.build();

		List<BooleanClause<Filter>> mustBooleanClauses = _getBooleanClauses(
			searchContext);

		Assert.assertEquals(
			mustBooleanClauses.toString(), 0, mustBooleanClauses.size());
	}

	@Test
	public void testBuildWithCustomDateFilterWithInvalidDateType() {
		DateFormat simpleDateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd");

		HttpServletRequest httpServletRequest =
			_getHttpServletRequestWithParameters(
				HashMapBuilder.put(
					"dateType", RandomTestUtil.randomString()
				).put(
					"endDate", simpleDateFormat.format(new Date())
				).put(
					"startDate", simpleDateFormat.format(new Date())
				).build());

		AssetCategoryLocalService assetCategoryLocalService = Mockito.mock(
			AssetCategoryLocalService.class);
		AssetVocabularyLocalService assetVocabularyLocalService = Mockito.mock(
			AssetVocabularyLocalService.class);

		ContentDashboardItemFilterProviderRegistry
			contentDashboardItemFilterProviderRegistry =
				_getContentDashboardItemFilterProviderRegistry(
					Collections.emptyList());

		ContentDashboardSearchContextBuilder
			contentDashboardSearchContextBuilder =
				new ContentDashboardSearchContextBuilder(
					httpServletRequest, assetCategoryLocalService,
					assetVocabularyLocalService,
					contentDashboardItemFilterProviderRegistry);

		Assert.assertNotNull(contentDashboardSearchContextBuilder);

		SearchContext searchContext =
			contentDashboardSearchContextBuilder.build();

		List<BooleanClause<Filter>> mustBooleanClauses = _getBooleanClauses(
			searchContext);

		Assert.assertEquals(
			mustBooleanClauses.toString(), 0, mustBooleanClauses.size());
	}

	@Test
	public void testBuildWithUnavailableContentDashboardItemFilterProvider()
		throws ContentDashboardItemActionException {

		String parameterName = RandomTestUtil.randomString();
		String[] parameterValues = {RandomTestUtil.randomString()};

		HttpServletRequest httpServletRequest =
			_getHttpServletRequestWithParameterValues(
				parameterName, parameterValues);

		AssetCategoryLocalService assetCategoryLocalService = Mockito.mock(
			AssetCategoryLocalService.class);
		AssetVocabularyLocalService assetVocabularyLocalService = Mockito.mock(
			AssetVocabularyLocalService.class);

		ContentDashboardItemFilterProviderRegistry
			contentDashboardItemFilterProviderRegistry =
				_getContentDashboardItemFilterProviderRegistry(
					Arrays.asList(
						_getContentDashboardItemFilterProvider(
							_getContentDashboardItemFilter(
								parameterName, parameterValues),
							httpServletRequest, false)));

		ContentDashboardSearchContextBuilder
			contentDashboardSearchContextBuilder =
				new ContentDashboardSearchContextBuilder(
					httpServletRequest, assetCategoryLocalService,
					assetVocabularyLocalService,
					contentDashboardItemFilterProviderRegistry);

		Assert.assertNotNull(contentDashboardSearchContextBuilder);

		SearchContext searchContext =
			contentDashboardSearchContextBuilder.build();

		List<BooleanClause<Filter>> mustBooleanClauses = _getBooleanClauses(
			searchContext);

		Assert.assertEquals(
			mustBooleanClauses.toString(), 0, mustBooleanClauses.size());
	}

	private List<BooleanClause<Filter>> _getBooleanClauses(
		SearchContext searchContext) {

		BooleanClause<Query>[] booleanClauses =
			searchContext.getBooleanClauses();

		Assert.assertNotNull(booleanClauses);

		Assert.assertEquals(
			booleanClauses.toString(), 1, booleanClauses.length);

		BooleanClause<Query> booleanClause = booleanClauses[0];

		BooleanQuery booleanQuery = (BooleanQuery)booleanClause.getClause();

		BooleanFilter preBooleanFilter = booleanQuery.getPreBooleanFilter();

		return preBooleanFilter.getMustBooleanClauses();
	}

	private ContentDashboardItemFilter _getContentDashboardItemFilter(
		String parameterName, String... parameterValues) {

		ContentDashboardItemFilter contentDashboardItemFilter = Mockito.mock(
			ContentDashboardItemFilter.class);

		TermsFilter termsFilter = new TermsFilter(parameterName);

		for (String parameterValue : parameterValues) {
			termsFilter.addValue(parameterValue);
		}

		Mockito.when(
			contentDashboardItemFilter.getFilter()
		).thenReturn(
			termsFilter
		);

		return contentDashboardItemFilter;
	}

	private ContentDashboardItemFilterProvider
			_getContentDashboardItemFilterProvider(
				ContentDashboardItemFilter contentDashboardItemFilter,
				HttpServletRequest httpServletRequest, boolean show)
		throws ContentDashboardItemActionException {

		ContentDashboardItemFilterProvider contentDashboardItemFilterProvider =
			Mockito.mock(ContentDashboardItemFilterProvider.class);

		Mockito.when(
			contentDashboardItemFilterProvider.getContentDashboardItemFilter(
				httpServletRequest)
		).thenReturn(
			contentDashboardItemFilter
		);

		Mockito.when(
			contentDashboardItemFilterProvider.isShow(httpServletRequest)
		).thenReturn(
			show
		);

		return contentDashboardItemFilterProvider;
	}

	private ContentDashboardItemFilterProviderRegistry
		_getContentDashboardItemFilterProviderRegistry(
			List<ContentDashboardItemFilterProvider>
				contentDashboardItemFilterProviders) {

		ContentDashboardItemFilterProviderRegistry
			contentDashboardItemFilterProviderRegistry = Mockito.mock(
				ContentDashboardItemFilterProviderRegistry.class);

		Mockito.when(
			contentDashboardItemFilterProviderRegistry.
				getContentDashboardItemFilterProviders()
		).thenReturn(
			contentDashboardItemFilterProviders
		);

		return contentDashboardItemFilterProviderRegistry;
	}

	private HttpServletRequest _getHttpServletRequestWithParameters(
		Map<String, String> parameters) {

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			Mockito.when(
				httpServletRequest.getParameter(entry.getKey())
			).thenReturn(
				entry.getValue()
			);
		}

		return httpServletRequest;
	}

	private HttpServletRequest _getHttpServletRequestWithParameterValues(
		String parameterName, String... parameterValues) {

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		Mockito.when(
			httpServletRequest.getParameterValues(parameterName)
		).thenReturn(
			parameterValues
		);

		return httpServletRequest;
	}

	private void _setUpCompanyLocalServiceUtil() throws Exception {
		CompanyLocalServiceUtil companyLocalServiceUtil =
			new CompanyLocalServiceUtil();

		Company company = Mockito.mock(Company.class);

		Mockito.when(
			_companyLocalService.getCompany(Mockito.anyLong())
		).thenReturn(
			company
		);

		companyLocalServiceUtil.setService(_companyLocalService);
	}

	private void _setUpDLFileEntryTypeLocalServiceUtil() {
		DLFileEntryTypeLocalServiceUtil dlFileEntryTypeLocalServiceUtil =
			new DLFileEntryTypeLocalServiceUtil();

		Mockito.when(
			_dlFileEntryTypeLocalService.fetchFileEntryType(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			null
		);

		dlFileEntryTypeLocalServiceUtil.setService(
			_dlFileEntryTypeLocalService);
	}

	private void _setUpPortal() {
		PortalUtil portalUtil = new PortalUtil();

		Mockito.when(
			_portal.getCompanyId(Mockito.any(HttpServletRequest.class))
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		portalUtil.setPortal(_portal);
	}

	private void _setUpSearchContextFactory() {
		Mockito.doReturn(
			new SearchContext()
		).when(
			_searchContextFactory
		).getSearchContext(
			Mockito.any(), Mockito.any(), Mockito.anyLong(), Mockito.any(),
			Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyLong(),
			Mockito.any(), Mockito.anyLong()
		);
	}

	private final CompanyLocalService _companyLocalService = Mockito.mock(
		CompanyLocalService.class);
	private final DLFileEntryTypeLocalService _dlFileEntryTypeLocalService =
		Mockito.mock(DLFileEntryTypeLocalService.class);
	private final Portal _portal = Mockito.mock(Portal.class);
	private final SearchContextFactory _searchContextFactory = Mockito.mock(
		SearchContextFactory.class);

}