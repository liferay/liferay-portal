import React, {createContext, useContext} from 'react';
import {Config} from 'shared/util/config';

const PageConfigContext = createContext<Config | null>(null);

export const PageConfigProvider = ({
	children,
	config,
}: {
	children: React.ReactNode;
	config: Config;
}) => (
	<PageConfigContext.Provider value={config}>
		{children}
	</PageConfigContext.Provider>
);

export const usePageConfig = () => {
	const config = useContext(PageConfigContext);

	if (!config) {
		throw new Error(
			'usePageConfig must be used within a PageConfigProvider'
		);
	}

	return config;
};
