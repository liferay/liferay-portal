/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom/extend-expect';
import {State} from '@liferay/frontend-js-state-web';
import {act, fireEvent, render, screen} from '@testing-library/react';

import {LAYOUT_DATA_ITEM_TYPES} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/layoutDataItemTypes';
import {VIEWPORT_SIZES} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/viewportSizes';
import {config} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/index';
import {StoreAPIContextProvider} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import updateFormItemConfig from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/updateFormItemConfig';
import {pageContentsAtom} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/usePageContents';
import {openInfoFieldSelector} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/common/openInfoFieldSelector';
import {FormGeneralPanel} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/browser/components/page_structure/components/item_configuration_panels/FormGeneralPanel';

jest.mock(
	'../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/services/serviceFetch',
	() => jest.fn(() => Promise.resolve({}))
);

jest.mock(
	'../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/updateFormItemConfig',
	() => jest.fn(() => () => Promise.resolve())
);

jest.mock(
	'../../../../../../../../../src/main/resources/META-INF/resources/page_editor/common/openInfoFieldSelector',
	() => ({
		openInfoFieldSelector: jest.fn(() => {}),
	})
);

jest.mock(
	'../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/services/FormService',
	() => ({
		getFormFields: jest.fn(() => Promise.resolve([])),
	})
);

jest.mock(
	'../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/index',
	() => ({
		config: {
			availableLanguages: {
				en_US: {
					default: false,
					displayName: 'English (United States)',
					languageIcon: 'en-us',
					languageId: 'en_US',
					w3cLanguageId: 'en-US',
				},
			},
			commonStyles: [],
			formTypes: [
				{
					isRestricted: false,
					label: 'None',
					subtypes: [],
					value: '0',
				},
				{
					className: '11111-className',
					isRestricted: false,
					label: 'Form Type 1',
					subtypes: [],
					value: '11111',
				},
				{
					isRestricted: true,
					label: 'Form Type 2',
					subtypes: [],
					value: '22222',
				},
			],
		},
	})
);

const MAPPED_FORM_ITEM = {
	children: [],
	config: {
		classNameId: '11111',
		classTypeId: '0',
	},
	itemId: 'form-item',
	parentId: '',
	type: LAYOUT_DATA_ITEM_TYPES.form,
};

const UNMAPPED_FORM_ITEM = {
	children: [],
	config: {classNameId: '0'},
	itemId: 'form-item',
	parentId: '',
	type: LAYOUT_DATA_ITEM_TYPES.form,
};

const renderComponent = ({
	item = MAPPED_FORM_ITEM,
	successMessage,
	fragmentEntryLinks,
} = {}) => {
	const mockDispatch = jest.fn((a) => {
		if (typeof a === 'function') {
			return a(mockDispatch, () => state);
		}
	});

	const layoutDataItem = {
		...item,
		config: {...item.config, successMessage},
	};

	const state = {
		fragmentEntryLinks: {...fragmentEntryLinks},
		languageId: 'en_US',
		layoutData: {
			items: {
				[item.itemId]: layoutDataItem,
			},
		},
		mappingFields: {
			'11111-0': [
				{
					fields: [
						{
							key: '1',
							label: 'My Display Page',
							name: 'myDisplayPageURL',
							type: 'display-page',
						},
					],
					label: 'Display Page',
				},
			],
		},
		permissions: {UPDATE: true},
		selectedViewportSize: VIEWPORT_SIZES.desktop,
	};

	return render(
		<StoreAPIContextProvider dispatch={mockDispatch} getState={() => state}>
			<FormGeneralPanel item={layoutDataItem} />
		</StoreAPIContextProvider>
	);
};

