/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import FrontendDataSetContext from '../../../src/main/resources/META-INF/resources/FrontendDataSetContext';
import ResultsBar from '../../../src/main/resources/META-INF/resources/management_bar/controls/ResultsBar';

const STATUS_FILTER = {
	active: true,
	enabled: true,
	entityFieldType: 'string',
	id: 'status',
	label: 'Status',
	multiple: false,
	preloadedData: {},
	selectedData: {
		exclude: false,
		selectedItems: [{label: 'Approved', value: 'approved'}],
	},
	selectedItemsLabel: 'Approved',
	type: 'selection',
};

describe('ResultsBar', () => {
	const renderResultsBar = ({
		searchParam = 'liferay',
		showFilters,
	}: {
		searchParam?: string;
		showFilters?: boolean;
	}) =>
		render(
			<FrontendDataSetContext.Provider
				value={
					{
						globalFDSState: {filters: [STATUS_FILTER]},
						onClearResultsBar: () => {},
						searchParam,
						searching: false,
					} as any
				}
			>
				<ResultsBar
					dataLoading={false}
					disabled={false}
					showFilters={showFilters}
					total={1}
				/>
			</FrontendDataSetContext.Provider>
		);

	it('resumes the active filters', () => {
		renderResultsBar({});

		expect(screen.queryByText('Status:')).toBeInTheDocument();
	});

	it('does not resume the filters when showFilters is false', () => {
		renderResultsBar({showFilters: false});

		expect(screen.queryByText('Status:')).not.toBeInTheDocument();
	});

	it('keeps the search resume and its clear action when showFilters is false', () => {
		renderResultsBar({showFilters: false});

		expect(screen.queryByText('liferay')).toBeInTheDocument();
		expect(screen.queryByText('clear')).toBeInTheDocument();
	});

	it('does not render when showFilters is false and nothing else is resumable', () => {
		renderResultsBar({searchParam: '', showFilters: false});

		expect(screen.queryByText('clear')).not.toBeInTheDocument();
	});
});
