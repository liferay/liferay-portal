/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLink from '@clayui/link';
import ClayNavigationBar from '@clayui/navigation-bar';
import {useState} from 'react';

import {useSolutionContext} from '../../../../../context/SolutionContext';
import {Submit} from '../SolutionForm/pages';
import SolutionsDetailsHeader from '../components/SolutionDetailsHeader';

import './index.scss';

export type Solution = {
	attachmentTitle: string;
	categories: string[];
	description: string;
	name: string;
	storefront: ProductImages[];
	tags: string[];
	thumbnail: string;
};

const NAVIGATION_BAR_OPTIONS = {
	DETAILS: 'Details',
};

const SolutionsDetails = () => {
	const [active, setActive] = useState(NAVIGATION_BAR_OPTIONS.DETAILS);
	const [context] = useSolutionContext();

	const {_product} = context;

	return (
		<div className="solutions-details-page-container w-100">
			<div className="mb-5 solutions-details-header">
				<SolutionsDetailsHeader product={_product} />

				<ClayNavigationBar className="w-100" triggerLabel={active}>
					<ClayNavigationBar.Item
						active={active === NAVIGATION_BAR_OPTIONS.DETAILS}
					>
						<ClayLink
							onClick={() => {
								setActive(NAVIGATION_BAR_OPTIONS.DETAILS);
							}}
						>
							Details
						</ClayLink>
					</ClayNavigationBar.Item>
				</ClayNavigationBar>
			</div>

			<div className="solution-details-page-content">
				{active === NAVIGATION_BAR_OPTIONS.DETAILS && (
					<Submit readOnly />
				)}
			</div>
		</div>
	);
};

export default SolutionsDetails;
