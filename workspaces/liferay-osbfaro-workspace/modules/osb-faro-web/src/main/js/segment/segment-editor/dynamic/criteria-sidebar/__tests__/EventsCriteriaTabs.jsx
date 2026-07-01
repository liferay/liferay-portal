import EventsCriteriaTabs from '../EventsCriteriaTabs';
import React from 'react';
import {
	cleanup,
	fireEvent,
	render,
	screen,
	waitFor
} from '@testing-library/react';
import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';
import {List} from 'immutable';
import {Property} from 'shared/util/records';
import {PropertyTypes} from '../../utils/constants';
import {useQuery} from '@apollo/client';

jest.unmock('react-dom');

jest.mock('@apollo/client', () => ({
	...jest.requireActual('@apollo/client'),
	useQuery: jest.fn()
}));

const mockLiferayLanguage = key => {
	const messages = {
		custom: 'Custom',
		default: 'Default',
		event: 'event'
	};

	return messages[key] || key;
};

global.Liferay = {
	Language: {
		get: mockLiferayLanguage
	}
};

const defaultEvents = new List([
	new Property({
		label: 'Viewed Page',
		name: 'viewed-page',
		propertyKey: 'web',
		type: PropertyTypes.Behavior
	}),
	new Property({
		label: 'Submitted Form',
		name: 'submitted-form',
		propertyKey: 'web',
		type: PropertyTypes.Behavior
	})
]);

const customEventsResult = (count, total = count) => ({
	data: {
		eventDefinitions: {
			eventDefinitions: Array.from({length: count}, (_, i) => ({
				description: '',
				displayName: `Custom Event ${i + 1}`,
				hidden: false,
				id: `${i + 1}`,
				name: `custom-event-${i + 1}`,
				type: 'CUSTOM'
			})),
			total
		}
	},
	loading: false
});

const renderTabs = (props = {}) =>
	render(
		<DndProvider backend={HTML5Backend}>
			<EventsCriteriaTabs
				defaultEvents={defaultEvents}
				searchValue=''
				{...props}
			/>
		</DndProvider>
	);

const lastQueryVariables = () => {
	const calls = useQuery.mock.calls;

	return calls[calls.length - 1][1].variables;
};

describe('EventsCriteriaTabs', () => {
	beforeEach(() => {
		useQuery.mockReturnValue(customEventsResult(3));
	});

	afterEach(() => {
		cleanup();
		jest.clearAllMocks();
	});

	it('renders both the Default and Custom tabs', () => {
		renderTabs();

		expect(screen.getByText('Default')).toBeInTheDocument();
		expect(screen.getByText('Custom')).toBeInTheDocument();
	});

	it('opens on the Default tab and lists the frontend default events', () => {
		renderTabs();

		expect(screen.getByText('Viewed Page')).toBeInTheDocument();
		expect(screen.getByText('Submitted Form')).toBeInTheDocument();

		// Custom events are not in the DOM until the Custom tab is selected.

		expect(screen.queryByText('Custom Event 1')).not.toBeInTheDocument();
	});

	it('lists the backend custom events when the Custom tab is selected', () => {
		renderTabs();

		fireEvent.click(screen.getByText('Custom'));

		expect(screen.getByText('Custom Event 1')).toBeInTheDocument();
		expect(screen.getByText('Custom Event 3')).toBeInTheDocument();

		expect(screen.queryByText('Viewed Page')).not.toBeInTheDocument();
	});

	it('renders criteria items inside the shared property-subgroups-list container so they pick up the sidebar item styles', () => {
		const {container} = renderTabs();

		const propertySubgroupsList = container.querySelector(
			'.property-subgroups-list'
		);

		expect(propertySubgroupsList).toBeInTheDocument();
		expect(
			propertySubgroupsList.querySelector('.criteria-sidebar-item-root')
		).toBeInTheDocument();
	});

	it('requests custom events from the backend with eventType Custom, page 0 and size 10', () => {
		renderTabs();

		expect(lastQueryVariables()).toMatchObject({
			eventType: 'CUSTOM',
			page: 0,
			size: 10
		});
	});

	it('paginates custom events through the backend when the total exceeds the page size', () => {
		useQuery.mockReturnValue(customEventsResult(10, 25));

		renderTabs();

		fireEvent.click(screen.getByText('Custom'));
		fireEvent.click(screen.getByText('2'));

		expect(lastQueryVariables().page).toBe(1);
	});

	it('does not render pagination when the total fits in a single page', () => {
		useQuery.mockReturnValue(customEventsResult(3, 3));

		renderTabs();

		fireEvent.click(screen.getByText('Custom'));

		expect(screen.queryByText('2')).not.toBeInTheDocument();
	});

	it('passes the search keyword to the backend query and resets to the first page', async () => {
		useQuery.mockReturnValue(customEventsResult(10, 25));

		const {rerender} = renderTabs();

		fireEvent.click(screen.getByText('Custom'));
		fireEvent.click(screen.getByText('2'));

		expect(lastQueryVariables().page).toBe(1);

		rerender(
			<DndProvider backend={HTML5Backend}>
				<EventsCriteriaTabs
					defaultEvents={defaultEvents}
					searchValue='checkout'
				/>
			</DndProvider>
		);

		await waitFor(() =>
			expect(lastQueryVariables()).toMatchObject({
				keyword: 'checkout',
				page: 0
			})
		);
	});

	it('filters the default events client-side by the search keyword', () => {
		renderTabs({searchValue: 'Viewed'});

		expect(screen.getByText('Viewed Page')).toBeInTheDocument();
		expect(screen.queryByText('Submitted Form')).not.toBeInTheDocument();
	});

	it('shows the no-entries empty state when there are no custom events', () => {
		useQuery.mockReturnValue(customEventsResult(0, 0));

		const {container} = renderTabs();

		fireEvent.click(screen.getByText('Custom'));

		expect(
			container.querySelectorAll('[data-testid^="criteria-item-"]')
		).toHaveLength(0);
		expect(screen.getByText('no-custom-events-yet')).toBeInTheDocument();
		expect(
			screen.getByText('learn-more-about-events').closest('a')
		).toHaveAttribute(
			'href',
			'https://learn.liferay.com/w/dxp/personalization/analytics-cloud/touchpoints/events-analytics/tracking-events'
		);
	});

	it('shows an empty state when a search yields no custom events', async () => {
		useQuery.mockReturnValue(customEventsResult(0, 0));

		renderTabs({searchValue: 'nothing-matches'});

		fireEvent.click(screen.getByText('Custom'));

		await waitFor(() =>
			expect(screen.getByText('no-results-found')).toBeInTheDocument()
		);
	});
});
