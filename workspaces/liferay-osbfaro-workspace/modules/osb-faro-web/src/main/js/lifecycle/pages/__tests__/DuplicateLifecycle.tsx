import * as API from 'shared/api';
import DuplicateLifecycle from '../DuplicateLifecycle';
import mockStore from 'test/mock-store';
import React from 'react';
import {Alert} from 'shared/types';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import {ILifecycleStage} from 'shared/api/lifecycle';
import {MemoryRouter} from 'react-router-dom';
import {Provider} from 'react-redux';
import {Routes, toRoute} from 'shared/util/router';
import {useLifecycleAccountData} from 'lifecycle/hooks/useLifecycleAccountData';
import {useRequest} from 'shared/hooks/useRequest';
import {useUnsavedChangesPrompt} from 'shared/hooks/useUnsavedChangesPrompt';
import {AccountDataStatus} from 'lifecycle/utils/types';

jest.unmock('react-dom');

jest.mock('shared/hooks/useRequest', () => ({
	useRequest: jest.fn(),
}));

const mockDispatch = jest.fn();

jest.mock('react-redux', () => ({
	...jest.requireActual('react-redux'),
	useDispatch: () => mockDispatch,
}));

jest.mock('lifecycle/hooks/useLifecycleAccountData', () => ({
	useLifecycleAccountData: jest.fn(),
}));

jest.mock('shared/hooks/useCurrentUser', () => ({
	useCurrentUser: () => ({isAdmin: () => true, name: 'Anneta Smith'}),
}));

jest.mock('shared/actions/alerts', () => ({
	...jest.requireActual('shared/actions/alerts'),
	addAlert: jest.fn((alert) => alert),
}));

const mockPush = jest.fn();

const mockReplace = jest.fn();

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		channelId: '123',
		groupId: '23',
		sourceLifecycleId: 'erc-9',
	}),
}));

jest.mock('shared/hooks/useUnsavedChangesPrompt', () => ({
	useUnsavedChangesPrompt: jest.fn(),
}));

jest.mock('shared/hooks/useTimeZone', () => ({
	useTimeZone: () => ({timeZoneId: 'UTC'}),
}));

jest.mock('shared/hooks/useHistoryAdapter', () => ({
	useHistoryAdapter: () => ({push: mockPush, replace: mockReplace}),
}));

const mockedUseRequest = useRequest as jest.Mock;

const STAGE_NAMES = ['Aware', 'Qualified', 'Pipeline'];

const buildStages = (): ILifecycleStage[] =>
	STAGE_NAMES.map((name, index) => ({
		accountLifecycleStageRule: {
			filterMetadata: JSON.stringify({
				conditionValue: '1000',
				field: 'account.annualRevenue',
				fieldDataCategory: 'Number',
				fieldDataType: 'NUMERIC',
				operator: 'gt',
			}),
			filterString: '(account.annualRevenue gt 1000)',
		},
		description: `Stage ${index + 1}`,
		externalReferenceCode: `erc-stage-${index + 1}`,
		id: `stage-${index}`,
		maxDuration: 30,
		name,
		position: index + 1,
	})).reverse();

const lifecycle = {
	accountLifecycleStages: buildStages(),
	authorName: 'Someone Else',
	defaultAccountLifecycle: true,
	description: 'Lifecycle for qualified accounts',
	externalReferenceCode: 'erc-9',
	id: '9',
	modifiedDate: 1791556330000,
	name: 'Growth and Retention Hub',
	processedDate: 1700000000000,
	segmentId: '456',
};

let existingLifecycleNames: string[];

const mockRequests = (props: any) => {
	if (props.dataSourceFn === (API as any).catalog.fetchCatalogFields) {
		return {data: {items: []}, error: false, loading: false};
	}

	if (props.dataSourceFn === (API as any).lifecycle.fetchLifecyclesPage) {
		return {
			data: {items: existingLifecycleNames.map((name) => ({name}))},
			error: false,
			loading: false,
		};
	}

	return {data: lifecycle, error: false, loading: false};
};

const renderPage = () =>
	render(
		<Provider store={mockStore()}>
			<MemoryRouter>
				<DuplicateLifecycle />
			</MemoryRouter>
		</Provider>
	);

const clickCreate = () =>
	fireEvent.click(screen.getByRole('button', {name: /create lifecycle/i}));

