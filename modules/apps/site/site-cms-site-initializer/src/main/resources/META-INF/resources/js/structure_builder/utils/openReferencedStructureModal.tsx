/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayModal from '@clayui/modal';
import ClayMultiSelect from '@clayui/multi-select';
import {isNullOrUndefined} from '@liferay/layout-js-components-web';
import classNames from 'classnames';
import {FieldFeedback, openModal, useId} from 'frontend-js-components-web';
import React, {Dispatch, useState} from 'react';

import {
	ObjectDefinition,
	ObjectDefinitions,
} from '../../common/types/ObjectDefinition';
import getLocalizedValue from '../../common/utils/getLocalizedValue';
import {CacheStatus} from '../contexts/CacheContext';
import {Action} from '../contexts/StateContext';
import {ReferencedStructure, Structure} from '../types/Structure';
import {SystemFieldNames} from '../types/SystemFieldNames';
import {Uuid} from '../types/Uuid';
import {buildReferencedStructure} from '../utils/buildStructure';
import getRandomId from '../utils/getRandomId';
import getRandomName from '../utils/getRandomName';

type Item = {
	label: string;
	value: string;
};

export default function openReferencedStructureModal({
	dispatch,
	objectDefinitions,
	parentUuid,
	status,
	structure,
	systemFieldNames,
}: {
	dispatch: Dispatch<Action>;
	objectDefinitions: ObjectDefinitions;
	parentUuid: Uuid;
	status: CacheStatus;
	structure: Structure;
	systemFieldNames: SystemFieldNames;
}) {
	const addReferencedStructures = (
		referencedStructures: ReferencedStructure[]
	) =>
		dispatch({
			referencedStructures,
			type: 'add-referenced-structures',
		});

	openModal({
		center: true,
		contentComponent: ({closeModal}: {closeModal: () => void}) => (
			<ReferencedStructureModal
				closeModal={closeModal}
				objectDefinitions={objectDefinitions}
				onAdd={addReferencedStructures}
				parentUuid={parentUuid}
				status={status}
				structure={structure}
				systemFieldNames={systemFieldNames}
			/>
		),
	});
}

export function ReferencedStructureModal({
	closeModal,
	objectDefinitions,
	onAdd,
	parentUuid,
	status,
	structure,
	systemFieldNames,
}: {
	closeModal: () => void;
	objectDefinitions: ObjectDefinitions;
	onAdd: (referencedStructures: ReferencedStructure[]) => void;
	parentUuid: Uuid;
	status: CacheStatus;
	structure: Structure;
	systemFieldNames: SystemFieldNames;
}) {
	const [selection, setSelection] = useState<Item[]>([]);
	const [hasError, setHasError] = useState(false);

	const id = useId();
	const sourceItems = getItems(objectDefinitions, structure.erc);

	return (
		<>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
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
							const newSelection = selection
								.map((item) =>
									sourceItems.find(
										(sourceItem) =>
											sourceItem.label.toLowerCase() ===
											item.label.toLowerCase()
									)
								)
								.filter(
									(item): item is Item =>
										!isNullOrUndefined(item)
								);

							setSelection(newSelection);
							setHasError(!newSelection.length);
						}}
						sourceItems={sourceItems}
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
							onClick={closeModal}
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

								const structures = buildStructures({
									mainStructureERC: structure.erc,
									objectDefinitions,
									parentUuid,
									selection,
									systemFieldNames,
								});

								onAdd(structures);

								closeModal();
							}}
						>
							{Liferay.Language.get('add')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
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
		if (relationship.deletionType !== 'cascade') {
			continue;
		}

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

function buildStructures({
	mainStructureERC,
	objectDefinitions,
	parentUuid,
	selection,
	systemFieldNames,
}: {
	mainStructureERC: Structure['erc'];
	objectDefinitions: ObjectDefinitions;
	parentUuid: Uuid;
	selection: Item[];
	systemFieldNames: SystemFieldNames;
}) {
	const ercs = selection.map(({value}) => value);

	return ercs.map((erc) => {
		const structure = buildReferencedStructure({
			ancestors: [mainStructureERC],
			erc,
			objectDefinitions,
			parent: parentUuid,
			relationshipERC: getRandomId(),
			relationshipName: getRandomName(),
			systemFieldNames,
		});

		return structure;
	});
}
