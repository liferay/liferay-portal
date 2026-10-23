import ClayModal from '@clayui/modal';
import AssignSegments from './AssignSegments';
import React, {useState} from 'react';
import type {Observer} from '@clayui/modal/lib/types';
import Welcome from './Welcome';
import {MODAL_SIZE_XL} from 'shared/components/modals/sizes';

const MODAL_SCREENS = [Welcome, AssignSegments];

interface IUnassignedSegmentsModalProps {
	groupId: string;
	observer: Observer;
	onClose: () => void;
}

const UnassignedSegmentsModal: React.FC<IUnassignedSegmentsModalProps> = ({
	groupId,
	observer,
	onClose,
}) => {
	const [step, setStep] = useState(0);

	const ScreenComponent = MODAL_SCREENS[step];

	return (
		<ClayModal disableAutoClose observer={observer} size={MODAL_SIZE_XL}>
			<ScreenComponent
				groupId={groupId}
				onClose={onClose}
				onNext={(increment = 1) => setStep(step + increment)}
			/>
		</ClayModal>
	);
};

export default UnassignedSegmentsModal;
