/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal, {useModal} from '@clayui/modal';
import {useId} from 'frontend-js-components-web';
import React, {useState} from 'react';
import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';

import {ItemList} from '../fragments_and_widgets/components/reorder_sets_modal/ItemList';

import './AudiencesPriorityModal.scss';

const DragAndDropProvider = DndProvider as React.FC<
	React.PropsWithChildren<{backend: typeof HTML5Backend}>
>;

interface Audience {
	label: string;
	value: string;
}

interface AudiencesPriorityModalProps {
	audiences: Audience[];
	onClose: () => void;
	onSave: (audiences: Audience[]) => void;
}

export default function AudiencesPriorityModal({
	audiences,
	onClose: onCloseModal,
	onSave,
}: AudiencesPriorityModalProps) {
	const {observer, onClose} = useModal({onClose: onCloseModal});

	const audiencesListId = useId();

	const [orderedAudiences, setOrderedAudiences] = useState(audiences);

	return (
		<DragAndDropProvider backend={HTML5Backend}>
			<ClayModal
				className="element-variations__audiences-priority-modal"
				observer={observer}
			>
				<ClayModal.Header>
					{Liferay.Language.get('audiences-priority')}
				</ClayModal.Header>

				<ClayModal.Body className="p-0">
					<p className="m-0 p-4 text-secondary">
						{Liferay.Language.get(
							'prioritize-your-audiences-to-determine-what-a-user-who-belongs-to-multiple-audiences-at-the-same-time-will-see-with-the-first-audience-taking-precedence'
						)}
					</p>

					<ItemList
						items={orderedAudiences.map(({label, value}) => ({
							id: value,
							name: label,
						}))}
						listId={audiencesListId}
						updateLists={(listId, items) =>
							setOrderedAudiences(
								items.map(({id, name}) => ({
									label: name,
									value: id,
								}))
							)
						}
					/>
				</ClayModal.Body>

				<ClayModal.Footer
					last={
						<ClayButton.Group spaced>
							<ClayButton
								displayType="secondary"
								onClick={onClose}
							>
								{Liferay.Language.get('cancel')}
							</ClayButton>

							<ClayButton
								displayType="primary"
								onClick={() => {
									onSave(orderedAudiences);

									onClose();
								}}
							>
								{Liferay.Language.get('save')}
							</ClayButton>
						</ClayButton.Group>
					}
				/>
			</ClayModal>
		</DragAndDropProvider>
	);
}
