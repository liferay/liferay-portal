/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.fido2.web.internal.checker;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import com.liferay.multi.factor.authentication.fido2.credential.model.MFAFIDO2CredentialEntry;
import com.liferay.multi.factor.authentication.fido2.credential.service.MFAFIDO2CredentialEntryLocalService;
import com.liferay.multi.factor.authentication.fido2.web.internal.audit.MFAFIDO2AuditMessageBuilder;
import com.liferay.multi.factor.authentication.fido2.web.internal.configuration.MFAFIDO2Configuration;
import com.liferay.multi.factor.authentication.fido2.web.internal.constants.MFAFIDO2WebKeys;
import com.liferay.multi.factor.authentication.fido2.web.internal.util.ConvertUtil;
import com.liferay.multi.factor.authentication.fido2.web.internal.yubico.webauthn.MFAFIDO2CredentialRepository;
import com.liferay.multi.factor.authentication.spi.checker.browser.BrowserMFAChecker;
import com.liferay.multi.factor.authentication.spi.checker.setup.SetupMFAChecker;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.util.PropsValues;

import com.yubico.webauthn.AssertionRequest;
import com.yubico.webauthn.AssertionResult;
import com.yubico.webauthn.FinishAssertionOptions;
import com.yubico.webauthn.FinishRegistrationOptions;
import com.yubico.webauthn.RegistrationResult;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.StartAssertionOptions;
import com.yubico.webauthn.StartRegistrationOptions;
import com.yubico.webauthn.data.AuthenticatorAssertionResponse;
import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.ClientAssertionExtensionOutputs;
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import com.yubico.webauthn.data.RelyingPartyIdentity;
import com.yubico.webauthn.data.UserIdentity;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Arthur Chan
 */
