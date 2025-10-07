/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.model.listener;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.service.CPDAvailabilityEstimateLocalService;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalService;

import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(service = ModelListener.class)
public class CProductModelListener extends BaseModelListener<CProduct> {

	@Override
	public void onAfterUpdate(CProduct originalCProduct, CProduct cProduct) {
		if (Objects.equals(
				originalCProduct.getExternalReferenceCode(),
				cProduct.getExternalReferenceCode())) {

			return;
		}

		_updateSiteNavigationMenuItem(
			cProduct.getExternalReferenceCode(),
			originalCProduct.getExternalReferenceCode());
	}

	@Override
	public void onBeforeRemove(CProduct cProduct) {
		_cpdAvailabilityEstimateLocalService.
			deleteCPDAvailabilityEstimateByCProductId(cProduct.getCProductId());
	}

	private void _updateSiteNavigationMenuItem(
		String externalReferenceCode, String originalExternalReferenceCode) {

		List<SiteNavigationMenuItem> siteNavigationMenuItems =
			_siteNavigationMenuItemLocalService.getSiteNavigationMenuItems(
				CPDefinition.class.getName());

		for (SiteNavigationMenuItem siteNavigationMenuItem :
				siteNavigationMenuItems) {

			UnicodeProperties unicodeProperties =
				UnicodePropertiesBuilder.fastLoad(
					siteNavigationMenuItem.getTypeSettings()
				).build();

			String productNavigationMenuItemExternalReferenceCode =
				unicodeProperties.getProperty("externalReferenceCode");

			if (!Objects.equals(
					originalExternalReferenceCode,
					productNavigationMenuItemExternalReferenceCode)) {

				continue;
			}

			unicodeProperties.setProperty(
				"externalReferenceCode", externalReferenceCode);

			siteNavigationMenuItem.setTypeSettings(
				unicodeProperties.toString());

			_siteNavigationMenuItemLocalService.updateSiteNavigationMenuItem(
				siteNavigationMenuItem);
		}
	}

	@Reference
	private CPDAvailabilityEstimateLocalService
		_cpdAvailabilityEstimateLocalService;

	@Reference
	private SiteNavigationMenuItemLocalService
		_siteNavigationMenuItemLocalService;

}