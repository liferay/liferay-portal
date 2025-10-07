/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {expandSection} from '../../utils/expandSection';
import fillAndClickOutside from '../../utils/fillAndClickOutside';
import getRandomString from '../../utils/getRandomString';

type SegmentSection = 'context' | 'segments' | 'user' | 'user-organization';

type SegmentProperty =
	| 'Country'
	| 'Date of Birth'
	| 'Email Address'
	| 'First Name'
	| 'Last Name'
	| 'Name'
	| 'Organization'
	| 'Parent Organization'
	| 'Regular Role'
	| 'Segments'
	| 'Site'
	| string
	| 'Tag'
	| 'Type';

type SegmentProperties = Partial<Record<SegmentSection, SegmentProperty[]>>;

export class SegmentEditorPage {
	readonly page: Page;

	readonly emptyDropzone: Locator;
	readonly loading: Locator;
	readonly saveButton: Locator;

	constructor(page: Page) {
		this.page = page;

		this.emptyDropzone = page.locator('.empty-drop-zone');
		this.loading = page.locator('.sheet .loading-animation');
		this.saveButton = page.getByText('Save');
	}

	/**
	 * Creates a segment with the given name and properties.
	 * For now we allow passing a plain list of properties
	 * for each section so nesting is not supported
	 */

	async createSegment(name: string, properties: SegmentProperties) {

		// Fill title

		await fillAndClickOutside(
			this.page,
			this.page.getByPlaceholder('Untitled Segment'),
			name
		);

		// Add properties of each section

		for (const [section, items] of Object.entries(properties)) {
			for (const property of items) {
				await this.addProperty(section as SegmentSection, property);
			}
		}

		await this.saveButton.click();
	}

	/**
	 * Adds the given property in the last position
	 * of the given section
	 */

	async addProperty(section: SegmentSection, property: SegmentProperty) {
		const panel = this.page.locator(`#${section}`);
		const header = panel.locator('.panel-header');
		const body = panel.locator('.panel-body');

		// Calculate target dropzone

		const dropzone = this.page.locator(`.drop-zone-${section}`);
		const target =
			(await dropzone.count()) === 0
				? this.emptyDropzone
				: dropzone.last();

		// Open panel section if it's not already open

		await expandSection(header);

		await body.waitFor();

		// Map known label exceptions

		const labelMap: Record<string, string> = {
			'Country': 'Drag Country',
			'Name': 'Drag Name',
			'Organization': 'Drag Organization',
			'Parent Organization': 'Drag Parent Organization',
			'Segments': 'Drag Segment',
			'Site': 'Drag Site',
			'Tag': 'Drag Tag',
			'Team': 'Drag Team',
		};

		const label = labelMap[property] ?? `Drag ${property}`;

		// Add property to desired dropzone

		try {
			await this.page.getByLabel(label, {exact: true}).press('Enter');
		}
		catch {
			try {
				await this.page
					.getByRole('menuitem', {exact: true, name: label})
					.press('Enter');
			}
			catch {
				await this.page
					.locator('li', {hasText: property})
					.press('Enter');
			}
		}

		await target.press('Enter');

		await this.loading.waitFor();
		await this.loading.waitFor({state: 'hidden'});

		// Configure property

		await this.configureProperty(property, getRandomString());
	}

	/**
	 * Allows configuring properties that need it before saving.
	 * It looks for an unconfigured property instance and configures it
	 */

	async configureProperty(property: SegmentProperty, value: string) {
		if (property === 'First Name') {
			const input = this.page.locator(
				'[aria-label="First Name: Input a value."][value=""]'
			);

			await fillAndClickOutside(this.page, input, value);
		}
		else if (property === 'Name') {
			const input = this.page.locator(
				'[aria-label="Name: Input a value."][value=""]'
			);

			await fillAndClickOutside(this.page, input, value);
		}
	}
}
