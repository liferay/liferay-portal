jest.unmock('react-dom');

import ConnectorEntities from '../ConnectorEntities';
import React from 'react';
import {ConnectorStatus, Entity} from '../types';
import {render} from '@testing-library/react';

describe('ConnectorEntities', () => {
	it('renders one item per entity with its derived label', () => {
		const {getByText} = render(
			<ConnectorEntities
				entities={[{entity: Entity.Accounts}, {entity: Entity.Events}]}
				syncedCounts={{}}
			/>
		);

		expect(getByText('Accounts')).toBeTruthy();
		expect(getByText('Events')).toBeTruthy();
	});

	it('renders the synced count when it is a non-negative number', () => {
		const {getByText} = render(
			<ConnectorEntities
				entities={[{entity: Entity.Accounts}]}
				syncedCounts={{[Entity.Accounts]: 42}}
			/>
		);

		expect(getByText(/42/)).toBeTruthy();
	});

	it('renders the synced count with comma separators when it is large', () => {
		const {getByText} = render(
			<ConnectorEntities
				entities={[{entity: Entity.Accounts}]}
				syncedCounts={{[Entity.Accounts]: 279089}}
			/>
		);

		expect(getByText('279,089 Items Synced')).toBeTruthy();
	});

	it('renders the synced count when it is zero', () => {
		const {getByText} = render(
			<ConnectorEntities
				entities={[{entity: Entity.Accounts}]}
				syncedCounts={{[Entity.Accounts]: 0}}
			/>
		);

		expect(getByText(/0/)).toBeTruthy();
	});

	it('hides the synced count when no value is provided', () => {
		const {queryByText} = render(
			<ConnectorEntities
				entities={[{entity: Entity.Accounts}]}
				syncedCounts={{}}
			/>
		);

		expect(queryByText(/Items Synced/i)).toBeNull();
	});

	it('renders the Configured label with success display when count is greater than zero', () => {
		const {getByText} = render(
			<ConnectorEntities
				entities={[{entity: Entity.Accounts}]}
				syncedCounts={{[Entity.Accounts]: 5}}
			/>
		);

		expect(getByText('Configured')).toBeTruthy();
	});

	it('renders the Unconfigured label when count is zero', () => {
		const {getByText} = render(
			<ConnectorEntities
				entities={[{entity: Entity.Accounts}]}
				syncedCounts={{[Entity.Accounts]: 0}}
			/>
		);

		expect(getByText('Unconfigured')).toBeTruthy();
	});

	it('renders the Unconfigured label when no count is provided', () => {
		const {getByText} = render(
			<ConnectorEntities
				entities={[{entity: Entity.Accounts}]}
				syncedCounts={{}}
			/>
		);

		expect(getByText('Unconfigured')).toBeTruthy();
	});

	it('renders the Unconfigured label when connector status is Disconnected even with count greater than zero', () => {
		const {getByText} = render(
			<ConnectorEntities
				connectorStatus={ConnectorStatus.Disconnected}
				entities={[{entity: Entity.Accounts}]}
				syncedCounts={{[Entity.Accounts]: 5}}
			/>
		);

		expect(getByText('Unconfigured')).toBeTruthy();
	});

	it('renders the Configured label when connector status is Active and count is greater than zero', () => {
		const {getByText} = render(
			<ConnectorEntities
				connectorStatus={ConnectorStatus.Active}
				entities={[{entity: Entity.Accounts}]}
				syncedCounts={{[Entity.Accounts]: 5}}
			/>
		);

		expect(getByText('Configured')).toBeTruthy();
	});

	it('renders the Configured label when connector status is Inactive and count is greater than zero', () => {
		const {getByText} = render(
			<ConnectorEntities
				connectorStatus={ConnectorStatus.Inactive}
				entities={[{entity: Entity.Accounts}]}
				syncedCounts={{[Entity.Accounts]: 5}}
			/>
		);

		expect(getByText('Configured')).toBeTruthy();
	});

	it('renders nothing inside the list when no entities are provided', () => {
		const {container} = render(
			<ConnectorEntities entities={[]} syncedCounts={{}} />
		);

		expect(container.querySelectorAll('.list-group-item')).toHaveLength(0);
	});
});
