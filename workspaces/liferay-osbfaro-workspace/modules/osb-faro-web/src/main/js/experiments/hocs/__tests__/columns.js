import getColumns from '../columns';
import React from 'react';
import {MemoryRouter} from 'react-router-dom';
import {render, screen} from '@testing-library/react';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		channelId: '2000',
		groupId: '1000',
	}),
}));

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

const renderNameColumn = (data) => {
	const {cellRenderer: CellRenderer} = getColumns(TIME_ZONE_ID).find(
		(column) => column.accessor === 'name'
	);

	return render(
		<MemoryRouter>
			<table>
				<tbody>
					<tr>
						<CellRenderer data={data} />
					</tr>
				</tbody>
			</table>
		</MemoryRouter>
	);
};

describe('columns', () => {
	it('renders the experiment title', () => {
		renderNameColumn({id: '1', name: 'My test', pageURL: 'https://a.com'});

		expect(screen.getByText('My test')).toBeInTheDocument();
	});

	it('renders a missing experiment title as no data', () => {
		const {container} = renderNameColumn({
			id: '1',
			name: '',
			pageURL: 'https://a.com',
		});

		expect(container.querySelector('td')).toHaveTextContent('–');
		expect(screen.getByText('–')).toHaveClass('text-secondary');
	});

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
