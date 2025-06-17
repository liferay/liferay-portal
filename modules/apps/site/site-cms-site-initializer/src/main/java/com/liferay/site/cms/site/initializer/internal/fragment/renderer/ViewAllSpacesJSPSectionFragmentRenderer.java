/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.depot.service.DepotEntryPinLocalService;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.site.cms.site.initializer.internal.display.context.ViewAllSpacesDisplayContext;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(service = FragmentRenderer.class)
public class ViewAllSpacesJSPSectionFragmentRenderer
	extends BaseJSPSectionFragmentRenderer<ViewAllSpacesDisplayContext> {

	@Override
	public String getCollectionKey() {
		return "sections";
	}

	@Override
	public String getLabelKey() {
		return "all-spaces-section";
	}

	@Override
	protected ViewAllSpacesDisplayContext getDisplayContext(
		HttpServletRequest httpServletRequest) {

		return new ViewAllSpacesDisplayContext(
			_depotEntryPinLocalService, httpServletRequest, language, _portal);
	}

	@Override
	protected String getJSPPath() {
		return "/view_all_spaces.jsp";
	}

	@Reference
	private DepotEntryPinLocalService _depotEntryPinLocalService;

	@Reference
	private Portal _portal;

}