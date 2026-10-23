import {schema} from 'normalizr';

function processStrategy(entity) {
	return {data: entity};
}

export const account = new schema.Entity('accounts', {}, {processStrategy});
export const dataSource = new schema.Entity(
	'dataSources',
	{},
	{processStrategy}
);
export const individual = new schema.Entity(
	'individuals',
	{},
	{processStrategy}
);
export const interest = new schema.Entity('interests', {}, {processStrategy});
export const project = (groupId) =>
	new schema.Entity(
		'projects',
		{},
		{
			idAttribute: ({groupId: id}) => groupId || id,
			processStrategy,
		}
	);

export const segment = new schema.Entity('segments', {}, {processStrategy});
export const user = new schema.Entity('users', {}, {processStrategy});

export const accounts = new schema.Array(account);
export const dataSources = new schema.Array(dataSource);
export const individuals = new schema.Array(individual);
export const projects = new schema.Array(project);
export const segments = new schema.Array(segment);

/**
 * Returns the normalization schema for an entity's distribution. The
 * reason we need to take the contactsEntityId is that there is nothing
 * in the response that we can use to identify to what the data belongs
 * to. Eventually, we will want to refactor this to probably not rely
 * on the normalizer reducer at all
 */
export function getDistributionSchema(individualSegmentId) {
	return new schema.Entity(
		'distributions',
		{},
		{
			idAttribute: () => individualSegmentId,
			processStrategy,
		}
	);
}
