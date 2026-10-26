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
	within,
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

const mockUseParams = jest.fn();

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => mockUseParams(),
}));

const mockedUseCurrentUser = useCurrentUser as jest.Mock;

const ActionsProbe = ({
	lifecycle,
	onDefaultChange,
	onDelete,
	onShowDetails,
	onView,
}: {
	lifecycle?: ILifecycle | null;
	onDefaultChange?: () => void;
	onDelete?: () => void;
	onShowDetails?: () => void;
	onView?: () => void;
}) => {
	const {actions, manageNotificationsModal} = useLifecycleActions({
		lifecycle,
		onDefaultChange,
		onDelete,
		onShowDetails,
		onView,
	});

	return (
		<>
			<ul>
				{actions.map(({className, label, onClick}) => (
					<li key={label}>
						<button
							className={className}
							onClick={onClick}
							type="button"
						>
							{label}
						</button>
					</li>
				))}
			</ul>

			{manageNotificationsModal}
		</>
	);
};

const LocationProbe = () => (
	<div data-testid="location">{useLocation().pathname}</div>
);

const renderActions = (props: React.ComponentProps<typeof ActionsProbe>) =>
	render(
		<Provider store={mockStore()}>
			<ModalRenderer />

			<MemoryRouter initialEntries={['/workspace/23/123/lifecycle/1']}>
				<ActionsProbe {...props} />

				<LocationProbe />
			</MemoryRouter>
		</Provider>
	);

const getLabels = () =>
	screen
		.getAllByRole('button')
		.filter((button) => !button.closest('.modal-renderer-root'))
		.map(({textContent}) => textContent);

