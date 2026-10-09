import ClayButton from '@clayui/button';
import ClayIcon from 'shared/components/Icon';
import FilterPicker, {IFilterPickerItem} from 'shared/components/FilterPicker';
import Form from 'shared/components/form';
import OperatorSelect from './OperatorSelect';
import React, {useEffect, useRef} from 'react';
import ValueInput from './ValueInput';
import {
	AddEntity,
	EntityType,
	ReferencedEntities,
	withReferencedObjectsConsumer,
} from '../../../context/referencedObjects';
import {
	ATTRIBUTES_PAGE_SIZE,
	encodeAttributeId,
	getDefaultAttributeOperator,
	getDefaultAttributeValue,
	validateAttributeValue,
} from './utils';
import {Attribute} from 'event-analysis/utils/types';
import {
	AttributeConjunctionChangeParams,
	AttributeFilterState,
	Criterion,
} from '../../../utils/types';
import {
	FunctionalOperators,
	RelationalOperators,
} from '../../../utils/constants';
import {Map} from 'immutable';
import {PaginatedDataSourceFn} from 'shared/hooks/usePaginatedRequest';

export type AttributesDataSourceFn = PaginatedDataSourceFn<Attribute>;

const toFilterPickerItem = ({
	displayName,
	name,
}: Attribute): IFilterPickerItem => ({
	id: name,
	name: displayName || name,
});

interface IAttributeFilterConjunctionInputProps {
	addEntity: AddEntity;
	attributes: Attribute[];
	attributesDataSourceFn: AttributesDataSourceFn;
	conjunctionCriterion: Criterion;
	onChange: (params: AttributeConjunctionChangeParams) => void;
	onClear?: () => void;
	referencedEntities: ReferencedEntities;
	small?: boolean;
	touched: AttributeFilterState;
	valid: AttributeFilterState;
}

const AttributeFilterConjunctionInput: React.FC<
	IAttributeFilterConjunctionInputProps
> = ({
	addEntity,
	attributes,
	attributesDataSourceFn,
	conjunctionCriterion,
	onChange,
	onClear,
	referencedEntities,
	small,
	touched,
	valid,
}) => {
	useEffect(() => {
		if (!getAttributeId()) {
			const defaultAttribute = attributes[0];

			setAttribute(defaultAttribute);
		}
	}, []);

	const loadedAttributesRef = useRef<{[name: string]: Attribute}>({});

	const getAttributeFromContext = (): Attribute => {
		const attributeId = getAttributeId();

		const referencedAttributeIMap = referencedEntities?.getIn([
			EntityType.Attributes,
			attributeId,
		]);

		return (
			attributes.find(
				(attribute) =>
					attribute &&
					encodeAttributeId(attribute.name) === attributeId
			) ||
			referencedAttributeIMap?.toJS() ||
			attributes[0]
		);
	};

	const getAttributeId = (): string => {
		const [, id] = (conjunctionCriterion.propertyName ?? '').split('/');

		return id;
	};

	const fetchAttributeItems: PaginatedDataSourceFn<IFilterPickerItem> = (
		params
	) =>
		attributesDataSourceFn(params).then(({items, total}) => {
			items.forEach((attribute) => {
				loadedAttributesRef.current[attribute.name] = attribute;
			});

			return {items: items.map(toFilterPickerItem), total};
		});

	const setAttribute = (attribute: Attribute) => {
		const encodedId = encodeAttributeId(attribute.name);

		addEntity({
			entityType: EntityType.Attributes,
			payload: Map({...attribute, id: encodedId}),
		});

		const defaultAttributeValue = getDefaultAttributeValue(
			attribute.dataType,
			conjunctionCriterion.operatorName as unknown as
				| RelationalOperators
				| FunctionalOperators
		);

		const defaultAttributeOperator = getDefaultAttributeOperator(
			attribute.dataType
		);

		onChange({
			attribute,
			criterion: {
				operatorName:
					defaultAttributeOperator as unknown as Criterion['operatorName'],
				propertyName: `attribute/${encodedId}`,
				value: defaultAttributeValue,
			},
			touched: {...touched, attribute: true, attributeValue: false},
			valid: {
				...valid,
				attribute: true,
				attributeValue: validateAttributeValue(
					defaultAttributeValue,
					attribute.dataType,
					defaultAttributeOperator
				),
			},
		});
	};

	const attribute = getAttributeFromContext();
	const {operatorName, value} = conjunctionCriterion;

	const handleAttributeChange = (item: IFilterPickerItem | null) => {
		const selectedAttribute = item && loadedAttributesRef.current[item.id];

		if (selectedAttribute && selectedAttribute.name !== attribute.name) {
			setAttribute(selectedAttribute);
		}
	};

	return (
		<>
			<Form.GroupItem shrink>
				<FilterPicker
					className={small ? 'form-control-sm' : undefined}
					displayType="select"
					entityLabel={Liferay.Language.get('event-attributes')}
					onFilterChange={handleAttributeChange}
					pageSize={ATTRIBUTES_PAGE_SIZE}
					paginatedDataSourceFn={fetchAttributeItems}
					selected={toFilterPickerItem(attribute)}
					showAllOption={false}
				/>
			</Form.GroupItem>

			<OperatorSelect
				dataType={attribute.dataType}
				onChange={(params: {criterion: Criterion}) =>
					onChange({
						attribute,
						criterion: params.criterion,
						touched,
						valid,
					})
				}
				operatorName={operatorName}
				small={small}
			/>

			<ValueInput
				dataType={attribute.dataType}
				onChange={(params) =>
					onChange({
						attribute,
						criterion: params.criterion ?? {},
						touched: {
							...touched,
							attributeValue:
								params.touched?.attributeValue ??
								touched.attributeValue,
						},
						valid: {
							...valid,
							attributeValue:
								params.valid?.attributeValue ??
								valid.attributeValue,
						},
					})
				}
				operatorName={operatorName}
				touched={touched.attributeValue}
				valid={valid.attributeValue}
				value={value}
			/>

			{onClear && (
				<ClayButton
					aria-label={Liferay.Language.get('clear')}
					className="attribute-filter-clear button-root ml-auto mr-2 text-secondary"
					displayType="unstyled"
					onClick={onClear}
				>
					<ClayIcon className="icon-root" symbol="times-circle" />
				</ClayButton>
			)}
		</>
	);
};

export default withReferencedObjectsConsumer(AttributeFilterConjunctionInput);
