import BasePage from 'settings/components/BasePage';
import React from 'react';
import RequestList from '../hocs/RequestList';
import {getDataPrivacy} from 'shared/util/breadcrumbs';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useParams} from 'react-router-dom';
import {useTimeZone} from 'shared/hooks/useTimeZone';

export const RequestLog = () => {
	const {groupId} = useParams();
	const currentUser = useCurrentUser();
	const {timeZoneId} = useTimeZone();

	return (
		<BasePage
			breadcrumbItems={[
				getDataPrivacy({groupId}),
				{
					active: true,
					label: Liferay.Language.get('request-log')
				}
			]}
			className='request-log-page-root'
			documentTitle={Liferay.Language.get('request-log')}
		>
			<RequestList currentUser={currentUser} timeZoneId={timeZoneId} />
		</BasePage>
	);
};

export default RequestLog;
