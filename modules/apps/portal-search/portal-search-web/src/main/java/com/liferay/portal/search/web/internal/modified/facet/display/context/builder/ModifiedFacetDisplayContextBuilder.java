/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.modified.facet.display.context.builder;

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
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.web.internal.facet.display.context.BucketDisplayContext;
import com.liferay.portal.search.web.internal.modified.facet.configuration.ModifiedFacetPortletInstanceConfiguration;
import com.liferay.portal.search.web.internal.modified.facet.display.context.ModifiedFacetCalendarDisplayContext;
import com.liferay.portal.search.web.internal.modified.facet.display.context.ModifiedFacetDisplayContext;
import com.liferay.portal.search.web.internal.util.DateRangeFactoryUtil;
import com.liferay.portal.search.web.internal.util.DisplayContextHelperUtil;
import com.liferay.portal.search.web.internal.util.comparator.BucketDisplayContextComparatorFactoryUtil;

import jakarta.portlet.RenderRequest;

import java.io.Serializable;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Lino Alves
 * @author Adam Brandizzi
 */
public class ModifiedFacetDisplayContextBuilder implements Serializable {

	public ModifiedFacetDisplayContextBuilder(RenderRequest renderRequest)
		throws ConfigurationException {

		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_modifiedFacetPortletInstanceConfiguration =
			ConfigurationProviderUtil.getPortletInstanceConfiguration(
				ModifiedFacetPortletInstanceConfiguration.class, _themeDisplay);
	}

	public ModifiedFacetDisplayContext build() {
		ModifiedFacetDisplayContext modifiedFacetDisplayContext =
			new ModifiedFacetDisplayContext();

		modifiedFacetDisplayContext.setBucketDisplayContexts(
			_buildBucketDisplayContexts());
		modifiedFacetDisplayContext.setCalendarDisplayContext(
			_buildCalendarDisplayContext());
		modifiedFacetDisplayContext.setCustomRangeBucketDisplayContext(
			_buildCustomRangeBucketDisplayContext());
		modifiedFacetDisplayContext.setDefaultBucketDisplayContext(
			_buildDefaultBucketDisplayContext());
		modifiedFacetDisplayContext.setDisplayStyleGroupId(
			getDisplayStyleGroupId());
		modifiedFacetDisplayContext.
			setModifiedFacetPortletInstanceConfiguration(
				_modifiedFacetPortletInstanceConfiguration);
		modifiedFacetDisplayContext.setNothingSelected(isNothingSelected());
		modifiedFacetDisplayContext.setPaginationStartParameterName(
			_paginationStartParameterName);
		modifiedFacetDisplayContext.setParameterName(_parameterName);
		modifiedFacetDisplayContext.setRenderNothing(isRenderNothing());

		return modifiedFacetDisplayContext;
	}

