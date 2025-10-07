/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.servlet.profile;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LRUMap;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.saml.constants.SamlWebKeys;
import com.liferay.saml.opensaml.integration.internal.binding.SamlBinding;
import com.liferay.saml.opensaml.integration.internal.bootstrap.ParserPoolUtil;
import com.liferay.saml.opensaml.integration.internal.resolver.AttributePublisherImpl;
import com.liferay.saml.opensaml.integration.internal.resolver.AttributeResolverSAMLContextImpl;
import com.liferay.saml.opensaml.integration.internal.resolver.DecrypterContext;
import com.liferay.saml.opensaml.integration.internal.resolver.DefaultServiceReferenceMapper;
import com.liferay.saml.opensaml.integration.internal.resolver.NameIdResolverSAMLContextImpl;
import com.liferay.saml.opensaml.integration.internal.resolver.SubjectAssertionContext;
import com.liferay.saml.opensaml.integration.internal.resolver.UserResolverSAMLContextImpl;
import com.liferay.saml.opensaml.integration.internal.util.ConfigurationServiceBootstrapUtil;
import com.liferay.saml.opensaml.integration.internal.util.OpenSamlUtil;
import com.liferay.saml.opensaml.integration.internal.util.SamlUtil;
import com.liferay.saml.opensaml.integration.resolver.AttributeResolver;
import com.liferay.saml.opensaml.integration.resolver.NameIdResolver;
import com.liferay.saml.opensaml.integration.resolver.UserResolver;
import com.liferay.saml.persistence.exception.NoSuchIdpSpSessionException;
import com.liferay.saml.persistence.model.SamlIdpSpConnection;
import com.liferay.saml.persistence.model.SamlIdpSsoSession;
import com.liferay.saml.persistence.model.SamlSpAuthRequest;
import com.liferay.saml.persistence.model.SamlSpIdpConnection;
import com.liferay.saml.persistence.model.SamlSpMessage;
import com.liferay.saml.persistence.model.SamlSpSession;
import com.liferay.saml.persistence.service.SamlIdpSpSessionLocalService;
import com.liferay.saml.persistence.service.SamlIdpSsoSessionLocalService;
import com.liferay.saml.persistence.service.SamlSpAuthRequestLocalService;
import com.liferay.saml.persistence.service.SamlSpMessageLocalService;
import com.liferay.saml.runtime.SamlException;
import com.liferay.saml.runtime.configuration.SamlConfiguration;
import com.liferay.saml.runtime.configuration.SamlProviderConfiguration;
import com.liferay.saml.runtime.exception.AssertionException;
import com.liferay.saml.runtime.exception.AudienceException;
import com.liferay.saml.runtime.exception.AuthnAgeException;
import com.liferay.saml.runtime.exception.DestinationException;
import com.liferay.saml.runtime.exception.EntityInteractionException;
import com.liferay.saml.runtime.exception.ExpiredException;
import com.liferay.saml.runtime.exception.InResponseToException;
import com.liferay.saml.runtime.exception.IssuerException;
import com.liferay.saml.runtime.exception.ReplayException;
import com.liferay.saml.runtime.exception.SignatureException;
import com.liferay.saml.runtime.exception.StatusException;
import com.liferay.saml.runtime.exception.SubjectException;
import com.liferay.saml.runtime.servlet.profile.WebSsoProfile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import net.shibboleth.utilities.java.support.xml.ParserPool;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.messaging.context.InOutOperationContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.messaging.context.SAMLEndpointContext;
import org.opensaml.saml.common.messaging.context.SAMLMessageInfoContext;
import org.opensaml.saml.common.messaging.context.SAMLMetadataContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLProtocolContext;
import org.opensaml.saml.common.messaging.context.SAMLSelfEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLSubjectNameIdentifierContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.criterion.ProtocolCriterion;
import org.opensaml.saml.criterion.RoleDescriptorCriterion;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.Audience;
import org.opensaml.saml.saml2.core.AudienceRestriction;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NameIDPolicy;
import org.opensaml.saml.saml2.core.NameIDType;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.saml.saml2.encryption.Encrypter;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
import org.opensaml.saml.security.impl.SAMLMetadataEncryptionParametersResolver;
import org.opensaml.saml.security.impl.SAMLSignatureProfileValidator;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.security.trust.TrustEngine;
import org.opensaml.xmlsec.DecryptionParameters;
import org.opensaml.xmlsec.DecryptionParametersResolver;
import org.opensaml.xmlsec.EncryptionConfiguration;
import org.opensaml.xmlsec.EncryptionParameters;
import org.opensaml.xmlsec.SecurityConfigurationSupport;
import org.opensaml.xmlsec.context.SecurityParametersContext;
import org.opensaml.xmlsec.criterion.DecryptionConfigurationCriterion;
import org.opensaml.xmlsec.criterion.EncryptionConfigurationCriterion;
import org.opensaml.xmlsec.criterion.EncryptionOptionalCriterion;
import org.opensaml.xmlsec.encryption.support.DataEncryptionParameters;
import org.opensaml.xmlsec.encryption.support.DecryptionException;
import org.opensaml.xmlsec.encryption.support.KeyEncryptionParameters;
import org.opensaml.xmlsec.impl.BasicDecryptionParametersResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Mika Koivisto
 */
@Component(
	configurationPid = "com.liferay.saml.runtime.configuration.SamlConfiguration",
	service = WebSsoProfile.class
)
public class WebSsoProfileImpl extends BaseProfile implements WebSsoProfile {

