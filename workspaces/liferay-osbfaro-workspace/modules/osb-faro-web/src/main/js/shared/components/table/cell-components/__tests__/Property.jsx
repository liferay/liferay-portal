import PropertyCell from '../Property';
import React from 'react';
import {render} from '@testing-library/react';

jest.unmock('react-dom');

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

		expect(getByText('–')).toHaveClass('text-secondary');
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

		expect(getByText('–')).toHaveClass('text-secondary');
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
