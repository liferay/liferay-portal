import {FaroEnv} from './constants';
import {Project, User} from './records';

// LPD-101615: Pendo is temporarily disabled because Analytics Cloud has no
// user consent flow yet. Tracking users without consent is not compliant
// with our own consent model (the DXP tracking script asks first) nor with
// consent regulations. The guard disables both the identify payload and the
// pendo.js agent request (`script` is injected by external-scripts.js on
// every page load, so it must return the inert stub too). Remove the
// constant once the consent banner ships.

const PENDO_TRACKING_ENABLED: boolean = false;

export class Pendo {
	initialize({currentUser, project}: {currentUser: User; project: Project}) {
		if (!PENDO_TRACKING_ENABLED) {
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

	get script() {
		if (PENDO_TRACKING_ENABLED && FARO_ENV === FaroEnv.Production) {
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
