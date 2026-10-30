/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.util;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

import java.net.URI;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistry;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallerFactory;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.schema.XSBase64Binary;
import org.opensaml.core.xml.schema.XSBoolean;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.core.xml.schema.XSDateTime;
import org.opensaml.core.xml.schema.XSInteger;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.core.xml.schema.XSURI;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.SignableSAMLObject;
import org.opensaml.saml.criterion.RoleDescriptorCriterion;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.AttributeValue;
import org.opensaml.saml.saml2.core.Audience;
import org.opensaml.saml.saml2.core.AudienceRestriction;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.opensaml.saml.saml2.core.LogoutResponse;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NameIDPolicy;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.SessionIndex;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SingleLogoutService;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
import org.opensaml.saml.security.impl.SAMLMetadataSignatureSigningParametersResolver;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.UsageType;
import org.opensaml.xmlsec.EncryptionConfiguration;
import org.opensaml.xmlsec.SignatureSigningConfiguration;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.context.SecurityParametersContext;
import org.opensaml.xmlsec.criterion.SignatureSigningConfigurationCriterion;
import org.opensaml.xmlsec.impl.BasicSignatureSigningConfiguration;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.keyinfo.KeyInfoGeneratorFactory;
import org.opensaml.xmlsec.keyinfo.KeyInfoGeneratorManager;
import org.opensaml.xmlsec.keyinfo.NamedKeyInfoGeneratorManager;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignatureSupport;
import org.opensaml.xmlsec.signature.support.Signer;

/**
 * @author Mika Koivisto
 */
public class OpenSamlUtil {

	public static Assertion buildAssertion() {
		SAMLObjectBuilder<Assertion> samlObjectBuilder =
			(SAMLObjectBuilder<Assertion>)_getBuilder(
				Assertion.DEFAULT_ELEMENT_NAME);

		Assertion assertion = samlObjectBuilder.buildObject();

		assertion.setVersion(SAMLVersion.VERSION_20);

		return assertion;
	}

	public static AssertionConsumerService buildAssertionConsumerService(
		String binding, int index, boolean isDefault, String location) {

		SAMLObjectBuilder<AssertionConsumerService> samlObjectBuilder =
			(SAMLObjectBuilder<AssertionConsumerService>)_getBuilder(
				AssertionConsumerService.DEFAULT_ELEMENT_NAME);

		AssertionConsumerService assertionConsumerService =
			samlObjectBuilder.buildObject();

		assertionConsumerService.setBinding(binding);
		assertionConsumerService.setIndex(Integer.valueOf(index));
		assertionConsumerService.setIsDefault(Boolean.valueOf(isDefault));
		assertionConsumerService.setLocation(location);

		return assertionConsumerService;
	}

	public static Attribute buildAttribute() {
		SAMLObjectBuilder<Attribute> samlObjectBuilder =
			(SAMLObjectBuilder<Attribute>)_getBuilder(
				Attribute.DEFAULT_ELEMENT_NAME);

		return samlObjectBuilder.buildObject();
	}

	public static Attribute buildAttribute(String name, Serializable value) {
		return buildAttribute(name, Attribute.UNSPECIFIED, value);
	}

	public static Attribute buildAttribute(
		String name, String nameFormat, Serializable value) {

		return buildAttribute(name, null, nameFormat, value);
	}

	public static Attribute buildAttribute(
		String name, String friendlyName, String nameFormat) {

		Attribute attribute = buildAttribute();

		if (Validator.isNotNull(friendlyName)) {
			attribute.setFriendlyName(friendlyName);
		}

		attribute.setName(name);
		attribute.setNameFormat(nameFormat);

		return attribute;
	}

	public static Attribute buildAttribute(
		String name, String friendlyName, String nameFormat, Boolean value) {

		Attribute attribute = buildAttribute(name, friendlyName, nameFormat);

		List<XMLObject> xmlObjects = attribute.getAttributeValues();

		XMLObject xmlObject = buildAttributeValue(value);

		xmlObjects.add(xmlObject);

		return attribute;
	}

