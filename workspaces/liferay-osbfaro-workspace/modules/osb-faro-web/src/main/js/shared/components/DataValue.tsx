import React from 'react';
import {
	DataValueType,
	formatDataValue,
	IDataValueOptions,
	isMissingDataValue,
} from 'shared/util/data-value';
import {NO_DATA_VALUE} from 'shared/util/constants';

interface IDataValueProps extends IDataValueOptions {
	format?: (value: any) => React.ReactNode;
	processing?: boolean;
	type?: DataValueType;
	value: unknown;
}

const DataValue: React.FC<IDataValueProps> = ({
	format,
	processing = false,
	type = 'text',
	value,
	...options
}) => {
	if (processing) {
		return <span>{Liferay.Language.get('processing')}</span>;
	}

	if (isMissingDataValue(value, type)) {
		return <span className="text-secondary">{NO_DATA_VALUE}</span>;
	}

	return (
		<>{format ? format(value) : formatDataValue(value, type, options)}</>
	);
};

export default DataValue;
