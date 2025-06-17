/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.feature.flag.web.internal.display;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.feature.flag.web.internal.feature.flag.FeatureFlagsBag;
import com.liferay.feature.flag.web.internal.feature.flag.FeatureFlagsBagProvider;
import com.liferay.feature.flag.web.internal.model.FeatureFlagDisplay;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.dao.search.SearchPaginationUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlag;
import com.liferay.portal.kernel.feature.flag.FeatureFlagType;
import com.liferay.portal.kernel.feature.flag.constants.FeatureFlagConstants;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = FeatureFlagsDisplayContextFactory.class)
public class FeatureFlagsDisplayContextFactory {

	public FeatureFlagsDisplayContext create(
		FeatureFlagType featureFlagType, HttpServletRequest httpServletRequest,
		boolean systemScoped) {

		FeatureFlagsDisplayContext featureFlagsDisplayContext =
			new FeatureFlagsDisplayContext();

		Locale locale = _portal.getLocale(httpServletRequest);

		featureFlagsDisplayContext.setDescription(
			featureFlagType.getDescription(locale));

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST);

		String displayStyle = ParamUtil.getString(
			portletRequest, "displayStyle", "descriptive");

		featureFlagsDisplayContext.setDisplayStyle(displayStyle);

		PortletResponse portletResponse =
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);

		LiferayPortletRequest liferayPortletRequest =
			_portal.getLiferayPortletRequest(portletRequest);
		LiferayPortletResponse liferayPortletResponse =
			_portal.getLiferayPortletResponse(portletResponse);

		SearchContainer<FeatureFlagDisplay> searchContainer =
			new SearchContainer<>(
				portletRequest,
				PortletURLUtil.getCurrent(
					liferayPortletRequest, liferayPortletResponse),
				null, "no-feature-flags-were-found");

		searchContainer.setId("accountEntryAccountGroupsSearchContainer");
		searchContainer.setOrderByCol(
			SearchOrderByUtil.getOrderByCol(
				portletRequest, ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
				"order-by-col", "name"));
		searchContainer.setOrderByType(
			SearchOrderByUtil.getOrderByType(
				portletRequest, ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
				"order-by-type", "asc"));

		Predicate<FeatureFlag> predicate = featureFlagType.getPredicate();

		String keywords = ParamUtil.getString(portletRequest, "keywords");

		if (Validator.isNotNull(keywords)) {
			predicate = predicate.and(
				featureFlag ->
					_contains(locale, featureFlag.getKey(), keywords) ||
					_contains(locale, featureFlag.getTitle(locale), keywords) ||
					_contains(
						locale, featureFlag.getDescription(locale), keywords));
		}

		for (FeatureFlagsManagementToolbarDisplayContext.Filter filter :
				FeatureFlagsManagementToolbarDisplayContext.FILTERS) {

			predicate = predicate.and(filter.getPredicate(httpServletRequest));
		}

		Predicate<FeatureFlag> finalPredicate = predicate;

		long featureFlagCompanyId = systemScoped ? CompanyConstants.SYSTEM :
			_portal.getCompanyId(httpServletRequest);

		FeatureFlagsBag featureFlagsBag =
			_featureFlagsBagProvider.getOrCreateFeatureFlagsBag(
				featureFlagCompanyId);

		List<FeatureFlagDisplay> featureFlagDisplays = TransformUtil.transform(
			featureFlagsBag.getFeatureFlags(finalPredicate),
			featureFlag -> new FeatureFlagDisplay(
				featureFlagCompanyId,
				_getFeatureFlagDependencies(
					featureFlagCompanyId, featureFlag, featureFlagsBag),
				featureFlag, locale));

		Comparator<FeatureFlagDisplay> comparator = Comparator.comparing(
			FeatureFlagDisplay::getTitle);

		if (Objects.equals(searchContainer.getOrderByType(), "desc")) {
			comparator = comparator.reversed();
		}

		featureFlagDisplays.sort(comparator);

		int[] startAndEnd = SearchPaginationUtil.calculateStartAndEnd(
			searchContainer.getStart(), searchContainer.getEnd(),
			featureFlagDisplays.size());

		searchContainer.setResultsAndTotal(
			() -> featureFlagDisplays.subList(startAndEnd[0], startAndEnd[1]),
			featureFlagDisplays.size());

		featureFlagsDisplayContext.setManagementToolbarDisplayContext(
			new FeatureFlagsManagementToolbarDisplayContext(
				httpServletRequest, liferayPortletRequest,
				liferayPortletResponse, searchContainer));
		featureFlagsDisplayContext.setSearchContainer(searchContainer);

		if (Objects.equals(displayStyle, "descriptive")) {
			featureFlagsDisplayContext.setSearchResultCssClass("list-group");
		}

		featureFlagsDisplayContext.setTitle(featureFlagType.getTitle(locale));

		return featureFlagsDisplayContext;
	}

	private boolean _contains(Locale locale, String s1, String s2) {
		String normalized = _normalize(locale, s1);

		return normalized.contains(_normalize(locale, s2));
	}

	private List<FeatureFlag> _getFeatureFlagDependencies(
		long companyId, FeatureFlag featureFlag,
		FeatureFlagsBag featureFlagsBag) {

		List<String> dependencyKeys = new ArrayList<>(
			Arrays.asList(featureFlag.getDependencyKeys()));

		List<FeatureFlag> dependencyFeatureFlags = new ArrayList<>();

		if (companyId != CompanyConstants.SYSTEM) {
			List<String> systemScopedDependencyKeys = new ArrayList<>();

			for (String key : dependencyKeys) {
				if (GetterUtil.getBoolean(
						PropsUtil.get(
							FeatureFlagConstants.getKey(
								key,
								ExtendedObjectClassDefinition.Scope.SYSTEM.
									getValue())))) {

					systemScopedDependencyKeys.add(key);
				}
			}

			dependencyKeys.removeAll(systemScopedDependencyKeys);

			FeatureFlagsBag systemFeatureFlagsBag =
				_featureFlagsBagProvider.getOrCreateFeatureFlagsBag(
					CompanyConstants.SYSTEM);

			dependencyFeatureFlags.addAll(
				systemFeatureFlagsBag.getFeatureFlags(
					systemFeatureFlag -> ArrayUtil.contains(
						systemScopedDependencyKeys.toArray(),
						systemFeatureFlag.getKey())));
		}

		dependencyFeatureFlags.addAll(
			featureFlagsBag.getFeatureFlags(
				curFeatureFlag -> ArrayUtil.contains(
					dependencyKeys.toArray(), curFeatureFlag.getKey())));

		return dependencyFeatureFlags;
	}

	private String _normalize(Locale locale, String string) {
		return StringUtil.toLowerCase(string, locale);
	}

	@Reference
	private FeatureFlagsBagProvider _featureFlagsBagProvider;

	@Reference
	private Portal _portal;

}