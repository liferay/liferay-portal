/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.exportimport.content.processor;

import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.SingleVMPool;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(service = JournalArticleExportImportProcessorCache.class)
public class JournalArticleExportImportProcessorCache {

	public void clear() {
		_portalCache.removeAll();
	}

	public String get(String key) {
		return _portalCache.get(key);
	}

	public void put(String key, String content) {
		_portalCache.put(key, content);
	}

	@Activate
	protected void activate() {
		_portalCache =
			(PortalCache<String, String>)_singleVMPool.getPortalCache(
				JournalArticleExportImportProcessorCache.class.getName());
	}

	private PortalCache<String, String> _portalCache;

	@Reference
	private SingleVMPool _singleVMPool;

}