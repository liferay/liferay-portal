import * as data from 'test/data';
import client from 'shared/apollo/client';
import mockStore from 'test/mock-store';
import Overview from '../Overview';
import React from 'react';
import {ApolloProvider} from '@apollo/client';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {Segment} from 'shared/util/records';
import {MemoryRouter} from 'react-router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

const segment = new Segment(
	data.mockSegment(0, {
		criteriaString: "(demographics/middleName/value eq 'additionalName')",
		referencedObjects: {
			fieldMappings: {
				individual: {
					demographics: {
						middleName: {
							context: 'demographics',
							dataSourceFieldNames: {},
							displayName: 'Middle Name',
							displayType: 'input-field',
							id: 'middleName',
							name: 'additionalName',
							ownerType: 'individual',
							rawType: 'Text',
							type: 'text'
						}
					}
				}
			}
		}
	})
);

jest.mock('shared/hooks/useTimeZone', () => ({
	useTimeZone: () => ({
		timeZoneId: 'UTC'
	})
}));

describe('SegmentOverview', () => {
	it('should render', async () => {
		const {container} = render(
			<Provider store={mockStore()}>
				<ApolloProvider client={client}>
					<MemoryRouter>
						<Overview groupId='23' segment={segment} />
					</MemoryRouter>
				</ApolloProvider>
			</Provider>
		);

		await waitForLoadingToBeRemoved(container).then(() => {
			expect(container).toMatchSnapshot();
		});
	});
});
