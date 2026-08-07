import {
	getTrackingConsent,
	resetTrackingConsent,
	setTrackingConsent,
	TrackingConsentValues,
} from '../tracking-consent';

describe('tracking-consent', () => {
	afterEach(() => {
		resetTrackingConsent();
	});

	it('returns null when the user has not made a decision yet', () => {
		expect(getTrackingConsent()).toBeNull();
	});

	it('stores an accepted decision', () => {
		setTrackingConsent(true);

		expect(getTrackingConsent()).toBe(TrackingConsentValues.Accepted);
	});

	it('stores a declined decision', () => {
		setTrackingConsent(false);

		expect(getTrackingConsent()).toBe(TrackingConsentValues.Declined);
	});

	it('resets the stored decision', () => {
		setTrackingConsent(true);

		resetTrackingConsent();

		expect(getTrackingConsent()).toBeNull();
	});
});
