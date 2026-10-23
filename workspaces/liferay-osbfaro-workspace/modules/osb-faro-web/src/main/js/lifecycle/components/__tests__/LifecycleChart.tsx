import LifecycleChart from '../LifecycleChart';
import React from 'react';
import {cleanup, fireEvent, render, screen} from '@testing-library/react';
import {ILifecycleStage} from 'lifecycle/utils/types';
import {
	LifecycleContextProvider,
	useLifecycle,
} from '../../context/LifecycleContext';

jest.unmock('react-dom');

let stageCount = 0;

const buildStage = (stage: Partial<ILifecycleStage> = {}): ILifecycleStage => {
	stageCount++;

	return {
		accountsCount: 1,
		averageStageDuration: 0,
		conversionRateToNextStage: 10,
		description: '',
		id: `stage-${stageCount}`,
		percentage: 10,
		priority: stageCount,
		stageName: `Stage ${stageCount}`,
		...stage,
	};
};

const sampleStages: ILifecycleStage[] = [
	buildStage({
		accountsCount: 41,
		averageStageDuration: 4.5,
		conversionRateToNextStage: 54,
		description: 'Accounts with an active deal.',
		id: '4103',
		percentage: 18.2,
		priority: 3,
		stageName: 'Pipeline',
	}),
	buildStage({
		accountsCount: 13,
		averageStageDuration: 9.8,
		conversionRateToNextStage: 492,
		description: 'Cold accounts showing early intent.',
		id: '4101',
		percentage: 43.6,
		priority: 1,
		stageName: 'Aware',
	}),
	buildStage({
		accountsCount: 64,
		averageStageDuration: 4.6,
		conversionRateToNextStage: 64,
		description: 'Accounts that passed qualification.',
		id: '4102',
		percentage: 28.4,
		priority: 2,
		stageName: 'Qualified',
	}),
	buildStage({
		accountsCount: 0,
		averageStageDuration: 0,
		conversionRateToNextStage: null,
		description: '',
		id: '4104',
		percentage: 0,
		priority: 4,
		stageName: 'At Risk',
	}),
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

const getStageNames = (container: HTMLElement) =>
	Array.from(
		container.querySelectorAll('.stage-metrics [id^="stage-metrics-title"]')
	).map((element) => element.textContent);

describe('LifecycleChart', () => {
	afterEach(cleanup);

	it('should title the card with the lifecycle name and description', () => {
		renderChart({
			description: 'Lifecycle for marketing-qualified accounts',
			name: 'Growth and Retention Hub',
			stages: sampleStages,
		});

		expect(
			screen.getByText('Growth and Retention Hub')
		).toBeInTheDocument();
		expect(
			screen.getByText('Lifecycle for marketing-qualified accounts')
		).toBeInTheDocument();
	});

	it('should show only the lifecycle name when it has no description', () => {
		renderChart({
			name: 'Growth and Retention Hub',
			stages: sampleStages,
		});

		expect(
			screen.getByText('Growth and Retention Hub')
		).toBeInTheDocument();
		expect(screen.queryByText(/the distribution of accounts/i)).toBeNull();
	});

	it('should render a loading spinner while the request is in flight', () => {
		const {container, queryByText} = renderChart({loading: true});

		expect(container.querySelector('.loading-root')).toBeInTheDocument();
		expect(
			container.querySelector('.loading-root')?.closest('.card')
		).toHaveStyle({minHeight: '432px'});
		expect(queryByText('Aware')).toBeNull();
	});

	it('should not reserve the loading height once the stages arrive', () => {
		const {container} = renderChart({stages: sampleStages});

		expect(
			container.querySelector('.stage-metrics')?.closest('.card')
		).not.toHaveStyle({minHeight: '432px'});
	});

	it('should render the empty state when the request errors', () => {
		const {container} = renderChart({error: true, stages: sampleStages});

		expect(
			screen.getByText(/no lifecycle data available/i)
		).toBeInTheDocument();
		expect(container.querySelector('.stage-metrics')).toBeNull();
	});

	it('should render nothing when the lifecycle has no stages', () => {
		const {container} = renderChart({
			name: 'Growth and Retention Hub',
			stages: [],
		});

		expect(screen.queryByText(/lifecycle stages/i)).toBeNull();
		expect(screen.queryByText('Growth and Retention Hub')).toBeNull();
		expect(container.querySelector('.card')).toBeNull();
	});

	it('should render every stage by name in display order, At Risk included', () => {
		const {container} = renderChart({stages: sampleStages});

		expect(getStageNames(container)).toEqual([
			'Aware',
			'Qualified',
			'Pipeline',
			'At Risk',
		]);
	});

	it('should render each stage with its metrics and bar', () => {
		const {container, getByText} = renderChart({stages: sampleStages});

		expect(getByText('13')).toBeInTheDocument();
		expect(getByText('64')).toBeInTheDocument();
		expect(getByText('41')).toBeInTheDocument();

		expect(container.querySelectorAll('.stage-metrics__bar')).toHaveLength(
			3
		);
		expect(
			container.querySelectorAll('.stage-metrics__bar--empty')
		).toHaveLength(1);
	});

	it('should render no conversion section between the stages', () => {
		const {container} = renderChart({stages: sampleStages});

		expect(container.querySelector('.stage-progression')).toBeNull();
	});

	it('should render the conversion label on each bar that has a next stage', () => {
		const {container} = renderChart({stages: sampleStages});

		const labels = Array.from(
			container.querySelectorAll('.stage-metrics__conversion')
		).map((element) => element.textContent);

		expect(labels).toHaveLength(3);
		expect(labels[0]).toMatch(/492%/);
		expect(labels[1]).toMatch(/64%/);
		expect(labels[2]).toMatch(/54%/);
		expect(labels[0]).toMatch(/conversion to next stage/i);
		expect(
			container.querySelector(
				'.stage-metrics__conversion .lexicon-icon-angle-double-right-small'
			)
		).toBeInTheDocument();
	});

	it('should render account counts abbreviated from a thousand upwards', () => {
		const {getByText, queryByText} = renderChart({
			stages: [
				buildStage({accountsCount: 5350}),
				buildStage({accountsCount: 1656000}),
				buildStage({accountsCount: 999}),
			],
		});

		expect(getByText('5.3K')).toBeInTheDocument();
		expect(getByText('1.6M')).toBeInTheDocument();
		expect(getByText('999')).toBeInTheDocument();

		expect(queryByText('5350')).toBeNull();
		expect(queryByText('1656000')).toBeNull();
	});

	it('should render the percentage of all accounts rounded to a single decimal', () => {
		const {getByText, queryByText} = renderChart({
			stages: [
				buildStage({percentage: 83.62}),
				buildStage({percentage: 50}),
			],
		});

		expect(getByText(/83.6% of all accounts/i)).toBeInTheDocument();
		expect(getByText(/^50% of all accounts/i)).toBeInTheDocument();

		expect(queryByText(/83.62% of all accounts/i)).toBeNull();
	});

	it('should show "avg. days" for non-zero averages and "no activity" otherwise', () => {
		const {getAllByText, getByText} = renderChart({stages: sampleStages});

		expect(getAllByText(/avg\. days/i)).toHaveLength(3);
		expect(getByText(/no activity/i)).toBeInTheDocument();
	});

	it('should render avg. days values rounded to one decimal', () => {
		const {getByText, queryByText} = renderChart({
			stages: [
				buildStage({averageStageDuration: 9.8333333}),
				buildStage({averageStageDuration: 4.6789}),
				buildStage({averageStageDuration: 13}),
			],
		});

		expect(getByText(/^9\.8 avg\. days$/i)).toBeInTheDocument();
		expect(getByText(/^4\.7 avg\. days$/i)).toBeInTheDocument();
		expect(getByText(/^13 avg\. days$/i)).toBeInTheDocument();

		expect(queryByText(/9\.83/)).toBeNull();
	});

	it('should use the singular for an average of one day', () => {
		const {getAllByText, queryByText} = renderChart({
			stages: [
				buildStage({averageStageDuration: 1}),
				buildStage({averageStageDuration: 1.04}),
			],
		});

		expect(getAllByText(/^1 avg\. day$/i)).toHaveLength(2);
		expect(queryByText(/avg\. days/i)).toBeNull();
	});

	it('should show the stage description in a popover only on click', () => {
		renderChart({stages: sampleStages});

		const [awareHelp] = screen.getAllByRole('button', {
			name: /more information/i,
		});

		fireEvent.focus(awareHelp);
		fireEvent.mouseEnter(awareHelp);

		expect(
			screen.queryByText(/cold accounts showing early intent/i)
		).toBeNull();

		fireEvent.click(awareHelp);

		expect(
			screen.getByText(/cold accounts showing early intent/i)
		).toBeInTheDocument();
	});

	it('should show the help icon as a filled question mark', () => {
		const {container} = renderChart({stages: sampleStages});

		expect(
			container.querySelectorAll('.lexicon-icon-question-circle-full')
		).toHaveLength(3);
	});

	it('should show no help icon for a stage without a description', () => {
		renderChart({stages: sampleStages});

		expect(
			screen.getAllByRole('button', {name: /more information/i})
		).toHaveLength(3);
	});

	it('should leave the context lifecycleStageFilter unset on mount', () => {
		const {getByTestId} = renderChart({stages: sampleStages});

		expect(getByTestId('lifecycle-filter').textContent).toBe('none');
	});

	it('should show a filter button on every stage', () => {
		renderChart({stages: sampleStages});

		expect(screen.getAllByTitle(/filter by/i)).toHaveLength(
			sampleStages.length
		);
	});

	it('should filter by the id of the stage', () => {
		const {getByTestId} = renderChart({stages: sampleStages});

		const filterButton = screen.getByRole('button', {name: /aware/i});

		expect(filterButton).toHaveAttribute(
			'title',
			expect.stringMatching(/filter by aware/i)
		);

		fireEvent.click(filterButton);

		expect(getByTestId('lifecycle-filter').textContent).toBe('4101');
	});

	it('should show no pagination while every stage fits', () => {
		renderChart({stages: sampleStages});

		expect(screen.queryByText(/scroll to view all stages/i)).toBeNull();
		expect(screen.queryByRole('button', {name: /previous/i})).toBeNull();
		expect(screen.queryByRole('button', {name: /next/i})).toBeNull();
	});

	describe('when the stages do not fit', () => {
		const descriptors = ['clientWidth', 'scrollWidth', 'scrollLeft'].map(
			(property) => [
				property,
				Object.getOwnPropertyDescriptor(
					HTMLElement.prototype,
					property
				),
			]
		);

		const scrollBy = jest.fn();

		beforeEach(() => {
			Object.defineProperty(HTMLElement.prototype, 'clientWidth', {
				configurable: true,
				get: () => 400,
			});
			Object.defineProperty(HTMLElement.prototype, 'scrollWidth', {
				configurable: true,
				get: () => 1000,
			});
			Object.defineProperty(HTMLElement.prototype, 'scrollLeft', {
				configurable: true,
				get: () => 0,
			});

			(HTMLElement.prototype as any).scrollBy = scrollBy;
		});

		afterEach(() => {
			descriptors.forEach(([property, descriptor]) => {
				if (descriptor) {
					Object.defineProperty(
						HTMLElement.prototype,
						property as string,
						descriptor as PropertyDescriptor
					);
				}
				else {
					delete (HTMLElement.prototype as any)[property as string];
				}
			});

			delete (HTMLElement.prototype as any).scrollBy;

			scrollBy.mockClear();
		});

		it('should show the pagination with the previous arrow disabled at the start', () => {
			renderChart({stages: sampleStages});

			expect(
				screen.getByText(/scroll to view all stages/i)
			).toBeInTheDocument();
			expect(
				screen.getByRole('button', {name: /previous/i})
			).toBeDisabled();
			expect(screen.getByRole('button', {name: /next/i})).toBeEnabled();
		});

		it('should scroll to the next card with the next arrow', () => {
			renderChart({stages: sampleStages});

			fireEvent.click(screen.getByRole('button', {name: /next/i}));

			expect(scrollBy).toHaveBeenCalledWith(
				expect.objectContaining({left: expect.any(Number)})
			);
		});
	});
});
