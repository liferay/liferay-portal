import AccountName from '../AccountName';
import React from 'react';
import {render} from '@testing-library/react';

jest.unmock('react-dom');

const renderAccountName = (accountName: string | null) =>
	render(
		<table>
			<tbody>
				<tr>
					<AccountName data={{accountName: accountName as string}} />
				</tr>
			</tbody>
		</table>
	);

describe('AccountName', () => {
	it('should render the account name', () => {
		const {getByText} = renderAccountName('Acme');

		expect(getByText('Acme')).toBeTruthy();
	});

	it.each([null, ''])('should render %p as no data', (accountName) => {
		const {getByText} = renderAccountName(accountName);

		expect(getByText('–')).toHaveClass('text-secondary');
	});
});
