/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedLayoutTest} from '../../../fixtures/isolatedLayoutTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {liferayConfig} from '../../../liferay.config';
import getRandomString from '../../../utils/getRandomString';
import {performLoginViaApi, performLogout} from '../../../utils/performLogin';
import {clientExtensionsPageTest} from '../fixtures/clientExtensionsPageTest';
import {Column} from '../pages/ClientExtensionsPage';
import {WaitAction} from '../pages/EditClientExtensionsPage';
import {editCustomElementPageTest} from './fixtures/editCustomElementPageTest';
import {EditCustomElementPage} from './pages/EditCustomElementPage';

const test = mergeTests(
	clientExtensionsPageTest,
	editCustomElementPageTest,
	featureFlagsTest({
		'LPD-11235': {enabled: true},
	}),
	loginTest()
);

const testSample = mergeTests(
	clientExtensionsPageTest,
	editCustomElementPageTest,
	isolatedLayoutTest({publish: false}),
	pageEditorPagesTest,
	loginTest()
);

testSample.describe('Samples', () => {
	const SAMPLES = [
		{
			erc: 'LXC:liferay-sample-custom-element-1',
			htmlElementName: 'vanilla-counter',
			name: 'Liferay Sample Custom Element 1',
			renderTestLocator: (page: Page) =>
				page.getByText('Portlet internal route'),
		},
		{
			erc: 'LXC:liferay-sample-custom-element-3',
			htmlElementName: 'liferay-sample-custom-element-3',
			name: 'Liferay Sample Custom Element 3',
			renderTestLocator: (page: Page) =>
				page.getByText(
					'liferay-sample-custom-element-3 app is running!'
				),
		},
		{
			erc: 'LXC:liferay-sample-custom-element-4',
			htmlElementName: 'liferay-sample-custom-element-4',
			name: 'Liferay Sample Custom Element 4',
			renderTestLocator: (page: Page) =>
				page.getByRole('heading', {name: 'Hello Test. Welcome!'}),
		},
		{
			erc: 'LXC:liferay-sample-custom-element-5',
			htmlElementName: 'liferay-sample-custom-element-5',
			name: 'Liferay Sample Custom Element 5',
			renderTestLocator: (page: Page) => page.getByText('Success!'),
		},
		{
			erc: 'LXC:liferay-sample-etc-frontend-custom-element',
			htmlElementName: 'liferay-sample-etc-frontend-custom-element',
			name: 'Liferay Sample Etc Frontend Custom Element',
			renderTestLocator: (page: Page) => page.getByText('Greetings in:'),
		},
	];

	for (const sample of SAMPLES) {
		testSample(
			`${sample.name} is registered and can be used`,
			async ({clientExtensionsPage, layout, page, pageEditorPage}) => {
				await test.step(`${sample.name} is visible and configured from Workspace`, async () => {
					await clientExtensionsPage.goto();

					await clientExtensionsPage.search(sample.name);

					await expect(
						clientExtensionsPage.getRowByText(sample.name)
					).toBeVisible();

					await expect(
						clientExtensionsPage.getCellByText(
							Column.CONFIGURED_FROM,
							sample.name
						)
					).toHaveText('Workspace');
				});

				await test.step(`${sample.name} can be viewed and information is read-only`, async () => {
					const viewClientExtensionPage =
						await clientExtensionsPage.viewClientExtension(
							sample.name
						);

					await expect(
						viewClientExtensionPage.nameInput
					).toBeVisible();
					await expect(
						viewClientExtensionPage.nameInput
					).toBeDisabled();
					await expect(viewClientExtensionPage.nameInput).toHaveValue(
						sample.name
					);

					const htmlElementNameInput =
						viewClientExtensionPage.getInputByLabel(
							'HTML Element Name'
						);

					await expect(htmlElementNameInput).toBeVisible();
					await expect(htmlElementNameInput).toBeDisabled();
					await expect(htmlElementNameInput).toHaveValue(
						sample.htmlElementName
					);
				});

				await test.step(`${sample.name} can be added to a page and is rendered`, async () => {
					await pageEditorPage.goto(layout);

					await pageEditorPage.addWidget(
						'Client Extensions',
						sample.name
					);
					await pageEditorPage.publishPage();

					await page.goto(`/web/guest${layout.friendlyURL}`);

					await expect(
						page.locator(sample.htmlElementName)
					).toBeVisible();
					await expect(sample.renderTestLocator(page)).toBeVisible();
				});
			}
		);
	}
});

