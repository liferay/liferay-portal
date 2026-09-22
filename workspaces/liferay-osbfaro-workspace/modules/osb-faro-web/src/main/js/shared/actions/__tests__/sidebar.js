import * as actions from '../sidebar';
import {isFSA} from 'flux-standard-action';

describe('Sidebar Actions', () => {
	describe('collapseSidebar', () => {
		it('should return an action', () => {
			const action = actions.collapseSidebar();

			expect(isFSA(action)).toBe(true);
			expect(action.type).toBe(actions.actionTypes.COLLAPSE_SIDEBAR);
		});
	});
});
