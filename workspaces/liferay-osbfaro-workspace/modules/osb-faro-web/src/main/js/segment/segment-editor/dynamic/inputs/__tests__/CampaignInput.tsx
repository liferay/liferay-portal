import CampaignInput from '../CampaignInput';
import React from 'react';
import {CAMPAIGN_PROPERTY} from '../../utils/properties';
import {cleanup, render, screen} from '@testing-library/react';
import {CustomValue} from 'shared/util/records';
import {fromJS, List, Map} from 'immutable';
import {getDefaultValue} from '../../criteria-sidebar/CriteriaSidebarCollapse';
import {ReferencedObjectsContext} from '../../context/referencedObjects';
import {SegmentCategories} from 'shared/util/constants';

const getValue = (ids: string[] = []): CustomValue =>
	getDefaultValue(CAMPAIGN_PROPERTY).setIn(
		['criterionGroup', 'items', 0, 'value'],
		List(ids)
	);

const renderInput = (
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
			<CampaignInput
				channelId="456"
				displayValue={CAMPAIGN_PROPERTY.label}
				groupId="123"
				onChange={jest.fn()}
				operatorRenderer={() => <div>{'is'}</div>}
				property={CAMPAIGN_PROPERTY}
				segmentCategory={segmentCategory}
				value={getValue(ids)}
			/>
		</ReferencedObjectsContext.Provider>
	);

describe('CampaignInput', () => {
	afterEach(cleanup);

	it('should show the current name of the campaign the row holds, without a picker', () => {
		renderInput(['c1']);

		expect(screen.getByText('Spring Sale')).toBeInTheDocument();
		expect(screen.queryByRole('combobox')).not.toBeInTheDocument();
	});

	it.each([
		[SegmentCategories.Individual, 'Individual'],
		[SegmentCategories.Account, 'Account'],
	])(
		'should label the row with the segment category for %s segments',
		(segmentCategory, label) => {
			renderInput(['c1'], segmentCategory);

			expect(screen.getByText(label)).toBeInTheDocument();
			expect(screen.getByText('campaign')).toBeInTheDocument();
			expect(screen.getByText('touched by')).toBeInTheDocument();
			expect(screen.queryByText('Campaign Name')).not.toBeInTheDocument();
		}
	);

	it('should show every campaign of a saved criterion that holds several', () => {
		renderInput(['c1', 'c2']);

		expect(screen.getByText('Spring Sale')).toBeInTheDocument();
		expect(screen.getByText('Black Friday')).toBeInTheDocument();
	});
});
