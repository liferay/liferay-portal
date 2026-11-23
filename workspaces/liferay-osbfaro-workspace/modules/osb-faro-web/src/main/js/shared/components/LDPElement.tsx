import React from 'react';
import {useLDPEnabled} from 'shared/hooks/useLDPEnabled';
import {usePageConfig} from 'shared/context/pageConfig';

interface ILDPElementProps {
	children: React.ReactNode;
	fallback: React.ReactNode;
}

export const LDPElement: React.FC<ILDPElementProps> = ({
	children,
	fallback,
}) => {
	const {groupId} = usePageConfig();

	const LDPEnabled = useLDPEnabled({groupId});

	return <>{LDPEnabled ? children : fallback}</>;
};
