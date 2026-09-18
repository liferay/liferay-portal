import {actionTypes} from '../actions/sidebar';
import {createReducer} from 'redux-toolbox';
import {Map} from 'immutable';

/**
 * Reads the per-user slice, replacing it with an empty Map when missing or
 * when it is still the flat boolean this reducer stored before it tracked
 * per-section expansion. `setIn` throws when an intermediate value isn't a
 * Collection, and browsers may still have that older shape in localStorage.
 */

const getUserState = (state, currentUserId) => {
	const userState = state.get(String(currentUserId));

	return Map.isMap(userState) ? userState : new Map();
};

const actionHandlers = {
	[actionTypes.COLLAPSE_SIDEBAR]: (state, {payload}) => {
		const {collapsed, currentUserId} = payload;

		return state.set(
			String(currentUserId),
			getUserState(state, currentUserId).set('collapsed', collapsed)
		);
	},

	[actionTypes.SET_SIDEBAR_SECTION_EXPANDED]: (state, {payload}) => {
		const {currentUserId, expanded, sectionKey} = payload;

		return state.set(
			String(currentUserId),
			getUserState(state, currentUserId).setIn(
				['expandedSections', sectionKey],
				expanded
			)
		);
	},
};

export default createReducer(new Map(), actionHandlers);
