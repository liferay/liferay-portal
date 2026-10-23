import * as API from 'shared/api';
import CreateLifecycle from '../CreateLifecycle';
import React from 'react';
import {Alert} from 'shared/types';
import {
	createStageCondition,
	createStageConfig,
	IStageConfig,
	MatchLogic,
} from 'lifecycle/utils/stageConfiguration';
import {LIFECYCLE_STAGE_DEFAULT_MAX_DAYS} from 'lifecycle/utils/constants';
import {
	fireEvent,
	render,
	screen,
	waitFor,
	within,
} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';
import {CATALOG_FIELDS_MAX_PAGE_SIZE} from 'shared/api/catalog';
import {Routes, toRoute} from 'shared/util/router';
import {useRequest} from 'shared/hooks/useRequest';
import {useUnsavedChangesPrompt} from 'shared/hooks/useUnsavedChangesPrompt';

jest.unmock('react-dom');

jest.mock('shared/hooks/useRequest', () => ({
	useRequest: jest.fn(),
}));

const mockDispatch = jest.fn();

jest.mock('react-redux', () => ({
	useDispatch: () => mockDispatch,
}));

jest.mock('shared/actions/alerts', () => ({
	addAlert: jest.fn((alert) => alert),
}));

jest.mock('lifecycle/utils/stageConfiguration', () => ({
	...jest.requireActual('lifecycle/utils/stageConfiguration'),
	createStageConfig: jest.fn(),
}));

const buildStage = (configured: boolean): IStageConfig => ({
	conditions: [
		{
			...createStageCondition(),
			conditionValue: configured ? '1000' : null,
			field: configured ? 'account.annualRevenue' : null,
			fieldDataCategory: configured ? 'Number' : null,
			fieldDataType: configured ? 'NUMERIC' : null,
			operator: configured ? 'gt' : null,
		},
	],
	description: configured ? 'A configured stage' : '',
	id: null,
	key: 'stage-key',
	matchLogic: MatchLogic.All,
	maxTimeDays: LIFECYCLE_STAGE_DEFAULT_MAX_DAYS,
	maxTimeEnabled: true,
	name: configured ? 'Aware' : '',
});

const mockedCreateStageConfig = createStageConfig as jest.Mock;

const mockPush = jest.fn();

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({channelId: '123', groupId: '23'}),
}));

jest.mock('shared/hooks/useUnsavedChangesPrompt', () => ({
	useUnsavedChangesPrompt: jest.fn(),
}));

jest.mock('shared/hooks/useCurrentUser', () => ({
	useCurrentUser: () => ({name: 'Anneta Smith'}),
}));

jest.mock('shared/hooks/useTimeZone', () => ({
	useTimeZone: () => ({timeZoneId: 'UTC'}),
}));

jest.mock('shared/hooks/useHistoryAdapter', () => ({
	useHistoryAdapter: () => ({push: mockPush}),
}));

const mockedUseRequest = useRequest as jest.Mock;

const renderPage = () =>
	render(
		<MemoryRouter>
			<CreateLifecycle />
		</MemoryRouter>
	);

const lifecycleURL = toRoute(Routes.LIFECYCLE, {
	channelId: '123',
	groupId: '23',
});

