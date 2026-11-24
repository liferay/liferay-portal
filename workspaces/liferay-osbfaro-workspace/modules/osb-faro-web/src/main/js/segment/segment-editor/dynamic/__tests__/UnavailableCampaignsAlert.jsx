import React from 'react';
import UnavailableCampaignsAlert from '../UnavailableCampaignsAlert';
import {CAMPAIGN_PROPERTY} from '../utils/properties';
import {cleanup, render, screen} from '@testing-library/react';
import {fromJS, List, Map} from 'immutable';
import {getDefaultValue} from '../criteria-sidebar/CriteriaSidebarCollapse';
import {ReferencedObjectsContext} from '../context/referencedObjects';

const MESSAGE =
	'This segment has a criterion that is no longer available. Update or remove it to keep the segment accurate.';

const renderAlert = campaignId =>
	render(
		<ReferencedObjectsContext.Provider
			value={{
				referencedEntities: fromJS({
					campaigns: {c1: {campaignName: 'Spring Sale', id: 'c1'}}
				}),
				referencedProperties: Map()
			}}
		>
			<UnavailableCampaignsAlert
				criteria={{
					conjunctionName: 'and',
					criteriaGroupId: 'group_0',
					items: [
						{
							operatorName: 'campaigns-filter',
							propertyName: 'id',
							value: getDefaultValue(CAMPAIGN_PROPERTY).setIn(
								['criterionGroup', 'items', 0, 'value'],
								List([campaignId])
							)
						}
					]
				}}
			/>
		</ReferencedObjectsContext.Provider>
	);

describe('UnavailableCampaignsAlert', () => {
	afterEach(cleanup);

	it('should warn about a criterion whose campaign no longer exists', () => {
		renderAlert('deleted');

		expect(screen.getByText(MESSAGE)).toBeInTheDocument();
	});

	it('should stay hidden while every campaign still exists', () => {
		renderAlert('c1');

		expect(screen.queryByText(MESSAGE)).not.toBeInTheDocument();
	});
});
