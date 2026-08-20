/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.frontend.data.set;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.frontend.data.set.SystemFDSEntry;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.site.cms.site.initializer.internal.constants.CMSSiteInitializerFDSNames;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = "frontend.data.set.name=" + CMSSiteInitializerFDSNames.BROKEN_LINKS_SECTION,
	service = SystemFDSEntry.class
)
public class ViewBrokenLinksSectionSystemFDSEntry implements SystemFDSEntry {

	@Override
	public String getAdditionalAPIURLParameters(
		HttpServletRequest httpServletRequest) {

		long groupId = GetterUtil.getLong(
			httpServletRequest.getParameter("groupId"));

		if (groupId <= 0) {
			return null;
		}

		DepotEntry depotEntry = _depotEntryLocalService.fetchGroupDepotEntry(
			groupId);

		if (depotEntry == null) {
			return null;
		}

		return "assetLibraryId=" + depotEntry.getDepotEntryId();
	}

	@Override
	public int getDefaultItemsPerPage() {
		return 20;
	}

	@Override
	public String getDescription() {
		return "CMS Broken Links Section";
	}

	@Override
	public boolean getHideManagementBarInEmptyState() {
		return true;
	}

	@Override
	public String getName() {
		return CMSSiteInitializerFDSNames.BROKEN_LINKS_SECTION;
	}

	@Override
	public String getPropsTransformer() {
		return "{BrokenLinksFDSPropsTransformer} from " +
			"site-cms-site-initializer";
	}

	@Override
	public String getRESTApplication() {
		return "/headless-cms/v1.0";
	}

	@Override
	public String getRESTEndpoint() {
		return "/v1.0/broken-link-assets";
	}

	@Override
	public String getRESTSchema() {
		return "BrokenLinkAsset";
	}

	@Override
	public String getSymbol() {
		return "link";
	}

	@Override
	public String getTitle() {
		return "Broken Links Section";
	}

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

}