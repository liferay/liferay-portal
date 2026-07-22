import * as data from 'test/data';
import mockStore from 'test/mock-store';
import OverviewCDP from '../OverviewCDP';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {Individual} from 'shared/util/records';
import {Provider} from 'react-redux';
import {MemoryRouter} from 'react-router';

jest.unmock('react-dom');

jest.mock('../../components/ContextualInformation', () => ({
	__esModule: true,
	default: () => <div>{'ContextualInformation'}</div>
}));

jest.mock('../../hoc/ProfileCardCDP', () => ({
	__esModule: true,
	default: () => <div>{'ProfileCardCDP'}</div>
}));

const mockIndividual = data.getImmutableMock(Individual, data.mockIndividual);

const renderComponent = () =>
	render(
		<Provider store={mockStore()}>
			<MemoryRouter>
				<OverviewCDP groupId='23' individual={mockIndividual} />
			</MemoryRouter>
		</Provider>
	);

describe('IndividualOverview', () => {
	afterEach(() => {
		cleanup();
	});

	it('should render the contextual information and profile card sections', () => {
		const {getByText} = renderComponent();

		expect(getByText('ContextualInformation')).toBeTruthy();
		expect(getByText('ProfileCardCDP')).toBeTruthy();
	});

	it('should not render the no site data synced empty state', () => {
		const {queryByText} = renderComponent();

		expect(queryByText('No Site Data Synced')).not.toBeInTheDocument();
		expect(queryByText('Connect Data Source')).not.toBeInTheDocument();
	});
});
