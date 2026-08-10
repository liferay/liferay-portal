import {POSITIONS as ALIGN_POSITIONS} from './align';

export enum AcquisitionTypes {
	Channel = 'CHANNEL',
	Referrer = 'REFERRER',
	SourceMedium = 'SOURCE_MEDIUM',
}

export enum ActivityActions {
	Comments = 3,
	Downloads = 0,
	Impressions = 4,
	Submissions = 1,
	Visits = 2,
}

export enum Applications {
	Contacts = 'contacts',
	Main = 'main',
}

export enum EventNames {
	Click = 'click',
	Comment = 'comment',
	Download = 'download',
	Impression = 'impression',
	Submit = 'submit',
	View = 'view',
}

export enum AssetTypes {
	Asset = 'Asset',
	Blog = 'Blog',
	Document = 'Document',
	Form = 'Form',
	Journal = 'Journal',
	ObjectEntry = 'ObjectEntry',
	WebContent = 'WebContent',
	WebPage = 'Page',
}

/**
 * Application ids produced by Liferay DXP itself. Everything else comes from an
 * external data source (a webhook), which the activity stream both labels
 * differently and leaves ungrouped, since only DXP events are page bound.
 */
export const LIFERAY_DXP_APPLICATION_IDS = new Set([
	'Blog',
	'Comment',
	'CustomEvent',
	'Document',
	'Form',
	'Layout',
	'ObjectEntry',
	'Page',
	'Ratings',
	'WebContent',
]);

export enum ChannelPermissionTypes {
	AllUsers = 0,
	SelectUsers = 1,
}

export enum CompositionTypes {
	AccountInterests = 'accountInterests',
	Acquisitions = 'acquisitions',
	IndividualInterests = 'individualInterests',
	SearchTerms = 'searchTerms',
	SegmentInterests = 'individualSegmentInterests',
	SiteInterests = 'siteInterests',
}

export enum ConjunctionKey {
	And = 'and',
	Or = 'or',
	Then = 'then',
}

export enum CredentialTypes {
	OAuth1 = 'OAuth 1 Authentication',
	OAuth2 = 'OAuth 2 Authentication',
	Token = 'Token Authentication',
}

export enum DataSourceDisplayStatuses {
	Active = 'ACTIVE',
	Configuring = 'CONFIGURING',
	DeleteError = 'DELETE_ERROR',
	InDeletion = 'IN_DELETION',
	Inactive = 'INACTIVE',
}

export enum DataSourceProgressStatuses {
	Completed = 'COMPLETED',
	Failed = 'FAILED',
	InProgress = 'IN_PROGRESS',
	Started = 'STARTED',
}

export enum DataSourceStates {
	ActionNeeded = 'ACTION_NEEDED',
	AnalyticsClientConfigurationFailure = 'ANALYTICS_CLIENT_CONFIGURATION_FAILURE',
	CredentialsInvalid = 'CREDENTIALS_INVALID',
	CredentialsValid = 'CREDENTIALS_VALID',
	Disconnected = 'DISCONNECTED',
	InProgressDeleting = 'IN_PROGRESS_DELETING',
	LiferayVersionInvalid = 'LIFERAY_VERSION_INVALID',
	UndefinedError = 'UNDEFINED_ERROR',
	Ready = 'READY',
	UrlInvalid = 'URL_INVALID',
	Unconfigured = 'UNCONFIGURED',
}
export enum DataSourceStatuses {
	Active = 'ACTIVE',
	Authenticated = 'AUTHENTICATED',
	Configuring = 'CONFIGURING',
	Inactive = 'INACTIVE',
	OauthExpired = 'OAUTH_EXPIRED',
}

export enum DataSourceTypes {
	Csv = 'CSV',
	Demandbase = 'DEMANDBASE',
	Hubspot = 'HUBSPOT',
	Liferay = 'LIFERAY',
	MarketoCampaign = 'MARKETO_CAMPAIGN',
	MarketoEventStream = 'MARKETO',
	Salesforce = 'SALESFORCE',
}

export enum EntityTypes {
	Account = 0,
	AccountsSegment = 3,
	Asset = 5,
	DataSource = 1,
	Individual = 2,
	IndividualsSegment = 4,
	Page = 6,
}

export enum ExpirationPeriod {
	In30Days = '2592000',
	In6Months = '15778800',
	In1Year = '31557600',
	Indefinite = '3155760000',
}

export enum FaroEnv {
	Local = 'local',
	Production = 'prd',
	Staging = 'stg',
}

export enum FieldContexts {
	Account = 'account',
	Custom = 'custom',
	Demographics = 'demographics',
	Interests = 'interests',
	Organization = 'organization',
}

export enum FieldOwnerTypes {
	Account = 'account',
	Individual = 'individual',
	Organization = 'organization',
}

export enum FieldTypes {
	Boolean = 'Boolean',
	Date = 'Date',
	Number = 'Number',
	String = 'Text',
}

export enum GDPRRequestStatuses {
	Completed = 'COMPLETED',
	Error = 'ERROR',
	Expired = 'EXPIRED',
	Pending = 'PENDING',
	Running = 'RUNNING',
}

