/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm, {ClaySelectWithOption} from '@clayui/form';
import ClayModal from '@clayui/modal';
import React, {useId, useState} from 'react';

import SpaceService from '../../../common/services/SpaceService';
import {Space} from '../../../common/types/Space';
import {openCMSModal} from '../../../common/utils/openCMSModal';

interface ContentType {
	externalReferenceCode: string;
	label: string;
	name: string;
}

interface GenerateContentWithAIData {
	action: 'generateContentWithAI';
	contentTypes: string;
}

function openChat(contentTypes: ContentType[], spaceId?: string) {
	Liferay.fire('openAIAssistantChat', {
		contentTypes,
		context: {spaceId},
	});
}

function SpaceSelectionModalContent({
	closeModal,
	contentTypes,
	spaces,
}: {
	closeModal: () => void;
	contentTypes: ContentType[];
	spaces: Space[];
}) {
	const [siteId, setSiteId] = useState(String(spaces[0].siteId));

	const selectId = useId();

	return (
		<>
			<ClayModal.Header>
				{Liferay.Language.get('select-a-space')}
			</ClayModal.Header>

			<ClayModal.Body>
				<ClayForm.Group>
					<label htmlFor={selectId}>
						{Liferay.Language.get('space')}
					</label>

					<ClaySelectWithOption
						id={selectId}
						onChange={(event) => setSiteId(event.target.value)}
						options={spaces.map((space) => ({
							label: space.name,
							value: String(space.siteId),
						}))}
						value={siteId}
					/>
				</ClayForm.Group>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={closeModal}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							displayType="primary"
							onClick={() => {
								closeModal();

								openChat(contentTypes, siteId);
							}}
						>
							{Liferay.Language.get('select')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
}

export default function generateContentWithAIAction(
	data: GenerateContentWithAIData
) {
	const contentTypes: ContentType[] = JSON.parse(data.contentTypes);

	SpaceService.getSpaces()
		.then((spaces) => {
			if (spaces.length <= 1) {
				openChat(
					contentTypes,
					spaces[0] ? String(spaces[0].siteId) : undefined
				);

				return;
			}

			openCMSModal({
				center: true,
				contentComponent: ({closeModal}: {closeModal: () => void}) => (
					<SpaceSelectionModalContent
						closeModal={closeModal}
						contentTypes={contentTypes}
						spaces={spaces}
					/>
				),
				size: 'sm',
			});
		})
		.catch((error) => {
			console.error('Failed to retrieve spaces:', error);
		});
}
