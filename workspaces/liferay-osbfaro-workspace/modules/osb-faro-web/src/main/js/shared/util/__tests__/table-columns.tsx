import React from 'react';
import Row from 'shared/components/table/Row';
import {
	accountsListColumns,
	changesListColumns,
	eventListColumns,
	individualsListColumns,
	IndividualsListCDPColumns,
	membershipChangesColumns,
	metricsListColumns,
	pagesListColumns,
	segmentsListColumns,
	usersListColumns,
} from '../table-columns';
import {MemoryRouter} from 'react-router';
import {render} from '@testing-library/react';

jest.unmock('react-dom');

describe('accountsListColumns', () => {
	it('activitiesCount should format a number with locale-aware grouping', () => {
		expect(accountsListColumns.activitiesCount.dataFormatter(1234567)).toBe(
			'1,234,567'
		);
	});

	it('activitiesCount should format a numeric string with locale-aware grouping', () => {
		expect(
			accountsListColumns.activitiesCount.dataFormatter('1234567')
		).toBe('1,234,567');
	});
});

describe('metricsListColumns', () => {
	it('ratingsMetric should format the rating out of 10, stripping trailing zeros', () => {
		expect(metricsListColumns.ratingsMetric.dataFormatter(0.8)).toBe(
			'8/10'
		);
		expect(metricsListColumns.ratingsMetric.dataFormatter(0.753)).toBe(
			'7.53/10'
		);
	});
});

describe('pagesListColumns', () => {
	const renderInactiveViewCount = (viewCount?: number | null) =>
		render(
			<table>
				<tbody>
					<Row
						columns={[pagesListColumns.inactiveViewCount]}
						data={{viewCount}}
						onRowDelete={jest.fn()}
						onRowSave={jest.fn()}
						rowIndex={0}
					/>
				</tbody>
			</table>
		);

	it('inactiveViewCount should render the view count', () => {
		const {getByText} = renderInactiveViewCount(12);

		expect(getByText('12')).toBeInTheDocument();
	});

	it('inactiveViewCount should render a zero view count', () => {
		const {getByText} = renderInactiveViewCount(0);

		expect(getByText('0')).toBeInTheDocument();
	});
});

const NOW = '2026-06-10T14:30:00Z';

const renderCell = (
	CellRenderer: React.ComponentType<any>,
	props: {[key: string]: any}
) =>
	render(
		<table>
			<tbody>
				<tr>
					<CellRenderer {...props} />
				</tr>
			</tbody>
		</table>
	);

const freezeTime = () => {
	jest.useFakeTimers().setSystemTime(new Date(NOW));
};

describe('membershipChangesColumns', () => {
	it('renders the first seen date as a long UTC date', () => {
		const {getByText} = renderCell(
			membershipChangesColumns.firstSeen.cellRenderer,
			{data: {firstSeenTime: '2026-06-10T23:30:00Z'}}
		);

		expect(getByText('June 10, 2026')).toBeInTheDocument();
	});

	it('renders the last active date as a long UTC date', () => {
		const {getByText} = renderCell(
			membershipChangesColumns.lastActive.cellRenderer,
			{data: {lastActivityTime: '2026-06-10T23:30:00Z'}}
		);

		expect(getByText('June 10, 2026')).toBeInTheDocument();
	});
});

describe('membershipChangesColumns no data', () => {
	it.each([null, undefined, ''])(
		'renders a %p first seen date as no data',
		(firstSeenTime) => {
			const {getByText} = renderCell(
				membershipChangesColumns.firstSeen.cellRenderer,
				{data: {firstSeenTime}}
			);

			expect(getByText('–')).toHaveClass('text-secondary');
		}
	);

	it.each([null, undefined, ''])(
		'renders a %p last active date as no data',
		(lastActivityTime) => {
			const {getByText} = renderCell(
				membershipChangesColumns.lastActive.cellRenderer,
				{data: {lastActivityTime}}
			);

			expect(getByText('–')).toHaveClass('text-secondary');
		}
	);
});

