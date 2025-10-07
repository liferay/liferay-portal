/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayBreadcrumb from '@clayui/breadcrumb';
import React, {useMemo} from 'react';

import getLocalizedValue from '../../common/utils/getLocalizedValue';
import {useSelector, useStateDispatch} from '../contexts/StateContext';
import selectStructureChildren from '../selectors/selectStructureChildren';
import selectStructureLocalizedLabel from '../selectors/selectStructureLocalizedLabel';
import selectStructureUuid from '../selectors/selectStructureUuid';
import {
	ReferencedStructure,
	RepeatableGroup,
	Structure,
} from '../types/Structure';
import {Uuid} from '../types/Uuid';

type Path = {label: string; uuid: Uuid}[];

export default function Breadcrumb({uuid}: {uuid: Uuid}) {
	const dispatch = useStateDispatch();

	const children = useSelector(selectStructureChildren);

	const structureLabel = useSelector(selectStructureLocalizedLabel);
	const structureUuid = useSelector(selectStructureUuid);

	const items = useMemo(() => {
		const path = getPath(uuid, children, [
			{label: structureLabel, uuid: structureUuid},
		]);

		if (!path) {
			return [];
		}

		return path.map((item) => {
			if (item.uuid === uuid) {
				return {
					active: true,
					label: item.label,
				};
			}

			return {
				label: item.label,
				onClick: () => {
					dispatch({
						selection: [item.uuid],
						type: 'set-selection',
					});
				},
			};
		});
	}, [children, dispatch, structureLabel, structureUuid, uuid]);

	return (
		<div className="mb-3">
			<ClayBreadcrumb items={items} />
		</div>
	);
}

function getPath(
	uuid: Uuid,
	children: (ReferencedStructure | RepeatableGroup | Structure)['children'],
	path: Path = []
): Path | null {
	for (const child of children.values()) {
		if (child.uuid === uuid) {
			return [
				...path,
				{
					label: getLocalizedValue(child.label),
					uuid: child.uuid,
				},
			];
		}
		else if (
			child.type === 'referenced-structure' ||
			child.type === 'repeatable-group'
		) {
			const nextPath = getPath(uuid, child.children, [
				...path,
				{
					label: getLocalizedValue(child.label),
					uuid: child.uuid,
				},
			]);

			if (nextPath) {
				return nextPath;
			}
		}
	}

	return null;
}
