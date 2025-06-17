/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {cmsPagesTest} from './fixtures/cmsPagesTest';
import {FIELD_TYPES, StructureBuilderPage} from './pages/StructureBuilderPage';

const test = mergeTests(
	cmsPagesTest,
	featureFlagsTest({
		'LPD-11232': {enabled: true},
		'LPD-17564': {enabled: true},
	}),
	loginTest(),
	pageEditorPagesTest
);

test(
	'Structures can be saved and published',
	{tag: '@LPD-36752'},
	async ({page, structureBuilderPage}) => {

		// Go to the Structure Builder

		await structureBuilderPage.createStructure();

		await structureBuilderPage.enableForAllSpaces();

		// Change label and name

		const label = `Structure${getRandomInt()}`;

		await structureBuilderPage.changeStructureSettings({
			label,
			name: label,
		});

		// Add fields and check they are selected by default

		await structureBuilderPage.addField('Text');

		await expect(
			page.locator('.breadcrumb-link', {hasText: 'Text'})
		).toBeVisible();

		await structureBuilderPage.addField('Long Text');

		await expect(
			page.locator('.breadcrumb-link', {hasText: 'Long Text'})
		).toBeVisible();

		// Save the structure

		const {id} = await structureBuilderPage.saveStructure();

		await expect(page.locator('.alert-danger')).not.toBeVisible();

		// Remove a field

		await structureBuilderPage.deleteFields([{label: 'Long Text'}]);

		// Publish it

		await structureBuilderPage.publishStructure();

		// Check name changes in management bar

		await expect(
			page.locator('.management-bar').getByText(label)
		).toBeVisible();

		// Check another field with same name can not be added

		await structureBuilderPage.addField('Text');
		await structureBuilderPage.selectFields([{label: 'Text', nth: 1}]);
		await structureBuilderPage.changeFieldSettings({name: 'text'});

		// Delete structure

		await structureBuilderPage.deleteStructure(id);
	}
);

test(
	'Structures can be saved with all type of fields',
	{tag: '@LPD-36752'},
	async ({picklistBuilderPage, structureBuilderPage}) => {

		// Add a picklist

		const picklist = await picklistBuilderPage.createPicklist();

		// Go to the Structure Builder

		await structureBuilderPage.createStructure();

		await structureBuilderPage.enableForAllSpaces();

		// Change label and name

		const label = `Structure${getRandomInt()}`;

		await structureBuilderPage.changeStructureSettings({
			label,
			name: label,
		});

		// Add a field of each type

		for (const type of FIELD_TYPES) {
			await structureBuilderPage.addField(type);

			if (type === 'Single Select' || type === 'Multiselect') {
				await structureBuilderPage.changeFieldSettings({
					picklist: picklist.name,
				});
			}
		}

		// Save and publish the structure

		const {id} = await structureBuilderPage.saveStructure();

		await structureBuilderPage.publishStructure();

		// Delete picklist

		await picklistBuilderPage.deletePicklist(picklist.id);

		// Delete structure

		await structureBuilderPage.deleteStructure(id);
	}
);

test(
	'Can delete multiple fields',
	{tag: '@LPD-36767'},
	async ({structureBuilderPage}) => {

		// Go to the Structure Builder

		await structureBuilderPage.createStructure();

		await structureBuilderPage.enableForAllSpaces();

		// Change label and name

		const label = `Structure${getRandomInt()}`;

		await structureBuilderPage.changeStructureSettings({
			label,
			name: label,
		});

		// Add four fields

		const types = ['Text', 'Long Text', 'Upload', 'Numeric'] as const;

		for (const type of types) {
			await structureBuilderPage.addField(type);
		}

		// Save and publish the structure

		const {id} = await structureBuilderPage.saveStructure();
		await structureBuilderPage.publishStructure();

		// Select and delete three fields

		await structureBuilderPage.deleteFields([
			{label: 'Text'},
			{label: 'Long Text'},
			{label: 'Upload'},
		]);

		// Delete structure

		await structureBuilderPage.deleteStructure(id);
	}
);

