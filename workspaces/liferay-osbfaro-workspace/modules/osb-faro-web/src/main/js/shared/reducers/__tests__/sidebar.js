import reducer from '../sidebar';
import {actionTypes} from '../../actions/sidebar';
import {Map} from 'immutable';

describe('Sidebar Reducer', () => {
	it('should be a function', () => {
		expect(reducer).toBeInstanceOf(Function);
	});

	it(`should handle ${actionTypes.COLLAPSE_SIDEBAR}`, () => {
		const currentUserId = '23';
		const collapsed = true;

		const action = {
			payload: {
				collapsed,
				currentUserId,
			},
			type: actionTypes.COLLAPSE_SIDEBAR,
		};

		const state = reducer(new Map(), action);

		expect(state).toEqual(
			new Map({
				[currentUserId]: new Map({collapsed}),
			})
		);
	});

	it(`should handle ${actionTypes.COLLAPSE_SIDEBAR} without clobbering an existing expandedSections entry`, () => {
		const currentUserId = '23';

		const initialState = new Map({
			[currentUserId]: new Map({
				collapsed: false,
				expandedSections: new Map({touchpoints: false}),
			}),
		});

		const action = {
			payload: {
				collapsed: true,
				currentUserId,
			},
			type: actionTypes.COLLAPSE_SIDEBAR,
		};

		const state = reducer(initialState, action);

		expect(state).toEqual(
			new Map({
				[currentUserId]: new Map({
					collapsed: true,
					expandedSections: new Map({touchpoints: false}),
				}),
			})
		);
	});

	it(`should handle ${actionTypes.COLLAPSE_SIDEBAR} when the stored value predates per-section state`, () => {
		const currentUserId = '23';

		// Older sessions may still have the flat `{userId: collapsed}` shape
		// this reducer stored before it tracked per-section expansion.

		const initialState = new Map({[currentUserId]: true});

		const action = {
			payload: {
				collapsed: false,
				currentUserId,
			},
			type: actionTypes.COLLAPSE_SIDEBAR,
		};

		const state = reducer(initialState, action);

		expect(state).toEqual(
			new Map({
				[currentUserId]: new Map({collapsed: false}),
			})
		);
	});

	it(`should handle ${actionTypes.SET_SIDEBAR_SECTION_EXPANDED}`, () => {
		const currentUserId = '23';
		const expanded = false;
		const sectionKey = 'touchpoints';

		const action = {
			payload: {
				currentUserId,
				expanded,
				sectionKey,
			},
			type: actionTypes.SET_SIDEBAR_SECTION_EXPANDED,
		};

		const state = reducer(new Map(), action);

		expect(state).toEqual(
			new Map({
				[currentUserId]: new Map({
					expandedSections: new Map({[sectionKey]: expanded}),
				}),
			})
		);
	});

	it(`should handle ${actionTypes.SET_SIDEBAR_SECTION_EXPANDED} without clobbering an existing collapsed entry`, () => {
		const currentUserId = '23';

		const initialState = new Map({
			[currentUserId]: new Map({collapsed: true}),
		});

		const action = {
			payload: {
				currentUserId,
				expanded: false,
				sectionKey: 'touchpoints',
			},
			type: actionTypes.SET_SIDEBAR_SECTION_EXPANDED,
		};

		const state = reducer(initialState, action);

		expect(state).toEqual(
			new Map({
				[currentUserId]: new Map({
					collapsed: true,
					expandedSections: new Map({touchpoints: false}),
				}),
			})
		);
	});

	it(`should handle ${actionTypes.SET_SIDEBAR_SECTION_EXPANDED} when the stored value predates per-section state`, () => {
		const currentUserId = '23';

		// Older sessions may still have the flat `{userId: collapsed}` shape
		// this reducer stored before it tracked per-section expansion.
		// `setIn` throws when an intermediate value isn't a Collection, so
		// this must not be dispatched against the raw boolean as-is.

		const initialState = new Map({[currentUserId]: true});

		const action = {
			payload: {
				currentUserId,
				expanded: false,
				sectionKey: 'touchpoints',
			},
			type: actionTypes.SET_SIDEBAR_SECTION_EXPANDED,
		};

		expect(() => reducer(initialState, action)).not.toThrow();

		const state = reducer(initialState, action);

		expect(state).toEqual(
			new Map({
				[currentUserId]: new Map({
					expandedSections: new Map({touchpoints: false}),
				}),
			})
		);
	});
});
