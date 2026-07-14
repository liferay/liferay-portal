/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import React, {useContext} from 'react';

import {SectionHeader} from '../common/SectionHeader';
import {SpacesDropdown} from '../common/SpacesDropdown';
import {InventoryContext, InventoryContextProvider} from './InventoryContext';
import {ContentCard} from './components/ContentCard';
import {ExpiredAssetsCard} from './components/ExpiredAssetsCard';
import {FilesCard} from './components/FilesCard';
import {InventoryAnalysisCard} from './components/InventoryAnalysisCard';
import {LanguagesDropdown} from './components/LanguagesDropdown';

import '../../../../css/dashboard/InventoryDashboard.scss';

export default function InventoryDashboard({
	constants,
}: {
	constants: {[key: string]: string};
}) {
	return (
		<InventoryContextProvider value={{constants}}>
			<InventoryDashboardContent />
		</InventoryContextProvider>
	);
}

function InventoryDashboardContent() {
	const {
		changeSpace,
		filters: {space},
	} = useContext(InventoryContext);

	return (
		<>
			<ClayLayout.Row className="mb-4">
				<ClayLayout.Col size={12}>
					<div className="d-flex">
						<SpacesDropdown
							className="mr-3"
							onSelectSpace={changeSpace}
							selectedSpace={space}
						/>

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
						ariaLevel={2}
						icon="diagram"
						role="heading"
						title={Liferay.Language.get('assets-distributions')}
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
						title={Liferay.Language.get('health-and-usage')}
					/>
				</ClayLayout.Col>
			</ClayLayout.Row>

			<ClayLayout.Row className="mb-4">
				<ClayLayout.Col size={12}>
					<ExpiredAssetsCard />
				</ClayLayout.Col>
			</ClayLayout.Row>
		</>
	);
}
