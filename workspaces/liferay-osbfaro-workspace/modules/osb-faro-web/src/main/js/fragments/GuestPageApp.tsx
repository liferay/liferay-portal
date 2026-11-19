import AppProviders from '../AppProviders';
import AppShell from '../AppShell';
import React, {useState} from 'react';
import {initializeConfig} from 'shared/util/config';

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
	useState(() =>
		initializeConfig({friendlyURL, pageURLs, params, signOutURL})
	);

	return (
		<AppProviders>
			<AppShell>{children}</AppShell>
		</AppProviders>
	);
};

export default GuestPageApp;
