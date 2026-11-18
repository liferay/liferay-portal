/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import {FDS_EVENT} from '@liferay/frontend-data-set-web';
import {
	IBulkActionFDSData,
	IBulkActionTaskStarterDTO,
	triggerAssetBulkAction,
} from '@liferay/site-cms-site-initializer';
import React, {useCallback, useState} from 'react';

import {getProcessingBulkActionTasksCount} from '../../utils/api';
import {getDoneTasksOpenSubtasksCount} from '../../utils/subtasksUtil';
import {
	displayErrorToast,
	displayOpenSubtasksToast,
} from '../../utils/toastUtil';
import StateSelector, {State} from '../StateSelector';

function displayOpenSubtasksToastOnUpdate(
	dataSetId: string,
	selectedData: IBulkActionFDSData
) {
	const tasks = (selectedData.items ?? []).map(({embedded}) => embedded);

	if (!tasks.length) {
		return;
	}

	const handleFDSUpdateDisplay = async ({id}: {id: string}) => {
		if (id !== dataSetId) {
			return;
		}

		if (await getProcessingBulkActionTasksCount()) {
			return;
		}

		Liferay.detach(FDS_EVENT.UPDATE_DISPLAY, handleFDSUpdateDisplay);

		const openSubtasksCount = await getDoneTasksOpenSubtasksCount(tasks);

		if (openSubtasksCount) {
			displayOpenSubtasksToast(openSubtasksCount);
		}
	};

	Liferay.on(FDS_EVENT.UPDATE_DISPLAY, handleFDSUpdateDisplay);
}

export default function BulkEditStateModalContent({
	apiURL,
	closeModal,
	dataSetId,
	selectedData,
	states,
}: {
	apiURL?: string;
	closeModal: () => void;
	dataSetId: string;
	selectedData: IBulkActionFDSData;
	states: State[];
}) {
	const [selectedState, setSelectedState] = useState<string>('');
	const [submitDisabled, setSubmitDisabled] = useState<boolean>(false);

	const doBulkSubmit = useCallback(async () => {
		if (!selectedState) {
			return;
		}

		setSubmitDisabled(true);

		triggerAssetBulkAction({
			apiURL,
			dataSetId,
			keyValues: {
				status: selectedState,
			},
			onCreateError: ({error}) => {
				setSubmitDisabled(false);

				displayErrorToast(error as string);
			},
			onCreateSuccess: ({error = ''}) => {
				if (error) {
					setSubmitDisabled(false);

					displayErrorToast(error as string);

					return;
				}

				if (selectedState === 'done' && !selectedData.selectAll) {
					displayOpenSubtasksToastOnUpdate(dataSetId, selectedData);
				}

				closeModal();
			},
			overrideDefaultErrorToast: true,
			selectedData,
			type: 'StatusObjectBulkSelectionAction',
		} as IBulkActionTaskStarterDTO<'StatusObjectBulkSelectionAction'>);
	}, [
		apiURL,
		closeModal,
		dataSetId,
		selectedData,
		selectedState,
		setSubmitDisabled,
	]);

	return (
		<>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('update-state')}
			</ClayModal.Header>

			<ClayModal.Body>
				<label>{Liferay.Language.get('state')}</label>

				<StateSelector
					initialSelectedKey=""
					onChange={async (key: string) => {
						setSelectedState(key);
					}}
					states={states}
					triggerClassName="form-control form-control-select"
				/>
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
							disabled={submitDisabled}
							displayType="primary"
							onClick={doBulkSubmit}
							type="button"
						>
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
}
