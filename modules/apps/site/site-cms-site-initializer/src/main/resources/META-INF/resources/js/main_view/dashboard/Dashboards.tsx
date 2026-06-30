/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import {ClayTooltipProvider} from '@clayui/tooltip';
import React from 'react';

import Breadcrumb from '../../common/components/Breadcrumb';
import InventoryDashboard from './inventory/InventoryDashboard';

import '../../../css/dashboard/Dashboard.scss';

import {ILearnResourceContext} from 'frontend-js-components-web';

import EnterpriseOnlyPlaceholder from '../../common/components/EnterpriseOnlyPlaceholder';

interface IDashboards {
	constants: {
		[key: string]: string;
	};
	freeTier: boolean;
	learnResources: ILearnResourceContext;
}

const Dashboards: React.FC<IDashboards> = ({
	constants,
	freeTier,
	learnResources,
}) => {
	return (
		<>
			<Breadcrumb
				breadcrumbItems={[{label: Liferay.Language.get('dashboard')}]}
				freeTier={freeTier}
				hideSpace
			/>

			<ClayTooltipProvider>
				<ClayLayout.Container className="px-4" fluid>
					{freeTier ? (
						<EnterpriseOnlyPlaceholder
							learnResources={learnResources}
						/>
					) : (
						<InventoryDashboard constants={constants} />
					)}
				</ClayLayout.Container>
			</ClayTooltipProvider>
		</>
	);
};

export default Dashboards;
