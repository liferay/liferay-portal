/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '../../../css/categorization/Categorization.scss';

import ClayButton from '@clayui/button';
import ClayNavigationBar from '@clayui/navigation-bar';
import {navigate} from 'frontend-js-web';
import React from 'react';

import Breadcrumb from '../../common/components/Breadcrumb';

export default function CategorizationToolbar({
	activeTab,
	tagsURL,
	vocabulariesURL,
}: {
	activeTab: string;
	tagsURL: string;
	vocabulariesURL: string;
}) {
	return (
		<div>
			<Breadcrumb
				breadcrumbItems={[
					{
						active: true,
						label: Liferay.Language.get('categorization'),
					},
				]}
				hideSpace
			/>

			<ClayNavigationBar
				aria-label={Liferay.Language.get('navigation')}
				fluidSize={false}
				triggerLabel={activeTab}
			>
				<ClayNavigationBar.Item
					active={activeTab === 'vocabularies'}
					key={Liferay.Language.get('vocabularies')}
				>
					<ClayButton onClick={() => navigate(vocabulariesURL)}>
						{Liferay.Language.get('vocabularies')}
					</ClayButton>
				</ClayNavigationBar.Item>

				<ClayNavigationBar.Item
					active={activeTab === 'tags'}
					key={Liferay.Language.get('tags')}
				>
					<ClayButton onClick={() => navigate(tagsURL)}>
						{Liferay.Language.get('tags')}
					</ClayButton>
				</ClayNavigationBar.Item>
			</ClayNavigationBar>
		</div>
	);
}
