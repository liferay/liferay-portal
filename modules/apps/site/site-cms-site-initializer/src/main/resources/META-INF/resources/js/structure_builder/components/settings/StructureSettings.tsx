/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayLabel from '@clayui/label';
import ClayLayout from '@clayui/layout';
import ClayTabs from '@clayui/tabs';
import React, {useEffect, useState} from 'react';

import focusInvalidElement from '../../../common/utils/focusInvalidElement';
import {useCache} from '../../contexts/CacheContext';
import {useSelector, useStateDispatch} from '../../contexts/StateContext';
import selectErrors from '../../selectors/selectErrors';
import selectStructure from '../../selectors/selectStructure';
import selectStructureLabel from '../../selectors/selectStructureLabel';
import selectStructureType from '../../selectors/selectStructureType';
import selectStructureUuid from '../../selectors/selectStructureUuid';
import ERCInput from '../ERCInput';
import Input from '../Input';
import {LocalizedInput} from '../LocalizedInput';
import SlugInput from '../SlugInput';
import SpacesSelector from '../SpacesSelector';
import WorkflowTab from './WorkflowTab';

const STRUCTURE_TYPES = {
	L_CMS_CONTENT_STRUCTURES: Liferay.Language.get('content'),
	L_CMS_FILE_TYPES: Liferay.Language.get('file'),
};

export default function StructureSettings() {
	const dispatch = useStateDispatch();

	const structureLabel = useSelector(selectStructureLabel);
	const structureType = useSelector(selectStructureType);
	const structureUuid = useSelector(selectStructureUuid);
	const errors = useSelector(selectErrors(structureUuid));

	const {data: objectDefinitions} = useCache('object-definitions');

	const [activeTab, setActiveTab] = useState(0);

	useEffect(() => {
		focusInvalidElement();
	}, []);

	useEffect(() => {
		if (errors.size) {
			setActiveTab(0);
		}
	}, [errors]);

	return (
		<ClayLayout.ContainerFluid className="px-4" size="md" view>
			{errors.get('global') ? (
				<ClayAlert
					displayType="danger"
					role={null}
					title={Liferay.Language.get('error')}
				>
					{errors.get('global')}
				</ClayAlert>
			) : null}

			<ClayLabel className="mb-3" displayType="info" inverse>
				{STRUCTURE_TYPES[structureType]}
			</ClayLabel>

			<LocalizedInput
				aria-label={Liferay.Language.get('content-structure-label')}
				className="form-control-inline structure-builder__title-input"
				error={errors.get('label')}
				formGroupClassName="ml-n3"
				onSave={(translations) => {
					if (Object.keys(translations).length) {
						dispatch({
							label: translations,
							objectDefinitions,
							type: 'update-structure',
						});
					}
				}}
				placeholder={Liferay.Language.get('content-structure-label')}
				required
				translations={structureLabel}
			/>

			<ClayTabs active={activeTab} onActiveChange={setActiveTab}>
				<ClayTabs.List>
					<ClayTabs.Item>
						{Liferay.Language.get('general')}
					</ClayTabs.Item>

					<ClayTabs.Item>
						{Liferay.Language.get('workflow')}
					</ClayTabs.Item>
				</ClayTabs.List>

				<ClayTabs.Panels fade>
					<ClayTabs.TabPane className="px-0">
						<GeneralTab />
					</ClayTabs.TabPane>

					<ClayTabs.TabPane className="px-0">
						<WorkflowTab />
					</ClayTabs.TabPane>
				</ClayTabs.Panels>
			</ClayTabs>
		</ClayLayout.ContainerFluid>
	);
}

function GeneralTab() {
	const dispatch = useStateDispatch();
	const structure = useSelector(selectStructure);
	const errors = useSelector(selectErrors(structure.uuid));

	const {data: objectDefinitions} = useCache('object-definitions');

	const {erc, name, slug, status, system} = structure;

	return (
		<div>
			<Input
				disabled={status === 'published'}
				error={errors.get('name')}
				label={Liferay.Language.get('content-structure-name')}
				onValueChange={(value) =>
					dispatch({
						name: value,
						objectDefinitions,
						type: 'update-structure',
					})
				}
				placeholder={Liferay.Language.get('content-structure-name')}
				required
				value={name}
			/>

			<ERCInput
				disabled={system}
				error={errors.get('erc')}
				onValueChange={(value) =>
					dispatch({erc: value, type: 'update-structure'})
				}
				value={erc}
			/>

			<SlugInput
				disabled={system}
				error={errors.get('slug')}
				onValueChange={(value) =>
					dispatch({slug: value, type: 'update-structure'})
				}
				value={slug}
			/>

			<SpacesSelector structure={structure} />
		</div>
	);
}