	public static Attribute buildAttribute(
		String name, String friendlyName, String nameFormat, boolean[] values) {

		Attribute attribute = buildAttribute();

		if (Validator.isNotNull(friendlyName)) {
			attribute.setFriendlyName(friendlyName);
		}

		attribute.setName(name);
		attribute.setNameFormat(nameFormat);

		List<XMLObject> xmlObjects = attribute.getAttributeValues();

		for (boolean value : values) {
			XMLObject xmlObject = buildAttributeValue(Boolean.valueOf(value));

			xmlObjects.add(xmlObject);
		}

		return attribute;
	}

	public static Attribute buildAttribute(
		String name, String friendlyName, String nameFormat, Date value) {

		DateTime dateTime = new DateTime(value);

		dateTime = dateTime.withZone(DateTimeZone.UTC);

		return buildAttribute(name, friendlyName, nameFormat, dateTime);
	}

	public static Attribute buildAttribute(
		String name, String friendlyName, String nameFormat, Date[] values) {

		DateTime[] dateTimeValues = new DateTime[values.length];

		for (int i = 0; i < values.length; i++) {
			DateTime dateTime = new DateTime(values[i]);

			dateTime = dateTime.withZone(DateTimeZone.UTC);

			dateTimeValues[i] = dateTime;
		}

		return buildAttribute(name, friendlyName, nameFormat, dateTimeValues);
	}

	public static Attribute buildAttribute(
		String name, String friendlyName, String nameFormat, DateTime value) {

		Attribute attribute = buildAttribute();

		if (Validator.isNotNull(friendlyName)) {
			attribute.setFriendlyName(friendlyName);
		}

		attribute.setName(name);
		attribute.setNameFormat(nameFormat);

		XMLObject xmlObject = buildAttributeValue(value);

		List<XMLObject> xmlObjects = attribute.getAttributeValues();

		xmlObjects.add(xmlObject);

		return attribute;
	}

	public static Attribute buildAttribute(
		String name, String friendlyName, String nameFormat,
		DateTime[] values) {

		Attribute attribute = buildAttribute();

		if (Validator.isNotNull(friendlyName)) {
			attribute.setFriendlyName(friendlyName);
		}

		attribute.setName(name);
		attribute.setNameFormat(nameFormat);

		List<XMLObject> xmlObjects = attribute.getAttributeValues();

		for (DateTime value : values) {
			XMLObject xmlObject = buildAttributeValue(value);

			xmlObjects.add(xmlObject);
		}

		return attribute;
	}

	public static Attribute buildAttribute(
		String name, String friendlyName, String nameFormat, int[] values) {

		Attribute attribute = buildAttribute();

		if (Validator.isNotNull(friendlyName)) {
			attribute.setFriendlyName(friendlyName);
		}

		attribute.setName(name);
		attribute.setNameFormat(nameFormat);

		List<XMLObject> xmlObjects = attribute.getAttributeValues();

		for (int value : values) {
			XMLObject xmlObject = buildAttributeValue(Integer.valueOf(value));

			xmlObjects.add(xmlObject);
		}

		return attribute;
	}

	public static Attribute buildAttribute(
		String name, String friendlyName, String nameFormat,
		Serializable value) {

		if (value instanceof Boolean) {
			return buildAttribute(
				name, friendlyName, nameFormat, (Boolean)value);
		}
		else if (value instanceof boolean[]) {
			return buildAttribute(
				name, friendlyName, nameFormat, (boolean[])value);
		}
		else if (value instanceof Date) {
			return buildAttribute(name, friendlyName, nameFormat, (Date)value);
		}
		else if (value instanceof Date) {
			return buildAttribute(
				name, friendlyName, nameFormat, (Date[])value);
		}
		else if (value instanceof double[]) {
			double[] values = (double[])value;

			return buildAttribute(
				name, friendlyName, nameFormat,
				ArrayUtil.toStringArray(values));
		}
		else if (value instanceof float[]) {
			float[] values = (float[])value;

			return buildAttribute(
				name, friendlyName, nameFormat,
				ArrayUtil.toStringArray(values));
		}
		else if (value instanceof int[]) {
			return buildAttribute(name, friendlyName, nameFormat, (int[])value);
		}
		else if (value instanceof long[]) {
			long[] values = (long[])value;

			return buildAttribute(
				name, friendlyName, nameFormat,
				ArrayUtil.toStringArray(values));
		}
		else if (value instanceof Number[]) {
			Number[] values = (Number[])value;

			return buildAttribute(
				name, friendlyName, nameFormat,
				ArrayUtil.toStringArray(values));
		}
		else if (value instanceof short[]) {
			short[] values = (short[])value;

			return buildAttribute(
				name, friendlyName, nameFormat,
				ArrayUtil.toStringArray(values));
		}
		else if (value instanceof String[]) {
			return buildAttribute(
				name, friendlyName, nameFormat, (String[])value);
		}

		return buildAttribute(
			name, friendlyName, nameFormat, String.valueOf(value));
	}

