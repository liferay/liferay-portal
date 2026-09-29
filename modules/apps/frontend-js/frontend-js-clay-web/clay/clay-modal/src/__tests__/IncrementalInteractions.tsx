/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/* eslint-disable no-sparse-arrays */

import ClayModal, {ClayModalProvider, Context, useModal} from '..';
import Button from '@clayui/button';
import {act, cleanup, fireEvent, render} from '@testing-library/react';
import React from 'react';
import ReactDOM from 'react-dom';

const spritemap = 'icons.svg';

function ModalUnmountedWithoutClose() {
	const [visible, setVisible] = React.useState(false);
	const {observer} = useModal();

	return (
		<>
			{visible && <ClayModal observer={observer} spritemap={spritemap} />}

			<Button aria-label="show" onClick={() => setVisible(true)}>
				Show
			</Button>

			<Button aria-label="hide" onClick={() => setVisible(false)}>
				Hide
			</Button>
		</>
	);
}

interface IProps extends React.HTMLAttributes<HTMLDivElement> {
	children?: any;
	initialVisible?: boolean;
}

function ModalWithState({children, initialVisible = false, ...props}: IProps) {
	const [visible, setVisible] = React.useState(initialVisible);
	const {observer} = useModal({onClose: () => setVisible(false)});

	return (
		<>
			{visible && (
				<ClayModal observer={observer} spritemap={spritemap} {...props}>
					{children}
				</ClayModal>
			)}
			<Button aria-label="button" onClick={() => setVisible(true)}>
				Foo
			</Button>
		</>
	);
}

function ModalWithHookState() {
	const {observer, onOpenChange, open} = useModal();

	return (
		<>
			{open && <ClayModal observer={observer} spritemap={spritemap} />}
			<Button aria-label="button" onClick={() => onOpenChange(true)}>
				Foo
			</Button>
		</>
	);
}

interface IStackedModalsProps {
	lowerClassName?: string;
	lowerZIndex?: number;
	upperZIndex?: number;
}

function StackedModals({
	lowerClassName,
	lowerZIndex,
	upperZIndex,
}: IStackedModalsProps) {
	const lower = useModal();
	const upper = useModal();

	return (
		<>
			{lower.open && (
				<ClayModal
					className={lowerClassName}
					observer={lower.observer}
					spritemap={spritemap}
					zIndex={lowerZIndex}
				/>
			)}

			{upper.open && (
				<ClayModal
					observer={upper.observer}
					spritemap={spritemap}
					zIndex={upperZIndex}
				/>
			)}

			<Button
				aria-label="openLower"
				onClick={() => lower.onOpenChange(true)}
			>
				Lower
			</Button>

			<Button
				aria-label="openUpper"
				onClick={() => upper.onOpenChange(true)}
			>
				Upper
			</Button>

			<Button
				aria-label="closeLower"
				onClick={() => lower.onOpenChange(false)}
			>
				Close lower
			</Button>

			<Button
				aria-label="closeUpper"
				onClick={() => upper.onOpenChange(false)}
			>
				Close upper
			</Button>
		</>
	);
}

