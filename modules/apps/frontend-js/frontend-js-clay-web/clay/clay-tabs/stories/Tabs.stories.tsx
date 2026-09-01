/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useState} from 'react';

import ClayTabs from '../src';

export default {
	argTypes: {
		activation: {
			control: {type: 'select'},
			options: ['manual', 'automatic'],
		},
		displayType: {
			control: {type: 'select'},
			options: ['basic', 'underline', null],
		},
	},
	component: ClayTabs,
	title: 'Design System/Components/Tabs',
};
export function Default(args: any) {
	const [active, setActive] = useState<number>(0);

	return (
		<>
			<ClayTabs
				activation={args.activation}
				active={active}
				displayType={args.displayType}
				justified={args.justified}
				modern={args.modern}
				onActiveChange={setActive}
			>
				<ClayTabs.Item
					disabled={args.disabledFirstTab}
					innerProps={{
						'aria-controls': 'tabpanel1',
					}}
				>
					Tab 1
				</ClayTabs.Item>

				<ClayTabs.Item
					disabled={args.disabledSecondTab}
					innerProps={{
						'aria-controls': 'tabpanel2',
					}}
				>
					Tab 2
				</ClayTabs.Item>

				<ClayTabs.Item
					disabled={args.disabledThirdTab}
					innerProps={{
						'aria-controls': 'tabpanel3',
					}}
				>
					Tab 3
				</ClayTabs.Item>

				<ClayTabs.Item
					disabled={args.disabledFourthTab}
					innerProps={{
						'aria-controls': 'tabpanel4',
					}}
				>
					Tab 4
				</ClayTabs.Item>
			</ClayTabs>
			<ClayTabs.Content activeIndex={active} fade={args.fade}>
				<ClayTabs.TabPane id="tabpanel1">
					{`1. Single origin, extra id beans, eu to go, skinny
				americano ut aftertas te sugar. At americano, viennese
				variety iced grounds, grinder froth and pumpkin spice
				aromatic. Cultivar aged lungo, grounds café au lait,
				skinny, blue mountain, in variety sugar shop roast.
				Wings, blue mountain affogato organic cappuccino java
				plunger pot. Single shot variety pumpkin spice seasonal
				skinny barista carajillo robust cream.`}
				</ClayTabs.TabPane>

				<ClayTabs.TabPane id="tabpanel2">
					{`2. Single origin, extra id beans, eu to go, skinny
				americano ut aftertaste sugar. At americano, viennese
				variety iced grounds, grinder froth and pumpkin spice
				aromatic. Cultivar aged lungo, grounds café au lait,
				skinny, blue mountain, in variety sugar shop roast.
				Wings, blue mountain affogato organic cappuccino java
				plunger pot. Single shot variety pumpkin spice seasonal
				skinny barista carajillo robust cream.`}
				</ClayTabs.TabPane>

				<ClayTabs.TabPane id="tabpanel3">
					{`3. Single origin, extra id beans, eu to go, skinny
				americano ut aftertaste sugar. At americano, viennese
				variety iced grounds, grinder froth and pumpkin spice
				aromatic. Cultivar aged lungo, grounds café au lait,
				skinny, blue mountain, in variety sugar shop roast.
				Wings, blue mountain affogato organic cappuccino java
				plunger pot. Single shot variety pumpkin spice seasonal
				skinny barista carajillo robust cream.`}
				</ClayTabs.TabPane>

				<ClayTabs.TabPane id="tabpanel4">
					{`4. Single origin, extra id beans, eu to go, skinny
				americano ut aftertaste sugar. At americano, viennese
				variety iced grounds, grinder froth and pumpkin spice
				aromatic. Cultivar aged lungo, grounds café au lait,
				skinny, blue mountain, in variety sugar shop roast.
				Wings, blue mountain affogato organic cappuccino java
				plunger pot. Single shot variety pumpkin spice seasonal
				skinny barista carajillo robust cream.`}
				</ClayTabs.TabPane>

				<ClayTabs.TabPane id="tabpanel5">
					{`4. Single origin, extra id beans, eu to go, skinny
				americano ut aftertaste sugar. At americano, viennese
				variety iced grounds, grinder froth and pumpkin spice
				aromatic. Cultivar aged lungo, grounds café au lait,
				skinny, blue mountain, in variety sugar shop roast.
				Wings, blue mountain affogato organic cappuccino java
				plunger pot. Single shot variety pumpkin spice seasonal
				skinny barista carajillo robust cream.`}
				</ClayTabs.TabPane>
			</ClayTabs.Content>
		</>
	);
}

