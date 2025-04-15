/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.custom.facet.display.context.builder;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.search.facet.config.FacetConfiguration;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.web.internal.custom.facet.configuration.CustomFacetPortletInstanceConfiguration;
import com.liferay.portal.search.web.internal.custom.facet.display.context.CustomFacetCalendarDisplayContext;
import com.liferay.portal.search.web.internal.custom.facet.display.context.CustomFacetDisplayContext;
import com.liferay.portal.search.web.internal.custom.facet.util.CustomFacetUtil;
import com.liferay.portal.search.web.internal.facet.display.context.BucketDisplayContext;
import com.liferay.portal.search.web.internal.util.DateRangeFactoryUtil;
import com.liferay.portal.search.web.internal.util.DisplayContextHelperUtil;
import com.liferay.portal.search.web.internal.util.comparator.BucketDisplayContextComparatorFactoryUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

/**
 * @author Wade Cao
 * @author Petteri Karttunen
 */
public class CustomFacetDisplayContextBuilder {

	public CustomFacetDisplayContextBuilder(
			String aggregationType, HttpServletRequest httpServletRequest)
		throws ConfigurationException {

		_aggregationType = aggregationType;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_customFacetPortletInstanceConfiguration =
			ConfigurationProviderUtil.getPortletInstanceConfiguration(
				CustomFacetPortletInstanceConfiguration.class, _themeDisplay);

		_locale = _themeDisplay.getLocale();
		_timeZone = _themeDisplay.getTimeZone();
	}

	public CustomFacetDisplayContextBuilder aggregationField(
		String aggregationField) {

		_aggregationField = aggregationField;

		return this;
	}

	public CustomFacetDisplayContext build() throws ConfigurationException {
		if (CustomFacetUtil.isRangeAggregation(_aggregationType)) {
			return _buildRangeAggregationCustomFacetDisplayContext();
		}

		return new CustomFacetDisplayContext(
			_aggregationType,
			_buildTermsAggregationBucketDisplayContexts(
				getTermsAggregationTermCollectors()),
			null, _customFacetPortletInstanceConfiguration, null,
			getDisplayCaption(), _getDisplayStyleGroupId(), _from,
			isNothingSelected(), _paginationStartParameterName, _parameterName,
			_getFirstParameterValue(), _parameterValues, isRenderNothing(),
			_showInputRange, _to);
	}

	public CustomFacetDisplayContextBuilder currentURL(String currentURL) {
		_currentURL = currentURL;

		return this;
	}

	public CustomFacetDisplayContextBuilder customDisplayCaption(
		String customDisplayCaption) {

		_customDisplayCaption = customDisplayCaption;

		return this;
	}

	public CustomFacetDisplayContextBuilder facet(Facet facet) {
		_facet = facet;

		return this;
	}

	public CustomFacetDisplayContextBuilder frequenciesVisible(
		boolean frequenciesVisible) {

		_frequenciesVisible = frequenciesVisible;

		return this;
	}

	public CustomFacetDisplayContextBuilder frequencyThreshold(
		int frequencyThreshold) {

		_frequencyThreshold = frequencyThreshold;

		return this;
	}

	public CustomFacetDisplayContextBuilder fromParameterValue(String from) {
		_from = from;

		return this;
	}

	public CustomFacetDisplayContextBuilder maxTerms(int maxTerms) {
		_maxTerms = maxTerms;

		return this;
	}

	public CustomFacetDisplayContextBuilder order(String order) {
		_order = order;

		return this;
	}

	public CustomFacetDisplayContextBuilder paginationStartParameterName(
		String paginationStartParameterName) {

		_paginationStartParameterName = paginationStartParameterName;

		return this;
	}

	public CustomFacetDisplayContextBuilder parameterName(
		String parameterName) {

		_parameterName = parameterName;

		return this;
	}

	public CustomFacetDisplayContextBuilder parameterValue(
		String parameterValue) {

		parameterValue = StringUtil.trim(
			Objects.requireNonNull(parameterValue));

		if (!parameterValue.isEmpty()) {
			_parameterValues = Collections.singletonList(parameterValue);
		}

		return this;
	}

