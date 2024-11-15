/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.util;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.joda.time.DateTime;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.core.xml.schema.XSBoolean;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.core.xml.schema.XSDateTime;
import org.opensaml.core.xml.schema.XSInteger;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.messaging.context.InOutOperationContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.messaging.context.SAMLMetadataContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SSODescriptor;
import org.opensaml.saml.saml2.metadata.SingleLogoutService;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;

/**
 * @author Mika Koivisto
 */
public class SamlUtil {

	public static AssertionConsumerService
			getAssertionConsumerServiceForBinding(
				SPSSODescriptor spSSODescriptor, String binding)
		throws ResolverException {

		AssertionConsumerService assertionConsumerService =
			spSSODescriptor.getDefaultAssertionConsumerService();

		if (binding.equals(assertionConsumerService.getBinding())) {
			return assertionConsumerService;
		}

		List<AssertionConsumerService> assertionConsumerServices =
			spSSODescriptor.getAssertionConsumerServices();

		for (AssertionConsumerService curAssertionConsumerService :
				assertionConsumerServices) {

			if (binding.equals(curAssertionConsumerService.getBinding())) {
				return curAssertionConsumerService;
			}
		}

		throw new ResolverException("Binding " + binding + " is not supported");
	}

	public static Attribute getAttribute(
		List<Attribute> attributes, String attributeName) {

		for (Attribute attribute : attributes) {
			if (attributeName.equals(attribute.getName())) {
				return attribute;
			}
		}

		return null;
	}

	public static Map<String, List<Serializable>> getAttributesMap(
		List<Attribute> attributes, Properties attributeMappingsProperties) {

		Map<String, List<Serializable>> attributesMap = new HashMap<>();

		for (Attribute attribute : attributes) {
			boolean implicitMapping = false;

			String key = attributeMappingsProperties.getProperty(
				attribute.getName());

			if (Validator.isNull(key) &&
				Validator.isNotNull(attribute.getFriendlyName())) {

				key = attributeMappingsProperties.getProperty(
					attribute.getFriendlyName());
			}

			if (Validator.isNull(key)) {
				if (attributeMappingsProperties.containsKey(
						attribute.getName())) {

					continue;
				}

				implicitMapping = true;
				key = attribute.getName();
			}

			List<XMLObject> xmlValues = attribute.getAttributeValues();

			List<Serializable> values = attributesMap.get(key);

			if (values == null) {
				values = new ArrayList<>(xmlValues.size());
			}

			for (XMLObject xmlObject : xmlValues) {
				Serializable value = getXMLObjectValue(xmlObject);

				if (value != null) {
					if (implicitMapping) {
						values.add(value);
					}
					else {
						values.add(0, value);
					}
				}
			}

			attributesMap.put(key, values);
		}

		return attributesMap;
	}

	public static AuthnRequest getAuthnRequest(
		MessageContext<?> messageContext) {

		InOutOperationContext<AuthnRequest, ?> inOutOperationContext =
			messageContext.getSubcontext(InOutOperationContext.class, false);

		if (inOutOperationContext == null) {
			return null;
		}

		MessageContext<AuthnRequest> inboundMessageContext =
			inOutOperationContext.getInboundMessageContext();

		return inboundMessageContext.getMessage();
	}

	public static EntityDescriptor getEntityDescriptorById(
		String entityId, EntitiesDescriptor descriptor) {

		List<EntityDescriptor> entityDescriptors =
			descriptor.getEntityDescriptors();

		if ((entityDescriptors != null) && !entityDescriptors.isEmpty()) {
			for (EntityDescriptor entityDescriptor : entityDescriptors) {
				if (Objects.equals(entityDescriptor.getEntityID(), entityId)) {
					return entityDescriptor;
				}
			}
		}

		return null;
	}

	public static EntityDescriptor getEntityDescriptorById(
		String entityId, XMLObject metadata) {

		if (metadata instanceof EntityDescriptor) {
			EntityDescriptor entityDescriptor = (EntityDescriptor)metadata;

			if (Objects.equals(entityDescriptor.getEntityID(), entityId)) {
				return entityDescriptor;
			}
		}
		else if (metadata instanceof EntitiesDescriptor) {
			return getEntityDescriptorById(
				entityId, (EntitiesDescriptor)metadata);
		}

		return null;
	}

	public static String getNameIdFormat(NameID nameID) {
		String format = nameID.getFormat();

		if (Validator.isNull(format)) {
			return NameID.UNSPECIFIED;
		}

		return format;
	}

	public static SingleLogoutService getSingleLogoutServiceForBinding(
			SSODescriptor ssoDescriptor, String binding)
		throws ResolverException {

		List<SingleLogoutService> singleLogoutServices =
			ssoDescriptor.getSingleLogoutServices();

		for (SingleLogoutService singleLogoutService : singleLogoutServices) {
			if (binding.equals(singleLogoutService.getBinding())) {
				return singleLogoutService;
			}
		}

		throw new ResolverException("Binding " + binding + " is not supported");
	}

	public static SingleSignOnService getSingleSignOnServiceForBinding(
			IDPSSODescriptor idpSSODescriptor, String binding)
		throws ResolverException {

		List<SingleSignOnService> singleSignOnServices =
			idpSSODescriptor.getSingleSignOnServices();

		for (SingleSignOnService singleSignOnService : singleSignOnServices) {
			if (binding.equals(singleSignOnService.getBinding())) {
				return singleSignOnService;
			}
		}

		throw new ResolverException("Binding " + binding + " is not supported");
	}

	public static Date getValueAsDate(
		String key, Map<String, List<Serializable>> attributesMap) {

		List<Serializable> values = attributesMap.get(key);

		if (ListUtil.isEmpty(values)) {
			return null;
		}

		DateTime dateTime = new DateTime(values.get(0));

		return dateTime.toDate();
	}

