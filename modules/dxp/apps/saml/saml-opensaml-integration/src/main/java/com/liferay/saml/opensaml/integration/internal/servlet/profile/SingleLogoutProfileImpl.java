/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.servlet.profile;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.saml.constants.SamlWebKeys;
import com.liferay.saml.helper.SamlHttpRequestHelper;
import com.liferay.saml.opensaml.integration.internal.binding.SamlBinding;
import com.liferay.saml.opensaml.integration.internal.transport.HttpClientFactory;
import com.liferay.saml.opensaml.integration.internal.util.OpenSamlUtil;
import com.liferay.saml.opensaml.integration.internal.util.SamlUtil;
import com.liferay.saml.persistence.model.SamlIdpSpSession;
import com.liferay.saml.persistence.model.SamlIdpSsoSession;
import com.liferay.saml.persistence.model.SamlPeerBinding;
import com.liferay.saml.persistence.model.SamlSpSession;
import com.liferay.saml.persistence.service.SamlIdpSpSessionLocalService;
import com.liferay.saml.persistence.service.SamlIdpSsoSessionLocalService;
import com.liferay.saml.persistence.service.SamlPeerBindingLocalService;
import com.liferay.saml.runtime.SamlException;
import com.liferay.saml.runtime.exception.UnsolicitedLogoutResponseException;
import com.liferay.saml.runtime.exception.UnsupportedBindingException;
import com.liferay.saml.runtime.servlet.profile.SingleLogoutProfile;
import com.liferay.saml.util.JspUtil;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.Writer;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.messaging.context.InOutOperationContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.decoder.servlet.HttpServletRequestMessageDecoder;
import org.opensaml.messaging.encoder.servlet.HttpServletResponseMessageEncoder;
import org.opensaml.messaging.pipeline.httpclient.BasicHttpClientMessagePipeline;
import org.opensaml.messaging.pipeline.httpclient.HttpClientMessagePipeline;
import org.opensaml.messaging.pipeline.httpclient.HttpClientMessagePipelineFactory;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.messaging.context.SAMLEndpointContext;
import org.opensaml.saml.common.messaging.context.SAMLMetadataContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLProtocolContext;
import org.opensaml.saml.common.messaging.context.SAMLSelfEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLSubjectNameIdentifierContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.opensaml.saml.saml2.core.LogoutResponse;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.SessionIndex;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SSODescriptor;
import org.opensaml.saml.saml2.metadata.SingleLogoutService;
import org.opensaml.security.credential.Credential;
import org.opensaml.soap.client.http.PipelineFactoryHttpSOAPClient;
import org.opensaml.xmlsec.context.SecurityParametersContext;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mika Koivisto
 */
