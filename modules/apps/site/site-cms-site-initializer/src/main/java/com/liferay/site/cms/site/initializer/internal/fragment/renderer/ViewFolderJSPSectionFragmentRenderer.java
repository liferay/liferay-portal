/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.configuration.DLConfiguration;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.site.cms.site.initializer.internal.display.context.ViewFolderSectionDisplayContext;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Galluzzi
 */
@Component(
	configurationPid = "com.liferay.document.library.configuration.DLConfiguration",
	service = FragmentRenderer.class
)
public class ViewFolderJSPSectionFragmentRenderer
	extends BaseJSPSectionFragmentRenderer<ViewFolderSectionDisplayContext> {

	@Override
	public String getCollectionKey() {
		return "sections";
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_dlConfiguration = ConfigurableUtil.createConfigurable(
			DLConfiguration.class, properties);
	}

	@Override
	protected ViewFolderSectionDisplayContext getDisplayContext(
		HttpServletRequest httpServletRequest) {

		return new ViewFolderSectionDisplayContext(
			_depotEntryLocalService, _dlConfiguration, _groupLocalService,
			httpServletRequest, language, _objectDefinitionService,
			_objectDefinitionSettingLocalService,
			_objectEntryFolderLocalService,
			_objectEntryFolderModelResourcePermission, _portal);
	}

	@Override
	protected String getLabelKey() {
		return "view-folder";
	}

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	private volatile DLConfiguration _dlConfiguration;

	@Reference
	private GroupLocalService _groupLocalService;

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

}