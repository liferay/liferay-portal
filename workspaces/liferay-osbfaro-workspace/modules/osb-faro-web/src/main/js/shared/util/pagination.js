import FaroConstants, {OrderByDirections} from 'shared/util/constants';
import PropTypes from 'prop-types';
import {ACCOUNTS, INDIVIDUALS, SEGMENTS, USERS} from 'shared/util/router';
import {get} from 'lodash';
import {Map, OrderedMap} from 'immutable';
import {OrderParams} from 'shared/util/records';

const {
	cur: defaultCur,
	delta: defaultDelta,
	orderAscending,
	orderDescending,
} = FaroConstants.pagination;

export const ABANDONMENTS_METRIC = 'abandonmentsMetric';
export const ACCOUNT_NAME = 'accountName';
export const ACCOUNT_TYPE = 'accountType';
export const ACTIVITIES_COUNT = 'activitiesCount';
export const AVG_TIME_ON_PAGE_METRIC = 'avgTimeOnPageMetric';
export const BOUNCE_RATE_METRIC = 'bounceRateMetric';
export const COMMENTS_METRIC = 'commentsMetric';
export const COMPLETION_TIME_METRIC = 'completionTimeMetric';
export const COUNT = 'count';
export const COUNTRY = 'country';
export const CREATE_DATE = 'createDate';
export const CREATE_TIME = 'createTime';
export const CREATED_BY_USER_NAME = 'createdByUserName';
export const DATE_ADDED = 'dateAdded';
export const DATE_CHANGED = 'dateChanged';
export const DATE_CREATED = 'dateCreated';
export const DATE_FIRST = 'dateFirst';
export const DATE_RECORDED = 'dateRecorded';
export const DISPLAY_NAME = 'displayName';
export const DOWNLOADS_METRIC = 'downloadsMetric';
export const EMAIL_ADDRESS = 'emailAddress';
export const ENTRANCES_METRIC = 'entrancesMetric';
export const EXIT_RATE_METRIC = 'exitRateMetric';
export const FAMILY_NAME = 'familyName';
export const FIRST_ACTIVITY_DATE = 'firstActivityDate';
export const FIRST_NAME = 'firstName';
export const GIVEN_NAME = 'givenName';
export const ID = 'id';
export const IMPRESSIONS_METRIC = 'impressionMadeMetric';
export const INDIVIDUAL_COUNT = 'individualCount';
export const INDIVIDUAL_EMAIL = 'individualEmail';
export const INDIVIDUAL_NAME = 'individualName';
export const INDIVIDUAL_TYPE = 'individualType';
export const INTERESTS = 'interests';
export const JOB_TITLE = 'jobTitle';
export const KEYWORD = 'keyword';
export const LAST_ACTIVITY_DATE = 'lastActivityDate';
export const LAST_NAME = 'lastName';
export const LOCATION = 'location';
export const MODIFIED_DATE = 'modifiedDate';
export const NAME = 'name';
export const OPERATION = 'operation';
export const PROVIDER_TYPE = 'providerType';
export const RATINGS_METRIC = 'ratingsMetric';
export const READING_TIME_METRIC = 'readingTimeMetric';
export const ROLE_NAME = 'roleName';
export const SCORE = 'score';
export const START_TIME = 'startTime';
export const STATUS = 'status';
export const SUBMISSIONS_METRIC = 'submissionsMetric';
export const TITLE = 'title';
export const TOTAL_ACTIVITIES = 'totalActivities';
export const UNIQUE_VISITS_COUNT = 'uniqueVisitsCount';
export const URL = 'url';
export const USER_NAME = 'author/name';
export const VIEWS_METRIC = 'viewsMetric';
export const VISITORS_METRIC = 'visitorsMetric';

const INVERTED_SORT_FIELDS = [
	ABANDONMENTS_METRIC,
	ACTIVITIES_COUNT,
	AVG_TIME_ON_PAGE_METRIC,
	BOUNCE_RATE_METRIC,
	COMMENTS_METRIC,
	COMPLETION_TIME_METRIC,
	COUNT,
	CREATE_DATE,
	CREATE_TIME,
	DATE_ADDED,
	DATE_CHANGED,
	DATE_CREATED,
	DATE_FIRST,
	DATE_RECORDED,
	DOWNLOADS_METRIC,
	ENTRANCES_METRIC,
	EXIT_RATE_METRIC,
	IMPRESSIONS_METRIC,
	INDIVIDUAL_COUNT,
	LAST_ACTIVITY_DATE,
	MODIFIED_DATE,
	RATINGS_METRIC,
	READING_TIME_METRIC,
	SCORE,
	START_TIME,
	SUBMISSIONS_METRIC,
	TOTAL_ACTIVITIES,
	UNIQUE_VISITS_COUNT,
	VIEWS_METRIC,
	VISITORS_METRIC,
];

