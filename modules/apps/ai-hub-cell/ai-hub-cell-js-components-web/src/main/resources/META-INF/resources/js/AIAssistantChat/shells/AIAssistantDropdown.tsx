/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayDropDown from '@clayui/drop-down';
import React, {useCallback} from 'react';

import AIAssistantPanelHeader from '../components/AIAssistantPanelHeader';

interface AIAssistantDropdownProps {
	active: boolean;
	bodyNode: HTMLElement;
	onActiveChange: (active: boolean) => void;
	onExpand?: () => void;
	trigger: React.ReactElement;
}

const AIAssistantDropdown: React.FC<AIAssistantDropdownProps> = ({
	active,
	bodyNode,
	onActiveChange,
	onExpand,
	trigger,
}) => {
	const mountBodyNode = useCallback(
		(node: HTMLDivElement | null) => {
			node?.appendChild(bodyNode);
		},
		[bodyNode]
	);

	return (
		<ClayDropDown
			active={active}
			alignmentPosition={4}
			className="ai-assistant-chat__dropdown"
			hasRightSymbols={false}
			menuElementAttrs={{
				className: 'ai-assistant-chat__panel cadmin',
			}}
			onActiveChange={onActiveChange}
			trigger={trigger}
		>
			<div className="ai-assistant ai-assistant-chat__dropdown-container">
				<AIAssistantPanelHeader
					onClose={() => onActiveChange(false)}
					onToggleExpanded={onExpand}
				/>

				<div
					className="ai-assistant-chat__body-slot"
					ref={mountBodyNode}
				/>
			</div>
		</ClayDropDown>
	);
};

export default AIAssistantDropdown;
