/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.liveusers;

import com.liferay.portal.kernel.cluster.ClusterExecutorUtil;
import com.liferay.portal.kernel.cluster.ClusterMasterExecutorUtil;
import com.liferay.portal.kernel.cluster.ClusterNode;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.UserTracker;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.UserTrackerLocalServiceUtil;
import com.liferay.portal.kernel.service.persistence.UserTrackerUtil;
import com.liferay.portal.kernel.servlet.PortalSessionContext;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Charles May
 * @author Brian Wing Shun Chan
 */
public class LiveUsers {

	public static void addClusterNode(
		String clusterNodeId, Map<Long, Map<Long, Set<String>>> clusterUsers) {

		if (Validator.isNull(clusterNodeId)) {
			return;
		}

		for (Map.Entry<Long, Map<Long, Set<String>>> companyUsers :
				clusterUsers.entrySet()) {

			long companyId = companyUsers.getKey();
			Map<Long, Set<String>> userSessionsMap = companyUsers.getValue();

			for (Map.Entry<Long, Set<String>> userSessions :
					userSessionsMap.entrySet()) {

				long userId = userSessions.getKey();

				for (String sessionId : userSessions.getValue()) {
					signIn(
						clusterNodeId, companyId, userId, sessionId, null, null,
						null);
				}
			}
		}
	}

	public static void deleteGroup(long companyId, long groupId) {
		Map<Long, Set<Long>> liveUsers = _getLiveUsers(companyId);

		liveUsers.remove(groupId);
	}

	public static Set<Long> getGroupUsers(long companyId, long groupId) {
		return _getGroupUsers(_getLiveUsers(companyId), groupId);
	}

	public static int getGroupUsersCount(long companyId, long groupId) {
		Set<Long> groupUsers = getGroupUsers(companyId, groupId);

		return groupUsers.size();
	}

	public static Map<Long, Map<Long, Set<String>>> getLocalClusterUsers() {
		ClusterNode clusterNode = ClusterExecutorUtil.getLocalClusterNode();

		if (clusterNode == null) {
			return null;
		}

		return _clusterUsers.get(clusterNode.getClusterNodeId());
	}

	public static Map<String, UserTracker> getSessionUsers(long companyId) {
		Map<String, UserTracker> sessionUsers = _sessionUsers.get(companyId);

		if (sessionUsers == null) {
			sessionUsers = new ConcurrentHashMap<>();

			_sessionUsers.put(companyId, sessionUsers);
		}

		return sessionUsers;
	}

	public static int getSessionUsersCount(long companyId) {
		Map<String, UserTracker> sessionUsers = getSessionUsers(companyId);

		return sessionUsers.size();
	}

	public static UserTracker getUserTracker(long companyId, String sessionId) {
		Map<String, UserTracker> sessionUsers = getSessionUsers(companyId);

		return sessionUsers.get(sessionId);
	}

	public static void joinGroup(long companyId, long groupId, long userId) {
		Map<Long, Set<Long>> liveUsers = _getLiveUsers(companyId);

		if (_getUserTrackers(companyId, userId) != null) {
			Set<Long> groupUsers = _getGroupUsers(liveUsers, groupId);

			groupUsers.add(userId);
		}
	}

	public static void joinGroup(long companyId, long groupId, long[] userIds) {
		Set<Long> groupUsers = _getGroupUsers(
			_getLiveUsers(companyId), groupId);

		for (long userId : userIds) {
			if (_getUserTrackers(companyId, userId) != null) {
				groupUsers.add(userId);
			}
		}
	}

	public static void leaveGroup(long companyId, long groupId, long userId) {
		Set<Long> groupUsers = _getGroupUsers(
			_getLiveUsers(companyId), groupId);

		groupUsers.remove(userId);
	}

	public static void leaveGroup(
		long companyId, long groupId, long[] userIds) {

		Set<Long> groupUsers = _getGroupUsers(
			_getLiveUsers(companyId), groupId);

		for (long userId : userIds) {
			groupUsers.remove(userId);
		}
	}

