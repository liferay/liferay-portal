import ConfirmationModal, {
	IConfirmationModalProps,
} from 'shared/components/ConfirmationModal';
import React, {useEffect} from 'react';
import {useModal} from '@clayui/modal';

type IProps = Omit<IConfirmationModalProps, 'observer'>;

/**
 * A confirmation that is open for as long as it is mounted, for the class
 * components that cannot use the `useModal` hook. `onClose` runs once the
 * modal has finished closing, so the caller can unmount it.
 */
const ConfirmationDialog: React.FC<IProps> = ({onClose, ...otherProps}) => {
	const {observer, onOpenChange, open} = useModal({defaultOpen: true});

	useEffect(() => {
		if (!open) {
			onClose();
		}
	}, [open]);

	return (
		<ConfirmationModal
			{...otherProps}
			observer={observer}
			onClose={() => onOpenChange(false)}
		/>
	);
};

export default ConfirmationDialog;
