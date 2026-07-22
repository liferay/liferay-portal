import * as data from 'test/data';
import GlobalAttributeList from '../GlobalAttributeList';
import React from 'react';
import {AttributeTypes} from 'event-analysis/utils/types';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {mockEventAttributeDefinitionsReq} from 'test/graphql-data';
import {render} from '@testing-library/react';
import {Routes} from 'shared/util/router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

const mockGroupId = '23';

const Wrapper = ({children, mocks = []}) => (
	<MemoryRouter
		initialEntries={[
			`/workspace/${mockGroupId}/settings/definitions/event-attributes/global?delta=1`
		]}
	>
		<RouterRoutes>
			<Route
				element={
					<MockedProvider addTypename={false} mocks={mocks}>
						{children}
					</MockedProvider>
				}
				path={`${Routes.SETTINGS_DEFINITIONS_EVENT_ATTRIBUTES_GLOBAL}/*`}
			/>
		</RouterRoutes>
	</MemoryRouter>
);

describe('GlobalAttributeList', () => {
	it('should render', async () => {
		const mocks = [
			mockEventAttributeDefinitionsReq(
				[
					data.mockEventAttributeDefinition(0, {
						__typename: 'EventAttributeDefinition'
					})
				],
				{
					keyword: '',
					page: 0,
					size: 1,
					sort: {column: 'name', type: 'ASC'},
					type: AttributeTypes.Global
				}
			)
		];

		const {container} = render(
			<Wrapper mocks={mocks}>
				<GlobalAttributeList />
			</Wrapper>
		);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});
});