@Component(
	configurationPid = "com.liferay.multi.factor.authentication.fido2.web.internal.configuration.MFAFIDO2Configuration.scoped",
	configurationPolicy = ConfigurationPolicy.REQUIRE, service = {}
)
public class FIDO2BrowserSetupMFAChecker
	implements BrowserMFAChecker, SetupMFAChecker {

	@Override
	public void includeBrowserVerification(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, long userId)
		throws Exception {

		String mfaFIDO2AssertionRequest = _objectMapper.writeValueAsString(
			_getAssertionRequest(userId));

		httpServletRequest.setAttribute(
			MFAFIDO2WebKeys.MFA_FIDO2_ASSERTION_REQUEST,
			mfaFIDO2AssertionRequest);

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher(
				"/mfa_fido2_checker/verify_browser.jsp");

		requestDispatcher.include(httpServletRequest, httpServletResponse);

		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		HttpSession httpSession = originalHttpServletRequest.getSession();

		httpSession.setAttribute(
			MFAFIDO2WebKeys.MFA_FIDO2_ASSERTION_REQUEST,
			mfaFIDO2AssertionRequest);
	}

	@Override
	public void includeSetup(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, long userId)
		throws Exception {

		List<MFAFIDO2CredentialEntry> mfaFIDO2CredentialEntries =
			_mfaFIDO2CredentialEntryLocalService.
				getMFAFIDO2CredentialEntriesByUserId(userId);

		if (mfaFIDO2CredentialEntries.size() <
				_mfaFIDO2Configuration.allowedCredentialsPerUser()) {

			String mfaFIDO2PKCCOptions = _objectMapper.writeValueAsString(
				_getPublicKeyCredentialCreationOptions(userId));

			httpServletRequest.setAttribute(
				MFAFIDO2WebKeys.MFA_FIDO2_PKCC_OPTIONS, mfaFIDO2PKCCOptions);

			HttpServletRequest originalHttpServletRequest =
				_portal.getOriginalServletRequest(httpServletRequest);

			HttpSession httpSession = originalHttpServletRequest.getSession();

			httpSession.setAttribute(
				MFAFIDO2WebKeys.MFA_FIDO2_PKCC_OPTIONS, mfaFIDO2PKCCOptions);
		}

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher(
				"/mfa_fido2_checker/setup.jsp");

		requestDispatcher.include(httpServletRequest, httpServletResponse);
	}

	@Override
	public boolean isAvailable(long userId) {
		List<MFAFIDO2CredentialEntry> mfaFIDO2CredentialEntries =
			_mfaFIDO2CredentialEntryLocalService.
				getMFAFIDO2CredentialEntriesByUserId(userId);

		if (!mfaFIDO2CredentialEntries.isEmpty()) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isBrowserVerified(
		HttpServletRequest httpServletRequest, long userId) {

		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		if (_isVerified(originalHttpServletRequest.getSession(false), userId)) {
			return true;
		}

		return false;
	}

	@Override
	public void removeExistingSetup(long userId) {
		List<MFAFIDO2CredentialEntry> mfaFIDO2CredentialEntries =
			_mfaFIDO2CredentialEntryLocalService.
				getMFAFIDO2CredentialEntriesByUserId(userId);

		for (MFAFIDO2CredentialEntry mfaFIDO2CredentialEntry :
				mfaFIDO2CredentialEntries) {

			if (mfaFIDO2CredentialEntry != null) {
				_mfaFIDO2CredentialEntryLocalService.
					deleteMFAFIDO2CredentialEntry(mfaFIDO2CredentialEntry);
			}
		}
	}

	@Override
	public boolean setUp(HttpServletRequest httpServletRequest, long userId) {
		try {
			RegistrationResult registrationResult = _getRegistrationResult(
				httpServletRequest);

			PublicKeyCredentialDescriptor publicKeyCredentialDescriptor =
				registrationResult.getKeyId();

			ByteArray credentialIdByteArray =
				publicKeyCredentialDescriptor.getId();

			ByteArray publicKeyCOSEByteArray =
				registrationResult.getPublicKeyCose();

			_mfaFIDO2CredentialEntryLocalService.addMFAFIDO2CredentialEntry(
				userId, credentialIdByteArray.getBase64(), 0,
				publicKeyCOSEByteArray.getBase64());

			return true;
		}
		catch (Exception exception) {
			_log.error(
				StringBundler.concat(
					"Unable to setup FIDO2 for user ", userId, ": ",
					exception.getMessage()),
				exception);
		}

		return false;
	}

	@Override
	public boolean verifyBrowserRequest(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, long userId)
		throws Exception {

		User user = _userLocalService.fetchUser(userId);

		if (user == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Requested FIDO2 verification for nonexistent user " +
						userId);
			}

			MFAFIDO2AuditMessageBuilder mfaFIDO2AuditMessageBuilder =
				_mfaFIDO2AuditMessageBuilderSnapshot.get();

			if (mfaFIDO2AuditMessageBuilder != null) {
				mfaFIDO2AuditMessageBuilder.routeAuditMessage(
					mfaFIDO2AuditMessageBuilder.
						buildNonexistentUserVerificationFailureAuditMessage(
							CompanyThreadLocal.getCompanyId(), userId,
							_getClassName()));
			}

			return false;
		}

		if (!isAvailable(user.getUserId())) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Requested FIDO2 verification for user " + userId +
						" with incomplete configuration");
			}

			MFAFIDO2AuditMessageBuilder mfaFIDO2AuditMessageBuilder =
				_mfaFIDO2AuditMessageBuilderSnapshot.get();

			if (mfaFIDO2AuditMessageBuilder != null) {
				mfaFIDO2AuditMessageBuilder.routeAuditMessage(
					mfaFIDO2AuditMessageBuilder.
						buildUnconfiguredUserVerificationFailureAuditMessage(
							CompanyThreadLocal.getCompanyId(), user,
							_getClassName()));
			}

			return false;
		}

		try {
			AssertionResult assertionResult = _getAssertionResult(
				httpServletRequest);

			ByteArray credentialIdByteArray = assertionResult.getCredentialId();

			if (!assertionResult.isSuccess()) {
				_mfaFIDO2CredentialEntryLocalService.updateAttempts(
					userId, credentialIdByteArray.getBase64(), 0);

				MFAFIDO2AuditMessageBuilder mfaFIDO2AuditMessageBuilder =
					_mfaFIDO2AuditMessageBuilderSnapshot.get();

				if (mfaFIDO2AuditMessageBuilder != null) {
					mfaFIDO2AuditMessageBuilder.routeAuditMessage(
						mfaFIDO2AuditMessageBuilder.
							buildVerificationFailureAuditMessage(
								user, _getClassName(),
								"Incorrect FIDO2 verification"));
				}

				return false;
			}

			_mfaFIDO2CredentialEntryLocalService.updateAttempts(
				userId, credentialIdByteArray.getBase64(),
				assertionResult.getSignatureCount());
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return false;
		}

		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		HttpSession httpSession = originalHttpServletRequest.getSession();

		httpSession.setAttribute(
			MFAFIDO2WebKeys.MFA_FIDO2_VALIDATED_AT_TIME,
			System.currentTimeMillis());
		httpSession.setAttribute(
			MFAFIDO2WebKeys.MFA_FIDO2_VALIDATED_USER_ID, userId);

		MFAFIDO2AuditMessageBuilder mfaFIDO2AuditMessageBuilder =
			_mfaFIDO2AuditMessageBuilderSnapshot.get();

		if (mfaFIDO2AuditMessageBuilder != null) {
			mfaFIDO2AuditMessageBuilder.routeAuditMessage(
				mfaFIDO2AuditMessageBuilder.buildVerifiedAuditMessage(
					user, _getClassName()));
		}

		return true;
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_mfaFIDO2Configuration = ConfigurableUtil.createConfigurable(
			MFAFIDO2Configuration.class, properties);

		_objectMapper = new ObjectMapper();

		_objectMapper = _objectMapper.configure(
			SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		_objectMapper = _objectMapper.registerModule(new Jdk8Module());
		_objectMapper = _objectMapper.setSerializationInclusion(
			JsonInclude.Include.NON_ABSENT);

		_relyingParty = RelyingParty.builder(
		).identity(
			RelyingPartyIdentity.builder(
			).id(
				_mfaFIDO2Configuration.relyingPartyId()
			).name(
				_mfaFIDO2Configuration.relyingPartyName()
			).build()
		).credentialRepository(
			new MFAFIDO2CredentialRepository(
				_mfaFIDO2CredentialEntryLocalService, _userLocalService)
		).origins(
			SetUtil.fromArray(_mfaFIDO2Configuration.origins())
		).allowOriginPort(
			_mfaFIDO2Configuration.allowOriginPort()
		).allowOriginSubdomain(
			_mfaFIDO2Configuration.allowOriginSubdomain()
		).build();

		if (PropsValues.SESSION_ENABLE_PHISHING_PROTECTION) {
			List<String> sessionPhishingProtectedAttributes = new ArrayList<>(
				Arrays.asList(
					PropsValues.SESSION_PHISHING_PROTECTED_ATTRIBUTES));

			sessionPhishingProtectedAttributes.add(
				MFAFIDO2WebKeys.MFA_FIDO2_VALIDATED_AT_TIME);
			sessionPhishingProtectedAttributes.add(
				MFAFIDO2WebKeys.MFA_FIDO2_VALIDATED_USER_ID);

			PropsValues.SESSION_PHISHING_PROTECTED_ATTRIBUTES =
				sessionPhishingProtectedAttributes.toArray(new String[0]);
		}

		_serviceRegistration = bundleContext.registerService(
			new String[] {
				BrowserMFAChecker.class.getName(),
				SetupMFAChecker.class.getName()
			},
			this, new HashMapDictionary<>(properties));
	}

	@Deactivate
	protected void deactivate() {
		if (_serviceRegistration == null) {
			return;
		}

		_serviceRegistration.unregister();

		if (PropsValues.SESSION_ENABLE_PHISHING_PROTECTION) {
			List<String> sessionPhishingProtectedAttributes = new ArrayList<>(
				Arrays.asList(
					PropsValues.SESSION_PHISHING_PROTECTED_ATTRIBUTES));

			sessionPhishingProtectedAttributes.remove(
				MFAFIDO2WebKeys.MFA_FIDO2_VALIDATED_AT_TIME);
			sessionPhishingProtectedAttributes.remove(
				MFAFIDO2WebKeys.MFA_FIDO2_VALIDATED_USER_ID);

			PropsValues.SESSION_PHISHING_PROTECTED_ATTRIBUTES =
				sessionPhishingProtectedAttributes.toArray(new String[0]);
		}
	}

	private AssertionRequest _getAssertionRequest(long userId)
		throws Exception {

		User user = _userLocalService.getUserById(userId);

		return _relyingParty.startAssertion(
			StartAssertionOptions.builder(
			).username(
				user.getScreenName()
			).build());
	}

	private AssertionResult _getAssertionResult(
			HttpServletRequest httpServletRequest)
		throws Exception {

		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		HttpSession httpSession = originalHttpServletRequest.getSession();

		return _relyingParty.finishAssertion(
			FinishAssertionOptions.builder(
			).request(
				_objectMapper.readValue(
					GetterUtil.getString(
						httpSession.getAttribute(
							MFAFIDO2WebKeys.MFA_FIDO2_ASSERTION_REQUEST)),
					AssertionRequest.class)
			).response(
				_objectMapper.readValue(
					ParamUtil.getString(httpServletRequest, "responseJSON"),
					new TypeReference
						<PublicKeyCredential
							<AuthenticatorAssertionResponse,
							 ClientAssertionExtensionOutputs>>() {
					})
			).build());
	}

	private String _getClassName() {
		Class<?> clazz = getClass();

		return clazz.getName();
	}

	private PublicKeyCredentialCreationOptions
		_getPublicKeyCredentialCreationOptions(long userId) {

		User user = _userLocalService.fetchUserById(userId);

		return _relyingParty.startRegistration(
			StartRegistrationOptions.builder(
			).user(
				UserIdentity.builder(
				).name(
					user.getScreenName()
				).displayName(
					user.getFullName()
				).id(
					ConvertUtil.toByteArray(userId)
				).build()
			).build());
	}

	private RegistrationResult _getRegistrationResult(
			HttpServletRequest httpServletRequest)
		throws Exception {

		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		HttpSession httpSession = originalHttpServletRequest.getSession();

		return _relyingParty.finishRegistration(
			FinishRegistrationOptions.builder(
			).request(
				_objectMapper.readValue(
					GetterUtil.getString(
						httpSession.getAttribute(
							MFAFIDO2WebKeys.MFA_FIDO2_PKCC_OPTIONS)),
					PublicKeyCredentialCreationOptions.class)
			).response(
				_objectMapper.readValue(
					ParamUtil.getString(httpServletRequest, "responseJSON"),
					new TypeReference
						<PublicKeyCredential
							<AuthenticatorAttestationResponse,
							 ClientRegistrationExtensionOutputs>>() {
					})
			).build());
	}

	private boolean _isVerified(HttpSession httpSession, long userId) {
		User user = _userLocalService.fetchUser(userId);

		if (user == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Requested FIDO2 verification for nonexistent user " +
						userId);
			}

			MFAFIDO2AuditMessageBuilder mfaFIDO2AuditMessageBuilder =
				_mfaFIDO2AuditMessageBuilderSnapshot.get();

			if (mfaFIDO2AuditMessageBuilder != null) {
				mfaFIDO2AuditMessageBuilder.routeAuditMessage(
					mfaFIDO2AuditMessageBuilder.
						buildNonexistentUserVerificationFailureAuditMessage(
							CompanyThreadLocal.getCompanyId(), userId,
							_getClassName()));
			}

			return false;
		}

		if (httpSession == null) {
			MFAFIDO2AuditMessageBuilder mfaFIDO2AuditMessageBuilder =
				_mfaFIDO2AuditMessageBuilderSnapshot.get();

			if (mfaFIDO2AuditMessageBuilder != null) {
				mfaFIDO2AuditMessageBuilder.routeAuditMessage(
					mfaFIDO2AuditMessageBuilder.buildNotVerifiedAuditMessage(
						user, _getClassName(), "Empty session"));
			}

			return false;
		}

		return Objects.equals(
			httpSession.getAttribute(
				MFAFIDO2WebKeys.MFA_FIDO2_VALIDATED_USER_ID),
			userId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FIDO2BrowserSetupMFAChecker.class);

	private static final Snapshot<MFAFIDO2AuditMessageBuilder>
		_mfaFIDO2AuditMessageBuilderSnapshot = new Snapshot<>(
			FIDO2BrowserSetupMFAChecker.class,
			MFAFIDO2AuditMessageBuilder.class);

	private MFAFIDO2Configuration _mfaFIDO2Configuration;

	@Reference
	private MFAFIDO2CredentialEntryLocalService
		_mfaFIDO2CredentialEntryLocalService;

	private ObjectMapper _objectMapper;

	@Reference
	private Portal _portal;

	private RelyingParty _relyingParty;
	private ServiceRegistration<?> _serviceRegistration;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.multi.factor.authentication.fido2.web)"
	)
	private ServletContext _servletContext;

	@Reference
	private UserLocalService _userLocalService;

}