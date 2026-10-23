import ClayModal from '@clayui/modal';
import ConfigureWorkspace from './ConfigureWorkspace';
import ConnectDXP from './ConnectDXP';
import InvitePeople from './InvitePeople';
import React, {useState} from 'react';
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

interface IOnboardingModalProps {
	groupId: string;
	observer: Observer;
	onClose: () => void;
}

const OnboardingModal: React.FC<IOnboardingModalProps> = ({
	groupId,
	observer,
	onClose,
}) => {
	const [dxpConnected, setDxpConnected] = useState(false);
	const [step, setStep] = useState(0);

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
