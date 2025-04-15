/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.email.otp.web.internal.checker;

import com.liferay.multi.factor.authentication.email.otp.configuration.MFAEmailOTPConfiguration;
import com.liferay.multi.factor.authentication.email.otp.model.MFAEmailOTPEntry;
import com.liferay.multi.factor.authentication.email.otp.service.MFAEmailOTPEntryLocalService;
import com.liferay.multi.factor.authentication.email.otp.web.internal.constants.MFAEmailOTPEventTypes;
import com.liferay.multi.factor.authentication.email.otp.web.internal.constants.MFAEmailOTPWebKeys;
import com.liferay.multi.factor.authentication.spi.checker.browser.BrowserMFAChecker;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.audit.AuditException;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouterUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Arthur Chan
 * @author Marta Medio
 */
@Component(
	configurationPid = "com.liferay.multi.factor.authentication.email.otp.configuration.MFAEmailOTPConfiguration.scoped",
	service = BrowserMFAChecker.class
)
public class EmailOTPBrowserMFAChecker implements BrowserMFAChecker {

	@Override
	public void includeBrowserVerification(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, long userId)
		throws IOException, ServletException {

		User user = _userLocalService.fetchUser(userId);

		if (user == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Requested one-time password email verification for " +
						"nonexistent user " + userId);
			}

