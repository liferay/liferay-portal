/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import DropDown from '@clayui/drop-down';
import Form from '@clayui/form';
import Icon from '@clayui/icon';
import Label from '@clayui/label';
import Layout from '@clayui/layout';
import {useId} from '@clayui/shared';
import React from 'react';

import {Option, Picker} from '../src/picker';
import {Text} from '../src/typography';

export default {
	component: Picker,
	title: 'Design System/Components/Picker',
};
export function Default() {
	const pickerId = useId();
	const labelId = useId();

	return (
		<div style={{width: '150px'}}>
			<Form.Group>
				<label htmlFor={pickerId} id={labelId}>
					Choose a fruit
				</label>

				<Picker aria-labelledby={labelId} id={pickerId}>
					<Option key="apple">Apple</Option>

					<Option disabled key="banana">
						Banana
					</Option>

					<Option key="mangos">Mangos</Option>

					<Option key="blueberry">Blueberry</Option>

					<Option key="boysenberry">Boysenberry</Option>

					<Option key="cherry">Cherry</Option>

					<Option key="cranberry">Cranberry</Option>

					<Option key="eggplant">Eggplant</Option>

					<Option key="fig">Fig</Option>

					<Option key="grape">Grape</Option>

					<Option key="guava">Guava</Option>

					<Option key="huckleberry">Huckleberry</Option>
				</Picker>
			</Form.Group>
		</div>
	);
}
export function Search() {
	const pickerId1 = useId();
	const labelId1 = useId();
	const pickerId2 = useId();
	const labelId2 = useId();

	return (
		<div style={{display: 'flex', gap: '20px'}}>
			<div style={{width: '200px'}}>
				<Form.Group>
					<label htmlFor={pickerId1} id={labelId1}>
						Searchable (Explicit)
					</label>

					<Picker
						aria-labelledby={labelId1}
						id={pickerId1}
						searchable
					>
						<Option key="apple">Apple</Option>

						<Option key="banana">Banana</Option>

						<Option key="mangos">Mangos</Option>

						<Option key="blueberry">Blueberry</Option>
					</Picker>
				</Form.Group>
			</div>

			<div style={{width: '200px'}}>
				<Form.Group>
					<label htmlFor={pickerId2} id={labelId2}>
						Automatic (Threshold 5)
					</label>

					<Picker
						aria-labelledby={labelId2}
						id={pickerId2}
						items={[
							'Apple',
							'Banana',
							'Blueberry',
							'Cherry',
							'Grape',
							'Lemon',
							'Lime',
							'Mango',
						]}
						searchableThreshold={5}
					>
						{(item) => <Option key={item}>{item}</Option>}
					</Picker>
				</Form.Group>
			</div>
		</div>
	);
}

const Trigger = React.forwardRef<
	HTMLDivElement,
	React.HTMLAttributes<HTMLDivElement>
>(({children, ...otherProps}, ref) => (
	<div ref={ref} {...otherProps} tabIndex={0}>
		<Icon className="mr-2" symbol="user" />

		{children}
	</div>
));

