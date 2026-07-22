import * as API from 'shared/api';
import EnrichedProfilesCard from '../EnrichedProfilesCard';
import React from 'react';
import ReactDOM from 'react-dom';
import {cleanup, queryByText, render} from '@testing-library/react';
import {CredentialTypes} from 'shared/util/constants';
import {DataSource} from 'shared/util/records';
import {
	getImmutableMock,
	mockCSVDataSource,
	mockLiferayDataSource,
	mockSalesforceDataSource
} from 'test/data';
import {MemoryRouter} from 'react-router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		groupId: '23'
	})
}));

ReactDOM.createPortal = jest.fn();

const DefaultComponent = props => (
	<MemoryRouter>
		<EnrichedProfilesCard {...props} />
	</MemoryRouter>
);

describe('EnrichedProfilesCard', () => {
	afterEach(() => {
		jest.clearAllMocks();

		cleanup();
	});

	it('should render', async () => {
		const {container} = render(<EnrichedProfilesCard />);

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});

	it('should render a fallback display if count is not finite', async () => {
		API.individuals.fetchEnrichedProfilesCount.mockReturnValue(
			Promise.resolve({total: 0})
		);

		const {container} = render(<EnrichedProfilesCard />);

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		expect(container.querySelector('.total')).toHaveTextContent(
			'0 Profiles'
		);
	});
});

describe('EnrichedProfilesCard Enrich Profiles Prompt', () => {
	const baseDescription =
		'should render a prompt to enrich profiles if the only connected datasource is DXP with no contacts configuration';

	const enrichPrompt = /Know Your Audience Better/;

	const assertHasEnrichPrompt = container => {
		expect(queryByText(container, enrichPrompt)).toBeTruthy();
	};

	it(baseDescription, () => {
		const mockLiferay = getImmutableMock(
			DataSource,
			mockLiferayDataSource,
			0,
			{
				credentials: {type: CredentialTypes.Token},
				details: {contactsSelected: false}
			}
		);

		const {container} = render(
			<DefaultComponent dataSources={[mockLiferay]} />
		);

		assertHasEnrichPrompt(container);
	});

	it(`${baseDescription} and has a legacy authentication method`, () => {
		const mockLiferay = getImmutableMock(
			DataSource,
			mockLiferayDataSource,
			0,
			{credentials: {type: CredentialTypes.OAuth2}}
		);

		const {container} = render(
			<DefaultComponent dataSources={[mockLiferay]} />
		);

		assertHasEnrichPrompt(container);
	});

	it('should render a prompt to enrich profiles if there are no connected datasources', () => {
		const {container} = render(<DefaultComponent dataSources={[]} />);

		assertHasEnrichPrompt(container);
	});

	it('should render a prompt to enrich profiles if there are multiple DXPs without a contacts configuration', () => {
		const {container} = render(
			<DefaultComponent
				dataSources={[
					getImmutableMock(DataSource, mockLiferayDataSource),
					getImmutableMock(DataSource, mockLiferayDataSource)
				]}
			/>
		);

		assertHasEnrichPrompt(container);
	});

	it('should NOT render a prompt if the only connected datasources are non-DXP', () => {
		const {queryByText} = render(
			<DefaultComponent
				dataSources={[
					getImmutableMock(DataSource, mockSalesforceDataSource),
					getImmutableMock(DataSource, mockCSVDataSource)
				]}
			/>
		);

		expect(queryByText(enrichPrompt)).toBeNull();
	});
});