	public static void removeClusterNode(String clusterNodeId) {
		if (Validator.isNull(clusterNodeId)) {
			return;
		}

		Map<Long, Map<Long, Set<String>>> clusterUsers = _clusterUsers.remove(
			clusterNodeId);

		if (clusterUsers == null) {
			return;
		}

		for (Map.Entry<Long, Map<Long, Set<String>>> companyUsers :
				clusterUsers.entrySet()) {

			long companyId = companyUsers.getKey();
			Map<Long, Set<String>> userSessionsMap = companyUsers.getValue();

			for (Map.Entry<Long, Set<String>> userSessions :
					userSessionsMap.entrySet()) {

				long userId = userSessions.getKey();

				for (String sessionId : userSessions.getValue()) {
					signOut(clusterNodeId, companyId, userId, sessionId);
				}
			}
		}
	}

	public static void signIn(
		String clusterNodeId, long companyId, long userId, String sessionId,
		String remoteAddr, String remoteHost, String userAgent) {

		_addClusterUser(clusterNodeId, companyId, userId, sessionId);

		_updateGroupStatus(companyId, userId, true);

		Map<String, UserTracker> sessionUsers = getSessionUsers(companyId);

		UserTracker userTracker = sessionUsers.get(sessionId);

		if ((userTracker == null) &&
			PropsValues.SESSION_TRACKER_MEMORY_ENABLED) {

			userTracker = UserTrackerUtil.create(0);

			userTracker.setCompanyId(companyId);
			userTracker.setUserId(userId);
			userTracker.setModifiedDate(new Date());
			userTracker.setSessionId(sessionId);
			userTracker.setRemoteAddr(remoteAddr);
			userTracker.setRemoteHost(remoteHost);
			userTracker.setUserAgent(userAgent);

			sessionUsers.put(sessionId, userTracker);

			_addUserTracker(companyId, userId, userTracker);
		}
	}