describe('IndividualsListCDPColumns', () => {
	it('renders the first seen date as a long UTC date', () => {
		const {getByText} = renderCell(
			IndividualsListCDPColumns.firstSeen.cellRenderer,
			{data: {firstActivityDate: '2026-06-10T23:30:00Z'}}
		);

		expect(getByText('June 10, 2026')).toBeInTheDocument();
	});

	it('renders the last active date as a long UTC date', () => {
		const {getByText} = renderCell(
			IndividualsListCDPColumns.lastActive.cellRenderer,
			{data: {lastActivityDate: '2026-06-10T23:30:00Z'}}
		);

		expect(getByText('June 10, 2026')).toBeInTheDocument();
	});

	it.each([null, undefined, ''])(
		'renders a %p account name as no data',
		(accountName) => {
			const {getByText} = renderCell(
				IndividualsListCDPColumns.accountNames.cellRenderer,
				{data: {accountName}}
			);

			expect(getByText('–')).toHaveClass('text-secondary');
		}
	);

	it.each([null, undefined, ''])(
		'renders a %p country as no data',
		(country) => {
			const {getByText} = renderCell(
				IndividualsListCDPColumns.country.cellRenderer,
				{data: {properties: {country}}}
			);

			expect(getByText('–')).toHaveClass('text-secondary');
		}
	);

	it.each([null, undefined, ''])(
		'renders a %p first seen date as no data',
		(firstActivityDate) => {
			const {getByText} = renderCell(
				IndividualsListCDPColumns.firstSeen.cellRenderer,
				{data: {firstActivityDate}}
			);

			expect(getByText('–')).toHaveClass('text-secondary');
		}
	);

	it.each([null, undefined, ''])(
		'renders a %p last active date as no data',
		(lastActivityDate) => {
			const {getByText} = renderCell(
				IndividualsListCDPColumns.lastActive.cellRenderer,
				{data: {lastActivityDate}}
			);

			expect(getByText('–')).toHaveClass('text-secondary');
		}
	);
});

describe('changesListColumns', () => {
	afterEach(() => {
		jest.useRealTimers();
	});

	it('renders the first seen date in the given time zone', () => {
		expect(
			changesListColumns
				.getDateFirst('America/Recife')
				.dataFormatter('2026-06-10T01:30:00Z')
		).toBe('Jun 9, 2026');
	});

	it.each([
		['Today at 12:15 PM', 'UTC', '2026-06-10T12:15:00Z'],
		['Yesterday at 10:30 PM', 'America/Recife', '2026-06-10T01:30:00Z'],
		['Apr 30, 2026', 'America/Recife', '2026-05-01T01:30:00Z'],
	])(
		'renders the membership change date as %s',
		(expected, timeZoneId, dateChanged) => {
			freezeTime();

			const {getByText} = render(
				<div>
					{changesListColumns
						.getOperation(timeZoneId)
						.dataFormatter(null, {dateChanged, operation: 'ADDED'})}
				</div>
			);

			expect(getByText(expected)).toBeInTheDocument();
		}
	);
});

describe('changesListColumns no data', () => {
	const renderRow = (column: any, data: {[key: string]: any}) =>
		render(
			<table>
				<tbody>
					<Row
						columns={[column]}
						data={data}
						onRowDelete={jest.fn()}
						onRowSave={jest.fn()}
						rowIndex={0}
					/>
				</tbody>
			</table>
		);

	it.each([null, undefined, ''])(
		'renders a %p first seen date as no data',
		(dateFirst) => {
			const {getByText} = renderRow(
				changesListColumns.getDateFirst('UTC'),
				{dateFirst, id: '1'}
			);

			expect(getByText('–')).toHaveClass('text-secondary');
		}
	);

	it.each([null, undefined, ''])(
		'renders a %p email as no data',
		(individualEmail) => {
			const {getByText} = renderRow(changesListColumns.individualEmail, {
				id: '1',
				individualEmail,
			});

			expect(getByText('–')).toHaveClass('text-secondary');
		}
	);

	it.each([null, undefined, ''])(
		'renders a %p membership change date as no data',
		(dateChanged) => {
			const {getByText} = renderRow(
				changesListColumns.getOperation('UTC'),
				{dateChanged, id: '1', operation: 'ADDED'}
			);

			expect(getByText('–')).toHaveClass('text-secondary');
		}
	);
});

describe('individualsListColumns', () => {
	it('renders the date created in the given time zone', () => {
		const {getByText} = renderCell(
			individualsListColumns.getDateCreated('America/Recife')
				.cellRenderer,
			{data: {dateCreated: '2026-06-10T01:30:00Z'}}
		);

		expect(getByText('Jun 9, 2026')).toBeInTheDocument();
	});

	it('renders the last activity date in the given time zone', () => {
		expect(
			individualsListColumns
				.getLastActivityDate('America/Recife')
				.dataFormatter('2026-06-10T01:30:00Z')
		).toBe('Jun 9, 2026');
	});
});

