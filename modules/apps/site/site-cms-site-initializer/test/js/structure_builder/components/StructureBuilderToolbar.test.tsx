/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen, waitFor, within} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import SpaceService from '../../../../src/main/resources/META-INF/resources/js/common/services/SpaceService';
import StructureService from '../../../../src/main/resources/META-INF/resources/js/common/services/StructureService';
import {ObjectDefinitions} from '../../../../src/main/resources/META-INF/resources/js/common/types/ObjectDefinition';
import {Space} from '../../../../src/main/resources/META-INF/resources/js/common/types/Space';
import StructureBuilderToolbar from '../../../../src/main/resources/META-INF/resources/js/structure_builder/components/StructureBuilderToolbar';
import {Structure} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/types/Structure';
import {Field} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/field';
import getUuid from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/getUuid';
import {addParams, mockNavigate} from '../../__mocks__/frontend-js-web';
import {MockCacheProvider} from '../mocks/MockCacheProvider';
import {MockState, MockStateProvider} from '../mocks/MockStateProvider';

jest.mock('@liferay/layout-js-components-web', () => {
	const actual = jest.requireActual('@liferay/layout-js-components-web');

	return {
		...actual,
		openConfirmModal: jest.fn(),
	};
});

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/structure_builder/config',
	() => {
		return {
			config: {
				editStructureDisplayPageURL: 'http://localhost:8080/edit',
				resetStructureDisplayPageURL: 'http://localhost:8080/reset',
				structureBuilderURL: 'http://localhost:8080/structure-builder',
			},
		};
	}
);

const DEFAULT_CHILDREN = new Map([[getUuid(), {} as Field]]);

const MOCK_SPACE_1: Space = {
	assetLibraryKey: 'space-1-key',
	creatorUserId: '1',
	description: 'Space 1',
	externalReferenceCode: 'space-1-erc',
	friendlyURL: '/asset-library-1',
	id: 1,
	name: 'Space 1',
	siteId: 1001,
};

const MOCK_SPACE_2: Space = {
	assetLibraryKey: 'space-2-key',
	creatorUserId: '1',
	description: 'Space 2',
	externalReferenceCode: 'space-2-erc',
	friendlyURL: '/asset-library-2',
	id: 2,
	name: 'Space 2',
	siteId: 1002,
};

const MOCK_OBJECT_DEFINITIONS: ObjectDefinitions = {
	'structure-erc': {
		externalReferenceCode: 'structure-erc',
		label: {en_US: 'Structure'} as any,
		objectDefinitionSettings: [
			{
				name: 'acceptedGroupExternalReferenceCodes',
				value: 'space-1-erc,space-2-erc',
			},
			{name: 'acceptAllGroups', value: 'false'},
		],
		pluralLabel: {en_US: 'Structures'} as any,
		scope: 'site',
	} as any,
};

const renderComponent = (state: MockState) => {
	const structure: Partial<Structure> = {
		children: DEFAULT_CHILDREN,
		erc: 'structure-erc',
		spaces: 'all',
		...state.structure,
	};

	return render(
		<MockCacheProvider
			objectDefinitions={MOCK_OBJECT_DEFINITIONS}
			spaces={[MOCK_SPACE_1, MOCK_SPACE_2]}
		>
			<MockStateProvider state={{...state, structure}}>
				<StructureBuilderToolbar />
			</MockStateProvider>
		</MockCacheProvider>
	);
};