	public static void signOut(
		String clusterNodeId, long companyId, long userId, String sessionId) {

		_removeClusterUser(clusterNodeId, companyId, userId, sessionId);

		List<UserTracker> userTrackers = _getUserTrackers(companyId, userId);

		if ((userTrackers == null) || (userTrackers.size() <= 1)) {
			_updateGroupStatus(companyId, userId, false);
		}

		Map<String, UserTracker> sessionUsers = getSessionUsers(companyId);

		UserTracker userTracker = sessionUsers.remove(sessionId);

		if (userTracker == null) {
			return;
		}

		try {
			if (ClusterMasterExecutorUtil.isMaster()) {
				UserTrackerLocalServiceUtil.addUserTracker(
					userTracker.getCompanyId(), userTracker.getUserId(),
					userTracker.getModifiedDate(), sessionId,
					userTracker.getRemoteAddr(), userTracker.getRemoteHost(),
					userTracker.getUserAgent(), userTracker.getPaths());
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		try {
			HttpSession httpSession = PortalSessionContext.get(sessionId);

			if (httpSession != null) {
				httpSession.invalidate();
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		_removeUserTracker(companyId, userId, userTracker);
	}

	private static void _addClusterUser(
		String clusterNodeId, long companyId, long userId, String sessionId) {

		if (Validator.isNull(clusterNodeId)) {
			return;
		}

		Map<Long, Map<Long, Set<String>>> clusterUsers = _clusterUsers.get(
			clusterNodeId);

		if (clusterUsers == null) {
			clusterUsers = new ConcurrentHashMap<>();

			_clusterUsers.put(clusterNodeId, clusterUsers);
		}

		Map<Long, Set<String>> companyUsers = clusterUsers.get(companyId);

		if (companyUsers == null) {
			companyUsers = new ConcurrentHashMap<>();

			clusterUsers.put(companyId, companyUsers);
		}

		Set<String> userSessions = companyUsers.get(userId);

		if (userSessions == null) {
			userSessions = Collections.newSetFromMap(new ConcurrentHashMap<>());

			companyUsers.put(userId, userSessions);
		}

		userSessions.add(sessionId);
	}

	private static void _addUserTracker(
		long companyId, long userId, UserTracker userTracker) {

		List<UserTracker> userTrackers = _getUserTrackers(companyId, userId);

		if (userTrackers != null) {
			userTrackers.add(userTracker);
		}
		else {
			userTrackers = new ArrayList<>();

			userTrackers.add(userTracker);

			Map<Long, List<UserTracker>> userTrackersMap = _getUserTrackersMap(
				companyId);

			userTrackersMap.put(userId, userTrackers);
		}
	}

	private static Set<Long> _getGroupUsers(
		Map<Long, Set<Long>> liveUsers, long groupId) {

		Set<Long> groupUsers = liveUsers.get(groupId);

		if (groupUsers == null) {
			groupUsers = Collections.newSetFromMap(new ConcurrentHashMap<>());

			liveUsers.put(groupId, groupUsers);
		}

		return groupUsers;
	}

	private static Map<Long, Set<Long>> _getLiveUsers(long companyId) {
		Map<Long, Set<Long>> liveUsers = _liveUsers.get(companyId);

		if (liveUsers == null) {
			liveUsers = new ConcurrentHashMap<>();

			_liveUsers.put(companyId, liveUsers);
		}

		return liveUsers;
	}

	private static List<UserTracker> _getUserTrackers(
		long companyId, long userId) {

		Map<Long, List<UserTracker>> userTrackersMap = _getUserTrackersMap(
			companyId);

		return userTrackersMap.get(userId);
	}

	private static Map<Long, List<UserTracker>> _getUserTrackersMap(
		long companyId) {

		Map<Long, List<UserTracker>> userTrackersMap = _userTrackers.get(
			companyId);

		if (userTrackersMap == null) {
			userTrackersMap = new ConcurrentHashMap<>();

			_userTrackers.put(companyId, userTrackersMap);
		}

		return userTrackersMap;
	}

	private static void _removeClusterUser(
		String clusterNodeId, long companyId, long userId, String sessionId) {

		if (Validator.isNull(clusterNodeId)) {
			return;
		}

		Map<Long, Map<Long, Set<String>>> clusterUsers = _clusterUsers.get(
			clusterNodeId);

		if (clusterUsers == null) {
			return;
		}

		Map<Long, Set<String>> companyUsers = clusterUsers.get(companyId);

		if (companyUsers == null) {
			return;
		}

		Set<String> userSessions = companyUsers.get(userId);

		if (userSessions == null) {
			return;
		}

		userSessions.remove(sessionId);
	}

	private static void _removeUserTracker(
		long companyId, long userId, UserTracker userTracker) {

		List<UserTracker> userTrackers = _getUserTrackers(companyId, userId);

		if (userTrackers == null) {
			return;
		}

		String sessionId = userTracker.getSessionId();

		Iterator<UserTracker> iterator = userTrackers.iterator();

		while (iterator.hasNext()) {
			UserTracker curUserTracker = iterator.next();

			if (sessionId.equals(curUserTracker.getSessionId())) {
				iterator.remove();
			}
		}

		if (userTrackers.isEmpty()) {
			Map<Long, List<UserTracker>> userTrackersMap = _getUserTrackersMap(
				companyId);

			userTrackersMap.remove(userId);
		}
	}

	private static Map<Long, Set<Long>> _updateGroupStatus(
		long companyId, long userId, boolean signedIn) {

		Map<Long, Set<Long>> liveUsers = _getLiveUsers(companyId);

		List<Group> groups = GroupLocalServiceUtil.search(
			companyId, null, null,
			LinkedHashMapBuilder.<String, Object>put(
				"usersGroups", userId
			).build(),
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (Group group : groups) {
			Set<Long> groupUsers = _getGroupUsers(
				liveUsers, group.getGroupId());

			if (signedIn) {
				groupUsers.add(userId);
			}
			else {
				groupUsers.remove(userId);
			}
		}

		return liveUsers;
	}

	private LiveUsers() {
	}

	private static final Log _log = LogFactoryUtil.getLog(LiveUsers.class);

	private static final Map<String, Map<Long, Map<Long, Set<String>>>>
		_clusterUsers = new ConcurrentHashMap<>();
	private static final Map<Long, Map<Long, Set<Long>>> _liveUsers =
		new ConcurrentHashMap<>();
	private static final Map<Long, Map<String, UserTracker>> _sessionUsers =
		new ConcurrentHashMap<>();
	private static final Map<Long, Map<Long, List<UserTracker>>> _userTrackers =
		new ConcurrentHashMap<>();

}