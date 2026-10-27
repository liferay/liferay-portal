import * as API from 'shared/api';
import EditLifecycle from '../EditLifecycle';
import React from 'react';
import {Alert} from 'shared/types';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import {ILifecycleStage} from 'shared/api/lifecycle';
import {MemoryRouter} from 'react-router-dom';
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

const mockPush = jest.fn();

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({channelId: '123', groupId: '23', lifecycleId: 'erc-9'}),
}));

jest.mock('shared/hooks/useUnsavedChangesPrompt', () => ({
	useUnsavedChangesPrompt: jest.fn(),
}));

jest.mock('shared/hooks/useTimeZone', () => ({
	useTimeZone: () => ({timeZoneId: 'UTC'}),
}));

jest.mock('shared/hooks/useHistoryAdapter', () => ({
	useHistoryAdapter: () => ({push: mockPush}),
}));

const mockedUseRequest = useRequest as jest.Mock;

const STAGE_NAMES = ['Aware', 'Qualified', 'Pipeline'];

const buildStages = (): ILifecycleStage[] =>
	STAGE_NAMES.map((stageName, index) => ({
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
		description: 'A configured stage',
		externalReferenceCode: `erc-stage-${index + 1}`,
		id: `stage-${index}`,
		maxDuration: 30,
		priority: index + 1,
		stageName,
	})).reverse();

const lifecycle = {
	authorName: 'Anneta Smith',
	description: 'Lifecycle for qualified accounts',
	externalReferenceCode: 'erc-9',
	id: '9',
	modifiedDate: 1791556330000,
	name: 'Growth and Retention Hub',
	processedDate: 1700000000000,
	stages: buildStages(),
};

const mockCatalog =
	(error = false, refetch = jest.fn()) =>
	(props: any) => {
		if (props.dataSourceFn === (API as any).catalog.fetchCatalogFields) {
			return {
				data: error ? null : {items: []},
				error,
				loading: false,
				refetch,
			};
		}

		return {data: lifecycle, error: false, loading: false};
	};

const renderPage = () =>
	render(
		<MemoryRouter>
			<EditLifecycle />
		</MemoryRouter>
	);

const lifecycleURL = toRoute(Routes.LIFECYCLE_DASHBOARD, {
	channelId: '123',
	groupId: '23',
	lifecycleId: 'erc-9',
});