	public static Attribute buildAttribute(
		String name, String friendlyName, String nameFormat, String value) {

		Attribute attribute = buildAttribute();

		if (Validator.isNotNull(friendlyName)) {
			attribute.setFriendlyName(friendlyName);
		}

		attribute.setName(name);
		attribute.setNameFormat(nameFormat);

		XMLObject xmlObject = buildAttributeValue(value);

		List<XMLObject> xmlObjects = attribute.getAttributeValues();

		xmlObjects.add(xmlObject);

		return attribute;
	}

	public static Attribute buildAttribute(
		String name, String friendlyName, String nameFormat, String[] values) {

		Attribute attribute = buildAttribute();

		if (Validator.isNotNull(friendlyName)) {
			attribute.setFriendlyName(friendlyName);
		}

		attribute.setName(name);
		attribute.setNameFormat(nameFormat);

		List<XMLObject> xmlObjects = attribute.getAttributeValues();

		for (String value : values) {
			XMLObject xmlObject = buildAttributeValue(value);

			xmlObjects.add(xmlObject);
		}

		return attribute;
	}

	public static XMLObject buildAttributeBase64Value(String value) {
		XMLObjectBuilder<XSBase64Binary> xmlObjectBuilder = _getBuilder(
			XSBase64Binary.TYPE_NAME);

		XSBase64Binary xsBase64Binary = xmlObjectBuilder.buildObject(
			AttributeValue.DEFAULT_ELEMENT_NAME, XSBase64Binary.TYPE_NAME);

		xsBase64Binary.setValue(value);

		return xsBase64Binary;
	}

	public static AttributeStatement buildAttributeStatement() {
		SAMLObjectBuilder<AttributeStatement> samlObjectBuilder =
			(SAMLObjectBuilder<AttributeStatement>)_getBuilder(
				AttributeStatement.DEFAULT_ELEMENT_NAME);

		return samlObjectBuilder.buildObject();
	}

	public static XMLObject buildAttributeValue(Boolean value) {
		XMLObjectBuilder<XSBoolean> xmlObjectBuilder = _getBuilder(
			XSBoolean.TYPE_NAME);

		XSBoolean xsBoolean = xmlObjectBuilder.buildObject(
			AttributeValue.DEFAULT_ELEMENT_NAME, XSBoolean.TYPE_NAME);

		XSBooleanValue xsBooleanValue = new XSBooleanValue(value, false);

		xsBoolean.setValue(xsBooleanValue);

		return xsBoolean;
	}

	public static XMLObject buildAttributeValue(DateTime value) {
		XMLObjectBuilder<XSDateTime> xmlObjectBuilder = _getBuilder(
			XSDateTime.TYPE_NAME);

		XSDateTime xsDateTime = xmlObjectBuilder.buildObject(
			AttributeValue.DEFAULT_ELEMENT_NAME, XSDateTime.TYPE_NAME);

		xsDateTime.setValue(value);

		return xsDateTime;
	}

	public static XMLObject buildAttributeValue(Integer value) {
		XMLObjectBuilder<XSInteger> xmlObjectBuilder = _getBuilder(
			XSInteger.TYPE_NAME);

		XSInteger xsInteger = xmlObjectBuilder.buildObject(
			AttributeValue.DEFAULT_ELEMENT_NAME, XSInteger.TYPE_NAME);

		xsInteger.setValue(value);

		return xsInteger;
	}

	public static XMLObject buildAttributeValue(String value) {
		XMLObjectBuilder<XSString> xmlObjectBuilder = _getBuilder(
			XSString.TYPE_NAME);

		XSString xsString = xmlObjectBuilder.buildObject(
			AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);

		xsString.setValue(value);

		return xsString;
	}