test(
	'Title field does not allow XSS injections',
	{tag: '@LPD-39400'},
	async ({clientExtensionsPage, editCustomElementPage}) => {
		const NAME = '<svg onload="document.write(\'\')">';

		await editCustomElementPage.goto();

		await editCustomElementPage.nameInput.fill(NAME);
		await editCustomElementPage.htmlElementNameInput.fill('test-element');
		await editCustomElementPage.javaScriptURLInput.fill(
			liferayConfig.environment.baseUrl
		);

		await editCustomElementPage.publish(WaitAction.SUCCESS);

		await clientExtensionsPage.goto();
		const editCustomElementPage2 =
			await clientExtensionsPage.editClientExtension(
				NAME,
				EditCustomElementPage
			);

		await expect(editCustomElementPage2.nameHeader).toHaveText(NAME);

		await test.step('Clean up', async () => {
			await clientExtensionsPage.goto();
			await clientExtensionsPage.deleteClientExtension(NAME);
		});
	}
);

test('Can cancel the creation of a Custom Element', async ({
	editCustomElementPage,
}) => {
	const clientExtensionName = getRandomString();

	await editCustomElementPage.goto();

	await editCustomElementPage.cssURLInput.fill(getRandomString());
	await editCustomElementPage.descriptionContentEditable.fill(
		getRandomString()
	);
	await editCustomElementPage.friendlyURLMappingInput.fill(getRandomString());
	await editCustomElementPage.htmlElementNameInput.fill(
		`html-${getRandomString()}`
	);
	await editCustomElementPage.instanceableCheckbox.check();
	await editCustomElementPage.javaScriptURLInput.fill(getRandomString());
	await editCustomElementPage.nameInput.fill(clientExtensionName);
	await editCustomElementPage.sourceCodeURLInput.fill(getRandomString());
	await editCustomElementPage.useESModulesCheckbox.check();

	const clientExtensionsPage = await editCustomElementPage.cancel();

	await expect(
		clientExtensionsPage.getRowByText(clientExtensionName)
	).not.toBeVisible();
});

test(
	'Publishing with invalid field values results in error',
	{tag: '@LPD-75288'},
	async ({editCustomElementPage, page}) => {
		await test.step('Go to "Add Custom Element" page', async () => {
			await editCustomElementPage.goto();
		});

		await test.step('Name cannot be empty', async () => {
			await editCustomElementPage.fillRequiredFields();

			await editCustomElementPage.nameInput.clear();

			await editCustomElementPage.publish(WaitAction.ERROR);
		});

		await test.step('HTML Element Name cannot be empty', async () => {
			await editCustomElementPage.fillRequiredFields();

			await editCustomElementPage.htmlElementNameInput.clear();

			await editCustomElementPage.publish(WaitAction.NONE);

			await expect(
				page.getByText('The HTML Element Name field is required')
			).toBeVisible();
		});

		await test.step('HTML Element Name cannot contain a space character', async () => {
			await editCustomElementPage.fillRequiredFields();

			await editCustomElementPage.htmlElementNameInput.fill('foo bar');

			await editCustomElementPage.publish(WaitAction.INVALID_CHARACTER);
		});

		await test.step('HTML Element Name must contain a hyphen', async () => {
			await editCustomElementPage.fillRequiredFields();

			await editCustomElementPage.htmlElementNameInput.fill('foo');

			await editCustomElementPage.publish(WaitAction.MISSING_HYPHEN);
		});

		await test.step('HTML Element Name must start with a lowercase letter', async () => {
			await editCustomElementPage.fillRequiredFields();

			await editCustomElementPage.htmlElementNameInput.fill('Foo-bar');

			await editCustomElementPage.publish(
				WaitAction.UPPERCASE_STARTING_LETTER
			);
		});

		await test.step('JavaScript URL field cannot be empty', async () => {
			await editCustomElementPage.fillRequiredFields();

			await editCustomElementPage.javaScriptURLInput.clear();

			await editCustomElementPage.publish(WaitAction.NONE);

			await expect(
				page.getByText('The JavaScript URL field is required')
			).toBeVisible();
		});
	}
);

