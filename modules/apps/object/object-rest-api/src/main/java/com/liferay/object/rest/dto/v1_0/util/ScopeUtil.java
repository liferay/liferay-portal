/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.dto.v1_0.util;

import com.liferay.headless.object.dto.v1_0.Scope;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;

/**
 * @author Petteri Karttunen
 */
public class ScopeUtil {

	public static Scope toScope(Group group) {
		if (group == null) {
			return null;
		}

		return new Scope() {
			{
				setExternalReferenceCode(group::getExternalReferenceCode);
				setType(() -> _getType(group));
			}
		};
	}

	public static Scope toScope(long groupId) {
		return toScope(GroupLocalServiceUtil.fetchGroup(groupId));
	}

	private static Scope.Type _getType(Group group) {
		if (group.isCMS()) {
			return Scope.Type.CMS;
		}
		else if (group.isDepot()) {
			return Scope.Type.ASSET_LIBRARY;
		}
		else if (group.isSite()) {
			return Scope.Type.SITE;
		}

		return null;
	}

}