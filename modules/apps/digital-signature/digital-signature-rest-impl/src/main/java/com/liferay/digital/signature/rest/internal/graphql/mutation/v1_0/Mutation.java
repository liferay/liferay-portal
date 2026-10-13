/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.rest.internal.graphql.mutation.v1_0;

import com.liferay.digital.signature.rest.dto.v1_0.DSEnvelope;
import com.liferay.digital.signature.rest.dto.v1_0.DSEnvelopeSignatureURL;
import com.liferay.digital.signature.rest.dto.v1_0.DSRecipientViewDefinition;
import com.liferay.digital.signature.rest.dto.v1_0.SignatureRequest;
import com.liferay.digital.signature.rest.resource.v1_0.DSEnvelopeResource;
import com.liferay.digital.signature.rest.resource.v1_0.DSRecipientViewDefinitionResource;
import com.liferay.digital.signature.rest.resource.v1_0.SignatureRequestResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.validation.constraints.NotEmpty;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author José Abelenda
 * @generated
 */
@Generated("")
public class Mutation {

	public static void setDSEnvelopeResourceComponentServiceObjects(
		ComponentServiceObjects<DSEnvelopeResource>
			dsEnvelopeResourceComponentServiceObjects) {

		_dsEnvelopeResourceComponentServiceObjects =
			dsEnvelopeResourceComponentServiceObjects;
	}

	public static void
		setDSRecipientViewDefinitionResourceComponentServiceObjects(
			ComponentServiceObjects<DSRecipientViewDefinitionResource>
				dsRecipientViewDefinitionResourceComponentServiceObjects) {

		_dsRecipientViewDefinitionResourceComponentServiceObjects =
			dsRecipientViewDefinitionResourceComponentServiceObjects;
	}

	public static void setSignatureRequestResourceComponentServiceObjects(
		ComponentServiceObjects<SignatureRequestResource>
			signatureRequestResourceComponentServiceObjects) {

		_signatureRequestResourceComponentServiceObjects =
			signatureRequestResourceComponentServiceObjects;
	}

	@GraphQLField
	public DSEnvelope createSiteDSEnvelope(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("dsEnvelope") DSEnvelope dsEnvelope)
		throws Exception {

		return _applyComponentServiceObjects(
			_dsEnvelopeResourceComponentServiceObjects,
			this::_populateResourceContext,
			dsEnvelopeResource -> dsEnvelopeResource.postSiteDSEnvelope(
				Long.valueOf(siteKey), dsEnvelope));
	}

