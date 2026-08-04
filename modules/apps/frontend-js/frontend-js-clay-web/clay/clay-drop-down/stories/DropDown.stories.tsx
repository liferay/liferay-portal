/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Heading} from '@clayui/core';
import {ClayCheckbox, ClayInput, ClayRadio} from '@clayui/form';
import ClayModal, {useModal} from '@clayui/modal';
import React, {useEffect, useRef, useState} from 'react';

import ClayDropDown, {
	Align,
	ClayDropDownWithAI,
	ClayDropDownWithDrilldown,
	ClayDropDownWithItems,
} from '../src';

const ITEMS = [
	{
		onClick: (event) => {
			event.preventDefault();

			alert('you clicked!');
		},
		title: 'clickable',
	},
	{
		type: 'divider' as const,
	},
	{
		items: [
			{
				title: 'one',
				type: 'radio' as const,
				value: 'one',
			},
			{
				title: 'two',
				type: 'radio' as const,
				value: 'two',
			},
		],

		name: 'radio',
		onChange: (value: string) => alert(`New Radio checked ${value}`),
		title: 'radio',
		type: 'radiogroup' as const,
	},
	{
		items: [
			{
				checked: true,
				onChange: () => alert('checkbox changed'),
				title: 'checkbox',
				type: 'checkbox' as const,
			},
			{
				checked: true,
				onChange: () => alert('checkbox changed'),
				title: 'checkbox 1',
				type: 'checkbox' as const,
			},
		],

		title: 'checkbox',
		type: 'group' as const,
	},
	{
		href: '#',
		title: 'linkable',
	},
];

export default {
	argTypes: {
		alignmentPosition: {
			control: {type: 'select'},
			options: Align,
		},
		height: {
			control: {type: 'select'},
			options: ['auto', undefined],
		},
		width: {
			control: {type: 'select'},
			options: ['sm', 'full', undefined],
		},
	},
	title: 'Design System/Components/DropDown',
};
export function Default(args: any) {
	return (
		<ClayDropDown
			alignmentPosition={args.alignmentPosition}
			menuHeight={args.height}
			menuWidth={args.width}
			renderMenuOnClick={args.renderMenuOnClick}
			trigger={<ClayButton>Click Me</ClayButton>}
			triggerIcon="caret-bottom"
		>
			<ClayDropDown.ItemList>
				{[
					{href: '#one', title: 'one'},
					{href: '#two', title: 'two'},
					{disabled: true, href: '#three', title: 'three'},
					{href: '#four', title: 'four'},
				].map(({href, title, ...otherProps}, i) => (
					<ClayDropDown.Item
						href={href}
						key={i}
						onClick={() => {}}
						{...otherProps}
					>
						{title}
					</ClayDropDown.Item>
				))}
			</ClayDropDown.ItemList>
		</ClayDropDown>
	);
}

