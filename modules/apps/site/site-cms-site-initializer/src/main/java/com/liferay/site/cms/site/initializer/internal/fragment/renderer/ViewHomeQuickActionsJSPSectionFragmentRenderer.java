/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.cms.site.initializer.internal.display.context.ViewHomeQuickActionsDisplayContext;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Christian Dorado
 */
@Component(service = FragmentRenderer.class)
public class ViewHomeQuickActionsJSPSectionFragmentRenderer
	extends BaseJSPSectionFragmentRenderer<ViewHomeQuickActionsDisplayContext> {

	@Override
	public String getLabelKey() {
		return "home-quick-actions";
	}

	@Override
	protected ViewHomeQuickActionsDisplayContext getDisplayContext(
		HttpServletRequest httpServletRequest) {

		return new ViewHomeQuickActionsDisplayContext(
			_depotEntryLocalService, groupLocalService,
			_objectDefinitionService, _objectEntryFolderLocalService,
			_objectEntryFolderModelResourcePermission,
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY));
	}

	@Override
	protected String getJSPPath() {
		return "/view_home_quick_actions.jsp";
	}

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private ObjectDefinitionService _objectDefinitionService;

	@Reference
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.object.model.ObjectEntryFolder)"
	)
	private ModelResourcePermission<ObjectEntryFolder>
		_objectEntryFolderModelResourcePermission;

}