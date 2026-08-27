import {TimeSpans} from 'shared/util/constants';

export const EVER = 'ever';
export const SINCE = 'since';

export const DAYS = 'days';
export const HOURS = 'hours';

export const HOURS_IN_A_DAY = 24;
export const MAX_DAYS = 30;
export const MAX_NESTED_OR_CRITERIA = 3;
export const MAX_SEQUENTIAL_CRITERIA = 5;

export const NESTED_OR_LIMIT_ALERT = {
	exceedsLimit: {
		color: 'danger',
		text: Liferay.Language.get(
			'the-maximum-number-of-or-conditions-has-been-exceeded-remove-items-to-save-the-segment'
		),
	},
	reachedLimit: {
		color: 'warning',
		text: Liferay.Language.get(
			'the-maximum-number-of-or-conditions-has-been-reached'
		),
	},
} as const;

export const SEQUENTIAL_LIMIT_ALERT = {
	exceedsLimit: {
		color: 'danger',
		text: Liferay.Language.get(
			'the-maximum-number-of-sequential-criteria-has-been-exceeded-remove-items-to-save-the-segment'
		),
	},
	reachedLimit: {
		color: 'warning',
		text: Liferay.Language.get(
			'the-maximum-number-of-sequential-criteria-has-been-reached'
		),
	},
} as const;

export type NestedOrLimitState = keyof typeof NESTED_OR_LIMIT_ALERT;

export type SequentialLimitState = keyof typeof SEQUENTIAL_LIMIT_ALERT;

export const isKnown = 'is-known';
export const isUnknown = 'is-unknown';

export enum IndividualTypes {
	ANONYMOUS = 'ANONYMOUS',
	KNOWN = 'KNOWN',
}

export enum AccountTypes {
	KNOWN = 'KNOWN',
	UNKNOWN = 'UNKNOWN',
}

/**
 * Constants for date formatting
 */

export const INPUT_DATE_FORMAT = 'YYYY-MM-DD';
export const INPUT_DATE_TIME_FORMAT = 'YYYY-MM-DDTHH:mmZ';

/**
 * Constants for OData query.
 */

export enum Conjunctions {
	And = 'and',
	Or = 'or',
}

export enum CustomFunctionOperators {
	AccountsFilter = 'accounts-filter',
	AccountsFilterByCount = 'accounts-filter-by-count',
	ActivitiesFilter = 'activities-filter',
	ActivitiesFilterByCount = 'activities-filter-by-count',
	EventsFilterByCount = 'events-filter-by-count',
	InterestsFilter = 'interests-filter',
	OrganizationsFilter = 'organizations-filter',
	SessionsFilter = 'sessions-filter',
	TagsFilter = 'tags-filter',
	VocabulariesFilter = 'vocabularies-filter',
}

export enum DisplayOnlyOperators {
	IsKnown = 'ne',
	IsUnknown = 'eq',
}

export enum FunctionalOperators {
	Between = 'between',
	Contains = 'contains',
}

export enum NotOperators {
	NotAccountsFilter = 'not-accounts-filter',
	NotAccountsFilterByCount = 'not-accounts-filter-by-count',
	NotActivitiesFilter = 'not-activities-filter',
	NotActivitiesFilterByCount = 'not-activities-filter-by-count',
	NotContains = 'not-contains',
	NotEventsFilterByCount = 'not-events-filter-by-count',
	NotOrganizationsFilter = 'not-organizations-filter',
	NotSessionsFilter = 'not-sessions-filter',
	NotTagsFilter = 'not-tags-filter',
	NotVocabulariesFilter = 'not-vocabularies-filter',
}

export const GROUP = 'GROUP';

export enum RelationalOperators {
	EQ = 'eq',
	GE = 'ge',
	GT = 'gt',
	In = 'in',
	LE = 'le',
	LT = 'lt',
	NE = 'ne',
}

/**
 * Constants to match property types in the passed in supportedProperties array.
 */

export enum PropertyTypes {
	AccountDate = 'account-date',
	AccountNumber = 'account-number',
	AccountSelectText = 'account-select-text',
	AccountText = 'account-text',
	Behavior = 'behavior',
	Boolean = 'boolean',
	Date = 'date',
	DateTime = 'date-time',
	Duration = 'duration',
	Event = 'event',
	Interest = 'interest',
	Number = 'number',
	OrganizationBoolean = 'organization-boolean',
	OrganizationDate = 'organization-date',
	OrganizationDateTime = 'organization-date-time',
	OrganizationNumber = 'organization-number',
	OrganizationSelectText = 'organization-select-text',
	OrganizationText = 'organization-text',
	SelectText = 'select-text',
	SessionChannel = 'session-channel',
	SessionDateTime = 'session-date-time',
	SessionGeolocation = 'session-geolocation',
	SessionNumber = 'session-number',
	SessionText = 'session-text',
	SessionUtmParameter = 'session-utm-parameter',
	Tag = 'tag',
	Text = 'text',
	Vocabulary = 'vocabulary',
}

