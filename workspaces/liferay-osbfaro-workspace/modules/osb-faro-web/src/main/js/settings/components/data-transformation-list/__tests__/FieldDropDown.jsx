import FieldDropDown from '../FieldDropDown';
import mockStore from 'test/mock-store';
import React from 'react';
import {act, cleanup, render} from '@testing-library/react';
import {Map} from 'immutable';
import {MemoryRouter} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {Provider} from 'react-redux';

const DefaultWrapper = ({children}) => (
	<Provider store={mockStore()}>
		<MemoryRouter>
			<MockedProvider>{children}</MockedProvider>
		</MemoryRouter>
	</Provider>
);

const DefaultComponent = props => (
	<FieldDropDown
		buttonPlaceholder='Search'
		dataIMap={new Map()}
		inputPlaceholder='Search here'
		searchItems={[]}
		{...props}
	/>
);

describe('FieldDropDown', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(
			<DefaultWrapper>
				<DefaultComponent />
			</DefaultWrapper>
		);

		act(() => {
			jest.advanceTimersByTime(250);
		});

		expect(container).toMatchSnapshot();
	});

	it('should render with a title', () => {
		const {getByText} = render(
			<DefaultWrapper>
				<DefaultComponent title='FOO BAR' />
			</DefaultWrapper>
		);

		act(() => {
			jest.advanceTimersByTime(250);
		});

		expect(getByText('FOO BAR')).toBeTruthy();
	});

	it('should render with data', () => {
		const {getByText} = render(
			<DefaultWrapper>
				<DefaultComponent
					dataIMap={new Map({name: 'foo', value: 450})}
				/>
			</DefaultWrapper>
		);

		act(() => {
			jest.advanceTimersByTime(250);
		});

		expect(getByText('foo')).toBeTruthy();
		expect(getByText('450')).toBeTruthy();
	});
});
