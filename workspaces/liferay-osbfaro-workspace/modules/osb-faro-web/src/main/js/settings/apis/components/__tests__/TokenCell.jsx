import * as data from 'test/data';
import React from 'react';
import TokenCell from '../TokenCell';
import {cleanup, getByText, render} from '@testing-library/react';
import {getISODate} from 'shared/util/date';
import {mockGetDateNow} from 'test/mock-date';

jest.unmock('react-dom');

describe('TokenCell', () => {
	beforeAll(() => {
		mockGetDateNow(getISODate(data.getTimestamp(0)));
	});

	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<TokenCell data={data.mockApiToken()} />);

		expect(container).toMatchSnapshot();
	});

	it('should render with an expired label if the Token is expired', () => {
		const {container} = render(
			<TokenCell
				data={data.mockApiToken({
					expirationDate: getISODate(data.getTimestamp(-1))
				})}
			/>
		);

		expect(
			getByText(container.querySelector('.label'), 'Expired')
		).toBeTruthy();

		jest.restoreAllMocks();
	});
});