describe('StructureBuilderToolbar', () => {
	beforeAll(() => {
		StructureService.createStructure = jest
			.fn()
			.mockResolvedValue({error: null});

		StructureService.updateStructure = jest
			.fn()
			.mockResolvedValue({error: null});

		SpaceService.getSpaceContents = jest
			.fn()
			.mockResolvedValue({data: {totalCount: 0}, error: null});
	});

	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('Save button is not shown if structure is published', async () => {
		renderComponent({structure: {status: 'published'}});

		const saveButton = screen.queryByText('save');

		expect(saveButton).not.toBeInTheDocument();
	});

	it('Save button calls correct endpoint when status is new', async () => {
		renderComponent({structure: {status: 'new'}});

		const saveButton = screen.getByText('save');

		await userEvent.click(saveButton);

		await waitFor(() => {
			expect(StructureService.createStructure).toBeCalled();
		});

		expect(StructureService.updateStructure).not.toBeCalled();
	});

	it('Save button calls correct endpoint when status is draft', async () => {
		renderComponent({structure: {status: 'draft'}});

		const saveButton = screen.getByText('save');

		await userEvent.click(saveButton);

		await waitFor(() => {
			expect(StructureService.updateStructure).toBeCalled();
		});

		expect(StructureService.createStructure).not.toBeCalled();
	});

	it('Publish button calls correct endpoint when status is new', async () => {
		renderComponent({structure: {status: 'new'}});

		const publishButton = screen.getByRole('button', {name: 'publish'});

		await userEvent.click(publishButton);

		await waitFor(() => {
			expect(StructureService.createStructure).toBeCalled();
		});

		expect(StructureService.updateStructure).not.toBeCalled();
	});

	it('Publish button calls correct endpoint when status is draft', async () => {
		renderComponent({structure: {status: 'draft'}});

		const publishButton = screen.getByText('publish');

		await userEvent.click(publishButton);

		await waitFor(() => {
			expect(StructureService.updateStructure).toBeCalled();
		});

		expect(StructureService.createStructure).not.toBeCalled();
	});

	it('Publish button calls correct endpoint when status is published', async () => {
		renderComponent({structure: {status: 'published'}});

		const publishButton = screen.getByText('publish');

		await userEvent.click(publishButton);

		await waitFor(() => {
			expect(StructureService.updateStructure).toBeCalled();
		});

		expect(StructureService.createStructure).not.toBeCalled();
	});

	it('Shows warning modal when a published field has been deleted', async () => {
		renderComponent({
			history: {
				deletedChildren: [{} as Field],
				deletedGroupERCs: [],
				deletedRelationships: [],
				modifiedNames: new Set(),
				modifiedSlugs: new Set(),
			},
			structure: {status: 'published'},
		});

		const publishButton = screen.getByText('publish');

		await userEvent.click(publishButton);

		await waitFor(() => {
			expect(
				require('@liferay/layout-js-components-web').openConfirmModal
			).toBeCalledWith(
				expect.objectContaining({
					text: 'you-have-made-changes-to-the-content-structure-that-may-impact-existing-stored-data-once-published',
				})
			);
		});

		expect(StructureService.createStructure).not.toBeCalled();
		expect(StructureService.updateStructure).not.toBeCalled();
	});

	it('Shows modal to publish when trying to customize editor and the structure is not published', async () => {
		renderComponent({structure: {status: 'new'}});

		const managementBar: HTMLElement | null =
			document.querySelector('.component-tbar')!;

		const customizeEditorButton =
			within(managementBar).getByText('customize-editor');

		await userEvent.click(customizeEditorButton);

		await waitFor(() => {
			expect(
				require('@liferay/layout-js-components-web').openConfirmModal
			).toBeCalledWith(
				expect.objectContaining({
					text: 'to-customize-the-editor-you-need-to-publish-the-content-structure-first',
				})
			);
		});
	});

	it('Shows modal to publish when trying to customize editor and the structure is published and there are changes', async () => {
		renderComponent({
			structure: {status: 'published'},
			unsavedChanges: true,
		});

		const managementBar: HTMLElement | null =
			document.querySelector('.component-tbar')!;

		const customizeEditorButton =
			within(managementBar).getByText('customize-editor');

		await userEvent.click(customizeEditorButton);

		await waitFor(() => {
			expect(
				require('@liferay/layout-js-components-web').openConfirmModal
			).toBeCalledWith(
				expect.objectContaining({
					text: 'to-customize-the-editor-you-need-to-publish-the-content-structure-first',
				})
			);
		});
	});

	it('Shows modal to publish when trying to customize editor and the structure is published and some fields have been deleted', async () => {
		renderComponent({
			history: {
				deletedChildren: [{} as Field],
				deletedGroupERCs: [],
				deletedRelationships: [],
				modifiedNames: new Set(),
				modifiedSlugs: new Set(),
			},
			structure: {status: 'published'},
		});

		const managementBar: HTMLElement | null =
			document.querySelector('.component-tbar')!;

		const customizeEditorButton =
			within(managementBar).getByText('customize-editor');

		await userEvent.click(customizeEditorButton);

		await waitFor(() => {
			expect(
				require('@liferay/layout-js-components-web').openConfirmModal
			).toBeCalledWith(
				expect.objectContaining({
					text: 'to-customize-the-editor-you-need-to-publish-the-content-structure-first.-you-have-made-changes-to-the-content-structure-that-may-impact-existing-stored-data-once-published',
				})
			);
		});
	});

	it('Navigates to customize editor if the structure is published', async () => {
		const expectedUrl =
			'http://localhost:8080/edit?backURL=http%3A%2F%2Flocalhost%3A8080%2Fstructure-builder%3FobjectDefinitionExternalReferenceCode%3Dstructure-erc&objectDefinitionExternalReferenceCode=structure-erc';

		addParams.mockReturnValue(expectedUrl);

		renderComponent({structure: {status: 'published'}});

		const managementBar: HTMLElement | null =
			document.querySelector('.component-tbar')!;

		const customizeEditorButton =
			within(managementBar).getByText('customize-editor');

		await userEvent.click(customizeEditorButton);

		await waitFor(() => {
			expect(mockNavigate).toBeCalledWith(
				expect.stringContaining(expectedUrl)
			);
		});
	});

	it('restores removed spaces with content when publishing', async () => {
		(
			require('@liferay/layout-js-components-web')
				.openConfirmModal as jest.Mock
		).mockResolvedValue(true);

		(SpaceService.getSpaceContents as jest.Mock).mockResolvedValue({
			data: {totalCount: 2},
			error: null,
		});

		renderComponent({
			structure: {
				erc: 'structure-erc',
				path: '/my-structure',
				spaces: [MOCK_SPACE_1.externalReferenceCode],
				status: 'draft',
			},
		});

		const publishButton = screen.getByRole('button', {name: 'publish'});

		await userEvent.click(publishButton);

		await waitFor(() => {
			expect(SpaceService.getSpaceContents).toBeCalledWith({
				path: '/my-structure',
				siteId: MOCK_SPACE_2.siteId,
			});
		});

		await waitFor(() => {
			expect(StructureService.updateStructure).toBeCalledWith(
				expect.objectContaining({
					spaces: [
						MOCK_SPACE_1.externalReferenceCode,
						MOCK_SPACE_2.externalReferenceCode,
					],
				})
			);
		});
	});
});
