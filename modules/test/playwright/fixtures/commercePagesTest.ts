/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '@playwright/test';

import {CommerceAccountManagementPage} from '../pages/commerce/commerce-account-web/commerceAccountManagementPage';
import {CommerceChannelDefaultsPage} from '../pages/commerce/commerce-account-web/commerceChannelDefaultsPage';
import {CommerceCartPage} from '../pages/commerce/commerce-cart-content-web/commerceCartPage';
import {CommerceCartSummaryPage} from '../pages/commerce/commerce-cart-content-web/commerceCartSummaryPage';
import {CommerceAdminCatalogsPage} from '../pages/commerce/commerce-catalog-web/commerceAdminCatalogsPage';
import {CommerceAdminChannelDetailsCountriesPage} from '../pages/commerce/commerce-channel-web/commerceAdminChannelDetailsCountriesPage';
import {CommerceAdminChannelDetailsCurrenciesPage} from '../pages/commerce/commerce-channel-web/commerceAdminChannelDetailsCurrenciesPage';
import {CommerceAdminChannelDetailsPage} from '../pages/commerce/commerce-channel-web/commerceAdminChannelDetailsPage';
import {CommerceAdminChannelDetailsTypePage} from '../pages/commerce/commerce-channel-web/commerceAdminChannelDetailsTypePage';
import {CommerceAdminChannelsPage} from '../pages/commerce/commerce-channel-web/commerceAdminChannelsPage';
import {CheckoutPage} from '../pages/commerce/commerce-checkout-web/checkoutPage';
import {CommerceAdminCurrenciesPage} from '../pages/commerce/commerce-currency-web/commerceAdminCurrenciesPage';
import {CommerceAdminCurrencyDetailsPage} from '../pages/commerce/commerce-currency-web/commerceAdminCurrencyDetailsPage';
import {CommerceAdminHealthCheckPage} from '../pages/commerce/commerce-health-status-web/commerceAdminHealthCheckPage';
import {CommerceLayoutsPage} from '../pages/commerce/commerce-order-content-web/commerceLayoutsPage';
import {PendingOrdersPage} from '../pages/commerce/commerce-order-content-web/pendingOrdersPage';
import {PlacedOrderPage} from '../pages/commerce/commerce-order-content-web/placedOrderPage';
import {PlacedOrdersPage} from '../pages/commerce/commerce-order-content-web/placedOrdersPage';
import {ReturnDetailsPage} from '../pages/commerce/commerce-order-content-web/returnDetailsPage';
import {ReturnsPage} from '../pages/commerce/commerce-order-content-web/returnsPage';
import {CommerceAdminOrderDetailsPage} from '../pages/commerce/commerce-order-web/commerceAdminOrderDetailsPage';
import {CommerceAdminOrderNotesPage} from '../pages/commerce/commerce-order-web/commerceAdminOrderNotesPage';
import {CommerceAdminOrderTypeDetailsPage} from '../pages/commerce/commerce-order-web/commerceAdminOrderTypeDetailsPage';
import {CommerceAdminOrderTypesPage} from '../pages/commerce/commerce-order-web/commerceAdminOrderTypesPage';
import {CommerceAdminOrdersPage} from '../pages/commerce/commerce-order-web/commerceAdminOrdersPage';
import {CommerceAdminReturnsPage} from '../pages/commerce/commerce-order-web/commerceAdminReturnsPage';
import {OrganizationManagementPage} from '../pages/commerce/commerce-organization-web/organizationManagementPage';
import {CommercePaymentsPage} from '../pages/commerce/commerce-payment-web/commercePaymentsPage';
import {CommerceAdminDiscountDetailsPage} from '../pages/commerce/commerce-pricing-web/commerceAdminDiscountDetailsPage';
import {CommerceAdminDiscountsPage} from '../pages/commerce/commerce-pricing-web/commerceAdminDiscountsPage';
import {SpecificationFacetsPage} from '../pages/commerce/commerce-product-content-search-web/specificationFacetsPage';
import {ProductDetailsPage} from '../pages/commerce/commerce-product-content-web/productDetailsPage';
import {ProductPublisherPage} from '../pages/commerce/commerce-product-content-web/productPublisherPage';
import {AttachmentsPage} from '../pages/commerce/commerce-product-definitions-web/attachmentsPage';
import {CommerceAdminProductConfigurationEntriesPage} from '../pages/commerce/commerce-product-definitions-web/commerceAdminProductConfigurationEntriesPage';
import {CommerceAdminProductConfigurationEntryPage} from '../pages/commerce/commerce-product-definitions-web/commerceAdminProductConfigurationEntryPage';
import {CommerceAdminProductConfigurationListPage} from '../pages/commerce/commerce-product-definitions-web/commerceAdminProductConfigurationListPage';
import {CommerceAdminProductConfigurationListsPage} from '../pages/commerce/commerce-product-definitions-web/commerceAdminProductConfigurationListsPage';
import {CommerceAdminProductDetailsConfigurationPage} from '../pages/commerce/commerce-product-definitions-web/commerceAdminProductDetailsConfigurationPage';
import {CommerceAdminProductDetailsDiagramPage} from '../pages/commerce/commerce-product-definitions-web/commerceAdminProductDetailsDiagramPage';
import {CommerceAdminProductDetailsMediaPage} from '../pages/commerce/commerce-product-definitions-web/commerceAdminProductDetailsMediaPage';
import {CommerceAdminProductDetailsPage} from '../pages/commerce/commerce-product-definitions-web/commerceAdminProductDetailsPage';
import {CommerceAdminProductDetailsProductOptionsPage} from '../pages/commerce/commerce-product-definitions-web/commerceAdminProductDetailsProductOptionsPage';
import {CommerceAdminProductDetailsProductRelationsPage} from '../pages/commerce/commerce-product-definitions-web/commerceAdminProductDetailsProductRelationsPage';
import {CommerceAdminProductDetailsSkusPage} from '../pages/commerce/commerce-product-definitions-web/commerceAdminProductDetailsSkusPage';
import {CommerceAdminProductDetailsVisibilityPage} from '../pages/commerce/commerce-product-definitions-web/commerceAdminProductDetailsVisibilityPage';
import {CommerceAdminProductPage} from '../pages/commerce/commerce-product-definitions-web/commerceAdminProductPage';
import {CommerceSpecificationsPage} from '../pages/commerce/commerce-product-options-web/commerceSpecificationsPage';
import {CommerceAdminShipmentsPage} from '../pages/commerce/commerce-shipment-web/commerceAdminShipmentsPage';
import {CommerceThemeClassicCatalogPage} from '../pages/commerce/commerce-theme-classic/commerceThemeClassicCatalogPage';
import {CommerceThemeClassicOrdersPage} from '../pages/commerce/commerce-theme-classic/commerceThemeClassicOrdersPage';
import {CommerceThemeMiniumCatalogPage} from '../pages/commerce/commerce-theme-minium/commerceThemeMiniumCatalogPage';
import {CommerceThemeMiniumPage} from '../pages/commerce/commerce-theme-minium/commerceThemeMiniumPage';
import {CommerceAdminWarehouseDetailsPage} from '../pages/commerce/commerce-warehouse-web/commerceAdminWarehouseDetailsPage';
import {CommerceAdminWarehouseEligibilityPage} from '../pages/commerce/commerce-warehouse-web/commerceAdminWarehouseEligibilityPage';
import {CommerceAdminWarehousesPage} from '../pages/commerce/commerce-warehouse-web/commerceAdminWarehousesPage';
import {CommerceWishListPage} from '../pages/commerce/commerce-wish-list-web/commerceWishListPage';
import {CommerceCatalogSystemSettingsPage} from '../pages/commerce/commerceCatalogSystemSettingsPage';
import {CommerceInstanceSettingsPage} from '../pages/commerce/commerceInstanceSettingsPage';
import {CommerceMiniCartPage} from '../pages/commerce/commerceMiniCartPage';

