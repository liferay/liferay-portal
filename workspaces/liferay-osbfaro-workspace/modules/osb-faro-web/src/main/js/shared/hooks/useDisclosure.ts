import {Dispatch, SetStateAction, useCallback, useState} from 'react';
interface IUseDisclosure {
	initialOpen?: boolean;
}
export function useDisclosure({initialOpen = false}: IUseDisclosure = {}): {
	close: () => void;
	isOpen: boolean;
	open: () => void;
	setIsOpen: Dispatch<SetStateAction<boolean>>;
	toggle: () => void;
} {
	const [isOpen, setIsOpen] = useState(initialOpen);

	const close = useCallback(() => setIsOpen(false), []);

	const open = useCallback(() => setIsOpen(true), []);

	const toggle = useCallback(() => setIsOpen((isOpen) => !isOpen), []);

	return {close, isOpen, open, setIsOpen, toggle};
}
