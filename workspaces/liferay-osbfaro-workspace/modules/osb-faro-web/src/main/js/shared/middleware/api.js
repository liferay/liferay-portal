import sendRequest from 'shared/util/request';
import {get, isNil} from 'lodash';

export const CALL_API = 'CALL_API';

export function toAction(type, ...objs) {
	const action = Object.assign(...objs, {type});

	delete action.meta[CALL_API];

	return action;
}

export default () => (next) => (action) => {
	const request = get(action, ['meta', CALL_API]);

	if (isNil(request)) {
		return next(action);
	}

	const {data, requestFn, types} = request;

	const [requestType, successType, failureType] = types;

	next(toAction(requestType, action));

	const retVal = requestFn ? requestFn(data) : sendRequest(request);

	return retVal.then(
		(payload) => {
			next(
				toAction(successType, action, {
					meta: {
						...action.meta,
						schema: request.schema,
					},
					payload,
				})
			);

			return {payload};
		},
		(error) => {
			next(
				toAction(failureType, action, {
					error: true,
					errorStatus: get(error, ['status'], null),
				})
			);

			throw error;
		}
	);
};
