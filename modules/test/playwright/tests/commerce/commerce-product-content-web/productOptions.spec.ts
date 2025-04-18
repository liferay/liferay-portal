/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {applicationsMenuPageTest} from '../../../fixtures/applicationsMenuPageTest';
import {commercePagesTest} from '../../../fixtures/commercePagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import performLogin, {performLogout} from '../../../utils/performLogin';
import {classicCommerceSetUp, guestCheckoutSetUp} from '../utils/commerce';

export const test = mergeTests(
    apiHelpersTest,
    applicationsMenuPageTest,
    commercePagesTest,
    dataApiHelpersTest,
    featureFlagsTest({
        'LPD-10562': {enabled: true},
        'LPD-20379': {enabled: true},
    }),
    loginTest()
);

test(
    'User can see SKU updated on the product details page when values are selected from multiple options',
    {tag: ['@COMMERCE-12167']},
    async ({
        apiHelpers,
        applicationsMenuPage,
        commerceAdminProductPage,
        page,
        productDetailsPage,
        site,
        widgetPagePage,
    }) => {
        const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
            groupId: site.id,
            title: getRandomString(),
        });

        await apiHelpers.headlessCommerceAdminChannel.postChannel({
            name: 'View product details',
            siteGroupId: site.id,
        });

        const catalog = await apiHelpers.headlessCommerceAdminCatalog.postCatalog(
            {
                name: 'View product details',
            });

        const option1 = await apiHelpers.headlessCommerceAdminCatalog.postOption(
            'select',
            'color',
            'Color',
            1
        );

        const option2 = await apiHelpers.headlessCommerceAdminCatalog.postOption(
            'select',
            'size',
            'Size',
            2
        );

        const product1 = await apiHelpers.headlessCommerceAdminCatalog.postProduct(
            {
                catalogId: catalog.id,
                name: {en_US: getRandomString()},
            });

        const product2 = await apiHelpers.headlessCommerceAdminCatalog.postProduct(
            {
                catalogId: catalog.id,
                name: {en_US: getRandomString()},
            });

        const productBundle =
            await apiHelpers.headlessCommerceAdminCatalog.postProduct({
                catalogId: catalog.id,
                name: {en_US: 'ProductBundle'},
                productOptions: [
                    {
                        fieldType: 'select',
                        key: 'color',
                        name: {
                            en_US: 'Color',
                        },
                        optionId: option1.id,
                        priceType: 'static',
                        priority: 1,
                        productOptionValues: [
                            {
                                deltaPrice: 10.0,
                                key: 'black',
                                name: {
                                    en_US: 'Black',
                                },
                                priority: 1,
                                quantity: 1,
                                skuId: product1.skus[0].id,
                            },
                            {
                                deltaPrice: 20.0,
                                key: 'white',
                                name: {
                                    en_US: 'White',
                                },
                                priority: 2,
                                quantity: 1,
                            },
                        ],
                        skuContributor: true,
                    },
                    {
                        fieldType: 'select',
                        key: 'size',
                        name: {
                            en_US: 'Size',
                        },
                        optionId: option2.id,
                        priceType: 'static',
                        priority: 2,
                        productOptionValues: [
                            {
                                deltaPrice: 30.0,
                                key: 'xs',
                                name: {
                                    en_US: 'XS',
                                },
                                priority: 1,
                                quantity: 1,
                            },
                            {
                                deltaPrice: 40.0,
                                key: 'xl',
                                name: {
                                    en_US: 'XL',
                                },
                                priority: 2,
                                quantity: 1,
                                skuId: product2.skus[0].id,
                            },
                        ],
                        skuContributor: true,
                    },
                ],
            });

        await applicationsMenuPage.goToProducts();

        await commerceAdminProductPage.managementToolbarSearchInput.fill(
            'ProductBundle'
        );
        await commerceAdminProductPage.managementToolbarSearchInput.press(
            'Enter');

        await commerceAdminProductPage
            .productsTableRowLink('ProductBundle')
            .click();

        await commerceAdminProductPage.generateSkus();

        const productBundleSkus = await apiHelpers.headlessCommerceAdminCatalog
            .getProduct(productBundle.productId)
            .then((product) => {
                return product.skus;
            });

        const sku1 = productBundleSkus.find(
            (sku) => sku.sku === 'WHITEXL' || sku.sku === 'XLWHITE'
        );

        await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
            sku1.id,
            {
                incrementalOrderQuantity: 2,
                name: {en_US: 'Pallet'},
                priority: 2,
                rate: 3,
            }
        );

        await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
            sku1.id,
            {
                incrementalOrderQuantity: 3,
                name: {en_US: 'Box'},
                primary: true,
                priority: 1,
                rate: 1,
            }
        );

        const sku2 = productBundleSkus.find(
            (sku) => sku.sku === 'BLACKXL' || sku.sku === 'XLBLACK'
        );

        await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
            sku2.id,
            {
                incrementalOrderQuantity: 3,
                name: {en_US: 'Box'},
                primary: true,
                priority: 1,
                rate: 1,
            }
        );

        await apiHelpers.headlessCommerceAdminCatalog.postSkuUnitOfMeasure(
            sku2.id,
            {
                incrementalOrderQuantity: 2,
                name: {en_US: 'Package'},
                priority: 2,
                rate: 0.5,
            }
        );

        await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

        await widgetPagePage.addPortlet('Product Details');

        await page.goto(`/web/${site.name}/p/productbundle`);

        await expect(
            await productDetailsPage.optionSelector('Size')).toBeVisible();

        await expect(
            await productDetailsPage.optionSelector('Color')
        ).toBeVisible();

        await (
            await productDetailsPage.optionSelector('Color')
        ).selectOption({label: 'Black'});

        await (
            await productDetailsPage.optionSelector('Size')
        ).selectOption({label: 'XL + $ 10.00'});

        await expect(await productDetailsPage.uomTable('Unit')).toBeVisible();

        await expect(
            await productDetailsPage.priceField('$ 50.00')).toBeVisible();
    });

test(
    'User can add to cart and checkout a product with upload option',
    {tag: ['@LPD-50964']},
    async ({
        apiHelpers,
        applicationsMenuPage,
        commerceAdminProductPage,
        page,
        productDetailsPage,
        site,
        widgetPagePage,
    }) => {
        test.setTimeout(180000);

        const {catalog, site} = await classicCommerceSetUp(
            apiHelpers,
            `UploadOption${getRandomString()}`
        );

        const uploadOption = await apiHelpers.headlessCommerceAdminCatalog.postOption(
            'upload',
            'Upload',
            'Upload',
            1
        );

        const productWithUploadOption =
            await apiHelpers.headlessCommerceAdminCatalog.postProduct({
                catalogId: catalog.id,
                name: {en_US: 'ProductWithUploadOption'},
                productOptions: [
                    {
                        fieldType: 'upload',
                        key: 'upload',
                        name: {
                            en_US: 'Upload',
                        },
                        optionId: uploadOption.id,
                        priceType: 'static',
                        priority: 1,
                    },
                ],
            });

        await applicationsMenuPage.goToProducts();

        await commerceAdminProductPage.managementToolbarSearchInput.fill(
            'ProductWithUploadOption'
        );
        await commerceAdminProductPage.managementToolbarSearchInput.press(
            'Enter');

        await page.goto(`/web/${site.name}/p/productwithuploadoption`);

        await expect(
            await productDetailsPage.optionSelector('Upload')).toBeVisible();


    });