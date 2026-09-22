import {actionTypes} from '../actions/sidebar';
import {createReducer} from 'redux-toolbox';
import {Map} from 'immutable';

/**
 * Reads the per-user slice, replacing it with an empty Map when missing or
 * when it is still the flat boolean this reducer stored before it tracked
 * sections. `setIn` throws when an intermediate value isn't a Collection,
 * and browsers may still have that older shape in localStorage.
 */

const getUserState = (state, currentUserId) => {
	const userState = state.get(String(currentUserId));

	return Map.isMap(userState) ? userState : new Map();
};

const actionHandlers = {
	[actionTypes.COLLAPSE_SIDEBAR]: (state, {payload}) => {
		const {collapsed, currentUserId, sectionKey} = payload;

		const userState = getUserState(state, currentUserId);

		return state.set(
			String(currentUserId),
			sectionKey
				? userState.setIn(['collapsedSections', sectionKey], collapsed)
				: userState.set('collapsed', collapsed)
		);
	},
};

export default createReducer(new Map(), actionHandlers);
