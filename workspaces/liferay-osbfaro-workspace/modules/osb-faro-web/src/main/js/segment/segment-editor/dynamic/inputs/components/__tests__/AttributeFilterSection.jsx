import * as data from 'test/data';
import AttributeFilterSection from '../AttributeFilterSection';
import React from 'react';
import {InMemoryCache} from '@apollo/client';
import {MockedProvider} from '@apollo/client/testing';
import {mockEventPropertiesReq} from 'test/graphql-data';
import {RelationalOperators} from '../../../utils/constants';
import {render, screen, waitFor} from '@testing-library/react';

jest.unmock('react-dom');

jest.mock('../attribute-conjunction-input', () => (props) => (
	<div data-testid='attribute-conjunction-input'>
		{props.attributes.map((attribute) => attribute.displayName).join(',')}
	</div>
));

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
});
