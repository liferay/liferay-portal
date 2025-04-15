/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.associated.data.web.internal.helper;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.user.associated.data.anonymizer.UADAnonymousUserProvider;

import jakarta.portlet.PortletRequest;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = SelectedUserHelper.class)
public class SelectedUserHelper {

	public User getSelectedUser(PortletRequest portletRequest)
		throws PortalException {

		User selectedUser = portal.getSelectedUser(portletRequest);

		if (Objects.equals(portal.getUser(portletRequest), selectedUser)) {
			throw new PortalException(
				"The selected user cannot be the logged in user");
		}

		if (uadAnonymousUserProvider.isAnonymousUser(selectedUser)) {
			throw new PortalException(
				"The selected user cannot be the anonymous user");
		}

		return selectedUser;
	}

	public long getSelectedUserId(PortletRequest portletRequest)
		throws PortalException {

		User selectedUser = getSelectedUser(portletRequest);

		return selectedUser.getUserId();
	}

	@Reference
	protected Portal portal;

	@Reference
	protected UADAnonymousUserProvider uadAnonymousUserProvider;

}