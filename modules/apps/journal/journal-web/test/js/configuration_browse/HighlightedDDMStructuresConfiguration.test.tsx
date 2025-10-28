/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {openSelectionModal} from 'frontend-js-components-web';
import React from 'react';

import HighlightedDDMStructuresConfiguration, {
	itemSelectorValueToDDMStructure,
	removeDuplicates,
} from '../../../src/main/resources/META-INF/resources/js/configuration_browse/HighlightedDDMStructuresConfiguration';

jest.mock('frontend-js-components-web', () => ({
	openSelectionModal: jest.fn(),
}));

jest.mock('frontend-js-web', () => ({
	sub: jest.fn((langKey, arg) => langKey.replace('x', arg)),
}));

const renderComponent = () => {
	return render(
		<HighlightedDDMStructuresConfiguration
			ddmStructures={[
				{
					ddmStructureId: '1',
					name: 'Structure 1',
					scope: 'My scope',
				},
				{
					ddmStructureId: '2',
					name: 'Structure 2',
					scope: 'My scope',
				},
			]}
			portletNamespace="namespace"
			selectDDMStructureURL="url"
		/>
	);
};

describe('HighlightedDDMStructuresConfiguration', () => {
	afterEach(() => {

		// @ts-ignore

		openSelectionModal.mockReset();
	});

	it('renders a list of structures', () => {
		renderComponent();

		expect(screen.getByText('Structure 1')).toBeInTheDocument();
		expect(screen.getByText('Structure 2')).toBeInTheDocument();
	});

	it('removes a structure when the remove button is pressed', () => {
		renderComponent();

		const select = screen.getByLabelText('remove-Structure 2');

		userEvent.click(select);

		expect(screen.getByText('Structure 1')).toBeInTheDocument();
		expect(screen.queryByText('Structure 2')).not.toBeInTheDocument();
	});

	it('opens a modal when the select button is pressed', () => {
		renderComponent();

		const select = screen.getByLabelText('select-highlighted-structures');
		userEvent.click(select);

		expect(select).toBeInTheDocument();

		expect(openSelectionModal).toHaveBeenCalledWith(
			expect.objectContaining({
				multiple: true,
				title: 'select-structures',
				url: 'url',
			})
		);
	});

	it('converts item selector value to ddm structure', () => {
		renderComponent();

		const itemSelectorValue = {
			value: '{"ddmstructurekey":"1","scope":"My scope","name":"Structure 1","ddmstructureid":"1"}',
		};

		expect(
			itemSelectorValueToDDMStructure(itemSelectorValue)
		).toStrictEqual({
			ddmStructureId: '1',
			name: 'Structure 1',
			scope: 'My scope',
		});
	});

	it('removes the structures duplicated', () => {
		const structures = [
			{
				id: '1',
			},
			{
				id: '1',
			},
			{
				id: '2',
			},
		];

		expect(removeDuplicates(structures, (item) => item.id)).toStrictEqual([
			{
				id: '1',
			},
			{
				id: '2',
			},
		]);
	});
});
