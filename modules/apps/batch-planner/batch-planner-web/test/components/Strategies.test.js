/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen, waitFor, within} from '@testing-library/react';
import React from 'react';

import Strategies from '../../src/main/resources/META-INF/resources/js/components/Strategies';
import {SCHEMA_SELECTED_EVENT} from '../../src/main/resources/META-INF/resources/js/constants';

const PORTLET_NAMESPACE = 'test';

function getOptionLabels(selectLabel) {
	return within(screen.getByLabelText(selectLabel))
		.queryAllByRole('option')
		.map((option) => option.textContent);
}

function renderStrategies(items) {
	fetch.mockResponse(JSON.stringify({items}));

	render(<Strategies portletNamespace={PORTLET_NAMESPACE} />);

	Liferay.fire(SCHEMA_SELECTED_EVENT, {
		schemaName: 'com.liferay.test.Entity',
	});
}

describe('Strategies', () => {
	it('renders the create and update strategies in their own selects', async () => {
		renderStrategies([
			{name: 'UPSERT', type: 'create'},
			{name: 'INSERT', type: 'create'},
			{name: 'UPDATE', type: 'update'},
			{name: 'PARTIAL_UPDATE', type: 'update'},
		]);

		await waitFor(() => {
			expect(
				screen.getByLabelText('import-strategy')
			).toBeInTheDocument();
		});

		expect(getOptionLabels('import-strategy')).toEqual([
			'only-add-new-records',
			'add-or-update-records',
		]);
		expect(getOptionLabels('update-strategy')).toEqual([
			'update-changed-record-fields',
			'overwrite-records',
		]);
	});

	it('renders an empty update strategy select when there are only create strategies', async () => {
		renderStrategies([{name: 'INSERT', type: 'create'}]);

		await waitFor(() => {
			expect(
				screen.getByLabelText('import-strategy')
			).toBeInTheDocument();
		});

		expect(getOptionLabels('import-strategy')).toEqual([
			'only-add-new-records',
		]);
		expect(getOptionLabels('update-strategy')).toEqual([]);
	});

	it('renders nothing when there are no strategies', async () => {
		renderStrategies([]);

		await waitFor(() => {
			expect(fetch).toHaveBeenCalledTimes(1);
		});

		expect(
			screen.queryByLabelText('import-strategy')
		).not.toBeInTheDocument();
	});

	it('clears the strategies when the schema is deselected', async () => {
		renderStrategies([{name: 'INSERT', type: 'create'}]);

		await waitFor(() => {
			expect(
				screen.getByLabelText('import-strategy')
			).toBeInTheDocument();
		});

		Liferay.fire(SCHEMA_SELECTED_EVENT, {});

		await waitFor(() => {
			expect(
				screen.queryByLabelText('import-strategy')
			).not.toBeInTheDocument();
		});
	});
});
