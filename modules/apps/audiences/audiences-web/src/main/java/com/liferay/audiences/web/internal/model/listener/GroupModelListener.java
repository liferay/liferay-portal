/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.web.internal.model.listener;

import com.liferay.audiences.model.AudiencesEntry;
import com.liferay.frontend.js.audiences.AudiencesDefinition;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelListener;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(service = ModelListener.class)
public class GroupModelListener extends BaseModelListener<Group> {

	@Override
	public void onAfterRemove(Group group) {
		_portalCache.remove(group.getCompanyId());
	}

	@Activate
	protected void activate() {
		_portalCache =
			(PortalCache<Long, AudiencesDefinition>)_multiVMPool.getPortalCache(
				AudiencesEntry.class.getName());
	}

	@Reference
	private MultiVMPool _multiVMPool;

	private PortalCache<Long, AudiencesDefinition> _portalCache;

}