/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page} from '@playwright/test';

import {LayoutSetPrototype} from '../../../helpers/json-web-services/JSONWebServicesLayoutSetPrototypeApiHelper';
import {PagesAdminPage} from '../../../pages/layout-admin-web/PagesAdminPage';
import {PageEditorPage} from '../../../pages/layout-content-page-editor-web/PageEditorPage';
import {ApplicationsMenuPage} from '../../../pages/product-navigation-applications-menu/ApplicationsMenuPage';
import {ProductMenuPage} from '../../../pages/product-navigation-control-menu-web/ProductMenuPage';
import {UIElementsPage} from '../../../pages/uielements/UIElementsPage';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {waitForAlert} from '../../../utils/waitForAlert';
import {LayoutSetPrototypePage} from '../pages/LayoutSetPrototypePage';

export default async function createSiteTemplateWithContentPageAndAssetPublisher({
	apiHelpers,
	applicationsMenuPage,
	layoutSetPrototypePage,
	page,
	pageEditorPage,
	pagesAdminPage,
	productMenuPage,
	templateName,
}: {
	apiHelpers: any;
	applicationsMenuPage: ApplicationsMenuPage;
	layoutSetPrototypePage: LayoutSetPrototypePage;
	page: Page;
	pageEditorPage: PageEditorPage;
	pagesAdminPage: PagesAdminPage;
	productMenuPage: ProductMenuPage;
	templateName: string;
	uiElementsPage: UIElementsPage;
}): Promise<LayoutSetPrototype> {
	const layoutSetPrototype =
		await apiHelpers.jsonWebServicesLayoutSetPrototype.addLayoutSetPrototypes(
			templateName
		);

	await applicationsMenuPage.goToSiteTemplates();

	const siteTemplateUrl =
		await layoutSetPrototypePage.getSiteTemplateUrl(templateName);

	await page.goto(siteTemplateUrl);

	await productMenuPage.checkIfAdecuateProductMenu(templateName);
	await productMenuPage.openProductMenuIfClosed();

	await productMenuPage.goToPages();

	await pagesAdminPage.newButton.click();
	await layoutSetPrototypePage.addTemplatePageButton.waitFor({
		state: 'visible',
	});
	await layoutSetPrototypePage.addTemplatePageButton.click();
	await pagesAdminPage.addPage({
		name: templateName,
	});
	await pageEditorPage.addWidget('Content Management', 'Asset Publisher');

	await pageEditorPage.getFragmentId('Asset Publisher');

	await page
		.locator('#wrapper')
		.getByRole('button', {name: 'Options'})
		.click();

	const configurationModal = page.frameLocator(
		'iframe[title="Configuration"]'
	);

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: configurationModal.getByRole('group', {
			name: 'Asset Selection',
		}),
		trigger: page.getByRole('menuitem', {
			exact: true,
			name: 'Configuration',
		}),
	});

	const configurationManualInput = await configurationModal.getByLabel(
		'Manual',
		{exact: true}
	);

	if (await configurationManualInput.isHidden()) {
		await configurationModal
			.getByRole('link', {name: 'Asset Selection'})
			.click();
	}
	if (!(await configurationManualInput.isChecked())) {
		await configurationManualInput.click();

		await waitForAlert(
			configurationModal,
			'Success:You have successfully updated the setup.'
		);
	}

	const scopeSection = configurationModal.locator('#scopeContent');
	if (await scopeSection.isHidden()) {
		await configurationModal.getByRole('button', {name: 'Scope'}).click();
	}
	await scopeSection.waitFor();

	const selectButton = scopeSection.locator('button.dropdown-toggle', {
		hasText: 'Select',
	});
	await selectButton.click();

	const globalOption = configurationModal.getByRole('menuitem', {
		name: 'Global',
	});
	await globalOption.click();

	await waitForAlert(
		configurationModal,
		'Success:You have successfully updated the setup.'
	);

	const currentSiteDeleteButton = scopeSection
		.getByRole('row', {name: /^Current Site/})
		.getByLabel('Delete');
	await currentSiteDeleteButton.click();

	await waitForAlert(
		configurationModal,
		'Success:You have successfully updated the setup.'
	);

	const assetEntriesSection = configurationModal.locator(
		'#assetEntriesContent'
	);
	if (await assetEntriesSection.isHidden()) {
		await configurationModal
			.getByRole('button', {name: 'Asset Entries'})
			.click();
	}
	await assetEntriesSection.waitFor();

	const selectAssetEntriesButton = assetEntriesSection.locator(
		'button.dropdown-toggle',
		{hasText: 'Select'}
	);
	await selectAssetEntriesButton.click();

	const basicWebContentOption = configurationModal.getByRole('menuitem', {
		name: 'Basic Web Content',
	});
	await basicWebContentOption.click();

	const selectWebContentModal = await configurationModal.frameLocator(
		'iframe[title*="Select Basic Web Content"]'
	);

	await selectWebContentModal.locator('#main-content').waitFor();

	const checkbox = selectWebContentModal
		.getByTestId('row')
		.first()
		.locator('input[type="checkbox"]');
	await checkbox.check();

	const addButton = configurationModal.getByRole('button', {name: 'Add'});
	await addButton.click();

	await waitForAlert(
		configurationModal,
		'Success:You have successfully updated the setup.'
	);

	await configurationModal.getByRole('button', {name: 'Save'}).click();

	await waitForAlert(
		configurationModal,
		'Success:You have successfully updated the setup.'
	);

	await configurationModal.getByRole('button', {name: 'Cancel'}).click();

	await pageEditorPage.publishPage();

	return layoutSetPrototype;
}