	public static XMLObject buildAttributeValue(URI value) {
		XMLObjectBuilder<XSURI> xmlObjectBuilder = _getBuilder(XSURI.TYPE_NAME);

		XSURI xsBase64Binary = xmlObjectBuilder.buildObject(
			AttributeValue.DEFAULT_ELEMENT_NAME, XSURI.TYPE_NAME);

		xsBase64Binary.setValue(value.toString());

		return xsBase64Binary;
	}

	public static Audience buildAudience() {
		SAMLObjectBuilder<Audience> samlObjectBuilder =
			(SAMLObjectBuilder<Audience>)_getBuilder(
				Audience.DEFAULT_ELEMENT_NAME);

		return samlObjectBuilder.buildObject();
	}

	public static AudienceRestriction buildAudienceRestriction() {
		SAMLObjectBuilder<AudienceRestriction> samlObjectBuilder =
			(SAMLObjectBuilder<AudienceRestriction>)_getBuilder(
				AudienceRestriction.DEFAULT_ELEMENT_NAME);

		return samlObjectBuilder.buildObject();
	}

	public static AuthnContext buildAuthnContext() {
		SAMLObjectBuilder<AuthnContext> samlObjectBuilder =
			(SAMLObjectBuilder<AuthnContext>)_getBuilder(
				AuthnContext.DEFAULT_ELEMENT_NAME);

		return samlObjectBuilder.buildObject();
	}

	public static AuthnContextClassRef buildAuthnContextClassRef() {
		SAMLObjectBuilder<AuthnContextClassRef> samlObjectBuilder =
			(SAMLObjectBuilder<AuthnContextClassRef>)_getBuilder(
				AuthnContextClassRef.DEFAULT_ELEMENT_NAME);

		return samlObjectBuilder.buildObject();
	}

	public static AuthnRequest buildAuthnRequest(
		String spEntityId, AssertionConsumerService assertionConsumerService,
		SingleSignOnService singleSignOnService, NameIDPolicy nameIDPolicy) {

		SAMLObjectBuilder<AuthnRequest> samlObjectBuilder =
			(SAMLObjectBuilder<AuthnRequest>)_getBuilder(
				AuthnRequest.DEFAULT_ELEMENT_NAME);

		AuthnRequest authnRequest = samlObjectBuilder.buildObject();

		DateTime now = new DateTime(DateTimeZone.UTC);

		authnRequest.setForceAuthn(false);
		authnRequest.setIsPassive(false);
		authnRequest.setIssueInstant(now);

		authnRequest.setIssuer(buildIssuer(spEntityId));

		authnRequest.setAssertionConsumerServiceURL(
			assertionConsumerService.getLocation());
		authnRequest.setDestination(singleSignOnService.getLocation());
		authnRequest.setNameIDPolicy(nameIDPolicy);
		authnRequest.setProtocolBinding(assertionConsumerService.getBinding());
		authnRequest.setVersion(SAMLVersion.VERSION_20);

		return authnRequest;
	}

	public static AuthnStatement buildAuthnStatement() {
		SAMLObjectBuilder<AuthnStatement> samlObjectBuilder =
			(SAMLObjectBuilder<AuthnStatement>)_getBuilder(
				AuthnStatement.DEFAULT_ELEMENT_NAME);

		return samlObjectBuilder.buildObject();
	}

	public static Conditions buildConditions() {
		SAMLObjectBuilder<Conditions> samlObjectBuilder =
			(SAMLObjectBuilder<Conditions>)_getBuilder(
				Conditions.DEFAULT_ELEMENT_NAME);

		return samlObjectBuilder.buildObject();
	}

	public static EntityDescriptor buildEntityDescriptor() {
		SAMLObjectBuilder<EntityDescriptor> samlObjectBuilder =
			(SAMLObjectBuilder<EntityDescriptor>)_getBuilder(
				EntityDescriptor.DEFAULT_ELEMENT_NAME);

		return samlObjectBuilder.buildObject();
	}

	public static IDPSSODescriptor buildIdpSsoDescriptor() {
		SAMLObjectBuilder<IDPSSODescriptor> samlObjectBuilder =
			(SAMLObjectBuilder<IDPSSODescriptor>)_getBuilder(
				IDPSSODescriptor.DEFAULT_ELEMENT_NAME);

		return samlObjectBuilder.buildObject();
	}

