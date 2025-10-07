/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import {ILearnResourceContext} from 'frontend-js-components-web';
import React, {useEffect} from 'react';

import {Picklist} from '../../../common/types/Picklist';
import PicklistBuilderContextProvider, {
	buildState,
	useId,
} from '../../contexts/PicklistBuilderContext';
import PicklistBuilderToolbar from './PicklistBuilderToolbar';
import PicklistFields from './PicklistFields';
import PicklistOptions from './PicklistOptions';

export default function PicklistBuilder({
	learnResources,
	state,
}: {
	learnResources: ILearnResourceContext;
	state: {listTypeDefinition: Picklist};
}) {
	return (
		<PicklistBuilderContextProvider
			initialState={buildState(state.listTypeDefinition)}
		>
			<div className="d-flex flex-column">
				<HistoryManager />

				<PicklistBuilderToolbar />

				<ClayLayout.ContainerFluid className="px-4" size="md" view>
					<PicklistFields learnResources={learnResources} />

					<PicklistOptions />
				</ClayLayout.ContainerFluid>
			</div>
		</PicklistBuilderContextProvider>
	);
}

function HistoryManager() {
	const id = useId();

	useEffect(() => {
		if (!id) {
			return;
		}

		const url = new URL(window.location.href);

		url.searchParams.set('listTypeDefinitionId', id.toString());

		history.replaceState(null, document.head.title, url.href);
	}, [id]);

	return null;
}