describe('CreateLifecycle', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		(API as any).catalog = {fetchCatalogFields: jest.fn()};

		(API as any).lifecycle = {
			createLifecycle: jest.fn().mockResolvedValue({}),
			fetchLifecycles: jest.fn(),
		};

		mockedCreateStageConfig.mockReturnValue(buildStage(false));

		mockedUseRequest.mockReturnValue({data: [], loading: false});
	});

	it('opens on the configuration summary with the new lifecycle toolbar', () => {
		renderPage();

		expect(
			screen.getByRole('heading', {level: 1, name: /new lifecycle/i})
		).toBeInTheDocument();
		expect(screen.getByRole('link', {name: /cancel/i})).toBeInTheDocument();
		expect(
			screen.getByRole('button', {name: /create lifecycle/i})
		).toBeInTheDocument();
		expect(
			screen.getByRole('heading', {name: /lifecycle conditions/i})
		).toBeInTheDocument();
		expect(
			screen.getByRole('group', {name: /configuration summary/i})
		).toHaveAttribute('aria-current', 'true');
	});

	it('starts with no stages and the empty state', () => {
		renderPage();

		expect(screen.getByText(/no stages yet/i)).toBeInTheDocument();
		expect(screen.queryByRole('group', {name: /^stage 1$/i})).toBeNull();
		expect(
			(screen.getByLabelText(/^stages/i) as HTMLInputElement).value
		).toMatch(/^0 stages$/i);
	});

	it('adds a stage from the empty state and selects it', () => {
		renderPage();

		fireEvent.click(screen.getByRole('button', {name: /^new$/i}));

		const stage = screen.getByRole('group', {name: /^stage 1$/i});

		expect(stage).toHaveTextContent(/stage 1.+no trigger/i);
		expect(stage).toHaveAttribute('aria-current', 'true');
		expect(screen.getByText(/unconfigured/i)).toBeInTheDocument();
		expect(screen.getByLabelText(/^name/i)).toHaveAttribute(
			'placeholder',
			expect.stringMatching(/new stage/i)
		);
	});

	it('shows the stage name on its chip as it is typed', () => {
		renderPage();

		fireEvent.click(screen.getByRole('button', {name: /add.stage/i}));

		fireEvent.change(screen.getByLabelText(/^name/i), {
			target: {value: 'Qualified'},
		});

		expect(
			screen.getByRole('group', {name: /^qualified$/i})
		).toHaveTextContent(/qualified/i);
	});

	it('shows the empty state and the summary again after removing the last stage', () => {
		renderPage();

		fireEvent.click(screen.getByRole('button', {name: /add.stage/i}));

		fireEvent.click(screen.getByRole('button', {name: /remove.stage 1/i}));

		expect(screen.getByText(/no stages yet/i)).toBeInTheDocument();
		expect(
			screen.getByRole('group', {name: /configuration summary/i})
		).toHaveAttribute('aria-current', 'true');
		expect(
			(screen.getByLabelText(/^stages/i) as HTMLInputElement).value
		).toMatch(/^0 stages$/i);
	});

	it('summarizes a new lifecycle with the current user and no date', () => {
		renderPage();

		expect(screen.getByLabelText(/^name/i)).toHaveAttribute(
			'placeholder',
			expect.stringMatching(/new lifecycle/i)
		);
		expect(screen.getByLabelText(/last modified by/i)).toHaveValue(
			'Anneta Smith'
		);
		expect(screen.getByLabelText(/modified date/i)).toHaveValue('');
	});

	it('renders the name input and updates its value', () => {
		renderPage();

		const nameInput = screen.getByLabelText(/^name/i);

		fireEvent.change(nameInput, {target: {value: 'My Lifecycle'}});

		fireEvent.change(screen.getByLabelText(/^description/i), {
			target: {value: 'Qualified accounts'},
		});

		expect(nameInput).toHaveValue('My Lifecycle');
		expect(
			screen.getByRole('group', {name: /configuration summary/i})
		).toHaveTextContent(/my lifecycle/i);
	});

	it('links cancel and back to the dashboard', () => {
		renderPage();

		expect(screen.getByRole('link', {name: /cancel/i})).toHaveAttribute(
			'href',
			lifecycleURL
		);
		expect(screen.getByRole('link', {name: /back/i})).toHaveAttribute(
			'href',
			lifecycleURL
		);
	});

	it('blocks Create and lists every error of the lifecycle in an alert', () => {
		renderPage();

		fireEvent.click(
			screen.getByRole('button', {name: /create lifecycle/i})
		);

		expect(API.lifecycle.createLifecycle).not.toHaveBeenCalled();
		expect(
			screen.getByText(/review 3 errors in this configuration/i)
		).toBeInTheDocument();
		expect(
			screen.getByText(/the lifecycle name field is required/i)
		).toBeInTheDocument();
		expect(
			screen.getByText(/the lifecycle description field is required/i)
		).toBeInTheDocument();
		expect(screen.getByText(/add at least one stage/i)).toBeInTheDocument();
		expect(
			screen.getByText(/^the name field is required/i)
		).toBeInTheDocument();
	});

	it('dismisses the errors alert', () => {
		renderPage();

		fireEvent.click(
			screen.getByRole('button', {name: /create lifecycle/i})
		);

		fireEvent.click(
			within(
				screen.getByText(/review 3 errors/i).closest('.alert')!
			).getByRole('button', {name: /close/i})
		);

		expect(screen.queryByText(/review 3 errors/i)).toBeNull();
	});

	it('lists only the selected stage errors on its page', () => {
		renderPage();

		fireEvent.click(screen.getByRole('button', {name: /add.stage/i}));

		fireEvent.click(
			screen.getByRole('button', {name: /create lifecycle/i})
		);

		expect(API.lifecycle.createLifecycle).not.toHaveBeenCalled();
		expect(
			screen.getByText(/review 3 errors in this configuration/i)
		).toBeInTheDocument();
		expect(
			screen.getByText(/the stage name field is required/i)
		).toBeInTheDocument();
		expect(
			screen.getByText(/stage triggers are required/i)
		).toBeInTheDocument();
		expect(
			screen.queryByText(/the lifecycle name field is required/i)
		).toBeNull();
	});

	it('asks for unique stage names on the page of a duplicate stage', () => {
		mockedCreateStageConfig
			.mockReturnValueOnce({...buildStage(true), key: 'first'})
			.mockReturnValueOnce({...buildStage(true), key: 'second'});

		renderPage();

		fireEvent.click(screen.getByRole('button', {name: /add.stage/i}));
		fireEvent.click(screen.getByRole('button', {name: /add.stage/i}));

		fireEvent.click(
			screen.getByRole('button', {name: /create lifecycle/i})
		);

		expect(API.lifecycle.createLifecycle).not.toHaveBeenCalled();
		expect(
			screen.getByText(/review 1 error in this configuration/i)
		).toBeInTheDocument();
		expect(
			screen.getByText(/stage names should be unique/i)
		).toBeInTheDocument();
		expect(screen.getByText(/enter a unique name/i)).toBeInTheDocument();

		screen
			.getAllByRole('group', {name: /^aware$/i})
			.forEach((chip) => expect(chip).toHaveTextContent(/error/i));
	});

	it('stops the lifecycle name at the character limit', () => {
		renderPage();

		const nameInput = screen.getByLabelText(/^name/i);

		expect(nameInput).toHaveAttribute('maxLength', '24');

		fireEvent.change(nameInput, {target: {value: 'a'.repeat(23)}});

		expect(
			screen.queryByText(/the character limit has been reached/i)
		).toBeNull();

		fireEvent.change(nameInput, {target: {value: 'a'.repeat(24)}});

		expect(
			screen.getByText(/the character limit has been reached/i)
		).toBeInTheDocument();
	});

	it('stops the lifecycle description at the character limit', () => {
		renderPage();

		const descriptionInput = screen.getByLabelText(/^description/i);

		expect(descriptionInput).toHaveAttribute('maxLength', '120');

		fireEvent.change(descriptionInput, {
			target: {value: 'a'.repeat(120)},
		});

		expect(
			screen.getByText(/the character limit has been reached/i)
		).toBeInTheDocument();
	});

	it('shows the error icon on the summary chip until the name and description are filled', () => {
		renderPage();

		const summary = () =>
			screen.getByRole('group', {name: /configuration summary/i});

		expect(summary()).toHaveTextContent(/error/i);

		fireEvent.change(screen.getByLabelText(/^name/i), {
			target: {value: 'My Lifecycle'},
		});

		expect(summary()).toHaveTextContent(/error/i);

		fireEvent.change(screen.getByLabelText(/^description/i), {
			target: {value: 'Qualified accounts'},
		});

		expect(summary()).not.toHaveTextContent(/error/i);
	});

	it('shows the error icon on a new stage chip only after a save attempt', () => {
		renderPage();

		fireEvent.click(screen.getByRole('button', {name: /add.stage/i}));

		expect(
			screen.getByRole('group', {name: /^stage 1$/i})
		).not.toHaveTextContent(/error/i);

		fireEvent.click(
			screen.getByRole('button', {name: /create lifecycle/i})
		);

		expect(
			screen.getByRole('group', {name: /^stage 1$/i})
		).toHaveTextContent(/error/i);
	});

	it('names a new stage "New Stage" in the breadcrumb', () => {
		renderPage();

		fireEvent.click(screen.getByRole('button', {name: /add.stage/i}));

		expect(
			screen.getByRole('navigation', {name: /breadcrumb/i})
		).toHaveTextContent(/new stage/i);
	});

	it('shows the name error once the empty name is left', () => {
		renderPage();

		const nameInput = screen.getByLabelText(/^name/i);

		expect(screen.queryByText(/^the name field is required/i)).toBeNull();

		fireEvent.blur(nameInput);

		expect(
			screen.getByText(/^the name field is required/i)
		).toBeInTheDocument();
		expect(nameInput).toHaveAttribute(
			'aria-describedby',
			'lifecycleNameError'
		);
	});

	it('shows the description error once the empty description is left', () => {
		renderPage();

		const descriptionInput = screen.getByLabelText(/^description/i);

		fireEvent.blur(descriptionInput);

		expect(
			screen.getByText(/the description field is required/i)
		).toBeInTheDocument();
		expect(descriptionInput).toHaveAttribute(
			'aria-describedby',
			'lifecycleDescriptionError'
		);
	});

	it('warns about unsaved changes only once something changed', () => {
		const mockedPrompt = useUnsavedChangesPrompt as jest.Mock;

		renderPage();

		expect(mockedPrompt).toHaveBeenLastCalledWith(false, expect.anything());

		fireEvent.change(screen.getByLabelText(/^name/i), {
			target: {value: 'My Lifecycle'},
		});

		expect(mockedPrompt).toHaveBeenLastCalledWith(true, expect.anything());

		fireEvent.change(screen.getByLabelText(/^name/i), {
			target: {value: ''},
		});

		expect(mockedPrompt).toHaveBeenLastCalledWith(false, expect.anything());
	});

	it('renders a loading state while lifecycles are loading', () => {
		mockedUseRequest.mockReturnValue({data: undefined, loading: true});

		renderPage();

		expect(screen.queryByText(/new lifecycle/i)).toBeNull();
	});

	it('redirects to a 404 when a lifecycle already exists', () => {
		mockedUseRequest.mockReturnValue({
			data: [{id: '1'}],
			loading: false,
		});

		renderPage();

		expect(screen.queryByText(/new lifecycle/i)).toBeNull();
	});

	it('checks for lifecycles in the current channel', () => {
		renderPage();

		expect(mockedUseRequest).toHaveBeenCalledWith(
			expect.objectContaining({
				dataSourceFn: API.lifecycle.fetchLifecycles,
				variables: {channelId: '123', groupId: '23'},
			})
		);
	});

	it('requests the entire catalog in a single page', () => {
		renderPage();

		expect(mockedUseRequest).toHaveBeenCalledWith(
			expect.objectContaining({
				dataSourceFn: (API as any).catalog.fetchCatalogFields,
				variables: expect.objectContaining({
					pageSize: CATALOG_FIELDS_MAX_PAGE_SIZE,
					tableName: 'account',
				}),
			})
		);
	});

	it('surfaces an error state when the catalog request fails', () => {
		const refetch = jest.fn();

		mockedUseRequest.mockImplementation(({dataSourceFn}: any) =>
			dataSourceFn === (API as any).catalog.fetchCatalogFields
				? {data: null, error: true, loading: false, refetch}
				: {data: [], error: false, loading: false}
		);

		renderPage();

		expect(
			screen.getByText(/an unexpected error occurred/i)
		).toBeInTheDocument();
		expect(screen.queryByText(/lifecycle conditions/i)).toBeNull();
		expect(mockDispatch).toHaveBeenCalled();

		fireEvent.click(screen.getByRole('button', {name: /reload/i}));

		expect(refetch).toHaveBeenCalled();
	});

	it('sends a single create request while one is in flight', async () => {
		mockedCreateStageConfig.mockReturnValue(buildStage(true));

		(API.lifecycle.createLifecycle as jest.Mock).mockReturnValue(
			new Promise(() => {})
		);

		renderPage();

		fireEvent.change(screen.getByLabelText(/^name/i), {
			target: {value: 'My Lifecycle'},
		});

		fireEvent.change(screen.getByLabelText(/^description/i), {
			target: {value: 'Qualified accounts'},
		});

		fireEvent.click(screen.getByRole('button', {name: /add.stage/i}));

		const createButton = screen.getByRole('button', {
			name: /create lifecycle/i,
		});

		fireEvent.click(createButton);
		fireEvent.click(createButton);

		await waitFor(() => expect(createButton).toBeDisabled());

		expect(API.lifecycle.createLifecycle).toHaveBeenCalledTimes(1);
	});

	it('creates the lifecycle with the contract payload, alerts success, and navigates to the dashboard', async () => {
		mockedCreateStageConfig.mockReturnValue(buildStage(true));

		renderPage();

		fireEvent.change(screen.getByLabelText(/^name/i), {
			target: {value: 'My Lifecycle'},
		});

		fireEvent.change(screen.getByLabelText(/^description/i), {
			target: {value: 'Qualified accounts'},
		});

		fireEvent.click(screen.getByRole('button', {name: /add.stage/i}));

		fireEvent.click(
			screen.getByRole('button', {name: /create lifecycle/i})
		);

		await waitFor(() =>
			expect(API.lifecycle.createLifecycle).toHaveBeenCalled()
		);

		const payload = (API.lifecycle.createLifecycle as jest.Mock).mock
			.calls[0][0];

		expect(payload).toEqual(
			expect.objectContaining({
				channelId: '123',
				description: 'Qualified accounts',
				name: 'My Lifecycle',
			})
		);
		expect(payload.stages).toEqual([
			expect.objectContaining({priority: 1, stageName: 'Aware'}),
		]);
		expect(payload.stages[0]).not.toHaveProperty('stageType');
		expect(payload.stages[0].accountLifecycleStageRule.name).toBe(
			'My Lifecycle Stage Aware Criteria'
		);

		await waitFor(() =>
			expect(mockPush).toHaveBeenCalledWith(lifecycleURL)
		);

		expect(mockDispatch).toHaveBeenCalledWith(
			expect.objectContaining({alertType: Alert.Types.Success})
		);
	});

	it('alerts an error and does not navigate when creation fails', async () => {
		mockedCreateStageConfig.mockReturnValue(buildStage(true));

		(API.lifecycle.createLifecycle as jest.Mock).mockRejectedValue(
			new Error('failed')
		);

		renderPage();

		fireEvent.change(screen.getByLabelText(/^name/i), {
			target: {value: 'My Lifecycle'},
		});

		fireEvent.change(screen.getByLabelText(/^description/i), {
			target: {value: 'Qualified accounts'},
		});

		fireEvent.click(screen.getByRole('button', {name: /add.stage/i}));

		fireEvent.click(
			screen.getByRole('button', {name: /create lifecycle/i})
		);

		await waitFor(() =>
			expect(mockDispatch).toHaveBeenCalledWith(
				expect.objectContaining({alertType: Alert.Types.Error})
			)
		);

		expect(mockPush).not.toHaveBeenCalled();
	});
});
