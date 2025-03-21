/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.rest.internal.graphql.servlet.v1_0;

import com.liferay.analytics.reports.rest.internal.graphql.mutation.v1_0.Mutation;
import com.liferay.analytics.reports.rest.internal.graphql.query.v1_0.Query;
import com.liferay.analytics.reports.rest.internal.resource.v1_0.AssetAppearsOnHistogramMetricResourceImpl;
import com.liferay.analytics.reports.rest.internal.resource.v1_0.AssetDeviceMetricResourceImpl;
import com.liferay.analytics.reports.rest.internal.resource.v1_0.AssetHistogramMetricResourceImpl;
import com.liferay.analytics.reports.rest.internal.resource.v1_0.AssetMetricResourceImpl;
import com.liferay.analytics.reports.rest.resource.v1_0.AssetAppearsOnHistogramMetricResource;
import com.liferay.analytics.reports.rest.resource.v1_0.AssetDeviceMetricResource;
import com.liferay.analytics.reports.rest.resource.v1_0.AssetHistogramMetricResource;
import com.liferay.analytics.reports.rest.resource.v1_0.AssetMetricResource;
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
 * @author Marcos Martins
 * @generated
 */
@Component(service = ServletData.class)
@Generated("")
public class ServletDataImpl implements ServletData {

	@Activate
	public void activate(BundleContext bundleContext) {
		Query.setAssetAppearsOnHistogramMetricResourceComponentServiceObjects(
			_assetAppearsOnHistogramMetricResourceComponentServiceObjects);
		Query.setAssetDeviceMetricResourceComponentServiceObjects(
			_assetDeviceMetricResourceComponentServiceObjects);
		Query.setAssetHistogramMetricResourceComponentServiceObjects(
			_assetHistogramMetricResourceComponentServiceObjects);
		Query.setAssetMetricResourceComponentServiceObjects(
			_assetMetricResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "Liferay.Analytics.Reports.REST";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/analytics-reports-rest-graphql/v1_0";
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
						"query#groupAssetMetricAssetTypeAppearsOnHistogram",
						new ObjectValuePair<>(
							AssetAppearsOnHistogramMetricResourceImpl.class,
							"getGroupAssetMetricAssetTypeAppearsOnHistogram"));
					put(
						"query#groupAssetMetricAssetTypeDevice",
						new ObjectValuePair<>(
							AssetDeviceMetricResourceImpl.class,
							"getGroupAssetMetricAssetTypeDevice"));
					put(
						"query#groupAssetMetricAssetTypeHistogram",
						new ObjectValuePair<>(
							AssetHistogramMetricResourceImpl.class,
							"getGroupAssetMetricAssetTypeHistogram"));
					put(
						"query#groupAssetMetric",
						new ObjectValuePair<>(
							AssetMetricResourceImpl.class,
							"getGroupAssetMetric"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<AssetAppearsOnHistogramMetricResource>
		_assetAppearsOnHistogramMetricResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<AssetDeviceMetricResource>
		_assetDeviceMetricResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<AssetHistogramMetricResource>
		_assetHistogramMetricResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<AssetMetricResource>
		_assetMetricResourceComponentServiceObjects;

}