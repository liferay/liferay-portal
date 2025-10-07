/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayDatePicker from '@clayui/date-picker';
import ClayTabs from '@clayui/tabs';
import {dateUtils} from 'frontend-js-web';
import React, {useState} from 'react';

export function App() {
	const [active, setActive] = useState(0);

	return (
		<div>
			<ClayTabs
				activation="manual"
				active={active}
				justified={false}
				onActiveChange={setActive}
			>
				<ClayTabs.Item
					innerProps={{
						'aria-controls': 'tabpanel1',
					}}
				>
					Clay Alert
				</ClayTabs.Item>

				<ClayTabs.Item
					innerProps={{
						'aria-controls': 'tabpanel2',
					}}
				>
					Clay Date Picker
				</ClayTabs.Item>
			</ClayTabs>

			<ClayTabs.Content activeIndex={active} fade>
				<ClayTabs.TabPane id="tabpanel1">
					<ClayAlert title="Info">
						This widget is used to test out Clay components. Simply
						add whatever JS you want to App.js and redeploy.
					</ClayAlert>

					<div className="clay-test-class">
						This is where your code goes.
					</div>
				</ClayTabs.TabPane>

				<ClayTabs.TabPane id="tabpanel2">
					<ClayDatePicker
						ariaLabels={{
							buttonChooseDate: `${Liferay.Language.get(
								'select-date'
							)}`,
							buttonDot: `${Liferay.Language.get(
								'select-current-date'
							)}`,
							buttonNextMonth: `${Liferay.Language.get(
								'select-next-month'
							)}`,
							buttonPreviousMonth: `${Liferay.Language.get(
								'select-previous-month'
							)}`,
							dialog: `${Liferay.Language.get('select-date')}`,
							selectMonth: `${Liferay.Language.get('select-a-month')}`,
							selectYear: `${Liferay.Language.get('select-a-year')}`,
						}}
						firstDayOfWeek={dateUtils.getFirstDayOfWeek()}
						months={[
							`${Liferay.Language.get('january')}`,
							`${Liferay.Language.get('february')}`,
							`${Liferay.Language.get('march')}`,
							`${Liferay.Language.get('april')}`,
							`${Liferay.Language.get('may')}`,
							`${Liferay.Language.get('june')}`,
							`${Liferay.Language.get('july')}`,
							`${Liferay.Language.get('august')}`,
							`${Liferay.Language.get('september')}`,
							`${Liferay.Language.get('october')}`,
							`${Liferay.Language.get('november')}`,
							`${Liferay.Language.get('december')}`,
						]}
						placeholder="YYYY-MM-DD HH:mm"
						required
						time
						weekdaysShort={dateUtils.getWeekdaysShort()}
						years={{
							end: 9999,
							start: new Date().getFullYear(),
						}}
					/>
				</ClayTabs.TabPane>
			</ClayTabs.Content>
		</div>
	);
}
