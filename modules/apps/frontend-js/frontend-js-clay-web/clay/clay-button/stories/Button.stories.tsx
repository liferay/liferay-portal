/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import ClayButton, {ClayAIButton, ClayButtonWithIcon} from '../src';

export default {
	argTypes: {
		displayType: {
			control: {type: 'select'},
			options: [
				'ai',
				'link',
				'primary',
				'secondary',
				'success',
				'info',
				'warning',
				'danger',
				'unstyled',
				'beta',
				'beta-dark',
			],
		},
		size: {
			control: {type: 'select'},
			options: [null, 'xs', 'sm'],
		},
	},
	component: ClayButton,
	title: 'Design System/Components/Button',
};
export function Default(args: any) {
	return (
		<ClayButton
			block={args.block}
			borderless={args.borderless}
			dark={args.dark}
			disabled={args.disabled}
			displayType={args.displayType}
			loading={args.loading}
			monospaced={args.monospaced}
			outline={args.outline}
			rounded={args.rounded}
			size={args.size}
			small={args.small}
			translucent={args.translucent}
		>
			{args.children}
		</ClayButton>
	);
}

Default.args = {
	block: false,
	borderless: false,
	children: 'Click Me',
	dark: false,
	disabled: false,
	displayType: 'primary',
	loading: false,
	monospaced: false,
	outline: false,
	rounded: false,
	small: false,
	translucent: false,
};
export function Group(args: any) {
	return (
		<ClayButton.Group spaced={args.spaced}>
			<ClayButton>This</ClayButton>

			<ClayButton displayType="secondary">is</ClayButton>

			<ClayButton>a</ClayButton>

			<ClayButton displayType="secondary">button</ClayButton>

			<ClayButton>group.</ClayButton>
		</ClayButton.Group>
	);
}

Group.args = {
	spaced: false,
};
export function GroupVertical(args: any) {
	return (
		<ClayButton.Group spaced={args.spaced} vertical>
			<ClayButton>This</ClayButton>

			<ClayButton displayType="secondary">is</ClayButton>

			<ClayButton>a</ClayButton>

			<ClayButton displayType="secondary">button</ClayButton>

			<ClayButton>group.</ClayButton>
		</ClayButton.Group>
	);
}

GroupVertical.args = {
	spaced: false,
};
export function Icon() {
	return (
		<>
			<ClayButtonWithIcon aria-label="Trash" symbol="trash" />
			<ClayButtonWithIcon
				aria-label="Cog"
				displayType="secondary"
				symbol="cog"
			/>

			<ClayButtonWithIcon
				aria-label="Cut"
				displayType="link"
				symbol="cut"
			/>
			<ClayButtonWithIcon
				aria-label="Desktop"
				displayType="unstyled"
				symbol="desktop"
			/>

			<ClayButtonWithIcon
				aria-label="Desktop"
				displayType="unstyled"
				monospaced={false}
				symbol="desktop"
			/>
		</>
	);
}

const AI_STATES = [
	{className: undefined, disabled: false, label: 'Default'},
	{className: 'focus', disabled: false, label: 'Focus'},
	{className: 'active', disabled: false, label: 'Active'},
	{className: undefined, disabled: true, label: 'Disabled'},
];

const AI_VARIANTS = [
	{label: 'Calling (gradient)', props: {gradient: true}},
	{label: 'Primary (solid)', props: {}},
	{label: 'Outline', props: {outline: true}},
	{label: 'Borderless', props: {borderless: true}},
	{label: 'Link', props: {link: true}},
	{label: 'Subtle', props: {translucent: true}},
];

export function AI() {
	return (
		<div className="p-4">
			<p className="text-secondary">
				<code>ClayAIButton</code> renders the AI icon and a{' '}

				<code>Chat with AI</code> label. The <code>gradient</code> prop
				switches the filled variant from solid purple to the "calling"
				gradient; <code>outline</code>, <code>borderless</code>,{' '}

				<code>translucent</code>, and <code>monospaced</code> come from
				the base button. Hover a button to see its hover state.
			</p>

			<table>
				<tbody>
					<tr>
						<td />

						{AI_STATES.map((state) => (
							<td
								className="pb-2 px-3 text-secondary"
								key={state.label}
							>
								{state.label}
							</td>
						))}

						<td className="pb-2 px-3 text-secondary">Icon</td>
					</tr>

					{AI_VARIANTS.map((variant) => (
						<tr key={variant.label}>
							<td className="pr-3 text-secondary">
								{variant.label}
							</td>

							{AI_STATES.map((state) => (
								<td className="p-3" key={state.label}>
									<ClayAIButton
										{...variant.props}
										className={state.className}
										disabled={state.disabled}
									/>
								</td>
							))}

							<td className="p-3">
								<ClayAIButton {...variant.props} monospaced />
							</td>
						</tr>
					))}
				</tbody>
			</table>

			<p className="mt-4 text-secondary">
				With no children, <code>ClayAIButton</code> renders its{' '}

				<code>label</code> (default <code>Chat with AI</code>). A custom{' '}

				<code>label</code> overrides the default, and{' '}

				<code>children</code> take precedence over both.
			</p>

			<table>
				<tbody>
					<tr>
						<td className="pr-3 text-secondary">Default label</td>

						<td className="p-3">
							<ClayAIButton />
						</td>
					</tr>

					<tr>
						<td className="pr-3 text-secondary">Custom label</td>

						<td className="p-3">
							<ClayAIButton label="Ask AI" />
						</td>
					</tr>

					<tr>
						<td className="pr-3 text-secondary">Children</td>

						<td className="p-3">
							<ClayAIButton>Improve writing</ClayAIButton>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	);
}