	public CustomFacetDisplayContextBuilder parameterValues(
		String[] parameterValues) {

		if (parameterValues != null) {
			_parameterValues = ListUtil.fromArray(parameterValues);
		}

		return this;
	}

	public CustomFacetDisplayContextBuilder showInputRange(
		boolean showInputRange) {

		_showInputRange = showInputRange;

		return this;
	}

	public CustomFacetDisplayContextBuilder toParameterValue(String to) {
		_to = to;

		return this;
	}

	public CustomFacetDisplayContextBuilder totalHits(int totalHits) {
		_totalHits = totalHits;

		return this;
	}

	protected String getDisplayCaption() {
		String customDisplayCaption = StringUtil.trim(_customDisplayCaption);

		if (Validator.isNotNull(customDisplayCaption)) {
			return customDisplayCaption;
		}

		String aggregationField = StringUtil.trim(_aggregationField);

		if (Validator.isNotNull(aggregationField)) {
			return aggregationField;
		}

		return "custom";
	}

	protected int getFrequency(TermCollector termCollector) {
		if (termCollector != null) {
			return termCollector.getFrequency();
		}

		return 0;
	}

	protected TermCollector getTermCollector(String range) {
		if (_facet == null) {
			return null;
		}

		FacetCollector facetCollector = _facet.getFacetCollector();

		if (facetCollector == null) {
			return null;
		}

		return facetCollector.getTermCollector(range);
	}

	protected List<TermCollector> getTermsAggregationTermCollectors() {
		if (_facet != null) {
			FacetCollector facetCollector = _facet.getFacetCollector();

			if (facetCollector != null) {
				return facetCollector.getTermCollectors();
			}
		}

		return Collections.<TermCollector>emptyList();
	}

	protected boolean isNothingSelected() {
		if (_parameterValues.isEmpty() && Validator.isBlank(_from) &&
			Validator.isBlank(_to)) {

			return true;
		}

		return false;
	}

	protected boolean isRenderNothing() {
		if (_totalHits > 0) {
			return false;
		}

		return isNothingSelected();
	}

	protected boolean isSelected(String value) {
		return _parameterValues.contains(value);
	}

	private CustomFacetCalendarDisplayContext _buildCalendarDisplayContext() {
		if (!_aggregationType.equals("dateRange")) {
			return null;
		}

		CustomFacetCalendarDisplayContextBuilder
			customFacetCalendarDisplayContextBuilder =
				new CustomFacetCalendarDisplayContextBuilder();

		for (String parameterValue : _parameterValues) {
			if (parameterValue.startsWith(StringPool.OPEN_CURLY_BRACE)) {
				customFacetCalendarDisplayContextBuilder.rangeString(
					parameterValue);
			}
		}

		return customFacetCalendarDisplayContextBuilder.from(
			_from
		).locale(
			_locale
		).timeZone(
			_timeZone
		).to(
			_to
		).build();
	}

	private BucketDisplayContext _buildCustomRangeBucketDisplayContext() {
		boolean selected = _isCustomRangeSelected();

		BucketDisplayContext bucketDisplayContext = new BucketDisplayContext();

		bucketDisplayContext.setBucketText("custom-range");
		bucketDisplayContext.setFilterValue(_getCustomRangeURL());
		bucketDisplayContext.setFrequency(
			getFrequency(_getCustomRangeTermCollector(selected)));
		bucketDisplayContext.setFrequencyVisible(_frequenciesVisible);
		bucketDisplayContext.setLocale(_locale);
		bucketDisplayContext.setSelected(selected);

		return bucketDisplayContext;
	}

	private BucketDisplayContext _buildRangeAggregationBucketDisplayContext(
		String label, String range) {

		BucketDisplayContext bucketDisplayContext = new BucketDisplayContext();

		bucketDisplayContext.setBucketText(label);
		bucketDisplayContext.setFilterValue(_getLabeledRangeURL(label));
		bucketDisplayContext.setFrequency(
			getFrequency(getTermCollector(range)));
		bucketDisplayContext.setFrequencyVisible(_frequenciesVisible);
		bucketDisplayContext.setLocale(_locale);
		bucketDisplayContext.setSelected(_parameterValues.contains(label));

		return bucketDisplayContext;
	}

