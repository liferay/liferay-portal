/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export class ApplicationPage {
	readonly addEndpointButton: Locator;
	readonly addSchemaButton: Locator;
	readonly applicationTitleTextBox: Locator;
	readonly createButton: Locator;
	readonly endpointConfigurationSchemaSelector: Locator;
	readonly endpointPathTextBox: Locator;
	readonly httpMethodButton: Locator;
	readonly page: Page;
	readonly pathParameterTextBox: Locator;
	readonly publishButton: Locator;
	readonly schemaNameTextBox: Locator;
	readonly schemaObjectDefinitionSelector: Locator;

	constructor(page: Page) {
		this.addEndpointButton = page.getByLabel('Add API Endpoint');
		this.addSchemaButton = page.getByLabel('Add New Schema');
		this.applicationTitleTextBox = page.getByRole('textbox', {
			name: 'Enter title.',
		});
		this.createButton = page.getByRole('button', {name: 'Create'});
		this.endpointConfigurationSchemaSelector = page.getByLabel(
			'Response Body Schema',
			{exact: true}
		);
		this.endpointPathTextBox = page.getByPlaceholder('Enter Path');
		this.httpMethodButton = page.getByLabel('Method');
		this.page = page;
		this.pathParameterTextBox = page.getByPlaceholder('{Enter Parameter}');
		this.publishButton = page.getByRole('button', {name: 'Publish'});
		this.schemaNameTextBox = page.getByPlaceholder('Enter name.');
		this.schemaObjectDefinitionSelector = page.getByLabel(
			'Select an Object Definition',
			{exact: true}
		);
	}

	async configureSchemaOfEndpoint(name: string) {
		await this.endpointConfigurationSchemaSelector.click();
		await this.page.getByRole('menuitem', {name}).click();
	}

	async goToDetailsTab() {
		await this.page.getByRole('button', {name: 'Details'}).click();
		await this.page.waitForLoadState();
	}

	async goToEndpointsTab() {
		await this.page.getByRole('button', {name: 'Endpoints'}).click();
		await this.page.waitForLoadState();
	}

	async goToSchemasTab() {
		await this.page.getByRole('button', {name: 'Schemas'}).click();
		await this.page.waitForLoadState();
	}

	async goToEndpointConfigurationTab() {
		await this.page.getByRole('tab', {name: 'Configuration'}).click();
		await this.page.waitForLoadState();
	}

	async goToEndpointInfoTab() {
		await this.page.getByRole('tab', {name: 'Info'}).click();
		await this.page.waitForLoadState();
	}

	async goToEditEndpoint(path: string) {
		await this.page.getByRole('button', {name: path}).click();
		await this.page.waitForLoadState();
	}

	async selectEndpointRequestSchema(name: string) {
		await this.page.getByLabel('Request Body Schema').click();
		await this.page.getByRole('menuitem', {name}).click();
	}

	async selectEndpointResponseSchema(name: string) {
		await this.page.getByLabel('Response Body Schema').click();
		await this.page.getByRole('menuitem', {name}).click();
	}

	async setEndpointMethod(method: 'GET' | 'POST') {
		await this.page.getByLabel('Method').click();
		await this.page.getByRole('menuitem', {name: method}).click();
	}

	async setEndpointScope(scope: 'Company' | 'Site') {
		await this.page.getByLabel('Select Scope').click();
		await this.page.getByRole('menuitem', {name: scope}).click();
	}

	async setEndpointType(type: 'Collection' | 'Single Element') {
		await this.page.getByLabel('Select Type').click();
		await this.page.getByRole('menuitem', {name: type}).click();
	}

	async setSchemaMainObjectDefinition(objectName: string) {
		await this.schemaObjectDefinitionSelector.click();
		await this.page.getByRole('menuitem', {name: objectName}).click();
	}

	async createEndpoint(
		method: 'GET' | 'POST',
		scope: 'Company' | 'Site',
		path: string
	) {
		await this.goToEndpointsTab();
		await this.addEndpointButton.click();
		await this.setEndpointMethod(method);
		await this.setEndpointScope(scope);
		await this.endpointPathTextBox.fill(path);
		await this.createButton.click();
	}

	async createSingleElementEndpoint(
		scope: 'Company' | 'Site',
		path: string,
		pathParameter: string
	) {
		await this.goToEndpointsTab();
		await this.addEndpointButton.click();
		await this.setEndpointMethod('GET');
		await this.setEndpointType('Single Element');
		await this.setEndpointScope(scope);
		await this.endpointPathTextBox.fill(path);
		await this.pathParameterTextBox.fill(pathParameter);
		await this.createButton.click();
	}
}
