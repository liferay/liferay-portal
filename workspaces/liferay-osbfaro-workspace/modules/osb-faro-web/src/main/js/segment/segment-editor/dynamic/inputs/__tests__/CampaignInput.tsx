import CampaignInput from '../CampaignInput';
import React from 'react';
import {CAMPAIGN_PROPERTY} from '../../utils/properties';
import {cleanup, render, screen} from '@testing-library/react';
import {fromJS, List, Map} from 'immutable';
import {getDefaultValue} from '../../criteria-sidebar/CriteriaSidebarCollapse';
import {ReferencedObjectsContext} from '../../context/referencedObjects';
import {SegmentCategories} from 'shared/util/constants';

const Input: React.FC<{ids: string[]}> = ({ids}) => (
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
			segmentCategory={SegmentCategories.Individual}
			value={getDefaultValue(CAMPAIGN_PROPERTY).setIn(
				['criterionGroup', 'items', 0, 'value'],
				List(ids)
			)}
		/>
	</ReferencedObjectsContext.Provider>
);

describe('CampaignInput', () => {
	afterEach(cleanup);

	it('should label the row with the segment category', () => {
		render(<Input ids={['c1']} />);

		expect(screen.getByText(/^individual$/i)).toBeInTheDocument();
		expect(screen.getByText(/^campaign$/i)).toBeInTheDocument();
		expect(screen.getByText(/^touched by$/i)).toBeInTheDocument();
		expect(screen.queryByText(/campaign name/i)).not.toBeInTheDocument();
	});

	it('should show the current names of the campaigns, without a picker', () => {
		render(<Input ids={['c1', 'c2']} />);

		expect(screen.getByText(/spring sale/i)).toBeInTheDocument();
		expect(screen.getByText(/black friday/i)).toBeInTheDocument();
		expect(screen.queryByRole('combobox')).not.toBeInTheDocument();
	});
});
