/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.helper;

import com.liferay.osb.faro.constants.FaroProjectConstants;
import com.liferay.osb.faro.engine.client.model.LCPProject;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.osb.faro.util.FaroProjectUtil;
import com.liferay.osb.faro.web.internal.model.display.contacts.ProjectDisplay;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matthew Kong
 */
@Component(service = ProjectHelper.class)
public class ProjectHelper {

	public void addGlobalState(
		List<String> keys, Map<String, Object> stateMap) {

		for (long groupId : _getGroupIds(keys)) {
			_globalStateMaps.put(groupId, stateMap);
		}
	}

	public void deleteGlobalState(long groupId) {
		_globalStateMaps.remove(groupId);
	}

	public void deleteGlobalStates(List<String> keys) {
		if (StringUtil.equals(keys.get(0), "all")) {
			_globalStateMaps.clear();

			return;
		}

		for (long groupId : _getGroupIds(keys)) {
			_globalStateMaps.remove(groupId);
		}
	}

	public Map<String, Object> getGlobalStateMap(FaroProject faroProject) {
		return _globalStateMaps.get(faroProject.getGroupId());
	}

	public ProjectDisplay getProjectDisplay(FaroProject faroProject)
		throws PortalException {

		ProjectDisplay projectDisplay = new ProjectDisplay(faroProject);

		Group group = _groupLocalService.getGroup(faroProject.getGroupId());

		String friendlyURL = group.getFriendlyURL();

		if (!StringUtil.equals(
				friendlyURL,
				FaroProjectUtil.getDefaultFriendlyURL(group.getGroupId()))) {

			projectDisplay.setFriendlyURL(friendlyURL);
		}

		Map<String, Object> globalStateMap = getGlobalStateMap(faroProject);

		if (globalStateMap == null) {
			return projectDisplay;
		}

		String state = (String)globalStateMap.get("state");

		if (StringUtil.equals(
				projectDisplay.getState(),
				FaroProjectConstants.STATE_UNAVAILABLE) &&
			StringUtil.equals(state, FaroProjectConstants.STATE_SCHEDULED)) {

			return projectDisplay;
		}
		else if (StringUtil.equals(
					projectDisplay.getState(),
					FaroProjectConstants.STATE_READY) &&
				 StringUtil.equals(
					 state, FaroProjectConstants.STATE_MAINTENANCE)) {

			deleteGlobalState(faroProject.getGroupId());

			return projectDisplay;
		}

		projectDisplay.setState(state);
		projectDisplay.setStateEndDate((Date)globalStateMap.get("endDate"));
		projectDisplay.setStateStartDate((Date)globalStateMap.get("startDate"));

		return projectDisplay;
	}

	private List<Long> _getGroupIds(List<String> keys) {
		String key = keys.get(0);

		if (key.equals("all")) {
			return TransformUtil.transform(
				_faroProjectLocalService.getFaroProjects(
					QueryUtil.ALL_POS, QueryUtil.ALL_POS),
				FaroProject::getGroupId);
		}

		if (key.equals(LCPProject.Cluster.AS1.toString()) ||
			key.equals(LCPProject.Cluster.EU2.toString()) ||
			key.equals(LCPProject.Cluster.EU3.toString()) ||
			key.equals(LCPProject.Cluster.SA.toString()) ||
			key.equals(LCPProject.Cluster.US.toString())) {

			return TransformUtil.transform(
				_faroProjectLocalService.getFaroProjects(key),
				FaroProject::getGroupId);
		}

		return TransformUtil.transform(keys, GetterUtil::getLong);
	}

	private static final Map<Long, Map<String, Object>> _globalStateMaps =
		new HashMap<>();

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

}