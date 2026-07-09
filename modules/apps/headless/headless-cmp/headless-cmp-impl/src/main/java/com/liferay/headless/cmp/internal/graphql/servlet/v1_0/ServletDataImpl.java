/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cmp.internal.graphql.servlet.v1_0;

import com.liferay.headless.cmp.internal.graphql.mutation.v1_0.Mutation;
import com.liferay.headless.cmp.internal.graphql.query.v1_0.Query;
import com.liferay.headless.cmp.internal.resource.v1_0.ContentCoverageResourceImpl;
import com.liferay.headless.cmp.internal.resource.v1_0.TaskAssigneeResourceImpl;
import com.liferay.headless.cmp.internal.resource.v1_0.TaskStatisticsResourceImpl;
import com.liferay.headless.cmp.resource.v1_0.ContentCoverageResource;
import com.liferay.headless.cmp.resource.v1_0.TaskAssigneeResource;
import com.liferay.headless.cmp.resource.v1_0.TaskStatisticsResource;
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
 * @author Carolina Barbosa
 * @generated
 */
@Component(service = ServletData.class)
@Generated("")
public class ServletDataImpl implements ServletData {

	@Activate
	public void activate(BundleContext bundleContext) {
		Query.setContentCoverageResourceComponentServiceObjects(
			_contentCoverageResourceComponentServiceObjects);
		Query.setTaskAssigneeResourceComponentServiceObjects(
			_taskAssigneeResourceComponentServiceObjects);
		Query.setTaskStatisticsResourceComponentServiceObjects(
			_taskStatisticsResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "Liferay.Headless.CMP";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/headless-cmp-graphql/v1_0";
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
						"query#projectContentCoverage",
						new ObjectValuePair<>(
							ContentCoverageResourceImpl.class,
							"getProjectContentCoverage"));
					put(
						"query#taskAssignees",
						new ObjectValuePair<>(
							TaskAssigneeResourceImpl.class,
							"getTaskAssigneesPage"));
					put(
						"query#projectTaskStatistics",
						new ObjectValuePair<>(
							TaskStatisticsResourceImpl.class,
							"getProjectTaskStatistics"));
					put(
						"query#taskStatistics",
						new ObjectValuePair<>(
							TaskStatisticsResourceImpl.class,
							"getTaskStatistics"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ContentCoverageResource>
		_contentCoverageResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<TaskAssigneeResource>
		_taskAssigneeResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<TaskStatisticsResource>
		_taskStatisticsResourceComponentServiceObjects;

}
// LIFERAY-REST-BUILDER-HASH:-544864076