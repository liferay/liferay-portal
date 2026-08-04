import * as API from 'shared/api';
import {Alert} from 'shared/types';
import {
	CredentialTypes,
	DataSourceStates,
	DataSourceStatuses,
	DataSourceTypes,
	EntityTypes,
} from 'shared/util/constants';
import {DataSource} from 'shared/util/records';
import {Map as ImmutableMap, List} from 'immutable';
import {Routes, toRoute} from 'shared/util/router';
import {sub} from 'shared/util/lang';
import {toPromise} from 'shared/components/form';

export const LIFERAY_SITE_TYPE = `${EntityTypes.DataSource}-site`;

/**
 * Default timeout for a dataSource warning alert
 */
export const WARNING_TIMEOUT = 7000;

export const SERVICE_ERROR_MESSAGE_MAP: Record<number, string> = {
	403: Liferay.Language.get(
		'data-source-credentials-invalid-please-contact-your-oauth-administrator'
	),
	404: Liferay.Language.get(
		'there-was-an-error-with-the-servers-connection.-please-try-again-when-data-sources-status-is-active'
	),
};

/**
 * Return config to use in the alert for a service error.
 * @param {Number} code - The http response error code.
 * @returns {Object} The props to use for the alert.
 */
export function getServiceAlertConfig(code: number) {
	const message = ((code: number) => {
		switch (code) {
			case 401:
			case 403:
				return SERVICE_ERROR_MESSAGE_MAP[403];
			case 404:
			default:
				return SERVICE_ERROR_MESSAGE_MAP[404];
		}
	})(code);

	return {
		alertType: Alert.Types.Warning,
		message,
		timeout: WARNING_TIMEOUT,
	};
}

/**
 * Object map for displaying a DataSource status/state
 */
export const STATUS_DISPLAY = {
	[DataSourceStates.ActionNeeded]: {
		display: 'warning',
		label: Liferay.Language.get('action-needed'),
		message: Liferay.Language.get('action-needed'),
	},
	active: {
		display: 'success',
		label: Liferay.Language.get('active'),
		message: Liferay.Language.get(
			'all-data-coming-from-this-data-source-is-up-to-date.-there-are-no-errors-to-report'
		),
	},
	[DataSourceStates.CredentialsInvalid]: {
		display: 'warning',
		label: Liferay.Language.get('invalid-credentials'),
		message: Liferay.Language.get(
			'the-authorization-for-this-data-source-has-expired.-please-reauthorize-the-token-in-the-oauth-tab'
		),
	},
	[DataSourceStates.CredentialsValid]: {
		display: 'info',
		label: Liferay.Language.get('authenticated'),
		message: Liferay.Language.get(
			'you-have-successfully-authenticated.-you-can-now-configure-your-data'
		),
	},
	default: {
		display: 'secondary',
		label: Liferay.Language.get('not-configured'),
		message: Liferay.Language.get(
			'data-source-has-not-been-created.-please-authorize-and-save-to-get-started'
		),
	},
	[DataSourceStates.Disconnected]: {
		display: 'secondary',
		label: Liferay.Language.get('disconnected'),
		message: sub(
			Liferay.Language.get(
				'the-data-source-is-disconnected.-data-is-no-longer-being-synced-from-x,-but-you-can-reconnect-to-resume-syncing'
			),
			[Liferay.Language.get('liferay-dxp')]
		) as string,
	},
	[DataSourceStates.InProgressDeleting]: {
		display: 'info',
		label: Liferay.Language.get('deletion-in-progress'),
		message: Liferay.Language.get(
			'this-data-source-and-its-related-data-are-currently-being-deleted'
		),
	},
	[DataSourceStates.LiferayVersionInvalid]: {
		display: 'warning',
		label: Liferay.Language.get('unsupported-version'),
		message: Liferay.Language.get(
			'this-method-of-connection-does-not-support-the-data-source-liferay-version'
		),
	},
	tokenCredentialsValid: {
		display: 'success',
		label: Liferay.Language.get('connected'),
		message: Liferay.Language.get(
			'you-have-successfully-authenticated-your-token-with-your-data-source.-you-can-now-configure-your-data-in-dxp'
		),
	},
	[DataSourceStates.UndefinedError]: {
		display: 'warning',
		label: Liferay.Language.get('inactive'),
		message: Liferay.Language.get(
			'a-server-error-occurred-while-connected-to-your-data-source.-analytics-cloud-will-attempt-to-automatically-reconnect-during-its-next-regularly-scheduled-sync'
		),
	},
};

/**
 * @param {Object} props
 * @param {DataSource} props.dataSource - A DataSource record.
 * @param {string} props.groupId
 * @returns {string} - Returns a route to the data source authorization page if not valid.
 */
export function dataSourceRedirectFn({
	dataSource,
	groupId,
}: {
	dataSource: DataSource;
	groupId: string;
}) {
	if (isDataSourceValid(dataSource.state)) {
		return null;
	}
	else {
		return toRoute(Routes.SETTINGS_DATA_SOURCE, {
			groupId,
			id: dataSource.id,
		});
	}
}

/**
 * Check if the dataSource state is valid.
 * @param {string} state - The dataSource state.
 * @returns {boolean} - True if this state is valid or false if not valid.
 */
export function isDataSourceValid(state?: string): boolean {
	return [DataSourceStates.CredentialsValid, DataSourceStates.Ready].includes(
		state as DataSourceStates
	);
}

/**
 * Form validator to check if the dataSource name is already taken
 * @param {string} value - The current input value for the dataSource name.
 * @returns {Promise.<Object>} - A Promise resolving with an assertion object
 */
