/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.content.internal.graphql.servlet.v1_0;

import com.liferay.headless.admin.content.internal.graphql.mutation.v1_0.Mutation;
import com.liferay.headless.admin.content.internal.graphql.query.v1_0.Query;
import com.liferay.headless.admin.content.internal.resource.v1_0.DisplayPageTemplateResourceImpl;
import com.liferay.headless.admin.content.internal.resource.v1_0.PageDefinitionResourceImpl;
import com.liferay.headless.admin.content.internal.resource.v1_0.StructuredContentResourceImpl;
import com.liferay.headless.admin.content.resource.v1_0.DisplayPageTemplateResource;
import com.liferay.headless.admin.content.resource.v1_0.PageDefinitionResource;
import com.liferay.headless.admin.content.resource.v1_0.StructuredContentResource;
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
 * @author Javier Gamarra
 * @generated
 */
@Component(service = ServletData.class)
@Generated("")
public class ServletDataImpl implements ServletData {

	@Activate
	public void activate(BundleContext bundleContext) {
		Mutation.setPageDefinitionResourceComponentServiceObjects(
			_pageDefinitionResourceComponentServiceObjects);
		Mutation.setStructuredContentResourceComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects);

		Query.setDisplayPageTemplateResourceComponentServiceObjects(
			_displayPageTemplateResourceComponentServiceObjects);
		Query.setStructuredContentResourceComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "Liferay.Headless.Admin.Content";
	}

	@Override
	public String getGraphQLNamespace() {
		return "admin";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/headless-admin-content-graphql/v1_0";
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
						"mutation#createSitePageDefinitionPreview",
						new ObjectValuePair<>(
							PageDefinitionResourceImpl.class,
							"postSitePageDefinitionPreview"));
					put(
						"mutation#createSiteStructuredContentDraft",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"postSiteStructuredContentDraft"));
					put(
						"mutation#deleteStructuredContentByVersion",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"deleteStructuredContentByVersion"));

					put(
						"query#displayPageTemplates",
						new ObjectValuePair<>(
							DisplayPageTemplateResourceImpl.class,
							"getSiteDisplayPageTemplatesPage"));
					put(
						"query#displayPageTemplate",
						new ObjectValuePair<>(
							DisplayPageTemplateResourceImpl.class,
							"getSiteDisplayPageTemplate"));
					put(
						"query#structuredContents",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"getSiteStructuredContentsPage"));
					put(
						"query#structuredContentByVersion",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"getStructuredContentByVersion"));
					put(
						"query#structuredContentsVersions",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"getStructuredContentsVersionsPage"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<PageDefinitionResource>
		_pageDefinitionResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<StructuredContentResource>
		_structuredContentResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<DisplayPageTemplateResource>
		_displayPageTemplateResourceComponentServiceObjects;

}