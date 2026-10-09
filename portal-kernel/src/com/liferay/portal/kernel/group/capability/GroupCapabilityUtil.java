/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.group.capability;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.GroupCapabilityContributor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alejandro Tardín
 */
public class GroupCapabilityUtil {

	public static boolean isSupportsPages(Group group) {
		for (GroupCapability groupCapability : _getGroupCapabilities(group)) {
			if (!groupCapability.isSupportsPages()) {
				return false;
			}
		}

		return true;
	}

	public static boolean isSupportsPortlet(Group group, Portlet portlet) {
		for (GroupCapability groupCapability : _getGroupCapabilities(group)) {
			if (!groupCapability.isSupportsPortlet(portlet)) {
				return false;
			}
		}

		return true;
	}

	public static boolean isSupportsScope(Group group) {
		for (GroupCapability groupCapability : _getGroupCapabilities(group)) {
			if (!groupCapability.isSupportsScopes()) {
				return false;
			}
		}

		return true;
	}

	private static List<GroupCapability> _getGroupCapabilities(Group group) {
		List<GroupCapability> groupCapabilities = new ArrayList<>();

		if (group == null) {
			return groupCapabilities;
		}

		for (GroupCapabilityContributor groupCapabilityContributor :
				_groupCapabilityContributors) {

			GroupCapability capability =
				groupCapabilityContributor.getGroupCapability(group);

			if (capability == null) {
				continue;
			}

			groupCapabilities.add(capability);
		}

		return groupCapabilities;
	}

	private static final ServiceTrackerList<GroupCapabilityContributor>
		_groupCapabilityContributors = ServiceTrackerListFactory.open(
			SystemBundleUtil.getBundleContext(),
			GroupCapabilityContributor.class);

}