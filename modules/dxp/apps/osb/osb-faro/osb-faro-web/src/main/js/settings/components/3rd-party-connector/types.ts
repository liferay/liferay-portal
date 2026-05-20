import {ComponentType} from 'react';

export enum Entity {
	Accounts = 'accounts',
	Events = 'events',
	Individuals = 'individuals',
	Sites = 'sites',
	Users = 'users'
}

export interface ConnectorEntityCellProps {
	data: {
		channelId: string;
		count: number;
	};
}

export interface ConnectorEntityFetchParams {
	groupId: string;
	id: string;
}

export interface ConnectorEntityDescriptor {
	entity: Entity;
	fetchCount?: (params: ConnectorEntityFetchParams) => Promise<number>;
}

export interface ConnectorColumnDescriptor {
	accessor: string;
	cellRenderer?: ComponentType<ConnectorEntityCellProps>;
	label: string;
}

export interface Languages {
	connectDescription: string;
	connectTitle: string;
	endpointHelper: string;
	endpointLabel: string;
	tokenLabel: string;
}

export interface ConnectorConfig {
	columns?: ConnectorColumnDescriptor[];
	displayName: string;
	endpointPath: string;

	/**
	 * Entities tracked by the connector (e.g. accounts for Demandbase,
	 * events for Hubspot). The first entry is the primary entity used
	 * to derive the data-presence signal in the connection-status alert.
	 */
	entities: ConnectorEntityDescriptor[];

	helpUrl?: string;
	languages: Languages;
	requiresLDP?: boolean;
	singleton?: boolean;
	slug: string;
	type: string;
}

export enum ConnectorStatus {
	Active = 'ACTIVE',
	Disconnected = 'DISCONNECTED',
	Inactive = 'INACTIVE'
}
