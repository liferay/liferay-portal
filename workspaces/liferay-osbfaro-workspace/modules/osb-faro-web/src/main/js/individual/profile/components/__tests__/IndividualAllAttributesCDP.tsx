import * as API from 'shared/api';
import IndividualAllAttributesCDP from '../IndividualAllAttributesCDP';
import mockStore from 'test/mock-store';
import React from 'react';
import {MemoryRouter} from 'react-router-dom';
import {Provider} from 'react-redux';
import {render, screen} from '@testing-library/react';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

describe('IndividualAllAttributesCDP', () => {
	it('renders the last modified date with the custom date format', async () => {
		(API.individuals.fetchDetails as jest.Mock).mockReturnValueOnce(
			Promise.resolve({
				custom: {},
				demographics: {
					jobTitle: [
						{
							dataSourceId: '123',
							dataSourceName: 'Liferay DXP',
							dateModified: '2026-06-10T23:30:00.000Z',
							name: 'jobTitle',
							sourceName: 'jobTitle',
							value: 'Developer',
						},
					],
				},
			})
		);

		const {container} = render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<IndividualAllAttributesCDP
						groupId="23"
						individualId="1"
						showEmptyState={false}
					/>
				</MemoryRouter>
			</Provider>
		);

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		expect(screen.getByText('Jun 10, 2026')).toBeInTheDocument();
	});
});
