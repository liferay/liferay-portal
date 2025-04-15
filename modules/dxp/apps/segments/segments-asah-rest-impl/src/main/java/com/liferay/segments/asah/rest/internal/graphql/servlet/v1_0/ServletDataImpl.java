/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.asah.rest.internal.graphql.servlet.v1_0;

import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.vulcan.graphql.servlet.ServletData;
import com.liferay.segments.asah.rest.internal.graphql.mutation.v1_0.Mutation;
import com.liferay.segments.asah.rest.internal.graphql.query.v1_0.Query;
import com.liferay.segments.asah.rest.internal.resource.v1_0.ExperimentResourceImpl;
import com.liferay.segments.asah.rest.internal.resource.v1_0.ExperimentRunResourceImpl;
import com.liferay.segments.asah.rest.internal.resource.v1_0.StatusResourceImpl;
import com.liferay.segments.asah.rest.resource.v1_0.ExperimentResource;
import com.liferay.segments.asah.rest.resource.v1_0.ExperimentRunResource;
import com.liferay.segments.asah.rest.resource.v1_0.StatusResource;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Generated;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;

/**
 * @author Javier Gamarra
 * @generated
 */
@Component(service = ServletData.class)
@Generated("")
public class ServletDataImpl implements ServletData {

	@Activate
	public void activate(BundleContext bundleContext) {
		Mutation.setExperimentResourceComponentServiceObjects(
			_experimentResourceComponentServiceObjects);
		Mutation.setExperimentRunResourceComponentServiceObjects(
			_experimentRunResourceComponentServiceObjects);
		Mutation.setStatusResourceComponentServiceObjects(
			_statusResourceComponentServiceObjects);

		Query.setExperimentResourceComponentServiceObjects(
			_experimentResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "Liferay.Segments.Asah.REST";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/segments-asah-graphql/v1_0";
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
						"mutation#deleteExperiment",
						new ObjectValuePair<>(
							ExperimentResourceImpl.class, "deleteExperiment"));
					put(
						"mutation#deleteExperimentBatch",
						new ObjectValuePair<>(
							ExperimentResourceImpl.class,
							"deleteExperimentBatch"));
					put(
						"mutation#createExperimentRun",
						new ObjectValuePair<>(
							ExperimentRunResourceImpl.class,
							"postExperimentRun"));
					put(
						"mutation#createExperimentStatus",
						new ObjectValuePair<>(
							StatusResourceImpl.class, "postExperimentStatus"));
					put(
						"mutation#createExperimentStatusBatch",
						new ObjectValuePair<>(
							StatusResourceImpl.class,
							"postExperimentStatusBatch"));

					put(
						"query#experiment",
						new ObjectValuePair<>(
							ExperimentResourceImpl.class, "getExperiment"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ExperimentResource>
		_experimentResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ExperimentRunResource>
		_experimentRunResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<StatusResource>
		_statusResourceComponentServiceObjects;

}