Default.args = {
	alignmentPosition: 5,
	height: '',
	renderMenuOnClick: false,
	width: '',
};
export function Dynamic() {
	return (
		<ClayDropDown
			items={['one', 'two', 'three', 'four']}
			trigger={<ClayButton>Click Me</ClayButton>}
			triggerIcon="caret-bottom"
		>
			{(item) => (
				<ClayDropDown.Item key={item} onClick={() => {}}>
					{item}
				</ClayDropDown.Item>
			)}
		</ClayDropDown>
	);
}
export function DynamicWithSearch() {
	return (
		<ClayDropDown
			trigger={<ClayButton>Click Me</ClayButton>}
			triggerIcon="caret-bottom"
		>
			<ClayDropDown.Search placeholder="Type to filter" />

			<ClayDropDown.ItemList items={['one', 'two', 'three', 'four']}>
				{(item: string) => (
					<ClayDropDown.Item key={item} onClick={() => {}}>
						{item}
					</ClayDropDown.Item>
				)}
			</ClayDropDown.ItemList>
		</ClayDropDown>
	);
}
export function DynamicGroup() {
	const items = [
		{
			children: [
				{id: 2, name: 'Apple'},
				{id: 3, name: 'Banana'},
				{id: 4, name: 'Mangos'},
			],

			id: 1,
			name: 'Fruit',
		},
		{
			children: [
				{id: 6, name: 'Potatoes'},
				{id: 7, name: 'Tomatoes'},
				{id: 8, name: 'Onions'},
			],

			id: 5,
			name: 'Vegetable',
		},
	];

	return (
		<ClayDropDown
			filterKey="name"
			trigger={<ClayButton>Select</ClayButton>}
			triggerIcon="caret-bottom"
		>
			<ClayDropDown.Search placeholder="Type to filter" />

			<ClayDropDown.ItemList items={items}>
				{(item: any) => (
					<ClayDropDown.Group
						header={item.name}
						items={item.children}
						key={item.name}
					>
						{(item: any) => (
							<ClayDropDown.Item
								key={item.name}
								onClick={() => {}}
							>
								{item.name}
							</ClayDropDown.Item>
						)}
					</ClayDropDown.Group>
				)}
			</ClayDropDown.ItemList>
		</ClayDropDown>
	);
}
export function Groups() {
	return (
		<ClayDropDown
			alignmentPosition={Align.BottomLeft}
			trigger={<ClayButton>Click Me</ClayButton>}
			triggerIcon="caret-bottom"
		>
			<ClayDropDown.ItemList>
				<ClayDropDown.Group header="Group #1">
					{[
						{href: '#one', title: 'one'},
						{href: '#two', title: 'two'},
						{href: '#three', title: 'three'},
					].map((item, i) => (
						<ClayDropDown.Item href={item.href} key={i}>
							{item.title}
						</ClayDropDown.Item>
					))}
				</ClayDropDown.Group>

				<ClayDropDown.Group header="Group #2">
					{[
						{href: '#one', title: 'one'},
						{href: '#two', title: 'two'},
						{href: '#three', title: 'three'},
					].map((item, i) => (
						<ClayDropDown.Item href={item.href} key={i}>
							{item.title}
						</ClayDropDown.Item>
					))}
				</ClayDropDown.Group>
			</ClayDropDown.ItemList>
		</ClayDropDown>
	);
}
export function Checkbox() {
	return (
		<ClayDropDown
			alignmentPosition={Align.BottomLeft}
			trigger={<ClayButton>Click Me</ClayButton>}
			triggerIcon="caret-bottom"
		>
			<ClayDropDown.ItemList>
				<ClayDropDown.Section>
					<ClayCheckbox
						checked
						onChange={() => {}}
						title="I'm a checkbox!"
					/>
				</ClayDropDown.Section>
			</ClayDropDown.ItemList>
		</ClayDropDown>
	);
}
export function Search() {
	const [query, setQuery] = useState('');

	return (
		<ClayDropDown
			alignmentPosition={Align.BottomLeft}
			trigger={<ClayButton>Click Me</ClayButton>}
			triggerIcon="caret-bottom"
		>
			<ClayDropDown.Search
				onChange={(event) => setQuery(event.target.value)}
				value={query}
			/>

			<ClayDropDown.ItemList>
				{[
					{href: '#one', title: 'one'},
					{href: '#two', title: 'two'},
					{disabled: true, href: '#three', title: 'three'},
					{href: '#four', title: 'four'},
				]
					.filter(({title}) => title.match(query))
					.map(({href, title, ...otherProps}, i) => (
						<ClayDropDown.Item href={href} key={i} {...otherProps}>
							{title}
						</ClayDropDown.Item>
					))}
			</ClayDropDown.ItemList>
		</ClayDropDown>
	);
}
export function Radio() {
	return (
		<ClayDropDown
			alignmentPosition={Align.BottomLeft}
			trigger={<ClayButton>Click Me</ClayButton>}
			triggerIcon="caret-bottom"
		>
			<ClayDropDown.ItemList>
				<ClayDropDown.Group header="Order">
					<ClayDropDown.Section>
						<ClayRadio checked title="Ascending" value="asc" />
					</ClayDropDown.Section>

					<ClayDropDown.Section>
						<ClayRadio title="Descending" value="desc" />
					</ClayDropDown.Section>
				</ClayDropDown.Group>
			</ClayDropDown.ItemList>
		</ClayDropDown>
	);
}
export function CaptionAndHelp() {
	return (
		<ClayDropDown
			alignmentPosition={Align.BottomLeft}
			trigger={<ClayButton>Click Me</ClayButton>}
			triggerIcon="caret-bottom"
		>
			<ClayDropDown.Help>Can I help you?</ClayDropDown.Help>

			<ClayDropDown.ItemList>
				{[
					{href: '#one', title: 'one'},
					{href: '#two', title: 'two'},
					{href: '#three', title: 'three'},
				].map((item, i) => (
					<ClayDropDown.Item href={item.href} key={i}>
						{item.title}
					</ClayDropDown.Item>
				))}
			</ClayDropDown.ItemList>

			<ClayDropDown.Caption>... or maybe not.</ClayDropDown.Caption>
		</ClayDropDown>
	);
}
export function ItemsWithIcons() {
	return (
		<ClayDropDown
			alignmentPosition={Align.BottomLeft}
			hasLeftSymbols
			hasRightSymbols
			trigger={<ClayButton>Click Me</ClayButton>}
			triggerIcon="caret-bottom"
		>
			<ClayDropDown.ItemList>
				{[
					{left: 'trash', title: 'Left'},
					{right: 'check', title: 'Right'},
					{left: 'trash', right: 'check', title: 'Both'},
				].map((item, i) => (
					<ClayDropDown.Item
						key={i}
						onClick={() => {}}
						symbolLeft={item.left}
						symbolRight={item.right}
					>
						{item.title}
					</ClayDropDown.Item>
				))}
			</ClayDropDown.ItemList>
		</ClayDropDown>
	);
}
export function CustomOffset() {
	return (
		<ClayDropDown
			alignmentPosition={Align.BottomLeft}
			offsetFn={() => [20, 20]}
			trigger={<ClayButton>Click Me</ClayButton>}
			triggerIcon="caret-bottom"
		>
			<ClayDropDown.ItemList>
				{[
					{href: '#one', title: 'one'},
					{href: '#two', title: 'two'},
					{disabled: true, href: '#three', title: 'three'},
					{href: '#four', title: 'four'},
				].map(({href, title, ...otherProps}, i) => (
					<ClayDropDown.Item href={href} key={i} {...otherProps}>
						{title}
					</ClayDropDown.Item>
				))}
			</ClayDropDown.ItemList>
		</ClayDropDown>
	);
}
export function AlignmentPositions() {
	return (
		<div style={{margin: '200px 300px'}}>
			{Object.keys(Align).map((alignPosition) => (
				<>
					<ClayDropDownWithItems
						alignmentPosition={
							Align[alignPosition as keyof typeof Align]
						}
						items={[
							{href: '#one', title: 'one'},
							{href: '#two', title: 'two'},
							{disabled: true, href: '#three', title: 'three'},
							{href: '#four', title: 'four'},
						]}
						key={alignPosition}
						trigger={<ClayButton>{alignPosition}</ClayButton>}
						triggerIcon="caret-bottom"
					/>

					<br />
				</>
			))}
		</div>
	);
}
export function Drilldown(args: any) {
	return (
		<ClayDropDownWithDrilldown
			defaultActiveMenu="x0a3"
			menus={{
				x0a3: [
					{href: '#', title: 'Hash Link'},
					{type: 'divider'},
					{onClick: () => alert('test'), title: 'Alert!'},
					{type: 'divider'},
					{child: 'x0a4', title: 'Subnav'},
				],

				x0a4: [
					{href: '#', title: '2nd hash link'},
					{type: 'divider'},
					{child: 'x0a5', title: 'Subnav'},
				],

				x0a5: [{title: 'The'}, {type: 'divider'}, {title: 'End'}],
			}}
			messages={{
				back: 'Back',
				goTo: 'Go to',
			}}
			renderMenuOnClick={args.renderMenuOnClick}
			trigger={<ClayButton>Click Me</ClayButton>}
			triggerIcon="caret-bottom"
		/>
	);
}