/**
 * Constants for CriteriaBuilder component.
 */

export const CUSTOM_FUNCTION_OPERATOR_KEY_MAP = {
	['accounts.filter']: CustomFunctionOperators.AccountsFilter,
	['accounts.filterByCount']: CustomFunctionOperators.AccountsFilterByCount,
	['activities.filter']: CustomFunctionOperators.ActivitiesFilter,
	['activities.filterByCount']:
		CustomFunctionOperators.ActivitiesFilterByCount,
	['events.filterByCount']: CustomFunctionOperators.EventsFilterByCount,
	['interests.filter']: CustomFunctionOperators.InterestsFilter,
	['organizations.filter']: CustomFunctionOperators.OrganizationsFilter,
	['sessions.filter']: CustomFunctionOperators.SessionsFilter,
	['tag.filter']: CustomFunctionOperators.TagsFilter,
	['vocabulary.filter']: CustomFunctionOperators.VocabulariesFilter,
};

export const SUPPORTED_CONJUNCTION_OPTIONS = [
	{
		key: Conjunctions.And,
		label: Liferay.Language.get('and'),
		name: Conjunctions.And,
	},
	{
		key: Conjunctions.Or,
		label: Liferay.Language.get('or'),
		name: Conjunctions.Or,
	},
];

export const SUPPORTED_OPERATORS_MAP = {
	[PropertyTypes.AccountDate]: [
		{
			key: CustomFunctionOperators.AccountsFilter,
			label: Liferay.Language.get('is').toLowerCase(),
			name: CustomFunctionOperators.AccountsFilter,
		},
	],
	[PropertyTypes.AccountNumber]: [
		{
			key: CustomFunctionOperators.AccountsFilter,
			label: Liferay.Language.get('is').toLowerCase(),
			name: CustomFunctionOperators.AccountsFilter,
		},
	],
	[PropertyTypes.AccountSelectText]: [
		{
			key: CustomFunctionOperators.AccountsFilter,
			label: Liferay.Language.get('is').toLowerCase(),
			name: CustomFunctionOperators.AccountsFilter,
		},
		{
			key: NotOperators.NotAccountsFilter,
			label: Liferay.Language.get('is-not').toLowerCase(),
			name: NotOperators.NotAccountsFilter,
		},
	],
	[PropertyTypes.AccountText]: [
		{
			key: CustomFunctionOperators.AccountsFilter,
			label: Liferay.Language.get('is').toLowerCase(),
			name: CustomFunctionOperators.AccountsFilter,
		},
	],
	[PropertyTypes.Behavior]: [
		{
			key: CustomFunctionOperators.ActivitiesFilterByCount,
			label: Liferay.Language.get('has').toLowerCase(),
			name: CustomFunctionOperators.ActivitiesFilterByCount,
		},
		{
			key: NotOperators.NotActivitiesFilterByCount,
			label: Liferay.Language.get('has-not').toLowerCase(),
			name: NotOperators.NotActivitiesFilterByCount,
		},
	],
	[PropertyTypes.Boolean]: [
		{
			key: RelationalOperators.EQ,
			label: Liferay.Language.get('is').toLowerCase(),
			name: RelationalOperators.EQ,
		},
	],
	[PropertyTypes.Tag]: [
		{
			key: CustomFunctionOperators.TagsFilter,
			label: Liferay.Language.get('has').toLowerCase(),
			name: CustomFunctionOperators.TagsFilter,
		},
		{
			key: NotOperators.NotTagsFilter,
			label: Liferay.Language.get('has-not').toLowerCase(),
			name: NotOperators.NotTagsFilter,
		},
	],
	[PropertyTypes.Vocabulary]: [
		{
			key: CustomFunctionOperators.VocabulariesFilter,
			label: Liferay.Language.get('has').toLowerCase(),
			name: CustomFunctionOperators.VocabulariesFilter,
		},
		{
			key: NotOperators.NotVocabulariesFilter,
			label: Liferay.Language.get('has-not').toLowerCase(),
			name: NotOperators.NotVocabulariesFilter,
		},
	],
	[PropertyTypes.Date]: [
		{
			key: RelationalOperators.LT,
			label: Liferay.Language.get('is-before').toLowerCase(),
			name: RelationalOperators.LT,
		},
		{
			key: RelationalOperators.EQ,
			label: Liferay.Language.get('is').toLowerCase(),
			name: RelationalOperators.EQ,
		},
		{
			key: RelationalOperators.GT,
			label: Liferay.Language.get('is-after').toLowerCase(),
			name: RelationalOperators.GT,
		},
	],
	[PropertyTypes.DateTime]: [
		{
			key: RelationalOperators.LT,
			label: Liferay.Language.get('is-before').toLowerCase(),
			name: RelationalOperators.LT,
		},
		{
			key: RelationalOperators.EQ,
			label: Liferay.Language.get('is').toLowerCase(),
			name: RelationalOperators.EQ,
		},
		{
			key: RelationalOperators.GT,
			label: Liferay.Language.get('is-after').toLowerCase(),
			name: RelationalOperators.GT,
		},
	],
	[PropertyTypes.Duration]: [
		{
			key: RelationalOperators.GT,
			label: Liferay.Language.get('greater-than').toLowerCase(),
			name: RelationalOperators.GT,
		},
		{
			key: RelationalOperators.LT,
			label: Liferay.Language.get('less-than').toLowerCase(),
			name: RelationalOperators.LT,
		},
	],
	[PropertyTypes.Event]: [
		{
			key: CustomFunctionOperators.EventsFilterByCount,
			label: Liferay.Language.get('has').toLowerCase(),
			name: CustomFunctionOperators.EventsFilterByCount,
		},
		{
			key: NotOperators.NotEventsFilterByCount,
			label: Liferay.Language.get('has-not').toLowerCase(),
			name: NotOperators.NotEventsFilterByCount,
		},
	],
	[PropertyTypes.Interest]: [
		{
			key: CustomFunctionOperators.InterestsFilter,
			label: Liferay.Language.get('is').toLowerCase(),
			name: CustomFunctionOperators.InterestsFilter,
		},
	],
	[PropertyTypes.Number]: [
		{
			key: RelationalOperators.EQ,
			label: Liferay.Language.get('is-equal-to').toLowerCase(),
			name: RelationalOperators.EQ,
		},
		{
			key: RelationalOperators.GT,
			label: Liferay.Language.get('greater-than').toLowerCase(),
			name: RelationalOperators.GT,
		},
		{
			key: RelationalOperators.LT,
			label: Liferay.Language.get('less-than').toLowerCase(),
			name: RelationalOperators.LT,
		},
		{
			key: RelationalOperators.NE,
			label: Liferay.Language.get('is-not-equal-to').toLowerCase(),
			name: RelationalOperators.NE,
		},
		{
			key: isKnown,
			label: Liferay.Language.get('is-known').toLowerCase(),
			name: DisplayOnlyOperators.IsKnown,
		},
		{
			key: isUnknown,
			label: Liferay.Language.get('is-unknown').toLowerCase(),
			name: DisplayOnlyOperators.IsUnknown,
		},
	],
	[PropertyTypes.OrganizationBoolean]: [
		{
			key: CustomFunctionOperators.OrganizationsFilter,
			label: Liferay.Language.get('is').toLowerCase(),
			name: CustomFunctionOperators.OrganizationsFilter,
		},
	],
	[PropertyTypes.OrganizationDate]: [
		{
			key: CustomFunctionOperators.OrganizationsFilter,
			label: Liferay.Language.get('is').toLowerCase(),
			name: CustomFunctionOperators.OrganizationsFilter,
		},
	],
	[PropertyTypes.OrganizationDateTime]: [
		{
			key: CustomFunctionOperators.OrganizationsFilter,
			label: Liferay.Language.get('is').toLowerCase(),
			name: CustomFunctionOperators.OrganizationsFilter,
		},
	],
	[PropertyTypes.OrganizationNumber]: [
		{
			key: CustomFunctionOperators.OrganizationsFilter,
			label: Liferay.Language.get('is').toLowerCase(),
			name: CustomFunctionOperators.OrganizationsFilter,
		},
	],
	[PropertyTypes.OrganizationSelectText]: [
		{
			key: CustomFunctionOperators.OrganizationsFilter,
			label: Liferay.Language.get('is').toLowerCase(),
			name: CustomFunctionOperators.OrganizationsFilter,
		},
		{
			key: NotOperators.NotOrganizationsFilter,
			label: Liferay.Language.get('is-not').toLowerCase(),
			name: NotOperators.NotOrganizationsFilter,
		},
	],
	[PropertyTypes.OrganizationText]: [
		{
			key: CustomFunctionOperators.OrganizationsFilter,
			label: Liferay.Language.get('is').toLowerCase(),
			name: CustomFunctionOperators.OrganizationsFilter,
		},
	],
	[PropertyTypes.SelectText]: [
		{
			key: RelationalOperators.EQ,
			label: Liferay.Language.get('is').toLowerCase(),
			name: RelationalOperators.EQ,
		},
		{
			key: RelationalOperators.NE,
			label: Liferay.Language.get('is-not').toLowerCase(),
			name: RelationalOperators.NE,
		},
	],
	[PropertyTypes.SessionChannel]: [
		{
			key: CustomFunctionOperators.SessionsFilter,
			label: Liferay.Language.get('is').toLowerCase(),
			name: CustomFunctionOperators.SessionsFilter,
		},
	],
	[PropertyTypes.SessionDateTime]: [
		{
			key: CustomFunctionOperators.SessionsFilter,
			label: Liferay.Language.get('is').toLowerCase(),
			name: CustomFunctionOperators.SessionsFilter,
		},
	],
	[PropertyTypes.SessionGeolocation]: [
		{
			key: CustomFunctionOperators.SessionsFilter,
			label: Liferay.Language.get('is').toLowerCase(),
			name: CustomFunctionOperators.SessionsFilter,
		},
	],
	[PropertyTypes.SessionNumber]: [
		{
			key: CustomFunctionOperators.SessionsFilter,
			label: Liferay.Language.get('is').toLowerCase(),
			name: CustomFunctionOperators.SessionsFilter,
		},
	],
	[PropertyTypes.SessionText]: [
		{
			key: CustomFunctionOperators.SessionsFilter,
			label: Liferay.Language.get('is').toLowerCase(),
			name: CustomFunctionOperators.SessionsFilter,
		},
	],
	[PropertyTypes.SessionUtmParameter]: [
		{
			key: CustomFunctionOperators.SessionsFilter,
			label: Liferay.Language.get('is').toLowerCase(),
			name: CustomFunctionOperators.SessionsFilter,
		},
	],
	[PropertyTypes.Text]: [
		{
			key: RelationalOperators.EQ,
			label: Liferay.Language.get('is').toLowerCase(),
			name: RelationalOperators.EQ,
		},
		{
			key: RelationalOperators.NE,
			label: Liferay.Language.get('is-not').toLowerCase(),
			name: RelationalOperators.NE,
		},
		{
			key: FunctionalOperators.Contains,
			label: Liferay.Language.get('contains').toLowerCase(),
			name: FunctionalOperators.Contains,
		},
		{
			key: NotOperators.NotContains,
			label: Liferay.Language.get('does-not-contain').toLowerCase(),
			name: NotOperators.NotContains,
		},
		{
			key: isKnown,
			label: Liferay.Language.get('is-known').toLowerCase(),
			name: DisplayOnlyOperators.IsKnown,
		},
		{
			key: isUnknown,
			label: Liferay.Language.get('is-unknown').toLowerCase(),
			name: DisplayOnlyOperators.IsUnknown,
		},
	],
};