test('Check that Name field can be translated', async ({
	editCustomElementPage,
}) => {
	await editCustomElementPage.goto();
	await editCustomElementPage.fillRequiredFields();

	const defaultTranslationName = getRandomString();
	const ptTranslationName = getRandomString();

	await editCustomElementPage.fillName('en_US', defaultTranslationName);
	await editCustomElementPage.changeNameLanguage('pt_BR');
	await editCustomElementPage.fillName('pt_BR', ptTranslationName);

	await test.step('Check expectations', async () => {
		await editCustomElementPage.changeNameLanguage('en_US');
		await expect(editCustomElementPage.nameInput).toHaveValue(
			defaultTranslationName
		);

		await editCustomElementPage.changeNameLanguage('pt_BR');
		await expect(editCustomElementPage.nameInput).toHaveValue(
			ptTranslationName
		);
	});
});

test('Check that JavaScript URL field is required', async ({
	editCustomElementPage,
	page,
}) => {
	await editCustomElementPage.goto();
	await editCustomElementPage.fillRequiredFields();

	await test.step('Check expectations', async () => {
		await editCustomElementPage.javaScriptURLInput.clear();
		await editCustomElementPage.publish(WaitAction.NONE);

		await expect(
			page.getByText('The JavaScript URL field is required.')
		).toBeVisible();
	});
});

test('Client extension can be created, edited and deleted', async ({
	clientExtensionsPage,
	editCustomElementPage,
}) => {
	const clientExtensionName = getRandomString();
	const newClientExtensionName = getRandomString();

	await editCustomElementPage.goto();

	await test.step('Create a new client extension', async () => {
		await editCustomElementPage.cssURLInput.fill(getRandomString());
		await editCustomElementPage.descriptionContentEditable.fill(
			getRandomString()
		);
		await editCustomElementPage.friendlyURLMappingInput.fill(
			getRandomString()
		);
		await editCustomElementPage.htmlElementNameInput.fill(
			`html-${getRandomString()}`
		);
		await editCustomElementPage.instanceableCheckbox.check();
		await editCustomElementPage.javaScriptURLInput.fill(getRandomString());
		await editCustomElementPage.nameInput.fill(clientExtensionName);
		await editCustomElementPage.sourceCodeURLInput.fill(getRandomString());
		await editCustomElementPage.useESModulesCheckbox.check();

		await editCustomElementPage.publish(WaitAction.SUCCESS);

		await clientExtensionsPage.goto();

		await expect(
			clientExtensionsPage.getRowByText(clientExtensionName)
		).toBeVisible();
	});

	await test.step('Edit the client extension', async () => {
		await clientExtensionsPage.editClientExtension(
			clientExtensionName,
			EditCustomElementPage
		);

		const newCSSURL = `/${getRandomString()}`;
		const newDescription = getRandomString();
		const newFriendlyURLMapping = getRandomString();
		const newHtmlElementName = 'html-element-' + getRandomString();
		const newJavaScriptURL = `/${getRandomString()}`;
		const newSourceCodeUrl = getRandomString();

		await editCustomElementPage.cssURLInput.fill(newCSSURL);
		await editCustomElementPage.descriptionContentEditable.fill(
			newDescription
		);
		await editCustomElementPage.friendlyURLMappingInput.fill(
			newFriendlyURLMapping
		);
		await editCustomElementPage.htmlElementNameInput.fill(
			newHtmlElementName
		);
		await editCustomElementPage.javaScriptURLInput.fill(newJavaScriptURL);
		await editCustomElementPage.nameInput.fill(newClientExtensionName);
		await editCustomElementPage.sourceCodeURLInput.fill(newSourceCodeUrl);

		await editCustomElementPage.publish(WaitAction.SUCCESS);

		await clientExtensionsPage.goto();

		await clientExtensionsPage.editClientExtension(
			newClientExtensionName,
			EditCustomElementPage
		);

		await expect(editCustomElementPage.cssURLInput).toHaveValue(newCSSURL);
		await expect(
			editCustomElementPage.descriptionContentEditable.getByText(
				newDescription
			)
		).toBeVisible();
		await expect(editCustomElementPage.friendlyURLMappingInput).toHaveValue(
			newFriendlyURLMapping
		);
		await expect(editCustomElementPage.htmlElementNameInput).toHaveValue(
			newHtmlElementName
		);
		await expect(editCustomElementPage.javaScriptURLInput).toHaveValue(
			newJavaScriptURL
		);
		await expect(editCustomElementPage.nameInput).toHaveValue(
			newClientExtensionName
		);
		await expect(editCustomElementPage.sourceCodeURLInput).toHaveValue(
			newSourceCodeUrl
		);
	});

	await test.step('Delete the client extension', async () => {
		await clientExtensionsPage.goto();

		await clientExtensionsPage.deleteClientExtension(
			newClientExtensionName
		);

		await expect(
			clientExtensionsPage.getRowByText(newClientExtensionName)
		).not.toBeVisible();
	});
});

