/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.frontend.data.set.action;

import com.liferay.frontend.data.set.FDSEntryItemImportPolicy;
import com.liferay.frontend.data.set.action.FDSCreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.site.cms.site.initializer.contributor.CMSStructureObjectFolderContributor;
import com.liferay.site.cms.site.initializer.internal.constants.CMSSiteInitializerFDSNames;
import com.liferay.site.cms.site.initializer.internal.display.context.SectionDisplayContextUtil;
import com.liferay.site.cms.site.initializer.internal.util.ActionUtil;

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
	service = FDSCreationMenu.class
)
public class ViewAllSectionFDSCreationMenu implements FDSCreationMenu {

	@Override
	public CreationMenu getCreationMenu(HttpServletRequest httpServletRequest) {
		return SectionDisplayContextUtil.getCreationMenu(
			ActionUtil.getAllSectionCreationMenuDropdownItems(
				_cmsStructureObjectFolderContributors, httpServletRequest),
			httpServletRequest, null);
	}

	@Override
	public FDSEntryItemImportPolicy getFDSEntryItemImportPolicy() {
		return FDSEntryItemImportPolicy.GROUP_PROXY;
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile List<CMSStructureObjectFolderContributor>
		_cmsStructureObjectFolderContributors;

}