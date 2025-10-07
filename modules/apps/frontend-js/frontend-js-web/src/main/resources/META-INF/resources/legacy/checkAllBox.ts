/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export function checkAllBox(
	form: HTMLElement | null | Element | string,
	name: string | string[],
	allBox: HTMLElement | null | Element | string
): number {
	let totalOn: number = 0;

	if (form) {
		form = Liferay.Util.getDOM(form);

		if (typeof form === 'string') {
			form = document.querySelector(form);
		}

		allBox = Liferay.Util.getDOM(allBox);

		if (typeof allBox === 'string') {
			allBox =
				document.querySelector(allBox) ||
				(form ? form.querySelector(`input[name="${allBox}"]`) : null);
		}

		const inputs: HTMLInputElement[] = form
			? Array.from(form.querySelectorAll('input[type=checkbox]'))
			: [];

		if (!Array.isArray(name)) {
			name = [name];
		}

		let totalBoxes: number = 0;

		inputs.forEach((input) => {
			if (
				allBox instanceof Element &&
				(input.id !== allBox.id ||
					(allBox instanceof HTMLInputElement &&
						input.id !== allBox.name &&
						name.indexOf(input.name) > -1))
			) {
				totalBoxes++;

				if (input.checked) {
					totalOn++;
				}
			}
		});

		if (allBox instanceof HTMLInputElement) {
			allBox.checked = totalBoxes === totalOn;
		}
	}

	return totalOn;
}
