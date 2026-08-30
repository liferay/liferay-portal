/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render} from '@testing-library/react';
import * as React from 'react';
import {act} from 'react-dom/test-utils';

import Modal from '../../../src/main/resources/META-INF/resources/modal/components/Modal';

describe('Modal', () => {
	beforeAll(() => {
		Liferay.on = jest.fn(() => {
			return {
				detach: jest.fn(),
			};
		});
	});

	beforeEach(() => {
		jest.useFakeTimers();
	});

	it('renders markup based on given configuration', () => {
		const {baseElement} = render(
			<Modal
				id="abcd"
				iframeProps={{id: 'efgh'}}
				size="lg"
				title="My Modal"
				url="https://www.sample.url?p_p_id=com_liferay_MyPortlet"
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(baseElement).toMatchSnapshot();
	});

	it('renders in full screen, if url is set', () => {
		const {baseElement} = render(<Modal url="https://www.sample.url" />);

		expect(baseElement.querySelector('.modal-full-screen')).toBeTruthy();
	});

	it('renders in given size, even if url is set', () => {
		const {baseElement} = render(
			<Modal size="lg" url="https://www.sample.url" />
		);

		expect(baseElement.querySelector('.modal-lg')).toBeTruthy();
	});

	it('closes modal on cancel type button click', () => {
		const onCloseCallback = jest.fn();

		render(
			<Modal
				buttons={[{id: 'myButton', type: 'cancel'}]}
				onClose={onCloseCallback}
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		fireEvent.click(
			document.getElementById('myButton') as HTMLButtonElement
		);

		expect(onCloseCallback).toBeCalled();
	});

	// We are skipping this test because Jest does not support
	// document.createRange, but will support it in a future version. See more:
	//
	//      https://github.com/liferay/liferay-npm-tools/issues/440

	it.skip('renders given body HTML', () => {
		const sampleId = 'sampleId';

		render(<Modal bodyHTML={`<div id='${sampleId}' />`} />);

		act(() => {
			jest.runAllTimers();
		});

		expect(document.getElementById(sampleId)).toBeTruthy();
	});

	it('renders given body component', () => {
		const sampleId = 'sampleId';

		const SampleBodyComponent = () => {
			return <div id={sampleId} />;
		};

		render(<Modal bodyComponent={SampleBodyComponent} />);

		act(() => {
			jest.runAllTimers();
		});

		expect(document.getElementById(sampleId)).toBeTruthy();
	});

	it('calls onOpen once, even when the modal rerenders', () => {
		const onOpen = jest.fn();

		const SampleBodyComponent = () => {
			return <div />;
		};

		const {rerender} = render(
			<Modal bodyComponent={SampleBodyComponent} onOpen={onOpen} />
		);

		act(() => {
			jest.runAllTimers();
		});

		rerender(<Modal bodyComponent={SampleBodyComponent} onOpen={onOpen} />);

		act(() => {
			jest.runAllTimers();
		});

		expect(onOpen).toHaveBeenCalledTimes(1);
	});

	it('keeps the body state when the modal rerenders', () => {
		const sampleId = 'sampleId';

		const SampleBodyComponent = () => {
			return <input id={sampleId} />;
		};

		const {baseElement, rerender} = render(
			<Modal bodyComponent={SampleBodyComponent} />
		);

		act(() => {
			jest.runAllTimers();
		});

		fireEvent.change(
			baseElement.querySelector(`#${sampleId}`) as HTMLInputElement,
			{target: {value: 'typed'}}
		);

		rerender(<Modal bodyComponent={SampleBodyComponent} />);

		act(() => {
			jest.runAllTimers();
		});

		expect(
			(baseElement.querySelector(`#${sampleId}`) as HTMLInputElement)
				.value
		).toBe('typed');
	});

	it('renders given header HTML', () => {
		const sampleId = 'sampleId';

		render(<Modal headerHTML={`<div id='${sampleId}' />`} />);

		act(() => {
			jest.runAllTimers();
		});

		expect(document.getElementById(sampleId)).toBeTruthy();
	});

	// Disabling this test because Modal's behavior now is that the focus goes
	// to the modal to announce that it is open.

	xit('when providing "autoFocus: true" inside a button configuration, it will make this button focused', () => {
		render(
			<Modal
				buttons={[
					{autoFocus: true, id: 'modal-button-ok', label: 'ok'},
				]}
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(document.getElementById('modal-button-ok')).toHaveFocus();
	});
});
