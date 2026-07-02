/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.style.book.internal.graphql.servlet.v1_0;

import com.liferay.headless.admin.style.book.internal.graphql.mutation.v1_0.Mutation;
import com.liferay.headless.admin.style.book.internal.graphql.query.v1_0.Query;
import com.liferay.headless.admin.style.book.internal.resource.v1_0.StyleBookResourceImpl;
import com.liferay.headless.admin.style.book.resource.v1_0.StyleBookResource;
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
		Mutation.setStyleBookResourceComponentServiceObjects(
			_styleBookResourceComponentServiceObjects);

		Query.setStyleBookResourceComponentServiceObjects(
			_styleBookResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "Liferay.Headless.Admin.Style.Book";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/headless-admin-style-book-graphql/v1_0";
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
						"mutation#deleteAssetLibraryStyleBook",
						new ObjectValuePair<>(
							StyleBookResourceImpl.class,
							"deleteAssetLibraryStyleBook"));
					put(
						"mutation#deleteSiteStyleBook",
						new ObjectValuePair<>(
							StyleBookResourceImpl.class,
							"deleteSiteStyleBook"));
					put(
						"mutation#patchSiteStyleBook",
						new ObjectValuePair<>(
							StyleBookResourceImpl.class, "patchSiteStyleBook"));
					put(
						"mutation#createAssetLibraryStyleBooksPageExportBatch",
						new ObjectValuePair<>(
							StyleBookResourceImpl.class,
							"postAssetLibraryStyleBooksPageExportBatch"));
					put(
						"mutation#createSiteStyleBook",
						new ObjectValuePair<>(
							StyleBookResourceImpl.class, "postSiteStyleBook"));
					put(
						"mutation#createSiteStyleBookBatch",
						new ObjectValuePair<>(
							StyleBookResourceImpl.class,
							"postSiteStyleBookBatch"));
					put(
						"mutation#createSiteStyleBooksPageExportBatch",
						new ObjectValuePair<>(
							StyleBookResourceImpl.class,
							"postSiteStyleBooksPageExportBatch"));
					put(
						"mutation#updateSiteStyleBook",
						new ObjectValuePair<>(
							StyleBookResourceImpl.class, "putSiteStyleBook"));

					put(
						"query#assetLibraryStyleBook",
						new ObjectValuePair<>(
							StyleBookResourceImpl.class,
							"getAssetLibraryStyleBook"));
					put(
						"query#assetLibraryStyleBooks",
						new ObjectValuePair<>(
							StyleBookResourceImpl.class,
							"getAssetLibraryStyleBooksPage"));
					put(
						"query#styleBook",
						new ObjectValuePair<>(
							StyleBookResourceImpl.class, "getSiteStyleBook"));
					put(
						"query#styleBooks",
						new ObjectValuePair<>(
							StyleBookResourceImpl.class,
							"getSiteStyleBooksPage"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<StyleBookResource>
		_styleBookResourceComponentServiceObjects;

}
// LIFERAY-REST-BUILDER-HASH:2015410798