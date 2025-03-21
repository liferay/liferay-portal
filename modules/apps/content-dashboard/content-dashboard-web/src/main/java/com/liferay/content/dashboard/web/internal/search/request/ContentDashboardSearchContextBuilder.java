/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.web.internal.search.request;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.content.dashboard.item.action.exception.ContentDashboardItemActionException;
import com.liferay.content.dashboard.item.filter.ContentDashboardItemFilter;
import com.liferay.content.dashboard.item.filter.provider.ContentDashboardItemFilterProvider;
import com.liferay.content.dashboard.web.internal.constants.ContentDashboardConstants;
import com.liferay.content.dashboard.web.internal.item.filter.ContentDashboardItemFilterProviderRegistry;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.ExistsFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.RangeTermFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * @author Cristina González
 */
public class ContentDashboardSearchContextBuilder {

	public ContentDashboardSearchContextBuilder(
		HttpServletRequest httpServletRequest,
		AssetCategoryLocalService assetCategoryLocalService,
		AssetVocabularyLocalService assetVocabularyLocalService,
		ContentDashboardItemFilterProviderRegistry
			contentDashboardItemFilterProviderRegistry) {

		_httpServletRequest = httpServletRequest;
		_assetCategoryLocalService = assetCategoryLocalService;
		_assetVocabularyLocalService = assetVocabularyLocalService;
		_contentDashboardItemFilterProviderRegistry =
			contentDashboardItemFilterProviderRegistry;
	}

	public SearchContext build() {
		SearchContext searchContext = SearchContextFactory.getInstance(
			_httpServletRequest);

		int status = ParamUtil.getInteger(
			_httpServletRequest, "status", WorkflowConstants.STATUS_ANY);

		if (status == WorkflowConstants.STATUS_APPROVED) {
			searchContext.setAttribute("head", Boolean.TRUE);
		}
		else {
			searchContext.setAttribute("latest", Boolean.TRUE);
		}

		searchContext.setAttribute("status", status);
		searchContext.setBooleanClauses(_getBooleanClauses());

		String[] contentDashboardItemSubtypePayloads =
			ParamUtil.getParameterValues(
				_httpServletRequest, "contentDashboardItemSubtypePayload",
				new String[0], false);

		if (ArrayUtil.isNotEmpty(contentDashboardItemSubtypePayloads)) {
			searchContext.setClassTypeIds(
				TransformUtil.transformToLongArray(
					Arrays.asList(contentDashboardItemSubtypePayloads),
					contentDashboardItemSubtypePayload -> {
						try {
							JSONObject jsonObject =
								JSONFactoryUtil.createJSONObject(
									contentDashboardItemSubtypePayload);

							if (jsonObject.isNull("classPK")) {
								return null;
							}

							return jsonObject.getLong("classPK");
						}
						catch (JSONException jsonException) {
							_log.error(jsonException);

							return null;
						}
					}));
		}

		if (_end != null) {
			searchContext.setEnd(_end);
		}

		if (ArrayUtil.isNotEmpty(contentDashboardItemSubtypePayloads)) {
			searchContext.setEntryClassNames(
				TransformUtil.transform(
					contentDashboardItemSubtypePayloads,
					contentDashboardItemSubtypePayload -> {
						try {
							JSONObject jsonObject =
								JSONFactoryUtil.createJSONObject(
									contentDashboardItemSubtypePayload);

							String entryClassName = jsonObject.getString(
								Field.ENTRY_CLASS_NAME);

							if (Validator.isNull(entryClassName)) {
								return null;
							}

							return entryClassName;
						}
						catch (JSONException jsonException) {
							_log.error(jsonException);

							return null;
						}
					},
					String.class));
		}

		long groupId = ParamUtil.getLong(_httpServletRequest, "scopeId");

		if (groupId > 0) {
			searchContext.setGroupIds(new long[] {groupId});
		}
		else {
			searchContext.setGroupIds(null);
		}

		searchContext.setIncludeInternalAssetCategories(true);
		searchContext.setIncludeStagingGroups(Boolean.FALSE);

		if (ArrayUtil.isNotEmpty(_sort)) {
			searchContext.setSorts(_sort);
		}

		if (_start != null) {
			searchContext.setStart(_start);
		}

		return searchContext;
	}

	public ContentDashboardSearchContextBuilder withEnd(int end) {
		_end = end;

		return this;
	}

	public ContentDashboardSearchContextBuilder withSort(Sort... sort) {
		_sort = sort;

		return this;
	}

