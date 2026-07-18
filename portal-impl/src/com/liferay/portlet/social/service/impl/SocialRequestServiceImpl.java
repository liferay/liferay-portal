/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.social.service.impl;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portlet.social.service.base.SocialRequestServiceBaseImpl;
import com.liferay.social.kernel.model.SocialRequest;

/**
 * Provides the remote service for updating social requests. Its methods include
 * permission checks.
 *
 * @author Brian Wing Shun Chan
 */
public class SocialRequestServiceImpl extends SocialRequestServiceBaseImpl {

	@Override
	public SocialRequest updateRequest(
			long requestId, int status, ThemeDisplay themeDisplay)
		throws PortalException {

		_check(getPermissionChecker(), requestId, ActionKeys.UPDATE);

		return socialRequestLocalService.updateRequest(
			requestId, status, themeDisplay);
	}

	private void _check(
			PermissionChecker permissionChecker, long requestId,
			String actionId)
		throws PortalException {

		if (!_contains(permissionChecker, requestId, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, SocialRequest.class.getName(), requestId,
				actionId);
		}
	}

	private boolean _contains(
			PermissionChecker permissionChecker, long requestId,
			String actionId)
		throws PortalException {

		if (permissionChecker.isOmniadmin()) {
			return true;
		}

		if (actionId.equals(ActionKeys.UPDATE)) {
			SocialRequest request = socialRequestPersistence.findByPrimaryKey(
				requestId);

			if (permissionChecker.getUserId() == request.getReceiverUserId()) {
				return true;
			}
		}

		return false;
	}

}