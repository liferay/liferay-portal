import ClayIcon from 'shared/components/Icon';
import ClaySticker from '@clayui/sticker';
import getCN from 'classnames';
import React from 'react';
import {DATA_TYPE_ICONS_MAP, DataTypes} from 'shared/types/DataTypes';

type StickerDisplayType = 'primary' | 'success' | 'warning';

export type ConditionStickerType =
	| {dataType: DataTypes; displayType?: StickerDisplayType; symbol?: never}
	| {dataType?: never; displayType?: StickerDisplayType; symbol: string};

type ConditionStickerProps = ConditionStickerType & {
	className?: string;
};

const ConditionSticker: React.FC<ConditionStickerProps> = ({
	className,
	dataType,
	displayType = 'primary',
	symbol,
}) => (
	<ClaySticker
		className={getCN('condition-sticker flex-shrink-0', className)}
		displayType={displayType}
	>
		<ClayIcon
			symbol={
				dataType !== undefined ? DATA_TYPE_ICONS_MAP[dataType] : symbol!
			}
		/>
	</ClaySticker>
);

export default ConditionSticker;
