import LifecycleStatus from '../LifecycleStatus';
import React from 'react';
import {
	cleanup,
	fireEvent,
	render,
	screen,
	within,
} from '@testing-library/react';
import {useRequest} from 'shared/hooks/useRequest';

jest.unmock('react-dom');

jest.mock('shared/api', () => ({
	accounts: {
		fetchLifecycleStatus: jest.fn(),
	},
	lifecycle: {
		fetchChannelLifecycles: jest.fn(),
	},
}));

jest.mock('shared/hooks/useRequest', () => ({
	useRequest: jest.fn(),
}));

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({channelId: '123', groupId: '23', id: 'acc-1'}),
}));

const mockedUseRequest = useRequest as jest.Mock;

const DEFAULT_STATUS = {
	accountLifecycleStageStatuses: [
		{
			endDate: '2026-01-16T00:00:00.000Z',
			id: 'als-aware',
			name: 'Aware',
			position: 0,
			startDate: '2026-01-04T00:00:00.000Z',
		},
		{
			id: 'als-engaged',
			name: 'Engaged',
			position: 1,
			startDate: '2026-01-16T00:00:00.000Z',
		},
		{
			id: 'als-pipeline',
			name: 'Pipeline',
			position: 2,
		},
		{
			id: 'als-onboarding',
			name: 'Onboarding',
			position: 3,
		},
		{
			id: 'als-established',
			name: 'Established',
			position: 4,
		},
		{
			id: 'als-at-risk',
			name: 'At Risk',
			position: 5,
		},
	],
	id: 'al-1',
	name: 'Default Lifecycle',
};

const useRequestImpl =
	({
		lifecyclesData,
		lifecyclesLoading = false,
		statusData,
		statusDataByLifecycleId,
		statusLoading = false,
	}: {
		lifecyclesData?: Array<{
			defaultAccountLifecycle?: boolean;
			id: string;
			name: string;
		}>;
		lifecyclesLoading?: boolean;
		statusData?: unknown;
		statusDataByLifecycleId?: {[lifecycleId: string]: unknown};
		statusLoading?: boolean;
	}) =>
	({variables}: {variables: {[key: string]: any}}) =>
		variables.accountLifecycleId !== undefined
			? {
					data:
						statusDataByLifecycleId?.[
							variables.accountLifecycleId
						] ?? statusData,
					loading: statusLoading,
				}
			: {data: lifecyclesData, loading: lifecyclesLoading};

const buildStatus = (lifecycleId: string, stageName: string) => ({
	accountLifecycleStageStatuses: [
		{
			id: `${lifecycleId}-stage`,
			name: stageName,
			position: 0,
			startDate: '2026-01-04T00:00:00.000Z',
		},
	],
	id: lifecycleId,
	name: lifecycleId,
});

jest.mock('@clayui/multi-step-nav', () => {
	const MultiStepNav = ({
		children,
		className,
	}: {
		children: React.ReactNode;
		className?: string;
	}) => <ol className={className}>{children}</ol>;

	MultiStepNav.Item = ({
		active,
		children,
		state,
	}: {
		active?: boolean;
		children: React.ReactNode;
		state?: string;
	}) => (
		<li data-active={active ? 'true' : undefined} data-state={state}>
			{children}
		</li>
	);

	MultiStepNav.Title = ({children}: {children: React.ReactNode}) => (
		<span>{children}</span>
	);

	MultiStepNav.Divider = () => null;

	MultiStepNav.Indicator = ({
		label,
		subTitle,
	}: {
		label?: React.ReactText;
		subTitle?: React.ReactText;
	}) => (
		<div>
			{subTitle ? <span>{subTitle}</span> : null}
			<span>{label}</span>
		</div>
	);

	return {__esModule: true, default: MultiStepNav};
});

