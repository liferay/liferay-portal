/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrameLocator, Locator, Page, expect} from '@playwright/test';

import {liferayConfig} from '../../../liferay.config';

export class CommerceLayoutsPage {
	readonly accountSelectorButton: (name: string) => Locator;
	readonly addOrderButton: Locator;
	readonly addPageButton: Locator;
	readonly addPageModalSubmitButton: Locator;
	readonly addPageNameInput: Locator;
	readonly addWidgetButton: Locator;
	readonly addWidgetLabel: (widgetName: string) => Locator;
	readonly availableThemesFrame: FrameLocator;
	readonly backLink: Locator;
	readonly cancelButton: Locator;
	readonly changeCurrentThemeButton: Locator;
	readonly closeProductMenuButton: Locator;
	readonly configureMenuItem: Locator;
	readonly createNewOrderButton: Locator;
	readonly createPageMenuItem: Locator;
	readonly defaultDisplayPageTemplateIcon: Locator;
	readonly defineCustomThemeCheckbox: Locator;
	readonly deleteEntriesButton: Locator;
	readonly deleteLayoutModal: Locator;
	readonly deleteMenuItemModal: Locator;
	readonly deletePageButton: Locator;
	readonly designMenuItem: Locator;
	readonly designLink: Locator;
	readonly displayPageTemplateCheckBox: (displayPageName: string) => Locator;
	readonly displayPageTemplateLink: (name: string) => Locator;
	readonly displayPageTemplatesLink: Locator;
	readonly editMenuItem: Locator;
	readonly firstFragment: Locator;
	readonly fragmentsAndWidgetsTab: Locator;
	readonly fragmentMenuItem: (itemName: string) => Locator;
	readonly iconLock: Locator;
	readonly infoBoxButton: (label: string) => Locator;
	readonly infoBoxCancelButton: Locator;
	readonly infoBoxDeletePurchaseOrderDocumentButton: Locator;
	readonly infoBoxEditPurchaseOrderDocumentButton: Locator;
	readonly infoBoxFieldSelect: Locator;
	readonly infoBoxLabelInput: Locator;
	readonly infoBoxShippingMethodAlert: Locator;
	readonly infoBoxShippingMethodSelect: Locator;
	readonly infoBoxReadOnlyToggle: Locator;
	readonly infoBoxValue: (name: string) => Locator;
	readonly inputTextArea: Locator;
	readonly inputTextbox: (name: string) => Locator;
	readonly labelField: Locator;
	readonly markAsDefaultMenuItem: Locator;
	readonly moreActionsButton: Locator;
	readonly openProductMenuButton: Locator;
	readonly orderActionsButton: (orderActionName: string) => Locator;
	readonly orderActionDropDownButton: Locator;
	readonly orderItemCardButton: Locator;
	readonly page: Page;
	readonly pageEditorCollectionItem: Locator;
	readonly pageEditorElement: (selector: string) => Locator;
	readonly pageEditorText: (text: RegExp | string) => Locator;
	readonly pagesMenuItem: Locator;
	readonly pageTemplatesMenuItem: Locator;
	readonly paymentTermsSelect: Locator;
	readonly pendingOrdersLink: Locator;
	readonly previewItemSelectorButton: Locator;
	readonly productMenuItem: Locator;
	readonly publishButton: Locator;
	readonly saveButton: Locator;
	readonly searchFormInput: Locator;
	readonly selectCollectionButton: Locator;
	readonly selectOtherItemDropdownItem: Locator;
	readonly selectRelatedItemsCollectionProviders: Locator;
	readonly showLabelInput: Locator;
	readonly siteBuilderMenuItem: Locator;
	readonly siteHomePageLink: Locator;
	readonly stepTrackerItem: (name: string) => Locator;
	readonly submitButton: Locator;
	readonly widgetPageTemplateButton: Locator;

	readonly orderTypeModal: Locator;
	readonly orderTypeModalHeading: Locator;
	readonly orderTypeModalInput: Locator;
	readonly orderTypeModalButton: Locator;

