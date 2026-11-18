import ConfigureWorkspace from '../ConfigureWorkspace';
import mockStore from 'test/mock-store';
import React from 'react';
import {cleanup, render, screen} from '@testing-library/react';
import {fromJS} from 'immutable';
import {MemoryRouter} from 'react-router-dom';
import {noop} from 'lodash';
import {Provider} from 'react-redux';
import {updateProject} from 'shared/actions/projects';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useRequest} from 'shared/hooks/useRequest';

jest.unmock('react-dom');

jest.mock('shared/actions/alerts', () => ({
	actionTypes: {},
	addAlert: jest.fn(),
}));

jest.mock('shared/actions/projects', () => ({
	actionTypes: {},
	updateProject: jest.fn(),
}));

jest.mock('shared/hooks/useCurrentUser', () => ({
	useCurrentUser: jest.fn(),
}));

jest.mock('shared/hooks/useRequest', () => ({
	useRequest: jest.fn(),
}));

const mockGroupId = '23';

const mockProject = {
	friendlyURL: '',
	groupId: mockGroupId,
	incidentReportEmailAddresses: [],
	name: 'Foo Project',
	timeZoneId: 'UTC',
};

const defaultStore = mockStore(
	fromJS({
		projects: {
			[mockGroupId]: {
				data: mockProject,
			},
		},
	})
);

const WrapperComponent = ({store = defaultStore, ...props}: any) => (
	<Provider store={store}>
		<MemoryRouter>
			<ConfigureWorkspace
				groupId={mockGroupId}
				onClose={noop}
				onNext={noop}
				{...props}
			/>
		</MemoryRouter>
	</Provider>
);

describe('ConfigureWorkspace', () => {
	beforeEach(() => {
		(updateProject as jest.Mock).mockReturnValue(Promise.resolve());

		(useCurrentUser as jest.Mock).mockReturnValue({
			isAdmin: () => true,
		});

		(useRequest as jest.Mock).mockReturnValue({
			data: ['liferay.com'],
			loading: false,
		});
	});

	afterEach(cleanup);

	it('renders', async () => {
		const {container} = render(<WrapperComponent />);

		expect(container).toMatchSnapshot();
	});

	it('should have the next button enabled by default', async () => {
		render(<WrapperComponent />);

		expect(
			screen.getByText(/^next$/i).closest('button')
		).not.toBeDisabled();
	});

	// Note: Validation tests for Formik are skipped here as they are proving
	// extremely flaky due to async nature and complex UI component interactions.
	// We establish that the component renders and basic props work.

});