	@Override
	public void processAuthnRequest(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws PortalException {

		try {
			_processAuthnRequest(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			ExceptionHandlerUtil.handleException(exception);
		}
	}

	@Override
	public void processResponse(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws PortalException {

		MessageContext<?> messageContext = null;

		try {
			messageContext = _decodeAuthnResponse(
				httpServletRequest, httpServletResponse,
				samlBindingProvider.getSamlBinding(
					SAMLConstants.SAML2_POST_BINDING_URI));

			_processResponse(
				messageContext, httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
			else {
				if (!(exception instanceof AuthnAgeException ||
					  exception instanceof SubjectException)) {

					_log.error(exception);
				}
			}

			if (messageContext == null) {
				ExceptionHandlerUtil.handleException(exception);

				return;
			}

			SAMLPeerEntityContext samlPeerEntityContext =
				messageContext.getSubcontext(SAMLPeerEntityContext.class);

			if (samlPeerEntityContext == null) {
				ExceptionHandlerUtil.handleException(exception);

				return;
			}

			SAMLSubjectNameIdentifierContext samlSubjectNameIdentifierContext =
				messageContext.getSubcontext(
					SAMLSubjectNameIdentifierContext.class);

			if (samlSubjectNameIdentifierContext == null) {
				ExceptionHandlerUtil.handleException(exception);

				return;
			}

			NameID saml2SubjectNameID =
				samlSubjectNameIdentifierContext.getSAML2SubjectNameID();

			if (saml2SubjectNameID == null) {
				ExceptionHandlerUtil.handleException(exception);

				return;
			}

			String nameIdValue = saml2SubjectNameID.getValue();

			throw new EntityInteractionException(
				samlPeerEntityContext.getEntityId(), nameIdValue, exception);
		}
	}

	@Override
	public void sendAuthnRequest(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String relayState)
		throws PortalException {

		try {
			doSendAuthnRequest(
				httpServletRequest, httpServletResponse, relayState);
		}
		catch (Exception exception) {
			ExceptionHandlerUtil.handleException(exception);
		}
	}

	@Override
	public void updateSamlSpSession(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		SamlSpSession samlSpSession = getSamlSpSession(httpServletRequest);

		HttpSession httpSession = httpServletRequest.getSession();

		String jSessionId = httpSession.getId();

		if ((samlSpSession != null) &&
			!jSessionId.equals(samlSpSession.getJSessionId())) {

			try {
				samlSpSessionLocalService.updateSamlSpSession(
					samlSpSession.getPrimaryKey(), jSessionId);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		super.activate(bundleContext);

		_samlConfiguration = ConfigurableUtil.createConfigurable(
			SamlConfiguration.class, properties);
		_stringAttributeResolverServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, AttributeResolver.class, "(companyId=*)",
				new DefaultServiceReferenceMapper(_log));
		_stringNameIdResolverServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, NameIdResolver.class, "(companyId=*)",
				new DefaultServiceReferenceMapper(_log));
	}

	@Deactivate
	@Override
	protected void deactivate() {
		super.deactivate();

		_stringAttributeResolverServiceTrackerMap.close();
		_stringNameIdResolverServiceTrackerMap.close();
	}

	protected SamlSsoRequestContext decodeAuthnRequest(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		SamlSsoRequestContext samlSsoRequestContext = null;

		String samlMessageId = ParamUtil.getString(
			httpServletRequest, "samlMessageId");

		if (!Validator.isBlank(samlMessageId)) {
			samlSsoRequestContext = _decodeAuthnConversationAfterLogin(
				httpServletRequest, httpServletResponse);

			if (samlSsoRequestContext != null) {
				MessageContext<?> messageContext =
					samlSsoRequestContext.getSAMLMessageContext();

				InOutOperationContext<?, ?> inOutOperationContext =
					messageContext.getSubcontext(
						InOutOperationContext.class, false);

				if (inOutOperationContext != null) {
					MessageContext<?> inboundMessageContext =
						inOutOperationContext.getInboundMessageContext();

					SAMLMessageInfoContext samlMessageInfoContext =
						inboundMessageContext.getSubcontext(
							SAMLMessageInfoContext.class, true);

					if (samlMessageId.equals(
							samlMessageInfoContext.getMessageId())) {

						return samlSsoRequestContext;
					}
				}
			}
		}

		boolean idpInitiatedSSO = false;

		String entityId = ParamUtil.getString(httpServletRequest, "entityId");
		String samlRequest = ParamUtil.getString(
			httpServletRequest, "SAMLRequest");

		if (Validator.isNotNull(entityId) && Validator.isNull(samlRequest)) {
			idpInitiatedSSO = true;
		}

		if (idpInitiatedSSO) {
			if (Validator.isBlank(samlMessageId)) {
				samlSsoRequestContext = _decodeAuthnConversationAfterLogin(
					httpServletRequest, httpServletResponse);
			}

			if (samlSsoRequestContext != null) {
				MessageContext<?> messageContext =
					samlSsoRequestContext.getSAMLMessageContext();

				SAMLPeerEntityContext samlPeerEntityContext =
					messageContext.getSubcontext(SAMLPeerEntityContext.class);

				if (entityId.equals(samlPeerEntityContext.getEntityId())) {
					return samlSsoRequestContext;
				}
			}
		}

		MessageContext<?> messageContext = null;

		SamlBinding samlBinding = null;

		if (StringUtil.equalsIgnoreCase(
				httpServletRequest.getMethod(), "GET")) {

			samlBinding = samlBindingProvider.getSamlBinding(
				SAMLConstants.SAML2_REDIRECT_BINDING_URI);
		}
		else {
			samlBinding = samlBindingProvider.getSamlBinding(
				SAMLConstants.SAML2_POST_BINDING_URI);
		}

		if (idpInitiatedSSO) {
			messageContext = getMessageContext(
				httpServletRequest, httpServletResponse, entityId);

			SAMLBindingContext samlBindingContext =
				messageContext.getSubcontext(SAMLBindingContext.class);

			samlBindingContext.setBindingUri(
				samlBinding.getCommunicationProfileId());

			String relayState = ParamUtil.getString(
				httpServletRequest, "RelayState");

			samlBindingContext.setRelayState(relayState);

			SAMLPeerEntityContext samlPeerEntityContext =
				messageContext.getSubcontext(SAMLPeerEntityContext.class);

			samlSsoRequestContext = new SamlSsoRequestContext(
				samlPeerEntityContext.getEntityId(), relayState,
				messageContext);
		}
		else {
			SamlProviderConfiguration samlProviderConfiguration =
				samlProviderConfigurationHelper.getSamlProviderConfiguration();

			messageContext = decodeSamlMessage(
				httpServletRequest, httpServletResponse, samlBinding,
				samlProviderConfiguration.authnRequestSignatureRequired());

			InOutOperationContext<AuthnRequest, ?> inOutOperationContext =
				messageContext.getSubcontext(InOutOperationContext.class);

			MessageContext<AuthnRequest> inboundMessageContext =
				inOutOperationContext.getInboundMessageContext();

			AuthnRequest authnRequest = inboundMessageContext.getMessage();

			SAMLMessageInfoContext samlMessageInfoContext =
				inboundMessageContext.getSubcontext(
					SAMLMessageInfoContext.class, true);

			samlMessageInfoContext.setMessageId(authnRequest.getID());

			String authnRequestXml = OpenSamlUtil.marshall(authnRequest);

			SAMLPeerEntityContext samlPeerEntityContext =
				messageContext.getSubcontext(SAMLPeerEntityContext.class);

			SAMLBindingContext samlBindingContext =
				messageContext.getSubcontext(SAMLBindingContext.class);

			samlSsoRequestContext = new SamlSsoRequestContext(
				authnRequestXml, samlPeerEntityContext.getEntityId(),
				samlBindingContext.getRelayState(), messageContext);
		}

		String samlSsoSessionId = getSamlSsoSessionId(httpServletRequest);

		if (Validator.isNotNull(samlSsoSessionId)) {
			samlSsoRequestContext.setSamlSsoSessionId(samlSsoSessionId);
		}
		else {
			samlSsoRequestContext.setNewSession(true);
			samlSsoRequestContext.setSamlSsoSessionId(generateIdentifier(30));
		}

		samlSsoRequestContext.setStage(SamlSsoRequestContext.STAGE_INITIAL);
		samlSsoRequestContext.setUserId(portal.getUserId(httpServletRequest));

		return samlSsoRequestContext;
	}

	protected void doSendAuthnRequest(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String relayState)
		throws Exception {

		SamlSpIdpConnection samlSpIdpConnection =
			(SamlSpIdpConnection)httpServletRequest.getAttribute(
				SamlWebKeys.SAML_SP_IDP_CONNECTION);

		if (samlSpIdpConnection == null) {
			return;
		}

		String entityId = samlSpIdpConnection.getSamlIdpEntityId();

		MessageContext<?> messageContext = getMessageContext(
			httpServletRequest, httpServletResponse, entityId);

		InOutOperationContext<?, AuthnRequest> inOutOperationContext =
			new InOutOperationContext(
				new MessageContext(), new MessageContext());

		messageContext.addSubcontext(inOutOperationContext);

		MessageContext<AuthnRequest> outboundMessageContext =
			inOutOperationContext.getOutboundMessageContext();

		SAMLSelfEntityContext samlSelfEntityContext =
			messageContext.getSubcontext(SAMLSelfEntityContext.class);

		SAMLMetadataContext samlSelfMetadataContext =
			samlSelfEntityContext.getSubcontext(SAMLMetadataContext.class);

		SPSSODescriptor spSSODescriptor =
			(SPSSODescriptor)samlSelfMetadataContext.getRoleDescriptor();

		SAMLPeerEntityContext samlPeerEntityContext =
			messageContext.getSubcontext(SAMLPeerEntityContext.class);

		outboundMessageContext.addSubcontext(samlPeerEntityContext);

		SAMLMetadataContext samlPeerMetadataContext =
			samlPeerEntityContext.getSubcontext(SAMLMetadataContext.class);

		IDPSSODescriptor idpSSODescriptor =
			(IDPSSODescriptor)samlPeerMetadataContext.getRoleDescriptor();

		SingleSignOnService singleSignOnService =
			SamlUtil.resolveSingleSignOnService(
				idpSSODescriptor, SAMLConstants.SAML2_POST_BINDING_URI);

		NameIDPolicy nameIDPolicy = OpenSamlUtil.buildNameIdPolicy();

		nameIDPolicy.setAllowCreate(true);
		nameIDPolicy.setFormat(_getNameIdFormat(entityId));

		AuthnRequest authnRequest = OpenSamlUtil.buildAuthnRequest(
			samlSelfEntityContext.getEntityId(),
			SamlUtil.getAssertionConsumerServiceForBinding(
				spSSODescriptor, SAMLConstants.SAML2_POST_BINDING_URI),
			singleSignOnService, nameIDPolicy);

		if (samlSpIdpConnection.isForceAuthn() ||
			GetterUtil.getBoolean(
				httpServletRequest.getAttribute(
					SamlWebKeys.FORCE_REAUTHENTICATION),
				Boolean.FALSE)) {

			authnRequest.setForceAuthn(true);
		}
		else {
			authnRequest.setForceAuthn(false);
		}

		authnRequest.setID(generateIdentifier(20));

		outboundMessageContext.setMessage(authnRequest);

		if (spSSODescriptor.isAuthnRequestsSigned() ||
			idpSSODescriptor.getWantAuthnRequestsSigned()) {

			Credential credential = getSigningCredential();

			SecurityParametersContext securityParametersContext =
				outboundMessageContext.getSubcontext(
					SecurityParametersContext.class, true);

			OpenSamlUtil.prepareSecurityParametersContext(
				getSigningCredential(), securityParametersContext,
				idpSSODescriptor);

			OpenSamlUtil.signObject(authnRequest, credential, idpSSODescriptor);
		}

		SAMLBindingContext samlBindingContext =
			outboundMessageContext.getSubcontext(
				SAMLBindingContext.class, true);

		samlBindingContext.setRelayState(authnRequest.getID());

		SAMLEndpointContext samlPeerEndpointContext =
			samlPeerEntityContext.getSubcontext(
				SAMLEndpointContext.class, true);

		samlPeerEndpointContext.setEndpoint(singleSignOnService);

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			httpServletRequest);

		_samlSpAuthRequestLocalService.addSamlSpAuthRequest(
			samlPeerEntityContext.getEntityId(), relayState,
			authnRequest.getID(), serviceContext);

		sendSamlMessage(messageContext, httpServletResponse);
	}

	protected AudienceRestriction getSuccessAudienceRestriction(
		String entityId) {

		AudienceRestriction audienceRestriction =
			OpenSamlUtil.buildAudienceRestriction();

		List<Audience> audiences = audienceRestriction.getAudiences();

		Audience audience = OpenSamlUtil.buildAudience();

		audience.setAudienceURI(entityId);

		audiences.add(audience);

		return audienceRestriction;
	}

	protected Conditions getSuccessConditions(
		SamlSsoRequestContext samlSsoRequestContext, DateTime notBeforeDateTime,
		DateTime notOnOrAfterDateTime) {

		Conditions conditions = OpenSamlUtil.buildConditions();

		conditions.setNotBefore(notBeforeDateTime);
		conditions.setNotOnOrAfter(notOnOrAfterDateTime);

		List<AudienceRestriction> audienceRestrictions =
			conditions.getAudienceRestrictions();

		MessageContext<?> messageContext =
			samlSsoRequestContext.getSAMLMessageContext();

		SAMLPeerEntityContext samlPeerEntityContext =
			messageContext.getSubcontext(SAMLPeerEntityContext.class);

		AudienceRestriction audienceRestriction = getSuccessAudienceRestriction(
			samlPeerEntityContext.getEntityId());

		audienceRestrictions.add(audienceRestriction);

		return conditions;
	}

	protected Subject getSuccessSubject(
		NameID nameID, SubjectConfirmationData subjectConfirmationData) {

		SubjectConfirmation subjectConfirmation =
			OpenSamlUtil.buildSubjectConfirmation();

		subjectConfirmation.setMethod(SubjectConfirmation.METHOD_BEARER);
		subjectConfirmation.setSubjectConfirmationData(subjectConfirmationData);

		Subject subject = OpenSamlUtil.buildSubject(nameID);

		List<SubjectConfirmation> subjectConfirmations =
			subject.getSubjectConfirmations();

		subjectConfirmations.add(subjectConfirmation);

		return subject;
	}

	protected SubjectConfirmationData getSuccessSubjectConfirmationData(
		SamlSsoRequestContext samlSsoRequestContext,
		AssertionConsumerService assertionConsumerService,
		DateTime issueInstantDateTime) {

		SubjectConfirmationData subjectConfirmationData =
			OpenSamlUtil.buildSubjectConfirmationData();

		MessageContext<?> messageContext =
			samlSsoRequestContext.getSAMLMessageContext();

		InOutOperationContext<?, ?> inOutOperationContext =
			messageContext.getSubcontext(InOutOperationContext.class, false);

		if (inOutOperationContext != null) {
			MessageContext<?> inboundMessageContext =
				inOutOperationContext.getInboundMessageContext();

			SAMLMessageInfoContext samlMessageInfoContext =
				inboundMessageContext.getSubcontext(
					SAMLMessageInfoContext.class);

			subjectConfirmationData.setInResponseTo(
				samlMessageInfoContext.getMessageId());
		}

		subjectConfirmationData.setRecipient(
			assertionConsumerService.getLocation());

		SAMLPeerEntityContext samlPeerEntityContext =
			messageContext.getSubcontext(SAMLPeerEntityContext.class);

		DateTime notOnOrAfterDateTime = issueInstantDateTime.plusSeconds(
			_getAssertionLifetime(samlPeerEntityContext.getEntityId()));

		subjectConfirmationData.setNotOnOrAfter(notOnOrAfterDateTime);

		return subjectConfirmationData;
	}

	protected void verifyAssertionSignature(
			Signature signature, MessageContext<?> messageContext,
			TrustEngine<Signature> trustEngine)
		throws PortalException {

		SAMLSelfEntityContext samlSelfEntityContext =
			messageContext.getSubcontext(SAMLSelfEntityContext.class);

		SAMLMetadataContext samlSelfMetadataContext =
			samlSelfEntityContext.getSubcontext(SAMLMetadataContext.class);

		SPSSODescriptor spSSODescriptor =
			(SPSSODescriptor)samlSelfMetadataContext.getRoleDescriptor();

		if (signature != null) {
			_verifySignature(messageContext, signature, trustEngine);
		}
		else if (spSSODescriptor.getWantAssertionsSigned()) {
			throw new SignatureException("SAML assertion is not signed");
		}
	}

	protected void verifyAudienceRestrictions(
			List<AudienceRestriction> audienceRestrictions,
			MessageContext<?> messageContext)
		throws PortalException {

		if (audienceRestrictions.isEmpty()) {
			return;
		}

		SAMLSelfEntityContext samlSelfEntityContext =
			messageContext.getSubcontext(SAMLSelfEntityContext.class);

		for (AudienceRestriction audienceRestriction : audienceRestrictions) {
			for (Audience audience : audienceRestriction.getAudiences()) {
				String audienceURI = audience.getAudienceURI();

				if (audienceURI.equals(samlSelfEntityContext.getEntityId())) {
					return;
				}
			}
		}

		throw new AudienceException("Unable verify audience");
	}

	protected void verifyConditions(
			MessageContext<?> messageContext, Conditions conditions)
		throws PortalException {

		verifyAudienceRestrictions(
			conditions.getAudienceRestrictions(), messageContext);

		DateTime nowDateTime = new DateTime(DateTimeZone.UTC);

		DateTime notBeforeDateTime = conditions.getNotBefore();

		if (notBeforeDateTime != null) {
			verifyNotBeforeDateTime(
				nowDateTime, _getClockSkew(), notBeforeDateTime);
		}

		DateTime notOnOrAfterDateTime = conditions.getNotOnOrAfter();

		if (notOnOrAfterDateTime != null) {
			verifyNotOnOrAfterDateTime(
				nowDateTime, _getClockSkew(), notOnOrAfterDateTime);
		}
	}

	protected void verifyDestination(
			MessageContext<?> messageContext, String destination)
		throws PortalException {

		SAMLSelfEntityContext samlSelfEntityContext =
			messageContext.getSubcontext(SAMLSelfEntityContext.class);

		SAMLMetadataContext samlSelfMetadataContext =
			samlSelfEntityContext.getSubcontext(SAMLMetadataContext.class);

		SPSSODescriptor spSSODescriptor =
			(SPSSODescriptor)samlSelfMetadataContext.getRoleDescriptor();

		List<AssertionConsumerService> assertionConsumerServices =
			spSSODescriptor.getAssertionConsumerServices();

		SAMLBindingContext samlBindingContext = messageContext.getSubcontext(
			SAMLBindingContext.class);

		for (AssertionConsumerService assertionConsumerService :
				assertionConsumerServices) {

			String binding = assertionConsumerService.getBinding();

			if (destination.equals(assertionConsumerService.getLocation()) &&
				binding.equals(samlBindingContext.getBindingUri())) {

				return;
			}
		}

		throw new DestinationException(
			StringBundler.concat(
				"Destination ", destination, " does not match any assertion ",
				"consumer location with binding ",
				samlBindingContext.getBindingUri()));
	}

	protected String verifyInResponseTo(Response samlResponse)
		throws PortalException {

		if (Validator.isNull(samlResponse.getInResponseTo())) {
			return null;
		}

		Issuer issuer = samlResponse.getIssuer();

		String issuerEntityId = issuer.getValue();

		String inResponseTo = samlResponse.getInResponseTo();

		SamlSpAuthRequest samlSpAuthRequest =
			_samlSpAuthRequestLocalService.fetchSamlSpAuthRequest(
				issuerEntityId, inResponseTo);

		if (samlSpAuthRequest != null) {
			_samlSpAuthRequestLocalService.deleteSamlSpAuthRequest(
				samlSpAuthRequest);

			return samlSpAuthRequest.getSamlRelayState();
		}

		throw new InResponseToException(
			StringBundler.concat(
				"Response in response to ", inResponseTo,
				" does not match any authentication requests"));
	}

	protected void verifyIssuer(MessageContext<?> messageContext, Issuer issuer)
		throws PortalException {

		String issuerFormat = issuer.getFormat();

		if ((issuerFormat != null) && !issuerFormat.equals(NameIDType.ENTITY)) {
			throw new IssuerException("Invalid issuer format " + issuerFormat);
		}

		SAMLPeerEntityContext samlPeerEntityContext =
			messageContext.getSubcontext(SAMLPeerEntityContext.class);

		String peerEntityId = samlPeerEntityContext.getEntityId();

		if (!peerEntityId.equals(issuer.getValue())) {
			throw new IssuerException(
				"Issuer does not match expected peer entity ID " +
					peerEntityId);
		}
	}

	protected void verifyNotBeforeDateTime(
			DateTime nowDateTime, long clockSkew, DateTime dateTime)
		throws PortalException {

		DateTime lowerBoundDateTime = dateTime.minus(new Duration(clockSkew));

		if (!nowDateTime.isBefore(lowerBoundDateTime)) {
			return;
		}

		throw new AssertionException(
			StringBundler.concat(
				"Date ", nowDateTime, " is before ", lowerBoundDateTime,
				" including clock skew ", clockSkew));
	}

	protected void verifyNotOnOrAfterDateTime(
			DateTime nowDateTime, long clockSkew, DateTime dateTime)
		throws PortalException {

		DateTime upperBoundDateTime = dateTime.plus(new Duration(clockSkew));

		if (!(nowDateTime.isEqual(upperBoundDateTime) ||
			  nowDateTime.isAfter(upperBoundDateTime))) {

			return;
		}

		throw new ExpiredException(
			StringBundler.concat(
				"Date ", nowDateTime, " is after ", upperBoundDateTime,
				" including clock skew ", clockSkew));
	}

	protected void verifyReplay(
			MessageContext<?> messageContext, Assertion assertion)
		throws PortalException {

		Issuer issuer = assertion.getIssuer();

		String idpEntityId = issuer.getValue();

		String messageKey = assertion.getID();

		DateTime notOnOrAfterDateTime = new DateTime(DateTimeZone.UTC);

		notOnOrAfterDateTime = notOnOrAfterDateTime.plus(
			_samlConfiguration.getReplayChacheDuration() + _getClockSkew());

		try {
			SamlSpMessage samlSpMessage =
				_samlSpMessageLocalService.fetchSamlSpMessage(
					idpEntityId, messageKey);

			if ((samlSpMessage != null) && !samlSpMessage.isExpired()) {
				throw new ReplayException(
					StringBundler.concat(
						"SAML assertion ", messageKey, " replayed from IdP ",
						idpEntityId));
			}

			if (samlSpMessage != null) {
				samlSpMessage.setCreateDate(new Date());
				samlSpMessage.setExpirationDate(notOnOrAfterDateTime.toDate());

				_samlSpMessageLocalService.updateSamlSpMessage(samlSpMessage);
			}
			else {
				ServiceContext serviceContext = new ServiceContext();

				serviceContext.setCompanyId(CompanyThreadLocal.getCompanyId());

				_samlSpMessageLocalService.addSamlSpMessage(
					idpEntityId, notOnOrAfterDateTime.toDate(), messageKey,
					serviceContext);
			}
		}
		catch (SystemException systemException) {
			throw new SamlException(systemException);
		}
	}

	protected void verifySubject(
			MessageContext<?> messageContext, Subject subject)
		throws PortalException {

		List<SubjectConfirmation> subjectConfirmations =
			subject.getSubjectConfirmations();

		for (SubjectConfirmation subjectConfirmation : subjectConfirmations) {
			String method = subjectConfirmation.getMethod();

			if (!method.equals(SubjectConfirmation.METHOD_BEARER)) {
				continue;
			}

			SubjectConfirmationData subjectConfirmationData =
				subjectConfirmation.getSubjectConfirmationData();

			if (subjectConfirmationData == null) {
				continue;
			}

			DateTime nowDateTime = new DateTime(DateTimeZone.UTC);
			long clockSkew = _getClockSkew();

			DateTime notBeforeDateTime = subjectConfirmationData.getNotBefore();

			if (notBeforeDateTime != null) {
				verifyNotBeforeDateTime(
					nowDateTime, clockSkew, notBeforeDateTime);
			}

			DateTime notOnOrAfterDateTime =
				subjectConfirmationData.getNotOnOrAfter();

			if (notOnOrAfterDateTime != null) {
				verifyNotOnOrAfterDateTime(
					nowDateTime, clockSkew, notOnOrAfterDateTime);
			}

			if (Validator.isNull(subjectConfirmationData.getRecipient())) {
				continue;
			}

			verifyDestination(
				messageContext, subjectConfirmationData.getRecipient());

			NameID nameID = subject.getNameID();

			if (Validator.isNull(nameID.getValue())) {
				continue;
			}

			SAMLSubjectNameIdentifierContext samlSubjectNameIdentifierContext =
				messageContext.getSubcontext(
					SAMLSubjectNameIdentifierContext.class);

			samlSubjectNameIdentifierContext.setSubjectNameIdentifier(nameID);

			return;
		}

		throw new SubjectException("Unable to verify subject");
	}

	private void _addSamlSsoSession(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			SamlSsoRequestContext samlSsoRequestContext, NameID nameID)
		throws Exception {

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			httpServletRequest);

		SamlIdpSsoSession samlIdpSsoSession =
			_samlIdpSsoSessionLocalService.addSamlIdpSsoSession(
				samlSsoRequestContext.getSamlSsoSessionId(), serviceContext);

		MessageContext<?> messageContext =
			samlSsoRequestContext.getSAMLMessageContext();

		SAMLPeerEntityContext samlPeerEntityContext =
			messageContext.getSubcontext(SAMLPeerEntityContext.class);

		_samlIdpSpSessionLocalService.addSamlIdpSpSession(
			samlIdpSsoSession.getSamlIdpSsoSessionId(),
			samlPeerEntityContext.getEntityId(), nameID.getFormat(),
			nameID.getValue(), serviceContext);

		addNonpersistentCookie(
			httpServletRequest, httpServletResponse,
			SamlWebKeys.SAML_SSO_SESSION_ID,
			samlSsoRequestContext.getSamlSsoSessionId());

		HttpSession httpSession = httpServletRequest.getSession();

		httpSession.setAttribute(
			SamlWebKeys.SAML_SSO_SESSION_ID,
			samlSsoRequestContext.getSamlSsoSessionId());
	}

	private Decrypter _createDecrypter() {
		DecryptionParametersResolver decryptionParametersResolver =
			new BasicDecryptionParametersResolver();

		try {
			DecryptionParameters decryptionParameters =
				decryptionParametersResolver.resolveSingle(
					new CriteriaSet(
						new DecryptionConfigurationCriterion(
							SecurityConfigurationSupport.
								getGlobalDecryptionConfiguration())));

			if (decryptionParameters == null) {
				throw new RuntimeException(
					"Unable to resolve decryption parameters from the " +
						"configuration");
			}

			decryptionParameters.setKEKKeyInfoCredentialResolver(
				new DefaultKeyInfoCredentialResolver());

			Decrypter decrypter = new CustomParserPoolDecrypter(
				decryptionParameters);

			decrypter.setRootInNewDocument(true);

			return decrypter;
		}
		catch (ResolverException resolverException) {
			_log.error(resolverException);
		}

		return null;
	}

	private SAMLMetadataEncryptionParametersResolver
		_createSAMLMetadataEncryptionParametersResolver() {

		SAMLMetadataEncryptionParametersResolver
			samlMetadataEncryptionParametersResolver =
				new SAMLMetadataEncryptionParametersResolver(
					getMetadataCredentialResolver());

		samlMetadataEncryptionParametersResolver.
			setAutoGenerateDataEncryptionCredential(true);

		return samlMetadataEncryptionParametersResolver;
	}

	private SamlSsoRequestContext _decodeAuthnConversationAfterLogin(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		SamlSsoRequestContext samlSsoRequestContext = null;

		HttpSession httpSession = httpServletRequest.getSession();

		Map<String, SamlSsoRequestContext> samlSsoRequestContexts =
			(Map<String, SamlSsoRequestContext>)httpSession.getAttribute(
				SamlWebKeys.SAML_SSO_REQUEST_CONTEXT);

		if (samlSsoRequestContexts != null) {
			samlSsoRequestContext = samlSsoRequestContexts.remove(
				ParamUtil.getString(httpServletRequest, "samlMessageId"));
		}

		if (samlSsoRequestContext != null) {
			MessageContext<?> messageContext = getMessageContext(
				httpServletRequest, httpServletResponse,
				samlSsoRequestContext.getPeerEntityId());

			samlSsoRequestContext.setSAMLMessageContext(messageContext);

			String authnRequestXml = samlSsoRequestContext.getAuthnRequestXml();

			if (Validator.isNotNull(authnRequestXml)) {
				AuthnRequest authnRequest =
					(AuthnRequest)OpenSamlUtil.unmarshall(authnRequestXml);

				InOutOperationContext<AuthnRequest, ?> inOutOperationContext =
					new InOutOperationContext(
						new MessageContext(), new MessageContext());

				messageContext.addSubcontext(inOutOperationContext);

				MessageContext<AuthnRequest> inboundMessageContext =
					inOutOperationContext.getInboundMessageContext();

				inboundMessageContext.setMessage(authnRequest);

				SAMLMessageInfoContext samlInboundMessageInfoContext =
					inboundMessageContext.getSubcontext(
						SAMLMessageInfoContext.class, true);

				samlInboundMessageInfoContext.setMessageId(
					authnRequest.getID());
			}

			String relayState = samlSsoRequestContext.getRelayState();

			SAMLBindingContext samlBindingContext =
				messageContext.getSubcontext(SAMLBindingContext.class, true);

			samlBindingContext.setRelayState(relayState);

			String samlSsoSessionId = getSamlSsoSessionId(httpServletRequest);

			if (Validator.isNotNull(samlSsoSessionId)) {
				samlSsoRequestContext.setSamlSsoSessionId(samlSsoSessionId);
			}
			else {
				samlSsoRequestContext.setNewSession(true);
				samlSsoRequestContext.setSamlSsoSessionId(
					generateIdentifier(30));
			}

			samlSsoRequestContext.setStage(
				SamlSsoRequestContext.STAGE_AUTHENTICATED);
			samlSsoRequestContext.setUserId(
				portal.getUserId(httpServletRequest));

			return samlSsoRequestContext;
		}

		return null;
	}

	private MessageContext<?> _decodeAuthnResponse(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, SamlBinding samlBinding)
		throws Exception {

		MessageContext<?> messageContext = decodeSamlMessage(
			httpServletRequest, httpServletResponse, samlBinding, true);

		InOutOperationContext<Response, ?> inOutOperationContext =
			messageContext.getSubcontext(InOutOperationContext.class);

		MessageContext<Response> inboundMessageContext =
			inOutOperationContext.getInboundMessageContext();

		Response samlResponse = inboundMessageContext.getMessage();

		List<EncryptedAssertion> encryptedAssertions =
			samlResponse.getEncryptedAssertions();

		List<Assertion> assertions = new ArrayList<>(
			samlResponse.getAssertions());

		Decrypter decrypter = _decrypterDCLSingleton.getSingleton(
			this::_createDecrypter);

		if (decrypter == null) {
			if (!encryptedAssertions.isEmpty()) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Message returned encrypted assertions but there is " +
							"no decrypter available");
				}
			}
		}
		else {
			for (EncryptedAssertion encryptedAssertion : encryptedAssertions) {
				try {
					assertions.add(decrypter.decrypt(encryptedAssertion));
				}
				catch (DecryptionException decryptionException) {
					_log.error(
						"Unable to assertion decryption", decryptionException);
				}
			}

			inboundMessageContext.addSubcontext(
				new DecrypterContext(decrypter));
		}

