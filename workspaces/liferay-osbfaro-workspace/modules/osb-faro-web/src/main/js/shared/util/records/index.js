import {INDIVIDUALS, PAGEVIEWS} from 'shared/util/subscriptions';
import {List, Map, Record} from 'immutable';

export {default as Account} from './Account';
export {default as CustomValue} from './CustomValue';
export {default as DataSource} from './DataSource';
export {default as DistributionTab} from './DistributionTab';
export {default as Individual} from './Individual';
export {default as User} from './User';
export {default as OrderParams} from './OrderParams';
export {default as Project} from './Project';
export {default as Property} from './Property';
export {default as PropertyGroup} from './PropertyGroup';
export {default as PropertySubgroup} from './PropertySubgroup';
export {default as RemoteData, remoteDataFromList} from './RemoteData';
export {default as Segment} from './Segment';
export {default as TimeZone} from './TimeZone';

export const Changeset = new Record({
	added: new Map(),
	removed: new Map(),
});

export const Distribution = new Record({
	data: new List(),
	error: false,
	fieldMappingFieldName: null,
	loading: true,
});

export const Metric = new Record({
	count: 0,
	limit: 0,
	status: 0,
});

export const Plan = new Record({
	addOns: new Map({
		[INDIVIDUALS]: new Map(),
		[PAGEVIEWS]: new Map(),
	}),
	endDate: Date.now(),
	metrics: new Map({
		[INDIVIDUALS]: new Metric(),
		[PAGEVIEWS]: new Metric(),
	}),
	name: '',
	startDate: Date.now(),
});

export const SearchResults = new Record({
	disableSearch: false,
	items: new List(),
	total: 0,
});
