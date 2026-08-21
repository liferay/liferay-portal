import {FaroEnv} from './constants';
import {Project, User} from './records';

const TRACKING_CONSENT_COOKIE = 'LIFERAY_PRODUCT_EXPERIENCE_MANAGEMENT';

export enum TrackingConsentValues {
	Accepted = 'true',
	Declined = 'false',
}

export class Pendo {

	/**
	 * Returns the stored tracking consent decision, or `null` when the user
	 * has not made a decision yet. Uses the same cookie name as the DXP
	 * tracking script so the consent model stays consistent across products.
	 */
	getUserConsent(): string | null {
		return (
			Liferay.Util.Cookie.get(
				TRACKING_CONSENT_COOKIE,
				Liferay.Util.Cookie.TYPES.NECESSARY
			) ?? null
		);
	}

	initialize({currentUser, project}: {currentUser: User; project: Project}) {
		this.injectAgent();

		if (typeof pendo === 'undefined') {
			return;
		}

		const data = {
			account: {
				...(project.corpProjectUuid && {
					id: project.corpProjectUuid,
				}),
				name: project.corpProjectName,
				planLevel: project.faroSubscription.get('name'),
			},
			visitor: {
				email: currentUser.emailAddress,
				full_name: currentUser.name,
				id: currentUser.id,
				role: currentUser.roleName,
			},
		};

		if (pendo?.isReady?.()) {
			return pendo.identify(data);
		}

		return pendo.initialize(data);
	}

	setUserConsent(accepted: boolean) {
		Liferay.Util.Cookie.set(
			TRACKING_CONSENT_COOKIE,
			accepted
				? TrackingConsentValues.Accepted
				: TrackingConsentValues.Declined,
			Liferay.Util.Cookie.TYPES.NECESSARY
		);
	}

	/**
	 * Appends the agent loader on demand. On the page load where the user
	 * accepts tracking, external-scripts.js already skipped the loader (no
	 * consent existed when the page was rendered), so it must be injected
	 * here for tracking to start without a reload.
	 */
	private injectAgent() {
		if (typeof pendo !== 'undefined' || FARO_ENV !== FaroEnv.Production) {
			return;
		}

		const script = document.createElement('script');

		script.innerHTML = this.script;

		const nonce = (Liferay as unknown as {CSP?: {nonce?: string}}).CSP
			?.nonce;

		if (nonce) {
			script.setAttribute('nonce', nonce);
		}

		document.body.appendChild(script);
	}

	get script() {
		if (FARO_ENV === FaroEnv.Production) {

			// Before the user accepts tracking the page must not request
			// anything from pendo.io, not even the agent script: an empty
			// entry is filtered out by external-scripts.js.

			if (this.getUserConsent() !== TrackingConsentValues.Accepted) {
				return '';
			}

			return `(function(apiKey){
			(function(p,e,n,d,o){var v,w,x,y,z;o=p[d]=p[d]||{};o._q=o._q||[];
				v=['initialize','identify','updateOptions','pageLoad','track'];for(w=0,x=v.length;w<x;++w)(function(m){
					o[m]=o[m]||function(){o._q[m===v[0]?'unshift':'push']([m].concat([].slice.call(arguments,0)));};})(v[w]);
				y=e.createElement(n);y.async=!0;y.src='https://cdn.pendo.io/agent/static/'+apiKey+'/pendo.js';
				z=e.getElementsByTagName(n)[0];z.parentNode.insertBefore(y,z);})(window,document,'script','pendo');
			})('${FARO_PENDO_API_KEY}')`;
		}

		return '(function(){window.pendo = {identify: () => {}, initialize: () => {}, isReady: () => {}}})()';
	}
}
