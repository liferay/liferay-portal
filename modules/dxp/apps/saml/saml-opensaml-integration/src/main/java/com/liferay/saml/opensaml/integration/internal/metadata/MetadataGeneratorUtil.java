/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.metadata;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.saml.opensaml.integration.internal.util.ConfigurationServiceBootstrapUtil;
import com.liferay.saml.opensaml.integration.internal.util.OpenSamlUtil;
import com.liferay.saml.runtime.exception.CredentialException;
import com.liferay.saml.runtime.exception.EntityIdException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.ext.saml2alg.DigestMethod;
import org.opensaml.saml.ext.saml2alg.SigningMethod;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.EncryptionMethod;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SingleLogoutService;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.UsageType;
import org.opensaml.xmlsec.EncryptionConfiguration;
import org.opensaml.xmlsec.SignatureSigningConfiguration;
import org.opensaml.xmlsec.algorithm.AlgorithmDescriptor;
import org.opensaml.xmlsec.algorithm.AlgorithmRegistry;
import org.opensaml.xmlsec.algorithm.AlgorithmSupport;
import org.opensaml.xmlsec.algorithm.KeyLengthSpecifiedAlgorithm;
import org.opensaml.xmlsec.encryption.KeySize;
import org.opensaml.xmlsec.encryption.impl.KeySizeBuilder;

/**
 * @author Mika Koivisto
 */
public class MetadataGeneratorUtil {

	public static EntityDescriptor buildIbEntityDescriptor(
			String portalURL, String entityId, boolean signAuthnRequests,
			boolean wantAuthnRequestSigned, boolean signMetadata,
			Credential credential, Credential encryptionCredential)
		throws Exception {

		EntityDescriptor idpEntityDescriptor = buildIdpEntityDescriptor(
			portalURL, entityId, wantAuthnRequestSigned, signMetadata,
			credential, encryptionCredential);

		List<RoleDescriptor> idpRoleDescriptors =
			idpEntityDescriptor.getRoleDescriptors();

		EntityDescriptor spEntityDescriptor = buildSpEntityDescriptor(
			portalURL, entityId, signAuthnRequests, signMetadata,
			wantAuthnRequestSigned, credential, encryptionCredential);

		List<RoleDescriptor> spRoleDescriptors =
			spEntityDescriptor.getRoleDescriptors();

		RoleDescriptor roleDescriptor = spRoleDescriptors.get(0);

		roleDescriptor.detach();

		idpRoleDescriptors.addAll(spEntityDescriptor.getRoleDescriptors());

		return idpEntityDescriptor;
	}

	public static EntityDescriptor buildIdpEntityDescriptor(
			String portalURL, String entityId, boolean wantAuthnRequestSigned,
			boolean signMetadata, Credential credential,
			Credential encryptionCredential)
		throws Exception {

		if (Validator.isNull(entityId)) {
			throw new EntityIdException("Entity ID is required");
		}

		if (credential == null) {
			throw new CredentialException("Credential is required");
		}

		EntityDescriptor entityDescriptor =
			OpenSamlUtil.buildEntityDescriptor();

		entityDescriptor.setEntityID(entityId);

		List<RoleDescriptor> roleDescriptors =
			entityDescriptor.getRoleDescriptors();

		RoleDescriptor roleDescriptor = buildIdpSsoDescriptor(
			portalURL, entityId, wantAuthnRequestSigned, credential,
			encryptionCredential);

		Extensions extensions = (Extensions)XMLObjectSupport.buildXMLObject(
			Extensions.DEFAULT_ELEMENT_NAME);

		List<XMLObject> unknownXMLObjects = extensions.getUnknownXMLObjects();

		unknownXMLObjects.addAll(_getExtensionXmlObjects(credential));

		roleDescriptor.setExtensions(extensions);

		roleDescriptors.add(roleDescriptor);

		if (signMetadata) {
			OpenSamlUtil.signObject(entityDescriptor, credential, null);
		}

		return entityDescriptor;
	}