Default.args = {
	activation: 'manual',
	disabledFirstTab: false,
	disabledFourthTab: false,
	disabledSecondTab: true,
	disabledThirdTab: false,
	displayType: 'underline',
	justified: false,
	modern: true,
};
export function NewDefault(args: any) {
	return (
		<ClayTabs activation={args.activation} displayType={args.displayType}>
			<ClayTabs.List>
				<ClayTabs.Item>Tab 1</ClayTabs.Item>

				<ClayTabs.Item>Tab 2</ClayTabs.Item>

				<ClayTabs.Item>Tab 3</ClayTabs.Item>
			</ClayTabs.List>

			<ClayTabs.Panels>
				<ClayTabs.TabPanel>Tab Content 1</ClayTabs.TabPanel>

				<ClayTabs.TabPanel>Tab Content 2</ClayTabs.TabPanel>

				<ClayTabs.TabPanel>Tab Content 3</ClayTabs.TabPanel>
			</ClayTabs.Panels>
		</ClayTabs>
	);
}

NewDefault.args = {
	activation: 'manual',
};
export function WithState() {
	const [active, setActive] = useState(0);

	return (
		<div style={{padding: 50}}>
			<ClayTabs active={active} modern={false} onActiveChange={setActive}>
				{['Tab 1', 'Tab 2', 'Tab 3'].map((item) => (
					<ClayTabs.Item key={item}>{item}</ClayTabs.Item>
				))}
			</ClayTabs>

			<ClayTabs.Content activeIndex={active} fade>
				<ClayTabs.TabPane>
					Single origin, extra id beans, eu to go, skinny americano ut
					aftertaste sugar. At americano, viennese variety iced
					grounds, grinder froth and pumpkin spice aromatic. Cultivar
					aged lungo, grounds café au lait, skinny, blue mountain, in
					variety sugar shop roast. Wings, blue mountain affogato
					organic cappuccino java plunger pot. Single shot variety
					pumpkin spice seasonal skinny barista carajillo robust
					cream.
				</ClayTabs.TabPane>

				<ClayTabs.TabPane>
					Iced, crema, coffee id kopi-luwak coffee variety. Skinny
					extraction, id trifecta qui trifecta grinder. Barista
					robusta arabica breve ut skinny milk beans macchiato
					carajillo espresso. Cultivar single shot brewed, coffee
					steamed to go wings to go cortado. Grinder, siphon coffee
					acerbic espresso cinnamon crema breve.
				</ClayTabs.TabPane>

				<ClayTabs.TabPane>
					Skinny extraction, id trifecta qui trifecta grinder. Barista
					robusta arabica breve ut skinny milk beans macchiato
					carajillo espresso. Cultivar single shot brewed, coffee
					steamed to go wings to go cortado. Grinder, siphon coffee
					acerbic espresso cinnamon crema breve.
				</ClayTabs.TabPane>
			</ClayTabs.Content>
		</div>
	);
}

export function ItemsWithIcon() {
	const ITEMS = [
		{label: 'Details', symbol: 'info-circle'},
		{label: 'Categorization', symbol: 'categories'},
		{label: 'Performance', symbol: 'analytics'},
		{label: 'Versions', symbol: 'time'},
		{label: 'Comments', symbol: 'comments'},
	];

	return (
		<ClayTabs>
			<ClayTabs.List className="c-gap-1">
				{ITEMS.map(({label, symbol}) => (
					<ClayTabs.ItemWithIcon
						key={label}
						label={label}
						symbol={symbol}
					/>
				))}
			</ClayTabs.List>

			<ClayTabs.Panels>
				{ITEMS.map(({label}) => (
					<ClayTabs.TabPanel key={label}>
						{label} Content
					</ClayTabs.TabPanel>
				))}
			</ClayTabs.Panels>
		</ClayTabs>
	);
}
