/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayLayout from '@clayui/layout';
import {
	ChatActionButton,
	ChatDropdownContainer,
	ChatPanel,
} from 'frontend-js-components-web';
import React, {useState} from 'react';

const SAMPLE_HEIGHT = 500;

function useTrigger() {
	const [open, setOpen] = useState(false);

	const trigger = (
		<ClayButton onClick={() => setOpen(!open)}>
			{open
				? Liferay.Language.get('close')
				: Liferay.Language.get('open')}
		</ClayButton>
	);

	return {open, setOpen, trigger};
}

function ChatPanelSample() {
	const noop = () => {};

	return (
		<ChatPanel>
			<ChatPanel.Header title={Liferay.Language.get('ai-assistant')}>
				<ChatActionButton
					ariaLabel={Liferay.Language.get('add')}
					onClick={noop}
					symbol="plus"
				/>

				<ChatActionButton
					ariaLabel={Liferay.Language.get('share')}
					onClick={noop}
					symbol="share"
				/>
			</ChatPanel.Header>

			<ChatPanel.Body>
				{Liferay.Language.get('example-text')}
			</ChatPanel.Body>
		</ChatPanel>
	);
}

function ChatDropdownSample() {
	const {open, setOpen, trigger} = useTrigger();

	return (
		<ClayLayout.Col size={4}>
			<h4>Dropdown</h4>

			<div style={{height: SAMPLE_HEIGHT}}>
				<ChatDropdownContainer
					onOpenChange={setOpen}
					open={open}
					trigger={trigger}
				>
					<ChatPanelSample />
				</ChatDropdownContainer>
			</div>
		</ClayLayout.Col>
	);
}

export default function ChatPanelSamples() {
	return (
		<ClayLayout.Row>
			<ChatDropdownSample />
		</ClayLayout.Row>
	);
}
