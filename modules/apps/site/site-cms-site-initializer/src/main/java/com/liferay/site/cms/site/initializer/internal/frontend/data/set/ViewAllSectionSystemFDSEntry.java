/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.frontend.data.set;

import com.liferay.frontend.data.set.SystemFDSEntry;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.site.cms.site.initializer.internal.constants.CMSSiteInitializerFDSNames;
import com.liferay.site.cms.site.initializer.internal.display.context.SectionDisplayContextUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Daniel Sanz
 */
@Component(
	property = "frontend.data.set.name=" + CMSSiteInitializerFDSNames.ALL_SECTION,
	service = SystemFDSEntry.class
)
public class ViewAllSectionSystemFDSEntry implements SystemFDSEntry {

	@Override
	public String getAdditionalAPIURLParameters(
		HttpServletRequest httpServletRequest) {

		String filterString = SectionDisplayContextUtil.appendStatus(
			SectionDisplayContextUtil.appendGroupIds(
				StringBundler.concat(
					"(cmsSection eq 'contents' or cmsSection eq 'files') and ",
					"objectDefinitionExternalReferenceCode ne '",
					ObjectEntryFolderConstants.
						EXTERNAL_REFERENCE_CODE_OBJECT_ENTRY_FOLDER,
					"' and rootDescendantNode eq false"),
				httpServletRequest));

		String additionalAPIURLParameters =
			SectionDisplayContextUtil.getAdditionalAPIURLParameters(
				filterString, httpServletRequest, null);

		String searchQuery = httpServletRequest.getParameter("q");

		if (searchQuery != null) {
			return StringBundler.concat(
				additionalAPIURLParameters, "&search=",
				URLCodec.encodeURL(searchQuery));
		}

		return additionalAPIURLParameters;
	}

	@Override
	public int getDefaultItemsPerPage() {
		return 20;
	}

	@Override
	public String getDescription() {
		return "CMS All Section";
	}

	@Override
	public boolean getHideManagementBarInEmptyState() {
		return true;
	}

	@Override
	public String getName() {
		return CMSSiteInitializerFDSNames.ALL_SECTION;
	}

	@Override
	public String getPropsTransformer() {
		return "{AssetsFilesDropFDSPropsTransformer} from " +
			"site-cms-site-initializer";
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
	public boolean getSnapshotsEnabled() {
		return true;
	}

	@Override
	public String getSymbol() {
		return "sheets";
	}

	@Override
	public String getTitle() {
		return "All Section";
	}

}