export const paginationDefaults = {
	delta: defaultDelta,
	filterBy: new Map(),
	page: defaultCur,
	query: '',
};

export const paginationConfig = {
	delta: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
	filterBy: PropTypes.instanceOf(Map),
	orderIOMap: PropTypes.instanceOf(OrderedMap),
	page: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
	query: PropTypes.string,
};

export const ACCESSOR_TO_FIELD_MAP = {
	['properties.accountType']: ACCOUNT_TYPE,
	['properties.individualCount']: INDIVIDUAL_COUNT,
	['properties.interests']: INTERESTS,
	['properties.jobTitle']: JOB_TITLE,
	['properties.location']: LOCATION,
	['properties.totalActivities']: TOTAL_ACTIVITIES,
	userName: USER_NAME,
	viewCount: UNIQUE_VISITS_COUNT,
};

export const getFieldNameFromAccessor = (accessor = '') =>
	get(ACCESSOR_TO_FIELD_MAP, [accessor], accessor);

const SYSTEM_FIELDS = [
	ACTIVITIES_COUNT,
	DATE_CHANGED,
	DATE_CREATED,
	DATE_FIRST,
	INDIVIDUAL_COUNT,
	INDIVIDUAL_NAME,
	INDIVIDUAL_EMAIL,
	LAST_ACTIVITY_DATE,
	OPERATION,
	USER_NAME,
];

export function buildOrderByFields({field, sortOrder}, entityType) {
	if (entityType === INDIVIDUALS && field === NAME) {
		return [GIVEN_NAME, FAMILY_NAME].map((columnAccessor) =>
			createOrderByField(columnAccessor, sortOrder)
		);
	}
	else if (entityType === SEGMENTS && field === NAME) {
		return [createOrderByField(NAME, sortOrder, true)];
	}
	else if (entityType === ACCOUNTS && field === NAME) {
		return [createOrderByField(ACCOUNT_NAME, sortOrder)];
	}
	else if (entityType === USERS && field === NAME) {
		return [FIRST_NAME, LAST_NAME].map((columnAccessor) =>
			createOrderByField(columnAccessor, sortOrder)
		);
	}
	else if (entityType === INTERESTS && field === NAME) {
		return [createOrderByField(field, sortOrder, true)];
	}
	else {
		return [createOrderByField(field, sortOrder)];
	}
}

const ORDER_BY_DIRECTIONS_MAP = {
	[OrderByDirections.Ascending]: orderAscending,
	[OrderByDirections.Descending]: orderDescending,
};

export function createOrderByField(field, sortOrder, system) {
	const orderBy = ORDER_BY_DIRECTIONS_MAP[sortOrder] || sortOrder;

	return {
		fieldName: field,
		orderBy,
		system: system || SYSTEM_FIELDS.includes(field),
	};
}

export const getDefaultSortOrder = (fieldName) =>
	INVERTED_SORT_FIELDS.includes(fieldName)
		? OrderByDirections.Descending
		: OrderByDirections.Ascending;

export const invertSortOrder = (currentSortOrder) => {
	if (currentSortOrder) {
		return currentSortOrder === OrderByDirections.Ascending
			? OrderByDirections.Descending
			: OrderByDirections.Ascending;
	}
	else {
		return OrderByDirections.Ascending;
	}
};

export const createOrderIOMap = (field, sortOrder) =>
	OrderedMap({
		[field]: new OrderParams({
			field,
			sortOrder: sortOrder || getDefaultSortOrder(field),
		}),
	});

export const getSortFromOrderIOMap = (orderIOMap) => {
	if (orderIOMap) {
		const {field, sortOrder} = orderIOMap.first();

		return {
			column: field,
			type: sortOrder,
		};
	}
};

export const getGraphQLVariablesFromPagination = ({
	delta,
	orderIOMap,
	page,
	query,
}) => ({
	keywords: query,
	size: delta,
	sort: getSortFromOrderIOMap(orderIOMap),
	start: (page - 1) * delta,
});
