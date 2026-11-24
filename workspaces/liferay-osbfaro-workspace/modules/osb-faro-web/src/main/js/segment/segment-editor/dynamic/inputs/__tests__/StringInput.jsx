import * as API from 'shared/api';
import React from 'react';
import StringInput from '../StringInput';
import {cleanup, render, waitFor} from '@testing-library/react';
import {
	focusAutocompleteInput,
	mockListGeometry,
	mockPaginatedFieldValues,
	scrollListToBottom,
	waitForListOptions
} from 'test/infinite-scroll';
import {Property} from 'shared/util/records';

describe('StringInput', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(
			<StringInput
				operatorRenderer={() => <div>{'operator'}</div>}
				property={new Property()}
				touched={false}
				valid={false}
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render with data', () => {
		const {container} = render(
			<StringInput
				displayValue='Name'
				operatorRenderer={() => <div>{'operator'}</div>}
				property={new Property()}
				touched={false}
				valid
				value='Test Test'
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render w/o value input when value is null', () => {
		const {queryByTestId} = render(
			<StringInput
				displayValue='Name'
				operatorRenderer={() => <div>{'operator'}</div>}
				property={new Property()}
				touched={false}
				valid
				value={null}
			/>
		);

		expect(queryByTestId('value-input')).toBeNull();
	});

	it('should render w/ has-error when touched and not valid', () => {
		const {container} = render(
			<StringInput
				displayValue='Name'
				operatorRenderer={() => <div>{'operator'}</div>}
				property={new Property()}
				touched
				valid={false}
				value=''
			/>
		);

		expect(container.querySelector('.has-error')).toBeTruthy();
	});

	it('loads the next page of values when the list is scrolled to the bottom', async () => {
		API.individuals.fetchFieldValues.mockImplementationOnce(
			mockPaginatedFieldValues()
		);
		API.individuals.fetchFieldValues.mockImplementationOnce(
			mockPaginatedFieldValues()
		);

		const restoreListGeometry = mockListGeometry();

		render(
			<StringInput
				channelId='123'
				groupId='456'
				operatorRenderer={() => <div>{'operator'}</div>}
				property={new Property({id: 'jobTitle'})}
				touched={false}
				valid
				value=''
			/>
		);

		focusAutocompleteInput();

		await waitForListOptions();

		scrollListToBottom();

		await waitFor(() =>
			expect(API.individuals.fetchFieldValues).toHaveBeenLastCalledWith({
				channelId: '123',
				delta: 20,
				fieldMappingFieldName: 'jobTitle',
				groupId: '456',
				page: 2,
				query: ''
			})
		);

		restoreListGeometry();
	});
});
