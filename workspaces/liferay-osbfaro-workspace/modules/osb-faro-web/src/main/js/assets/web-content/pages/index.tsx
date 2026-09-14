import AssetDashboard from 'assets/pages/Dashboard';
import React from 'react';
import {Router} from 'shared/types';

const WebContent: React.FC<{
	className: string;
	router: Router;
}> = (props) => <AssetDashboard {...props} slug="web-content" />;

export default WebContent;
