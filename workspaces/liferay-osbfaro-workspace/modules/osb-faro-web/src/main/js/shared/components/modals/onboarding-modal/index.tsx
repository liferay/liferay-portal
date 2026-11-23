import ClayModal from '@clayui/modal';
import ConfigureWorkspace from './ConfigureWorkspace';
import ConnectDXP from './ConnectDXP';
import InvitePeople from './InvitePeople';
import React, {useEffect, useState} from 'react';
import type {Observer} from '@clayui/modal/lib/types';
import ReadyToGo from './ReadyToGo';
import Welcome from './Welcome';

const MODAL_SCREENS = [
	Welcome,
	ConfigureWorkspace,
	ConnectDXP,
	InvitePeople,
	ReadyToGo,
];

const ONBOARDING_STATE_KEY = 'onboardingState';

export interface IOnboardingState {
	dxpConnected: boolean;
	groupId: string;
	step: number;
}

const isOnboardingState = (value: unknown): value is IOnboardingState =>
	typeof value === 'object' &&
	value !== null &&
	'dxpConnected' in value &&
	typeof value.dxpConnected === 'boolean' &&
	'groupId' in value &&
	typeof value.groupId === 'string' &&
	'step' in value &&
	typeof value.step === 'number';

/**
 * Returns the onboarding a previous page of the workspace left open, so the
 * modal resumes on the step where the page was left.
 */
export const getOnboardingState = (
	groupId: string
): IOnboardingState | null => {
	try {
		const onboardingState: unknown = JSON.parse(
			window.sessionStorage.getItem(ONBOARDING_STATE_KEY) ?? 'null'
		);

		if (
			isOnboardingState(onboardingState) &&
			onboardingState.groupId === groupId
		) {
			return onboardingState;
		}
	}
	catch (error) {}

	return null;
};

interface IOnboardingModalProps {
	groupId: string;
	initialState?: IOnboardingState | null;
	observer: Observer;
	onClose: () => void;
}

const OnboardingModal: React.FC<IOnboardingModalProps> = ({
	groupId,
	initialState,
	observer,
	onClose,
}) => {
	const [dxpConnected, setDxpConnected] = useState(
		initialState?.dxpConnected ?? false
	);
	const [step, setStep] = useState(initialState?.step ?? 0);

	useEffect(() => {
		try {
			window.sessionStorage.setItem(
				ONBOARDING_STATE_KEY,
				JSON.stringify({dxpConnected, groupId, step})
			);
		}
		catch (error) {}
	}, [dxpConnected, groupId, step]);

	useEffect(
		() => () => {
			try {
				window.sessionStorage.removeItem(ONBOARDING_STATE_KEY);
			}
			catch (error) {}
		},
		[]
	);

	const ScreenComponent = MODAL_SCREENS[step];

	return (
		<ClayModal observer={observer}>
			<ScreenComponent
				dxpConnected={dxpConnected}
				groupId={groupId}
				onboarding
				onClose={onClose}
				onDxpConnected={setDxpConnected}
				onNext={(increment = 1) => setStep(step + increment)}
				onPrevious={() => setStep(step - 1)}
			/>
		</ClayModal>
	);
};

export default OnboardingModal;