	constructor(page: Page) {
		this.accountSelectorButton = (name) => {
			return page.getByRole('button', {
				name,
			});
		};
		this.addOrderButton = page.getByRole('button', {
			exact: true,
			name: 'Add Order',
		});
		this.addPageButton = page
			.getByTestId('creationMenuNewButton')
			.locator('visible=true');
		this.addPageModalSubmitButton = page
			.frameLocator('#addLayoutDialog_iframe_')
			.getByTestId('addLayoutFooter')
			.getByRole('button', {exact: true, name: 'Add'});
		this.addPageNameInput = page
			.frameLocator('#addLayoutDialog_iframe_')
			.getByTestId('addPageNameInput');
		this.addWidgetButton = page.getByTestId('add');
		this.addWidgetLabel = (widgetName) => {
			return page
				.getByTestId('addPanelTabItem')
				.filter({has: page.locator(`text="${widgetName}"`)})
				.getByRole('button', {exact: true, name: 'Add Content'});
		};
		this.availableThemesFrame = page.frameLocator(
			'iframe[title="Available Themes"]'
		);
		this.backLink = page.getByRole('link', {exact: true, name: 'Back'});
		this.cancelButton = page.getByRole('button', {
			exact: true,
			name: 'Cancel',
		});
		this.changeCurrentThemeButton = page.getByRole('button', {
			exact: true,
			name: 'Change Current Theme',
		});
		this.closeProductMenuButton = page.getByRole('tab', {
			exact: true,
			name: 'Close Product Menu',
		});
		this.configureMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Configure',
		});
		this.createNewOrderButton = page.getByRole('button', {
			exact: true,
			name: 'Create New Order',
		});
		this.createPageMenuItem = page
			.getByTestId('dropdownMenu')
			.getByRole('menuitem', {
				exact: true,
				name: 'Page',
			});
		this.defaultDisplayPageTemplateIcon = page
			.getByTestId('row')
			.locator('use')
			.nth(1);
		this.defineCustomThemeCheckbox = page.getByLabel(
			'Define a custom theme for this page.'
		);
		this.deleteEntriesButton = page
			.getByLabel('Delete Entries- Loading')
			.getByRole('button', {name: 'Delete'});
		this.deleteLayoutModal = page.locator('#deleteLayoutModalDeleteButton');
		this.deleteMenuItemModal = page.getByRole('menuitem', {
			exact: true,
			name: 'Delete',
		});
		this.deletePageButton = page
			.getByTestId('actionDropdownItem')
			.getByRole('button', {
				exact: true,
				name: 'Delete',
			});
		this.designMenuItem = page
			.getByTestId('appGroup')
			.filter({hasText: 'Design'});
		this.designLink = page.getByRole('link', {exact: true, name: 'Design'});
		this.displayPageTemplateCheckBox = (displayPageName: string) =>
			page.getByLabel(displayPageName);
		this.displayPageTemplateLink = (name: string) =>
			page.getByRole('link', {exact: true, name});
		this.displayPageTemplatesLink = page.getByRole('link', {
			exact: true,
			name: 'Display Page Templates',
		});
		this.editMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Edit',
		});
		this.firstFragment = page.locator('#page-editor div').nth(2);
		this.fragmentsAndWidgetsTab = page.getByRole('tab', {
			exact: true,
			name: 'Components',
		});
		this.fragmentMenuItem = (itemName: string) =>
			page.getByRole('menuitem', {
				name: itemName,
			});
		this.infoBoxButton = (label: string) =>
			page.getByTestId(label + '-infoBoxButton');
		this.infoBoxCancelButton = page.getByRole('button', {
			exact: true,
			name: 'Cancel',
		});
		this.iconLock = page.locator('.lexicon-icon-lock');
		this.infoBoxDeletePurchaseOrderDocumentButton = page.getByTestId(
			'purchaseOrderDocument-infoBoxDeleteButton'
		);
		this.infoBoxEditPurchaseOrderDocumentButton = page.getByTestId(
			'purchaseOrderDocument-infoBoxEditButton'
		);
		this.infoBoxFieldSelect = page.getByLabel('Field', {exact: true});
		this.infoBoxLabelInput = page.getByLabel('Label', {exact: true});
		this.infoBoxReadOnlyToggle = page.getByLabel('Read Only');
		this.infoBoxShippingMethodAlert = page.getByText('are no available');
		this.infoBoxShippingMethodSelect = page.getByLabel('Choose Carrier');
		this.infoBoxValue = (name: string) => page.getByText(name);
		this.inputTextArea = page.getByRole('textbox');
		this.inputTextbox = (name: string) =>
			page.getByRole('textbox', {exact: true, name});
		this.markAsDefaultMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Mark as Default',
		});
		this.labelField = page.getByLabel('Field', {exact: true});
		this.moreActionsButton = page.getByLabel('More actions');
		this.openProductMenuButton = page.getByRole('tab', {
			exact: true,
			name: 'Open Product Menu',
		});
		this.orderActionsButton = (orderActionName: string) =>
			page.getByRole('button', {exact: true, name: orderActionName});
		this.orderActionDropDownButton = page
			.locator(
				'.lfr-layout-structure-item-com-liferay-commerce-order-content-web-internal-fragment-renderer-orderactionsfragmentrenderer'
			)
			.locator('.dropdown-toggle');
		this.orderItemCardButton = page
			.frameLocator('iframe[title="Select"]')
			.getByRole('button', {name: 'Select Order Items'});
		this.orderTypeModal = page.locator('.modal-content');
		this.orderTypeModalHeading = this.orderTypeModal.getByRole('heading', {
			name: 'Select Order Type',
		});
		this.orderTypeModalInput = this.orderTypeModal.getByLabel(
			'Order Type',
			{exact: true}
		);
		this.orderTypeModalButton = this.orderTypeModal.getByRole('button', {
			name: 'Add Order',
		});
		this.page = page;
		this.pageEditorCollectionItem = page
			.locator('.page-editor__collection-item')
			.first();
		this.pageEditorElement = (selector: string) =>
			page.locator('#page-editor').locator(selector);
		this.pageEditorText = (text: RegExp | string) =>
			page.locator('#page-editor').getByText(text);
		this.pagesMenuItem = page
			.getByTestId('app')
			.filter({hasNotText: 'Locked', hasText: 'Pages'});
		this.pageTemplatesMenuItem = page
			.getByTestId('app')
			.filter({hasText: 'Page Templates'});
		this.paymentTermsSelect = page.locator(
			'#paymentTermId_infoBoxModalTermInput'
		);
		this.pendingOrdersLink = page.getByRole('link', {
			exact: true,
			name: 'Pending Orders',
		});
		this.previewItemSelectorButton = page.getByTestId(
			'previewItemSelectorButton'
		);
		this.productMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Product',
		});
		this.publishButton = page.getByRole('button', {
			exact: true,
			name: 'Publish',
		});
		this.saveButton = page.getByRole('button', {exact: true, name: 'Save'});
		this.searchFormInput = page.getByRole('textbox', {
			name: 'Search Form',
		});
		this.selectCollectionButton = page.getByRole('button', {
			exact: true,
			name: 'Select Collection',
		});
		this.selectOtherItemDropdownItem = page.getByTestId(
			'selectOtherItemDropdownItem'
		);

		this.selectRelatedItemsCollectionProviders = page
			.frameLocator('iframe[title="Select"]')
			.getByRole('link', {name: 'Related Items Collection Providers'});
		this.showLabelInput = page.getByLabel('Show Label', {exact: true});
		this.siteBuilderMenuItem = page
			.getByTestId('appGroup')
			.filter({hasText: 'Site Builder'});
		this.siteHomePageLink = page.getByRole('link', {
			exact: true,
			name: 'Home',
		});
		this.stepTrackerItem = (name: string) =>
			page.locator('li').filter({hasText: name});
		this.submitButton = page.getByRole('button', {
			exact: true,
			name: 'Submit',
		});
		this.widgetPageTemplateButton = page
			.getByTestId('cardPageItemDirectory')
			.getByRole('button', {
				exact: true,
				name: 'Widget Page',
			});
	}

	async addFragment(itemName: string, menuName: string = '') {
		const source = this.fragmentMenuItem(itemName);

		if ((await source.isHidden()) && menuName) {
			await this.page
				.getByRole('menuitem', {
					exact: true,
					name: menuName,
				})
				.click();
		}

		await source.focus();
		await source.press('Enter');
		await source.press('Enter');
	}

	async addFragmentToCollectionDisplay(itemName: string) {
		const source = this.fragmentMenuItem(itemName);

		const target = this.pageEditorCollectionItem;

		await source.focus();
		await source.dragTo(target);
	}

	async addProductFragment(itemName: string) {
		await this.productMenuItem.click();

		const source = this.fragmentMenuItem(itemName);

		await source.focus();
		await source.press('Enter');
		await source.press('Enter');

		await this.productMenuItem.click();
	}

	async addWidget(itemName: string, menuName: string = '') {
		await this.page
			.getByRole('tab', {
				exact: true,
				name: 'Widgets',
			})
			.click();

		const source = await this.page.getByRole('menuitem', {
			name: itemName,
		});

		if ((await source.isHidden()) && menuName) {
			await this.page
				.getByRole('menuitem', {
					exact: true,
					name: menuName,
				})
				.click();
		}

		await source.focus();
		await source.press('Enter');
		await source.press('Enter');
	}

	async addWidgetToPage(widgetName: string) {
		await this.addWidgetButton.click();
		await this.searchFormInput.fill(widgetName);
		await this.addWidgetLabel(widgetName).click();
	}

	async createDisplayPageTemplate(
		displayPageTemplateName: string,
		contentTypeLabel: string = 'Product',
		siteName: string = 'guest'
	) {
		await this.page
			.getByRole('link', {exact: true, name: 'Display Page Templates'})
			.click();
		await this.page.getByRole('button', {exact: true, name: 'New'}).click();
		await this.page
			.getByRole('menuitem', {exact: true, name: 'Display Page Template'})
			.click();
		await this.page
			.getByRole('button', {exact: true, name: 'Blank'})
			.click();
		await this.page
			.getByLabel('Name', {exact: true})
			.fill(displayPageTemplateName);
		await this.page
			.getByLabel('Content Type')
			.selectOption({label: contentTypeLabel});
		await Promise.all([
			this.page.getByRole('button', {exact: true, name: 'Save'}).click(),
			this.page.waitForResponse(
				(resp) => resp.status() === 200 && resp.url().includes(siteName)
			),
		]);
	}

	async configureDisplayPageTemplateTheme(themeName: string) {
		await this.moreActionsButton.click();
		await this.configureMenuItem.click();
		await this.designLink.click();
		await this.defineCustomThemeCheckbox.check();
		await this.changeCurrentThemeButton.click();
		await this.availableThemesFrame
			.getByRole('button', {exact: true, name: themeName})
			.click();
		await this.saveButton.click();
	}

	async expectOrderActionButtons({
		approveCount = 0,
		checkoutCount = 0,
		rejectCount = 0,
		reorderCount = 0,
		requestQuoteCount = 0,
		submitCount = 0,
	}) {
		await expect(this.orderActionsButton('Approve')).toHaveCount(
			approveCount
		);
		await expect(this.orderActionsButton('Checkout')).toHaveCount(
			checkoutCount
		);
		await expect(this.orderActionsButton('Reject')).toHaveCount(
			rejectCount
		);
		await expect(this.orderActionsButton('Reorder')).toHaveCount(
			reorderCount
		);
		await expect(this.orderActionsButton('Request Quote')).toHaveCount(
			requestQuoteCount
		);
		await expect(this.orderActionsButton('Submit')).toHaveCount(
			submitCount
		);
	}

	async goto() {
		await this.page.goto(liferayConfig.environment.baseUrl);
	}

	async goToDisplayPageTemplates(navigation: boolean = false) {
		if (navigation) {
			await this.goto();
		}

		if (
			(await this.closeProductMenuButton.isVisible()) &&
			(await this.pageTemplatesMenuItem.isVisible())
		) {
			return;
		}
		else if (
			(await this.closeProductMenuButton.isVisible()) &&
			(await this.pageTemplatesMenuItem.isHidden())
		) {
			await this.designMenuItem.click();
		}
		else if (await this.openProductMenuButton.isVisible()) {
			await this.openProductMenuButton.click();

			const promise = new Promise(() => {
				if (this.pageTemplatesMenuItem.isHidden()) {
					this.designMenuItem.click();
				}
			});

			Promise.race([
				promise,
				expect(this.pageTemplatesMenuItem).toBeVisible(),
			]).catch((error) => console.error(error));
		}

		await Promise.all([
			this.pageTemplatesMenuItem.click(),
			this.page.waitForResponse(
				(resp) =>
					resp.status() === 200 &&
					resp
						.url()
						.includes(
							'p_p_id=com_liferay_layout_page_template_admin_web_portlet_LayoutPageTemplatesPortlet'
						)
			),
		]);

		await Promise.all([
			this.displayPageTemplatesLink.click(),
			this.page.waitForResponse(
				(resp) =>
					resp.status() === 200 &&
					resp
						.url()
						.includes(
							'p_p_id=com_liferay_layout_page_template_admin_web_portlet_LayoutPageTemplatesPortlet'
						)
			),
		]);
	}

	async goToPages(navigation: boolean = true, siteName?: string) {
		if (navigation) {
			await this.goto();
		}

		if (siteName) {
			await this.page.goto(
				`/group/${siteName}/~/control_panel/manage?p_p_id=com_liferay_layout_admin_web_portlet_GroupPagesPortlet`
			);
		}
		else {
			if (
				(await this.closeProductMenuButton.isVisible()) &&
				(await this.pagesMenuItem.isHidden())
			) {
				await this.siteBuilderMenuItem.click();
			}
			else if (await this.openProductMenuButton.isVisible()) {
				await this.openProductMenuButton.click();

				if (await this.pagesMenuItem.isHidden()) {
					await this.siteBuilderMenuItem.click();
				}
			}

			await Promise.all([
				this.pagesMenuItem.click(),
				this.page.waitForResponse(
					(resp) =>
						resp.status() === 200 &&
						resp
							.url()
							.includes(
								'p_p_id=com_liferay_layout_admin_web_portlet_GroupPagesPortlet'
							)
				),
			]);
		}
	}

	async selectDisplayPageTemplatePreviewItem(
		itemName: string,
		visible: boolean = true
	) {
		await this.previewItemSelectorButton.click();
		await this.selectOtherItemDropdownItem.click();

		const itemButton = await this.page
			.frameLocator('iframe[title="Select"]')
			.getByRole('button', {name: itemName});

		if (!visible) {
			await expect(itemButton).toHaveCount(0);

			return;
		}

		await expect(itemButton).toBeVisible();

		await itemButton.click();
	}
}
