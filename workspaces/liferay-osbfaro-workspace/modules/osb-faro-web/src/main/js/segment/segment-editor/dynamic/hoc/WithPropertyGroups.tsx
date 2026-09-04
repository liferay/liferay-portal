import * as API from 'shared/api';
import React from 'react';
import {compose} from 'redux';
import {
	convertFieldMappingToAccountProperty,
	convertFieldMappingToIndividualProperty,
	convertFieldMappingToOrganizationProperty,
	createInterestProperty,
} from '../utils/utils';
import {
	FieldContexts,
	FieldOwnerTypes,
	SegmentCategories,
	SegmentTypes,
} from 'shared/util/constants';
import {
	createWebBehaviors,
	INDIVIDUAL_PROPERTIES,
	ORGANIZATION_PROPERTIES,
	SESSION_PROPERTIES,
} from '../utils/properties';
import {List} from 'immutable';
import {PropertyGroup, PropertySubgroup} from 'shared/util/records';
import {withError, withLoading, withQuery} from 'shared/hoc';

const MAX_DELTA = 500;

const fetchPropertyGroups = ({
	channelId,
	groupId,
	segmentCategory,
	type,
}: {
	channelId: string;
	groupId: string;
	segmentCategory?: string;
	type?: string;
}): Promise<any> =>
	Promise.all([
		API.fieldMappings.search({
			context: FieldContexts.Demographics,
			delta: MAX_DELTA,
			groupId,
			ownerType: FieldOwnerTypes.Individual,
		}),
		API.fieldMappings.search({
			context: FieldContexts.Custom,
			delta: MAX_DELTA,
			groupId,
			ownerType: FieldOwnerTypes.Individual,
		}),
		API.fieldMappings.search({
			channelId,
			context: FieldContexts.Account,
			delta: MAX_DELTA,
			groupId,
			ownerType: FieldOwnerTypes.Account,
		}),
		Promise.resolve(ORGANIZATION_PROPERTIES),
		API.fieldMappings.search({
			context: FieldContexts.Custom,
			delta: MAX_DELTA,
			groupId,
			ownerType: FieldOwnerTypes.Organization,
		}),
		Promise.resolve(
			createWebBehaviors(
				segmentCategory === SegmentCategories.Account
					? Liferay.Language.get('account')
					: Liferay.Language.get('individual')
			)
		),
		type === SegmentTypes.Batch &&
		segmentCategory !== SegmentCategories.Account
			? API.interests.searchKeywords({
					channelId,
					delta: MAX_DELTA,
					groupId,
				})
			: Promise.resolve({items: []}),
		Promise.resolve(SESSION_PROPERTIES),
		Promise.resolve({items: [], totalCount: 0}),
	]);

