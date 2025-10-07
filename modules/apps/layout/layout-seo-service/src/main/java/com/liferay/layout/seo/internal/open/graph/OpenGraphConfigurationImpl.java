/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.seo.internal.open.graph;

import com.liferay.layout.seo.internal.configuration.LayoutSEOCompanyConfiguration;
import com.liferay.layout.seo.internal.configuration.LayoutSEOGroupConfiguration;
import com.liferay.layout.seo.model.LayoutSEOSite;
import com.liferay.layout.seo.open.graph.OpenGraphConfiguration;
import com.liferay.layout.seo.service.LayoutSEOSiteLocalService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(service = OpenGraphConfiguration.class)
public class OpenGraphConfigurationImpl implements OpenGraphConfiguration {

	@Override
	public boolean isLayoutTranslatedLanguagesEnabled(Company company)
		throws PortalException {

		LayoutSEOCompanyConfiguration layoutSEOCompanyConfiguration =
			_configurationProvider.getCompanyConfiguration(
				LayoutSEOCompanyConfiguration.class, company.getCompanyId());

		if (!layoutSEOCompanyConfiguration.enableOpenGraph()) {
			return false;
		}

		return layoutSEOCompanyConfiguration.enableLayoutTranslatedLanguages();
	}

	@Override
	public boolean isLayoutTranslatedLanguagesEnabled(Group group)
		throws PortalException {

		LayoutSEOCompanyConfiguration layoutSEOCompanyConfiguration =
			_configurationProvider.getCompanyConfiguration(
				LayoutSEOCompanyConfiguration.class, group.getCompanyId());

		if (!layoutSEOCompanyConfiguration.enableOpenGraph() ||
			!_isOpenGraphEnabled(group)) {

			return false;
		}

		if (layoutSEOCompanyConfiguration.enableLayoutTranslatedLanguages()) {
			return true;
		}

		LayoutSEOGroupConfiguration layoutSEOGroupConfiguration =
			_configurationProvider.getGroupConfiguration(
				LayoutSEOGroupConfiguration.class, group.getGroupId());

		return layoutSEOGroupConfiguration.enableLayoutTranslatedLanguages();
	}

	@Override
	public boolean isOpenGraphEnabled(Group group) throws PortalException {
		if (!isOpenGraphEnabled(group.getCompanyId())) {
			return false;
		}

		return _isOpenGraphEnabled(group);
	}

	@Override
	public boolean isOpenGraphEnabled(long companyId) throws PortalException {
		LayoutSEOCompanyConfiguration layoutSEOCompanyConfiguration =
			_configurationProvider.getCompanyConfiguration(
				LayoutSEOCompanyConfiguration.class, companyId);

		return layoutSEOCompanyConfiguration.enableOpenGraph();
	}

	private boolean _isOpenGraphEnabled(Group group) {
		LayoutSEOSite layoutSEOSite =
			_layoutSEOSiteLocalService.fetchLayoutSEOSiteByGroupId(
				group.getGroupId());

		if ((layoutSEOSite == null) || layoutSEOSite.isOpenGraphEnabled()) {
			return true;
		}

		return false;
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private LayoutSEOSiteLocalService _layoutSEOSiteLocalService;

}