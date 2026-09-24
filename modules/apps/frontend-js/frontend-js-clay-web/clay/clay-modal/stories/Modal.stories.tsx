/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAutocomplete from '@clayui/autocomplete';
import ClayButton from '@clayui/button';
import ClayDropDown, {ClayDropDownWithItems} from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import React, {useContext, useRef, useState} from 'react';

import ClayModal, {ClayModalProvider, Context, useModal} from '../src';
const group1075Image = require('./static/group-1075.svg');

export default {
	argTypes: {
		size: {
			control: {type: 'select'},
			options: ['full-screen', 'lg', 'sm', undefined],
		},
		status: {
			control: {type: 'select'},
			options: ['danger', 'info', 'success', 'warning', undefined],
		},
	},
	component: ClayModal,
	title: 'Design System/Components/Modal',
};
export function Default(args: any) {
	const {observer, onOpenChange, open} = useModal();

	return (
		<>
			{open && (
				<ClayModal
					center={args.center}
					disableAutoClose={args.autoClose}
					observer={observer}
					size={args.size}
					status={args.status}
				>
					<ClayModal.Header>{args.title}</ClayModal.Header>

					<ClayModal.Body
						iFrameProps={{
							'aria-label': 'Hello World',
						}}
						scrollable={args.scrollable}
						url={args.url}
					>
						<p>
							Lorem ipsum dolor sit amet, consectetur adipiscing
							elit. Curabitur dignissim eu ante eget lobortis.
							Praesent a mattis diam, nec auctor nisi. Nam porta
							lobortis massa hendrerit sagittis. Integer sit amet
							risus accumsan nunc pretium convallis eget eu
							tellus. Mauris id massa nec nunc mollis sagittis.
							Class aptent taciti sociosqu ad litora torquent per
							conubia nostra, per inceptos himenaeos. Nulla sit
							amet turpis dui. Aliquam et urna id nisl posuere
							condimentum. Nullam arcu lacus, fermentum sit amet
							quam sed, consequat semper nisi. Vivamus varius
							ligula et viverra placerat. Sed eget quam magna. Ut
							posuere enim porttitor, mollis justo eget, molestie
							mauris. Duis lobortis purus quis risus sodales
							dictum ut eu velit.
						</p>
					</ClayModal.Body>

					<ClayModal.Footer
						last={
							<ClayButton.Group spaced>
								<ClayButton
									displayType="secondary"
									onClick={() => onOpenChange(false)}
								>
									Cancel
								</ClayButton>

								<ClayButton onClick={() => onOpenChange(false)}>
									Save changes
								</ClayButton>
							</ClayButton.Group>
						}
					/>
				</ClayModal>
			)}
			<ClayButton
				displayType="primary"
				onClick={() => onOpenChange(true)}
			>
				Open modal
			</ClayButton>
		</>
	);
}

Default.args = {
	autoClose: false,
	center: false,
	scrollable: false,
	size: 'lg',
	status: undefined,
	title: 'Modal Title',
	url: '',
};
export function CustomHeader() {
	const {observer, onOpenChange, open} = useModal();

	return (
		<>
			{open && (
				<ClayModal observer={observer} size="lg">
					<ClayModal.Header withTitle={false}>
						<ClayModal.ItemGroup>
							<ClayModal.Item>
								<ClayModal.TitleSection>
									<ClayModal.Title>
										<ClayModal.TitleIndicator>
											<ClayIcon symbol="info-circle" />
										</ClayModal.TitleIndicator>
										Modal Title
									</ClayModal.Title>
								</ClayModal.TitleSection>
							</ClayModal.Item>

							<ClayModal.Item shrink>
								<ClayModal.SubtitleSection>
									<ClayModal.Subtitle>
										Modal Subtitle
									</ClayModal.Subtitle>
								</ClayModal.SubtitleSection>
							</ClayModal.Item>
						</ClayModal.ItemGroup>

						<ClayButton
							aria-label="close"
							className="close"
							displayType="unstyled"
							onClick={() => onOpenChange(false)}
						>
							<ClayIcon symbol="times" />
						</ClayButton>
					</ClayModal.Header>

					<ClayModal.Body />
				</ClayModal>
			)}

			<ClayButton
				displayType="primary"
				onClick={() => onOpenChange(true)}
			>
				Open modal
			</ClayButton>
		</>
	);
}

const dropDownItems = [
	{
		label: 'clickable',
		onClick: () => {
			alert('you clicked!');
		},
	},
	{
		type: 'divider' as const,
	},
	{
		items: [
			{
				label: 'one',
				type: 'radio' as const,
				value: 'one',
			},
			{
				label: 'two',
				type: 'radio' as const,
				value: 'two',
			},
		],

		label: 'radio',
		name: 'radio',
		onChange: (value: string) => alert(`New Radio checked ${value}`),
		type: 'radiogroup' as const,
	},
	{
		items: [
			{
				checked: true,
				label: 'checkbox',
				onChange: () => alert('checkbox changed'),
				type: 'checkbox' as const,
			},
			{
				checked: true,
				label: 'checkbox 1',
				onChange: () => alert('checkbox changed'),
				type: 'checkbox' as const,
			},
		],

		label: 'checkbox',
		type: 'group' as const,
	},
	{
		href: '#',
		label: 'linkable',
	},
];

