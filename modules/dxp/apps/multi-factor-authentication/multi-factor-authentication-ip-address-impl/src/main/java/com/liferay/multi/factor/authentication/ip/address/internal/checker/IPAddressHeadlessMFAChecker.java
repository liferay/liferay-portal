/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.ip.address.internal.checker;

import com.liferay.multi.factor.authentication.ip.address.internal.configuration.MFAIPAddressConfiguration;
import com.liferay.multi.factor.authentication.ip.address.internal.constants.MFAIPAddressEventTypes;
import com.liferay.multi.factor.authentication.spi.checker.headless.HeadlessMFAChecker;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.audit.AuditException;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouterUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.access.control.AccessControlUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.HashMapDictionary;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marta Medio
 */
@Component(
	configurationPid = "com.liferay.multi.factor.authentication.ip.address.internal.configuration.MFAIPAddressConfiguration.scoped",
	service = {}
)
public class IPAddressHeadlessMFAChecker implements HeadlessMFAChecker {

	@Override
	public boolean verifyHeadlessRequest(
		HttpServletRequest httpServletRequest, long userId) {

		User user = _userLocalService.fetchUser(userId);

		if (user == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Requested IP address verification for nonexistent user " +
						userId);
			}

			_mfaipAddressAuditMessageBuilder.routeAuditMessage(
				_mfaipAddressAuditMessageBuilder.
					buildNonexistentUserVerificationFailureAuditMessage(
						CompanyThreadLocal.getCompanyId(), userId,
						_getClassName()));

			return false;
		}

		if (AccessControlUtil.isAccessAllowed(
				httpServletRequest, _allowedIpAddressesAndNetmasks)) {

			_mfaipAddressAuditMessageBuilder.routeAuditMessage(
				_mfaipAddressAuditMessageBuilder.
					buildVerificationSuccessAuditMessage(
						user, _getClassName()));

			return true;
		}

		_mfaipAddressAuditMessageBuilder.routeAuditMessage(
			_mfaipAddressAuditMessageBuilder.
				buildVerificationFailureAuditMessage(
					user, _getClassName(), "IP is not allowed"));

		return false;
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		MFAIPAddressConfiguration mfaIPAddressConfiguration =
			ConfigurableUtil.createConfigurable(
				MFAIPAddressConfiguration.class, properties);

		if (!mfaIPAddressConfiguration.enabled()) {
			return;
		}

		_allowedIpAddressesAndNetmasks = new HashSet<>(
			Arrays.asList(
				mfaIPAddressConfiguration.allowedIPAddressAndNetMask()));

		_serviceRegistration = bundleContext.registerService(
			HeadlessMFAChecker.class, this,
			new HashMapDictionary<>(properties));
	}

	@Deactivate
	protected void deactivate() {
		if (_serviceRegistration == null) {
			return;
		}

		_serviceRegistration.unregister();
	}

	private String _getClassName() {
		Class<?> clazz = getClass();

		return clazz.getName();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		IPAddressHeadlessMFAChecker.class);

	private Set<String> _allowedIpAddressesAndNetmasks;
	private final MFAIPAddressAuditMessageBuilder
		_mfaipAddressAuditMessageBuilder =
			new MFAIPAddressAuditMessageBuilder();
	private ServiceRegistration<HeadlessMFAChecker> _serviceRegistration;

	@Reference
	private UserLocalService _userLocalService;

	private class MFAIPAddressAuditMessageBuilder {

		public AuditMessage buildNonexistentUserVerificationFailureAuditMessage(
			long companyId, long userId, String mfaCheckerClassName) {

			return new AuditMessage(
				MFAIPAddressEventTypes.MFA_IP_ADDRESS_VERIFICATION_FAILURE,
				companyId, userId, "Nonexistent", mfaCheckerClassName,
				String.valueOf(userId), null,
				JSONUtil.put("reason", "Nonexistent User"));
		}

		public AuditMessage buildVerificationFailureAuditMessage(
			User user, String mfaCheckerClassName, String reason) {

			return new AuditMessage(
				MFAIPAddressEventTypes.MFA_IP_ADDRESS_VERIFICATION_FAILURE,
				user.getCompanyId(), user.getUserId(), user.getFullName(),
				mfaCheckerClassName, String.valueOf(user.getPrimaryKey()), null,
				JSONUtil.put("reason", reason));
		}

		public AuditMessage buildVerificationSuccessAuditMessage(
			User user, String mfaCheckerClassName) {

			return new AuditMessage(
				MFAIPAddressEventTypes.MFA_IP_ADDRESS_VERIFICATION_SUCCESS,
				user.getCompanyId(), user.getUserId(), user.getFullName(),
				mfaCheckerClassName, String.valueOf(user.getPrimaryKey()), null,
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
			MFAIPAddressAuditMessageBuilder.class);

	}

}