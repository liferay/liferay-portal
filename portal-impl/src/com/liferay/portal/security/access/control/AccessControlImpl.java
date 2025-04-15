/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.access.control;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.access.control.AccessControl;
import com.liferay.portal.kernel.security.access.control.AccessControlThreadLocal;
import com.liferay.portal.kernel.security.access.control.AccessControlUtil;
import com.liferay.portal.kernel.security.auth.AccessControlContext;
import com.liferay.portal.kernel.security.auth.AuthException;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierResult;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.security.auth.AuthVerifierPipeline;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

/**
 * @author Raymond Augé
 */
public class AccessControlImpl implements AccessControl {

	@Override
	public void initAccessControlContext(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, Map<String, Object> settings) {

		AccessControlContext accessControlContext =
			AccessControlUtil.getAccessControlContext();

		if (accessControlContext != null) {
			throw new IllegalStateException(
				"Authentication context is already initialized");
		}

		accessControlContext = new AccessControlContext();

		accessControlContext.setRequest(httpServletRequest);
		accessControlContext.setResponse(httpServletResponse);

		Map<String, Object> accessControlContextSettings =
			accessControlContext.getSettings();

		accessControlContextSettings.putAll(settings);

		AccessControlUtil.setAccessControlContext(accessControlContext);
	}

	@Override
	public void initContextUser(long userId) throws AuthException {
		try {
			User user = UserLocalServiceUtil.getUser(userId);

			CompanyThreadLocal.setCompanyId(user.getCompanyId());

			PrincipalThreadLocal.setName(userId);

			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(user));

			AccessControlThreadLocal.setRemoteAccess(false);
		}
		catch (Exception exception) {
			throw new AuthException(exception.getMessage(), exception);
		}
	}

	@Override
	public AuthVerifierResult.State verifyRequest() throws PortalException {
		AuthVerifierResult authVerifierResult = null;

		AccessControlContext accessControlContext =
			AccessControlUtil.getAccessControlContext();

		Map<String, Object> settings = accessControlContext.getSettings();

		if (!settings.containsKey(AuthVerifierPipeline.class.getName())) {
			AuthVerifierPipeline portalAuthVerifierPipeline =
				AuthVerifierPipeline.getPortalAuthVerifierPipeline();

			authVerifierResult = portalAuthVerifierPipeline.verifyRequest(
				accessControlContext);
		}
		else {
			AuthVerifierPipeline authVerifierPipeline =
				(AuthVerifierPipeline)settings.get(
					AuthVerifierPipeline.class.getName());

			authVerifierResult = authVerifierPipeline.verifyRequest(
				accessControlContext);

			if ((authVerifierResult.getState() ==
					AuthVerifierResult.State.NOT_APPLICABLE) ||
				(authVerifierResult.getState() ==
					AuthVerifierResult.State.UNSUCCESSFUL)) {

				AuthVerifierPipeline portalAuthVerifierPipeline =
					AuthVerifierPipeline.getPortalAuthVerifierPipeline();

				authVerifierResult = portalAuthVerifierPipeline.verifyRequest(
					accessControlContext);
			}
		}

		Map<String, Object> authVerifierResultSettings =
			authVerifierResult.getSettings();

		if (authVerifierResultSettings != null) {
			settings.putAll(authVerifierResultSettings);
		}

		accessControlContext.setAuthVerifierResult(authVerifierResult);

		return authVerifierResult.getState();
	}

}