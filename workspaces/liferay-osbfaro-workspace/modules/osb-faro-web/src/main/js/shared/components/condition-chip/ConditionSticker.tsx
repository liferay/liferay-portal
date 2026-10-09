import ClayIcon from 'shared/components/Icon';
import ClaySticker from '@clayui/sticker';
import getCN from 'classnames';
import React from 'react';
import {DATA_TYPE_ICONS_MAP, DataTypes} from 'shared/types/DataTypes';

export type ConditionStickerType =
	| {dataType: DataTypes; symbol?: never}
	| {dataType?: never; symbol: string};

type ConditionStickerProps = ConditionStickerType & {
	className?: string;
};

const ConditionSticker: React.FC<ConditionStickerProps> = ({
	className,
	...type
}) => (
	<ClaySticker
		className={getCN('condition-sticker flex-shrink-0', className)}
		displayType="primary"
	>
		<ClayIcon
			symbol={
				type.dataType !== undefined
					? DATA_TYPE_ICONS_MAP[type.dataType]
					: type.symbol
			}
		/>
	</ClaySticker>
);

export default ConditionSticker;
