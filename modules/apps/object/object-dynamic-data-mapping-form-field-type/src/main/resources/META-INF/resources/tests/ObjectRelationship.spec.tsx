/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, waitFor} from '@testing-library/react';
import {fetch} from 'frontend-js-web';
import React from 'react';

import ObjectRelationship, {
	getLabel,
} from '../js/ObjectRelationship/ObjectRelationship';

import type {Locale} from 'dynamic-data-mapping-form-field-type';

jest.mock('frontend-js-web', () => {
	const originalModule = jest.requireActual('frontend-js-web');

	return {
		...originalModule,
		fetch: jest.fn(),
	};
});

const API_URL =
	'http://localhost:8080/o/headless-admin-user/v1.0/organizations';

const DEFAULT_PROPS = {
	fieldName: 'relationship',
	inputName: 'relationship',
	name: 'relationship',
	objectDefinitionDefaultLanguageId: 'en_US' as Locale,
	objectEntryId: '0',
	objectFieldBusinessType: 'Text',
	onChange: () => {},
};

describe('fetchData', () => {
	it('appends the query string after the id when fetching a selected value by id', async () => {
		(fetch as jest.Mock)
			.mockResolvedValueOnce({
				json: () => Promise.resolve({items: []}),
			})
			.mockResolvedValueOnce({
				json: () => Promise.resolve({id: 123}),
			});

		render(
			<ObjectRelationship
				{...DEFAULT_PROPS}
				apiURL={`${API_URL}?flatten=true`}
				value="123"
			/>
		);

		await waitFor(() =>
			expect(fetch).toHaveBeenCalledWith(
				`${API_URL}/123?flatten=true`,
				expect.anything()
			)
		);
	});

	it('clears the value when the fetched item matches neither valueKey nor id', async () => {
		const onChange = jest.fn();

		(fetch as jest.Mock)
			.mockResolvedValueOnce({
				json: () => Promise.resolve({items: []}),
			})
			.mockResolvedValueOnce({
				json: () => Promise.resolve({id: 39013, productId: 39013}),
			});

		render(
			<ObjectRelationship
				{...DEFAULT_PROPS}
				apiURL={API_URL}
				onChange={onChange}
				value="39014"
				valueKey="productId"
			/>
		);

		await waitFor(() =>
			expect(onChange).toHaveBeenCalledWith({target: {value: null}})
		);
	});

	it('joins the search term with "&" when the apiURL already has a query string', async () => {
		(fetch as jest.Mock).mockResolvedValue({
			json: () => Promise.resolve({items: []}),
		});

		const apiURL = `${API_URL}?flatten=true`;

		const {getByRole} = render(
			<ObjectRelationship {...DEFAULT_PROPS} apiURL={apiURL} />
		);

		fireEvent.change(getByRole('textbox'), {target: {value: 'Org B'}});

		await waitFor(() =>
			expect(fetch).toHaveBeenCalledWith(
				`${apiURL}&search=Org%20B`,
				expect.anything()
			)
		);
	});

	it('joins the search term with "?" when the apiURL has no query string', async () => {
		(fetch as jest.Mock).mockResolvedValue({
			json: () => Promise.resolve({items: []}),
		});

		const {getByRole} = render(
			<ObjectRelationship {...DEFAULT_PROPS} apiURL={API_URL} />
		);

		fireEvent.change(getByRole('textbox'), {target: {value: 'Org B'}});

		await waitFor(() =>
			expect(fetch).toHaveBeenCalledWith(
				`${API_URL}?search=Org%20B`,
				expect.anything()
			)
		);
	});

	it('keeps the value when the fetched item matches on valueKey instead of id', async () => {
		const onChange = jest.fn();

		(fetch as jest.Mock)
			.mockResolvedValueOnce({
				json: () => Promise.resolve({items: []}),
			})
			.mockResolvedValueOnce({
				json: () => Promise.resolve({id: 39013, productId: 39014}),
			});

		const {container} = render(
			<ObjectRelationship
				{...DEFAULT_PROPS}
				apiURL={API_URL}
				onChange={onChange}
				value="39014"
				valueKey="productId"
			/>
		);

		await waitFor(() =>
			expect(container.querySelector('input[type="hidden"]')).toHaveValue(
				'39014'
			)
		);

		expect(onChange).not.toHaveBeenCalled();
	});
});

describe('getLabel', () => {
	it('returns as a string the same boolean value passed in booleanField', () => {
		let label = getLabel(
			{booleanField: true},
			'booleanField',
			'en_US',
			'Boolean'
		);

		expect(label).toBe('true');

		label = getLabel(
			{booleanField: false},
			'booleanField',
			'en_US',
			'Boolean'
		);

		expect(label).toBe('false');
	});
});
