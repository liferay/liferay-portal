import getColumns from '../columns';
import React from 'react';
import {render} from '@testing-library/react';

jest.unmock('react-dom');

const TIME_ZONE_ID = 'America/Recife';

const renderDateColumn = (accessor, data) => {
	const {cellRenderer: CellRenderer, cellRendererProps} = getColumns(
		TIME_ZONE_ID
	).find((column) => column.accessor === accessor);

	return render(
		<table>
			<tbody>
				<tr>
					<CellRenderer data={data} {...cellRendererProps} />
				</tr>
			</tbody>
		</table>
	);
};

describe('columns', () => {
	beforeEach(() => {
		jest.useFakeTimers().setSystemTime(
			new Date('2026-06-10T14:30:00.000Z')
		);
	});

	afterEach(() => {
		jest.useRealTimers();
	});

	it('renders the created date in the given time zone', () => {
		const {container} = renderDateColumn('createDate', {
			createDate: '2026-06-05T01:30:00.000Z',
		});

		expect(container.querySelector('td')).toHaveTextContent('Jun 4, 2026');
	});

	it('renders the last modified date in the given time zone when it is older than a day', () => {
		const {container} = renderDateColumn('modifiedDate', {
			modifiedDate: '2026-06-05T01:30:00.000Z',
		});

		expect(container.querySelector('td')).toHaveTextContent('Jun 4, 2026');
	});

	it('renders the last modified date as a relative time when it is within a day', () => {
		const {container} = renderDateColumn('modifiedDate', {
			modifiedDate: '2026-06-10T12:30:00.000Z',
		});

		expect(container.querySelector('td')).toHaveTextContent('2 hours ago');
	});
});
