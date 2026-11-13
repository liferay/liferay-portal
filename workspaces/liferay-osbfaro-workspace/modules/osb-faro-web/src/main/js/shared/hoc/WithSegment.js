import withAction from './WithAction';
import {fetchSegment} from '../actions/segments';
import {Routes, SEGMENTS, toRoute} from 'shared/util/router';

export const withSegment = (includeReferencedObjects = false) =>
	withAction(
		({groupId, id}) =>
			fetchSegment({groupId, includeReferencedObjects, segmentId: id}),
		(state, {id}) => state.getIn(['segments', id]),
		{
			errorPageProps: ({channelId, groupId}) => ({
				href: toRoute(Routes.CONTACTS_LIST_ENTITY, {
					channelId,
					groupId,
					type: SEGMENTS,
				}),
				linkLabel: Liferay.Language.get('go-to-segments'),
				message: Liferay.Language.get(
					'the-segment-you-are-looking-for-does-not-exist'
				),
				subtitle: Liferay.Language.get('segment-was-not-found'),
			}),
			propName: 'segment',
		}
	);

export default withSegment();
