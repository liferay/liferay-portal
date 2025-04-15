/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.facet.display.context.builder;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.web.internal.facet.display.context.BucketDisplayContext;
import com.liferay.portal.search.web.internal.facet.display.context.FolderSearchFacetDisplayContext;
import com.liferay.portal.search.web.internal.facet.display.context.FolderTitleLookup;
import com.liferay.portal.search.web.internal.folder.facet.configuration.FolderFacetPortletInstanceConfiguration;
import com.liferay.portal.search.web.internal.util.DisplayContextHelperUtil;
import com.liferay.portal.search.web.internal.util.comparator.BucketDisplayContextComparatorFactoryUtil;

import jakarta.portlet.RenderRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * @author Lino Alves
 */
public class FolderSearchFacetDisplayContextBuilder {

	public FolderSearchFacetDisplayContextBuilder(RenderRequest renderRequest)
		throws ConfigurationException {

		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_folderFacetPortletInstanceConfiguration =
			ConfigurationProviderUtil.getPortletInstanceConfiguration(
				FolderFacetPortletInstanceConfiguration.class, _themeDisplay);
	}

	public FolderSearchFacetDisplayContext build() {
		FolderSearchFacetDisplayContext folderSearchFacetDisplayContext =
			new FolderSearchFacetDisplayContext();

		List<BucketDisplayContext> bucketDisplayContexts =
			_buildBucketDisplayContexts();

		folderSearchFacetDisplayContext.setBucketDisplayContexts(
			bucketDisplayContexts);

		folderSearchFacetDisplayContext.setDisplayStyleGroupId(
			getDisplayStyleGroupId());
		folderSearchFacetDisplayContext.
			setFolderFacetPortletInstanceConfiguration(
				_folderFacetPortletInstanceConfiguration);
		folderSearchFacetDisplayContext.setNothingSelected(isNothingSelected());
		folderSearchFacetDisplayContext.setPaginationStartParameterName(
			_paginationStartParameterName);
		folderSearchFacetDisplayContext.setParameterName(_parameterName);
		folderSearchFacetDisplayContext.setParameterValue(
			getFirstParameterValueString());
		folderSearchFacetDisplayContext.setParameterValues(
			getParameterValueStrings());
		folderSearchFacetDisplayContext.setRenderNothing(
			isRenderNothing(bucketDisplayContexts, getTermCollectors()));

		return folderSearchFacetDisplayContext;
	}

	public void setFacet(Facet facet) {
		_facet = facet;
	}

	public void setFolderTitleLookup(FolderTitleLookup folderTitleLookup) {
		_folderTitleLookup = folderTitleLookup;
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
		_selectedFolderIds = TransformUtil.transformToList(
			parameterValues,
			value -> {
				long folderId = GetterUtil.getLong(value);

				if (folderId <= 0) {
					return null;
				}

				return folderId;
			});
	}

	protected long getDisplayStyleGroupId() {
		return DisplayContextHelperUtil.getDisplayStyleGroupId(
			_folderFacetPortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode(),
			_themeDisplay);
	}

	protected String getFirstParameterValueString() {
		if (_selectedFolderIds.isEmpty()) {
			return StringPool.BLANK;
		}

		return String.valueOf(_selectedFolderIds.get(0));
	}

	protected List<String> getParameterValueStrings() {
		return TransformUtil.transform(_selectedFolderIds, String::valueOf);
	}

	protected List<TermCollector> getTermCollectors() {
		if (_facet == null) {
			return Collections.emptyList();
		}

		FacetCollector facetCollector = _facet.getFacetCollector();

		if (facetCollector == null) {
			return Collections.emptyList();
		}

		return facetCollector.getTermCollectors();
	}

	protected boolean isNothingSelected() {
		return _selectedFolderIds.isEmpty();
	}

	protected boolean isRenderNothing(
		List<BucketDisplayContext> bucketDisplayContexts,
		List<TermCollector> termCollectors) {

		if ((isNothingSelected() && ListUtil.isEmpty(termCollectors)) ||
			ListUtil.isEmpty(bucketDisplayContexts)) {

			return true;
		}

		return false;
	}

	protected boolean isSelected(long folderId) {
		return _selectedFolderIds.contains(folderId);
	}

	private BucketDisplayContext _buildBucketDisplayContext(
		long folderId, String displayName, int frequency, boolean selected) {

		BucketDisplayContext bucketDisplayContext = new BucketDisplayContext();

		bucketDisplayContext.setBucketText(displayName);
		bucketDisplayContext.setFilterValue(String.valueOf(folderId));
		bucketDisplayContext.setFrequency(frequency);
		bucketDisplayContext.setFrequencyVisible(_frequenciesVisible);
		bucketDisplayContext.setLocale(_locale);
		bucketDisplayContext.setSelected(selected);

		return bucketDisplayContext;
	}

	private BucketDisplayContext _buildBucketDisplayContext(
		TermCollector termCollector) {

		long folderId = GetterUtil.getLong(termCollector.getTerm());

		String displayName = _getDisplayName(folderId);

		if ((folderId == 0) || (displayName == null)) {
			return null;
		}

		return _buildBucketDisplayContext(
			folderId, displayName, termCollector.getFrequency(),
			isSelected(folderId));
	}

	private List<BucketDisplayContext> _buildBucketDisplayContexts() {
		List<TermCollector> termCollectors = getTermCollectors();

		if (termCollectors.isEmpty()) {
			return _getEmptyBucketDisplayContexts();
		}

		List<BucketDisplayContext> bucketDisplayContexts = new ArrayList<>(
			termCollectors.size());

		for (int i = 0; i < termCollectors.size(); i++) {
			if ((_maxTerms > 0) && (i >= _maxTerms)) {
				break;
			}

			TermCollector termCollector = termCollectors.get(i);

			if ((_frequencyThreshold > 0) &&
				(_frequencyThreshold > termCollector.getFrequency())) {

				break;
			}

			BucketDisplayContext bucketDisplayContext =
				_buildBucketDisplayContext(termCollector);

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

	private String _getDisplayName(long folderId) {
		String title = _folderTitleLookup.getFolderTitle(folderId);

		if (Validator.isNotNull(title)) {
			return title;
		}

		return null;
	}

	private BucketDisplayContext _getEmptyBucketDisplayContext(long folderId) {
		return _buildBucketDisplayContext(
			folderId, _getDisplayName(folderId), 0, true);
	}

	private List<BucketDisplayContext> _getEmptyBucketDisplayContexts() {
		return TransformUtil.transform(
			_selectedFolderIds, this::_getEmptyBucketDisplayContext);
	}

	private Facet _facet;
	private final FolderFacetPortletInstanceConfiguration
		_folderFacetPortletInstanceConfiguration;
	private FolderTitleLookup _folderTitleLookup;
	private boolean _frequenciesVisible;
	private int _frequencyThreshold;
	private Locale _locale;
	private int _maxTerms;
	private String _order;
	private String _paginationStartParameterName;
	private String _parameterName;
	private List<Long> _selectedFolderIds = Collections.emptyList();
	private final ThemeDisplay _themeDisplay;

}