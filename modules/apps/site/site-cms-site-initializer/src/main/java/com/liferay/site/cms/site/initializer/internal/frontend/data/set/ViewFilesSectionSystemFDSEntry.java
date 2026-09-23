/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.frontend.data.set;

import com.liferay.frontend.data.set.SystemFDSEntry;
import com.liferay.site.cms.site.initializer.internal.constants.CMSSiteInitializerFDSNames;
import com.liferay.site.cms.site.initializer.internal.display.context.SectionDisplayContextUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Jan Brychta
 */
@Component(
	property = "frontend.data.set.name=" + CMSSiteInitializerFDSNames.FILES_SECTION,
	service = SystemFDSEntry.class
)
public class ViewFilesSectionSystemFDSEntry implements SystemFDSEntry {

	@Override
	public String getAdditionalAPIURLParameters(
		HttpServletRequest httpServletRequest) {

		String filterString = SectionDisplayContextUtil.appendStatus(
			SectionDisplayContextUtil.appendGroupIds(
				"cmsRoot eq true and cmsSection eq 'files' and " +
					"rootDescendantNode eq false",
				httpServletRequest));

		return SectionDisplayContextUtil.getAdditionalAPIURLParameters(
			filterString, httpServletRequest, null);
	}

	@Override
	public int getDefaultItemsPerPage() {
		return 20;
	}

	@Override
	public String getDescription() {
		return "CMS Files Section";
	}

	@Override
	public boolean getHideManagementBarInEmptyState() {
		return true;
	}

	@Override
	public String getName() {
		return CMSSiteInitializerFDSNames.FILES_SECTION;
	}

	@Override
	public String getRESTApplication() {
		return "/search/v1.0";
	}

	@Override
	public String getRESTEndpoint() {
		return "/v1.0/search";
	}

	@Override
	public String getRESTSchema() {
		return "SearchResult";
	}

	@Override
	public boolean getSearchAsYouType() {
		return true;
	}

	@Override
	public boolean getSearchSuggestionsEnabled() {
		return true;
	}

	@Override
	public boolean getSnapshotsEnabled() {
		return true;
	}

	@Override
	public String getSymbol() {
		return "documents-and-media";
	}

	@Override
	public String getTitle() {
		return "Files Section";
	}

}