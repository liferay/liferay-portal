/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.model.listener;

import com.liferay.frontend.js.audiences.ElementVariations;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariation;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ModelListener;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(service = ModelListener.class)
public class ElementVariationsLayoutModelListener
	extends BaseModelListener<Layout> {

	@Override
	public void onAfterRemove(Layout layout) {
		_portalCache.remove(layout.getPlid());
	}

	@Override
	public void onAfterUpdate(Layout originalLayout, Layout layout) {
		_portalCache.remove(layout.getPlid());
	}

	@Activate
	protected void activate() {
		_portalCache =
			(PortalCache<Long, ElementVariations>)_multiVMPool.getPortalCache(
				LayoutPageTemplateStructureRelElementVariation.class.getName());
	}

	@Deactivate
	protected void deactivate() {
		_multiVMPool.removePortalCache(
			LayoutPageTemplateStructureRelElementVariation.class.getName());
	}

	@Reference
	private MultiVMPool _multiVMPool;

	private PortalCache<Long, ElementVariations> _portalCache;

}