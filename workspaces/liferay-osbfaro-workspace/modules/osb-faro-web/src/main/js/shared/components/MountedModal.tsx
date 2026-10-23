import React, {useEffect} from 'react';
import type {Observer} from '@clayui/modal/lib/types';
import {useModal} from '@clayui/modal';

interface IMountedModalProps {
	children: (observer: Observer, close: () => void) => React.ReactNode;
	onClose: () => void;
}

/**
 * A modal that is open for as long as this component is mounted, for the
 * class components that cannot use the `useModal` hook. `onClose` runs once
 * the modal has finished closing, so the caller can unmount it.
 */
const MountedModal: React.FC<IMountedModalProps> = ({children, onClose}) => {
	const {observer, onOpenChange, open} = useModal({defaultOpen: true});

	useEffect(() => {
		if (!open) {
			onClose();
		}
	}, [open]);

	return <>{children(observer, () => onOpenChange(false))}</>;
};

export default MountedModal;