Drilldown.args = {
	renderMenuOnClick: false,
};
export function DrillDownWithActive() {
	const [active, setActive] = useState(true);

	const onActiveChange = () => {
		setActive(!active);
	};

	return (
		<ClayDropDownWithDrilldown
			active={active}
			defaultActiveMenu="x0a3"
			menus={{
				x0a3: [
					{href: '#', title: 'Hash Link'},
					{onClick: () => alert('test'), title: 'Alert!'},
					{
						onClick: () => {
							onActiveChange();
						},
						title: 'Toggle menu',
					},
					{child: 'x0a4', title: 'Subnav'},
				],

				x0a4: [
					{href: '#', title: '2nd hash link'},
					{child: 'x0a5', title: 'Subnav'},
				],

				x0a5: [{title: 'The'}, {title: 'End'}],
			}}
			messages={{
				back: 'Back',
				goTo: 'Go to',
			}}
			onActiveChange={onActiveChange}
			trigger={<ClayButton>Click Me</ClayButton>}
			triggerIcon="caret-bottom"
		/>
	);
}
export function DropDownWithItems(args: any) {
	const [value, setValue] = useState('');

	return (
		<ClayDropDownWithItems
			caption="Showing 7 of 203 Structures"
			footerContent={
				<>
					<ClayButton displayType="secondary">Cancel</ClayButton>
					<ClayButton>Done</ClayButton>
				</>
			}
			helpText="You can customize this menu or see all you have by pressing 'more'."
			items={ITEMS}
			onSearchValueChange={setValue}
			renderMenuOnClick={args.renderMenuOnClick}
			searchProps={{
				formProps: {
					onSubmit: (event) => {
						event.preventDefault();
						alert('Submitted!');
					},
				},
			}}
			searchValue={value}
			searchable={args.searchable}
			trigger={<ClayButton>Click Me</ClayButton>}
			triggerIcon="caret-bottom"
		/>
	);
}