describe('LifecycleStatus', () => {
	beforeEach(() => {
		jest.clearAllMocks();
		mockedUseRequest.mockImplementation(
			useRequestImpl({
				lifecyclesData: [{id: 'al-1', name: 'Default Lifecycle'}],
				statusData: DEFAULT_STATUS,
			})
		);
	});

	afterEach(cleanup);

	describe('rendering', () => {
		it('should render the card title and description', () => {
			render(<LifecycleStatus />);

			expect(screen.getByText('LIFECYCLE STATUS')).toBeInTheDocument();
			expect(
				screen.getByText(
					'Shows the current stage progression for this account.'
				)
			).toBeInTheDocument();
		});

		it('should render every progression stage label in the multistep view', () => {
			const {container} = render(<LifecycleStatus />);

			const multistep = container.querySelector(
				'.lifecycle-status-multistep'
			) as HTMLElement;

			expect(within(multistep).getByText('Aware')).toBeInTheDocument();
			expect(within(multistep).getByText('Engaged')).toBeInTheDocument();
			expect(within(multistep).getByText('Pipeline')).toBeInTheDocument();
			expect(
				within(multistep).getByText('Onboarding')
			).toBeInTheDocument();
			expect(
				within(multistep).getByText('Established')
			).toBeInTheDocument();
		});

		it('should render the custom stage names in their position order', () => {
			mockedUseRequest.mockImplementation(
				useRequestImpl({
					lifecyclesData: [{id: 'al-1', name: 'Default Lifecycle'}],
					statusData: {
						accountLifecycleStageStatuses: [
							{
								id: 'als-customer',
								name: 'Customer',
								position: 1,
							},
							{
								id: 'als-prospect',
								name: 'Prospect',
								position: 0,
							},
						],
						id: 'al-1',
						name: 'Default Lifecycle',
					},
				})
			);

			const {container} = render(<LifecycleStatus />);

			const multistep = container.querySelector(
				'.lifecycle-status-multistep'
			) as HTMLElement;

			expect(
				within(multistep)
					.getAllByText(/^(Customer|Prospect)$/)
					.map((title) => title.textContent)
			).toEqual(['Prospect', 'Customer']);
		});

		it('should render numeric indicators 1 through 5 in the multistep view', () => {
			const {container} = render(<LifecycleStatus />);

			const multistep = container.querySelector(
				'.lifecycle-status-multistep'
			) as HTMLElement;

			for (const label of ['1', '2', '3', '4', '5']) {
				expect(within(multistep).getByText(label)).toBeInTheDocument();
			}
		});

		it('should render the start date below stages that have one', () => {
			const {container} = render(<LifecycleStatus />);

			const multistep = container.querySelector(
				'.lifecycle-status-multistep'
			) as HTMLElement;

			expect(
				within(multistep).getByText('Jan 4, 2026')
			).toBeInTheDocument();
			expect(
				within(multistep).getByText('Jan 16, 2026')
			).toBeInTheDocument();
		});

		it('should mark the in-progress stage as active and completed stages as complete', () => {
			const {container} = render(<LifecycleStatus />);

			const items = container.querySelectorAll(
				'.lifecycle-status-multistep li'
			);

			expect(items[0].getAttribute('data-state')).toBe('complete');
			expect(items[1].getAttribute('data-active')).toBe('true');
			expect(items[1].getAttribute('data-state')).toBeNull();
			expect(items[2].getAttribute('data-state')).toBeNull();
		});

		it('should render the At-Risk stage with a sticker and icon in the multistep view', () => {
			const {container} = render(<LifecycleStatus />);

			const multistep = container.querySelector(
				'.lifecycle-status-multistep'
			) as HTMLElement;

			expect(within(multistep).getByText('At Risk')).toBeInTheDocument();
			expect(multistep.querySelector('.sticker')).toBeInTheDocument();
			expect(
				multistep.querySelector('.lexicon-icon-exclamation-circle')
			).toBeInTheDocument();
		});
	});

	describe('summary view', () => {
		it('should render the active stage label and step counter', () => {
			const {container} = render(<LifecycleStatus />);

			const summary = container.querySelector(
				'.lifecycle-status-summary'
			) as HTMLElement;

			expect(within(summary).getByText('Engaged')).toBeInTheDocument();
			expect(
				within(summary).getByText('Step 2 of 5')
			).toBeInTheDocument();
		});

		it('should render the active stage start date', () => {
			const {container} = render(<LifecycleStatus />);

			const summary = container.querySelector(
				'.lifecycle-status-summary'
			) as HTMLElement;

			expect(
				within(summary).getByText('Jan 16, 2026')
			).toBeInTheDocument();
		});

		it('should render the at-risk row as No when the account is not at risk', () => {
			const {container} = render(<LifecycleStatus />);

			const summary = container.querySelector(
				'.lifecycle-status-summary'
			) as HTMLElement;

			expect(within(summary).getByText('At Risk')).toBeInTheDocument();
			expect(within(summary).getByText('No')).toBeInTheDocument();
		});

		it('should render the at-risk row when no progression stage is active', () => {
			mockedUseRequest.mockImplementation(
				useRequestImpl({
					lifecyclesData: [{id: 'al-1', name: 'Default Lifecycle'}],
					statusData: {
						accountLifecycleStageStatuses: [
							{
								id: 'als-aware',
								name: 'Aware',
								position: 0,
							},
							{
								id: 'als-at-risk',
								name: 'At Risk',
								position: 1,
								startDate: '2026-02-01T00:00:00.000Z',
							},
						],
						id: 'al-1',
						name: 'Default Lifecycle',
					},
				})
			);

			const {container} = render(<LifecycleStatus />);

			const summary = container.querySelector(
				'.lifecycle-status-summary'
			) as HTMLElement;

			expect(summary).not.toBeNull();
			expect(within(summary).getByText('At Risk')).toBeInTheDocument();
			expect(within(summary).getByText('Yes')).toBeInTheDocument();
			expect(within(summary).queryByText('Aware')).toBeNull();
		});

		it('should not render the summary when the lifecycle has no at-risk stage and no active stage', () => {
			mockedUseRequest.mockImplementation(
				useRequestImpl({
					lifecyclesData: [{id: 'al-1', name: 'Default Lifecycle'}],
					statusData: {
						accountLifecycleStageStatuses: [
							{
								id: 'als-aware',
								name: 'Aware',
								position: 0,
							},
						],
						id: 'al-1',
						name: 'Default Lifecycle',
					},
				})
			);

			const {container} = render(<LifecycleStatus />);

			expect(
				container.querySelector('.lifecycle-status-summary')
			).toBeNull();
		});
	});

	describe('request', () => {
		it.each([
			{
				defaultLifecycleId: 'al-1',
				expectedLifecycle: 'Customer',
				expectedStage: 'Prospect',
			},
			{
				defaultLifecycleId: 'al-2',
				expectedLifecycle: 'Partner',
				expectedStage: 'Reseller',
			},
		])(
			'should render the status of the default lifecycle $defaultLifecycleId',
			({defaultLifecycleId, expectedLifecycle, expectedStage}) => {
				mockedUseRequest.mockImplementation(
					useRequestImpl({
						lifecyclesData: [
							{
								defaultAccountLifecycle:
									defaultLifecycleId === 'al-1',
								id: 'al-1',
								name: 'Customer',
							},
							{
								defaultAccountLifecycle:
									defaultLifecycleId === 'al-2',
								id: 'al-2',
								name: 'Partner',
							},
						],
						statusDataByLifecycleId: {
							'al-1': buildStatus('al-1', 'Prospect'),
							'al-2': buildStatus('al-2', 'Reseller'),
						},
					})
				);

				const {container} = render(<LifecycleStatus />);

				expect(
					screen.getByRole('combobox', {name: 'Lifecycle'})
				).toHaveTextContent(expectedLifecycle);
				expect(
					within(
						container.querySelector(
							'.lifecycle-status-multistep'
						) as HTMLElement
					).getByText(expectedStage)
				).toBeInTheDocument();
			}
		);

		it('should render the status of the lifecycle selected in the card', () => {
			mockedUseRequest.mockImplementation(
				useRequestImpl({
					lifecyclesData: [
						{
							defaultAccountLifecycle: true,
							id: 'al-1',
							name: 'Customer',
						},
						{id: 'al-2', name: 'Partner'},
					],
					statusDataByLifecycleId: {
						'al-1': buildStatus('al-1', 'Prospect'),
						'al-2': buildStatus('al-2', 'Reseller'),
					},
				})
			);

			const {container} = render(<LifecycleStatus />);

			fireEvent.click(screen.getByRole('combobox', {name: 'Lifecycle'}));
			fireEvent.click(screen.getByRole('option', {name: 'Partner'}));

			const multistep = container.querySelector(
				'.lifecycle-status-multistep'
			) as HTMLElement;

			expect(within(multistep).getByText('Reseller')).toBeInTheDocument();
			expect(within(multistep).queryByText('Prospect')).toBeNull();
		});

		it('should render the fetched status when the backend returns data', () => {
			mockedUseRequest.mockImplementation(
				useRequestImpl({
					lifecyclesData: [{id: 'al-1', name: 'Default Lifecycle'}],
					statusData: {
						accountLifecycleStageStatuses: [
							{
								endDate: '2026-03-01T00:00:00.000Z',
								id: 'als-aware',
								name: 'Aware',
								position: 0,
								startDate: '2026-02-01T00:00:00.000Z',
							},
							{
								id: 'als-engaged',
								name: 'Engaged',
								position: 1,
								startDate: '2026-03-01T00:00:00.000Z',
							},
							{
								id: 'als-pipeline',
								name: 'Pipeline',
								position: 2,
							},
							{
								id: 'als-onboarding',
								name: 'Onboarding',
								position: 3,
							},
							{
								id: 'als-established',
								name: 'Established',
								position: 4,
							},
							{
								id: 'als-at-risk',
								name: 'At Risk',
								position: 5,
							},
						],
						id: 'al-1',
						name: 'Default Lifecycle',
					},
				})
			);

			const {container} = render(<LifecycleStatus />);

			const multistep = container.querySelector(
				'.lifecycle-status-multistep'
			) as HTMLElement;

			expect(
				within(multistep).getByText('Feb 1, 2026')
			).toBeInTheDocument();
			expect(
				within(multistep).getByText('Mar 1, 2026')
			).toBeInTheDocument();
		});

		it('should render the empty state when no lifecycles are returned', () => {
			mockedUseRequest.mockImplementation(
				useRequestImpl({lifecyclesData: []})
			);

			const {container} = render(<LifecycleStatus />);

			expect(
				screen.getByText('No Lifecycle Data Available')
			).toBeInTheDocument();
			expect(
				screen.getByText('Lifecycle data will appear here when synced.')
			).toBeInTheDocument();
			expect(
				container.querySelector('.lifecycle-status-multistep')
			).toBeNull();
			expect(
				container.querySelector('.lifecycle-status-summary')
			).toBeNull();
		});

		it('should render the empty state when the matching status has no stages', () => {
			mockedUseRequest.mockImplementation(
				useRequestImpl({
					lifecyclesData: [{id: 'al-1', name: 'Default Lifecycle'}],
					statusData: {
						accountLifecycleStageStatuses: [],
						id: 'al-1',
						name: 'Default Lifecycle',
					},
				})
			);

			render(<LifecycleStatus />);

			expect(
				screen.getByText('No Lifecycle Data Available')
			).toBeInTheDocument();
		});
	});

	describe('loading', () => {
		it('should render the loading indicator while the lifecycles list is loading', () => {
			mockedUseRequest.mockImplementation(
				useRequestImpl({lifecyclesLoading: true})
			);

			const {container} = render(<LifecycleStatus />);

			expect(
				container.querySelector('.loading-root')
			).toBeInTheDocument();
			expect(
				container.querySelector('.lifecycle-status-multistep')
			).toBeNull();
		});

		it('should render the loading indicator while the matching status is loading', () => {
			mockedUseRequest.mockImplementation(
				useRequestImpl({
					lifecyclesData: [{id: 'al-1', name: 'Default Lifecycle'}],
					statusLoading: true,
				})
			);

			const {container} = render(<LifecycleStatus />);

			expect(
				container.querySelector('.loading-root')
			).toBeInTheDocument();
			expect(
				container.querySelector('.lifecycle-status-multistep')
			).toBeNull();
		});

		it('should not render the loading indicator when no lifecycles are returned', () => {
			mockedUseRequest.mockImplementation(
				useRequestImpl({
					lifecyclesData: [],
					statusLoading: true,
				})
			);

			const {container} = render(<LifecycleStatus />);

			expect(container.querySelector('.loading-root')).toBeNull();
		});
	});
});
