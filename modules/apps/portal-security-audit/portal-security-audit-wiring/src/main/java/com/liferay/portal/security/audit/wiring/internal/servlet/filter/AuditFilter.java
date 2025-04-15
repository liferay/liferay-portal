/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.wiring.internal.servlet.filter;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.audit.AuditRequestThreadLocal;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogContext;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.BaseFilter;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.TryFilter;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.security.audit.wiring.internal.configuration.AuditLogContextConfiguration;

import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 * @author Brian Wing Shun Chan
 * @author Arthur Chan
 * @author Stian Sigvartsen
 */
@Component(
	configurationPid = "com.liferay.portal.security.audit.wiring.internal.configuration.AuditLogContextConfiguration",
	enabled = false,
	property = {
		"after-filter=Session Max Allowed Filter", "servlet-context-name=",
		"servlet-filter-name=Audit Filter", "url-pattern=/*",
		"url-regex-ignore-pattern=^/html/.+\\.(css|gif|html|ico|jpg|js|png)(\\?.*)?$"
	},
	service = Filter.class
)
public class AuditFilter extends BaseFilter implements TryFilter {

	@Override
	public Object doFilterTry(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		AuditRequestThreadLocal auditRequestThreadLocal =
			AuditRequestThreadLocal.getAuditThreadLocal();

		auditRequestThreadLocal.setClientHost(
			httpServletRequest.getRemoteHost());

		String remoteAddr = httpServletRequest.getRemoteAddr();

		auditRequestThreadLocal.setClientIP(remoteAddr);

		auditRequestThreadLocal.setQueryString(
			httpServletRequest.getQueryString());

		String userEmailAddress = StringPool.BLANK;

		HttpSession httpSession = httpServletRequest.getSession();

		Long userId = (Long)httpSession.getAttribute(WebKeys.USER_ID);

		String userLogin = StringPool.BLANK;

		if (userId != null) {
			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						_portal.getCompanyId(httpServletRequest))) {

				User user = _userLocalService.fetchUser(userId);

				if (user != null) {
					userEmailAddress = user.getEmailAddress();

					auditRequestThreadLocal.setRealUserEmailAddress(
						userEmailAddress);

					auditRequestThreadLocal.setRealUserId(userId);

					userLogin = _getUserLogin(user);

					auditRequestThreadLocal.setRealUserLogin(userLogin);
				}
			}
		}

		StringBuffer sb = httpServletRequest.getRequestURL();

		auditRequestThreadLocal.setRequestURL(sb.toString());

		auditRequestThreadLocal.setServerName(
			httpServletRequest.getServerName());
		auditRequestThreadLocal.setServerPort(
			httpServletRequest.getServerPort());
		auditRequestThreadLocal.setSessionID(httpSession.getId());

		if (!_auditLogContextConfiguration.enabled()) {
			return null;
		}

		String xRequestId = null;

		if (_auditLogContextConfiguration.useIncomingXRequestId()) {
			xRequestId = httpServletRequest.getHeader(HttpHeaders.X_REQUEST_ID);
		}

		if (!_isValidXRequestId(xRequestId)) {
			xRequestId = PortalUUIDUtil.generate();
		}

		httpServletResponse.setHeader(HttpHeaders.X_REQUEST_ID, xRequestId);

		_auditLogContext.setContext(
			remoteAddr, _portal.getCompanyId(httpServletRequest),
			httpServletRequest.getServerName(), userEmailAddress, userId,
			userLogin, xRequestId);

		return null;
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_auditLogContext = new AuditLogContext();

		_serviceRegistration = bundleContext.registerService(
			LogContext.class, _auditLogContext, new HashMapDictionary());

		_auditLogContextConfiguration = ConfigurableUtil.createConfigurable(
			AuditLogContextConfiguration.class, properties);
	}

	@Deactivate
	protected void deactivate() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	private String _getUserLogin(User user) {
		String authType = PrefsPropsUtil.getString(
			user.getCompanyId(), PropsKeys.COMPANY_SECURITY_AUTH_TYPE,
			StringPool.BLANK);

		if (authType.equals(CompanyConstants.AUTH_TYPE_EA)) {
			return user.getEmailAddress();
		}
		else if (authType.equals(CompanyConstants.AUTH_TYPE_ID)) {
			return String.valueOf(user.getUserId());
		}
		else if (authType.equals(CompanyConstants.AUTH_TYPE_SN)) {
			return user.getScreenName();
		}

		return StringPool.BLANK;
	}

	private boolean _isValidXRequestId(String xRequestId) {
		if (Validator.isBlank(xRequestId)) {
			if (_log.isDebugEnabled()) {
				_log.debug("Incoming X-Request-Id is empty");
			}

			return false;
		}

		if ((xRequestId.length() < 20) || (xRequestId.length() > 200)) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Incoming X-Request-Id has an invalid length: " +
						xRequestId.length());
			}

			return false;
		}

		for (int i = 0; i < xRequestId.length(); ++i) {
			if ((xRequestId.charAt(i) < 32) || (xRequestId.charAt(i) > 126)) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Incoming X-Request-Id contains invalid characters");
				}

				return false;
			}
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(AuditFilter.class);

	private AuditLogContext _auditLogContext;
	private AuditLogContextConfiguration _auditLogContextConfiguration;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private Portal _portal;

	private ServiceRegistration<LogContext> _serviceRegistration;

	@Reference
	private UserLocalService _userLocalService;

	private class AuditLogContext implements LogContext {

		public AuditLogContext() {
			_contexts = new CentralizedThreadLocal<>(
				AuditLogContext.class + "._contexts",
				HashMap<String, String>::new);
		}

		@Override
		public Map<String, String> getContext(String logName) {
			return _contexts.get();
		}

		@Override
		public String getName() {
			return AuditLogContext.class.getSimpleName();
		}

		public void setContext(
			String clientIP, long companyId, String serverName,
			String userEmailAddress, Long userId, String userLogin,
			String xRequestId) {

			_contexts.set(
				HashMapBuilder.put(
					"clientIP", clientIP
				).put(
					"companyId", String.valueOf(companyId)
				).put(
					"serverName", serverName
				).put(
					"userEmailAddress", userEmailAddress
				).put(
					"userId", (userId != null) ? String.valueOf(userId) : ""
				).put(
					"userLogin", userLogin
				).put(
					"virtualHostName",
					() -> {
						if (companyId >= 0) {
							Company company = _companyLocalService.fetchCompany(
								companyId);

							if (company != null) {
								return company.getVirtualHostname();
							}
						}

						return "";
					}
				).put(
					"xRequestId", xRequestId
				).build());
		}

		private final ThreadLocal<Map<String, String>> _contexts;

	}

}