function Autocomplete() {
	const inputRef = useRef<HTMLInputElement | null>(null);
	const [value, setValue] = useState('');
	const [active, setActive] = useState(!!value);
	const filteredItems = ['one', 'two', 'three', 'four', 'five'].filter(
		(item) => item.match(value)
	);

	return (
		<ClayAutocomplete>
			<ClayAutocomplete.Input
				aria-label="Numbers: Enter a number from One to Five"
				autoFocus
				onBlur={() => {
					setActive(false);
				}}
				onChange={(event: any) => {
					setValue(event.target.value);
					setActive(!!event.target.value);
				}}
				ref={inputRef}
				value={value}
			/>

			<ClayAutocomplete.DropDown active={active} onSetActive={setActive}>
				<ClayDropDown.ItemList>
					{filteredItems.map((item) => (
						<ClayAutocomplete.Item
							key={item}
							match={value}
							onMouseDown={() => {
								setActive(false);
								setValue(item);
							}}
							value={item}
						/>
					))}
				</ClayDropDown.ItemList>
			</ClayAutocomplete.DropDown>
		</ClayAutocomplete>
	);
}
export function AutocompleteAndDropDown() {
	const {observer, onOpenChange, open} = useModal();

	return (
		<>
			{open && (
				<ClayModal observer={observer} size="lg">
					<ClayModal.Header>Title</ClayModal.Header>

					<ClayModal.Body
						iFrameProps={{
							'aria-label': 'Hello World',
						}}
					>
						<div className="row">
							<div className="col-md-3">
								<Autocomplete />
							</div>

							<div className="col-md-3">
								<ClayDropDownWithItems
									items={dropDownItems}
									trigger={<ClayButton>Click Me</ClayButton>}
								/>
							</div>
						</div>
					</ClayModal.Body>
				</ClayModal>
			)}
			<ClayButton
				displayType="primary"
				onClick={() => onOpenChange(true)}
			>
				Open modal
			</ClayButton>
		</>
	);
}

function MyApp() {
	const [state, dispatch] = useContext(Context);

	return (
		<ClayButton
			displayType="primary"
			onClick={() =>
				dispatch({
					payload: {
						body: <h2>Hello world!</h2>,
						footer: [
							<></>,
							<></>,
							<ClayButton key={3} onClick={state.onClose}>
								Primary
							</ClayButton>,
						],
						header: 'Title',
						size: 'lg',
					},
					type: 1,
				})
			}
		>
			Open modal
		</ClayButton>
	);
}

function MyAppWithoutFooterAndHeader() {
	const [, dispatch] = useContext(Context);

	return (
		<ClayButton
			displayType="primary"
			onClick={() =>
				dispatch({
					payload: {
						body: <h2>Hello world!</h2>,
						size: 'lg',
					},
					type: 1,
				})
			}
		>
			Open modal without Footer and Header
		</ClayButton>
	);
}
export function Provider() {
	return (
		<ClayModalProvider>
			<MyApp />

			<MyAppWithoutFooterAndHeader />
		</ClayModalProvider>
	);
}
export function CardStyleModal(args: any) {
	const {observer, onOpenChange, open} = useModal();

	return (
		<>
			{open && (
				<ClayModal
					center={args.center}
					disableAutoClose={args.autoClose}
					observer={observer}
					size={args.size}
				>
					<ClayModal.Body
						iFrameProps={{
							'aria-label': 'Hello World',
						}}
						scrollable={args.scrollable}
					>
						<ClayButton
							aria-label="Close the modal"
							className="close"
							displayType={null}
							onClick={() => onOpenChange(false)}
						>
							<ClayIcon symbol="times" />
						</ClayButton>

						<div className="aspect-ratio aspect-ratio-16-to-9 bg-primary-l3 modal-banner-img modal-banner-img-start">
							<div className="aspect-ratio-item aspect-ratio-item-center-middle aspect-ratio-item-fluid">
								<img
									alt="An image of adding modules to a page"
									src={group1075Image}
								/>
							</div>
						</div>

						<ClayModal.Title>{args.title}</ClayModal.Title>

						<p>
							Lorem ipsum dolor sit amet, consectetur adipiscing
							elit. Curabitur dignissim eu ante eget lobortis.
							Praesent a mattis diam, nec auctor nisi. Nam porta
							lobortis massa hendrerit sagittis.
						</p>
					</ClayModal.Body>

					<ClayModal.Footer
						last={
							<ClayButton.Group spaced>
								<ClayButton
									displayType="secondary"
									onClick={() => onOpenChange(false)}
								>
									Cancel
								</ClayButton>

								<ClayButton onClick={() => onOpenChange(false)}>
									<span className="inline-item inline-item-before">
										<ClayIcon symbol="marketplace" />
									</span>
									Explore Marketplace
								</ClayButton>
							</ClayButton.Group>
						}
					/>
				</ClayModal>
			)}
			<ClayButton
				displayType="primary"
				onClick={() => onOpenChange(true)}
			>
				Open modal
			</ClayButton>
		</>
	);
}

CardStyleModal.args = {
	autoClose: false,
	center: false,
	scrollable: false,
	size: 'md',
	title: 'Lorem Ipsum Dolor Sit Amet!',
};
