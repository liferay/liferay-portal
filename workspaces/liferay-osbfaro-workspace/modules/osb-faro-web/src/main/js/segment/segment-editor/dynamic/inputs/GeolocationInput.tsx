import * as API from 'shared/api';
import AutocompleteInput from 'shared/components/AutocompleteInput';
import ClayButton from '@clayui/button';
import DateFilterConjunctionInput from './components/DateFilterConjunctionInput';
import Form from 'shared/components/form';
import getCN from 'classnames';
import React from 'react';
import {CustomValue} from 'shared/util/records';
import {fromJS, Map} from 'immutable';
import {GEOLOCATION_OPTIONS} from '../utils/constants';
import {
	getFilterCriterionIMap,
	getIndexFromPropertyName,
	getOperator,
	getPropertyValue,
	removeItemsByIndex,
	setPropertyValue,
} from '../utils/custom-inputs';
import {ISegmentEditorCustomInputBase} from '../utils/types';
import {isNull} from 'lodash';
import {isValid} from '../utils/utils';
import {Option, Picker} from '@clayui/core';
import {
	IPaginatedDataSourceParams,
	IPaginatedDataSourceResult,
} from 'shared/hooks/usePaginatedRequest';

/**
 * Location Types
 */
const CITY = 'city';
const COUNTRY = 'country';
const REGION = 'region';

/**
 * Create a new templated criterion entry for Geolocation.
 * @param {string} locationType - The location type.
 * @param {string} value - The value of the criterion.
 * @param {Map} valueIMap - The Immutable Map representing the custom input value.
 * @returns {Map} - The Immutable Map representing the new criterion entry.
 */
export function createLocationTypeEntry(
	locationType: string,
	value: string,
	valueIMap: CustomValue
): Map<string, any> {
	const selectedOperator = getOperator(
		valueIMap,
		getLocationTypeIndex(valueIMap, COUNTRY)
	);

	return Map({
		operatorName: selectedOperator,
		propertyName: `context/${locationType}`,
		value,
	});
}

/**
 * Get the index of the entry in the criteria list with the matching location type.
 * @param {Map} valueIMap - The Immutable Map representing the custom input value.
 * @param {string} locationType - The location type.
 * @returns {number} - The index of the matching entry or -1 if not found.
 */
export function getLocationTypeIndex(
	valueIMap: CustomValue,
	locationType: string
): number {
	return getIndexFromPropertyName(valueIMap, `context/${locationType}`);
}

/**
 * Get the value of the criterion that matches the locationType.
 * @param {Map} valueIMap - The Immutable Map representing the custom input value.
 * @param {string} locationType - The location type.
 * @returns {string} - The value of the criterion that matches the locationType.
 */
export function getLocationTypeValue(
	valueIMap: CustomValue,
	locationType: string
): string {
	const locationTypeIndex = getLocationTypeIndex(valueIMap, locationType);

	return locationTypeIndex > -1
		? getPropertyValue(valueIMap, 'value', locationTypeIndex)
		: '';
}

/**
 * Set the value of the criterion that matches the locationType.
 * @param {Map} valueIMap - The Immutable Map representing the custom input value.
 * @param {string} locationType - The location type.
 * @param {string} newValue - The new value to set for the locationType.
 * @returns {Map} - The updated valueIMap with the new value set for the locationType criterion.
 */
export function setLocationTypeValue(
	valueIMap: CustomValue,
	locationType: string,
	newValue: string
): CustomValue {
	const locationTypeIndex = getLocationTypeIndex(valueIMap, locationType);

	return locationTypeIndex > -1
		? setPropertyValue(valueIMap, 'value', locationTypeIndex, newValue)
		: (valueIMap.updateIn(['criterionGroup', 'items'], (iList) =>
				iList.push(
					createLocationTypeEntry(locationType, newValue, valueIMap)
				)
			) as CustomValue);
}

/**
 * Update all of the locationType operators, when the operator dropdown changes.
 * @param {Map} valueIMap - The Immutable Map representing the custom input value.
 * @param {string} newOperator - The new operator.
 * @returns {Map} - The updated valueIMap with the new operator set for the locationType criteria.
 */
