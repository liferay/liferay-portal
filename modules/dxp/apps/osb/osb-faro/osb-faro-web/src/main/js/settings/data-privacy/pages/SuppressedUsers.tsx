import BasePage from 'settings/components/BasePage';
import React from 'react';
import SuppressedUserList from '../hocs/SuppressedUserList';
import {getDataPrivacy} from 'shared/util/breadcrumbs';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useTimeZone} from 'shared/hooks/useTimeZone';

export const SuppressedUsers = ({router}) => {
	const currentUser = useCurrentUser();
	const {timeZoneId} = useTimeZone();

	const groupId = router.params.groupId;

	return (
		<BasePage
			breadcrumbItems={[
				getDataPrivacy({groupId}),
				{
					active: true,
					label: Liferay.Language.get('suppressed-user-list')
				}
			]}
			className='suppressed-users-page-root'
			documentTitle={Liferay.Language.get('suppressed-user-list')}
		>
			<SuppressedUserList
				currentUser={currentUser}
				router={router}
				timeZoneId={timeZoneId}
			/>
		</BasePage>
	);
};

export default SuppressedUsers;
