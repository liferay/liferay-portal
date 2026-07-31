/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import ContentGapMatrixGrid from '../../js/components/content_gap_matrix/ContentGapMatrixGrid';
import {PARTIAL_COVERAGE_MATRIX} from '../../js/components/content_gap_matrix/services/fixtures';
import {
	MatrixData,
	NO_FUNNEL_STAGE,
	NO_PERSONA,
} from '../../js/components/content_gap_matrix/types';

const FUNNEL_STAGE_FILTER_ID = 'cmpFunnelStageCategoryIds';

const PERSONA_FILTER_ID = 'cmpPersonaCategoryIds';

const AWARENESS_ITEM = {label: 'Awareness', value: '50001'};

const CHAMPION_ITEM = {label: 'Champion', value: '40002'};

const REAL_FUNNEL_STAGE_ITEMS = [
	AWARENESS_ITEM,
	{label: 'Consideration', value: '50002'},
	{label: 'Decision', value: '50003'},
	{label: 'Retention', value: '50004'},
];

const REAL_PERSONA_ITEMS = [
	{label: 'Decision Maker', value: '40001'},
	CHAMPION_ITEM,
	{label: 'Technical Evaluator', value: '40003'},
	{label: 'End User', value: '40004'},
];

const mockSetAssetFDSState = jest.fn();

let mockAssetFDSState: any;

jest.mock('@liferay/frontend-js-state-web/react', () => ({
	useLiferayState: () => [mockAssetFDSState, mockSetAssetFDSState],
}));

function getUpdatedFilter(id: string) {
	const [{filters}] = mockSetAssetFDSState.mock.calls[0];

	return filters.find((filter: {id: string}) => filter.id === id);
}

/**
 * Puts the data set's category filters in the state the given selections would
 * leave them in, alongside the combined category filter the matrix must not
 * touch.
 */
function setFDSState(selectedDataMap: Record<string, unknown> = {}) {
	mockAssetFDSState = {
		filters: [
			...[PERSONA_FILTER_ID, FUNNEL_STAGE_FILTER_ID].map((id) => {
				const selectedData = selectedDataMap[id];

				if (!selectedData) {
					return {active: false, id};
				}

				return {active: true, id, selectedData};
			}),
			{active: true, id: 'taxonomyCategoryIds'},
		],
		search: {query: ''},
	};
}