export function updateLocationOperators(
	valueIMap: CustomValue,
	newOperator: string
): CustomValue {
	const locationPropertyNames = [COUNTRY, REGION, CITY].map(
		(name) => `context/${name}`
	);

	return valueIMap.updateIn(['criterionGroup', 'items'], (iList: any) =>
		iList.map((entry: Map<string, any>) => {
			const locationProperty = locationPropertyNames.includes(
				entry.get('propertyName')
			);

			return locationProperty
				? entry.set('operatorName', newOperator)
				: entry;
		})
	) as CustomValue;
}

type LocationDataSourceFn = (
	params: IPaginatedDataSourceParams
) => Promise<IPaginatedDataSourceResult>;

function getLocationFilter(
	valueIMap: CustomValue,
	locationTypes: string[]
): string {
	return locationTypes
		.map((locationType) => [
			locationType,
			getLocationTypeValue(valueIMap, locationType),
		])
		.filter(([, locationValue]) => locationValue)
		.map(
			([locationType, locationValue]) =>
				`context/${locationType} eq '${locationValue}'`
		)
		.join(' and ');
}

function fetchLocations({
	channelId,
	filter,
	groupId,
	locationType,
}: {
	channelId?: string;
	filter?: string;
	groupId?: string;
	locationType: string;
}): LocationDataSourceFn {
	return ({page, pageSize, query}) =>
		API.session.fetchFieldValues({
			channelId,
			delta: pageSize,
			fieldName: `context/${locationType}`,
			filter,
			groupId: groupId!,
			page,
			query,
		});
}

interface IButtonInputTriggerProps {
	className: string;
	dataSourceKey: string;
	paginatedDataSourceFn: LocationDataSourceFn;
	editing: boolean;
	label: string;
	onChange: (value: string) => void;
	onBlur: () => void;
	onClick: () => void;
	placeholder: string;
	value: string;
}

const ButtonInputTrigger: React.FC<IButtonInputTriggerProps> = ({
	editing,
	label,
	onClick,
	value,
	...otherProps
}) =>
	editing ? (
		<AutocompleteInput value={value} {...otherProps} />
	) : (
		<ClayButton
			className="button-root"
			displayType="secondary"
			onClick={onClick}
		>
			{label}
		</ClayButton>
	);

interface IGeolocationInputProps extends ISegmentEditorCustomInputBase {
	touched: {country: boolean; dateFilter: boolean};
	valid: {country: boolean; dateFilter: boolean};
}

export default class GeolocationInput extends React.Component<
	IGeolocationInputProps,
	{editCity: boolean; editRegion: boolean}
