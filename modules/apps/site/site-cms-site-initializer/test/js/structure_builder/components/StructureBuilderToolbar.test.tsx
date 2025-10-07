/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen, waitFor, within} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import StructureBuilderToolbar from '../../../../src/main/resources/META-INF/resources/js/structure_builder/components/StructureBuilderToolbar';
import StructureService from '../../../../src/main/resources/META-INF/resources/js/structure_builder/services/StructureService';
import {Structure} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/types/Structure';
import {Field} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/field';
import getUuid from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/getUuid';
import {addParams, mockNavigate} from '../../__mocks__/frontend-js-web';
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

const renderComponent = (state: MockState) => {
	const structure: Partial<Structure> = {
		children: DEFAULT_CHILDREN,
		erc: 'structure-erc',
		spaces: 'all',
		...state.structure,
	};

	return render(
		<MockStateProvider state={{...state, structure}}>
			<StructureBuilderToolbar />
		</MockStateProvider>
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
			history: {deletedChildren: true},
			structure: {status: 'published'},
		});

		const publishButton = screen.getByText('publish');

		await userEvent.click(publishButton);

		await waitFor(() => {
			expect(
				require('@liferay/layout-js-components-web').openConfirmModal
			).toBeCalledWith(
				expect.objectContaining({
					text: 'you-removed-one-or-more-fields-from-the-content-structure',
				})
			);
		});

		expect(StructureService.createStructure).not.toBeCalled();
		expect(StructureService.updateStructure).not.toBeCalled();
	});

	it('Shows modal to publish when trying to customize experience and the structure is not published', async () => {
		renderComponent({
			structure: {status: 'new'},
		});

		const managementBar: HTMLElement | null =
			document.querySelector('.component-tbar')!;

		const customizeExperienceButton = within(managementBar).getByText(
			'customize-experience'
		);

		await userEvent.click(customizeExperienceButton);

		await waitFor(() => {
			expect(
				require('@liferay/layout-js-components-web').openConfirmModal
			).toBeCalledWith(
				expect.objectContaining({
					text: 'to-customize-the-experience-you-need-to-publish-the-content-structure-first',
				})
			);
		});
	});

	it('Shows modal to publish when trying to customize experience and the structure is published and there are changes', async () => {
		renderComponent({
			structure: {status: 'published'},
			unsavedChanges: true,
		});

		const managementBar: HTMLElement | null =
			document.querySelector('.component-tbar')!;

		const customizeExperienceButton = within(managementBar).getByText(
			'customize-experience'
		);

		await userEvent.click(customizeExperienceButton);

		await waitFor(() => {
			expect(
				require('@liferay/layout-js-components-web').openConfirmModal
			).toBeCalledWith(
				expect.objectContaining({
					text: 'to-customize-the-experience-you-need-to-publish-the-content-structure-first',
				})
			);
		});
	});

	it('Shows modal to publish when trying to customize experience and the structure is published and some fields have been deleted', async () => {
		renderComponent({
			history: {deletedChildren: true},
			structure: {status: 'published'},
		});

		const managementBar: HTMLElement | null =
			document.querySelector('.component-tbar')!;

		const customizeExperienceButton = within(managementBar).getByText(
			'customize-experience'
		);

		await userEvent.click(customizeExperienceButton);

		await waitFor(() => {
			expect(
				require('@liferay/layout-js-components-web').openConfirmModal
			).toBeCalledWith(
				expect.objectContaining({
					text: 'to-customize-the-experience-you-need-to-publish-the-content-structure-first.-you-removed-one-or-more-fields-from-the-content-structure',
				})
			);
		});
	});

	it('Navigates to customize experience if the structure is published', async () => {
		const expectedUrl =
			'http://localhost:8080/edit?backURL=http%3A%2F%2Flocalhost%3A8080%2Fstructure-builder%3FobjectDefinitionExternalReferenceCode%3Dstructure-erc&objectDefinitionExternalReferenceCode=structure-erc';

		addParams.mockReturnValue(expectedUrl);

		renderComponent({
			structure: {status: 'published'},
		});

		const managementBar: HTMLElement | null =
			document.querySelector('.component-tbar')!;

		const customizeExperienceButton = within(managementBar).getByText(
			'customize-experience'
		);

		await userEvent.click(customizeExperienceButton);

		await waitFor(() => {
			expect(mockNavigate).toBeCalledWith(
				expect.stringContaining(expectedUrl)
			);
		});
	});
});
