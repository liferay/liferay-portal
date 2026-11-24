import * as API from 'shared/api';
import AccountInput from '../AccountInput';
import React from 'react';
import {cleanup, fireEvent, render, waitFor} from '@testing-library/react';
import {
	focusAutocompleteInput,
	mockListGeometry,
	mockPaginatedFieldValues,
	scrollListToBottom,
	waitForListOptions
} from 'test/infinite-scroll';
import {fromJS} from 'immutable';
import {Property} from 'shared/util/records';
import {PropertyTypes, RelationalOperators} from '../../utils/constants';

const {EQ} = RelationalOperators;

describe('AccountInput', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container, getAllByText, getByText} = render(
			<AccountInput
				property={new Property()}
				value={fromJS({criterionGroup: {items: [{operatorName: EQ}]}})}
			/>
		);

		fireEvent.click(getByText('is'));

		expect(getAllByText('is')[1]).toBeTruthy();
		expect(getByText('is not')).toBeTruthy();
		expect(getByText('contains')).toBeTruthy();
		expect(getByText('does not contain')).toBeTruthy();
		expect(getByText('is known')).toBeTruthy();
		expect(getByText('is unknown')).toBeTruthy();
		expect(container).toMatchSnapshot();
	});

	it('should render a CustomNumberInput', () => {
		const {queryByText} = render(
			<AccountInput
				property={new Property({type: PropertyTypes.AccountNumber})}
				value={fromJS({criterionGroup: {items: [{operatorName: EQ}]}})}
			/>
		);

		expect(queryByText('is equal to')).not.toBeNull();
	});

	it('loads the next page of values when the list is scrolled to the bottom', async () => {
		API.accounts.fetchFieldValues.mockImplementationOnce(
			mockPaginatedFieldValues()
		);
		API.accounts.fetchFieldValues.mockImplementationOnce(
			mockPaginatedFieldValues()
		);

		const restoreListGeometry = mockListGeometry();

		render(
			<AccountInput
				channelId='123'
				groupId='456'
				property={new Property({id: 'industry'})}
				value={fromJS({
					criterionGroup: {items: [{operatorName: EQ, value: ''}]}
				})}
			/>
		);

		focusAutocompleteInput();

		await waitForListOptions();

		scrollListToBottom();

		await waitFor(() =>
			expect(API.accounts.fetchFieldValues).toHaveBeenLastCalledWith({
				channelId: '123',
				delta: 20,
				fieldMappingFieldName: 'industry',
				groupId: '456',
				page: 2,
				query: ''
			})
		);

		restoreListGeometry();
	});
});
