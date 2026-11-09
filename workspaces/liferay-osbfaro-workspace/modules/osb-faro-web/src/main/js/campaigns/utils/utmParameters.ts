import {ICampaignAcquisitionParameter} from 'shared/api/campaigns';
import {
	ICustomUtmParameter,
	UtmParameter,
	UtmParameters,
} from 'campaigns/utils/types';
import {UTM_PARAMETERS} from 'campaigns/utils/constants';
import {camelCase} from 'lodash';

/**
 * The API sends one item per value, ordered by name and then by usage, so
 * grouping by name in arrival order keeps the most used value first. It names
 * parameters as the URL does (`utm_source`), while the card keys the five
 * standard ones in camel case. Anything else goes through as a custom
 * parameter under its own name.
 */
export function groupUtmParameters(
	parameters: ICampaignAcquisitionParameter[]
): {
	customParameters: ICustomUtmParameter[];
	utmParameters: UtmParameters;
} {
	const customParameters: ICustomUtmParameter[] = [];
	const utmParameters: UtmParameters = {};

	parameters.forEach(({name, standard, value}) => {
		const parameter = camelCase(name) as UtmParameter;

		if (standard && UTM_PARAMETERS.includes(parameter)) {
			utmParameters[parameter] = [
				...(utmParameters[parameter] ?? []),
				value,
			];
		}
		else {
			const customParameter = customParameters.find(
				(customParameter) => customParameter.name === name
			);

			if (customParameter) {
				customParameter.values.push(value);
			}
			else {
				customParameters.push({name, values: [value]});
			}
		}
	});

	return {customParameters, utmParameters};
}
