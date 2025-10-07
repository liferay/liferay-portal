/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.site.cms.site.initializer.internal.display.context.ViewSpaceSitesSummarySectionDisplayContext;
import com.liferay.site.cms.site.initializer.internal.util.InfoItemUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(service = FragmentRenderer.class)
public class ViewSpaceSitesSummaryJSPSectionFragmentRenderer
	extends BaseJSPSectionFragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "sections";
	}

	@Override
	protected Object getDisplayContext(HttpServletRequest httpServletRequest)
		throws PortalException {

		long groupId = InfoItemUtil.getGroupId(httpServletRequest);

		Group group = _groupService.getGroup(groupId);

		return new ViewSpaceSitesSummarySectionDisplayContext(
			_depotEntryService, _depotEntryGroupRelLocalService,
			group.getExternalReferenceCode(), groupId, httpServletRequest,
			_language, _userModelResourcePermission);
	}

	@Override
	protected String getJSPPath() {
		return "/view_space_sites_summary.jsp";
	}

	@Override
	protected String getLabelKey() {
		return "space-sites-summary";
	}

	@Reference
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Reference
	private DepotEntryService _depotEntryService;

	@Reference
	private GroupService _groupService;

	@Reference
	private Language _language;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.model.User)"
	)
	private ModelResourcePermission<User> _userModelResourcePermission;

}