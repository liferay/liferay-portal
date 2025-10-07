/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.rest.internal.graphql.servlet.v1_0;

import com.liferay.digital.signature.rest.internal.graphql.mutation.v1_0.Mutation;
import com.liferay.digital.signature.rest.internal.graphql.query.v1_0.Query;
import com.liferay.digital.signature.rest.internal.resource.v1_0.DSEnvelopeResourceImpl;
import com.liferay.digital.signature.rest.internal.resource.v1_0.DSRecipientViewDefinitionResourceImpl;
import com.liferay.digital.signature.rest.resource.v1_0.DSEnvelopeResource;
import com.liferay.digital.signature.rest.resource.v1_0.DSRecipientViewDefinitionResource;
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
 * @author José Abelenda
 * @generated
 */
@Component(service = ServletData.class)
@Generated("")
public class ServletDataImpl implements ServletData {

	@Activate
	public void activate(BundleContext bundleContext) {
		Mutation.setDSEnvelopeResourceComponentServiceObjects(
			_dsEnvelopeResourceComponentServiceObjects);
		Mutation.setDSRecipientViewDefinitionResourceComponentServiceObjects(
			_dsRecipientViewDefinitionResourceComponentServiceObjects);

		Query.setDSEnvelopeResourceComponentServiceObjects(
			_dsEnvelopeResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "Liferay.Digital.Signature.REST";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/digital-signature-rest-graphql/v1_0";
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
						"mutation#createSiteDSEnvelope",
						new ObjectValuePair<>(
							DSEnvelopeResourceImpl.class,
							"postSiteDSEnvelope"));
					put(
						"mutation#createSiteDSEnvelopeBatch",
						new ObjectValuePair<>(
							DSEnvelopeResourceImpl.class,
							"postSiteDSEnvelopeBatch"));
					put(
						"mutation#createSiteDSEnvelopesPageExportBatch",
						new ObjectValuePair<>(
							DSEnvelopeResourceImpl.class,
							"postSiteDSEnvelopesPageExportBatch"));
					put(
						"mutation#createSiteDSRecipientViewDefinition",
						new ObjectValuePair<>(
							DSRecipientViewDefinitionResourceImpl.class,
							"postSiteDSRecipientViewDefinition"));
					put(
						"mutation#createSiteDSRecipientViewDefinitionBatch",
						new ObjectValuePair<>(
							DSRecipientViewDefinitionResourceImpl.class,
							"postSiteDSRecipientViewDefinitionBatch"));

					put(
						"query#dSEnvelope",
						new ObjectValuePair<>(
							DSEnvelopeResourceImpl.class, "getSiteDSEnvelope"));
					put(
						"query#dSEnvelopes",
						new ObjectValuePair<>(
							DSEnvelopeResourceImpl.class,
							"getSiteDSEnvelopesPage"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<DSEnvelopeResource>
		_dsEnvelopeResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<DSRecipientViewDefinitionResource>
		_dsRecipientViewDefinitionResourceComponentServiceObjects;

}