	private List<BucketDisplayContext>
		_buildRangeAggregationBucketDisplayContexts() {

		JSONArray rangesJSONArray = _getRangesJSONArray();

		if (rangesJSONArray == null) {
			return Collections.emptyList();
		}

		List<BucketDisplayContext> bucketDisplayContexts = new ArrayList<>();

		for (int i = 0; i < rangesJSONArray.length(); i++) {
			JSONObject jsonObject = rangesJSONArray.getJSONObject(i);

			String label = jsonObject.getString("label");

			if (label.equals("custom-range")) {
				continue;
			}

			String range = jsonObject.getString("range");

			if ((_frequencyThreshold > 0) &&
				(_frequencyThreshold > getFrequency(getTermCollector(range)))) {

				continue;
			}

			bucketDisplayContexts.add(
				_buildRangeAggregationBucketDisplayContext(label, range));
		}

		if (!_order.equals("rangesConfiguration")) {
			bucketDisplayContexts.sort(
				BucketDisplayContextComparatorFactoryUtil.
					getBucketDisplayContextComparator(_order));
		}

		return bucketDisplayContexts;
	}

	private CustomFacetDisplayContext
		_buildRangeAggregationCustomFacetDisplayContext() {

		return new CustomFacetDisplayContext(
			_aggregationType, _buildRangeAggregationBucketDisplayContexts(),
			_buildCalendarDisplayContext(),
			_customFacetPortletInstanceConfiguration,
			_buildCustomRangeBucketDisplayContext(), getDisplayCaption(),
			_getDisplayStyleGroupId(), _from, isNothingSelected(),
			_paginationStartParameterName, _parameterName, null, null,
			isRenderNothing(), _showInputRange, _to);
	}

	private BucketDisplayContext _buildTermsAggregationBucketDisplayContext(
		TermCollector termCollector) {

		BucketDisplayContext bucketDisplayContext = new BucketDisplayContext();

		String term = GetterUtil.getString(termCollector.getTerm());

		bucketDisplayContext.setBucketText(term);
		bucketDisplayContext.setFilterValue(_getTermsURL(term));

		bucketDisplayContext.setFrequency(termCollector.getFrequency());
		bucketDisplayContext.setFrequencyVisible(_frequenciesVisible);
		bucketDisplayContext.setLocale(_locale);
		bucketDisplayContext.setSelected(isSelected(term));

		return bucketDisplayContext;
	}

	private List<BucketDisplayContext>
		_buildTermsAggregationBucketDisplayContexts(
			List<TermCollector> termCollectors) {

		if (termCollectors.isEmpty()) {
			return _getEmptyBucketDisplayContexts();
		}

		List<BucketDisplayContext> bucketDisplayContexts = new ArrayList<>(
			termCollectors.size());

		for (int i = 0; i < termCollectors.size(); i++) {
			TermCollector termCollector = termCollectors.get(i);

			if (((_maxTerms > 0) && (i >= _maxTerms)) ||
				((_frequencyThreshold > 0) &&
				 (_frequencyThreshold > termCollector.getFrequency()))) {

				break;
			}

			bucketDisplayContexts.add(
				_buildTermsAggregationBucketDisplayContext(termCollector));
		}

		if (_order != null) {
			bucketDisplayContexts.sort(
				BucketDisplayContextComparatorFactoryUtil.
					getBucketDisplayContextComparator(_order));
		}

		return bucketDisplayContexts;
	}

	private String _getCustomDateRangeURL() {
		DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd");

		Calendar calendar = CalendarFactoryUtil.getCalendar(_timeZone);

		String to = dateFormat.format(calendar.getTime());

		calendar.add(Calendar.DATE, -1);

		String from = dateFormat.format(calendar.getTime());

		String rangeURL = HttpComponentsUtil.removeParameter(
			_currentURL, _paginationStartParameterName);

		rangeURL = HttpComponentsUtil.removeParameter(rangeURL, _parameterName);
		rangeURL = HttpComponentsUtil.setParameter(
			rangeURL, _parameterName + "From", from);

		return HttpComponentsUtil.setParameter(
			rangeURL, _parameterName + "To", to);
	}

