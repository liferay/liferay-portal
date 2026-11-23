import AppProviders from '../AppProviders';
import AppShell from '../AppShell';
import React, {useState} from 'react';
import {PageConfigProvider} from 'shared/context/pageConfig';
import {buildConfig} from 'shared/util/config';

export interface IGuestPageAppProps<
	Params extends {[name: string]: string | undefined} = {
		[name: string]: string | undefined;
	},
> {
	friendlyURL: string;
	pageURLs: {[friendlyURL: string]: string};
	params?: Params | null;
	signOutURL: string;
}

const GuestPageApp: React.FC<
	IGuestPageAppProps & {children: React.ReactNode}
> = ({children, friendlyURL, pageURLs, params, signOutURL}) => {
	const [config] = useState(() =>
		buildConfig({friendlyURL, pageURLs, params, signOutURL})
	);

	return (
		<PageConfigProvider config={config}>
			<AppProviders>
				<AppShell>{children}</AppShell>
			</AppProviders>
		</PageConfigProvider>
	);
};

export default GuestPageApp;
