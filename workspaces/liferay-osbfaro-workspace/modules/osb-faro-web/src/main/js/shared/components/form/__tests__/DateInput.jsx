import Form from '../index';
import mockStore from 'test/mock-store';
import React from 'react';
import {MemoryRouter} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {mockPreferenceReq} from 'test/graphql-data';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';

const DefaultComponent = props => (
	<Provider store={mockStore()}>
		<MemoryRouter>
			<MockedProvider addTypename={false} mocks={[mockPreferenceReq()]}>
				<Form
					initialValues={{
						foo: ''
					}}
					onSubmit={jest.fn()}
				>
					<Form.Form>
						<Form.DateInput
							label='Foo Date'
							name='foo'
							{...props}
						/>
					</Form.Form>
				</Form>
			</MockedProvider>
		</MemoryRouter>
	</Provider>
);

describe('DateInput', () => {
	const labelContent = 'Foo Date';

	it('should render', () => {
		const {container} = render(<DefaultComponent />);

		expect(container).toMatchSnapshot();
	});

	it('should render with label', () => {
		const {queryByText} = render(<DefaultComponent label={labelContent} />);

		expect(queryByText(labelContent)).toBeTruthy();
	});

	it('should render as required', () => {
		const {getByLabelText} = render(
			<DefaultComponent label={labelContent} required />
		);

		expect(getByLabelText(labelContent, {exact: false})).toBeRequired();
	});
});