		SignatureTrustEngine signatureTrustEngine = getSignatureTrustEngine();

		Assertion assertion = null;

		for (Assertion curAssertion : assertions) {
			try {
				_verifyAssertion(
					curAssertion, messageContext, signatureTrustEngine);
			}
			catch (SamlException samlException) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Rejecting assertion " + curAssertion.getID(),
						samlException);
				}

				continue;
			}

			List<AuthnStatement> authnStatements =
				curAssertion.getAuthnStatements();

			if (!authnStatements.isEmpty()) {
				Subject subject = curAssertion.getSubject();

				if ((subject != null) &&
					(subject.getSubjectConfirmations() != null)) {

					for (SubjectConfirmation subjectConfirmation :
							subject.getSubjectConfirmations()) {

						if (SubjectConfirmation.METHOD_BEARER.equals(
								subjectConfirmation.getMethod())) {

							assertion = curAssertion;

							break;
						}
					}
				}
			}
		}

		if (assertion == null) {
			throw new AssertionException(
				"Response does not contain any acceptable assertions");
		}

		inboundMessageContext.addSubcontext(
			new SubjectAssertionContext(assertion));

		return messageContext;
	}

	private String _fetchSamlIdpSPNameIdFormat(
		long companyId, String entityId) {

		try {
			SamlIdpSpConnection samlIdpSpConnection =
				samlIdpSpConnectionLocalService.getSamlIdpSpConnection(
					companyId, entityId);

			return samlIdpSpConnection.getNameIdFormat();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	private String _fetchSamlSpIdpNameIdFormat(
		long companyId, String entityId) {

		try {
			SamlSpIdpConnection samlSpIdpConnection =
				samlSpIdpConnectionLocalService.getSamlSpIdpConnection(
					companyId, entityId);

			return samlSpIdpConnection.getNameIdFormat();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	private int _getAssertionLifetime(String entityId) {
		long companyId = CompanyThreadLocal.getCompanyId();

		try {
			SamlIdpSpConnection samlIdpSpConnection =
				samlIdpSpConnectionLocalService.getSamlIdpSpConnection(
					companyId, entityId);

			return samlIdpSpConnection.getAssertionLifetime();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		SamlProviderConfiguration samlProviderConfiguration =
			samlProviderConfigurationHelper.getSamlProviderConfiguration();

		return samlProviderConfiguration.defaultAssertionLifetime();
	}

	private AttributeResolver _getAttributeResolver(String entityId) {
		long companyId = CompanyThreadLocal.getCompanyId();

		AttributeResolver attributeResolver =
			_stringAttributeResolverServiceTrackerMap.getService(
				companyId + "," + entityId);

		if (attributeResolver == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"No attribute resolver for company ID ", companyId,
						" and entity ID ", entityId));
			}

			attributeResolver = _defaultAttributeResolver;
		}

		return attributeResolver;
	}

	private String _getAuthRedirectURL(
			HttpServletRequest httpServletRequest, String redirect)
		throws Exception {

		StringBundler sb = new StringBundler(3);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		sb.append(themeDisplay.getPathMain());

		sb.append("/portal/saml/auth_redirect?redirect=");

		if (Validator.isNull(redirect)) {
			redirect = portal.getHomeURL(httpServletRequest);
		}

		sb.append(URLCodec.encodeURL(redirect));

		return sb.toString();
	}

	private long _getClockSkew() {
		SamlProviderConfiguration samlProviderConfiguration =
			samlProviderConfigurationHelper.getSamlProviderConfiguration();

		return samlProviderConfiguration.clockSkew();
	}

	private Credential _getEncryptionCredential() throws SamlException {
		try {
			String entityId = localEntityManager.getLocalEntityId();

			if (Validator.isNull(entityId)) {
				return null;
			}

			return credentialResolver.resolveSingle(
				new CriteriaSet(
					new EntityIdCriterion(entityId),
					new UsageCriterion(UsageType.ENCRYPTION)));
		}
		catch (ResolverException resolverException) {
			throw new SamlException(resolverException);
		}
	}

	private String _getNameIdFormat(String entityId) {
		long companyId = CompanyThreadLocal.getCompanyId();

		if (samlProviderConfigurationHelper.isRoleIb()) {
			String nameIdFormat = _fetchSamlIdpSPNameIdFormat(
				companyId, entityId);

			if (Validator.isNotNull(nameIdFormat)) {
				return nameIdFormat;
			}

			return _fetchSamlSpIdpNameIdFormat(companyId, entityId);
		}
		else if (samlProviderConfigurationHelper.isRoleIdp()) {
			return _fetchSamlIdpSPNameIdFormat(companyId, entityId);
		}
		else if (samlProviderConfigurationHelper.isRoleSp()) {
			return _fetchSamlSpIdpNameIdFormat(companyId, entityId);
		}

		return null;
	}

	private NameIdResolver _getNameIdResolver(String entityId) {
		long companyId = CompanyThreadLocal.getCompanyId();

		NameIdResolver nameIdResolver =
			_stringNameIdResolverServiceTrackerMap.getService(
				companyId + "," + entityId);

		if (nameIdResolver == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"No attribute resolver for company ID ", companyId,
						" and entity ID ", entityId));
			}

			nameIdResolver = _defaultNameIdResolver;
		}

		return nameIdResolver;
	}

	private Assertion _getSuccessAssertion(
		SamlSsoRequestContext samlSsoRequestContext,
		AssertionConsumerService assertionConsumerService, NameID nameID) {

		MessageContext<AuthnRequest> messageContext =
			(MessageContext<AuthnRequest>)
				samlSsoRequestContext.getSAMLMessageContext();

		Assertion assertion = OpenSamlUtil.buildAssertion();

		DateTime issueInstantDateTime = new DateTime(DateTimeZone.UTC);

		SubjectConfirmationData subjectConfirmationData =
			getSuccessSubjectConfirmationData(
				samlSsoRequestContext, assertionConsumerService,
				issueInstantDateTime);

		assertion.setConditions(
			getSuccessConditions(
				samlSsoRequestContext, issueInstantDateTime,
				subjectConfirmationData.getNotOnOrAfter()));

		assertion.setID(generateIdentifier(20));
		assertion.setIssueInstant(issueInstantDateTime);

		SAMLSelfEntityContext samlSelfEntityContext =
			messageContext.getSubcontext(SAMLSelfEntityContext.class);

		assertion.setIssuer(
			OpenSamlUtil.buildIssuer(samlSelfEntityContext.getEntityId()));

		assertion.setSubject(
			getSuccessSubject(nameID, subjectConfirmationData));
		assertion.setVersion(SAMLVersion.VERSION_20);

		List<AuthnStatement> authnStatements = assertion.getAuthnStatements();

		authnStatements.add(
			_getSuccessAuthnStatement(samlSsoRequestContext, assertion));

		SAMLPeerEntityContext samlPeerEntityContext =
			messageContext.getSubcontext(SAMLPeerEntityContext.class);

		boolean attributesEnabled = _isAttributesEnabled(
			samlPeerEntityContext.getEntityId());

		if (!attributesEnabled) {
			return assertion;
		}

		User user = _userLocalService.fetchUser(
			samlSsoRequestContext.getUserId());

		AttributeResolver attributeResolver = _getAttributeResolver(
			samlPeerEntityContext.getEntityId());

		AttributePublisherImpl attributePublisherImpl =
			new AttributePublisherImpl();

		attributeResolver.resolve(
			user, new AttributeResolverSAMLContextImpl(messageContext),
			attributePublisherImpl);

		List<Attribute> attributes = attributePublisherImpl.getAttributes();

		if (attributes.isEmpty()) {
			return assertion;
		}

		List<AttributeStatement> attributeStatements =
			assertion.getAttributeStatements();

		AttributeStatement attributeStatement =
			OpenSamlUtil.buildAttributeStatement();

		attributeStatements.add(attributeStatement);

		List<Attribute> attributeStatementAttributes =
			attributeStatement.getAttributes();

		attributeStatementAttributes.addAll(attributes);

		return assertion;
	}

	private AuthnContext _getSuccessAuthnContext() {
		AuthnContext authnContext = OpenSamlUtil.buildAuthnContext();

		AuthnContextClassRef authnContextClassRef =
			OpenSamlUtil.buildAuthnContextClassRef();

		authnContextClassRef.setAuthnContextClassRef(
			AuthnContext.UNSPECIFIED_AUTHN_CTX);

		authnContext.setAuthnContextClassRef(authnContextClassRef);

		return authnContext;
	}

	private AuthnStatement _getSuccessAuthnStatement(
		SamlSsoRequestContext samlSsoRequestContext, Assertion assertion) {

		AuthnStatement authnStatement = OpenSamlUtil.buildAuthnStatement();

		authnStatement.setAuthnContext(_getSuccessAuthnContext());

		authnStatement.setAuthnInstant(assertion.getIssueInstant());
		authnStatement.setSessionIndex(
			samlSsoRequestContext.getSamlSsoSessionId());

		return authnStatement;
	}

	private NameID _getSuccessNameId(
			SamlSsoRequestContext samlSsoRequestContext)
		throws Exception {

		String nameIdFormat = null;
		String spNameQualifier = null;

		MessageContext<AuthnRequest> messageContext =
			(MessageContext<AuthnRequest>)
				samlSsoRequestContext.getSAMLMessageContext();

		SAMLPeerEntityContext samlPeerEntityContext =
			messageContext.getSubcontext(SAMLPeerEntityContext.class);

		NameIdResolver nameIdResolver = _getNameIdResolver(
			samlPeerEntityContext.getEntityId());

		boolean allowCreate = false;

		AuthnRequest authnRequest = SamlUtil.getAuthnRequest(messageContext);

		if (authnRequest != null) {
			NameIDPolicy nameIDPolicy = authnRequest.getNameIDPolicy();

			if (nameIDPolicy != null) {
				nameIdFormat = nameIDPolicy.getFormat();
				spNameQualifier = nameIDPolicy.getSPNameQualifier();
				allowCreate = nameIDPolicy.getAllowCreate();
			}
		}

		if (nameIdFormat == null) {
			nameIdFormat = _getNameIdFormat(
				samlPeerEntityContext.getEntityId());
		}

		return OpenSamlUtil.buildNameId(
			nameIdFormat, null, spNameQualifier,
			nameIdResolver.resolve(
				_userLocalService.fetchUser(samlSsoRequestContext.getUserId()),
				samlPeerEntityContext.getEntityId(), nameIdFormat,
				spNameQualifier, allowCreate,
				new NameIdResolverSAMLContextImpl(messageContext)));
	}

	private Response _getSuccessResponse(
		SamlSsoRequestContext samlSsoRequestContext,
		AssertionConsumerService assertionConsumerService,
		DateTime issueInstant) {

		Response response = OpenSamlUtil.buildResponse();

		response.setDestination(assertionConsumerService.getLocation());
		response.setID(generateIdentifier(20));

		MessageContext<?> messageContext =
			samlSsoRequestContext.getSAMLMessageContext();

		InOutOperationContext<?, ?> inOutOperationContext =
			messageContext.getSubcontext(InOutOperationContext.class, false);

		if (inOutOperationContext != null) {
			MessageContext<?> inboundMessageContext =
				inOutOperationContext.getInboundMessageContext();

			SAMLMessageInfoContext samlMessageInfoContext =
				inboundMessageContext.getSubcontext(
					SAMLMessageInfoContext.class);

			if (Validator.isNotNull(samlMessageInfoContext.getMessageId())) {
				response.setInResponseTo(samlMessageInfoContext.getMessageId());
			}
		}

		response.setIssueInstant(issueInstant);

		SAMLSelfEntityContext samlSelfEntityContext =
			messageContext.getSubcontext(SAMLSelfEntityContext.class);

		response.setIssuer(
			OpenSamlUtil.buildIssuer(samlSelfEntityContext.getEntityId()));

		StatusCode statusCode = OpenSamlUtil.buildStatusCode(
			StatusCode.SUCCESS);

		response.setStatus(OpenSamlUtil.buildStatus(statusCode));

		response.setVersion(SAMLVersion.VERSION_20);

		return response;
	}

	private boolean _isAttributesEnabled(String entityId) {
		long companyId = CompanyThreadLocal.getCompanyId();

		try {
			SamlIdpSpConnection samlIdpSpConnection =
				samlIdpSpConnectionLocalService.getSamlIdpSpConnection(
					companyId, entityId);

			return samlIdpSpConnection.isAttributesEnabled();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return false;
	}

	private void _processAuthnRequest(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		SamlSsoRequestContext samlSsoRequestContext = decodeAuthnRequest(
			httpServletRequest, httpServletResponse);

		MessageContext<?> messageContext =
			samlSsoRequestContext.getSAMLMessageContext();

		InOutOperationContext<AuthnRequest, ?> inOutOperationContext =
			messageContext.getSubcontext(InOutOperationContext.class, false);

		AuthnRequest authnRequest = null;
		User user = _userLocalService.fetchUser(
			samlSsoRequestContext.getUserId());

		if (inOutOperationContext != null) {
			MessageContext<AuthnRequest> inboundMessageContext =
				inOutOperationContext.getInboundMessageContext();

			authnRequest = inboundMessageContext.getMessage();

			if ((authnRequest != null) && authnRequest.isPassive() &&
				(user == null)) {

				SamlProviderConfiguration samlProviderConfiguration =
					samlProviderConfigurationHelper.
						getSamlProviderConfiguration();

				_sendFailureResponse(
					samlProviderConfiguration.
						authnRequestSigningAllowsDynamicACSURL(),
					httpServletResponse, samlSsoRequestContext,
					StatusCode.NO_PASSIVE);

				return;
			}
		}

		boolean sessionExpired = false;

		if (!samlSsoRequestContext.isNewSession()) {
			String samlSsoSessionId =
				samlSsoRequestContext.getSamlSsoSessionId();

			SamlIdpSsoSession samlIdpSsoSession =
				_samlIdpSsoSessionLocalService.fetchSamlIdpSso(
					samlSsoSessionId);

			if (samlIdpSsoSession != null) {
				sessionExpired = samlIdpSsoSession.isExpired();
			}
			else {
				samlSsoSessionId = null;

				samlSsoRequestContext.setSamlSsoSessionId(null);
			}

			if (sessionExpired || Validator.isNull(samlSsoSessionId)) {
				CookiesManagerUtil.deleteCookies(
					CookiesManagerUtil.getDomain(httpServletRequest),
					httpServletRequest, httpServletResponse,
					SamlWebKeys.SAML_SSO_SESSION_ID);

				samlSsoRequestContext.setNewSession(true);
				samlSsoRequestContext.setSamlSsoSessionId(
					generateIdentifier(30));
			}
		}

		if (sessionExpired || (user == null) ||
			((authnRequest != null) && authnRequest.isForceAuthn() &&
			 (user != null) &&
			 (samlSsoRequestContext.getStage() ==
				 SamlSsoRequestContext.STAGE_INITIAL))) {

			boolean forceAuthn = false;

			if ((authnRequest != null) && authnRequest.isForceAuthn()) {
				forceAuthn = true;
			}

			_redirectToLogin(
				httpServletRequest, httpServletResponse, samlSsoRequestContext,
				forceAuthn);
		}
		else {
			SamlProviderConfiguration samlProviderConfiguration =
				samlProviderConfigurationHelper.getSamlProviderConfiguration();

			_sendSuccessResponse(
				samlProviderConfiguration.
					authnRequestSigningAllowsDynamicACSURL(),
				httpServletRequest, httpServletResponse, samlSsoRequestContext);

			HttpSession httpSession = httpServletRequest.getSession(false);

			if (httpSession != null) {
				httpSession.removeAttribute(SamlWebKeys.FORCE_REAUTHENTICATION);
			}
		}
	}

	private void _processResponse(
			MessageContext<?> messageContext,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		InOutOperationContext<Response, ?> inOutOperationContext =
			messageContext.getSubcontext(InOutOperationContext.class);

		MessageContext<Response> inboundMessageContext =
			inOutOperationContext.getInboundMessageContext();

		Response samlResponse = inboundMessageContext.getMessage();

		Status status = samlResponse.getStatus();

		StatusCode statusCode = status.getStatusCode();

		String statusCodeURI = statusCode.getValue();

		if (!statusCodeURI.equals(StatusCode.SUCCESS)) {
			StatusCode childStatusCode = statusCode.getStatusCode();

			if ((childStatusCode != null) &&
				Validator.isNotNull(childStatusCode.getValue())) {

				throw new StatusException(childStatusCode.getValue());
			}

			throw new StatusException(statusCodeURI);
		}

		String redirect = verifyInResponseTo(samlResponse);

		if (Validator.isNull(redirect)) {
			SAMLBindingContext samlBindingContext =
				messageContext.getSubcontext(SAMLBindingContext.class);

			redirect = portal.escapeRedirect(
				samlBindingContext.getRelayState());
		}

		httpServletRequest.setAttribute(WebKeys.REDIRECT, redirect);

		verifyDestination(messageContext, samlResponse.getDestination());

		Issuer issuer = samlResponse.getIssuer();

		verifyIssuer(messageContext, issuer);

		SAMLSubjectNameIdentifierContext samlSubjectNameIdentifierContext =
			messageContext.getSubcontext(
				SAMLSubjectNameIdentifierContext.class);

		NameID nameID =
			samlSubjectNameIdentifierContext.getSAML2SubjectNameID();

		if (nameID == null) {
			throw new SamlException("Name ID not present in subject");
		}

		if (_log.isDebugEnabled()) {
			_log.debug("SAML authenticated user " + nameID.getValue());
		}

		SAMLPeerEntityContext samlPeerEntityContext =
			messageContext.getSubcontext(SAMLPeerEntityContext.class);

		SamlSpIdpConnection samlSpIdpConnection =
			samlSpIdpConnectionLocalService.getSamlSpIdpConnection(
				CompanyThreadLocal.getCompanyId(),
				samlPeerEntityContext.getEntityId());

		if (Validator.isNull(samlResponse.getInResponseTo()) &&
			samlSpIdpConnection.isForceAuthn()) {

			throw new AuthnAgeException();
		}

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			httpServletRequest);

		serviceContext.setAttribute(
			"SamlIdpEntityId", samlSpIdpConnection.getSamlIdpEntityId());

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		User user = null;

		try {
			user = _userResolver.resolveUser(
				new UserResolverSAMLContextImpl(
					(MessageContext<Response>)messageContext),
				serviceContext);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		if (user == null) {
			throw new SubjectException(
				"No user could not be matched or provisioned");
		}

		serviceContext.setUserId(user.getUserId());

		SamlSpSession samlSpSession = getSamlSpSession(httpServletRequest);
		HttpSession httpSession = httpServletRequest.getSession();

		SubjectAssertionContext subjectAssertionContext =
			inboundMessageContext.getSubcontext(SubjectAssertionContext.class);

		Assertion assertion = subjectAssertionContext.getAssertion();

		List<AuthnStatement> authnStatements = assertion.getAuthnStatements();

		AuthnStatement authnStatement = authnStatements.get(0);

		String sessionIndex = authnStatement.getSessionIndex();

		if (samlSpSession != null) {
			samlSpSessionLocalService.updateSamlSpSession(
				samlSpSession.getSamlSpSessionId(),
				OpenSamlUtil.marshall(assertion), httpSession.getId(),
				nameID.getFormat(), nameID.getNameQualifier(),
				nameID.getSPNameQualifier(), nameID.getValue(),
				issuer.getValue(), samlSpSession.getSamlSpSessionKey(),
				sessionIndex, serviceContext);
		}
		else {
			String samlSpSessionKey = generateIdentifier(30);

			samlSpSession = samlSpSessionLocalService.addSamlSpSession(
				OpenSamlUtil.marshall(assertion), httpSession.getId(),
				nameID.getFormat(), nameID.getNameQualifier(),
				nameID.getSPNameQualifier(), nameID.getValue(),
				issuer.getValue(), samlSpSessionKey, sessionIndex,
				serviceContext);
		}

		httpSession.setAttribute(
			SamlWebKeys.SAML_SP_SESSION_KEY,
			samlSpSession.getSamlSpSessionKey());

		addNonpersistentCookie(
			httpServletRequest, httpServletResponse,
			SamlWebKeys.SAML_SP_SESSION_KEY,
			samlSpSession.getSamlSpSessionKey());

		httpServletResponse.sendRedirect(
			_getAuthRedirectURL(httpServletRequest, redirect));
	}

	private void _redirectToLogin(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse,
		SamlSsoRequestContext samlSsoRequestContext, boolean forceAuthn) {

		HttpSession httpSession = httpServletRequest.getSession();

		if (forceAuthn) {
			logout(httpServletRequest, httpServletResponse);

			httpSession = httpServletRequest.getSession(true);

			httpSession.setAttribute(
				SamlWebKeys.FORCE_REAUTHENTICATION, Boolean.TRUE);
		}

		MessageContext<?> samlMessageContext =
			samlSsoRequestContext.getSAMLMessageContext();

		samlSsoRequestContext.setSAMLMessageContext(null);

		httpServletResponse.addHeader(
			HttpHeaders.CACHE_CONTROL,
			HttpHeaders.CACHE_CONTROL_NO_CACHE_VALUE);
		httpServletResponse.addHeader(
			HttpHeaders.PRAGMA, HttpHeaders.PRAGMA_NO_CACHE_VALUE);

		StringBundler sb = new StringBundler(3);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		sb.append(themeDisplay.getPathMain());

		sb.append("/portal/login?redirect=");

		StringBundler redirectSB = new StringBundler(6);

		redirectSB.append(themeDisplay.getPathMain());
		redirectSB.append("/portal/saml/sso");

		SAMLPeerEntityContext samlPeerEntityContext =
			samlMessageContext.getSubcontext(SAMLPeerEntityContext.class);

		InOutOperationContext<?, ?> inOutOperationContext =
			samlMessageContext.getSubcontext(
				InOutOperationContext.class, false);

		if (inOutOperationContext != null) {
			MessageContext<?> inboundMessageContext =
				inOutOperationContext.getInboundMessageContext();

			SAMLMessageInfoContext samlMessageInfoContext =
				inboundMessageContext.getSubcontext(
					SAMLMessageInfoContext.class);

			if ((samlMessageInfoContext != null) &&
				(samlMessageInfoContext.getMessageId() != null)) {

				_saveSamlSsoRequestContext(
					httpSession, samlMessageInfoContext.getMessageId(),
					samlSsoRequestContext);

				redirectSB.append("?samlMessageId=");
				redirectSB.append(
					URLCodec.encodeURL(samlMessageInfoContext.getMessageId()));
			}
		}
		else if (samlPeerEntityContext.getEntityId() != null) {
			String samlMessageId = generateIdentifier(20);

			_saveSamlSsoRequestContext(
				httpSession, samlMessageId, samlSsoRequestContext);

			redirectSB.append("?entityId=");
			redirectSB.append(
				URLCodec.encodeURL(samlPeerEntityContext.getEntityId()));
			redirectSB.append("&samlMessageId=");
			redirectSB.append(samlMessageId);
		}

		sb.append(URLCodec.encodeURL(redirectSB.toString()));

		String redirect = sb.toString();

		try {
			httpServletResponse.sendRedirect(redirect);
		}
		catch (IOException ioException) {
			throw new SystemException(ioException);
		}
	}

	private void _saveSamlSsoRequestContext(
		HttpSession httpSession, String samlMessageId,
		SamlSsoRequestContext samlSsoRequestContext) {

		Map<String, SamlSsoRequestContext> samlSsoRequestContexts =
			(Map<String, SamlSsoRequestContext>)httpSession.getAttribute(
				SamlWebKeys.SAML_SSO_REQUEST_CONTEXT);

		if (samlSsoRequestContexts == null) {
			samlSsoRequestContexts = new LRUMap<>(
				_samlConfiguration.getMaxSamlSsoRequestContexts());

			httpSession.setAttribute(
				SamlWebKeys.SAML_SSO_REQUEST_CONTEXT, samlSsoRequestContexts);
		}

		samlSsoRequestContexts.put(samlMessageId, samlSsoRequestContext);
	}

	private void _sendFailureResponse(
			boolean dynamicACSURL, HttpServletResponse httpServletResponse,
			SamlSsoRequestContext samlSsoRequestContext, String statusURI)
		throws Exception {

		MessageContext<?> messageContext =
			samlSsoRequestContext.getSAMLMessageContext();

		SamlBinding samlBinding = samlBindingProvider.getSamlBinding(
			SAMLConstants.SAML2_POST_BINDING_URI);

		AssertionConsumerService assertionConsumerService =
			SamlUtil.resolverAssertionConsumerService(
				messageContext, samlBinding.getCommunicationProfileId(),
				dynamicACSURL);

		SAMLPeerEntityContext samlPeerEntityContext =
			messageContext.getSubcontext(SAMLPeerEntityContext.class);

		SAMLEndpointContext samlPeerEndpointContext =
			samlPeerEntityContext.getSubcontext(
				SAMLEndpointContext.class, true);

		samlPeerEndpointContext.setEndpoint(assertionConsumerService);

		samlPeerEntityContext.addSubcontext(samlPeerEndpointContext);

		Credential credential = getSigningCredential();

		InOutOperationContext<?, Response> inOutOperationContext =
			messageContext.getSubcontext(InOutOperationContext.class);

		MessageContext<Response> outboundMessageContext =
			inOutOperationContext.getOutboundMessageContext();

		outboundMessageContext.addSubcontext(samlPeerEntityContext);

		SecurityParametersContext securityParametersContext =
			outboundMessageContext.getSubcontext(
				SecurityParametersContext.class, true);

		SAMLMetadataContext samlPeerMetadataContext =
			samlPeerEntityContext.getSubcontext(SAMLMetadataContext.class);

		OpenSamlUtil.prepareSecurityParametersContext(
			credential, securityParametersContext,
			samlPeerMetadataContext.getRoleDescriptor());

		Response response = OpenSamlUtil.buildResponse();

		response.setDestination(assertionConsumerService.getLocation());
		response.setID(generateIdentifier(20));

		MessageContext<?> inboundMessageContext =
			inOutOperationContext.getInboundMessageContext();

		SAMLMessageInfoContext samlMessageInfoContext =
			inboundMessageContext.getSubcontext(SAMLMessageInfoContext.class);

		response.setInResponseTo(samlMessageInfoContext.getMessageId());

		DateTime issueInstantDateTime = new DateTime(DateTimeZone.UTC);

		response.setIssueInstant(issueInstantDateTime);

		SAMLSelfEntityContext samlSelfEntityContext =
			messageContext.getSubcontext(SAMLSelfEntityContext.class);

		response.setIssuer(
			OpenSamlUtil.buildIssuer(samlSelfEntityContext.getEntityId()));

		StatusCode statusCode = OpenSamlUtil.buildStatusCode(
			StatusCode.RESPONDER);

		statusCode.setStatusCode(OpenSamlUtil.buildStatusCode(statusURI));

		response.setStatus(OpenSamlUtil.buildStatus(statusCode));

		outboundMessageContext.setMessage(response);

		sendSamlMessage(messageContext, httpServletResponse);
	}

	private void _sendSuccessResponse(
			boolean dynamicACSURL, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			SamlSsoRequestContext samlSsoRequestContext)
		throws Exception {

		MessageContext<?> messageContext =
			samlSsoRequestContext.getSAMLMessageContext();

		SamlBinding samlBinding = samlBindingProvider.getSamlBinding(
			SAMLConstants.SAML2_POST_BINDING_URI);

		AssertionConsumerService assertionConsumerService =
			SamlUtil.resolverAssertionConsumerService(
				messageContext, samlBinding.getCommunicationProfileId(),
				dynamicACSURL);

		NameID nameID = _getSuccessNameId(samlSsoRequestContext);

		Assertion assertion = _getSuccessAssertion(
			samlSsoRequestContext, assertionConsumerService, nameID);

		Credential credential = getSigningCredential();

		SAMLPeerEntityContext samlPeerEntityContext =
			messageContext.getSubcontext(SAMLPeerEntityContext.class);

		SAMLMetadataContext samlPeerMetadataContext =
			samlPeerEntityContext.getSubcontext(SAMLMetadataContext.class);

		SPSSODescriptor spSSODescriptor =
			(SPSSODescriptor)samlPeerMetadataContext.getRoleDescriptor();

		if (spSSODescriptor.getWantAssertionsSigned()) {
			OpenSamlUtil.signObject(assertion, credential, spSSODescriptor);
		}

		Response samlResponse = _getSuccessResponse(
			samlSsoRequestContext, assertionConsumerService,
			assertion.getIssueInstant());

		SamlIdpSpConnection samlIdpSpConnection =
			samlIdpSpConnectionLocalService.getSamlIdpSpConnection(
				CompanyThreadLocal.getCompanyId(),
				samlPeerEntityContext.getEntityId());

		CriteriaSet criteriaSet = new CriteriaSet(
			new EncryptionConfigurationCriterion(
				ConfigurationServiceBootstrapUtil.get(
					EncryptionConfiguration.class)),
			new RoleDescriptorCriterion(spSSODescriptor));

		if (!samlIdpSpConnection.isEncryptionForced()) {
			criteriaSet.add(new EncryptionOptionalCriterion(true));
		}

		SAMLMetadataEncryptionParametersResolver
			samlMetadataEncryptionParametersResolver =
				_samlMetadataEncryptionParametersResolverDCLSingleton.
					getSingleton(
						this::_createSAMLMetadataEncryptionParametersResolver);

		EncryptionParameters encryptionParameters =
			samlMetadataEncryptionParametersResolver.resolveSingle(criteriaSet);

		if (encryptionParameters != null) {
			Encrypter encrypter = new Encrypter(
				new DataEncryptionParameters(encryptionParameters),
				new KeyEncryptionParameters(
					encryptionParameters, samlPeerEntityContext.getEntityId()));

			EncryptedAssertion encryptedAssertion = encrypter.encrypt(
				assertion);

			List<EncryptedAssertion> encryptedAssertions =
				samlResponse.getEncryptedAssertions();

			encryptedAssertions.add(encryptedAssertion);
		}
		else if (samlIdpSpConnection.isEncryptionForced()) {
			throw new SamlException(
				StringBundler.concat(
					"Encryption is forced for ",
					samlPeerEntityContext.getEntityId(),
					", but no encryption parameters have been successfully ",
					"negotiated"));
		}
		else {
			List<Assertion> assertions = samlResponse.getAssertions();

			assertions.add(assertion);
		}

		InOutOperationContext<?, Response> inOutOperationContext =
			messageContext.getSubcontext(InOutOperationContext.class, false);

		if (inOutOperationContext == null) {
			inOutOperationContext =
				(InOutOperationContext)messageContext.addSubcontext(
					new InOutOperationContext<>(
						new MessageContext<>(), new MessageContext<>()));
		}

		MessageContext<Response> outboundMessageContext =
			inOutOperationContext.getOutboundMessageContext();

		outboundMessageContext.addSubcontext(
			messageContext.getSubcontext(SAMLBindingContext.class, true));
		outboundMessageContext.setMessage(samlResponse);

		SecurityParametersContext securityParametersContext =
			outboundMessageContext.getSubcontext(
				SecurityParametersContext.class, true);

		OpenSamlUtil.prepareSecurityParametersContext(
			credential, securityParametersContext, spSSODescriptor);

		SAMLProtocolContext samlProtocolContext =
			outboundMessageContext.getSubcontext(
				SAMLProtocolContext.class, true);

		samlProtocolContext.setProtocol(SAMLConstants.SAML20P_NS);

		SAMLPeerEntityContext samlOutboundPeerEntityContext =
			outboundMessageContext.getSubcontext(
				SAMLPeerEntityContext.class, true);

		SAMLEndpointContext samlPeerEndpointContext =
			samlOutboundPeerEntityContext.getSubcontext(
				SAMLEndpointContext.class, true);

		samlPeerEndpointContext.setEndpoint(assertionConsumerService);

		if (samlSsoRequestContext.isNewSession()) {
			_addSamlSsoSession(
				httpServletRequest, httpServletResponse, samlSsoRequestContext,
				nameID);
		}
		else {
			_updateSamlSsoSession(
				httpServletRequest, samlSsoRequestContext, nameID);
		}

		sendSamlMessage(messageContext, httpServletResponse);
	}

	private void _updateSamlSsoSession(
			HttpServletRequest httpServletRequest,
			SamlSsoRequestContext samlSsoRequestContext, NameID nameID)
		throws Exception {

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			httpServletRequest);

		SamlIdpSsoSession samlIdpSsoSession =
			_samlIdpSsoSessionLocalService.updateModifiedDate(
				samlSsoRequestContext.getSamlSsoSessionId());

		MessageContext<?> messageContext =
			samlSsoRequestContext.getSAMLMessageContext();

		SAMLPeerEntityContext samlPeerEntityContext =
			messageContext.getSubcontext(SAMLPeerEntityContext.class);

		try {
			_samlIdpSpSessionLocalService.updateModifiedDate(
				samlIdpSsoSession.getSamlIdpSsoSessionId(),
				samlPeerEntityContext.getEntityId());
		}
		catch (NoSuchIdpSpSessionException noSuchIdpSpSessionException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchIdpSpSessionException);
			}

			_samlIdpSpSessionLocalService.addSamlIdpSpSession(
				samlIdpSsoSession.getSamlIdpSsoSessionId(),
				samlPeerEntityContext.getEntityId(), nameID.getFormat(),
				nameID.getValue(), serviceContext);
		}
	}

	private void _verifyAssertion(
			Assertion assertion, MessageContext<?> messageContext,
			TrustEngine<Signature> trustEngine)
		throws PortalException {

		verifyReplay(messageContext, assertion);
		verifyIssuer(messageContext, assertion.getIssuer());
		verifyAssertionSignature(
			assertion.getSignature(), messageContext, trustEngine);
		verifyConditions(messageContext, assertion.getConditions());
		verifySubject(messageContext, assertion.getSubject());
	}

	private void _verifySignature(
			MessageContext<?> messageContext, Signature signature,
			TrustEngine<Signature> trustEngine)
		throws PortalException {

		try {
			_samlSignatureProfileValidator.validate(signature);

			SAMLPeerEntityContext samlPeerEntityContext =
				messageContext.getSubcontext(SAMLPeerEntityContext.class);

			CriteriaSet criteriaSet = new CriteriaSet();

			criteriaSet.add(
				new EntityIdCriterion(samlPeerEntityContext.getEntityId()));
			criteriaSet.add(
				new EntityRoleCriterion(IDPSSODescriptor.DEFAULT_ELEMENT_NAME));
			criteriaSet.add(new ProtocolCriterion(SAMLConstants.SAML20P_NS));
			criteriaSet.add(new UsageCriterion(UsageType.SIGNING));

			if (!trustEngine.validate(signature, criteriaSet)) {
				throw new SignatureException("Unable validate signature trust");
			}
		}
		catch (Exception exception) {
			if (exception instanceof PortalException) {
				throw (PortalException)exception;
			}

			throw new SignatureException(
				"Unable to verify signature", exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WebSsoProfileImpl.class);

	private static final SAMLSignatureProfileValidator
		_samlSignatureProfileValidator = new SAMLSignatureProfileValidator();

	private final DCLSingleton<Decrypter> _decrypterDCLSingleton =
		new DCLSingleton<>();

	@Reference(
		policyOption = ReferencePolicyOption.GREEDY, target = "(!(companyId=*))"
	)
	private AttributeResolver _defaultAttributeResolver;

	@Reference(
		policyOption = ReferencePolicyOption.GREEDY, target = "(!(companyId=*))"
	)
	private NameIdResolver _defaultNameIdResolver;

	private SamlConfiguration _samlConfiguration;

	@Reference
	private SamlIdpSpSessionLocalService _samlIdpSpSessionLocalService;

	@Reference
	private SamlIdpSsoSessionLocalService _samlIdpSsoSessionLocalService;

	private final DCLSingleton<SAMLMetadataEncryptionParametersResolver>
		_samlMetadataEncryptionParametersResolverDCLSingleton =
			new DCLSingleton<>();

	@Reference
	private SamlSpAuthRequestLocalService _samlSpAuthRequestLocalService;

	@Reference
	private SamlSpMessageLocalService _samlSpMessageLocalService;

	private ServiceTrackerMap<String, AttributeResolver>
		_stringAttributeResolverServiceTrackerMap;
	private ServiceTrackerMap<String, NameIdResolver>
		_stringNameIdResolverServiceTrackerMap;

	@Reference
	private UserLocalService _userLocalService;

	@Reference(policyOption = ReferencePolicyOption.GREEDY)
	private UserResolver _userResolver;

	private class CustomParserPoolDecrypter extends Decrypter {

		public CustomParserPoolDecrypter(
			DecryptionParameters decryptionParameters) {

			super(decryptionParameters);
		}

		@Override
		protected ParserPool buildParserPool() {
			return ParserPoolUtil.getParserPool();
		}

	}

	private class DefaultKeyInfoCredentialResolver
		implements KeyInfoCredentialResolver {

		@Override
		public Iterable<Credential> resolve(CriteriaSet criteria)
			throws ResolverException {

			return Collections.singletonList(resolveSingle(criteria));
		}

		@Override
		public Credential resolveSingle(CriteriaSet criteria)
			throws ResolverException {

			try {
				return _getEncryptionCredential();
			}
			catch (SamlException samlException) {
				throw new ResolverException(samlException);
			}
		}

	}

}