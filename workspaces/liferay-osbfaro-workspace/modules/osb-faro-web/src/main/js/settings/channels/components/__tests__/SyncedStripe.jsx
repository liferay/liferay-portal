import React from 'react';
import SyncedStripe, {getTitle} from '../SyncedStripe';
import {cleanup, render} from '@testing-library/react';

jest.unmock('react-dom');

describe('SyncedStripe', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<SyncedStripe sitesSyncedCount={0} />);

		expect(container).toMatchSnapshot();
	});

	it.each`
		sitesSyncedCount
		${0}
		${1}
		${2}
	`(
		'returns correct text if sitesSyncedCount is $sitesSyncedCount',
		({sitesSyncedCount}) => {
			const {queryByText} = render(
				<SyncedStripe sitesSyncedCount={sitesSyncedCount} />
			);

			if (sitesSyncedCount === 1) {
				expect(
					queryByText('There is 1 site synced to this property.')
				).toBeInTheDocument();
			} else {
				expect(
					queryByText(
						`There are ${sitesSyncedCount} sites synced to this property.`
					)
				).toBeInTheDocument();
			}
		}
	);
});

describe('getTitle', () => {
	it.each`
		sitesSyncedCount
		${0}
		${1}
		${2}
	`(
		'returns correct text if sitesSyncedCount is $sitesSyncedCount',
		({sitesSyncedCount}) => {
			const title = getTitle(sitesSyncedCount);

			if (sitesSyncedCount === 1) {
				expect(title).toEqual('There is 1 site synced to this property.');
			} else {
				expect(title).toEqual(
					`There are ${sitesSyncedCount} sites synced to this property.`
				);
			}
		}
	);
});