	public static DateTime getValueAsDateTime(
		String key, Map<String, List<Serializable>> attributesMap) {

		List<Serializable> values = attributesMap.get(key);

		if (ListUtil.isEmpty(values)) {
			return null;
		}

		return new DateTime(values.get(0));
	}

	public static String getValueAsString(Attribute attribute) {
		if (attribute == null) {
			return null;
		}

		List<XMLObject> values = attribute.getAttributeValues();

		if (values.isEmpty()) {
			return null;
		}

		return getValueAsString(values.get(0));
	}

	public static String getValueAsString(
		String key, Map<String, List<Serializable>> attributesMap) {

		List<Serializable> values = attributesMap.get(key);

		if (ListUtil.isEmpty(values)) {
			return null;
		}

		return String.valueOf(values.get(0));
	}

	public static String getValueAsString(XMLObject xmlObject) {
		if (xmlObject instanceof XSAny) {
			XSAny xsAny = (XSAny)xmlObject;

			return xsAny.getTextContent();
		}
		else if (xmlObject instanceof XSDateTime) {
			XSDateTime xsDateTime = (XSDateTime)xmlObject;

			return String.valueOf(xsDateTime.getValue());
		}
		else if (xmlObject instanceof XSString) {
			XSString xsString = (XSString)xmlObject;

			return xsString.getValue();
		}

		return null;
	}

	public static Serializable getXMLObjectValue(XMLObject xmlObject) {
		if (xmlObject instanceof XSAny) {
			XSAny xsAny = (XSAny)xmlObject;

			return xsAny.getTextContent();
		}
		else if (xmlObject instanceof XSBoolean) {
			XSBoolean xsBoolean = (XSBoolean)xmlObject;

			XSBooleanValue xsBooleanValue = xsBoolean.getValue();

			return xsBooleanValue.getValue();
		}
		else if (xmlObject instanceof XSDateTime) {
			XSDateTime xsDateTime = (XSDateTime)xmlObject;

			return String.valueOf(xsDateTime.getValue());
		}
		else if (xmlObject instanceof XSInteger) {
			XSInteger xsInteger = (XSInteger)xmlObject;

			return xsInteger.getValue();
		}
		else if (xmlObject instanceof XSString) {
			XSString xsString = (XSString)xmlObject;

			return xsString.getValue();
		}

		return null;
	}

	public static AssertionConsumerService resolverAssertionConsumerService(
		MessageContext<?> messageContext, String binding,
		boolean dynamicACSURL) {

		AuthnRequest authnRequest = getAuthnRequest(messageContext);

		Integer assertionConsumerServiceIndex = null;
		String assertionConsumerServiceURL = null;

		if (authnRequest != null) {
			assertionConsumerServiceIndex =
				authnRequest.getAssertionConsumerServiceIndex();
			assertionConsumerServiceURL =
				authnRequest.getAssertionConsumerServiceURL();
			binding = GetterUtil.getString(
				authnRequest.getProtocolBinding(), binding);
		}

		SAMLPeerEntityContext samlPeerEntityContext =
			messageContext.getSubcontext(SAMLPeerEntityContext.class);

		SAMLMetadataContext samlMetadataContext =
			samlPeerEntityContext.getSubcontext(SAMLMetadataContext.class);

		SPSSODescriptor spSSODescriptor =
			(SPSSODescriptor)samlMetadataContext.getRoleDescriptor();

		for (AssertionConsumerService assertionConsumerService :
				spSSODescriptor.getAssertionConsumerServices()) {

			if (!binding.equals(assertionConsumerService.getBinding())) {
				continue;
			}

			Integer curAssertionConsumerServiceIndex =
				assertionConsumerService.getIndex();

			if ((assertionConsumerServiceIndex != null) &&
				(curAssertionConsumerServiceIndex.intValue() ==
					assertionConsumerServiceIndex.intValue())) {

				return assertionConsumerService;
			}
			else if (Validator.isNotNull(assertionConsumerServiceURL) &&
					 assertionConsumerServiceURL.equals(
						 assertionConsumerService.getLocation())) {

				return assertionConsumerService;
			}
		}

		if (dynamicACSURL && Validator.isNotNull(assertionConsumerServiceURL)) {
			return OpenSamlUtil.buildAssertionConsumerService(
				binding, -1, false, assertionConsumerServiceURL);
		}

		for (AssertionConsumerService assertionConsumerService :
				spSSODescriptor.getAssertionConsumerServices()) {

			if (binding.equals(assertionConsumerService.getBinding())) {
				return assertionConsumerService;
			}
		}

		return null;
	}

	public static SingleLogoutService resolveSingleLogoutService(
		SSODescriptor ssoDescriptor, String preferredBinding) {

		List<SingleLogoutService> singleLogoutServices =
			ssoDescriptor.getSingleLogoutServices();

		for (SingleLogoutService singleLogoutService : singleLogoutServices) {
			if (preferredBinding.equals(singleLogoutService.getBinding())) {
				return singleLogoutService;
			}
		}

		if (!singleLogoutServices.isEmpty()) {
			return singleLogoutServices.get(0);
		}

		return null;
	}

	public static SingleSignOnService resolveSingleSignOnService(
		IDPSSODescriptor idpSSODescriptor, String preferredBinding) {

		List<SingleSignOnService> singleSignOnServices =
			idpSSODescriptor.getSingleSignOnServices();

		for (SingleSignOnService singleSignOnService : singleSignOnServices) {
			if (preferredBinding.equals(singleSignOnService.getBinding())) {
				return singleSignOnService;
			}
		}

		if (!singleSignOnServices.isEmpty()) {
			return singleSignOnServices.get(0);
		}

		return null;
	}

}