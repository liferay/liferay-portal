jest.mock('shared/store', () => ({
	__esModule: true,
	default: {
		getState: jest.fn(),
	},
}));

import HTMLBarChart from '../HTMLBarChart';
import React from 'react';
import {fireEvent, render} from '@testing-library/react';
import {fromJS} from 'immutable';
import {LanguageIds} from 'shared/util/constants';
import store from 'shared/store';

jest.unmock('react-dom');

function mockLocale(languageId) {
	store.getState.mockReturnValue(
		fromJS({
			currentUser: {data: '23'},
			users: {23: {data: {languageId}}},
		})
	);
}

beforeEach(() => {
	mockLocale(LanguageIds.English);
});

const CLASSNAME = '.analytics-bar-chart-html';

const HEADER = [
	{
		color: 'red',
		icon: 'home',
		label: 'header column 1'
	},
	{
		color: 'green',
		icon: 'home',
		label: 'header column 1'
	}
];

const COLUMNS = [
	{
		color: 'red',
		icon: 'home',
		label: 'item column 1'
	},
	{
		color: 'green',
		icon: 'home',
		label: 'item column 2'
	}
];

const PROGRESS_WITH_STRING = [
	{
		color: 'red',
		value: '50%'
	},
	{
		color: 'blue',
		value: '50%'
	}
];

const PROGRESS_WITH_NUMBER = [
	{
		color: 'red',
		value: 500
	},
	{
		color: 'blue',
		value: 500
	}
];

const ITEMS = [
	{
		columns: COLUMNS,
		progress: PROGRESS_WITH_STRING
	}
];

const TOOLTIP = {
	header: [
		{
			label: 'tooltip header'
		}
	],
	rows: [
		{
			columns: [
				{
					label: 'tooltip column 1'
				},
				{
					label: 'tooltip column 2'
				}
			]
		}
	]
};

const ITEMS_WITH_TOOLTIP = [
	{
		columns: COLUMNS,
		progress: PROGRESS_WITH_STRING,
		tooltip: TOOLTIP
	}
];

