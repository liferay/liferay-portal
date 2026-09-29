/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayPortal, IPortalBaseProps, stack} from '@clayui/shared';
import {suppressOthers} from 'aria-hidden';
import classNames from 'classnames';
import React, {useEffect, useMemo, useRef} from 'react';
import warning from 'warning';

import Body from './Body';
import Context, {IContext} from './Context';
import Footer from './Footer';
import Header, {
	Item,
	ItemGroup,
	Subtitle,
	SubtitleSection,
	Title,
	TitleIndicator,
	TitleSection,
} from './Header';
import {useUserInteractions} from './Hook';
import {Observer, ObserverType} from './types';
import {useModalStack} from './useModalStack';

interface IProps
	extends React.HTMLAttributes<HTMLDivElement>,
		Omit<IContext, 'onClose' | 'ariaLabelledby'> {

	/**
	 * Flag indicating to vertically center the modal.
	 */
	center?: boolean;

	/**
	 * Container element to render modal into.
	 */
	containerElementRef?: React.RefObject<Element>;

	/**
	 * Props to add to the ClayPortal.
	 */
	containerProps?: IPortalBaseProps;

	/**
	 * A flag indicating if the modal shouldn't
	 * be closed when either the ESC key is pressed
	 * or when clicking outside the modal
	 */
	disableAutoClose?: boolean;

	/**
	 * Observer is Modal's communication system with `useModal`
	 * hook, adds observer from `useModal` hook here.
	 */
	observer: Observer;

	/**
	 * The size of element modal.
	 */
	size?: 'full-screen' | 'lg' | 'sm';

	/**
	 * Allows setting a custom z-index value, overriding the default one which is 1040, modal body z-index will be +10 of this value. Without it, a modal opened over another modal is stacked above it automatically; setting it turns that off.
	 */
	zIndex?: number;
}

const warningMessage = `You need to pass the 'observer' prop to ClayModal for everything to work fine, use the 'useModal' hook that exposes the observer.

> const {observer} = useModal({...});
>
> return (
> 	<ClayModal observer={observer}>
> 		...
> 	</ClayModal>
> );
`;

let counter = 0;

function Modal({
	center,
	children,
	className,
	containerElementRef,
	containerProps = {},
	disableAutoClose = false,
	observer,
	role = 'dialog',
	size,
	spritemap,
	status,
	zIndex,
	...otherProps
}: IProps) {
	const modalElementRef = useRef<HTMLDivElement | null>(null);
	const modalBodyElementRef = useRef<HTMLDivElement | null>(null);
	const [show, content] =
		observer && observer.mutation ? observer.mutation : [false, false];
	warning(observer !== undefined, warningMessage);
	useUserInteractions(
		modalElementRef,
		modalBodyElementRef,
		() => !disableAutoClose && observer.dispatch(ObserverType.Close),
		show,
		content
	);
	useEffect(() => {
		observer.dispatch(ObserverType.RestoreFocus, document.activeElement);
		observer.dispatch(ObserverType.Open);

		return () => observer.dispatch(ObserverType.Unmount);
	}, []);
	useEffect(() => {
		if (modalBodyElementRef.current && show && content) {
			const focusedElement =
				modalBodyElementRef.current.querySelector('h1');
			if (focusedElement) {
				focusedElement.focus();
			}
			else {
				modalBodyElementRef.current.focus();
			}
		}
	}, [show, content]);
	const ariaLabelledby = useMemo(() => {
		counter++;

		return `clay-modal-label-${counter}`;
	}, []);

	const {zIndex: stackZIndex} = useModalStack(modalElementRef, {
		active: show && content,
		disabled: zIndex !== undefined,
	});

	useEffect(() => {
		if (show && content) {
			stack.push(modalElementRef);
		}

		return () => {
			const index = stack.indexOf(modalElementRef);
			if (index >= 0) {
				stack.splice(index, 1);
			}
		};
	}, [show, modalElementRef, content]);
	useEffect(() => {
		if (
			modalElementRef.current &&
			show &&
			stack[stack.length - 1] === modalElementRef
		) {

			// Hide everything from ARIA except the Modal Body

			return suppressOthers(modalElementRef.current);
		}
	}, [show]);

	const backdropZIndex = zIndex ?? stackZIndex;
	const modalZIndex =
		zIndex === undefined
			? stackZIndex && stackZIndex + 1
			: zIndex && zIndex + 10;

	return (
		<ClayPortal
			{...containerProps}
			containerRef={containerElementRef}
			subPortalRef={modalElementRef}
		>
			<div
				aria-hidden="true"
				className={classNames('modal-backdrop fade', {
					show,
				})}
				style={{zIndex: backdropZIndex}}
			/>

			<div
				{...otherProps}
				className={classNames('fade modal d-block', className, {
					show,
				})}
				ref={modalElementRef}
				style={{zIndex: modalZIndex}}
			>
				<div
					className={classNames('modal-dialog', {
						[`modal-${size}`]: size,
						[`modal-${status}`]: status,
						'modal-dialog-centered': center,
					})}
				>
					<div
						aria-labelledby={ariaLabelledby}
						aria-modal="true"
						className="modal-content"
						ref={modalBodyElementRef}
						role={role}
						tabIndex={-1}
					>
						<Context.Provider
							value={{
								ariaLabelledby,
								onClose: () =>
									observer.dispatch(ObserverType.Close),
								spritemap,
								status,
							}}
						>
							{content && children}
						</Context.Provider>
					</div>
				</div>
			</div>
		</ClayPortal>
	);
}

Modal.Body = Body;
Modal.Footer = Footer;
Modal.Header = Header;
Modal.Item = Item;
Modal.ItemGroup = ItemGroup;
Modal.Subtitle = Subtitle;
Modal.SubtitleSection = SubtitleSection;
Modal.Title = Title;
Modal.TitleIndicator = TitleIndicator;
Modal.TitleSection = TitleSection;

export default Modal;
