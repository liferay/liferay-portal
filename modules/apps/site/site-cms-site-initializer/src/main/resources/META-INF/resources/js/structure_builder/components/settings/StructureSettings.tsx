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
import {useSelector, useStateDispatch} from '../../contexts/StateContext';
import selectInvalids from '../../selectors/selectInvalids';
import selectState from '../../selectors/selectState';
import selectStructureERC from '../../selectors/selectStructureERC';
import selectStructureError from '../../selectors/selectStructureError';
import selectStructureLabel from '../../selectors/selectStructureLabel';
import selectStructureName from '../../selectors/selectStructureName';
import selectStructureStatus from '../../selectors/selectStructureStatus';
import selectStructureUuid from '../../selectors/selectStructureUuid';
import ERCInput from '../ERCInput';
import Input from '../Input';
import {LocalizedInput} from '../LocalizedInput';
import SpacesSelector from '../SpacesSelector';
import WorkflowTab from './WorkflowTab';

export default function StructureSettings() {
	const dispatch = useStateDispatch();
	const error = useSelector(selectStructureError);
	const invalids = useSelector(selectInvalids);
	const structureError = useSelector(selectStructureError);
	const structureLabel = useSelector(selectStructureLabel);
	const structureUuid = useSelector(selectStructureUuid);

	const [activeTab, setActiveTab] = useState(0);

	useEffect(() => {
		focusInvalidElement();
	}, []);

	useEffect(() => {
		if (structureError || invalids.has(structureUuid)) {
			setActiveTab(0);
		}
	}, [invalids, structureError, structureUuid]);

	return (
		<ClayLayout.ContainerFluid className="px-4" size="md" view>
			{error ? (
				<ClayAlert
					displayType="danger"
					role={null}
					title={Liferay.Language.get('error')}
				>
					{error}
				</ClayAlert>
			) : null}

			<ClayLabel className="mb-3" displayType="info">
				{Liferay.Language.get('content')}
			</ClayLabel>

			<LocalizedInput
				aria-label={Liferay.Language.get('content-structure-label')}
				className="form-control-inline structure-builder__title-input"
				formGroupClassName="ml-n3"
				onSave={(translations) => {
					dispatch({
						label: translations,
						type: 'update-structure',
					});
				}}
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
	const name = useSelector(selectStructureName);
	const erc = useSelector(selectStructureERC);
	const status = useSelector(selectStructureStatus);
	const state = useSelector(selectState);

	return (
		<div>
			<Input
				disabled={status === 'published'}
				label={Liferay.Language.get('content-structure-name')}
				onValueChange={(value) =>
					dispatch({name: value, type: 'update-structure'})
				}
				required
				value={name}
			/>

			<ERCInput
				onValueChange={(value) =>
					dispatch({erc: value, type: 'update-structure'})
				}
				value={erc}
			/>

			<SpacesSelector structure={state.structure} />
		</div>
	);
}
