/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.cache;

import com.liferay.frontend.js.audiences.ElementVariations;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariation;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 * @author Georgel Pop
 */
@Component(service = ElementVariationsCache.class)
public class ElementVariationsCache {

	public ElementVariations getElementVariations(
		long plid, long segmentsExperienceId) {

		return _portalCache.get(_getPortalCacheKey(plid, segmentsExperienceId));
	}

	public void putElementVariations(
		long plid, long segmentsExperienceId,
		ElementVariations elementVariations) {

		_portalCache.put(
			_getPortalCacheKey(plid, segmentsExperienceId), elementVariations);
	}

	public void removeElementVariations(long plid, long segmentsExperienceId) {
		_portalCache.remove(_getPortalCacheKey(plid, segmentsExperienceId));
	}

	@Activate
	protected void activate() {
		_portalCache =
			(PortalCache<String, ElementVariations>)_multiVMPool.getPortalCache(
				LayoutPageTemplateStructureRelElementVariation.class.getName());
	}

	@Deactivate
	protected void deactivate() {
		_multiVMPool.removePortalCache(
			LayoutPageTemplateStructureRelElementVariation.class.getName());
	}

	private String _getPortalCacheKey(long plid, long segmentsExperienceId) {
		return plid + StringPool.POUND + segmentsExperienceId;
	}

	@Reference
	private MultiVMPool _multiVMPool;

	private PortalCache<String, ElementVariations> _portalCache;

}