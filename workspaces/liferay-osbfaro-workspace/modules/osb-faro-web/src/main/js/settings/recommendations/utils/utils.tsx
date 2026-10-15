import {
	JobRunDataPeriods,
	JobRunFrequencies,
	JobRunStatuses,
	JobStatuses,
	JobTypes,
	RangeKeyTimeRanges,
} from 'shared/util/constants';

export const CANONICAL_URL = 'canonicalUrl';
export const DESCRIPTION = 'description';
export const KEYWORDS = 'keywords';
export const TITLE = 'title';
export const URL = 'url';

export const METADATA_TAGS = [CANONICAL_URL, DESCRIPTION, KEYWORDS, TITLE, URL];

export const EXCLUDE = 'excludeFilter';
export const INCLUDE = 'includeFilter';

export type Filter = {
	id: string;
	name: string;
	value: string;
};

export type JobParameter = {
	name: string;
	value: string;
};

export type JobProperty = {
	filter: string;
	negate: boolean;
};

export type Job = {
	id: string;
	name: string;
	parameters: JobParameter[];
	runDataPeriod: JobRunDataPeriods;
	runDate: string;
	runFrequency: JobRunFrequencies;
	status: JobStatuses;
	type: JobTypes;
};

export const JOB_RUN_STATUSES_DISPLAY_MAP = {
	[JobRunStatuses.Completed]: 'success',
	[JobRunStatuses.Failed]: 'danger',
	[JobRunStatuses.Published]: 'secondary',
	[JobRunStatuses.Running]: 'warning',
};

export const JOB_RUN_STATUSES_LABEL_MAP = {
	[JobRunStatuses.Completed]: Liferay.Language.get('completed'),
	[JobRunStatuses.Failed]: Liferay.Language.get('failed'),
	[JobRunStatuses.Published]: Liferay.Language.get('live-version'),
	[JobRunStatuses.Running]: Liferay.Language.get('training'),
};

export const JOB_STATUSES_DISPLAY_MAP = {
	[JobStatuses.Failed]: 'danger',
	[JobStatuses.Pending]: 'secondary',
	[JobStatuses.Ready]: 'success',
	[JobStatuses.Running]: 'warning',
	[JobStatuses.Scheduled]: 'info',
};

export const JOB_STATUSES_LABEL_MAP = {
	[JobStatuses.Failed]: Liferay.Language.get('failed'),
	[JobStatuses.Pending]: Liferay.Language.get('pending'),
	[JobStatuses.Ready]: Liferay.Language.get('ready'),
	[JobStatuses.Running]: Liferay.Language.get('training'),
	[JobStatuses.Scheduled]: Liferay.Language.get('scheduled'),
};

export const JOB_RUN_FREQUENCIES_LABEL_MAP = {
	[JobRunFrequencies.Every7Days]: Liferay.Language.get('every-7-days'),
	[JobRunFrequencies.Every14Days]: Liferay.Language.get('every-14-days'),
	[JobRunFrequencies.Every30Days]: Liferay.Language.get('every-30-days'),
	[JobRunFrequencies.Manual]: Liferay.Language.get('run-manually'),
};

export const JOB_RUN_FREQUENCIES_LIST = [
	{
		name: Liferay.Language.get('run-manually'),
		value: JobRunFrequencies.Manual,
	},
	{
		name: Liferay.Language.get('every-7-days'),
		value: JobRunFrequencies.Every7Days,
	},
	{
		name: Liferay.Language.get('every-14-days'),
		value: JobRunFrequencies.Every14Days,
	},
	{
		name: Liferay.Language.get('every-30-days'),
		value: JobRunFrequencies.Every30Days,
	},
];

export const JOB_RUN_DATA_PERIODS_LABEL_MAP = {
	[JobRunDataPeriods.Last7Days]: Liferay.Language.get('last-7-days'),
	[JobRunDataPeriods.Last30Days]: Liferay.Language.get('last-30-days'),
	[JobRunDataPeriods.Last180Days]: Liferay.Language.get('last-180-days'),
	[JobRunDataPeriods.Last365Days]: Liferay.Language.get('last-year'),
};

export const JOB_RUN_DATA_PERIODS_RANGE_KEY_MAP = {
	[JobRunDataPeriods.Last7Days]: RangeKeyTimeRanges.Last7Days,
	[JobRunDataPeriods.Last30Days]: RangeKeyTimeRanges.Last30Days,
	[JobRunDataPeriods.Last180Days]: RangeKeyTimeRanges.Last180Days,
	[JobRunDataPeriods.Last365Days]: RangeKeyTimeRanges.LastYear,
};

export const JOB_RUN_DATA_PERIODS_LIST = [
	{
		name: Liferay.Language.get('last-7-days'),
		value: JobRunDataPeriods.Last7Days,
	},
	{
		name: Liferay.Language.get('last-30-days'),
		value: JobRunDataPeriods.Last30Days,
	},
	{
		name: Liferay.Language.get('last-180-days'),
		value: JobRunDataPeriods.Last180Days,
	},
	{
		name: Liferay.Language.get('last-year'),
		value: JobRunDataPeriods.Last365Days,
	},
];

export const JOB_TYPES_LABEL_MAP = {
	[JobTypes.ItemSimilarity]: Liferay.Language.get('item-similarity'),
};

export const RULE_NAME_LABEL_MAP = {
	excludeFilter: Liferay.Language.get('exclude'),
	includeFilter: Liferay.Language.get('include'),
};

export const getPropertiesFromItems = (itemFilters: Filter[]): JobProperty[] =>
	itemFilters.map(({name, value}) => ({
		filter: value,
		negate: name === EXCLUDE,
	}));

export const getFilterValueBreakdown = (
	filter: string
): {exactMatchSign: string; metadataTag: string; rule: string} => {
	const [rule, exactMatchSign, metadataTag] = filter
		.split(/\s*([=~])\s*/, 3)
		.reverse();

	return {exactMatchSign, metadataTag, rule};
};
