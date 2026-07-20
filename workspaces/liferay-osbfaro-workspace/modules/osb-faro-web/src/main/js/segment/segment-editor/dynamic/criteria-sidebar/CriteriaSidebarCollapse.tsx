import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import CriteriaSidebarItem from './CriteriaSidebarItem';
import EmptyState from '@clayui/empty-state';
import React from 'react';
import URLConstants from 'shared/util/url-constants';
import {
	EVENT_KEY,
	FunctionalOperators,
	PropertyTypes,
	RelationalOperators,
	TimeSpans,
} from '../utils/constants';
import {createCustomValueMap} from '../utils/custom-inputs';
import {FieldOwnerTypes} from 'shared/util/constants';
import {jsDatetoYYYYMMDD} from '../utils/utils';
import {List} from 'immutable';
import {Property, PropertyGroup, PropertySubgroup} from 'shared/util/records';
import {Routes, toRoute} from 'shared/util/router';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useParams} from 'react-router-dom';

/**
 * Returns a default value for a property provided.
 */
export const getDefaultValue = (property: Property): any => {
	const {name, options, type} = property;

	switch (type) {
		case PropertyTypes.Date:
			return jsDatetoYYYYMMDD(new Date());
		case PropertyTypes.DateTime:
			return new Date().toISOString();
		case PropertyTypes.OrganizationDate: {
			return createCustomValueMap([
				{
					key: 'criterionGroup',
					value: [
						{
							operatorName: RelationalOperators.EQ,
							propertyName: name,
							value: jsDatetoYYYYMMDD(new Date()),
						},
					],
				},
			]);
		}
		case PropertyTypes.SessionDateTime:
		case PropertyTypes.OrganizationDateTime:
			return createCustomValueMap([
				{
					key: 'criterionGroup',
					value: [
						{
							operatorName: RelationalOperators.EQ,
							propertyName: name,
							value: new Date().toISOString(),
						},
					],
				},
			]);
		case PropertyTypes.Boolean:
			return 'true';
		case PropertyTypes.Interest:
			return createCustomValueMap([
				{
					key: 'criterionGroup',
					value: [
						{
							operatorName: RelationalOperators.EQ,
							propertyName: 'name',
							value: name,
						},
						{
							operatorName: RelationalOperators.EQ,
							propertyName: 'score',
							value: 'true',
						},
					],
				},
			]);
		case PropertyTypes.AccountDate:
			return createCustomValueMap([
				{
					key: 'criterionGroup',
					value: [
						{
							operatorName: RelationalOperators.EQ,
							propertyName: name,
							value: new Date().toISOString(),
						},
					],
				},
			]);
		case PropertyTypes.AccountNumber:
		case PropertyTypes.AccountText:
		case PropertyTypes.OrganizationSelectText:
		case PropertyTypes.OrganizationText:
		case PropertyTypes.OrganizationNumber:
			return createCustomValueMap([
				{
					key: 'criterionGroup',
					value: [
						{
							operatorName: RelationalOperators.EQ,
							propertyName: name,
							value: '',
						},
					],
				},
			]);
		case PropertyTypes.Event:
			return createCustomValueMap([
				{
					key: 'criterionGroup',
					value: [
						{
							operatorName: RelationalOperators.EQ,
							propertyName: EVENT_KEY,
							value: name,
						},

						{
							operatorName: FunctionalOperators.Contains,
							propertyName: 'attribute/',
							value: '',
						},
						{
							operatorName: RelationalOperators.GT,
							propertyName: 'day',
							value: TimeSpans.Last24Hours,
						},
					],
				},
				{key: 'operator', value: RelationalOperators.GE},
				{key: 'value', value: 1},
			]);
		case PropertyTypes.Behavior:

			// No asset type is seeded: the criterion starts without an asset
			// filter so the picker shows the "Select a type" placeholder and the
			// user must choose a type (or Page) before the segment can be saved.

			return createCustomValueMap([
				{
					key: 'criterionGroup',
					value: [
						{
							operatorName: FunctionalOperators.Contains,
							propertyName: 'attribute/',
							value: '',
						},
						{
							operatorName: RelationalOperators.GT,
							propertyName: 'day',
							value: TimeSpans.Last24Hours,
						},
					],
				},
				{key: 'operator', value: RelationalOperators.GE},
				{key: 'value', value: 1},
			]);
		case PropertyTypes.Tag:
		case PropertyTypes.Vocabulary:
			return createCustomValueMap([
				{key: 'operator', value: RelationalOperators.GE},
				{key: 'value', value: 1},
			]);
		case PropertyTypes.OrganizationBoolean:
			return createCustomValueMap([
				{
					key: 'criterionGroup',
					value: [
						{
							operatorName: RelationalOperators.EQ,
							propertyName: name,
							value: 'true',
						},
					],
				},
			]);
		case PropertyTypes.SessionGeolocation:
		case PropertyTypes.SessionNumber:
		case PropertyTypes.SessionText:
			return createCustomValueMap([
				{
					key: 'criterionGroup',
					value: [
						{
							operatorName: RelationalOperators.EQ,
							propertyName: name,
							value: options?.length ? options[0].value : '',
						},
						{
							operatorName: RelationalOperators.GT,
							propertyName: 'completeDate',
							value: TimeSpans.Last24Hours,
						},
					],
				},
			]);
		case PropertyTypes.Text:
			if (options && !!options.length) {
				return options![0].value;
			}

			return '';
		default:
			return '';
	}
};

