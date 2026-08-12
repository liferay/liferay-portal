/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fetch from 'jest-fetch-mock';

import '@testing-library/jest-dom';
import {act, fireEvent, render, screen, within} from '@testing-library/react';
import React from 'react';

import {loadingElement} from '../../../utils/__tests__/helpers';
import {
	fetchPropertiesResponse,
	fetchTableDataResponse,
} from '../../../utils/__tests__/mocks';
import Properties from '../Properties';

describe('Properties', () => {
	afterEach(() => {
		jest.restoreAllMocks();
	});

	it('renders properties table header', async () => {
		fetch.mockResponseOnce(JSON.stringify(fetchPropertiesResponse));

		render(<Properties />);

		await loadingElement();

		expect(screen.getAllByRole('columnheader')).toHaveLength(3);
		expect(
			screen.getByRole('columnheader', {
				name: /available-properties/i,
			})
		).toBeInTheDocument();
		expect(
			screen.getByRole('columnheader', {
				name: /sites/i,
			})
		).toBeInTheDocument();
	});

	it('renders the empty state when there are no properties', async () => {
		fetch.mockResponse(JSON.stringify(fetchTableDataResponse([])));

		render(<Properties />);

		await loadingElement();

		expect(
			screen.getByText(/create-a-property-to-add-sites/i)
		).toBeInTheDocument();

		expect(
			screen.getByRole('button', {name: /^new-property$/i})
		).toBeInTheDocument();
	});

	it('renders properties table content', async () => {
		fetch.mockResponseOnce(JSON.stringify(fetchPropertiesResponse));

		render(<Properties />);

		await loadingElement();

		const first = screen.getByTestId(/Liferay DXP/i);

		expect(within(first).getByText(/Liferay DXP/i)).toBeInTheDocument();
		expect(within(first).getByText(/0/i)).toBeInTheDocument();
		expect(within(first).getByRole('assign-button')).toBeInTheDocument();

		const second = screen.getByTestId(/Beryl Commerce/i);

		expect(within(second).getByText(/Beryl Commerce/i)).toBeInTheDocument();
		expect(within(second).getByText(/5/i)).toBeInTheDocument();
		expect(within(second).getByRole('assign-button')).toBeInTheDocument();
	});

	it('renders filter options', async () => {
		fetch.mockResponseOnce(JSON.stringify(fetchPropertiesResponse));

		render(<Properties />);

		await loadingElement();

		fireEvent.click(
			screen.getByRole('button', {
				name: /filter-and-order/i,
			})
		);

		expect(
			screen.getByRole('menuitem', {
				name: /available-properties/i,
			})
		).toBeInTheDocument();
		expect(
			screen.getByRole('menuitem', {
				name: /create-date/i,
			})
		).toBeInTheDocument();
	});

	it('renders assign modal', async () => {
		fetch.mockResponseOnce(JSON.stringify(fetchPropertiesResponse));

		jest.useFakeTimers();

		render(<Properties />);

		await loadingElement();

		expect(document.body).not.toHaveClass('modal-open');

		fireEvent.click(screen.getAllByRole('assign-button')[0]);

		expect(document.body).toHaveClass('modal-open');
	});

	it('renders modal to create a new property', async () => {
		fetch.mockResponse(JSON.stringify(fetchPropertiesResponse));

		jest.useFakeTimers();

		render(<Properties />);

		await loadingElement();

		const addProperyButton = screen.getByRole('button', {
			name: /create-a-new-property/i,
		});

		expect(addProperyButton).toBeInTheDocument();
		expect(document.body).not.toHaveClass('modal-open');

		await act(async () => {
			await fireEvent.click(addProperyButton);

			jest.advanceTimersByTime(100);
		});

		expect(document.body).toHaveClass('modal-open');
		expect(screen.getByText(/new-property/i)).toBeInTheDocument();
	});
});
