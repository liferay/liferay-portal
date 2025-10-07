/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.internal.graphql.servlet.v1_0;

import com.liferay.headless.cms.internal.graphql.mutation.v1_0.Mutation;
import com.liferay.headless.cms.internal.graphql.query.v1_0.Query;
import com.liferay.headless.cms.internal.resource.v1_0.AssetUsageResourceImpl;
import com.liferay.headless.cms.internal.resource.v1_0.BulkActionResourceImpl;
import com.liferay.headless.cms.resource.v1_0.AssetUsageResource;
import com.liferay.headless.cms.resource.v1_0.BulkActionResource;
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
 * @author Crescenzo Rega
 * @generated
 */
@Component(service = ServletData.class)
@Generated("")
public class ServletDataImpl implements ServletData {

	@Activate
	public void activate(BundleContext bundleContext) {
		Mutation.setBulkActionResourceComponentServiceObjects(
			_bulkActionResourceComponentServiceObjects);

		Query.setAssetUsageResourceComponentServiceObjects(
			_assetUsageResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "Liferay.Headless.CMS";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/headless-cms-graphql/v1_0";
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
						"mutation#createBulkAction",
						new ObjectValuePair<>(
							BulkActionResourceImpl.class, "postBulkAction"));
					put(
						"mutation#createBulkActionItemPreviewPage",
						new ObjectValuePair<>(
							BulkActionResourceImpl.class,
							"postBulkActionItemPreviewPage"));

					put(
						"query#assetUsagesAsset",
						new ObjectValuePair<>(
							AssetUsageResourceImpl.class,
							"getAssetUsagesAssetPage"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<BulkActionResource>
		_bulkActionResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<AssetUsageResource>
		_assetUsageResourceComponentServiceObjects;

}