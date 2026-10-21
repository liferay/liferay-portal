/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.internal.graphql.servlet.v2_0;

import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.tools.rest.builder.test.internal.graphql.mutation.v2_0.Mutation;
import com.liferay.portal.tools.rest.builder.test.internal.graphql.query.v2_0.Query;
import com.liferay.portal.tools.rest.builder.test.internal.resource.v2_0.FeatureFlagApplicationTestEntityResourceImpl;
import com.liferay.portal.tools.rest.builder.test.resource.v2_0.FeatureFlagApplicationTestEntityResource;
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
 * @author Alejandro Tardín
 * @generated
 */
@Component(service = ServletData.class)
@Generated("")
public class ServletDataImpl implements ServletData {

	@Activate
	public void activate(BundleContext bundleContext) {
		Mutation.
			setFeatureFlagApplicationTestEntityResourceComponentServiceObjects(
				_featureFlagApplicationTestEntityResourceComponentServiceObjects);

		Query.
			setFeatureFlagApplicationTestEntityResourceComponentServiceObjects(
				_featureFlagApplicationTestEntityResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "Liferay.Portal.Tools.REST.Builder.Test";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/portal-tools-rest-builder-test-graphql/v2_0";
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
						"mutation#createFeatureFlagApplicationTestEntitiesPageExportBatch",
						new ObjectValuePair<>(
							FeatureFlagApplicationTestEntityResourceImpl.class,
							"postFeatureFlagApplicationTestEntitiesPageExportBatch"));

					put(
						"query#featureFlagApplicationTestEntities",
						new ObjectValuePair<>(
							FeatureFlagApplicationTestEntityResourceImpl.class,
							"getFeatureFlagApplicationTestEntitiesPage"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<FeatureFlagApplicationTestEntityResource>
		_featureFlagApplicationTestEntityResourceComponentServiceObjects;

}
// LIFERAY-REST-BUILDER-HASH:2069163910