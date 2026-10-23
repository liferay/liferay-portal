import React, {useEffect, useRef} from 'react';

interface IModalAutoFocusProps {
	selector: string;
}

/**
 * Clay focuses the title of a modal once its content mounts, which blurs a
 * field that uses `autoFocus` and marks it as touched, so its validation error
 * shows as soon as the modal opens. Render this inside the modal instead of
 * using `autoFocus`: it focuses the field after Clay has moved the focus.
 */
const ModalAutoFocus: React.FC<IModalAutoFocusProps> = ({selector}) => {
	const anchorRef = useRef<HTMLSpanElement>(null);

	useEffect(() => {
		const timeoutId = window.setTimeout(() => {
			anchorRef.current
				?.closest('.modal')
				?.querySelector<HTMLElement>(selector)
				?.focus();
		});

		return () => window.clearTimeout(timeoutId);
	}, [selector]);

	return <span hidden ref={anchorRef} />;
};

export default ModalAutoFocus;