	private TermCollector _getCustomRangeTermCollector(boolean selected) {
		if ((_facet == null) || !selected) {
			return null;
		}

		FacetCollector facetCollector = _facet.getFacetCollector();

		if (facetCollector == null) {
			return null;
		}

		if (_aggregationType.equals("dateRange")) {
			SearchContext searchContext = _facet.getSearchContext();

			return facetCollector.getTermCollector(
				DateRangeFactoryUtil.getRangeString(
					_from, _to, searchContext.getTimeZone()));
		}

		return facetCollector.getTermCollector(
			StringBundler.concat("[", _from, " TO ", _to, "]"));
	}

	private String _getCustomRangeURL() {
		if (_aggregationType.equals("dateRange")) {
			return _getCustomDateRangeURL();
		}

		String rangeURL = HttpComponentsUtil.removeParameter(
			_currentURL, _paginationStartParameterName);

		rangeURL = HttpComponentsUtil.removeParameter(rangeURL, _parameterName);
		rangeURL = HttpComponentsUtil.setParameter(
			rangeURL, _parameterName + "From", 0);

		return HttpComponentsUtil.setParameter(
			rangeURL, _parameterName + "To", 0);
	}

	private long _getDisplayStyleGroupId() {
		return DisplayContextHelperUtil.getDisplayStyleGroupId(
			_customFacetPortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode(),
			_themeDisplay);
	}

	private List<BucketDisplayContext> _getEmptyBucketDisplayContexts() {
		if (_parameterValues.isEmpty()) {
			return Collections.emptyList();
		}

		BucketDisplayContext bucketDisplayContext = new BucketDisplayContext();

		bucketDisplayContext.setBucketText(_parameterValues.get(0));
		bucketDisplayContext.setFilterValue(_parameterValues.get(0));
		bucketDisplayContext.setFrequency(0);
		bucketDisplayContext.setFrequencyVisible(_frequenciesVisible);
		bucketDisplayContext.setSelected(true);

		return Collections.singletonList(bucketDisplayContext);
	}

	private String _getFirstParameterValue() {
		if (_parameterValues.isEmpty()) {
			return StringPool.BLANK;
		}

		return _parameterValues.get(0);
	}

	private String _getLabeledRangeURL(String label) {
		String rangeURL = HttpComponentsUtil.removeParameter(
			_currentURL, _paginationStartParameterName);

		rangeURL = HttpComponentsUtil.removeParameter(
			rangeURL, _parameterName + "From");
		rangeURL = HttpComponentsUtil.removeParameter(
			rangeURL, _parameterName + "To");

		return HttpComponentsUtil.setParameter(rangeURL, _parameterName, label);
	}

	private JSONArray _getRangesJSONArray() {
		if (_facet == null) {
			return null;
		}

		FacetConfiguration facetConfiguration = _facet.getFacetConfiguration();

		if (facetConfiguration == null) {
			return null;
		}

		JSONObject dataJSONObject = facetConfiguration.getData();

		return dataJSONObject.getJSONArray("ranges");
	}

	private String _getTermsURL(String term) {
		String termsURL = HttpComponentsUtil.removeParameter(
			_currentURL, _paginationStartParameterName);

		termsURL = HttpComponentsUtil.removeParameter(termsURL, _parameterName);
		termsURL = HttpComponentsUtil.setParameter(
			termsURL, _parameterName, term);

		return termsURL;
	}

	private boolean _isCustomRangeSelected() {
		if (!Validator.isBlank(_from) || !Validator.isBlank(_to)) {
			return true;
		}

		return false;
	}

	private String _aggregationField;
	private final String _aggregationType;
	private String _currentURL;
	private String _customDisplayCaption;
	private final CustomFacetPortletInstanceConfiguration
		_customFacetPortletInstanceConfiguration;
	private Facet _facet;
	private boolean _frequenciesVisible;
	private int _frequencyThreshold;
	private String _from;
	private final Locale _locale;
	private int _maxTerms;
	private String _order;
	private String _paginationStartParameterName;
	private String _parameterName;
	private List<String> _parameterValues = Collections.emptyList();
	private boolean _showInputRange;
	private final ThemeDisplay _themeDisplay;
	private final TimeZone _timeZone;
	private String _to;
	private int _totalHits;

}