import {AssetTypes} from 'shared/util/constants';
import {CSVType} from 'shared/components/download-report/utils';
import {isEmpty} from 'lodash';
import {Name} from 'shared/components/audience-report/types';
import {Routes, setUriQueryValues, toRoute} from 'shared/util/router';

/**
 * Describes one asset type the dashboards cover.
 *
 * Two upstream APIs spell these types differently, so a descriptor carries both
 * spellings rather than forcing one of them on the other. `graphQLType` is what
 * the `assets` GraphQL query returns, and doubles as the root field each
 * dashboard query reads. `restType` is what the `asset-summary` REST endpoint
 * returns. Web content is where the two disagree: `journal` over GraphQL,
 * `webContent` over REST.
 */
export interface AssetDescriptor {
	assetType: AssetTypes;

	/**
	 * Object entries are the one asset type with no known individuals export.
	 */
	csvType?: CSVType;

	graphQLType: Name;
	restType?: string;
	routes: {
		accounts: string;
		knownIndividuals: string;
		overview: string;
	};
	slug: string;
}

const OBJECT_ENTRY_DESCRIPTOR: AssetDescriptor = {
	assetType: AssetTypes.ObjectEntry,
	graphQLType: Name.ObjectEntry,
	routes: {
		accounts: Routes.ASSETS_OBJECT_ENTRY_ACCOUNTS,
		knownIndividuals: Routes.ASSETS_OBJECT_ENTRY_KNOWN_INDIVIDUALS,
		overview: Routes.ASSETS_OBJECT_ENTRY_OVERVIEW,
	},
	slug: 'object-entry',
};

export const ASSET_DESCRIPTORS: AssetDescriptor[] = [
	{
		assetType: AssetTypes.Blog,
		csvType: CSVType.Individual,
		graphQLType: Name.Blog,
		restType: 'blog',
		routes: {
			accounts: Routes.ASSETS_BLOGS_ACCOUNTS,
			knownIndividuals: Routes.ASSETS_BLOGS_KNOWN_INDIVIDUALS,
			overview: Routes.ASSETS_BLOGS_OVERVIEW,
		},
		slug: 'blogs',
	},
	{
		assetType: AssetTypes.Document,
		csvType: CSVType.Individual,
		graphQLType: Name.Document,
		restType: 'document',
		routes: {
			accounts: Routes.ASSETS_DOCUMENTS_AND_MEDIA_ACCOUNTS,
			knownIndividuals:
				Routes.ASSETS_DOCUMENTS_AND_MEDIA_KNOWN_INDIVIDUALS,
			overview: Routes.ASSETS_DOCUMENTS_AND_MEDIA_OVERVIEW,
		},
		slug: 'documents-and-media',
	},
	{
		assetType: AssetTypes.Form,
		csvType: CSVType.Individual,
		graphQLType: Name.Form,
		restType: 'form',
		routes: {
			accounts: Routes.ASSETS_FORMS_ACCOUNTS,
			knownIndividuals: Routes.ASSETS_FORMS_KNOWN_INDIVIDUALS,
			overview: Routes.ASSETS_FORMS_OVERVIEW,
		},
		slug: 'forms',
	},
	{
		assetType: AssetTypes.Journal,
		csvType: CSVType.Individual,
		graphQLType: Name.Journal,
		restType: 'webContent',
		routes: {
			accounts: Routes.ASSETS_WEB_CONTENT_ACCOUNTS,
			knownIndividuals: Routes.ASSETS_WEB_CONTENT_KNOWN_INDIVIDUALS,
			overview: Routes.ASSETS_WEB_CONTENT_OVERVIEW,
		},
		slug: 'web-content',
	},
	OBJECT_ENTRY_DESCRIPTOR,
];

/**
 * Object entries are the catch all: the asset summary carries types that have
 * no dashboard of their own (`folder`, for one), and they render there rather
 * than dead ending on an error page.
 */
export const getAssetDescriptorByGraphQLType = (
	graphQLType?: string
): AssetDescriptor =>
	ASSET_DESCRIPTORS.find(
		(descriptor) => descriptor.graphQLType === graphQLType
	) ?? OBJECT_ENTRY_DESCRIPTOR;

export const getAssetDescriptorByRESTType = (
	restType?: string
): AssetDescriptor =>
	ASSET_DESCRIPTORS.find(
		(descriptor) => !!restType && descriptor.restType === restType
	) ?? OBJECT_ENTRY_DESCRIPTOR;

export const getAssetDescriptorBySlug = (
	slug?: string
): AssetDescriptor | undefined =>
	ASSET_DESCRIPTORS.find((descriptor) => descriptor.slug === slug);

/**
 * Builds the overview URL for an asset named the way the GraphQL API names it.
 */
export const toAssetOverviewRoute = (
	graphQLType: string,
	routeParams: {[key: string]: any},
	query: {[key: string]: any}
) => {
	const {routes, slug} = getAssetDescriptorByGraphQLType(graphQLType);

	const route = toRoute(routes.overview, {...routeParams, assetType: slug});

	return !isEmpty(query) ? setUriQueryValues(query, route) : route;
};
