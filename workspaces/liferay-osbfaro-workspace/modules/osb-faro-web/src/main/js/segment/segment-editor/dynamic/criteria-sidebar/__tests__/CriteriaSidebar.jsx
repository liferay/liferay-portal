import * as API from 'shared/api';
import * as data from 'test/data';
import apolloClient from 'shared/apollo/client';
import CriteriaSidebar from '../index';
import React from 'react';
import {
	cleanup,
	fireEvent,
	render,
	screen,
	waitFor,
	within
} from '@testing-library/react';
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

jest.mock('@apollo/client', () => ({
	...jest.requireActual('@apollo/client'),
	useQuery: jest.fn()
}));

jest.mock('shared/hooks/useCurrentUser', () => ({
	useCurrentUser: () => ({isAdmin: () => true})
}));

jest.mock('shared/context/dataSources', () => ({
	useDataSources: () => ({empty: false, loading: false})
}));

jest.mock('react-router-dom', () => ({
	useParams: () => ({groupId: '12345'})
}));

const campaignPropertyGroupList = new List([
	new PropertyGroup({
		label: 'Campaigns',
		propertyKey: 'campaign',
		propertySubgroups: new List([
			new PropertySubgroup({properties: new List()})
		])
	})
]);

const CampaignsSidebar = ({addEntities = jest.fn()}) => (
	<DndProvider backend={HTML5Backend}>
		<ReferencedObjectsContext.Provider
			value={{
				addEntities,
				addProperty: jest.fn(),
				referencedEntities: Map(),
				referencedProperties: Map()
			}}
		>
			<CriteriaSidebar
				channelId='123'
				groupId='456'
				propertyGroupsIList={campaignPropertyGroupList}
				type={SegmentTypes.Batch}
			/>
		</ReferencedObjectsContext.Provider>
	</DndProvider>
);

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

		expect(screen.getByText(/^conditions library$/i)).toBeInTheDocument();
		expect(
			screen.getByText(/^interests$/i, {selector: '[role="combobox"]'})
		).toBeInTheDocument();
		expect(screen.getByText(/^page views$/i)).toBeInTheDocument();
		expect(screen.getByText(/^dxp custom fields$/i)).toBeInTheDocument();
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
			screen.getByRole('combobox', {name: /^condition type$/i})
		).toHaveTextContent(/interests/i);
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

	it('lists the Campaigns section under Behavioral', () => {
		useQuery.mockReturnValue({
			data: {eventDefinitions: {eventDefinitions: [], total: 0}},
			loading: false
		});

		render(
			<DndProvider backend={HTML5Backend}>
				<CriteriaSidebar
					propertyGroupsIList={new List([
						...realTimePropertyGroupList,
						new PropertyGroup({
							label: 'Account',
							propertyKey: 'account',
							propertySubgroups: new List([
								new PropertySubgroup({properties: new List()})
							])
						}),
						new PropertyGroup({
							label: 'Campaigns',
							propertyKey: 'campaign',
							propertySubgroups: new List([
								new PropertySubgroup({properties: new List()})
							])
						})
					])}
					type={SegmentTypes.Batch}
				/>
			</DndProvider>
		);

		fireEvent.click(screen.getByRole('combobox', {name: /^condition type$/i}));

		expect(
			within(screen.getByRole('group', {name: /behavioral/i}))
				.getAllByRole('option')
				.map(({textContent}) => textContent)
		).toEqual(['Events', 'Campaigns']);
		expect(
			within(screen.getByRole('group', {name: /attributes/i}))
				.getAllByRole('option')
				.map(({textContent}) => textContent)
		).toEqual(['Account']);
	});

	describe('Campaigns section', () => {
		it('should offer to clear a search that matches no campaign', async () => {
			render(<CampaignsSidebar />);

			const searchInput = screen.getByPlaceholderText(/^search$/i);

			fireEvent.change(searchInput, {target: {value: 'Summer Launch'}});

			const clearButton = await screen.findByRole('button', {
				name: /^clear search$/i
			});

			expect(screen.getByText(/^no results were found$/i)).toBeInTheDocument();
			expect(
				screen.getByText(/review your search and try again/i)
			).toBeInTheDocument();

			fireEvent.click(clearButton);

			expect(searchInput).toHaveValue('');
		});

		it('should register the listed campaigns as referenced objects', async () => {
			API.campaigns.searchCampaignNames.mockResolvedValueOnce({
				items: [{id: 'c1', name: 'Spring Sale'}],
				totalCount: 1
			});

			const addEntities = jest.fn();

			render(<CampaignsSidebar addEntities={addEntities} />);

			await waitFor(() =>
				expect(addEntities).toHaveBeenCalledWith({
					entityType: 'campaigns',
					payload: [Map({campaignName: 'Spring Sale', id: 'c1'})]
				})
			);
		});

		it('should explain the empty section when nothing was searched', async () => {
			render(<CampaignsSidebar />);

			expect(
				await screen.findByText(/^no campaigns were found$/i)
			).toBeInTheDocument();
			expect(
				screen.queryByRole('button', {name: /^clear search$/i})
			).not.toBeInTheDocument();
		});
	});

	it('should render w/ "No Results Were Found" when propertyGroupsIList is empty', () => {
		render(
			<CriteriaSidebar
				propertyGroupsIList={new List()}
				type={SegmentTypes.Batch}
			/>
		);

		expect(
			screen.queryByText(/^no results were found$/i)
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

		expect(screen.getByText(/^default$/i)).toBeInTheDocument();
		expect(screen.getByText(/^custom$/i)).toBeInTheDocument();

		// The frontend default events render under the Default tab.

		fireEvent.click(screen.getByText(/^default$/i));

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

		expect(screen.getByText(/^default$/i)).toBeInTheDocument();
		expect(screen.getByText(/^custom$/i)).toBeInTheDocument();
		expect(screen.getByText(/^first name$/i)).toBeInTheDocument();

		fireEvent.click(screen.getByText(/^custom$/i));

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
