/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import StructureService from '../../../../src/main/resources/META-INF/resources/js/services/StructureService';
import StructureBuilderManagementBar from '../../../../src/main/resources/META-INF/resources/js/structure_builder/components/StructureBuilderManagementBar';
import {State} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/contexts/StateContext';
import {Field} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/field';
import getUuid from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/getUuid';
import {MockStateProvider} from '../mocks/MockStateProvider';

jest.mock('@liferay/layout-js-components-web', () => {
	const actual = jest.requireActual('@liferay/layout-js-components-web');

	return {
		...actual,
		openConfirmModal: jest.fn(),
	};
});

type Props = {
	state?: Partial<State>;
};

const DEFAULT_FIELDS = new Map([[getUuid(), {} as Field]]);

const renderComponent = ({state}: Props = {}) => {
	return render(
		<MockStateProvider
			state={{...state, fields: DEFAULT_FIELDS, spaces: 'all'}}
		>
			<StructureBuilderManagementBar />
		</MockStateProvider>
	);
};

describe('StructureBuilderManagementBar', () => {
	beforeAll(() => {
		StructureService.createStructure = jest.fn().mockResolvedValue({id: 1});
		StructureService.updateStructure = jest.fn();
		StructureService.publishStructure = jest.fn();
	});

	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('Save button is not shown if structure is published', async () => {
		renderComponent({state: {status: 'published'}});

		const saveButton = screen.queryByText('save');

		expect(saveButton).not.toBeInTheDocument();
	});

	it('Save button calls correct endpoint when status is new', async () => {
		renderComponent({state: {status: 'new'}});

		const saveButton = screen.getByText('save');

		await userEvent.click(saveButton);

		await waitFor(() => {
			expect(StructureService.createStructure).toBeCalled();
		});

		expect(StructureService.updateStructure).not.toBeCalled();
	});

	it('Save button calls correct endpoint when status is draft', async () => {
		renderComponent({state: {status: 'draft'}});

		const saveButton = screen.getByText('save');

		await userEvent.click(saveButton);

		await waitFor(() => {
			expect(StructureService.updateStructure).toBeCalled();
		});

		expect(StructureService.createStructure).not.toBeCalled();
	});

	it('Publish button calls correct endpoint when status is new', async () => {
		renderComponent({state: {status: 'new'}});

		const publishButton = screen.getByText('publish');

		await userEvent.click(publishButton);

		await waitFor(() => {
			expect(StructureService.createStructure).toBeCalled();
			expect(StructureService.publishStructure).toBeCalled();
		});

		expect(StructureService.updateStructure).not.toBeCalled();
	});

	it('Publish button calls correct endpoint when status is draft', async () => {
		renderComponent({state: {status: 'draft'}});

		const publishButton = screen.getByText('publish');

		await userEvent.click(publishButton);

		await waitFor(() => {
			expect(StructureService.updateStructure).toBeCalled();
			expect(StructureService.publishStructure).toBeCalled();
		});

		expect(StructureService.createStructure).not.toBeCalled();
	});

	it('Publish button calls correct endpoint when status is published', async () => {
		renderComponent({state: {status: 'published'}});

		const publishButton = screen.getByText('publish');

		await userEvent.click(publishButton);

		await waitFor(() => {
			expect(StructureService.updateStructure).toBeCalled();
		});

		expect(StructureService.publishStructure).not.toBeCalled();
		expect(StructureService.createStructure).not.toBeCalled();
	});

	it('Shows warning modal when a published field has been deleted', async () => {
		renderComponent({
			state: {history: {deletedFields: true}, status: 'published'},
		});

		const publishButton = screen.getByText('publish');

		await userEvent.click(publishButton);

		await waitFor(() => {
			expect(
				require('@liferay/layout-js-components-web').openConfirmModal
			).toBeCalledWith(
				expect.objectContaining({
					text: 'you-removed-one-or-more-fields-from-the-structure',
				})
			);
		});

		expect(StructureService.createStructure).not.toBeCalled();
		expect(StructureService.updateStructure).not.toBeCalled();
		expect(StructureService.publishStructure).not.toBeCalled();
	});
});
