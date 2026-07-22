import mockStore from 'test/mock-store';
import React from 'react';
import TimeZoneAlert from '../TimeZoneAlert';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {MemoryRouter} from 'react-router';

jest.unmock('react-dom');

jest.mock('shared/hooks/useTimeZone', () => ({
	useTimeZone: () => ({
		displayTimeZone: 'UTC -03:00 Brasilia Time (America/Recife)',
		timeZoneId: 'UTC'
	})
}));

describe('TimeZoneAlert', () => {
	it('should render', () => {
		const {container} = render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<TimeZoneAlert groupId='23' onClose={jest.fn()} />
				</MemoryRouter>
			</Provider>
		);
		expect(container).toMatchSnapshot();
	});
});
