/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import refreshOnContentChanged from '../../../../src/main/resources/META-INF/resources/js/common/utils/refreshOnContentChanged';

describe('refreshOnContentChanged', () => {
	it('refreshes every registered data set when content is generated', () => {
		const fireSpy = jest
			.spyOn(Liferay, 'fire')
			.mockImplementation(() => {});
		const onSpy = jest
			.spyOn(Liferay, 'on')
			.mockImplementation((() => {}) as never);

		try {
			refreshOnContentChanged('fds-contents');
			refreshOnContentChanged('fds-all-related-assets');
			refreshOnContentChanged('fds-contents');
			refreshOnContentChanged(undefined);

			const registrations = onSpy.mock.calls.filter(
				([eventName]) => eventName === 'cms:aiAssistant:contentChanged'
			);

			expect(registrations).toHaveLength(1);

			const [, handler] = registrations[0] as [string, () => void];

			handler();

			expect(fireSpy).toHaveBeenCalledWith('fds-update-display', {
				id: 'fds-contents',
			});
			expect(fireSpy).toHaveBeenCalledWith('fds-update-display', {
				id: 'fds-all-related-assets',
			});
			expect(
				fireSpy.mock.calls.filter(
					([eventName]) => eventName === 'fds-update-display'
				)
			).toHaveLength(2);
		}
		finally {
			fireSpy.mockRestore();
			onSpy.mockRestore();
		}
	});
});