	public static Issuer buildIssuer(String value) {
		SAMLObjectBuilder<Issuer> samlObjectBuilder =
			(SAMLObjectBuilder<Issuer>)_getBuilder(Issuer.DEFAULT_ELEMENT_NAME);

		Issuer issuer = samlObjectBuilder.buildObject();

		issuer.setValue(value);

		return issuer;
	}

	public static KeyDescriptor buildKeyDescriptor(
		UsageType usageType, KeyInfo keyInfo) {

		SAMLObjectBuilder<KeyDescriptor> samlObjectBuilder =
			(SAMLObjectBuilder<KeyDescriptor>)_getBuilder(
				KeyDescriptor.DEFAULT_ELEMENT_NAME);

		KeyDescriptor keyDescriptor = samlObjectBuilder.buildObject();

		keyDescriptor.setKeyInfo(keyInfo);
		keyDescriptor.setUse(usageType);

		return keyDescriptor;
	}

	public static KeyInfo buildKeyInfo(Credential credential)
		throws SecurityException {

		EncryptionConfiguration encryptionConfiguration =
			ConfigurationServiceBootstrapUtil.get(
				EncryptionConfiguration.class);

		NamedKeyInfoGeneratorManager namedKeyInfoGeneratorManager =
			encryptionConfiguration.getDataKeyInfoGeneratorManager();

		KeyInfoGeneratorManager keyInfoGeneratorManager =
			namedKeyInfoGeneratorManager.getDefaultManager();

		KeyInfoGeneratorFactory keyInfoGeneratorFactory =
			keyInfoGeneratorManager.getFactory(credential);

		KeyInfoGenerator keyInfoGenerator =
			keyInfoGeneratorFactory.newInstance();

		return keyInfoGenerator.generate(credential);
	}

	public static LogoutRequest buildLogoutRequest() {
		SAMLObjectBuilder<LogoutRequest> samlObjectBuilder =
			(SAMLObjectBuilder<LogoutRequest>)_getBuilder(
				LogoutRequest.DEFAULT_ELEMENT_NAME);

		return samlObjectBuilder.buildObject();
	}

	public static LogoutResponse buildLogoutResponse() {
		SAMLObjectBuilder<LogoutResponse> samlObjectBuilder =
			(SAMLObjectBuilder<LogoutResponse>)_getBuilder(
				LogoutResponse.DEFAULT_ELEMENT_NAME);

		return samlObjectBuilder.buildObject();
	}

	public static NameID buildNameId(String nameIdFormat, String nameIdValue) {
		return buildNameId(nameIdFormat, null, null, nameIdValue);
	}

	public static NameID buildNameId(
		String nameIdFormat, String nameIdNameQualifier,
		String nameIdSPNameQualifier, String nameIdValue) {

		SAMLObjectBuilder<NameID> samlObjectBuilder =
			(SAMLObjectBuilder<NameID>)_getBuilder(NameID.DEFAULT_ELEMENT_NAME);

		NameID nameID = samlObjectBuilder.buildObject();

		nameID.setFormat(nameIdFormat);
		nameID.setValue(nameIdValue);

		if (Validator.isNotNull(nameIdNameQualifier)) {
			nameID.setNameQualifier(nameIdNameQualifier);
		}

		if (Validator.isNotNull(nameIdSPNameQualifier)) {
			nameID.setSPNameQualifier(nameIdSPNameQualifier);
		}

		return nameID;
	}

	public static NameIDPolicy buildNameIdPolicy() {
		SAMLObjectBuilder<NameIDPolicy> samlObjectBuilder =
			(SAMLObjectBuilder<NameIDPolicy>)_getBuilder(
				NameIDPolicy.DEFAULT_ELEMENT_NAME);

		return samlObjectBuilder.buildObject();
	}

	public static Response buildResponse() {
		SAMLObjectBuilder<Response> samlObjectBuilder =
			(SAMLObjectBuilder<Response>)_getBuilder(
				Response.DEFAULT_ELEMENT_NAME);

		Response response = samlObjectBuilder.buildObject();

		response.setVersion(SAMLVersion.VERSION_20);

		return response;
	}

