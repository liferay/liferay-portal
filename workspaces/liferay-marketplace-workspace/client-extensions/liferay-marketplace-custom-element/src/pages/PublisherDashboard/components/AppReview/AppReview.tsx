/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {NewAppInitialState} from '../../../../context/NewAppContext';
import Build from './Build/Build';
import Categories from './Categories/Categories';
import Description from './Description/Description';
import Licensing from './Licensing/Licensing';
import Pricing from './Pricing/Pricing';
import Profile from './Profile/Profile';
import Storefront from './Storefront/Storefront';
import Support from './Support/Support';

import './AppReview.scss';

export type AppReviewProps = {
	children?: any;
	context: NewAppInitialState;
	editNavigate?: () => void;
	isLastSection?: boolean;
	required?: boolean;
};

const AppReview: React.FC<AppReviewProps> & {
	Build: React.FC<AppReviewProps>;
	Categories: React.FC<AppReviewProps>;
	Description: React.FC<AppReviewProps>;
	Licensing: React.FC<AppReviewProps>;
	Pricing: React.FC<AppReviewProps>;
	Profile: React.FC<AppReviewProps>;
	Storefront: React.FC<AppReviewProps>;
	Support: React.FC<AppReviewProps>;
} = ({children}) => <div>{children}</div>;

AppReview.Build = Build;
AppReview.Categories = Categories;
AppReview.Description = Description;
AppReview.Licensing = Licensing;
AppReview.Pricing = Pricing;
AppReview.Profile = Profile;
AppReview.Storefront = Storefront;
AppReview.Support = Support;

export default AppReview;
