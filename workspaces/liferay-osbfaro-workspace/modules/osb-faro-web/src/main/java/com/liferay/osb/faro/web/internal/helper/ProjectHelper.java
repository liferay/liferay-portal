/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.helper;

import com.liferay.osb.faro.engine.client.model.LCPProject;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;

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

}