import MembershipChanges from '../MembershipChanges';
import React from 'react';
import {render} from '@testing-library/react';

jest.unmock('react-dom');

const tableRow = document.createElement('tr');

describe('MembershipChanges', () => {
	it('renders the create date as a long UTC date', () => {
		const {getByText} = render(
			<MembershipChanges
				data={{
					createDateTime: '2026-06-10T23:30:00Z',
					type: 'ADDED'
				}}
			/>,
			{container: document.body.appendChild(tableRow)}
		);

		expect(getByText('June 10, 2026')).toBeTruthy();
		expect(getByText('Added')).toBeTruthy();
	});
});
