import FaroConstants from 'shared/util/constants';
import sendRequest from 'shared/util/request';
import {escapeSingleQuotes} from 'segment/segment-editor/dynamic/utils/odata';
import {RESTParams} from 'shared/types';

const {cur: defaultCur, delta: defaultDelta} = FaroConstants.pagination;

export interface IAcquisitionParameter {
	fieldName: string;
	name: string;
	standard: boolean;
	type: string;
}

interface IFetchAcquisitionParameters {
	channelId?: string;
	groupId: string;
}

interface IFetchFieldValues extends RESTParams {
	channelId?: string;
	fieldName?: string;
	filter?: string;
}

/**
 * Lists a channel's default and custom UTM acquisition parameters (e.g.
 * utm_source, or a custom utm_cid), each already carrying the field name to
 * filter sessions by. See AcquisitionParameterUtil in osb-asah-common and
 * SessionFaroController's "/acquisition_parameters" route (underscore, not
 * hyphen, unlike every other route here).
 */
export const fetchAcquisitionParameters = ({
	channelId = '',
	groupId,
}: IFetchAcquisitionParameters): Promise<{
	items: IAcquisitionParameter[];
	total: number;
}> =>
	sendRequest({
		data: {
			channelId,
		},
		method: 'GET',
		path: `contacts/${groupId}/session/acquisition_parameters`,
	});

export const fetchFieldValues = ({
	channelId = '',
	delta = defaultDelta,
	fieldName,
	filter,
	groupId,
	page = defaultCur,
	query,
}: IFetchFieldValues): Promise<{
	disableSearch: boolean;
	items: string[];
	total: number;
}> =>
	sendRequest({
		data: {
			channelId,
			cur: page,
			delta,
			fieldName,
			filter,
			query: query ? escapeSingleQuotes(query) : query,
		},
		method: 'GET',
		path: `contacts/${groupId}/session/values`,
	});