Trigger.displayName = 'Trigger';
export function CustomTrigger() {
	const pickerId = useId();
	const labelId = useId();

	return (
		<div style={{width: '180px'}}>
			<Form.Group>
				<label htmlFor={pickerId} id={labelId}>
					Choose a user
				</label>

				<Picker
					aria-labelledby={labelId}
					as={Trigger}
					id={pickerId}
					items={['Liam', 'Noah', 'Oliver']}
				>
					{(item) => <Option key={item}>{item}</Option>}
				</Picker>
			</Form.Group>
		</div>
	);
}
export function CustomOptions() {
	const pickerId = useId();
	const labelId = useId();

	return (
		<div style={{width: '180px'}}>
			<Form.Group>
				<label htmlFor={pickerId} id={labelId}>
					Choose a user
				</label>

				<Picker
					aria-labelledby={labelId}
					id={pickerId}
					items={['Liam', 'Noah', 'Oliver']}
				>
					{(item) => (
						<Option
							aria-describedby={`${item}-description ${item}-status`}
							aria-labelledby={`${item}-title`}
							key={item}
							textValue={item}
						>
							<Layout.ContentRow>
								<Layout.ContentCol expand>
									<Text
										id={`${item}-title`}
										size={3}
										weight="semi-bold"
									>
										{item}
									</Text>

									<Text
										aria-hidden
										color="secondary"
										id={`${item}-description`}
										size={2}
									>
										Description
									</Text>
								</Layout.ContentCol>

								<Layout.ContentCol>
									<Label
										aria-hidden
										displayType="success"
										id={`${item}-status`}
									>
										Active
									</Label>
								</Layout.ContentCol>
							</Layout.ContentRow>
						</Option>
					)}
				</Picker>
			</Form.Group>
		</div>
	);
}
export function CustomGroup() {
	const pickerId = useId();
	const labelId = useId();

	return (
		<div style={{width: '180px'}}>
			<Form.Group>
				<label htmlFor={pickerId} id={labelId}>
					Select an option
				</label>

				<Picker
					aria-labelledby={labelId}
					id={pickerId}
					items={[
						{
							items: [
								{label: 'Apple', value: '1'},
								{label: 'Banana', value: '2'},
								{label: 'Mangos', value: '3'},
							],

							label: 'Fruit',
						},
						{
							items: [
								{label: 'Onions', value: '4'},
								{label: 'abc', value: '5'},
								{label: 'def', value: '6'},
							],

							label: 'Vegetable',
						},
					]}
				>
					{(group) => (
						<DropDown.Group
							header={group.label}
							items={group.items}
						>
							{(item) => (
								<Option key={item.value}>{item.label}</Option>
							)}
						</DropDown.Group>
					)}
				</Picker>
			</Form.Group>
		</div>
	);
}
export function Shrink() {
	const pickerId = useId();
	const labelId = useId();

	return (
		<Form.Group>
			<label htmlFor={pickerId} id={labelId}>
				Select an option
			</label>

			<Picker
				aria-labelledby={labelId}
				id={pickerId}
				items={[
					{
						items: [
							{label: 'Apple', value: '1'},
							{label: 'Banana', value: '2'},
							{label: 'Mangos', value: '3'},
						],

						label: 'Fruit',
					},
					{
						items: [
							{label: 'Onions', value: '4'},
							{label: 'abc', value: '5'},
							{label: 'def', value: '6'},
						],

						label: 'Vegetable',
					},
				]}
				shrink
			>
				{(group) => (
					<DropDown.Group header={group.label} items={group.items}>
						{(item) => (
							<Option key={item.value}>{item.label}</Option>
						)}
					</DropDown.Group>
				)}
			</Picker>
		</Form.Group>
	);
}
export function Width() {
	const pickerId = useId();
	const labelId = useId();

	return (
		<div style={{display: 'flex'}}>
			<div style={{marginRight: '50px', width: '250px'}}>
				<Form.Group>
					<label htmlFor={pickerId} id={labelId}>
						Choose a fruit
					</label>

					<Picker
						aria-labelledby={labelId}
						defaultActive
						id={pickerId}
					>
						<Option key="apple">Apple</Option>

						<Option disabled key="banana">
							Banana
						</Option>

						<Option key="mangos">Mangos</Option>

						<Option key="blueberry">Blueberry</Option>

						<Option key="boysenberry">Boysenberry</Option>

						<Option key="cherry">Cherry</Option>

						<Option key="cranberry">Cranberry</Option>

						<Option key="eggplant">Eggplant</Option>

						<Option key="fig">Fig</Option>

						<Option key="grape">Grape</Option>

						<Option key="guava">Guava</Option>

						<Option key="huckleberry">Huckleberry</Option>
					</Picker>
				</Form.Group>
			</div>

			<div style={{marginRight: '80px', width: '100px'}}>
				<Form.Group>
					<label htmlFor={pickerId} id={labelId}>
						Choose a fruit
					</label>

					<Picker
						aria-labelledby={labelId}
						defaultActive
						id={pickerId}
						placeholder="Fruit"
					>
						<Option key="apple">Apple</Option>

						<Option disabled key="banana">
							Banana
						</Option>

						<Option key="mangos">Mangos</Option>

						<Option key="blueberry">Blueberry</Option>

						<Option key="boysenberry">Boysenberry</Option>

						<Option key="cherry">Cherry</Option>

						<Option key="cranberry">Cranberry</Option>

						<Option key="eggplant">Eggplant</Option>

						<Option key="fig">Fig</Option>

						<Option key="grape">Grape</Option>

						<Option key="guava">Guava</Option>

						<Option key="huckleberry">Huckleberry</Option>
					</Picker>
				</Form.Group>
			</div>

			<div style={{marginRight: '50px', width: '100px'}}>
				<Form.Group>
					<label htmlFor={pickerId} id={labelId}>
						Year
					</label>

					<Picker
						aria-labelledby={labelId}
						defaultActive
						id={pickerId}
						placeholder="Year"
						width={85}
					>
						<Option key="2020">2020</Option>

						<Option key="2021">2021</Option>

						<Option key="2022">2022</Option>

						<Option key="2023">2023</Option>

						<Option key="2024">2024</Option>
					</Picker>
				</Form.Group>
			</div>

			<div style={{marginRight: '50px', width: '100px'}}>
				<Form.Group>
					<label htmlFor={pickerId} id={labelId}>
						Long Option Year
					</label>

					<Picker
						aria-labelledby={labelId}
						defaultActive
						id={pickerId}
						placeholder="Year"
					>
						<Option key="2020">
							2020 was really a looooooooong year
						</Option>

						<Option key="2021">2021</Option>

						<Option key="2022">2022</Option>

						<Option key="2023">2023</Option>

						<Option key="2024">2024</Option>
					</Picker>
				</Form.Group>
			</div>
		</div>
	);
}
export function KeyboardArrowsIndicator() {
	const pickerId = useId();
	const labelId = useId();

	return (
		<div style={{width: '200px'}}>
			<Form.Group>
				<label htmlFor={pickerId} id={labelId}>
					Choose a fruit
				</label>

				<Picker
					aria-labelledby={labelId}
					defaultActive
					displayKeyboardArrowsIndicator
					id={pickerId}
				>
					<Option key="apple">Apple</Option>

					<Option key="banana">Banana</Option>

					<Option key="mangos">Mangos</Option>

					<Option key="blueberry">Blueberry</Option>

					<Option key="cherry">Cherry</Option>

					<Option key="grape">Grape</Option>
				</Picker>
			</Form.Group>
		</div>
	);
}
