jest.unmock('react-dom');

import Input from '../Input';
import mockStore from 'test/mock-store';
import React from 'react';
import {Field, Form, Formik} from 'formik';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {waitForLoadingToBeRemoved} from 'test/helpers';

const DefaultComponent = props => (
	<Formik initialValues={{foo: ''}} onSubmit={jest.fn()}>
		<Form>
			<Field
				component={Input}
				label='Input label'
				mask={[]}
				name='foo'
				{...props}
			/>
		</Form>
	</Formik>
);

describe('Input', () => {
	it('should render', async () => {
		const {container} = render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					
							<MockedProvider
								cache={
									new InMemoryCache({
										addTypename: false
									})
								}
							>
								<Formik
									initialValues={{foo: ''}}
									onSubmit={jest.fn()}
								>
									<Form>
										<Field
											component={Input}
											inline
											name='foo'
										/>
									</Form>
								</Formik>
							</MockedProvider>
						
				</MemoryRouter>
			</Provider>
		);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});

	it('should render a labelled input', async () => {
		const {container, queryByText} = render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					
							<MockedProvider
								cache={
									new InMemoryCache({
										addTypename: false
									})
								}
							>
								<DefaultComponent />
							</MockedProvider>
						
				</MemoryRouter>
			</Provider>
		);

		await waitForLoadingToBeRemoved(container);

		expect(queryByText('Input label')).toBeTruthy();
	});

	it('should render a required input', async () => {
		const {container, queryByText} = render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					
							<MockedProvider
								cache={
									new InMemoryCache({
										addTypename: false
									})
								}
							>
								<DefaultComponent required />
							</MockedProvider>
						
				</MemoryRouter>
			</Provider>
		);

		await waitForLoadingToBeRemoved(container);

		expect(queryByText('Input label').closest('label')).toHaveClass(
			'required'
		);
	});
});