describe('useLifecycleActions', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		mockedUseCurrentUser.mockReturnValue({isAdmin: () => true});

		mockUseParams.mockReturnValue({channelId: '123', groupId: '23'});
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

		expect(labels).toHaveLength(6);
		expect(labels[0]).toMatch(/^view$/i);
		expect(labels[1]).toMatch(/^edit$/i);
		expect(labels[2]).toMatch(/manage.notifications/i);
		expect(labels[3]).toMatch(/set.default/i);
		expect(labels[4]).toMatch(/show.details/i);
		expect(labels[5]).toMatch(/^delete$/i);
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

	it('opens the notifications modal on Manage Notifications', async () => {
		renderActions({lifecycle: {externalReferenceCode: 'erc-5', id: '5'}});

		fireEvent.click(
			screen.getByRole('button', {name: /manage.notifications/i})
		);

		expect(
			await screen.findByText(/manage lifecycle notifications/i)
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

	describe('the Delete action', () => {
		const getDialog = () => screen.getByRole('dialog');

		const openDeleteModal = () =>
			fireEvent.click(screen.getByRole('button', {name: /^delete$/i}));

		const confirmDelete = () =>
			fireEvent.click(
				within(getDialog()).getByRole('button', {name: /^delete$/i})
			);

		const growthHub = {
			defaultAccountLifecycle: false,
			externalReferenceCode: 'erc-5',
			id: '5',
			name: 'Growth Hub',
		};

		it('shows in red', () => {
			renderActions({lifecycle: growthHub});

			expect(screen.getByRole('button', {name: /^delete$/i})).toHaveClass(
				'text-danger'
			);
		});

		it('shows on the default lifecycle', () => {
			renderActions({
				lifecycle: {...growthHub, defaultAccountLifecycle: true},
			});

			const labels = getLabels();

			expect(labels[labels.length - 1]).toMatch(/^delete$/i);
			expect(
				screen.queryByRole('button', {name: /set.default/i})
			).toBeNull();
		});

		it('shows while the default is unknown', () => {
			renderActions({
				lifecycle: {
					externalReferenceCode: 'erc-5',
					id: '5',
					name: 'Growth Hub',
				},
			});

			expect(
				screen.getByRole('button', {name: /^delete$/i})
			).toBeInTheDocument();
		});

		it('hands the deleted default lifecycle to the caller', async () => {
			const onDelete = jest.fn();

			const defaultLifecycle = {
				...growthHub,
				defaultAccountLifecycle: true,
			};

			renderActions({lifecycle: defaultLifecycle, onDelete});

			openDeleteModal();

			confirmDelete();

			await waitFor(() =>
				expect(onDelete).toHaveBeenCalledWith(defaultLifecycle)
			);
		});

		it('is hidden for non-admins', () => {
			mockedUseCurrentUser.mockReturnValue({isAdmin: () => false});

			renderActions({lifecycle: growthHub});

			expect(
				screen.queryByRole('button', {name: /^delete$/i})
			).toBeNull();
		});

		it('asks for confirmation with the lifecycle name in quotes', () => {
			renderActions({lifecycle: growthHub});

			openDeleteModal();

			expect(getDialog()).toHaveClass('modal-danger');
			expect(getDialog()).toHaveTextContent(/delete "growth hub"\?/i);
			expect(getDialog()).toHaveTextContent(
				/delete the lifecycle "growth hub".+account data will not be removed/i
			);
			expect(API.lifecycle.deleteLifecycle).not.toHaveBeenCalled();
		});

		it('sends nothing when cancelled', () => {
			renderActions({lifecycle: growthHub});

			openDeleteModal();

			fireEvent.click(
				within(getDialog()).getByRole('button', {name: /cancel/i})
			);

			expect(screen.queryByRole('dialog')).toBeNull();
			expect(API.lifecycle.deleteLifecycle).not.toHaveBeenCalled();
		});

		it('deletes another lifecycle and lets the caller reload', async () => {
			const onDelete = jest.fn();

			renderActions({lifecycle: growthHub, onDelete});

			openDeleteModal();

			confirmDelete();

			expect(API.lifecycle.deleteLifecycle).toHaveBeenCalledTimes(1);
			expect(API.lifecycle.deleteLifecycle).toHaveBeenCalledWith({
				channelId: '123',
				groupId: '23',
				lifecycleId: '5',
			});

			await waitFor(() =>
				expect(onDelete).toHaveBeenCalledWith(growthHub)
			);

			expect(addAlert).toHaveBeenCalledWith(
				expect.objectContaining({
					alertType: Alert.Types.Success,
					message: expect.stringMatching(/growth hub was deleted/i),
				})
			);
			expect(screen.getByTestId('location')).toHaveTextContent(
				'/workspace/23/123/lifecycle/1'
			);
		});

		it('opens the default lifecycle after deleting the open one', async () => {
			const onDelete = jest.fn();

			mockUseParams.mockReturnValue({
				channelId: '123',
				groupId: '23',
				lifecycleId: 'erc-5',
			});

			renderActions({lifecycle: growthHub, onDelete});

			openDeleteModal();

			confirmDelete();

			await waitFor(() =>
				expect(screen.getByTestId('location')).toHaveTextContent(
					new RegExp(
						`^${toRoute(Routes.LIFECYCLE, {
							channelId: '123',
							groupId: '23',
						})}$`
					)
				)
			);

			expect(onDelete).not.toHaveBeenCalled();
		});

		it('alerts an error when the delete fails', async () => {
			const onDelete = jest.fn();

			(API.lifecycle.deleteLifecycle as jest.Mock).mockImplementationOnce(
				() => Promise.reject(new Error('failed'))
			);

			renderActions({lifecycle: growthHub, onDelete});

			openDeleteModal();

			confirmDelete();

			await waitFor(() =>
				expect(addAlert).toHaveBeenCalledWith(
					expect.objectContaining({
						alertType: Alert.Types.Error,
						message: expect.stringMatching(
							/delete process failed/i
						),
					})
				)
			);

			expect(onDelete).not.toHaveBeenCalled();
		});
	});
});
