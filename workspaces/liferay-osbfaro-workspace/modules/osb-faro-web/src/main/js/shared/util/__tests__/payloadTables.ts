import {formatPayloadTables, PayloadLayout} from '../payloadTables';

const LAYOUT: PayloadLayout = {
	groups: [
		{key: 'extra'},
		{key: 'tags', title: 'Tags'},
		{
			key: 'owner',
			links: {ownerName: 'ownerUrl'},
			title: 'Owner',
		},
	],
	title: 'Details',
};

const OWNER_URL = '/people/ada';

describe('formatPayloadTables', () => {
	it('turns a flat payload into a single table titled by the layout', () => {
		expect(formatPayloadTables({id: '42', name: 'Report'}, LAYOUT)).toEqual(
			[
				{
					rows: [
						{property: 'id', value: '42'},
						{property: 'name', value: 'Report'},
					],
					title: 'Details',
				},
			]
		);
	});

	it('titles the main table with the header the payload carries', () => {
		const [{title}] = formatPayloadTables(
			{header: 'Session Attributes', languageID: 'en_US'},
			LAYOUT
		);

		expect(title).toBe('Session Attributes');
	});

	it('leaves the header out of the rows it titles', () => {
		const [{rows}] = formatPayloadTables(
			{header: 'Session Attributes', languageID: 'en_US'},
			LAYOUT
		);

		expect(rows).toEqual([{property: 'languageID', value: 'en_US'}]);
	});

	it('flattens a group without a title into the main table', () => {
		const [{rows}] = formatPayloadTables(
			{extra: {color: 'blue'}, id: '42'},
			LAYOUT
		);

		expect(rows).toEqual([
			{property: 'color', value: 'blue'},
			{property: 'id', value: '42'},
		]);
	});

	it('lays a titled group out in a table of its own', () => {
		expect(
			formatPayloadTables({id: '42', tags: {topic: 'sales'}}, LAYOUT)
		).toEqual([
			{rows: [{property: 'id', value: '42'}], title: 'Details'},
			{rows: [{property: 'topic', value: 'sales'}], title: 'Tags'},
		]);
	});

	it('orders the group tables by the layout rather than by the payload', () => {
		expect(
			formatPayloadTables(
				{id: '42', owner: {ownerName: 'Ada'}, tags: {topic: 'sales'}},
				LAYOUT
			).map(({title}) => title)
		).toEqual(['Details', 'Tags', 'Owner']);
	});

	it('omits the table of a group the payload does not carry or carries empty', () => {
		expect(
			formatPayloadTables({id: '42', tags: {}}, LAYOUT).map(
				({title}) => title
			)
		).toEqual(['Details']);
	});

	it('ignores a group key that does not hold a group', () => {
		expect(formatPayloadTables({id: '42', tags: 'sales'}, LAYOUT)).toEqual([
			{rows: [{property: 'id', value: '42'}], title: 'Details'},
		]);
	});

	it('links a property to its link target instead of listing the target', () => {
		const [, {rows}] = formatPayloadTables(
			{id: '42', owner: {ownerName: 'Ada', ownerUrl: OWNER_URL}},
			LAYOUT
		);

		expect(rows).toEqual([
			{href: OWNER_URL, property: 'ownerName', value: 'Ada'},
		]);
	});

	it('leaves a property unlinked when the group carries no link target', () => {
		const [, {rows}] = formatPayloadTables(
			{id: '42', owner: {ownerName: 'Ada'}},
			LAYOUT
		);

		expect(rows).toEqual([{property: 'ownerName', value: 'Ada'}]);
	});

	it('does not link a property the payload sent empty', () => {
		const [, {rows}] = formatPayloadTables(
			{id: '42', owner: {ownerName: null, ownerUrl: OWNER_URL}},
			LAYOUT
		);

		expect(rows).toEqual([{property: 'ownerName', value: '–'}]);
	});

	it('lists a link target outside its group as an ordinary row', () => {
		const [{rows}] = formatPayloadTables(
			{extra: {ownerName: 'Ada', ownerUrl: OWNER_URL}},
			LAYOUT
		);

		expect(rows).toEqual([
			{property: 'ownerName', value: 'Ada'},
			{property: 'ownerUrl', value: OWNER_URL},
		]);
	});

	it('renders an empty value as a dash', () => {
		const [{rows}] = formatPayloadTables(
			{eventDate: null, eventId: '', pageTitle: undefined},
			LAYOUT
		);

		expect(rows).toEqual([
			{property: 'eventDate', value: '–'},
			{property: 'eventId', value: '–'},
			{property: 'pageTitle', value: '–'},
		]);
	});

	it('stringifies a value that is neither a string nor empty', () => {
		const [{rows}] = formatPayloadTables({screenWidth: 1440}, LAYOUT);

		expect(rows).toEqual([{property: 'screenWidth', value: '1440'}]);
	});

	it('returns no table at all for an empty payload', () => {
		expect(formatPayloadTables({}, LAYOUT)).toEqual([]);
	});
});
