import * as API from 'shared/api';
import * as data from 'test/data';
import client from 'shared/apollo/client';
import GeolocationInput from '../GeolocationInput';
import React from 'react';
import {ApolloProvider} from '@apollo/client';
import {
	cleanup,
	fireEvent,
	render,
	screen,
	waitFor
} from '@testing-library/react';
import {createCustomValueMap} from '../../utils/custom-inputs';
import {
	mockListGeometry,
	mockPaginatedFieldValues,
	scrollListToBottom,
	waitForListOptions
} from 'test/infinite-scroll';
import {MockedProvider} from '@apollo/client/testing';
import {mockPreferenceReq} from 'test/graphql-data';
import {Property} from 'shared/util/records';
import {RelationalOperators, TimeSpans} from '../../utils/constants';

const mockValue = createCustomValueMap([
	{
		key: 'criterionGroup',
		value: [
			{
				operatorName: RelationalOperators.EQ,
				propertyName: 'context/country',
				value: 'foo country'
			},
			{
				operatorName: RelationalOperators.EQ,
				propertyName: 'completeDate',
				value: TimeSpans.Last7Days
			},
			{
				operatorName: RelationalOperators.EQ,
				propertyName: 'context/region',
				value: 'foo region'
			},
			{
				operatorName: RelationalOperators.EQ,
				propertyName: 'context/city',
				value: 'foo city'
			}
		]
	}
]);

const emptyMockValue = createCustomValueMap([
	{
		key: 'criterionGroup',
		value: [
			{
				operatorName: RelationalOperators.EQ,
				propertyName: 'context/country',
				value: ''
			},
			{
				operatorName: RelationalOperators.EQ,
				propertyName: 'completeDate',
				value: TimeSpans.Last7Days
			}
		]
	}
]);

const WrapperComponent = ({children}) => (
	<ApolloProvider client={client}>
		<MockedProvider mocks={[mockPreferenceReq()]}>
			{children}
		</MockedProvider>
	</ApolloProvider>
);

describe('GeolocationInput', () => {
	afterEach(cleanup);

	it('loads the next page of regions with the country filter', async () => {
		API.session.fetchFieldValues.mockImplementation(
			mockPaginatedFieldValues()
		);

		const restoreListGeometry = mockListGeometry();

		render(
			<WrapperComponent>
				<GeolocationInput
					channelId="123"
					groupId="456"
					onChange={jest.fn()}
					property={new Property(data.mockProperty({}))}
					touched={false}
					valid
					value={mockValue}
				/>
			</WrapperComponent>
		);

		fireEvent.focus(screen.getByPlaceholderText(/^region$/i));

		await waitForListOptions();

		scrollListToBottom();

		await waitFor(() =>
			expect(API.session.fetchFieldValues).toHaveBeenCalledWith({
				channelId: '123',
				delta: 20,
				fieldName: 'context/region',
				filter: "context/country eq 'foo country'",
				groupId: '456',
				page: 2,
				query: 'foo region'
			})
		);

		restoreListGeometry();
	});

	it('should render', () => {
		const {getAllByText, getByText} = render(
			<WrapperComponent>
				<GeolocationInput
					onChange={jest.fn()}
					property={new Property(data.mockProperty({}))}
					touched={false}
					valid={false}
					value={emptyMockValue}
				/>
			</WrapperComponent>
		);
		fireEvent.click(getByText('was'));
		fireEvent.click(getByText('on'));

		expect(getAllByText('was')[1]).toBeInTheDocument();
		expect(getByText('was not')).toBeInTheDocument();
		expect(getByText('contained')).toBeInTheDocument();
		expect(getByText('did not contain')).toBeInTheDocument();

		expect(getByText('since')).toBeInTheDocument();
		expect(getByText('after')).toBeInTheDocument();
		expect(getByText('before')).toBeInTheDocument();
		expect(getByText('between')).toBeInTheDocument();
		expect(getByText('ever')).toBeInTheDocument();
		expect(getAllByText('on')[1]).toBeInTheDocument();
	});

	it('should mark the country input as invalid', () => {
		const {getByPlaceholderText} = render(
			<WrapperComponent>
				<GeolocationInput
					onChange={jest.fn()}
					property={new Property(data.mockProperty({}))}
					touched
					valid={false}
					value={emptyMockValue}
				/>
			</WrapperComponent>
		);
		expect(getByPlaceholderText(/^country$/i)).toBeInvalid();
	});

	it('should render inputs for all location values if they all have non-zero length values', () => {
		const {container} = render(
			<WrapperComponent>
				<GeolocationInput
					onChange={jest.fn()}
					property={new Property(data.mockProperty({}))}
					touched={false}
					valid
					value={mockValue}
				/>
			</WrapperComponent>
		);
		expect(container.querySelectorAll('input').length).toBe(4);
	});

	it('should render the region input as a button if the initial value for the input was empty', () => {
		const {container} = render(
			<WrapperComponent>
				<GeolocationInput
					onChange={jest.fn()}
					property={new Property(data.mockProperty({}))}
					touched={false}
					valid
					value={createCustomValueMap([
						{
							key: 'criterionGroup',
							value: [
								{
									operatorName: RelationalOperators.EQ,
									propertyName: 'context/country',
									value: 'foo country'
								},
								{
									operatorName: RelationalOperators.EQ,
									propertyName: 'completeDate',
									value: TimeSpans.Last7Days
								},
								{
									operatorName: RelationalOperators.EQ,
									propertyName: 'context/region',
									value: ''
								}
							]
						}
					])}
				/>
			</WrapperComponent>
		);

		expect(container.querySelectorAll('.button-root')[0]).toHaveTextContent(
			/add region/i
		);
	});

	it('should render the city input as a button if the initial value for the input was empty', () => {
		const {container} = render(
			<WrapperComponent>
				<GeolocationInput
					onChange={jest.fn()}
					property={new Property(data.mockProperty({}))}
					touched={false}
					valid
					value={createCustomValueMap([
						{
							key: 'criterionGroup',
							value: [
								{
									operatorName: RelationalOperators.EQ,
									propertyName: 'context/country',
									value: 'foo country'
								},
								{
									operatorName: RelationalOperators.EQ,
									propertyName: 'completeDate',
									value: TimeSpans.Last7Days
								},
								{
									operatorName: RelationalOperators.EQ,
									propertyName: 'context/region',
									value: 'foo region'
								},
								{
									operatorName: RelationalOperators.EQ,
									propertyName: 'context/city',
									value: ''
								}
							]
						}
					])}
				/>
			</WrapperComponent>
		);

		expect(container.querySelector('.button-root')).toHaveTextContent(
			/add city/i
		);
	});
});
