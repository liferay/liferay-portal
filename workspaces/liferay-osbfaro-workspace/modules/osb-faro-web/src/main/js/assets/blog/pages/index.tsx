import AssetDashboard from 'assets/pages/Dashboard';
import React from 'react';
import {Router} from 'shared/types';

const Blog: React.FC<{
	className: string;
	router: Router;
}> = (props) => <AssetDashboard {...props} slug="blogs" />;

export default Blog;
