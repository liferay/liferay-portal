import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import Clipboard from 'clipboard';
import React, {useEffect, useRef, useState} from 'react';
import {addAlert} from 'shared/actions/alerts';
import {Alert} from 'shared/types';
import {ButtonProps} from '@clayui/button';
import {useDispatch} from 'react-redux';

interface ICopyButtonProps {
	borderless?: ButtonProps['borderless'];
	buttonText?: string;
	className?: string;
	displayType?: ButtonProps['displayType'];

	/**
	 * Names what the button copies, for the tooltip and the accessible label.
	 * Defaults to the generic "Click to Copy" for a button whose surroundings
	 * already say what is being copied.
	 */
	label?: string;
	monospaced?: ButtonProps['monospaced'];
	onClick?: (event: React.MouseEvent) => void;

	/**
	 * Runs once the text has actually reached the clipboard, for a caller with
	 * its own state to settle. The button already announces the copy itself, so
	 * this is not the place to do that. A failed copy never calls it.
	 */
	onCopy?: () => void;
	position?: string;
	size?: ButtonProps['size'];
	text: string;
}

const CopyButton: React.FC<ICopyButtonProps> = ({
	buttonText,
	displayType,
	label = Liferay.Language.get('click-to-copy'),
	onClick,
	onCopy,
	text,
	...otherProps
}) => {
	const [copied, setCopied] = useState(false);
	const buttonRef = useRef(null);
	const dispatch = useDispatch();

	// The clipboard is bound once, so the handler would otherwise close over
	// the callback from the first render. Reading it from a ref that every
	// render refreshes keeps a caller free to pass an inline function.

	const onCopyRef = useRef(onCopy);

	useEffect(() => {
		onCopyRef.current = onCopy;
	});

	useEffect(() => {
		if (!buttonRef.current) {
			return;
		}

		// Bind to the button itself instead of the '[data-clipboard-text]'
		// selector: a selector delegates the listener to document.body, which
		// never sees the click when an ancestor stops its propagation (as the
		// table row actions do to keep the row from being selected).

		const _clipboard = new Clipboard(buttonRef.current);

		// Every copy in the app announces itself the same way, so the alert is
		// the button's own business rather than each caller's.

		_clipboard.on('success', (event) => {
			setCopied(true);

			dispatch(
				addAlert({
					alertType: Alert.Types.Success,
					message: Liferay.Language.get(
						'copied-successfully-to-the-clipboard'
					),
				})
			);

			onCopyRef.current?.();

			event.clearSelection();
		});

		return () => _clipboard.destroy();
	}, [dispatch]);

	return (
		<ClayButton
			aria-label={label}
			className="button-root"
			data-clipboard-text={text}
			displayType={displayType}
			onClick={onClick}
			ref={buttonRef}
			title={copied ? Liferay.Language.get('copied') : label}
			{...otherProps}
		>
			{buttonText || <ClayIcon className="icon-root" symbol="copy" />}
		</ClayButton>
	);
};

export default CopyButton;
