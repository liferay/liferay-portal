/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render} from '@testing-library/react';
import React from 'react';
import {act} from 'react-dom/test-utils';

import Checkin from '../../../../src/main/resources/META-INF/resources/js/document_library/checkin/Checkin.es';

const bridgeComponentId = '_portletNamespace_DocumentLibraryCheckinModal';
const dlVersionNumberIncreaseValues = {
	MAJOR: 'MAJOR',
	MINOR: 'MINOR',
	NONE: 'NONE',
};

function _renderCheckinComponent({checkedOut = true} = {}) {
	return render(
		<Checkin
			checkedOut={checkedOut}
			dlVersionNumberIncreaseValues={dlVersionNumberIncreaseValues}
			portletNamespace="_portletNamespace_"
		/>,
		{
			baseElement: document.body,
		}
	);
}

describe('Checkin', () => {
	beforeEach(() => {
		const components = {};

		Liferay.component = (id, component) => {
			components[id] = component;
		};
		Liferay.componentReady = (id) => Promise.resolve(components[id]);

		Liferay.destroyComponent = jest.fn();
	});

	describe('when the file is checked out', () => {
		describe('and the component is rendered', () => {
			let result;
			beforeEach(() => {
				result = _renderCheckinComponent();
			});

			describe('and we call the open method on the bridge component', () => {
				let callback;
				beforeEach(() => {
					callback = jest.fn();

					return act(() =>
						Liferay.componentReady(bridgeComponentId).then(
							({open}) => {
								open(callback);
							}
						)
					);
				});

				it('renders the form', async () => {
					const form = await result.findByRole('form');

					expect(form).toBeTruthy();
				});

				describe('and the form is submitted', () => {
					beforeEach(async () => {
						const form = await result.findByRole('form');

						act(() => {
							fireEvent.submit(form);
						});
					});

					it('the callback is called with the major version', () => {
						expect(callback).toHaveBeenCalledWith(
							dlVersionNumberIncreaseValues.MAJOR,
							''
						);
					});
				});

				describe('and the save button is cliked with changes in version and changeLog', () => {
					beforeEach(async () => {
						const saveButton = await result.findByText('save');

						const changeLogField =
							await result.findByLabelText('version-notes');

						const minorVersionRadio =
							await result.findByLabelText('minor-version');

						act(() => {
							fireEvent.change(changeLogField, {
								target: {value: 'ChangeLog notes'},
							});
							fireEvent.click(minorVersionRadio);
						});

						act(() => {
							fireEvent.click(saveButton);
						});
					});

					it('the callback is called with the minor version and version notes', () => {
						expect(callback).toHaveBeenCalledWith(
							dlVersionNumberIncreaseValues.MINOR,
							'ChangeLog notes'
						);
					});
				});
			});
		});
	});

	describe('when the file is not checked out', () => {
		describe('and the component is rendered', () => {
			let result;
			beforeEach(() => {
				result = _renderCheckinComponent({checkedOut: false});
			});

			describe('and we call the open method on the bridge component', () => {
				let callback;
				beforeEach(() => {
					callback = jest.fn();

					return act(() =>
						Liferay.componentReady(bridgeComponentId).then(
							({open}) => open(callback)
						)
					);
				});

				it('renders the form', async () => {
					const form = await result.findByRole('form');
					expect(form).toBeTruthy();
				});

				describe('and the form is submitted', () => {
					beforeEach(async () => {
						const form = await result.findByRole('form');

						act(() => {
							fireEvent.submit(form);
						});
					});

					it('the callback is called with the major version', () => {
						expect(callback).toHaveBeenCalledWith(
							dlVersionNumberIncreaseValues.MINOR,
							''
						);
					});
				});
			});
		});
	});
});
