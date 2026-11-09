import {UtmParameter} from 'campaigns/utils/types';

/**
 * The order the rows follow, which is the order a URL usually carries them in
 * rather than an alphabetical one.
 */
export const UTM_PARAMETERS: UtmParameter[] = [
	'utmSource',
	'utmMedium',
	'utmCampaign',
	'utmContent',
	'utmTerm',
];
