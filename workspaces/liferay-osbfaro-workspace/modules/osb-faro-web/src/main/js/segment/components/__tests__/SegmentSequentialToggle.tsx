import React from 'react';
import SegmentSequentialToggle from '../SegmentSequentialToggle';
import {cleanup, fireEvent, render, waitFor} from '@testing-library/react';
import {Form, Formik, FormikProps} from 'formik';

jest.unmock('react-dom');

type Values = {sequential: boolean};

const renderToggle = () => {
	let formik: FormikProps<Values> | undefined;

	const result = render(
		<Formik<Values>
			initialValues={{sequential: false}}
			onSubmit={jest.fn()}
		>
			{(props) => {
				formik = props;

				return (
					<Form>
						<SegmentSequentialToggle name="sequential" />
					</Form>
				);
			}}
		</Formik>
	);

	return {...result, getFormik: () => formik!};
};

describe('SegmentSequentialToggle', () => {
	afterEach(cleanup);

	it('updates the field when it is toggled', async () => {
		const {getByRole, getFormik} = renderToggle();

		fireEvent.click(
			getByRole('switch', {name: /^enable sequential criteria$/i})
		);

		await waitFor(() => expect(getFormik().values.sequential).toBe(true));
	});

	it('marks the field as touched when it loses focus', async () => {
		const {getByRole, getFormik} = renderToggle();

		fireEvent.blur(
			getByRole('switch', {name: /^enable sequential criteria$/i})
		);

		await waitFor(() => expect(getFormik().touched.sequential).toBe(true));
	});

	it('does not use the field name as the element id', () => {
		const {getByRole} = renderToggle();

		expect(
			getByRole('switch', {name: /^enable sequential criteria$/i})
		).not.toHaveAttribute('id', 'sequential');
	});
});
