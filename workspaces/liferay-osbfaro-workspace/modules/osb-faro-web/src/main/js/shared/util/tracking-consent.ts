const TRACKING_CONSENT_COOKIE_NAME = 'LIFERAY_PRODUCT_EXPERIENCE_MANAGEMENT';

export enum TrackingConsentValues {
	Accepted = 'true',
	Declined = 'false',
}

const getCookie = (name: string): string | null => {
	const cookies = document.cookie.split('; ');

	for (const cookie of cookies) {
		const [cookieName, cookieValue] = cookie.split('=');

		if (cookieName === name) {
			return decodeURIComponent(cookieValue);
		}
	}

	return null;
};

const removeCookie = (name: string): void => {
	document.cookie = `${name}=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;`;
};

const setCookie = (name: string, value: string): void => {
	const date = new Date();

	date.setTime(date.getTime() + 365 * 24 * 60 * 60 * 1000);

	document.cookie = `${name}=${encodeURIComponent(
		value
	)}; expires=${date.toUTCString()}; path=/`;
};

/**
 * Returns the stored tracking consent decision, or `null` when the user has
 * not made a decision yet. Uses the same cookie name as the DXP tracking
 * script so the consent model stays consistent across products.
 */
export const getTrackingConsent = (): string | null =>
	getCookie(TRACKING_CONSENT_COOKIE_NAME);

export const resetTrackingConsent = (): void =>
	removeCookie(TRACKING_CONSENT_COOKIE_NAME);

export const setTrackingConsent = (accepted: boolean): void =>
	setCookie(
		TRACKING_CONSENT_COOKIE_NAME,
		accepted ? TrackingConsentValues.Accepted : TrackingConsentValues.Declined
	);
