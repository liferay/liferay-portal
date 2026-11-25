import React, {createContext, useEffect, useReducer} from 'react';
import {
	convertReferencedObjectsToProperties,
	getPropertyContextFromRaw,
	getPropertyNameFromRaw,
} from '../utils/utils';
import {FieldOwnerTypes} from 'shared/util/constants';
import {Map} from 'immutable';
import {Property, Segment} from 'shared/util/records';

export enum ActionType {
	AddEntities = 'addEntities',
	AddEntity = 'addEntity',
	AddProperty = 'addProperty',
	ReplaceAll = 'replaceAll',
}

export const ACTION_TYPES: {[key: string]: ActionType} = {
	addEntities: ActionType.AddEntities,
	addEntity: ActionType.AddEntity,
	addProperty: ActionType.AddProperty,
};

export enum EntityType {
	Assets = 'assets',
	Attributes = 'attributes',
	Campaigns = 'campaigns',
	CustomEvents = 'custom-events',
	Groups = 'groups',
	Organizations = 'organizations',
	Roles = 'roles',
	Teams = 'teams',
	UserGroups = 'user-groups',
	Users = 'users',
}

export type AddEntities = (params: {
	entityType: EntityType;
	payload: any[];
}) => void;
export type AddEntity = (params: {
	entityType: EntityType;
	payload: any;
}) => void;
export type AddProperty = (payload: Property) => void;

export type ReferencedEntities = Map<string, Map<string, Map<string, any>>>;
export type ReferencedProperties = Map<string, Map<string, Property>>;

export const ReferencedObjectsContext = createContext<{
	addEntities?: AddEntities;
	addEntity?: AddEntity;
	addProperty?: AddProperty;
	referencedEntities: ReferencedEntities;
	referencedProperties: ReferencedProperties;
}>({
	referencedEntities: Map(),
	referencedProperties: Map(),
});

type Action = {
	entityType?: EntityType;
	payload: any;
	type: ActionType;
};

export const referencedPropertiesReducer = (
	state: ReferencedProperties,
	{payload, type}: Action
): ReferencedProperties => {
	switch (type) {
		case ActionType.AddProperty:
			if (
				[
					FieldOwnerTypes.Account,
					FieldOwnerTypes.Individual,
					FieldOwnerTypes.Organization,
				].includes(payload.propertyKey)
			) {
				return state.setIn(
					[
						payload.propertyKey,
						getPropertyContextFromRaw(payload.name),
						getPropertyNameFromRaw(payload.name),
					],
					payload
				);
			}
			else if (payload.propertyKey === 'event') {
				return state.setIn(
					[payload.propertyKey, payload.name],
					payload
				);
			}
			else if (payload.propertyKey === 'vocabulary') {
				return state.setIn(['vocabulary', payload.name], payload);
			}
			else if (payload.propertyKey === 'tag') {
				return state.setIn(['tag', payload.name], payload);
			}

			return state;
		case ActionType.ReplaceAll:
			return payload;
		default:
			throw new Error('Unhandled action type: ${type}');
	}
};

export const referencedEntitiesReducer = (
	state: ReferencedEntities,
	{entityType, payload, type}: Action
): ReferencedEntities => {
	switch (type) {
		case ActionType.AddEntities:
			return state.mergeIn(
				[entityType],
				Map(
					payload.map((item: Map<string, any>) => [
						item.get('id'),
						item,
					])
				)
			);
		case ActionType.AddEntity:
			return state.setIn(
				[entityType, getPropertyNameFromRaw(payload.get('id'))],
				payload
			);
		case ActionType.ReplaceAll:
			return payload;
		default:
			throw new Error('Unhandled action type: ${type}');
	}
};

const createReferencedEntitiesIMapFromSegment = (
	segment: Segment
): ReferencedEntities => {
	const referencedObjects = segment.referencedObjects ?? Map();

	return Map({
		[EntityType.Assets]: referencedObjects.get(EntityType.Assets),
		[EntityType.Attributes]: referencedObjects.get(
			EntityType.Attributes,
			Map({})
		),
		[EntityType.Campaigns]: referencedObjects.get(
			EntityType.Campaigns,
			Map({})
		),
		[EntityType.CustomEvents]: referencedObjects.get(
			EntityType.CustomEvents
		),
		[EntityType.Groups]: referencedObjects.get(EntityType.Groups),
		[EntityType.Organizations]: referencedObjects.get(
			EntityType.Organizations
		),
		[EntityType.Roles]: referencedObjects.get(EntityType.Roles),
		[EntityType.Teams]: referencedObjects.get(EntityType.Teams),
		[EntityType.UserGroups]: referencedObjects.get(EntityType.UserGroups),
		[EntityType.Users]: referencedObjects.get(EntityType.Users),
	});
};

export const ReferencedObjectsProvider = ({
	children,
	segment,
}: {
	children: React.ReactNode;
	segment?: Segment;
}) => {
	const [referencedEntities, referencedEntitiesDispatch] = useReducer(
		referencedEntitiesReducer,
		segment
			? createReferencedEntitiesIMapFromSegment(segment)
			: Map<string, any>()
	);

	const [referencedProperties, referencedPropertiesDispatch] = useReducer(
		referencedPropertiesReducer,
		segment
			? convertReferencedObjectsToProperties(
					segment.get('referencedObjects', Map())
				)
			: Map<string, any>()
	);

	useEffect(() => {
		referencedEntitiesDispatch({
			payload: segment
				? createReferencedEntitiesIMapFromSegment(segment)
				: Map(),

			type: ActionType.ReplaceAll,
		});

		referencedPropertiesDispatch({
			payload: segment
				? convertReferencedObjectsToProperties(
						segment.get('referencedObjects', Map())
					)
				: Map(),
			type: ActionType.ReplaceAll,
		});
	}, [segment]);

	return (
		<ReferencedObjectsContext.Provider
			value={{
				addEntities: ({
					entityType,
					payload,
				}: {
					entityType: EntityType;
					payload: any[];
				}) => {
					referencedEntitiesDispatch({
						entityType,
						payload,
						type: ActionType.AddEntities,
					});
				},
				addEntity: ({
					entityType,
					payload,
				}: {
					entityType: EntityType;
					payload: any;
				}) => {
					referencedEntitiesDispatch({
						entityType,
						payload,
						type: ActionType.AddEntity,
					});
				},
				addProperty: (payload: Property) =>
					referencedPropertiesDispatch({
						payload,
						type: ActionType.AddProperty,
					}),
				referencedEntities,
				referencedProperties,
			}}
		>
			{children}
		</ReferencedObjectsContext.Provider>
	);
};

export const withReferencedObjectsProvider =
	<P extends {segment?: Segment}>(WrappedComponent: React.ComponentType<P>) =>
	(props: P) => (
		<ReferencedObjectsProvider segment={props.segment}>
			<WrappedComponent {...props} />
		</ReferencedObjectsProvider>
	);

export const withReferencedObjectsConsumer =
	<P extends object>(WrappedComponent: React.ComponentType<P>) =>
	(
		props: Omit<
			P,
			| 'addEntities'
			| 'addEntity'
			| 'addProperty'
			| 'referencedEntities'
			| 'referencedProperties'
		>
	) => (
		<ReferencedObjectsContext.Consumer>
			{(referencedObjects) => (
				<WrappedComponent
					{...(props as P)}
					{...(referencedObjects as any)}
				/>
			)}
		</ReferencedObjectsContext.Consumer>
	);