DropDownWithItems.args = {
	renderMenuOnClick: false,
	searchable: true,
};
export function DropDownWithItemsWithCustomActive() {
	const [value, setValue] = useState('');
	const [active, setActive] = useState(false);

	return (
		<>
			<ClayDropDownWithItems
				active={active}
				caption="Showing 7 of 203 Structures"
				closeOnClickOutside={false}
				footerContent={
					<>
						<ClayButton displayType="secondary">Cancel</ClayButton>
						<ClayButton>Done</ClayButton>
					</>
				}
				helpText="You can customize this menu or see all you have by pressing 'more'."
				items={ITEMS}
				onActiveChange={setActive}
				onSearchValueChange={setValue}
				searchProps={{
					formProps: {
						onSubmit: (event) => {
							event.preventDefault();
							alert('Submitted!');
						},
					},
				}}
				searchValue={value}
				searchable
				trigger={<ClayButton>Click Me</ClayButton>}
				triggerIcon="caret-bottom"
			/>

			<button onClick={() => setActive(!active)} style={{float: 'right'}}>
				External Control
			</button>
		</>
	);
}
export function InModal() {
	const [visible, setVisible] = useState(false);
	const {observer, onClose} = useModal({
		onClose: () => setVisible(false),
	});
	const inputRef = useRef(null);
	const dropdownMenuRef = useRef(null);
	const [panelVisibility, setPanelVisibility] = useState(false);

	return (
		<>
			{visible && (
				<ClayModal observer={observer} size="lg" status="info">
					<ClayModal.Header>Title</ClayModal.Header>

					<ClayModal.Body scrollable>
						<ClayInput
							onClick={() => setPanelVisibility(!panelVisibility)}
							placeholder="meow"
							ref={inputRef}
						/>

						<ClayDropDown.Menu
							active={panelVisibility}
							alignElementRef={inputRef}
							onActiveChange={() =>
								setPanelVisibility(!panelVisibility)
							}
							ref={dropdownMenuRef}
						>
							<ClayDropDown.Item>my panel item</ClayDropDown.Item>
						</ClayDropDown.Menu>

						<img alt="cat" src="https://cataas.com/cat/says/it" />

						<img alt="cat" src="https://cataas.com/cat/says/will" />

						<img alt="cat" src="https://cataas.com/cat/says/have" />

						<img alt="cat" src="https://cataas.com/cat/says/a" />

						<img
							alt="cat"
							src="https://cataas.com/cat/says/scroll"
						/>
					</ClayModal.Body>

					<ClayModal.Footer
						first={
							<ClayButton.Group spaced>
								<ClayButton displayType="secondary">
									Secondary
								</ClayButton>

								<ClayButton displayType="secondary">
									Secondary
								</ClayButton>
							</ClayButton.Group>
						}
						last={
							<ClayButton onClick={onClose}>Primary</ClayButton>
						}
					/>
				</ClayModal>
			)}
			<ClayButton displayType="primary" onClick={() => setVisible(true)}>
				Open modal
			</ClayButton>
		</>
	);
}
export function CascadingMenu() {
	return (
		<ClayDropDownWithItems
			items={[
				{title: 'Folder'},
				{type: 'divider'},
				{
					items: [
						{
							symbolLeft: 'document',
							symbolRight: 'check',
							title: 'Basic Document',
						},
						{title: 'Contract'},
						{title: 'Marketing Banner'},
						{title: 'Spreadsheet'},
						{title: 'Presentation'},
					],

					title: 'Document',
					type: 'contextual',
				},
				{title: 'Shortcut'},
				{title: 'Repository'},
			]}
			trigger={<ClayButton>Cascading Menu</ClayButton>}
			triggerIcon="caret-bottom"
		/>
	);
}
export function KeyboardArrowsIndicator() {
	return (
		<ClayDropDown
			defaultActive
			displayKeyboardArrowsIndicator
			trigger={<ClayButton>Choose a fruit</ClayButton>}
		>
			<ClayDropDown.ItemList>
				<ClayDropDown.Item href="#apple">Apple</ClayDropDown.Item>

				<ClayDropDown.Item href="#banana">Banana</ClayDropDown.Item>

				<ClayDropDown.Item href="#blueberry">
					Blueberry
				</ClayDropDown.Item>

				<ClayDropDown.Item href="#cherry">Cherry</ClayDropDown.Item>

				<ClayDropDown.Item href="#grape">Grape</ClayDropDown.Item>
			</ClayDropDown.ItemList>
		</ClayDropDown>
	);
}

