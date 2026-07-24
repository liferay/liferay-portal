/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useEffect, useState} from 'react';

import {getUserAccount} from '../services/getUserAccount';
import Avatar from './Avatar';

const UserChatItem: React.FC<{message: string}> = ({message}) => {
	const [userAccount, setUserAccount] = useState<any>(null);

	useEffect(() => {
		async function getCurrentUserAccount() {
			try {
				setUserAccount(
					await getUserAccount(
						Liferay.ThemeDisplay.getUserId().toString()
					)
				);
			}
			catch (error) {
				console.error('Error fetching user info:', error);
			}
		}

		getCurrentUserAccount();
	}, []);

	return (
		<div className="ai-assistant-chat__user-message">
			<span className="ai-assistant-chat__user-message-content">
				{message}
			</span>

			<div className="ai-assistant-chat__user-message-avatar">
				<Avatar image={userAccount?.image} name={userAccount?.name} />
			</div>
		</div>
	);
};

export default UserChatItem;
