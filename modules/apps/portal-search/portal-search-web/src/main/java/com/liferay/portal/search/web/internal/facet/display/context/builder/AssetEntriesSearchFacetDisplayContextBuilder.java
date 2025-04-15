/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.facet.display.context.builder;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.security.permission.comparator.ModelResourceComparator;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.SortedArrayList;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.web.internal.facet.display.context.AssetEntriesSearchFacetDisplayContext;
import com.liferay.portal.search.web.internal.facet.display.context.BucketDisplayContext;
import com.liferay.portal.search.web.internal.type.facet.configuration.TypeFacetPortletInstanceConfiguration;
import com.liferay.portal.search.web.internal.util.DisplayContextHelperUtil;
import com.liferay.portal.search.web.internal.util.comparator.BucketDisplayContextComparatorFactoryUtil;

import jakarta.portlet.RenderRequest;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author Lino Alves
 */
public class AssetEntriesSearchFacetDisplayContextBuilder
	implements Serializable {

	public AssetEntriesSearchFacetDisplayContextBuilder(
			RenderRequest renderRequest)
		throws ConfigurationException {

		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_typeFacetPortletInstanceConfiguration =
			ConfigurationProviderUtil.getPortletInstanceConfiguration(
				TypeFacetPortletInstanceConfiguration.class, _themeDisplay);
	}

	public AssetEntriesSearchFacetDisplayContext build() {
		setTypeNames(getTypeNames());

		AssetEntriesSearchFacetDisplayContext
			assetEntriesSearchFacetDisplayContext =
				new AssetEntriesSearchFacetDisplayContext();

		List<BucketDisplayContext> bucketDisplayContexts =
			_buildBucketDisplayContexts();

		assetEntriesSearchFacetDisplayContext.setBucketDisplayContexts(
			bucketDisplayContexts);

		assetEntriesSearchFacetDisplayContext.setDisplayStyleGroupId(
			getDisplayStyleGroupId());
		assetEntriesSearchFacetDisplayContext.setNothingSelected(
			isNothingSelected());
		assetEntriesSearchFacetDisplayContext.setPaginationStartParameterName(
			_paginationStartParameterName);
		assetEntriesSearchFacetDisplayContext.setParameterName(_parameterName);
		assetEntriesSearchFacetDisplayContext.setParameterValue(
			getFirstParameterValue());
		assetEntriesSearchFacetDisplayContext.setParameterValues(
			_parameterValues);
		assetEntriesSearchFacetDisplayContext.setRenderNothing(
			ListUtil.isEmpty(bucketDisplayContexts));
		assetEntriesSearchFacetDisplayContext.
			setTypeFacetPortletInstanceConfiguration(
				_typeFacetPortletInstanceConfiguration);

		return assetEntriesSearchFacetDisplayContext;
	}

	public BucketDisplayContext buildBucketDisplayContext(
		String typeName, boolean selected, String assetType, int frequency) {

		BucketDisplayContext bucketDisplayContext = new BucketDisplayContext();

		bucketDisplayContext.setBucketText(typeName);
		bucketDisplayContext.setFilterValue(assetType);
		bucketDisplayContext.setFrequency(frequency);
		bucketDisplayContext.setFrequencyVisible(_frequenciesVisible);
		bucketDisplayContext.setLocale(_locale);
		bucketDisplayContext.setSelected(selected);

		return bucketDisplayContext;
	}

	public int getPopularity(
		int frequency, int maxCount, int minCount, double multiplier) {

		int popularity = maxCount - (maxCount - (frequency - minCount));

		return (int)(1 + (popularity * multiplier));
	}

	public boolean isNothingSelected() {
		return _parameterValues.isEmpty();
	}

	public void setClassNames(String[] classNames) {
		_classNames = classNames;
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

		_parameterValues = Collections.singletonList(parameterValue);
	}

	public void setParameterValues(String... paramValues) {
		_parameterValues = ListUtil.fromArray(paramValues);
	}

	public void setTypeNames(Map<String, String> typeNames) {
		_typeNames = typeNames;
	}

	protected long getDisplayStyleGroupId() {
		return DisplayContextHelperUtil.getDisplayStyleGroupId(
			_typeFacetPortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode(),
			_themeDisplay);
	}

	protected String getFirstParameterValue() {
		if (_parameterValues.isEmpty()) {
			return StringPool.BLANK;
		}

		return _parameterValues.get(0);
	}

	protected Map<String, String> getTypeNames() {
		Map<String, String> assetTypesTypeNames = new HashMap<>();

		if (_classNames == null) {
			return assetTypesTypeNames;
		}

		for (String className : _classNames) {
			String typeName = className;

			AssetRendererFactory<?> assetRendererFactory =
				AssetRendererFactoryRegistryUtil.
					getAssetRendererFactoryByClassName(className);

			if (assetRendererFactory != null) {
				typeName = assetRendererFactory.getTypeName(
					_themeDisplay.getLocale());
			}
			else if (className.startsWith(
						ObjectDefinitionConstants.
							CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION)) {

				ObjectDefinition objectDefinition =
					ObjectDefinitionLocalServiceUtil.
						fetchObjectDefinitionByClassName(
							_themeDisplay.getCompanyId(), className);

				if (objectDefinition == null) {
					continue;
				}

				typeName = objectDefinition.getLabel(_themeDisplay.getLocale());
			}

			assetTypesTypeNames.put(className, typeName);
		}

		return assetTypesTypeNames;
	}

	private List<BucketDisplayContext> _buildBucketDisplayContexts() {
		if (_facet == null) {
			return Collections.emptyList();
		}

		FacetCollector facetCollector = _facet.getFacetCollector();

		if (facetCollector == null) {
			return Collections.emptyList();
		}

		List<BucketDisplayContext> bucketDisplayContexts = new ArrayList<>();

		List<String> assetTypes = new SortedArrayList<>(
			new ModelResourceComparator(_locale));

		for (String className : _classNames) {
			if (assetTypes.contains(className)) {
				continue;
			}

			assetTypes.add(className);
		}

		for (String assetType : assetTypes) {
			TermCollector termCollector = facetCollector.getTermCollector(
				assetType);

			int frequency = 0;

			if (termCollector != null) {
				frequency = termCollector.getFrequency();
			}

			if (_frequencyThreshold > frequency) {
				continue;
			}

			boolean selected = false;

			if (termCollector != null) {
				selected = _parameterValues.contains(termCollector.getTerm());
			}

			String typeName = _typeNames.get(assetType);

			if (Validator.isBlank(typeName)) {
				typeName = assetType;
			}

			BucketDisplayContext bucketDisplayContext =
				buildBucketDisplayContext(
					typeName, selected, assetType, frequency);

			bucketDisplayContexts.add(bucketDisplayContext);
		}

		if (_order != null) {
			bucketDisplayContexts.sort(
				BucketDisplayContextComparatorFactoryUtil.
					getBucketDisplayContextComparator(_order));
		}

		return bucketDisplayContexts;
	}

	private String[] _classNames;
	private Facet _facet;
	private boolean _frequenciesVisible;
	private int _frequencyThreshold;
	private Locale _locale;
	private String _order;
	private String _paginationStartParameterName;
	private String _parameterName;
	private List<String> _parameterValues = Collections.emptyList();
	private final ThemeDisplay _themeDisplay;
	private final TypeFacetPortletInstanceConfiguration
		_typeFacetPortletInstanceConfiguration;
	private Map<String, String> _typeNames = Collections.emptyMap();

}