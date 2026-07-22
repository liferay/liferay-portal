import Interests from '../Interests';
import React from 'react';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {mockIndividualInterestsReq} from 'test/graphql-data';
import {render} from '@testing-library/react';
import {Routes} from 'shared/util/router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

describe('Interests', () => {
	it('renders', async () => {
		const {getByText} = render(
			<MockedProvider
				mocks={[
					mockIndividualInterestsReq(defaultVars => ({
						...defaultVars,
						id: undefined,
						keywords: '',
						size: 2
					}))
				]}
			>
				<MemoryRouter
					initialEntries={[
						'/workspace/123/456/contacts/individuals/interests'
					]}
				>
					<RouterRoutes>
						<Route
							element={
								<Interests />
							}
							path={`${Routes.CONTACTS_INDIVIDUALS_INTERESTS}/*`}
						/>
					</RouterRoutes>
				</MemoryRouter>
			</MockedProvider>
		);

		await waitForLoadingToBeRemoved(document.body);

		jest.runAllTimers();

		expect(getByText('Interest Topics')).toBeInTheDocument();
	});
});
