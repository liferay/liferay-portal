import * as API from 'shared/api';
import EditLifecycle from '../EditLifecycle';
import React from 'react';
import {Alert} from 'shared/types';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import {ILifecycleStage} from 'shared/api/lifecycle';
import {LIFECYCLE_STAGE_ORDER} from 'lifecycle/utils/stageConfiguration';
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
	useHistory: () => ({push: mockPush}),
	useParams: () => ({channelId: '123', groupId: '23', lifecycleId: '9'}),
}));

const mockedUseRequest = useRequest as jest.Mock;

const buildStages = (): ILifecycleStage[] =>
	LIFECYCLE_STAGE_ORDER.map((stageType, index) => ({
		description: 'A configured stage',
		displayOrder: index + 1,
		id: `stage-${index}`,
		maxDuration: 30,
		accountLifecycleStageRule: {
			filterString: '(account.annualRevenue gt 1000)',
			filterMetadata: JSON.stringify({
				conditionValue: '1000',
				field: 'account.annualRevenue',
				fieldDataCategory: 'Number',
				fieldDataType: 'NUMERIC',
				operator: 'gt',
			}),
		},
		stageType,
	}));

const lifecycle = {
	id: '9',
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

const lifecycleURL = toRoute(Routes.LIFECYCLE, {
	channelId: '123',
	groupId: '23',
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

	it('renders the toolbar with a Save action and the prefilled name', () => {
		renderPage();

		expect(screen.getByText('Lifecycle Settings')).toBeInTheDocument();
		expect(screen.getByRole('button', {name: 'Save'})).toBeInTheDocument();
		expect(screen.getByLabelText('Lifecycle Name')).toHaveValue(
			'Growth and Retention Hub'
		);
	});

	it('renders a loading state while the lifecycle is loading', () => {
		mockedUseRequest.mockReturnValue({data: undefined, loading: true});

		renderPage();

		expect(screen.queryByText('Lifecycle Settings')).toBeNull();
	});

	it('renders a 404 when the lifecycle is not found', () => {
		mockedUseRequest.mockImplementation((props: any) =>
			props.dataSourceFn === (API as any).catalog.fetchCatalogFields
				? {data: {items: []}, error: false, loading: false}
				: {data: null, error: true, loading: false}
		);

		renderPage();

		expect(screen.queryByText('Lifecycle Settings')).toBeNull();
	});

	it('saves via PUT, alerts success, and navigates to the dashboard', async () => {
		renderPage();

		fireEvent.click(screen.getByRole('button', {name: 'Save'}));

		await waitFor(() =>
			expect(API.lifecycle.updateLifecycle).toHaveBeenCalled()
		);

		const payload = (API.lifecycle.updateLifecycle as jest.Mock).mock
			.calls[0][0];

		expect(payload).toEqual(
			expect.objectContaining({
				groupId: '23',
				lifecycleId: '9',
				name: 'Growth and Retention Hub',
			})
		);
		expect(payload.stages).toHaveLength(6);
		expect(payload.stages[0].id).toBe('stage-0');

		await waitFor(() =>
			expect(mockPush).toHaveBeenCalledWith(lifecycleURL)
		);

		expect(mockDispatch).toHaveBeenCalledWith(
			expect.objectContaining({alertType: Alert.Types.Success})
		);
	});
});
