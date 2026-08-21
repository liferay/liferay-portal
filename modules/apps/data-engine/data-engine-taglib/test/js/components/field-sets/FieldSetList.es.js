/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, cleanup, render} from '@testing-library/react';
import {useConfig, useForm, useFormState} from 'data-engine-js-components-web';
import React from 'react';

import FieldSetList from '../../../../src/main/resources/META-INF/resources/js/components/field-sets/FieldSetList';
import {getItems} from '../../../../src/main/resources/META-INF/resources/js/utils/client.es';

jest.mock('data-engine-js-components-web', () => ({
	DRAG_TYPES: {DRAG_FIELDSET_ADD: 'DRAG_FIELDSET_ADD'},
	EVENT_TYPES: {FIELD_SET: {ADD: 'FIELD_SET.ADD'}},
	useConfig: jest.fn(),
	useForm: jest.fn(),
	useFormState: jest.fn(),
}));

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/utils/client.es',
	() => ({
		getItems: jest.fn(),
	})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/components/field-types/FieldType.es',
	() => {
		const React = require('react');

		return {
			__esModule: true,
			default: ({label}) =>
				React.createElement(
					'div',
					{'data-testid': 'field-type'},
					label
				),
		};
	}
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/components/field-sets/FieldSetModal',
	() => ({
		__esModule: true,
		default: () => null,
	})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/components/field-sets/actions/useDeleteFieldSet.es',
	() => ({
		__esModule: true,
		default: () => jest.fn(),
	})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/components/field-sets/actions/usePropagateFieldSet.es',
	() => ({
		__esModule: true,
		default: () => jest.fn(),
	})
);

function makeFieldSet(id, name) {
	return {
		dataDefinitionFields: [],
		dataDefinitionKey: String(id),
		defaultLanguageId: 'en_US',
		id,
		name: {en_US: name},
	};
}

const defaultConfig = {
	contentType: 'journal',
	dataDefinitionId: '1',
	groupId: '20120',
};

const defaultDefinition = {
	dataDefinitionFields: [],
};

