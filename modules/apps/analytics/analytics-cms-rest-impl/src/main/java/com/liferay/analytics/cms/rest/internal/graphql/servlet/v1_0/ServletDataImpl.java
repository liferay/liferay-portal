/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.internal.graphql.servlet.v1_0;

import com.liferay.analytics.cms.rest.internal.graphql.mutation.v1_0.Mutation;
import com.liferay.analytics.cms.rest.internal.graphql.query.v1_0.Query;
import com.liferay.analytics.cms.rest.internal.resource.v1_0.InventoryAnalysisResourceImpl;
import com.liferay.analytics.cms.rest.internal.resource.v1_0.OverviewResourceImpl;
import com.liferay.analytics.cms.rest.resource.v1_0.InventoryAnalysisResource;
import com.liferay.analytics.cms.rest.resource.v1_0.OverviewResource;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.vulcan.graphql.servlet.ServletData;

import jakarta.annotation.Generated;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;

/**
 * @author Rachael Koestartyo
 * @generated
 */
@Component(service = ServletData.class)
@Generated("")
public class ServletDataImpl implements ServletData {

	@Activate
	public void activate(BundleContext bundleContext) {
		Query.setInventoryAnalysisResourceComponentServiceObjects(
			_inventoryAnalysisResourceComponentServiceObjects);
		Query.setOverviewResourceComponentServiceObjects(
			_overviewResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "Liferay.Analytics.CMS.REST";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/analytics-cms-rest-graphql/v1_0";
	}

	@Override
	public Query getQuery() {
		return new Query();
	}

	public ObjectValuePair<Class<?>, String> getResourceMethodObjectValuePair(
		String methodName, boolean mutation) {

		if (mutation) {
			return _resourceMethodObjectValuePairs.get(
				"mutation#" + methodName);
		}

		return _resourceMethodObjectValuePairs.get("query#" + methodName);
	}

	private static final Map<String, ObjectValuePair<Class<?>, String>>
		_resourceMethodObjectValuePairs =
			new HashMap<String, ObjectValuePair<Class<?>, String>>() {
				{
					put(
						"query#inventoryAnalysis",
						new ObjectValuePair<>(
							InventoryAnalysisResourceImpl.class,
							"getInventoryAnalysis"));
					put(
						"query#contentOverview",
						new ObjectValuePair<>(
							OverviewResourceImpl.class, "getContentOverview"));
					put(
						"query#fileOverview",
						new ObjectValuePair<>(
							OverviewResourceImpl.class, "getFileOverview"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<InventoryAnalysisResource>
		_inventoryAnalysisResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<OverviewResource>
		_overviewResourceComponentServiceObjects;

}