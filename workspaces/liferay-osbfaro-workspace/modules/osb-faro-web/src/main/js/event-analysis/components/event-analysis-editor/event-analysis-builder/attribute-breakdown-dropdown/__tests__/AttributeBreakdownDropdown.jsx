import * as data from 'test/data';
import AttributeBreakdownDropdown from '../index';
import mockStore from 'test/mock-store';
import React from 'react';
import {act, fireEvent, render, waitFor} from '@testing-library/react';
import {DISPLAY_NAME} from 'shared/util/pagination';
import {MockedProvider} from '@apollo/client/testing';
import {mockEventAttributeDefinitionsReq} from 'test/graphql-data';
import {OrderByDirections} from 'shared/util/constants';
import {Provider} from 'react-redux';
import {range} from 'lodash';

describe('AttributeBreakdownDropdown', () => {
	const defaultMocks = [
		mockEventAttributeDefinitionsReq(
			range(10).map(i =>
				data.mockEventAttributeDefinition(i, {
					__typename: 'EventAttributeDefinition'
				})
			),
			{
				keyword: '',
				size: 200,
				sort: {
					column: DISPLAY_NAME,
					type: OrderByDirections.Ascending
				}
			}
		)
	];

	const WrappedComponent = ({mocks = defaultMocks, ...props}) => (
		<Provider store={mockStore()}>
			<MockedProvider addTypename={false} mocks={mocks}>
				<AttributeBreakdownDropdown
					disabledIds={[]}
					onAttributeSelect={jest.fn()}
					trigger={<button data-testid='target'>{'click me'}</button>}
					uneditableIds={[]}
					{...props}
				/>
			</MockedProvider>
		</Provider>
	);

	it('render', async () => {
		const {container, getByTestId} = render(<WrappedComponent />);

		fireEvent.click(getByTestId('target'));

		await waitFor(() =>
			expect(document.body.querySelector('.loading-animation')).toBeNull()
		);

		act(() => {
			jest.advanceTimersByTime(250);
		});

		expect(container).toMatchSnapshot();

		const dropdownMenu = document.body.querySelector(
			'.base-dropdown-menu-root'
		);

		expect(dropdownMenu).toBeTruthy();
		expect(dropdownMenu).toMatchSnapshot();

		expect(
			dropdownMenu.querySelectorAll('.dropdown-item.active')
		).toHaveLength(0);
	});

	it('render w/ disabled attributes', async () => {
		const {getByTestId} = render(
			<WrappedComponent disabledIds={['1', '2']} />
		);

		fireEvent.click(getByTestId('target'));

		await waitFor(() =>
			expect(document.body.querySelector('.loading-animation')).toBeNull()
		);

		act(() => {
			jest.advanceTimersByTime(250);
		});

		expect(
			document.body.querySelectorAll('.dropdown-item.disabled')
		).toHaveLength(2);
	});
});