const sourceURL = toRoute(Routes.LIFECYCLE_DASHBOARD, {
	channelId: '123',
	groupId: '23',
	lifecycleId: 'erc-9',
});

describe('DuplicateLifecycle', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		(API as any).catalog = {fetchCatalogFields: jest.fn()};

		(API as any).lifecycle = {
			createLifecycle: jest.fn().mockResolvedValue({
				externalReferenceCode: 'erc-10',
				id: '10',
				name: 'Growth and Retention Hub (1)',
			}),
			fetchLifecycle: jest.fn(),
			fetchLifecyclesPage: jest.fn().mockResolvedValue({
				items: [
					{
						externalReferenceCode: 'erc-9',
						id: '9',
						name: 'Growth and Retention Hub',
					},
				],
			}),
			updateLifecycle: jest.fn(),
		};

		existingLifecycleNames = ['Growth and Retention Hub'];

		mockedUseRequest.mockImplementation(mockRequests);

		(useLifecycleAccountData as jest.Mock).mockReturnValue({
			refetch: jest.fn(),
			status: AccountDataStatus.Ready,
		});
	});

	it('loads the original lifecycle by its external reference code', () => {
		renderPage();

		expect(mockedUseRequest).toHaveBeenCalledWith(
			expect.objectContaining({
				dataSourceFn: API.lifecycle.fetchLifecycle,
				variables: {groupId: '23', lifecycleId: 'erc-9'},
			})
		);
	});

	it('opens the new lifecycle editor prefilled with a copy of the original', () => {
		renderPage();

		expect(
			screen.getByRole('heading', {level: 1, name: /new lifecycle/i})
		).toBeInTheDocument();
		expect(
			screen.getByRole('button', {name: /create lifecycle/i})
		).toBeInTheDocument();
		expect(screen.getByLabelText(/^name/i)).toHaveValue(
			'Growth and Retention Hub (1)'
		);
		expect(screen.getByLabelText(/^description/i)).toHaveValue(
			'Lifecycle for qualified accounts'
		);
		expect(
			screen
				.getAllByRole('group', {name: /^(aware|qualified|pipeline)$/i})
				.map((chip) => chip.getAttribute('aria-label'))
		).toEqual(STAGE_NAMES);
	});

	it('looks up the existing copies of the original in its channel', () => {
		renderPage();

		expect(mockedUseRequest).toHaveBeenCalledWith(
			expect.objectContaining({
				dataSourceFn: API.lifecycle.fetchLifecyclesPage,
				skipRequest: false,
				variables: expect.objectContaining({
					channelId: '123',
					groupId: '23',
					search: 'Growth and Retention Hub',
				}),
			})
		);
	});

	it('prefills the next free copy number when copies already exist', () => {
		existingLifecycleNames = [
			'Growth and Retention Hub',
			'Growth and Retention Hub (1)',
		];

		renderPage();

		expect(screen.getByLabelText(/^name/i)).toHaveValue(
			'Growth and Retention Hub (2)'
		);
	});

	it('waits for the existing names before it opens the editor', () => {
		mockedUseRequest.mockImplementation((props: any) =>
			props.dataSourceFn === API.lifecycle.fetchLifecyclesPage
				? {data: null, error: false, loading: true}
				: mockRequests(props)
		);

		renderPage();

		expect(screen.queryByLabelText(/^name/i)).not.toBeInTheDocument();
	});

	it('summarizes the copy with the current user and no date', () => {
		renderPage();

		expect(screen.getByLabelText(/last modified by/i)).toHaveValue(
			'Anneta Smith'
		);
		expect(screen.getByLabelText(/modified date/i)).toHaveValue('');
	});

	it('links cancel and back to the original dashboard', () => {
		renderPage();

		expect(screen.getByRole('link', {name: /cancel/i})).toHaveAttribute(
			'href',
			sourceURL
		);
		expect(screen.getByRole('link', {name: /back/i})).toHaveAttribute(
			'href',
			sourceURL
		);
	});

	it('leaves without a prompt until the copy is changed', () => {
		const mockedPrompt = useUnsavedChangesPrompt as jest.Mock;

		renderPage();

		expect(mockedPrompt).toHaveBeenLastCalledWith(false, expect.anything());

		fireEvent.change(screen.getByLabelText(/^name/i), {
			target: {value: 'Renamed Hub'},
		});

		expect(mockedPrompt).toHaveBeenLastCalledWith(true, expect.anything());
		expect(API.lifecycle.createLifecycle).not.toHaveBeenCalled();
	});

	it('creates a new lifecycle with copied stages and the original segment', async () => {
		renderPage();

		clickCreate();

		await waitFor(() =>
			expect(API.lifecycle.createLifecycle).toHaveBeenCalled()
		);

		const payload = (API.lifecycle.createLifecycle as jest.Mock).mock
			.calls[0][0];

		expect(payload).toEqual(
			expect.objectContaining({
				channelId: '123',
				description: 'Lifecycle for qualified accounts',
				groupId: '23',
				name: 'Growth and Retention Hub (1)',
				segmentId: '456',
			})
		);
		expect(payload).not.toHaveProperty('defaultAccountLifecycle');
		expect(
			payload.stages.map(
				({description, maxDuration, name, position}: any) => ({
					description,
					maxDuration,
					name,
					position,
				})
			)
		).toEqual([
			{
				description: 'Stage 1',
				maxDuration: 30,
				name: 'Aware',
				position: 1,
			},
			{
				description: 'Stage 2',
				maxDuration: 30,
				name: 'Qualified',
				position: 2,
			},
			{
				description: 'Stage 3',
				maxDuration: 30,
				name: 'Pipeline',
				position: 3,
			},
		]);
		payload.stages.forEach((stage: any) => {
			expect(stage).not.toHaveProperty('id');
			expect(stage.accountLifecycleStageRule).toEqual(
				expect.objectContaining({
					filterString: '(account.annualRevenue gt 1000)',
					name: `Growth and Retention Hub (1) Stage ${stage.name} Criteria`,
				})
			);
		});
		expect(API.lifecycle.updateLifecycle).not.toHaveBeenCalled();
	});

	it('alerts success and opens the dashboard of the copy', async () => {
		renderPage();

		clickCreate();

		await waitFor(() =>
			expect(mockReplace).toHaveBeenCalledWith(
				toRoute(Routes.LIFECYCLE_DASHBOARD, {
					channelId: '123',
					groupId: '23',
					lifecycleId: 'erc-10',
				})
			)
		);

		expect(mockDispatch).toHaveBeenCalledWith(
			expect.objectContaining({
				alertType: Alert.Types.Success,
				message: expect.stringMatching(
					/growth and retention hub \(1\).+created/i
				),
			})
		);
	});

	it('keeps the editor open with an error toast when creation fails', async () => {
		(API.lifecycle.createLifecycle as jest.Mock).mockRejectedValue(
			new Error('failed')
		);

		renderPage();

		clickCreate();

		await waitFor(() =>
			expect(mockDispatch).toHaveBeenCalledWith(
				expect.objectContaining({
					alertType: Alert.Types.Error,
					message: expect.stringMatching(/creation process failed/i),
				})
			)
		);

		expect(mockReplace).not.toHaveBeenCalled();
		expect(screen.getByLabelText(/^name/i)).toHaveValue(
			'Growth and Retention Hub (1)'
		);
	});

	it('asks for a unique name when the copy takes the original name', async () => {
		renderPage();

		fireEvent.change(screen.getByLabelText(/^name/i), {
			target: {value: 'Growth and Retention Hub'},
		});

		clickCreate();

		expect(
			await screen.findByText(/this lifecycle name is currently in use/i)
		).toBeInTheDocument();
		expect(API.lifecycle.createLifecycle).not.toHaveBeenCalled();
	});

	it('shows the dashboard not found state when the original is missing', () => {
		mockedUseRequest.mockImplementation((props: any) =>
			props.dataSourceFn === (API as any).catalog.fetchCatalogFields
				? {data: {items: []}, error: false, loading: false}
				: {data: null, error: true, loading: false}
		);

		renderPage();

		expect(screen.queryByText(/new lifecycle/i)).toBeNull();
		expect(
			screen.getByText(/dashboard was not found/i)
		).toBeInTheDocument();
	});

	it('shows the empty state when the channel has no account data', () => {
		(useLifecycleAccountData as jest.Mock).mockReturnValue({
			refetch: jest.fn(),
			status: AccountDataStatus.NoAccountData,
		});

		renderPage();

		expect(
			screen.getByText(/no account data available/i)
		).toBeInTheDocument();
		expect(
			screen.queryByRole('button', {name: /create lifecycle/i})
		).toBeNull();
	});
});
