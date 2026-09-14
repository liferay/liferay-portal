import AssetDashboard from 'assets/pages/Dashboard';
import React from 'react';
import {Router} from 'shared/types';

const ObjectEntry: React.FC<{
	className: string;
	router: Router;
}> = (props) => <AssetDashboard {...props} slug="object-entry" />;

export default ObjectEntry;
