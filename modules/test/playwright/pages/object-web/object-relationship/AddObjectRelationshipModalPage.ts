/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectRelationship} from '@liferay/object-admin-rest-client-js/v1_0';
import {Locator, Page, expect} from '@playwright/test';

import {ObjectRelationshipFormPage} from './ObjectRelationshipFormPage';

export class AddNewObjectRelationshipModalPage {
	readonly modalHeader: Locator;
	readonly objectRelationshipFormPage: ObjectRelationshipFormPage;
	readonly page: Page;

	constructor(page: Page) {
		this.modalHeader = page.getByRole('heading', {
			name: 'New Relationship',
		});
		this.objectRelationshipFormPage = new ObjectRelationshipFormPage(
			page,
			'.modal-content'
		);
		this.page = page;
	}

	async handleForm({
		inherited,
		manyRecordsOf,
		objectRelationshipLabel,
		parameter,
		type,
	}: {
		inherited?: boolean;
		manyRecordsOf?: string;
		objectRelationshipLabel: string;
		parameter?: string;
		type: ObjectRelationshipType;
	}): Promise<ObjectRelationship> {
		await expect(this.modalHeader).toBeVisible();

		await this.objectRelationshipFormPage.labelInput.fill(
			objectRelationshipLabel
		);

		await this.objectRelationshipFormPage.selectType(type);

		if (inherited) {
			await this.objectRelationshipFormPage.inheritanceCheckbox.check();
		}

		if (manyRecordsOf) {
			await this.objectRelationshipFormPage.selectManyRecordsOf(
				manyRecordsOf
			);
		}

		if (parameter) {
			await this.objectRelationshipFormPage.selectParameter(parameter);
		}

		const responsePromise = this.page.waitForResponse(
			'**/object-relationships'
		);

		await this.objectRelationshipFormPage.saveButton.click();

		const response = await responsePromise;

		return response.json();
	}
}