interface ICriteriaSidebarCollapseProps {
	propertyGroupsIList: List<PropertyGroup>;
	propertyKey: string;
	searchValue: string;
}

const CriteriaSidebarCollapse: React.FC<ICriteriaSidebarCollapseProps> = ({
	propertyGroupsIList,
	propertyKey,
	searchValue,
}) => {
	const {groupId} = useParams();
	const currentUser = useCurrentUser();
	const authorized = currentUser.isAdmin();

	const filterProperties = (): List<PropertySubgroup> => {
		const propertyGroup = propertyGroupsIList.find(
			(propertyGroup: PropertyGroup | undefined) =>
				propertyKey === propertyGroup?.propertyKey
		);

		const propertySubgroupsIList = propertyGroup
			? propertyGroup.propertySubgroups
			: List<PropertySubgroup>();

		if (searchValue) {
			return propertySubgroupsIList.map(
				(subgroup: PropertySubgroup | undefined) =>
					new PropertySubgroup({
						label: subgroup?.label ?? '',
						properties: (subgroup?.properties ?? List()).filter(
							(property: Property | undefined) => {
								const propertyLabel = (
									property?.label ?? ''
								).toLowerCase();

								return propertyLabel.includes(
									searchValue.toLowerCase()
								);
							}
						) as List<Property>,
					})
			) as List<PropertySubgroup>;
		}

		return propertySubgroupsIList;
	};

	const filteredProperties = filterProperties();

	const noResults = filteredProperties
		.filterNot(
			(subgroup: PropertySubgroup | undefined) =>
				!!subgroup?.properties.isEmpty()
		)
		.isEmpty();

	if (!!searchValue && noResults) {
		return (
			<div className="empty-message">
				<EmptyState
					className="text-center"
					description={Liferay.Language.get(
						'review-your-search-and-try-again'
					)}
					title={Liferay.Language.get('no-results-found')}
				/>
			</div>
		);
	}

	if (propertyKey === FieldOwnerTypes.Account && !searchValue && noResults) {
		return (
			<div className="align-items-center d-flex empty-message h-100 justify-content-center">
				<EmptyState
					className="text-center"
					description={Liferay.Language.get(
						'connect-a-data-source-containing-account-data'
					)}
					title={Liferay.Language.get('no-account-data-synced')}
				>
					<ClayLink
						decoration="underline"
						href={URLConstants.HelpConnectDxp}
						key="helpConnectDxpText"
						target="_blank"
					>
						{Liferay.Language.get('learn-more-about-data-sources')}

						<span className="inline-item inline-item-after">
							<ClayIcon fontSize={10} symbol="shortcut" />
						</span>
					</ClayLink>
					{authorized && (
						<ClayLink
							button
							className="button-root mt-3"
							displayType="secondary"
							href={toRoute(Routes.SETTINGS_DATA_SOURCE_LIST, {
								groupId,
							})}
						>
							{Liferay.Language.get('connect-data-source')}
						</ClayLink>
					)}
				</EmptyState>
			</div>
		);
	}

	return (
		<ul className="property-subgroups-list active">
			{filteredProperties.toArray().map(({label, properties}, i) => (
				<li key={`${label}-${i}`}>
					{label && (
						<div className="property-subgroup-label">{label}</div>
					)}

					{properties.isEmpty() ? (
						<div className="empty-message">
							{Liferay.Language.get('no-results-were-found')}
						</div>
					) : (
						<ul className="properties-list">
							{properties.toArray().map((property, i) => {
								const {label, name, propertyKey, type} =
									property;

								return (
									<CriteriaSidebarItem
										className={`color--${propertyKey}`}
										defaultValue={getDefaultValue(property)}
										key={`${name}-${i}`}
										label={label}
										name={name}
										property={property}
										propertyKey={propertyKey}
										type={type}
									/>
								);
							})}
						</ul>
					)}
				</li>
			))}
		</ul>
	);
};

export default CriteriaSidebarCollapse;
