/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
const emptyImage = require('@clayui/css/src/images/images/empty_state.svg');
const emptyImageReducedMotion = require('@clayui/css/src/images/images/empty_state_reduced_motion.svg');
const imagesSpritemap = require('@clayui/css/src/images/images/empty_states.svg');
const searchImage = require('@clayui/css/src/images/images/search_state.svg');
const successImage = require('@clayui/css/src/images/images/success_state.svg');
import ClayIcon from '@clayui/icon';
import React from 'react';

import ClayEmptyState from '../src';

export default {
	component: ClayEmptyState,
	title: 'Design System/Components/EmptyState',
};
export function Default() {
	return (
		<ClayEmptyState>
			<ClayButton displayType="primary">Button</ClayButton>
		</ClayEmptyState>
	);
}
export function Title() {
	return (
		<ClayEmptyState title={null}>
			<ClayButton displayType="primary">Button</ClayButton>
		</ClayEmptyState>
	);
}
export function EmptyState(args: any) {
	return (
		<ClayEmptyState imgSrc={emptyImage} small={args.small}>
			<ClayButton displayType="secondary">Button</ClayButton>
		</ClayEmptyState>
	);
}

EmptyState.args = {
	small: false,
};
export function SearchState() {
	return (
		<ClayEmptyState
			description="This is a description of what the button will allow you to do"
			imgSrc={searchImage}
			title="No content yet"
		/>
	);
}
export function SuccessState() {
	return (
		<ClayEmptyState
			description="You don't have more notifications to review"
			imgSrc={successImage}
			title="Hurray"
		/>
	);
}
export function WithIcon(args: any) {
	return (
		<div
			className={
				args.reducedMotion ? 'c-prefers-reduced-motion' : undefined
			}
		>
			<ClayEmptyState
				className={args.small ? 'c-empty-state-sm' : undefined}
				description="You don't have more notifications to review"
				image={
					<ClayIcon
						illustration
						spritemap={imagesSpritemap}
						symbol={args.symbol}
					/>
				}
				title="Hurray"
			>
				<ClayButton displayType="primary">Button</ClayButton>
			</ClayEmptyState>
		</div>
	);
}

WithIcon.args = {
	reducedMotion: false,
	small: false,
	symbol: 'success-state',
};

WithIcon.argTypes = {
	reducedMotion: {
		control: {type: 'boolean'},
	},
	symbol: {
		control: {type: 'select'},
		options: [
			'action-toolbar',
			'ai-burst',
			'ai-chat',
			'ai-prompt',
			'ai-workflow',
			'audience',
			'connectors',
			'contenttypes',
			'discovery',
			'document',
			'knowledge-graph',
			'pagebuilder',
			'products',
			'success-state',
			'empty-state',
			'search-state',
			'toolbar-canvas',
			'toolbar-list',
			'workflow',
		],
	},
};

export function WithImage() {
	return (
		<ClayEmptyState
			description="You don't have more notifications to review"
			imgSrc={successImage}
			title="Hurray"
		/>
	);
}
export function WithReducedMotion() {
	return (
		<div className="c-prefers-reduced-motion">
			<ClayEmptyState
				description="You don't have more notifications to review"
				imgSrc={successImage}
				imgSrcReducedMotion={emptyImageReducedMotion}
				title="Hurray"
			/>
		</div>
	);
}
export function WithReducedMotionNull() {
	return (
		<div className="c-prefers-reduced-motion">
			<ClayEmptyState
				description="You don't have more notifications to review"
				imgSrc={successImage}
				imgSrcReducedMotion={null}
				title="Hurray"
			/>
		</div>
	);
}
