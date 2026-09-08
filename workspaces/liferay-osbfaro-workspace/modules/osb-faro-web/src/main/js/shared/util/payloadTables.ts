/**
 * The value a row shows when the payload carried the property but no value for
 * it. Rendering the property with a dash keeps a parameter the tenant did send
 * — empty — tellable apart from one it never sent at all, which produces no row.
 */
const EMPTY_VALUE = '-';

/**
 * The payload key a caller uses to title the table instead of leaving the
 * default. It names the table, so it never becomes a row of its own.
 */
const HEADER_KEY = 'header';

/**
 * The payload key holding the event's own attributes, which arrive nested one
 * level down. They read as attributes like any other, so they are flattened
 * into the same table rather than shown as a nested structure.
 */
const PROPERTIES_KEY = 'properties';

export type PayloadTableRow = {
	property: string;
	value: string;
};

export type PayloadTable = {
	rows: PayloadTableRow[];
	title: string;
};

/**
 * Whether a property carries acquisition data, which the tables show apart from
 * the rest of the payload. The match is on the `utm` prefix rather than a fixed
 * list of parameters, so a tenant's custom `utm_*` parameter lands in the UTM
 * table alongside the standard five, and neither `utm_medium` nor `utmMedium`
 * depends on which spelling the API settles on.
 */
const isUtmProperty = (property: string): boolean =>
	property.toLowerCase().startsWith('utm');

const formatValue = (value: unknown): string =>
	value === null || value === undefined || value === ''
		? EMPTY_VALUE
		: String(value);

const isNestedProperties = (
	key: string,
	value: unknown
): value is Record<string, unknown> =>
	key === PROPERTIES_KEY && typeof value === 'object' && value !== null;

/**
 * Splits an event or session payload into the tables the activity timeline
 * renders when a row is expanded: the payload's own attributes, and the
 * acquisition parameters that came with the touch. A table with no rows is
 * left out entirely, so a payload carrying no UTM parameter renders one table
 * and an empty payload renders none.
 */
export const formatPayloadTables = (
	payload: Record<string, unknown>
): PayloadTable[] => {
	const attributeRows: PayloadTableRow[] = [];
	const utmRows: PayloadTableRow[] = [];

	let title = Liferay.Language.get('event-attributes');

	const addRow = (property: string, value: unknown) => {
		const row = {property, value: formatValue(value)};

		if (isUtmProperty(property)) {
			utmRows.push(row);
		}
		else {
			attributeRows.push(row);
		}
	};

	Object.entries(payload).forEach(([key, value]) => {
		if (key === HEADER_KEY) {
			title = String(value);
		}
		else if (isNestedProperties(key, value)) {
			Object.entries(value).forEach(([nestedKey, nestedValue]) =>
				addRow(nestedKey, nestedValue)
			);
		}
		else {
			addRow(key, value);
		}
	});

	const tables: PayloadTable[] = [];

	if (attributeRows.length) {
		tables.push({rows: attributeRows, title});
	}

	if (utmRows.length) {
		tables.push({
			rows: utmRows,
			title: Liferay.Language.get('utm-parameters'),
		});
	}

	return tables;
};
