import AssetDashboard from 'assets/pages/Dashboard';
import React from 'react';
import {Router} from 'shared/types';

const DocumentAndMedia: React.FC<{
	className: string;
	router: Router;
}> = (props) => <AssetDashboard {...props} slug="documents-and-media" />;

export default DocumentAndMedia;