/**
 * The operators a criterion offers for the value comparison itself, which
 * are not the ones its property type maps to in SUPPORTED_OPERATORS_MAP:
 * that entry describes the outer call wrapping the criterion (e.g.
 * "sessions.filter"), while the comparison inside it reuses the plain text
 * operators, or the closed "is"/"is not" pair when the value can only ever
 * be one of a fixed list of options (e.g. Channel).
 */
export const getCustomInputOperators = (type: PropertyTypes) =>
	type === PropertyTypes.SessionChannel
		? SUPPORTED_OPERATORS_MAP[PropertyTypes.SelectText]
		: SUPPORTED_OPERATORS_MAP[PropertyTypes.Text];

/**
 * Session property types whose input carries no date filter conjunction, so
 * their criterion holds a single item and neither the editor nor the
 * criteria card has a time period to show for them.
 */
export const DATELESS_SESSION_PROPERTY_TYPES = [
	PropertyTypes.SessionChannel,
	PropertyTypes.SessionDateTime,
	PropertyTypes.SessionUtmParameter,
];

export const SUPPORTED_PROPERTY_TYPES_MAP = {
	[PropertyTypes.AccountDate]: [CustomFunctionOperators.AccountsFilter],
	[PropertyTypes.AccountNumber]: [CustomFunctionOperators.AccountsFilter],
	[PropertyTypes.AccountSelectText]: [
		CustomFunctionOperators.AccountsFilter,
		NotOperators.NotAccountsFilter,
	],
	[PropertyTypes.AccountText]: [CustomFunctionOperators.AccountsFilter],
	[PropertyTypes.Behavior]: [
		CustomFunctionOperators.ActivitiesFilterByCount,
		NotOperators.NotActivitiesFilterByCount,
	],
	[PropertyTypes.Boolean]: [RelationalOperators.EQ],
	[PropertyTypes.Tag]: [
		CustomFunctionOperators.TagsFilter,
		NotOperators.NotTagsFilter,
	],
	[PropertyTypes.Vocabulary]: [
		CustomFunctionOperators.VocabulariesFilter,
		NotOperators.NotVocabulariesFilter,
	],
	[PropertyTypes.Date]: [
		RelationalOperators.EQ,
		RelationalOperators.GE,
		RelationalOperators.GT,
		RelationalOperators.LE,
		RelationalOperators.LT,
		RelationalOperators.NE,
	],
	[PropertyTypes.DateTime]: [
		RelationalOperators.EQ,
		RelationalOperators.GE,
		RelationalOperators.GT,
		RelationalOperators.LE,
		RelationalOperators.LT,
		RelationalOperators.NE,
	],
	[PropertyTypes.Duration]: [RelationalOperators.GT, RelationalOperators.LT],
	[PropertyTypes.Event]: [
		CustomFunctionOperators.EventsFilterByCount,
		NotOperators.NotActivitiesFilterByCount,
	],
	[PropertyTypes.Interest]: [CustomFunctionOperators.InterestsFilter],
	[PropertyTypes.Number]: [
		RelationalOperators.EQ,
		RelationalOperators.GE,
		RelationalOperators.GT,
		RelationalOperators.LE,
		RelationalOperators.LT,
		RelationalOperators.NE,
	],
	[PropertyTypes.SessionDateTime]: [PropertyTypes.SessionDateTime],
	[PropertyTypes.SessionNumber]: [CustomFunctionOperators.SessionsFilter],
	[PropertyTypes.SessionText]: [CustomFunctionOperators.SessionsFilter],
	[PropertyTypes.Text]: [
		RelationalOperators.EQ,
		RelationalOperators.NE,
		FunctionalOperators.Contains,
		NotOperators.NotContains,
		DisplayOnlyOperators.IsKnown,
		DisplayOnlyOperators.IsUnknown,
	],
};

