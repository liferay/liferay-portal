/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, fireEvent, render, waitFor} from '@testing-library/react';
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

const SCOPED_API_URL = 'http://localhost:8080/o/c/objs/scopes/1';

describe('fetchData', () => {
	it('clears the value when nothing resolves the selected option', async () => {
		const onChange = jest.fn();

		(fetch as jest.Mock).mockResolvedValueOnce({
			json: () => Promise.resolve({items: []}),
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

	it('ignores the response of a request the search term has superseded', async () => {
		let resolveInitialFetch: (response: unknown) => void = () => {};

		(fetch as jest.Mock)
			.mockReset()
			.mockReturnValueOnce(
				new Promise((resolve) => {
					resolveInitialFetch = resolve;
				})
			)
			.mockResolvedValueOnce({
				json: () => Promise.resolve({items: [{id: 2, label: 'Org B'}]}),
			});

		const {getAllByRole, getByRole} = render(
			<ObjectRelationship {...DEFAULT_PROPS} apiURL={API_URL} />
		);

		fireEvent.focus(getByRole('textbox'));
		fireEvent.change(getByRole('textbox'), {target: {value: 'Org B'}});

		await waitFor(() =>
			expect(getByRole('menu')).toHaveTextContent('Org B')
		);

		await act(async () => {
			resolveInitialFetch({
				json: () =>
					Promise.resolve({
						items: [
							{id: 1, label: 'Org A'},
							{id: 2, label: 'Org B'},
						],
					}),
			});

			await new Promise((resolve) => setTimeout(resolve, 0));
		});

		expect(getAllByRole('menuitem')).toHaveLength(1);
		expect(getByRole('menu')).not.toHaveTextContent('Org A');
		expect(fetch).toHaveBeenCalledTimes(2);
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

	it('keeps the value when a list item matches on valueKey instead of id', async () => {
		const onChange = jest.fn();

		(fetch as jest.Mock).mockResolvedValueOnce({
			json: () =>
				Promise.resolve({items: [{id: 39013, productId: 39014}]}),
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

	it('keeps the value when the selected option label resolves it outside the first page', async () => {
		const onChange = jest.fn();

		(fetch as jest.Mock).mockResolvedValue({
			json: () => Promise.resolve({items: []}),
		});

		const {container, getByRole} = render(
			<ObjectRelationship
				{...DEFAULT_PROPS}
				apiURL={SCOPED_API_URL}
				onChange={onChange}
				selectedOptionLabel="Org A"
				value="1"
			/>
		);

		await waitFor(() => expect(getByRole('textbox')).toHaveValue('Org A'));

		expect(container.querySelector('input[type="hidden"]')).toHaveValue(
			'1'
		);
		expect(onChange).not.toHaveBeenCalled();
	});

	it('replaces the selected option label when an option is chosen', async () => {
		const onChange = jest.fn();

		(fetch as jest.Mock).mockResolvedValue({
			json: () => Promise.resolve({items: [{id: 2, label: 'Org B'}]}),
		});

		const {container, getByRole} = render(
			<ObjectRelationship
				{...DEFAULT_PROPS}
				apiURL={API_URL}
				onChange={onChange}
				selectedOptionLabel="Org A"
				value="1"
			/>
		);

		await waitFor(() => expect(getByRole('textbox')).toHaveValue('Org A'));

		fireEvent.focus(getByRole('textbox'));
		fireEvent.click(getByRole('menuitem'));

		expect(getByRole('textbox')).toHaveValue('Org B');
		expect(container.querySelector('input[type="hidden"]')).toHaveValue(
			'2'
		);
		expect(onChange).toHaveBeenCalledWith({target: {value: '2'}});
	});

	it('replaces the selected option label with the typed search term', async () => {
		const onChange = jest.fn();

		(fetch as jest.Mock).mockResolvedValue({
			json: () => Promise.resolve({items: []}),
		});

		const {getByRole} = render(
			<ObjectRelationship
				{...DEFAULT_PROPS}
				apiURL={API_URL}
				onChange={onChange}
				selectedOptionLabel="Org A"
				value="1"
			/>
		);

		await waitFor(() => expect(getByRole('textbox')).toHaveValue('Org A'));

		fireEvent.change(getByRole('textbox'), {target: {value: 'Org'}});

		expect(getByRole('textbox')).toHaveValue('Org');
		expect(onChange).toHaveBeenCalledWith({target: {value: null}});
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
