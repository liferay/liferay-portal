/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.events;

import com.liferay.portal.kernel.cluster.ClusterExecutorUtil;
import com.liferay.portal.kernel.cluster.ClusterNode;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.Date;

/**
 * @author Brian Wing Shun Chan
 */
public class LoginPostAction extends Action {

	@Override
	public void run(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws ActionException {

		try {
			if (_log.isDebugEnabled()) {
				_log.debug("Running " + httpServletRequest.getRemoteUser());
			}

			HttpSession httpSession = httpServletRequest.getSession();

			long companyId = PortalUtil.getCompanyId(httpServletRequest);
			long userId = 0;

			// Language

			httpSession.removeAttribute(WebKeys.LOCALE);

			// Live users

			if (PropsValues.LIVE_USERS_ENABLED) {
				JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

				ClusterNode clusterNode =
					ClusterExecutorUtil.getLocalClusterNode();

				if (clusterNode != null) {
					jsonObject.put(
						"clusterNodeId", clusterNode.getClusterNodeId());
				}

				jsonObject.put(
					"command", "signIn"
				).put(
					"companyId", companyId
				).put(
					"remoteAddr", httpServletRequest.getRemoteAddr()
				).put(
					"remoteHost", httpServletRequest.getRemoteHost()
				).put(
					"sessionId", httpSession.getId()
				);

				String userAgent = httpServletRequest.getHeader(
					HttpHeaders.USER_AGENT);

				jsonObject.put("userAgent", userAgent);

				userId = PortalUtil.getUserId(httpServletRequest);

				jsonObject.put("userId", userId);

				MessageBusUtil.sendMessage(
					DestinationNames.LIVE_USERS, jsonObject.toString());
			}

			if (PrefsPropsUtil.getBoolean(
					companyId, PropsKeys.ADMIN_SYNC_DEFAULT_ASSOCIATIONS)) {

				if (userId == 0) {
					userId = PortalUtil.getUserId(httpServletRequest);
				}

				boolean reindex = false;

				if (UserLocalServiceUtil.addDefaultGroups(userId) |
					UserLocalServiceUtil.addDefaultRoles(userId) |
					UserLocalServiceUtil.addDefaultUserGroups(userId)) {

					reindex = true;
				}

				if (reindex) {
					Indexer<User> userIndexer = IndexerRegistryUtil.getIndexer(
						User.class.getName());

					userIndexer.reindex(User.class.getName(), userId);
				}
			}

			User user = PortalUtil.getUser(httpServletRequest);

			PasswordPolicy passwordPolicy = user.getPasswordPolicy();

			if ((passwordPolicy != null) && passwordPolicy.isExpireable() &&
				(passwordPolicy.getWarningTime() > 0)) {

				_setPasswordExpirationMessage(
					httpServletRequest, passwordPolicy, user);
			}
		}
		catch (Exception exception) {
			throw new ActionException(exception);
		}
	}

	private void _setPasswordExpirationMessage(
			HttpServletRequest httpServletRequest,
			PasswordPolicy passwordPolicy, User user)
		throws PortalException {

		Date date = new Date();

		if (user.getPasswordModifiedDate() == null) {
			HttpSession httpSession = httpServletRequest.getSession(false);

			if (httpSession != null) {
				date = new Date(httpSession.getCreationTime());
			}

			user.setPasswordModifiedDate(date);

			user = UserLocalServiceUtil.updateUser(user);
		}

		Date passwordModifiedDate = user.getPasswordModifiedDate();

		long passwordExpirationTime =
			(passwordPolicy.getMaxAge() * 1000) +
				passwordModifiedDate.getTime();

		long startWarningTime =
			passwordExpirationTime - (passwordPolicy.getWarningTime() * 1000);

		if (date.getTime() > startWarningTime) {
			int passwordExpiresInXDays =
				(int)((passwordExpirationTime - date.getTime()) / Time.DAY);

			if (passwordExpiresInXDays >= 0) {
				SessionMessages.add(
					httpServletRequest, "passwordExpiresInXDays",
					passwordExpiresInXDays);
			}
			else {
				int remainingGraceLogins =
					passwordPolicy.getGraceLimit() - user.getGraceLoginCount();

				SessionMessages.add(
					httpServletRequest, "remainingGraceLogins",
					remainingGraceLogins);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LoginPostAction.class);

}