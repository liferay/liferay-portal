import ClayButton from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import Form from 'shared/components/form';
import getCN from 'classnames';
import OperatorSelect from './OperatorSelect';
import React, {useEffect, useState} from 'react';
import Sticker from 'shared/components/Sticker';
import ValueInput from './ValueInput';
import {
	AddEntity,
	EntityType,
	ReferencedEntities,
	withReferencedObjectsConsumer,
} from '../../../context/referencedObjects';
import {Attribute} from 'event-analysis/utils/types';
import {
	AttributeConjunctionChangeParams,
	AttributeFilterState,
	Criterion,
} from '../../../utils/types';
import {DATA_TYPE_ICONS_MAP} from 'event-analysis/utils/utils';
import {
	FunctionalOperators,
	RelationalOperators,
} from '../../../utils/constants';
import {
	encodeAttributeId,
	getDefaultAttributeOperator,
	getDefaultAttributeValue,
	validateAttributeValue,
} from './utils';
import {Map} from 'immutable';

interface IAttributeFilterConjunctionInputProps {
	addEntity: AddEntity;
	attributes: Attribute[];
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
	conjunctionCriterion,
	onChange,
	onClear,
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

	const [attributesDisplayed, setAttributesDisplayed] =
		useState<Attribute[]>(attributes);
	const [searchValue, setSearchValue] = useState<string>('');

	const getAttributeFromContext = (): Attribute => {
		const attributeId = getAttributeId();

		return (
			attributes.find(
				(attribute) =>
					attribute &&
					encodeAttributeId(attribute.name) === attributeId
			) || attributes[0]
		);
	};

	const getAttributeId = (): string => {
		const [, id] = (conjunctionCriterion.propertyName ?? '').split('/');

		return id;
	};

	const handleAttributeChange = (value: string) => {
		const attribute = attributes.find(({id}) => id === value);

		if (attribute) {
			setAttribute(attribute);
		}
	};

	const getAttributes = (query: string) => {
		if (!query) return attributes;

		return attributes.filter(
			({displayName, name}) =>
				(displayName ?? '')
					.toLowerCase()
					.includes(query.toLowerCase()) ||
				name.toLowerCase().includes(query.toLowerCase())
		);
	};

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

	return (
		<>
			<Form.GroupItem shrink>
				<ClayDropDown
					closeOnClick
					trigger={
						<ClayButton
							className={getCN(
								'form-control form-control-select form-control-select-secondary',
								{'form-control-sm': small}
							)}
							displayType="secondary"
						>
							{attribute.displayName || attribute.name}
						</ClayButton>
					}
				>
					<ClayDropDown.Search
						className="py-2 px-2"
						onChange={(query: string) => {
							setSearchValue(query);
							setAttributesDisplayed(getAttributes(query));
						}}
						placeholder={Liferay.Language.get('search')}
						value={searchValue}
					/>

					<ClayDropDown.ItemList items={attributesDisplayed}>
						{(item: unknown) => {
							const {dataType, displayName, id, name} =
								item as Attribute;
							return (
								<ClayDropDown.Item
									active={id === attribute.id}
									key={name}
									onClick={() => handleAttributeChange(id)}
									roleItem="option"
								>
									<Sticker
										className="mr-3"
										display="secondary"
									>
										<ClayIcon
											symbol={
												DATA_TYPE_ICONS_MAP[dataType]
											}
										/>
									</Sticker>

									{displayName ?? name}
								</ClayDropDown.Item>
							);
						}}
					</ClayDropDown.ItemList>
				</ClayDropDown>
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