/**
 * Values for criteria row inputs.
 */

export const STRING_OPTIONS = [
	FunctionalOperators.Contains,
	NotOperators.NotContains,
	RelationalOperators.EQ,
	RelationalOperators.NE,
];

export const STRING_OPERATOR_LABELS_MAP = {
	[FunctionalOperators.Contains]:
		Liferay.Language.get('contains').toLowerCase(),
	[NotOperators.NotContains]:
		Liferay.Language.get('does-not-contain').toLowerCase(),
	[RelationalOperators.EQ]: Liferay.Language.get('is').toLowerCase(),
	[RelationalOperators.NE]: Liferay.Language.get('is-not').toLowerCase(),
};

export const BOOLEAN_OPTIONS = [
	{
		label: Liferay.Language.get('true'),
		value: 'true',
	},
	{
		label: Liferay.Language.get('false'),
		value: 'false',
	},
];

export const INTEREST_BOOLEAN_OPTIONS = [
	{
		label: Liferay.Language.get('is').toLowerCase(),
		value: 'true',
	},
	{
		label: Liferay.Language.get('is-not').toLowerCase(),
		value: 'false',
	},
];

export const OCCURENCE_OPTIONS = [
	{
		key: RelationalOperators.GE,
		label: Liferay.Language.get('at-least').toLowerCase(),
		value: RelationalOperators.GE,
	},
	{
		key: RelationalOperators.LE,
		label: Liferay.Language.get('at-most').toLowerCase(),
		value: RelationalOperators.LE,
	},
];