	public static IDPSSODescriptor buildIdpSsoDescriptor(
			String portalURL, String entityId, boolean wantAuthnRequestSigned,
			Credential credential, Credential encryptionCredential)
		throws Exception {

		IDPSSODescriptor idpSSODescriptor =
			OpenSamlUtil.buildIdpSsoDescriptor();

		idpSSODescriptor.addSupportedProtocol(SAMLConstants.SAML20P_NS);
		idpSSODescriptor.setWantAuthnRequestsSigned(wantAuthnRequestSigned);

		List<KeyDescriptor> keyDescriptors =
			idpSSODescriptor.getKeyDescriptors();

		KeyDescriptor keyDescriptor = OpenSamlUtil.buildKeyDescriptor(
			UsageType.SIGNING, OpenSamlUtil.buildKeyInfo(credential));

		keyDescriptors.add(keyDescriptor);

		if (encryptionCredential != null) {
			keyDescriptors.add(
				getEncryptionKeyDescriptor(encryptionCredential));
		}

		List<SingleSignOnService> singleSignOnServices =
			idpSSODescriptor.getSingleSignOnServices();

		SingleSignOnService singleSignOnService =
			OpenSamlUtil.buildSingleSignOnService(
				SAMLConstants.SAML2_REDIRECT_BINDING_URI,
				StringBundler.concat(
					portalURL, PortalUtil.getPathMain(), "/portal/saml/sso"));

		singleSignOnServices.add(singleSignOnService);

		singleSignOnService = OpenSamlUtil.buildSingleSignOnService(
			SAMLConstants.SAML2_POST_BINDING_URI,
			StringBundler.concat(
				portalURL, PortalUtil.getPathMain(), "/portal/saml/sso"));

		singleSignOnServices.add(singleSignOnService);

		List<SingleLogoutService> singleLogoutServices =
			idpSSODescriptor.getSingleLogoutServices();

		SingleLogoutService postSingleLogoutService =
			OpenSamlUtil.buildSingleLogoutService(
				SAMLConstants.SAML2_POST_BINDING_URI,
				StringBundler.concat(
					portalURL, PortalUtil.getPathMain(), "/portal/saml/slo"));

		singleLogoutServices.add(postSingleLogoutService);

		SingleLogoutService redirectSingleLogoutService =
			OpenSamlUtil.buildSingleLogoutService(
				SAMLConstants.SAML2_REDIRECT_BINDING_URI,
				StringBundler.concat(
					portalURL, PortalUtil.getPathMain(), "/portal/saml/slo"));

		singleLogoutServices.add(redirectSingleLogoutService);

		return idpSSODescriptor;
	}

	public static EntityDescriptor buildSpEntityDescriptor(
			String portalURL, String entityId, boolean signAuthnRequests,
			boolean signMetadata, boolean wantAssertionsSigned,
			Credential credential, Credential encryptionCredential)
		throws Exception {

		EntityDescriptor entityDescriptor =
			OpenSamlUtil.buildEntityDescriptor();

		entityDescriptor.setEntityID(entityId);

		List<RoleDescriptor> roleDescriptors =
			entityDescriptor.getRoleDescriptors();

		RoleDescriptor roleDescriptor = buildSpSsoDescriptor(
			portalURL, entityId, signAuthnRequests, wantAssertionsSigned,
			credential, encryptionCredential);

		Extensions extensions = (Extensions)XMLObjectSupport.buildXMLObject(
			Extensions.DEFAULT_ELEMENT_NAME);

		List<XMLObject> unknownXMLObjects = extensions.getUnknownXMLObjects();

		unknownXMLObjects.addAll(_getExtensionXmlObjects(credential));

		roleDescriptor.setExtensions(extensions);

		roleDescriptors.add(roleDescriptor);

		if (signMetadata) {
			OpenSamlUtil.signObject(entityDescriptor, credential, null);
		}

		return entityDescriptor;
	}

	public static SPSSODescriptor buildSpSsoDescriptor(
			String portalURL, String entityId, boolean signAuthnRequests,
			boolean wantAssertionsSigned, Credential credential,
			Credential encryptionCredential)
		throws Exception {

		SPSSODescriptor spSSODescriptor = OpenSamlUtil.buildSpSsoDescriptor();

		spSSODescriptor.addSupportedProtocol(SAMLConstants.SAML20P_NS);

		spSSODescriptor.setAuthnRequestsSigned(signAuthnRequests);
		spSSODescriptor.setWantAssertionsSigned(wantAssertionsSigned);

		List<AssertionConsumerService> assertionConsumerServices =
			spSSODescriptor.getAssertionConsumerServices();

		AssertionConsumerService assertionConsumerService =
			OpenSamlUtil.buildAssertionConsumerService(
				SAMLConstants.SAML2_POST_BINDING_URI, 1, true,
				StringBundler.concat(
					portalURL, PortalUtil.getPathMain(), "/portal/saml/acs"));

		assertionConsumerServices.add(assertionConsumerService);

		List<KeyDescriptor> keyDescriptors =
			spSSODescriptor.getKeyDescriptors();

		KeyDescriptor keyDescriptor = OpenSamlUtil.buildKeyDescriptor(
			UsageType.SIGNING, OpenSamlUtil.buildKeyInfo(credential));

		keyDescriptors.add(keyDescriptor);

		if (encryptionCredential != null) {
			keyDescriptors.add(
				getEncryptionKeyDescriptor(encryptionCredential));
		}

		List<SingleLogoutService> singleLogoutServices =
			spSSODescriptor.getSingleLogoutServices();

		SingleLogoutService postSingleLogoutService =
			OpenSamlUtil.buildSingleLogoutService(
				SAMLConstants.SAML2_POST_BINDING_URI,
				StringBundler.concat(
					portalURL, PortalUtil.getPathMain(), "/portal/saml/slo"));

		singleLogoutServices.add(postSingleLogoutService);

		SingleLogoutService redirectSingleLogoutService =
			OpenSamlUtil.buildSingleLogoutService(
				SAMLConstants.SAML2_REDIRECT_BINDING_URI,
				StringBundler.concat(
					portalURL, PortalUtil.getPathMain(), "/portal/saml/slo"));

		singleLogoutServices.add(redirectSingleLogoutService);

		SingleLogoutService soapSingleLogoutService =
			OpenSamlUtil.buildSingleLogoutService(
				SAMLConstants.SAML2_SOAP11_BINDING_URI,
				StringBundler.concat(
					portalURL, PortalUtil.getPathMain(),
					"/portal/saml/slo_soap"));

		singleLogoutServices.add(soapSingleLogoutService);

		return spSSODescriptor;
	}

