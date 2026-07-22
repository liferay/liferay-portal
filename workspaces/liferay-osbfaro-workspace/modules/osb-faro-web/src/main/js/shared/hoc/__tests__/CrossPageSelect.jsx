import CrossPageSelect, {
	defaultSearch,
	defaultSort,
	fetchLocalData,
	ViewSelectedToggle,
	withSelection
} from '../CrossPageSelect';
import mockStore from 'test/mock-store';
import React from 'react';
import {act, cleanup, fireEvent, render} from '@testing-library/react';
import {createOrderIOMap, NAME} from 'shared/util/pagination';
import {inputSearchText, selectAllAndToggle} from 'test/helpers';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {OrderedMap} from 'immutable';
import {Provider} from 'react-redux';
import {range} from 'lodash';
import {Routes} from 'shared/util/router';
import {SelectionProvider} from 'shared/context/selection';

jest.unmock('react-dom');

const mockItemArray = [
	{id: '1', name: 'orange'},
	{id: '2', name: 'apple'},
	{id: '3', name: 'banana'},
	{id: '4', name: 'grapefruit'},
	{id: '5', name: 'strawberry'},
	{id: '6', name: 'tangerine'}
];

const mockData = new OrderedMap(mockItemArray.map(item => [item.id, item]));

const defaultProps = {
	columns: [{accessor: 'name', label: 'name'}],
	delta: 2,
	empty: false,
	error: false,
	items: mockItemArray,
	loading: false,
	orderIOMap: createOrderIOMap('name'),
	total: mockItemArray.length
};

const DefaultComponent = props => (
	<Provider store={mockStore()}>
		<MemoryRouter
			initialEntries={[
				'/workspace/23/settings/definitions/events/custom'
			]}
		>
			<RouterRoutes>
				<Route
					element={
						<SelectionProvider>
							<CrossPageSelect {...defaultProps} {...props} />
						</SelectionProvider>
					}
					path={`${Routes.SETTINGS_DEFINITIONS_EVENTS_CUSTOM}/*`}
				/>
			</RouterRoutes>
		</MemoryRouter>
	</Provider>
);

describe('CrossPageSelect', () => {
	afterEach(cleanup);

	it('should render the server data list by default', () => {
		const {container} = render(<DefaultComponent />);

		expect(container).toMatchSnapshot();
	});

	it('should render the selected list when the user presses the "view selected link"', () => {
		jest.useFakeTimers();

		const {container, getByTestId} = render(<DefaultComponent />);

		const firstRowCheckbox = container.querySelector(
			'.table > tbody:nth-of-type(1) > tr .custom-checkbox input'
		);

		fireEvent.click(firstRowCheckbox);

		act(() => {
			jest.advanceTimersByTime(250);
		});

		fireEvent.click(getByTestId('view-selected'));

		act(() => {
			jest.advanceTimersByTime(250);
		});

		expect(container).toMatchSnapshot();

		jest.useRealTimers();
	});

	it('should be able to sort local data when a sort field is clicked', () => {
		const {container} = render(
			<DefaultComponent orderIOMap={createOrderIOMap('name', 'DESC')} />
		);

		selectAllAndToggle(container);

		const tableRows = container.querySelectorAll('tbody > tr');

		expect(tableRows.length).toBe(2);

		expect(tableRows[0]).toHaveTextContent('tangerine');
		expect(tableRows[1]).toHaveTextContent('strawberry');
	});

	it('should update local data displayed when a different pagination delta is chosen', () => {
		const {container, getByText} = render(<DefaultComponent delta={3} />);

		selectAllAndToggle(container);

		expect(getByText('apple')).toBeTruthy();
		expect(getByText('banana')).toBeTruthy();
		expect(getByText('grapefruit')).toBeTruthy();
	});

	it('it should search selected items when given a custom search function', () => {
		const mockSearcFn = ({items}) =>
			items.filter(({name}) => name === 'grapefruit');

		const {container} = render(
			<DefaultComponent searchSelectedFn={mockSearcFn} />
		);

		selectAllAndToggle(container);

		inputSearchText(container, 'fooQuery');

		const tableRows = container.querySelectorAll('tbody > tr');

		expect(tableRows.length).toBe(1);

		expect(tableRows[0]).toHaveTextContent('grapefruit');
	});
});

describe('defaultSearch', () => {
	it('should return the results of a search on the given items', () => {
		expect(
			defaultSearch({items: mockData, query: 'orange'}).toArray()
		).toEqual(mockItemArray.slice(0, 1));
	});
});

describe('defaultSort', () => {
	it('should return the results of a sort on the given items', () => {
		expect(defaultSort(mockData, createOrderIOMap(NAME)).toArray()).toEqual(
			[
				mockItemArray[1],
				mockItemArray[2],
				mockItemArray[3],
				mockItemArray[0],
				mockItemArray[4],
				mockItemArray[5]
			]
		);
	});
});

describe('fetchLocalData', () => {
	it('should return the paginated results', () => {
		const mockLocalData = new OrderedMap(
			range(9)
				.map(i => ({id: i, name: `name-${i}`}))
				.map(item => [item.id, item])
		);

		expect(
			fetchLocalData({
				delta: 5,
				items: mockLocalData,
				orderIOMap: createOrderIOMap(NAME),
				page: 1,
				query: ''
			})
		).toEqual(
			expect.objectContaining({
				items: mockLocalData.slice(0, 5).toArray(),
				total: 9
			})
		);
	});
});

describe('WithSelection', () => {
	const expectedArgs = {
		onSelectEntirePage: expect.any(Function),
		onSelectItemsChange: expect.any(Function),
		selectedItemsIOMap: expect.any(OrderedMap),
		selectEntirePage: false,
		selectEntirePageIndeterminate: false,
		showCheckbox: true
	};

	afterEach(cleanup);

	it('should return a function component with the mapped props', () => {
		const componentSpy = jest.fn(() => <div />);

		const WrappedComponent = withSelection(componentSpy);

		render(<WrappedComponent items={mockItemArray} />);

		expect(componentSpy).toBeCalledWith(
			expect.objectContaining({
				items: [...mockItemArray],
				...expectedArgs
			}),
			{}
		);
	});

	it('should NOT mark the toolbar as all checked if every item is disabled and there are no selected items', () => {
		const componentSpy = jest.fn(() => <div />);

		const WrappedComponent = withSelection(componentSpy);

		render(
			<WrappedComponent
				checkDisabled={({name}) => name === 'orange'}
				items={[mockItemArray[0]]}
			/>
		);

		expect(componentSpy).toBeCalledWith(
			expect.objectContaining({
				items: [mockItemArray[0]],
				...expectedArgs
			}),
			{}
		);
	});
});

describe('ViewSelectedToggle', () => {
	const defaultProps = {onClick: jest.fn(), selectedItemsCount: 1};

	it('should render with the "view selected" message', () => {
		const {container} = render(
			<ViewSelectedToggle {...defaultProps} showSelected={false} />
		);

		expect(container).toMatchSnapshot();
	});

	it('should render with the "return to list" message', () => {
		const {container} = render(
			<ViewSelectedToggle {...defaultProps} showSelected />
		);

		expect(container).toMatchSnapshot();
	});
});
