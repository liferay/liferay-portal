import {List} from 'immutable';
import {Property} from 'shared/util/records';
import {PropertyTypes} from '../constants';

const createAccountProperty = ({
	label,
	name,
	type,
}: {
	label: string;
	name: string;
	type: PropertyTypes;
}): Property =>
	new Property({
		entityName: Liferay.Language.get('account'),
		id: name,
		label,
		name,
		propertyKey: 'account',
		type,
	});

const ACCOUNT_PROPERTIES = List(
	[
		{
			label: Liferay.Language.get('account-name'),
			name: 'accountName',
			type: PropertyTypes.AccountText,
		},
		{
			label: Liferay.Language.get('account-type'),
			name: 'accountType',
			type: PropertyTypes.AccountText,
		},
		{
			label: Liferay.Language.get('annual-revenue'),
			name: 'annualRevenue',
			type: PropertyTypes.AccountNumber,
		},
		{
			label: Liferay.Language.get('country'),
			name: 'country',
			type: PropertyTypes.AccountText,
		},
		{
			label: Liferay.Language.get('created-date'),
			name: 'createDate',
			type: PropertyTypes.AccountDate,
		},
		{
			label: Liferay.Language.get('currency-code'),
			name: 'currencyCode',
			type: PropertyTypes.AccountText,
		},
		{
			label: Liferay.Language.get('id'),
			name: 'id',
			type: PropertyTypes.AccountText,
		},
		{
			label: Liferay.Language.get('industry'),
			name: 'industry',
			type: PropertyTypes.AccountText,
		},
		{
			label: Liferay.Language.get('last-activity-date'),
			name: 'lastActivityDate',
			type: PropertyTypes.AccountDate,
		},
		{
			label: Liferay.Language.get('lifecycle-stage'),
			name: 'lifecycleStatus',
			type: PropertyTypes.AccountSelectText,
		},
		{
			label: Liferay.Language.get('number-of-employees'),
			name: 'numberOfEmployees',
			type: PropertyTypes.AccountNumber,
		},
		{
			label: Liferay.Language.get('state'),
			name: 'state',
			type: PropertyTypes.AccountText,
		},
		{
			label: Liferay.Language.get('customer-since'),
			name: 'yearStarted',
			type: PropertyTypes.AccountNumber,
		},
	].map(createAccountProperty)
);

export default ACCOUNT_PROPERTIES;
