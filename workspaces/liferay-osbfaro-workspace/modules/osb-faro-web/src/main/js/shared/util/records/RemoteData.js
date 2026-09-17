import {Record} from 'immutable';

/**
 * Convert an immutable collection of RemoteDatas to a single RemoteData
 * @param {array} remoteDatas - Array of RemoteDatas
 * @returns {Record} - A new Remote data from the combined RemoteDatas
 */
export function remoteDataFromList(remoteDatas) {
	const data = remoteDatas.length ? remoteDatas.map(({data}) => data) : null;

	const error = remoteDatas.some(({error}) => error);

	const loading =
		remoteDatas.some(({loading}) => loading) || !remoteDatas.length;

	return new RemoteData({data, error, loading});
}

export default class RemoteData extends (new Record({
	data: null,
	error: false,
	errorStatus: null,
	loading: true,
})) {
	constructor(params = {}) {
		super(params);
	}
}
