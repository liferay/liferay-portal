/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Text} from '@clayui/core';
import ClayLayout from '@clayui/layout';
import React from 'react';

import {ViewDashboardContextProvider} from './ViewDashboardContext';
import {ContentCard} from './components/ContentCard';
import {FilesCard} from './components/FilesCard';
import {InventoryAnalysisCard} from './components/InventoryAnalysisCard';
import {LanguagesDropdown} from './components/LanguagesDropdown';
import {SectionHeader} from './components/SectionHeader';
import {SpacesDropdown} from './components/SpacesDropdown';

import '../../../css/dashboard/Dashboard.scss';

export default function ViewDashboard() {
	return (
		<ViewDashboardContextProvider>
			<ClayLayout.Container className="p-5" fluid>
				<ClayLayout.Row className="mb-5">
					<ClayLayout.Col size={12}>
						<Text size={7} weight="bold">
							{Liferay.Language.get('dashboard')}
						</Text>
					</ClayLayout.Col>
				</ClayLayout.Row>

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
							icon="plus-squares"
							title={Liferay.Language.get('what-is-new')}
						/>
					</ClayLayout.Col>
				</ClayLayout.Row>

				<ClayLayout.Row className="mb-4">
					<ClayLayout.Col className="mb-3" lg={12} xl={6}>
						<ContentCard />
					</ClayLayout.Col>

					<ClayLayout.Col className="mb-3" lg={12} xl={6}>
						<FilesCard />
					</ClayLayout.Col>
				</ClayLayout.Row>

				<ClayLayout.Row className="mb-2">
					<ClayLayout.Col size={12}>
						<SectionHeader
							icon="diagram"
							title={Liferay.Language.get('assets-distributions')}
						/>
					</ClayLayout.Col>
				</ClayLayout.Row>

				<ClayLayout.Row>
					<ClayLayout.Col size={12}>
						<InventoryAnalysisCard />
					</ClayLayout.Col>
				</ClayLayout.Row>
			</ClayLayout.Container>
		</ViewDashboardContextProvider>
	);
}
