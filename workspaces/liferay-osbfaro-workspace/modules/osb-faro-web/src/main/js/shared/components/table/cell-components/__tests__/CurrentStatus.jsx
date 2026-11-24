import CurrentStatusCell from '../CurrentStatus';
import React from 'react';
import {render} from '@testing-library/react';

const tableRow = document.createElement('tr');

describe('CurrentStatusCell', () => {
	it('should render', () => {
		const {container} = render(
			<CurrentStatusCell
				data={{
					currentMember: true
				}}
			/>,
			{container: document.body.appendChild(tableRow)}
		);

		expect(container).toMatchSnapshot();
	});

	it('should render as a non-member', () => {
		const {container} = render(
			<CurrentStatusCell
				data={{
					currentMember: false
				}}
			/>,
			{container: document.body.appendChild(tableRow)}
		);

		expect(container).toMatchSnapshot();
	});
});
