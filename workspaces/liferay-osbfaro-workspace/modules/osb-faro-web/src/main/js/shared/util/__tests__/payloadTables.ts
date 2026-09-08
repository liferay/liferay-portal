import {formatPayloadTables} from '../payloadTables';

describe('formatPayloadTables', () => {
	it('turns a flat payload into a single attributes table', () => {
		expect(
			formatPayloadTables({
				applicationId: 'webContent',
				eventId: 'webContentViewed',
			})
		).toEqual([
			{
				rows: [
					{property: 'applicationId', value: 'webContent'},
					{property: 'eventId', value: 'webContentViewed'},
				],
				title: 'Event Attributes',
			},
		]);
	});

	it('titles the table with the header the payload carries', () => {
		const [{title}] = formatPayloadTables({
			header: 'Session Attributes',
			languageID: 'en_US',
		});

		expect(title).toBe('Session Attributes');
	});

	it('leaves the header out of the rows it titles', () => {
		const [{rows}] = formatPayloadTables({
			header: 'Session Attributes',
			languageID: 'en_US',
		});

		expect(rows).toEqual([{property: 'languageID', value: 'en_US'}]);
	});

	it('flattens nested properties into rows of the attributes table', () => {
		const [{rows}] = formatPayloadTables({
			eventId: 'elementClicked',
			properties: {elementId: 'compare-models'},
		});

		expect(rows).toEqual([
			{property: 'eventId', value: 'elementClicked'},
			{property: 'elementId', value: 'compare-models'},
		]);
	});

	it('splits keys prefixed with utm into their own table', () => {
		expect(
			formatPayloadTables({
				eventId: 'pageViewed',
				properties: {utm_medium: 'email'},
			})
		).toEqual([
			{
				rows: [{property: 'eventId', value: 'pageViewed'}],
				title: 'Event Attributes',
			},
			{
				rows: [{property: 'utm_medium', value: 'email'}],
				title: 'UTM Parameters',
			},
		]);
	});

	it('splits camel cased utm keys the same way as snake cased ones', () => {
		const [, utmTable] = formatPayloadTables({
			eventId: 'pageViewed',
			utmMedium: 'email',
		});

		expect(utmTable.rows).toEqual([
			{property: 'utmMedium', value: 'email'},
		]);
	});

	it('does not mistake a key that merely contains utm for a utm key', () => {
		const [{rows}, utmTable] = formatPayloadTables({
			customUtmFlag: 'true',
		});

		expect(rows).toEqual([{property: 'customUtmFlag', value: 'true'}]);
		expect(utmTable).toBeUndefined();
	});

	it('omits the utm table when the payload carries no utm key', () => {
		expect(formatPayloadTables({eventId: 'pageViewed'})).toHaveLength(1);
	});

	it('renders an empty value as a dash', () => {
		const [{rows}] = formatPayloadTables({
			eventDate: null,
			eventId: '',
			pageTitle: undefined,
		});

		expect(rows).toEqual([
			{property: 'eventDate', value: '-'},
			{property: 'eventId', value: '-'},
			{property: 'pageTitle', value: '-'},
		]);
	});

	it('stringifies a value that is neither a string nor empty', () => {
		const [{rows}] = formatPayloadTables({screenWidth: 1440});

		expect(rows).toEqual([{property: 'screenWidth', value: '1440'}]);
	});

	it('returns no table at all for an empty payload', () => {
		expect(formatPayloadTables({})).toEqual([]);
	});
});
