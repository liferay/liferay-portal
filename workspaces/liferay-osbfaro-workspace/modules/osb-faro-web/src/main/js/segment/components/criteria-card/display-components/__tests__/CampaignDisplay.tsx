import * as API from 'shared/api';
import CampaignDisplay from '../CampaignDisplay';
import React from 'react';
import {CAMPAIGN_PROPERTY} from 'segment/segment-editor/dynamic/utils/properties';
import {cleanup, render, screen} from '@testing-library/react';
import {Criterion} from 'segment/segment-editor/dynamic/utils/types';
import {getDefaultValue} from 'segment/segment-editor/dynamic/criteria-sidebar/CriteriaSidebarCollapse';
import {List} from 'immutable';
import {MemoryRouter, Route, Routes} from 'react-router-dom';
import {NotOperators} from 'segment/segment-editor/dynamic/utils/constants';
import {SegmentTypes} from 'shared/util/constants';

jest.unmock('react-dom');

const fetchCampaign = API.campaigns.fetchCampaign as jest.Mock;

const renderDisplay = (ids: string[]) =>
	render(
		<MemoryRouter initialEntries={['/workspace/123/456/segments']}>
			<Routes>
				<Route
					element={
						<CampaignDisplay
							criterion={
								{
									operatorName:
										NotOperators.NotCampaignsFilter,
									propertyName: 'id',
									value: getDefaultValue(
										CAMPAIGN_PROPERTY
									).setIn(
										['criterionGroup', 'items', 0, 'value'],
										List(ids)
									),
								} as unknown as Criterion
							}
							property={CAMPAIGN_PROPERTY}
							segmentType={SegmentTypes.Batch}
						/>
					}
					path="/workspace/:groupId/:channelId/segments"
				/>
			</Routes>
		</MemoryRouter>
	);

describe('CampaignDisplay', () => {
	afterEach(cleanup);

	beforeEach(() => {
		fetchCampaign.mockReset();
	});

	it('should show the operator and the names of the campaigns', async () => {
		fetchCampaign.mockImplementation(({id}) =>
			Promise.resolve({
				campaignName: id === 'c1' ? 'Spring Sale' : 'Black Friday',
				id,
			})
		);

		renderDisplay(['c1', 'c2']);

		expect(await screen.findByText("'Spring Sale'")).toBeInTheDocument();
		expect(screen.getByText("'Black Friday'")).toBeInTheDocument();
		expect(screen.getByText('is not')).toBeInTheDocument();
		expect(fetchCampaign).toHaveBeenCalledWith({
			channelId: '456',
			groupId: '123',
			id: 'c1',
		});
	});

	it('should show a campaign that no longer exists as undefined', async () => {
		fetchCampaign.mockReturnValue(Promise.reject(new Error('Not Found')));

		renderDisplay(['c1']);

		expect(
			await screen.findByText('Undefined Campaign Name')
		).toBeInTheDocument();
	});
});
