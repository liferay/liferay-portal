import mockDate from 'test/mock-date';
import React from 'react';
import VerticalTimeline from '../VerticalTimeline';
import {cleanup, fireEvent, render, waitFor} from '@testing-library/react';
import {StaticRouter} from 'react-router';

jest.unmock('react-dom');

const ITEMS = [
	{
		header: true,
		title: 'Yesterday'
	},
	{
		attributes: {
			contentLanguageID: undefined,
			header: 'Session Attributes',
			screenHeight: '1229',
			screenWidth: '1541',
			timezoneOffset: '-07:00',
			userAgent:
				'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36'
		},
		browserName: 'Firefox',
		device: 'Unknown',
		endTime: undefined,
		nestedItems: [
			{
				attributes: {
					canonicalUrl: 'http://192.168.86.193:3001/',
					header: 'Event Attributes',
					referrer: '',
					title: 'Liferay Home Page',
					url: 'http://192.168.86.193:3001/'
				},
				subtitle: 'www.liferay.com/testing',
				symbol: 'web-content',
				time: 1518648993917,
				title: 'Visited Liferay: Testing'
			},
			{
				subtitle: 'www.liferay.com/testing 2',
				symbol: 'web-content',
				time: 1518648993917,
				title: 'Visited Liferay: Testing 2'
			}
		],
		subtitle: '3 Document Downloads, 2 Form Submissions, 24 Page Visits',
		symbol: 'web-content',
		time: 1518648993917,
		title: 'Opened Email',
		type: 'Download'
	},
	{
		header: true,
		title: 'Today'
	},
	{
		attributes: {
			header: 'Session Attributes',
			screenHeight: '1229',
			screenWidth: '1541',
			timezoneOffset: '-07:00',
			userAgent:
				'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36'
		},
		browserName: 'Firefox',
		device: 'Mobile',
		endTime: 'Wed Sep 01 20:52:49 GMT 2021',
		nestedItems: [
			{
				attributes: {
					canonicalUrl: 'http://192.168.86.193:3001/',
					header: 'Event Attributes',
					referrer: '',
					title: 'Liferay Home Page',
					url: 'http://192.168.86.193:3001/'
				},
				subtitle: 'www.liferay.com/testing',
				symbol: 'web-content',
				time: 1518648993917,
				title: 'Visited Liferay: Testing'
			},
			{
				subtitle: 'www.liferay.com/testing 2',
				symbol: 'web-content',
				time: 1518648993917,
				title: 'Visited Liferay: Testing 2'
			}
		],
		subtitle: '3 Document Downloads, 2 Form Submissions, 24 Page Visits',
		symbol: 'web-content',
		time: 1518648993917,
		title: 'Opened Email',
		type: 'Download'
	},
	{
		attributes: {
			header: 'Session Attributes',
			screenHeight: '1229',
			screenWidth: '1541',
			timezoneOffset: '-07:00',
			userAgent:
				'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36'
		},
		browserName: 'Firefox',
		device: 'Desktop',
		endTime: 'Wed Sep 01 20:52:49 GMT 2021',
		nestedItems: [
			{
				attributes: {
					canonicalUrl: 'http://192.168.86.193:3001/',
					header: 'Event Attributes',
					referrer: '',
					title: 'Liferay Home Page',
					url: 'http://192.168.86.193:3001/'
				},
				description:
					'Liferay: Digital experience software tailored to your needs',
				subtitle: 'www.liferay.com/testing',
				symbol: 'web-content',
				time: 1518648993917,
				title: 'Visited Liferay: Testing'
			},
			{
				attributes: {
					canonicalUrl: 'http://192.168.86.193:3001/',
					header: 'Event Attributes',
					referrer: '',
					title: 'Liferay Home Page',
					url: 'http://192.168.86.193:3001/'
				},
				subtitle: 'www.liferay.com/testing 2',
				symbol: 'web-content',
				time: 1518648993917,
				title: 'Visited Liferay: Testing 2'
			}
		],
		subtitle: '3 Document Downloads, 2 Form Submissions, 24 Page Visits',
		symbol: 'web-content',
		time: 1518648993917,
		title: 'Opened Email',
		type: 'Download',
		url: 'www.liferay.com'
	}
];

const SESSION_ATTRIBUTES_TITLE = 'Session Attributes';

const DefaultComponent = props => (
	<StaticRouter>
		<VerticalTimeline {...props} />
	</StaticRouter>
);

const createDataSourceItem = ({applicationId, userAgent}) => ({
	applicationId,
	attributes: {
		header: 'Session Attributes'
	},
	browserName: 'Firefox',
	device: 'Desktop',
	nestedItems: [
		{
			subtitle: 'www.liferay.com/testing',
			time: 1518648993917,
			title: 'Visited Liferay: Testing'
		}
	],
	time: 1518648993917,
	title: 'Opened Email',
	userAgent
});

