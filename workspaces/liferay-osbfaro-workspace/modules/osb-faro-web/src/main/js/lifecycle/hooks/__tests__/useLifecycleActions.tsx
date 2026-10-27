import * as API from 'shared/api';
import mockStore from 'test/mock-store';
import ModalRenderer from 'shared/components/ModalRenderer';
import React from 'react';
import {
	cleanup,
	fireEvent,
	render,
	screen,
	waitFor,
} from '@testing-library/react';
import {addAlert} from 'shared/actions/alerts';
import {Alert} from 'shared/types';
import {ILifecycle} from 'shared/api/lifecycle';
import {MemoryRouter, useLocation} from 'react-router-dom';
import {Provider} from 'react-redux';
import {Routes, toRoute} from 'shared/util/router';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useLifecycleActions} from '../useLifecycleActions';

jest.unmock('react-dom');

jest.mock('shared/actions/alerts', () => ({
	...jest.requireActual('shared/actions/alerts'),
	addAlert: jest.fn((payload) => ({payload, type: 'ADD_ALERT'})),
}));

jest.mock('shared/hooks/useCurrentUser', () => ({
	useCurrentUser: jest.fn(),
}));

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({channelId: '123', groupId: '23'}),
}));

const mockedUseCurrentUser = useCurrentUser as jest.Mock;

const ActionsProbe = ({
	lifecycle,
	onDefaultChange,
	onShowDetails,
	onView,
}: {
	lifecycle?: ILifecycle | null;
	onDefaultChange?: () => void;
	onShowDetails?: () => void;
	onView?: () => void;
}) => {
	const actions = useLifecycleActions({
		lifecycle,
		onDefaultChange,
		onShowDetails,
		onView,
	});

	return (
		<ul>
			{actions.map(({label, onClick}) => (
				<li key={label}>
					<button onClick={onClick} type="button">
						{label}
					</button>
				</li>
			))}
		</ul>
	);
};

const LocationProbe = () => (
	<div data-testid="location">{useLocation().pathname}</div>
);

const renderActions = (props: React.ComponentProps<typeof ActionsProbe>) =>
	render(
		<Provider store={mockStore()}>
			<ModalRenderer />

			<MemoryRouter>
				<ActionsProbe {...props} />

				<LocationProbe />
			</MemoryRouter>
		</Provider>
	);

const getLabels = () =>
	screen.getAllByRole('button').map(({textContent}) => textContent);

describe('useLifecycleActions', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		mockedUseCurrentUser.mockReturnValue({isAdmin: () => true});
	});

	afterEach(cleanup);

	it('returns no actions without a lifecycle', () => {
		renderActions({lifecycle: null});

		expect(screen.queryAllByRole('button')).toHaveLength(0);
	});

	it('lists every action in order for a non-default lifecycle', () => {
		renderActions({
			lifecycle: {
				defaultAccountLifecycle: false,
				externalReferenceCode: 'erc-1',
				id: '1',
			},
			onShowDetails: jest.fn(),
			onView: jest.fn(),
		});

		const labels = getLabels();

		expect(labels).toHaveLength(5);
		expect(labels[0]).toMatch(/^view$/i);
		expect(labels[1]).toMatch(/^edit$/i);
		expect(labels[2]).toMatch(/manage.notifications/i);
		expect(labels[3]).toMatch(/set.default/i);
		expect(labels[4]).toMatch(/show.details/i);
	});

	it('hides View and Show Details when no handler is given', () => {
		renderActions({
			lifecycle: {
				defaultAccountLifecycle: false,
				externalReferenceCode: 'erc-1',
				id: '1',
			},
		});

		expect(screen.queryByRole('button', {name: /^view$/i})).toBeNull();
		expect(
			screen.queryByRole('button', {name: /show.details/i})
		).toBeNull();
	});

	it('hides Edit for non-admins', () => {
		mockedUseCurrentUser.mockReturnValue({isAdmin: () => false});

		renderActions({lifecycle: {externalReferenceCode: 'erc-1', id: '1'}});

		expect(screen.queryByRole('button', {name: /^edit$/i})).toBeNull();
		expect(
			screen.getByRole('button', {name: /manage.notifications/i})
		).toBeInTheDocument();
	});

	it('hides Set Default on the default lifecycle', () => {
		renderActions({
			lifecycle: {
				defaultAccountLifecycle: true,
				externalReferenceCode: 'erc-1',
				id: '1',
			},
		});

		expect(screen.queryByRole('button', {name: /set.default/i})).toBeNull();
	});

	it('hides Set Default while the default is unknown', () => {
		renderActions({lifecycle: {externalReferenceCode: 'erc-1', id: '1'}});

		expect(screen.queryByRole('button', {name: /set.default/i})).toBeNull();
	});

	it('opens the lifecycle dashboard by its external reference code on View', () => {
		const onView = jest.fn();

		renderActions({
			lifecycle: {externalReferenceCode: 'erc-5', id: '5'},
			onView,
		});

		fireEvent.click(screen.getByRole('button', {name: /^view$/i}));

		expect(screen.getByTestId('location')).toHaveTextContent(
			toRoute(Routes.LIFECYCLE_DASHBOARD, {
				channelId: '123',
				groupId: '23',
				lifecycleId: 'erc-5',
			})
		);
		expect(onView).toHaveBeenCalled();
	});

	it('opens the lifecycle editor by its external reference code on Edit', () => {
		renderActions({lifecycle: {externalReferenceCode: 'erc-5', id: '5'}});

		fireEvent.click(screen.getByRole('button', {name: /^edit$/i}));

		expect(screen.getByTestId('location')).toHaveTextContent(
			toRoute(Routes.LIFECYCLE_EDIT, {
				channelId: '123',
				groupId: '23',
				lifecycleId: 'erc-5',
			})
		);
	});

	it('opens the notifications modal on Manage Notifications', () => {
		renderActions({lifecycle: {externalReferenceCode: 'erc-5', id: '5'}});

		fireEvent.click(
			screen.getByRole('button', {name: /manage.notifications/i})
		);

		expect(
			screen.getByText(/manage lifecycle notifications/i)
		).toBeInTheDocument();
	});

	it('sets the current lifecycle version as default on Set Default', async () => {
		const onDefaultChange = jest.fn();

		renderActions({
			lifecycle: {
				defaultAccountLifecycle: false,
				externalReferenceCode: 'erc-5',
				id: '5',
			},
			onDefaultChange,
		});

		fireEvent.click(screen.getByRole('button', {name: /set.default/i}));

		expect(API.lifecycle.setDefaultLifecycle).toHaveBeenCalledWith({
			groupId: '23',
			lifecycleId: '5',
		});

		await waitFor(() => expect(onDefaultChange).toHaveBeenCalled());

		expect(addAlert).toHaveBeenCalledWith(
			expect.objectContaining({alertType: Alert.Types.Success})
		);
	});

	it('calls the details handler on Show Details', () => {
		const onShowDetails = jest.fn();

		renderActions({
			lifecycle: {externalReferenceCode: 'erc-5', id: '5'},
			onShowDetails,
		});

		fireEvent.click(screen.getByRole('button', {name: /show.details/i}));

		expect(onShowDetails).toHaveBeenCalled();
	});
});
