import {Pendo, TrackingConsentValues} from '../pendo';

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