export const GEOLOCATION_OPTIONS = [
	{
		label: Liferay.Language.get('was').toLowerCase(),
		value: RelationalOperators.EQ,
	},
	{
		label: Liferay.Language.get('was-not').toLowerCase(),
		value: RelationalOperators.NE,
	},
	{
		label: Liferay.Language.get('contained').toLowerCase(),
		value: FunctionalOperators.Contains,
	},
	{
		label: Liferay.Language.get('did-not-contain').toLowerCase(),
		value: NotOperators.NotContains,
	},
];

export const TIME_CONJUNCTION_OPTIONS = [
	{
		label: Liferay.Language.get('since').toLowerCase(),
		value: SINCE,
	},
	{
		label: Liferay.Language.get('after').toLowerCase(),
		value: RelationalOperators.GT,
	},
	{
		label: Liferay.Language.get('before').toLowerCase(),
		value: RelationalOperators.LT,
	},
	{
		label: Liferay.Language.get('between').toLowerCase(),
		value: FunctionalOperators.Between,
	},
	{
		label: Liferay.Language.get('ever').toLowerCase(),
		value: EVER,
	},
	{
		label: Liferay.Language.get('on').toLowerCase(),
		value: RelationalOperators.EQ,
	},
];

export const ACQUISITION_PARAMETER_PROPERTY_PREFIX = 'context/';
export const ACTIVITY_KEY = 'activityKey';
export const ATTRIBUTE_PROPERTY_PREFIX = 'attribute/';
export const EVENT_KEY = 'eventId';

