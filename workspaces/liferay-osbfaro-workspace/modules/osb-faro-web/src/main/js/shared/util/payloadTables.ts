import {NO_DATA_VALUE} from 'shared/util/constants';

/**
 * The value a row shows when the payload carried the property but no value for
 * it. Rendering the property with a dash keeps a parameter the tenant did send
 * — empty — tellable apart from one it never sent at all, which produces no row.
 */
const EMPTY_VALUE = NO_DATA_VALUE;

/**
 * The payload key a caller uses to title the main table instead of leaving the
 * layout's title. It names the table, so it never becomes a row of its own.
 */
const HEADER_KEY = 'header';

/**
 * A group of attributes the payload nests one level down, under `key`. A group
 * with a `title` gets a table of its own; one without joins the main table, so
 * which table a property lands in is the payload's own classification rather
 * than a guess made from the property's name.
 *
 * `links` maps a property to the sibling property holding where it links to.
 * The link target is where the property points rather than an attribute, so it
 * never becomes a row of its own.
 */
export type PayloadGroup = {
	key: string;
	links?: Record<string, string>;
	title?: string;
};

/**
 * How a payload splits into tables: the title of the main table, which holds
 * every top-level property, and the groups nested inside the payload, whose
 * own tables follow the main one in the order they are listed.
 */
export type PayloadLayout = {
	groups: PayloadGroup[];
	title: string;
};

export type PayloadTableRow = {
	href?: string;
	property: string;
	value: string;
};

export type PayloadTable = {
	rows: PayloadTableRow[];
	title: string;
};

const formatValue = (value: unknown): string =>
	value === null || value === undefined || value === ''
		? EMPTY_VALUE
		: String(value);

const isNestedGroup = (value: unknown): value is Record<string, unknown> =>
	typeof value === 'object' && value !== null;

/**
 * A row only links when there is somewhere to go and something to click, so a
 * property the payload sent empty renders as a plain dash.
 */
const toRow = (
	property: string,
	value: unknown,
	href?: unknown
): PayloadTableRow => {
	const formattedValue = formatValue(value);

	return {
		...(typeof href === 'string' &&
			href &&
			formattedValue !== EMPTY_VALUE && {href}),
		property,
		value: formattedValue,
	};
};

const toGroupRows = (
	group: Record<string, unknown>,
	links: Record<string, string> = {}
): PayloadTableRow[] => {
	const linkTargets = new Set(Object.values(links));

	return Object.entries(group)
		.filter(([property]) => !linkTargets.has(property))
		.map(([property, value]) =>
			toRow(property, value, links[property] && group[links[property]])
		);
};

/**
 * Splits a payload into the tables a row renders when expanded, following the
 * given layout. A table with no rows is left out entirely, so a payload missing
 * one of the layout's groups renders no table for it and an empty payload
 * renders none.
 */
export const formatPayloadTables = (
	payload: Record<string, unknown>,
	{groups, title}: PayloadLayout
): PayloadTable[] => {
	const groupsByKey = new Map(groups.map((group) => [group.key, group]));

	const rowsByGroup = new Map<PayloadGroup, PayloadTableRow[]>();

	const mainTable: PayloadTable = {rows: [], title};

	Object.entries(payload).forEach(([key, value]) => {
		const group = groupsByKey.get(key);

		if (key === HEADER_KEY) {
			mainTable.title = String(value);
		}
		else if (!group) {
			mainTable.rows.push(toRow(key, value));
		}
		else if (isNestedGroup(value)) {
			const rows = toGroupRows(value, group.links);

			if (group.title) {
				rowsByGroup.set(group, rows);
			}
			else {
				mainTable.rows.push(...rows);
			}
		}
	});

	const groupTables = groups.flatMap((group) =>
		group.title
			? [{rows: rowsByGroup.get(group) ?? [], title: group.title}]
			: []
	);

	return [mainTable, ...groupTables].filter(({rows}) => rows.length);
};