test(
	'Can configure a text field',
	{tag: '@LPD-49168'},
	async ({page, structureBuilderPage}) => {

		// Go to the Structure Builder

		await structureBuilderPage.createStructure();

		await structureBuilderPage.enableForAllSpaces();

		// Change label,name and erc

		const label = `Structure${getRandomInt()}`;
		const erc = getRandomString();

		await structureBuilderPage.changeStructureSettings({
			erc,
			label,
			name: label,
		});

		// Add a text field

		await structureBuilderPage.addField('Text');

		await structureBuilderPage.selectFields([{label: 'Text'}]);

		// Configure the field

		await structureBuilderPage.changeFieldSettings({
			erc,
			label: 'Text Edited',
			localizable: true,
			name: 'textEdited',
		});

		await page.getByLabel('Accept Unique Values Only').click();

		await page.getByLabel('Limit Characters').click();

		const maximumNumberOfCharactersInput = page.getByLabel(
			'Maximum Number of Characters'
		);
		maximumNumberOfCharactersInput.fill('10');

		await maximumNumberOfCharactersInput.blur();

		// Save and publish the structure

		const {id} = await structureBuilderPage.saveStructure();

		await expect(page.locator('.alert-danger')).not.toBeVisible();

		const {objectFields} = await structureBuilderPage.publishStructure();

		// Check the text field is created with the correct settings

		const textObjectField = objectFields.find(
			({name}) => name === 'textEdited'
		);

		expect(textObjectField).toBeDefined();

		expect(textObjectField.label).toStrictEqual({en_US: 'Text Edited'});
		expect(textObjectField.localized).toBe(true);
		expect(textObjectField.name).toBe('textEdited');
		expect(textObjectField.objectFieldSettings[0]).toStrictEqual({
			name: 'uniqueValues',
			value: true,
		});
		expect(textObjectField.objectFieldSettings[1]).toStrictEqual({
			name: 'maxLength',
			value: 10,
		});
		expect(textObjectField.objectFieldSettings[2]).toStrictEqual({
			name: 'showCounter',
			value: true,
		});

		// Delete structure

		await structureBuilderPage.deleteStructure(id);
	}
);

