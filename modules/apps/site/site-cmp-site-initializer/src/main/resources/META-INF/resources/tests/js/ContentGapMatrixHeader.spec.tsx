/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import ContentGapMatrixHeader from '../../js/components/content_gap_matrix/ContentGapMatrixHeader';
import {
	EMPTY_MATRIX,
	FULL_COVERAGE_MATRIX,
	PARTIAL_COVERAGE_MATRIX,
} from '../../js/components/content_gap_matrix/services/fixtures';

describe('ContentGapMatrixHeader', () => {
	it('colors the coverage badge secondary at partial coverage', () => {
		const {container} = render(
			<ContentGapMatrixHeader data={PARTIAL_COVERAGE_MATRIX} />
		);

		const label = container.querySelector(
			'.lfr-cmp__content-gap-matrix-header-stats .label'
		);

		expect(label).toHaveClass('label-inverse-secondary');
	});

	it('colors the coverage badge success at full coverage', () => {
		const {container} = render(
			<ContentGapMatrixHeader data={FULL_COVERAGE_MATRIX} />
		);

		const label = container.querySelector(
			'.lfr-cmp__content-gap-matrix-header-stats .label'
		);

		expect(label).toHaveClass('label-inverse-success');
	});

	it('colors the coverage badge warning at zero coverage', () => {
		const {container} = render(
			<ContentGapMatrixHeader data={EMPTY_MATRIX} />
		);

		const label = container.querySelector(
			'.lfr-cmp__content-gap-matrix-header-stats .label'
		);

		expect(label).toHaveClass('label-inverse-warning');
	});

	it('replaces the critical gaps count with "No Assets Found" when the project has no assets', () => {
		const {getByText, queryByText} = render(
			<ContentGapMatrixHeader data={EMPTY_MATRIX} />
		);

		expect(getByText('no-assets-found')).toBeInTheDocument();
		expect(
			queryByText('x-critical-gaps', {exact: false})
		).not.toBeInTheDocument();
		expect(getByText('x-covered')).toBeInTheDocument();
	});

	it('shows only the title (no badges) when no data is provided, as in the unconfigured project state', () => {
		const {getByText, queryByText} = render(<ContentGapMatrixHeader />);

		expect(getByText('content-coverage-matrix')).toBeInTheDocument();
		expect(
			queryByText('x-covered', {exact: false})
		).not.toBeInTheDocument();
		expect(
			queryByText('x-critical-gaps', {exact: false})
		).not.toBeInTheDocument();
	});

	it('shows the critical gaps count when the project has assets', () => {
		const {getByText, queryByText} = render(
			<ContentGapMatrixHeader data={PARTIAL_COVERAGE_MATRIX} />
		);

		expect(
			getByText('x-critical-gaps', {exact: false})
		).toBeInTheDocument();
		expect(queryByText('no-assets-found')).not.toBeInTheDocument();
	});
});
