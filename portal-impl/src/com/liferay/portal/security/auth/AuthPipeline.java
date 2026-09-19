/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.auth;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.security.auth.AuthDNE;
import com.liferay.portal.kernel.security.auth.AuthException;
import com.liferay.portal.kernel.security.auth.AuthFailure;
import com.liferay.portal.kernel.security.auth.Authenticator;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.List;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 */
public class AuthPipeline {

	public static int authenticateByEmailAddress(
			String key, long companyId, String emailAddress, String password,
			Map<String, String[]> headerMap, Map<String, String[]> parameterMap)
		throws AuthException {

		return _authenticate(
			key, companyId, emailAddress, password,
			CompanyConstants.AUTH_TYPE_EA, headerMap, parameterMap);
	}

	public static int authenticateByScreenName(
			String key, long companyId, String screenName, String password,
			Map<String, String[]> headerMap, Map<String, String[]> parameterMap)
		throws AuthException {

		return _authenticate(
			key, companyId, screenName, password, CompanyConstants.AUTH_TYPE_SN,
			headerMap, parameterMap);
	}

	public static int authenticateByUserId(
			String key, long companyId, long userId, String password,
			Map<String, String[]> headerMap, Map<String, String[]> parameterMap)
		throws AuthException {

		return _authenticate(
			key, companyId, String.valueOf(userId), password,
			CompanyConstants.AUTH_TYPE_ID, headerMap, parameterMap);
	}

	public static void onDoesNotExist(
			long companyId, String authType, String login,
			Map<String, String[]> headerMap, Map<String, String[]> parameterMap)
		throws AuthException {

		List<AuthDNE> authDNEs = _authDNEs.toList();

		if (authDNEs.isEmpty()) {
			return;
		}

		for (AuthDNE authDNE : authDNEs) {
			try {
				authDNE.onDoesNotExist(
					companyId, authType, login, headerMap, parameterMap);
			}
			catch (AuthException authException) {
				throw authException;
			}
			catch (Exception exception) {
				throw new AuthException(exception);
			}
		}
	}

	public static void onFailureByEmailAddress(
			String key, long companyId, String emailAddress,
			Map<String, String[]> headerMap, Map<String, String[]> parameterMap)
		throws AuthException {

		_onFailure(
			key, companyId, emailAddress, CompanyConstants.AUTH_TYPE_EA,
			headerMap, parameterMap);
	}

	public static void onFailureByScreenName(
			String key, long companyId, String screenName,
			Map<String, String[]> headerMap, Map<String, String[]> parameterMap)
		throws AuthException {

		_onFailure(
			key, companyId, screenName, CompanyConstants.AUTH_TYPE_SN,
			headerMap, parameterMap);
	}

	public static void onFailureByUserId(
			String key, long companyId, long userId,
			Map<String, String[]> headerMap, Map<String, String[]> parameterMap)
		throws AuthException {

		_onFailure(
			key, companyId, String.valueOf(userId),
			CompanyConstants.AUTH_TYPE_ID, headerMap, parameterMap);
	}

	public static void onMaxFailuresByEmailAddress(
			String key, long companyId, String emailAddress,
			Map<String, String[]> headerMap, Map<String, String[]> parameterMap)
		throws AuthException {

		onFailureByEmailAddress(
			key, companyId, emailAddress, headerMap, parameterMap);
	}

	public static void onMaxFailuresByScreenName(
			String key, long companyId, String screenName,
			Map<String, String[]> headerMap, Map<String, String[]> parameterMap)
		throws AuthException {

		onFailureByScreenName(
			key, companyId, screenName, headerMap, parameterMap);
	}

	public static void onMaxFailuresByUserId(
			String key, long companyId, long userId,
			Map<String, String[]> headerMap, Map<String, String[]> parameterMap)
		throws AuthException {

		onFailureByUserId(key, companyId, userId, headerMap, parameterMap);
	}

	private static int _authenticate(
			String key, long companyId, String login, String password,
			String authType, Map<String, String[]> headerMap,
			Map<String, String[]> parameterMap)
		throws AuthException {

		List<Authenticator> authenticators = _authenticators.getService(key);

		if (ListUtil.isEmpty(authenticators)) {
			return Authenticator.SUCCESS;
		}

		boolean skipLiferayCheck = false;

		for (Authenticator authenticator : authenticators) {
			try {
				int authResult = Authenticator.FAILURE;

				if (authType.equals(CompanyConstants.AUTH_TYPE_EA)) {
					authResult = authenticator.authenticateByEmailAddress(
						companyId, login, password, headerMap, parameterMap);
				}
				else if (authType.equals(CompanyConstants.AUTH_TYPE_SN)) {
					authResult = authenticator.authenticateByScreenName(
						companyId, login, password, headerMap, parameterMap);
				}
				else if (authType.equals(CompanyConstants.AUTH_TYPE_ID)) {
					long userId = GetterUtil.getLong(login);

					authResult = authenticator.authenticateByUserId(
						companyId, userId, password, headerMap, parameterMap);
				}

				if (authResult == Authenticator.SKIP_LIFERAY_CHECK) {
					skipLiferayCheck = true;
				}
				else if (authResult != Authenticator.SUCCESS) {
					return authResult;
				}
			}
			catch (AuthException authException) {
				throw authException;
			}
			catch (Exception exception) {
				throw new AuthException(exception);
			}
		}

		if (skipLiferayCheck) {
			return Authenticator.SKIP_LIFERAY_CHECK;
		}

		return Authenticator.SUCCESS;
	}

	private static void _onFailure(
			String key, long companyId, String login, String authType,
			Map<String, String[]> headerMap, Map<String, String[]> parameterMap)
		throws AuthException {

		List<AuthFailure> authFailures = _authFailures.getService(key);

		if (authFailures.isEmpty()) {
			return;
		}

		for (AuthFailure authFailure : authFailures) {
			try {
				if (authType.equals(CompanyConstants.AUTH_TYPE_EA)) {
					authFailure.onFailureByEmailAddress(
						companyId, login, headerMap, parameterMap);
				}
				else if (authType.equals(CompanyConstants.AUTH_TYPE_SN)) {
					authFailure.onFailureByScreenName(
						companyId, login, headerMap, parameterMap);
				}
				else if (authType.equals(CompanyConstants.AUTH_TYPE_ID)) {
					long userId = GetterUtil.getLong(login);

					authFailure.onFailureByUserId(
						companyId, userId, headerMap, parameterMap);
				}
			}
			catch (AuthException authException) {
				throw authException;
			}
			catch (Exception exception) {
				throw new AuthException(exception);
			}
		}
	}

	private static final ServiceTrackerList<AuthDNE> _authDNEs =
		ServiceTrackerListFactory.open(
			SystemBundleUtil.getBundleContext(), AuthDNE.class);
	private static final ServiceTrackerMap<String, List<AuthFailure>>
		_authFailures = ServiceTrackerMapFactory.openMultiValueMap(
			SystemBundleUtil.getBundleContext(), AuthFailure.class, "key");
	private static final ServiceTrackerMap<String, List<Authenticator>>
		_authenticators = ServiceTrackerMapFactory.openMultiValueMap(
			SystemBundleUtil.getBundleContext(), Authenticator.class, "key");

}