/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import ObjectFolderCardHeader from '../../components/ViewObjectDefinitions/ObjectFolderCardHeader';
import {getObjectFolderActions} from '../../components/ViewObjectDefinitions/objectDefinitionUtil';

const mockSelectedObjectFolder = {
	externalReferenceCode: 'TST123',
	label: {en_US: 'Test Folder'},
	name: 'test-folder',
};

const ticketFolderHTTPMethods = {
	objectDefinitionActions: {
		create: {href: '', method: 'POST'},
	},
	objectFolderActions: {
		delete: {href: '', method: 'DELETE'},
		get: {href: '', method: 'GET'},
		permissions: {href: '', method: 'PATCH'},
		update: {href: '', method: 'PUT'},
	},
};

const mockItems = [
	{
		label: 'Edit',
		onClick: jest.fn(),
		value: 'edit',
	},
	{
		label: 'Delete',
		onClick: jest.fn(),
		value: 'delete',
	},
];

const mockModelBuilderURL = 'http://localhost:8080/model-builder';

describe('The ObjectFolderCardHeader component should', () => {
	it('render all object folder actions', () => {
		render(
			<ObjectFolderCardHeader
				items={
					getObjectFolderActions({
						actions: {
							objectDefinitionActions:
								ticketFolderHTTPMethods.objectDefinitionActions,
							objectFolderActions:
								ticketFolderHTTPMethods.objectFolderActions,
						},
						baseResourceURL: '',
						importObjectDefinitionURL: '',
						objectFolderExternalReferenceCode: '',
						objectFolderId: 1,
						objectFolderPermissionsURL: '',
						portletNamespace: '',
						setModalImportProperties: () => {},
						setShowModal: () => {},
					}) as IItem[]
				}
				modelBuilderURL=""
				selectedObjectFolder={mockSelectedObjectFolder}
			></ObjectFolderCardHeader>
		);

		userEvent.click(
			screen.getByRole('button', {name: 'object-folder-actions'})
		);

		const menuItem = screen.getAllByRole('menuitem');

		expect(menuItem).toHaveLength(5);

		expect(menuItem[0]).toHaveAttribute('value', 'editObjectFolder');

		expect(menuItem[1]).toHaveAttribute('value', 'exportObjectFolder');

		expect(menuItem[2]).toHaveAttribute('value', 'importObjectDefinition');

		expect(menuItem[3]).toHaveAttribute('value', 'objectFolderPermissions');

		expect(menuItem[4]).toHaveAttribute('value', 'deleteObjectFolder');
	});

	it('renders with selected object folder and items', () => {
		render(
			<ObjectFolderCardHeader
				items={mockItems}
				modelBuilderURL={mockModelBuilderURL}
				selectedObjectFolder={mockSelectedObjectFolder}
			/>
		);

		expect(screen.getByText('Test Folder')).toBeInTheDocument();
		expect(screen.getByText('erc:')).toBeInTheDocument();
		expect(screen.getByText('TST123')).toBeInTheDocument();
		expect(
			screen.getByLabelText('object-folder-actions')
		).toBeInTheDocument();
		expect(
			screen.getByLabelText('view-in-model-builder')
		).toBeInTheDocument();
	});

	it('renders without a selected object folder', () => {
		render(
			<ObjectFolderCardHeader
				items={mockItems}
				modelBuilderURL={mockModelBuilderURL}
			/>
		);

		expect(
			screen.queryByLabelText('object-folder-actions')
		).not.toBeInTheDocument();
		expect(screen.queryByText('erc:')).toBeInTheDocument();
		expect(
			screen.queryByLabelText('view-in-model-builder')
		).not.toBeInTheDocument();
	});

	it('opens dropdown menu when trigger is clicked', () => {
		render(
			<ObjectFolderCardHeader
				items={mockItems}
				modelBuilderURL={mockModelBuilderURL}
				selectedObjectFolder={mockSelectedObjectFolder}
			/>
		);

		const dropdownTrigger = screen.getByLabelText('object-folder-actions');
		fireEvent.click(dropdownTrigger);

		mockItems.forEach((item) => {
			expect(screen.getByText(item.label)).toBeInTheDocument();
		});
	});

	it('displays empty external reference code if not provided', () => {
		const mockSelectedObjectFolder = {
			label: {en_US: 'Test Folder'},
			name: 'test-folder',
		};

		render(
			<ObjectFolderCardHeader
				items={mockItems}
				modelBuilderURL={mockModelBuilderURL}
				selectedObjectFolder={mockSelectedObjectFolder}
			/>
		);

		expect(screen.getByText('erc:')).toBeInTheDocument();
		expect(screen.queryByText('TST123')).not.toBeInTheDocument();
	});

	it('displays folder name if label is not provided', () => {
		const mockSelectedObjectFolder = {
			externalReferenceCode: 'TST123',
			name: 'test-folder',
		};

		render(
			<ObjectFolderCardHeader
				items={mockItems}
				modelBuilderURL={mockModelBuilderURL}
				selectedObjectFolder={mockSelectedObjectFolder}
			/>
		);

		expect(screen.getByText('test-folder')).toBeInTheDocument();
		expect(screen.getByText('TST123')).toBeInTheDocument();
	});
});