export const ASSET_TYPE_APPLICATION_ID_MAP: Record<string, string> = {
	blogs: 'Blog',
	'documents-and-media': 'Document',
	forms: 'Form',
	'web-content': 'WebContent',
};

export const EVENT_TYPE_EVENT_ID_MAP: Record<string, Record<string, string>> = {
	comment: {
		blogs: 'commentPosted',
	},
	download: {
		'basic-document': 'documentDownloaded',
		'documents-and-media': 'documentDownloaded',
	},
	impression: {
		blogs: 'blogImpressionMade',
		'web-content': 'webContentImpressionMade',
	},
	submit: {
		forms: 'formSubmitted',
	},
	view: {
		'basic-document': 'documentPreviewed',
		blogs: 'blogViewed',
		'documents-and-media': 'documentPreviewed',
		forms: 'formViewed',
		'web-content': 'webContentViewed',
	},
};

export const ASSET_TYPE_COMPATIBLE_EVENTS_MAP: Record<string, string[]> = {
	any: ['all', 'view', 'download', 'impression', 'submit', 'comment'],
	blogs: ['all', 'view', 'comment', 'impression'],
	'documents-and-media': ['all', 'view', 'download'],
	forms: ['all', 'view', 'submit'],
	'web-content': ['all', 'view', 'impression'],
};

export const APPLICATION_ID_ASSET_TYPE_MAP: Record<string, string> = {
	Blog: 'blogs',
	Document: 'documents-and-media',
	Form: 'forms',
	WebContent: 'web-content',
};

export const EVENT_ID_EVENT_TYPE_MAP: Record<string, string> = {
	blogImpressionMade: 'impression',
	blogViewed: 'view',
	commentPosted: 'comment',
	documentDownloaded: 'download',
	documentPreviewed: 'view',
	formSubmitted: 'submit',
	formViewed: 'view',
	webContentImpressionMade: 'impression',
	webContentViewed: 'view',
};

export const ALL_APPLICATION_IDS = Array.from(
	new Set(Object.values(ASSET_TYPE_APPLICATION_ID_MAP))
);

export const ALL_EVENT_IDS = Array.from(
	new Set(
		([] as string[]).concat(
			...Object.values(EVENT_TYPE_EVENT_ID_MAP).map((m) =>
				Object.values(m)
			)
		)
	)
);

export const TIME_WINDOW_OPTIONS = [
	{
		label: Liferay.Language.get('hours'),
		value: HOURS,
	},
	{
		label: Liferay.Language.get('days'),
		value: DAYS,
	},
];

export const TIME_PERIOD_OPTIONS = [
	{
		label: Liferay.Language.get('last-24-hours'),
		value: TimeSpans.Last24Hours,
	},
	{
		label: Liferay.Language.get('yesterday'),
		value: TimeSpans.Yesterday,
	},
	{
		label: Liferay.Language.get('last-seven-days'),
		value: TimeSpans.Last7Days,
	},
	{
		label: Liferay.Language.get('last-28-days'),
		value: TimeSpans.Last28Days,
	},
	{
		label: Liferay.Language.get('last-30-days'),
		value: TimeSpans.Last30Days,
	},
	{
		label: Liferay.Language.get('last-90-days'),
		value: TimeSpans.Last90Days,
	},
];

export {TimeSpans};
