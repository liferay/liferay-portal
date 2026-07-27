/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import ContentGapCell from '../../js/components/content_gap_matrix/ContentGapCell';
import {
	NO_PERSONA,
	TaxonomyTerm,
} from '../../js/components/content_gap_matrix/types';

const PERSONA: TaxonomyTerm = {
	externalReferenceCode: 'P',
	id: '1',
	name: 'Champion',
};

const STAGE: TaxonomyTerm = {
	externalReferenceCode: 'S',
	id: '2',
	name: 'Awareness',
};

describe('ContentGapCell', () => {
	afterEach(() => {
		Liferay.FeatureFlags['LPD-62272'] = false;
	});

	it('applies the persona and funnel-stage filter when a cell is clicked', () => {
		const onFilter = jest.fn();

		const {getByLabelText} = render(
			<ContentGapCell
				funnelStage={STAGE}
				maxRealCount={40}
				onFilter={onFilter}
				persona={PERSONA}
				totalCount={24}
			/>
		);

		fireEvent.click(getByLabelText('Champion, Awareness: 24'));

		expect(onFilter).toHaveBeenCalledWith(PERSONA, STAGE);
	});

	it('applies the persona and funnel-stage filter when a sentinel cell is clicked', () => {
		const onFilter = jest.fn();

		const {getByLabelText} = render(
			<ContentGapCell
				funnelStage={STAGE}
				maxRealCount={40}
				onFilter={onFilter}
				persona={NO_PERSONA}
				totalCount={24}
			/>
		);

		fireEvent.click(getByLabelText('no-persona, Awareness: 24'));

		expect(onFilter).toHaveBeenCalledWith(NO_PERSONA, STAGE);
	});

	it('applies the persona and funnel-stage filter when Enter is pressed on a cell', () => {
		const onFilter = jest.fn();

		const {getByLabelText} = render(
			<ContentGapCell
				funnelStage={STAGE}
				maxRealCount={40}
				onFilter={onFilter}
				persona={PERSONA}
				totalCount={24}
			/>
		);

		fireEvent.keyDown(getByLabelText('Champion, Awareness: 24'), {
			key: 'Enter',
		});

		expect(onFilter).toHaveBeenCalledWith(PERSONA, STAGE);
	});

	it('applies the persona and funnel-stage filter when the filter action is clicked', () => {
		const onFilter = jest.fn();

		render(
			<ContentGapCell
				funnelStage={STAGE}
				maxRealCount={40}
				onFilter={onFilter}
				persona={PERSONA}
				totalCount={24}
			/>
		);

		fireEvent.click(screen.getByText('filter'));

		expect(onFilter).toHaveBeenCalledTimes(1);
		expect(onFilter).toHaveBeenCalledWith(PERSONA, STAGE);
	});

	it('colors a real cell by its tier', () => {
		const {container} = render(
			<ContentGapCell
				funnelStage={STAGE}
				maxRealCount={40}
				persona={PERSONA}
				totalCount={24}
			/>
		);

		const cell = container.querySelector('.lfr-cmp__content-gap-cell');

		expect(cell?.className).toContain('lfr-cmp__content-gap-cell--tier-');
	});

	it('colors a sentinel cell by its count instead of graying it out', () => {
		const {container} = render(
			<ContentGapCell
				funnelStage={STAGE}
				maxRealCount={40}
				persona={NO_PERSONA}
				totalCount={24}
			/>
		);

		const cell = container.querySelector('.lfr-cmp__content-gap-cell');

		expect(cell?.className).toContain('lfr-cmp__content-gap-cell--tier-');
		expect(cell?.className).not.toContain('--gap');
		expect(cell?.className).not.toContain('--sentinel');
	});

	it('generates content for the persona and funnel stage when generate is clicked', () => {
		Liferay.FeatureFlags['LPD-62272'] = true;

		const onGenerate = jest.fn();

		render(
			<ContentGapCell
				funnelStage={STAGE}
				maxRealCount={40}
				onFilter={jest.fn()}
				onGenerate={onGenerate}
				persona={PERSONA}
				totalCount={24}
			/>
		);

		fireEvent.click(screen.getByText('generate'));

		expect(onGenerate).toHaveBeenCalledWith(PERSONA, STAGE);
	});

	it('hides the generate action on a sentinel cell', () => {
		Liferay.FeatureFlags['LPD-62272'] = true;

		render(
			<ContentGapCell
				funnelStage={STAGE}
				maxRealCount={40}
				onFilter={jest.fn()}
				onGenerate={jest.fn()}
				persona={NO_PERSONA}
				totalCount={24}
			/>
		);

		expect(screen.getByText('filter')).toBeInTheDocument();
		expect(screen.queryByText('generate')).not.toBeInTheDocument();
	});

	it('highlights the cell whose filter is applied', () => {
		const {container} = render(
			<ContentGapCell
				funnelStage={STAGE}
				maxRealCount={40}
				persona={PERSONA}
				selected
				totalCount={24}
			/>
		);

		expect(
			container.querySelector('.lfr-cmp__content-gap-cell--selected')
		).not.toBeNull();
	});

	it('marks a zero cell as a gap with no tier fill', () => {
		const {container} = render(
			<ContentGapCell
				funnelStage={STAGE}
				maxRealCount={40}
				persona={PERSONA}
				totalCount={0}
			/>
		);

		const cell = container.querySelector('.lfr-cmp__content-gap-cell');

		expect(cell?.className).toContain('lfr-cmp__content-gap-cell--gap');
		expect(cell?.className).not.toContain('--tier-');
	});

	it('shows the cell actions without a click', () => {
		Liferay.FeatureFlags['LPD-62272'] = true;

		render(
			<ContentGapCell
				funnelStage={STAGE}
				maxRealCount={40}
				onFilter={jest.fn()}
				onGenerate={jest.fn()}
				persona={PERSONA}
				totalCount={24}
			/>
		);

		expect(screen.getByText('filter')).toBeInTheDocument();
		expect(screen.getByText('generate')).toBeInTheDocument();
	});
});
