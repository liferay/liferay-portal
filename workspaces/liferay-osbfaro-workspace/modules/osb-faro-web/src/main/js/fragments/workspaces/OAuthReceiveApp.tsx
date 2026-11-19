import BundleRouter from 'route-middleware/BundleRouter';
import GuestPageApp, {IGuestPageAppProps} from '../GuestPageApp';
import OAuthReceive from 'settings/pages/OAuthReceive';
import React from 'react';

interface IOAuthReceiveParams {
	[name: string]: string | undefined;
	code?: string;
	error?: string;
	error_description?: string;
	oauth_token?: string;
	oauth_verifier?: string;
}

const OAuthReceiveApp: React.FC<IGuestPageAppProps<IOAuthReceiveParams>> = (
	props
) => (
	<GuestPageApp {...props}>
		<BundleRouter data={OAuthReceive} />
	</GuestPageApp>
);

export default OAuthReceiveApp;
