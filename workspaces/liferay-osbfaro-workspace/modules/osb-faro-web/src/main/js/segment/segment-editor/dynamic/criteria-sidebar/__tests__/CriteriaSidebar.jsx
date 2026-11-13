import * as data from 'test/data';
import apolloClient from 'shared/apollo/client';
import CriteriaSidebar from '../index';
import React from 'react';
import {cleanup, fireEvent, render, screen, waitFor} from '@testing-library/react';
import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';
import {List, Map} from 'immutable';
import {Property, PropertyGroup, PropertySubgroup} from 'shared/util/records';
import {ReferencedObjectsContext} from '../../context/referencedObjects';
import {SegmentTypes} from 'shared/util/constants';
import {useQuery} from '@apollo/client';

jest.mock('shared/apollo/client', () => ({
	__esModule: true,
	default: {query: jest.fn()}
}));

const mockLiferayLanguage = key => {
	const messages = {
		'add-x': 'Add {0}',
		'conditions-library': 'Conditions Library',
		custom: 'Custom',
		default: 'Default',
		event: 'event',
		'no-results-were-found': 'No Results Were Found'
	};
	return messages[key] || key;
};

global.Liferay = {
	Language: {
		get: mockLiferayLanguage
	}
};

jest.mock('@apollo/client', () => ({
	...jest.requireActual('@apollo/client'),
	useQuery: jest.fn()
}));

jest.mock('shared/hooks/useCurrentUser', () => ({
	useCurrentUser: () => ({isAdmin: () => true})
}));

jest.mock('react-router-dom', () => ({
	useParams: () => ({groupId: '12345'})
}));

jest.unmock('react-dom');