export enum GDPRRequestTypes {
	Access = 'ACCESS',
	Delete = 'DELETE',
	Suppress = 'SUPPRESS',
	Unsuppress = 'UNSUPPRESS',
}

export enum JobRunDataPeriods {
	Last7Days = 'LAST_7_DAYS',
	Last30Days = 'LAST_30_DAYS',
	Last180Days = 'LAST_180_DAYS',
	Last365Days = 'LAST_365_DAYS',
}

export enum JobRunFrequencies {
	Every7Days = 'EVERY_7_DAYS',
	Every14Days = 'EVERY_14_DAYS',
	Every30Days = 'EVERY_30_DAYS',
	Manual = 'MANUAL',
}

export enum JobRunStatuses {
	Completed = 'COMPLETED',
	Failed = 'FAILED',
	Published = 'PUBLISHED',
	Running = 'RUNNING',
}

export enum JobStatuses {
	Failed = 'FAILED',
	Pending = 'PENDING',
	Ready = 'READY',
	Running = 'RUNNING',
	Scheduled = 'SCHEDULED',
}

export enum JobTypes {
	ItemSimilarity = 'CONTENT_RECOMMENDATION_ITEM_SIMILARITY',
}

export enum LanguageIds {
	English = 'en_US',
	Japanese = 'ja_JP',
	Portuguese = 'pt_BR',
	Spanish = 'es_ES',
}

/**
 * - Loading (1) The status is set to `loading` only when the first
 *   requisition occurs.
 * - Refetch (2) The status is set to `refetch` when a change in the variables API
 *   or refetch method is called.
 * - Polling (3) The status is set to `polling` when pollInterval is set above 0.
 * - Unused (4) When no request is happening the status will be `unused`.
 * - Error (5) When any timeout or request `error` occurs, the status will be set
 *   to error.
 */
export enum NetworkState {
	Error = 5,
	Loading = 1,
	Polling = 3,
	Refetch = 2,
	Unused = 4,
}

export enum OrderByDirections {
	Ascending = 'ASC',
	Descending = 'DESC',
}

export enum PreferencesScopes {
	User = 'user',
	Group = 'group',
}

export enum ProjectStates {
	Activating = 'ACTIVATING',
	AutoRedeployFailed = 'AUTO_REDEPLOY_FAILED',
	Deactivated = 'DEACTIVATED',
	Maintenance = 'MAINTENANCE',
	NotReady = 'NOT READY',
	Ready = 'READY',
	Scheduled = 'SCHEDULED',
	Unavailable = 'UNAVAILABLE',
	Unconfigured = 'UNCONFIGURED',
}

export enum RangeKeyTimeRanges {
	CustomRange = 'CUSTOM',
	Last180Days = '180',
	Last24Hours = '0',
	Last28Days = '28',
	Last30Days = '30',
	Last7Days = '7',
	Last90Days = '90',
	LastYear = '365',
	Yesterday = '1',
}

export enum SegmentActivationScheduleTypes {
	Batch = 'BATCH',
	RealTime = 'REAL_TIME',
}

export enum SegmentActivationFrequencyTypes {
	Between = 'BETWEEN',
	Indefinitely = 'INDEFINITELY',
}

export enum SegmentCategories {
	Account = 'ACCOUNT',
	Individual = 'INDIVIDUAL',
}

export enum SegmentStates {
	Disabled = 'DISABLED',
	InProgress = 'IN_PROGRESS',
	Ready = 'READY',
}

export enum SegmentTypes {
	Batch = 'BATCH',
	RealTime = 'REAL_TIME',
}

export enum SessionEntityTypes {
	Account = 'ACCOUNT',
	Individual = 'INDIVIDUAL',
}

export enum Sizes {
	Small = 'sm',
	Medium = 'md',
	Large = 'lg',
	XLarge = 'xl',
	XXLarge = 'xxl',
	XXXLarge = 'xxxl',
}

export enum SubscriptionStatuses {
	Approaching = 1,
	Ok = 0,
	Over = 2,
}

export enum TimeIntervals {
	Day = 'day',
	Month = 'month',
	Quarter = 'quarter',
	Week = 'week',
	Year = 'year',
}

export enum TimeSpans {
	AllTime = 'ever',
	LastYear = 'lastYear',
	Last24Hours = 'last24Hours',
	Last7Days = 'last7Days',
	Last28Days = 'last28Days',
	Last30Days = 'last30Days',
	Last90Days = 'last90Days',
	Today = 'today',
	Yesterday = 'yesterday',
}

export enum UserRoleNames {
	Administrator = 'Site Administrator',
	Member = 'Site Member',
	Owner = 'Site Owner',
}

export enum UserStatuses {
	Approved = 0,
	Pending = 1,
	Requested = 2,
}

