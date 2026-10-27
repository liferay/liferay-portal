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
 * The payload keys holding groups of attributes, which arrive nested one level
 * down. Each is flattened into the table named beside it, so which table a
 * parameter lands in is the API's classification rather than a guess made from
 * the parameter's name.
 */
const NESTED_KEYS = {
	acquisitionProperties: 'utm',
	campaign: 'campaign',
	properties: 'attributes',
} as const;

/**
 * The campaign group's property that names the campaign, which the table links
 * to the campaign's own page.
 */
const CAMPAIGN_NAME_KEY = 'campaignName';

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
 * Splits an event or session payload into the tables the activity timeline
 * renders when a row is expanded: the payload's own attributes, the campaign
 * the touch carried, and the acquisition parameters that came with it. A table
 * with no rows is left out entirely, so a payload carrying no UTM parameter
 * renders no UTM table and an empty payload renders none. A campaign that did
 * not resolve to a name has nothing to link, so only a named one is a link.
 */
export const formatPayloadTables = (
	payload: Record<string, unknown>,
	{campaignUrl}: {campaignUrl?: string} = {}
): PayloadTable[] => {
	const rowsByGroup: Record<string, PayloadTableRow[]> = {
		attributes: [],
		campaign: [],
		utm: [],
	};

	let title = Liferay.Language.get('event-attributes');

	const addRow = (group: string, property: string, value: unknown) => {
		const formattedValue = formatValue(value);

		rowsByGroup[group].push({
			...(campaignUrl &&
				group === 'campaign' &&
				property === CAMPAIGN_NAME_KEY &&
				formattedValue !== EMPTY_VALUE && {href: campaignUrl}),
			property,
			value: formattedValue,
		});
	};

	Object.entries(payload).forEach(([key, value]) => {
		if (key === HEADER_KEY) {
			title = String(value);
		}
		else if (key in NESTED_KEYS) {
			const group = NESTED_KEYS[key as keyof typeof NESTED_KEYS];

			if (isNestedGroup(value)) {
				Object.entries(value).forEach(([nestedKey, nestedValue]) =>
					addRow(group, nestedKey, nestedValue)
				);
			}
		}
		else {
			addRow('attributes', key, value);
		}
	});

	const tables: PayloadTable[] = [];

	if (rowsByGroup.attributes.length) {
		tables.push({rows: rowsByGroup.attributes, title});
	}

	if (rowsByGroup.campaign.length) {
		tables.push({
			rows: rowsByGroup.campaign,
			title: Liferay.Language.get('campaign'),
		});
	}

	if (rowsByGroup.utm.length) {
		tables.push({
			rows: rowsByGroup.utm,
			title: Liferay.Language.get('utm-parameters'),
		});
	}

	return tables;
};