const mapResultToProps = (
	[
		individualDemographicsMappings,
		individualCustomMappings,
		accountMappings,
		organizationProperties,
		organizationCustomMappings,
		webBehaviors,
		interestKeywords,
		sessionProperties,
	]: any[],
	{
		segmentCategory,
		type,
	}: {segmentCategory: SegmentCategories; type: SegmentTypes}
) => {
	const isAccountSegment = segmentCategory === SegmentCategories.Account;

	const individualCriteriaEnabled =
		type === SegmentTypes.Batch && !isAccountSegment;

	const individualDemographicProperties =
		individualDemographicsMappings.items.map(
			convertFieldMappingToIndividualProperty
		);

	let individualSubgroupsIList = List([
		new PropertySubgroup({
			properties: List(
				individualDemographicProperties.concat(INDIVIDUAL_PROPERTIES)
			),
		}),
	]);

	individualSubgroupsIList = individualSubgroupsIList.push(
		new PropertySubgroup({
			label: Liferay.Language.get('dxp-custom-fields'),
			properties: List(
				individualCustomMappings.items.map(
					convertFieldMappingToIndividualProperty
				)
			),
		})
	);

	const propertyGroupsIList = List(
		[
			new PropertyGroup({
				label: Liferay.Language.get('events'),
				propertyKey: 'web',
				propertySubgroups: List([
					new PropertySubgroup({
						label: Liferay.Language.get('default-events'),
						properties: webBehaviors,
					}),
				]),
			}),
			individualCriteriaEnabled &&
				new PropertyGroup({
					label: Liferay.Language.get('individual'),
					propertyKey: FieldOwnerTypes.Individual,
					propertySubgroups: individualSubgroupsIList,
				}),
			type === SegmentTypes.Batch &&
				new PropertyGroup({
					label: Liferay.Language.get('account'),
					propertyKey: FieldOwnerTypes.Account,
					propertySubgroups: List([
						new PropertySubgroup({
							properties: List(
								accountMappings.items.map(
									convertFieldMappingToAccountProperty
								)
							),
						}),
					]),
				}),
			individualCriteriaEnabled &&
				new PropertyGroup({
					label: Liferay.Language.get('interests'),
					propertyKey: 'interest',
					propertySubgroups: List([
						new PropertySubgroup({
							properties: List(
								interestKeywords.items.map(
									createInterestProperty
								)
							),
						}),
					]),
				}),
			individualCriteriaEnabled &&
				new PropertyGroup({
					label: Liferay.Language.get('session'),
					propertyKey: 'session',
					propertySubgroups: List([
						new PropertySubgroup({
							properties: List(sessionProperties),
						}),
					]),
				}),
			individualCriteriaEnabled &&
				new PropertyGroup({
					label: Liferay.Language.get('vocabularies-and-categories'),
					propertyKey: 'vocabulary',
					propertySubgroups: List([
						new PropertySubgroup({properties: List()}),
					]),
				}),
			individualCriteriaEnabled &&
				new PropertyGroup({
					label: Liferay.Language.get('tags'),
					propertyKey: 'tag',
					propertySubgroups: List([
						new PropertySubgroup({properties: List()}),
					]),
				}),
		].filter(Boolean) as PropertyGroup[]
	);

	if (individualCriteriaEnabled) {
		const organizationPropertyGroup = new PropertyGroup({
			label: Liferay.Language.get('organization'),
			propertyKey: FieldOwnerTypes.Organization,
			propertySubgroups: List([
				new PropertySubgroup({properties: organizationProperties}),
				new PropertySubgroup({
					label: Liferay.Language.get('dxp-custom-fields'),
					properties: List(
						organizationCustomMappings.items.map(
							convertFieldMappingToOrganizationProperty
						)
					),
				}),
			]),
		});

		return {
			propertyGroupsIList: propertyGroupsIList.push(
				organizationPropertyGroup
			),
		};
	}

	return {propertyGroupsIList};
};

export const withPropertyGroups = (
	WrappedComponent: React.ComponentType<any>
) =>
	class extends React.Component<{
		propertyGroupsIList: List<PropertyGroup>;
	}> {
		render() {
			const {propertyGroupsIList, ...otherProps} = this.props;

			return (
				<WrappedComponent
					{...otherProps}
					propertyGroupsIList={propertyGroupsIList}
				/>
			);
		}
	};

/**
 * Requests the property groups the editor needs, with the loading and error
 * states `withRequest` would provide.
 *
 * This composes the same three HOCs `withRequest` composes, but once per
 * wrapped component rather than on every render. `withRequest` builds them
 * inside its own render, which makes the subtree a new component type each
 * time, so any re-render of an ancestor unmounts and remounts the editor. The
 * editor cannot survive that: the remount discards the `useBlocker`
 * registration behind its unsaved changes guard, and React Router drops the
 * blocked navigation with no prompt, leaving the user stuck on the page. See
 * LPD-104396.
 */
const withPropertyGroupsRequest = (
	WrappedComponent: React.ComponentType<any>
) =>
	compose(

		// The third argument hands `withQuery`'s own result straight through,
		// which is what it does when the argument is omitted. The result is
		// mapped below instead, once the loading and error states have been
		// peeled off it.

		withQuery(
			fetchPropertyGroups,
			(props: any) => props,
			(resultProps: any) => resultProps
		),
		withError({page: true}),
		withLoading()
	)(({data, ...otherProps}: any) => (
		<WrappedComponent
			{...otherProps}
			{...mapResultToProps(data, otherProps)}
		/>
	));

export default compose(withPropertyGroupsRequest, withPropertyGroups);