	public static SessionIndex buildSessionIndex(String sessionIndexString) {
		SAMLObjectBuilder<SessionIndex> samlObjectBuilder =
			(SAMLObjectBuilder<SessionIndex>)_getBuilder(
				SessionIndex.DEFAULT_ELEMENT_NAME);

		SessionIndex sessionIndex = samlObjectBuilder.buildObject();

		sessionIndex.setSessionIndex(sessionIndexString);

		return sessionIndex;
	}

	public static Signature buildSignature(Credential credential) {
		XMLObjectBuilder<Signature> samlObjectBuilder = _getBuilder(
			Signature.DEFAULT_ELEMENT_NAME);

		Signature signature = samlObjectBuilder.buildObject(
			Signature.DEFAULT_ELEMENT_NAME);

		signature.setSigningCredential(credential);

		return signature;
	}

	public static SingleLogoutService buildSingleLogoutService(
		String binding, String location) {

		SAMLObjectBuilder<SingleLogoutService> samlObjectBuilder =
			(SAMLObjectBuilder<SingleLogoutService>)_getBuilder(
				SingleLogoutService.DEFAULT_ELEMENT_NAME);

		SingleLogoutService singleLogoutService =
			samlObjectBuilder.buildObject();

		singleLogoutService.setBinding(binding);
		singleLogoutService.setLocation(location);

		return singleLogoutService;
	}

	public static SingleSignOnService buildSingleSignOnService(
		String binding, String location) {

		SAMLObjectBuilder<SingleSignOnService> samlObjectBuilder =
			(SAMLObjectBuilder<SingleSignOnService>)_getBuilder(
				SingleSignOnService.DEFAULT_ELEMENT_NAME);

		SingleSignOnService singleSignOnService =
			samlObjectBuilder.buildObject();

		singleSignOnService.setBinding(binding);
		singleSignOnService.setLocation(location);

		return singleSignOnService;
	}

	public static SPSSODescriptor buildSpSsoDescriptor() {
		SAMLObjectBuilder<SPSSODescriptor> samlObjectBuilder =
			(SAMLObjectBuilder<SPSSODescriptor>)_getBuilder(
				SPSSODescriptor.DEFAULT_ELEMENT_NAME);

		return samlObjectBuilder.buildObject();
	}

	public static Status buildStatus(StatusCode statusCode) {
		SAMLObjectBuilder<Status> samlObjectBuilder =
			(SAMLObjectBuilder<Status>)_getBuilder(Status.DEFAULT_ELEMENT_NAME);

		Status status = samlObjectBuilder.buildObject();

		status.setStatusCode(statusCode);

		return status;
	}

	public static StatusCode buildStatusCode(String value) {
		SAMLObjectBuilder<StatusCode> samlObjectBuilder =
			(SAMLObjectBuilder<StatusCode>)_getBuilder(
				StatusCode.DEFAULT_ELEMENT_NAME);

		StatusCode statusCode = samlObjectBuilder.buildObject();

		statusCode.setValue(value);

		return statusCode;
	}

	public static Subject buildSubject(NameID nameID) {
		SAMLObjectBuilder<Subject> samlObjectBuilder =
			(SAMLObjectBuilder<Subject>)_getBuilder(
				Subject.DEFAULT_ELEMENT_NAME);

		Subject subject = samlObjectBuilder.buildObject();

		subject.setNameID(nameID);

		return subject;
	}

	public static SubjectConfirmation buildSubjectConfirmation() {
		SAMLObjectBuilder<SubjectConfirmation> samlObjectBuilder =
			(SAMLObjectBuilder<SubjectConfirmation>)_getBuilder(
				SubjectConfirmation.DEFAULT_ELEMENT_NAME);

		return samlObjectBuilder.buildObject();
	}

	public static SubjectConfirmationData buildSubjectConfirmationData() {
		SAMLObjectBuilder<SubjectConfirmationData> samlObjectBuilder =
			(SAMLObjectBuilder<SubjectConfirmationData>)_getBuilder(
				SubjectConfirmationData.DEFAULT_ELEMENT_NAME);

		return samlObjectBuilder.buildObject();
	}