const commercePagesTest = test.extend<{
	attachmentsPage: AttachmentsPage;
	checkoutPage: CheckoutPage;
	commerceAccountManagementPage: CommerceAccountManagementPage;
	commerceAdminCatalogsPage: CommerceAdminCatalogsPage;
	commerceAdminChannelDetailsCountriesPage: CommerceAdminChannelDetailsCountriesPage;
	commerceAdminChannelDetailsCurrenciesPage: CommerceAdminChannelDetailsCurrenciesPage;
	commerceAdminChannelDetailsPage: CommerceAdminChannelDetailsPage;
	commerceAdminChannelDetailsTypePage: CommerceAdminChannelDetailsTypePage;
	commerceAdminChannelsPage: CommerceAdminChannelsPage;
	commerceAdminCurrenciesPage: CommerceAdminCurrenciesPage;
	commerceAdminCurrencyDetailsPage: CommerceAdminCurrencyDetailsPage;
	commerceAdminDiscountDetailsPage: CommerceAdminDiscountDetailsPage;
	commerceAdminDiscountsPage: CommerceAdminDiscountsPage;
	commerceAdminHealthCheckPage: CommerceAdminHealthCheckPage;
	commerceAdminOrderDetailsPage: CommerceAdminOrderDetailsPage;
	commerceAdminOrderNotesPage: CommerceAdminOrderNotesPage;
	commerceAdminOrderTypeDetailsPage: CommerceAdminOrderTypeDetailsPage;
	commerceAdminOrderTypesPage: CommerceAdminOrderTypesPage;
	commerceAdminOrdersPage: CommerceAdminOrdersPage;
	commerceAdminProductConfigurationEntriesPage: CommerceAdminProductConfigurationEntriesPage;
	commerceAdminProductConfigurationEntryPage: CommerceAdminProductConfigurationEntryPage;
	commerceAdminProductConfigurationListPage: CommerceAdminProductConfigurationListPage;
	commerceAdminProductConfigurationListsPage: CommerceAdminProductConfigurationListsPage;
	commerceAdminProductDetailsConfigurationPage: CommerceAdminProductDetailsConfigurationPage;
	commerceAdminProductDetailsDiagramPage: CommerceAdminProductDetailsDiagramPage;
	commerceAdminProductDetailsMediaPage: CommerceAdminProductDetailsMediaPage;
	commerceAdminProductDetailsPage: CommerceAdminProductDetailsPage;
	commerceAdminProductDetailsProductOptionsPage: CommerceAdminProductDetailsProductOptionsPage;
	commerceAdminProductDetailsProductRelationsPage: CommerceAdminProductDetailsProductRelationsPage;
	commerceAdminProductDetailsSkusPage: CommerceAdminProductDetailsSkusPage;
	commerceAdminProductDetailsVisibilityPage: CommerceAdminProductDetailsVisibilityPage;
	commerceAdminProductPage: CommerceAdminProductPage;
	commerceAdminReturnsPage: CommerceAdminReturnsPage;
	commerceAdminShipmentsPage: CommerceAdminShipmentsPage;
	commerceAdminWarehouseDetailsPage: CommerceAdminWarehouseDetailsPage;
	commerceAdminWarehouseEligibilityPage: CommerceAdminWarehouseEligibilityPage;
	commerceAdminWarehousesPage: CommerceAdminWarehousesPage;
	commerceCartPage: CommerceCartPage;
	commerceCartSummaryPage: CommerceCartSummaryPage;
	commerceCatalogSystemSettingsPage: CommerceCatalogSystemSettingsPage;
	commerceChannelDefaultsPage: CommerceChannelDefaultsPage;
	commerceInstanceSettingsPage: CommerceInstanceSettingsPage;
	commerceLayoutsPage: CommerceLayoutsPage;
	commerceMiniCartPage: CommerceMiniCartPage;
	commercePaymentsPage: CommercePaymentsPage;
	commerceSpecificationsPage: CommerceSpecificationsPage;
	commerceThemeClassicCatalogPage: CommerceThemeClassicCatalogPage;
	commerceThemeClassicOrdersPage: CommerceThemeClassicOrdersPage;
	commerceThemeMiniumCatalogPage: CommerceThemeMiniumCatalogPage;
	commerceThemeMiniumPage: CommerceThemeMiniumPage;
	commerceWishListPage: CommerceWishListPage;
	organizationManagementPage: OrganizationManagementPage;
	pendingOrdersPage: PendingOrdersPage;
	placedOrderPage: PlacedOrderPage;
	placedOrdersPage: PlacedOrdersPage;
	productDetailsPage: ProductDetailsPage;
	productPublisherPage: ProductPublisherPage;
	returnDetailsPage: ReturnDetailsPage;
	returnsPage: ReturnsPage;
	specificationFacetsPage: SpecificationFacetsPage;
}>({
	attachmentsPage: async ({page}, use) => {
		await use(new AttachmentsPage(page));
	},
	checkoutPage: async ({page}, use) => {
		await use(new CheckoutPage(page));
	},
	commerceAccountManagementPage: async ({page}, use) => {
		await use(new CommerceAccountManagementPage(page));
	},
	commerceAdminCatalogsPage: async ({page}, use) => {
		await use(new CommerceAdminCatalogsPage(page));
	},
	commerceAdminChannelDetailsCountriesPage: async ({page}, use) => {
		await use(new CommerceAdminChannelDetailsCountriesPage(page));
	},
	commerceAdminChannelDetailsCurrenciesPage: async ({page}, use) => {
		await use(new CommerceAdminChannelDetailsCurrenciesPage(page));
	},
	commerceAdminChannelDetailsPage: async ({page}, use) => {
		await use(new CommerceAdminChannelDetailsPage(page));
	},
	commerceAdminChannelDetailsTypePage: async ({page}, use) => {
		await use(new CommerceAdminChannelDetailsTypePage(page));
	},
	commerceAdminChannelsPage: async ({page}, use) => {
		await use(new CommerceAdminChannelsPage(page));
	},
	commerceAdminCurrenciesPage: async ({page}, use) => {
		await use(new CommerceAdminCurrenciesPage(page));
	},
	commerceAdminCurrencyDetailsPage: async ({page}, use) => {
		await use(new CommerceAdminCurrencyDetailsPage(page));
	},
	commerceAdminDiscountDetailsPage: async ({page}, use) => {
		await use(new CommerceAdminDiscountDetailsPage(page));
	},
	commerceAdminDiscountsPage: async ({page}, use) => {
		await use(new CommerceAdminDiscountsPage(page));
	},
	commerceAdminHealthCheckPage: async ({page}, use) => {
		await use(new CommerceAdminHealthCheckPage(page));
	},
	commerceAdminOrderDetailsPage: async ({page}, use) => {
		await use(new CommerceAdminOrderDetailsPage(page));
	},
	commerceAdminOrderNotesPage: async ({page}, user) => {
		await user(new CommerceAdminOrderNotesPage(page));
	},
	commerceAdminOrderTypeDetailsPage: async ({page}, use) => {
		await use(new CommerceAdminOrderTypeDetailsPage(page));
	},
	commerceAdminOrderTypesPage: async ({page}, use) => {
		await use(new CommerceAdminOrderTypesPage(page));
	},
	commerceAdminOrdersPage: async ({page}, use) => {
		await use(new CommerceAdminOrdersPage(page));
	},
	commerceAdminProductConfigurationEntriesPage: async ({page}, use) => {
		await use(new CommerceAdminProductConfigurationEntriesPage(page));
	},
	commerceAdminProductConfigurationEntryPage: async ({page}, use) => {
		await use(new CommerceAdminProductConfigurationEntryPage(page));
	},
	commerceAdminProductConfigurationListPage: async ({page}, use) => {
		await use(new CommerceAdminProductConfigurationListPage(page));
	},
	commerceAdminProductConfigurationListsPage: async ({page}, use) => {
		await use(new CommerceAdminProductConfigurationListsPage(page));
	},
	commerceAdminProductDetailsConfigurationPage: async ({page}, use) => {
		await use(new CommerceAdminProductDetailsConfigurationPage(page));
	},
	commerceAdminProductDetailsDiagramPage: async ({page}, use) => {
		await use(new CommerceAdminProductDetailsDiagramPage(page));
	},
	commerceAdminProductDetailsMediaPage: async ({page}, use) => {
		await use(new CommerceAdminProductDetailsMediaPage(page));
	},
	commerceAdminProductDetailsPage: async ({page}, use) => {
		await use(new CommerceAdminProductDetailsPage(page));
	},
	commerceAdminProductDetailsProductOptionsPage: async ({page}, use) => {
		await use(new CommerceAdminProductDetailsProductOptionsPage(page));
	},
	commerceAdminProductDetailsProductRelationsPage: async ({page}, use) => {
		await use(new CommerceAdminProductDetailsProductRelationsPage(page));
	},
	commerceAdminProductDetailsSkusPage: async ({page}, use) => {
		await use(new CommerceAdminProductDetailsSkusPage(page));
	},
	commerceAdminProductDetailsVisibilityPage: async ({page}, use) => {
		await use(new CommerceAdminProductDetailsVisibilityPage(page));
	},
	commerceAdminProductPage: async ({page}, use) => {
		await use(new CommerceAdminProductPage(page));
	},
	commerceAdminReturnsPage: async ({page}, use) => {
		await use(new CommerceAdminReturnsPage(page));
	},
	commerceAdminShipmentsPage: async ({page}, use) => {
		await use(new CommerceAdminShipmentsPage(page));
	},
	commerceAdminWarehouseDetailsPage: async ({page}, use) => {
		await use(new CommerceAdminWarehouseDetailsPage(page));
	},
	commerceAdminWarehouseEligibilityPage: async ({page}, use) => {
		await use(new CommerceAdminWarehouseEligibilityPage(page));
	},
	commerceAdminWarehousesPage: async ({page}, use) => {
		await use(new CommerceAdminWarehousesPage(page));
	},
	commerceCartPage: async ({page}, use) => {
		await use(new CommerceCartPage(page));
	},
	commerceCartSummaryPage: async ({page}, use) => {
		await use(new CommerceCartSummaryPage(page));
	},
	commerceCatalogSystemSettingsPage: async ({page}, use) => {
		await use(new CommerceCatalogSystemSettingsPage(page));
	},
	commerceChannelDefaultsPage: async ({page}, use) => {
		await use(new CommerceChannelDefaultsPage(page));
	},
	commerceInstanceSettingsPage: async ({page}, use) => {
		await use(new CommerceInstanceSettingsPage(page));
	},
	commerceLayoutsPage: async ({page}, use) => {
		await use(new CommerceLayoutsPage(page));
	},
	commerceMiniCartPage: async ({page}, use) => {
		await use(new CommerceMiniCartPage(page));
	},
	commercePaymentsPage: async ({page}, use) => {
		await use(new CommercePaymentsPage(page));
	},
	commerceSpecificationsPage: async ({page}, use) => {
		await use(new CommerceSpecificationsPage(page));
	},
	commerceThemeClassicCatalogPage: async ({page}, use) => {
		await use(new CommerceThemeClassicCatalogPage(page));
	},
	commerceThemeClassicOrdersPage: async ({page}, use) => {
		await use(new CommerceThemeClassicOrdersPage(page));
	},
	commerceThemeMiniumCatalogPage: async ({page}, use) => {
		await use(new CommerceThemeMiniumCatalogPage(page));
	},
	commerceThemeMiniumPage: async ({page}, use) => {
		await use(new CommerceThemeMiniumPage(page));
	},
	commerceWishListPage: async ({page}, use) => {
		await use(new CommerceWishListPage(page));
	},
	organizationManagementPage: async ({page}, use) => {
		await use(new OrganizationManagementPage(page));
	},
	pendingOrdersPage: async ({page}, use) => {
		await use(new PendingOrdersPage(page));
	},
	placedOrderPage: async ({page}, use) => {
		await use(new PlacedOrderPage(page));
	},
	placedOrdersPage: async ({page}, use) => {
		await use(new PlacedOrdersPage(page));
	},
	productDetailsPage: async ({page}, use) => {
		await use(new ProductDetailsPage(page));
	},
	productPublisherPage: async ({page}, use) => {
		await use(new ProductPublisherPage(page));
	},
	returnDetailsPage: async ({page}, use) => {
		await use(new ReturnDetailsPage(page));
	},
	returnsPage: async ({page}, use) => {
		await use(new ReturnsPage(page));
	},
	specificationFacetsPage: async ({page}, use) => {
		await use(new SpecificationFacetsPage(page));
	},
});

export {commercePagesTest};
