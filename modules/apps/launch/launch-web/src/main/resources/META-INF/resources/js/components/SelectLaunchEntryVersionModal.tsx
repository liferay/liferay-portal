/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal, {useModal} from '@clayui/modal';
import ClayTable from '@clayui/table';
import React, {useState} from 'react';

import {LaunchEntryVersion} from '../api/launches';
import WorkflowStatusLabel from './WorkflowStatusLabel';

interface Props {
	baseVersionKey?: string;
	launchEntryVersions: LaunchEntryVersion[];
	onClose: () => void;
	onSelect: (versionKey: string) => void;
}

export default function SelectLaunchEntryVersionModal({
	baseVersionKey,
	launchEntryVersions,
	onClose: onCloseModal,
	onSelect,
}: Props) {
	const [versionKey, setVersionKey] = useState(() => {
		if (
			baseVersionKey &&
			launchEntryVersions.some(
				(launchEntryVersion) =>
					launchEntryVersion.versionKey === baseVersionKey
			)
		) {
			return baseVersionKey;
		}

		return launchEntryVersions[0]?.versionKey ?? '';
	});

	const {observer, onClose} = useModal({onClose: onCloseModal});

	return (
		<ClayModal observer={observer} size="lg">
			<ClayModal.Header>
				{Liferay.Language.get('select-version')}
			</ClayModal.Header>

			<ClayModal.Body>
				<ClayTable>
					<ClayTable.Head>
						<ClayTable.Row>
							<ClayTable.Cell headingCell />

							<ClayTable.Cell headingCell>
								{Liferay.Language.get('version')}
							</ClayTable.Cell>

							<ClayTable.Cell headingCell>
								{Liferay.Language.get('status')}
							</ClayTable.Cell>

							<ClayTable.Cell headingCell>
								{Liferay.Language.get('author')}
							</ClayTable.Cell>

							<ClayTable.Cell headingCell>
								{Liferay.Language.get('modified')}
							</ClayTable.Cell>
						</ClayTable.Row>
					</ClayTable.Head>

					<ClayTable.Body>
						{launchEntryVersions.map((launchEntryVersion) => (
							<ClayTable.Row
								key={launchEntryVersion.versionKey}
								onClick={() =>
									setVersionKey(launchEntryVersion.versionKey)
								}
							>
								<ClayTable.Cell>
									<input
										checked={
											versionKey ===
											launchEntryVersion.versionKey
										}
										onChange={() =>
											setVersionKey(
												launchEntryVersion.versionKey
											)
										}
										type="radio"
									/>
								</ClayTable.Cell>

								<ClayTable.Cell>
									{launchEntryVersion.label}
								</ClayTable.Cell>

								<ClayTable.Cell>
									<WorkflowStatusLabel
										workflowStatus={
											launchEntryVersion.status
										}
									/>
								</ClayTable.Cell>

								<ClayTable.Cell>
									{launchEntryVersion.userName}
								</ClayTable.Cell>

								<ClayTable.Cell>
									{new Date(
										launchEntryVersion.modified
									).toLocaleString()}
								</ClayTable.Cell>
							</ClayTable.Row>
						))}
					</ClayTable.Body>
				</ClayTable>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton displayType="secondary" onClick={onClose}>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							disabled={!versionKey}
							onClick={() => {
								onSelect(versionKey);

								onClose();
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
