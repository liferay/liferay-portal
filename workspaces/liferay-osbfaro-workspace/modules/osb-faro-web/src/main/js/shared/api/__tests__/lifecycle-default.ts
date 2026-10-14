import {getDefaultLifecycle} from '../lifecycle';

describe('getDefaultLifecycle', () => {
	it('returns the lifecycle flagged as the default', () => {
		expect(
			getDefaultLifecycle([
				{default: false, id: '1'},
				{default: true, id: '2'},
			])
		).toEqual({default: true, id: '2'});
	});

	it('falls back to the first lifecycle while none is flagged', () => {
		expect(getDefaultLifecycle([{id: '1'}, {id: '2'}])).toEqual({id: '1'});
	});

	it('returns nothing without lifecycles', () => {
		expect(getDefaultLifecycle([])).toBeUndefined();
	});
});
