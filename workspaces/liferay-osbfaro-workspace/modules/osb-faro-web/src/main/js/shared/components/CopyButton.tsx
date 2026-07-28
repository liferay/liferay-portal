import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import Clipboard from 'clipboard';
import React, {useEffect, useRef, useState} from 'react';
import {ButtonProps} from '@clayui/button';

interface ICopyButtonProps {
	buttonText?: string;
	className?: string;
	displayType?: ButtonProps['displayType'];
	onClick?: (event: React.MouseEvent) => void;
	position?: string;
	text: string;
}

const CopyButton: React.FC<ICopyButtonProps> = ({
	buttonText,
	displayType,
	onClick,
	text,
	...otherProps
}) => {
	const [title, setTitle] = useState(Liferay.Language.get('click-to-copy'));
	const buttonRef = useRef(null);

	useEffect(() => {
		if (!buttonRef.current) {
			return;
		}

		// Bind to the button itself instead of the '[data-clipboard-text]'
		// selector: a selector delegates the listener to document.body, which
		// never sees the click when an ancestor stops its propagation (as the
		// table row actions do to keep the row from being selected).

		const _clipboard = new Clipboard(buttonRef.current);

		_clipboard.on('success', (event) => {
			setTitle(Liferay.Language.get('copied'));

			event.clearSelection();
		});

		return () => _clipboard.destroy();
	}, []);

	return (
		<ClayButton
			aria-label={Liferay.Language.get('click-to-copy')}
			className="button-root"
			data-clipboard-text={text}
			displayType={displayType}
			onClick={onClick}
			ref={buttonRef}
			title={title}
			{...otherProps}
		>
			{buttonText || <ClayIcon className="icon-root" symbol="copy" />}
		</ClayButton>
	);
};

export default CopyButton;