export function validateUniqueName({
	groupId,
	value,
}: {
	groupId: string;
	value: string;
}) {
	return API.dataSource
		.search({
			delta: 1,
			groupId,
			name: value,
			page: 1,
			query: '',
		})
		.then((result) => {
			let error = '';
			if (result.total) {
				error = Liferay.Language.get(
					'a-data-source-already-exists-with-that-name.-please-enter-a-different-name'
				);
			}

			return toPromise(error);
		});
}

/**
 * Get the preselection rule the select properties modal must apply for a
 * DataSource. Demandbase only enriches individuals that browse a synced site,
 * so the properties already syncing a site start out selected. Every other
 * provider type starts with an empty selection, so no filter is returned.
 * @param {DataSource} dataSource - The DataSource the properties are assigned to.
 * @returns {function|undefined} - The filter to pass to the modal, when any.
 */
export function getChannelsAutoSelectFilter(
	dataSource?: DataSource | null
): ((channel: {groupsCount: number}) => boolean) | undefined {
	if (dataSource?.providerType !== DataSourceTypes.Demandbase) {
		return undefined;
	}

	return (channel) => channel.groupsCount > 0;
}

/**
 * Utility for getting the display object for a dataSource state in order
 * to display its status.
 */
export function getDataSourceDisplayObject(dataSource: DataSource) {
	if (!dataSource) {
		return STATUS_DISPLAY.default;
	}

	const {state, status} = dataSource;

	const active = status === DataSourceStatuses.Active;

	const credentialsType = dataSource.getIn(['credentials', 'type']);

	switch (state) {
		case DataSourceStates.AnalyticsClientConfigurationFailure:
		case DataSourceStates.CredentialsInvalid:
		case DataSourceStates.UrlInvalid:
			return STATUS_DISPLAY[DataSourceStates.CredentialsInvalid];
		case DataSourceStates.Disconnected:
			return STATUS_DISPLAY[DataSourceStates.Disconnected];
		case DataSourceStates.CredentialsValid:
			if (
				validAnalyticsConfig(dataSource) ||
				(active &&
					(validContactsConfig(dataSource) ||
						dataSource.sitesSelected ||
						dataSource.contactsSelected))
			) {
				return STATUS_DISPLAY.active;
			}

			return credentialsType === CredentialTypes.Token ||
				credentialsType === CredentialTypes.OAuth2
				? STATUS_DISPLAY.tokenCredentialsValid
				: STATUS_DISPLAY[DataSourceStates.CredentialsValid];
		case DataSourceStates.InProgressDeleting:
			return STATUS_DISPLAY[DataSourceStates.InProgressDeleting];
		case DataSourceStates.LiferayVersionInvalid:
			return STATUS_DISPLAY[DataSourceStates.LiferayVersionInvalid];
		case DataSourceStates.Ready:
			return STATUS_DISPLAY.active;
		case DataSourceStates.UndefinedError:
			return {
				...STATUS_DISPLAY[DataSourceStates.UndefinedError],
				message: [
					STATUS_DISPLAY[DataSourceStates.UndefinedError].message,
				],
			};
		default:
			return STATUS_DISPLAY.default;
	}
}

/**
 * Get an array of ids from a DataSource configuration Map.
 * @param {Map} configIMap - An ImmutableMap of List.<Map>.
 * @param {string} key - The Map key that contains the List of objects.
 * @returns {array} - The List of objects converted to an array of numerical ids.
 */
export function getIdsFromConfiguration(
	configIMap: ImmutableMap<string, unknown>,
	key: string
): number[] {
	const list = configIMap.get(key, List()) as List<
		ImmutableMap<string, unknown>
	>;

	return list.map((itemIMap) => Number(itemIMap?.get('id'))).toArray();
}

/**
 * Helper function for checking validity of a DataSource's analyticsConfiguration.
 */
export function validAnalyticsConfig(dataSource: DataSource): boolean {
	const {provider, providerType} = dataSource;

	const analyticsConfiguration =
		provider && provider.get('analyticsConfiguration');

	if (!analyticsConfiguration) {
		return false;
	}

	switch (providerType) {
		case DataSourceTypes.Liferay:
			return Boolean(
				analyticsConfiguration.get('enableAllSites') ||
					analyticsConfiguration.get('sites').size
			);

		// TODO: Add validation on salesforce anlayticsConfiguration

		case DataSourceTypes.Salesforce:
		default:
			return Boolean(analyticsConfiguration);
	}
}

/**
 * Helper function for checking validity of a DataSource's contactsConfiguration.
 */
export function validContactsConfig(dataSource: DataSource): boolean {
	const {provider, providerType, status} = dataSource;

	const contactsConfiguration =
		provider && provider.get('contactsConfiguration');
	const accountsConfiguration =
		provider && provider.get('accountsConfiguration');

	if (!contactsConfiguration && providerType !== DataSourceTypes.Csv) {
		return false;
	}

	switch (providerType) {
		case DataSourceTypes.Csv:
			return status === DataSourceStatuses.Active;
		case DataSourceTypes.Liferay:
			return Boolean(
				contactsConfiguration.get('enableAllContacts') ||
					contactsConfiguration.get('organizations').size ||
					contactsConfiguration.get('userGroups').size
			);
		case DataSourceTypes.Salesforce:
			return Boolean(
				accountsConfiguration.get('enableAllAccounts') ||
					contactsConfiguration.get('enableAllContacts') ||
					contactsConfiguration.get('enableAllLeads')
			);
		default:
			return Boolean(contactsConfiguration);
	}
}
