import {POSITIONS as ALIGN_POSITIONS} from './align';

export enum AcquisitionTypes {
	Channel = 'CHANNEL',
	Referrer = 'REFERRER',
	SourceMedium = 'SOURCE_MEDIUM'
}

export enum ActivityActions {
	Comments = 3,
	Downloads = 0,
	Impressions = 4,
	Submissions = 1,
	Visits = 2
}

export enum Applications {
	Contacts = 'contacts',
	Main = 'main'
}

export enum AssetNames {
	BlogViewed = 'blogViewed',
	CommentPosted = 'commentPosted',
	DocumentDownloaded = 'documentDownloaded',
	DocumentPreviewed = 'documentPreviewed',
	FormSubmitted = 'formSubmitted',
	FormViewed = 'formViewed',
	PageViewed = 'pageViewed',
	WebContentViewed = 'webContentViewed'
}

export enum AssetTypes {
	Asset = 'Asset',
	Blog = 'Blog',
	Document = 'Document',
	Form = 'Form',
	Journal = 'Journal',
	WebContent = 'WebContent',
	WebPage = 'Page'
}

export enum ChannelPermissionTypes {
	AllUsers = 0,
	SelectUsers = 1
}

export enum CompositionTypes {
	AccountInterests = 'accountInterests',
	Acquisitions = 'acquisitions',
	IndividualInterests = 'individualInterests',
	SearchTerms = 'searchTerms',
	SegmentInterests = 'individualSegmentInterests',
	SiteInterests = 'siteInterests'
}

export enum ConjunctionKey {
	And = 'and',
	Or = 'or'
}

export enum CredentialTypes {
	OAuth1 = 'OAuth 1 Authentication',
	OAuth2 = 'OAuth 2 Authentication',
	Token = 'Token Authentication'
}

export enum DataSourceDisplayStatuses {
	Active = 'ACTIVE',
	Configuring = 'CONFIGURING',
	DeleteError = 'DELETE_ERROR',
	InDeletion = 'IN_DELETION',
	Inactive = 'INACTIVE'
}

export enum DataSourceProgressStatuses {
	Completed = 'COMPLETED',
	Failed = 'FAILED',
	InProgress = 'IN_PROGRESS',
	Started = 'STARTED'
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
	Unconfigured = 'UNCONFIGURED'
}
export enum DataSourceStatuses {
	Active = 'ACTIVE',
	Inactive = 'INACTIVE'
}

export enum DataSourceTypes {
	Csv = 'CSV',
	Liferay = 'LIFERAY',
	Salesforce = 'SALESFORCE'
}

export enum EntityTypes {
	Account = 0,
	AccountsSegment = 3,
	Asset = 5,
	DataSource = 1,
	Individual = 2,
	IndividualsSegment = 4,
	Page = 6
}

export enum ExpirationPeriod {
	In30Days = '2592000',
	In6Months = '15778800',
	In1Year = '31557600',
	Indefinite = '3155760000'
}

export enum FaroEnv {
	Local = 'local',
	Production = 'prd',
	Staging = 'stg'
}

export enum FieldContexts {
	Custom = 'custom',
	Demographics = 'demographics',
	Interests = 'interests',
	Organization = 'organization'
}

export enum FieldOwnerTypes {
	Account = 'account',
	Individual = 'individual',
	Organization = 'organization'
}

export enum FieldTypes {
	Boolean = 'Boolean',
	Date = 'Date',
	Number = 'Number',
	String = 'Text'
}

export enum GDPRRequestStatuses {
	Completed = 'COMPLETED',
	Error = 'ERROR',
	Expired = 'EXPIRED',
	Pending = 'PENDING',
	Running = 'RUNNING'
}

export enum GDPRRequestTypes {
	Access = 'ACCESS',
	Delete = 'DELETE',
	Suppress = 'SUPPRESS',
	Unsuppress = 'UNSUPPRESS'
}

export enum JobRunDataPeriods {
	Last7Days = 'LAST_7_DAYS',
	Last30Days = 'LAST_30_DAYS',
	Last180Days = 'LAST_180_DAYS',
	Last365Days = 'LAST_365_DAYS'
}

export enum JobRunFrequencies {
	Every7Days = 'EVERY_7_DAYS',
	Every14Days = 'EVERY_14_DAYS',
	Every30Days = 'EVERY_30_DAYS',
	Manual = 'MANUAL'
}

export enum JobRunStatuses {
	Completed = 'COMPLETED',
	Failed = 'FAILED',
	Published = 'PUBLISHED',
	Running = 'RUNNING'
}

export enum JobStatuses {
	Failed = 'FAILED',
	Pending = 'PENDING',
	Ready = 'READY',
	Running = 'RUNNING',
	Scheduled = 'SCHEDULED'
}

export enum JobTypes {
	ItemSimilarity = 'CONTENT_RECOMMENDATION_ITEM_SIMILARITY'
}

export enum LanguageIds {
	English = 'en_US',
	Japanese = 'ja_JP',
	Portuguese = 'pt_BR',
	Spanish = 'es_ES'
}

export enum OrderByDirections {
	Ascending = 'ASC',
	Descending = 'DESC'
}

export enum PreferencesScopes {
	User = 'user',
	Group = 'group'
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
	Unconfigured = 'UNCONFIGURED'
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
	Yesterday = '1'
}

export enum SegmentStates {
	Disabled = 'DISABLED',
	InProgress = 'IN_PROGRESS',
	Ready = 'READY'
}

export enum SegmentTypes {
	Dynamic = 'DYNAMIC'
}

export enum SessionEntityTypes {
	Account = 'ACCOUNT',
	Individual = 'INDIVIDUAL'
}

