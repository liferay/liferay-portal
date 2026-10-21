/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import FilterResultsStatus from '../../src/main/resources/META-INF/resources/js/components/FilterResultsStatus';

describe('FilterResultsStatus', () => {
	it('announces nothing and stays out of sight without a query', () => {
		render(<FilterResultsStatus matchCount={0} query="" />);

		expect(screen.getByRole('status')).toBeEmptyDOMElement();
		expect(screen.getByRole('status')).toHaveClass('sr-only');
	});

	it('announces the number of matches out of sight', () => {
		render(<FilterResultsStatus matchCount={3} query="name" />);

		expect(screen.getByRole('status')).toHaveTextContent('3-results-found');
		expect(screen.getByRole('status')).toHaveClass('sr-only');
	});

	it('announces a single match in the singular', () => {
		render(<FilterResultsStatus matchCount={1} query="name" />);

		expect(screen.getByRole('status')).toHaveTextContent('1-result-found');
	});

	it('shows that nothing matches', () => {
		render(<FilterResultsStatus matchCount={0} query="zzz" />);

		expect(screen.getByRole('status')).toHaveTextContent(
			'no-results-found'
		);
		expect(screen.getByRole('status')).toHaveClass('text-secondary');
		expect(screen.getByRole('status')).not.toHaveClass('sr-only');
	});
});
