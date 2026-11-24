import * as data from 'test/data';
import AttributeFilterSection from '../AttributeFilterSection';
import React from 'react';
import {InMemoryCache} from '@apollo/client';
import {MockedProvider} from '@apollo/client/testing';
import {mockEventPropertiesReq} from 'test/graphql-data';
import {RelationalOperators} from '../../../utils/constants';
import {render, screen, waitFor} from '@testing-library/react';

let mockInputProps;

jest.mock('../attribute-conjunction-input', () => (props) => {
	mockInputProps = props;

	return (
		<div data-testid='attribute-conjunction-input'>
			{props.attributes
				.map((attribute) => attribute.displayName)
				.join(',')}
		</div>
	);
});

const renderWithMocks = (mocks, props = {}) =>
	render(
		<MockedProvider
			addTypename={false}
			cache={
				new InMemoryCache({addTypename: false, freezeResults: false})
			}
			mocks={mocks}
		>
			<AttributeFilterSection
				conjunctionCriterion={{
					operatorName: RelationalOperators.EQ,
					propertyName: 'attribute/',
					value: ''
				}}
				eventId='documentDownloaded'
				onChange={jest.fn()}
				touched={{attribute: false, attributeValue: false}}
				valid={{attribute: true, attributeValue: true}}
				{...props}
			/>
		</MockedProvider>
	);

describe('AttributeFilterSection', () => {
	it('should render nothing when there is no eventId', () => {
		const {container} = renderWithMocks([], {eventId: ''});

		expect(container.firstChild).toBeNull();
	});

	it('should render the fetched attributes', async () => {
		const mocks = [
			mockEventPropertiesReq(
				[
					data.mockEventAttributeDefinition(0, {
						__typename: 'EventProperty'
					}),
					data.mockEventAttributeDefinition(1, {
						__typename: 'EventProperty'
					})
				],
				{
					eventId: 'documentDownloaded',
					size: 25
				}
			)
		];

		renderWithMocks(mocks);

		await waitFor(() =>
			expect(screen.getByText('where event attribute')).toBeTruthy()
		);

		expect(
			screen.getByTestId('attribute-conjunction-input')
		).toHaveTextContent('displayName-0,displayName-1');
	});

	it('should render nothing when the event has no attributes', async () => {
		const mocks = [
			mockEventPropertiesReq([], {
				eventId: 'documentDownloaded',
				size: 25
			})
		];

		const {container} = renderWithMocks(mocks);

		await waitFor(() => expect(container.firstChild).toBeNull());
	});

	it('should page the attributes through the event properties query', async () => {
		const attributes = [0, 1].map((index) =>
			data.mockEventAttributeDefinition(index, {
				__typename: 'EventProperty'
			})
		);

		renderWithMocks([
			mockEventPropertiesReq(attributes, {
				eventId: 'documentDownloaded',
				size: 25
			}),
			mockEventPropertiesReq([attributes[1]], {
				eventId: 'documentDownloaded',
				keyword: 'name',
				page: 1,
				size: 25
			})
		]);

		await screen.findByTestId('attribute-conjunction-input');

		let result;

		mockInputProps
			.attributesDataSourceFn({page: 2, pageSize: 25, query: 'name'})
			.then((value) => (result = value));

		const {dataType, displayName, id, name} = attributes[1];

		await waitFor(() =>
			expect(result).toEqual({
				items: [expect.objectContaining({dataType, displayName, id, name})],
				total: 1
			})
		);
	});
});
