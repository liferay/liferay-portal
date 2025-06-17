/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayBreadcrumb from '@clayui/breadcrumb';
import React, {useMemo} from 'react';

import {useCache} from '../contexts/CacheContext';
import {useSelector, useStateDispatch} from '../contexts/StateContext';
import selectStructureFields from '../selectors/selectStructureFields';
import selectStructureLocalizedLabel from '../selectors/selectStructureLocalizedLabel';
import selectStructureUuid from '../selectors/selectStructureUuid';
import {ReferencedStructure, Structures} from '../types/Structure';
import {Uuid} from '../types/Uuid';
import {Field} from '../utils/field';
import getReferencedStructureLabel from '../utils/getReferencedStructureLabel';

type Path = {label: string; uuid: Uuid}[];

export default function Breadcrumb({uuid}: {uuid: Uuid}) {
	const dispatch = useStateDispatch();

	const structureLabel = useSelector(selectStructureLocalizedLabel);
	const structureUuid = useSelector(selectStructureUuid);
	const fields = useSelector(selectStructureFields);

	const {data: structures} = useCache('structures');

	const items = useMemo(() => {
		const path = getPath(uuid, fields, structures, [
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
	}, [dispatch, fields, structureLabel, structureUuid, structures, uuid]);

	return (
		<div className="mb-3">
			<ClayBreadcrumb items={items} />
		</div>
	);
}

function getPath(
	uuid: Uuid,
	fields: (Field | ReferencedStructure)[],
	structures: Structures,
	path: Path = []
): Path | null {
	for (const field of fields) {
		if (field.uuid === uuid) {
			if (field.type === 'referenced-structure') {
				path.push({
					label: getReferencedStructureLabel(field.erc, structures),
					uuid: field.uuid,
				});
			}
			else {
				path.push({
					label: field!.label[
						Liferay.ThemeDisplay.getDefaultLanguageId()
					]!,
					uuid: field.uuid,
				});
			}

			return path;
		}

		if (field.type === 'referenced-structure') {
			const structure = structures.get(field.erc);

			path.push({
				label: getReferencedStructureLabel(field.erc, structures),
				uuid: field.uuid,
			});

			if (structure) {
				const nextPath = getPath(
					uuid,
					selectStructureFields(structure),
					structures,
					path
				);

				if (nextPath) {
					return nextPath;
				}
			}
		}
	}

	return null;
}