describe('ContentGapMatrixGrid', () => {
	beforeEach(() => {
		mockSetAssetFDSState.mockClear();

		setFDSState();
	});

	it('applies the persona and funnel stage of a clicked cell to the asset data set', () => {
		render(
			<ContentGapMatrixGrid
				assetFDSId="assetFDSId"
				data={PARTIAL_COVERAGE_MATRIX}
			/>
		);

		fireEvent.click(screen.getByLabelText('Champion, Awareness: 4'));

		expect(getUpdatedFilter(PERSONA_FILTER_ID)).toEqual({
			active: true,
			id: PERSONA_FILTER_ID,
			selectedData: {exclude: false, selectedItems: [CHAMPION_ITEM]},
		});
		expect(getUpdatedFilter(FUNNEL_STAGE_FILTER_ID)).toEqual({
			active: true,
			id: FUNNEL_STAGE_FILTER_ID,
			selectedData: {exclude: false, selectedItems: [AWARENESS_ITEM]},
		});
		expect(getUpdatedFilter('taxonomyCategoryIds')).toEqual({
			active: true,
			id: 'taxonomyCategoryIds',
		});
	});

	it('excludes every real persona when a no persona cell is clicked', () => {
		render(
			<ContentGapMatrixGrid
				assetFDSId="assetFDSId"
				data={PARTIAL_COVERAGE_MATRIX}
			/>
		);

		fireEvent.click(screen.getByLabelText('no-persona, Awareness: 2'));

		expect(getUpdatedFilter(PERSONA_FILTER_ID)).toEqual({
			active: true,
			id: PERSONA_FILTER_ID,
			selectedData: {exclude: true, selectedItems: REAL_PERSONA_ITEMS},
		});
		expect(getUpdatedFilter(FUNNEL_STAGE_FILTER_ID)).toEqual({
			active: true,
			id: FUNNEL_STAGE_FILTER_ID,
			selectedData: {exclude: false, selectedItems: [AWARENESS_ITEM]},
		});
	});

	it('excludes every real term of both axes when the fully uncategorized cell is clicked', () => {
		render(
			<ContentGapMatrixGrid
				assetFDSId="assetFDSId"
				data={PARTIAL_COVERAGE_MATRIX}
			/>
		);

		fireEvent.click(screen.getByLabelText('no-persona, no-funnel: 3'));

		expect(getUpdatedFilter(PERSONA_FILTER_ID)).toEqual({
			active: true,
			id: PERSONA_FILTER_ID,
			selectedData: {exclude: true, selectedItems: REAL_PERSONA_ITEMS},
		});
		expect(getUpdatedFilter(FUNNEL_STAGE_FILTER_ID)).toEqual({
			active: true,
			id: FUNNEL_STAGE_FILTER_ID,
			selectedData: {
				exclude: true,
				selectedItems: REAL_FUNNEL_STAGE_ITEMS,
			},
		});
	});

	it('highlights only the cell the asset data set is filtered by', () => {
		setFDSState({
			[FUNNEL_STAGE_FILTER_ID]: {
				exclude: false,
				selectedItems: [AWARENESS_ITEM],
			},
			[PERSONA_FILTER_ID]: {
				exclude: false,
				selectedItems: [CHAMPION_ITEM],
			},
		});

		const {container} = render(
			<ContentGapMatrixGrid
				assetFDSId="assetFDSId"
				data={PARTIAL_COVERAGE_MATRIX}
			/>
		);

		const selectedCells = container.querySelectorAll(
			'.lfr-cmp__content-gap-cell--selected'
		);

		expect(selectedCells).toHaveLength(1);
		expect(selectedCells[0]).toHaveAttribute(
			'aria-label',
			'Champion, Awareness: 4'
		);
	});

	it('highlights the uncategorized cell when both filters exclude every real term', () => {
		setFDSState({
			[FUNNEL_STAGE_FILTER_ID]: {
				exclude: true,
				selectedItems: REAL_FUNNEL_STAGE_ITEMS,
			},
			[PERSONA_FILTER_ID]: {
				exclude: true,
				selectedItems: REAL_PERSONA_ITEMS,
			},
		});

		const {container} = render(
			<ContentGapMatrixGrid
				assetFDSId="assetFDSId"
				data={PARTIAL_COVERAGE_MATRIX}
			/>
		);

		const selectedCells = container.querySelectorAll(
			'.lfr-cmp__content-gap-cell--selected'
		);

		expect(selectedCells).toHaveLength(1);
		expect(selectedCells[0]).toHaveAttribute(
			'aria-label',
			'no-persona, no-funnel: 3'
		);
	});

	it('highlights no cell when a filter holds several terms', () => {
		setFDSState({
			[FUNNEL_STAGE_FILTER_ID]: {
				exclude: false,
				selectedItems: [AWARENESS_ITEM],
			},
			[PERSONA_FILTER_ID]: {
				exclude: false,
				selectedItems: REAL_PERSONA_ITEMS,
			},
		});

		const {container} = render(
			<ContentGapMatrixGrid
				assetFDSId="assetFDSId"
				data={PARTIAL_COVERAGE_MATRIX}
			/>
		);

		expect(
			container.querySelectorAll('.lfr-cmp__content-gap-cell--selected')
		).toHaveLength(0);
	});

	describe('when the assistant generates content', () => {
		let fireSpy: jest.SpyInstance;
		let onSpy: jest.SpyInstance;

		function getContentChangedHandler() {
			const registrations = onSpy.mock.calls.filter(
				([eventName]) => eventName === 'cms:aiAssistant:contentChanged'
			);

			expect(registrations).toHaveLength(1);

			return registrations[0][1];
		}

		beforeEach(() => {
			fireSpy = jest.spyOn(Liferay, 'fire').mockImplementation(() => {});
			onSpy = jest
				.spyOn(Liferay, 'on')
				.mockImplementation((() => {}) as never);

			fireSpy.mockClear();
			onSpy.mockClear();
		});

		afterEach(() => {
			fireSpy.mockRestore();
			onSpy.mockRestore();
		});

		it('refreshes the asset data set when content is generated', () => {
			render(
				<ContentGapMatrixGrid
					assetFDSId="assetFDSId"
					data={PARTIAL_COVERAGE_MATRIX}
				/>
			);

			const handler = getContentChangedHandler();

			handler();

			expect(fireSpy).toHaveBeenCalledWith('fds-update-display', {
				id: 'assetFDSId',
			});
		});

		it('detaches the listener on unmount', () => {
			const detachSpy = jest
				.spyOn(Liferay, 'detach')
				.mockImplementation((() => {}) as never);

			try {
				const {unmount} = render(
					<ContentGapMatrixGrid
						assetFDSId="assetFDSId"
						data={PARTIAL_COVERAGE_MATRIX}
					/>
				);

				const handler = getContentChangedHandler();

				unmount();

				expect(detachSpy).toHaveBeenCalledWith(
					'cms:aiAssistant:contentChanged',
					handler
				);
			}
			finally {
				detachSpy.mockRestore();
			}
		});
	});

	it('leaves the filter of an axis without real terms inactive', () => {
		const data: MatrixData = {
			cells: [
				{
					funnelStageId: '50001',
					personaId: NO_PERSONA.id,
					totalCount: 6,
				},
			],
			funnelStages: [
				{
					externalReferenceCode: 'STAGE_AWARENESS',
					id: '50001',
					name: 'Awareness',
				},
				NO_FUNNEL_STAGE,
			],
			personas: [NO_PERSONA],
			totalAssetCount: 6,
		};

		render(<ContentGapMatrixGrid assetFDSId="assetFDSId" data={data} />);

		fireEvent.click(screen.getByLabelText('no-persona, Awareness: 6'));

		expect(getUpdatedFilter(PERSONA_FILTER_ID)).toEqual({
			active: false,
			id: PERSONA_FILTER_ID,
			odataFilterString: undefined,
			selectedData: undefined,
		});
		expect(getUpdatedFilter(FUNNEL_STAGE_FILTER_ID)).toEqual({
			active: true,
			id: FUNNEL_STAGE_FILTER_ID,
			selectedData: {exclude: false, selectedItems: [AWARENESS_ITEM]},
		});
	});
});