test(
	`Verify that JavaScript URL repeatable field can be assigned multiple values`,
	{tag: '@LPS-158545'},
	async ({clientExtensionsPage, editCustomElementPage}) => {
		await test.step('Create a custom element with two JavaScript URLs', async () => {
			await editCustomElementPage.goto();

			await editCustomElementPage.nameInput.fill('Test Custom Element');
			await editCustomElementPage.htmlElementNameInput.fill(
				'test-custom-element'
			);

			await editCustomElementPage.javaScriptURLInput
				.nth(0)
				.fill('https://www.liferay.com/');
			await editCustomElementPage.addJavaScriptURLButton.nth(0).click();
			await editCustomElementPage.javaScriptURLInput
				.nth(1)
				.fill('https://www.liferay.com/company/our-story');

			await editCustomElementPage.publish(WaitAction.SUCCESS);
		});

		const editCustomElementPage2 =
			await test.step('Edit the custom element again', async () => {
				await clientExtensionsPage.goto();

				return await clientExtensionsPage.editClientExtension(
					'Test Custom Element',
					EditCustomElementPage
				);
			});

		await test.step('Check expectations', async () => {
			await expect(
				editCustomElementPage2.javaScriptURLInput.nth(0)
			).toHaveValue('https://www.liferay.com/');

			await expect(
				editCustomElementPage2.javaScriptURLInput.nth(1)
			).toHaveValue('https://www.liferay.com/company/our-story');
		});

		await test.step('Clean up', async () => {
			await clientExtensionsPage.goto();
			await clientExtensionsPage.deleteClientExtension(
				'Test Custom Element'
			);
		});
	}
);

test(
	`Verify deletion of one of JavaScript URL multiple values`,
	{tag: '@LPS-152023'},
	async ({editCustomElementPage}) => {
		await test.step('Create a custom element with two JavaScript URLs', async () => {
			await editCustomElementPage.goto();

			await editCustomElementPage.nameInput.fill('Test Custom Element');
			await editCustomElementPage.htmlElementNameInput.fill(
				'test-custom-element'
			);

			await editCustomElementPage.javaScriptURLInput
				.nth(0)
				.fill('https://www.liferay.com/');
			await editCustomElementPage.addJavaScriptURLButton.nth(0).click();
			await editCustomElementPage.javaScriptURLInput
				.nth(1)
				.fill('https://www.liferay.com/company/our-story');
		});

		await test.step('Remove the first JavaScript URL', async () => {
			await editCustomElementPage.deleteJavaScriptURLButton
				.nth(0)
				.click();
		});

		await test.step('Check expectations', async () => {
			await expect(editCustomElementPage.javaScriptURLInput).toHaveCount(
				2
			);
			await expect(
				editCustomElementPage.javaScriptURLInput.nth(0)
			).toBeHidden();
			await expect(
				editCustomElementPage.javaScriptURLInput.nth(1)
			).toHaveValue('https://www.liferay.com/company/our-story');

			await expect(
				editCustomElementPage.deleteJavaScriptURLButton
			).toHaveCount(2);
			await expect(
				editCustomElementPage.deleteJavaScriptURLButton.nth(0)
			).toBeHidden();
			await expect(
				editCustomElementPage.deleteJavaScriptURLButton.nth(1)
			).toBeHidden();
		});
	}
);

