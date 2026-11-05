/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Text} from '@clayui/core';
import ClayLayout from '@clayui/layout';
import {sub} from 'frontend-js-web';
import React, {useId, useState} from 'react';

import {Group, GroupType} from '../../../types/exportImportPreview';
import {ExportImportProcess} from '../../../types/exportImportProcess';
import SectionTags from '../content_selector/SectionTags';
import GroupSelectorModal from './GroupSelectorModal';

const LABELS = {
	site: {
		empty: Liferay.Language.get('no-sites-are-selected'),
		select: Liferay.Language.get('select-sites'),
		title: Liferay.Language.get('sites'),
	},
	space: {
		empty: Liferay.Language.get('no-spaces-are-selected'),
		select: Liferay.Language.get('select-spaces'),
		title: Liferay.Language.get('spaces'),
	},
};

function toEntries(groups: Group[]) {
	return groups.map(
		(group) => [group.externalReferenceCode, group] as [string, Group]
	);
}

export default function GroupsControl({
	apiURL,
	groups,
	onChange,
	process = 'export',
	selectedExternalReferenceCodes,
	totalCount,
	type,
}: {
	apiURL?: string;
	groups?: Group[];
	onChange: (externalReferenceCodes: string[]) => void;
	process?: Exclude<ExportImportProcess, 'publish'>;
	selectedExternalReferenceCodes: string[];
	totalCount?: number;
	type: GroupType;
}) {
	const descriptionId = useId();

	const [showModal, setShowModal] = useState(false);

	const [
		knownGroupsByExternalReferenceCode,
		setKnownGroupsByExternalReferenceCode,
	] = useState(() => new Map(toEntries(groups ?? [])));

	const labels = LABELS[type];

	const selectedGroups = selectedExternalReferenceCodes.map(
		(externalReferenceCode) =>
			knownGroupsByExternalReferenceCode.get(externalReferenceCode) ?? {
				externalReferenceCode,
			}
	);

	const description = selectedGroups.length
		? sub(
				Liferay.Language.get('selected-x'),
				selectedGroups
					.map(
						({descriptiveName, externalReferenceCode}) =>
							descriptiveName || externalReferenceCode
					)
					.join(', ')
			)
		: labels.empty;

	return (
		<>
			<ClayLayout.ContentRow verticalAlign="center">
				<Text size={6} weight="bold">
					{labels.title}
				</Text>

				<SectionTags additionCount={totalCount} />
			</ClayLayout.ContentRow>

			<Text as="p" color="secondary" id={descriptionId} size={3} truncate>
				{description}
			</Text>

			<ClayButton
				aria-describedby={descriptionId}
				displayType="primary"
				onClick={() => setShowModal(true)}
				outline
				size="sm"
			>
				{labels.select}
			</ClayButton>

			{showModal && (
				<GroupSelectorModal
					apiURL={apiURL}
					groups={groups}
					onClose={() => setShowModal(false)}
					onSubmit={(nextSelectedGroups) => {
						setKnownGroupsByExternalReferenceCode(
							(previousKnownGroupsByExternalReferenceCode) =>
								new Map([
									...previousKnownGroupsByExternalReferenceCode,
									...toEntries(nextSelectedGroups),
								])
						);

						onChange(
							nextSelectedGroups.map(
								({externalReferenceCode}) =>
									externalReferenceCode
							)
						);
					}}
					process={process}
					selectedGroups={selectedGroups}
					title={labels.select}
					type={type}
				/>
			)}
		</>
	);
}