@Component(service = SingleLogoutProfile.class)
public class SingleLogoutProfileImpl
	extends BaseProfile implements SingleLogoutProfile {

	@Override
	public boolean isSingleLogoutSupported(
		HttpServletRequest httpServletRequest) {

		try {
			SamlSpSession samlSpSession = getSamlSpSession(httpServletRequest);

			if ((samlSpSession == null) || samlSpSession.isTerminated()) {
				return false;
			}

			User user = _userLocalService.getUser(samlSpSession.getUserId());

			if (!user.isSetupComplete()) {
				return false;
			}

			MetadataResolver metadataResolver = getMetadataResolver();

			SamlPeerBinding samlPeerBinding =
				_samlPeerBindingLocalService.getSamlPeerBinding(
					samlSpSession.getSamlPeerBindingId());

			EntityDescriptor entityDescriptor = metadataResolver.resolveSingle(
				new CriteriaSet(
					new EntityIdCriterion(
						samlPeerBinding.getSamlPeerEntityId())));

			SingleLogoutService singleLogoutService =
				SamlUtil.resolveSingleLogoutService(
					entityDescriptor.getIDPSSODescriptor(
						SAMLConstants.SAML20P_NS),
					SAMLConstants.SAML2_REDIRECT_BINDING_URI);

			if (singleLogoutService != null) {
				String binding = singleLogoutService.getBinding();

				if (!binding.equals(SAMLConstants.SAML2_SOAP11_BINDING_URI)) {
					return true;
				}
			}
		}
		catch (Exception exception) {
			String message =
				"Unable to verify single logout support: " +
					exception.getMessage();

			if (_log.isDebugEnabled()) {
				_log.debug(message, exception);
			}
			else if (_log.isWarnEnabled()) {
				_log.warn(message);
			}
		}

		return false;
	}

	@Override
	public void processIdpLogout(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws PortalException {

		String requestPath = _samlHttpRequestHelper.getRequestPath(
			httpServletRequest);

		try {
			httpServletResponse.addHeader(
				HttpHeaders.CACHE_CONTROL,
				HttpHeaders.CACHE_CONTROL_NO_CACHE_VALUE);
			httpServletResponse.addHeader(
				HttpHeaders.PRAGMA, HttpHeaders.PRAGMA_NO_CACHE_VALUE);

			if (requestPath.equals("/c/portal/logout")) {
				_initiateIdpSingleLogout(
					httpServletRequest, httpServletResponse);
			}
			else if (requestPath.equals("/c/portal/saml/slo_logout")) {
				SamlSloContext samlSloContext = _getSamlSloContext(
					httpServletRequest, null);

				if (samlSloContext == null) {
					_redirectToLogout(httpServletRequest, httpServletResponse);

					return;
				}

				String cmd = ParamUtil.getString(
					httpServletRequest, Constants.CMD);

				if (Validator.isNull(cmd)) {
					httpServletRequest.setAttribute(
						SamlWebKeys.SAML_SLO_CONTEXT,
						samlSloContext.toJSONObject());

					JspUtil.dispatch(
						httpServletRequest, httpServletResponse,
						JspUtil.PATH_PORTAL_SAML_SLO, "single-sign-out");
				}
				else if (cmd.equals("logout")) {
					performIdpSpLogout(
						httpServletRequest, httpServletResponse,
						samlSloContext);
				}
				else if (cmd.equals("finish")) {
					_performIdpFinishLogout(
						httpServletRequest, httpServletResponse,
						samlSloContext);
				}
				else if (cmd.equals("status")) {
					_performIdpStatus(httpServletResponse, samlSloContext);
				}
			}
		}
		catch (Exception exception) {
			ExceptionHandlerUtil.handleException(exception);
		}
	}

	@Override
	public void processSingleLogout(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws PortalException {

		SamlBinding samlBinding = null;

		String method = httpServletRequest.getMethod();
		String requestPath = _samlHttpRequestHelper.getRequestPath(
			httpServletRequest);

		if (requestPath.endsWith("/slo") &&
			StringUtil.equalsIgnoreCase(method, HttpMethods.GET)) {

			samlBinding = samlBindingProvider.getSamlBinding(
				SAMLConstants.SAML2_REDIRECT_BINDING_URI);
		}
		else if (requestPath.endsWith("/slo") &&
				 StringUtil.equalsIgnoreCase(method, HttpMethods.POST)) {

			samlBinding = samlBindingProvider.getSamlBinding(
				SAMLConstants.SAML2_POST_BINDING_URI);
		}
		else if (requestPath.endsWith("/slo_soap") &&
				 StringUtil.equalsIgnoreCase(method, HttpMethods.POST)) {

			samlBinding = samlBindingProvider.getSamlBinding(
				SAMLConstants.SAML2_SOAP11_BINDING_URI);
		}
		else {
			throw new UnsupportedBindingException();
		}

		try {
			MessageContext<?> messageContext = decodeSamlMessage(
				httpServletRequest, httpServletResponse, samlBinding, true);

			InOutOperationContext<?, ?> inOutOperationContext =
				messageContext.getSubcontext(InOutOperationContext.class);

			MessageContext<?> inboundMessageContext =
				inOutOperationContext.getInboundMessageContext();

			Object inboundSamlMessage = inboundMessageContext.getMessage();

			if (inboundSamlMessage instanceof LogoutRequest) {
				_processSingleLogoutRequest(
					httpServletRequest, httpServletResponse, messageContext);
			}
			else if (inboundSamlMessage instanceof LogoutResponse) {
				_processSingleLogoutResponse(
					httpServletRequest, httpServletResponse, messageContext);
			}
			else {
				throw new SamlException(
					"Unrecognized inbound SAML message " +
						inboundSamlMessage.getClass());
			}
		}
		catch (Exception exception) {
			ExceptionHandlerUtil.handleException(exception);
		}
	}

	@Override
	public void processSpLogout(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws PortalException {

		try {
			sendSpLogoutRequest(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			ExceptionHandlerUtil.handleException(exception);
		}
	}

	@Override
	public void terminateSpSession(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			SamlSpSession samlSpSession = getSamlSpSession(httpServletRequest);

			if (samlSpSession == null) {
				return;
			}

			samlSpSessionLocalService.deleteSamlSpSession(samlSpSession);

			CookiesManagerUtil.deleteCookies(
				CookiesManagerUtil.getDomain(httpServletRequest),
				httpServletRequest, httpServletResponse,
				SamlWebKeys.SAML_SP_SESSION_KEY);
		}
		catch (SystemException systemException) {
			if (_log.isDebugEnabled()) {
				_log.debug(systemException);
			}
			else {
				_log.error(systemException);
			}
		}
	}

	@Override
	public void terminateSsoSession(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		String samlSsoSessionId = getSamlSsoSessionId(httpServletRequest);

		if (Validator.isNotNull(samlSsoSessionId)) {
			try {
				SamlIdpSsoSession samlIdpSsoSession =
					_samlIdpSsoSessionLocalService.fetchSamlIdpSso(
						samlSsoSessionId);

				if (samlIdpSsoSession != null) {
					_samlIdpSsoSessionLocalService.deleteSamlIdpSsoSession(
						samlIdpSsoSession);

					List<SamlIdpSpSession> samlIdpSpSessions =
						_samlIdpSpSessionLocalService.getSamlIdpSpSessions(
							samlIdpSsoSession.getSamlIdpSsoSessionId());

					for (SamlIdpSpSession samlIdpSpSession :
							samlIdpSpSessions) {

						_samlIdpSpSessionLocalService.deleteSamlIdpSpSession(
							samlIdpSpSession);
					}
				}
			}
			catch (SystemException systemException) {
				if (_log.isDebugEnabled()) {
					_log.debug(systemException);
				}
				else {
					_log.error(systemException);
				}
			}
		}

		CookiesManagerUtil.deleteCookies(
			CookiesManagerUtil.getDomain(httpServletRequest),
			httpServletRequest, httpServletResponse,
			SamlWebKeys.SAML_SSO_SESSION_ID);
	}

	@Activate
	@Override
	protected void activate(BundleContext bundleContext) {
		super.activate(bundleContext);
	}

	@Deactivate
	@Override
	protected void deactivate() {
		super.deactivate();
	}

	protected void performIdpSpLogout(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			SamlSloContext samlSloContext)
		throws Exception {

		String entityId = ParamUtil.getString(httpServletRequest, "entityId");

		SamlSloRequestInfo samlSloRequestInfo =
			samlSloContext.getSamlSloRequestInfo(entityId);

		if (samlSloRequestInfo == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Received logout request for service provider " + entityId +
						" that the user is not logged into");
			}

			JspUtil.dispatch(
				httpServletRequest, httpServletResponse,
				JspUtil.PATH_PORTAL_SAML_ERROR, "single-sign-out", true);

			return;
		}

		if (samlSloRequestInfo.getStatus() ==
				SamlSloRequestInfo.REQUEST_STATUS_SUCCESS) {

			httpServletRequest.setAttribute(
				SamlWebKeys.SAML_SLO_REQUEST_INFO,
				samlSloRequestInfo.toJSONObject());

			JspUtil.dispatch(
				httpServletRequest, httpServletResponse,
				JspUtil.PATH_PORTAL_SAML_SLO_SP_STATUS, "single-sign-out",
				true);

			return;
		}

		MessageContext<?> messageContext = getMessageContext(
			httpServletRequest, httpServletResponse, entityId);

		SAMLPeerEntityContext samlPeerEntityContext =
			messageContext.getSubcontext(SAMLPeerEntityContext.class);

		SAMLMetadataContext samlPeerMetadataContext =
			samlPeerEntityContext.getSubcontext(SAMLMetadataContext.class);

		SPSSODescriptor spSSODescriptor =
			(SPSSODescriptor)samlPeerMetadataContext.getRoleDescriptor();

		SingleLogoutService singleLogoutService =
			SamlUtil.resolveSingleLogoutService(
				spSSODescriptor, SAMLConstants.SAML2_SOAP11_BINDING_URI);

		if (singleLogoutService == null) {
			if (_log.isDebugEnabled()) {
				_log.debug("Single logout not supported by " + entityId);
			}

			samlSloRequestInfo.setStatus(
				SamlSloRequestInfo.REQUEST_STATUS_UNSUPPORTED);
			samlSloRequestInfo.setStatusCode(StatusCode.UNSUPPORTED_BINDING);

			httpServletRequest.setAttribute(
				SamlWebKeys.SAML_SLO_REQUEST_INFO,
				samlSloRequestInfo.toJSONObject());

			JspUtil.dispatch(
				httpServletRequest, httpServletResponse,
				JspUtil.PATH_PORTAL_SAML_SLO_SP_STATUS, "single-sign-out",
				true);
		}
		else {
			try {
				sendIdpLogoutRequest(
					httpServletRequest, httpServletResponse, samlSloContext,
					samlSloRequestInfo);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Unable to perform a single logout for service ",
							"provider ", entityId, " with binding ",
							singleLogoutService.getBinding(), " to ",
							singleLogoutService.getLocation()),
						exception);
				}

				samlSloRequestInfo.setStatus(
					SamlSloRequestInfo.REQUEST_STATUS_FAILED);
				samlSloRequestInfo.setStatusCode(StatusCode.PARTIAL_LOGOUT);

				httpServletRequest.setAttribute(
					SamlWebKeys.SAML_SLO_REQUEST_INFO,
					samlSloRequestInfo.toJSONObject());

				JspUtil.dispatch(
					httpServletRequest, httpServletResponse,
					JspUtil.PATH_PORTAL_SAML_SLO_SP_STATUS, "single-sign-out",
					true);
			}
		}
	}

	protected void sendIdpLogoutRequest(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			SamlSloContext samlSloContext,
			SamlSloRequestInfo samlSloRequestInfo)
		throws Exception {

		MessageContext<LogoutRequest> messageContext =
			(MessageContext<LogoutRequest>)getMessageContext(
				httpServletRequest, httpServletResponse,
				samlSloRequestInfo.getEntityId());

		SAMLPeerEntityContext samlPeerEntityContext =
			messageContext.getSubcontext(SAMLPeerEntityContext.class);

		SAMLMetadataContext samlMetadataContext =
			samlPeerEntityContext.getSubcontext(SAMLMetadataContext.class);

		SPSSODescriptor spSSODescriptor =
			(SPSSODescriptor)samlMetadataContext.getRoleDescriptor();

		SingleLogoutService singleLogoutService =
			SamlUtil.resolveSingleLogoutService(
				spSSODescriptor, SAMLConstants.SAML2_REDIRECT_BINDING_URI);

		SAMLEndpointContext samlPeerEndpointContext =
			samlPeerEntityContext.getSubcontext(
				SAMLEndpointContext.class, true);

		samlPeerEndpointContext.setEndpoint(singleLogoutService);

		SamlIdpSpSession samlIdpSpSession =
			samlSloRequestInfo.getSamlIdpSpSession();

		SamlPeerBinding samlPeerBinding =
			_samlPeerBindingLocalService.getSamlPeerBinding(
				samlIdpSpSession.getSamlPeerBindingId());

		NameID nameID = OpenSamlUtil.buildNameId(
			samlPeerBinding.getSamlNameIdFormat(),
			samlPeerBinding.getSamlNameIdValue());

		SAMLSubjectNameIdentifierContext samlSubjectNameIdentifierContext =
			messageContext.getSubcontext(
				SAMLSubjectNameIdentifierContext.class, true);

		samlSubjectNameIdentifierContext.setSubjectNameIdentifier(nameID);

		samlSloRequestInfo.setInitiateTime(new DateTime(DateTimeZone.UTC));
		samlSloRequestInfo.setStatus(
			SamlSloRequestInfo.REQUEST_STATUS_INITIATED);

		String binding = singleLogoutService.getBinding();

		if (binding.equals(SAMLConstants.SAML2_SOAP11_BINDING_URI)) {
			samlSloRequestInfo.setStatusCode(
				_sendSyncLogoutRequest(messageContext, samlSloContext));

			httpServletRequest.setAttribute(
				SamlWebKeys.SAML_SLO_REQUEST_INFO,
				samlSloRequestInfo.toJSONObject());

			JspUtil.dispatch(
				httpServletRequest, httpServletResponse,
				JspUtil.PATH_PORTAL_SAML_SLO_SP_STATUS, "single-sign-out",
				true);
		}
		else {
			_sendAsyncLogoutRequest(
				messageContext, samlSloContext, httpServletResponse);
		}
	}

	protected void sendSpLogoutRequest(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		SamlSpSession samlSpSession = getSamlSpSession(httpServletRequest);

		if ((samlSpSession == null) || samlSpSession.isTerminated()) {
			_redirectToLogout(httpServletRequest, httpServletResponse);

			return;
		}

		LogoutRequest logoutRequest = OpenSamlUtil.buildLogoutRequest();

		SamlPeerBinding samlPeerBinding =
			_samlPeerBindingLocalService.getSamlPeerBinding(
				samlSpSession.getSamlPeerBindingId());

		_terminateSamlSpSessions(
			samlPeerBinding.getSamlNameIdFormat(),
			samlPeerBinding.getSamlNameIdNameQualifier(),
			samlPeerBinding.getSamlNameIdSpNameQualifier(),
			samlPeerBinding.getSamlNameIdValue(),
			samlPeerBinding.getSamlPeerEntityId(),
			Collections.singletonList(samlSpSession.getSessionIndex()));

		MessageContext<?> messageContext = getMessageContext(
			httpServletRequest, httpServletResponse,
			samlPeerBinding.getSamlPeerEntityId());

		InOutOperationContext<?, LogoutRequest> inOutOperationContext =
			new InOutOperationContext(
				new MessageContext(), new MessageContext());

		messageContext.addSubcontext(inOutOperationContext);

		MessageContext<LogoutRequest> outboundMessageContext =
			inOutOperationContext.getOutboundMessageContext();

		SAMLBindingContext samlBindingContext =
			outboundMessageContext.getSubcontext(
				SAMLBindingContext.class, true);

		samlBindingContext.setRelayState(
			portal.getPortalURL(httpServletRequest));

		outboundMessageContext.setMessage(logoutRequest);

		SAMLPeerEntityContext samlPeerEntityContext =
			messageContext.getSubcontext(SAMLPeerEntityContext.class);

		outboundMessageContext.addSubcontext(samlPeerEntityContext);

		SAMLMetadataContext samlPeerMetadataContext =
			samlPeerEntityContext.getSubcontext(
				SAMLMetadataContext.class, true);

		IDPSSODescriptor idpSSODescriptor =
			(IDPSSODescriptor)samlPeerMetadataContext.getRoleDescriptor();

		SingleLogoutService singleLogoutService =
			SamlUtil.resolveSingleLogoutService(
				idpSSODescriptor, SAMLConstants.SAML2_POST_BINDING_URI);

		logoutRequest.setDestination(singleLogoutService.getLocation());

		logoutRequest.setID(generateIdentifier(20));

		DateTime issueInstantDateTime = new DateTime(DateTimeZone.UTC);

		logoutRequest.setIssueInstant(issueInstantDateTime);

		SAMLSelfEntityContext samlSelfEntityContext =
			messageContext.getSubcontext(SAMLSelfEntityContext.class);

		Issuer issuer = OpenSamlUtil.buildIssuer(
			samlSelfEntityContext.getEntityId());

		outboundMessageContext.addSubcontext(samlSelfEntityContext);

		SecurityParametersContext securityParametersContext =
			outboundMessageContext.getSubcontext(
				SecurityParametersContext.class, true);

		OpenSamlUtil.prepareSecurityParametersContext(
			getSigningCredential(), securityParametersContext,
			idpSSODescriptor);

		logoutRequest.setIssuer(issuer);
		logoutRequest.setNameID(
			OpenSamlUtil.buildNameId(
				samlPeerBinding.getSamlNameIdFormat(),
				samlPeerBinding.getSamlNameIdNameQualifier(),
				samlPeerBinding.getSamlNameIdSpNameQualifier(),
				samlPeerBinding.getSamlNameIdValue()));
		logoutRequest.setVersion(SAMLVersion.VERSION_20);

		_addSessionIndex(logoutRequest, samlSpSession.getSessionIndex());

		SAMLEndpointContext samlPeerEndpointSubcontext =
			samlPeerEntityContext.getSubcontext(
				SAMLEndpointContext.class, true);

		samlPeerEndpointSubcontext.setEndpoint(singleLogoutService);

		sendSamlMessage(messageContext, httpServletResponse);
	}

	private void _addSessionIndex(
		LogoutRequest logoutRequest, String sessionIndexString) {

		if (Validator.isNull(sessionIndexString)) {
			return;
		}

		List<SessionIndex> sessionIndexes = logoutRequest.getSessionIndexes();

		SessionIndex sessionIndex = OpenSamlUtil.buildSessionIndex(
			sessionIndexString);

		sessionIndexes.add(sessionIndex);
	}

	private SamlSloContext _getSamlSloContext(
		HttpServletRequest httpServletRequest,
		MessageContext<?> messageContext) {

		HttpSession httpSession = httpServletRequest.getSession();

		SamlSloContext samlSloContext =
			(SamlSloContext)httpSession.getAttribute(
				SamlWebKeys.SAML_SLO_CONTEXT);

		String samlSsoSessionId = getSamlSsoSessionId(httpServletRequest);

		if (messageContext != null) {
			InOutOperationContext<LogoutRequest, ?> inOutOperationContext =
				messageContext.getSubcontext(InOutOperationContext.class);

			MessageContext<LogoutRequest> inboundMessageContext =
				inOutOperationContext.getInboundMessageContext();

			LogoutRequest logoutRequest = inboundMessageContext.getMessage();

			List<SessionIndex> sessionIndexes =
				logoutRequest.getSessionIndexes();

			if (!sessionIndexes.isEmpty()) {
				SessionIndex sessionIndex = sessionIndexes.get(0);

				samlSsoSessionId = sessionIndex.getSessionIndex();
			}
		}

		if ((samlSloContext == null) && Validator.isNotNull(samlSsoSessionId)) {
			SamlIdpSsoSession samlIdpSsoSession =
				_samlIdpSsoSessionLocalService.fetchSamlIdpSso(
					samlSsoSessionId);

			if (samlIdpSsoSession != null) {
				samlSloContext = new SamlSloContext(
					samlIdpSsoSession, messageContext);

				samlSloContext.setSamlSsoSessionId(samlSsoSessionId);

				if (messageContext != null) {
					SAMLBindingContext samlBindingContext =
						messageContext.getSubcontext(SAMLBindingContext.class);

					samlSloContext.setRelayState(
						samlBindingContext.getRelayState());
				}

				samlSloContext.setUserId(portal.getUserId(httpServletRequest));

				httpSession.setAttribute(
					SamlWebKeys.SAML_SLO_CONTEXT, samlSloContext);
			}
		}

		return samlSloContext;
	}

	private void _initiateIdpSingleLogout(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		SamlSloContext samlSloContext = _getSamlSloContext(
			httpServletRequest, null);

		if (samlSloContext != null) {
			String redirect = StringBundler.concat(
				portal.getPortalURL(httpServletRequest), portal.getPathMain(),
				"/portal/saml/slo_logout");

			httpServletResponse.sendRedirect(redirect);
		}
		else {
			_redirectToLogout(httpServletRequest, httpServletResponse);
		}
	}

	private void _performIdpFinishLogout(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			SamlSloContext samlSloContext)
		throws Exception {

		if (samlSloContext.getMessageContext() != null) {
			String statusCode = StatusCode.SUCCESS;

			for (SamlSloRequestInfo samlRequestInfo :
					samlSloContext.getSamlSloRequestInfos()) {

				String samlRequestInfoStatusCode =
					samlRequestInfo.getStatusCode();

				if (!samlRequestInfoStatusCode.equals(StatusCode.SUCCESS)) {
					statusCode = StatusCode.PARTIAL_LOGOUT;

					break;
				}
			}

			_sendIdpLogoutResponse(
				httpServletRequest, httpServletResponse, statusCode,
				samlSloContext);
		}
		else {
			_redirectToLogout(httpServletRequest, httpServletResponse);
		}
	}

	private void _performIdpStatus(
			HttpServletResponse httpServletResponse,
			SamlSloContext samlSloContext)
		throws Exception {

		for (SamlSloRequestInfo samlRequestInfo :
				samlSloContext.getSamlSloRequestInfos()) {

			int status = samlRequestInfo.getStatus();

			if (status == SamlSloRequestInfo.REQUEST_STATUS_INITIATED) {
				DateTime initiateDateTime = samlRequestInfo.getInitiateTime();

				DateTime expireDateTime = initiateDateTime.plusSeconds(10);

				if (expireDateTime.isBeforeNow()) {
					samlRequestInfo.setStatus(
						SamlSloRequestInfo.REQUEST_STATUS_TIMED_OUT);
					samlRequestInfo.setStatusCode(StatusCode.PARTIAL_LOGOUT);
				}
			}
		}

		httpServletResponse.setContentType(ContentTypes.TEXT_JAVASCRIPT);

		Writer writer = httpServletResponse.getWriter();

		JSONObject jsonObject = samlSloContext.toJSONObject();

		writer.write(jsonObject.toString());
	}

	private void _processIdpLogoutRequest(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			MessageContext<?> messageContext)
		throws Exception {

		SamlSloContext samlSloContext = _getSamlSloContext(
			httpServletRequest, messageContext);

		if (samlSloContext == null) {
			_sendIdpLogoutResponse(
				httpServletRequest, httpServletResponse,
				StatusCode.UNKNOWN_PRINCIPAL,
				new SamlSloContext(null, messageContext));

			return;
		}

		Set<String> samlSpEntityIds = samlSloContext.getSamlSpEntityIds();

		SAMLBindingContext samlBindingContext = messageContext.getSubcontext(
			SAMLBindingContext.class);

		String binding = samlBindingContext.getBindingUri();

		if (binding.equals(SAMLConstants.SAML2_SOAP11_BINDING_URI)) {
			_sendIdpLogoutResponse(
				httpServletRequest, httpServletResponse,
				StatusCode.UNSUPPORTED_BINDING, samlSloContext);
		}
		else if (!samlSpEntityIds.isEmpty()) {
			_initiateIdpSingleLogout(httpServletRequest, httpServletResponse);
		}
		else {
			_sendIdpLogoutResponse(
				httpServletRequest, httpServletResponse, StatusCode.SUCCESS,
				samlSloContext);
		}
	}

	private void _processIdpLogoutResponse(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			MessageContext<?> messageContext)
		throws Exception {

		SamlSloContext samlSloContext = _getSamlSloContext(
			httpServletRequest, null);

		SAMLPeerEntityContext samlPeerEntityContext =
			messageContext.getSubcontext(SAMLPeerEntityContext.class);

		if (samlSloContext == null) {
			throw new UnsolicitedLogoutResponseException(
				"Received logout response from " +
					samlPeerEntityContext.getEntityId() +
						" without an active SSO session");
		}

		InOutOperationContext<LogoutResponse, ?> inOutOperationContext =
			messageContext.getSubcontext(InOutOperationContext.class);

		MessageContext<LogoutResponse> inboundMessageContext =
			inOutOperationContext.getInboundMessageContext();

		LogoutResponse logoutResponse = inboundMessageContext.getMessage();

		Issuer issuer = logoutResponse.getIssuer();

		String entityId = issuer.getValue();

		SamlSloRequestInfo samlSloRequestInfo =
			samlSloContext.getSamlSloRequestInfo(entityId);

		if (samlSloRequestInfo == null) {
			throw new UnsolicitedLogoutResponseException(
				"Received unsolicited logout response from " +
					samlPeerEntityContext.getEntityId());
		}

		Status status = logoutResponse.getStatus();

		StatusCode statusCode = status.getStatusCode();

		samlSloRequestInfo.setStatusCode(statusCode.getValue());

		httpServletRequest.setAttribute(
			SamlWebKeys.SAML_SLO_REQUEST_INFO,
			samlSloRequestInfo.toJSONObject());

		JspUtil.dispatch(
			httpServletRequest, httpServletResponse,
			JspUtil.PATH_PORTAL_SAML_SLO_SP_STATUS, "single-sign-out", true);
	}

	private void _processSingleLogoutRequest(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			MessageContext<?> messageContext)
		throws Exception {

		if (samlProviderConfigurationHelper.isRoleIdp()) {
			_processIdpLogoutRequest(
				httpServletRequest, httpServletResponse, messageContext);
		}
		else if (samlProviderConfigurationHelper.isRoleSp()) {
			_processSpLogoutRequest(httpServletResponse, messageContext);
		}
	}

	private void _processSingleLogoutResponse(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			MessageContext<?> messageContext)
		throws Exception {

		if (samlProviderConfigurationHelper.isRoleIdp()) {
			_processIdpLogoutResponse(
				httpServletRequest, httpServletResponse, messageContext);
		}
		else if (samlProviderConfigurationHelper.isRoleSp()) {
			_processSpLogoutResponse(httpServletRequest, httpServletResponse);
		}
	}

	private void _processSpLogoutRequest(
			HttpServletResponse httpServletResponse,
			MessageContext<?> messageContext)
		throws Exception {

		InOutOperationContext<LogoutRequest, LogoutResponse>
			inOutOperationContext = messageContext.getSubcontext(
				InOutOperationContext.class);

		MessageContext<LogoutRequest> inboundMessageContext =
			inOutOperationContext.getInboundMessageContext();

		LogoutRequest logoutRequest = inboundMessageContext.getMessage();

		NameID nameID = logoutRequest.getNameID();

		SAMLPeerEntityContext samlPeerEntityContext =
			messageContext.getSubcontext(SAMLPeerEntityContext.class);

		String statusCodeURI = _terminateSamlSpSessions(
			nameID.getFormat(), nameID.getNameQualifier(),
			nameID.getSPNameQualifier(), nameID.getValue(),
			samlPeerEntityContext.getEntityId(),
			TransformUtil.transform(
				logoutRequest.getSessionIndexes(),
				SessionIndex::getSessionIndex));

		LogoutResponse logoutResponse = OpenSamlUtil.buildLogoutResponse();

		MessageContext<LogoutResponse> outboundMessageContext =
			inOutOperationContext.getOutboundMessageContext();

		SecurityParametersContext securityParametersContext =
			outboundMessageContext.getSubcontext(
				SecurityParametersContext.class, true);

		SAMLMetadataContext samlPeerMetadataContext =
			samlPeerEntityContext.getSubcontext(SAMLMetadataContext.class);

		SSODescriptor ssoDescriptor =
			(SSODescriptor)samlPeerMetadataContext.getRoleDescriptor();

		OpenSamlUtil.prepareSecurityParametersContext(
			getSigningCredential(), securityParametersContext, ssoDescriptor);

		outboundMessageContext.setMessage(logoutResponse);

		logoutResponse.setID(generateIdentifier(20));
		logoutResponse.setInResponseTo(logoutRequest.getID());
		logoutResponse.setIssueInstant(new DateTime(DateTimeZone.UTC));

		SAMLSelfEntityContext samlSelfEntityContext =
			messageContext.getSubcontext(SAMLSelfEntityContext.class);

		logoutResponse.setIssuer(
			OpenSamlUtil.buildIssuer(samlSelfEntityContext.getEntityId()));

		StatusCode statusCode = OpenSamlUtil.buildStatusCode(statusCodeURI);

		logoutResponse.setStatus(OpenSamlUtil.buildStatus(statusCode));

		logoutResponse.setVersion(SAMLVersion.VERSION_20);

		SAMLBindingContext samlBindingContext = messageContext.getSubcontext(
			SAMLBindingContext.class);

		SingleLogoutService singleLogoutService =
			SamlUtil.resolveSingleLogoutService(
				ssoDescriptor, samlBindingContext.getBindingUri());

		SAMLEndpointContext samlPeerEndpointContext =
			samlPeerEntityContext.getSubcontext(
				SAMLEndpointContext.class, true);

		samlPeerEndpointContext.setEndpoint(singleLogoutService);

		logoutResponse.setDestination(singleLogoutService.getLocation());

		outboundMessageContext.addSubcontext(samlBindingContext);
		outboundMessageContext.addSubcontext(samlPeerEntityContext);
		outboundMessageContext.addSubcontext(samlSelfEntityContext);

		sendSamlMessage(messageContext, httpServletResponse);
	}

	private void _processSpLogoutResponse(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		_redirectToLogout(httpServletRequest, httpServletResponse);
	}

	private void _redirectToLogout(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		if (samlProviderConfigurationHelper.isRoleIdp()) {
			terminateSsoSession(httpServletRequest, httpServletResponse);
		}

		String relayState = ParamUtil.getString(
			httpServletRequest, "RelayState");

		if (Validator.isNotNull(relayState)) {
			httpServletResponse.sendRedirect(
				portal.escapeRedirect(
					StringBundler.concat(
						relayState, portal.getPathMain(), "/portal/logout")));
		}
		else {
			httpServletResponse.sendRedirect(
				StringBundler.concat(
					portal.getPortalURL(httpServletRequest),
					portal.getPathMain(), "/portal/logout"));
		}
	}

	private void _sendAsyncLogoutRequest(
			MessageContext<LogoutRequest> messageContext,
			SamlSloContext samlSloContext,
			HttpServletResponse httpServletResponse)
		throws Exception {

		SAMLPeerEntityContext samlPeerEntityContext =
			messageContext.getSubcontext(SAMLPeerEntityContext.class);

		SAMLEndpointContext samlPeerEndpointContext =
			samlPeerEntityContext.getSubcontext(SAMLEndpointContext.class);

		SingleLogoutService singleLogoutService =
			(SingleLogoutService)samlPeerEndpointContext.getEndpoint();

		LogoutRequest logoutRequest = OpenSamlUtil.buildLogoutRequest();

		logoutRequest.setDestination(singleLogoutService.getLocation());
		logoutRequest.setID(generateIdentifier(20));
		logoutRequest.setIssueInstant(new DateTime(DateTimeZone.UTC));

		SAMLSelfEntityContext samlSelfEntityContext =
			messageContext.getSubcontext(SAMLSelfEntityContext.class);

		logoutRequest.setIssuer(
			OpenSamlUtil.buildIssuer(samlSelfEntityContext.getEntityId()));

		SAMLSubjectNameIdentifierContext samlSubjectNameIdentifierContext =
			messageContext.getSubcontext(
				SAMLSubjectNameIdentifierContext.class);

		logoutRequest.setNameID(
			samlSubjectNameIdentifierContext.getSAML2SubjectNameID());

		logoutRequest.setVersion(SAMLVersion.VERSION_20);

		_addSessionIndex(logoutRequest, samlSloContext.getSamlSsoSessionId());

		messageContext.setMessage(logoutRequest);

		Credential credential = getSigningCredential();

		SAMLProtocolContext samlProtocolContext = messageContext.getSubcontext(
			SAMLProtocolContext.class, true);

		samlProtocolContext.setProtocol(SAMLConstants.SAML20P_NS);

		SAMLMetadataContext samlMetadataContext =
			samlPeerEntityContext.getSubcontext(SAMLMetadataContext.class);

		RoleDescriptor roleDescriptor = samlMetadataContext.getRoleDescriptor();

		OpenSamlUtil.signObject(logoutRequest, credential, roleDescriptor);

		SamlBinding samlBinding = samlBindingProvider.getSamlBinding(
			singleLogoutService.getBinding());

		Supplier<HttpServletResponseMessageEncoder>
			httpServletResponseMessageEncoderSupplier =
				samlBinding.getHttpServletResponseMessageEncoderSupplier();

		HttpServletResponseMessageEncoder httpServletResponseMessageEncoder =
			httpServletResponseMessageEncoderSupplier.get();

		SecurityParametersContext securityParametersContext =
			messageContext.getSubcontext(SecurityParametersContext.class);

		OpenSamlUtil.prepareSecurityParametersContext(
			credential, securityParametersContext, roleDescriptor);

		httpServletResponseMessageEncoder.setHttpServletResponse(
			httpServletResponse);
		httpServletResponseMessageEncoder.setMessageContext(messageContext);

		httpServletResponseMessageEncoder.initialize();

		httpServletResponseMessageEncoder.encode();
	}

	private void _sendIdpLogoutResponse(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String statusCodeURI,
			SamlSloContext samlSloContext)
		throws Exception {

		LogoutResponse logoutResponse = OpenSamlUtil.buildLogoutResponse();

		MessageContext<?> messageContext = samlSloContext.getMessageContext();

		InOutOperationContext<LogoutRequest, LogoutResponse>
			inOutOperationContext = messageContext.getSubcontext(
				InOutOperationContext.class);

		MessageContext<LogoutRequest> inboundMessageContext =
			inOutOperationContext.getInboundMessageContext();

		SAMLPeerEntityContext samlPeerEntityContext =
			messageContext.getSubcontext(SAMLPeerEntityContext.class);

		SAMLMetadataContext samlPeerMetadataContext =
			samlPeerEntityContext.getSubcontext(SAMLMetadataContext.class);

		SSODescriptor ssoDescriptor =
			(SSODescriptor)samlPeerMetadataContext.getRoleDescriptor();

		SAMLBindingContext samlBindingContext = messageContext.getSubcontext(
			SAMLBindingContext.class);

		SingleLogoutService singleLogoutService =
			SamlUtil.resolveSingleLogoutService(
				ssoDescriptor, samlBindingContext.getBindingUri());

		logoutResponse.setDestination(singleLogoutService.getLocation());

		logoutResponse.setID(generateIdentifier(20));

		LogoutRequest logoutRequest = inboundMessageContext.getMessage();

		logoutResponse.setInResponseTo(logoutRequest.getID());

		logoutResponse.setIssueInstant(new DateTime(DateTimeZone.UTC));

		SAMLSelfEntityContext samlSelfEntityContext =
			messageContext.getSubcontext(SAMLSelfEntityContext.class);

		logoutResponse.setIssuer(
			OpenSamlUtil.buildIssuer(samlSelfEntityContext.getEntityId()));

		StatusCode statusCode = OpenSamlUtil.buildStatusCode(statusCodeURI);

		logoutResponse.setStatus(OpenSamlUtil.buildStatus(statusCode));

		logoutResponse.setVersion(SAMLVersion.VERSION_20);

		MessageContext<LogoutResponse> outboundMessageContext =
			inOutOperationContext.getOutboundMessageContext();

		outboundMessageContext.setMessage(logoutResponse);

		outboundMessageContext.addSubcontext(samlPeerEntityContext);

		samlBindingContext = outboundMessageContext.getSubcontext(
			SAMLBindingContext.class, true);

		samlBindingContext.setRelayState(samlSloContext.getRelayState());

		SecurityParametersContext securityParametersContext =
			outboundMessageContext.getSubcontext(
				SecurityParametersContext.class, true);

		OpenSamlUtil.prepareSecurityParametersContext(
			getSigningCredential(), securityParametersContext, ssoDescriptor);

		SAMLProtocolContext samlProtocolContext =
			outboundMessageContext.getSubcontext(
				SAMLProtocolContext.class, true);

		samlProtocolContext.setProtocol(SAMLConstants.SAML20P_NS);

		SAMLEndpointContext samlPeerEndpointContext =
			samlPeerEntityContext.getSubcontext(
				SAMLEndpointContext.class, true);

		samlPeerEndpointContext.setEndpoint(singleLogoutService);

		if (!statusCodeURI.equals(StatusCode.UNSUPPORTED_BINDING)) {
			terminateSsoSession(httpServletRequest, httpServletResponse);

			logout(httpServletRequest, httpServletResponse);
		}

		sendSamlMessage(messageContext, httpServletResponse);
	}

	private String _sendSyncLogoutRequest(
			MessageContext<?> messageContext, SamlSloContext samlSloContext)
		throws Exception {

		SAMLPeerEntityContext samlPeerEntityContext =
			messageContext.getSubcontext(SAMLPeerEntityContext.class);

		SAMLEndpointContext samlPeerEndpointSubcontext =
			samlPeerEntityContext.getSubcontext(SAMLEndpointContext.class);

		SingleLogoutService singleLogoutService =
			(SingleLogoutService)samlPeerEndpointSubcontext.getEndpoint();

		LogoutRequest logoutRequest = OpenSamlUtil.buildLogoutRequest();

		logoutRequest.setDestination(singleLogoutService.getLocation());
		logoutRequest.setID(generateIdentifier(20));
		logoutRequest.setIssueInstant(new DateTime(DateTimeZone.UTC));

		SAMLSelfEntityContext samlSelfEntityContext =
			messageContext.getSubcontext(SAMLSelfEntityContext.class);

		logoutRequest.setIssuer(
			OpenSamlUtil.buildIssuer(samlSelfEntityContext.getEntityId()));

		SAMLSubjectNameIdentifierContext samlSubjectNameIdentifierContext =
			messageContext.getSubcontext(
				SAMLSubjectNameIdentifierContext.class);

		logoutRequest.setNameID(
			samlSubjectNameIdentifierContext.getSAML2SubjectNameID());

		logoutRequest.setVersion(SAMLVersion.VERSION_20);

		_addSessionIndex(logoutRequest, samlSloContext.getSamlSsoSessionId());

		InOutOperationContext<LogoutResponse, LogoutRequest>
			inOutOperationContext = messageContext.getSubcontext(
				InOutOperationContext.class);

		MessageContext<LogoutRequest> outboundMessageContext =
			inOutOperationContext.getOutboundMessageContext();

		outboundMessageContext.addSubcontext(samlPeerEndpointSubcontext);

		outboundMessageContext.setMessage(logoutRequest);

		Credential credential = getSigningCredential();

		SecurityParametersContext securityParametersContext =
			outboundMessageContext.getSubcontext(
				SecurityParametersContext.class, true);

		SAMLMetadataContext samlPeerMetadataContext =
			samlPeerEntityContext.getSubcontext(SAMLMetadataContext.class);

		RoleDescriptor roleDescriptor =
			samlPeerMetadataContext.getRoleDescriptor();

		OpenSamlUtil.prepareSecurityParametersContext(
			credential, securityParametersContext, roleDescriptor);

		SAMLProtocolContext samlProtocolContext =
			outboundMessageContext.getSubcontext(SAMLProtocolContext.class);

		samlProtocolContext.setProtocol(SAMLConstants.SAML20P_NS);

		OpenSamlUtil.signObject(logoutRequest, credential, roleDescriptor);

		SamlBinding samlBinding = samlBindingProvider.getSamlBinding(
			SAMLConstants.SAML2_SOAP11_BINDING_URI);

		PipelineFactoryHttpSOAPClient<Object, Object>
			pipelineFactoryHttpSOAPClient =
				new PipelineFactoryHttpSOAPClient<>();

		pipelineFactoryHttpSOAPClient.setPipelineFactory(
			new HttpClientMessagePipelineFactory<Object, Object>() {

				@Nonnull
				@Override
				public HttpClientMessagePipeline<Object, Object> newInstance() {
					Supplier<HttpServletResponseMessageEncoder>
						httpServletResponseMessageEncoderSupplier =
							samlBinding.
								getHttpServletResponseMessageEncoderSupplier();

					Supplier<HttpServletRequestMessageDecoder>
						httpServletResponseMessageDecoder =
							samlBinding.
								getHttpServletRequestMessageDecoderSupplier();

					return new BasicHttpClientMessagePipeline<>(
						httpServletResponseMessageEncoderSupplier.get(),
						httpServletResponseMessageDecoder.get());
				}

				@Nonnull
				@Override
				public HttpClientMessagePipeline<Object, Object> newInstance(
					@Nullable String pipelineName) {

					return newInstance();
				}

			});

		pipelineFactoryHttpSOAPClient.setHttpClient(
			_httpClientFactory.getHttpClient());

		pipelineFactoryHttpSOAPClient.initialize();

		pipelineFactoryHttpSOAPClient.send(
			singleLogoutService.getLocation(), inOutOperationContext);

		MessageContext<LogoutResponse> inboundMessageContext =
			inOutOperationContext.getInboundMessageContext();

		LogoutResponse logoutResponse = inboundMessageContext.getMessage();

		Status status = logoutResponse.getStatus();

		StatusCode statusCode = status.getStatusCode();

		return statusCode.getValue();
	}

	private String _terminateSamlSpSessions(
			String nameIDFormat, String nameIDNameQualifier,
			String nameIDSPNameQualifier, String nameIDValue,
			String samlPeerEntityId, List<String> sessionIndexes)
		throws Exception {

		String statusCodeURI = StatusCode.UNKNOWN_PRINCIPAL;

		if (sessionIndexes.isEmpty()) {
			List<SamlSpSession> samlSpSessions =
				samlSpSessionLocalService.getSamlSpSessions(
					CompanyThreadLocal.getCompanyId(), nameIDFormat,
					nameIDNameQualifier, nameIDSPNameQualifier, nameIDValue,
					samlPeerEntityId);

			if (!samlSpSessions.isEmpty()) {
				statusCodeURI = StatusCode.SUCCESS;
			}

			for (SamlSpSession samlSpSession : samlSpSessions) {
				samlSpSession.setTerminated(true);

				samlSpSessionLocalService.updateSamlSpSession(samlSpSession);
			}
		}

		for (String sessionIndex : sessionIndexes) {
			List<SamlSpSession> samlSpSessions =
				samlSpSessionLocalService.fetchSamlSpSessionsBySessionIndex(
					CompanyThreadLocal.getCompanyId(), sessionIndex);

			for (SamlSpSession samlSpSession : samlSpSessions) {
				SamlPeerBinding samlPeerBinding =
					_samlPeerBindingLocalService.getSamlPeerBinding(
						samlSpSession.getSamlPeerBindingId());

				if (Objects.equals(
						samlPeerBinding.getSamlNameIdValue(), nameIDValue) &&
					Objects.equals(
						samlPeerBinding.getSamlNameIdFormat(), nameIDFormat)) {

					statusCodeURI = StatusCode.SUCCESS;

					samlSpSession.setTerminated(true);

					samlSpSessionLocalService.updateSamlSpSession(
						samlSpSession);
				}
			}
		}

		return statusCodeURI;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SingleLogoutProfileImpl.class);

	@Reference
	private HttpClientFactory _httpClientFactory;

	@Reference
	private SamlHttpRequestHelper _samlHttpRequestHelper;

	@Reference
	private SamlIdpSpSessionLocalService _samlIdpSpSessionLocalService;

	@Reference
	private SamlIdpSsoSessionLocalService _samlIdpSsoSessionLocalService;

	@Reference
	private SamlPeerBindingLocalService _samlPeerBindingLocalService;

	@Reference
	private UserLocalService _userLocalService;

}