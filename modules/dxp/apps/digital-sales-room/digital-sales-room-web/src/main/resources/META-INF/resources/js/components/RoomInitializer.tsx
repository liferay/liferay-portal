/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import React, {SetStateAction, useCallback, useState} from 'react';

import RoomService from '../common/services/RoomService';
import {displayErrorToast} from '../common/utils/toastUtil';
import {
	IRoomContext,
	IRoomDataContext,
	IRoomInitializerProps,
	ISiteTemplate,
} from '../common/utils/types';
import RoomNameStep from './RoomNameStep';
import RoomSelectAccountStep from './RoomSelectAccountStep';
import RoomSelectTemplateStep from './RoomSelectTemplateStep';

const DEFAULT_DATA_CONTEXT: IRoomDataContext = {
	errors: {},
	friendlyURL: '',
	roomName: '',
	share: {
		emailAddresses: [],
	},
};

const STEPS_DEFINITION = [
	{
		numberOfSteps: 1,
		steps: [RoomSelectAccountStep],
	},
	{
		numberOfSteps: 2,
		steps: [RoomSelectAccountStep, RoomSelectTemplateStep],
	},
	{
		numberOfSteps: 3,
		steps: [RoomSelectAccountStep, RoomSelectTemplateStep, RoomNameStep],
	},
];

export const RoomContext = React.createContext<IRoomContext>({
	dataContext: DEFAULT_DATA_CONTEXT,
	loading: false,
	setDataContext: () => {},
	setLoading: () => {},
});

export function getFriendlyURL(friendlyURL: string) {
	if (!friendlyURL) {
		return '';
	}

	if (friendlyURL.startsWith('/')) {
		return friendlyURL;
	}

	return `/${friendlyURL}`;
}

function StepLoader({
	numberOfSteps,
	setHandleStepSubmit,
	siteTemplates = [],
	step,
}: {
	numberOfSteps: number;
	setHandleStepSubmit: (
		callback: SetStateAction<(event: Event) => Promise<boolean>>
	) => void;
	siteTemplates: Array<ISiteTemplate>;
	step: number;
}) {
	const stepDefinition = STEPS_DEFINITION.find(
		(item) => item.numberOfSteps === numberOfSteps
	);

	if (stepDefinition) {
		const Component = stepDefinition.steps[step - 1];

		return (
			<Component
				numberOfSteps={numberOfSteps}
				setHandleStepSubmit={setHandleStepSubmit}
				siteTemplates={siteTemplates}
				step={step}
			></Component>
		);
	}

	return <div></div>;
}

function RoomInitializer({
	closeModal,
	createRedirectURL = '',
	numberOfSteps = 3,
	siteTemplates = [],
}: IRoomInitializerProps) {
	const [dataContext, setDataContext] = useState(DEFAULT_DATA_CONTEXT);
	const [handleStepSubmit, setHandleStepSubmit] = useState(
		() =>
			async (event: Event): Promise<boolean> => {
				event.preventDefault();

				return false;
			}
	);
	const [loading, setLoading] = useState(false);
	const [step, setStep] = useState(1);

	const handleBack = useCallback(() => {
		setStep((prevState) => {
			return Math.max(prevState - 1, 1);
		});
	}, []);

	const handleCancel = useCallback(() => {
		closeModal();
	}, [closeModal]);

	const handleNext = useCallback(
		async (event: any) => {
			setLoading(true);

			try {
				const stepValid = await handleStepSubmit(event);

				if (stepValid) {
					setStep((prevState) => {
						return prevState + 1;
					});
				}
			}
			finally {
				setLoading(false);
			}
		},
		[handleStepSubmit]
	);

	const handleSave = useCallback(
		async (event: any) => {
			setLoading(true);

			try {
				const stepValid = await handleStepSubmit(event);

				if (stepValid) {
					let room = await RoomService.addRoom({
						accountEntryId: dataContext.accountId || 0,
						friendlyURL: dataContext.friendlyURL || '',
						name: dataContext.roomName,
						siteTemplateKey: dataContext.templateKey || '',
					});

					for (let i = 0; i < 10; i++) {
						await new Promise((resolve) =>
							setTimeout(resolve, 2000)
						);

						room = await RoomService.getRoom(room.id);

						if (room.initialized) {
							break;
						}
					}

					window.location.href = createRedirectURL.replace(
						/{siteId}/,
						String(room.siteId)
					);
				}
			}
			catch (error) {
				displayErrorToast((error as Error).message);
			}
			finally {
				setLoading(false);
			}
		},
		[createRedirectURL, dataContext, handleStepSubmit]
	);

	return (
		<RoomContext.Provider
			value={{dataContext, loading, setDataContext, setLoading}}
		>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				<>{Liferay.Language.get('create-new-digital-sales-room')}</>
			</ClayModal.Header>

			<ClayModal.Body>
				<StepLoader
					numberOfSteps={numberOfSteps}
					setHandleStepSubmit={setHandleStepSubmit}
					siteTemplates={siteTemplates}
					step={step}
				/>
			</ClayModal.Body>

			<ClayModal.Footer
				first={
					<ClayButton
						className="text-secondary"
						data-testid="button-cancel"
						disabled={loading}
						displayType="link"
						onClick={handleCancel}
					>
						{Liferay.Language.get('cancel')}
					</ClayButton>
				}
				last={
					<ClayButton.Group spaced>
						{step > 1 && (
							<ClayButton
								data-testid="button-back"
								disabled={loading}
								displayType="secondary"
								onClick={handleBack}
							>
								{Liferay.Language.get('back')}
							</ClayButton>
						)}

						{step < numberOfSteps && (
							<ClayButton
								data-testid="button-next"
								disabled={loading}
								onClick={handleNext}
							>
								{Liferay.Language.get('next')}
							</ClayButton>
						)}

						{step >= numberOfSteps && (
							<ClayButton
								data-testid="button-save"
								disabled={loading}
								onClick={handleSave}
							>
								{Liferay.Language.get('save')}
							</ClayButton>
						)}
					</ClayButton.Group>
				}
			/>
		</RoomContext.Provider>
	);
}

export default RoomInitializer;
