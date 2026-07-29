import {EventNames} from 'shared/util/constants';
import {List} from 'immutable';
import {Property} from 'shared/util/records';
import {PropertyTypes} from '../constants';

const createWebProperty = ({
	entityName,
	label,
	name,
}: {
	entityName: string;
	label: string;
	name: string;
}): Property =>
	new Property({
		entityName,
		label,
		name,
		propertyKey: 'web',
		type: PropertyTypes.Behavior,
	});

export const createWebBehaviors = (entityName: string): List<Property> =>
	List(
		[
			{label: Liferay.Language.get('click'), name: EventNames.Click},
			{label: Liferay.Language.get('comment'), name: EventNames.Comment},
			{
				label: Liferay.Language.get('download'),
				name: EventNames.Download,
			},
			{
				label: Liferay.Language.get('impression'),
				name: EventNames.Impression,
			},
			{label: Liferay.Language.get('submit'), name: EventNames.Submit},
			{label: Liferay.Language.get('view'), name: EventNames.View},
		].map(({label, name}) => createWebProperty({entityName, label, name}))
	);

const WEB_BEHAVIORS = createWebBehaviors(Liferay.Language.get('individual'));

export default WEB_BEHAVIORS;