	@GraphQLField
	public Response createSiteDSEnvelopeBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_dsEnvelopeResourceComponentServiceObjects,
			this::_populateResourceContext,
			dsEnvelopeResource -> dsEnvelopeResource.postSiteDSEnvelopeBatch(
				Long.valueOf(siteKey), callbackURL, object));
	}

	@GraphQLField
	public Response createSiteDSEnvelopesPageExportBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("fromDate") String fromDate,
			@GraphQLName("keywords") String keywords,
			@GraphQLName("order") String order,
			@GraphQLName("status") String status,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_dsEnvelopeResourceComponentServiceObjects,
			this::_populateResourceContext,
			dsEnvelopeResource ->
				dsEnvelopeResource.postSiteDSEnvelopesPageExportBatch(
					Long.valueOf(siteKey), fromDate, keywords, order, status,
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public DSEnvelopeSignatureURL createSiteDSRecipientViewDefinition(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("dsEnvelopeId") String dsEnvelopeId,
			@GraphQLName("dsRecipientViewDefinition") DSRecipientViewDefinition
				dsRecipientViewDefinition)
		throws Exception {

		return _applyComponentServiceObjects(
			_dsRecipientViewDefinitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			dsRecipientViewDefinitionResource ->
				dsRecipientViewDefinitionResource.
					postSiteDSRecipientViewDefinition(
						Long.valueOf(siteKey), dsEnvelopeId,
						dsRecipientViewDefinition));
	}

	@GraphQLField
	public Response createSiteDSRecipientViewDefinitionBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("dsEnvelopeId") String dsEnvelopeId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_dsRecipientViewDefinitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			dsRecipientViewDefinitionResource ->
				dsRecipientViewDefinitionResource.
					postSiteDSRecipientViewDefinitionBatch(
						Long.valueOf(siteKey), dsEnvelopeId, callbackURL,
						object));
	}

	@GraphQLField(
		description = "Sends each recipient whose turn it is a link to sign the signature request. Requires UPDATE permission on the signature request."
	)
	public SignatureRequest createSignatureRequestNotification(
			@GraphQLName("signatureRequestId") Long signatureRequestId)
		throws Exception {

		return _applyComponentServiceObjects(
			_signatureRequestResourceComponentServiceObjects,
			this::_populateResourceContext,
			signatureRequestResource ->
				signatureRequestResource.postSignatureRequestNotification(
					signatureRequestId));
	}

	@GraphQLField(
		description = "Voids the signature request with the provider and records the reason. Requires UPDATE permission on the signature request."
	)
	public SignatureRequest createSignatureRequestVoid(
			@GraphQLName("signatureRequestId") Long signatureRequestId,
			@GraphQLName("voidedReason") String voidedReason)
		throws Exception {

		return _applyComponentServiceObjects(
			_signatureRequestResourceComponentServiceObjects,
			this::_populateResourceContext,
			signatureRequestResource ->
				signatureRequestResource.postSignatureRequestVoid(
					signatureRequestId, voidedReason));
	}

	@GraphQLField(
		description = "Sends the documents to the recipients through the signature provider and records the signature request. Requires permission to add signature requests and UPDATE permission on each document."
	)
	public SignatureRequest createSiteSignatureRequest(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("signatureRequest") SignatureRequest signatureRequest)
		throws Exception {

		return _applyComponentServiceObjects(
			_signatureRequestResourceComponentServiceObjects,
			this::_populateResourceContext,
			signatureRequestResource ->
				signatureRequestResource.postSiteSignatureRequest(
					Long.valueOf(siteKey), signatureRequest));
	}

	@GraphQLField
	public Response createSiteSignatureRequestBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_signatureRequestResourceComponentServiceObjects,
			this::_populateResourceContext,
			signatureRequestResource ->
				signatureRequestResource.postSiteSignatureRequestBatch(
					Long.valueOf(siteKey), callbackURL, object));
	}

	@GraphQLField
	public Response createSiteSignatureRequestsPageExportBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("search") String search,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_signatureRequestResourceComponentServiceObjects,
			this::_populateResourceContext,
			signatureRequestResource ->
				signatureRequestResource.
					postSiteSignatureRequestsPageExportBatch(
						Long.valueOf(siteKey), search, callbackURL, contentType,
						fieldNames));
	}

	private <T, R, E1 extends Throwable, E2 extends Throwable> R
			_applyComponentServiceObjects(
				ComponentServiceObjects<T> componentServiceObjects,
				UnsafeConsumer<T, E1> unsafeConsumer,
				UnsafeFunction<T, R, E2> unsafeFunction)
		throws E1, E2 {

		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			return unsafeFunction.apply(resource);
		}
		finally {
			componentServiceObjects.ungetService(resource);
		}
	}

	private <T, E1 extends Throwable, E2 extends Throwable> void
			_applyVoidComponentServiceObjects(
				ComponentServiceObjects<T> componentServiceObjects,
				UnsafeConsumer<T, E1> unsafeConsumer,
				UnsafeConsumer<T, E2> unsafeFunction)
		throws E1, E2 {

		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			unsafeFunction.accept(resource);
		}
		finally {
			componentServiceObjects.ungetService(resource);
		}
	}

	private void _populateResourceContext(DSEnvelopeResource dsEnvelopeResource)
		throws Exception {

		dsEnvelopeResource.setContextAcceptLanguage(_acceptLanguage);
		dsEnvelopeResource.setContextCompany(_company);
		dsEnvelopeResource.setContextHttpServletRequest(_httpServletRequest);
		dsEnvelopeResource.setContextHttpServletResponse(_httpServletResponse);
		dsEnvelopeResource.setContextUriInfo(_uriInfo);
		dsEnvelopeResource.setContextUser(_user);
		dsEnvelopeResource.setGroupLocalService(_groupLocalService);
		dsEnvelopeResource.setRoleLocalService(_roleLocalService);

		dsEnvelopeResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		dsEnvelopeResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			DSRecipientViewDefinitionResource dsRecipientViewDefinitionResource)
		throws Exception {

		dsRecipientViewDefinitionResource.setContextAcceptLanguage(
			_acceptLanguage);
		dsRecipientViewDefinitionResource.setContextCompany(_company);
		dsRecipientViewDefinitionResource.setContextHttpServletRequest(
			_httpServletRequest);
		dsRecipientViewDefinitionResource.setContextHttpServletResponse(
			_httpServletResponse);
		dsRecipientViewDefinitionResource.setContextUriInfo(_uriInfo);
		dsRecipientViewDefinitionResource.setContextUser(_user);
		dsRecipientViewDefinitionResource.setGroupLocalService(
			_groupLocalService);
		dsRecipientViewDefinitionResource.setRoleLocalService(
			_roleLocalService);

		dsRecipientViewDefinitionResource.
			setVulcanBatchEngineExportTaskResource(
				_vulcanBatchEngineExportTaskResource);

		dsRecipientViewDefinitionResource.
			setVulcanBatchEngineImportTaskResource(
				_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			SignatureRequestResource signatureRequestResource)
		throws Exception {

		signatureRequestResource.setContextAcceptLanguage(_acceptLanguage);
		signatureRequestResource.setContextCompany(_company);
		signatureRequestResource.setContextHttpServletRequest(
			_httpServletRequest);
		signatureRequestResource.setContextHttpServletResponse(
			_httpServletResponse);
		signatureRequestResource.setContextUriInfo(_uriInfo);
		signatureRequestResource.setContextUser(_user);
		signatureRequestResource.setGroupLocalService(_groupLocalService);
		signatureRequestResource.setRoleLocalService(_roleLocalService);

		signatureRequestResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		signatureRequestResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private static ComponentServiceObjects<DSEnvelopeResource>
		_dsEnvelopeResourceComponentServiceObjects;
	private static ComponentServiceObjects<DSRecipientViewDefinitionResource>
		_dsRecipientViewDefinitionResourceComponentServiceObjects;
	private static ComponentServiceObjects<SignatureRequestResource>
		_signatureRequestResourceComponentServiceObjects;

	private AcceptLanguage _acceptLanguage;
	private com.liferay.portal.kernel.model.Company _company;
	private GroupLocalService _groupLocalService;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private RoleLocalService _roleLocalService;
	private BiFunction<Object, String, com.liferay.portal.kernel.search.Sort[]>
		_sortsBiFunction;
	private UriInfo _uriInfo;
	private com.liferay.portal.kernel.model.User _user;
	private VulcanBatchEngineExportTaskResource
		_vulcanBatchEngineExportTaskResource;
	private VulcanBatchEngineImportTaskResource
		_vulcanBatchEngineImportTaskResource;

}
// LIFERAY-REST-BUILDER-HASH:-1434916264