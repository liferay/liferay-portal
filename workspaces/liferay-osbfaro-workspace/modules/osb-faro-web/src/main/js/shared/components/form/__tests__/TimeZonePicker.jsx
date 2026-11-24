import * as pedantic from 'test/pedantic';
import Form from '../index';
import mockStore from 'test/mock-store';
import React from 'react';
import TimeZonePicker from '../TimeZonePicker';
import {cleanup, render} from '@testing-library/react';
import {fromJS} from 'immutable';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {Provider} from 'react-redux';

const DefaultComponent = props => (
	<Provider
		store={mockStore(
			fromJS({
				projects: {
					23: {
						data: {
							timeZone: {
								timeZoneId: 'UTC'
							}
						}
					}
				}
			})
		)}
	>
		<MemoryRouter>
			<MockedProvider
				cache={
					new InMemoryCache({
						addTypename: false
					})
				}
				mocks={[]}
			>
				<Form
					initialValues={{
						timeZoneId: 'UTC'
					}}
					onSubmit={jest.fn()}
				>
					<TimeZonePicker
						fieldName='timeZoneId'
						setFieldTouched={jest.fn()}
						setFieldValue={jest.fn()}
						{...props}
					/>
				</Form>
			</MockedProvider>
		</MemoryRouter>
	</Provider>
);

describe('TimeZonePicker', () => {
	beforeEach(() => {
		pedantic.disable();
	});

	afterEach(() => {
		pedantic.enable();
		cleanup();
	});

	it('renders', () => {
		const {container} = render(<DefaultComponent />);

		expect(container).toMatchSnapshot();
	});
});
