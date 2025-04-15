/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.events.EventsProcessorUtil;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.cookies.constants.CookiesConstants;
import com.liferay.portal.kernel.exception.NoSuchVirtualHostException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.VirtualHost;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.service.VirtualHostLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.initializer.kernel.util.SiteInitializerThreadLocal;

import jakarta.servlet.http.HttpServletRequest;

import java.sql.SQLException;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Brian Wing Shun Chan
 * @author Jose Oliver
 * @author Atul Patel
 * @author Mika Koivisto
 */
public class PortalInstances {

	public static Company addCompany(
			String siteInitializerKey,
			UnsafeSupplier<Company, PortalException> unsafeSupplier)
		throws PortalException {

		try (SafeCloseable safeCloseable =
				SiteInitializerThreadLocal.setKeyWithSafeCloseable(
					siteInitializerKey)) {

			return unsafeSupplier.get();
		}
	}

	public static long getCompanyId(HttpServletRequest httpServletRequest) {
		try {
			return getCompanyId(httpServletRequest, false);
		}
		catch (NoSuchVirtualHostException noSuchVirtualHostException) {
			_log.error(noSuchVirtualHostException);

			return 0;
		}
	}

	public static long getCompanyId(
			HttpServletRequest httpServletRequest, boolean strict)
		throws NoSuchVirtualHostException {

		if (_log.isDebugEnabled()) {
			_log.debug("Get company ID");
		}

		Long companyIdObj = (Long)httpServletRequest.getAttribute(
			WebKeys.COMPANY_ID);

		if (_log.isDebugEnabled()) {
			_log.debug("Company ID from request " + companyIdObj);
		}

		if (companyIdObj != null) {
			return companyIdObj.longValue();
		}

		long companyId = _getCompanyIdByVirtualHosts(
			httpServletRequest, strict);

		if (_log.isDebugEnabled()) {
			_log.debug("Company ID from host " + companyId);
		}

		if (companyId <= 0) {
			long cookieCompanyId = GetterUtil.getLong(
				CookiesManagerUtil.getCookieValue(
					CookiesConstants.NAME_COMPANY_ID, httpServletRequest,
					false));

			if (cookieCompanyId > 0) {
				try {
					Company cookieCompany =
						CompanyLocalServiceUtil.fetchCompanyById(
							cookieCompanyId);

					if (cookieCompany == null) {
						if (_log.isWarnEnabled()) {
							_log.warn(
								"Company ID from cookie " + cookieCompanyId +
									" does not exist");
						}
					}
					else {
						companyId = cookieCompanyId;

						if (_log.isDebugEnabled()) {
							_log.debug("Company ID from cookie " + companyId);
						}
					}
				}
				catch (Exception exception) {
					_log.error(exception);
				}
			}
		}

		if (companyId <= 0) {
			companyId = PortalInstancePool.getDefaultCompanyId();

			if (_log.isDebugEnabled()) {
				_log.debug("Default company ID " + companyId);
			}
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Set company ID " + companyId);
		}

		httpServletRequest.setAttribute(
			WebKeys.COMPANY_ID, Long.valueOf(companyId));

		CompanyThreadLocal.setCompanyId(companyId);

		if (Validator.isNotNull(PropsValues.VIRTUAL_HOSTS_DEFAULT_SITE_NAME) &&
			(httpServletRequest.getAttribute(WebKeys.VIRTUAL_HOST_LAYOUT_SET) ==
				null)) {

			try {
				Group group = GroupLocalServiceUtil.getGroup(
					companyId, PropsValues.VIRTUAL_HOSTS_DEFAULT_SITE_NAME);

				LayoutSet layoutSet = LayoutSetLocalServiceUtil.getLayoutSet(
					group.getGroupId(), false);

				TreeMap<String, String> virtualHostnames =
					layoutSet.getVirtualHostnames();

				if (virtualHostnames.isEmpty() ||
					_isCompanyVirtualHostname(
						companyId, httpServletRequest.getServerName())) {

					httpServletRequest.setAttribute(
						WebKeys.VIRTUAL_HOST_LAYOUT_SET, layoutSet);
				}
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}

		return companyId;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             PortalInstancePool#getCompanyIds}
	 */
	@Deprecated
	public static long[] getCompanyIds() {
		return PortalInstancePool.getCompanyIds();
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             PortalInstancePool#getCompanyIds}
	 */
	@Deprecated
	public static long[] getCompanyIdsBySQL() throws SQLException {
		return PortalInstancePool.getCompanyIds();
	}

	public static Long getCopyInProcessCompanyId() {
		return _copyInProcessCompanyId;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             PortalInstancePool#getDefaultCompanyId}
	 */
	@Deprecated
	public static long getDefaultCompanyId() {
		return PortalInstancePool.getDefaultCompanyId();
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             PortalInstancePool#getDefaultCompanyId}
	 */
	@Deprecated
	public static long getDefaultCompanyIdBySQL() throws SQLException {
		return PortalInstancePool.getDefaultCompanyId();
	}

	public static Long getInsertionInProcessCompanyId() {
		return _insertionInProcessCompanyId;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             PortalInstancePool#getWebIds}}
	 */
	@Deprecated
	public static String[] getWebIds() {
		return PortalInstancePool.getWebIds();
	}

	public static long initCompany(Company company) {
		return initCompany(company, false);
	}

	public static long initCompany(Company company, boolean skipCheck) {

		// Begin initializing company

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Begin initializing company with web ID " + company.getWebId());
		}

		String currentThreadPrincipalName = PrincipalThreadLocal.getName();

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					company.getCompanyId())) {

			if (!skipCheck) {
				try {
					CompanyLocalServiceUtil.checkCompany(company.getWebId());
				}
				catch (Exception exception) {
					_log.error(exception);
				}
			}

			String principalName = null;

			long userId = PrincipalThreadLocal.getUserId();

			if (userId > 0) {
				User user = UserLocalServiceUtil.fetchUser(userId);

				if ((user != null) &&
					(user.getCompanyId() == company.getCompanyId())) {

					principalName = currentThreadPrincipalName;
				}
			}

			PrincipalThreadLocal.setName(principalName);

			// Process application startup events

			if (_log.isDebugEnabled()) {
				_log.debug("Process application startup events");
			}

			try {
				EventsProcessorUtil.process(
					PropsKeys.APPLICATION_STARTUP_EVENTS,
					PropsValues.APPLICATION_STARTUP_EVENTS,
					new String[] {String.valueOf(company.getCompanyId())});
			}
			catch (Exception exception) {
				_log.error(exception);
			}

			// End initializing company

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"End initializing company with web ID ",
						company.getWebId(), " and company ID ",
						company.getCompanyId()));
			}

			PortalInstancePool.add(company);
		}
		finally {
			PrincipalThreadLocal.setName(currentThreadPrincipalName);
		}

		return company.getCompanyId();
	}

