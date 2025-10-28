/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import ObjectFoldersSideBar from '../../components/ViewObjectDefinitions/ObjectFoldersSidebar';
import {exportObjectEntity} from '../../utils/exportObjectEntity';

const mockBaseResourceURL = 'http://localhost/resource';
const mockExportObjectFolderURL = 'http://localhost/export';
const mockImportObjectFolderURL = 'http://localhost/import';

const mockObjectDefinitionsActions = {
	create: {href: '', method: ''},
};

const mockObjectFolder1 = {
	actions: {get: {href: '', method: 'GET'}},
	dateCreated: '2023-08-07T14:42:21Z',
	dateModified: '2023-08-07T14:42:21Z',
	externalReferenceCode: 'folder1',
	id: 2,
	label: {en_US: 'Folder 1'},
	name: 'folder1',
	objectFolderItems: [],
};

const mockObjectFolder2 = {
	actions: {get: {href: '', method: 'GET'}},
	dateCreated: '2023-08-07T14:45:00Z',
	dateModified: '2023-08-07T14:45:00Z',
	externalReferenceCode: 'folder2',
	id: 1,
	label: {en_US: 'Folder 2'},
	name: 'folder2',
	objectFolderItems: [],
};

const mockObjectFoldersRequestInfo = {
	actions: {create: {href: '', method: 'POST'}},
	items: [mockObjectFolder1, mockObjectFolder2],
};

const mockSelectedObjectFolder = {
	externalReferenceCode: 'folder1',
	id: 1,
	label: {en_US: 'Folder 1'},
	name: 'folder-1',
};

const mockSetModalImportProperties = jest.fn();
const mockSetSelectedObjectFolder = jest.fn();
const mockSetShowModal = jest.fn();

jest.mock('frontend-js-web', () => ({
	createResourceURL: jest.fn(() => ({href: mockExportObjectFolderURL})),
	sub: jest.fn(),
}));

jest.mock('../../utils/exportObjectEntity', () => ({
	exportObjectEntity: jest.fn(),
}));

