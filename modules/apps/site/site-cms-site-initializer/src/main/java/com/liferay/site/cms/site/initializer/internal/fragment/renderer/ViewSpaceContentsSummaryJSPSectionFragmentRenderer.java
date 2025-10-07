/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.site.cms.site.initializer.internal.display.context.ViewSpaceContentsSummarySectionDisplayContext;
import com.liferay.site.cms.site.initializer.internal.util.InfoItemUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(service = FragmentRenderer.class)
public class ViewSpaceContentsSummaryJSPSectionFragmentRenderer
	extends BaseJSPSectionFragmentRenderer
		<ViewSpaceContentsSummarySectionDisplayContext> {

	public String getCollectionKey() {
		return "sections";
	}

	@Override
	public String getLabelKey() {
		return "space-contents-summary";
	}

	@Override
	protected ViewSpaceContentsSummarySectionDisplayContext getDisplayContext(
		HttpServletRequest httpServletRequest) {

		return new ViewSpaceContentsSummarySectionDisplayContext(
			_depotEntryLocalService,
			InfoItemUtil.getGroupId(httpServletRequest), _groupLocalService,
			httpServletRequest, _language, _objectDefinitionService,
			_objectDefinitionSettingLocalService,
			_objectEntryFolderLocalService,
			_objectEntryFolderModelResourcePermission, _portal);
	}

	@Override
	protected String getJSPPath() {
		return "/view_space_contents_summary.jsp";
	}

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

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