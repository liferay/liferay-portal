/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.facet.display.context.builder;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.search.facet.config.FacetConfiguration;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.web.internal.facet.display.context.AssetTagsSearchFacetDisplayContext;
import com.liferay.portal.search.web.internal.facet.display.context.BucketDisplayContext;
import com.liferay.portal.search.web.internal.tag.facet.configuration.TagFacetPortletInstanceConfiguration;
import com.liferay.portal.search.web.internal.util.DisplayContextHelperUtil;
import com.liferay.portal.search.web.internal.util.comparator.BucketDisplayContextComparatorFactoryUtil;

import jakarta.portlet.RenderRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Lino Alves
 */
public class AssetTagsSearchFacetDisplayContextBuilder {

	public AssetTagsSearchFacetDisplayContextBuilder(
			RenderRequest renderRequest)
		throws ConfigurationException {

		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_tagFacetPortletInstanceConfiguration =
			ConfigurationProviderUtil.getPortletInstanceConfiguration(
				TagFacetPortletInstanceConfiguration.class, _themeDisplay);
	}

	public AssetTagsSearchFacetDisplayContext build() {
		AssetTagsSearchFacetDisplayContext assetTagsSearchFacetDisplayContext =
			new AssetTagsSearchFacetDisplayContext();

		assetTagsSearchFacetDisplayContext.setBucketDisplayContexts(
			_buildBucketDisplayContexts());
		assetTagsSearchFacetDisplayContext.setCloudWithCount(
			_isCloudWithCount());
		assetTagsSearchFacetDisplayContext.setDisplayStyleGroupId(
			getDisplayStyleGroupId());
		assetTagsSearchFacetDisplayContext.setFacetLabel(_getFacetLabel());
		assetTagsSearchFacetDisplayContext.setNothingSelected(
			isNothingSelected());
		assetTagsSearchFacetDisplayContext.setPaginationStartParameterName(
			_paginationStartParameterName);
		assetTagsSearchFacetDisplayContext.setParameterName(_parameterName);
		assetTagsSearchFacetDisplayContext.setParameterValue(
			getFirstParameterValue());
		assetTagsSearchFacetDisplayContext.setParameterValues(_selectedTags);
		assetTagsSearchFacetDisplayContext.setRenderNothing(isRenderNothing());
		assetTagsSearchFacetDisplayContext.
			setTagFacetPortletInstanceConfiguration(
				_tagFacetPortletInstanceConfiguration);

		return assetTagsSearchFacetDisplayContext;
	}

	public long getDisplayStyleGroupId() {
		return DisplayContextHelperUtil.getDisplayStyleGroupId(
			_tagFacetPortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode(),
			_themeDisplay);
	}

	public void setDisplayStyle(String displayStyle) {
		_displayStyle = displayStyle;
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
		parameterValue = StringUtil.trim(
			Objects.requireNonNull(parameterValue));

		if (parameterValue.isEmpty()) {
			return;
		}

		_selectedTags = Collections.singletonList(parameterValue);
	}

	public void setParameterValues(String... parameterValues) {
		_selectedTags = ListUtil.fromArray(parameterValues);
	}

	protected BucketDisplayContext buildBucketDisplayContext(
		TermCollector termCollector, int maxCount, int minCount,
		double multiplier) {

		BucketDisplayContext bucketDisplayContext = new BucketDisplayContext();

		String value = termCollector.getTerm();

		bucketDisplayContext.setBucketText(value);
		bucketDisplayContext.setFilterValue(value);

		int frequency = termCollector.getFrequency();

		bucketDisplayContext.setFrequency(frequency);

		bucketDisplayContext.setFrequencyVisible(_frequenciesVisible);
		bucketDisplayContext.setLocale(_locale);

		int popularity = (int)getPopularity(
			frequency, minCount, maxCount, multiplier);

		bucketDisplayContext.setPopularity(popularity);

		bucketDisplayContext.setSelected(isSelected(value));

		return bucketDisplayContext;
	}

