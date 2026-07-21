import ActivityStreamCard from '../ActivityStreamCard';
import mockStore from 'test/mock-store';
import React from 'react';
import {MemoryRouter} from 'react-router-dom';
import {Provider} from 'react-redux';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {render} from '@testing-library/react';
import {TrendClassification} from 'segment/types';

jest.unmock('react-dom');

jest.mock('recharts', () => {
	const OriginalModule = jest.requireActual('recharts');

	return {
		...OriginalModule,
		ResponsiveContainer: ({children}: {children: React.ReactNode}) => (
			<OriginalModule.ResponsiveContainer height={350} width={800}>
				{children}
			</OriginalModule.ResponsiveContainer>
		),
	};
});

const baseProps = {
	activityHistory: [],
	chartLoading: false,
	delta: 20,
	emptyChartContent: <div>{'Empty chart overlay'}</div>,
	footerLabel: 'The events footer',
	interval: 'D' as const,
	noResultsRenderer: <div>{'No sessions here'}</div>,
	onChartReload: jest.fn(),
	onClearDateSelection: jest.fn(),
	onDeltaChange: jest.fn(),
	onPageChange: jest.fn(),
	onPointSelect: jest.fn(),
	onSearchChange: jest.fn(),
	onSearchSubmit: jest.fn(),
	page: 1,
	rangeSelectors: {
		rangeEnd: null,
		rangeKey: RangeKeyTimeRanges.Last30Days,
		rangeStart: null,
	},
	searchValue: '',
	selected: false,
	sessionsMappedResults: {
		empty: true,
		error: null,
		items: [],
		loading: false,
		total: 0,
	},
	timeZoneId: 'UTC',
};

const renderCard = (props = {}) =>
	render(
		<Provider store={mockStore()}>
			<MemoryRouter>
				<ActivityStreamCard {...baseProps} {...props} />
			</MemoryRouter>
		</Provider>
	);

describe('ActivityStreamCard', () => {
	it('renders the search input, footer label, empty chart and no-results content', () => {
		const {container, getByPlaceholderText, getByText} = renderCard();

		expect(getByPlaceholderText('Search')).toBeInTheDocument();
		expect(getByText('The events footer')).toBeInTheDocument();
		expect(getByText('Empty chart overlay')).toBeInTheDocument();
		expect(getByText('No sessions here')).toBeInTheDocument();
		expect(container.querySelector('.trend-summary')).toBeNull();
	});

	it('renders the trend summary with a positive trend icon when a trend is provided', () => {
		const {container} = renderCard({
			trendSummary: {
				classification: TrendClassification.Positive,
				percentage: 22.5,
				value: 56,
			},
		});

		expect(container.querySelector('.trend-summary')).toBeInTheDocument();
		expect(
			container.querySelector('.lexicon-icon-caret-top-l')
		).toBeInTheDocument();
	});

	it('omits the trend summary when no trend is provided', () => {
		const {container} = renderCard({trendSummary: undefined});

		expect(container.querySelector('.trend-summary')).toBeNull();
	});

	it('renders the no-results content when there are events but no sessions', () => {
		const {container, getByText} = renderCard({
			sessionsMappedResults: {
				empty: false,
				error: null,
				items: [],
				loading: false,
				total: 6,
			},
		});

		expect(getByText('No sessions here')).toBeInTheDocument();
		expect(container.querySelector('.pagination-bar-root')).toBeNull();
	});
});