export enum Sizes {
	Small = 'sm',
	Medium = 'md',
	Large = 'lg',
	XLarge = 'xl',
	XXLarge = 'xxl',
	XXXLarge = 'xxxl'
}

export enum SubscriptionStatuses {
	Approaching = 1,
	Ok = 0,
	Over = 2
}

export enum TimeIntervals {
	Day = 'day',
	Month = 'month',
	Quarter = 'quarter',
	Week = 'week',
	Year = 'year'
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
	Yesterday = 'yesterday'
}

export enum UserRoleNames {
	Administrator = 'Site Administrator',
	Member = 'Site Member',
	Owner = 'Site Owner'
}

export enum UserStatuses {
	Approved = 0,
	Pending = 1,
	Requested = 2
}

export const ALIGNMENTS_MAP = {
	bottom: ALIGN_POSITIONS.BottomCenter,
	'bottom-left': ALIGN_POSITIONS.BottomLeft,
	'bottom-right': ALIGN_POSITIONS.BottomRight,
	left: ALIGN_POSITIONS.LeftCenter,
	right: ALIGN_POSITIONS.RightCenter,
	top: ALIGN_POSITIONS.TopCenter,
	'top-left': ALIGN_POSITIONS.TopLeft,
	'top-right': ALIGN_POSITIONS.TopRight
};

export const ASSET_METRICS = [
	{
		key: 'abandonmentsMetric',
		selectTitle: `${Liferay.Language.get(
			'form-abandonment'
		)} (${Liferay.Language.get('ratio').toLowerCase()})`,
		title: Liferay.Language.get('form-abandonment'),
		type: 'percentage'
	},
	{
		key: 'clicksMetric',
		selectTitle: `${Liferay.Language.get(
			'asset-clicks'
		)} (${Liferay.Language.get('sum').toLowerCase()})`,
		title: Liferay.Language.get('clicks'),
		type: 'number'
	},
	{
		key: 'completionTimeMetric',
		selectTitle: `${Liferay.Language.get(
			'form-completion-time'
		)} (${Liferay.Language.get('average').toLowerCase()})`,
		title: Liferay.Language.get('completion-time'),
		type: 'time'
	},
	{
		key: 'downloadsMetric',
		selectTitle: `${Liferay.Language.get(
			'asset-downloads'
		)} (${Liferay.Language.get('sum').toLowerCase()})`,
		title: Liferay.Language.get('downloads'),
		type: 'number'
	},
	{
		key: 'readingTimeMetric',
		selectTitle: `${Liferay.Language.get(
			'asset-interaction-time'
		)} (${Liferay.Language.get('average').toLowerCase()})`,
		title: Liferay.Language.get('interaction-time'),
		type: 'time'
	},
	{
		key: 'submissionsMetric',
		selectTitle: `${Liferay.Language.get(
			'form-submissions'
		)} (${Liferay.Language.get('sum').toLowerCase()})`,
		title: Liferay.Language.get('form-submissions'),
		type: 'number'
	},
	{
		key: 'viewsMetric',
		selectTitle: `${Liferay.Language.get(
			'asset-views'
		)} (${Liferay.Language.get('sum').toLowerCase()})`,
		title: Liferay.Language.get('views'),
		type: 'number'
	}
];

export const DATA_RETENTION_PERIOD_KEY = 'data-retention-period';

export const DEVELOPER_MODE = FARO_DEV_MODE;

// LRAC-11571 Disable temporarily Accounts

export const ENABLE_ACCOUNTS = false;

export const ENABLE_ADD_TRIAL_WORKSPACE =
	FARO_ENV === FaroEnv.Local || FARO_ENV === FaroEnv.Staging;

// LRAC - 11652 Hide Displayed Asset Card on the Page report

export const ENABLE_ASSET_CARD = false;

// LRAC-13649 Hide the keywords blocklist screen from Settings>Definitions

export const ENABLE_BLOCKLIST_KEYWORDS = false;

// LRAC-11651 Disable temporarily CSV File

export const ENABLE_CSVFILE = false;

// LRAC-13389 Disable temporarily Delete Property and Delete Data Source buttons

export const ENABLE_DELETE_DATA_SOURCE_BUTTON = false;

export const ENABLE_DELETE_PROPERTY_BUTTON = false;

// LRAC-11650 Hide Form Abandonment Card

export const ENABLE_FORM_ABANDONMENT = false;

// LRAC-10908 Hide global filters

export const ENABLE_GLOBAL_FILTER = false;

// LRAC-13781 [BUG] The Last Seen column only displays the creation date of the token

export const ENABLE_LAST_ACCESS_DATE = false;

// LRAC-11571 Disable temporarily Salesforce

export const ENABLE_SALESFORCE = false;

export const EXPIRATION_DATE_LABELS = {
	[ExpirationPeriod.In30Days]: Liferay.Language.get('30-days'),
	[ExpirationPeriod.In6Months]: Liferay.Language.get('6-months'),
	[ExpirationPeriod.In1Year]: Liferay.Language.get('1-year'),
	[ExpirationPeriod.Indefinite]: Liferay.Language.get('indefinite')
};

export const LANGUAGES = [
	{
		id: LanguageIds.English,
		label: Liferay.Language.get('english')
	},
	{
		id: LanguageIds.Japanese,
		label: Liferay.Language.get('japanese')
	},
	{
		id: LanguageIds.Portuguese,
		label: Liferay.Language.get('portuguese')
	},
	{
		id: LanguageIds.Spanish,
		label: Liferay.Language.get('spanish')
	}
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
	'top'
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
	[RangeKeyTimeRanges.Yesterday]: Liferay.Language.get('yesterday')
};

export const TWO_DAYS = '172800000';

const Constants: Window['faroConstants'] = window.faroConstants;

export default Constants;
