/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.facet.display.context.builder;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.search.web.internal.facet.display.context.AssetCategoriesSearchFacetDisplayContext;
import com.liferay.portal.search.web.internal.facet.display.context.BucketDisplayContext;
import com.liferay.portal.search.web.internal.util.comparator.BucketDisplayContextComparatorFactoryUtil;

import jakarta.portlet.RenderRequest;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Lino Alves
 */
public class AssetCategoriesSearchFacetDisplayContextBuilder
	implements Serializable {

	public AssetCategoriesSearchFacetDisplayContextBuilder(
		RenderRequest renderRequest) {

		_renderRequest = renderRequest;
	}

	public AssetCategoriesSearchFacetDisplayContext build() {
		_buckets = _collectBuckets(_facet);

		AssetCategoriesSearchFacetDisplayContext
			assetCategoriesSearchFacetDisplayContext =
				_createAssetCategoriesSearchFacetDisplayContext();

		List<BucketDisplayContext> bucketDisplayContexts =
			_getBucketDisplayContexts();

		assetCategoriesSearchFacetDisplayContext.setBucketDisplayContexts(
			bucketDisplayContexts);

		Map<String, List<BucketDisplayContext>> bucketDisplayContextsMap =
			_getBucketDisplayContextsMap(bucketDisplayContexts);

		assetCategoriesSearchFacetDisplayContext.setBucketDisplayContextsMap(
			bucketDisplayContextsMap);

		assetCategoriesSearchFacetDisplayContext.setCloud(_isCloud());
		assetCategoriesSearchFacetDisplayContext.setNothingSelected(
			isNothingSelected());
		assetCategoriesSearchFacetDisplayContext.
			setPaginationStartParameterName(_paginationStartParameterName);
		assetCategoriesSearchFacetDisplayContext.setParameterName(
			_parameterName);
		assetCategoriesSearchFacetDisplayContext.setParameterValue(
			getFirstParameterValueString());
		assetCategoriesSearchFacetDisplayContext.setParameterValues(
			getParameterValueStrings());
		assetCategoriesSearchFacetDisplayContext.setRenderNothing(
			isRenderNothing());
		assetCategoriesSearchFacetDisplayContext.setVocabularyNames(
			_sortVocabularyNames(bucketDisplayContextsMap.keySet()));

		return assetCategoriesSearchFacetDisplayContext;
	}

	public long getExcludedGroupId() {
		return _excludedGroupId;
	}

	public void setAssetCategoryLocalService(
		AssetCategoryLocalService assetCategoryLocalService) {

		_assetCategoryLocalService = assetCategoryLocalService;
	}

	public void setAssetCategoryPermissionChecker(
		AssetCategoryPermissionChecker assetCategoryPermissionChecker) {

		_assetCategoryPermissionChecker = assetCategoryPermissionChecker;
	}

	public void setAssetVocabularyLocalService(
		AssetVocabularyLocalService assetVocabularyLocalService) {

		_assetVocabularyLocalService = assetVocabularyLocalService;
	}

	public void setDisplayStyle(String displayStyle) {
		_displayStyle = displayStyle;
	}

	public void setExcludedGroupId(long excludedGroupId) {
		_excludedGroupId = excludedGroupId;
	}

	public void setFacet(Facet facet) {
		_facet = facet;
	}

	public void setFrequenciesVisible(boolean frequenciesVisible) {
		_frequenciesVisible = frequenciesVisible;
	}

	public void setFrequencyThreshold(int frequencyThreshold) {
		_frequencyThreshold = frequencyThreshold;
	}

	public void setLocale(Locale locale) {
		_locale = locale;
	}

	public void setMaxTerms(int maxTerms) {
		_maxTerms = maxTerms;
	}

	public void setOrder(String order) {
		_order = order;
	}

	public void setPaginationStartParameterName(
		String paginationStartParameterName) {

		_paginationStartParameterName = paginationStartParameterName;
	}

	public void setParameterName(String parameterName) {
		_parameterName = parameterName;
	}

	public void setParameterValue(String parameterValue) {
		setParameterValues(GetterUtil.getString(parameterValue));
	}

	public void setParameterValues(String... parameterValues) {
		_selectedCategoryIds = TransformUtil.transformToList(
			parameterValues,
			parameterValue -> {
				if (parameterValue.equals(StringPool.BLANK)) {
					return null;
				}

				return GetterUtil.getLong(parameterValue);
			});
	}

	public void setPortal(Portal portal) {
		_portal = portal;
	}

	protected BucketDisplayContext buildBucketDisplayContext(
		AssetCategory assetCategory, int frequency, boolean selected,
		int popularity) {

		BucketDisplayContext bucketDisplayContext = new BucketDisplayContext();

		bucketDisplayContext.setBucketText(assetCategory.getTitle(_locale));
		bucketDisplayContext.setFilterValue(
			String.valueOf(assetCategory.getCategoryId()));
		bucketDisplayContext.setFrequency(frequency);
		bucketDisplayContext.setFrequencyVisible(_frequenciesVisible);
		bucketDisplayContext.setLocale(_locale);
		bucketDisplayContext.setPopularity(popularity);
		bucketDisplayContext.setSelected(selected);

		return bucketDisplayContext;
	}

	protected List<BucketDisplayContext> getEmptyBucketDisplayContexts() {
		return TransformUtil.transform(
			_selectedCategoryIds, this::_getEmptyBucketDisplayContext);
	}

	protected String getFirstParameterValueString() {
		if (_selectedCategoryIds.isEmpty()) {
			return StringPool.BLANK;
		}

		return String.valueOf(_selectedCategoryIds.get(0));
	}

	protected List<String> getParameterValueStrings() {
		return TransformUtil.transform(_selectedCategoryIds, String::valueOf);
	}

	protected double getPopularity(
		int frequency, int maxCount, int minCount, double multiplier) {

		double popularity = maxCount - (maxCount - (frequency - minCount));

		return 1 + (popularity * multiplier);
	}

	protected boolean isNothingSelected() {
		return _selectedCategoryIds.isEmpty();
	}

	protected boolean isRenderNothing() {
		if (isNothingSelected() && _buckets.isEmpty()) {
			return true;
		}

		return false;
	}

	protected boolean isSelected(long categoryId) {
		return _selectedCategoryIds.contains(categoryId);
	}

	private List<Tuple> _collectBuckets(Facet facet) {
		if (facet == null) {
			return Collections.emptyList();
		}

		FacetCollector facetCollector = facet.getFacetCollector();

		return TransformUtil.transform(
			facetCollector.getTermCollectors(),
			termCollector -> {
				long assetCategoryId = 0;

				String fieldName = facet.getFieldName();

				if ((fieldName != null) &&
					fieldName.equals("assetVocabularyCategoryIds")) {

					String[] parts = StringUtil.split(
						termCollector.getTerm(), StringPool.DASH);

					assetCategoryId = GetterUtil.getLong(parts[1]);
				}
				else {
					assetCategoryId = GetterUtil.getLong(
						termCollector.getTerm());
				}

				if (assetCategoryId <= 0) {
					return null;
				}

				AssetCategory assetCategory = _fetchAssetCategory(
					assetCategoryId);

				if (assetCategory == null) {
					return null;
				}

				return new Tuple(assetCategory, termCollector.getFrequency());
			});
	}

	private AssetCategoriesSearchFacetDisplayContext
		_createAssetCategoriesSearchFacetDisplayContext() {

		try {
			return new AssetCategoriesSearchFacetDisplayContext(
				_portal.getHttpServletRequest(_renderRequest));
		}
		catch (ConfigurationException configurationException) {
			throw new RuntimeException(configurationException);
		}
	}

	private AssetCategory _fetchAssetCategory(long assetCategoryId) {
		AssetCategory assetCategory =
			_assetCategoryLocalService.fetchAssetCategory(assetCategoryId);

		if ((assetCategory != null) &&
			_assetCategoryPermissionChecker.hasPermission(assetCategory)) {

			return assetCategory;
		}

		return null;
	}

	private void _filterBuckets() {
		if (_buckets.isEmpty()) {
			return;
		}

		_buckets = ListUtil.filter(
			_buckets,
			tuple -> {
				if (_excludedGroupId == 0) {
					return true;
				}

				AssetCategory assetCategory = (AssetCategory)tuple.getObject(0);

				if (assetCategory.getGroupId() == _excludedGroupId) {
					return false;
				}

				return true;
			});
	}

	private List<BucketDisplayContext> _getBucketDisplayContexts() {
		if (_buckets.isEmpty()) {
			return getEmptyBucketDisplayContexts();
		}

		_filterBuckets();

		int maxCount = 1;
		int minCount = 1;

		if (_frequenciesVisible && _displayStyle.equals("cloud")) {

			// The cloud style may not list tags in the order of frequency.
			// Keep looking through the results until we reach the maximum
			// number of terms or we run out of terms.

			for (int i = 0, j = 0; i < _buckets.size(); i++, j++) {
				if (j >= _maxTerms) {
					break;
				}

				Tuple tuple = _buckets.get(i);

				Integer frequency = (Integer)tuple.getObject(1);

				if (_frequencyThreshold > frequency) {
					j--;

					continue;
				}

				maxCount = Math.max(maxCount, frequency);
				minCount = Math.min(minCount, frequency);
			}
		}

		double multiplier = 1;

		if (maxCount != minCount) {
			multiplier = (double)5 / (maxCount - minCount);
		}

		List<BucketDisplayContext> bucketDisplayContexts = new ArrayList<>(
			_buckets.size());

		for (int i = 0, j = 0; i < _buckets.size(); i++, j++) {
			if ((_maxTerms > 0) && (j >= _maxTerms)) {
				break;
			}

			Tuple tuple = _buckets.get(i);

			Integer frequency = (Integer)tuple.getObject(1);

			if (_frequencyThreshold > frequency) {
				j--;

				continue;
			}

			int popularity = (int)getPopularity(
				frequency, maxCount, minCount, multiplier);

			AssetCategory assetCategory = (AssetCategory)tuple.getObject(0);

			BucketDisplayContext bucketDisplayContext =
				buildBucketDisplayContext(
					assetCategory, frequency,
					isSelected(assetCategory.getCategoryId()), popularity);

			bucketDisplayContexts.add(bucketDisplayContext);

			if (_order != null) {
				bucketDisplayContexts.sort(
					BucketDisplayContextComparatorFactoryUtil.
						getBucketDisplayContextComparator(_order));
			}
		}

		return bucketDisplayContexts;
	}

	private Map<String, List<BucketDisplayContext>>
		_getBucketDisplayContextsMap(
			List<BucketDisplayContext> bucketDisplayContexts) {

		Map<String, List<BucketDisplayContext>> bucketDisplayContextsMap =
			new HashMap<>();

		for (BucketDisplayContext bucketDisplayContext :
				bucketDisplayContexts) {

			AssetCategory assetCategory =
				_assetCategoryLocalService.fetchAssetCategory(
					Long.valueOf(bucketDisplayContext.getFilterValue()));

			AssetVocabulary assetVocabulary =
				_assetVocabularyLocalService.fetchAssetVocabulary(
					assetCategory.getVocabularyId());

			String title = assetVocabulary.getTitle(_locale);

			List<BucketDisplayContext> curBucketDisplayContexts =
				bucketDisplayContextsMap.get(title);

			if (curBucketDisplayContexts == null) {
				curBucketDisplayContexts = new ArrayList<>();
			}

			curBucketDisplayContexts.add(bucketDisplayContext);

			if (_order != null) {
				curBucketDisplayContexts.sort(
					BucketDisplayContextComparatorFactoryUtil.
						getBucketDisplayContextComparator(_order));
			}

			bucketDisplayContextsMap.put(title, curBucketDisplayContexts);
		}

		return bucketDisplayContextsMap;
	}

	private BucketDisplayContext _getEmptyBucketDisplayContext(
		long assetCategoryId) {

		AssetCategory assetCategory = _fetchAssetCategory(assetCategoryId);

		if (assetCategory == null) {
			return null;
		}

		return buildBucketDisplayContext(assetCategory, 0, true, 1);
	}

	private boolean _isCloud() {
		if (_frequenciesVisible && _displayStyle.equals("cloud")) {
			return true;
		}

		return false;
	}

	private List<String> _sortVocabularyNames(Set<String> vocabularyNamesSet) {
		List<String> vocabularyNames = ListUtil.fromCollection(
			vocabularyNamesSet);

		Collections.sort(vocabularyNames);

		return vocabularyNames;
	}

	private AssetCategoryLocalService _assetCategoryLocalService;
	private AssetCategoryPermissionChecker _assetCategoryPermissionChecker;
	private AssetVocabularyLocalService _assetVocabularyLocalService;
	private List<Tuple> _buckets;
	private String _displayStyle;
	private long _excludedGroupId;
	private Facet _facet;
	private boolean _frequenciesVisible;
	private int _frequencyThreshold;
	private Locale _locale;
	private int _maxTerms;
	private String _order;
	private String _paginationStartParameterName;
	private String _parameterName;
	private Portal _portal;
	private final RenderRequest _renderRequest;
	private List<Long> _selectedCategoryIds = Collections.emptyList();

}