	public static String marshall(XMLObject xmlObject)
		throws MarshallingException {

		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		XMLObjectSupport.marshallToOutputStream(
			xmlObject, byteArrayOutputStream);

		return byteArrayOutputStream.toString();
	}

	public static void prepareSecurityParametersContext(
			Credential credential,
			SecurityParametersContext securityParametersContext,
			RoleDescriptor peerRoleDescriptor)
		throws PortalException {

		try {
			SignatureSigningParameters signatureSigningParameters =
				_getSignatureSigningParameters(credential, peerRoleDescriptor);

			securityParametersContext.setSignatureSigningParameters(
				signatureSigningParameters);

			signatureSigningParameters.setSigningCredential(credential);
		}
		catch (ResolverException resolverException) {
			throw new PortalException(resolverException);
		}
	}

	public static void signObject(
			SignableSAMLObject signableObject, Credential credential,
			RoleDescriptor peerRoleDescriptor)
		throws MarshallingException, SecurityException, SignatureException {

		Signature signature = buildSignature(credential);

		try {
			SignatureSupport.prepareSignatureParams(
				signature,
				_getSignatureSigningParameters(credential, peerRoleDescriptor));

			signableObject.setSignature(signature);

			XMLObjectProviderRegistry xmlObjectProviderRegistry =
				ConfigurationServiceBootstrapUtil.get(
					XMLObjectProviderRegistry.class);

			MarshallerFactory marshallerFactory =
				xmlObjectProviderRegistry.getMarshallerFactory();

			Marshaller marshaller = marshallerFactory.getMarshaller(
				signableObject);

			marshaller.marshall(signableObject);

			Signer.signObject(signature);
		}
		catch (ResolverException resolverException) {
			throw new SignatureException(resolverException);
		}
	}

	public static XMLObject unmarshall(String xml)
		throws UnmarshallingException, XMLParserException {

		XMLObjectProviderRegistry xmlObjectProviderRegistry =
			ConfigurationServiceBootstrapUtil.get(
				XMLObjectProviderRegistry.class);

		return XMLObjectSupport.unmarshallFromInputStream(
			xmlObjectProviderRegistry.getParserPool(),
			new ByteArrayInputStream(xml.getBytes()));
	}

	private static XMLObjectBuilderFactory _createXMLObjectBuilderFactory() {
		XMLObjectProviderRegistry xmlObjectProviderRegistry =
			ConfigurationServiceBootstrapUtil.get(
				XMLObjectProviderRegistry.class);

		return xmlObjectProviderRegistry.getBuilderFactory();
	}

	@SuppressWarnings("rawtypes")
	private static XMLObjectBuilder _getBuilder(QName qName) {
		XMLObjectBuilderFactory xmlObjectBuilderFactory =
			_xmlObjectBuilderFactoryDCLSingleton.getSingleton(
				OpenSamlUtil::_createXMLObjectBuilderFactory);

		return xmlObjectBuilderFactory.getBuilder(qName);
	}

	private static SignatureSigningParameters _getSignatureSigningParameters(
			Credential credential, RoleDescriptor peerRoleDescriptor)
		throws ResolverException {

		BasicSignatureSigningConfiguration basicSignatureSigningConfiguration =
			new BasicSignatureSigningConfiguration();

		basicSignatureSigningConfiguration.setSigningCredentials(
			Collections.singletonList(credential));

		SignatureSigningConfigurationCriterion
			signatureSigningConfigurationCriterion =
				new SignatureSigningConfigurationCriterion(
					ConfigurationServiceBootstrapUtil.get(
						SignatureSigningConfiguration.class),
					basicSignatureSigningConfiguration);

		CriteriaSet criteriaSet = new CriteriaSet(
			signatureSigningConfigurationCriterion);

		if (peerRoleDescriptor != null) {
			criteriaSet.add(new RoleDescriptorCriterion(peerRoleDescriptor));
		}

		SAMLMetadataSignatureSigningParametersResolver
			samlMetadataSignatureSigningParametersResolver =
				new SAMLMetadataSignatureSigningParametersResolver();

		return samlMetadataSignatureSigningParametersResolver.resolveSingle(
			criteriaSet);
	}

	private static final DCLSingleton<XMLObjectBuilderFactory>
		_xmlObjectBuilderFactoryDCLSingleton = new DCLSingleton<>();

}