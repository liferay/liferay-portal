import React, {useContext} from 'react';
import {BOOLEAN_LABELS_MAP} from 'event-analysis/utils/utils';
import {Criterion} from 'segment/segment-editor/dynamic/utils/types';
import {DataTypes} from 'event-analysis/utils/types';
import {
	EntityType,
	ReferencedObjectsContext,
} from 'segment/segment-editor/dynamic/context/referencedObjects';
import {formatTime} from 'shared/util/time';
import {formatUTCDate, getCustomDateFormat} from 'shared/util/date';
import {FunctionalOperators} from 'segment/segment-editor/dynamic/utils/constants';
import {
	decodeAttributeId,
	getOperatorOptions,
} from 'segment/segment-editor/dynamic/inputs/components/attribute-conjunction-input/utils';

interface IAttributeConjunctionDisplayProps {
	conjunctionCriterion: Criterion;
}

const AttributeConjunctionDisplay: React.FC<
	IAttributeConjunctionDisplayProps
> = ({conjunctionCriterion: {operatorName, propertyName, value}}) => {
	const {referencedEntities} = useContext(ReferencedObjectsContext);

	const [, id] = (propertyName ?? '').split('/');

	if (!id) {
		return (
			<b className="undefined-entity">
				{Liferay.Language.get('undefined-attribute')}
			</b>
		);
	}

	const attributeIMap = referencedEntities.getIn([EntityType.Attributes, id]);

	const dataType = attributeIMap?.get('dataType') ?? DataTypes.String;
	const displayName =
		attributeIMap?.get('displayName') ?? decodeAttributeId(id);

	const operatorOptions = getOperatorOptions(dataType);

	const {label = Liferay.Language.get('is').toLowerCase()} =
		operatorOptions?.find(
			({value}: {value: string}) => value === operatorName
		) || {};

	const formatByDataType = (value: any, dataType: DataTypes) => {
		switch (dataType) {
			case DataTypes.Boolean:
				return BOOLEAN_LABELS_MAP[value];
			case DataTypes.Date:
				if (FunctionalOperators.Between === operatorName) {
					const {end, start} = value;

					return `${formatUTCDate(
						start,
						getCustomDateFormat()
					)} - ${formatUTCDate(end, getCustomDateFormat())}`;
				}

				return formatUTCDate(value, getCustomDateFormat());
			case DataTypes.Duration:
				return formatTime(value);
			case DataTypes.Number:
				if (FunctionalOperators.Between === operatorName) {
					const {end, start} = value;

					return `${start} - ${end}`;
				}

				return value;
			case DataTypes.String:
			default:
				return `"${value}"`;
		}
	};

	const displayValue = formatByDataType(value, dataType);

	return (
		<>
			<span className="text-nowrap">
				{Liferay.Language.get('where-event-attribute').toLowerCase()}
			</span>

			<b className="text-secondary">{displayName}</b>

			<b className="text-secondary">{label}</b>

			<b className="text-secondary">{displayValue}</b>
		</>
	);
};

export default AttributeConjunctionDisplay;
