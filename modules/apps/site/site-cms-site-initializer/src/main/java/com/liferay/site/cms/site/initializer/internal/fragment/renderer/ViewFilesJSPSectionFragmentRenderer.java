/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.site.cms.site.initializer.internal.display.context.ViewFilesSectionDisplayContext;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sam Ziemer
 */
@Component(service = FragmentRenderer.class)
public class ViewFilesJSPSectionFragmentRenderer
	extends BaseJSPSectionFragmentRenderer<ViewFilesSectionDisplayContext> {

	@Override
	public String getCollectionKey() {
		return "sections";
	}

	@Override
	public String getLabelKey() {
		return "files";
	}

	@Override
	protected ViewFilesSectionDisplayContext getDisplayContext(
		HttpServletRequest httpServletRequest) {

		return new ViewFilesSectionDisplayContext(
			_depotEntryLocalService, groupLocalService, httpServletRequest,
			language, _objectDefinitionService,
			_objectDefinitionSettingLocalService, _portal);
	}

	@Override
	protected String getJSPPath() {
		return "/view_files.jsp";
	}

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private ObjectDefinitionService _objectDefinitionService;

	@Reference
	private ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

	@Reference
	private Portal _portal;

}