describe('The ObjectFoldersSidebar component should', () => {
	it('render all created object folders', () => {
		render(
			<ObjectFoldersSideBar
				baseResourceURL=""
				importObjectFolderURL=""
				objectDefinitionsActions={{create: {href: '', method: 'POST'}}}
				objectFoldersRequestInfo={mockObjectFoldersRequestInfo}
				portletNamespace=""
				selectedObjectFolder={mockObjectFolder1}
				setModalImportProperties={() => {}}
				setSelectedObjectFolder={() => {}}
				setShowModal={() => {}}
			/>
		);

		expect(screen.getAllByRole('listitem')).toHaveLength(2);

		expect(screen.getByText('Folder 1')).toBeInTheDocument();

		expect(screen.getByText('Folder 2')).toBeInTheDocument();

		userEvent.click(
			screen.getByRole('button', {name: 'object-folder-actions'})
		);

		const menuItem = screen.getAllByRole('menuitem');

		expect(menuItem).toHaveLength(2);

		expect(menuItem[0]).toHaveAttribute('value', 'exportObjectFolder');

		expect(menuItem[1]).toHaveAttribute('value', 'importObjectFolder');
	});

	it('renders object folders and allows selection', () => {
		render(
			<ObjectFoldersSideBar
				baseResourceURL={mockBaseResourceURL}
				importObjectFolderURL={mockImportObjectFolderURL}
				objectDefinitionsActions={mockObjectDefinitionsActions}
				objectFoldersRequestInfo={mockObjectFoldersRequestInfo}
				portletNamespace="portletNamespace"
				selectedObjectFolder={mockSelectedObjectFolder}
				setModalImportProperties={mockSetModalImportProperties}
				setSelectedObjectFolder={mockSetSelectedObjectFolder}
				setShowModal={mockSetShowModal}
			/>
		);

		expect(screen.getByText('Folder 1')).toBeInTheDocument();
		expect(screen.getByText('Folder 2')).toBeInTheDocument();

		fireEvent.click(screen.getByText('Folder 2'));
		expect(mockSetSelectedObjectFolder).toHaveBeenCalledWith(
			mockObjectFoldersRequestInfo.items[1]
		);
	});

	it('triggers export action for selected folder', () => {
		render(
			<ObjectFoldersSideBar
				baseResourceURL={mockBaseResourceURL}
				importObjectFolderURL={mockImportObjectFolderURL}
				objectDefinitionsActions={mockObjectDefinitionsActions}
				objectFoldersRequestInfo={mockObjectFoldersRequestInfo}
				portletNamespace="portletNamespace"
				selectedObjectFolder={mockSelectedObjectFolder}
				setModalImportProperties={mockSetModalImportProperties}
				setSelectedObjectFolder={mockSetSelectedObjectFolder}
				setShowModal={mockSetShowModal}
			/>
		);

		fireEvent.click(screen.getByLabelText('object-folder-actions'));
		fireEvent.click(screen.getByText('export-object-folder'));

		expect(mockSetShowModal).not.toHaveBeenCalled();

		expect(exportObjectEntity).toHaveBeenCalledWith({
			exportObjectEntityURL: mockExportObjectFolderURL,
			objectEntityId: mockSelectedObjectFolder.id,
		});
	});

	it('opens import modal when clicking import action', () => {
		render(
			<ObjectFoldersSideBar
				baseResourceURL={mockBaseResourceURL}
				importObjectFolderURL={mockImportObjectFolderURL}
				objectDefinitionsActions={mockObjectDefinitionsActions}
				objectFoldersRequestInfo={mockObjectFoldersRequestInfo}
				portletNamespace="portletNamespace"
				selectedObjectFolder={mockSelectedObjectFolder}
				setModalImportProperties={mockSetModalImportProperties}
				setSelectedObjectFolder={mockSetSelectedObjectFolder}
				setShowModal={mockSetShowModal}
			/>
		);

		fireEvent.click(screen.getByLabelText('object-folder-actions'));
		fireEvent.click(screen.getByText('import-object-folder'));

		expect(mockSetModalImportProperties).toHaveBeenCalledWith({
			JSONInputId: 'objectFolderJSON',
			apiURL: '/o/object-admin/v1.0/object-folders/by-external-reference-code/',
			importURL: mockImportObjectFolderURL,
			modalImportKey: 'objectFolder',
		});

		expect(mockSetShowModal).toHaveBeenCalledWith(expect.any(Function));
	});

	it('renders correctly without selectedObjectFolder', () => {
		render(
			<ObjectFoldersSideBar
				baseResourceURL={mockBaseResourceURL}
				importObjectFolderURL={mockImportObjectFolderURL}
				objectDefinitionsActions={mockObjectDefinitionsActions}
				objectFoldersRequestInfo={mockObjectFoldersRequestInfo}
				portletNamespace="portletNamespace"
				setModalImportProperties={mockSetModalImportProperties}
				setSelectedObjectFolder={mockSetSelectedObjectFolder}
				setShowModal={mockSetShowModal}
			/>
		);

		expect(
			screen.queryByLabelText('object-folder-actions')
		).not.toBeInTheDocument();
		expect(
			screen.queryByLabelText('add-object-folder')
		).not.toBeInTheDocument();
	});

	it('opens add folder modal when "add" button is clicked', () => {
		render(
			<ObjectFoldersSideBar
				baseResourceURL={mockBaseResourceURL}
				importObjectFolderURL={mockImportObjectFolderURL}
				objectDefinitionsActions={mockObjectDefinitionsActions}
				objectFoldersRequestInfo={mockObjectFoldersRequestInfo}
				portletNamespace="portletNamespace"
				selectedObjectFolder={mockSelectedObjectFolder}
				setModalImportProperties={mockSetModalImportProperties}
				setSelectedObjectFolder={mockSetSelectedObjectFolder}
				setShowModal={mockSetShowModal}
			/>
		);

		fireEvent.click(screen.getByLabelText('add-object-folder'));

		expect(mockSetShowModal).toHaveBeenCalledWith(expect.any(Function));
	});
});