describe('HTMLBarChart', () => {
	it('should render component without crashing', () => {
		const {container} = render(<HTMLBarChart items={ITEMS} />);

		expect(container).toMatchSnapshot();
	});

	it('should render component with header', () => {
		const {container} = render(
			<HTMLBarChart header={HEADER} items={ITEMS} />
		);

		expect(container.querySelector(`${CLASSNAME}-header`)).toBeTruthy();
	});

	it('should render component with items', () => {
		const {container} = render(<HTMLBarChart items={ITEMS} />);

		expect(container.querySelector(`${CLASSNAME}-items`)).toBeTruthy();
	});

	it('should update component when receive new data', () => {
		const props = {items: ITEMS};

		const {queryByText, rerender} = render(<HTMLBarChart {...props} />);

		const newProps = {
			items: [
				{
					columns: [
						{
							color: 'red',
							icon: 'home',
							label: 'item column 3'
						},
						{
							color: 'green',
							icon: 'home',
							label: 'item column 4'
						}
					],
					progress: PROGRESS_WITH_STRING
				}
			]
		};

		expect(queryByText('item column 4')).toBeNull();

		rerender(<HTMLBarChart {...newProps} />);

		expect(queryByText('item column 4')).toBeTruthy();
	});

	it('should render component with items nested', () => {
		const {container} = render(
			<HTMLBarChart
				items={[
					...ITEMS,
					{
						expanded: true,
						items: ITEMS
					}
				]}
			/>
		);

		expect(
			container.querySelector(`${CLASSNAME}-group-items`)
		).toBeTruthy();
	});

	it('should render component item with control button to toggle content', () => {
		const {container} = render(
			<HTMLBarChart
				items={[
					...ITEMS,
					{
						expanded: true,
						items: ITEMS,
						showControls: true
					}
				]}
			/>
		);

		expect(container.querySelector(`${CLASSNAME}-button`)).toBeTruthy();
	});

	it('should set tooltip when MouseEnter on Item', () => {
		const {container, getByText} = render(
			<HTMLBarChart items={ITEMS_WITH_TOOLTIP} />
		);

		fireEvent.mouseEnter(container.querySelector(`${CLASSNAME}-item`));

		expect(getByText('tooltip header')).toBeTruthy();
		expect(getByText('tooltip column 1')).toBeTruthy();
	});

	it('should set tooltip when MouseLeave on Item', () => {
		const {container, queryByText} = render(
			<HTMLBarChart items={ITEMS_WITH_TOOLTIP} />
		);

		fireEvent.mouseEnter(container.querySelector(`${CLASSNAME}-item`));

		expect(queryByText('tooltip header')).toBeTruthy();

		fireEvent.mouseLeave(container.querySelector(`${CLASSNAME}-item`));

		expect(queryByText('tooltip header')).toBeFalsy();
	});

	it('should render Progress item based on grid', () => {
		const {container} = render(
			<HTMLBarChart
				grid={{
					formatter: value => value,
					maxValue: 1000,
					minValue: 0,
					show: true
				}}
				items={[
					{
						columns: COLUMNS,
						progress: PROGRESS_WITH_NUMBER
					}
				]}
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render Progress item based on grid and type of grid is percentage', () => {
		const {container} = render(
			<HTMLBarChart
				grid={{
					maxValue: 1000,
					minValue: 0,
					show: true,
					type: 'percentage'
				}}
				items={[
					{
						columns: COLUMNS,
						progress: PROGRESS_WITH_NUMBER
					}
				]}
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render Progress with Interval item based on grid and type of grid is percentage', () => {
		const {container} = render(
			<HTMLBarChart
				grid={{
					formatter: value => value,
					maxValue: 1000,
					minValue: 0,
					show: true,
					type: 'percentage'
				}}
				items={[
					{
						columns: COLUMNS,
						intervals: [
							{
								end: 600,
								start: 400
							},
							{
								end: 600,
								start: 400
							}
						],
						progress: PROGRESS_WITH_NUMBER
					}
				]}
			/>
		);

		expect(container).toMatchSnapshot();
	});

	describe('width calculation is locale independent', () => {
		afterEach(() => {
			store.getState.mockReset();
		});

		it('computes the same progress bar width regardless of the account locale', () => {
			mockLocale(LanguageIds.English);

			const {container: enUSContainer} = render(
				<HTMLBarChart
					grid={{
						maxValue: 1000,
						minValue: 0,
						show: true,
						type: 'number'
					}}
					items={[{columns: COLUMNS, progress: PROGRESS_WITH_NUMBER}]}
				/>
			);

			mockLocale(LanguageIds.Portuguese);

			const {container: ptBRContainer} = render(
				<HTMLBarChart
					grid={{
						maxValue: 1000,
						minValue: 0,
						show: true,
						type: 'number'
					}}
					items={[{columns: COLUMNS, progress: PROGRESS_WITH_NUMBER}]}
				/>
			);

			const getWidth = container =>
				container.querySelector(`${CLASSNAME}-progress div`).style
					.width;

			const width = getWidth(enUSContainer);

			expect(width).toMatch(/^\d+(\.\d+)?%$/);
			expect(getWidth(ptBRContainer)).toBe(width);
		});

		it('computes the same interval width and position regardless of the account locale', () => {
			const props = {
				grid: {
					maxValue: 1000,
					minValue: 0,
					show: true,
					type: 'number'
				},
				items: [
					{
						columns: COLUMNS,
						intervals: [{end: 600, start: 400}],
						progress: PROGRESS_WITH_NUMBER
					}
				]
			};

			mockLocale(LanguageIds.English);

			const {container: enUSContainer} = render(
				<HTMLBarChart {...props} />
			);

			mockLocale(LanguageIds.Portuguese);

			const {container: ptBRContainer} = render(
				<HTMLBarChart {...props} />
			);

			const getIntervalStyle = container =>
				container.querySelector(`${CLASSNAME}-interval div`).style;

			const enUSStyle = getIntervalStyle(enUSContainer);

			expect(enUSStyle.width).toMatch(/^\d+(\.\d+)?%$/);
			expect(enUSStyle.marginLeft).toMatch(/^\d+(\.\d+)?%$/);

			const ptBRStyle = getIntervalStyle(ptBRContainer);

			expect(ptBRStyle.width).toBe(enUSStyle.width);
			expect(ptBRStyle.marginLeft).toBe(enUSStyle.marginLeft);
		});
	});

	it('should return an array with intervals', () => {
		const {getByText} = render(
			<HTMLBarChart
				data={{
					items: [
						{
							columns: COLUMNS,
							progress: PROGRESS_WITH_NUMBER
						}
					]
				}}
				grid={{
					formatter: value => value,
					maxValue: 1000,
					minValue: 0,
					show: true
				}}
				items={ITEMS}
			/>
		);

		expect(getByText('0')).toBeTruthy();
		expect(getByText('500')).toBeTruthy();
		expect(getByText('1K')).toBeTruthy();
		expect(getByText('1.5K')).toBeTruthy();
	});

	it('should render an arrow down icon when there is a scroll down', () => {
		Object.defineProperty(HTMLElement.prototype, 'clientHeight', {
			configurable: true,
			value: 500
		});
		Object.defineProperty(HTMLElement.prototype, 'offsetHeight', {
			configurable: true,
			value: 500
		});
		Object.defineProperty(HTMLElement.prototype, 'scrollHeight', {
			configurable: true,
			value: 600
		});

		const {container} = render(<HTMLBarChart items={ITEMS} />);

		fireEvent.scroll(container.querySelector(`${CLASSNAME}-group-items`), {
			target: {scrollY: 150}
		});

		expect(container.querySelector('.icon.text-l-secondary')).toBeTruthy();

		expect(
			container.querySelector('.icon.text-l-secondary').firstChild
		).toHaveAttribute('href', '#angle-down');
	});

	it('should render an arrow down icon when the scroll is at the end of content', () => {
		Object.defineProperty(HTMLElement.prototype, 'clientHeight', {
			configurable: true,
			value: 600
		});
		Object.defineProperty(HTMLElement.prototype, 'offsetHeight', {
			configurable: true,
			value: 500
		});
		Object.defineProperty(HTMLElement.prototype, 'scrollHeight', {
			configurable: true,
			value: 500
		});

		const {container} = render(<HTMLBarChart items={ITEMS} />);

		fireEvent.scroll(container.querySelector(`${CLASSNAME}-group-items`), {
			target: {scrollY: 0}
		});

		expect(container.querySelector('.icon.text-l-secondary')).toBeFalsy();
	});

	it('should return true if there is items', () => {
		const {container, getAllByText, getByText} = render(
			<HTMLBarChart header={HEADER} items={ITEMS_WITH_TOOLTIP} />
		);

		fireEvent.mouseEnter(container.querySelector(`${CLASSNAME}-item`));

		expect(getByText('item column 1')).toBeTruthy();
		expect(getAllByText('header column 1')[0]).toBeTruthy();
		expect(getByText('tooltip header')).toBeTruthy();
		expect(getByText('tooltip column 1')).toBeTruthy();
	});

	it('should render label as component when passing to props', () => {
		const label = () => (
			<div className='label'>{'this is a label as component'}</div>
		);

		const {container, getByText} = render(
			<HTMLBarChart
				items={[
					{
						columns: [
							{
								label
							}
						]
					}
				]}
			/>
		);

		expect(getByText('this is a label as component')).toBeTruthy();

		expect(container.querySelector(`${CLASSNAME}-column`)).toBeTruthy();
	});

	it('should render icon without Circle component when there is a not color', () => {
		const columns = [
			{
				icon: 'home',
				label: 'item column 1'
			}
		];

		const {container} = render(
			<HTMLBarChart
				items={[
					{
						columns
					}
				]}
			/>
		);

		expect(container.querySelector('use')).toHaveAttribute('href', '#home');

		expect(container.querySelector(`${CLASSNAME}-column`)).toBeTruthy();
	});
});
