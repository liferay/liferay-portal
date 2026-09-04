/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, waitFor} from '@testing-library/react';
import React from 'react';
import {ReactFlowProvider} from 'react-flow-renderer';

import '@testing-library/jest-dom';

import EditObjectFolder from '../../components/ModelBuilder/EditObjectFolder';
import {ObjectFolderContextProvider} from '../../components/ModelBuilder/ModelBuilderContext/objectFolderContext';
import {mockReactFlow} from './Diagram/reactFlowMocks';

const OBJECT_FOLDER_EXTERNAL_REFERENCE_CODE = 'OBJECT_FOLDER_ERC';

const OBJECT_FOLDER_NAME = 'objectFolder';

const OBJECT_DEFINITION_EXTERNAL_REFERENCE_CODE = 'OBJECT_DEFINITION_ERC';

const objectDefinition = {
	actions: {},
	defaultLanguageId: 'en_US',
	externalReferenceCode: OBJECT_DEFINITION_EXTERNAL_REFERENCE_CODE,
	id: 1,
	label: {en_US: 'Object Definition'},
	name: 'ObjectDefinition',
	objectFields: [],
	objectFolderExternalReferenceCode: OBJECT_FOLDER_EXTERNAL_REFERENCE_CODE,
	objectRelationships: [],
	status: {code: 0, label: 'approved', label_i18n: 'Approved'},
};

function buildObjectFolder(positionX: number, positionY: number) {
	return {
		actions: {},
		externalReferenceCode: OBJECT_FOLDER_EXTERNAL_REFERENCE_CODE,
		id: 1,
		label: {en_US: 'Object Folder'},
		name: OBJECT_FOLDER_NAME,
		objectFolderItems: [
			{
				linkedObjectDefinition: false,
				objectDefinitionExternalReferenceCode:
					OBJECT_DEFINITION_EXTERNAL_REFERENCE_CODE,
				positionX,
				positionY,
			},
		],
	};
}

function getObjectFolderPutRequests() {
	return (fetch as unknown as jest.Mock).mock.calls.filter(
		([url, options]) =>
			options?.method === 'PUT' &&
			String(url).includes(
				'/o/object-admin/v1.0/object-folders/by-external-reference-code/'
			)
	);
}

function mockFetchResponses(positionX: number, positionY: number) {
	(fetch as unknown as jest.Mock).mockImplementation((url: string) => {
		if (String(url).includes('/object-folders?pageSize=-1')) {
			return Promise.resolve(
				new Response(
					JSON.stringify({
						items: [buildObjectFolder(positionX, positionY)],
					})
				)
			);
		}

		if (String(url).includes('/object-definitions?')) {
			return Promise.resolve(
				new Response(JSON.stringify({items: [objectDefinition]}))
			);
		}

		if (String(url).includes('get_object_definition_info')) {
			return Promise.resolve(
				new Response(
					JSON.stringify({tableName: 'ObjectDefinitionTable'})
				)
			);
		}

		return Promise.resolve(new Response(JSON.stringify({})));
	});
}

const ReactFlowProviderWrapper = ReactFlowProvider as React.FC<{
	children?: React.ReactNode;
}>;

function renderEditObjectFolder() {
	return render(
		<ReactFlowProviderWrapper>
			<ObjectFolderContextProvider
				value={{
					baseResourceURL:
						'http://localhost/base-resource-url?p_p_id=com_liferay_object_web_internal_object_definitions_portlet_ObjectDefinitionsPortlet',
					learnResourceContext: {},
					objectDefinitionPermissionsURL: '',
					objectFolderName: OBJECT_FOLDER_NAME,
				}}
			>
				<EditObjectFolder
					companies={[]}
					objectRelationshipDeletionTypes={[]}
					sites={[]}
					viewObjectDefinitionsURL="http://localhost/view-object-definitions"
				/>
			</ObjectFolderContextProvider>
		</ReactFlowProviderWrapper>
	);
}

describe('EditObjectFolder', () => {
	beforeEach(() => {
		mockReactFlow();
	});

	it('does not save the object folder when every node is already placed', async () => {
		mockFetchResponses(100, 200);

		renderEditObjectFolder();

		await waitFor(() =>
			expect(
				(fetch as unknown as jest.Mock).mock.calls.some(([url]) =>
					String(url).includes('get_object_definition_info')
				)
			).toBe(true)
		);

		expect(getObjectFolderPutRequests()).toHaveLength(0);
	});

	it('saves the object folder once when a node position was computed', async () => {
		mockFetchResponses(0, 0);

		renderEditObjectFolder();

		await waitFor(() =>
			expect(getObjectFolderPutRequests()).toHaveLength(1)
		);
	});
});
