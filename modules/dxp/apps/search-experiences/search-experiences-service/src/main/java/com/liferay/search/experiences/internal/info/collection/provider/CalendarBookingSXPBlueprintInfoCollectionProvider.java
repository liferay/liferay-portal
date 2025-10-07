/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.info.collection.provider;

import com.liferay.asset.util.AssetHelper;
import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.service.CalendarBookingLocalService;
import com.liferay.info.collection.provider.CollectionQuery;
import com.liferay.info.collection.provider.FilteredInfoCollectionProvider;
import com.liferay.info.collection.provider.SingleFormVariationInfoCollectionProvider;
import com.liferay.info.pagination.InfoPage;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.search.experiences.model.SXPBlueprint;

import java.util.Collections;
import java.util.List;

/**
 * @author Joshua Cords
 */
public class CalendarBookingSXPBlueprintInfoCollectionProvider
	extends SXPBlueprintInfoCollectionProvider<CalendarBooking>
	implements FilteredInfoCollectionProvider<CalendarBooking>,
			   SingleFormVariationInfoCollectionProvider<CalendarBooking> {

	public CalendarBookingSXPBlueprintInfoCollectionProvider(
		AssetHelper assetHelper,
		CalendarBookingLocalService calendarBookingLocalService,
		Searcher searcher,
		SearchRequestBuilderFactory searchRequestBuilderFactory,
		SXPBlueprint sxpBlueprint) {

		super(assetHelper, searcher, searchRequestBuilderFactory, sxpBlueprint);

		_calendarBookingLocalService = calendarBookingLocalService;
	}

	@Override
	public InfoPage<CalendarBooking> getCollectionInfoPage(
		CollectionQuery collectionQuery) {

		try {
			SearchResponse searchResponse = getCollectionQuerySearchResponse(
				collectionQuery);

			return InfoPage.of(
				_getCalendarBookings(searchResponse.getSearchHits()),
				collectionQuery.getPagination(), searchResponse.getTotalHits());
		}
		catch (Exception exception) {
			_log.error("Unable to get calendar bookings", exception);
		}

		return InfoPage.of(
			Collections.emptyList(), collectionQuery.getPagination(), 0);
	}

	private List<CalendarBooking> _getCalendarBookings(SearchHits searchHits) {
		return TransformUtil.transform(
			searchHits.getSearchHits(),
			searchHit -> {
				Document document = searchHit.getDocument();

				return _calendarBookingLocalService.getCalendarBooking(
					document.getLong(Field.ENTRY_CLASS_PK));
			});
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CalendarBookingSXPBlueprintInfoCollectionProvider.class);

	private final CalendarBookingLocalService _calendarBookingLocalService;

}