test.describe('Frontend validations', () => {
	test(
		'Validations when saving the structure',
		{tag: '@LPD-36752'},
		async ({page, picklistBuilderPage, structureBuilderPage}) => {

			// Add a picklist

			const picklist = await picklistBuilderPage.createPicklist();

			// Go to the Structure Builder

			await structureBuilderPage.createStructure();

			// Add a Text field

			await structureBuilderPage.addField('Text');

			// Try to save and check we can't publish without spaces

			await expect(async () => {
				await structureBuilderPage.saveButton.click();

				await expect(
					page.getByText('Spaces must be selected')
				).toBeAttached({
					timeout: 500,
				});
			}).toPass();

			await structureBuilderPage.enableForAllSpaces();

			// Set label and empty name

			const label = `Structure${getRandomInt()}`;

			await structureBuilderPage.changeStructureSettings({
				label,
				name: '',
			});

			await expect(
				page.getByText('This field is required')
			).toBeVisible();

			// Add a Single Select field and select it

			await structureBuilderPage.addField('Single Select');

			await structureBuilderPage.selectFields([{label: 'Single Select'}]);

			// Put empty name

			await structureBuilderPage.changeFieldSettings({name: ''});

			// Try to save and check it redirects to structure view

			await clickAndExpectToBeVisible({
				target: page.getByText('Structure Name'),
				trigger: structureBuilderPage.saveButton,
			});

			// Fill name

			await structureBuilderPage.changeStructureSettings({name: label});

			// Now try to save and check it redirects to field view

			await clickAndExpectToBeVisible({
				target: page.locator('.breadcrumb-link', {
					hasText: 'Single Select',
				}),
				trigger: structureBuilderPage.saveButton,
			});

			// Fill name, select picklist and save again

			await structureBuilderPage.changeFieldSettings({name: 'name'});

			await structureBuilderPage.changeFieldSettings({
				picklist: picklist.name,
			});

			// Check picklist setting is saved

			await structureBuilderPage.selectFields([{label: 'Text'}]);

			await structureBuilderPage.selectFields([{label: 'Single Select'}]);

			await expect(page.getByText(picklist.name)).toBeVisible();

			// Save

			const {id} = await structureBuilderPage.saveStructure();

			// Publish structure

			await structureBuilderPage.publishStructure();

			// Delete one field and check warning modal is show when publishing

			await structureBuilderPage.deleteFields([{label: 'Text'}]);

			await clickAndExpectToBeVisible({
				target: page.getByText(
					'You removed one or more fields from the structure'
				),
				trigger: structureBuilderPage.publishButton,
			});

			await clickAndExpectToBeHidden({
				target: page.getByText(
					'You removed one or more fields from the structure'
				),
				trigger: page.locator('.btn-danger'),
			});

			await waitForAlert(page, 'published successfully', {
				timeout: 5000,
			});

			// Check the warning does not appear anymore

			await structureBuilderPage.publishStructure();

			// Delete structure

			await structureBuilderPage.deleteStructure(id);

			// Delete picklist

			await picklistBuilderPage.deletePicklist(picklist.id);
		}
	);

	test(
		'Validations in the picklist picker',
		{tag: '@LPD-51647'},
		async ({page, picklistBuilderPage, structureBuilderPage}) => {

			// Add a picklist

			const picklist = await picklistBuilderPage.createPicklist();

			// Go to the Structure Builder

			await structureBuilderPage.createStructure();

			// Add a Single Select field and check for blur error

			await structureBuilderPage.addField('Single Select');

			await structureBuilderPage.selectFields([{label: 'Single Select'}]);

			const picklistPicker = page.getByLabel('Picklist');

			const errorMessage = page.getByText('This field is required.');

			await expect(errorMessage).not.toBeAttached();

			await picklistPicker.press('Tab');

			await expect(errorMessage).toBeAttached();

			await structureBuilderPage.changeFieldSettings({
				picklist: picklist.name,
			});

			await expect(errorMessage).not.toBeAttached();

			// Add a Multiselect field and check for outside click error

			await structureBuilderPage.addField('Multiselect');

			await structureBuilderPage.selectFields([{label: 'Multiselect'}]);

			await expect(errorMessage).not.toBeAttached();

			await picklistPicker.click();

			await page.locator('body').click();

			await expect(errorMessage).toBeAttached();

			// Delete picklist

			await picklistBuilderPage.deletePicklist(picklist.id);
		}
	);
});

test(
	'Create a picklist from the structure builder by opening other tab',
	{tag: '@LPD-52544'},
	async ({context, page, picklistBuilderPage, structureBuilderPage}) => {

		// Go to the Structure Builder

		await structureBuilderPage.createStructure();

		// Add a Single Select field and select it

		await structureBuilderPage.addField('Single Select');

		await structureBuilderPage.selectFields([{label: 'Single Select'}]);

		// Create new picklist from the button "New Picklist"

		const pagePromise = context.waitForEvent('page');

		await page.getByText('New Picklist').click();

		const newPage = await pagePromise;

		// The picklist builder opens in a new tab

		await expect(
			newPage.getByRole('heading', {name: 'New Picklist'})
		).toBeAttached();

		// Change the picklist name

		await newPage.getByLabel('Picklist Name').fill('Plants');

		// Save the picklist

		await newPage.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(newPage, 'Success:Plants was saved successfully.');

		// Check the new picklist in the structure builder opening the picker by keyboard

		const picklistPicker = page.getByLabel('Picklist');

		await picklistPicker.press('Enter');

		await expect(picklistPicker).toBeFocused();

		await expect(page.getByRole('option', {name: 'Plants'})).toBeAttached();

		// Delete picklist

		const picklist = await picklistBuilderPage.getPicklist('Plants');

		await picklistBuilderPage.deletePicklist(picklist.id);
	}
);

