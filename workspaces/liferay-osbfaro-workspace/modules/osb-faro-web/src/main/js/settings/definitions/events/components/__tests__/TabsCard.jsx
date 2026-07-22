import mockStore from 'test/mock-store';
import React from 'react';
import TabsCard from '../TabsCard';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {MemoryRouter} from 'react-router';

jest.unmock('react-dom');

describe('TabsCard', () => {
	it('should render', () => {
		const {container} = render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<TabsCard groupId='23' />
				</MemoryRouter>
			</Provider>
		);

		expect(container).toMatchSnapshot();
	});
});