	public static KeyDescriptor getEncryptionKeyDescriptor(
			Credential credential)
		throws SecurityException {

		KeyDescriptor encryptionKeyDescriptor = OpenSamlUtil.buildKeyDescriptor(
			UsageType.ENCRYPTION, OpenSamlUtil.buildKeyInfo(credential));

		List<EncryptionMethod> encryptionMethods =
			encryptionKeyDescriptor.getEncryptionMethods();

		List<String> algorithms = new ArrayList<>();

		EncryptionConfiguration encryptionConfiguration =
			ConfigurationServiceBootstrapUtil.get(
				EncryptionConfiguration.class);

		algorithms.addAll(
			encryptionConfiguration.getDataEncryptionAlgorithms());
		algorithms.addAll(
			encryptionConfiguration.getKeyTransportEncryptionAlgorithms());

		for (String algorithm : algorithms) {
			AlgorithmRegistry algorithmRegistry =
				AlgorithmSupport.getGlobalAlgorithmRegistry();

			Collection<String> blacklistedAlgorithms =
				encryptionConfiguration.getBlacklistedAlgorithms();

			if (!algorithmRegistry.isRuntimeSupported(algorithm) ||
				blacklistedAlgorithms.contains(algorithm)) {

				continue;
			}

			AlgorithmDescriptor algorithmDescriptor = algorithmRegistry.get(
				algorithm);

			if (AlgorithmSupport.isKeyEncryptionAlgorithm(
					algorithmDescriptor) &&
				!AlgorithmSupport.credentialSupportsAlgorithmForEncryption(
					credential, algorithmDescriptor)) {

				continue;
			}

			EncryptionMethod encryptionMethod =
				(EncryptionMethod)XMLObjectSupport.buildXMLObject(
					EncryptionMethod.DEFAULT_ELEMENT_NAME);

			encryptionMethod.setAlgorithm(algorithmDescriptor.getURI());

			if (encryptionMethod instanceof KeyLengthSpecifiedAlgorithm) {
				KeyLengthSpecifiedAlgorithm keySpecifiedAlgorithm =
					(KeyLengthSpecifiedAlgorithm)encryptionMethod;

				KeySizeBuilder keySizeBuilder = new KeySizeBuilder();

				KeySize keySize = keySizeBuilder.buildObject();

				keySize.setValue(keySpecifiedAlgorithm.getKeyLength());

				encryptionMethod.setKeySize(keySize);
			}

			encryptionMethods.add(encryptionMethod);
		}

		return encryptionKeyDescriptor;
	}

	private static List<XMLObject> _getExtensionXmlObjects(
		Credential credential) {

		List<XMLObject> xmlObjects = new ArrayList<>();

		AlgorithmRegistry algorithmRegistry =
			AlgorithmSupport.getGlobalAlgorithmRegistry();

		SignatureSigningConfiguration signatureSigningConfiguration =
			ConfigurationServiceBootstrapUtil.get(
				SignatureSigningConfiguration.class);

		Collection<String> blacklistedAlgorithms =
			signatureSigningConfiguration.getBlacklistedAlgorithms();

		for (String digestMethodString :
				signatureSigningConfiguration.
					getSignatureReferenceDigestMethods()) {

			if (!algorithmRegistry.isRuntimeSupported(digestMethodString) ||
				blacklistedAlgorithms.contains(digestMethodString)) {

				continue;
			}

			AlgorithmDescriptor algorithmDescriptor = algorithmRegistry.get(
				digestMethodString);

			DigestMethod digestMethod =
				(DigestMethod)XMLObjectSupport.buildXMLObject(
					DigestMethod.DEFAULT_ELEMENT_NAME);

			digestMethod.setAlgorithm(algorithmDescriptor.getURI());

			xmlObjects.add(digestMethod);
		}

		for (String signatureAlgorithms :
				signatureSigningConfiguration.getSignatureAlgorithms()) {

			if (!algorithmRegistry.isRuntimeSupported(signatureAlgorithms) ||
				blacklistedAlgorithms.contains(signatureAlgorithms)) {

				continue;
			}

			AlgorithmDescriptor algorithmDescriptor = algorithmRegistry.get(
				signatureAlgorithms);

			if (!AlgorithmSupport.credentialSupportsAlgorithmForSigning(
					credential, algorithmDescriptor)) {

				continue;
			}

			SigningMethod signingMethod =
				(SigningMethod)XMLObjectSupport.buildXMLObject(
					SigningMethod.DEFAULT_ELEMENT_NAME);

			signingMethod.setAlgorithm(algorithmDescriptor.getURI());

			xmlObjects.add(signingMethod);
		}

		return xmlObjects;
	}

}