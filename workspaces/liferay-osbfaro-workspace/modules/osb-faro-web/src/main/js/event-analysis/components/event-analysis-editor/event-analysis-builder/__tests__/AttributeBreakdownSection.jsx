import AttributeBreakdownSection from '../AttributeBreakdownSection';
import mockStore from 'test/mock-store';
import React from 'react';
import {AnnounceContext} from 'shared/components/condition-chip/AnnounceContext';
import {AttributesContext, AttributesProvider} from '../../context/attributes';
import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {Provider} from 'react-redux';
import {fireEvent, render, screen} from '@testing-library/react';
import {Routes} from 'shared/util/router';

jest.unmock('react-dom');

const Providers = ({children}) => (
	<Provider store={mockStore()}>
		<MemoryRouter initialEntries={['/workspace/23/event-analysis']}>
			<RouterRoutes>
				<Route
					element={
						<MockedProvider
							cache={
								new InMemoryCache({
									addTypename: false,
									freezeResults: false
								})
							}
						>
							<DndProvider backend={HTML5Backend}>
								{children}
							</DndProvider>
						</MockedProvider>
					}
					path={`${Routes.EVENT_ANALYSIS}/*`}
				/>
			</RouterRoutes>
		</MemoryRouter>
	</Provider>
);

const WrappedComponent = ({announce = () => {}, eventId, ...attributes}) => (
	<Providers>
		<AttributesContext.Provider
			value={{
				attributes: {},
				breakdownOrder: [],
				breakdowns: {},
				...attributes
			}}
		>
			<AnnounceContext.Provider value={announce}>
				<AttributeBreakdownSection eventId={eventId} />
			</AnnounceContext.Provider>
		</AttributesContext.Provider>
	</Providers>
);

