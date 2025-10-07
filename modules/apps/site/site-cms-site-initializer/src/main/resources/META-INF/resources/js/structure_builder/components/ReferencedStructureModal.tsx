/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayModal, {useModal} from '@clayui/modal';
import ClayMultiSelect from '@clayui/multi-select';
import classNames from 'classnames';
import {FieldFeedback, useId} from 'frontend-js-components-web';
import React, {useEffect, useState} from 'react';

import getLocalizedValue from '../../common/utils/getLocalizedValue';
import {useCache} from '../contexts/CacheContext';
import {useSelector, useStateDispatch} from '../contexts/StateContext';
import selectStructureERC from '../selectors/selectStructureERC';
import selectStructureUuid from '../selectors/selectStructureUuid';
import {ObjectDefinition, ObjectDefinitions} from '../types/ObjectDefinition';
import {ReferencedStructure, Structure} from '../types/Structure';
import {Uuid} from '../types/Uuid';
import {buildReferencedStructure} from '../utils/buildStructure';
import getRandomName from '../utils/getRandomName';

type Item = {
	label: string;
	value: string;
};

export default function ReferencedStructureModal({
	onAdd,
	onCloseModal,
}: {
	onAdd: (referencedStructures: ReferencedStructure[]) => void;
	onCloseModal: () => void;
}) {
	const {observer, onClose} = useModal({
		onClose: () => onCloseModal(),
	});

	const dispatch = useStateDispatch();

	const structureUuid = useSelector(selectStructureUuid);
	const structureERC = useSelector(selectStructureERC);

	const {
		data: objectDefinitions,
		load,
		status,
	} = useCache('object-definitions');

	const [selection, setSelection] = useState<Item[]>([]);
	const [hasError, setHasError] = useState(false);

	const id = useId();

	useEffect(() => {
		if (status === 'stale') {
			load().then((objectDefinitions) =>
				dispatch({
					objectDefinitions,
					type: 'refresh-referenced-structures',
				})
			);
		}
	}, [dispatch, load, status]);

	return (
		<ClayModal observer={observer}>
			<ClayModal.Header>
				{Liferay.Language.get('referenced-content-structure')}
			</ClayModal.Header>

			<ClayModal.Body>
				<p className="text-secondary">
					{Liferay.Language.get(
						'select-the-content-structures-to-be-referenced'
					)}
				</p>

				<ClayForm.Group className={classNames({'has-error': hasError})}>
					<label htmlFor={id}>
						{Liferay.Language.get('content-structures')}

						<ClayIcon
							className="ml-1 reference-mark"
							focusable="false"
							role="presentation"
							symbol="asterisk"
						/>
					</label>

					<ClayMultiSelect
						id={id}
						items={selection}
						loadingState={status === 'saving' ? 1 : 0}
						onItemsChange={(selection: Item[]) => {
							setSelection(selection);

							setHasError(!selection.length);
						}}
						sourceItems={getItems(objectDefinitions, structureERC)}
					/>

					{hasError ? (
						<FieldFeedback
							errorMessage={Liferay.Language.get(
								'this-field-is-required'
							)}
						/>
					) : null}
				</ClayForm.Group>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={onClose}
							type="button"
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							displayType="primary"
							onClick={() => {
								if (!selection.length) {
									setHasError(true);

									return;
								}

								const structures = buildStructures(
									selection,
									objectDefinitions,
									structureUuid,
									structureERC
								);

								onAdd(structures);

								onCloseModal();
							}}
						>
							{Liferay.Language.get('add')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
}

function getItems(
	objectDefinitions: ObjectDefinitions,
	mainStructureERC: Structure['erc']
): Item[] {
	const items = [];

	// Exclude objectDefinitions that are repeatable groups,
	// main objectDefinition itself and objectDefinitions
	// that have a circular dependency with the main one

	for (const objectDefinition of Object.values(objectDefinitions)) {
		if (
			objectDefinition.externalReferenceCode === mainStructureERC ||
			objectDefinition.objectFolderExternalReferenceCode ===
				'L_CMS_STRUCTURE_REPEATABLE_GROUPS' ||
			hasCircularDependency(
				objectDefinition,
				objectDefinitions,
				mainStructureERC
			)
		) {
			continue;
		}

		items.push({
			label: getLocalizedValue(objectDefinition.label),
			value: objectDefinition.externalReferenceCode,
		});
	}

	return items;
}

function hasCircularDependency(
	objectDefinition: ObjectDefinition,
	objectDefinitions: ObjectDefinitions,
	mainStructureERC: Structure['erc']
) {
	if (!objectDefinition.objectRelationships?.length) {
		return false;
	}

	for (const relationship of objectDefinition.objectRelationships) {
		if (
			relationship.objectDefinitionExternalReferenceCode2 ===
			mainStructureERC
		) {
			return true;
		}

		const hasDependency = hasCircularDependency(
			objectDefinitions[
				relationship.objectDefinitionExternalReferenceCode2
			],
			objectDefinitions,
			mainStructureERC
		);

		if (hasDependency) {
			return true;
		}
	}

	return false;
}

function buildStructures(
	selection: Item[],
	objectDefinitions: ObjectDefinitions,
	mainStructureUuid: Uuid,
	mainStructureERC: Structure['erc']
) {
	const ercs = selection.map(({value}) => value);

	return ercs.map((erc) => {
		const structure = buildReferencedStructure({
			ancestors: [mainStructureERC],
			erc,
			objectDefinitions,
			parent: mainStructureUuid,
			relationshipName: getRandomName(),
		});

		return structure;
	});
}
