/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import {ClayVerticalNav} from '@clayui/nav';
import React, {ReactElement, useState} from 'react';

interface Props {
	mainContentMap: Map<string, ReactElement>;
}

interface VerticalNavItem {
	active: boolean;
	label: string;
	onClick: () => void;
}

/**
 * Container component for the main content of the Categorization section's
 * individual entity-related pages with a navigation sidebar, such as creating
 * or editing an entity.
 *
 * @param mainContentMap Map of <key: string, value: ReactElement> pairs where
 * the keys are the language key for a given view and the values are the
 * corresponding React component. Maps retain insertion order - the vertical
 * nav bar's items will be in the order that entries were inserted into the map.
 *
 * @constructor
 */
const CategorizationContentContainer = ({mainContentMap}: Props) => {
	const [activeVerticalNavKey, setActiveVerticalNavKey] = useState(
		mainContentMap.keys().next().value
	);

	const verticalNavItems: VerticalNavItem[] = [];

	mainContentMap.forEach((value: ReactElement, key: string) => {
		verticalNavItems.push({
			active: activeVerticalNavKey === key,
			label: Liferay.Language.get(key),
			onClick: () => {
				setActiveVerticalNavKey(key);
			},
		});
	});

	return (
		<>
			<ClayLayout.ContainerFluid size={false}>
				<ClayLayout.Row className="min-vh-100">
					<ClayLayout.Col
						className="cms-sidebar-nav sidebar-layout"
						md="auto"
						sm={12}
					>
						<div
							className="px-md-2 py-3 py-md-4"
							data-testid="categorization-sidebar"
						>
							<ClayVerticalNav items={verticalNavItems} />
						</div>
					</ClayLayout.Col>

					<ClayLayout.Col className="col-md" sm={12}>
						{mainContentMap.has(activeVerticalNavKey) &&
							mainContentMap.get(activeVerticalNavKey)}
					</ClayLayout.Col>
				</ClayLayout.Row>
			</ClayLayout.ContainerFluid>
		</>
	);
};

export default CategorizationContentContainer;
