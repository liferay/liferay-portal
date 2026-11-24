import PropertyCell from '../Property';
import React from 'react';
import {render} from '@testing-library/react';

describe('PropertyCell', () => {
	it('should render', () => {
		const {container} = render(
			<PropertyCell
				data={{
					name: 'email',
					value: 'TestTest@liferay.com'
				}}
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render the no data value when has no value', () => {
		const {getByText} = render(
			<PropertyCell
				data={{
					name: 'email',
					value: ''
				}}
			/>
		);

		expect(getByText('–')).toBeInTheDocument();
	});

	it('should render the no data value when the value is null', () => {
		const {getByText} = render(
			<PropertyCell
				data={{
					name: 'email',
					value: null
				}}
			/>
		);

		expect(getByText('–')).toBeInTheDocument();
	});

	it('should render a zero value', () => {
		const {getByText} = render(
			<PropertyCell
				data={{
					name: 'count',
					value: 0
				}}
			/>
		);

		expect(getByText('0')).toBeTruthy();
	});
});
