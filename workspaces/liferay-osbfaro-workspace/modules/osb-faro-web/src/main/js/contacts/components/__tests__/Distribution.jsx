import * as API from 'shared/api';
import * as data from 'test/data';
import React from 'react';
import {cleanup, render, waitFor} from '@testing-library/react';
import {Distribution} from '../Distribution';
import {List} from 'immutable';
import {User} from 'shared/util/records';

jest.unmock('react-dom');

const defaultProps = {
	currentUser: new User(data.mockUser()),
	distributionsKey: 'test',
	fieldDistributionIList: new List(),
	fieldMappingFieldName: 'test',
	groupId: '23',
	id: 'test',
	loading: true,
	numberOfBins: 10
};

describe('SegmentDistribution', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {getByText} = render(<Distribution {...defaultProps} />);

		expect(
			getByText(Liferay.Language.get('distribution-by-attribute'))
		).toBeInTheDocument();
	});

	it('should fetch the distribution of the default field mapping', async () => {
		API.fieldMappings.fetchDefault.mockReturnValueOnce(
			Promise.resolve(data.mockFieldMapping(0, {id: 'jobTitle'}))
		);

		const fetchDistribution = jest.fn();

		render(
			<Distribution
				{...defaultProps}
				fetchDistribution={fetchDistribution}
				fieldMappingFieldName={undefined}
				history={{replace: jest.fn()}}
			/>
		);

		await waitFor(() =>
			expect(fetchDistribution).toHaveBeenCalledWith(
				expect.objectContaining({fieldMappingFieldName: 'jobTitle'})
			)
		);
	});

	xit('should render a Chart component if loading is false', () => {
		const {container} = render(
			<Distribution
				{...defaultProps}
				fieldDistributionIList={
					new List([{count: 3, values: ['stuff']}])
				}
				loading={false}
			/>
		);

		expect(
			container.querySelector('.recharts-responsive-container')
		).toBeTruthy();
	});
});