describe('Modal -> IncrementalInteractions', () => {
	afterEach(() => {
		jest.clearAllTimers();

		cleanup();
	});

	beforeAll(() => {
		jest.useFakeTimers();

		// @ts-ignore

		ReactDOM.createPortal = jest.fn((element) => {
			return element;
		});
	});

	afterAll(() => {
		jest.useRealTimers();
	});

	it('open the modal', () => {
		const {container, getByLabelText} = render(<ModalWithState />);

		expect(document.body.classList).not.toContain('modal-open');

		fireEvent.click(getByLabelText('button'), {});

		expect(document.body.classList).toContain('modal-open');
		expect(
			container.querySelector('.modal-backdrop.fade.show')
		).toBeDefined();
		expect(
			container.querySelector('.fade.modal.d-block.show')
		).toBeDefined();
	});

	it('open the modal with useModal state', () => {
		const {container, getByLabelText} = render(<ModalWithHookState />);

		expect(document.body.classList).not.toContain('modal-open');

		fireEvent.click(getByLabelText('button'), {});

		expect(document.body.classList).toContain('modal-open');
		expect(
			container.querySelector('.modal-backdrop.fade.show')
		).toBeDefined();
		expect(
			container.querySelector('.fade.modal.d-block.show')
		).toBeDefined();
	});

	it('close the modal by clicking on the overlay', () => {
		render(<ModalWithState initialVisible />);

		act(() => {
			jest.runAllTimers();
		});

		const backdropElSelector = '.modal-backdrop.fade.show';
		const modalElSelector = '.fade.modal.d-block.show';

		const backdropEl = document.querySelector(backdropElSelector);
		const modalEl = document.querySelector(modalElSelector);

		expect(backdropEl).toBeDefined();
		expect(modalEl).toBeDefined();

		fireEvent.mouseDown(modalEl!);
		fireEvent.mouseUp(modalEl!);

		expect(document.body.classList).not.toContain('modal-open');
		expect(document.querySelector(backdropElSelector)).toBeNull();
		expect(document.querySelector(modalElSelector)).toBeNull();
	});

	it('do not close modal when event is prevented by clicking on overlay', () => {
		const ModalWithEventPrevented = () => {
			const handleDocumentClick = (event: Event) => {
				event.preventDefault();
			};

			React.useEffect(() => {
				document.addEventListener('click', handleDocumentClick);

				return () => {
					document.removeEventListener('click', handleDocumentClick);
				};
			}, []);

			return <ModalWithState />;
		};

		const {getByLabelText} = render(<ModalWithEventPrevented />);

		fireEvent.click(getByLabelText('button'), {});

		act(() => {
			jest.runAllTimers();
		});

		const backdropElSelector = '.modal-backdrop.fade.show';
		const modalElSelector = '.fade.modal.d-block.show';

		const modalEl = document.querySelector(modalElSelector);

		fireEvent.click(modalEl!, {});

		expect(document.body.classList).toContain('modal-open');
		expect(document.querySelector(backdropElSelector)).toBeDefined();
		expect(document.querySelector(modalElSelector)).toBeDefined();
	});

	it('close the modal when pressing ESC', async () => {
		const {container} = render(<ModalWithState initialVisible />);

		act(() => {
			jest.runAllTimers();
		});

		const backdropElSelector = '.modal-backdrop.fade.show';
		const modalElSelector = '.fade.modal.d-block.show';

		const backdropEl = document.querySelector(backdropElSelector);
		const modalEl = document.querySelector(modalElSelector);

		expect(backdropEl).toBeDefined();
		expect(modalEl).toBeDefined();

		fireEvent.keyDown(container, {key: 'Escape'});

		expect(document.body.classList).not.toContain('modal-open');
		expect(document.querySelector(backdropElSelector)).toBeNull();
		expect(document.querySelector(modalElSelector)).toBeNull();
	});

	it('close the modal when clicking on the close button of the Header component', () => {
		const {getByLabelText} = render(
			<ModalWithState initialVisible>
				<ClayModal.Header>Title</ClayModal.Header>
			</ModalWithState>
		);

		act(() => {
			jest.runAllTimers();
		});

		const backdropElSelector = '.modal-backdrop.fade.show';
		const modalElSelector = '.fade.modal.d-block.show';

		const backdropEl = document.querySelector(backdropElSelector);
		const modalEl = document.querySelector(modalElSelector);
		const buttonHeaderCloseEl = getByLabelText('Close');

		expect(backdropEl).toBeDefined();
		expect(modalEl).toBeDefined();

		fireEvent.click(buttonHeaderCloseEl);

		expect(document.body.classList).not.toContain('modal-open');
		expect(document.querySelector(backdropElSelector)).toBeNull();
		expect(document.querySelector(modalElSelector)).toBeNull();
	});

	it('close the modal when click on the button of Footer component', () => {
		const ModalState = () => {
			const [visible, setVisible] = React.useState(true);
			const {observer, onClose} = useModal({
				onClose: () => setVisible(false),
			});

			if (!visible) {
				return null;
			}

			return (
				<ClayModal observer={observer} spritemap={spritemap}>
					<ClayModal.Footer
						last={
							<Button aria-label="buttonFooter" onClick={onClose}>
								Foo
							</Button>
						}
					/>
				</ClayModal>
			);
		};
		const {getByLabelText} = render(<ModalState />);

		act(() => {
			jest.runAllTimers();
		});

		const backdropElSelector = '.modal-backdrop.fade.show';
		const modalElSelector = '.fade.modal.d-block.show';

		const backdropEl = document.querySelector(backdropElSelector);
		const modalEl = document.querySelector(modalElSelector);
		const buttonFooterCloseEl = getByLabelText('buttonFooter');

		expect(backdropEl).toBeDefined();
		expect(modalEl).toBeDefined();

		fireEvent.click(buttonFooterCloseEl);

		expect(document.body.classList).not.toContain('modal-open');
		expect(document.querySelector(backdropElSelector)).toBeNull();
		expect(document.querySelector(modalElSelector)).toBeNull();
	});
});

