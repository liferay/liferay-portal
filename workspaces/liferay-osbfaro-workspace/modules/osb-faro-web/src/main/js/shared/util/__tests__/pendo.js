import {Pendo, TrackingConsentValues} from '../pendo';
import {Map} from 'immutable';
import {Project, User} from '../records';

describe('Pendo user consent', () => {
	let cookies;

	beforeAll(() => {
		Liferay.Util = {
			Cookie: {
				get: (name) => cookies[name],
				set: (name, value) => {
					cookies[name] = value;

					return true;
				},
				TYPES: {NECESSARY: 'CONSENT_TYPE_NECESSARY'},
			},
		};
	});

	beforeEach(() => {
		cookies = {};
	});

	it('returns null when the user has not made a decision yet', () => {
		expect(new Pendo().getUserConsent()).toBeNull();
	});

	it('stores an accepted decision', () => {
		const pendo = new Pendo();

		pendo.setUserConsent(true);

		expect(pendo.getUserConsent()).toBe(TrackingConsentValues.Accepted);
	});

	it('stores a declined decision', () => {
		const pendo = new Pendo();

		pendo.setUserConsent(false);

		expect(pendo.getUserConsent()).toBe(TrackingConsentValues.Declined);
	});
});

describe('Pendo account', () => {
	const currentUser = new User({
		emailAddress: 'user@example.com',
		id: 42,
		name: 'Example User',
		roleName: 'Site Member',
	});

	const subscription = {faroSubscription: Map({name: 'Basic'})};

	let initialize;

	beforeEach(() => {
		initialize = jest.fn();

		global.pendo = {initialize, isReady: () => false};
	});

	afterEach(() => {
		delete global.pendo;
	});

	function account(project) {
		new Pendo().initialize({currentUser, project: new Project(project)});

		return initialize.mock.calls[0][0].account;
	}

	it('keeps the corp project for workspaces that still carry one', () => {
		expect(
			account({
				...subscription,
				accountKey: 'ACC-1',
				accountName: 'Account',
				corpProjectName: 'Corp Project',
				corpProjectUuid: 'KOR-1',
				groupId: 123,
				name: 'Workspace',
			})
		).toEqual({id: 'KOR-1', name: 'Corp Project', planLevel: 'Basic'});
	});

	it('falls back to the account when there is no corp project', () => {
		expect(
			account({
				...subscription,
				accountKey: 'ACC-1',
				accountName: 'Account',
				groupId: 123,
				name: 'Workspace',
			})
		).toEqual({id: 'ACC-1', name: 'Account', planLevel: 'Basic'});
	});

	it('falls back to the workspace when there is no account either', () => {
		expect(
			account({...subscription, groupId: 123, name: 'Workspace'})
		).toEqual({id: '123', name: 'Workspace', planLevel: 'Basic'});
	});

	it('identifies the visitor from the current user', () => {
		account({...subscription, groupId: 123, name: 'Workspace'});

		expect(initialize.mock.calls[0][0].visitor).toEqual({
			email: 'user@example.com',
			full_name: 'Example User',
			id: 42,
			role: 'Site Member',
		});
	});
});
