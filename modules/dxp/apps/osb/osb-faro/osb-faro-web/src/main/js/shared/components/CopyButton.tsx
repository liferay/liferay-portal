import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import Clipboard from 'clipboard';
import React, {useEffect, useState} from 'react';
import {ButtonProps} from '@clayui/button';

interface ICopyButtonProps {
	buttonText?: string;
	className?: string;
	displayType?: ButtonProps['displayType'];
	onClick?: (any) => void;
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

	useEffect(() => {
		const _clipboard = new Clipboard('[data-clipboard-text]');

		_clipboard.on('success', event => {
			setTitle(Liferay.Language.get('copied'));

			event.clearSelection();
		});

		return () => _clipboard.destroy();
	}, []);

	return (
		<ClayButton
			aria-label={Liferay.Language.get('click-to-copy')}
			className='button-root'
			data-clipboard-text={text}
			displayType={displayType}
			onClick={onClick}
			title={title}
			{...otherProps}
		>
			{buttonText || <ClayIcon className='icon-root' symbol='copy' />}
		</ClayButton>
	);
};

export default CopyButton;
