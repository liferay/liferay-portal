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

import {useCache} from '../../contexts/CacheContext';
import {useStateDispatch} from '../../contexts/StateContext';
import selectStructureERC from '../../selectors/selectStructureERC';
import selectStructureName from '../../selectors/selectStructureName';
import selectStructureStatus from '../../selectors/selectStructureStatus';
import {ReferencedStructure, Structure} from '../../types/Structure';
import getReferencedStructureLabel from '../../utils/getReferencedStructureLabel';
import getStructureEditURL from '../../utils/getStructureEditURL';
import Breadcrumb from '../Breadcrumb';
import ERCInput from '../ERCInput';
import Input from '../Input';
import Spaces from '../Spaces';

export default function ReferencedStructureSettings({
	referencedStructure,
}: {
	referencedStructure: ReferencedStructure;
}) {
	const {data: structures} = useCache('structures');

	const label = getReferencedStructureLabel(
		referencedStructure.erc,
		structures
	);

	const structure = structures.get(referencedStructure.erc);

	if (!structure) {
		return null;
	}

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
						Liferay.Language.get('x-is-a-referenced-structure'),
						label
					)}
				</span>

				<ClayLink
					className="ml-1"
					displayType="unstyled"
					href={getStructureEditURL(structure)}
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
						{Liferay.Language.get('validations')}
					</ClayTabs.Item>
				</ClayTabs.List>

				<ClayTabs.Panels fade>
					<ClayTabs.TabPane className="px-0">
						<GeneralTab structure={structure} />
					</ClayTabs.TabPane>

					<ClayTabs.TabPane className="px-0">
						<ValidationsTab />
					</ClayTabs.TabPane>
				</ClayTabs.Panels>
			</ClayTabs>
		</ClayLayout.ContainerFluid>
	);
}

function GeneralTab({structure}: {structure: Structure}) {
	const dispatch = useStateDispatch();

	const name = selectStructureName(structure);
	const erc = selectStructureERC(structure);
	const status = selectStructureStatus(structure);

	return (
		<div>
			<div className="mb-4">
				<p className="font-weight-semi-bold mb-0 text-3">
					{Liferay.Language.get('field-type')}
				</p>

				<ClayLabel displayType="warning">
					{Liferay.Language.get('referenced-structure')}
				</ClayLabel>
			</div>

			<Input
				disabled={status === 'published'}
				label={Liferay.Language.get('structure-name')}
				onValueChange={(value) =>
					dispatch({name: value, type: 'update-structure'})
				}
				required
				value={name}
			/>

			<ERCInput
				disabled
				onValueChange={(value) =>
					dispatch({erc: value, type: 'update-structure'})
				}
				value={erc}
			/>

			<Spaces disabled structure={structure} />
		</div>
	);
}

function ValidationsTab() {
	return <div></div>;
}
