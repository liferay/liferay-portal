import ClayIcon from 'shared/components/Icon';
import ClaySticker from '@clayui/sticker';
import React from 'react';

interface ILifecycleStickerProps {
	className?: string;
	pinned?: boolean;
}

const LifecycleSticker: React.FC<ILifecycleStickerProps> = ({
	className,
	pinned = false,
}) => (
	<ClaySticker
		aria-label={pinned ? Liferay.Language.get('default') : undefined}
		borderless
		className={className}
		displayType="outline-1"
		role={pinned ? 'img' : undefined}
	>
		<ClayIcon aria-hidden symbol={pinned ? 'pin' : 'plant'} />
	</ClaySticker>
);

export default LifecycleSticker;