test.describe('Customize experience', () => {
	let structureId = null;

	test.afterEach(async ({structureBuilderPage}) => {
		if (structureId) {
			await structureBuilderPage.deleteStructure(Number(structureId));
		}
	});

	test(
		'Alerts are displayed when trying to customize the experience without publishing the structure',
		{
			tag: '@LPD-50370',
		},
		async ({page, structureBuilderPage}) => {

			// Go to the Structure Builder

			await structureBuilderPage.createStructure();

			await structureBuilderPage.enableForAllSpaces();

			await structureBuilderPage.changeStructureSettings({
				name: `StructureName${getRandomInt()}`,
			});

			// Add two Text fields

			await structureBuilderPage.addField('Text');

			await structureBuilderPage.changeFieldSettings({
				label: 'Field 1',
			});

			await structureBuilderPage.addField('Text');

			await structureBuilderPage.changeFieldSettings({
				label: 'Field 2',
			});

			// Try to customize the experience without publishing the structure

			await page
				.getByRole('button', {name: 'Customize Experience'})
				.click();

			// Check the warning is shown

			await expect(
				page.getByText(
					'To customize the experience you need to publish the structure first.'
				)
			).toBeAttached();

			// Publish the structure

			await page
				.getByRole('dialog', {
					name: 'Publish to Customize Experience',
				})
				.getByRole('button', {name: 'Publish'})
				.click();

			await waitForAlert(
				page,
				'Remember to review the customized experience if needed.',
				{autoClose: false}
			);

			// Check the customized experience

			const url = new URL(page.url());

			structureId = url.searchParams.get('objectDefinitionId');

			await page
				.getByRole('alert')
				.getByRole('button', {name: 'Customize Experience'})
				.click();

			await structureBuilderPage.waitForExperienceCustomizerModal();

			await expect(page.getByLabel('Field 1')).toBeVisible();

			// Go back to the structure builder

			await page
				.locator('.management-bar')
				.getByRole('link', {name: 'Back'})
				.click();

			// Delete the field and try to customize the experience again

			await structureBuilderPage.deleteFields([{label: 'Field 1'}]);

			await page
				.getByRole('button', {name: 'Customize Experience'})
				.click();

			// Check the warning is shown

			await expect(
				page.getByText(
					'To customize the experience you need to publish the structure first. You removed one or more fields from the structure.'
				)
			).toBeAttached();

			await page
				.getByRole('dialog', {
					name: 'Publish to Customize Experience',
				})
				.getByRole('button', {name: 'Publish'})
				.click();

			await page
				.getByRole('alert')
				.getByRole('button', {name: 'Customize Experience'})
				.click();

			await structureBuilderPage.waitForExperienceCustomizerModal();

			// Check the experience is regenerated removing the deleted field

			await expect(page.getByLabel('Field 1')).not.toBeVisible();
			await expect(page.getByLabel('Field 2')).toBeVisible();
		}
	);

	test(
		'Edit experience link is shown every time we publish if it is customized',
		{
			tag: '@LPD-50370',
		},
		async ({page, pageEditorPage, structureBuilderPage}) => {

			// Go to the Structure Builder

			await structureBuilderPage.createStructure();

			await structureBuilderPage.enableForAllSpaces();

			await structureBuilderPage.changeStructureSettings({
				name: `StructureName${getRandomInt()}`,
			});

			// Add two Text fields

			await structureBuilderPage.addField('Text');

			await structureBuilderPage.changeFieldSettings({
				label: 'Field 1',
			});

			await structureBuilderPage.addField('Text');

			await structureBuilderPage.changeFieldSettings({
				label: 'Field 2',
			});

			// Publish the structure and check standard toast is shown

			await expect(async () => {
				await structureBuilderPage.publishButton.click({timeout: 1000});

				await waitForAlert(
					page,
					'Success:Untitled Structure was published successfully.',
					{exact: true}
				);
			}).toPass();

			const url = new URL(page.url());

			structureId = url.searchParams.get('objectDefinitionId');

			// Customize the experience

			await structureBuilderPage.customizeExperience();

			const fragmentId = await pageEditorPage.getFragmentId('Text', 0);

			await pageEditorPage.deleteFragment(fragmentId);

			// Go back to structure builder

			await clickAndExpectToBeVisible({
				target: page.getByText('Structure Fields'),
				trigger: page
					.locator('.management-bar')
					.getByRole('link', {name: 'Back'}),
			});

			// Publish again and check edit experience link is show in toast

			await expect(async () => {
				await structureBuilderPage.publishButton.click({timeout: 1000});

				await waitForAlert(
					page,
					'Remember to review the customized experience if needed'
				);
			}).toPass();
		}
	);

	test(
		'Can autogenerate default experience after customizing it',
		{
			tag: '@LPD-50376',
		},
		async ({page, pageEditorPage, structureBuilderPage}) => {

			// Go to the Structure Builder

			await structureBuilderPage.createStructure();

			await structureBuilderPage.enableForAllSpaces();

			await structureBuilderPage.changeStructureSettings({
				name: `StructureName${getRandomInt()}`,
			});

			// Publish the structure

			await structureBuilderPage.publishStructure();

			const url = new URL(page.url());

			structureId = url.searchParams.get('objectDefinitionId');

			// Customize the experience and add a fragment

			await structureBuilderPage.customizeExperience();

			await pageEditorPage.addFragment('Basic Components', 'Heading');

			// Regenerate Display Page and check the Heading is not present

			await pageEditorPage.regenerateDisplayPage();

			await page
				.getByText('Select a Page Element', {exact: true})
				.waitFor();

			await expect(
				page.locator(
					'.lfr-layout-structure-item-basic-component-heading'
				)
			).not.toBeVisible();
		}
	);
});

