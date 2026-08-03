/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.frontend.data.set;

import com.liferay.frontend.data.set.SystemFDSEntry;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.site.cms.site.initializer.internal.constants.CMSSiteInitializerFDSNames;
import com.liferay.site.cms.site.initializer.internal.display.context.SectionDisplayContextUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Veronica Gonzalez
 */
@Component(
	property = "frontend.data.set.name=" + CMSSiteInitializerFDSNames.OVERDUE_REVIEWS_SECTION,
	service = SystemFDSEntry.class
)
public class ViewOverdueReviewsSectionSystemFDSEntry implements SystemFDSEntry {

	@Override
	public String getAdditionalAPIURLParameters(
		HttpServletRequest httpServletRequest) {

		String filterString = StringBundler.concat(
			"dateReview lt now() and (cmsSection eq 'contents' or cmsSection ",
			"eq 'files') and objectDefinitionExternalReferenceCode ne '",
			ObjectEntryFolderConstants.
				EXTERNAL_REFERENCE_CODE_OBJECT_ENTRY_FOLDER,
			"' and rootDescendantNode eq false");

		long groupId = GetterUtil.getLong(
			httpServletRequest.getParameter("groupId"));

		if (groupId > 0) {
			filterString = StringBundler.concat(
				filterString, " and groupIds/any(g:g eq ", groupId, ")");
		}

		filterString = SectionDisplayContextUtil.appendGroupIds(
			filterString, httpServletRequest);

		filterString = SectionDisplayContextUtil.appendStatus(filterString);

		return StringBundler.concat(
			"emptySearch=true&filter=", filterString,
			"&nestedFields=embedded,systemProperties.objectDefinitionBrief",
			"&sort=dateReview:asc");
	}

	@Override
	public int getDefaultItemsPerPage() {
		return 20;
	}

	@Override
	public String getDescription() {
		return "CMS Overdue Reviews Section";
	}

	@Override
	public boolean getHideManagementBarInEmptyState() {
		return true;
	}

	@Override
	public String getName() {
		return CMSSiteInitializerFDSNames.OVERDUE_REVIEWS_SECTION;
	}

	@Override
	public String getPropsTransformer() {
		return "{OverdueReviewsFDSPropsTransformer} from " +
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
	public String getSymbol() {
		return "date";
	}

	@Override
	public String getTitle() {
		return "Overdue Reviews Section";
	}

}