	public ContentDashboardSearchContextBuilder withStart(int start) {
		_start = start;

		return this;
	}

	private Filter _getAssetCategoryIdsFilter() {
		AssetCategoryIds assetCategoryIds = new AssetCategoryIds(
			ParamUtil.getLongValues(_httpServletRequest, "assetCategoryId"),
			_assetCategoryLocalService, _assetVocabularyLocalService);

		if ((assetCategoryIds == null) ||
			(ArrayUtil.isEmpty(
				assetCategoryIds.getExternalAssetCategoryIds()) &&
			 ArrayUtil.isEmpty(
				 assetCategoryIds.getInternalAssetCategoryIds()))) {

			return null;
		}

		BooleanFilter booleanFilter = new BooleanFilter();

		if (ArrayUtil.isNotEmpty(
				assetCategoryIds.getExternalAssetCategoryIds())) {

			booleanFilter.add(
				_getTermsFilter(
					Field.ASSET_CATEGORY_IDS,
					assetCategoryIds.getExternalAssetCategoryIds()),
				BooleanClauseOccur.MUST);
		}

		if (ArrayUtil.isNotEmpty(
				assetCategoryIds.getInternalAssetCategoryIds())) {

			booleanFilter.add(
				_getTermsFilter(
					Field.ASSET_INTERNAL_CATEGORY_IDS,
					assetCategoryIds.getInternalAssetCategoryIds()),
				BooleanClauseOccur.MUST);
		}

		return booleanFilter;
	}

	private Filter _getAssetTagNamesFilter() {
		String[] assetTagNames = ParamUtil.getStringValues(
			_httpServletRequest, "assetTagId");

		if (ArrayUtil.isEmpty(assetTagNames)) {
			return null;
		}

		BooleanFilter booleanFilter = new BooleanFilter();

		for (String assetTagName : assetTagNames) {
			booleanFilter.addTerm(
				Field.ASSET_TAG_NAMES + ".raw", assetTagName,
				BooleanClauseOccur.MUST);
		}

		return booleanFilter;
	}

	private Filter _getAuthorIdsFilter() {
		long[] authorIds = ParamUtil.getLongValues(
			_httpServletRequest, "authorIds");

		if (ArrayUtil.isEmpty(authorIds)) {
			return null;
		}

		TermsFilter termsFilter = new TermsFilter(Field.USER_ID);

		for (long authorId : authorIds) {
			termsFilter.addValue(String.valueOf(authorId));
		}

		return termsFilter;
	}

	private BooleanClause[] _getBooleanClauses() {
		BooleanQueryImpl booleanQueryImpl = new BooleanQueryImpl();

		BooleanFilter booleanFilter = new BooleanFilter();

		for (Filter filter :
				Arrays.asList(
					_getAssetCategoryIdsFilter(), _getAssetTagNamesFilter(),
					_getAuthorIdsFilter(), _getDateTypeRangeFilter(),
					_getGoogleDriveShortcutFilter(), _getReviewDateFilter())) {

			if (filter != null) {
				booleanFilter.add(filter, BooleanClauseOccur.MUST);
			}
		}

		for (ContentDashboardItemFilterProvider
				contentDashboardItemFilterProvider :
					_contentDashboardItemFilterProviderRegistry.
						getContentDashboardItemFilterProviders()) {

			if (!contentDashboardItemFilterProvider.isShow(
					_httpServletRequest)) {

				continue;
			}

			try {
				ContentDashboardItemFilter contentDashboardItemFilter =
					contentDashboardItemFilterProvider.
						getContentDashboardItemFilter(_httpServletRequest);

				Filter filter = contentDashboardItemFilter.getFilter();

				if (filter != null) {
					booleanFilter.add(filter, BooleanClauseOccur.MUST);
				}
			}
			catch (ContentDashboardItemActionException
						contentDashboardItemActionException) {

				if (_log.isDebugEnabled()) {
					_log.debug(contentDashboardItemActionException);
				}
			}
		}

		booleanQueryImpl.setPreBooleanFilter(booleanFilter);

		return new BooleanClause[] {
			BooleanClauseFactoryUtil.create(
				booleanQueryImpl, BooleanClauseOccur.MUST.getName())
		};
	}

