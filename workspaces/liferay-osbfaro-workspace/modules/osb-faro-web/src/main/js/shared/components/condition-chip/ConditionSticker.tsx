import ClayIcon from 'shared/components/Icon';
import ClaySticker from '@clayui/sticker';
import getCN from 'classnames';
import React from 'react';
import {DATA_TYPE_ICONS_MAP, DataTypes} from 'shared/types/DataTypes';

type StatusDisplayType = 'success' | 'warning';

export type ConditionStickerType =
	| {dataType: DataTypes; displayType?: 'primary'; symbol?: never}
	| {dataType?: never; displayType?: 'primary'; symbol: string}
	| {dataType?: never; displayType: StatusDisplayType; symbol?: never};

type ConditionStickerProps = ConditionStickerType & {
	className?: string;
};

const STATUS_SYMBOLS: {[key in StatusDisplayType]: string} = {
	success: 'check-circle',
	warning: 'exclamation-circle',
};

const getSymbol = (type: ConditionStickerType): string => {
	if (type.dataType !== undefined) {
		return DATA_TYPE_ICONS_MAP[type.dataType];
	}

	if (type.symbol !== undefined) {
		return type.symbol;
	}

	return STATUS_SYMBOLS[type.displayType];
};

const ConditionSticker: React.FC<ConditionStickerProps> = ({
	className,
	...type
}) => (
	<ClaySticker
		className={getCN('condition-sticker flex-shrink-0', className)}
		displayType={type.displayType ?? 'primary'}
	>
		<ClayIcon symbol={getSymbol(type)} />
	</ClaySticker>
);

export default ConditionSticker;