describe('AttributeBreakdownSection', () => {
	jest.useFakeTimers();

	it('renders only the title without an event', () => {
		render(<WrappedComponent />);

		expect(
			screen.getByRole('heading', {name: /breakdown.by/i})
		).toBeInTheDocument();
		expect(
			screen.queryByRole('button', {name: /add.breakdown/i})
		).toBeNull();
	});

	it('renders only the title and the add button without breakdowns', () => {
		const {container} = render(<WrappedComponent eventId='1' />);

		expect(
			screen.getByRole('heading', {name: /breakdown.by/i})
		).toBeInTheDocument();
		expect(
			screen.getByRole('button', {name: /add.breakdown/i})
		).toBeInTheDocument();
		expect(container.querySelector('.attribute-list')).toBeNull();
	});

	it('hides the add button when there are 5 breakdowns', () => {
		render(
			<WrappedComponent
				attributes={{
					1: {
						dataType: 'STRING',
						displayName: 'Title',
						id: '1',
						name: 'title'
					},
					123123: {
						dataType: 'STRING',
						displayName: 'Job Title',
						id: '123123',
						name: 'jobTitle'
					},
					321321: {
						dataType: 'STRING',
						displayName: 'Article Title',
						id: '321321',
						name: 'articleTitle'
					},
					400: {
						dataType: 'STRING',
						displayName: 'Author',
						id: '400',
						name: 'author'
					},
					500: {
						dataType: 'STRING',
						displayName: 'Date',
						id: '500',
						name: 'date'
					}
				}}
				breakdownOrder={['1', '321321', '123123', '400', '500']}
				breakdowns={{
					1: {
						attributeId: '1',
						dataType: 'STRING',
						type: 'event'
					},
					123123: {
						attributeId: '123123',
						dataType: 'STRING',
						type: 'event'
					},
					321321: {
						attributeId: '321321',
						dataType: 'STRING',
						type: 'event'
					},
					400: {
						attributeId: '400',
						dataType: 'STRING',
						type: 'event'
					},
					500: {
						attributeId: '500',
						dataType: 'STRING',
						type: 'event'
					}
				}}
				eventId='2'
			/>
		);

		expect(
			screen.queryByRole('button', {name: /add.breakdown/i})
		).toBeNull();
	});

	it('renders the breakdowns and focuses the section when one is removed', () => {
		const deleteBreakdown = jest.fn();

		const {container} = render(
			<WrappedComponent
				attributes={{
					123123: {
						displayName: 'Job Title',
						id: '123123',
						name: 'jobTitle'
					},
					321321: {
						displayName: 'Article Title',
						id: '321321',
						name: 'articleTitle'
					}
				}}
				breakdownOrder={['321321', '123123']}
				breakdowns={{
					123123: {
						attributeId: '123123',
						dataType: 'STRING',
						id: '123123',
						type: 'event'
					},
					321321: {
						attributeId: '321321',
						dataType: 'STRING',
						id: '321321',
						type: 'event'
					}
				}}
				deleteBreakdown={deleteBreakdown}
				eventId='2'
			/>
		);

		expect(
			container.querySelectorAll('.attribute-list .condition-chip')
		).toHaveLength(2);

		fireEvent.click(container.querySelector('.condition-chip-remove'));

		jest.runAllTimers();

		expect(deleteBreakdown).toHaveBeenCalledWith({id: '321321'});
		expect(document.activeElement).toBe(
			container.querySelector('.attribute-breakdown-section-root')
		);
	});

	describe('keyboard reordering', () => {
		const threeBreakdowns = {
			attributes: {
				1: {displayName: 'Page Title', id: '1', name: 'pageTitle'},
				2: {displayName: 'Category', id: '2', name: 'category'},
				3: {displayName: 'Href', id: '3', name: 'href'}
			},
			breakdownOrder: ['1', '2', '3'],
			breakdowns: {
				1: {attributeId: '1', dataType: 'STRING', id: '1', type: 'event'},
				2: {attributeId: '2', dataType: 'STRING', id: '2', type: 'event'},
				3: {attributeId: '3', dataType: 'STRING', id: '3', type: 'event'}
			}
		};

		it('moves a breakdown with the arrow keys and places it with Enter', () => {
			const announce = jest.fn();
			const moveBreakdown = jest.fn();

			render(
				<WrappedComponent
					{...threeBreakdowns}
					announce={announce}
					eventId='2'
					moveBreakdown={moveBreakdown}
				/>
			);

			const handle = screen.getByRole('button', {name: /drag.page title/i});

			fireEvent.keyDown(handle, {key: 'Enter'});

			expect(announce).toHaveBeenLastCalledWith(
				expect.stringMatching(/page title/i)
			);

			fireEvent.keyDown(handle, {key: 'ArrowDown'});
			fireEvent.keyDown(handle, {key: 'ArrowDown'});

			expect(announce).toHaveBeenLastCalledWith(
				expect.stringMatching(/3.*3/)
			);

			fireEvent.keyDown(handle, {key: 'Enter'});

			expect(moveBreakdown).toHaveBeenCalledWith({from: 0, to: 2});
			expect(announce).toHaveBeenLastCalledWith(
				expect.stringMatching(/page title.*3.*3/i)
			);
		});

		it('cancels the movement with Escape without moving the breakdown', () => {
			const announce = jest.fn();
			const moveBreakdown = jest.fn();

			render(
				<WrappedComponent
					{...threeBreakdowns}
					announce={announce}
					eventId='2'
					moveBreakdown={moveBreakdown}
				/>
			);

			const handle = screen.getByRole('button', {name: /drag.category/i});

			fireEvent.keyDown(handle, {key: ' '});
			fireEvent.keyDown(handle, {key: 'ArrowUp'});
			fireEvent.keyDown(handle, {key: 'Escape'});

			expect(moveBreakdown).not.toHaveBeenCalled();
			expect(announce).toHaveBeenLastCalledWith(
				expect.stringMatching(/category/i)
			);
		});

		it('jumps to the first position with Home', () => {
			const moveBreakdown = jest.fn();

			render(
				<WrappedComponent
					{...threeBreakdowns}
					eventId='2'
					moveBreakdown={moveBreakdown}
				/>
			);

			const handle = screen.getByRole('button', {name: /drag.href/i});

			fireEvent.keyDown(handle, {key: 'Enter'});
			fireEvent.keyDown(handle, {key: 'Home'});
			fireEvent.keyDown(handle, {key: 'Enter'});

			expect(moveBreakdown).toHaveBeenCalledWith({from: 2, to: 0});
		});

		it('cancels the movement when the handle loses focus', () => {
			const announce = jest.fn();
			const moveBreakdown = jest.fn();

			render(
				<WrappedComponent
					{...threeBreakdowns}
					announce={announce}
					eventId='2'
					moveBreakdown={moveBreakdown}
				/>
			);

			const handle = screen.getByRole('button', {name: /drag.category/i});

			fireEvent.keyDown(handle, {key: 'Enter'});
			fireEvent.keyDown(handle, {key: 'ArrowDown'});
			fireEvent.blur(handle);

			expect(moveBreakdown).not.toHaveBeenCalled();
			expect(announce).toHaveBeenLastCalledWith(
				expect.stringMatching(/category/i)
			);
		});

		it('reorders the breakdowns and keeps the focus on the moved handle', () => {
			render(
				<Providers>
					<AttributesProvider
						initialState={{
							...threeBreakdowns,
							filterOrder: [],
							filters: {}
						}}
					>
						<AttributeBreakdownSection eventId='2' />
					</AttributesProvider>
				</Providers>
			);

			fireEvent.keyDown(
				screen.getByRole('button', {name: /drag.page title/i}),
				{key: 'Enter'}
			);
			fireEvent.keyDown(
				screen.getByRole('button', {name: /drag.page title/i}),
				{key: 'End'}
			);
			fireEvent.keyDown(
				screen.getByRole('button', {name: /drag.page title/i}),
				{key: 'Enter'}
			);

			expect(
				screen
					.getAllByRole('button', {name: /^drag/i})
					.map((handle) => handle.getAttribute('aria-label'))
			).toEqual([
				expect.stringMatching(/category/i),
				expect.stringMatching(/href/i),
				expect.stringMatching(/page title/i)
			]);
			expect(document.activeElement).toBe(
				screen.getByRole('button', {name: /drag.page title/i})
			);
		});
	});
});
