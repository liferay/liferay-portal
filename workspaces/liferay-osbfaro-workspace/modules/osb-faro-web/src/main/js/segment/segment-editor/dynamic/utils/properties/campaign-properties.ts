import {Property} from 'shared/util/records';
import {PropertyTypes} from '../constants';

// `id` is the field `campaigns.filter` matches on.

const CAMPAIGN_PROPERTY = new Property({
	entityName: Liferay.Language.get('campaign'),
	label: Liferay.Language.get('campaign-name'),
	name: 'id',
	propertyKey: 'campaign',
	type: PropertyTypes.Campaign,
});

export default CAMPAIGN_PROPERTY;