describe('EditLifecycle', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		(API as any).catalog = {fetchCatalogFields: jest.fn()};

		(API as any).lifecycle = {
			fetchLifecycle: jest.fn(),
			updateLifecycle: jest.fn().mockResolvedValue({}),
		};

		mockedUseRequest.mockImplementation(mockCatalog());
	});

	it('opens on the configuration summary with the edit toolbar', () => {
		renderPage();

		expect(
			screen.getByRole('heading', {level: 1, name: /edit lifecycle/i})
		).toBeInTheDocument();
		expect(
			screen.getByRole('button', {name: /save lifecycle/i})
		).toBeInTheDocument();
		expect(
			screen.getByRole('group', {name: /configuration summary/i})
		).toHaveAttribute('aria-current', 'true');
		expect(screen.getByLabelText(/^name/i)).toHaveValue(
			'Growth and Retention Hub'
		);
	});

	it('lists one chip per stage, in display order and labelled by name', () => {
		renderPage();

		const chips = screen.getAllByRole('group', {
			name: /^(aware|qualified|pipeline)$/i,
		});

		expect(chips.map((chip) => chip.getAttribute('aria-label'))).toEqual(
			STAGE_NAMES
		);
		expect(chips[0]).toHaveTextContent(/stage 1.+1 condition/i);
	});

	it('shows the selected stage and keeps edits when switching items', () => {
		renderPage();

		fireEvent.change(screen.getByLabelText(/^name/i), {
			target: {value: 'Renamed Hub'},
		});

		const qualified = screen.getByRole('group', {name: /qualified/i});

		fireEvent.click(qualified);

		expect(qualified).toHaveAttribute('aria-current', 'true');
		expect(screen.getByLabelText(/^name/i)).toHaveValue('Qualified');

		fireEvent.click(
			screen.getByRole('group', {name: /configuration summary/i})
		);

		expect(screen.getByLabelText(/^name/i)).toHaveValue('Renamed Hub');
	});

	it('summarizes the saved lifecycle with its author and modified date', () => {
		renderPage();

		expect(screen.getByLabelText(/^description/i)).toHaveValue(
			'Lifecycle for qualified accounts'
		);
		expect(
			(screen.getByLabelText(/^stages/i) as HTMLInputElement).value
		).toMatch(/^3 stages$/i);
		expect(screen.getByLabelText(/last modified by/i)).toHaveValue(
			'Anneta Smith'
		);
		expect(
			(screen.getByLabelText(/modified date/i) as HTMLInputElement).value
		).toMatch(/2026/);
	});

	it('saves the edited description and stage name', async () => {
		renderPage();

		fireEvent.change(screen.getByLabelText(/^description/i), {
			target: {value: 'Renamed description'},
		});

		fireEvent.click(screen.getByRole('group', {name: /qualified/i}));

		fireEvent.change(screen.getByLabelText(/^name/i), {
			target: {value: 'Engaged'},
		});

		expect(
			screen.getByRole('group', {name: /^engaged$/i})
		).toBeInTheDocument();

		fireEvent.click(screen.getByRole('button', {name: /save lifecycle/i}));

		await waitFor(() =>
			expect(API.lifecycle.updateLifecycle).toHaveBeenCalled()
		);

		const payload = (API.lifecycle.updateLifecycle as jest.Mock).mock
			.calls[0][0];

		expect(payload.description).toBe('Renamed description');
		expect(payload.stages[1]).toEqual(
			expect.objectContaining({id: 'stage-1', stageName: 'Engaged'})
		);
		expect(payload.stages[0].stageName).toBe('Aware');
	});

	it('moves the selection to the next stage when the selected one is removed', () => {
		renderPage();

		fireEvent.click(screen.getByRole('group', {name: /^qualified$/i}));

		fireEvent.click(
			screen.getByRole('button', {name: /remove.qualified/i})
		);

		expect(screen.queryByRole('group', {name: /^qualified$/i})).toBeNull();
		expect(
			screen.getByRole('group', {name: /^pipeline$/i})
		).toHaveAttribute('aria-current', 'true');
		expect(
			screen.getByRole('group', {name: /^pipeline$/i})
		).toHaveTextContent(/stage 2/i);
	});

	it('moves the selection to the previous stage when the last one is removed', () => {
		renderPage();

		fireEvent.click(screen.getByRole('group', {name: /^pipeline$/i}));

		fireEvent.click(screen.getByRole('button', {name: /remove.pipeline/i}));

		expect(
			screen.getByRole('group', {name: /^qualified$/i})
		).toHaveAttribute('aria-current', 'true');
	});

	it('counts the stages left after removing one', () => {
		renderPage();

		fireEvent.click(screen.getByRole('button', {name: /remove.aware/i}));

		expect(
			(screen.getByLabelText(/^stages/i) as HTMLInputElement).value
		).toMatch(/^2 stages$/i);
	});

	it('saves removed and reordered stages numbered in their new order', async () => {
		renderPage();

		fireEvent.click(screen.getByRole('button', {name: /remove.aware/i}));

		const handle = screen.getByRole('button', {name: /drag.pipeline/i});

		handle.focus();

		fireEvent.keyDown(handle, {key: 'Enter'});
		fireEvent.keyDown(handle, {key: 'ArrowUp'});
		fireEvent.keyDown(handle, {key: 'Enter'});

		expect(
			screen.getByRole('group', {name: /^pipeline$/i})
		).toHaveTextContent(/stage 1/i);

		fireEvent.click(screen.getByRole('button', {name: /save lifecycle/i}));

		await waitFor(() =>
			expect(API.lifecycle.updateLifecycle).toHaveBeenCalled()
		);

		const payload = (API.lifecycle.updateLifecycle as jest.Mock).mock
			.calls[0][0];

		expect(
			payload.stages.map(({id, priority, stageName}: any) => [
				priority,
				id,
				stageName,
			])
		).toEqual([
			[1, 'stage-2', 'Pipeline'],
			[2, 'stage-1', 'Qualified'],
		]);
	});

	it('adds a stage at the end and selects it', () => {
		renderPage();

		fireEvent.click(screen.getByRole('button', {name: /add.stage/i}));

		const stage = screen.getByRole('group', {name: /^stage 4$/i});

		expect(stage).toHaveAttribute('aria-current', 'true');
		expect(stage).toHaveTextContent(/stage 4.+no trigger/i);
	});

	it('leaves directly without changes, even after switching items', () => {
		const mockedPrompt = useUnsavedChangesPrompt as jest.Mock;

		renderPage();

		fireEvent.click(screen.getByRole('group', {name: /^qualified$/i}));

		fireEvent.click(
			screen.getByRole('group', {name: /configuration summary/i})
		);

		expect(mockedPrompt).toHaveBeenLastCalledWith(false, expect.anything());
	});

	it('warns about unsaved changes after editing a stage', () => {
		const mockedPrompt = useUnsavedChangesPrompt as jest.Mock;

		renderPage();

		fireEvent.click(screen.getByRole('group', {name: /^qualified$/i}));

		fireEvent.change(screen.getByLabelText(/^description/i), {
			target: {value: 'Changed'},
		});

		expect(mockedPrompt).toHaveBeenLastCalledWith(true, expect.anything());
	});

	it('warns about unsaved changes after reordering the stages', () => {
		const mockedPrompt = useUnsavedChangesPrompt as jest.Mock;

		renderPage();

		const handle = screen.getByRole('button', {name: /drag.aware/i});

		handle.focus();

		fireEvent.keyDown(handle, {key: 'Enter'});
		fireEvent.keyDown(handle, {key: 'ArrowDown'});
		fireEvent.keyDown(handle, {key: 'Enter'});

		expect(mockedPrompt).toHaveBeenLastCalledWith(true, expect.anything());
	});

	it('blocks Save when the last stage is removed', () => {
		renderPage();

		['aware', 'qualified', 'pipeline'].forEach((name) =>
			fireEvent.click(
				screen.getByRole('button', {
					name: new RegExp(`remove.${name}`, 'i'),
				})
			)
		);

		fireEvent.click(screen.getByRole('button', {name: /save lifecycle/i}));

		expect(API.lifecycle.updateLifecycle).not.toHaveBeenCalled();
		expect(screen.getByText(/add at least one stage/i)).toBeInTheDocument();
	});

	it('shows the save failure alert when the server rejects the lifecycle', async () => {
		(API.lifecycle.updateLifecycle as jest.Mock).mockRejectedValue(
			new Error('failed')
		);

		renderPage();

		fireEvent.click(screen.getByRole('button', {name: /save lifecycle/i}));

		await waitFor(() =>
			expect(mockDispatch).toHaveBeenCalledWith(
				expect.objectContaining({alertType: Alert.Types.Error})
			)
		);

		expect(mockPush).not.toHaveBeenCalled();
	});

	it('tells the user to reload when someone else changed the lifecycle', async () => {
		(API.lifecycle.updateLifecycle as jest.Mock).mockRejectedValue(
			Object.assign(new Error('Request error'), {status: 409})
		);

		renderPage();

		fireEvent.click(screen.getByRole('button', {name: /save lifecycle/i}));

		await waitFor(() =>
			expect(mockDispatch).toHaveBeenCalledWith(
				expect.objectContaining({
					alertType: Alert.Types.Error,
					message: expect.stringMatching(/changed.by.another.user/i),
				})
			)
		);

		expect(mockPush).not.toHaveBeenCalled();
	});

	it('renders a loading state while the lifecycle is loading', () => {
		mockedUseRequest.mockReturnValue({data: undefined, loading: true});

		renderPage();

		expect(screen.queryByText(/edit lifecycle/i)).toBeNull();
	});

	it('renders a 404 when the lifecycle is not found', () => {
		mockedUseRequest.mockImplementation((props: any) =>
			props.dataSourceFn === (API as any).catalog.fetchCatalogFields
				? {data: {items: []}, error: false, loading: false}
				: {data: null, error: true, loading: false}
		);

		renderPage();

		expect(screen.queryByText(/edit lifecycle/i)).toBeNull();
	});

	it('saves the loaded version by its id, alerts success, and navigates to the dashboard', async () => {
		renderPage();

		fireEvent.click(screen.getByRole('button', {name: /save lifecycle/i}));

		await waitFor(() =>
			expect(API.lifecycle.updateLifecycle).toHaveBeenCalled()
		);

		const payload = (API.lifecycle.updateLifecycle as jest.Mock).mock
			.calls[0][0];

		expect(payload).toEqual(
			expect.objectContaining({
				description: 'Lifecycle for qualified accounts',
				groupId: '23',
				lifecycleId: '9',
				name: 'Growth and Retention Hub',
			})
		);
		expect(
			payload.stages.map(({id, priority, stageName}: any) => [
				priority,
				id,
				stageName,
			])
		).toEqual([
			[1, 'stage-0', 'Aware'],
			[2, 'stage-1', 'Qualified'],
			[3, 'stage-2', 'Pipeline'],
		]);
		expect(payload.stages[0]).not.toHaveProperty('stageType');

		await waitFor(() =>
			expect(mockPush).toHaveBeenCalledWith(lifecycleURL)
		);

		expect(mockDispatch).toHaveBeenCalledWith(
			expect.objectContaining({alertType: Alert.Types.Success})
		);
	});
});
