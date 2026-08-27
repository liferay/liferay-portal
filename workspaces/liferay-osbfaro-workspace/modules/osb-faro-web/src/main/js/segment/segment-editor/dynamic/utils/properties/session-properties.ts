import {List} from 'immutable';
import {Property} from 'shared/util/records';
import {
	ACQUISITION_PARAMETER_PROPERTY_PREFIX,
	PropertyTypes,
} from '../constants';

export const DEVICE_OPTIONS = [
	{label: Liferay.Language.get('desktop'), value: 'Desktop'},
	{
		label: Liferay.Language.get('smartphone'),
		value: 'Smartphone',
	},
	{label: Liferay.Language.get('tablet'), value: 'Tablet'},
];

/**
 * The channels a session can be acquired through, matched against
 * Session.acquisitionChannel.
 */
export const CHANNEL_OPTIONS = [
	{label: Liferay.Language.get('direct'), value: 'direct'},
	{label: Liferay.Language.get('organic'), value: 'organic'},
	{label: Liferay.Language.get('search'), value: 'search'},
	{label: Liferay.Language.get('paid'), value: 'paid'},
	{label: Liferay.Language.get('referral'), value: 'referral'},
	{label: Liferay.Language.get('social'), value: 'social'},
];

export interface IAcquisitionParameter {

	/**
	 * The criterion's propertyName, prefix included (e.g.
	 * "context/acquisitionSource"). AcquisitionParameterUtil#getFieldName in
	 * osb-asah-common already returns it fully qualified, so it is used as
	 * is rather than composed.
	 */
	fieldName: string;

	/**
	 * The literal query parameter name (e.g. "utm_source", or a custom
	 * "utm_cid") as returned by the acquisition parameters discovery
	 * endpoint.
	 */
	name: string;
}

/**
 * The default UTM parameters every channel is expected to capture, and the
 * backend field name each is stored and filtered under (e.g. "utm_source"
 * is "context/acquisitionSource"), per AcquisitionParameterUtil in
 * osb-asah-common. Used to seed the parameter-name autocomplete before the
 * acquisition parameters discovery endpoint (GET
 * contacts/{groupId}/session/acquisition_parameters) resolves, and as its
 * fallback if that call fails.
 */
export const DEFAULT_UTM_PARAMETER_OPTIONS: IAcquisitionParameter[] = [
	{fieldName: 'context/acquisitionSource', name: 'utm_source'},
	{fieldName: 'context/acquisitionMedium', name: 'utm_medium'},
	{fieldName: 'context/acquisitionCampaign', name: 'utm_campaign'},
	{fieldName: 'context/acquisitionTerm', name: 'utm_term'},
	{fieldName: 'context/acquisitionContent', name: 'utm_content'},
];

/**
 * Human-readable labels for the standard UTM parameters, keyed by the
 * literal query parameter name. Custom parameters returned by the
 * acquisition parameters discovery endpoint have no such translation, so
 * callers should fall back to the raw name for anything not listed here.
 */
const UTM_PARAMETER_LABELS: Record<string, string> = {
	utm_campaign: Liferay.Language.get('utm-campaign'),
	utm_content: Liferay.Language.get('utm-content'),
	utm_medium: Liferay.Language.get('utm-medium'),
	utm_source: Liferay.Language.get('utm-source'),
	utm_term: Liferay.Language.get('utm-term'),
};

export const getUtmParameterLabel = (name: string): string =>
	UTM_PARAMETER_LABELS[name] ?? name;

/**
 * Resolves the field name a UTM Parameter criterion filters on back to the
 * parameter's label, so the criteria card names the parameter the user
 * picked instead of the generic "UTM Parameter" entry it was dragged from.
 * Custom parameters are stored under their own name, which is what gets
 * shown for them.
 */
export const getUtmParameterLabelByFieldName = (fieldName: string): string => {
	const acquisitionParameter = DEFAULT_UTM_PARAMETER_OPTIONS.find(
		(option) => option.fieldName === fieldName
	);

	if (acquisitionParameter) {
		return getUtmParameterLabel(acquisitionParameter.name);
	}

	return fieldName.startsWith(ACQUISITION_PARAMETER_PROPERTY_PREFIX)
		? fieldName.slice(ACQUISITION_PARAMETER_PROPERTY_PREFIX.length)
		: fieldName;
};

const createSessionProperty = ({
	label,
	name,
	options,
	type,
}: {
	label: string;
	name: string;
	options?: {label: string; value: string}[];
	type: PropertyTypes;
}) =>
	new Property({
		entityName: Liferay.Language.get('session'),
		label,
		name,
		options,
		propertyKey: 'session',
		type,
	});

const SESSION_PROPERTIES = List(
	[
		{
			label: Liferay.Language.get('browser'),
			name: 'context/browserName',
			type: PropertyTypes.SessionText,
		},
		{
			label: Liferay.Language.get('channel'),
			name: 'context/channel',
			options: CHANNEL_OPTIONS,
			type: PropertyTypes.SessionChannel,
		},
		{
			label: Liferay.Language.get('device'),
			name: 'context/deviceType',
			options: DEVICE_OPTIONS,
			type: PropertyTypes.SessionText,
		},
		{
			label: Liferay.Language.get('geolocation'),
			name: 'context/country',
			type: PropertyTypes.SessionGeolocation,
		},
		{
			label: Liferay.Language.get('referrer'),
			name: 'context/referrer',
			type: PropertyTypes.SessionText,
		},
		{
			label: Liferay.Language.get('date-&-time'),
			name: 'completeDate',
			type: PropertyTypes.SessionDateTime,
		},
		{
			label: Liferay.Language.get('url'),
			name: 'context/url',
			type: PropertyTypes.SessionText,
		},
		{
			label: Liferay.Language.get('utm-parameter'),
			name: 'attribute/utmParameter',
			type: PropertyTypes.SessionUtmParameter,
		},
	].map(createSessionProperty)
);

export default SESSION_PROPERTIES;