test(
	`Verify that a new CSS URL row can be created`,
	{tag: '@LPS-152023'},
	async ({editCustomElementPage}) => {
		await editCustomElementPage.goto();

		await editCustomElementPage.addCSSURLButton.nth(0).click();

		await expect(editCustomElementPage.cssURLInput).toHaveCount(2);

		await expect(editCustomElementPage.addCSSURLButton).toHaveCount(2);

		await expect(editCustomElementPage.deleteCSSURLButton).toHaveCount(2);
	}
);

test(
	`Verify that a new JavaScript URL row can be created`,
	{tag: '@LPS-152023'},
	async ({editCustomElementPage}) => {
		await editCustomElementPage.goto();

		await editCustomElementPage.addJavaScriptURLButton.nth(0).click();

		await expect(editCustomElementPage.javaScriptURLInput).toHaveCount(2);

		await expect(editCustomElementPage.addJavaScriptURLButton).toHaveCount(
			2
		);

		await expect(
			editCustomElementPage.deleteJavaScriptURLButton
		).toHaveCount(2);
	}
);

testSample(
	'Custom Element can be instanceable',
	{tag: '@LPS-139377'},
	async ({
		clientExtensionsPage,
		editCustomElementPage,
		layout,
		page,
		pageEditorPage,
	}) => {
		const clientExtensionName = getRandomString();
		const htmlElementName = `html-${getRandomString()}`;

		await test.step('Create an instanceable Custom Element', async () => {
			await editCustomElementPage.goto();

			await editCustomElementPage.nameInput.fill(clientExtensionName);
			await editCustomElementPage.htmlElementNameInput.fill(
				htmlElementName
			);
			await editCustomElementPage.javaScriptURLInput.fill(
				'https://www.example.com/test.js'
			);
			await editCustomElementPage.instanceableCheckbox.check();

			await editCustomElementPage.publish(WaitAction.SUCCESS);
		});

		await test.step('Add the widget to two grid columns', async () => {
			await pageEditorPage.goto(layout);

			await pageEditorPage.addWidget(
				'Client Extensions',
				clientExtensionName
			);
			await pageEditorPage.addWidget(
				'Client Extensions',
				clientExtensionName
			);
			await pageEditorPage.publishPage();
		});

		await test.step('Verify both instances render', async () => {
			await page.goto(`/web/guest${layout.friendlyURL}`);

			const elements = page.locator(htmlElementName);

			await expect(elements).toHaveCount(2);
		});

		await test.step('Clean up', async () => {
			await clientExtensionsPage.goto();

			await clientExtensionsPage.deleteClientExtension(
				clientExtensionName
			);
		});
	}
);

