import * as data from 'test/data';
import React from 'react';
import {Formik} from 'formik';
import {FormSelectFieldInput} from '../SelectFieldInput';
import {render} from '@testing-library/react';

const Wrapper = ({children}) => (
	<Formik initialValues={{foo: ''}} onSubmit={() => {}}>
		{children}
	</Formik>
);

describe('SelectFieldInput', () => {
	it('should render', () => {
		const {getByPlaceholderText} = render(
			<Wrapper>
				<FormSelectFieldInput
					field={{name: 'foo'}}
					form={data.mockForm()}
					groupId='23'
					name='foo'
				/>
			</Wrapper>
		);

		expect(
			getByPlaceholderText(Liferay.Language.get('select-field'))
		).toBeInTheDocument();
	});

	it('should render with a label', () => {
		const label = 'bar';

		const {getByText} = render(
			<Wrapper>
				<FormSelectFieldInput
					field={{name: 'foo'}}
					form={data.mockForm()}
					groupId='23'
					label={label}
					name='foo'
				/>
			</Wrapper>
		);

		expect(getByText(label)).toBeTruthy();
	});
});
