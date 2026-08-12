import AssetAppearsOnResolver from './AssetAppearsOnResolver';
import CustomAssetsListResolver from './CustomAssetsListResolver';
import DocumentsAndMediaListResolver from './DocumentsAndMediaListResolver';
import DocumentsAndMediaMetricsResolver from './DocumentsAndMediaMetricsResolver';
import EventAnalysisListResolver from './EventAnalysisListResolver';
import EventsByUserSessionsResolver from './EventsByUserSessionsResolver';
import ExperimentResolver from './ExperimentResolver';
import IndividualSiteMetricsResolver from './individualSiteMetricsResolver';
import InterestsResolver from './InterestsResolver';
import PagePathResolver from './PagePathResolver';

/**
 * How it works?
 *
 * Add a @client value on the query to mock data
 * on frontend side, example:
 *
 * query Foo ($foo: String!) {
 *     queryName (title: $title) @client {
 *         data
 *     }
 * }
 */

export const resolvers = {
	assetPages: AssetAppearsOnResolver,
	dashboards: CustomAssetsListResolver,
	document: DocumentsAndMediaMetricsResolver,
	documents: DocumentsAndMediaListResolver,
	eventAnalysisList: EventAnalysisListResolver,
	eventsByUserSessions: EventsByUserSessionsResolver,
	experiment: ExperimentResolver,
	individualInterests: InterestsResolver,
	pagePath: PagePathResolver,
	site: IndividualSiteMetricsResolver,
	siteInterests: InterestsResolver,
};
