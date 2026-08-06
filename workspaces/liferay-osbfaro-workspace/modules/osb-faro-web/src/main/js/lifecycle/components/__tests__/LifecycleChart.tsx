import LifecycleChart from '../LifecycleChart';
import React from 'react';
import {cleanup, fireEvent, render} from '@testing-library/react';
import {ILifecycleStage} from 'lifecycle/utils/types';
import {
	LifecycleContextProvider,
	useLifecycle,
} from '../../context/LifecycleContext';
import {LifecycleStages} from 'contacts/pages/account/utils/constants';

jest.unmock('react-dom');

const sampleStages: ILifecycleStage[] = [
	{
		accountsCount: 13,
		averageStageDuration: 9.8,
		conversionRateToNextStage: 492,
		description: 'Aware description.',
		percentage: 43.6,
		stageType: LifecycleStages.AWARE,
	},
	{
		accountsCount: 64,
		averageStageDuration: 4.6,
		conversionRateToNextStage: 64,
		description: 'Engaged description.',
		percentage: 28.4,
		stageType: LifecycleStages.ENGAGED,
	},
	{
		accountsCount: 41,
		averageStageDuration: 4.5,
		conversionRateToNextStage: 54,
		description: 'Pipeline description.',
		percentage: 18.2,
		stageType: LifecycleStages.PIPELINE,
	},
	{
		accountsCount: 22,
		averageStageDuration: 12,
		conversionRateToNextStage: 0,
		description: 'Onboarding description.',
		percentage: 9.8,
		stageType: LifecycleStages.ONBOARDING,
	},
	{
		accountsCount: 0,
		averageStageDuration: 0,
		conversionRateToNextStage: null,
		description: 'Established description.',
		percentage: 0,
		stageType: LifecycleStages.ESTABLISHED,
	},
];

const FilterProbe = () => {
	const {filters} = useLifecycle();
	return (
		<span data-testid="lifecycle-filter">
			{filters.lifecycleStageFilter ?? 'none'}
		</span>
	);
};

const renderChart = (props: Parameters<typeof LifecycleChart>[0] = {}) =>
	render(
		<LifecycleContextProvider lifecycleId="1">
			<LifecycleChart {...props} />
			<FilterProbe />
		</LifecycleContextProvider>
	);

