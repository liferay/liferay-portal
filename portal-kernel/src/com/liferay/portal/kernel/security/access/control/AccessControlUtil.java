/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.access.control;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.internal.security.access.control.AllowedIPAddressesValidator;
import com.liferay.portal.kernel.internal.security.access.control.AllowedIPAddressesValidatorFactory;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.auth.AccessControlContext;
import com.liferay.portal.kernel.security.auth.AuthException;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierResult;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;
import java.util.Set;

/**
 * @author Tomas Polesovsky
 * @author Michael C. Han
 * @author Raymond Augé
 */
public class AccessControlUtil {

	public static AccessControl getAccessControl() {
		return _accessControlSnapshot.get();
	}

	public static AccessControlContext getAccessControlContext() {
		return _accessControlContext.get();
	}

	public static void initAccessControlContext(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, Map<String, Object> settings) {

		AccessControl accessControl = _accessControlSnapshot.get();

		accessControl.initAccessControlContext(
			httpServletRequest, httpServletResponse, settings);
	}

	public static void initContextUser(long userId) throws AuthException {
		AccessControl accessControl = _accessControlSnapshot.get();

		accessControl.initContextUser(userId);
	}

	public static boolean isAccessAllowed(
		HttpServletRequest httpServletRequest, Set<String> hostsAllowed) {

		if (hostsAllowed.isEmpty()) {
			return true;
		}

		String remoteAddr = httpServletRequest.getRemoteAddr();

		Set<String> computerAddresses = PortalUtil.getComputerAddresses();

		if ((computerAddresses.contains(remoteAddr) &&
			 hostsAllowed.contains(_SERVER_IP)) ||
			hostsAllowed.contains(remoteAddr)) {

			return true;
		}

		for (String hostAllowed : hostsAllowed) {
			AllowedIPAddressesValidator allowedIPAddressesValidator =
				AllowedIPAddressesValidatorFactory.create(hostAllowed);

			if (allowedIPAddressesValidator.isAllowedIPAddress(remoteAddr)) {
				return true;
			}
		}

		return false;
	}

	public static void setAccessControlContext(
		AccessControlContext accessControlContext) {

		_accessControlContext.set(accessControlContext);
	}

	public static AuthVerifierResult.State verifyRequest()
		throws PortalException {

		AccessControl accessControl = _accessControlSnapshot.get();

		return accessControl.verifyRequest();
	}

	private AccessControlUtil() {
	}

	private static final String _SERVER_IP = "SERVER_IP";

	private static final ThreadLocal<AccessControlContext>
		_accessControlContext = new CentralizedThreadLocal<>(
			AccessControlUtil.class + "._accessControlContext");
	private static final Snapshot<AccessControl> _accessControlSnapshot =
		new Snapshot<>(AccessControlUtil.class, AccessControl.class);

}