const AI_ITEMS = [
	{label: 'Improve writing', symbolLeft: 'stars'},
	{label: 'Fix spelling & grammar', symbolLeft: 'check'},
	{
		label: 'Translate to',
		symbolLeft: 'text',
		symbolRight: 'angle-right',
	},
	{label: 'Make shorter', symbolLeft: 'list'},
	{label: 'Make longer', symbolLeft: 'list'},
];

type AIFlowState = 'menu' | 'prompt' | 'result' | 'working';

// Shared controlled flow: opens, walks menu -> prompt -> working, then fakes
// the async AI request to reach result.

function useAIFlow() {
	const [active, setActive] = useState(false);
	const [aiState, setAiState] = useState<AIFlowState>('menu');

	return {
		active,
		aiState,
		flowProps: {
			active,
			aiState,
			onAccept: () => {
				setActive(false);
				setAiState('menu');
			},
			onActiveChange: setActive,
			onAiStateChange: setAiState,
			onReset: () => setAiState('menu'),
			onSubmit: () => {

				// Fake the async AI request; ignore the result if the user has
				// meanwhile moved away from the working state (e.g. re-opened
				// the menu with a right-click).

				setTimeout(
					() =>
						setAiState((state) =>
							state === 'working' ? 'result' : state
						),
					1500
				);
			},
		},
		setActive,
		setAiState,
	};
}

function AIDefault() {
	const {flowProps} = useAIFlow();

	return (
		<ClayDropDownWithAI
			{...flowProps}
			items={AI_ITEMS}
			trigger={<ClayButton>AI actions</ClayButton>}
			workingLabel="Improving writing…"
		/>
	);
}

function AIInput() {
	const {flowProps} = useAIFlow();

	return (
		<ClayDropDownWithAI
			{...flowProps}
			items={AI_ITEMS}
			trigger={
				<div>
					<ClayInput
						aria-label="Click to open the AI menu"
						defaultValue="I like milk"
					/>
				</div>
			}
			workingLabel="Improving writing…"
		/>
	);
}

function AIInputSelection() {
	const {active, aiState, flowProps, setActive, setAiState} = useAIFlow();

	return (
		<ClayDropDownWithAI
			{...flowProps}
			items={AI_ITEMS}
			openOnClick={false}
			trigger={
				<div
					onClick={() => {
						if (aiState === 'menu' || aiState === 'prompt') {
							setActive(false);
						}
					}}
					onContextMenu={(event) => {
						event.preventDefault();

						// The working and result states can only be dismissed by
						// their own controls, so ignore the right-click there.

						if (
							active &&
							aiState !== 'menu' &&
							aiState !== 'prompt'
						) {
							return;
						}

						setAiState('menu');
						setActive(true);
					}}
				>
					<ClayInput
						aria-label="Right-click to open the AI menu"
						defaultValue="I like milk"
					/>
				</div>
			}
		/>
	);
}

