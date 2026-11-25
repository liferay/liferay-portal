import CampaignDisplay from '../CampaignDisplay';
import React from 'react';
import {CAMPAIGN_PROPERTY} from 'segment/segment-editor/dynamic/utils/properties';
import {cleanup, render, screen} from '@testing-library/react';
import {Criterion} from 'segment/segment-editor/dynamic/utils/types';
import {fromJS, List, Map} from 'immutable';
import {getDefaultValue} from 'segment/segment-editor/dynamic/criteria-sidebar/CriteriaSidebarCollapse';
import {NotOperators} from 'segment/segment-editor/dynamic/utils/constants';
import {ReferencedObjectsContext} from 'segment/segment-editor/dynamic/context/referencedObjects';
import {SegmentCategories, SegmentTypes} from 'shared/util/constants';

const Display: React.FC<{ids: string[]}> = ({ids}) => (
	<ReferencedObjectsContext.Provider
		value={{
			referencedEntities: fromJS({
				campaigns: {
					c1: {campaignName: 'Spring Sale', id: 'c1'},
					c2: {campaignName: 'Black Friday', id: 'c2'},
				},
			}),
			referencedProperties: Map(),
		}}
	>
		<CampaignDisplay
			criterion={
				{
					operatorName: NotOperators.NotCampaignsFilter,
					propertyName: 'id',
					value: getDefaultValue(CAMPAIGN_PROPERTY).setIn(
						['criterionGroup', 'items', 0, 'value'],
						List(ids)
					),
				} as unknown as Criterion
			}
			property={CAMPAIGN_PROPERTY}
			segmentCategory={SegmentCategories.Individual}
			segmentType={SegmentTypes.Batch}
		/>
	</ReferencedObjectsContext.Provider>
);

describe('CampaignDisplay', () => {
	afterEach(cleanup);

	it('should show the operator and the current names of the campaigns', () => {
		render(<Display ids={['c1', 'c2']} />);

		expect(screen.getByText(/spring sale/i)).toBeInTheDocument();
		expect(screen.getByText(/black friday/i)).toBeInTheDocument();
		expect(screen.getByText(/^is not$/i)).toBeInTheDocument();
	});

	it('should read like the segment builder row', () => {
		render(<Display ids={['c1']} />);

		expect(screen.getByText(/^individual$/i)).toBeInTheDocument();
		expect(screen.getByText(/^campaign$/i)).toBeInTheDocument();
		expect(screen.getByText(/^touched by$/i)).toBeInTheDocument();
		expect(screen.queryByText(/campaign name/i)).not.toBeInTheDocument();
	});

	it('should tell that a campaign missing from the referenced objects no longer exists', () => {
		render(<Display ids={['deleted']} />);

		expect(
			screen.getByText(/campaign no longer exists/i)
		).toBeInTheDocument();
		expect(screen.queryByText(/^is not$/i)).not.toBeInTheDocument();
	});
});
