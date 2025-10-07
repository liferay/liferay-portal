/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import ClayLayout from '@clayui/layout';
import ClayLink from '@clayui/link';
import ClayTabs from '@clayui/tabs';
import {sub} from 'frontend-js-web';
import React from 'react';

import getLocalizedValue from '../../../common/utils/getLocalizedValue';
import {ReferencedStructure} from '../../types/Structure';
import Breadcrumb from '../Breadcrumb';
import ERCInput from '../ERCInput';
import Input from '../Input';
import SpacesSelector from '../SpacesSelector';
import WorkflowTab from './WorkflowTab';

export default function ReferencedStructureSettings({
	referencedStructure,
}: {
	referencedStructure: ReferencedStructure;
}) {
	const label = getLocalizedValue(referencedStructure.label);

	return (
		<ClayLayout.ContainerFluid className="px-4" size="md" view>
			<Breadcrumb uuid={referencedStructure.uuid} />

			<ClayAlert
				className="mb-4"
				displayType="info"
				role={null}
				title={Liferay.Language.get('info')}
			>
				<span>
					{sub(
						Liferay.Language.get(
							'x-is-a-referenced-content-structure'
						),
						label
					)}
				</span>

				<ClayLink
					className="ml-1"
					displayType="unstyled"
					href={referencedStructure.editURL}
					target="_blank"
				>
					{sub(
						Liferay.Language.get('to-edit,-open-the-x-edit-page'),
						label
					)}

					<ClayIcon className="ml-2" symbol="shortcut" />
				</ClayLink>
			</ClayAlert>

			<ClayTabs>
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
						<GeneralTab referencedStructure={referencedStructure} />
					</ClayTabs.TabPane>

					<ClayTabs.TabPane className="px-0">
						<WorkflowTab disabled={true} />
					</ClayTabs.TabPane>
				</ClayTabs.Panels>
			</ClayTabs>
		</ClayLayout.ContainerFluid>
	);
}

function GeneralTab({
	referencedStructure,
}: {
	referencedStructure: ReferencedStructure;
}) {
	const {erc, name} = referencedStructure;

	return (
		<div>
			<div className="mb-4">
				<p className="font-weight-semi-bold mb-0 text-3">
					{Liferay.Language.get('field-type')}
				</p>

				<ClayLabel displayType="warning">
					{Liferay.Language.get('referenced-content-structure')}
				</ClayLabel>
			</div>

			<Input
				disabled
				label={Liferay.Language.get('content-structure-name')}
				onValueChange={() => {}}
				required
				value={name}
			/>

			<ERCInput disabled onValueChange={() => {}} value={erc} />

			<SpacesSelector disabled structure={referencedStructure} />
		</div>
	);
}
