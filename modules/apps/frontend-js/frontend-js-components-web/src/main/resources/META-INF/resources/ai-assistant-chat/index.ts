/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import ChatPanelComponent, {ChatPanelProps} from './ChatPanel';
import ChatPanelBody from './ChatPanelBody';
import ChatPanelHeader from './ChatPanelHeader';

const ChatPanel = ChatPanelComponent as React.FC<ChatPanelProps> & {
	Body: typeof ChatPanelBody;
	Header: typeof ChatPanelHeader;
};

ChatPanel.Header = ChatPanelHeader;
ChatPanel.Body = ChatPanelBody;

export default ChatPanel;
