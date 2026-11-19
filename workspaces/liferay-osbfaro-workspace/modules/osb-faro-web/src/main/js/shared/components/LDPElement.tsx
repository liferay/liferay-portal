import React from 'react';
import {useLDPEnabled} from 'shared/hooks/useLDPEnabled';
import {config} from 'shared/util/config';

interface ILDPElementProps {
	children: React.ReactNode;
	fallback: React.ReactNode;
}

export const LDPElement: React.FC<ILDPElementProps> = ({
	children,
	fallback,
}) => {
	const {groupId = ''} = config;

	const LDPEnabled = useLDPEnabled({groupId});

	return <>{LDPEnabled ? children : fallback}</>;
};
