/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import {ClayTooltipProvider} from '@clayui/tooltip';
import React from 'react';

import Breadcrumb from '../../common/components/Breadcrumb';
import {DashboardsContextProvider} from './DashboardsContext';
import {ContentCard} from './components/ContentCard';
import {ExpiredAssetsCard} from './components/ExpiredAssetsCard';
import {FilesCard} from './components/FilesCard';
import {InventoryAnalysisCard} from './components/InventoryAnalysisCard';
import {LanguagesDropdown} from './components/LanguagesDropdown';
import {SectionHeader} from './components/SectionHeader';
import {SpacesDropdown} from './components/SpacesDropdown';

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
				<DashboardsContextProvider value={{constants}}>
					<ClayLayout.Container className="px-4" fluid>
						{freeTier ? (
							<EnterpriseOnlyPlaceholder
								learnResources={learnResources}
							/>
						) : (
							<>
								<ClayLayout.Row className="mb-4">
									<ClayLayout.Col size={12}>
										<div className="d-flex">
											<SpacesDropdown className="mr-3" />

											<LanguagesDropdown />
										</div>
									</ClayLayout.Col>
								</ClayLayout.Row>

								<ClayLayout.Row className="mb-2">
									<ClayLayout.Col size={12}>
										<SectionHeader
											ariaLevel={2}
											icon="plus-squares"
											role="heading"
											title={Liferay.Language.get(
												'what-is-new'
											)}
										/>
									</ClayLayout.Col>
								</ClayLayout.Row>

								<ClayLayout.Row className="mb-4">
									<ClayLayout.Col
										className="mb-3"
										lg={12}
										xl={6}
									>
										<ContentCard />
									</ClayLayout.Col>

									<ClayLayout.Col
										className="mb-3"
										lg={12}
										xl={6}
									>
										<FilesCard />
									</ClayLayout.Col>
								</ClayLayout.Row>

								<ClayLayout.Row className="mb-2">
									<ClayLayout.Col size={12}>
										<SectionHeader
											ariaLevel={2}
											icon="diagram"
											role="heading"
											title={Liferay.Language.get(
												'assets-distributions'
											)}
										/>
									</ClayLayout.Col>
								</ClayLayout.Row>

								<ClayLayout.Row className="mb-4">
									<ClayLayout.Col size={12}>
										<InventoryAnalysisCard />
									</ClayLayout.Col>
								</ClayLayout.Row>

								<ClayLayout.Row className="mb-2">
									<ClayLayout.Col size={12}>
										<SectionHeader
											ariaLevel={2}
											icon="order-form-pencil"
											role="heading"
											title={Liferay.Language.get(
												'health-and-usage'
											)}
										/>
									</ClayLayout.Col>
								</ClayLayout.Row>

								<ClayLayout.Row className="mb-4">
									<ClayLayout.Col size={12}>
										<ExpiredAssetsCard />
									</ClayLayout.Col>
								</ClayLayout.Row>
							</>
						)}
					</ClayLayout.Container>
				</DashboardsContextProvider>
			</ClayTooltipProvider>
		</>
	);
};

export default Dashboards;
