/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import React from 'react';

import {ContentCard} from './ContentCard';
import {ExpiredAssetsCard} from './ExpiredAssetsCard';
import {FilesCard} from './FilesCard';
import {InventoryAnalysisCard} from './InventoryAnalysisCard';
import {LanguagesDropdown} from './LanguagesDropdown';
import {SectionHeader} from './SectionHeader';
import {SpacesDropdown} from './SpacesDropdown';

export default function InventoryDashboard() {
	return (
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
