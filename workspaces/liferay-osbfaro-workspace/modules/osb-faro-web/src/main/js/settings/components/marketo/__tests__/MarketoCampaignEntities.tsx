jest.unmock('react-dom');

import MarketoCampaignEntities from '../MarketoCampaignEntities';
import React from 'react';
import {render} from '@testing-library/react';

describe('MarketoCampaignEntities', () => {
	it('renders the Configured label when the synced count is greater than zero', () => {
		const {getByText} = render(
			<MarketoCampaignEntities
				enabledIndividuals
				individualsSyncedCount={5}
				onIndividualsChange={jest.fn()}
			/>
		);

		expect(getByText('CONFIGURED')).toBeTruthy();
	});

	it('renders the Unconfigured label when the synced count is zero', () => {
		const {getByText} = render(
			<MarketoCampaignEntities
				enabledIndividuals
				individualsSyncedCount={0}
				onIndividualsChange={jest.fn()}
			/>
		);

		expect(getByText('UNCONFIGURED')).toBeTruthy();
	});

	it('renders the Unconfigured label when no synced count is provided', () => {
		const {getByText} = render(
			<MarketoCampaignEntities
				enabledIndividuals={false}
				onIndividualsChange={jest.fn()}
			/>
		);

		expect(getByText('UNCONFIGURED')).toBeTruthy();
	});
});
