/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

import '@testing-library/jest-dom/extend-expect';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {Picklist} from '../../../../src/main/resources/META-INF/resources/js/common/types/Picklist';
import StructureFieldSettings from '../../../../src/main/resources/META-INF/resources/js/structure_builder/components/settings/StructureFieldSettings';
import {State} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/contexts/StateContext';
import {Uuid} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/types/Uuid';
import {
	Field,
	getDefaultField,
} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/field';
import getUuid from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/getUuid';
import {MockCacheProvider} from '../mocks/MockCacheProvider';
import {MockState, MockStateProvider} from '../mocks/MockStateProvider';

const TEXT_FIELD_UUID = getUuid();

const FIELD: Field = {
	erc: 'test-erc',
	indexableConfig: {
		indexed: false,
	},
	label: {
		en_US: 'Test Field',
		es_ES: 'Campo de Prueba',
	},
	localized: false,
	locked: false,
	name: 'TextField',
	parent: getUuid(),
	required: false,
	settings: {},
	type: 'text',
	uuid: TEXT_FIELD_UUID,
};

const DEFAULT_STATE: State = {
	error: null,
	history: {deletedChildren: false},
	invalids: new Map(),
	publishedChildren: new Set(),
	selection: [],
	structure: {
		children: new Map([[TEXT_FIELD_UUID, FIELD]]),
		erc: 'structure-erc',
		label: 'untitled-structure' as any,
		name: 'UntitledStructure',
		spaces: [],
		status: 'new',
		uuid: getUuid(),
		workflows: {},
	},
	unsavedChanges: false,
};

const DEFAULT_PICKLISTS = [
	{
		externalReferenceCode: '1',
		id: 1,
		listTypeEntries: [],
		name: 'papaya',
		name_i18n: {en_US: 'Papaya'},
	},
];

const MOCK_DISPATCH = jest.fn();

const renderComponent = ({
	picklists = DEFAULT_PICKLISTS,
	state = DEFAULT_STATE,
	uuid = TEXT_FIELD_UUID,
}: {
	picklists?: Picklist[];
	state?: MockState;
	uuid?: Uuid;
} = {}) => {
	const field = state.structure?.children?.get(uuid) as Field;

	return render(
		<MockStateProvider dispatch={MOCK_DISPATCH} state={state}>
			<MockCacheProvider picklists={picklists}>
				<StructureFieldSettings field={field} />
			</MockCacheProvider>
		</MockStateProvider>
	);
};