	public static boolean isAutoLoginIgnoreHost(String host) {
		return _autoLoginIgnoreHosts.contains(host);
	}

	public static boolean isAutoLoginIgnorePath(String path) {
		return _autoLoginIgnorePaths.contains(path);
	}

	public static boolean isCompanyActive(long companyId) {
		try {
			Company company = CompanyLocalServiceUtil.fetchCompanyById(
				companyId);

			if (company != null) {
				return company.isActive();
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return false;
	}

	public static boolean isCompanyInCopyProcess() {
		if (_copyInProcessCompanyId != null) {
			return true;
		}

		return false;
	}

	public static boolean isCompanyInDeletionProcess(long companyId) {
		return _companyIdsInDeletionProcess.contains(companyId);
	}

	public static boolean isCompanyInInsertionProcess() {
		if (_insertionInProcessCompanyId != null) {
			return true;
		}

		return false;
	}

	public static boolean isCurrentCompanyInDeletionProcess() {
		return _companyIdsInDeletionProcess.contains(
			CompanyThreadLocal.getCompanyId());
	}

	public static boolean isVirtualHostsIgnoreHost(String host) {
		return _virtualHostsIgnoreHosts.contains(host);
	}

	public static boolean isVirtualHostsIgnorePath(String path) {
		return _virtualHostsIgnorePaths.contains(path);
	}

	public static void removeCompany(long companyId) {
		try {
			EventsProcessorUtil.process(
				PropsKeys.APPLICATION_SHUTDOWN_EVENTS,
				PropsValues.APPLICATION_SHUTDOWN_EVENTS,
				new String[] {String.valueOf(companyId)});
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		PortalInstancePool.remove(companyId);

		WebAppPool.remove(companyId, WebKeys.PORTLET_CATEGORY);
	}

	public static SafeCloseable setCompanyInDeletionProcessWithSafeCloseable(
		long companyId) {

		if (_companyIdsInDeletionProcess.contains(companyId)) {
			throw new UnsupportedOperationException(
				companyId + " is already in deletion");
		}

		_companyIdsInDeletionProcess.add(companyId);

		return () -> _companyIdsInDeletionProcess.remove(companyId);
	}

	public static SafeCloseable setCopyInProcessCompanyIdWithSafeCloseable(
		long companyId) {

		if (_copyInProcessCompanyId != null) {
			throw new UnsupportedOperationException(
				"Company in copy process company ID is not null");
		}

		_copyInProcessCompanyId = companyId;

		return () -> _copyInProcessCompanyId = null;
	}

	public static SafeCloseable setInsertionInProcessCompanyIdWithSafeCloseable(
		long companyId) {

		if (_insertionInProcessCompanyId != null) {
			throw new UnsupportedOperationException(
				"Company in insertion process company ID is not null");
		}

		_insertionInProcessCompanyId = companyId;

		return () -> _insertionInProcessCompanyId = null;
	}

	private static long _getCompanyIdByHost(
		String host, HttpServletRequest httpServletRequest) {

		if (Validator.isNull(host)) {
			return 0;
		}

		try {
			VirtualHost virtualHost =
				VirtualHostLocalServiceUtil.fetchVirtualHost(host);

			if (virtualHost == null) {
				return 0;
			}

			CompanyThreadLocal.setCompanyId(virtualHost.getCompanyId());

			if (virtualHost.getLayoutSetId() != 0) {
				_setAttributes(virtualHost, httpServletRequest);
			}

			return virtualHost.getCompanyId();
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return 0;
	}

	private static long _getCompanyIdByVirtualHosts(
			HttpServletRequest httpServletRequest, boolean strict)
		throws NoSuchVirtualHostException {

		String host = PortalUtil.getHost(httpServletRequest);

		if (_log.isDebugEnabled()) {
			_log.debug("Host " + host);
		}

		if (isVirtualHostsIgnoreHost(host)) {
			return 0;
		}

		long companyId = _getCompanyIdByHost(host, httpServletRequest);

		if (strict && (companyId == 0)) {
			throw new NoSuchVirtualHostException(host);
		}

		return companyId;
	}

	private static boolean _isCompanyVirtualHostname(
			long companyId, String serverName)
		throws PortalException {

		Company company = CompanyLocalServiceUtil.getCompany(companyId);

		String virtualHostname = company.getVirtualHostname();

		if (Validator.isNull(virtualHostname)) {
			virtualHostname = "localhost";
		}

		if (Objects.equals(virtualHostname, serverName)) {
			return true;
		}

		return false;
	}

	private static void _setAttributes(
			VirtualHost virtualHost, HttpServletRequest httpServletRequest)
		throws PortalException {

		LayoutSet layoutSet = LayoutSetLocalServiceUtil.getLayoutSet(
			virtualHost.getLayoutSetId());

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Company ", virtualHost.getCompanyId(),
					" is associated with layout set ",
					virtualHost.getLayoutSetId()));
		}

		httpServletRequest.setAttribute(
			WebKeys.VIRTUAL_HOST_LAYOUT_SET, layoutSet);

		String languageId = virtualHost.getLanguageId();

		if (Validator.isNotNull(languageId) &&
			LanguageUtil.isAvailableLocale(
				layoutSet.getGroupId(), languageId)) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Virtual host ", virtualHost.getHostname(),
						" has default language ", languageId));
			}

			httpServletRequest.setAttribute(
				WebKeys.VIRTUAL_HOST_LANGUAGE_ID, languageId);
		}
	}

	private PortalInstances() {
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortalInstances.class);

	private static final Set<String> _autoLoginIgnoreHosts;
	private static final Set<String> _autoLoginIgnorePaths;
	private static final List<Long> _companyIdsInDeletionProcess =
		new CopyOnWriteArrayList<>();
	private static Long _copyInProcessCompanyId;
	private static Long _insertionInProcessCompanyId;
	private static final Set<String> _virtualHostsIgnoreHosts;
	private static final Set<String> _virtualHostsIgnorePaths;

	static {
		_autoLoginIgnoreHosts = SetUtil.fromArray(
			PropsUtil.getArray(PropsKeys.AUTO_LOGIN_IGNORE_HOSTS));
		_autoLoginIgnorePaths = SetUtil.fromArray(
			PropsUtil.getArray(PropsKeys.AUTO_LOGIN_IGNORE_PATHS));
		_virtualHostsIgnoreHosts = SetUtil.fromArray(
			PropsUtil.getArray(PropsKeys.VIRTUAL_HOSTS_IGNORE_HOSTS));
		_virtualHostsIgnorePaths = SetUtil.fromArray(
			PropsUtil.getArray(PropsKeys.VIRTUAL_HOSTS_IGNORE_PATHS));
	}

}