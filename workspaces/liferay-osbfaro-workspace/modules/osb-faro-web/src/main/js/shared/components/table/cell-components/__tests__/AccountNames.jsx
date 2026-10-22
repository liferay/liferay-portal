import AccountNames from '../AccountNames';
import React from 'react';
import {render} from '@testing-library/react';

jest.unmock('react-dom');

const tableRow = document.createElement('tr');

describe('AccountNames', () => {
	it('should render', () => {
		const {container} = render(
			<AccountNames
				data={{
					accountName: 'foo'
				}}
			/>,
			{container: document.body.appendChild(tableRow)}
		);

		expect(container).toMatchSnapshot();
	});

	it('should render a fallback display', () => {
		const {getByText} = render(
			<AccountNames
				data={{
					accountName: null
				}}
			/>,
			{container: document.body.appendChild(tableRow)}
		);

		expect(getByText('–')).toHaveClass('text-secondary');
	});

	it('should render an empty account name as no data', () => {
		const {getByText} = render(
			<AccountNames
				data={{
					accountName: ''
				}}
			/>,
			{container: document.body.appendChild(tableRow)}
		);

		expect(getByText('–')).toHaveClass('text-secondary');
	});
});
