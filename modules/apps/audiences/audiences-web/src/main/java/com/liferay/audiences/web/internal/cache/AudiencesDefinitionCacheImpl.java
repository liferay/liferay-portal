/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.web.internal.cache;

import com.liferay.audiences.cache.AudiencesDefinitionCache;
import com.liferay.audiences.model.AudiencesEntry;
import com.liferay.frontend.js.audiences.AudiencesDefinition;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(service = AudiencesDefinitionCache.class)
public class AudiencesDefinitionCacheImpl implements AudiencesDefinitionCache {

	@Override
	public AudiencesDefinition getAudiencesDefinition(long companyId) {
		return _portalCache.get(companyId);
	}

	@Override
	public void putAudiencesDefinition(
		long companyId, AudiencesDefinition audiencesDefinition) {

		_portalCache.put(companyId, audiencesDefinition);
	}

	@Override
	public void removeAudiencesDefinition(long companyId) {
		_portalCache.remove(companyId);
	}

	@Activate
	protected void activate() {
		_portalCache =
			(PortalCache<Long, AudiencesDefinition>)_multiVMPool.getPortalCache(
				AudiencesEntry.class.getName());
	}

	@Deactivate
	protected void deactivate() {
		_multiVMPool.removePortalCache(AudiencesEntry.class.getName());
	}

	@Reference
	private MultiVMPool _multiVMPool;

	private PortalCache<Long, AudiencesDefinition> _portalCache;

}