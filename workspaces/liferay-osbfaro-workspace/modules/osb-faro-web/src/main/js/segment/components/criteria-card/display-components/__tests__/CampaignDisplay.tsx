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

const renderDisplay = (
	ids: string[],
	segmentCategory = SegmentCategories.Individual
) =>
	render(
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
				segmentCategory={segmentCategory}
				segmentType={SegmentTypes.Batch}
			/>
		</ReferencedObjectsContext.Provider>
	);

describe('CampaignDisplay', () => {
	afterEach(cleanup);

	it('should show the operator and the current names of the campaigns', () => {
		renderDisplay(['c1', 'c2']);

		expect(screen.getByText("'Spring Sale'")).toBeInTheDocument();
		expect(screen.getByText("'Black Friday'")).toBeInTheDocument();
		expect(screen.getByText('is not')).toBeInTheDocument();
	});

	it.each([
		[SegmentCategories.Individual, 'Individual'],
		[SegmentCategories.Account, 'Account'],
	])(
		'should read like the segment builder row for %s segments',
		(segmentCategory, label) => {
			renderDisplay(['c1'], segmentCategory);

			expect(screen.getByText(label)).toBeInTheDocument();
			expect(screen.getByText('campaign')).toBeInTheDocument();
			expect(screen.getByText('touched by')).toBeInTheDocument();
			expect(screen.queryByText('Campaign Name')).not.toBeInTheDocument();
		}
	);

	it('should tell that a campaign missing from the referenced objects no longer exists', () => {
		renderDisplay(['deleted']);

		expect(
			screen.getByText('Campaign no longer exists.')
		).toBeInTheDocument();
		expect(screen.queryByText('is not')).not.toBeInTheDocument();
	});
});