test(
	'Add correct initial fields depending on type',
	{
		tag: '@LPD-50371',
	},
	async ({page, structureBuilderPage}) => {

		// Go to the Structure Builder with type content and check initial fields

		await structureBuilderPage.createStructure();

		await structureBuilderPage.changeStructureSettings({
			label: getRandomString(),
		});

		await expect(
			page.locator('.treeview-link', {hasText: 'Title'})
		).toBeVisible();

		await expect(
			page.locator('.treeview-link', {hasText: 'File'})
		).not.toBeVisible();

		// Check with type file

		await structureBuilderPage.createStructure('file');

		await structureBuilderPage.changeStructureSettings({
			label: getRandomString(),
		});

		await expect(
			page.locator('.treeview-link', {hasText: 'Title'})
		).toBeVisible();

		await expect(
			page.locator('.treeview-link', {hasText: 'File'})
		).toBeVisible();
	}
);

test.describe('Referenced structures', () => {
	const createStructure = async (
		page: StructureBuilderPage,
		label: string,
		publish: boolean = true
	) => {
		await page.createStructure();

		await page.enableForAllSpaces();

		await page.changeStructureSettings({
			label,
			name: `StructureName${getRandomInt()}`,
		});

		const {id} = await page.saveStructure();

		if (publish) {
			await page.publishStructure();
		}

		return id;
	};

	test(
		'Can reference several structures and they are persisted',
		{
			tag: '@LPD-49645',
		},
		async ({page, structureBuilderPage}) => {
			const label1 = getRandomString();
			const label2 = getRandomString();
			const label3 = getRandomString();
			const label4 = getRandomString();

			// Create three structures, one of them in draft

			const id1 = await createStructure(structureBuilderPage, label1);
			const id2 = await createStructure(structureBuilderPage, label2);
			const id3 = await createStructure(
				structureBuilderPage,
				label3,
				false
			);

			// Create another one and reference the first two

			const id4 = await createStructure(structureBuilderPage, label4);

			await structureBuilderPage.addReferencedStructures([
				label1,
				label2,
			]);

			// Check the one in draft can't be referenced

			await expect(async () => {
				await clickAndExpectToBeVisible({
					target: page.getByRole('menuitem', {
						exact: true,
						name: 'Referenced Structure',
					}),
					trigger: page.getByLabel('Add Field'),
				});

				await clickAndExpectToBeVisible({
					target: page.locator('.modal-title', {
						hasText: 'Referenced Structure',
					}),
					timeout: 2000,
					trigger: page.getByRole('menuitem', {
						exact: true,
						name: 'Referenced Structure',
					}),
				});

				await page.getByLabel('Structures').click({timeout: 1000});

				await expect(
					page.getByRole('option', {name: label1})
				).toBeVisible();

				await expect(
					page.getByRole('option', {name: label3})
				).not.toBeVisible();

				// Check we can't click Add without structures

				await page
					.locator('.modal-title', {
						hasText: 'Referenced Structure',
					})
					.click({timeout: 500});

				await clickAndExpectToBeVisible({
					target: page
						.locator('.modal-body')
						.getByText('This field is required'),
					trigger: page.locator('.modal-footer').getByText('Add'),
				});

				// Close modal

				await clickAndExpectToBeHidden({
					target: page.locator('.modal-title', {
						hasText: 'Referenced Structure',
					}),
					timeout: 2000,
					trigger: page.locator('.modal-header .close'),
				});
			}).toPass();

			// Publish the structure

			await structureBuilderPage.publishStructure();

			// Check everything is persisted

			await structureBuilderPage.editStructure(id4);

			await expect(
				page.locator('.treeview-link', {hasText: label1})
			).toBeVisible();

			await expect(
				page.locator('.treeview-link', {hasText: label2})
			).toBeVisible();

			// Delete the structures

			await structureBuilderPage.deleteStructure(id1);
			await structureBuilderPage.deleteStructure(id2);
			await structureBuilderPage.deleteStructure(id3);
			await structureBuilderPage.deleteStructure(id4);
		}
	);

	test(
		'Can edit referenced structure in another tab',
		{
			tag: '@LPD-49645',
		},
		async ({context, page, structureBuilderPage}) => {
			const label1 = getRandomString();
			const label2 = getRandomString();

			// Create one structure

			const id1 = await createStructure(structureBuilderPage, label1);

			// Create another one and reference the first one

			const id2 = await createStructure(structureBuilderPage, label2);

			await structureBuilderPage.addReferencedStructures([label1]);

			// Check we can't edit referenced structure

			await structureBuilderPage.selectFields([{label: label1}]);

			await expect(page.getByLabel('Structure Name')).toBeDisabled();
			await expect(page.getByLabel('ERC')).toBeDisabled();
			await expect(structureBuilderPage.spaceSelector).toBeDisabled();

			// Publish the structure

			await structureBuilderPage.publishStructure();

			// Edit referenced structure in another tab

			const pagePromise = context.waitForEvent('page');

			const treeItem = page
				.locator('.treeview-item')
				.getByLabel(label1, {exact: true});

			await structureBuilderPage.selectFields([{label: label1}]);

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('menuitem', {name: 'Edit'}),
				trigger: treeItem.getByLabel('Field Options'),
			});

			const newPage = await pagePromise;

			const newStructureBuilderPage = new StructureBuilderPage(newPage);

			await newPage
				.locator('.management-bar')
				.getByText(label1)
				.waitFor();

			// Add new field and publish

			await newStructureBuilderPage.addField('Date');

			await expect(async () => {
				await newStructureBuilderPage.publishButton.click({
					timeout: 500,
				});

				await expect(
					newPage.locator('.modal-title', {hasText: 'Publish'})
				).toBeVisible({timeout: 3000});

				await newPage
					.getByText('Publish and Propagate')
					.click({timeout: 500});

				await waitForAlert(newPage, 'published', {timeout: 2000});
			}).toPass();

			// Check in first structure that the tree is updated with the new field

			await structureBuilderPage.expandField({label: label1});

			const dateTreeItem = page.locator('.treeview-link', {
				hasText: 'Date',
			});

			await expect(dateTreeItem).toBeVisible();

			// Check we can't delete referenced structure fields

			await structureBuilderPage.selectFields([{label: 'Date'}]);

			await expect(
				dateTreeItem.getByLabel('Field Options')
			).not.toBeVisible();

			// Delete the structures

			await structureBuilderPage.deleteStructure(id1);
			await structureBuilderPage.deleteStructure(id2);
		}
	);
});
