/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import {openToast} from 'frontend-js-components-web';
import React, {SetStateAction, useCallback, useState} from 'react';

import ApiHelper from '../commons/ApiHelper';
import DSRRoomDetailsStep from './DSRRoomDetailsStep';
import DSRRoomSelectTemplateStep from './DSRRoomSelectTemplateStep';
import DSRRoomSettingsStep from './DSRRoomSettingsStep';
import DSRShareRoomStep from './DSRShareRoomStep';
import {TDSRContext, TDSRDataContext, TDSRInitializerProps} from './DSRTypes';

const DEFAULT_DATA_CONTEXT: TDSRDataContext = {
	banner: {},
	clientLogo: {},
	clientName: '',
	errors: {},
	friendlyURL: '',
	primaryColor: '0B5FFF',
	roomName: '',
	secondaryColor: 'FFFFFF',
	share: {
		emailAddresses: [],
	},
};

const STEPS_DEFINITION = [
	{
		numberOfSteps: 1,
		steps: [DSRRoomDetailsStep],
	},
	{
		numberOfSteps: 2,
		steps: [DSRRoomDetailsStep, DSRRoomSettingsStep],
	},
	{
		numberOfSteps: 3,
		steps: [DSRRoomDetailsStep, DSRRoomSettingsStep, DSRShareRoomStep],
	},
	{
		numberOfSteps: 4,
		steps: [
			DSRRoomSelectTemplateStep,
			DSRRoomDetailsStep,
			DSRRoomSettingsStep,
			DSRShareRoomStep,
		],
	},
];

export const DSRContext = React.createContext<TDSRContext>({
	dataContext: DEFAULT_DATA_CONTEXT,
	loading: false,
	setDataContext: () => {},
	setLoading: () => {},
});

export function getColor(color: string) {
	if (!color) {
		return '';
	}

	if (color.startsWith('#')) {
		return color;
	}

	if (/^([0-9A-F]{3}){1,2}$/i.test(color)) {
		return `#${color}`;
	}

	return color;
}

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
	step,
}: {
	numberOfSteps: number;
	setHandleStepSubmit: (
		callback: SetStateAction<(event: Event) => Promise<boolean>>
	) => void;
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
				step={step}
			></Component>
		);
	}

	return <div></div>;
}

function DSRInitializer({closeModal, numberOfSteps = 3}: TDSRInitializerProps) {
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
					const payload = {
						name: dataContext.roomName,
						r_accountToDSRRooms_accountEntryId:
							dataContext.accountId || 0,
					};

					const {error} = await ApiHelper.post<{
						id: number;
						name: string;
					}>('/o/digital-sales-room/rooms/', payload);

					if (error) {
						throw new Error(error);
					}

					const friendlyURL =
						'/' +
						dataContext.roomName
							.toLowerCase()
							.replace(/[^a-z0-9]+/g, '-')
							.replace(/^-|-$/g, '');

					window.location.href = `/web${friendlyURL}?p_l_back_url=/web${friendlyURL}&p_l_mode=edit`;
				}
			}
			catch (error) {
				openToast({
					message: (error as Error).message,
					type: 'danger',
				});
			}
			finally {
				setLoading(false);
			}
		},
		[dataContext, handleStepSubmit]
	);

	return (
		<DSRContext.Provider
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
		</DSRContext.Provider>
	);
}

export default DSRInitializer;