	public void setCurrentURL(String currentURL) {
		_currentURL = currentURL;
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

	public void setFromParameterValue(String from) {
		_from = from;
	}

	public void setLocale(Locale locale) {
		_locale = locale;
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

	public void setParameterValues(String... parameterValues) {
		_selectedRanges = ListUtil.fromArray(parameterValues);
	}

	public void setTimeZone(TimeZone timeZone) {
		_timeZone = timeZone;
	}

	public void setToParameterValue(String to) {
		_to = to;
	}

	public void setTotalHits(int totalHits) {
		_totalHits = totalHits;
	}

	protected long getDisplayStyleGroupId() {
		return DisplayContextHelperUtil.getDisplayStyleGroupId(
			_modifiedFacetPortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode(),
			_themeDisplay);
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

	protected boolean isNothingSelected() {
		if (!_selectedRanges.isEmpty() ||
			(!Validator.isBlank(_from) && !Validator.isBlank(_to))) {

			return false;
		}

		return true;
	}

	protected boolean isRenderNothing() {
		if (_totalHits > 0) {
			return false;
		}

		return isNothingSelected();
	}

	private BucketDisplayContext _buildBucketDisplayContext(
		String label, String range) {

		BucketDisplayContext bucketDisplayContext = new BucketDisplayContext();

		bucketDisplayContext.setBucketText(label);
		bucketDisplayContext.setFilterValue(_getLabeledRangeURL(label));
		bucketDisplayContext.setFrequency(
			getFrequency(getTermCollector(range)));
		bucketDisplayContext.setFrequencyVisible(_frequenciesVisible);
		bucketDisplayContext.setLocale(_locale);
		bucketDisplayContext.setSelected(_selectedRanges.contains(label));

		return bucketDisplayContext;
	}

	private List<BucketDisplayContext> _buildBucketDisplayContexts() {
		JSONArray rangesJSONArray = _getRangesJSONArray();

		if (rangesJSONArray == null) {
			return null;
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

			bucketDisplayContexts.add(_buildBucketDisplayContext(label, range));
		}

		if (!_order.equals("rangesConfiguration")) {
			bucketDisplayContexts.sort(
				BucketDisplayContextComparatorFactoryUtil.
					getBucketDisplayContextComparator(_order));
		}

		return bucketDisplayContexts;
	}

	private ModifiedFacetCalendarDisplayContext _buildCalendarDisplayContext() {
		ModifiedFacetCalendarDisplayContextBuilder
			modifiedFacetCalendarDisplayContextBuilder =
				new ModifiedFacetCalendarDisplayContextBuilder();

		for (String selectedRange : _selectedRanges) {
			if (selectedRange.startsWith(StringPool.OPEN_CURLY_BRACE)) {
				modifiedFacetCalendarDisplayContextBuilder.setRangeString(
					selectedRange);
			}
		}

		modifiedFacetCalendarDisplayContextBuilder.setFrom(_from);
		modifiedFacetCalendarDisplayContextBuilder.setLocale(_locale);
		modifiedFacetCalendarDisplayContextBuilder.setTimeZone(_timeZone);
		modifiedFacetCalendarDisplayContextBuilder.setTo(_to);

		return modifiedFacetCalendarDisplayContextBuilder.build();
	}

	private BucketDisplayContext _buildCustomRangeBucketDisplayContext() {
		boolean selected = _isCustomRangeSelected();

		BucketDisplayContext bucketDisplayContext = new BucketDisplayContext();

		bucketDisplayContext.setBucketText("custom-range");
		bucketDisplayContext.setFilterValue(_getCustomRangeURL());
		bucketDisplayContext.setFrequency(
			getFrequency(_getCustomRangeTermCollector(selected)));
		bucketDisplayContext.setFrequencyVisible(_frequenciesVisible);
		bucketDisplayContext.setSelected(selected);

		return bucketDisplayContext;
	}

	private BucketDisplayContext _buildDefaultBucketDisplayContext() {
		if (_facet == null) {
			return null;
		}

		FacetConfiguration facetConfiguration = _facet.getFacetConfiguration();

		String label = facetConfiguration.getLabel();

		BucketDisplayContext bucketDisplayContext = new BucketDisplayContext();

		bucketDisplayContext.setBucketText(label);
		bucketDisplayContext.setSelected(true);

		return bucketDisplayContext;
	}

	private TermCollector _getCustomRangeTermCollector(boolean selected) {
		if (!selected) {
			return null;
		}

		FacetCollector facetCollector = _facet.getFacetCollector();
		SearchContext searchContext = _facet.getSearchContext();

		return facetCollector.getTermCollector(
			DateRangeFactoryUtil.getRangeString(
				_from, _to, searchContext.getTimeZone()));
	}

	private String _getCustomRangeURL() {
		DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd", LocaleUtil.US);

		Calendar calendar = CalendarFactoryUtil.getCalendar(_timeZone);

		String to = dateFormat.format(calendar.getTime());

		calendar.add(Calendar.DATE, -1);

		String from = dateFormat.format(calendar.getTime());

		String rangeURL = HttpComponentsUtil.removeParameter(
			_currentURL, "modified");

		rangeURL = HttpComponentsUtil.removeParameter(
			rangeURL, _paginationStartParameterName);

		rangeURL = HttpComponentsUtil.setParameter(
			rangeURL, "modifiedFrom", from);

		return HttpComponentsUtil.setParameter(rangeURL, "modifiedTo", to);
	}

	private String _getLabeledRangeURL(String label) {
		String rangeURL = HttpComponentsUtil.removeParameter(
			_currentURL, "modifiedFrom");

		rangeURL = HttpComponentsUtil.removeParameter(rangeURL, "modifiedTo");

		rangeURL = HttpComponentsUtil.removeParameter(
			rangeURL, _paginationStartParameterName);

		return HttpComponentsUtil.setParameter(rangeURL, "modified", label);
	}

	private JSONArray _getRangesJSONArray() {
		if (_facet == null) {
			return null;
		}

		FacetConfiguration facetConfiguration = _facet.getFacetConfiguration();

		JSONObject dataJSONObject = facetConfiguration.getData();

		return dataJSONObject.getJSONArray("ranges");
	}

	private boolean _isCustomRangeSelected() {
		if (Validator.isBlank(_from) && Validator.isBlank(_to)) {
			return false;
		}

		return true;
	}

	private String _currentURL;
	private Facet _facet;
	private boolean _frequenciesVisible;
	private int _frequencyThreshold;
	private String _from;
	private Locale _locale;
	private final ModifiedFacetPortletInstanceConfiguration
		_modifiedFacetPortletInstanceConfiguration;
	private String _order;
	private String _paginationStartParameterName;
	private String _parameterName;
	private List<String> _selectedRanges = Collections.emptyList();
	private final ThemeDisplay _themeDisplay;
	private TimeZone _timeZone;
	private String _to;
	private int _totalHits;

}