/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.frontend.data.set;

import com.liferay.frontend.data.set.SystemFDSEntry;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.site.cms.site.initializer.contributor.CMSSectionTypeContributor;
import com.liferay.site.cms.site.initializer.internal.constants.CMSSiteInitializerFDSNames;
import com.liferay.site.cms.site.initializer.internal.display.context.SectionDisplayContextUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

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

		StringBundler sb = new StringBundler();

		sb.append("(cmsSection eq 'contents' or cmsSection eq 'files'");

		for (CMSSectionTypeContributor cmsSectionTypeContributor :
				_cmsSectionTypeContributors) {

			String cmsSectionType =
				cmsSectionTypeContributor.getCMSSectionType();

			if (Validator.isNull(cmsSectionType)) {
				continue;
			}

			sb.append(" or cmsSection eq '");
			sb.append(cmsSectionType);
			sb.append("'");
		}

		sb.append(") and objectDefinitionExternalReferenceCode ne '");
		sb.append(
			ObjectEntryFolderConstants.
				EXTERNAL_REFERENCE_CODE_OBJECT_ENTRY_FOLDER);
		sb.append("' and rootDescendantNode eq false");

		String filterString = SectionDisplayContextUtil.appendStatus(
			SectionDisplayContextUtil.appendGroupIds(
				sb.toString(), httpServletRequest));

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

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile List<CMSSectionTypeContributor>
		_cmsSectionTypeContributors;

}