describe('VerticalTimeline', () => {
	afterEach(cleanup);

	beforeAll(mockDate);

	it('should render with a header and initialExpanded', () => {
		const {container} = render(
			<DefaultComponent
				headerLabels={{
					count: 'count',
					label: 'label',
					title: 'title'
				}}
				initialExpanded
				items={ITEMS}
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render on loading state', () => {
		const {container} = render(<DefaultComponent loading />);

		expect(container.querySelector('.loading-root')).toBeInTheDocument();
	});

	it('should expand TimelineItem when clicked', async () => {
		const {container, getAllByText} = render(
			<DefaultComponent
				headerLabels={{
					count: 'count',
					label: 'label',
					title: 'title'
				}}
				initialExpanded
				items={ITEMS}
			/>
		);

		fireEvent.click(
			container.getElementsByClassName(
				'timeline-panel-body-content selectable'
			)[0]
		);

		const sessionAttributes = await waitFor(
			() => getAllByText(/Session Attributes/)[0]
		);

		expect(sessionAttributes).toHaveTextContent(SESSION_ATTRIBUTES_TITLE);
	});

	it('should display the "DXP" label for Liferay DXP data sources', () => {
		['CustomEvent', 'WebContent'].forEach(applicationId => {
			const {getByText, unmount} = render(
				<DefaultComponent
					items={[
						createDataSourceItem({
							applicationId,
							userAgent:
								'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36'
						})
					]}
				/>
			);

			expect(getByText('DXP')).toBeInTheDocument();

			unmount();
		});
	});

	it('should display the application ID label for the HubSpot webhook data source', () => {
		const {getByText, queryByText} = render(
			<DefaultComponent
				items={[
					createDataSourceItem({
						applicationId: 'Hubspot',
						userAgent: 'HubSpot Webhook'
					})
				]}
			/>
		);

		expect(getByText('HUBSPOT')).toBeInTheDocument();
		expect(queryByText('DXP')).not.toBeInTheDocument();
	});

	it('should display the application ID label for the Marketo webhook data source', () => {
		const {getByText, queryByText} = render(
			<DefaultComponent
				items={[
					createDataSourceItem({
						applicationId: 'Marketo',
						userAgent: 'Marketo Webhook'
					})
				]}
			/>
		);

		expect(getByText('MARKETO')).toBeInTheDocument();
		expect(queryByText('DXP')).not.toBeInTheDocument();
	});

	it('hides the data source label when the workspace is not on the LDP plan', () => {
		const {queryByText} = render(
			<DefaultComponent
				items={[
					createDataSourceItem({
						applicationId: 'WebContent',
						userAgent:
							'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36'
					})
				]}
				LDPEnabled={false}
			/>
		);

		expect(queryByText('DXP')).not.toBeInTheDocument();
	});

	it('shows the data source label when the workspace is on the LDP plan', () => {
		const {getByText} = render(
			<DefaultComponent
				items={[
					createDataSourceItem({
						applicationId: 'WebContent',
						userAgent:
							'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36'
					})
				]}
				LDPEnabled
			/>
		);

		expect(getByText('DXP')).toBeInTheDocument();
	});

	it('renders a known individual user header with the user icon', () => {
		const {container, getByText} = render(
			<DefaultComponent
				items={[{title: 'Ada Lovelace', userHeader: true}]}
			/>
		);

		expect(getByText('Ada Lovelace')).toBeInTheDocument();
		expect(
			container.querySelector('.user-header .lexicon-icon-user')
		).toBeInTheDocument();
	});

	it('renders an anonymous user header with the anonymize icon', () => {
		const {container, getByText} = render(
			<DefaultComponent
				items={[
					{isAnonymous: true, title: 'Anonymous', userHeader: true}
				]}
			/>
		);

		expect(getByText('Anonymous')).toBeInTheDocument();
		expect(
			container.querySelector('.user-header .lexicon-icon-anonymize')
		).toBeInTheDocument();
	});

	it('renders the user header name as a link when a url is provided', () => {
		const {getByText} = render(
			<DefaultComponent
				items={[
					{
						title: 'Ada Lovelace',
						userHeader: true,
						userHeaderUrl:
							'/workspace/liferay.com/1/contacts/individuals/known-individuals/abc'
					}
				]}
			/>
		);

		expect(getByText('Ada Lovelace').closest('a')).toHaveAttribute(
			'href',
			'/workspace/liferay.com/1/contacts/individuals/known-individuals/abc'
		);
	});
});
