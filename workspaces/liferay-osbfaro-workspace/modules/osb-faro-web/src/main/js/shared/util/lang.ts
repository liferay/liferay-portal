import {
	AcquisitionTypes,
	AssetObjectTypes,
	AssetTypes,
	CompositionTypes,
	DataSourceTypes,
	EntityTypes,
	UserRoleNames,
} from 'shared/util/constants';

const SPLIT_REGEX = /({\d+})/g;

/**
 * Utility function for substituting variables into language keys.
 *
 * @example
 * sub(Liferay.Language.get('search-x'), ['all'])
 * => 'search all'
 * sub(Liferay.Language.get('search-x'), [<b>all<b>], false)
 * => 'search <b>all</b>'
 *
 * @param {string} langKey This is the language key used from our properties file
 * @param {array} args Arguments to pass into language key
 * @param {boolean} join Boolean used to indicate whether to call `.join()` on
 * the array before it is returned. Use `false` if subbing in JSX.
 * @return {(string|Array)}
 */
export const sub = (
	langKey: string,
	args: any[],
	join: boolean = true
): string | any[] => {
	const keyArray = langKey
		.split(SPLIT_REGEX)
		.filter((val) => val.length !== 0);

	for (let i = 0; i < args.length; i++) {
		const arg = args[i];

		const indexKey = `{${i}}`;

		let argIndex = keyArray.indexOf(indexKey);

		while (argIndex >= 0) {
			keyArray.splice(argIndex, 1, arg);

			argIndex = keyArray.indexOf(indexKey);
		}
	}

	return join ? (keyArray.join('') as string) : (keyArray as any[]);
};

export const getPluralMessage = (
	singular: string,
	plural: string,
	count: number = 0,
	toString?: boolean,
	subArray?: any[]
) => {
	const message = count === 1 ? singular : plural;

	return sub(message, subArray || [count.toLocaleString()], toString);
};

export const ACQUISITION_LABEL_MAP = {
	[AcquisitionTypes.Channel]: Liferay.Language.get('channel'),
	[AcquisitionTypes.Referrer]: Liferay.Language.get('referrer'),
	[AcquisitionTypes.SourceMedium]: Liferay.Language.get('source-medium'),
};

export const ASSET_OBJECT_TYPE_LANG_MAP = {
	[AssetObjectTypes.Content]: Liferay.Language.get('content'),
	[AssetObjectTypes.File]: Liferay.Language.get('file'),
};

export const ASSET_TYPE_LANG_MAP = {
	[AssetTypes.Blog]: Liferay.Language.get('blog'),
	[AssetTypes.Document]: Liferay.Language.get('document'),
	[AssetTypes.Form]: Liferay.Language.get('form'),
	[AssetTypes.WebContent]: Liferay.Language.get('web-content'),
	[AssetTypes.WebPage]: Liferay.Language.get('page'),
};

export const COMPOSITION_LABEL_MAP = {
	[CompositionTypes.AccountInterests]: Liferay.Language.get('interests'),
	[CompositionTypes.Acquisitions]: Liferay.Language.get('acquisitions'),
	[CompositionTypes.SegmentInterests]: Liferay.Language.get('interests'),
	[CompositionTypes.SiteInterests]: Liferay.Language.get('interests'),
	[CompositionTypes.SearchTerms]: Liferay.Language.get('search-terms'),
};

export const DETAILS_LABEL_MAP = {
	accountDate: Liferay.Language.get('account-date'),
	accountNumber: Liferay.Language.get('account-number'),
	accountType: Liferay.Language.get('account-type'),
	annualRevenue: Liferay.Language.get('annual-revenue'),
	billingAddress: Liferay.Language.get('billing-address'),
	description: Liferay.Language.get('description'),
	fax: Liferay.Language.get('fax-number'),
	industry: Liferay.Language.get('industry'),
	name: Liferay.Language.get('name'),
	numberOfEmployees: Liferay.Language.get('employees'),
	ownership: Liferay.Language.get('ownership'),
	phone: Liferay.Language.get('phone-number'),
	shippingAddress: Liferay.Language.get('shipping-address'),
	website: Liferay.Language.get('website'),
	yearStarted: Liferay.Language.get('year-started'),
};

const ENTITY_LANG_MAP = {
	[EntityTypes.Account]: Liferay.Language.get('accounts'),
	[EntityTypes.Asset]: Liferay.Language.get('assets'),
	[EntityTypes.DataSource]: Liferay.Language.get('data-source'),
	[EntityTypes.Individual]: Liferay.Language.get('individuals'),
	[EntityTypes.IndividualsSegment]: Liferay.Language.get('segments'),
	[EntityTypes.Page]: Liferay.Language.get('pages'),
};

const DATA_SOURCE_LANG_MAP: Record<string, string> = {
	[DataSourceTypes.Csv]: Liferay.Language.get('csv'),
	[DataSourceTypes.Liferay]: Liferay.Language.get('liferay-dxp'),
};

export const getDataSourceLangKey = (type: string): string =>
	DATA_SOURCE_LANG_MAP[type];

export const getTypeLangKey = (type: string): string =>
	(ENTITY_LANG_MAP as Record<string, string>)[type];

export const getDisplayRole = (roleName: UserRoleNames): string => {
	switch (roleName) {
		case UserRoleNames.Administrator:
			return Liferay.Language.get('administrator');
		case UserRoleNames.Owner:
			return Liferay.Language.get('owner');
		case UserRoleNames.Member:
			return Liferay.Language.get('member');
		default:
			return roleName;
	}
};

const LABELS_MAP: Record<string, string> = {
	desktop: Liferay.Language.get('desktop'),
	mobile: Liferay.Language.get('other-mobile'),
	smartphone: Liferay.Language.get('phone'),
	tablet: Liferay.Language.get('tablet'),
	tv: Liferay.Language.get('tv'),
};

/**
 * Get Device Label
 * @param {string} deviceType
 */

export const getDeviceLabel = (deviceType: string): string =>
	LABELS_MAP[deviceType.toLowerCase()] || deviceType;

export const isJapaneseLang = (value: string): boolean =>
	/[\u3040-\u309F\u30A0-\u30FF\u4E00-\u9FAF]/.test(value);
