import ClayModal from '@clayui/modal';
import IssueSubmitted from './IssueSubmitted';
import React, {useState} from 'react';
import type {Observer} from '@clayui/modal/lib/types';
import ReportIssue from './ReportIssue';

const MODAL_SCREENS = [ReportIssue, IssueSubmitted];

interface IHelpWidgetModalProps {
	groupId: string;
	observer: Observer;
	onClose: () => void;
}

const HelpWidgetModal: React.FC<IHelpWidgetModalProps> = ({
	groupId,
	observer,
	onClose,
}) => {
	const [step, setStep] = useState(0);

	const ScreenComponent = MODAL_SCREENS[step];

	return (
		<ClayModal observer={observer}>
			<ScreenComponent
				groupId={groupId}
				onClose={onClose}
				onNext={(increment = 1) => setStep(step + increment)}
			/>
		</ClayModal>
	);
};

export default HelpWidgetModal;
