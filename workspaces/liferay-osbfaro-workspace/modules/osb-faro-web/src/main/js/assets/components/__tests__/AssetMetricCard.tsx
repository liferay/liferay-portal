import AssetMetricCard from '../AssetMetricCard';
import client from 'shared/apollo/client';
import React from 'react';
import URLConstants from 'shared/util/url-constants';
import {ApolloProvider} from '@apollo/client';
import {cleanup, render} from '@testing-library/react';
import {
	AbandonmentsMetric,
	CommentsMetric,
	CompletionTimeMetric,
	DownloadsMetric,
	ImpressionMadeMetric,
	Metric,
	RatingsMetric,
	ReadingTimeMetric,
	SubmissionsMetric,
	ViewsMetric,
} from 'shared/components/metric-card/metrics';
import {
	mockAssetMetricReq,
	mockAssetTabsReq,
	mockPreferenceReq,
	mockTimeRangeReq,
} from 'test/graphql-data';
import {MockedProvider} from '@apollo/client/testing';
import {RangeKeyTimeRanges, THIRTEEN_MONTHS} from 'shared/util/constants';
import {MemoryRouter} from 'react-router-dom';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		assetId: '123',
		channelId: '456',
		groupId: '2000',
		query: {
			rangeKey: RangeKeyTimeRanges.Last30Days,
		},
		title: 'My awesome asset',
		touchpoint: 'https://liferay.com',
	}),
}));

/**
 * Override Recharts Responsive Container
 * width dimensions fixed to be able to render charts
 */

jest.mock('recharts', () => {
	const OriginalModule = jest.requireActual('recharts');

	return {
		...OriginalModule,
		ResponsiveContainer: ({children}: {children: React.ReactNode}) => (
			<OriginalModule.ResponsiveContainer height={350} width={800}>
				{children}
			</OriginalModule.ResponsiveContainer>
		),
		Tooltip: ({children, ...props}: {children: React.ReactNode}) => (
			<OriginalModule.Tooltip {...props} active>
				{children}
			</OriginalModule.Tooltip>
		),
	};
});

interface IAssetCase {
	documentationURL: URLConstants;
	metrics: Metric[];
	name: string;
	slug: string;
	variableType?: string;
}

const CASES: IAssetCase[] = [
	{
		documentationURL: URLConstants.VisitorBehaviorBlogsLink,
		metrics: [
			ViewsMetric,
			ReadingTimeMetric,
			CommentsMetric,
			RatingsMetric,
		],
		name: 'blog',
		slug: 'blogs',
	},
	{
		documentationURL: URLConstants.VisitorBehaviorDocumentsAndMediaLink,
		metrics: [
			DownloadsMetric,
			ImpressionMadeMetric,
			CommentsMetric,
			RatingsMetric,
		],
		name: 'document',
		slug: 'documents-and-media',
	},
	{
		documentationURL: URLConstants.VisitorBehaviorFormsLink,
		metrics: [
			SubmissionsMetric,
			ViewsMetric,
			AbandonmentsMetric,
			CompletionTimeMetric,
		],
		name: 'form',
		slug: 'forms',
	},
	{
		documentationURL: URLConstants.VisitorBehaviorWebContentLink,
		metrics: [ViewsMetric],
		name: 'journal',
		slug: 'web-content',
	},
	{
		documentationURL: URLConstants.VisitorBehaviorWebContentLink,
		metrics: [ImpressionMadeMetric, ViewsMetric, DownloadsMetric],
		name: 'objectEntry',
		slug: 'object-entry',
		variableType: 'objectEntry',
	},
];

const WrappedComponent = ({
	assetCase,
	empty = false,
}: {
	assetCase: IAssetCase;
	empty?: boolean;
}) => (
	<ApolloProvider client={client}>
		<MemoryRouter>
			<MockedProvider
				mocks={[
					mockTimeRangeReq(),
					mockPreferenceReq(THIRTEEN_MONTHS),
					mockAssetTabsReq({
						metrics: assetCase.metrics,
						name: assetCase.name,
						rangeKey: Number(RangeKeyTimeRanges.Last30Days),
					}),
					mockAssetMetricReq({
						empty,
						metricName: assetCase.metrics[0].name,
						queryName: assetCase.name,
						rangeKey: Number(RangeKeyTimeRanges.Last30Days),
						type: assetCase.variableType,
					}),
				]}
			>
				<AssetMetricCard
					documentationURL={assetCase.documentationURL}
					label={Liferay.Language.get('visitors-behavior')}
					metrics={assetCase.metrics}
					name={assetCase.name}
					variableType={assetCase.variableType}
				/>
			</MockedProvider>
		</MemoryRouter>
	</ApolloProvider>
);

describe('AssetMetricCard', () => {
	afterEach(cleanup);

	describe.each(CASES)('$slug', (assetCase) => {
		it('should render the card for the asset type', async () => {
			const {getByText} = render(
				<WrappedComponent assetCase={assetCase} />
			);

			await waitForLoadingToBeRemoved(document.body);

			expect(getByText('Visitors Behavior')).toBeInTheDocument();
		});

		it('should render the empty state linking to the right documentation', async () => {
			const {container, getByText} = render(
				<WrappedComponent assetCase={assetCase} empty />
			);

			await waitForLoadingToBeRemoved(container);

			expect(
				getByText('No visitors data was found.')
			).toBeInTheDocument();

			expect(
				getByText('Learn more about visitor behavior.')
			).toHaveAttribute('href', assetCase.documentationURL);
		});
	});
});
