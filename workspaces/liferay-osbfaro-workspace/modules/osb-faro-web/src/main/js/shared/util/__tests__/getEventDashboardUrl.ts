import getEventDashboardUrl, {
	EventDashboardContext,
} from '../getEventDashboardUrl';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {UserSessionEvent} from 'shared/queries/UserSessionQuery';

const CONTEXT: EventDashboardContext = {
	channelId: '420253908131944590',
	groupId: 'liferay.com',
	rangeSelectors: {
		rangeEnd: null,
		rangeKey: RangeKeyTimeRanges.Last30Days,
		rangeStart: null,
	},
};

const buildEvent = (
	overrides: Partial<UserSessionEvent> = {}
): UserSessionEvent =>
	({
		applicationId: 'WebContent',
		assetTitle: 'Company - Nav Item 1',
		canonicalUrl: 'https://www.liferay.com/careers',
		name: 'webContentViewed',
		pageTitle: 'Careers',
		properties: [
			{name: 'articleId', value: '521827670'},
			{name: 'type', value: 'web-content'},
		],
		...overrides,
	}) as unknown as UserSessionEvent;

describe('getEventDashboardUrl', () => {
	it('builds an asset dashboard link for an asset event', () => {
		expect(getEventDashboardUrl(buildEvent(), CONTEXT)).toBe(
			'/workspace/liferay.com/420253908131944590/assets/web-content/521827670/page/Any/Company%20-%20Nav%20Item%201/webContent?rangeKey=30'
		);
	});

	it('builds a page dashboard link for a pageViewed event', () => {
		const event = buildEvent({
			applicationId: 'Page',
			assetTitle: null as unknown as string,
			canonicalUrl: 'https://www.liferay.com/legal',
			name: 'pageViewed',
			pageTitle: 'Legal 2026 - Liferay DXP',
			properties: [{name: 'externalReferenceCode', value: 'abc'}],
		});

		expect(getEventDashboardUrl(event, CONTEXT)).toBe(
			'/workspace/liferay.com/420253908131944590/sites/pages/overview/https%3A%2F%2Fwww.liferay.com%2Flegal/Legal%202026%20-%20Liferay%20DXP?rangeKey=30'
		);
	});

	it('builds an asset dashboard link for a form event using formId', () => {
		const event = buildEvent({
			applicationId: 'Form',
			assetTitle: 'Contact Form',
			name: 'formSubmitted',
			properties: [{name: 'formId', value: '54321'}],
		});

		expect(getEventDashboardUrl(event, CONTEXT)).toBe(
			'/workspace/liferay.com/420253908131944590/assets/forms/54321/page/Any/Contact%20Form/form?rangeKey=30'
		);
	});

	it('returns undefined for a webhook event', () => {
		expect(
			getEventDashboardUrl(buildEvent(), {...CONTEXT, isWebhook: true})
		).toBeUndefined();
	});

	it('returns undefined for an unmapped application id', () => {
		const event = buildEvent({
			applicationId: 'Blog',
			properties: [{name: 'entryId', value: '1'}],
		});

		expect(getEventDashboardUrl(event, CONTEXT)).toBeUndefined();
	});

	it('returns undefined when the group or channel is missing', () => {
		expect(
			getEventDashboardUrl(buildEvent(), {
				rangeSelectors: CONTEXT.rangeSelectors,
			})
		).toBeUndefined();
	});
});