export const ALIGNMENTS_MAP = {
	bottom: ALIGN_POSITIONS.BottomCenter,
	'bottom-left': ALIGN_POSITIONS.BottomLeft,
	'bottom-right': ALIGN_POSITIONS.BottomRight,
	left: ALIGN_POSITIONS.LeftCenter,
	right: ALIGN_POSITIONS.RightCenter,
	top: ALIGN_POSITIONS.TopCenter,
	'top-left': ALIGN_POSITIONS.TopLeft,
	'top-right': ALIGN_POSITIONS.TopRight,
};

export const ASSET_METRICS = [
	{
		key: 'abandonmentsMetric',
		selectTitle: `${Liferay.Language.get(
			'form-abandonment'
		)} (${Liferay.Language.get('ratio').toLowerCase()})`,
		title: Liferay.Language.get('form-abandonment'),
		type: 'percentage',
	},
	{
		key: 'clicksMetric',
		selectTitle: `${Liferay.Language.get(
			'asset-clicks'
		)} (${Liferay.Language.get('sum').toLowerCase()})`,
		title: Liferay.Language.get('clicks'),
		type: 'number',
	},
	{
		key: 'completionTimeMetric',
		selectTitle: `${Liferay.Language.get(
			'form-completion-time'
		)} (${Liferay.Language.get('average').toLowerCase()})`,
		title: Liferay.Language.get('completion-time'),
		type: 'time',
	},
	{
		key: 'downloadsMetric',
		selectTitle: `${Liferay.Language.get(
			'asset-downloads'
		)} (${Liferay.Language.get('sum').toLowerCase()})`,
		title: Liferay.Language.get('downloads'),
		type: 'number',
	},
	{
		key: 'readingTimeMetric',
		selectTitle: `${Liferay.Language.get(
			'asset-interaction-time'
		)} (${Liferay.Language.get('average').toLowerCase()})`,
		title: Liferay.Language.get('interaction-time'),
		type: 'time',
	},
	{
		key: 'submissionsMetric',
		selectTitle: `${Liferay.Language.get(
			'form-submissions'
		)} (${Liferay.Language.get('sum').toLowerCase()})`,
		title: Liferay.Language.get('form-submissions'),
		type: 'number',
	},
	{
		key: 'viewsMetric',
		selectTitle: `${Liferay.Language.get(
			'asset-views'
		)} (${Liferay.Language.get('sum').toLowerCase()})`,
		title: Liferay.Language.get('views'),
		type: 'number',
	},
];

export const DATA_RETENTION_PERIOD_KEY = 'data-retention-period';

export const DEVELOPER_MODE = FARO_DEV_MODE;

export const ENABLE_ADD_TRIAL_WORKSPACE =
	FARO_ENV === FaroEnv.Local || FARO_ENV === FaroEnv.Staging;

// Feature flags toggled at runtime now live in `shared/util/feature-flags.ts`.

export const EXPIRATION_DATE_LABELS = {
	[ExpirationPeriod.In30Days]: Liferay.Language.get('30-days'),
	[ExpirationPeriod.In6Months]: Liferay.Language.get('6-months'),
	[ExpirationPeriod.In1Year]: Liferay.Language.get('1-year'),
	[ExpirationPeriod.Indefinite]: Liferay.Language.get('indefinite'),
};

export const LANGUAGES = [
	{
		id: LanguageIds.English,
		label: Liferay.Language.get('english'),
	},
	{
		id: LanguageIds.Japanese,
		label: Liferay.Language.get('japanese'),
	},
	{
		id: LanguageIds.Portuguese,
		label: Liferay.Language.get('portuguese'),
	},
	{
		id: LanguageIds.Spanish,
		label: Liferay.Language.get('spanish'),
	},
];

export const ONE_DAY = '86400000';

export const ONE_MONTH = '2592000000';

export const POSITIONS = [
	'top',
	'top',
	'right',
	'bottom',
	'bottom',
	'bottom',
	'left',
	'top',
];

export const SEVEN_MONTHS = '18144000000';

export const THIRTEEN_MONTHS = '33696000000';

export const TIME_RANGE_LABELS = {
	[RangeKeyTimeRanges.Last180Days]: Liferay.Language.get('last-180-days'),
	[RangeKeyTimeRanges.Last24Hours]: Liferay.Language.get('last-24-hours'),
	[RangeKeyTimeRanges.Last28Days]: Liferay.Language.get('last-28-days'),
	[RangeKeyTimeRanges.Last30Days]: Liferay.Language.get('last-30-days'),
	[RangeKeyTimeRanges.Last7Days]: Liferay.Language.get('last-seven-days'),
	[RangeKeyTimeRanges.Last90Days]: Liferay.Language.get('last-90-days'),
	[RangeKeyTimeRanges.LastYear]: Liferay.Language.get('last-year'),
	[RangeKeyTimeRanges.Yesterday]: Liferay.Language.get('yesterday'),
};

/**
 * Fills the trend row on cards without a trend so that it keeps its height,
 * leaving the value aligned with the sibling cards that do render a trend.
 */
export const TREND_PLACEHOLDER = '\u00A0';

export const TWO_DAYS = '172800000';

const Constants: Window['faroConstants'] = window.faroConstants;

export default Constants;
