import {EventNames} from 'shared/util/constants';
import {List} from 'immutable';
import {Property} from 'shared/util/records';
import {PropertyTypes} from '../constants';

const createWebProperty = ({
	label,
	name
}: {
	label: string;
	name: string;
}): Property =>
	new Property({
		entityName: Liferay.Language.get('individual'),
		label,
		name,
		propertyKey: 'web',
		type: PropertyTypes.Behavior
	});

const WEB_BEHAVIORS = List(
	[
		{label: Liferay.Language.get('click'), name: EventNames.Click},
		{label: Liferay.Language.get('comment'), name: EventNames.Comment},
		{label: Liferay.Language.get('download'), name: EventNames.Download},
		{
			label: Liferay.Language.get('impression'),
			name: EventNames.Impression
		},
		{label: Liferay.Language.get('submit'), name: EventNames.Submit},
		{label: Liferay.Language.get('view'), name: EventNames.View}
	].map(createWebProperty)
);

export default WEB_BEHAVIORS;