describe('metricsListColumns dates', () => {
	it('renders the create date in the given time zone', () => {
		const {getByText} = renderCell(
			metricsListColumns.getCreateDate('America/Recife').cellRenderer,
			{data: {createDate: '2026-06-10T01:30:00Z'}}
		);

		expect(getByText('Jun 9, 2026')).toBeInTheDocument();
	});

	it('renders the modified date with the modifier name', () => {
		const {getByText} = renderCell(
			metricsListColumns.modifiedDate.cellRenderer,
			{
				data: {
					modifiedByUserName: 'Test Test',
					modifiedDate: '2026-06-10T12:15:00Z',
				},
			}
		);

		expect(
			getByText('Jun 10, 2026 Last Modified by Test Test')
		).toBeInTheDocument();
	});

	it('renders a missing modified date as no data', () => {
		const {getByText} = renderCell(
			metricsListColumns.modifiedDate.cellRenderer,
			{data: {modifiedByUserName: 'Test Test', modifiedDate: null}}
		);

		expect(getByText('–')).toHaveClass('text-secondary');
	});
});

describe('segmentsListColumns', () => {
	afterEach(() => {
		jest.useRealTimers();
	});

	it.each([
		['Today at 12:15 PM', '2026-06-10T12:15:00Z'],
		['Yesterday at 12:15 PM', '2026-06-09T12:15:00Z'],
		['May 1, 2026', '2026-05-01T12:15:00Z'],
	])(
		'renders the individual added date as %s',
		(expected, individualAddedDate) => {
			freezeTime();

			const {cellRendererProps} = segmentsListColumns.individualAddedDate;

			const {getByText} = renderCell(
				segmentsListColumns.individualAddedDate.cellRenderer,
				{...cellRendererProps, data: {individualAddedDate}}
			);

			expect(getByText(expected)).toBeInTheDocument();
		}
	);
});

describe('individualsListColumns no data', () => {
	const renderRow = (column: any, data: {[key: string]: any}) =>
		render(
			<table>
				<tbody>
					<Row
						columns={[column]}
						data={data}
						onRowDelete={jest.fn()}
						onRowSave={jest.fn()}
						rowIndex={0}
					/>
				</tbody>
			</table>
		);

	it.each([null, undefined, ''])('renders a %p email as no data', (email) => {
		const {getByText} = renderRow(individualsListColumns.email, {
			id: '1',
			properties: {email},
		});

		expect(getByText('–')).toHaveClass('text-secondary');
	});

	it.each([null, undefined, ''])(
		'renders a %p last activity date as no data',
		(lastActivityDate) => {
			const {getByText} = renderRow(
				individualsListColumns.getLastActivityDate('UTC'),
				{id: '1', lastActivityDate}
			);

			expect(getByText('–')).toHaveClass('text-secondary');
		}
	);

	it.each([null, undefined, ''])(
		'renders a %p job title as no data',
		(jobTitle) => {
			const {cellRenderer: NameJobTitleCell, cellRendererProps} =
				individualsListColumns.getNameJobTitle({
					channelId: '1',
					groupId: '2',
				});

			const {getByText} = render(
				<MemoryRouter>
					<table>
						<tbody>
							<tr>
								<NameJobTitleCell
									{...cellRendererProps}
									data={{
										id: '3',
										name: 'foo',
										properties: {jobTitle},
									}}
								/>
							</tr>
						</tbody>
					</table>
				</MemoryRouter>
			);

			expect(getByText('–')).toHaveClass('text-secondary');
		}
	);
});

describe('usersListColumns', () => {
	afterEach(() => {
		jest.useRealTimers();
	});

	it.each([
		['Today at 12:15 PM', 'UTC', '2026-06-10T12:15:00Z'],
		['Yesterday at 10:30 PM', 'America/Recife', '2026-06-10T01:30:00Z'],
		['Apr 30, 2026', 'America/Recife', '2026-05-01T01:30:00Z'],
	])(
		'renders the last login date as %s',
		(expected, timeZoneId, lastLoginDate) => {
			freezeTime();

			const {cellRenderer, cellRendererProps} =
				usersListColumns.getLastLoginDate(timeZoneId);

			const {getByText} = renderCell(cellRenderer, {
				...cellRendererProps,
				data: {lastLoginDate},
			});

			expect(getByText(expected)).toBeInTheDocument();
		}
	);
});

describe('eventListColumns', () => {
	it.each(['description', 'lastSeenURL'] as const)(
		'%s should render the value',
		(field) => {
			const {getByText} = renderCell(
				eventListColumns[field].cellRenderer,
				{data: {[field]: 'Test value', hidden: false}}
			);

			expect(getByText('Test value')).toBeInTheDocument();
		}
	);

	it.each([
		['description', null],
		['description', ''],
		['lastSeenURL', null],
		['lastSeenURL', ''],
	] as const)('%s should render %p as no data', (field, value) => {
		const {getByText} = renderCell(eventListColumns[field].cellRenderer, {
			data: {[field]: value, hidden: false},
		});

		expect(getByText('–')).toHaveClass('text-secondary');
	});
});
