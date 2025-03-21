/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.object.internal.graphql.servlet.v1_0;

import com.liferay.headless.object.internal.graphql.mutation.v1_0.Mutation;
import com.liferay.headless.object.internal.graphql.query.v1_0.Query;
import com.liferay.headless.object.internal.resource.v1_0.ObjectEntryFolderResourceImpl;
import com.liferay.headless.object.resource.v1_0.ObjectEntryFolderResource;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.vulcan.graphql.servlet.ServletData;

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
 * @author Alicia García
 * @generated
 */
@Component(service = ServletData.class)
@Generated("")
public class ServletDataImpl implements ServletData {

	@Activate
	public void activate(BundleContext bundleContext) {
		Mutation.setObjectEntryFolderResourceComponentServiceObjects(
			_objectEntryFolderResourceComponentServiceObjects);

		Query.setObjectEntryFolderResourceComponentServiceObjects(
			_objectEntryFolderResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "Liferay.Headless.Object";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/headless-object-graphql/v1_0";
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
						"mutation#deleteObjectEntryFolder",
						new ObjectValuePair<>(
							ObjectEntryFolderResourceImpl.class,
							"deleteObjectEntryFolder"));
					put(
						"mutation#deleteObjectEntryFolderBatch",
						new ObjectValuePair<>(
							ObjectEntryFolderResourceImpl.class,
							"deleteObjectEntryFolderBatch"));
					put(
						"mutation#patchObjectEntryFolder",
						new ObjectValuePair<>(
							ObjectEntryFolderResourceImpl.class,
							"patchObjectEntryFolder"));
					put(
						"mutation#deleteScopeScopeKeyObjectEntryFolderByExternalReferenceCode",
						new ObjectValuePair<>(
							ObjectEntryFolderResourceImpl.class,
							"deleteScopeScopeKeyObjectEntryFolderByExternalReferenceCode"));
					put(
						"mutation#patchScopeScopeKeyObjectEntryFolderByExternalReferenceCode",
						new ObjectValuePair<>(
							ObjectEntryFolderResourceImpl.class,
							"patchScopeScopeKeyObjectEntryFolderByExternalReferenceCode"));
					put(
						"mutation#updateScopeScopeKeyObjectEntryFolderByExternalReferenceCode",
						new ObjectValuePair<>(
							ObjectEntryFolderResourceImpl.class,
							"putScopeScopeKeyObjectEntryFolderByExternalReferenceCode"));
					put(
						"mutation#createScopeScopeKeyObjectEntryFolder",
						new ObjectValuePair<>(
							ObjectEntryFolderResourceImpl.class,
							"postScopeScopeKeyObjectEntryFolder"));

					put(
						"query#objectEntryFolder",
						new ObjectValuePair<>(
							ObjectEntryFolderResourceImpl.class,
							"getObjectEntryFolder"));
					put(
						"query#scopeScopeKeyObjectEntryFolderByExternalReferenceCode",
						new ObjectValuePair<>(
							ObjectEntryFolderResourceImpl.class,
							"getScopeScopeKeyObjectEntryFolderByExternalReferenceCode"));
					put(
						"query#scopeScopeKeyObjectEntryFolders",
						new ObjectValuePair<>(
							ObjectEntryFolderResourceImpl.class,
							"getScopeScopeKeyObjectEntryFoldersPage"));

					put(
						"query#ObjectEntryFolder.parentObjectEntryFolder",
						new ObjectValuePair<>(
							ObjectEntryFolderResourceImpl.class,
							"getObjectEntryFolder"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ObjectEntryFolderResource>
		_objectEntryFolderResourceComponentServiceObjects;

}