describe('FieldSetList — server-side search', () => {
	beforeEach(() => {
		jest.useFakeTimers();

		themeDisplay.getCompanyGroupId = jest.fn(() => defaultConfig.groupId);

		useConfig.mockReturnValue(defaultConfig);
		useForm.mockReturnValue(jest.fn());
		useFormState.mockReturnValue({
			dataDefinition: defaultDefinition,
			fieldSets: [],
		});

		getItems.mockResolvedValue([]);
	});

	afterEach(() => {
		jest.clearAllTimers();
		jest.useRealTimers();
		jest.clearAllMocks();
		cleanup();
	});

	it('shows the empty state when there are no fieldsets and no search term', () => {
		const {queryByText} = render(<FieldSetList searchTerm="" />);

		expect(queryByText('there-are-no-fieldsets')).toBeTruthy();
	});

	it('renders local fieldsets when no search term is active', () => {
		const fieldSets = [makeFieldSet(10, 'Alpha'), makeFieldSet(20, 'Beta')];

		useFormState.mockReturnValue({
			dataDefinition: defaultDefinition,
			fieldSets,
		});

		const {getAllByTestId, queryByText} = render(
			<FieldSetList searchTerm="" />
		);

		expect(getAllByTestId('field-type')).toHaveLength(2);
		expect(queryByText('Alpha')).toBeTruthy();
		expect(queryByText('Beta')).toBeTruthy();
	});

	it('shows a loading indicator during the debounce window before the fetch starts', () => {
		const {container, queryByText} = render(
			<FieldSetList searchTerm="alpha" />
		);

		expect(container.querySelector('.loading-animation')).toBeTruthy();
		expect(queryByText('no-results-found')).toBeFalsy();
		expect(queryByText('there-are-no-fieldsets')).toBeFalsy();
	});

	it('calls getItems with the search term after the debounce delay', async () => {
		render(<FieldSetList searchTerm="alpha" />);

		expect(getItems).not.toHaveBeenCalled();

		await act(async () => {
			jest.advanceTimersByTime(300);
		});

		expect(getItems).toHaveBeenCalledWith(
			expect.stringContaining('journal'),
			'alpha',
			expect.objectContaining({signal: expect.any(AbortSignal)})
		);
	});

	it('displays search results returned by the server', async () => {
		const results = [makeFieldSet(10, 'Alpha'), makeFieldSet(20, 'Beta')];

		getItems.mockResolvedValue(results);

		const {getAllByTestId, queryByText} = render(
			<FieldSetList searchTerm="alpha" />
		);

		await act(async () => {
			jest.advanceTimersByTime(300);
		});

		expect(getAllByTestId('field-type')).toHaveLength(2);
		expect(queryByText('Alpha')).toBeTruthy();
		expect(queryByText('Beta')).toBeTruthy();
	});

	it('shows the "no results found" empty state when the server returns no matches', async () => {
		getItems.mockResolvedValue([]);

		const {queryByText} = render(<FieldSetList searchTerm="zzz" />);

		await act(async () => {
			jest.advanceTimersByTime(300);
		});

		expect(queryByText('no-results-found')).toBeTruthy();
	});

	it('restores the local fieldset list when the search term is cleared', async () => {
		const fieldSets = [makeFieldSet(10, 'Alpha')];

		useFormState.mockReturnValue({
			dataDefinition: defaultDefinition,
			fieldSets,
		});

		getItems.mockResolvedValue([makeFieldSet(20, 'ServerResult')]);

		const {getAllByTestId, queryByText, rerender} = render(
			<FieldSetList searchTerm="alpha" />
		);

		await act(async () => {
			jest.advanceTimersByTime(300);
		});

		expect(queryByText('ServerResult')).toBeTruthy();

		await act(async () => {
			rerender(<FieldSetList searchTerm="" />);
		});

		expect(getAllByTestId('field-type')).toHaveLength(1);
		expect(queryByText('Alpha')).toBeTruthy();
		expect(queryByText('ServerResult')).toBeFalsy();
	});

	it('fetches both site and global fieldsets when groupId differs from companyGroupId', async () => {
		useConfig.mockReturnValue({
			...defaultConfig,
			groupId: '99999',
		});

		const siteResult = makeFieldSet(10, 'Site');
		const globalResult = makeFieldSet(20, 'Global');

		getItems
			.mockResolvedValueOnce([siteResult])
			.mockResolvedValueOnce([globalResult]);

		const {queryByText} = render(<FieldSetList searchTerm="test" />);

		await act(async () => {
			jest.advanceTimersByTime(300);
		});

		expect(getItems).toHaveBeenCalledTimes(2);
		expect(queryByText('Site')).toBeTruthy();
		expect(queryByText('Global')).toBeTruthy();
	});

	it('excludes the current data definition from search results', async () => {
		const self = makeFieldSet(1, 'Self');
		const other = makeFieldSet(2, 'Other');

		getItems.mockResolvedValue([self, other]);

		const {queryByText} = render(<FieldSetList searchTerm="x" />);

		await act(async () => {
			jest.advanceTimersByTime(300);
		});

		expect(queryByText('Self')).toBeFalsy();
		expect(queryByText('Other')).toBeTruthy();
	});

	it('shows the "no results found" empty state when the fetch fails', async () => {
		getItems.mockRejectedValue(new Error('network error'));

		const {queryByText} = render(<FieldSetList searchTerm="alpha" />);

		await act(async () => {
			jest.advanceTimersByTime(300);
		});

		expect(queryByText('no-results-found')).toBeTruthy();
	});

	it('fires only one request when the search term changes before the debounce fires', async () => {
		const {rerender} = render(<FieldSetList searchTerm="a" />);

		rerender(<FieldSetList searchTerm="ab" />);
		rerender(<FieldSetList searchTerm="abc" />);

		await act(async () => {
			jest.advanceTimersByTime(300);
		});

		expect(getItems).toHaveBeenCalledTimes(1);
		expect(getItems).toHaveBeenCalledWith(
			expect.any(String),
			'abc',
			expect.any(Object)
		);
	});
});
