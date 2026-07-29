import React from 'react';
import VerticalTimeline from '../VerticalTimeline';
import {cleanup, fireEvent, render, screen} from '@testing-library/react';

jest.unmock('react-dom');

const TIME_ZONE_ID = 'UTC';

const renderTimeline = (props) =>
	render(<VerticalTimeline timeZoneId={TIME_ZONE_ID} {...props} />);

describe('VerticalTimeline', () => {
	afterEach(cleanup);

	it('renders on loading state', () => {
		const {container} = renderTimeline({loading: true});

		expect(container.querySelector('.loading-root')).toBeInTheDocument();
	});

	describe('day row', () => {
		it('shows the day title and its event count', () => {
			renderTimeline({
				items: [{header: true, title: 'Yesterday', totalEvents: 3}]
			});

			expect(screen.getByText('Yesterday')).toBeInTheDocument();
			expect(screen.getByText('3')).toBeInTheDocument();
		});
	});

	describe('individual row', () => {
		const INDIVIDUAL_ITEM = {
			individual: true,
			individualId: 'ind-1',
			individualName: 'Ada Lovelace',
			individualUrl: '/workspace/liferay.com/1/contacts/individuals/known-individuals/ind-1',
			isAnonymous: false
		};

		it('renders the individual\'s name as a link when a url is provided', () => {
			renderTimeline({items: [INDIVIDUAL_ITEM]});

			expect(screen.getByText('Ada Lovelace').closest('a')).toHaveAttribute(
				'href',
				INDIVIDUAL_ITEM.individualUrl
			);
		});

		it('renders the individual\'s name as plain text when there is no url', () => {
			renderTimeline({
				items: [{...INDIVIDUAL_ITEM, individualUrl: undefined}]
			});

			expect(screen.getByText('Ada Lovelace').closest('a')).toBeNull();
		});

		it('shows the raw id on its own line for an anonymous individual', () => {
			renderTimeline({
				items: [
					{
						...INDIVIDUAL_ITEM,
						individualId: 'e484348e-anon',
						individualName: 'Anonymous User',
						individualUrl: undefined,
						isAnonymous: true
					}
				]
			});

			expect(screen.getByText('Anonymous User')).toBeInTheDocument();
			expect(screen.getByText('e484348e-anon')).toBeInTheDocument();
		});

		it('is not expandable', () => {
			const {container} = renderTimeline({items: [INDIVIDUAL_ITEM]});

			expect(
				container.querySelector('.individual-row .row-main')
			).not.toBeInTheDocument();
			expect(
				container.querySelector('.individual-row .angle-icon')
			).not.toBeInTheDocument();
		});
	});

	describe('session row', () => {
		const SESSION_ITEM = {
			applicationId: 'Page',
			attributes: {
				header: 'Session Attributes',
				userAgent: 'Mozilla/5.0'
			},
			browserName: 'Firefox',
			device: 'Desktop',
			endTime: '2026-07-16T11:00:00.000Z',
			nestedItems: [],
			session: true,
			time: '2026-07-16T10:00:00.000Z',
			totalEvents: 2,
			userAgent: 'Mozilla/5.0'
		};

		it('shows the session time range', () => {
			renderTimeline({items: [SESSION_ITEM]});

			expect(
				screen.getByText('Session: 10:00 am - 11:00 am')
			).toBeInTheDocument();
		});

		it('shows "in progress" when the session has no end time', () => {
			renderTimeline({items: [{...SESSION_ITEM, endTime: undefined}]});

			expect(
				screen.getByText('Session: 10:00 am - in progress')
			).toBeInTheDocument();
		});

		it('shows "no timestamps" for a webhook session', () => {
			renderTimeline({items: [{...SESSION_ITEM, noTimestamps: true}]});

			expect(
				screen.getByText('Session: 10:00 am - no timestamps')
			).toBeInTheDocument();
		});

		it('reveals its own raw attributes when expanded', () => {
			const {container} = renderTimeline({items: [SESSION_ITEM]});

			expect(
				container.querySelector('.attributes-payload')
			).not.toBeInTheDocument();

			fireEvent.click(container.querySelector('.session-row .row-main'));

			expect(
				container.querySelector('.attributes-payload')
			).toHaveTextContent('Session Attributes');
		});

		it('always shows its pages, without needing to expand', () => {
			renderTimeline({
				items: [
					{
						...SESSION_ITEM,
						nestedItems: [
							{
								descriptionUrl: undefined,
								nestedItems: [],
								pageGroup: true,
								subtitle: 'https://liferay.com/home',
								time: '2026-07-16T10:05:00.000Z',
								title: 'Home',
								totalEvents: 1
							}
						]
					}
				]
			});

			expect(screen.getByText('Home')).toBeInTheDocument();
		});

		it('should display the "DXP" label for a Liferay DXP data source', () => {
			renderTimeline({
				items: [{...SESSION_ITEM, applicationId: 'WebContent'}]
			});

			expect(screen.getByText('DXP')).toBeInTheDocument();
		});

		it('should display the application id label for a webhook data source', () => {
			renderTimeline({
				items: [
					{
						...SESSION_ITEM,
						applicationId: 'HubSpot',
						userAgent: 'HubSpot Webhook'
					}
				]
			});

			expect(screen.getByText('HUBSPOT')).toBeInTheDocument();
			expect(screen.queryByText('DXP')).not.toBeInTheDocument();
		});

		it('hides the data source label when the workspace is not on the LDP plan', () => {
			renderTimeline({
				items: [{...SESSION_ITEM, applicationId: 'WebContent'}],
				LDPEnabled: false
			});

			expect(screen.queryByText('DXP')).not.toBeInTheDocument();
		});
	});

	describe('page group row', () => {
		const PAGE_ITEM = {
			descriptionUrl: '/workspace/liferay.com/1/sites/touchpoints',
			nestedItems: [],
			pageGroup: true,
			subtitle: 'https://liferay.com/home',
			time: '2026-07-16T10:00:00.000Z',
			title: 'Home',
			totalEvents: 2
		};

		it('renders the page title as a link to its dashboard when a descriptionUrl is provided', () => {
			renderTimeline({items: [PAGE_ITEM]});

			expect(screen.getByText('Home').closest('a')).toHaveAttribute(
				'href',
				PAGE_ITEM.descriptionUrl
			);
		});

		it('renders the page title as plain text when there is no descriptionUrl', () => {
			renderTimeline({items: [{...PAGE_ITEM, descriptionUrl: undefined}]});

			expect(screen.getByText('Home').closest('a')).toBeNull();
		});

		it('renders the page url as an external link', () => {
			renderTimeline({items: [PAGE_ITEM]});

			expect(screen.getByText(PAGE_ITEM.subtitle).closest('a')).toHaveAttribute(
				'href',
				PAGE_ITEM.subtitle
			);
		});

		it('shows the event count for the page', () => {
			renderTimeline({items: [PAGE_ITEM]});

			expect(screen.getByText('2')).toBeInTheDocument();
		});

		it('reveals its own events when expanded', () => {
			const {container} = renderTimeline({
				items: [
					{
						...PAGE_ITEM,
						nestedItems: [
							{
								attributes: {},
								description: undefined,
								descriptionUrl: undefined,
								subtitle: undefined,
								time: '2026-07-16T10:01:00.000Z',
								title: 'pageViewed'
							}
						]
					}
				]
			});

			expect(screen.queryByText('pageViewed')).not.toBeInTheDocument();

			fireEvent.click(container.querySelector('.page-row .row-main'));

			expect(screen.getByText('pageViewed')).toBeInTheDocument();
		});
	});

	describe('event row', () => {
		const EVENT_ITEM = {
			attributes: {applicationId: 'HubSpot', eventId: 'emailViewed'},
			description: 'Liferay: Digital experience software',
			descriptionUrl: '/workspace/liferay.com/1/assets/blogs/1',
			subtitle: 'https://hubspot.com',
			time: '2026-07-16T10:00:00.000Z',
			title: 'emailViewed'
		};

		it('shows the event name and time', () => {
			renderTimeline({items: [EVENT_ITEM]});

			expect(screen.getByText('emailViewed')).toBeInTheDocument();
			expect(screen.getByText('10:00 am')).toBeInTheDocument();
		});

		it('renders the description as a link when a descriptionUrl is provided', () => {
			renderTimeline({items: [EVENT_ITEM]});

			expect(
				screen.getByText(EVENT_ITEM.description).closest('a')
			).toHaveAttribute('href', EVENT_ITEM.descriptionUrl);
		});

		it('renders the event\'s own subtitle as an external link', () => {
			renderTimeline({items: [EVENT_ITEM]});

			expect(screen.getByText(EVENT_ITEM.subtitle).closest('a')).toHaveAttribute(
				'href',
				EVENT_ITEM.subtitle
			);
		});

		it('reveals its own raw attributes when expanded', () => {
			const {container} = renderTimeline({items: [EVENT_ITEM]});

			expect(
				container.querySelector('.attributes-payload')
			).not.toBeInTheDocument();

			fireEvent.click(container.querySelector('.event-row .row-main'));

			expect(
				container.querySelector('.attributes-payload')
			).toHaveTextContent('HubSpot');
		});
	});
});
