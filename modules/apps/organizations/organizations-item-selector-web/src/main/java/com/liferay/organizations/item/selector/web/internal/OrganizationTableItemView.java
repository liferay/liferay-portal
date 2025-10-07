/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.organizations.item.selector.web.internal;

import com.liferay.item.selector.TableItemView;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchEntry;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.taglib.search.StatusSearchEntry;
import com.liferay.taglib.search.TextSearchEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Eudaldo Alonso
 */
public class OrganizationTableItemView implements TableItemView {

	public OrganizationTableItemView(Organization organization) {
		_organization = organization;
	}

	@Override
	public List<String> getHeaderNames() {
		if (FeatureFlagManagerUtil.isEnabled("LPD-35914")) {
			return ListUtil.fromArray("name", "path", "type", "status");
		}

		return ListUtil.fromArray("name", "path", "type");
	}

	@Override
	public List<SearchEntry> getSearchEntries(Locale locale) {
		List<SearchEntry> searchEntries = new ArrayList<>();

		TextSearchEntry nameTextSearchEntry = new TextSearchEntry();

		nameTextSearchEntry.setCssClass(
			"entry entry-selector table-cell-expand table-cell-minw-200");
		nameTextSearchEntry.setName(HtmlUtil.escape(_organization.getName()));

		searchEntries.add(nameTextSearchEntry);

		TextSearchEntry pathTextSearchEntry = new TextSearchEntry();

		pathTextSearchEntry.setCssClass(
			"table-cell-expand-smaller table-cell-minw-150");
		pathTextSearchEntry.setName(_getPath(_organization));

		searchEntries.add(pathTextSearchEntry);

		TextSearchEntry typeTextSearchEntry = new TextSearchEntry();

		typeTextSearchEntry.setCssClass(
			"table-cell-expand-smaller table-cell-minw-150");
		typeTextSearchEntry.setName(
			LanguageUtil.get(locale, _organization.getType()));

		searchEntries.add(typeTextSearchEntry);

		if (FeatureFlagManagerUtil.isEnabled("LPD-35914")) {
			StatusSearchEntry statusSearchEntry = new StatusSearchEntry();

			statusSearchEntry.setCssClass("text-nowrap");
			statusSearchEntry.setName(
				WorkflowConstants.getStatusLabel(_organization.getStatus()));

			searchEntries.add(statusSearchEntry);
		}

		return searchEntries;
	}

	private String _getPath(Organization organization) {
		List<Organization> organizations = new ArrayList<>();

		while (organization.getParentOrganizationId() !=
					OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID) {

			organization = OrganizationLocalServiceUtil.fetchOrganization(
				organization.getParentOrganizationId());

			if (organization == null) {
				break;
			}

			organizations.add(organization);
		}

		if (organizations.isEmpty()) {
			return StringPool.BLANK;
		}

		int size = organizations.size();

		StringBundler sb = new StringBundler(((size - 1) * 4) + 1);

		organization = organizations.get(size - 1);

		sb.append(organization.getName());

		for (int i = size - 2; i >= 0; i--) {
			organization = organizations.get(i);

			sb.append(StringPool.SPACE);
			sb.append(StringPool.GREATER_THAN);
			sb.append(StringPool.SPACE);
			sb.append(organization.getName());
		}

		return sb.toString();
	}

	private final Organization _organization;

}