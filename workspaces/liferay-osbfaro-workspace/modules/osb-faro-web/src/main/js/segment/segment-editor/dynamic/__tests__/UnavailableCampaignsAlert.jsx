import React from 'react';
import UnavailableCampaignsAlert from '../UnavailableCampaignsAlert';
import {CAMPAIGN_PROPERTY} from '../utils/properties';
import {cleanup, render, screen} from '@testing-library/react';
import {fromJS, List, Map} from 'immutable';
import {getDefaultValue} from '../criteria-sidebar/CriteriaSidebarCollapse';
import {ReferencedObjectsContext} from '../context/referencedObjects';

const Alert = ({campaignId}) => (
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
		render(<Alert campaignId='deleted' />);

		expect(
			screen.getByText(/criterion that is no longer available/i)
		).toBeInTheDocument();
	});
});
