import withAction from './WithAction';
import {fetchIndividual} from '../actions/individuals';
import {INDIVIDUALS, Routes, toRoute} from 'shared/util/router';

export default withAction(
	({channelId, groupId, id}) =>
		fetchIndividual({channelId, groupId, individualId: id}),
	(state, {id}) => state.getIn(['individuals', id]),
	{
		errorPageProps: ({channelId, groupId}) => ({
			href: toRoute(Routes.CONTACTS_LIST_ENTITY, {
				channelId,
				groupId,
				type: INDIVIDUALS,
			}),
			linkLabel: Liferay.Language.get('go-to-individuals'),
			message: Liferay.Language.get(
				'the-individual-you-are-looking-for-does-not-exist'
			),
			subtitle: Liferay.Language.get('individual-was-not-found'),
		}),
		propName: 'individual',
	}
);