describe('ModalProvider -> IncrementalInteractions', () => {
	afterEach(() => {
		jest.clearAllTimers();

		cleanup();
	});

	beforeAll(() => {
		jest.useFakeTimers();

		// @ts-ignore

		ReactDOM.createPortal = jest.fn((element) => {
			return element;
		});
	});

	afterAll(() => {
		jest.useRealTimers();
	});

	it('will not render modal title and footer when not providing it', () => {
		const ModalWithProvider = () => {
			const [, dispatch] = React.useContext(Context);

			return (
				<Button
					data-testid="button"
					displayType="primary"
					onClick={() =>
						dispatch({
							payload: {
								body: <h1>Hello world!</h1>,
								size: 'lg',
							},
							type: 1,
						})
					}
				>
					Open modal
				</Button>
			);
		};

		const {getByTestId} = render(
			<ClayModalProvider spritemap={spritemap}>
				<ModalWithProvider />
			</ClayModalProvider>
		);

		const button = getByTestId('button');

		fireEvent.click(button, {});

		act(() => {
			jest.runAllTimers();
		});

		expect(document.querySelector('modal-header')).toBeNull();
		expect(document.querySelector('modal-footer')).toBeNull();
	});

	it('renders a modal when dispatching Open by provider', () => {
		const ModalWithProvider = () => {
			const [state, dispatch] = React.useContext(Context);

			return (
				<Button
					data-testid="button"
					displayType="primary"
					onClick={() =>
						dispatch({
							payload: {
								body: <h1>Hello world!</h1>,
								footer: [
									<></>,
									<></>,
									<Button key={3} onClick={state.onClose}>
										Primary
									</Button>,
								],
								header: 'Title',
								size: 'lg',
							},
							type: 1,
						})
					}
				>
					Open modal
				</Button>
			);
		};

		const {getByTestId} = render(
			<ClayModalProvider spritemap={spritemap}>
				<ModalWithProvider />
			</ClayModalProvider>
		);

		const button = getByTestId('button');

		fireEvent.click(button, {});

		act(() => {
			jest.runAllTimers();
		});

		expect(document.body).toMatchSnapshot();
	});

	it('renders a modal closed when dispatching Close by provider', () => {
		const ModalWithProvider = () => {
			const [state, dispatch] = React.useContext(Context);

			React.useEffect(() => {
				dispatch({
					payload: {
						body: <h1>Hello world!</h1>,
						footer: [
							<></>,
							<></>,
							<Button key={3} onClick={state.onClose}>
								Primary
							</Button>,
						],
						header: 'Title',
						size: 'lg',
					},
					type: 1,
				});
			}, []);

			return (
				<Button
					data-testid="button"
					displayType="primary"
					onClick={() => dispatch({type: 0})}
				>
					Open modal
				</Button>
			);
		};

		const {getByTestId} = render(
			<ClayModalProvider spritemap={spritemap}>
				<ModalWithProvider />
			</ClayModalProvider>
		);

		const button = getByTestId('button');

		const backdropElSelector = '.modal-backdrop.fade.show';
		const modalElSelector = '.fade.modal.d-block.show';

		const backdropEl = document.querySelector(backdropElSelector);
		const modalEl = document.querySelector(modalElSelector);

		expect(backdropEl).toBeDefined();
		expect(modalEl).toBeDefined();

		fireEvent.click(button, {});

		expect(document.body).toMatchSnapshot();
	});
});