describe('FormGeneralPanel', () => {
	beforeAll(() => {
		State.writeAtom(pageContentsAtom, {
			data: [],
			status: 'saved',
		});
	});

	beforeEach(() => {
		updateFormItemConfig.mockClear();
	});

	it('renders success action options if the form is mapped', async () => {
		await act(async () => {
			renderComponent();
		});

		expect(screen.getByLabelText('success-action')).toBeInTheDocument();
	});

	it('does not renders success action options if the form is not mapped', async () => {
		await act(async () => {
			renderComponent({item: UNMAPPED_FORM_ITEM});
		});

		expect(
			screen.queryByLabelText('success-action')
		).not.toBeInTheDocument();
	});

	it('embedded message is the default option selected', async () => {
		await act(async () => {
			renderComponent();
		});

		expect(screen.getByLabelText('embedded-message')).toBeInTheDocument();

		expect(
			screen.getByDisplayValue(
				'thank-you.-your-information-was-successfully-received'
			)
		).toBeInTheDocument();
	});

	it('save text as embedded message when user type it', async () => {
		await act(async () => {
			renderComponent();
		});

		const input = screen.queryByLabelText('embedded-message');

		await userEvent.clear(input);
		await userEvent.type(input, 'New message');

		fireEvent.blur(input);

		expect(updateFormItemConfig).toBeCalledWith(
			expect.objectContaining({
				itemConfig: {
					successMessage: {
						message: {en_US: 'New message'},
						type: 'embedded',
					},
				},
			})
		);
	});

	it('save url when user type it', async () => {
		await act(async () => {
			renderComponent({successMessage: {type: 'url'}});
		});

		const input = screen.getByLabelText('external-url');

		await userEvent.clear(input);
		await userEvent.type(input, 'https://liferay.com');

		fireEvent.blur(input);

		expect(updateFormItemConfig).toBeCalledWith(
			expect.objectContaining({
				itemConfig: {
					successMessage: {
						type: 'url',
						url: {en_US: 'https://liferay.com'},
					},
				},
			})
		);
	});

	it('loads the correct fields when the item is already configured with text', async () => {
		await act(async () => {
			renderComponent({successMessage: {message: {en_US: 'Message'}}});
		});

		const input = screen.getByLabelText('embedded-message');

		expect(input).toBeInTheDocument();
		expect(input.value).toBe('Message');
	});

	it('loads the correct fields when the item is already configured with url', async () => {
		await act(async () => {
			renderComponent({
				successMessage: {
					type: 'url',
					url: {en_US: 'https://liferay.com'},
				},
			});
		});

		const input = screen.getByLabelText('external-url');

		expect(input).toBeInTheDocument();
		expect(input.value).toBe('https://liferay.com');
	});

	it('renders the success notification text selector when Show Notification is enabled', async () => {
		await act(async () => {
			renderComponent({
				successMessage: {
					showNotification: true,
				},
			});
		});

		expect(
			screen.getByLabelText('success-notification-text')
		).toBeInTheDocument();
	});

	it('loads the correct fields when the item is already configured with page', async () => {
		config.layoutItemSelectorURL = 'http://example.com';

		global.Liferay = {
			...global.Liferay,
			PortletKeys: {
				ITEM_SELECTOR: '',
			},
			Util: {
				...global.Liferay.Util,
				getPortletNamespace: () => '',
			},
		};

		await act(async () => {
			renderComponent({
				successMessage: {
					layout: {
						groupId: '1',
						layoutId: '1',
						layoutUuid: 'uuid',
						privateLayout: false,
						title: 'My Page',
					},
					type: 'page',
				},
			});
		});

		expect(screen.getByLabelText('page')).toBeInTheDocument();
		expect(screen.getByDisplayValue('My Page')).toBeInTheDocument();
	});

	it('loads the correct fields when the item is already configured with display page', async () => {
		config.layoutItemSelectorURL = 'http://example.com';

		global.Liferay = {
			...global.Liferay,
			PortletKeys: {
				ITEM_SELECTOR: '',
			},
			Util: {
				...global.Liferay.Util,
				getPortletNamespace: () => '',
			},
		};

		await act(async () => {
			renderComponent({
				successMessage: {
					displayPage: 'myDisplayPageURL',
					type: 'displayPage',
				},
			});
		});

		expect(screen.getByLabelText('display-page')).toBeInTheDocument();
		expect(screen.getByText('My Display Page')).toBeInTheDocument();
	});

	it('renders the permission retriction message when the mapped item does not have permissions', () => {
		renderComponent({
			item: {
				...MAPPED_FORM_ITEM,

				config: {
					...MAPPED_FORM_ITEM.config,
					classNameId: '22222',
				},
			},
		});

		expect(
			screen.getByText(
				'this-content-cannot-be-displayed-due-to-permission-restrictions'
			)
		).toBeInTheDocument();
	});

	it('shows a warning if it is mapped to an object that does not exist anymore', () => {
		renderComponent({
			item: {
				children: [],
				config: {
					classNameId: '33333',
					classTypeId: '33333',
				},
				itemId: 'form',
				type: LAYOUT_DATA_ITEM_TYPES.form,
			},
		});

		expect(
			screen.getByText(
				'this-content-is-currently-unavailable-or-has-been-deleted.-users-cannot-see-this-fragment'
			)
		).toBeInTheDocument();
	});

	it('opens field selection modal with correct type when clicking sidebar button', async () => {
		await act(async () => {
			renderComponent();
		});

		const button = screen.getByText('manage-form-fields');

		await fireEvent.click(button);

		expect(openInfoFieldSelector).toBeCalledWith(
			expect.objectContaining({
				itemType: '11111-className',
			})
		);
	});
});