			return;
		}

		if (_isMaximumAllowedAttemptsReached(user.getUserId())) {
			httpServletRequest.setAttribute(
				MFAEmailOTPWebKeys.MFA_EMAIL_OTP_FAILED_ATTEMPTS_RETRY_TIMEOUT,
				_mfaEmailOTPConfiguration.retryTimeout());
		}

		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		HttpSession httpSession = originalHttpServletRequest.getSession();

		Long mfaEmailOTPUserId = (Long)httpSession.getAttribute(
			MFAEmailOTPWebKeys.MFA_EMAIL_OTP_USER_ID);

		if ((mfaEmailOTPUserId != null) && (mfaEmailOTPUserId != userId)) {
			httpSession.removeAttribute(MFAEmailOTPWebKeys.MFA_EMAIL_OTP_PHASE);
			httpSession.removeAttribute(
				MFAEmailOTPWebKeys.MFA_EMAIL_OTP_SET_AT_TIME);
			httpSession.removeAttribute(
				MFAEmailOTPWebKeys.MFA_EMAIL_OTP_USER_ID);
		}

		httpServletRequest.setAttribute(
			MFAEmailOTPWebKeys.MFA_EMAIL_OTP_SEND_TO_ADDRESS_OBFUSCATED,
			obfuscateEmailAddress(user.getEmailAddress()));
		httpServletRequest.setAttribute(
			MFAEmailOTPWebKeys.MFA_EMAIL_OTP_SET_AT_TIME,
			GetterUtil.getLong(
				httpSession.getAttribute(
					MFAEmailOTPWebKeys.MFA_EMAIL_OTP_SET_AT_TIME),
				Long.MIN_VALUE));

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher(
				"/mfa_email_otp_checker/verify_browser.jsp");

		requestDispatcher.include(httpServletRequest, httpServletResponse);

		httpSession.setAttribute(
			MFAEmailOTPWebKeys.MFA_EMAIL_OTP_PHASE, "verify");
		httpSession.setAttribute(
			MFAEmailOTPWebKeys.MFA_EMAIL_OTP_USER_ID, userId);
	}

	@Override
	public boolean isBrowserVerified(
		HttpServletRequest httpServletRequest, long userId) {

		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		HttpSession httpSession = originalHttpServletRequest.getSession(false);

		if (_isVerified(httpSession, userId)) {
			return true;
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
					"Requested one-time password email verification for " +
						"nonexistent user " + userId);
			}

			_mfaEmailOTPAuditMessageBuilder.routeAuditMessage(
				_mfaEmailOTPAuditMessageBuilder.
					buildNonexistentUserVerificationFailureAuditMessage(
						CompanyThreadLocal.getCompanyId(), userId,
						_getClassName()));

			return false;
		}

		MFAEmailOTPEntry mfaEmailOTPEntry =
			_mfaEmailOTPEntryLocalService.fetchMFAEmailOTPEntryByUserId(userId);

		if (mfaEmailOTPEntry == null) {
			_mfaEmailOTPEntryLocalService.addMFAEmailOTPEntry(userId);
		}

		if (_isMaximumAllowedAttemptsReached(userId)) {
			_mfaEmailOTPAuditMessageBuilder.routeAuditMessage(
				_mfaEmailOTPAuditMessageBuilder.
					buildVerificationFailureAuditMessage(
						user, _getClassName(),
						"Reached maximum allowed attempts"));

			return false;
		}

		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		HttpSession httpSession = originalHttpServletRequest.getSession();

		String otp = ParamUtil.getString(httpServletRequest, "otp");

		if (_verify(httpSession, otp)) {
			httpSession.setAttribute(
				MFAEmailOTPWebKeys.MFA_EMAIL_OTP_VALIDATED_AT_TIME,
				System.currentTimeMillis());
			httpSession.setAttribute(
				MFAEmailOTPWebKeys.MFA_EMAIL_OTP_VALIDATED_USER_ID, userId);

			_mfaEmailOTPEntryLocalService.updateAttempts(
				userId, originalHttpServletRequest.getRemoteAddr(), true);

			_mfaEmailOTPAuditMessageBuilder.routeAuditMessage(
				_mfaEmailOTPAuditMessageBuilder.
					buildVerificationSuccessAuditMessage(
						user, _getClassName()));

			return true;
		}

		_mfaEmailOTPAuditMessageBuilder.routeAuditMessage(
			_mfaEmailOTPAuditMessageBuilder.
				buildVerificationFailureAuditMessage(
					user, _getClassName(),
					"Incorrect email one-time password"));

		_mfaEmailOTPEntryLocalService.updateAttempts(
			userId, originalHttpServletRequest.getRemoteAddr(), false);

		return false;
	}

	protected static String obfuscateEmailAddress(String emailAddress) {
		String alias = emailAddress.substring(0, emailAddress.indexOf('@'));

		int maskLength = Math.max(
			(int)Math.ceil(alias.length() / 2.0), Math.min(3, alias.length()));

		int startIndex = (int)Math.ceil((alias.length() - maskLength) / 2.0);

		int endIndex = startIndex + maskLength;

		char[] chars = emailAddress.toCharArray();

		for (int i = startIndex; i < endIndex; i++) {
			chars[i] = '*';
		}

		return new String(chars);
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_mfaEmailOTPConfiguration = ConfigurableUtil.createConfigurable(
			MFAEmailOTPConfiguration.class, properties);

		if (!PropsValues.SESSION_ENABLE_PHISHING_PROTECTION) {
			return;
		}

		List<String> sessionPhishingProtectedAttributes = new ArrayList<>(
			Arrays.asList(PropsValues.SESSION_PHISHING_PROTECTED_ATTRIBUTES));

		sessionPhishingProtectedAttributes.add(
			MFAEmailOTPWebKeys.MFA_EMAIL_OTP_VALIDATED_AT_TIME);
		sessionPhishingProtectedAttributes.add(
			MFAEmailOTPWebKeys.MFA_EMAIL_OTP_VALIDATED_USER_ID);

		PropsValues.SESSION_PHISHING_PROTECTED_ATTRIBUTES =
			sessionPhishingProtectedAttributes.toArray(new String[0]);
	}

	@Deactivate
	protected void deactivate() {
		if (!PropsValues.SESSION_ENABLE_PHISHING_PROTECTION) {
			return;
		}

		List<String> sessionPhishingProtectedAttributes = new ArrayList<>(
			Arrays.asList(PropsValues.SESSION_PHISHING_PROTECTED_ATTRIBUTES));

		sessionPhishingProtectedAttributes.remove(
			MFAEmailOTPWebKeys.MFA_EMAIL_OTP_VALIDATED_AT_TIME);
		sessionPhishingProtectedAttributes.remove(
			MFAEmailOTPWebKeys.MFA_EMAIL_OTP_VALIDATED_USER_ID);

		PropsValues.SESSION_PHISHING_PROTECTED_ATTRIBUTES =
			sessionPhishingProtectedAttributes.toArray(new String[0]);
	}

	private String _getClassName() {
		Class<?> clazz = getClass();

		return clazz.getName();
	}

	private boolean _isMaximumAllowedAttemptsReached(long userId) {
		try {
			MFAEmailOTPEntry mfaEmailOTPEntry =
				_mfaEmailOTPEntryLocalService.fetchMFAEmailOTPEntryByUserId(
					userId);

			if (mfaEmailOTPEntry == null) {
				return false;
			}

			if ((_mfaEmailOTPConfiguration.failedAttemptsAllowed() >= 0) &&
				(_mfaEmailOTPConfiguration.failedAttemptsAllowed() <=
					mfaEmailOTPEntry.getFailedAttempts()) &&
				(_mfaEmailOTPConfiguration.retryTimeout() >= 0)) {

				Date lastFailDate = mfaEmailOTPEntry.getLastFailDate();

				long time =
					(_mfaEmailOTPConfiguration.retryTimeout() * Time.SECOND) +
						lastFailDate.getTime();

				if (time <= System.currentTimeMillis()) {
					_mfaEmailOTPEntryLocalService.resetFailedAttempts(userId);
				}
				else {
					return true;
				}
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return false;
	}

	private boolean _isVerified(HttpSession httpSession, long userId) {
		User user = _userLocalService.fetchUser(userId);

		if (user == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Requested one-time password email verification for " +
						"nonexistent user " + userId);
			}

			_mfaEmailOTPAuditMessageBuilder.routeAuditMessage(
				_mfaEmailOTPAuditMessageBuilder.
					buildNonexistentUserVerificationFailureAuditMessage(
						CompanyThreadLocal.getCompanyId(), userId,
						_getClassName()));

			return false;
		}

		if (httpSession == null) {
			_mfaEmailOTPAuditMessageBuilder.routeAuditMessage(
				_mfaEmailOTPAuditMessageBuilder.buildNotVerifiedAuditMessage(
					user, _getClassName(), "Empty session"));

			return false;
		}

		Object mfaEmailOTPValidatedUserId = httpSession.getAttribute(
			MFAEmailOTPWebKeys.MFA_EMAIL_OTP_VALIDATED_USER_ID);

		if (mfaEmailOTPValidatedUserId == null) {
			_mfaEmailOTPAuditMessageBuilder.routeAuditMessage(
				_mfaEmailOTPAuditMessageBuilder.buildNotVerifiedAuditMessage(
					user, _getClassName(), "Not verified yet"));

			return false;
		}

		if (!Objects.equals(mfaEmailOTPValidatedUserId, userId)) {
			_mfaEmailOTPAuditMessageBuilder.routeAuditMessage(
				_mfaEmailOTPAuditMessageBuilder.buildNotVerifiedAuditMessage(
					user, _getClassName(), "Not the same user"));

			return false;
		}

		return true;
	}

	private boolean _verify(HttpSession httpSession, String otp) {
		String expectedMFAEmailOTP = (String)httpSession.getAttribute(
			MFAEmailOTPWebKeys.MFA_EMAIL_OTP);

		if ((expectedMFAEmailOTP == null) || !expectedMFAEmailOTP.equals(otp)) {
			return false;
		}

		httpSession.removeAttribute(MFAEmailOTPWebKeys.MFA_EMAIL_OTP);
		httpSession.removeAttribute(MFAEmailOTPWebKeys.MFA_EMAIL_OTP_PHASE);
		httpSession.removeAttribute(
			MFAEmailOTPWebKeys.MFA_EMAIL_OTP_SET_AT_TIME);
		httpSession.removeAttribute(MFAEmailOTPWebKeys.MFA_EMAIL_OTP_USER_ID);

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EmailOTPBrowserMFAChecker.class);

	private final MFAEmailOTPAuditMessageBuilder
		_mfaEmailOTPAuditMessageBuilder = new MFAEmailOTPAuditMessageBuilder();
	private MFAEmailOTPConfiguration _mfaEmailOTPConfiguration;

	@Reference
	private MFAEmailOTPEntryLocalService _mfaEmailOTPEntryLocalService;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.multi.factor.authentication.email.otp.web)"
	)
	private ServletContext _servletContext;

	@Reference
	private UserLocalService _userLocalService;

	private class MFAEmailOTPAuditMessageBuilder {

		public AuditMessage buildNonexistentUserVerificationFailureAuditMessage(
			long companyId, long userId, String checkerClassName) {

			return new AuditMessage(
				MFAEmailOTPEventTypes.MFA_EMAIL_OTP_VERIFICATION_FAILURE,
				companyId, userId, "Nonexistent", checkerClassName,
				String.valueOf(userId), null,
				JSONUtil.put("reason", "Nonexistent User"));
		}

		public AuditMessage buildNotVerifiedAuditMessage(
			User user, String checkerClassName, String reason) {

			return new AuditMessage(
				MFAEmailOTPEventTypes.MFA_EMAIL_OTP_NOT_VERIFIED,
				user.getCompanyId(), user.getUserId(), user.getFullName(),
				checkerClassName, String.valueOf(user.getPrimaryKey()), null,
				JSONUtil.put("reason", reason));
		}

		public AuditMessage buildVerificationFailureAuditMessage(
			User user, String checkerClassName, String reason) {

			return new AuditMessage(
				MFAEmailOTPEventTypes.MFA_EMAIL_OTP_VERIFICATION_FAILURE,
				user.getCompanyId(), user.getUserId(), user.getFullName(),
				checkerClassName, String.valueOf(user.getPrimaryKey()), null,
				JSONUtil.put("reason", reason));
		}

		public AuditMessage buildVerificationSuccessAuditMessage(
			User user, String checkerClassName) {

			return new AuditMessage(
				MFAEmailOTPEventTypes.MFA_EMAIL_OTP_VERIFICATION_SUCCESS,
				user.getCompanyId(), user.getUserId(), user.getFullName(),
				checkerClassName, String.valueOf(user.getPrimaryKey()), null,
				null);
		}

		public AuditMessage buildVerifiedAuditMessage(
			User user, String checkerClassName) {

			return new AuditMessage(
				MFAEmailOTPEventTypes.MFA_EMAIL_OTP_VERIFIED,
				user.getCompanyId(), user.getUserId(), user.getFullName(),
				checkerClassName, String.valueOf(user.getPrimaryKey()), null,
				null);
		}

		public void routeAuditMessage(AuditMessage auditMessage) {
			try {
				AuditRouterUtil.route(auditMessage);
			}
			catch (AuditException auditException) {
				if (_log.isWarnEnabled()) {
					_log.warn("Unable to route audit message", auditException);
				}
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		private final Log _log = LogFactoryUtil.getLog(
			MFAEmailOTPAuditMessageBuilder.class);

	}

}