describe('StructureFieldSettings', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		(global as any).Liferay.Language.direction = {
			en_US: 'rtl',
		};
	});

	it('renders', () => {
		renderComponent();

		expect(screen.getByText('Test Field')).toBeInTheDocument();
		expect(screen.getByText('field-type')).toBeInTheDocument();
		expect(screen.getByText('text')).toBeInTheDocument();
	});

	it('updates field name when input changes', async () => {
		renderComponent();

		const nameInput = screen.getByLabelText('field-name');

		await userEvent.clear(nameInput);
		await userEvent.type(nameInput, 'newFieldName');

		fireEvent.blur(nameInput);

		expect(MOCK_DISPATCH).toHaveBeenCalledWith({
			name: 'newFieldName',
			type: 'update-field',
			uuid: TEXT_FIELD_UUID,
		});
	});

	it('toggles required and localizable fields', async () => {
		renderComponent();

		await userEvent.click(screen.getByLabelText('mandatory'));

		expect(MOCK_DISPATCH).toHaveBeenCalledWith({
			required: true,
			type: 'update-field',
			uuid: TEXT_FIELD_UUID,
		});

		await userEvent.click(screen.getByLabelText('localizable'));

		expect(MOCK_DISPATCH).toHaveBeenCalledWith({
			localized: true,
			type: 'update-field',
			uuid: TEXT_FIELD_UUID,
		});
	});

	it('updates searchable configuration', async () => {
		renderComponent();

		await userEvent.click(screen.getByText('search'));

		await userEvent.click(screen.getByLabelText('searchable'));

		expect(MOCK_DISPATCH).toHaveBeenCalledWith({
			indexableConfig: {
				indexed: true,
				indexedAsKeyword: false,
				indexedLanguageId: 'en_US',
			},
			type: 'update-field',
			uuid: TEXT_FIELD_UUID,
		});
	});

	it('updates keyword configuration', async () => {
		renderComponent({
			state: {
				...DEFAULT_STATE,
				structure: {
					...DEFAULT_STATE.structure,
					children: new Map([
						[
							TEXT_FIELD_UUID,
							{
								...FIELD,
								indexableConfig: {
									indexed: true,
									indexedAsKeyword: false,
									indexedLanguageId: 'en_US',
								},
							},
						],
					]),
				},
			},
		});

		await userEvent.click(screen.getByLabelText('keyword'));

		expect(MOCK_DISPATCH).toHaveBeenCalledWith({
			indexableConfig: {
				indexed: true,
				indexedAsKeyword: true,
				indexedLanguageId: undefined,
			},
			type: 'update-field',
			uuid: TEXT_FIELD_UUID,
		});
	});

	it('updates specific date time configuration', async () => {
		const uuid = getUuid();

		renderComponent({
			state: {
				...DEFAULT_STATE,
				structure: {
					...DEFAULT_STATE.structure,
					children: new Map([
						[
							uuid,
							{
								...getDefaultField({
									parent: getUuid(),
									type: 'datetime',
								}),
								uuid,
							},
						],
					]),
				},
			},
			uuid,
		});

		await userEvent.click(screen.getByLabelText('time-storage'));

		await userEvent.click(screen.getByText('use-input-as-entered'));

		expect(MOCK_DISPATCH).toHaveBeenCalledWith({
			settings: {
				timeStorage: 'useInputAsEntered',
			},
			type: 'update-field',
			uuid,
		});
	});

	it('updates specific long text configuration', async () => {
		const uuid = getUuid();

		renderComponent({
			state: {
				...DEFAULT_STATE,
				structure: {
					...DEFAULT_STATE.structure,
					children: new Map([
						[
							uuid,
							{
								...getDefaultField({
									parent: getUuid(),
									type: 'long-text',
								}),
								uuid,
							},
						],
					]),
				},
			},
			uuid,
		});

		expect(
			screen.queryByLabelText('maximun-number-of-characters')
		).not.toBeInTheDocument();

		await userEvent.click(screen.getByLabelText('limit-characters'));

		const numberOfCharactersInput = screen.getByLabelText(
			'maximum-number-of-characters'
		);

		await userEvent.type(numberOfCharactersInput, '10');
		fireEvent.blur(numberOfCharactersInput);

		expect(MOCK_DISPATCH).toHaveBeenCalledWith({
			settings: {
				maxLength: 10,
				showCounter: true,
			},
			type: 'update-field',
			uuid,
		});
	});

	it('updates specific numeric configuration', async () => {
		const uuid = getUuid();

		renderComponent({
			state: {
				...DEFAULT_STATE,
				structure: {
					...DEFAULT_STATE.structure,
					children: new Map([
						[
							uuid,
							{
								...getDefaultField({
									parent: getUuid(),
									type: 'integer',
								}),
								uuid,
							},
						],
					]),
				},
			},
			uuid,
		});

		await userEvent.click(
			screen.getByLabelText('accept-unique-values-only')
		);

		expect(MOCK_DISPATCH).toHaveBeenCalledWith({
			settings: {
				uniqueValues: true,
			},
			type: 'update-field',
			uuid,
		});
	});

	it('updates specific text configuration', async () => {
		renderComponent();

		await userEvent.click(
			screen.getByLabelText('accept-unique-values-only')
		);

		expect(MOCK_DISPATCH).toHaveBeenCalledWith({
			settings: {
				uniqueValues: true,
			},
			type: 'update-field',
			uuid: TEXT_FIELD_UUID,
		});

		expect(
			screen.queryByLabelText('maximun-number-of-characters')
		).not.toBeInTheDocument();

		await userEvent.click(screen.getByLabelText('limit-characters'));

		const numberOfCharactersInput = screen.getByLabelText(
			'maximum-number-of-characters'
		);

		await userEvent.type(numberOfCharactersInput, '10');
		fireEvent.blur(numberOfCharactersInput);

		expect(MOCK_DISPATCH).toHaveBeenCalledWith({
			settings: {
				maxLength: 10,
				showCounter: true,
			},
			type: 'update-field',
			uuid: TEXT_FIELD_UUID,
		});
	});

	it('updates specific upload configuration', async () => {
		const uuid = getUuid();

		renderComponent({
			state: {
				...DEFAULT_STATE,
				structure: {
					...DEFAULT_STATE.structure,
					children: new Map([
						[
							uuid,
							{
								...getDefaultField({
									parent: getUuid(),
									type: 'upload',
								}),
								uuid,
							},
						],
					]),
				},
			},
			uuid,
		});

		expect(
			screen.queryByLabelText('storage-folder')
		).not.toBeInTheDocument();

		await userEvent.click(
			screen.getByLabelText('show-files-in-documents-and-media')
		);

		expect(MOCK_DISPATCH).toHaveBeenCalledWith({
			settings: {
				acceptedFileExtensions: 'jpeg, jpg, pdf, png',
				fileSource: 'userComputer',
				maximumFileSize: 100,
				showFilesInDocumentsAndMedia: true,
				storageDLFolderPath: '/new',
			},
			type: 'update-field',
			uuid,
		});

		MOCK_DISPATCH.mockClear();

		const acceptedFileExtensionsInput = screen.getByLabelText(
			'accepted-file-extensions'
		);

		await userEvent.clear(acceptedFileExtensionsInput);
		await userEvent.type(acceptedFileExtensionsInput, 'gif');
		fireEvent.blur(acceptedFileExtensionsInput);

		expect(MOCK_DISPATCH).toHaveBeenCalledWith({
			settings: {
				acceptedFileExtensions: 'gif',
				fileSource: 'userComputer',
				maximumFileSize: 100,
			},
			type: 'update-field',
			uuid,
		});

		const maximumFileSizeInput = screen.getByLabelText('maximum-file-size');

		await userEvent.clear(maximumFileSizeInput);
		await userEvent.type(maximumFileSizeInput, '200');
		fireEvent.blur(maximumFileSizeInput);

		expect(MOCK_DISPATCH).toHaveBeenCalledWith({
			settings: {
				acceptedFileExtensions: 'jpeg, jpg, pdf, png',
				fileSource: 'userComputer',
				maximumFileSize: 200,
			},
			type: 'update-field',
			uuid,
		});
	});

	it('updates the single select field with the selected picklist', async () => {
		const uuid = getUuid();

		renderComponent({
			state: {
				...DEFAULT_STATE,
				structure: {
					...DEFAULT_STATE.structure,
					children: new Map([
						[
							uuid,
							{
								...getDefaultField({
									parent: getUuid(),
									type: 'single-select',
								}),
								uuid,
							},
						],
					]),
				},
			},
			uuid,
		});

		await userEvent.click(screen.getByLabelText('picklist'));
		await userEvent.click(screen.getByText('papaya'));

		expect(MOCK_DISPATCH).toHaveBeenCalledWith({
			picklistId: 1,
			type: 'update-field',
			uuid,
		});
	});

	it('updates the multiselect field with the selected picklist', async () => {
		const uuid = getUuid();

		renderComponent({
			state: {
				...DEFAULT_STATE,
				structure: {
					...DEFAULT_STATE.structure,
					children: new Map([
						[
							uuid,
							{
								...getDefaultField({
									parent: getUuid(),
									type: 'multiselect',
								}),
								uuid,
							},
						],
					]),
				},
			},
			uuid,
		});

		await userEvent.click(screen.getByLabelText('picklist'));
		await userEvent.click(screen.getByText('papaya'));

		expect(MOCK_DISPATCH).toHaveBeenCalledWith({
			picklistId: 1,
			type: 'update-field',
			uuid,
		});
	});

	it('disables the picklist picker when there are no picklists to select', async () => {
		const uuid = getUuid();

		renderComponent({
			picklists: [],
			state: {
				...DEFAULT_STATE,
				structure: {
					...DEFAULT_STATE.structure,
					children: new Map([
						[
							uuid,
							{
								...getDefaultField({
									parent: getUuid(),
									type: 'single-select',
								}),
								uuid,
							},
						],
					]),
				},
			},
			uuid,
		});

		await waitFor(() => {
			expect(screen.getByLabelText('picklist')).toBeDisabled();
		});
	});

	it('disables fields when structure is published', () => {
		renderComponent({
			state: {
				...DEFAULT_STATE,
				publishedChildren: new Set([TEXT_FIELD_UUID]),
				structure: {
					...DEFAULT_STATE.structure,
					status: 'published',
				},
			},
		});

		expect(
			screen.getByLabelText('accept-unique-values-only')
		).toBeDisabled();
		expect(screen.getByLabelText('erc')).toBeDisabled();
		expect(screen.getByLabelText('field-name')).toBeDisabled();
		expect(screen.getByLabelText('localizable')).toBeDisabled();
		expect(screen.getByLabelText('mandatory')).toBeDisabled();

		expect(screen.getByLabelText('limit-characters')).not.toBeDisabled();
	});
});
