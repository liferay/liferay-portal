import * as data from 'test/data';
import CreatedByCell from '../CreatedBy';
import React from 'react';
import {render} from '@testing-library/react';

jest.unmock('react-dom');

const tableRow = document.createElement('tr');

describe('CreatedByCell', () => {
	it('should render', () => {
		const {container} = render(
			<CreatedByCell
				data={{
					dateModified: data.getTimestamp(),
					userName: 'Test Test'
				}}
			/>,
			{container: document.body.appendChild(tableRow)}
		);

		expect(container).toMatchSnapshot();
	});

	it('renders the last edited date in the given time zone', () => {
		const {getByText} = render(
			<CreatedByCell
				data={{
					dateModified: Date.UTC(2026, 5, 10, 1, 30),
					userName: 'Test Test'
				}}
				timeZoneId='America/Recife'
			/>,
			{container: document.body.appendChild(tableRow)}
		);

		expect(getByText('Last Edited: 6/9/26')).toBeTruthy();
	});
});
