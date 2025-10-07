/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.headless.asset.library.resource.v1_0.AssetLibraryResource;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.cms.site.initializer.internal.display.context.ViewRecycleBinSectionDisplayContext;
import com.liferay.trash.TrashHelper;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Leite
 */
@Component(service = FragmentRenderer.class)
public class ViewRecycleBinJSPSectionFragmentRenderer
	extends BaseJSPSectionFragmentRenderer
		<ViewRecycleBinSectionDisplayContext> {

	@Override
	public String getLabelKey() {
		return "recycle-bin";
	}

	@Override
	protected ViewRecycleBinSectionDisplayContext getDisplayContext(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return new ViewRecycleBinSectionDisplayContext(
			_assetLibraryResourceFactory, _depotEntryLocalService,
			themeDisplay.getScopeGroupId(), groupLocalService,
			httpServletRequest, language, _objectDefinitionService,
			_objectDefinitionSettingLocalService,
			_objectEntryFolderLocalService,
			_objectEntryFolderModelResourcePermission, _portal, _trashHelper);
	}

	@Override
	protected String getJSPPath() {
		return "/view_recycle_bin.jsp";
	}

	@Reference
	private AssetLibraryResource.Factory _assetLibraryResourceFactory;

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private ObjectDefinitionService _objectDefinitionService;

	@Reference
	private ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

	@Reference
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.object.model.ObjectEntryFolder)"
	)
	private ModelResourcePermission<ObjectEntryFolder>
		_objectEntryFolderModelResourcePermission;

	@Reference
	private Portal _portal;

	@Reference
	private TrashHelper _trashHelper;

}