import {Entity} from '../types';
import {getEntityDisplay} from '../getEntityDisplay';

describe('getEntityDisplay', () => {
	it('returns Accounts label and briefcase icon', () => {
		expect(getEntityDisplay(Entity.Accounts)).toEqual({
			icon: 'briefcase',
			label: 'Accounts'
		});
	});

	it('returns Events label and click icon', () => {
		expect(getEntityDisplay(Entity.Events)).toEqual({
			icon: 'click',
			label: 'Events'
		});
	});

	it('returns Individuals label and user icon', () => {
		expect(getEntityDisplay(Entity.Individuals)).toEqual({
			icon: 'user',
			label: 'Individuals'
		});
	});

	it('returns Sites label and sites icon', () => {
		expect(getEntityDisplay(Entity.Sites)).toEqual({
			icon: 'sites',
			label: 'Sites'
		});
	});

	it('returns Users label and users icon', () => {
		expect(getEntityDisplay(Entity.Users)).toEqual({
			icon: 'users',
			label: 'Users'
		});
	});
});