> {
	constructor(props: IGeolocationInputProps) {
		super(props);

		const {value} = props;

		this.state = {
			editCity: !!getLocationTypeValue(value, CITY),
			editRegion: !!getLocationTypeValue(value, REGION),
		};

		this.handleConjunctionChange = this.handleConjunctionChange.bind(this);
		this.handleCountryBlur = this.handleCountryBlur.bind(this);
		this.handleLocationOnBlur = this.handleLocationOnBlur.bind(this);
		this.handleLocationTypeChange =
			this.handleLocationTypeChange.bind(this);
		this.handleOperatorChange = this.handleOperatorChange.bind(this);
	}

	getConjunctionDateFilterIMap(value: CustomValue) {
		const conjunctionDateFilterIndex = getIndexFromPropertyName(
			value,
			'completeDate'
		);

		if (conjunctionDateFilterIndex >= 0) {
			return getFilterCriterionIMap(
				value,
				conjunctionDateFilterIndex
			).toJS();
		}

		return {propertyName: 'completeDate'};
	}

	handleConjunctionChange(criterion: any) {
		const {onChange, touched, valid, value} = this.props;

		onChange({
			touched: {...touched, dateFilter: criterion && criterion.touched},
			valid: {...valid, dateFilter: isNull(criterion) || criterion.valid},
			value: isNull(criterion)
				? value.deleteIn(['criterionGroup', 'items', 1])
				: value.mergeIn(
						['criterionGroup', 'items', 1],
						fromJS(criterion)
					),
		});
	}

	handleCountryBlur() {
		const {onChange, touched, valid, value} = this.props;

		onChange({
			touched: {...touched, country: true},
			valid: {
				...valid,
				country: isValid(getLocationTypeValue(value, COUNTRY)),
			},
		});
	}

	handleLocationOnBlur(locationType: string) {
		const {onChange, value} = this.props;

		const inputValue = getLocationTypeValue(value, locationType);

		if (!inputValue.length) {
			const criterionIndex = getLocationTypeIndex(value, locationType);

			onChange({
				value: removeItemsByIndex(value, [criterionIndex]),
			});
		}
	}

	handleLocationTypeChange(inputValue: string, locationType: string) {
		const {onChange, valid, value} = this.props;

		let params: {
			value: Map<any, any>;
			valid?: {country: boolean; dateFilter: boolean};
		} = {
			value: setLocationTypeValue(value, locationType, inputValue),
		};

		if (locationType === COUNTRY) {
			params = {
				...params,
				valid: {...valid, country: isValid(inputValue)},
			};
		}

		onChange(params);
	}

	handleOperatorChange(newValue: React.Key) {
		const {onChange, value} = this.props;

		onChange({value: updateLocationOperators(value, String(newValue))});
	}

	render() {
		const {
			props: {
				channelId,
				displayValue,
				groupId,
				property: {entityName},
				touched,
				valid,
				value,
			},
			state: {editCity, editRegion},
		} = this;

		const cityInputValue = getLocationTypeValue(value, CITY);
		const regionInputValue = getLocationTypeValue(value, REGION);

		const cityFilter = getLocationFilter(value, [COUNTRY, REGION]);
		const regionFilter = getLocationFilter(value, [COUNTRY]);

		const conjunctionCriterion = this.getConjunctionDateFilterIMap(value);

		return (
			<div className="criteria-statement geolocation-input">
				<Form.Group autoFit>
					<Form.GroupItem className="entity-name" label shrink>
						{entityName}
					</Form.GroupItem>

					<Form.GroupItem className="display-value" label shrink>
						{displayValue}
					</Form.GroupItem>

					<Form.GroupItem shrink>
						<Picker
							className="operator-input"
							items={GEOLOCATION_OPTIONS}
							onSelectionChange={this.handleOperatorChange}
							selectedKey={getOperator(
								value,
								getLocationTypeIndex(value, COUNTRY)
							)}
						>
							{({label, value}) => (
								<Option key={value}>{label}</Option>
							)}
						</Picker>
					</Form.GroupItem>

					<Form.GroupItem shrink>
						<AutocompleteInput
							aria-invalid={(!valid && touched) || undefined}
							className={getCN({
								'has-error': !valid && touched,
							})}
							onBlur={this.handleCountryBlur}
							onChange={(value) =>
								this.handleLocationTypeChange(value, COUNTRY)
							}
							paginatedDataSourceFn={fetchLocations({
								channelId,
								groupId,
								locationType: COUNTRY,
							})}
							placeholder={Liferay.Language.get('country')}
							value={getLocationTypeValue(value, COUNTRY)}
						/>
					</Form.GroupItem>

					<Form.GroupItem shrink>
						<ButtonInputTrigger
							className="region"
							dataSourceKey={regionFilter}
							editing={editRegion || !!regionInputValue.length}
							label={Liferay.Language.get('add-region')}
							onBlur={() => this.handleLocationOnBlur(REGION)}
							onChange={(value) =>
								this.handleLocationTypeChange(value, REGION)
							}
							onClick={() => this.setState({editRegion: true})}
							paginatedDataSourceFn={fetchLocations({
								channelId,
								filter: regionFilter,
								groupId,
								locationType: REGION,
							})}
							placeholder={Liferay.Language.get('region')}
							value={regionInputValue}
						/>
					</Form.GroupItem>

					<Form.GroupItem shrink>
						<ButtonInputTrigger
							className="city"
							dataSourceKey={cityFilter}
							editing={editCity || !!cityInputValue.length}
							label={Liferay.Language.get('add-city')}
							onBlur={() => this.handleLocationOnBlur(CITY)}
							onChange={(value) =>
								this.handleLocationTypeChange(value, CITY)
							}
							onClick={() => this.setState({editCity: true})}
							paginatedDataSourceFn={fetchLocations({
								channelId,
								filter: cityFilter,
								groupId,
								locationType: CITY,
							})}
							placeholder={Liferay.Language.get('city')}
							value={cityInputValue}
						/>
					</Form.GroupItem>
				</Form.Group>

				<Form.Group autoFit>
					<DateFilterConjunctionInput
						conjunctionCriterion={conjunctionCriterion}
						onChange={this.handleConjunctionChange}
					/>
				</Form.Group>
			</div>
		);
	}
}