const AI_FIELD_TEXT = 'I like milk';

const AI_WORKING_KEYFRAMES = `
@keyframes ai-working-pulse {
	0%,
	100% {
		opacity: 1;
	}

	50% {
		opacity: 0.4;
	}
}
`;

function AIRealUsage() {
	const {active, aiState, flowProps, setActive, setAiState} = useAIFlow();
	const fieldRef = useRef<HTMLDivElement>(null);
	const highlightRef = useRef<HTMLSpanElement | null>(null);

	const working = active && aiState === 'working';

	useEffect(() => {
		if (fieldRef.current && !fieldRef.current.textContent) {
			fieldRef.current.textContent = AI_FIELD_TEXT;
		}
	}, []);

	// Remove the highlight once the flow closes, restoring plain text.

	useEffect(() => {
		const span = highlightRef.current;

		if (!active && span?.parentNode) {
			span.replaceWith(document.createTextNode(span.textContent ?? ''));

			fieldRef.current?.normalize();

			highlightRef.current = null;
		}
	}, [active]);

	const highlightSelection = () => {
		const windowSelection = window.getSelection();

		if (windowSelection?.rangeCount && !windowSelection.isCollapsed) {
			const span = document.createElement('span');

			span.style.background =
				'var(--Color-Charts-Purple-purple-l5, #F2E5FF)';

			try {
				windowSelection.getRangeAt(0).surroundContents(span);

				highlightRef.current = span;
			}
			catch (error) {

				// The selection spans multiple nodes; skip the highlight.

				highlightRef.current = null;
			}
		}
	};

	return (
		<ClayDropDownWithAI
			{...flowProps}
			items={AI_ITEMS}
			onAccept={() => setActive(false)}
			onReset={() => setActive(false)}
			openOnClick={false}
			trigger={
				<div className="position-relative">
					<style>{AI_WORKING_KEYFRAMES}</style>

					<div
						aria-label="Right-click to run AI on this field"
						className="form-control"
						contentEditable
						onContextMenu={(event) => {
							event.preventDefault();

							if (!active) {
								highlightSelection();

								setAiState('menu');
								setActive(true);
							}
						}}
						ref={fieldRef}
						role="textbox"
						suppressContentEditableWarning
					/>

					{working ? (
						<div
							className="position-absolute"
							style={{
								background: '#fff',
								borderRadius: '0.25rem',
								inset: 0,
							}}
						>
							<div
								className="position-absolute"
								style={{
									animation:
										'ai-working-pulse 1.5s ease-in-out infinite',
									background:
										'linear-gradient(270deg, rgba(77, 95, 255, 0.1) 0%, rgba(149, 0, 255, 0.1) 100%)',
									borderRadius: '0.25rem',
									inset: 0,
								}}
							/>
						</div>
					) : null}
				</div>
			}
		/>
	);
}

export function AI() {
	return (
		<div className="c-gap-5 d-flex flex-column">
			<div>
				<Heading level={3}>Default (button trigger)</Heading>

				<AIDefault />
			</div>

			<div>
				<Heading level={3}>Input trigger</Heading>

				<AIInput />
			</div>

			<div>
				<Heading level={3}>Input trigger with right-click</Heading>

				<p className="text-secondary">
					The consumer wires the right-click opener on the field and
					turns <code>openOnClick</code>

					off, so a left-click dismisses the flow while in the{' '}

					<code>menu</code> or <code>prompt</code> state.
				</p>

				<AIInputSelection />
			</div>

			<div>
				<Heading level={3}>Real Usage</Heading>

				<p className="text-secondary">
					The consumer owns the <code>trigger</code>, here a{' '}

					<code>contentEditable </code>
					field, and drives the controlled
					<code> aiState </code>

					and <code>active</code>
					props so the field can mirror each state. Right-clicking (
					<code>onContextMenu</code>) opens the menu with{' '}

					<code>openOnClick</code> off, the selected text is
					highlighted while the flow is open, a pulsing block covers
					the field while <code>working</code>, and{' '}

					<code>onAccept</code>/<code>onReset</code> close the flow
					and restore the field.
				</p>

				<AIRealUsage />
			</div>
		</div>
	);
}