describe('CriteriaSidebar', () => {
	const fullPropertyGroupList = new List([
		new PropertyGroup({
			label: 'Interests',
			name: 'Interests',
			propertyKey: 'interests',
			propertySubgroups: new List([
				new PropertySubgroup({
					properties: new List([
						data.getImmutableMock(Property, data.mockProperty, 0, {
							label: 'Page Views',
							name: 'Page Views'
						})
					])
				}),
				new PropertySubgroup({
					label: 'DXP Custom Fields',
					properties: new List([
						data.getImmutableMock(Property, data.mockProperty, 0, {
							label: 'Page Actions',
							name: 'Page Actions'
						})
					])
				})
			])
		})
	]);

	const realTimePropertyGroupList = new List([
		new PropertyGroup({
			label: 'Events',
			name: 'Events',
			propertyKey: 'web',
			propertySubgroups: new List([
				new PropertySubgroup({
					label: 'Default Event Properties',
					properties: new List([
						data.getImmutableMock(Property, data.mockProperty, 0, {
							label: 'Asset View',
							name: 'Asset View'
						})
					])
				})
			])
		})
	]);

	const individualPropertyGroupList = new List([
		new PropertyGroup({
			label: 'Individual',
			name: 'Individual',
			propertyKey: 'individual',
			propertySubgroups: new List([
				new PropertySubgroup({
					properties: new List([
						data.getImmutableMock(Property, data.mockProperty, 0, {
							label: 'First Name',
							name: 'firstName'
						})
					])
				}),
				new PropertySubgroup({
					label: 'DXP Custom Fields',
					properties: new List([
						data.getImmutableMock(Property, data.mockProperty, 0, {
							label: 'Loyalty Tier',
							name: 'loyaltyTier'
						})
					])
				})
			])
		})
	]);

	afterEach(cleanup);

	it('should render the sidebar structure with property groups', () => {
		render(
			<DndProvider backend={HTML5Backend}>
				<CriteriaSidebar
					propertyGroupsIList={fullPropertyGroupList}
					type={SegmentTypes.Batch}
				/>
			</DndProvider>
		);

		expect(screen.getByText('Conditions Library')).toBeInTheDocument();
		expect(
			screen.getByText('Interests', {selector: '[role="combobox"]'})
		).toBeInTheDocument();
		expect(screen.getByText('Page Views')).toBeInTheDocument();
		expect(screen.getByText('DXP Custom Fields')).toBeInTheDocument();
	});

	it('names the property-group picker for assistive technologies', () => {
		render(
			<DndProvider backend={HTML5Backend}>
				<CriteriaSidebar
					propertyGroupsIList={fullPropertyGroupList}
					type={SegmentTypes.Batch}
				/>
			</DndProvider>
		);

		expect(
			screen.getByRole('combobox', {name: 'condition-type'})
		).toHaveTextContent('Interests');
	});

	it('keeps a single tab stop and moves focus between items with the arrow keys', () => {
		render(
			<DndProvider backend={HTML5Backend}>
				<CriteriaSidebar
					propertyGroupsIList={fullPropertyGroupList}
					type={SegmentTypes.Batch}
				/>
			</DndProvider>
		);

		const [pageViews, pageActions] = screen.getAllByRole('menuitem');

		expect(pageViews).toHaveAttribute('tabindex', '0');
		expect(pageActions).toHaveAttribute('tabindex', '-1');

		pageViews.focus();

		fireEvent.keyDown(pageViews, {key: 'ArrowDown'});

		expect(pageActions).toHaveFocus();
		expect(pageActions).toHaveAttribute('tabindex', '0');
		expect(pageViews).toHaveAttribute('tabindex', '-1');

		fireEvent.keyDown(pageActions, {key: 'Home'});

		expect(pageViews).toHaveFocus();

		fireEvent.keyDown(pageViews, {key: 'End'});

		expect(pageActions).toHaveFocus();
	});

	it('should render w/ "No Results Were Found" when propertyGroupsIList is empty', () => {
		render(
			<CriteriaSidebar
				propertyGroupsIList={new List()}
				type={SegmentTypes.Batch}
			/>
		);

		expect(
			screen.queryByText('No Results Were Found')
		).not.toBeInTheDocument();
	});

	it('should not render the property-group picker in Real-Time mode', () => {
		useQuery.mockReturnValue({
			data: {eventDefinitions: {eventDefinitions: [], total: 0}},
			loading: false
		});

		render(
			<DndProvider backend={HTML5Backend}>
				<CriteriaSidebar
					propertyGroupsIList={realTimePropertyGroupList}
					type={SegmentTypes.RealTime}
				/>
			</DndProvider>
		);

		expect(screen.queryByRole('combobox')).not.toBeInTheDocument();
	});

	it('renders the Default and Custom event tabs for the events section', () => {
		useQuery.mockReturnValue({
			data: {eventDefinitions: {eventDefinitions: [], total: 0}},
			loading: false
		});

		render(
			<DndProvider backend={HTML5Backend}>
				<CriteriaSidebar
					propertyGroupsIList={realTimePropertyGroupList}
					type={SegmentTypes.RealTime}
				/>
			</DndProvider>
		);

		expect(screen.getByText('Default')).toBeInTheDocument();
		expect(screen.getByText('Custom')).toBeInTheDocument();

		// The frontend default events render under the Default tab.

		fireEvent.click(screen.getByText('Default'));

		expect(screen.getByText('Asset View')).toBeInTheDocument();
	});

	it('renders the Default and Custom tabs for the individual attributes section', () => {
		render(
			<DndProvider backend={HTML5Backend}>
				<CriteriaSidebar
					propertyGroupsIList={individualPropertyGroupList}
					type={SegmentTypes.Batch}
				/>
			</DndProvider>
		);

		expect(screen.getByText('Default')).toBeInTheDocument();
		expect(screen.getByText('Custom')).toBeInTheDocument();
		expect(screen.getByText('First Name')).toBeInTheDocument();

		fireEvent.click(screen.getByText('Custom'));

		expect(screen.getByText('Loyalty Tier')).toBeInTheDocument();
	});

	it('paginates the Search Terms section the same way Tags/Vocabularies do', async () => {
		apolloClient.query.mockResolvedValueOnce({
			data: {
				searchTerms: {
					compositions: [{count: 9, name: 'shoes'}],
					totalCount: 30
				}
			}
		});

		const searchTermPropertyGroupList = new List([
			new PropertyGroup({
				label: 'Search Terms',
				name: 'Search Terms',
				propertyKey: 'search-term',
				propertySubgroups: new List([
					new PropertySubgroup({properties: new List()})
				])
			})
		]);

		render(
			<DndProvider backend={HTML5Backend}>
				<CriteriaSidebar
					channelId='123'
					groupId='12345'
					propertyGroupsIList={searchTermPropertyGroupList}
					type={SegmentTypes.Batch}
				/>
			</DndProvider>
		);

		await waitFor(() =>
			expect(screen.getByText('shoes')).toBeInTheDocument()
		);

		expect(apolloClient.query).toHaveBeenCalledWith(
			expect.objectContaining({
				variables: expect.objectContaining({
					channelId: '123',
					size: 12,
					start: 0
				})
			})
		);

		expect(
			document.querySelector('.sidebar-pagination')
		).toBeInTheDocument();
	});

	it('registers each fetched property as a referenced object', async () => {
		apolloClient.query.mockResolvedValueOnce({
			data: {
				searchTerms: {
					compositions: [{count: 9, name: 'shoes'}],
					totalCount: 30
				}
			}
		});

		const addProperty = jest.fn();

		const searchTermPropertyGroupList = new List([
			new PropertyGroup({
				label: 'Search Terms',
				name: 'Search Terms',
				propertyKey: 'search-term',
				propertySubgroups: new List([
					new PropertySubgroup({properties: new List()})
				])
			})
		]);

		render(
			<DndProvider backend={HTML5Backend}>
				<ReferencedObjectsContext.Provider
					value={{
						addProperty,
						referencedEntities: Map(),
						referencedProperties: Map()
					}}
				>
					<CriteriaSidebar
						channelId='123'
						groupId='12345'
						propertyGroupsIList={searchTermPropertyGroupList}
						type={SegmentTypes.Batch}
					/>
				</ReferencedObjectsContext.Provider>
			</DndProvider>
		);

		await waitFor(() => expect(addProperty).toHaveBeenCalled());

		expect(addProperty.mock.calls[0][0].name).toBe('shoes');
	});
});
