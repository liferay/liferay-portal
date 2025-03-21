/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.admin.rest.internal.graphql.servlet.v1_0;

import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.vulcan.graphql.servlet.ServletData;
import com.liferay.saml.admin.rest.internal.graphql.mutation.v1_0.Mutation;
import com.liferay.saml.admin.rest.internal.graphql.query.v1_0.Query;
import com.liferay.saml.admin.rest.internal.resource.v1_0.SamlProviderResourceImpl;
import com.liferay.saml.admin.rest.resource.v1_0.SamlProviderResource;

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
 * @author Stian Sigvartsen
 * @generated
 */
@Component(service = ServletData.class)
@Generated("")
public class ServletDataImpl implements ServletData {

	@Activate
	public void activate(BundleContext bundleContext) {
		Mutation.setSamlProviderResourceComponentServiceObjects(
			_samlProviderResourceComponentServiceObjects);

		Query.setSamlProviderResourceComponentServiceObjects(
			_samlProviderResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "Liferay.Saml.Admin.REST";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/saml-admin-graphql/v1_0";
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
						"mutation#patchSamlProvider",
						new ObjectValuePair<>(
							SamlProviderResourceImpl.class,
							"patchSamlProvider"));
					put(
						"mutation#createSamlProvider",
						new ObjectValuePair<>(
							SamlProviderResourceImpl.class,
							"postSamlProvider"));
					put(
						"mutation#createSamlProviderBatch",
						new ObjectValuePair<>(
							SamlProviderResourceImpl.class,
							"postSamlProviderBatch"));

					put(
						"query#samlProvider",
						new ObjectValuePair<>(
							SamlProviderResourceImpl.class, "getSamlProvider"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<SamlProviderResource>
		_samlProviderResourceComponentServiceObjects;

}