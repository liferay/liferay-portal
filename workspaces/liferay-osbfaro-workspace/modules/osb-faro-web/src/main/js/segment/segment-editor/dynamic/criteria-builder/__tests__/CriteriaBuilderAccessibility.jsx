import client from 'shared/apollo/client';
import CriteriaBuilder from '../index';
import mockStore from 'test/mock-store';
import React from 'react';
import {ApolloProvider} from '@apollo/client';
import {cleanup, render} from '@testing-library/react';
import {Conjunctions} from '../../utils/constants';
import {DndProvider} from 'react-dnd';
import {expectNoAccessibilityViolations} from 'test/accessibility';
import {HTML5Backend} from 'react-dnd-html5-backend';
import {Map} from 'immutable';
import {MockedProvider} from '@apollo/client/testing';
import {mockPreferenceReq} from 'test/graphql-data';
import {Provider} from 'react-redux';

const makeRow = (rowId, propertyName) => ({
	operatorName: 'eq',
	propertyName,
	rowId,
	touched: false,
	valid: true,
	value: '2026-01-15'
});

const criteria = {
	conjunctionName: Conjunctions.And,
	criteriaGroupId: 'root',
	items: [
		makeRow('row_0', 'knownSinceDate'),
		{
			conjunctionName: Conjunctions.Or,
			criteriaGroupId: 'nested',
			items: [
				makeRow('row_1', 'knownSinceDate'),
				makeRow('row_2', 'removedAttribute')
			]
		}
	]
};

describe('CriteriaBuilder accessibility', () => {

	afterEach(cleanup);

	it('should have no axe violations in the groups, rows and operators', async () => {
		const {container} = render(
			<ApolloProvider client={client}>
				<Provider store={mockStore()}>
					<MockedProvider mocks={[mockPreferenceReq()]}>
						<DndProvider backend={HTML5Backend}>
							<CriteriaBuilder
								criteria={criteria}
								onChange={() => {}}
								referencedAssetsIMap={new Map()}
								referencedPropertiesIMap={new Map()}
							/>
						</DndProvider>
					</MockedProvider>
				</Provider>
			</ApolloProvider>
		);

		// The value inputs (here, the date range dropdown) are covered by their
		// own stories, so they are left out of this check.

		await expectNoAccessibilityViolations(container, {
			exclude: ['.dropdown-range-key-root'],
		});
	});
});
