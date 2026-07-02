import {ConnectorStatus} from './types';

export interface ConnectorStatusItem {
	bold: boolean;
	icon: string;
	iconDisplayType: 'secondary' | 'success';
	secondaryText: string;
	title: string;
}

const DATA_FLOW_ACTIVE: ConnectorStatusItem = {
	bold: true,
	icon: 'plug',
	iconDisplayType: 'success',
	secondaryText: Liferay.Language.get('data-received-successfully'),
	title: Liferay.Language.get('data-flow-established'),
};

const DATA_SOURCE_DISCONNECTED: ConnectorStatusItem = {
	bold: true,
	icon: 'plug',
	iconDisplayType: 'secondary',
	secondaryText: Liferay.Language.get('token-revoked-successfully'),
	title: Liferay.Language.get('data-source-disconnected'),
};

const INACTIVE_DATA_FLOW: ConnectorStatusItem = {
	bold: true,
	icon: 'voice',
	iconDisplayType: 'secondary',
	secondaryText: Liferay.Language.get(
		'there-were-no-activities-in-the-past-90-days'
	),
	title: Liferay.Language.get('inactive-data-flow'),
};

const LISTENING: ConnectorStatusItem = {
	bold: true,
	icon: 'voice',
	iconDisplayType: 'success',
	secondaryText: Liferay.Language.get(
		'waiting-for-first-data-to-arrive-this-may-take-some-time'
	),
	title: Liferay.Language.get('listening'),
};

const TOKEN_GENERATED: ConnectorStatusItem = {
	bold: true,
	icon: 'check-circle-full',
	iconDisplayType: 'success',
	secondaryText: Liferay.Language.get(
		'liferay-data-platform-setup-completed'
	),
	title: Liferay.Language.get('token-generated'),
};

export function getInitialLogEntries(
	status: ConnectorStatus,
	entityCount: number
): ConnectorStatusItem[] {
	if (status === ConnectorStatus.Disconnected) {
		return [DATA_SOURCE_DISCONNECTED];
	}

	if (status === ConnectorStatus.Active && entityCount > 0) {
		return [DATA_FLOW_ACTIVE, LISTENING, TOKEN_GENERATED];
	}

	if (entityCount > 0) {
		return [
			INACTIVE_DATA_FLOW,
			DATA_FLOW_ACTIVE,
			LISTENING,
			TOKEN_GENERATED,
		];
	}

	return [TOKEN_GENERATED];
}