describe('LifecycleChart', () => {
	afterEach(cleanup);

	it('should render a loading spinner while the request is in flight', () => {
		const {container, queryByText} = renderChart({loading: true});

		expect(container.querySelector('.loading-root')).toBeInTheDocument();
		expect(queryByText('Aware')).toBeNull();
	});

	it('should render every stage title in the empty state when the request errors', () => {
		const {getByText} = renderChart({error: true});

		expect(getByText('Aware')).toBeInTheDocument();
		expect(getByText('Engaged')).toBeInTheDocument();
		expect(getByText('Pipeline')).toBeInTheDocument();
		expect(getByText('Onboarding')).toBeInTheDocument();
		expect(getByText('Established')).toBeInTheDocument();
	});

	it('should omit bar and trapezium visuals in the empty state', () => {
		const {container, getAllByText} = renderChart({error: true});

		expect(getAllByText('no activity')).toHaveLength(5);

		expect(container.querySelector('.stage-metrics__bar')).toBeNull();
		expect(
			container.querySelector('.stage-metrics__bar--empty')
		).toBeNull();
		expect(container.querySelector('.stage-progression__fill')).toBeNull();
	});

	it('should fall back to the empty state when the stages array is empty', () => {
		const {getAllByText} = renderChart({stages: []});

		expect(getAllByText('no activity')).toHaveLength(5);
	});

	it('should render each stage with its metrics when stages are provided', () => {
		const {container, getByText} = renderChart({stages: sampleStages});

		expect(getByText('13')).toBeInTheDocument();
		expect(getByText('64')).toBeInTheDocument();
		expect(getByText('41')).toBeInTheDocument();
		expect(getByText('22')).toBeInTheDocument();

		expect(container.querySelectorAll('.stage-metrics__bar')).toHaveLength(
			4
		);
		expect(
			container.querySelectorAll('.stage-metrics__bar--empty')
		).toHaveLength(1);

		expect(
			container.querySelectorAll('.stage-progression__fill')
		).toHaveLength(4);
	});

	it('should render account counts abbreviated from a thousand upwards', () => {
		const stagesWithLargeCounts: ILifecycleStage[] = [
			{
				accountsCount: 5350,
				averageStageDuration: 0,
				conversionRateToNextStage: 10,
				description: '',
				percentage: 83.62,
				stageType: LifecycleStages.AWARE,
			},
			{
				accountsCount: 1656000,
				averageStageDuration: 0,
				conversionRateToNextStage: 10,
				description: '',
				percentage: 15.38,
				stageType: LifecycleStages.ENGAGED,
			},
			{
				accountsCount: 999,
				averageStageDuration: 0,
				conversionRateToNextStage: null,
				description: '',
				percentage: 1,
				stageType: LifecycleStages.PIPELINE,
			},
		];

		const {getByText, queryByText} = renderChart({
			stages: stagesWithLargeCounts,
		});

		expect(getByText('5.3K')).toBeInTheDocument();
		expect(getByText('1.6M')).toBeInTheDocument();
		expect(getByText('999')).toBeInTheDocument();

		expect(queryByText('5350')).toBeNull();
		expect(queryByText('1656000')).toBeNull();
	});

	it('should render the percentage of all accounts rounded to a single decimal', () => {
		const stagesWithLongPercentages: ILifecycleStage[] = [
			{
				accountsCount: 1,
				averageStageDuration: 0,
				conversionRateToNextStage: 10,
				description: '',
				percentage: 83.62,
				stageType: LifecycleStages.AWARE,
			},
			{
				accountsCount: 1,
				averageStageDuration: 0,
				conversionRateToNextStage: null,
				description: '',
				percentage: 50,
				stageType: LifecycleStages.ENGAGED,
			},
		];

		const {getByText, queryByText} = renderChart({
			stages: stagesWithLongPercentages,
		});

		expect(getByText('83.6% of all accounts')).toBeInTheDocument();
		expect(getByText('50% of all accounts')).toBeInTheDocument();

		expect(queryByText('83.62% of all accounts')).toBeNull();
		expect(queryByText('50.00% of all accounts')).toBeNull();
	});

	it('should show "avg. day" for non-zero averages and "no activity" otherwise', () => {
		const {getAllByText, getByText} = renderChart({stages: sampleStages});

		expect(getAllByText('avg. day')).toHaveLength(4);
		expect(getByText('no activity')).toBeInTheDocument();
	});

	it('should render avg. day values rounded to two decimals', () => {
		const stagesWithLongDecimals: ILifecycleStage[] = [
			{
				accountsCount: 1,
				averageStageDuration: 9.8333333,
				conversionRateToNextStage: 10,
				description: '',
				percentage: 10,
				stageType: LifecycleStages.AWARE,
			},
			{
				accountsCount: 1,
				averageStageDuration: 4.6789,
				conversionRateToNextStage: 10,
				description: '',
				percentage: 10,
				stageType: LifecycleStages.ENGAGED,
			},
			{
				accountsCount: 1,
				averageStageDuration: 12,
				conversionRateToNextStage: 10,
				description: '',
				percentage: 10,
				stageType: LifecycleStages.PIPELINE,
			},
			{
				accountsCount: 1,
				averageStageDuration: 4.5,
				conversionRateToNextStage: 10,
				description: '',
				percentage: 10,
				stageType: LifecycleStages.ONBOARDING,
			},
			{
				accountsCount: 0,
				averageStageDuration: 0,
				conversionRateToNextStage: null,
				description: '',
				percentage: 0,
				stageType: LifecycleStages.ESTABLISHED,
			},
		];

		const {getByText, queryByText} = renderChart({
			stages: stagesWithLongDecimals,
		});

		expect(getByText('9.83')).toBeInTheDocument();
		expect(getByText('4.68')).toBeInTheDocument();
		expect(getByText('12')).toBeInTheDocument();
		expect(getByText('4.50')).toBeInTheDocument();

		expect(queryByText('9.8333333')).toBeNull();
		expect(queryByText('4.6789')).toBeNull();
	});

	it('should render progression labels from conversionRateToNextStage', () => {
		const {container} = renderChart({stages: sampleStages});

		const labels = Array.from(
			container.querySelectorAll('.label-info')
		).map((el) => el.textContent);

		expect(labels[0]).toContain('492%');
		expect(labels[1]).toContain('64%');
		expect(labels[2]).toContain('54%');
		expect(labels[3]).toContain('0%');
	});

	it('should default the context lifecycleStageFilter to AT_RISK on mount', () => {
		const {getByTestId} = renderChart({stages: sampleStages});

		expect(getByTestId('lifecycle-filter').textContent).toBe(
			LifecycleStages.AT_RISK
		);
	});

	it('should update the context lifecycleStageFilter when a stage filter button is clicked', () => {
		const {getAllByRole, getByTestId} = renderChart({stages: sampleStages});

		const filterButtons = getAllByRole('button');
		fireEvent.click(filterButtons[0]);

		expect(getByTestId('lifecycle-filter').textContent).toBe(
			LifecycleStages.AWARE
		);

		fireEvent.click(filterButtons[2]);

		expect(getByTestId('lifecycle-filter').textContent).toBe(
			LifecycleStages.PIPELINE
		);
	});

	it('should render a "filter by stage" tooltip on each stage filter button', () => {
		const {getAllByRole} = renderChart({stages: sampleStages});

		const filterButtons = getAllByRole('button');

		expect(filterButtons[0]).toHaveAttribute('title', 'Filter By Aware');
		expect(filterButtons[1]).toHaveAttribute('title', 'Filter By Engaged');
		expect(filterButtons[2]).toHaveAttribute('title', 'Filter By Pipeline');
		expect(filterButtons[3]).toHaveAttribute(
			'title',
			'Filter By Onboarding'
		);
		expect(filterButtons[4]).toHaveAttribute(
			'title',
			'Filter By Established'
		);
	});
});
