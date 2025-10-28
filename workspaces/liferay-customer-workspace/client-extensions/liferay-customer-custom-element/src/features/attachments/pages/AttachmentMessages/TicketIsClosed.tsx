/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useAppPropertiesContext} from '~/contexts/AppPropertiesContext';
import i18n from '~/utils/I18n';

import AttachmentMessage from '../../components/AttachmentMessage/AttachmentMessage';

const TicketIsClosed = () => {
	const {createTicketURL} = useAppPropertiesContext();

	return (
		<AttachmentMessage
			icon="warning-full"
			subtitle={i18n.translate(
				'no-further-edits-can-be-made-when-tickets-are-closed-please-open-a-new-support-ticket-if-assistance-is-needed'
			)}
			title={i18n.translate('this-ticket-has-been-closed')}
		>
			<a className="btn btn-primary" href={createTicketURL}>
				{i18n.translate('create-new-ticket')}
			</a>
		</AttachmentMessage>
	);
};

export default TicketIsClosed;
