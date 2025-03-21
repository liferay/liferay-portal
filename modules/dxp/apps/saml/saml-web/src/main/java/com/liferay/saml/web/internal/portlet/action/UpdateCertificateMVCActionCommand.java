/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.web.internal.portlet.action;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropertiesParamUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.saml.constants.SamlPortletKeys;
import com.liferay.saml.constants.SamlWebKeys;
import com.liferay.saml.runtime.certificate.CertificateEntityId;
import com.liferay.saml.runtime.certificate.CertificateTool;
import com.liferay.saml.runtime.configuration.SamlProviderConfigurationHelper;
import com.liferay.saml.runtime.exception.CertificateKeyPasswordException;
import com.liferay.saml.runtime.exception.UnsupportedBindingException;
import com.liferay.saml.runtime.metadata.LocalEntityManager;
import com.liferay.saml.util.PortletPropsKeys;
import com.liferay.saml.web.internal.util.SamlTempFileEntryUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.io.IOException;

import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import java.util.Calendar;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 * @author Stian Sigvartsen
 */
@Component(
	configurationPid = "com.liferay.saml.runtime.configuration.SamlKeyStoreManagerConfiguration",
	property = {
		"jakarta.portlet.name=" + SamlPortletKeys.SAML_ADMIN,
		"mvc.command.name=/admin/update_certificate"
	},
	service = MVCActionCommand.class
)
public class UpdateCertificateMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		if (!permissionChecker.isCompanyAdmin()) {
			throw new PrincipalException();
		}

		String cmd = ParamUtil.get(actionRequest, Constants.CMD, "auth");

		if (cmd.equals("auth")) {
			_authenticateCertificate(actionRequest, actionResponse);
		}
		else if (cmd.equals("delete")) {
			_deleteCertificate(actionRequest);
		}
		else if (cmd.equals("import")) {
			_importCertificate(actionRequest, themeDisplay.getUser());
		}
		else if (cmd.equals("replace")) {
			_replaceCertificate(actionRequest, actionResponse);
		}
	}

	private void _authenticateCertificate(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		LocalEntityManager.CertificateUsage certificateUsage =
			LocalEntityManager.CertificateUsage.valueOf(
				ParamUtil.getString(actionRequest, "certificateUsage"));

		UnicodeProperties unicodeProperties = PropertiesParamUtil.getProperties(
			actionRequest, "settings--");

		String keystoreCredentialPassword = unicodeProperties.getProperty(
			_getCertificateUsagePropertyKey(certificateUsage));

		if (Validator.isNotNull(keystoreCredentialPassword)) {
			_samlProviderConfigurationHelper.updateProperties(
				unicodeProperties);
		}

		try {
			X509Certificate x509Certificate =
				_localEntityManager.getLocalEntityCertificate(certificateUsage);

			actionRequest.setAttribute(
				SamlWebKeys.SAML_X509_CERTIFICATE, x509Certificate);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			SessionErrors.add(
				actionRequest, CertificateKeyPasswordException.class);
		}

		actionResponse.setRenderParameter(
			"mvcRenderCommandName", "/admin/update_certificate");
	}

	private void _deleteCertificate(ActionRequest actionRequest)
		throws Exception {

		_localEntityManager.deleteLocalEntityCertificate(
			LocalEntityManager.CertificateUsage.valueOf(
				ParamUtil.getString(actionRequest, "certificateUsage")));
	}

	private String _getCertificateUsagePropertyKey(
			LocalEntityManager.CertificateUsage certificateUsage)
		throws Exception {

		if (certificateUsage == LocalEntityManager.CertificateUsage.SIGNING) {
			return PortletPropsKeys.SAML_KEYSTORE_CREDENTIAL_PASSWORD;
		}
		else if (certificateUsage ==
					LocalEntityManager.CertificateUsage.ENCRYPTION) {

			return PortletPropsKeys.
				SAML_KEYSTORE_ENCRYPTION_CREDENTIAL_PASSWORD;
		}

		throw new UnsupportedBindingException(
			"Unsupported certificate usage: " + certificateUsage.name());
	}

	private void _importCertificate(ActionRequest actionRequest, User user)
		throws Exception {

		hideDefaultSuccessMessage(actionRequest);

		String selectUploadedFile = ParamUtil.getString(
			actionRequest, "selectUploadedFile");

		FileEntry fileEntry = SamlTempFileEntryUtil.getTempFileEntry(
			user, selectUploadedFile);

		String keyStorePassword = ParamUtil.getString(
			actionRequest, "keyStorePassword");

		char[] password = keyStorePassword.toCharArray();

		String selectKeyStoreAlias = actionRequest.getParameter(
			"selectKeyStoreAlias");

		KeyStore keyStore = null;
		KeyStore.PrivateKeyEntry privateKeyEntry = null;

		try {
			keyStore = KeyStore.getInstance("PKCS12");

			keyStore.load(fileEntry.getContentStream(), password);

			actionRequest.setAttribute(SamlWebKeys.SAML_KEYSTORE, keyStore);

			if (Validator.isBlank(selectKeyStoreAlias)) {
				return;
			}

			if (!keyStore.entryInstanceOf(
					selectKeyStoreAlias, KeyStore.PrivateKeyEntry.class)) {

				throw new IllegalArgumentException();
			}

			privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(
				selectKeyStoreAlias, new KeyStore.PasswordProtection(password));
		}
		catch (CertificateException certificateException) {
			if (_log.isDebugEnabled()) {
				_log.debug(certificateException);
			}

			SessionErrors.add(actionRequest, "certificateException");

			return;
		}
		catch (IOException ioException) {
			if (ioException.getCause() instanceof UnrecoverableKeyException) {
				SessionErrors.add(actionRequest, "incorrectKeyStorePassword");

				return;
			}

			throw new PortalException(ioException);
		}
		catch (KeyStoreException | NoSuchAlgorithmException exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			if (keyStore == null) {
				SessionErrors.add(
					actionRequest,
					"keyStoreIntegrityCheckingAlgorithmNotSupported");
			}
			else {
				SessionErrors.add(
					actionRequest, "keyEncryptionAlgorithmNotSupported");
			}

			return;
		}
		catch (UnrecoverableEntryException unrecoverableEntryException) {
			if (_log.isDebugEnabled()) {
				_log.debug(unrecoverableEntryException);
			}

			SessionErrors.add(actionRequest, "incorrectKeyPassword");

			return;
		}

		X509Certificate x509Certificate =
			(X509Certificate)privateKeyEntry.getCertificate();
		LocalEntityManager.CertificateUsage certificateUsage =
			LocalEntityManager.CertificateUsage.valueOf(
				ParamUtil.getString(actionRequest, "certificateUsage"));

		_localEntityManager.storeLocalEntityCertificate(
			privateKeyEntry.getPrivateKey(), keyStorePassword, x509Certificate,
			certificateUsage);

		_samlProviderConfigurationHelper.updateProperties(
			UnicodePropertiesBuilder.put(
				_getCertificateUsagePropertyKey(certificateUsage),
				keyStorePassword
			).build());

		SamlTempFileEntryUtil.deleteTempFileEntry(user, selectUploadedFile);

		actionRequest.setAttribute(
			SamlWebKeys.SAML_X509_CERTIFICATE, x509Certificate);
	}

	private void _replaceCertificate(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		hideDefaultSuccessMessage(actionRequest);

		UnicodeProperties unicodeProperties = PropertiesParamUtil.getProperties(
			actionRequest, "settings--");

		LocalEntityManager.CertificateUsage certificateUsage =
			LocalEntityManager.CertificateUsage.valueOf(
				ParamUtil.getString(actionRequest, "certificateUsage"));

		String keystoreCredentialPassword = unicodeProperties.getProperty(
			_getCertificateUsagePropertyKey(certificateUsage));

		if (Validator.isNull(keystoreCredentialPassword)) {
			throw new CertificateKeyPasswordException();
		}

		int certificateValidityDays = ParamUtil.getInteger(
			actionRequest, "certificateValidityDays");

		if (certificateValidityDays == 0) {
			SessionErrors.add(actionRequest, "certificateValidityDays");

			return;
		}

		Calendar startDate = Calendar.getInstance();

		Calendar endDate = (Calendar)startDate.clone();

		endDate.add(Calendar.DAY_OF_YEAR, certificateValidityDays);

		if (endDate.get(Calendar.YEAR) > 9999) {
			SessionErrors.add(actionRequest, "certificateValidityDays");

			return;
		}

		String keyAlgorithm = ParamUtil.getString(
			actionRequest, "certificateKeyAlgorithm");

		KeyPair keyPair = _certificateTool.generateKeyPair(
			keyAlgorithm,
			ParamUtil.getInteger(actionRequest, "certificateKeyLength"));

		CertificateEntityId subjectCertificateEntityId =
			new CertificateEntityId(
				ParamUtil.getString(actionRequest, "certificateCommonName"),
				ParamUtil.getString(actionRequest, "certificateOrganization"),
				ParamUtil.getString(
					actionRequest, "certificateOrganizationUnit"),
				ParamUtil.getString(actionRequest, "certificateLocality"),
				ParamUtil.getString(actionRequest, "certificateState"),
				ParamUtil.getString(actionRequest, "certificateCountry"));

		X509Certificate x509Certificate = _certificateTool.generateCertificate(
			keyPair, subjectCertificateEntityId, subjectCertificateEntityId,
			startDate.getTime(), endDate.getTime(),
			_SHA256_PREFIX + keyAlgorithm);

		_localEntityManager.storeLocalEntityCertificate(
			keyPair.getPrivate(), keystoreCredentialPassword, x509Certificate,
			certificateUsage);

		_samlProviderConfigurationHelper.updateProperties(unicodeProperties);

		actionRequest.setAttribute(
			SamlWebKeys.SAML_X509_CERTIFICATE, x509Certificate);
		actionResponse.setWindowState(LiferayWindowState.EXCLUSIVE);
	}

	private static final String _SHA256_PREFIX = "SHA256with";

	private static final Log _log = LogFactoryUtil.getLog(
		UpdateCertificateMVCActionCommand.class);

	@Reference
	private CertificateTool _certificateTool;

	@Reference
	private LocalEntityManager _localEntityManager;

	@Reference
	private SamlProviderConfigurationHelper _samlProviderConfigurationHelper;

}