import AssetDashboard from 'assets/pages/Dashboard';
import React from 'react';
import {Router} from 'shared/types';

const Form: React.FC<{
	className: string;
	router: Router;
}> = (props) => <AssetDashboard {...props} slug="forms" />;

export default Form;
