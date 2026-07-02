import {ConnectorStatus} from '../types';
import {getInitialLogEntries} from '../getConnectorStatusItems';

describe('getInitialLogEntries', () => {
	it('seeds a single Token generated entry for Inactive with no data', () => {
		const entries = getInitialLogEntries(ConnectorStatus.Inactive, 0);

		expect(entries).toHaveLength(1);
		expect(entries[0].title).toBe('Token Generated');
	});

	it('falls back to a single Token generated entry for Active with no data (a state that should no longer occur)', () => {
		const entries = getInitialLogEntries(ConnectorStatus.Active, 0);

		expect(entries).toHaveLength(1);
		expect(entries[0].title).toBe('Token Generated');
	});

	it('seeds Data flow, Listening, Token generated for Active with data', () => {
		const entries = getInitialLogEntries(ConnectorStatus.Active, 5);

		expect(entries).toHaveLength(3);
		expect(entries[0].title).toBe('Data Flow Established');
		expect(entries[0].iconDisplayType).toBe('success');
		expect(entries[1].title).toBe('Listening...');
		expect(entries[2].title).toBe('Token Generated');
	});

	it('seeds Inactive data flow and prior history for Inactive with data', () => {
		const entries = getInitialLogEntries(ConnectorStatus.Inactive, 10);

		expect(entries).toHaveLength(4);
		expect(entries[0].title).toBe('Inactive Data Flow');
		expect(entries[1].title).toBe('Data Flow Established');
		expect(entries[2].title).toBe('Listening...');
		expect(entries[3].title).toBe('Token Generated');
	});

	it('shows only the Data source disconnected entry for Disconnected status', () => {
		const entries = getInitialLogEntries(ConnectorStatus.Disconnected, 0);

		expect(entries).toHaveLength(1);
		expect(entries[0].title).toBe('Data Source Disconnected');
	});
});

describe('item shape', () => {
	it('every entry from getInitialLogEntries has all required fields', () => {
		const allSeeds = [
			getInitialLogEntries(ConnectorStatus.Inactive, 0),
			getInitialLogEntries(ConnectorStatus.Active, 0),
			getInitialLogEntries(ConnectorStatus.Active, 1),
			getInitialLogEntries(ConnectorStatus.Inactive, 1),
			getInitialLogEntries(ConnectorStatus.Disconnected, 0),
		];

		allSeeds.forEach((entries) => {
			entries.forEach((entry) => {
				expect(typeof entry.bold).toBe('boolean');
				expect(typeof entry.icon).toBe('string');
				expect(['secondary', 'success']).toContain(
					entry.iconDisplayType
				);
				expect(typeof entry.title).toBe('string');
				expect(typeof entry.secondaryText).toBe('string');
			});
		});
	});
});
