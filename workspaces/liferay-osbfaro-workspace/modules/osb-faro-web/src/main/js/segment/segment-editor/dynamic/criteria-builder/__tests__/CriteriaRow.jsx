import client from 'shared/apollo/client';
import CriteriaRow from '../CriteriaRow';
import mockStore from 'test/mock-store';
import React from 'react';
import {ApolloProvider} from '@apollo/client';
import {CAMPAIGN_PROPERTY} from '../../utils/properties';
import {cleanup, fireEvent, render, screen} from '@testing-library/react';
import {DndProvider} from 'react-dnd';
import {fromJS, List, Map} from 'immutable';
import {getDefaultValue} from '../../criteria-sidebar/CriteriaSidebarCollapse';
import {HTML5Backend} from 'react-dnd-html5-backend';
import {MockedProvider} from '@apollo/client/testing';
import {mockPreferenceReq} from 'test/graphql-data';
import {Provider} from 'react-redux';
import {ReferencedObjectsContext} from '../../context/referencedObjects';

const WrapperComponent = ({children}) => (
	<ApolloProvider client={client}>
		<Provider store={mockStore()}>
			<MockedProvider mocks={[mockPreferenceReq()]}>
				{children}
			</MockedProvider>
		</Provider>
	</ApolloProvider>
);

describe('CriteriaRow', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {getAllByText, getByText} = render(
			<WrapperComponent>
				<DndProvider backend={HTML5Backend}>
					<CriteriaRow
						criterion={{
							operatorName: 'sessions-filter',
							propertyName: 'context/referrer',
							rowId: 'row_0',
							touched: false,
							valid: true,
							value: fromJS({
								criterionGroup: {
									conjunctionName: 'and',
									criteriaGroupId: 'group_0',
									items: [
										{
											operatorName: 'ne',
											propertyName: 'context/referrer',
											rowId: 'row_1',
											touched: false,
											valid: true
										},
										{
											operatorName: 'gt',
											propertyName: 'completeDate',
											rowId: 'row_2',
											touched: false,
											valid: true
										}
									]
								}
							})
						}}
						referencedAssetsIMap={new Map()}
						referencedPropertiesIMap={new Map()}
					/>
				</DndProvider>
			</WrapperComponent>
		);

		fireEvent.click(getByText('is not'));

		expect(getByText('is')).toBeInTheDocument();
		expect(getAllByText('is not')[1]).toBeInTheDocument();
		expect(getByText('contains')).toBeInTheDocument();
		expect(getByText('does not contain')).toBeInTheDocument();
		expect(getByText('is known')).toBeInTheDocument();
		expect(getByText('is unknown')).toBeInTheDocument();
	});

	it('should name the row with its attribute, operator and value, and its operator picker', () => {
		const {getByRole} = render(
			<WrapperComponent>
				<DndProvider backend={HTML5Backend}>
					<CriteriaRow
						criterion={{
							operatorName: 'eq',
							propertyName: 'knownSinceDate',
							rowId: 'row_0',
							touched: false,
							valid: true,
							value: '2026-01-15'
						}}
						referencedAssetsIMap={new Map()}
						referencedPropertiesIMap={new Map()}
					/>
				</DndProvider>
			</WrapperComponent>
		);

		expect(
			getByRole('group', {name: /known since date.*2026/i})
		).toBeInTheDocument();
		expect(getByRole('combobox', {name: /^operator$/i})).toBeInTheDocument();
		expect(getByRole('button', {name: /^menu$/i})).toBeInTheDocument();
	});

	it('should render w/ Non-Existent Property message and only the delete action', () => {
		const onDelete = jest.fn();

		const {getByRole, queryByRole} = render(
			<WrapperComponent>
				<DndProvider backend={HTML5Backend}>
					<CriteriaRow
						index={3}
						onDelete={onDelete}
						referencedAssetsIMap={new Map()}
						referencedPropertiesIMap={new Map()}
					/>
				</DndProvider>
			</WrapperComponent>
		);

		expect(getByRole('alert')).toHaveTextContent(
			/^attribute no longer exists\.$/i
		);
		expect(queryByRole('button', {name: /^menu$/i})).not.toBeInTheDocument();

		fireEvent.click(getByRole('button', {name: /^delete$/i}));

		expect(onDelete).toHaveBeenCalledWith(3);
	});

	describe('campaign criterion', () => {
		const renderCampaignRow = ({onDelete = jest.fn()} = {}) =>
			render(
				<WrapperComponent>
					<ReferencedObjectsContext.Provider
						value={{
							referencedEntities: fromJS({
								campaigns: {c1: {campaignName: 'Spring Sale', id: 'c1'}}
							}),
							referencedProperties: Map()
						}}
					>
						<DndProvider backend={HTML5Backend}>
							<CriteriaRow
								criterion={{
									operatorName: 'campaigns-filter',
									propertyName: 'id',
									rowId: 'row_0',
									touched: false,
									valid: true,
									value: getDefaultValue(
										CAMPAIGN_PROPERTY
									).setIn(
										['criterionGroup', 'items', 0, 'value'],
										List([campaignId])
									)
								}}
								index={0}
								onDelete={onDelete}
							/>
						</DndProvider>
					</ReferencedObjectsContext.Provider>
				</WrapperComponent>
			);

		let campaignId;

		it('should show the name of a campaign that still exists', () => {
			campaignId = 'c1';

			renderCampaignRow();

			expect(screen.getByText('Spring Sale')).toBeInTheDocument();
			expect(
				screen.queryByText('Campaign no longer exists.')
			).not.toBeInTheDocument();
		});

		it('should only offer to remove a campaign that no longer exists', () => {
			campaignId = 'deleted';

			const onDelete = jest.fn();

			renderCampaignRow({onDelete});

			expect(
				screen.getByText('Campaign no longer exists.')
			).toBeInTheDocument();
			expect(screen.queryByText('Duplicate')).not.toBeInTheDocument();

			fireEvent.click(screen.getByRole('button', {name: 'Delete'}));

			expect(onDelete).toHaveBeenCalledWith(0);
		});
	});
});
