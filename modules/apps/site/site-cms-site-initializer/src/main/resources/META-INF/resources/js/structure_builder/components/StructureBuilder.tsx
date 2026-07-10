/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '../../../css/structure_builder/StructureBuilder.scss';

import React, {useEffect} from 'react';

import {
	ObjectDefinition,
	ObjectDefinitions,
	ObjectRelationship,
} from '../../common/types/ObjectDefinition';
import {
	DefaultLanguageLabels,
	setDefaultLanguageLabels,
} from '../../common/utils/defaultLanguageLabels';
import {Config, initializeConfig} from '../config';
import CacheContextProvider from '../contexts/CacheContext';
import StateContextProvider, {useSelector} from '../contexts/StateContext';
import selectStructureId from '../selectors/selectStructureId';
import selectStructureStatus from '../selectors/selectStructureStatus';
import buildState from '../utils/buildState';
import {setSystemObjectFieldNames} from '../utils/isCustomObjectField';
import HelpButton from './HelpButton';
import ShortcutManager from './ShortcutManager';
import Sidebar from './Sidebar';
import StructureBuilderToolbar from './StructureBuilderToolbar';
import Settings from './settings/Settings';

export default function StructureBuilder({
	config,
	defaultLanguageLabels,
	state,
	systemObjectFieldNames,
}: {
	config: Config;
	defaultLanguageLabels: DefaultLanguageLabels;
	state: {
		mainObjectDefinition: ObjectDefinition;
		objectDefinitions: ObjectDefinitions;
		relatedContentObjectRelationships: ObjectRelationship[];
	};
	systemObjectFieldNames: Record<string, string[]>;
}) {
	initializeConfig(config);
	setDefaultLanguageLabels(defaultLanguageLabels);
	setSystemObjectFieldNames(systemObjectFieldNames);

	return (
		<StateContextProvider initialState={buildState(state)}>
			<CacheContextProvider
				initialData={{
					'object-definitions': state.objectDefinitions,
				}}
			>
				<div className="d-flex flex-column structure-builder__wrapper">
					<HistoryManager />

					<ShortcutManager />

					<StructureBuilderToolbar />

					<div className="d-flex flex-grow-1 p-2 p-md-4">
						<Sidebar />

						<Settings />
					</div>

					<HelpButton />
				</div>
			</CacheContextProvider>
		</StateContextProvider>
	);
}

function HistoryManager() {
	const id = useSelector(selectStructureId);
	const status = useSelector(selectStructureStatus);

	useEffect(() => {
		if (status !== 'published' || !id) {
			return;
		}

		const url = new URL(window.location.href);

		if (url.searchParams.has('objectFolderExternalReferenceCode')) {
			url.searchParams.delete('objectFolderExternalReferenceCode');
		}

		url.searchParams.set('objectDefinitionId', String(id));

		history.replaceState(null, document.head.title, url.href);
	}, [id, status]);

	return null;
}
