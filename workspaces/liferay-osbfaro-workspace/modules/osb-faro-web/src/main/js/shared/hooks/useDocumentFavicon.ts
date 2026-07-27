import acFaviconURL from '../../../images/favicons/ac_favicon.svg';
import ldpFaviconURL from '../../../images/favicons/ldp_favicon.svg';
import {useEffect} from 'react';

/**
 * Points the document favicon at the branding for the current plan.
 *
 * The theme serves a single static favicon for the whole instance, so the
 * branding can only be resolved here, where the plan is known. The selector
 * matches both "icon" and "shortcut icon" because the theme emits the latter.
 */
export const useDocumentFavicon = (ldpEnabled: boolean): void => {
	useEffect(() => {
		let link = document.querySelector("link[rel~='icon']");

		if (!link) {
			link = document.createElement('link');

			link.setAttribute('rel', 'icon');

			document.head.appendChild(link);
		}

		link.setAttribute('href', ldpEnabled ? ldpFaviconURL : acFaviconURL);

		// The theme's favicon is an ICO, so a stale type would make the
		// browser reject the SVG.

		link.setAttribute('type', 'image/svg+xml');
	}, [ldpEnabled]);
};
