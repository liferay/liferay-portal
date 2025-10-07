/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLabel from '@clayui/label';
import ClayLayout from '@clayui/layout';
import ClayTabs from '@clayui/tabs';
import {useId} from 'frontend-js-components-web';
import React, {useEffect} from 'react';

import focusInvalidElement from '../../../common/utils/focusInvalidElement';
import {useStateDispatch} from '../../contexts/StateContext';
import {RepeatableGroup} from '../../types/Structure';
import Breadcrumb from '../Breadcrumb';
import {LocalizedInput} from '../LocalizedInput';

export default function RepeatableGroupSettings({
	disabled,
	group,
}: {
	disabled?: boolean;
	group: RepeatableGroup;
}) {
	useEffect(() => {
		focusInvalidElement();
	}, []);

	return (
		<ClayLayout.ContainerFluid className="px-4" size="md" view>
			<Breadcrumb uuid={group.uuid} />

			<ClayTabs>
				<ClayTabs.List>
					<ClayTabs.Item>
						{Liferay.Language.get('general')}
					</ClayTabs.Item>

					<ClayTabs.Item>
						{Liferay.Language.get('search')}
					</ClayTabs.Item>
				</ClayTabs.List>

				<ClayTabs.Panels fade>
					<ClayTabs.TabPane className="px-0">
						<GeneralTab disabled={disabled} group={group} />
					</ClayTabs.TabPane>

					<ClayTabs.TabPane className="px-0">
						<SearchTab />
					</ClayTabs.TabPane>
				</ClayTabs.Panels>
			</ClayTabs>
		</ClayLayout.ContainerFluid>
	);
}

function GeneralTab({
	disabled,
	group,
}: {
	disabled?: boolean;
	group: RepeatableGroup;
}) {
	const dispatch = useStateDispatch();

	const labelInputId = useId();

	return (
		<div>
			<div className="pb-2">
				<p className="font-weight-semi-bold mb-0 text-3">
					{Liferay.Language.get('field-type')}
				</p>

				<ClayLabel displayType="success">
					{Liferay.Language.get('repeatable-group')}
				</ClayLabel>
			</div>

			<LocalizedInput
				disabled={disabled}
				formGroupClassName="mt-4"
				id={labelInputId}
				label={Liferay.Language.get('label')}
				onSave={(translations) => {
					dispatch({
						label: translations,
						type: 'update-repeatable-group',
						uuid: group.uuid,
					});
				}}
				required
				translations={group.label}
			/>
		</div>
	);
}

function SearchTab() {
	return <div />;
}
