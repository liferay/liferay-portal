/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.language.override.internal.graphql.servlet.v1_0;

import com.liferay.headless.admin.language.override.internal.graphql.mutation.v1_0.Mutation;
import com.liferay.headless.admin.language.override.internal.graphql.query.v1_0.Query;
import com.liferay.headless.admin.language.override.internal.resource.v1_0.LanguageOverrideResourceImpl;
import com.liferay.headless.admin.language.override.resource.v1_0.LanguageOverrideResource;
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
 * @author Thiago Buarque
 * @generated
 */
@Component(service = ServletData.class)
@Generated("")
public class ServletDataImpl implements ServletData {

	@Activate
	public void activate(BundleContext bundleContext) {
		Mutation.setLanguageOverrideResourceComponentServiceObjects(
			_languageOverrideResourceComponentServiceObjects);

		Query.setLanguageOverrideResourceComponentServiceObjects(
			_languageOverrideResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "Liferay.Headless.Admin.Language.Override";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/headless-admin-language-override-graphql/v1_0";
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
						"mutation#deleteLanguageOverrideByExternalReferenceCode",
						new ObjectValuePair<>(
							LanguageOverrideResourceImpl.class,
							"deleteLanguageOverrideByExternalReferenceCode"));
					put(
						"mutation#createLanguageOverride",
						new ObjectValuePair<>(
							LanguageOverrideResourceImpl.class,
							"postLanguageOverride"));
					put(
						"mutation#createLanguageOverrideBatch",
						new ObjectValuePair<>(
							LanguageOverrideResourceImpl.class,
							"postLanguageOverrideBatch"));
					put(
						"mutation#createLanguageOverridesPageExportBatch",
						new ObjectValuePair<>(
							LanguageOverrideResourceImpl.class,
							"postLanguageOverridesPageExportBatch"));
					put(
						"mutation#updateLanguageOverrideByExternalReferenceCode",
						new ObjectValuePair<>(
							LanguageOverrideResourceImpl.class,
							"putLanguageOverrideByExternalReferenceCode"));

					put(
						"query#languageOverrideByExternalReferenceCode",
						new ObjectValuePair<>(
							LanguageOverrideResourceImpl.class,
							"getLanguageOverrideByExternalReferenceCode"));
					put(
						"query#languageOverrides",
						new ObjectValuePair<>(
							LanguageOverrideResourceImpl.class,
							"getLanguageOverridesPage"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<LanguageOverrideResource>
		_languageOverrideResourceComponentServiceObjects;

}
// LIFERAY-REST-BUILDER-HASH:-839029297