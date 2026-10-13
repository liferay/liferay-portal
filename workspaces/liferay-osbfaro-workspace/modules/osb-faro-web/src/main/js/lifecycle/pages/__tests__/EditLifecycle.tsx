import * as API from 'shared/api';
import EditLifecycle from '../EditLifecycle';
import React from 'react';
import {Alert} from 'shared/types';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import {ILifecycleStage} from 'shared/api/lifecycle';
import {MemoryRouter} from 'react-router-dom';
import {Routes, toRoute} from 'shared/util/router';
import {useRequest} from 'shared/hooks/useRequest';

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
	useParams: () => ({channelId: '123', groupId: '23', lifecycleId: '9'}),
}));

jest.mock('shared/hooks/useHistoryAdapter', () => ({
	useHistoryAdapter: () => ({push: mockPush}),
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
		description: 'A configured stage',
		displayOrder: index + 1,
		externalReferenceCode: `erc-stage-${index + 1}`,
		id: `stage-${index}`,
		maxDuration: 30,
		name,
	})).reverse();

const lifecycle = {
	authorName: 'Anneta Smith',
	description: 'Lifecycle for qualified accounts',
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
	lifecycleId: '9',
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
		expect(screen.getByLabelText(/title/i)).toHaveValue(
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

		fireEvent.change(screen.getByLabelText(/title/i), {
			target: {value: 'Renamed Hub'},
		});

		const qualified = screen.getByRole('group', {name: /qualified/i});

		fireEvent.click(qualified);

		expect(qualified).toHaveAttribute('aria-current', 'true');
		expect(screen.getByText(/^stage 2$/i)).toBeInTheDocument();
		expect(screen.queryByLabelText(/title/i)).toBeNull();

		fireEvent.click(
			screen.getByRole('group', {name: /configuration summary/i})
		);

		expect(screen.getByLabelText(/title/i)).toHaveValue('Renamed Hub');
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

	it('saves the contract payload, alerts success, and navigates to the dashboard', async () => {
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
			payload.stages.map(({displayOrder, id, name}: any) => [
				displayOrder,
				id,
				name,
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
