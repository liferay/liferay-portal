/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {ReactNode} from 'react';

import {Header} from '../Header/Header';

export type DashboardListItems = {
	itemName: string;
	itemTitle: string;
	symbol: string;
};

type DashBoardPageProps = {
	buttonDisabled?: boolean;
	buttonMessage?: string | ReactNode | boolean;
	children: ReactNode;
	messages: {
		description: string;
		title: string;
	};
	onButtonClick?: () => void;
};

const DashboardPage: React.FC<DashBoardPageProps> = ({
	buttonDisabled = false,
	buttonMessage,
	children,
	messages,
	onButtonClick,
}) => (
	<div className="w-100">
		<div className="align-items-center d-flex justify-content-between">
			<Header description={messages.description} title={messages.title} />

			{buttonMessage && (
				<ClayButton disabled={buttonDisabled} onClick={onButtonClick}>
					{buttonMessage}
				</ClayButton>
			)}
		</div>

		{children}
	</div>
);

export {DashboardPage};