	protected List<BucketDisplayContext>
		getEmptySearchResultBucketDisplayContexts() {

		if (_selectedTags.isEmpty()) {
			return Collections.emptyList();
		}

		BucketDisplayContext bucketDisplayContext = new BucketDisplayContext();

		bucketDisplayContext.setBucketText(_selectedTags.get(0));
		bucketDisplayContext.setFilterValue(_selectedTags.get(0));
		bucketDisplayContext.setFrequency(0);
		bucketDisplayContext.setFrequencyVisible(_frequenciesVisible);
		bucketDisplayContext.setPopularity(0);
		bucketDisplayContext.setSelected(true);

		return Collections.singletonList(bucketDisplayContext);
	}

	protected String getFirstParameterValue() {
		if (_selectedTags.isEmpty()) {
			return StringPool.BLANK;
		}

		return _selectedTags.get(0);
	}

	protected double getPopularity(
		int frequency, int minCount, int maxCount, double multiplier) {

		double popularity = maxCount - (maxCount - (frequency - minCount));

		return 1 + (popularity * multiplier);
	}

	protected List<TermCollector> getTermCollectors() {
		if (_facet == null) {
			return Collections.emptyList();
		}

		FacetCollector facetCollector = _facet.getFacetCollector();

		return facetCollector.getTermCollectors();
	}

	protected boolean isNothingSelected() {
		return _selectedTags.isEmpty();
	}

	protected boolean isRenderNothing() {
		List<TermCollector> termCollectors = getTermCollectors();

		if (isNothingSelected() && termCollectors.isEmpty()) {
			return true;
		}

		return false;
	}

	protected boolean isSelected(String value) {
		return _selectedTags.contains(value);
	}

	private List<BucketDisplayContext> _buildBucketDisplayContexts() {
		List<TermCollector> termCollectors = getTermCollectors();

		if (termCollectors.isEmpty()) {
			return getEmptySearchResultBucketDisplayContexts();
		}

		List<BucketDisplayContext> bucketDisplayContexts = new ArrayList<>(
			termCollectors.size());

		int maxCount = 1;
		int minCount = 1;

		if (_isCloudWithCount()) {

			// The cloud style may not list tags in the order of frequency.
			// Keep looking through the results until we reach the maximum
			// number of terms or we run out of terms

			for (int i = 0, j = 0; i < termCollectors.size(); i++, j++) {
				if ((_maxTerms > 0) && (j >= _maxTerms)) {
					break;
				}

				TermCollector termCollector = termCollectors.get(i);

				int frequency = termCollector.getFrequency();

				if ((_frequencyThreshold > 0) &&
					(_frequencyThreshold > frequency)) {

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

		for (int i = 0, j = 0; i < termCollectors.size(); i++, j++) {
			if ((_maxTerms > 0) && (j >= _maxTerms)) {
				break;
			}

			TermCollector termCollector = termCollectors.get(i);

			int frequency = termCollector.getFrequency();

			if ((_frequencyThreshold > 0) &&
				(_frequencyThreshold > frequency)) {

				j--;

				continue;
			}

			BucketDisplayContext bucketDisplayContext =
				buildBucketDisplayContext(
					termCollector, maxCount, minCount, multiplier);

			if (bucketDisplayContext != null) {
				bucketDisplayContexts.add(bucketDisplayContext);
			}
		}

		if (_order != null) {
			bucketDisplayContexts.sort(
				BucketDisplayContextComparatorFactoryUtil.
					getBucketDisplayContextComparator(_order));
		}

		return bucketDisplayContexts;
	}

	private String _getFacetLabel() {
		if (_facet != null) {
			FacetConfiguration facetConfiguration =
				_facet.getFacetConfiguration();

			if (facetConfiguration != null) {
				return facetConfiguration.getLabel();
			}

			return StringPool.BLANK;
		}

		return StringPool.BLANK;
	}

	private boolean _isCloudWithCount() {
		if (_frequenciesVisible && _displayStyle.equals("cloud")) {
			return true;
		}

		return false;
	}

	private String _displayStyle;
	private Facet _facet;
	private boolean _frequenciesVisible;
	private int _frequencyThreshold;
	private Locale _locale;
	private int _maxTerms;
	private String _order;
	private String _paginationStartParameterName;
	private String _parameterName;
	private List<String> _selectedTags = Collections.emptyList();
	private final TagFacetPortletInstanceConfiguration
		_tagFacetPortletInstanceConfiguration;
	private final ThemeDisplay _themeDisplay;

}