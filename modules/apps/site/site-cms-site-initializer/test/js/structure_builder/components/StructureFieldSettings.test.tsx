/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

import '@testing-library/jest-dom/extend-expect';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import StructureFieldSettings from '../../../../src/main/resources/META-INF/resources/js/structure_builder/components/settings/StructureFieldSettings';
import {
	Action,
	State,
} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/contexts/StateContext';
import {Uuid} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/types/Uuid';
import {
	Field,
	getDefaultField,
} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/field';
import getUuid from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/getUuid';
import {Picklist} from '../../../../src/main/resources/META-INF/resources/js/types/Picklist';
import {MockCacheProvider} from '../mocks/MockCacheProvider';
import {MockStateProvider} from '../mocks/MockStateProvider';

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
	name: 'TextField',
	required: false,
	settings: {},
	type: 'text',
	uuid: TEXT_FIELD_UUID,
};

const DEFAULT_STATE: State = {
	erc: 'structure-erc',
	error: null,
	fields: new Map([[TEXT_FIELD_UUID, FIELD]]),
	history: {deletedFields: false},
	id: null,
	invalids: new Map(),
	label: 'untitled-structure' as any,
	name: 'UntitledStructure',
	publishedFields: new Set(),
	selection: [],
	spaces: [],
	status: 'new',
	unsavedChanges: false,
	uuid: getUuid(),
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

const renderComponent = ({
	dispatch = jest.fn(),
	picklists = DEFAULT_PICKLISTS,
	state = DEFAULT_STATE,
	uuid = TEXT_FIELD_UUID,
}: {
	dispatch?: React.Dispatch<Action>;
	picklists?: Picklist[];
	state?: State;
	uuid?: Uuid;
} = {}) => {
	const field = state.fields.get(uuid) as Field;

	return render(
		<MockStateProvider dispatch={dispatch} state={state}>
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
		const mockDispatch = jest.fn();

		renderComponent({dispatch: mockDispatch});

		const nameInput = screen.getByLabelText('field-name');

		await userEvent.clear(nameInput);
		await userEvent.type(nameInput, 'newFieldName');

		fireEvent.blur(nameInput);

		expect(mockDispatch).toHaveBeenCalledWith({
			name: 'newFieldName',
			type: 'update-field',
			uuid: TEXT_FIELD_UUID,
		});
	});

	it('toggles required and localizable fields', async () => {
		const mockDispatch = jest.fn();

		renderComponent({dispatch: mockDispatch});

		await userEvent.click(screen.getByLabelText('mandatory'));

		expect(mockDispatch).toHaveBeenCalledWith({
			required: true,
			type: 'update-field',
			uuid: TEXT_FIELD_UUID,
		});

		await userEvent.click(screen.getByLabelText('localizable'));

		expect(mockDispatch).toHaveBeenCalledWith({
			localized: true,
			type: 'update-field',
			uuid: TEXT_FIELD_UUID,
		});
	});

	it('updates searchable configuration', async () => {
		const mockDispatch = jest.fn();

		renderComponent({dispatch: mockDispatch});

		await userEvent.click(screen.getByText('search'));

		await userEvent.click(screen.getByLabelText('searchable'));

		expect(mockDispatch).toHaveBeenCalledWith({
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
		const mockDispatch = jest.fn();

		renderComponent({
			dispatch: mockDispatch,
			state: {
				...DEFAULT_STATE,
				fields: new Map([
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
		});

		await userEvent.click(screen.getByLabelText('keyword'));

		expect(mockDispatch).toHaveBeenCalledWith({
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
		const mockDispatch = jest.fn();

		const uuid = getUuid();

		renderComponent({
			dispatch: mockDispatch,
			state: {
				...DEFAULT_STATE,
				fields: new Map([
					[
						uuid,
						{
							...getDefaultField({type: 'datetime'}),
							uuid,
						},
					],
				]),
			},
			uuid,
		});

		await userEvent.click(screen.getByLabelText('time-storage'));

		await userEvent.click(screen.getByText('use-input-as-entered'));

		expect(mockDispatch).toHaveBeenCalledWith({
			settings: {
				timeStorage: 'useInputAsEntered',
			},
			type: 'update-field',
			uuid,
		});
	});

	it('updates specific long text configuration', async () => {
		const mockDispatch = jest.fn();

		const uuid = getUuid();

		renderComponent({
			dispatch: mockDispatch,
			state: {
				...DEFAULT_STATE,
				fields: new Map([
					[
						uuid,
						{
							...getDefaultField({type: 'long-text'}),
							uuid,
						},
					],
				]),
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

		expect(mockDispatch).toHaveBeenCalledWith({
			settings: {
				maxLength: 10,
				showCounter: true,
			},
			type: 'update-field',
			uuid,
		});
	});

	it('updates specific numeric configuration', async () => {
		const mockDispatch = jest.fn();

		const uuid = getUuid();

		renderComponent({
			dispatch: mockDispatch,
			state: {
				...DEFAULT_STATE,
				fields: new Map([
					[
						uuid,
						{
							...getDefaultField({type: 'integer'}),
							uuid,
						},
					],
				]),
			},
			uuid,
		});

		await userEvent.click(
			screen.getByLabelText('accept-unique-values-only')
		);

		expect(mockDispatch).toHaveBeenCalledWith({
			settings: {
				uniqueValues: true,
			},
			type: 'update-field',
			uuid,
		});
	});

	it('updates specific text configuration', async () => {
		const mockDispatch = jest.fn();

		renderComponent({dispatch: mockDispatch});

		await userEvent.click(
			screen.getByLabelText('accept-unique-values-only')
		);

		expect(mockDispatch).toHaveBeenCalledWith({
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

		expect(mockDispatch).toHaveBeenCalledWith({
			settings: {
				maxLength: 10,
				showCounter: true,
			},
			type: 'update-field',
			uuid: TEXT_FIELD_UUID,
		});
	});

	it('updates specific upload configuration', async () => {
		const mockDispatch = jest.fn();

		const uuid = getUuid();

		renderComponent({
			dispatch: mockDispatch,
			state: {
				...DEFAULT_STATE,
				fields: new Map([
					[
						uuid,
						{
							...getDefaultField({type: 'upload'}),
							uuid,
						},
					],
				]),
			},
			uuid,
		});

		expect(
			screen.queryByLabelText('storage-folder')
		).not.toBeInTheDocument();

		await userEvent.click(
			screen.getByLabelText('show-files-in-documents-and-media')
		);

		expect(mockDispatch).toHaveBeenCalledWith({
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

		mockDispatch.mockClear();

		const acceptedFileExtensionsInput = screen.getByLabelText(
			'accepted-file-extensions'
		);

		await userEvent.clear(acceptedFileExtensionsInput);
		await userEvent.type(acceptedFileExtensionsInput, 'gif');
		fireEvent.blur(acceptedFileExtensionsInput);

		expect(mockDispatch).toHaveBeenCalledWith({
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

		expect(mockDispatch).toHaveBeenCalledWith({
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
		const mockDispatch = jest.fn();
		const uuid = getUuid();

		renderComponent({
			dispatch: mockDispatch,
			state: {
				...DEFAULT_STATE,
				fields: new Map([
					[uuid, {...getDefaultField({type: 'single-select'}), uuid}],
				]),
			},
			uuid,
		});

		await userEvent.click(screen.getByLabelText('picklist'));
		await userEvent.click(screen.getByText('papaya'));

		expect(mockDispatch).toHaveBeenCalledWith({
			picklistId: 1,
			type: 'update-field',
			uuid,
		});
	});

	it('updates the multiselect field with the selected picklist', async () => {
		const mockDispatch = jest.fn();
		const uuid = getUuid();

		renderComponent({
			dispatch: mockDispatch,
			state: {
				...DEFAULT_STATE,
				fields: new Map([
					[uuid, {...getDefaultField({type: 'multiselect'}), uuid}],
				]),
			},
			uuid,
		});

		await userEvent.click(screen.getByLabelText('picklist'));
		await userEvent.click(screen.getByText('papaya'));

		expect(mockDispatch).toHaveBeenCalledWith({
			picklistId: 1,
			type: 'update-field',
			uuid,
		});
	});

	it('disables the picklist picker when there are no picklists to select', async () => {
		const mockDispatch = jest.fn();
		const uuid = getUuid();

		renderComponent({
			dispatch: mockDispatch,
			picklists: [],
			state: {
				...DEFAULT_STATE,
				fields: new Map([
					[uuid, {...getDefaultField({type: 'single-select'}), uuid}],
				]),
			},
			uuid,
		});

		await waitFor(() => {
			expect(screen.getByLabelText('picklist')).toBeDisabled();
		});
	});

	it('disables fields when structure is published', () => {
		const mockDispatch = jest.fn();

		renderComponent({
			dispatch: mockDispatch,
			state: {
				...DEFAULT_STATE,
				publishedFields: new Set([TEXT_FIELD_UUID]),
				status: 'published',
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
