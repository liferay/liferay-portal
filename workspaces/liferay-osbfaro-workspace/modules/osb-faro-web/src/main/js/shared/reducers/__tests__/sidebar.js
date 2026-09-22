import reducer from '../sidebar';
import {actionTypes} from '../../actions/sidebar';
import {Map} from 'immutable';

const collapse = (payload) => ({
	payload,
	type: actionTypes.COLLAPSE_SIDEBAR,
});

describe('Sidebar Reducer', () => {
	it('should be a function', () => {
		expect(reducer).toBeInstanceOf(Function);
	});

	it('should collapse the sidebar', () => {
		const state = reducer(
			new Map(),
			collapse({collapsed: true, currentUserId: '23'})
		);

		expect(state).toEqual(new Map({23: new Map({collapsed: true})}));
	});

	it('should collapse a single section when given a sectionKey', () => {
		const state = reducer(
			new Map(),
			collapse({
				collapsed: true,
				currentUserId: '23',
				sectionKey: 'touchpoints',
			})
		);

		expect(state).toEqual(
			new Map({
				23: new Map({
					collapsedSections: new Map({touchpoints: true}),
				}),
			})
		);
	});

	it('should keep the sidebar and its sections independent', () => {
		const state = reducer(
			new Map({23: new Map({collapsed: true})}),
			collapse({
				collapsed: true,
				currentUserId: '23',
				sectionKey: 'touchpoints',
			})
		);

		expect(state).toEqual(
			new Map({
				23: new Map({
					collapsed: true,
					collapsedSections: new Map({touchpoints: true}),
				}),
			})
		);
	});

	it('should handle a stored value that predates per-section state', () => {

		// Older sessions may still have the flat `{userId: collapsed}` shape
		// this reducer stored before it tracked sections, which `setIn`
		// rejects as an intermediate value.

		const initialState = new Map({23: true});

		expect(() =>
			reducer(
				initialState,
				collapse({
					collapsed: true,
					currentUserId: '23',
					sectionKey: 'touchpoints',
				})
			)
		).not.toThrow();

		expect(
			reducer(
				initialState,
				collapse({collapsed: false, currentUserId: '23'})
			)
		).toEqual(new Map({23: new Map({collapsed: false})}));
	});
});