	private Filter _getDateTypeRangeFilter() {
		String dateType = ParamUtil.getString(_httpServletRequest, "dateType");
		String endDateString = ParamUtil.getString(
			_httpServletRequest, "endDate");
		String startDateString = ParamUtil.getString(
			_httpServletRequest, "startDate");

		if (Validator.isNull(dateType) && Validator.isNull(endDateString) &&
			Validator.isNull(startDateString)) {

			return null;
		}

		ContentDashboardConstants.DateType filterDateType =
			ContentDashboardConstants.DateType.parse(dateType);

		if (filterDateType == null) {
			return null;
		}

		DateFormat simpleDateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd");

		Calendar endDateCalendar = Calendar.getInstance();
		Calendar startDateCalendar = Calendar.getInstance();

		try {
			endDateCalendar.setTime(simpleDateFormat.parse(endDateString));

			endDateCalendar.add(Calendar.DATE, 1);

			startDateCalendar.setTime(simpleDateFormat.parse(startDateString));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return null;
		}

		return new RangeTermFilter(
			Field.getSortableFieldName(filterDateType.getField()), true, false,
			String.valueOf(startDateCalendar.getTimeInMillis()),
			String.valueOf(endDateCalendar.getTimeInMillis()));
	}

	private Filter _getGoogleDriveShortcutFilter() {
		long companyId = PortalUtil.getCompanyId(_httpServletRequest);

		try {
			Company company = CompanyLocalServiceUtil.getCompany(companyId);

			DLFileEntryType googleDocsDLFileEntryType =
				DLFileEntryTypeLocalServiceUtil.fetchFileEntryType(
					company.getGroupId(), "GOOGLE_DOCS");

			if (googleDocsDLFileEntryType == null) {
				return null;
			}

			BooleanFilter booleanFilter = new BooleanFilter();

			booleanFilter.addTerm(
				"classTypeId",
				String.valueOf(googleDocsDLFileEntryType.getFileEntryTypeId()),
				BooleanClauseOccur.MUST_NOT);

			return booleanFilter;
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return null;
	}

	private Filter _getReviewDateFilter() {
		String reviewDateString = ParamUtil.getString(
			_httpServletRequest, "reviewDate");

		if (Validator.isNull(reviewDateString)) {
			return null;
		}

		ExistsFilter existsFilter = new ExistsFilter("reviewDate");

		BooleanFilter existBooleanFilter = new BooleanFilter();

		existBooleanFilter.add(existsFilter, BooleanClauseOccur.MUST);

		return existBooleanFilter;
	}

	private BooleanFilter _getTermsFilter(String field, long[] values) {
		BooleanFilter booleanFilter = new BooleanFilter();

		for (Long value : values) {
			booleanFilter.addTerm(
				field, String.valueOf(value), BooleanClauseOccur.MUST);
		}

		return booleanFilter;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ContentDashboardSearchContextBuilder.class);

	private final AssetCategoryLocalService _assetCategoryLocalService;
	private final AssetVocabularyLocalService _assetVocabularyLocalService;
	private final ContentDashboardItemFilterProviderRegistry
		_contentDashboardItemFilterProviderRegistry;
	private Integer _end;
	private final HttpServletRequest _httpServletRequest;
	private Sort[] _sort;
	private Integer _start;

	private static class AssetCategoryIds {

		public AssetCategoryIds(
			long[] assetCategoryIds,
			AssetCategoryLocalService assetCategoryLocalService,
			AssetVocabularyLocalService assetVocabularyLocalService) {

			List<Long> externalAssetCategoryIds = new ArrayList<>();
			List<Long> internalAssetCategoryIds = new ArrayList<>();

			for (long assetCategoryId : assetCategoryIds) {
				AssetCategory assetCategory =
					assetCategoryLocalService.fetchAssetCategory(
						assetCategoryId);

				AssetVocabulary assetVocabulary =
					assetVocabularyLocalService.fetchAssetVocabulary(
						assetCategory.getVocabularyId());

				if (assetVocabulary.getVisibilityType() ==
						AssetVocabularyConstants.VISIBILITY_TYPE_INTERNAL) {

					internalAssetCategoryIds.add(assetCategoryId);
				}
				else {
					externalAssetCategoryIds.add(assetCategoryId);
				}
			}

			_externalAssetCategoryIds = ArrayUtil.toLongArray(
				externalAssetCategoryIds);
			_internalAssetCategoryIds = ArrayUtil.toLongArray(
				internalAssetCategoryIds);
		}

		public long[] getExternalAssetCategoryIds() {
			return _externalAssetCategoryIds;
		}

		public long[] getInternalAssetCategoryIds() {
			return _internalAssetCategoryIds;
		}

		private final long[] _externalAssetCategoryIds;
		private final long[] _internalAssetCategoryIds;

	}

}