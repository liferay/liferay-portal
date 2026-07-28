import acFaviconURL from '../../../images/favicons/ac_favicon.svg';
import ldpFaviconURL from '../../../images/favicons/ldp_favicon.svg';
import React from 'react';
import {useEffect} from 'react';
import {useLDPEnabled} from 'shared/hooks/useLDPEnabled';
import {useParams} from 'react-router-dom';

interface IDocumentTitleProps {
	ldpEnabled?: boolean;
	loadingLDPEnabled?: boolean;
	title: string;
}

const DocumentTitle: React.FC<IDocumentTitleProps> = ({
	ldpEnabled,
	loadingLDPEnabled,
	title,
}) => {
	const {groupId = ''} = useParams<{groupId?: string}>();
	const routeLDPEnabled = useLDPEnabled({groupId});

	const LDPEnabled = ldpEnabled ?? routeLDPEnabled;

	// The theme serves a single static favicon for the whole instance, so the
	// branding can only be resolved here, where the plan is known.

	useEffect(() => {

		// The plan is still unknown, so claiming a brand now would flash the
		// wrong icon before the check resolves. Leaving the theme's own favicon
		// in place is the neutral state.

		if (loadingLDPEnabled) {
			return;
		}

		// The selector matches both "icon" and "shortcut icon" because the
		// theme emits the latter.

		let link = document.querySelector("link[rel~='icon']");

		if (!link) {
			link = document.createElement('link');

			link.setAttribute('rel', 'icon');

			document.head.appendChild(link);
		}

		link.setAttribute('href', LDPEnabled ? ldpFaviconURL : acFaviconURL);

		// The theme's favicon is an ICO, so a stale type would make the browser
		// reject the SVG.

		link.setAttribute('type', 'image/svg+xml');
	}, [LDPEnabled, loadingLDPEnabled]);

	useEffect(() => {
		if (loadingLDPEnabled) {
			document.title = title;

			return;
		}

		const defaultTitle = LDPEnabled
			? Liferay.Language.get('liferay-data-platform')
			: Liferay.Language.get('analytics-cloud');

		// Pages that render their own heading pass an empty title, so the
		// product name has to stand on its own rather than trail a dash.

		document.title = title ? `${title} - ${defaultTitle}` : defaultTitle;
	}, [LDPEnabled, loadingLDPEnabled, title]);

	return null;
};

export default DocumentTitle;
