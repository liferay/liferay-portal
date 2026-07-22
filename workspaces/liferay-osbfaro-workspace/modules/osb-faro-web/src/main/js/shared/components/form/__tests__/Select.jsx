jest.unmock('react-dom');

import mockStore from 'test/mock-store';
import React from 'react';
import Select from '../Select';
import {Field, Form, Formik} from 'formik';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {waitForLoadingToBeRemoved} from 'test/helpers';

describe('Select', () => {
	it('renders', async () => {
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
									initialValues={{test: 'red'}}
									onSubmit={jest.fn()}
								>
									<Form>
										<Field component={Select} name='test'>
											<Select.Item value='red'>
												{'red'}
											</Select.Item>
										</Field>
									</Form>
								</Formik>
							</MockedProvider>
						
				</MemoryRouter>
			</Provider>
		);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});

	it('renders w/ label', async () => {
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
									initialValues={{test: ''}}
									onSubmit={jest.fn()}
								>
									<Form>
										<Field
											component={Select}
											label='foo'
											name='test'
										/>
									</Form>
								</Formik>
							</MockedProvider>
						
				</MemoryRouter>
			</Provider>
		);

		await waitForLoadingToBeRemoved(container);

		expect(container.querySelector('label')).not.toBeNull();
	});

	it('renders w/ error', async () => {
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
									initialErrors={{
										test: 'can not be test'
									}}
									initialTouched={{test: true}}
									initialValues={{test: 'red'}}
									onSubmit={jest.fn()}
								>
									<Form>
										<Field component={Select} name='test'>
											<Select.Item value='red'>
												{'red'}
											</Select.Item>
										</Field>
									</Form>
								</Formik>
							</MockedProvider>
						
				</MemoryRouter>
			</Provider>
		);

		await waitForLoadingToBeRemoved(container);

		expect(container.querySelector('.has-error')).not.toBeNull();
	});
});
