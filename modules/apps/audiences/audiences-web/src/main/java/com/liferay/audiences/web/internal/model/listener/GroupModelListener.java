/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.web.internal.model.listener;

import com.liferay.audiences.cache.AudiencesDefinitionCache;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Group;

/**
 * @author Víctor Galán
 */
public class GroupModelListener extends BaseModelListener<Group> {

	public GroupModelListener(
		AudiencesDefinitionCache audiencesDefinitionCache) {

		_audiencesDefinitionCache = audiencesDefinitionCache;
	}

	@Override
	public void onAfterRemove(Group group) {
		_audiencesDefinitionCache.removeAudiencesDefinition(
			group.getCompanyId());
	}

	private final AudiencesDefinitionCache _audiencesDefinitionCache;

}