describe('Modal -> IncrementalInteractions -> stacked modals', () => {
	let styleElement: HTMLStyleElement;

	function openStackedModals(getByLabelText: (label: string) => HTMLElement) {
		fireEvent.click(getByLabelText('openLower'));

		act(() => {
			jest.advanceTimersByTime(100);
		});

		fireEvent.click(getByLabelText('openUpper'));

		act(() => {
			jest.advanceTimersByTime(100);
		});
	}

	beforeAll(() => {
		jest.useFakeTimers();

		// @ts-ignore

		ReactDOM.createPortal = jest.fn((element) => {
			return element;
		});
	});

	beforeEach(() => {
		styleElement = document.createElement('style');
		styleElement.textContent =
			'.modal {z-index: 1050} .modal-backdrop {z-index: 1040}';

		document.head.appendChild(styleElement);
	});

	afterEach(() => {
		jest.clearAllTimers();

		cleanup();

		styleElement.remove();
	});

	afterAll(() => {
		jest.useRealTimers();
	});

	it('stacks the top modal one above the z-index of the modal below', () => {
		const {getByLabelText} = render(<StackedModals />);

		openStackedModals(getByLabelText);

		const backdrops =
			document.querySelectorAll<HTMLElement>('.modal-backdrop');
		const modals = document.querySelectorAll<HTMLElement>('.modal');

		expect(modals).toHaveLength(2);
		expect(backdrops[0].style.zIndex).toBe('');
		expect(backdrops[1].style.zIndex).toBe('1051');
		expect(modals[0].style.zIndex).toBe('');
		expect(modals[1].style.zIndex).toBe('1052');
	});

	it('stacks the top modal when the modal below has an unparsable z-index', () => {
		styleElement.textContent += '.modal.lower-modal {z-index: auto}';

		const {getByLabelText} = render(
			<StackedModals lowerClassName="lower-modal" />
		);

		openStackedModals(getByLabelText);

		const backdrops =
			document.querySelectorAll<HTMLElement>('.modal-backdrop');
		const modals = document.querySelectorAll<HTMLElement>('.modal');

		expect(modals).toHaveLength(2);
		expect(backdrops[0].style.zIndex).toBe('');
		expect(backdrops[1].style.zIndex).toBe('1051');
		expect(modals[0].style.zIndex).toBe('');
		expect(modals[1].style.zIndex).toBe('1052');
	});

	it('stacks the top modal above a modal with an explicit zIndex', () => {
		const {getByLabelText} = render(<StackedModals lowerZIndex={2040} />);

		openStackedModals(getByLabelText);

		const backdrops =
			document.querySelectorAll<HTMLElement>('.modal-backdrop');
		const modals = document.querySelectorAll<HTMLElement>('.modal');

		expect(modals).toHaveLength(2);
		expect(backdrops[0].style.zIndex).toBe('2040');
		expect(backdrops[1].style.zIndex).toBe('2051');
		expect(modals[0].style.zIndex).toBe('2050');
		expect(modals[1].style.zIndex).toBe('2052');
	});

	it('stacks the top modal when its explicit zIndex is removed while open', () => {
		const {getByLabelText, rerender} = render(
			<StackedModals upperZIndex={1000} />
		);

		openStackedModals(getByLabelText);

		const backdrops =
			document.querySelectorAll<HTMLElement>('.modal-backdrop');
		const modals = document.querySelectorAll<HTMLElement>('.modal');

		expect(backdrops[1].style.zIndex).toBe('1000');
		expect(modals[1].style.zIndex).toBe('1010');

		rerender(<StackedModals />);

		expect(backdrops[0].style.zIndex).toBe('');
		expect(backdrops[1].style.zIndex).toBe('1051');
		expect(modals[0].style.zIndex).toBe('');
		expect(modals[1].style.zIndex).toBe('1052');
	});

	it('keeps modal-open on the body until the last modal closes', () => {
		const {getByLabelText} = render(<StackedModals />);

		openStackedModals(getByLabelText);

		fireEvent.click(getByLabelText('closeUpper'));

		act(() => {
			jest.advanceTimersByTime(100);
		});

		expect(document.body).toHaveClass('modal-open');
		expect(document.querySelectorAll('.modal')).toHaveLength(1);

		fireEvent.click(getByLabelText('closeLower'));

		act(() => {
			jest.advanceTimersByTime(100);
		});

		expect(document.body).not.toHaveClass('modal-open');
		expect(document.querySelectorAll('.modal')).toHaveLength(0);
	});

	it('releases modal-open when the modal unmounts without closing', () => {
		const {getByLabelText} = render(<ModalUnmountedWithoutClose />);

		fireEvent.click(getByLabelText('show'));

		expect(document.body).toHaveClass('modal-open');

		fireEvent.click(getByLabelText('hide'));

		expect(document.body).not.toHaveClass('modal-open');
		expect(document.querySelector('.modal')).not.toBeInTheDocument();
	});
});