testSample(
	'Custom Element can inject HTML properties',
	{tag: '@LPS-139377'},
	async ({
		clientExtensionsPage,
		editCustomElementPage,
		layout,
		page,
		pageEditorPage,
	}) => {
		const clientExtensionName = getRandomString();
		const htmlElementName = `html-${getRandomString()}`;

		const propertyName1 = `prop-${getRandomString()}`;
		const propertyName2 = `prop-${getRandomString()}`;

		const propertyValue1 = getRandomString();
		const propertyValue2 = getRandomString();

		await test.step('Create a Custom Element with properties', async () => {
			await editCustomElementPage.goto();

			await editCustomElementPage.nameInput.fill(clientExtensionName);
			await editCustomElementPage.htmlElementNameInput.fill(
				htmlElementName
			);
			await editCustomElementPage.javaScriptURLInput.fill(
				'https://www.example.com/test.js'
			);

			await editCustomElementPage.propertiesTextArea.fill(
				`${propertyName1}=${propertyValue1}\n${propertyName2}=${propertyValue2}`
			);

			await editCustomElementPage.publish(WaitAction.SUCCESS);
		});

		await test.step('Add widget to page and verify HTML property', async () => {
			await pageEditorPage.goto(layout);

			await pageEditorPage.addWidget(
				'Client Extensions',
				clientExtensionName
			);
			await pageEditorPage.publishPage();

			await page.goto(`/web/guest${layout.friendlyURL}`);

			const element = page.locator(htmlElementName);

			await expect(element).toHaveAttribute(
				propertyName1,
				propertyValue1
			);
			await expect(element).toHaveAttribute(
				propertyName2,
				propertyValue2
			);
		});

		await test.step('Clean up', async () => {
			await clientExtensionsPage.goto();

			await clientExtensionsPage.deleteClientExtension(
				clientExtensionName
			);
		});
	}
);

testSample(
	'Custom Element renders correctly when placed on a page (non ES module)',
	{tag: '@LPS-159013'},
	async ({
		clientExtensionsPage,
		editCustomElementPage,
		layout,
		page,
		pageEditorPage,
	}) => {
		const clientExtensionName = getRandomString();
		const htmlElementName = `html-${getRandomString()}`;
		const jsResourceName = `res-${getRandomString()}.js`;

		await test.step('Create a non-ES module Custom Element', async () => {
			await editCustomElementPage.goto();

			await editCustomElementPage.nameInput.fill(clientExtensionName);
			await editCustomElementPage.htmlElementNameInput.fill(
				htmlElementName
			);
			await editCustomElementPage.javaScriptURLInput.fill(
				`https://www.example.com/${jsResourceName}`
			);

			await editCustomElementPage.publish(WaitAction.SUCCESS);
		});

		await test.step('Add widget to page and verify script type', async () => {
			await pageEditorPage.goto(layout);

			await pageEditorPage.addWidget(
				'Client Extensions',
				clientExtensionName
			);
			await pageEditorPage.publishPage();

			await page.goto(`/web/guest${layout.friendlyURL}`);

			await expect(
				page.locator(`script[src*="${jsResourceName}"]:not(type)`)
			).toBeAttached();
		});

		await test.step('Clean up', async () => {
			await clientExtensionsPage.goto();

			await clientExtensionsPage.deleteClientExtension(
				clientExtensionName
			);
		});
	}
);

testSample(
	'Custom Element renders correctly when placed on a page (ES module type)',
	{tag: '@LPS-139377'},
	async ({
		clientExtensionsPage,
		editCustomElementPage,
		layout,
		page,
		pageEditorPage,
	}) => {
		const clientExtensionName = getRandomString();
		const htmlElementName = `html-${getRandomString()}`;
		const jsResourceName = `res-${getRandomString()}.js`;

		await test.step('Create an ES module Custom Element', async () => {
			await editCustomElementPage.goto();

			await editCustomElementPage.nameInput.fill(clientExtensionName);
			await editCustomElementPage.htmlElementNameInput.fill(
				htmlElementName
			);
			await editCustomElementPage.javaScriptURLInput.fill(
				`https://www.example.com/${jsResourceName}`
			);
			await editCustomElementPage.useESModulesCheckbox.check();

			await editCustomElementPage.publish(WaitAction.SUCCESS);
		});

		await test.step('Add to page and verify type="module" script', async () => {
			await pageEditorPage.goto(layout);

			await pageEditorPage.addWidget(
				'Client Extensions',
				clientExtensionName
			);
			await pageEditorPage.publishPage();

			await page.goto(`/web/guest${layout.friendlyURL}`);

			await expect(
				page.locator(`script[type="module"][src*="${jsResourceName}"]`)
			).toBeAttached();
		});

		await test.step('Clean up', async () => {
			await clientExtensionsPage.goto();

			await clientExtensionsPage.deleteClientExtension(
				clientExtensionName
			);
		});
	}
);

