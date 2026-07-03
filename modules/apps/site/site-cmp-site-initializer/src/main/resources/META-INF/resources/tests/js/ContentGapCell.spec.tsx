/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
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
});