test(
	'UI label is present for non-OSGi client extensions',
	{tag: '@LPS-154725'},
	async ({clientExtensionsPage, editCustomElementPage}) => {
		const clientExtensionName = getRandomString();

		await test.step('Create a Custom Element', async () => {
			await editCustomElementPage.goto();

			await editCustomElementPage.nameInput.fill(clientExtensionName);
			await editCustomElementPage.htmlElementNameInput.fill(
				`html-${getRandomString()}`
			);
			await editCustomElementPage.javaScriptURLInput.fill(
				'https://www.example.com/test.js'
			);

			await editCustomElementPage.publish(WaitAction.SUCCESS);
		});

		await test.step('Verify UI label is present', async () => {
			await clientExtensionsPage.goto();

			await clientExtensionsPage.search(clientExtensionName);

			const row = clientExtensionsPage.getRowByText(clientExtensionName);

			await expect(row).toBeVisible();

			await expect(row.locator('td').nth(Column.TYPE)).toContainText(
				'Custom Element'
			);
		});

		await test.step('Clean up', async () => {
			await clientExtensionsPage.deleteClientExtension(
				clientExtensionName
			);
		});
	}
);

testSample(
	'Custom Element resources are not loaded for a guest without VIEW permission',
	{tag: '@LPD-95613'},
	async ({
		clientExtensionsPage,
		editCustomElementPage,
		layout,
		page,
		pageEditorPage,
	}) => {
		const clientExtensionName = getRandomString();
		const cssResourceName = `res-${getRandomString()}.css`;
		const htmlElementName = `html-${getRandomString()}`;
		const jsResourceName = `res-${getRandomString()}.js`;

		await test.step('Create a Custom Element with CSS and JavaScript resources', async () => {
			await editCustomElementPage.goto();

			await editCustomElementPage.cssURLInput.fill(
				`https://www.example.com/${cssResourceName}`
			);
			await editCustomElementPage.htmlElementNameInput.fill(
				htmlElementName
			);
			await editCustomElementPage.javaScriptURLInput.fill(
				`https://www.example.com/${jsResourceName}`
			);
			await editCustomElementPage.nameInput.fill(clientExtensionName);

			await editCustomElementPage.publish(WaitAction.SUCCESS);
		});

		await test.step('Add the widget and remove VIEW permission for the Guest role', async () => {
			await pageEditorPage.goto(layout);

			await pageEditorPage.addWidget(
				'Client Extensions',
				clientExtensionName
			);

			const widgetId =
				await pageEditorPage.getFragmentId(clientExtensionName);

			await pageEditorPage.changeWidgetPermission(
				widgetId,
				'#guest_ACTION_VIEW',
				false
			);

			await pageEditorPage.publishPage();
		});

		await test.step('Resources load for a user with VIEW permission', async () => {
			await page.goto(`/web/guest${layout.friendlyURL}`);

			await expect(page.locator(htmlElementName)).toBeAttached();
			await expect(
				page.locator(`script[src*="${jsResourceName}"]`)
			).toBeAttached();
			await expect(
				page.locator(`link[href*="${cssResourceName}"]`)
			).toBeAttached();
		});

		await test.step('Resources do not load for a guest without VIEW permission', async () => {
			await performLogout(page);

			await expect(async () => {
				await page.goto(`/web/guest${layout.friendlyURL}`);

				await expect(page.locator(htmlElementName)).toHaveCount(0);
				await expect(
					page.locator(`script[src*="${jsResourceName}"]`)
				).toHaveCount(0);
				await expect(
					page.locator(`link[href*="${cssResourceName}"]`)
				).toHaveCount(0);
			}).toPass();
		});

		await test.step('Clean up', async () => {
			await performLoginViaApi({page, screenName: 'test'});

			await clientExtensionsPage.goto();

			await clientExtensionsPage.deleteClientExtension(
				clientExtensionName
			);
		});
	}
);
